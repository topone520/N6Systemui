package com.adayo.systemui.manager;

public interface HotspotController {
    boolean isHotspotEnabled();
    boolean isHotspotTransient();
    void setHotspotEnabled(boolean enabled);
    boolean isHotspotSupported();
    int getNumConnectedDevices();
    String getHotspotName();
    String getHotspotPassword();
    int getHotspotAPMode();
}
