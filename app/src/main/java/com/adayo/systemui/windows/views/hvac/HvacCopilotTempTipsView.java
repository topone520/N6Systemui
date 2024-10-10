package com.adayo.systemui.windows.views.hvac;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

/**
 * @author ADAYO-13
 */
public class HvacCopilotTempTipsView {
    private static String TAG = HvacCopilotTempTipsView.class.getSimpleName();
    private volatile static HvacCopilotTempTipsView _initial_view_bar;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams params;

    public static HvacCopilotTempTipsView getInstance() {
        if (_initial_view_bar == null) {
            synchronized (HvacCopilotTempTipsView.class) {
                if (_initial_view_bar == null) {
                    _initial_view_bar = new HvacCopilotTempTipsView();
                }
            }
        }
        return _initial_view_bar;
    }

    @SuppressLint("ClickableViewAccessibility")

    @RequiresApi(api = Build.VERSION_CODES.O)
    private HvacCopilotTempTipsView() {
        // 获取 WindowManager 服务
        _init_bar();
    }

    private void _init_bar() {
        LogUtil.d("initViewBar: ]");
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        // 设置悬浮窗口的参数
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT
                , WindowManager.LayoutParams.WRAP_CONTENT
                , 2073
                , WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                , PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL; // 悬浮窗口的初始位置
        View mFloatingView = AAOP_HSkin.getLayoutInflater(SystemUIApplication.getSystemUIContext()).inflate(R.layout.hvac_temp_tips_layout, null);
        mWindowManager.addView(mFloatingView, params);
    }

}
