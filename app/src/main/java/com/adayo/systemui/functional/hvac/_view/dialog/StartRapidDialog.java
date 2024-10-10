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
import com.adayo.systemui.windows.dialogs.BaseDialog;
import com.adayo.systemui.windows.dialogs.BindViewBaseDialog;
import com.adayo.systemui.windows.dialogs.HvacRun180SRapidView;
import com.adayo.systemui.windows.dialogs.HvacRun3SRapidView;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;


public class StartRapidDialog extends BindViewBaseDialog {
    private final String TAG = StartRapidDialog.class.getSimpleName();
    private TextView tv_start;
    private TextView tv_close;

    public StartRapidDialog(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }

    @Override
    protected void initView() {
        WindowTipsDialog dialog = new WindowTipsDialog(getContext(), 800, 440);
        tv_start = findViewById(R.id.tv_start);
        tv_close = findViewById(R.id.tv_close);

        tv_close.setOnClickListener(v -> dismiss());
        tv_start.setOnClickListener(view -> {
            dialog.show();
            dismiss();
        });
    }

    @Override
    protected void createViewBinder() {

    }

    @Override
    protected int acquireResourceId() {
        return R.layout.cb_rapid_layout;
    }
}
