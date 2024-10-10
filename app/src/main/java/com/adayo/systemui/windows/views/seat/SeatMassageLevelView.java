package com.adayo.systemui.windows.views.seat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.android.systemui.R;

public class SeatMassageLevelView extends FrameLayout {
    private static String TAG = SeatMassageLevelView.class.getSimpleName();
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioButton radioButton4;
    private RadioGroup radioGroup;

    private MassageLevelListener _levelListener;

    public void set_levelListener(MassageLevelListener _levelListener) {
        this._levelListener = _levelListener;
    }

    public SeatMassageLevelView(Context context) {
        super(context);
        initialize();
    }

    public SeatMassageLevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize() {
        addView(AAOP_HSkin.getLayoutInflater(getContext()).inflate(R.layout.seat_massage_radiogroup, null));
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioButton1 = (RadioButton) findViewById(R.id.radio_button_1);
        radioButton2 = (RadioButton) findViewById(R.id.radio_button_2);
        radioButton3 = (RadioButton) findViewById(R.id.radio_button_3);
        radioButton4 = (RadioButton) findViewById(R.id.radio_button_4);
        init_radioGroup();
    }

    private void init_radioGroup() {
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // 获取当前选中的 RadioButton 在 RadioGroup 中的下标
            if (group.findViewById(checkedId).isPressed()) {
                _levelListener.onMassageLevel(getLevelConvert(radioGroup.indexOfChild(findViewById(checkedId))));
            }
        });
    }

    public int getLevelConvert(int gear) {
        //1--高  2--中  3--低 0-关
        //1--低  2--中  3--高
        if (gear == 0) {
            return 0;
        } else if (gear == 1) {
            return SeatSOAConstant.NEW_SEAT_GEAR_HIGH;
        } else if (gear == 2) {
            return SeatSOAConstant.NEW_SEAT_GEAR_MID;
        }
        return SeatSOAConstant.NEW_SEAT_GEAR_LOW;
    }

    public void _update_ui(int position) {
        AAOP_LogUtils.d(TAG, "position = " + position);
        if (position == 3) {
            radioButton2.setChecked(true);
        } else if (position == 2) {
            radioButton3.setChecked(true);
        } else if (position == 1) {
            radioButton4.setChecked(true);
        } else {
            radioButton1.setChecked(true);
        }
    }

    public interface MassageLevelListener {
        void onMassageLevel(int level);
    }
}
