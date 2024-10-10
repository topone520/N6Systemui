package com.adayo.systemui.functional.negative._binder;

import static com.adayo.systemui.contents.PublicContents.PACKAGE_NAME;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.deviceservice.IDeviceFuncCallBack;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.views.QsSeekBar;
import com.android.systemui.R;

public class NegativeBrightnessViewBinder extends AbstractViewBinder<Integer> {


    public NegativeBrightnessViewBinder() {
        super(new ViewBinderProviderInteger.Builder().build());
    }

    @Override
    protected void _bind_view(View view) {
        QsSeekBar brightness_adjust = view.findViewById(R.id.brightness_adjust);

        AAOP_DeviceServiceManager.getInstance().registerDeviceFuncListener(new IDeviceFuncCallBack.Stub() {
            @Override
            public int onChangeListener(Bundle bundle) {
                if(bundle.containsKey("IVI_first_screen")) {
                    int iviFirstScreen = bundle.getInt("IVI_first_screen");
                    LogUtil.d("iviFirstScreen: "+iviFirstScreen);
                    brightness_adjust.setProgress(iviFirstScreen);
                }
                return 0;
            }
        }, PACKAGE_NAME, "DisplayDevice");

        brightness_adjust.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    LogUtil.d("mBrightnessSeekBar onProgressChanged progress = " + progress + " fromUser = " + fromUser);
                    Bundle param = new Bundle();
                    param.putInt("screen", 0); //0表示IVI屏, 1表示仪表屏
                    param.putInt("value", progress);// 0~10
                    AAOP_DeviceServiceManager.getInstance().setDeviceFuncUniversalInterface("DisplayDevice", "setScreenLightControl", param);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void _update_ui(Integer value) {

    }

    @Override
    protected void _update_ui(Bundle bundle) {

    }
}
