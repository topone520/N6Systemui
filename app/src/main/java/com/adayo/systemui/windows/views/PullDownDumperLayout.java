package com.adayo.systemui.windows.views;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.adayo.systemui.utils.HideSoftInputUtil;
import com.adayo.systemui.utils.LogUtil;
import com.adayo.systemui.utils.WindowsUtils;
import com.adayo.systemui.windows.panels.HvacPanel;
import com.android.systemui.SystemUIApplication;

import java.util.jar.Attributes;


/**
 * @author XuYue
 * @description:
 * @date :2021/9/23 13:45
 */
public class PullDownDumperLayout extends RelativeLayout implements View.OnTouchListener, Animator.AnimatorListener {

    private volatile static PullDownDumperLayout pullDownDumperLayout;
    /**
     * 取布局中的第一个子元素为下拉隐藏头部
     */
    private RelativeLayout mHeadLayout;
//    private View mNewLayout;

    private boolean isAnimation = false;
    private OnTouchListener onTouchListener;
    private onLongClickListener onLongClickListener;

    /**
     * 隐藏头部布局的高
     */
    public static final int mHeadLayoutHeight = 720;

    /**
     * 隐藏头部的布局参数
     */
    private MarginLayoutParams mHeadLayoutParams;
//    private MarginLayoutParams mNewLayoutParams;

    /**
     * 判断是否为第一次初始化，第一次初始化需要把headView移出界面外
     */
    private boolean mOnLayoutIsInit = false;

    /**
     * 按下时的y轴坐标
     */
    private float mDownY;

    /**
     * 移动时，前一个坐标
     */
    private float mMoveY;

    private float yVelocity;
    private final int referenceVelocity = 600;

    /**
     * 触发动画的分界线
     */
    private final int mBoundary = 60;

    private final int duration = 300;
    private boolean fromOutSide = false;
    private float alpha = 0f;

    private float mInitialDownY;
    private boolean mIsBeingDragged = false;
    private GestureDetector gestureDetector;
    // 对外暴露禁止滑动接口
    private boolean scrollEnabled = true;

    public static PullDownDumperLayout getInstance() {
        if (pullDownDumperLayout == null) {
            synchronized (HvacPanel.class) {
                if (pullDownDumperLayout == null) {
                    pullDownDumperLayout = new PullDownDumperLayout(SystemUIApplication.getSystemUIContext());
                }
            }
        }
        return pullDownDumperLayout;
    }

    public PullDownDumperLayout(Context context) {
        super(context);
        init();
    }

    public PullDownDumperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                // 处理长按事件
                onLongClickListener.onLongClick();
            }
        });
    }

    /**
     * 布局开始设置每一个控件
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //只初始化一次
        if (!mOnLayoutIsInit && changed) {
            //将第一个子元素作为头部移出界面外
            mHeadLayout = (RelativeLayout) this.getChildAt(0);
            mHeadLayoutParams = (MarginLayoutParams) mHeadLayout.getLayoutParams();
            mHeadLayoutParams.topMargin = -mHeadLayoutHeight;
            mHeadLayout.setLayoutParams(mHeadLayoutParams);
            mHeadLayout.setAlpha(alpha);
//            mNewLayout = this.getChildAt(1);
//            mNewLayoutParams = (MarginLayoutParams) mNewLayout.getLayoutParams();
//            mNewLayoutParams.topMargin = -mHeadLayoutHeight;
//            mNewLayout.setLayoutParams(mNewLayoutParams);
            //TODO 设置手势监听器，不能触碰的控件需要添加android:clickable="true"
//            mHeadLayout.setOnTouchListener(this);
//            mNewLayout.setOnTouchListener(this);
            //标记已被初始化
            mOnLayoutIsInit = true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        LogUtil.d("onInterceptTouchEvent"+scrollEnabled);
        if (!scrollEnabled) {
            return true;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialDownY = ev.getRawY();
                mIsBeingDragged = false;
                return false;  // Allow initial touch to pass through
            case MotionEvent.ACTION_MOVE:
                float deltaY = ev.getRawY() - mInitialDownY;
                if (!mIsBeingDragged && Math.abs(deltaY) > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    mIsBeingDragged = true;
                    requestDisallowInterceptTouchEvent(true);
                    return doAnimation(ev, true);
                }
                break;
        }
        return false;
    }

    /**
     * 屏幕触摸操作监听器
     *
     * @return 返回false表示在执行onTouch后会继续执行onTouchEvent
     **/

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        LogUtil.d("onTouch"+scrollEnabled);
        if (!scrollEnabled) {
            return false;
        }
        if (MotionEvent.ACTION_DOWN==event.getAction()){

            HideSoftInputUtil.hideSoftInput(this.getWindowToken());
        }
        if(fromOutSide){
            return false;
        }
        return doAnimation(event, false);
    }

    private int movingDistance = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.d("onTouchEvent"+scrollEnabled);
        if (!scrollEnabled) {
            return false;
        }
         gestureDetector.onTouchEvent(event); // 将事件传递给GestureDetector处理长按事件
//        this.onTouchListener.onTouchEvent(event);
//
//        if (MotionEvent.ACTION_DOWN==event.getAction()){
//
//            HideSoftInputUtil.hideSoftInput(this.getWindowToken());
//        }
//        if(fromOutSide){
//            return false;
//        }
        return doAnimation(event, false);
    }

    public boolean doAnimation(MotionEvent event, boolean fromOutside){
        if (!mOnLayoutIsInit || null == mHeadLayout) {

            return super.onTouchEvent(event);
        }
        fromOutSide = fromOutside;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = event.getRawY();
                mMoveY = mDownY;
                break;
            case MotionEvent.ACTION_MOVE:
                VelocityTracker mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(100);
//                if(0 != mVelocityTracker.getYVelocity()) {
//                    yVelocity = mVelocityTracker.getYVelocity();//去绝对值。向左滑，值为负数
//                }
                float currY = event.getRawY();
                int vector = (int) (currY - mMoveY);//向量，用于判断手势的上滑和下滑
                mMoveY = currY;
                //判断是否为滑动
                if(isOpen && mHeadLayout.getTranslationY() >= mHeadLayoutHeight && vector > 0){
                    return false;
                }
                if (Math.abs(vector) == 0 || movingDistance >= mHeadLayoutHeight*1.15f) {
                    return false;
                }
                movingDistance = movingDistance + vector;
                if (isOpen) {
                    alpha = (mHeadLayoutHeight + movingDistance)*1.0f/mHeadLayoutHeight;
                    mHeadLayout.setTranslationY(mHeadLayoutHeight + movingDistance);
                    mHeadLayout.setAlpha(alpha);
//                    mNewLayout.setTranslationY(mHeadLayoutHeight + movingDistance);
                } else {
                    alpha = movingDistance*1.0f/mHeadLayoutHeight;
                    mHeadLayout.setTranslationY(movingDistance);
                    mHeadLayout.setAlpha(alpha);
//                    mNewLayout.setTranslationY(movingDistance);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (yVelocity > referenceVelocity) {
                    if (isOpen) {
                        startAnimation(mHeadLayoutHeight + movingDistance, mHeadLayoutHeight);
                    } else {
                        startAnimation(movingDistance, mHeadLayoutHeight);
                    }
                    setOpen(true);
                    break;
                }

                if (yVelocity < -referenceVelocity) {
                    if (isOpen) {
                        startAnimation(mHeadLayoutHeight + movingDistance, 0);
                    } else {
                        startAnimation(movingDistance, 0);
                    }
                    setOpen(false);
                    break;
                }

                if (Math.abs(movingDistance) >= mBoundary) {
                    if (event.getRawY() - mDownY < 0) {
                        if (isOpen) {
                            startAnimation(mHeadLayoutHeight + movingDistance, 0);
                        } else {
                            startAnimation(movingDistance, 0);
                        }
                        setOpen(false);
                    } else {
                        if (isOpen) {
                            startAnimation(mHeadLayoutHeight + movingDistance, mHeadLayoutHeight);
                        } else {
                            startAnimation(movingDistance, mHeadLayoutHeight);
                        }
                        setOpen(true);
                    }
                } else {
                    if (isOpen) {
                        startAnimation(mHeadLayoutHeight + movingDistance, mHeadLayoutHeight);
                    } else {
                        startAnimation(movingDistance, 0);
                    }
                }

            case MotionEvent.ACTION_CANCEL:
                if (yVelocity > referenceVelocity) {
                    if (isOpen) {
                        startAnimation(mHeadLayoutHeight + movingDistance, mHeadLayoutHeight);
                    } else {
                        startAnimation(movingDistance, mHeadLayoutHeight);
                    }
                    setOpen(true);
                    break;
                }

                if (yVelocity < -referenceVelocity) {
                    if (isOpen) {
                        startAnimation(mHeadLayoutHeight + movingDistance, 0);
                    } else {
                        startAnimation(movingDistance, 0);
                    }
                    setOpen(false);
                    break;
                }

                if (Math.abs(movingDistance) >= mBoundary) {
                    if (event.getRawY() - mDownY < 0) {
                        if (isOpen) {
                            startAnimation(mHeadLayoutHeight + movingDistance, 0);
                        } else {
                            startAnimation(movingDistance, 0);
                        }
                        setOpen(false);
                    } else {
                        if (isOpen) {
                            startAnimation(mHeadLayoutHeight + movingDistance, mHeadLayoutHeight);
                        } else {
                            startAnimation(movingDistance, mHeadLayoutHeight);
                        }
                        setOpen(true);
                    }
                } else {
                    if (isOpen) {
                        startAnimation(mHeadLayoutHeight + movingDistance, mHeadLayoutHeight);
                    } else {
                        startAnimation(movingDistance, 0);
                    }
                }
                break;
            default:
                break;
        }


        return true;
    }

    private boolean isOpen = false;

    @Override
    public void onAnimationStart(Animator animation) {
        isAnimation = true;
    }

    @Override
    public void onAnimationStart(Animator animation,boolean isReverse) {
        this.onAnimationStart(animation);
    }

    @Override
    public void onAnimationEnd(Animator animation,boolean isReverse) {
        this.onAnimationEnd(animation);

    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (!isOpen) {
            mHeadLayout.setAlpha(0.0f);
            WindowsUtils.dismissQsPanel();
        }else{
//            StatusBar.getInstance().setVisibility(View.GONE);
        }
        fromOutSide = false;
        movingDistance = 0;
        isAnimation = false;
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        fromOutSide = false;
        movingDistance = 0;
        isAnimation = false;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }

    public void startOpenAnimation(){
        startAnimation(0, mHeadLayoutHeight);
        setOpen(true);
    }
    public void startCloseAnimation(){
        startAnimation(mHeadLayoutHeight, 0);
        setOpen(false);
    }

    private void startAnimation(int from, int to) {
        if(isAnimation){
            return;
        }
        if (null != mHeadLayout) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(mHeadLayout, "translationY", from, to);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(duration);
            animator.addListener(this);
            animator.start();
            ObjectAnimator alphaAnimator  = ObjectAnimator.ofFloat(mHeadLayout, "alpha", alpha, to == 0 ? 0f : 1f);
            alphaAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            alphaAnimator.setDuration(duration);
            alphaAnimator.start();
            alpha = to == 0 ? 0f : 1f;
        }
//        if (null != mNewLayout) {
//            ObjectAnimator animator = ObjectAnimator.ofFloat(mNewLayout, "translationY", from, to);
//            animator.setInterpolator(new AccelerateDecelerateInterpolator());
//            animator.setDuration(duration);
//            animator.addListener(this);
//            animator.start();
//        }
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    public interface OnTouchListener{
        void onTouchEvent(MotionEvent event);
    }

    public void setOnLongClickListener(onLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public interface onLongClickListener{
        void onLongClick();
    }

    public void setOpen(boolean open) {
        isOpen = open;
//        ADSBusManager.getInstance().sendQs(isOpen);

    }

    public boolean isOpen(){
        return isOpen;
    }

    /**
     * 设置是否允许滑动
     * @param enabled true表示允许滑动，false表示禁止滑动
     */
    public void setScrollEnabled(boolean enabled) {
        scrollEnabled = enabled;
    }
}