package kz.tamur.web.component;

import kz.tamur.comps.models.IntColumnPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.Constants;
import kz.tamur.rt.adapters.FloatColumnAdapter;
import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

public class OrWebFloatColumn extends OrWebTableColumn {

    public static final PropertyNode PROPS = new IntColumnPropertyRoot();

    OrWebFloatColumn(Element xml, int mode, OrFrame frame, String id) throws KrnException {
        super(xml, mode, frame, id);
        editor = new OrWebFloatField(xml, mode, frame, true, id);
        adapter = new FloatColumnAdapter(frame, this);
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public int getComponentStatus() {
        return Constants.TABLE_COMP;
    }
}
