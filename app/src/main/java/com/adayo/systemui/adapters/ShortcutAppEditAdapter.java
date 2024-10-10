package com.adayo.systemui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.systemui.bean.AppInfo;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import java.util.ArrayList;
import java.util.List;

public class ShortcutAppEditAdapter extends RecyclerView.Adapter<ShortcutAppEditAdapter.ViewHolder>{

    private List<AppInfo> mAppInfoList = new ArrayList<>();

    public ShortcutAppEditAdapter(List<AppInfo> appInfoList) {
        mAppInfoList = appInfoList;
    }

    @NonNull
    @Override
    public ShortcutAppEditAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(SystemUIApplication.getSystemUIContext()).inflate(R.layout.item_shortcut_app_edit, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ShortcutAppEditAdapter.ViewHolder holder, int position) {
        int start = position * 10;
        int end = Math.min(start + 10, mAppInfoList.size());
        if (start < end){
            List<AppInfo> pageData = mAppInfoList.subList(start, end);
            holder.bind(pageData);
        }
    }

    @Override
    public int getItemCount() {
        return (int) Math.ceil((double) mAppInfoList.size() / 10);
    }

    public void setData(List<AppInfo> appInfo) {
        mAppInfoList = appInfo;
        notifyDataSetChanged();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        public GridView _grid_view;
        private GridAdapter gridAdapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _grid_view = itemView.findViewById(R.id.gridView);
        }

        public void bind(List<AppInfo> pageData) {
            GridAdapter gridAdapter = new GridAdapter(pageData);
            _grid_view.setAdapter(gridAdapter);
            gridAdapter.setOnItemDragListener(viewHolder -> {
                mOnItemDragListener.onItemDrag(viewHolder);
            });
        }
    }

    private OnItemDragListener mOnItemDragListener;
    public void setOnItemDragListener(OnItemDragListener onItemDragListener){
        mOnItemDragListener = onItemDragListener;
    }

    public interface OnItemDragListener {
        void onItemDrag(RecyclerView.ViewHolder viewHolder);
    }

}
