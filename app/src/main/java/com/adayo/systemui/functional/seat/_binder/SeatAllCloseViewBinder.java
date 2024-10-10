package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.proxy.vehicle.SeatService;

public class SeatAllCloseViewBinder extends AbstractViewBinder<Integer> {
    private final static String TAG = SeatAllCloseViewBinder.class.getName();
    private final int _position;
    private final int _resource_id;

    public SeatAllCloseViewBinder(int resource_id, int position) {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(SeatService.getInstance())
                .withSetMessageName(SeatSOAConstant.MSG_SET_ALL_CLOSE_FUNCTION)
                .withInitialValue(0)
                .build());
        _position = position;
        _resource_id = resource_id;
    }

    @Override
    public void _bind_view(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt("target", _position);
        view.findViewById(_resource_id).setOnClickListener(v -> SeatService.getInstance().invoke(SeatSOAConstant.MSG_SET_ALL_CLOSE_FUNCTION, bundle, new ADSBusReturnValue()));
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {
    }
}
