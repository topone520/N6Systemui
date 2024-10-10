package com.adayo.systemui.functional.statusbar.bluetooth._binder;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.soavb.foundation.binder.AbstractBTViewBinder;
import com.adayo.soavb.service.bluetooth.BTCallbackListener;
import com.adayo.soavb.service.bluetooth.BT_CALLBACK_TYPE;
import com.adayo.systemui.manager.PopupsManager;
import com.adayo.systemui.manager.SourceControllerImpl;
import com.adayo.systemui.windows.panels.HvacPanel;
import com.android.systemui.R;

import java.util.HashMap;

public class BluetoothNameViewBinder extends AbstractBTViewBinder implements View.OnClickListener {


    private TextView _bt_name;
    private RelativeLayout _rl_bt_more;

    @SuppressLint("SetTextI18n")
    @Override
    public void bindView(View view) {
        _bt_name = view.findViewById(R.id.bt_name);
        _rl_bt_more = view.findViewById(R.id.rl_bt_more);
        _bt_name.setText("可被发现为”"+getBluetoothService().getSettingsPresenter().getLocalAdapterName()+"“");
        registerCallBack(BT_CALLBACK_TYPE.UPDATE_LOCAL_BLUETOOTH_NAME, (BTCallbackListener<String>) name -> _bt_name.setText("可被发现为”"+name+"“"));
        _rl_bt_more.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_bt_more:
                HvacPanel.getInstance().hvacScrollLayoutDismiss();
                SourceControllerImpl.getInstance().requestSoureApp(AdayoSource.ADAYO_SOURCE_BT_SETTING,
                        "ADAYO_SOURCE_BT_SETTING", AppConfigType.SourceSwitch.APP_ON.getValue(), new HashMap<>());
                PopupsManager.getInstance().dismiss();
                break;
            default:
                break;
        }
    }
}
