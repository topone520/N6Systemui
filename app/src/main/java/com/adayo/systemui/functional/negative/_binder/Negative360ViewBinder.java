package com.adayo.systemui.functional.negative._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.functional.negative._messageAction.MessageActions;
import com.adayo.systemui.windows.views.QsIconView;
import com.android.systemui.R;

public class Negative360ViewBinder extends AbstractViewBinder<Integer> {
    QsIconView _qs_360;

    public Negative360ViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }

    @Override
    protected void _bind_view(View view) {
        _qs_360 = view.findViewById(R.id.qs_360);
        _qs_360.setListener(v -> {
            if (!MessageActions.getInstance().isEditType()){
                _qs_360.setSelected(!_qs_360.isSelected());
                _qs_360.setTextColor(_qs_360.isSelected());
                Bundle bundle = new Bundle();
                bundle.putString("SourceType", "ADAYO_SOURCE_AVM");
                bundle.putString("VOICE_CONTROL", "AVM_ON");
                AAOP_DeviceServiceManager.getInstance().setDeviceFuncUniversalInterface("CameraDevice", "open_panorama", bundle);
            }

        });
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}