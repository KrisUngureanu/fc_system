package kz.tamur.comps;


import kz.tamur.comps.models.DateColumnPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

import java.awt.*;

public class OrDateColumn extends OrTableColumn {
    
    public static final PropertyNode PROPS = new DateColumnPropertyRoot();

    OrDateColumn(Element xml, int mode, OrFrame frame) throws KrnException {
        super(xml, mode, frame);
        editor = new OrDateField(xml, mode, frame, true);
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
        return PropertyHelper.getPropertyValue(prop, super.getXml(), frame);
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
