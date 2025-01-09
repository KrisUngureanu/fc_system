package kz.tamur.comps;


import kz.tamur.comps.models.DocFieldColumnPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import org.jdom.Element;

import java.awt.*;

public class OrDocFieldColumn extends OrTableColumn {
    
    public static final PropertyNode PROPS = new DocFieldColumnPropertyRoot();

    OrDocFieldColumn(Element xml, int mode, OrFrame frame) {
        super(xml, mode, frame);
        editor = new OrDocField(xml, mode, frame, true);
    }

    public GridBagConstraints getConstraints() {
        return null;
    }

    public Element getXml() {
        return super.getXml();
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public PropertyValue getPropertyValue(PropertyNode prop) {
        return PropertyHelper.getPropertyValue(prop, getXml(), frame);
    }

    public void setPropertyValue(PropertyValue value) {
        super.setPropertyValue(value);
    }

    public int getComponentStatus() {
        return Constants.TABLE_COMP;
    }


    public int getMode() {
        return super.getMode();
    }

    public int getTabIndex() {
        return -1;
    }
}
