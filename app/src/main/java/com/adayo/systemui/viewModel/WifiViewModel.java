package com.adayo.systemui.viewModel;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.adayo.systemui.bean.WifiErrorMsg;
import com.adayo.systemui.utils.AccessPoint;
import com.adayo.systemui.utils.CarWifiManager;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.utils.WifiUtil;
import com.android.systemui.SystemUIApplication;

import java.util.ArrayList;
import java.util.List;

public class WifiViewModel extends AndroidViewModel implements CarWifiManager.Listener {
    private static final String TAG = WifiViewModel.class.getSimpleName();
    private final Context mContext = SystemUIApplication.getSystemUIContext();
    private final CarWifiManager mCarWifiManager;
    private final MutableLiveData<WifiErrorMsg> mConnectError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mWifiState = new MutableLiveData<>();
    private final MutableLiveData<List<AccessPoint>> mWifiScanList = new MutableLiveData<>();
    private final MutableLiveData<AccessPoint> mConnectedAccessPoint = new MutableLiveData<>();
    private final MutableLiveData<AccessPoint> mOutConnectedAP = new MutableLiveData<>();
    private final List<AccessPoint> mLastReqConnectAP = new ArrayList<>();

    private final WifiManager.ActionListener mConnectionListener = new WifiManager.ActionListener() {
        @Override
        public void onSuccess() {
            LogUtil.i("ActionListener onSuccess--");
        }

        @Override
        public void onFailure(int i) {
            LogUtil.i("ActionListener onFailure reason=" + i);
        }
    };

    {
        mCarWifiManager = new CarWifiManager(mContext);
        mCarWifiManager.addListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    String action = intent.getAction();
                    if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                        if (intent.getExtras() != null && intent.getExtras().containsKey(WifiManager.EXTRA_SUPPLICANT_ERROR)) {
                            int supplicantError = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
                            LogUtil.i("SUPPLICANT_STATE_CHANGED_ACTION  supplicantError=" + supplicantError);
                            if (supplicantError == WifiManager.ERROR_AUTHENTICATING) {
                                if (!mLastReqConnectAP.isEmpty()) {
                                    AccessPoint lastAP = mLastReqConnectAP.get(0);
                                    mConnectError.postValue(new WifiErrorMsg(lastAP, WifiManager.ERROR_AUTHENTICATING));
                                    reqRemoveWifi(lastAP);
                                }
                            }
                        }
                    }
                    if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                        boolean connected = networkInfo != null && networkInfo.isConnected();
                        if (connected) {
                            WifiInfo info = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                            if (info == null) {
                                info = mCarWifiManager.getConnectionInfo();
                            }
                            WifiConfiguration configuration = getWifiConfigurationForNetworkId(info, mCarWifiManager.getConfiguredNetworks());
                            AccessPoint accessPoint = new AccessPoint(mContext, configuration);
                            accessPoint.setRssi(info.getRssi());
                            mOutConnectedAP.postValue(accessPoint);
                        } else {
                            mOutConnectedAP.postValue(null);
                        }
                    }

                    if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                        onWifiStateChanged(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
                    }
                }catch (Exception e){
                    LogUtil.e("  mContext.registerReceiver " + e.getMessage());
                }
            }
        }, filter);
    }

    public WifiViewModel(@NonNull Application application) {
        super(application);
    }

    public CarWifiManager getWifiManager() {
        return mCarWifiManager;
    }

    public AccessPoint getConnectedAP() {
        AccessPoint ap = mCarWifiManager.getConnectedAccessPoint();
        LogUtil.i("getConnectedAP = " + ap);
        return ap;
    }

    public void reqWifiEnabled(boolean state) {
        LogUtil.i("reqWifiEnabled = " + state);
        mCarWifiManager.setWifiEnabled(state);
    }

    public boolean isWifiEnabled() {
        LogUtil.i("isWifiEnabled = " + mCarWifiManager.isWifiEnabled());
        return mCarWifiManager.isWifiEnabled();
    }

    public void reqStartScan() {
        LogUtil.i("reqStartScan");
        mCarWifiManager.start();
    }

    public void reqStopScan() {
        LogUtil.i("reqStopScan");
        mCarWifiManager.stop();
    }

    public void reqConnectWifi(AccessPoint accessPoint, String password, boolean supplicantError) {
        mLastReqConnectAP.add(accessPoint);
        boolean isAccessPointDisabledByWrongPassword = WifiUtil.isAccessPointDisabledByWrongPassword(accessPoint);
        LogUtil.i("reqConnectWifi -> ssid=" + accessPoint.getSsid() + "--isAccessPointDisabledByWrongPassword=" + isAccessPointDisabledByWrongPassword);
        if (WifiUtil.isOpenNetwork(accessPoint.getSecurity()) && !accessPoint.isSaved() && !accessPoint.isActive()) {
            LogUtil.i("reqConnectWifi -> if");
            mCarWifiManager.connectToPublicWifi(accessPoint, mConnectionListener);
        } else if (accessPoint.isSaved() && !WifiUtil.isAccessPointDisabledByWrongPassword(accessPoint) && !supplicantError) {
            LogUtil.i("reqConnectWifi -> else if");
            mCarWifiManager.connectToSavedWifi(accessPoint, mConnectionListener);
        } else {
            LogUtil.i("reqConnectWifi -> else");
            WifiUtil.connectToAccessPoint(mContext, accessPoint.getSsid().toString(), accessPoint.getSecurity(), password, false, mConnectionListener);
        }
    }

    public void reqDisConnect() {
        LogUtil.i("reqDisConnect");
        mCarWifiManager.disconnectWifi();
    }

    public void reqRemoveWifi(@NonNull AccessPoint accessPoint) {
        LogUtil.i("reqRemoveWifi -> ssid=" + accessPoint.getSsid());
        WifiUtil.forget(mContext, accessPoint);
        mLastReqConnectAP.clear();
    }

    public MutableLiveData<Boolean> getWifiState() {
        if (mWifiState.getValue() == null) {
            mWifiState.setValue(isWifiEnabled());
        }
        LogUtil.i("getWifiState=" + mWifiState.getValue());
        return mWifiState;
    }

    public MutableLiveData<WifiErrorMsg> getConnectError() {
        LogUtil.i("getConnectError");
        return mConnectError;
    }

    public MutableLiveData<AccessPoint> getConnectedAccessPoint() {
        WifiInfo connectionInfo = mCarWifiManager.getConnectionInfo();
        LogUtil.i("WLAN ERROR getConnectionInfo=" + connectionInfo);
        AccessPoint connectedAccessPoint = mCarWifiManager.getConnectedAccessPoint();
        mConnectedAccessPoint.setValue(connectedAccessPoint);
        LogUtil.i("WLAN ERROR getConnectedAccessPoint=" + connectedAccessPoint);
        return mConnectedAccessPoint;
    }

    public MutableLiveData<AccessPoint> getOutConnectedAP() {
        LogUtil.i("getOutConnectedAP=" + mOutConnectedAP.getValue());
        return mOutConnectedAP;
    }

    public MutableLiveData<List<AccessPoint>> getWifiScanList() {
        if (mWifiScanList.getValue() == null) {
            AccessPoint connectedAccessPoint = mCarWifiManager.getConnectedAccessPoint();
            List<AccessPoint> allAccessPoints = mCarWifiManager.getAllAccessPoints();
            if (connectedAccessPoint != null) {
                allAccessPoints.remove(connectedAccessPoint);
            }
        }
        LogUtil.i("getWifiScanList=" + mWifiScanList.getValue());
        return mWifiScanList;
    }

    @Override
    public void onAccessPointsChanged() {
        AccessPoint connectedAccessPoint = mCarWifiManager.getConnectedAccessPoint();
        LogUtil.i("onAccessPointsChanged connectedAccessPoint = " + connectedAccessPoint);
        if (connectedAccessPoint != null && !mLastReqConnectAP.isEmpty()){
            if (connectedAccessPoint.getSsidStr().equals(mLastReqConnectAP.get(0).getSsidStr())){
                mLastReqConnectAP.clear();
            }
        }
        mConnectedAccessPoint.postValue(connectedAccessPoint);
        List<AccessPoint> allAccessPoints = mCarWifiManager.getAllAccessPoints();
        LogUtil.i("SAVE LIST = " + mCarWifiManager.getSavedAccessPoints() + " ALL = " + allAccessPoints);
//        if (connectedAccessPoint != null) {
//            allAccessPoints.remove(connectedAccessPoint);
//        }
        mWifiScanList.setValue(allAccessPoints);
    }

    private WifiConfiguration getWifiConfigurationForNetworkId(WifiInfo wifiInfo, final List<WifiConfiguration> configs) {
        if (configs != null) {
            for (WifiConfiguration config : configs) {
                if (wifiInfo != null && wifiInfo.getNetworkId() == config.networkId) {
                    return config;
                }
            }
        }
        return null;
    }

    public void onWifiStateChanged(int state) {
        LogUtil.i("onWifiStateChanged=" + state);
        switch (state) {
            case WifiManager.WIFI_STATE_ENABLED:
//            case WifiManager.WIFI_STATE_ENABLING:
                mWifiState.setValue(true);
                break;
//            case WifiManager.WIFI_STATE_DISABLING:
            case WifiManager.WIFI_STATE_DISABLED:
            case WifiManager.WIFI_STATE_UNKNOWN:
                mWifiState.setValue(false);
                mOutConnectedAP.postValue(null);
                break;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        LogUtil.i("call onCleared");
        mCarWifiManager.removeListener(this);
        mCarWifiManager.stop();
        mCarWifiManager.destroy();
    }
}
