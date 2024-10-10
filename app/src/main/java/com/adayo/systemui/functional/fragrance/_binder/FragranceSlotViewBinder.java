package com.adayo.systemui.functional.fragrance._binder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.FragranceSOAConstant;
import com.adayo.systemui.contents.FragranceSingleton;
import com.adayo.systemui.contents.HvacSOAConstant;
import com.adayo.systemui.functional.fragrance._contract.IChannelMonitor;
import com.adayo.systemui.functional.fragrance._contract.ITitleMonitor;
import com.adayo.systemui.proxy.vehicle.FragranceService;
import com.adayo.systemui.proxy.vehicle.HvacService;
import com.adayo.systemui.room.database.FragranceDatabase;
import com.adayo.systemui.windows.views.fragrance.FragranceSlotBgView;
import com.adayo.systemui.windows.views.fragrance.FragranceSlotView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

//香氛当前槽位&&香氛开关
public class FragranceSlotViewBinder extends AbstractViewBinder<Integer> implements IChannelMonitor.IChannelObservable, ITitleMonitor.ITitleObservable {
    private static final String TAG = FragranceSlotViewBinder.class.getSimpleName();
    private FragranceSlotView _slot_view;
    private FragranceSlotBgView _slot_bg_view;
    private final int _slot_position;
    private final int _slot_view_id, _slot_bg_view_id;
    private static final HashSet<String> _slot_event_list = new HashSet<>(Arrays.asList(FragranceSOAConstant.MSG_EVENT_FRAGRANCE_SLOT, FragranceSOAConstant.MSG_EVENT_FRAGRANCE_SWITCH));

    private final IChannelMonitor _monitor;

    public FragranceSlotViewBinder(IChannelMonitor monitor, int position, int slotViewId, int slotBgViewId) {
        // super(new ViewBinderProviderInteger(FragranceService.getInstance(), null, null, null, _slot_event_list));
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(FragranceService.getInstance())
                .withRelationMessages(_slot_event_list)
                .withInitialValue(0)
                .build());
        _monitor = monitor;
        _slot_position = position;
        _slot_view_id = slotViewId;
        _slot_bg_view_id = slotBgViewId;
    }

    @Override
    public void _bind_view(View view) {
        _slot_bg_view = view.findViewById(_slot_bg_view_id);
        _slot_view = view.findViewById(_slot_view_id);
        _slot_view.setPosition(_slot_position);
        _slot_view.setClickListener(() -> {
            if (FragranceSingleton.getInstance().isFragranceOpen()) {
                //香氛打开
                if (FragranceSingleton.getInstance().getFragranceSlot() == _slot_position) {
                    //点击当前选中通道 关闭香氛
                    FragranceSingleton.getInstance().setFragranceSwitch(false);
                    Bundle bundle = new Bundle();
                    bundle.putInt("value", AreaConstant.FRAGRANCE_OFF_IO);
                    FragranceService.getInstance().invoke(FragranceSOAConstant.MSG_FRAGRANCE_OPEN, bundle, new ADSBusReturnValue());
                } else {
                    //点击了其他通道 正常切通道
                    FragranceSingleton.getInstance().setFragranceSlot(_slot_position);
                    Bundle bundle = new Bundle();
                    bundle.putInt("value", _slot_position);
                    FragranceService.getInstance().invoke(FragranceSOAConstant.MSG_CHANGE_FRAGRANCE_TYPE, bundle, new ADSBusReturnValue());
                }
                _monitor.selectChannel(_slot_position, FragranceSingleton.getInstance().isFragranceOpen());
            } else {
                //香氛关闭
                if (FragranceSingleton.getInstance().getFragranceSlot() == _slot_position) {
                    //点击当前选中通道 打开香氛
                    FragranceSingleton.getInstance().setFragranceSwitch(true);
                    Bundle bundle = new Bundle();
                    bundle.putInt("value", AreaConstant.FRAGRANCE_ON_IO);
                    FragranceService.getInstance().invoke(FragranceSOAConstant.MSG_FRAGRANCE_OPEN, bundle, new ADSBusReturnValue());
                } else {
                    //点击了其他通道 正常切通道
                    FragranceSingleton.getInstance().setFragranceSlot(_slot_position);
                    Bundle bundle = new Bundle();
                    bundle.putInt("value", _slot_position);
                    FragranceService.getInstance().invoke(FragranceSOAConstant.MSG_CHANGE_FRAGRANCE_TYPE, bundle, new ADSBusReturnValue());
                }
                _monitor.selectChannel(_slot_position, FragranceSingleton.getInstance().isFragranceOpen());
            }
        });
    }

    @Override
    protected void _update_ui(Integer value) {
        Log.d(TAG, "_update_ui with value: --------- = " + value);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        Log.d(TAG, "_update_ui() bundle.toString(): " + bundle.toString());
        final Optional<String> message_id = bundle.containsKey("message_id") ? Optional.ofNullable(bundle.getString("message_id")) : Optional.empty();
        if (!message_id.isPresent()) {
            Log.d(TAG, "_update_ui: message_id is null");
            return;
        }
        int switchStatus = 0; //香氛开关
        int slot = 0; //香氛通道
        switch (message_id.get()) {
            case FragranceSOAConstant.MSG_EVENT_FRAGRANCE_SLOT:
                //香氛通道监听
                slot = bundle.getInt("value");
                FragranceSingleton.getInstance().setFragranceSlot(slot);
                Log.d(TAG, "_update_ui() slot = " + slot);
                break;
            case FragranceSOAConstant.MSG_EVENT_FRAGRANCE_SWITCH:
                //香氛开关监听
                switchStatus = bundle.getInt("value");
                Log.d(TAG, "_update_ui() switchState = " + switchStatus);
                if (switchStatus == AreaConstant.FRAGRANCE_ON_IO) {
                    FragranceSingleton.getInstance().setFragranceSwitch(true);
                } else if (switchStatus == AreaConstant.FRAGRANCE_OFF_IO) {
                    FragranceSingleton.getInstance().setFragranceSwitch(false);
                }
                break;
        }
        if (switchStatus == AreaConstant.FRAGRANCE_ON_IO) {
            //香氛开启
            _slot_view.updateSwitchUI(slot == _slot_position);
        } else if (switchStatus == AreaConstant.FRAGRANCE_OFF_IO) {
            //香氛关闭
            _slot_view.updateSwitchUI(false);
        }
        AAOP_LogUtils.d(TAG, "slot -----> ");
        try {
            //不存在的香氛类型
            if (slot < FragranceSOAConstant.TYPE_NULL || slot > FragranceSOAConstant.TYPE_THREE) return;
            _slot_bg_view.setUpdateViewUI(FragranceDatabase.getInstance().fragranceDao().getFragranceInfoByPosition(slot).getBackground());
            _slot_bg_view.setUpdateWriteUI(FragranceDatabase.getInstance().fragranceDao().getFragranceInfoByPosition(slot).getWrite());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onChannelChanged(int channel, boolean isOpen) {
        Log.d(TAG, "onChannelChanged: channel = " + channel + " | " + "isOpen = " + isOpen);
        //不存在的香氛类型
        if (channel < FragranceSOAConstant.TYPE_NULL || channel > FragranceSOAConstant.TYPE_THREE) return;
        if (!isOpen) {
            _slot_view.updateSwitchUI(false); //香氛off，通道全部hide
        } else {
            _slot_view.updateSwitchUI(channel == _slot_position);
        }
        try {
            //不存在的香氛类型
            _slot_bg_view.setUpdateViewUI(FragranceDatabase.getInstance().fragranceDao().getFragranceInfoByPosition(channel).getBackground());
            _slot_bg_view.setUpdateWriteUI(FragranceDatabase.getInstance().fragranceDao().getFragranceInfoByPosition(channel).getWrite());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onTitleChanged(int position) {
        //不存在的香氛类型
        if (position < FragranceSOAConstant.TYPE_NULL || position > FragranceSOAConstant.TYPE_THREE) return;
        //自定义香氛名称后立刻切换为默认背景
        try {
            _slot_bg_view.setUpdateViewUI(FragranceDatabase.getInstance().fragranceDao().getFragranceInfoByPosition(position).getBackground());
            _slot_bg_view.setUpdateWriteUI(FragranceDatabase.getInstance().fragranceDao().getFragranceInfoByPosition(position).getWrite());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
