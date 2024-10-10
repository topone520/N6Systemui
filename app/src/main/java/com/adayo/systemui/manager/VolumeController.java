package com.adayo.systemui.manager;

public interface VolumeController {
    int getVolume(int volumeType);
    void setVolume(int volumeType, int volume);
    int getSysMute();
    void setSysMute(int mute);
}
