package com.adayo.systemui.functional.statusbar._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.VehicleS0AConstant;
import com.adayo.systemui.proxy.vehicle.VehicleDriverService;
import com.android.systemui.R;

public class StatusWirelessViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = StatusWirelessViewBinder.class.getSimpleName();
    private static final int SCENE_MODE_OPEN = 1;
    private ImageView _icon_wireless;

    public StatusWirelessViewBinder() {
        super(TAG,new ViewBinderProviderInteger.Builder()
                .withService(VehicleDriverService.getInstance())
                .withGetMessageName(VehicleS0AConstant.MSG_GET_PHONE_STATE)
                .withEventMessageName(VehicleS0AConstant.MSG_EVENT_CHARGE_STATE)
                .withInitialValue(-1)
                .build());
    }

    @Override
    protected void _bind_view(View view) {
        _icon_wireless = view.findViewById(R.id.icon_wireless);
    }

    @Override
    protected void _update_ui(Integer integer) {
        AAOP_LogUtils.i(TAG, "::_update_ui integer: " + integer);
        _icon_wireless.setVisibility(integer == SCENE_MODE_OPEN ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.i(TAG, "::_update_ui bundle: " + bundle);
    }
}