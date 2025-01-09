package com.cifs.or2.server;

import java.util.EventListener;
import java.io.File;

import com.cifs.or2.server.exchange.Box;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 08.12.2003
 * Time: 17:00:27
 * To change this template use Options | File Templates.
 */
public interface BoxListener extends EventListener {
    int messageReceived(File file,Object msg, Box box);
    int responseSend(long flowId,boolean result);
}
