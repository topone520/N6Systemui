package com.adayo.systemui.contents;

public class HvacSingleton {

    private static HvacSingleton instance;

    private HvacSingleton() {

    }

    public static synchronized HvacSingleton getInstance() {
        if (instance == null) {
            instance = new HvacSingleton();
        }
        return instance;
    }

    //空调主界面是否可见  用来控制急速控温提示的逻辑
    private boolean isHvacHomeShow;

    //判断空调主界面可见，并且当前可视的是空调的layout
    private boolean isHvacLayout;

    //记录当前用户点击的卡槽，默认1
    private int driverSlot = 1;
    private int copilotSlot = 1;
    private boolean isSeatDialogShow;

    public boolean isSeatDialogShow() {
        return isSeatDialogShow;
    }

    public void setSeatDialogShow(boolean seatDialogShow) {
        isSeatDialogShow = seatDialogShow;
    }

    public int getDriverSlot() {
        return driverSlot;
    }

    public void setDriverSlot(int driverSlot) {
        this.driverSlot = driverSlot;
    }

    public int getCopilotSlot() {
        return copilotSlot;
    }

    public void setCopilotSlot(int copilotSlot) {
        this.copilotSlot = copilotSlot;
    }

    public boolean isHvacLayout() {
        return isHvacLayout;
    }

    public void setHvacLayout(boolean hvacLayout) {
        isHvacLayout = hvacLayout;
    }

    public static void setInstance(HvacSingleton instance) {
        HvacSingleton.instance = instance;
    }


    public boolean isHvacHomeShow() {
        return isHvacHomeShow;
    }

    public void setHvacHomeShow(boolean hvacHomeShow) {
        isHvacHomeShow = hvacHomeShow;
    }

}
