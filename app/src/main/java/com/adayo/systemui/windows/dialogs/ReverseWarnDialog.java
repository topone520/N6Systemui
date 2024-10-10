package com.adayo.systemui.windows.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adayo.systemui.windows.views.SwitchButtonVe;
import com.android.systemui.R;

public class ReverseWarnDialog extends BaseDialog implements View.OnClickListener {
    private static final String TAG = "ReverseWarnDialog";
    private SwitchButtonVe warnSwitch;
    private TextView tvMoreSetting;
    private ImageView ivEnterMoreSetting;
    private RadioGroup typeGroup;
    private RadioButton typeOne, typeTwo;


    public ReverseWarnDialog(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }

    protected void initView() {
        warnSwitch = findViewById(R.id.sb_warn_switch);
        tvMoreSetting = findViewById(R.id.tv_more_setting);
        ivEnterMoreSetting = findViewById(R.id.iv_enter_more_setting);
        typeGroup = findViewById(R.id.rg_type);
        typeOne = findViewById(R.id.rb1);
        typeTwo = findViewById(R.id.rb2);
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
                // 判断点击的是声音一
                if (checkedId == R.id.rb1) {

                } else {

                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_reverse_warn;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_enter_more_setting) {

        }
    }
}