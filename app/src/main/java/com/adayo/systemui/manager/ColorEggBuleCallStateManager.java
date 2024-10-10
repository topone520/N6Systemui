package com.adayo.systemui.manager;

public class ColorEggBuleCallStateManager {

    private static final String TAG = "BuleCallStateManager";
    private static ColorEggBuleCallStateManager buleCallStateManager;

    BuleCallStateCallBack buleCallStateCallBack;

    private int buleState;

    public static ColorEggBuleCallStateManager getInstance() {
        if (null == buleCallStateManager) {
            synchronized (ColorEggBuleCallStateManager.class) {
                if (null == buleCallStateManager) {
                    buleCallStateManager = new ColorEggBuleCallStateManager();
                }
            }
        }
        return buleCallStateManager;
    }



    public ColorEggBuleCallStateManager(){

    }

    public void setState(int state){

        buleState = state;
        if (this.buleCallStateCallBack != null){
            this.buleCallStateCallBack.onStateChange(buleState);
        }

    }

    public int getBuleState() {
        return buleState;
    }

    public void setBuleCallStateCallBack(BuleCallStateCallBack buleCallStateCallBack) {
        this.buleCallStateCallBack = buleCallStateCallBack;
    }

    public void unBuleCallStateCallBack(){

        this.buleCallStateCallBack = null;

    }

    public interface BuleCallStateCallBack{

        void onStateChange(int state);

    }


}
