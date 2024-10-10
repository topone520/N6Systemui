package com.adayo.systemui.windows.views;

import static com.adayo.systemui.contents.PublicContents.TYPE_SWITCH_POPUP_BT;
import static com.adayo.systemui.contents.PublicContents.TYPE_SWITCH_POPUP_HOTSPOT;
import static com.adayo.systemui.contents.PublicContents.TYPE_SWITCH_POPUP_SIGNAL;
import static com.adayo.systemui.contents.PublicContents.TYPE_SWITCH_POPUP_WIFI;
import static com.adayo.systemui.contents.PublicContents.WIFI_AP_STATE_ENABLED;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.systemui.bean.BluetoothInfo;
import com.adayo.systemui.bean.HotspotInfo;
import com.adayo.systemui.bean.SignalInfo;
import com.adayo.systemui.bean.WiFiInfo;
import com.adayo.systemui.interfaces.BaseCallback;
import com.adayo.systemui.manager.BluetoothControllerImpl;
import com.adayo.systemui.manager.HotspotControllerImpl;
import com.adayo.systemui.manager.SignalControllerImpl;
import com.adayo.systemui.manager.WiFiControllerImpl;
import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class PopupSwitchView extends LinearLayout implements CompoundButton.OnCheckedChangeListener {
    private SwitchButtonVe switchButtonVe;
    private TextView switchName;

    private boolean signalSwitchState;

    public PopupSwitchView(Context context) {
        super(context);
        initView();
    }

    public PopupSwitchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PopupSwitchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View mRootView = AAOP_HSkin.getLayoutInflater(getContext()).inflate(R.layout.popup_switch_layout, this, true);
        switchButtonVe = mRootView.findViewById(R.id.switch_btn);
        switchName = mRootView.findViewById(R.id.switch_name);
        switchButtonVe.setOnCheckedChangeListener(this);
        initSignalData();
    }

//    private int currentType;
//    public void setPopupType(int type){
//        currentType = type;
//        switch (type){
//            case TYPE_SWITCH_POPUP_SIGNAL:
//                initSignalData();
//                break;
//        }
//    }

    private void initSignalData(){
        setData(R.string.signal_switch);
        SignalControllerImpl.getInstance().addCallback((BaseCallback<SignalInfo>) data -> {
            if (null != data) {
                LogUtil.d("getSignalSwitchState = " + data.getSignalSwitchState());
                signalSwitchState = data.getSignalSwitchState();
                switchButtonVe.setChecked(data.getSignalSwitchState());
            }
        });
    }

    private void setData(int res){
        if(null == switchName){
            return;
        }
        switchName.setText(SystemUIApplication.getSystemUIContext().getString(res));
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//        switch (currentType){
//            case TYPE_SWITCH_POPUP_SIGNAL:
                //Todo signal switch set
                if (isChecked != signalSwitchState) {
                    SignalControllerImpl.getInstance().setSignalEnabled(isChecked);
                }
//                break;
//        }
    }

}
