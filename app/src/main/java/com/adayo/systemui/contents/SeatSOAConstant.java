package com.adayo.systemui.contents;

public class SeatSOAConstant {

    public static final int SEAT_ADJUST_UP = 0x1;
    public static final int SEAT_ADJUST_DOWN = 0x2;
    public static final int SEAT_ADJUST_OFF = 0x0;

    public static final int LEFT_FRONT = 0x01;
    public static final int RIGHT_FRONT = 0x02;
    public static final int LEFT_AREA = 0x04;
    public static final int RIGHT_AREA = 0x08;
    public static final int AREA_BACK_TARGET = LEFT_AREA | RIGHT_AREA;

    public static final int MEMORY_OUT = 3;
    public static final int MEMORY_SAVE = 1;

    public static final int FRONT_ROW = LEFT_FRONT | RIGHT_FRONT;
    public static final int BACK_ROW = LEFT_AREA | RIGHT_AREA;

    public static final int SEAT_MASSAGE_OPEN = 1;
    public static final int SEAT_MASSAGE_CLOSE = 0;
    public static final int SEAT_MASSAGE_LEVEL_MAX = 3;

    public static final int SEAT_HEAT_AUTO_OPEN = 2;
    public static final int SEAT_HEAT_AUTO_CLOSE = 1;

    public static int NEW_SEAT_GEAR_HIGH = 3;
    public static int NEW_SEAT_GEAR_MID = 2;
    public static int NEW_SEAT_GEAR_LOW = 1;
    public static int NEW_SEAT_GEAR_CLOSE = 0;
    public static int ENERGY_OPEN = 1;

    /**
     * 座椅调节
     */
    public static final int SEAT_ADJUST_VALUE = 1;
    /**
     * 座椅加热设置  状态获取  监听
     */
    public static final String MSG_SET_SEAT_HEATING = "MSG_SET_SEAT_HEATING";
    public static final String MSG_GET_SEAT_HEATING = "MSG_GET_SEAT_HEATING";
    public static final String MSG_EVENT_SEAT_HEATING = "MSG_EVENT_SEAT_HEATING";

    /**
     * 座椅通风   状态获取   监听
     */
    public static final String MSG_SET_SEAT_VENTILATION = "MSG_SET_SEAT_VENTILATION";
    public static final String MSG_GET_SEAT_VENTILATION = "MSG_GET_SEAT_VENTILATION";
    public static final String MSG_EVENT_SEAT_VENTILATION = "MSG_EVENT_SEAT_VENTILATION";
    /**
     * 座椅迎宾   状态获取   监听
     */
    public static final String MSG_SET_SEAT_VELCOME = "MSG_SET_SEAT_VELCOME";
    public static final String MSG_GET_SEAT_VELCOME = "MSG_GET_SEAT_VELCOME";
    public static final String MSG_EVENT_SEAT_VELCOME = "MSG_EVENT_SEAT_VELCOME";


    /**
     * 座椅按摩 开关 档位 模式  状态获取   监听
     */
    public static final String MSG_SET_SEAT_MASSAGE = "MSG_SET_SEAT_MASSAGE";
    public static final String MSG_GET_SEAT_MASSAGE = "MSG_GET_SEAT_MASSAGE";
    public static final String MSG_EVENT_SEAT_MASSAGE = "MSG_EVENT_SEAT_MASSAGE";


    public static final String MSG_SET_SEAT_MASSAGE_GEAR = "MSG_SET_SEAT_MASSAGE_GEAR";
    public static final String MSG_GET_SEAT_MASSAGE_GEAR = "MSG_GET_SEAT_MASSAGE_GEAR";
    public static final String MSG_EVENT_SEAT_MASSAGE_GEAR = "MSG_EVENT_SEAT_MASSAGE_GEAR";

    public static final String MSG_SET_SEAT_MASSAGE_MOVE = "MSG_SET_SEAT_MASSAGE_MOVE";
    public static final String MSG_GET_SEAT_MASSAGE_MOVE = "MSG_GET_SEAT_MASSAGE_MOVE";
    public static final String MSG_EVENT_SEAT_MASSAGE_MOVE = "MSG_EVENT_SEAT_MASSAGE_MOVE";


    /**
     * 方向盘
     */
    public static final String MSG_SET_SEAT_STEERING_WHEEL = "MSG_SET_SEAT_STEERING_WHEEL";
    public static final String MSG_GET_SEAT_STEERING_WHEEL = "MSG_GET_SEAT_STEERING_WHEEL";
    public static final String MSG_EVENT_SEAT_STEERING_WHEEL = "MSG_EVENT_SEAT_STEERING_WHEEL";

    /**
     * 前后排全关
     */
    public static final String MSG_SET_ALL_CLOSE_FUNCTION = "MSG_SET_ALL_CLOSE_FUNCTION";


    /**
     * \
     * 靠背
     */
    public static final String MSG_SET_SEAT_BACKREST = "MSG_SET_SEAT_BACKREST";
    public static final String MSG_GET_SEAT_BACKREST = "MSG_GET_SEAT_BACKREST";
    public static final String MSG_EVENT_SEAT_BACKREST = "MSG_EVENT_SEAT_BACKREST";
    /**
     * 座椅加热自动换挡
     */
    public static final String MSG_SET_PERF_SEAT_HEAT_LV_AUTO_REDUCE_REQ_SET = "MSG_SET_PERF_SEAT_HEAT_LV_AUTO_REDUCE_REQ_SET";
    public static final String MSG_GET_PERF_SEAT_HEAT_LV_AUTO_REDUCE_REQ_SET = "MSG_SET_PERF_SEAT_HEAT_LV_AUTO_REDUCE_REQ_SET";
    public static final String MSG_EVENT_PERF_SEAT_HEAT_LV_AUTO_REDUCE_REQ_SET = "MSG_SET_PERF_SEAT_HEAT_LV_AUTO_REDUCE_REQ_SET";


    public static final String MSG_EVENT_STEERING_STATUS = "MSG_EVENT_STEERING_STATUS";
    public static final String MSG_EVENT_REARVIEW_STATUS = "MSG_EVENT_REARVIEW_STATUS";
    public static final String MSG_EVENT_GEAR = "MSG_EVENT_GEAR";
    public static final String MSG_EVENT_SPEED_VALUE = "MSG_EVENT_SPEED_VALUE";
    //机制节能
    public static final String MSG_EVENT_EXTREME_ENERGY = "MSG_EVENT_EXTREME_ENERGY";
    public static final String MSG_SET_SEAT_MEMORY = "MSG_SET_SEAT_MEMORY";
    //蓄电池电量
    public static final String BCM_BATTSOC_RECEIVE = "BCM_BATTSOC_RECEIVE";


    public static final String MSG_SET_SEAT_HEIGHT_BTN = "MSG_SET_SEAT_HEIGHT_BTN";
    public static final String MSG_SET_SEAT_BACKREST_BTN = "MSG_SET_SEAT_BACKREST_BTN";
    public static final String MSG_SET_SEAT_CUSHION_BTN = "MSG_SET_SEAT_CUSHION_BTN";
    public static final String MSG_SET_SEAT_FRONT_REAR_BTN = "MSG_SET_SEAT_FRONT_REAR_BTN";


    public static final String MSG_EVENT_SEAT_HEIGHT_BTN = "MSG_EVENT_SEAT_HEIGHT_BTN";
    public static final String MSG_EVENT_SEAT_BACKREST_BTN = "MSG_EVENT_SEAT_BACKREST_BTN";
    public static final String MSG_EVENT_SEAT_CUSHION_BTN = "MSG_EVENT_SEAT_CUSHION_BTN";
    public static final String MSG_EVENT_SEAT_FRONT_REAR_BTN = "MSG_EVENT_SEAT_FRONT_REAR_BTN";

    //报警等级
    public final static String MSG_GET_WARNING_LEVEL = "MSG_GET_WARNING_LEVEL";
    public final static String MSG_EVENT_WARNING_LEVEL = "MSG_EVENT_WARNING_LEVEL";

    //自命令
    public static final String SEAT_GET_CHILD_POSITION = "SEAT_GET_CHILD_POSITION";
    public static final String SEAT_EVENT_CHILD_POSITION = "SEAT_EVENT_CHILD_POSITION";

}
