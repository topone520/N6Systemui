package com.adayo.systemui.manager;

import static com.adayo.proxy.setting.system.contants.SettingsContantsDef.SETTING_STREAM_BLUETOOTH_SCO;
import static com.adayo.proxy.setting.system.contants.SettingsContantsDef.SETTING_STREAM_MUSIC;
import static com.adayo.proxy.setting.system.contants.SettingsContantsDef.SETTING_STREAM_NAVI;
import static com.adayo.proxy.setting.system.contants.SettingsContantsDef.SETTING_STREAM_RADAR;
import static com.adayo.proxy.setting.system.contants.SettingsContantsDef.SETTING_STREAM_RING;
import static com.adayo.proxy.setting.system.contants.SettingsContantsDef.SETTING_STREAM_SYSTEM;
import static com.adayo.proxy.setting.system.contants.SettingsContantsDef.SETTING_STREAM_TTS;
import static com.adayo.systemui.contents.PublicContents.AUDIO_DEVICE;
import static com.adayo.systemui.contents.PublicContents.PACKAGE_NAME;

import android.os.Bundle;

import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.deviceservice.IDeviceFuncCallBack;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.systemui.bases.BaseControllerImpl;
import com.adayo.systemui.bean.VolumeInfo;
import com.adayo.systemui.utils.LogUtil;

public class VolumeControllerImpl extends BaseControllerImpl<VolumeInfo> implements VolumeController {
    /**
     * 音频流常量
     public static final int SETTING_STREAM_SYSTEM = 1;                //系统音量（按键音音量）
     public static final int SETTING_STREAM_RING = 2;                //电话铃声音量
     public static final int SETTING_STREAM_MUSIC = 3;                //媒体音量（USB音乐音量）
     public static final int SETTING_STREAM_BLUETOOTH_SCO = 6;//蓝牙电话音量
     public static final int SETTING_STREAM_TTS = 9;                        //语音播报音量
     public static final int SETTING_STREAM_NAVI = 11;                //导航播报音量
     public static final int SETTING_STREAM_INSTRUMENT = 13;        //仪表报警音量（通常与雷达报警音量同步）

     public static final int SETTING_STREAM_VOICE_CALL = 0;         //TBOX电话音量
     public static final int SETTING_STREAM_ALARM = 4;                //系统报警音（一般不用）
     public static final int SETTING_STREAM_NOTIFICATION = 5;//通知音（一般就微信用）
     public static final int SETTING_STREAM_SYSTEM_ENFORCED = 7;//系统强制音量（过往项目没用过）
     public static final int SETTING_STREAM_DTMF = 8;                //这个不知道是啥音量（过往项目没用过）
     public static final int SETTING_STREAM_RESVERED = 10;        //覆盖音量（过往项目没用过）
     public static final int SETTING_STREAM_RADAR = 12;                //雷达报警音量
    public static final int SETTING_STREAM_VOICE_CAR = 14;        //对讲机音量（通常与语音音量同步）
    public static final int SETTING_STREAM_MUSIC_BT = 15;        //蓝牙音乐音量
    public static final int SETTING_STREAM_AUX_IN = 16;                //AUX IN音量（通常与媒体音量同步）
    public static final int SETTING_STREAM_VR = 17;                        //微信语音播报（通常与语音音量同步）
    public static final int SETTING_STREAM_FM = 18;                        //收音音量（通常与媒体音量同步）
     */
    private volatile static VolumeControllerImpl mVolumeControllerImpl;
    private AAOP_DeviceServiceManager mSettingsSvcIfManager;
    private ShareDataManager mShareDataManager;

    private VolumeInfo volumeInfo;

    private VolumeControllerImpl() {
        mSettingsSvcIfManager = AAOP_DeviceServiceManager.getInstance();
        mShareDataManager = ShareDataManager.getShareDataManager();
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);
    }

    public static VolumeControllerImpl getInstance() {
        if (mVolumeControllerImpl == null) {
            synchronized (VolumeControllerImpl.class) {
                if (mVolumeControllerImpl == null) {
                    mVolumeControllerImpl = new VolumeControllerImpl();
                }
            }
        }
        return mVolumeControllerImpl;
    }

    @Override
    protected boolean registerListener() {
        int result = AAOP_DeviceServiceManager.getInstance().registerDeviceFuncListener(new IDeviceFuncCallBack.Stub() {
            @Override
            public int onChangeListener(Bundle bundle) {
                LogUtil.d("onChangeListener bundle.toString() = " + bundle.toString());
                if(bundle.containsKey("set_audio_stream_volume")) {
                    int[] audioStreamArr = bundle.getIntArray("set_audio_stream_volume");
                    // 更新dock栏音量UI
                    if (audioStreamArr != null) {
                        if(null == volumeInfo){
                            volumeInfo = new VolumeInfo();
                        }
                        volumeInfo.setSystemMute(AAOP_DeviceServiceManager.getInstance().getSysMute());
                        volumeInfo.setMediaVolume(audioStreamArr[SETTING_STREAM_MUSIC]);
                        volumeInfo.setBluetoothVolume(audioStreamArr[SETTING_STREAM_BLUETOOTH_SCO]);
                        volumeInfo.setNaviVolume(audioStreamArr[SETTING_STREAM_NAVI]);
                        volumeInfo.setBellVolume(audioStreamArr[SETTING_STREAM_RING]);
                        volumeInfo.setVoiceVolume(audioStreamArr[SETTING_STREAM_TTS]);
                        volumeInfo.setNotificationVolume(audioStreamArr[SETTING_STREAM_SYSTEM]);
                        volumeInfo.setAlarmVolume(audioStreamArr[SETTING_STREAM_RADAR]);
                    }
                    mHandler.removeMessages(NOTIFY_CALLBACKS);
                    mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
                }
                return 0;
            }
        }, PACKAGE_NAME, AUDIO_DEVICE);
        return result == -1000;
    }

    @Override
    protected VolumeInfo getDataInfo() {
        if(null == volumeInfo){
            volumeInfo = new VolumeInfo();
        }
        LogUtil.d("ccm------> onDataChange =  ");
        volumeInfo.setSystemMute(AAOP_DeviceServiceManager.getInstance().getSysMute());
        volumeInfo.setMediaVolume(AAOP_DeviceServiceManager.getInstance().getAudioStreamVolume(SETTING_STREAM_MUSIC));
        volumeInfo.setBluetoothVolume(AAOP_DeviceServiceManager.getInstance().getAudioStreamVolume(SETTING_STREAM_BLUETOOTH_SCO));
        volumeInfo.setNaviVolume(AAOP_DeviceServiceManager.getInstance().getAudioStreamVolume(SETTING_STREAM_NAVI));
        volumeInfo.setBellVolume(AAOP_DeviceServiceManager.getInstance().getAudioStreamVolume(SETTING_STREAM_RING));
        volumeInfo.setVoiceVolume(AAOP_DeviceServiceManager.getInstance().getAudioStreamVolume(SETTING_STREAM_TTS));
        volumeInfo.setNotificationVolume(AAOP_DeviceServiceManager.getInstance().getAudioStreamVolume(SETTING_STREAM_SYSTEM));
        volumeInfo.setAlarmVolume(AAOP_DeviceServiceManager.getInstance().getAudioStreamVolume(SETTING_STREAM_RADAR));
        return volumeInfo;
    }

    public VolumeInfo getCurrentDataInfo(){
        return volumeInfo;
    }

    @Override
    public int getVolume(int volumeType) {
        if (null != mSettingsSvcIfManager) {
            return mSettingsSvcIfManager.getAudioStreamVolume(volumeType);
        }
        return 0;
    }

    @Override
    public void setVolume(int volumeType, int volume) {
        if (null != mSettingsSvcIfManager) {
            mSettingsSvcIfManager.setAudioStreamVolume(volumeType, volume);
        }
    }

    @Override
    public int getSysMute() {
        if (null != mSettingsSvcIfManager) {
            return mSettingsSvcIfManager.getSysMute();
        }
        return 0;
    }

    @Override
    public void setSysMute(int mute) {
        if (null != mSettingsSvcIfManager) {
            mSettingsSvcIfManager.setSysMute(mute);
        }
    }
}
