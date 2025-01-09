package com.cifs.or2.server;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 08.12.2003
 * Time: 17:08:31
 * To change this template use Options | File Templates.
 */
public interface OutBox {
    void sendRequest(int docId, int tid);
    void cancelRequest(int docId);
}
