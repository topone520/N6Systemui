package com.adayo.systemui.functional.fragrance._binder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.adayo.proxy.system.aaop_systemservice.soa.bus.ADSBusReturnValue;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.AreaConstant;
import com.adayo.systemui.contents.FragranceSOAConstant;
import com.adayo.systemui.contents.FragranceSingleton;
import com.adayo.systemui.proxy.vehicle.FragranceService;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.room.database.FragranceDatabase;
import com.adayo.systemui.windows.views.fragrance.ChannelView;
import com.android.systemui.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

//香氛当前槽位&&香氛开关
public class ChannelViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = ChannelViewBinder.class.getSimpleName();
    private ChannelView _channel_view;
    private int iv_id, _iv_id1;
    private static final HashSet<String> _slot_event_list = new HashSet<>(Arrays.asList(FragranceSOAConstant.MSG_EVENT_FRAGRANCE_SLOT, FragranceSOAConstant.MSG_EVENT_FRAGRANCE_SWITCH));


    public ChannelViewBinder(int iv_bg, int iv_id1) {
        super(TAG, new ViewBinderProviderInteger.Builder()
                .withService(FragranceService.getInstance())
                .withGetMessageName(FragranceSOAConstant.MSG_GET_FRAGRANCE_SLOT)
                .withSetMessageName(FragranceSOAConstant.MSG_CHANGE_FRAGRANCE_TYPE)
                .withRelationMessages(_slot_event_list)
                .withInitialValue(0)
                .build());
        iv_id = iv_bg;
        _iv_id1 = iv_id1;
    }

    @Override
    public void _bind_view(View view) {
        ImageView iv_bg = view.findViewById(iv_id);
        ImageView iv_content = view.findViewById(_iv_id1);
        _channel_view = view.findViewById(R.id.channel_view);
        _channel_view.setChannelSelectListener(new ChannelView.ChannelSelectListener() {
            @Override
            public void onSelectChannel(int slot) {
                AAOP_LogUtils.d(TAG, "channel = " + slot);
                _set_value(slot);
                //下发香氛开启
            }

            @Override
            public void onChannelBg(int[] resources) {
                iv_bg.setBackgroundResource(resources[0]);
                iv_content.setBackgroundResource(resources[1]);
            }

            @Override
            public void onSwitch(int aSwitch) {
                AAOP_LogUtils.d(TAG, " aSwitch = " + aSwitch);
                Bundle bundle = new Bundle();
                bundle.putInt("value", aSwitch);
                FragranceService.getInstance().invoke(FragranceSOAConstant.MSG_FRAGRANCE_OPEN, bundle, new ADSBusReturnValue());
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
        switch (Objects.requireNonNull(bundle.getString("message_id"))) {
            case FragranceSOAConstant.MSG_EVENT_FRAGRANCE_SLOT:
                //香氛通道监听
                _channel_view.updateChannel(bundle.getInt("value"));
                break;
            case FragranceSOAConstant.MSG_EVENT_FRAGRANCE_SWITCH:
                //香氛开关监听
                if (bundle.getInt("value") == FragranceSOAConstant.CLOSE)
                    _channel_view.updateClose();
                _channel_view.updateSwitch(bundle.getInt("value"));
                break;
        }
    }
}
