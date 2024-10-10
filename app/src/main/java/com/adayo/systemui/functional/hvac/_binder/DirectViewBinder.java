package com.adayo.systemui.functional.hvac._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.utils.ViewStyleUtils;
import com.android.systemui.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class DirectViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = DirectViewBinder.class.getSimpleName();
    private final int _position;
    private final int _resource_id;
    private TextView _tv_blow_move;
    private boolean _is_direct_blowing = true;

    private static final HashSet<String> _event_list = new HashSet<>(Arrays.asList(HvacSOAConstant.MSG_EVENT_FAN_SPEED, HvacSOAConstant.MSG_EVENT_BLOWING_DIRECTION));

    public DirectViewBinder(int position, int resource_id) {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(HvacService.getInstance())
                .withGetMessageName(HvacSOAConstant.MSG_GET_BLOWING_DIRECTION)
                .withSetMessageName(HvacSOAConstant.MSG_SET_BLOWING_DIRECTION)
                .withRelationMessages(_event_list)
                .withInitialValue(1)
                .withCompare(false)
                .build());
        _position = position;
        _resource_id = resource_id;
    }

    @Override
    public void _bind_view(View view) {
        _tv_blow_move = view.findViewById(_resource_id);
        _tv_blow_move.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            _is_direct_blowing = !_is_direct_blowing;
            _tv_blow_move.setText(_is_direct_blowing ? R.string.hvac_direct_blow : R.string.hvac_no_direct_blow);
            bundle.putInt("action", _is_direct_blowing ? AreaConstant.DIRECT_BLOWING : AreaConstant.PREVENT_DIRECT_BLOWING);
            bundle.putInt("target", _position);
            boolean result = HvacService.getInstance().invoke(HvacSOAConstant.MSG_SET_BLOWING_DIRECTION, bundle, new ADSBusReturnValue());
        });
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, "bundle = " + bundle.toString());
        String message_id = bundle.getString("message_id");
        switch (Objects.requireNonNull(message_id)) {
            case HvacSOAConstant.MSG_EVENT_FAN_SPEED:
                boolean is_hvac_switch = bundle.getInt("value") == HvacSOAConstant.HVAC_OFF_IO;
                _tv_blow_move.setSelected(is_hvac_switch);
                break;
            case HvacSOAConstant.MSG_EVENT_BLOWING_DIRECTION:
                break;
        }
    }
}
