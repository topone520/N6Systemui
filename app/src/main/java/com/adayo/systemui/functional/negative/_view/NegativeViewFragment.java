package com.adayo.systemui.functional.negative._view;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.message.MessageAction;
import com.adayo.soavb.foundation.view.AbstractBindViewDialog;
import com.adayo.soavb.foundation.view.AbstractBindViewFramelayout;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.contents.VehicleS0AConstant;
import com.adayo.systemui.eventbus.EventContents;
import com.adayo.systemui.eventbus.EventData;
import com.adayo.systemui.functional.negative._binder.Negative360ViewBinder;
import com.adayo.systemui.functional.negative._binder.NegativeBrightnessViewBinder;
import com.adayo.systemui.functional.negative._binder.NegativeProfileViewBinder;
import com.adayo.systemui.functional.negative._binder.NegativeScreenViewBinder;
import com.adayo.systemui.functional.negative._binder.NegativeTailgateViewBinder;
import com.adayo.systemui.functional.negative._binder.NegativeVolumeViewBinder;
import com.adayo.systemui.functional.negative._binder.SeatAdjustViewBinder;
import com.adayo.systemui.functional.negative._binder.SeatHeatViewBinder;
import com.adayo.systemui.functional.negative._binder.SeatMassageViewBinder;
import com.adayo.systemui.functional.negative._binder.SeatVentilateViewBinder;
import com.adayo.systemui.functional.negative._binder.SteeringViewBinder;
import com.adayo.systemui.functional.negative._messageAction.MessageActions;
import com.adayo.systemui.functional.negative._view.dialog.NegativeCleanDialog;
import com.adayo.systemui.functional.negative._view.dialog.PassengerSeatDialog;
import com.adayo.systemui.functional.negative._view.dialog.SeatMassageDialog;
import com.adayo.systemui.functional.seat._view.dialog.FrontRowSeatAdjustDialogBindView;
import com.adayo.systemui.manager.ReportBcmManager;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.proxy.vehicle.VehicleDoorService;
import com.adayo.systemui.proxy.vehicle.VehicleDriverService;
import com.adayo.systemui.proxy.vehicle.VehicleLightService;
import com.adayo.systemui.proxy.vehicle.VehicleWindowsService;
import com.adayo.systemui.utils.SeatResourceUtils;
import com.adayo.systemui.windows.dialogs.BindViewBaseDialog;
import com.adayo.systemui.windows.panels.QsViewPanel;
import com.adayo.systemui.windows.views.PullDownDumperLayout;
import com.adayo.systemui.windows.views.QsIconView;
import com.android.systemui.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class NegativeViewFragment extends AbstractBindViewFramelayout {
    private static final String TAG = NegativeViewFragment.class.getSimpleName();
    private RelativeLayout mQsLayout, mRlCard;
    private ConstraintLayout mClEditCard, mClHost, mClSeekbar, mClAssistant;
    private Button mBtEnter;
    private BindViewBaseDialog _front_row_seat_adjust_dialog;
    private AbstractBindViewDialog _negative_clean_dialog, _seat_massage_dialog, _passenger_seat_dialog;

    private QsIconView mDriverWind, mDriverHeat, mDriverWheel, mSeatMassage, mQsHud,
            mQsEsp, mQsClean, mQsDark, mQsLamp, mQsTailgate, mQs360, mQsScreen,
            mPassengerHeat, mPassengerWind, mPassengerMassage, mPassengerAdjust ;
    private QsIconView mQsRear, mQsPedestrian, mQsSlope, mQsRadar, mQsAtmosphere,
            mQsLock, mQsChild, mQsSkylight, mQsSunroof, mQsCruise, mQsWarning,
            mQsAssist, mQsProtection, mQsCollision, mQsPrevention;
    private RelativeLayout mProfileCardLayout;
    private TextView mProfileCard;
    private static final int MODE_OPEN = 1;
    //0关
    private static final int MODE_CLOSE = 0;
    private static final int DARK_MODE_OPEN = 3;
    private ImageView mIconProfileEnable;

    public NegativeViewFragment(@NonNull Context context) {
        super(context);
        _init_view();
        _init_data();
    }

    public NegativeViewFragment(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _init_view();
        _init_data();
    }

    private void _init_data() {
        mQsHud.injectAction(MessageActions.getInstance().getMessageAction(VehicleDriverService.getInstance(),VehicleS0AConstant.MSG_GET_HUD_STATE,VehicleS0AConstant.MSG_SET_HUD_STATE,VehicleS0AConstant.MSG_EVENT_HUD_STATE));
        mQsEsp.injectAction(MessageActions.getInstance().getMessageAction(VehicleDriverService.getInstance(),VehicleS0AConstant.MSG_GET_ESP_STATE,VehicleS0AConstant.MSG_SET_ESP_STATE,VehicleS0AConstant.MSG_EVENT_ESP_STATE));
        mQsClean.injectAction(MessageActions.getInstance().getMessageAction(null,"","",""));
        mQsDark.injectAction(MessageActions.getInstance().getMessageAction(null,"","",""));
        mQsLamp.injectAction(MessageActions.getInstance().getMessageAction(VehicleLightService.getInstance(),VehicleS0AConstant.MSG_GET_HIGH_BEAM_LIGHTS,VehicleS0AConstant.MSG_SET_HIGH_BEAM_LIGHTS,VehicleS0AConstant.MSG_EVENT_HIGH_BEAM_LIGHTS));

        mQsRear.injectAction(MessageActions.getInstance().getMessageAction(VehicleDoorService.getInstance(),VehicleS0AConstant.MSG_GET_RE_OPER_PAN_LOCK_CMD,VehicleS0AConstant.MSG_SET_RE_OPER_PAN_LOCK_CMD,VehicleS0AConstant.MSG_EVENT_RE_OPER_PAN_LOCK_CMD));
        mQsPedestrian.injectAction(MessageActions.getInstance().getMessageAction(VehicleDoorService.getInstance(),VehicleS0AConstant.MSG_GET_ALERT_SOUND,VehicleS0AConstant.MSG_SET_ALERT_SOUND,VehicleS0AConstant.MSG_EVENT_ALERT_SOUND));
        mQsSlope.injectAction(MessageActions.getInstance().getMessageAction(VehicleDriverService.getInstance(),VehicleS0AConstant.MSG_GET_HDC_STATE,VehicleS0AConstant.MSG_SET_HDC_STATE,VehicleS0AConstant.MSG_EVENT_HDC_STATE));
        mQsRadar.injectAction(MessageActions.getInstance().getMessageAction(null,"","",""));
        mQsAtmosphere.injectAction(MessageActions.getInstance().getMessageAction(null,"","",""));
        mQsLock.injectAction(MessageActions.getInstance().getMessageAction(VehicleDoorService.getInstance(),VehicleS0AConstant.MSG_GET_ALL_LOCK,VehicleS0AConstant.MSG_SET_ALL_LOCK,VehicleS0AConstant.MSG_EVENT_ALL_LOCK));
        mQsChild.injectAction(MessageActions.getInstance().getMessageAction(VehicleDoorService.getInstance(),VehicleS0AConstant.MSG_GET_KID_LOCK_STATE,VehicleS0AConstant.MSG_SET_KID_LOCK_STATE,VehicleS0AConstant.MSG_EVENT_KID_LOCK_STATE));
        mQsSkylight.injectAction(MessageActions.getInstance().getMessageAction(VehicleWindowsService.getInstance(),VehicleS0AConstant.MSG_GET_SKY_WINDOW_HORIZONTAL_PROGRESS,VehicleS0AConstant.MSG_SET_SKY_WINDOW_HORIZONTAL_PROGRESS,VehicleS0AConstant.MSG_EVENT_SKY_WINDOW_HORIZONTAL_PROGRESS));
        mQsSunroof.injectAction(MessageActions.getInstance().getMessageAction(null,"","",""));
        mQsCruise.injectAction(MessageActions.getInstance().getMessageAction(null,"","",""));
        mQsWarning.injectAction(MessageActions.getInstance().getMessageAction(null,"","",""));
        mQsAssist.injectAction(MessageActions.getInstance().getMessageAction(null,"","",""));
        mQsProtection.injectAction(MessageActions.getInstance().getMessageAction(null,"","",""));
        mQsCollision.injectAction(MessageActions.getInstance().getMessageAction(null,"","",""));
        mQsPrevention.injectAction(MessageActions.getInstance().getMessageAction(null,"","",""));
    }


    private void _init_view() {
        EventBus.getDefault().register(this);
        mQsLayout = findViewById(R.id.qs_layout);
        mDriverWind = findViewById(R.id.driver_wind);
        mDriverHeat = findViewById(R.id.driver_heat);
        mDriverWheel = findViewById(R.id.driver_wheel);
        mSeatMassage = findViewById(R.id.seat_massage);
        mProfileCardLayout = findViewById(R.id.profile_card_layout);
        mProfileCard = findViewById(R.id.profile_card);
        mQsTailgate = findViewById(R.id.qs_tailgate);
        mQs360 = findViewById(R.id.qs_360);
        mQsScreen = findViewById(R.id.qs_screen);
        mPassengerHeat = findViewById(R.id.passenger_heat);
        mPassengerWind = findViewById(R.id.passenger_wind);
        mPassengerMassage = findViewById(R.id.passenger_massage);
        mPassengerAdjust = findViewById(R.id.passenger_adjust);

        mIconProfileEnable = findViewById(R.id.icon_profile_enable);


        mClEditCard = findViewById(R.id.cl_edit_card);
        mClHost = findViewById(R.id.cl_host);
        mClSeekbar = findViewById(R.id.cl_seekbar);
        mClAssistant = findViewById(R.id.cl_assistant);
        mRlCard = findViewById(R.id.rl_card);
        mBtEnter = findViewById(R.id.bt_enter);

        mQsHud = findViewById(R.id.qs_hud);
        mQsEsp = findViewById(R.id.qs_esp);
        mQsClean = findViewById(R.id.qs_clean);
        mQsDark = findViewById(R.id.qs_dark);
        mQsLamp = findViewById(R.id.qs_lamp);

        mQsRear = findViewById(R.id.qs_rear);
        mQsPedestrian = findViewById(R.id.qs_pedestrian);
        mQsSlope = findViewById(R.id.qs_slope);
        mQsRadar = findViewById(R.id.qs_radar);
        mQsAtmosphere = findViewById(R.id.qs_atmosphere);
        mQsLock = findViewById(R.id.qs_lock);
        mQsChild = findViewById(R.id.qs_child);
        mQsSkylight = findViewById(R.id.qs_skylight);
        mQsSunroof = findViewById(R.id.qs_sunroof);
        mQsCruise = findViewById(R.id.qs_cruise);
        mQsWarning = findViewById(R.id.qs_warning);
        mQsAssist = findViewById(R.id.qs_assist);
        mQsProtection = findViewById(R.id.qs_protection);
        mQsCollision = findViewById(R.id.qs_collision);
        mQsPrevention = findViewById(R.id.qs_prevention);



        setOnLongClickListener(() -> {
            AAOP_LogUtils.i(TAG, "onLongClickListener");
            if (mClEditCard.getVisibility() == View.GONE) {
                MessageActions.getInstance().setEditType(true);
                mClHost.setVisibility(View.GONE);
                mClSeekbar.setVisibility(View.GONE);
                mClAssistant.setVisibility(View.GONE);
                mClEditCard.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) mRlCard.getLayoutParams();
                layoutParams1.leftMargin += 2098;
                mRlCard.setLayoutParams(layoutParams1);
                PullDownDumperLayout.getInstance().setScrollEnabled(false);
                mIconProfileEnable.setVisibility(View.VISIBLE);
                mQsTailgate.setIconEnableVisibility(View.VISIBLE);
                mQs360.setIconEnableVisibility(View.VISIBLE);
                mQsScreen.setIconEnableVisibility(View.VISIBLE);
            }
        });

        mBtEnter.setOnClickListener(v -> {
            if (mClEditCard.getVisibility() == View.VISIBLE) {
                MessageActions.getInstance().setEditType(false);
                mClHost.setVisibility(View.VISIBLE);
                mClSeekbar.setVisibility(View.VISIBLE);
                mClAssistant.setVisibility(View.VISIBLE);
                mClEditCard.setVisibility(View.GONE);
                RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) mRlCard.getLayoutParams();
                layoutParams1.leftMargin -= 2098;
                mRlCard.setLayoutParams(layoutParams1);
                PullDownDumperLayout.getInstance().setScrollEnabled(true);
                ReportBcmManager.getInstance().sendNegativeReport("5004002", "Negativeonepage_Userdefined_functions", "修改自定义快捷键: ");
                mIconProfileEnable.setVisibility(View.GONE);
                mQsTailgate.setIconEnableVisibility(View.GONE);
                mQs360.setIconEnableVisibility(View.GONE);
                mQsScreen.setIconEnableVisibility(View.GONE);
            }
        });

        onViewClick(mQsHud);
        onViewClick(mQsEsp);
        onViewClick(mQsClean);
        onViewClick(mQsDark);
        onViewClick(mQsLamp);
        onViewClick(mQsRear);
        onViewClick(mQsPedestrian);
        onViewClick(mQsSlope);
        onViewClick(mQsRadar);
        onViewClick(mQsAtmosphere);
        onViewClick(mQsLock);
        onViewClick(mQsChild);
        onViewClick(mQsSkylight);
        onViewClick(mQsSunroof);
        onViewClick(mQsCruise);
        onViewClick(mQsWarning);
        onViewClick(mQsAssist);
        onViewClick(mQsProtection);
        onViewClick(mQsCollision);
        onViewClick(mQsPrevention);

        onViewLongClick(mQsHud);
        onViewLongClick(mQsEsp);
        onViewLongClick(mQsClean);
        onViewLongClick(mQsDark);
        onViewLongClick(mQsLamp);
        onViewLongClick(mQsRear);
        onViewLongClick(mQsPedestrian);
        onViewLongClick(mQsSlope);
        onViewLongClick(mQsRadar);
        onViewLongClick(mQsAtmosphere);
        onViewLongClick(mQsLock);
        onViewLongClick(mQsChild);
        onViewLongClick(mQsSkylight);
        onViewLongClick(mQsSunroof);
        onViewLongClick(mQsCruise);
        onViewLongClick(mQsWarning);
        onViewLongClick(mQsAssist);
        onViewLongClick(mQsProtection);
        onViewLongClick(mQsCollision);
        onViewLongClick(mQsPrevention);

        onViewDragClick(mQsHud);
        onViewDragClick(mQsEsp);
        onViewDragClick(mQsClean);
        onViewDragClick(mQsDark);
        onViewDragClick(mQsLamp);
        onViewDragClick(mQsRear);
        onViewDragClick(mQsPedestrian);
        onViewDragClick(mQsSlope);
        onViewDragClick(mQsRadar);
        onViewDragClick(mQsAtmosphere);
        onViewDragClick(mQsLock);
        onViewDragClick(mQsChild);
        onViewDragClick(mQsSkylight);
        onViewDragClick(mQsSunroof);
        onViewDragClick(mQsCruise);
        onViewDragClick(mQsWarning);
        onViewDragClick(mQsAssist);
        onViewDragClick(mQsProtection);
        onViewDragClick(mQsCollision);
        onViewDragClick(mQsPrevention);


    }

    private void onViewClick(QsIconView view) {
        view.setListener(v -> {
            if (!MessageActions.getInstance().isEditType()){
                view.getMessageAction().setValue(view.isSelected() ? MODE_CLOSE : MODE_OPEN);
                if (getContext().getResources().getString(R.string.screen_jijing).equals(view.getIconText())){
                    _negative_clean_dialog.show();
                    QsViewPanel.getInstance().closeView();
                }else if (getContext().getResources().getString(R.string.screen_dark_mode).equals(view.getIconText())){
                    int mode = AAOP_DeviceServiceManager.getInstance().getDayNightMode() == DARK_MODE_OPEN ? 2 : 3;
                    view.setSelected(mode == DARK_MODE_OPEN);
                    view.setTextColor(mode == DARK_MODE_OPEN);
                    AAOP_DeviceServiceManager.getInstance().setDayNightMode(mode);//1自动,2白天,3夜间
                }
            }
        });
    }

    @Override
    protected void createViewBinder() {
        //座椅dialog 绑定
        _init_dialog_layout();

        // 通风
        insertViewBinder(SeatVentilateViewBinder.Builder.createLeft(R.id.driver_wind, SeatResourceUtils.LEFT_SEAT_RESOURCES_VENTILATE));
        insertViewBinder(SeatVentilateViewBinder.Builder.createRight(R.id.passenger_wind, SeatResourceUtils.RIGHT_SEAT_RESOURCES_VENTILATE));
        // 座椅加热
        insertViewBinder(SeatHeatViewBinder.Builder.createLeft(R.id.driver_heat, SeatResourceUtils.LEFT_SEAT_RESOURCES_HEAT));
        insertViewBinder(SeatHeatViewBinder.Builder.createRight(R.id.passenger_heat, SeatResourceUtils.RIGHT_SEAT_RESOURCES_HEAT));
        // 座椅调节
        insertViewBinder(new SeatAdjustViewBinder(SeatSOAConstant.LEFT_FRONT, _front_row_seat_adjust_dialog));
        insertViewBinder(new SeatAdjustViewBinder(SeatSOAConstant.RIGHT_FRONT, _front_row_seat_adjust_dialog));
        // 方向盘加热
        insertViewBinder(new SteeringViewBinder());
        // 按摩
        insertViewBinder(SeatMassageViewBinder.Builder.createLeft(R.id.seat_massage, SeatResourceUtils.LEFT_SEAT_RESOURCES_MASSAGE, _seat_massage_dialog));
        insertViewBinder(SeatMassageViewBinder.Builder.createRight(R.id.passenger_massage, SeatResourceUtils.RIGHT_SEAT_RESOURCES_MASSAGE, _passenger_seat_dialog));
        // 亮度/音量
        insertViewBinder(new NegativeBrightnessViewBinder());
        insertViewBinder(new NegativeVolumeViewBinder());
        // 情景模式
        insertViewBinder(new NegativeProfileViewBinder());
        // 抬头显示
        //insertViewBinder(new NegativeHudViewBinder());
        // 车身稳定
        //insertViewBinder(new NegativeEspViewBinder());
        // 极净模式
        //insertViewBinder(new NegativeCleanViewBinder(_negative_clean_dialog));
        // 夜间模式
        //insertViewBinder(new NegativeDarkViewBinder());
        // 后尾门
        insertViewBinder(new NegativeTailgateViewBinder());
        // 全景影像
        insertViewBinder(new Negative360ViewBinder());
        // 智能大灯
        //insertViewBinder(new NegativeLampViewBinder());
        // 息屏
        insertViewBinder(new NegativeScreenViewBinder());

    }

    private void _init_dialog_layout() {
        // 座椅调节
        _front_row_seat_adjust_dialog = new FrontRowSeatAdjustDialogBindView(getContext(), 2480, 680);

        // 极静模式
        _negative_clean_dialog = new NegativeCleanDialog(getContext());
        // 右侧座椅按摩
        _seat_massage_dialog = new SeatMassageDialog(getContext());
        // 左侧座椅按摩
        _passenger_seat_dialog = new PassengerSeatDialog(getContext());

    }

    @Override
    protected int acquireResourceId() {
        return R.layout.dialog_negativescreen;
    }

    @Override
    public void dispatchMessage(Bundle bundle) {
        super.dispatchMessage(bundle);
        _negative_clean_dialog.dispatchMessage(bundle);
        _seat_massage_dialog.dispatchMessage(bundle);
        _passenger_seat_dialog.dispatchMessage(bundle);
    }

    public void initialize() {
        AAOP_LogUtils.i(TAG, "::initialize()");
        VehicleDoorService.getInstance().connect("Negative_DOOR", service -> {
            AAOP_LogUtils.i(TAG, "DOOR::onConnected()" + service.acquireName());
            service.subscribe(this::dispatchMessage);
        });

        VehicleDriverService.getInstance().connect("Negative_DRIVER", service -> {
            AAOP_LogUtils.i(TAG, "DRIVER::onConnected()" + service.acquireName());
            service.subscribe(this::dispatchMessage);
        });

        SeatService.getInstance().connect("Negative_SEAT", service -> {
            AAOP_LogUtils.i(TAG, "Seat::onConnected()" + service.acquireName());
            service.subscribe(this::dispatchMessage);
        });

        VehicleLightService.getInstance().connect("Negative_LIGHT", service -> {
            AAOP_LogUtils.i(TAG, "LIGHT::onConnected()" + service.acquireName());
            service.subscribe(this::dispatchMessage);
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData data) {
        AAOP_LogUtils.d(TAG, "ccp eventbus ->>" + data.getType());
        switch (data.getType()) {
            case EventContents.EVENT_HVAC_LAYOUT_CCP_71:
                requestFocusChange(View.FOCUS_UP);
                break;
            case EventContents.EVENT_HVAC_LAYOUT_CCP_81:
                requestFocusChange(View.FOCUS_DOWN);
                break;
            case EventContents.EVENT_HVAC_LAYOUT_CCP_91:
                requestFocusChange(View.FOCUS_LEFT);
                break;
            case EventContents.EVENT_HVAC_LAYOUT_CCP_101:
                requestFocusChange(View.FOCUS_RIGHT);
                break;
        }

    }

    private void requestFocusChange(int direction) {
        View focused = mQsLayout.findFocus();
        AAOP_LogUtils.d(TAG, "requestFocusChange: " + focused);
        if (null != focused) {
            //获取当前焦点View所指向的下一个焦点的View，direction指的是上下左右对应的常量
            View nextView = focused.focusSearch(direction);
            if (null != nextView) {
                //设置焦点迁移到目标View
                nextView.setFocusableInTouchMode(true);
                nextView.setFocusedByDefault(true);
                nextView.requestFocus(direction);
                AAOP_LogUtils.d(TAG, "requestFocusChange nextView: " + nextView);
            }
        } else {
            mDriverWind.setFocusableInTouchMode(true);
            mDriverWind.setFocusedByDefault(true);
            mDriverWind.requestFocus();
        }
    }

    public void onClick() {
        AAOP_LogUtils.d(TAG, "onClick: focusable: "+mProfileCardLayout.isFocused() );
        onFocused(mDriverWind);
        onFocused(mDriverHeat);
        onFocused(mDriverWheel);
        onFocused(mSeatMassage);
        onFocused(mQsHud);
        onFocused(mQsEsp);
        onFocused(mQsClean);
        onFocused(mQsDark);
        onFocused(mQsLamp);
        onFocused(mQsTailgate);
        onFocused(mQs360);
        onFocused(mQsScreen);
        onFocused(mPassengerHeat);
        onFocused(mPassengerWind);
        onFocused(mPassengerMassage);
        onFocused(mPassengerAdjust);
        onFocused(mDriverWind);
        onFocused(mDriverWind);
        onFocused(mDriverWind);
        onFocused(mDriverWind);
        onFocused(mDriverWind);
        if (mProfileCardLayout.isFocused()){
            mProfileCard.performClick();
        }

    }

    private void onFocused(QsIconView view) {
        if (view.isFocused()){
            view.onPerformClick();
        }
    }

    private MessageAction<Integer> editMessageAction;
    private void onViewLongClick(QsIconView view) {
        Log.d(TAG, "onViewLongClick  ");
        view.setLongListener(v -> {
            AAOP_LogUtils.d(TAG, "onViewLongClick  isEditType: "+MessageActions.getInstance().isEditType());
            if (MessageActions.getInstance().isEditType()) {
                Intent intent = new Intent();
                intent.putExtra("idView", view.getId());
                intent.putExtra("imageView", view.getIcon());
                intent.putExtra("textName", view.getIconText());
                editMessageAction = view.getMessageAction();
                AAOP_LogUtils.d(TAG, "onViewLongClick view.getIcon(): " + view.getIcon() + " ,view.getIconText(): " + view.getIconText()+" ,view.getId(): "+view.getId());
                ClipData dataItem = new ClipData("label", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, new ClipData.Item(intent));
                v.startDragAndDrop(dataItem, new View.DragShadowBuilder(v), null, View.DRAG_FLAG_GLOBAL);
            }
        });
    }

    private void onViewDragClick(QsIconView view) {
        view.setOnDragListener((v, dragevent) -> {
            if (MessageActions.getInstance().isEditType()) {
                if (dragevent.getAction() == DragEvent.ACTION_DROP) {
                    ClipData.Item itemAt = dragevent.getClipData().getItemAt(0);
                    int idView = itemAt.getIntent().getIntExtra("idView", 0);
                    int imageView = itemAt.getIntent().getIntExtra("imageView", 0);
                    String textName = itemAt.getIntent().getStringExtra("textName");

                    String iconText = view.getIconText();
                    int iconImage = view.getIcon();
                    MessageAction<Integer> messageAction = view.getMessageAction();

                    view.setIconImage(imageView);
                    view.setIconText(textName);
                    view.injectAction(editMessageAction);
                    AAOP_LogUtils.d(TAG, "onViewDragClick view.getId(): "+view.getId()+" ,"+idView);
                    if (idView == mQsHud.getId()){
                        upDataEditUI(mQsHud,iconImage,iconText,messageAction);
                    }else if (idView == mQsEsp.getId()){
                        upDataEditUI(mQsEsp,iconImage,iconText,messageAction);
                    }else if (idView == mQsClean.getId()){
                        upDataEditUI(mQsClean,iconImage,iconText,messageAction);
                    }else if (idView == mQsDark.getId()){
                        upDataEditUI(mQsDark,iconImage,iconText,messageAction);
                    }else if (idView == mQsLamp.getId()){
                        upDataEditUI(mQsLamp,iconImage,iconText,messageAction);
                    }else if (idView == mQsRear.getId()){
                        upDataEditUI(mQsRear,iconImage,iconText,messageAction);
                    }else if (idView == mQsPedestrian.getId()){
                        upDataEditUI(mQsPedestrian,iconImage,iconText,messageAction);
                    }else if (idView == mQsRadar.getId()){
                        upDataEditUI(mQsRadar,iconImage,iconText,messageAction);
                    }else if (idView == mQsAtmosphere.getId()){
                        upDataEditUI(mQsAtmosphere,iconImage,iconText,messageAction);
                    }else if (idView == mQsLock.getId()){
                        upDataEditUI(mQsLock,iconImage,iconText,messageAction);
                    }else if (idView == mQsChild.getId()){
                        upDataEditUI(mQsChild,iconImage,iconText,messageAction);
                    }else if (idView == mQsSkylight.getId()){
                        upDataEditUI(mQsSkylight,iconImage,iconText,messageAction);
                    }else if (idView == mQsSunroof.getId()){
                        upDataEditUI(mQsSunroof,iconImage,iconText,messageAction);
                    }else if (idView == mQsCruise.getId()){
                        upDataEditUI(mQsCruise,iconImage,iconText,messageAction);
                    }else if (idView == mQsWarning.getId()){
                        upDataEditUI(mQsWarning,iconImage,iconText,messageAction);
                    }else if (idView == mQsAssist.getId()){
                        upDataEditUI(mQsAssist,iconImage,iconText,messageAction);
                    }else if (idView == mQsProtection.getId()){
                        upDataEditUI(mQsProtection,iconImage,iconText,messageAction);
                    }else if (idView == mQsCollision.getId()){
                        upDataEditUI(mQsCollision,iconImage,iconText,messageAction);
                    }else if (idView == mQsPrevention.getId()){
                        upDataEditUI(mQsPrevention,iconImage,iconText,messageAction);
                    }
                    AAOP_LogUtils.d(TAG, "onViewDragClick imageView: " + imageView + " ,textName: " + textName);
                }
            }
            return true;
        });
    }

    public void upDataEditUI(QsIconView view, int iconImage,String iconText,MessageAction<Integer> messageAction){
        view.setIconImage(iconImage);
        view.setIconText(iconText);
        view.injectAction(messageAction);
        editMessageAction = null;
    }

    public OnLongListener mOnLongListener;

    public void setOnLongClickListener(OnLongListener onLongListener) {
        mOnLongListener = onLongListener;
    }

    public interface OnLongListener{
        void onLongListener();
    }
}
