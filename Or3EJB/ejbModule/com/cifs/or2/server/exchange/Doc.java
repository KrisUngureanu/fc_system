package com.cifs.or2.server.exchange;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 11.12.2003
 * Time: 21:21:04
 * To change this template use Options | File Templates.
 */
public class Doc extends XmlObject {
    public static final String STATE = "State";
    public static final String TID = "Transaction";
    public static final String GUID = "Guid";
    public static final String FIRST_GUID = "FirstGuid";
    public static final String INQUIRY_GUID = "InquiryGuid";
    public static final String ERROR_MSG = "ErrorMessage";

    public String getName() {
        return "Doc";
    }

    public Doc(String id) {
        super("Doc", id);
    }

    public int getIntId() {
        return Integer.parseInt(getId());
    }

    public int getTransactionId() {
        return Integer.parseInt((String)getFirstAttribute(TID));
    }

    public boolean equals(Object obj) {
        if (obj instanceof Doc) {
            return (getIntId() == ((Doc)obj).getIntId());
        }
        return false;
    }

    public static XmlFactory getFactory() {
        return new XmlFactory() {
            public XmlObject createObject(String name, String id) {
                return new Doc(id);
            }
        };
    }
}
