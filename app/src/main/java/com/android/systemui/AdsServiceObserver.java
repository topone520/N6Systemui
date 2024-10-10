package com.android.systemui;

import android.os.Bundle;
import android.os.RemoteException;

import com.adayo.proxy.system.aaop_systemservice.IADSBusSubscribe;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusErrorCodeEnum;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.IADSBusServiceObserver;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.utils.Dispatcher;
import com.adayo.systemui.windows.dialogs.ScreenShutDownDialog;

import java.util.ArrayList;
import java.util.List;


public class AdsServiceObserver implements IADSBusServiceObserver {

    private final static String TAG = AdsServiceObserver.class.getSimpleName();
    private final List<IADSBusSubscribe> _listeners = new ArrayList<>();

    public AdsServiceObserver() {

    }

    public int onInvoked(String service_name, String message_id, Bundle param, ADSBusReturnValue value) {

        AAOP_LogUtils.d(TAG, "onInvoked message_id: " + message_id);

        switch (message_id) {
            case "MSG_SET_SCREEN_OFF_VALUE":
                if (!param.containsKey("value"))
                    return ADSBusErrorCodeEnum.ADSBUS_ERR_PARAMETER_FAIL.getErrorCode();

                final int paramValue = param.getInt("value");
                if (!(paramValue == 0 || paramValue == 1))
                    return ADSBusErrorCodeEnum.ADSBUS_ERR_PARAMETER_FAIL.getErrorCode();

                if (ScreenShutDownDialog.getInstance().isScreenOn() == (paramValue == 1))
                    return ADSBusErrorCodeEnum.ADSBUS_ERR_SUCCESS.getErrorCode();

                _handleScreenStatus(paramValue);
                _onScreenStatusChanged();
                break;

            case "MSG_GET_SCREEN_OFF_VALUE":
                value.getmReturnValue().putInt("value", ScreenShutDownDialog.getInstance().isScreenOn() ? 1 : 0);
                break;

            default:
                return ADSBusErrorCodeEnum.ADSBUS_ERR_FAIL.getErrorCode();
        }

        return ADSBusErrorCodeEnum.ADSBUS_ERR_SUCCESS.getErrorCode();
    }

    private void _onScreenStatusChanged() {
        final Bundle bundle = new Bundle();
        bundle.putInt("value", ScreenShutDownDialog.getInstance().isScreenOn() ? 1 : 0);
        bundle.putString("message_id", "MSG_EVENT_SCREEN_OFF_VALUE");

        Dispatcher.getInstance().dispatch(() -> _listeners.forEach(it -> {
            try {
                AAOP_LogUtils.d(TAG, "_onScreenStatusChanged bundle: " + bundle);
                it.onEvent(bundle);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }));
    }

    private static void _handleScreenStatus(int paramValue) {
        Dispatcher.getInstance().dispatchToUI(() -> {
            AAOP_LogUtils.d(TAG, "onInvoked paramValue: " + paramValue);
            if (paramValue == 1) {
                ScreenShutDownDialog.getInstance().turnOnOff(true);
                ScreenShutDownDialog.getInstance().dismiss();
            } else {
                ScreenShutDownDialog.getInstance().turnOnOff(false);
                ScreenShutDownDialog.getInstance().show();
            }
        });
    }

    public int onRegisterSubscribe(String receiver_name, Bundle param, IADSBusSubscribe subscriber) {
        AAOP_LogUtils.d(TAG, "onRegisterSubscribe receiver_name: " + receiver_name);
        if (subscriber == null) return ADSBusErrorCodeEnum.ADSBUS_ERR_PARAMETER_FAIL.getErrorCode();
        _listeners.add(subscriber);
        return ADSBusErrorCodeEnum.ADSBUS_ERR_SUCCESS.getErrorCode();
    }
}