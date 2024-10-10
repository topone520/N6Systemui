package com.adayo.systemui.windows.views.seat;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.android.systemui.R;

public class CrossSlideView extends View {
    private final String TAG = CrossSlideView.class.getSimpleName();
    private static final float LINE_SIZE = 8f;
    private static final float THUMB_SIZE = 44f;
    private Paint linePaint;
    private Paint thumbPaint;
    private float progressX = 80;
    private float progressY = 80;
    private Paint smallCirclePaint;
    private static final float THRESHOLD_SIZE = 15f;
    private float lastTouchX, lastTouchY;
    private boolean isUp, isDown;

    private boolean isVer, isSlide;

    private OnHeightProgressListener onHeightProgressListener;
    private OnWightProgressListener onWightProgressListener;

    public void setOnHeightProgressListener(OnHeightProgressListener onHeightProgressListener) {
        this.onHeightProgressListener = onHeightProgressListener;
    }

    public void setOnWightProgressListener(OnWightProgressListener onWightProgressListener) {
        this.onWightProgressListener = onWightProgressListener;
    }

    public CrossSlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setLayerType(LAYER_TYPE_HARDWARE, null);
        smallCirclePaint = new Paint();
        smallCirclePaint.setColor(Color.WHITE); // 根据需要调整颜色
        smallCirclePaint.setStyle(Paint.Style.FILL);
        smallCirclePaint.setAntiAlias(true);
        linePaint = new Paint();
        linePaint.setColor(getResources().getColor(R.color.c15ffffff));
        linePaint.setStrokeWidth(LINE_SIZE);
        linePaint.setAntiAlias(true);
        thumbPaint = new Paint();
        thumbPaint.setColor(getResources().getColor(R.color.c30ffffff));
        thumbPaint.setStyle(Paint.Style.STROKE);
        thumbPaint.setStrokeWidth(5f); // 根据需要调整描边宽度
        thumbPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制两条线
        int TICK_HEIGHT = 12;
        float startY = getHeight() / 2f - TICK_HEIGHT / 2f;
        float stopY = startY + TICK_HEIGHT;
        float backgroundRadius = TICK_HEIGHT / 2f;
        RectF backgroundRect = new RectF(0, startY, getWidth(), stopY);
        canvas.drawRoundRect(backgroundRect, backgroundRadius, backgroundRadius, linePaint);
        // canvas.drawRoundRect(backgroundRect, backgroundRadius, backgroundRadius, linePaint);
        float startX = getWidth() / 2f - TICK_HEIGHT / 2f;  // 刻度尺的起始坐标
        float stopX = startX + TICK_HEIGHT;  // 刻度尺的终止坐标

        // 绘制默认背景，宽度为12px，位置居中
        float backgroundRadius1 = TICK_HEIGHT / 2f; // 圆角半径
        RectF backgroundRect1 = new RectF(startX, 0, stopX, getHeight());
        canvas.drawRoundRect(backgroundRect1, backgroundRadius1, backgroundRadius, linePaint);

        // 绘制滑块
        float thumbRadius = THUMB_SIZE / 2f;
        // 边界检查，确保滑块不越界
        if (progressX < thumbRadius) progressX = thumbRadius;
        if (progressX > getWidth() - thumbRadius) progressX = getWidth() - thumbRadius;
        if (progressY < thumbRadius) progressY = thumbRadius;
        if (progressY > getHeight() - thumbRadius) progressY = getHeight() - thumbRadius;
        canvas.drawCircle(progressX, progressY, thumbRadius, thumbPaint);
        canvas.drawCircle(progressX, progressY, 17, smallCirclePaint);
    }

    private float startX;
    private float startY;
    private static final int THRESHOLD = 10; // 阈值

    private int slideThreshold = 10;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int VIEW_WIDTH_HEIGHT = 160;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 记录手指按下的坐标
                lastTouchX = x;
                lastTouchY = y;
                startX = event.getX();
                startY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                float endX = event.getX();
                float endY = event.getY();
                float dx = endX - startX;
                float dy = endY - startY;
                slideThreshold--;
                if (!isSlide) {
                    if (slideThreshold < 0) isSlide = true;
                    // 判断滑动方向
                    if (Math.abs(dx) > Math.abs(dy)) {
                        if (Math.abs(dx) > THRESHOLD) {
                            isVer = false;
                            AAOP_LogUtils.d(TAG, "----------------11111false");
                        }
                    } else {
                        if (Math.abs(dy) > THRESHOLD) {
                            isVer = true;
                            AAOP_LogUtils.d(TAG, "----------------11111tr");
                        }
                    }
                } else {
                    // 判断当前滑动方向
                    float deltaX = x - lastTouchX;
                    float deltaY = y - lastTouchY;
                    // 在阈值范围内，根据当前滑动方向更新滑块位置
                    if (isVer) {
                        // 在垂直滑动时，实时根据 Y 轴更新
                        progressY += deltaY;
                        AAOP_LogUtils.d(TAG, "---------------Y = " + progressY);
                        if (progressY > VIEW_WIDTH_HEIGHT / 2) {
                            if (!isDown) {
                                isDown = true;
                                isUp = false;
                                onHeightProgressListener.onAdjustAction(SeatSOAConstant.SEAT_ADJUST_DOWN);
                            }

                        } else {
                            if (!isUp) {
                                isDown = false;
                                isUp = true;
                                onHeightProgressListener.onAdjustAction(SeatSOAConstant.SEAT_ADJUST_UP);
                            }
                        }
                    } else {
                        // 在水平滑动时，实时根据 X 轴更新
                        progressX += deltaX;
                        AAOP_LogUtils.d(TAG, "---------------X = " + progressX);
                        if (progressX > VIEW_WIDTH_HEIGHT / 2) {
                            if (!isUp) {
                                isDown = false;
                                isUp = true;
                                onWightProgressListener.onAdjustAction(SeatSOAConstant.SEAT_ADJUST_UP);
                            }

                        } else {
                            if (!isDown) {
                                isDown = true;
                                isUp = false;
                                onWightProgressListener.onAdjustAction(SeatSOAConstant.SEAT_ADJUST_DOWN);
                            }
                        }
                    }
                    lastTouchX = x;
                    lastTouchY = y;
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 在手指抬起时，根据滑块的位置计算目标位置，并执行平移动画
                animateToCenter();
                if (!isVer) {
                    onWightProgressListener.onAdjustAction(SeatSOAConstant.SEAT_ADJUST_OFF);
                } else {
                    onHeightProgressListener.onAdjustAction(SeatSOAConstant.SEAT_ADJUST_OFF);
                }
                isUp = false;
                isDown = false;
                isVer = false;
                isSlide = false;
                slideThreshold = 10;
                break;
        }
        return true;
    }

    private void animateToCenter() {
        ValueAnimator animatorX;
        ValueAnimator animatorY;

        // 计算X轴的平移动画
        if (progressX < getWidth() / 2f) {
            // 左侧平移到中间
            animatorX = ValueAnimator.ofFloat(progressX, getWidth() / 2f);
        } else {
            // 右侧平移到中间
            animatorX = ValueAnimator.ofFloat(progressX, getWidth() / 2f);
        }
        animatorX.addUpdateListener(animation -> {
            progressX = (float) animation.getAnimatedValue();
            invalidate();
        });
        // 计算Y轴的平移动画
        if (progressY < getHeight() / 2f) {
            // 上侧平移到中间
            animatorY = ValueAnimator.ofFloat(progressY, getHeight() / 2f);
        } else {
            // 下侧平移到中间
            animatorY = ValueAnimator.ofFloat(progressY, getHeight() / 2f);
        }
        animatorY.addUpdateListener(animation -> {
            progressY = (float) animation.getAnimatedValue();
            invalidate();
        });
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorX, animatorY);
        animatorSet.setDuration(300); // 设置动画时长
        animatorSet.start();
    }

    public interface OnHeightProgressListener {
        void onAdjustAction(int action);
    }

    public interface OnWightProgressListener {
        void onAdjustAction(int action);
    }
}
