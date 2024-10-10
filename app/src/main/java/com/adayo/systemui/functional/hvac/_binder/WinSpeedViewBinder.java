package com.adayo.systemui.functional.hvac._binder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusErrorCodeEnum;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.proxy.vehicle.Dimensional;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.utils.GlobalTimer;
import com.adayo.systemui.utils.SPUtils;
import com.adayo.systemui.windows.views.hvac.HvacWinSpeedAdjustView;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class WinSpeedViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = WinSpeedViewBinder.class.getSimpleName();
    private HvacWinSpeedAdjustView _win_speed_adjust_view;

    public WinSpeedViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(HvacService.getInstance())
                .withGetMessageName(HvacSOAConstant.MSG_GET_FAN_SPEED_VALUE)
                .withSetMessageName(HvacSOAConstant.MSG_SET_FAN_SPEED_VALUE_ACTION)
                .withEventMessageName(HvacSOAConstant.MSG_EVENT_FAN_SPEED)
                .withInitialValue(0)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        _win_speed_adjust_view = view.findViewById(R.id.win_speed_adjust_view);
        _win_speed_adjust_view.setLevelListener(new HvacWinSpeedAdjustView.WinSpeedLevelListener() {
            @Override
            public void onCurrentSpeed(int level) {
                final Bundle _bundle = new Bundle();
                _bundle.putInt("action", AreaConstant.HVAC_TEMP_ACTION_SPECIFIC); //具体值
                _bundle.putInt("value", level);
                boolean resultWinSpeed = HvacService.getInstance().invoke(HvacSOAConstant.MSG_SET_FAN_SPEED_VALUE_ACTION, _bundle, new ADSBusReturnValue());
                AAOP_LogUtils.d(TAG, "windSpeed: " + resultWinSpeed + "  level = " + level);
                ReportBcmManager.getInstance().sendHvacReport("8630010", "wind_speed_setting", level);
            }

            @Override
            public void onClickStopGet() {
                AAOP_LogUtils.d(TAG, "2000 ms ----- get");
                ADSBusReturnValue value = new ADSBusReturnValue();
                HvacService.getInstance().invoke(HvacSOAConstant.MSG_GET_FAN_SPEED_VALUE, new Bundle(), value);
                _update_ui(value.getmReturnValue().getInt("value"));
            }
        });
    }

    @Override
    protected void _update_ui(Integer value) {
        AAOP_LogUtils.d(TAG, "value = " + value);
        _win_speed_adjust_view._update_ui(value);
        Dimensional.getInstance().doAction("DataSource.Model.HvacFanSpeed", value + "");
        if (value == AreaConstant.HVAC_OFF_IO) {
            //风速0，取消计算计时器 滤芯寿命
            GlobalTimer.getInstance().stopTimer();
            GlobalTimer.getInstance().statisticsTime();
        } else {
            GlobalTimer.getInstance().startTimer();
        }
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, "bundle = " + bundle.toString());
    }
}
