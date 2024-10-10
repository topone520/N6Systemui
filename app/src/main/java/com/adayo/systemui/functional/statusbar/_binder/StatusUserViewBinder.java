package com.adayo.systemui.functional.statusbar._binder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.manager.SourceControllerImpl;
import com.android.systemui.R;
import com.baic.icc.account.api.AccountManager;
import com.baic.icc.account.api.UserInfo;

import java.util.HashMap;


public class StatusUserViewBinder extends AbstractViewBinder<Integer> {

    private ImageView _icon_user;
    private TextView _tv_user;

    public StatusUserViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }

    @Override
    protected void _bind_view(View view) {
        _icon_user = view.findViewById(R.id.icon_user);
        _tv_user = view.findViewById(R.id.tv_user);
        updateUI();
        _icon_user.setOnClickListener(v -> {
            SourceControllerImpl.getInstance().requestSoureApp("ADAYO_SOURCE_ACCOUNT", "ADAYO_SOURCE_ACCOUNT", AppConfigType.SourceSwitch.APP_ON.getValue(), new HashMap<>());
        });
        _tv_user.setOnClickListener(v -> {
            SourceControllerImpl.getInstance().requestSoureApp("ADAYO_SOURCE_ACCOUNT", "ADAYO_SOURCE_ACCOUNT", AppConfigType.SourceSwitch.APP_ON.getValue(), new HashMap<>());
        });
        registerReceiver(_icon_user.getContext());
    }

    private void registerReceiver(Context context) {
        LogStateBroadcastReceiver receiver = new LogStateBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AccountManager.LOGIN_STATE_ACTION);
        intentFilter.addAction(AccountManager.LOGOUT_STATE_ACTION);
        context.registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void _update_ui(Integer integer) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }

    private void updateUI() {
        UserInfo userInfo = AccountManager.getUserInfo();
        int loginStatus = userInfo.getLoginStatus();
        if (loginStatus == 2) {
            _icon_user.setImageResource(Integer.parseInt(userInfo.getAvatar()));
            _tv_user.setText(userInfo.getNickName());
        } else {
            _icon_user.setImageResource(R.drawable.selector_icon_user_unlogin);
            _tv_user.setText("未登录");
        }
    }

    class LogStateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case AccountManager.LOGIN_STATE_ACTION:
                    //登录
                case AccountManager.LOGOUT_STATE_ACTION:
                    //登出
                    updateUI();
                    break;
            }
        }
    }
}

