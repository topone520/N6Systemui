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

public class SYNCViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = SYNCViewBinder.class.getSimpleName();
    private TextView _tv_sync;
    private boolean _is_sync_open = true;

    public SYNCViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(HvacService.getInstance())
                .withGetMessageName(HvacSOAConstant.MSG_GET_SYNC_STATE)
                .withSetMessageName(HvacSOAConstant.MSG_SET_SYNC)
                .withEventMessageName(HvacSOAConstant.MSG_EVENT_SYNC_STATE)
                .withInitialValue(0)
                .withCompare(false)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        _tv_sync = view.findViewById(R.id.tv_sync);
        _tv_sync.setOnClickListener(v -> {
            _is_sync_open = !_is_sync_open;
            _tv_sync.setSelected(_is_sync_open);
            _set_value(_is_sync_open ? HvacSOAConstant.HVAC_ON_IO : HvacSOAConstant.HVAC_OFF_IO);
            if (_is_sync_open) {
                TimeUtils.startTimer();
                ReportBcmManager.getInstance().sendHvacReport("8630037", "", "开启双温区同步功能");
            } else {
                ReportBcmManager.getInstance().sendHvacReport("8630038", "", TimeUtils.getSyncJson());
            }
        });
    }

    @Override
    protected void _update_ui(Integer value) {
        AAOP_LogUtils.d(TAG, "value = " + value);
        _is_sync_open = value == AreaConstant.HVAC_ON_IO;
        _tv_sync.setSelected(_is_sync_open);
    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}
