package com.adayo.systemui.functional.statusbar._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.bean.VolumeInfo;
import com.adayo.systemui.interfaces.BaseCallback;
import com.adayo.systemui.manager.PopupsManager;
import com.adayo.systemui.manager.VolumeControllerImpl;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.views.PopupVolumeView;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class StatusVolumeViewBinder extends AbstractViewBinder<Integer> {

    private ImageView _icon_volume;
    private PopupVolumeView _popupVolumeView;

    public StatusVolumeViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }

    @Override



    protected void _bind_view(View view) {
        _icon_volume = view.findViewById(R.id.icon_volume);
        _icon_volume.setTag(false);
        VolumeControllerImpl.getInstance().addCallback((BaseCallback<VolumeInfo>) this::updateDockVolume);
        _icon_volume.setOnClickListener(v -> {
            _popupVolumeView = new PopupVolumeView(SystemUIApplication.getSystemUIContext());
            setSwitchPopupWindow(_popupVolumeView, 2130);




        });
    }

    private void updateDockVolume(VolumeInfo volumeInfo) {
        if (null != volumeInfo) {
            int mediaVol = volumeInfo.getMediaVolume();
            LogUtil.d("setDockVolume media vol value:" + mediaVol);
            int resId = R.drawable.selector_icon_volume_mute;
            if (mediaVol == 0) {
                resId = R.drawable.selector_icon_volume_mute;
            } else if (mediaVol >= 1 && mediaVol <= 13) {
                resId = R.drawable.selector_icon_volume_one;
            } else if (mediaVol >= 14 && mediaVol <= 26) {
                resId = R.drawable.selector_icon_volume_two;
            } else if (mediaVol >= 27) {
                resId = R.drawable.selector_icon_volume_three;
            }
            _icon_volume.setImageResource(resId);
        }
    }

    private void setSwitchPopupWindow(View popupView, int x) {
        if ((boolean) _icon_volume.getTag()) {
            PopupsManager.getInstance().dismiss();
        } else {
            _icon_volume.post(() -> {
                PopupsManager.getInstance().showAtLocation(_icon_volume, popupView, x, 0, false, 10000, R.style.PopupWindowInOutAnimation, 2066, true);
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
