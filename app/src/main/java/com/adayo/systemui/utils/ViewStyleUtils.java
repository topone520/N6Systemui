package com.adayo.systemui.utils;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.android.systemui.R;

public class ViewStyleUtils {
    public enum STYLE_TYPE {
        SELECT,
        NORMAL,
        DISABLE;
    }

    public static void style1(STYLE_TYPE type, TextView button) {
        AAOP_LogUtils.e("VIEWSTYLEUTILS", "type：" + type);

        switch (type) {
            case SELECT:
                AAOP_HSkin.with(button).addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.text_color_select1).applySkin(false);
                AAOP_HSkin.with(button).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.comm_c4972b8_bg).applySkin(false);
                break;
            case NORMAL:
                AAOP_HSkin.with(button).addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.text_color_normals).applySkin(false);
                AAOP_HSkin.with(button).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.bg_button_radius_normals_8).applySkin(false);
                break;
        }
    }

    public static void viewStyleColor1(STYLE_TYPE type, TextView textView) {
        AAOP_LogUtils.e("VIEWSTYLEUTILS", "type：" + type);
        switch (type) {
            case SELECT:
                AAOP_HSkin.with(textView).addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.text_color_select1).applySkin(false);
                break;
            case NORMAL:
                AAOP_HSkin.with(textView).addViewAttrs(AAOP_HSkin.ATTR_TEXT_COLOR, R.color.text_color_normals1).applySkin(false);
                break;
        }
    }

    public static void viewStyleBackground(STYLE_TYPE type, View view) {
        AAOP_LogUtils.e("VIEWSTYLEUTILS", "type：" + type);
        switch (type) {
            case SELECT:
                AAOP_HSkin.with(view).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.comm_c478bc5_bg).applySkin(false);
                break;
            case NORMAL:
                AAOP_HSkin.with(view).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.drawable.comm_c39414f_bg).applySkin(false);
                break;
        }
    }
}
