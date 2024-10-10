package com.adayo.systemui.utils;

import android.os.SystemClock;
import android.view.View;

public class DebounceClickListener implements View.OnClickListener {
    private static final long DEFAULT_DEBOUNCE_INTERVAL = 200; // 默认的点击间隔时间

    private long lastClickTime;
    private long debounceInterval;
    private View.OnClickListener clickListener;

    public DebounceClickListener(View.OnClickListener clickListener) {
        this(clickListener, DEFAULT_DEBOUNCE_INTERVAL);
    }

    public DebounceClickListener(View.OnClickListener clickListener, long debounceInterval) {
        this.clickListener = clickListener;
        this.debounceInterval = debounceInterval;
    }

    @Override
    public void onClick(View view) {
        long currentTime = SystemClock.elapsedRealtime();
        if (currentTime - lastClickTime >= debounceInterval) {
            lastClickTime = currentTime;
            if (clickListener != null) {
                clickListener.onClick(view);
            }
        }
    }
}
