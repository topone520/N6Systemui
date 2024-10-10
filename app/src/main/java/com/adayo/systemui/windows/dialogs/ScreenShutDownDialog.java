package com.adayo.systemui.windows.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class ScreenShutDownDialog extends BaseDialog {

    private ConstraintLayout cl_shutdown;
    @SuppressLint("StaticFieldLeak")
    private volatile static ScreenShutDownDialog mScreenShutDownDialog;

    private boolean _isOnScreen;

    public static ScreenShutDownDialog getInstance() {
        if (mScreenShutDownDialog == null) {
            synchronized (ScreenShutDownDialog.class) {
                if (mScreenShutDownDialog == null) {
                    mScreenShutDownDialog = new ScreenShutDownDialog(SystemUIApplication.getSystemUIContext(), 3900, 720);
                    mScreenShutDownDialog._isOnScreen = true;
                }
            }
        }
        return mScreenShutDownDialog;
    }

    public ScreenShutDownDialog(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void initView() {
        cl_shutdown = findViewById(R.id.cl_shutdown);
    }

    @Override
    protected void initData() {
        //设置背光开关
        turnOnOff(false);
    }

    public void turnOnOff(boolean on_off) {
        _isOnScreen = on_off;
        //设置背光开关
        Bundle param = new Bundle();
        param.putInt("screen", 0); //0表示IVI屏, 1表示仪表屏
        param.putInt("type", on_off ? 1 : 0);// 0是关背光，1是开启背光
        AAOP_DeviceServiceManager.getInstance().setDeviceFuncUniversalInterface("DisplayDevice", "setScreenLightControl", param);
    }

    public boolean isScreenOn() {
        return _isOnScreen;
    }

    @Override
    protected void initListener() {
        cl_shutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置背光开关
                turnOnOff(true);
                dismiss();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.screen_shutdown_dialog_layout;
    }
}
