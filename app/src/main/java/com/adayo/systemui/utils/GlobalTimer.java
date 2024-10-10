package com.adayo.systemui.utils;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.android.systemui.SystemUIApplication;

public class GlobalTimer {

    private static GlobalTimer instance;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;

    private int accumulation;

    private GlobalTimer() {

    }

    public static synchronized GlobalTimer getInstance() {
        if (instance == null) {
            instance = new GlobalTimer();
        }
        return instance;
    }

    public void startTimer() {
        if (!isTimerRunning) {
            countDownTimer = new CountDownTimer(Long.MAX_VALUE, 60000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Log.d("TAG", "onTick:--- ");
                    // 每分钟记一次
                    accumulation++;
                }

                @Override
                public void onFinish() {
                }
            };

            countDownTimer.start();
            isTimerRunning = true;
        }
    }

    public void stopTimer() {
        if (isTimerRunning && countDownTimer != null) {
            countDownTimer.cancel();
            isTimerRunning = false;
        }
    }

    public boolean isTimerRunning() {
        return isTimerRunning;
    }

    public void statisticsTime() {
        //功能规范：下电状态进行数据计算
        try {
            int hour = accumulation % 60;
            if (hour == 0) return;
            Bundle bundleFilter = new Bundle();
            bundleFilter.putInt("value", hour);
            HvacService.getInstance().invoke(HvacSOAConstant.MSG_SET_FILTER_ELEMENT, bundleFilter, new ADSBusReturnValue());
            accumulation = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

