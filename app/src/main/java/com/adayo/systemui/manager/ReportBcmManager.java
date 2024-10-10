package com.adayo.systemui.manager;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBus;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusErrorCodeEnum;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSManager;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.IADSBusClientObserver;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.IADSEvent;
import com.adayo.proxy.system.aaop_systemservice.soa.data.ADSBusAddress;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.contents.AreaConstant;

import org.json.JSONObject;

import java.util.List;

public class ReportBcmManager {

    private static final String TAG = "HVAC_Bcm_" + ReportBcmManager.class.getSimpleName();
    private static final String SN_DATA_REPORT = "SN_DATA_REPORT";

    private static ReportBcmManager instance;

    public ADSBus mBcmInstance = null;
    private ADSManager mADSManager = null;

    private ReportBcmManager() {
        bindService();
    }

    public static ReportBcmManager getInstance() {
        synchronized (ReportBcmManager.class) {
            if (instance == null) {
                instance = new ReportBcmManager();
            }
            return instance;
        }
    }

    private void bindService() {
        mADSManager = new ADSManager();
        ADSBusErrorCodeEnum code = mADSManager.findService(TAG, SN_DATA_REPORT, mIADSBusClientObserver);
        AAOP_LogUtils.i(TAG, "SN_DATA_REPORT findService errorCode: " + code);
    }

    public IADSBusClientObserver mIADSBusClientObserver = new IADSBusClientObserver() {
        //find服务上线通知
        @Override
        public void onOnline(List<ADSBusAddress> address) {
            AAOP_LogUtils.i(TAG, "SN_DATA_REPORT onOnline");
            if (address.size() > 0) {
                for (ADSBusAddress data : address) {
                    AAOP_LogUtils.i(TAG, "SN_DRIVING_DATA getServiceName = " + data.getServiceName());
                    if (TextUtils.equals(data.getServiceName(), SN_DATA_REPORT) && data.getState() == ADSBusAddress.SERVICE_STATE_ONLINE) {
                        mBcmInstance = mADSManager.connectService(data);
                        //订阅信息
                        Bundle bundle = new Bundle();
                        bundle.putString("service_name", SN_DATA_REPORT);
                        mBcmInstance.subscribe(SN_DATA_REPORT + TAG, bundle, mIADSEvent);
                        AAOP_LogUtils.i(TAG, "SN_DATA_REPORT BEGIN FETCH DATA");
                    }
                }
            }
        }

        //find服务下线通知
        @Override
        public void onOffline(List<ADSBusAddress> address) {
            //如果判断关心的服务已经下线，重新走find流程
            AAOP_LogUtils.i(TAG, "SN_DRIVING_DATA onOffline");
        }
    };

    public IADSEvent mIADSEvent = event -> {
        AAOP_LogUtils.i(TAG, "SN_DRIVING_DATA onEvent");
    };

    /**
     * key: "behaviorid", value: 用户行为ID（String）：参照《车机埋点需求表》中用户行为参考系
     * <p>
     * key: "interactiveway", value: 交互方式（String）：行为触发方式，具体值参照《车机埋点需求表》中交互放肆参考系
     * <p>
     * key: "reportData", value: 上报数据JSON数据（String）：各模块需要上报的行为数据，具体数据结果参考《车机埋点需求表》中“User Tracking Data Item”
     * <p>
     * key: "cpSource", value: 内容源名称（String）：实际使用的CP供应商App名称
     */
    public void sendHvacReport(String userId, String json, Object content) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        try {
            jsonObject2.put(json, content);
            jsonObject1.put("air_conditioning", jsonObject2);
            jsonObject.put("trackingdata", jsonObject1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mBcmInstance == null) return;
        Bundle bundle = new Bundle();
        bundle.putString("behaviorid", userId);
        bundle.putString("interactiveway", "1");
        bundle.putString("reportData", jsonObject.toString());
        bundle.putString("cpSource", "");
        mBcmInstance.invoke(SN_DATA_REPORT, "MSG_POST_DATA_REPORT", bundle, new ADSBusReturnValue());
    }
    /**
     * key: "behaviorid", value: 用户行为ID（String）：参照《车机埋点需求表》中用户行为参考系
     * <p>
     * key: "interactiveway", value: 交互方式（String）：行为触发方式，具体值参照《车机埋点需求表》中交互放肆参考系
     * <p>
     * key: "reportData", value: 上报数据JSON数据（String）：各模块需要上报的行为数据，具体数据结果参考《车机埋点需求表》中“User Tracking Data Item”
     * <p>
     * key: "cpSource", value: 内容源名称（String）：实际使用的CP供应商App名称
     */
    public void sendSeatReport(String userId, String json, Object content) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        try {
            jsonObject2.put(json, content);
            jsonObject1.put("setting", jsonObject2);
            jsonObject.put("trackingdata", jsonObject1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mBcmInstance == null) return;
        Bundle bundle = new Bundle();
        bundle.putString("behaviorid", userId);
        bundle.putString("interactiveway", "1");
        bundle.putString("reportData", jsonObject.toString());
        bundle.putString("cpSource", "");
        mBcmInstance.invoke(SN_DATA_REPORT, "MSG_POST_DATA_REPORT", bundle, new ADSBusReturnValue());
    }

    /**
     * key: "behaviorid", value: 用户行为ID（String）：参照《车机埋点需求表》中用户行为参考系
     * <p>
     * key: "interactiveway", value: 交互方式（String）：行为触发方式，具体值参照《车机埋点需求表》中交互放肆参考系
     * <p>
     * key: "reportData", value: 上报数据JSON数据（String）：各模块需要上报的行为数据，具体数据结果参考《车机埋点需求表》中“User Tracking Data Item”
     * <p>
     * key: "cpSource", value: 内容源名称（String）：实际使用的CP供应商App名称
     */
    public void sendNegativeReport(String userId, String json, String content) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        try {
            jsonObject2.put(json, content);
            jsonObject1.put("Negativeonepage", jsonObject2);
            jsonObject.put("trackingdata", jsonObject1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mBcmInstance == null) return;
        Bundle bundle = new Bundle();
        bundle.putString("behaviorid", userId);
        bundle.putString("interactiveway", "1");
        bundle.putString("reportData", jsonObject.toString());
        bundle.putString("cpSource", "");
        mBcmInstance.invoke(SN_DATA_REPORT, "MSG_POST_DATA_REPORT", bundle, new ADSBusReturnValue());
    }

}
