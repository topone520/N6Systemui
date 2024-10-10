package com.adayo.systemui.functional.hvac._binder;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.contract.IViewBinder;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.proxy.vehicle.Dimensional;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.utils.TimeUtils;

public class FaceViewBinder extends AbstractViewBinder<Integer> implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = FaceViewBinder.class.getSimpleName();
    private CheckBox _cb_face;
    private final int _view_id;
    private final int _position;

    public FaceViewBinder(ViewBinderProviderInteger provider, int position, int view_id) {
        super(TAG, provider);
        _view_id = view_id;
        _position = position;
    }

    @Override
    public void _bind_view(View view) {
        _cb_face = view.findViewById(_view_id);
        _init_click();
    }

    private void _init_click() {
        _cb_face.setOnCheckedChangeListener(this);
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {
        AAOP_LogUtils.d(TAG, "_update_ui: bundle = " + bundle.toString());
        //控制位置
        int area = bundle.getInt("target");
        //模式
        int blowing_mode = bundle.getInt("value");
        _cb_face.setChecked(area == _position && (blowing_mode == AreaConstant.BLOW_FACE || blowing_mode == AreaConstant.BLOW_FACE_FEAT));

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!buttonView.isPressed()) return;//代码设置不可用
        _set_value(0);
        if (_position==AreaConstant.BAIC_LEFT_FRONT){
            Dimensional.getInstance().doAction("DataSource.Model.MainDriverWindMode","3");
        }else {
            Dimensional.getInstance().doAction("DataSource.Model.CopilotWindMode","3");
        }
        if (isChecked) {
            TimeUtils.startTimer();
            ReportBcmManager.getInstance().sendHvacReport("8630004", "windface_turnonway", "中控屏打开空调吹面");
        } else {
            ReportBcmManager.getInstance().sendHvacReport("8630005", "windface_duration", TimeUtils.getTimeInSeconds());
        }

    }

    public static class Builder {

        public static IViewBinder createLeft(int resource_id) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", AreaConstant.BAIC_LEFT_FRONT);
            bundle.putInt("value", AreaConstant.BLOW_FACE);
            return createInstance(AreaConstant.BAIC_LEFT_FRONT, bundle, resource_id);
        }

        public static IViewBinder createRight(int resource_id) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", AreaConstant.BAIC_RIGHT_FRONT);
            bundle.putInt("value", AreaConstant.BLOW_FACE);
            return createInstance(AreaConstant.BAIC_RIGHT_FRONT, bundle, resource_id);
        }

        private static IViewBinder createInstance(final int position, final Bundle bundle, int resource_id) {
            ViewBinderProviderInteger provider = (ViewBinderProviderInteger) new ViewBinderProviderInteger.Builder()
                    .withBundle(bundle)
                    .withService(HvacService.getInstance())
                    .withGetMessageName(HvacSOAConstant.MSG_GET_BLOWING_MODE)
                    .withSetMessageName(HvacSOAConstant.MSG_BTN_SET_BLOWING_MODE)
                    .withEventMessageName(HvacSOAConstant.MSG_EVENT_BLOWING_MODE)
                    .withInitialValue(0)
                    .withCompare(false)
                    .build();
            return new FaceViewBinder(provider, position, resource_id);
        }
    }
}
