package com.adayo.systemui.utils;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.android.systemui.SystemUIApplication;
import com.baic.icc.sdk.scene.data.SimpleScene;
import com.baic.icc.sdk.scene.proxy.SceneConnectProxy;

import java.util.List;

public class AAOP_SceneConnectProxy {

    private static final String TAG = AAOP_SceneConnectProxy.class.getSimpleName();
    private volatile static AAOP_SceneConnectProxy mAAOP_SceneConnectProxy;
    private boolean bindService = false;
    private int count = 0;

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

    public boolean isBindService(){
        return bindService;
    }

    public void init(){
        bindService = SceneConnectProxy.getInstance(SystemUIApplication.getSystemUIContext()).bindService();
        AAOP_LogUtils.d(TAG,"bindService: "+ bindService);
        if (bindService && count <= 5){
            count++;
            bindService = false;
        }

        SceneConnectProxy.getInstance(SystemUIApplication.getSystemUIContext()).registerSceneChangedListener(new SceneConnectProxy.ISceneChangedListener() {
            @Override
            public void sceneOpen(SimpleScene simpleScene) {
                AAOP_LogUtils.d(TAG,"sceneOpen simpleScene: "+simpleScene);
                onSceneOpenListener.onSceneOpen(simpleScene.getSceneName(), simpleScene.getSceneStatus());
            }

            @Override
            public void sceneClose(SimpleScene simpleScene) {
                AAOP_LogUtils.d(TAG,"sceneClose simpleScene: "+simpleScene);
                onSceneOpenListener.onSceneOpen(simpleScene.getSceneName(), simpleScene.getSceneStatus());
            }

            @Override
            public void sceneListChanged(List<SimpleScene> list) {
                AAOP_LogUtils.d(TAG,"sceneListChanged list: "+list);
            }
        });

    }

    private onSceneOpenListener onSceneOpenListener;
    public void setOnSceneOpenListener(onSceneOpenListener onSceneOpenListener){
        this.onSceneOpenListener = onSceneOpenListener;
    }
    public interface onSceneOpenListener{
        void onSceneOpen(String sceneName,int sceneStatus);
    }




}
