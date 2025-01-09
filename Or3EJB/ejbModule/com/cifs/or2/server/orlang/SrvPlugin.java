package com.cifs.or2.server.orlang;
import com.cifs.or2.server.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Кайржан
 * Date: 01.07.2005
 * Time: 17:49:31
 * To change this template use File | Settings | File Templates.
 */
public interface SrvPlugin {
    public Session getSession();
    public void setSession(Session session);
}
