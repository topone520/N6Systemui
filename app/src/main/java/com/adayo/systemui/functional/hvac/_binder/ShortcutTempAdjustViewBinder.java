package com.adayo.systemui.functional.hvac._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusErrorCodeEnum;
import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.contract.IViewBinder;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderFloat;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.windows.views.hvac.HorTempSelectorView;

public class ShortcutTempAdjustViewBinder extends AbstractViewBinder<Float> {
    private static final String TAG = ShortcutTempAdjustViewBinder.class.getSimpleName();
    private final int _position;
    private final int _resource_id;
    private HorTempSelectorView _temp_select_view;

    public ShortcutTempAdjustViewBinder(ViewBinderProviderFloat provider, int position, int resource_id) {
        super(TAG, provider);
        _position = position;
        _resource_id = resource_id;
    }

    @Override
    public void _bind_view(View view) {
        _temp_select_view = view.findViewById(_resource_id);
        _temp_select_view.set_listener(new HorTempSelectorView.OnSelectPositionListener() {
            @Override
            public void onSelectTemp(float temp) {
                _set_value(temp);
            }

            @Override
            public void resetViewTimer() {

            }
        });
    }

    @Override
    protected void _update_ui(Float value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, "_update_ui: bundle = " + bundle.toString());
        float value = bundle.getFloat("value");
        int area = bundle.getInt("target");
        if (_position == area) _temp_select_view._update_ui(value);
    }

    public static class Builder {

        public static IViewBinder createLeft(int resource_id) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", AreaConstant.BAIC_LEFT_FRONT);
            bundle.putInt("action", AreaConstant.HVAC_TEMP_ACTION_SPECIFIC);
            return createInstance(SeatSOAConstant.LEFT_FRONT, bundle, resource_id);
        }

        public static IViewBinder createRight(int resource_id) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", AreaConstant.BAIC_RIGHT_FRONT);
            bundle.putInt("action", AreaConstant.HVAC_TEMP_ACTION_SPECIFIC);
            return createInstance(SeatSOAConstant.RIGHT_FRONT, bundle, resource_id);
        }

        private static IViewBinder createInstance(final int position, final Bundle bundle, int resource_id) {
            ViewBinderProviderFloat provider = (ViewBinderProviderFloat) new ViewBinderProviderFloat.Builder()
                    .withService(HvacService.getInstance())
                    .withBundle(bundle)
                    .withGetMessageName(HvacSOAConstant.MSG_GET_TEMPERATURE)
                    .withSetMessageName(HvacSOAConstant.MSG_SET_TEMPERATURE_VALUE_ACTION)
                    .withEventMessageName(HvacSOAConstant.MSG_EVENT_TEMPERATURE)
                    .withInitialValue(17.5F)
                    .build();
            return new ShortcutTempAdjustViewBinder(provider, position, resource_id);
        }
    }
}
