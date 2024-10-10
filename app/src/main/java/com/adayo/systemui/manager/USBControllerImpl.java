package com.adayo.systemui.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.adayo.systemui.bases.BaseControllerImpl;
import com.adayo.systemui.bean.USBInfo;
import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.SystemUIApplication;

import java.util.Objects;

public class USBControllerImpl extends BaseControllerImpl<USBInfo> implements USBController {
    private static final String TAG = USBControllerImpl.class.getSimpleName();
    private volatile static USBControllerImpl mUSBControllerImpl;
    private UsbManager mUsbManager;
    private USBInfo usbInfo;

    BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) return;
            LogUtil.d("onReceive");
            if (null == usbInfo) usbInfo = new USBInfo();
            String action = intent.getAction();
            switch (Objects.requireNonNull(action)) {
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    Log.d(TAG, "usb extract");
                    usbInfo.setInsert(true);
                    mHandler.removeMessages(NOTIFY_CALLBACKS);
                    mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    Log.d(TAG, "usb insert");
                    usbInfo.setInsert(false);
                    mHandler.removeMessages(NOTIFY_CALLBACKS);
                    mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
                    break;
                default:
                    break;
            }
        }
    };

    private USBControllerImpl() {
        mUsbManager = (UsbManager)SystemUIApplication.getSystemUIContext().getSystemService(Context.USB_SERVICE);
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);
    }

    public static USBControllerImpl getInstance() {
        if (mUSBControllerImpl == null) {
            synchronized (USBControllerImpl.class) {
                if (mUSBControllerImpl == null) {
                    mUSBControllerImpl = new USBControllerImpl();
                }
            }
        }
        return mUSBControllerImpl;
    }

    @Override
    public int getUSBDeviceNum() {
        if(null == usbInfo){
            return 0;
        }
        return usbInfo.getDeviceNum();
    }

    @Override
    public int getUSBMountNum() {
        if(null == usbInfo){
            return 0;
        }
        return usbInfo.getMountNum();
    }

    @Override
    public boolean isUSBDevice() {
        return getUSBDeviceNum() > 0 ? true : false;
    }

    @Override
    public boolean isUSBMount() {
        return getUSBMountNum() > 0 ? true : false;
    }

    @Override
    protected boolean registerListener() {
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        usbFilter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        usbFilter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        usbFilter.addAction("android.hardware.usb.action.USB_STATE");
        SystemUIApplication.getSystemUIContext().registerReceiver(usbReceiver, usbFilter);
        return true;
    }

    @Override
    protected USBInfo getDataInfo() {
        if(null == usbInfo){
            usbInfo = new USBInfo();
        }
        usbInfo.setDeviceNum(mUsbManager.getDeviceList().size());
        return usbInfo;
    }
}
