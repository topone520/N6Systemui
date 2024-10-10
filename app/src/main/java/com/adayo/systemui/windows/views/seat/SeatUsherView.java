package com.adayo.systemui.windows.views.seat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.windows.views.SwitchButtonVe;
import com.android.systemui.R;

public class SeatUsherView extends FrameLayout implements View.OnClickListener {
    private static String TAG = SeatUsherView.class.getSimpleName();
    private SwitchButtonVe _seat_usher;
    private TextView _tv_usher;
    private SeatUsherListener _usher_listener;
    private final int USHER_OPEN = 2;
    private final int USHER_CLOSE = 1;

    public void set_usher_listener(SeatUsherListener _usher_listener) {
        this._usher_listener = _usher_listener;
    }

    public SeatUsherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _initialize_view();
    }

    private void _initialize_view() {
        View inflate = AAOP_HSkin.getLayoutInflater(getContext()).inflate(R.layout.seat_velcome_layout, null);
        addView(inflate);
        inflate.setOnClickListener(this);
        _seat_usher = findViewById(R.id.seat_velcome);
        _tv_usher = findViewById(R.id.tv_velcome);
        _seat_usher.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed() && _usher_listener != null) {
                _usher_listener.onUsherSwitch(_seat_usher.isChecked() ? USHER_OPEN : USHER_CLOSE);
            }
        });
    }

    public void _show_textview(int position) {
        _tv_usher.setText(getContext().getResources().getString(position == SeatSOAConstant.LEFT_FRONT ? R.string.seat_host_welcome : R.string.seat_deputy_welcome));
    }

    public void _update_ui(int aSwitch) {
        _seat_usher.setChecked(aSwitch == USHER_OPEN);
    }

    @Override
    public void onClick(View v) {
        _seat_usher.setChecked(!_seat_usher.isChecked());
        if (_usher_listener != null)
            _usher_listener.onUsherSwitch(_seat_usher.isChecked() ? USHER_OPEN : USHER_CLOSE);
    }

    public interface SeatUsherListener {
        void onUsherSwitch(int aSwitch);
    }
}
