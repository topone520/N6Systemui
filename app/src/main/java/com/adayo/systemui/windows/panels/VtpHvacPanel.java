package com.adayo.systemui.windows.panels;

import android.app.Presentation;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.service.SoaService;
import com.adayo.systemui.functional.fragrance._view.VtpFragranceViewFragment;
import com.adayo.systemui.functional.hvac._view.VtpHvacViewGroup;
import com.adayo.systemui.functional.seat._view.VtpSeatViewFragment;
import com.adayo.systemui.interfaces.HvacLayoutSwitchListener;
import com.adayo.systemui.proxy.vehicle.FragranceService;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.utils.DisplayUtils;
import com.adayo.systemui.utils.HvacChildViewAnimation;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class VtpHvacPanel extends Presentation implements HvacLayoutSwitchListener {

    private final String TAG = VtpHvacPanel.class.getSimpleName();
    private static VtpHvacPanel instance;
    private ConstraintLayout mFloatLayout;
    private VtpHvacViewGroup hvacView;
    private VtpSeatViewFragment seatView;
    private VtpFragranceViewFragment fragranceView;

    private VtpHvacPanel(Context outerContext, Display display) {
        super(outerContext, display);
    }

    private void _register_service() {
        AAOP_LogUtils.d(TAG, "_register_service dialog - hvacPanel ---->");
        SeatService.getInstance().connect("VtpHvacPanel_SEAT", service -> {
            AAOP_LogUtils.i(TAG, "Seat::onConnected()" + service.acquireName());
            service.subscribe(bundle -> {
                if (null == seatView) return;
                seatView.dispatchMessage(bundle);
            });
        });

        HvacService.getInstance().connect("VtpHvacPanel_HVAC", service -> {
            AAOP_LogUtils.i(TAG, "Hvac::onConnected()" + service.acquireName());
            service.subscribe(bundle -> {
                if (null == hvacView) return;
                hvacView.dispatchMessage(bundle);
            });
        });

        FragranceService.getInstance().connect("VtpHvacPanel_FRAGRANCE", service -> {
            AAOP_LogUtils.i(TAG, "Fragrance::onConnected()" + service.acquireName());
            service.subscribe(bundle -> {
                if (null == fragranceView) return;
                fragranceView.dispatchMessage(bundle);
            });
        });
    }

    public static VtpHvacPanel getInstance() {
        if (instance == null) {
            instance = new VtpHvacPanel(SystemUIApplication.getSystemUIContext(), DisplayUtils.getDisplay());
        }
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        window.setGravity(Gravity.TOP);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, 960);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.getAttributes().windowAnimations = R.style.DialogAnimationSlideUp;
        mFloatLayout = (ConstraintLayout) AAOP_HSkin.getLayoutInflater(getContext()).inflate(R.layout.vtp_hvac_panel_layout, null);
        setContentView(mFloatLayout);
        hvacView = mFloatLayout.findViewById(R.id.hvac_view);
        seatView = mFloatLayout.findViewById(R.id.seat_view);
        fragranceView = mFloatLayout.findViewById(R.id.fragrance_view);
        AAOP_LogUtils.d(TAG, "protected void onCreate dialog - hvacPanel ---->");
        _register_service();

        hvacView.setListener(this);
        seatView.setListener(this);
        fragranceView.setListener(this);
    }

    public void showHvacLayout() {
        showDialog();
        onHvacLayout();
    }

    public void showSeatLayout() {
        showDialog();
        onSeatLayout();
    }

    @Override
    public void onHvacLayout() {
        HvacChildViewAnimation.performFadeOutAnimation(seatView);
        HvacChildViewAnimation.performFadeOutAnimation(fragranceView);
        HvacChildViewAnimation.fadeInView(hvacView);
    }

    @Override
    public void onSeatLayout() {
        HvacChildViewAnimation.performFadeOutAnimation(hvacView);
        HvacChildViewAnimation.performFadeOutAnimation(fragranceView);
        HvacChildViewAnimation.fadeInView(seatView);
    }

    @Override
    public void onFragranceLayout() {
        HvacChildViewAnimation.performFadeOutAnimation(seatView);
        HvacChildViewAnimation.performFadeOutAnimation(hvacView);
        HvacChildViewAnimation.fadeInView(fragranceView);
    }

    // 添加方法来显示和隐藏
    public void showDialog() {
        AAOP_LogUtils.d(TAG, "show --------------->");
        if (isShowing()) return;
        show();
    }

    public void hideDialog() {
        if (!isShowing()) return;
        AAOP_LogUtils.d(TAG, "hide ---------------->");
        dismiss();
    }
}

