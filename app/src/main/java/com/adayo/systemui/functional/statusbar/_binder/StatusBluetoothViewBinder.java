package com.adayo.systemui.functional.statusbar._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.bean.BluetoothInfo;
import com.adayo.systemui.interfaces.BaseCallback;
import com.adayo.systemui.manager.BluetoothControllerImpl;
import com.adayo.systemui.manager.PopupsManager;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.views.PopupBtView;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class StatusBluetoothViewBinder extends AbstractViewBinder<Integer> {

    private ImageView _icon_bluetooth;
    private PopupBtView _popupBtView;

    public StatusBluetoothViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }

    @Override
    protected void _bind_view(View view) {
        _icon_bluetooth = view.findViewById(R.id.icon_bluetooth);
        _icon_bluetooth.setTag(false);
        BluetoothControllerImpl.getInstance().addCallback((BaseCallback<BluetoothInfo>) this::updateBluetoothView);
        _icon_bluetooth.setOnClickListener(v -> {
            if (_popupBtView == null) {
                _popupBtView = new PopupBtView(SystemUIApplication.getSystemUIContext());
            }
            setSwitchPopupWindow(_popupBtView, 2312);
        });
    }

    private void updateBluetoothView(BluetoothInfo data) {
        if (null != data) {
            LogUtil.d("BluetoothControllerImpl   isEnable = " + data.isEnable() + " ; getBTDevicesName = " + data.getDeviceName() + " ; isMount = " + (data.isA2dpMount() || data.isHfpMount()));
            _icon_bluetooth.setActivated(data.isEnable());
            _icon_bluetooth.setSelected(data.isA2dpMount() || data.isHfpMount());
        }
    }

    private void setSwitchPopupWindow(View popupView, int x) {
        if ((boolean) _icon_bluetooth.getTag()) {
            PopupsManager.getInstance().dismiss();
        } else {
            _icon_bluetooth.post(() -> {
                PopupsManager.getInstance().showAtLocation(_icon_bluetooth, popupView, x, 0, false, 10000, R.style.PopupWindowInOutAnimation, 2066, true);
            });
        }
    }

    @Override
    protected void _update_ui(Integer integer) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}