package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.soavb.utils.Dispatcher;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.android.systemui.R;

public class CollapseViewBinder extends AbstractViewBinder<Integer> {
    private final static String TAG = CollapseViewBinder.class.getName();
    private boolean is_collapse;
    private int _left_area, _right_area;
    private final int MAX = 100;

    public CollapseViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(SeatService.getInstance())
                .withInitialValue(0)
                .build());
    }

    @Override
    public void _bind_view(View view) {

        TextView tv_collapse = view.findViewById(R.id.tv_collapse);
        tv_collapse.setOnClickListener(v -> {
            is_collapse = !is_collapse;
            if (is_collapse) {
                //先获取数据保存
                //下发后排放倒信号
                Bundle left_bundle = new Bundle();
                left_bundle.putInt("target", SeatSOAConstant.LEFT_AREA);
                ADSBusReturnValue left_value = new ADSBusReturnValue();
                SeatService.getInstance().invoke(SeatSOAConstant.MSG_GET_SEAT_BACKREST, left_bundle, left_value);
                _left_area = left_value.getmReturnValue().getInt("value");
                Bundle right_bundle = new Bundle();
                right_bundle.putInt("target", SeatSOAConstant.LEFT_AREA);
                ADSBusReturnValue right_value = new ADSBusReturnValue();
                SeatService.getInstance().invoke(SeatSOAConstant.MSG_GET_SEAT_BACKREST, left_bundle, right_value);
                _right_area = right_value.getmReturnValue().getInt("value");

                //下发放倒
                Bundle bundle = new Bundle();
                bundle.putInt("target", SeatSOAConstant.LEFT_AREA);
                bundle.putInt("value", MAX);
                SeatService.getInstance().invoke(SeatSOAConstant.MSG_SET_SEAT_BACKREST, bundle, new ADSBusReturnValue());
                Dispatcher.getInstance().dispatch(() -> {
                    Bundle bundle1 = new Bundle();
                    bundle1.putInt("value", MAX);
                    bundle1.putInt("target", SeatSOAConstant.RIGHT_AREA);
                    SeatService.getInstance().invoke(SeatSOAConstant.MSG_SET_SEAT_BACKREST, bundle1, new ADSBusReturnValue());
                }, 200);

            } else {
                //将之前保存的数据下发到后排进行恢复
                Bundle bundle = new Bundle();
                bundle.putInt("target", SeatSOAConstant.LEFT_AREA);
                bundle.putInt("value", _left_area);
                SeatService.getInstance().invoke(SeatSOAConstant.MSG_SET_SEAT_BACKREST, bundle, new ADSBusReturnValue());
                Dispatcher.getInstance().dispatch(() -> {
                    Bundle bundle1 = new Bundle();
                    bundle1.putInt("target", SeatSOAConstant.RIGHT_AREA);
                    bundle1.putInt("value", _right_area);
                    SeatService.getInstance().invoke(SeatSOAConstant.MSG_SET_SEAT_BACKREST, bundle1, new ADSBusReturnValue());
                }, 200);
            }

            tv_collapse.setText(is_collapse ? R.string.one_click_collapse : R.string.one_click_recovery);
        });
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {
    }
}
