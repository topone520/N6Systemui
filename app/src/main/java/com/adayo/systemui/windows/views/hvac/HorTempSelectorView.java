package com.adayo.systemui.windows.views.hvac;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.utils.TempIndexUtil;
import com.adayo.systemui.utils.WindowsUtils;
import com.android.systemui.R;

public class HorTempSelectorView extends FrameLayout implements TouchEventListener {
    static String TAG = HorTempSelectorView.class.getName();
    private final String[] TR_ARRAY = {"Hi", "32.0", "31.5", "31.0", "30.5",
            "30.0", "29.5", "29.0", "28.5", "28.0",
            "27.5", "27.0", "26.5", "26.0", "25.5",
            "25.0", "24.5", "24.0", "23.5", "23.0",
            "22.5", "22.0", "21.5", "21.0", "20.5",
            "20.0", "19.5", "19.0", "18.5", "18.0", "Lo"};
    private final float TEMP_MAX = 32.5f;
    private final float TEMP_MIN = 17.5f;
    private Context _context;
    private EHorizontalSelectedView _hor_temp_view;
    private ImageView _img_reduce;
    private ImageView _img_add;
    private TextView _tv_temp;

    private OnSelectPositionListener _listener;
    private int _select_position;

    private boolean _is_long;
    private View inflate;
    private SharedTouchListener sharedTouchListener;

    public void set_listener(OnSelectPositionListener temp) {
        this._listener = temp;
    }

    public HorTempSelectorView(@NonNull Context context) {
        super(context);
    }

    public HorTempSelectorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this._context = context;
        initView();
    }

    public HorTempSelectorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this._context = context;
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        sharedTouchListener = new SharedTouchListener(this);
        inflate = LayoutInflater.from(_context).inflate(R.layout.hor_temp_select_layout, null);
        addView(inflate);
        //温度滑块
        _hor_temp_view = findViewById(R.id.hor_temp_view);
        //温度减
        _img_reduce = findViewById(R.id.img_reduce);
        //温度加
        _img_add = findViewById(R.id.img_add);
        _tv_temp = findViewById(R.id.tv_temp);
        _img_add.setOnTouchListener(sharedTouchListener);
        _img_reduce.setOnTouchListener(sharedTouchListener);
        _tv_temp.setText(getShowTemp());

        initOnClick();
        viewLinearTouch();
    }

    private void viewLinearTouch() {
        inflate.setOnLongClickListener(view -> {
            Log.d(TAG, "viewLinearTouch: +++++++++");
            longPressRunnable();
            return false;
        });

        inflate.setOnTouchListener((v, event) -> {
            if (!_is_long) {
                try {
                    WindowsUtils.showHvacPanel(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                _hor_temp_view.setOnTouch(event);
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    Log.d(TAG, "viewLinearTouch: up-------------->");
                    _view_event_UP();
                    break;
            }
            return false;
        });
    }

    public void longPressRunnable() {
        _is_long = true;
        _hor_temp_view.setVisibility(VISIBLE);
        _img_reduce.setVisibility(GONE);
        _img_add.setVisibility(GONE);
        _tv_temp.setVisibility(GONE);
    }

    public void _view_event_UP() {
        _is_long = false;
        _hor_temp_view.setVisibility(GONE);
        _img_reduce.setVisibility(VISIBLE);
        _img_add.setVisibility(VISIBLE);
        _tv_temp.setVisibility(VISIBLE);
    }

    private void initOnClick() {
        _hor_temp_view.setOnRollingListener(new EHorizontalSelectedView.OnRollingListener() {
            @Override
            public void onRolling(int position, String s, boolean isReq) {
                AAOP_LogUtils.d(" position = " + position + "   temp = " + s);
                _tv_temp.setText(s);
                _select_position = position;
                _listener.onSelectTemp(getTemp());
            }

            @Override
            public void hideResetTimer() {

            }
        });
    }

    public void _update_ui(float temp) {
        int position = query_position(temp);
        if (position == -1) return;
        _select_position = position;
        _tv_temp.setText(getShowTemp());
        _hor_temp_view.setSelectNum(position);
        AAOP_LogUtils.d(TAG, "position = " + position + " temp = " + getShowTemp());
    }

    private int query_position(float position) {
        return TempIndexUtil.indexOf(position);
    }

    @Override
    public void onTouchEvent(View view) {
        _listener.resetViewTimer();
        AAOP_LogUtils.d(TAG, " setTouchEventListener");
        if (view.getId() == R.id.img_add) {
            AAOP_LogUtils.d(TAG, "add ");
            _select_position--;
            if (_select_position < 0) {
                _select_position = 0;
                return;
            }
        } else if (view.getId() == R.id.img_reduce) {
            AAOP_LogUtils.d(TAG, "reduce ");
            _select_position++;
            if (_select_position > TR_ARRAY.length - 1) {
                _select_position = TR_ARRAY.length - 1;
                return;
            }
        }
        _listener.onSelectTemp(getTemp());
        _hor_temp_view.setSelectNum(_select_position);
        Log.d(TAG, "onTouchEvent: " + getTemp() + "");
        _tv_temp.setText(getShowTemp());
    }

    private float getTemp() {
        Log.d(TAG, "getTemp: " + _select_position);
        float temp;
        if (_select_position == TR_ARRAY.length - 1) temp = TEMP_MIN;
        else if (_select_position == 0) temp = TEMP_MAX;
        else temp = Float.parseFloat(TR_ARRAY[_select_position]);
        return temp;
    }

    private String getShowTemp() {
        String temp;
        if (_select_position == TR_ARRAY.length - 1) temp = "Lo";
        else if (_select_position == 0) temp = "Hi";
        else temp = TR_ARRAY[_select_position];
        return temp;
    }

    public interface OnSelectPositionListener {
        void onSelectTemp(float temp);

        void resetViewTimer();
    }
}
