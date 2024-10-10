package com.adayo.systemui.windows.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.systemui.adapters.LinkingDeviceAdapter;
import com.adayo.systemui.adapters.NearbyNetworksAdapter;
import com.adayo.systemui.bean.WiFiInfo;
import com.adayo.systemui.interfaces.BaseCallback;
import com.adayo.systemui.manager.PopupsManager;
import com.adayo.systemui.manager.SourceControllerImpl;
import com.adayo.systemui.manager.WiFiControllerImpl;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.panels.HvacPanel;
import com.android.systemui.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PopupWifiView extends LinearLayout implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private SwitchButtonVe wifiSwitchButton;
    private RelativeLayout rl_wifi_more, _rl_wifi;
    private RecyclerView _rv_linking_device, _rv_nearby_networks;
    private List<WiFiInfo> _wifi_info_list = new ArrayList<>();
    private NearbyNetworksAdapter _nearby_networks_adapter;
    private LinkingDeviceAdapter _linking_device_adapter;

    public PopupWifiView(Context context) {
        super(context);
        initView();
    }

    public PopupWifiView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PopupWifiView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @SuppressLint("MissingInflatedId")
    private void initView() {
        View mRootView = AAOP_HSkin.getLayoutInflater(getContext()).inflate(R.layout.popup_wifi_layout, this, true);
        wifiSwitchButton = mRootView.findViewById(R.id.wifi_switch_btn);
        rl_wifi_more = mRootView.findViewById(R.id.rl_wifi_more);
        _rl_wifi = mRootView.findViewById(R.id.rl_wifi);
        _rv_linking_device = mRootView.findViewById(R.id.rv_linking_device);
        _rv_nearby_networks = mRootView.findViewById(R.id.rv_nearby_networks);
        wifiSwitchButton.setOnCheckedChangeListener(this);
        rl_wifi_more.setOnClickListener(this);

        initLinkingDevice();
        initNearbyNetworks();

    }

    private void initLinkingDevice() {
        _rv_linking_device.setVisibility(WiFiControllerImpl.getInstance().isWiFiConnected() ? VISIBLE : GONE);
        _linking_device_adapter = new LinkingDeviceAdapter(WiFiControllerImpl.getInstance().getWifiDevicesList());
        _rv_linking_device.setLayoutManager(new LinearLayoutManager(getContext()));
        _rv_linking_device.setAdapter(_linking_device_adapter);
    }

    private void initNearbyNetworks() {
        List<ScanResult> wifiList = WiFiControllerImpl.getInstance().getWifiList();
        for (ScanResult wifiLists : wifiList) {
            WiFiInfo wiFiInfo = new WiFiInfo();
            if (WiFiControllerImpl.getInstance().isWiFiConnected()) {
                List<WiFiInfo> wifiDevicesList = WiFiControllerImpl.getInstance().getWifiDevicesList();
                if (!wifiDevicesList.isEmpty() && wiFiInfo.getDeviceName() != null && wiFiInfo.getDeviceName().equals(wifiDevicesList.get(0).getDeviceName())) {
                    return;
                }
            }
            wiFiInfo.setDeviceName(wifiLists.SSID);
            wiFiInfo.setRssi(WifiManager.calculateSignalLevel(wifiLists.level, 5));
            LogUtil.d("initNearbyNetworks WiFiInfo：" + wifiLists.SSID + " ,2: " + WifiManager.calculateSignalLevel(wifiLists.level, 5));
            _wifi_info_list.add(wiFiInfo);
        }
        _nearby_networks_adapter = new NearbyNetworksAdapter(_wifi_info_list);
        _rv_nearby_networks.setLayoutManager(new LinearLayoutManager(getContext()));
        _rv_nearby_networks.setAdapter(_nearby_networks_adapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUI() {
        WiFiControllerImpl.getInstance().startScanWifi();
        _rv_linking_device.setVisibility(WiFiControllerImpl.getInstance().isWiFiConnected() ? VISIBLE : GONE);
        if (_linking_device_adapter != null) {
            _linking_device_adapter.setWifiList(WiFiControllerImpl.getInstance().getWifiDevicesList());
            _linking_device_adapter.notifyDataSetChanged();
        }


        List<ScanResult> wifiList = WiFiControllerImpl.getInstance().getWifiList();
        _wifi_info_list.clear();
        for (ScanResult wifiLists : wifiList) {
            WiFiInfo wiFiInfo = new WiFiInfo();
            if (WiFiControllerImpl.getInstance().isWiFiConnected()) {
                List<WiFiInfo> wifiDevicesList = WiFiControllerImpl.getInstance().getWifiDevicesList();
                if (!wifiDevicesList.isEmpty() && wiFiInfo.getDeviceName() != null && wiFiInfo.getDeviceName().equals(wifiDevicesList.get(0).getDeviceName())) {
                    return;
                }
            }
            wiFiInfo.setDeviceName(wifiLists.SSID);
            wiFiInfo.setRssi(WifiManager.calculateSignalLevel(wifiLists.level, 5));
            LogUtil.d("updateUI WiFiInfo： " + wifiLists.SSID + " ,2: " + WifiManager.calculateSignalLevel(wifiLists.level, 5));
            _wifi_info_list.add(wiFiInfo);
        }
        if (_nearby_networks_adapter != null) {
            _nearby_networks_adapter.setWifiList(_wifi_info_list);
            _nearby_networks_adapter.notifyDataSetChanged();
        }

    }


    private boolean currentWifiIsEnable;

    public void initWiFiData() {
        WiFiControllerImpl.getInstance().addCallback((BaseCallback<WiFiInfo>) data -> {
            if (null != data) {
                LogUtil.d("getState = " + data.isEnable());
                if (currentWifiIsEnable != data.isEnable()) {
                    currentWifiIsEnable = data.isEnable();
                    wifiSwitchButton.setChecked(data.isEnable());
                }
                updateUI();
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        WiFiControllerImpl.getInstance().setWiFiEnable(isChecked);
        _rl_wifi.setVisibility(isChecked ? VISIBLE : GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_wifi_more:
                HvacPanel.getInstance().hvacScrollLayoutDismiss();
                SourceControllerImpl.getInstance().requestSoureApp(AdayoSource.ADAYO_SOURCE_WIFI,
                        "ADAYO_SOURCE_WIFI", AppConfigType.SourceSwitch.APP_ON.getValue(), new HashMap<>());
                PopupsManager.getInstance().dismiss();
                break;
            default:
                break;
        }
    }
}
