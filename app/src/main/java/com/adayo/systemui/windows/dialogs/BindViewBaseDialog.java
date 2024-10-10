package com.adayo.systemui.windows.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.soavb.foundation.view.AbstractBindViewDialog;
import com.android.systemui.R;

public abstract class BindViewBaseDialog extends AbstractBindViewDialog {

    protected Context _mContext;
    private final int _dialogWidth;
    private final int _dialogHeight;

    public BindViewBaseDialog(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, R.style.TransparentStyle);//这里设置dialog的统一主题，全屏、背景透明等
        _mContext = context;
        this._dialogWidth = dialogWidth;
        this._dialogHeight = _dialogHeight;
    }


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(true);
        Window window = getWindow();
        if (window != null) {
            // Set the gravity of the content view to top-center
            window.setGravity(Gravity.CENTER);

            // Set system UI visibility
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            WindowManager.LayoutParams params = window.getAttributes();
            params.type = 2071;
            params.width = _dialogWidth;
            params.height = _dialogHeight;
            // Calculate the y-coordinate to position the dialog at the top, considering status bar height
            Rect rect = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            int statusBarHeight = rect.top;

            params.y = statusBarHeight; // Adjust this value based on your needs

            window.setAttributes(params);
        }
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    protected abstract void initView();
    public synchronized void showPanel() {
        if (!isShowing()) {
            show();
        } else {
            dismiss();
        }
    }
}
