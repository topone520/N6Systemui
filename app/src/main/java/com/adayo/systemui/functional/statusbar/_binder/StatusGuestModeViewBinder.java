package com.adayo.systemui.functional.statusbar._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.VehicleS0AConstant;
import com.adayo.systemui.proxy.vehicle.VehicleSceneService;
import com.android.systemui.R;

public class StatusGuestModeViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = StatusGuestModeViewBinder.class.getSimpleName();
    private static final int SCENE_MODE_OPEN = 1;
    private ImageView _guest_mode;

    public StatusGuestModeViewBinder() {
        super(TAG,new ViewBinderProviderInteger.Builder()
                .withService(VehicleSceneService.getInstance())
                .withGetMessageName(VehicleS0AConstant.MSG_GET_EXHIBITION_MODE)
                .withEventMessageName(VehicleS0AConstant.MSG_EVENT_EXHIBITION_MOED)
                .withInitialValue(-1)
                .build());
    }

    @Override
    protected void _bind_view(View view) {
        _guest_mode = view.findViewById(R.id.guest_mode);
    }

    @Override
    protected void _update_ui(Integer integer) {
        AAOP_LogUtils.i(TAG, "::_update_ui integer: " + integer);
        _guest_mode.setVisibility(integer == SCENE_MODE_OPEN ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.i(TAG, "::_update_ui bundle: " + bundle);
    }
}