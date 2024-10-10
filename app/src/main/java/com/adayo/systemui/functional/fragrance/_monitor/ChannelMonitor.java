package com.adayo.systemui.functional.fragrance._monitor;

import com.adayo.systemui.functional.fragrance._contract.IChannelMonitor;

import java.util.HashSet;

public class ChannelMonitor implements IChannelMonitor {
    private int _channel = -1;
    private final HashSet<IChannelObservable> _items = new HashSet<>();

    public ChannelMonitor() {
    }

    public void insertObserver(IChannelObservable observer) {
        if (observer == null) return;
        _items.add(observer);
    }

    @Override
    public void selectChannel(final int channel, final boolean isOpen) {
//        if (channel == _channel) return;
//        _channel = channel;
        for (IChannelObservable observer : _items) {
            observer.onChannelChanged(channel, isOpen);
        }
    }
}
