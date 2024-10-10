package com.adayo.systemui.utils;

import android.os.Handler;

import com.adayo.systemui.contents.AreaConstant;

import org.greenrobot.eventbus.EventBus;

public class RapidTimeUtils {
    public static final int TIME = 183;
    static Handler handler = new Handler();

    public static void startTimer() {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> EventBus.getDefault().post(AreaConstant.HVAC_OFF_IO), TIME * 1000);
    }

    public static void stopTimer() {
        handler.removeCallbacksAndMessages(null);
    }
}
