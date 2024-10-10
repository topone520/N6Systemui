package com.adayo.systemui.bean;

public class SystemInfo {
    /**
     *     public static final int SCREEN_NORMAL = 0;
     *     public static final int SCREEN_OFF = 1;
     *     public static final int SCREEN_POWER_OFF = 2;
     *     public static final int SCREEN_FACKSHUT = 3;
     */

    private int systemStatus;

    public int getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(int systemStatus) {
        this.systemStatus = systemStatus;
    }
}
