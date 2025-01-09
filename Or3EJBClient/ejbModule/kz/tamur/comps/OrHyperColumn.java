package kz.tamur.comps;

import kz.tamur.comps.models.HyperColumnPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.MainFrame;

import org.jdom.Element;

import java.awt.*;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;

public class OrHyperColumn extends OrTableColumn {
    
    public static final PropertyNode PROPS = new HyperColumnPropertyRoot();

    OrHyperColumn(Element xml, int mode, OrFrame frame) {
        super(xml, mode, frame);
        editor = new OrHyperLabel(xml, mode, frame, true);
        if (mode == Mode.RUNTIME) {
    		String iconName = MainFrame.iconsSettings.get("iconHyperColumn");
    		ImageIcon icon = kz.tamur.rt.Utils.getImageIconFull(iconName);
    		if (icon != null) {
    			((AbstractButton) editor).setIcon(icon);
    		}
    	}
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
