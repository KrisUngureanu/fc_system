package kz.tamur.web.component;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.models.TreeColumnPropertyRoot;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.PropertyValue;
import kz.tamur.rt.adapters.TreeColumnAdapter;
import kz.tamur.util.Pair;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * User: �������������
 * Date: 07.06.2007
 * Time: 15:11:43
 * To change this template use File | Settings | File Templates.
 */
public class OrWebTreeColumn extends OrWebTableColumn {

    public static final PropertyNode PROPS = new TreeColumnPropertyRoot();
    private String tipForInput;

    OrWebTreeColumn(Element xml, int mode, OrFrame frame, String id) throws KrnException {
        super(xml, mode, frame, id);
        editor = new OrWebTreeField(xml, mode, frame, true, id);
        adapter = new TreeColumnAdapter(frame, this);
    }

    protected void init() {
        PropertyValue pv = getPropertyValue(getProperties().getChild("header").getChild("tipForInput"));
        if (!pv.isNull()) {
            Pair p = pv.resourceStringValue();
            tipForInput = frame.getString((String) p.first);
        }
        super.init();
    }
    
    public String getTipForInput() {
    	return tipForInput;
    }
    
    public PropertyNode getProperties() {
        return PROPS;
    }

    public int getTabIndex() {
        return -1;
    }

    public void setLangId(long langId) {
        super.setLangId(langId);
        editor.setLangId(langId);
    }
    
    public String getData(String nid) {
    	return ((OrWebTreeField)editor).getData(nid);
    }

    public JsonObject getCellEditor(int row) {
        table.absoluteRow(row);
        return new JsonObject().add("tree", 1).add("title", getTitle().replace("@", " "));
    }
}