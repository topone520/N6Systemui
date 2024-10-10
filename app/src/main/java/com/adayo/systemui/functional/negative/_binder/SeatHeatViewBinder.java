package com.adayo.systemui.functional.negative._binder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.contract.IViewBinder;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.soavb.utils.Dispatcher;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.windows.views.QsIconView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;


public class SeatHeatViewBinder extends AbstractViewBinder<Integer> {

    private static final String TAG = SeatHeatViewBinder.class.getSimpleName();
    private QsIconView _re_heat;
    private final int _seat_position;
    private final int _resource_id;
    private final int[] _resource;

    private boolean _is_switch;
    private int _level;
    private int number;
    private static final HashSet<String> _hash_set = new HashSet<>(Arrays.asList(SeatSOAConstant.MSG_EVENT_SEAT_HEATING, SeatSOAConstant.MSG_EVENT_EXTREME_ENERGY, HvacSOAConstant.MSG_EVENT_POWER_VALUE));

    public SeatHeatViewBinder(ViewBinderProviderInteger provider, int position, int resource_id, int[] resource) {
        super(TAG, provider);
        _seat_position = position;
        _resource_id = resource_id;
        _resource = resource;
    }

    @Override
    protected void _bind_view(View view) {
        Log.d(TAG, "bindView: list = " + "  re_heat = " + _re_heat);
        _re_heat = view.findViewById(_resource_id);
        _re_heat.injectResource(_resource);
        _re_heat.setListener(level -> {
            Log.d(TAG, "bindView: level = " + level);
            _set_value(level);
        });
    }

    @Override
    protected void _update_ui(Integer value) {
        Log.d(TAG, "_update_ui with value: --------- = " + value);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, "bundle = " + bundle);
        int value = bundle.getInt("value", 0);
        switch (Objects.requireNonNull(bundle.getString("message_id"))) {
            case SeatSOAConstant.MSG_EVENT_SEAT_STEERING_WHEEL:
                if (bundle.getInt("target") == _seat_position) {
                    _re_heat._update_ui(value);
                }
                if (_is_switch) {
                    number++;
                }
                break;
            case SeatSOAConstant.MSG_EVENT_EXTREME_ENERGY:
                if (value == SeatSOAConstant.ENERGY_OPEN) {
                    _is_switch = true;
                    _level = _re_heat.get_level();
                    if (_re_heat.get_level() != SeatSOAConstant.SEAT_MASSAGE_CLOSE) {
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
                _re_heat.setPowerStatus(value);
                break;
        }
    }

    public static class Builder {

        public static IViewBinder createLeft(int resource_id, int[] resource) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.LEFT_FRONT);
            return createInstance(SeatSOAConstant.LEFT_FRONT, bundle, resource_id, resource);
        }

        public static IViewBinder createRight(int resource_id, int[] resource) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.RIGHT_FRONT);
            return createInstance(SeatSOAConstant.RIGHT_FRONT, bundle, resource_id, resource);
        }

        private static IViewBinder createInstance(final int position, final Bundle bundle, int resource_id, int[] resource) {
            ViewBinderProviderInteger provider = (ViewBinderProviderInteger) new ViewBinderProviderInteger.Builder()
                    .withService(SeatService.getInstance())
                    .withBundle(bundle)
                    .withGetMessageName(SeatSOAConstant.MSG_GET_SEAT_HEATING)
                    .withSetMessageName(SeatSOAConstant.MSG_SET_SEAT_HEATING)
                    .withRelationMessages(_hash_set)
                    .withInitialValue(-1).build();
            return new SeatHeatViewBinder(provider, position, resource_id, resource);
        }
    }
}