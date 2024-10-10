/*
 * Copyright (C) 2017 The Android Open Source Project
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


import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import java.util.ArrayList;
import java.util.List;

public class CarWifiManager implements WifiTracker.WifiListener {
    private final String TAG = CarWifiManager.class.getName();

    private final Context mContext;
    private final List<Listener> mListeners = new ArrayList<>();
    private boolean mStarted;

    private final WifiTracker mWifiTracker;
    private final WifiManager mWifiManager;

    public interface Listener {
        void onAccessPointsChanged();
    }

    public CarWifiManager(Context context) {
        mContext = context;
        mWifiManager = mContext.getSystemService(WifiManager.class);
        mWifiTracker = new WifiTracker(context, this);
    }

    public boolean addListener(Listener listener) {
        return mListeners.add(listener);
    }

    public boolean removeListener(Listener listener) {
        return mListeners.remove(listener);
    }

    @UiThread
    public void start() {
        if (!mStarted) {
            mStarted = true;
            mWifiTracker.onStart();
        }
    }

    @UiThread
    public void stop() {
        if (mStarted) {
            mStarted = false;
            mWifiTracker.onStop();
        }
    }

    @UiThread
    public void destroy() {
        mWifiTracker.onDestroy();
    }

    public WifiInfo getConnectionInfo(){
        return mWifiManager.getConnectionInfo();
    }

    public List<WifiConfiguration> getConfiguredNetworks() {
        return mWifiManager.getConfiguredNetworks();
    }

    public void updateNetworkInfo(NetworkInfo networkInfo){
        mWifiTracker.updateNetworkInfo(networkInfo);
    }

    public List<AccessPoint> getAllAccessPoints() {
        return getAccessPoints(false);
    }

    public List<AccessPoint> getSavedAccessPoints() {
        return getAccessPoints(true);
    }

    private List<AccessPoint> getAccessPoints(boolean saved) {
        LogUtil.i("getAccessPoints saved = " + saved);
        List<AccessPoint> accessPoints = new ArrayList<AccessPoint>();
        if (mWifiManager.isWifiEnabled()) {
            for (AccessPoint accessPoint : mWifiTracker.getAccessPoints()) {
                if (shouldIncludeAp(accessPoint, saved)) {
                    accessPoints.add(accessPoint);
                }
            }
        }
        return accessPoints;
    }

    private boolean shouldIncludeAp(AccessPoint accessPoint, boolean saved) {
        return saved ? accessPoint.isReachable() && accessPoint.isSaved()
                : accessPoint.isReachable();
    }

    @Nullable
    public AccessPoint getConnectedAccessPoint() {
        for (AccessPoint accessPoint : getAllAccessPoints()) {
            if (accessPoint.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                return accessPoint;
            }
        }
        return null;
    }

    public boolean isWifiEnabled() {
        return mWifiManager.isWifiEnabled();
    }

    public int getWifiState() {
        return mWifiManager.getWifiState();
    }

    public boolean setWifiEnabled(boolean enabled) {
        LogUtil.i("enabled = " + enabled);
        return mWifiManager.setWifiEnabled(enabled);
    }

    public void connectToPublicWifi(AccessPoint accessPoint, WifiManager.ActionListener listener) {
        accessPoint.generateOpenNetworkConfig();
        LogUtil.i("connectToPublicWifi--" + accessPoint.getSsid());
        mWifiManager.connect(accessPoint.getConfig(), listener);
    }

    public void connectToSavedWifi(AccessPoint accessPoint, WifiManager.ActionListener listener) {
        if (accessPoint.isSaved()) {
            LogUtil.i("connectToSavedWifi--" + accessPoint.getSsid());
            mWifiManager.connect(accessPoint.getConfig(), listener);
        }
    }

    @Override
    public void onConnectedChanged() {

    }

    @Override
    public void onAccessPointsChanged() {
        for (Listener listener : mListeners) {
            listener.onAccessPointsChanged();
        }
    }

    public void disconnectWifi() {
        mWifiManager.disconnect();
    }
}
