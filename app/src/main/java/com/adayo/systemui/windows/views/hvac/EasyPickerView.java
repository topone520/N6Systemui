package com.adayo.systemui.windows.views.hvac;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import com.android.systemui.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 滚轮视图，可设置是否循环模式，实现OnScrollChangedListener接口以监听滚轮变化
 * Created by huzn on 2016/10/27.
 */
public class EasyPickerView extends View {
    private String TAG = EasyPickerView.class.getSimpleName() + "_EasyPickerView";

    private boolean isScrollEnd = false;
    private boolean isScrollEndNothing = false;
    private final String HI = "Hi";
    private final String LO = "Lo";

    // 文字大小
    private int textSize;
    // 颜色，默认Color.BLACK
    private int textColor;
    // 文字之间的间隔，默认10dp
    private int textPadding;
    // 文字最大放大比例，默认2.0f
    private float textMaxScale;
    // 文字最小alpha值，范围0.0f~1.0f，默认0.4f
    private float textMinAlpha;
    // 是否循环模式，默认是
    private boolean isRecycleMode;
    // 正常状态下最多显示几个文字，默认3（偶数时，边缘的文字会截断）
    private int maxShowNum;
    //文字是否居中
    private int mItemAlign;
    private TextPaint textPaint;
    private Paint.FontMetrics fm;
    private TextPaint mDataPaint;

    private Scroller scroller;
    private VelocityTracker velocityTracker;
    private int minimumVelocity;
    private int maximumVelocity;
    private int scaledTouchSlop;

    // 数据
    private List<String> dataList = new ArrayList<>();
    // 中间x坐标
    private int cx;
    // 中间y坐标
    private int cy;
    // 文字最大宽度
    private float maxTextWidth;
    // 文字高度
    private int textHeight;
    // 实际内容宽度
    private int contentWidth;
    // 实际内容高度
    private int contentHeight;

    // 按下时的y坐标
    private float downY;
    // 本次滑动的y坐标偏移值
    private float offsetY;
    // 在fling之前的offsetY
    private float oldOffsetY;
    // 当前选中项
    private int curIndex;
    private int offsetIndex;
    // 本次调用moveTo的目标index
    private int moveToIndex;

    // 回弹距离
    private float bounceDistance;
    // 是否正处于滑动状态
    private boolean isSliding = false;
    private boolean isEnable = true; // 滑条是否可操作

    public void setIsEnabled(boolean isEnabled) {
        isEnable = isEnabled;
    }

    private boolean isShowSymbol;
    private String symbolText;
    private int symbolSize = 36; // 最小size
    private int symbolXOffset = 41; // 向右+ 最小值 最大时是60, 从文字左下角临近处开始绘制 45
    private int symbolYOffset = 9; // 向上+ 最小值 最大时是24, 从文字左下角临近处开始绘制 18
    // 符号最大放大比例
    private float symbolTextMaxScale = 1.33f;
    private float symbolYMaxScale = 2.4f;
    private float symbolXMaxScale = 1.5f;
    // 符号内容宽度
    private int symbolContentWidth = 20;
    private int symbolContentHeight = 20;
    private final int TIME = 2000;
    private final Handler handler = new Handler();

    public EasyPickerView(Context context) {
        this(context, null);
    }

    public EasyPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EasyPickerView, defStyleAttr, 0);
        textSize = a.getDimensionPixelSize(R.styleable.EasyPickerView_epvTextSize, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 40, getResources().getDisplayMetrics()));
        textColor = a.getColor(R.styleable.EasyPickerView_epvTextColor, Color.WHITE);
        textPadding = a.getDimensionPixelSize(R.styleable.EasyPickerView_epvTextPadding, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 32, getResources().getDisplayMetrics()));
        textMaxScale = a.getFloat(R.styleable.EasyPickerView_epvTextMaxScale, 2.0f);
        textMinAlpha = a.getFloat(R.styleable.EasyPickerView_epvTextMinAlpha, 0.4f);
        isRecycleMode = a.getBoolean(R.styleable.EasyPickerView_epvRecycleMode, false);
        maxShowNum = a.getInteger(R.styleable.EasyPickerView_epvMaxShowNum, 3);
        mItemAlign = a.getInt(R.styleable.EasyPickerView_epvAlign, 0);
        isShowSymbol = a.getBoolean(R.styleable.EasyPickerView_epvShowSymbol, false);
        symbolText = a.getString(R.styleable.EasyPickerView_epvSymbolText);

        a.recycle();

        symbolContentWidth = isShowSymbol ? symbolContentWidth : 0;
        symbolContentHeight = isShowSymbol ? symbolContentHeight : 0;

        mDataPaint = new TextPaint();
//        mDataPaint.setFlags(1);
        mDataPaint.setColor(textColor);
        mDataPaint.setTextSize(symbolSize);
        mDataPaint.setAntiAlias(true);

        textPaint = new TextPaint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        updateItemTextAlign();

//        Typeface typeFace = Typeface.createFromAsset(SystemUIApplication.getSystemUIContext().getAssets(), "fonts/roboto_light.ttf");
//        textPaint.setTypeface(typeFace);
//        mDataPaint.setTypeface(typeFace);

        fm = textPaint.getFontMetrics();
        textHeight = (int) (fm.bottom - fm.top);

        scroller = new Scroller(context);
        minimumVelocity = ViewConfiguration.get(getContext()).getScaledMinimumFlingVelocity();
        maximumVelocity = ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity();
        scaledTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    private void updateItemTextAlign() {
        switch (mItemAlign) {
            case 1:
                textPaint.setTextAlign(Paint.Align.LEFT);
                break;
            case 2:
                textPaint.setTextAlign(Paint.Align.RIGHT);
                break;
            default:
                textPaint.setTextAlign(Paint.Align.CENTER);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        contentWidth = (int) (maxTextWidth * textMaxScale + getPaddingLeft() + getPaddingRight()) + symbolContentWidth;
        if (mode != MeasureSpec.EXACTLY) { // wrap_content
            width = contentWidth + symbolContentWidth;
        }

        mode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        contentHeight = textHeight * maxShowNum + textPadding * maxShowNum + symbolContentHeight;
        if (mode != MeasureSpec.EXACTLY) { // wrap_content
            height = contentHeight + getPaddingTop() + getPaddingBottom() + symbolContentWidth;
        }

        cx = width / 2;
        cy = height / 2;

        setMeasuredDimension(width, height);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        addVelocityTracker(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handler.removeCallbacksAndMessages(null);
                Log.d(TAG, "finishScroll----------- action ACTION_DOWN, scroller.isFinished()= " + scroller.isFinished());
                if (!isEnable) {
                    return true;
                }
                if (!scroller.isFinished()) {
                    scroller.forceFinished(true);
                    isScrollEnd = true;
                    Log.d(TAG, "finishScroll----------- action down, 不call回调: " + isScrollEndNothing + ", 结束： " + isScrollEnd);
                    finishScroll();
                }
                downY = event.getY();
                //TODO 滑动开始
//                if (null != onScrollChangedListener) {
//                    onScrollChangedListener.onScrollStart(curIndex, !isScrollEndNothing);
//                }
                break;

            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "finishScroll----------- action ACTION_MOVE");
                if (!isEnable) {
                    return true;
                }
                isScrollEndNothing = false;
                offsetY = event.getY() - downY;
                if (isSliding || Math.abs(offsetY) > scaledTouchSlop) {
                    isSliding = true;
                    Log.d(TAG, "finishScroll----------- action move in");
                    reDraw();
                }
                break;

            case MotionEvent.ACTION_UP:
                Log.d(TAG, "finishScroll----------- action ACTION_UP");
                if (!isEnable) {
                    return true;
                }
                int scrollYVelocity = 2 * getScrollYVelocity() / 3;
                if (Math.abs(scrollYVelocity) > minimumVelocity) {
                    oldOffsetY = offsetY;
                    scroller.fling(0, 0, 0, scrollYVelocity, 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                    invalidate();
                } else {
                    isScrollEnd = true;
                    Log.d(TAG, "finishScroll----------- action up, 不call回调: " + isScrollEndNothing + ", 结束： " + isScrollEnd);
                    finishScroll();
                }

                // 没有滑动，则判断点击事件
                if (!isSliding) {
                    if (downY < contentHeight / 3) {
                        moveBy(-1);
                    } else if (downY > 2 * contentHeight / 3) {
                        moveBy(1);
                    }
                }

                isSliding = false;
                recycleVelocityTracker();
                break;
            default:

                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (null != dataList && dataList.size() > 0) {
            canvas.clipRect(
                    cx - contentWidth / 2,
                    cy - contentHeight / 2,
                    cx + contentWidth / 2,
                    cy + contentHeight / 2
            );
//            Log.d(TAG, "drawSymbol: top " + contentHeight / 2 + ", bottom: " + contentHeight / 2);


            // 绘制文字，从当前中间项往前、后一共绘制maxShowNum个字
            int size = dataList.size();
            int centerPadding = textHeight + textPadding;
            int half = maxShowNum / 2 + 1;
            for (int i = -half; i <= half; i++) {
                int index = curIndex - offsetIndex + i;

                if (isRecycleMode) {
                    if (index < 0) {
                        index = (index + 1) % dataList.size() + dataList.size() - 1;
                    } else if (index > dataList.size() - 1) {
                        index = index % dataList.size();
                    }
                }

                if (index >= 0 && index < size) {
                    // 计算每个字的中间y坐标
                    int tempY = cy + i * centerPadding;
                    tempY += offsetY % centerPadding;

                    // 根据每个字中间y坐标到cy的距离，计算出scale值
                    float scale = 1.0f - (1.0f * Math.abs(tempY - cy) / centerPadding);

                    // 根据textMaxScale，计算出tempScale值，即实际text应该放大的倍数，范围 1~textMaxScale
                    float tempScale = scale * (textMaxScale - 1.0f) + 1.0f;
                    tempScale = tempScale < 1.0f ? 1.0f : tempScale;

                    float tempSymbolTextScale = scale * (symbolTextMaxScale - 1.0f) + 1.0f;
                    tempSymbolTextScale = tempSymbolTextScale < 1.0f ? 1.0f : tempSymbolTextScale;

                    float tempSymbolYScale = scale * (symbolYMaxScale - 1.0f) + 1.0f;
                    tempSymbolYScale = tempSymbolYScale < 1.0f ? 1.0f : tempSymbolYScale;

                    float tempSymbolXScale = scale * (symbolXMaxScale - 1.0f) + 1.0f;
                    tempSymbolXScale = tempSymbolXScale < 1.0f ? 1.0f : tempSymbolXScale;

                    // 计算文字alpha值
                    float textAlpha = textMinAlpha;
                    if (textMaxScale != 1) {
                        float tempAlpha = (tempScale - 1) / (textMaxScale - 1);
                        textAlpha = (1 - textMinAlpha) * tempAlpha + textMinAlpha;
                    }

                    textPaint.setTextSize(textSize * tempScale);
                    textPaint.setAlpha((int) (255 * textAlpha));

                    Log.d(TAG, "finishScroll-00---i: " + i + ", tempScale: " + tempScale + ", temperature: " + dataList.get(index));
                    //TODO  阴影
                    if (tempScale > 1.5) {
                        // textPaint.setShadowLayer(12, 8, 15, ContextCompat.getColor(getContext(), R.color.black));
                    } else {
                        //  textPaint.setShadowLayer(0, 8, 15, ContextCompat.getColor(getContext(), R.color.black));
                    }

                    // 绘制
                    Paint.FontMetrics tempFm = textPaint.getFontMetrics();
                    String text = dataList.get(index);
                    float textWidth = textPaint.measureText(text);
                    if (mItemAlign == 1) {
                        canvas.drawText(text, 0, tempY - (tempFm.ascent + tempFm.descent) / 2, textPaint);
                    } else if (mItemAlign == 2) {
//                        canvas.drawText(text, cx - textWidth, tempY - (tempFm.ascent + tempFm.descent) / 2, textPaint);
                        canvas.drawText(text, cx + 71, tempY - (tempFm.ascent + tempFm.descent) / 2, textPaint);
                    } else {
//                        canvas.drawText(text, cx - textWidth / 2, tempY - (tempFm.ascent + tempFm.descent) / 2, textPaint);
                        canvas.drawText(text, cx, tempY - (tempFm.ascent + tempFm.descent) / 2, textPaint);
                    }
                    if (isShowSymbol && index != 0 && index != size - 1) {
                        mDataPaint.setTextSize(symbolSize * tempSymbolTextScale);
                        mDataPaint.setAlpha((int) (255 * textAlpha));
                        canvas.drawText(symbolText, cx + (symbolXOffset * tempSymbolXScale), tempY - (symbolYOffset * tempSymbolYScale)/*- (tempFm.ascent + tempFm.descent) / 2*/, mDataPaint);
                    }

                }
            }
        }
    }

    private void setTextShadowLayer(TextPaint mTPaint, float radius, float dx, float dy, int color) {
        mTPaint.setShadowLayer(radius, dx, dy, color);
        invalidate();
    }

    public void setTextColor(int colorId) {
        Log.d(TAG, "epvTextColor----------- setTextColor: " + colorId);
        mDataPaint.setColor(colorId);
        textPaint.setColor(colorId);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            offsetY = oldOffsetY + scroller.getCurrY();

            if (!scroller.isFinished()) {
//                Log.d(TAG, "finishScroll----------- scroll not end");
                reDraw();
            } else {
                isScrollEnd = true;
                Log.d(TAG, "finishScroll----------- scroll end 1, 不call回调: " + isScrollEndNothing + ", 结束： " + isScrollEnd);
                reDraw();
                finishScroll();
                Log.d(TAG, "finishScroll----------- scroll end 2, 不call回调: " + isScrollEndNothing + ", 结束： " + isScrollEnd);

                isScrollEndNothing = false;
//                Log.d(TAG, "finishScroll----------- scroll end 3, 不call回调: " + isScrollEndNothing + ", 结束： " + isScrollEnd);

            }
        }
    }

    private void addVelocityTracker(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }

        velocityTracker.addMovement(event);
    }

    private void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    private int getScrollYVelocity() {
        velocityTracker.computeCurrentVelocity(1000, maximumVelocity);
        int velocity = (int) velocityTracker.getYVelocity();
        return velocity;
    }

    private void reDraw() {
        // curIndex需要偏移的量
        int i = (int) (offsetY / (textHeight + textPadding));
        float iTemp = (float) (offsetY / (textHeight + textPadding));
        if (isRecycleMode || (curIndex - i >= 0 && curIndex - i < dataList.size())) {
            Log.d(TAG, "finishScroll----------- reDraw**, i: " + i + ", iTemp: " + iTemp + ", currIndex: " + curIndex + ", offsetIndex: " + offsetIndex + ", offsetY: " + offsetY + ", textHeight: " + textHeight + ", textPadding: " + textPadding);
            if (offsetIndex != i) {
                offsetIndex = i;
                //TODO 滑动中
//                if (null != onScrollChangedListener) {
//                    onScrollChangedListener.onScrollChanged(getNowIndex(-offsetIndex), !isScrollEndNothing);
//                }
            }
            postInvalidate();
        } else {
            Log.d(TAG, "finishScroll----------- reDraw**, curIndex: " + curIndex + ", 不call回调: " + isScrollEndNothing + ", 结束： " + isScrollEnd + ", dataListSize: " + dataList.size() + ", offsetIndex: " + offsetIndex);
//            isScrollEnd = true;
            if (curIndex == 0 || curIndex == dataList.size() - 1) {
                scroller.forceFinished(true);
                isScrollEnd = true;
            }
            finishScroll();
        }
    }

    private void finishScroll() {
        Log.d(TAG, "finishScroll----------- finishScroll curIndex: " + curIndex + ", offsetIndex: " + offsetIndex + ", textHeight=" + textHeight + ", textPadding=" + textPadding + ", offsetY=" + offsetY);
        // 判断结束滑动后应该停留在哪个位置
        int centerPadding = textHeight + textPadding;
        int getTempOffsetIndex = getNowIndex(-offsetIndex);
        int indexDiff = Math.abs(moveToIndex - Math.abs(getTempOffsetIndex));
        Log.d(TAG, "finishScroll----------- finishScroll moveToIndex: " + moveToIndex + ", getTempOffsetIndex=" + getTempOffsetIndex + ", indexDiff =" + indexDiff);
        if (Math.abs(getTempOffsetIndex) < moveToIndex && 1 == indexDiff) { // && curIndex != 1
            // 如果最终index不是moveTo的index(控件若为循环mode时是否ok待调试）
            // 正常不会遇到此情况：滚动“冲突”导致offsetY未达到指定位置（差一点点），从而 int i = (int) (offsetY / (textHeight + textPadding));得出的offsetIndex的值达不到整数
            Log.d(TAG, "finishScroll----------- finishScroll 00");
            if (offsetIndex >= 0) {
                ++offsetIndex;
            } else {
                --offsetIndex;
            }
        } else {
            // 按拖动位置决定最后回弹到哪个index
            float v = offsetY % centerPadding;
            if (v > 0.5f * centerPadding) {
                Log.d(TAG, "finishScroll----------- finishScroll 01");
                ++offsetIndex;
            } else if (v < -0.5f * centerPadding) {
                Log.d(TAG, "finishScroll----------- finishScroll 02");
                --offsetIndex;
            }
        }

        // 重置curIndex
        curIndex = getNowIndex(-offsetIndex);
        Log.d(TAG, "finishScroll----------- finishScroll curIndex: " + curIndex + ", offsetIndex: " + offsetIndex);

        // 计算回弹的距离
        bounceDistance = offsetIndex * centerPadding - offsetY;
        offsetY += bounceDistance;

        // 更新
        if (isScrollEnd) {
            isScrollEnd = false;
            //TODO 滑动停止
            /*if (null != onScrollChangedListener) {
                Log.d(TAG, "finishScroll----------- notify");
                onScrollChangedListener.onScrollFinished(curIndex, !isScrollEndNothing);
            }*/
            if (null != onScrollChangedListener) {
                onScrollChangedListener.onChangeValue(getSelectTemp());
            }
            handler.postDelayed(() -> {
                if (null != onScrollChangedListener) {
                    onScrollChangedListener.onStopGet();
                }
            }, TIME);
        }


        // 重绘
        reset();
        postInvalidate();
    }

    private int getNowIndex(int offsetIndex) {
        int index = curIndex + offsetIndex;
        Log.d(TAG, "getNowIndex curIndex=" + curIndex + ", offsetIndex=" + offsetIndex);
        if (isRecycleMode) {
            if (index < 0) {
                index = (index + 1) % dataList.size() + dataList.size() - 1;
            } else if (index > dataList.size() - 1) {
                index = index % dataList.size();
            }
        } else {
            if (index < 0) {
                index = 0;
            } else if (index > dataList.size() - 1) {
                index = dataList.size() - 1;
            }
        }
        return index;
    }

    private void reset() {
        Log.d(TAG, "reset scroll data");
        offsetY = 0;
        oldOffsetY = 0;
        offsetIndex = 0;
        bounceDistance = 0;
        moveToIndex = 0;
    }

    /**
     * 设置要显示的数据
     *
     * @param dataList 要显示的数据
     */
    public void setDataList(List<String> dataList) {
        Log.d(TAG, "setDataList, size=" + dataList.size());
        this.dataList.clear();
        this.dataList.addAll(dataList);

        // 更新maxTextWidth
        if (null != dataList && dataList.size() > 0) {
            int size = dataList.size();
            for (int i = 0; i < size; i++) {
                float tempWidth = textPaint.measureText(dataList.get(i));
                if (tempWidth > maxTextWidth) {
                    maxTextWidth = tempWidth;
                }
            }
            curIndex = 0;
        }
        requestLayout();
        invalidate();
    }

    /**
     * 选中下标转换float
     */
    private float getSelectTemp() {
        float selectTemp;
        if (dataList.get(curIndex).equals(HI)) {
            selectTemp = 32.5f;
        } else if (dataList.get(curIndex).equals(LO)) {
            selectTemp = 17.5f;
        } else {
            selectTemp = Float.parseFloat(dataList.get(curIndex));
        }
        return selectTemp;
    }

    /**
     * 将传递过来的float转换成string，找下标更新
     *
     * @param selectTempStr
     */
    public void setSelectTemp(float selectTempStr) {
        Log.d(TAG, "setSelectTemp: " + selectTempStr);
        String selectNumTemp;
        float MAX = 32.5f;
        float MIN = 17.5f;
        if (BigDecimal.valueOf(selectTempStr).compareTo(BigDecimal.valueOf(MAX)) == 0) {
            selectNumTemp = HI;
        } else if (BigDecimal.valueOf(selectTempStr).compareTo(BigDecimal.valueOf(MIN)) == 0) {
            selectNumTemp = LO;
        } else {
            selectNumTemp = selectTempStr + "";
        }
        if (dataList.contains(selectNumTemp)) {
            if (isScrollEnd) {
                this.curIndex = dataList.indexOf(selectNumTemp);
                requestLayout();
                invalidate();
                Log.d(TAG, "setSelectTemp: ------" + selectTempStr);
            }

            Log.d(TAG, "setSelectTemp: refresh end ...");
        }
    }

    public void ccpUP() {
        if (curIndex >= dataList.size() - 1) return;
        curIndex++;
        requestLayout();
        invalidate();
        if (onScrollChangedListener != null)
            onScrollChangedListener.onChangeValue(getSelectTemp());
    }

    public void ccpDOWN() {
        if (curIndex <= 0) return;
        curIndex--;
        requestLayout();
        invalidate();
        if (onScrollChangedListener != null)
            onScrollChangedListener.onChangeValue(getSelectTemp());
    }

    /**
     * 滚动到指定位置
     *
     * @param index      需要滚动到的指定位置
     * @param isFromUser 是否为用户触发 (用来区分是否要在回调中执行某些逻辑)
     */
    public void moveTo(int index, boolean isFromUser) {
        Log.d(TAG, "finishScroll----------- mvoe to start1, index: " + index + ", dataList.size: " + dataList.size() + ", curIndex: " + curIndex + ", isFromUser: " + isFromUser);

        if (!scroller.isFinished()) {
            Log.d(TAG, "finishScroll----------- forceFinished");
            scroller.forceFinished(true);
        }

        Log.d(TAG, "finishScroll----------- mvoe to start2, index: " + index + ", curIndex: " + curIndex);

        isScrollEndNothing = !isFromUser;
        Log.d(TAG, "finishScroll----------- mvoe to, 不call回调: " + isScrollEndNothing + ", 结束： " + isScrollEnd);

        finishScroll();

        moveToIndex = index;
        int dy = 0;
        int centerPadding = textHeight + textPadding;
        if (!isRecycleMode) {
            dy = (curIndex - index) * centerPadding;
        } else {
            int offsetIndex = curIndex - index;
            Log.d(TAG, "set offsetIndex curIndex=" + curIndex + ", index=" + index + ", offsetIndex=" + offsetIndex);
            int d1 = Math.abs(offsetIndex) * centerPadding;
            int d2 = (dataList.size() - Math.abs(offsetIndex)) * centerPadding;

            if (offsetIndex > 0) {
                if (d1 < d2) {
                    dy = d1; // ascent
                } else {
                    dy = -d2; // descent
                }
            } else {
                if (d1 < d2) {
                    dy = -d1; // descent
                } else {
                    dy = d2; // ascent
                }
            }
        }
        Log.d(TAG, "finishScroll----------- mvoe to, dy: " + dy + ", curIndex： " + curIndex + ", index: " + index + ", padding: " + centerPadding);

        scroller.startScroll(0, 0, 0, dy, 500);
        invalidate();
    }

    /**
     * 扩展方法，根据传入的value来曲下标，滚动到指定位置
     */
    public void moveTo(float value) {
        if (isScrollEnd) return;
        String valueStr = value + "";
        if (value == 32.5f) valueStr = HI;
        if (value == 17.5f) valueStr = LO;
        if (!dataList.contains(valueStr)) return;
        int index = dataList.indexOf(valueStr);
        if (!scroller.isFinished()) {
            Log.d(TAG, "finishScroll----------- forceFinished");
            scroller.forceFinished(true);
        }
        isScrollEndNothing = false;
        Log.d(TAG, "finishScroll----------- mvoe to, 不call回调: " + isScrollEndNothing + ", 结束： " + isScrollEnd);

        finishScroll();

        moveToIndex = index;
        int dy = 0;
        int centerPadding = textHeight + textPadding;
        if (!isRecycleMode) {
            dy = (curIndex - index) * centerPadding;
        } else {
            int offsetIndex = curIndex - index;
            Log.d(TAG, "set offsetIndex curIndex=" + curIndex + ", index=" + index + ", offsetIndex=" + offsetIndex);
            int d1 = Math.abs(offsetIndex) * centerPadding;
            int d2 = (dataList.size() - Math.abs(offsetIndex)) * centerPadding;

            if (offsetIndex > 0) {
                if (d1 < d2) {
                    dy = d1; // ascent
                } else {
                    dy = -d2; // descent
                }
            } else {
                if (d1 < d2) {
                    dy = -d1; // descent
                } else {
                    dy = d2; // ascent
                }
            }
        }
        Log.d(TAG, "finishScroll----------- mvoe to, dy: " + dy + ", curIndex： " + curIndex + ", index: " + index + ", padding: " + centerPadding);

        scroller.startScroll(0, 0, 0, dy, 500);
        invalidate();
    }

    /**
     * 滚动指定的偏移量
     *
     * @param offsetIndex 指定的偏移量
     */
    public void moveBy(int offsetIndex) {
        moveTo(getNowIndex(offsetIndex), true);
    }

    /**
     * 滚动发生变化时的回调接口
     */
    public interface OnScrollChangedListener {

        // public void onScrollStart(int curIndex, boolean isFromUser);

        // public void onScrollChanged(int curIndex, boolean isFromUser);

        //    public void onScrollFinished(int curIndex, boolean isFromUser);

        void onChangeValue(float value);

        void onStopGet();
    }

    private OnScrollChangedListener onScrollChangedListener;

    public void setOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.onScrollChangedListener = onScrollChangedListener;
    }
}
