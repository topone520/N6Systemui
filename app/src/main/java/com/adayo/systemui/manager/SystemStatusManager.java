package com.adayo.systemui.manager;

import static com.adayo.systemui.contents.PublicContents.SETTING_LOCK;
import static com.adayo.systemui.contents.PublicContents.SETTING_LOCK_ID_CLOCK;
import static com.adayo.systemui.contents.PublicContents.SETTING_MUTE;
import static com.adayo.systemui.contents.PublicContents.SETTING_UNLOCK;
import static com.adayo.systemui.contents.PublicContents.SETTING_UNMUTE;
import static com.adayo.systemui.contents.SystemContents.SCREEN_NORMAL;
import static com.adayo.systemui.contents.SystemContents.SCREEN_OFF;
import static com.adayo.systemui.contents.SystemContents.SCREEN_POWER_OFF;
import static com.adayo.systemui.contents.SystemContents.SCREEN_SHUTDOWN;
import static com.adayo.systemui.contents.SystemContents.SCREEN_STANDBY;
import static com.adayo.systemui.contents.SystemContents.SCREEN_STR_SHUTDOWN;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.system.aaop_systemservice.AAOP_SystemServiceManager;
import com.adayo.proxy.system.aaop_systemservice.IAAOP_SystemServiceCallBack;
import com.adayo.proxy.system.aaop_systemservice.bean.AAOP_RelyInfoEntry;
import com.adayo.proxy.system.aaop_systemservice.bean.AAOP_ServiceInfoEntry;
import com.adayo.proxy.system.aaop_systemservice.contants.AAOP_SystemServiceContantsDef;
import com.adayo.systemui.bean.SystemInfo;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.utils.WindowsUtils;

import java.util.ArrayList;

/**
 * @author XuYue
 * @description:
 * @date :2021/10/20 10:47
 */
public class SystemStatusManager extends IAAOP_SystemServiceCallBack.Stub {
    private volatile static SystemStatusManager systemStatusManager;
    private static final String DISPLAY_DEVICE = "DisplayDevice";
    private static final String SET_BACK_LIGHT_SWITCH = "set_back_light_switch";

    private final ArrayList<ServiceStatusCallBack> mCallbacks = new ArrayList<>();

    private final ArrayList<SystemStatusCallBack> systemStatusCallBacks = new ArrayList<>();
    private final ArrayList<SystemStatusCallBack> systemStatusCallBacksTemporary = new ArrayList<>();
    private AAOP_ServiceInfoEntry myServiceInfoEntry;
    private int systemStatus;

    private AAOP_SystemServiceManager systemServiceManager;
    private SystemInfo systemInfo = new SystemInfo();

    private SystemStatusManager() {
        try {
            initSystemStatus();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static SystemStatusManager getInstance() {
        if (systemStatusManager == null) {
            synchronized (SystemStatusManager.class) {
                if (systemStatusManager == null) {
                    systemStatusManager = new SystemStatusManager();
                }
            }
        }
        return systemStatusManager;
    }

    public int getSystemStatus() {
        return systemInfo.getSystemStatus();
    }

    public void setSystemStatus(AAOP_SystemServiceContantsDef.AAOP_SCREEN_STATUS status) {
        LogUtil.debugD(status.getValue() + "");
        if (null != systemServiceManager) {
            try {
                systemServiceManager.setScreenState(status);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void initSystemStatus() throws RemoteException {
        systemServiceManager = AAOP_SystemServiceManager.getInstance();
        ArrayList<AAOP_RelyInfoEntry> relyInfoEntries = new ArrayList<>();
        boolean registerRes = systemServiceManager.registerAAOPSystemServiceAPP(this, relyInfoEntries);
        onNotifySystemState(systemServiceManager.getSystemState().getValue());
        LogUtil.debugD("System Status = " + systemInfo.getSystemStatus() + " ; registerRes = " + registerRes);



    }

    private final int UPDATE_UI = 10001;
    private final int NOTIFY_SERVICE_STATUS = 10002;
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_UI:
                    updateViews(msg.arg1);
                    break;
                case NOTIFY_SERVICE_STATUS:
                    notifyServiceStatus();
                    break;
                default:
                    break;
            }
        }
    };

    private void notifyServiceStatus(){
        synchronized (lock) {
            for (ServiceStatusCallBack mCallBack : mCallbacks) {
                notifyCallback(mCallBack);
            }
        }
    }

    private void notifyCallback(ServiceStatusCallBack callBack){
        callBack.notifyServiceStatus(myServiceInfoEntry);
    }

    private static final  Object lock = new Object();
    public void addCallback(ServiceStatusCallBack callBack) {
        synchronized (lock) {
            if (null != callBack && !mCallbacks.contains(callBack)) {
                mCallbacks.add(callBack);
                notifyCallback(callBack);
            }
        }
    }

    public void addSystemCallback(SystemStatusCallBack callBack){
        synchronized (lock) {
            if (null != callBack && !systemStatusCallBacks.contains(callBack)) {
                systemStatusCallBacks.add(callBack);
                notifySystemCallback(callBack);
            }
        }
    }
    private void notifySystemStatus(){
        synchronized (lock) {
            systemStatusCallBacksTemporary.clear();
            systemStatusCallBacksTemporary.addAll(systemStatusCallBacks);
            for (SystemStatusCallBack mCallBack : systemStatusCallBacksTemporary) {
                notifySystemCallback(mCallBack);
            }

        }
    }

    public void removeSystemCallback(SystemStatusCallBack callback) {
        LogUtil.d(" removeCallback size" + systemStatusCallBacks.size());
        synchronized (lock) {
            if (systemStatusCallBacks.contains(callback)) {
                systemStatusCallBacks.remove(callback);
            }

        }
    }


    private void notifySystemCallback(SystemStatusCallBack callBack){
        callBack.notifySystemStaus(systemStatus);
    }


    private void updateViews(int value){
        if (value == AAOP_SystemServiceContantsDef.AAOP_SYSTEM_STATUS.AAOP_STATUS_SCREENOFF.getValue()) {
            systemStatus = SCREEN_OFF;
        } else if (value == AAOP_SystemServiceContantsDef.AAOP_SYSTEM_STATUS.AAOP_STATUS_POWEROFF.getValue()) {
            systemStatus = SCREEN_POWER_OFF;
        } else if (value == AAOP_SystemServiceContantsDef.AAOP_SYSTEM_STATUS.AAOP_STATUS_FACKESHUT.getValue()){
            systemStatus = SCREEN_STANDBY;
        } else if (value == AAOP_SystemServiceContantsDef.AAOP_SYSTEM_STATUS.AAOP_STATUS_SHUTDOWN.getValue()){
            systemStatus = SCREEN_SHUTDOWN;
        } else if (value == AAOP_SystemServiceContantsDef.AAOP_SYSTEM_STATUS.AAOP_STATUS_MAX.getValue()){
            systemStatus = SCREEN_STR_SHUTDOWN;
        } else {
            systemStatus = SCREEN_NORMAL;
        }
        systemInfo.setSystemStatus(systemStatus);
        WindowsUtils.setSystemStatus(systemStatus);
        changeStatus();
    }

    @Override
    public void onNotifySystemState(int i) {
        LogUtil.debugD("System Status = " + i);
        Message msgVolume = Message.obtain();
        msgVolume.what = UPDATE_UI;
        msgVolume.arg1 = i;
        mHandler.sendMessage(msgVolume);
    }

    @Override
    public void onNotifyPowerState(int i) {
        LogUtil.debugD("Power Status = " + i);
    }

    @Override
    public void onNotifyRelyServiceStarted(AAOP_ServiceInfoEntry aaop_serviceInfoEntry) {
        LogUtil.debugD("aaop_serviceInfoEntry = " + aaop_serviceInfoEntry);
        myServiceInfoEntry = aaop_serviceInfoEntry;
        Message serviceInfoEntry = Message.obtain();
        serviceInfoEntry.what = NOTIFY_SERVICE_STATUS;
        mHandler.sendMessage(serviceInfoEntry);
    }

    public void changeStatus() {
        LogUtil.debugD("getSystemStatus = " + getSystemStatus());
        switch (getSystemStatus()) {
            case SCREEN_SHUTDOWN:
            case SCREEN_STANDBY:
                AAOP_DeviceServiceManager.getInstance().setSysMuteLock(SETTING_LOCK, SETTING_LOCK_ID_CLOCK, SETTING_MUTE);
                SourceControllerImpl.getInstance().requestAdayoAudioFocus(AdayoSource.ADAYO_SOURCE_FAKESHUT);
                break;

            case SCREEN_STR_SHUTDOWN:
                AAOP_DeviceServiceManager.getInstance().setSysMuteLock(SETTING_LOCK, SETTING_LOCK_ID_CLOCK, SETTING_MUTE);
                SourceControllerImpl.getInstance().requestAdayoAudioFocus(AdayoSource.ADAYO_SOURCE_FAKESHUT);
                notifySystemStatus();

                break;
            default:
                AAOP_DeviceServiceManager.getInstance().setSysMuteLock(SETTING_UNLOCK, SETTING_LOCK_ID_CLOCK, SETTING_UNMUTE);
                SourceControllerImpl.getInstance().abandonAdayoAudioFocus(AdayoSource.ADAYO_SOURCE_FAKESHUT);
                break;
        }
    }


    public interface ServiceStatusCallBack{
        void notifyServiceStatus(AAOP_ServiceInfoEntry aaop_serviceInfoEntry);
    }

    public interface SystemStatusCallBack{
        void notifySystemStaus(int systemStatus);
    }


}
