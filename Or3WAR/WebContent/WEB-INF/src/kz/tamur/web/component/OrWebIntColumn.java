package kz.tamur.web.component;

import kz.tamur.comps.models.IntColumnPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.OrFrame;
import kz.tamur.rt.adapters.IntColumnAdapter;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

public class OrWebIntColumn extends OrWebTableColumn {

    public static final PropertyNode PROPS = new IntColumnPropertyRoot();

    OrWebIntColumn(Element xml, int mode, OrFrame frame, String id) throws KrnException {
        super(xml, mode, frame, id);
        editor = new OrWebIntField(xml, mode, frame, true, id);
        adapter = new IntColumnAdapter(frame, this);
    }

    public PropertyNode getProperties() {
        return PROPS;
    }
}
