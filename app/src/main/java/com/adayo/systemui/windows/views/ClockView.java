package com.adayo.systemui.windows.views;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ClockView extends LinearLayout {

    private boolean mAttached = false;

    private TextView clockTime;
    private TextView clockTimeUnit;

    public ClockView(Context context) {
        this(context, null);
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        View mRootView = LayoutInflater.from(getContext()).inflate(R.layout.clock_view, this, true);
        clockTime = mRootView.findViewById(R.id.clock_time);
        clockTimeUnit = mRootView.findViewById(R.id.clock_time_unit);
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        LogUtil.debugI("onConfigurationChanged");
        updateTime();
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mAttached) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
            getContext().registerReceiver(receiver, filter);
            mAttached = true;
        }
        try {
            getHandler().post(() -> updateTime());
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void updateTime() {
        boolean is24 = DateFormat.is24HourFormat(getContext());
        String time;
        if (is24) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            time = sdf.format(new Date());
            clockTimeUnit.setVisibility(View.GONE);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm", Locale.getDefault());
            time = sdf.format(new Date());
            clockTimeUnit.setVisibility(View.VISIBLE);
        }
        if (Calendar.getInstance().get(Calendar.AM_PM) == 0) {
            clockTimeUnit.setText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.am));
        } else {
            clockTimeUnit.setText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.pm));
        }
        if (null != time && null != clockTime) {
            LogUtil.debugI("clockTime = " + clockTime);
            clockTime.setText(time);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            getContext().unregisterReceiver(receiver);
            mAttached = false;
        }
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_TIME_TICK:
                case Intent.ACTION_TIME_CHANGED:
                case Intent.ACTION_TIMEZONE_CHANGED:
                case Intent.ACTION_CONFIGURATION_CHANGED:
                    try {
                        getHandler().post(() -> updateTime());
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public void setIconsDark(int textColor) {
        if (null != clockTime) {
            clockTime.setTextColor(textColor);
        }
        if (null != clockTimeUnit) {
            clockTimeUnit.setTextColor(textColor);
        }
    }
}
