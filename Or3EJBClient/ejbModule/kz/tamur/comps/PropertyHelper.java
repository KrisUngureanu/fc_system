package kz.tamur.comps;

import static kz.tamur.comps.models.Types.*;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;
import java.util.UUID;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.or3.client.props.inspector.ProcessRecord;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;

import org.jdom.Element;

import com.cifs.or2.kernel.KrnObject;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.04.2004
 * Time: 14:37:23
 * To change this template use File | Settings | File Templates.
 */
public class PropertyHelper {
    public static boolean genUUID = false;
    public static boolean forseGenUUID = false;

    public static PropertyValue getPropertyValue(PropertyNode prop, Element xml, OrFrame frame) {
        Element e = getElement(prop, xml);
        KrnObject lang = (frame != null) ? frame.getInterfaceLang() : null;
        long langId = (lang != null) ? lang.id : 0;
        PropertyValue pv = new PropertyValue(e, prop, langId);
        if (!pv.isNull()) {
            int type = prop.getType();
            Pair<String, Object> p;
            switch (type) {
            case STYLEDTEXT:
                p = pv.resourceStringValue();
                if (p.first != null) {
                    pv.setResourceStringValue(p.first, frame.getBytes((String) p.first));
                }
                break;
            case RSTRING:
                p = pv.resourceStringValue();
                pv.setResourceStringValue(p.first, frame.getString((String) p.first));
                break;
            case PROCESSES:
                if (pv.objectValue() instanceof List) {
                    List<ProcessRecord> prs = (List<ProcessRecord>) pv.objectValue();
                    for (ProcessRecord pr : prs) {
                        Pair<String, Object> sn = pr.getShortName();
                        if (sn != null)
                            pr.setShortName(frame.getString((String) sn.first));
                    }
                }
                break;
            case HTML_TEXT:
                if (!(pv.objectValue() instanceof Expression)) {
                    p = pv.resourceStringValue();
                    if (p.first != null) {
                        pv.setResourceStringValue(p.first, frame.getBytes((String) p.first));
                    }
                }
                break;

            }
        }
        return pv;
    }

    public static void setPropertyValue(PropertyValue value, Element xml, OrFrame frame) {
        Element e = createElement(value.getProperty(), xml);
        int type = value.getProperty().getType();
        String uid;
        Pair<String, Object> p;
        switch (type) {
        case RSTRING:
            p = value.resourceStringValue();
            uid = p.first;
            if (uid == null || uid.isEmpty()) {
                uid = frame.getNextUid();
                value.setResourceStringValue(uid, p.second);
            }
            frame.setString(uid, (String) p.second);
            break;
        case MSTRING:
            if (frame != null) {
                value.setLangId(frame.getInterfaceLang().id);
            }
            break;
        case STYLEDTEXT:
            if (!value.isNull()) {
                p = value.resourceStringValue();
                uid = p.first;
                if (uid == null || uid.isEmpty()) {
                    uid = frame.getNextUid();
                    value.setResourceStringValue(uid, p.second);
                }
                frame.setBytes(uid, (byte[]) p.second);
            }
            break;
        case HTML_TEXT:
            if (!value.isNull() && !(value.objectValue() instanceof Expression)) {
                p = value.resourceStringValue();
                uid = p.first;
                Object data = p.second;
                if (data instanceof byte[]) {
                    if (uid == null || uid.isEmpty()) {
                        uid = frame.getNextUid();
                        value.setResourceStringValue(uid, data);
                    }
                    frame.setBytes(uid, (byte[]) data);
                }
            }
            break;
        case PROCESSES:
            if (value.objectValue() instanceof List) {
                List<ProcessRecord> prs = (List<ProcessRecord>) value.objectValue();
                for (ProcessRecord pr : prs) {
                    p = pr.getShortName();
                    if (p != null) {
                        if (p.first == null) {
                            p = new Pair<String, Object>(frame.getNextUid(), p.second);
                            pr.setShortName(p);
                        }
                        frame.setString(p.first, (String) p.second);
                    }
                }
            }
            break;
        }
        value.save(e);
    }
    
    public static void addProperty(PropertyValue value, Element xml) {
        Element e = createElement(value.getProperty(), xml);
        e.addContent(value.elementValue());
    }

    public static void insertProperty(PropertyValue value,int index, Element xml) {
        Element e = createElement(value.getProperty(), xml);
        e.addContent(index,value.elementValue());
    }

    public static void removeProperty(PropertyValue value, Element xml) {
        Element e = getElement(value.getProperty(), xml);
        e.removeContent(value.elementValue());
    }

    private static Element getElement(PropertyNode prop, Element xml) {
    	if (prop != null) {
	        String[] path = prop.getPath();
	        if (path.length > 0) {
	            Element e = xml;
	            for (int i = 0; i < path.length && e != null; i++) {
	                String p = path[i];
	                Element t = e.getChild(p);
	                e = t == null && "isVisibleNumRows".equals(p) ? e.getChild("isVisible") : t;
	            }
	            return e;
	        }
    	}
        return null;
    }

    private static Element createElement(PropertyNode prop, Element xml) {
        String[] path = prop.getPath();
        Element e = xml;
        for (int i = 0; i < path.length; i++) {
            Element child = e.getChild(path[i]);
            if (child == null) {
                child = new Element(path[i]);
                e.addContent(child);
            }
            e = child;
        }
        return e;
    }

    public static GridBagConstraints getConstraints(PropertyNode root, Element xml) {
        GridBagConstraints cs = new GridBagConstraints();
        PropertyNode pos = root.getChild("pos");
        cs.gridx = getPropertyValue(pos.getChild("x"), xml, null).intValue();
        cs.gridy = getPropertyValue(pos.getChild("y"), xml, null).intValue();
        cs.gridwidth = getPropertyValue(pos.getChild("width"), xml, null).intValue();
        cs.gridheight = getPropertyValue(pos.getChild("height"), xml, null).intValue();
        cs.weightx = getPropertyValue(pos.getChild("weightx"), xml, null).doubleValue();
        cs.weighty = getPropertyValue(pos.getChild("weighty"), xml, null).doubleValue();
        cs.fill = getPropertyValue(pos.getChild("fill"), xml, null).intValue();
        PropertyValue pv = getPropertyValue(pos.getChild("anchor"), xml, null);
        cs.anchor = (!pv.isNull()) ? pv.intValue() : GridBagConstraints.CENTER;
        PropertyNode insNode = pos.getChild("insets");
        int top = getPropertyValue(insNode.getChild("topInsets"), xml, null).intValue();
        int left = getPropertyValue(insNode.getChild("leftInsets"), xml, null).intValue(1);
        int bottom = getPropertyValue(insNode.getChild("bottomInsets"), xml, null).intValue(1);
        int right = getPropertyValue(insNode.getChild("rightInsets"), xml, null).intValue();
        cs.insets = new Insets(top, left, bottom, right);
        return cs;
    }

    public static Dimension getPreferredSize(OrGuiComponent c) {
        PropertyNode ps = c.getProperties().getChild("pos").getChild("pref");
        Element xml = c.getXml();
        int width = getPropertyValue(ps.getChild("width"), xml, null).intValue();
        int height = getPropertyValue(ps.getChild("height"), xml, null).intValue();
        if (width == 0 && height == 0) {
            return null;
        }
        return new Dimension(width, height);
    }

    public static Dimension getMinimumSize(OrGuiComponent c) {
        PropertyNode ps = c.getProperties().getChild("pos").getChild("min");
        Element xml = c.getXml();
        int width = getPropertyValue(ps.getChild("width"), xml, null).intValue();
        int height = getPropertyValue(ps.getChild("height"), xml, null).intValue();
        if (width == 0 && height == 0) {
            return null;
        }
        return new Dimension(width, height);
    }

    public static Dimension getMaximumSize(OrGuiComponent c) {
        PropertyNode ps = c.getProperties().getChild("pos").getChild("max");
        Element xml = c.getXml();
        int width = getPropertyValue(ps.getChild("width"), xml, null).intValue();
        int height = getPropertyValue(ps.getChild("height"), xml, null).intValue();
        if (width == 0 && height == 0) {
            return null;
        }
        return new Dimension(width, height);
    }
    /**
     * Получение уникального идентификатора компонента UUID Если идентификатора
     * не существует, то ему генерируется новый UUID
     * 
     * @param c
     *            компонент UUID которого необходимо узнать
     * @return UUID компонента
     */
    public static String getUUID(OrGuiComponent c) {
    	return getUUID(c, false);
    }
    
    public static String getUUID(OrGuiComponent c, boolean isEditor) {
        String uuid = null;
        PropertyNode pnv = c.getProperties().getChild("UUID");
        if (c != null && pnv != null) {
            PropertyValue pv = c.getPropertyValue(pnv);
            if (!isEditor && (forseGenUUID || pv.isNull())) {
                Element xml = c.getXml();
                if (xml == null)
                    return "nullXML";
                // если свойство не найдено происходит генерация UUID и
                // запись его в свойства объекта
                uuid = UUID.randomUUID().toString();
                if (c instanceof OrMemoField || c instanceof OrRichTextEditor) {
                	uuid = "m_" + uuid; 
                }
                setPropertyValue(new PropertyValue(uuid, pnv), xml, null);
                genUUID = true;
                return uuid;
            } else {
                return Funcs.normalizeInput(pv.stringValue());
            }

        } else {
            return "empty";
        }
    }

    /**
     * Получение уникального идентификатора компонента UUID
     * 
     * @param c
     *            компонент UUID которого необходимо узнать
     * @return UUID компонента
     */
    public static String getUUIDNoGen(OrGuiComponent c) {
        String uuid = null;
        PropertyNode props = c.getProperties();
        if (props != null) {
            PropertyNode pn = props.getChild("UUID");
            Element xml = c.getXml();
            if (pn != null) {
                uuid = getPropertyValue(pn, xml, null).stringValue();
            }
        }
        return uuid;
    }
/*
    public static byte[] getDescription(OrGuiComponent c) {
        PropertyNode props = c.getProperties();
        if (props != null) {
            PropertyNode pn = props.getChild("description");
            Element xml = c.getXml();
            if (pn != null) {
                PropertyValue pv = getPropertyValue(pn, xml, null);
                if (pv != null && !pv.isNull()) {
                    Pair p = pv.resourceStringValue();
                    return (byte[])p.second;
                }
            }
        }
        return null;
    }
*/

}
