package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.functional.seat._view.dialog.VtpSeatMassageDialogBindView;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.android.systemui.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class VtpSeatMassageModeShowViewBinder extends AbstractViewBinder<Integer> {

    private static final String TAG = VtpSeatMassageModeShowViewBinder.class.getSimpleName();
    private TextView _tv_massage;
    private final int _seat_position;
    private final int _resource_id;
    private VtpSeatMassageDialogBindView _dialog;
    private String[] _mode;
    private static final HashSet<String> _hash_set = new HashSet<>(Arrays.asList(SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE, SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE_GEAR, SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE_MOVE));

    public VtpSeatMassageModeShowViewBinder(int position, int resource_id, VtpSeatMassageDialogBindView dialog) {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(SeatService.getInstance())
                .withGetMessageName(SeatSOAConstant.MSG_GET_SEAT_MASSAGE_GEAR)
                .withSetMessageName(SeatSOAConstant.MSG_SET_SEAT_MASSAGE_GEAR)
                .withRelationMessages(_hash_set)
                .withInitialValue(0)
                .build());
        _seat_position = position;
        _resource_id = resource_id;
        _dialog = dialog;
    }

    @Override
    public void _bind_view(View view) {
        _mode = new String[]{view.getResources().getString(R.string.seat_move1),
                view.getResources().getString(R.string.seat_move2),
                view.getResources().getString(R.string.seat_move3),
                view.getResources().getString(R.string.seat_move4),
                view.getResources().getString(R.string.seat_move5),
                view.getResources().getString(R.string.seat_move6),
                view.getResources().getString(R.string.seat_move7),
                view.getResources().getString(R.string.seat_move8)};
        _tv_massage = view.findViewById(_resource_id);
        _tv_massage.setOnClickListener(v -> _dialog.show());
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
                if (_seat_position == bundle.getInt("target")) {
                    _tv_massage.setBackgroundResource(bundle.getInt("value") == AreaConstant.HVAC_ON_IO ? R.drawable.comm_c4972b8_bg : R.drawable.comm_c8039414f_bg);
                }
                return;
            case SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE_GEAR:
                if (_seat_position == bundle.getInt("target")) {
                    int value = bundle.getInt("value");
                    _tv_massage.setBackgroundResource((value >= SeatSOAConstant.SEAT_MASSAGE_OPEN && value <= SeatSOAConstant.SEAT_MASSAGE_LEVEL_MAX) ? R.drawable.comm_c4972b8_bg : R.drawable.comm_c8039414f_bg);
                }
                break;
            case SeatSOAConstant.MSG_EVENT_SEAT_MASSAGE_MOVE:
                if (_seat_position == bundle.getInt("target")) {
                    int value = bundle.getInt("value");
                    if (value < 0 || value > _mode.length - 1) return;
                    if (value > 0) value = value - 1;
                    _tv_massage.setText(_mode[value]);
                }

                break;
        }
    }
}
