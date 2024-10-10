package com.adayo.systemui.windows.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.adayo.systemui.contents.HvacSingleton;
import com.android.systemui.R;


/**
 * ACTION：
 * dock栏快速上划：
 * dock栏慢速上划：
 * 页面内点击：
 * 下划：
 * 下划又快速非显示非禁止区域上划：
 */
public class VtpMyScrollerView extends ConstraintLayout {
    private String TAG = "ScrollerView";
    GestureDetector gestureDetector;
    OnTouchListener onTouchListener;
    /**
     * downY：手指按下时距离View顶部的距离
     * moveY：手指在屏幕上滑动的距离（不停变化）
     * movedY：手指在屏幕上总共滑动的距离（为了确定手指一共滑动了多少距离，不能超过可滑动的最大距离）:底部为0,顶部为980
     */
    private int downY, moveY, movedY;

    private View mBgChild;
    //子View
    private View mChild;

    private Scroller mScroller;

    //可滑动的最大距离
    private int mScrollHeight = 960;

    //子View是否在顶部
    private boolean isTop = false;

    private boolean isFinalMovingUp = true; // 手指抬起前是想要上拉/下拉

    //最初子View在View内可见的高度
    private float visibilityHeight;

    //定义左右距离不可滑动X轴   上拉下滑定义的复用X轴 左右
    private int touchLimitationX = 0;
    // 触发划动的区域高度限制 Y轴手势触发高度  上边距和下边距  也是复用，可以更改，上一   下一
    private int touchLimitationY = 780; // 780

    // 上滑操作手指松开时，移动距离小于 moveLimitationY_up 则收起
    private int moveLimitationY_up = 100;
    // 下滑操作手指松开时，移动距离大于 mScrollHeight - moveLimitationY_down 则收起
    private int moveLimitationY_down = 100;// todo


    private float yVelocity;

    private final int referenceVelocity = 500; // 快速滑动临界值，调灵敏度
    public static final int DIALOG_AUTO_HIDE_DURATION_TEN_SECONDS = 10000; // 空调页面自动收起时间判断

    private final int VISIBILITY_NOTIFY_TYPE = 1001;
    private final int DISAPPEAR_DURATION_NOTIFY_TYPE = 1002;
    private final int DISAPPEAR_DURATION_AUTO_HIDE_TYPE = 1003;
    private boolean isInPullDownDuration = false; // 不在pageHideDuration时间内，空调页面恢复为默认page
    private int pageHideDuration = 60000; // 页面已收起时间判断：页面收起后，pageMemoryDuration后页面需恢复为其它状态时使用
    // 父view是否为：不拦截，也不响应（效果：在非touchLimition区域不触发上下划动页面）
    // isContinueWithNoAction = true 可左右滑
    private boolean isContinueWithNoAction = false;
    private boolean isContinueOutsideWithNoAction = false;

    /**
     * 上下划动动效
     * 1.上划: 980px用时500ms
     * (1)快速上划: 500ms
     * (2)慢速上划:
     * 速度"固定",时间根据松手时剩余移动距离计算：t=s/(980/500)
     * 移动速度整体有线性变化,后200ms速度减缓为0
     * (3)无透明度变化:控件层
     * (4)有透明度变化:
     * 顶部横杠(有位移):渐显效果，控件层到达指定位置后，100ms不透明度由100%变为0%(不透明~透明) 需求好像反了
     * 纯色底图(无位移) + 背景图(有位移): 0~回弹值 = 渐隐(全显~透明); 回弹值~980px = 透明
     * <p>
     * <p>
     * 3.下划: 980px用时500ms
     * (1)快速下划: 500ms
     * (2)慢速上划:
     * 速度"固定",时间根据松手时剩余移动距离计算：t=s/(980/500)
     * 移动速度整体有线性变化,后200ms速度减缓为0
     * (3)无透明度变化:控件层
     * (4)有透明度变化:
     * 顶部横杠(有位移):渐隐效果，手指点按热区后（不论是快速下划还是慢速下划），100ms不透明度由0%变为100%(透明~不透明) 需求好像反了
     * 纯色底图(无位移) + 背景图(有位移): 980px~回弹值 = 渐显(透明~全显); 回弹值~0 = 全显
     **/
    private int moveDurationShow = 400; // 拉起
    private int moveDurationDismiss = 400; // 收起

    private float mUpBgLimitShowDistance = moveLimitationY_up;
    //    private float mDownBgLimitHideDistance = moveLimitationY_down;
    private float mUpDownTransparencyRatio = 1f / mUpBgLimitShowDistance;


    public VtpMyScrollerView(Context context) {
        this(context, null);
    }

    public VtpMyScrollerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VtpMyScrollerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyScrollerView);
        visibilityHeight = ta.getDimension(R.styleable.MyScrollerView_visibility_height, 100);
        ta.recycle();
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //   mScrollHeight = (int) (mChild.getMeasuredHeight() - visibilityHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mChild.layout(0, mScrollHeight, mChild.getMeasuredWidth(), mChild.getMeasuredHeight() + mScrollHeight);
    }

    private void init(Context context) {
        mScroller = new Scroller(context, new DecelerateInterpolator());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() == 0) {
            throw new RuntimeException(getResources().getString(R.string.screen_no_control));
        }
        if (getChildAt(0) == null) {
            throw new RuntimeException(getResources().getString(R.string.screen_no_background));
        }
        if (getChildAt(1) == null) {
            throw new RuntimeException(getResources().getString(R.string.screen_no_content));
        }
        /*if(getChildCount() > 1){
            throw new RuntimeException("只能有一个子控件！");
        }*/
        mBgChild = getChildAt(0);
        mBgChild.setAlpha(0f);
        mChild = getChildAt(1);
    }

    /**
     * TOUCH 事件不被拦截，继续下发
     *
     * @param event (downX, downY) = (0, 0) = 页面左上角
     */
    private void doDown(MotionEvent event) {
        downY = (int) event.getY();
        int downX = (int) event.getX();
        isContinueWithNoAction = (!isTop && downY < mChild.getMeasuredHeight() - touchLimitationY) || (isTop && downY > touchLimitationY) || !(downX >= touchLimitationX && downX <= mChild.getMeasuredWidth() - touchLimitationX);
        Log.d(TAG, "doScroll isContinueWithNoActionDOWN=" + isContinueWithNoAction + ", downX: " + downX + ", downY: " + downY + ", isTop: " + isTop);
    }

    private float mLastXIntercept = 0;
    private float mLastYIntercept = 0;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        AAOP_LogUtils.d(TAG, "doScrollInterceptTouchEvent action = " + event.getAction());
        int pointerIndex = event.getActionIndex();
        Log.d(TAG, "noScroll 01: pointerNum=" + event.getPointerCount() + ", pointerIndex=" + pointerIndex);

        boolean intercepted = false;
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("TAG", "doScrollInterceptTouchEvent DOWN");
//                finishScroll();

                setAutoHideAction(false);
                // 应对事件拦截:
                // 拦截事件，才会触发onTouchEvent的down
                // 此down时尚未拦截，也就不会触发onTouchEvent的down，那么则在这里补充判断doDown()(原在onTouchEvent中判断）
                doDown(event);
                /*// 不消费此次滑动事件判断(会直接从父view全部拦截，子view也会无响应）：
                // 不在顶部：按下位置在0~980（1080-100）
                // 在顶部：100~1080
                if ((!isTop && downY < mChild.getMeasuredHeight() - touchLimition) || (isTop && downY > touchLimition)){
                    Log.d("Gesture cancel 2: downY " + downY + ", super.onTouchEvent(event) " + super.onTouchEvent(event));
                    return true;
                }*/

                intercepted = false;
                mLastXIntercept = x;
                mLastYIntercept = y;

                if (!isContinueWithNoAction) {
                    // 拦截， 不可左右滑
                    setSlideChildEffect(false);
                }

                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "doScrollInterceptTouchEvent MOVE isContinueWithNoAction=" + isContinueWithNoAction);
                final float deltaX = Math.abs(x - mLastXIntercept);
                final float deltaY = Math.abs(y - mLastYIntercept);

                if (isContinueWithNoAction) {
                    // 不拦截， 可左右滑
                    Log.d(TAG, "doScrollInterceptTouchEvent: 不拦截 deltaX = " + deltaX + ",  deltaY = " + deltaY);
                    intercepted = false;
                } else {
                    // 拦截， 不可左右滑
                    Log.d(TAG, "doScrollInterceptTouchEvent: 拦截2 deltaX = " + deltaX + ",  deltaY = " + deltaY);
                    intercepted = true;
                }

                break;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "doScrollInterceptTouchEvent UP");
                intercepted = false;
                setAutoHideAction(true);
                setSlideChildEffect(true);
                break;
            default:
                break;
        }
        mLastXIntercept = x;
        mLastYIntercept = y;

        // 遍历子视图，判断是否有子视图被触摸到
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            Rect rect = new Rect();
            child.getHitRect(rect);
            if (rect.contains((int) event.getX(), (int) event.getY())) {
                // 子视图被触摸到，不拦截事件
                Log.d(TAG, "onInterceptTouchEvent  ---> child view ----");
                return false;
            }
        }
        // 没有子视图被触摸到，拦截事件，执行父视图的滑动逻辑
        //return true;
        return intercepted;
    }

    /**
     * MyScrollerView本身上下滑，调用此方法
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerIndex = event.getActionIndex();
        Log.d(TAG, "noScroll 012: pointerNum=" + event.getPointerCount() + ", pointerIndex=" + pointerIndex);
        // 判断多点触摸的情况，拦截滑动事件
//        if (event.getPointerCount() > 1) {
//            return true;
//        }
//        if (pointerIndex != 0) {
//            // 拦截 不向上传递,自己消费
//            return false;
//        }

        return doTouchEvent(event, false);
    }

    /**
     * 从dock栏上划，调用此方法
     */
    public boolean onOutsideTouchEvent(MotionEvent event) {
        return doTouchEvent(event, true);
    }

    private final int DOOK_HEIGHT = 100;

    public boolean doTouchEvent(MotionEvent event, boolean fromOutside) {
        Log.d(TAG, "doScrollEvent action = " + event.getAction() + ", fromOutside=" + fromOutside + ", isContinueWithNoAction=" + isContinueWithNoAction + ", isTop=" + isTop);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "doScrollEvent: [[ DOWN ]] ");
//                finishScroll();
//                doDown(event);
                // 不消费此次滑动事件判断(按下位置不在要求区域,不会被拉起/收起)：
                // 上到下 0到1080
                // 不在顶部：按下位置 < 1080-300 = 780
                // 在顶部：按下位置 > 1080-300 = 780
                if (fromOutside) {
                    if (isTop) {
                        Log.d(TAG, "doScrollEvent111111111111111: [[ DOWN ]] ");
                        return super.onTouchEvent(event);
                    }
                } else {
                    if (isContinueWithNoAction) {
                        Log.d(TAG, "doScrollEvent222222222222222: [[ DOWN ]] ");
                        return super.onTouchEvent(event);
                    }
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "doScrollEvent: [[ MOVE ]] , isContinueWithNoAction=" + isContinueWithNoAction + ", isTop=" + isTop);
                if (fromOutside) {
                    if (isTop) {
                        break;
                    }
                } else {
                    if (isContinueWithNoAction) {
                        break;
                    }
                }
                resetHandler();
                // 快速滑动灵敏度
                VelocityTracker mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(50);
                if (0 != mVelocityTracker.getYVelocity()) {
                    yVelocity = mVelocityTracker.getYVelocity();//去绝对值。向左滑，值为负数
                }
//                Log.debugD("yVelocity: " + yVelocity);
/*
                // 上划时，底部moveY=0，顶部moveY=-980
                // 下划时，顶部moveY=0，底部moveY=980 moveY=1080
                //上滑的dook栏高度隐藏
                if (moveY<-DOOK_HEIGHT){
                    //  HvacInitialViewBar.getInstance().hideInVisable();
                }*/
                moveY = (int) event.getY();
                int currPosY = this.getScrollY();
                doScrollPageEffect(currPosY);

                //deY是滑动的距离，向上滑时deY>0 ，向下滑时deY<0
                int deY = downY - moveY;
                Log.d(TAG, "doScrollEvent: [[ MOVE ING ]]  currPosY(视图当前位置)：" + currPosY + ", downY（上次位置）： " + downY + "， moveY（本次位置）: " + moveY + ", deY（本次和上次移动差值）: " + deY + ", movedY(已移动总和）: " + movedY);

                // 以下包括划动中途转换上/下划动方向后的结果
                //向上滑动时的处理
                if (deY > 0) {
                    // 0 ~ -980
                    isFinalMovingUp = true;

                    //将每次滑动的距离相加，为了防止子View的滑动超过View的顶部
                    movedY += deY;
                    Log.d(TAG, "doScrollEvent: [[ MOVE ING ]] move up, this movedY=" + movedY + ", this deY=" + deY);
                    if (movedY > mScrollHeight) {
                        movedY = mScrollHeight;
                    } else if (movedY < -mScrollHeight) {
                        movedY = -mScrollHeight;
                    } else {
                        if (currPosY < mScrollHeight) {
                            scrollBy(0, deY);
                            invalidate();
                        }
                    }
                    downY = moveY;
                    return true;
                }
                //向下滑动时的处理，向下滑动时需要判断子View是否在顶部，如果不在顶部则不消费此次事件
                if (deY < 0) {
                    // 980 ~ 0
                    isFinalMovingUp = false;
                    movedY += deY;
                    Log.d(TAG, "doScrollEvent: [[ MOVE ING ]] move down, this movedY=" + movedY + ", this deY=" + deY);
                    if (movedY < 0) {
                        movedY = 0;
                    } else {
                        scrollBy(0, deY);
                        invalidate();
                    }
                    downY = moveY;

                    //手指滑动监听超出屏幕底部时候进行隐藏
                    if (downY == 0 || movedY == 0) {
                        Log.d("TAG", "doTouchEvent:++++++ ");
                        setVisibility(GONE);
                        HvacSingleton.getInstance().setHvacHomeShow(true);
//                        HvacInitialViewBar.getInstance().show();
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "doScrollEvent: [[ MOVE ]] , isContinueWithNoAction=" + isContinueWithNoAction + ", isTop=" + isTop);
                if (fromOutside) {
                    if (isTop) {
                        break;
                    }
                } else {
                    if (isContinueWithNoAction) {
                        break;
                    }
                }
                resetHandler();
                // 快速滑动灵敏度
                mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(event);
                mVelocityTracker.computeCurrentVelocity(50);
                if (0 != mVelocityTracker.getYVelocity()) {
                    yVelocity = mVelocityTracker.getYVelocity();//去绝对值。向左滑，值为负数
                }
//                Log.debugD("yVelocity: " + yVelocity);
/*
                // 上划时，底部moveY=0，顶部moveY=-980
                // 下划时，顶部moveY=0，底部moveY=980 moveY=1080
                //上滑的dook栏高度隐藏
                if (moveY<-DOOK_HEIGHT){
                    //  HvacInitialViewBar.getInstance().hideInVisable();
                }*/
                moveY = (int) event.getY();
                currPosY = this.getScrollY();
                doScrollPageEffect(currPosY);

                //deY是滑动的距离，向上滑时deY>0 ，向下滑时deY<0
                deY = downY - moveY;
                Log.d(TAG, "doScrollEvent: [[ MOVE ING ]]  currPosY(视图当前位置)：" + currPosY + ", downY（上次位置）： " + downY + "， moveY（本次位置）: " + moveY + ", deY（本次和上次移动差值）: " + deY + ", movedY(已移动总和）: " + movedY);

                // 以下包括划动中途转换上/下划动方向后的结果
                //向上滑动时的处理
                if (deY > 0) {
                    // 0 ~ -980
                    isFinalMovingUp = true;

                    //将每次滑动的距离相加，为了防止子View的滑动超过View的顶部
                    movedY += deY;
                    Log.d(TAG, "doScrollEvent: [[ MOVE ING ]] move up, this movedY=" + movedY + ", this deY=" + deY);
                    if (movedY > mScrollHeight) {
                        movedY = mScrollHeight;
                    } else if (movedY < -mScrollHeight) {
                        movedY = -mScrollHeight;
                    } else {
                        if (currPosY < mScrollHeight) {
                            scrollBy(0, deY);
                            invalidate();
                        }
                    }
                    downY = moveY;
                    return true;
                }
                //向下滑动时的处理，向下滑动时需要判断子View是否在顶部，如果不在顶部则不消费此次事件
                if (deY < 0) {
                    // 980 ~ 0
                    isFinalMovingUp = false;
                    movedY += deY;
                    Log.d(TAG, "doScrollEvent: [[ MOVE ING ]] move down, this movedY=" + movedY + ", this deY=" + deY);
                    if (movedY < 0) {
                        movedY = 0;
                    } else {
                        scrollBy(0, deY);
                        invalidate();
                    }
                    downY = moveY;

                    //手指滑动监听超出屏幕底部时候进行隐藏
                    if (downY == 0 || movedY == 0) {
                        Log.d("TAG", "doTouchEvent:++++++ ");
                        setVisibility(GONE);
                        HvacSingleton.getInstance().setHvacHomeShow(true);
//                        HvacInitialViewBar.getInstance().show();
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "doScrollEvent: [[ UP ]], isMovingUp: " + isFinalMovingUp);
//                resetScrollData();
                setAutoHideAction(true);
                if (fromOutside) {
                    if (isTop) {
                        break;
                    }
                } else {
                    if (isContinueWithNoAction) {
                        break;
                    }
                }
                if (yVelocity < -referenceVelocity) {
                    Log.d(TAG, "doScrollEvent: page recognized showing speed: " + yVelocity);
                    doAutomaticAnim(true);
                    break;
                }
                if (yVelocity > referenceVelocity) {
                    Log.d(TAG, "doScrollEvent: page recognized dismissing speed: " + yVelocity);
                    doAutomaticAnim(false);
                    break;
                }
                //
                if (isFinalMovingUp) {
                    Log.d(TAG, "isFinalMovingUp true");
                    doAutomaticAnim(movedY > moveLimitationY_up);
                } else {
                    Log.d(TAG, "doScrollEvent: page recognized dismissing movePos: " + movedY + " mScrollHeight = " + mScrollHeight + "  moveLimitationY_down = " + moveLimitationY_down);
                    doAutomaticAnim(movedY > mScrollHeight - moveLimitationY_down);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void setPageAlpha(float alpha, int i) {
        Log.d(TAG, "setPageAlpha, alpha = " + alpha + ", num = " + i);
        if (alpha < 0) {
            alpha = 0f;
        }
        if (alpha > 1) {
            alpha = 1f;
        }
        if (alpha != mBgChild.getAlpha()) {
            mBgChild.setAlpha(alpha);
        }
//        WindowsUtils.setHvacPagesBgEffect(alpha);
    }

    public void doAutomaticAnim(boolean isShow) {
        doAutomaticAnim(isShow, isShow ? moveDurationShow : moveDurationDismiss);
    }

    private void doAutomaticAnim(boolean isShow, int scrollTime) {
        Log.d(TAG, "doAutomaticAnim, isShow=" + isShow + ", isInPullDownDuration=" + isInPullDownDuration);
//        WindowsUtils.setHvacMainViewState(isShow);
        isTop = isShow;
        resetHandler();
        if (isShow) {
            // 拉起
            int offsetY = mScrollHeight - getScrollY();
            Log.d(TAG, "pull page up start Y: " + getScrollY() + ", dxY: " + offsetY + ", maxY: " + mScrollHeight);
            startScrollAction(getScrollY(), offsetY, moveDurationShow);
            movedY = mScrollHeight;
            setAutoHideAction(true);
            setSlideChildEffect(true);
//            WindowsUtils.setHvacPagesBgEffect(1f);
        } else {
            // 收起
            Log.d(TAG, "pull page down start Y: " + getScrollY() + ", goal Y: " + -getScrollY());
            startScrollAction(getScrollY(), -getScrollY(), moveDurationDismiss);
            movedY = 0;
            downY = 0;
            isInPullDownDuration = true;
            setAutoHideAction(false);
            mHandler.sendEmptyMessageDelayed(VISIBILITY_NOTIFY_TYPE, moveDurationDismiss); // moveDurationDismiss后设置为GONE
            mHandler.sendEmptyMessageDelayed(DISAPPEAR_DURATION_NOTIFY_TYPE, pageHideDuration);
            setSlideChildEffect(false);
            //根据设置的动画持续收起时间进行隐藏该界面
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //隐藏主界面
                    //展示浮窗
//                    HvacInitialViewBar.getInstance().show();
                    setVisibility(GONE);
                    HvacSingleton.getInstance().setHvacHomeShow(true);
                }
            }, moveDurationDismiss);
        }
    }

    private void resetScrollData() {
        moveY = 0;
        movedY = 0;
        downY = 0;
    }

    /**
     * 调用动画前重置各相关状态
     */
    private void resetHandler() {
        if (mHandler.hasMessages(DISAPPEAR_DURATION_NOTIFY_TYPE)) {
            mHandler.removeMessages(DISAPPEAR_DURATION_NOTIFY_TYPE);
            isInPullDownDuration = false;
        }
        if (mHandler.hasMessages(VISIBILITY_NOTIFY_TYPE)) {
            mHandler.removeMessages(VISIBILITY_NOTIFY_TYPE);
        }
    }

    private void startScrollAction(int startY, int dxY, int time) {
//        finishScroll();
        Log.d(TAG, "startScrollAction startY=" + startY + ", dxY=" + dxY);
        /*this.post(new Runnable() {
            @Override
            public void run() {
                // 启动滚动动画
                Log.debugD("startScrollAction 1");
                mScroller.startScroll(0,startY,0, dxY, time);
                invalidate();
//                invalidate();
//                requestLayout();
            }
        });*/
        mScroller.startScroll(0, startY, 0, dxY, time);
//        requestLayout();
        invalidate();
    }

    private void finishScroll() {
        if (!mScroller.isFinished()) {
            Log.d(TAG, "startScrollAction before computeScrollOffset ----- stop scrolling");
            mScroller.forceFinished(true);
            mScroller.abortAnimation();
//            requestLayout();
            invalidate();
        }
    }

    /**
     * 手指按下：渐显
     * 划动结束：渐隐
     *
     * @param isShow
     */
    private void setSlideChildEffect(boolean isShow) {
        Log.d(TAG, "setSlideChildEffect : " + isShow);
        // TODO
        /*AlphaAnimation alphaAnimation = new AlphaAnimation(isShow ? 0.0f : 1.0f, isShow ? 1.0f : 0.0f);
        alphaAnimation.setDuration(100);
        mSlideChild.startAnimation(alphaAnimation);*/
    }

    /**
     * 页面收起时长是否在pageHideDuration内
     *
     * @return true: 1min内:下次拉起显示上次收起时页面(不销毁,所以会自动记忆,此种情况不做处理)
     * false: 超过1min:下次拉起显示空调页面(因为上条,所以此种情况需判断并设置显示页为空调页)
     */
    public boolean getInPullDownDuration() {
        Log.d(TAG, "getInPullDownDuration : " + isInPullDownDuration);
        return isInPullDownDuration;
    }

    /**
     * 为判断：页面无操作（需包括弹窗）是否在pageAutoHideDuration内，在时间外则自动收起空调页面（包括弹窗）
     *
     * @param isStart true表示无操作开始计时，false表示有操作取消计时判断
     */
    public void setAutoHideAction(boolean isStart) {
        Log.d(TAG, "setAutoHideAction isStart=" + isStart);
        mHandler.removeMessages(DISAPPEAR_DURATION_AUTO_HIDE_TYPE);
        if (isStart) {
            mHandler.sendEmptyMessageDelayed(DISAPPEAR_DURATION_AUTO_HIDE_TYPE, DIALOG_AUTO_HIDE_DURATION_TEN_SECONDS);
        }
    }

    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.d(TAG, "mHandler, type: " + msg.what);
            switch (msg.what) {
                case VISIBILITY_NOTIFY_TYPE:
                    if (!isTop) {
                        Log.d(TAG, "pull down is true 01");
//                        WindowsUtils.setHvacPanelVisibility(View.GONE, AreaConstant.AREA_GLOBAL, true, false);
//                        WindowsManager.setHvacPanelVisibility(View.GONE, HvacContent.AREA_GLOBAL, true, false);
                    } else {
                        Log.d(TAG, "pull down is false 01");
                    }
                    break;
                case DISAPPEAR_DURATION_NOTIFY_TYPE:
                    isInPullDownDuration = false;
                    break;
                case DISAPPEAR_DURATION_AUTO_HIDE_TYPE:
//                    WindowsUtils.setHvacPanelVisibility(View.GONE, AreaConstant.AREA_GLOBAL, true, true);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScroller.computeScrollOffset()) {
            // 视图的实际滚动位置
            int scrollY = this.getScrollY();
            // 滚动动画的当前位置
            int currY = mScroller.getCurrY();

            Log.d(TAG, "computeScrollOffset ----- auto scrolling ---00-- scrollY = " + scrollY + ", currY = " + currY + ", isTop = " + isTop);
            doScrollPageEffect(scrollY);
            // 视图滚动到滚动动画的当前位置
            scrollTo(0, currY);
//            requestLayout();
            invalidate();

//            requestLayout();
        } else {
            // 视图的实际滚动位置
            int scrollY = this.getScrollY();
            // 滚动动画的当前位置
            int currY = mScroller.getCurrY();

            Log.d(TAG, "computeScrollOffset ----- auto scrolling ---11-- scrollY = " + scrollY + ", currY = " + currY + ", isTop = " + isTop);
            // 手动拖拽过程中，会一直回调：
            // 上拉时（scrollY=0~980，currY=0）
            // 下拉时（scrollY=980~0，currY=980）
            // startScroll时,有时回调，eg：
            // 上拉时：结束走一次 （scrollY=0~980，currY=980）
            // 下拉时：开始走一次+结束走一次（scrollY=980~，currY=980~）
            if (isTop) {
                // 上拉
                if (scrollY == mScrollHeight && currY == mScrollHeight) {
                    doScrollPageEffect(mScrollHeight);
                }
                /*if (currY == 0){
                    isTop = false;
                }*/
            } else {
                // 下拉

            }
        }

    }

    private void doScrollPageEffect(/*boolean isStartMovingUp, */int scrollY) {

        // 上划时，底部moveY=0，顶部moveY=980
        // 下划时，顶部moveY=980，底部moveY=0
        float pageAlpha;
        if (scrollY > mUpBgLimitShowDistance) {
            // 顶980 ~ 底mUpBgLimitShowDistance
            // 背景全显
            pageAlpha = 1f;
        } else {
            // 上拉（数大）背景渐显
            // 下拉（数小）背景渐透
            pageAlpha = scrollY * mUpDownTransparencyRatio; // mUpDownTransparencyRatio
//                    Log.debugD("on dock scroller doTouchEvent Event: move, mBgChild tempMoveY= " + newMoveY + ", mUpDownTransparencyRatio= " + mUpDownTransparencyRatio + ", result= " + newAlpha);
        }
        Log.d(TAG, "doScrollPageEffect ----- effect ing ----- scrollY = " + scrollY + ", pageAlpha = " + pageAlpha);
        setPageAlpha(pageAlpha, 3);
    }
}
