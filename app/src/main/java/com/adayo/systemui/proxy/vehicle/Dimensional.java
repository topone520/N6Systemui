package com.adayo.systemui.proxy.vehicle;

import android.content.Context;
import android.view.ViewGroup;

import com.adayo.proxy.system.aaop_systemservice.util.AAOP_LogUtils;
import com.android.systemui.SystemUIApplication;
import com.cocos.effect.CocosBaicAir;

import java.util.ArrayList;
import java.util.List;

public class Dimensional {
    private static final String TAG = Dimensional.class.getSimpleName();

    public void onResume() {
        CocosBaicAir.getInstance().onResume();
    }

    public void onStart() {
        CocosBaicAir.getInstance().onStart();
    }

    private static class _Holder {
        private static final Dimensional _instance = new Dimensional();
    }

    public interface IHvacMdvAdjustListener {
        void onEvent(int position, float hor, float ver);
    }

    private Dimensional() {
    }

    private boolean _online = false;

    public static Dimensional getInstance() {
        return _Holder._instance;
    }

    public boolean isInitial() {
        return _online;
    }

    public void initial(ViewGroup viewGroup, int theme, int type) {
        AAOP_LogUtils.i(TAG, "initial111 =========");
        CocosBaicAir.getInstance().init(SystemUIApplication.getSystemUIContext(), viewGroup, theme, type, 0, () -> {
            Dimensional.this._online = true;
            AAOP_LogUtils.i(TAG, "initial222 =========");
            CocosBaicAir.getInstance().setHvacTouchCallback((i, v, v1) -> _dsListeners.forEach(it -> it.onEvent(i, v, v1)));
        });
    }

    public void doAction(final String key, final String action) {
        AAOP_LogUtils.i(TAG, "key: " + key + " action: " + action);
        if (!isInitial()) return;
        CocosBaicAir.getInstance().setAction(key, action);
    }

    private final List<IHvacMdvAdjustListener> _dsListeners = new ArrayList<>();

    public void subscribe(IHvacMdvAdjustListener listener) {
        if (listener == null) return;
        _dsListeners.add(listener);
    }
}
