package com.adayo.systemui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.systemui.bean.WiFiInfo;
import com.adayo.systemui.manager.WiFiControllerImpl;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import java.util.ArrayList;
import java.util.List;

public class NearbyNetworksAdapter extends RecyclerView.Adapter<NearbyNetworksAdapter.ViewHolder> {

    private List<WiFiInfo> _wifi_info_list = new ArrayList<>();

    public NearbyNetworksAdapter(List<WiFiInfo> wiFiInfoList) {
        _wifi_info_list = wiFiInfoList;
    }

    public void setWifiList(List<WiFiInfo> wiFiInfoList) {
        _wifi_info_list = wiFiInfoList;
    }

    @NonNull
    @Override
    public NearbyNetworksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(SystemUIApplication.getSystemUIContext()).inflate(R.layout.item_wifi_device_available, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyNetworksAdapter.ViewHolder holder, int position) {
        WiFiInfo wiFiInfo = _wifi_info_list.get(position);
        holder._tv_wifi_name.setText(wiFiInfo.getDeviceName().equals("") ? "隐藏的网络" : wiFiInfo.getDeviceName());
        if (wiFiInfo.getRssi() == 1) {
            AAOP_HSkin.with(holder._img_wifi_level).addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.ivi_top_icon_wifi_1_n).applySkin(false);
        } else if (wiFiInfo.getRssi() == 2) {
            AAOP_HSkin.with(holder._img_wifi_level).addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.ivi_top_icon_wifi_2_n).applySkin(false);
        } else if (wiFiInfo.getRssi() == 3) {
            AAOP_HSkin.with(holder._img_wifi_level).addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.ivi_top_icon_wifi_3_n).applySkin(false);
        } else if (wiFiInfo.getRssi() == 4) {
            AAOP_HSkin.with(holder._img_wifi_level).addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.ivi_top_icon_wifi_4_n).applySkin(false);
        } else if (wiFiInfo.getRssi() == 5) {
            AAOP_HSkin.with(holder._img_wifi_level).addViewAttrs(AAOP_HSkin.ATTR_SRC, R.mipmap.ivi_top_icon_wifi_5_n).applySkin(false);
        }
    }

    @Override
    public int getItemCount() {
        return _wifi_info_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView _img_wifi_level;
        private TextView _tv_wifi_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _img_wifi_level = itemView.findViewById(R.id.img_wifi_level);
            _tv_wifi_name = itemView.findViewById(R.id.tv_wifi_name);
            itemView.findViewById(R.id.img_wifi_disconnect);
        }
    }
}
