package com.cifs.or2.server.plugins;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

import kz.tamur.comps.Constants;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Кайржан
 * Date: Jul 12, 2005
 * Time: 10:07:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class KATO implements SrvPlugin {
    private Session s;

    private KrnClass nodeCls;
    private KrnClass valCls;
    private KrnAttribute nodeAttr;
    private KrnAttribute parentAttr;
    private KrnAttribute childAttr;
    private KrnAttribute valueAttr;

    public Session getSession() {
        return s;
    }

    public void setSession(Session session) {
        s = session;
        if (nodeCls == null) {
            nodeCls = s.getClassByName("Структ классификатора КАТО");
            valCls = s.getClassByName("Классификатор КАТО");
            nodeAttr = s.getAttributeByName(valCls, "структ классификатора КАТО");
            parentAttr = s.getAttributeByName(nodeCls, "родитель");
            childAttr = s.getAttributeByName(nodeCls, "дети");
            valueAttr = s.getAttributeByName(nodeCls, "значение");
        }
    }

/*
    public KrnObject[] getRegion(KrnObject value) {
        KrnObject obj = value.getKrnObject();
        List regs = new ArrayList();
        KrnObject parent = null;
        while ((parent = getParent(obj)) != null) {
            regs.add(obj);
            obj = parent;
        }
        Collections.reverse(regs);
        KrnObject[] res = new KrnObject[3];
        for (int i = 0; i <= regs.size() - 1 && i<2; i++) {
            res[i] = (KrnObject) SrvOrLang.wrap(regs.get(i), s);
        }
        if (regs.size()>2)
            res[2] = (KrnObject) SrvOrLang.wrap(regs.get(regs.size() - 1), s);
        return res;
    }
*/
    /**
     * Возвращает данные гоелокации
     * @see com.cifs.or2.server.plugins.KATO#getRegion(KrnObject, KrnObject)
     * @param value значение объекта
     * @return данные геолокации: область, район, город, округ
     */
    public KrnObject[] getRegion(KrnObject value) {
        return getRegion(value, null);
    }
    /**
     * Возвращает геолокацию
     * @param value значение объекта
     * @param lang язык(объект Language)
     * @return данные геолокации: область, район, город, округ
     */
    public KrnObject[] getRegion(KrnObject value, KrnObject lang) {
        try {
            KrnObject obj = value.getKrnObject();
            KrnObject parent = null;
            KrnObject last_ = obj;
            Map tmp = new HashMap();
            KrnAttribute attr = s.getAttributeByName(valCls, "мультинаименование");

            long langId = (lang != null && lang.getKrnObject() != null)
                ? lang.getKrnObject().id
                : s.getContext().langId;

            boolean isStart = false;
            while ((parent = getParent(obj)) != null) {
                String[] str_value = s.getStrings(obj.id, attr.id, langId, false, s.getContext().trId);
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
                        if (obj== last_)
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
                }
                obj = parent;
                isStart = true;
            }
            KrnObject[] res = new KrnObject[4];
            res[2] = (KrnObject) last_;
            res[0] = (tmp.get("obl")== null) ? (KrnObject) tmp.get("gorod_oblast") : (KrnObject) tmp.get("obl");
            res[1] = (KrnObject) tmp.get("raion");
            res[3] = (KrnObject) tmp.get("okrug");
            if (tmp.get("obl")!= null && tmp.get("raion")!= null && last_==null)
                res[2] = (KrnObject)tmp.get("gorod_oblast");
            return res;
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Возвращает данные геолокации
     * @param value значение объекта
     * @return данные геолокации: область, район, обл. город, округ
     */
    public KrnObject[] getRegionForAR(KrnObject value) {
        return getRegionForAR(value, null);
    }
    /**
     * Возвращает данные геолокации
     * @param value значение объекта
     * @param lang язык(объект класса Language)
     * @return данные геолокации: область, район, обл. город, округ
     */
    public KrnObject[] getRegionForAR(KrnObject value, KrnObject lang) {
        try {
            KrnObject obj = value.getKrnObject();
            KrnObject parent = null;
            Map tmp = new HashMap();
            KrnAttribute attr = s.getAttributeByName(valCls, "мультинаименование");

            long langId = (lang != null && lang.getKrnObject() != null)
                ? lang.getKrnObject().id
                : s.getContext().langId;

            while ((parent = getParent(obj)) != null) {
                String[] str_value = new String[0];
                str_value = s.getStrings(obj.id, attr.id, langId, false, s.getContext().trId);
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
            res[0] = (KrnObject) tmp.get("obl");
            res[1] = (KrnObject) tmp.get("raion");
            res[3] = (KrnObject) tmp.get("okrug");
            res[2] = (KrnObject) tmp.get("gorod_oblast");
            return res;
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String format(String str) {//TODO
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
    /**
     * Возвращает родителя
     * @param obj объект
     * @return
     */
    private KrnObject getParent(KrnObject obj) {//TODO
        try {
            KrnObject[] par_obj = s.getObjects(obj.id, nodeAttr.id, new long[0], 0);
            KrnObject[] parents = s.getObjects(par_obj[0].id, parentAttr.id, new long[0], 0);
            if (parents.length > 0) {
                KrnObject[] values = s.getObjects(parents[0].id, valueAttr.id, new long[0], 0);
                return values[0];
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    private KrnObject getChild(KrnObject obj) {//TODO

        try {
            KrnObject[] par_obj = s.getObjects(obj.id, nodeAttr.id, new long[0], 0);
            KrnObject[] children = s.getObjects(par_obj[0].id, childAttr.id, new long[0], 0);
            if (children.length > 0) {
                KrnObject[] values = s.getObjects(children[0].id, valueAttr.id, new long[0], 0);
                return values[0];
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }
}
