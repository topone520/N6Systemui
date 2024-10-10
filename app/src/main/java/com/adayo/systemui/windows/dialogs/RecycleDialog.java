package com.adayo.systemui.windows.dialogs;

import android.content.Context;

import androidx.annotation.NonNull;

import com.android.systemui.R;

public class RecycleDialog extends BaseDialog{
    public RecycleDialog(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_scene;
    }
}
