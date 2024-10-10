package com.adayo.systemui.manager;

import static android.app.Notification.EXTRA_TEXT;
import static android.app.Notification.EXTRA_TITLE;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.adayo.systemui.bean.WarningMessageInfo;
import com.adayo.systemui.utils.ResourceUtil;
import com.adayo.systemui.windows.views.NotificationCenterView;
import com.adayo.systemui.windows.views.NotificationTopDialog;
import com.android.systemui.SystemUIApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class NotificationManager {

    private static final String TAG = "NotificationManager";
    private static NotificationManager notificationManager;

    BlockingQueue<StatusBarNotification> statusBarNotificationBlockingQueue = new ArrayBlockingQueue<>(100);
    List<WarningMessageInfo> warningMessageInfoList = new ArrayList<>();
    private String messageType;

    public static NotificationManager getInstance() {
        if (null == notificationManager) {
            synchronized (NotificationManager.class) {
                if (null == notificationManager) {
                    notificationManager = new NotificationManager();
                }
            }
        }
        return notificationManager;
    }

    public NotificationManager(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true){
                    if (!isShow()){
                        try {
                            StatusBarNotification statusBarNotification = statusBarNotificationBlockingQueue.take();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showMessageByDiffType(statusBarNotification, messageType);
                                }
                            });

                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }



            }
        }).start();

    }

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {//todo 超时
            int id = msg.what;
            Log.d(TAG, "time out id = "+id);



        }
    };


    public void showMessage(StatusBarNotification statusBarNotification,String mMessageType){
        messageType = mMessageType;
        try {
            statusBarNotificationBlockingQueue.put(statusBarNotification);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean isShow(){

        return NotificationTopDialog.isShow();

    }

    private void showMessageByDiffType(StatusBarNotification sbn,String messageType){

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

            String title1 = jsonObject1.getString("title");
            String message1 = jsonObject1.getString("message");

            int sourceTitleId = ResourceUtil.getStringId(SystemUIApplication.getSystemUIContext(), title1);
            int sourceMessageId = ResourceUtil.getStringId(SystemUIApplication.getSystemUIContext(), message1);

            String titleText = SystemUIApplication.getSystemUIContext().getResources().getString(sourceTitleId);
            String messageText = SystemUIApplication.getSystemUIContext().getResources().getString(sourceMessageId);

            String tagId = sbn.getTag();
            WarningMessageInfo warningMessageInfo = new WarningMessageInfo();
            warningMessageInfo.setTitle(titleText);
            warningMessageInfo.setMessage(messageText);
            warningMessageInfo.setId(tagId);

            NotificationTopDialog.Builder()
                    .setMessage(warningMessageInfo.getMessage())
                    .setMessageType(messageType)
                    .show();

            //todo 按tag区分，一样的不提示
//            NotificationTopDialog.getInstance().addMessage(warningMessageInfo.getTitle());


        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }


}
