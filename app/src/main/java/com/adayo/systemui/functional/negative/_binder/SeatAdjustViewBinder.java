package com.adayo.systemui.functional.negative._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.windows.dialogs.BindViewBaseDialog;
import com.adayo.systemui.windows.views.QsIconView;
import com.android.systemui.R;

/**
 * 座椅调节 前排和后排弹框不一个
 * 传入位置进行判断
 */
public class SeatAdjustViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = SeatAdjustViewBinder.class.getName();
    QsIconView _passenger_adjust;
    int _position;
    BindViewBaseDialog _front_row_dialog;

    public SeatAdjustViewBinder(int position, BindViewBaseDialog front_row_dialog) {
        //TODO 座椅调节待定功能
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(SeatService.getInstance())
                .withGetMessageName(SeatSOAConstant.MSG_GET_SEAT_HEATING)
                .withSetMessageName(SeatSOAConstant.MSG_SET_SEAT_HEATING)
                .withEventMessageName(SeatSOAConstant.MSG_EVENT_SEAT_HEATING)
                .withInitialValue(-1)
                .withCompare(false)
                .build());
        _position = position;
        _front_row_dialog = front_row_dialog;
    }

    @Override
    protected void _bind_view(View view) {
        _passenger_adjust = view.findViewById(R.id.passenger_adjust);

        _passenger_adjust.setListener(v -> {
            if (_position == SeatSOAConstant.LEFT_FRONT || _position == SeatSOAConstant.RIGHT_FRONT) {
                _front_row_dialog.show();
            }
        });
        _passenger_adjust.setOnChildClickListener(v -> {

        });
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}

