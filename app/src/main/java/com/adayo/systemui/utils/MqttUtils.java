package com.adayo.systemui.utils;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.car.VehiclePropertyIds;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;


import com.adayo.proxy.deviceservice.AAOP_DeviceServiceManager;
import com.adayo.proxy.infrastructure.adayosource.AdayoSource;
import com.adayo.proxy.infrastructure.sourcemng.Beans.AudioCbInfo;
import com.adayo.proxy.infrastructure.sourcemng.Beans.SourceInfo;
import com.adayo.proxy.infrastructure.sourcemng.Beans.UIDCbInfo;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngAudioSwitchManager;
import com.adayo.proxy.infrastructure.sourcemng.Control.SrcMngSourceInfoMng;
import com.adayo.proxy.infrastructure.sourcemng.Interface.IAdayoAudioFocusChange;
import com.adayo.proxy.infrastructure.sourcemng.Interface.IAdayoSourceListener;
import com.adayo.systemui.bean.MessageBeanInfo;
import com.adayo.systemui.manager.ColorEggBuleCallStateManager;
import com.adayo.systemui.manager.LocalSSLException;
//import com.adayo.systemui.windows.views.ColorEggUI;
import com.android.systemui.R;
import com.android.systemui.SystemUIApplication;
import com.baic.icc.mqtt.lib.listener.IMqttResponseListener;
import com.baic.icc.mqtt.lib.manager.MqttManager;
import com.baic.icc.mqtt.lib.state.MqttConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * The type Mqtt utils.
 *
 * @author :hailong.li
 * @date : 2023/12/13 desc :
 */
public class MqttUtils {

    private final String TAG = "_MqttUtils";
    private static MqttUtils instance;

    private Context mContext;

    private MqttManager mqttManager = null;

    private boolean isDownload = false;
    private static final int TYPE_SEND_TO_CAR = 21;
    /**
     * 流量提醒.
     *
     */
    private static final int TYPE_DATA_RECOMMEND = 20;
    private static final int TYPE_OTHER = 100;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MqttUtils getInstance() {
        // 避免不必要的同步
        if (instance == null) {
            synchronized (MqttUtils.class) {
                // 在第一次调用事初始化
                if (instance == null) {
                    instance = new MqttUtils();
                }
            }
        }
        return instance;
    }

    /**
     * Init.
     *
     * @param context the context
     */
    public void init(Context context) {
        mContext = context;
        Log.i(TAG, " 开始初始化 ");
        mqttManager = MqttManager.getInstance(context);
        if (!mqttManager.isConnected()) {
            register();
        }
//        mContext.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                sendNotification("测试","圣诞快乐，愿你生活充满幸福和欢笑");
////                saveVideo("https://vjs.zencdn.net/v/oceans.mp4","彩蛋");
//            }
//        }, new IntentFilter("com.adayo.systemui.message"));

    }

    private int dateType;
    private IMqttResponseListener mqttResponseListener = new IMqttResponseListener() {
        @Override
        public void onReceiveRespond(String topic, String peload) {
            Log.i(TAG, " topic === " + topic);
            Log.i(TAG, " peload === " + peload);
            if (MqttConstants.KEY_TO_DEVICE.equals(topic) || MqttConstants.KEY_TO_CARTYPE.equals(topic)) {
                try {
                    JSONObject jsonObject = new JSONObject(peload);
                    String data = jsonObject.getString("data");
                    JSONObject jsonData = new JSONObject(data);
                    dateType = jsonObject.getInt("dataType");
                    Log.i(TAG, "onReceiveRespond: datatype --> " + dateType);
                    switch (dateType) {
                        case TYPE_SEND_TO_CAR:
                            MessageBeanInfo messageBeanInfo = new MessageBeanInfo();
                            MessageBeanInfo.Data data1 = new MessageBeanInfo.Data();
                            String gps = jsonData.getString("gps");
                            String gpsName = jsonData.getString("gpsName");
                            String gpsAddress = jsonData.getString("gpsAddress");
                            String msgSource = jsonData.getString("msgSource");

                            Log.i(TAG, "onReceiveRespond: gps --> " + gps + "onReceiveRespond: gpsName --> " + gpsName);
                            Log.i(TAG, "onReceiveRespond: gpsAddress --> " + gpsAddress + "onReceiveRespond: msgSource --> " + msgSource);
                            data1.setGps(gps);
                            data1.setGpsName(gpsName);
                            data1.setGpsAddress(gpsAddress);
                            data1.setMsgSource(msgSource);
                            messageBeanInfo.setData(data1);
                            sendNotification("", "", messageBeanInfo);
                            break;
                        case TYPE_DATA_RECOMMEND:
                            MessageBeanInfo messageBeanInfo1 = new MessageBeanInfo();
                            MessageBeanInfo.Data data2 = new MessageBeanInfo.Data();
                            MessageBeanInfo.Data.FlowAlarm flowAlarmBean = new MessageBeanInfo.Data.FlowAlarm();
                            MessageBeanInfo.Data.TimeAlarm timeAlarmBean = new MessageBeanInfo.Data.TimeAlarm();
                            String packageName = jsonData.getString("packageName");
                            JSONObject flowAlarm = jsonData.getJSONObject("flowAlarm");
                            String flowLast = flowAlarm.getString("last");
                            JSONObject timeAlarm = jsonData.getJSONObject("timeAlarm");
                            String timeLast = timeAlarm.getString("last");
                            data2.setPackageName(packageName);
                            flowAlarmBean.setLast(flowLast);
                            timeAlarmBean.setLast(timeLast);
                            data2.setFlowAlarm(flowAlarmBean);
                            data2.setTimeAlarm(timeAlarmBean);
                            messageBeanInfo1.setData(data2);
                            sendNotification("", "", messageBeanInfo1);
                            break;
                        case TYPE_OTHER:

                            int dataType = jsonData.getInt("type"); //1是彩蛋， 其他都是其他
                            int msgId = jsonData.getInt("msgId");
                            if (dataType != 1) {
                                return;
                            }
                            Log.i(TAG, "onReceiveRespond: datatype --> " + dataType + "onReceiveRespond: msgId --> " + msgId);
                            NetworkUtils.getInstance().sendHttps(String.valueOf(dataType), String.valueOf(msgId), new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    // LocalSSLException为自定义类 处理拦截器返回的异常，进行对应的ui处理
                                    if (e instanceof LocalSSLException.PdsnException) {
                                        Log.d("TspService", "  PdsnException  :" + e.getMessage());
                                    } else if (e instanceof LocalSSLException.VerityException) {
                                        Log.d("TspService", "  VerityException  :" + e.getMessage());
                                    } else if (e instanceof LocalSSLException.SelfException) {
                                        Log.d("TspService", "  SelfException  :" + e.getMessage());
                                    } else {
                                        Log.d("TspService", "  BaicSSLException  :" + e.getMessage());
                                    }
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    if (response.code() == 200) {
                                        String result = response.body().string();
                                        Log.i(TAG, "onResponse: result --> " + result);
                                        try {
                                            JSONObject jsonObject = new JSONObject(result);
                                            String code = jsonObject.getString("statusCode");
                                            String data = jsonObject.getString("data");
                                            if ("0".equals(code)) {
                                                JSONObject jsonData = new JSONObject(data);
                                                String url = jsonData.getString("fileUrl");
                                                String title = jsonData.getString("title");
                                                String describe = jsonData.getString("describe");
                                                String cover = jsonData.getString("cover");
                                                long updateTime = jsonData.getLong("updateTime");
                                                int msgId = jsonData.getInt("msgId");
                                                MessageBeanInfo messageBeanInfo2 = new MessageBeanInfo();
                                                MessageBeanInfo.Data data2 = new MessageBeanInfo.Data();
                                                data2.setMsgId(msgId);
                                                data2.setTitle(title);
                                                data2.setDescribe(describe);
                                                data2.setCover(cover);
                                                data2.setUpdateTime(updateTime);
                                                messageBeanInfo2.setData(data2);
                                                sendNotification(title, describe, messageBeanInfo2);
                                                SPHelper spHelper = new SPHelper(SystemUIApplication.getSystemUIContext().getApplicationContext(), "coloregg");
                                                SPHelper.ContentValue contentValue = new SPHelper.ContentValue("coloreggMessage", data);
                                                spHelper.putValues(contentValue);
                                                if (!TextUtils.isEmpty(url)) {
                                                    saveVideo(url, title);
                                                }
                                            }
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                            });
                            break;
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        @Override
        public void onReceiveState(int state) {
            Log.i(TAG, "   onReceiveState === " + state);
            if (state == 1) {
                NetworkUtils.getInstance().sendDeviceOnline(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        // LocalSSLException为自定义类 处理拦截器返回的异常，进行对应的ui处理
                        if (e instanceof LocalSSLException.PdsnException) {
                            Log.d("TspService", "  PdsnException  :" + e.getMessage());
                        } else if (e instanceof LocalSSLException.VerityException) {
                            Log.d("TspService", "  VerityException  :" + e.getMessage());
                        } else if (e instanceof LocalSSLException.SelfException) {
                            Log.d("TspService", "  SelfException  :" + e.getMessage());
                        } else {
                            Log.d("TspService", "  BaicSSLException  :" + e.getMessage());
                        }
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.code() == 200) {
                            String result = response.body().string();
                            Log.i(TAG, "onResponse: result --> " + result);
                        }
                    }
                });
            }
        }
    };

    private void register() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mqttManager.addNetworkMqttRequestListener(mqttResponseListener, mContext.getPackageName(), new String[]{MqttConstants.KEY_TO_DEVICE, MqttConstants.KEY_TO_CARTYPE, MqttConstants.KEY_TO_ACCOUNTID});
            }
        });
    }

    /**
     * Send notification.
     *
     * @param title           the title
     * @param content         the content
     * @param messageBeanInfo the message bean info
     */
    public void sendNotification(String title, String content, MessageBeanInfo messageBeanInfo) {
        Log.i(TAG, "sendNotification: title --> " + title);
        NotificationManager notifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        //创建一个默认重要性的通知渠道 id每个包名不同
        NotificationChannel channel = new NotificationChannel("com.adayo.systemui.message", "notification", NotificationManager.IMPORTANCE_DEFAULT);
        notifyMgr.createNotificationChannel(channel);
        //创建一个通知消息的构造器
        Notification.Builder builder = new Notification.Builder(mContext);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //Android8.0开始必须给每个通知分配对应的渠道
            builder = new Notification.Builder(mContext, "com.adayo.systemui.message");
        }
        Bundle bundle = new Bundle();
//        bundle.putInt(NOTIFICATION_TYPE, NOTIFICTION);
//        bundle.putString(BUTTON_EXECUTE, "播放");
//        bundle.putString(BUTTON_CANCEL, "取消");
//        bundle.putString("action", "com.adayo.systemui.coloregg");
        bundle.putString("type", "type_notification_center");
        bundle.putInt("data_type", dateType);
        bundle.putSerializable("messageBeanInfoJson", messageBeanInfo);
//        bundle.putString("videoUrl", url);

        //使用通知管理器推送通知，然后在手机的通知栏就会看到消息 id如果相同则覆盖之前消息
        builder.setContentText(content)
//                .setContentIntent(PendingIntent.getActivity())
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher))
                .setExtras(bundle);
        ;//设置状态栏/导航栏/侧边栏图标

//                .setExtras(bundle);//必须设置
        Notification notify = builder.build();
        //使用通知管理器推送通知，然后在手机的通知栏就会看到消息 id如果相同则覆盖之前消息
        notifyMgr.notify(1, notify);
    }

    private void saveVideo(String httpUrl, String title) {
        if (!isDownload) {
            new Thread(() -> {
                File file = new File("/sdcard/Movies/coloregg/" + title + ".mp4");
                if (file.exists()) {
                    file.delete();
                }
                // 创建一个用于保存视频的ContentValues
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, title + ".mp4");
                values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/coloregg");
                // 获取ContentResolver并插入新的视频
                ContentResolver resolver = mContext.getContentResolver();
                Uri uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
                // 使用返回的Uri来写入文件
                try (OutputStream out = resolver.openOutputStream(uri)) {
                    // 下载在线视频并写入文件
                    URL url = new URL(httpUrl);
                    try (InputStream in = url.openStream()) {
                        isDownload = true;
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                    }
                    isDownload = false;
                } catch (IOException e) {
                    e.printStackTrace();
                    if (file.exists()) {
                        file.delete();
                    }
                    isDownload = false;
                }
            }).start();
        }
    }

    /**
     * Is download boolean.
     *
     * @return the boolean
     */
    public boolean isDownload() {
        return isDownload;
    }
}