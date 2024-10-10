package com.adayo.systemui.functional.hvac._binder;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.contract.IViewBinder;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.manager.TimerManager;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.android.systemui.R;

public class DefrostViewBinder extends AbstractViewBinder<Integer> implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = DefrostViewBinder.class.getSimpleName();
    private CheckBox _cb_defrost;

    public DefrostViewBinder(ViewBinderProviderInteger provider) {
        super(TAG, provider);
    }

    @Override
    public void _bind_view(View view) {
        _cb_defrost = view.findViewById(R.id.cb_wind_driver_defrost);
        _init_click();
    }

    private void _init_click() {
        _cb_defrost.setOnCheckedChangeListener(this);
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, "_update_ui: bundle = " + bundle.toString());
        //控制位置
        //模式
        int blowing_mode = bundle.getInt("value");
        _cb_defrost.setChecked((blowing_mode == AreaConstant.BLOW_WINDOW || blowing_mode == AreaConstant.BLOW_FEET_WINDOW));

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!buttonView.isPressed()) return;//代码设置不可用
        _set_value(0);
    }

    public static class Builder {

        public static IViewBinder createLeft() {
            Bundle bundle = new Bundle();
            bundle.putInt("target", AreaConstant.BAIC_LEFT_FRONT);
            bundle.putInt("value", 3);
            return createInstance(bundle);
        }

        private static IViewBinder createInstance(final Bundle bundle) {
            ViewBinderProviderInteger provider = (ViewBinderProviderInteger) new ViewBinderProviderInteger.Builder()
                    .withBundle(bundle)
                    .withService(HvacService.getInstance())
                    .withGetMessageName(HvacSOAConstant.MSG_GET_BLOWING_MODE)
                    .withSetMessageName(HvacSOAConstant.MSG_BTN_SET_BLOWING_MODE)
                    .withEventMessageName(HvacSOAConstant.MSG_EVENT_BLOWING_MODE)
                    .withInitialValue(0)
                    .withCompare(false)
                    .build();
            return new DefrostViewBinder(provider);
        }
    }
}
