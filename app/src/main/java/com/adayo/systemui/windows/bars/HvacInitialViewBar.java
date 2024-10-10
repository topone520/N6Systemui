package com.adayo.systemui.windows.bars;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.bean.SystemUISourceInfo;
import com.adayo.systemui.bean.ViewStateInfo;
import com.adayo.systemui.interfaces.BaseCallback;
import com.adayo.systemui.manager.SourceControllerImpl;
import com.adayo.systemui.manager.WindowsControllerImpl;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.views.hvac.HvacInitialChildView;
import com.adayo.systemui.windows.views.hvac.HvacInitialShortcutView;
import com.android.systemui.SystemUIApplication;

/**
 * @author ADAYO-13
 */
public class HvacInitialViewBar implements BaseCallback<SystemUISourceInfo> {
    private static String TAG = HvacInitialViewBar.class.getSimpleName();
    private volatile static HvacInitialViewBar _initial_view_bar;
    private WindowManager mWindowManager;

    private final Handler _handler = new Handler();
    private HvacInitialChildView _initial_view;
    private HvacInitialShortcutView _shortcut_view;
    private WindowManager.LayoutParams params;
    private boolean _is_home = true;

    public static HvacInitialViewBar getInstance() {
        if (_initial_view_bar == null) {
            synchronized (HvacInitialViewBar.class) {
                if (_initial_view_bar == null) {
                    _initial_view_bar = new HvacInitialViewBar();
                }
            }
        }
        return _initial_view_bar;
    }

    @SuppressLint("ClickableViewAccessibility")

    @RequiresApi(api = Build.VERSION_CODES.O)
    private HvacInitialViewBar() {
        // 获取 WindowManager 服务
        _init_view_bar();
    }

    private void _init_view_bar() {
        LogUtil.d("initViewBar: ]");
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        _initial_view = new HvacInitialChildView(SystemUIApplication.getSystemUIContext());
        _shortcut_view = new HvacInitialShortcutView(SystemUIApplication.getSystemUIContext());

        // 设置悬浮窗口的参数
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT
                , WindowManager.LayoutParams.WRAP_CONTENT
                , 2073
                , WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                , PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL; // 悬浮窗口的初始位置

        // 添加悬浮窗口到 WindowManager
        if (_shortcut_view.getParent() != null) {
            mWindowManager.removeView(_shortcut_view);
        }
        if (_initial_view.getParent() != null) {
            mWindowManager.removeView(_initial_view);
        }
        View mFloatingView = _shortcut_view;
        mWindowManager.addView(mFloatingView, params);

//        _initial_view.setChangeLayout(() -> {
//            //隐藏小视图  展示快捷栏
//            isHome(true);
//            AAOP_LogUtils.d(TAG, "setChangeLayout  ------>");
//            resetTimer();
//        });
        //注册源管理器
        SourceControllerImpl.getInstance().addCallback(this);
        WindowsControllerImpl.getInstance().addCallback((BaseCallback<ViewStateInfo>) data -> updateStatus(SourceControllerImpl.getInstance().getCurrentUISource()));
    }

    public void isHome(boolean is_current_home) {
        _handler.removeCallbacksAndMessages(null);
        if (!is_current_home && !_is_home) return;
        LogUtil.d(" is ishome = " + _is_home + "   current_home  = " + is_current_home);
        if (_shortcut_view.getParent() != null) return;
        if (_initial_view.getParent() == null) return;
        AAOP_LogUtils.d("success init -isHome");
        //删除小按钮，展示快捷栏
        ObjectAnimator exitAnimator = ObjectAnimator.ofFloat(_initial_view, "translationY", 0, _initial_view.getHeight());
        exitAnimator.setDuration(300); // 设置动画时长
        exitAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mWindowManager.removeView(_initial_view);
                LogUtil.d("isHOme---------------------removeView");
            }
        });
        exitAnimator.start();
        // 入场动画
        ObjectAnimator enterAnimator = ObjectAnimator.ofFloat(_shortcut_view, "translationY", _shortcut_view.getHeight(), 0);
        enterAnimator.setDuration(300); // 设置动画时长
        enterAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });
        enterAnimator.start();
        try {
            mWindowManager.addView(_shortcut_view, params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LogUtil.d("isHOme---------------------addView");
    }

    public void noHome() {
        //删除功能栏 ，展示小按钮  先判断当前功能栏的layout是否在window中，两层判断，在window中&&小按钮不在window
        _handler.removeCallbacksAndMessages(null);
        if (_is_home) return;
        LogUtil.d(" NO ishome = " + _is_home);
        if (_shortcut_view.getParent() == null) return;
        if (_initial_view.getParent() != null) return;
        AAOP_LogUtils.d("success init -noHome");
        //删除快捷栏，展示小按钮
        ObjectAnimator exitAnimator = ObjectAnimator.ofFloat(_shortcut_view, "translationY", 0, _shortcut_view.getHeight());
        exitAnimator.setDuration(300); // 设置动画时长
        exitAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // 出场动画结束时执行一些操作
                // 移除当前视图
                if (_is_home) return;
                mWindowManager.removeView(_shortcut_view);
                LogUtil.d("NOHOme---------------------removeView");
            }
        });
        exitAnimator.start();

        // 入场动画
        ObjectAnimator enterAnimator = ObjectAnimator.ofFloat(_initial_view, "translationY", _initial_view.getHeight(), 0);
        enterAnimator.setDuration(300); // 设置动画时长
        enterAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });
        enterAnimator.start();
        try {
            if (_is_home) return;
            mWindowManager.addView(_initial_view, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.d("noHOme---------------------AddView");
    }


    private void resetTimer() {
        _handler.removeCallbacksAndMessages(null); // 清除之前的延时任务
        // 5秒
        long delayMillis = 5000;
        _handler.postDelayed(() -> {
            // 在5秒内没有操作时触发相应操作
            if (!_is_home) noHome();

        }, delayMillis);
    }

    @Override
    public void onDataChange(SystemUISourceInfo data) {
        updateStatus(data.getUiSource());
    }

    private Handler handler_va = new Handler();

    public void updateStatus(String currentSource) {
        LogUtil.d("currentSource :  " + currentSource);
        if (null == currentSource || AdayoSource.ADAYO_SOURCE_NULL.equals(currentSource)) return;
        //true 当前在主界面
        if (AdayoSource.ADAYO_SOURCE_HOME.equals(currentSource)) {
            _is_home = true;
            isHome(false);
            LogUtil.d("updateStatus: true");
        } else {
            _is_home = false;
            handler_va.postDelayed(() -> {
                if (!_is_home) noHome();
            }, 1000);
            LogUtil.d("updateStatus: false");
        }
    }

}
