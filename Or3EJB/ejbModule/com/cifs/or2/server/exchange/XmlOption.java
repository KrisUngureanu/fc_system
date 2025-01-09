package com.cifs.or2.server.exchange;

import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 11.12.2003
 * Time: 21:20:05
 * To change this template use Options | File Templates.
 */
public class XmlOption extends XmlObject {
    public static final String REF = "Ref";
    public static final String TAG = "Tag";
    public static final String ARRAY = "Array";

    public String getName() {
        return "XmlOption";
    }

    public String getRef() {
        return (String)getFirstAttribute(REF);
    }

    public List getRefs() {
        return getAttribute(REF);
    }

    public String getTag() {
        return (String)getFirstAttribute(TAG);
    }

    private XmlOption() {
        super("XmlOption", null);
    }

    public static XmlFactory getFactory() {
        return new XmlFactory() {
            public XmlObject createObject(String name, String id) {
                return new XmlOption();
            }
        };
    }
}
