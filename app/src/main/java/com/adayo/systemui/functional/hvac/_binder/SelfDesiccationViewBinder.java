package com.adayo.systemui.functional.hvac._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.proxy.vehicle.Dimensional;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.android.systemui.R;

public class SelfDesiccationViewBinder extends AbstractViewBinder<Integer> {

    private static final String TAG = SelfDesiccationViewBinder.class.getSimpleName();
    private ImageView _iv_air_dry;
    private TextView _tv_air_dry;

    public SelfDesiccationViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(HvacService.getInstance())
                .withGetMessageName(HvacSOAConstant.MSG_GET_FAN_SPEED_VALUE)
                .withEventMessageName(HvacSOAConstant.MSG_EVENT_FAN_SPEED)
                .withInitialValue(0)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        _iv_air_dry = view.findViewById(R.id.iv_air_dry);
        _tv_air_dry = view.findViewById(R.id.tv_air_dry);
    }

    @Override
    protected void _update_ui(Integer value) {
        AAOP_LogUtils.d(TAG, "value = " + value);
        _iv_air_dry.setSelected(value > 0);
        _tv_air_dry.setSelected(value > 0);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
    }
}
