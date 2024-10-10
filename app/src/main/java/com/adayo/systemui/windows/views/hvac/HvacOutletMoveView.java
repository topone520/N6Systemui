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


public class HvacOutletMoveView extends ImageView {
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

    public void setListener(OutletListener listener) {
        this.listener = listener;
    }

    public HvacOutletMoveView(Context context, @Nullable AttributeSet attrs) {
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
        //确保imageview已经初始化完成，不然导致第一次触摸该view获取不到事件
        setOnTouchListener((v, event) -> {
            getParent().requestDisallowInterceptTouchEvent(true);
            if (longPressHandled) {
                gestureDetector.onTouchEvent(event);
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 在按下时，使用 Handler 延迟 800 毫秒后处理长按事件
                    handler.postDelayed(() -> {
                        longPressHandled = true;
                        listener.onViewMove();
                    }, 800);
                    break;
                case MotionEvent.ACTION_MOVE:
                    handler.removeCallbacksAndMessages(null);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // 在抬起或取消时，移除之前的长按处理
                    handler.removeCallbacksAndMessages(null);
                    longPressHandled = false;
                    isFirstScroll = true;
                    returnInit();
                    listener.onViewUp();
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
                } else isX = false;

                if (oldY != initSpeedY) {
                    isY = true;
                    oldY = initSpeedY;
                } else isY = false;
                listener.onViewListener(initSpeedX, initSpeedY, isX, isY);
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

    public interface OutletListener {
        void onViewUp();

        void onViewMove();

        void onViewListener(int X, int Y, boolean isX, boolean isY);
    }
}

