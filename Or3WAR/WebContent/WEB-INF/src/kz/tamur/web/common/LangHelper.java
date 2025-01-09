package kz.tamur.web.common;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.kernel.LongValue;
import com.cifs.or2.kernel.KrnException;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;

public class LangHelper {
    private static final Log LOG_ = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + LangHelper.class);

    private static Map<Long, WebLangItem> items = new HashMap<Long, WebLangItem>();
    private static Map<String, WebLangItem> langsByCode = new HashMap<String, WebLangItem>();
    private static List<Integer> configuration = new ArrayList<Integer>();

    private static Map<Integer, WebLangItem> rusLang = new HashMap<Integer, LangHelper.WebLangItem>();
    private static Map<Integer, WebLangItem> kazLang = new HashMap<Integer, LangHelper.WebLangItem>();
    
    private static final long shift = 100000000L;
    
    public static WebLangItem getRusLang(int configNumber) {
        return rusLang.get(configNumber);
    }

    public static WebLangItem getKazLang(int configNumber) {
        return kazLang.get(configNumber);
    }

    public static synchronized void reload(WebSession session) {
    	int configNumber = session.getConfigNumber();
    	if (!configuration.contains(configNumber)) {
    		configuration.add(configNumber);
	        try {
	            final Kernel krn = session.getKernel();
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
	                if (codeValue.value.equals("RU")) {
	                    code = "RU";
	                } else if (codeValue.value.equals("KZ")) {
	                    code = "KZ";
	                } else if (codeValue.value.equals("EN")) {
	                    code = "EN";
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
	                        WebLangItem wli = new WebLangItem(objById.get(codeValue.objectId), name, code);
	                        items.put(new Long(codeValue.objectId) + shift * configNumber, wli);
	                        langsByCode.put(code + configNumber, wli);
	                        if ("RU".equals(code))
	                            rusLang.put(configNumber, wli);
	                        else if ("KZ".equals(code))
	                            kazLang.put(configNumber, wli);
	                        break;
	                    }
	                }
	            }
	        } catch (KrnException e) {
	        	items = null;
	        	langsByCode = null;
	            LOG_.error(e, e);
	        }
    	}
    }

    public static WebLangItem getLangById(long langId, int configNumber) {
        return items.get(langId + shift * configNumber);
    }

    public static WebLangItem getLangByCode(String code, int configNumber) {
        return langsByCode.get(code + configNumber);
    }

    public static void init(WebSession webSession) {
        reload(webSession);
    }

    public static List<WebLangItem> getAll(int configNumber) {
    	Set<Long> ids = items.keySet();
        List<WebLangItem> res = new ArrayList<WebLangItem>();
        for (Long id : ids) {
        	long nid = id - shift * configNumber;
        	if (nid > 0 && nid < shift)
        		res.add(items.get(id));
        }
        return res;
    }

    public static class WebLangItem {
        public KrnObject obj;
        public String name;
        public String code;

        public WebLangItem(KrnObject obj, String name, String code) {
            this.obj = obj;
            this.name = name;
            this.code = code;
        }
    }
}
