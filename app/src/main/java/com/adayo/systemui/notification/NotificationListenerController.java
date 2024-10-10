package com.adayo.systemui.notification;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.adayo.systemui.notification.annotations.DependsOn;
import com.adayo.systemui.notification.annotations.ProvidesInterface;

@ProvidesInterface(action = NotificationListenerController.ACTION,
        version = NotificationListenerController.VERSION)
@DependsOn(target = NotificationListenerController.NotificationProvider.class)
public interface NotificationListenerController extends Plugin {
    String ACTION = "com.android.systemui.action.PLUGIN_NOTIFICATION_ASSISTANT";
    int VERSION = 1;

    void onListenerConnected(NotificationProvider provider);

    default boolean onNotificationPosted(StatusBarNotification sbn, NotificationListenerService.RankingMap rankingMap) {
        return false;
    }
    default boolean onNotificationRemoved(StatusBarNotification sbn, NotificationListenerService.RankingMap rankingMap) {
        return false;
    }

    default StatusBarNotification[] getActiveNotifications(
            StatusBarNotification[] activeNotifications) {
        return activeNotifications;
    }

    default NotificationListenerService.RankingMap getCurrentRanking(NotificationListenerService.RankingMap currentRanking) {
        return currentRanking;
    }

    @ProvidesInterface(version = NotificationProvider.VERSION)
    interface NotificationProvider {
        int VERSION = 1;

        // Methods to get info about current notifications
        StatusBarNotification[] getActiveNotifications();
        NotificationListenerService.RankingMap getRankingMap();

        // Methods to notify sysui of changes to notification list.
        void addNotification(StatusBarNotification sbn);
        void removeNotification(StatusBarNotification sbn);
        void updateRanking();
    }
}
