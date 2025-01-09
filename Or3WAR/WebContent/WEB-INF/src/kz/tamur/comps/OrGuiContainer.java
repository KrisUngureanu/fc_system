package kz.tamur.comps;

import kz.tamur.guidesigner.PropertyListener;
import kz.tamur.util.DescriptionSupport;


/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 18.03.2004
 * Time: 20:11:46
 * To change this template use File | Settings | File Templates.
 */
public interface OrGuiContainer extends OrGuiComponent, DescriptionSupport {
    boolean canAddComponent(int x, int y);
    void addComponent(OrGuiComponent c, Object cs);
    Object removeComponent(OrGuiComponent c);
    void moveComponent(OrGuiComponent c, int x, int y);
    void updateConstraints(OrGuiComponent c);
    void addPropertyListener(PropertyListener l);
    void removePropertyListener(PropertyListener l);
    void firePropertyModified();
    String getTitle();
    OrGuiComponent getComponent(String title);
}
