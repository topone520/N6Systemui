package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.contract.IViewBinder;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.windows.views.seat.SeatHeatImageView;
import com.adayo.systemui.windows.views.seat.VtpSeatHeatImageView;

public class VtpSeatHeatViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = VtpSeatHeatViewBinder.class.getSimpleName();
    private VtpSeatHeatImageView _re_heat;
    private final int _seat_position;
    private final int _resource_id;
    private final int[] _resource;

    public VtpSeatHeatViewBinder(ViewBinderProviderInteger provider, int position, int resource_id, int[] resource) {
        super(
                ((position == 1) ? "Left" : "Right") + TAG, provider);
        _seat_position = position;
        _resource_id = resource_id;
        _resource = resource;
    }

    @Override
    public void _bind_view(View view) {
        _re_heat = view.findViewById(_resource_id);
        _re_heat.injectResource(_resource);
        _re_heat.setViewListener(level -> {
            Log.d(TAG, "bindView: level = " + level);
            _set_value(level);
        });
    }

    @Override
    protected void _update_ui(Integer value) {
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        if (!bundle.containsKey("target") || !bundle.containsKey("value")) {
            AAOP_LogUtils.i(TAG, "not exist key(target, value)");
            return;
        }
        int value = bundle.getInt("value");
        AAOP_LogUtils.i(TAG, "_update_ui with bundle === " + bundle.toString() + "\rseat_position = " + _seat_position + "   value = " + value);

        if (bundle.getInt("target") == _seat_position) {
            _re_heat._update_ui(value);
        }
    }


    public static class Builder {

        public static IViewBinder createLeft(int resource_id, int[] resources) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.LEFT_FRONT);
            return createInstance(SeatSOAConstant.LEFT_FRONT, bundle, resource_id, resources);
        }

        public static IViewBinder createRight(int resource_id, int[] resources) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.RIGHT_FRONT);
            return createInstance(SeatSOAConstant.RIGHT_FRONT, bundle, resource_id, resources);
        }

        public static IViewBinder createLeftArea(int resource_id, int[] resources) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.LEFT_AREA);
            return createInstance(SeatSOAConstant.LEFT_AREA, bundle, resource_id,resources);
        }

        public static IViewBinder createRightArea(int resource_id, int[] resources) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.RIGHT_AREA);
            return createInstance(SeatSOAConstant.RIGHT_AREA, bundle, resource_id,resources);
        }

        private static IViewBinder createInstance(final int position, final Bundle bundle, int resource_id, int[] resources) {
            ViewBinderProviderInteger provider = (ViewBinderProviderInteger) new ViewBinderProviderInteger.Builder()
                    .withService(SeatService.getInstance())
                    .withBundle(bundle)
                    .withGetMessageName(SeatSOAConstant.MSG_GET_SEAT_HEATING)
                    .withSetMessageName(SeatSOAConstant.MSG_SET_SEAT_HEATING)
                    .withEventMessageName(SeatSOAConstant.MSG_EVENT_SEAT_HEATING)
                    .withInitialValue(0)
                    .build();
            return new VtpSeatHeatViewBinder(provider, position, resource_id, resources);
        }
    }

}
