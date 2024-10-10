package com.adayo.systemui.functional.hvac._binder;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.utils.ViewStyleUtils;
import com.android.systemui.R;

public class BackRowBlowingModeViewBinder extends AbstractViewBinder<Integer> implements View.OnClickListener {

    private static final String TAG = BackRowBlowingModeViewBinder.class.getSimpleName();
    private LinearLayout _linear_air, _linear_face, _linear_feet;
    private TextView _tv_back_row, _tv_face, _tv_feet;
    private boolean _is_switch, _is_open_face, _is_open_feet;

    public BackRowBlowingModeViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(HvacService.getInstance())
                .withGetMessageName(HvacSOAConstant.MSG_GET_BLOWING_MODE)
                .withSetMessageName(HvacSOAConstant.MSG_SET_BLOWING_MODE)
                .withEventMessageName(HvacSOAConstant.MSG_EVENT_BLOWING_MODE)
                .withInitialValue(0)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        _linear_air = view.findViewById(R.id.linear_back_row_air);
        _linear_face = view.findViewById(R.id.linear_face);
        _linear_feet = view.findViewById(R.id.linear_feet);
        _tv_back_row = view.findViewById(R.id.tv_back_row);
        _tv_face = view.findViewById(R.id.tv_face);
        _tv_feet = view.findViewById(R.id.tv_feet);
        _init_click();
    }

    private void _init_click() {
        _linear_air.setOnClickListener(this);
        _linear_face.setOnClickListener(this);
        _linear_feet.setOnClickListener(this);
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, " bundle = " + bundle.toString());
        //控制位置
        int area = bundle.getInt("target");
        //模式
        int blowing_mode = bundle.getInt("value");
        if (area == AreaConstant.BAIC_REAR) {
            if (blowing_mode == AreaConstant.BLOW_FACE) {
                //后排吹面
                _is_open_face = true;
                _is_switch = true;
                _is_open_feet = false;

            } else if (blowing_mode == AreaConstant.BLOW_FEET) {
                //后排吹脚
                _is_open_face = false;
                _is_switch = true;
                _is_open_feet = true;
            } else if (blowing_mode == AreaConstant.BLOW_FACE_FEAT) {
                //后排吹面吹脚
                _is_open_face = true;
                _is_switch = true;
                _is_open_feet = true;
            } else if (blowing_mode == AreaConstant.BLOW_BACK_END) {
                //截止 后排空调关闭 blowingMode = 0
                _is_open_face = false;
                _is_switch = false;
                _is_open_feet = false;
            }
        }
        _is_select_view();
    }

    private void _is_select_view() {
        _linear_air.setSelected(_is_switch);
        _linear_face.setSelected(_is_open_face);
        _linear_feet.setSelected(_is_open_feet);
        _tv_back_row.setSelected(_is_switch);
        _tv_face.setSelected(_is_open_face);
        _tv_feet.setSelected(_is_open_feet);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int blowing_mode;
        switch (v.getId()) {
            case R.id.linear_back_row_air:
                _is_switch = !_is_switch;
                blowing_mode = _is_switch ? AreaConstant.BLOW_FACE : AreaConstant.BLOW_BACK_END;
                _tv_back_row.setSelected(_is_switch);
                _linear_air.setSelected(_is_switch);
                break;
            case R.id.linear_face:
                if (!_is_switch) return;
                _is_open_face = !_is_open_face;
                _linear_face.setSelected(_is_open_face);
                _tv_face.setSelected(_is_open_face);
                if (_is_open_face) {
                    if (_is_open_feet) {
                        blowing_mode = AreaConstant.BLOW_FACE_FEAT;
                    } else {
                        blowing_mode = AreaConstant.BLOW_FACE;
                    }
                } else {
                    if (_is_open_feet) {
                        blowing_mode = AreaConstant.BLOW_FEET;
                    } else {
                        _is_open_face = true;
                        _linear_face.setSelected(true);
                        _tv_face.setSelected(_is_open_face);
                        return;
                    }
                }
                break;
            case R.id.linear_feet:
                if (!_is_switch) return;
                _is_open_feet = !_is_open_feet;
                _linear_feet.setSelected(_is_open_feet);
                _tv_feet.setSelected(_is_open_feet);
                if (_is_open_feet) {
                    if (_is_open_face) {
                        blowing_mode = AreaConstant.BLOW_FACE_FEAT;
                    } else {
                        blowing_mode = AreaConstant.BLOW_FEET;
                    }
                } else {
                    if (_is_open_face) {
                        blowing_mode = AreaConstant.BLOW_FACE;
                    } else {
                        _is_open_feet = true;
                        _linear_feet.setSelected(true);
                        _tv_feet.setSelected(_is_open_feet);
                        return;
                    }
                }
                break;
            default:
                return;
        }
        _distribute(blowing_mode);
    }

    private void _distribute(int blowing_mode) {
        Bundle bundle = new Bundle();
        bundle.putInt("target", AreaConstant.BAIC_REAR);
        bundle.putInt("value", blowing_mode);
        _update_ui(bundle);
        boolean result = HvacService.getInstance().invoke(HvacSOAConstant.MSG_SET_BLOWING_MODE, bundle, new ADSBusReturnValue());
        AAOP_LogUtils.d(TAG, "_distribute:  signal_value = " + blowing_mode + "  bcm_return = " + result);
    }
}
