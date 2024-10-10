package com.adayo.systemui.manager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.adayo.bpresenter.bluetooth.constants.BusinessConstants;
import com.adayo.bpresenter.bluetooth.contracts.IConnectStateContract;
import com.adayo.bpresenter.bluetooth.contracts.ISettingsContract;
import com.adayo.bpresenter.bluetooth.presenters.ConnectStatePresenter;
import com.adayo.bpresenter.bluetooth.presenters.SettingsPresenter;
import com.adayo.common.bluetooth.bean.RingtoneInfo;
import com.adayo.systemui.bases.BaseControllerImpl;
import com.adayo.systemui.bean.BluetoothInfo;
import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.SystemUIApplication;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class BluetoothControllerImpl extends BaseControllerImpl<BluetoothInfo> implements BluetoothController {
    private volatile static BluetoothControllerImpl mBluetoothControllerImpl;
    private SettingsPresenter settingsPresenter;
    private ConnectStatePresenter connectStatePresenter;
    private BluetoothAdapter blueAdapter;
    private BluetoothInfo bluetoothInfo =new BluetoothInfo();
    private IConnectStateContract.IConnectStateView connectStateView = new IConnectStateContract.IConnectStateView() {
        @Override
        public void onBindServiceSuccess(boolean isSuccess) {
            LogUtil.d("isSuccess = " + isSuccess);
            if(isSuccess){
                mHandler.removeMessages(REGISTER_CALLBACK);
                mHandler.sendEmptyMessage(REGISTER_CALLBACK);
            }
        }

        @Override
        public void updateHfpState(boolean state) {
            LogUtil.d("state = " + state);
            bluetoothInfo.setHfpMount(state);
            bluetoothInfo.setDeviceName(getBTDevicesName());
            bluetoothInfo.setAddress(getBTDevicesAddress());
            mHandler.removeMessages(NOTIFY_CALLBACKS);
            mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
        }

        @Override
        public void updateA2DPState(boolean state) {
            LogUtil.d("state = " + state);
            bluetoothInfo.setA2dpMount(state);
            bluetoothInfo.setDeviceName(getBTDevicesName());
            bluetoothInfo.setAddress(getBTDevicesAddress());
            mHandler.removeMessages(NOTIFY_CALLBACKS);
            mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
        }

        @Override
        public void updateMapState(boolean state) {
            LogUtil.d("state = " + state);
        }

        @Override
        public void setPresenter(IConnectStateContract.IConnectStatePresenter iConnectStatePresenter) {

        }

        @Override
        public void removePresenter(IConnectStateContract.IConnectStatePresenter iConnectStatePresenter) {

        }
    };

    private ISettingsContract.ISettingsView mSettingsView = new ISettingsContract.ISettingsView() {
        @Override
        public void setPresenter(ISettingsContract.ISettingsPresenter iSettingsPresenter) {

        }

        @Override
        public void removePresenter(ISettingsContract.ISettingsPresenter iSettingsPresenter) {

        }

        @Override
        public void updateAutoAnswer(boolean enable) {
            LogUtil.d("enable = " + enable);
        }

        @Override
        public void updateBluetoothStateChanged(BusinessConstants.BTStateType type) {
            LogUtil.d("type = " + type);
        }

        @Override
        public void updateAutoReconnect(boolean enable) {
            LogUtil.d("enable = " + enable);
        }

        @Override
        public void updateLocalBluetoothName(String btName) {
            LogUtil.d("btName = " + btName);
        }

        @Override
        public void updatePinCode(String btPinCode) {
            LogUtil.d("btPinCode = " + btPinCode);
        }

        @Override
        public void updateLocalBluetoothDiscoverable(boolean discoverable) {
            LogUtil.d("discoverable = " + discoverable);
        }

        @Override
        public void updateAutoDownloadContact(boolean autoDownload) {
            LogUtil.d("autoDownload = " + autoDownload);
        }

        @Override
        public void updatePrivateAnswerCall(boolean privateAnswer) {
            LogUtil.d("privateAnswer = " + privateAnswer);
        }

        @Override
        public void updateContactsSyncState(BusinessConstants.SyncState status) {
            LogUtil.d("status = " + status);
        }

        @Override
        public void updateServiceConnectState(boolean state) {
            LogUtil.d("state = " + state);
        }

        @Override
        public void updateRingtoneInfos(List<RingtoneInfo> list) {
            LogUtil.d("list = " + list);
        }

        @Override
        public void updataCurRingtonePosition(int position) {
            LogUtil.d("position = " + position);
        }

        @Override
        public void updateThreePartyCallEnable(boolean enable) {
            LogUtil.d("enable = " + enable);
        }

        @Override
        public void updateTwinConnectEnable(boolean enable) {
            LogUtil.d("enable = " + enable);
        }

        @Override
        public void notifyRingStop() {
            LogUtil.d("notifyRingStop");
        }

        @Override
        public void updateActiveDeviceChanged(String address, String name, boolean isMutiDev) {
            if(null == bluetoothInfo) {
                bluetoothInfo = new BluetoothInfo();
            }
            LogUtil.d("address = " + address + " ; name = " + name + " ; isMutiDev = " + isMutiDev);
            LogUtil.d("isEnable = " + bluetoothInfo.isEnable() + " ; getBTDevicesName = " + bluetoothInfo.getDeviceName() + " ; isMount = " + (bluetoothInfo.isA2dpMount()||bluetoothInfo.isHfpMount()) + " ; address = " + bluetoothInfo.getAddress());
            if(!TextUtils.isEmpty(address)) {
                String operatorStr = BluetoothControllerImpl.getInstance().getBTDevicesOperator(address) + "";
                bluetoothInfo.setOperator(operatorStr);
            }
            mHandler.removeMessages(NOTIFY_CALLBACKS);
            mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
//            mHandler.sendEmptyMessageDelayed(NOTIFY_CALLBACKS, 500);
        }
    };

    private BluetoothControllerImpl() {
        blueAdapter = BluetoothAdapter.getDefaultAdapter();
        settingsPresenter = new SettingsPresenter(SystemUIApplication.getSystemUIContext());
        settingsPresenter.setView(mSettingsView);
        mSettingsView.setPresenter(settingsPresenter);
        settingsPresenter.init();

        connectStatePresenter = new ConnectStatePresenter(SystemUIApplication.getSystemUIContext());
        connectStatePresenter.setView(connectStateView);
        connectStateView.setPresenter(connectStatePresenter);
        connectStatePresenter.init();
    }

    public static BluetoothControllerImpl getInstance() {
        if (mBluetoothControllerImpl == null) {
            synchronized (BluetoothControllerImpl.class) {
                if (mBluetoothControllerImpl == null) {
                    mBluetoothControllerImpl = new BluetoothControllerImpl();
                }
            }
        }
        return mBluetoothControllerImpl;
    }

    @Override
    public boolean isBTEnabled() {
        if (null != blueAdapter) {
            return blueAdapter.isEnabled();
        }
        return false;
    }

    private boolean canSetBt = true;
    @Override
    public void setBTEnabled(boolean enable) {
        LogUtil.debugD("enable = " + enable);
//        if (null != blueAdapter && canSetBt) {
        if (null != blueAdapter) {
            canSetBt = false;
            if (enable) {
                blueAdapter.enable();
            } else {
                blueAdapter.disable();
            }
        }
    }

    @Override
    public boolean isBTConnected() {
        return connectStatePresenter.isHfpConnected() || connectStatePresenter.isA2dpConnected();
//        if (null != blueAdapter) {
//            Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();
//            if (bondedDevices != null && bondedDevices.size() > 0) {
//                for (BluetoothDevice bondedDevice : bondedDevices) {
//                    try {
//                        //使用反射调用被隐藏的方法
//                        Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
//                        isConnectedMethod.setAccessible(true);
//                        return (boolean) isConnectedMethod.invoke(bondedDevice, (Object[]) null);
//                    } catch (NoSuchMethodException e) {
//                        LogUtil.w(e.getMessage());
//                    } catch (IllegalAccessException e) {
//                        LogUtil.w(e.getMessage());
//                    } catch (InvocationTargetException e) {
//                        LogUtil.w(e.getMessage());
//                    }
//                }
//            }
//        }
//        return false;
    }

    @Override
    public String getBTDevicesName() {
        if(null != blueAdapter) {
            //得到已匹配的蓝牙设备列表
            Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();
            if (bondedDevices != null && bondedDevices.size() > 0) {
                for (BluetoothDevice bondedDevice : bondedDevices) {
                    try {
                        //使用反射调用被隐藏的方法
                        Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                        isConnectedMethod.setAccessible(true);
                        boolean isConnected = (boolean) isConnectedMethod.invoke(bondedDevice, (Object[]) null);
                        if (isConnected) {
                            return bondedDevice.getName();
                        }
                    } catch (NoSuchMethodException e) {
                        LogUtil.w(e.getMessage());
                    } catch (IllegalAccessException e) {
                        LogUtil.w(e.getMessage());
                    } catch (InvocationTargetException e) {
                        LogUtil.w(e.getMessage());
                    }
                }
            }
        }
        return "";
    }

    @Override
    public String getBTDevicesAddress() {
        if(null != blueAdapter) {
            //得到已匹配的蓝牙设备列表
            Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();
            if (bondedDevices != null && bondedDevices.size() > 0) {
                for (BluetoothDevice bondedDevice : bondedDevices) {
                    try {
                        //使用反射调用被隐藏的方法
                        Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                        isConnectedMethod.setAccessible(true);
                        boolean isConnected = (boolean) isConnectedMethod.invoke(bondedDevice, (Object[]) null);
                        if (isConnected) {
//                            String address = bondedDevice.getAddress();
//                            if(null != address){
//                                address = address.replaceAll(":", "");
//                            }
                            return bondedDevice.getAddress();
                        }
                    } catch (NoSuchMethodException e) {
                        LogUtil.w(e.getMessage());
                    } catch (IllegalAccessException e) {
                        LogUtil.w(e.getMessage());
                    } catch (InvocationTargetException e) {
                        LogUtil.w(e.getMessage());
                    }
                }
            }
        }
        return "";
    }

    @Override
    public String getBTDevicesOperator(String address) {
        return settingsPresenter.getHfpRemoteDeviceNetworkOperator(address);
    }

    @Override
    public BluetoothInfo getBluetoothInfo() {
        return bluetoothInfo;
    }

    @Override
    protected boolean registerListener() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
//        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
//        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        SystemUIApplication.getSystemUIContext().registerReceiver(mBluetoothStateReceiver, filter);
        bluetoothInfo.setEnable(isBTEnabled());
        return true;
    }

    private final BroadcastReceiver mBluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(null == bluetoothInfo) {
                bluetoothInfo = new BluetoothInfo();
            }
            switch (action){
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
//                    switch (state) {
//                        case BluetoothAdapter.STATE_OFF:
//                            // 蓝牙已关闭
//                            break;
//                        case BluetoothAdapter.STATE_TURNING_OFF:
//                            // 蓝牙正在关闭
//                            break;
//                        case BluetoothAdapter.STATE_ON:
//                            // 蓝牙已打开
//                            break;
//                        case BluetoothAdapter.STATE_TURNING_ON:
//                            // 蓝牙正在打开
//                            break;
//                    }
                    LogUtil.d("state = " + state);
                    canSetBt = BluetoothAdapter.STATE_OFF == state || BluetoothAdapter.STATE_ON == state;
                    bluetoothInfo.setState(state);
                    bluetoothInfo.setEnable(isBTEnabled());
                    mHandler.removeMessages(NOTIFY_CALLBACKS);
                    mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
                    break;
//                case BluetoothDevice.ACTION_ACL_CONNECTED:
//                    bluetoothInfo.setMount(true);
//                    LogUtil.d("ACTION_ACL_CONNECTED isMount = " + bluetoothInfo.isMount());
//                    break;
//                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
//                    bluetoothInfo.setMount(false);
//                    LogUtil.d("ACTION_ACL_DISCONNECTED isMount = " + bluetoothInfo.isMount());
//                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected BluetoothInfo getDataInfo() {
        return bluetoothInfo;
    }
}
