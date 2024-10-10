package com.adayo.systemui.notification;

import android.service.notification.StatusBarNotification;

public class NotifyHelper {
    private static NotifyHelper instance;

    private NotifyListener notifyListener;

    public static NotifyHelper getInstance() {
        if (instance == null) {
            instance = new NotifyHelper();
        }
        return instance;
    }

    /**
     * 收到消息
     * @param type 消息类型
     */
    public void onReceive(int type) {
        if (notifyListener != null) {
            notifyListener.onReceiveMessage(type);
        }

    }

    /**
     * 收到消息
     * @param sbn 状态栏通知
     */
    public void onReceive(StatusBarNotification sbn) {
        if(notifyListener != null) {
            notifyListener.onReceiveMessage(sbn);
        }
    }

    /**
     * 移除消息
     * @param type 消息类型
     */
    public void onRemoved(int type) {
        if (notifyListener != null) {
            notifyListener.onRemovedMessage(type);
        }
    }

    /**
     * 移除消息
     * @param sbn 状态栏通知
     */
    public void onRemoved(StatusBarNotification sbn) {
        if (notifyListener != null) {
            notifyListener.onRemovedMessage(sbn);
        }
    }

    /**
     * 设置回调方法
     *
     * @param notifyListener 通知监听
     */
    public void setNotifyListener(NotifyListener notifyListener) {
        this.notifyListener = notifyListener;
    }
}
