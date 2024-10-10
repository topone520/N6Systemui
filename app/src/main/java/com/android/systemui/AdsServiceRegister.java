package com.android.systemui;

import android.util.Log;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSManager;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.IADSBusServiceObserver;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;

import java.util.ArrayList;

public class AdsServiceRegister {
    private final String TAG = AdsServiceRegister.class.getSimpleName();
    private final ArrayList<String> _items = new ArrayList<>();
    private final ADSManager _ads = new ADSManager();

    public AdsServiceRegister() {
    }

    public void injectService(String name) {
        _items.add(name);
    }

    public void submit(IADSBusServiceObserver observer) {
        AAOP_LogUtils.i(TAG, TAG + "::submit()");
        ArrayList<String> items = (ArrayList<String>) _items.clone();
        _items.clear();

        if (_ads.requestBindService(TAG + "AAOP_SRCEEN_SERVICE", items, observer)) {
            AAOP_LogUtils.i(TAG, " call ads.requestBindService() succeed");
            return;
        }

        new Thread(() -> {
            while (true) {
                boolean ret = _ads.requestBindService(TAG + "AAOP_SRCEEN_SERVICE", items, observer);
                AAOP_LogUtils.i(TAG, TAG + "- run: result = " + ret);
                if (ret) return;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    AAOP_LogUtils.i(TAG, TAG + "- run: " + ex.getMessage());
                }
            }
        });
    }
}