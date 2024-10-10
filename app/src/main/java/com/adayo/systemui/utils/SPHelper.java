package com.adayo.systemui.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SPHelper {
    SharedPreferences sharedPreferences;
    public SPHelper(Context context, String fileName) {
        sharedPreferences = context.createDeviceProtectedStorageContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }
    public static class ContentValue {
        String key;
        Object value;

        public ContentValue(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    public void putValues(ContentValue... contentValues) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (ContentValue contentValue : contentValues) {
            if (null != editor && contentValue.value instanceof String) {
                editor.putString(contentValue.key, contentValue.value.toString());
				editor.commit();
            }

            if (null != editor && contentValue.value instanceof Integer) {
                editor.putInt(contentValue.key, Integer.parseInt(contentValue.value.toString()));
				editor.commit();
            }

            if (null != editor && contentValue.value instanceof Long) {
                editor.putLong(contentValue.key, Long.parseLong(contentValue.value.toString()));
				editor.commit();
            }

            if (null != editor && contentValue.value instanceof Boolean) {
                editor.putBoolean(contentValue.key, Boolean.parseBoolean(contentValue.value.toString()));
				editor.commit();
            }
        }
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }
    public int getInt(String key,int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public long getLong(String key) {
        return sharedPreferences.getLong(key, -1);
    }

    public void clear() {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if(null != editor){
			editor.clear();
			editor.commit();
		}
    }
}
