package com.adayo.systemui.utils;

import android.content.Context;
import android.os.Bundle;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBus;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusErrorCodeEnum;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSManager;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.IADSBusClientObserver;
import com.adayo.proxy.system.aaop_systemservice.soa.data.ADSBusAddress;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;

import java.util.List;

public class ModelSystem_Device {
    public static final String TAG = "ModelSystem_Device";
    public static ModelSystem_Device INSTANCE;
    private Context mContext;
    public ADSBus mDeviceInstance = null;
    private ADSManager mADSManager = null;


    public static ModelSystem_Device getInstance() {
        if (null == INSTANCE) {
            synchronized (ModelSystem_Device.class) {
                if (null == INSTANCE) {
                    INSTANCE = new ModelSystem_Device();
                }
            }
        }
        return INSTANCE;
    }

    public void setContext(Context context) {
        mContext = context;
        mADSManager = new ADSManager();
        initDeviceManager();
    }

    public void initDeviceManager() {
        ADSBusErrorCodeEnum code = mADSManager.findService("CTER_AAOP_DEVICE", "SN_SYSTEM", mIADSBusClientObserver);
        AAOP_LogUtils.i(TAG, "initDeviceManager code = " + code);
    }

    public IADSBusClientObserver mIADSBusClientObserver = new IADSBusClientObserver() {

        //find服务上线通知
        @Override
        public void onOnline(List<ADSBusAddress> address) {
            AAOP_LogUtils.i(TAG, "SN_SYSTEM onOnline");
            if (address.size() > 0) {
                for (ADSBusAddress data : address) {
                    AAOP_LogUtils.i(TAG, "SN_SYSTEM getServiceName = " + data.getServiceName());
                    if (data.getState() == ADSBusAddress.SERVICE_STATE_ONLINE) {
                        if (data.getServiceName().equals("SN_SYSTEM")) { //判断上线的服务是否是自己find的服务
                            mDeviceInstance = mADSManager.connectService(data);
                            AAOP_LogUtils.i(TAG, "SN_SYSTEM onOnline mDeviceInstance = " + mDeviceInstance);
//                            Bundle bundle = new Bundle();
//                            bundle.putString("service_name", "SN_VOICE"); //SN_MEDIASCANNER :子功能
                            // TODO
                            //有监听需求，向服务进行订阅，服务有通知时，通过mIADSEvent的onEvent方法回调通知
//                            mDeviceInstance.subscribe("SN_SYSTEM", bundle, mIADSEvent);
                        }
                    }
                }
            }
        }

        //find服务下线通知
        @Override
        public void onOffline(List<ADSBusAddress> address) {
            //如果判断关心的服务已经下线，重新走find流程
            AAOP_LogUtils.i(TAG, "SN_SYSTEM onOffline");
            initDeviceManager();
        }
    };


    public void stopAp() {
        AAOP_LogUtils.i(TAG, "stopAp called");
        if (mDeviceInstance != null) {
            Bundle para = new Bundle();
            para.putString("value", "0");
            ADSBusReturnValue returnValue = new ADSBusReturnValue();
            ADSBusErrorCodeEnum result = mDeviceInstance.invoke("SN_SYSTEM", "MSG_SET_HOTSPOT_SWITCH", para, returnValue);
            AAOP_LogUtils.i(TAG, "stopAp called result = " + result.getErrorCode());
            return;
        }
        AAOP_LogUtils.i(TAG, "stopAp called failed because mDeviceInstance = null");
    }

}
