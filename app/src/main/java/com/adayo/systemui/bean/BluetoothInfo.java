package com.adayo.systemui.bean;

public class BluetoothInfo {
    private int state;
    private boolean enable;
    private boolean hfpMount;
    private boolean a2dpMount;
    private String deviceName;
    private String address;
    private String operator;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isHfpMount() {
        return hfpMount;
    }

    public void setHfpMount(boolean hfpMount) {
        this.hfpMount = hfpMount;
    }

    public boolean isA2dpMount() {
        return a2dpMount;
    }

    public void setA2dpMount(boolean a2dpMount) {
        this.a2dpMount = a2dpMount;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
