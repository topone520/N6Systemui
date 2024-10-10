package com.adayo.systemui.manager;

import android.os.Bundle;
import android.util.Log;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBus;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusErrorCodeEnum;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSManager;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.IADSBusClientObserver;
import com.adayo.proxy.system.aaop_systemservice.soa.data.ADSBusAddress;

import java.util.List;

public class NotifyVoiceManager {
    private static NotifyVoiceManager notifyVoiceManager;
    private static String TAG = NotifyVoiceManager.class.getName();
    private Bundle bundle = new Bundle();
    private ADSBus adsBus;

    public static NotifyVoiceManager getInstance() {
        if (notifyVoiceManager == null){
            synchronized (NotifyVoiceManager.class){
                if (notifyVoiceManager == null){
                    notifyVoiceManager = new NotifyVoiceManager();
                }
            }
        }
        return notifyVoiceManager;
    }

    public void init() {

        ADSManager adsManager = new ADSManager();
        ADSBusErrorCodeEnum codeEnum = adsManager.findService("SystemUI", "SN_VOICE", new IADSBusClientObserver() {
            @Override
            public void onOnline(List<ADSBusAddress> list) {
                for (ADSBusAddress adsBusAddress : list) {
                    adsBus = adsManager.connectService(adsBusAddress);
                }
            }

            @Override
            public void onOffline(List<ADSBusAddress> list) {

            }
        });
    }

    public void readMessage(String message) {
        bundle.clear();
        ADSBusReturnValue returnValue = new ADSBusReturnValue();
        bundle.putString("service_name","SN_VOICE");
        bundle.putString("text",message);
        adsBus.invoke("SN_VOICE","MSG_SET_TTS",bundle,returnValue);
    }
}
