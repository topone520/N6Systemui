package com.adayo.systemui.functional.hvac._binder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.utils.TimeUtils;
import com.android.systemui.R;

import java.util.Arrays;
import java.util.HashSet;

public class ECOViewBinder extends AbstractViewBinder<Integer> {

    private static final String TAG = ECOViewBinder.class.getSimpleName();
    private TextView _tv_eco;
    private boolean _is_eco_open = true;
    private static final HashSet<String> _hash_set = new HashSet<>(Arrays.asList(HvacSOAConstant.MSG_EVENT_ECO_STATE, HvacSOAConstant.MSG_EVENT_DRIVE_MODE));

    public ECOViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(HvacService.getInstance())
                .withGetMessageName(HvacSOAConstant.MSG_GET_ECO_STATE)
                .withSetMessageName(HvacSOAConstant.MSG_SET_ECO)
                .withRelationMessages(_hash_set)
                .withInitialValue(0)
                .withCompare(false)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        _tv_eco = view.findViewById(R.id.tv_eco);
        _tv_eco.setOnClickListener(v -> {
            Log.d(TAG, "bindView: eco  eco ------------");
            _is_eco_open = !_is_eco_open;
            _tv_eco.setSelected(_is_eco_open);
            _set_value(_is_eco_open ? HvacSOAConstant.HVAC_ON_IO : HvacSOAConstant.HVAC_OFF_IO);
            if (_is_eco_open) {
                TimeUtils.startTimer();
                ReportBcmManager.getInstance().sendHvacReport("8630049", "", "开启空调经济模式");
            } else {
                ReportBcmManager.getInstance().sendHvacReport("8630050", "air_Economic mode_duration", TimeUtils.getSyncJson());
            }
        });
    }

    @Override
    protected void _update_ui(Integer value) {
        AAOP_LogUtils.d(TAG, "_update_ui: " + value);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, "bundle = " + bundle.toString());
        String message_id = bundle.getString("message_id");
        int value = bundle.getInt("value");
        switch (message_id) {
            case HvacSOAConstant.MSG_EVENT_ECO_STATE:
                _is_eco_open = value == AreaConstant.HVAC_ON_IO;
                _tv_eco.setSelected(_is_eco_open);
                break;
            case HvacSOAConstant.MSG_EVENT_DRIVE_MODE:
                _tv_eco.setClickable(value != 11);
                break;
        }
    }
}
