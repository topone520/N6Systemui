package com.adayo.systemui.adapters;

import android.annotation.SuppressLint;
import android.view.FocusFinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.bean.AppInfo;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import java.util.ArrayList;
import java.util.List;

public class ShortcutAppAdapter extends RecyclerView.Adapter<ShortcutAppAdapter.ViewHolder> {

    private List<AppInfo> mAppInfoList = new ArrayList<>();
    private ViewHolder viewHolder;
    private int focusedItem = -1;

    public ShortcutAppAdapter(List<AppInfo> appInfoList) {
        mAppInfoList = appInfoList;
    }

    public void setData(List<AppInfo> appInfoList) {
        mAppInfoList = appInfoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ShortcutAppAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(SystemUIApplication.getSystemUIContext()).inflate(R.layout.item_shortcut_app, parent, false);
        viewHolder = new ViewHolder(inflate);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ShortcutAppAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.mImgShortcutApp.setImageDrawable(mAppInfoList.get(position).getImage());
        holder.mImgShortcutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClickListener(position);
            }
        });
        AAOP_LogUtils.d("ccm------>","onBindViewHolder focusedItem: "+focusedItem);
        if (focusedItem == position){
            holder.itemView.requestFocus();
        }else {
            if(focusedItem >= 0 && focusedItem <= getItemCount()){
                holder.itemView.clearFocus();
            }
        }

    }

    public int getFocusedItem() {
        return focusedItem;
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

    @Override
    public int getItemCount() {
        return mAppInfoList.size();
    }

    public void onClick() {
        if (viewHolder.itemView.isFocused()){
            viewHolder.mImgShortcutApp.performClick();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout mClMain;
        ImageView mImgShortcutApp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mClMain = itemView.findViewById(R.id.cl_main);
            mImgShortcutApp = itemView.findViewById(R.id.img_shortcut_app);
        }
    }

    private onItemClickListen onItemClickListener;

    public void setOnItemClickListener(onItemClickListen onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListen {
        void onItemClickListener(int position);
    }
}
