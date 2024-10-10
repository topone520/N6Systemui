package com.adayo.systemui.functional.statusbar._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.bean.WiFiInfo;
import com.adayo.systemui.interfaces.BaseCallback;
import com.adayo.systemui.manager.PopupsManager;
import com.adayo.systemui.manager.WiFiControllerImpl;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.views.PopupWifiView;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class StatusWifiViewBinder extends AbstractViewBinder<Integer> {

    private ImageView _icon_wifi;
    private PopupWifiView _popupWifiView;

    public StatusWifiViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }

    @Override
    protected void _bind_view(View view) {
        _icon_wifi = view.findViewById(R.id.icon_wifi);
        _icon_wifi.setTag(false);
        WiFiControllerImpl.getInstance().addCallback((BaseCallback<WiFiInfo>) this::updateWiFiView);
        _icon_wifi.setOnClickListener(v -> {
            if (_popupWifiView == null) {
                _popupWifiView = new PopupWifiView(SystemUIApplication.getSystemUIContext());
                _popupWifiView.initWiFiData();
            }
            setSwitchPopupWindow(_popupWifiView, 2240);
        });
    }

    private void updateWiFiView(WiFiInfo wiFiInfo) {
        if (null != wiFiInfo) {
            LogUtil.debugD("isWiFiEnable = " + wiFiInfo.isEnable()
                    + " ; isWiFiConnected = " + wiFiInfo.isConnected()
                    + " ; rssi = " + wiFiInfo.getRssi());
            if (wiFiInfo.isEnable()) {
                int res = R.drawable.selector_icon_wifi_zero;
                if (wiFiInfo.isConnected()) {
                    switch (wiFiInfo.getRssi()) {
                        case 0:
                            res = R.drawable.selector_icon_wifi_zero;
                            break;
                        case 1:
                            res = R.drawable.selector_icon_wifi_one;
                            break;
                        case 2:
                            res = R.drawable.selector_icon_wifi_two;
                            break;
                        case 3:
                            res = R.drawable.selector_icon_wifi_three;
                            break;
                        case 4:
                            res = R.drawable.selector_icon_wifi_four;
                            break;
                        default:
                            break;
                    }
                }
                _icon_wifi.setImageResource(res);
            } else {
                _icon_wifi.setImageResource(R.drawable.selector_icon_wifi_off);
            }
        }
    }

    private void setSwitchPopupWindow(View popupView, int x) {
        if ((boolean) _icon_wifi.getTag()) {
            PopupsManager.getInstance().dismiss();
        } else {
            _icon_wifi.post(() -> {
                PopupsManager.getInstance().showAtLocation(_icon_wifi, popupView, x, 0, false, 10000, R.style.PopupWindowInOutAnimation, 2066, true);
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