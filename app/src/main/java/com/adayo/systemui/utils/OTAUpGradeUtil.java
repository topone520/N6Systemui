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

import java.util.ArrayList;
import java.util.List;

/**
 * The type Ota up grade util.
 */
public class OTAUpGradeUtil {
    /**
     * The constant instance.
     */
    public static volatile OTAUpGradeUtil instance;
    /**
     * The constant TAG.
     */
    public static String TAG = "_OTAUpGradeUtil";
    private ADSBus mUpGradeInstance = null;
    private static final int HANDLER_UPGRADE = 0;
    private static final int GET_UPGRADE_STATUS = 1;
    private static final String MSG_EVENT_UPGRADE_STATUS = "msg_event_upgrade_status";
    private static final String MSG_SET_SHOW_UPGRADE_VIEW = "msg_set_show_upgrade_view";
    private String messageId = "";
    /**
     * The constant isOnline.
     */
    public static boolean isOnline = false;

    /**
     * 获取instance.
     *
     * @return 返回该类对象
     */
    public static OTAUpGradeUtil getInstance() {
        if (instance == null) {
            synchronized (OTAUpGradeUtil.class) {
                if (instance == null) {
                    instance = new OTAUpGradeUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化.
     *
     * @param mMessageId 消息id
     */
    public void init(String mMessageId) {
        messageId = mMessageId;
        ADSManager adsManager = new ADSManager();
        List<String> serviceList = new ArrayList<>();
        serviceList.add("SN_UPGRADE");
        ADSBusErrorCodeEnum codeEnum = adsManager.findService("com.android.systemui", serviceList, new IADSBusClientObserver() {
            @Override
            public void onOnline(List<ADSBusAddress> list) {
                isOnline = true;
                for (ADSBusAddress adsBusAddress : list) {
                    Log.i(TAG, " onOnline data:" + adsBusAddress.getState());
                    Log.i(TAG, "adsBusAddress.getState()" + adsBusAddress.getState());
                    if (adsBusAddress.getState() == ADSBusAddress.SERVICE_STATE_ONLINE) {
                        Log.i(TAG, "adsBusAddress.getServiceName()" + adsBusAddress.getServiceName());
                        if (adsBusAddress.getServiceName().equals("SN_UPGRADE")) {
                            mUpGradeInstance = adsManager.connectService(adsBusAddress);
                            Message message = new Message();
                            switch (messageId) {
                                case MSG_EVENT_UPGRADE_STATUS:
                                    message.what = GET_UPGRADE_STATUS;
                                    mHandler.sendMessage(message);
                                    break;
                                case MSG_SET_SHOW_UPGRADE_VIEW:
                                    message.what = HANDLER_UPGRADE;
                                    mHandler.sendMessage(message);
                                    break;
                            }

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
            switch (msg.what) {
                case HANDLER_UPGRADE:
                    Log.i(TAG, " case" + "HANDLER_UPGRADE------------");
                    getUpGrade();
                    break;
                case GET_UPGRADE_STATUS:
                    Log.i(TAG, " case" + "GET_UPGRADE_STATUS------------");
                    getUpGradeStatus();
                    break;
            }
        }
    };
    /**
     * 获取升级信息.
     *
     */
    private void getUpGrade() {
        Log.i(TAG, " case" + "getUpGrade------------");
        if (mUpGradeInstance != null) {
            Log.i(TAG, " case" + "mUpGradeInstance------------");
            Bundle bundle = new Bundle();
            bundle.putInt("type", 0);
            ADSBusReturnValue returnValue = new ADSBusReturnValue();
            ADSBusErrorCodeEnum result = mUpGradeInstance.invoke("SN_UPGRADE", "MSG_SET_SHOW_UPGRADE_VIEW", bundle, returnValue);
            Log.i(TAG, " case" + "case-----result-------" + result);
            int value = returnValue.getmReturnValue().getInt("result"); //参考javadoc中returnValue Bundle的说明
            Log.i(TAG, "invoke key currentAudioFocus:" + " --value:" + value);

        }

    }

    /**
     * 获取升级状态.
     *
     * @return 返回该状态
     */
    public int getUpGradeStatus() {
        Log.i(TAG, " getUpGradeStatus" + "getUpGradeStatus------------");
        if (mUpGradeInstance != null) {
            Log.i(TAG, " getUpGradeStatus" + "getUpGradeStatus------------");
            ADSBusReturnValue returnValue = new ADSBusReturnValue();
            ADSBusErrorCodeEnum result = mUpGradeInstance.invoke("SN_UPGRADE", "MSG_GET_UPGRADE_STATUS", returnValue);
            Log.i(TAG, " case" + "case-----result-------" + result);
            int value = returnValue.getmReturnValue().getInt("result"); //参考javadoc中returnValue Bundle的说明
            int otaState = returnValue.getmReturnValue().getInt("state");
            String version = returnValue.getmReturnValue().getString("version");
            Log.i(TAG, "invoke key currentAudioFocus:" + " --value:" + value);
            Log.i(TAG, "invoke key currentAudioFocus:" + " --otaState:" + otaState);
            Log.i(TAG, "invoke key currentAudioFocus:" + " --version:" + version);

            return otaState;
        } else {
            return -1;
        }
    }

}
