package com.adayo.systemui.manager;

import android.annotation.SuppressLint;
import android.annotation.TestApi;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiClient;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserManager;

import androidx.annotation.NonNull;

import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.deviceservice.IDeviceFuncCallBack;
import com.adayo.systemui.bases.BaseControllerImpl;
import com.adayo.systemui.bean.HotspotInfo;
import com.adayo.systemui.bean.SignalInfo;
import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.SystemUIApplication;

import java.util.List;

public class SignalControllerImpl extends BaseControllerImpl<SignalInfo> implements SignalController {


    private SignalInfo signalInfo = new SignalInfo();

    private volatile static SignalControllerImpl mSignalControllerImpl;

    private SignalControllerImpl() {
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);
    }

    public static SignalControllerImpl getInstance() {
        if (mSignalControllerImpl == null) {
            synchronized (SignalControllerImpl.class) {
                if (mSignalControllerImpl == null) {
                    mSignalControllerImpl = new SignalControllerImpl();
                }
            }
        }
        return mSignalControllerImpl;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = (Bundle) msg.obj;
            if (bundle.containsKey("SignalQuality")) {
                int signalQuality = bundle.getInt("SignalQuality");//从0~100%代表网络信号强度
                LogUtil.d("signalQuality = " + signalQuality);
                signalInfo.setSignalLevel(signalQuality);
            }
            if(bundle.containsKey("WanConnInfo")) {
                int signalType = bundle.getInt("WanConnInfo");//0：NoNetwork 1：Connecting 2：2G在线 3：3G在线 4：4G在线 5：5G在线
                LogUtil.d("signalType = " + signalType);
                signalInfo.setSignalType(signalType);
            }
            if(bundle.containsKey("DataSwitch")) {
                int dataSwitch = bundle.getInt("DataSwitch");//0：关闭数据通信 1：开启数据通信
                LogUtil.d("dataSwitch = " + dataSwitch);
                signalInfo.setSignalSwitchState(dataSwitch == 1);
            }
//            if(bundle.containsKey("CallType")) {
//                int CallType = bundle.getInt("CallType");//5 和 0：空闲 其他都是在打电话。
//            }
            LogUtil.d("update signal = " + signalInfo.toString());
            mHandler.removeMessages(NOTIFY_CALLBACKS);
            mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
        }
    };

    @Override
    protected boolean registerListener() {
        //获取移动数据开关
//        Bundle paramSwitch = AAOP_DeviceServiceManager.getInstance().getDeviceDataUniversalInterface("TBoxDevice");
//        int wlanSwitch = paramSwitch.getInt("DataSwitch");//1表示开启移动数据,0表示关闭移动数据
//        signalInfo.setSignalSwitchState(wlanSwitch == 1);
//        //获取网络类型（2G,3G,4G,）
//        Bundle paramType = AAOP_DeviceServiceManager.getInstance().getDeviceDataUniversalInterface("TBoxDevice");
//        int wanConnInfo = paramType.getInt("WanConnInfo");//0：NoNetwork 1：Connecting 2：2G在线 3：3G在线 4：4G在线 5：5G在线
//        signalInfo.setSignalType(wanConnInfo);
//        //获取网络类型（2G,3G,4G,）
//        Bundle paramLevel = AAOP_DeviceServiceManager.getInstance().getDeviceDataUniversalInterface("TBoxDevice");
//        int signalQuality = paramLevel.getInt("SignalQuality");//从0~100%代表网络信号强度
//        signalInfo.setSignalLevel(signalQuality);
//        LogUtil.d("wlanSwitch = " + wlanSwitch + ", wanConnInfo = " + wanConnInfo + ", signalQuality = " + signalQuality);
//        LogUtil.d("init signal = " + signalInfo.toString());
        //热点信息和4G网络信息注册监听方法
        LogUtil.d("SignalControllerImpl----registerListener");
        int result = AAOP_DeviceServiceManager.getInstance().registerDeviceFuncListener(new IDeviceFuncCallBack.Stub() {
            @Override
            public int onChangeListener(Bundle bundle) throws RemoteException {
                LogUtil.d("onChangeListener");
                Message message = handler.obtainMessage();
                message.obj = bundle;
                handler.sendMessage(message);
                return 0;
            }
        }, "com.android.systemui", "TBoxDevice");
        return result == -1000;
    }

    @Override
    protected SignalInfo getDataInfo() {
        return signalInfo;
    }

    @Override
    public boolean isSignalEnabled() {
        return signalInfo.getSignalSwitchState();
    }

    @Override
    public void setSignalEnabled(boolean enabled) {
        //设置移动数据开关
        Bundle param = new Bundle();
        param.putInt("DataSwitch", enabled ? 1 : 0); //1表示开启移动数据,0表示关闭移动数据
        AAOP_DeviceServiceManager.getInstance().setDeviceFuncUniversalInterface("TBoxDevice", "setDataSwitch", param);
        LogUtil.d("setSignalEnabled---" + enabled);
    }

    @Override
    public int getSignalLevel() {
        return signalInfo.getSignalLevel();
    }

    @Override
    public int getSignalType() {
        return signalInfo.getSignalType();
    }

}
