package com.adayo.systemui.utils;

import android.text.TextUtils;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.room.bean.FragranceInfo;
import com.adayo.systemui.contents.FragranceSOAConstant;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import java.util.HashMap;
import java.util.Map;

public class FragranceMapUtils {
    private static final String TAG = FragranceMapUtils.class.getSimpleName();
    public static HashMap<Integer, Integer> typeMap = null;

    public static FragranceInfo makeFragranceInfo(int position, int type, String newTitle) {
        switch (type) {
            case FragranceSOAConstant.TYPE_ONE:
                return new FragranceInfo(position, type,
                        TextUtils.isEmpty(newTitle) ?
                                SystemUIApplication.getSystemUIContext().getResources().getString(R.string.fragrance_title_a) : newTitle,
                        R.mipmap.vtp_ac_fragrance_button_p1,
                        R.mipmap.vtp_ac_fragrance_button_s1,
                        R.mipmap.ivi_ac_fragrance_bg_bule1,
                        R.layout.fragrance_slot_copywriting_1);
            case FragranceSOAConstant.TYPE_TWO:
                return new FragranceInfo(position, type,
                        TextUtils.isEmpty(newTitle) ?
                                SystemUIApplication.getSystemUIContext().getResources().getString(R.string.fragrance_title_b) : newTitle,
                        R.mipmap.vtp_ac_fragrance_button_p2,
                        R.mipmap.vtp_ac_fragrance_button_s2,
                        R.mipmap.ivi_ac_fragrance_bg_green,
                        R.layout.fragrance_slot_copywriting_2);
            case FragranceSOAConstant.TYPE_THREE:
                return new FragranceInfo(position, type,
                        TextUtils.isEmpty(newTitle) ?
                                SystemUIApplication.getSystemUIContext().getResources().getString(R.string.fragrance_title_c) : newTitle,
                        R.mipmap.vtp_ac_fragrance_button_p3,
                        R.mipmap.vtp_ac_fragrance_button_s3,
                        R.mipmap.ivi_ac_fragrance_bg_yellow,
                        R.layout.fragrance_slot_copywriting_3);
            case FragranceSOAConstant.TYPE_NULL:
                return new FragranceInfo(position, type,
                        SystemUIApplication.getSystemUIContext().getResources().getString(R.string.fragrance_title_n),
                        R.drawable.drawable_fragrance_default,
                        R.drawable.drawable_fragrance_default,
                        R.mipmap.ivi_ac_fragrance_bg_bule1,
                        R.layout.fragrance_slot_copywriting_4);
            default:
                return new FragranceInfo();
        }
    }

    public static void setType(int position, int type) {
        if (typeMap == null) typeMap = new HashMap<>();
        typeMap.put(position, type);
    }

    public static int getMap(int position) {
        if (typeMap == null) typeMap = new HashMap<>();
        if (!typeMap.containsKey(position)) {
            return 0;
        }
        return typeMap.get(position);
    }

    /**
     * 所有通道香型是否为null
     *
     * @return
     */
    public static boolean isAllTypeNull() {
        boolean isTypeNull = true;
        for (Map.Entry<Integer, Integer> entry : typeMap.entrySet()) {
            AAOP_LogUtils.d(TAG, "香氛---》" + entry.getValue());
            if (entry.getValue() != FragranceSOAConstant.TYPE_NULL) {
                isTypeNull = false;
            }
        }
        return isTypeNull;
    }

    /**
     * 没有选中任何通道的情况下 显示第一种（第一种无香氛则向下顺延）
     */
    public static int getDefaultXml() {
        if (typeMap == null) typeMap = new HashMap<>();
        if (typeMap.containsKey(FragranceSOAConstant.FRAGRANCE_A_POSITION) && typeMap.get(FragranceSOAConstant.FRAGRANCE_A_POSITION) != FragranceSOAConstant.TYPE_NULL) {
            return FragranceSOAConstant.FRAGRANCE_A_POSITION;
        } else if (typeMap.containsKey(FragranceSOAConstant.FRAGRANCE_B_POSITION) && typeMap.get(FragranceSOAConstant.FRAGRANCE_B_POSITION) != FragranceSOAConstant.TYPE_NULL) {
            return FragranceSOAConstant.FRAGRANCE_B_POSITION;
        } else if (typeMap.containsKey(FragranceSOAConstant.FRAGRANCE_C_POSITION) && typeMap.get(FragranceSOAConstant.FRAGRANCE_C_POSITION) != FragranceSOAConstant.TYPE_NULL) {
            return FragranceSOAConstant.FRAGRANCE_C_POSITION;
        } else {
            return 0;
        }
    }

}
