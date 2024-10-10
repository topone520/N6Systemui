package com.adayo.systemui.bean;

import java.io.Serializable;

public class VolumeInfo implements Serializable {
    private int systemMute;
    private int mediaVolume;
    private int bluetoothVolume;
    private int naviVolume;
    private int bellVolume;
    private int voiceVolume;
    private int notificationVolume;
    private int alarmVolume;

    public int getSystemMute() {
        return systemMute;
    }

    public void setSystemMute(int systemMute) {
        this.systemMute = systemMute;
    }

    public int getMediaVolume() {
        return mediaVolume;
    }

    public void setMediaVolume(int mediaVolume) {
        this.mediaVolume = mediaVolume;
    }

    public int getBluetoothVolume() {
        return bluetoothVolume;
    }

    public void setBluetoothVolume(int bluetoothVolume) {
        this.bluetoothVolume = bluetoothVolume;
    }

    public int getNaviVolume() {
        return naviVolume;
    }

    public void setNaviVolume(int naviVolume) {
        this.naviVolume = naviVolume;
    }

    public int getBellVolume() {
        return bellVolume;
    }

    public void setBellVolume(int bellVolume) {
        this.bellVolume = bellVolume;
    }

    public int getVoiceVolume() {
        return voiceVolume;
    }

    public void setVoiceVolume(int voiceVolume) {
        this.voiceVolume = voiceVolume;
    }

    public int getNotificationVolume() {
        return notificationVolume;
    }

    public void setNotificationVolume(int notificationVolume) {
        this.notificationVolume = notificationVolume;
    }

    public int getAlarmVolume() {
        return alarmVolume;
    }

    public void setAlarmVolume(int alarmVolume) {
        this.alarmVolume = alarmVolume;
    }
}
