package com.adayo.systemui.manager;

public interface SignalController {
    boolean isSignalEnabled();

    void setSignalEnabled(boolean enabled);

    int getSignalLevel();

    int getSignalType();
}
