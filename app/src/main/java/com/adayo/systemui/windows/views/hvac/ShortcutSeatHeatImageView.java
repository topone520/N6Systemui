package com.adayo.systemui.windows.views.hvac;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.R;

import java.util.List;

public class ShortcutSeatHeatImageView extends ImageView implements TouchEventListener {
    private static final String TAG = ShortcutSeatHeatImageView.class.getName();
    private static final int HEAT_LEVEL_MIN = 0;
    private static final int HEAT_LEVEL_MAX = 3;
    private static final int HEAT_LEVEL_STS_MAX = 4;
    private int _heat_level = HEAT_LEVEL_MAX;
    private int[] _resource;
    private HeatLevelListener _listener;


    public ShortcutSeatHeatImageView(@NonNull Context context) {
        super(context);
    }

    public ShortcutSeatHeatImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _initialize_view();
    }

    private void _initialize_view() {
        AAOP_LogUtils.i(TAG, "_initialize_view() +++");
        setOnTouchListener(new SharedTouchListener(this));
    }

    public void setListener(HeatLevelListener listener) {
        _listener = listener;
    }

    public void _update_ui(int position) {
        Log.d(TAG, "_update_ui: " + position);
        if (position < 0 || position > HEAT_LEVEL_STS_MAX) return;
        if (position == HEAT_LEVEL_STS_MAX) position = HEAT_LEVEL_MIN;
        _heat_level = position;
        initUpdateView();
    }

    public void injectResource(int[] resource) {
        _resource = resource;
        setImageResource(resource[0]);
    }

    private void initUpdateView() {
        LogUtil.d(TAG + "::initUpdateView " + _heat_level + "  size = " + _resource.length);
        try {
            setImageResource(_heat_level == 0 ? _resource[0] : _resource[_heat_level]);
        } catch (IndexOutOfBoundsException e) {
            LogUtil.d(TAG + "IndexOutOfBoundsException");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTouchEvent(View view) {
        _heat_level--;
        if (_heat_level < HEAT_LEVEL_MIN) {
            _heat_level = _resource.length - 1;
        }
        initUpdateView();
        if (_listener == null) return;
        Log.d(TAG, "_initialize_view: " + _heat_level);
        _listener.onHeatLevelChanged(_heat_level);
    }

    public interface HeatLevelListener {
        void onHeatLevelChanged(int level);
    }
}
