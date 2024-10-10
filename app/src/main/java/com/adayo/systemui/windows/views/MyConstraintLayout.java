package com.adayo.systemui.windows.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.utils.WindowsUtils;
import com.android.systemui.R;

/**
 * @author ADAYO-13
 */
public class MyConstraintLayout extends ConstraintLayout {
    static String TAG = MyConstraintLayout.class.getName();

    public MyConstraintLayout(@NonNull Context context) {
        super(context);
    }

    public MyConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyConstraintLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        LogUtil.d("My dispatchTouchEvent---");
        try {
            WindowsUtils.showHvacPanel(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onTouchEvent(event);
    }
}
