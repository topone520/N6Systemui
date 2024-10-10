package com.adayo.systemui.functional.hvac._view.dialog;

import android.content.Context;

import androidx.annotation.NonNull;

import com.adayo.systemui.windows.dialogs.BindViewBaseDialog;
import com.android.systemui.R;

public class VtpForestDialogBindView extends BindViewBaseDialog {

    public VtpForestDialogBindView(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void createViewBinder() {

    }

    @Override
    protected int acquireResourceId() {
        return R.layout.vtp_hvac_forest_layout;
    }
}
