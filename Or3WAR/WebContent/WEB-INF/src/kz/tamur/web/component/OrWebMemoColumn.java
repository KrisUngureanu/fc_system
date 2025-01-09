package kz.tamur.web.component;

import kz.tamur.comps.OrFrame;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.MemoColumnPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.adapters.MemoColumnAdapter;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

public class OrWebMemoColumn extends OrWebTableColumn {

    public static final PropertyNode PROPS = new MemoColumnPropertyRoot();
    private boolean showTextAsXML;

    OrWebMemoColumn(Element xml, int mode, OrFrame frame, String id) throws KrnException {
        super(xml, mode, frame, id);
        editor = new OrWebMemoField(xml, mode, frame, true, id);
        adapter = new MemoColumnAdapter(frame, this);
    	PropertyNode pn = PROPS.getChild("view").getChild("showTextAsXML");
    	if (pn != null) {
    		PropertyValue pv = getPropertyValue(pn);
    		if (!pv.isNull()) {
    			showTextAsXML = pv.booleanValue();
    		}
    	}
    }
    
    public boolean isShowTextAsXML() {
    	return showTextAsXML;
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public int getTabIndex() {
        return -1;
    }
}