package com.adayo.systemui.functional.hvac._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.proxy.vehicle.Dimensional;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.utils.ViewStyleUtils;
import com.android.systemui.R;

public class WaterIonsViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = WaterIonsViewBinder.class.getSimpleName();
    private LinearLayout _linear_water_ions;
    private TextView _tv_water_ions;
    private boolean _is_ions_open;

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
        _tv_water_ions = view.findViewById(R.id.tv_water_ions);
        _linear_water_ions = view.findViewById(R.id.linear_water_ions);
        _linear_water_ions.setOnClickListener(v -> {
            _is_ions_open = !_is_ions_open;
            _linear_water_ions.setSelected(_is_ions_open);
            _tv_water_ions.setSelected(_is_ions_open);
            _set_value(_is_ions_open ? HvacSOAConstant.HVAC_ON_IO : HvacSOAConstant.HVAC_OFF_IO);
            Dimensional.getInstance().doAction("DataSource.Model.FuLiZi", _is_ions_open ? "1" : "0");
        });
    }

    @Override
    protected void _update_ui(Integer value) {
        AAOP_LogUtils.d(TAG, "value = " + value);
        _is_ions_open = value == AreaConstant.HVAC_ON_IO;
        _linear_water_ions.setSelected(_is_ions_open);
        _tv_water_ions.setSelected(_is_ions_open);

    }

    @Override
    protected void _update_ui(Bundle bundle) {
    }
}
