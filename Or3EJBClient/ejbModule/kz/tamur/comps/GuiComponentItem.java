package kz.tamur.comps;

import static kz.tamur.comps.models.Types.BOOLEAN;
import static kz.tamur.comps.models.Types.BORDER;
import static kz.tamur.comps.models.Types.COLOR;
import static kz.tamur.comps.models.Types.DOUBLE;
import static kz.tamur.comps.models.Types.ENUM;
import static kz.tamur.comps.models.Types.ENUM_TOOL_TIP;
import static kz.tamur.comps.models.Types.EXPR;
import static kz.tamur.comps.models.Types.FILTER;
import static kz.tamur.comps.models.Types.FONT;
import static kz.tamur.comps.models.Types.GRADIENT_COLOR;
import static kz.tamur.comps.models.Types.HTML_TEXT;
import static kz.tamur.comps.models.Types.IMAGE;
import static kz.tamur.comps.models.Types.INTEGER;
import static kz.tamur.comps.models.Types.KRNOBJECT;
import static kz.tamur.comps.models.Types.KRNOBJECT_ID;
import static kz.tamur.comps.models.Types.MSTRING;
import static kz.tamur.comps.models.Types.PMENUITEM;
import static kz.tamur.comps.models.Types.PROCESSES;
import static kz.tamur.comps.models.Types.REF;
import static kz.tamur.comps.models.Types.REPORT;
import static kz.tamur.comps.models.Types.RSTRING;
import static kz.tamur.comps.models.Types.SEQUENCE;
import static kz.tamur.comps.models.Types.STRING;
import static kz.tamur.comps.models.Types.STYLEDTEXT;
import static kz.tamur.comps.models.Types.VIEW_STRING;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.KrnObjectItem;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import kz.tamur.comps.models.EnumValue;
import kz.tamur.comps.models.EnumValueToolTip;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.DesignerFrame;
import kz.tamur.guidesigner.InterfaceFrame;
import kz.tamur.guidesigner.MessagesFactory;
import kz.tamur.guidesigner.filters.FilterRecord;
import kz.tamur.or3.client.props.BorderProperty;
import kz.tamur.or3.client.props.CheckProperty;
import kz.tamur.or3.client.props.ColorProperty;
import kz.tamur.or3.client.props.ComboProperty;
import kz.tamur.or3.client.props.ComboPropertyItem;
import kz.tamur.or3.client.props.ComboToolTipProperty;
import kz.tamur.or3.client.props.DescProperty;
import kz.tamur.or3.client.props.ExprProperty;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.or3.client.props.FolderProperty;
import kz.tamur.or3.client.props.FontProperty;
import kz.tamur.or3.client.props.GradientColorProperty;
import kz.tamur.or3.client.props.GuiComponentProperty;
import kz.tamur.or3.client.props.HTMLProperty;
import kz.tamur.or3.client.props.ImageProperty;
import kz.tamur.or3.client.props.Inspectable;
import kz.tamur.or3.client.props.KrnObjectProperty;
import kz.tamur.or3.client.props.KrnOrExprProperty;
import kz.tamur.or3.client.props.Property;
import kz.tamur.or3.client.props.ReportProperty;
import kz.tamur.or3.client.props.StringProperty;
import kz.tamur.or3.client.props.TreeOrExprProperty;
import kz.tamur.or3.client.props.TreeProperty;
import kz.tamur.or3.client.props.UiOrJumpProperty;
import kz.tamur.or3.client.props.inspector.RefProperty;
import kz.tamur.util.Pair;


public class GuiComponentItem implements Inspectable {
    private static Map<Class, Property> proots = new HashMap<Class, Property>();
    private Object item;
    private DesignerFrame df;
    private PropertyNode props;
    public GuiComponentItem(Object item, DesignerFrame owner){
       this.item = item;
        this.df=owner;
    }

    /**
     * Получает свойства для компонента что находится в <code>GuiComponentItem.item</code>
     */
    public Object getItem() {
    	return this.item;
    }
    
    public DesignerFrame getDesignerFrame() {
    	return df;
    }
    
    public Property getProperties() {
        if (item instanceof OrGuiComponent) {
            Property proot = proots.get(item.getClass());
            // инициализация компонента
            if (proot == null) {
                props = ((OrGuiComponent) item).getProperties();
                proot = convertGuiProperties(null, props);
                proot.setNode(props);
                proots.put(item.getClass(), proot);
            }
            // возвратить уже существующую карту свойств
            return proot;
        }
        return null;
    }

    /**
     * Используется для компонентов с динамическими свойствами.
     * Если <code>GuiComponentItem.item</code> не <code>OrChartPanel</code>, то действие метода идентично {@link kz.tamur.comps.GuiComponentItem#getProperties()}.
     * Если же в <code>GuiComponentItem.item</code> содержится <code>OrChartPanel</code> - метод перечитывает карту свойств объекта
     */
    public Property getNewProperties() {
        if (item instanceof OrGuiComponent) {
            Property proot = proots.get(item.getClass());
            if (proot == null) {
                props = ((OrGuiComponent) item).getProperties();
                proot = convertGuiProperties(null, props);
                proots.put(item.getClass(), proot);
            } else if (item instanceof OrChartPanel 
                    || item instanceof OrButton 
                    || item instanceof OrPopUpPanel
                    || item instanceof OrDocField 
                    || item instanceof OrHyperLabel 
                    || item instanceof OrHyperPopup
                    || item instanceof OrTreeField
                    || item instanceof OrAccordion) {
                props = ((OrGuiComponent) item).getProperties();
                proot = convertGuiProperties(null, props);
                proots.clear();
                proots.put(item.getClass(), proot);
            }
            return proot;
        }
        return null;
    }

    public Object getValue(Property prop) {
        Object res = "";
        if (item != null && !(prop instanceof FolderProperty)) {
            if (item instanceof OrGuiComponent) {
                PropertyValue pv = getPropertyValue(((OrGuiComponent) item), getPath(prop));
                if (pv != null && pv.objectValue() != null) {
                    int pt = pv.getProperty().getType();
                    Pair p;
                    switch (pt) {
                    case INTEGER:
                        res = String.valueOf(pv.intValue());
                        break;
                    case DOUBLE:
                        res = String.valueOf(pv.doubleValue());
                        break;
                    case STRING:
                    case VIEW_STRING:
                    case MSTRING:
                        res = pv.stringValue();
                        break;
                    case BOOLEAN:
                        res = pv.booleanValue();
                        break;
                    case KRNOBJECT:
                        if (pv.getKrnClassName() != null && !"".equals(pv.getKrnClassName()) && pv.getKrnObjectId() != null
                                && !"".equals(pv.getKrnObjectId())) { // TODO оптимизировать
                            StringTokenizer st_ids = new StringTokenizer(pv.getKrnObjectId(), ",");
                            StringTokenizer st_titles = new StringTokenizer(pv.getTitle(), ",");
                            Vector<KrnObjectItem> objs1 = new Vector<KrnObjectItem>();
                            while (st_ids.hasMoreTokens()) {
                                String id_ = st_ids.nextToken();
                                String title_ = "";
                                if (st_titles.hasMoreTokens()) {
                                    title_ = st_titles.nextToken();
                                }
                                try {
                                    KrnObject[] krn_obj = Kernel.instance().getObjectsByIds(new long[] { Long.valueOf(id_) }, -1);
                                    if (krn_obj.length > 0) {
                                        objs1.add(new KrnObjectItem(krn_obj[0], title_));
                                    }
                                } catch (KrnException e) {
                                    e.printStackTrace();
                                }
                            }
                            res = objs1;
                        }
                        break;
                    case REF:
                        res = pv.stringValue();
                        break;
                    case EXPR:
                        res = pv.stringValue();
                        break;
                    case COLOR:
                        res = pv.colorValue();
                        break;
                    case FONT:
                        res = pv.fontValue();
                        break;
                    case BORDER:
                        res = pv.borderValue();
                        break;
                    case IMAGE:
                        res = pv.getImageValue();
                        break;
                    case STYLEDTEXT:
                        p = pv.resourceStringValue();
                        if (p != null) {
                            res = p.second;
                        }
                        break;
                    case REPORT:
                        res = pv.reportValue();
                        break;
                    case SEQUENCE:
                        res = "";
                        break;
                    case FILTER:
                        Object value = pv.objectValue();
                        Vector<KrnObjectItem> objs = new Vector<KrnObjectItem>();
                        if (value instanceof FilterRecord) {
                            FilterRecord fr = (FilterRecord) value;
                            objs.add(new KrnObjectItem(fr.getKrnObject(), fr.getTitle()));
                        } else if (value instanceof FilterRecord[]) {
                            for (FilterRecord fr : (FilterRecord[]) value) {
                                objs.add(new KrnObjectItem(fr.getKrnObject(), fr.getTitle()));
                            }
                        }
                        res = objs;
                        break;
                    case PMENUITEM:
                        res = pv.menuItemsValues();
                        break;
                    case ENUM:
                    case ENUM_TOOL_TIP:
                        res = pv.stringValue();
                        break;
                    case RSTRING:
                        p = pv.resourceStringValue();
                        if (p != null) {
                            res = p.second;
                        }
                        break;
                    case KRNOBJECT_ID:// получение идентификаторов выбранных объектов
                        String krnObj = pv.getKrnObjectId();
                        if (krnObj != null && !krnObj.isEmpty()) {
                            // удаление левой части идентификатора
                            res = krnObj.replaceAll("\\d+\\.", "");
                        }
                        break;
                    case HTML_TEXT:
                        if (pv.objectValue() instanceof Expression) {
                            res = pv.objectValue();
                        } else {
                            p = pv.resourceStringValue();
                            if (p != null) {
                                res = p.second;
                            }
                        }
                        break;
                    case GRADIENT_COLOR:
                        res = pv.objectValue();
                        break;
                    default:
                        res = pv.objectValue();
                        break;
                    }
                } else if (pv != null && pv.getProperty() != null) {
                    res = pv.getProperty().getDefaultValue();
                }
            }
            if (res == null)
                res = "";
            if (prop instanceof ComboProperty) {
                res = ((ComboProperty) prop).getItem(res.toString());
            } else if (prop instanceof ComboToolTipProperty) {
                res = ((ComboToolTipProperty) prop).getItem(res.toString());
            } else if (prop instanceof ExprProperty || (prop instanceof KrnOrExprProperty && res instanceof String)
                    || (prop instanceof TreeOrExprProperty && res instanceof String)) {
                res = new Expression(res.toString());
            }
        }
        return res;
    }

    public void setValue(Property prop, Object value) {
        if (item instanceof OrGuiComponent && !(prop instanceof FolderProperty)) {
            OrGuiComponent item = (OrGuiComponent) this.item;
            String nameProp = prop.getId();
            if (value instanceof Expression && !("toolTip".equals(nameProp) || "editor".equals(nameProp))) {
                value = ((Expression) value).text;
            }
            setPropertyValue(item, getPath(prop), value);
            df.setModified(item);

            // обработка динамических свойств компонентов
            if (item instanceof OrChartPanel) {
                if ("typeChart".equals(nameProp)) {
                    // обновление
                    ((OrChartPanel) item).initDynProp(Integer.parseInt(((ComboPropertyItem) value).id), true);
                    // Обновить таблицу вывода данных
                    df.reloadProp(item);
                } else if ("countSeries601".equals(nameProp)) {
                    ((OrChartPanel) item).initDynPropSeries(Integer.parseInt(((ComboPropertyItem) value).id), true);
                    df.reloadProp(item);
                }
            }

            if ("showOnTopPan".equals(nameProp)) {
                item.updateDynProp();
                df.reloadProp(item);
            }
            
            if (item instanceof OrAccordion && "countPanel".equals(nameProp)) {
                item.updateDynProp();
                df.reloadProp(item);
            }
            if (item instanceof OrGISPanel && "layersCount".equals(nameProp)) {
                item.updateDynProp();
                df.reloadProp(item);
            }
        }
    }
    
    public void setValue(Property prop, Object value, InterfaceFrame ifc) {
        if (item instanceof OrGuiComponent && !(prop instanceof FolderProperty)) {
            OrGuiComponent item = (OrGuiComponent) this.item;
            String nameProp = prop.getId();
            if (value instanceof Expression && !("toolTip".equals(nameProp) || "editor".equals(nameProp))) {
                value = ((Expression) value).text;
            }
            setPropertyValue(item, getPath(prop), value);
            df.setModified(item, ifc);

            // обработка динамических свойств компонентов
            if (item instanceof OrChartPanel) {
                if ("typeChart".equals(nameProp)) {
                    // обновление
                    ((OrChartPanel) item).initDynProp(Integer.parseInt(((ComboPropertyItem) value).id), true);
                    // Обновить таблицу вывода данных
                    df.reloadProp(item);
                } else if ("countSeries601".equals(nameProp)) {
                    ((OrChartPanel) item).initDynPropSeries(Integer.parseInt(((ComboPropertyItem) value).id), true);
                    df.reloadProp(item);
                }
            }

            if ("showOnTopPan".equals(nameProp)) {
                item.updateDynProp();
                df.reloadProp(item);
            }
            
            if (item instanceof OrAccordion && "countPanel".equals(nameProp)) {
                item.updateDynProp();
                df.reloadProp(item);
            }
            if (item instanceof OrGISPanel && "layersCount".equals(nameProp)) {
                item.updateDynProp();
                df.reloadProp(item);
            }
        }
    }
    
	public void setValue(Property prop, Object value, Object oldValue) {
		setValue(prop, value);
	}

    private String getPath(Property prop){
        if (prop==null) {
            return null;
        }
        String res= prop.getId();
        Property prop_=prop.getParent();
        while(prop_.getId()!=null && !"Root".equals(prop_.getId())){
             res=prop_.getId()+"."+res;
             prop_=prop_.getParent();
        }
        return res;
    }
    
    private PropertyValue getPropertyValue(OrGuiComponent c, String propId) {
        PropertyValue res = null;
        if (propId != null) {
            StringTokenizer st = new StringTokenizer(propId, ".");
            PropertyNode pn = c.getProperties();
            while (pn != null && st.hasMoreTokens()) {
                pn = pn.getChild(st.nextToken());
            }
            if (pn != null) {
                res = c.getPropertyValue(pn);
            }
        }
        return res;
    }
 
    private void setPropertyValue(OrGuiComponent c, String propId, Object value) {
        StringTokenizer st = new StringTokenizer(propId, ".");
        PropertyNode pn = c.getProperties();
        // блокировка логических операторов
        boolean isBlock = false;
        while (pn != null && st.hasMoreTokens()) {
            pn = pn.getChild(st.nextToken());
        }
        if (pn != null) {
            Pair<String, Object> p;
            PropertyValue pv;
            switch (pn.getType()) {
            case FILTER:
                if (value != null) {
                    if (pn.isArray()) {
                        FilterRecord[] value_ = new FilterRecord[((Vector) value).size()];
                        for (int i = 0; i < value_.length; ++i) {
                            KrnObjectItem obj = (KrnObjectItem) ((Vector) value).get(i);
                            value_[i] = new FilterRecord(obj.obj, obj.title);
                        }
                        value = value_;
                    } else {
                        value = new FilterRecord(((KrnObjectItem) value).obj, ((KrnObjectItem) value).title);
                    }
                }
                break;
            case STYLEDTEXT:
            case RSTRING:
                pv = c.getPropertyValue(pn);
                p = pv.resourceStringValue();
                value = new Pair(p != null ? p.first : null, value);
                break;
            case HTML_TEXT:
                if (value instanceof byte[]) {
                    pv = c.getPropertyValue(pn);
                    p = pv.resourceStringValue();
                    value = new Pair(p != null ? p.first : null, value);
                }
                break;
            case KRNOBJECT_ID:
                if (value != null) {
                    isBlock = true;
                    // перенести идентификаторы объектов в вектор
                    Vector<KrnObjectItem> val = (Vector<KrnObjectItem>) value;
                    StringBuilder temp = new StringBuilder();
                    final int size = val.size() - 1;
                    // сконвертировать вектор в строку, разделяя элементы запятой
                    for (int i = 0;; ++i) {
                        temp.append((val.get(i)).obj.id);
                        if (i == size) {
                            break;
                        }
                        temp.append(",");
                    }
                    value = temp.toString();
                }
                break;
            }

            if (value instanceof Vector && !isBlock) {
                KrnObjectItem obj = (KrnObjectItem) ((Vector) value).get(0);
                c.setPropertyValue(new PropertyValue(String.valueOf(obj.obj.id), pn.getKrnClassName(), obj.title, pn));
            } else if (value instanceof KrnObjectItem) {
                c.setPropertyValue(new PropertyValue(String.valueOf(((KrnObjectItem) value).obj.id), pn.getKrnClassName(),
                        ((KrnObjectItem) value).title, pn));
            } else {
                if (value instanceof String && ((String)value).length() > 0) {
                	try {
	                	if (pn.getType() == INTEGER) {
	                        value = Integer.valueOf((String) value);
	                    } else if (pn.getType() == DOUBLE) {
	                        value = Double.valueOf((String) value);
	                    }
                	} catch (NumberFormatException e) {
                		MessagesFactory.showMessageDialog(df.getTopLevelAncestor(), MessagesFactory.ERROR_MESSAGE,
                				"Неверный формат значения!");
                		value = c.getPropertyValue(pn);
                	}
                }
                c.setPropertyValue(new PropertyValue(value, pn));
            }
        }
    }

    private Property convertGuiProperties(Property parent, PropertyNode node) {
        Property res = null;
        int nt = node.getType();
        if (df != null) {
            node.setPlainMode(df.isPlainMode());
        }
        if (nt == -1) {
            Property parent_ = new FolderProperty(parent, node);
            if (parent == null) {
                res = parent_;
            }
            int count = node.getChildCount();
            for (int i = 0; i < count; i++) {
                convertGuiProperties(parent_, node.getChildAt(i));
            }
        } else {

            switch (nt) {
            case INTEGER:
            case DOUBLE:
            case STRING:
            case MSTRING:
                new StringProperty(parent, node);
                break;
            case VIEW_STRING:
                new StringProperty(parent, node, false);
                break;
            case BOOLEAN:
                new CheckProperty(parent, node);
                break;
            case REF:
                new RefProperty(parent, node);
                break;
            case EXPR:
                new ExprProperty(parent, node);
                break;
            case COLOR:
                new ColorProperty(parent, node);
                break;
            case FONT:
                new FontProperty(parent, node);
                break;
            case BORDER:
                new BorderProperty(parent, node);
                break;
            case IMAGE:
                new ImageProperty(parent, node);
                break;
            case STYLEDTEXT:
                new DescProperty(parent, node);
                break;
            case REPORT:
                new ReportProperty(parent, node);
                break;
            case SEQUENCE:
                new StringProperty(parent, node);
                break;
            case FILTER:
                if (node.isArray()) {
                    new KrnObjectProperty(parent, node, "Filter", "title");
                } else {
                    new TreeProperty(parent, node, "Filter");
                }
                break;
            case PMENUITEM:
                new GuiComponentProperty(parent, node, nt);
                break;
            case ENUM:
                ComboProperty list = new ComboProperty(parent, node);
                EnumValue[] evs = node.getEnumValues();
                for (EnumValue ev : evs) {
                    list.addItem(String.valueOf(ev.code), ev.name);
                }
                break;
            case ENUM_TOOL_TIP:
                ComboToolTipProperty list1 = new ComboToolTipProperty(parent, node);
                EnumValueToolTip[] evs1 = (EnumValueToolTip[]) node.getEnumToolTipValues();
                for (EnumValueToolTip ev : evs1) {
                    list1.addItem(String.valueOf(ev.code), ev.name, ev.pathIco);
                }
                break;
            case RSTRING:
                new StringProperty(parent, node);
                break;
            case PROCESSES:
                new GuiComponentProperty(parent, node, nt);
                break;
            case KRNOBJECT:
            case KRNOBJECT_ID:
                if ("UI".equals(node.getKrnClassName())) {
                    new UiOrJumpProperty(parent, node.getName(), node.toString());
                } else {
                    new KrnObjectProperty(parent, node);
                }
                break;
            case HTML_TEXT:
                new HTMLProperty(parent, node);
                break;
            case GRADIENT_COLOR:
                new GradientColorProperty(parent, node);
                break;
            }
        }
        return res;
    }
   
    public String getTitle() {
        String res = "";
        if (item == null) {
            res = "Интерфейсы";
        } else {
            String caption = "";
            String compClass = "";
            PropertyValue pv = null;
            OrGuiComponent comp = (OrGuiComponent) item;
            PropertyNode node = comp.getProperties().getChild("title");
            if (!(item instanceof OrTableColumn) && node != null && !(item instanceof OrCheckBox)) {
                pv = comp.getPropertyValue(node);
                if (pv != null) {
                    int type = node.getType();
                    if (type == VIEW_STRING || type == STRING || type == MSTRING) {
                        caption = pv.stringValue();
                    } else if (type == RSTRING) {
                        caption = (String) pv.resourceStringValue().second;
                    }
                }
            } else if (comp instanceof OrTableColumn) {
                pv = comp.getPropertyValue(comp.getProperties().getChild("header").getChild("text"));
                if (pv != null) {
                    caption = (String) pv.resourceStringValue().second;
                }
            }
            String packName = "kz.tamur.comps.";
            compClass = comp.getClass().getName().substring(packName.length());
            res = "Интерфейсы" + " - " + caption + " [" + compClass + "]";
        }
        return res;
    }
}
