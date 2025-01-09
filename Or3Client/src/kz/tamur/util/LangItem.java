package kz.tamur.util;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.util.Funcs;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.LongValue;

import javax.swing.*;
import java.util.*;

import kz.tamur.comps.CommonUtil;
import kz.tamur.comps.Constants;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 05.05.2004
 * Time: 16:36:48
 * To change this template use File | Settings | File Templates.
 */
public class LangItem {
    private static Map<Long, LangItem> items;

    public final KrnObject obj;
    public final String name;
    public final String code;
    public final ImageIcon icon;

    public static LangItem getById(long id) {
    	if (items != null)
    		return items.get(new Long(id));
    	else 
    		return null;
    }

    public static synchronized void initialize(Kernel kernel) {
        reload(kernel);
    }

    public static LangItem getByCode(String code) {
        //code = "RU", "KZ" e.t.c.
        for (Long objId : items.keySet()) {
            LangItem li = items.get(objId);
            if (code.equals(li.code)) {
                return li;
            }
        }
        return null;
    }

    public static List<LangItem> getAll() {
        return new ArrayList<LangItem>(items.values());
    }

    private LangItem(KrnObject obj, String name, String code, ImageIcon icon) {
        this.obj = obj;
        this.name = name;
        this.code = code;
        this.icon = icon;
    }

    public String toString() {
        return name;
    }

    public boolean equals(Object obj) {
        if (obj instanceof LangItem) {
            return (this.obj.id == ((LangItem)obj).obj.id);
        }
        return false;
    }

    private static synchronized void reload(Kernel krn) {
    	if (items == null && krn.getBaseName() != null) {
//    		System.out.println("@ Loading lang items.");
	        items = new HashMap<Long, LangItem>();
	        try {
	        	Map<Long, KrnObject> objById = new HashMap<Long, KrnObject>();
	        	for(KrnObject obj : Kernel.LANGUAGES) {
	        		objById.put(obj.id, obj);
	        	}
	            long[] langIds = Funcs.makeObjectIdArray(Kernel.LANGUAGES);
	            StringValue[] svs = krn.getStringValues(
	                    langIds, Kernel.SC_LANGUAGE.id, "name", 0, false, 0);
	            StringValue[] codes = krn.getStringValues(
	                    langIds, Kernel.SC_LANGUAGE.id, "code", 0, false, 0);
	            LongValue[] selectedLangs = krn.getLongValues(langIds,
	                    Kernel.SC_LANGUAGE.id, "lang?", 0);
	            for (int i = 0; i < codes.length; i++) {
	                StringValue codeValue = codes[i];
	                String code = "NA";
	                ImageIcon ic = null;
	                if (codeValue.value.equals("RU")) {
	                    if (Constants.SE_UI || krn.getUser().isDesignerRun) {
	                       ic = CommonUtil.getImageIconFull("RULangFlag.png"); 
	                    }else {
	                        ic = CommonUtil.getImageIcon("RULang");
	                    }
	                    code = "RU";
                    } else if (codeValue.value.equals("KZ")) {
                        if (Constants.SE_UI || krn.getUser().isDesignerRun) {
                            ic = CommonUtil.getImageIconFull("KZLangFlag.png");
                        } else {
                            ic = CommonUtil.getImageIcon("KZLang");
                        }
                        code = "KZ";
                    } else if (codeValue.value.equals("EN")) {
	                    ic = CommonUtil.getImageIcon("ENLang");
	                    code = "EN";
	                } else {
	                    ic = CommonUtil.getImageIcon("NALang");
	                }
	                String name = "";
	                for (int j = 0; j < svs.length; j++) {
	                    StringValue sv = svs[j];
	                    if (codeValue.objectId == sv.objectId && sv.value != null) {
	                        name = sv.value;
	                        break;
	                    }
	                }
	                for (int j = 0; j < selectedLangs.length; j++) {
	                    LongValue selValue = selectedLangs[j];
	                    if (selValue.objectId == codeValue.objectId && selValue.value == 1) {
	                        items.put(codeValue.objectId,
	                                  new LangItem(objById.get(codeValue.objectId), name, code, ic));
	                        break;
	                    }
	                }
	            }
	        } catch (KrnException e) {
	        	items = null;
	            e.printStackTrace();
	        }
    	}
    }
}
