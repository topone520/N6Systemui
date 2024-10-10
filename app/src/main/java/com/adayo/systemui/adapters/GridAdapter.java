package com.adayo.systemui.adapters;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.bean.AppInfo;
import com.android.systemui.R;

import java.util.List;

public class GridAdapter extends BaseAdapter {
    private static final String TAG = GridAdapter.class.getSimpleName();
    private List<AppInfo> data;

    public GridAdapter(List<AppInfo> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.item_allapp, parent, false);

            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AppInfo appInfo = data.get(position);

        viewHolder._iv_icon.setImageDrawable(appInfo.getImage());
        viewHolder._tv_name.setText(appInfo.getAppName());

        // 设置长按事件
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mOnItemDragListener != null) {
                    mOnItemDragListener.onItemDrag(viewHolder);
                    // 设置拖拽的数据
                    Intent intent = new Intent();
                    intent.putExtra("appView", "rv_shortcut_edit_app");
                    intent.putExtra("appName", appInfo.getAppName());
                    intent.putExtra("position", position);
                    AAOP_LogUtils.d(TAG, "position: " + position);
                    ClipData dataItem = new ClipData("label", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, new ClipData.Item(intent));
                    view.startDragAndDrop(dataItem, new View.DragShadowBuilder(view), null, View.DRAG_FLAG_GLOBAL);
                }
                return true;
            }
        });

        return convertView;
    }

    // ViewHolder 类
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView _iv_icon;
        TextView _tv_name;

        public ViewHolder(View itemView) {
            super(itemView);
            _iv_icon = itemView.findViewById(R.id.iv_icon);
            _tv_name = itemView.findViewById(R.id.tv_name);
        }
    }


    private OnItemDragListener mOnItemDragListener;

    public void setOnItemDragListener(OnItemDragListener onItemDragListener) {
        mOnItemDragListener = onItemDragListener;
    }

    public interface OnItemDragListener {
        void onItemDrag(RecyclerView.ViewHolder viewHolder);
    }


}