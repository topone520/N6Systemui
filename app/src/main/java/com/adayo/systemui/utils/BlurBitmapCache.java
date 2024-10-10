package com.adayo.systemui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 用于做高斯模糊的图片缓存以提升性能，减少内存
 *
 * created at 2021/12/17 19:44
 */
public class BlurBitmapCache {

    private static final String TAG = "BlurBitmapCache";

    public static BlurBitmapCache instance;


    private float roundRadio = 16;
    private DisplayMetrics mDisplayMetrics;


    public BlurBitmapCache() {
        mDisplayMetrics = new DisplayMetrics();
    }

    public static BlurBitmapCache getInstance() {
        if (null == instance) {
            synchronized (BlurBitmapCache.class) {
                if (null == instance) {
                    instance = new BlurBitmapCache();
                }
            }
        }
        return instance;
    }

    public float getRoundRadio() {
        return roundRadio;
    }

    public Bitmap process(Context context) {
        Bitmap bitmap = screenCap();
        if (null != bitmap) {
            return setBitmap(context, bitmap, 25, 0.2f, roundRadio, true);
        }
        return null;
    }

    public Bitmap processRefresh(Context context, Drawable drawable){
        BitmapDrawable drawable1 = (BitmapDrawable) drawable;
        Bitmap mBitmap = drawable1.getBitmap();
        return setBitmap(context, mBitmap, 25, 0.2f, roundRadio, true);
    }

    public Bitmap processRefresh(Context context, int drawableId){
        Bitmap originalBitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        Bitmap copyBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);

        return setBitmap(context, copyBitmap, 25, 0.2f, roundRadio, true);
    }


    /**
     * 在Application调用，或在setContentView之前调用
     *
     * @param context       上下文
     * @param source        要被高斯模糊的原图
     * @param blurRadius    高斯模糊的半径
     * @param scale         缩放比例，为了加速高斯模糊，值越小越快，但会不清晰
     * @param roundRadio    圆角半径
     * @param shouldRecycle 是否要回收source，如果外部不再使用该Bitmap，则设置为true
     */
    public Bitmap setBitmap(Context context, Bitmap source, int blurRadius, float scale, float roundRadio, boolean shouldRecycle) {
        Bitmap blurBitmap;

        this.roundRadio = roundRadio;

        int srcWidth = source.getWidth();
        int srcHeight = source.getHeight();

        int width = Math.round(srcWidth * scale);
        int height = Math.round(srcHeight * scale);

        Bitmap inputBmp = Bitmap.createScaledBitmap(source, width, height, false);

        RenderScript renderScript = RenderScript.create(context);

        final Allocation input = Allocation.createFromBitmap(renderScript, inputBmp);
        final Allocation output = Allocation.createTyped(renderScript, input.getType());

        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        scriptIntrinsicBlur.setInput(input);

        scriptIntrinsicBlur.setRadius(blurRadius);

        scriptIntrinsicBlur.forEach(output);

        output.copyTo(inputBmp);

        renderScript.destroy();

        if (shouldRecycle) {
            source.recycle();
        }

        if (scale == 1) {
            blurBitmap = inputBmp;
        } else {
            blurBitmap = Bitmap.createScaledBitmap(inputBmp, srcWidth, srcHeight, false);
            inputBmp.recycle();
        }
        return blurBitmap;
    }

    /**
     * 在不需要的时候释放缓存的高斯模糊图片
     */
//    public void recycleBitmap() {
//        if (null != blurBitmap) {
//            blurBitmap.recycle();
//            blurBitmap = null;
//        }
//    }

    /**
     * 截屏
     */
    private Bitmap screenCap() {
        Log.d("screenCap", "screenCap width = " + mDisplayMetrics.widthPixels + ", height = " + mDisplayMetrics.heightPixels);

        try {
            Class cls = Class.forName("android.view.SurfaceControl");
            Method screenshot = cls.getMethod("screenshot", Rect.class, int.class, int.class, int.class);
            screenshot.setAccessible(true);
            Bitmap source = (Bitmap) screenshot.invoke(null, new Rect(), mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels, 0);
            Bitmap screenBitmap = source.copy(Bitmap.Config.ARGB_4444, true);
            Log.d(TAG, ", screenBitmap = " + screenBitmap);
            source.recycle();
            source = null;

            return screenBitmap;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.d("screenCap", ", screenCap ClassNotFoundException = " + e.getMessage());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Log.d("screenCap", ", screenCap NoSuchMethodException = " + e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.d("screenCap", ", screenCap IllegalAccessException = " + e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            Log.d("screenCap", ", screenCap InvocationTargetException = " + e.getMessage());
        }
        return null;
    }

    public Bitmap ninePatch2Bitmap(Context context, int resId, int width, int height) {

        Drawable drawable = context.getResources().getDrawable(resId);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);

        Canvas canvas = new Canvas(bitmap);

        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());

        drawable.draw(canvas);

        return bitmap;

    }
}
