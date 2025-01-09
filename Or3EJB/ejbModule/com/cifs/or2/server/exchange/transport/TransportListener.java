package com.cifs.or2.server.exchange.transport;

import org.jdom.Element;

import java.util.EventListener;

public interface TransportListener extends EventListener {
    boolean messageReceived(Element xml);
    boolean messageSent(ExchangeObject exchange);
}
