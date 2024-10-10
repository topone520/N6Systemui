package com.adayo.systemui.functional.fragrance._binder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.FragranceSOAConstant;
import com.adayo.systemui.proxy.vehicle.FragranceService;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.windows.views.fragrance.ChannelView;
import com.android.systemui.R;

//香氛当前槽位&&香氛开关
public class ConcentrationViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = ConcentrationViewBinder.class.getSimpleName();
    private ChannelView _channel_view;

    public ConcentrationViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(FragranceService.getInstance())
                .withGetMessageName(FragranceSOAConstant.MSG_GET_FRAGRANCE_CONCENTRATION)
                .withSetMessageName(FragranceSOAConstant.MSG_CHANGE_FRAGRANCE_CONCENTRATION)
                .withEventMessageName(FragranceSOAConstant.MSG_EVENT_FRAGRANCE_CONCENTRATION)
                .withInitialValue(0)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        _channel_view = view.findViewById(R.id.channel_view);
        _channel_view.setChannelConcentrationListener(status -> {
            AAOP_LogUtils.d(TAG, " status = " + status);
            _set_value(status);
        });
    }

    @Override
    protected void _update_ui(Integer value) {
        Log.d(TAG, "_update_ui with value: --------- = " + value);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG," bundle = "+bundle.toString());
        _channel_view.updateConcentration(bundle.getInt("value",-1));
    }
}
