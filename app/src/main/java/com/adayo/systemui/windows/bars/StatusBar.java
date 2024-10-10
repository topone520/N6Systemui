package com.adayo.systemui.windows.bars;

import static com.adayo.systemui.contents.PublicContents.STATUS_BAR;
import static com.adayo.systemui.contents.SystemUIConfigs.HAS_STATUS_BAR;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.systemui.functional.statusbar._binder.SenselessPowerMonitor;
import com.adayo.systemui.functional.statusbar._view.StatusViewFragment;
import com.adayo.systemui.functional.statusbar._view._dialog.PowerMonitorDialog;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.utils.OTAUpGradeUtil;
import com.adayo.systemui.utils.WindowsUtils;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class StatusBar implements View.OnTouchListener {
    private volatile static StatusBar mStatusBar;
    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;
    private ConstraintLayout mFloatLayout;
    private boolean isAdded = false;
    private StatusViewFragment _status_view;

    private SenselessPowerMonitor _monitor;

    private StatusBar() {
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        initView();
        initData();
    }

    private void initData() {
        LogUtil.debugD("StatusBar- Add Callback");
        _monitor = new SenselessPowerMonitor();
        _monitor.setListener(() -> {
            PowerMonitorDialog powerMonitorDialog = new PowerMonitorDialog(SystemUIApplication.getSystemUIContext(), 720, 360);
            powerMonitorDialog.onShow();
        });
    }

    public static StatusBar getInstance() {
        if (mStatusBar == null) {
            synchronized (StatusBar.class) {
                if (mStatusBar == null) {
                    mStatusBar = new StatusBar();
                }
            }
        }
        return mStatusBar;
    }

    private void initView() {
        mFloatLayout = (ConstraintLayout) View.inflate(SystemUIApplication.getSystemUIContext(), R.layout.status_bar, null);
        mLayoutParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80, WindowManager.LayoutParams.TYPE_STATUS_BAR, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, PixelFormat.TRANSLUCENT);
        mLayoutParams.token = new Binder();
        mLayoutParams.gravity = Gravity.TOP;
        mLayoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        mLayoutParams.setTitle(STATUS_BAR);
        mLayoutParams.packageName = SystemUIApplication.getSystemUIContext().getPackageName();
        mLayoutParams.windowAnimations = R.style.StatusBarAnimation;
        mFloatLayout.setOnTouchListener(this);

        _status_view = mFloatLayout.findViewById(R.id.status_view);

        _status_view.initialize();
    }

    private void show() {
        if (!isAdded && null != mWindowManager && null != mFloatLayout) {
            mWindowManager.addView(mFloatLayout, mLayoutParams);
            isAdded = true;
        }
    }

    public void setVisibility(int visible) {
        if (!HAS_STATUS_BAR) {
            return;
        }
        LogUtil.debugD(visible + "");
        show();
        if (null != mFloatLayout) {
            mFloatLayout.setVisibility(visible);
        }
    }

    public int getVisibility() {
        int visible = View.VISIBLE;
        if (null != mFloatLayout) {
            visible = mFloatLayout.getVisibility();
        }
        LogUtil.debugD(visible + "");
        return visible;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        LogUtil.debugD("x--y onTouchEvent:x=====" + event.getRawX() + "----------y=====" + event.getRawY());
        _monitor.clickScreen();
        return WindowsUtils.showQsPanel(event);
    }

    public void messageIconSetSelected(boolean isSelected) {
        if (isSelected) {
            _status_view.setSelected(true);
        } else {
            _status_view.setSelected(false);
        }
    }

    public void otaIconSetSelected(String title, CharSequence content, String time) {
        _status_view.otaIconSetSelected(title, content, time);
    }

    public void otaIconStatus(int state) {
        _status_view.otaIconStatus(state);
    }
}
