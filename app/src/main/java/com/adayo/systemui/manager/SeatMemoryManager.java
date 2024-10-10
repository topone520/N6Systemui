package com.adayo.systemui.manager;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.service.SoaService;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.contents.HvacSingleton;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.eventbus.EventContents;
import com.adayo.systemui.eventbus.EventData;
import com.adayo.systemui.proxy.vehicle.PowerService;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.proxy.vehicle.VehicleDriverService;
import com.adayo.systemui.proxy.vehicle.VehicleGearService;
import com.adayo.systemui.utils.GlobalTimer;
import com.adayo.systemui.windows.dialogs.SeatMemoryInputDialog;
import com.adayo.systemui.windows.views.seat.SeatMemoryView;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

public class SeatMemoryManager {

    private static final String TAG = "HVAC_Bcm_" + SeatMemoryManager.class.getSimpleName();

    private static SeatMemoryManager instance;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = (Bundle) msg.obj;
            handlerData(bundle);
        }
    };

    private SeatMemoryManager() {
        bindService();
    }

    public static SeatMemoryManager getInstance() {
        synchronized (SeatMemoryManager.class) {
            if (instance == null) {
                instance = new SeatMemoryManager();
            }
            return instance;
        }
    }

    private void bindService() {
        //座椅
        SeatService.getInstance().connect(TAG + "_SEAT", service -> {
            AAOP_LogUtils.i(TAG, "Seat::onConnected()" + service.acquireName());
            service.subscribe(bundle -> {
                Message message = new Message();
                message.obj = bundle;
                handler.sendMessage(message);
            });
        });
        //车设
        VehicleDriverService.getInstance().connect(TAG + "_DRIVER", service -> {
            AAOP_LogUtils.i(TAG, "SN_DRIVER::onConnected()" + service.acquireName());
            service.subscribe(bundle -> {
                Message message = new Message();
                message.obj = bundle;
                handler.sendMessage(message);
            });
        });
        //档位
        VehicleGearService.getInstance().connect(TAG + "_Gear", service -> {
            service.subscribe(bundle -> {
                Message message = new Message();
                message.obj = bundle;
                handler.sendMessage(message);
            });
        });

        PowerService.getInstance().connect(TAG + "_POWER", service -> {
            AAOP_LogUtils.i(TAG, "Power::onConnected()" + service.acquireName());
            service.subscribe(bundle -> {
                Message message = new Message();
                message.obj = bundle;
                handler.sendMessage(message);
            });
        });

    }

    private int cardGrep = 0, power = HvacSOAConstant.HVAC_POWER_MODE_IGN_ON, commValue, cardSpeed;

    private void handlerData(Bundle bundle) {
        if (null == bundle) return;
        AAOP_LogUtils.d(TAG, "bundle = " + bundle.toString());

        //主驾驶：高度，靠背，坐垫，前后，方向盘，左后视镜
        //副驾驶：高度，靠背，坐垫，前后，右后视镜
//        "MSG_EVENT_SEAT_HEIGHT_ADJUSTMENT"
        String messageId = bundle.getString("message_id", "node");
        Object value = bundle.getInt("value", 0);
        int target = bundle.getInt("target", 0);
        switch (messageId) {
            //高度
            case SeatSOAConstant.MSG_EVENT_SEAT_HEIGHT_BTN:
                //靠背
            case SeatSOAConstant.MSG_EVENT_SEAT_BACKREST_BTN:
                //坐垫
            case SeatSOAConstant.MSG_EVENT_SEAT_CUSHION_BTN:
                //前后
            case SeatSOAConstant.MSG_EVENT_SEAT_FRONT_REAR_BTN:
                //后视镜
            case SeatSOAConstant.MSG_EVENT_REARVIEW_STATUS:
                commValue = (int) value;
                if (target == SeatSOAConstant.LEFT_FRONT) {
                    showDialog(true);
                } else if (target == SeatSOAConstant.RIGHT_FRONT) {
                    showDialog(false);
                }
                break;
            case SeatSOAConstant.MSG_EVENT_STEERING_STATUS:
                //方向盘
                showDialog(true);
                break;
            //档位
            case SeatSOAConstant.MSG_EVENT_GEAR:
                cardGrep = (int) value;
                break;
            //电源模式
            case HvacSOAConstant.MSG_EVENT_POWER_VALUE:
                power = (int) value;
                break;
            //车速
            case SeatSOAConstant.MSG_EVENT_SPEED_VALUE:
                cardSpeed = (int) value;
                break;
        }
    }

    public void showDialog(boolean isDriver) {
        //车速《5，电源ON，档位！=R
        AAOP_LogUtils.d(TAG, "cardSpeed = " + cardSpeed + "  power = " + power + "  cardGrep = " + cardGrep + "  value = " + commValue + "  HvacSingleton.getInstance().isSeatDialogShow() = " + HvacSingleton.getInstance().isSeatDialogShow());
        if (cardSpeed <= 5 && power == HvacSOAConstant.HVAC_POWER_MODE_IGN_ON && cardGrep != 3 && commValue != 0 && commValue != 3) {
            if (!HvacSingleton.getInstance().isSeatDialogShow()) {
                SeatMemoryInputDialog.getInstance().updateTips(isDriver);
            } else {
                //使用eventbus将消息投递到dialog中
                EventBus.getDefault().post(new EventData(EventContents.EVENT_SEAT_MEMORY, isDriver));
            }
        }
    }

    public void setMemoryInput(int target, int instruct) {
        Bundle bundle = new Bundle();
        bundle.putInt("target", target);
        bundle.putInt("child_target", target);
        bundle.putInt("user_id", 111);
        bundle.putInt("instruct", instruct);
        ADSBusReturnValue value = new ADSBusReturnValue();
        SeatService.getInstance().invoke(SeatSOAConstant.MSG_SET_SEAT_MEMORY, bundle, value);
    }
}
