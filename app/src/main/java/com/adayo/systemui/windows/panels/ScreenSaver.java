package com.adayo.systemui.windows.panels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class ScreenSaver implements View.OnTouchListener{
    private volatile static ScreenSaver screenSaver;
    private RelativeLayout mFloatLayout;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    private boolean isAdded = false;

    private ScreenSaver() {
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        initView();
    }

    public static ScreenSaver getInstance() {
        if (screenSaver == null) {
            synchronized (ScreenSaver.class) {
                if (screenSaver == null) {
                    screenSaver = new ScreenSaver();
                }
            }
        }
        return screenSaver;
    }

    @SuppressLint("WrongConstant")
    private void initView() {
        mFloatLayout = (RelativeLayout) View.inflate(SystemUIApplication.getSystemUIContext(), R.layout.screen_view, null);
        mLayoutParams = new WindowManager.LayoutParams(2065);
        mLayoutParams.width = 3840;
        mLayoutParams.height = 720;
        mLayoutParams.type = 2065;        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
        mLayoutParams.setTitle(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.screen_scene_screenOff));
        mLayoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
        mLayoutParams.gravity = Gravity.END|Gravity.TOP;
        mFloatLayout.setOnTouchListener(this);

    }

    private int visibility = View.GONE;
    public void setVisibility(int visible) {
        LogUtil.debugD(visible + " = visible");
        if(visibility == visible){
            return;
        }
        visibility = visible;
        if (!isAdded && null != mWindowManager && null != mFloatLayout) {
            mWindowManager.addView(mFloatLayout, mLayoutParams);
            isAdded = true;
        }
        if (null != mFloatLayout) {
            mFloatLayout.setVisibility(visible);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }
}
