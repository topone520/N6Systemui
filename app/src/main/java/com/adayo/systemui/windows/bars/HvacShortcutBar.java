package com.adayo.systemui.windows.bars;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.systemui.bean.SystemUISourceInfo;
import com.adayo.systemui.bean.ViewStateInfo;
import com.adayo.systemui.interfaces.BaseCallback;
import com.adayo.systemui.manager.SourceControllerImpl;
import com.adayo.systemui.manager.WindowsControllerImpl;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.views.hvac.HvacInitialChildView;
import com.android.systemui.SystemUIApplication;

/**
 * @author ADAYO-13
 */
public class HvacShortcutBar implements BaseCallback<SystemUISourceInfo> {
    private static String TAG = HvacShortcutBar.class.getSimpleName();
    private volatile static HvacShortcutBar _initial_view_bar;
    private WindowManager mWindowManager;
    private HvacInitialChildView _initial_view;
    private WindowManager.LayoutParams params;

    public static HvacShortcutBar getInstance() {
        if (_initial_view_bar == null) {
            synchronized (HvacShortcutBar.class) {
                if (_initial_view_bar == null) {
                    _initial_view_bar = new HvacShortcutBar();
                }
            }
        }
        return _initial_view_bar;
    }

    @SuppressLint("ClickableViewAccessibility")

    @RequiresApi(api = Build.VERSION_CODES.O)
    private HvacShortcutBar() {
        // 获取 WindowManager 服务
        _init_bar();
    }

    private void _init_bar() {
        LogUtil.d("initViewBar: ]");
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        _initial_view = new HvacInitialChildView(SystemUIApplication.getSystemUIContext());

        // 设置悬浮窗口的参数
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT
                , WindowManager.LayoutParams.WRAP_CONTENT
                , 2073
                , WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                , PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL; // 悬浮窗口的初始位置
        View mFloatingView = _initial_view;
        mWindowManager.addView(mFloatingView, params);
        //注册源管理器
        SourceControllerImpl.getInstance().addCallback(this);
        WindowsControllerImpl.getInstance().addCallback((BaseCallback<ViewStateInfo>) data -> updateStatus(SourceControllerImpl.getInstance().getCurrentUISource()));
    }

    @Override
    public void onDataChange(SystemUISourceInfo data) {
        updateStatus(data.getUiSource());
    }

    public void updateStatus(String currentSource) {
        LogUtil.d("currentSource :  " + currentSource);
        if (null == currentSource || AdayoSource.ADAYO_SOURCE_NULL.equals(currentSource)) return;
        //true 当前在主界面
        if (_initial_view == null) return;
        _initial_view.ivStatus(AdayoSource.ADAYO_SOURCE_HOME.equals(currentSource) ? View.GONE : View.VISIBLE);
    }

}
