package com.adayo.systemui.utils;

import java.lang.reflect.Method;

public class PropUtils {
    /**
     * 设置属性值
     *
     * @param key   长度不能超过31，key.length <= 30
     * @param value 长度不能超过91，value.length<=90
     */
    public static void set(String key, String value) {
        // android.os.SystemProperties
        // public static void set(String key, String val)
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            Method method = cls.getMethod("set", String.class, String.class);
            method.invoke(null, key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取属性值
     *
     * @param key 长度不能超过31，key.length <= 30
     * @param defValue
     * @return
     */
    public static String get(String key, String defValue) {
        // android.os.SystemProperties
        // public static String get(String key, String def)
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            Method method = cls.getMethod("get", String.class, String.class);
            return (String) method.invoke(null, key, defValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defValue;
    }
}
