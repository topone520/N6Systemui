package com.adayo.systemui.windows.views;


import androidx.recyclerview.widget.RecyclerView;

public interface OnItemPositionListener {
    //交换
    void onItemSwap(int from, int target);

    //滑动
    void onItemMoved(int position);

    //停止交换
    void onItemSwapEnd();


    //开始拖拽
    void onStartMoved(RecyclerView.ViewHolder viewHolder);
}
