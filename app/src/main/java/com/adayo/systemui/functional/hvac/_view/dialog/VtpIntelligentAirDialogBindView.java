package com.adayo.systemui.functional.hvac._view.dialog;

import android.content.Context;

import androidx.annotation.NonNull;

import com.adayo.systemui.functional.hvac._binder.PurificationViewBinder;
import com.adayo.systemui.functional.hvac._binder.RapidViewBinder;
import com.adayo.systemui.functional.hvac._binder.SelfDesiccationViewBinder;
import com.adayo.systemui.functional.hvac._binder.VtpPMViewBinder;
import com.adayo.systemui.functional.hvac._binder.WaterIonsViewBinder;
import com.adayo.systemui.windows.dialogs.BindViewBaseDialog;
import com.android.systemui.R;


public class VtpIntelligentAirDialogBindView extends BindViewBaseDialog implements OnCloseIntelligentWindow {
    private static final String TAG = VtpBackBlowingModeDialogBindView.class.getName();

    public VtpIntelligentAirDialogBindView(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }


    @Override
    protected void initView() {
    }

    @Override
    protected void createViewBinder() {
        //智能空调
        insertViewBinder(new VtpPMViewBinder());
        insertViewBinder(new PurificationViewBinder());
        insertViewBinder(new SelfDesiccationViewBinder());
        insertViewBinder(new WaterIonsViewBinder());
        RapidViewBinder _rapid_view_binder = new RapidViewBinder();
        _rapid_view_binder.set_intelligentWindow(this);
        insertViewBinder(_rapid_view_binder);
    }

    @Override
    protected int acquireResourceId() {
        return R.layout.vtp_cb_intell_layout;
    }

    @Override
    public void onCloseDialog() {
        dismiss();
    }
}
