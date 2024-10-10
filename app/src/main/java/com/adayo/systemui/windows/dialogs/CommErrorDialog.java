package com.adayo.systemui.windows.dialogs;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class CommErrorDialog extends BindViewBaseDialog {

    private static CommErrorDialog instance;

    private CommErrorDialog(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }

    public static CommErrorDialog getInstance() {
        if (instance == null) {
            synchronized (CommErrorDialog.class) {
                if (instance == null) {
                    instance = new CommErrorDialog(SystemUIApplication.getSystemUIContext(), 640, 440);
                }
            }
        }
        return instance;
    }

    @Override
    protected void initView() {
        findViewById(R.id.tv_sure).setOnClickListener(v -> dismiss());
    }

    @Override
    protected void createViewBinder() {
    }

    @Override
    protected int acquireResourceId() {
        return R.layout.comm_error_dialog_layout;
    }
}

