package com.adayo.systemui.utils;

import android.content.ContentResolver;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeDataUtils {

    /**
     * 一些时间格式
     */
    public final static String FORMAT_TIME = "H:mm";
    public final static String FORMAT_TIME_12 = "h:mm";
    public final static String FORMAT_DATE_TIMES = "M月d日";
    public final static String FORMAT_ENGLISH_DATE_TIMES = "MMMM d";
    private static boolean mChina;

    /**
     * 获取当前日期是星期几<br>
     *
     * @param date
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date date) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        String[] weekDaysEnglish = {"SUn", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        //if (mChina) {
            return weekDays[w];
//        } else {
//            return weekDaysEnglish[w];
//        }

    }

    /**
     * 获取当前时间
     *
     * @return 当前时间是几点
     */
    public static String getHourAndMin(Context context,long time) {
        String hour;
        if ("24".equals(TimeDataUtils.getStrTimeFormat(context))) {
            hour = FORMAT_TIME;
        } else {
            hour = FORMAT_TIME_12;
        }
        SimpleDateFormat format = new SimpleDateFormat(hour);
        return format.format(new Date(time));
    }

    /**
     * 获取日期
     *
     * @return 当前日期是几月几日
     */
    public static String getDataTime(Context context, long time) {
        mChina = "CN".equals(context.getResources().getConfiguration().locale.getCountry());
        String dataTime;
        if (mChina) {
            dataTime = FORMAT_DATE_TIMES;
        } else {
            dataTime = FORMAT_ENGLISH_DATE_TIMES;
        }
        SimpleDateFormat format = new SimpleDateFormat(dataTime);
        return format.format(new Date(time));
    }

    /**
     * 获取时间是12小时制还是24小时制
     *
     * @return 当前时间是12小时制还是24小时制
     */
    public static String getStrTimeFormat(Context context) {
        ContentResolver cv = context.getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv, android.provider.Settings.System.TIME_12_24);
        return strTimeFormat;
    }

}
