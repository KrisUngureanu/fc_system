package kz.tamur.comps;

import static kz.tamur.comps.models.Types.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.border.Border;
import javax.swing.text.Document;

import kz.tamur.comps.models.EnumValue;
import kz.tamur.comps.models.GradientColor;
import kz.tamur.comps.models.PropertyNode;
import kz.tamur.guidesigner.InterfaceFrame;
import kz.tamur.guidesigner.filters.FilterRecord;
import kz.tamur.guidesigner.reports.ReportRecord;
import kz.tamur.guidesigner.serviceControl.ControlTabbedContent;
import kz.tamur.or3.client.props.ComboPropertyItem;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.or3.client.props.inspector.ProcessRecord;
import kz.tamur.rt.Utils;
import kz.tamur.util.Pair;

import org.jdom.CDATA;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Text;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.KrnObjectItem;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.04.2004
 * Time: 14:1:30
 */
public class PropertyValue {
    private Object value;
    private PropertyNode prop;
    private String title;
    private String className;
    private String uids;
    private long langId;
    private boolean isArray;

    public PropertyValue(Object value, PropertyNode prop) {
        this.value = value;
        this.prop = prop;
        isArray = prop.isArray();
    }

    public Object getValue() {
        return value;
    }

    public PropertyNode getPropertyNode() {
        return prop;
    }

    public PropertyValue(Element e, PropertyNode prop, long langId) {
        this.prop = prop;
        this.langId = langId;
        isArray = prop != null ? prop.isArray() : false;
        String str;
        Element ch;
        List children;
        if (e != null) {
            switch (prop.getType()) {
            case INTEGER:
                str = e.getText();
                if (str.equals("true") || str.equals("false")) {
                    str = "0";
                }
                value = (str.length() > 0) ? new Integer(str) : null;
                break;
            case DOUBLE:
                str = e.getText().replaceAll("[^0-9\\.]", "");
                try {
                    value = (str.length() > 0) ? new Double(str) : null;
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                    value = null;
                }
                break;
            case STRING:
            case VIEW_STRING:
                value = e.getText();
                break;
            case MSTRING:
                ch = e.getChild("L" + langId);
                if (ch == null) {
                    children = e.getChildren();
                    if (children.size() > 0) {
                        value = ((Element) children.get(0)).getText();
                    }
                } else {
                    value = ch.getText();
                }
                break;
            case BOOLEAN:
                value = Boolean.valueOf(e.getText());
                break;
            case KRNOBJECT:
                children = e.getChildren();
                if (children.size() > 0) {
                    try {
                        if (!isArray) {
                            ch = (Element) children.get(0);
                            str = ch.getAttributeValue("id");
                            String className_ = ch.getAttributeValue("class");
                            if (str != null && !str.isEmpty()) {
                                StringBuilder vl = new StringBuilder();
                                
                                Pattern pattern = Pattern.compile("[^\\,]+"); // ищем разделенные запятой уиды
                        		Matcher m = pattern.matcher(str);
                        		while (m.find()) {
                                    String val = m.group();
                                    if (val.contains(".")
                                            || (className_.equals("Language") && (val.equals("102") || val.equals("103")))) {
                                        vl.append(getIdByUid(val));
                                    } else
                                        vl.append(val);
                        			
                        		  vl.append(',');
                        		}
                                value = vl.substring(0, vl.length() - 1);
                            } else
                                value = "";

                            uids = str;
                            className = ch.getAttributeValue("class");
                            title = ch.getAttributeValue("title");
                        } else {
                            ch = (Element) children.get(0);
                            className = ch.getAttributeValue("class");
                            List allObjs = ch.getChildren();
                            if (allObjs.size() > 0) {
                                Map m = new TreeMap();
                                String val = "";
                                for (int i = 0; i < allObjs.size(); i++) {
                                    Element c = (Element) allObjs.get(i);
                                    String str1 = c.getAttributeValue("id");
                                    StringTokenizer st = new StringTokenizer(str1, ".");
                                    if (st.countTokens() > 1
                                            || (className.equals("Language") && (str1.equals("102") || str1.equals("103")))) {
                                        val = String.valueOf(getIdByUid(str1));
                                    } else {
                                        val = str1;
                                    }
                                    if ("filters".equals(prop.getName())) {
                                        m.put(new Long(val), new Pair(c.getAttributeValue("title"), c.getAttributeValue("expr")));
                                    } else {
                                        m.put(new Long(val), c.getAttributeValue("title"));
                                    }
                                }
                                value = m;
                            }
                        }
                    } catch (KrnException e1) {
                        e1.printStackTrace();
                    }
                }
                break;
            case KRNOBJECT_ITEM:
                children = e.getChildren();
                if (children.size() > 0) {
                    try {
                        KrnObject fobj = null;
                        ch = (Element) children.get(0);
                        if (!isArray) {
                            str = ch.getAttributeValue("id");
                            if (str.length() > 0) {
                                StringTokenizer st = new StringTokenizer(str, ".");
                                if (st.countTokens() > 1) {
                                    fobj = Kernel.instance().getCachedObjectByUid(str);
                                } else {
                                    fobj = Kernel.instance().getCachedObjectById(Long.parseLong(str));
                                }
                                String title = ch.getAttributeValue("title");
                                value = new KrnObjectItem(fobj, title);
                            }
                        } else {
                            List flrs = ch.getChildren();
                            if (flrs.size() > 0) {
                            	KrnObjectItem[] filters = new KrnObjectItem[flrs.size()];
                                for (int i = 0; i < flrs.size(); i++) {
                                    Element child = (Element) flrs.get(i);
                                    String str1 = child.getAttributeValue("id");
                                    if (str1.length() > 0) {
                                        StringTokenizer st = new StringTokenizer(str1, ".");
                                        if (st.countTokens() > 1) {
                                            fobj = Kernel.instance().getCachedObjectByUid(str1);
                                        } else {
                                            fobj = Kernel.instance().getCachedObjectById(Long.parseLong(str1));
                                        }
                                        String title = child.getAttributeValue("title");
                                        KrnObjectItem fr = new KrnObjectItem(fobj, title);
                                        filters[i] = fr;
                                    }
                                }
                                value = filters;
                            }
                        }
                    } catch (KrnException e1) {
                        e1.printStackTrace();
                    }
                }
                break;
            case REF:
            case EXPR:
                value = e.getText();
                break;
            case COLOR:
                str = e.getText();
                try {
                    value = str.isEmpty() ? null : Color.decode(str);
                } catch (Exception e2) {
                    value = null;
                }
                break;
            case FONT:
                str = e.getText();
                value = str.isEmpty() ? null : Font.decode(str);
                break;
            case BORDER:
                str = e.getText();
                value = str.isEmpty() ? null : Utils.decodeBorder(str);
                break;
            case IMAGE:
                str = e.getText();
                value = str.isEmpty() ? null : Utils.decodeImage(str);
                break;
            case STYLEDTEXT:
                value = null;
                if (e.getContentSize() > 0) {
                    java.util.List list = e.getContent();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i) instanceof CDATA) {
                            String s = ((CDATA) list.get(i)).getText();
                            value = new Pair(null, s.getBytes());
                            break;
                        } else if (list.get(i) instanceof Text) {
                            value = new Pair(e.getText(), null);
                        }
                    }
                }
                break;
            case REPORT:
                children = e.getChildren();
                ReportRecord root = new ReportRecord();
                if (children.size() > 0) {
                    try {
                        ch = (Element) children.get(0);
                        long id;
                        long fid;
                        if (!isArray) {
                            str = ch.getAttributeValue("id");
                            StringTokenizer st = new StringTokenizer(str, ".");
                            if (st.countTokens() > 1) {
                                id = getIdByUid(str);
                            } else {
                                id = Long.parseLong(ch.getAttributeValue("id"));
                            }
                            // String title = ch.getAttributeValue("title");
                            String path = ch.getAttributeValue("path");
                            String func = ch.getAttributeValue("func");
                            String visFunc = ch.getAttributeValue("visFunc");
                            str = ch.getAttributeValue("fid");
                            st = new StringTokenizer(str, ".");
                            if (st.countTokens() > 1) {
                                fid = getIdByUid(str);
                            } else {
                                fid = Long.parseLong(ch.getAttributeValue("fid"));
                            }
                            String srv = ch.getAttributeValue("srv");
                            boolean formOnServer = "1".equals(srv);
                            value = new ReportRecord(id, path, fid, func, visFunc, formOnServer);
                        } else {
                            List reps = ch.getChildren();
                            root = new ReportRecord();
                            loadReports(root, reps);
                            value = root;
                        }
                    } catch (KrnException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    value = root;
                }
                break;
            case SEQUENCE:
                children = e.getChildren();
                if (children.size() > 0) {
                    try {
                        ch = (Element) children.get(0);
                        str = ch.getAttributeValue("id");
                        StringTokenizer st = new StringTokenizer(str, ".");
                        if (st.countTokens() > 1) {
                            value = String.valueOf(getIdByUid(str));
                        } else {
                            value = str;
                        }
                        className = ch.getAttributeValue("class");
                        title = ch.getAttributeValue("title");
                    } catch (KrnException e1) {
                        e1.printStackTrace();
                    }
                }
                break;
            case FILTER:
                children = e.getChildren();
                if (children.size() > 0) {
                    try {
                        KrnObject fobj = null;
                        ch = (Element) children.get(0);
                        if (!isArray) {
                            str = ch.getAttributeValue("id");
                            if (str.length() > 0) {
                                StringTokenizer st = new StringTokenizer(str, ".");
                                if (st.countTokens() > 1) {
                                    fobj = Kernel.instance().getCachedObjectByUid(str);
                                } else {
                                    fobj = Kernel.instance().getCachedObjectById(Long.parseLong(str));
                                }
                                String title = ch.getAttributeValue("title");
                                value = new FilterRecord(fobj, title);
                            }
                        } else {
                            List flrs = ch.getChildren();
                            if (flrs.size() > 0) {
                                FilterRecord[] filters = new FilterRecord[flrs.size()];
                                for (int i = 0; i < flrs.size(); i++) {
                                    Element child = (Element) flrs.get(i);
                                    String str1 = child.getAttributeValue("id");
                                    if (str1.length() > 0) {
                                        StringTokenizer st = new StringTokenizer(str1, ".");
                                        if (st.countTokens() > 1) {
                                            fobj = Kernel.instance().getCachedObjectByUid(str1);
                                        } else {
                                            fobj = Kernel.instance().getCachedObjectById(Long.parseLong(str1));
                                        }
                                        String title = child.getAttributeValue("title");
                                        FilterRecord fr = new FilterRecord(fobj, title);
                                        filters[i] = fr;
                                    }
                                }
                                value = filters;
                            }
                        }
                    } catch (KrnException e1) {
                        e1.printStackTrace();
                    }
                }
                break;
            case PMENUITEM:
                children = e.getChildren();
                if (children.size() > 0) {
                    ch = (Element) children.get(0);
                    if (!isArray) {
                        String title = ch.getAttributeValue("title");
                        String expr = ch.getAttributeValue("expr");
                        value = new MenuItemRecord(title, expr);
                    } else {
                        List items = ch.getChildren();
                        if (items.size() > 0) {
                            MenuItemRecord[] mItemRecords = new MenuItemRecord[items.size()];
                            for (int i = 0; i < items.size(); i++) {
                                Element child = (Element) items.get(i);
                                String title = child.getAttributeValue("title");
                                String expr = child.getAttributeValue("expr");
                                MenuItemRecord mr = new MenuItemRecord(title, expr);
                                mItemRecords[i] = mr;
                            }
                            value = mItemRecords;
                        }
                    }
                }
                break;
            case ENUM:
            case ENUM_TOOL_TIP:
                str = e.getText();
                value = str.isEmpty() ? null : new Integer(str);
                break;
            case RSTRING:
                value = new Pair(e.getText(), null);
                break;
            case PROCESSES:
                List<Content> chs = e.getContent();
                if (chs.size() > 0) {
                    Content c = chs.get(0);
                    if (c instanceof CDATA) {
                        value = ((CDATA) c).getText();
                    } else {
                        List<ProcessRecord> prs = new ArrayList<ProcessRecord>();
                        for (Content elm : chs) {
                            if (elm instanceof Element) {
                                Element chE = (Element) elm;
                                KrnObject prObj = null;
                                String name = null;
                                if (chE.getAttributeValue("uid") != null) {
                                    prObj = new KrnObject(Long.parseLong(chE.getAttributeValue("id")),
                                            chE.getAttributeValue("uid"), Long.parseLong(chE.getAttributeValue("cls")));
                                    Element nameE = chE.getChild("name");
                                    name = nameE != null ? nameE.getText() : chE.getText();
                                }
                                String shortNameUid = chE.getAttributeValue("title");
                                Element exprE = chE.getChild("eexpr");
                                Expression eexpr = exprE != null ? new Expression(exprE.getText()) : null;
                                exprE = chE.getChild("vexpr");
                                Expression vexpr = exprE != null ? new Expression(exprE.getText()) : null;
                                exprE = chE.getChild("aexpr");
                                Expression aexpr = exprE != null ? new Expression(exprE.getText()) : null;
                                Element iconE = chE.getChild("icon");
                                BufferedImage icon = null;
                                if (iconE != null) {
                                    try {
                                        ByteArrayInputStream bis = new ByteArrayInputStream(Utils.decodeImage(iconE.getText()));
                                        icon = ImageIO.read(bis);
                                        bis.close();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                                prs.add(new ProcessRecord(prObj, aexpr, name, shortNameUid, eexpr, vexpr, icon));
                            }
                        }
                        if (prs.size() > 0)
                            value = prs;
                    }
                }
                break;
            case KRNOBJECT_ID:
                children = e.getChildren();
                if (children.size() > 0) {
                    ch = (Element) children.get(0);
                    String id = ch.getAttributeValue("id");
                    value = id == null || id.isEmpty() ? "" : id;
                    className = ch.getAttributeValue("class");
                    title = ch.getAttributeValue("title");
                }
                break;
            case HTML_TEXT:
                value = null;
                if (e.getContentSize() > 0) {
                    java.util.List list = e.getContent();
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i) instanceof CDATA) {
                            String s = ((CDATA) list.get(i)).getText();
                            value = new Pair(null, s.getBytes());
                            break;
                        } else if (list.get(i) instanceof Text) {
                            String text = e.getText();
                            if (text.replaceAll("\\d*", "").isEmpty()) {
                                value = new Pair(text, null);
                            } else {
                                value = new Expression(text);
                            }
                        }
                    }
                }
                break;
            case GRADIENT_COLOR:
                str = e.getText();
                value = str == null || str.isEmpty() ? null : new GradientColor(str);
                break;
            default:
                value = e;
                break;
            }
        }
    }

    private void loadReports(ReportRecord parent, List reps) throws KrnException {
        if (reps.size() > 0) {
            for (int i = 0; i < reps.size(); i++) {
                Element child = (Element) reps.get(i);
                if ("Folder".equals(child.getName())) {
                    String uid = child.getAttributeValue("uid");
                    if (uid == null) {
                        InterfaceFrame frm = ControlTabbedContent.instance().getSelectedFrame();
                        uid = frm.getNextUid();
                    }
                    ReportRecord folder = new ReportRecord(uid);
                    parent.addChild(folder);
                    loadReports(folder, child.getChildren());
                } else if ("Report".equals(child.getName())) {
                    String str = child.getAttributeValue("id");
                    long id;
                    if (str.indexOf(".") > -1) {
                        id = getIdByUid(str);
                    } else {
                        id = Long.parseLong(child.getAttributeValue("id"));
                    }
                    String path = child.getAttributeValue("path");
                    String func = child.getAttributeValue("func");
                    String visFunc = child.getAttributeValue("visFunc");
                    String srv = child.getAttributeValue("srv");
                    boolean formOnServer = "1".equals(srv);

                    long fid = 0;
                    if (child.getAttributeValue("fid") != null) {
                        str = child.getAttributeValue("fid");
                        if (str.indexOf(".") > -1) {
                            fid = getIdByUid(str);
                        } else {
                            String f = child.getAttributeValue("fid");
                            if (!"".equals(f)) {
                                fid = Long.parseLong(f);
                            }
                        }
                    }
                    ReportRecord rr = new ReportRecord(id, path, fid, func, visFunc, formOnServer);
                    parent.addChild(rr);
                }
            }
        }
    }

    private void unloadReports(ReportRecord parent, Element reps) throws KrnException {
        List<ReportRecord> reports = parent.getChildren();
        for (ReportRecord report : reports) {
            if (report.isFolder()) {
                Element ch = new Element("Folder");
                String uid = report.getUid();
                ch.setAttribute("uid", uid);
                unloadReports(report, ch);
                reps.addContent(ch);
            } else {
                long[] ids = new long[] { report.getObjId() };
                if (ids[0] != 0) {
                    Map uids = Kernel.instance().getObjectUids(ids);
                    Element ch = new Element("Report");
                    ch.setAttribute("id", uids.get(ids[0]).toString());
                    // ch.setAttribute("title", report.getTitle());
                    ch.setAttribute("path", report.getPath());
                    ch.setAttribute("func", report.getFunc());
                    ch.setAttribute("visFunc", report.getVisibilityFunc());
                    ch.setAttribute("srv", report.formOnServerStr());

                    ids = new long[] { report.getFilterId() };
                    uids = Kernel.instance().getObjectUids(ids);
                    String fid = (String) uids.get(ids[0]);
                    if (fid != null)
                        ch.setAttribute("fid", fid);
                    reps.addContent(ch);
                }
            }
        }
    }

    public PropertyValue(boolean value, PropertyNode prop) {
        this.value = value;
        this.prop = prop;
    }

    public PropertyValue(long value, PropertyNode prop) {
        this.value = (int) value;
        this.prop = prop;
    }

    public PropertyValue(int value, PropertyNode prop) {
        this.value = value;
        this.prop = prop;
    }

    public PropertyValue(double value, PropertyNode prop) {
        this.value = value;
        this.prop = prop;
    }

    public PropertyValue(String id, String className, String title, PropertyNode prop) {
        this.value = id;
        this.className = className;
        this.title = title;
        this.prop = prop;
    }

    public PropertyValue(Map values, String className, PropertyNode prop) {
        this.value = values;
        this.className = className;
        this.prop = prop;
    }

    public PropertyValue(ReportRecord[] val, PropertyNode prop) {
        this.value = val;
        this.prop = prop;
    }

    public PropertyValue(FilterRecord[] val, PropertyNode prop) {
        this.value = val;
        this.prop = prop;
    }

    public PropertyValue(MenuItemRecord[] val, PropertyNode prop) {
        this.value = val;
        this.prop = prop;
    }

    public PropertyValue(String value, long langId, PropertyNode prop) {
        this.value = value;
        this.langId = langId;
        this.prop = prop;
    }

    public boolean booleanValue() {
        return value instanceof Boolean && (Boolean) value;
    }

    public int intValue() {
        return value instanceof Number ? ((Number) value).intValue() : 0;
    }
    
    public int intValue(int def) {
        return value instanceof Number ? ((Number) value).intValue() : def;
    }
    
    public int enumValue() {
        if (value instanceof ComboPropertyItem) {
            return Integer.parseInt(((ComboPropertyItem) value).id);
        } else if (value instanceof EnumValue) {
            return ((EnumValue) value).code;
        } else {
            return intValue();
        }
    }

    public double doubleValue() {
        return value instanceof Number ? ((Number) value).doubleValue() : 0.0;
    }

    public Object objectValue() {
        return value;
    }

    public Map objectsValue() {
        return (TreeMap) value;
    }

    public long getLangId() {
        return langId;
    }

    public Element elementValue() {
        return (Element) value;
    }

    public Color colorValue() {
        return (Color) value;
    }

    public Font fontValue() {
        return (Font) value;
    }

    public Border borderValue() {
        return (Border) value;
    }

    public byte[] getImageValue() {
        return value instanceof byte[] ? (byte[]) value : null;
    }

    public boolean isNull() {
        // FIXME избавиться от value.toString() - жрёт много ресурсов
        return value == null || value.toString().isEmpty();
    }

    public void save(Element e) {
        Element ch;
        switch (prop.getType()) {
        case INTEGER:
        case DOUBLE:
            e.setText(value == null ? "" : value.toString());
            break;
        case STRING:
        case VIEW_STRING:
            e.setText(value != null ? value.toString() : null);
            break;
        case MSTRING:
            ch = e.getChild("L" + langId);
            if (ch == null) {
                ch = new Element("L" + langId);
                e.addContent(ch);
            }
            ch.setText(value != null ? value.toString() : null);
            break;
        case BOOLEAN:
            e.setText(value != null && (Boolean) value ? "true" : "false");
            break;
        case KRNOBJECT:
            e.getChildren().clear();
            if (!isNull()) {
                try {
                    if (!prop.isArray()) {
                        ch = new Element("KrnObject");
                        if (value.toString().indexOf(",") > 0) {
                            StringBuilder vl = new StringBuilder();
                            int m = 0;
                            while (true) {
                                if (value.toString().indexOf(",", m) > 0) {
                                    String val = value.toString().substring(m, value.toString().indexOf(",", m));
                                    long[] ids = new long[] { Long.valueOf(val) };
                                    Map uids = Kernel.instance().getObjectUids(ids);
                                    vl.append(uids.get(ids[0]));
                                    vl.append((m = value.toString().indexOf(",", m) + 1) > 0 ? "," : "");
                                } else {
                                    String val = value.toString().substring(m);
                                    long[] ids = new long[] { Long.valueOf(val) };
                                    Map uids = Kernel.instance().getObjectUids(ids);
                                    vl.append(uids.get(ids[0]));
                                    break;
                                }
                            }
                            ch.setAttribute("id", vl.toString());
                        } else {
                            if (value != null && !"".equals(value.toString())) {
                                long[] ids = new long[] { Long.parseLong(value.toString()) };
                                Map uids = Kernel.instance().getObjectUids(ids);
                                if (!"Language".equals(className) || "language".equals(prop.getName())) {
                                    ch.setAttribute("id", uids.get(ids[0]).toString());
                                } else {
                                    ch.setAttribute("id", value.toString());
                                }
                            }
                        }
                        ch.setAttribute("class", className);
                        ch.setAttribute("title", title);
                        e.addContent(ch);
                    } else {
                        Map m = (TreeMap) value;
                        Set s = m.keySet();
                        Iterator it = s.iterator();
                        ch = new Element("KrnObjects");
                        ch.setAttribute("class", className);
                        while (it.hasNext()) {
                            Element child = new Element("KrnObject");
                            Long objId = (Long) it.next();
                            long[] ids = new long[] { objId };
                            Map uids = Kernel.instance().getObjectUids(ids);
                            if (!"Language".equals(className)) {
                                child.setAttribute("id", uids.get(ids[0]).toString());
                            } else {
                                child.setAttribute("id", String.valueOf(objId.intValue()));
                            }
                            if ("filters".equals(prop.getName())) {
                                child.setAttribute("title", (String) ((Pair) m.get(objId)).first);
                                child.setAttribute("expr", (String) ((Pair) m.get(objId)).second);
                            } else {
                                child.setAttribute("title", (String) m.get(objId));
                            }
                            ch.addContent(child);
                        }
                        e.addContent(ch);
                    }
                } catch (KrnException e1) {
                    e1.printStackTrace();
                }
            }
            break;
        case KRNOBJECT_ITEM:
            e.getChildren().clear();
            if (!isNull()) {
                try {
                    if (!prop.isArray()) {
                        KrnObjectItem fr = (KrnObjectItem) value;
                        ch = new Element("KrnObjectItem");
                        long[] ids = new long[] { fr.obj.id};
                        Map uids = Kernel.instance().getObjectUids(ids);
                        ch.setAttribute("id", uids.get(ids[0]).toString());
                        ch.setAttribute("title", fr.title);
                        e.addContent(ch);
                    } else {
                    	KrnObjectItem[] filters = (KrnObjectItem[]) value;
                        Element element = new Element("KrnObjectItems");
                        for (int i = 0; i < filters.length; i++) {
                            ch = new Element("Filter");
                            KrnObjectItem filter = filters[i];
                            long[] ids = new long[] { filter.obj.id };
                            Map uids = Kernel.instance().getObjectUids(ids);
                            ch.setAttribute("id", uids.get(ids[0]).toString());
                            ch.setAttribute("title", filter.title);
                            element.addContent(ch);
                        }
                        e.addContent(element);
                    }
                } catch (KrnException e1) {
                    e1.printStackTrace();
                }
            }
            break;
        case REF:
        case EXPR:
            e.setText(value != null ? value.toString() : null);
            break;
        case COLOR:
            e.setText(value == null ? "" : String.valueOf(((Color) value).getRGB()));
            break;
        case FONT:
            if (value != null) {
                Font f = (Font) value;
                String fontStyle = "PLAIN";
                switch (f.getStyle()) {
                case Font.ITALIC:
                    fontStyle = "ITALIC";
                    break;
                case Font.BOLD:
                    fontStyle = "BOLD";
                    break;
                case Font.BOLD + Font.ITALIC:
                    fontStyle = "BOLDITALIC";
                    break;
                }
                e.setText(new StringBuilder().append(f.getFamily()).append("-").append(fontStyle).append("-").append(f.getSize())
                        .toString());
            } else
                e.setText("");
            break;
        case BORDER:
            e.setText(value == null ? "" : Utils.getBorderToString((Border) value));
            break;
        case IMAGE:
            e.setText(value == null ? "" : Utils.getImageToString((byte[]) value));
            break;
        case STYLEDTEXT:
            if (value instanceof byte[]) {
                CDATA cdata = new CDATA(new String((byte[]) value));
                e.setText("");
                e.addContent(cdata);
            } else if (value instanceof Pair) {
                e.setText(((Pair) value).first.toString());
            }
            break;
        case REPORT:
            try {
                e.getChildren().clear();
                if (!isNull()) {
                    if (!prop.isArray()) {
                        ReportRecord rr = (ReportRecord) value;
                        ch = new Element("Report");
                        long[] ids = new long[] { rr.getObjId() };
                        Map uids = Kernel.instance().getObjectUids(ids);
                        ch.setAttribute("id", uids.get(ids[0]).toString());
                        ch.setAttribute("path", rr.getPath());
                        ids = new long[] { rr.getFilterId() };
                        uids = Kernel.instance().getObjectUids(ids);
                        ch.setAttribute("fid", uids.get(ids[0]).toString());
                        e.addContent(ch);
                    } else {
                        ReportRecord root = (ReportRecord) value;
                        Element element = new Element("Reports");
                        unloadReports(root, element);
                        e.addContent(element);
                    }
                }
            } catch (KrnException e1) {
                e1.printStackTrace();
            }
            break;
        case SEQUENCE:
            e.getChildren().clear();
            if (!isNull()) {
                try {
                    ch = new Element("Sequence");
                    long[] ids = new long[] { Integer.valueOf(value.toString()) };
                    Map uids = Kernel.instance().getObjectUids(ids);
                    ch.setAttribute("id", uids.get(ids[0]).toString());
                    ch.setAttribute("class", className);
                    ch.setAttribute("title", title);
                    e.addContent(ch);
                } catch (KrnException e1) {
                    e1.printStackTrace();
                }
            }
            break;
        case FILTER:
            e.getChildren().clear();
            if (!isNull()) {
                try {
                    if (!prop.isArray()) {
                        FilterRecord fr = (FilterRecord) value;
                        ch = new Element("Filter");
                        long[] ids = new long[] { fr.getObjId() };
                        Map uids = Kernel.instance().getObjectUids(ids);
                        ch.setAttribute("id", uids.get(ids[0]).toString());
                        ch.setAttribute("title", fr.getTitle());
                        e.addContent(ch);
                    } else {
                        FilterRecord[] filters = (FilterRecord[]) value;
                        Element element = new Element("Filters");
                        for (int i = 0; i < filters.length; i++) {
                            ch = new Element("Filter");
                            FilterRecord filter = filters[i];
                            long[] ids = new long[] { filter.getObjId() };
                            Map uids = Kernel.instance().getObjectUids(ids);
                            ch.setAttribute("id", uids.get(ids[0]).toString());
                            ch.setAttribute("title", filter.getTitle());
                            element.addContent(ch);
                        }
                        e.addContent(element);
                    }
                } catch (KrnException e1) {
                    e1.printStackTrace();
                }
            }
            break;
        case PMENUITEM:
            e.getChildren().clear();
            if (!isNull()) {
                if (!prop.isArray()) {
                    MenuItemRecord mr = (MenuItemRecord) value;
                    ch = new Element("MenuItem");
                    ch.setAttribute("title", mr.getTitle());
                    ch.setAttribute("expr", mr.getExpr());
                    e.addContent(ch);
                } else {
                    MenuItemRecord[] items = (MenuItemRecord[]) value;
                    Element element = new Element("MenuItems");
                    for (int i = 0; i < items.length; i++) {
                        Element mi = new Element("MenuItem");
                        MenuItemRecord item = items[i];
                        mi.setAttribute("title", item.getTitle());
                        mi.setAttribute("expr", item.getExpr());
                        element.addContent(mi);
                    }
                    e.addContent(element);
                }
            }
            break;
        case ENUM:
        case ENUM_TOOL_TIP:
            if (value instanceof ComboPropertyItem) {
                e.setText(((ComboPropertyItem) value).id);
            } else if (value instanceof EnumValue) {
                e.setText(String.valueOf(((EnumValue) value).code));
            } else if (value != null) {
                e.setText(value.toString());
            } else {
                e.setText("");
            }
            break;
        case COMPONENT:
            e.getChildren().clear();
            e.addContent((Element) value);
            break;
        case RSTRING:
            e.setText((value != null) ? ((Pair) value).first.toString() : null);
            break;
        case PROCESSES:
            e.removeContent();
            if (value instanceof String) { // формула
                CDATA cdata = new CDATA((String) value);
                e.addContent(cdata);
            } else if (value instanceof List) {
                List<ProcessRecord> prs = (List<ProcessRecord>) value;
                for (ProcessRecord pr : prs) {
                    Element prE = new Element("Process");
                    KrnObject prObj = pr.getKrnObject();
                    if (prObj != null) {
                        prE.setAttribute("uid", prObj.uid);
                        prE.setAttribute("id", String.valueOf(prObj.id));
                        prE.setAttribute("cls", String.valueOf(prObj.classId));
                        // Наименование процесса
                        Element nameE = new Element("name");
                        nameE.setText(pr.getName());
                        prE.addContent(nameE);
                    }
                    if (pr.getShortName() != null)
                        prE.setAttribute("title", pr.getShortName().first);
                    // Формула доступности
                    Expression expr = pr.getEnabledExpr();
                    if (expr != null) {
                        Element exprE = new Element("eexpr");
                        exprE.addContent(new CDATA(expr.text));
                        prE.addContent(exprE);
                    }
                    // Формула видимости
                    expr = pr.getVisibleExpr();
                    if (expr != null) {
                        Element exprE = new Element("vexpr");
                        exprE.addContent(new CDATA(expr.text));
                        prE.addContent(exprE);
                    }
                    // Формула действие
                    expr = pr.getActionExpr();
                    if (expr != null) {
                        Element exprE = new Element("aexpr");
                        exprE.addContent(new CDATA(expr.text));
                        prE.addContent(exprE);
                    }
                    // Иконка
                    BufferedImage icon = pr.getImage();
                    if (icon != null) {
                        try {
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            ImageIO.write(icon, "png", bos);
                            Element iconE = new Element("icon");
                            iconE.addContent(Utils.getImageToString(bos.toByteArray()));
                            prE.addContent(iconE);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    e.addContent(prE);
                }
            }
            break;
        case KRNOBJECT_ID:
            if (!isNull() && value != null) {
                e.setText(value.toString());
                ch = new Element("KrnObject");
                ch.setAttribute("id", value.toString());
                e.addContent(ch);
            }
            break;
        case HTML_TEXT:
            if (value instanceof byte[]) {
                CDATA cdata = new CDATA(new String((byte[]) value));
                e.setText("");
                e.addContent(cdata);
            } else if (value instanceof Pair) {
                e.setText(((Pair) value).first.toString());
            } else if (value instanceof Expression) {
                e.setText((value != null) ? ((Expression) value).text : null);
            } else if (value == null) {
                e.setText(null);
            }
            break;
        case GRADIENT_COLOR:
            e.setText(value == null ? "" : ((GradientColor) value).toString());
            break;
        }
    }

    public PropertyNode getProperty() {
        return prop;
    }

    public String stringValue() {
        if (title != null) {
            return title;
        } else if (value instanceof Font) {
            Font f = (Font) value;
            String fontStyle = "PLAIN";
            switch (f.getStyle()) {
            case Font.ITALIC:
                fontStyle = "ITALIC";
                break;
            case Font.BOLD:
                fontStyle = "BOLD";
                break;
            case Font.BOLD + Font.ITALIC:
                fontStyle = "BOLDITALIC";
                break;
            }
            return new StringBuilder().append(f.getFamily()).append("-").append(fontStyle).append("-").append(f.getSize())
                    .toString();
        } else if (value instanceof Color) {
            return String.valueOf(((Color) value).getRGB());
        } else if (value instanceof Border) {
            return Utils.getBorderToString((Border) value);
        } else if (value instanceof Boolean) {
            return ((Boolean) value) ? "true" : "false";
        } else if (value instanceof File) {
            return Utils.getImageToString((byte[]) value);
        } else if (value instanceof Document) {
            return Utils.getImageToString((byte[]) value);
        } else if (value instanceof ReportRecord) {
            ReportRecord r = (ReportRecord) value;
            return r.toString(langId);
        } else if (value instanceof ReportRecord[]) {
            ReportRecord[] records = (ReportRecord[]) value;
            StringBuffer str = new StringBuffer();
            if (records.length > 0) {
                str.append(records[0].getObjId()).append(":").append(records[0].getTitle(langId));
                for (int i = 1; i < records.length; i++) {
                    ReportRecord record = records[i];
                    str.append(",").append(record.getObjId()).append(":").append(record.getTitle(langId));
                }
            }
            return str.toString();
        } else if (value instanceof FilterRecord) {
            FilterRecord f = (FilterRecord) value;
            return f.getObjId() + ":" + f.getTitle();
        } else if (value instanceof FilterRecord[]) {
            FilterRecord[] records = (FilterRecord[]) value;
            StringBuffer str = new StringBuffer();
            if (records.length > 0) {
                str.append(records[0].getObjId()).append(":").append(records[0].getTitle());
                for (int i = 1; i < records.length; i++) {
                    FilterRecord record = records[i];
                    str.append(",").append(record.getObjId()).append(":").append(record.getTitle());
                }
            }
            return str.toString();
        } else if (value instanceof MenuItemRecord) {
            MenuItemRecord m = (MenuItemRecord) value;
            return String.valueOf(m.getTitle());
        } else if (value instanceof MenuItemRecord[]) {
            MenuItemRecord[] items = (MenuItemRecord[]) value;
            StringBuffer str = new StringBuffer();
            if (items.length > 0) {
                str.append(items[0].getTitle());
                for (int i = 1; i < items.length; i++) {
                    MenuItemRecord record = items[i];
                    str.append(",").append(record.getTitle());
                }
            }
            return str.toString();
        } else if (value instanceof Pair) {
            if (((Pair) value).second instanceof String) {
                return (String) ((Pair) value).second;
            } else if (((Pair) value).second instanceof byte[]) {
                return new String((byte[]) ((Pair) value).second);
            }
            return "";
        } else if (value != null) {
            return value.toString();
        } else {
            return "";
        }
    }

    public String filterString() {
        if (title != null) {
            return title;
        } else if (value instanceof FilterRecord) {
            FilterRecord f = (FilterRecord) value;
            return (f.getKrnObject()!=null ?f.getKrnObject().uid:"null") + " : " + f.getTitle();
        } else if (value instanceof FilterRecord[]) {
            FilterRecord[] records = (FilterRecord[]) value;
            StringBuffer str = new StringBuffer();
            if (records.length > 0) {
                str.append(records[0].getKrnObject().uid).append(" : ").append(records[0].getTitle());
                for (int i = 1; i < records.length; i++) {
                    FilterRecord record = records[i];
                    str.append(",").append(record.getKrnObject().uid).append(" : ").append(record.getTitle());
                }
            }
            return str.toString();
        } else {
            return "";
        }
    }

    public String toString() {
        return stringValue();
    }

    private boolean eq(int type, String find, int compareType) {
        switch (type) {
        case INTEGER:
        	try {
        		return this.intValue() == Integer.parseInt(find);
        	} catch (Throwable e) {
        		return false;
        	}
        case VIEW_STRING:
            return this.stringValue().indexOf(find) != -1;
        case STRING:
        case MSTRING:
        case REF:
        case EXPR:
            if (compareType == Constants.ALL) {
                return this.stringValue().equals(find);
            } else if (compareType == Constants.START_WHIH) {
                return this.stringValue().startsWith(find);
            } else if (compareType == Constants.CONTAINS) {
                return this.stringValue().indexOf(find) != -1;
            }
            break;
        case FILTER:
            return this.filterString().indexOf(find) != -1;
        case RSTRING:
            Pair pThis = this.resourceStringValue();
            if (compareType == Constants.START_WHIH) {
                return ((String) pThis.second).startsWith(find);
            } else if (compareType == Constants.ALL) {
                return pThis.second.equals(find);
            } else if (compareType == Constants.CONTAINS) {
                return ((String) pThis.second).indexOf(find) != -1;
            }
            break;
        case REPORT:
        	ReportRecord r = this.reportValue();
        	return reportEq(r, find, compareType);
        case KRNOBJECT:
            return this.uids != null && this.uids.indexOf(find) != -1;
        }
        return false;
    }

    private boolean reportEq(ReportRecord r, String find, int compareType) {
    	boolean res = false;
    	if (r != null) {
    		String t = r.getPath();
    		if (t != null) {
                if (compareType == Constants.START_WHIH) {
                    res = t.startsWith(find);
                } else if (compareType == Constants.ALL) {
                    res = t.equals(find);
                } else if (compareType == Constants.CONTAINS) {
                    res = t.indexOf(find) != -1;
                }
    		}
    		if (res) return true;
    		t = r.getFunc();
    		if (t != null) {
                if (compareType == Constants.START_WHIH) {
                    res = t.startsWith(find);
                } else if (compareType == Constants.ALL) {
                    res = t.equals(find);
                } else if (compareType == Constants.CONTAINS) {
                    res = t.indexOf(find) != -1;
                }
    		}
    		if (res) return true;
    		t = r.getVisibilityFunc();
    		if (t != null) {
                if (compareType == Constants.START_WHIH) {
                    res = t.startsWith(find);
                } else if (compareType == Constants.ALL) {
                    res = t.equals(find);
                } else if (compareType == Constants.CONTAINS) {
                    res = t.indexOf(find) != -1;
                }
    		}
    		if (res) return true;
    		if (r.getChildren() != null) {
	    		for (ReportRecord ch : r.getChildren()) {
	    			res = reportEq(ch, find, compareType);
	        		if (res) return true;
	    		}
    		}
    	}
    	return res;
    }
    
    public boolean equals(String find, int compareType) {
        if (find != null && find.length() > 0 && !isNull()) {
            int type = getProperty().getType();
            return eq(type, find, compareType);
        }
        return false;
    }

    public String getKrnObjectId() {
        return value.toString();
    }

    public String getKrnClassName() {
        return className;
    }

    public String getTitle() {
        return title;
    }

    public ReportRecord reportValue() {
        return (ReportRecord) value;
    }

    public ReportRecord[] reportValues() {
        return (ReportRecord[]) value;
    }

    public FilterRecord filterValue() {
        return (FilterRecord) value;
    }

    public FilterRecord[] filterValues() {
        return (FilterRecord[]) value;
    }

    public MenuItemRecord menuItemValue() {
        return (MenuItemRecord) value;
    }

    public MenuItemRecord[] menuItemsValues() {
        return (MenuItemRecord[]) value;
    }

    public Pair<String, Object> resourceStringValue() {
        return (Pair<String, Object>) value;
    }

    public void setResourceStringValue(String uid, Object value) {
        this.value = new Pair<String, Object>(uid, value);
    }

    public PropertyValue(Pair val, PropertyNode node) {
        prop = node;
        value = val;
    }

    public void setLangId(long langId) {
        this.langId = langId;
    }

    private long getIdByUid(String uid) throws KrnException {
        KrnObject obj = Kernel.instance().getCachedObjectByUid(uid);
        return (obj != null) ? obj.id : 0;
    }

    public List<ProcessRecord> processRecordsValue() {
        return (List<ProcessRecord>) value;
    }
}
