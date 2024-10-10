package com.adayo.systemui.bean;

import java.io.Serializable;

public class MessageBeanInfo implements Serializable {

    private String msgId;
    private String msgVersion;
    private String sendTime;
    private String dataType;

    private String title;

    private String packageName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    private boolean isChecked = false;
    private Data data;

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgVersion() {
        return msgVersion;
    }

    public void setMsgVersion(String msgVersion) {
        this.msgVersion = msgVersion;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data implements Serializable {
        /**
         * dataType = 1
         */
        private String access_token;
        private String refresh_token;
        private UserInfo userInfo;
        /**
         * dataType = 2
         */
        private String pdsn;
        private String userAccountId;
        private String userName;
        /**
         * dataType = 10
         */
        private String appkey;
        private String token;
        private String appuid;
        private Integer expiresin;
        /**
         * dataType = 11
         * private String appkey;
         */
        private Integer type;
        private Integer status;
        /**
         * dataType = 12
         private String appkey;
         private Integer type;
         private Integer status;
         */
        /**
         * dataType = 13
         * private Integer appkey;
         * private String userAccountId;
         */
        /**
         * dataType = 14
         * private String appkey;
         * private Integer status;
         */
        private String msg;

        /**
         * dataType = 19
         */
        private String payTime;
        private String packageId;
        private String resultStatus;

        /**
         * dataType = 20
         */
        private String packageName;
        private FlowAlarm flowAlarm;
        private TimeAlarm timeAlarm;

        public static class FlowAlarm {
            private String last;

            public String getLast() {
                return last;
            }

            public void setLast(String last) {
                this.last = last;
            }
        }

        public static class TimeAlarm {
            private String last;

            public String getLast() {
                return last;
            }

            public void setLast(String last) {
                this.last = last;
            }
        }

        /**
         * dataType = 21
         */
        private String gps;
        private String gpsName;
        private String gpsAddress;
        private String msgSource;

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

        /**
         * dataType = 899
         */
        private String id;
        private String searchPath;
        private String searchName;
        private String lst;

        /**
         * dataType = 900 日志上传指令
         */

        /**
         * dataType = 901
         * private String id;
         */
        private String message;
        private Long time;

        /**
         * dataType = 902
         * private String id;
         * private String userAccountId;
         * private Long time;
         */
        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getRefresh_token() {
            return refresh_token;
        }

        public void setRefresh_token(String refresh_token) {
            this.refresh_token = refresh_token;
        }

        public UserInfo getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(UserInfo userInfo) {
            this.userInfo = userInfo;
        }

        public String getAppkey() {
            return appkey;
        }

        public void setAppkey(String appkey) {
            this.appkey = appkey;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getAppuid() {
            return appuid;
        }

        public void setAppuid(String appuid) {
            this.appuid = appuid;
        }

        public Integer getExpiresin() {
            return expiresin;
        }

        public void setExpiresin(Integer expiresin) {
            this.expiresin = expiresin;
        }

        public String getPdsn() {
            return pdsn;
        }

        public void setPdsn(String pdsn) {
            this.pdsn = pdsn;
        }

        public String getUserAccountId() {
            return userAccountId;
        }

        public void setUserAccountId(String userAccountId) {
            this.userAccountId = userAccountId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getPayTime() {
            return payTime;
        }

        public void setPayTime(String payTime) {
            this.payTime = payTime;
        }

        public String getPackageId() {
            return packageId;
        }

        public void setPackageId(String packageId) {
            this.packageId = packageId;
        }

        public String getResultStatus() {
            return resultStatus;
        }

        public void setResultStatus(String resultStatus) {
            this.resultStatus = resultStatus;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public FlowAlarm getFlowAlarm() {
            return flowAlarm;
        }

        public void setFlowAlarm(FlowAlarm flowAlarm) {
            this.flowAlarm = flowAlarm;
        }

        public TimeAlarm getTimeAlarm() {
            return timeAlarm;
        }

        public void setTimeAlarm(TimeAlarm timeAlarm) {
            this.timeAlarm = timeAlarm;
        }

        public String getGps() {
            return gps;
        }

        public void setGps(String gps) {
            this.gps = gps;
        }

        public String getGpsName() {
            return gpsName;
        }

        public void setGpsName(String gpsName) {
            this.gpsName = gpsName;
        }

        public String getGpsAddress() {
            return gpsAddress;
        }

        public void setGpsAddress(String gpsAddress) {
            this.gpsAddress = gpsAddress;
        }

        public String getMsgSource() {
            return msgSource;
        }

        public void setMsgSource(String msgSource) {
            this.msgSource = msgSource;
        }

        public Integer getMsgId() {
            return msgId;
        }

        public void setMsgId(Integer msgId) {
            this.msgId = msgId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSearchPath() {
            return searchPath;
        }

        public void setSearchPath(String searchPath) {
            this.searchPath = searchPath;
        }

        public String getSearchName() {
            return searchName;
        }

        public void setSearchName(String searchName) {
            this.searchName = searchName;
        }

        public String getLst() {
            return lst;
        }

        public void setLst(String lst) {
            this.lst = lst;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Long getTime() {
            return time;
        }

        public void setTime(Long time) {
            this.time = time;
        }

        public static class UserInfo {
            private Long userAccountId;
            private String avatar;
            private String nickName;
            private Integer userType;
            private Integer carOwner;

            public Long getUserAccountId() {
                return userAccountId;
            }

            public void setUserAccountId(Long userAccountId) {
                this.userAccountId = userAccountId;
            }

            public String getAvatar() {
                return avatar;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public String getNickName() {
                return nickName;
            }

            public void setNickName(String nickName) {
                this.nickName = nickName;
            }

            public Integer getUserType() {
                return userType;
            }

            public void setUserType(Integer userType) {
                this.userType = userType;
            }

            public Integer getCarOwner() {
                return carOwner;
            }

            public void setCarOwner(Integer carOwner) {
                this.carOwner = carOwner;
            }
        }

    }
}
