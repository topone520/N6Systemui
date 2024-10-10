package com.adayo.systemui.windows.views.hvac;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.view.AbstractBindViewFramelayout;
import com.adayo.soavb.service.SoaService;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.functional.hvac._binder.ShortcutTempAdjustViewBinder;
import com.adayo.systemui.functional.hvac._binder.ShortcutWinSpeedViewBinder;
import com.adayo.systemui.functional.seat._binder.ShortcutHeatViewBinder;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.utils.SeatResourceUtils;
import com.adayo.systemui.utils.WindowsUtils;
import com.android.systemui.R;

public class HvacInitialShortcutView extends AbstractBindViewFramelayout {
    private static final String TAG = HvacInitialShortcutView.class.getName();

    public HvacInitialShortcutView(@NonNull Context context) {
        super(context);
        //注册空调 座椅 子服务
        _init_service();
    }

    @Override
    protected void createViewBinder() {
        //座椅加热
        super.insertViewBinder(new ShortcutHeatViewBinder(SeatSOAConstant.LEFT_FRONT, R.id.iv_driver_heat, SeatResourceUtils.LEFT_SEAT_RESOURCES_HEAT));
        super.insertViewBinder(new ShortcutHeatViewBinder(SeatSOAConstant.RIGHT_FRONT, R.id.iv_copilot_heat, SeatResourceUtils.RIGHT_SEAT_RESOURCES_HEAT));
        //温度选择
        super.insertViewBinder(ShortcutTempAdjustViewBinder.Builder.createLeft(R.id.hor_driver_temp));
        super.insertViewBinder(ShortcutTempAdjustViewBinder.Builder.createRight(R.id.hor_copilot_temp));
        //风速调节
        super.insertViewBinder(new ShortcutWinSpeedViewBinder());
    }

    @Override
    protected int acquireResourceId() {
        return R.layout.acd_layout;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return WindowsUtils.showHvacPanel(event);
    }

    private void _init_service() {
        SeatService.getInstance().connect("HvacInitialViewBar_SEAT", service -> {
            AAOP_LogUtils.i(TAG, "Seat::onConnected()" + service.acquireName());
//            boolean subscribe = service.subscribe("RECEIVER_NAME_HvacInitialViewBar_SEAT", new Bundle(), this::dispatchMessage);
            service.subscribe(this::dispatchMessage);
//            Log.d(TAG, "initView: subscribe = " + subscribe);
        });
        HvacService.getInstance().connect("HvacInitialViewBar_HVAC", service -> {
            AAOP_LogUtils.i(TAG, "Hvac::onConnected()" + service.acquireName());
            service.subscribe(this::dispatchMessage);
        });
    }
}
