package com.adayo.systemui.windows.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

/**
 * The type Common dialog.
 */
public class CommonDialog extends Dialog implements View.OnClickListener {
    /**
     * 第一个按钮.
     */
    TextView memory;
    /**
     * 取消按钮.
     */
    TextView cancel;
    /**
     * 图片.
     */
    ImageView iv_image;
    /**
     * 标题.
     */
    TextView tv_title;
    /**
     * message.
     */
    TextView tv_tips;

    /**
     * 第一个按钮的点击事件回调.
     */
    OnButtonClickListener onButtonClickListenerOne;
    /**
     * 第二个按钮的点击事件回调.
     */
    OnButtonClickListener onButtonClickListenerTwo;

    /**
     * The Timer.
     */
    CountDownTimer timer;
    private WindowManager mWindowManager;
    private boolean isAdded = false;
    private View view;

    /**
     * Instantiates a new Common dialog.
     *
     * @param context the context
     */
    public CommonDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    /**
     * Instantiates a new Common dialog.
     *
     * @param context    the context
     * @param themeResId the theme res id
     */
    public CommonDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    /**
     * Instantiates a new Common dialog.
     *
     * @param context        the context
     * @param cancelable     the cancelable
     * @param cancelListener the cancel listener
     */
    protected CommonDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }


    private void initView() {
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;;
        getWindow().setType(2066);
        layoutParams.flags =
                WindowManager.LayoutParams.FLAG_FULLSCREEN | // 使窗口全屏
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | // 允许在窗口之外的区域接收触摸事件
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | // 在屏幕内布局
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | // 允许窗口超出屏幕边界
                        WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR | // 在窗口装饰区域内布局
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED; // 启用硬件加速
        layoutParams.systemUiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        layoutParams.format = PixelFormat.TRANSLUCENT;

        view = AAOP_HSkin.getLayoutInflater(SystemUIApplication.getSystemUIContext())
                .inflate(R.layout.layout_dialog_common, null);

        memory = view.findViewById(R.id.yes);
        cancel = view.findViewById(R.id.no);
        iv_image = view.findViewById(R.id.iv_image);
        tv_title = view.findViewById(R.id.tv_title);
        tv_tips = view.findViewById(R.id.tv_tips);


        iv_image.setVisibility(View.GONE);
        memory.setOnClickListener(this);
        cancel.setOnClickListener(this);

        if (!isAdded && null != mWindowManager) {
            if (null != view) {
                mWindowManager.addView(view, layoutParams);
                AAOP_HSkin.getWindowViewManager().addWindowView(view).applySkinForViews(true);
            }
            isAdded = true;
        }
    }

    /**
     * 创建dialog.
     *
     * @param context    the context
     * @param themeResId 主题id
     * @return the common dialog
     */
    public static CommonDialog createDialog(Context context, int themeResId) {

        return new CommonDialog(context, themeResId);

    }

    /**
     * 设置第一个按钮.
     *
     * @param text                  按钮文字
     * @param drawableId            the drawable id
     * @param onButtonClickListener 第一个按钮的点击事件回调
     * @return the button one
     */
    public CommonDialog setButtonOne(String text, int drawableId, OnButtonClickListener onButtonClickListener) {
        if (TextUtils.isEmpty(text)) {
            memory.setVisibility(View.GONE);
            return this;
        }
        onButtonClickListenerOne = onButtonClickListener;
        memory.setVisibility(View.VISIBLE);
        try {
            AAOP_HSkin.with(memory).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, drawableId).applySkin(false);
        }catch (Exception e){
            e.printStackTrace();
        }
        memory.setText(text);
        return this;
    }

    /**
     * Sets button two.
     *
     * @param text                  按钮文字
     * @param drawableId            the drawable id
     * @param onButtonClickListener 第二个按钮点击事件的回调
     * @return the button two
     */
    public CommonDialog setButtonTwo(String text, int drawableId, OnButtonClickListener onButtonClickListener) {
        if (TextUtils.isEmpty(text) || drawableId == 0) {
            cancel.setVisibility(View.GONE);
        } else {
            onButtonClickListenerTwo = onButtonClickListener;
            cancel.setVisibility(View.VISIBLE);
            try {
                AAOP_HSkin.with(cancel).addViewAttrs(AAOP_HSkin.ATTR_BACKGROUND, drawableId).applySkin(false);
            }catch (Exception e){
                e.printStackTrace();
            }
            cancel.setText(text);
        }
        return this;
    }

    /**
     * 设置标题.
     *
     * @param text 标题文字
     * @return the title name
     */
    public CommonDialog setTitleName(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            tv_title.setText(text);
            tv_title.setVisibility(View.VISIBLE);
        }
        return this;
    }

    /**
     * 设置内容.
     *
     * @param text 内容
     * @return the tips
     */
    public CommonDialog setTips(CharSequence text) {
        if (text != null) {
            tv_tips.setText(text);
        }
        return this;
    }


    @Override
    public void show() {
        super.show();
        if (null != view && view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void dismiss() {
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        super.dismiss();
        if (null != view && view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.yes:

                if (onButtonClickListenerOne != null) {
                    onButtonClickListenerOne.onButtonClick(this);

                }

                dismiss();

                break;
            case R.id.no:
                if (onButtonClickListenerTwo != null) {
                    onButtonClickListenerTwo.onButtonClick(this);

                }
                dismiss();
                break;

            default:

                break;

        }

    }


    /**
     * The interface On button click listener.
     */
    public interface OnButtonClickListener {

        /**
         * On button click.
         *
         * @param commonDialog the common dialog
         */
        void onButtonClick(CommonDialog commonDialog);

    }
}
