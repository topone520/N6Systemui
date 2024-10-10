package com.adayo.systemui.windows.panels;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.service.SoaService;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.contents.HvacSingleton;
import com.adayo.systemui.functional.fragrance._view.FragranceViewFragment;
import com.adayo.systemui.functional.fragrance._view.FragranceViewGroup;
import com.adayo.systemui.interfaces.HvacLayoutSwitchListener;
import com.adayo.systemui.proxy.vehicle.BattSocService;
import com.adayo.systemui.proxy.vehicle.Dimensional;
import com.adayo.systemui.proxy.vehicle.FragranceService;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.proxy.vehicle.PowerService;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.proxy.vehicle.VehicleDoorService;
import com.adayo.systemui.proxy.vehicle.VehicleDriverService;
import com.adayo.systemui.proxy.vehicle.VehicleWindowService;
import com.adayo.systemui.proxy.vehicle.WarningService;
import com.adayo.systemui.utils.CarTypeUtils;
import com.adayo.systemui.utils.GlobalTimer;
import com.adayo.systemui.utils.HvacChildViewAnimation;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.functional.seat._view.SeatViewFragment;
import com.adayo.systemui.windows.views.MyScrollerView;
import com.adayo.systemui.functional.hvac._view.HvacViewGroup;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

/**
 * @author XuYue
 * @description:
 * @date :2021/11/3 10:59
 */
public class HvacPanel implements HvacLayoutSwitchListener {

    private final String TAG = HvacPanel.class.getSimpleName();
    private volatile static HvacPanel hvacPanel;
    private WindowManager.LayoutParams layoutParams;
    private ConstraintLayout mFloatLayout;
    private WindowManager mWindowManager;
    private MyScrollerView hvacScrollLayout;

    private boolean isAdded = false;
    private HvacViewGroup hvacView;
    private SeatViewFragment seatView;
    private FragranceViewGroup fragranceView;

    public static HvacPanel getInstance() {
        if (hvacPanel == null) {
            synchronized (HvacPanel.class) {
                if (hvacPanel == null) {
                    hvacPanel = new HvacPanel();
                }
            }
        }
        return hvacPanel;
    }

    private HvacPanel() {
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        initView();
    }

    private int cardType = 0;

    private void initView() {
        AAOP_LogUtils.d(TAG, "hvacPanel initView ------111");
        layoutParams = new WindowManager.LayoutParams(2072);
        layoutParams.width = 3840;
        layoutParams.height = 720;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR | WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
//        layoutParams.systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
//        layoutParams.systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
//        layoutParams.systemUiVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        layoutParams.systemUiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        layoutParams.setTitle(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.wifi_fail_hvacpanel));
        layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
        layoutParams.gravity = Gravity.END | Gravity.TOP;
        layoutParams.format = PixelFormat.TRANSLUCENT;

        mFloatLayout = (ConstraintLayout) LayoutInflater.from(SystemUIApplication.getSystemUIContext()).inflate(R.layout.hvac_panel_layout, null);
        hvacScrollLayout = mFloatLayout.findViewById(R.id.hvac_scroll_layout);

        hvacView = mFloatLayout.findViewById(R.id.hvac_view);
        seatView = mFloatLayout.findViewById(R.id.seat_view);
        fragranceView = mFloatLayout.findViewById(R.id.fragrance_view);

        CarTypeUtils.CarType carType = CarTypeUtils.getCarType();
        if (carType == CarTypeUtils.CarType.N60) {
            cardType = 0;
            hvacView.setBackgroundResource(R.mipmap.vtp_ac_bg);
        } else if (carType == CarTypeUtils.CarType.N61) {
            cardType = 1;
            hvacView.setBackgroundResource(R.mipmap.ivi_ac_bg);
        }
        hvacView.setListener(this);
        seatView.setListener(this);
        fragranceView.setListener(this);

        if (!isAdded && null != mWindowManager && null != mFloatLayout) {
            mWindowManager.addView(mFloatLayout, layoutParams);
            mFloatLayout.setVisibility(View.GONE);
            isAdded = true;
        }

        AAOP_LogUtils.d(TAG, "register service ------111");
        SeatService.getInstance().connect("HvacPanel_SEAT", service -> {
            AAOP_LogUtils.i(TAG, "Seat::onConnected()" + service.acquireName());
            service.subscribe(seatView::dispatchMessage);
        });

        HvacService.getInstance().connect("HvacPanel_HVAC", service -> {
            AAOP_LogUtils.i(TAG, "Hvac::onConnected()" + service.acquireName());
            service.subscribe(hvacView::dispatchMessage);
        });

        FragranceService.getInstance().connect("HvacPanel_FRAGRANCE", service -> {
            AAOP_LogUtils.i(TAG, "Fragrance::onConnected()" + service.acquireName());
            service.subscribe(fragranceView::dispatchMessage);
        });

        PowerService.getInstance().connect("HvacPanel_POWER", service -> {
            AAOP_LogUtils.i(TAG, "Power::onConnected()" + service.acquireName());
            service.subscribe(bundle -> {
                hvacView.dispatchMessage(bundle);
                //判断当前电源状态
                if (bundle.getString("message_id", "node").equals(HvacSOAConstant.MSG_EVENT_POWER_VALUE)) {
                    if (bundle.getInt("value", -1) != HvacSOAConstant.HVAC_POWER_MODE_IGN_ON) {
                        //电源状态不是ON
                        GlobalTimer.getInstance().stopTimer();
                        GlobalTimer.getInstance().statisticsTime();//结算
                    }
                }

            });
        });
        VehicleDriverService.getInstance().connect("HvacPanel_SN_DRIVER", service -> {
            AAOP_LogUtils.i(TAG, "SN_DRIVER::onConnected()" + service.acquireName());
            service.subscribe(hvacView::dispatchMessage);
        });
        VehicleDoorService.getInstance().connect("HvacPanel_SN_DOOR", service -> {
            AAOP_LogUtils.i(TAG, "SN_DOOR::onConnected()" + service.acquireName());
            service.subscribe(hvacView::dispatchMessage);
        });
        VehicleWindowService.getInstance().connect("HvacPanel_SN_WINDOW", service -> {
            AAOP_LogUtils.i(TAG, "SN_WINDOW::onConnected()" + service.acquireName());
             service.subscribe(hvacView::dispatchMessage);
        });
//        //剩余电量蓄电池
//        BattSocService.getInstance().connect("HvacPanel_SN_LANTERNDANCE", service -> {
//            AAOP_LogUtils.i(TAG, "SN_WINDOW::onConnected()" + service.acquireName());
//            service.subscribe(seatView::dispatchMessage);
//        });
        //报警等级
        WarningService.getInstance().connect("HvacPanel_SN_WARNING", service -> {
            AAOP_LogUtils.i(TAG, "SN_WARNING::onConnected()" + service.acquireName());
            service.subscribe(seatView::dispatchMessage);
        });


    }

    public boolean show(@NonNull MotionEvent event) {
        boolean fromOutside = false;
        if (mFloatLayout.getVisibility() != View.VISIBLE) {
            AAOP_LogUtils.d(TAG, "--------------------- show");
            show();
        }
        fromOutside = true;
        LogUtil.debugD("fromOutSide == " + fromOutside + ", getRawY() = " + event.getRawY());
        //最外层滑动
        hvacScrollLayout.onOutsideTouchEvent(event);
        HvacSingleton.getInstance().setHvacHomeShow(false);
        return true;
    }

    public boolean showEvent(@NonNull MotionEvent event) {
        //最外层滑动
        hvacScrollLayout.onOutsideTouchEvent(event);
        HvacSingleton.getInstance().setHvacHomeShow(false);
        return true;
    }
    public boolean closeEvent(@NonNull MotionEvent event) {
        //最外层滑动
        hvacScrollLayout.onCloseTouchEvent(event);
        HvacSingleton.getInstance().setHvacHomeShow(true);
        return true;
    }

    private void show() {
        if (null != mFloatLayout) {
            mFloatLayout.setVisibility(View.VISIBLE);
        }
    }

    public void hvacScrollLayoutDismiss() {
        Log.d(TAG, "hvacScrollLayoutDismiss: ");
        if (null == hvacScrollLayout) return;
        if (hvacScrollLayout.getVisibility() == View.VISIBLE) {
            HvacSingleton.getInstance().setHvacLayout(false);
            hvacScrollLayout.doAutomaticAnim(false);
            HvacSingleton.getInstance().setHvacHomeShow(true);
        }
    }

    public void hvacScrollLayoutShow() {
        Log.d(TAG, "hvacScrollLayoutShow: ");
        if (null == hvacScrollLayout) return;
        if (hvacScrollLayout.getVisibility() == View.GONE) {
            hvacScrollLayout.setVisibility(View.VISIBLE);
            HvacSingleton.getInstance().setHvacLayout(true);
            hvacScrollLayout.doAutomaticAnim(true);
            HvacSingleton.getInstance().setHvacHomeShow(false);
        }
    }

    public void ccpOpenHvacLayout() {
        Log.d(TAG, "hvacScrollLayoutShow: ");
        if (null == hvacScrollLayout) return;
        if (hvacScrollLayout.getVisibility() == View.GONE) {
            hvacScrollLayout.setVisibility(View.VISIBLE);
            hvacScrollLayout.doAutomaticAnim(true);
            onHvacLayout();
        }
    }

    public void dismiss() {
        if (null != mFloatLayout) {
            mFloatLayout.setVisibility(View.GONE);
            HvacSingleton.getInstance().setHvacHomeShow(true);
        }
        // windowsController.notifyVisibility(TYPE_OF_HVAC_PANEL, View.GONE);
    }

    @Override
    public void onHvacLayout() {
        //显示空调界面
        HvacChildViewAnimation.performFadeOutAnimation(seatView);
        HvacChildViewAnimation.performFadeOutAnimation(fragranceView);
        HvacChildViewAnimation.fadeInView(hvacView);
        if (mFloatLayout != null && mFloatLayout.getVisibility() == View.VISIBLE) {
            HvacSingleton.getInstance().setHvacLayout(true);
        }
    }

    @Override
    public void onSeatLayout() {
        //显示座椅
        HvacChildViewAnimation.performFadeOutAnimation(seatView);
        HvacChildViewAnimation.performFadeOutAnimation(hvacView);
        HvacChildViewAnimation.fadeInView(seatView);
        HvacSingleton.getInstance().setHvacLayout(false);
    }

    @Override
    public void onFragranceLayout() {
        //显示香氛
        HvacChildViewAnimation.performFadeOutAnimation(seatView);
        HvacChildViewAnimation.performFadeOutAnimation(hvacView);
        HvacChildViewAnimation.fadeInView(fragranceView);
        HvacSingleton.getInstance().setHvacLayout(false);
    }
}
