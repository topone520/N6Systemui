package com.adayo.systemui.functional.hvac._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.windows.views.hvac.VtpPMView;
import com.android.systemui.R;

public class VtpPMViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = VtpPMViewBinder.class.getSimpleName();
    private VtpPMView _pm_view;

    public VtpPMViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(HvacService.getInstance())
                .withGetMessageName(HvacSOAConstant.MSG_GET_AIR_QUALITY)
                .withEventMessageName(HvacSOAConstant.MSG_EVENT_AIR_QUALITY)
                .withInitialValue(0)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        _pm_view = view.findViewById(R.id.pm_view);
    }

    @Override
    protected void _update_ui(Integer value) {
        _pm_view._update_ui(value);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
    }
}
