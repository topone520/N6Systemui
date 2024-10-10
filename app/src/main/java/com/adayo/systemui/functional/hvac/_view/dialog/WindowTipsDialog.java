package com.adayo.systemui.functional.hvac._view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.windows.dialogs.BindViewBaseDialog;
import com.android.systemui.R;


public class WindowTipsDialog extends BindViewBaseDialog {
    private final String TAG = WindowTipsDialog.class.getSimpleName();
    private final Bundle _bundle = new Bundle();

    public WindowTipsDialog(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }

    @Override
    protected void initView() {
        findViewById(R.id.tv_start).setOnClickListener(view -> start_rapid(11));
        findViewById(R.id.tv_close).setOnClickListener(v -> start_rapid(1));
    }

    private void start_rapid(int value) {
        _bundle.putInt("value", value);
        boolean resultWinRapid = HvacService.getInstance().invoke(HvacSOAConstant.MSG_SET_EXTREME_SPEED_SWITCH, _bundle, new ADSBusReturnValue());
        Log.d(TAG, "rapidSwitch: " + resultWinRapid);
        dismiss();
    }

    @Override
    protected void createViewBinder() {

    }

    @Override
    protected int acquireResourceId() {
        return R.layout.cb_rapid_window_layout;
    }
}
