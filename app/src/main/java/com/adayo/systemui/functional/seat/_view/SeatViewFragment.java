package com.adayo.systemui.functional.seat._view;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.view.AbstractBindViewFramelayout;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.functional.seat._binder.SeatAdjustViewBinder;
import com.adayo.systemui.functional.seat._binder.SeatAllCloseViewBinder;
import com.adayo.systemui.functional.seat._binder.SeatHeatGrepViewBinder;
import com.adayo.systemui.functional.seat._binder.SeatHeatViewBinder;
import com.adayo.systemui.functional.seat._binder.SeatMassageViewBinder;
import com.adayo.systemui.functional.seat._binder.SeatVentilateViewBinder;
import com.adayo.systemui.functional.seat._binder.SteeringViewBinder;
import com.adayo.systemui.functional.seat._view.dialog.BackRowSeatDialogBindView;
import com.adayo.systemui.functional.seat._view.dialog.FrontRowSeatAdjustDialogBindView;
import com.adayo.systemui.functional.seat._view.dialog.SeatMassageDialogBindView;
import com.adayo.systemui.interfaces.HvacLayoutSwitchListener;
import com.adayo.systemui.utils.HvacChildViewAnimation;
import com.adayo.systemui.windows.dialogs.BindViewBaseDialog;
import com.android.systemui.R;

public class SeatViewFragment extends AbstractBindViewFramelayout {
    private static final String TAG = SeatViewFragment.class.getSimpleName();
    private LinearLayout _seat_back_view;
    private ConstraintLayout _seat_left_front_view;
    private ConstraintLayout _seat_right_front_view;
    private ImageView _iv_front_row_bg, _iv_area_row_bg;
    private TextView _tv_front_row_switch, _tv_back_raw, _tv_area_row_switch, _tv_front_raw;
    private HvacLayoutSwitchListener listener;
    private BindViewBaseDialog _back_row_seat_dialog, _front_row_seat_adjust_dialog;
    private SeatMassageDialogBindView _massage_dialog_front_row, _massage_dialog_back_row;

    public void setListener(HvacLayoutSwitchListener listener) {
        this.listener = listener;
    }

    public SeatViewFragment(@NonNull Context context) {
        super(context);
        _init_view();
    }

    public SeatViewFragment(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _init_view();
    }

    private void _init_view() {
        ImageView _ivi_seat_close = findViewById(R.id.ivi_seat_close);
        _seat_left_front_view = findViewById(R.id.seat_left_front_view);
        _seat_back_view = findViewById(R.id.seat_back_view);
        _seat_right_front_view = findViewById(R.id.seat_right_front_view);
        _tv_front_row_switch = findViewById(R.id.tv_front_row_switch);
        _tv_area_row_switch = findViewById(R.id.tv_area_row_switch);
        _tv_back_raw = findViewById(R.id.tv_back_raw);
        _tv_front_raw = findViewById(R.id.tv_front_raw);
        _iv_front_row_bg = findViewById(R.id.iv_front_row_bg);
        _iv_area_row_bg = findViewById(R.id.iv_area_row_bg);
        _tv_back_raw.setOnClickListener(v -> is_show_front_row(true));
        _tv_front_raw.setOnClickListener(v -> is_show_front_row(false));
        _ivi_seat_close.setOnClickListener(v -> listener.onHvacLayout());
    }

    /**
     * true 隐藏前排,显示后排   false 隐藏后排,显示前排
     *
     * @param is_show
     */
    private void is_show_front_row(boolean is_show) {
        if (is_show) {
            _iv_front_row_bg.setVisibility(GONE);
            _iv_area_row_bg.setVisibility(VISIBLE);
            HvacChildViewAnimation.performFadeOutAnimation(_seat_left_front_view);
            HvacChildViewAnimation.performFadeOutAnimation(_seat_right_front_view);
            _tv_front_row_switch.setVisibility(GONE);
            HvacChildViewAnimation.performFadeOutAnimation(_tv_back_raw);
            HvacChildViewAnimation.fadeInView(_seat_back_view);
            _tv_area_row_switch.setVisibility(VISIBLE);
            HvacChildViewAnimation.fadeInView(_tv_front_raw);
        } else {
            _iv_front_row_bg.setVisibility(VISIBLE);
            _iv_area_row_bg.setVisibility(GONE);
            HvacChildViewAnimation.performFadeOutAnimation(_seat_back_view);
            _tv_area_row_switch.setVisibility(GONE);
            HvacChildViewAnimation.performFadeOutAnimation(_tv_front_raw);
            _tv_front_row_switch.setVisibility(VISIBLE);
            HvacChildViewAnimation.fadeInView(_seat_right_front_view);
            HvacChildViewAnimation.fadeInView(_seat_left_front_view);
            HvacChildViewAnimation.fadeInView(_tv_back_raw);
        }
    }

    @Override
    protected void createViewBinder() {
        //座椅dialog 绑定
        _init_dialog_layout();

        //ViewGroup
        //加热自动降档
        super.insertViewBinder(new SeatHeatGrepViewBinder());
        //方向盘加热
        super.insertViewBinder(new SteeringViewBinder());
        //座椅调节
        super.insertViewBinder(new SeatAdjustViewBinder(SeatSOAConstant.LEFT_FRONT, R.id.re_left_front_adjust, _front_row_seat_adjust_dialog, _back_row_seat_dialog));
        super.insertViewBinder(new SeatAdjustViewBinder(SeatSOAConstant.RIGHT_FRONT, R.id.re_right_front_adjust, _front_row_seat_adjust_dialog, _back_row_seat_dialog));
        super.insertViewBinder(new SeatAdjustViewBinder(SeatSOAConstant.LEFT_AREA, R.id.re_left_rear_adjust, _front_row_seat_adjust_dialog, _back_row_seat_dialog));
        super.insertViewBinder(new SeatAdjustViewBinder(SeatSOAConstant.RIGHT_AREA, R.id.re_right_rear_adjust, _front_row_seat_adjust_dialog, _back_row_seat_dialog));
        //座椅加热
        super.insertViewBinder(SeatHeatViewBinder.Builder.createLeft(R.id.re_left_front_heat));
        super.insertViewBinder(SeatHeatViewBinder.Builder.createRight(R.id.re_right_front_heat));
        super.insertViewBinder(SeatHeatViewBinder.Builder.createLeftArea(R.id.re_left_rear_heat));
        super.insertViewBinder(SeatHeatViewBinder.Builder.createRightArea(R.id.re_right_rear_heat));
        //按摩SeatResourceUtils.
        super.insertViewBinder(SeatMassageViewBinder.Builder.createLeft(R.id.re_left_front_massage, _massage_dialog_front_row));
        super.insertViewBinder(SeatMassageViewBinder.Builder.createRight(R.id.re_right_front_massage, _massage_dialog_front_row));
        super.insertViewBinder(SeatMassageViewBinder.Builder.createLeftArea(R.id.re_left_rear_massage, _massage_dialog_back_row));
        super.insertViewBinder(SeatMassageViewBinder.Builder.createRightArea(R.id.re_right_rear_massage, _massage_dialog_back_row));
        //通风SeatResourceUtils.
        super.insertViewBinder(SeatVentilateViewBinder.Builder.createLeft(R.id.re_left_front_ventilate));
        super.insertViewBinder(SeatVentilateViewBinder.Builder.createRight(R.id.re_right_front_ventilate));
        //全关按钮
        super.insertViewBinder(new SeatAllCloseViewBinder(R.id.tv_front_row_switch, SeatSOAConstant.FRONT_ROW));
        super.insertViewBinder(new SeatAllCloseViewBinder(R.id.tv_area_row_switch, SeatSOAConstant.BACK_ROW));
    }

    private void _init_dialog_layout() {
        //后排座椅调节
        _back_row_seat_dialog = new BackRowSeatDialogBindView(getContext(), 1368, 640);
        //前排座椅调节
        _front_row_seat_adjust_dialog = new FrontRowSeatAdjustDialogBindView(getContext(), 2480, 680);
        //座椅按摩
        _massage_dialog_front_row = new SeatMassageDialogBindView(getContext(), 2728, 680, SeatSOAConstant.FRONT_ROW);
        _massage_dialog_back_row = new SeatMassageDialogBindView(getContext(), 2728, 680, SeatSOAConstant.BACK_ROW);
    }

    @Override
    protected int acquireResourceId() {
        return R.layout.seat_view_group_layout;
    }

    @Override
    public void dispatchMessage(Bundle bundle) {
        AAOP_LogUtils.i(TAG, "dispatchMessage bundle:" + bundle.getString("message_id"));
        super.dispatchMessage(bundle);
        _back_row_seat_dialog.dispatchMessage(bundle);
        _front_row_seat_adjust_dialog.dispatchMessage(bundle);
        _massage_dialog_front_row.dispatchMessage(bundle);
        _massage_dialog_back_row.dispatchMessage(bundle);
    }

}
