package com.adayo.systemui.functional.fragrance._binder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.FragranceSOAConstant;
import com.adayo.systemui.proxy.vehicle.FragranceService;
import com.adayo.systemui.windows.views.fragrance.FragranceSlotView;
import com.adayo.systemui.windows.views.fragrance.VtpFragranceSlotView;

//香氛浓度
public class VtpFragranceConcentrationViewBinder extends AbstractViewBinder<Integer> {

    private static final String TAG = VtpFragranceConcentrationViewBinder.class.getSimpleName();
    private VtpFragranceSlotView _slot_view;
    private final int _slot_view_id;

    public VtpFragranceConcentrationViewBinder(int slotViewId) {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(FragranceService.getInstance())
                .withGetMessageName(FragranceSOAConstant.MSG_GET_FRAGRANCE_CONCENTRATION)
                .withSetMessageName(FragranceSOAConstant.MSG_CHANGE_FRAGRANCE_CONCENTRATION)
                .withEventMessageName(FragranceSOAConstant.MSG_EVENT_FRAGRANCE_CONCENTRATION)
                .withInitialValue(0)
                .build());
        _slot_view_id = slotViewId;
    }

    @Override
    public void _bind_view(View view) {
        _slot_view = view.findViewById(_slot_view_id);
        _slot_view.setSlideListener(level -> _set_value(level));
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {
        int value = bundle.getInt("value");
        _slot_view.updateConcentrationUI(value);
        Log.d(TAG, "_update_ui: value = " + value);
    }
}
