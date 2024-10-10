package com.adayo.systemui.utils;

import android.content.Context;

public class ResourceUtil {

    public static int getLayoutId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "layout",
                context.getPackageName());
    }

    public static int getStringId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "string",
                context.getPackageName());
    }

    public static int getArrayId(Context context, String resName) {
        return context.getResources().getIdentifier(resName, "array",
                context.getPackageName());
    }

    public static int getDrawableId(Context context, String resName) {
        return context.getResources().getIdentifier(resName,
                "drawable", context.getPackageName());
    }

    public static int getStyleId(Context context, String resName) {
        return context.getResources().getIdentifier(resName,
                "style", context.getPackageName());
    }

    public static int getId(Context context, String resName) {
        return context.getResources().getIdentifier(resName,
                "id", context.getPackageName());
    }

    public static int getColorId(Context context, String resName) {
        return context.getResources().getIdentifier(resName,
                "color", context.getPackageName());
    }

    public static int getAttrId(Context context, String resName) {
        return context.getResources().getIdentifier(resName,
                "attr", context.getPackageName());
    }

    public static int getStyleableId(Context context, String resName) {
        return context.getResources().getIdentifier(resName,
                "styleable", context.getPackageName());
    }


    public static int getMipmapId(Context context, String resName) {
        return context.getResources().getIdentifier(resName,
                "mipmap", context.getPackageName());
    }
}
