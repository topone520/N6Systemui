package com.adayo.systemui.windows.views.hvac;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.android.systemui.R;

import java.util.Arrays;
import java.util.List;


public class EHorizontalSelectedView extends View {
    private final static String TAG = EHorizontalSelectedView.class.getSimpleName();
    private final String[] TR_ARRAY = {"Hi", "32.0", "31.5", "31.0", "30.5",
            "30.0", "29.5", "29.0", "28.5", "28.0",
            "27.5", "27.0", "26.5", "26.0", "25.5",
            "25.0", "24.5", "24.0", "23.5", "23.0",
            "22.5", "22.0", "21.5", "21.0", "20.5",
            "20.0", "19.5", "19.0", "18.5", "18.0", "Lo"};
    private Context mContext;
    private Paint mOthers;
    private Paint mSelect;
    private List<String> data = Arrays.asList(TR_ARRAY);
    /**
     * 可见数
     */
    private int seeSize = 3;
    private Rect mRect = new Rect();
    /**
     * 选中位置
     */
    private int selectNum = 0;
    private float downX;
    private int mItemSize;
    private float mOffset;
    private int selectColor = Color.WHITE;
    private int otherColor = Color.WHITE;
    private float otherTextSize = 68;
    private float selectTextSize = 80;

    public EHorizontalSelectedView(Context context) {
        this(context, null);
        super.setWillNotDraw(false);
    }

    public EHorizontalSelectedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        super.setWillNotDraw(false);
        this.mContext = context;

        initAttrs(attrs);
        initPaint();
    }

    public EHorizontalSelectedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setWillNotDraw(false);
        this.mContext = context;

        initAttrs(attrs);
        initPaint();
    }


    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.EHorizontalSelectedView);
        otherTextSize = typedArray.getDimension(R.styleable.EHorizontalSelectedView_otherTextSize, px2dip(mContext, otherTextSize));
        selectTextSize = typedArray.getDimension(R.styleable.EHorizontalSelectedView_selectTextSize, px2dip(mContext, selectTextSize));
        seeSize = typedArray.getInteger(R.styleable.EHorizontalSelectedView_seeSize, 3);
        otherColor = typedArray.getColor(R.styleable.EHorizontalSelectedView_otherColor, Color.WHITE);
        selectColor = typedArray.getColor(R.styleable.EHorizontalSelectedView_selectColor, Color.WHITE);
        typedArray.recycle();
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private void initPaint() {
        AAOP_LogUtils.d(TAG, "initPaint create");
        mOthers = new Paint();
        mOthers.setAntiAlias(true);
        mOthers.setTextSize(otherTextSize);
        mOthers.setColor(otherColor);
        mOthers.setTextAlign(Paint.Align.CENTER);

        mSelect = new Paint();
        mSelect.setAntiAlias(true);
        mSelect.setColor(selectColor);
        mSelect.setTextSize(selectTextSize);
        mSelect.setTextAlign(Paint.Align.CENTER);
        Log.d("TAG", "initPaint: " + mSelect.getStrokeWidth());
    }

    LinearGradient gradientLeft, gradientRight;


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d("TAG", "onDraw: ---------------------------------------------------------------------------------     " + mOffset);

        // int startColor = Color.parseColor("#8B9299");
        int startColor = Color.WHITE;
        int endColor = Color.parseColor("#4F5965");

        if (gradientLeft == null) {
            gradientLeft = new LinearGradient(0, 0, getWidth() / seeSize, getHeight(), endColor, startColor, Shader.TileMode.CLAMP);
        }
        if (gradientRight == null) {
            gradientRight = new LinearGradient(getWidth() / (seeSize - 1) + 90, 0, getWidth(), getHeight(), startColor, endColor, Shader.TileMode.CLAMP);
        }

        // 获取宽度
        int width = getWidth();
        int height = getHeight();
        // 获取每个条目的大小
        if (seeSize == 0) {
            return;
        }
        mItemSize = width / seeSize;
        int tmp = 0;
        for (String datum : data) {
            mOthers.getTextBounds(datum, 0, datum.length(), mRect);
            int textWidth = mRect.width();
            if (textWidth > tmp) {
                tmp = textWidth;
            }
        }
        // 修正文字过大导致长度bug
        mItemSize = Math.max(mItemSize, tmp);
        //seeSize = width / mItemSize;
        // | dfadf |  dsafa | afasdf |
        // 得到选中的条目
        // 画出第一个
        for (int j = 0; j < data.size(); j++) {
            String datum = data.get(j);
            mOthers.getTextBounds(datum, 0, datum.length(), mRect);
            int textWidth = mRect.width();
            int textHeight = mRect.height();

            //   Log.d("TAG", "onDraw: textwidth" + textWidth + "   textheight" + textHeight + "   moffset" + mOffset + "    mitemsize" + mItemSize + "    height" + height + "    width" + width+"   mitemsize"+mItemSize);
            if (j != selectNum) {
                // 画其他的
                if (j < selectNum) {
                    mOthers.setShader(gradientLeft);
                    int a = selectNum - j;
                    canvas.drawText(datum, mItemSize * seeSize / 2 - a * mItemSize + mOffset + 20, 65, mOthers);
                } else {
                    //右边
                    mOthers.setShader(gradientRight);
                    int a = j - selectNum;
                    canvas.drawText(datum, mItemSize * seeSize / 2 + a * mItemSize + mOffset - 20, 65, mOthers);
                }
            } else {
                canvas.drawText(datum, mItemSize * seeSize / 2 + mOffset, 65, mSelect);
            }
        }
    }


    public void setOnTouch(MotionEvent event) {
        onTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mOnRollingListener != null)
            mOnRollingListener.hideResetTimer();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mOnRollingListener != null)
                    mOnRollingListener.hideResetTimer();
                float scrollX = event.getX();
                invalidate();
                Log.d("-------------------", "onTouchEvent: downX" + downX + "    " + scrollX);
                mOffset = scrollX - downX;
                // 向右滑动
                if (scrollX > downX) {

                    Log.d("TAG", "onTouchEvent: --------------------MOVE向右滑动");
                    // 如果滑动距离大于一个条目的大小，则减1
                    if (scrollX - downX >= mItemSize) {
                        Log.d("TAG", "updateView: ---------------itemsize" + mItemSize);
                        if (selectNum > 0) {
                            mOffset = 0;
                            selectNum = selectNum - 1;
                            downX = scrollX;
                            if (mOnRollingListener != null) {
                                mOnRollingListener.onRolling(selectNum, data.get(selectNum), false);
                            }
                        }
                    }
                } else {
                    Log.d("TAG", "onTouchEvent: --------------------MOVE向坐====滑动" + downX);
                    //向左滑动大于一个条目的大小,则加1
                    if (downX - scrollX >= mItemSize) {
                        if (selectNum < data.size() - 1) {
                            mOffset = 0;
                            selectNum = selectNum + 1;
                            downX = scrollX;
                            if (mOnRollingListener != null) {
                                mOnRollingListener.onRolling(selectNum, data.get(selectNum), false);
                            }
                        }
                    }
                }
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                mOffset = 0;
                invalidate();
                Log.d(TAG, "onTouchEvent: =================>");
                //TODO 更改为手指抬起下发
                if (mOnRollingListener != null) {
                    mOnRollingListener.onRolling(selectNum, data.get(selectNum), true);
                }
                break;
            default:
                break;
        }
        return true;
    }

    public int getSelectNum() {
        return selectNum;
    }

    public void setSelectNum(int selectNum) {
        if (selectNum > data.size()) {
            selectNum = data.size() - 1;
        }
        this.selectNum = selectNum;
        invalidate();
    }

    public void setSelectTextColor(int color) {
        this.selectColor = color;
        invalidate();
    }

    public int getSelectColor() {
        return selectColor;
    }

    public void setOtherTextColor(int color) {
        this.otherColor = color;
        invalidate();
    }

    public String getSelectText() {
        return data.get(selectNum);
    }

    public void setData(List<String> data) {
        this.data = data;
        invalidate();
    }

    public void setSeeSize(int seeSize) {
        this.seeSize = seeSize;
        invalidate();
    }

    private OnRollingListener mOnRollingListener;

    public void setOnRollingListener(OnRollingListener onRollingListener) {
        mOnRollingListener = onRollingListener;
    }

    public interface OnRollingListener {
        /**
         * 滚动监听
         *
         * @param position 角标
         * @param s        滚动的文字
         */
        void onRolling(int position, String s, boolean isReq);

        //新增
        //手势滑动MOVE 重新计算5S
        void hideResetTimer();
    }

    public void setOtherTextSize(float textSize) {
        AAOP_LogUtils.d(TAG, "reset .....");
        this.otherTextSize = px2dip(mContext, textSize);
        mOthers.setTextSize(otherTextSize);
    }

    public void setSelectTextSize(float textSize) {
        this.selectTextSize = px2dip(mContext, textSize);
        mSelect.setTextSize(selectTextSize);
    }
}
