package com.adayo.systemui.windows.dialogs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.views.SwitchButtonVe;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class ChildLockDialog extends BaseDialog implements View.OnClickListener {
    private SwitchButtonVe childLockSwitch;
    private TextView tvMoreSetting, leftChildLock, rightChildLock;
    private ImageView ivEnterMoreSetting;
    private childLockClickListener mClickListener = null;

    public ChildLockDialog(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }

    protected void initView() {
        childLockSwitch = findViewById(R.id.sb_child_lock_switch);
        tvMoreSetting = findViewById(R.id.tv_more_setting);
        ivEnterMoreSetting = findViewById(R.id.iv_enter_more_setting);
        leftChildLock = findViewById(R.id.btn_left_child_lock);
        rightChildLock = findViewById(R.id.btn_right_child_lock);
    }

    @Override
    protected void initData() {
        tvMoreSetting.setOnClickListener(this);
        ivEnterMoreSetting.setOnClickListener(this);
        leftChildLock.setOnClickListener(this);
        rightChildLock.setOnClickListener(this);
    }


    protected void initListener() {
        childLockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LogUtil.d("childLockSwitch isChecked = " + isChecked);
                if (buttonView.isPressed()) {

                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_child_lock;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_left_child_lock) {//左儿童锁
            leftChildLock.setSelected(!leftChildLock.isSelected());
        } else if (id == R.id.btn_right_child_lock) {//右儿童锁
            rightChildLock.setSelected(!rightChildLock.isSelected());
        } else if (id == R.id.tv_more_setting || id == R.id.iv_enter_more_setting) {//点击更多设置

        }
    }

    public void setChildLockClickListener(childLockClickListener clickListener) {
        mClickListener = clickListener;
    }


    public interface childLockClickListener {
        void switchStatus(boolean isChecked);

    }

}
