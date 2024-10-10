package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.windows.dialogs.BindViewBaseDialog;

/**
 * 座椅调节 前排和后排弹框不一个
 * 传入位置进行判断
 */
public class SeatAdjustViewBinder extends AbstractViewBinder<Integer> {
    private final static String TAG = SeatAdjustViewBinder.class.getName();
    private RelativeLayout _seat_adjust;
    private int _position;
    private final int _resource_id;
    private BindViewBaseDialog _front_row_dialog, _area_row_dialog;

    public SeatAdjustViewBinder(int position, int resource_id, BindViewBaseDialog front_row_dialog, BindViewBaseDialog area_row_dialog) {
        //TODO 座椅调节待定功能
        //  super(new ViewBinderProviderInteger(SeatService.getInstance(), SeatSOAConstant., SeatSOAConstant., SeatSOAConstant.));
        super(TAG,new ViewBinderProviderInteger.Builder()
                .withService(SeatService.getInstance())
                .withGetMessageName(SeatSOAConstant.MSG_GET_SEAT_HEATING)
                .withSetMessageName(SeatSOAConstant.MSG_SET_SEAT_HEATING)
                .withEventMessageName(SeatSOAConstant.MSG_EVENT_SEAT_HEATING)
                .withInitialValue(0)
                .withCompare(false)
                .build());
        _position = position;
        _resource_id = resource_id;
        _front_row_dialog = front_row_dialog;
        _area_row_dialog = area_row_dialog;
    }

    @Override
    public void _bind_view(View view) {
        _seat_adjust = view.findViewById(_resource_id);
        _seat_adjust.setOnClickListener(v -> {
            if (_position == SeatSOAConstant.LEFT_FRONT || _position == SeatSOAConstant.RIGHT_FRONT) {
                _front_row_dialog.show();
            } else {
                _area_row_dialog.show();
            }
        });
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}
