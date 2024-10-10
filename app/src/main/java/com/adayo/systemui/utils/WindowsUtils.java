package com.adayo.systemui.utils;

import static com.adayo.systemui.contents.SystemContents.SCREEN_SHUTDOWN;
import static com.adayo.systemui.contents.SystemContents.SCREEN_STANDBY;
import static com.adayo.systemui.contents.SystemContents.SCREEN_STR_SHUTDOWN;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.adayo.systemui.windows.bars.DockBar;
import com.adayo.systemui.windows.bars.HvacShortcutBar;
import com.adayo.systemui.windows.bars.ShortcutDockBar;
import com.adayo.systemui.windows.bars.StatusBar;
import com.adayo.systemui.windows.bars.HvacInitialViewBar;
import com.adayo.systemui.windows.panels.HvacPanel;
import com.adayo.systemui.windows.panels.QsViewPanel;
import com.adayo.systemui.windows.panels.ScreenSaver;
import com.adayo.systemui.windows.panels.ShowAppListDialog;

@SuppressWarnings("ALL")
public class WindowsUtils {
    private static StatusBar statusBar;
    private static DockBar dockBar;
    private static QsViewPanel qsViewPanel;
    private static HvacPanel hvacPanel;
    private static ScreenSaver screenSaver;
    //  private static HvacInitialViewBar hvacInitialViewBar;
    private static ShortcutDockBar mShortcutDockBar;
    private static HvacShortcutBar mHvacShortcutBar;


    public static void setStatusBarVisibility(int visible) {
        if (null == statusBar) {
            statusBar = StatusBar.getInstance();
        }
        statusBar.setVisibility(visible);
    }

    public static void setDockBarVisibility(int visible) {
        if (null == dockBar) {
            dockBar = DockBar.getInstance();
        }
        dockBar.setVisibility(visible);
    }

    public static boolean showQsPanel(@NonNull MotionEvent event) {

        if (null == qsViewPanel) {
            qsViewPanel = QsViewPanel.getInstance();
        }
        return qsViewPanel.show(event);
    }

    public static void dismissQsPanel() {
        if (null == qsViewPanel) {
            qsViewPanel = QsViewPanel.getInstance();
        }
        qsViewPanel.dismiss();
    }

    public static void setQsPanelVisibility(int visible) {
//        LogUtil.d("setQsPanelVisibility");
//        String currentUISource = SourceControllerImpl.getInstance().getCurrentUISource();
//        if(visible == View.VISIBLE && (AdayoSource.ADAYO_SOURCE_AVM.equals(currentUISource) || "ADAYO_SOURCE_APA".equals(currentUISource) || AdayoSource.ADAYO_SOURCE_RVC.equals(currentUISource))){
//            return;
//        }
//        if (null == qsPanel) {
//            qsPanel = QsViewPanel.getInstance();
//        }
//        qsPanel.setVisibility(visible);
    }

    public static void showHvacInitialViewBar() {
        if (null == mHvacShortcutBar) {
            mHvacShortcutBar = HvacShortcutBar.getInstance();
        }
        if (null == hvacPanel) {
            hvacPanel = HvacPanel.getInstance();
        }
    }

    public static boolean showHvacPanel(@NonNull MotionEvent event) {
        if (null == hvacPanel) {
            hvacPanel = HvacPanel.getInstance();
        }
        return hvacPanel.show(event);
    }

    public static void showShortcutDockBar() {
        if (null == mShortcutDockBar) {
            mShortcutDockBar = ShortcutDockBar.getInstance();
        }
    }

    public static void setHvacPagesBgEffect(float alpha) {
    }

    public static void setHvacMainViewState(boolean isShow) {
    }

    public static void setHvacPanelVisibility(int visible, int area, boolean isFromUser, boolean needAdmin) {


    }

    public static void isShowAllAppsPanel() {
        if (ShowAppListDialog.getInstance().isShowing()) {
            ShowAppListDialog.getInstance().dismiss();
        } else {
            ShowAppListDialog.getInstance().showPanel();
        }
    }

    public static void showAllAppsPanel() {
        ShowAppListDialog.getInstance().showPanel();
    }

    public static void dismissAllAppsPanel() {
        ShowAppListDialog.getInstance().dismiss();
    }

    public static void setSystemStatus(int systemStatus) {
        if (systemStatus == SCREEN_STANDBY || systemStatus == SCREEN_SHUTDOWN || systemStatus == SCREEN_STR_SHUTDOWN) {
            if (null == screenSaver) {
                screenSaver = ScreenSaver.getInstance();
            }
            screenSaver.setVisibility(View.VISIBLE);

        } else {
            if (null != screenSaver) {
                screenSaver.setVisibility(View.GONE);
            }
        }
    }

    public static void setScreenViewVisibility(int visible) {

    }

    public static void setScreenViewVisibility(int visible, int screenType) {

    }
}
