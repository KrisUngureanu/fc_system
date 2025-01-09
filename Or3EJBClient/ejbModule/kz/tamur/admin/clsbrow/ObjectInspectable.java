package kz.tamur.admin.clsbrow;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

import java.util.Map;
import java.util.Vector;

public interface ObjectInspectable {

	ObjectProperty getObjectProperties();

	Object getValue(ObjectProperty prop);
	void setValue(ObjectProperty prop, Object value);
    void setObjectInspector(ObjectPropertyInspector inspector);
    void fillObject(KrnObject obj);
    void saveKrnObjectItem() throws KrnException;
    ObjectPropertyInspector getObjectInspector();
    KrnObject getKrnObject();
    String getTitle();
    void setObjectArray(KrnAttribute attr, Vector value);
    Map getObjectArray();
    long getLangId();
    void setLangData(long attrId,Map langData);
    Map getLangData(long attrId);
}
