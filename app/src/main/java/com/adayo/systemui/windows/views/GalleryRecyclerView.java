package com.adayo.systemui.windows.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class GalleryRecyclerView extends RecyclerView {
    /**
     * 相邻卡片之间的间隔
     */
    private int mIntervalDistance = 0;
    private float mSelectedScale = 0.5f;

    public GalleryRecyclerView(@NonNull Context context) {
        super(context);
    }

    public GalleryRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GalleryRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getIntervalDistance() {
       return mIntervalDistance;
    }

    @Override
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int childWidth = child.getWidth() - child.getPaddingLeft() - child.getPaddingRight();
        int childHeight = child.getHeight() - child.getPaddingTop() - child.getPaddingBottom();
        int width = getWidth();
        if(width <= child.getWidth()){
            return super.drawChild(canvas, child, drawingTime);
        }
        int pivot = (width - childWidth)/2;
        int x = child.getLeft();
        float scale , alpha;
        //alpha = 1 - 0.6f*Math.abs(x - pivot)/pivot;
        if(x <= pivot){
            scale = 2*(1-mSelectedScale)*(x+childWidth) / (width+childWidth) + mSelectedScale;
        }else{
            scale = 2*(1-mSelectedScale)*(width - x) / (width+childWidth) + mSelectedScale;
        }
        child.setPivotX(childWidth / 2);
        child.setPivotY(childHeight / 2);
        child.setScaleX(scale);
        child.setScaleY(scale);
        //child.setAlpha(alpha);
        return super.drawChild(canvas, child, drawingTime);
    }

    //    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
//        if(mIntervalDistance <= 0){
//            View childView = getChildAt(0);
//            mIntervalDistance = childView.getMeasuredWidth()/2;
//        }
//    }
//
//
//
//    /**
//     * 重写Fling逻辑使居中对齐
//     * @param velocityX
//     * @param velocityY
//     * @return
//     */
//    @Override
//    public boolean fling(int velocityX, int velocityY) {
//        /**
//         * 缩小滑动距离
//         */
//        int flingx = (int)(velocityX*0.4f);
//        double distance = getSplineFlingDistance(velocityX);
//
//        return super.fling(velocityX, velocityY);
//    }
//
//    private double getSplineFlingDistance(int velocity){
//        final double l = getSplineFlingDistance(velocity);
//        final double decelMinusOne = 2 - 1.0;
//        return mFlingFriction*getPhysicalCoeff()*Math.exp(2/decelMinusOne*1);
//    }
//
//    private static final float INFLEXTION = 0.35f;
//    private float mFlingFriction = ViewConfiguration.getScrollFriction();
//    private double getDeceleration(int velocity){
//        final float ppi = this.getResources().getDisplayMetrics().density*160f;
//        float mPhysicalCoeff = SensorManager.GRAVITY_EARTH //g(m/s^2)
//                *39.37f//inch/meter
//                *ppi
//                *0.84f;
//        return Math.log(INFLEXTION*Math.abs(velocity))/(mFlingFriction*mPhysicalCoeff);
//    }
//
//    private float getPhysicalCoeff(){
//        final float ppi = this.getResources().getDisplayMetrics().density*160f;
//        float mPhysicalCoeff = SensorManager.GRAVITY_EARTH //g(m/s^2)
//                *39.37f//inch/meter
//                *ppi
//                *0.84f;
//        return mPhysicalCoeff;
//    }
}
