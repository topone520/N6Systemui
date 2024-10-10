package com.adayo.systemui.windows.bars;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.adayo.systemui.bean.SystemUISourceInfo;
import com.adayo.systemui.interfaces.BaseCallback;
import com.adayo.systemui.manager.SourceControllerImpl;
import com.adayo.systemui.windows.panels.ShortcutDockPanel;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class ShortcutDockBar implements BaseCallback<SystemUISourceInfo> {

    private static String TAG = ShortcutDockBar.class.getSimpleName();
    private volatile static ShortcutDockBar mShortcutDockBar;
    private WindowManager mWindowManager;
    private RelativeLayout mFloatLayout;
    private WindowManager.LayoutParams mLayoutParams;
    private ImageView _img_shortcut;
    private float startX;

    public static ShortcutDockBar getInstance(){
        if (mShortcutDockBar == null){
            synchronized (ShortcutDockBar.class){
                if (mShortcutDockBar == null){
                    mShortcutDockBar = new ShortcutDockBar();
                }
            }
        }
        return mShortcutDockBar;
    }

    private ShortcutDockBar(){
        initView();
        initData();
    }

    @SuppressLint({"RtlHardcoded", "ClickableViewAccessibility"})
    private void initView() {
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        mFloatLayout = (RelativeLayout) View.inflate(SystemUIApplication.getSystemUIContext(), R.layout.shortcut_dock_bar, null);
        mLayoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                2073,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        mLayoutParams.gravity = Gravity.RIGHT | Gravity.CENTER_HORIZONTAL;
        mLayoutParams.y = -54;
        mWindowManager.addView(mFloatLayout, mLayoutParams);

        _img_shortcut = mFloatLayout.findViewById(R.id.img_shortcut);
        _img_shortcut.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getRawX() - startX;
                        if (deltaX < -50) {  // You can adjust the threshold for left swipe
                            showWindow();
                        }
                        break;
                }
                return true;
            }
        });


    }

    private void initData() {
        SourceControllerImpl.getInstance().addCallback(this);

    }

    private void showWindow() {
        ShortcutDockPanel.getInstance().setShortcutBarVisibility(View.VISIBLE);
        _img_shortcut.setVisibility(View.GONE);
    }


    @Override
    public void onDataChange(SystemUISourceInfo data) {

    }

    public void setShortcutBarVisibility(int visibility){
        _img_shortcut.setVisibility(visibility);
    }
}
