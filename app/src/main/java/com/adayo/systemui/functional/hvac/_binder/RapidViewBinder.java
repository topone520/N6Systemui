package com.adayo.systemui.functional.hvac._binder;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.functional.hvac._view.dialog.OnCloseIntelligentWindow;
import com.adayo.systemui.functional.hvac._view.dialog.StartRapidDialog;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.proxy.vehicle.VehicleWindowService;
import com.adayo.systemui.utils.RapidTimeUtils;
import com.adayo.systemui.utils.ViewStyleUtils;
import com.adayo.systemui.windows.dialogs.HvacRun180SRapidView;
import com.adayo.systemui.windows.dialogs.HvacRun3SRapidView;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.HashSet;

public class RapidViewBinder extends AbstractViewBinder<Integer> {

    private static final String TAG = RapidViewBinder.class.getSimpleName();
    private LinearLayout _cb_rapid;
    private boolean _is_open_rapid;
    private int _rapid_status;
    private OnCloseIntelligentWindow _intelligentWindow;

    Handler handler = new Handler();
    private static final HashSet<String> _temp_event_list = new HashSet<>(Arrays.asList(HvacSOAConstant.MSG_EVENT_EXTREME_SPEED_SWITCH, HvacSOAConstant.HVAC_ECC_INSDT_REVEICE));

    public void set_intelligentWindow(OnCloseIntelligentWindow _intelligentWindow) {
        this._intelligentWindow = _intelligentWindow;
    }

    public RapidViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(HvacService.getInstance())
                .withGetMessageName(HvacSOAConstant.MSG_GET_EXTREME_SPEED_SWITCH)
                .withSetMessageName(HvacSOAConstant.MSG_SET_EXTREME_SPEED_SWITCH)
                .withRelationMessages(_temp_event_list)
                .withInitialValue(0)
                .withCompare(false)
                .build());
    }

    /**
     * 极速控温逻辑
     * 点击 弹出 再次确认 开启的按钮，并关闭当前父弹框，根据上报的值来确认是否要开启控温
     * 上报：1：打开，开启180S计时器
     * 上报：2：待机，持续监听温度上报接口，温度达到要求再次下发急速控温 开启信号
     * 上报：0：关闭
     *
     * @param view
     */

    @Override
    public void _bind_view(View view) {
        EventBus.getDefault().register(this);
        StartRapidDialog startRapidDialog = new StartRapidDialog(view.getContext(), 800, 440);
        _cb_rapid = view.findViewById(R.id.cb_rapid);

        _cb_rapid.setOnClickListener(v -> {
            if (_is_open_rapid) {
                _set_value(HvacSOAConstant.HVAC_OFF_IO);
            } else {
                //tankaung
                startRapidDialog.show();
                if (_intelligentWindow != null) _intelligentWindow.onCloseDialog();
            }
        });
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    /**
     * 0关闭 1升温 2降温 3待机
     *
     * @param bundle
     */
    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, " bundle = " + bundle.toString());
        String message_id = bundle.getString("message_id", "Node");
        switch (message_id) {
            case HvacSOAConstant.MSG_EVENT_EXTREME_SPEED_SWITCH:
                //急速控温状态
                int rapid_status = bundle.getInt("value");
                _rapid_status = rapid_status;
                //除了关闭都是高亮
                _is_open_rapid = rapid_status != AreaConstant.HVAC_OFF_IO;
                AAOP_LogUtils.d(TAG, "value = " + rapid_status + " is_open = " + _is_open_rapid);
                _cb_rapid.setSelected(_is_open_rapid);
                if (_is_open_rapid && _rapid_status == AreaConstant.HVAC_ON_IO) {
                    //开启计时器
                    RapidTimeUtils.startTimer();
                    HvacRun3SRapidView.getInstance().rapidShow(SystemUIApplication.getSystemUIContext());
                    if (_rapid_status == 2) {
                        close_window();
                    }
                } else {
                    handler.removeCallbacksAndMessages(null);
                    RapidTimeUtils.stopTimer();
                }
                HvacRun180SRapidView.getInstance().upDataRapidStatus(rapid_status);
                break;
            case HvacSOAConstant.HVAC_ECC_INSDT_REVEICE:
                //温度上报
                float temp = bundle.getFloat("value");
                int type = bundle.getInt("type", -1);
                if (type == 0 && _rapid_status == 3 && (temp < AreaConstant.RAPID_MIN || temp > AreaConstant.RAPID_MAX)) {
                    _set_value(HvacSOAConstant.HVAC_ON_IO);
                }
                break;
        }
    }

    public void close_window() {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> {
            //下发四个门窗关闭指令
            if (_rapid_status != AreaConstant.HVAC_OFF_IO) {
//            t.MSG_SET_FL_WINDOW_STATE);
//            t.MSG_SET_FR_WINDOW_STATE);
//            MSG_SET_RL_WINDOW_STATE);
//            MSG_SET_RR_WINDOW_STATE);
                //0x5自动关窗
                Bundle bundle = new Bundle();
                bundle.putInt("value", 5);
                VehicleWindowService.getInstance().invoke("MSG_SET_FL_WINDOW_STATE", bundle, new ADSBusReturnValue());
                VehicleWindowService.getInstance().invoke("MSG_SET_FR_WINDOW_STATE", bundle, new ADSBusReturnValue());
                VehicleWindowService.getInstance().invoke("MSG_SET_RL_WINDOW_STATE", bundle, new ADSBusReturnValue());
                VehicleWindowService.getInstance().invoke("MSG_SET_RR_WINDOW_STATE", bundle, new ADSBusReturnValue());
            }
        }, 30000);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(int value) {
        AAOP_LogUtils.d(TAG, "value = " + value);
        _set_value(value);
    }
}
