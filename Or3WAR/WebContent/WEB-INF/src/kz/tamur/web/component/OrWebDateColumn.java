package kz.tamur.web.component;

import kz.tamur.comps.models.DateColumnPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.Constants;
import kz.tamur.rt.adapters.DateColumnAdapter;
import org.jdom.Element;
import com.cifs.or2.kernel.KrnException;

public class OrWebDateColumn extends OrWebTableColumn {

    public static final PropertyNode PROPS = new DateColumnPropertyRoot();

    OrWebDateColumn(Element xml, int mode, OrFrame frame, String id) throws KrnException {
        super(xml, mode, frame, id);
        editor = new OrWebDateField(xml, mode, frame, true, id);
        adapter = new DateColumnAdapter(frame, this);
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public int getComponentStatus() {
        return Constants.TABLE_COMP;
    }
}
