package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderFloat;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.windows.views.seat.HorSlideView;

public class SeatBackrestViewBinder extends AbstractViewBinder<Integer> {
    private final static String TAG = SeatBackrestViewBinder.class.getName();
    private final int _position;
    private final int _resource_id;
    private HorSlideView _hor_view;

    public SeatBackrestViewBinder(int position, int resource_id) {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(SeatService.getInstance())
                .withSetMessageName(SeatSOAConstant.MSG_SET_SEAT_BACKREST_BTN)
                .withEventMessageName(SeatSOAConstant.MSG_EVENT_SEAT_BACKREST_BTN)
                .withInitialValue(0)
                .build());
        _position = position;
        _resource_id = resource_id;
    }

    /*   0x0: Off
   0x1: Up
   0x2: Down*/
    @Override
    public void _bind_view(View view) {
        _hor_view = view.findViewById(_resource_id);
        _hor_view.setSlideListener(action -> {
            AAOP_LogUtils.d(TAG, "action--------" + action);
            Bundle bundle = new Bundle();
            bundle.putInt("target", _position);
            bundle.putInt("value", _position == SeatSOAConstant.RIGHT_FRONT ? value_negation(action) : action);
            SeatService.getInstance().invoke(SeatSOAConstant.MSG_SET_SEAT_BACKREST_BTN, bundle, new ADSBusReturnValue());
        });
    }

    public int value_negation(int value) {
        if (value == SeatSOAConstant.SEAT_ADJUST_UP) {
            return SeatSOAConstant.SEAT_ADJUST_DOWN;
        } else if (value == SeatSOAConstant.SEAT_ADJUST_DOWN) {
            return SeatSOAConstant.SEAT_ADJUST_UP;
        }
        return SeatSOAConstant.SEAT_ADJUST_OFF;
    }


    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, "bundle = " + bundle.toString());
        if (_position == bundle.getInt("target", -1)) {
            _hor_view.setProgress(bundle.getFloat("value"));
        }
    }

}
