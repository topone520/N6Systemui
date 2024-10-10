package com.adayo.systemui.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.PopupWindow;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.R;

public class PopupsManager {
    private volatile static PopupsManager popupsManager;
    private PopupWindow mPopup;

    private PopupsManager() {
        if (this.mPopup == null) {
            mPopup = new PopupWindow();
        }
    }

    public static PopupsManager getInstance() {
        if (popupsManager == null) {
            synchronized (PopupsManager.class) {
                if (popupsManager == null) {
                    popupsManager = new PopupsManager();
                }
            }
        }
        return popupsManager;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
        }
    };

    private final Runnable mLongClickRunnable = () -> {
        LogUtil.d("mLongClickRunnable");
        dismiss();
    };

    private View currentView;
    public void showAtLocation(View view, View popupView, int x, int y, boolean focusable, int time, int style, int type, boolean isOnTouch) {
        if(this.mPopup != null){
            mPopup.dismiss();
        }
        currentView = view;
        if (this.mPopup == null) {
            mPopup = new PopupWindow();
        }
        this.mPopup.setContentView(popupView);
        this.mPopup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.mPopup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.mPopup.setFocusable(focusable);
        this.mPopup.setOutsideTouchable(true);
        this.mPopup.setWindowLayoutType(type);
        this.mPopup.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
        this.mPopup.setAnimationStyle(style);
        AAOP_HSkin.getWindowViewManager().addWindowView(mPopup.getContentView());
        currentView.setTag(true);
        this.mPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                LogUtil.d("setOnDismissListener");
                mHandler.removeCallbacks(mLongClickRunnable);
                dismiss();
            }
        });
        mPopup.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                LogUtil.d("setOnTouchListener");
                mHandler.removeCallbacks(mLongClickRunnable);
                mHandler.postDelayed(mLongClickRunnable, time);
                return false;
            }
        });


        ((ViewGroup) this.mPopup.getContentView().getParent()).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int[] location = new int[2];
                view.getLocationOnScreen(location);
                int x = location[0];
                int y = location[1];

                LogUtil.d("x " + x + " y " + y + "w " + view.getHeight() + " h " + view.getHeight());
                LogUtil.d("event.getX " + event.getRawX() + " event.getRawY " + event.getRawY() );

                int leftx = x;
                int rightx = x + view.getWidth();
                int topy = y;
                int bottomy = y + view.getHeight();

                if (event.getRawX() > leftx && event.getRawX() < rightx && event.getRawY() > topy && event.getRawY() < bottomy){
                    return isOnTouch;
                }
                return false;
            }
        });

        mHandler.removeCallbacks(mLongClickRunnable);
        mHandler.postDelayed(mLongClickRunnable, time);
//        NavigationBar.getInstance().closeAllApp();

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                LogUtil.d("setOnTouchListener");
                return false;
            }
        });
//        animationStart(popupView,animationDirection);
    }

    public void dismiss() {
        if(null != currentView) {
            currentView.setTag(false);
        }
        if (this.mPopup != null && this.mPopup.isShowing()) {
            this.mPopup.dismiss();
        }
    }
}
