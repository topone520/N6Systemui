package com.adayo.systemui.functional.hvac._view;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.view.AbstractBindViewFramelayout;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.eventbus.EventContents;
import com.adayo.systemui.eventbus.EventData;
import com.adayo.systemui.functional.hvac._binder.ACViewBinder;
import com.adayo.systemui.functional.hvac._binder.AUTOViewBinder;
import com.adayo.systemui.functional.hvac._binder.CirculateViewBinder;
import com.adayo.systemui.functional.hvac._binder.CocosMDVAdjustViewBinder;
import com.adayo.systemui.functional.hvac._binder.DefrostViewBinder;
import com.adayo.systemui.functional.hvac._binder.DirectViewBinder;
import com.adayo.systemui.functional.hvac._binder.ECOViewBinder;
import com.adayo.systemui.functional.hvac._binder.FaceViewBinder;
import com.adayo.systemui.functional.hvac._binder.FeetViewBinder;
import com.adayo.systemui.functional.hvac._binder.FrontDefrostViewBinder;
import com.adayo.systemui.functional.hvac._binder.HvacSwitchViewBinder;
import com.adayo.systemui.functional.hvac._binder.MDVAdjustViewBinder;
import com.adayo.systemui.functional.hvac._binder.SYNCViewBinder;
import com.adayo.systemui.functional.hvac._binder.TempAdjustViewBinder;
import com.adayo.systemui.functional.hvac._binder.WinSpeedViewBinder;
import com.adayo.systemui.functional.hvac._view.dialog.BackBlowingModeDialogBindView;
import com.adayo.systemui.functional.hvac._view.dialog.ForestDialog;
import com.adayo.systemui.functional.hvac._view.dialog.IntelligentAirDialogBindView;
import com.adayo.systemui.interfaces.HvacLayoutSwitchListener;
import com.adayo.systemui.proxy.vehicle.Dimensional;
import com.adayo.systemui.utils.ViewStyleUtils;
import com.adayo.systemui.windows.dialogs.BindViewBaseDialog;
import com.adayo.systemui.windows.panels.HvacPanel;
import com.adayo.systemui.windows.views.hvac.EasyPickerView;
import com.adayo.systemui.windows.views.hvac.HvacMDVShowView;
import com.adayo.systemui.windows.views.hvac.HvacSpeedSeekBar;
import com.adayo.systemui.windows.views.hvac.HvacWinSpeedAdjustView;
import com.adayo.systemui.windows.views.hvac.VerSelectedView;
import com.android.systemui.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

//public class HvacViewGroup extends AbstractBindViewFramelayout implements MDVAdjustViewBinder.MDVAdjustListener {
public class HvacViewGroup extends AbstractBindViewFramelayout {
    private final String TAG = HvacViewGroup.class.getSimpleName();
    private HvacLayoutSwitchListener listener;
    private TextView _tv_seat, _tv_fragrance;
    private EasyPickerView _driver_temp_select, _copilot_temp_select;
    private LinearLayout _ll_main_wind_direction, _ll_center_move, _ll_co_wind_direction;
    private HvacMDVShowView _mdv_show_view;
    private BindViewBaseDialog _blowing_mode_dialog, _intelligent_air_dialog;
    private ConstraintLayout _hvac_view_group;
    private HvacWinSpeedAdjustView _win_speed_adjust_view;
    private HvacSpeedSeekBar _speed_seekbar;
    private View ll_wind_driver_feet;
    private View cb_wind_driver_feet;
    private View ll_wind_driver_face;
    private View cb_wind_driver_face;
    private View ll_wind_driver_defrost;
    private View cb_wind_driver_defrost;
    private View ll_driver_blow;
    private View tv_driver_blow;
    private View ll_hvac_switch;
    private View cb_hvac_switch;
    private View ll_wind_circulate;
    private View cb_wind_circulate;
    private View ll_wind_window;
    private View cb_wind_window;
    private View ll_tv_auto;
    private View tv_auto;
    private View ll_tv_ac;
    private View tv_ac;
    private View ll_tv_sync;
    private View tv_sync;
    private View ll_iv_air_protect;
    private View iv_air_protect;
    private View ll_tv_eco;
    private View tv_eco;
    private View ll_tv_intelligent;
    private View tv_intelligent;
    private View ll_tv_back_row;
    private View tv_back_row;
    private View ll_tv_copilot_blow;
    private View tv_copilot_blow;
    private View ll_wind_co_face;
    private View cb_wind_co_face;
    private View ll_wind_co_feet;
    private View cb_wind_co_feet;

    public void setListener(HvacLayoutSwitchListener listener) {
        this.listener = listener;
    }

    public HvacViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        _init_view();
    }

    @Override
    protected void createViewBinder() {
        AAOP_LogUtils.d(TAG, "createViewBinder() ------111");
        _init_dialog_layout();
        insertViewBinder(TempAdjustViewBinder.Builder.createLeft(R.id.driver_temp_select));
        insertViewBinder(TempAdjustViewBinder.Builder.createRight(R.id.copilot_temp_select));
        insertViewBinder(new ACViewBinder());
        insertViewBinder(new AUTOViewBinder());
        insertViewBinder(FaceViewBinder.Builder.createLeft(R.id.cb_wind_driver_face));
        insertViewBinder(FaceViewBinder.Builder.createRight(R.id.cb_wind_co_face));
        insertViewBinder(FeetViewBinder.Builder.createLeft(R.id.cb_wind_driver_feet));
        insertViewBinder(FeetViewBinder.Builder.createRight(R.id.cb_wind_co_feet));
        insertViewBinder(DefrostViewBinder.Builder.createLeft());
        insertViewBinder(new CirculateViewBinder());
        insertViewBinder(new DirectViewBinder(AreaConstant.BAIC_LEFT_FRONT_2, R.id.tv_driver_blow));
        insertViewBinder(new DirectViewBinder(AreaConstant.BAIC_RIGHT_FRONT_2, R.id.tv_copilot_blow));
        insertViewBinder(new ECOViewBinder());
        insertViewBinder(new FrontDefrostViewBinder());
        insertViewBinder(new HvacSwitchViewBinder());
        insertViewBinder(new SYNCViewBinder());
        insertViewBinder(new WinSpeedViewBinder());

        insertViewBinder(new CocosMDVAdjustViewBinder());
        //MDV调节
//        MDVAdjustViewBinder _mdv_left_adjust = new MDVAdjustViewBinder(AreaConstant.BAIC_LEFT_LEFT_FRONT_2, R.id.mdv_left_adjust);
//        _mdv_left_adjust.set_listener(this);
//        insertViewBinder(_mdv_left_adjust);
//        MDVAdjustViewBinder _mdv_left_center_adjust = new MDVAdjustViewBinder(AreaConstant.BAIC_LEFT_MID_FRONT_2, R.id.mdv_left_center_adjust);
//        _mdv_left_center_adjust.set_listener(this);
//        insertViewBinder(_mdv_left_center_adjust);
//        MDVAdjustViewBinder _mdv_right_center_adjust = new MDVAdjustViewBinder(AreaConstant.BAIC_RIGHT_MID_FRONT_2, R.id.mdv_right_center_adjust);
//        _mdv_right_center_adjust.set_listener(this);
//        insertViewBinder(_mdv_right_center_adjust);
//        MDVAdjustViewBinder _mdv_right_adjust = new MDVAdjustViewBinder(AreaConstant.BAIC_RIGHT_RIGHT_FRONT_2, R.id.mdv_right_adjust);
//        _mdv_right_adjust.set_listener(this);
//        insertViewBinder(_mdv_right_adjust);
    }

    private void _init_dialog_layout() {
        //后排空调
        _blowing_mode_dialog = new BackBlowingModeDialogBindView(getContext(), 800, 440);
        //智能空调
        _intelligent_air_dialog = new IntelligentAirDialogBindView(getContext(), 1912, 440);
    }

    private void _init_view() {
        EventBus.getDefault().register(this);
        _hvac_view_group = findViewById(R.id.hvac_view_group);
        _tv_seat = findViewById(R.id.tv_hvac_seat);
        _tv_fragrance = findViewById(R.id.tv_hvac_fragrance);
        _driver_temp_select = findViewById(R.id.driver_temp_select);
        _copilot_temp_select = findViewById(R.id.copilot_temp_select);
        _ll_main_wind_direction = findViewById(R.id.ll_main_wind_direction);
        _ll_center_move = findViewById(R.id.ll_center_move);
        _ll_co_wind_direction = findViewById(R.id.ll_co_wind_direction);
        _mdv_show_view = findViewById(R.id.mdv_show_view);
        _win_speed_adjust_view = findViewById(R.id.win_speed_adjust_view);

        ll_wind_driver_feet = findViewById(R.id.ll_wind_driver_feet);
        cb_wind_driver_feet = findViewById(R.id.cb_wind_driver_feet);
        ll_wind_driver_face = findViewById(R.id.ll_wind_driver_face);
        cb_wind_driver_face = findViewById(R.id.cb_wind_driver_face);
        ll_wind_driver_defrost = findViewById(R.id.ll_wind_driver_defrost);
        cb_wind_driver_defrost = findViewById(R.id.cb_wind_driver_defrost);
        ll_driver_blow = findViewById(R.id.ll_driver_blow);
        tv_driver_blow = findViewById(R.id.tv_driver_blow);
        ll_hvac_switch = findViewById(R.id.ll_hvac_switch);
        cb_hvac_switch = findViewById(R.id.cb_hvac_switch);
        ll_wind_circulate = findViewById(R.id.ll_wind_circulate);
        cb_wind_circulate = findViewById(R.id.cb_wind_circulate);
        ll_wind_window = findViewById(R.id.ll_wind_window);
        cb_wind_window = findViewById(R.id.cb_wind_window);
        ll_tv_auto = findViewById(R.id.ll_tv_auto);
        tv_auto = findViewById(R.id.tv_auto);
        ll_tv_ac = findViewById(R.id.ll_tv_ac);
        tv_ac = findViewById(R.id.tv_ac);
        ll_tv_sync = findViewById(R.id.ll_tv_sync);
        tv_sync = findViewById(R.id.tv_sync);
        ll_iv_air_protect = findViewById(R.id.ll_iv_air_protect);
        iv_air_protect = findViewById(R.id.iv_air_protect);
        ll_tv_eco = findViewById(R.id.ll_tv_eco);
        tv_eco = findViewById(R.id.tv_eco);
        ll_tv_intelligent = findViewById(R.id.ll_tv_intelligent);
        tv_intelligent = findViewById(R.id.tv_intelligent);
        ll_tv_back_row = findViewById(R.id.ll_tv_back_row);
        tv_back_row = findViewById(R.id.tv_back_row);
        ll_tv_copilot_blow = findViewById(R.id.ll_tv_copilot_blow);
        tv_copilot_blow = findViewById(R.id.tv_copilot_blow);
        ll_wind_co_face = findViewById(R.id.ll_wind_co_face);
        cb_wind_co_face = findViewById(R.id.cb_wind_co_face);
        ll_wind_co_feet = findViewById(R.id.ll_wind_co_feet);
        cb_wind_co_feet = findViewById(R.id.cb_wind_co_feet);


        //逐级向下找
        _speed_seekbar = _win_speed_adjust_view.findViewById(R.id.win_speed_adjust);
        _tv_seat.setOnClickListener(v -> listener.onSeatLayout());
        _tv_fragrance.setOnClickListener(v -> listener.onFragranceLayout());
        findViewById(R.id.iv_air_protect).setOnClickListener(v -> new ForestDialog(getContext(), 2400, 640).show());
        TextView tv_intelligent = findViewById(R.id.tv_intelligent);
        tv_intelligent.setOnClickListener(v -> _intelligent_air_dialog.show());
        TextView tv_back_row = findViewById(R.id.tv_back_row);
        tv_back_row.setOnClickListener(v -> _blowing_mode_dialog.show());

        //设置默认聚焦view
        _driver_temp_select.setFocusableInTouchMode(true);
        _driver_temp_select.setFocusedByDefault(true);
        _driver_temp_select.requestFocus();

        RelativeLayout iv_hvac_bg = findViewById(R.id.iv_hvac_bg);
        Dimensional.getInstance().initial(iv_hvac_bg, 0, 0);
        Dimensional.getInstance().onStart();

        View view1 = findViewById(R.id.hvac_view_id1);
        View view2 = findViewById(R.id.hvac_view_id2);
        view1.setOnTouchListener((v, event) -> {
            return HvacPanel.getInstance().closeEvent(event);
        });
        view2.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                HvacPanel.getInstance().show(event);
                return false;
            }
        });

    }


    private void requestFocusChange(int direction) {
        //先判断 主驾副驾seekbar和风量的seekbar是否在焦点上，在的话进行view操作
//        if (_driver_temp_select.hasFocus()) {
//            switch (direction) {
//                case View.FOCUS_UP:
//                    _driver_temp_select.ccpDOWN();
//                    break;
//                case View.FOCUS_DOWN:
//                    _driver_temp_select.ccpUP();
//                    break;
//            }
//        } else if (_copilot_temp_select.hasFocus()) {
//            switch (direction) {
//                case View.FOCUS_UP:
//                    _copilot_temp_select.ccpDOWN();
//                    break;
//                case View.FOCUS_DOWN:
//                    _copilot_temp_select.ccpUP();
//                    break;
//            }
//        } else if (_speed_seekbar.hasFocus()) {
//            //seekbar left  right
//            switch (direction) {
//                case View.FOCUS_LEFT:
//                    _speed_seekbar.ccpLeft();
//                    break;
//                case View.FOCUS_RIGHT:
//                    _speed_seekbar.ccpRight();
//                    break;
//            }
//        }
        //获取当前焦点所在View，注意这个地方需要用获取范围的最外层父View，只能获取到focusLayout及所有子View
        View focused = _hvac_view_group.findFocus();
        if (null != focused) {
            AAOP_LogUtils.d(TAG, "-------viewGroup focused !=null");
            //获取当前焦点View所指向的下一个焦点的View，direction指的是上下左右对应的常量
            View nextView = focused.focusSearch(direction);
            if (null != nextView) {
                AAOP_LogUtils.d(TAG, "-------view focused !=null");
                //设置焦点迁移到目标View
                nextView.setFocusableInTouchMode(true);
                nextView.setFocusedByDefault(true);
                nextView.requestFocus(direction);
            }
        } else {
            AAOP_LogUtils.d(TAG, "-------viewGroup focused ==null");
            _driver_temp_select.setFocusableInTouchMode(true);
            _driver_temp_select.setFocusedByDefault(true);
            _driver_temp_select.requestFocus();
        }
    }

    public void onClick(){
        onFocused(ll_wind_driver_feet,cb_wind_driver_feet);
        onFocused(ll_wind_driver_face,cb_wind_driver_face);
        onFocused(ll_wind_driver_defrost,cb_wind_driver_defrost);
        onFocused(ll_driver_blow,tv_driver_blow);
        onFocused(ll_hvac_switch,cb_hvac_switch);
        onFocused(ll_wind_circulate,cb_wind_circulate);
        onFocused(ll_wind_window,cb_wind_window);
        onFocused(ll_tv_auto,tv_auto);
        onFocused(ll_tv_ac,tv_ac);
        onFocused(ll_tv_sync,tv_sync);
        onFocused(ll_iv_air_protect,iv_air_protect);
        onFocused(ll_tv_eco,tv_eco);
        onFocused(ll_tv_intelligent,tv_intelligent);
        onFocused(ll_tv_back_row,tv_back_row);
        onFocused(ll_tv_copilot_blow,tv_copilot_blow);
        onFocused(ll_wind_co_face,cb_wind_co_face);
        onFocused(ll_wind_co_feet,cb_wind_co_feet);
    }
    public void onFocused(View view1, View view2){
        if (view1.isFocused()){
            view2.performClick();
        }
    }

    @Override
    protected int acquireResourceId() {
        return R.layout.hvac_view_layout;
    }

    @Override
    public void dispatchMessage(Bundle bundle) {
        super.dispatchMessage(bundle);
        _blowing_mode_dialog.dispatchMessage(bundle);
        _intelligent_air_dialog.dispatchMessage(bundle);
    }

/*    @Override
    public void isShowMDVView(boolean is_show) {
        AAOP_LogUtils.d(TAG, "MDV Adjust ->" + is_show);
        _tv_seat.setVisibility(is_show ? GONE : VISIBLE);
        _tv_fragrance.setVisibility(is_show ? GONE : VISIBLE);
        _driver_temp_select.setVisibility(is_show ? GONE : VISIBLE);
        _copilot_temp_select.setVisibility(is_show ? GONE : VISIBLE);
        _ll_main_wind_direction.setVisibility(is_show ? GONE : VISIBLE);
        _ll_center_move.setVisibility(is_show ? GONE : VISIBLE);
        _ll_co_wind_direction.setVisibility(is_show ? GONE : VISIBLE);
        _mdv_show_view.setVisibility(is_show ? VISIBLE : GONE);
    }*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData data) {
        AAOP_LogUtils.d(TAG, "ccp eventbus ->>" + data.getType());

        switch (data.getType()) {
            case EventContents.EVENT_HVAC_LAYOUT_CCP_6:
                onClick();
                break;
            case EventContents.EVENT_HVAC_LAYOUT_CCP_7:
                requestFocusChange(View.FOCUS_UP);
                break;
            case EventContents.EVENT_HVAC_LAYOUT_CCP_8:
                requestFocusChange(View.FOCUS_DOWN);
                break;
            case EventContents.EVENT_HVAC_LAYOUT_CCP_9:
                requestFocusChange(View.FOCUS_LEFT);
                break;
            case EventContents.EVENT_HVAC_LAYOUT_CCP_10:
                requestFocusChange(View.FOCUS_RIGHT);
                break;
        }

    }
}
