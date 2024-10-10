package com.adayo.systemui.functional.fragrance._contract;

public interface ITitleMonitor {
    interface ITitleObservable {
        void onTitleChanged(final int position);
    }
    void changeTitle(final int position);
}
