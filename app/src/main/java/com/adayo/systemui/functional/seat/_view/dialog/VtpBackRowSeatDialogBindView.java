package com.adayo.systemui.functional.seat._view.dialog;

import android.content.Context;

import androidx.annotation.NonNull;

import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.functional.seat._binder.SeatBackrestViewBinder;
import com.adayo.systemui.windows.dialogs.BindViewBaseDialog;
import com.android.systemui.R;


public class VtpBackRowSeatDialogBindView extends BindViewBaseDialog {

    public VtpBackRowSeatDialogBindView(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void createViewBinder() {
        insertViewBinder(new SeatBackrestViewBinder(SeatSOAConstant.LEFT_AREA, R.id.left_area_back));
        insertViewBinder(new SeatBackrestViewBinder(SeatSOAConstant.RIGHT_AREA, R.id.right_area_back));
    }

    @Override
    protected int acquireResourceId() {
        return R.layout.vtp_seat_back_row_layout;
    }
}
