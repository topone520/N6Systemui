package com.adayo.systemui.functional.statusbar.bluetooth._binder;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.bpresenter.bluetooth.constants.BusinessConstants;
import com.adayo.common.bluetooth.bean.BluetoothDevice;
import com.adayo.common.bluetooth.constant.BtDef;
import com.adayo.proxy.setting.system.utils.LogUtil;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractBTViewBinder;
import com.adayo.soavb.service.bluetooth.BTCallbackListener;
import com.adayo.soavb.service.bluetooth.BT_CALLBACK_TYPE;
import com.adayo.systemui.adapters.BtAvailableAdapter;
import com.adayo.systemui.adapters.BtPairedAdapter;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import java.util.ArrayList;
import java.util.List;

public class BluetoothPairedDevicesViewBinder extends AbstractBTViewBinder implements BtPairedAdapter.OnOperationListener, BtAvailableAdapter.OnConnectListener {
    private static final String TAG = BluetoothPairedDevicesViewBinder.class.getSimpleName();

    private RelativeLayout _bluetooth_rl;
    private RecyclerView _rv_linking_device;
    private RecyclerView _rv_bt_nearby_equipment;
    private BtPairedAdapter _bt_paired_adapter;
    private BusinessConstants.BTStateType _bt_state;
    private BtAvailableAdapter _bt_available_adapter;
    private List<BluetoothDevice> _paired_devices = new ArrayList<>();

    @Override
    public void bindView(View view) {
        _bluetooth_rl = view.findViewById(R.id.bluetooth_rl);
        _rv_linking_device = view.findViewById(R.id.rv_linking_device);
        _rv_bt_nearby_equipment = view.findViewById(R.id.rv_bt_nearby_equipment);

        initLinkingDevice(view);
        initNearbyEquipment(view);

    }

    private void initLinkingDevice(View view) {
        _bt_paired_adapter = new BtPairedAdapter();
        _rv_linking_device.setLayoutManager(new LinearLayoutManager(view.getContext()));
        _rv_linking_device.setAdapter(_bt_paired_adapter);
        _bt_paired_adapter.setOnConnectListener(this);
        registerCallBack(BT_CALLBACK_TYPE.UPDATE_BLUETOOTH_STATE_CHANGE, (BTCallbackListener<BusinessConstants.BTStateType>) type -> {
            AAOP_LogUtils.i(TAG, "Bluetooth State:" + type);
            _bt_state = type;
            if (type == BusinessConstants.BTStateType.TURN_ON) {
                getBluetoothService().getScanningPresenter().startScan();
                _bluetooth_rl.setVisibility(View.VISIBLE);
                List<BluetoothDevice> pairedDevices = getBluetoothService().getPairedDevices();
                for (BluetoothDevice devices : pairedDevices){
                    if (devices.getState() == BtDef.STATE_CONNECTED){
                        _rv_linking_device.setVisibility(View.VISIBLE);
                    }
                }
            } else if (type == BusinessConstants.BTStateType.TURN_OFF) {
                _bt_paired_adapter.clear();
                _bluetooth_rl.setVisibility(View.GONE);
            }
        });
        registerCallBack(BT_CALLBACK_TYPE.UPDATE_PAIRED_DEVICES, (BTCallbackListener<List<BluetoothDevice>>) devices -> _bt_paired_adapter.setNewData(devices));
        AAOP_LogUtils.i(TAG, "UPDATE_PAIRED_DEVICES:" + getBluetoothService().getSettingsPresenter().isBluetoothEnable());
        if (getBluetoothService().getSettingsPresenter().isBluetoothEnable()) {
            getBluetoothService().getScanningPresenter().startScan();
            _bt_paired_adapter.setNewData(getBluetoothService().getPairedDevices());
            _bluetooth_rl.setVisibility(View.VISIBLE);
            _rv_linking_device.setVisibility(View.VISIBLE);
        }
    }

    private void initNearbyEquipment(View view) {
        _bt_available_adapter = new BtAvailableAdapter();
        _rv_bt_nearby_equipment.setLayoutManager(new LinearLayoutManager(view.getContext()));
        _rv_bt_nearby_equipment.setAdapter(_bt_available_adapter);

        _bt_available_adapter.setOnConnectListener(this);

        // 注册蓝牙设备检索回调
        registerCallBack(BT_CALLBACK_TYPE.UPDATE_FOUNDED_DEVICES, (BTCallbackListener<List<BluetoothDevice>>) bluetoothDevices -> {
            List<BluetoothDevice> data = new ArrayList<>();
            data.addAll(bluetoothDevices);
            _bt_available_adapter.setNewData(data);
        });
        // 注册蓝牙状态回调
        registerCallBack(BT_CALLBACK_TYPE.UPDATE_BLUETOOTH_STATE_CHANGE, (BTCallbackListener<BusinessConstants.BTStateType>) type -> {
            AAOP_LogUtils.i(TAG, "BTStateType:" + type);
        });
        registerCallBack(BT_CALLBACK_TYPE.UPDATE_PAIRED_DEVICES, (BTCallbackListener<List<BluetoothDevice>>) devices -> _paired_devices = devices);
        // 开始扫描设备
        registerCallBack(BT_CALLBACK_TYPE.SHOW_SCANNING, nothing -> {
            Animation animation = new RotateAnimation(0f, -359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(1000L);
            animation.setRepeatCount(Animation.INFINITE);
            LinearInterpolator interpolator = new LinearInterpolator();
            animation.setInterpolator(interpolator);
        });
        // 刷新设备
        getBluetoothService().getScanningPresenter().startScan();
        //注册蓝牙连接回调
        registerCallBack(BT_CALLBACK_TYPE.UPDATE_CONNNECTIONS_STATUS, (BTCallbackListener<BluetoothDevice>) device -> {
            String address = device.getAddress();
            int state = device.getState();
            _bt_available_adapter.notifyDeviceStateChanged(address, state);
        });

    }

    @Override
    public void onBTClick(BluetoothDevice bluetoothDevice, String tvStatus) {
        LogUtil.i(TAG, " onBTClick ");
        LogUtil.i(TAG, "device.isHfpConnected " + bluetoothDevice.isHfpConnected());
        LogUtil.i(TAG, "bluetoothDevice.isA2dpConnected() " + bluetoothDevice.isA2dpConnected());
        if (tvStatus.equals(SystemUIApplication.getSystemUIContext().getString(R.string.screen_bluetooth_text))){
            if (bluetoothDevice.isA2dpConnected() || bluetoothDevice.isHfpConnected()) {
                getBluetoothService().getScanningPresenter().disconnectDevice(bluetoothDevice.getAddress());
            }
        }else {
            getBluetoothService().getPairedPresenter().connectDevice(bluetoothDevice.getAddress());
        }

    }


    @Override
    public void onConnect(BluetoothDevice device) {
        if (_paired_devices.size() >= 10) {

        } else {
            getBluetoothService().getScanningPresenter().connectDevice(device.getAddress());
        }
    }
}
