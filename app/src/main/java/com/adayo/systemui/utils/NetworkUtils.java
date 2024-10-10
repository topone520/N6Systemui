package com.adayo.systemui.utils;

import android.text.TextUtils;
import android.util.Log;

import com.adayo.proxy.setting.system.utils.LogUtil;
import com.adayo.proxy.system.systemservice.SystemServiceConst;
import com.adayo.proxy.system.systemservice.SystemServiceManager;
import com.adayo.systemui.manager.MutualAuthIntercept;
import com.baic.icc.mutualauth.SSLHelper;
import com.baic.icc.mutualauth.TspServerUriManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.internal.tls.OkHostnameVerifier;

/**
 * The type Network utils.
 */
public class NetworkUtils {
    /**
     * The constant Service_DOMAIN.
     */
//    public static final String Service_DOMAIN = "https://ivstsp.bjev.com.cn";
    public static final String Service_DOMAIN = "https://testicctsp.bjev.com.cn";
    private static final String TAG = NetworkUtils.class.getSimpleName();

    /**
     * The constant networkUtils.
     */
    public static NetworkUtils networkUtils;

    private static OkHttpClient okHttpClient;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static NetworkUtils getInstance() {
        if (networkUtils == null) {
            networkUtils = new NetworkUtils();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (SystemServiceManager.getInstance().conectsystemService()) {
                String sn = SystemServiceManager.getInstance().getSystemConfigInfo((byte) SystemServiceConst.SYS_CONFIG_SERNUM);
                LogUtil.i(TAG,"   sn ==  " + sn);
                int state = SSLHelper.getInstance().getCertState(SystemServiceManager.getInstance().getSystemConfigInfo((byte) SystemServiceConst.SYS_CONFIG_SERNUM));
                LogUtil.i(TAG,"   CertState ==  " + state);
                if (state == 2){
//                    try {
//                        addTlsMutualAuth(builder);
//                    } catch (MalformedURLException e) {
//                        throw new RuntimeException(e);
//                    }
                }
            }else {
                LogUtil.i(TAG,"nononononono");
            }
            okHttpClient = builder.build();
        }
        return networkUtils;
    }

    private static void addTlsMutualAuth(OkHttpClient.Builder builder) throws MalformedURLException {
        builder.addInterceptor(new MutualAuthIntercept());
        SSLSocketFactory sslSocketFactory = SSLHelper.getInstance().getSSLSocketFactory();
        X509TrustManager trustManager = SSLHelper.getInstance().getTrustManager();
        if (sslSocketFactory == null) {
            return;
        }
        if (trustManager == null) {
            return;
        }
        builder.sslSocketFactory(sslSocketFactory, trustManager);
        String host = new URL(TspServerUriManager.getHttpUrl()).getHost();
        builder.hostnameVerifier((hostname, session) -> {
            if (hostname.equals(host)) {
                return true;
            }
            boolean verify = OkHostnameVerifier.INSTANCE.verify(hostname, session);
            Log.d("TAG", "verify : " + verify);
            return verify;
        });
    }


    /**
     * Send https.
     *
     * @param type     the type
     * @param msgId    the msg id
     * @param callback the callback
     */
    public synchronized void sendHttps(String type, String msgId, Callback callback) {
        //创建一个RequestBody(参数1：数据类型 参数2传递的json串)
        String vinCode = "";
        if (SystemServiceManager.getInstance().conectsystemService()) {
            vinCode = SystemServiceManager.getInstance().getSystemConfigInfo((byte) SystemServiceConst.SYS_CONFIG_VIN);
        }
        LogUtil.i(TAG, "   vinCode ==== " + vinCode);
        if (TextUtils.isEmpty(vinCode)) {
            return;
        }
//        String url = String.format("https://$s/device/vehicle/getNewMsg?vin=$s&type=$s&msgId=$s",
//                Service_DOMAIN,vinCode, type,msgId);

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(Service_DOMAIN);
        urlBuilder.append("/device/vehicle/getNewMsg?vin=");
        urlBuilder.append(vinCode);
        urlBuilder.append("&msgId=");
        urlBuilder.append(msgId);
        urlBuilder.append("&type=");
        urlBuilder.append(type);

        String url = urlBuilder.toString();
        LogUtil.i(TAG, url);
        //创建一个请求对象
        long ts = System.currentTimeMillis();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json;charset=UTF-8")
                .addHeader("sign", getIdentifyCodeV2Sign(String.valueOf(ts), "665d4a44c3dfc37a0c20b14189a115ce"))
                .addHeader("ts", String.valueOf(ts))
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * send to car.
     *
     * @param callback the callback
     */
    public synchronized void sendDeviceOnline(Callback callback){
        String vinCode = "";
        if (SystemServiceManager.getInstance().conectsystemService()) {
            vinCode = SystemServiceManager.getInstance().getSystemConfigInfo((byte) SystemServiceConst.SYS_CONFIG_VIN);
        }
        LogUtil.i(TAG,"  vinCode == " + vinCode);
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(Service_DOMAIN);
        urlBuilder.append("/device/vehicle/deviceOnlineInform");
        String url = urlBuilder.toString();
        LogUtil.i(TAG,"  deviceOnlineInform == " + url);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("vin",vinCode);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        RequestBody requestBody = RequestBody.create(jsonObject.toString(),MediaType.parse("application/json;charset=utf-8"));
        //创建一个请求对象
        long ts = System.currentTimeMillis();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("sign", getIdentifyCodeV2Sign(String.valueOf(ts), "665d4a44c3dfc37a0c20b14189a115ce"))
                .addHeader("ts", String.valueOf(ts))
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 获取认证签名的code.
     *
     * @param ts         the ts
     * @param projectKey the project key
     * @return the identify code v 2 sign
     */
    public static String getIdentifyCodeV2Sign(String ts, String projectKey) {
        String str = ts + projectKey + "1";
        return sha256(str);
    }

    /**
     * Sha 256 string.
     *
     * @param str the str
     * @return the string
     */
    public static String sha256(String str) {
        String encodestr = "";
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
            encodestr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return encodestr;
    }

    /**
     * Byte 2 hex string.
     *
     * @param bytes the bytes
     * @return the string
     */
    public static String byte2Hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String temp;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                sb.append("0");
            }
            sb.append(temp);
        }
        return sb.toString();
    }
}
