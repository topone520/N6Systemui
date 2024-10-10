package com.adayo.systemui.windows.views.seat;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.dialogs.CommErrorDialog;
import com.android.systemui.R;

public class SeatHeatImageView extends FrameLayout {
    private static final String TAG = SeatHeatImageView.class.getName();
    private static final int HEAT_LEVEL_MIN = 0;
    private static final int HEAT_LEVEL_MAX = 3;
    private static final int HEAT_LEVEL_STS_MAX = 4;
    private ImageView _iv_heat;
    private TextView _tv_heat;
    private int _heat_level = HEAT_LEVEL_MAX;
    private int[] _resource;
    private HeatLevelListener _listener;

    public SeatHeatImageView(@NonNull Context context) {
        super(context);
    }

    public SeatHeatImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _initialize_view(context);
    }

    private void _initialize_view(Context context) {
        AAOP_LogUtils.i(TAG, "_initialize_view() +++");
        addView(LayoutInflater.from(context).inflate(R.layout.seat_heat_comm_layout, null));
        ((RelativeLayout) findViewById(R.id.re_heat)).setOnClickListener(v -> {
            if (power_status != HvacSOAConstant.HVAC_POWER_MODE_IGN_ON|| warning_level == HvacSOAConstant.HVAC_POWER_MODE_IGN_ON) {
                CommErrorDialog.getInstance().show();
                return;
            }
            _heat_level--;
            if (_heat_level < HEAT_LEVEL_MIN) {
                _heat_level = _resource.length - 1;
            }
            initUpdateView();
            if (_listener == null) return;
            Log.d(TAG, "_initialize_view: " + _heat_level);
            _listener.onHeatLevelChanged(_heat_level);
        });
        _iv_heat = (ImageView) findViewById(R.id.iv_heat);
        _tv_heat = (TextView) findViewById(R.id.tv_heat);
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

    public int get_level() {
        return _heat_level;
    }

    public void injectResource(int[] resource) {
        _resource = resource;
        _iv_heat.setBackgroundResource(resource[0]);
        _tv_heat.setTextColor(getContext().getColor(R.color.white40));
        AAOP_HSkin.with(_iv_heat).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, _resource[0]).applySkin(false);
        AAOP_HSkin.with(_tv_heat).addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.seat_text_color2).applySkin(false);
    }

    private void initUpdateView() {
        LogUtil.d(TAG + "::initUpdateView " + _heat_level + "  size = " + _resource.length);
        try {
            AAOP_HSkin.with(_iv_heat).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, _heat_level == 0 ? _resource[0] : _resource[_heat_level]).applySkin(false);
            AAOP_HSkin.with(_tv_heat).addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, _heat_level != 0 ? R.color.seat_text_color1 : R.color.seat_text_color2).applySkin(false);
        } catch (IndexOutOfBoundsException e) {
            LogUtil.d(TAG + "IndexOutOfBoundsException");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int power_status = HvacSOAConstant.HVAC_POWER_MODE_IGN_ON;
    private int warning_level = HvacSOAConstant.HVAC_OFF_IO;
    public void setPowerStatus(int status) {
        power_status = status;
    }
    public void setWarningLevel(int level) {
        warning_level = level;
    }
    public interface HeatLevelListener {
        void onHeatLevelChanged(int level);
    }
}
