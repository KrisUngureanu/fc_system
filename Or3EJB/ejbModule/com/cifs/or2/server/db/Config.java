package com.cifs.or2.server.db;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.server.Session;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.util.*;

public class Config {
    private static Config inst;
    private long lastModified = -1;
    private Set<String> deepProcessingClasses;
    private Set<Long> skippingAttrs;
    private Map<Long, KrnAttribute> titles;

    public static synchronized Config instance(Session s) {
        if (inst == null) {
            inst = new Config(s);
        }
        return inst;
    }

    public synchronized boolean needsDeepProcessing(long classId, long typeId,
                                                    Session s) {
        load(s);
        return deepProcessingClasses.contains("" + classId + "." + typeId);
    }

    public synchronized boolean isSkippingAttribute(long attrId, Session s) {
        load(s);
        return skippingAttrs.contains(new Long(attrId));
    }

    public synchronized KrnAttribute getTitleAttrId(long classId) {
        return titles.get(new Long(classId));
    }

    private Config(Session s) {
        load(s);
    }

    private void load(Session s) {
        try {
            File f = new File("config.xml");
            long lm = f.lastModified();
            if (lastModified != lm) {
                lastModified = lm;
                deepProcessingClasses = new HashSet<String>();
                skippingAttrs = new HashSet<Long>();
                titles = new HashMap<Long, KrnAttribute>();
                if (f.exists()) {
                    SAXBuilder builder = new SAXBuilder();
                    Document doc = builder.build(f);
                    Element e = doc.getRootElement().getChild("obj2xml");
                    if (e != null) {
                        List elements = e.getChildren();
                        for (int i = 0; i < elements.size(); i++) {
                            processClassElement((Element) elements.get(i), s);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processClassElement(Element classElement, Session s)
            throws KrnException {
        String className = classElement.getAttributeValue("name");
        KrnClass clazz = s.getClassByName(className);
        Set<Long> clazzIds = new HashSet<Long>();
        clazzIds.add(new Long(clazz.id));
        boolean rec = (classElement.getAttributeValue("subclasses") != null);
        if (rec) {
            Set<KrnClass> subClasses = s.getSubClasses(clazz.id);
            for (Iterator<KrnClass> it = subClasses.iterator(); it.hasNext();) {
                clazzIds.add(it.next().id);
            }
        }

        Element e = classElement.getChild("includes");
        if (e != null) {
            List elements = e.getChildren();
            for (int i = 0; i < elements.size(); i++) {
                Element element = (Element) elements.get(i);
                String name = element.getAttributeValue("name");
                rec = (element.getAttributeValue("subclasses") != null);
                KrnClass cls = s.getClassByName(name);
                deepProcessingClasses.add("" + clazz.id + "." + cls.id);
                if (rec) {
                    Set<KrnClass> subClasses = s.getSubClasses(cls.id);
                    for (Iterator<KrnClass> it = subClasses.iterator(); it.hasNext();) {
                        long classId = it.next().id;
                        for (Iterator<Long> cIt = clazzIds.iterator(); cIt.hasNext();) {
                        	deepProcessingClasses.add("" + cIt.next() + "." + classId);
                        }
                    }
                }
            }
        }
        e = classElement.getChild("excludes");
        if (e != null) {
            List elements = e.getChildren();
            for (int i = 0; i < elements.size(); i++) {
                Element element = (Element) elements.get(i);
                String name = element.getAttributeValue("name");
                rec = (element.getAttributeValue("subclasses") != null);
                KrnClass cls = s.getClassByName(name);
                deepProcessingClasses.remove("" + clazz.id + "." + cls.id);
                if (rec) {
                    Set<KrnClass> subClasses = s.getSubClasses(cls.id);
                    for (Iterator<KrnClass> it = subClasses.iterator(); it.hasNext();) {
                        long classId = it.next().id;
                        for (Iterator<Long> cIt = clazzIds.iterator(); cIt.hasNext();) {
                            deepProcessingClasses.remove("" + cIt.next() + "." + classId);
                        }
                    }
                }
            }
        }
        e = classElement.getChild("skippingAttrs");
        if (e != null) {
            List elements = e.getChildren();
            for (int i = 0; i < elements.size(); i++) {
                Element element = (Element) elements.get(i);
                String name = element.getAttributeValue("name");
                KrnAttribute attr = s.getAttributeByName(clazz, name);
                skippingAttrs.add(new Long(attr.id));
            }
        }
        Element title = classElement.getChild("title");
        if (title != null) {
            String attrName = title.getAttributeValue("attrName");
            KrnAttribute attr = s.getAttributeByName(clazz, attrName);
            for (Iterator<Long> cIt = clazzIds.iterator(); cIt.hasNext();) {
                titles.put(cIt.next(), attr);
            }
        }
    }
}
