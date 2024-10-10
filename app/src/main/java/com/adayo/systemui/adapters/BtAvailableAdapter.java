package com.adayo.systemui.adapters;

import android.view.View;
import androidx.annotation.NonNull;
import com.adayo.common.bluetooth.bean.BluetoothDevice;
import com.adayo.common.bluetooth.constant.BtDef;
import com.android.systemui.R;

import java.util.List;

public class BtAvailableAdapter extends BaseRecyclerViewAdapters<BluetoothDevice>{

    public BtAvailableAdapter() {
        super(R.layout.item_bluetooth_device_available);
    }

    @Override
    protected void bindView(BaseRecyclerHolderView holder, BluetoothDevice device, int itemPos) {
        holder.setText(R.id.tv_bt_name, device.getRemoteDevName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnConnectListener) {
                    mOnConnectListener.onConnect(device);
                }
            }
        });
    }

    private int getPosByAddress(@NonNull String address) {
        List<BluetoothDevice> data = getData();
        for (int i = 0; i < data.size(); i++) {
            if (address.equals(data.get(i).getAddress())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 设备连接状态变化调用
     *
     * @param address
     * @param state
     */
    public void notifyDeviceStateChanged(@NonNull String address, int state) {
        int pos = getPosByAddress(address);
        if (pos != -1) {
            //已连接从列表中移除
            if (state == BtDef.STATE_CONNECTED) {
                removeData(pos);
            } else {
                getData().get(pos).setState(state);
                notifyItemChanged(pos);
            }
        }
    }

    private OnConnectListener mOnConnectListener;
    public void setOnConnectListener(OnConnectListener onConnectListener) {
        mOnConnectListener = onConnectListener;
    }
    public interface OnConnectListener {
        void onConnect(BluetoothDevice device);
    }
}
