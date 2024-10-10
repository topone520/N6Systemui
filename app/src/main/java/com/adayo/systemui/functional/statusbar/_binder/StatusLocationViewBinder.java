package com.adayo.systemui.functional.statusbar._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.bean.GpsInfo;
import com.adayo.systemui.interfaces.BaseCallback;
import com.adayo.systemui.manager.GpsControllerImpl;
import com.android.systemui.R;

public class StatusLocationViewBinder extends AbstractViewBinder<Integer> {

    private View _icon_location;

    public StatusLocationViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }

    @Override
    protected void _bind_view(View view) {
        _icon_location = view.findViewById(R.id.icon_location);
        GpsControllerImpl.getInstance().addCallback((BaseCallback<GpsInfo>) data -> _icon_location.setVisibility(data.getGpsShow() ? View.VISIBLE : View.GONE));
    }

    @Override
    protected void _update_ui(Integer integer) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}