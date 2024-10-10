package com.adayo.systemui.manager;

import android.util.Log;

import androidx.annotation.NonNull;

import com.adayo.proxy.system.systemservice.SystemServiceConst;
import com.adayo.proxy.system.systemservice.SystemServiceManager;
import com.baic.icc.mutualauth.SSLHelper;
import com.baic.icc.mutualauth.TspEnvManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class MutualAuthIntercept implements Interceptor {
    private volatile String pdsn = "";
    
    private boolean isMutual = TspEnvManager.getTspEnvType() != TspEnvManager.ENV_TEST_SINGLE;

    private static final String TAG = "MutualAuthIntercept";

    public MutualAuthIntercept() {
        initPdsn();
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!isMutual) {
            return chain.proceed(chain.request());
        }

        if (pdsn.isEmpty()) {
            throw new LocalSSLException.PdsnException();
        }
        boolean verityCer = verityCer(pdsn);
        Log.d(TAG, "  verityCer  :" + verityCer);
        if (!verityCer) {
            throw new LocalSSLException.VerityException();
        }

        return chain.proceed(chain.request());
    }

    private boolean verityCer(String pdsn) {
        return SSLHelper.getInstance().getCertState(pdsn) == 2;
    }

    private void initPdsn() {
        boolean connectRlt = SystemServiceManager.getInstance().conectsystemService();
        Log.d(TAG, "  connect carservice  :" + connectRlt);
        if (connectRlt) {
            byte sysConfigSernum = SystemServiceConst.SYS_CONFIG_SERNUM;
            pdsn = SystemServiceManager.getInstance()
                .getSystemConfigInfo(sysConfigSernum);
            Log.d(TAG, "  get pdsn  :" + pdsn);
        }
    }

}
