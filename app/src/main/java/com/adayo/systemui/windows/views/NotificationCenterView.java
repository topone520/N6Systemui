package com.adayo.systemui.windows.views;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.systemui.adapters.NotificationCenterPageAdapter;
import com.adayo.systemui.adapters.SystemMessageAdapter;
import com.adayo.systemui.adapters.WarningMessageAdapter;
import com.adayo.systemui.bean.RefreshSystemMessageEvent;
import com.adayo.systemui.bean.RefreshWarningMessageEvent;
import com.adayo.systemui.bean.SystemMessageInfo;
import com.adayo.systemui.bean.WarningMessageInfo;
import com.adayo.systemui.eventbus.EventContents;
import com.adayo.systemui.manager.NotifyVoiceManager;
import com.adayo.systemui.notification.TrackingEnum;
import com.adayo.systemui.utils.NoScrollViewPager;
import com.adayo.systemui.utils.ResourceUtil;
import com.adayo.systemui.utils.SPHelper;
import com.adayo.systemui.utils.SwipeDeleteCallBack;
import com.adayo.systemui.utils.TrackingMessageDataUtil;
import com.adayo.systemui.windows.bars.StatusBar;
import com.adayo.systemui.windows.dialogs.CommonDialog;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Notification center view.
 */
public class NotificationCenterView implements View.OnClickListener {
    /**
     * The constant TAG.
     */
    public static String TAG = NotificationCenterView.class.getName();
    private NoScrollViewPager viewPager2;
    private NotificationCenterPageAdapter pageAdapter;
    private List<View> pageViews;

    private WindowManager.LayoutParams layoutParams;
    private WindowManager mWindowManager;
    private View mSystemMessageLayout;
    private View mWarningMessageLayout;
    private View mAppLayout;
    private SystemMessageAdapter messageAdapter;
    private WarningMessageAdapter warningMessageAdapter;
    private List<SystemMessageInfo> systemMessageInfoList;
    private View mAppMessageLayout;
    private boolean isAdded = false;
    private volatile static NotificationCenterView notificationCenterView;
    private View mRootView;
    private TextView tvSystemMessage;
    private TextView tvAppMessage;
    private ImageView ivEdit;
    private ImageView ivClose;
    private TextView tvComplete;
    private LinearLayout llMessage;
    private LinearLayout llMessageList;
    private LinearLayout llBtn;
    private TextView tvSelectAll;
    private LinearLayout llNoMessage;
    private LinearLayout llNoWarningMessage;
    private RecyclerView rvSystemMessage;
    private RecyclerView rvWarningMessage;
    private TextView tvDelete;
    private int systemMessageSelectPos;
    private LinearLayout llMessageDetail;
    private LinearLayout llMessageCenter;
    private ImageView ivBack;
    private List<SystemMessageInfo> selectNotificationList;
    private List<WarningMessageInfo> selectWarningList;
    private List<WarningMessageInfo> warningMessageInfoList = new ArrayList<>();
    ;
    private Gson gson;
    private SPHelper spHelper;
    private TextView tvSelectedNum;
    private TextView tvDetailTitle;
    private TextView tvPlatForm;
    private TextView tvDetailTime;
    private TextView tvDetailContent;
    private TextView tvGoToTrafficReCharge;
    private ImageView ivVoice;
    private String messageType;
    private ImageView ivNewPage;
    private TextView tvReadAll;
    //    private RefreshUIReceiver refreshUIReceiver;
    private boolean isRefreshUIReceiverRegistered = false;
    private static final String CANCEL_BG = "common_dialog_cancel_bg";
    private static final String DEL_BG = "common_dialog_del_bg";
    private static final String REQUEST_GUIDE = "com.adayo.service.navi.requestGuide";
    private static final String NOTIFY_SERVICE = "NotifyService";

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static NotificationCenterView getInstance() {
        if (notificationCenterView == null) {
            synchronized (NotificationCenterView.class) {
                if (notificationCenterView == null) {
                    notificationCenterView = new NotificationCenterView();
                }
            }
        }
        return notificationCenterView;
    }

    private NotificationCenterView() {
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        initView();
    }

    @SuppressLint({"InflateParams", "ClickableViewAccessibility"})
    private void initView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        layoutParams = new WindowManager.LayoutParams(2071);
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | // 使窗口全屏
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | // 在屏幕内布局
//                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | // 允许在窗口之外的区域接收触摸事件
//                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | // 允许窗口超出屏幕边界
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR | // 在窗口装饰区域内布局
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED; // 启用硬件加速
        layoutParams.systemUiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        LayoutInflater lf = AAOP_HSkin.getLayoutInflater(SystemUIApplication.getSystemUIContext());
        mRootView = lf.inflate(R.layout.view_notification_center, null);

        gson = new Gson();
        spHelper = new SPHelper(SystemUIApplication.getSystemUIContext(), NOTIFY_SERVICE);

        llMessage = mRootView.findViewById(R.id.ll_message);
        llMessageList = mRootView.findViewById(R.id.ll_message_list);
        llMessageDetail = mRootView.findViewById(R.id.ll_message_detail);
        llMessageCenter = mRootView.findViewById(R.id.ll_nessage_center);
        tvDetailTitle = mRootView.findViewById(R.id.tv_detail_title);
        tvDetailTime = mRootView.findViewById(R.id.tv_detail_time);
        tvDetailContent = mRootView.findViewById(R.id.tv_detail_content);
        tvGoToTrafficReCharge = mRootView.findViewById(R.id.tv_go_to_traffic_recharge);
        tvReadAll = mRootView.findViewById(R.id.tv_read_all);
        ivVoice = mRootView.findViewById(R.id.iv_voice);
        ivBack = mRootView.findViewById(R.id.iv_back);
        ivNewPage = mRootView.findViewById(R.id.iv_new_page);
        llBtn = mRootView.findViewById(R.id.ll_btn);
        ivEdit = mRootView.findViewById(R.id.iv_edit);
        tvComplete = mRootView.findViewById(R.id.tv_complete);
        tvSelectAll = mRootView.findViewById(R.id.tv_sel_all);
        tvDelete = mRootView.findViewById(R.id.tv_delete);
        ivClose = mRootView.findViewById(R.id.iv_close);
        tvSystemMessage = mRootView.findViewById(R.id.tv_system_message);
        tvAppMessage = mRootView.findViewById(R.id.tv_app_message);
        viewPager2 = mRootView.findViewById(R.id.view_pager2);
        pageViews = new ArrayList<>();
        mSystemMessageLayout = lf.inflate(R.layout.layout_system_message, null);
        mWarningMessageLayout = lf.inflate(R.layout.layout_warning_message, null);

        setSystemMessageAdapter();
        setWarningMessageAdapter();

        llNoMessage = mSystemMessageLayout.findViewById(R.id.ll_no_message);
        llNoWarningMessage = mWarningMessageLayout.findViewById(R.id.ll_no_warning_message);
        pageViews.add(mSystemMessageLayout);
        pageViews.add(mWarningMessageLayout);
        pageAdapter = new NotificationCenterPageAdapter(pageViews, SystemUIApplication.getSystemUIContext());
        viewPager2.setAdapter(pageAdapter);
        if (!TextUtils.isEmpty(messageType)) {
            if (messageType.equals("0")) {
                viewPager2.setCurrentItem(0);
                tvSystemMessage.setAlpha(1);
                tvAppMessage.setAlpha(0.7f);
            } else {
                viewPager2.setCurrentItem(1);
                tvSystemMessage.setAlpha(0.7f);
                tvAppMessage.setAlpha(1);
            }
        } else {
            viewPager2.setCurrentItem(0);
        }
        viewPager2.setNoScroll(true);
        tvSystemMessage.setOnClickListener(this);
        tvAppMessage.setOnClickListener(this);
        ivClose.setOnClickListener(this);
//        mRootView.setOnClickListener(v -> dismiss());
        ivEdit.setOnClickListener(this);
        tvComplete.setOnClickListener(this);
        tvSelectAll.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivVoice.setOnClickListener(this);
        tvReadAll.setOnClickListener(this);
        tvGoToTrafficReCharge.setOnClickListener(this);

        viewPager2.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    upDateSystemMessageUI();
                } else {
                    upDateWarningMessageUI();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        Log.i(TAG, "isSystemMessageAllRead1:" + spHelper.getBoolean("isSystemMessageAllRead") + "isWarningMessageAllRead1:" + spHelper.getBoolean("isWarningMessageAllRead"));

        if (spHelper.getBoolean("isSystemMessageAllRead") && spHelper.getBoolean("isWarningMessageAllRead")) {
            StatusBar.getInstance().messageIconSetSelected(false);
        }

        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        llMessageCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        llMessageDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        if (!isAdded && null != mWindowManager) {
            if (null != mRootView) {
                mWindowManager.addView(mRootView, layoutParams);
                AAOP_HSkin.getWindowViewManager().addWindowView(mRootView).applySkinForViews(true);
            }
            isAdded = true;
        }

    }

    private void setSystemMessageAdapter() {
        selectNotificationList = new ArrayList<>();
        rvSystemMessage = mSystemMessageLayout.findViewById(R.id.rv_system_message);
        getData();
        messageAdapter = new SystemMessageAdapter(systemMessageInfoList, SystemUIApplication.getSystemUIContext());
        rvSystemMessage.setLayoutManager(new LinearLayoutManager(SystemUIApplication.getSystemUIContext(), LinearLayoutManager.VERTICAL, false));
        rvSystemMessage.setAdapter(messageAdapter);
        messageAdapter.setOnCheckedListener(new SystemMessageAdapter.OnCheckedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChecked(int pos, List<SystemMessageInfo> list) {
                selectNotificationList.clear();
                for (SystemMessageInfo systemMessageInfo : list) {
                    if (systemMessageInfo.isChecked()) {
                        selectNotificationList.add(systemMessageInfo);
                    }
                }
                if (selectNotificationList.size() > 0) {
                    tvDelete.setAlpha(1);
                    tvDelete.setClickable(true);
                } else {
                    tvDelete.setAlpha(0.3f);
                    tvDelete.setClickable(false);
                }
//                tvSelectedNum.setText("已选择" + selectNotificationList.size() + "项");
            }
        });

        messageAdapter.setOnItemClickListener(new SystemMessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                if (!TextUtils.isEmpty(systemMessageInfoList.get(pos).getGpsName())) {
                    Intent intent = new Intent(REQUEST_GUIDE);
                    intent.putExtra("poiName", systemMessageInfoList.get(pos).getGpsName());
                    intent.putExtra("longitude", systemMessageInfoList.get(pos).getLongitude());
                    intent.putExtra("latitude", systemMessageInfoList.get(pos).getLatitude());
                    SystemUIApplication.getSystemUIContext().sendBroadcast(intent);
                } else {
                    llMessageList.setVisibility(View.GONE);
                    llMessageDetail.setVisibility(View.VISIBLE);
                    systemMessageInfoList.get(pos).setRead(true);
                    messageAdapter.setNotificationInfoList(systemMessageInfoList);
                    tvDetailTitle.setText(systemMessageInfoList.get(pos).getTitle());
                }
            }
        });

        SwipeDeleteCallBack callBack = new SwipeDeleteCallBack(messageAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callBack);
        itemTouchHelper.attachToRecyclerView(rvSystemMessage);

    }

    private void setWarningMessageAdapter() {
        selectWarningList = new ArrayList<>();
        rvWarningMessage = mWarningMessageLayout.findViewById(R.id.rv_warning_message);

        warningMessageAdapter = new WarningMessageAdapter(warningMessageInfoList, SystemUIApplication.getSystemUIContext());
        rvWarningMessage.setLayoutManager(new LinearLayoutManager(SystemUIApplication.getSystemUIContext(), LinearLayoutManager.VERTICAL, false));
        rvWarningMessage.setAdapter(warningMessageAdapter);
        warningMessageAdapter.setOnCheckedListener(new WarningMessageAdapter.OnCheckedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChecked(int pos, List<WarningMessageInfo> list) {
                selectWarningList.clear();
                for (WarningMessageInfo warningMessageInfo : list) {
                    if (warningMessageInfo.isChecked()) {
                        selectWarningList.add(warningMessageInfo);
                    }
                }
                if (selectWarningList.size() > 0) {
                    tvDelete.setAlpha(1);
                    tvDelete.setClickable(true);
                } else {
                    tvDelete.setAlpha(0.3f);
                    tvDelete.setClickable(false);
                }
//                tvSelectedNum.setText("已选择" + selectWarningList.size() + "项");
            }
        });

        warningMessageAdapter.setOnItemClickListener(new WarningMessageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                llMessageList.setVisibility(View.GONE);
                llMessageDetail.setVisibility(View.VISIBLE);
                warningMessageInfoList.get(pos).setRead(true);
                warningMessageAdapter.setWarningMessageInfoList(warningMessageInfoList);
            }
        });
        //告警消息不支持主动删除
//        SwipeDeleteWarningCallBack callBack = new SwipeDeleteWarningCallBack(warningMessageAdapter);
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callBack);
//        itemTouchHelper.attachToRecyclerView(rvWarningMessage);

    }

    private void getData() {
        systemMessageInfoList = new ArrayList<>();
        String sss = spHelper.getString("systemMessageInfoJson");
        if (!TextUtils.isEmpty(sss)) {
            systemMessageInfoList.addAll(gson.fromJson(sss, new TypeToken<List<SystemMessageInfo>>() {
            }.getType()));
//            String newMessageInfoList = gson.toJson(systemMessageInfoList);
        }
        //添加测试假消息
//        for (int i = 0; i < 5; i++) {
//            SystemMessageInfo systemMessageInfo = new SystemMessageInfo();
//            systemMessageInfo.setChecked(false);
//            systemMessageInfo.setId(i);
//            systemMessageInfo.setTitle("消息标题");
//            systemMessageInfo.setMessage("消息内容");
//            systemMessageInfoList.add(systemMessageInfo);
//        }
    }

    /**
     * The type Refresh ui receiver.
     */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshSystemMessageEvent data) {
        Log.i(TAG, "RefreshSystemMessageEvent");
        upDateSystemMessageUI();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(RefreshWarningMessageEvent data) {
        upDateWarningMessageUI();
    }


//    public class RefreshUIReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.i(TAG, "onReceive");
//            if (intent.getAction().equals("refresh_system_message")) {
//                Log.i(TAG, "upDateSystemMessageUI");
//                upDateSystemMessageUI();
//            }
//            if (intent.getAction().equals("refresh_warning_message")) {
//                Log.i(TAG, "upDateWarningMessageUI");
//                upDateWarningMessageUI();
//            }
//        }
//    }

    private void upDateSystemMessageUI() {
        Log.i(TAG, "upDateSystemMessageUI");
        isSystemMessageNoData();
    }

    private void upDateWarningMessageUI() {
        isWarningMessageNoData();
    }

    private void isSystemMessageNoData() {
        if (systemMessageInfoList.size() == 0) {
            Log.i(TAG, "isSystemMessageNoData");
            llNoMessage.setVisibility(View.VISIBLE);
            rvSystemMessage.setVisibility(View.GONE);
            ivEdit.setVisibility(View.GONE);
            tvReadAll.setVisibility(View.GONE);
            ivNewPage.setVisibility(View.GONE);
        } else {
            llNoMessage.setVisibility(View.GONE);
            rvSystemMessage.setVisibility(View.VISIBLE);
            ivEdit.setVisibility(View.VISIBLE);
            tvReadAll.setVisibility(View.VISIBLE);
            Log.i(TAG, "systemMessageInfoList:isSystemMessageNoData  " + gson.toJson(systemMessageInfoList));
            for (SystemMessageInfo systemMessageInfo : systemMessageInfoList) {
                if (systemMessageInfo.isRead()) {
                    tvReadAll.setAlpha(0.3f);
                } else {
                    tvReadAll.setAlpha(0.7f);
                }
            }
            //todo 全部已读有问题
//            if ()
            ivNewPage.setVisibility(View.VISIBLE);
            messageAdapter.setNotificationInfoList(systemMessageInfoList);
//            Log.i(TAG,"systemMessageInfoList" + gson.toJson(systemMessageInfoList));
        }

    }

    private void isWarningMessageNoData() {
        ivEdit.setVisibility(View.GONE);
        if (warningMessageInfoList.size() == 0) {
            llNoWarningMessage.setVisibility(View.VISIBLE);
            if (rvWarningMessage != null) {
                rvWarningMessage.setVisibility(View.GONE);
            }
//            ivEdit.setVisibility(View.GONE);
            tvReadAll.setVisibility(View.GONE);
            ivNewPage.setVisibility(View.GONE);
        } else {
            llNoWarningMessage.setVisibility(View.GONE);
            if (rvWarningMessage != null) {
                rvWarningMessage.setVisibility(View.VISIBLE);
            }
//            ivEdit.setVisibility(View.VISIBLE);
            for (WarningMessageInfo warningMessageInfo : warningMessageInfoList) {
                if (!warningMessageInfo.isRead()) {
                    tvReadAll.setAlpha(0.7f);
                } else {
                    tvReadAll.setAlpha(0.3f);
                }
            }
            tvReadAll.setVisibility(View.VISIBLE);
            ivNewPage.setVisibility(View.VISIBLE);
            warningMessageAdapter.setWarningMessageInfoList(warningMessageInfoList);
        }
    }

    private void isWarningMessageStatus() {
        if (warningMessageInfoList.size() == 0) {
            llNoWarningMessage.setVisibility(View.VISIBLE);
            if (rvWarningMessage != null) {
                rvWarningMessage.setVisibility(View.GONE);
            }
            ivNewPage.setVisibility(View.GONE);
        } else {
            llNoWarningMessage.setVisibility(View.GONE);
            if (rvWarningMessage != null) {
                rvWarningMessage.setVisibility(View.VISIBLE);
            }
            ivNewPage.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 添加告警消息.
     *
     * @param warningMessageInfos 告警消息消息体
     */
    public void addWarningMessage(List<WarningMessageInfo> warningMessageInfos) {
        warningMessageInfoList = warningMessageInfos;
        warningMessageAdapter.setWarningMessageInfoList(warningMessageInfoList);
        upDateWarningMessageUI();
        if (null != mRootView && mRootView.getVisibility() != View.GONE) {
            mRootView.setVisibility(View.GONE);
        }
    }


    /**
     * 判断是否显示.
     *
     * @param showDetail   显示详情
     * @param mMessageType 消息类型
     * @return the boolean
     */
    public boolean show(boolean showDetail, String mMessageType) {
        TrackingMessageDataUtil.getInstance().trackingData(TrackingEnum.MESSAGE_INFO.getBehaviorid());

        String ss = spHelper.getString("systemMessageInfoJson");
        Log.i(TAG, "systemMessageInfoJson33" + ss);

//        refreshUIReceiver = new RefreshUIReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("refresh_system_message");
//        intentFilter.addAction("refresh_warning_message");
//        SystemUIApplication.getSystemUIContext().registerReceiver(refreshUIReceiver, intentFilter);
        isRefreshUIReceiverRegistered = true;
        messageType = mMessageType;
        if (null != mRootView && mRootView.getVisibility() != View.VISIBLE) {
            mRootView.setVisibility(View.VISIBLE);
        }

        if (messageType.equals("0")) {
            viewPager2.setCurrentItem(0);
            tvSystemMessage.setAlpha(1);
            tvAppMessage.setAlpha(0.7f);
        } else {
            viewPager2.setCurrentItem(1);
            tvSystemMessage.setAlpha(0.7f);
            tvAppMessage.setAlpha(1);
        }

        if (viewPager2.getCurrentItem() == 0) {
            if (messageAdapter != null) {
                systemMessageInfoList.clear();
//                String ss = spHelper.getString("systemMessageInfoJson");
//                Log.i(TAG, "systemMessageInfoJson33" + ss);
                if (gson.fromJson(spHelper.getString("systemMessageInfoJson"), new TypeToken<List<SystemMessageInfo>>() {
                }.getType()) != null) {
                    systemMessageInfoList.addAll(gson.fromJson(spHelper.getString("systemMessageInfoJson"), new TypeToken<List<SystemMessageInfo>>() {
                    }.getType()));
                    String newMessageInfoList = gson.toJson(systemMessageInfoList);
                    Log.i(TAG, "newMessageInfoList" + newMessageInfoList);
                }
                messageAdapter.setNotificationInfoList(systemMessageInfoList);
                upDateSystemMessageUI();
            }
        } else {
            if (warningMessageAdapter != null) {
                warningMessageAdapter.setWarningMessageInfoList(warningMessageInfoList);
                upDateWarningMessageUI();
            }

            ivEdit.setVisibility(View.GONE);
            tvComplete.setVisibility(View.GONE);

        }
        if (!showDetail) {
            llMessageList.setVisibility(View.VISIBLE);
            llMessageDetail.setVisibility(View.GONE);
        } else {
            if (viewPager2.getCurrentItem() == 0) {
                SystemMessageInfo systemMessageInfo = new SystemMessageInfo();
                systemMessageInfo = gson.fromJson(spHelper.getString("messageDetailJson"), new TypeToken<SystemMessageInfo>() {
                }.getType());
                llMessageList.setVisibility(View.GONE);
                llMessageDetail.setVisibility(View.VISIBLE);
                tvDetailTitle.setText(systemMessageInfo.getTitle());
                tvDetailContent.setText(systemMessageInfo.getMessage());
                //todo 还有别的字段待对应
            } else {
                WarningMessageInfo warningMessageInfo = new WarningMessageInfo();
                llMessageList.setVisibility(View.GONE);
                llMessageDetail.setVisibility(View.VISIBLE);
//                tvDetailTitle.setText(systemMessageInfo.getTitle());
                tvDetailContent.setText(warningMessageInfo.getMessage());
            }
        }

        return true;

    }


    /**
     * Dismiss.
     */
    public void dismiss() {
        if (null != mRootView && mRootView.getVisibility() != View.GONE) {
            mRootView.setVisibility(View.GONE);
        }
//        if (isRefreshUIReceiverRegistered) {
//            SystemUIApplication.getSystemUIContext().unregisterReceiver(refreshUIReceiver);
//        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        llMessageList.setVisibility(View.VISIBLE);
        llMessageDetail.setVisibility(View.GONE);
        isRefreshUIReceiverRegistered = false;
        Log.i(TAG, "isSystemMessageAllRead2:" + spHelper.getBoolean("isSystemMessageAllRead") + "isWarningMessageAllRead2:" + spHelper.getBoolean("isWarningMessageAllRead"));
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_system_message:
                viewPager2.setCurrentItem(0);
                tvSystemMessage.setAlpha(1);
                tvAppMessage.setAlpha(0.7f);
                break;
            case R.id.tv_app_message:
                viewPager2.setCurrentItem(1);
                tvSystemMessage.setAlpha(0.7f);
                tvAppMessage.setAlpha(1);
                break;
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.iv_edit:
                tvComplete.setVisibility(View.VISIBLE);
                ivEdit.setVisibility(View.GONE);
                tvReadAll.setVisibility(View.GONE);
                ivNewPage.setVisibility(View.GONE);
                llMessage.setVisibility(View.GONE);
                llBtn.setVisibility(View.VISIBLE);
                if (viewPager2.getCurrentItem() == 0) {
                    messageAdapter.setCheckedBtnShow(true);
                } else {
                    warningMessageAdapter.setCheckedBtnShow(true);
                }
                break;
            case R.id.tv_complete:
                tvComplete.setVisibility(View.GONE);
                ivEdit.setVisibility(View.VISIBLE);
                tvReadAll.setVisibility(View.VISIBLE);
                ivNewPage.setVisibility(View.VISIBLE);
                llMessage.setVisibility(View.VISIBLE);
                llBtn.setVisibility(View.GONE);
                if (viewPager2.getCurrentItem() == 0) {
                    upDateSystemMessageUI();
                    messageAdapter.setCheckedBtnShow(false);
                } else {
                    warningMessageAdapter.setCheckedBtnShow(false);
                    upDateWarningMessageUI();
                }

                break;
            case R.id.tv_sel_all:
                if (viewPager2.getCurrentItem() == 0) {
                    selectNotificationList.clear();
                    for (SystemMessageInfo systemMessageInfo : systemMessageInfoList) {
                        systemMessageInfo.setChecked(true);
                        selectNotificationList.add(systemMessageInfo);
                    }

                    if (selectNotificationList.size() > 0) {
                        tvDelete.setAlpha(1);
                        tvDelete.setClickable(true);
                    } else {
                        tvDelete.setAlpha(0.3f);
                        tvDelete.setClickable(false);
                    }
                    messageAdapter.setNotificationInfoList(systemMessageInfoList);
                } else {
                    selectWarningList.clear();
                    for (WarningMessageInfo warningMessageInfo : warningMessageInfoList) {
                        warningMessageInfo.setChecked(true);
                        selectWarningList.add(warningMessageInfo);
                    }

                    if (selectWarningList.size() > 0) {
                        tvDelete.setAlpha(1);
                        tvDelete.setClickable(true);
                    } else {
                        tvDelete.setAlpha(0.3f);
                        tvDelete.setClickable(false);
                    }
                    warningMessageAdapter.setWarningMessageInfoList(warningMessageInfoList);
                }

                break;
            case R.id.tv_delete:
                if (viewPager2.getCurrentItem() == 0) {
                    if (systemMessageInfoList.size() > 0) {
                        tvDelete.setAlpha(1);
                        tvDelete.setClickable(true);
                        showSystemMessageDelDialog();
                    } else {
                        tvDelete.setAlpha(0.3f);
                        tvDelete.setClickable(false);
                    }
                } else {
                    if (warningMessageInfoList.size() > 0) {
                        tvDelete.setAlpha(1);
                        tvDelete.setClickable(true);
                        showWarningMessageDelDialog();
                    } else {
                        tvDelete.setAlpha(0.3f);
                        tvDelete.setClickable(false);
                    }
                }

                break;
            case R.id.iv_back:
                //todo 如果全都已读，要置灰
                if (messageType.equals("0")) {
                    viewPager2.setCurrentItem(0);
                    tvSystemMessage.setAlpha(1);
                    tvAppMessage.setAlpha(0.7f);
                } else {
                    viewPager2.setCurrentItem(1);
                    tvSystemMessage.setAlpha(0.7f);
                    tvAppMessage.setAlpha(1);
                }
                if (viewPager2.getCurrentItem() == 0) {
                    isSystemMessageNoData();
                } else {
                    isWarningMessageNoData();
                }
                llMessageList.setVisibility(View.VISIBLE);
                llMessageDetail.setVisibility(View.GONE);
                break;
            case R.id.iv_voice:
                NotifyVoiceManager.getInstance().readMessage(tvDetailContent.getText().toString());
                break;
            case R.id.tv_read_all:
                if (viewPager2.getCurrentItem() == 0) {
                    for (int i = 0; i < systemMessageInfoList.size(); i++) {
                        systemMessageInfoList.get(i).setRead(true);
                    }
                    Log.i(TAG, "systemMessageInfoList" + gson.toJson(systemMessageInfoList));
                    messageAdapter.setNotificationInfoList(systemMessageInfoList);
                    String systemMessageInfoJson = gson.toJson(systemMessageInfoList);
                    spHelper.putValues(new SPHelper.ContentValue("systemMessageInfoJson", systemMessageInfoJson));

                    tvReadAll.setAlpha(0.3f);
                } else {
                    for (int i = 0; i < warningMessageInfoList.size(); i++) {
                        warningMessageInfoList.get(i).setRead(true);
                    }
                    warningMessageAdapter.setWarningMessageInfoList(warningMessageInfoList);
                    tvReadAll.setAlpha(0.3f);
                }
                break;
            case R.id.tv_go_to_traffic_recharge:
                //去充值流量

                break;
        }
    }

    private void showSystemMessageDelDialog() {
        String title = "";
        if (selectNotificationList.size() == systemMessageInfoList.size()) {
            title = SystemUIApplication.getSystemUIContext().getString(R.string.sure_delete_all_message) + "?";
        } else {
            title = SystemUIApplication.getSystemUIContext().getString(R.string.sure)
                    + SystemUIApplication.getSystemUIContext().getString(R.string.delete)
                    + selectNotificationList.size()
                    + SystemUIApplication.getSystemUIContext().getString(R.string.num)
                    + SystemUIApplication.getSystemUIContext().getString(R.string.message) + "?";
        }

        CommonDialog.createDialog(SystemUIApplication.getSystemUIContext(), R.style.NotTransparentStyle).setTitleName(title).setTips("").setButtonOne("删除", ResourceUtil.getDrawableId(SystemUIApplication.getSystemUIContext(), DEL_BG), new CommonDialog.OnButtonClickListener() {
            @Override
            public void onButtonClick(CommonDialog commonDialog) {
                systemMessageInfoList.removeIf(SystemMessageInfo::isChecked);
                messageAdapter.setNotificationInfoList(systemMessageInfoList);
                upDateSystemMessageUI();
                String systemMessageInfoListJson = gson.toJson(systemMessageInfoList);
                spHelper.putValues(new SPHelper.ContentValue("systemMessageInfoJson", systemMessageInfoListJson));
                selectNotificationList.clear();
                for (SystemMessageInfo systemMessageInfo : systemMessageInfoList) {
                    systemMessageInfo.setChecked(true);
                    selectNotificationList.add(systemMessageInfo);
                }

                if (selectNotificationList.size() > 0) {
                    tvDelete.setAlpha(1);
                    tvDelete.setClickable(true);
                } else {
                    tvDelete.setAlpha(0.3f);
                    tvDelete.setClickable(false);
                    spHelper.putValues(new SPHelper.ContentValue("itemId", 0));
                }
            }
        }).setButtonTwo(SystemUIApplication.getSystemUIContext().getString(R.string.cancel_text), ResourceUtil.getDrawableId(SystemUIApplication.getSystemUIContext(), CANCEL_BG), null).show();
    }

    private void showWarningMessageDelDialog() {
        String title = "";
        if (selectWarningList.size() == systemMessageInfoList.size()) {
            title = SystemUIApplication.getSystemUIContext().getString(R.string.sure_delete_all_message) + "?";
        } else {
            title = SystemUIApplication.getSystemUIContext().getString(R.string.sure)
                    + SystemUIApplication.getSystemUIContext().getString(R.string.delete)
                    + selectWarningList.size()
                    + SystemUIApplication.getSystemUIContext().getString(R.string.num)
                    + SystemUIApplication.getSystemUIContext().getString(R.string.message) + "?";
        }

        CommonDialog.createDialog(SystemUIApplication.getSystemUIContext(), R.style.NotTransparentStyle).setTitleName(title).setTips("").setButtonOne("删除", ResourceUtil.getDrawableId(SystemUIApplication.getSystemUIContext(), DEL_BG), new CommonDialog.OnButtonClickListener() {
            @Override
            public void onButtonClick(CommonDialog commonDialog) {
                TrackingMessageDataUtil.getInstance().trackingData(TrackingEnum.MESSAGE_DEL.getBehaviorid());
                warningMessageInfoList.removeIf(WarningMessageInfo::isChecked);
                isWarningMessageStatus();
                Log.i(TAG, "warningMessageInfoList" + gson.toJson(warningMessageInfoList));
                warningMessageAdapter.setWarningMessageInfoList(warningMessageInfoList);
                selectWarningList.clear();
                for (WarningMessageInfo warningMessageInfo : warningMessageInfoList) {
                    warningMessageInfo.setChecked(true);
                    selectWarningList.add(warningMessageInfo);
                }

                if (selectWarningList.size() > 0) {
                    tvDelete.setAlpha(1);
                    tvDelete.setClickable(true);
                } else {
                    tvDelete.setAlpha(0.3f);
                    tvDelete.setClickable(false);
                    spHelper.putValues(new SPHelper.ContentValue("itemId", 0));
                }
            }
        }).setButtonTwo(SystemUIApplication.getSystemUIContext().getString(R.string.cancel_text), ResourceUtil.getDrawableId(SystemUIApplication.getSystemUIContext(), CANCEL_BG), null).show();
    }
}
