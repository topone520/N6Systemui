package com.adayo.systemui.windows.views.seat;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.setting.system.utils.LogUtil;
import com.android.systemui.R;

public class VtpSeatHeatImageView extends ImageView {
    private static final String TAG = VtpSeatHeatImageView.class.getName();
    public static final int HEAT_LEVEL_MIN = 0;
    private static final int HEAT_LEVEL_MAX = 3;
    private static final int HEAT_LEVEL_STS_MAX = 4;
    private int _heat_level = HEAT_LEVEL_MAX;
    private int[] _resource;
    private HeatLevelViewLister viewListener;


    public VtpSeatHeatImageView(@NonNull Context context) {
        super(context);
    }

    public VtpSeatHeatImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _initialize_view(context);
    }

    private void _initialize_view(Context context) {
        setOnClickListener(v -> {
            _heat_level--;
            if (_heat_level < HEAT_LEVEL_MIN) {
                _heat_level = _resource.length - 1;
            }
            initUpdateView();
            if (viewListener != null) {
                viewListener.onHeatLevelViewChanged(_heat_level);
            }
        });
        _update_ui(HEAT_LEVEL_MIN);
    }

    public void setViewListener(HeatLevelViewLister listener) {
        viewListener = listener;
    }

    public void _update_ui(int position) {
        Log.d(TAG, "_update_ui: " + position);
        if (position < 0 || position > HEAT_LEVEL_STS_MAX) return;
        if (position == HEAT_LEVEL_STS_MAX) position = HEAT_LEVEL_MAX;
        _heat_level = position;
        initUpdateView();
    }

    public void injectResource(int[] resource) {
        _resource = resource;
    }

    private void initUpdateView() {
//        LogUtil.d(TAG,"::initUpdateView " + _heat_level + "  size = " + _resource.length);
        try {
            setImageResource(_heat_level == 0 ? _resource[0] : _resource[_heat_level]);
        } catch (IndexOutOfBoundsException e) {
            LogUtil.d(TAG, "IndexOutOfBoundsException");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface HeatLevelViewLister {
        void onHeatLevelViewChanged(int level);
    }
}
