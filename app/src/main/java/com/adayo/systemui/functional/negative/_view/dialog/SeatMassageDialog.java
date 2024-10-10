package com.adayo.systemui.functional.negative._view.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.view.AbstractBindViewDialog;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.functional.seat._binder.SeatMassageMoveViewBinder;
import com.android.systemui.R;

public class SeatMassageDialog extends AbstractBindViewDialog {
    private static final String TAG = SeatMassageDialog.class.getSimpleName();

    public SeatMassageDialog(@NonNull Context context) {
        super(context, R.style.TransparentStyle);
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            // Set the gravity of the content view to top-center
            window.setGravity(Gravity.CENTER);
            // Set system UI visibility
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            WindowManager.LayoutParams params = window.getAttributes();
            params.type = 2071;
            params.width = 1264;
            params.height = 440;
            // Calculate the y-coordinate to position the dialog at the top, considering status bar height
            Rect rect = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            int statusBarHeight = rect.top;
            params.y = statusBarHeight; // Adjust this value based on your needs
            window.setAttributes(params);
        }
    }

    @Override
    protected void createViewBinder() {
        insertViewBinder(SeatMassageMoveViewBinder.Builder.createLeft(R.id.seat_massage_rlv));
    }

    @Override
    protected int acquireResourceId() {
        return R.layout.dialog_seat_massage_layout;
    }
}
