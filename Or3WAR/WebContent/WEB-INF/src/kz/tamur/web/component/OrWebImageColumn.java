package kz.tamur.web.component;

import kz.tamur.comps.models.ImageColumnPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.*;
import kz.tamur.rt.adapters.ImageColumnAdapter;
import kz.tamur.web.OrWebImage;

import org.jdom.Element;
import com.cifs.or2.kernel.KrnException;

public class OrWebImageColumn extends OrWebTableColumn {

    /** Идентификатор компонента */
    
    /** Константа PROPS. */
    public static final PropertyNode PROPS = new ImageColumnPropertyRoot();

    OrWebImageColumn(Element xml, int mode, OrFrame frame, String id) throws KrnException {
        super(xml, mode, frame, id);
        editor = new OrWebImage(xml, mode, frame, true, id);
        adapter = new ImageColumnAdapter(frame, this);
    }

    public PropertyNode getProperties() {
        return PROPS;
    }
}
