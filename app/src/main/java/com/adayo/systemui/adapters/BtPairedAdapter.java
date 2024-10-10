package com.adayo.systemui.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.adayo.common.bluetooth.bean.BluetoothDevice;
import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.android.systemui.R;

public class BtPairedAdapter extends BaseRecyclerViewAdapters<BluetoothDevice> {
    private static final String TAG = BtPairedAdapter.class.getSimpleName();

    public BtPairedAdapter() {
        super(R.layout.item_bluetooth_device_paired);
    }

    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    @Override
    protected void bindView(BaseRecyclerHolderView holder, BluetoothDevice device, int itemPos) {
        AAOP_LogUtils.i(TAG, "bindView() State:" + device.getState());
        holder.setText(R.id.tv_bt_name, device.getRemoteDevName());
        FrameLayout fl_bg = holder.itemView.findViewById(R.id.fl_bg);
        if (device.isHfpConnected() || device.isA2dpConnected()){
            fl_bg.setBackground(new ColorDrawable(Color.parseColor("#3F5B8C")));
            holder.setText(R.id.tv_bt_state, R.string.screen_bluetooth_text);
            holder.setTextColor(R.id.tv_bt_state, Color.parseColor("#5390E5"));
            holder.setImageResource(R.id.img_bt_disconnect, R.mipmap.ivi_launcher_icon_unconnection_n);
        }else {
            fl_bg.setBackground(new ColorDrawable(Color.parseColor("#00FFFFFF")));
            holder.setText(R.id.tv_bt_state, R.string.screen_bluetooth_no_text);
            holder.setTextColor(R.id.tv_bt_state, Color.parseColor("#B3FFFFFF"));
            holder.setImageResource(R.id.img_bt_disconnect, R.mipmap.ivi_launcher_icon_connection_n);
        }

        TextView tv_bt_state = holder.itemView.findViewById(R.id.tv_bt_state);
        ImageView img_bt_disconnect = holder.itemView.findViewById(R.id.img_bt_disconnect);
        img_bt_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnOperationListener) {
                    mOnOperationListener.onBTClick(device, tv_bt_state.getText().toString());
                }
            }
        });
    }

    private OnOperationListener mOnOperationListener;
    public void setOnConnectListener(OnOperationListener onOperationListener) {
        mOnOperationListener = onOperationListener;
    }

    public interface OnOperationListener {
        void onBTClick(BluetoothDevice bluetoothDevice, String tvStatus);

    }
}
