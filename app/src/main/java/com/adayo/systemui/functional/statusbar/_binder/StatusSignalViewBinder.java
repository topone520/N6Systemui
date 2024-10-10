package com.adayo.systemui.functional.statusbar._binder;

import static com.adayo.systemui.contents.PublicContents.CHINA_MOBILE;
import static com.adayo.systemui.contents.PublicContents.CHINA_NET;
import static com.adayo.systemui.contents.PublicContents.CHINA_UNICOM;
import static com.adayo.systemui.contents.PublicContents.TYPE_SWITCH_POPUP_SIGNAL;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.bean.BluetoothInfo;
import com.adayo.systemui.bean.SignalInfo;
import com.adayo.systemui.interfaces.BaseCallback;
import com.adayo.systemui.manager.BluetoothControllerImpl;
import com.adayo.systemui.manager.PopupsManager;
import com.adayo.systemui.manager.SignalControllerImpl;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.views.IconView;
import com.adayo.systemui.windows.views.PopupSwitchView;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class StatusSignalViewBinder extends AbstractViewBinder<Integer> {

    private TextView _operator;
    private IconView _icon_signal;
    private PopupSwitchView _popupSignalView;

    public StatusSignalViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }

    @Override
    protected void _bind_view(View view) {
        _operator = view.findViewById(R.id.operator);
        BluetoothControllerImpl.getInstance().addCallback((BaseCallback<BluetoothInfo>) this::updateOperatorView);

        _icon_signal = view.findViewById(R.id.icon_signal);
        _icon_signal.setTag(false);
        _icon_signal.setBackgroundIcon(R.mipmap.ivi_top_icon_signal_0_n);
        SignalControllerImpl.getInstance().addCallback((BaseCallback<SignalInfo>) this::updateSignalView);
        _icon_signal.setOnClickListener(v -> {
            if (_popupSignalView == null) {
                _popupSignalView = new PopupSwitchView(SystemUIApplication.getSystemUIContext());
                // _popupSignalView.setPopupType(TYPE_SWITCH_POPUP_SIGNAL);
            }
            setSwitchPopupWindow(_popupSignalView, 2795);
        });
    }

    private void updateSignalView(SignalInfo signalInfo) {
        int signalLevel = signalInfo.getSignalLevel();
        int signalType = signalInfo.getSignalType();//0：NoNetwork 1：Connecting 2：2G在线 3：3G在线 4：4G在线 5：5G在线
        LogUtil.d("updateSignalView---" + signalInfo.toString());
        if (signalInfo.getSignalSwitchState()) {
            if (signalLevel == 0) {
                _icon_signal.setBackgroundIcon(R.mipmap.ivi_top_icon_signal_0_n);
            } else if (signalLevel > 0 && signalLevel <= 20) {
                _icon_signal.setBackgroundIcon(R.mipmap.ivi_top_icon_signal_1_n);
            } else if (signalLevel > 20 && signalLevel <= 40) {
                _icon_signal.setBackgroundIcon(R.mipmap.ivi_top_icon_signal_2_n);
            } else if (signalLevel > 40 && signalLevel <= 60) {
                _icon_signal.setBackgroundIcon(R.mipmap.ivi_top_icon_signal_3_n);
            } else if (signalLevel > 60 && signalLevel <= 80) {
                _icon_signal.setBackgroundIcon(R.mipmap.ivi_top_icon_signal_4_n);
            } else if (signalLevel > 80 && signalLevel <= 100) {
                _icon_signal.setBackgroundIcon(R.mipmap.ivi_top_icon_signal_5_n);
            }
            if (signalType == 0 || signalType == 1) {
                _icon_signal.setForegroundIconVisibility(View.GONE);
            } else if (signalType == 2) {
                _icon_signal.setForegroundIcon(R.mipmap.ivi_top_icon_signal_2g_n, View.VISIBLE);
            } else if (signalType == 3) {
                _icon_signal.setForegroundIcon(R.mipmap.ivi_top_icon_signal_3g_n, View.VISIBLE);
            } else if (signalType == 4) {
                _icon_signal.setForegroundIcon(R.mipmap.ivi_top_icon_signal_4g_n, View.VISIBLE);
            } else if (signalType == 5) {
                _icon_signal.setForegroundIcon(R.mipmap.ivi_top_icon_signal_5g_n, View.VISIBLE);
            }
        } else {
            _icon_signal.setBackgroundIcon(R.mipmap.ivi_top_icon_signal_0_n);
            _icon_signal.setForegroundIconVisibility(View.GONE);
        }
    }

    private void updateOperatorView(BluetoothInfo data) {
        if (null != data) {
            LogUtil.d("BluetoothControllerImpl getOperator = " + data.getOperator());
            String operatorStr = data.getOperator();
            if (!TextUtils.isEmpty(operatorStr) && !"null".equals(operatorStr)) {
                switch (operatorStr) {
                    case CHINA_MOBILE:
                        operatorStr = SystemUIApplication.getSystemUIContext().getString(R.string.cmcc);
                        break;
                    case CHINA_NET:
                        operatorStr = SystemUIApplication.getSystemUIContext().getString(R.string.chinanet);
                        break;
                    case CHINA_UNICOM:
                        operatorStr = SystemUIApplication.getSystemUIContext().getString(R.string.china_unicom);
                        break;
                    default:
                        break;
                }
                _operator.setText(operatorStr);
                _operator.setVisibility(data.isA2dpMount() || data.isHfpMount() ? View.VISIBLE : View.GONE);
            } else {
                _operator.setVisibility(View.GONE);
            }
        }
    }

    private void setSwitchPopupWindow(View popupView, int x) {
        if ((boolean) _icon_signal.getTag()) {
            PopupsManager.getInstance().dismiss();
        } else {
            _icon_signal.post(() -> {
                PopupsManager.getInstance().showAtLocation(_icon_signal, popupView, x, 0, false, 10000, R.style.PopupWindowInOutAnimation, 2066, true);
            });
        }
    }

    @Override
    protected void _update_ui(Integer integer) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}
