package com.adayo.systemui.functional.hvac._view.dialog;

import android.content.Context;

import androidx.annotation.NonNull;

import com.adayo.systemui.functional.hvac._binder.IntellIonsViewBinder;
import com.adayo.systemui.functional.hvac._binder.PMViewBinder;
import com.adayo.systemui.functional.hvac._binder.PurificationViewBinder;
import com.adayo.systemui.functional.hvac._binder.RapidViewBinder;
import com.adayo.systemui.functional.hvac._binder.SelfDesiccationViewBinder;
import com.adayo.systemui.functional.hvac._binder.WaterIonsViewBinder;
import com.adayo.systemui.windows.dialogs.BindViewBaseDialog;
import com.android.systemui.R;


public class IntelligentAirDialogBindView extends BindViewBaseDialog implements OnCloseIntelligentWindow {

    public IntelligentAirDialogBindView(@NonNull Context context, int dialogWidth, int dialogHeight) {
        super(context, dialogWidth, dialogHeight);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void createViewBinder() {
        //智能空调
        insertViewBinder(new PMViewBinder());
        insertViewBinder(new IntellIonsViewBinder());
        insertViewBinder(new PurificationViewBinder());
        insertViewBinder(new SelfDesiccationViewBinder());
        insertViewBinder(new WaterIonsViewBinder());
        RapidViewBinder _rapid_view_binder = new RapidViewBinder();
        _rapid_view_binder.set_intelligentWindow(this);
        insertViewBinder(_rapid_view_binder);
    }

    @Override
    protected int acquireResourceId() {
        return R.layout.cb_intell_layout;
    }

    @Override
    public void onCloseDialog() {
        dismiss();
    }
}
