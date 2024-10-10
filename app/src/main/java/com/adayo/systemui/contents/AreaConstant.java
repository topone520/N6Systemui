package com.adayo.systemui.contents;

import android.car.VehicleAreaType;

/**
 * create by gn
 * comments: 区域
 */
public class AreaConstant {

    //温度
    public static String[] TR_ARRAY = {"Hi", "32.0", "31.5", "31.0", "30.5",
            "30.0", "29.5", "29.0", "28.5", "28.0",
            "27.5", "27.0", "26.5", "26.0", "25.5",
            "25.0", "24.5", "24.0", "23.5", "23.0",
            "22.5", "22.0", "21.5", "21.0", "20.5",
            "20.0", "19.5", "19.0", "18.5", "18.0", "Lo"};

    //空调最低温和最高温
    public static float HVAC_TEMP_MAX = 17.5F;
    public static float HVAC_TEMP_MIN = 32.5F;

    /**
     * PM 2.5
     * 0~50
     * 51~100
     * 101~150
     * 151~200
     * 201~300
     * >300
     * 级
     * 级
     * 三级
     * 四级
     * 五级
     * 六级
     * 优
     * 良
     * 轻度污染
     * 营中污染
     * 重 度污染
     * 严重污染
     * 绿色
     * 黄色
     * 色
     * 红色
     * 紫色
     * 褐 红色
     */

    public static int PM_0 = 0;
    public static int PM_50 = 50;

    public static int PM_100 = 100;

    public static int PM_150 = 150;

    public static int PM_200 = 200;

    public static int PM_300 = 300;

    /**
     * 通用 传值 BcmService
     */
    /**
     * SOA通用ON
     */
    public static final int HVAC_OFF_IO = 0;
    /**
     * SOA通用OFF
     */
    public static final int HVAC_ON_IO = 1;

    /**
     * 空调开关
     */
    public static final int HVAC_SWITCH_OPEN = 1;
    public static final int HVAC_SWITCH_CLOSE = 2;

    /**
     * 香氛开关
     */
    public static final int FRAGRANCE_OFF_IO = 0;
    public static final int FRAGRANCE_ON_IO = 1;

    /**
     * 空调调温  具体值   最大值   最小值
     */
    public static final int HVAC_TEMP_ACTION_SPECIFIC = 1;
    public static final int HVAC_TEMP_ACTION_MAX = 3;
    public static final int HVAC_TEMP_ACTION_MIN = 4;

    /**
     * 急速控温最大和最小临界值
     */
    public static final int RAPID_MAX = 40;
    public static final int RAPID_MIN = 10;
    /**
     * 信号区域参数统一用0
     */
    public static final int AREA_GLOBAL = VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL;

    public static final int DEVICE_DEFAULT = -1;

    public static final int TYPE_IVI = 0x1;
    public static final int TYPE_CLUSTER = 0x2;
    public static final int TYPE_IVI_CLUSTER = 0x2;
    public static final int TYPE_FOUR = 0x4;
    public static final int TYPE_ENGHT = 0x8;

    /**
     * 前排  后排
     */

    public static final int TYPE_IVI_CLUSTER_ = TYPE_IVI | TYPE_CLUSTER;
    public static final int TYPE_IVI_FOUR = TYPE_IVI | TYPE_FOUR;
    public static final int TYPE_CLUSTER_FOUR = TYPE_CLUSTER | TYPE_FOUR;
    public static final int TYPE_FOUR_ENGHT = TYPE_FOUR | TYPE_ENGHT;
    public static final int TYPE_IVI_CLUSTER_FOUR = TYPE_IVI | TYPE_CLUSTER | TYPE_FOUR;


    public static final int TYPE_IVI_CLUSTER_FOUR_ENGHT = TYPE_IVI | TYPE_CLUSTER | TYPE_FOUR | TYPE_ENGHT;


    //region 循环模式
    public static final int HVAC_VALUE_INTER_CIRCULATION_IO = 1;
    //外循环
    public static final int HVAC_VALUE_OUT_CIRCULATION_IO = 2;

    //region AREA 接口-位置参数:通用值
    /**
     * 左前/主驾
     */
    public static final int BAIC_LEFT_FRONT = 0x01;
    /**
     * 右前/副驾
     */
    public static final int BAIC_RIGHT_FRONT = 0x02;
    /**
     * 左后
     */
    public static final int BAIC_LEFT_REAR = 0x04;
    /**
     * 右后
     */
    public static final int BAIC_RIGHT_REAR = 0x08;
    /**
     * 前排
     */
    public static final int BAIC_FRONT = BAIC_LEFT_FRONT | BAIC_RIGHT_FRONT;
    /**
     * 吹脚吹面吹窗
     */
    public static final int BAIC_FEET_FACE_WINDOW = BAIC_LEFT_FRONT | BAIC_RIGHT_FRONT | BAIC_LEFT_REAR;
    /**
     * 吹脚吹窗
     */
    public static final int BAIC_FEET_WINDOW = BAIC_RIGHT_FRONT | BAIC_LEFT_REAR;
    /**
     * 后排
     */
    public static final int BAIC_REAR = BAIC_LEFT_REAR | BAIC_RIGHT_REAR;
    /**
     * 左侧
     */
    public static final int BAIC_LEFT = BAIC_LEFT_FRONT | BAIC_LEFT_REAR;
    /**
     * 右侧
     */
    public static final int BAIC_RIGHT = BAIC_RIGHT_FRONT | BAIC_RIGHT_REAR;
    /**
     * 全部
     */
    public static final int BAIC_ALL = BAIC_LEFT_FRONT | BAIC_RIGHT_FRONT | BAIC_LEFT_REAR | BAIC_RIGHT_REAR;
    /**
     * 二排中 后排中
     */
    public static final int DEVICE_MIDDLE_BACK = 9;
    //endregion

    /**
     * 吹风模式
     */
    public static final int BLOW_FACE = 0x1;//吹面
    public static final int BLOW_FACE_FEAT = 0x2;//吹面吹腿
    public static final int BLOW_FEET = 0x3;//吹脚

    public static final int BLOW_FEET_WINDOW = 0x4;//吹脚吹窗
    public static final int BLOW_WINDOW = 0x5;//吹窗
    public static final int BLOW_BACK_END = 0x4;//后排截止

    //region AREA 接口-位置参数:电动出风口
    // 出风口电机(前左（0x01）、前左中（0x02）、前右（0x04）、前右中（0x08）、左后（0x10）、右后（0x20）、 主驾：（0x1|0x2）、副驾：（0x4|0x8）、前排：（0x1|0x2|0x4|0x8）、后排：（0x10|0x20）、左侧：（0x1|0x4|0x10）、右侧：（0x2|0x8|0x20）、整车：（0x1|0x2|0x4|0x8|0x10|0x20）)
    /**
     * 出风口定义，直吹和防直吹也在此范围
     */
    public static final int DIRECT_BLOWING = 1;
    public static final int PREVENT_DIRECT_BLOWING = 2;
    /**
     * （出风口电机）前左
     */
    public static final int BAIC_LEFT_LEFT_FRONT_2 = 0x01;
    /**
     * （出风口电机）前左中
     */
    public static final int BAIC_LEFT_MID_FRONT_2 = 0x02;
    /**
     * （出风口电机）前右
     */
    public static final int BAIC_RIGHT_RIGHT_FRONT_2 = 0x04;
    /**
     * （出风口电机）前右中
     */
    public static final int BAIC_RIGHT_MID_FRONT_2 = 0x08;
    /**
     * （出风口电机）左后
     */
    public static final int BAIC_LEFT_REAR_2 = 0x10;
    /**
     * （出风口电机）右后
     */
    public static final int BAIC_RIGHT_REAR_2 = 0x20;
    /**
     * （出风口电机）主驾
     */
    public static final int BAIC_LEFT_FRONT_2 = BAIC_LEFT_LEFT_FRONT_2 | BAIC_LEFT_MID_FRONT_2;
    /**
     * （出风口电机）副驾
     */
    public static final int BAIC_RIGHT_FRONT_2 = BAIC_RIGHT_RIGHT_FRONT_2 | BAIC_RIGHT_MID_FRONT_2;
    /**
     * （出风口电机）前排
     */
    public static final int BAIC_FRONT_2 = BAIC_LEFT_LEFT_FRONT_2 | BAIC_LEFT_MID_FRONT_2 | BAIC_RIGHT_RIGHT_FRONT_2 | BAIC_RIGHT_MID_FRONT_2;
    /**
     * （出风口电机）后排
     */
    public static final int BAIC_REAR_2 = BAIC_LEFT_REAR_2 | BAIC_RIGHT_REAR_2;
    /**
     * （出风口电机）左侧
     */
    public static final int BAIC_LEFT_2 = BAIC_LEFT_LEFT_FRONT_2 | BAIC_RIGHT_RIGHT_FRONT_2 | BAIC_LEFT_REAR_2;
    /**
     * （出风口电机）右侧
     */
    public static final int BAIC_RIGHT_2 = BAIC_LEFT_MID_FRONT_2 | BAIC_RIGHT_MID_FRONT_2 | BAIC_RIGHT_REAR_2;
    /**
     * （出风口电机）整车
     */
    public static final int BAIC_ALL_2 = BAIC_LEFT_LEFT_FRONT_2 | BAIC_LEFT_MID_FRONT_2 | BAIC_RIGHT_RIGHT_FRONT_2 | BAIC_RIGHT_MID_FRONT_2 | BAIC_LEFT_REAR_2 | BAIC_RIGHT_REAR_2;
    //endregion

}
