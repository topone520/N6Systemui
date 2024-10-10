package com.adayo.systemui.bean;

public class ViewStateInfo {
    private int statusBarVisibility;
    private int navigationBarVisibility;
    private int dockBarVisibility;
    private int qsPanelVisibility = -1;
    private int hvacPanelVisibility = -1;
    private int screenVisibility;
    private int allAppsPanelVisibility = -1;

    public int getAllAppsPanelVisibility() {
        return allAppsPanelVisibility;
    }

    public void setAllAppsPanelVisibility(int allAppsPanelVisibility) {
        this.allAppsPanelVisibility = allAppsPanelVisibility;
    }

    public int getScreenVisibility() {
        return screenVisibility;
    }

    public void setScreenVisibility(int screenVisibility) {
        this.screenVisibility = screenVisibility;
    }

    public int getVolumeDialogVisibility() {
        return volumeDialogVisibility;
    }

    public void setVolumeDialogVisibility(int volumeDialogVisibility) {
        this.volumeDialogVisibility = volumeDialogVisibility;
    }

    private int volumeDialogVisibility;

    public int getStatusBarVisibility() {
        return statusBarVisibility;
    }

    public void setStatusBarVisibility(int statusBarVisibility) {
        this.statusBarVisibility = statusBarVisibility;
    }

    public int getNavigationBarVisibility() {
        return navigationBarVisibility;
    }

    public void setNavigationBarVisibility(int navigationBarVisibility) {
        this.navigationBarVisibility = navigationBarVisibility;
    }

    public int getDockBarVisibility() {
        return dockBarVisibility;
    }

    public void setDockBarVisibility(int dockBarVisibility) {
        this.dockBarVisibility = dockBarVisibility;
    }

    public int getQsPanelVisibility() {
        return qsPanelVisibility;
    }

    public void setQsPanelVisibility(int qsPanelVisibility) {
        this.qsPanelVisibility = qsPanelVisibility;
    }

    public int getHvacPanelVisibility() {
        return hvacPanelVisibility;
    }

    public void setHvacPanelVisibility(int hvacPanelVisibility) {
        this.hvacPanelVisibility = hvacPanelVisibility;
    }
}
