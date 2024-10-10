package com.adayo.systemui.windows.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.content.ContextCompat;

import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

/**
 * @author Y4170
 */
public class VolumeSeekBar extends AppCompatSeekBar {
    /**
     * 定义一个展现时间的PopupWindow
     */
    private PopupWindow mPopupWindow;

    private View mView;
    /**
     * 显示时间的TextView
     */
    private TextView dialogSeekTime;
    /**
     * 用来表示该组件在整个屏幕内的绝对坐标，其中 mPosition[0] 代表X坐标,mPosition[1] 代表Y坐标。
     */
    private int[] mPosition;
    /**
     * SeekBar上的Thumb的宽度，即那个托动的小黄点的宽度
     */
    private final int mThumbWidth = 24;
    private Context mContext;
    private boolean isTouching = false;
    private boolean isNeedShowPopup = false;

    public VolumeSeekBar(@NonNull Context context) {
        this(context,null);
    }

    public VolumeSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VolumeSeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        mContext = context;
        setSplitTrack(false);
//        setThumb(ContextCompat.getDrawable(mContext, R.drawable.adayo_seekbar_thumb));
        setPadding(0, 0, 0, 0);
        setProgressDrawable(ContextCompat.getDrawable(mContext , R.drawable.adayo_seekbar_background));
        mView = LayoutInflater.from(SystemUIApplication.getSystemUIContext()).inflate(R.layout.seek_popu,null);
        dialogSeekTime = (TextView) mView.findViewById(R.id.dialogSeekTime);
        mPopupWindow = new PopupWindow(mView,64,44,false);
        mPopupWindow.setAnimationStyle(0);
        mPopupWindow.setClippingEnabled(false);
        mPosition = new int[2];
    }

    /**
     * 如果不需要
     *
     * @param need 是否需要时间窗口
     * @return 控件
     */
    private boolean needShowPopup = true;
    public VolumeSeekBar setNeedTimePark(boolean need){
        needShowPopup = need;
        return this;
    }

    /**
     * 当系统弹窗等层级较高的画面使用时，如果无法显示需要设置层级与宿主窗口层级一致或更高
     *
     * @param type 窗口层级类型
     * @return 控件
     */
    private int windowType = 2065;
    public VolumeSeekBar setPopupWindowType(int type){
        if(null != mPopupWindow){
            windowType = type;
            mPopupWindow.setWindowLayoutType(type);
        }
        return this;
    }

    /**
     * 获取控件的宽度
     *
     * @param v
     * @return 控件的宽度
     */
    private int getViewWidth(View v) {
        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        v.measure(w,h);
        return v.getMeasuredWidth();
    }

    /**
     * 获取控件的高度
     *
     * @param v
     * @return 控件的高度
     */
    private int getViewHeight(View v) {
        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        v.measure(w,h);
        return v.getMeasuredHeight();
    }

    /**
     * 隐藏进度拖动条的PopupWindow
     */
    private void hideSeekDialog() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    /**
     * 显示进度拖动条的PopupWindow
     *
     * @param str
     *     时间值
     */
    private void showSeekDialog(String str) {
        if(!needShowPopup){
            return;
        }
        dialogSeekTime.setText(str);
        int progress = this.getProgress();
        // 计算每个进度值所占的宽度
        int thumb_x = (int) (progress * (1.0f * (this.getWidth() - 32) / this.getMax()));
//        // 更新后的PopupWindow的Y坐标
//        int middle = mPosition[1] - 52;
//        if(windowType == 2008 || windowType == 2038){
//            middle = middle - 80;
//        }
        if (mPopupWindow != null) {
            try {
                /*
                 * 获取在整个屏幕内的绝对坐标，注意这个值是要从屏幕顶端算起，也就是包括了通知栏的高度。
                 * 其中 mPosition[0] 代表X坐标,mPosition[1]代表Y坐标。
                 */
                this.getLocationOnScreen(mPosition);
                // 更新后的PopupWindow的Y坐标
                int middle = mPosition[1];
//                int middle = mPosition[1] - 52;
                if(windowType == 2008 || windowType == 2038){
                    middle = middle - 80;
                }
                int x = thumb_x + mPosition[0] - getViewWidth(mView) + mThumbWidth/2;
                LogUtil.d("mPopupWindow == " + mPopupWindow.toString()
                        + " ; seekBar = " + this.toString() + " ; mPosition[0] = " + mPosition[0]
                        + " ; mPosition[1] = " + mPosition[1]  + " ; middle = " + middle + " ; x = " + x);
                // 相对某个控件的位置（正左下方），在X、Y方向各有偏移
//                mPopupWindow.showAsDropDown(this,(int) mPosition[0],mPosition[1]);
                /*
                 * 更新后的PopupWindow的X坐标
                 * 首先要把当前坐标值减去PopWindow的宽度的一半，再加上Thumb的宽度一半。
                 * 这样才能使PopWindow的中心点和Thumb的中心点的X坐标相等
                 */
//                int x = thumb_x + mPosition[0] - getViewWidth(mView) + mThumbWidth/2;
                if(!mPopupWindow.isShowing()) {
                    LogUtil.d("showAtLocation");
                    mPopupWindow.showAtLocation(this.getRootView(), Gravity.NO_GRAVITY, x, middle);
                }else{
                    LogUtil.d("update");
                    mPopupWindow.update(x,middle,getViewWidth(mView),getViewHeight(mView));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            LogUtil.d("mPopupWindow == null");
            mPopupWindow = new PopupWindow(mView,mView.getWidth(),mView.getHeight(),false);
            mPopupWindow.setAnimationStyle(0);
            mPopupWindow.setClippingEnabled(false);
            mPosition = new int[2];
            // showSeekDialog(str);
        }
    }

    private long lastTouchUpTime = 0;
    @Override
    public synchronized void setProgress(int progress) {
        if(isTouching || Math.abs(System.currentTimeMillis() - 500) < lastTouchUpTime){
            return;
        }
        super.setProgress(progress);
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        super.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                LogUtil.d("progress = " + progress + " ; fromUser = " + fromUser + " ; isTouching = " + isTouching + " ; isNeedShowPopup = " + isNeedShowPopup);
                if(fromUser && isTouching){
                    // showSeekDialog(progress + "");
                    if(isNeedShowPopup) {
                        isNeedShowPopup = false;
                    }
                }
                if (listener != null) {
                    listener.onProgressChanged(seekBar, progress, fromUser);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                LogUtil.d("onStartTrackingTouch");
                isTouching = true;
                isNeedShowPopup = true;

                if (listener != null) {
                    listener.onStartTrackingTouch(seekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                LogUtil.d("onStopTrackingTouch");
                lastTouchUpTime = System.currentTimeMillis();
                isTouching = false;
                isNeedShowPopup = false;
                hideSeekDialog();
                if (listener != null) {
                    listener.onStopTrackingTouch(seekBar);
                }
            }
        });
    }
}

