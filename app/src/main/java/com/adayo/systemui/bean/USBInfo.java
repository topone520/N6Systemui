package com.adayo.systemui.bean;

public class USBInfo {
    private int deviceNum;
    private int mountNum;
    private boolean isInsert;

    public boolean isInsert() {
        return isInsert;
    }

    public void setInsert(boolean insert) {
        isInsert = insert;
    }

    public int getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(int deviceNum) {
        this.deviceNum = deviceNum;
    }

    public int getMountNum() {
        return mountNum;
    }

    public void setMountNum(int mountNum) {
        this.mountNum = mountNum;
    }
}
