package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusErrorCodeEnum;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.contract.IViewBinder;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.manager.TimerManager;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.windows.views.seat.SeatMassageLevelView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class SeatMassageLevelViewBinder extends AbstractViewBinder<Integer> {

    private static final String TAG = SeatMassageLevelViewBinder.class.getSimpleName();
    private SeatMassageLevelView _massage_level;

    private final int _seat_position;
    private final int _resource_id;
    private static final HashSet<String> _hash_set = new HashSet<>(Arrays.asList(SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE, SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE_GEAR));

    public SeatMassageLevelViewBinder(ViewBinderProviderInteger provider, int position, int resource_id) {
        super(TAG, provider);
        _seat_position = position;
        _resource_id = resource_id;
    }

    @Override
    public void _bind_view(View view) {
        _massage_level = view.findViewById(_resource_id);
        _massage_level.set_levelListener(level -> {
            AAOP_LogUtils.d(TAG, "listener seat massage req --------->");
            _set_value(level);
            AAOP_LogUtils.d(TAG, "onMassageLevel: target = " + _seat_position + "  --value = " + level);
        });
    }

    @Override
    protected void _update_ui(Integer value) {
        AAOP_LogUtils.d(TAG, "_update_ui:: value = " + value);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, "bundle = " + bundle.toString());
        switch (Objects.requireNonNull(bundle.getString("message_id"))) {
            case SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE:
                if (_seat_position == bundle.getInt("target")) {
                    if (bundle.getInt("value", -1) == SeatSOAConstant.SEAT_MASSAGE_CLOSE)
                        _massage_level._update_ui(SeatSOAConstant.SEAT_MASSAGE_CLOSE);
                }
                return;
            case SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE_GEAR:
                if (_seat_position == bundle.getInt("target"))
                    _massage_level._update_ui(bundle.getInt("value"));
                break;
        }
    }


    public static class Builder {

        public static IViewBinder createLeft(int resource_id) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.LEFT_FRONT);
            return createInstance(SeatSOAConstant.LEFT_FRONT, bundle, resource_id);
        }

        public static IViewBinder createRight(int resource_id) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.RIGHT_FRONT);
            return createInstance(SeatSOAConstant.RIGHT_FRONT, bundle, resource_id);
        }

        public static IViewBinder createLeftArea(int resource_id) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.LEFT_AREA);
            return createInstance(SeatSOAConstant.LEFT_AREA, bundle, resource_id);
        }

        public static IViewBinder createRightArea(int resource_id) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.RIGHT_AREA);
            return createInstance(SeatSOAConstant.RIGHT_AREA, bundle, resource_id);
        }

        private static IViewBinder createInstance(final int position, final Bundle bundle, int resource_id) {
            AAOP_LogUtils.d(TAG, "bundle = " + bundle.toString() + "  position = " + position);
            ViewBinderProviderInteger provider = (ViewBinderProviderInteger) new ViewBinderProviderInteger.Builder()
                    .withService(SeatService.getInstance())
                    .withBundle(bundle)
                    .withGetMessageName(SeatSOAConstant.MSG_GET_SEAT_MASSAGE_GEAR)
                    .withSetMessageName(SeatSOAConstant.MSG_SET_SEAT_MASSAGE_GEAR)
                    // .withRelationMessages(_hash_set)
                    .withEventMessageName(SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE_GEAR)
                    .withInitialValue(0)
                    .build();
            return new SeatMassageLevelViewBinder(provider, position, resource_id);
        }
    }
}
