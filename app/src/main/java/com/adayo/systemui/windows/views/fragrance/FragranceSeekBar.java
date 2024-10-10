package com.adayo.systemui.windows.views.fragrance;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.contents.FragranceSOAConstant;

public class FragranceSeekBar extends View {
    public static String TAG = FragranceSeekBar.class.getName();
    private Context _context;
    private Bitmap backgroundBitmap;//绘制背景
    private Bitmap progressBitmap;//绘制填充
    private Paint paint;//绘制bitmap
    private Paint textPaint;//title
    private Paint textConcentrationPaint;

    private int progress;
    private final float TEXT_SIZE = 40F;

    private int oldWidth = 520;
    private int oldHeight = 120;

    //默认可滑动
    private boolean thumbVisible = true;

    private boolean isClick = true;//判断是否是点击
    private final int SEEKBAR_SLIDE_THRESHOLD = 5;
    private boolean isOverstep;//判断是否越界
    private FragranceProgressListener listener;
    private String title = "";
    private String newTitle = "";//记录长按之前的香氛浓度
    private String concentration = "";//香氛浓度
    private String newConcentration = "";//记录长按之前的香氛浓度
    private float textWidth = 0f;//记录当前手指需要长按的一个范围  width=textPaint.width  height  = 40f;
    private boolean isTextLongClick = false;//用来记录当前是否在该text上边
    private boolean isLongPressDetected;//记录当前是否长按
    private int TEXT_SLIDE_THRESHOLD = 20;//用来判断当前手指在文字上是否是长按操作
    private static final long LONG_PRESS_TIME = 800; // 长按时间阈值
    private Handler longPressHandler;

    public void setListener(FragranceProgressListener listener) {
        this.listener = listener;
    }

    public FragranceSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        init();
    }

    //更新标题
    public void setSeekbarTitle(String title) {
        AAOP_LogUtils.d(TAG, "title --->" + title);
        this.title = title == null ? "" : title;
        invalidate();
    }

    public String getSeekbarTitle() {
        return newTitle;
    }

    //更新香氛浓度
    public void setConcentration(String str) {
        this.concentration = str;
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        longPressHandler = new Handler(Looper.getMainLooper());//直接获取主线程 looper 对象创建handler
        paint = new Paint();
        paint.setAntiAlias(true);
        textConcentrationPaint = new Paint();
        textConcentrationPaint.setAntiAlias(true);
        textConcentrationPaint.setColor(Color.WHITE);
        textConcentrationPaint.setTextSize(TEXT_SIZE); // 使用默认文本大小

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(TEXT_SIZE); // 使用默认文本大小
        textPaint.setAntiAlias(true);
    }

    public void hideThumb() {
        thumbVisible = false;
        invalidate();
    }

    public void showThumb() {
        thumbVisible = true;
        invalidate();
    }

    public boolean isShowThumb() {
        return thumbVisible;
    }

    public void setBackgroundBitmap(Bitmap backgroundBitmap) {
        this.backgroundBitmap = backgroundBitmap;
        invalidate();
    }

    public void setProgressBitmap(Bitmap progressBitmap) {
        this.progressBitmap = progressBitmap;
        invalidate();
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (progress <= FragranceSOAConstant.SLIDE_LOW) {
            if (old_level != FragranceSOAConstant.CONCENTRATION_LOW) {
                concentration = "低";
            }
            old_level = FragranceSOAConstant.CONCENTRATION_LOW;
        } else if (progress <= FragranceSOAConstant.SLIDE_MID) {
            if (old_level != FragranceSOAConstant.CONCENTRATION_MID) {
                concentration = "中";
            }
            old_level = FragranceSOAConstant.CONCENTRATION_MID;
        } else {
            if (old_level != FragranceSOAConstant.CONCENTRATION_HIGH) {
                concentration = "高";
            }
            old_level = FragranceSOAConstant.CONCENTRATION_HIGH;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制背景
        if (backgroundBitmap != null) {
            backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap, oldWidth, oldHeight, true);
            canvas.drawBitmap(backgroundBitmap, 0, 0, paint);
        }
        // 绘制进度
        if (thumbVisible && progressBitmap != null) {
            int drawWidth = (int) ((float) progress / 100 * getWidth());
            canvas.save();
            canvas.clipRect(0, 0, drawWidth, getHeight());
            progressBitmap = Bitmap.createScaledBitmap(progressBitmap, oldWidth, oldHeight, true);
            canvas.drawBitmap(progressBitmap, 0, 0, paint);
            canvas.restore();
        }
        //绘制textview
        canvas.drawText(title, 60, getHeight() / 2 + TEXT_SIZE / 2, textPaint);
        textWidth = textPaint.measureText(title);
        //绘制香氛浓度
        if (thumbVisible)
            canvas.drawText(concentration, 416, getHeight() / 2 + TEXT_SIZE / 2, textConcentrationPaint);
    }

    //点击事件，记录一个像素5的阈值来关闭或者打开
    private float downX;
    private float downY;
    private int old_level = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        Log.d(TAG, "onTouchEvent: ");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                //判断当前手指是否在文字上
                Log.d(TAG, "onTouchEvent: downX = " + downX + "  downY  = " + downY + "  textWidth = " + textWidth);
                if (Math.abs(downX) - 60 < textWidth) {
                    Log.d(TAG, "onTouchEvent: long");
                    isTextLongClick = true;
                    longPressHandler.postDelayed(longPressRunnable, LONG_PRESS_TIME);
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if (Math.abs(event.getX() - downX) > TEXT_SLIDE_THRESHOLD || Math.abs(event.getY() - downY) > TEXT_SLIDE_THRESHOLD) {
                    cancelLongPress();
                }
                if (!isLongPressDetected) {
                    isClick = false;
                    Log.d(TAG, "onTouchEvent: 越界downX= " + downX + "   eventGetX= " + event.getX() + "   number = " + Math.abs(event.getX() - downX));
                    if (Math.abs(event.getX() - downX) < SEEKBAR_SLIDE_THRESHOLD) {
                        isClick = true;
                    } else {
                        isOverstep = true;
                    }
                    int newProgress = (int) (event.getX() * 100 / getWidth());
                    if (thumbVisible && !isClick) {
                        setProgress(Math.max(0, Math.min(100, newProgress)));
                        if (newProgress <= FragranceSOAConstant.SLIDE_LOW) {
                            if (old_level != FragranceSOAConstant.CONCENTRATION_LOW) {
                                listener.level(FragranceSOAConstant.CONCENTRATION_LOW);
                                setConcentration("低");
                            }
                            old_level = FragranceSOAConstant.CONCENTRATION_LOW;
                        } else if (newProgress <= FragranceSOAConstant.SLIDE_MID) {
                            if (old_level != FragranceSOAConstant.CONCENTRATION_MID) {
                                listener.level(FragranceSOAConstant.CONCENTRATION_MID);
                                setConcentration("中");
                            }
                            old_level = FragranceSOAConstant.CONCENTRATION_MID;
                        } else {
                            if (old_level != FragranceSOAConstant.CONCENTRATION_HIGH) {
                                listener.level(FragranceSOAConstant.CONCENTRATION_HIGH);
                                setConcentration("高");
                            }
                            old_level = FragranceSOAConstant.CONCENTRATION_HIGH;
                        }
                    }
                }
                Log.d(TAG, "onTouchEvent: MOVE" + event.getX() + "  eventY " + event.getY());

                Log.d(TAG, "onTouchEvent: thumbVisible " + thumbVisible);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent: UP  isClick =" + isClick + " isOverstep  " + isOverstep + " isLongPressDetected = " + isLongPressDetected + "   isTextLongClick = " + isTextLongClick);
                if (isClick && listener != null && !isOverstep && !isLongPressDetected) {
                    Log.d(TAG, "onTouchEvent: up listener onClick");
                    listener.onClick();
                }
                isClick = true;
                isOverstep = false;
                cancelLongPress();
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (backgroundBitmap != null) {
            backgroundBitmap = scaleBitmapToSeekBarSize(backgroundBitmap);
        }
        if (progressBitmap != null) {
            progressBitmap = scaleBitmapToSeekBarSize(progressBitmap);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public interface FragranceProgressListener {
        void level(int level);

        void onClick();

        void onLongClick();
    }

    public void cancelLongPress() {
        longPressHandler.removeCallbacks(longPressRunnable);
        isLongPressDetected = false;
        Log.d(TAG, "cancelLongPress:------------ ");
        isTextLongClick = false;
    }

    private Runnable longPressRunnable = () -> {
        isLongPressDetected = true;
        if (isLongPressDetected && isTextLongClick && listener != null) {
            longHideText();
            listener.onLongClick();
        }
    };

    private Bitmap scaleBitmapToSeekBarSize(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        float scaleX = getWidth() * 1.0f / bitmap.getWidth();
        float scaleY = getHeight() * 1.0f / bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleX, scaleY);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public void longHideText() {
        newConcentration = concentration;
        concentration = "";
        newTitle = title;
        title = "";
        invalidate();
    }

    public void cancelLongShowText() {
        if (concentration.equals(""))
            concentration = newConcentration;
        if (title.equals(""))
            title = newTitle;
        invalidate();
    }

}



