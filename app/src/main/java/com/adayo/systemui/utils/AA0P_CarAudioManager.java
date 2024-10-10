package com.adayo.systemui.utils;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.media.CarAudioManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;


public class AA0P_CarAudioManager {

    private volatile static AA0P_CarAudioManager AA0P_CarAudioManager;
    private Car mCar;
    private CarAudioManager mCarAudioManager;

    public static AA0P_CarAudioManager getInstance() {
        if (AA0P_CarAudioManager == null) {
            synchronized (AA0P_CarAudioManager.class) {
                if (AA0P_CarAudioManager == null) {
                    AA0P_CarAudioManager = new AA0P_CarAudioManager();
                }
            }
        }
        return AA0P_CarAudioManager;
    }

    public void init(Context context){
        //初始化时对 mCar 进行初始化，并注册 mServiceConnection 服务连接监听器
        mCar = Car.createCar(context, mServiceConnection);
        //开始连接
        mCar.connect();
    }

    public void uninit(){
        //断开连接
        mCar.disconnect();
    }

    public CarAudioManager getCarAudioManager(){
        return mCarAudioManager;
    }


    /**
     * 服务连接监听器，服务连接或断开时会调用该监听器方法通知应用
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                mCarAudioManager = (CarAudioManager) mCar.getCarManager(Car.AUDIO_SERVICE);
                LogUtil.d( "Car is connected!!!");
            } catch (CarNotConnectedException e) {
                LogUtil.e("Car is not connected!", e);
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d("Car is disconnected!!!");
            mCarAudioManager = null;
        }
    };
}
