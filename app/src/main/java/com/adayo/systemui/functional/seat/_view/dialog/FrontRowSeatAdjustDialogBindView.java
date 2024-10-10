package com.adayo.systemui.functional.seat._view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.contents.HvacSingleton;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.eventbus.EventContents;
import com.adayo.systemui.eventbus.EventData;
import com.adayo.systemui.functional.seat._binder.SeatBackrestViewBinder;
import com.adayo.systemui.functional.seat._binder.SeatCushionViewBinder;
import com.adayo.systemui.functional.seat._binder.SeatFrontRearViewBinder;
import com.adayo.systemui.functional.seat._binder.SeatHeightAdjustViewBinder;
import com.adayo.systemui.functional.seat._binder.SeatMemoryViewBinder;
import com.adayo.systemui.functional.seat._binder.SeatUsherViewBinder;
import com.adayo.systemui.manager.SeatMemoryManager;
import com.adayo.systemui.windows.dialogs.BindViewBaseDialog;
import com.android.systemui.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class FrontRowSeatAdjustDialogBindView extends BindViewBaseDialog {
    private final static String TAG = FrontRowSeatAdjustDialogBindView.class.getSimpleName();
    private TextView copilot_save;
    private TextView driver_save;

    public FrontRowSeatAdjustDialogBindView(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }

    @Override
    protected void initView() {
        driver_save = findViewById(R.id.driver_save);
        copilot_save = findViewById(R.id.copilot_save);
        driver_save.setOnClickListener(v -> SeatMemoryManager.getInstance().setMemoryInput(SeatSOAConstant.LEFT_FRONT, SeatSOAConstant.MEMORY_SAVE));
        copilot_save.setOnClickListener(v -> SeatMemoryManager.getInstance().setMemoryInput(SeatSOAConstant.RIGHT_FRONT, SeatSOAConstant.MEMORY_SAVE));
    }

    @Override
    protected void createViewBinder() {
        //迎宾
        AAOP_LogUtils.d(TAG, "createViewBinder+++++++++++++");
        insertViewBinder(SeatUsherViewBinder.Builder.createLeft(R.id.driver_usher));
        insertViewBinder(SeatUsherViewBinder.Builder.createRight(R.id.copilot_usher));
        insertViewBinder(new SeatFrontRearViewBinder(SeatSOAConstant.LEFT_FRONT, R.id.driver_cross_adjust));
        insertViewBinder(new SeatFrontRearViewBinder(SeatSOAConstant.RIGHT_FRONT, R.id.copilot_cross_adjust));
        insertViewBinder(new SeatHeightAdjustViewBinder(SeatSOAConstant.LEFT_FRONT, R.id.driver_cross_adjust));
        insertViewBinder(new SeatHeightAdjustViewBinder(SeatSOAConstant.RIGHT_FRONT, R.id.copilot_cross_adjust));
        insertViewBinder(new SeatBackrestViewBinder(SeatSOAConstant.LEFT_FRONT, R.id.driver_backrest));
        insertViewBinder(new SeatBackrestViewBinder(SeatSOAConstant.RIGHT_FRONT, R.id.copilot_backrest));
        insertViewBinder(new SeatCushionViewBinder(SeatSOAConstant.LEFT_FRONT, R.id.driver_cushion));
        insertViewBinder(new SeatCushionViewBinder(SeatSOAConstant.RIGHT_FRONT, R.id.copilot_cushion));
        insertViewBinder(SeatMemoryViewBinder.Builder.createLeft(R.id.driver_memory));
        insertViewBinder(SeatMemoryViewBinder.Builder.createRight(R.id.copilot_memory));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setOnDismissListener(dialog -> {
            AAOP_LogUtils.d(TAG, "setOnDismissListener------");
            HvacSingleton.getInstance().setSeatDialogShow(false);
        });
        setOnShowListener(dialog -> {
            HvacSingleton.getInstance().setSeatDialogShow(true);
            if (null != driver_save && null != copilot_save) {
                driver_save.setVisibility(View.GONE);
                copilot_save.setVisibility(View.GONE);
            }
            AAOP_LogUtils.d(TAG, "setOnShowListener------");
        });
    }

    @Override
    protected int acquireResourceId() {
        return R.layout.front_row_seat_adjust_layout;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData data) {
        boolean isDriver = (Boolean) data.getData();
        AAOP_LogUtils.d(TAG, "ccp eventbus ->>" + data.getType() + "  isDrier = " + isDriver);
        switch (data.getType()) {
            case EventContents.EVENT_SEAT_MEMORY:
                AAOP_LogUtils.d(TAG, "-----switch case HvacSingleton.getInstance().isSeatDialogShow()=" + HvacSingleton.getInstance().isSeatDialogShow());
                if (HvacSingleton.getInstance().isSeatDialogShow()) {
                    AAOP_LogUtils.d(TAG, "isShowing = true");
                    if (isDriver && null != driver_save) {
                        driver_save.setVisibility(View.VISIBLE);
                    } else {
                        if (null != copilot_save) {
                            copilot_save.setVisibility(View.VISIBLE);
                        }
                    }
                }
                break;
        }

    }
}
