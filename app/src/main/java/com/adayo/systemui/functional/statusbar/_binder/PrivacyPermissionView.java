package com.adayo.systemui.functional.statusbar._binder;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBus;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSManager;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.IADSBusClientObserver;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.IADSEvent;
import com.adayo.proxy.system.aaop_systemservice.soa.data.ADSBusAddress;

import java.util.List;

public class PrivacyPermissionView {
    private static final String TAG = PrivacyPermissionView.class.getSimpleName();
    private static volatile PrivacyPermissionView sInstance = null;
    private ADSManager mAdsManager;
    private Handler mHandler;
    private ADSBus mPrivacy;

    private PrivacyPermissionView() {

    }

    //查找隐私服务
    public void connect() {
        mAdsManager = new ADSManager();
        mHandler = new Handler(msg -> {
            Log.d(TAG, TAG + "- msg.arg1: " + msg.arg1);
            if (msg.arg1 == 0) {
                //连接隐私服务
                connectPrivacy((ADSBusAddress) msg.obj);
            }

            if (msg.arg1 == 1) {
                Bundle bundle = msg.getData();
                if (bundle == null) return false;
                String message_id = (String) msg.obj;
                Log.d(TAG, TAG + "- connect: message_id: " + message_id + ", bundle: " + bundle.toString());
                onMicrophoneListener.onMicrophone(bundle);
                onCameraListener.onCamera(bundle);

            }
            return false;
        });
        mAdsManager.findService(Constant.PACKAGE, Constant.SN_PRIVACY, mIADSBusClientObserver);
    }


    public static PrivacyPermissionView getInstance() {
        if (sInstance == null) {
            synchronized (PrivacyPermissionView.class) {
                if (sInstance == null) {
                    sInstance = new PrivacyPermissionView();
                }
            }
        }
        return sInstance;
    }


    //连接隐私服务
    private void connectPrivacy(ADSBusAddress adsBusAddress) {
        Log.d(TAG, TAG + "- connectPrivacy: " + adsBusAddress.toString());
        //连接隐私权限服务
        mPrivacy = mAdsManager.connectService(adsBusAddress);
        if (mPrivacy == null) {
            Log.d(TAG, TAG + "- connectPrivacy: mPrivacy == null");
            return;
        }
        //判断连接是否成功
        boolean connect = mPrivacy.isConnect();
        Log.d(TAG, TAG + "- connectPrivacy: " + Constant.SN_PRIVACY + "connect: " + connect);
        //订阅隐私权限服
        Bundle bundle = new Bundle();
        mPrivacy.subscribe(Constant.PACKAGE, bundle, mIADSEvent);
    }

    public ADSBus getPrivacy(){
        return mPrivacy;
    }

    private IADSBusClientObserver mIADSBusClientObserver = new IADSBusClientObserver() {
        @Override
        public void onOnline(List<ADSBusAddress> list) {
            Log.d(TAG, TAG + "- onOnline: " + list.size());
            for (ADSBusAddress adsBusAddress : list) {
                if (adsBusAddress.getState() == ADSBusAddress.SERVICE_STATE_ONLINE) {
                    Message message = mHandler.obtainMessage();
                    Log.d(TAG, TAG + "onOnline: " + adsBusAddress.getServiceName().equals(Constant.SN_PRIVACY));
                    //if (adsBusAddress.getServiceName().equals(Constant.SN_PRIVACY)) {
                        message.arg1 = 0;
                    //}
                    message.obj = adsBusAddress;
                    mHandler.sendMessage(message);
                }
            }
        }

        @Override
        public void onOffline(List<ADSBusAddress> list) {
            Log.d(TAG, TAG + "- onOffline: ");
        }
    };

    private IADSEvent mIADSEvent = new IADSEvent() {
        @Override
        public void onEvent(Bundle bundle) {
            Log.d(TAG, TAG + "- onEvent: " + bundle.toString());
            String message_id = bundle.getString(Constant.MESSAGE_ID, "");
            Message message = mHandler.obtainMessage();
            message.arg1 = 1;
            message.obj = message_id;
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    };

    private onCameraListener onCameraListener;
    public void setOnCameraListener(onCameraListener onCameraListener){
        this.onCameraListener = onCameraListener;
    }
    public interface onCameraListener{
        void onCamera(Bundle bundle);
    }

    private onMicrophoneListener onMicrophoneListener;
    public void setOnMicrophoneListener(onMicrophoneListener onMicrophoneListener){
        this.onMicrophoneListener = onMicrophoneListener;
    }
    public interface onMicrophoneListener{
        void onMicrophone(Bundle bundle);
    }


}
