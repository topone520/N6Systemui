package com.adayo.systemui.bean;

import androidx.annotation.NonNull;

public class SignalInfo {
    private boolean mSignalSwitchState;
    private int mSignalLevel;
    private int mSignalType;

    public boolean getSignalSwitchState() {
        return mSignalSwitchState;
    }

    public void setSignalSwitchState(boolean state) {
        this.mSignalSwitchState = state;
    }

    public int getSignalLevel() {
        return mSignalLevel;
    }

    public void setSignalLevel(int level) {
        this.mSignalLevel = level;
    }

    public int getSignalType() {
        return mSignalType;
    }

    public void setSignalType(int type) {
        this.mSignalType = type;
    }

    @NonNull
    @Override
    public String toString() {
        String value = "SignalSwitchState:" + mSignalSwitchState + ", " +
                        "SignalLevel:" + mSignalLevel + ", " +
                        "SignalType:" + mSignalType;
        return value;
    }
}
