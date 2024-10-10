package com.adayo.systemui.functional.negative._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.functional.negative._messageAction.MessageActions;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.windows.dialogs.ScreenShutDownDialog;
import com.adayo.systemui.windows.views.QsIconView;
import com.android.systemui.R;

public class NegativeScreenViewBinder extends AbstractViewBinder<Integer> {
    private QsIconView _qs_screen;

    public NegativeScreenViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }


    @Override
    protected void _bind_view(View view) {
        _qs_screen = view.findViewById(R.id.qs_screen);
        _qs_screen.setListener(v -> {
            if (!MessageActions.getInstance().isEditType()){
                ScreenShutDownDialog.getInstance().turnOnOff(false);
                ScreenShutDownDialog.getInstance().show();
            }
        });
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}
