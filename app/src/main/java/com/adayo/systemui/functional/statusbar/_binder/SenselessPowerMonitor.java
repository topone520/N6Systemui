package com.adayo.systemui.functional.statusbar._binder;

import android.os.Bundle;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.contract.Constant;
import com.adayo.soavb.utils.Dispatcher;
import com.adayo.systemui.proxy.vehicle.VehicleGearService;
import com.adayo.systemui.proxy.vehicle.VehiclePowerService;

public class SenselessPowerMonitor {
    private final static String TAG = SenselessPowerMonitor.class.getSimpleName();
    private boolean _isPark = false;
    private boolean _isEnable = false;
    private IListener _listener;

    public interface IListener {
        void onPowerNotify();
    }

    public SenselessPowerMonitor() {
        Dispatcher.getInstance().dispatch(() -> {
            VehicleGearService.getInstance().connect("SystemUI.SenselessPowerMonitor.Gear", service -> {
                service.subscribe(this::onGearChanged);
                _isEnable = fetchMessage("MSG_GET_GEAR_ENABLE") == 1;
                _isPark = fetchMessage("MSG_GET_GEAR") == 1;
            });

            VehiclePowerService.getInstance().connect("SystemUI.SenselessPowerMonitor.Power", service -> {
                service.subscribe(this::onKlEvent);
            });
        });
    }

    public void setListener(SenselessPowerMonitor.IListener listener) {
        _listener = listener;
    }

    private void onKlEvent(Bundle bundle) {
        if (bundle == null || !bundle.containsKey(Constant.TAG_FIELD_MESSAGE_ID) || !bundle.containsKey(Constant.TAG_FIELD_DEFAULT_VALUE_KEY))
            return;
        final String message_id = bundle.getString(Constant.TAG_FIELD_MESSAGE_ID);
        if (message_id == null) return;
        if (_listener == null) return;
        AAOP_LogUtils.d(TAG,"onKlEvent message_id: "+message_id);
        if (message_id.equals("MSG_EVENT_FEELLESS_OVERTIME_STATE")) {
            Dispatcher.getInstance().dispatchToUI(() -> _listener.onPowerNotify());
        }
    }

    private void onGearChanged(Bundle bundle) {
        if (bundle == null || !bundle.containsKey(Constant.TAG_FIELD_MESSAGE_ID) || !bundle.containsKey(Constant.TAG_FIELD_DEFAULT_VALUE_KEY))
            return;
        final String message_id = bundle.getString(Constant.TAG_FIELD_MESSAGE_ID);
        if (message_id == null) return;

        AAOP_LogUtils.d(TAG,"onGearChanged message_id: "+message_id);
        switch (message_id) {
            case "MSG_EVENT_GEAR_ENABLE":
                _isEnable = bundle.getInt(Constant.TAG_FIELD_DEFAULT_VALUE_KEY) == 1;
                break;
            case "MSG_EVENT_GEAR":
                _isPark = bundle.getInt(Constant.TAG_FIELD_DEFAULT_VALUE_KEY) == 1;
                postMessage(0);
                break;

            default:
                break;
        }
    }

    public void clickScreen() {
        if (!(_isEnable && _isPark)) return;
        postMessage(1);
    }

    private void postMessage(final int value) {
        final Bundle bundle = new Bundle();
        bundle.putInt(Constant.TAG_FIELD_DEFAULT_VALUE_KEY, value);
        VehiclePowerService.getInstance().invoke("MSG_SET_KL_KEEP_SEND_VALUE", bundle, new ADSBusReturnValue());
        if (value == 1){
            Dispatcher.getInstance().dispatch(new Runnable() {
                @Override
                public void run() {
                    postMessage(0);
                }
            },60000);
        }
    }

    private int fetchMessage(final String message_id) {
        final ADSBusReturnValue value = new ADSBusReturnValue();
        VehicleGearService.getInstance().invoke(message_id, new Bundle(), value);
        return value.getmReturnValue().getInt(Constant.TAG_FIELD_DEFAULT_VALUE_KEY);
    }
}
