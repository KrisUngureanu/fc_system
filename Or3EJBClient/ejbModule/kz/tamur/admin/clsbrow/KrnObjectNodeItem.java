package kz.tamur.admin.clsbrow;


import static kz.tamur.util.CollectionTypes.COLLECTION_ARRAY;
import static kz.tamur.util.CollectionTypes.COLLECTION_NONE;
import static kz.tamur.util.CollectionTypes.COLLECTION_SET;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.DateValue;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.TimeValue;

public class KrnObjectNodeItem implements ObjectInspectable {
    private ObjectBrowser owner;
    private KrnObject item;
    private List<KrnAttribute> attrs;
    private long langId =  com.cifs.or2.client.Utils.getDataLangId();
    private Kernel krn=Kernel.instance();
    private ObjectPropertyInspector inspector;
    private KrnClass cls;
    private Map<Long,Vector> objArrayMap=new HashMap<Long,Vector>();
    private List<Long> attrModified=new ArrayList<Long>();
    private Map<Long,Object> attrMap=new HashMap<Long,Object>();
    private Map<Long,Map> langMap=new HashMap<Long,Map>();
    public KrnObjectNodeItem(KrnObject item,ObjectBrowser owner){
       this.item = item;
        this.owner=owner;
        this.cls=owner.getKrnClass();
        fillObject(item);
    }

    public ObjectProperty getObjectProperties() {
        ObjectProperty proot = new FolderObjectProperty(null, null);
        if(item!=null)
        try{
            cls=krn.getClass(item.classId);
            attrs= krn.getAttributes(cls);
            for(KrnAttribute attr:attrs){
                KrnClass cls_=krn.getClass(attr.typeClassId);
                if(cls_.id>=99){
                    new KrnObjectProperty(proot,attr);
                }else if(attr.typeClassId == Kernel.IC_BLOB){
                    new BlobObjectProperty(proot,attr);
                }else if(attr.typeClassId == Kernel.IC_MEMO){
                    new MemoObjectProperty(proot,attr);
                }else if(attr.typeClassId == Kernel.IC_DATE
                        || attr.typeClassId == Kernel.IC_TIME){
                    new DateTimeObjectProperty(proot,attr);
                }else if(attr.typeClassId == Kernel.IC_FLOAT
                        || attr.typeClassId == Kernel.IC_INTEGER
                        || attr.typeClassId == Kernel.IC_BOOL){
                    new NumberObjectProperty(proot,attr);
                }else{
                    new StringObjectProperty(proot,attr);
                }
            }
        }catch(KrnException ex){
            ex.printStackTrace();
        }

        if(item!=null){
            }
        return proot;
    }

    public Object getValue(ObjectProperty prop) {
        Object res="";
        if(item!=null && !(prop instanceof FolderObjectProperty)){
            res=attrMap.get(prop.getAttr().id);
        }
        return res;
    }

    public void setValue(ObjectProperty prop, Object value) {
        if(item!=null && !(prop instanceof FolderObjectProperty)){
            attrMap.put(prop.getAttr().id,value);
            if(!attrModified.contains(prop.getAttr().id))
                attrModified.add(prop.getAttr().id);
            owner.enableTransactionButtons(true);
        }
    }
    public void fillObject(KrnObject item) {
        Object res="";
        if(item!=null){
            try{
                attrMap.clear();
                attrModified.clear();
                objArrayMap.clear();
                langMap.clear();
                cls=krn.getClass(item.classId);
                attrs= krn.getAttributes(cls);
                // Загружаем одиночные простые атрибуты
                AttrRequestBuilder arb = new AttrRequestBuilder(cls, krn);
                for (KrnAttribute attr : attrs) {
                	if (attr.collectionType == COLLECTION_NONE && attr.rAttrId == 0 && attr.id > 2) {
                        long lid = (attr.isMultilingual) ? langId : 0;
                        arb.add(attr, lid);
                	}
                }
                List<Object[]> rows = krn.getObjects(new long[] {item.id}, arb.build(), ObjectBrowser.transId);
                if (rows.size() > 0) {
                	Object[] row = rows.get(0);
                	int i = 2;
                    for (KrnAttribute attr : attrs) {
                    	if (attr.collectionType == COLLECTION_NONE && attr.rAttrId == 0 && attr.id > 2) {
                    		Object value = row[i++];
                    		if (attr.typeClassId == Kernel.IC_BOOL && value instanceof Boolean)
                    			value = (Boolean)value ? 1 : 0;
                    		attrMap.put(attr.id, value);
                    	}
                    }
                }
                
                for(KrnAttribute attr:attrs){
                	if (attr.collectionType == COLLECTION_NONE && attr.rAttrId == 0)
                		continue;
                    if(attr.typeClassId>=99){
                        // Добавляем поле ссылку на объект
                        KrnObject[] values = krn.getObjects(item, attr, ObjectBrowser.transId);
                        if (attr.collectionType == COLLECTION_ARRAY
                                || attr.collectionType == COLLECTION_SET) {
                            res= values;
                        } else {
                            res= ((values.length == 0) ? null : values[0]);
                        }
                    }else if(attr.typeClassId==Kernel.IC_STRING){

                        long x = (attr.isMultilingual) ? langId : 0;
                        String[] values = krn.getStrings(item, attr, x, ObjectBrowser.transId);
                        if (attr.collectionType == COLLECTION_ARRAY
                                || attr.collectionType == COLLECTION_SET) {
                            res=values;
                        } else {
                            res = (values.length == 0) ? "" : values[0];
                        }
                    } else if ((attr.typeClassId == Kernel.IC_INTEGER && attr.id > 2)
                            || attr.typeClassId == Kernel.IC_BOOL
                    ) {
                        // Добавляем целое поле
                        long[] values = krn.getLongs(item, attr, ObjectBrowser.transId);
                        if (attr.collectionType == COLLECTION_ARRAY
                                || attr.collectionType == COLLECTION_SET) {
                            res=values;
                        } else {
                            res= (values.length == 0) ? "" : values[0];
                        }
                    } else if (attr.typeClassId == Kernel.IC_FLOAT) {
                        // Добавляем вещественное поле
                        double[] values = krn.getFloats(item, attr, ObjectBrowser.transId);
                        if (attr.collectionType == COLLECTION_ARRAY
                                || attr.collectionType == COLLECTION_SET) {
                            res=values;
                        } else {
                            res = (values.length == 0) ? 0 : values[0];
                        }
                    } else if (attr.typeClassId == Kernel.IC_DATE) {
                        // Добавляем поле даты
                        DateValue[] values = krn.getDateValues(new long[] {item.id}, attr, ObjectBrowser.transId);
                        if (attr.collectionType == COLLECTION_ARRAY
                                || attr.collectionType == COLLECTION_SET) {
                            res=values;
                        } else {
                            res = (values.length == 0) ? null : values[0];
                        }
                    } else if (attr.typeClassId == Kernel.IC_TIME) {
                        // Добавляем поле даты
                        TimeValue[] values = krn.getTimeValues(new long[] {item.id}, attr, ObjectBrowser.transId);
                        if (attr.collectionType == COLLECTION_ARRAY
                                || attr.collectionType == COLLECTION_SET) {
                            res= values;
                        } else {
                            res = (values.length == 0) ? null : values[0];
                        }
                    } else if (attr.typeClassId == Kernel.IC_BLOB) {
                        // Добавляем BLOB поле
                        long lang = attr.isMultilingual ? langId : 0;
                        if (attr.collectionType == COLLECTION_ARRAY
                                || attr.collectionType == COLLECTION_SET) {
                            res = krn.getBlobs(item.id, attr, lang, ObjectBrowser.transId);
                        } else {
                            res = krn.getBlob(item.id, attr, 0, lang, ObjectBrowser.transId);
                        }
                    } else if (attr.typeClassId == Kernel.IC_MEMO) {
                        // Добавляем MEMO поле
                        long lang = attr.isMultilingual ? langId : 0;
                        String[] values = krn.getStrings(item, attr, lang, ObjectBrowser.transId);
                        if (attr.collectionType == COLLECTION_ARRAY
                                || attr.collectionType == COLLECTION_SET) {
                            res= values;
                        } else {
                            res = (values.length > 0) ? values[0] : "";
                        }
                    }
                    attrMap.put(attr.id,res);
                }
            }catch(KrnException ex){
                ex.printStackTrace();
            }
        }
    }

	public void saveKrnObjectItem() throws KrnException {
		List<Long> executed = new ArrayList<Long>();
		for (Long attr_id : attrModified) {
			Object value = attrMap.get(attr_id);
			KrnAttribute attr = krn.getAttributeById(attr_id);
			if (attr.collectionType == 0) {
				if (value == null) {
					krn.deleteValue(item.id, attr.id, new int[] { 0 }, ObjectBrowser.transId);
				} else if (attr.typeClassId >= 99 && value instanceof KrnObject) {
					// Добавляем поле ссылку на объект
					krn.setObject(item.id, attr.id, 0, ((KrnObject) value).id, ObjectBrowser.transId, false);
				} else if (attr.typeClassId == Kernel.IC_STRING) {
					if (attr.isMultilingual) {
						Map<Long, String> langData = langMap.get(attr.id);
						if (langData != null && langData.size() > 0) {
							for (Long key : langData.keySet()) {
								krn.setString(item.id, attr.id, 0, key, langData.get(key), ObjectBrowser.transId);
							}
						}
					} else {
						krn.setString(item.id, attr.id, 0, 0, (String) value, ObjectBrowser.transId);
					}
				} else if ((attr.typeClassId == Kernel.IC_INTEGER && attr.id > 2) || attr.typeClassId == Kernel.IC_BOOL) {
					// Добавляем целое поле
					krn.setLong(item.id, attr.id, 0, (Long) value, ObjectBrowser.transId);
				} else if (attr.typeClassId == Kernel.IC_FLOAT) {
					// Добавляем вещественное поле
					krn.setFloat(item.id, attr.id, 0, (Double) value, ObjectBrowser.transId);
				} else if (attr.typeClassId == Kernel.IC_DATE) {
					// Добавляем поле даты
					krn.setDate(item.id, attr.id, 0, kz.tamur.util.Funcs.convertDate((com.cifs.or2.kernel.Date) value), ObjectBrowser.transId);
				} else if (attr.typeClassId == Kernel.IC_TIME) {
					// Добавляем поле времени
					krn.setTime(item.id, attr.id, 0, kz.tamur.util.Funcs.convertTime((com.cifs.or2.kernel.Time) value), ObjectBrowser.transId);
				} else if (attr.typeClassId == Kernel.IC_BLOB) {
					// Добавляем BLOB поле
					if (attr.isMultilingual) {
						Map<Long, byte[]> langData = langMap.get(attr.id);
						if (langData != null && langData.size() > 0) {
							for (Long key : langData.keySet()) {
								krn.setBlob(item.id, attr.id, 0, langData.get(key), key, ObjectBrowser.transId);
							}
						}
					} else {
						krn.setBlob(item.id, attr.id, 0, (byte[]) value, 0, ObjectBrowser.transId);
					}
				} else if (attr.typeClassId == Kernel.IC_MEMO) {
					// Добавляем MEMO поле
					if (attr.isMultilingual) {
						Map<Long, String> langData = langMap.get(attr.id);
						if (langData != null && langData.size() > 0) {
							for (Long key : langData.keySet()) {
								krn.setString(item.id, attr.id, 0, key, langData.get(key), ObjectBrowser.transId);
							}
						}
					} else {
						krn.setString(item.id, attr.id, 0, 0, (String) value, ObjectBrowser.transId);
					}
				}
			}
			executed.add(attr_id);
		}
		attrModified.removeAll(executed);
	}
	
    public void setObjectArray(KrnAttribute attr,Vector value){
        objArrayMap.put(attr.id,value);
    }
    public Map getObjectArray(){
        return objArrayMap;
    }
    public void setObjectInspector(ObjectPropertyInspector inspector){
        this.inspector=inspector;
    }
    public ObjectPropertyInspector getObjectInspector(){
        return inspector;
    }
    public String getTitle(){
        return cls.name;
    }
    public KrnObject getKrnObject(){
           return item;
    }
    public long getLangId(){
        return langId;
    }
    public void setLangData(long attrId,Map langData){
        if(langMap.containsKey(attrId)){
            langMap.get(attrId).putAll(langData);
        }else
            langMap.put(attrId,langData);
        if(!attrModified.contains(attrId))
            attrModified.add(attrId);
        owner.enableTransactionButtons(true);
    }
    public Map getLangData(long attrId){
        return langMap.get(attrId);
    }
}
