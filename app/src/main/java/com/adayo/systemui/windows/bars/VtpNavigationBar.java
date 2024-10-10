package com.adayo.systemui.windows.bars;

import android.app.Presentation;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.proxy.vehicle.FragranceService;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.utils.DisplayUtils;
import com.adayo.systemui.windows.views.VtpNavigationView;
import com.android.systemui.SystemUIApplication;

public class VtpNavigationBar extends Presentation {
    private final static String TAG = VtpNavigationBar.class.getName();

    public VtpNavigationBar(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayUtils.intentSettingHome();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, 120);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        VtpNavigationView vtpNavigationView = new VtpNavigationView(SystemUIApplication.getSystemUIContext());
        setContentView(vtpNavigationView);
        _register_service(vtpNavigationView);
    }

    private void _register_service(VtpNavigationView vtpNavigationView) {
        SeatService.getInstance().connect("VtpVtpNavigationBar_SEAT", service -> {
            AAOP_LogUtils.i(TAG, "Seat::onConnected()" + service.acquireName());
            service.subscribe(vtpNavigationView::dispatchMessage);
        });

        HvacService.getInstance().connect("VtpVtpNavigationBar_HVAC", service -> {
            AAOP_LogUtils.i(TAG, "Hvac::onConnected()" + service.acquireName());
            service.subscribe(vtpNavigationView::dispatchMessage);
        });
    }
}
