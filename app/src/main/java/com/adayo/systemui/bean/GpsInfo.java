package com.adayo.systemui.bean;

import androidx.annotation.NonNull;

public class GpsInfo {
    private boolean gpsShow;

    public boolean getGpsShow() {
        return gpsShow;
    }

    public void setGpsShow(boolean state) {
        this.gpsShow = state;
    }

    @NonNull
    @Override
    public String toString() {
        String value = "gpsShow:" + gpsShow;
        return value;
    }
}
