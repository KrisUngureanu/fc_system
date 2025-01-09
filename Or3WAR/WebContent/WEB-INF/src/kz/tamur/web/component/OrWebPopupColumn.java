package kz.tamur.web.component;

import kz.tamur.comps.models.PopupColumnPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.Mode;
import kz.tamur.rt.adapters.PopupColumnAdapter;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;

public class OrWebPopupColumn extends OrWebTableColumn {

    public static final PropertyNode PROPS = new PopupColumnPropertyRoot();

    OrWebPopupColumn(Element xml, int mode, OrFrame frame, String id) throws KrnException {
        super(xml, mode, frame, id);
        editor = new OrWebHyperPopup(xml, mode, frame, true, id);
        if (mode == Mode.RUNTIME) {
            adapter = new PopupColumnAdapter(frame, this);
            ((OrWebHyperPopup)editor).setColumnAdapter(adapter);
        }
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public int getTabIndex() {
        return -1;
    }
}
