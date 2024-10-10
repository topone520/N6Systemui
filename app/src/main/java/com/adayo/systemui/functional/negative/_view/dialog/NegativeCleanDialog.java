package com.adayo.systemui.functional.negative._view.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adayo.soavb.foundation.view.AbstractBindViewDialog;
import com.adayo.systemui.utils.DebounceClickListener;
import com.adayo.systemui.utils.TimeDataUtils;
import com.android.systemui.R;

import java.util.Calendar;
import java.util.Locale;

public class NegativeCleanDialog extends AbstractBindViewDialog {
    View _fl_layout;
    private static final long CLICK_INTERVAL_TIME = 300;
    private static long lastClickTime = 0;
    private TextView _dialog_time, _dialog_week, _dialog_data;

    public NegativeCleanDialog(@NonNull
                               Context context) {
        super(context, R.style.TransparentStyle);
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();


        if (window != null) {
            // Set the gravity of the content view to top-center
            window.setGravity(Gravity.CENTER);

            // Set system UI visibility
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            WindowManager.LayoutParams params = window.getAttributes();
            params.type = 2071;
            params.width = 3700;
            params.height = 720;

            // Calculate the y-coordinate to position the dialog at the top, considering status bar height
            Rect rect = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            int statusBarHeight = rect.top;

            params.y = statusBarHeight; // Adjust this value based on your needs

            window.setAttributes(params);
        }

        _fl_layout = findViewById(R.id.fl_layout);

        _dialog_time = findViewById(R.id.dialog_time);
        _dialog_week = findViewById(R.id.dialog_week);
        _dialog_data = findViewById(R.id.dialog_data);
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        long unixTime = calendar.getTimeInMillis();
        _dialog_time.setText(TimeDataUtils.getHourAndMin(_dialog_time.getContext(),unixTime));
        _dialog_week.setText(TimeDataUtils.getWeekOfDate(calendar.getTime()));
        _dialog_data.setText(TimeDataUtils.getDataTime(_dialog_time.getContext(),unixTime));

        _fl_layout.setOnClickListener(v -> {
            long currentTimeMillis = SystemClock.uptimeMillis();
            if (currentTimeMillis - lastClickTime < CLICK_INTERVAL_TIME){
                dismiss();
                return;
            }
            lastClickTime = currentTimeMillis;
        });
    }

    @Override
    protected void createViewBinder() {

    }

    @Override
    protected int acquireResourceId() {
        return R.layout.dialog_negative_clean_layout;
    }
}
