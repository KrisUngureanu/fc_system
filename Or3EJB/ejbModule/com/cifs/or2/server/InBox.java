package com.cifs.or2.server;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 17.12.2003
 * Time: 18:25:23
 * To change this template use Options | File Templates.
 */
public interface InBox {
    void receiveResponse(int docId, int tid);
    void cancelRequest(int docId);
}
