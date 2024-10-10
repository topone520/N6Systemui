package com.adayo.systemui.utils;

import com.adayo.systemui.contents.SeatSOAConstant;

import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicLong;

public class TimeUtils {

    private static AtomicLong startTimeMillis = new AtomicLong();

    /**
     * 开始计时
     */
    public static void startTimer() {
        startTimeMillis.set(System.currentTimeMillis());
    }

    /**
     * 获取已经经过的时间（秒）
     *
     * @return 经过的秒数
     */
    public static synchronized int getElapsedTimeInSeconds() {
        long currentTimeMillis = System.currentTimeMillis();
        long elapsedTimeMillis = currentTimeMillis - startTimeMillis.get();
        return (int) (elapsedTimeMillis / 1000);
    }

    /**
     * 获取已经经过的时间（秒）
     *
     * @return 经过的毫秒
     */
    public static synchronized int getTimeInSeconds() {
        long currentTimeMillis = System.currentTimeMillis();
        long elapsedTimeMillis = currentTimeMillis - startTimeMillis.get();
        return (int) (elapsedTimeMillis);
    }

    /**
     * 获取已经经过的时间（分钟）
     *
     * @return 经过的分钟数
     */
    public static synchronized int getElapsedTimeInMinutes() {
        return getElapsedTimeInSeconds() / 60;
    }

    /**
     * 获取已经经过的时间（小时）
     *
     * @return 经过的小时数
     */
    public static synchronized int getElapsedTimeInHours() {
        return getElapsedTimeInMinutes() / 60;
    }

    public static String getTempJson(Object str1, Object str2) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("temperature_setting", str1);
            jsonObject.put("air_temperature_setting_position", str2);
            jsonObject.put("temperature_settingway", "中控屏");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getSyncJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("wind_two_synch_duration", getTimeInSeconds());
            jsonObject.put("wind_two_synch_turnoffway", "手动关闭");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getVentilateJson(Object position, Object level) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("seat_wind_position", position);
            jsonObject.put("seat_wind_turnonway", "中控屏开启");
            jsonObject.put("seat_wind_shift", level);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getHeatJson(int position, Object level) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("seat_heating_position", getSeatPosition(position));
            jsonObject.put("seat_heating_turnonway", "中控屏开启");
            jsonObject.put("seat_heating_shift", level);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static String getSeatPosition(int position) {
        if (position == SeatSOAConstant.LEFT_FRONT) {
            return "主驾";
        } else if (position == SeatSOAConstant.RIGHT_FRONT) {
            return "副驾";
        } else if (position == SeatSOAConstant.LEFT_AREA) {
            return "坐后排";
        } else {
            return "右后排";
        }
    }
}


