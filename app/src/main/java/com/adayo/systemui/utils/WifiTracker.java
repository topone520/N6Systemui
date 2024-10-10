/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.adayo.systemui.utils;

import android.annotation.AnyThread;
import android.annotation.MainThread;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkKey;
import android.net.NetworkRequest;
import android.net.NetworkScoreManager;
import android.net.ScoredNetwork;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.net.wifi.WifiNetworkScoreCache.CacheListener;
import android.net.wifi.hotspot2.OsuProvider;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.GuardedBy;
import androidx.lifecycle.LifecycleObserver;

import com.adayo.proxy.media.mediascanner.utils.ToastUtils;
import com.adayo.systemui.utils.events.OnDestroy;
import com.adayo.systemui.utils.events.OnStart;
import com.adayo.systemui.utils.events.OnStop;
import com.android.systemui.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class WifiTracker implements LifecycleObserver, OnStart, OnStop, OnDestroy {

    private static final long DEFAULT_MAX_CACHED_SCORE_AGE_MILLIS = 20 * DateUtils.MINUTE_IN_MILLIS;
    static final long MAX_SCAN_RESULT_AGE_MILLIS = 15000;
    private static final String TAG = "WifiTracker";
    private static final int WIFI_RESCAN_INTERVAL_MS = 4 * 1000;
    private final Context mContext;
    private final WifiManager mWifiManager;
    private final IntentFilter mFilter;
    private final ConnectivityManager mConnectivityManager;
    private final NetworkRequest mNetworkRequest;
    private final AtomicBoolean mConnected = new AtomicBoolean(false);
    private final WifiListenerExecutor mListener;
    Handler mWorkHandler;
    private HandlerThread mWorkThread;
    private WifiTrackerNetworkCallback mNetworkCallback;
    private final Object mLock = new Object();

    @GuardedBy("mLock")
    private final List<AccessPoint> mInternalAccessPoints = new ArrayList<>();

    @GuardedBy("mLock")
    private final Set<NetworkKey> mRequestedScores = new ArraySet<>();

    private boolean mStaleScanResults = true;

    private boolean mLastScanSucceeded = true;
    private final HashMap<String, ScanResult> mScanResultCache = new HashMap<>();
    private boolean mRegistered;

    private NetworkInfo mLastNetworkInfo;
    private WifiInfo mLastInfo;

    private final NetworkScoreManager mNetworkScoreManager;
    private WifiNetworkScoreCache mScoreCache;
    private boolean mNetworkScoringUiEnabled;
    private long mMaxSpeedLabelScoreCacheAge;

    private static final String WIFI_SECURITY_PSK = "PSK";
    private static final String WIFI_SECURITY_EAP = "EAP";
    private static final String WIFI_SECURITY_SAE = "SAE";
    private static final String WIFI_SECURITY_OWE = "OWE";
    private static final String WIFI_SECURITY_SUITE_B_192 = "SUITE_B_192";

    @GuardedBy("mLock")
    Scanner mScanner;

    private static IntentFilter newIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
        filter.addAction(WifiManager.ACTION_LINK_CONFIGURATION_CHANGED);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        return filter;
    }

    public WifiTracker(Context context, WifiListener wifiListener) {
        this(context, wifiListener, context.getSystemService(WifiManager.class), context.getSystemService(ConnectivityManager.class), context.getSystemService(NetworkScoreManager.class), newIntentFilter());
    }

    WifiTracker(Context context, WifiListener wifiListener, WifiManager wifiManager, ConnectivityManager connectivityManager, NetworkScoreManager networkScoreManager, IntentFilter filter) {
        mContext = context;
        mWifiManager = wifiManager;
        mListener = new WifiListenerExecutor(wifiListener);
        mConnectivityManager = connectivityManager;
        mFilter = filter;
        mNetworkRequest = new NetworkRequest.Builder().clearCapabilities().addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN).addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build();
        mNetworkScoreManager = networkScoreManager;
        final HandlerThread workThread = new HandlerThread(TAG + "{" + Integer.toHexString(System.identityHashCode(this)) + "}", Process.THREAD_PRIORITY_BACKGROUND);
        workThread.start();
        setWorkThread(workThread);
    }

    void setWorkThread(HandlerThread workThread) {
        mWorkThread = workThread;
        mWorkHandler = new Handler(workThread.getLooper());
        mScoreCache = new WifiNetworkScoreCache(mContext, new CacheListener(mWorkHandler) {
            @Override
            public void networkCacheUpdated(List<ScoredNetwork> networks) {
                if (!mRegistered) return;

                if (Log.isLoggable(TAG, Log.VERBOSE)) {
                    Log.v(TAG, "Score cache was updated with networks: " + networks);
                }
                updateNetworkScores();
            }
        });
    }

    @Override
    public void onDestroy() {
        mWorkThread.quit();
    }

    public void pauseScanning() {
        synchronized (mLock) {
            if (mScanner != null) {
                mScanner.pause();
                mScanner = null;
            }
        }
        mStaleScanResults = true;
    }

    public void resumeScanning() {
        synchronized (mLock) {
            if (mScanner == null) {
                mScanner = new Scanner();
            }

            if (isWifiEnabled()) {
                mScanner.resume();
            }
        }
    }

    @Override
    @MainThread
    public void onStart() {
        forceUpdate();
        registerScoreCache();
        mNetworkScoringUiEnabled = Settings.Global.getInt(mContext.getContentResolver(), Settings.Global.NETWORK_SCORING_UI_ENABLED, 0) == 1;

        mMaxSpeedLabelScoreCacheAge = Settings.Global.getLong(mContext.getContentResolver(), Settings.Global.SPEED_LABEL_CACHE_EVICTION_AGE_MILLIS, DEFAULT_MAX_CACHED_SCORE_AGE_MILLIS);

        resumeScanning();
        if (!mRegistered) {
            mContext.registerReceiver(mReceiver, mFilter, null, mWorkHandler);
            mNetworkCallback = new WifiTrackerNetworkCallback();
            mConnectivityManager.registerNetworkCallback(mNetworkRequest, mNetworkCallback, mWorkHandler);
            mRegistered = true;
        }
    }

    @MainThread
    void forceUpdate() {
        mLastInfo = mWifiManager.getConnectionInfo();
        mLastNetworkInfo = mConnectivityManager.getNetworkInfo(mWifiManager.getCurrentNetwork());

        fetchScansAndConfigsAndUpdateAccessPoints();
    }

    private void registerScoreCache() {
        mNetworkScoreManager.registerNetworkScoreCache(NetworkKey.TYPE_WIFI, mScoreCache, NetworkScoreManager.SCORE_FILTER_SCAN_RESULTS);
    }

    private void requestScoresForNetworkKeys(Collection<NetworkKey> keys) {
        if (keys.isEmpty()) return;

        Log.d(TAG, "Requesting scores for Network Keys: " + keys);
        mNetworkScoreManager.requestScores(keys.toArray(new NetworkKey[keys.size()]));
        synchronized (mLock) {
            mRequestedScores.addAll(keys);
        }
    }

    @Override
    @MainThread
    public void onStop() {
        if (mRegistered) {
            mContext.unregisterReceiver(mReceiver);
            mConnectivityManager.unregisterNetworkCallback(mNetworkCallback);
            mRegistered = false;
        }
        unregisterScoreCache();
        pauseScanning();
        mWorkHandler.removeCallbacksAndMessages(null);
    }

    private void unregisterScoreCache() {
        mNetworkScoreManager.unregisterNetworkScoreCache(NetworkKey.TYPE_WIFI, mScoreCache);

        synchronized (mLock) {
            mRequestedScores.clear();
        }
    }

    @AnyThread
    public List<AccessPoint> getAccessPoints() {
        synchronized (mLock) {
            return new ArrayList<>(mInternalAccessPoints);
        }
    }

    public boolean isWifiEnabled() {
        return mWifiManager != null && mWifiManager.isWifiEnabled();
    }

    private ArrayMap<String, List<ScanResult>> updateScanResultCache(final List<ScanResult> newResults) {
        for (ScanResult newResult : newResults) {
            if (newResult.SSID == null || newResult.SSID.isEmpty()) {
                continue;
            }
            mScanResultCache.put(newResult.BSSID, newResult);
        }
        evictOldScans();
        ArrayMap<String, List<ScanResult>> scanResultsByApKey = new ArrayMap<>();
        for (ScanResult result : mScanResultCache.values()) {
            if (result.SSID == null || result.SSID.length() == 0 || result.capabilities.contains("[IBSS]")) {
                continue;
            }

            String apKey = AccessPoint.getKey(mContext, result);
            List<ScanResult> resultList;
            if (scanResultsByApKey.containsKey(apKey)) {
                resultList = scanResultsByApKey.get(apKey);
            } else {
                resultList = new ArrayList<>();
                scanResultsByApKey.put(apKey, resultList);
            }

            resultList.add(result);
        }
        return scanResultsByApKey;
    }


    private void evictOldScans() {
        long evictionTimeoutMillis = mLastScanSucceeded ? MAX_SCAN_RESULT_AGE_MILLIS : MAX_SCAN_RESULT_AGE_MILLIS * 2;

        long nowMs = SystemClock.elapsedRealtime();
        for (Iterator<ScanResult> iter = mScanResultCache.values().iterator(); iter.hasNext(); ) {
            ScanResult result = iter.next();
            if (nowMs - result.timestamp / 1000 > evictionTimeoutMillis) {
                iter.remove();
            }
        }
    }

    private WifiConfiguration getWifiConfigurationForNetworkId(int networkId, final List<WifiConfiguration> configs) {
        if (configs != null) {
            for (WifiConfiguration config : configs) {
                if (mLastInfo != null && networkId == config.networkId) {
                    return config;
                }
            }
        }
        return null;
    }

    private void fetchScansAndConfigsAndUpdateAccessPoints() {
        List<ScanResult> newScanResults = mWifiManager.getScanResults();

        final List<ScanResult> filteredScanResults = filterScanResultsByCapabilities(newScanResults);

        Log.i(TAG, "Fetched scan results: " + filteredScanResults);

        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        updateAccessPoints(filteredScanResults, configs);
    }

    private void updateAccessPoints(final List<ScanResult> newScanResults, List<WifiConfiguration> configs) {
        WifiConfiguration connectionConfig = null;
        if (mLastInfo != null) {
            connectionConfig = getWifiConfigurationForNetworkId(mLastInfo.getNetworkId(), configs);
        }

        synchronized (mLock) {
            ArrayMap<String, List<ScanResult>> scanResultsByApKey = updateScanResultCache(newScanResults);

            List<AccessPoint> cachedAccessPoints;
            cachedAccessPoints = new ArrayList<>(mInternalAccessPoints);

            ArrayList<AccessPoint> accessPoints = new ArrayList<>();

            final List<NetworkKey> scoresToRequest = new ArrayList<>();

            for (Map.Entry<String, List<ScanResult>> entry : scanResultsByApKey.entrySet()) {
                for (ScanResult result : entry.getValue()) {
                    NetworkKey key = NetworkKey.createFromScanResult(result);
                    if (key != null && !mRequestedScores.contains(key)) {
                        scoresToRequest.add(key);
                    }
                }

                AccessPoint accessPoint = getCachedOrCreate(entry.getValue(), cachedAccessPoints);

                final List<WifiConfiguration> matchedConfigs = configs.stream().filter(config -> accessPoint.matches(config)).collect(Collectors.toList());

                final int matchedConfigCount = matchedConfigs.size();
                if (matchedConfigCount == 0) {
                    accessPoint.update(null);
                } else if (matchedConfigCount == 1) {
                    accessPoint.update(matchedConfigs.get(0));
                } else {
                    Optional<WifiConfiguration> preferredConfig = matchedConfigs.stream().filter(config -> isSaeOrOwe(config)).findFirst();
                    if (preferredConfig.isPresent()) {
                        accessPoint.update(preferredConfig.get());
                    } else {
                        accessPoint.update(matchedConfigs.get(0));
                    }
                }

                accessPoints.add(accessPoint);
            }

            List<ScanResult> cachedScanResults = new ArrayList<>(mScanResultCache.values());

            accessPoints.addAll(updatePasspointAccessPoints(mWifiManager.getAllMatchingWifiConfigs(cachedScanResults), cachedAccessPoints));

            accessPoints.addAll(updateOsuAccessPoints(mWifiManager.getMatchingOsuProviders(cachedScanResults), cachedAccessPoints));

            if (mLastInfo != null && mLastNetworkInfo != null) {
                for (AccessPoint ap : accessPoints) {
                    ap.update(connectionConfig, mLastInfo, mLastNetworkInfo);
                }
            }

            if (accessPoints.isEmpty() && connectionConfig != null) {
                AccessPoint activeAp = new AccessPoint(mContext, connectionConfig);
                activeAp.update(connectionConfig, mLastInfo, mLastNetworkInfo);
                accessPoints.add(activeAp);
                scoresToRequest.add(NetworkKey.createFromWifiInfo(mLastInfo));
            }

            requestScoresForNetworkKeys(scoresToRequest);
            for (AccessPoint ap : accessPoints) {
                ap.update(mScoreCache, mNetworkScoringUiEnabled, mMaxSpeedLabelScoreCacheAge);
            }
            Collections.sort(accessPoints, new Comparator<AccessPoint>() {
                @Override
                public int compare(AccessPoint o1, AccessPoint o2) {
                    return o2.getLevel() - o1.getLevel();
                }
            });
//            Collections.sort(accessPoints);

            Log.d(TAG, "------ Dumping AccessPoints that were not seen on this scan ------");
            for (AccessPoint prevAccessPoint : mInternalAccessPoints) {
                String prevTitle = prevAccessPoint.getTitle();
                boolean found = false;
                for (AccessPoint newAccessPoint : accessPoints) {
                    if (newAccessPoint.getTitle() != null && newAccessPoint.getTitle().equals(prevTitle)) {
                        found = true;
                        break;
                    }
                }
                if (!found) Log.d(TAG, "Did not find " + prevTitle + " in this scan");
            }
            Log.d(TAG, "---- Done dumping AccessPoints that were not seen on this scan ----");

            mInternalAccessPoints.clear();
            mInternalAccessPoints.addAll(accessPoints);
        }

        conditionallyNotifyListeners();
    }

    private static boolean isSaeOrOwe(WifiConfiguration config) {
        final int security = AccessPoint.getSecurity(config);
        return security == AccessPoint.SECURITY_SAE || security == AccessPoint.SECURITY_OWE;
    }

    List<AccessPoint> updatePasspointAccessPoints(List<Pair<WifiConfiguration, Map<Integer, List<ScanResult>>>> passpointConfigsAndScans, List<AccessPoint> accessPointCache) {
        List<AccessPoint> accessPoints = new ArrayList<>();

        Set<String> seenFQDNs = new ArraySet<>();
        for (Pair<WifiConfiguration, Map<Integer, List<ScanResult>>> pairing : passpointConfigsAndScans) {
            WifiConfiguration config = pairing.first;
            if (seenFQDNs.add(config.FQDN)) {
                List<ScanResult> homeScans = pairing.second.get(WifiManager.PASSPOINT_HOME_NETWORK);
                List<ScanResult> roamingScans = pairing.second.get(WifiManager.PASSPOINT_ROAMING_NETWORK);

                AccessPoint accessPoint = getCachedOrCreatePasspoint(config, homeScans, roamingScans, accessPointCache);
                accessPoints.add(accessPoint);
            }
        }
        return accessPoints;
    }

    List<AccessPoint> updateOsuAccessPoints(Map<OsuProvider, List<ScanResult>> providersAndScans, List<AccessPoint> accessPointCache) {
        List<AccessPoint> accessPoints = new ArrayList<>();

        Set<OsuProvider> alreadyProvisioned = mWifiManager.getMatchingPasspointConfigsForOsuProviders(providersAndScans.keySet()).keySet();
        for (OsuProvider provider : providersAndScans.keySet()) {
            if (!alreadyProvisioned.contains(provider)) {
                AccessPoint accessPointOsu = getCachedOrCreateOsu(provider, providersAndScans.get(provider), accessPointCache);
                accessPoints.add(accessPointOsu);
            }
        }
        return accessPoints;
    }

    private AccessPoint getCachedOrCreate(List<ScanResult> scanResults, List<AccessPoint> cache) {
        AccessPoint accessPoint = getCachedByKey(cache, AccessPoint.getKey(mContext, scanResults.get(0)));
        if (accessPoint == null) {
            accessPoint = new AccessPoint(mContext, scanResults);
        } else {
            accessPoint.setScanResults(scanResults);
        }
        return accessPoint;
    }

    private AccessPoint getCachedOrCreatePasspoint(WifiConfiguration config, List<ScanResult> homeScans, List<ScanResult> roamingScans, List<AccessPoint> cache) {
        AccessPoint accessPoint = getCachedByKey(cache, AccessPoint.getKey(config));
        if (accessPoint == null) {
            accessPoint = new AccessPoint(mContext, config, homeScans, roamingScans);
        } else {
            accessPoint.update(config);
            accessPoint.setScanResultsPasspoint(homeScans, roamingScans);
        }
        return accessPoint;
    }

    private AccessPoint getCachedOrCreateOsu(OsuProvider provider, List<ScanResult> scanResults, List<AccessPoint> cache) {
        AccessPoint accessPoint = getCachedByKey(cache, AccessPoint.getKey(provider));
        if (accessPoint == null) {
            accessPoint = new AccessPoint(mContext, provider, scanResults);
        } else {
            accessPoint.setScanResults(scanResults);
        }
        return accessPoint;
    }

    private AccessPoint getCachedByKey(List<AccessPoint> cache, String key) {
        ListIterator<AccessPoint> lit = cache.listIterator();
        while (lit.hasNext()) {
            AccessPoint currentAccessPoint = lit.next();
            if (currentAccessPoint.getKey().equals(key)) {
                lit.remove();
                return currentAccessPoint;
            }
        }
        return null;
    }

    public void updateNetworkInfo(NetworkInfo networkInfo) {
        if (!isWifiEnabled()) {
            clearAccessPointsAndConditionallyUpdate();
            return;
        }

        if (networkInfo != null) {
            mLastNetworkInfo = networkInfo;
            Log.d(TAG, "mLastNetworkInfo set: " + mLastNetworkInfo);

            if (networkInfo.isConnected() != mConnected.getAndSet(networkInfo.isConnected())) {
                mListener.onConnectedChanged();
            }
        }

        WifiConfiguration connectionConfig = null;

        mLastInfo = mWifiManager.getConnectionInfo();
        Log.d(TAG, "mLastInfo set as: " + mLastInfo);

        if (mLastInfo != null) {
            connectionConfig = getWifiConfigurationForNetworkId(mLastInfo.getNetworkId(), mWifiManager.getConfiguredNetworks());
        }

        boolean updated = false;
        boolean reorder = false;
        synchronized (mLock) {
            for (int i = mInternalAccessPoints.size() - 1; i >= 0; --i) {
                AccessPoint ap = mInternalAccessPoints.get(i);
                boolean previouslyConnected = ap.isActive();
                if (ap.update(connectionConfig, mLastInfo, mLastNetworkInfo)) {
                    updated = true;
                    if (previouslyConnected != ap.isActive()) reorder = true;
                }
                if (ap.update(mScoreCache, mNetworkScoringUiEnabled, mMaxSpeedLabelScoreCacheAge)) {
                    reorder = true;
                    updated = true;
                }
            }

            if (reorder) {
                Collections.sort(mInternalAccessPoints);
            }
            if (updated) {
                conditionallyNotifyListeners();
            }
        }
    }

    private void clearAccessPointsAndConditionallyUpdate() {
        synchronized (mLock) {
            if (!mInternalAccessPoints.isEmpty()) {
                mInternalAccessPoints.clear();
                conditionallyNotifyListeners();
            }
        }
    }

    private void updateNetworkScores() {
        synchronized (mLock) {
            boolean updated = false;
            for (int i = 0; i < mInternalAccessPoints.size(); i++) {
                if (mInternalAccessPoints.get(i).update(mScoreCache, mNetworkScoringUiEnabled, mMaxSpeedLabelScoreCacheAge)) {
                    updated = true;
                }
            }
            if (updated) {
                Collections.sort(mInternalAccessPoints);
                conditionallyNotifyListeners();
            }
        }
    }

    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                mStaleScanResults = false;
                mLastScanSucceeded = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, true);
                Settings.Global.putInt(mContext.getContentResolver(), "SETTING_APP_WIFI_SCANING", 0);
                fetchScansAndConfigsAndUpdateAccessPoints();
            } else if (WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION.equals(action) || WifiManager.ACTION_LINK_CONFIGURATION_CHANGED.equals(action)) {
                fetchScansAndConfigsAndUpdateAccessPoints();
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                updateNetworkInfo(info);
                fetchScansAndConfigsAndUpdateAccessPoints();
            } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
                updateNetworkInfo(null);
            }
        }
    };

    private final class WifiTrackerNetworkCallback extends ConnectivityManager.NetworkCallback {
        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities nc) {
            if (network.equals(mWifiManager.getCurrentNetwork())) {
                updateNetworkInfo(null);
            }
        }
    }

    class Scanner extends Handler {
        static final int MSG_SCAN = 0;

        private int mRetry = 0;

        void resume() {
            Log.d(TAG, "Scanner resume");
            if (!hasMessages(MSG_SCAN)) {
                sendEmptyMessage(MSG_SCAN);
            }
        }

        void pause() {
            Log.d(TAG, "Scanner pause");
            mRetry = 0;
            removeMessages(MSG_SCAN);
        }

        boolean isScanning() {
            return hasMessages(MSG_SCAN);
        }

        @Override
        public void handleMessage(Message message) {
            if (message.what != MSG_SCAN) return;
            if (mWifiManager.startScan()) {
                mRetry = 0;
            } else if (++mRetry >= 3) {
                mRetry = 0;
                if (mContext != null) {
                    ToastUtils.showToast(mContext, mContext.getString(R.string.wifi_fail_to_scan), Toast.LENGTH_SHORT);
                }
                return;
            }
            sendEmptyMessageDelayed(MSG_SCAN, WIFI_RESCAN_INTERVAL_MS);
        }
    }

    class WifiListenerExecutor implements WifiListener {

        private final WifiListener mDelegate;

        public WifiListenerExecutor(WifiListener listener) {
            mDelegate = listener;
        }

        @Override
        public void onConnectedChanged() {
            runAndLog(mDelegate::onConnectedChanged, "Invoking onConnectedChanged callback");
        }

        @Override
        public void onAccessPointsChanged() {
            runAndLog(mDelegate::onAccessPointsChanged, "Invoking onAccessPointsChanged callback");
        }

        private void runAndLog(Runnable r, String verboseLog) {
            ThreadUtils.postOnMainThread(() -> {
                if (mRegistered) {
                    Log.i(TAG, verboseLog);
                    r.run();
                }
            });
        }
    }

    public interface WifiListener {
        void onConnectedChanged();

        void onAccessPointsChanged();
    }

    private void conditionallyNotifyListeners() {
        if (mStaleScanResults) {
            return;
        }
        mListener.onAccessPointsChanged();
    }

    private List<ScanResult> filterScanResultsByCapabilities(List<ScanResult> scanResults) {
        if (scanResults == null) {
            return null;
        }

        final boolean isOweSupported = mWifiManager.isEnhancedOpenSupported();
        final boolean isSaeSupported = mWifiManager.isWpa3SaeSupported();
        final boolean isSuiteBSupported = mWifiManager.isWpa3SuiteBSupported();

        List<ScanResult> filteredScanResultList = new ArrayList<>();

        for (ScanResult scanResult : scanResults) {
            if (scanResult.capabilities.contains(WIFI_SECURITY_PSK)) {
                filteredScanResultList.add(scanResult);
                continue;
            }

            if ((scanResult.capabilities.contains(WIFI_SECURITY_SUITE_B_192) && !isSuiteBSupported) || (scanResult.capabilities.contains(WIFI_SECURITY_SAE) && !isSaeSupported) || (scanResult.capabilities.contains(WIFI_SECURITY_OWE) && !isOweSupported)) {
                Log.v(TAG, "filterScanResultsByCapabilities: Filtering SSID " + scanResult.SSID + " with capabilities: " + scanResult.capabilities);
            } else {
                filteredScanResultList.add(scanResult);
            }
        }

        return filteredScanResultList;
    }
}
