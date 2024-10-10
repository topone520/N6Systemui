package com.adayo.systemui.windows.views.hvac;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.attribute.ISkinAttrHandler;
import com.adayo.proxy.aaop_hskin.entity.SkinAttr;
import com.adayo.proxy.aaop_hskin.resource.IResourceManager;
import com.adayo.proxy.aaop_hskin.utils.SkinAttrUtils;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.android.systemui.R;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;


public class VerSelectedView extends View {
    private final String[] TR_ARRAY = {"Hi", "32.0", "31.5", "31.0", "30.5",
            "30.0", "29.5", "29.0", "28.5", "28.0",
            "27.5", "27.0", "26.5", "26.0", "25.5",
            "25.0", "24.5", "24.0", "23.5", "23.0",
            "22.5", "22.0", "21.5", "21.0", "20.5",
            "20.0", "19.5", "19.0", "18.5", "18.0", "Lo"};
    private final String HI = "Hi";
    private final String LO = "Lo";
    private final String TAG = VerSelectedView.class.getName();
    private Context mContext;
    //其他两个item
    private Paint mOthers;
    //选中的item
    private Paint mSelect;
    private List<String> data = Arrays.asList(TR_ARRAY);
    /**
     * 可见数
     */
    private int seeSize;
    private Rect mRect = new Rect();
    /**
     * 选中位置
     */
    private int selectNum = 0;
    private float downY;
    private int mItemSize;
    private float mOffset;
    private int selectColor = Color.WHITE;
    private int otherColor = Color.WHITE;
    private float otherTextSize;
    private float selectTextSize;

    private boolean isStop = true;//用来判断当前手指是否停止，停止在更新UI

    public VerSelectedView(Context context) {
        this(context, null);
        super.setWillNotDraw(false);
    }

    public VerSelectedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        super.setWillNotDraw(false);
        this.mContext = context;

        initAttrs(attrs);
        initPaint();
    }

    public VerSelectedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setWillNotDraw(false);
        this.mContext = context;

        initAttrs(attrs);
        initPaint();
    }


    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.EHorizontalSelectedView);
        otherTextSize = typedArray.getDimension(R.styleable.EHorizontalSelectedView_otherTextSize, 50);
        selectTextSize = typedArray.getDimension(R.styleable.EHorizontalSelectedView_selectTextSize, 100);
        seeSize = typedArray.getInteger(R.styleable.EHorizontalSelectedView_seeSize, 3);
        otherColor = typedArray.getColor(R.styleable.EHorizontalSelectedView_otherColor, Color.WHITE);
        selectColor = typedArray.getColor(R.styleable.EHorizontalSelectedView_selectColor, Color.WHITE);
        typedArray.recycle();
    }

    private void initPaint() {
        //未选中字体
        mOthers = new Paint();
        mOthers.setAntiAlias(true);
        mOthers.setTextSize(otherTextSize);
        mOthers.setColor(otherColor);
        mOthers.setTextAlign(Paint.Align.CENTER);
        //选中字体
        mSelect = new Paint();
        mSelect.setAntiAlias(true);
        mSelect.setColor(selectColor);
        mSelect.setTextSize(selectTextSize);
        mSelect.setTextAlign(Paint.Align.CENTER);
        Log.d("TAG", "initPaint: " + mSelect.getStrokeWidth());
    }

    public void ccpUP() {
        if (selectNum >= data.size() - 1) return;
        selectNum++;
        invalidate();
        if (mOnRollingListener != null)
            mOnRollingListener.onRolling(getSelectTemp());
    }

    public void ccpDOWN() {
        if (selectNum <= 0) return;
        selectNum--;
        invalidate();
        if (mOnRollingListener != null)
            mOnRollingListener.onRolling(getSelectTemp());
    }

    LinearGradient gradientLeft, gradientRight;


    int[] colors = {Color.parseColor("#FFFFFF"), Color.parseColor("#2A2E35")};
    float[] positions = {0.2f, 1.0f}; // 调整这里的位置值


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d("TAG", "onDraw: ---------------------------------------------------------------------------------     " + getWidth());

        // int startColor = Color.parseColor("#8B9299");
        int startColor = Color.WHITE;
        int endColor = Color.parseColor("#2A2E35");

        if (gradientLeft == null)
            gradientLeft = new LinearGradient(0, 0, 0, getHeight(), endColor, startColor, Shader.TileMode.CLAMP);
        // gradientLeft = new LinearGradient(0, 0, getWidth() / seeSize, getHeight(), endColor, startColor, Shader.TileMode.CLAMP);
        if (gradientRight == null)
            // gradientRight = new LinearGradient(getWidth() / (seeSize - 1) + 90, 0, getWidth(), getHeight(), startColor, endColor, Shader.TileMode.CLAMP);
            gradientRight = new LinearGradient(0, 0, 0, getHeight(), colors, positions, Shader.TileMode.CLAMP);

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
        //比例不等宽高，会让每页显示数量初始化大于或小于设定的
        //  seeSize = height / mItemSize;
        // | dfadf |  dsafa | afasdf |
        // 得到选中的条目
        // 画出第一个
        for (int j = 0; j < data.size(); j++) {
            String datum = data.get(j);
            mOthers.getTextBounds(datum, 0, datum.length(), mRect);
            int textWidth = mRect.width();
            int textHeight = mRect.height();
            if (j != selectNum) {
                // 上方
                if (j < selectNum) {
                    mOthers.setShader(gradientLeft);
                    int a = selectNum - j;
                    canvas.drawText(datum, getWidth() / 2, mItemSize * seeSize / 2 - a * mItemSize + mOffset, mOthers);
                } else {
                    //下方
                    mOthers.setShader(gradientRight);
                    int a = j - selectNum;
                    canvas.drawText(datum, getWidth() / 2, mItemSize * seeSize / 2 + a * mItemSize + mOffset - 10, mOthers);
                }
            } else {
                canvas.drawText(datum, getWidth() / 2, mItemSize * seeSize / 2 + mOffset + 10, mSelect);
            }
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: match click");
        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                isStop = false;
                break;
            case MotionEvent.ACTION_MOVE:

                float scrollY = event.getY();
                mOffset = scrollY - downY;
                //向上滑动
                if (scrollY > downY) {
                    if (scrollY - downY >= mItemSize) {
                        if (selectNum > 0) {
                            mOffset = 0;
                            selectNum = selectNum - 1;
                            downY = scrollY;
                        }
                    }
                } else {
                    if (downY - scrollY >= mItemSize) {
                        mOffset = 0;
                        if (selectNum >= TR_ARRAY.length - 1) {
                            selectNum = TR_ARRAY.length - 1;
                        } else {
                            selectNum = selectNum + 1;
                        }
                        downY = scrollY;
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.d("tag", "onTouchEvent: ===========================抬起手至");
                //抬起手指时，偏移量归零，相当于回弹。
                mOffset = 0;
                invalidate();
                isStop = true;
                if (mOnRollingListener != null)
                    mOnRollingListener.onRolling(getSelectTemp());
                break;
            default:
                break;
        }
        return true;
    }

    public int getSelectNum() {
        return selectNum;
    }

    //将传递过来的float转换成string，找下标更新
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
        if (data.contains(selectNumTemp)) {
            if (isStop) {
                this.selectNum = data.indexOf(selectNumTemp);
                invalidate();
            }

            Log.d(TAG, "setSelectTemp: refresh end ...");
        }
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
         * @param selectTemp 温度返回转换成需要的
         */
//        void onRolling(int position, float selectTemp, boolean isReq);
        void onRolling(float selectTemp);

    }

    private float getSelectTemp() {
        float selectTemp;
        if (data.get(selectNum).equals(HI)) {
            selectTemp = 32.5f;
        } else if (data.get(selectNum).equals(LO)) {
            selectTemp = 17.5f;
        } else {
            selectTemp = Float.parseFloat(data.get(selectNum));
        }
        return selectTemp;
    }


    public void setOtherTextSize(float otherTextSize) {
        this.otherTextSize = otherTextSize;
    }

    public void setSelectTextSize(float selectTextSize) {
        this.selectTextSize = selectTextSize;
    }


}
