package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusErrorCodeEnum;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.contract.IViewBinder;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.functional.negative._binder.SeatHeatViewBinder;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.windows.views.seat.SeatMassageModeView;

public class SeatMassageMoveViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = SeatMassageMoveViewBinder.class.getSimpleName();
    private SeatMassageModeView _massage_move;
    private final int _seat_position;
    private final int _resource_id;

    public SeatMassageMoveViewBinder(ViewBinderProviderInteger provider, int position, int resource_id) {
        super(TAG, provider);
        _seat_position = position;
        _resource_id = resource_id;
        AAOP_LogUtils.d(TAG, "position = " + position + "   id ->this = " + resource_id);
    }

    @Override
    public void _bind_view(View view) {
        _massage_move = view.findViewById(_resource_id);
        _massage_move.setMoveListener(pos -> {
            Bundle _bundle = new Bundle();
            _bundle.putInt("target", _seat_position);
            _bundle.putInt("value", pos);
            boolean invoke = SeatService.getInstance().invoke(SeatSOAConstant.MSG_SET_SEAT_MASSAGE_MOVE, _bundle, new ADSBusReturnValue());
            AAOP_LogUtils.d(TAG, "onSelectMassageMove: target = " + _seat_position + "  --value = " + pos + "  service return = " + invoke);
        });
    }

    @Override
    protected void _update_ui(Integer value) {
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, "bundle = " + bundle.toString());
        if (bundle.getInt("target") == _seat_position) {
            _massage_move.update_ui(bundle.getInt("value", -1));
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
            ViewBinderProviderInteger provider = (ViewBinderProviderInteger) new ViewBinderProviderInteger.Builder()
                    .withService(SeatService.getInstance())
                    .withBundle(bundle)
                    .withGetMessageName(SeatSOAConstant.MSG_GET_SEAT_MASSAGE_MOVE)
                    .withSetMessageName(SeatSOAConstant.MSG_SET_SEAT_MASSAGE_MOVE)
                    .withEventMessageName(SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE_MOVE)
                    .withInitialValue(0)
                    .build();
            return new SeatMassageMoveViewBinder(provider, position, resource_id);
        }
    }
}
