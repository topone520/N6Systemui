package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.windows.views.hvac.ShortcutSeatHeatImageView;

public class ShortcutHeatViewBinder extends AbstractViewBinder<Integer> {

    private static final String TAG = ShortcutHeatViewBinder.class.getSimpleName();
    private ShortcutSeatHeatImageView _iv_heat;
    private final int _seat_position;
    private final int _resource_id;
    private final int[] _resource;

    public ShortcutHeatViewBinder(int position, int resource_id, int[] resource) {
        super(TAG,new ViewBinderProviderInteger.Builder()
                .withService(SeatService.getInstance())
                .withGetMessageName(SeatSOAConstant.MSG_GET_SEAT_HEATING)
                .withSetMessageName(SeatSOAConstant.MSG_SET_SEAT_HEATING)
                .withEventMessageName(SeatSOAConstant.MSG_EVENT_SEAT_HEATING)
                .withInitialValue(0)
                .build());
        _seat_position = position;
        _resource_id = resource_id;
        _resource = resource;
    }

    @Override
    public void _bind_view(View view) {
        _iv_heat = view.findViewById(_resource_id);
        _iv_heat.injectResource(_resource);
        _iv_heat.setListener(level -> {
            AAOP_LogUtils.d(TAG, "level = " + level);
            final Bundle _bundle = new Bundle();
            _bundle.putString("service_name", "SN_SEAT");
            _bundle.putInt("target", _seat_position);
            _bundle.putInt("value", level);
            SeatService.getInstance().invoke(SeatSOAConstant.MSG_SET_SEAT_HEATING, _bundle, new ADSBusReturnValue());
        });
    }

    @Override
    protected void _update_ui(Integer value) {
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        int value = bundle.getInt("value");
        AAOP_LogUtils.i(TAG, "_update_ui with bundle === " + bundle.toString() + "\rseat_position = " + _seat_position + "   value = " + value);
        if (bundle.getInt("target") == _seat_position) {
            _iv_heat._update_ui(value);
        }
    }
}
