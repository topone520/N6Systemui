package com.adayo.systemui.functional.hvac._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.utils.TimeUtils;
import com.adayo.systemui.utils.ViewStyleUtils;
import com.android.systemui.R;

public class ACViewBinder extends AbstractViewBinder<Integer> {

    private static final String TAG = ACViewBinder.class.getSimpleName();
    private TextView _tv_ac;
    private boolean _is_ac_open;

    public ACViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(HvacService.getInstance())
                .withGetMessageName(HvacSOAConstant.MSG_GET_AC_STATE)
                .withSetMessageName(HvacSOAConstant.MSG_SET_AC)
                .withEventMessageName(HvacSOAConstant.MSG_EVENT_AC_STATE)
                .withInitialValue(0)
                .withCompare(false)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        _tv_ac = view.findViewById(R.id.tv_ac);
        _tv_ac.setOnClickListener(v -> {
            _is_ac_open = !_is_ac_open;
            _tv_ac.setSelected(_is_ac_open);
            _set_value(_is_ac_open ? HvacSOAConstant.HVAC_ON_IO : HvacSOAConstant.HVAC_OFF_IO);
            if (_is_ac_open) {
                TimeUtils.startTimer();
                ReportBcmManager.getInstance().sendHvacReport("8630013", "", "开启AC（空调制冷）");
            } else {
                ReportBcmManager.getInstance().sendHvacReport("8630014", "air_ac_duration", TimeUtils.getTimeInSeconds());
            }
        });
    }

    @Override
    protected void _update_ui(Integer value) {
        AAOP_LogUtils.d(TAG, "_update_ui: " + value);
        _is_ac_open = value == AreaConstant.HVAC_ON_IO;
        _tv_ac.setSelected(_is_ac_open);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
    }
}
