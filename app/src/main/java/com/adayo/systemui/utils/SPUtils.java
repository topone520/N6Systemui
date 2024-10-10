package com.adayo.systemui.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONArray;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/*
 * SP工具类
 */
public class SPUtils {

    private static final String TAG = "SPUtils";
    private static final String FILE_NAME = "systemUIShared";

    private static SPUtils instance;
    private SharedPreferences sharedPreferences;

    private SPUtils(Context context) {
        sharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SPUtils getInstance(Context context) {
        if (instance == null) {
            instance = new SPUtils(context.getApplicationContext());
        }
        return instance;
    }

    public void saveList(String key, List<String> list) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, String.valueOf(list));
        editor.apply();
    }

    public List<String> getList(String key, Object object) {
        List<String> listTemp = new ArrayList<>();
        try {
            String json = sharedPreferences.getString(key, (String) object);
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                String jsonArrayString = jsonArray.getString(i);
                listTemp.add(jsonArrayString);
            }
            Log.d(TAG, "getList.listTemp :" + listTemp);
        } catch (Exception e) {
            Log.d(TAG, "getList.Exception :" + e);
        }
        return listTemp;
    }

    public void put(String key, Object object) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(editor);
    }

    public Object get(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return sharedPreferences.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sharedPreferences.getLong(key, (Long) defaultObject);
        }
        return null;
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        private static Method findApplyMethod() {
            try {
                Class<SharedPreferences.Editor> clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException ignored) {
            }
            return null;
        }

        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException | IllegalAccessException |
                     InvocationTargetException ignored) {
            }
            editor.commit();
            try {
                Runtime.getRuntime().exec("sync");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
