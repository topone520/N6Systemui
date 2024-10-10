package com.adayo.systemui.windows.views;

import static com.adayo.systemui.contents.PublicContents.WIFI_AP_STATE_ENABLED;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.systemui.bean.HotspotInfo;
import com.adayo.systemui.interfaces.BaseCallback;
import com.adayo.systemui.manager.HotspotControllerImpl;
import com.adayo.systemui.manager.PopupsManager;
import com.adayo.systemui.manager.SourceControllerImpl;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.panels.HvacPanel;
import com.android.systemui.R;

import java.util.HashMap;

public class PopupHotspotView extends LinearLayout implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private SwitchButtonVe _hotspotSwitchButton;
    private RelativeLayout _rl_hotspot_more, _rl_hotspot_list;
    private TextView _tv_hotspot_name, _tv_hotspot_password;
    private ImageView _img_hotspot_hint;
    private RadioButton _btn_hotspot_type1, _btn_hotspot_type2;
    private String _hotspot_password;
    private static final int HOTSPOT_2_4G = 1;
    private static final int HOTSPOT_5G = 3;

    public PopupHotspotView(Context context) {
        super(context);
        initView();
    }

    public PopupHotspotView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PopupHotspotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @SuppressLint("MissingInflatedId")
    private void initView() {
        View mRootView = AAOP_HSkin.getLayoutInflater(getContext()).inflate(R.layout.popup_hotspot_layout, this, true);
        _hotspotSwitchButton = mRootView.findViewById(R.id.hotspot_switch_btn);
        _rl_hotspot_more = mRootView.findViewById(R.id.rl_hotspot_more);
        _rl_hotspot_list = mRootView.findViewById(R.id.rl_hotspot_list);
        _tv_hotspot_name = mRootView.findViewById(R.id.tv_hotspot_name);
        _tv_hotspot_password = mRootView.findViewById(R.id.tv_hotspot_password);
        _img_hotspot_hint = mRootView.findViewById(R.id.img_hotspot_hint);
        _btn_hotspot_type1 = mRootView.findViewById(R.id.btn_hotspot_type1);
        _btn_hotspot_type2 = mRootView.findViewById(R.id.btn_hotspot_type2);

        if (null != _hotspot_password && _hotspot_password.length() > 6) {
            _tv_hotspot_password.setText(_hotspot_password.substring(0, 6));
        }
        _tv_hotspot_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        _rl_hotspot_more.setOnClickListener(this);
        _hotspotSwitchButton.setOnCheckedChangeListener(this);
        _img_hotspot_hint.setOnClickListener(this);
        _btn_hotspot_type1.setOnClickListener(this);
        _btn_hotspot_type2.setOnClickListener(this);
        updateUI();
    }

    @SuppressLint("NewApi")
    private void updateUI() {
        _hotspot_password = HotspotControllerImpl.getInstance().getHotspotPassword();
        LogUtil.d("updateUI name: " + HotspotControllerImpl.getInstance().getHotspotName() + " ,password: " + HotspotControllerImpl.getInstance().getHotspotPassword() + " ,apmode: " + HotspotControllerImpl.getInstance().getHotspotAPMode());
        _tv_hotspot_name.setText(HotspotControllerImpl.getInstance().getHotspotName());
        if (_tv_hotspot_password.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            _tv_hotspot_password.setText(_hotspot_password);
        } else {
            _tv_hotspot_password.setText(_hotspot_password.substring(0, 6));
        }
        _btn_hotspot_type1.setSelected(HotspotControllerImpl.getInstance().getHotspotAPMode() == HOTSPOT_2_4G);
        _btn_hotspot_type2.setSelected(HotspotControllerImpl.getInstance().getHotspotAPMode() == HOTSPOT_5G);
    }

    public void initHotspotData() {
        HotspotControllerImpl.getInstance().addCallback((BaseCallback<HotspotInfo>) data -> {
            if (null != data) {
                LogUtil.d("getState = " + data.getmHotspotState());
                _hotspotSwitchButton.setChecked(data.getmHotspotState() == WIFI_AP_STATE_ENABLED);
                _rl_hotspot_list.setVisibility(data.getmHotspotState() == WIFI_AP_STATE_ENABLED ? VISIBLE : GONE);
                updateUI();
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        HotspotControllerImpl.getInstance().setHotspotEnabled(isChecked);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.rl_hotspot_more){
            HvacPanel.getInstance().hvacScrollLayoutDismiss();
            SourceControllerImpl.getInstance().requestSoureApp("ADAYO_SOURCE_AP",
                    "ADAYO_SOURCE_AP", AppConfigType.SourceSwitch.APP_ON.getValue(), new HashMap<>());
            PopupsManager.getInstance().dismiss();
        } else if (viewId == R.id.img_hotspot_hint){
            if (_tv_hotspot_password.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                _tv_hotspot_password.setText(_hotspot_password.substring(0, 6));
                _tv_hotspot_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                _img_hotspot_hint.setImageResource(R.mipmap.ivi_top_icon_wifi_password_view_n);
            } else {
                _tv_hotspot_password.setText(_hotspot_password);
                _tv_hotspot_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                _img_hotspot_hint.setImageResource(R.mipmap.ivi_top_icon_wifi_password_hide_n);
            }
        } else if (viewId == R.id.btn_hotspot_type1){
            _btn_hotspot_type1.setSelected(true);
            _btn_hotspot_type2.setSelected(false);
            HotspotControllerImpl.getInstance().reqAPMode(HOTSPOT_2_4G);

        } else if (viewId == R.id.btn_hotspot_type2){
            _btn_hotspot_type1.setSelected(false);
            _btn_hotspot_type2.setSelected(true);
            HotspotControllerImpl.getInstance().reqAPMode(HOTSPOT_5G);
        }
    }
}
