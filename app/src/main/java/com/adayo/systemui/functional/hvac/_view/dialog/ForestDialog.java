package com.adayo.systemui.functional.hvac._view.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.adayo.systemui.windows.dialogs.BaseDialog;
import com.adayo.systemui.windows.dialogs.BindViewBaseDialog;
import com.android.systemui.R;

public class ForestDialog extends BindViewBaseDialog {

    public ForestDialog(@NonNull Context context, int dialogWidth, int _dialogHeight) {
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
        return R.layout.hvac_forest_layout;
    }
}
