package com.adayo.systemui.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class MessageBeanInfo1 implements Parcelable {

    /**
     * dataType = 100
     * private Integer type;
     */
    private Integer msgId;

    private String title;
    private String describe;
    private Integer fileType;
    private String fileUrl;
    private String cover;
    private Double fileSize;
    private Long updateTime;
    private Long startTime;
    private Long endTime;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Double getFileSize() {
        return fileSize;
    }

    public void setFileSize(Double fileSize) {
        this.fileSize = fileSize;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(msgId);
        parcel.writeString(title);
        parcel.writeString(describe);
        parcel.writeInt(fileType);
        parcel.writeString(fileUrl);
        parcel.writeString(cover);
        parcel.writeDouble(fileSize);
        parcel.writeLong(updateTime);
        parcel.writeLong(startTime);
        parcel.writeLong(endTime);
    }

    public static final Creator<MessageBeanInfo1> CREATOR = new Creator<MessageBeanInfo1>() {
        @Override
        public MessageBeanInfo1 createFromParcel(Parcel parcel) {
            return new MessageBeanInfo1();
        }

        @Override
        public MessageBeanInfo1[] newArray(int i) {
            return new MessageBeanInfo1[i];
        }
    };
}
