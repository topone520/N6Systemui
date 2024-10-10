package com.adayo.systemui.utils;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.adayo.proxy.media.app.phone.callback.IBtPhoneCallInfoAidl;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBus;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusErrorCodeEnum;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSManager;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.IADSBusClientObserver;
import com.adayo.proxy.system.aaop_systemservice.soa.data.ADSBusAddress;

import java.util.List;

/**
 * 获取蓝牙通话状态.
 */
public class CallingStatusSoaUtil {
    /**
     * The constant instance.
     */
    public static volatile CallingStatusSoaUtil instance;
    /**
     * The constant TAG.
     */
    public static String TAG = "_CallingStatusSoaUtil";
    private ADSBus mCallingStatusSoaInstance = null;

    private static final String SERVICE_NAME = "SN_BT_PHONE";
    private static final String messageId = "MSG_BTP_REGISTER_CALL_INFO_CHANGED_LISTENER";
    /**
     * The constant isOnline.
     */
    public static boolean isOnline = false;
    /**
     * The Value.
     */
    private int value = -1;
    private static final String SN_NAME = "com.android.systemui";
    private static final String PHONE_LISTENER = "com.adayo.proxy.media.app.phone.callback.IBtPhoneCallInfoAidl.Stub";
    /**
     * 初始状态, 无电话时
     */
    int IDLE = -1;
    /**
     * 唤起通话弹窗后默认为电话状态（兼容网络通话异常情况）
     */
    private static final int CALLING = 0;
    /**
     * 挂断后
     */
    private static final int ENDING = 2;
    /**
     * 来电中（单方来电）
     */
    private static final int INCOMING = 3;
    /**
     * 去电中（单方去电）
     */
    private static final int DIALING = 4;
    /**
     * 单方通话中
     */
    private static final int SINGLE_CALLING = 5;
    /**
     * 三方来电
     */
    private static final int THIRD_INCOMING = 6;
    /**
     * 三方去电
     */
    private static final int THIRD_DIALING = 7;
    /**
     * 三方通话中(当前通话中的为单方来电时的电话,三方电话保持中)
     */
    private static final int THIRD_CALLING_1 = 8;
    /**
     * 三方通话中(当前通话中的为三方来电时的电话,单方电话保持中)
     */
    private static final int THIRD_CALLING_2 = 9;

    private boolean isPlay = true;

    /**
     * 获取instance.
     *
     * @return 返回该类对象
     */
    public static CallingStatusSoaUtil getInstance() {
        if (instance == null) {
            synchronized (CallingStatusSoaUtil.class) {
                if (instance == null) {
                    instance = new CallingStatusSoaUtil();
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
                            mCallingStatusSoaInstance = adsManager.connectService(adsBusAddress);
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
    public boolean isPlay() {
        Log.i(TAG, " case" + "sendToSoa------------");
        if (mCallingStatusSoaInstance != null) {
            Log.i(TAG, " case" + "mPGradeSoaInstance------------");
            Bundle para = new Bundle();
            para.putString("messageId", messageId);
            para.putBinder("listener", new IBtPhoneCallInfoAidl.Stub() {
                @Override
                public void updateCallState(int i) throws RemoteException {
                    switch (i){
                        case CALLING:
                        case INCOMING:
                        case DIALING:
                        case SINGLE_CALLING:
                        case THIRD_INCOMING:
                        case THIRD_CALLING_1:
                        case THIRD_CALLING_2:
                            isPlay = false;
                            break;
                        default:
                            isPlay = true;
                            break;
                    }
                }

                @Override
                public void updateOneWayName(String s) throws RemoteException {

                }

                @Override
                public void updateOneWayNumber(String s) throws RemoteException {

                }

                @Override
                public void updateThirdWayName(String s) throws RemoteException {

                }

                @Override
                public void updateThirdWayNumber(String s) throws RemoteException {

                }

                @Override
                public void updateMicMuteState(int i) throws RemoteException {

                }

                @Override
                public void updateAudioChannelState(int i) throws RemoteException {

                }

                @Override
                public void updatePrivacyState(int i) throws RemoteException {

                }
            });
            ADSBusReturnValue returnValue = new ADSBusReturnValue();
            ADSBusErrorCodeEnum result = mCallingStatusSoaInstance.invoke(SERVICE_NAME, messageId, para, returnValue);
            Log.i(TAG, " case" + "case-----result-------" + result);
            value = returnValue.getmReturnValue().getInt("result");
        }
        return isPlay;
    }

}
