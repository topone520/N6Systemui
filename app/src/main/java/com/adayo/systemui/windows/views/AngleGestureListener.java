package com.adayo.systemui.windows.views;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class AngleGestureListener extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 50; // 调整滑动敏感度
    private OnAngleChangeListener onAngleChangeListener;

    public interface OnAngleChangeListener {
        void onAngleChange(int angle, int type);
    }

    public AngleGestureListener(Context context, OnAngleChangeListener listener) {
        onAngleChangeListener = listener;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float dx = e2.getX() - e1.getX();
        float dy = e2.getY() - e1.getY();

        // 计算角度
        double angle = Math.atan2(dy, dx) * 180 / Math.PI;

        // 将角度转为正值
        if (angle < 0) {
            angle += 360;
        }

        // 将角度转为象限 90 度内的值
        int quadrant = (int) (angle / 90);

        // 计算十份的角度范围
        int angleSegment = 360 / 10;

        // 计算在十份中的位置
        int positionInSegment = (int) ((angle % 90) / angleSegment);

        // 计算最终的角度值
        int finalAngle = quadrant * 90 + positionInSegment * angleSegment;

        // 计算类型（type），这里简单地使用 positionInSegment + 1 作为类型值
        int type = positionInSegment + 1;

        // 回调监听器
        if (onAngleChangeListener != null) {
            onAngleChangeListener.onAngleChange(finalAngle, type);
        }

        return super.onFling(e1, e2, velocityX, velocityY);
    }
}

