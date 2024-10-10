package com.adayo.systemui.functional.ccp._view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adayo.systemui.bean.VolumeInfo;
import com.adayo.systemui.interfaces.BaseCallback;
import com.adayo.systemui.manager.VolumeControllerImpl;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.dialogs.BaseDialog;
import com.adayo.systemui.windows.views.ScreenVolumeSeekBar;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class CcpVolumeDialog extends BaseDialog implements BaseCallback<VolumeInfo> {

    private volatile static CcpVolumeDialog ccpVolumeDialog;
    public static final int SETTING_STREAM_MUSIC = 3;        //媒体音量（USB音乐音量）
    private Handler handler;
    private Handler mHandlerSeek = new Handler();
    private ScreenVolumeSeekBar mMediaVolume;
    private TextView mMediaValue;
    private int type = -1;
    private int value = -1;


    public static CcpVolumeDialog getInstance() {
        if (ccpVolumeDialog == null) {
            synchronized (CcpVolumeDialog.class) {
                if (ccpVolumeDialog == null) {
                    ccpVolumeDialog = new CcpVolumeDialog(SystemUIApplication.getSystemUIContext(), 616, 288);
                }
            }
        }
        return ccpVolumeDialog;
    }

    public CcpVolumeDialog(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void initView() {
        mMediaVolume = findViewById(R.id.media_volume);
        mMediaValue = findViewById(R.id.media_value);

        mMediaVolume.setMax(39);
    }

    @Override
    protected void initData() {
        VolumeControllerImpl.getInstance().addCallback(this);
    }

    @Override
    protected void initListener() {
        mMediaVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onUserAction();
                type = SETTING_STREAM_MUSIC;
                value = progress;
                upDataVolume(type, value);
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
        return R.layout.dialog_ccp_volume;
    }

    public boolean isShow() {
        return isShow();
    }

    public void onShow() {
        startAutoDismissTask();
        show();
    }

    // 启动自动关闭任务
    private void startAutoDismissTask() {
        handler.removeCallbacksAndMessages(null);

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
        int progress = mMediaVolume.getProgress();
        progress--;
        if (progress >= 0 && progress <= 39) {
            mMediaVolume.setProgress(progress);
            mMediaValue.setText(progress);
            upDataVolume(SETTING_STREAM_MUSIC, progress);
        }
    }

    public void rightProgress() {
        int progress = mMediaVolume.getProgress();
        progress++;
        if (progress >= 0 && progress <= 39) {
            mMediaVolume.setProgress(progress);
            mMediaValue.setText(progress);
            upDataVolume(SETTING_STREAM_MUSIC, progress);
        }
    }

    private void upDataVolume(int type, int progress) {
        VolumeControllerImpl.getInstance().setVolume(type, value);
    }

    // 重写 dismiss 方法，在对话框消失时移除任务，防止内存泄漏
    @Override
    public void dismiss() {
        handler.removeCallbacksAndMessages(null);
        super.dismiss();
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            LogUtil.i("setDeviceFuncUniversalInterface runnable: type =  " + type + " ; value = " + value);
            if (value != -1 && type != -1) {
                VolumeControllerImpl.getInstance().setVolume(type, value);
                mHandlerSeek.postDelayed(runnable, 200);
            }
        }
    };

    @Override
    public void onDataChange(VolumeInfo data) {
        mMediaVolume.setProgress(data.getMediaVolume());
        mMediaValue.setText(String.valueOf(data.getMediaVolume()));
    }
}
