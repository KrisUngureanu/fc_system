package com.cifs.or2.server.exchange;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.12.2003
 * Time: 18:36:02
 * To change this template use Options | File Templates.
 */
public interface XmlFactory {
    public XmlObject createObject(String name, String id);
}
