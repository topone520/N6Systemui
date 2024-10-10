package com.adayo.systemui.bean;

public class HotspotInfo {
    private int mHotspotState;
    private int mNumConnectedDevices;
    private boolean mWaitingForCallback;
    private String mHotspotName;
    private String mHotspotPassword;
    private int mHotspotAPMode;

    public int getmHotspotState() {
        return mHotspotState;
    }

    public void setmHotspotState(int mHotspotState) {
        this.mHotspotState = mHotspotState;
    }

    public int getmNumConnectedDevices() {
        return mNumConnectedDevices;
    }

    public void setmNumConnectedDevices(int mNumConnectedDevices) {
        this.mNumConnectedDevices = mNumConnectedDevices;
    }

    public boolean ismWaitingForCallback() {
        return mWaitingForCallback;
    }

    public void setmWaitingForCallback(boolean mWaitingForCallback) {
        this.mWaitingForCallback = mWaitingForCallback;
    }

    public String getmHotspotName() {
        return mHotspotName;
    }

    public void setmHotspotName(String mHotspotName) {
        this.mHotspotName = mHotspotName;
    }

    public String getmHotspotPassword() {
        return mHotspotPassword;
    }

    public void setmHotspotPassword(String mHotspotPassword) {
        this.mHotspotPassword = mHotspotPassword;
    }

    public int getmHotspotAPMode() {
        return mHotspotAPMode;
    }

    public void setmHotspotAPMode(int mHotspotAPMode) {
        this.mHotspotAPMode = mHotspotAPMode;
    }
}
