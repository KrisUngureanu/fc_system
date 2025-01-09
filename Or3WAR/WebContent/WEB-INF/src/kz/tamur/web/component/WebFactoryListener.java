package kz.tamur.web.component;

import kz.tamur.web.common.webgui.WebComponent;

import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: Erik
 * Date: 12.07.2006
 * Time: 16:37:07
 * To change this template use File | Settings | File Templates.
 */
public interface WebFactoryListener extends EventListener {
    void componentCreated(WebComponent c);
    void componentCreating(String className);
    
}
