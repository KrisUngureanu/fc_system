package kz.tamur.web.component;

import kz.tamur.comps.models.HyperColumnPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.OrFrame;
import kz.tamur.rt.adapters.HyperColumnAdapter;
import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

public class OrWebHyperColumn extends OrWebTableColumn {

    public static final PropertyNode PROPS = new HyperColumnPropertyRoot();

    OrWebHyperColumn(Element xml, int mode, OrFrame frame, String id) throws KrnException {
        super(xml, mode, frame, id);
        editor = new OrWebHyperLabel(xml, mode, frame, true, id);
        adapter = new HyperColumnAdapter(frame, this);
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public int getTabIndex() {
        return -1;
    }
}
