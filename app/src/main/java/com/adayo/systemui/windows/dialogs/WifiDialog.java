package com.adayo.systemui.windows.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.systemui.adapters.WifiRecycleViewAdapter;
import com.adayo.systemui.utils.AccessPoint;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.viewModel.WifiViewModel;
import com.adayo.systemui.windows.views.SwitchButtonVe;
import com.android.systemui.R;

import java.util.ArrayList;
import java.util.List;

public class WifiDialog extends BaseDialog {

    private RecyclerView _recyclerView;

    private WifiRecycleViewAdapter _wifiRecycleViewAdapter;

    private WifiViewModel _wifiViewModel;

    private SwitchButtonVe _btnWifiSwitch;

    public WifiDialog(@NonNull Context context, int dialogWidth, int dialogHeight) {
        super(context, dialogWidth, dialogHeight);
    }

    @Override
    protected void initView() {
        _btnWifiSwitch = findViewById(R.id.btn_wifi_switch);

        _recyclerView = findViewById(R.id.rv_dialog_wifi);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(_mContext);
        _recyclerView.setLayoutManager(linearLayoutManager);

        _wifiRecycleViewAdapter = new WifiRecycleViewAdapter(_mContext, new ArrayList<>());
        _recyclerView.setAdapter(_wifiRecycleViewAdapter);

        setHeaderView();
    }

    @Override
    protected void initData() {

        _wifiViewModel = getAndroidFactory().create(WifiViewModel.class);

        _wifiViewModel.reqStartScan();
        _wifiViewModel.onAccessPointsChanged();

        _wifiViewModel.getWifiState().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                _btnWifiSwitch.setChecked(aBoolean);
                _wifiRecycleViewAdapter.updateConnectNameStatus(aBoolean);
            }
        });

        _wifiViewModel.getConnectedAccessPoint().observe(this, new Observer<AccessPoint>() {
            @Override
            public void onChanged(AccessPoint accessPoint) {
                if (accessPoint != null) {
                    _wifiRecycleViewAdapter.setDeviceWifiName(accessPoint.getSsid().toString());
                }
            }
        });

        _wifiViewModel.getWifiScanList().observe(this, new Observer<List<AccessPoint>>() {
            @Override
            public void onChanged(List<AccessPoint> accessPoints) {
                _wifiRecycleViewAdapter.setData(accessPoints);
            }
        });

    }

    @Override
    protected void initListener() {
        _btnWifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                LogUtil.d("btn: status = " + isChecked);
                if (compoundButton.isPressed()) {
                    _wifiViewModel.reqWifiEnabled(isChecked);
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_wifi;
    }

    protected void setHeaderView() {
        View header = LayoutInflater.from(_mContext).inflate(R.layout.layout_wifi_header, _recyclerView, false);
        _wifiRecycleViewAdapter.setHeaderView(header);
    }
}
