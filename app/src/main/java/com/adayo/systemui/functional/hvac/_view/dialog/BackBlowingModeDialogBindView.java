package com.adayo.systemui.functional.hvac._view.dialog;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.systemui.functional.hvac._binder.BackRowBlowingModeViewBinder;
import com.adayo.systemui.windows.dialogs.BindViewBaseDialog;
import com.android.systemui.R;

public class BackBlowingModeDialogBindView extends BindViewBaseDialog {
    private static final String TAG = BackBlowingModeDialogBindView.class.getName();

    public BackBlowingModeDialogBindView(@NonNull Context context, int dialogWidth, int dialogHeight) {
        super(context, dialogWidth, dialogHeight);
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
        return R.layout.cb_codir_layout;
    }
}
