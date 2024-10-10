package com.adayo.systemui.windows.views;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class MyTouchHelperCallback extends ItemTouchHelper.Callback {

    private OnItemPositionListener mItemPositionListener;
    private Context mContext;

    public MyTouchHelperCallback(OnItemPositionListener itemPositionListener, Context context) {
        mItemPositionListener = itemPositionListener;
        mContext = context;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlag;
        int swipeFlags;
        //如果是表格布局，则可以上下左右的拖动，但是不能滑动
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            swipeFlags = 0;
        } else {
            dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        }

        //通过makeMovementFlags生成最终结果
        return makeMovementFlags(dragFlag, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //被拖动的item位置
        int fromPosition = viewHolder.getLayoutPosition();
        //他的目标位置
        int targetPosition = target.getLayoutPosition();
        //为了降低耦合，使用接口让Adapter去实现交换功能
        mItemPositionListener.onItemSwap(fromPosition, targetPosition);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //为了降低耦合，使用接口让Adapter去实现交换功能
        mItemPositionListener.onItemMoved(viewHolder.getLayoutPosition());
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        //当开始拖拽的时候
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {//添加震动
//            viewHolder.itemView.setBackground(mContext.getResources().getDrawable(R.drawable.home_slip_p_bg));
            mItemPositionListener.onStartMoved(viewHolder);
            viewHolder.itemView.setScaleX(1.05f);
            viewHolder.itemView.setScaleY(1.05f);
//            viewHolder.itemView.setRotation(0.9f);
//            viewHolder.itemView.setAlpha(0.8f);
//            viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
//            Vibrator vibrator = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);
//            vibrator.vibrate(50);
        }
    }

    //当手指松开的时候
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);

        viewHolder.itemView.setScaleX(1f);
        viewHolder.itemView.setScaleY(1f);

        mItemPositionListener.onItemSwapEnd();
        super.clearView(recyclerView, viewHolder);
    }

    //禁止长按滚动交换，需要滚动的时候使用{@link ItemTouchHelper#startDrag(ViewHolder)}
    @Override
    public boolean isLongPressDragEnabled() {
//        return false;
        return true;
    }
}

