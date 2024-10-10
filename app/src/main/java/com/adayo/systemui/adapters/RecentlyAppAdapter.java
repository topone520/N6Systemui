package com.adayo.systemui.adapters;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.bean.AppInfo;
import com.adayo.systemui.contents.ConnectDeviceConstant;
import com.adayo.systemui.eventbus.EventContents;
import com.adayo.systemui.eventbus.EventData;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.views.hvac.HvacSpeedSeekBar;
import com.adayo.systemui.windows.views.hvac.VerSelectedView;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class RecentlyAppAdapter extends RecyclerView.Adapter<RecentlyAppAdapter.ViewHolder> {

    private boolean isClick = false;

    private List<AppInfo> list;
    private OnItemClickListener onItemClickListener;

    private ViewHolder viewHolder;
    private int focusedItem = -1;
    private boolean isFocused = false;

    public RecentlyAppAdapter(List<AppInfo> list) {
        this.list = list;
    }

    public RecentlyAppAdapter(List<AppInfo> list, boolean isClik) {

        this.list = list;
        this.isClick = isClik;

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(SystemUIApplication.getSystemUIContext()).inflate(R.layout.item_recently, viewGroup, false);
        viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final int index = i;
        AppInfo info = list.get(i);
        if (TextUtils.isEmpty(info.getAppName())) {
            viewHolder.la_parent.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.la_parent.setVisibility(View.VISIBLE);
            viewHolder.iv_icon.setImageDrawable(info.getImage());
            viewHolder.la_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClickListener(index);
                }
            });

            viewHolder.la_parent.setEnabled(true);
            viewHolder.iv_icon.setEnabled(true);
            if (TextUtils.equals(ConnectDeviceConstant.PM_CAR_PLAY, info.getPackageName())) {
                viewHolder.iv_icon.setImageResource(R.drawable.ivi_home_allapp_carplay_icon);
                Context cxt = viewHolder.itemView.getContext();
                ContentResolver resolver = cxt.getContentResolver();
                boolean enabled = TextUtils.equals(ConnectDeviceConstant.VALUE_CAR_PLAY, Settings.System.getString(resolver, ConnectDeviceConstant.KEY_WIRELESS_DEVICE))
                        || TextUtils.equals(ConnectDeviceConstant.VALUE_CAR_PLAY, Settings.System.getString(resolver, ConnectDeviceConstant.KEY_WIRED_DEVICE));
                LogUtil.i("enabled = " + enabled);
                viewHolder.la_parent.setEnabled(enabled);
                viewHolder.iv_icon.setEnabled(enabled);
            }

            AAOP_LogUtils.d("ccm------>","onBindViewHolder focusedItem: "+focusedItem);
            if (focusedItem == i){
                isFocused = true;
                viewHolder.itemView.requestFocus();
            }else {
                if(focusedItem >= 0 && focusedItem <= getItemCount()){
                    isFocused = false;
                    viewHolder.itemView.clearFocus();
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public boolean getItemFocused() {
        return isFocused;
    }

    public void setItemFocused(boolean focused) {
        isFocused = focused;
    }

    public void moveFocus(boolean moveForward) {
        int nextItem = moveForward ? focusedItem + 1 : focusedItem - 1;
        AAOP_LogUtils.d("ccm------>","moveFocus nextItem: "+nextItem+" ,focusedItem: "+focusedItem);
        if (nextItem >= 0 && nextItem < getItemCount()) {
            notifyItemChanged(focusedItem); // 取消当前焦点
            focusedItem = nextItem;
            notifyItemChanged(focusedItem); // 设置新的焦点
        }
    }

    public void onClick() {
        if (isFocused){
            viewHolder.la_parent.performClick();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_icon;
        LinearLayout la_parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_icon = itemView.findViewById(R.id.iv_recently_icon);
            la_parent = itemView.findViewById(R.id.la_recently_parent);

        }
    }

    public interface OnItemClickListener {

        void onItemClickListener(int position);

    }

}
