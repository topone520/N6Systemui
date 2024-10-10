package com.adayo.systemui.functional.statusbar._binder;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.soavb.utils.Dispatcher;
import com.adayo.systemui.utils.AA0P_CarLinkManager;
import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.R;
import com.carlinx.arcfox.carlink.arclink.ArcLinkManager;

public class StatusCarLinkViewBinder extends AbstractViewBinder<Integer> {

    private ImageView _icon_car_link;

    public StatusCarLinkViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }

    @Override
    protected void _bind_view(View view) {
        _icon_car_link = view.findViewById(R.id.icon_car_link);
        _icon_car_link.setOnClickListener(v -> {
            ArcLinkManager.getInstance().showDevicesEventCallback((int) _icon_car_link.getX(), (int) _icon_car_link.getY());
        });

        AA0P_CarLinkManager.getInstance().setOnStateListener2(new AA0P_CarLinkManager.OnStateListener2() {
            @Override
            public void onStateListener2(String s, int state) {
                LogUtil.d("onConnectStateChanged: " + s + " , " + state);
                Dispatcher.getInstance().dispatchToUI(() -> {
                    if (state == 2) {
                        _icon_car_link.setVisibility(View.VISIBLE);
                    } else {
                        _icon_car_link.setVisibility(View.GONE);
                    }
                });

            }
        });
    }

    @Override
    protected void _update_ui(Integer integer) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}