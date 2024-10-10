package com.adayo.systemui.utils;


import android.util.Log;

/**
 * Created by XuYue on 2023/11/06.
 */
public class LogUtil {
    private static final String TAG = "SystemUI";//类名

    private static String className;//类名
    private static String methodName;//方法名
    private static int lineNumber;//行数

    private LogUtil() {
        /* Protect from instantiations */
    }

    public static boolean isDebuggable() {
        return true;//BuildConfig.DEBUG;
    }

    private static String createLog(String msg) {
        return methodName + "(" + className + ":" + lineNumber + ")" + msg;
    }

    private static final int index = 1;
    private static void getMethodNames(StackTraceElement[] sElements) {
        if(sElements.length <= index){
            return;
        }
        StackTraceElement stackTraceElement = sElements[index];
        className = stackTraceElement.getFileName();
        methodName = stackTraceElement.getMethodName();
        lineNumber = stackTraceElement.getLineNumber();
    }

    public static void e(String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.e(TAG, createLog(message));
    }

    public static void e(String message, Throwable e) {
        getMethodNames(new Throwable().getStackTrace());
        Log.e(TAG, createLog(message), e);
    }

    public static void i(String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.i(TAG, createLog(message));
    }

    public static void d(String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.d(TAG, createLog(message));
    }

    public static void v(String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.v(TAG, createLog(message));
    }

    public static void w(String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.w(TAG, createLog(message));
    }

    public static void wtf(String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.wtf(TAG, createLog(message));
    }

    public static void debugE(String message) {
        getMethodNames(new Throwable().getStackTrace());
        Log.e(TAG, createLog(message));
    }

    public static void debugI(String message) {
        if (!isDebuggable()) {
            return;
        }
        getMethodNames(new Throwable().getStackTrace());
        Log.i(TAG, createLog(message));
    }

    public static void debugD(String message) {
        if (!isDebuggable()) {
            return;
        }
        getMethodNames(new Throwable().getStackTrace());
        Log.d(TAG, createLog(message));
    }

    public static void debugV(String message) {
        if (!isDebuggable()) {
            return;
        }
        getMethodNames(new Throwable().getStackTrace());
        Log.v(TAG, createLog(message));
    }

    public static void debugW(String message) {
        if (!isDebuggable()) {
            return;
        }
        getMethodNames(new Throwable().getStackTrace());
        Log.w(TAG, createLog(message));
    }
}
