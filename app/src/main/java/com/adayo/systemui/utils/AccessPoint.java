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

import android.annotation.IntDef;
import android.annotation.MainThread;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkInfo.State;
import android.net.NetworkKey;
import android.net.ScoredNetwork;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.net.wifi.hotspot2.OsuProvider;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;

import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.CollectionUtils;
import com.android.systemui.R;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class AccessPoint implements Comparable<AccessPoint>, Serializable {
    static final String TAG = AccessPoint.class.getSimpleName();

    public static final int LOWER_FREQ_24GHZ = 2400;
    public static final int HIGHER_FREQ_24GHZ = 2500;
    public static final int LOWER_FREQ_5GHZ = 4900;
    public static final int HIGHER_FREQ_5GHZ = 5900;
    private String mKey;
    private final Object mLock = new Object();

    @IntDef({Speed.NONE, Speed.SLOW, Speed.MODERATE, Speed.FAST, Speed.VERY_FAST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Speed {
        int NONE = 0;
        int SLOW = 5;
        int MODERATE = 10;
        int FAST = 20;
        int VERY_FAST = 30;
    }

    @IntDef({PasspointConfigurationVersion.INVALID,
            PasspointConfigurationVersion.NO_OSU_PROVISIONED,
            PasspointConfigurationVersion.OSU_PROVISIONED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PasspointConfigurationVersion {
        int INVALID = 0;
        int NO_OSU_PROVISIONED = 1;
        int OSU_PROVISIONED = 2;
    }

    @GuardedBy("mLock")
    private final ArraySet<ScanResult> mScanResults = new ArraySet<>();

    @GuardedBy("mLock")
    private final ArraySet<ScanResult> mExtraScanResults = new ArraySet<>();

    private final Map<String, TimestampedScoredNetwork> mScoredNetworkCache = new HashMap<>();

    static final String KEY_NETWORKINFO = "key_networkinfo";
    static final String KEY_WIFIINFO = "key_wifiinfo";
    static final String KEY_SSID = "key_ssid";
    static final String KEY_SECURITY = "key_security";
    static final String KEY_SPEED = "key_speed";
    static final String KEY_PSKTYPE = "key_psktype";
    static final String KEY_SCANRESULTS = "key_scanresults";
    static final String KEY_SCOREDNETWORKCACHE = "key_scorednetworkcache";
    static final String KEY_CONFIG = "key_config";
    static final String KEY_PASSPOINT_UNIQUE_ID = "key_passpoint_unique_id";
    static final String KEY_FQDN = "key_fqdn";
    static final String KEY_PROVIDER_FRIENDLY_NAME = "key_provider_friendly_name";
    static final String KEY_EAPTYPE = "eap_psktype";
    static final String KEY_SUBSCRIPTION_EXPIRATION_TIME_IN_MILLIS =
            "key_subscription_expiration_time_in_millis";
    static final String KEY_PASSPOINT_CONFIGURATION_VERSION = "key_passpoint_configuration_version";
    static final String KEY_IS_PSK_SAE_TRANSITION_MODE = "key_is_psk_sae_transition_mode";
    static final String KEY_IS_OWE_TRANSITION_MODE = "key_is_owe_transition_mode";
    static final AtomicInteger sLastId = new AtomicInteger(0);

    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_WEP = 1;
    public static final int SECURITY_PSK = 2;
    public static final int SECURITY_EAP = 3;
    public static final int SECURITY_OWE = 4;
    public static final int SECURITY_SAE = 5;
    public static final int SECURITY_EAP_SUITE_B = 6;
    public static final int SECURITY_MAX_VAL = 7; // Has to be the last

    private static final int PSK_UNKNOWN = 0;
    private static final int PSK_WPA = 1;
    private static final int PSK_WPA2 = 2;
    private static final int PSK_WPA_WPA2 = 3;

    private static final int EAP_UNKNOWN = 0;
    private static final int EAP_WPA = 1; // WPA-EAP
    private static final int EAP_WPA2_WPA3 = 2; // RSN-EAP

    public static final int UNREACHABLE_RSSI = Integer.MIN_VALUE;

    public static final String KEY_PREFIX_AP = "AP:";
    public static final String KEY_PREFIX_PASSPOINT_UNIQUE_ID = "PASSPOINT:";
    public static final String KEY_PREFIX_OSU = "OSU:";

    private final Context mContext;

    private WifiManager mWifiManager;

    private String ssid;
    private String bssid;
    private int security;
    private int networkId = WifiConfiguration.INVALID_NETWORK_ID;

    private int pskType = PSK_UNKNOWN;
    private int mEapType = EAP_UNKNOWN;

    private WifiConfiguration mConfig;

    private int mRssi = UNREACHABLE_RSSI;

    private WifiInfo mInfo;
    private NetworkInfo mNetworkInfo;
    AccessPointListener mAccessPointListener;

    private Object mTag;

    @Speed
    private int mSpeed = Speed.NONE;
    private boolean mIsScoredNetworkMetered = false;

    private String mPasspointUniqueId;
    private String mFqdn;
    private String mProviderFriendlyName;
    private boolean mIsRoaming = false;
    private long mSubscriptionExpirationTimeInMillis;
    @PasspointConfigurationVersion
    private int mPasspointConfigurationVersion =
            PasspointConfigurationVersion.INVALID;

    private OsuProvider mOsuProvider;

    private String mOsuStatus;
    private String mOsuFailure;
    private boolean mOsuProvisioningComplete = false;

    private boolean mIsPskSaeTransitionMode = false;
    private boolean mIsOweTransitionMode = false;

    public AccessPoint(Context context, Bundle savedState) {
        mContext = context;

        if (savedState.containsKey(KEY_CONFIG)) {
            mConfig = savedState.getParcelable(KEY_CONFIG);
        }
        if (mConfig != null) {
            loadConfig(mConfig);
        }
        if (savedState.containsKey(KEY_SSID)) {
            ssid = savedState.getString(KEY_SSID);
        }
        if (savedState.containsKey(KEY_SECURITY)) {
            security = savedState.getInt(KEY_SECURITY);
        }
        if (savedState.containsKey(KEY_SPEED)) {
            mSpeed = savedState.getInt(KEY_SPEED);
        }
        if (savedState.containsKey(KEY_PSKTYPE)) {
            pskType = savedState.getInt(KEY_PSKTYPE);
        }
        if (savedState.containsKey(KEY_EAPTYPE)) {
            mEapType = savedState.getInt(KEY_EAPTYPE);
        }
        mInfo = savedState.getParcelable(KEY_WIFIINFO);
        if (savedState.containsKey(KEY_NETWORKINFO)) {
            mNetworkInfo = savedState.getParcelable(KEY_NETWORKINFO);
        }
        if (savedState.containsKey(KEY_SCANRESULTS)) {
            Parcelable[] scanResults = savedState.getParcelableArray(KEY_SCANRESULTS);
            mScanResults.clear();
            for (Parcelable result : scanResults) {
                mScanResults.add((ScanResult) result);
            }
        }
        if (savedState.containsKey(KEY_SCOREDNETWORKCACHE)) {
            ArrayList<TimestampedScoredNetwork> scoredNetworkArrayList =
                    savedState.getParcelableArrayList(KEY_SCOREDNETWORKCACHE);
            for (TimestampedScoredNetwork timedScore : scoredNetworkArrayList) {
                mScoredNetworkCache.put(timedScore.getScore().networkKey.wifiKey.bssid, timedScore);
            }
        }
        if (savedState.containsKey(KEY_PASSPOINT_UNIQUE_ID)) {
            mPasspointUniqueId = savedState.getString(KEY_PASSPOINT_UNIQUE_ID);
        }
        if (savedState.containsKey(KEY_FQDN)) {
            mFqdn = savedState.getString(KEY_FQDN);
        }
        if (savedState.containsKey(KEY_PROVIDER_FRIENDLY_NAME)) {
            mProviderFriendlyName = savedState.getString(KEY_PROVIDER_FRIENDLY_NAME);
        }
        if (savedState.containsKey(KEY_SUBSCRIPTION_EXPIRATION_TIME_IN_MILLIS)) {
            mSubscriptionExpirationTimeInMillis =
                    savedState.getLong(KEY_SUBSCRIPTION_EXPIRATION_TIME_IN_MILLIS);
        }
        if (savedState.containsKey(KEY_PASSPOINT_CONFIGURATION_VERSION)) {
            mPasspointConfigurationVersion = savedState.getInt(KEY_PASSPOINT_CONFIGURATION_VERSION);
        }
        if (savedState.containsKey(KEY_IS_PSK_SAE_TRANSITION_MODE)) {
            mIsPskSaeTransitionMode = savedState.getBoolean(KEY_IS_PSK_SAE_TRANSITION_MODE);
        }
        if (savedState.containsKey(KEY_IS_OWE_TRANSITION_MODE)) {
            mIsOweTransitionMode = savedState.getBoolean(KEY_IS_OWE_TRANSITION_MODE);
        }

        update(mConfig, mInfo, mNetworkInfo);

        updateKey();
        updateBestRssiInfo();
    }

    public AccessPoint(Context context, WifiConfiguration config) {
        mContext = context;
        loadConfig(config);
        updateKey();
    }

//    public AccessPoint(Context context, PasspointConfiguration config) {
//        mContext = context;
//        mPasspointUniqueId = config.getUniqueId();
//        mFqdn = config.getHomeSp().getFqdn();
//        mProviderFriendlyName = config.getHomeSp().getFriendlyName();
//        mSubscriptionExpirationTimeInMillis = config.getSubscriptionExpirationTimeMillis();
//        if (config.isOsuProvisioned()) {
//            mPasspointConfigurationVersion = PasspointConfigurationVersion.OSU_PROVISIONED;
//        } else {
//            mPasspointConfigurationVersion = PasspointConfigurationVersion.NO_OSU_PROVISIONED;
//        }
//        updateKey();
//    }

    public AccessPoint(@NonNull Context context, @NonNull WifiConfiguration config,
                       @Nullable Collection<ScanResult> homeScans,
                       @Nullable Collection<ScanResult> roamingScans) {
        mContext = context;
        networkId = config.networkId;
        mConfig = config;
        mPasspointUniqueId = config.getKey();
        mFqdn = config.FQDN;
        setScanResultsPasspoint(homeScans, roamingScans);
        updateKey();
    }

    public AccessPoint(@NonNull Context context, @NonNull OsuProvider provider,
                       @NonNull Collection<ScanResult> results) {
        mContext = context;
        mOsuProvider = provider;
        setScanResults(results);
        updateKey();
    }

    AccessPoint(Context context, Collection<ScanResult> results) {
        mContext = context;
        setScanResults(results);
        updateKey();
    }

    @VisibleForTesting
    void loadConfig(WifiConfiguration config) {
        if (config != null) {
            ssid = (config.SSID == null ? "" : removeDoubleQuotes(config.SSID));
            bssid = config.BSSID;
            security = getSecurity(config);
            networkId = config.networkId;
            mConfig = config;
        }
    }

    private void updateKey() {
        if (isPasspoint()) {
            mKey = getKey(mConfig);
        } else if (isPasspointConfig()) {
            mKey = getKey(mPasspointUniqueId);
        } else if (isOsuProvider()) {
            mKey = getKey(mOsuProvider);
        } else { // Non-Passpoint AP
            mKey = getKey(getSsidStr(), getBssid(), getSecurity());
        }
    }

    @Override
    public int compareTo(@NonNull AccessPoint other) {
        // Active one goes first.
        if (isActive() && !other.isActive()) return -1;
        if (!isActive() && other.isActive()) return 1;

        // Reachable one goes before unreachable one.
        if (isReachable() && !other.isReachable()) return -1;
        if (!isReachable() && other.isReachable()) return 1;

        // Configured (saved) one goes before unconfigured one.
        if (isSaved() && !other.isSaved()) return -1;
        if (!isSaved() && other.isSaved()) return 1;

        // Faster speeds go before slower speeds - but only if visible change in speed label
        if (getSpeed() != other.getSpeed()) {
            return other.getSpeed() - getSpeed();
        }

        WifiManager wifiManager = getWifiManager();
        // Sort by signal strength, bucketed by level
        int difference = wifiManager.calculateSignalLevel(other.mRssi)
                - wifiManager.calculateSignalLevel(mRssi);
        if (difference != 0) {
            return difference;
        }

        // Sort by title.
        difference = getTitle().compareToIgnoreCase(other.getTitle());
        if (difference != 0) {
            return difference;
        }

        // Do a case sensitive comparison to distinguish SSIDs that differ in case only
        return getSsidStr().compareTo(other.getSsidStr());
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof AccessPoint)) return false;
        return (this.compareTo((AccessPoint) other) == 0);
    }

    @Override
    public int hashCode() {
        int result = 0;
        if (mInfo != null) result += 13 * mInfo.hashCode();
        result += 19 * mRssi;
        result += 23 * networkId;
        result += 29 * ssid.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append("AccessPoint(")
                .append(ssid);
        if (bssid != null) {
            builder.append(":").append(bssid);
        }
        if (isSaved()) {
            builder.append(',').append("saved");
        }
        if (isActive()) {
            builder.append(',').append("active");
        }
        if (isEphemeral()) {
            builder.append(',').append("ephemeral");
        }
        if (isConnectable()) {
            builder.append(',').append("connectable");
        }
        if ((security != SECURITY_NONE) && (security != SECURITY_OWE)) {
            builder.append(',').append(securityToString(security, pskType));
        }
        builder.append(",level=").append(getLevel());
        if (mSpeed != Speed.NONE) {
            builder.append(",speed=").append(mSpeed);
        }
        builder.append(",metered=").append(isMetered());

        builder.append(",rssi=").append(mRssi);
        synchronized (mLock) {
            builder.append(",scan cache size=").append(mScanResults.size()
                    + mExtraScanResults.size());
        }

        return builder.append(')').toString();
    }

    boolean update(
            WifiNetworkScoreCache scoreCache,
            boolean scoringUiEnabled,
            long maxScoreCacheAgeMillis) {
        boolean scoreChanged = false;
        if (scoringUiEnabled) {
            scoreChanged = updateScores(scoreCache, maxScoreCacheAgeMillis);
        }
        return updateMetered(scoreCache) || scoreChanged;
    }

    private boolean updateScores(WifiNetworkScoreCache scoreCache, long maxScoreCacheAgeMillis) {
        long nowMillis = SystemClock.elapsedRealtime();
        synchronized (mLock) {
            for (ScanResult result : mScanResults) {
                ScoredNetwork score = scoreCache.getScoredNetwork(result);
                if (score == null) {
                    continue;
                }
                TimestampedScoredNetwork timedScore = mScoredNetworkCache.get(result.BSSID);
                if (timedScore == null) {
                    mScoredNetworkCache.put(
                            result.BSSID, new TimestampedScoredNetwork(score, nowMillis));
                } else {
                    // Update data since the has been seen in the score cache
                    timedScore.update(score, nowMillis);
                }
            }
        }

        // Remove old cached networks
        long evictionCutoff = nowMillis - maxScoreCacheAgeMillis;
        Iterator<TimestampedScoredNetwork> iterator = mScoredNetworkCache.values().iterator();
        iterator.forEachRemaining(timestampedScoredNetwork -> {
            if (timestampedScoredNetwork.getUpdatedTimestampMillis() < evictionCutoff) {
                iterator.remove();
            }
        });

        return updateSpeed();
    }

    private boolean updateSpeed() {
        int oldSpeed = mSpeed;
        mSpeed = generateAverageSpeedForSsid();

        boolean changed = oldSpeed != mSpeed;
        Log.i(TAG, String.format("%s: Set speed to %d", ssid, mSpeed));
        return changed;
    }

    @Speed
    private int generateAverageSpeedForSsid() {
        if (mScoredNetworkCache.isEmpty()) {
            return Speed.NONE;
        }

        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, String.format("Generating fallbackspeed for %s using cache: %s",
                    getSsidStr(), mScoredNetworkCache));
        }

        int count = 0;
        int totalSpeed = 0;
        for (TimestampedScoredNetwork timedScore : mScoredNetworkCache.values()) {
            int speed = timedScore.getScore().calculateBadge(mRssi);
            if (speed != Speed.NONE) {
                count++;
                totalSpeed += speed;
            }
        }
        int speed = count == 0 ? Speed.NONE : totalSpeed / count;
        Log.i(TAG, String.format("%s generated fallback speed is: %d", getSsidStr(), speed));
        return roundToClosestSpeedEnum(speed);
    }

    private boolean updateMetered(WifiNetworkScoreCache scoreCache) {
        boolean oldMetering = mIsScoredNetworkMetered;
        mIsScoredNetworkMetered = false;

        if (isActive() && mInfo != null) {
            NetworkKey key = NetworkKey.createFromWifiInfo(mInfo);
            ScoredNetwork score = scoreCache.getScoredNetwork(key);
            if (score != null) {
                mIsScoredNetworkMetered |= score.meteredHint;
            }
        } else {
            synchronized (mLock) {
                for (ScanResult result : mScanResults) {
                    ScoredNetwork score = scoreCache.getScoredNetwork(result);
                    if (score == null) {
                        continue;
                    }
                    mIsScoredNetworkMetered |= score.meteredHint;
                }
            }
        }
        return oldMetering != mIsScoredNetworkMetered;
    }

    public static String getKey(Context context, ScanResult result) {
        return getKey(result.SSID, result.BSSID, getSecurity(context, result));
    }

    public static String getKey(WifiConfiguration config) {
        if (config.isPasspoint()) {
            return getKey(config.getKey());
        } else {
            return getKey(removeDoubleQuotes(config.SSID), config.BSSID, getSecurity(config));
        }
    }

    public static String getKey(String passpointUniqueId) {
        return new StringBuilder()
                .append(KEY_PREFIX_PASSPOINT_UNIQUE_ID)
                .append(passpointUniqueId).toString();
    }

    public static String getKey(OsuProvider provider) {
        return new StringBuilder()
                .append(KEY_PREFIX_OSU)
                .append(provider.getFriendlyName())
                .append(',')
                .append(provider.getServerUri()).toString();
    }

    private static String getKey(String ssid, String bssid, int security) {
        StringBuilder builder = new StringBuilder();
        builder.append(KEY_PREFIX_AP);
        if (TextUtils.isEmpty(ssid)) {
            builder.append(bssid);
        } else {
            builder.append(ssid);
        }
        builder.append(',').append(security);
        return builder.toString();
    }

    public String getKey() {
        return mKey;
    }

    public boolean matches(AccessPoint other) {
        if (isPasspoint() || isPasspointConfig() || isOsuProvider()) {
            return getKey().equals(other.getKey());
        }

        if (!isSameSsidOrBssid(other)) {
            return false;
        }

        final int otherApSecurity = other.getSecurity();
        if (mIsPskSaeTransitionMode) {
            if (otherApSecurity == SECURITY_SAE && getWifiManager().isWpa3SaeSupported()) {
                return true;
            } else if (otherApSecurity == SECURITY_PSK) {
                return true;
            }
        } else {
            if ((security == SECURITY_SAE || security == SECURITY_PSK)
                    && other.isPskSaeTransitionMode()) {
                return true;
            }
        }

        if (mIsOweTransitionMode) {
            if (otherApSecurity == SECURITY_OWE && getWifiManager().isEnhancedOpenSupported()) {
                return true;
            } else if (otherApSecurity == SECURITY_NONE) {
                return true;
            }
        } else {
            if ((security == SECURITY_OWE || security == SECURITY_NONE)
                    && other.isOweTransitionMode()) {
                return true;
            }
        }

        return security == other.getSecurity();
    }

    public boolean matches(WifiConfiguration config) {
        if (config.isPasspoint()) {
            return (isPasspoint() && config.getKey().equals(mConfig.getKey()));
        }

        if (!ssid.equals(removeDoubleQuotes(config.SSID))
                || (mConfig != null && mConfig.shared != config.shared)) {
            return false;
        }

        final int configSecurity = getSecurity(config);
        if (mIsPskSaeTransitionMode) {
            if (configSecurity == SECURITY_SAE && getWifiManager().isWpa3SaeSupported()) {
                return true;
            } else if (configSecurity == SECURITY_PSK) {
                return true;
            }
        }

        if (mIsOweTransitionMode) {
            if (configSecurity == SECURITY_OWE && getWifiManager().isEnhancedOpenSupported()) {
                return true;
            } else if (configSecurity == SECURITY_NONE) {
                return true;
            }
        }

        return security == getSecurity(config);
    }

    private boolean matches(WifiConfiguration config, WifiInfo wifiInfo) {
        if (config == null || wifiInfo == null) {
            return false;
        }
        if (!config.isPasspoint() && !isSameSsidOrBssid(wifiInfo)) {
            return false;
        }
        return matches(config);
    }

    @VisibleForTesting
    boolean matches(ScanResult scanResult) {
        if (scanResult == null) {
            return false;
        }
        if (isPasspoint() || isOsuProvider()) {
            throw new IllegalStateException("Should not matches a Passpoint by ScanResult");
        }

        if (!isSameSsidOrBssid(scanResult)) {
            return false;
        }

        if (mIsPskSaeTransitionMode) {
            if (scanResult.capabilities.contains("SAE")
                    && getWifiManager().isWpa3SaeSupported()) {
                return true;
            } else if (scanResult.capabilities.contains("PSK")) {
                return true;
            }
        } else {
            if ((security == SECURITY_SAE || security == SECURITY_PSK)
                    && AccessPoint.isPskSaeTransitionMode(scanResult)) {
                return true;
            }
        }

        if (mIsOweTransitionMode) {
            final int scanResultSccurity = getSecurity(mContext, scanResult);
            if (scanResultSccurity == SECURITY_OWE && getWifiManager().isEnhancedOpenSupported()) {
                return true;
            } else if (scanResultSccurity == SECURITY_NONE) {
                return true;
            }
        } else {
            if ((security == SECURITY_OWE || security == SECURITY_NONE)
                    && AccessPoint.isOweTransitionMode(scanResult)) {
                return true;
            }
        }

        return security == getSecurity(mContext, scanResult);
    }

    public WifiConfiguration getConfig() {
        return mConfig;
    }

    public String getPasspointFqdn() {
        return mFqdn;
    }

    public void clearConfig() {
        mConfig = null;
        networkId = WifiConfiguration.INVALID_NETWORK_ID;
    }

    public WifiInfo getInfo() {
        return mInfo;
    }

    /**
     * Returns the number of levels to show for a Wifi icon, from 0 to
     * {@link WifiManager#getMaxSignalLevel()}.
     *
     * <p>Use {@link #isReachable()} to determine if an AccessPoint is in range, as this method will
     * always return at least 0.
     */
    public int getLevel() {
        return getWifiManager().calculateSignalLevel(mRssi);
    }

    public int getRssi() {
        return mRssi;
    }

    /**
     * Returns the underlying scan result set.
     *
     * <p>Callers should not modify this set.
     */
    public Set<ScanResult> getScanResults() {
        Set<ScanResult> allScans = new ArraySet<>();
        synchronized (mLock) {
            allScans.addAll(mScanResults);
            allScans.addAll(mExtraScanResults);
        }
        return allScans;
    }

    public Map<String, TimestampedScoredNetwork> getScoredNetworkCache() {
        return mScoredNetworkCache;
    }

    private void updateBestRssiInfo() {
        if (this.isActive()) {
            return;
        }

        ScanResult bestResult = null;
        int bestRssi = UNREACHABLE_RSSI;
        synchronized (mLock) {
            for (ScanResult result : mScanResults) {
                if (result.level > bestRssi) {
                    bestRssi = result.level;
                    bestResult = result;
                }
            }
        }

        // Set the rssi to the average of the current rssi and the previous rssi.
        if (bestRssi != UNREACHABLE_RSSI && mRssi != UNREACHABLE_RSSI) {
            mRssi = (mRssi + bestRssi) / 2;
        } else {
            mRssi = bestRssi;
        }

        if (bestResult != null) {
            ssid = bestResult.SSID;
            bssid = bestResult.BSSID;
            security = getSecurity(mContext, bestResult);
            if (security == SECURITY_PSK || security == SECURITY_SAE) {
                pskType = getPskType(bestResult);
            }
            if (security == SECURITY_EAP) {
                mEapType = getEapType(bestResult);
            }

            mIsPskSaeTransitionMode = AccessPoint.isPskSaeTransitionMode(bestResult);
            mIsOweTransitionMode = AccessPoint.isOweTransitionMode(bestResult);
        }
        // Update the config SSID of a Passpoint network to that of the best RSSI
        if (isPasspoint()) {
            mConfig.SSID = convertToQuotedString(ssid);
        }
    }

    public boolean isMetered() {
        return mIsScoredNetworkMetered
                || WifiConfiguration.isMetered(mConfig, mInfo);
    }

    public NetworkInfo getNetworkInfo() {
        return mNetworkInfo;
    }

    public int getSecurity() {
        return security;
    }

//    public String getSecurityString(boolean concise) {
//        Context context = mContext;
//        if (isPasspoint() || isPasspointConfig()) {
//            return concise ? context.getString(R.string.wifi_security_short_eap) :
//                    context.getString(R.string.wifi_security_eap);
//        }
//
//        if (mIsPskSaeTransitionMode) {
//            return concise ? context.getString(R.string.wifi_security_short_psk_sae) :
//                    context.getString(R.string.wifi_security_psk_sae);
//        }
//        if (mIsOweTransitionMode) {
//            return concise ? context.getString(R.string.wifi_security_short_none_owe) :
//                    context.getString(R.string.wifi_security_none_owe);
//        }
//
//        switch (security) {
//            case SECURITY_EAP:
//                switch (mEapType) {
//                    case EAP_WPA:
//                        return concise ? context.getString(R.string.wifi_security_short_eap_wpa) :
//                                context.getString(R.string.wifi_security_eap_wpa);
//                    case EAP_WPA2_WPA3:
//                        return concise
//                                ? context.getString(R.string.wifi_security_short_eap_wpa2_wpa3) :
//                                context.getString(R.string.wifi_security_eap_wpa2_wpa3);
//                    case EAP_UNKNOWN:
//                    default:
//                        return concise
//                                ? context.getString(R.string.wifi_security_short_eap) :
//                                context.getString(R.string.wifi_security_eap);
//                }
//            case SECURITY_EAP_SUITE_B:
//                return concise ? context.getString(R.string.wifi_security_short_eap_suiteb) :
//                        context.getString(R.string.wifi_security_eap_suiteb);
//            case SECURITY_PSK:
//                switch (pskType) {
//                    case PSK_WPA:
//                        return concise ? context.getString(R.string.wifi_security_short_wpa) :
//                                context.getString(R.string.wifi_security_wpa);
//                    case PSK_WPA2:
//                        return concise ? context.getString(R.string.wifi_security_short_wpa2) :
//                                context.getString(R.string.wifi_security_wpa2);
//                    case PSK_WPA_WPA2:
//                        return concise ? context.getString(R.string.wifi_security_short_wpa_wpa2) :
//                                context.getString(R.string.wifi_security_wpa_wpa2);
//                    case PSK_UNKNOWN:
//                    default:
//                        return concise ? context.getString(R.string.wifi_security_short_psk_generic)
//                                : context.getString(R.string.wifi_security_psk_generic);
//                }
//            case SECURITY_WEP:
//                return concise ? context.getString(R.string.wifi_security_short_wep) :
//                        context.getString(R.string.wifi_security_wep);
//            case SECURITY_SAE:
//                return concise ? context.getString(R.string.wifi_security_short_sae) :
//                        context.getString(R.string.wifi_security_sae);
//            case SECURITY_OWE:
//                return concise ? context.getString(R.string.wifi_security_short_owe) :
//                        context.getString(R.string.wifi_security_owe);
//            case SECURITY_NONE:
//            default:
//                return concise ? "" : context.getString(R.string.wifi_security_none);
//        }
//    }

    public String getSsidStr() {
        return ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public CharSequence getSsid() {
        return ssid;
    }

    @Deprecated
    public String getConfigName() {
        if (mConfig != null && mConfig.isPasspoint()) {
            return mConfig.providerFriendlyName;
        } else if (mPasspointUniqueId != null) {
            return mProviderFriendlyName;
        } else {
            return ssid;
        }
    }

    public DetailedState getDetailedState() {
        if (mNetworkInfo != null) {
            return mNetworkInfo.getDetailedState();
        }
        Log.w(TAG, "NetworkInfo is null, cannot return detailed state");
        return null;
    }

    public State getState() {
        if (mNetworkInfo != null) {
            return mNetworkInfo.getState();
        }
        Log.w(TAG, "NetworkInfo is null, cannot return state");
        return null;
    }

    public String getTitle() {
        if (isPasspoint()) {
            return mConfig.providerFriendlyName;
        } else if (isPasspointConfig()) {
            return mProviderFriendlyName;
        } else if (isOsuProvider()) {
            return mOsuProvider.getFriendlyName();
        } else {
            return getSsidStr();
        }
    }

    public boolean isActive() {
        return mNetworkInfo != null &&
                (networkId != WifiConfiguration.INVALID_NETWORK_ID ||
                        mNetworkInfo.getState() != State.DISCONNECTED);
    }

    public boolean isConnectable() {
        return getLevel() != -1 && getDetailedState() == null;
    }

    public boolean isEphemeral() {
        return mInfo != null && mInfo.isEphemeral() &&
                mNetworkInfo != null && mNetworkInfo.getState() != State.DISCONNECTED;
    }

    public boolean isPasspoint() {
        return mConfig != null && mConfig.isPasspoint();
    }

    public boolean isPasspointConfig() {
        return mPasspointUniqueId != null && mConfig == null;
    }

    public boolean isOsuProvider() {
        return mOsuProvider != null;
    }

    public boolean isExpired() {
        if (mSubscriptionExpirationTimeInMillis <= 0) {
            return false;
        } else {
            return System.currentTimeMillis() >= mSubscriptionExpirationTimeInMillis;
        }
    }

    public boolean isPasspointConfigurationR1() {
        return mPasspointConfigurationVersion == PasspointConfigurationVersion.NO_OSU_PROVISIONED;
    }

    public boolean isPasspointConfigurationOsuProvisioned() {
        return mPasspointConfigurationVersion == PasspointConfigurationVersion.OSU_PROVISIONED;
    }

    private boolean isInfoForThisAccessPoint(WifiConfiguration config, WifiInfo info) {
        if (info.isOsuAp() || mOsuStatus != null) {
            return (info.isOsuAp() && mOsuStatus != null);
        } else if (info.isPasspointAp() || isPasspoint()) {
            return (info.isPasspointAp() && isPasspoint()
                    && TextUtils.equals(info.getPasspointFqdn(), mConfig.FQDN)
                    && TextUtils.equals(info.getPasspointProviderFriendlyName(),
                    mConfig.providerFriendlyName));
        }

        if (networkId != WifiConfiguration.INVALID_NETWORK_ID) {
            return networkId == info.getNetworkId();
        } else if (config != null) {
            return matches(config, info);
        } else {
            return TextUtils.equals(removeDoubleQuotes(info.getSSID()), ssid);
        }
    }

    public boolean isSaved() {
        return mConfig != null;
    }

    public Object getTag() {
        return mTag;
    }

    public void setTag(Object tag) {
        mTag = tag;
    }

    public void generateOpenNetworkConfig() {
        if (!isOpenNetwork()) {
            throw new IllegalStateException();
        }
        if (mConfig != null)
            return;
        mConfig = new WifiConfiguration();
        mConfig.SSID = AccessPoint.convertToQuotedString(ssid);

        if (security == SECURITY_NONE) {
            mConfig.allowedKeyManagement.set(KeyMgmt.NONE);
        } else {
            mConfig.allowedKeyManagement.set(KeyMgmt.OWE);
            mConfig.requirePmf = true;
        }
    }

    public void saveWifiState(Bundle savedState) {
        if (ssid != null) savedState.putString(KEY_SSID, getSsidStr());
        savedState.putInt(KEY_SECURITY, security);
        savedState.putInt(KEY_SPEED, mSpeed);
        savedState.putInt(KEY_PSKTYPE, pskType);
        savedState.putInt(KEY_EAPTYPE, mEapType);
        if (mConfig != null) savedState.putParcelable(KEY_CONFIG, mConfig);
        savedState.putParcelable(KEY_WIFIINFO, mInfo);
        synchronized (mLock) {
            savedState.putParcelableArray(KEY_SCANRESULTS,
                    mScanResults.toArray(new Parcelable[mScanResults.size()
                            + mExtraScanResults.size()]));
        }
        savedState.putParcelableArrayList(KEY_SCOREDNETWORKCACHE,
                new ArrayList<>(mScoredNetworkCache.values()));
        if (mNetworkInfo != null) {
            savedState.putParcelable(KEY_NETWORKINFO, mNetworkInfo);
        }
        if (mPasspointUniqueId != null) {
            savedState.putString(KEY_PASSPOINT_UNIQUE_ID, mPasspointUniqueId);
        }
        if (mFqdn != null) {
            savedState.putString(KEY_FQDN, mFqdn);
        }
        if (mProviderFriendlyName != null) {
            savedState.putString(KEY_PROVIDER_FRIENDLY_NAME, mProviderFriendlyName);
        }
        savedState.putLong(KEY_SUBSCRIPTION_EXPIRATION_TIME_IN_MILLIS,
                mSubscriptionExpirationTimeInMillis);
        savedState.putInt(KEY_PASSPOINT_CONFIGURATION_VERSION, mPasspointConfigurationVersion);
        savedState.putBoolean(KEY_IS_PSK_SAE_TRANSITION_MODE, mIsPskSaeTransitionMode);
        savedState.putBoolean(KEY_IS_OWE_TRANSITION_MODE, mIsOweTransitionMode);
    }

    public void setListener(AccessPointListener listener) {
        mAccessPointListener = listener;
    }

    void setScanResults(Collection<ScanResult> scanResults) {
        if (CollectionUtils.isEmpty(scanResults)) {
            Log.d(TAG, "Cannot set scan results to empty list");
            return;
        }

        if (mKey != null && !isPasspoint() && !isOsuProvider()) {
            for (ScanResult result : scanResults) {
                if (!matches(result)) {
                    Log.d(TAG, String.format(
                            "ScanResult %s\nkey of %s did not match current AP key %s",
                            result, getKey(mContext, result), mKey));
                    return;
                }
            }
        }

        int oldLevel = getLevel();
        synchronized (mLock) {
            mScanResults.clear();
            mScanResults.addAll(scanResults);
        }
        updateBestRssiInfo();
        int newLevel = getLevel();

        if (newLevel > 0 && newLevel != oldLevel) {
            updateSpeed();
            ThreadUtils.postOnMainThread(() -> {
                if (mAccessPointListener != null) {
                    mAccessPointListener.onLevelChanged(this);
                }
            });

        }

        ThreadUtils.postOnMainThread(() -> {
            if (mAccessPointListener != null) {
                mAccessPointListener.onAccessPointChanged(this);
            }
        });
    }

    void setScanResultsPasspoint(
            @Nullable Collection<ScanResult> homeScans,
            @Nullable Collection<ScanResult> roamingScans) {
        synchronized (mLock) {
            mExtraScanResults.clear();
            if (!CollectionUtils.isEmpty(homeScans)) {
                mIsRoaming = false;
                if (!CollectionUtils.isEmpty(roamingScans)) {
                    mExtraScanResults.addAll(roamingScans);
                }
                setScanResults(homeScans);
            } else if (!CollectionUtils.isEmpty(roamingScans)) {
                mIsRoaming = true;
                setScanResults(roamingScans);
            }
        }
    }

    public boolean update(
            @Nullable WifiConfiguration config, WifiInfo info, NetworkInfo networkInfo) {
        boolean updated = false;
        final int oldLevel = getLevel();
        if (info != null && isInfoForThisAccessPoint(config, info)) {
            updated = (mInfo == null);
            if (!isPasspoint() && mConfig != config) {
                update(config); // Notifies the AccessPointListener of the change
            }
            if (mRssi != info.getRssi() && info.getRssi() != WifiInfo.INVALID_RSSI) {
                mRssi = info.getRssi();
                updated = true;
            } else if (mNetworkInfo != null && networkInfo != null
                    && mNetworkInfo.getDetailedState() != networkInfo.getDetailedState()) {
                updated = true;
            }
            mInfo = info;
            mNetworkInfo = networkInfo;
        } else if (mInfo != null) {
            updated = true;
            mInfo = null;
            mNetworkInfo = null;
        }
        if (updated && mAccessPointListener != null) {
            ThreadUtils.postOnMainThread(() -> {
                if (mAccessPointListener != null) {
                    mAccessPointListener.onAccessPointChanged(this);
                }
            });

            if (oldLevel != getLevel() /* current level */) {
                ThreadUtils.postOnMainThread(() -> {
                    if (mAccessPointListener != null) {
                        mAccessPointListener.onLevelChanged(this);
                    }
                });
            }
        }

        return updated;
    }

    void update(@Nullable WifiConfiguration config) {
        mConfig = config;
        if (mConfig != null && !isPasspoint()) {
            ssid = removeDoubleQuotes(mConfig.SSID);
        }
        networkId = config != null ? config.networkId : WifiConfiguration.INVALID_NETWORK_ID;
        ThreadUtils.postOnMainThread(() -> {
            if (mAccessPointListener != null) {
                mAccessPointListener.onAccessPointChanged(this);
            }
        });
    }

    public void setRssi(int rssi) {
        mRssi = rssi;
    }

    void setUnreachable() {
        setRssi(AccessPoint.UNREACHABLE_RSSI);
    }

    int getSpeed() {
        return mSpeed;
    }

    @Nullable
    String getSpeedLabel() {
        return getSpeedLabel(mSpeed);
    }

    @Nullable
    @Speed
    private static int roundToClosestSpeedEnum(int speed) {
        if (speed < Speed.SLOW) {
            return Speed.NONE;
        } else if (speed < (Speed.SLOW + Speed.MODERATE) / 2) {
            return Speed.SLOW;
        } else if (speed < (Speed.MODERATE + Speed.FAST) / 2) {
            return Speed.MODERATE;
        } else if (speed < (Speed.FAST + Speed.VERY_FAST) / 2) {
            return Speed.FAST;
        } else {
            return Speed.VERY_FAST;
        }
    }

    @Nullable
    String getSpeedLabel(@Speed int speed) {
        return getSpeedLabel(mContext, speed);
    }

    private static String getSpeedLabel(Context context, int speed) {
        switch (speed) {
            case Speed.VERY_FAST:
                return context.getString(R.string.speed_label_very_fast);
            case Speed.FAST:
                return context.getString(R.string.speed_label_fast);
            case Speed.MODERATE:
                return context.getString(R.string.speed_label_okay);
            case Speed.SLOW:
                return context.getString(R.string.speed_label_slow);
            case Speed.NONE:
            default:
                return null;
        }
    }

    @Nullable
    public static String getSpeedLabel(Context context, ScoredNetwork scoredNetwork, int rssi) {
        return getSpeedLabel(context, roundToClosestSpeedEnum(scoredNetwork.calculateBadge(rssi)));
    }

    public boolean isReachable() {
        return mRssi != UNREACHABLE_RSSI;
    }

    private static CharSequence getAppLabel(String packageName, PackageManager packageManager) {
        CharSequence appLabel = "";
        ApplicationInfo appInfo = null;
        try {
            int userId = UserHandle.getUserId(UserHandle.USER_CURRENT);
            appInfo = packageManager.getApplicationInfoAsUser(packageName, 0 /* flags */, userId);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to get app info", e);
            return appLabel;
        }
        if (appInfo != null) {
            appLabel = appInfo.loadLabel(packageManager);
        }
        return appLabel;
    }

    public static String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }

    private static int getPskType(ScanResult result) {
        boolean wpa = result.capabilities.contains("WPA-PSK");
        boolean wpa2 = result.capabilities.contains("RSN-PSK");
        boolean wpa3 = result.capabilities.contains("RSN-SAE");
        if (wpa2 && wpa) {
            return PSK_WPA_WPA2;
        } else if (wpa2) {
            return PSK_WPA2;
        } else if (wpa) {
            return PSK_WPA;
        } else {
            if (!wpa3) {
                // Suppress warning for WPA3 only networks
                Log.w(TAG, "Received abnormal flag string: " + result.capabilities);
            }
            return PSK_UNKNOWN;
        }
    }

    private static int getEapType(ScanResult result) {
        // WPA2-Enterprise and WPA3-Enterprise (non 192-bit) advertise RSN-EAP-CCMP
        if (result.capabilities.contains("RSN-EAP")) {
            return EAP_WPA2_WPA3;
        }
        // WPA-Enterprise advertises WPA-EAP-TKIP
        if (result.capabilities.contains("WPA-EAP")) {
            return EAP_WPA;
        }
        return EAP_UNKNOWN;
    }

    private static int getSecurity(Context context, ScanResult result) {
        final boolean isWep = result.capabilities.contains("WEP");
        final boolean isSae = result.capabilities.contains("SAE");
        final boolean isPsk = result.capabilities.contains("PSK");
        final boolean isEapSuiteB192 = result.capabilities.contains("EAP_SUITE_B_192");
        final boolean isEap = result.capabilities.contains("EAP");
        final boolean isOwe = result.capabilities.contains("OWE");
        final boolean isOweTransition = result.capabilities.contains("OWE_TRANSITION");

        if (isSae && isPsk) {
            final WifiManager wifiManager = (WifiManager)
                    context.getSystemService(Context.WIFI_SERVICE);
            return wifiManager.isWpa3SaeSupported() ? SECURITY_SAE : SECURITY_PSK;
        }
        if (isOweTransition) {
            final WifiManager wifiManager = (WifiManager)
                    context.getSystemService(Context.WIFI_SERVICE);
            return wifiManager.isEnhancedOpenSupported() ? SECURITY_OWE : SECURITY_NONE;
        }

        if (isWep) {
            return SECURITY_WEP;
        } else if (isSae) {
            return SECURITY_SAE;
        } else if (isPsk) {
            return SECURITY_PSK;
        } else if (isEapSuiteB192) {
            return SECURITY_EAP_SUITE_B;
        } else if (isEap) {
            return SECURITY_EAP;
        } else if (isOwe) {
            return SECURITY_OWE;
        }
        return SECURITY_NONE;
    }

    static int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(KeyMgmt.SAE)) {
            return SECURITY_SAE;
        }
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(KeyMgmt.SUITE_B_192)) {
            return SECURITY_EAP_SUITE_B;
        }
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) ||
                config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        if (config.allowedKeyManagement.get(KeyMgmt.OWE)) {
            return SECURITY_OWE;
        }
        return (config.wepTxKeyIndex >= 0
                && config.wepTxKeyIndex < config.wepKeys.length
                && config.wepKeys[config.wepTxKeyIndex] != null)
                ? SECURITY_WEP : SECURITY_NONE;
    }

    public static String securityToString(int security, int pskType) {
        if (security == SECURITY_WEP) {
            return "WEP";
        } else if (security == SECURITY_PSK) {
            if (pskType == PSK_WPA) {
                return "WPA";
            } else if (pskType == PSK_WPA2) {
                return "WPA2";
            } else if (pskType == PSK_WPA_WPA2) {
                return "WPA_WPA2";
            }
            return "PSK";
        } else if (security == SECURITY_EAP) {
            return "EAP";
        } else if (security == SECURITY_SAE) {
            return "SAE";
        } else if (security == SECURITY_EAP_SUITE_B) {
            return "SUITE_B";
        } else if (security == SECURITY_OWE) {
            return "OWE";
        }
        return "NONE";
    }

    static String removeDoubleQuotes(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        int length = string.length();
        if ((length > 1) && (string.charAt(0) == '"')
                && (string.charAt(length - 1) == '"')) {
            return string.substring(1, length - 1);
        }
        return string;
    }

    private WifiManager getWifiManager() {
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        }
        return mWifiManager;
    }

    public boolean isOpenNetwork() {
        return security == SECURITY_NONE || security == SECURITY_OWE;
    }

    public interface AccessPointListener {
        @MainThread
        void onAccessPointChanged(AccessPoint accessPoint);

        @MainThread
        void onLevelChanged(AccessPoint accessPoint);
    }

    public boolean isPskSaeTransitionMode() {
        return mIsPskSaeTransitionMode;
    }

    public boolean isOweTransitionMode() {
        return mIsOweTransitionMode;
    }

    private static boolean isPskSaeTransitionMode(ScanResult scanResult) {
        return scanResult.capabilities.contains("PSK")
                && scanResult.capabilities.contains("SAE");
    }

    private static boolean isOweTransitionMode(ScanResult scanResult) {
        return scanResult.capabilities.contains("OWE_TRANSITION");
    }

    private boolean isSameSsidOrBssid(ScanResult scanResult) {
        if (scanResult == null) {
            return false;
        }

        if (TextUtils.equals(ssid, scanResult.SSID)) {
            return true;
        } else if (scanResult.BSSID != null && TextUtils.equals(bssid, scanResult.BSSID)) {
            return true;
        }
        return false;
    }

    private boolean isSameSsidOrBssid(WifiInfo wifiInfo) {
        if (wifiInfo == null) {
            return false;
        }

        if (TextUtils.equals(ssid, removeDoubleQuotes(wifiInfo.getSSID()))) {
            return true;
        } else if (wifiInfo.getBSSID() != null && TextUtils.equals(bssid, wifiInfo.getBSSID())) {
            return true;
        }
        return false;
    }

    private boolean isSameSsidOrBssid(AccessPoint accessPoint) {
        if (accessPoint == null) {
            return false;
        }

        if (TextUtils.equals(ssid, accessPoint.getSsid())) {
            return true;
        } else if (accessPoint.getBssid() != null
                && TextUtils.equals(bssid, accessPoint.getBssid())) {
            return true;
        }
        return false;
    }
}
