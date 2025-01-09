package kz.tamur.guidesigner;

import kz.tamur.comps.OrGuiComponent;
import kz.tamur.comps.models.PropertyNode;

import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: Vital
 * Date: 14.05.2004
 * Time: 16:13:07
 * To change this template use File | Settings | File Templates.
 */
public interface PropertyListener extends EventListener {

    public static final int DELETE_EVENT = 0;
    public static final int SELECT_EVENT = 1;
    public static final int RENAME_EVENT = 2;
    public static final int DRAG_EVENT = 3;        

    public void propertyModified(OrGuiComponent c);
    public void propertyModified(OrGuiComponent c, PropertyNode property);
    public void propertyModified(OrGuiComponent c, int propertyEvent);

}
