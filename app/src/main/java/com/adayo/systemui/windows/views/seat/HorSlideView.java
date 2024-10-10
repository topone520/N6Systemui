package com.adayo.systemui.windows.views.seat;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.functional.seat.SeatAdjustInfiniteLoopHandler;
import com.android.systemui.R;


public class HorSlideView extends View {
    private final String TAG = HorSlideView.class.getSimpleName();
    private static final float TICK_HEIGHT = 12f;
    private static final float THUMB_SIZE = 44;//滑块大小
    private Bitmap progressBitmap;
    private Paint backgroundPaint;
    private float progress = 58;  //默认中间 getWidth - bitmap.getWidth/2
    private float startX = 58;  //回弹距离 getWidth - bitmap.getWidth/2
    private OnHorSlideListener slideListener;
    private boolean isLeft;
    private boolean isRight;

    public void setSlideListener(OnHorSlideListener slideListener) {
        this.slideListener = slideListener;
    }

    public HorSlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        init();
    }

    public void setProgressBitmap(Bitmap bitmap) {
        progressBitmap = bitmap;
    }

    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(getResources().getColor(R.color.c15ffffff));
        backgroundPaint.setAntiAlias(true);
        progressBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ivi_ac_seat_seat_icon_n);

    }

    public void setProgress(float progress) {
        if (progress < 0 || progress > 100) return;
        this.progress = progress;
    }

    public interface OnSeekBarChangeListener {
        void onProgressChanged(float progress);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float startY = getHeight() / 2f - TICK_HEIGHT / 2f;
        float stopY = startY + TICK_HEIGHT;
        // 绘制默认背景，高度为12px，位置居中
        float backgroundRadius = TICK_HEIGHT / 2f; // 圆角半径
        RectF backgroundRect = new RectF(0, startY, getWidth(), stopY);
        canvas.drawRoundRect(backgroundRect, backgroundRadius, backgroundRadius, backgroundPaint);
        Log.d(TAG, "onDraw:  progress = " + progress);
        // 绘制滑块（圆形）
        float thumbY = getHeight() / 2f;
        Log.d(TAG, "onDraw: " + thumbY);
        if (progress > getWidth() - THUMB_SIZE) progress = getWidth() - THUMB_SIZE;
        if (progress < THUMB_SIZE / 2) progress = 0;
        canvas.drawBitmap(progressBitmap, progress, thumbY - progressBitmap.getHeight() / 2, null);
    }

    private boolean isOver;
    int VIEW_WIDTH = 160;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        float x = event.getX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (x >= startX && x < getWidth() / 2 + startX / 2) {
                    isOver = true;
                }
                if (x >= 0 && x <= getWidth() && isOver) {
                    progress = x - THUMB_SIZE / 2;//bitmap要更新left位置，需要减去bitmap.getWidth/2，不然会偏移
                    AAOP_LogUtils.d(TAG, "progress = " + progress);
                    if (progress > VIEW_WIDTH / 2 && slideListener != null) {
                        // 滑动位置在中间点右侧，且未触发右侧回调
                        if (!isRight) {
                            isRight = true;
                            isLeft = false;
                            slideListener.onAdjustAction(SeatSOAConstant.SEAT_ADJUST_UP);
                            AAOP_LogUtils.d(TAG, "-------------right");
                        }
                    } else {
                        // 滑动位置在中间点左侧，且未触发左侧回调
                        if (!isLeft) {
                            isLeft = true;
                            isRight = false;
                            slideListener.onAdjustAction(SeatSOAConstant.SEAT_ADJUST_DOWN);
                            AAOP_LogUtils.d(TAG, "-------------left");
                        }
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                isOver = false;
                isLeft = false;
                isRight = false;
                if (slideListener != null)
                    slideListener.onAdjustAction(SeatSOAConstant.SEAT_ADJUST_OFF);
                animateToStartPosition();
                break;
        }
        return true;
    }

    private void animateToStartPosition() {
        ValueAnimator animator = ValueAnimator.ofFloat(progress, startX);
        animator.setDuration(300);
        animator.addUpdateListener(animation -> {
            progress = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    public interface OnHorSlideListener {
        void onAdjustAction(int action);
    }
}




