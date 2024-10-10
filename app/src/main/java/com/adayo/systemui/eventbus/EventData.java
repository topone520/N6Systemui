package com.adayo.systemui.eventbus;

public class EventData {
    public EventData(int type, Object data) {
        this.type = type;
        this.data = data;
    }

    public EventData(int type, Object data, int value) {
        this.type = type;
        this.data = data;
        this.value = value;
    }

    private int type;
    private Object data;
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
