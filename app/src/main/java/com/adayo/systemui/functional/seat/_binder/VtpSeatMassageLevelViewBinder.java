package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.windows.views.seat.SeatMassageLevelView;
import com.adayo.systemui.windows.views.seat.VtpSeatMassageImageView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class VtpSeatMassageLevelViewBinder extends AbstractViewBinder<Integer> {

    private static final String TAG = VtpSeatMassageLevelViewBinder.class.getSimpleName();
    private VtpSeatMassageImageView _massage_level;
    private final int _seat_position;
    private final int _resource_id;
    private static final HashSet<String> _hash_set = new HashSet<>(Arrays.asList(SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE, SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE_GEAR));

    public VtpSeatMassageLevelViewBinder(int position, int resource_id) {
        super(TAG,new ViewBinderProviderInteger.Builder()
                .withService(SeatService.getInstance())
                .withGetMessageName(SeatSOAConstant.MSG_GET_SEAT_MASSAGE_GEAR)
                .withSetMessageName(SeatSOAConstant.MSG_SET_SEAT_MASSAGE_GEAR)
                .withRelationMessages(_hash_set)
                .withInitialValue(0)
                .build());
        _seat_position = position;
        _resource_id = resource_id;
    }

    @Override
    public void _bind_view(View view) {
        _massage_level = view.findViewById(_resource_id);
        _massage_level.setViewListener(level -> {
            AAOP_LogUtils.d(TAG,"listener seat massage req --------->");
            Bundle bundle = new Bundle();
            bundle.putInt("target", _seat_position);
            bundle.putInt("value", level);
            bundle.putInt(TAG, level);
            boolean invoke = SeatService.getInstance().invoke(SeatSOAConstant.MSG_SET_SEAT_MASSAGE_GEAR, bundle, new ADSBusReturnValue());
            AAOP_LogUtils.d(TAG, "onMassageLevel: target = " + _seat_position + "  --value = " + level + "  service return = " + invoke);
        });
    }

    @Override
    protected void _update_ui(Integer value) {
        AAOP_LogUtils.d("_update_ui:: value = " + value);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, "bundle = " + bundle.toString());
        switch (Objects.requireNonNull(bundle.getString("message_id"))) {
            case SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE:
                if (_seat_position == bundle.getInt("target")){
                    _massage_level._update_ui(bundle.getInt("value") == SeatSOAConstant.SEAT_MASSAGE_OPEN ? SeatSOAConstant.SEAT_MASSAGE_LEVEL_MAX : SeatSOAConstant.SEAT_MASSAGE_CLOSE);
                }
                return;
            case SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE_GEAR:
                if (_seat_position == bundle.getInt("target"))
                    _massage_level._update_ui(bundle.getInt("value"));
                break;
        }
    }
}
