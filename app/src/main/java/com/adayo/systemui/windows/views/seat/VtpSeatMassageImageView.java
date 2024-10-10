package com.adayo.systemui.windows.views.seat;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.setting.system.utils.LogUtil;
import com.android.systemui.R;


public class VtpSeatMassageImageView extends ImageView {
    private final static String TAG = VtpSeatMassageImageView.class.getSimpleName();
    private int[] _level_resource = {R.mipmap.vtp_ac_icon_massage_n, R.mipmap.vtp_ac_icon_massage_s1, R.mipmap.vtp_ac_icon_massage_s2, R.mipmap.vtp_ac_icon_massage_s3};

    public static final int LEVEL_MIN = 0;
    private static final int LEVEL_MAX = 3;
    private int _level = LEVEL_MAX;

    private HeatLevelViewLister viewListener;

    public VtpSeatMassageImageView(@NonNull Context context) {
        super(context);
    }

    public VtpSeatMassageImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _initialize_view();
    }

    private void _initialize_view() {
        setOnClickListener(v -> {
            _level--;
            if (_level < LEVEL_MIN) {
                _level = _level_resource.length - 1;
            }
            initUpdateView(_level);
            if (viewListener != null) {
                viewListener.onHeatLevelViewChanged(_level);
            }
        });
        initUpdateView(0);
    }

    public void _update_ui(int level) {
        LogUtil.d(TAG, "::_update_ui: " + level);
        initUpdateView(level);
    }

    private void initUpdateView(int level) {
        int MASSAGE_LEVEL_MAX = 3;
        if (level < 0 || level > MASSAGE_LEVEL_MAX) return;
        setBackgroundResource(_level_resource[level]);
    }

    public void setViewListener(HeatLevelViewLister listener) {
        viewListener = listener;
    }

    public interface HeatLevelViewLister {
        void onHeatLevelViewChanged(int level);
    }
}
