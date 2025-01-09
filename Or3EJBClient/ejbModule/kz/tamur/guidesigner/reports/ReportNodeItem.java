package kz.tamur.guidesigner.reports;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.KrnObjectItem;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import kz.tamur.comps.Constants;
import kz.tamur.comps.OrReportPrinter;
import kz.tamur.comps.PropertyValue;
import kz.tamur.comps.models.En2Ru;
import kz.tamur.or3.client.props.*;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class ReportNodeItem implements Inspectable {
    private static Property proot;
    private Object item;
    private ReportPanel owner;

    public ReportNodeItem(Object item, ReportPanel owner) {
        this.item = item;
        this.owner = owner;
    }

    public Property getProperties() {
        proot = new FolderProperty(null, null, "Элементы");
        if (item != null) {
            if (item instanceof ReportNode) {
                new StringProperty(proot, "title", "Заголовок");
                new StringProperty(proot, "titleKaz", "ЗаголовокКаз");
                ComboProperty oper = new ComboProperty(proot, "editorType", "Тип редактора");
                oper.addItem("" + Constants.MSWORD_EDITOR, "Microsoft Word")
                	.addItem("" + Constants.MSEXCEL_EDITOR, "Microsoft Excel")
                	.addItem("" + Constants.JASPER_EDITOR, "Jasper Reports");
                new CheckProperty(proot, "groupType", "Одиночный");
                new StringProperty(proot, "macros", "Запустить макрос");
                new StringProperty(proot, "templatePassword", En2Ru.translate("templatePassword"));
                new KrnObjectProperty(proot, "bases", "Структуры баз", "Структура баз", "наименование");
            }
        }
        return proot;
    }

    public Object getValue(Property prop) {
        Object res = "";
        if (item != null && !(prop instanceof FolderProperty)) {
            if (item instanceof ReportNode && ((ReportNode) item).getOrGuiComponent() != null) {
                PropertyValue value = ((OrReportPrinter) ((ReportNode) item).getOrGuiComponent()).getPropertyValue(prop.getId());
                if (!value.isNull()) {
                    res = value.objectValue();
                    if ("bases".equals(prop.getId()) && res instanceof Map) {
                        Vector<KrnObjectItem> objs = new Vector<KrnObjectItem>();
                        Map value_ = (Map) res;
                        for (Object val : value_.keySet()) {
                            KrnObject[] krn_obj = new KrnObject[0];
                            try {
                                krn_obj = Kernel.instance().getObjectsByIds(new long[] { (Long) val }, -1);
                            } catch (KrnException e) {
                                e.printStackTrace();
                            }
                            if (krn_obj.length > 0) {
                                objs.add(new KrnObjectItem(krn_obj[0], value_.get(val).toString()));
                            }
                        }
                        res = objs;
                    }
                }
            }
            if (res == null)
                res = "";
            if (prop instanceof ComboProperty) {
                res = ((ComboProperty) prop).getItem(res != null ? res.toString() : "");
            }

        }
        return res;
    }

    public void setValue(Property prop, Object value) {
        if (item != null && item instanceof ReportNode && !(prop instanceof FolderProperty)) {
            if (prop instanceof ComboProperty) {
                value = ((ComboPropertyItem) value).id;
            }
            if ("bases".equals(prop.getId())) {
                Map m = new TreeMap();
                if (value instanceof Vector) {
                    for (KrnObjectItem obj : (Vector<KrnObjectItem>) value) {
                        m.put(obj.obj.id, obj.title);
                    }
                }
                ((OrReportPrinter) ((ReportNode) item).getOrGuiComponent()).setPropertyValue(prop.getId(), m);
            } else {
                ((OrReportPrinter) ((ReportNode) item).getOrGuiComponent()).setPropertyValue(prop.getId(), value);
                if ("title".equals(prop.getId())) {
                    ((ReportNode) item).setTitle((String) value);
                    owner.nodeRename();
                }
            }
            owner.setModified();
        }
    }
    
	public void setValue(Property prop, Object value, Object oldValue) {
		setValue(prop, value);
	}

    public String getTitle() {
        return "";
    }

    @Override
    public Property getNewProperties() {
        return null;
    }
}
