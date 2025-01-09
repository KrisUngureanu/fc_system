package kz.tamur.or3ee.server.kit;

import java.util.HashMap;
import java.util.Map;

import kz.tamur.ods.AttrRequest;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.db.Database;

public class AttrRequestBuilder {

    private AttrRequest root;
    private final KrnClass cls;
    private Database db;
    private Map<String, Integer> indexes = new HashMap<String, Integer>();

    public AttrRequestBuilder(KrnClass cls, Database db) {
        this.cls = cls;
        this.db = db;

        root = new AttrRequest(null);
    }

    public AttrRequestBuilder(String clsName, Database db) {
    	this(db.getClassByName(clsName), db);
    }

    public AttrRequestBuilder(KrnClass cls, Session session) {
    	this(cls, session.getDriver().getDatabase());
    }

    public AttrRequestBuilder(String clsName, Session session) {
    	this(session.getDriver().getDatabase().getClassByName(clsName), session.getDriver().getDatabase());
    }

    public AttrRequestBuilder add(String attrName) {
        return add(attrName, 0);
    }

    public AttrRequestBuilder add(String attrName, long langId) {
        KrnAttribute attr = db.getAttributeByName(cls.id, attrName);
        if (attr != null) {
            AttrRequest ar = new AttrRequest(root);
            ar.attrId = attr.id;
            ar.langId = langId;
            indexes.put(langId > 0 ? attrName + langId : attrName, indexes.size() + 2);
        }
        return this;
    }

    public AttrRequestBuilder add(String attrName, AttrRequestBuilder builder) {
        root.add(builder.root);
        builder.root.attrId = db.getAttributeByName(cls.id, attrName).id;
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

    public AttrRequest build() {
        return root;
    }
    
    public KrnClass getCls() {
    	return cls;
    }

    public KrnObject getObject(Object[] row) {
        return (KrnObject) row[0];
    }

    public long getTransactionId(Object[] row) {
        return (Long) row[1];
    }

    public Object getValue(String name, Object[] row) {
        Integer index = indexes.get(name);
        return index != null ? row[index] : null;
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
}
