package kz.tamur.web.component;

import kz.tamur.comps.Constants;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.models.DocFieldColumnPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.rt.adapters.DocFieldColumnAdapter;

import org.apache.commons.fileupload.FileItem;
import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonObject;

public class OrWebDocFieldColumn extends OrWebTableColumn {

    private static final PropertyNode PROPS = new DocFieldColumnPropertyRoot();

    OrWebDocFieldColumn(Element xml, int mode, OrFrame frame, String id) throws KrnException {
        super(xml, mode, frame, id);
        editor = new OrWebDocField(xml, mode, frame, true, id);
        adapter = new DocFieldColumnAdapter(frame, this);
    }

    public JsonObject getCellEditor(int row) {
        table.absoluteRow(row);
        JsonObject options = new JsonObject();
        switch (((OrWebDocField) editor).getAdapter().getAction()) {
        case Constants.DOC_UPDATE:
            options.add("action", "DOC_UPDATE");
            break;
        case Constants.DOC_VIEW:
            options.add("action", "DOC_VIEW");
            break;
        case Constants.DOC_EDIT:
            options.add("action", "DOC_EDIT");
            break;
        default:
            return options;
        }
        options.add("uid", getUUID());
        options.add("row", row);
        return new JsonObject().add("type", "file").add("options", options);
    }

    public void setValue(int row, FileItem fileItem) {
        table.absoluteRow(row);
        ((OrWebDocField) editor).setValue(fileItem);
    }

    public PropertyNode getProperties() {
        return PROPS;
    }
}
