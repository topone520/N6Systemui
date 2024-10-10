package com.adayo.systemui.windows.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.systemui.bean.MessageBeanInfo;
import com.adayo.systemui.bean.RefreshSystemMessageEvent;
import com.adayo.systemui.notification.TrackingEnum;
import com.adayo.systemui.utils.TrackingMessageDataUtil;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;

import java.text.DecimalFormat;

/**
 * 主动提醒弹窗.
 */
public class NotificationTopDialog extends Dialog {

    private TextView tvTopMessage;
    private LinearLayout llRoot;

    /**
     * The constant TAG.
     */
    public static final String TAG = "_MessageDialog";
    /**
     * The Tv message.
     */
//    TextView tv_title;
    TextView tv_message;
    /**
     * The Pending intent.
     */
//    ImageView iv_icon;
    PendingIntent pendingIntent;
    /**
     * The Down y.
     */
//    Button memory;
//    Button cancel;
    float downY;
    /**
     * The Move y.
     */
    float moveY;
    /**
     * The Is slide up.
     */
    boolean isSlideUp;
    private static NotificationTopDialog messageDialog;
    private CountDownTimer countDownTimer;
    private String messageType;
    private MessageBeanInfo messageBeanInfo;
    private double longitude;
    private double latitude;
    private WindowManager mWindowManager;
    private View view;
    private boolean isAdded = false;
    private static final String REQUEST_GUIDE = "com.adayo.service.navi.requestGuide";

    /**
     * Instantiates a new Notification top dialog.
     *
     * @param context the context
     */
    public NotificationTopDialog(@NonNull Context context) {
        super(context);
        initView();
    }

    /**
     * Instantiates a new Notification top dialog.
     *
     * @param context    the context
     * @param themeResId the theme res id
     */
    public NotificationTopDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    /**
     * Instantiates a new Notification top dialog.
     *
     * @param context        the context
     * @param cancelable     the cancelable
     * @param cancelListener the cancel listener
     */
    protected NotificationTopDialog(@NonNull Context context, boolean cancelable, @Nullable DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }


    /**
     * 创建主动提醒dialog.
     *
     * @return the notification top dialog
     */
    public static NotificationTopDialog Builder() {
        if (messageDialog == null) {
            messageDialog = new NotificationTopDialog(SystemUIApplication.getSystemUIContext());
        }
        return messageDialog;
    }


    /**
     * 设置message.
     *
     * @param message 消息
     * @return the message
     */
    public NotificationTopDialog setMessage(String message) {
        tvTopMessage.setText(message);
        return this;
    }

    /**
     * 设置消息类型.
     *
     * @param mMessageType 消息类型
     * @return the message type
     */
    public NotificationTopDialog setMessageType(String mMessageType) {
        messageType = mMessageType;

        return this;
    }

    /**
     * 设置message info.
     *
     * @param mMessageBeanInfo 消息体
     * @return the message info
     */
    public NotificationTopDialog setMessageInfo(MessageBeanInfo mMessageBeanInfo) {
        messageBeanInfo = new MessageBeanInfo();
        messageBeanInfo = mMessageBeanInfo;
        String[] gps = messageBeanInfo.getData().getGps().split(",");
        DecimalFormat decimalFormat = new DecimalFormat("#.######");
        longitude = Double.valueOf(decimalFormat.format(gps[0]));
        latitude = Double.valueOf(decimalFormat.format(gps[1]));
        return this;
    }


    /**
     * 设置intent意图.
     *
     * @param intent the intent
     * @return the intent
     */
    public NotificationTopDialog setIntent(PendingIntent intent) {

        pendingIntent = intent;
        return this;
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        mWindowManager = (WindowManager) SystemUIApplication.getSystemUIContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.y = 20;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = 160;
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
        layoutParams.gravity = Gravity.TOP;
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        getWindow().setDimAmount(0);
        getWindow().setAttributes(layoutParams);
        view = AAOP_HSkin.getLayoutInflater(SystemUIApplication.getSystemUIContext())
                .inflate(R.layout.view_notification_center_top, null);
        tvTopMessage = view.findViewById(R.id.tv_top_message);
        llRoot = view.findViewById(R.id.ll_root);

        if (!isAdded && null != mWindowManager) {
            if (null != view) {
                mWindowManager.addView(view, layoutParams);
                AAOP_HSkin.getWindowViewManager().addWindowView(view).applySkinForViews(true);
            }
            isAdded = true;
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                isSlideUp = false;
                break;

            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                float upY = downY - event.getY();
                if (upY > 20) {
                    TrackingMessageDataUtil.getInstance().trackingData(TrackingEnum.MESSAGE_SLIDE_UP.getBehaviorid());
                    dismiss();
                }
                if (upY < -20) {
                    if (messageType.equals("100")) {
                        Intent intent = new Intent(REQUEST_GUIDE);
                        intent.putExtra("poiName", messageBeanInfo.getData().getGpsName());
                        intent.putExtra("longitude", longitude);
                        intent.putExtra("latitude", latitude);
                        SystemUIApplication.getSystemUIContext().sendBroadcast(intent);
                    } else {
                        NotificationCenterView.getInstance().show(true, messageType);
                    }
                    dismiss();
                }
                if (upY == 0.0) {
                    if (messageType.equals("100")) {
                        Intent intent = new Intent(REQUEST_GUIDE);
                        intent.putExtra("poiName", messageBeanInfo.getData().getGpsName());
                        intent.putExtra("longitude", longitude);
                        intent.putExtra("latitude", latitude);
                        SystemUIApplication.getSystemUIContext().sendBroadcast(intent);
                    } else {
                        NotificationCenterView.getInstance().show(true, messageType);
                    }
                    dismiss();
                }

                break;

        }


        return super.onTouchEvent(event);

    }


    @Override
    public void show() {
        super.show();
        if (null != view && view.getVisibility() != View.VISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
        TrackingMessageDataUtil.getInstance().trackingData(TrackingEnum.MESSAGE_INFO.getBehaviorid());
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
        tvTopMessage.setText("");
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

}
