package com.adayo.systemui.functional.hvac._view;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.soavb.foundation.view.AbstractBindViewFramelayout;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.functional.hvac._binder.ACViewBinder;
import com.adayo.systemui.functional.hvac._binder.AUTOViewBinder;
import com.adayo.systemui.functional.hvac._binder.DefrostViewBinder;
import com.adayo.systemui.functional.hvac._binder.DirectViewBinder;
import com.adayo.systemui.functional.hvac._binder.ECOViewBinder;
import com.adayo.systemui.functional.hvac._binder.FaceViewBinder;
import com.adayo.systemui.functional.hvac._binder.FeetViewBinder;
import com.adayo.systemui.functional.hvac._binder.FrontDefrostViewBinder;
import com.adayo.systemui.functional.hvac._binder.HvacSwitchViewBinder;
import com.adayo.systemui.functional.hvac._binder.MDVAdjustViewBinder;
import com.adayo.systemui.functional.hvac._binder.SYNCViewBinder;
import com.adayo.systemui.functional.hvac._binder.TempAdjustViewBinder;
import com.adayo.systemui.functional.hvac._binder.WinSpeedViewBinder;
import com.adayo.systemui.functional.hvac._view.dialog.VtpBackBlowingModeDialogBindView;
import com.adayo.systemui.functional.hvac._view.dialog.VtpForestDialogBindView;
import com.adayo.systemui.functional.hvac._view.dialog.VtpIntelligentAirDialogBindView;
import com.adayo.systemui.interfaces.HvacLayoutSwitchListener;
import com.android.systemui.R;

public class VtpHvacViewGroup extends AbstractBindViewFramelayout implements MDVAdjustViewBinder.MDVAdjustListener {
    private final String TAG = VtpHvacViewGroup.class.getSimpleName();
    private HvacLayoutSwitchListener listener;
    private VtpBackBlowingModeDialogBindView _blowing_mode_dialog;
    private VtpIntelligentAirDialogBindView _intelligent_air_dialog;

    public void setListener(HvacLayoutSwitchListener listener) {
        this.listener = listener;
    }

    public VtpHvacViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "HvacViewGroup: ---------------------------------------------");
        _init_view();
    }

    @Override
    protected void createViewBinder() {
        Log.d(TAG, "createViewBinder: =============================================");
        _init_dialog_layout();
        insertViewBinder(TempAdjustViewBinder.Builder.createLeft(R.id.driver_temp_select));
        insertViewBinder(TempAdjustViewBinder.Builder.createRight(R.id.copilot_temp_select));
        insertViewBinder(new ACViewBinder());
        insertViewBinder(new AUTOViewBinder());
        insertViewBinder(FaceViewBinder.Builder.createLeft(R.id.cb_wind_driver_face));
        insertViewBinder(FaceViewBinder.Builder.createRight(R.id.cb_wind_co_face));
        insertViewBinder(FeetViewBinder.Builder.createLeft(R.id.cb_wind_driver_feet));
        insertViewBinder(FeetViewBinder.Builder.createRight(R.id.cb_wind_co_feet));
        insertViewBinder(DefrostViewBinder.Builder.createLeft());
       // insertViewBinder(new CirculateViewBinder());
        insertViewBinder(new DirectViewBinder(AreaConstant.BAIC_LEFT_FRONT_2, R.id.tv_driver_blow));
        insertViewBinder(new DirectViewBinder(AreaConstant.BAIC_RIGHT_FRONT_2, R.id.tv_copilot_blow));
        insertViewBinder(new ECOViewBinder());
        insertViewBinder(new FrontDefrostViewBinder());
        insertViewBinder(new HvacSwitchViewBinder());
        insertViewBinder(new SYNCViewBinder());
        insertViewBinder(new WinSpeedViewBinder());
        //MDV调节
//        MDVAdjustViewBinder _mdv_left_adjust = new MDVAdjustViewBinder(AreaConstant.BAIC_LEFT_LEFT_FRONT_2, R.id.mdv_left_adjust);
//        _mdv_left_adjust.set_listener(this);
//        insertViewBinder(_mdv_left_adjust);
//        MDVAdjustViewBinder _mdv_left_center_adjust = new MDVAdjustViewBinder(AreaConstant.BAIC_LEFT_MID_FRONT_2, R.id.mdv_left_center_adjust);
//        _mdv_left_center_adjust.set_listener(this);
//        insertViewBinder(_mdv_left_center_adjust);
//        MDVAdjustViewBinder _mdv_right_center_adjust = new MDVAdjustViewBinder(AreaConstant.BAIC_RIGHT_MID_FRONT_2, R.id.mdv_right_center_adjust);
//        _mdv_right_center_adjust.set_listener(this);
//        insertViewBinder(_mdv_right_center_adjust);
//        MDVAdjustViewBinder _mdv_right_adjust = new MDVAdjustViewBinder(AreaConstant.BAIC_RIGHT_RIGHT_FRONT_2, R.id.mdv_right_adjust);
//        _mdv_right_adjust.set_listener(this);
//        insertViewBinder(_mdv_right_adjust);
    }


    @Override
    protected int acquireResourceId() {
        return R.layout.vtp_hvac_view_layout;
    }

    private void _init_dialog_layout() {
        //后排空调
        _blowing_mode_dialog = new VtpBackBlowingModeDialogBindView(getContext(),1000,440);
        //智能空调
        _intelligent_air_dialog = new VtpIntelligentAirDialogBindView(getContext(), 1600, 440);
    }

    private void _init_view() {
//        _tv_seat = findViewById(R.id.tv_hvac_seat);
//        _tv_fragrance = findViewById(R.id.tv_hvac_fragrance);
//        _driver_temp_select = findViewById(R.id.driver_temp_select);
//        _copilot_temp_select = findViewById(R.id.copilot_temp_select);
//        _ll_main_wind_direction = findViewById(R.id.ll_main_wind_direction);
//        _ll_center_move = findViewById(R.id.ll_center_move);
//        _ll_co_wind_direction = findViewById(R.id.ll_co_wind_direction);
//        _mdv_show_view = findViewById(R.id.mdv_show_view);
        findViewById(R.id.tv_hvac_seat).setOnClickListener(v -> listener.onSeatLayout());
        findViewById(R.id.tv_hvac_fragrance).setOnClickListener(v -> listener.onFragranceLayout());
        findViewById(R.id.iv_air_protect).setOnClickListener(v -> new VtpForestDialogBindView(getContext(),1720,860).show());
        findViewById(R.id.tv_intelligent).setOnClickListener(v -> _intelligent_air_dialog.show());
        findViewById(R.id.tv_back_row).setOnClickListener(v -> _blowing_mode_dialog.show());
    }

    @Override
    public void isShowMDVView(boolean is_show) {
//        AAOP_LogUtils.d(TAG, "MDV Adjust ->" + is_show);
//        _tv_seat.setVisibility(is_show ? GONE : VISIBLE);
//        _tv_fragrance.setVisibility(is_show ? GONE : VISIBLE);
//        _driver_temp_select.setVisibility(is_show ? GONE : VISIBLE);
//        _copilot_temp_select.setVisibility(is_show ? GONE : VISIBLE);
//        _ll_main_wind_direction.setVisibility(is_show ? GONE : VISIBLE);
//        _ll_center_move.setVisibility(is_show ? GONE : VISIBLE);
//        _ll_co_wind_direction.setVisibility(is_show ? GONE : VISIBLE);
//        _mdv_show_view.setVisibility(is_show ? VISIBLE : GONE);
    }

    @Override
    public void dispatchMessage(Bundle bundle) {
        super.dispatchMessage(bundle);
        _blowing_mode_dialog.dispatchMessage(bundle);
        _intelligent_air_dialog.dispatchMessage(bundle);
    }
}
