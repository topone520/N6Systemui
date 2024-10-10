package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.contract.IViewBinder;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.manager.TimerManager;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.functional.seat._view.dialog.SeatMassageDialogBindView;
import com.adayo.systemui.utils.SeatResourceUtils;
import com.adayo.systemui.windows.dialogs.CommErrorDialog;
import com.adayo.systemui.windows.views.seat.SeatMassageImageView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class SeatMassageViewBinder extends AbstractViewBinder<Integer> {

    private static final String TAG = SeatMassageViewBinder.class.getSimpleName();
    private SeatMassageImageView _re_massage;
    private final int _seat_position;
    private final int _resource_id;
    private final int[] _level_resources;
    private final SeatMassageDialogBindView _massage_dialog;

    private static final HashSet<String> _hash_set = new HashSet<>(Arrays.asList(SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE, SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE_GEAR, HvacSOAConstant.MSG_EVENT_POWER_VALUE));
    private TimerManager _timer_manager;
    private boolean _is_power = true;

    public SeatMassageViewBinder(ViewBinderProviderInteger provider, int position, int resource_id, int[] level_resources, SeatMassageDialogBindView massage_dialog) {
        super(TAG, provider);
        _seat_position = position;
        _resource_id = resource_id;
        _level_resources = level_resources;
        _massage_dialog = massage_dialog;
    }

    @Override
    public void _bind_view(View view) {
        AAOP_LogUtils.d(TAG, "show view seat massage req --------->");
        _re_massage = view.findViewById(_resource_id);
        _re_massage.injectResource(_level_resources);
        _re_massage.setOnClickListener(v -> {
            Log.d(TAG, "bindView: ");
            if (_is_power) {
                _massage_dialog.show();
            } else {
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

        public static IViewBinder createLeft(int resource_id, SeatMassageDialogBindView massage_dialog) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.LEFT_FRONT);
            return createInstance(SeatSOAConstant.LEFT_FRONT, bundle, resource_id, SeatResourceUtils.LEFT_SEAT_RESOURCES_MASSAGE, massage_dialog);
        }

        public static IViewBinder createRight(int resource_id, SeatMassageDialogBindView massage_dialog) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.RIGHT_FRONT);
            return createInstance(SeatSOAConstant.RIGHT_FRONT, bundle, resource_id, SeatResourceUtils.RIGHT_SEAT_RESOURCES_MASSAGE, massage_dialog);
        }

        public static IViewBinder createLeftArea(int resource_id, SeatMassageDialogBindView massage_dialog) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.LEFT_AREA);
            return createInstance(SeatSOAConstant.LEFT_AREA, bundle, resource_id, SeatResourceUtils.LEFT_SEAT_RESOURCES_MASSAGE, massage_dialog);
        }

        public static IViewBinder createRightArea(int resource_id, SeatMassageDialogBindView massage_dialog) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.RIGHT_AREA);
            return createInstance(SeatSOAConstant.RIGHT_AREA, bundle, resource_id, SeatResourceUtils.RIGHT_SEAT_RESOURCES_MASSAGE, massage_dialog);
        }

        private static IViewBinder createInstance(final int position, final Bundle bundle, int resource_id, int[] resources, SeatMassageDialogBindView massage_dialog) {
            ViewBinderProviderInteger provider = (ViewBinderProviderInteger) new ViewBinderProviderInteger.Builder()
                    .withService(SeatService.getInstance())
                    .withBundle(bundle)
                    .withGetMessageName(SeatSOAConstant.MSG_GET_SEAT_MASSAGE_GEAR)
                    .withSetMessageName(SeatSOAConstant.MSG_SET_SEAT_MASSAGE_GEAR)
                    .withRelationMessages(_hash_set)
                    .withInitialValue(0)
                    .build();
            return new SeatMassageViewBinder(provider, position, resource_id, resources, massage_dialog);
        }
    }
}
