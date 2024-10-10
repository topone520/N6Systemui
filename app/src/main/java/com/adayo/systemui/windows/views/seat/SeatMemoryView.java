package com.adayo.systemui.windows.views.seat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.android.systemui.R;

public class SeatMemoryView extends FrameLayout implements View.OnClickListener {
    private final static String TAG = SeatMemoryView.class.getSimpleName();
    private final int SLOT_1 = 1;
    private final int SLOT_2 = 2;
    private final int SLOT_3 = 3;
    private ImageView seatMemory1;
    private ImageView seatMemory2;
    private ImageView seatMemory3;
    private int _old_slot;
    private int _new_slot;
    private SeatMemorySlotListener seatMemorySlotListener;

    public void setSeatMemorySlotListener(SeatMemorySlotListener seatMemorySlotListener) {
        this.seatMemorySlotListener = seatMemorySlotListener;
    }

    public void updateUI(int slot) {
        if (slot < 1 || slot > 3) return;
        _set_view_bg(slot);
    }

    public SeatMemoryView(@NonNull Context context) {
        super(context);
        _initialize_view();
    }

    public SeatMemoryView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _initialize_view();
    }

    private void _initialize_view() {
        addView(AAOP_HSkin.getLayoutInflater(getContext()).inflate(R.layout.seat_memory_layout, null));
        seatMemory1 = (ImageView) findViewById(R.id.seat_memory_1);
        seatMemory2 = (ImageView) findViewById(R.id.seat_memory_2);
        seatMemory3 = (ImageView) findViewById(R.id.seat_memory_3);
        seatMemory1.setOnClickListener(this);
        seatMemory2.setOnClickListener(this);
        seatMemory3.setOnClickListener(this);
    }



    public void settingXmlView(int imgSrc1, int imgSrc2, int imgSrc3) {
        seatMemory1.setImageResource(imgSrc1);
        seatMemory2.setImageResource(imgSrc2);
        seatMemory3.setImageResource(imgSrc3);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.seat_memory_1:
                _new_slot = SLOT_1;
                break;
            case R.id.seat_memory_2:
                _new_slot = SLOT_2;
                break;
            case R.id.seat_memory_3:
                _new_slot = SLOT_3;
                break;
        }
        _set_view_bg(_new_slot);
        if (_old_slot != _new_slot) {
            _old_slot = _new_slot;
            if (seatMemorySlotListener != null) {
                seatMemorySlotListener.onSelectSlot(_new_slot);
            }
        }
    }

    private void _set_view_bg(int slot) {
        AAOP_LogUtils.d(TAG, "slot = " + slot);
        seatMemory1.setSelected(slot == SLOT_1);
        seatMemory2.setSelected(slot == SLOT_2);
        seatMemory3.setSelected(slot == SLOT_3);
    }

    public interface SeatMemorySlotListener {
        void onSelectSlot(int slot);
    }
}
