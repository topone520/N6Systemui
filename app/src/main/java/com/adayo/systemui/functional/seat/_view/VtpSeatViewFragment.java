package com.adayo.systemui.functional.seat._view;


import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.view.AbstractBindViewFramelayout;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.functional.seat._binder.SeatAllCloseViewBinder;
import com.adayo.systemui.functional.seat._binder.VtpSeatHeatGrepViewBinder;
import com.adayo.systemui.functional.seat._binder.VtpSeatHeatViewBinder;
import com.adayo.systemui.functional.seat._binder.VtpSeatMassageLevelViewBinder;
import com.adayo.systemui.functional.seat._binder.VtpSeatMassageModeShowViewBinder;
import com.adayo.systemui.functional.seat._binder.VtpSeatVentilateViewBinder;
import com.adayo.systemui.functional.seat._binder.VtpSteeringViewBinder;
import com.adayo.systemui.functional.seat._view.dialog.VtpBackRowSeatDialogBindView;
import com.adayo.systemui.functional.seat._view.dialog.VtpFrontRowSeatAdjustDialogBindView;
import com.adayo.systemui.functional.seat._view.dialog.VtpSeatMassageDialogBindView;
import com.adayo.systemui.interfaces.HvacLayoutSwitchListener;
import com.adayo.systemui.utils.SeatResourceUtils;
import com.adayo.systemui.windows.views.seat.VtpSeatHeatImageView;
import com.adayo.systemui.windows.views.seat.VtpSeatMassageImageView;
import com.adayo.systemui.windows.views.seat.VtpSeatVentilateImageView;
import com.android.systemui.R;

public class VtpSeatViewFragment extends AbstractBindViewFramelayout {
    private static final String TAG = VtpSeatViewFragment.class.getName();
    private HvacLayoutSwitchListener listener;
    private boolean _is_front_back = true;

    public void setListener(HvacLayoutSwitchListener listener) {
        this.listener = listener;
    }

    public VtpSeatViewFragment(@NonNull Context context) {
        super(context);
        _init_view();
    }

    public VtpSeatViewFragment(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _init_view();
    }

    @Override
    protected void createViewBinder() {
        _init_dialog_layout();
        //方向盘加热
        insertViewBinder(new VtpSteeringViewBinder());
        //座椅加热
        super.insertViewBinder(VtpSeatHeatViewBinder.Builder.createLeft(R.id.re_left_front_heat, SeatResourceUtils.VTP_SEAT_RESOURCES_HEAT));
        super.insertViewBinder(VtpSeatHeatViewBinder.Builder.createRight(R.id.re_right_front_heat, SeatResourceUtils.VTP_SEAT_RESOURCES_HEAT));
        super.insertViewBinder(VtpSeatHeatViewBinder.Builder.createLeftArea(R.id.re_left_area_heat, SeatResourceUtils.VTP_SEAT_RESOURCES_HEAT));
        super.insertViewBinder(VtpSeatHeatViewBinder.Builder.createRightArea(R.id.re_right_area_heat, SeatResourceUtils.VTP_SEAT_RESOURCES_HEAT));

        //座椅通风
        super.insertViewBinder(new VtpSeatVentilateViewBinder(SeatSOAConstant.LEFT_FRONT, R.id.re_left_front_ventilate));
        super.insertViewBinder(new VtpSeatVentilateViewBinder(SeatSOAConstant.RIGHT_FRONT, R.id.re_right_front_ventilate));

        //座椅按摩
        super.insertViewBinder(new VtpSeatMassageLevelViewBinder(SeatSOAConstant.LEFT_FRONT, R.id.iv_left_front_massage));
        super.insertViewBinder(new VtpSeatMassageLevelViewBinder(SeatSOAConstant.RIGHT_FRONT, R.id.iv_right_front_massage));
        super.insertViewBinder(new VtpSeatMassageLevelViewBinder(SeatSOAConstant.LEFT_AREA, R.id.iv_left_area_massage));
        super.insertViewBinder(new VtpSeatMassageLevelViewBinder(SeatSOAConstant.RIGHT_AREA, R.id.iv_right_area_massage));

        //座椅按摩模式
        super.insertViewBinder(new VtpSeatMassageModeShowViewBinder(SeatSOAConstant.LEFT_FRONT, R.id.tv_left_front_massage_mode, _left_front_massage_dialog));
        super.insertViewBinder(new VtpSeatMassageModeShowViewBinder(SeatSOAConstant.RIGHT_FRONT, R.id.tv_right_front_massage_mode, _right_front_massage_dialog));
        super.insertViewBinder(new VtpSeatMassageModeShowViewBinder(SeatSOAConstant.LEFT_AREA, R.id.tv_left_area_massage_mode, _left_area_massage_dialog));
        super.insertViewBinder(new VtpSeatMassageModeShowViewBinder(SeatSOAConstant.RIGHT_AREA, R.id.tv_right_area_massage_mode, _right_area_massage_dialog));

        //加热自动换挡
        super.insertViewBinder(new VtpSeatHeatGrepViewBinder());

        super.insertViewBinder(new SeatAllCloseViewBinder(R.id.iv_all_front_close, SeatSOAConstant.FRONT_ROW));
        super.insertViewBinder(new SeatAllCloseViewBinder(R.id.iv_all_area_close, SeatSOAConstant.BACK_ROW));

    }

    private VtpBackRowSeatDialogBindView _back_row_adjust_dialog;
    private VtpFrontRowSeatAdjustDialogBindView _front_row_adjust_dialog;
    private VtpSeatMassageDialogBindView _left_front_massage_dialog, _left_area_massage_dialog, _right_front_massage_dialog, _right_area_massage_dialog;

    private void _init_dialog_layout() {
        _back_row_adjust_dialog = new VtpBackRowSeatDialogBindView(getContext(), 1172, 670);
        _front_row_adjust_dialog = new VtpFrontRowSeatAdjustDialogBindView(getContext(), 1776, 784);
        _left_front_massage_dialog = new VtpSeatMassageDialogBindView(getContext(), 1264, 440, SeatSOAConstant.LEFT_FRONT);
        _left_area_massage_dialog = new VtpSeatMassageDialogBindView(getContext(), 1264, 440, SeatSOAConstant.LEFT_AREA);
        _right_front_massage_dialog = new VtpSeatMassageDialogBindView(getContext(), 1264, 440, SeatSOAConstant.RIGHT_FRONT);
        _right_area_massage_dialog = new VtpSeatMassageDialogBindView(getContext(), 1264, 440, SeatSOAConstant.RIGHT_AREA);
    }

    @Override
    protected int acquireResourceId() {
        return R.layout.vtp_seat_view_group_layout;
    }

    private ConstraintLayout _con_front_back, _con_area_back;
    private VtpSeatHeatImageView _re_left_front_heat, _re_right_front_heat, _re_left_area_heat, _re_right_area_heat;
    private VtpSeatMassageImageView _iv_left_area_massage, _iv_right_area_massage, _iv_left_front_massage, _iv_right_front_massage;
    private TextView _tv_left_front_massage_mode, _tv_right_front_massage_mode, _tv_left_area_massage_mode, _tv_right_area_massage_mode;
    private VtpSeatVentilateImageView _re_left_front_ventilate, _re_right_front_ventilate;

    private void _init_view() {
        findViewById(R.id.ivi_seat_close).setOnClickListener(v -> listener.onHvacLayout());
        findViewById(R.id.open_front).setOnClickListener(v -> {
            _is_front_back = false;
            _is_show_front_back(true);
        });
        findViewById(R.id.open_area).setOnClickListener(v -> {
            _is_front_back = true;
            _is_show_front_back(false);
        });
        findViewById(R.id.tv_adjust).setOnClickListener(v -> {
            if (_is_front_back) {
                //前排
                _front_row_adjust_dialog.show();
            } else {
                _back_row_adjust_dialog.show();
            }
        });
        RadioButton _rb_heat = findViewById(R.id.rb_heat);
        RadioButton _rb_massage = findViewById(R.id.rb_massage);
        _rb_heat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed())
                _is_show_heat_view(true);
        });
        _rb_massage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed())
                _is_show_heat_view(false);
        });
        _con_front_back = findViewById(R.id.con_front_back);
        _con_area_back = findViewById(R.id.con_area_back);

        _re_left_front_ventilate = findViewById(R.id.re_left_front_ventilate);
        _re_right_front_ventilate = findViewById(R.id.re_right_front_ventilate);

        _re_left_front_heat = findViewById(R.id.re_left_front_heat);
        _re_right_front_heat = findViewById(R.id.re_right_front_heat);
        _re_left_area_heat = findViewById(R.id.re_left_area_heat);
        _re_right_area_heat = findViewById(R.id.re_right_area_heat);

        _iv_left_area_massage = findViewById(R.id.iv_left_area_massage);
        _iv_right_area_massage = findViewById(R.id.iv_right_area_massage);
        _iv_left_front_massage = findViewById(R.id.iv_left_front_massage);
        _iv_right_front_massage = findViewById(R.id.iv_right_front_massage);

        _tv_left_front_massage_mode = findViewById(R.id.tv_left_front_massage_mode);
        _tv_right_front_massage_mode = findViewById(R.id.tv_right_front_massage_mode);
        _tv_left_area_massage_mode = findViewById(R.id.tv_left_area_massage_mode);
        _tv_right_area_massage_mode = findViewById(R.id.tv_right_area_massage_mode);
    }

    private void _is_show_front_back(boolean is_show) {
        _con_front_back.setVisibility(is_show ? VISIBLE : GONE);
        _con_area_back.setVisibility(is_show ? GONE : VISIBLE);
    }

    private void _is_show_heat_view(boolean is_show) {
        //true 显示 通风加热  false  显示按摩
        AAOP_LogUtils.d(TAG, "is_show = " + is_show);
        _re_left_front_heat.setVisibility(is_show ? VISIBLE : GONE);
        _re_right_front_heat.setVisibility(is_show ? VISIBLE : GONE);
        _re_left_area_heat.setVisibility(is_show ? VISIBLE : GONE);
        _re_right_area_heat.setVisibility(is_show ? VISIBLE : GONE);
        _re_left_front_ventilate.setVisibility(is_show ? VISIBLE : GONE);
        _re_right_front_ventilate.setVisibility(is_show ? VISIBLE : GONE);
        _re_left_front_ventilate.setVisibility(is_show ? VISIBLE : GONE);
        _re_right_front_ventilate.setVisibility(is_show ? VISIBLE : GONE);

        _iv_left_front_massage.setVisibility(is_show ? GONE : VISIBLE);
        _iv_right_front_massage.setVisibility(is_show ? GONE : VISIBLE);
        _iv_left_area_massage.setVisibility(is_show ? GONE : VISIBLE);
        _iv_right_area_massage.setVisibility(is_show ? GONE : VISIBLE);
        _tv_left_front_massage_mode.setVisibility(is_show ? GONE : VISIBLE);
        _tv_right_front_massage_mode.setVisibility(is_show ? GONE : VISIBLE);
        _tv_right_area_massage_mode.setVisibility(is_show ? GONE : VISIBLE);
        _tv_left_area_massage_mode.setVisibility(is_show ? GONE : VISIBLE);
    }

    @Override
    public void dispatchMessage(Bundle bundle) {
        super.dispatchMessage(bundle);
        _back_row_adjust_dialog.dispatchMessage(bundle);
        _front_row_adjust_dialog.dispatchMessage(bundle);
        _left_front_massage_dialog.dispatchMessage(bundle);
        _left_area_massage_dialog.dispatchMessage(bundle);
        _right_front_massage_dialog.dispatchMessage(bundle);
        _right_area_massage_dialog.dispatchMessage(bundle);

    }
}

