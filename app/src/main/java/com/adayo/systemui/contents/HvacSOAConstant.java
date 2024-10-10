package com.adayo.systemui.contents;


/**
 * create by gn
 * comments: 空调子服务 SOA常量类
 */
public class HvacSOAConstant {

    //region 参数 （后缀含义：信号下发：S，信号上报：R，接口SET：I，接口GET/RETURN：O,主驾：D，副驾：P,后排：R）


    //空调子服务名称
    public static final String SN_HVAC = "SN_HVAC";
    public static final String SN_DVR = "SN_DVR";
    public static final String SN_SEAT = "SN_SEAT";
    public static final String SN_FRAGRANCE = "SN_FRAGRANCE";
    public static final String SN_DRIVER = "SN_DRIVER";
    public static final String SN_WINDOW = "SN_WINDOW";

    /**
     * 电源模式可用状态 - 通用
     */
    public static final int HVAC_POWER_MODE_IGN_ON = 2;

    /**
     * SOA通用ON
     */
    public static final int HVAC_OFF_IO = 0;
    public static final int HVAC_OFF_CLOSE = 2;
    /**
     * SOA通用OFF
     */
    public static final int HVAC_ON_IO = 1;

    /**
     * 空调特殊信号通用切换信号值
     */
    public static final int HVAC_SWITCH_SIGNAL = 1;

    /**
     * 内外循环
     */
    public static final int HVAC_OUTSIDE = 1;
    public static final int HVAC_WITHIN = 0;//内循环

    //region 吹风模式

    public static final int CLOSE_BLOW_MODE_IO = 0x00;
    public static final int FACE_BLOW_MODE_IO = 0x01;
    public static final int FOOT_BLOW_MODE_IO = 0x02;
    public static final int FACE_FOOT_BLOW_MODE_IO = FACE_BLOW_MODE_IO | FOOT_BLOW_MODE_IO;
    public static final int WINDOW_BLOW_MODE_IO = 0x04;
    public static final int FACE_WINDOW_BLOW_MODE_IO = FACE_BLOW_MODE_IO | WINDOW_BLOW_MODE_IO; // 一般无此模式
    public static final int FOOT_WINDOW_BLOW_MODE_IO = FOOT_BLOW_MODE_IO | WINDOW_BLOW_MODE_IO;
    public static final int FACE_FOOT_WINDOW_BLOW_MODE_IO = FACE_BLOW_MODE_IO | FOOT_BLOW_MODE_IO | WINDOW_BLOW_MODE_IO; // 一般无此模式

    //endregion

    //region 出风口电机:电机角度/风门开度
    /**
     * NO Action
     */
    public static final int ECC_MOT_ACTV_STS_0_IO = 0;
    /**
     * 向左/向上 调节 100% / 风门开度 100%
     */
    public static final int ECC_MOT_ACTV_STS_1_IO = 1;
    /**
     * 向左/向上 调节 80% / 风门开度 10%
     */
    public static final int ECC_MOT_ACTV_STS_2_IO = 2;
    /**
     * 向左/向上 调节 60% / 风门开度 20%
     */
    public static final int ECC_MOT_ACTV_STS_3_IO = 3;
    /**
     * 向左/向上 调节 40% / 风门开度 30%
     */
    public static final int ECC_MOT_ACTV_STS_4_IO = 4;
    /**
     * 向左/向上 调节 20% / 风门开度 40%
     */
    public static final int ECC_MOT_ACTV_STS_5_IO = 5;
    /**
     * 正方向 / 风门开度 50%
     */
    public static final int ECC_MOT_ACTV_STS_6_IO = 6;
    /**
     * 向右/向下 调节 20% 风门开度 60%
     */
    public static final int ECC_MOT_ACTV_STS_7_IO = 7;
    /**
     * 向右/向下 调节 40% 风门开度 70%
     */
    public static final int ECC_MOT_ACTV_STS_8_IO = 8;
    /**
     * 向右/向下 调节 60% 风门开度 80%
     */
    public static final int ECC_MOT_ACTV_STS_9_IO = 9;
    /**
     * 向右/向下 调节 80% 风门开度 90%
     */
    public static final int ECC_MOT_ACTV_STS_10_IO = 10;
    /**
     * 向右/向下 调节 100% 风门开度 100%
     */
    public static final int ECC_MOT_ACTV_STS_11_IO = 11;

    public static final int ECC_MOT_ACTV_ACTION_DIRECTION_NO_IO = 0;
    public static final int ECC_MOT_ACTV_ACTION_SPECIFIED_IO = 0;
    public static final int ECC_MOT_ACTV_ACTION_BLOW_P_IO = 1;
    public static final int ECC_MOT_ACTV_ACTION_NOT_BLOW_P_IO = 2;
    //endregion

    //region 风速
    public static final int HVAC_VALUE_BLOW_VOLUME_0_IO = 0;
    public static final int HVAC_VALUE_BLOW_VOLUME_1_IO = 1;
    public static final int HVAC_VALUE_BLOW_VOLUME_2_IO = 2;
    public static final int HVAC_VALUE_BLOW_VOLUME_3_IO = 3;
    public static final int HVAC_VALUE_BLOW_VOLUME_4_IO = 4;
    public static final int HVAC_VALUE_BLOW_VOLUME_5_IO = 5;
    public static final int HVAC_VALUE_BLOW_VOLUME_6_IO = 6;
    public static final int HVAC_VALUE_BLOW_VOLUME_7_IO = 7;
    //endregion

    //region 循环模式
    public static final int HVAC_VALUE_INTER_CIRCULATION_IO = 1;
    public static final int HVAC_VALUE_OUT_CIRCULATION_IO = 2;
    public static final int HVAC_VALUE_MIX_CIRCULATION_IO = 4;
    //endregion

    //endregion

    //region SOA MSG_ID SET
    /**
     * 电源模式上报
     */
    public static final String MSG_EVENT_POWER_VALUE = "MSG_EVENT_POWER_VALUE";

    /**
     * 空调后除霜开关状态设置
     */
    public static final String MSG_SET_STATE = "MSG_SET_STATE";
    /**
     * 空调AC开关状态设置
     */
    public static final String MSG_SET_AC = "MSG_SET_AC";
    /**
     * 空调空气净化开关状态设置
     */
    public static final String MSG_SET_AIR_PURIFIER_SWITCH = "MSG_SET_AIR_PURIFIER_SWITCH";
    /**
     * 空调AUTO开关状态设置
     */
    public static final String MSG_SET_AUTO = "MSG_SET_AUTO";
    /**
     * 空调自干燥开关状态设置
     */
    public static final String MSG_SET_AUTODRY = "MSG_SET_AUTODRY";
    /**
     * 空调自干燥开关状态设置
     */
    public static final String MSG_SET_BLOWING_DIRECTION = "MSG_SET_BLOWING_DIRECTION";
    /**
     * 空调吹风模式设置
     */
    public static final String MSG_SET_BLOWING_MODE = "MSG_SET_BLOWING_MODE";
    /**
     * 空调吹风模式硬按键设置
     */
    public static final String MSG_BTN_SET_BLOWING_MODE = "MSG_BTN_SET_BLOWING_MODE";
    /**
     * 空调吹风模式设置
     */
    public static final String MSG_SET_CIRCULATION = "MSG_SET_CIRCULATION";
    /**
     * 空调节能模式开关状态设置
     */
    public static final String MSG_SET_ECO = "MSG_SET_ECO";
    /**
     * 空调极速控温开关状态设置
     */
    public static final String MSG_SET_EXTREME_SPEED_SWITCH = "MSG_SET_EXTREME_SPEED_SWITCH";


    /**
     * 空调风速设置
     */
    public static final String MSG_SET_FAN_SPEED_VALUE_ACTION = "MSG_SET_FAN_SPEED_VALUE_ACTION";
    /**
     * 空调前除霜开关状态设置
     */
    public static final String MSG_SET_FRONT_DEFROST = "MSG_SET_FRONT_DEFROST";
    /**
     * 空调负离子开关状态设置
     */
    public static final String MSG_SET_NEGATIVE_ION_SWITCH = "MSG_SET_NEGATIVE_ION_SWITCH";
    /**
     * 空调出风口开关状态设置
     */
    public static final String MSG_SET_OUTLET_SWITCH = "MSG_SET_OUTLET_SWITCH";
    /**
     * 空调后除霜开关状态设置
     */
    public static final String MSG_SET_REAR_DEFROST = "MSG_SET_REAR_DEFROST";
    /**
     * 空调温度同步开关状态设置
     */
    public static final String MSG_SET_SYNC = "MSG_SET_SYNC";
    /**
     * 空调温度设置
     */
    public static final String MSG_SET_TEMPERATURE_VALUE_ACTION = "MSG_SET_TEMPERATURE_VALUE_ACTION";
    /**
     * DVR声音开关设置：0:关闭，1: 开启
     */
    public static final String MSG_DVR_SET_AUDIO_ENABLE = "MSG_DVR_SET_AUDIO_ENABLE";
    /**
     * DVR开关设置：0:关闭，1: 开启
     */
    public static final String MSG_DVR_SET_ENABLE = "MSG_DVR_SET_ENABLE";
    /**
     * DVR开关状态：0:关闭，1: 开启
     */
    public static final String MSG_EVENT_DVR_ENABLE = "MSG_EVENT_DVR_ENABLE";

    //endregion

    //region SOA MSG_ID GET
    // TODO 注释待更换引用时补充

    public static final String MSG_GET_AC_STATE = "MSG_GET_AC_STATE";
    public static final String MSG_GET_AIR_PURIFIER_SWITCH = "MSG_GET_AIR_PURIFIER_SWITCH";
    public static final String MSG_GET_AIR_QUALITY = "MSG_GET_AIR_QUALITY";
    public static final String MSG_GET_AUTO_STATE = "MSG_GET_AUTO_STATE";
    public static final String MSG_GET_AUTODRY = "MSG_GET_AUTODRY";
    public static final String MSG_GET_BLOWING_DIRECTION = "MSG_GET_BLOWING_DIRECTION";
    public static final String MSG_GET_BLOWING_MODE = "MSG_GET_BLOWING_MODE";
    public static final String MSG_GET_CIRCULATION = "MSG_GET_CIRCULATION";
    public static final String MSG_GET_ECO_STATE = "MSG_GET_ECO_STATE";
    public static final String MSG_GET_EXTREME_SPEED_SWITCH = "MSG_GET_EXTREME_SPEED_SWITCH";
    public static final String MSG_GET_FAN_SPEED_VALUE = "MSG_GET_FAN_SPEED_VALUE";
    public static final String MSG_GET_FRONT_DEFROST = "MSG_GET_FRONT_DEFROST";
    public static final String MSG_GET_NEGATIVE_ION_SWITCH = "MSG_GET_NEGATIVE_ION_SWITCH";
    public static final String MSG_GET_OUTLET_SWITCH = "MSG_GET_OUTLET_SWITCH";
    public static final String MSG_GET_REAR_DEFROST = "MSG_GET_REAR_DEFROST";
    public static final String MSG_GET_STATE = "MSG_GET_STATE";
    public static final String MSG_GET_SYNC_STATE = "MSG_GET_SYNC_STATE";
    public static final String MSG_GET_TEMPERATURE = "MSG_GET_TEMPERATURE";
    public static final String MSG_GET_THERMAL_MANAGEMENT_LIMITATIONS = "MSG_GET_THERMAL_MANAGEMENT_LIMITATIONS";

    //endregion

    //region SOA Interface MSG_ID SUBSCRIBE


    public static final String MSG_EVENT_HIGH_BEAM_LIGHTS = "MSG_EVENT_HIGH_BEAM_LIGHTS";
    /**
     * 空调AC开关状态监听
     */
    public static final String MSG_EVENT_AC_STATE = "MSG_EVENT_AC_STATE";
    /**
     * 空调PM2.5数据监听
     */
    public static final String MSG_EVENT_AIR_QUALITY = "MSG_EVENT_AIR_QUALITY";
    /**
     * 空调AUTO开关状态监听
     */
    public static final String MSG_EVENT_AUTO_STATE = "MSG_EVENT_AUTO_STATE";
    /**
     * 空调自干燥关状态监听
     */
    public static final String MSG_EVENT_AUTODRY = "MSG_EVENT_AUTODRY";
    /**
     * 空调吹风风向监听
     */
    public static final String MSG_EVENT_BLOWING_DIRECTION = "MSG_EVENT_BLOWING_DIRECTION";
    /**
     * 空调吹风模式监听
     */
    public static final String MSG_EVENT_BLOWING_MODE = "MSG_EVENT_BLOWING_MODE";
    /**
     * MSG_EVENT_ECO_STATE
     */
    public static final String MSG_EVENT_ECO_STATE = "MSG_EVENT_ECO_STATE";
    /**
     * 极速控温
     */
    public static final String MSG_EVENT_EXTREME_SPEED_SWITCH = "MSG_EVENT_EXTREME_SPEED_SWITCH";
    /**
     * 空调净化
     */
    public static final String MSG_EVENT_AIR_PURIFIER_SWITCH = "MSG_EVENT_AIR_PURIFIER_SWITCH";
    /**
     * 空调风量监听
     */
    public static final String MSG_EVENT_FAN_SPEED = "MSG_EVENT_FAN_SPEED";
    /**
     * 空调前除霜关状态监听
     */
    public static final String MSG_EVENT_FRONT_DEFROST = "MSG_EVENT_FRONT_DEFROST";
    /**
     * 空调负离子开关监听
     */
    public static final String MSG_EVENT_NEGATIVE_ION_SWITCH = "MSG_EVENT_NEGATIVE_ION_SWITCH";
    /**
     * 空调出风口开关监听
     */
    public static final String MSG_EVENT_OUTLET_SWITCH = "MSG_EVENT_OUTLET_SWITCH";
    /**
     * 空调循环模式监听
     */
    public static final String MSG_EVENT_REAR_CIRCULATION = "MSG_EVENT_REAR_CIRCULATION";
    /**
     * 空调后除霜关状态监听
     */
    public static final String MSG_EVENT_REAR_DEFROST = "MSG_EVENT_REAR_DEFROST";
    /**
     * 空调开关状态监听
     */
    public static final String MSG_EVENT_STATE = "MSG_EVENT_STATE";
    /**
     * 空调温度同步开关状态监听
     */
    public static final String MSG_EVENT_SYNC_STATE = "MSG_EVENT_SYNC_STATE";
    /**
     * 空调温度监听
     */
    public static final String MSG_EVENT_TEMPERATURE = "MSG_EVENT_TEMPERATURE";
    /**
     * 空调乘员舱热管理功能限制开关监听
     */
    public static final String MSG_EVENT_THERMAL_MANAGEMENT_LIMITATIONS_SWITCH = "MSG_EVENT_THERMAL_MANAGEMENT_LIMITATIONS_SWITCH";


    /**
     * 车内温度上报
     */
    public static final String HVAC_ECC_INSDT_REVEICE = "HVAC_ECC_INSDT_REVEICE";
    public static final String HVAC_GET_ECC_TEMP = "HVAC_GET_ECC_TEMP";


    /**
     * 滤芯
     */
    public static final String MSG_SET_FILTER_ELEMENT = "MSG_SET_FILTER_ELEMENT";
    public static final String MSG_GET_FILTER_ELEMENT = "MSG_GET_FILTER_ELEMENT";

    /**8
     * 机制节能
     */
    public static final String MSG_EVENT_DRIVE_MODE = "MSG_EVENT_DRIVE_MODE";


    /**
     * 智能离子
     */
    public static final String MSG_SET_INTELL_IONS = "MSG_SET_INTELL_IONS";
    public static final String MSG_GET_INTELL_IONS = "MSG_GET_INTELL_IONS";
    public static final String MSG_EVENT_INTELL_IONS = "MSG_EVENT_RIGHT_CHILD_LOCK_CMD";

//    public static final List<String> INVOKE_MSG_ID_LIST = new ArrayList<>(Arrays.asList(MSG_EVENT_AC_STATE, MSG_EVENT_AIR_QUALITY, MSG_EVENT_AUTO_STATE, MSG_EVENT_AUTODRY,
//            MSG_EVENT_BLOWING_DIRECTION, MSG_EVENT_BLOWING_MODE, MSG_EVENT_ECO_STATE, MSG_EVENT_EXTREME_SPEED_SWITCH, MSG_EVENT_FAN_SPEED, MSG_EVENT_FRONT_DEFROST, MSG_EVENT_NEGATIVE_ION_SWITCH,
//            MSG_EVENT_OUTLET_SWITCH, MSG_EVENT_REAR_CIRCULATION, MSG_EVENT_REAR_DEFROST, MSG_EVENT_STATE, MSG_EVENT_SYNC_STATE, MSG_EVENT_SYNC_STATE, MSG_EVENT_TEMPERATURE));

    //endregion

}
