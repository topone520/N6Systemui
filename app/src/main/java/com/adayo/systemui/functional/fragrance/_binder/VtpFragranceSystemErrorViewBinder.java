package com.adayo.systemui.functional.fragrance._binder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.FragranceSOAConstant;
import com.adayo.systemui.proxy.vehicle.FragranceService;

public class VtpFragranceSystemErrorViewBinder extends AbstractViewBinder<Integer> {

    private static final String TAG = VtpFragranceSystemErrorViewBinder.class.getSimpleName();
    private RelativeLayout fragSystemErrorView; //警告-香氛系统故障
    private final int _resource_id;

    public VtpFragranceSystemErrorViewBinder(int resourceId) {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(FragranceService.getInstance())
                .withEventMessageName(FragranceSOAConstant.MSG_EVENT_FRAGRANCE_SYSTEM_ERROR)
                .withInitialValue(0)
                .build());
        _resource_id = resourceId;
    }

    @Override
    public void _bind_view(View view) {
        fragSystemErrorView = view.findViewById(_resource_id);
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {
        int value = bundle.getInt("value");
        fragSystemErrorView.setVisibility(value == FragranceSOAConstant.FRAGRANCE_ERROR ? View.VISIBLE : View.GONE);
        Log.d(TAG, "_update_ui: value = " + value);
    }
}
