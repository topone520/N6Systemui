package com.android.systemui;

import android.annotation.MainThread;
import android.app.ActivityTaskManager;
import android.app.INotificationManager;
import android.app.ITransientNotificationCallback;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.usb.IUsbManager;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.IAccessibilityManager;
import android.widget.ToastPresenter;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.attribute.ISkinAttrHandler;
import com.adayo.proxy.aaop_hskin.entity.SkinAttr;
import com.adayo.proxy.aaop_hskin.resource.IResourceManager;
import com.adayo.proxy.aaop_hskin.utils.SkinAttrUtils;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.proxy.infrastructure.sourcemng.Beans.SourceInfo;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSwitchManager;
import com.adayo.proxy.system.aaop_systemservice.AAOP_SystemServiceManager;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.manager.CommandQueue;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.manager.SeatMemoryManager;
import com.adayo.systemui.manager.SourceControllerImpl;
import com.adayo.systemui.manager.SystemStatusManager;
import com.adayo.systemui.notification.NotificationListener;
import com.adayo.systemui.utils.CarTypeUtils;
import com.adayo.systemui.utils.DisplayUtils;
import com.adayo.systemui.utils.MqttUtils;
import com.adayo.systemui.utils.WindowsUtils;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.bars.VtpNavigationBar;
import com.adayo.systemui.windows.panels.VtpHvacPanel;
import com.adayo.systemui.windows.views.hvac.VerSelectedView;
import com.android.internal.statusbar.IStatusBarService;

import java.util.HashMap;
import java.util.Objects;

public class SystemUIService extends Service implements CommandQueue.Callbacks {
    private final static String TAG = SystemUIService.class.getName();
    NotificationListener mNotificationListener;
    private UsbDevice mDevice;
    private UsbAccessory mAccessory;
    private PendingIntent mPendingIntent;
    private String mPackageName;
    private int mUid;
    private AdsServiceRegister mAdsServiceRegister;
    private AdsServiceObserver mAdsServiceObserver;

    public SystemUIService() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.debugI("onCreate begin");
        try {
            ActivityTaskManager.getService().setLockScreenShown(false, false);
        } catch (RemoteException e) {
            LogUtil.i("ActivityTaskManager error");
        }
        registerStatusBar();
        createAndAddWindows();
        registerReceivers();
        registerSystemStatus();
        DisplayUtils.initDisplay();
        DisplayUtils.initIntentSetting();
        LogUtil.debugI("onCreate end");

        mAdsServiceRegister = new AdsServiceRegister();
        mAdsServiceObserver = new AdsServiceObserver();
        mAdsServiceRegister.injectService("SN_SCREEN_OFF");
        mAdsServiceRegister.submit(mAdsServiceObserver);

        mNotificationListener = new NotificationListener();
        mNotificationListener.setUpWithPresenter();
        SeatMemoryManager.getInstance();
        ReportBcmManager.getInstance();
        //mqtt??
        MqttUtils.getInstance().init(SystemUIApplication.getSystemUIContext());

        //adb shell am startservice -n com.baic.icc.mqtt.service/com.baic.icc.mqtt.service.MqttService

        if (CarTypeUtils.getCarType() == CarTypeUtils.CarType.N60) {
            AAOP_LogUtils.i(TAG, "Car Type: 60");
            VtpHvacPanel.getInstance();
            //扶手
            VtpNavigationBar presentationBottom = new VtpNavigationBar(SystemUIApplication.getSystemUIContext(), DisplayUtils.getDisplay());
            presentationBottom.show();
            addHvacBarWindow();

        } else {
            AAOP_LogUtils.i(TAG, "Car Type: 61");
            addHvacBarWindow();
        }

    }

    private void registerSystemStatus() {
        SystemStatusManager.getInstance();
    }

    private void registerReceivers() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.debugI("onBind begin");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.debugI("begin");
        if (null != intent) {
            grantDevicePermission(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void grantDevicePermission(Intent intent) {
        mDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        mAccessory = (UsbAccessory) intent.getParcelableExtra(UsbManager.EXTRA_ACCESSORY);
        mPendingIntent = (PendingIntent) intent.getParcelableExtra(Intent.EXTRA_INTENT);
        mUid = intent.getIntExtra(Intent.EXTRA_UID, -1);
        mPackageName = intent.getStringExtra(UsbManager.EXTRA_PACKAGE);
//        mPackageName = intent.getStringExtra("android.hardware.usb.extra.PACKAGE");
//        boolean canBeDefault = intent.getBooleanExtra(UsbManager.EXTRA_CAN_BE_DEFAULT, false);
        LogUtil.d("mUid = " + mUid + " ; mPackageName = " + mPackageName + " ; canBeDefault = ");
        if (null != mPendingIntent) {
            IBinder b = ServiceManager.getService(USB_SERVICE);
            IUsbManager service = IUsbManager.Stub.asInterface(b);

            Intent mIntent = new Intent();
            try {
                if (mDevice != null) {
                    mIntent.putExtra(UsbManager.EXTRA_DEVICE, mDevice);
                    service.grantDevicePermission(mDevice, mUid);
                    final int userId = UserHandle.getUserId(mUid);
//                    UserHandle.getUserHandleForUid()
                    service.setDevicePackage(mDevice, mPackageName, userId);
                }
                if (mAccessory != null) {
                    mIntent.putExtra(UsbManager.EXTRA_ACCESSORY, mAccessory);
                    service.grantAccessoryPermission(mAccessory, mUid);
                    final int userId = UserHandle.getUserId(mUid);
                    service.setAccessoryPackage(mAccessory, mPackageName, userId);
                }
                mIntent.putExtra(UsbManager.EXTRA_PERMISSION_GRANTED, true);
                mPendingIntent.send(this, 0, mIntent);
            } catch (PendingIntent.CanceledException e) {
                LogUtil.w("PendingIntent was cancelled");
            } catch (RemoteException e) {
                LogUtil.e("IUsbService connection failed", e);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        LogUtil.debugI("begin");
        super.onConfigurationChanged(newConfig);
        LogUtil.debugI("end");
    }

    private void createAndAddWindows() {
        LogUtil.debugI("begin");
        addStatusBarWindow();
        addNavigationBarWindow();
        addDockBarWindow();
        //     addHvacBarWindow();
        addShortcutDockBarWindow();
        //   addHvacBarBottom();
        Log.i("sendMessage", "fgeSendMessage2ComService+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

//        AAOP_SystemServiceManager.getInstance().setPowerState(7003, AAOP_SystemServiceContantsDef.AAOP_POWER_STATUS.getType(0));
//        new BootAnimationDialog();
        LogUtil.debugI("end");
    }

    private void addDockBarWindow() {
        LogUtil.debugI("begin");
        WindowsUtils.setDockBarVisibility(View.VISIBLE);
        LogUtil.debugI("end");
    }

    private void addNavigationBarWindow() {
        LogUtil.debugI("begin");
//        WindowsUtils.setNavigationBarVisibility(View.VISIBLE);
        LogUtil.debugI("end");
    }

    private void addHvacBarWindow() {
        LogUtil.debugI("begin");
        WindowsUtils.showHvacInitialViewBar();
        LogUtil.debugI("end");
    }

    private void addStatusBarWindow() {
        LogUtil.debugI("begin");
        WindowsUtils.setStatusBarVisibility(View.VISIBLE);
        LogUtil.debugI("end");
    }

    private void addShortcutDockBarWindow() {
        LogUtil.debugI("begin");
        WindowsUtils.showShortcutDockBar();
        LogUtil.debugI("end");
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void registerStatusBar() {
        LogUtil.debugI("begin");
        IStatusBarService barService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        CommandQueue mCommandQueue = new CommandQueue(getBaseContext());
        mCommandQueue.addCallback(this);
        try {
            barService.registerStatusBar(mCommandQueue);
        } catch (RemoteException ex) {
            // If the system process isn't there we're doomed anyway.
        }

        mNotificationManager = INotificationManager.Stub.asInterface(ServiceManager.getService(Context.NOTIFICATION_SERVICE));
        mAccessibilityManager = IAccessibilityManager.Stub.asInterface(ServiceManager.getService(Context.ACCESSIBILITY_SERVICE));
        Resources resources = getResources();
        mGravity = resources.getInteger(com.android.internal.R.integer.config_toastDefaultGravity);
        mY = resources.getDimensionPixelSize(com.android.internal.R.dimen.toast_y_offset);
        LogUtil.debugI("end");
    }


    @Override
    public void setWindowState(int displayId, int window, int state) {
        CommandQueue.Callbacks.super.setWindowState(displayId, window, state);

        LogUtil.debugI("setWindowState " + displayId + " window" + window + " state" + state + "backDisposition");

    }

    @Override
    public void topAppWindowChanged(int displayId, boolean isFullscreen, boolean isImmersive) {
        CommandQueue.Callbacks.super.topAppWindowChanged(displayId, isFullscreen, isImmersive);

        LogUtil.d("topAppWindowChanged() called with: displayId = [" + displayId + "], isFullscreen = [" + isFullscreen + "], isImmersive = [" + isImmersive + "]");

    }

    @Nullable
    private ToastPresenter mPresenter;
    @Nullable
    private ITransientNotificationCallback mCallback;
    private INotificationManager mNotificationManager;
    private IAccessibilityManager mAccessibilityManager;
    private int mGravity;
    private int mY;

    @Override
    @MainThread
    public void showToast(int uid, String packageName, IBinder token, CharSequence text, IBinder windowToken, int duration, @Nullable ITransientNotificationCallback callback) {
        LogUtil.debugD("showToast() called with: uid = [" + uid + "], packageName = [" + packageName + "], token = [" + token + "], text = [" + text + "], windowToken = [" + windowToken + "], duration = [" + duration + "], callback = [" + callback + "]");
        if (mPresenter != null) {
            hideCurrentToast();
        }
//        Context context = mContext.createContextAsUser(UserHandle.getUserHandleForUid(uid), 0);
        Context context = SystemUIApplication.getSystemUIContext();
        View view = ToastPresenter.getTextToastView(context, text);
        mCallback = callback;
        mPresenter = new ToastPresenter(context, mAccessibilityManager, mNotificationManager, packageName);
        mPresenter.show(view, token, windowToken, duration, mGravity, 0, mY, 0, 0, mCallback);
    }

    @Override
    public void hideToast(String packageName, IBinder token) {
        if (mPresenter == null || !Objects.equals(mPresenter.getPackageName(), packageName) || !Objects.equals(mPresenter.getToken(), token)) {
            LogUtil.w("Attempt to hide non-current toast from package " + packageName);
            return;
        }
        hideCurrentToast();
    }

    @MainThread
    private void hideCurrentToast() {
        mPresenter.hide(mCallback);
        mPresenter = null;
    }

    @Override
    public void toggleKeyboardShortcutsMenu(int deviceId) {
        CommandQueue.Callbacks.super.toggleKeyboardShortcutsMenu(deviceId);
        LogUtil.d("toggleKeyboardShortcutsMenu");

    }

    @Override
    public void dismissKeyboardShortcutsMenu() {
        CommandQueue.Callbacks.super.dismissKeyboardShortcutsMenu();
        LogUtil.d("dismissKeyboardShortcutsMenu");
    }

    @Override
    public void toggleRecentApps() {
        LogUtil.d("toggleRecentApps");
        if (!AdayoSource.ADAYO_SOURCE_HOME.equals(SourceControllerImpl.getInstance().getCurrentUISource())) {
            SourceControllerImpl.getInstance().requestSource(AppConfigType.SourceSwitch.APP_ON.getValue(), AdayoSource.ADAYO_SOURCE_HOME, new HashMap<>());
        }
        CommandQueue.Callbacks.super.toggleRecentApps();
//        TaskManagerView.getInstance().show(true);
    }

    @Override
    public void onGestureEvent(int action, float x, float y) {
        CommandQueue.Callbacks.super.onGestureEvent(action, x, y);
        LogUtil.d("onGestureEvent() called with: action = [" + action + "], x = [" + x + "], y = [" + y + "]");
    }
}
