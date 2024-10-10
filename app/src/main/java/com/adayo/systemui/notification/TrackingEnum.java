package com.adayo.systemui.notification;

/**
 * The enum Tracking enum.
 */
public enum TrackingEnum {

    /**
     * 新消息接收.
     */
    RECEIVE_MESSAGE("2012001"),
    /**
     * 消息内容展示.
     */
    MESSAGE_INFO("2012002"),
    /**
     * 消息删除.
     */
    MESSAGE_DEL("2012003"),
    /**
     * 消息划走.
     */
    MESSAGE_SLIDE_UP("2012004");

    public String getBehaviorid() {
        return behaviorid;
    }

    private String behaviorid;
    TrackingEnum(String behaviorid) {
        this.behaviorid = behaviorid;
    }
}
