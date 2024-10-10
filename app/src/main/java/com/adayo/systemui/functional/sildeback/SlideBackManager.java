package com.adayo.systemui.functional.sildeback;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.adayo.systemui.contents.SystemUIContent;
import com.android.systemui.SystemUIApplication;

public class SlideBackManager {

    private final static String TAG = "SlideBackManager";
    private volatile static SlideBackManager slideBackManager;

    private SlideBackView slideBackView;
    private WindowManager mWindowManager;

    private WindowManager.LayoutParams mLayoutParams;

    ISlideView slideView;

    private int canSlideWidth = 28;
    private boolean enable = true;

    private float downX;
    private float moveX;
    private boolean startDrag = false;

    private int direction = Gravity.LEFT;

    private boolean isShow = false;

    public static SlideBackManager getInstance() {
        if (slideBackManager == null) {
            synchronized (SlideBackManager.class) {
                if (slideBackManager == null) {
                    slideBackManager = new SlideBackManager();
                }
            }
        }
        return slideBackManager;
    }

    public SlideBackManager() {
        initView();
    }

    private void initView() {

        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        if (slideView == null) {
            slideView = new DefaultSlideView(SystemUIApplication.getSystemUIContext());
        }

        slideBackView = new SlideBackView(SystemUIApplication.getSystemUIContext(), slideView);
        mLayoutParams = new WindowManager.LayoutParams(66, 250, 2071, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);

    }


    public void show(int gravity) {
        if (isShow) {
            return;
        }
        isShow = true;
        mLayoutParams.gravity = Gravity.TOP | gravity;
        mLayoutParams.y = 0;
        mWindowManager.addView(slideBackView, mLayoutParams);
    }

    public void dissmis() {
        isShow = false;
        mWindowManager.removeView(slideBackView);
    }

    private void setSlideViewY(SlideBackView view, int y) {
        mLayoutParams.y = y - 100;
        mWindowManager.updateViewLayout(slideBackView, mLayoutParams);
    }

    public boolean onTouchEvent(int action, float x, float y) {
        Log.d(TAG, "onTouchEvent() called with: action = [" + action + "], x = [" + x + "], y = [" + y + "]");
        float currentX = x;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                float currentY = y;
                if (currentY > Utils.d2p(SystemUIApplication.getSystemUIContext(), 100) && currentX <= canSlideWidth) {
                    downX = currentX;
                    startDrag = true;
                    direction = Gravity.LEFT;
                    slideBackView.updateRate(0, false, direction);
                }
                if (currentY > Utils.d2p(SystemUIApplication.getSystemUIContext(), 100) && (3840 - currentX) <= canSlideWidth) {
                    downX = currentX;
                    startDrag = true;
                    direction = Gravity.RIGHT;
                    slideBackView.updateRate(0, false, direction);
                }
                if (startDrag) {
                    show(direction);
                    setSlideViewY(slideBackView, (int) (y));
                }
                Log.d("SlideControlLayout", " ACTION_DOWN  currentX" + currentX + " currentY" + currentY + " direction" + direction);
                break;
            case MotionEvent.ACTION_MOVE:
                if (startDrag) {
                    moveX = currentX - downX;
                    Log.d("SlideControlLayout", " ACTION_MOVE  moveX" + moveX + " currentX" + currentX + " downX" + downX + " getWidth" + slideBackView.getWidth());
                    if (Math.abs(moveX) <= slideBackView.getWidth() * 2) {
                        slideBackView.updateRate(Math.abs(moveX) / 2, false, direction);
                    } else {
                        slideBackView.updateRate(slideBackView.getWidth(), false, direction);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (startDrag && Math.abs(moveX) >= slideBackView.getWidth() * 2) {
                    onBack();
                    slideBackView.updateRate(0, false, direction);
                } else {
                    slideBackView.updateRate(0, startDrag, direction);
                }
                moveX = 0;
                if (startDrag) {
                    dissmis();
                }
                startDrag = false;
                break;
            default:
                break;
        }
        return startDrag;
    }

    public void onBack() {
//        if(QsPanel.getInstance().isOpen()){
//            QsPanel.getInstance().dismiss();
//            return;
//        }
//        if (HvacView.getInstance().isShowing()){
//            HvacView.getInstance().dismiss();
//            return;
//        }
        new Thread(() -> {
            try {
                Instrumentation inst = new Instrumentation();
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
            } catch (Exception e) {
                Log.w(SystemUIContent.TAG, e.getMessage());
            }
        }).start();
    }


}
