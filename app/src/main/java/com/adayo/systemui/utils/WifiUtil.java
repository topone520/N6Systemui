/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package com.adayo.systemui.utils;

import static android.net.wifi.WifiConfiguration.NetworkSelectionStatus.NETWORK_SELECTION_ENABLED;

import android.content.Context;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WifiUtil {

    private static final String TAG = WifiUtil.class.getSimpleName();

    public static boolean isOpenNetwork(int security) {
        LogUtil.i("security = " + security);
        return security == AccessPoint.SECURITY_NONE || security == AccessPoint.SECURITY_OWE;
    }

    public static boolean canSignIntoNetwork(NetworkCapabilities capabilities) {
        LogUtil.i("capabilities = " + capabilities);
        return (capabilities != null
                && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL));
    }

    public static void connectToAccessPoint(Context context, String ssid, int security,
                                            String password, boolean hidden, @Nullable WifiManager.ActionListener listener) {
        LogUtil.i("ssid = " + ssid + " security = " + security + " hidden = " + hidden);
        WifiManager wifiManager = context.getSystemService(WifiManager.class);
        WifiConfiguration wifiConfig = getWifiConfig(ssid, security, password, hidden);
        wifiManager.connect(wifiConfig, listener);
    }

    private static WifiConfiguration getWifiConfig(String ssid, int security,
                                                   String password, boolean hidden) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.hiddenSSID = hidden;
        return finishWifiConfig(wifiConfig, security, password);
    }


    public static WifiConfiguration getWifiConfig(@NonNull AccessPoint accessPoint,
                                                  String password) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        if (!accessPoint.isSaved()) {
            wifiConfig.SSID = AccessPoint.convertToQuotedString(
                    accessPoint.getSsidStr());
        } else {
            wifiConfig.networkId = accessPoint.getConfig().networkId;
            wifiConfig.hiddenSSID = accessPoint.getConfig().hiddenSSID;
        }

        return finishWifiConfig(wifiConfig, accessPoint.getSecurity(), password);
    }

    private static WifiConfiguration finishWifiConfig(WifiConfiguration wifiConfig, int security,
                                                      String password) {
        switch (security) {
            case AccessPoint.SECURITY_NONE:
                wifiConfig.setSecurityParams(WifiConfiguration.SECURITY_TYPE_OPEN);
                break;
            case AccessPoint.SECURITY_WEP:
                wifiConfig.setSecurityParams(WifiConfiguration.SECURITY_TYPE_WEP);
                if (!TextUtils.isEmpty(password)) {
                    int length = password.length();
                    if ((length == 10 || length == 26 || length == 58)
                            && password.matches("[0-9A-Fa-f]*")) {
                        wifiConfig.wepKeys[0] = password;
                    } else {
                        wifiConfig.wepKeys[0] = '"' + password + '"';
                    }
                }
                break;
            case AccessPoint.SECURITY_PSK:
                wifiConfig.setSecurityParams(WifiConfiguration.SECURITY_TYPE_PSK);
                if (!TextUtils.isEmpty(password)) {
                    if (password.matches("[0-9A-Fa-f]{64}")) {
                        wifiConfig.preSharedKey = password;
                    } else {
                        wifiConfig.preSharedKey = '"' + password + '"';
                    }
                }
                break;
            case AccessPoint.SECURITY_EAP:
            case AccessPoint.SECURITY_EAP_SUITE_B:
                if (security == AccessPoint.SECURITY_EAP_SUITE_B) {
                    wifiConfig.setSecurityParams(WifiConfiguration.SECURITY_TYPE_EAP_SUITE_B);
                } else {
                    wifiConfig.setSecurityParams(WifiConfiguration.SECURITY_TYPE_EAP);
                }
                if (!TextUtils.isEmpty(password)) {
                    wifiConfig.enterpriseConfig.setPassword(password);
                }
                break;
            case AccessPoint.SECURITY_SAE:
                wifiConfig.setSecurityParams(WifiConfiguration.SECURITY_TYPE_SAE);
                if (!TextUtils.isEmpty(password)) {
                    wifiConfig.preSharedKey = '"' + password + '"';
                }
                break;
            case AccessPoint.SECURITY_OWE:
                wifiConfig.setSecurityParams(WifiConfiguration.SECURITY_TYPE_OWE);
                break;
            default:
                throw new IllegalArgumentException("unknown security type " + security);
        }
        return wifiConfig;
    }

    public static void forget(Context context, AccessPoint accessPoint) {
        WifiManager wifiManager = context.getSystemService(WifiManager.class);
        if (!accessPoint.isSaved()) {
            if (accessPoint.getNetworkInfo() != null
                    && accessPoint.getNetworkInfo().getState() != NetworkInfo.State.DISCONNECTED) {
                wifiManager.disableEphemeralNetwork(
                        AccessPoint.convertToQuotedString(accessPoint.getSsidStr()));
            } else {
                LogUtil.e("Failed to forget invalid network " + accessPoint.getConfig());
            }
        } else {
            wifiManager.forget(accessPoint.getConfig().networkId, new WifiManager.ActionListener() {
                @Override
                public void onSuccess() {
                    LogUtil.i("Network successfully forgotten");
                }

                @Override
                public void onFailure(int reason) {
                    LogUtil.i("Could not forget network. Failure code: " + reason);
                }
            });
        }
    }

    public static boolean isAccessPointDisabledByWrongPassword(AccessPoint accessPoint) {
        WifiConfiguration config = accessPoint.getConfig();
        if (config == null) {
            return false;
        }
        WifiConfiguration.NetworkSelectionStatus networkStatus =
                config.getNetworkSelectionStatus();
        if (networkStatus == null
                || networkStatus.getNetworkSelectionStatus() == NETWORK_SELECTION_ENABLED) {
            return false;
        }
        return networkStatus.getNetworkSelectionDisableReason()
                == WifiConfiguration.NetworkSelectionStatus.DISABLED_BY_WRONG_PASSWORD;
    }

}
