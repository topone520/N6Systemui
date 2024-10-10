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

public class HvacSwitchViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = HvacSwitchViewBinder.class.getSimpleName();
    private CheckBox _cb_hvac_switch;

    public HvacSwitchViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(HvacService.getInstance())
                .withGetMessageName(HvacSOAConstant.MSG_GET_STATE)
                .withSetMessageName(HvacSOAConstant.MSG_SET_STATE)
                .withEventMessageName(HvacSOAConstant.MSG_EVENT_FAN_SPEED)
                .withInitialValue(0)
                .withCompare(false)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        _cb_hvac_switch = view.findViewById(R.id.cb_hvac_switch);
        _cb_hvac_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                _set_value(_cb_hvac_switch.isChecked() ? AreaConstant.HVAC_SWITCH_OPEN : AreaConstant.HVAC_SWITCH_CLOSE);
                if (_cb_hvac_switch.isChecked()) {
                    ReportBcmManager.getInstance().sendHvacReport("8630002", "air_turnonway", "中控屏开启空调");
                }
            }
        });
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, " bundle = " + bundle.toString());
        _cb_hvac_switch.setChecked(bundle.getInt("value") != HvacSOAConstant.HVAC_OFF_IO);
    }
}
