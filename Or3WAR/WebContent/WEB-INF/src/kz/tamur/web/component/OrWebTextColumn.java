package kz.tamur.web.component;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TextColumnPropertyRoot;
import kz.tamur.comps.*;
import kz.tamur.rt.adapters.TextColumnAdapter;
import org.jdom.Element;
import com.cifs.or2.kernel.KrnException;

public class OrWebTextColumn extends OrWebTableColumn {

    public static final PropertyNode PROPS = new TextColumnPropertyRoot();

    OrWebTextColumn(Element xml, int mode, OrFrame frame, String id) throws KrnException {
        super(xml, mode, frame, id);
        editor = new OrWebTextField(xml, mode, frame, true, id);
        adapter = new TextColumnAdapter(frame, this);
    }

    public PropertyNode getProperties() {
        return PROPS;
    }
}
