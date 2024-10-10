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

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.R;

public class VtpSeatSteeringImageView extends ImageView {
    private final String TAG = VtpSeatSteeringImageView.class.getSimpleName();
    private SteeringListener steeringListener;
    private boolean _is_open;

    public void setSteeringListener(SteeringListener steeringListener) {
        this.steeringListener = steeringListener;
    }

    public VtpSeatSteeringImageView(@NonNull Context context) {
        super(context);
    }

    public VtpSeatSteeringImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _initialize_view();
    }

    public VtpSeatSteeringImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _initialize_view();
    }

    private void _initialize_view() {
        initClick();
    }

    private void initClick() {
        setOnClickListener(v -> {
            _is_open = !_is_open;
            steeringListener.onSteeringGrep(_is_open ? SeatSOAConstant.SEAT_MASSAGE_OPEN : SeatSOAConstant.SEAT_MASSAGE_CLOSE);
            _update_ui(_is_open ? SeatSOAConstant.SEAT_MASSAGE_OPEN : SeatSOAConstant.SEAT_MASSAGE_CLOSE);
        });
    }

    public void _update_ui(int select_position) {
        _is_open = select_position == SeatSOAConstant.SEAT_MASSAGE_OPEN;
        setImageResource(_is_open ? R.mipmap.ivi_ac_seats_icon_wheel_hot_s3 : R.mipmap.ivi_ac_seats_icon_wheel_hot_n);
    }

    public interface SteeringListener {
        void onSteeringGrep(int grep);
    }
}
