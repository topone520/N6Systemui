package com.adayo.systemui.functional.hvac._binder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusErrorCodeEnum;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.windows.views.hvac.HvacWinSpeedImageView;
import com.android.systemui.R;

public class ShortcutWinSpeedViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = ShortcutWinSpeedViewBinder.class.getSimpleName();
    private HvacWinSpeedImageView _iv_speed_adjust;
    private final Bundle _bundle = new Bundle();

    public ShortcutWinSpeedViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(HvacService.getInstance())
                .withGetMessageName(HvacSOAConstant.MSG_GET_FAN_SPEED_VALUE)
                .withSetMessageName(HvacSOAConstant.MSG_SET_FAN_SPEED_VALUE_ACTION)
                .withEventMessageName(HvacSOAConstant.MSG_EVENT_FAN_SPEED)
                .withInitialValue(0)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        _iv_speed_adjust = view.findViewById(R.id.iv_speed_adjust);
        _iv_speed_adjust.setListener(level -> {
            _bundle.clear();
            _bundle.putInt("action", AreaConstant.HVAC_TEMP_ACTION_SPECIFIC); //具体值
            _bundle.putInt("value", level);
            boolean resultWinSpeed = HvacService.getInstance().invoke(HvacSOAConstant.MSG_SET_FAN_SPEED_VALUE_ACTION, _bundle, new ADSBusReturnValue());
            AAOP_LogUtils.d(TAG, "windSpeed: " + resultWinSpeed);
        });
    }

    @Override
    protected void _update_ui(Integer value) {
        _iv_speed_adjust._update_ui(value);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
    }
}
