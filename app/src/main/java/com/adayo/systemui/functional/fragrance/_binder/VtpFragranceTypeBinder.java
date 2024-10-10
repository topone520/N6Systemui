package com.adayo.systemui.functional.fragrance._binder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.FragranceSOAConstant;
import com.adayo.systemui.functional.fragrance._monitor.TitleMonitor;
import com.adayo.systemui.interfaces.HvacLayoutSwitchListener;
import com.adayo.systemui.proxy.vehicle.FragranceService;
import com.adayo.systemui.room.database.FragranceDatabase;
import com.adayo.systemui.utils.FragranceMapUtils;
import com.adayo.systemui.utils.VtpFragranceMapUtils;
import com.adayo.systemui.windows.views.fragrance.FragranceSlotView;
import com.adayo.systemui.windows.views.fragrance.VtpFragranceSlotView;

public class VtpFragranceTypeBinder extends AbstractViewBinder<Integer> {

    private static final String TAG = VtpFragranceTypeBinder.class.getSimpleName();
    private final int _slot_view_id;
    private final int _lin_error_id;
    private final int _slot_position; //香氛通道
    private HvacLayoutSwitchListener _layout_switch_listener;
    private VtpFragranceSlotView _slot_view; //香氛通道控件
    private RelativeLayout _frag_lin_error_view;

    public VtpFragranceTypeBinder(HvacLayoutSwitchListener layoutSwitchListener, int position, int slotViewId, int linErrorId) {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(FragranceService.getInstance())
                .withGetMessageName(FragranceSOAConstant.MSG_GET_FRAGRANCE_TYPE)
                .withEventMessageName(FragranceSOAConstant.MSG_EVENT_FRAGRANCE_TYPE)
                .withInitialValue(0)
                .build());
        _slot_view_id = slotViewId;
        _lin_error_id = linErrorId;
        _slot_position = position;
        _layout_switch_listener = layoutSwitchListener;
    }

    @Override
    public void _bind_view(View view) {
        _slot_view = view.findViewById(_slot_view_id);
        _frag_lin_error_view = view.findViewById(_lin_error_id);
    }

    @Override
    protected void _update_ui(Integer value) {
        Log.d(TAG, "_update_ui with value: --------- = " + value);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        Log.d(TAG, "_update_ui() bundle.toString(): " + bundle.toString());
        int slot = bundle.getInt("slot");
        int type = bundle.getInt("value");
        Log.d(TAG, "_update_ui() slot = " + slot + "type = " + type);

        /**
         * 1.先存储香氛香型
         * 2.判断当前应该显示什么
         * 2.1-通道背景显示
         * 2.2-大背景显示
         */
        if (_slot_position == slot) {
            //存储香氛数据
            VtpFragranceMapUtils.setType(_slot_position, type);
            //通道均不含香氛显示无香氛页面
            _frag_lin_error_view.setVisibility(VtpFragranceMapUtils.isAllTypeNull() ? View.VISIBLE : View.GONE);
            if (FragranceDatabase.getInstance().vtpFragranceDao().getFragranceInfoList().size() < 3) {
                //表中的数据不超过三条 说明为第一次使用 新增数据 记录香氛信息
                Log.d(TAG, "_update_ui: first use fragrance");
                FragranceDatabase.getInstance().vtpFragranceDao().insertFragranceInfo(VtpFragranceMapUtils.makeFragranceInfo(
                        _slot_position,
                        type,
                        ""));
            }
            int oldType;
            oldType = FragranceDatabase.getInstance().vtpFragranceDao().getFragranceInfoByPosition(_slot_position).getType();
            if (oldType != type) {
                //香氛种类被更换 更新数据
                FragranceDatabase.getInstance().vtpFragranceDao().updateFragranceInfo(VtpFragranceMapUtils.makeFragranceInfo(
                        _slot_position,
                        type,
                        ""));
            }
            _slot_view.updateTypeUI(FragranceDatabase.getInstance().vtpFragranceDao().getFragranceInfoByPosition(_slot_position));
            //判断自身香型是否为空 为空则显示下一个
            if (type == FragranceSOAConstant.TYPE_NULL) {
                int position = VtpFragranceMapUtils.getDefaultXml();
                TitleMonitor.getInstance().changeTitle(position);
            }
        }
    }
}
