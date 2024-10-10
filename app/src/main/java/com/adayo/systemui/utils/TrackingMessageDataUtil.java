package com.adayo.systemui.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBus;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusErrorCodeEnum;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSManager;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.IADSBusClientObserver;
import com.adayo.proxy.system.aaop_systemservice.soa.data.ADSBusAddress;

import java.util.List;

/**
 * The type Color egg soa util.
 */
public class TrackingMessageDataUtil {
    /**
     * The constant instance.
     */
    public static volatile TrackingMessageDataUtil instance;
    /**
     * The constant TAG.
     */
    public static String TAG = "_TrackingMessageDataUtil";
    private ADSBus mTrackingMessageDataInstance = null;

    private static final String SERVICE_NAME = "SN_DATA_REPORT";
    private static final String SN_NAME = "com.android.systemui";

    private static final String MESSAGE_ID = "MSG_POST_DATA_REPORT";

    /**
     * 获取instance.
     *
     * @return 返回该类对象
     */
    public static TrackingMessageDataUtil getInstance() {
        if (instance == null) {
            synchronized (TrackingMessageDataUtil.class) {
                if (instance == null) {
                    instance = new TrackingMessageDataUtil();
                }
            }
        }
        return instance;
    }

    public void trackingData(String behaviorid) {
        ADSManager adsManager = new ADSManager();
        ADSBusErrorCodeEnum codeEnum = adsManager.findService(SN_NAME, SERVICE_NAME, new IADSBusClientObserver() {
            @Override
            public void onOnline(List<ADSBusAddress> list) {
                for (ADSBusAddress adsBusAddress : list) {
                    Log.i(TAG, " onOnline data:" + adsBusAddress.getState());
                    Log.i(TAG, "adsBusAddress.getState()" + adsBusAddress.getState());
                    if (adsBusAddress.getState() == ADSBusAddress.SERVICE_STATE_ONLINE) {
                        Log.i(TAG, "adsBusAddress.getServiceName()" + adsBusAddress.getServiceName());
                        if (adsBusAddress.getServiceName().equals(SERVICE_NAME)) {
                            mTrackingMessageDataInstance = adsManager.connectService(adsBusAddress);
                            sendData(behaviorid);
                        }
                    }
                }
            }

            @Override
            public void onOffline(List<ADSBusAddress> list) {
                for (ADSBusAddress adsBusAddress : list) {
                    Log.i(TAG, " onOffline data:" + adsBusAddress.getState());
                }
            }
        });
        Log.i(TAG, "codeEnum" + codeEnum);
        if (codeEnum == ADSBusErrorCodeEnum.ADSBUS_ERR_SUCCESS) {
            Log.i(TAG, "find success");
        }
    }


    public void sendData(String behaviorid) {
        Log.i(TAG, " case" + "sendToSoa------------");
        if (mTrackingMessageDataInstance != null) {
            Log.i(TAG, " case" + "mTrackingMessageDataInstance------------");
            Bundle para = new Bundle();
            para.putString("behaviorid", behaviorid);
            para.putString("interactiveway", "");
            para.putString("reportData", "");
            para.putString("cpSource", "");
            ADSBusReturnValue returnValue = new ADSBusReturnValue();
            ADSBusErrorCodeEnum result = mTrackingMessageDataInstance.invoke(SERVICE_NAME, MESSAGE_ID, para, returnValue);
            int value = returnValue.getmReturnValue().getInt("code");
            Log.i(TAG, " case" + "case-----value-------" + value);
            Log.i(TAG, " case" + "case-----result-------" + result);

        }

    }

}
