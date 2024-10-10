package com.adayo.systemui.functional.negative._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.VehicleS0AConstant;
import com.adayo.systemui.functional.negative._messageAction.MessageActions;
import com.adayo.systemui.proxy.vehicle.VehicleDoorService;
import com.adayo.systemui.windows.views.QsIconView;
import com.android.systemui.R;

public class NegativeTailgateViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = NegativeTailgateViewBinder.class.getSimpleName();
    private static final int DOOR_CONTROL_OPEN = 1;
    private static final int DOOR_CONTROL_CLOSE = 2;
    private QsIconView _qs_tailgate;

    public NegativeTailgateViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(VehicleDoorService.getInstance())
                .withGetMessageName(VehicleS0AConstant.MSG_GET_REAR_BACK_DOOR_CONTROLLER_STATE)
                .withSetMessageName(VehicleS0AConstant.MSG_SET_REAR_BACK_DOOR_CONTROLLER_STATE)
                .withEventMessageName(VehicleS0AConstant.MSG_EVENT_REAR_BACK_DOOR_CONTROLLER_STATE)
                .withInitialValue(-1)
                .build());
        // super(new ViewBinderProviderInteger(VehicleDoorService.getInstance(), VehicleS0AConstant.MSG_SET_REAR_BACK_DOOR_CONTROLLER_STATE, VehicleS0AConstant.MSG_GET_REAR_BACK_DOOR_CONTROLLER_STATE, VehicleS0AConstant.MSG_EVENT_REAR_BACK_DOOR_CONTROLLER_STATE));
    }

    @Override
    protected void _bind_view(View view) {
        _qs_tailgate = view.findViewById(R.id.qs_tailgate);
        _qs_tailgate.setListener(v -> _adjust_sensibility_mode(_qs_tailgate.isSelected() ? DOOR_CONTROL_CLOSE : DOOR_CONTROL_OPEN));
    }

    private void _adjust_sensibility_mode(int mode) {
        if (!MessageActions.getInstance().isEditType()){
            AAOP_LogUtils.i(TAG, "::adjustSensibilityMode(" + mode + ")" + "\tcurrent mode = " + _get_value());
            _set_value(mode);
        }

    }

    @Override
    protected void _update_ui(Integer value) {
        AAOP_LogUtils.i(TAG, "::_update_ui value: " + value);
        _qs_tailgate.setSelected(value == DOOR_CONTROL_OPEN);
        _qs_tailgate.setTextColor(value == DOOR_CONTROL_OPEN);
    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}