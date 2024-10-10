package com.adayo.systemui.windows.views.hvac;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.adayo.systemui.contents.HvacSingleton;
import com.adayo.systemui.functional.hvac._view.dialog.HvacWindscreenDialog;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.utils.WindowsUtils;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class HvacWinSpeedImageView extends FrameLayout {

    static String TAG = HvacWinSpeedImageView.class.getName();

    private GestureDetector gestureDetector;
    private HvacWindscreenDialog windscreenDialog;
    private final int SPEED_LEVEL_MAX = 7;

    private static final int THRESHOLD = 50;
    private int totalScroll = 0;
    private SpeedLevelListener listener;
    private Handler handler;
    private boolean longPressHandled = false;
    private boolean isFirstScroll = true;
    private int _speed_level = 0;
    private TextView _tv_speed;

    public void setListener(SpeedLevelListener listener) {
        this.listener = listener;
    }

    public HvacWinSpeedImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        View viewInflate = LayoutInflater.from(context).inflate(R.layout.hvac_wind_speed_layout, this);
        _tv_speed = findViewById(R.id.tv_speed);
        gestureDetector = new GestureDetector(context, new MyGestureListener());
        handler = new Handler(Looper.getMainLooper());
        windscreenDialog = HvacWindscreenDialog.getInstance(SystemUIApplication.getSystemUIContext());
        viewInflate.setOnLongClickListener(view -> {
            longPressHandled = true;
            showPopView();
            return false;
        });

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //确保imageview已经初始化完成，不然导致第一次触摸该view获取不到事件
        setOnTouchListener((v, event) -> {
            if (longPressHandled) {
                LogUtil.d("Long ......");
                gestureDetector.onTouchEvent(event);
            } else {
                WindowsUtils.showHvacPanel(event);
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // 在抬起或取消时，移除之前的长按处理
                    handler.removeCallbacksAndMessages(null);
                    longPressHandled = false;
                    totalScroll = 0;
                    isFirstScroll = true;
                    LogUtil.d("ACTION_CANCEL  =  " + totalScroll);
                    hideSpeedDialog();
                    break;
            }
            return false;
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (isFirstScroll) {
                distanceX = 0;
                isFirstScroll = false;
            }
            totalScroll += distanceX;
            Log.d(TAG, "onScroll: " + distanceX + "  totalScroll: " + totalScroll);
            if (Math.abs(totalScroll) >= THRESHOLD) {
                // 达到阈值，执行你的操作
                if (totalScroll > 0) {
                    if (_speed_level <= 0) {
                        _speed_level = 0;
                    } else {
                        _speed_level--;
                    }
                } else {
                    if (_speed_level >= SPEED_LEVEL_MAX) {
                        _speed_level = SPEED_LEVEL_MAX;
                    } else {
                        _speed_level++;
                    }
                }
                // 重置总滑动值
                totalScroll = 0;
                //调温下发
                _update_ui(_speed_level);
                listener.onSpeedLevel(_speed_level);
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            totalScroll = 0;
            LogUtil.d("onSingleTapUp  ----");
            return super.onSingleTapUp(e);
        }
    }

    public void _update_ui(int level) {
        if (level < 0 || level > SPEED_LEVEL_MAX) level = 0;
        _speed_level = level;
        windscreenDialog.updateWindscreen(_speed_level);
        _tv_speed.setText(_speed_level + "");
    }

    public interface SpeedLevelListener {
        void onSpeedLevel(int level);
    }

    private void showPopView() {
        if (windscreenDialog == null)
            windscreenDialog = HvacWindscreenDialog.getInstance(SystemUIApplication.getSystemUIContext());
        LogUtil.debugD("showPopView----");
        windscreenDialog.show();
    }

    public void hideSpeedDialog() {
        windscreenDialog.dismiss();
    }
}

