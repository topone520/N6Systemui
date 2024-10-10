package com.adayo.systemui.windows.views.hvac;

import android.view.MotionEvent;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.utils.WindowsUtils;

// 定义接口回调
interface TouchEventListener {
    void onTouchEvent(View view);
}

public class SharedTouchListener implements View.OnTouchListener {
    private final String TAG = SharedTouchListener.class.getSimpleName();
    private static final int THRESHOLD = 20;
    private final TouchEventListener _touch_event_listener;
    private float _down_start_x, _down_start_y;
    private boolean _is_click = true;

    // 构造函数，接受接口回调参数
    public SharedTouchListener(TouchEventListener listener) {
        this._touch_event_listener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        AAOP_LogUtils.d(TAG, "子view ----》 viewGroup ");
        // 处理触摸事件逻辑
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 处理按下事件
                _down_start_x = event.getX();
                _down_start_y = event.getY();
                AAOP_LogUtils.d(TAG, "downX = " + _down_start_x + "   downY = " + _down_start_y);
                break;
            case MotionEvent.ACTION_MOVE:
                // 处理移动事件
                float deltaX = Math.abs(event.getX() - _down_start_x);
                float deltaY = Math.abs(event.getY() - _down_start_y);
                AAOP_LogUtils.d(TAG, "--- MOVE deltaX = " + deltaX + "   deltaY = " + deltaY + "  getX = " + event.getX() + "  getY = " + event.getY());
                // 判断滑动距离，可以根据具体情况设置一个阈值
                if (deltaX > THRESHOLD || deltaY > THRESHOLD) {
                    _is_click = false;
                    // 视为滑动
                    // 处理滑动逻辑
                    WindowsUtils.showHvacPanel(event);
                } else {
                    _is_click = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                // 处理抬起事件
                AAOP_LogUtils.d(TAG, " ----UP is_click = " + _is_click);
                if (_touch_event_listener != null && _is_click) {
                    _touch_event_listener.onTouchEvent(v);
                } else {
                    WindowsUtils.showHvacPanel(event);
                }
                _is_click = true;
                break;
        }
        return true;
    }
}



