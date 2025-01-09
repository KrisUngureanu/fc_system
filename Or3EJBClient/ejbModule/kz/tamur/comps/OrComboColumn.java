package kz.tamur.comps;

import kz.tamur.comps.models.ComboColumnPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.adapters.ComponentAdapter;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

import java.awt.*;

public class OrComboColumn extends OrTableColumn {
    
    public static final PropertyNode PROPS = new ComboColumnPropertyRoot();

    OrComboColumn(Element xml, int mode, OrFrame frame) throws KrnException {
        super(xml, mode, frame);
        editor = new OrComboBox(xml, mode, frame, true);
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

    public int getTabIndex() {
        return -1;
    }

    public ComponentAdapter getAdapter() {
        return null;
    }
}
