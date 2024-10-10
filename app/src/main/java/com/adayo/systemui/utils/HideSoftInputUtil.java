package com.adayo.systemui.utils;

import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.android.systemui.SystemUIApplication;

public class HideSoftInputUtil {

    public static  void hideSoftInput(IBinder token) {
        Log.d("TAG", "hideSoftInput: token = "+token);
        if (token != null) {
            InputMethodManager manager = (InputMethodManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
