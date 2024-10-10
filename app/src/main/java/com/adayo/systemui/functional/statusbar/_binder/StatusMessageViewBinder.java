package com.adayo.systemui.functional.statusbar._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.windows.ColorEggDialog;
import com.adayo.systemui.windows.views.NotificationCenterView;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class StatusMessageViewBinder extends AbstractViewBinder<Integer> {

    private ImageView _icon_message;

    public StatusMessageViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }

    @Override
    protected void _bind_view(View view) {
        _icon_message = view.findViewById(R.id.icon_message);
        _icon_message.setTag(false);
        _icon_message.setOnClickListener(v -> {
            NotificationCenterView.getInstance().show(false,"0");
            //todo 为了测试彩蛋
//            ColorEggDialog.createDialog(SystemUIApplication.getSystemUIContext(), R.style.NotTransparentStyle).setMessageInfo(null).show();

            _icon_message.setSelected(false);
        });
    }

    public void messageIconSetSelected(boolean isSelected){
        if (isSelected){
            _icon_message.setSelected(true);
        }else {
            _icon_message.setSelected(false);
        }
    }

    @Override
    protected void _update_ui(Integer integer) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}
