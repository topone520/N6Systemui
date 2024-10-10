package com.adayo.systemui.windows.bars;

import static android.view.WindowManager.LayoutParams.TYPE_NAVIGATION_BAR;
import static com.adayo.systemui.contents.PublicContents.DOCK_BAR;
import static com.adayo.systemui.contents.SystemUIConfigs.HAS_DOCK_BAR;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.systemui.bean.SystemUISourceInfo;
import com.adayo.systemui.bean.ViewStateInfo;
import com.adayo.systemui.functional.sildeback.DefaultSlideView;
import com.adayo.systemui.functional.sildeback.SlideControlLayout;
import com.adayo.systemui.interfaces.BaseCallback;
import com.adayo.systemui.manager.SourceControllerImpl;
import com.adayo.systemui.manager.WindowsControllerImpl;
import com.adayo.systemui.utils.CarTypeUtils;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.utils.WindowsUtils;
import com.adayo.systemui.utils.AAOP_SceneConnectProxy;
import com.adayo.systemui.windows.panels.HvacPanel;
import com.adayo.systemui.windows.panels.QsViewPanel;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import java.util.HashMap;

public class DockBar implements View.OnClickListener, BaseCallback<SystemUISourceInfo>, View.OnLongClickListener {

    private final String TAG = getClass().getName();

    private volatile static DockBar sideBar;
    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager mWindowManager;
    private LinearLayout mFloatLayout;

    private ImageView home;
    private ImageView media;
    private ImageView apps;
    private ImageView carSetting;

    private boolean isAdded = false;
    private SlideControlLayout slideControlLayout;

    private DockBar() {
        initView();
        initData();
    }

    public static DockBar getInstance() {
        if (sideBar == null) {
            synchronized (DockBar.class) {
                if (sideBar == null) {
                    sideBar = new DockBar();
                }
            }
        }
        return sideBar;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        mFloatLayout = (LinearLayout) View.inflate(SystemUIApplication.getSystemUIContext(), R.layout.dock_bar, null);
        mLayoutParams = new WindowManager.LayoutParams(140, 720, TYPE_NAVIGATION_BAR, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING | WindowManager.LayoutParams.FLAG_SPLIT_TOUCH | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH | WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, PixelFormat.TRANSLUCENT);
        mLayoutParams.token = new Binder();
        mLayoutParams.gravity = Gravity.LEFT;
        mLayoutParams.y = 0;
        mLayoutParams.x = 0;
        mLayoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;
        mLayoutParams.setTitle(DOCK_BAR);
        mLayoutParams.windowAnimations = R.style.LeftInOutAnimation;

        home = mFloatLayout.findViewById(R.id.home);
        media = mFloatLayout.findViewById(R.id.media);
        apps = mFloatLayout.findViewById(R.id.apps);
        carSetting = mFloatLayout.findViewById(R.id.car_setting);

        if (CarTypeUtils.getCarType() == CarTypeUtils.CarType.N60)
            carSetting.setImageResource(R.drawable.selector_icon_change);
        else carSetting.setImageResource(R.drawable.selector_icon_car);

        mFloatLayout.setOnTouchListener((v, event) -> slideControlLayout.onDockTouchEvent(event));
        home.setOnClickListener(this);
        media.setOnClickListener(this);
        apps.setOnClickListener(this);
        carSetting.setOnClickListener(this);
        home.setOnLongClickListener(this);
        mFloatLayout.setLayoutParams(mLayoutParams);

        addBackView();
    }

    private void addBackView() {
        slideControlLayout = new SlideControlLayout(SystemUIApplication.getSystemUIContext(), 28, new DefaultSlideView(SystemUIApplication.getSystemUIContext()), null).attachToActivity(Gravity.LEFT);
        new SlideControlLayout(SystemUIApplication.getSystemUIContext(), 28, new DefaultSlideView(SystemUIApplication.getSystemUIContext()), null).attachToActivity(Gravity.RIGHT);

    }

    private void initData() {
        SourceControllerImpl.getInstance().addCallback(this);
        WindowsControllerImpl.getInstance().addCallback((BaseCallback<ViewStateInfo>) data -> updateUI(SourceControllerImpl.getInstance().getCurrentUISource(), View.VISIBLE == data.getAllAppsPanelVisibility()));
    }

    private boolean currentAppsPanelIsShowing;

    private void updateUI(String currentSource, boolean isAllAppsShowing) {
        LogUtil.debugD("CurrentSource = " + currentSource + " ; isAllAppsShowing = " + isAllAppsShowing);
        if (home.isSelected() != AdayoSource.ADAYO_SOURCE_HOME.equals(currentSource) || currentAppsPanelIsShowing != isAllAppsShowing) {
            home.setSelected(AdayoSource.ADAYO_SOURCE_HOME.equals(currentSource) && !isAllAppsShowing);
        }
        if (media.isSelected() != AdayoSource.ADAYO_SOURCE_MULTIMEDIA.equals(currentSource) || currentAppsPanelIsShowing != isAllAppsShowing) {
            media.setSelected(AdayoSource.ADAYO_SOURCE_MULTIMEDIA.equals(currentSource) && !isAllAppsShowing);
        }
        if (carSetting.isSelected() != AdayoSource.ADAYO_SOURCE_BCM.equals(currentSource) || currentAppsPanelIsShowing != isAllAppsShowing) {
            carSetting.setSelected(AdayoSource.ADAYO_SOURCE_BCM.equals(currentSource) && !isAllAppsShowing);
        }
        if (currentAppsPanelIsShowing != isAllAppsShowing) {
            currentAppsPanelIsShowing = isAllAppsShowing;
            apps.setSelected(isAllAppsShowing);
        }
    }

    public void setVisibility(int visible) {
        if (!HAS_DOCK_BAR || null == mFloatLayout) {
            return;
        }
        LogUtil.debugD(visible + "");
        if (!isAdded && null != mWindowManager) {
            mWindowManager.addView(mFloatLayout, mLayoutParams);
            isAdded = true;
        }
        mFloatLayout.setVisibility(visible);
//        windowsController.notifyVisibility(SystemUIContent.TYPE_OF_STATUS_BAR, visible);
    }

    @Override
    public void onClick(View v) {
        HvacPanel.getInstance().hvacScrollLayoutDismiss();
        QsViewPanel.getInstance().closeView();
        switch (v.getId()) {
            case R.id.car_setting:
                LogUtil.d("onClick car_setting ");
                if (CarTypeUtils.getCarType() == CarTypeUtils.CarType.N60) {
                    SourceControllerImpl.getInstance().requestSource(AppConfigType.SourceSwitch.APP_ON.getValue(), AdayoSource.ADAYO_SOURCE_CHARGECENTER, new HashMap<>());
                } else {
                    SourceControllerImpl.getInstance().requestSource(AppConfigType.SourceSwitch.APP_ON.getValue(), AdayoSource.ADAYO_SOURCE_BCM, new HashMap<>());
                }

                WindowsUtils.dismissAllAppsPanel();
                break;
            case R.id.home:
                LogUtil.d("onClick home ");
                SourceControllerImpl.getInstance().requestSource(AppConfigType.SourceSwitch.APP_ON.getValue(), AdayoSource.ADAYO_SOURCE_HOME, new HashMap<>());
                WindowsUtils.dismissAllAppsPanel();
                break;
            case R.id.media:
                LogUtil.d("onClick media ");
//                try {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setClassName("com.adayo.audio.demo","com.adayo.audiodemo.MainActivity");
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    SystemUIApplication.getSystemUIContext().startActivity(intent);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
                SourceControllerImpl.getInstance().requestSoureApp(AdayoSource.ADAYO_SOURCE_MULTIMEDIA, "com.arcfox.media", AppConfigType.SourceSwitch.APP_ON.getValue(), new HashMap<>());
//                SourceControllerImpl.getInstance().requestSource(AppConfigType.SourceSwitch.APP_ON.getValue(), AdayoSource.ADAYO_SOURCE_MULTIMEDIA,new HashMap<>());
                WindowsUtils.dismissAllAppsPanel();
                break;
            case R.id.apps:
                LogUtil.d("onClick apps ~~~~~~~~~~");
                WindowsUtils.showAllAppsPanel();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDataChange(SystemUISourceInfo data) {
        updateUI(data.getUiSource(), View.VISIBLE == WindowsControllerImpl.getInstance().getViewState().getAllAppsPanelVisibility());
    }

    @Override
    public boolean onLongClick(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClassName("com.adayo.app.factorymode", "com.adayo.app.factorymode.ui.activity.SettingsFactoryActivity");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        SystemUIApplication.getSystemUIContext().startActivity(intent);
        return false;
    }
}
