package com.adayo.systemui.utils;

import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import java.lang.reflect.Field;

/**
 * 功能描述:自定义toast显示时长
 */
public class CustomToastUtil {

    public static volatile CustomToastUtil instance;
    public static String TAG = "_CustomToastUtil";

    /**
     * 获取instance.
     *
     * @return 返回该类对象
     */
    public static CustomToastUtil getInstance() {
        if (instance == null) {
            synchronized (CustomToastUtil.class) {
                if (instance == null) {
                    instance = new CustomToastUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 发布通知
     *
     * @param title    标题
     * @param gravity  显示位置
     * @param duration 显示时间（暂时不可自定义）
     */
    public void showToast(String title, int gravity, long duration) {
        LayoutInflater inflater = AAOP_HSkin.getLayoutInflater(SystemUIApplication.getSystemUIContext());
        View toastView = inflater.inflate(R.layout.common_toast_layout, null);
        TextView tvToast = toastView.findViewById(R.id.tv_toast);
        tvToast.setText(title);
        Toast mToast = new Toast(SystemUIApplication.getSystemUIContext());
        switch (gravity) {
            case 0:
                mToast.setGravity(Gravity.TOP, 0, 0);
                break;
            case 1:
                mToast.setGravity(Gravity.CENTER, 0, 0);
                break;
            case 2:
                mToast.setGravity(Gravity.BOTTOM, 0, 80);
                break;
        }


        mToast.setView(toastView);

        if (duration == 0) {
            mToast.setDuration(Toast.LENGTH_SHORT);
        } else {
            mToast.setDuration(Toast.LENGTH_LONG);
        }
        mToast.show();

    }
}
