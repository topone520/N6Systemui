package com.adayo.systemui.manager;

import static com.adayo.systemui.contents.PublicContents.TYPE_OF_ALL_APPS_PANEL;
import static com.adayo.systemui.contents.PublicContents.TYPE_OF_DOCK_BAR;
import static com.adayo.systemui.contents.PublicContents.TYPE_OF_HVAC_PANEL;
import static com.adayo.systemui.contents.PublicContents.TYPE_OF_NAVIGATION_BAR;
import static com.adayo.systemui.contents.PublicContents.TYPE_OF_QS_PANEL;
import static com.adayo.systemui.contents.PublicContents.TYPE_OF_SCREENT_OFF;
import static com.adayo.systemui.contents.PublicContents.TYPE_OF_STATUS_BAR;
import static com.adayo.systemui.contents.PublicContents.TYPE_OF_VOLUME_DIALOG;

import android.view.View;

import com.adayo.systemui.bases.BaseControllerImpl;
import com.adayo.systemui.bean.ViewStateInfo;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.utils.WindowsUtils;

public class WindowsControllerImpl extends BaseControllerImpl<ViewStateInfo> implements WindowsController{
    private volatile static WindowsControllerImpl windowsController;
    private ViewStateInfo viewStateInfo = new ViewStateInfo();

    private WindowsControllerImpl() {
    }

    public static WindowsControllerImpl getInstance() {
        if (windowsController == null) {
            synchronized (WindowsControllerImpl.class) {
                if (windowsController == null) {
                    windowsController = new WindowsControllerImpl();
                }
            }
        }
        return windowsController;
    }

    @Override
    protected boolean registerListener() {
        return true;
    }

    @Override
    protected ViewStateInfo getDataInfo() {
        return viewStateInfo;
    }

    public ViewStateInfo getViewState() {
        return viewStateInfo;
    }

    @Override
    public void notifyVisibility(int mode, int visible) {
        switch (mode) {
            case TYPE_OF_DOCK_BAR:
                viewStateInfo.setDockBarVisibility(visible);
                break;
            case TYPE_OF_STATUS_BAR:
                viewStateInfo.setStatusBarVisibility(visible);
                break;
            case TYPE_OF_NAVIGATION_BAR:
                viewStateInfo.setNavigationBarVisibility(visible);
                break;
            case TYPE_OF_QS_PANEL:
                viewStateInfo.setQsPanelVisibility(visible);
                break;
            case TYPE_OF_HVAC_PANEL:
                viewStateInfo.setHvacPanelVisibility(visible);
                if (View.VISIBLE == visible) {
                    WindowsUtils.setQsPanelVisibility(View.GONE);
                }
                break;
            case TYPE_OF_VOLUME_DIALOG:
                viewStateInfo.setVolumeDialogVisibility(visible);
                break;
            case TYPE_OF_SCREENT_OFF:
                viewStateInfo.setScreenVisibility(visible);
            case TYPE_OF_ALL_APPS_PANEL:
                viewStateInfo.setAllAppsPanelVisibility(visible);
                break;
            default:
                break;
        }
        LogUtil.debugD("viewStateInfo.getQsPanelVisibility() = " + viewStateInfo.getQsPanelVisibility());
        LogUtil.debugD("viewStateInfo.getHvacPanelVisibility() = " + viewStateInfo.getHvacPanelVisibility());
        mHandler.removeMessages(NOTIFY_CALLBACKS);
        mHandler.sendEmptyMessage(NOTIFY_CALLBACKS);
    }
}
