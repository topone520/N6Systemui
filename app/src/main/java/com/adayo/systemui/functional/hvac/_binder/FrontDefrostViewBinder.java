package com.adayo.systemui.functional.hvac._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.proxy.vehicle.Dimensional;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.utils.TimeUtils;
import com.android.systemui.R;

public class FrontDefrostViewBinder extends AbstractViewBinder<Integer> {

    private static final String TAG = FrontDefrostViewBinder.class.getSimpleName();
    private CheckBox _cb_wind;

    public FrontDefrostViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(HvacService.getInstance())
                .withGetMessageName(HvacSOAConstant.MSG_GET_FRONT_DEFROST)
                .withSetMessageName(HvacSOAConstant.MSG_SET_FRONT_DEFROST)
                .withEventMessageName(HvacSOAConstant.MSG_EVENT_FRONT_DEFROST)
                .withInitialValue(0)
                .withCompare(false)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        _cb_wind = view.findViewById(R.id.cb_wind_window);
        _cb_wind.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                AAOP_LogUtils.d(TAG, _cb_wind.isChecked() + "");
                _set_value(isChecked ? HvacSOAConstant.HVAC_ON_IO : HvacSOAConstant.HVAC_OFF_IO);
                if (isChecked) {
                    TimeUtils.startTimer();
                    ReportBcmManager.getInstance().sendHvacReport("8630008", "windwindow_turnonway", "中控屏打开空调吹窗");
                } else {
                    ReportBcmManager.getInstance().sendHvacReport("8630009", "windwindow_duration", TimeUtils.getTimeInSeconds());
                }
            }
        });
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, "value  =  " + bundle.toString());
        _cb_wind.setChecked(bundle.getInt("value") == AreaConstant.HVAC_ON_IO);
    }
}
