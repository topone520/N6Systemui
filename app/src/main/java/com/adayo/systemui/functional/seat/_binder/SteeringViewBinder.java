package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.soavb.utils.Dispatcher;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.utils.TimeUtils;
import com.adayo.systemui.windows.views.seat.SeatSteeringImageView;
import com.android.systemui.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class SteeringViewBinder extends AbstractViewBinder<Integer> {
    private final static String TAG = SteeringViewBinder.class.getName();
    private SeatSteeringImageView _re_steering;
    private static final HashSet<String> _hash_set = new HashSet<>(Arrays.asList(SeatSOAConstant.MSG_EVENT_SEAT_STEERING_WHEEL, SeatSOAConstant.MSG_EVENT_EXTREME_ENERGY));
    private boolean _is_switch;
    private boolean _is_steering;
    private List<Integer> list = new ArrayList<>();

    public SteeringViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(SeatService.getInstance())
                .withGetMessageName(SeatSOAConstant.MSG_GET_SEAT_STEERING_WHEEL)
                .withSetMessageName(SeatSOAConstant.MSG_SET_SEAT_STEERING_WHEEL)
                .withRelationMessages(_hash_set)
                .withInitialValue(0)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        _re_steering = view.findViewById(R.id.re_left_front_steering);
        _re_steering.setSteeringListener(grep -> {
            _set_value(grep);
            if (grep == SeatSOAConstant.SEAT_MASSAGE_OPEN) {
                TimeUtils.startTimer();
                ReportBcmManager.getInstance().sendHvacReport("8630028", "", "开启方向盘加热(手动)");
            } else {
                ReportBcmManager.getInstance().sendHvacReport("8630029", "Steering_wheel_heating_duration", TimeUtils.getTimeInSeconds() + "");
            }
        });
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, "bundle = " + bundle.toString());
        int value = bundle.getInt("value", 0);
        switch (bundle.getString("message_id")) {
            case SeatSOAConstant.MSG_EVENT_SEAT_STEERING_WHEEL:
                _is_steering = value == SeatSOAConstant.SEAT_MASSAGE_OPEN;
                _re_steering._update_ui(value);
                if (_is_switch) {
                    list.add(value);
                }
                break;
            case SeatSOAConstant.MSG_EVENT_EXTREME_ENERGY:
                if (value == SeatSOAConstant.ENERGY_OPEN) {
                    _is_switch = true;
                    if (_is_steering) {
                        //下发一个关闭信号
                        Dispatcher.getInstance().dispatch(() -> _set_value(SeatSOAConstant.ENERGY_OPEN), 200);
                    }
                } else {
                    _is_steering = false;
                    if (list.size() == 0 || list.size() == 1) {
                        _set_value(SeatSOAConstant.ENERGY_OPEN);
                    }
                    list.clear();
                }

                break;
        }
    }
}
