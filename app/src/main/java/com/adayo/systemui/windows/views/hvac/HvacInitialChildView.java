package com.adayo.systemui.windows.views.hvac;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.adayo.systemui.interfaces.HvacChangeLayout;
import com.adayo.systemui.utils.WindowsUtils;
import com.android.systemui.R;

public class HvacInitialChildView extends FrameLayout {
    private final String TAG = HvacInitialChildView.class.getSimpleName();

    private Context context;
    private ImageView imgIcon;

    public HvacInitialChildView(@NonNull Context context) {
        super(context);
        this.context = context;
        initView();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        View inflate = LayoutInflater.from(context).inflate(R.layout.hvac_initill_layout, null);
        addView(inflate);
        setOnTouchListener((view, motionEvent) -> {
            Log.d(TAG, "onTouch: -----------");
            WindowsUtils.showHvacPanel(motionEvent);
            return true;
        });
        imgIcon = findViewById(R.id.img_icon);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOnTouchListener((v, event) -> {
            WindowsUtils.showHvacPanel(event);
            return true;
        });
    }

    public void ivStatus(int status) {
        if (imgIcon == null) return;
        imgIcon.setVisibility(status);
    }

}
