package com.adayo.systemui.functional.statusbar._binder;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.deviceservice.IDeviceFuncCallBack;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import java.util.Objects;

public class StatusPmGroupViewBinder extends AbstractViewBinder<Integer> {

    private static final int TBOX = 10003;
    private static final int MSG_UPDATE_VIEW = 0;
    private RelativeLayout _rl_pm_group;
    private TextView _tv_pm_value;
    private ImageView _icon_pm;

    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message message) {
            LogUtil.d("handleMessage: message.what = " + message.what);
            if (message.what == MSG_UPDATE_VIEW) {
                Bundle bundle = (Bundle) message.obj;
                updatePmViewStatus(bundle);
            } else if (message.what == TBOX) {
                Bundle bundle = (Bundle) message.obj;
                updateTBoxView(bundle);
            }
        }
    };

    public StatusPmGroupViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }

    @Override
    protected void _bind_view(View view) {
        _rl_pm_group = view.findViewById(R.id.rl_pm_group);
        _tv_pm_value = view.findViewById(R.id.tv_pm_value);
        _icon_pm = view.findViewById(R.id.icon_pm);

        AAOP_DeviceServiceManager.getInstance().registerDeviceFuncListener(iDeviceCallBack, "com.android.systemui", "TBoxDevice");
    }

    public IDeviceFuncCallBack.Stub iDeviceCallBack = new IDeviceFuncCallBack.Stub() {

        @Override
        public int onChangeListener(Bundle bundle) throws RemoteException {
            Message msg = handler.obtainMessage(TBOX);
            msg.obj = bundle;
            handler.sendMessage(msg);
            return 0;
        }
    };

    private void updatePmViewStatus(Bundle bundle) {
        if (bundle == null) return;
        LogUtil.d("updateViewStatus: ");
        String messageId = bundle.getString("message_id");
        if (Objects.requireNonNull(messageId).equals(HvacSOAConstant.MSG_EVENT_AIR_QUALITY)){
            if (!bundle.containsKey("value")) {
                LogUtil.d("updatePmViewStatus: !bundle.containsKey");
                _rl_pm_group.setVisibility(View.GONE);
                return;
            }
            _rl_pm_group.setVisibility(View.VISIBLE);
            int pmNum = bundle.getInt("value");
            LogUtil.d("updatePmView: pmNum = " + pmNum);
            if (pmNum > AreaConstant.PM_100) {
                _tv_pm_value.setTextSize(10);
            } else {
                _tv_pm_value.setTextSize(32);
            }
            if (pmNum >= AreaConstant.PM_0 && pmNum <= AreaConstant.PM_50) {
                _tv_pm_value.setText(SystemUIApplication.getSystemUIContext().getString(R.string.status_pm_excellent));
            } else if (pmNum > AreaConstant.PM_50 && pmNum <= AreaConstant.PM_100) {
                _tv_pm_value.setText(SystemUIApplication.getSystemUIContext().getString(R.string.status_pm_good));
            } else if (pmNum > AreaConstant.PM_100 && pmNum <= AreaConstant.PM_150) {
                _tv_pm_value.setText(SystemUIApplication.getSystemUIContext().getString(R.string.status_pm_mild));
            } else if (pmNum > AreaConstant.PM_150 && pmNum <= AreaConstant.PM_200) {
                _tv_pm_value.setText(SystemUIApplication.getSystemUIContext().getString(R.string.status_pm_moderate));
            } else if (pmNum > AreaConstant.PM_200 && pmNum <= AreaConstant.PM_300) {
                _tv_pm_value.setText(SystemUIApplication.getSystemUIContext().getString(R.string.status_pm_severe));
            } else {
                _tv_pm_value.setText(SystemUIApplication.getSystemUIContext().getString(R.string.status_pm_serious));
            }
        }
    }

    private void updateTBoxView(Bundle bundle) {
        if (bundle.containsKey("ServiceProvider")) {
            String ServiceProvider = bundle.getString("ServiceProvider");//“00000”：不显示（没有注册到网络）；“46000”：中国移动；“46001”：中国联通；“46002”：中国移动；“46003”：中国电信；
            LogUtil.d("updateTBoxView: ServiceProvider = " + ServiceProvider);
        }
        if (bundle.containsKey("SignalQuality")) {
            int SignalQuality = bundle.getInt("SignalQuality");//从0-5代表网络信号强度，0为无网络信号，5为满格网络信号强度
            LogUtil.d("updateTBoxView: SignalQuality = " + SignalQuality);
        }
        if (bundle.containsKey("WanConnInfo")) {
            int WanConnInfo = bundle.getInt("WanConnInfo");//0：NoNetwork 1：Connecting 2：2G在线 3：3G在线 4：4G在线 5：5G在线
            LogUtil.d("updateTBoxView: WanConnInfo = " + WanConnInfo);
        }
        if (bundle.containsKey("DataSwitch")) {
            int DataSwitch = bundle.getInt("DataSwitch");//0：关闭数据通信 1：开启数据通信
            LogUtil.d("updateTBoxView: DataSwitch = " + DataSwitch);
        }
    }

    @Override
    protected void _update_ui(Integer integer) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}