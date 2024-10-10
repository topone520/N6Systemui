package com.adayo.systemui.utils;

import android.os.RemoteException;

import com.adayo.proxy.system.aaop_systemservice.AAOP_SystemServiceManager;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;

public class CarTypeUtils {
    private static final String TAG = CarTypeUtils.class.getSimpleName();

    public enum CarType {
        N60,
        N61,
        UNKNOW
    }

    public static CarType getCarType() {
        int hs7013ACarType = -1;
        try {
            // dock栏配置字区分显示充电中心还是车控
            hs7013ACarType = AAOP_SystemServiceManager.getInstance().getOffLineConfigInfo("HS7013A_CarType");
            AAOP_LogUtils.d(TAG, "hs7013ACarType: " + hs7013ACarType);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        switch (hs7013ACarType) {
            case 0:
            case 2:
                return CarType.N60;
            case 1:
            case 3:
                return CarType.N61;
            default:
                return CarType.UNKNOW;
        }
    }
}
