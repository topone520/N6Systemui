package com.adayo.systemui.manager;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiClient;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Looper;
import android.os.UserManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.MutableLiveData;

import com.adayo.systemui.bases.BaseControllerImpl;
import com.adayo.systemui.bean.HotspotInfo;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.utils.ModelSystem_Device;
import com.android.systemui.SystemUIApplication;

import java.util.List;

public class HotspotControllerImpl extends BaseControllerImpl<HotspotInfo> implements HotspotController, WifiManager.SoftApCallback {
    private static String TAG = HotspotControllerImpl.class.getSimpleName();
    private final WifiStateReceiver mWifiStateReceiver = new WifiStateReceiver();
    private final ConnectivityManager mConnectivityManager;
    private final WifiManager mWifiManager;
    private boolean mRestartWifiApAfterConfigChange = false;

    private HotspotInfo hotspotInfo = new HotspotInfo();
    public MutableLiveData<Boolean> mAPSwitch = new MutableLiveData<>();

    private volatile static HotspotControllerImpl mHotspotControllerImpl;

    private final ConnectivityManager.OnStartTetheringCallback mOnStartTetheringCallback = new ConnectivityManager.OnStartTetheringCallback() {

    };

    private HotspotControllerImpl() {
        mConnectivityManager = (ConnectivityManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifiManager = (WifiManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WIFI_SERVICE);
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);

        mWifiManager.registerSoftApCallback(new HandlerExecutor(mHandler), new WifiManager.SoftApCallback() {
            @Override
            public void onStateChanged(int state, int failureReason) {
                LogUtil.i(TAG + String.format("onStateChanged state=%1s , failureReason=%2s", state, failureReason));
                mAPSwitch.postValue(state == WifiManager.WIFI_AP_STATE_ENABLED || state == WifiManager.WIFI_AP_STATE_ENABLING);
                if (mRestartWifiApAfterConfigChange && state == WifiManager.WIFI_AP_STATE_DISABLED) {
                    startTethering();
                    mRestartWifiApAfterConfigChange = false;
                }
            }

            @Override
            public void onConnectedClientsChanged(@NonNull List<WifiClient> clients) {
                LogUtil.i(TAG + "onConnectedClientsChanged clients=" + clients);
            }
        });
    }

    public static HotspotControllerImpl getInstance() {
        if (mHotspotControllerImpl == null) {
            synchronized (HotspotControllerImpl.class) {
                if (mHotspotControllerImpl == null) {
                    mHotspotControllerImpl = new HotspotControllerImpl();
                }
            }
        }
        return mHotspotControllerImpl;
    }

    @Override
    public boolean isHotspotSupported() {
        return mConnectivityManager.isTetheringSupported() && mConnectivityManager.getTetherableWifiRegexs().length != 0 && UserManager.get(SystemUIApplication.getSystemUIContext()).isUserAdmin(ActivityManager.getCurrentUser());
    }

    /**
     * Updates the wifi state receiver to either start or stop listening to get updates to the
     * hotspot status. Additionally starts listening to wifi manager state to track the number of
     * connected devices.
     *
     * @param shouldListen whether we should start listening to various wifi statuses
     */
    private void updateWifiStateListeners(boolean shouldListen) {
        mWifiStateReceiver.setListening(shouldListen);
        if (shouldListen) {
            mWifiManager.registerSoftApCallback(new HandlerExecutor(new Handler(Looper.getMainLooper())), this);
        } else {
            mWifiManager.unregisterSoftApCallback(this);
        }
    }

    @Override
    public boolean isHotspotEnabled() {
        return hotspotInfo.getmHotspotState() == WifiManager.WIFI_AP_STATE_ENABLED;
    }

    @Override
    public boolean isHotspotTransient() {
        return hotspotInfo.ismWaitingForCallback() || (hotspotInfo.getmHotspotState() == WifiManager.WIFI_AP_STATE_ENABLING);
    }

    private OnStartTetheringCallback myOnStartTetheringCallback;

    @Override
    public void setHotspotEnabled(boolean enabled) {
        if (hotspotInfo.ismWaitingForCallback()) {
            LogUtil.d("Ignoring setHotspotEnabled; waiting for callback.");
            return;
        }
        if (enabled) {
            if (null == myOnStartTetheringCallback) {
                myOnStartTetheringCallback = new OnStartTetheringCallback();
            }
            hotspotInfo.setmWaitingForCallback(true);
            LogUtil.d("Starting tethering");
            mConnectivityManager.startTethering(ConnectivityManager.TETHERING_WIFI, true, myOnStartTetheringCallback);
        } else {
            mConnectivityManager.stopTethering(ConnectivityManager.TETHERING_WIFI);
        }
    }

    @Override
    public int getNumConnectedDevices() {
        return hotspotInfo.getmNumConnectedDevices();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public String getHotspotName() {
        SoftApConfiguration apConfiguration = mWifiManager.getSoftApConfiguration();
        if (apConfiguration != null) {
            return apConfiguration.getSsid();
        }
        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public String getHotspotPassword() {
        SoftApConfiguration apConfiguration = mWifiManager.getSoftApConfiguration();
        if (apConfiguration != null) {
            return apConfiguration.getPassphrase();
        }
        return "";
    }

    @Override
    public int getHotspotAPMode() {
        SoftApConfiguration apConfiguration = mWifiManager.getSoftApConfiguration();
        if (apConfiguration != null) {
            return apConfiguration.getBand();
        }
        return 0;
    }

    public void startTethering() {
        reqMaxClient(8);
        mConnectivityManager.startTethering(ConnectivityManager.TETHERING_WIFI, true, mOnStartTetheringCallback, new Handler(Looper.getMainLooper()));
    }

    public void reqMaxClient(int max) {
        LogUtil.i(TAG + "call reqMaxClient max=" + max);
        SoftApConfiguration config = mWifiManager.getSoftApConfiguration();
        SoftApConfiguration apConfiguration = new SoftApConfiguration.Builder(config).setMaxNumberOfClients(max).build();
        setConfig(apConfiguration);
    }


    /**
     * 1 2.4GHz
     * 2 5GHz
     * 3 混合模式
     */
    public void reqAPMode(int wlanMode) {
        LogUtil.i("call reqAPMode wlanMode=" + wlanMode);
        SoftApConfiguration config = mWifiManager.getSoftApConfiguration();
        SoftApConfiguration apConfiguration = new SoftApConfiguration.Builder(config).setBand(wlanMode).build();
        setConfig(apConfiguration);
    }

    private void setConfig(SoftApConfiguration apConfiguration) {
        if (mWifiManager.getWifiApState() == WifiManager.WIFI_AP_STATE_ENABLED) {
            mRestartWifiApAfterConfigChange = true;
            stopTethering();
        }
        mWifiManager.setSoftApConfiguration(apConfiguration);
    }

    public void stopTethering() {
        mConnectivityManager.stopTethering(ConnectivityManager.TETHERING_WIFI);
        ModelSystem_Device.getInstance().stopAp();
//        mConnectivityManager.stopTethering(ConnectivityManager.TETHERING_WIFI);
    }

    /**
     * Sends a hotspot changed callback with the new enabled status. Wraps
     * {@link #fireHotspotChangedCallback(boolean, int)} and assumes that the number of devices has
     * not changed.
     *
     * @param enabled whether the hotspot is enabled
     */
    private void fireHotspotChangedCallback(boolean enabled) {
        LogUtil.d("mNumConnectedDevices " + hotspotInfo.getmNumConnectedDevices());
        mHandler.removeMessages(NOTIFY_CALLBACKS);
        mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
    }

    /**
     * Sends a hotspot changed callback with the new enabled status & the number of devices
     * connected to the hotspot. Be careful when calling over multiple threads, especially if one of
     * them is the main thread (as it can be blocked).
     *
     * @param enabled             whether the hotspot is enabled
     * @param numConnectedDevices number of devices connected to the hotspot
     */
    private void fireHotspotChangedCallback(boolean enabled, int numConnectedDevices) {
        mHandler.removeMessages(NOTIFY_CALLBACKS);
        mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
    }

    private void fireHotspotChangedCallback() {
        mHandler.removeMessages(NOTIFY_CALLBACKS);
        mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
    }

    @Override
    public void onStateChanged(int state, int failureReason) {
        LogUtil.d("state = " + state + " ; failureReason = " + failureReason);
        // Do nothing - we don't care about changing anything here.
    }

    @Override
    public void onConnectedClientsChanged(@NonNull List<WifiClient> clients) {
//    public void onConnectedClientsChanged(int numConnectedDevices) {
        if (null == clients) {
            return;
        }
        LogUtil.d("numConnectedDevices " + clients.size());
        hotspotInfo.setmNumConnectedDevices(clients.size());
        fireHotspotChangedCallback();
    }

    @Override
    protected boolean registerListener() {
        updateWifiStateListeners(true);
        return true;
    }

    @Override
    protected HotspotInfo getDataInfo() {
        return hotspotInfo;
    }

    private final class OnStartTetheringCallback extends ConnectivityManager.OnStartTetheringCallback {
        @Override
        public void onTetheringStarted() {
            LogUtil.d("onTetheringStarted");
            // Don't fire a callback here, instead wait for the next update from wifi.
        }


        @Override
        public void onTetheringFailed() {
            LogUtil.d("onTetheringFailed");
            hotspotInfo.setmWaitingForCallback(false);
            fireHotspotChangedCallback(isHotspotEnabled());
            // TODO: Show error.
        }
    }

    /**
     * Class to listen in on wifi state and update the hotspot state
     */
    private final class WifiStateReceiver extends BroadcastReceiver {
        private boolean mRegistered;

        public void setListening(boolean listening) {
            if (listening && !mRegistered) {
                LogUtil.d("Registering receiver");
                final IntentFilter filter = new IntentFilter();
                filter.addAction(WifiManager.WIFI_AP_STATE_CHANGED_ACTION);
                SystemUIApplication.getSystemUIContext().registerReceiver(this, filter);
                mRegistered = true;
            } else if (!listening && mRegistered) {
                LogUtil.d("Unregistering receiver");
                SystemUIApplication.getSystemUIContext().unregisterReceiver(this);
                mRegistered = false;
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_AP_STATE, WifiManager.WIFI_AP_STATE_FAILED);
            LogUtil.d("onReceive " + state);
            if (state == WifiManager.WIFI_AP_STATE_ENABLED || state == WifiManager.WIFI_AP_STATE_DISABLED) {
                hotspotInfo.setmHotspotState(state);
                if (!isHotspotEnabled()) {
                    hotspotInfo.setmNumConnectedDevices(0);
                }
                fireHotspotChangedCallback(isHotspotEnabled());
                hotspotInfo.setmWaitingForCallback(false);
            } else if (state == WifiManager.WIFI_AP_STATE_FAILED) {
                hotspotInfo.setmWaitingForCallback(false);
            }
        }
    }
}
