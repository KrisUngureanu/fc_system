package com.cifs.or2.client.util;

import java.util.HashMap;
import java.util.Map;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.Date;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.Time;

import kz.tamur.ods.AttrRequest;

public class AttrRequestBuilder {

    private AttrRequest root;
    private final KrnClass cls;
    private final Kernel krn;
    private Map<String, Integer> indexes = new HashMap<String, Integer>();

    public AttrRequestBuilder(KrnClass cls, Kernel krn) {
        this.cls = cls;
        this.krn = krn;

        root = new AttrRequest(null);
    }

    public AttrRequestBuilder add(String attrName) throws KrnException {
        return add(attrName, 0);
    }

    public AttrRequestBuilder add(String attrName, long langId) throws KrnException {
        KrnAttribute attr = krn.getAttributeByName(cls, attrName);
        if (attr != null) {
            AttrRequest ar = new AttrRequest(root);
            ar.attrId = attr.id;
            ar.langId = langId;
            indexes.put(langId > 0 ? attrName + langId : attrName, indexes.size() + 2);
        }
        return this;
    }

    public AttrRequestBuilder add(KrnAttribute attr) {
        return add(attr, 0);
    }

    public AttrRequestBuilder add(KrnAttribute attr, long langId) {
        AttrRequest ar = new AttrRequest(root);
        ar.attrId = attr.id;
        ar.langId = langId;
        indexes.put(langId > 0 ? attr.name + langId : attr.name, indexes.size() + 2);
        return this;
    }

    public AttrRequestBuilder add(String attrName, AttrRequestBuilder builder) throws KrnException {
        root.add(builder.root);
        builder.root.attrId = krn.getAttributeByName(cls, attrName).id;
        indexes.put(attrName, indexes.size() + 2);
        int subSize;
        int index = 0;
        for (String key : builder.indexes.keySet()) {
            int oldIndex = builder.indexes.get(key);
            subSize = builder.indexes.size();
            indexes.put(attrName + "." + key, indexes.size() + (subSize - (subSize - oldIndex) - index));
            index++;
        }
        return this;
    }

    public AttrRequestBuilder add(KrnAttribute attr, AttrRequestBuilder builder) {
        root.add(builder.root);
        builder.root.attrId = attr.id;
        indexes.put(attr.name, indexes.size() + 2);
        int subSize;
        int index = 0;
        for (String key : builder.indexes.keySet()) {
            int oldIndex = builder.indexes.get(key);
            subSize = builder.indexes.size();
            indexes.put(attr.name + "." + key, indexes.size() + (subSize - (subSize - oldIndex) - index));
            index++;
        }
        return this;
    }

    public AttrRequest build() {
        return root;
    }

    public KrnObject getObject(Object[] row) {
        return (KrnObject) row[0];
    }

    public long getTransactionId(Object[] row) {
        return (Long) row[1];
    }

    public Object getValue(String name, Object[] row) {
        Integer index = indexes.get(name);
        return index != null && index < row.length ? row[index] : null;
    }

    public boolean getBooleanValue(String name, Object[] row) {
        Boolean value = (Boolean) getValue(name, row);
        return value != null ? value.booleanValue() : false;
    }

    public boolean getBooleanValue(String name, Object[] row, boolean defValue) {
        Boolean value = (Boolean) getValue(name, row);
        return value != null ? value.booleanValue() : defValue;
    }

    public long getLongValue(String name, Object[] row) {
        return getLongValue(name, row, 0);
    }

    public long getLongValue(String name, Object[] row, long defValue) {
        Number value = (Number) getValue(name, row);
        return value != null ? value.longValue() : defValue;
    }

    public int getIntValue(String name, Object[] row) {
        Number value = (Number) getValue(name, row);
        return value != null ? value.intValue() : 0;
    }

    public String getStringValue(String name, Object[] row) {
        return (String) getValue(name, row);
    }
    
    public String getStringValue(String name, long langId, Object[] row) {
        return (String) getValue(name + langId, row);
    }

    public KrnObject getObjectValue(String name, Object[] row) {
        return (KrnObject) getValue(name, row);
    }

    public Date getDateValue(String name, Object[] row) {
        return (Date) getValue(name, row);
    }

    public Time getTimeValue(String name, Object[] row) {
        return (Time) getValue(name, row);
    }
}
