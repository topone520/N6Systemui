package com.adayo.systemui.adapters;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.systemui.bean.WiFiInfo;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import java.util.ArrayList;
import java.util.List;

public class LinkingDeviceAdapter extends RecyclerView.Adapter<LinkingDeviceAdapter.ViewHolder> {

    private List<WiFiInfo> _wifi_info_list = new ArrayList<>();

    public LinkingDeviceAdapter(List<WiFiInfo> wiFiInfoList) {
        _wifi_info_list = wiFiInfoList;
    }

    public void setWifiList(List<WiFiInfo> wiFiInfoList) {
        _wifi_info_list = wiFiInfoList;
    }

    @NonNull
    @Override
    public LinkingDeviceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(SystemUIApplication.getSystemUIContext()).inflate(R.layout.item_wifi_device_paired, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull LinkingDeviceAdapter.ViewHolder holder, int position) {
        WiFiInfo wiFiInfo = _wifi_info_list.get(position);
        AAOP_HSkin.with(holder._fl_bg).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.color.c3f5b8c).applySkin(false);
        AAOP_HSkin.with(holder._tv_wifi_state).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, R.color.c5390e5).applySkin(false);
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

        private FrameLayout _fl_bg;
        private ImageView _img_wifi_level;
        private TextView _tv_wifi_name, _tv_wifi_state;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _fl_bg = itemView.findViewById(R.id.fl_bg);
            _img_wifi_level = itemView.findViewById(R.id.img_wifi_level);
            _tv_wifi_name = itemView.findViewById(R.id.tv_wifi_name);
            _tv_wifi_state = itemView.findViewById(R.id.tv_wifi_state);
        }
    }
}
