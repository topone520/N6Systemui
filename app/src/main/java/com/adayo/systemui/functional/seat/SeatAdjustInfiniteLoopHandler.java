package com.adayo.systemui.functional.seat;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class SeatAdjustInfiniteLoopHandler {

    private static final long LOOP_INTERVAL = 200; // 循环间隔时间，单位毫秒

    public interface LoopCallback {
        void onLoop();
    }

    private Handler handler;
    private boolean isLooping = false;
    private LoopCallback loopCallback;

    public SeatAdjustInfiniteLoopHandler(LoopCallback callback) {
        this.loopCallback = callback;

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 0) {
                    if (isLooping && loopCallback != null) {
                        loopCallback.onLoop();
                        sendEmptyMessageDelayed(0, LOOP_INTERVAL);
                        Log.d("TAG", "handleMessage11111: ");
                    }
                }
            }
        };
    }

    public void startLoop() {
        if (!isLooping) {
            isLooping = true;
            handler.sendEmptyMessage(0);
        }
    }

    public void stopLoop() {
        if (isLooping) {
            isLooping = false;
            handler.removeMessages(0);
        }
    }

    public void setLoopCallback(LoopCallback loopCallback) {
        this.loopCallback = loopCallback;
    }
}

