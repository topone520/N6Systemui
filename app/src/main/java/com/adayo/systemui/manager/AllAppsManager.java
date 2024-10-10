package com.adayo.systemui.manager;

import static android.content.Context.ACTIVITY_SERVICE;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.systemui.bean.AppInfo;
import com.adayo.systemui.contents.ConnectDeviceConstant;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.utils.SPHelper;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * by jcshao
 */

public class AllAppsManager {
    private static final String TAG = "AllAppsManager";
    private static final int APP_MAX_NUM = 10;
    private static AllAppsManager mAllAppsManager;
    private List<AppInfo> otherData = new ArrayList<>();
    private List<AppInfo> mShortcutDockNoneApp = new ArrayList<>();
    private List<OnDataChangeListener> mOnDataChangeListener = new ArrayList<>();
    private Context context;


    public static AllAppsManager getInstance(Context context) {
        if (null == mAllAppsManager) {
            synchronized (AllAppsManager.class) {
                if (null == mAllAppsManager) {
                    mAllAppsManager = new AllAppsManager(context);
                }
            }
        }
        return mAllAppsManager;
    }

    public void onCreate() {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStop() {

    }

    public void onDestroy() {

    }

    private AllAppsManager(Context context) {
        this.context = context;

    }

    /**
     * 获取最近任务列表具体信息 * @param context * @return  返回最近任务列表
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static List<AppInfo> getRecentlyApp(Context context, List<AppInfo> appInfoList) {
        List<AppInfo> list = new ArrayList<>();
        final PackageManager pm = context.getPackageManager();

        final ActivityManager tasksManager = (ActivityManager) SystemUIApplication.getSystemUIContext().getSystemService(ACTIVITY_SERVICE);

        //此方法要调用Framework层的方法，获取最近任务列表
        List<ActivityManager.RecentTaskInfo> recents = tasksManager.getRecentTasks(APP_MAX_NUM, 0);
        try {
            for (int i = 0; i < recents.size(); i++) {

                //通过RecentTaskInfo转换ResolveInfo 获取包名、应用名、icon
                final ActivityManager.RecentTaskInfo info = recents.get(i);
                Intent intent = new Intent(info.baseIntent);
                if (info.origActivity != null) {
                    intent.setComponent(info.origActivity);
                }
                intent.setFlags((intent.getFlags() & ~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED) | Intent.FLAG_ACTIVITY_NEW_TASK);

                // 获取指定应用程序activity的信息
                final ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
                //屏蔽不显示的应用
                if (resolveInfo != null) {
                    final ActivityInfo activityInfo = resolveInfo.activityInfo;
                    final String title = activityInfo.loadLabel(pm).toString();
                    final String pkName = resolveInfo.activityInfo.packageName;

                    AppInfo infoBean = new AppInfo();
                    LogUtil.d("getRelyAcentpp: pkName = " + pkName);
                    if (!isIgnoreApplication(pkName)) {
                        Drawable drawable = getIcon(pkName);
                        Drawable icon = activityInfo.loadIcon(pm);
                        if (drawable != null) {
                            LogUtil.d("getRecentlyApp: drawable != null");
                            for (int y = 0; y < appInfoList.size(); y++) {
                                if (appInfoList.get(y).getPackageName().equals(pkName)) {
                                    infoBean.setImage(appInfoList.get(y).getImage());
                                }
                            }
                            if (pkName.equals("com.adayo.app.bcm")){
                                infoBean.setImage(context.getDrawable(R.mipmap.ivi_home_allapp_settings_icon_120));
                            }
                        } else {
                            LogUtil.d("getRecentlyApp: drawable == null");
                            infoBean.setImage(icon);
                        }
                        infoBean.setAppName(title);
                        infoBean.setPackageName(pkName);
                        list.add(infoBean);
                    }

                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    final static HashSet<String> _ignore_list = new HashSet<>(Arrays.asList("com.adayo.app.factorymode", "com.adayo.service.log", "com.adayo.audio.demo"));

    private static boolean isIgnoreApplication(String package_name) {
        return _ignore_list.contains(package_name);
    }

    private void setRoundIcon(String pkName) {
        PackageManager packageManager = SystemUIApplication.getSystemUIContext().getPackageManager();
        packageManager.setComponentEnabledSetting(new ComponentName(String.valueOf(this), pkName + ".MainActivity"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        packageManager.setComponentEnabledSetting(new ComponentName(String.valueOf(this), pkName + ".RoundActivity"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    private void setPrimitiveIcon(String pkName) {
        PackageManager packageManager = SystemUIApplication.getSystemUIContext().getPackageManager();
        packageManager.setComponentEnabledSetting(new ComponentName(String.valueOf(this), pkName + ".RoundActivity"), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        packageManager.setComponentEnabledSetting(new ComponentName(String.valueOf(this), pkName + ".MainActivity"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    private static Drawable getIcon(String pkName) {
        Drawable appIcon = null;
        if (pkName != null) {
            PackageManager packageManager = SystemUIApplication.getSystemUIContext().getPackageManager();
            PackageInfo packageInfo;
            ApplicationInfo applicationInfo = null;
            try {
                packageInfo = packageManager.getPackageInfo(pkName, 0);
                applicationInfo = packageInfo.applicationInfo;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            appIcon = packageManager.getApplicationIcon(applicationInfo);

        }
        return appIcon;
    }

    public List<AppInfo> getRecentlyApp(List<AppInfo> appInfoList) {

        List<AppInfo> appInfoLists = new ArrayList();

        try {
            List<AppInfo> appInfoListAll = getRecentlyApp(SystemUIApplication.getSystemUIContext(), appInfoList);
            // 获取前三条数据
            appInfoLists = appInfoListAll.subList(0, Math.min(3, appInfoListAll.size()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return appInfoLists;

    }


    public List<AppInfo> getFloatApp() {

        List<AppInfo> myAppInfos = new ArrayList();

        AppInfo myAppInfo = new AppInfo();

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_map));
        myAppInfo.setPackageName(ConnectDeviceConstant.PM_MAP);
        myAppInfo.setSource("");
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_map_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_dvr));
        myAppInfo.setPackageName("com.adayo.app.dvr");
        myAppInfo.setSource(AdayoSource.ADAYO_SOURCE_DVR);
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_selfie_icon_120 ));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_appstore));
        myAppInfo.setPackageName("com.baic.icc.appstore");
        myAppInfo.setSource("ADAYO_SOURCE_APPSTORE");
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_shop_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_camera));
        myAppInfo.setPackageName("com.arcsoft.magicmirror");
        myAppInfo.setSource("ADAYO_SOURCE_BEAUTY_SHOT");
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_camara_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_carmaster));
        myAppInfo.setPackageName("com.adayo.app.userguide");
        myAppInfo.setSource("ADAYO_SOURCE_USER_GUIDE");
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_driver_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_battery));
        myAppInfo.setPackageName("com.baic.icc.battery");
        myAppInfo.setSource(AdayoSource.ADAYO_SOURCE_CHARGECENTER);
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_charge_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_phone));
        myAppInfo.setPackageName("com.adayo.app.adayobluetoothphone");
        myAppInfo.setSource(AdayoSource.ADAYO_SOURCE_BT_PHONE);
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_bluetoothtelephone_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_account));
        myAppInfo.setPackageName("com.baic.icc.account");
        myAppInfo.setSource("ADAYO_SOURCE_ACCOUNT");
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_vip_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_drivingreport));
        myAppInfo.setPackageName("com.adayo.app.drivingdata");
        myAppInfo.setSource(AdayoSource.ADAYO_SOURCE_DRIVINGREPORT);
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_travel_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_weather));
        myAppInfo.setPackageName("com.adayo.app.weather");
        myAppInfo.setSource("ADAYO_SOURCE_WEATHER");
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_weather_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_wallpaper));
        myAppInfo.setPackageName("com.baic.icc.wallpaperstore");
        myAppInfo.setSource("ADAYO_SOURCE_WALLPAPER_STORE");
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_wallpaper_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_scene));
        myAppInfo.setPackageName(ConnectDeviceConstant.PM_SCENE);
        myAppInfo.setSource("");
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_higer_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_image));
        myAppInfo.setPackageName("com.adayo.aaop_deviceservice");
        myAppInfo.setSource("ADAYO_SOURCE_AVM");
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_avm_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_link));
        myAppInfo.setPackageName(ConnectDeviceConstant.PM_LINK);
        myAppInfo.setSource("");
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_link_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_parking));
        myAppInfo.setPackageName("com.adayo.aaop_deviceservice");
        myAppInfo.setSource("ADAYO_SOURCE_APA");
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_pack_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_carplay));
        myAppInfo.setPackageName(ConnectDeviceConstant.PM_CAR_PLAY);
        myAppInfo.setSource("");
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_carplay_icon_120));
        myAppInfos.add(myAppInfo);

        //TODO icall&ecall
        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_icall_ecall));
        myAppInfo.setPackageName("");
        myAppInfo.setSource("");
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_icall_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_hi_car));
        myAppInfo.setPackageName(ConnectDeviceConstant.PM_HI_CAR);
        myAppInfo.setSource("ADAYO_SOURCE_HICAR");
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_hicar_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_gallery));
        myAppInfo.setPackageName("com.adayo.app.gallery");
        myAppInfo.setSource("ADAYO_SOURCE_PHOTO");
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_photo_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_lightdance));
        myAppInfo.setPackageName("com.adayo.app.lampdance");
        myAppInfo.setSource("ADAYO_SOURCE_LAMPDANCE");
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_lightdance_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_media));
        myAppInfo.setPackageName("com.arcfox.media");
        myAppInfo.setSource(AdayoSource.ADAYO_SOURCE_MULTIMEDIA);
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_media_icon_120));
        myAppInfos.add(myAppInfo);

        //TODO 车机管家
        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_car_manager));
        myAppInfo.setPackageName("com.adayo.app.housekeep");
        myAppInfo.setSource("ADAYO_SOURCE_HOUSE_KEEP");
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_housekeep_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_setting));
        myAppInfo.setPackageName("com.adayo.app.setting");
        myAppInfo.setSource(AdayoSource.ADAYO_SOURCE_SETTING);
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_settings_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_car_bit));
        myAppInfo.setPackageName("net.easyconn");
        myAppInfo.setSource(AdayoSource.ADAYO_SOURCE_CARBIT);
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_carbit_icon_120));
        myAppInfos.add(myAppInfo);

        myAppInfo = new AppInfo();
        myAppInfo.setAppName(context.getString(R.string.menu_tencent_video));
        myAppInfo.setPackageName("com.tencent.qqlive.audiobox");
        myAppInfo.setSource("com.tencent.qqlive.audiobox");
        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_qqlive_120));
        myAppInfos.add(myAppInfo);

//        myAppInfo = new AppInfo();
//        myAppInfo.setAppName(context.getString(R.string.menu_fm));
//        myAppInfo.setPackageName("com.arcfox.media");
//        myAppInfo.setSource(AdayoSource.ADAYO_SOURCE_RADIO);
//        myAppInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_radio_icon_120));
//        myAppInfos.add(myAppInfo);

        return myAppInfos;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public List<AppInfo> getShortDockApp() {
        List<AppInfo> mShortDockAppList = new ArrayList();
        AppInfo appInfo;

        appInfo = new AppInfo();
        appInfo.setAppName(context.getString(R.string.menu_dvr));
        appInfo.setPackageName("com.adayo.app.dvr");
        appInfo.setSource(AdayoSource.ADAYO_SOURCE_DVR);
        appInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_selfie_icon_120 ));
        mShortDockAppList.add(appInfo);

        appInfo = new AppInfo();
        appInfo.setAppName(context.getString(R.string.menu_camera));
        appInfo.setPackageName("com.arcsoft.magicmirror");
        appInfo.setSource("ADAYO_SOURCE_BEAUTY_SHOT");
        appInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_camara_icon_120));
        mShortDockAppList.add(appInfo);

        appInfo = new AppInfo();
        appInfo.setAppName(context.getString(R.string.menu_media));
        appInfo.setPackageName("com.arcfox.media");
        appInfo.setSource(AdayoSource.ADAYO_SOURCE_MULTIMEDIA);
        appInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_media_icon_120));
        mShortDockAppList.add(appInfo);

        appInfo = new AppInfo();
        appInfo.setAppName(context.getString(R.string.menu_ac));
        appInfo.setPackageName("com.android.systemui");
        appInfo.setSource("");
        appInfo.setImage(this.context.getDrawable(R.mipmap.ivi_home_allapp_ac_icon_120));
        mShortDockAppList.add(appInfo);

        return mShortDockAppList;

    }


    public List<AppInfo> getAllAppData() {
        PackageManager packageManager = context.getPackageManager();
        SPHelper spHelper = new SPHelper(SystemUIApplication.getSystemUIContext(), "systemui");
        SPHelper notifiySpHelper = new SPHelper(SystemUIApplication.getSystemUIContext(), "systemui_notifiy");
        String json = spHelper.getString("allapp");
        HashMap<String, Integer> map = null;
        if (!TextUtils.isEmpty(json)) {
            java.lang.reflect.Type type = new TypeToken<HashMap<String, Integer>>() {
            }.getType();
            map = new Gson().fromJson(json, type);
        }

        List<AppInfo> myAppInfos = new ArrayList();
//        int xmlId = R.xml.app_list;
//        XmlResourceParser xmlParser = context.getResources().getXml(xmlId);
//        try {
//            int event = xmlParser.getEventType();
//            int index = 0;
//            while (event != XmlPullParser.END_DOCUMENT){
//                switch (event){
//                    case XmlPullParser.START_DOCUMENT:
//                        break;
//                    case XmlPullParser.START_TAG:
//                        if(xmlParser.getName().equals("app")){
//                            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(Xml.asAttributeSet(xmlParser), R.styleable.AppInfo);
//
//                            int icon = obtainStyledAttributes.getResourceId(0,0);
//                            String name = obtainStyledAttributes.getString(1);
//                            String pName = obtainStyledAttributes.getString(2);
//                            String source = obtainStyledAttributes.getString(3);
//                            if(TextUtils.equals(source,"ADAYO_SOURCE_REARVIEW") && !ConfigureWordByViewUtils.getInstance().isbView()){
//                                event = xmlParser.next();
//                                continue;
//                            }
//                            AppInfo appInfo = new AppInfo();
//                            appInfo.setAppName(name);
//                            appInfo.setImage(context.getDrawable(icon));
//                            appInfo.setSource(source);
//                            appInfo.setPackageName(pName);
//                            appInfo.setNumberStart(spHelper.getInt("ADAYO_SOURCE_CHARGECENTER"));
//                            appInfo.setMessageNotification(notifiySpHelper.getInt(pName) == 0 ? 0 : 1);
//
//                            if (map != null && map.get(appInfo.getPackageName()) != null){
//                                appInfo.setIndex(map.get(appInfo.getPackageName()));
//                            }
//
//
//                            myAppInfos.add(appInfo);
//
//                        }
//                        break;
//
//                    case XmlPullParser.END_TAG:
//                        break;
//                    default:
//                        break;
//                }
//                event = xmlParser.next();
//            }
//        }catch (XmlPullParserException | IOException e){
//            e.printStackTrace();
//        }
        try {
            List packageInfos = packageManager.getInstalledPackages(0);
            AppInfo myAppInfo = new AppInfo();
            for (int i = 0; i < packageInfos.size(); i++) {
                PackageInfo packageInfo = (PackageInfo) packageInfos.get(i);
                //过滤掉系统app
                if ((ApplicationInfo.FLAG_SYSTEM & packageInfo.applicationInfo.flags) != 0) {
                    LogUtil.e("FLAG_SYSTEM" + packageInfo.packageName);

                    continue;
                }
                myAppInfo = new AppInfo();
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                myAppInfo.setAppName(appName);
                myAppInfo.setPackageName(packageInfo.packageName);
                myAppInfo.setNumberStart(spHelper.getInt(packageInfo.packageName));
                myAppInfo.setMessageNotification(notifiySpHelper.getInt(packageInfo.packageName) == 0 ? 0 : 1);
                LogUtil.e("=======================");
                LogUtil.e("包名:" + packageInfo.packageName);
                LogUtil.e("应用名:" + appName);
                LogUtil.e("=======================");
                if (packageInfo.applicationInfo.loadIcon(packageManager) == null) {
                    continue;
                }
                if (map != null && map.get(myAppInfo.getPackageName()) != null) {
                    myAppInfo.setIndex(map.get(myAppInfo.getPackageName()));
                }
                myAppInfo.setImage(packageInfo.applicationInfo.loadIcon(packageManager));
                myAppInfos.add(myAppInfo);
            }
        } catch (Exception e) {
            LogUtil.e("获取应用包信息失败");
        }


        List<AppInfo> list = myAppInfos.stream().sorted(Comparator.comparing(e -> e.getIndex())).collect(Collectors.toList());

        return list;

    }

    public void setShortcutDockNoneApp(List<AppInfo> shortcutDockNoneApp){
        mShortcutDockNoneApp = shortcutDockNoneApp;
    }

    public List<AppInfo> getShortcutDockNoneApp(){
        return mShortcutDockNoneApp;
    }


    public void saveAllApp(List<AppInfo> list) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (int i = 0; i < list.size(); i++) {
            AppInfo info = list.get(i);
            map.put(info.getPackageName(), i);
        }
        String json = new Gson().toJson(map);
        SPHelper spHelper = new SPHelper(SystemUIApplication.getSystemUIContext(), "systemui");
        spHelper.putValues(new SPHelper.ContentValue("allapp", json));

    }

    public interface OnDataChangeListener {
        void dataChange();
    }

    public void registerDataChangeListener(OnDataChangeListener mOnDataChangeListener) {
        this.mOnDataChangeListener.add(mOnDataChangeListener);
    }

}
