package com.adayo.systemui.functional.hvac._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.utils.TimeUtils;
import com.android.systemui.R;

public class CirculateViewBinder extends AbstractViewBinder<Integer> {

    private static final String TAG = CirculateViewBinder.class.getSimpleName();
    private CheckBox _cb_wind_circulate;

    public CirculateViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(HvacService.getInstance())
                .withGetMessageName(HvacSOAConstant.MSG_GET_CIRCULATION)
                .withSetMessageName(HvacSOAConstant.MSG_SET_CIRCULATION)
                .withEventMessageName(HvacSOAConstant.MSG_EVENT_REAR_CIRCULATION)
                .withInitialValue(0)
                .withCompare(false)
                .build());
    }


    @Override
    public void _bind_view(View view) {
        _cb_wind_circulate = view.findViewById(R.id.cb_wind_circulate);
        _cb_wind_circulate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                _set_value(isChecked ? HvacSOAConstant.HVAC_ON_IO : HvacSOAConstant.HVAC_OFF_IO);
                ReportBcmManager.getInstance().sendHvacReport("8630017", "wind_Cyclicmode_setting", _cb_wind_circulate.isChecked() ? "内循环" : "外循环");
            }
        });
    }

    @Override
    protected void _update_ui(Integer value) {
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, "_update_ui: " + bundle.toString());
        //循环状态 0 内循环 1外循环
        _cb_wind_circulate.setChecked(bundle.getInt("value") == HvacSOAConstant.HVAC_WITHIN);
    }
}
