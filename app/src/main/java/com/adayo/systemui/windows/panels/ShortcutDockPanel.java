package com.adayo.systemui.windows.panels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.adapters.ShortcutAppAdapter;
import com.adayo.systemui.bean.AppInfo;
import com.adayo.systemui.eventbus.EventContents;
import com.adayo.systemui.eventbus.EventData;
import com.adayo.systemui.manager.AllAppsManager;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.manager.SourceControllerImpl;
import com.adayo.systemui.utils.SPUtils;
import com.adayo.systemui.windows.bars.ShortcutDockBar;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShortcutDockPanel {
    private static final String TAG = ShortcutDockPanel.class.getSimpleName();
    private volatile static ShortcutDockPanel mShortcutDockPanel;
    private WindowManager mWindowManager;
    private RelativeLayout mFloatLayout;
    private WindowManager.LayoutParams mLayoutParams;
    private LinearLayout mLinearShortcut;
    private RecyclerView mRvShortcutApp;
    private ImageView mImgShortcutEdit;
    private float startX;
    private ShortcutAppAdapter mShortcutAppAdapter;
    private List<AppInfo> mShortAppListNone = new ArrayList<>();

    public static ShortcutDockPanel getInstance() {
        if (mShortcutDockPanel == null) {
            synchronized (ShortcutDockPanel.class) {
                if (mShortcutDockPanel == null) {
                    mShortcutDockPanel = new ShortcutDockPanel();
                }
            }
        }
        return mShortcutDockPanel;
    }

    private ShortcutDockPanel() {
        initView();
        initData();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        mFloatLayout = (RelativeLayout) View.inflate(SystemUIApplication.getSystemUIContext(), R.layout.shortcut_dock_panel_layout, null);
        mLayoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, 2073, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        mLayoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
        mLayoutParams.x = 60;
        mWindowManager.addView(mFloatLayout, mLayoutParams);

        EventBus.getDefault().register(this);
        mLinearShortcut = mFloatLayout.findViewById(R.id.linear_shortcut);
        mRvShortcutApp = mFloatLayout.findViewById(R.id.rv_shortcut_app);
        mImgShortcutEdit = mFloatLayout.findViewById(R.id.img_shortcut_edit);

        mLinearShortcut.setVisibility(View.GONE);

        mLinearShortcut.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getRawX();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = event.getRawX() - startX;
                    if (deltaX > -50) {  // You can adjust the threshold for left swipe
                        setShortcutBarVisibility(View.GONE);
                        ShortcutDockBar.getInstance().setShortcutBarVisibility(View.VISIBLE);
                    }
                    break;
            }
            return true;
        });

        mImgShortcutEdit.setOnClickListener(v -> {
            ShortcutDockEditDialog.getInstance().setShortcutEditVisibility(View.VISIBLE);
            setShortcutBarVisibility(View.GONE);
        });

    }

    private void initData() {
        AllAppsManager allAppsManager = AllAppsManager.getInstance(mRvShortcutApp.getContext());
        List<AppInfo> shortDockApp = allAppsManager.getShortDockApp();
        SPUtils instance = SPUtils.getInstance(mRvShortcutApp.getContext());
        List<String> shortAppListNone = instance.getList("short_app_list_none", null);
        AAOP_LogUtils.d(TAG, "shortAppListNone.size: " + shortAppListNone.size());
        if (shortAppListNone.size() == 0) {
            for (AppInfo shortDockApps : shortDockApp) {
                shortAppListNone.add(shortDockApps.getAppName());
            }
            instance.saveList("short_app_list_none", shortAppListNone);
            mShortAppListNone.addAll(shortDockApp);
        } else {
            List<AppInfo> floatApp = allAppsManager.getFloatApp();
            for (String appName : shortAppListNone) {
                for (AppInfo shortDockApps : shortDockApp) {
                    if (shortDockApps.getAppName().equals(appName)) {
                        mShortAppListNone.add(shortDockApps);
                        break;
                    }
                }
                for (AppInfo floatApps : floatApp) {
                    if (floatApps.getAppName().equals(appName)) {
                        mShortAppListNone.add(floatApps);
                        break;
                    }
                }
            }
        }
        AAOP_LogUtils.d(TAG, "shortAppListNone: " + shortAppListNone + " ,mShortAppListNone: " + mShortAppListNone);
        allAppsManager.setShortcutDockNoneApp(mShortAppListNone);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SystemUIApplication.getSystemUIContext(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        mRvShortcutApp.setLayoutManager(linearLayoutManager);
        mShortcutAppAdapter = new ShortcutAppAdapter(mShortAppListNone);
        mRvShortcutApp.setAdapter(mShortcutAppAdapter);
        mRvShortcutApp.setFocusable(true);
        mRvShortcutApp.setFocusableInTouchMode(true);
        mRvShortcutApp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mRvShortcutApp.post(new Runnable() {
                        @Override
                        public void run() {
                            AAOP_LogUtils.d(TAG, "onFocusChange" +mShortcutAppAdapter.getFocusedItem());
//                            mRvShortcutApp.scrollToPosition(mShortcutAppAdapter.getFocusedItem());
//                            mRvShortcutApp.findViewHolderForAdapterPosition(mShortcutAppAdapter.getFocusedItem()).itemView.requestFocus();
                        }
                    });
                }
            }
        });

        mShortcutAppAdapter.setOnItemClickListener(position -> {
            startApp(mShortAppListNone.get(position));
            setShortcutBarVisibility(View.GONE);
            ShortcutDockBar.getInstance().setShortcutBarVisibility(View.VISIBLE);
        });

    }

    public void upDataUI(List<AppInfo> appInfoList) {
        mShortcutAppAdapter.setData(appInfoList);
    }

    public void setShortcutBarVisibility(int visibility) {
        mLinearShortcut.setVisibility(visibility);
    }

    public boolean isShowShortcutDock() {
        return mLinearShortcut.getVisibility() == View.VISIBLE;
    }

    private void startApp(AppInfo info) {
        if (TextUtils.equals(info.getAppName(), SystemUIApplication.getSystemUIContext().getString(R.string.menu_ac))) {
            HvacPanel.getInstance().hvacScrollLayoutShow();
        } else {
            SourceControllerImpl.getInstance().requestSource(AppConfigType.SourceSwitch.APP_ON.getValue(), info.getSource(), new HashMap<>());
        }
        mShortcutAppAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData data) {
        AAOP_LogUtils.d(TAG, "ccp eventbus ->>" + data.getType());
        switch (data.getType()) {
            case EventContents.EVENT_HVAC_LAYOUT_CCP_7111:
                requestFocusChange(View.FOCUS_UP);
                break;
            case EventContents.EVENT_HVAC_LAYOUT_CCP_8111:
                requestFocusChange(View.FOCUS_DOWN);
                break;
            default:
                break;
        }

    }

    public void requestFocusChange(int direction) {
        switch (direction){
            case View.FOCUS_DOWN:
                mShortcutAppAdapter.moveFocus(true);
                break;
            case View.FOCUS_UP:
                mShortcutAppAdapter.moveFocus(false);
                break;
        }
    }

    public void onClick() {
        mShortcutAppAdapter.onClick();
    }
}
