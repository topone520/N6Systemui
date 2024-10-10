package com.adayo.systemui.contents;

public class FragranceSingleton {

    private static FragranceSingleton instance;

    private FragranceSingleton() {

    }

    public static synchronized FragranceSingleton getInstance() {
        if (instance == null) {
            instance = new FragranceSingleton();
        }
        return instance;
    }

    //记录香氛当前槽位 1:A; 2:B; 3:C
    private int fragranceSlot;
    //记录香氛当前开关状态 false:OFF; true:ON
    private boolean isFragranceOpen;

    public static void setInstance(FragranceSingleton instance) {
        FragranceSingleton.instance = instance;
    }

    public int getFragranceSlot() {
        return fragranceSlot;
    }

    public void setFragranceSlot(int fragranceSlot) {
        this.fragranceSlot = fragranceSlot;
    }

    public boolean isFragranceOpen() {
        return isFragranceOpen;
    }

    public void setFragranceSwitch(boolean isFragranceOpen) {
        this.isFragranceOpen = isFragranceOpen;
    }
}
