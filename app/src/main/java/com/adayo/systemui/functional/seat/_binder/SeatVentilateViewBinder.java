package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.contract.IViewBinder;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.soavb.utils.Dispatcher;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.utils.SeatResourceUtils;
import com.adayo.systemui.utils.TimeUtils;
import com.adayo.systemui.windows.views.seat.SeatVentilateImageView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class SeatVentilateViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = SeatVentilateViewBinder.class.getSimpleName();
    private SeatVentilateImageView _re_ventilate;
    private final int _seat_position;
    private final int _resource_id;
    private final int[] _level_resources;
    private static final HashSet<String> _hash_set = new HashSet<>(Arrays.asList(SeatSOAConstant.MSG_EVENT_SEAT_VENTILATION, SeatSOAConstant.MSG_EVENT_EXTREME_ENERGY, HvacSOAConstant.MSG_EVENT_POWER_VALUE, SeatSOAConstant.MSG_EVENT_WARNING_LEVEL));

    private boolean _is_switch;
    private int _level;
    private int number;

    public SeatVentilateViewBinder(ViewBinderProviderInteger provider, int position, int resource_id, int[] level_resources) {
        super(TAG, provider);
        _seat_position = position;
        _resource_id = resource_id;
        _level_resources = level_resources;
    }

    @Override
    public void _bind_view(View view) {
        _re_ventilate = view.findViewById(_resource_id);
        _re_ventilate.injectResource(_level_resources);
        _re_ventilate.setListener(level -> {
            AAOP_LogUtils.d(TAG, "level = " + level);
            _set_value(level);
            if (level != SeatSOAConstant.SEAT_MASSAGE_CLOSE) {
                TimeUtils.startTimer();
                ReportBcmManager.getInstance().sendSeatReport("2004003", "", TimeUtils.getVentilateJson(_seat_position, level));
            } else {
                ReportBcmManager.getInstance().sendHvacReport("2004005", "seat_wind_duration", TimeUtils.getTimeInSeconds());
            }
        });
    }


    @Override
    protected void _update_ui(Integer value) {
        AAOP_LogUtils.d("update_ui:: value = " + value);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.i(TAG, "_update_ui with bundle === " + bundle.toString() + "\rseat_position = " + _seat_position);
        int value = bundle.getInt("value", 0);
        switch (Objects.requireNonNull(bundle.getString("message_id"))) {
            case SeatSOAConstant.MSG_EVENT_SEAT_STEERING_WHEEL:
                if (bundle.getInt("target") == _seat_position) {
                    _re_ventilate._update_ui(value);
                }
                if (_is_switch) {
                    number++;
                }
                break;
            case SeatSOAConstant.MSG_EVENT_EXTREME_ENERGY:
                if (value == SeatSOAConstant.ENERGY_OPEN) {
                    _is_switch = true;
                    _level = _re_ventilate.get_level();
                    if (_re_ventilate.get_level() != SeatSOAConstant.SEAT_MASSAGE_CLOSE) {
                        Dispatcher.getInstance().dispatch(() -> _set_value(SeatSOAConstant.SEAT_MASSAGE_CLOSE), 200);
                    }
                } else {
                    if (number == 1) {
                        _set_value(_level);
                    }
                    number = 0;
                }
                break;
            case HvacSOAConstant.MSG_EVENT_POWER_VALUE:
                _re_ventilate.setPowerStatus(value);
                break;
            case SeatSOAConstant.MSG_EVENT_WARNING_LEVEL:
                _re_ventilate.setWarningLevel(value);
                break;
        }
    }


    public static class Builder {

        public static IViewBinder createLeft(int resource_id) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.LEFT_FRONT);
            return createInstance(SeatSOAConstant.LEFT_FRONT, bundle, resource_id, SeatResourceUtils.LEFT_SEAT_RESOURCES_VENTILATE);
        }

        public static IViewBinder createRight(int resource_id) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.RIGHT_FRONT);
            return createInstance(SeatSOAConstant.RIGHT_FRONT, bundle, resource_id, SeatResourceUtils.RIGHT_SEAT_RESOURCES_VENTILATE);
        }

        private static IViewBinder createInstance(final int position, final Bundle bundle, int resource_id, int[] resources) {
            ViewBinderProviderInteger provider = (ViewBinderProviderInteger) new ViewBinderProviderInteger.Builder()
                    .withService(SeatService.getInstance())
                    .withBundle(bundle)
                    .withGetMessageName(SeatSOAConstant.MSG_GET_SEAT_VENTILATION)
                    .withSetMessageName(SeatSOAConstant.MSG_SET_SEAT_VENTILATION)
                    .withRelationMessages(_hash_set)
                    .withInitialValue(0)
                    .build();
            return new SeatVentilateViewBinder(provider, position, resource_id, resources);
        }
    }
}
