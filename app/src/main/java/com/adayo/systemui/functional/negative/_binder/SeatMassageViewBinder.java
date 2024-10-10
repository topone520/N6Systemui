package com.adayo.systemui.functional.negative._binder;

import android.os.Bundle;

import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.contract.IViewBinder;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.soavb.foundation.view.AbstractBindViewDialog;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.manager.TimerManager;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.dialogs.CommErrorDialog;
import com.adayo.systemui.windows.views.QsIconView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class SeatMassageViewBinder extends AbstractViewBinder<Integer> {

    private static final String TAG = SeatMassageViewBinder.class.getSimpleName();
    private QsIconView _re_massage;
    private final int _seat_position;
    private final int _resource_id;
    private final int[] _level_resources;
    private final AbstractBindViewDialog _dialog;
    private static final HashSet<String> _hash_set = new HashSet<>(Arrays.asList(SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE, SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE_GEAR));
    private TimerManager _timer_manager;
    private boolean _is_power = true;


    public SeatMassageViewBinder(ViewBinderProviderInteger provider, int position, int resource_id, int[] level_resources, AbstractBindViewDialog dialog) {
        super(TAG, provider);
        _seat_position = position;
        _resource_id = resource_id;
        _level_resources = level_resources;
        _dialog = dialog;
    }

    @Override
    protected void _bind_view(View view) {
        AAOP_LogUtils.d(TAG, "show view seat massage req --------->");
        _re_massage = view.findViewById(_resource_id);
        _re_massage.injectResource(_level_resources);
        _re_massage.showIconSubscript();
        _re_massage.setListener(grep -> {
            if (_is_power){
                _set_value(grep);
            }else {
                CommErrorDialog.getInstance().show();
            }

        });
        _re_massage.setOnChildClickListener(v -> {
            if (_is_power){
                _dialog.show();
            }else {
                CommErrorDialog.getInstance().show();
            }
        });
        _init_time();
    }

    private void _init_time() {
        _timer_manager = new TimerManager(() -> {
            AAOP_LogUtils.d(TAG, "target = " + _seat_position);
            Bundle bundle = new Bundle();
            bundle.putInt("value", SeatSOAConstant.SEAT_MASSAGE_CLOSE);
            bundle.putString("message_id", SeatSOAConstant.MSG_SET_SEAT_MASSAGE);
            bundle.putInt("target", _seat_position);
            SeatService.getInstance().invoke(SeatSOAConstant.MSG_SET_SEAT_MASSAGE, bundle, new ADSBusReturnValue());
        });
    }


    @Override
    protected void _update_ui(Integer value) {
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        int value = bundle.getInt("value");
        switch (Objects.requireNonNull(bundle.getString("message_id"))) {
            case SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE:
                int target = bundle.getInt("target");
                if (_seat_position == target && value == SeatSOAConstant.SEAT_MASSAGE_CLOSE) {
                    _re_massage._update_ui(SeatSOAConstant.SEAT_MASSAGE_CLOSE);
                    _timer_manager.stopTimers();
                    AAOP_LogUtils.d(TAG, "stop timer");
                } else {
                    if (_seat_position == target) {
                        _timer_manager.startTimers();
                        AAOP_LogUtils.d(TAG, "start timer");
                    }
                }
                return;
            case SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE_GEAR:
                if (_seat_position == bundle.getInt("target"))
                    _re_massage._update_ui(value);
                break;
            case HvacSOAConstant.MSG_EVENT_POWER_VALUE:
                _is_power = value == HvacSOAConstant.HVAC_POWER_MODE_IGN_ON;
                break;
        }
    }

    public static class Builder {
        public static IViewBinder createLeft(int resource_id, int[] level_resources, AbstractBindViewDialog dialog) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.LEFT_FRONT);
            return createInstance(SeatSOAConstant.LEFT_FRONT, bundle, resource_id, level_resources, dialog);
        }

        public static IViewBinder createRight(int resource_id, int[] level_resources, AbstractBindViewDialog dialog) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.RIGHT_FRONT);
            return createInstance(SeatSOAConstant.RIGHT_FRONT, bundle, resource_id, level_resources, dialog);
        }

        private static IViewBinder createInstance(final int position, final Bundle bundle, int resource_id, int[] level_resources, AbstractBindViewDialog dialog) {
            ViewBinderProviderInteger provider = (ViewBinderProviderInteger) new ViewBinderProviderInteger.Builder()
                    .withService(SeatService.getInstance())
                    .withBundle(bundle)
                    .withGetMessageName(SeatSOAConstant.MSG_GET_SEAT_MASSAGE_GEAR)
                    .withSetMessageName(SeatSOAConstant.MSG_SET_SEAT_MASSAGE_GEAR)
                    .withRelationMessages(_hash_set)
                    .withInitialValue(-1)
                    .build();
            return new SeatMassageViewBinder(provider, position, resource_id, level_resources, dialog);
        }
    }

}