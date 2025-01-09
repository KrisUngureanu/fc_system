package com.cifs.or2.server.exchange.transport;

import java.io.File;
import com.cifs.or2.server.exchange.Box;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 23.06.2005
 * Time: 17:30:05
 * To change this template use File | Settings | File Templates.
 */
public class ExchangeObject {
    private Box box;
    private File file;
    private String msgId;
    private String msgType;
    private long flowId;
    public int countRej=0;

    public ExchangeObject(Box box, File file,String msgId,String msgType,long flowId) {
        this.box = box;
        this.file = file;
        this.msgId = msgId;
        this.msgType = msgType;
        this.flowId = flowId;
    }
    public Box getBox() {
        return box;
    }
    public void  setBox(Box box) {
        this.box=box;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public String getMsgId() {
        return msgId;
    }
    public String getMsgType() {
        return msgType;
    }

    public long getFlowId() {
        return flowId;
    }
}
