package com.adayo.systemui.windows.panels;

import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
import static com.adayo.systemui.contents.PublicContents.TYPE_OF_ALL_APPS_PANEL;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.IPackageDeleteObserver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.carplay.proxy.CarPlayProxy;
import com.adayo.carplay.proxy.Session;
import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AppConfigType;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.utils.Dispatcher;
import com.adayo.systemui.adapters.AllAppAdapter;
import com.adayo.systemui.adapters.RecentlyAppAdapter;
import com.adayo.systemui.bean.AppInfo;
import com.adayo.systemui.contents.ConnectDeviceConstant;
import com.adayo.systemui.eventbus.EventContents;
import com.adayo.systemui.eventbus.EventData;
import com.adayo.systemui.manager.AllAppsManager;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.manager.SourceControllerImpl;
import com.adayo.systemui.manager.WindowsControllerImpl;
import com.adayo.systemui.utils.AA0P_CarLinkManager;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.dialogs.PopupDeleteAppDialog;
import com.adayo.systemui.windows.views.MyTouchHelperCallback;
import com.adayo.systemui.windows.views.OnItemPositionListener;
import com.adayo.systemui.windows.views.PagingScrollHelper;
import com.adayo.systemui.windows.views.hvac.HvacSpeedSeekBar;
import com.adayo.systemui.windows.views.hvac.VerSelectedView;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.carlinx.arcfox.carlink.arclink.ArcLinkManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class ShowAppListDialog extends Dialog implements PagingScrollHelper.onPageChangeListener {

    private static final String TAG = ShowAppListDialog.class.getSimpleName();
    private RecyclerView appList;
    private RecyclerView rv_float;

    private LinearLayout indicatorView;
    private RelativeLayout rl_app_recent, rl_app_edit;
    private CardView cv_edit_enter;
    private AllAppAdapter adapter;
    private RecentlyAppAdapter floatAdapter;
    private List<AppInfo> appInfoList = new ArrayList<>();
    private List<AppInfo> appOldList = new ArrayList<>();
    private List<AppInfo> newInfoList = new ArrayList<>();
    private List<AppInfo> floatAppInfoList = new ArrayList<>();
    private PagingScrollHelper scrollHelper = new PagingScrollHelper();
    private int realitySize;
    private List<ImageView> indicatorImages = new ArrayList<>();
    private PopupDeleteAppDialog popupDeleteAppDialog;
    private volatile static ShowAppListDialog dialog;
    private float scrollX;
    private float scrollY;

    public static ShowAppListDialog getInstance() {
        if (dialog == null) {
            synchronized (ShowAppListDialog.class) {
                if (dialog == null) {
                    dialog = new ShowAppListDialog(SystemUIApplication.getSystemUIContext());
                }
            }
        }

        return dialog;
    }


    private ShowAppListDialog(Context context) {
        super(context, R.style.TransparentStyle);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM | Gravity.RIGHT);
        window.setType(TYPE_APPLICATION_OVERLAY);
        window.setWindowAnimations(R.style.AllAppInOutAnimation);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        layoutParams.x = 0;
        layoutParams.width = 1784;
        layoutParams.height = 560;
        window.setAttributes(layoutParams);
        setContentView(R.layout.dialog_applist);
        EventBus.getDefault().register(this);
        appList = findViewById(R.id.dialog_applist);
        rv_float = findViewById(R.id.rv_float);
        indicatorView = findViewById(R.id.dialog_indicator);
        rl_app_recent = findViewById(R.id.rl_app_recent);
        rl_app_edit = findViewById(R.id.rl_app_edit);
        cv_edit_enter = findViewById(R.id.cv_edit_enter);
        setCanceledOnTouchOutside(true);
        initData();

        initCarPlay();
    }

    private void enterEditMode() {
        int size = appInfoList.size();
        for (int i = 0; i < size; i++) {
            AppInfo appInfo = appInfoList.get(i);
            appInfo.setIsCanClick(false);
            LogUtil.d("AppDelete---appInfo.getAppName = " + appInfo.getAppName() + appInfo.getAppName().length());
            if (_is_support_app(appInfo) && !appInfo.getAppName().isEmpty()) {
                LogUtil.d("IsShowDelete true");
                appInfo.setIsShowDelete(true);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void initCarPlay() {
        getContext().getContentResolver().registerContentObserver(Settings.System.getUriFor(ConnectDeviceConstant.KEY_WIRELESS_DEVICE), true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange, @Nullable Uri uri) {
                super.onChange(selfChange, uri);
                LogUtil.i("uri = " + uri);
                if (null != adapter) {
                    adapter.notifyDataSetChanged();
                }
                if (null != floatAdapter) {
                    floatAdapter.notifyDataSetChanged();
                }
            }
        });
        getContext().getContentResolver().registerContentObserver(Settings.System.getUriFor(ConnectDeviceConstant.KEY_WIRED_DEVICE), true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange, @Nullable Uri uri) {
                super.onChange(selfChange, uri);
                LogUtil.i("uri = " + uri);
                if (null != adapter) {
                    adapter.notifyDataSetChanged();
                }
                if (null != floatAdapter) {
                    floatAdapter.notifyDataSetChanged();
                }
            }
        });

    }


    private void initData() {
        //RecyclerView翻页效果
//        scrollHelper.setUpRecycleView(appList);
//        scrollHelper.setOnPageChangeListener(this);

        appInfoList.addAll(AllAppsManager.getInstance(getContext()).getAllAppData());
        LogUtil.d("appInfoList getAllAppData(): " + AllAppsManager.getInstance(getContext()).getAllAppData());
        appInfoList.addAll(AllAppsManager.getInstance(getContext()).getFloatApp());
        LogUtil.d("appInfoList getFloatApp(): " + AllAppsManager.getInstance(getContext()).getFloatApp());
        appOldList.addAll(appInfoList);
        realitySize = appInfoList.size();
        addData();
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        appList.setLayoutManager(manager);
        adapter = new AllAppAdapter(appInfoList);
        appList.setAdapter(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(new MyTouchHelperCallback(adapter, getContext()));
        adapter.setOnItemLongClickListener(() -> {
            rl_app_edit.setVisibility(View.VISIBLE);
            rl_app_recent.setVisibility(View.GONE);
            helper.attachToRecyclerView(appList);
        });

        cv_edit_enter.setOnClickListener(v -> {
            rl_app_edit.setVisibility(View.GONE);
            rl_app_recent.setVisibility(View.VISIBLE);
            helper.attachToRecyclerView(null);
        });


        AA0P_CarLinkManager.getInstance().setOnAppListListener((s, list) -> {
            newInfoList.clear();
            if (s != null) {
                if (!list.isEmpty()) {
                    appInfoList.clear();
                    appInfoList.addAll(appOldList);
                    LogUtil.d(appInfoList.size() + " getAppList: appInfoList+++：" + appInfoList.toString());
                    for (int i = 0; i < list.size(); i++) {
                        LogUtil.d("getAppList: " + list.get(i));
                        AppInfo appInfo = new AppInfo();
                        appInfo.setAppId(list.get(i).appId);
                        appInfo.setAppName(list.get(i).label);
                        appInfo.setCategory(list.get(i).category);
                        appInfo.setIconPath(list.get(i).iconPath);
                        appInfo.setIconDesc(list.get(i).iconDesc);
                        appInfoList.add(appInfo);
                        newInfoList.add(appInfo);
                    }
                    addData();
                    LogUtil.d(appInfoList.size() + " getAppList: appInfoList：" + appInfoList.toString());
                    LogUtil.d("getAppList: newInfoList：" + newInfoList.toString());
                    adapter.notifyDataSetChanged();
                }
            }
        });
        AA0P_CarLinkManager.getInstance().setOnStateListener(new AA0P_CarLinkManager.OnStateListener() {
            @Override
            public void onStateListener(String s, int state) {
                LogUtil.d("onConnectStateChanged: " + s + " , " + state);
                if (state == 1) {
                    if (newInfoList.size() == 0) {
                        return;
                    }
                    LogUtil.d("getAppList: onConnectStateChanged: state：" + state);
                    for (int i = 0; i < newInfoList.size(); i++) {
                        for (int i1 = 0; i1 < appInfoList.size(); i1++) {
                            if (appInfoList.get(i1).getAppId() == newInfoList.get(i).getAppId()) {
                                appInfoList.remove(i1);
                            }
                        }
                    }
                    initIndicator();
                    adapter.setAppList(appInfoList);
                    adapter.notifyDataSetChanged();
                }
            }
        });


        adapter.setOnItemClickListener(new AllAppAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                LogUtil.d("adapter:onItemClickListener: " + position + " , " + appInfoList.get(position));
                startApp(appInfoList.get(position));
                dismiss();
            }
        });

//        adapter.setOnItemLongClickListener(new AllAppAdapter.OnItemLongClickListener() {
//            @Override
//            public void onItemLongClickListener() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        // enterEditMode();
//                    }
//                }, 500);
//            }
//        });

        adapter.setOnDeleteClickListener(new AllAppAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClickListener(int position) {
                AppInfo appInfo = appInfoList.get(position);
                LogUtil.d("AppDelete----onDeleteClickListener----" + appInfo.getPackageName());
                popupDeleteAppDialog = new PopupDeleteAppDialog(SystemUIApplication.getSystemUIContext(), 720, 360);
                popupDeleteAppDialog.setAppInfo(appInfo);
                popupDeleteAppDialog.showPanel();
            }
        });
//        此方法进入编辑模式过于灵敏，暂时屏蔽
//        appList.setOnTouchListener((v, event) -> {
//            if(event.getAction() == MotionEvent.ACTION_DOWN){
//                scrollX = event.getX();
//                scrollY = event.getY();
//            }
//            if (event.getAction() == MotionEvent.ACTION_UP) {
//                if (v.getId() != 0 && Math.abs(scrollX - event.getX()) <= 5 && Math.abs(scrollY - event.getY()) <= 5) {
//                    //recyclerView空白处点击事件
//                    enterEditMode();
//                }
//            }
//            return false;
//        });

    }

    //不可卸载的应用包名
    final static HashSet<String> _items = new HashSet<>(Arrays.asList("com.adayo.app.dvr", "com.autonavi.amapauto", "com.baic.icc.app.scene", "com.baic.icc.wallpaperstore", "com.carlinx.arcfox.carlink", "com.adayo.app.hicar", "com.arcsoft.magicmirror", "com.adayo.app.drivingdata", "com.adayo.app.setting", "com.adayo.app.adayobluetoothphone", "com.adayo.app.gallery", "com.adayo.app.drivingreport", "com.adayo.app.media", "com.arcfox.media", "com.baic.icc.account", "com.adayo.app.filemanager", "com.adayo.app.weather", "com.baic.icc.appstore", "com.adayo.carplay.view", "com.adayo.aaop_deviceservice", "com.adayo.app.userguide", "com.baic.icc.battery"));

    private boolean _is_support_app(AppInfo appInfo) {

        return !_items.contains(appInfo.getPackageName());
    }

    public void deleteApp(AppInfo appInfo) {
        LogUtil.d("AppDelete----deleteApp---" + appInfo.getPackageName());
        popupDeleteAppDialog.showPanel();
        popupDeleteAppDialog = null;
        SystemUIApplication.getSystemUIContext().getPackageManager().deletePackage(appInfo.getPackageName(), new IPackageDeleteObserver.Stub() {
            @Override
            public void packageDeleted(String packageName, int returnCode) {
                LogUtil.d("AppDelete---returnCode = " + returnCode);
                if (returnCode == 1) {
                    //packageName 卸载成功
                    appInfoList.remove(appInfo);
                    int size = appInfoList.size();
                    for (int i = 0; i < size; i++) {
                        AppInfo appInfo = appInfoList.get(i);
                        appInfo.setIsCanClick(true);
                        appInfo.setIsShowDelete(false);
                    }
                    adapter.setAppList(appInfoList);
                    adapter.notifyDataSetChanged();
                } else {
                    //packageName 卸载失败
                    int size = appInfoList.size();
                    for (int i = 0; i < size; i++) {
                        AppInfo appInfo = appInfoList.get(i);
                        appInfo.setIsCanClick(true);
                        appInfo.setIsShowDelete(false);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        }, 0);

    }

    public void cancelDeleteApp() {
        LogUtil.d("AppDelete----cancelDeleteApp");
        popupDeleteAppDialog.showPanel();
        popupDeleteAppDialog = null;
        int size = appInfoList.size();
        for (int i = 0; i < size; i++) {
            AppInfo appInfo = appInfoList.get(i);
            appInfo.setIsCanClick(true);
            appInfo.setIsShowDelete(false);
        }
        adapter.notifyDataSetChanged();
    }

    private void updateRecentlyAppList() {
        floatAppInfoList.clear();
        floatAppInfoList.addAll(AllAppsManager.getInstance(getContext()).getRecentlyApp(appInfoList));
        GridLayoutManager floatManager = new GridLayoutManager(getContext(), 1);
//        floatManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_float.setLayoutManager(floatManager);
        floatAdapter = new RecentlyAppAdapter(floatAppInfoList, true);
        rv_float.setAdapter(floatAdapter);
        floatAdapter.setOnItemClickListener(new RecentlyAppAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(int position) {
                startApp(floatAppInfoList.get(position));
                dismiss();
            }
        });

    }

    public synchronized void showPanel() {
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM | Gravity.LEFT);
        window.setType(TYPE_APPLICATION_OVERLAY);
        window.setWindowAnimations(R.style.AllAppInOutAnimation);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        layoutParams.x = 40;
        layoutParams.y = 88;
        layoutParams.width = 1800;
        layoutParams.height = 560;
        window.setAttributes(layoutParams);
        if (!isShowing()) {
            updateRecentlyAppList();
            show();
            ReportBcmManager.getInstance().sendNegativeReport("5002002", "", "点开应用列表");
        } else {
            dismiss();
        }
    }

    @Override
    public void show() {
        super.show();
        WindowsControllerImpl.getInstance().notifyVisibility(TYPE_OF_ALL_APPS_PANEL, View.VISIBLE);
        Settings.System.putString(getContext().getContentResolver(), ConnectDeviceConstant.KEY_ALL_APP_STATUS, ConnectDeviceConstant.VALUE_ALL_APP_STATUS_SHOW);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        WindowsControllerImpl.getInstance().notifyVisibility(TYPE_OF_ALL_APPS_PANEL, View.GONE);
        Settings.System.putString(getContext().getContentResolver(), ConnectDeviceConstant.KEY_ALL_APP_STATUS, ConnectDeviceConstant.VALUE_ALL_APP_STATUS_DISMISS);
    }

    private void startApp(AppInfo info) {
        String uid = SourceControllerImpl.getInstance().getCurrentUISource();
        Log.e("ShowAppList", "SrcMngSwitchManager ++++++   :" + info.toString());
        Log.e("ShowAppList", "SrcMngSwitchManager.getInstance().getCurrentUID() app_view :" + uid);
        Log.e("ShowAppList", "startApp Source:" + info.getSource() + "pName " + info.getPackageName());
        //拉起指定app
        if (newInfoList.size() > 0) {
            for (AppInfo appInfo : newInfoList) {
                if (appInfo.getAppId() == info.getAppId()) {
                    ArcLinkManager.getInstance().appStartEventCallback(info.getAppId());
                    adapter.notifyDataSetChanged();
                    return;
                }
            }
        }
        if (TextUtils.equals(info.getKey(), uid)) {
            return;
        }
        Log.d("ccm------>", "startApp :" + info.getAppName());

        //判断自动泊车
        if (info.getPackageName() != null && info.getPackageName().equals("com.adayo.aaop_deviceservice")) {
            if (info.getSource().equals("ADAYO_SOURCE_APA")) {
                Bundle bundle = new Bundle();
                bundle.putString("SourceType", "ADAYO_SOURCE_APA");
                bundle.putString("VOICE_CONTROL", "APA_ON");
                AAOP_DeviceServiceManager.getInstance().setDeviceFuncUniversalInterface("CameraDevice", "open_parking", bundle);
            } else if (info.getSource() != null && info.getSource().equals("ADAYO_SOURCE_AVM")) {
                Bundle bundle = new Bundle();
                bundle.putString("SourceType", "ADAYO_SOURCE_AVM");
                bundle.putString("VOICE_CONTROL", "AVM_ON");
                AAOP_DeviceServiceManager.getInstance().setDeviceFuncUniversalInterface("CameraDevice", "open_panorama", bundle);
            }
        } else if (info.getAppName().equals("ICall")) {
            Log.d("ccm------>", "startApp2 :" + info.getAppName());
            AAOP_DeviceServiceManager.getInstance().setDeviceFuncUniversalInterface("TBoxDevice", "makeICall", new Bundle());
        } else {
            HashMap hashMap = new HashMap();
            if (TextUtils.isEmpty(info.getSource()) || AdayoSource.ADAYO_SOURCE_MULTIMEDIA.equals(info.getSource())) {
                String packageName = info.getPackageName();
                Log.e("ShowAppList", "无source" + packageName);
                SourceControllerImpl.getInstance().requestSoureApp(AdayoSource.ADAYO_SOURCE_APPMORE, packageName, AppConfigType.SourceSwitch.APP_ON.getValue(), new HashMap<>());
            } else {
                Log.e("ShowAppList", "有source");
                if (TextUtils.equals(info.getSource(), AdayoSource.ADAYO_SOURCE_CAMERA)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("SourceType", AdayoSource.ADAYO_SOURCE_AVM);
                    bundle.putString("VOICE_CONTROL", "AVM_ON");
                    AAOP_DeviceServiceManager.getInstance().setDeviceFuncUniversalInterface("CameraDevice", "open_panorama", bundle);
                } else {
                    //TODO  仿照北汽，这块逻辑布吉岛
                }

                //连接carplay时，打开carplay的电话页，未连接时，打开蓝牙电话页
                if (TextUtils.equals(info.getSource(), AdayoSource.ADAYO_SOURCE_BT_PHONE)) {
                    ContentResolver resolver = getContext().getContentResolver();
                    boolean enabled = TextUtils.equals(ConnectDeviceConstant.VALUE_CAR_PLAY, Settings.System.getString(resolver, ConnectDeviceConstant.KEY_WIRELESS_DEVICE)) || TextUtils.equals(ConnectDeviceConstant.VALUE_CAR_PLAY, Settings.System.getString(resolver, ConnectDeviceConstant.KEY_WIRED_DEVICE));
                    if (enabled) {
                        List<Session> sessions = CarPlayProxy.getInstance().getActiveSessions();

                        if (sessions == null || sessions.size() == 0) {
                            LogUtil.i("sessions == null||sessions.size() <= 0");
                            CarPlayProxy.getInstance().bindServiceAddListener(SystemUIApplication.getSystemUIContext(), new CarPlayProxy.IServiceConnectListener() {
                                @Override
                                public void onServiceConnected() {
                                }

                                @Override
                                public void onServiceDisConnected() {
                                }
                            });
                            hashMap.put("AppType", "Telephone");
                            SourceControllerImpl.getInstance().requestSoureApp(AdayoSource.ADAYO_SOURCE_APPMORE, ConnectDeviceConstant.PM_CAR_PLAY, AppConfigType.SourceSwitch.APP_ON.getValue(), hashMap);
                        } else {
                            CarPlayProxy.getInstance().launchCarPlay(sessions.get(0), 2);
                        }
                    } else {
                        SourceControllerImpl.getInstance().requestSource(AppConfigType.SourceSwitch.APP_ON.getValue(), info.getSource(), hashMap);
                    }
                } else {
                    SourceControllerImpl.getInstance().requestSource(AppConfigType.SourceSwitch.APP_ON.getValue(), info.getSource(), hashMap);
                }

            }
            info.setNewInstall(false);
        }
        //上面逻辑不知道具体是干什么的

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPageChange(int index) {
        Log.e("ShowAppList", "当前页数 :" + index);
        if (indicatorImages.size() == 0) return;
        for (int i = 0; i < indicatorImages.size(); i++) {
            indicatorImages.get(i).setImageResource(R.mipmap.icon_unselect);
        }
        if (index < indicatorImages.size()) {
            indicatorImages.get(index).setImageResource(R.mipmap.icon_select);
        }
    }


    /**
     * 长安拖拽
     */

    private class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
        // 设置支持的拖动和滑动的方向
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN | ItemTouchHelper.UP; // 支持上下拖动
            int swipeFlags = 0; // 不支持滑动
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        // 在拖动过程中不断调用，用于刷新RecyclerView的显示
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = source.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            // 更新数据集中的位置
//            Collections.swap(dataList, fromPosition, toPosition);
            if (toPosition < realitySize) {
                // 更新RecyclerView的显示
                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            }

            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        }
    }

    /**
     * 因为使用RecyclerView实现分页效果，
     * 当超过一页的时候并没有完全翻过去，手动创建第二页数据并填满
     */
    private void addData() {
        int index = appInfoList.size() % 12;
        if (index > 0 && index < 12) {
            addInfo(12 - index);
        }
        if (appInfoList.size() / 12 > 1) {
            initIndicator();
        }
    }

    private void addInfo(int i) {
        for (int j = 0; j < i; j++) {
            AppInfo info = new AppInfo();
            info.setAppName("");
            info.setPackageName("");
            info.setSource("");
            info.setImage(null);
            info.setAdd(true);
            appInfoList.add(info);
        }
    }

    private void initIndicator() {
        indicatorView.removeAllViews();
        indicatorImages.clear();
        int size = appInfoList.size() / 12;
        LogUtil.d(TAG + " initIndicator：" + size);
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(SystemUIApplication.getSystemUIContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams custom_params = new LinearLayout.LayoutParams(8, 8);
            custom_params.leftMargin = 8;
            custom_params.rightMargin = 8;
            if (i == 0) {
                imageView.setImageResource(R.mipmap.icon_select);
            } else {
                imageView.setImageResource(R.mipmap.icon_unselect);
            }
            indicatorImages.add(imageView);
            indicatorView.addView(imageView, custom_params);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData data) {
        AAOP_LogUtils.d(TAG, "ccp eventbus ->>" + data.getType());
        switch (data.getType()) {
            case EventContents.EVENT_HVAC_LAYOUT_CCP_711:
                AAOP_LogUtils.d(TAG, "appList: " + floatAdapter.getItemFocused()+" , appList: "+adapter.getItemFocused());
                if (!floatAdapter.getItemFocused() && !adapter.getItemFocused()){
                    floatAdapter.moveFocus(false);
                }else if (floatAdapter.getItemFocused()){
                    floatAdapter.moveFocus(false);
                    adapter.setItemFocused(false);
                }else {
                    adapter.moveFocus(View.FOCUS_UP);
                    floatAdapter.setItemFocused(false);
                }
                break;
            case EventContents.EVENT_HVAC_LAYOUT_CCP_811:
                AAOP_LogUtils.d(TAG, "appList: " + floatAdapter.getItemFocused()+" , appList: "+adapter.getItemFocused());
                if (!floatAdapter.getItemFocused() && !adapter.getItemFocused()){
                    floatAdapter.moveFocus(true);
                }else if (floatAdapter.getItemFocused()){
                    floatAdapter.moveFocus(true);
                    adapter.setItemFocused(false);
                }else {
                    adapter.moveFocus(View.FOCUS_DOWN);
                    floatAdapter.setItemFocused(false);
                }
                break;
            case EventContents.EVENT_HVAC_LAYOUT_CCP_911:
                AAOP_LogUtils.d(TAG, "appList: " + floatAdapter.getItemFocused()+" , appList: "+adapter.getItemFocused());
                if (adapter.getFocusedItem() == 0){
                    floatAdapter.moveFocus(false);
                    adapter.setItemFocused(false);
                }else {
                    adapter.moveFocus(View.FOCUS_LEFT);
                    floatAdapter.setItemFocused(false);
                }
                break;
            case EventContents.EVENT_HVAC_LAYOUT_CCP_1011:
                AAOP_LogUtils.d(TAG, "appList: " + floatAdapter.getItemFocused()+" , appList: "+adapter.getItemFocused());
                adapter.moveFocus(View.FOCUS_RIGHT);
                floatAdapter.setItemFocused(false);
                break;
        }
    }

    public void onClick() {
        adapter.onClick();
        floatAdapter.onClick();
    }
}
