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
import com.adayo.systemui.functional.seat._binder.SeatMassageMoveViewBinder;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.utils.TimeUtils;
import com.adayo.systemui.windows.views.hvac.EasyPickerView;
import com.adayo.systemui.windows.views.hvac.VerSelectedView;

import java.util.Arrays;

public class TempAdjustViewBinder extends AbstractViewBinder<Float> {
    private static final String TAG = TempAdjustViewBinder.class.getSimpleName();
    private final int _position;
    private final int _resource_id;
    private EasyPickerView _temp_select_view;
    private final String[] TR_ARRAY = {"Hi", "32.0", "31.5", "31.0", "30.5",
            "30.0", "29.5", "29.0", "28.5", "28.0",
            "27.5", "27.0", "26.5", "26.0", "25.5",
            "25.0", "24.5", "24.0", "23.5", "23.0",
            "22.5", "22.0", "21.5", "21.0", "20.5",
            "20.0", "19.5", "19.0", "18.5", "18.0", "Lo"};

    public TempAdjustViewBinder(ViewBinderProviderFloat provider, int position, int resource_id) {
        super(TAG, provider);
        _position = position;
        _resource_id = resource_id;
    }

    @Override
    public void _bind_view(View view) {
        _temp_select_view = view.findViewById(_resource_id);
        _temp_select_view.setDataList(Arrays.asList(TR_ARRAY));
        _temp_select_view.setOnScrollChangedListener(new EasyPickerView.OnScrollChangedListener() {
            @Override
            public void onChangeValue(float value) {
                AAOP_LogUtils.d(TAG, "temp = " + value);
                _set_value(value);
                ReportBcmManager.getInstance().sendHvacReport("8630003", "", TimeUtils.getTempJson(value, _position));
            }

            @Override
            public void onStopGet() {
                //调用get
                Bundle bundle = new Bundle();
                bundle.putInt("target", _position);
                ADSBusReturnValue value = new ADSBusReturnValue();
                HvacService.getInstance().invoke(HvacSOAConstant.MSG_GET_TEMPERATURE, bundle, value);
                _update_ui(value.getmReturnValue());
            }
        });
    }

    @Override
    protected void _update_ui(Float value) {
        AAOP_LogUtils.d(TAG, "--------------------value = " + value);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, "_update_ui: bundle = " + bundle.toString());
        float value = bundle.getFloat("value");
        int area = bundle.getInt("target");
        if (_position == area) _temp_select_view.moveTo(value);
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
            return new TempAdjustViewBinder(provider, position, resource_id);
        }
    }
}
