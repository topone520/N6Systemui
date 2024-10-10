package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusErrorCodeEnum;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.contract.IViewBinder;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.utils.SeatResourceUtils;
import com.adayo.systemui.windows.views.seat.SeatUsherView;

public class SeatUsherViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = SeatUsherViewBinder.class.getSimpleName();
    private SeatUsherView _seat_usher;
    private final int _position;
    private final int _resource_id;

    public SeatUsherViewBinder(ViewBinderProviderInteger provider,int position, int resource_id) {
        super(TAG, provider);
        _position = position;
        _resource_id = resource_id;
    }

    @Override
    public void _bind_view(View view) {
        Log.d(TAG, "_bind_view: dialog usher initView");
        _seat_usher = view.findViewById(_resource_id);
        _seat_usher._show_textview(_position);
        _seat_usher.set_usher_listener(aSwitch -> {
            final Bundle _bundle = new Bundle();
            _bundle.putInt("value", aSwitch);
            _bundle.putInt("target", _position);
            boolean resultVelcome = SeatService.getInstance().invoke(SeatSOAConstant.MSG_SET_SEAT_VELCOME, _bundle, new ADSBusReturnValue());
            AAOP_LogUtils.d(TAG, "seatVelcome: seatPosition = " + _position + "    mode = " + aSwitch + " result = " + resultVelcome);
        });
    }

    @Override
    protected void _update_ui(Integer value) {
        AAOP_LogUtils.d(TAG, "_update_ui:: value = " + value);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        int status = bundle.getInt("value");
        int target = bundle.getInt("target");
        AAOP_LogUtils.d(TAG, "sts velcome value = " + status + "  target  = " + target);
        if (target == _position)
            _seat_usher._update_ui(status);
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

        private static IViewBinder createInstance(final int position, final Bundle bundle, int resource_id) {
            ViewBinderProviderInteger provider = (ViewBinderProviderInteger) new ViewBinderProviderInteger.Builder()
                    .withService(SeatService.getInstance())
                    .withBundle(bundle)
                    .withGetMessageName(SeatSOAConstant.MSG_GET_SEAT_VELCOME)
                    .withSetMessageName(SeatSOAConstant.MSG_SET_SEAT_VELCOME)
                    .withEventMessageName(SeatSOAConstant.MSG_EVENT_SEAT_VELCOME)
                    .withInitialValue(0)
                    .build();
            return new SeatUsherViewBinder(provider, position, resource_id);
        }
    }
}
