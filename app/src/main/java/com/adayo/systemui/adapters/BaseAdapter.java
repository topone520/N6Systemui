package com.adayo.systemui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.android.systemui.R;

import java.util.List;

/**
 * 作为RecylerView Adapter的基类<P/>
 * @param <T>继承时需要指定数据类型
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<BaseAdapter.ViewHolder> {
    private List<T> dataList;

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public List<T> getDataList() {
        return dataList;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * 创建列表子视图和ViewHolder
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getId(),null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseAdapter.ViewHolder holder, int position) {
        if(null != dataList && null != dataList.get(position)){
            setData(holder , position);
        }
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull BaseAdapter.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        setData(holder , position);
    }

    /**
     * 抽象方法用于外部实现视图内容的实例化和赋值
     * @param holder
     * @param pos
     */
    public abstract void setData(ViewHolder holder , int pos);

    /**
     * 抽象方法用于外部指定子视图的布局id
     * @return
     */
    public abstract int getId();

    public abstract boolean areItemsTheSame(@NonNull Picture oldItem , @NonNull Picture newItem);

    public abstract boolean areContentsTheSame(@NonNull Picture oldItem , @NonNull Picture newItem);

    /**
     * DIFF可用于分析列表数据的变化，避免全部子视图的刷新，可节省子视图更新时的CPU消耗
     */
    private static DiffUtil.ItemCallback DIFF_CALLBACK = new DiffUtil.ItemCallback<Picture>() {
        @Override
        public boolean areItemsTheSame(@NonNull Picture oldItem, @NonNull Picture newItem) {
            return areItemsTheSame(oldItem,newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Picture oldItem, @NonNull Picture newItem) {
            return areContentsTheSame(oldItem,newItem);
        }
    };

    /**
     * 子视图的Holder，由标题和图片构成
     */
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageView;
        public TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
            textView = itemView.findViewById(R.id.name);
        }
    }

    /**
     * 注册监听的接口
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 定义点击事件的监听
     */
    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void onItemClick(int pos);
    }
}

