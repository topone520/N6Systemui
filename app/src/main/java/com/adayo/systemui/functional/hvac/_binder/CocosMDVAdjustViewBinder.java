package com.adayo.systemui.functional.hvac._binder;

import android.os.Bundle;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.proxy.vehicle.Dimensional;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.windows.views.hvac.HvacMDVAdjustView;
import com.adayo.systemui.windows.views.hvac.HvacMDVShowView;
import com.android.systemui.R;

public class CocosMDVAdjustViewBinder extends AbstractViewBinder<Integer> implements Dimensional.IHvacMdvAdjustListener {
    private static final String TAG = CocosMDVAdjustViewBinder.class.getSimpleName();

    public CocosMDVAdjustViewBinder() {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(HvacService.getInstance())
                .withGetMessageName(HvacSOAConstant.MSG_GET_BLOWING_DIRECTION)
                .withSetMessageName(HvacSOAConstant.MSG_SET_BLOWING_DIRECTION)
                .withEventMessageName(HvacSOAConstant.MSG_EVENT_BLOWING_DIRECTION)
                .withInitialValue(1)
                .withCompare(false)
                .build());
    }

    @Override
    public void _bind_view(View view) {
        Dimensional.getInstance().subscribe(this);
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
        /*if (_position == target) {
            if (horizontal > INIT_NUMBER)
                _mdv_horizontal = horizontal * THRESHOLD;
            if (vertical > INIT_NUMBER)
                _mdv_vertical = vertical * THRESHOLD;
//            _mdv_show_view.updateHorBar(_mdv_horizontal);
//            _mdv_show_view.updateVerBar(_mdv_vertical);
            _mdv_adjust_view.updateSlideValue(horizontal, vertical);
        }*/
        String doAction = convertSts(target) + "," + horizontal + "," + vertical;
        Dimensional.getInstance().doAction("DataSource.Model.HvacFanAngle", doAction);
    }

    private int old_hor, old_ver;

    @Override
    public void onEvent(int position, float hor, float ver) {
        int horizontal = convertOutlet((int) hor);
        int vertical = convertOutlet((int) ver);
        Bundle bundle = new Bundle();
        bundle.putInt("action", 0);
        bundle.putInt("horizontal", horizontal);
        bundle.putInt("vertical", vertical);
        if (old_hor != horizontal) {
            old_hor = horizontal;
            bundle.putInt("direction", 1);
        }
        if (old_ver != vertical) {
            old_ver = vertical;
            bundle.putInt("direction", 0);
        }

        bundle.putInt("target", convertSend(position));
        bundle.putInt("value", 0);
        HvacService.getInstance().invoke(HvacSOAConstant.MSG_SET_BLOWING_DIRECTION, bundle, new ADSBusReturnValue());
    }

    public int convertOutlet(int value) {
        if (value == 10) {
            return 1;
        } else if (value == 8) {
            return 2;
        } else if (value == 6) {
            return 3;
        } else if (value == 4) {
            return 4;
        } else if (value == 2) {
            return 5;
        } else if (value == 0) {
            return 6;
        } else if (value == -2) {
            return 7;
        } else if (value == -4) {
            return 8;
        } else if (value == -6) {
            return 9;
        } else if (value == -8) {
            return 10;
        } else if (value == -10) {
            return 11;
        }
        return 0;//无效值
    }

    public int convertSend(int position) {
        if (position == 0) {
            return AreaConstant.BAIC_LEFT_LEFT_FRONT_2;
        } else if (position == 1) {
            return AreaConstant.BAIC_LEFT_MID_FRONT_2;
        } else if (position == 2) {
            return AreaConstant.BAIC_RIGHT_MID_FRONT_2;
        }
        return AreaConstant.BAIC_RIGHT_RIGHT_FRONT_2;
    }

    public int convertSts(int position) {
        if (position == AreaConstant.BAIC_LEFT_LEFT_FRONT_2) {
            return 0;
        } else if (position == AreaConstant.BAIC_LEFT_MID_FRONT_2) {
            return 1;
        } else if (position == AreaConstant.BAIC_RIGHT_MID_FRONT_2) {
            return 2;
        }
        return 3;
    }
}
