package com.adayo.systemui.viewModel;

import static com.adayo.common.bluetooth.constant.BtDef.STATE_CONNECTED;
import static com.adayo.common.bluetooth.constant.BtDef.STATE_CONNECTING;
import static com.adayo.common.bluetooth.constant.BtDef.STATE_CONNECT_FAIL;
import static com.adayo.common.bluetooth.constant.BtDef.STATE_READY;
import static com.adayo.common.bluetooth.constant.BtDef.UUID_IAP;

import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.adayo.bpresenter.bluetooth.constants.BusinessConstants;
import com.adayo.bpresenter.bluetooth.contracts.IBTWndMangerContract;
import com.adayo.bpresenter.bluetooth.contracts.IPairedContract;
import com.adayo.bpresenter.bluetooth.contracts.IScanningContract;
import com.adayo.bpresenter.bluetooth.contracts.ISettingsContract;
import com.adayo.bpresenter.bluetooth.presenters.BTWndManagerPresenter;
import com.adayo.bpresenter.bluetooth.presenters.PairedPresenter;
import com.adayo.bpresenter.bluetooth.presenters.ScanningPresenter;
import com.adayo.bpresenter.bluetooth.presenters.SettingsPresenter;
import com.adayo.common.bluetooth.bean.BluetoothDevice;
import com.adayo.common.bluetooth.bean.RingtoneInfo;
import com.adayo.systemui.bean.BluetoothPairingEntity;
import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.SystemUIApplication;
import com.kunminx.architecture.ui.callback.UnPeekLiveData;
import com.nforetek.bt.base.jar.NforeBtBaseJar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class BtViewModel extends AndroidViewModel {
    private static final String ACTION_CONNECT_STATUS = "com.adayo.app.setting.carplay_status";
    private static final String TAG = BtViewModel.class.getSimpleName();
    public static final String ACTION_CAR_PLAY_STATUS = "com.adayo.carplay.carplay_status";
    public static final String ACTION_CAR_PLAY_STATE = "mfiservice.connect.state.action.update";
    public static final String ACTION_SOURCE_CHANGE = "adayo.keyEvent.onKeyLongPress";
    public static final String ACTION_CAR_PLAY_STATUS_SYSTEM = "com.adayo.carplay.carplay_status_SYSTEM";
    public static final String ACTION_CAR_PLAY_STATUS_SYSTEM_ = "com.adayo.carplay.carplay_status_SYSTEM_";
    public static final String CAR_PLAY_CONNECTED = "CarPlayConnected";
    public static final String CAR_PLAY_REFRESH = "CarPlayRefresh";
    public static final String CAR_PLAY_CONNECTED_LIST = "CarPlayConnectedList";
    public static final String CAR_PLAY_FAIL = "CarPlayfail";

    public static final String EXTRA_STATUS = "status";
    public static final String EXTRA_IS_SHOW = "isShow";
    public static final String EXTRA_SOURCE_DEF = "SourceDef";
    public static final String EXTRA_CONNECT_MODE = "ConnectMode";
    public static final String EXTRA_CONNECT_DEVICEID = "DeviceId";
    public static final String EXTRA_CONNECT_MOBILENAME = "mobileName";
    public static final String EXTRA_CONNECT_MACADDRESS = "macAddress";

    //优先连接的常用设备
    public static final String EXTRA_CONNECT_FREQUENT = "connect_frequent";
    //开关状态
    private final MutableLiveData<BusinessConstants.BTStateType> mBtSwitch = new MutableLiveData<>();
    //扫描到的设备
    private final MutableLiveData<List<BluetoothDevice>> mFoundedDevices = new MutableLiveData<>();
    //设备名称
    private final MutableLiveData<String> mBtName = new MutableLiveData<>();
    //当前活动的（焦点）Device
    private final UnPeekLiveData<BluetoothDevice> mActiveDevice = new UnPeekLiveData<>();
    //已配对的设备
    private final MutableLiveData<List<BluetoothDevice>> mPairedDevice = new MutableLiveData<>();
    //扫描设备状态
    private final MutableLiveData<Boolean> mScanningState = new MutableLiveData<>();
    //自动同步联系人开关状态
    private final MutableLiveData<Boolean> mAutoDownloadPbEnable = new MutableLiveData<>();
    //已连接设备名称
    private final MutableLiveData<String> mConnectedDeviceName = new MutableLiveData<>();
    //状态-已配对设备是否正在连接中
    private boolean isPairedDeviceConnecting;
    //配对请求的设备信息
    private final MutableLiveData<BluetoothPairingEntity> mPairingDevice = new MutableLiveData<>();
    private boolean mReqPaired = false;

    private SettingsPresenter mSettingsPresenter;
    private PairedPresenter mPairedPresenter;
    private ScanningPresenter mScanningPresenter;
    private BTWndManagerPresenter mBTWndManagerPresenter;
    private final Handler mHandler;

    private boolean btStateInit = false;

    {
        Context context = SystemUIApplication.getSystemUIContext();

        mHandler = new Handler(Looper.getMainLooper());
        mSettingsPresenter = new SettingsPresenter(context);
        mPairedPresenter = new PairedPresenter(context);
        mScanningPresenter = new ScanningPresenter(context);
        mBTWndManagerPresenter = new BTWndManagerPresenter(context);
    }

    ISettingsContract.ISettingsView mSettingsView = new ISettingsContract.ISettingsView() {

        @Override
        public void setPresenter(ISettingsContract.ISettingsPresenter iSettingsPresenter) {

        }

        @Override
        public void removePresenter(ISettingsContract.ISettingsPresenter iSettingsPresenter) {

        }

        @Override
        public void updateAutoAnswer(boolean enable) {

        }

        @Override
        public void updateBluetoothStateChanged(BusinessConstants.BTStateType type) {
            btStateInit = true;
            //回调蓝牙开关状态
            LogUtil.i("ISettingsView -> updateBluetoothStateChanged = " + type);
            if (type == BusinessConstants.BTStateType.TURN_ON) {
                //蓝牙双连接开关
                mSettingsPresenter.setTwinConnectEnable(false);
                //设置自动连接
                mSettingsPresenter.setBluetoothAutoReconnectEnable(true);
                //设置蓝牙可被发现
                mSettingsPresenter.setLocalBtDiscoverable(true);
                //关闭状态变为打开状态时扫描设备
                reqStartScan(0, true);
                //关闭状态变为打开时更新已配对设备
                reqPairedDevices();
            }
            mBtSwitch.setValue(type);
        }

        @Override
        public void updateAutoReconnect(boolean enable) {
            //回调自动连接开关状态
            LogUtil.i("ISettingsView -> updateAutoReconnect = " + enable);
        }

        @Override
        public void updateLocalBluetoothName(String btName) {
            //回调本地蓝牙名称
            LogUtil.i("ISettingsView -> updateLocalBluetoothName = " + btName);
            mBtName.setValue(btName);
        }

        @Override
        public void updatePinCode(String btPinCode) {
            //回调配对码
            LogUtil.i("ISettingsView -> updatePinCode = " + btPinCode);
        }

        @Override
        public void updateLocalBluetoothDiscoverable(boolean discoverable) {
            //回调本地蓝牙是否可以被发现
            LogUtil.i("ISettingsView -> updateLocalBluetoothDiscoverable = " + discoverable);
        }

        @Override
        public void updateAutoDownloadContact(boolean autoDownload) {
            //回调自动下载电话本开关状态
            LogUtil.i("ISettingsView -> updateAutoDownloadContact = " + autoDownload);
            mAutoDownloadPbEnable.setValue(autoDownload);
        }

        @Override
        public void updatePrivateAnswerCall(boolean privateAnswer) {
            //回调私密接听开关状态
            LogUtil.i("ISettingsView -> updatePrivateAnswerCall = " + privateAnswer);
        }

        @Override
        public void updateContactsSyncState(BusinessConstants.SyncState status) {
            //回调电话本同步状态
            LogUtil.i("ISettingsView -> updateContactsSyncState = " + status);
        }

        @Override
        public void updateServiceConnectState(boolean state) {
            //回调绑定蓝牙服务是否成功状态    父类接口会回调执行PairedPresenter#update获取已配对设备
            LogUtil.i("ISettingsView -> updateServiceConnectState = " + state);
        }

        @Override
        public void updateRingtoneInfos(List<RingtoneInfo> list) {
            //回调本地来电铃声列表
            LogUtil.i("ISettingsView -> updateRingtoneInfos = " + list);
        }

        @Override
        public void updataCurRingtonePosition(int position) {
            //回调当前设定的本地来电铃声索引
            LogUtil.i("ISettingsView -> updataCurRingtonePosition = " + position);
        }

        @Override
        public void updateThreePartyCallEnable(boolean enable) {
            //回调三方通话开关状态
            LogUtil.i("ISettingsView -> updateThreePartyCallEnable = " + enable);
        }

        @Override
        public void updateTwinConnectEnable(boolean enable) {
            //回调双蓝牙连接开关状态
            LogUtil.i("ISettingsView -> updateTwinConnectEnable = " + enable);
        }

        @Override
        public void notifyRingStop() {
            //回调预览本地来电铃声结束
            LogUtil.i("ISettingsView -> notifyRingStop = ");
        }

        @Override
        public void updateActiveDeviceChanged(String address, String name, boolean isMutiDev) {
            LogUtil.i("ISettingsView -> updateActiveDeviceChanged = " + address + "--" + name + "--" + isMutiDev);
        }
    };

    IScanningContract.IScanningView mScanningView = new IScanningContract.IScanningView() {

        @Override
        public void setPresenter(IScanningContract.IScanningPresenter iScanningPresenter) {

        }

        @Override
        public void removePresenter(IScanningContract.IScanningPresenter iScanningPresenter) {

        }

        @Override
        public void showScanning() {
            //回调正在扫描附近蓝牙设备
            LogUtil.i("IScanningView -> showScanning");
            mScanningState.setValue(true);
        }

        @Override
        public void showScanFinished() {
            //回调扫描附近蓝牙设备结束
            LogUtil.i("IScanningView -> showScanFinished");
            mScanningState.setValue(false);
        }

        @Override
        public void updateFoundedDevices(List<BluetoothDevice> deviceList) {
            //扫描到设备之后，把扫描到的设备以集合的数据类型返回
            LogUtil.i("IScanningView -> updateFoundedDevices = " + deviceList);
            List<BluetoothDevice> lastDevices = new ArrayList<>();
            lastDevices.addAll(getFoundedDevices().getValue());
            for (int i = 0; i < deviceList.size(); i++) {
                boolean isDeviceInLast = false;
                for (int j = 0; j < lastDevices.size(); j++) {
                    if (deviceList.get(i).getAddress().equals(lastDevices.get(j).getAddress())) {
                        isDeviceInLast = true;
                        break;
                    }
                }
                if (!isDeviceInLast) {
                    lastDevices.add(deviceList.get(i));
                    mFoundedDevices.setValue(lastDevices);
                }
            }
        }

        @Override
        public void showDevicesNotFindMsg() {
            //扫描结束时，如没有扫描到相关设备回调显示没有找到设备的提示信息
            LogUtil.i("IScanningView -> showDevicesNotFindMsg");
        }

        @Override
        public void hideDevicesNotFindMsg() {
            //当有扫描到相关设备时回调隐藏没有找到设备的提示信息
            //实测这个接口回调开始扫描后会回调，P层会先清空 mFoundedDevices
            LogUtil.i("IScanningView -> hideDevicesNotFindMsg");
        }

        @Override
        public void showPairFailedMsg(String name) {
            //蓝牙配对不成功时，回调配对失败信息
            LogUtil.i("IScanningView -> showPairFailedMsg = " + name);
        }

        @Override
        public void showConnectingView() {
            //回调提示当前正在连接设备中
            LogUtil.i("IScanningView -> showConnectingView");
        }

        @Override
        public void hideConnectingView() {
            //回调连接设备结束，隐藏相关正在连接的提示
            LogUtil.i("IScanningView -> hideConnectingView");
        }

        @Override
        public void updateConnectionStatus(BusinessConstants.ConnectionStatus type, String address) {
            //点击搜索列表发起连接设备之后，回调连接的状态
            LogUtil.i("IScanningView -> updateConnectionStatus = " + type + "--" + address);
            BluetoothDevice device = new BluetoothDevice();
            device.setAddress(address);
            switch (type) {
                case SUCCESSED:
                    if (isConnectIPhoneDevice(address)) {
//                        showCarPlayConnectDialog(address);
                    }
                    device.setState(STATE_CONNECTED);
                    break;
                case CONNECTING:
                    device.setState(STATE_CONNECTING);
                    break;
                case FAILED:
                case PAIR_FAILED:
                    device.setState(STATE_CONNECT_FAIL);
                    break;
                default:
                    device.setState(STATE_READY);
            }
            mActiveDevice.setValue(device);
        }

        @Override
        public void showSelectReplaceDeviceView(String connAddr, String[] address, String[] name) {
            LogUtil.i("IScanningView -> showSelectReplaceDeviceView = " + connAddr + "--" + Arrays.toString(address) + "--" + Arrays.toString(name));
        }

        @Override
        public void showPairingView() {
            LogUtil.i("IScanningView -> showPairingView");
        }
    };

    IPairedContract.IPairedView mPairedView = new IPairedContract.IPairedView() {

        @Override
        public void setPresenter(IPairedContract.IPairedPresenter iPairedPresenter) {

        }

        @Override
        public void removePresenter(IPairedContract.IPairedPresenter iPairedPresenter) {

        }

        @Override
        public void updateConnectionStatus(BusinessConstants.ConnectionStatus type, String address, int connType) {
            //已配对列表发起连接设备之后，回调连接的状态
            LogUtil.i("IPairedView -> updateConnectionStatus = " + type + "--" + address + "--" + connType);
            BluetoothDevice device = new BluetoothDevice();
            device.setAddress(address);
            switch (type) {
                case SUCCESSED:
                    device.setState(STATE_CONNECTED);
//                    MediaMmkvUtil.getInstance().encodeString(ContextUtil.get(), CAR_PLAY_CONNECTED, "");
//                    MediaMmkvUtil.getInstance().encodeString(ContextUtil.get(), CAR_PLAY_REFRESH, "");
//                    MediaMmkvUtil.getInstance().encodeString(ContextUtil.get(), CAR_PLAY_FAIL, "");
                    break;
                case CONNECTING:
                    device.setState(STATE_CONNECTING);
                    break;
                case FAILED:
                case PAIR_FAILED:
                    device.setState(STATE_CONNECT_FAIL);
                    break;
                default:
                    device.setState(STATE_READY);
            }
            mActiveDevice.setValue(device);
        }

        @Override
        public void updatePairedDevices(List<BluetoothDevice> list) {
            //请求配对历史成功回调，以list集合数据类型返回
            LogUtil.i("IPairedView -> updatePairedDevices = " + list);
            int pos = 0;
            for (BluetoothDevice device : list) {
                if (isPairedDeviceConnecting) {
                    BluetoothDevice active = mActiveDevice.getValue();
                    if (active != null && device.getAddress().equals(active.getAddress())) {
                        device.setState(active.getState());
                    }
                } else {
                    if (device.isHfpConnected() || device.isA2dpConnected()) {
                        device.setState(STATE_CONNECTED);
                        Collections.swap(list, 0, pos);
                    } else {
                        device.setState(STATE_READY);
                    }
                }
                pos++;
            }
            mPairedDevice.setValue(list);
        }

        @Override
        public void showBTName(String btName) {
            //回调本地蓝牙名称
            LogUtil.i("IPairedView -> showBTName = " + btName);
        }

        @Override
        public void showPairFailedMsg(String name) {
            //蓝牙配对不成功时，回调配对失败信息
            LogUtil.i("IPairedView -> showPairFailedMsg = " + name);
        }

        @Override
        public void showNoPairedDeviceMsg() {
            //历史配对列表为空时，回调没有找到设备的提示信息
            LogUtil.i("IPairedView -> showNoPairedDeviceMsg");
        }

        @Override
        public void hideNoPairedDeviceMsg() {
            //历史配对列表从空变为非空时，回调隐藏没有找到设备的提示信息
            LogUtil.i("IPairedView -> hideNoPairedDeviceMsg");
        }

        @Override
        public void showConnectingView(String address, int connType) {
            //回调正在连接状态
            LogUtil.i("IPairedView -> showConnectingView = " + address + "--" + connType);
            isPairedDeviceConnecting = true;
        }

        @Override
        public void hideConnectingView(String address, int connType) {
            //回调连接结束
            LogUtil.i("IPairedView -> hideConnectingView = " + address + "--" + connType);
            isPairedDeviceConnecting = false;
        }

        @Override
        public void showSelectReplaceDeviceView(String connAddr, String[] address, String[] name) {
            LogUtil.i("IPairedView -> showSelectReplaceDeviceView = " + connAddr + "--" + Arrays.toString(address) + "--" + Arrays.toString(name));
        }

        @Override
        public void onActiveDeviceStateChanged(int state) {
            LogUtil.i("IPairedView -> onActiveDeviceStateChanged = " + state);
        }
    };

    IBTWndMangerContract.IBTWndMangerView mBtWndMangerView = new IBTWndMangerContract.IBTWndMangerView() {

        @Override
        public boolean showPage(BusinessConstants.FRAGMENT_INDEX fragment_index, Bundle bundle) {
            return false;
        }

        @Override
        public boolean showPages(Set<BusinessConstants.FRAGMENT_INDEX> set, Bundle bundle) {
            return false;
        }

        @Override
        public void showWelcomeLoadingView() {

        }

        @Override
        public void hideWelcomeLoadingView() {

        }

        @Override
        public void finishUI() {

        }

        @Override
        public void updateHfpState(boolean state) {
            LogUtil.i("IBTWndMangerView -> updateHfpState = " + state);
            mConnectedDeviceName.setValue(mSettingsPresenter.getRemoteDeviceName(null));
        }

        @Override
        public void updateA2DPState(boolean state) {
            LogUtil.i("IBTWndMangerView -> updateA2DPState = " + state);
            mConnectedDeviceName.setValue(mSettingsPresenter.getRemoteDeviceName(null));
        }

        @Override
        public void showOrEnableBottomBar() {

        }

        @Override
        public void hideOrDisableBottomBar() {

        }

        @Override
        public void onTabSelectedChanged(BusinessConstants.FRAGMENT_INDEX index) {

        }
    };

    {
        mSettingsPresenter.setView(mSettingsView);
        mSettingsView.setPresenter(mSettingsPresenter);
        mSettingsPresenter.init();

        mPairedPresenter.setView(mPairedView);
        mPairedView.setPresenter(mPairedPresenter);
        mPairedPresenter.init();

        mScanningPresenter.setView(mScanningView);
        mScanningView.setPresenter(mScanningPresenter);
        mScanningPresenter.init();

        mBTWndManagerPresenter.setView(mBtWndMangerView);
        mBTWndManagerPresenter.init();
    }

    public BtViewModel(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        LogUtil.i("BtViewModel onCleared");
        if (null != mScanningPresenter) mSettingsPresenter.removeView(mSettingsView);
        if (null != mPairedPresenter) mPairedPresenter.removeView(mPairedView);
        if (null != mScanningPresenter) mScanningPresenter.removeView(mScanningView);
//        ContextUtil.get().unregisterReceiver(mCarPlayReceiver);
//
//        mSettingsPresenter = null;
//        mPairedPresenter = null;
//        mScanningPresenter = null;
    }

    /**
     * 蓝牙开关
     *
     * @param isBluetoothEnabled
     */
    public void reqBtSwitch(boolean isBluetoothEnabled) {
        LogUtil.i("reqBtSwitch " + isBluetoothEnabled);
        mSettingsPresenter.setBluetoothEnable(isBluetoothEnabled);
    }

    public MutableLiveData<BusinessConstants.BTStateType> getBtSwitch() {
        if (mBtSwitch.getValue() == null) {
            LogUtil.i("mBtSwitch = null");
            mBtSwitch.setValue(mSettingsPresenter.isBluetoothEnable() ? BusinessConstants.BTStateType.TURN_ON : BusinessConstants.BTStateType.TURN_OFF);
        }
        return mBtSwitch;
    }

    public Boolean getBtSwitchIsNull() {
        if (mBtSwitch.getValue() == null) {
            LogUtil.i("mBtSwitch = null");
            return false;
        }
        return true;
    }

    /**
     * 开始扫描
     */
    public void reqStartScan(int delay, boolean clearHistory) {
        LogUtil.i("reqStartScan");
        if (!mSettingsPresenter.isBluetoothEnable()) {
            return;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (clearHistory) {
                    mFoundedDevices.setValue(new ArrayList<>());
                }
                mScanningPresenter.startScan();
            }
        }, delay);
    }

    /**
     * 取消扫描
     */
    public void reqCancelScan() {
        LogUtil.i("reqCancelScan");
        mScanningPresenter.cancelScan();
    }

    public MutableLiveData<List<BluetoothDevice>> getFoundedDevices() {
        if (mFoundedDevices.getValue() == null) {
            mFoundedDevices.setValue(new ArrayList<>());
            LogUtil.i("mFoundedDevices is empty");
        }
        return mFoundedDevices;
    }

    /**
     * 设置设备蓝牙名称
     *
     * @param btName
     */
    public void reqBtName(String btName) {
        LogUtil.i("reqBtName = " + btName);
        mSettingsPresenter.setLocalBluetoothName(btName);
    }

    public MutableLiveData<String> getBtName() {
        if (mBtName.getValue() == null) {
            String btName = mSettingsPresenter.getLocalAdapterName();
            LogUtil.i("getBtName = " + btName);
            if (btName == null) {
                mBtName.setValue("");
            } else {
                mBtName.setValue(btName);
            }
        }
        return mBtName;
    }

    /**
     * 获取本地蓝牙地址
     *
     * @return
     */
    public String getBtAddress() {
        String address = mSettingsPresenter.getLocalAdapterAddress();
        LogUtil.i("getBtAddress = " + address);
        return address;
    }

    /**
     * 连接扫描到的设备
     *
     * @param bluetoothDevice
     */
    public void reqScanningConnectDevice(BluetoothDevice bluetoothDevice) {
        mReqPaired = true;
        LogUtil.i("reqScanningConnectDevice bluetoothDevice = " + bluetoothDevice + " name = " + bluetoothDevice.getRemoteDevName() + " bluetoothDevice getAddress= " + bluetoothDevice.getAddress());
        mScanningPresenter.connectDeviceCheckDevice(bluetoothDevice.getAddress());
    }

    /**
     * 确认/取消配对
     */
    public void reqConfirmPairDevice(boolean flag) {
        LogUtil.i("reqCancelPairDevice");
        mHandler.removeCallbacksAndMessages(null);
        try {
            if (flag) {
                NforeBtBaseJar.setPairingConfirmation();
            } else {
                NforeBtBaseJar.cancelPairingUserInput();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接已配对的设备
     *
     * @param bluetoothDevice
     */
    public void reqPairedConnectDevice(BluetoothDevice bluetoothDevice) {
        LogUtil.i("reqPairedConnectDevice bluetoothDevice = " + bluetoothDevice + " name = " + bluetoothDevice.getRemoteDevName() + " bluetoothDevice getAddress= " + bluetoothDevice.getAddress());
        mPairedPresenter.connectDeviceCheckDevice(bluetoothDevice.getAddress());

    }

    /**
     * 断开已连接的设备
     *
     * @param bluetoothDevice
     */
    public void reqDisconnectDevice(BluetoothDevice bluetoothDevice) {
        LogUtil.i("reqDisconnectDevice bluetoothDevice = " + bluetoothDevice + " name = " + bluetoothDevice.getRemoteDevName() + " bluetoothDevice getAddress= " + bluetoothDevice.getAddress());
        mScanningPresenter.disconnectDevice(bluetoothDevice.getAddress());
        mPairedPresenter.update();
    }

    /**
     * 获取已配对设备
     */
    public void reqPairedDevices() {
        LogUtil.i("reqPairedDevices");
        try {
            NforeBtBaseJar.reqBtPairedDevices(1);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除已配对的设备
     *
     * @param address
     */
    public void reqDeletePairedDevice(@NonNull String address) {
        LogUtil.i("reqDeletePairedDevice address = " + address);
        mPairedPresenter.deleteDevice(address);
    }

    /**
     * 自动同步通话记录开关
     *
     * @param enable
     */
    public void setAutoDownloadPbEnable(boolean enable) {
        LogUtil.i("setAutoDownloadPbEnable =" + enable);
        mSettingsPresenter.setAutoDownloadPbEnable(enable);
    }

    public MutableLiveData<Boolean> getAutoDownloadPbEnable() {
        if (mAutoDownloadPbEnable.getValue() == null) {
            Log.i(TAG, "getAutoDownloadPbEnable: isAutoDownloadPbEnable" + mSettingsPresenter.isAutoDownloadPbEnable());
            mAutoDownloadPbEnable.setValue(mSettingsPresenter.isAutoDownloadPbEnable());
        }
        mAutoDownloadPbEnable.setValue(mSettingsPresenter.isAutoDownloadPbEnable());
        LogUtil.i("getAutoDownloadPbEnable=" + mSettingsPresenter.isAutoDownloadPbEnable());
        return mAutoDownloadPbEnable;
    }

    public MutableLiveData<List<BluetoothDevice>> getPairedDevice() {
        if (mPairedDevice.getValue() == null) {
            mPairedDevice.setValue(new ArrayList<>());
        }
        LogUtil.i("getPairedDevice=" + mPairedDevice.getValue());
        return mPairedDevice;
    }

    public MutableLiveData<Boolean> getScanningState() {
        if (mScanningState.getValue() == null) {
            mScanningState.setValue(false);
        }
        LogUtil.i("getScanningState=" + mScanningState.getValue());
        return mScanningState;
    }

    public UnPeekLiveData<BluetoothDevice> getActiveDevice() {
        LogUtil.i("getActiveDevice=" + mActiveDevice.getValue());
        return mActiveDevice;
    }

    public MutableLiveData<String> getConnectedDeviceName() {
        LogUtil.i("getConnectedDeviceName=" + mConnectedDeviceName.getValue());
        return mConnectedDeviceName;
    }


    public boolean isHfpConnected() {
        boolean state = mSettingsPresenter.isHfpConnected();
        LogUtil.i("isHfpConnected=" + state);
        return state;
    }

    public boolean isA2dpConnected() {
        boolean state = mSettingsPresenter.isA2dpConnected();
        LogUtil.i("isA2dpConnected=" + state);
        return state;
    }

    /**
     * 蓝牙回调
     *
     * @return
     */
    public boolean getBtStateInit() {
        return btStateInit;
    }


    public MutableLiveData<BluetoothPairingEntity> getPairingDevice() {
        LogUtil.i("getPairingDevice=" + mPairingDevice);
        return mPairingDevice;
    }

    public void setPairingDevice(BluetoothPairingEntity entity) {
        LogUtil.i("setPairingDevice=" + entity);
        mReqPaired = false;
        mPairingDevice.postValue(entity);
    }

    public boolean isReqPaired() {
        return mReqPaired;
    }

    private boolean isConnectIPhoneDevice(String address) {
        boolean flag = false;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            android.bluetooth.BluetoothDevice device = adapter.getRemoteDevice(address);
            if (device != null) {
                @SuppressLint("MissingPermission")
                ParcelUuid[] uuids = device.getUuids();
                if (uuids != null && uuids.length > 0) {
                    for (int i = 0; i < uuids.length; i++) {
                        if (UUID_IAP.equals(uuids[i])) {
                            flag = true;
                            break;
                        }
                    }
                }
            }
        }
        return flag;
    }

}
