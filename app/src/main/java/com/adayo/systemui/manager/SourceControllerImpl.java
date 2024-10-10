package com.adayo.systemui.manager;

import static com.adayo.proxy.infrastructure.sourcemng.Beans.SourceConstants.DISP_A;
import static com.adayo.proxy.infrastructure.sourcemng.Beans.SourceConstants.SOURCE_INFO_CURRENT_UID;
import static com.adayo.proxy.infrastructure.sourcemng.Beans.SourceConstants.ZONE_A;

import android.media.AudioManager;
import android.text.TextUtils;

import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.share.ShareDataManager;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AudioCbInfo;
import com.adayo.proxy.infrastructure.sourcemng.Beans.SourceConstants;
import com.adayo.proxy.infrastructure.sourcemng.Beans.SourceInfo;
import com.adayo.proxy.infrastructure.sourcemng.Beans.UIDCbInfo;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngAudioSwitchManager;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSourceInfoMng;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;
import com.adayo.proxy.infrastructure.sourcemng.Interface.IAdayoAudioFocusChange;
import com.adayo.proxy.infrastructure.sourcemng.Interface.IAdayoSourceListener;
import com.adayo.systemui.bases.BaseControllerImpl;
import com.adayo.systemui.bean.SystemUISourceInfo;
import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.SystemUIApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SourceControllerImpl extends BaseControllerImpl<SystemUISourceInfo> implements SourceController, IAdayoSourceListener {
    private volatile static SourceControllerImpl sourceController;
    private ShareDataManager shareDataManager;
    private SrcMngSwitchManager srcMngSwitchManager;
    private SrcMngAudioSwitchManager srcMngAudioSwitchManager;
    private SystemUISourceInfo sourceInfo = new SystemUISourceInfo();

    private SourceControllerImpl() {
        shareDataManager = ShareDataManager.getShareDataManager();
        srcMngSwitchManager = SrcMngSwitchManager.getInstance();
        srcMngAudioSwitchManager = SrcMngAudioSwitchManager.getInstance();
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);
    }

    public static SourceControllerImpl getInstance() {
        if (sourceController == null) {
            synchronized (SourceControllerImpl.class) {
                if (sourceController == null) {
                    sourceController = new SourceControllerImpl();
                }
            }
        }
        return sourceController;
    }

    @Override
    public String getCurrentUISource() {
        if (null != srcMngSwitchManager) {
            String uid = srcMngSwitchManager.getCurrentUID();
            if(TextUtils.equals("ADAYO_SOURCE_MULTIMEDIA",uid)
                    ||TextUtils.equals("ADAYO_SOURCE_USB",uid)
                    ||TextUtils.equals("ADAYO_SOURCE_BT_AUDIO",uid)
                    ||TextUtils.equals("ADAYO_SOURCE_RADIO",uid)
                    ||TextUtils.equals("ADAYO_SOURCE_ONLINE_MUSIC_1",uid)
                    ||TextUtils.equals("ADAYO_SOURCE_NET_RADIO",uid)){
                uid = "ADAYO_SOURCE_MULTIMEDIA";
            }
            return uid;
        }
        return AdayoSource.ADAYO_SOURCE_HOME;
    }

    @Override
    public List<String> getCurrentAudioSource() {
        if (null != srcMngSwitchManager) {
            return srcMngSwitchManager.getCurrentAudioFocus();
        }
        return null;
    }

    @Override
    public void requestSource(int sourceSwitch, String sourceType, Map<String, String> map) {
        try {
            SourceInfo info = new SourceInfo(sourceType, map, sourceSwitch,
                    AppConfigType.SourceType.UI.getValue());
            SrcMngSwitchManager srcMngSwitchManager = SrcMngSwitchManager.getInstance();
            if(null != srcMngSwitchManager) {
//                reSwitchApp(info);
                srcMngSwitchManager.requestSwitchApp(info);
            }
        } catch (Exception e) {
            LogUtil.w(e.getMessage());
        }
    }

    @Override 
    public void requestSoureApp(String action, String packageName, int sourceSwitch, Map<String, String> map) {
        try {
            SourceInfo info = new SourceInfo(packageName,map,sourceSwitch, AppConfigType.SourceType.UI.getValue());
            SrcMngSwitchManager srcMngSwitchManager = SrcMngSwitchManager.getInstance();
            if(null != srcMngSwitchManager) {
                srcMngSwitchManager.requestSwitchApp(info);
            }
        } catch (Exception e) {
            LogUtil.w(e.getMessage());
        }
    }

    @Override
    public void requestAdayoAudioFocus(String audioFocus,IAdayoAudioFocusChange iAdayoAudioFocusChange) {
        if (null != srcMngAudioSwitchManager) {
            synchronized (srcMngAudioSwitchManager) {
                LogUtil.debugD(audioFocus);
                srcMngAudioSwitchManager.requestAdayoAudioFocus(AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT, audioFocus, iAdayoAudioFocusChange,
                        SystemUIApplication.getSystemUIContext());
            }
        }
    }

    @Override
    public void abandonAdayoAudioFocus(IAdayoAudioFocusChange iAdayoAudioFocusChange) {
        if (null != iAdayoAudioFocusChange) {
            if (null != srcMngAudioSwitchManager) {
                synchronized (srcMngAudioSwitchManager) {
                    srcMngAudioSwitchManager.abandonAdayoAudioFocus(iAdayoAudioFocusChange);
                }
            }
        }
    }

    private static HashMap<String, IAdayoAudioFocusChange> listenerMap = new HashMap();

    public void abandonAdayoAudioFocus(String audioFocus) {
        LogUtil.debugD(audioFocus);
        IAdayoAudioFocusChange iAdayoAudioFocusChange = listenerMap.get(audioFocus);
        if (null != iAdayoAudioFocusChange) {
            synchronized (srcMngAudioSwitchManager) {
                srcMngAudioSwitchManager.abandonAdayoAudioFocus(iAdayoAudioFocusChange);
            }
            listenerMap.remove(audioFocus);
        }
    }

    public void requestAdayoAudioFocus(String audioFocus) {
        LogUtil.debugD(audioFocus);
        IAdayoAudioFocusChange iAdayoAudioFocusChange = listenerMap.get(audioFocus);
        if (null == iAdayoAudioFocusChange) {
            iAdayoAudioFocusChange = new IAdayoAudioFocusChange() {
                @Override
                public void onAdayoAudioOnGain() {
                }

                @Override
                public void onAdayoAudioOnLoss() {
                }

                @Override
                public void onAdayoAudioLossTransient() {
                }

                @Override
                public void onAdayoAudioLossTransientCanDuck() {
                }
            };
            listenerMap.put(audioFocus, iAdayoAudioFocusChange);
        }
        synchronized (srcMngAudioSwitchManager) {
            srcMngAudioSwitchManager.requestAdayoAudioFocus(AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN, audioFocus, iAdayoAudioFocusChange,
                    SystemUIApplication.getSystemUIContext());
        }
    }

    @Override
    protected boolean registerListener() {
        return SourceConstants.SUCCESS == SrcMngSourceInfoMng.getInstance().registerSourceListener(this, SOURCE_INFO_CURRENT_UID);
    }

    @Override
    protected SystemUISourceInfo getDataInfo() {
        if(null == sourceInfo){
            sourceInfo = new SystemUISourceInfo();
            sourceInfo.setUiSource(getCurrentUISource());
            sourceInfo.setAudioSource(getCurrentAudioSource());
        }
        return sourceInfo;
    }

    @Override
    public void notifyAudioListChange(Map<String, List<AudioCbInfo>> map) {
        LogUtil.debugD(map.toString());
        List audioSource = getCurrentAudioSource();
        if(null != sourceInfo && audioSource.size() == sourceInfo.getAudioSource().size() && audioSource.containsAll(sourceInfo.getAudioSource())) {
            return;
        }
        sourceInfo.setAudioSource(audioSource);
        mHandler.removeMessages(NOTIFY_CALLBACKS);
        mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
    }

    @Override
    public void notifyUIList(Map<String, List<UIDCbInfo>> map) {
        LogUtil.debugD(map.toString());
        String uiSource = getCurrentUISource();
        if(null != sourceInfo && !TextUtils.isEmpty(uiSource) && uiSource.equals(sourceInfo.getUiSource())){
            return;
        }
        sourceInfo.setUiSource(uiSource);
        mHandler.removeMessages(NOTIFY_CALLBACKS);
        mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
    }
}
