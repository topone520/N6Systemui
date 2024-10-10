package com.adayo.systemui.utils.events;


import android.content.Context;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.android.systemui.SystemUIApplication;
import com.baic.icc.sdk.scene.data.SimpleScene;
import com.baic.icc.sdk.scene.proxy.SceneConnectProxy;

import java.util.List;

public class AAOP_SceneConnectProxy {

    private static final String TAG = AAOP_SceneConnectProxy.class.getSimpleName();
    private volatile static AAOP_SceneConnectProxy mAAOP_SceneConnectProxy;

    public static AAOP_SceneConnectProxy getInstance(){
        if (mAAOP_SceneConnectProxy == null){
            synchronized (AAOP_SceneConnectProxy.class){
                if (mAAOP_SceneConnectProxy == null){
                    mAAOP_SceneConnectProxy = new AAOP_SceneConnectProxy();
                }
            }
        }
        return mAAOP_SceneConnectProxy;
    }

    public void init(){
        boolean bindService = SceneConnectProxy.getInstance(SystemUIApplication.getSystemUIContext()).bindService();
        AAOP_LogUtils.d(TAG,"bindService: "+bindService);
        SceneConnectProxy.getInstance(SystemUIApplication.getSystemUIContext()).registerSceneChangedListener(new SceneConnectProxy.ISceneChangedListener() {
            @Override
            public void sceneOpen(SimpleScene simpleScene) {
                AAOP_LogUtils.d(TAG,"sceneOpen simpleScene: "+simpleScene);
            }

            @Override
            public void sceneClose(SimpleScene simpleScene) {
                AAOP_LogUtils.d(TAG,"sceneClose simpleScene: "+simpleScene);
            }

            @Override
            public void sceneListChanged(List<SimpleScene> list) {
                AAOP_LogUtils.d(TAG,"sceneListChanged list: "+list);
            }
        });

    }


}
