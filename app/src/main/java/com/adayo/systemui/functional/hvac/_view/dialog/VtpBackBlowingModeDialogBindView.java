package com.adayo.systemui.functional.hvac._view.dialog;

import android.content.Context;

import androidx.annotation.NonNull;

import com.adayo.systemui.functional.hvac._binder.BackRowBlowingModeViewBinder;
import com.adayo.systemui.windows.dialogs.BindViewBaseDialog;
import com.android.systemui.R;


public class VtpBackBlowingModeDialogBindView extends BindViewBaseDialog {
    private static final String TAG = VtpBackBlowingModeDialogBindView.class.getName();

    public VtpBackBlowingModeDialogBindView(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }


    @Override
    protected void initView() {

    }

    @Override
    protected void createViewBinder() {
        insertViewBinder(new BackRowBlowingModeViewBinder());
    }

    @Override
    protected int acquireResourceId() {
        return R.layout.vtp_cb_codir_layout;
    }
}
