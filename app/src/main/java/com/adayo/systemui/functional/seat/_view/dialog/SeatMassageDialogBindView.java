package com.adayo.systemui.functional.seat._view.dialog;

import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.functional.seat._binder.SeatMassageLevelViewBinder;
import com.adayo.systemui.functional.seat._binder.SeatMassageMoveViewBinder;
import com.adayo.systemui.windows.dialogs.BindViewBaseDialog;
import com.android.systemui.R;

public class SeatMassageDialogBindView extends BindViewBaseDialog {
    private static final String TAG = SeatMassageDialogBindView.class.getSimpleName();
    private TextView tv_left_position;
    private TextView tv_right_position;
    private int _seat_position;

    public SeatMassageDialogBindView(@NonNull Context context, int dialogWidth, int _dialogHeight, int seat_position) {
        super(context, dialogWidth, _dialogHeight);
        _seat_position = seat_position;
    }

    @Override
    protected void initView() {
        tv_left_position = findViewById(R.id.tv_left_massage);
        tv_right_position = findViewById(R.id.tv_right_massage);
        //根据创建对象传入的 前排 后排 来判断展示
        tv_left_position.setText(getContext().getResources().getString(_seat_position == SeatSOAConstant.FRONT_ROW ? R.string.driver_seat_massage_adjust : R.string.left_area_seat_massage_adjust));
        tv_right_position.setText(getContext().getResources().getString(_seat_position == SeatSOAConstant.FRONT_ROW ? R.string.copilot_seat_massage_adjust : R.string.right_rear_seat_massage_adjust));
    }

    @Override
    protected void createViewBinder() {
        //根据创建对象传入的 前排 后排 来传入对应位置
        //按摩档位 viewGroup
        //按摩模式
        if (_seat_position == SeatSOAConstant.FRONT_ROW) {
            //前排
            insertViewBinder(SeatMassageLevelViewBinder.Builder.createLeft(R.id.left_radio_group));
            insertViewBinder(SeatMassageLevelViewBinder.Builder.createRight(R.id.right_radio_group));
            insertViewBinder(SeatMassageMoveViewBinder.Builder.createLeft(R.id.left_seat_massage_rlv));
            insertViewBinder(SeatMassageMoveViewBinder.Builder.createRight(R.id.right_seat_massage_rlv));
        } else {
            //后排
            insertViewBinder(SeatMassageMoveViewBinder.Builder.createLeftArea(R.id.left_seat_massage_rlv));
            insertViewBinder(SeatMassageMoveViewBinder.Builder.createRightArea(R.id.right_seat_massage_rlv));
            insertViewBinder(SeatMassageLevelViewBinder.Builder.createLeftArea(R.id.left_radio_group));
            insertViewBinder(SeatMassageLevelViewBinder.Builder.createRightArea(R.id.right_radio_group));
        }
    }

    @Override
    protected int acquireResourceId() {
        return R.layout.seat_massage_dialog_layout;
    }
}
