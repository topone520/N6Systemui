package com.adayo.systemui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseRecyclerHolderView> {
    /**
     * 说明是带有Header的
     */
    public static final int TYPE_HEADER = 1;
    /**
     * 说明是不带有header
     */
    public static final int TYPE_NORMAL = 0;

    protected Context mContext;
    protected List<T> mList;
    protected OnItemOnClickListener<T> itemOnClickListener;
    protected View mHeaderView;

    protected abstract void showOnBindViewHolder(BaseRecyclerHolderView holder, int position);

    protected abstract BaseRecyclerHolderView showOnCreateViewHolder(ViewGroup parent, int viewType);

    protected abstract void setHeaderView(View headerView);

    public BaseRecyclerViewAdapter(Context mContext, List<T> mList) {
        this.mContext = mContext;
        if (mList != null) {
            this.mList = mList;
        }
    }

    @NonNull
    @Override
    public BaseRecyclerHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return showOnCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerHolderView holder, int position) {
        showOnBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null) {
            return mList.size();
        } else {
            return mList.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            //第一个item应该加载Header
            return TYPE_HEADER;
        }

        return TYPE_NORMAL;
    }

    /**
     * 添加一个集合
     *
     * @param data
     */
    @SuppressLint("NotifyDataSetChanged")
    public void addItemList(List<T> data) {
//        mList.addAll(data);
//        mList.clear();
        mList = data;
        notifyDataSetChanged();
    }

    /**
     * 添加一个对象
     *
     * @param data
     */
    public void addItemObject(T data) {
        mList.add(data);
        notifyItemInserted(mList.size() - 1);
    }

    /**
     * 添加一个对象，插入位置
     *
     * @param position
     * @param data
     */
    public void addItemObject(int position, T data) {
        mList.add(position, data);
        notifyItemInserted(position);
    }

    /**
     * 根据下标删除
     *
     * @param position
     */
    public void removeItem(int position) {
        mList.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 删除一个对象
     *
     * @param data
     */
    @SuppressLint("NotifyDataSetChanged")
    public void removeItem(T data) {
        if (data != null) {
            mList.remove(data);
            notifyDataSetChanged();
        }
    }

    /**
     * 删除所有数据
     */
    @SuppressLint("NotifyDataSetChanged")
    public void removeAll() {
        mList.clear();
        notifyDataSetChanged();
    }

    /**
     * 修改数据中某一个条数据
     *
     * @param position
     * @param data
     */
    public void updateItem(int position, T data) {
        mList.set(position, data);
        notifyItemChanged(position);
    }

    public void setOnItemClickListener(OnItemOnClickListener<T> itemListener) {
        this.itemOnClickListener = itemListener;
    }

    public interface OnItemOnClickListener<T> {
        //点击item
        void onItemClick(View view, int position, T data);

        //长按点击事件
        void onLongItemClick(View view, int position, T data);
    }
}
