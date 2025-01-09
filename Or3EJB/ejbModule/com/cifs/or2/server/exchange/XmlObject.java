package com.cifs.or2.server.exchange;

import com.cifs.or2.util.MultiMap;
import com.cifs.or2.util.Funcs;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import static kz.tamur.comps.Constants.EOL;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.12.2003
 * Time: 18:35:13
 * To change this template use Options | File Templates.
 */
public class XmlObject {
    private String id;
    private String name;
    private MultiMap<String, Object> attributes = new MultiMap<String, Object>(ArrayList.class);

    public XmlObject(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void save(StringBuffer storage) {
        storage.append("<" + name + ">" + EOL);
        for (Iterator<String> nameIt = attributes.keySet().iterator();
                nameIt.hasNext();) {
            String name = nameIt.next();
            storage.append("<" + name + ">");
            List<Object> values = attributes.get(name);
            for (int i = 0; i < values.size(); i++) {
                Object value = values.get(i);
                if (value instanceof XmlObject) {
                    storage.append(EOL);
                    XmlObject xo = (XmlObject)value;
                    if (xo.getId() == null) {
                        ((XmlObject)value).save(storage);
                    } else {
                        storage.append("<" + xo.getName() + " id=\""
                                       + xo.getId() + "\" />");
                    }
                    storage.append(EOL);
                } else {
                    storage.append(Funcs.xmlQuote(value.toString()));
                }
            }
            storage.append("</" + name + ">" + EOL);
        }
        storage.append("</" + name + ">");
    }

    public List<Object> getAttribute(final String name) {
        List<Object> res = attributes.get(name);
        return (res != null) ? res : Collections.emptyList();
    }

    public Object getFirstAttribute(final String name) {
        List<Object> res = getAttribute(name);
        if (res.size() > 0) {
            return res.get(0);
        }
        return null;
    }

    public Object getLastAttribute(final String name) {
        List<Object> res = getAttribute(name);
        if (res.size() > 0) {
            return res.get(res.size() - 1);
        }
        return null;
    }

    void setAttribute(final String name, final Object value, int index) {
        List<Object> attrs = attributes.get(name);
        if (attrs == null) {
            attributes.put(name, value);
        } else if (index == -1 || index >= attrs.size()) {
            attrs.add(value);
        } else {
            attrs.set(index, value);
        }
    }

    void delAttribute(final String name) {
        attributes.remove(name);
    }
}

