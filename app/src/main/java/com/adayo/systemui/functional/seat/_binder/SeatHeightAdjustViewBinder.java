package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.contract.IViewBinder;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderFloat;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.functional.negative._binder.SeatHeatViewBinder;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.windows.views.seat.CrossSlideView;

public class SeatHeightAdjustViewBinder extends AbstractViewBinder<Integer> {
    private final static String TAG = SeatHeightAdjustViewBinder.class.getSimpleName();
    private final int _position;
    private final int _resource_id;
    private CrossSlideView _cross_view;
    private int _old_action;

    public SeatHeightAdjustViewBinder(int position, int resource_id) {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(SeatService.getInstance())
                .withSetMessageName(SeatSOAConstant.MSG_SET_SEAT_HEIGHT_BTN)
                .withEventMessageName(SeatSOAConstant.MSG_EVENT_SEAT_HEIGHT_BTN)
                .withInitialValue(0)
                .build());
        _position = position;
        _resource_id = resource_id;
    }

    @Override
    public void _bind_view(View view) {
        _cross_view = view.findViewById(_resource_id);
        _cross_view.setOnHeightProgressListener(action -> {
            if (action == _old_action) return;
            _old_action = action;
            AAOP_LogUtils.d(TAG, "height = " + action);
            Bundle bundle = new Bundle();
            bundle.putInt("target", _position);
            bundle.putInt("value", action);
            SeatService.getInstance().invoke(SeatSOAConstant.MSG_SET_SEAT_HEIGHT_BTN, bundle, new ADSBusReturnValue());
        });

    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}
