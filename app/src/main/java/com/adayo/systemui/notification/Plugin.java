package com.adayo.systemui.notification;

import android.content.Context;

public interface Plugin {
    default int getVersion() {
        // Default of -1 indicates the plugin supports the new Requires model.
        return -1;
    }

    default void onCreate(Context sysuiContext, Context pluginContext) {
    }

    default void onDestroy() {
    }
}
