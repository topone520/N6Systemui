package com.adayo.systemui.functional.sildeback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.android.systemui.SystemUIApplication;

/**
 * 滑动手势控制等
 */
@SuppressLint("ViewConstructor")
public class SlideControlLayout extends FrameLayout {

    private final SlideBackView slideBackView;
    private final OnSlide onSlide;
    private int canSlideWidth;
    private boolean enable = true;

    private float downX;
    private float moveX;
    private boolean startDrag = false;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;

    private int direction = DIRECTION_LEFT;

    private final static int DIRECTION_LEFT = 0;
    private final static int DIRECTION_RIGHT = 1;

    public SlideControlLayout(@NonNull Context context, int canSlideWidth, ISlideView slideView, OnSlide onSlide) {
        super(context);
        this.canSlideWidth = canSlideWidth;
        this.onSlide = onSlide;
        slideBackView = new SlideBackView(context, slideView);
        mLayoutParams = new WindowManager.LayoutParams(20, ViewGroup.LayoutParams.MATCH_PARENT, 2071, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSLUCENT);

        addView(slideBackView);
    }


    public SlideControlLayout attachToActivity(int direction) {

        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        mLayoutParams.gravity = direction;

        mWindowManager.addView(this, mLayoutParams);
        return this;
    }

    private void onBack() {
        if (onSlide == null) {
            Utils.getActivityContext(getContext()).onBackPressed();
        } else {
            onSlide.onSlideBack();
        }
    }


    private void setSlideViewY(SlideBackView view, int x, int y) {
        if (!view.getSlideView().scrollVertical()) {
            scrollTo(100, 0);
            return;
        }
        scrollTo(100, -(y - view.getHeight() / 2));
    }

    //region 手势控制
    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!enable) {
            return false;
        }

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (motionEvent.getRawX() <= canSlideWidth) {
                    return true;
                }
        }

        return super.onInterceptTouchEvent(motionEvent);
    }

    public boolean onDockTouchEvent(MotionEvent motionEvent) {
        if (!enable) {
            return super.onTouchEvent(motionEvent);
        }
        return SlideBackManager.getInstance().onTouchEvent(motionEvent.getAction(), motionEvent.getRawX(), motionEvent.getRawY()) || super.onTouchEvent(motionEvent);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!enable) {
            return super.onTouchEvent(motionEvent);
        }
        return SlideBackManager.getInstance().onTouchEvent(motionEvent.getAction(), motionEvent.getRawX(), motionEvent.getRawY()) || super.onTouchEvent(motionEvent);
    }


    public void onTouchEvent(int action, float x, float y) {
        float currentX = x;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                float currentY = y;
                if (currentY > Utils.d2p(getContext(), 100) && currentX <= canSlideWidth) {
                    downX = currentX;
                    startDrag = true;
                    direction = Gravity.LEFT;
                    slideBackView.updateRate(0, false, direction);
                    setSlideViewY(slideBackView, 0, (int) (y));
                }
                if (currentY > Utils.d2p(getContext(), 100) && (3840 - currentX) <= canSlideWidth) {
                    downX = currentX;
                    startDrag = true;
                    direction = Gravity.RIGHT;
                    slideBackView.updateRate(0, false, direction);
                    setSlideViewY(slideBackView, 1870, (int) (y));
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
                if (startDrag && moveX >= slideBackView.getWidth() * 2) {
                    onBack();
                    slideBackView.updateRate(0, false, direction);
                } else if (startDrag && (3840 - moveX) >= slideBackView.getWidth() * 2) {
                    onBack();
                    slideBackView.updateRate(0, false, direction);
                } else {

                    slideBackView.updateRate(0, startDrag, direction);
                }
                moveX = 0;
                startDrag = false;
                break;
        }
    }


}
