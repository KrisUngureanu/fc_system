package kz.tamur.or3.client.comps.interfaces;

import kz.tamur.comps.OrGuiContainer;
import kz.tamur.comps.OrGuiComponent;

/**
 * Created by IntelliJ IDEA.
 * User: Erik
 * Date: 19.07.2006
 * Time: 19:26:13
 * To change this template use File | Settings | File Templates.
 */
public interface OrTabbedPaneComponent extends OrGuiContainer {
    void setTabVisible(OrGuiComponent comp, boolean visible);
}
