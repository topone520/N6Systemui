package com.adayo.systemui.adapters;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.ParcelFileDescriptor;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.bean.AppInfo;
import com.adayo.systemui.contents.ConnectDeviceConstant;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.windows.views.OnItemPositionListener;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import java.io.FileDescriptor;
import java.util.List;

public class AllAppAdapter extends RecyclerView.Adapter<AllAppAdapter.ViewHolder> implements OnItemPositionListener {

    private boolean isClick = false;

    private List<AppInfo> list;
    private OnItemClickListener onItemClickListener;

    private OnItemLongClickListener onItemLongClickListener;

    private OnDeleteClickListener onDeleteClickListener;
    private ViewHolder viewHolder;
    private int focusedItem = -1;
    private boolean isFocused = false;

    public AllAppAdapter(List<AppInfo> list) {
        this.list = list;
    }

    public AllAppAdapter(List<AppInfo> list, boolean isClik) {

        this.list = list;
        this.isClick = isClik;

    }

    public void setAppList(List<AppInfo> appList) {
        list = appList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(SystemUIApplication.getSystemUIContext()).inflate(R.layout.item_allapp, viewGroup, false);
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
            if (info.getImage() == null){
                ParcelFileDescriptor iconDesc = info.getIconDesc();
                if (iconDesc == null){
                    return;
                }
                FileDescriptor fileDescriptor = iconDesc.getFileDescriptor();

                Bitmap bm = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                viewHolder.iv_icon.setImageBitmap(bm);
            }else {
                viewHolder.iv_icon.setImageDrawable(info.getImage());
            }
            viewHolder.tv_name.setText(info.getAppName());
            viewHolder.delete_icon.setVisibility(info.getIsShowDelete() ? View.VISIBLE : View.GONE);
            viewHolder.la_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.d("adapter:onItemClickListener: "+index);
                    onItemClickListener.onItemClickListener(index);
                }
            });
            viewHolder.la_parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    LogUtil.d("AppDelete----onLongClick");
                    onItemLongClickListener.onItemLongClickListener();
                    return false;
                }
            });
            viewHolder.delete_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteClickListener.onDeleteClickListener(index);
                }
            });
            viewHolder.la_parent.setEnabled(info.getIsCanClick());
            viewHolder.iv_icon.setEnabled(info.getIsCanClick());
            if (TextUtils.equals(ConnectDeviceConstant.PM_CAR_PLAY, info.getPackageName())) {
                Context cxt = viewHolder.itemView.getContext();
                ContentResolver resolver = cxt.getContentResolver();
                boolean enabled = TextUtils.equals(ConnectDeviceConstant.VALUE_CAR_PLAY, Settings.System.getString(resolver, ConnectDeviceConstant.KEY_WIRELESS_DEVICE))
                        || TextUtils.equals(ConnectDeviceConstant.VALUE_CAR_PLAY, Settings.System.getString(resolver, ConnectDeviceConstant.KEY_WIRED_DEVICE));
                LogUtil.i("enabled = "+enabled);
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

    @Override
    public void onItemSwap(int from, int target) {
        AppInfo appInfo = list.get(from);
        list.remove(from);
        list.add(target, appInfo);
        notifyItemMoved(from, target);
    }

    @Override
    public void onItemMoved(int position) {

    }

    @Override
    public void onItemSwapEnd() {
        notifyDataSetChanged();
    }

    @Override
    public void onStartMoved(RecyclerView.ViewHolder viewHolder) {

    }
    public int getFocusedItem() {
        return focusedItem;
    }
    public boolean getItemFocused() {
        return isFocused;
    }
    public void setItemFocused(boolean focused) {
        isFocused = focused;
    }

    public void moveFocus(int focusType) {
        AAOP_LogUtils.d("ccm------>","moveFocus: ");
        switch (focusType){
            case View.FOCUS_DOWN:
                if (focusedItem < 0){
                    break;
                }
                focusedItem = focusedItem + 1;
                notifyItemChanged(focusedItem);
                break;
            case View.FOCUS_UP:
                if (focusedItem < 0){
                    break;
                }
                focusedItem = focusedItem - 1;
                notifyItemChanged(focusedItem);
                break;
            case View.FOCUS_LEFT:
                if (focusedItem < 0){
                    break;
                }
                focusedItem = focusedItem - 2;
                notifyItemChanged(focusedItem);
                break;
            case View.FOCUS_RIGHT:
                if (focusedItem < 0){
                    focusedItem = 0;
                    notifyItemChanged(focusedItem);
                    break;
                }
                focusedItem = focusedItem + 2;
                notifyItemChanged(focusedItem);
                break;
        }
    }

    public void onClick() {
        if (isFocused){
            viewHolder.la_parent.performClick();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        LinearLayout la_parent;
        ImageView delete_icon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            la_parent = itemView.findViewById(R.id.la_parent);
            delete_icon = itemView.findViewById(R.id.delete_icon);
        }
    }



    public interface OnItemClickListener {

        void onItemClickListener(int position);

    }

    public interface OnItemLongClickListener {
        void onItemLongClickListener();
    }

    public interface OnDeleteClickListener {
        void onDeleteClickListener(int position);
    }
}
