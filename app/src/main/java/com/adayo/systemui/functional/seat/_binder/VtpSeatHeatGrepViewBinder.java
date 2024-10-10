package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.manager.TimerManager;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.android.systemui.R;

/**
 * 当主副驾驶加热都没有打开时候不让点击
 * 当主驾驶有换挡操作时候启动计时器，根据档位自动换，或者点击切换以后，计时器时间跟着档位持续更新
 * 副驾驶同理，一个位置用一个
 */
public class VtpSeatHeatGrepViewBinder extends AbstractViewBinder<Integer> {
    private final static String TAG = VtpSeatHeatGrepViewBinder.class.getName();
    private boolean _is_open;
    public VtpSeatHeatGrepViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(SeatService.getInstance())
                .withGetMessageName(SeatSOAConstant.MSG_GET_SEAT_HEATING)
                .withSetMessageName(SeatSOAConstant.MSG_SET_SEAT_HEATING)
                .withEventMessageName(SeatSOAConstant.MSG_EVENT_SEAT_HEATING)
                .withInitialValue(0)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        ImageView _heat_grep = view.findViewById(R.id.iv_left_front_heat_grep);

        _heat_grep.setOnClickListener(v -> {
            _is_open = !_is_open;
            _heat_grep.setImageResource(_is_open ? R.mipmap.vtp_dock_icon_autoheat_s : R.mipmap.vtp_dock_icon_autoheat_n);
            start_timer();
        });
    }

    private void start_timer() {

    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {
        int target = bundle.getInt("target");
        switch (target) {
            case SeatSOAConstant.LEFT_FRONT:
                break;
            case SeatSOAConstant.RIGHT_FRONT:
                break;
        }
    }
}
