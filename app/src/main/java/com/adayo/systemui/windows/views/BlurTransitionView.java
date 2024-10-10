package com.adayo.systemui.windows.views;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.systemui.utils.BlurBitmapCache;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

public class BlurTransitionView extends RelativeLayout {
    ValueAnimator showAnimator, hideAnimator;

    int showTime = 100;


    public BlurTransitionView(Context context) {
        super(context);
        init();
    }

    public BlurTransitionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BlurTransitionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public BlurTransitionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
//        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BlurTransitionView);
//        showTime = typedArray.getInt(R.styleable.BlurTransitionView_showTime,100);
        init();
    }


    Rect srcRect;
    Rect dstRect;

//    Rect srcRectTop;
//    Rect dstRectTop;

    @SuppressLint("ResourceAsColor")
    private void init() {
        srcRect = new Rect();
        dstRect = new Rect();
//        srcRectTop = new Rect();
//        dstRectTop = new Rect();
        setBackgroundResource(R.color.transparent);


        setVisibility(View.GONE);
        initAnimator();
        initPaint();

        blurBitmap = BlurBitmapCache.getInstance().process(getContext());
        roundRadio = BlurBitmapCache.getInstance().getRoundRadio();
        show(showTime);

    }

    private void initAnimator() {
        showAnimator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
        hideAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
    }

    private static final String TAG = "BlurTransitionView";

    private Bitmap blurBitmap;
    private float roundRadio = 16;
    private Paint paint;
    private Paint myPaint = new Paint();
    int[] position;
    private PorterDuffXfermode mode;
//    Bitmap shadowBitmap;

    private void initPaint() {
        position = new int[2];
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);

        mode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
    }



    public void show(long time) {

        roundRadio = BlurBitmapCache.getInstance().getRoundRadio();

        if (hideAnimator.isRunning()) {
            hideAnimator.cancel();
        }
        if (showAnimator.isRunning()) {
            Log.d(TAG,"show isRunning");
            return;
        }
//        if (getAlpha() == 1 && View.VISIBLE == getVisibility()) {
//            Log.d(TAG,"show View.VISIBLE");
//            return;
//        }

        setVisibility(VISIBLE);

        showAnimator.setDuration(220);
        showAnimator.start();
        Log.d(TAG,"show end");
    }

    private void getScreenPosition() {
        getLocationOnScreen(position);

        srcRect.left = position[0];
        srcRect.top = position[1];
        srcRect.right = position[0] + getWidth();
        srcRect.bottom = position[1] + getHeight();

        dstRect.left = 0;
        dstRect.top = 0;
        dstRect.right = getWidth();
        dstRect.bottom = getHeight();
    }

    public void hide(long time) {
        if (showAnimator.isRunning()) {
            showAnimator.cancel();
        }
        if (hideAnimator.isRunning()) {
            return;
        }
        if (0 == time) {
            setVisibility(GONE);
            return;
        }
        if (getAlpha() == 0) {
            return;
        }
        hideAnimator.setDuration(time);
        hideAnimator.start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == blurBitmap) {
            return;
        }

        Log.d("AdayoCamera", TAG + " - onDraw: failed because ");
         getScreenPosition();
        int sc = canvas.saveLayer(0, 0, getWidth() , getHeight() , null);
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
        canvas.drawRoundRect(dstRect.left, dstRect.top, dstRect.right, dstRect.bottom, roundRadio, roundRadio, paint);
        paint.setXfermode(mode);
        canvas.drawBitmap(blurBitmap, srcRect, dstRect, paint);
        paint.setXfermode(null);
        canvas.restoreToCount(sc);
//
//        canvas.save();

//        canvas.drawRect(dstRectTop,paint);
//        canvas.drawRoundRect(dstRect.left, dstRect.top, dstRect.right, dstRect.bottom, roundRadio, roundRadio, paint);

//        canvas.drawBitmap(shadowBitmap, 0, 0, paint);
//        canvas.restore();
//

        myPaint.setAntiAlias(true);
        myPaint.setColor(SystemUIApplication.getSystemUIContext().getColor(R.color.dialog_bg));
//        if (DeviceServiceManager.getInstance().getDayNightModeAPP() == 2){
//            myPaint.setARGB(90,255,255,255);
//        } else {
//            myPaint.setARGB(90,60,65,81);
//        }
//        myPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(dstRect.left, dstRect.top, dstRect.right, dstRect.bottom, roundRadio, roundRadio, myPaint);

    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        Log.d("AdayoCamera", TAG + "onVisibilityChanged ");
//        if (visibility == VISIBLE){
//            Log.d("AdayoCamera", TAG + "onVisibilityChanged VISIBLE");
//            recycleBitmap();
//            blurBitmap = BlurBitmapCache.getInstance().process(getContext());
//            show(100);
//        }else{
//            recycleBitmap();
//        }

    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE){
            Log.d("AdayoCamera", TAG + "onVisibilityChanged VISIBLE");
            recycleBitmap();
            blurBitmap = BlurBitmapCache.getInstance().process(getContext());
            show(showTime);
        }else {
            recycleBitmap();
        }
        Log.d("AdayoCamera", TAG + "onWindowVisibilityChanged " + visibility);
    }



    /**
     * 在不需要的时候释放缓存的高斯模糊图片
     */
    private void recycleBitmap() {
        if (null != blurBitmap) {
            blurBitmap.recycle();
            blurBitmap = null;
        }
//        if (null != shadowBitmap) {
//            shadowBitmap.recycle();
//            shadowBitmap = null;
//        }
    }
}
