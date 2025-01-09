package kz.tamur.server.plugins.rn;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;
import com.cifs.or2.util.Funcs;

import java.util.*;

public class KATO implements SrvPlugin {
    private Session s;

    private KrnClass nodeCls;
    private KrnClass typeCls;

    private KrnAttribute parentAttr;
    
    private KrnAttribute typeAttr;
    
    // Республика
    private long republicId;
    // Город республиканского значения
    private long repCityId;
    // Область
    private long oblId;
    // Город областного значения
    private long oblCityId;
    // Район
    private long regionId;
    // Район в городе
    private long districtId;

    // Округи поселковый и сельский и аульный
    private long pOkrugId;
    private long sOkrugId;
    private long aOkrugId;

    public Session getSession() {
        return s;
    }

    public void setSession(Session session) {
        s = session;
        if (nodeCls == null) {
            try {
                nodeCls = s.getClassByName("АТЕ");
                typeCls = s.getClassByName("Тип элемента АТЕ");

                parentAttr = s.getAttributeByName(nodeCls, "родитель");
                typeAttr = s.getAttributeByName(nodeCls, "тип элемента АТЕ");

                KrnObject[] types = s.getClassObjects(typeCls, new long[0], 0);
                KrnAttribute arCodeAttr = s.getAttributeByName(typeCls, "код АР");
                long ids[] = Funcs.makeObjectIdArray(types);
                
                com.cifs.or2.kernel.StringValue[] strs = s.getStringValues(ids, arCodeAttr.id, 0, false, 0);
                for (StringValue sv : strs) {
                	if ("10".equals(sv.value)) {
                		republicId = sv.objectId;
                	} else if ("5".equals(sv.value)) {
                		regionId = sv.objectId;
                	} else if ("8".equals(sv.value)) {
                		districtId = sv.objectId;
                	} else if ("9".equals(sv.value)) {
                		repCityId = sv.objectId;
                	} else if ("6".equals(sv.value)) {
                		oblCityId = sv.objectId;
                	} else if ("7".equals(sv.value)) {
                		oblId = sv.objectId;
                	} else if ("52".equals(sv.value)) {
                		pOkrugId = sv.objectId;
                	} else if ("3".equals(sv.value)) {
                		sOkrugId = sv.objectId;
                	} else if ("59".equals(sv.value)) {
                		aOkrugId = sv.objectId;
                	}
                }
                
            } catch (Exception e) {
                System.out.println("RN KATO Plugin couldn't be used!");
            }
        }
    }

    public Map<String, Object> getAddressMap(KrnObject obj) {
        try {
            KrnObject parent = null;
            Map<String, Object> res = new HashMap<String, Object>();

            String rus = "";
            //String kaz = "";
            
            while ((parent = getParent(obj)) != null) {
                KrnObject[] types = s.getObjects(obj.id, typeAttr.id, new long[0], s.getContext().trId);
                
                if (types != null && types.length > 0) {
                	long typeId = types[0].id;
                    	
                	if (typeId == republicId) {
                		res.put("country", obj);
                	} else if (typeId == repCityId) {
                		if (res.get("city") == null) {
                			res.put("city", obj);
                			rus = (rus.length() > 0) ? "#{city}, " + rus : "#{city}";
                		}
                	} else if (typeId == oblId) {
                		res.put("obl", obj);
            			rus = (rus.length() > 0) ? "#{obl}, " + rus : "#{obl}";
                	} else if (typeId == oblCityId) {
                		if (res.get("city") == null) {
                			res.put("city", obj);
                			rus = (rus.length() > 0) ? "#{city}, " + rus : "#{city}";
                		}
                	} else if (typeId == regionId) {
                		res.put("region", obj);
                		rus = (rus.length() > 0) ? "#{region}, " + rus : "#{region}";
                	} else if (typeId == districtId) {
                		res.put("district", obj);
                		rus = "#{district}";
                	} else if (typeId != pOkrugId && typeId != sOkrugId && typeId != aOkrugId) {
                		res.put("city", obj);
                		rus = (rus.length() > 0) ? "#{city}, " + rus : "#{city}";
                	}
                }
                obj = parent;
            }
            res.put("templateRU", rus);
            
            return res;
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    private KrnObject getParent(KrnObject obj) {
        try {
            KrnObject[] parents = s.getObjects(obj.id, parentAttr.id, new long[0], 0);
            if (parents.length > 0) {
                return parents[0];
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }
}
