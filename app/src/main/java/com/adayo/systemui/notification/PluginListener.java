package com.adayo.systemui.notification;

import android.content.Context;

public interface PluginListener<T extends Plugin> {
    /**
     * Called when the plugin has been loaded and is ready to be used.
     * This may be called multiple times if multiple plugins are allowed.
     * It may also be called in the future if the plugin package changes
     * and needs to be reloaded.
     */
    void onPluginConnected(T plugin, Context pluginContext);

    /**
     * Called when a plugin has been uninstalled/updated and should be removed
     * from use.
     */
    default void onPluginDisconnected(T plugin) {
        // Optional.
    }
}
