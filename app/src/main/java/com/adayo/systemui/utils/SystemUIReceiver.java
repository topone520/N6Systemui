package com.adayo.systemui.utils;

import android.car.Car;
import android.car.media.CarAudioManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.adayo.soavb.utils.Dispatcher;
import com.adayo.systemui.contents.HvacSingleton;
import com.adayo.systemui.eventbus.EventContents;
import com.adayo.systemui.eventbus.EventData;
import com.adayo.systemui.functional.ccp._view.CcpBrightnessDialog;
import com.adayo.systemui.functional.ccp._view.CcpVolumeDialog;
import com.adayo.systemui.functional.hvac._view.HvacViewGroup;
import com.adayo.systemui.windows.dialogs.ScreenShutDownDialog;
import com.adayo.systemui.windows.panels.HvacPanel;
import com.adayo.systemui.windows.panels.QsViewPanel;
import com.adayo.systemui.windows.panels.ShortcutDockPanel;
import com.adayo.systemui.windows.panels.ShowAppListDialog;
import com.android.systemui.SystemUIApplication;

import org.greenrobot.eventbus.EventBus;

import java.io.PipedReader;

public class SystemUIReceiver extends BroadcastReceiver {
    private String TAG = SystemUIReceiver.class.getSimpleName();
    private final String RECEIVER_ACTION = "is_open";
    private final String RS_ACTION = "is_rs";
    private final String CCP_ACTION = "HardkeysCCP";
    private final String HVAC_OPEN = "open";
    private final String HVAC_CLOSE = "close";
    private final String RS_SWITCH = "switch";  // 息屏/取消息屏切换
    private final String RS_OPEN = "open"; // 息屏
    private final String RS_CLOSE = "close";  // 取消息屏

    private final String ROTATE_LEFT = "rotateLeft";
    private final String ROTATE_RIGHt = "rotateRight";
    private final String PRESS_SHORT = "pressShort";
    private final String PRESS_LONG = "pressLong";
    private final String MOVE_UP = "moveUp";
    private final String MOVE_DOWN = "moveDown";
    private final String MOVE_LEFT = "moveLeft";
    private final String MOVE_RIGHT = "moveRight";
    private final String VALUE = "value";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive: action: " + action);
        switch (action) {
            case RECEIVER_ACTION:
                if (intent.getStringExtra(RECEIVER_ACTION).equals(HVAC_OPEN)) {
                    Log.d(TAG, "onReceive: open");
                    HvacPanel.getInstance().ccpOpenHvacLayout();
                } else {
                    HvacPanel.getInstance().hvacScrollLayoutDismiss();
                    Log.d(TAG, "onReceive: is_close");
                }
                break;
            case RS_ACTION:
                ScreenShutDownDialog screenShutDownDialog = ScreenShutDownDialog.getInstance();
                if (intent.getStringExtra(RECEIVER_ACTION).equals(RS_OPEN)) {
                    Log.d(TAG, "onReceive: open");
                    if (!screenShutDownDialog.isShowing()) {
                        screenShutDownDialog.show();
                    }
                } else if (intent.getStringExtra(RECEIVER_ACTION).equals(RS_SWITCH)) {

                    Dispatcher.getInstance().dispatchToUI(() -> {
                        Log.d(TAG, "onReceive: switch" + screenShutDownDialog.isShowing());
                        if (screenShutDownDialog.isShowing()) {
                            screenShutDownDialog.dismiss();
                        } else {
                            screenShutDownDialog.show();
                        }
                    });

                } else if (intent.getStringExtra(RECEIVER_ACTION).equals(RS_CLOSE)) {
                    Log.d(TAG, "onReceive: is_close");
                    if (screenShutDownDialog.isShowing()) {
                        screenShutDownDialog.dismiss();
                    }
                }
                break;
            case CCP_ACTION:
                if (intent.getStringExtra(VALUE).equals(ROTATE_LEFT)) {
                    Log.d(TAG, "onReceive: ROTATE_LEFT");
                } else if (intent.getStringExtra(VALUE).equals(ROTATE_RIGHt)) {
                    Log.d(TAG, "onReceive: ROTATE_RIGHt");
                } else if (intent.getStringExtra(VALUE).equals(PRESS_SHORT)) {
                    Log.d(TAG, "onReceive: PRESS_SHORT");
                    // 点击事件
                    if (HvacSingleton.getInstance().isHvacLayout()) {
                        sendHvacEventBus(EventContents.EVENT_HVAC_LAYOUT_CCP_6);
                    } else if (QsViewPanel.getInstance().isQsViewShow()) {
                        QsViewPanel.getInstance().onClick();
                    } else if (ShowAppListDialog.getInstance().isShowing()) {
                        ShowAppListDialog.getInstance().onClick();
                    } else if (ShortcutDockPanel.getInstance().isShowShortcutDock()) {
                        ShortcutDockPanel.getInstance().onClick();
                    }
                } else if (intent.getStringExtra(VALUE).equals(PRESS_LONG)) {
                    Log.d(TAG, "onReceive: PRESS_LONG");
                } else if (intent.getStringExtra(VALUE).equals(MOVE_UP)) {
                    Log.d(TAG, "onReceive: MOVE_UP");
                    // 空调
                    if (HvacSingleton.getInstance().isHvacLayout()) {
                        //当前空调界面已经显示出来，进行按钮操作
                        sendHvacEventBus(EventContents.EVENT_HVAC_LAYOUT_CCP_7);
                    } else if (QsViewPanel.getInstance().isQsViewShow()) {
                        sendHvacEventBus(EventContents.EVENT_HVAC_LAYOUT_CCP_71);
                    } else if (ShowAppListDialog.getInstance().isShowing()) {
                        sendHvacEventBus(EventContents.EVENT_HVAC_LAYOUT_CCP_711);
                    } else if (ShortcutDockPanel.getInstance().isShowShortcutDock()) {
                        sendHvacEventBus(EventContents.EVENT_HVAC_LAYOUT_CCP_7111);
                    } else {
                        HvacPanel.getInstance().ccpOpenHvacLayout();
                    }
                } else if (intent.getStringExtra(VALUE).equals(MOVE_DOWN)) {
                    Log.d(TAG, "onReceive: MOVE_DOWN");
                    if (HvacSingleton.getInstance().isHvacLayout()) {
                        //当前空调界面已经显示出来，进行按钮操作
                        sendHvacEventBus(EventContents.EVENT_HVAC_LAYOUT_CCP_8);
                    } else if (QsViewPanel.getInstance().isQsViewShow()) {
                        sendHvacEventBus(EventContents.EVENT_HVAC_LAYOUT_CCP_81);
                    } else if (ShowAppListDialog.getInstance().isShowing()) {
                        sendHvacEventBus(EventContents.EVENT_HVAC_LAYOUT_CCP_811);
                    } else if (ShortcutDockPanel.getInstance().isShowShortcutDock()) {
                        sendHvacEventBus(EventContents.EVENT_HVAC_LAYOUT_CCP_8111);
                    } else {
                        // 负一屏
                        QsViewPanel.getInstance().isShowView();
                    }
                } else if (intent.getStringExtra(VALUE).equals(MOVE_LEFT)) {
                    Log.d(TAG, "onReceive: MOVE_LEFT");
                    if (HvacSingleton.getInstance().isHvacLayout()) {
                        //当前空调界面已经显示出来，进行按钮操作
                        sendHvacEventBus(EventContents.EVENT_HVAC_LAYOUT_CCP_9);
                    } else if (QsViewPanel.getInstance().isQsViewShow()) {
                        sendHvacEventBus(EventContents.EVENT_HVAC_LAYOUT_CCP_91);
                    } else if (ShowAppListDialog.getInstance().isShowing()) {
                        sendHvacEventBus(EventContents.EVENT_HVAC_LAYOUT_CCP_911);
                    }else {
                        // 应用中心
                        WindowsUtils.isShowAllAppsPanel();
                    }
                } else if (intent.getStringExtra(VALUE).equals(MOVE_RIGHT)) {
                    Log.d(TAG, "onReceive: MOVE_RIGHT");
                    if (HvacSingleton.getInstance().isHvacLayout()) {
                        //当前空调界面已经显示出来，进行按钮操作
                        sendHvacEventBus(EventContents.EVENT_HVAC_LAYOUT_CCP_10);
                    } else if (QsViewPanel.getInstance().isQsViewShow()) {
                        sendHvacEventBus(EventContents.EVENT_HVAC_LAYOUT_CCP_101);
                    } else if (ShowAppListDialog.getInstance().isShowing()) {
                        sendHvacEventBus(EventContents.EVENT_HVAC_LAYOUT_CCP_1011);
                    } else {
                        // 右侧应用中心
                        ShortcutDockPanel.getInstance().setShortcutBarVisibility(View.VISIBLE);
                    }

                }
                break;
            default:
                break;
        }

    }

    public void sendHvacEventBus(int type) {
        EventBus.getDefault().post(new EventData(type, null));
    }
}
