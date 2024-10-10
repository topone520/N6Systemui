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
import com.android.systemui.R;

public class IntellIonsViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = IntellIonsViewBinder.class.getSimpleName();
    private LinearLayout _linear_water_ions;
    private TextView _tv_water_ions;
    private boolean _is_ions_open;

    public IntellIonsViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(HvacService.getInstance())
                .withGetMessageName(HvacSOAConstant.MSG_GET_INTELL_IONS)
                .withSetMessageName(HvacSOAConstant.MSG_SET_INTELL_IONS)
                .withEventMessageName(HvacSOAConstant.MSG_EVENT_INTELL_IONS)
                .withInitialValue(0)
                .withCompare(false)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        _tv_water_ions = view.findViewById(R.id.tv_intell_ions);
        _linear_water_ions = view.findViewById(R.id.linear_intell_ions);
        _linear_water_ions.setOnClickListener(v -> {
            _is_ions_open = !_is_ions_open;
            _linear_water_ions.setSelected(_is_ions_open);
            _tv_water_ions.setSelected(_is_ions_open);
            _set_value(_is_ions_open ? HvacSOAConstant.HVAC_ON_IO : HvacSOAConstant.HVAC_OFF_CLOSE);
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
