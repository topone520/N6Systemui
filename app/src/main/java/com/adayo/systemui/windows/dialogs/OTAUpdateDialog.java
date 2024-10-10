package com.adayo.systemui.windows.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Icon;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.systemui.utils.OTAUpGradeUtil;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

/**
 * The type Ota update dialog.
 */
public class OTAUpdateDialog extends Dialog implements View.OnClickListener {

    private static final String TAG = "_OTAUpdateDialog";
    private static final String MSG_SET_SHOW_UPGRADE_VIEW = "msg_set_show_upgrade_view";
    /**
     * The Memory.
     */
    TextView memory;
    /**
     * The Cancel.
     */
    TextView cancel;
    /**
     * The Iv image.
     */
    ImageView iv_image;
    /**
     * The Tv title.
     */
    TextView tv_title;
    /**
     * The Tv tips.
     */
    TextView tv_tips;
    /**
     * The Tv time.
     */
    TextView tv_time;

    /**
     * The On button click listener one.
     */
    OnButtonClickListener onButtonClickListenerOne;
    /**
     * The On button click listener two.
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
     * Instantiates a new Ota update dialog.
     *
     * @param context the context
     */
    public OTAUpdateDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    /**
     * Instantiates a new Ota update dialog.
     *
     * @param context    the context
     * @param themeResId the theme res id
     */
    public OTAUpdateDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    /**
     * Instantiates a new Ota update dialog.
     *
     * @param context        the context
     * @param cancelable     the cancelable
     * @param cancelListener the cancel listener
     */
    protected OTAUpdateDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }


    private void initView() {
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams = new WindowManager.LayoutParams(2071);
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
//        layoutParams.x = 660;
//        layoutParams.y = -140;
//        layoutParams.width = 620;
//        layoutParams.height = 440;
        /*layoutParams.y = 104;*/
        layoutParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | // 使窗口全屏
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | // 允许在窗口之外的区域接收触摸事件
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | // 窗口不接收焦点
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | // 在屏幕内布局
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | // 允许窗口超出屏幕边界
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR | // 在窗口装饰区域内布局
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED; // 启用硬件加速
//        layoutParams.systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
//        layoutParams.systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
//        layoutParams.systemUiVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        layoutParams.systemUiVisibility |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//        layoutParams.setTitle(HVAC_PANEL_TITLE);
        layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_UNCHANGED | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
        layoutParams.gravity = Gravity.END | Gravity.TOP;
        layoutParams.format = PixelFormat.TRANSLUCENT;
//        LayoutInflater lf = AAOP_HSkin.getLayoutInflater(SystemUIApplication.getSystemUIContext());
//        mRootView = lf.inflate(R.layout.view_notification_center, null);
//
//
//        Window window = getWindow();
//        window.setGravity(Gravity.END | Gravity.TOP);
//        window.setWindowAnimations(R.style.DialogAnimation);
//        WindowManager.LayoutParams layoutParams = window.getAttributes();
//        layoutParams.type = 2066;
//        layoutParams.x = 660;
//        layoutParams.width = 620;
//        layoutParams.height = 440;
//        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
//        window.setAttributes(layoutParams);
//        setContentView(R.layout.layout_dialog_ota_update);

        view = AAOP_HSkin.getLayoutInflater(SystemUIApplication.getSystemUIContext())
                .inflate(R.layout.layout_dialog_ota_update, null);

//        AAOP_HSkin.getWindowViewManager().addWindowView(view);
//
//        mWindowManager.addView(view, layoutParams);

        memory = view.findViewById(R.id.yes);
        cancel = view.findViewById(R.id.no);
        iv_image = view.findViewById(R.id.iv_image);
        tv_title = view.findViewById(R.id.tv_title);
        tv_tips = view.findViewById(R.id.tv_tips);
        tv_time = view.findViewById(R.id.tv_time);


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
     * Create dialog ota update dialog.
     *
     * @return the ota update dialog
     */
    public static OTAUpdateDialog createDialog(){
        return null;
    }

    /**
     * Create dialog ota update dialog.
     *
     * @param context    the context
     * @param themeResId the theme res id
     * @return the ota update dialog
     */
    public static OTAUpdateDialog createDialog(Context context,int themeResId) {

        return new OTAUpdateDialog(context,themeResId);

    }

    /**
     * Sets button one.
     *
     * @param text                  the text
     * @param color                 the color
     * @param onButtonClickListener the on button click listener
     * @return the button one
     */
    public OTAUpdateDialog setButtonOne(String text, int color, OnButtonClickListener onButtonClickListener) {
        if (TextUtils.isEmpty(text)) {
            memory.setVisibility(View.GONE);
            return this;
        }
        onButtonClickListenerOne = onButtonClickListener;
        GradientDrawable memoryDrawable = (GradientDrawable) memory.getBackground();

        memoryDrawable.setColor(color);
        memory.setBackground(memoryDrawable);
        memory.setText(text);
        memory.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * Sets button two.
     *
     * @param text                  the text
     * @param onButtonClickListener the on button click listener
     * @return the button two
     */
    public OTAUpdateDialog setButtonTwo(String text, OnButtonClickListener onButtonClickListener) {
        if (TextUtils.isEmpty(text)) {
            cancel.setVisibility(View.GONE);
            return this;
        }
        onButtonClickListenerTwo = onButtonClickListener;
        cancel.setText(text);
        cancel.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * Sets button two.
     *
     * @param text                  the text
     * @param onButtonClickListener the on button click listener
     * @param time                  the time
     * @return the button two
     */
    public OTAUpdateDialog setButtonTwo(String text, OnButtonClickListener onButtonClickListener, int time) {
        if (TextUtils.isEmpty(text)) {
            return this;
        }
        if (time != 0) {
            if (null != timer) {
                timer.cancel();
            }
            timer = new CountDownTimer(time * 1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    cancel.setText(text + "(" + millisUntilFinished / 1000 + ")");
                }

                @Override
                public void onFinish() {
                    if (onButtonClickListenerTwo != null) {
                        onButtonClickListenerTwo.onButtonClick(OTAUpdateDialog.this);

                    }
                    dismiss();

                }
            }.start();
        }
        onButtonClickListenerTwo = onButtonClickListener;
        cancel.setText(text);
        cancel.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * Sets icon.
     *
     * @param rid the rid
     * @return the icon
     */
    public OTAUpdateDialog setIcon(int rid) {
        iv_image.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * Sets icon.
     *
     * @param icon the icon
     * @return the icon
     */
    public OTAUpdateDialog setIcon(Icon icon) {
        if (icon == null) {
            return this;
        }
        iv_image.setImageIcon(icon);
        iv_image.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * Sets title name.
     *
     * @param text the text
     * @return the title name
     */
    public OTAUpdateDialog setTitleName(CharSequence text) {
        if (!TextUtils.isEmpty(text)) {
            tv_title.setText(text);
            tv_title.setVisibility(View.VISIBLE);
        }
        return this;
    }

    /**
     * Sets time.
     *
     * @param text the text
     * @return the time
     */
    public OTAUpdateDialog setTime(String text) {
        if (!TextUtils.isEmpty(text)) {
            tv_time.setText(text);
            tv_time.setVisibility(View.VISIBLE);
        }
        return this;
    }

    /**
     * Sets tips.
     *
     * @param text the text
     * @return the tips
     */
    public OTAUpdateDialog setTips(CharSequence text) {
        if (text != null) {
            tv_tips.setText(text);
        }
        return this;
    }

    /**
     * Sets type.
     *
     * @param type the type
     * @return the type
     */
    public OTAUpdateDialog setType(int type) {

        getWindow().setType(type);
        return this;
    }


    @Override
    public void show() {
        try {
            super.show();
            if (null != view && view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        if (null != view && view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
        }
        super.dismiss();
//        if (null != view && view.getVisibility() != View.GONE) {
//            view.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.yes:
                //调用接口
                Log.i(TAG, "MSG_SET_SHOW_UPGRADE_VIEW");
                OTAUpGradeUtil.getInstance().init(MSG_SET_SHOW_UPGRADE_VIEW);
                dismiss();
                break;
            case R.id.no:

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
        void onButtonClick(OTAUpdateDialog commonDialog);

    }
}
