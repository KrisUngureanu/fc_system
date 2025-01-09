package kz.tamur.admin;

import java.util.*;

import com.cifs.or2.util.*;
import com.cifs.or2.kernel.*;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.Filter;
import com.cifs.or2.client.Utils;

public class User {
    private static final Kernel KRN_ = Kernel.instance();

    private Filter currentFilter_;
    private ArrayList<Filter> filters_;
    private MultiMap privateFilters_;
    private Set<Long> privateFids_;

    public KrnObject object;
    KrnObject ifcLang;
    KrnObject dataLang;
    KrnObject ifc;
    KrnObject[] role;
    KrnObject base;
    KrnObject[] access;
    String sign = "";
    String name = "";
    boolean admin = false;
    String baseCode = "";
    List<KrnObject> help;

    public User(KrnObject obj) throws KrnException {
        object = obj;
        filters_ = new ArrayList<Filter>();
        privateFilters_ = new MultiMap();
        privateFids_ = new TreeSet<Long>();
        help = new ArrayList<KrnObject>();

        try {
            KrnObject[] objs = KRN_.getObjects(object, "interface", 0);
            if (objs.length > 0)
                ifc = objs[0];

            objs = KRN_.getObjects(object, "interface language", 0);
            if (objs.length > 0)
                ifcLang = objs[0];
            else
                error("Данному пользователю не назначен язык интерфейса");

            objs = KRN_.getObjects(object, "data language", 0);
            if (objs.length > 0)
                dataLang = objs[0];
            else
                error("Данному пользователю не назначен язык данных");

            objs = KRN_.getObjects(object, "role", 0);
            if (objs.length > 0)
                role = objs;

            objs = KRN_.getObjects(object, "access", 0);
            if (objs.length > 0)
                access = objs;

            String[] strs = KRN_.getStrings(object, "name", dataLang.id, 0);
            if (strs.length > 0)
                name = strs[0];

            strs = KRN_.getStrings(object, "sign", dataLang.id, 0);
            if (strs.length > 0)
                sign = strs[0];
            long[] lngs = KRN_.getLongs(object, "admin", 0);
            if (lngs.length > 0)
                admin = lngs[0] == 1;

            objs = KRN_.getObjects(object, "base", 0);
            if (objs.length > 0) {
                base = objs[objs.length - 1];
                KRN_.selectBases(new long[]{base.id});
                strs = KRN_.getStrings(base, "код", 0, 0);
                if (strs != null && strs.length > 0)
                    baseCode = strs[0];
            } else
                error("Данному пользователю не назначен уровень доступа");

            KrnObject[] helpObjs = KRN_.getObjects(object, "helps", 0);
            if (helpObjs != null) {
                for (KrnObject helpObj : helpObjs) {
                    help.add(helpObj);
                }
            }

/*
            objs = KRN_.getObjects(object, "help", 0);
            if (objs.length > 0) {
                help = objs[0];
            }
*/

            loadFilters();
        } catch (Exception ex) {
            // Игнорируем ошибку для возможности добавить недостающие классы
            // при помощи Администратора
            ex.printStackTrace();
        }
    }

    public ArrayList<Filter> getFilters() {
        return filters_;
    }

    public ArrayList getPrivateFilters(String className) {
        return (ArrayList) privateFilters_.get(className);
    }

    public Filter getCurrentFilter() {
        return currentFilter_;
    }

    public void setCurrentFilter(Filter filter) {
        currentFilter_ = filter;
    }

    public void addPrivateFilter(KrnObject fobj) throws KrnException {
        Long fid = new Long(fobj.id);
        if (!privateFids_.contains(fid)) {
            long langId = (dataLang != null) ? dataLang.id : 0;
            String title = Utils.getString(fobj, "title", langId);
            String className = Utils.getString(fobj, "className", 0);
            String expr = Utils.getString(fobj, "expr", 0);
            int flag = (expr != null) ? Character.digit(expr.charAt(3), 10) : 0;
            int i = privateFids_.size();
            KRN_.setObject(object.id, object.classId, "privateFilters", i, fobj.id, 0, false);
            privateFids_.add(fid);
            privateFilters_.put(className, new Filter(fobj, title, className, flag));
        }
    }

    public void delPrivateFilter(Filter[] fobjs) throws KrnException {
        KrnObject[] objs_ = KRN_.getObjects(object, "privateFilters", 0);
        TreeSet<Integer> ts_ = new TreeSet<Integer>();
        for (int i = 0; i < objs_.length; ++i) {
            for (int j = 0; j < fobjs.length; ++j)
                if (objs_[i].id == fobjs[j].obj.id) ts_.add(new Integer(i));
        }
        int[] index_a = Funcs.makeIntArray(ts_);
        KRN_.deleteValue(object.id, object.classId, "privateFilters", index_a, 0);
        for (int i = 0; i < fobjs.length; ++i) {
            Long fid = new Long(fobjs[i].obj.id);
            String className = Utils.getString(fobjs[i].obj, "className", 0);
            privateFids_.add(fid);
            privateFilters_.get(className).remove(fobjs[i]);
        }
    }

    public KrnObject[] getRole() {
        return role;
    }

    public KrnObject[] getAccess() {
        return access;
    }

    public boolean getAdmin() {
        return admin;
    }

    public KrnObject getBase() {
        return base;
    }

    public KrnObject getDataLanguage() {
        return dataLang;
    }

    public KrnObject getIfcLang() {
        return ifcLang;
    }

    public KrnObject getIfc() {
        return ifc;
    }

    public String getUserSign() {
        return sign;
    }

    public String getBaseCode() {
        return baseCode;
    }

    private void loadFilters() throws KrnException {
        long langId = (dataLang != null) ? dataLang.id : 0;

        // загрузка фильтров
        Map fs = new TreeMap();

/*
    KrnObject[] fobjs = KRN_.getObjects (object, "filters", 0);
    Set userFids = new TreeSet();
    fillSet (fobjs, userFids);
    fillMap (fobjs, fs);

    fobjs = KRN_.getObjects (object, "privateFilters", 0);
    fillSet (fobjs, privateFids_);
    fillMap (fobjs, fs);

    fobjs = KRN_.getClassObjects (KRN_.getClassByName ("PublicFilter"), 0);
    Set publicFids = new TreeSet();
    fillSet (fobjs, publicFids);
    fillMap (fobjs, fs);
*/

        if (fs.size() > 0) {
            long[] filterIds = Funcs.makeLongArray(fs.keySet());
            // загрузка титулов
            Map<Long, String> titles = new TreeMap<Long, String>();
            StringValue[] svs = KRN_.getStringValues(filterIds, Kernel.SC_FILTER.id, "title", langId, false, 0);
            for (int i = 0; i < svs.length; ++i) {
                StringValue sv = svs[i];
                if (sv.index == 0)
                    titles.put(new Long(sv.objectId), sv.value);
            }
            // загрузка флагов
            Map<Long, Integer> flags = new TreeMap<Long, Integer>();
            svs = KRN_.getStringValues(filterIds, Kernel.SC_FILTER.id,
                    "expr", 0, false, 0);
            for (int i = 0; i < svs.length; ++i) {
                StringValue sv = svs[i];
                if (sv.index == 0) {
                    String str = sv.value;
                    if (str != null) {
                        str = str.substring(0, str.indexOf('|'));
                        if (str.length() > 3) {
                            Integer flag = new Integer(Character.digit(str.charAt(3), 10));
                            flags.put(new Long(sv.objectId), flag);
                        }
                    }
                }
            }
            // загрузка имен классов фильтруемых объектов
            svs = KRN_.getStringValues(filterIds, Kernel.SC_FILTER.id, "className",
                    0, false, 0);
            for (int i = 0; i < svs.length; ++i) {
                StringValue sv = svs[i];
                if (sv.index == 0) {
                    //Long fid = new Long(sv.objectId);
                    //KrnObject obj = (KrnObject) fs.get(fid);
                    //String title = (String) titles.get(new Long(sv.objectId));
                    //Integer tmp = (Integer) flags.get(fid);
                    //int flag = (tmp != null) ? tmp.intValue() : 0;
                    //Filter f = new Filter(obj, title, sv.value, flag);
                    // if (publicFids.contains (fid))
                    //  KRN_.addFilter(f);
                    //if (userFids.contains(fid))
                    //  filters_.add (f);
                    //if (privateFids_.contains (fid))
                    //  privateFilters_.put (f.className, f);
                }
            }
        }

        if (filters_.size() > 0)
            currentFilter_ = filters_.get(0);
    }

    private void error(String msg) throws KrnException {
        throw new KrnException(0, msg);
    }

    public KrnObject getObject() {
        return object;
    }

    public String getName() {
        return name;
    }

    public List<KrnObject> getHelp() {
        return help;
    }
}
