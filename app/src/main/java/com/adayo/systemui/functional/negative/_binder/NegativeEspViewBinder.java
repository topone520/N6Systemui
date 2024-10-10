package com.adayo.systemui.functional.negative._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.VehicleS0AConstant;
import com.adayo.systemui.proxy.vehicle.VehicleDriverService;
import com.adayo.systemui.windows.views.QsIconView;
import com.android.systemui.R;

public class NegativeEspViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = NegativeEspViewBinder.class.getSimpleName();
    private QsIconView _qs_esp;
    private static final int THE_BODY_MODE_OPEN = 1;
    private static final int THE_BODY_MODE_CLOSE = 0;

    public NegativeEspViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(VehicleDriverService.getInstance())
                .withGetMessageName(VehicleS0AConstant.MSG_GET_ESP_STATE)
                .withSetMessageName(VehicleS0AConstant.MSG_SET_ESP_STATE)
                .withEventMessageName(VehicleS0AConstant.MSG_EVENT_ESP_STATE)
                .withInitialValue(-1)
                .build());
    }

    @Override
    protected void _bind_view(View view) {
        _qs_esp = view.findViewById(R.id.qs_esp);
        _qs_esp.setListener(v -> the_body_stable(_qs_esp.isSelected() ? THE_BODY_MODE_CLOSE : THE_BODY_MODE_OPEN));
    }

    private void the_body_stable(int mode) {
        AAOP_LogUtils.i(TAG, "::the_body_stable(" + mode + ")" + "\tcurrent mode = " + _get_value());
        _set_value(mode);
    }

    @Override
    protected void _update_ui(Integer value) {
        AAOP_LogUtils.i(TAG, "::_update_ui value: " + value);
        _qs_esp.setSelected(value == THE_BODY_MODE_OPEN);
        _qs_esp.setTextColor(value == THE_BODY_MODE_OPEN);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
    }
}
