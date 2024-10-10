package com.adayo.systemui.windows.views.seat;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.android.systemui.R;

public class VtpSeatVentilateImageView extends ImageView {
    private static final String TAG = VtpSeatVentilateImageView.class.getName();
    public static final int VENTILATE_LEVEL_MIN = 0;
    private static final int VENTILATE_LEVEL_MAX = 3;
    private static final int VENTILATE_LEVEL_STS_MAX = 4;
    private int _ventilate_level = VENTILATE_LEVEL_MIN;
    private int[] _level_resource = {R.mipmap.vtp_ac_icon_wind_n, R.mipmap.vtp_ac_icon_wind_s1, R.mipmap.vtp_ac_icon_wind_s2, R.mipmap.vtp_ac_icon_wind_s3};;

    private VentilateLevelListener _listener;

    public void setListener(VentilateLevelListener listener) {
        _listener = listener;
    }

    public VtpSeatVentilateImageView(@NonNull Context context) {
        super(context);
    }

    public VtpSeatVentilateImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _initialize_view();
    }

    private void _initialize_view() {
        setOnClickListener(v -> {
            AAOP_LogUtils.d(TAG, "-------------->");
            _ventilate_level--;
            if (_ventilate_level < VENTILATE_LEVEL_MIN) {
                _ventilate_level = _level_resource.length - 1;
            }
            initUpdateView();
            if (_listener != null) {
                _listener.onVentilateLevelChanged(_ventilate_level);
            }
        });
        _update_ui(VENTILATE_LEVEL_MIN);
    }

    public void _update_ui(int position) {
        Log.d(TAG, "setStsUpdateView: " + position);
        if (position < VENTILATE_LEVEL_MIN || position > VENTILATE_LEVEL_STS_MAX) return;
        if (position == VENTILATE_LEVEL_STS_MAX) position = VENTILATE_LEVEL_MIN;
        _ventilate_level = position;
        initUpdateView();
    }

    public void injectResource(int[] level_resources) {
        _level_resource = level_resources;
        initUpdateView();
    }


    private void initUpdateView() {
        AAOP_LogUtils.d(TAG, "-------------->" + _ventilate_level);
        setImageResource(_ventilate_level <= 0 ? _level_resource[0] : _level_resource[_ventilate_level]);
    }

    public interface VentilateLevelListener {
        void onVentilateLevelChanged(int grep);
    }
}
