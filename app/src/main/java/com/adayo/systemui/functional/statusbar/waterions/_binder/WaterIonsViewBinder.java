package com.adayo.systemui.functional.statusbar.waterions._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.android.systemui.R;

public class WaterIonsViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = WaterIonsViewBinder.class.getSimpleName();
    private LinearLayout _linear_water_ions;
    private TextView _tv_water_ions;
    private boolean _is_ions_open;
    private View _view;

    public WaterIonsViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(HvacService.getInstance())
                .withGetMessageName(HvacSOAConstant.MSG_GET_NEGATIVE_ION_SWITCH)
                .withSetMessageName(HvacSOAConstant.MSG_SET_NEGATIVE_ION_SWITCH)
                .withEventMessageName(HvacSOAConstant.MSG_EVENT_NEGATIVE_ION_SWITCH)
                .withInitialValue(0)
                .withCompare(false)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        _view = view;
        _tv_water_ions = _view.findViewById(R.id.tv_water_ions);
        _linear_water_ions = _view.findViewById(R.id.linear_water_ions);
        _linear_water_ions.setOnClickListener(v -> {
            _is_ions_open = !_is_ions_open;
            _tv_water_ions.setTextColor(_view.getContext().getColor(_is_ions_open ? R.color.hvac_white : R.color.white40));
            _linear_water_ions.setBackgroundResource(_is_ions_open ? R.drawable.comm_c478bc5_bg : R.drawable.comm_c39414f_bg);
            _set_value(_is_ions_open ? HvacSOAConstant.HVAC_ON_IO : HvacSOAConstant.HVAC_OFF_IO);
        });
    }

    @Override
    protected void _update_ui(Integer value) {
        AAOP_LogUtils.d(TAG, "value = " + value);
        _is_ions_open = value == AreaConstant.HVAC_ON_IO;
        _tv_water_ions.setTextColor(_view.getContext().getColor(_is_ions_open ? R.color.hvac_white : R.color.white40));
        _linear_water_ions.setBackgroundResource(_is_ions_open ? R.drawable.comm_c478bc5_bg : R.drawable.comm_c39414f_bg);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
    }
}
