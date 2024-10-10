package com.adayo.systemui.contents;

import java.util.HashMap;
import java.util.Objects;

public class FragranceSOAConstant {

    public static final int FRAGRANCE_A_POSITION=1;
    public static final int FRAGRANCE_B_POSITION=2;
    public static final int FRAGRANCE_C_POSITION=3;

    /**
     * 香氛浓度
     */
    public static final int CONCENTRATION_HIGH=3;
    public static final int CONCENTRATION_MID=2;
    public static final int CONCENTRATION_LOW=1;

    /**
     * 香氛滑动档位判断
     */
    public static final int SLIDE_LOW=33;
    public static final int SLIDE_MID=66;
    public static final int SLIDE_HIGH=100;

    /**
     * 香氛故障
     */
    public static final int FRAGRANCE_ERROR=1;
    public static final int OPEN=1;
    public static final int CLOSE=0;

    /**
     * 香氛香型
     */
    public static final int TYPE_NULL = 0;
    public static final int TYPE_ONE = 1;
    public static final int TYPE_TWO = 2;
    public static final int TYPE_THREE = 3;

    /**
     * 香氛浓度设置
     */
    public static final String MSG_CHANGE_FRAGRANCE_CONCENTRATION = "MSG_CHANGE_FRAGRANCE_CONCENTRATION";
    public static final String MSG_GET_FRAGRANCE_CONCENTRATION = "MSG_GET_FRAGRANCE_CONCENTRATION";
    public static final String MSG_EVENT_FRAGRANCE_CONCENTRATION = "MSG_EVENT_FRAGRANCE_CONCENTRATION";

    /**
     * 香氛当前槽位选择
     */
    public static final String MSG_CHANGE_FRAGRANCE_TYPE = "MSG_CHANGE_FRAGRANCE_TYPE";
    public static final String MSG_GET_FRAGRANCE_SLOT = "MSG_GET_FRAGRANCE_SLOT";
    public static final String MSG_EVENT_FRAGRANCE_SLOT = "MSG_EVENT_FRAGRANCE_SLOT";

    /**
     * 香氛开关设置
     */
    public static final String MSG_FRAGRANCE_OPEN = "MSG_FRAGRANCE_OPEN";
    public static final String MSG_GET_FRAGRANCE_SWITCH = "MSG_GET_FRAGRANCE_SWITCH";
    public static final String MSG_EVENT_FRAGRANCE_SWITCH = "MSG_EVENT_FRAGRANCE_SWITCH";

    /**
     * 提神香氛释放状态
     */
    public static final String MSG_SET_FRAGRANCE_RELEASE = "MSG_SET_FRAGRANCE_RELEASE";
    public static final String MSG_GET_FRAGRANCE_RELEASE = "MSG_GET_FRAGRANCE_RELEASE";
    public static final String MSG_EVENT_FRAGRANCE_RELEASE = "MSG_EVENT_FRAGRANCE_RELEASE";

    /**
     * 香氛过期状态
     */
    public static final String MSG_GET_FRAGRANCE_EXPIRE_STATE = "MSG_GET_FRAGRANCE_EXPIRE_STATE";
    public static final String MSG_EVENT_FRAGRANCE_EXPIRE_STATE = "MSG_EVENT_FRAGRANCE_EXPIRE_STATE";

    /**
     * 香氛香型获取
     */
    public static final String MSG_GET_FRAGRANCE_TYPE = "MSG_GET_FRAGRANCE_TYPE";
    public static final String MSG_EVENT_FRAGRANCE_TYPE = "MSG_EVENT_FRAGRANCE_TYPE";

    /**
     * 香氛余量获取
     */
    public static final String MSG_GET_FRAGRANCE_SURPLUS = "MSG_GET_FRAGRANCE_SURPLUS";
    public static final String MSG_EVENT_FRAGRANCE_SURPLUS = "MSG_EVENT_FRAGRANCE_SURPLUS";

    /**
     * 香氛故障
     */
    public static final String MSG_EVENT_FRAGRANCE_SYSTEM_ERROR = "MSG_EVENT_FRAGRANCE_SYSTEM_ERROR";

}
