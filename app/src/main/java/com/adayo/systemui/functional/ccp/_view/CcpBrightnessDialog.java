package com.adayo.systemui.functional.ccp._view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.systemui.windows.dialogs.BaseDialog;
import com.adayo.systemui.windows.views.ScreenVolumeSeekBar;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class CcpBrightnessDialog extends BaseDialog {

    private volatile static CcpBrightnessDialog ccpBrightnessDialog;
    private Handler handler;
    private ScreenVolumeSeekBar mBrightness;
    private TextView mBrightnessValue;

    public static CcpBrightnessDialog getInstance() {
        if (ccpBrightnessDialog == null) {
            synchronized (CcpBrightnessDialog.class) {
                if (ccpBrightnessDialog == null) {
                    ccpBrightnessDialog = new CcpBrightnessDialog(SystemUIApplication.getSystemUIContext(), 616, 288);
                }
            }
        }
        return ccpBrightnessDialog;
    }

    public CcpBrightnessDialog(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void initView() {
        mBrightness = findViewById(R.id.brightness);
        mBrightnessValue = findViewById(R.id.brightness_value);

        mBrightness.setMax(39);
        mBrightness.setProgress(10);
        mBrightnessValue.setText(""+mBrightness.getProgress());
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        mBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onUserAction();
                mBrightnessValue.setText("" + progress);
                upDataBrightness(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_ccp_brightness;
    }

    public boolean isShow() {
        return isShow();
    }

    public void onShow() {
        // 在对话框显示时启动延迟任务
        startAutoDismissTask();
        show();
    }

    // 启动自动关闭任务
    private void startAutoDismissTask() {
        // 移除之前的任务，确保只有一个任务在执行
        handler.removeCallbacksAndMessages(null);

        // 延迟5秒执行 dismiss 操作
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 5000);
    }

    // 当用户执行了操作时，重置计时
    private void resetAutoDismissTask() {
        // 移除之前的任务，重新启动计时
        handler.removeCallbacksAndMessages(null);
        startAutoDismissTask();
    }

    // 在需要监听用户操作的地方调用这个方法，比如在点击按钮的地方
    private void onUserAction() {
        resetAutoDismissTask();
        // 这里可以添加其他用户操作的逻辑
    }

    public void leftProgress() {
        int progress = mBrightness.getProgress();
        progress--;
        if (progress >= 0 && progress <= 39) {
            mBrightness.setProgress(progress);
            mBrightnessValue.setText(progress);
            upDataBrightness(progress);
        }
    }

    public void rightProgress() {
        int progress = mBrightness.getProgress();
        progress++;
        if (progress >= 0 && progress <= 39) {
            mBrightness.setProgress(progress);
            mBrightnessValue.setText(progress);
            upDataBrightness(progress);
        }
    }

    private void upDataBrightness(int progress) {
        Bundle param = new Bundle();
        param.putInt("screen", 0); //0表示IVI屏, 1表示仪表屏
        param.putInt("value", progress);// 0~10
        AAOP_DeviceServiceManager.getInstance().setDeviceFuncUniversalInterface("DisplayDevice", "setScreenLightControl", param);
    }

    // 重写 dismiss 方法，在对话框消失时移除任务，防止内存泄漏
    @Override
    public void dismiss() {
        // 移除任务
        handler.removeCallbacksAndMessages(null);
        // 调用父类的 dismiss 方法
        super.dismiss();
    }
}