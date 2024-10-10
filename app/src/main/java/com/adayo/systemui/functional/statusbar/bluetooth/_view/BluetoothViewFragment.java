package com.adayo.systemui.functional.statusbar.bluetooth._view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.soavb.foundation.view.AbstractBTBindViewFramelayout;
import com.adayo.systemui.functional.statusbar.bluetooth._binder.BluetoothNameViewBinder;
import com.adayo.systemui.functional.statusbar.bluetooth._binder.BluetoothPairedDevicesViewBinder;
import com.adayo.systemui.functional.statusbar.bluetooth._binder.BluetoothStatusViewBinder;
import com.android.systemui.R;

public class BluetoothViewFragment extends AbstractBTBindViewFramelayout {
    private static final String TAG = BluetoothViewFragment.class.getSimpleName();

    public BluetoothViewFragment(@NonNull Context context) {
        super(context);
    }

    public BluetoothViewFragment(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected int acquireResourceId() {
        return R.layout.popup_bt_view_layout;
    }


    @Override
    protected void createViewBinder() {
        AAOP_LogUtils.i(TAG, "::createViewBinder()");
        insertViewBinder(new BluetoothStatusViewBinder());
        insertViewBinder(new BluetoothNameViewBinder());
        insertViewBinder(new BluetoothPairedDevicesViewBinder());
    }
}
