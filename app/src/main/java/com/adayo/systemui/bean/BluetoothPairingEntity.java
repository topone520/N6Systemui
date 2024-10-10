package com.adayo.systemui.bean;

import android.bluetooth.BluetoothDevice;

/**
 * 蓝牙配对信息
 */
public class BluetoothPairingEntity {

    private BluetoothDevice device;
    private int type;
    private String key;

    public BluetoothPairingEntity(BluetoothDevice device, int type, String key) {
        this.device = device;
        this.type = type;
        this.key = key;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return "BluetoothPairingEntity{" +
                "device=" + device.getName() +
                ", type=" + type +
                ", key='" + key + '\'' +
                '}';
    }
}

