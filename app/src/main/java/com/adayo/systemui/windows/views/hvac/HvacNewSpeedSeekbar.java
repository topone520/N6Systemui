package com.adayo.systemui.windows.views.hvac;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.android.systemui.R;

public class HvacNewSpeedSeekbar extends View {
    private final String TAG = HvacNewSpeedSeekbar.class.getSimpleName();
    private static final float TICK_HEIGHT = 12f;//指针高度
    private static final float THUMB_SIZE = 32f;//滑块大小 systemUI 是 32*32
    private static final int VIEW_GROUP_WIDTH = 438;//父容器的宽度

    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint thumbPaint;
    private Paint tickPaint;

    private int tickCount = 8;//指针数量
    private int progress;
    private float thumbX;
    private boolean isTouch;
    private int newProgress;

    private Handler handler = new Handler();

    private OnSeekBarChangeListener onSeekBarChangeListener;

    public HvacNewSpeedSeekbar(Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);

        init();
    }

    public HvacNewSpeedSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        init();
    }

    public HvacNewSpeedSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        init();
    }

    private void init() {
        // 默认背景颜色
        backgroundPaint = new Paint();
        backgroundPaint.setColor(getResources().getColor(R.color.c262e3b));
        backgroundPaint.setAntiAlias(true);

        // 滑动填充颜色
        progressPaint = new Paint();
        progressPaint.setColor(getResources().getColor(R.color.c8a96a4));
        progressPaint.setAntiAlias(true);

        // 滑块颜色
        thumbPaint = new Paint();
        thumbPaint.setColor(getResources().getColor(R.color.cededed));
        thumbPaint.setAntiAlias(true);

        //刻度颜色
        tickPaint = new Paint();
        tickPaint.setColor(getResources().getColor(android.R.color.white));
        tickPaint.setStrokeWidth(2);
        thumbPaint.setAntiAlias(true);
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        this.onSeekBarChangeListener = listener;
    }

    public interface OnSeekBarChangeListener {
        void onProgressChanged(int progress);
    }

    public void setTickCount(int count) {
        this.tickCount = count;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float startY = getHeight() / 2f - TICK_HEIGHT / 2f;  // 刻度尺的起始坐标
        float stopY = startY + TICK_HEIGHT;  // 刻度尺的终止坐标

        // 绘制默认背景，高度为12px，位置居中
        float backgroundRadius = TICK_HEIGHT / 2f; // 圆角半径
        RectF backgroundRect = new RectF(0, startY, getWidth(), stopY);
        canvas.drawRoundRect(backgroundRect, backgroundRadius, backgroundRadius, backgroundPaint);

        // 绘制滑动填充
        float progressX = thumbX;
        Log.d(TAG, "onDraw: proX" + progressX + "   thumbX = " + thumbX);
        // 绘制带有圆角的矩形（滑动填充）
        float progressRadius = TICK_HEIGHT / 2f; // 圆角半径
        RectF progressRect = new RectF(0, startY, thumbX, stopY);
        canvas.drawRoundRect(progressRect, progressRadius, progressRadius, progressPaint);
        // 绘制刻度尺
        for (int i = 0; i <= tickCount - 1; i++) {
            float x = i * (getWidth() - THUMB_SIZE / 2) / (tickCount - 1);
            // 第一个刻度线左边偏移15px
            if (i == 0) {
                x = 15;
            }
            Log.d(TAG, "onDraw: for x = " + x);

            if (thumbX >= x) {
                // 经过的刻度线颜色
                tickPaint.setColor(getResources().getColor(R.color.c262e3b));
            } else {
                // 未经过的刻度线颜色
                tickPaint.setColor(getResources().getColor(R.color.hvac_white));
            }
            canvas.drawLine(x, startY + 3, x, stopY - 3, tickPaint);
        }

        // 绘制滑块（圆形）
        float thumbRadius = THUMB_SIZE / 2f;
        float thumbY = getHeight() / 2f;

        // 边界检查，确保滑块不越界
        if (thumbX < thumbRadius) thumbX = thumbRadius;
        if (thumbX > getWidth() - thumbRadius) thumbX = getWidth() - thumbRadius;

        canvas.drawCircle(thumbX, thumbY, thumbRadius, thumbPaint);
    }


    public void setProgress(int progress) {
        AAOP_LogUtils.d(TAG, "progress = " + progress + "  isTouch = " + isTouch);
        newProgress = progress;
        if (progress < 0 || progress > tickCount || isTouch) return;
        this.progress = progress;
        Log.d(TAG, "setEEE: ");
        oldProgress = this.progress;
        if (progress == 1) {
            thumbX = (VIEW_GROUP_WIDTH / tickCount) * progress + THUMB_SIZE / 2;
        } else if (progress == tickCount - 1) {
            thumbX = VIEW_GROUP_WIDTH - THUMB_SIZE / 2;
        } else {
            thumbX = (VIEW_GROUP_WIDTH / tickCount) * progress + THUMB_SIZE;
        }
        invalidate();
    }


    private int oldProgress;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        float x = event.getX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                isTouch = true;
                if (x >= 0 && x <= getWidth()) {
                    thumbX = x;
                    progress = (int) (tickCount * x / getWidth());
                    if (onSeekBarChangeListener != null) {
                        Log.d(TAG, "onTouchEvent: ");
                        if (oldProgress != progress)
                            onSeekBarChangeListener.onProgressChanged(progress);
                        oldProgress = progress;
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                isTouch = false;
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(() -> setProgress(newProgress), 1500);
                break;
        }
        return true;
    }
}




