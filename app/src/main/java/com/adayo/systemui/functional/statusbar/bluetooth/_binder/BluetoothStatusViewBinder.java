package com.adayo.systemui.functional.statusbar.bluetooth._binder;

import android.view.View;

import com.adayo.bpresenter.bluetooth.constants.BusinessConstants;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractBTViewBinder;
import com.adayo.soavb.service.bluetooth.BTCallbackListener;
import com.adayo.soavb.service.bluetooth.BT_CALLBACK_TYPE;
import com.adayo.systemui.windows.views.SwitchButtonVe;
import com.android.systemui.R;

public class BluetoothStatusViewBinder extends AbstractBTViewBinder {

    private static final String TAG = BluetoothStatusViewBinder.class.getSimpleName();

    private SwitchButtonVe _bt_switch_btn;//控件

    @Override
    public void bindView(View view) {
        _bt_switch_btn = view.findViewById(R.id.bt_switch_btn);
        _bt_switch_btn.setChecked(getBluetoothService().getSettingsPresenter().isBluetoothEnable());
        _bt_switch_btn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            _bt_switch_btn.setEnabled(false);
            getBluetoothService().setBluetoothEnable(isChecked);
        });
        super.registerCallBack(BT_CALLBACK_TYPE.UPDATE_BLUETOOTH_STATE_CHANGE, (BTCallbackListener<BusinessConstants.BTStateType>) type -> {
            AAOP_LogUtils.i(TAG, "UPDATE_BLUETOOTH_STATE_CHANGE" + type + ",enable:" + getBluetoothService().getSettingsPresenter().isBluetoothEnable());

            if (type == BusinessConstants.BTStateType.TURN_OFF || type == BusinessConstants.BTStateType.TURN_ON) {
                if (_bt_switch_btn.isChecked() != (type == BusinessConstants.BTStateType.TURN_ON)) {
                    _bt_switch_btn.setChecked(type == BusinessConstants.BTStateType.TURN_ON);
                }
                _bt_switch_btn.setEnabled(true);
            } else {
                _bt_switch_btn.setEnabled(false);
            }
            if (type == BusinessConstants.BTStateType.TURN_ON) {
                getBluetoothService().getSettingsPresenter().setTwinConnectEnable(false);
                getBluetoothService().getSettingsPresenter().setBluetoothAutoReconnectEnable(true);
                getBluetoothService().getSettingsPresenter().setLocalBtDiscoverable(true);
            }
        });
    }
}
