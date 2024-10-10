package com.adayo.systemui.bean;


import android.os.Parcel;
import android.os.Parcelable;

import com.adayo.systemui.utils.AccessPoint;

/**
 * Wifi模块错误信息
 */
public class WifiErrorMsg implements Parcelable {

    private AccessPoint mAccessPoint;
    private int errorCode;

    public AccessPoint getAccessPoint() {
        return mAccessPoint;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.mAccessPoint);
        dest.writeInt(this.errorCode);
    }

    public void readFromParcel(Parcel source) {
        this.mAccessPoint = (AccessPoint) source.readSerializable();
        this.errorCode = source.readInt();
    }

    public WifiErrorMsg() {
    }

    public WifiErrorMsg(AccessPoint accessPoint, int errorCode) {
        this.mAccessPoint = accessPoint;
        this.errorCode = errorCode;
    }

    protected WifiErrorMsg(Parcel in) {
        this.mAccessPoint = (AccessPoint) in.readSerializable();
        this.errorCode = in.readInt();
    }

    public static final Creator<WifiErrorMsg> CREATOR = new Creator<WifiErrorMsg>() {
        @Override
        public WifiErrorMsg createFromParcel(Parcel source) {
            return new WifiErrorMsg(source);
        }

        @Override
        public WifiErrorMsg[] newArray(int size) {
            return new WifiErrorMsg[size];
        }
    };
}
