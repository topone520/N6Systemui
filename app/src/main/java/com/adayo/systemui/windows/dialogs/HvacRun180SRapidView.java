package com.adayo.systemui.windows.dialogs;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.contents.HvacSingleton;
import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.R;

public class HvacRun180SRapidView {
    private View mFloatingView;
    private int RAPID_TIME = 180;
    private CountDownTimer timerLong;
    private TextView tv_time;
    private int _rapid_status = 1;//记录极速控温的状态
    private int hvac_time = 5;
    private static HvacRun180SRapidView winInstance;
    private WindowManager mWindowManager;

    private HvacRun180SRapidView() {

    }

    public static synchronized HvacRun180SRapidView getInstance() {
        if (winInstance == null) {
            winInstance = new HvacRun180SRapidView();
        }
        return winInstance;
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void rapidShow(Context context) {
        closeRapid();
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        // 加载悬浮窗口的布局
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFloatingView = inflater.inflate(R.layout.hvac_rapid_toast_layout, null);

        Animator animator = AnimatorInflater.loadAnimator(context, R.animator.fade_in);
        animator.setTarget(mFloatingView);
        // 添加新的悬浮窗口到 WindowManager
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                914,
                160,
                2071,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.y = -60;
        // 添加悬浮窗口到 WindowManager
        mWindowManager.addView(mFloatingView, params);
        animator.start();
        tv_time = mFloatingView.findViewById(R.id.tv_time);
        //启用180S计时器
        timerLong = new CountDownTimer(RAPID_TIME * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 在每一秒更新显示剩余时间
                long seconds = millisUntilFinished / 1000;
                LogUtil.d("180 changeLayout time---= " + RAPID_TIME);
                tv_time.setText("（" + seconds + "S）");
                //先判断极速控温状态
                if (_rapid_status == HvacSOAConstant.HVAC_ON_IO) {
                    if (HvacSingleton.getInstance().isHvacHomeShow()) {
                        hvac_time--;
                        if (hvac_time == 0) {
                            //关闭弹框
                            closeRapid();
                        }
                    } else hvac_time = 5;
                } else {
                    closeRapid();
                }

            }

            @Override
            public void onFinish() {
                Log.d("TAG", "onFinish-----------------: ");
                // 计时结束移除
                closeRapid();
            }
        };

        // 启动计时器
        timerLong.start();
        mFloatingView.findViewById(R.id.tv_close).setOnClickListener(v -> closeRapid());
    }

    public void closeRapid() {
        destroyTimer(timerLong);
        if (mFloatingView != null && mFloatingView.getParent() != null) {
            mWindowManager.removeView(mFloatingView);
            mFloatingView = null;
        }
    }

    public void upDataRapidStatus(int status) {
        _rapid_status = status;
    }

    private void destroyTimer(CountDownTimer timerLong) {
        if (timerLong != null) {
            timerLong.cancel();
            timerLong = null;
        }
    }

}
