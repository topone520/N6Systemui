package com.adayo.systemui.notification;

import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import java.util.ArrayList;

public class NotificationListenerWithPlugins extends NotificationListenerService implements
        PluginListener<NotificationListenerController>{

    private ArrayList<NotificationListenerController> mPlugins = new ArrayList<>();
    private boolean mConnected;



    @Override
    public void registerAsSystemService(Context context, ComponentName componentName,
                                        int currentUser) throws RemoteException {
        super.registerAsSystemService(context, componentName, currentUser);
//        Dependency.get(PluginManager.class).addPluginListener(this,
//                NotificationListenerController.class);
    }

    @Override
    public void unregisterAsSystemService() throws RemoteException {
        super.unregisterAsSystemService();
//        Dependency.get(PluginManager.class).removePluginListener(this);
    }


    @Override
    public StatusBarNotification[] getActiveNotifications() {
        StatusBarNotification[] activeNotifications = super.getActiveNotifications();
        for (NotificationListenerController plugin : mPlugins) {
            activeNotifications = plugin.getActiveNotifications(activeNotifications);
        }
        return activeNotifications;
    }

    @Override
    public RankingMap getCurrentRanking() {
        RankingMap currentRanking = super.getCurrentRanking();
        for (NotificationListenerController plugin : mPlugins) {
            currentRanking = plugin.getCurrentRanking(currentRanking);
        }
        return currentRanking;
    }

    public void onPluginConnected() {
        mConnected = true;
        mPlugins.forEach(p -> p.onListenerConnected(getProvider()));
    }

    /**
     * Called when listener receives a onNotificationPosted.
     * Returns true to indicate this callback should be skipped.
     */
    public boolean onPluginNotificationPosted(StatusBarNotification sbn,
                                              final RankingMap rankingMap) {
        for (NotificationListenerController plugin : mPlugins) {
            if (plugin.onNotificationPosted(sbn, rankingMap)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Called when listener receives a onNotificationRemoved.
     * Returns true to indicate this callback should be skipped.
     */
    public boolean onPluginNotificationRemoved(StatusBarNotification sbn,
                                               final RankingMap rankingMap) {
        for (NotificationListenerController plugin : mPlugins) {
            if (plugin.onNotificationRemoved(sbn, rankingMap)) {
                return true;
            }
        }
        return false;
    }

    public RankingMap onPluginRankingUpdate(RankingMap rankingMap) {
        return getCurrentRanking();
    }

    @Override
    public void onPluginConnected(NotificationListenerController plugin, Context pluginContext) {
        mPlugins.add(plugin);
        if (mConnected) {
            plugin.onListenerConnected(getProvider());
        }
    }

    @Override
    public void onPluginDisconnected(NotificationListenerController plugin) {
        mPlugins.remove(plugin);
    }

    private NotificationListenerController.NotificationProvider getProvider() {
        return new NotificationListenerController.NotificationProvider() {
            @Override
            public StatusBarNotification[] getActiveNotifications() {
                return NotificationListenerWithPlugins.super.getActiveNotifications();
            }

            @Override
            public RankingMap getRankingMap() {
                return NotificationListenerWithPlugins.super.getCurrentRanking();
            }

            @Override
            public void addNotification(StatusBarNotification sbn) {
                onNotificationPosted(sbn, getRankingMap());
            }

            @Override
            public void removeNotification(StatusBarNotification sbn) {
                onNotificationRemoved(sbn, getRankingMap());
            }

            @Override
            public void updateRanking() {
                onNotificationRankingUpdate(getRankingMap());
            }
        };
    }
}
