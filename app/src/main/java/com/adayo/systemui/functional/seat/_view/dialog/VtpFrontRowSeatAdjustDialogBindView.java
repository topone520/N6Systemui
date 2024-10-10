package com.adayo.systemui.functional.seat._view.dialog;

import android.content.Context;

import androidx.annotation.NonNull;

import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.functional.seat._binder.SeatBackrestViewBinder;
import com.adayo.systemui.functional.seat._binder.SeatCushionViewBinder;
import com.adayo.systemui.functional.seat._binder.SeatFrontRearViewBinder;
import com.adayo.systemui.functional.seat._binder.SeatHeightAdjustViewBinder;
import com.adayo.systemui.functional.seat._binder.SeatMemoryViewBinder;
import com.adayo.systemui.functional.seat._binder.SeatUsherViewBinder;
import com.adayo.systemui.windows.dialogs.BindViewBaseDialog;
import com.adayo.systemui.windows.views.seat.SeatMemoryView;
import com.adayo.systemui.windows.views.seat.SeatUsherView;
import com.android.systemui.R;


public class VtpFrontRowSeatAdjustDialogBindView extends BindViewBaseDialog {
    public VtpFrontRowSeatAdjustDialogBindView(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }

    @Override
    protected void initView() {
        SeatUsherView seatUsherView = findViewById(R.id.copilot_usher);
        seatUsherView._show_textview(SeatSOAConstant.RIGHT_FRONT);
        SeatMemoryView seatMemoryView = findViewById(R.id.copilot_memory);
        seatMemoryView.settingXmlView(R.drawable.copilot_seat_memory_1,R.drawable.copilot_seat_memory_2,R.drawable.copilot_seat_memory_3);
    }

    @Override
    protected void createViewBinder() {
        insertViewBinder(SeatUsherViewBinder.Builder.createLeft(R.id.driver_usher));
        insertViewBinder(SeatUsherViewBinder.Builder.createRight(R.id.copilot_usher));
        insertViewBinder(new SeatFrontRearViewBinder(SeatSOAConstant.LEFT_FRONT, R.id.driver_cross_adjust));
        insertViewBinder(new SeatFrontRearViewBinder(SeatSOAConstant.RIGHT_FRONT, R.id.copilot_cross_adjust));
        insertViewBinder(new SeatHeightAdjustViewBinder(SeatSOAConstant.LEFT_FRONT, R.id.driver_cross_adjust));
        insertViewBinder(new SeatHeightAdjustViewBinder(SeatSOAConstant.RIGHT_FRONT, R.id.copilot_cross_adjust));
        insertViewBinder(new SeatBackrestViewBinder(SeatSOAConstant.LEFT_FRONT, R.id.driver_backrest));
        insertViewBinder(new SeatBackrestViewBinder(SeatSOAConstant.RIGHT_FRONT, R.id.copilot_backrest));
        insertViewBinder(new SeatCushionViewBinder(SeatSOAConstant.LEFT_FRONT, R.id.driver_cushion));
        insertViewBinder(new SeatCushionViewBinder(SeatSOAConstant.RIGHT_FRONT, R.id.copilot_cushion));
        insertViewBinder(SeatMemoryViewBinder.Builder.createLeft(R.id.driver_memory));
        insertViewBinder(SeatMemoryViewBinder.Builder.createRight(R.id.copilot_memory));
    }

    @Override
    protected int acquireResourceId() {
        return R.layout.vtp_front_row_seat_adjust_layout;
    }


//    @Override
//    protected void createViewBinder() {
//        //迎宾
////        insertViewBinder(new SeatUsherViewBinder(SeatSOAConstant.LEFT_FRONT, R.id.driver_usher));
////        insertViewBinder(new SeatUsherViewBinder(SeatSOAConstant.RIGHT_FRONT, R.id.copilot_usher));
//    }
//
//    @Override
//    protected int acquireResourceId() {
//        return R.layout.front_row_seat_adjust_layout;
//    }
}
