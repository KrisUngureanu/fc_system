package kz.tamur.web.component;

import kz.tamur.comps.OrFrame;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.CheckColumnPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.adapters.CheckBoxColumnAdapter;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

public class OrWebCheckColumn extends OrWebTableColumn {

    public static final PropertyNode PROPS = new CheckColumnPropertyRoot();
    private boolean uniqueSelection;

    OrWebCheckColumn(Element xml, int mode, OrFrame frame, String id) throws KrnException {
        super(xml, mode, frame, id);
        editor = new OrWebCheckBox(xml, mode, frame, true, id);
        adapter = new CheckBoxColumnAdapter(frame, this);
    }
    
    protected void init() {
        PropertyValue pv = getPropertyValue(getProperties().getChild("pov").getChild("activity").getChild("uniqueSelection"));
        if (!pv.isNull()) {
        	uniqueSelection = pv.booleanValue();
        }
        super.init();
    }
    
    public boolean isUniqueSelection() {
    	return uniqueSelection;
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public int getTabIndex() {
        return -1;
    }
}
