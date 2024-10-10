package com.adayo.systemui.manager;

import android.os.Handler;
import android.util.Log;

public class TimerManager {

    private final String TAG = TimerManager.class.getSimpleName();

    public interface TimerCallback {
        void onTimerComplete();
    }

    private final int durations = 900;
    private Handler handler = new Handler();
    private TimerCallback callback;

    public TimerManager(TimerCallback callback) {
        this.callback = callback;
    }

    public void startTimers() {
        startTimer();
    }

    public boolean isStart;

    private void startTimer() {
        if (isStart) return;
        isStart = true;
        handler.postDelayed(() -> callback.onTimerComplete(), durations * 1000);
    }

    public void stopTimers() {
        isStart = false;
        handler.removeCallbacksAndMessages(null);
    }
}


