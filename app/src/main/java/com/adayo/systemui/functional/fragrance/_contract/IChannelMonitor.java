package com.adayo.systemui.functional.fragrance._contract;

public interface IChannelMonitor {
    interface IChannelObservable {
        void onChannelChanged(final int channel, final boolean isOpen);
    }
    void selectChannel(final int channel, final boolean isOpen);
}
