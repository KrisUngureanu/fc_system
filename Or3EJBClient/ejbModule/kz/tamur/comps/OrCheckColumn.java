package kz.tamur.comps;

import kz.tamur.comps.models.CheckColumnPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.adapters.ComponentAdapter;
import org.jdom.Element;

import java.awt.*;

public class OrCheckColumn extends OrTableColumn {
   
    public static final PropertyNode PROPS = new CheckColumnPropertyRoot();

    OrCheckColumn(Element xml, int mode, OrFrame frame) {
        super(xml, mode, frame);
        editor = new OrCheckBox(xml, mode, frame, true);
        ((OrCheckBox) editor).setAnimate(false);
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

    public ComponentAdapter getAdapter() {
        return null;
    }

    public void setAdapter(ComponentAdapter adapter) {
    }
}
