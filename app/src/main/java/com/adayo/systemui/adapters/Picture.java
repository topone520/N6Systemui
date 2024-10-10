package com.adayo.systemui.adapters;

import android.widget.TextView;

/**
 * 网络图片数据实体
 */
public class Picture {
    public int id;


    public Integer url;//图片url链接

    public String text;

    public Picture(int id, Integer url, String textView) {
        this.id = id;
        this.url = url;
        this.text = textView;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Integer getUrl() {
        return url;
    }

    public void setUrl(Integer url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
