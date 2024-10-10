package com.adayo.systemui.functional.statusbar._view._dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adayo.systemui.windows.dialogs.BaseDialog;
import com.android.systemui.R;

public class PowerMonitorDialog extends BaseDialog {

    private LinearLayout _ll_enter, _ll_canel;
    private TextView _tv_enter;
    private int count = 59;

    public PowerMonitorDialog(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }

    @Override
    protected void initView() {
        _ll_enter = findViewById(R.id.ll_enter);
        _tv_enter = findViewById(R.id.tv_enter);
        _ll_canel = findViewById(R.id.ll_canel);
    }

    @Override
    protected void initData() {
        _ll_enter.setOnClickListener(v -> {
            dismiss();
        });
        _ll_canel.setOnClickListener(v -> {
            dismiss();
        });
    }

    @SuppressLint("SetTextI18n")
    public void onShow(){
        for (int i = 0; i < 60; i++){
            _tv_enter.setText(_mContext.getString(R.string.tv_power_enter)+"("+(count--)+"s)");
            if (count == 0){
                dismiss();
            }
        }
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_power_monitor;
    }
}
