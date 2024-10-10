package com.adayo.systemui.windows.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adayo.systemui.windows.views.ScreenVolumeSeekBar;
import com.adayo.systemui.windows.views.SwitchButtonVe;
import com.android.systemui.R;

public class SunroofDialog extends BaseDialog implements View.OnClickListener {
    private static final String TAG = "SunroofDialog";
    private SwitchButtonVe warnSwitch;
    private TextView tvMoreSetting;
    private ImageView ivEnterMoreSetting;
    private RadioGroup typeGroup;
    private RadioButton sunroofOne, sunroofTwo, sunroofThree;
    private ScreenVolumeSeekBar sunroofOpenAngle;

    public SunroofDialog(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }

    protected void initView() {
        warnSwitch = findViewById(R.id.sb_warn_switch);
        tvMoreSetting = findViewById(R.id.tv_more_setting);
        ivEnterMoreSetting = findViewById(R.id.iv_enter_more_setting);
        typeGroup = findViewById(R.id.rg_type);
        sunroofOne = findViewById(R.id.rb1);
        sunroofTwo = findViewById(R.id.rb2);
        sunroofThree = findViewById(R.id.rb3);
        sunroofOpenAngle = findViewById(R.id.sunroof_open_angle);
    }

    @Override
    protected void initData() {
        tvMoreSetting.setOnClickListener(this);
        ivEnterMoreSetting.setOnClickListener(this);
    }


    protected void initListener() {

        warnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        typeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rb1) {

                } else if (checkedId == R.id.rb2) {

                } else if (checkedId == R.id.rb3) {

                }
            }
        });

        sunroofOpenAngle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_sunroof;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_enter_more_setting) {

        }
    }
}
