package kz.tamur.comps;

import kz.tamur.comps.models.FloatColumnPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import org.jdom.Element;

import java.awt.*;

public class OrFloatColumn extends OrTableColumn {
    
    public static final PropertyNode PROPS = new FloatColumnPropertyRoot();

    OrFloatColumn(Element xml, int mode, OrFrame frame) {
        super(xml, mode, frame);
        editor = new OrFloatField(xml, mode, frame, true);
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
