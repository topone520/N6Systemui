package com.adayo.systemui.functional.hvac._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class AUTOViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = AUTOViewBinder.class.getSimpleName();
    private TextView _tv_auto;
    private boolean _is_auto_open;
    private static final HashSet<String> _event_list = new HashSet<>(Arrays.asList(HvacSOAConstant.MSG_EVENT_AUTO_STATE, HvacSOAConstant.MSG_EVENT_POWER_VALUE));

    public AUTOViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(HvacService.getInstance())
                .withGetMessageName(HvacSOAConstant.MSG_GET_AUTO_STATE)
                .withSetMessageName(HvacSOAConstant.MSG_SET_AUTO)
                .withRelationMessages(_event_list)
                .withInitialValue(0)
                .withCompare(false)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        _tv_auto = view.findViewById(R.id.tv_auto);
        _tv_auto.setOnClickListener(v -> {
            if (_is_auto_open) return;
            _is_auto_open = true;
            _tv_auto.setSelected(true);
            _set_value(HvacSOAConstant.HVAC_SWITCH_SIGNAL);
            if (_is_auto_open) {
                ReportBcmManager.getInstance().sendHvacReport("8630011", "", "开启AUTO");
            } else {
                ReportBcmManager.getInstance().sendHvacReport("8630012", "air_auto_turnoffway", "关闭AUTO");
            }
        });
    }

    @Override
    protected void _update_ui(Integer value) {
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, "bundle------ = " + bundle.toString());
        switch (Objects.requireNonNull(bundle.getString("message_id"))) {
            case HvacSOAConstant.MSG_EVENT_AUTO_STATE:
                _is_auto_open = bundle.getInt("value") == AreaConstant.HVAC_ON_IO;
                _tv_auto.setSelected(_is_auto_open);
                break;
            case HvacSOAConstant.MSG_EVENT_POWER_VALUE:
                _tv_auto.setEnabled(bundle.getInt("value") == HvacSOAConstant.HVAC_POWER_MODE_IGN_ON);
                break;
        }
    }
}
