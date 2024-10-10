package com.adayo.systemui.manager;

public interface WiFiController {
    boolean isWiFiEnable();
    void setWiFiEnable(boolean enable);
    boolean isWiFiConnected();
    int getRssi();
    String getSsid();
}
