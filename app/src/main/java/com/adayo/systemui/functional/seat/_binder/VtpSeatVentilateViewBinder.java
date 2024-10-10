package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.windows.views.seat.SeatVentilateImageView;
import com.adayo.systemui.windows.views.seat.VtpSeatVentilateImageView;

public class VtpSeatVentilateViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = VtpSeatVentilateViewBinder.class.getSimpleName();
    private VtpSeatVentilateImageView _re_ventilate;
    private final int _seat_position;
    private final int _resource_id;

    public VtpSeatVentilateViewBinder(int position, int resource_id) {
        super(TAG,new ViewBinderProviderInteger.Builder()
                .withService(SeatService.getInstance())
                .withGetMessageName(SeatSOAConstant.MSG_GET_SEAT_VENTILATION)
                .withSetMessageName(SeatSOAConstant.MSG_SET_SEAT_VENTILATION)
                .withEventMessageName(SeatSOAConstant.MSG_EVENT_SEAT_VENTILATION)
                .withInitialValue(0)
                .build());
        _seat_position = position;
        _resource_id = resource_id;
    }

    @Override
    public void _bind_view(View view) {
        _re_ventilate = view.findViewById(_resource_id);
        _re_ventilate.setListener(level -> {
            AAOP_LogUtils.d(TAG, "level = " + level);
            final Bundle _bundle = new Bundle();
            _bundle.putInt("target", _seat_position);
            _bundle.putInt("value", level);
            SeatService.getInstance().invoke(SeatSOAConstant.MSG_SET_SEAT_VENTILATION, _bundle, new ADSBusReturnValue());
        });
    }


    @Override
    protected void _update_ui(Integer value) {
        AAOP_LogUtils.d("update_ui:: value = " + value);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        int value = bundle.getInt("value");
        AAOP_LogUtils.i(TAG, "_update_ui with bundle === " + bundle.toString() + "\rseat_position = " + _seat_position + "   value = " + value);
        if (bundle.getInt("target") == _seat_position) {
            _re_ventilate._update_ui(value);
        }
    }
}
