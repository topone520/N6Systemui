package com.adayo.systemui.utils;

import com.adayo.systemui.contents.AreaConstant;

public class TempIndexUtil {
    public static int indexOf(float targetValue) {
        String value = targetValue + "";
        if (value.equals("17.5")) {
            return AreaConstant.TR_ARRAY.length - 1;
        }
        if (value.equals("32.5")) return 0;
        for (int i = 0; i < AreaConstant.TR_ARRAY.length; i++) {
            if (AreaConstant.TR_ARRAY[i].equals(value)) {
                return i; // 找到了，返回下标
            }
        }
        return -1; // 没找到，返回 0
    }
}
