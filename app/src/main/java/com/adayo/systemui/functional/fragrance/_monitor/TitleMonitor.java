package com.adayo.systemui.functional.fragrance._monitor;

import com.adayo.systemui.functional.fragrance._contract.ITitleMonitor;

import java.util.HashSet;

public class TitleMonitor implements ITitleMonitor {
    private final HashSet<ITitleObservable> _items = new HashSet<>();

    public TitleMonitor() {

    }

    private static class Holder {
        private final static TitleMonitor INSTENCE = new TitleMonitor();
    }

    public static TitleMonitor getInstance() {
        return Holder.INSTENCE;
    }

    public void insertObserver(ITitleObservable observer) {
        if (observer == null) return;
        _items.add(observer);
    }

    @Override
    public void changeTitle(int position) {
        for (ITitleObservable observer : _items) {
            observer.onTitleChanged(position);
        }
    }
}
