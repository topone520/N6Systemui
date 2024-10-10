package com.adayo.systemui.utils;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.view.Display;

import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.proxy.infrastructure.sourcemng.Beans.SourceInfo;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;
import com.android.systemui.SystemUIApplication;

import java.util.HashMap;

public class DisplayUtils {
    public static Display displays[];
    private static SourceInfo sourceInfoHome, sourceInfoView, sourceInfoVoice;

    public static void initDisplay() {
        DisplayManager mDisplayManager = (DisplayManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.DISPLAY_SERVICE);
        displays = mDisplayManager.getDisplays();
    }

    public static Display getDisplay() {
        return displays[1];
    }

    public static void initIntentSetting() {
        HashMap<String, String> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put("type", "top");
        sourceInfoHome = new SourceInfo(AdayoSource.ADAYO_SOURCE_SETTING, "com.adayo.vtp.top", stringIntegerHashMap,
                AppConfigType.SourceSwitch.APP_ON.getValue(), AppConfigType.SourceType.UI.getValue(), 1, null);

        HashMap<String, String> stringIntegerHashMap1 = new HashMap<>();
        stringIntegerHashMap1.put("type", "setting");
        sourceInfoView = new SourceInfo(AdayoSource.ADAYO_SOURCE_SETTING, "com.adayo.vtp.setting", stringIntegerHashMap1,
                AppConfigType.SourceSwitch.APP_ON.getValue(), AppConfigType.SourceType.UI.getValue(), 1, null);

        HashMap<String, String> stringIntegerHashMap2 = new HashMap<>();
        stringIntegerHashMap2.put("type", "voice");
        sourceInfoVoice = new SourceInfo(AdayoSource.ADAYO_SOURCE_BCM, "com.adayo.vtp.setting", stringIntegerHashMap2,
                AppConfigType.SourceSwitch.APP_ON.getValue(), AppConfigType.SourceType.UI.getValue(), 1, null);
    }

    public static void intentSettingHome() {
        SrcMngSwitchManager.getInstance().requestSwitchApp(sourceInfoHome);
    }

    public static void intentSettingView() {
        SrcMngSwitchManager.getInstance().requestSwitchApp(sourceInfoView);
    }

    public static void intentSettingVoice() {
        SrcMngSwitchManager.getInstance().requestSwitchApp(sourceInfoVoice);
    }
}
