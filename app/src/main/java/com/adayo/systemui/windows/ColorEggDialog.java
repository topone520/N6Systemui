package com.adayo.systemui.windows;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.systemui.bean.MessageBeanInfo;
import com.adayo.systemui.bean.MessageBeanInfo1;
import com.adayo.systemui.utils.CallingStatusSoaUtil;
import com.adayo.systemui.utils.ColorEggSoaUtil;
import com.adayo.systemui.utils.CustomToastUtil;
import com.adayo.systemui.utils.MqttUtils;
import com.adayo.systemui.utils.PGradeSoaUtil;
import com.adayo.systemui.windows.dialogs.CommonDialog;
import com.adayo.systemui.windows.views.NotificationCenterView;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import java.io.File;
import java.text.DecimalFormat;

/**
 * 节日彩蛋dialog.
 */
//主动提醒弹窗
public class ColorEggDialog extends Dialog implements View.OnClickListener {

    private TextView tvTitle;
    private TextView tvMessage;
    private ImageView ivCover;
    private ImageView ivPlay;
    private static final String MSG_SET_CLUSTER_FESTIVAL_SCENE = "MSG_SET_CLUSTER_FESTIVAL_SCENE";

    /**
     * The constant TAG.
     */
    public static final String TAG = "_ColorEggDialog";
    /**
     * The Pending intent.
     */
//    TextView tv_title;
    PendingIntent pendingIntent;
    /**
     * The Down y.
     */
    float downY;
    /**
     * The Move y.
     */
    float moveY;
    /**
     * The Is slide up.
     */
    boolean isSlideUp;
    private static ColorEggDialog messageDialog;
    private CountDownTimer countDownTimer;
    private String messageType;
    private MessageBeanInfo1 messageBeanInfo;

    private WindowManager mWindowManager;
    private View view;
    private boolean isAdded = false;
    private String title;
    private String message;
    private String cover;

    /**
     * Instantiates a new Color egg dialog.
     *
     * @param context the context
     */
    public ColorEggDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    /**
     * 初始化dialog.
     *
     * @param context    the context
     * @param themeResId 主题id
     */
    public ColorEggDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    /**
     * Instantiates a new Color egg dialog.
     *
     * @param context        the context
     * @param cancelable     the cancelable
     * @param cancelListener the cancel listener
     */
    protected ColorEggDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    /**
     * 创建dialog.
     *
     * @param context    the context
     * @param themeResId 主题id
     * @return the color egg dialog
     */
    public static ColorEggDialog createDialog(Context context, int themeResId) {

        return new ColorEggDialog(context, themeResId);

    }

    /**
     * 设置数据.
     *
     * @param mMessageBeanInfo 消息体
     * @return the message info
     */
    public ColorEggDialog setMessageInfo(MessageBeanInfo1 mMessageBeanInfo) {
        if (mMessageBeanInfo != null) {
            messageBeanInfo = mMessageBeanInfo;
            title = messageBeanInfo.getTitle();
            message = messageBeanInfo.getDescribe();
            cover = messageBeanInfo.getCover();

            tvTitle.setText(title);
            tvMessage.setText(message);
        }

        tvTitle.setText("圣诞节快乐！！！！");
        tvMessage.setText("圣诞节快乐！！！！圣诞节快乐！！！！");
        return this;
    }


    /**
     * 设置跳转意图.
     *
     * @param intent the intent
     * @return the intent
     */
    public ColorEggDialog setIntent(PendingIntent intent) {
        pendingIntent = intent;
        return this;
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        ;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        ;
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
                .inflate(R.layout.dialog_color_egg, null);
        tvTitle = view.findViewById(R.id.tv_title);
        tvMessage = view.findViewById(R.id.tv_message);
        ivCover = view.findViewById(R.id.iv_cover);
        ivPlay = view.findViewById(R.id.iv_play);

        ivPlay.setOnClickListener(this);

        if (!isAdded && null != mWindowManager) {
            if (null != view) {
                mWindowManager.addView(view, layoutParams);
                AAOP_HSkin.getWindowViewManager().addWindowView(view).applySkinForViews(true);
            }
            isAdded = true;
        }
    }

    @Override
    public void show() {
        super.show();
        if (null != view && view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }

        countDownTimer = new CountDownTimer(5 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                dismiss();

            }
        }.start();

    }

    @Override
    public void dismiss() {
        super.dismiss();
        tvTitle.setText("");
        tvMessage.setText("");
        countDownTimer.cancel();
        if (null != view && view.getVisibility() != View.GONE) {
            view.setVisibility(View.GONE);
        }

    }

    /**
     * 是否显示.
     *
     * @return the boolean
     */
    public static boolean isShow() {

        if (messageDialog == null) {
            return false;
        } else {
            return messageDialog.isShowing();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play:
//                File file = new File("/sdcard/Movies/coloregg/" + title + ".mp4");
                //文件下载存到/mnt/ota/syncplay/，调用我接口时，参数值只要传文件名就行了，无需带路径
                File file = new File("/mnt/ota/syncplay/" + "advideo" + ".mp4");
//                if (file.exists() && !MqttUtils.getInstance().isDownload()) {
//                    //调用soa接口
//                    Log.i(TAG,"play-------");
//                }
//                if (file.exists()) {
                //调用soa接口
                Log.i(TAG, "play-------");
                int value = PGradeSoaUtil.getInstance().sendToSoa();
                boolean isPlay = CallingStatusSoaUtil.getInstance().isPlay();
                if (value == 0x2) {
                    CustomToastUtil.getInstance().showToast
                            (SystemUIApplication.getSystemUIContext().getString(R.string.driving_can_not_play_video),
                                    2, 0);
                } else if (!isPlay) {
                    //蓝牙通话
                    CustomToastUtil.getInstance().showToast
                            (SystemUIApplication.getSystemUIContext().getString(R.string.blt_calling_play_wait),
                                    2, 0);
                } else {
                    ColorEggSoaUtil.getInstance().init(MSG_SET_CLUSTER_FESTIVAL_SCENE);
                    if (ColorEggSoaUtil.isOnline) {
                        Log.i(TAG, "video-path-------" + file.getPath());
                        ColorEggSoaUtil.getInstance().sendToSoa(file.getPath());
                        dismiss();
                    }
                }
//                }
                break;
        }
    }
}
