package com.adayo.systemui.windows.dialogs;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adayo.systemui.contents.HvacSingleton;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.functional.seat._binder.SeatMemoryViewBinder;
import com.adayo.systemui.manager.SeatMemoryManager;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class SeatMemoryInputDialog extends BindViewBaseDialog {

    private static SeatMemoryInputDialog instance;
    private TextView tv_tips;
    private StringBuffer buffer = new StringBuffer();
    private boolean isDriver;
    private Handler handler = new Handler();

    private SeatMemoryInputDialog(@NonNull Context context, int dialogWidth, int _dialogHeight) {
        super(context, dialogWidth, _dialogHeight);
    }

    public static SeatMemoryInputDialog getInstance() {
        if (instance == null) {
            synchronized (SeatMemoryInputDialog.class) {
                if (instance == null) {
                    instance = new SeatMemoryInputDialog(SystemUIApplication.getSystemUIContext(), 820, 360);
                }
            }
        }
        return instance;
    }


    @Override
    protected void initView() {
        findViewById(R.id.tv_input).setOnClickListener(v -> inputDataMcu());
        findViewById(R.id.tv_cancel).setOnClickListener(v -> dismiss());
        tv_tips = findViewById(R.id.tv_tips);
    }

    @Override
    protected void createViewBinder() {
    }

    @Override
    protected int acquireResourceId() {
        return R.layout.seat_memory_input_dialog_layout;
    }

    public void updateTips(boolean isDriver) {
        if (!isShowing()) {
            show();
        }
        resetHandler();
        this.isDriver = isDriver;
        if (tv_tips == null) return;
        buffer.setLength(0);
        buffer.append(getContext().getResources().getString(isDriver ? R.string.driver_is_input : R.string.copilot_is_input));
        if (isDriver) {
            if (HvacSingleton.getInstance().getDriverSlot() == 1) {
                buffer.append(getContext().getResources().getString(R.string.driver_input_1));
            } else if (HvacSingleton.getInstance().getDriverSlot() == 2) {
                buffer.append(getContext().getResources().getString(R.string.driver_input_2));
            } else {
                buffer.append(getContext().getResources().getString(R.string.driver_input_3));
            }
        } else {
            if (HvacSingleton.getInstance().getCopilotSlot() == 1) {
                buffer.append(getContext().getResources().getString(R.string.copilot_input_1));
            } else if (HvacSingleton.getInstance().getCopilotSlot() == 2) {
                buffer.append(getContext().getResources().getString(R.string.copilot_input_2));
            } else {
                buffer.append(getContext().getResources().getString(R.string.copilot_input_3));
            }
        }
        tv_tips.setText(buffer.toString());
    }

    private void resetHandler() {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> dismiss(), 5000);
    }

    private void inputDataMcu() {
        handler.removeCallbacksAndMessages(null);
        if (isDriver) {
            //TODO 下发主驾驶位置保存
            SeatMemoryManager.getInstance().setMemoryInput(SeatSOAConstant.LEFT_FRONT, SeatSOAConstant.MEMORY_SAVE);
        } else {
            //TODO 下发副驾驶位置保存
            SeatMemoryManager.getInstance().setMemoryInput(SeatSOAConstant.RIGHT_FRONT, SeatSOAConstant.MEMORY_SAVE);
        }
        dismiss();
    }
}

