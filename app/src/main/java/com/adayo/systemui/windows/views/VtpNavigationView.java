package com.adayo.systemui.windows.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.view.AbstractBindViewFramelayout;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.functional.hvac._binder.CirculateViewBinder;
import com.adayo.systemui.functional.hvac._binder.VtpShortcutTempAdjustViewBinder;
import com.adayo.systemui.functional.seat._binder.VtpSeatHeatViewBinder;
import com.adayo.systemui.manager.SourceControllerImpl;
import com.adayo.systemui.utils.DisplayUtils;
import com.adayo.systemui.utils.SeatResourceUtils;
import com.adayo.systemui.windows.panels.VtpHvacPanel;
import com.android.systemui.R;

import java.util.HashMap;


public class VtpNavigationView extends AbstractBindViewFramelayout implements View.OnClickListener {
    private final static String TAG = VtpNavigationView.class.getName();
    private ImageView _iv_bg_1, _iv_bg_2, _iv_bg_3, _iv_bg_4;

    public VtpNavigationView(@NonNull Context context) {
        super(context);
        _initialize_view();
    }

    @Override
    protected void createViewBinder() {
        insertViewBinder(new CirculateViewBinder());
        //温度选择
        insertViewBinder(VtpShortcutTempAdjustViewBinder.Builder.createLeft(R.id.hor_driver_temp));
        insertViewBinder(VtpShortcutTempAdjustViewBinder.Builder.createRight(R.id.hor_copilot_temp));

        //座椅加热
        insertViewBinder(VtpSeatHeatViewBinder.Builder.createLeft(R.id.iv_driver_heat, SeatResourceUtils.VTP_SHORT_LEFT_SEAT_RESOURCES_HEAT));
        insertViewBinder(VtpSeatHeatViewBinder.Builder.createRight(R.id.iv_copilot_heat, SeatResourceUtils.VTP_SHORT_RIGHT_SEAT_RESOURCES_HEAT));
    }

    @Override
    protected int acquireResourceId() {
        return R.layout.vtp_navigation_view_layout;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void _initialize_view() {
        setOnTouchListener((v, event) -> {
            VtpHvacPanel.getInstance().hideDialog();
            return false;
        });
        _iv_bg_1 = findViewById(R.id.iv_bg_1);
        _iv_bg_2 = findViewById(R.id.iv_bg_2);
        _iv_bg_3 = findViewById(R.id.iv_bg_3);
        _iv_bg_4 = findViewById(R.id.iv_bg_4);
        findViewById(R.id.iv_intent_vehicle).setOnClickListener(this);
        findViewById(R.id.iv_intent_setting).setOnClickListener(this);
        findViewById(R.id.iv_driver_heat).setOnClickListener(this);
        findViewById(R.id.hor_driver_temp).setOnClickListener(this);
        findViewById(R.id.iv_intent_hvac).setOnClickListener(this);
        findViewById(R.id.hor_copilot_temp).setOnClickListener(this);
        findViewById(R.id.iv_copilot_heat).setOnClickListener(this);
        findViewById(R.id.iv_voice).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        AAOP_LogUtils.d(TAG, "onclick ------");
        if (id != R.id.iv_intent_hvac && id != R.id.iv_driver_heat && id != R.id.iv_copilot_heat) {
            AAOP_LogUtils.d(TAG, "onClick: true -------------");
            VtpHvacPanel.getInstance().hideDialog();
        }
        if (id == R.id.iv_intent_vehicle) {
            AAOP_LogUtils.d(TAG, "车设车控首页");
            DisplayUtils.intentSettingHome();
            isShowBg(1);
        } else if (id == R.id.iv_intent_setting) {
            AAOP_LogUtils.d(TAG, "车设车控界面");
            DisplayUtils.intentSettingView();
            isShowBg(2);
        } else if (id == R.id.iv_driver_heat || id == R.id.iv_copilot_heat) {
            AAOP_LogUtils.d(TAG, "座椅界面");
            VtpHvacPanel.getInstance().showSeatLayout();
        } else if (id == R.id.iv_intent_hvac) {
            AAOP_LogUtils.d(TAG, "空调界面");
            isShowBg(3);
            VtpHvacPanel.getInstance().showHvacLayout();
        } else if (id == R.id.iv_voice) {
            DisplayUtils.intentSettingVoice();
            AAOP_LogUtils.d(TAG, "喇叭");
            isShowBg(4);
        }
    }

    private void isShowBg(int type) {
        //type 1：车控首页 2：车控界面 3：空调 4：喇叭
        _iv_bg_1.setVisibility(type == 1 ? VISIBLE : GONE);
        _iv_bg_2.setVisibility(type == 2 ? VISIBLE : GONE);
        _iv_bg_3.setVisibility(type == 3 ? VISIBLE : GONE);
        _iv_bg_4.setVisibility(type == 4 ? VISIBLE : GONE);
    }
}
