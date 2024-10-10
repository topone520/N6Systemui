package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.manager.TimerManager;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.android.systemui.R;

public class SeatHeatGrepViewBinder extends AbstractViewBinder<Integer> {
    private final static String TAG = SeatHeatGrepViewBinder.class.getName();
    private boolean _is_open;
    private TextView _tv_heat_grep;

    public SeatHeatGrepViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(SeatService.getInstance())
                .withGetMessageName(SeatSOAConstant.MSG_GET_PERF_SEAT_HEAT_LV_AUTO_REDUCE_REQ_SET)
                .withSetMessageName(SeatSOAConstant.MSG_SET_PERF_SEAT_HEAT_LV_AUTO_REDUCE_REQ_SET)
                .withEventMessageName(SeatSOAConstant.MSG_EVENT_PERF_SEAT_HEAT_LV_AUTO_REDUCE_REQ_SET)
                .withInitialValue(0)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        RelativeLayout _heat_grep = view.findViewById(R.id.re_left_front_heat_grep);
        _tv_heat_grep = view.findViewById(R.id.tv_heat_grep);
        _heat_grep.setOnClickListener(v -> {
            _is_open = !_is_open;
            _set_value(_is_open ? SeatSOAConstant.SEAT_HEAT_AUTO_OPEN : SeatSOAConstant.SEAT_HEAT_AUTO_CLOSE);
            _tv_heat_grep.setSelected(_is_open);
        });
    }


    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {
        int value = bundle.getInt("value");
        _is_open = value == AreaConstant.HVAC_ON_IO;
        _tv_heat_grep.setSelected(_is_open);
    }
}
