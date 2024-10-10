package com.adayo.systemui.windows.panels;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.contents.SystemUIContent;
import com.adayo.systemui.functional.negative._view.NegativeViewFragment;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.manager.WindowsControllerImpl;
import com.adayo.systemui.windows.views.PullDownDumperLayout;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

/**
 * 负一屏
 */
public class QsViewPanel {
    private final String TAG = QsViewPanel.class.getSimpleName();
    private volatile static QsViewPanel mQsPanel;
    private WindowsControllerImpl windowsController;
    private PullDownDumperLayout pullDownDumperLayout;

    private WindowManager.LayoutParams layoutParams;
    private WindowManager mWindowManager;
    private View mFloatLayout;
    private boolean isAdded = false;
    private NegativeViewFragment mNegativeView;


    private QsViewPanel() {
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        initView();
    }

    public static QsViewPanel getInstance() {
        if (mQsPanel == null) {
            synchronized (QsViewPanel.class) {
                if (mQsPanel == null) {
                    mQsPanel = new QsViewPanel();
                }
            }
        }
        return mQsPanel;
    }

    private void initView() {

        layoutParams = new WindowManager.LayoutParams(2071);
        mFloatLayout = LayoutInflater.from(SystemUIApplication.getSystemUIContext()).inflate(R.layout.negative_panel_layout, null);
        layoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;

        layoutParams.systemUiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        layoutParams.setTitle(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.screen_scene_qsviewpanele));
        layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
        layoutParams.gravity = Gravity.END | Gravity.TOP;
        layoutParams.format = PixelFormat.TRANSLUCENT;

        layoutParams.width = 3700;
        layoutParams.height = 720;

        mFloatLayout.setLayoutParams(layoutParams);
        pullDownDumperLayout = mFloatLayout.findViewById(R.id.layout);
        mNegativeView = mFloatLayout.findViewById(R.id.negative_view);

        if (!isAdded && null != mWindowManager && null != mFloatLayout) {
            mWindowManager.addView(mFloatLayout, layoutParams);
            isAdded = true;
        }

        mFloatLayout.setBackgroundResource(R.color.transparent);
        mFloatLayout.setVisibility(View.GONE);

        mNegativeView.initialize();
        // new NegativeEntry(SystemUIApplication.getSystemUIContext()).initialize();

        pullDownDumperLayout.setOnLongClickListener(new PullDownDumperLayout.onLongClickListener() {
            @Override
            public void onLongClick() {
                AAOP_LogUtils.i(TAG, "pullDownDumperLayout:onLongClick");
                mNegativeView.mOnLongListener.onLongListener();
            }
        });
    }


    public void show() {
        if (null == windowsController) {
            windowsController = WindowsControllerImpl.getInstance();
        }
        ReportBcmManager.getInstance().sendNegativeReport("5002001", "", "打开负一屏主页");
        windowsController.notifyVisibility(SystemUIContent.TYPE_OF_QS_PANEL, View.VISIBLE);
        mFloatLayout.setVisibility(View.VISIBLE);
    }

    public void dismiss() {
        if (null == windowsController) {
            windowsController = WindowsControllerImpl.getInstance();
        }
        windowsController.notifyVisibility(SystemUIContent.TYPE_OF_QS_PANEL, View.GONE);

        mFloatLayout.setVisibility(View.GONE);

    }

    public boolean show(@NonNull MotionEvent event) {
        boolean fromOutside = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (80 >= event.getRawY()) {
                    show();
                    fromOutside = true;
                }
                break;
            case MotionEvent.ACTION_OUTSIDE:
                return false;
        }
        return pullDownDumperLayout.doAnimation(event, fromOutside);
    }

    public boolean isQsViewShow() {
        return pullDownDumperLayout.getVisibility() == View.VISIBLE;
    }

    public void isShowView() {
        if (pullDownDumperLayout.getVisibility() == View.VISIBLE) {
            pullDownDumperLayout.startCloseAnimation();
        } else {
            pullDownDumperLayout.startOpenAnimation();
            pullDownDumperLayout.setVisibility(View.VISIBLE);
        }
    }

    public void closeView() {
        if (pullDownDumperLayout == null) return;
        if (pullDownDumperLayout.getVisibility() == View.GONE) return;
        pullDownDumperLayout.startCloseAnimation();
    }


    public void onClick() {
        mNegativeView.onClick();
    }

}
