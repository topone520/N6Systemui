package com.adayo.systemui.windows.views.seat;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.functional.seat.SeatAdjustInfiniteLoopHandler;
import com.android.systemui.R;

public class VerSlideView extends View {
    private final String TAG = VerSlideView.class.getSimpleName();
    private static final float HEIGHT = 12f;
    private static final float THUMB_SIZE = 44;
    private Bitmap progressBitmap;
    private Paint backgroundPaint;
    private float progress = 58;  // 默认中间 getHeight - bitmap.getHeight/2
    private float startY = 58;  // 回弹距离 getHeight - bitmap.getHeight/2
    private final int VIEW_HEIGHT = 160;
    private OnVerSlideListener slideListener;
    private boolean isTop;
    private boolean isBot;

    public void setSlideListener(OnVerSlideListener slideListener) {
        this.slideListener = slideListener;
    }

    public VerSlideView(Context context, AttributeSet attrs) {
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float startX = getWidth() / 2f - HEIGHT / 2f;
        float stopX = startX + HEIGHT;
        float backgroundRadius = HEIGHT / 2f;
        RectF backgroundRect = new RectF(startX, 0, stopX, getHeight());
        canvas.drawRoundRect(backgroundRect, backgroundRadius, backgroundRadius, backgroundPaint);
        float thumbX = getWidth() / 2f;
        if (progress > getHeight() - THUMB_SIZE) progress = getHeight() - THUMB_SIZE;
        if (progress < THUMB_SIZE / 2) progress = 0;
        canvas.drawBitmap(progressBitmap, thumbX - progressBitmap.getWidth() / 2, progress, null);
    }

    private boolean isOver;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (y >= startY && y < getHeight() / 2 + startY / 2) {
                    isOver = true;
                }
                if (y >= 0 && y <= getHeight() && isOver) {
                    progress = y - THUMB_SIZE / 2;
                    if (progress > getHeight() / 2 && slideListener != null && !isTop) {
                        // 滑动位置在中间点以上，且未触发向上回调
                        isTop = true;
                        isBot = false;
                        slideListener.onAdjustAction(SeatSOAConstant.SEAT_ADJUST_UP);
                    } else if (progress <= getHeight() / 2 && slideListener != null && !isBot) {
                        // 滑动位置在中间点以下，且未触发向下回调
                        isBot = true;
                        isTop = false;
                        slideListener.onAdjustAction(SeatSOAConstant.SEAT_ADJUST_DOWN);
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                isOver = false;
                isTop = false;
                isBot = false;
                slideListener.onAdjustAction(SeatSOAConstant.SEAT_ADJUST_OFF);
                animateToStartPosition();
                break;
        }
        return true;
    }

    private void animateToStartPosition() {
        ValueAnimator animator = ValueAnimator.ofFloat(progress, startY);
        animator.setDuration(300);
        animator.addUpdateListener(animation -> {
            progress = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    public interface OnVerSlideListener {
        void onAdjustAction(int action);
    }
}






