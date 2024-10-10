package com.adayo.systemui.functional.hvac._binder;

import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.proxy.vehicle.Dimensional;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.utils.TimeUtils;
import com.adayo.systemui.utils.ViewStyleUtils;
import com.android.systemui.R;

import javax.sql.DataSource;

public class PurificationViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = PurificationViewBinder.class.getSimpleName();
    private LinearLayout _linear_air_pur;
    private TextView _tv_pur_air;
    private boolean _is_pur_open;

    public PurificationViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(HvacService.getInstance())
                .withGetMessageName(HvacSOAConstant.MSG_GET_AIR_PURIFIER_SWITCH)
                .withSetMessageName(HvacSOAConstant.MSG_SET_AIR_PURIFIER_SWITCH)
                .withEventMessageName(HvacSOAConstant.MSG_EVENT_AIR_PURIFIER_SWITCH)
                .withInitialValue(0)
                .withCompare(false)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        _linear_air_pur = view.findViewById(R.id.cb_pur_air);
        _tv_pur_air = view.findViewById(R.id.tv_pur_air);
        _linear_air_pur.setOnClickListener(v -> {
            _is_pur_open = !_is_pur_open;
            _linear_air_pur.setSelected(_is_pur_open);
            _tv_pur_air.setSelected(_is_pur_open);
            _set_value(_is_pur_open ? HvacSOAConstant.HVAC_ON_IO : HvacSOAConstant.HVAC_OFF_IO);
            if (_is_pur_open) {
                TimeUtils.startTimer();
                ReportBcmManager.getInstance().sendHvacReport("8630024", "", "开启空气净化功能(手动)");
            } else {
                ReportBcmManager.getInstance().sendHvacReport("8630025", "wind_pure_duration", TimeUtils.getTimeInSeconds() + "");
            }
            Dimensional.getInstance().doAction("DataSource.Model.AirClear", _is_pur_open ? "1" : "0");
        });
    }

    @Override
    protected void _update_ui(Integer value) {
        AAOP_LogUtils.d(TAG, "value = " + value);
        _is_pur_open = value == AreaConstant.HVAC_ON_IO;
        _linear_air_pur.setSelected(_is_pur_open);
        _tv_pur_air.setSelected(_is_pur_open);

    }

    @Override
    protected void _update_ui(Bundle bundle) {
    }
}
