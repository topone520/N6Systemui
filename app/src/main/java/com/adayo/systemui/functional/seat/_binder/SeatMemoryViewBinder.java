package com.adayo.systemui.functional.seat._binder;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.contract.IViewBinder;
import com.adayo.soavb.foundation.binder.AbstractViewBinder;
import com.adayo.soavb.foundation.provider.ViewBinderProviderInteger;
import com.adayo.systemui.contents.HvacSingleton;
import com.adayo.systemui.contents.SeatSOAConstant;
import com.adayo.systemui.manager.SeatMemoryManager;
import com.adayo.systemui.proxy.vehicle.SeatService;
import com.adayo.systemui.windows.views.seat.SeatMemoryView;

/**
 * 座椅记忆
 */
public class SeatMemoryViewBinder extends AbstractViewBinder<Integer> {
    private static final String TAG = SeatMemoryViewBinder.class.getSimpleName();
    private SeatMemoryView _seat_memory;
    private final int _position;
    private final int _resource_id;

    public SeatMemoryViewBinder(ViewBinderProviderInteger provider, int position, int resource_id) {
        super(TAG, provider);
        _position = position;
        _resource_id = resource_id;
    }

    @Override
    public void _bind_view(View view) {
        Log.d(TAG, "_bind_view: dialog usher initView");
        _seat_memory = view.findViewById(_resource_id);
        _seat_memory.setSeatMemorySlotListener(slot -> {
            AAOP_LogUtils.d(TAG, "slot = " + slot);
            if (_position == SeatSOAConstant.LEFT_FRONT) {
                HvacSingleton.getInstance().setDriverSlot(slot);
            } else {
                HvacSingleton.getInstance().setCopilotSlot(slot);
            }
            SeatMemoryManager.getInstance().setMemoryInput(_position, SeatSOAConstant.MEMORY_OUT);
        });
    }

    @Override
    protected void _update_ui(Integer value) {
        AAOP_LogUtils.d(TAG, "_update_ui:: value = " + value);
    }

    @Override
    protected void _update_ui(Bundle bundle) {
        int value = bundle.getInt("value");
        int target = bundle.getInt("target");
        if (_position == target) {
            _seat_memory.updateUI(value);
        }
        if (value < 1 || value > 3) return;
        if (_position == SeatSOAConstant.LEFT_FRONT) {
            HvacSingleton.getInstance().setDriverSlot(value);
        } else {
            HvacSingleton.getInstance().setCopilotSlot(value);
        }

    }

    public static class Builder {

        public static IViewBinder createLeft(int resource_id) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.LEFT_FRONT);
            return createInstance(SeatSOAConstant.LEFT_FRONT, bundle, resource_id);
        }

        public static IViewBinder createRight(int resource_id) {
            Bundle bundle = new Bundle();
            bundle.putInt("target", SeatSOAConstant.RIGHT_FRONT);
            return createInstance(SeatSOAConstant.RIGHT_FRONT, bundle, resource_id);
        }

        private static IViewBinder createInstance(final int position, final Bundle bundle, int resource_id) {
            ViewBinderProviderInteger provider = (ViewBinderProviderInteger) new ViewBinderProviderInteger.Builder()
                    .withService(SeatService.getInstance())
                    .withBundle(bundle)
                    .withGetMessageName(SeatSOAConstant.SEAT_GET_CHILD_POSITION)
                    .withEventMessageName(SeatSOAConstant.SEAT_EVENT_CHILD_POSITION)
                    .withInitialValue(0)
                    .build();
            return new SeatMemoryViewBinder(provider, position, resource_id);
        }
    }
}
