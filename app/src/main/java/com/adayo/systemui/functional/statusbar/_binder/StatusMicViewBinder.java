package com.adayo.systemui.functional.statusbar._binder;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.android.systemui.R;

import java.util.Objects;

public class StatusMicViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = StatusMicViewBinder.class.getSimpleName();
    private ImageView _icon_mic;

    public StatusMicViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder().build());
    }

    @Override
    protected void _bind_view(View view) {
        _icon_mic = view.findViewById(R.id.icon_mic);
        PrivacyPermissionView.getInstance().setOnMicrophoneListener(bundle -> {
            AAOP_LogUtils.d(TAG, "bundle: " + bundle);
            String appName = bundle.getString("appName");
            String permission = bundle.getString("permission");
            String code = bundle.getString("code");
            String packageName = bundle.getString("packageName");
            if (Objects.requireNonNull(permission).equals(Constant.MICROPHONE)){
                if (Objects.requireNonNull(code).equals(Constant.ENABLE_ONE_YEAR)){
                    _icon_mic.setVisibility(View.VISIBLE);
                }else if (Objects.requireNonNull(code).equals(Constant.DISABLE)){
                    _icon_mic.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    protected void _update_ui(Integer integer) {
    }

    @Override
    protected void _update_ui(Bundle bundle) {
    }
}