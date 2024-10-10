package com.adayo.systemui.functional.negative._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.utils.TimeUtils;
import com.adayo.systemui.windows.views.QsIconView;
import com.android.systemui.R;

public class NegativeDarkViewBinder extends AbstractViewBinder<Integer> {
    private static final int DARK_MODE_OPEN = 3;
    QsIconView _qs_dark;

    public NegativeDarkViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }

    @Override
    protected void _bind_view(View view) {
        _qs_dark = view.findViewById(R.id.qs_dark);
        _qs_dark.setListener(v -> _adjust_dark_mode(AAOP_DeviceServiceManager.getInstance().getDayNightMode() == DARK_MODE_OPEN ? 2 : 3));
    }

    private void _adjust_dark_mode(int mode) {
        LogUtil.d("dayNightMode1:  "+mode);
        _qs_dark.setSelected(mode == DARK_MODE_OPEN);
        _qs_dark.setTextColor(mode == DARK_MODE_OPEN);
        AAOP_DeviceServiceManager.getInstance().setDayNightMode(mode);//1自动,2白天,3夜间
    }

    @Override
    protected void _update_ui(Integer value) {
    }

    @Override
    protected void _update_ui(Bundle bundle) {
    }
}