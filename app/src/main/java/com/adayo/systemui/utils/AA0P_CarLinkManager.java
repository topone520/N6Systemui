package com.adayo.systemui.utils;

import android.content.Context;

import com.adayo.systemui.adapters.AllAppAdapter;
import com.adayo.systemui.bean.AppInfo;
import com.android.systemui.SystemUIApplication;
import com.carlinx.arcfox.carlink.arclink.ArcLinkListener;
import com.carlinx.arcfox.carlink.arclink.ArcLinkManager;
import com.carlinx.arcfox.carlink.core.LinkDataType;
import com.carlinx.arcfox.carlink.model.AppGeneralInfo;
import com.carlinx.arcfox.carlink.model.CallInfo;
import com.carlinx.arcfox.carlink.model.MusicInfo;
import com.carlinx.arcfox.carlink.model.NavigationInfo;
import com.carlinx.arcfox.carlink.model.POIAddress;
import com.carlinx.arcfox.carlink.model.PhoneStateInfo;

import java.util.Collections;
import java.util.List;

public class AA0P_CarLinkManager {
    private volatile static AA0P_CarLinkManager AA0P_CarLinkManager;


    public static AA0P_CarLinkManager getInstance(){
        if (AA0P_CarLinkManager == null){
            synchronized (AA0P_CarLinkManager.class){
                if (AA0P_CarLinkManager == null){
                    AA0P_CarLinkManager = new AA0P_CarLinkManager();
                }
            }
        }
        return AA0P_CarLinkManager;
    }

    public void init(){
        ArcLinkManager.getInstance().init(SystemUIApplication.getSystemUIContext(), new ArcLinkManager.LinkStateListener() {
            @Override
            public void onLinkStateListener(boolean b) {
                if (b) {
                    ArcLinkManager.getInstance().registerArcLinkListener(new ArcLinkListener() {
                        @Override
                        public void startScan() {
                            LogUtil.d("startScan: ");
                        }

                        @Override
                        public void stopScan() {
                            LogUtil.d("stopScan");
                        }

                        @Override
                        public void onPinCode(String s, String s1) {
                            LogUtil.d("onPinCode: " + s + " , " + s1);
                        }

                        @Override
                        public void onConnectingProgress(String s, int i) {
                            LogUtil.d("onConnectingProgress: " + s + " , " + i);
                        }

                        @Override
                        public void onConnectStateChanged(String s, int state) {
                            LogUtil.d("onConnectStateChanged: " + s + " , " + state);
                            if (onStateListener!=null){
                                onStateListener.onStateListener(s,state);
                            }
                            if (onStateListener2!=null){
                                onStateListener2.onStateListener2(s,state);
                            }
                        }

                        @Override
                        public void getAppList(String s, List<AppGeneralInfo> list) {
                            if (onAppListListener!=null){
                                onAppListListener.onAppListListener(s,list);
                            }
                        }

                        @Override
                        public void onAppListStateChanged(String s, List<String> list, int i) {
                            LogUtil.d("onAppListStateChanged: " + s + " , " + list + " , " + i);
                        }

                        @Override
                        public void onAppStateChanged(String s, int i, boolean b, int i1, int i2, int i3) {
                            LogUtil.d("onAppStateChanged: " + s + " , " + i + " , " + b + " , " + i1 + " , " + i2 + " , " + i3);
                        }

                        @Override
                        public void onMusicInfoReceived(String s, MusicInfo musicInfo) {
                            LogUtil.d("onMusicInfoReceived: " + s + " , " + musicInfo);
                        }

                        @Override
                        public void onNavigationInfoReceived(String s, NavigationInfo navigationInfo) {
                            LogUtil.d("onNavigationInfoReceived: " + s + " , " + navigationInfo);
                        }

                        @Override
                        public void onPhoneStateInfoReceived(String s, PhoneStateInfo phoneStateInfo) {
                            LogUtil.d("onPhoneStateInfoReceived: " + s + " , " + phoneStateInfo);
                        }

                        @Override
                        public void onCallInfoReceived(String s, CallInfo callInfo) {
                            LogUtil.d("onCallInfoReceived: " + s + " , " + callInfo);
                        }

                        @Override
                        public void onPOIReceived(String s, POIAddress poiAddress) {
                            LogUtil.d("onPOIReceived: " + s + " , " + poiAddress);
                        }
                    }, Collections.singletonList(LinkDataType.LINK_DATA_APP));
                }
            }
        });
    }
    private OnStateListener onStateListener;
    public void setOnStateListener(OnStateListener onStateListener) {
        this.onStateListener = onStateListener;
    }
    public interface OnAppListListener {
        void onAppListListener(String s,List<AppGeneralInfo> list);
    }
    private OnAppListListener onAppListListener;
    public void setOnAppListListener(OnAppListListener onAppListListener) {
        this.onAppListListener = onAppListListener;
    }
    public interface OnStateListener {
        void onStateListener(String s,int state);
    }
    public interface OnStateListener2 {
        void onStateListener2(String s,int state);
    }
    private OnStateListener2 onStateListener2;
    public void setOnStateListener2(OnStateListener2 onStateListener) {
        this.onStateListener2 = onStateListener;
    }
}
