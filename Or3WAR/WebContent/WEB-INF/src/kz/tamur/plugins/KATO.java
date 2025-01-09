package kz.tamur.plugins;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import kz.tamur.comps.Constants;
import kz.tamur.rt.orlang.AbstractClientPlugin;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.Utils;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.LongValue;
import com.cifs.or2.kernel.ObjectValue;

/**
 * Created by IntelliJ IDEA.
 * User: Кайржан
 * Date: Jul 12, 2005
 * Time: 10:07:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class KATO extends AbstractClientPlugin {

    public String[] getCodes(String code) {
        String[] k = new String[5];
        k[0] = code.substring(0, 2);
        k[1] = code.substring(2, 4);
        k[2] = code.substring(4, 6);
        k[3] = code.substring(6, 9);
        k[4] = code.substring(9);
        return k;
    }

    public String format(String str) {
        String[] tips = new String[] {"Г.А.", "Р.А.", "П.А.", "\u049a.\u04d8.", "А.\u04d8.", "К.\u04d8.", "Г.","П.","ОТГ.","ПОДХОЗ","КР.ХОЗ","ЛЕСХОЗ","СЕЛХОЗ","МАШ.ДВОР","ОБЛАСТЬ"
                                      ,"РАЙОН","\u049a.","К.","Ж.","\u049aОСШАР","ШАР\u049aОЖ","ОРМАНШАР","АУЫЛШАР","МАШ.АУЛАСЫ",
        "ОБЛЫСЫ","АУДАНЫ","А.О.","А.","С.О.","С.","УЧ.","РЗД.","СТ."};
        str = str.toUpperCase(Constants.OK);
        String tmp = str;
        for (int i=0; i< tips.length;i++) {
            String patternStr = escapeRE(tips[i]);
            str = str.replaceAll(patternStr,"");
            if (!tmp.equals(str))
                break;
        }
        str = str.trim();
        return str;
    }

    static Pattern escaper = Pattern.compile("([^a-zA-zа-яА-я0-9])");
    public static String escapeRE(String str) {
        return escaper.matcher(str).replaceAll("\\\\$1");
    }

    public KrnObject[] getRegion(KrnObject value) {
        return getRegion(value, null);
    }

    public KrnObject[] getRegion(KrnObject value, KrnObject lang) {
        try {
            Kernel krn = ClientOrLang.getKernel();
            KrnObject obj = value.getKrnObject();
            KrnObject parent = null;
            KrnObject last_ = obj;
            Map tmp = new HashMap();
            boolean isStart = false;
            long langId = (lang != null && lang.getKrnObject() != null)
                ? lang.getKrnObject().id
                : Utils.getDataLangId(krn);

            while ((parent = getParent(obj)) != null) {
                String[] str_value = krn.getStrings(obj, "мультинаименование", langId, 0);
                if ((!str_value[0].endsWith("г.а.")) && (!str_value[0].endsWith("р.а.")) &&
                        (!str_value[0].endsWith("п.а.")) &&
                    (!str_value[0].endsWith("Г.А.")) && (!str_value[0].endsWith("Р.А.")) &&
                        (!str_value[0].endsWith("П.А."))
                ) {
                    if (str_value[0].indexOf("область") > -1 || str_value[0].indexOf("облысы") > -1 ||
                            str_value[0].indexOf("ОБЛАСТЬ") > -1 || str_value[0].indexOf("ОБЛЫСЫ") > -1)
                        tmp.put("obl",obj);
                    else if (str_value[0].indexOf("район") > -1 || str_value[0].indexOf("ауданы") > -1 ||
                            str_value[0].indexOf("РАЙОН") > -1 || str_value[0].indexOf("АУДАНЫ") > -1) {
                        if (obj == last_)
                            last_ = null;

                        tmp.put("raion",obj);
                    }
                    else if (str_value[0].indexOf("а.о.") > -1 || str_value[0].indexOf("с.о.") > -1 ||
                            str_value[0].indexOf("А.О.") > -1 || str_value[0].indexOf("С.О.") > -1)
                        tmp.put("okrug",obj);
                    else if (isStart && (str_value[0].indexOf("г.") >-1 || str_value[0].indexOf("Г.") >-1 ||
                            str_value[0].indexOf("\u049b.") >-1 || str_value[0].indexOf("\u049a.") >-1 ))
                        tmp.put("gorod_oblast", obj);
                } else if (str_value[0].toUpperCase(Constants.OK).equals("АСТАНА Г.А.") || str_value[0].toUpperCase(Constants.OK).equals("АСТАНА \u049a.\u04d8.") ||
                        str_value[0].toUpperCase(Constants.OK).equals("АЛМАТЫ Г.А.") || str_value[0].toUpperCase(Constants.OK).equals("АЛМАТЫ \u049a.\u04d8.")) {
                    KrnObject ch = getChild(obj);
                    if (ch != null && last_ != null && ch.id == last_.id) last_ = null;
                    tmp.put("obl",ch);
                } else if (str_value[0].toUpperCase(Constants.OK).endsWith("Г.А.") ||
                		   str_value[0].toUpperCase(Constants.OK).endsWith("\u049a.\u04d8.")) {
                    KrnObject ch = getChild(obj);
                    if (ch != null && last_ != null && ch.id != last_.id) {
                    	//last_ = null;
                    	tmp.put("gorod_oblast", ch);
                    } else if (ch != null && last_ == null && tmp.get("raion") != null) {
                    	tmp.put("gorod_oblast", ch);
                    	//last_ = (KrnObject)tmp.get("raion");
                    	last_ = (KrnObject)tmp.remove("raion");
                    }
                }

                obj = parent;
                isStart = true;
            }
            KrnObject[] res = new KrnObject[5];
            res[2] = last_;
            res[0] = (tmp.get("obl")== null) ? (KrnObject)tmp.get("gorod_oblast") : (KrnObject)tmp.get("obl");
            res[1] = (KrnObject)tmp.get("raion");
            res[3] = (KrnObject)tmp.get("okrug");
            if (tmp.get("obl")!= null && tmp.get("raion")!= null && last_==null)
                res[2] = (KrnObject)tmp.get("gorod_oblast");
            
            if (tmp.get("gorod_oblast") != null && last_ != null) {
            	res[4] = (KrnObject)tmp.get("gorod_oblast");
            }
            return res;
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;

    }

    public KrnObject[] getRegionForAR(KrnObject value) {
        return getRegionForAR(value, null);
    }

    public KrnObject[] getRegionForAR(KrnObject value, KrnObject lang) {
        try {
            Kernel krn = ClientOrLang.getKernel();
            KrnObject obj = value.getKrnObject();
            KrnObject parent = null;
            Map tmp = new HashMap();
            long langId = (lang != null && lang.getKrnObject() != null)
                ? lang.getKrnObject().id
                : Utils.getDataLangId(krn);

            while ((parent = getParent(obj)) != null) {
                String[] str_value = krn.getStrings(obj, "мультинаименование", langId, 0);
                if (str_value[0].indexOf("область") > -1 ||
                    str_value[0].indexOf("облысы") > -1 ||
                    str_value[0].indexOf("ОБЛАСТЬ") > -1 ||
                    str_value[0].indexOf("ОБЛЫСЫ") > -1) {

                    tmp.put("obl",obj);
                } else if (str_value[0].indexOf("район") > -1 ||
                           str_value[0].indexOf("ауданы") > -1 ||
                           str_value[0].indexOf("РАЙОН") > -1 ||
                           str_value[0].indexOf("АУДАНЫ") > -1) {

                    tmp.put("raion",obj);
                }
                else if (str_value[0].indexOf("а.о.") > -1 ||
                         str_value[0].indexOf("с.о.") > -1 ||
                         str_value[0].indexOf("А.О.") > -1 ||
                         str_value[0].indexOf("С.О.") > -1) {

                    tmp.put("okrug",obj);
                } else if ((!str_value[0].endsWith("г.а.")) &&
                           (!str_value[0].endsWith("р.а.")) &&
                           (!str_value[0].endsWith("п.а.")) &&
                           (!str_value[0].endsWith("\u049b.\u04d9.")) &&
                           (!str_value[0].endsWith("а.\u04d9.")) &&
                           (!str_value[0].endsWith("к.\u04d9.")) &&
                           (!str_value[0].endsWith("Г.А.")) &&
                           (!str_value[0].endsWith("Р.А.")) &&
                           (!str_value[0].endsWith("П.А.")) &&
                           (!str_value[0].endsWith("\u049a.\u04d8.")) &&
                           (!str_value[0].endsWith("А.\u04d8.")) &&
                           (!str_value[0].endsWith("К.\u04d8."))) {

                    tmp.put("gorod_oblast", obj);
            }
                obj = parent;
            }
            KrnObject[] res = new KrnObject[4];
            res[0] = (KrnObject)tmp.get("obl");
            res[1] = (KrnObject)tmp.get("raion");
            res[3] = (KrnObject)tmp.get("okrug");
            res[2] = (KrnObject)tmp.get("gorod_oblast");
            return res;
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;

    }

    private KrnObject getParent(KrnObject obj) {
        try {
            Kernel krn = ClientOrLang.getKernel();
            KrnObject[] par_obj = krn.getObjects(obj, "структ классификатора КАТО", 0);
            KrnObject[] parents = krn.getObjects(par_obj[0], "родитель", 0);
            if (parents.length > 0) {
                KrnObject[] values = krn.getObjects(parents[0], "значение", 0);
                return values[0];
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    private KrnObject getChild(KrnObject obj) {

        try {
            Kernel krn = ClientOrLang.getKernel();
            KrnObject[] par_obj = krn.getObjects(obj, "структ классификатора КАТО", 0);
            KrnObject[] children = krn.getObjects(par_obj[0], "дети", 0);
            if (children.length > 0) {
            	long[] ids = Funcs.makeObjectIdArray(children);
            	ObjectValue[] ovs = krn.getObjectValues(ids, children[0].classId, "значение", 0);
            	long[] vids = Funcs.makeObjectValueIdArray(ovs);
            	
            	LongValue[] lvs = krn.getLongValues(vids, ovs[0].value.classId, "лог -удален?-", 0);
            	
            	long objId = 0;
            	for(LongValue lv : lvs ) {
            		if (lv.value == 0) {
            			objId = lv.objectId;
            			for (ObjectValue ov : ovs) {
            				if (ov.value.id == objId)
            					return ov.value;
            			}
            		}
            	}
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }
}
