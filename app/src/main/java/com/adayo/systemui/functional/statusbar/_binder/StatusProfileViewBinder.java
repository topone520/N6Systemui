package com.adayo.systemui.functional.statusbar._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.soavb.utils.Dispatcher;
import com.adayo.systemui.utils.AAOP_SceneConnectProxy;
import com.android.systemui.R;

public class StatusProfileViewBinder extends AbstractViewBinder<Integer> {

    private static final String TAG = StatusProfileViewBinder.class.getSimpleName();

    private ImageView _icon_profile;

    public StatusProfileViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }

    @Override
    protected void _bind_view(View view) {
        _icon_profile = view.findViewById(R.id.icon_profile);
        AAOP_SceneConnectProxy.getInstance().setOnSceneOpenListener(new AAOP_SceneConnectProxy.onSceneOpenListener() {
            @Override
            public void onSceneOpen(String sceneName, int sceneStatus) {
                AAOP_LogUtils.d(TAG,"onSceneOpen");
                Dispatcher.getInstance().dispatchToUI(() -> {
                    AAOP_LogUtils.d(TAG,"onSceneOpen sceneStatus: "+sceneStatus);
                    _icon_profile.setVisibility(sceneStatus == 1 ? View.VISIBLE : View.GONE);
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