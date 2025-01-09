package com.cifs.or2.server.exchange.transport;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 14.12.2003
 * Time: 16:38:23
 * To change this template use Options | File Templates.
 */
public class TransportException extends Exception {
    public TransportException(String msg) {
        super(msg);
    }

    public TransportException(Exception e) {
        super(e.getMessage());
    }
}
