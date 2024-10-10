package com.adayo.systemui.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.adayo.systemui.adapters.SystemMessageAdapter;
import com.adayo.systemui.adapters.WarningMessageAdapter;

public class SwipeDeleteWarningCallBack extends ItemTouchHelper.Callback {

    private WarningMessageAdapter mAdapter;
    private String TAG = "SwipeDeleteWarningCallBack";

    public SwipeDeleteWarningCallBack(WarningMessageAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    //开启左右滑动和上下滑动
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
//        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swipeFlags = ItemTouchHelper.RIGHT;

        return makeMovementFlags(dragFlags, swipeFlags);
    }

    //上下滑动距离：滑动的距离超过 自身高度*0.5f时 才会调用onMove函数（滑动距离自定义）
    @Override
    public float getMoveThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.5f;
    }

    //上下滑动监听事件
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
//        mAdapter.changeItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    //左右滑动距离：滑动的距离超过 自身宽度*0.3f时 才会调用onSwiped函数（滑动距离自定义）
    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.3f;
    }

    //左右滑动监听事件
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.deleteItem(viewHolder.getAdapterPosition());
    }

    //滑动松手后的动画持续时间
    @Override
    public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
        return 200L;
    }
}