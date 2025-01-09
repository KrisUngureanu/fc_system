package com.cifs.or2.util;

import com.cifs.or2.kernel.*;

import java.util.*;

import kz.tamur.util.MapMap;
import kz.tamur.util.Pair;

public class Funcs {
    private static Map refs_;
    private static Map langs_ = new HashMap();
    private static Map filteredIds_;

    public static synchronized void setJepRefs(Map refs) {
        refs_ = refs;
    }

    public static Map getJepRefs() {
        return refs_;
    }

    public static Map getJepLangs() {
        return langs_;
    }

    public static synchronized void setFilteredIds(Map filteredIds) {
        filteredIds_ = filteredIds;
    }

    public static Map getFilteredIds() {
        return filteredIds_;
    }

    public static String getIds(long[] arr) {
        String res = ""; StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; i++)
            sb.append("," + arr[i]);
        if (sb.length() > 0)
            res = sb.toString().substring(1);
        return res;
    }
    
    public static long[] makeLongArray(Collection col) {
        if (col == null) return new long[0];
        long[] res = new long[col.size()];
        int j = 0;
        for (Iterator it = col.iterator(); it.hasNext(); ++j) {
            Number i = (Number) it.next();
            res[j] = i.longValue();
        }
        return res;
    }

    public static int[] makeIntArray(Collection col) {
        if (col == null) return new int[0];
        int[] res = new int[col.size()];
        int j = 0;
        for (Iterator it = col.iterator(); it.hasNext(); ++j) {
            Number i = (Number) it.next();
            res[j] = i.intValue();
        }
        return res;
    }

    public static long[] makeLongArray(String arr, String token) {
        StringTokenizer st = new StringTokenizer(arr, token);
        long[] res = new long[st.countTokens()];
        int i = 0;
        while (st.hasMoreTokens())
            res[i++] = Integer.parseInt(st.nextToken());
        return res;
    }

    public static Set getSet(Map map, Object key) {
        Set res = (Set) map.get(key);
        if (res == null) {
            res = new TreeSet();
            map.put(key, res);
        }
        return res;
    }

    public static Set getSet(MapMap mmap, Object key1, Object key2) {
        Set res = (Set) mmap.get(key1, key2);
        if (res == null) {
            res = new TreeSet();
            mmap.put(key1, key2, res);
        }
        return res;
    }

    public static String xmlQuote(String str) {
        if (str == null || str.length() == 0)
            return "";
        StringBuilder res = new StringBuilder(str.length() * 2);
        char[] arr = str.toCharArray();
        for (int i = 0; i < arr.length - 1; ++i) {
            switch (arr[i]) {
            case '<':
                res.append("&#60;");
                break;
            case '>':
                res.append("&#62;");
                break;
            case '&':
                if (arr[i+1] != '#')
                    res.append("&#38;");
                else
                    res.append(arr[i]);
                break;
            case '\'':
                res.append("&#39;");
                break;
            case '/':
                res.append("&#47;");
                break;
            case '"':
                res.append("&#34;");
                break;
            case '|':
                res.append("&#124;");
                break;
            default :
                res.append(arr[i]);
            }
        }
        switch (arr[arr.length - 1]) {
        case '<':
            res.append("&#60;");
            break;
        case '>':
            res.append("&#62;");
            break;
        case '&':
            res.append("&#38;");
            break;
        case '\'':
            res.append("&#39;");
            break;
        case '/':
            res.append("&#47;");
            break;
        case '"':
            res.append("&#34;");
            break;
        case '|':
            res.append("&#124;");
            break;
        default :
            res.append(arr[arr.length - 1]);
        }
        return res.toString();
    }

    public static String reverseXmlQuote(String str) {
        if (str == null)
            return "";
        str = str.replaceAll("&#60;", "<");
        str = str.replaceAll("&#62;", ">");
        str = str.replaceAll("&#38;", "&");
        str = str.replaceAll("&#39;", "\'");
        str = str.replaceAll("&#47;", "/");
        str = str.replaceAll("&#34;", "\"");
        str = str.replaceAll("&#124;", "|");
        return str;
    }

    public static String reverseHtmlQuote(String str) {
        if (str == null)
            return "";
        str = str.replaceAll("&lt;", "<");
        str = str.replaceAll("&gt;", ">");
        return str;
    }

    public static String toJavaString(String str) {
        if (str == null)
            return "";
        StringBuffer res = new StringBuffer();
        char[] arr = str.toCharArray();

        for (int i = 0; i < arr.length; ++i) {
            switch (arr[i]) {
            case '\'':
                res.append("\\\'");
                break;
            case '"':
                res.append("\\\"");
                break;
            case '\r':
                res.append("\\r");
                break;
            case '\n':
                res.append("\\n");
                break;
            default :
                res.append(arr[i]);
            }
        }
        return res.toString();
    }

    public static String[] append(String[] src, String[] strs) {
        int i = src.length;
        String[] res = new String[i + strs.length];
        System.arraycopy(src, 0, res, 0, src.length);
        System.arraycopy(strs, 0, res, src.length, strs.length);
        return res;
    }

    public static boolean isIntegralType(long classId) {
        return classId < 100;
    }

    public static String removeChars(String str, char ch) {
        if (str == null) return "";
        StringBuffer res = new StringBuffer(str.length());
        char[] s = str.toCharArray();
        for (int i = 0; i < s.length; i++) {
            if (s[i] != ch)
                res.append(s[i]);
        }
        return res.toString();
    }

    public static long[] makeObjectIdArray(KrnObject[] objs) {
        long[] res = new long[objs.length];
        for (int i = 0; i < res.length; ++i)
            res[i] = objs[i].id;
        return res;
    }

    public static String[] makeObjectUidArray(KrnObject[] objs) {
        String[] res = new String[objs.length];
        for (int i = 0; i < res.length; ++i)
            res[i] = objs[i].uid;
        return res;
    }

    public static MapMap convertObjectValues(ObjectValue[] ovs,
                                             boolean isArray) {
        MapMap res = new MapMap();
        for (int i = 0; i < ovs.length; ++i) {
            ObjectValue ov = ovs[i];
            if (isArray || ov.index == 0) {
                Pair val = new Pair(ov.value, new Long(ov.tid));
                res.put(new Long(ov.objectId), new Integer(ov.index), val);
            }
        }
        return res;
    }

    public static MapMap convertStringValues(StringValue[] svs,
                                             boolean isArray) {
        // HACK!!! Надо добавить поле tid в StringValue, LongValue и FloatValue
        final Integer ZERO_TID = new Integer(0);
        // END HACK
        MapMap res = new MapMap();
        for (int i = 0; i < svs.length; ++i) {
            StringValue sv = svs[i];
            if (isArray || sv.index == 0) {
                Pair val = new Pair(sv.value, ZERO_TID);
                res.put(new Long(sv.objectId), new Integer(sv.index), val);
            }
        }
        return res;
    }

    public static MapMap convertLongValues(LongValue[] lvs, boolean isArray) {
        // HACK!!! Надо добавить поле tid в StringValue, LongValue и FloatValue
        final Integer ZERO_TID = new Integer(0);
        // END HACK
        MapMap res = new MapMap();
        for (int i = 0; i < lvs.length; ++i) {
            LongValue lv = lvs[i];
            if (isArray || lv.index == 0) {
                Pair val = new Pair(new Long(lv.value), ZERO_TID);
                res.put(new Long(lv.objectId), new Integer(lv.index), val);
            }
        }
        return res;
    }

    public static Object getLastElement(Collection col) {
        Object res = null;

        for (Iterator it = col.iterator(); it.hasNext();) {
            res = it.next();
            if (!it.hasNext())
                break;
        }

        return res;
    }

    public static String ids2String(long[] ids, int offs, int len) {
        StringBuffer res = new StringBuffer();
        if (ids.length > offs) {
            res.append(ids[offs]);
            for (int i = offs + 1; i < offs + len; ++i)
                res.append("," + ids[i]);
        }
        return res.toString();
    }

    public static String ids2String(Collection ids) {
        StringBuffer res = new StringBuffer();
        if (ids.size() > 0) {
            Iterator it = ids.iterator();
            res.append(it.next());
            while (it.hasNext())
                res.append("," + it.next());
        }
        return res.toString();
    }

    public static String krnObjects2String(Collection objs) {
        StringBuffer res = new StringBuffer();
        if (objs.size() > 0) {
            Iterator it = objs.iterator();
            res.append(((KrnObject) it.next()).id);
            while (it.hasNext())
                res.append("," + ((KrnObject) it.next()).id);
        }
        return res.toString();
    }

    public static Pair splitAttrName(String path) {
        String name = null;
        Object index = null;
        int len = path.length();
        if (path.charAt(len - 1) == ']') {
            int open = path.lastIndexOf('[');
            name = path.substring(0, open);
            index = (len - open > 2) ? Integer.valueOf(path.substring(open + 1, len - 1)) : null;
        } else {
            name = path;
            index = "UNKNOWN";
        }
        return new Pair(name, index);
    }

    public static boolean contains(Collection objs, KrnObject obj) {
        for (Iterator it = objs.iterator(); it.hasNext();) {
            KrnObject o = (KrnObject) it.next();
            if (o == obj) {
                return true;
            } else if (o != null && obj != null && (o.id == obj.id)) {
                return true;
            }
        }
        return false;
    }

    public static String upgradeExpression(String expr) {
        if (expr != null && expr.length() > 0) {
            String str = expr.trim();
            if (str.charAt(str.length() - 1) != ';') {
                return str + ";";
            }
        }
        return expr;
    }
}
