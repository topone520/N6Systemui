package com.adayo.systemui.windows.views.hvac;



import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.android.systemui.R;

public class VerticalSeekBar extends View {
    private static final String TAG = "VerticalSeekBar1";
    /**
     * 加减按键画笔
     */
    private Paint buttonPaint;
    /**
     * progressbar画笔
     */
    private Paint barPaint;
    /**
     * 滑块thumb画笔
     */
    private Paint thumbPaint;
    /**
     * 加减按键线条宽度
     */
    private int buttonStrockWidth;
    /**
     * 加减按键长度
     */
    private int buttonWidth;
    /**
     * progressbar线条宽度
     */
    private int barStrockWidth;
    /**
     * 滑块thumb线条宽度
     */
    private int thumbStrockWidth;
    /**
     * 进度条color
     */
    private int barColor;
    /**
     * 进度color
     */
    private int barOnColor;
    /**
     * 滑块thumb颜色
     */
    private int thumbColor;

    /**
     * 滑块thumb线条长度
     */
    private int thumbWidth;
    /**
     * 最大值
     */
    private int maxProgress = 100;
    /**
     * 当前值
     */
    private int progress = 00;
    /**
     * view宽高
     */
    private int width, height;
    /**
     * progressbar的长度
     */
    private int progressBarLength;

    /**
     * 滑块所处位置
     * 0 中间值
     * 1 最大值
     * -1 最小值
     */
    private int state = 0;
    /**
     * progressbar头部和尾部的padding
     */
    private int endPadding;
    /**
     * Y轴方向移动的距离
     */
    private int moveY = 0;
    /**
     * Y轴上滑块所在位置
     */
    private int location;
    /**
     * 按下时X,Y轴按下位置
     */
    private int y;
    private int x;
    private int moveX;

    private RectF deleteF;
    private RectF addF;
    //是否是竖向
    private boolean Horitation;
    //动画执行时间
    private int mDuration = 200;
    //是否反向绘制
    private boolean isReverse = false;

    public VerticalSeekBar(Context context) {
        this(context, null);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalSeekBar);
        buttonStrockWidth = typedArray.getDimensionPixelOffset(R.styleable.VerticalSeekBar_button_strock_width, 5);
        barStrockWidth = typedArray.getDimensionPixelOffset(R.styleable.VerticalSeekBar_progressbar_strock_width, 15);
        //滑块的高度
        thumbStrockWidth = typedArray.getDimensionPixelOffset(R.styleable.VerticalSeekBar_thumb_strock_width, 64);
        buttonWidth = typedArray.getDimensionPixelOffset(R.styleable.VerticalSeekBar_button_width, 15);
        thumbWidth = typedArray.getDimensionPixelOffset(R.styleable.VerticalSeekBar_thumb_width, 60);
        endPadding = typedArray.getDimensionPixelOffset(R.styleable.VerticalSeekBar_end_padding, 0);
        Horitation = typedArray.getBoolean(R.styleable.VerticalSeekBar_Horitation, false);
        barColor = typedArray.getColor(R.styleable.VerticalSeekBar_progressbar_color, Color.WHITE);
        barOnColor = typedArray.getColor(R.styleable.VerticalSeekBar_progressbar_on_color, Color.BLUE);
        thumbColor = typedArray.getColor(R.styleable.VerticalSeekBar_thumb_color, Color.WHITE);
        isReverse = typedArray.getBoolean(R.styleable.VerticalSeekBar_isReverse, false);
        endPadding = barStrockWidth;

        typedArray.recycle();
        //设置竖直方向
        buttonPaint = new Paint();
        barPaint = new Paint();
        thumbPaint = new Paint();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int defaultValue = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getDisplayMetrics());
        width = measureHandler(widthMeasureSpec, defaultValue);
        height = measureHandler(heightMeasureSpec, defaultValue);
        if (!Horitation) {
            progressBarLength = height - endPadding * 2;
        } else {
            progressBarLength = width - endPadding * 2;
        }
        if (!Horitation) {
            if (isReverse) {
                location = (int) (height - (endPadding + progressBarLength * (1 - (float) progress / (float) maxProgress)));
            } else {
                location = (int) (endPadding + progressBarLength * (1 - (float) progress / (float) maxProgress));
            }
        } else {
            if (isReverse) {
                location = (int) (width - (endPadding + progressBarLength * (1 - (float) progress / (float) maxProgress)));
            } else {
                location = (int) (endPadding + progressBarLength * (1 - (float) progress / (float) maxProgress));
            }
        }
        Log.d(TAG, "onMeasure---width:" + width + ",height:" + height);

        setMeasuredDimension(width, height);
    }

    private DisplayMetrics getDisplayMetrics() {
        return getResources().getDisplayMetrics();
    }

    /**
     * 测量
     *
     * @param measureSpec
     * @param defaultSize
     * @return
     */
    private int measureHandler(int measureSpec, int defaultSize) {

        int result = defaultSize;
        int measureMode = MeasureSpec.getMode(measureSpec);
        int measureSize = MeasureSpec.getSize(measureSpec);
        if (measureMode == MeasureSpec.EXACTLY) {
            result = measureSize;
        } else if (measureMode == MeasureSpec.AT_MOST) {
            result = Math.min(defaultSize, measureSize);
        }
        return result;
    }

    /**
     * 显示进度动画效果（根据当前已有进度开始）
     *
     * @param toprogress
     */
    public void showAppendAnimation(int toprogress) {
        showAnimation(progress, toprogress, mDuration);
    }

    /**
     * 显示进度动画效果
     *
     * @param progress
     */
    public void showAnimation(int progress) {
        showAnimation(progress, mDuration);
    }

    /**
     * 显示进度动画效果
     *
     * @param progress
     * @param duration 动画时长
     */
    public void showAnimation(int progress, int duration) {
        showAnimation(0, progress, duration);
    }

    /**
     * 显示进度动画效果，从from到to变化
     *
     * @param from
     * @param to
     * @param duration 动画时长
     */
    public void showAnimation(int from, int to, int duration) {
        showAnimation(from, to, duration, null);
    }

    /**
     * 显示进度动画效果，从from到to变化
     *
     * @param from
     * @param to
     * @param duration 动画时长
     * @param listener
     */
    public void showAnimation(final int from, final int to, int duration, Animator.AnimatorListener listener) {
        Log.d(TAG, "showAnimation---progress: " + from + ",toprogress:" + to);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(from, to);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(duration);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                int maxValue = 0;
                int minValue = 0;
                maxValue = from > to ? from : to;
                minValue = from > to ? to : from;

                if (value > maxValue) {
                    value = maxValue;
                }
                if (value < minValue) {
                    value = minValue;
                }
                Log.d(TAG, "onAnimationUpdate: "+value);
                setProgress(value);
            }
        });

        if (listener != null) {
            valueAnimator.removeAllUpdateListeners();
            valueAnimator.addListener(listener);
        }

        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawButton(canvas);
        drawBar(canvas);
    }

    /**
     * 绘制加减号
     */
    private void drawButton(Canvas canvas) {
        buttonPaint.setAntiAlias(true);
        buttonPaint.setColor(Color.WHITE);
        buttonPaint.setStrokeWidth(buttonStrockWidth);
        buttonPaint.setStrokeCap(Paint.Cap.ROUND);

        //开始点
        float pointXS = (float) width / 2f;
        float pointYS = (float) (height - endPadding);

        //结束点
        float pointXE = (float) width / 2f;
        float pointYE = (float) endPadding;

        deleteF = new RectF();
        addF = new RectF();

//        deleteF.left = pointXS - buttonWidth;
//        deleteF.top = pointYS + buttonWidth * 3 - buttonWidth;
//        deleteF.right = pointXS + buttonWidth;
//        deleteF.bottom = pointYS + buttonWidth * 3 + buttonWidth;
//
//        addF.left = pointXE - buttonWidth;
//        addF.top = pointYE + buttonWidth * 3 - buttonWidth;
//        addF.right = pointXE + buttonWidth;
//        addF.bottom = pointYE + buttonWidth * 3 + buttonWidth;

        //减号
        canvas.drawLine(pointXS - buttonWidth / 2f,
                pointYS + endPadding / 2f,
                pointXS + buttonWidth / 2f,
                pointYS + endPadding / 2f,
                buttonPaint);

        //加号
        //横
        canvas.drawLine(pointXE - buttonWidth / 2f,
                pointYE - endPadding / 2f - buttonWidth / 2f,
                pointXE + buttonWidth / 2f,
                pointYE - endPadding / 2f - buttonWidth / 2f,
                buttonPaint);

        //竖
        canvas.drawLine(pointXE,
                pointYE - endPadding / 2f - buttonWidth,
                pointXE,
                pointYE - endPadding / 2f + buttonWidth,
                thumbPaint);
    }

    /**
     * 绘制progressbar
     */
    private void drawBar(Canvas canvas) {
        barPaint.setStyle(Paint.Style.FILL);
        barPaint.setAntiAlias(true);
        barPaint.setColor(isReverse ? barColor : barOnColor);
        barPaint.setStrokeWidth(barStrockWidth);
        barPaint.setStrokeCap(Paint.Cap.ROUND);
        if (!Horitation) {
            int nonProHeight = location + moveY;
            state = 0;
            if (nonProHeight >= height - endPadding) {
                nonProHeight = height - endPadding;
                state = 1;
            }
            if (nonProHeight <= endPadding) {
                nonProHeight = endPadding;
                state = -1;
            }
//            Log.d(TAG, "drawBar: nonProHeight:" + nonProHeight);
            if (trackListener != null) {
                if (isReverse) {
                    progress = (int) ((1 - (float) (nonProHeight - endPadding) / (float) progressBarLength) * 100);
                } else {
                    progress = (int) ((float) (nonProHeight - endPadding) / (float) progressBarLength * 100);
                }
                trackListener.tracking(progress);
            }
            //绘制上半段
            canvas.drawLine(width / 2f, endPadding, width / 2f, nonProHeight, barPaint);

            //绘制下半段
            int progressHeight = nonProHeight;
            barPaint.setColor(isReverse ? barOnColor : barColor);
            canvas.drawLine(width / 2f, progressHeight, width / 2f, height - endPadding, barPaint);
            drawThumb(progressHeight, canvas);
        } else {
            int nonProHeight = location + moveX;
            state = 0;
            if (nonProHeight >= width - endPadding) {
                nonProHeight = width - endPadding;
                state = 1;
            }
            if (nonProHeight <= endPadding) {
                nonProHeight = endPadding;
                state = -1;
            }
//            Log.d(TAG, "drawBar: nonProHeight:" + nonProHeight);
            if (trackListener != null) {
                if (isReverse) {
                    progress = (int) ((1 - (float) (nonProHeight - endPadding) / (float) progressBarLength) * 100);
                } else {
                    progress = (int) ((float) (nonProHeight - endPadding) / (float) progressBarLength * 100);
                }

                trackListener.tracking(progress);
            }
            //绘制上半段
            canvas.drawLine(endPadding, height / 2f, nonProHeight, height / 2f, barPaint);

            //绘制下半段
            int progressHeight = nonProHeight;
            barPaint.setColor(isReverse ? barOnColor : barColor);
            canvas.drawLine(nonProHeight, height / 2f, width - endPadding, height / 2f, barPaint);
            drawThumb(progressHeight, canvas);
        }
    }

    /**
     * 绘制thumb
     */
    private void drawThumb(int location, Canvas canvas) {
        thumbPaint.setAntiAlias(true);
        thumbPaint.setColor(thumbColor);
        thumbPaint.setStrokeWidth(thumbStrockWidth);
        thumbPaint.setStrokeCap(Paint.Cap.ROUND);
        if (!Horitation) {
            canvas.drawLine((width - thumbWidth) / 2f, location, (width + thumbWidth) / 2f, location, thumbPaint);
        } else {
            canvas.drawLine(location, (height - thumbWidth) / 2f, location, (height + thumbWidth) / 2f, thumbPaint);
        }
    }

    /**
     * 设置progress
     */
    public void setProgress(int progress) {
        this.progress = progress;
        if (!Horitation) {
            if (isReverse) {
                location = (int) (endPadding + progressBarLength * (1 - (float) progress / (float) maxProgress));
            } else {
                location = (int) (endPadding + progressBarLength * ((float) progress / (float) maxProgress));
            }
        } else {
            if (isReverse) {
                location = (int) (endPadding + progressBarLength * (1 - (float) progress / (float) maxProgress));
            } else {
                location = (int) (endPadding + progressBarLength * ((float) progress / (float) maxProgress));
            }
        }
        invalidate();
    }

    /**
     * 设置progress
     */
    public int getProgress() {
        return progress;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) event.getX();
                y = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveY = (int) (event.getY() - y);
                moveX = (int) (event.getX() - x);
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!Horitation) {
                    if (state == 0) {
                        location += moveY;
                    } else if (state == 1) {
                        location = height - endPadding - thumbStrockWidth;
                    } else if (state == -1) {
                        location = endPadding + thumbStrockWidth;
                    }
                } else {
                    if (state == 0) {
                        location += moveX;
                    } else if (state == 1) {
                        location = width - endPadding - thumbStrockWidth;
                    } else if (state == -1) {
                        location = endPadding + thumbStrockWidth;
                    }
                }
                moveX = 0;
                moveY = 0;
                break;
        }
        return true;
    }

    private SeekBarTrackListener trackListener;

    /**
     * 进度监听
     */
    public void setSeekBarTrackListener(SeekBarTrackListener listener) {
        this.trackListener = listener;
    }

    public interface SeekBarTrackListener {
        void tracking(int progress);
    }
}