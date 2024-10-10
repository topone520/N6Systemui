package com.adayo.systemui.functional.hvac._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.proxy.vehicle.Dimensional;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.windows.views.hvac.HvacMDVAdjustView;
import com.adayo.systemui.windows.views.hvac.HvacMDVShowView;
import com.android.systemui.R;

public class MDVAdjustViewBinder extends AbstractViewBinder<Integer>  {
    private static final String TAG = MDVAdjustViewBinder.class.getSimpleName();
    private static final int THRESHOLD = 10;
    private static final int INIT_NUMBER = 0;
    private final int _position;
    private final int _resource_id;
    private HvacMDVAdjustView _mdv_adjust_view;
    private MDVAdjustListener _listener;
    private HvacMDVShowView _mdv_show_view;
    //用来记录上报状态，更新 _mdv_show_view
    private int _mdv_horizontal, _mdv_vertical;

    public void set_listener(MDVAdjustListener _listener) {
        this._listener = _listener;
    }

    public MDVAdjustViewBinder(int position, int resource_id) {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(HvacService.getInstance())
                .withGetMessageName(HvacSOAConstant.MSG_GET_BLOWING_DIRECTION)
                .withSetMessageName(HvacSOAConstant.MSG_SET_BLOWING_DIRECTION)
                .withEventMessageName(HvacSOAConstant.MSG_EVENT_BLOWING_DIRECTION)
                .withInitialValue(1)
                .withCompare(false)
                .build());
        _position = position;
        _resource_id = resource_id;
    }

    @Override
    public void _bind_view(View view) {
        _mdv_adjust_view = view.findViewById(_resource_id);
        _mdv_show_view = view.findViewById(R.id.mdv_show_view);
        _mdv_adjust_view.setListener(new HvacMDVAdjustView.OutletListener() {
            @Override
            public void onIsShowView(boolean isShow) {
                _listener.isShowMDVView(isShow);
                if (isShow) {
                    _mdv_show_view.updateHorBar(_mdv_horizontal);
                    _mdv_show_view.updateVerBar(_mdv_vertical);
                    AAOP_LogUtils.d(TAG, " hor = " + _mdv_horizontal + "   ver = " + _mdv_vertical);
                }
            }

            @Override
            public void onViewSlideAdjust(int X, int Y, boolean isX, boolean isY) {
                AAOP_LogUtils.d(TAG, "left and right: X==> " + X + " ------ top and: Y==> " + Y);
                Bundle bundle = new Bundle();
                bundle.putInt("action", 0);
                bundle.putInt("horizontal", X);
                bundle.putInt("vertical", Y);
                bundle.putInt("direction", isX ? 1 : 0);
                bundle.putInt("target", _position);
                bundle.putInt("value", 666);
                HvacService.getInstance().invoke(HvacSOAConstant.MSG_SET_BLOWING_DIRECTION, bundle, new ADSBusReturnValue());
                _mdv_show_view.updateHorBar(X * THRESHOLD);
                _mdv_show_view.updateVerBar(Y * THRESHOLD);
            }
        });

    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, "bundle = " + bundle.toString());
        int horizontal = bundle.getInt("horizontal");
        int vertical = bundle.getInt("vertical");
        int target = bundle.getInt("target");
        AAOP_LogUtils.d(TAG, "position = " + _position);
        if (_position == target) {
            if (horizontal > INIT_NUMBER)
                _mdv_horizontal = horizontal * THRESHOLD;
            if (vertical > INIT_NUMBER)
                _mdv_vertical = vertical * THRESHOLD;
//            _mdv_show_view.updateHorBar(_mdv_horizontal);
//            _mdv_show_view.updateVerBar(_mdv_vertical);
            _mdv_adjust_view.updateSlideValue(horizontal, vertical);
        }
    }

    public interface MDVAdjustListener {
        void isShowMDVView(boolean is_show);
    }
}
