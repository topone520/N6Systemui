package com.adayo.systemui.functional.sildeback;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;

/**
 * 滑动返回视觉动画处理交接view
 */
@SuppressLint("ViewConstructor")
class SlideBackView extends View {
    private ISlideView slideView;
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private ValueAnimator animator;
    private float rate = 0;//曲线的控制点

    private int direction;

    SlideBackView(Context context, @NonNull ISlideView slideView) {
        super(context);
        setSlideView(slideView);
    }

    public SlideBackView setSlideView(@NonNull ISlideView slideView) {
        this.slideView = slideView;
        setLayoutParams(new SlideControlLayout.LayoutParams(slideView.getWidth(), slideView.getHeight()));

        return this;
    }

    public ISlideView getSlideView() {
        return slideView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        slideView.onDraw(canvas, rate,direction);
    }

    public void updateRate(float updateRate, boolean hasAnim ,int direction) {
        if (updateRate > getWidth()) {
            updateRate = getWidth();
        }
        this.direction = direction;
        if (rate == updateRate) {
            return;
        }
        cancelAnim();
        if (!hasAnim) {
            rate = updateRate;
            invalidate();
            if (rate == 0) {
                setVisibility(GONE);
            }else{
                setVisibility(VISIBLE);
            }
        }

        animator = ValueAnimator.ofFloat(rate, updateRate);
        animator.setDuration(200);
        animator.addUpdateListener(animation -> {
            rate = (Float) animation.getAnimatedValue();
            postInvalidate();
            if (rate == 0) {
                setVisibility(GONE);
            }else{
                setVisibility(VISIBLE);
            }

        });
        animator.setInterpolator(DECELERATE_INTERPOLATOR);
        animator.start();
    }

    private void cancelAnim() {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        cancelAnim();
        if (rate != 0) {
            rate = 0;
            invalidate();
        }
        super.onDetachedFromWindow();
    }


}