package com.adayo.systemui.windows.dialogs;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class HvacRun3SRapidView {
    private final int RAPID_TIME = 3;
    private CountDownTimer timerShort;
    private static HvacRun3SRapidView winInstance;
    private WindowManager mWindowManager;
    private View mFloatingView;

    private HvacRun3SRapidView() {

    }

    public static synchronized HvacRun3SRapidView getInstance() {
        if (winInstance == null) {
            winInstance = new HvacRun3SRapidView();
        }
        return winInstance;
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void rapidShow(Context context) {
        // 获取 WindowManager 服务
        if (mWindowManager != null) return;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        // 加载悬浮窗口的布局
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFloatingView = inflater.inflate(R.layout.hvac_three_toast_layout, null);

        Animator animator = AnimatorInflater.loadAnimator(context, R.animator.fade_in);
        animator.setTarget(mFloatingView);

        // 设置悬浮窗口的参数
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                500,
                112,
                2071,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL; // 悬浮窗口的初始位置
        params.y = 80;

        // 添加悬浮窗口到 WindowManager
        mWindowManager.addView(mFloatingView, params);
        animator.start();

        //三秒倒计时，进入180读秒
        timerShort = new CountDownTimer(RAPID_TIME * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                LogUtil.d("3 time---");
            }

            @Override
            public void onFinish() {
                LogUtil.d("3 time---end");
                // 计时结束移除 加载另一个视图
                HvacRun180SRapidView.getInstance().rapidShow(SystemUIApplication.getSystemUIContext());
                destroyTimer(timerShort);
            }
        };
        // 启动计时器
        timerShort.start();
    }

    private void destroyTimer(CountDownTimer timerLong) {
        if (mWindowManager != null && mFloatingView != null) {
            mWindowManager.removeView(mFloatingView);
            mWindowManager = null;
        }
        if (timerLong == null) return;
        timerLong.cancel();
        timerLong = null;
    }

}
