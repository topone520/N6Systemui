package com.adayo.systemui.adapters;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.bean.AppInfo;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import java.util.ArrayList;
import java.util.List;

public class ShortcutAppChildAdapter extends RecyclerView.Adapter<ShortcutAppChildAdapter.ViewHolder> {
    private static final String TAG = ShortcutAppChildAdapter.class.getSimpleName();
    private List<AppInfo> mAppInfoList = new ArrayList<>();


    public ShortcutAppChildAdapter(List<AppInfo> appInfoList) {
        mAppInfoList = appInfoList;
    }

    @NonNull
    @Override
    public ShortcutAppChildAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(SystemUIApplication.getSystemUIContext()).inflate(R.layout.item_shortcut_app, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ShortcutAppChildAdapter.ViewHolder holder, int position) {
        int pos = position;
        AppInfo appInfo = mAppInfoList.get(position);
        holder.mImgShortcutApp.setImageDrawable(appInfo.getImage());
        holder.mImgShortcutApp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemDragListener != null) {
                    mOnItemDragListener.onItemDrag(holder);
                    Intent intent = new Intent();
                    intent.putExtra("appView", "rv_shortcut_app");
                    intent.putExtra("appName", appInfo.getAppName());
                    intent.putExtra("position", pos);
                    AAOP_LogUtils.d(TAG, "pos: " + pos);
                    ClipData dataItem = new ClipData("label", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, new ClipData.Item(intent));
                    v.startDragAndDrop(dataItem, new View.DragShadowBuilder(v), null, View.DRAG_FLAG_GLOBAL);
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAppInfoList.size();
    }

    public void setData(List<AppInfo> shortcutDockNoneApp) {
        mAppInfoList = shortcutDockNoneApp;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImgShortcutApp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImgShortcutApp = itemView.findViewById(R.id.img_shortcut_app);
        }
    }

    private OnItemDragListener mOnItemDragListener;

    public void setOnItemDragListener(OnItemDragListener onItemDragListener) {
        mOnItemDragListener = onItemDragListener;
    }

    // 接口用于通知拖拽事件
    public interface OnItemDragListener {
        void onItemDrag(RecyclerView.ViewHolder viewHolder);
    }
}
