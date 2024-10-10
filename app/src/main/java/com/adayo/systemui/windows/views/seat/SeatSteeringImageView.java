package com.adayo.systemui.windows.views.seat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.android.systemui.R;

public class SeatSteeringImageView extends FrameLayout {
    private final String TAG = SeatSteeringImageView.class.getSimpleName();
    private Context _context;
    private RelativeLayout _re_steering;
    private ImageView _iv_steering;
    private TextView _tv_steering;
    private SteeringListener steeringListener;
    private boolean _is_open;

    public void setSteeringListener(SteeringListener steeringListener) {
        this.steeringListener = steeringListener;
    }

    public SeatSteeringImageView(@NonNull Context context) {
        super(context);
    }

    public SeatSteeringImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        _initialize_view();
    }

    public SeatSteeringImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _context = context;
        _initialize_view();
    }

    private void _initialize_view() {
        addView(AAOP_HSkin.getLayoutInflater(_context).inflate(R.layout.seat_steering_comm_layout, null));
        _re_steering = (RelativeLayout) findViewById(R.id.re_steering);
        _iv_steering = (ImageView) findViewById(R.id.iv_steering);
        _tv_steering = findViewById(R.id.tv_steering);
        initClick();
    }

    private void initClick() {
        _re_steering.setOnClickListener(v -> {
            _is_open = !_is_open;
            steeringListener.onSteeringGrep(_is_open ? SeatSOAConstant.SEAT_MASSAGE_OPEN : SeatSOAConstant.SEAT_MASSAGE_CLOSE);
            _update_ui(_is_open ? SeatSOAConstant.SEAT_MASSAGE_OPEN : SeatSOAConstant.SEAT_MASSAGE_CLOSE);
        });
    }

    public void _update_ui(int select_position) {
        _is_open = select_position == SeatSOAConstant.SEAT_MASSAGE_OPEN;
        AAOP_HSkin.with(_iv_steering).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, _is_open ? R.mipmap.ivi_ac_seats_icon_wheel_hot_s3 : R.mipmap.ivi_ac_seats_icon_wheel_hot_n).applySkin(false);
        _tv_steering.setSelected(_is_open);
    }

    public interface SteeringListener {
        void onSteeringGrep(int grep);
    }
}
