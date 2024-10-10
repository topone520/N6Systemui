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
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Color egg soa util.
 */
public class ColorEggSoaUtil {
    /**
     * The constant instance.
     */
    public static volatile ColorEggSoaUtil instance;
    /**
     * The constant TAG.
     */
    public static String TAG = "_ColorEggSoaUtil";
    private ADSBus mColorEggSoaInstance = null;

    private static final String SERVICE_NAME = "SN_CLUSTER_ADAPTER_SERVICE";
    private static final int SEND_TO_SOA = 0;
    private String messageId = "";
    /**
     * The constant isOnline.
     */
    public static boolean isOnline = false;
    /**
     * The Value.
     */
    int value = -1;
    private static final String SN_NAME = "com.android.systemui";

    /**
     * 获取instance.
     *
     * @return 返回该类对象
     */
    public static ColorEggSoaUtil getInstance() {
        if (instance == null) {
            synchronized (ColorEggSoaUtil.class) {
                if (instance == null) {
                    instance = new ColorEggSoaUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化.
     *
     * @param mMessageId message id
     */
    public void init(String mMessageId) {
        messageId = mMessageId;
        ADSManager adsManager = new ADSManager();
        ADSBusErrorCodeEnum codeEnum = adsManager.findService(SN_NAME, SERVICE_NAME, new IADSBusClientObserver() {
            @Override
            public void onOnline(List<ADSBusAddress> list) {
                isOnline = true;
                for (ADSBusAddress adsBusAddress : list) {
                    Log.i(TAG, " onOnline data:" + adsBusAddress.getState());
                    Log.i(TAG, "adsBusAddress.getState()" + adsBusAddress.getState());
                    if (adsBusAddress.getState() == ADSBusAddress.SERVICE_STATE_ONLINE) {
                        Log.i(TAG, "adsBusAddress.getServiceName()" + adsBusAddress.getServiceName());
                        if (adsBusAddress.getServiceName().equals(SERVICE_NAME)) {
                            mColorEggSoaInstance = adsManager.connectService(adsBusAddress);
                        }
                    }
                }
            }

            @Override
            public void onOffline(List<ADSBusAddress> list) {
                isOnline = false;
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

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.i(TAG, " case" + "handleMessage------------");
            if (msg.what == SEND_TO_SOA) {
//               sendToSoa();
            }
        }
    };

    /**
     * 发送数据.
     *
     * @param videoPath 本地视频路径
     */
    public void sendToSoa(String videoPath) {
        Log.i(TAG, " case" + "sendToSoa------------");
        if (mColorEggSoaInstance != null) {
            Log.i(TAG, " case" + "mColorEggSoaInstance------------");
            Bundle para = new Bundle();
            para.putString("messageId", messageId);
//            para.putInt("status", 1);
            para.putString("ipvideo", "advideo1.mp4");
            para.putString("ivifirstvideo", "advideo2.mp4");
            para.putString("ivisecondvideo", "advideo3.mp4");
            para.putString("audio", "music1.wav");
            ADSBusReturnValue returnValue = new ADSBusReturnValue();
            ADSBusErrorCodeEnum result = mColorEggSoaInstance.invoke(SERVICE_NAME, messageId, para, returnValue);
            Log.i(TAG, " case" + "case-----result-------" + result);
            for (String key : returnValue.getmReturnValue().keySet()) {
                Log.i(TAG, "festivalScene() 请求结果 遍历 onEvent key:" + key + "--value:" + returnValue.getmReturnValue().getString(key));
            }
        }

    }

}
