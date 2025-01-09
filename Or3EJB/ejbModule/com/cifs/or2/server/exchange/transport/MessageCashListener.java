package com.cifs.or2.server.exchange.transport;

import java.util.EventListener;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 22.06.2005
 * Time: 17:28:53
 * To change this template use File | Settings | File Templates.
 */
public interface MessageCashListener extends EventListener{
    int messageReceived(File file,Object msg);
    int responseSend(long flowId,boolean result);
}
