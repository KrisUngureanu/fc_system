package com.cifs.or2.server;

public class SessionManager {

    private static String serverBlockerIpAddress;

    public static synchronized void blockServer(String ipAddress) {
        if (serverBlockerIpAddress == null)
            serverBlockerIpAddress = ipAddress;
    }

    public static synchronized void unblockServer(String ipAddress) {
        if (ipAddress.equals(serverBlockerIpAddress))
            serverBlockerIpAddress = null;
    }

    public static synchronized int isServerBlocked(String ipAddress) {
        if (serverBlockerIpAddress == null || ipAddress == null)
            return 0; // Сервер не заблокирован
        else if (ipAddress.equals(serverBlockerIpAddress))
            return 1; // Сервер заблокирован Вами
        else
            return 2; // Сервер заблокирован кем-то
    }
}
