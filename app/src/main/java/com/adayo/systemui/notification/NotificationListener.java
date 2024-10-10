package com.adayo.systemui.notification;

import static android.app.Notification.EXTRA_TEXT;
import static android.app.Notification.EXTRA_TITLE;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.adayo.proxy.aaop_hskin.AAOP_HSkin;
import com.adayo.systemui.bean.MessageBeanInfo;
import com.adayo.systemui.bean.MessageBeanInfo1;
import com.adayo.systemui.bean.SystemMessageInfo;
import com.adayo.systemui.bean.WarningMessageInfo;
import com.adayo.systemui.manager.NotificationManager;
import com.adayo.systemui.utils.CustomToastUtil;
import com.adayo.systemui.utils.ResourceUtil;
import com.adayo.systemui.utils.SPHelper;
import com.adayo.systemui.utils.TrackingMessageDataUtil;
import com.adayo.systemui.windows.ColorEggDialog;
import com.adayo.systemui.windows.bars.StatusBar;
import com.adayo.systemui.windows.dialogs.CommonDialog;
import com.adayo.systemui.windows.views.NotificationCenterView;
import com.adayo.systemui.windows.views.NotificationTopDialog;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Notification listener.
 */
public class NotificationListener extends NotificationListenerService {
    /**
     * The constant TAG.
     */
    public static final String TAG = "NotifyService";
    private String mPreviousNotificationKey;
    private Long mPreviousNotificationKeyTime;
    private String btn1;
    private String btn2;
    /**
     * 类型为toast.
     */
    private static final String TYPE_TOAST = "type_toast";
    /**
     * 类型为dialog.
     */
    private static final String TYPE_DIALOG = "type_dialog";
    /**
     * 类型为消息中心.
     */
    private static final String TYPE_NOTIFICATION_CENTER = "type_notification_center";
    private static final String TYPE_SEND_TO_CAR = "type_send_to_car";
    /**
     * 类型为ota相关.
     */
    private static final String TYPE_OTA = "type_ota";
    /**
     * 默认显示在下方.
     */
    private int toastGravity = 2;
    private long toastDuration = 0;
    private int id;
    private long lastTime;
    private long currentTime;
    /**
     * The System message info list.
     */
    List<SystemMessageInfo> systemMessageInfoList = new ArrayList<>();
    /**
     * The Warning message info list.
     */
    List<WarningMessageInfo> warningMessageInfoList = new ArrayList<>();
    /**
     * The Message bean info list.
     */
    List<MessageBeanInfo> messageBeanInfoList = new ArrayList<>();
    private String systemMessageInfoJson;
    private PendingIntent pendingIntent;
    /**
     * 消息类型.
     */
    private int messageType = 0;
    /**
     * mqtt消息，1是彩蛋，99是其他.
     */
    private int dataType = 99;
    private MessageBeanInfo messageBeanInfo;
    private MessageBeanInfo1 messageBeanInfo1;
    private int btnColorBg1Id;
    private int btnColorBg2Id;


    /**
     * Instantiates a new Notification listener.
     */
    public NotificationListener() {

    }


    /**
     * 发布通知
     *
     * @param sbn 状态栏通知
     */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        //过滤同一消息接收到了两次
        Log.i(TAG, "onNotificationPosted");
        SPHelper spHelper = new SPHelper(SystemUIApplication.getSystemUIContext(), TAG);
        mPreviousNotificationKey = sbn.getKey();
        mPreviousNotificationKeyTime = sbn.getPostTime();
        String mPreviousNotification = spHelper.getString("notificationKey");
        Long mPreviousNotificationTime = spHelper.getLong("notificationTime");
        spHelper.putValues(new SPHelper.ContentValue("notificationKey", mPreviousNotificationKey));
        spHelper.putValues(new SPHelper.ContentValue("notificationTime", mPreviousNotificationKeyTime));
        if (mPreviousNotificationKey.equals(mPreviousNotification) && mPreviousNotificationKeyTime.equals(mPreviousNotificationTime)) {
            return;
        } else {
            spHelper.putValues(new SPHelper.ContentValue("notificationKey", mPreviousNotificationKey));
            spHelper.putValues(new SPHelper.ContentValue("notificationTime", mPreviousNotificationKeyTime));
            //记录时间
            lastTime = spHelper.getLong("time");
            currentTime = System.currentTimeMillis();
            spHelper.putValues(new SPHelper.ContentValue("time", currentTime));

            Bundle extras = new Bundle();
            extras = sbn.getNotification().extras;
            String type = extras.getString("type");//type
            String title = extras.getString(EXTRA_TITLE);//通知title
            CharSequence textChar = extras.getCharSequence(EXTRA_TEXT);
            Log.i(TAG, "title: " + title + "textChar: " + textChar + "");
            messageType = extras.getInt("message_type");
            dataType = extras.getInt("data_type");
            Log.i(TAG, "type: " + type + "messageType: " + messageType + "dataType: " + dataType);
            toastGravity = extras.getInt("toast_gravity", 2);
            toastDuration = extras.getLong("toast_duration", 0);

            String btn1DrawableName = extras.getString("btn1_drawable_name");
            String btn2DrawableName = extras.getString("btn2_drawable_name");
            Log.i(TAG, "btn1DrawableName s: " + btn1DrawableName + "btn2DrawableName s: " + btn2DrawableName);
            try {
                if (!TextUtils.isEmpty(btn1DrawableName)) {
                    btnColorBg1Id = ResourceUtil.getDrawableId(SystemUIApplication.getSystemUIContext(), btn1DrawableName);
                }
                if (!TextUtils.isEmpty(btn2DrawableName)) {
                    btnColorBg2Id = ResourceUtil.getDrawableId(SystemUIApplication.getSystemUIContext(), btn2DrawableName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.i(TAG, "btnColorBg2Id s: " + btnColorBg2Id);
            Notification.Action[] actions = sbn.getNotification().actions;
            if (actions != null) {
                if (actions.length > 1) {
                    btn1 = actions[0].title.toString();
                    btn2 = actions[1].title.toString();
                } else {
                    btn1 = actions[0].title.toString();
                }
                pendingIntent = actions[0].actionIntent;

            }

            if (type != null) {
                switch (type) {
                    case TYPE_DIALOG:
                        //1.11 切换语言体现为关闭弹窗
                        CommonDialog.createDialog(SystemUIApplication.getSystemUIContext(), R.style.NotTransparentStyle).setTitleName(title).setTips(textChar).setButtonOne(btn1, btnColorBg1Id, new CommonDialog.OnButtonClickListener() {
                            @Override
                            public void onButtonClick(CommonDialog commonDialog) {
                                try {
                                    Log.e(TAG, "try");
                                    if (pendingIntent != null) {
                                        pendingIntent.send();
                                    }
                                } catch (PendingIntent.CanceledException e) {
                                    e.printStackTrace();
                                    Log.e(TAG, "catch");
                                }
                            }
                        }).setButtonTwo(btn2, btnColorBg2Id, null).show();
                        break;
                    case TYPE_TOAST:
                        CustomToastUtil.getInstance().showToast(title, toastGravity, toastDuration);
                        break;
                    case TYPE_NOTIFICATION_CENTER:
                        TrackingMessageDataUtil.getInstance().trackingData(TrackingEnum.RECEIVE_MESSAGE.getBehaviorid());
                        Log.i(TAG, "TYPE_NOTIFICATION_CENTER");
                        StatusBar.getInstance().messageIconSetSelected(true);
                        Gson gson = new Gson();
                        if (messageType == 1) {
                            Log.i(TAG, "messageType1111111: ");
                            String jsonStr = "";
                            try {
                                InputStream inputStream = SystemUIApplication.getSystemUIContext().getAssets().open("ClusterAdapterAlarmNotify.json");
                                int size = inputStream.available();
                                byte[] buffer = new byte[size];
                                inputStream.read(buffer);
                                inputStream.close();
                                jsonStr = new String(buffer, StandardCharsets.UTF_8);
                                JSONObject jsonObject = new JSONObject(jsonStr);
                                JSONObject jsonObject1 = jsonObject.optJSONObject(sbn.getTag());

                                try {
                                    if (jsonObject1 != null) {
                                        String title1 = jsonObject1.getString("title");
                                        String message1 = jsonObject1.getString("message");
                                        Log.i(TAG, "title1: " + title1 + "message1: " + message1 + "");
                                        int sourceTitleId = ResourceUtil.getStringId(SystemUIApplication.getSystemUIContext(), title1);
                                        int sourceMessageId = ResourceUtil.getStringId(SystemUIApplication.getSystemUIContext(), message1);
                                        Log.i(TAG, "sourceTitleId: " + sourceTitleId + "sourceMessageId: " + sourceMessageId + "");
                                        String titleText = SystemUIApplication.getSystemUIContext().getResources().getString(sourceTitleId);
                                        String messageText = SystemUIApplication.getSystemUIContext().getResources().getString(sourceMessageId);
                                        String tagId = sbn.getTag();
                                        Log.i(TAG, "titleText: " + titleText + "messageText: " + messageText + "");
                                        WarningMessageInfo warningMessageInfo = new WarningMessageInfo();
                                        warningMessageInfo.setTitle(titleText);
                                        warningMessageInfo.setMessage(messageText);
                                        warningMessageInfo.setReceiveTime(currentTime);
                                        warningMessageInfo.setId(tagId);
                                        List<WarningMessageInfo> listUn = new ArrayList<>();
                                        listUn.add(0, warningMessageInfo);
                                        Log.i(TAG, "listUn" + gson.toJson(listUn));
                                        for (WarningMessageInfo s : listUn) {
                                            if (warningMessageInfoList.isEmpty()) {
                                                warningMessageInfoList.add(0, s);
                                            } else {
                                                boolean isSame = false;
                                                for (WarningMessageInfo s1 : warningMessageInfoList) {
                                                    if (s.getId().equals(s1.getId())) {
                                                        isSame = true;
                                                        break;
                                                    }
                                                }
                                                if (!isSame) {
                                                    warningMessageInfoList.add(0, s);
                                                }
                                            }
                                        }
                                        Log.i(TAG, "warningMessageInfoList" + gson.toJson(warningMessageInfoList));
                                        NotificationCenterView.getInstance().addWarningMessage(warningMessageInfoList);
                                        NotificationManager.getInstance().showMessage(sbn, String.valueOf(messageType));
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        } else {

                            id = spHelper.getInt("itemId");
                            id++;
                            spHelper.putValues(new SPHelper.ContentValue("itemId", id));

                            //保存数据
                            SystemMessageInfo systemMessageInfo = new SystemMessageInfo();
                            systemMessageInfo.setId(id);
                            messageBeanInfo = new MessageBeanInfo();
                            messageBeanInfo1 = new MessageBeanInfo1();
                            if (dataType == 21) {
                                //app指定導航位置信息
                                String messageJson = extras.getString("messageBeanInfoJson");
                                messageBeanInfo = gson.fromJson(messageJson, new TypeToken<MessageBeanInfo>() {
                                }.getType());
                                systemMessageInfo.setTitle(messageBeanInfo.getData().getMsgSource());
                                systemMessageInfo.setMessage(messageBeanInfo.getData().getGpsName());
                                systemMessageInfo.setReceiveTime(Long.valueOf(messageBeanInfo.getSendTime()));
                            } else if (dataType == 20) {
                                //流量提醒
                                String messageJson = extras.getString("messageBeanInfoJson");
                                messageBeanInfo = gson.fromJson(messageJson, new TypeToken<MessageBeanInfo>() {
                                }.getType());
                                //todo 需要根据packagename区分套餐来判断显示什么
//                                systemMessageInfo.setTitle(messageBeanInfo.getData().getMsgSource());
//                                systemMessageInfo.setMessage(messageBeanInfo.getData().getGpsName());
//                                systemMessageInfo.setReceiveTime(Long.valueOf(messageBeanInfo.getSendTime()));
                            } else if (dataType == 100) {
                                String messageJson = extras.getString("messageBeanInfoJson");
                                messageBeanInfo1 = gson.fromJson(messageJson, new TypeToken<MessageBeanInfo1>() {
                                }.getType());
                                Log.i(TAG, "messageBeanInfo1--------" + gson.toJson(messageBeanInfo1));
//                                systemMessageInfo.setTitle(messageBeanInfo.getData().getTitle());
//                                systemMessageInfo.setMessage(messageBeanInfo.getData().getDescribe());
//                                systemMessageInfo.setCover(messageBeanInfo.getData().getCover());
//                                systemMessageInfo.setReceiveTime(messageBeanInfo.getData().getUpdateTime());
                                //测试数据
                                systemMessageInfo.setTitle("圣诞节快乐");
                                systemMessageInfo.setMessage("圣诞节快乐哈啊哈哈哈啊哈哈哈哈哈");
                                systemMessageInfo.setCover("");
                                systemMessageInfo.setReceiveTime(1000000);
                            } else {
                                systemMessageInfo.setTitle(title);
                                if (!TextUtils.isEmpty(textChar)){
                                    systemMessageInfo.setMessage(textChar.toString());
                                }
                                systemMessageInfo.setReceiveTime(currentTime);
                            }
                            String messageDetailJson = gson.toJson(systemMessageInfo);
                            Log.i(TAG, "messageDetailJson" + messageDetailJson);
                            spHelper.putValues(new SPHelper.ContentValue("messageDetailJson", messageDetailJson));

                            if (gson.fromJson(spHelper.getString("systemMessageInfoJson"), new TypeToken<List<SystemMessageInfo>>() {
                            }.getType()) != null) {
                                systemMessageInfoList = gson.fromJson(spHelper.getString("systemMessageInfoJson"), new TypeToken<List<SystemMessageInfo>>() {
                                }.getType());
                            }
                            systemMessageInfoList.add(0, systemMessageInfo);
                            systemMessageInfoJson = gson.toJson(systemMessageInfoList);
                            spHelper.putValues(new SPHelper.ContentValue("systemMessageInfoJson", systemMessageInfoJson));
                            Log.i(TAG, "systemMessageInfoJson11" + systemMessageInfoJson);
                            String ss = spHelper.getString("systemMessageInfoJson");
                            Log.i(TAG, "systemMessageInfoJson22" + ss);
                            if (dataType == 21 || dataType == 20) {
                                NotificationTopDialog.Builder()
                                        .setMessage(messageBeanInfo.getData().getMsgSource())
                                        .setMessageType(String.valueOf(100))
                                        .show();
                            } else if (dataType == 100) {
                                ColorEggDialog.createDialog(SystemUIApplication.getSystemUIContext(), R.style.NotTransparentStyle).setMessageInfo(messageBeanInfo1).show();
                            } else {
                                NotificationTopDialog.Builder()
                                        .setMessage(systemMessageInfo.getMessage())
                                        .setMessageType(String.valueOf(messageType))
                                        .show();
                            }

                        }
                        break;
                    case TYPE_SEND_TO_CAR:
//                        NotificationTopDialog.Builder()
//                                    .setTitle(warningMessageInfo.getTitle())
//                                .setMessage(messageBeanInfo.getData().getGpsName())
//                                .setMessageType(String.valueOf(100))
//
//                                .show();
                        break;
                    case TYPE_OTA:
                        String model = extras.getString("model");
                        String version = extras.getString("version");
                        String time = extras.getString("time");
                        int otaState = extras.getInt("state");
                        StatusBar.getInstance().otaIconSetSelected(model, version, time);
                        StatusBar.getInstance().otaIconStatus(otaState);
                        break;
                }
            }


        }

    }


    /**
     * 通知已删除
     *
     * @param sbn 状态栏通知
     */
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        for (WarningMessageInfo warningMessageInfo : warningMessageInfoList) {
            if (warningMessageInfo.getId().equals(sbn.getTag())) {
                warningMessageInfoList.remove(warningMessageInfo);
            }
        }
        NotificationCenterView.getInstance().addWarningMessage(warningMessageInfoList);
    }

    /**
     * 监听断开
     */
    @Override
    public void onListenerDisconnected() {
        // 通知侦听器断开连接 - 请求重新绑定
        requestRebind(new ComponentName(this, NotificationListenerService.class));
    }

    /**
     * 注册系统服务.
     *
     * @param context       the context
     * @param componentName the component name
     * @param currentUser   the current user
     * @throws RemoteException the remote exception
     */
    @Override
    public void registerAsSystemService(Context context, ComponentName componentName, int currentUser) throws RemoteException {
        super.registerAsSystemService(context, componentName, currentUser);
//        Dependency.get(PluginManager.class).addPluginListener(this,
//                NotificationListenerController.class);
    }

    /**
     * Unregister as system service.
     *
     * @throws RemoteException the remote exception
     */
    @Override
    public void unregisterAsSystemService() throws RemoteException {
        super.unregisterAsSystemService();
//        Dependency.get(PluginManager.class).removePluginListener(this);
    }

    /**
     * Sets up with presenter.
     */
    public void setUpWithPresenter() {
        Log.e(TAG, "setUpWithPresenter");

        try {
            registerAsSystemService(SystemUIApplication.getSystemUIContext(), new ComponentName(SystemUIApplication.getSystemUIContext().getPackageName(), getClass().getCanonicalName()), -1);
//            toggleNotificationListenerService();
        } catch (RemoteException e) {
            Log.e(TAG, "Unable to register notification listener", e);
        }
    }
}
