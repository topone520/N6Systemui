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
 * 获取档位值.
 */
public class PGradeSoaUtil {
    /**
     * The constant instance.
     */
    public static volatile PGradeSoaUtil instance;
    /**
     * The constant TAG.
     */
    public static String TAG = "_PGradeSoaUtil";
    private ADSBus mPGradeSoaInstance = null;

    private static final String SERVICE_NAME = "SN_VEHICLE_GEARS";
    private static final int SEND_TO_SOA = 0;
    private static final String messageId = "MSG_GET_GEAR";
    /**
     * The constant isOnline.
     */
    public static boolean isOnline = false;
    /**
     * The Value.
     */
    private int value = -1;
    private static final String SN_NAME = "com.android.systemui";

    /**
     * 获取instance.
     *
     * @return 返回该类对象
     */
    public static PGradeSoaUtil getInstance() {
        if (instance == null) {
            synchronized (PGradeSoaUtil.class) {
                if (instance == null) {
                    instance = new PGradeSoaUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化.
     */
    public void init() {
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
                            mPGradeSoaInstance = adsManager.connectService(adsBusAddress);
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

    /**
     * 发送数据.
     */
    public int sendToSoa() {
        Log.i(TAG, " case" + "sendToSoa------------");
        if (mPGradeSoaInstance != null) {
            Log.i(TAG, " case" + "mPGradeSoaInstance------------");
            Bundle para = new Bundle();
            para.putString("messageId", messageId);
            ADSBusReturnValue returnValue = new ADSBusReturnValue();
            ADSBusErrorCodeEnum result = mPGradeSoaInstance.invoke(SERVICE_NAME, messageId, para, returnValue);
            Log.i(TAG, " case" + "case-----result-------" + result);
            value = returnValue.getmReturnValue().getInt("value");
        }
        return value;

    }

}
