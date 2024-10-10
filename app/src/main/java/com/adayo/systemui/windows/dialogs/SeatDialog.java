package com.adayo.systemui.windows.dialogs;

import android.content.Context;

import androidx.annotation.NonNull;

import com.adayo.systemui.windows.views.QsIconSeatButtin;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class SeatDialog extends BaseDialog {

    private QsIconSeatButtin seat_adjustmentone;
    private QsIconSeatButtin seat_adjustmenttwo;
    private QsIconSeatButtin seat_adjustmentthere;
    private QsIconSeatButtin passenger_seatone;
    private QsIconSeatButtin passenger_seattwo;
    private QsIconSeatButtin passenger_seatthere;
    private boolean isUsher;
    public SeatDialog(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }

    @Override
    protected void initView() {
        seat_adjustmentone = findViewById(R.id.first_gear);
        seat_adjustmentone.setIconLayout();
        seat_adjustmenttwo = findViewById(R.id.second_gear);
        seat_adjustmentone.setIconLayout();
        seat_adjustmentthere = findViewById(R.id.three_gears);
        seat_adjustmentone.setIconLayout();
        passenger_seatone = findViewById(R.id.one_gear);
        passenger_seatone.setIconLayout();
        passenger_seattwo = findViewById(R.id.two_gear);
        passenger_seattwo.setIconLayout();
        passenger_seatthere = findViewById(R.id.third_gears);
        passenger_seatthere.setIconLayout();
        updateUI();
    }

    public void updateUI() {
        seat_adjustmentone.setIconImage(R.mipmap.ivi_menu_down_icon_seat_1_n);
        seat_adjustmentone.setIconText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.massage_one));
        seat_adjustmenttwo.setIconImage(R.mipmap.ivi_menu_down_icon_seat_2_n);
        seat_adjustmenttwo.setIconText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.massage_two));
        seat_adjustmentthere.setIconImage(R.mipmap.ivi_menu_down_icon_seat_3_n);
        seat_adjustmentthere.setIconText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.massage_there));
        passenger_seatone.setIconImage(R.mipmap.ivi_menu_down_icon_seat_1_n);
        passenger_seatone.setIconText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.massage_one));
        passenger_seattwo.setIconImage(R.mipmap.ivi_menu_down_icon_seat_2_n);
        passenger_seattwo.setIconText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.massage_two));
        passenger_seatthere.setIconImage(R.mipmap.ivi_menu_down_icon_seat_3_n);
        passenger_seatthere.setIconText(SystemUIApplication.getSystemUIContext().getResources().getString(R.string.massage_there));
        setTvUsher();
        seat_adjustmentone.setOnClickListener(v -> setBG(1));
        seat_adjustmenttwo.setOnClickListener(v -> setBG(2));
        seat_adjustmentthere.setOnClickListener(v -> setBG(3));
        passenger_seatone.setOnClickListener(v -> setBG(1));
        passenger_seattwo.setOnClickListener(v -> setBG(2));
        passenger_seatthere.setOnClickListener(v -> setBG(3));

    }

    private void setBG(int type) {
        seat_adjustmentone.setBackgroundResource(type == 1 ? R.drawable.comm_c478bc5_bg : R.color.c00000000);
        seat_adjustmenttwo.setBackgroundResource(type == 2 ? R.drawable.comm_c478bc5_bg : R.color.c00000000);
        seat_adjustmentthere.setBackgroundResource(type == 3 ? R.drawable.comm_c478bc5_bg : R.color.c00000000);
        passenger_seatone.setBackgroundResource(type == 1 ? R.drawable.comm_c478bc5_bg : R.color.c00000000);
        passenger_seattwo.setBackgroundResource(type == 2 ? R.drawable.comm_c478bc5_bg : R.color.c00000000);
        passenger_seatthere.setBackgroundResource(type == 3 ? R.drawable.comm_c478bc5_bg : R.color.c00000000);
    }

    private void setTvUsher() {
        isUsher = !isUsher;
        //_listener.onVecome(isUsher);
//
    }

    public interface AdjustViewListener {
        void onVecome(boolean isUsher);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_seat_adjustment;
    }
}
