package kz.tamur.comps;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TreeColumnPropertyRoot;


import org.jdom.Element;

import java.awt.*;

public class OrTreeColumn extends OrTableColumn {
    
    public static final PropertyNode PROPS = new TreeColumnPropertyRoot();


    OrTreeColumn(Element xml, int mode, OrFrame frame) {
        super(xml, mode, frame);
        editor = new OrTreeField(xml, mode, frame, true);
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

    public void setLangId(long langId) {
        super.setLangId(langId);
        editor.setLangId(langId);
    }

    public int getMode() {
        return super.getMode();
    }

    public String getBorderTitleUID() {
        return null;
    }

    public int getTabIndex() {
        return -1;
    }
}
