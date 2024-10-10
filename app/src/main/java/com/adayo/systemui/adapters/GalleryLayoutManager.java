package com.adayo.systemui.adapters;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 为了点击item使相应的item居中显示，且实现RecyclerView的平缓滑动，重写LinearLayoutManager<P/>
 * @author mailanglideguozhe 20210520
 */
public class GalleryLayoutManager extends LinearLayoutManager {
    public GalleryLayoutManager(Context context) {
        super(context);
    }

    public GalleryLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public GalleryLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 重写smoothScrollToPosition方法，实现平滑滑动及停留在中间位置
     * @param recyclerView
     * @param state
     * @param position 目标位置
     */
    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        RecyclerView.SmoothScroller smoothScroller = new RecyclerviewSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    /**
     * 重写LinearSmoothScroller自定义 中间对齐需要的校正位置
     */
    private static class RecyclerviewSmoothScroller extends LinearSmoothScroller{
        public RecyclerviewSmoothScroller(Context context) {
            super(context);
        }

        /**
         * 为了确保子视图在显示的位置中间位置，需要设置应当校正子视图的位置。<P/>
         * 子视图校正需要移动的距离为Recycler布局中间位置与子视图中间位置的距离<P/>
         * @param viewStart 子视图的左侧位置
         * @param viewEnd 子视图的右侧位置
         * @param boxStart RecyclerView视图的左侧位置
         * @param boxEnd RecyclerView视图的左侧位置
         * @return 返回子视图校正需要移动的距离
         */
        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2);
        }

        /**
         * 计算滑动的速度，返回1px滑动所需的时间，举例 如返回0.8f，即滑动1000个像素点距离需要0.8s<P/>
         * @param displayMetrics
         * @return 返回1px滑动所需的时间，单位ms
         */
        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return super.calculateSpeedPerPixel(displayMetrics);
        }
    }
}
