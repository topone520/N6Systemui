package com.adayo.systemui.functional.negative._binder;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.VehicleS0AConstant;
import com.adayo.systemui.proxy.vehicle.VehicleLightService;
import com.adayo.systemui.windows.views.QsIconView;
import com.android.systemui.R;

public class NegativeLampViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = NegativeLampViewBinder.class.getSimpleName();
    private QsIconView _qs_lamp;
    private static final int LAMP_MODE_OPEN = 1;
    private static final int LAMP_MODE_CLOSE = 2;

    public NegativeLampViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(VehicleLightService.getInstance())
                .withGetMessageName(VehicleS0AConstant.MSG_GET_HIGH_BEAM_LIGHTS)
                .withSetMessageName(VehicleS0AConstant.MSG_SET_HIGH_BEAM_LIGHTS)
                .withEventMessageName(VehicleS0AConstant.MSG_EVENT_HIGH_BEAM_LIGHTS)
                .withInitialValue(-1)
                .build());
        //super(new ViewBinderProviderInteger(VehicleLightService.getInstance(), VehicleS0AConstant.MSG_SET_HIGH_BEAM_LIGHTS, VehicleS0AConstant.MSG_GET_HIGH_BEAM_LIGHTS  , VehicleS0AConstant.MSG_EVENT_HIGH_BEAM_LIGHTS));
    }

    @Override
    protected void _bind_view(View view) {
        _qs_lamp = view.findViewById(R.id.qs_lamp);
        _qs_lamp.setListener(v -> _adjust_logo_mode(_qs_lamp.isSelected() ? LAMP_MODE_OPEN : LAMP_MODE_CLOSE));
    }

    private void _adjust_logo_mode(int mode) {
        AAOP_LogUtils.i(TAG, "::adjustSensibilityMode(" + mode + ")" + "\tcurrent mode = " + _get_value());
        _set_value(mode);
    }

    @Override
    protected void _update_ui(Integer value) {
        AAOP_LogUtils.i(TAG, "::_update_ui1(" + value);
        _qs_lamp.setSelected(value == LAMP_MODE_CLOSE);
        _qs_lamp.setTextColor(value == LAMP_MODE_CLOSE);

    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}