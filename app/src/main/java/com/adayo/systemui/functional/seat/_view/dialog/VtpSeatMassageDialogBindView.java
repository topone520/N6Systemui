package com.adayo.systemui.functional.seat._view.dialog;


import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.functional.seat._binder.SeatMassageMoveViewBinder;
import com.adayo.systemui.windows.dialogs.BindViewBaseDialog;
import com.android.systemui.R;

public class VtpSeatMassageDialogBindView extends BindViewBaseDialog {
    private final int _seat_position;

    public VtpSeatMassageDialogBindView(@NonNull Context context, int dialogWidth, int _dialogHeight, int seat_position) {
        super(context, dialogWidth, _dialogHeight);
        _seat_position = seat_position;
    }

    @Override
    protected void initView() {
        TextView _pop_title = findViewById(R.id.pop_title);
        if (_seat_position == SeatSOAConstant.LEFT_FRONT) {
            _pop_title.setText(R.string.seat_main_massage_mode);
        } else if (_seat_position == SeatSOAConstant.RIGHT_FRONT) {
            _pop_title.setText(R.string.seat_co_massage_mode);
        } else if (_seat_position == SeatSOAConstant.LEFT_AREA) {
            _pop_title.setText(R.string.seat_left_area_massage_mode);
        } else {
            _pop_title.setText(R.string.seat_right_area_massage_mode);
        }
    }

    @Override
    protected void createViewBinder() {
        switch (_seat_position) {
            case SeatSOAConstant.LEFT_FRONT:
                insertViewBinder(SeatMassageMoveViewBinder.Builder.createLeft(R.id.seat_massage_rlv));
                break;
            case SeatSOAConstant.RIGHT_FRONT:
                insertViewBinder(SeatMassageMoveViewBinder.Builder.createRight(R.id.seat_massage_rlv));
                break;
            case SeatSOAConstant.LEFT_AREA:
                insertViewBinder(SeatMassageMoveViewBinder.Builder.createLeftArea(R.id.seat_massage_rlv));
                break;
            case SeatSOAConstant.RIGHT_AREA:
                insertViewBinder(SeatMassageMoveViewBinder.Builder.createRightArea(R.id.seat_massage_rlv));
                break;
        }
    }

    @Override
    protected int acquireResourceId() {
        return R.layout.vtp_dialog_seat_massage_layout;
    }

}
