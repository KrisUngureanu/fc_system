package kz.tamur.comps;

import static kz.tamur.comps.models.Types.*;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import kz.tamur.comps.models.PropertyNode;
import kz.tamur.or3.client.props.Expression;
import kz.tamur.or3.client.props.inspector.ProcessRecord;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Pair;
import kz.tamur.web.common.webgui.WebTextField;
import kz.tamur.web.component.OrWebButton;
import kz.tamur.web.component.OrWebComboBox;
import kz.tamur.web.component.OrWebMemoField;
import kz.tamur.web.component.OrWebRichTextEditor;
import kz.tamur.web.component.OrWebTreeField;
import kz.tamur.web.component.WebFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + PropertyHelper.class.getName());
    private static final int DEFAULT_HEIGHT_VALUE = 20;
	private static final int BOOTSTRAP_HEIGHT_VALUE = 26;
	public static boolean genUUID = false;
	public static Map<Integer, Map<Long, Map<String, Map<String, PropertyValue>>>> values = new HashMap<Integer, Map<Long, Map<String, Map<String, PropertyValue>>>>();
	
    public static PropertyValue getPropertyValue(PropertyNode prop, Element xml, String compId, OrFrame frame, String uuid, String compClass) {
    	if (prop != null) {
			String path = prop.getFullPath();
			if (path != null) {
		    	long ifcId = ((WebFrame)frame).getInterfaceId();
		    	int configNumber = ((WebFrame)frame).getSession().getConfigNumber();
		    	
		    	Map<Long, Map<String, Map<String, PropertyValue>>> confMap = null;
		    	
		    	synchronized (values) {
		    		confMap = values.get(configNumber);
		    		if (confMap == null) {
		    			confMap = new HashMap<Long, Map<String, Map<String, PropertyValue>>>();
		    			values.put(configNumber, confMap);
		    		}
		    	}
		    	
		    	Map<String, Map<String, PropertyValue>> ifcMap = null;
		    	synchronized (confMap) {
		    		ifcMap = confMap.get(ifcId);
		    		if (ifcMap == null) {
		    			ifcMap = new HashMap<String, Map<String, PropertyValue>>();
		    			confMap.put(ifcId, ifcMap);
		    		}
				}
	    	
		    	Map<String, PropertyValue> compMap = null;
		    	synchronized (ifcMap) {
		    		compMap = ifcMap.get(compId);
		    		if (compMap == null) {
		    			compMap = new HashMap<String, PropertyValue>();
		    			ifcMap.put(compId, compMap);
		    		}
				}
	
		    	synchronized (compMap) {
		    		PropertyValue pv = compMap.get(path);
		    		if (pv == null) {
		    			pv = getPropertyValue(prop, xml, frame, uuid, compClass);
		    			if (pv != null) compMap.put(path, pv);
		    		}
		    		return pv;
				}
			}
    	}
		return new PropertyValue((Object)null, null);
    }
    
    public static void clearPropertyValues(int configNumber) {
    	synchronized (values) {
			if (values.containsKey(configNumber)) {
				values.remove(configNumber);
			}
		}
    }

    public static PropertyValue getPropertyValue(PropertyNode prop, Element xml, OrFrame frame, String uuid, String compClass) {
    	try {
	        Element e = getElement(prop, xml);
	        KrnObject lang = (frame != null) ? frame.getInterfaceLang() : null;
	        long langId = (lang != null) ? lang.id : 0;
	        PropertyValue pv = new PropertyValue(e, prop, langId, frame.getKernel());
	        Pair<String, Object> p;
	        if (!pv.isNull()) {
	            switch (prop.getType()) {
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
    	} catch (Exception e) {
        	log.error("Ошибка при инициализации свойства '" + prop.getFullName() + "' компонента '" + compClass + "'; uuid = " + uuid);
    		log.error(e, e);
    	}
    	return null;
    }

    public static void setPropertyValue(PropertyValue value, Element xml, OrFrame frame) {
        Element e = createElement(value.getProperty(), xml);
        int type = value.getProperty().getType();
        Pair<String, Object> p;
        String uid;
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
        value.save(e, frame.getKernel());
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
        String[] path = prop.getPath();
        if (path.length > 0) {
            Element e = xml;
            for (int i = 0; i < path.length && e != null; i++) {
                String p = path[i];
                Element t = e.getChild(p);
                // доработка условия, чтобы уменьшить количество вызовов String.equals()
                e = (t == null && p.length()==16 && "isVisibleNumRows".equals(p)) ? e.getChild("isVisible") : t;
            }
            return e;
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

    public static GridBagConstraints getConstraints(PropertyNode root,
                                                    Element xml, String id, OrFrame frame) {
        GridBagConstraints cs = new GridBagConstraints();
        PropertyNode pos = root.getChild("pos");
        OrGuiComponent c = ((WebFrame)frame).getSelectedComponent();
        String uuid = c != null ? c.getUUID() : null; 
        String className = c != null ? c.getClass().getName() : null; 

        cs.gridx = getPropertyValue(pos.getChild("x"), xml, id, frame, uuid, className).intValue();
        cs.gridy = getPropertyValue(pos.getChild("y"), xml, id, frame, uuid, className).intValue();
        cs.gridwidth = getPropertyValue(pos.getChild("width"), xml, id, frame, uuid, className).intValue();
        cs.gridheight = getPropertyValue(pos.getChild("height"), xml, id, frame, uuid, className).intValue();
        cs.weightx = getPropertyValue(pos.getChild("weightx"), xml, id, frame, uuid, className).doubleValue();
        cs.weighty = getPropertyValue(pos.getChild("weighty"), xml, id, frame, uuid, className).doubleValue();
        cs.fill = getPropertyValue(pos.getChild("fill"), xml, id, frame, uuid, className).intValue();
        PropertyValue pv = getPropertyValue(pos.getChild("anchor"), xml, id, frame, uuid, className);
        cs.anchor = (!pv.isNull()) ? pv.intValue() : GridBagConstraints.CENTER;
        PropertyNode insNode = pos.getChild("insets");
        int top = getPropertyValue(insNode.getChild("topInsets"), xml, id, frame, uuid, className).intValue();
        int left = getPropertyValue(insNode.getChild("leftInsets"), xml, id, frame, uuid, className).intValue(1);
        int bottom = getPropertyValue(insNode.getChild("bottomInsets"), xml, id, frame, uuid, className).intValue(1);
        int right = getPropertyValue(insNode.getChild("rightInsets"), xml, id, frame, uuid, className).intValue();
        cs.insets = new Insets(top, left, bottom, right);
        return cs;
    }

    public static Dimension getPreferredSize(OrGuiComponent c, String id, OrFrame frame) {
        PropertyNode ps = c.getProperties().getChild("pos").getChild("pref");
        Element xml = c.getXml();
        int width = getPropertyValue(ps.getChild("width"), xml, id, frame, c.getUUID(), c.getClass().getName()).intValue();
        int height = getPropertyValue(ps.getChild("height"), xml, id, frame, c.getUUID(), c.getClass().getName()).intValue();
        
        if (height == DEFAULT_HEIGHT_VALUE && (c instanceof WebTextField || c instanceof OrWebButton 
        									|| c instanceof OrWebComboBox || c instanceof OrWebTreeField)) height = BOOTSTRAP_HEIGHT_VALUE;
        
        if (width == 0 && height == 0) {
            return null;
        }
        return new Dimension(width, height);
    }

    public static Dimension getMinimumSize(OrGuiComponent c, String id, OrFrame frame) {
        PropertyNode ps = c.getProperties().getChild("pos").getChild("min");
        Element xml = c.getXml();
        int width = getPropertyValue(ps.getChild("width"), xml, id, frame, c.getUUID(), c.getClass().getName()).intValue();
        int height = getPropertyValue(ps.getChild("height"), xml, id, frame, c.getUUID(), c.getClass().getName()).intValue();
        if (width == 0 && height == 0) {
            return null;
        }
        return new Dimension(width, height);
    }

    public static Dimension getMaximumSize(OrGuiComponent c, String id, OrFrame frame) {
        PropertyNode ps = c.getProperties().getChild("pos").getChild("max");
        Element xml = c.getXml();
        int width = getPropertyValue(ps.getChild("width"), xml, id, frame, c.getUUID(), c.getClass().getName()).intValue();
        int height = getPropertyValue(ps.getChild("height"), xml, id, frame, c.getUUID(), c.getClass().getName()).intValue();
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
    public static String getUUID(OrGuiComponent c, OrFrame frame) {
        String uuid = null;
        try {
        	if(c == null 
        			|| c.getProperties()==null 
        			|| c.getProperties().getChild("UUID")==null)
        		return "empty";
            PropertyNode pnv = c.getProperties().getChild("UUID");
            PropertyValue pv = c.getPropertyValue(pnv);
            if (pv.isNull()) {
                Element xml = c.getXml();
                if (xml == null)
                    return "nullXML";
                // если свойство не найдено происходит генерация UUID и
                // запись его в свойства объекта
                uuid = UUID.randomUUID().toString();
                if (c.getClass() == OrWebMemoField.class) {
                	uuid = "m_" + uuid; 
                } else if(c.getClass() == OrWebRichTextEditor.class) {
                	uuid = "m_" + uuid; 
                } 
                setPropertyValue(new PropertyValue(uuid, pnv), xml, frame);
                genUUID = true;
                return uuid;
            } else {
                return pv.stringValue();
            }
        } catch (Exception e) {
            log.error("Ошибка получения UUID для: " + c.getClass().getName() + " " + c.getVarName());
            log.error(e, e);
            return null;
        }
    }

    /**
     * Получение уникального идентификатора компонента UUID
     * 
     * @param c
     *            компонент UUID которого необходимо узнать
     * @return UUID компонента
     */
    public static String getUUIDNoGen(OrGuiComponent c, OrFrame frame) {
        String uuid = null;
        PropertyNode props = c.getProperties();
        if (props != null) {
            PropertyNode pn = props.getChild("UUID");
            Element xml = c.getXml();
            if (pn != null) {
                uuid = getPropertyValue(pn, xml, frame, c.getUUID(), c.getClass().getName()).stringValue();
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
