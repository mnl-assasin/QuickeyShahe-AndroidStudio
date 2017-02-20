package com.bulsu.quickeyshare;

import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by mykelneds on 19/02/2017.
 */

public class NetworkHelper {

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
}
