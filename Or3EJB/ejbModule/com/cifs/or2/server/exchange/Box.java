package com.cifs.or2.server.exchange;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.BoxListener;
import com.cifs.or2.server.exchange.transport.*;

import kz.tamur.or3ee.common.UserSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.swing.event.EventListenerList;
import java.io.File;

public class Box implements MessageCashListener {

    Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + Box.class.getName());

    private EventListenerList boxListeners = new EventListenerList();
    private KrnObject krnObject;
    private KrnObject base;

    private String name;
    private String urlIn;
    private String urlOut;
    private String xpathIn;
    private String xpathOut;
    private String xpathTypeIn;
    private String xpathTypeOut;
    private String xpathIdInit;
    private String charSet;
    private long restrict;
    private long transportId;

    public long getTypeMsg() {
        return typeMsg;
    }

    private long typeMsg;
    private byte[] config;
    private MessageCash messageCache;

    public Box(KrnObject obj,
               KrnObject base,
               String name,
               String urlIn,
               String urlOut,
               String xpathIn,
               String xpathOut,
               String xpathTypeIn,
               String xpathTypeOut,
               String xpathIdInit,
               byte[] config,
               String charSet,
               long restrict,
               long transportId,
               long typeMsg,
               MessageCash messageCache) {
        this.krnObject = obj;
        this.base=base;
        this.name = name;
        this.urlIn = urlIn;
        this.urlOut = urlOut;
        this.xpathIn=xpathIn;
        this.xpathOut=xpathOut;
        this.xpathTypeIn = xpathTypeIn;
        this.xpathTypeOut = xpathTypeOut;
        this.xpathIdInit = xpathIdInit;
        this.charSet = charSet;
        this.restrict=restrict;
        this.transportId = transportId;
        this.typeMsg = typeMsg;
        this.config = config;
        this.messageCache = messageCache;
    }

    public String getName() {
        return name;
    }

    public KrnObject getKrnObject() {
        return krnObject;
    }

    public String getUrlIn() {
        return urlIn;
    }
    public String getUrlOut() {
        return urlOut;
    }


    public void remove(){
    	messageCache.removeBox( this);
    }
    public long getTransportId() {
        return transportId;
    }

    public String getXpathOut() {
        return xpathOut;
    }

    public String getXpathIn() {
        return xpathIn;
    }
    //    }
    public void addBoxListener(BoxListener l) {
        boxListeners.add(BoxListener.class, l);
    }

    public void removeBoxListener(BoxListener l) {
        boxListeners.remove(BoxListener.class, l);
    }

    public void send(Object msg, long flowId,String objId) throws BoxException {
        try {
        	messageCache.dispose(MessageCash.OUT,krnObject.id,flowId,msg,objId);
        } catch (TransportException e) {
            log.error(e, e);
            //@todo Спецификация ошибок
            throw new BoxException("Ошибка при отправке", e);
        }
    }

    public int messageReceived(File file,Object msg) {
        Object[] listeners = boxListeners.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i]==BoxListener.class) {
                return ((BoxListener)listeners[i+1]).messageReceived(file,msg,this);
            }
        }
        return MessageCash.NOT_BOX;
    }

    public int responseSend(long flowId,boolean result) {
        Object[] listeners = boxListeners.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i]==BoxListener.class) {
                return ((BoxListener)listeners[i+1]).responseSend(flowId,result);
            }
        }
        return MessageCash.NOT_BOX;
    }

    public String getXpathTypeIn() {
        return xpathTypeIn;
    }

    public String getXpathTypeOut() {
        return xpathTypeOut;
    }
    public long getRestrict() {
        return restrict;
    }

    public String getXpathIdInit() {
        return xpathIdInit;
    }

    public String getCharSet() {
        return charSet;
    }

    public byte[] getExpr() {
        return config;
    }

    public KrnObject getBase() {
        return base;
    }
}
