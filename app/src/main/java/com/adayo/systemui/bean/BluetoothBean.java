package com.adayo.systemui.bean;

    //
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
    public final class BluetoothBean {
        private String mAddress;
        private String mRemoteDevName;
        private byte[] mDevAdr = new byte[6];
        private int mDevID = 0;
        private int category;
        private boolean isPaired;
        private boolean isHfpConnected;
        private boolean isMaster;
        private boolean isA2dpConnected;
        private boolean isBtConnecting;
        private boolean isMapConnected;
        private int mState;
        private int sortId;
        private int orderNum;
        private boolean isIPhoneDevice;
        private int connType;
        private boolean isAppleDevice;

        public BluetoothBean() {
        }

        public String getAddress() {
            return this.mAddress;
        }

        public void setAddress(String mAddress) {
            this.mAddress = mAddress;
        }

        public String getRemoteDevName() {
            return this.mRemoteDevName;
        }

        public void setRemoteDevName(String mRemoteDevName) {
            this.mRemoteDevName = mRemoteDevName;
        }

        public byte[] getDevAdr() {
            return this.mDevAdr;
        }

        public void setDevAdr(byte[] mDevAdr) {
            this.mDevAdr = mDevAdr;
        }

        public int getDevID() {
            return this.mDevID;
        }

        public void setDevID(int mDevID) {
            this.mDevID = mDevID;
        }

        public int getCategory() {
            return this.category;
        }

        public void setCategory(int category) {
            this.category = category;
        }

        public boolean isPaired() {
            return this.isPaired;
        }

        public void setPaired(boolean isPaired) {
            this.isPaired = isPaired;
        }

        public boolean isMaster() {
            return this.isMaster;
        }

        public void setMaster(boolean isMaster) {
            this.isMaster = isMaster;
        }

        public boolean isHfpConnected() {
            return this.isHfpConnected;
        }

        public void setHfpConnected(boolean isHfpConnected) {
            this.isHfpConnected = isHfpConnected;
        }

        public boolean isA2dpConnected() {
            return this.isA2dpConnected;
        }

        public void setA2dpConnected(boolean isA2dpConnected) {
            this.isA2dpConnected = isA2dpConnected;
        }

        public boolean isBtConnecting() {
            return this.isBtConnecting;
        }

        public void setBtConnecting(boolean connecting) {
            this.isBtConnecting = connecting;
        }

        public int getState() {
            return this.mState;
        }

        public void setState(int mState) {
            this.mState = mState;
        }

        public int getSortId() {
            return this.sortId;
        }

        public void setSortId(int sortId) {
            this.sortId = sortId;
        }

        public boolean isMapConnected() {
            return this.isMapConnected;
        }

        public void setMapConnected(boolean mapConnected) {
            this.isMapConnected = mapConnected;
        }

        public int getOrderNum() {
            return this.orderNum;
        }

        public void setOrderNum(int otherNum) {
            this.orderNum = otherNum;
        }

        public boolean isIPhoneDevice() {
            return this.isIPhoneDevice;
        }

        public void setIPhoneDevice(boolean IPhoneDevice) {
            this.isIPhoneDevice = IPhoneDevice;
        }

        public int getConnType() {
            return this.connType;
        }

        public void setConnType(int connType) {
            this.connType = connType;
        }

        public boolean isAppleDevice() {
            return this.isAppleDevice;
        }

        public void setAppleDevice(boolean appleDevice) {
            this.isAppleDevice = appleDevice;
        }
    }


