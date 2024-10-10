package com.adayo.systemui.manager;

import com.adayo.systemui.bean.BluetoothInfo;

public interface BluetoothController {
    boolean isBTEnabled();
    void setBTEnabled(boolean enable);
    boolean isBTConnected();
    String getBTDevicesName();
    String getBTDevicesAddress();
    String getBTDevicesOperator(String address);
    BluetoothInfo getBluetoothInfo();
}
