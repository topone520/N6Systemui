package com.adayo.systemui.adapters;


import android.view.ViewGroup;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerViewAdapters<T> extends RecyclerView.Adapter<BaseRecyclerHolderView> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;

    private List<T> mData = new ArrayList<>();
    private final int layoutId;
    private boolean hasHeaderView;
    private int headerLayoutId;

    public void setHeaderView(int headerLayoutId) {
        this.headerLayoutId = headerLayoutId;
        hasHeaderView = true;
        notifyItemInserted(0);
    }

    @Override
    public int getItemViewType(int position) {
        if (!hasHeaderView) {
            return TYPE_NORMAL;
        }
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;
    }

    public int getDataPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return !hasHeaderView ? position : position - 1;
    }

    public BaseRecyclerViewAdapters(int layoutId) {
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public BaseRecyclerHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (hasHeaderView && viewType == TYPE_HEADER) {
            return new BaseRecyclerHolderView(AAOP_HSkin.getLayoutInflater(parent.getContext()).inflate(headerLayoutId, parent, false));
            //return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(headerLayoutId, parent, false));
        }
        //return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
        return new BaseRecyclerHolderView(AAOP_HSkin.getLayoutInflater(parent.getContext()).inflate(layoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerHolderView holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            bindView(holder, null, position);
        } else {
            int dataPos = getDataPosition(holder);
            bindView(holder, mData.get(dataPos), position);
        }
    }

    @Override
    public int getItemCount() {
        return !hasHeaderView ? mData.size() : mData.size() + 1;
    }

    protected abstract void bindView(BaseRecyclerHolderView holder, T data, int itemPos);

    public List<T> getData() {
        return mData;
    }

    public void addAll(List<T> data) {
        this.mData.addAll(data);
    }

    public void clear() {
        this.mData.clear();
        this.notifyDataSetChanged();
    }

    public void addData(int position, T data) {
        int itemPos = position;
        if (hasHeaderView) {
            itemPos += 1;
        }
        this.mData.add(position, data);
        this.notifyItemRangeInserted(itemPos, 1);
    }

    public void addData(T data) {
        int itemPos = this.mData.size();
        if (hasHeaderView) {
            itemPos += 1;
        }
        this.mData.add(data);
        this.notifyItemRangeInserted(itemPos, 1);
    }

    public void addData(List<T> data) {
        int itemPos = this.mData.size();
        if (hasHeaderView) {
            itemPos += 1;
        }
        this.mData.addAll(data);
        this.notifyItemRangeInserted(itemPos, data.size());
    }

    public void addData(int position, List<T> data) {
        int itemPos = this.mData.size();
        if (hasHeaderView) {
            itemPos += 1;
        }
        this.mData.addAll(position, data);
        this.notifyItemRangeInserted(itemPos, data.size());
    }

    public void setNewData(List<T> data) {
        this.mData.clear();
        this.mData.addAll(data == null ? new ArrayList<>() : data); //= data == null ? new ArrayList<>() : data;
        this.notifyDataSetChanged();
    }

    public void removeData(@IntRange(from = 0L) int position) {
        int itemPos = position;
        if (hasHeaderView) {
            itemPos += 1;
        }
        if (this.mData != null && this.mData.size() > 0 && position < this.mData.size()) {
            this.mData.remove(position);
            this.notifyItemRemoved(itemPos);
            this.notifyItemRangeChanged(itemPos, getItemCount() - itemPos);
        }
    }
}
