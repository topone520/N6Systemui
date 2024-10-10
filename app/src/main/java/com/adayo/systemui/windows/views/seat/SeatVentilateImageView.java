package com.adayo.systemui.windows.views.seat;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.windows.dialogs.CommErrorDialog;
import com.android.systemui.R;

public class SeatVentilateImageView extends FrameLayout {
    private static final String TAG = SeatVentilateImageView.class.getName();
    private static final int VENTILATE_LEVEL_MIN = 0;
    private static final int VENTILATE_LEVEL_MAX = 3;
    private static final int VENTILATE_LEVEL_STS_MAX = 4;
    private ImageView _iv_ventilate;
    private TextView _tv_ventilate;
    private int _ventilate_level = VENTILATE_LEVEL_MIN;
    private int[] _level_resource;

    private VentilateLevelListener _listener;

    public void setListener(VentilateLevelListener listener) {
        _listener = listener;
    }

    public SeatVentilateImageView(@NonNull Context context) {
        super(context);
    }

    public SeatVentilateImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _initialize_view(context);
    }

    private void _initialize_view(Context context) {
        addView(AAOP_HSkin.getLayoutInflater(context).inflate(R.layout.seat_ventilate_comm_layout, null));
        _iv_ventilate = (ImageView) findViewById(R.id.iv_ventilate);
        _tv_ventilate = findViewById(R.id.tv_ventilate);
        findViewById(R.id.re_ventilate).setOnClickListener(v -> {
            if (power_status != HvacSOAConstant.HVAC_POWER_MODE_IGN_ON || warning_level == HvacSOAConstant.HVAC_POWER_MODE_IGN_ON) {
                CommErrorDialog.getInstance().show();
                return;
            }
            _ventilate_level--;
            if (_ventilate_level < VENTILATE_LEVEL_MIN) {
                _ventilate_level = _level_resource.length - 1;
            }
            initUpdateView();
            _listener.onVentilateLevelChanged(_ventilate_level);
        });
    }

    public void _update_ui(int position) {
        Log.d(TAG, "setStsUpdateView: " + position);
        if (position < VENTILATE_LEVEL_MIN || position > VENTILATE_LEVEL_STS_MAX) return;
        if (position == VENTILATE_LEVEL_STS_MAX) position = VENTILATE_LEVEL_MIN;
        _ventilate_level = position;
        initUpdateView();
    }

    public int get_level() {
        return _ventilate_level;
    }

    public void injectResource(int[] level_resources) {
        _level_resource = level_resources;
        initUpdateView();
    }


    private void initUpdateView() {
        AAOP_HSkin.with(_iv_ventilate).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, _ventilate_level <= 0 ? _level_resource[0] : _level_resource[_ventilate_level]).applySkin(false);
        AAOP_HSkin.with(_tv_ventilate).addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, _ventilate_level <= 0 ? R.color.seat_text_color2 : R.color.seat_text_color1).applySkin(false);
    }

    private int power_status = HvacSOAConstant.HVAC_POWER_MODE_IGN_ON;
    private int warning_level = HvacSOAConstant.HVAC_OFF_IO;

    public void setPowerStatus(int status) {
        power_status = status;
    }

    public void setWarningLevel(int level) {
        warning_level = level;
    }

    public interface VentilateLevelListener {
        void onVentilateLevelChanged(int grep);
    }
}
