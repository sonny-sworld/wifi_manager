package com.wifidemoSonny.utils;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.List;


public class WifiManager {


    /**
     */
    public static void startScanWifi(android.net.wifi.WifiManager manager) {
        if (manager != null) {
            manager.startScan();
        }
    }


    /**
     */
    public static List<ScanResult> getWifiList(android.net.wifi.WifiManager mWifiManager) {
        return mWifiManager.getScanResults();
    }


    /**
     */
    public static void saveNetworkByConfig(android.net.wifi.WifiManager manager, WifiConfiguration config) {
        if (manager == null) {
            return;
        }
        try {
            Method save = manager.getClass().getDeclaredMethod("save", WifiConfiguration.class, Class.forName("android.net.wifi.WifiManager$ActionListener"));
            if (save != null) {
                save.setAccessible(true);
                save.invoke(manager, config, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     */
    public static void forgetNetwork(android.net.wifi.WifiManager manager, int networkId) {
        if (manager == null) {
            return;
        }
        try {
            Method forget = manager.getClass().getDeclaredMethod("forget", int.class, Class.forName("android.net.wifi.WifiManager$ActionListener"));
            if (forget != null) {
                forget.setAccessible(true);
                forget.invoke(manager, networkId, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     */
    public static boolean disconnectNetwork(android.net.wifi.WifiManager manager) {
        return manager != null && manager.disconnect();
    }


    /**
     * @return
     */
    public static String getWiFiName(android.net.wifi.WifiManager manager) {
        WifiInfo wifiInfo = manager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    /**
     */
    public static String getEncrypt(android.net.wifi.WifiManager mWifiManager, ScanResult scanResult) {
        if (mWifiManager != null) {
            String capabilities = scanResult.capabilities;
            if (!TextUtils.isEmpty(capabilities)) {
                if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                    return "WPA";
                } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                    return "WEP";
                } else {
                    return "no password";
                }
            }
        }
        return "failed";
    }

    /**
     */
    public static boolean openWifi(android.net.wifi.WifiManager mWifiManager) {
        boolean bRet = true;
        if (!mWifiManager.isWifiEnabled()) {
            bRet = mWifiManager.setWifiEnabled(true);
        }
        return bRet;
    }
    /**
     * close wifi
     */
    public static boolean closeWifi(android.net.wifi.WifiManager mWifiManager) {
        boolean bRet = true;
        if (mWifiManager.isWifiEnabled()) {
            bRet = mWifiManager.setWifiEnabled(false);
        }
        return bRet;
    }


    public static void connectWifi(android.net.wifi.WifiManager wifiManager, String wifiName, String password, String type) {

        String ssid = "\"" + wifiName + "\"";
        String psd = "\"" + password + "\"";


        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = ssid;
        switch (type) {
            case "WEP":

                conf.wepKeys[0] = psd;
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                break;
            case "WPA":

                conf.preSharedKey = psd;
                break;
            default:

                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals(ssid)) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }
}
