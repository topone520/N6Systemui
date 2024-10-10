package com.adayo.systemui.manager;

import com.adayo.proxy.infrastructure.sourcemng.Interface.IAdayoAudioFocusChange;

import java.util.List;
import java.util.Map;

public interface SourceController {
    String getCurrentUISource();
    List<String> getCurrentAudioSource();
    void requestSource(int sourceSwitch, String sourceType, Map<String, String> map);
    void requestSoureApp(String packageName,String action,int sourceSwitch,Map<String, String> map);
    void requestAdayoAudioFocus(String audioFocus,IAdayoAudioFocusChange iAdayoAudioFocusChange);
    void abandonAdayoAudioFocus(IAdayoAudioFocusChange iAdayoAudioFocusChange);
}
