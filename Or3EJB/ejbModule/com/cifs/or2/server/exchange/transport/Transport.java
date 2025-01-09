package com.cifs.or2.server.exchange.transport;

import org.jdom.JDOMException;
import com.cifs.or2.server.exchange.Box;

import java.io.IOException;

public interface Transport {
    public int getId();
    public String getName();
    public void reset();
    public void restart(boolean isConnect);
    public void resend();
    public void received();
    public void check();
    public void stop();
    public void resume();
    public byte[] getTransportParam() throws IOException;
    public void setTransportParam(byte[] param) throws JDOMException, IOException;
    void addTransportListener(Box box);
    void removeTransportListener(Box box);
}
