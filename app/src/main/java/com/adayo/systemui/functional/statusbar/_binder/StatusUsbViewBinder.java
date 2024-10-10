package com.adayo.systemui.functional.statusbar._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.bean.USBInfo;
import com.adayo.systemui.interfaces.BaseCallback;
import com.adayo.systemui.manager.USBControllerImpl;
import com.android.systemui.R;

public class StatusUsbViewBinder extends AbstractViewBinder<Integer> {

    private ImageView _icon_usb;

    public StatusUsbViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }

    @Override
    protected void _bind_view(View view) {
        _icon_usb = view.findViewById(R.id.icon_usb);
        USBControllerImpl.getInstance().addCallback((BaseCallback<USBInfo>) data -> _icon_usb.setVisibility(data.isInsert() ? View.VISIBLE : View.GONE));
    }

    @Override
    protected void _update_ui(Integer integer) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}