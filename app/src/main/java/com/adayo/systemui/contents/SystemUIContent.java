package com.adayo.systemui.contents;

import static android.view.WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW;

import com.adayo.proxy.infrastructure.adayosource.AdayoSource;

public class SystemUIContent {
    public static final String TAG = "SYSTEMUI";
    public static final String PACKAGE_NAME = "com.adayo.app.systemui";

    public static final String[] sourceTypes = {AdayoSource.ADAYO_SOURCE_HOME, AdayoSource.ADAYO_SOURCE_BCM, AdayoSource.ADAYO_SOURCE_CAMERA, AdayoSource.ADAYO_SOURCE_SETTING};

    public static final String STATUS_BAR_SERVICE = "statusbar";
    public static final String NAVIGATION_BAR= "NavigationBar";
    public static final String DOCK_BAR= "dockbar";
    public static final String PARAMETER_KEY= "PARAMETER_KEY";
    public static final String DEVICE_INFO = "DeviceInfo";
    public static final int STANDBY = 0;
    public static final int CHARGING = 1;
    public static final int CHARGING_COMPLETED = 2;
    public static final int WPC_FAULT = 3;
    public static final int WPC_FAULT1 = 4;
    public static final int WPC_FAULT2 = 5;
    public static final int WPC_FAULT3 = 6;
    public static final int WPC_FAULT4= 7;
    public static final int WPC_FAULT5 = 8;
    public static final int WPC_FAULT6 = 9;

    public static final int TYPE_NAVIGATION_BAR = FIRST_SYSTEM_WINDOW+19;
    public static final int TYPE_NAVIGATION_BAR_PANEL = FIRST_SYSTEM_WINDOW+24;

    public static final String[] controlType = {"BLUETOOTH", "WIFI", "HOTSPOT", "SCREEN"};
    public static final int BLUETOOTH = 0;
    public static final int WIFI = 1;
    public static final int HOTSPOT = 2;
    public static final int SCREEN = 3;

    public static final int VOLUME= 0;
    public static final int BRIGHTNESS= 1;

    public static final int SYSTEM_STATUS_SCREEN = 1;
    public static final int SYSTEM_STATUS_POWER = 2;

    //Windows view type start
    public static final int TYPE_OF_DOCK_BAR = 0;
    public static final int TYPE_OF_STATUS_BAR = 1;
    public static final int TYPE_OF_NAVIGATION_BAR = 2;
    public static final int TYPE_OF_QS_PANEL = 3;
    public static final int TYPE_OF_HVAC_PANEL = 4;
    public static final int TYPE_OF_VOLUME_DIALOG = 5;
    public static final int TYPE_OF_SCREENT_OFF = 6;
    //Windows view type end

    //DELAYED time start
    public static final int RETRY_DELAYED = 1000;
    public static final int HVAC_DISMISS_DELAYED = 10000;
    public static final int HVAC_POPUP_DELAYED = 5000;
    public static final int VOLUME_POPUP_DELAYED = 3000;
    //DELAYED time end

    //type of ShareInfo start
    public static final int SHARE_DATA_VOLUME_ID = 7;
    public static final int SHARE_DATA_USB_DEVICE_ID = 17;
    public static final int SHARE_DATA_TBOX_SHARE_DATA_ID = 73;
    public static final int SHARE_DATA_SOURCE_MANAGER_ID = 14;
    public static final int SHARE_DATA_BRIGHTNESS_ID = 10;
    public static final int SHARE_DATA_UPGRADE_ID = 305;
    public static final int BACK_CAR_SHARE_ID = 16;
    public static final int BT_CALL_SHARE_ID = 23;
    public static final int DVR_SHARE_ID = 74;
    public static final int SYSTEMUI_SHARE_ID = 87;
    public static final int BLUETOOTH_SHARE_ID = 27;
    public static final int INSTALL_APP_STATE = 106;
    //type of ShareInfo end

    //hotspot enabled id
    public static final int WIFI_AP_STATE_ENABLED = 13;

    //Volume type start
    //    public static final int SETTING_STREAM_TYPE_TBOX = 0;    //tbox音频流
    public static final int SETTING_STREAM_TYPE_MEDIA = 3;    //媒体音频流
    public static final int SETTING_STREAM_TYPE_PHONE = 6;    //电话音频流
    public static final int SETTING_STREAM_TYPE_TTS = 9;    //语音音频流
    public static final int SETTING_STREAM_TYPE_NAVI = 11;    //导航音频流
    public static final int SETTING_STREAM_TYPE_RING = 2;    //铃声音频流
    public static final int SETTING_STREAM_TYPE_BT_MEDIA = 15;    //蓝牙音乐音频流

    public static final int SETTING_STREAM_SYSTEM = 1;  //系统
    public static final int SETTING_STREAM_BLUETOOTH_SCO = 6;     //通话
    public static final int SETTING_STREAM_MUSIC = 3;  //媒体
    public static final int SETTING_STREAM_TTS = 9;    //语音
    public static final int SETTING_STREAM_NAVI = 11;    //导航

    //Volume type end

    public static final int SYSTEM_PHONE_FREE = 7;    //蓝牙通话空闲

    /**
     * 屏幕状态
     */
    public static final int SCREEN_NORMAL = 0;
    public static final int SCREEN_OFF = 1;
    public static final int SCREEN_POWER_OFF = 2;
    public static final int SCREEN_FACKSHUT = 3;
    public static final int SCREEN_STRSHUTDOWN = 4;
    public static final int SCREEN_SHUTDOWN = 5;
    public static final int POWER_TYPE_CLOCK = 1;
    public static final int POWER_TYPE_BLACK = 3;
    public static final int POWER_TYPE_LOGO = 4;

    public static final int SETTING_LOCK_ID_CLOCK = 4;
    public static final int SETTING_MUTE = 1;
    public static final int SETTING_UNMUTE = 0;
    public static final int SETTING_LOCK = 1;
    public static final int SETTING_UNLOCK = 0;

    public static final String ACTION_SHOW_HVAC_PANEL = "ADAYO_HVAC";
    public static final String ACTION_UPDATE_QS_PANEL = "com.adayo.systemui.widget.ACTION_SHOW_QS_PANEL";
    public static final String ACTION_SHOW_VOLUME_PANEL = "com.adayo.systemui.action.ACTION_SHOW_VOLUME_PANEL";
    public static final String ACTION_HIDE_PANELS = "com.adayo.systemui.action.ACTION_HIDE_PANELS";
    public static final String ADAYO_NAVI_SERVICE_SEND = "ADAYO_NAVI_SERVICE_SEND";

    public static final String NAVI_APP_STATE = "NAVI_APP_STATE";

//    public static final String ACTION_UPDATE_STATUS = "com.adayo.systemui.widget.UPDATE_STATUS";
//    public static final String ACTION_UPDATE_WIND_MODE  = "com.adayo.systemui.widget.UPDATE_WIND_MODE";
//    public static final String ACTION_UPDATE_LEFT_TEMP  = "com.adayo.systemui.widget.UPDATE_LEFT_TEMP";
//    public static final String ACTION_UPDATE_RIGHT_TEMP  = "com.adayo.systemui.widget.UPDATE_RIGHT_TEMP";
//    public static final String ACTION_UPDATE_WIND_VALUE  = "com.adayo.systemui.widget.UPDATE_WIND_VALUE";
//    public static final String ACTION_UPDATE_CYCLE_MODE  = "com.adayo.systemui.widget.UPDATE_CYCLE_MODE";
//    public static final String ACTION_UPDATE_FRONT_DEFROST  = "com.adayo.systemui.widget.UPDATE_FRONT_DEFROST";

    public static final int NAVI_EXIT = 2;
    public static final int NAVI_START = 3;

//    public static final int PEPS_POWER_MODE = VehiclePropertyIds.PEPS_POWER_MODE;

    public static final int MESSAGE_TYPE_TRAFFIC = 0;
    public static final int MESSAGE_TYPE_TSP = 1;
    public static final int MESSAGE_TYPE_OTA = 2;



    public static final String BDL_KEY_SETTING_PAGE = "setting_page";//从外界进入app的tag
    public static final String BDL_VALUE_SETTING_PAGE_BT = "bt";//蓝牙
    public static final String BDL_VALUE_SETTING_PAGE_WIFI = "wifi";//wif
    public static final String BDL_VALUE_SETTING_PAGE_AP = "ap";//热点


    public static final String INTELLIGENT_ASSISTANT_REMIND_CLASS_TO_BROADCAST_SWITCH = "intelligent_assistant_remind_class_to_broadcast_switch";//提醒类播报开关
    public static final int INTELLIGENT_ASSISTANT_REMIND_CLASS_TO_BROADCAST_DEFAULT_VALUE = 1;//提醒类播报开关默认值

}
