package com.adayo.systemui.functional.statusbar._binder;

import static com.adayo.systemui.contents.PublicContents.WIFI_AP_STATE_ENABLED;

import android.os.Bundle;
import android.view.View;

import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.bean.HotspotInfo;
import com.adayo.systemui.interfaces.BaseCallback;
import com.adayo.systemui.manager.HotspotControllerImpl;
import com.adayo.systemui.manager.PopupsManager;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.views.IconView;
import com.adayo.systemui.windows.views.PopupHotspotView;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class StatusHotspotViewBinder extends AbstractViewBinder<Integer> {

    private IconView _icon_hotspot;
    private PopupHotspotView _popupHotspotView;

    public StatusHotspotViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }

    @Override
    protected void _bind_view(View view) {
        _icon_hotspot = view.findViewById(R.id.icon_hotspot);
        _icon_hotspot.setTag(false);
        HotspotControllerImpl.getInstance().addCallback((BaseCallback<HotspotInfo>) this::updateHotspotView);
        _icon_hotspot.setOnClickListener(v -> {
            if (_popupHotspotView == null) {
                _popupHotspotView = new PopupHotspotView(SystemUIApplication.getSystemUIContext());
                _popupHotspotView.initHotspotData();
            }
            setSwitchPopupWindow(_popupHotspotView, 1900);
        });
    }

    private void updateHotspotView(HotspotInfo hotspotInfo) {
        if (null == hotspotInfo || hotspotInfo.getmNumConnectedDevices() < 0 || hotspotInfo.getmNumConnectedDevices() > 8) {
            return;
        }
        LogUtil.debugD("getmHotspotState = " + hotspotInfo.getmHotspotState()
                + " ; getmNumConnectedDevices = " + hotspotInfo.getmNumConnectedDevices());
        if (!(hotspotInfo.getmHotspotState() == WIFI_AP_STATE_ENABLED)) {
            _icon_hotspot.setVisibility(View.GONE);
        } else {
            int bgRes = R.mipmap.ivi_top_icon_hotspot_unlinked_n;
            int fgRes = R.mipmap.ivi_top_icon_hotspot_unlinked_n;
            switch (hotspotInfo.getmNumConnectedDevices()) {
                case 0:
                    bgRes = R.mipmap.ivi_top_icon_hotspot_unlinked_n;
                    fgRes = R.mipmap.ivi_top_icon_hotspot_unlinked_n;
                    break;
                case 1:
                    bgRes = R.mipmap.ivi_top_icon_hotspot_link_n;
                    fgRes = R.mipmap.ivi_top_icon_hotspot_numeral_1_n;
                    break;
                case 2:
                    bgRes = R.mipmap.ivi_top_icon_hotspot_link_n;
                    fgRes = R.mipmap.ivi_top_icon_hotspot_numeral_2_n;
                    break;
                case 3:
                    bgRes = R.mipmap.ivi_top_icon_hotspot_link_n;
                    fgRes = R.mipmap.ivi_top_icon_hotspot_numeral_3_n;
                    break;
                case 4:
                    bgRes = R.mipmap.ivi_top_icon_hotspot_link_n;
                    fgRes = R.mipmap.ivi_top_icon_hotspot_numeral_4_n;
                    break;
                case 5:
                    bgRes = R.mipmap.ivi_top_icon_hotspot_link_n;
                    fgRes = R.mipmap.ivi_top_icon_hotspot_numeral_5_n;
                    break;
                case 6:
                    bgRes = R.mipmap.ivi_top_icon_hotspot_link_n;
                    fgRes = R.mipmap.ivi_top_icon_hotspot_numeral_6_n;
                    break;
                case 7:
                    bgRes = R.mipmap.ivi_top_icon_hotspot_link_n;
                    fgRes = R.mipmap.ivi_top_icon_hotspot_numeral_7_n;
                    break;
                case 8:
                    bgRes = R.mipmap.ivi_top_icon_hotspot_link_n;
                    fgRes = R.mipmap.ivi_top_icon_hotspot_numeral_8_n;
                    break;
                default:
                    break;
            }
            _icon_hotspot.setBackgroundIcon(bgRes);
            _icon_hotspot.setForegroundIcon(fgRes, View.VISIBLE);
            _icon_hotspot.setVisibility(View.VISIBLE);
        }
    }

    private void setSwitchPopupWindow(View popupView, int x) {
        if ((boolean) _icon_hotspot.getTag()) {
            PopupsManager.getInstance().dismiss();
        } else {
            _icon_hotspot.post(() -> {
                PopupsManager.getInstance().showAtLocation(_icon_hotspot, popupView, x, 0, false, 10000, R.style.PopupWindowInOutAnimation, 2066, true);
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