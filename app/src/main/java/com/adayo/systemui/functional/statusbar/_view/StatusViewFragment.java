package com.adayo.systemui.functional.statusbar._view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.system.aaop_systemservice.soa.data.ADSBusAddress;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.view.AbstractBindViewFramelayout;
import com.adayo.soavb.utils.Dispatcher;
import com.adayo.systemui.functional.statusbar._binder.PrivacyPermissionView;
import com.adayo.systemui.functional.statusbar._binder.Status220ViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusBluetoothViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusCameraViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusCarLinkViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusClockViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusDvrViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusGuestModeViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusHotspotViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusIsoFixWarningViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusLocationViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusMessageViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusMicViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusOtaViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusPmGroupViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusProfileViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusSafetyBeltWarningViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusSignalViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusUsbViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusUserViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusVolumeViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusWaterIonsViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusWifiViewBinder;
import com.adayo.systemui.functional.statusbar._binder.StatusWirelessViewBinder;
import com.adayo.systemui.proxy.vehicle.VehicleChargeService;
import com.adayo.systemui.proxy.vehicle.VehicleDoorService;
import com.adayo.systemui.proxy.vehicle.VehicleDriverService;
import com.adayo.systemui.proxy.vehicle.VehicleSceneService;
import com.adayo.systemui.utils.AAOP_SceneConnectProxy;
import com.android.systemui.R;

import java.util.Timer;
import java.util.TimerTask;


public class StatusViewFragment extends AbstractBindViewFramelayout {

    private static final String TAG = StatusViewFragment.class.getSimpleName();

    private StatusOtaViewBinder _statusOtaViewBinder;
    private StatusMessageViewBinder _statusMessageViewBinder;
    private boolean isPrivacy = false;
    private boolean isScene = false;

    private Handler mHandler = new Handler(msg -> {
        Log.d(TAG, TAG + "- msg.arg1: " + msg.arg1);
        switch (msg.arg1){
            case 0:
                //连接隐私服务
                if (null == PrivacyPermissionView.getInstance().getPrivacy()){
                    PrivacyPermissionView.getInstance().connect();
                }
                if (!AAOP_SceneConnectProxy.getInstance().isBindService()){
                    AAOP_SceneConnectProxy.getInstance().init();
                }
                isPrivacy = PrivacyPermissionView.getInstance().getPrivacy() != null;
                isScene = AAOP_SceneConnectProxy.getInstance().isBindService() == true;
                break;
            default:
                break;
        }
        return false;
    });
    private Timer mTimer;

    public StatusViewFragment(@NonNull Context context) {
        super(context);
    }

    public StatusViewFragment(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void createViewBinder() {
        // 时间
        insertViewBinder(new StatusClockViewBinder());
        // 音量
        insertViewBinder(new StatusVolumeViewBinder());
        // 消息提醒
        _statusMessageViewBinder = new StatusMessageViewBinder();
        insertViewBinder(_statusMessageViewBinder);
        // 信号强度
        insertViewBinder(new StatusSignalViewBinder());
        // 蓝牙
        insertViewBinder(new StatusBluetoothViewBinder());
        // wifi
        insertViewBinder(new StatusWifiViewBinder());
        // DVR
        insertViewBinder(new StatusDvrViewBinder());
        // 展车
        insertViewBinder(new StatusGuestModeViewBinder());
        // GPS
        insertViewBinder(new StatusLocationViewBinder());
        // 麦克风
        insertViewBinder(new StatusMicViewBinder());
        // 无线充电
        insertViewBinder(new StatusWirelessViewBinder());
        // 儿童座椅上拉带报警
        insertViewBinder(new StatusSafetyBeltWarningViewBinder());
        // 儿童座椅ISOFIX报警
        insertViewBinder(new StatusIsoFixWarningViewBinder());
        // 热点
        insertViewBinder(new StatusHotspotViewBinder());
        // U盘
        insertViewBinder(new StatusUsbViewBinder());
        // 情景模式
        insertViewBinder(new StatusProfileViewBinder());
        // 220V电源
        insertViewBinder(new Status220ViewBinder());
        // 车内相机
        insertViewBinder(new StatusCameraViewBinder());
        // CarLink
        insertViewBinder(new StatusCarLinkViewBinder());
        // 水离子
        insertViewBinder(new StatusWaterIonsViewBinder());
        // PM2.5
        insertViewBinder(new StatusPmGroupViewBinder());
        // ota
        _statusOtaViewBinder = new StatusOtaViewBinder();
        insertViewBinder(_statusOtaViewBinder);
        // 账户
        insertViewBinder(new StatusUserViewBinder());
    }

    @Override
    protected int acquireResourceId() {
        return R.layout.dialog_status_bar_view;
    }

    public void messageIconSetSelected(boolean isSelected){
        _statusMessageViewBinder.messageIconSetSelected(isSelected);
    }

    public void otaIconSetSelected(String title,CharSequence content,String time){
        _statusOtaViewBinder.otaIconSetSelected(title, content, time);
    }

    public void initialize() {
        AAOP_LogUtils.i(TAG, "::initialize()");
        VehicleSceneService.getInstance().connect("Status_SCENE", service -> {
            AAOP_LogUtils.i(TAG, "SCENE::onConnected()" + service.acquireName());
            service.subscribe(this::dispatchMessage);
        });
        VehicleDriverService.getInstance().connect("Status_DRIVER", service -> {
            AAOP_LogUtils.i(TAG, "DRIVER::onConnected()" + service.acquireName());
            service.subscribe(this::dispatchMessage);
        });
        VehicleChargeService.getInstance().connect("Status_CHARGE", service -> {
            AAOP_LogUtils.i(TAG, "CHARGE::onConnected()" + service.acquireName());
            service.subscribe(this::dispatchMessage);
        });

        VehicleDoorService.getInstance().connect("Status_DOOR", service -> {
            AAOP_LogUtils.i(TAG, "DOOR::onConnected()" + service.acquireName());
            service.subscribe(this::dispatchMessage);
        });

        mTimer = new Timer();
        mTimer.schedule(timerTask,0);
    }
    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            AAOP_LogUtils.i(TAG, "timerTask::run()" );
            while (true){
                try {
                    // 执行你的任务代码
                    AAOP_LogUtils.i(TAG, "timerTask::run()");
                    Message message = mHandler.obtainMessage();
                    message.arg1 = 0;
                    mHandler.sendMessage(message);
                    AAOP_LogUtils.i(TAG, "isPrivacy: "+isPrivacy+" ,isScene: "+isScene);
                    if (isPrivacy && isScene) {
                        break;
                    }
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            cancel();
        }
    };
	   public void otaIconStatus(int otaState){
        _statusOtaViewBinder.otaIconStatus(otaState);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTimer.cancel();
    }
}
