package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.windows.views.seat.SeatSteeringImageView;
import com.adayo.systemui.windows.views.seat.VtpSeatSteeringImageView;
import com.android.systemui.R;

public class VtpSteeringViewBinder extends AbstractViewBinder<Integer> {
    private final static String TAG = VtpSteeringViewBinder.class.getName();
    private VtpSeatSteeringImageView _re_steering;
    public VtpSteeringViewBinder() {
        super(TAG,new ViewBinderProviderInteger.Builder()
                .withService(SeatService.getInstance())
                .withGetMessageName(SeatSOAConstant.MSG_GET_SEAT_STEERING_WHEEL)
                .withSetMessageName(SeatSOAConstant.MSG_SET_SEAT_STEERING_WHEEL)
                .withEventMessageName(SeatSOAConstant.MSG_EVENT_SEAT_STEERING_WHEEL)
                .withInitialValue(0)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        _re_steering = view.findViewById(R.id.re_left_front_steering);
        _re_steering.setSteeringListener(grep -> _set_value(grep));
    }

    @Override
    protected void _update_ui(Integer value) {
        _re_steering._update_ui(value);
    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}
