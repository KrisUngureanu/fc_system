package kz.tamur.comps;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.adapters.ComponentAdapter;
import java.awt.*;

import org.jdom.Element;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.03.2004
 * Time: 18:14:33
 * To change this template use File | Settings | File Templates.
 */
public interface OrGuiComponent {
    GridBagConstraints getConstraints();
    PropertyNode getProperties();
    PropertyValue getPropertyValue(PropertyNode prop);
    Element getXml();
    int getComponentStatus();
    void setLangId(long langId);
    int getMode();
    //For copy process
    OrGuiContainer getGuiParent();
    void setGuiParent(OrGuiContainer parent);
    void setXml(Element xml);
    Dimension getPrefSize();
    Dimension getMaxSize();
    Dimension getMinSize();
    String getUUID();
    void setEnabled(boolean isEnabled);
    boolean isEnabled();
    byte[] getDescription();
    ComponentAdapter getAdapter();
    String getVarName();
    String getPath();
    com.cifs.or2.kernel.KrnAttribute getAttribute();
    
    // set value native for component (String, Long, Float, Date
	void setValue(Object value);
    Object getValue();
}
