package com.adayo.systemui.bean;

import java.util.List;

public class SystemUISourceInfo {
    private String uiSource;
    private List<String> audioSource;

    public String getUiSource() {
        return uiSource;
    }

    public void setUiSource(String uiSource) {
        this.uiSource = uiSource;
    }

    public List<String> getAudioSource() {
        return audioSource;
    }

    public void setAudioSource(List<String> audioSource) {
        this.audioSource = audioSource;
    }

}
