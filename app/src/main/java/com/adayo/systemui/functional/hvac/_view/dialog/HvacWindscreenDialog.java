package com.adayo.systemui.functional.hvac._view.dialog;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.adayo.systemui.contents.HvacSingleton;
import com.android.systemui.R;

public class HvacWindscreenDialog {

    private static HvacWindscreenDialog instance;
    private final Context applicationContext;
    private WindowManager windowManager;
    private View floatingView;
    private boolean isShowing = false;
    private TextView tvTemp;

    private HvacWindscreenDialog(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    // 获取单例实例的方法
    public static HvacWindscreenDialog getInstance(Context context) {
        if (instance == null) {
            instance = new HvacWindscreenDialog(context);
        }
        return instance;
    }

    public void show() {
        if (isShowing) {
            return;
        }
        windowManager = (WindowManager) applicationContext.getSystemService(Context.WINDOW_SERVICE);

        // 创建悬浮框的布局
        floatingView = LayoutInflater.from(applicationContext).inflate(R.layout.hvac_wsr_layout, null);

        // 如果悬浮框已经有了父视图，先将它从父视图中移除
        if (floatingView.getParent() != null) {
            ((FrameLayout) floatingView.getParent()).removeView(floatingView);
        }

        // 设置悬浮框大小和位置
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                96,
                88,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                2073,
                PixelFormat.TRANSLUCENT);

        layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        layoutParams.y = 100; // 距离底部 100 像素
        layoutParams.x = 52;

        // 找到布局文件中的视图元素
        tvTemp = floatingView.findViewById(R.id.tv_wsr);
        //    tvTemp.setText("0");
        // 显示悬浮框
        windowManager.addView(floatingView, layoutParams);
        isShowing = true;
    }

    public void dismiss() {
        if (isShowing && windowManager != null && floatingView != null) {
            windowManager.removeView(floatingView);
            isShowing = false;
        }
    }

    public void updateWindscreen(int level) {
        if (tvTemp == null) return;
        tvTemp.setText(level + "");
    }
}

