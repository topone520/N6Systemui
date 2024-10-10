package com.adayo.systemui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adayo.systemui.utils.AccessPoint;
import com.android.systemui.R;

import java.util.List;

public class WifiRecycleViewAdapter extends BaseRecyclerViewAdapter<AccessPoint> {

    private String mDeviceWifiName;
    private boolean _connectNameStatus;

    public WifiRecycleViewAdapter(Context mContext, List<AccessPoint> mList) {
        super(mContext, mList);
    }

    @Override
    protected void showOnBindViewHolder(BaseRecyclerHolderView holder, int position) {
        HolderView holderView = (HolderView) holder;
        if (getItemViewType(position) == TYPE_NORMAL) {
            if (mHeaderView == null) {
                holderView.tvItemWifiName.setText(mList.get(position).getSsid());
            } else {
                holderView.tvItemWifiName.setText(mList.get(position - 1).getSsid());
            }
        } else {
            if (mHeaderView == null) {
                holderView.tvItemWifiName.setText(mList.get(0).getSsid());
            } else {
                holderView.tvDeviceWifiName.setText(mDeviceWifiName);
                holderView.rvWifiConnectGroup.setVisibility(_connectNameStatus ? View.VISIBLE : View.GONE);
            }
        }
    }

    @Override
    protected BaseRecyclerHolderView showOnCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            return new HolderView(mHeaderView);
        }

        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_wifi_item, parent, false);
        return new HolderView(layout);
    }

    @Override
    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDeviceWifiName(String deviceWifiName) {
        this.mDeviceWifiName = deviceWifiName;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<AccessPoint> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateConnectNameStatus(Boolean aBoolean) {
        _connectNameStatus = aBoolean;
        notifyDataSetChanged();
    }

    public static class HolderView extends BaseRecyclerHolderView {

        // head view
        TextView tvDeviceWifiName;

        RelativeLayout rvWifiConnectGroup;

        // recycle view
        TextView tvItemWifiName;
        ImageView ivItemWifiLock;
        ImageView ivItemWifiMore;

        public HolderView(View itemView) {
            super(itemView);
            tvDeviceWifiName = itemView.findViewById(R.id.tv_device_wifi_name);
            rvWifiConnectGroup = itemView.findViewById(R.id.rv_wifi_connect_group);

            tvItemWifiName = itemView.findViewById(R.id.tv_item_wifi_name);
            ivItemWifiLock = itemView.findViewById(R.id.iv_item_wifi_lock);
            ivItemWifiMore = itemView.findViewById(R.id.iv_item_wifi_more);
        }
    }

}
