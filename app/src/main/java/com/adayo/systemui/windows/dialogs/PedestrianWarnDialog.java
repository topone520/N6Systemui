package com.adayo.systemui.windows.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adayo.systemui.windows.views.SwitchButtonVe;
import com.android.systemui.R;

public class PedestrianWarnDialog extends BaseDialog implements View.OnClickListener {
    private static final String TAG = "PedestrianWarnDialog";
    private SwitchButtonVe warnSwitch;
    private TextView tvMoreSetting;
    private ImageView ivEnterMoreSetting;
    private RadioGroup voiceGroup;
    private RadioButton voiceOne, voiceTwo;


    public PedestrianWarnDialog(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }

    protected void initView() {
        warnSwitch = findViewById(R.id.sb_warn_switch);
        tvMoreSetting = findViewById(R.id.tv_more_setting);
        ivEnterMoreSetting = findViewById(R.id.iv_enter_more_setting);
        voiceGroup = findViewById(R.id.rg_voice);
        voiceOne = findViewById(R.id.rb1);
        voiceTwo = findViewById(R.id.rb2);
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

        voiceGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 判断点击的是声音一
                if (checkedId == R.id.rb1) {

                } else {

                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_pedestrian_warn;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_enter_more_setting) {

        }
    }
}