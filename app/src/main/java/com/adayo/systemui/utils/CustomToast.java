package com.adayo.systemui.utils;

import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.systemui.R;

import java.lang.reflect.Field;

/**
 * 功能描述:自定义toast显示时长
 */
public class CustomToast extends Toast{
    private Toast mToast;
    private TimeCount timeCount;
    private String message;
    private int gravity;
    private Context mContext;
    private Handler mHandler = new Handler();
    private boolean canceled = true;
    private int mDuration;

//    public CustomToast(Context context, int gravity, String msg) {
//        message = msg;
//        mContext = context;
//        this.gravity = gravity;
//    }


    public CustomToast(Context mContext) {
        super(mContext);

    }

    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    @Override
    public void show() {
        try {
            Field field = Toast.class.getDeclaredField("mDuration");
            field.setAccessible(true);
            field.setInt(this,mDuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.show();
    }

    /**
     * 自定义时长、居中显示toast
     *
     * @param duration
     */
    public void show(int duration) {
        timeCount = new TimeCount(duration, 1000);
        if (canceled) {
            timeCount.start();
            canceled = false;
            showUntilCancel();
        }
    }

    /**
     * 隐藏toast
     */
    public void hide() {
        if (mToast != null) {
            mToast.cancel();
        }
        if (timeCount != null) {
            timeCount.cancel();
        }
        canceled = true;
    }

    private void showUntilCancel() {
        if (canceled) { //如果已经取消显示，就直接return
            return;
        }
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View toastView = inflater.inflate(R.layout.common_toast_layout, null);
        TextView tvToast = toastView.findViewById(R.id.tv_toast);
        tvToast.setText("message");
        mToast = new Toast(mContext);
        mToast.setGravity(Gravity.TOP, 0, 0);
        mToast.setView(toastView);
        try {
            if (mToast.getView().isShown()) {
                mToast.cancel();
            }
            // cancel same toast only on Android P and above, to avoid IllegalStateException on addView
            if (Build.VERSION.SDK_INT >= 28 && mToast.getView().isShown()) {
                mToast.cancel();
            }
            mToast.show();
        } catch (Exception e) {
            e.printStackTrace();
            mToast.show();//This line will show toast if previous toast is null
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showUntilCancel();
            }
        }, 3500);
    }

    /**
     * 自定义计时器
     */
    private class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval); //millisInFuture总计时长，countDownInterval时间间隔(一般为1000ms)
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            hide();
        }
    }
}
