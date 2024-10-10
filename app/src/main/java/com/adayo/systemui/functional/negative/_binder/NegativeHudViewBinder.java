package com.adayo.systemui.functional.negative._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.VehicleS0AConstant;
import com.adayo.systemui.proxy.vehicle.VehicleDriverService;
import com.adayo.systemui.windows.views.QsIconView;
import com.android.systemui.R;

public class NegativeHudViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = NegativeHudViewBinder.class.getSimpleName();
    //1开
    private static final int HUD_MODE_OPEN = 1;
    //0关
    private static final int HUD_MODE_CLOSE = 0;
    private QsIconView _qs_hud;

    public NegativeHudViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(VehicleDriverService.getInstance())
                .withGetMessageName(VehicleS0AConstant.MSG_GET_HUD_STATE)
                .withSetMessageName(VehicleS0AConstant.MSG_SET_HUD_STATE)
                .withEventMessageName(VehicleS0AConstant.MSG_EVENT_HUD_STATE)
                .withInitialValue(-1)
                .build());
    }

    @Override
    protected void _bind_view(View view) {
        _qs_hud = view.findViewById(R.id.qs_hud);
        _qs_hud.setListener(v -> hud_mode_adjustment(_qs_hud.isSelected() ? HUD_MODE_CLOSE : HUD_MODE_OPEN));

    }

    private void hud_mode_adjustment(int mode) {
        AAOP_LogUtils.i(TAG, "::hud_mode_adjustment(" + mode + ")" + "\tcurrent mode = " + _get_value());
        _set_value(mode);
    }

    @Override
    protected void _update_ui(Integer value) {
        AAOP_LogUtils.i(TAG, "::_update_ui value: " + value);
        _qs_hud.setSelected(value == HUD_MODE_OPEN);
        _qs_hud.setTextColor(value == HUD_MODE_OPEN);
    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}
