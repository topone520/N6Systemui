package com.adayo.systemui.windows.views.hvac;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.android.systemui.R;

public class HvacWinSpeedAdjustView extends FrameLayout {
    private static final String TAG = HvacWinSpeedAdjustView.class.getSimpleName();
    private ImageView _iv_breeze;
    private ImageView _iv_gale;
    private HvacSpeedSeekBar _win_speed_adjust;
    private final int SPEED_MIN = 0;
    private final int SPEED_MAX = 7;
    private int _current_value = SPEED_MIN;
    private WinSpeedLevelListener levelListener;

    private final int TIME = 2000;
    private final Handler handler = new Handler();
    private boolean isStop;

    public void setLevelListener(WinSpeedLevelListener levelListener) {
        this.levelListener = levelListener;
    }

    public HvacWinSpeedAdjustView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _initialize_view();
    }

    private void _initialize_view() {
        addView(AAOP_HSkin.getLayoutInflater(getContext()).inflate(R.layout.hvac_win_speed_layout, null));
        _iv_breeze = findViewById(R.id.iv_breeze);
        _iv_gale = findViewById(R.id.iv_gale);
        _win_speed_adjust = findViewById(R.id.win_speed_adjust);
        _init_click();
    }

    private void _init_click() {
        _iv_breeze.setOnClickListener(v -> {
            _current_value--;
            if (_current_value < SPEED_MIN) {
                _current_value = SPEED_MIN;
                return;
            }
            isStop = true;
            if (levelListener != null) levelListener.onCurrentSpeed(_current_value);
            _view_refresh_ui(_current_value);
            isViewStop();
        });
        _iv_gale.setOnClickListener(v -> {
            _current_value++;
            if (_current_value > SPEED_MAX) {
                _current_value = SPEED_MAX;
                return;
            }
            isStop = true;
            if (levelListener != null) levelListener.onCurrentSpeed(_current_value);
            _view_refresh_ui(_current_value);
            isViewStop();
        });
        _win_speed_adjust.setChangedListener(progress -> {
            _current_value = progress;
            if (levelListener != null) levelListener.onCurrentSpeed(_current_value);
            isViewStop();
        });
    }

    private void isViewStop() {
        isStop = true;
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> {
            if (null != levelListener) {
                isStop = false;
                levelListener.onClickStopGet();
                AAOP_LogUtils.d(TAG, "handler  end 2000  isSStop = " + isStop);
            }
        }, TIME);
    }

    public void _update_ui(int level) {
        AAOP_LogUtils.d(TAG, "_update_ui = " + isStop);
        if (isStop) return;
        _current_value = level;
        AAOP_LogUtils.d(TAG, "正常刷新---");
        _win_speed_adjust.setProgressSeekbar(level);
    }

    public void _view_refresh_ui(int level) {
        _win_speed_adjust.setProgressSeekbar(level);
    }

    public interface WinSpeedLevelListener {
        void onCurrentSpeed(int level);

        void onClickStopGet();
    }
}
