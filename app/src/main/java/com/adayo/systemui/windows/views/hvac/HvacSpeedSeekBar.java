package com.adayo.systemui.windows.views.hvac;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioManager;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;

public class HvacSpeedSeekBar extends SeekBar {
    private final String TAG = HvacSpeedSeekBar.class.getName();
    private final int TICK_COUNT = 8;
    private final int SPEED_MIN = 0;
    private final int SPEED_MAX = 7;
    private boolean isTouch;//是否滑动停止
    private final Paint tickPaint = new Paint();
    private int progress = 0;
    private AudioManager mAudioManager;

    private onProgressChangedListener changedListener;

    public void setChangedListener(onProgressChangedListener changedListener) {
        this.changedListener = changedListener;
    }

    public HvacSpeedSeekBar(@NonNull Context context) {
        super(context);
        init();
    }

    public HvacSpeedSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HvacSpeedSeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        // 设置刻度线画笔
        tickPaint.setColor(getResources().getColor(android.R.color.darker_gray));
        tickPaint.setStrokeWidth(2);

        setOnTouchListener((view, motionEvent) -> {
            //子view处理事件，父view不拦截
            getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    isTouch = true;
                    mAudioManager.playSoundEffect(0);
                }
                progress = i;
                invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTouch = false;
                if (changedListener != null) changedListener.onProgressChanged(progress);
            }
        });
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 计算每个刻度线的间距（不包括首尾的30px间距）
        float tickSpacing = (float) (getWidth() - 76) / (TICK_COUNT - 1);

        // 计算顶部和底部的间距
        float topMargin = 10;
        float bottomMargin = 10;

        // 绘制刻度线
        for (int i = 0; i < TICK_COUNT; i++) {
            float x = 38 + i * tickSpacing;
            //不让刻度显示在滑块上
            if (progress == i) continue;
            //TODO 更改覆盖的刻度颜色和未覆盖的刻度颜色
            canvas.drawLine(x, topMargin, x, getHeight() - bottomMargin, tickPaint);
        }
    }

    public void setProgressSeekbar(int seekbarProgress) {
        AAOP_LogUtils.d(TAG, "isTouch  = " + isTouch + "  progress = " + seekbarProgress);
        if (seekbarProgress < SPEED_MIN || seekbarProgress > SPEED_MAX) return;
        if (isTouch)
            return;
        this.setProgress(seekbarProgress);
        invalidate();
        AAOP_LogUtils.d(TAG, " refresh end ...");
    }

    public void ccpLeft() {
        if (progress <= SPEED_MIN) return;
        if (isTouch) {
            return;
        }
        progress--;
        this.setProgress(progress);
        invalidate();
        if (changedListener != null) changedListener.onProgressChanged(progress);
    }

    public void ccpRight() {
        if (progress >= SPEED_MAX) return;
        if (isTouch) {
            return;
        }
        progress++;
        this.setProgress(progress);
        invalidate();
        if (changedListener != null) changedListener.onProgressChanged(progress);
    }

    public interface onProgressChangedListener {
        void onProgressChanged(int progress);
    }
}

