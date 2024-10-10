package com.android.systemui;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.aaop_hskin.attribute.ISkinAttrHandler;
import com.adayo.proxy.aaop_hskin.entity.SkinAttr;
import com.adayo.proxy.aaop_hskin.resource.IResourceManager;
import com.adayo.proxy.aaop_hskin_helper.AAOP_HSkinHelper;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.service.bluetooth.BluetoothService;
import com.adayo.systemui.contents.ConnectDeviceConstant;
import com.adayo.systemui.utils.AA0P_CarAudioManager;
import com.adayo.systemui.utils.AA0P_CarLinkManager;
import com.adayo.systemui.utils.AAOP_SceneConnectProxy;
import com.adayo.systemui.windows.views.hvac.VerSelectedView;
import com.baic.icc.account.api.AccountManager;

public class SystemUIApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        context = getApplicationContext();
        super.onCreate();
        AAOP_HSkinHelper.init(getApplicationContext());
        AA0P_CarAudioManager.getInstance().init(getApplicationContext());
        AA0P_CarLinkManager.getInstance().init();
        AccountManager.init(getApplicationContext());
        Settings.System.putString(getContentResolver(), ConnectDeviceConstant.KEY_ALL_APP_STATUS, ConnectDeviceConstant.VALUE_ALL_APP_STATUS_DISMISS);
        BluetoothService.Build(getApplicationContext());
        SystemProperties.set("vendor.all.system_server.ready", "1");


        AAOP_HSkin
                .getInstance()
                .registerSkinAttrHandler("otherColor", new TextColorAttrHandler());
        AAOP_HSkin
                .getInstance()
                .registerSkinAttrHandler("selectColor", new TextColorAttrHandler());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AAOP_HSkin.getInstance().refreshLanguage();
    }

    public static Context getSystemUIContext() {
        return context;
    }


    class TextColorAttrHandler implements ISkinAttrHandler {

        @Override
        public void apply(View view, SkinAttr skinAttr, IResourceManager iResourceManager) {
            Log.d("VerticalSmoothMoveButtonSkin", "apply");
            if (null == view
                    || null == skinAttr
            ) {
                Log.d("VerticalSmoothMoveButtonSkin", "RETUREN skinAttr.mAttrName=" + skinAttr.mAttrName);
                return;
            }
            String dayNight = iResourceManager.getSimpleSkinIdentifier();
            if ("0".equals(dayNight)) {
                AAOP_LogUtils.d("TAG","------------------------------ black");
            } else {
                AAOP_LogUtils.d("TAG","------------------------------ white");
            }

            if ("otherColor".equals(skinAttr.mAttrName)) {
                int color = iResourceManager.getColor(
                        skinAttr.mAttrValueRefId);
                if (view instanceof VerSelectedView) {
                    ((VerSelectedView) view).setOtherTextColor(color);
                }

            }

            if ("selectColor".equals(skinAttr.mAttrName)) {
                int color = iResourceManager.getColor(
                        skinAttr.mAttrValueRefId);
                if (view instanceof VerSelectedView) {
                    ((VerSelectedView) view).setSelectTextColor(color);
                }

            }

        }
    }

}
