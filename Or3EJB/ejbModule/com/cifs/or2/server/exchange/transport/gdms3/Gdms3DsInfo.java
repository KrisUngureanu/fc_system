package com.cifs.or2.server.exchange.transport.gdms3;

public class Gdms3DsInfo {

    public static final int RES_OK = 0;
    public static final int RES_LIGHT_ERROR = 1;
    public static final int RES_FATAL_ERROR = 2;

    public String clientName;
    public String clientVer;
    public int clientStatus;
    public String problemDesc;
}
