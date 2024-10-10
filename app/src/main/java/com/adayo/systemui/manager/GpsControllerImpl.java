package com.adayo.systemui.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;

import com.adayo.systemui.bases.BaseControllerImpl;
import com.adayo.systemui.bean.GpsInfo;
import com.adayo.systemui.utils.LogUtil;
import com.android.systemui.SystemUIApplication;

public class GpsControllerImpl extends BaseControllerImpl<GpsInfo> implements GpsController {
    private static final String TAG = "GpsControllerImpl--";
    private volatile static GpsControllerImpl mGpsControllerImpl;

    private GpsInfo gpsInfo = new GpsInfo();

    private LocationManager locationManager;

    private GpsControllerImpl() {
        locationManager = (LocationManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        gpsInfo.setGpsShow(isGpsEnabled);
        LogUtil.d(TAG + "init gps = " + gpsInfo.toString());
        mHandler.removeMessages(REGISTER_CALLBACK);
        mHandler.sendEmptyMessage(REGISTER_CALLBACK);
    }

    public static GpsControllerImpl getInstance() {
        if (mGpsControllerImpl == null) {
            synchronized (GpsControllerImpl.class) {
                if (mGpsControllerImpl == null) {
                    mGpsControllerImpl = new GpsControllerImpl();
                }
            }
        }
        return mGpsControllerImpl;
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            LocationListener.super.onProviderEnabled(provider);
            LogUtil.d(TAG + "onProviderEnabled");
            gpsInfo.setGpsShow(true);
            LogUtil.d(TAG + "update gps = " + gpsInfo.toString());
            mHandler.removeMessages(NOTIFY_CALLBACKS);
            mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            LocationListener.super.onProviderDisabled(provider);
            LogUtil.d(TAG + "onProviderDisabled");
            gpsInfo.setGpsShow(false);
            LogUtil.d(TAG + "update gps = " + gpsInfo.toString());
            mHandler.removeMessages(NOTIFY_CALLBACKS);
            mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
        }
    };

    @SuppressLint("MissingPermission")
    GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {

        @Override
        public void onGpsStatusChanged(int event) {
            // 获取 GPS 状态
           GpsStatus gpsStatus = locationManager.getGpsStatus(null);

            // 处理 GPS 状态变化
            switch (event) {
                case GpsStatus.GPS_EVENT_STARTED:
                    // GPS 已启用
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    // GPS 已关闭
                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    // 首次定位成功
                    break;
                default:
                    break;
            }
//            mHandler.removeMessages(NOTIFY_CALLBACKS);
//            mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
        }
    };



    @SuppressLint("MissingPermission")
    @Override
    protected boolean registerListener() {
        LogUtil.d(TAG + "registerListener");
        locationManager.addGpsStatusListener(gpsStatusListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        return true;
    }

    @Override
    protected GpsInfo getDataInfo() {
        return gpsInfo;
    }

    @Override
    public boolean getGpsShow() {
        return gpsInfo.getGpsShow();
    }
}
