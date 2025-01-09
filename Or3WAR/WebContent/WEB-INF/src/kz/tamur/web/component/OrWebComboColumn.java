package kz.tamur.web.component;

import kz.tamur.comps.models.ComboColumnPropertyRoot;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.comps.*;
import kz.tamur.rt.adapters.ComboBoxAdapter;
import kz.tamur.rt.adapters.ComboBoxAdapter.OrComboItem;
import kz.tamur.rt.adapters.ComboColumnAdapter;

import org.jdom.Element;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

public class OrWebComboColumn extends OrWebTableColumn {

    public static final PropertyNode PROPS = new ComboColumnPropertyRoot();
    private Kernel krn;
    
    OrWebComboColumn(Element xml, int mode, OrFrame frame, String id) throws KrnException {
        super(xml, mode, frame, id);
        krn = ((WebFrame)frame).getSession().getKernel();
        editor = new OrWebComboBox(xml, mode, frame, true, id);
        adapter = new ComboColumnAdapter(frame, this);
    }

    public PropertyNode getProperties() {
        return PROPS;
    }

    public void setLangId(long langId) {
        super.setLangId(langId);
        editor.setLangId(langId);
    }

    public int getTabIndex() {
        return -1;
    }
    
    public JsonArray getData() {
    	((ComboBoxAdapter)editor.getAdapter()).calculateContent();
        JsonArray arr = new JsonArray();
        for (int i = 0; i < ((OrWebComboBox) editor).getItemCount(); ++i) {
            JsonObject itemObj = new JsonObject();
            OrComboItem item = (OrComboItem) ((OrWebComboBox) editor).getItemAt(i);
            itemObj.add(uuid, item.getObject() != null ? item.getObject().uid : item.toString());
            //String title = ((OrWebComboBox) editor).getItemAt(i).toString();
//            if (title.isEmpty()) {
//                title = "-";
//            }
            JsonArray titleArr = new JsonArray();
        	if (item.isListTitle()) {
        		for (Object title : item.getTitles()) {
        			titleArr.add(title != null ? title.toString() : "");
        		}
        	} else {
        		titleArr.add(item.toString());
        	}
            itemObj.add(uuid + "-title", titleArr);
            arr.add(itemObj);
        }

        return arr;
    }

    public JsonObject getCellEditor(int row) {
    	boolean comboSearch = ((OrWebComboBox) editor).getComboSeach();
        table.absoluteRow(row);
        return new JsonObject().add("type", "combobox").add("options", new JsonObject()
					.add("novalidate", true)
        			.add("valueField", uuid)
        			.add("textField", uuid + "-title")
        			.add("data",  getData())
        			.add("toUpperCase", comboSearch));
    }
}
