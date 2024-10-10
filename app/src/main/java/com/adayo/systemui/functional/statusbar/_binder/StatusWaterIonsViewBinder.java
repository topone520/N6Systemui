package com.adayo.systemui.functional.statusbar._binder;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.widget.ImageView;

import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.deviceservice.IDeviceFuncCallBack;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.R;

import java.util.Objects;

public class StatusWaterIonsViewBinder extends AbstractViewBinder<Integer> {

    private static final int TBOX = 10003;
    private static final int MSG_UPDATE_VIEW = 0;
    private ImageView _icon_water_ions;

    private final Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message message) {
            LogUtil.d("handleMessage: message.what = " + message.what);
            if (message.what == MSG_UPDATE_VIEW) {
                Bundle bundle = (Bundle) message.obj;
                updateNegativeViewStatus(bundle);
            } else if (message.what == TBOX) {
                Bundle bundle = (Bundle) message.obj;
                updateTBoxView(bundle);
            }
        }
    };

    public StatusWaterIonsViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }

    @Override
    protected void _bind_view(View view) {
        _icon_water_ions = view.findViewById(R.id.icon_water_ions);

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

    private void updateNegativeViewStatus(Bundle bundle) {
        if (bundle == null) return;
        LogUtil.d("updateViewStatus: ");
        String messageId = bundle.getString("message_id");
        if (Objects.requireNonNull(messageId).equals(HvacSOAConstant.MSG_EVENT_NEGATIVE_ION_SWITCH)){
            if (!bundle.containsKey("value")) {
                LogUtil.d("updateNegativeViewStatus: !bundle.containsKey");
                _icon_water_ions.setVisibility(View.GONE);
                return;
            }
            if (bundle.getInt("value") == 1) {
                _icon_water_ions.setVisibility(View.VISIBLE);
                _icon_water_ions.setImageResource(R.mipmap.ivi_top_icon_ion_on);
            } else {
                _icon_water_ions.setVisibility(View.GONE);
                _icon_water_ions.setImageResource(R.mipmap.ivi_top_icon_ion_off);
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