package com.bulsu.quickeyshare;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import static com.bulsu.quickeyshare.ApManager.isApOn;

/**
 * Created by mykelneds on 19/02/2017.
 */

public class NetworkHelper {

    static String TAG = NetworkHelper.class.getSimpleName();

    public static final int SERVER_PORT = 8080;

    public static String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip = inetAddress.getHostAddress();

                        String inet = inetAddress.getHostAddress();
                        Log.d("Sender2: ", inet);
                        Log.d("Sender2: ", inet.substring(0, inet.lastIndexOf(".") + 1));
                        Log.d("Sender2: ", "inet: " + inet.substring(inet.lastIndexOf(".") + 1));
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }

    public static WifiManager getWifiManager(Context ctx) {
        return (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static boolean isWifiEnabled(Context ctx) {
        WifiManager wifi = getWifiManager(ctx);
        return wifi.isWifiEnabled();
    }

    public static void setWifiEnabled(Context ctx, boolean isEnabled) {
        WifiManager wifi = getWifiManager(ctx);
        wifi.setWifiEnabled(isEnabled);
    }
    public static boolean startHotspot(Context context, String SSID) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = new WifiConfiguration();
        wificonfiguration.SSID = SSID;
        wificonfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wificonfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wificonfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wificonfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        try {
            // if WiFi is on, turn it off
            if (isApOn(context)) {
                wifimanager.setWifiEnabled(false);
            }
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, !isApOn(context));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
