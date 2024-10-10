package com.adayo.systemui.windows.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.systemui.manager.PopupsManager;
import com.adayo.systemui.manager.SourceControllerImpl;
import com.android.systemui.R;

import java.util.HashMap;

public class PopupDvrView extends LinearLayout implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private SwitchButtonVe _dvrSwitchButton;
    private RelativeLayout _rlDvrSound, rl_dvr_more;
    private ImageView _ivDvrSound;
    private boolean _sound = false;

    public PopupDvrView(Context context) {
        super(context);
        initView();
    }

    public PopupDvrView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PopupDvrView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @SuppressLint("MissingInflatedId")
    private void initView() {
        View mRootView = AAOP_HSkin.getLayoutInflater(getContext()).inflate(R.layout.popup_dvr_layout, this, true);
        _dvrSwitchButton = mRootView.findViewById(R.id.dvr_switch_btn);
        rl_dvr_more = mRootView.findViewById(R.id.rl_dvr_more);
        rl_dvr_more.setOnClickListener(this);
        _dvrSwitchButton.setOnCheckedChangeListener(this);
        _rlDvrSound = mRootView.findViewById(R.id.rl_dvr_sound);
        _ivDvrSound = mRootView.findViewById(R.id.iv_dvr_sound);
        _rlDvrSound.setOnClickListener(view -> {
            _sound = !_sound;
            _ivDvrSound.setSelected(_sound);
        });
    }


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked){
            _rlDvrSound.setVisibility(VISIBLE);
        }else {
            _rlDvrSound.setVisibility(GONE);
        }
        // HvacBcmManager.getInstance().rapidDVRSwitch(isChecked ? 1 : 0);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.rl_dvr_more){
            SourceControllerImpl.getInstance().requestSoureApp(AdayoSource.ADAYO_SOURCE_DVR,
                    "ADAYO_SOURCE_DVR", AppConfigType.SourceSwitch.APP_ON.getValue(), new HashMap<>());
            PopupsManager.getInstance().dismiss();
        }
    }
}
