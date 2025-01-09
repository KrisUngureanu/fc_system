package kz.tamur.comps;

import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 16.04.2004
 * Time: 16:07:10
 * To change this template use File | Settings | File Templates.
 */
public interface FactoryListener extends EventListener {
    void componentCreated(OrGuiComponent c);
    void componentCreating(String className);
}
