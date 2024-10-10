package com.adayo.systemui.room.bean;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "VtpFragranceSlotTable")
public class VtpFragranceInfo {

    @PrimaryKey
    @ColumnInfo(name = "position")
    private int position; //通道位置
    @ColumnInfo(name = "type")
    private int type; //香型种类
    @ColumnInfo(name = "title")
    private String title; //香型名
    @ColumnInfo(name = "slide")
    private int slide; //滑动时背景
    @ColumnInfo(name = "cover")
    private int cover; //静止时背景
    @ColumnInfo(name = "background")
    private int background; //全局背景
    @ColumnInfo(name = "write")
    private int write; //全局文字



    public VtpFragranceInfo() {

    }

    @Ignore
    public VtpFragranceInfo(int position, int type, String title) {
        this.position = position;
        this.type = type;
        this.title = title;
    }

    @Ignore
    public VtpFragranceInfo(int position, int type, String title, int slide, int cover, int background, int write) {
        this.position = position;
        this.type = type;
        this.title = title;
        this.slide = slide;
        this.cover = cover;
        this.background = background;
        this.write = write;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSlide() {
        return slide;
    }

    public void setSlide(int slide) {
        this.slide = slide;
    }

    public int getCover() {
        return cover;
    }

    public void setCover(int cover) {
        this.cover = cover;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public int getWrite() {
        return write;
    }

    public void setWrite(int write) {
        this.write = write;
    }
}
