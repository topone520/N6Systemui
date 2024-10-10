package com.adayo.systemui.bean;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;

import java.io.Serializable;

public class AppInfo implements Serializable,Comparable<AppInfo>{

    private Drawable image;
    private String appName;
    private String packageName="";
    private String source="";
    private int numberStart;
    private int messageNotification = 1;
    private boolean newInstall;

    private int index = 0;

    private int messageNum = 0;

    private Bitmap bg;

    private boolean isCanDrag = true;

    private boolean isLock = false;

    private boolean isCanClick = true;
    private boolean isShowDelete = false;
    private boolean isAdd = false;

    public int appId;
    public int category;
    public String iconPath;
    public ParcelFileDescriptor iconDesc;

    public boolean isAdd() {
        return isAdd;
    }

    public void setAdd(boolean add) {
        isAdd = add;
    }

    public AppInfo() {

    }

    public boolean isNewInstall() {
        return newInstall;
    }

    public void setNewInstall(boolean newInstall) {
        this.newInstall = newInstall;
    }

    public int getMessageNotification() {
        return messageNotification;
    }

    public void setMessageNotification(int messageNotification) {
        this.messageNotification = messageNotification;
    }

    public int getNumberStart() {
        return numberStart;
    }

    public void setNumberStart(int numberStart) {
        this.numberStart = numberStart;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getKey(){
        if (!TextUtils.isEmpty(source)){
            return source;
        } else {
            return packageName;
        }

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int compareTo(AppInfo o) {
        return o.getNumberStart() - this.numberStart;
    }

    public int getMessageNum() {
        return messageNum;
    }

    public void setMessageNum(int messageNum) {
        this.messageNum = messageNum;
    }

    public Bitmap getBg() {
        return bg;
    }

    public void setBg(Bitmap bg) {
        this.bg = bg;
    }

    public boolean isCanDrag() {
        return isCanDrag;
    }

    public void setCanDrag(boolean canDrag) {
        isCanDrag = canDrag;
    }


    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public void setIsCanClick (boolean isCanClick) {
        this.isCanClick = isCanClick;
    }

    public boolean getIsCanClick() {
        return isCanClick;
    }

    public void setIsShowDelete(boolean isShowDelete) {
        this.isShowDelete = isShowDelete;
    }

    public boolean getIsShowDelete() {
        return isShowDelete;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public ParcelFileDescriptor getIconDesc() {
        return iconDesc;
    }

    public void setIconDesc(ParcelFileDescriptor iconDesc) {
        this.iconDesc = iconDesc;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "image=" + image +
                ", appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", source='" + source + '\'' +
                ", numberStart=" + numberStart +
                ", messageNotification=" + messageNotification +
                ", newInstall=" + newInstall +
                ", index=" + index +
                ", messageNum=" + messageNum +
                ", bg=" + bg +
                ", isCanDrag=" + isCanDrag +
                ", isLock=" + isLock +
                ", isCanClick=" + isCanClick +
                ", isShowDelete=" + isShowDelete +
                ", isAdd=" + isAdd +
                ", appId=" + appId +
                ", category=" + category +
                ", iconPath='" + iconPath + '\'' +
                ", iconDesc=" + iconDesc +
                '}';
    }
}
