package com.adayo.systemui.windows.views.hvac;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;

/**
 * 调节的出风口位置img
 */

public class HvacMDVAdjustView extends ImageView {
    private final static String TAG = HvacMDVAdjustView.class.getName();
    private GestureDetector gestureDetector;

    private static final int THRESHOLD = 10;
    private int totalScrollX = 0;
    private int totalScrollY = 0;
    private OutletListener listener;
    private Handler handler;
    private boolean longPressHandled = false;
    private boolean isFirstScroll = true;

    private int initSpeedX = 5;
    private int initSpeedY = 5;

    private boolean isX;
    private boolean isY;

    private int oldX;
    private int oldY;

    private float initialX, initialY;


    public void setListener(OutletListener listener) {
        this.listener = listener;
    }

    public HvacMDVAdjustView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        gestureDetector = new GestureDetector(context, new MyGestureListener());
        handler = new Handler(Looper.getMainLooper());

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOnTouchListener((v, event) -> {
            getParent().requestDisallowInterceptTouchEvent(true);
            if (longPressHandled) {
                gestureDetector.onTouchEvent(event);
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    AAOP_LogUtils.d(TAG, "down----------");
                    // 在按下时，记录初始坐标
                    initialX = event.getX();
                    initialY = event.getY();
                    // 使用 Handler 延迟 800 毫秒后处理长按事件
                    handler.postDelayed(() -> {
                        longPressHandled = true;
                        listener.onIsShowView(true);
                    }, 800);
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 在移动时，计算手指移动的距离
                    float moveX = event.getX() - initialX;
                    float moveY = event.getY() - initialY;
                    float distance = (float) Math.sqrt(moveX * moveX + moveY * moveY);

                    // 判断移动距离是否达到20像素的阈值
                    if (distance >= 20 && !longPressHandled) {
                        handler.removeCallbacksAndMessages(null);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // 在抬起或取消时，移除之前的长按处理
                    handler.removeCallbacksAndMessages(null);
                    longPressHandled = false;
                    isFirstScroll = true;
                    returnInit();
                    listener.onIsShowView(false);
                    break;
            }

            return true;
        });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!longPressHandled) return false;
            if (isFirstScroll) {
                distanceX = 0;
                distanceY = 0;
                isFirstScroll = false;
            }
            //  listener.onUpdateUI((int)distanceX,(int)distanceY);
            totalScrollX += distanceX;
            totalScrollY += distanceY;
            if (Math.abs(totalScrollX) >= THRESHOLD || Math.abs(totalScrollY) >= THRESHOLD) {
                initSpeedY += (int) totalScrollY / THRESHOLD;
                initSpeedX += (int) totalScrollX / THRESHOLD;
                if (initSpeedX < 0) initSpeedX = 0;

                if (initSpeedX > 11) initSpeedX = 11;

                if (initSpeedY < 0) initSpeedY = 0;

                if (initSpeedY > 11) initSpeedY = 11;

                if (oldX != initSpeedX) {
                    isX = true;
                    oldX = initSpeedX;
                    listener.onViewSlideAdjust(initSpeedX, initSpeedY, true, isY);
                } else isX = false;

                if (oldY != initSpeedY) {
                    isY = true;
                    oldY = initSpeedY;
                    listener.onViewSlideAdjust(initSpeedX, initSpeedY, isX, true);
                } else isY = false;
                returnInit();
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            returnInit();
            return super.onSingleTapUp(e);
        }
    }

    private void returnInit() {
        totalScrollX = 0;
        totalScrollY = 0;
        isX = false;
        isY = false;
    }

    public void updateSlideValue(int hor, int ver) {
        if (hor > 0 && hor < 11) initSpeedY = hor;
        if (ver > 0 && ver < 11) initSpeedX = ver;
    }

    public interface OutletListener {
        void onIsShowView(boolean isShow);

        //  void onUpdateUI(int X,int Y);
        void onViewSlideAdjust(int X, int Y, boolean isX, boolean isY);
    }
}

