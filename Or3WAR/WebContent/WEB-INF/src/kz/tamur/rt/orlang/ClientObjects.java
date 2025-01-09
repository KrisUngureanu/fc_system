package kz.tamur.rt.orlang;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kz.tamur.lang.EvalException;
import kz.tamur.lang.Objects;
import kz.tamur.lang.Sequence;
import kz.tamur.lang.parser.LangUtils;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.data.Cache;
import kz.tamur.rt.data.ObjectRecord;
import kz.tamur.rt.data.Record;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.util.ClientQuery;
import com.cifs.or2.client.Utils;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnAttributeOperations;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnClassOperations;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnObjectOperations;

/**
 * Created by IntelliJ IDEA.
 * Date: 18.01.2005
 * Time: 15:34:54
 * 
 * @author berik
 */
public class ClientObjects extends Objects {

    /** log. */
    private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ClientObjects.class.getName());

    @Override
    public KrnClass getClass(String name) {
        try {
            Kernel krn = ClientOrLang.getKernel();
            return krn.getClassByName(name);
        } catch (KrnException e) {
            log.error(e, e);
        }
        return null;
    }
    
    @Override
    public List<KrnClass> getClasses(long baseClassId, boolean withSubclasses) {
        try {
            Kernel krn = ClientOrLang.getKernel();
            return krn.getClasses(baseClassId, withSubclasses);
        } catch (KrnException e) {
            log.error(e, e);
        }
        return null;
    }

    @Override
    public List<KrnAttribute> getClassAttributes(String name) {
        try {
            Kernel krn = ClientOrLang.getKernel();
            KrnClass cls = krn.getClassByName(name);
            return krn.getAttributes(cls);
        } catch (KrnException e) {
            log.error(e, e);
        }
        return null;
    }
    
    @Override
    public KrnClass getClassById(Number id) {
        try {
            Kernel krn = ClientOrLang.getKernel();
            ClassNode cls = krn.getClassNode(id.longValue());
            return cls.getKrnClass();
        } catch (KrnException e) {
            log.error(e, e);
        }
        return null;
    }

    @Override
    public KrnAttribute getAttribute(KrnClass cls, String name) {
        try {
            Kernel krn = ClientOrLang.getKernel();
            return krn.getAttributeByName(cls.getKrnClass(), name);
        } catch (KrnException e) {
            log.error(e, e);
        }
        return null;
    }
    
    @Override
    public KrnAttribute getAttributeById(Number id) {
        try {
            Kernel krn = ClientOrLang.getKernel();
            return krn.getAttributeById(id.longValue());
        } catch (KrnException e) {
            log.error(e, e);
        }
        return null;
    }
    
    @Override
    public KrnObject createObject(String className) {
        try {
            Kernel krn = ClientOrLang.getKernel();
            KrnClass cls = krn.getClassByName(className);
            Cache cash = ClientOrLang.getFrame().getCash();
            Record rec = cash.createObject(cls.id);
            return (KrnObject) rec.getValue();
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public KrnObject cloneObject(KrnObject obj) {
        return null;
    }

    @Override
    public KrnObject getObject(String uid) {
        try {
            Cache cache = ClientOrLang.getFrame().getCash();
            KrnObject obj = cache.getObjectByUid(uid);
            if (obj != null) {
                cache.findRecord(obj);
                return obj;
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public KrnObject getObject(Number id) {
        try {
            Kernel krn = ClientOrLang.getKernel();
            KrnObject[] objs = krn.getObjectsByIds(new long[] { id.longValue() }, -1);
            KrnObject obj = (objs.length > 0) ? objs[0] : null;
            if (obj != null) {
                Cache cache = ClientOrLang.getFrame().getCash();
                cache.findRecord(obj);
                return obj;
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<KrnObject> getClassObjects(String className) {
        try {
            Kernel krn = ClientOrLang.getKernel();
            KrnClass cls = krn.getClassByName(className);
            Cache cash = ClientOrLang.getFrame().getCash();
            Set<ObjectRecord> recs = cash.getObjects(cls.id, 0, null);
            List<KrnObject> res = new ArrayList<KrnObject>(recs.size());
            for (Record rec : recs) {
                res.add((KrnObject) rec.getValue());
            }
            return res;
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public List<KrnObject> filter(KrnObject filter, int limit,int beginRow,int endRow) {
        try {
            final Kernel krn = ClientOrLang.getKernel();
            Cache cash = ClientOrLang.getFrame().getCash();
            KrnObject[] objs = krn.getFilteredObjects(filter.getKrnObject(), limit, beginRow, endRow, cash.getTransactionId());
            List<KrnObject> l = new ArrayList<KrnObject>(objs.length);
            for (int i = 0; i < objs.length; i++) {
                l.add(objs[i]);
            }
            return l;
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long filterCount(KrnObject filter) throws Exception {
        final Kernel krn = ClientOrLang.getKernel();
        Cache cash = ClientOrLang.getFrame().getCash();
        long res = krn.filterCount(filter.getKrnObject(), cash.getTransactionId());
        return res;
    }

    @Override
    public List<Object> filterGroup(KrnObject filter) throws Exception {
        final Kernel krn = ClientOrLang.getKernel();
        Cache cash = ClientOrLang.getFrame().getCash();
        List<Object> res = krn.filterGroup(filter.getKrnObject(), cash.getTransactionId());
        return res;
    }
    @Override
    public List<Object> filterGroup(KrnObject filter, Map<String, Object> params, boolean allTransactions) throws Exception {
        final Kernel krn = ClientOrLang.getKernel();
        String uid = filter.getUID();
        krn.clearFilterParams(uid);
        if (params != null) {
            for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String name = it.next();
                Object value = params.get(name);
                if (value instanceof List) {
                    krn.setFilterParam(uid, name, (List) value);
                } else {
                    krn.setFilterParam(uid, name, Collections.singletonList(value));
                }
            }
        }
        Cache cash = ClientOrLang.getFrame().getCash();
        long trId = allTransactions ? -1 : cash.getTransactionId();
        return krn.filterGroup(filter.getKrnObject(), trId);
    }
    @Override
    public List<KrnObject> filter(KrnObject filter, Map<String, Object> params, boolean allTransactions, int limit, int beginRow, int endRow) throws Exception {
        final Kernel krn = ClientOrLang.getKernel();
        String uid = filter.getUID();
        krn.clearFilterParams(uid);
        if (params != null) {
            for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String name = it.next();
                Object value = params.get(name);
                if (value instanceof List) {
                    krn.setFilterParam(uid, name, (List) value);
                } else {
                    krn.setFilterParam(uid, name, Collections.singletonList(value));
                }
            }
        }
        KrnClassOperations cls_ops = KrnClass.getOperations();
        KrnAttributeOperations attr_ops = KrnAttribute.getOperations();
        KrnObjectOperations obj_ops = KrnObject.getOperations();
        try {
            Cache cash = ClientOrLang.getFrame().getCash();
            long trId = allTransactions ? -1 : cash.getTransactionId();
            KrnObject[] objs = krn.getFilteredObjects(filter.getKrnObject(), limit,beginRow,endRow, trId);
            List<KrnObject> l = new ArrayList<KrnObject>(objs.length);
            for (int i = 0; i < objs.length; i++) {
                l.add(objs[i]);
            }
            return l;
        } catch (KrnException e) {
            e.printStackTrace();
        } finally {
            KrnClass.setOperations(cls_ops);
            KrnAttribute.setOperations(attr_ops);
            KrnObject.setOperations(obj_ops);
        }
        return null;
    }
    
    @Override
    public long filterCount(KrnObject filter, Map<String, Object> params, boolean allTransactions) throws Exception {
        final Kernel krn = ClientOrLang.getKernel();
        String uid = filter.getUID();
        krn.clearFilterParams(uid);
        if (params != null) {
            for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String name = it.next();
                Object value = params.get(name);
                if (value instanceof List) {
                    krn.setFilterParam(uid, name, (List) value);
                } else {
                    krn.setFilterParam(uid, name, Collections.singletonList(value));
                }
            }
        }
        Cache cash = ClientOrLang.getFrame().getCash();
        long trId = allTransactions ? -1 : cash.getTransactionId();
        return krn.filterCount(filter.getKrnObject(), trId);
    }
    
    @Override
    public List sort(List objs, String path) {
        return null;
    }
    
    @Override
    public Sequence getSequence(String uid) {
        return null;
    }
    
    @Override
    public List<KrnObject> find(String path, Object value) {
        try {
            final Kernel krn = ClientOrLang.getKernel();
            Pair[] ps = Utils.parsePath(path, krn);
            if (ps.length == 1) {
                KrnAttribute attr = (KrnAttribute) ps[0].first;
                KrnClass cls = krn.getClassByName(path.substring(0, path.indexOf('.')));
                KrnObject[] objs = krn.getObjectsByAttribute(cls.id, attr.id, 0, ComparisonOperations.CO_EQUALS, value, 0);
                List<KrnObject> l = new ArrayList<KrnObject>(objs.length);
                for (int i = 0; i < objs.length; i++) {
                    l.add(objs[i]);
                }
                return l;
            } else {
                log.error("$Objects.find: Требуется путь с глубиной 1");
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    @Override
    public List getSqlResult(String sql, List vals, List params) {
        return null;
    }
    
    @Override
    public void removeObject(int id) {
    }

    
    @Override
    public String getUID(int id) {
        try {
            return ClientOrLang.getKernel().getUId(id);
        } catch (KrnException e) {
            log.error(e, e);
        }
        return "";
    }

    
    @Override
    public void setFilterParam(KrnObject filter, Map<String, Object> params) throws Exception {
        final Kernel krn = ClientOrLang.getKernel();
        final String fuid = filter.getUID();
        for (String pid : params.keySet()) {
            Object value = params.get(pid);
            if (value instanceof List) {
                krn.setFilterParam(fuid, pid, (List) value);
            } else if (value != null) {
                krn.setFilterParam(fuid, pid, Collections.singletonList(value));
            } else {
                krn.setFilterParam(fuid, pid, Collections.emptyList());
            }
        }
    }

    
    @Override
    public void clearFilterParam(KrnObject filter) throws KrnException {
        ClientOrLang.getKernel().clearFilterParams(filter.getUID());
    }

    
    @Override
    public List<Object> getFilterParam(String fuid, String pid) {
        try {
            final Kernel krn = ClientOrLang.getKernel();
            List<Object> res = krn.getFilterParam(fuid, pid);
            if (res != null) {
                List<Object> params = new ArrayList<Object>();
                for (int i = 0; i < res.size(); i++)
                    params.add(res.get(i));
                return params;
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Получить параметры фильтра.
     * 
     * @param fuid
     *            идентификатор фильтра.
     * @return карта параметров фильтра.
     */
    public Map getFilterParams(String fuid) {
        try {
            final Kernel krn = ClientOrLang.getKernel();
            Map res = krn.getFilterParams(fuid);
            if (res != null) {
                return (Map) res;
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<KrnAttribute> getAttributesByType(KrnClass cls, boolean inherited) throws KrnException {
        Kernel krn = ClientOrLang.getKernel();
        return krn.getAttributesByTypeId(cls.getId(), inherited);
    }

    @Override
    public boolean stopProcess(Activity activity) throws KrnException {
    	return ClientOrLang.getFrame().getSession().getTaskHelper().stopProcess(activity, false);
    }

    public boolean stopProcess(Activity activity, boolean forceCancel) throws KrnException {
    	return ClientOrLang.getFrame().getSession().getTaskHelper().stopProcess(activity, forceCancel);
    }
    
    /**
     * Создать запрос.
     * 
     * @param path
     *            путь к классу
     * @param lang
     *            язык
     * @return объект <code>SrvQuery</code>
     * @throws KrnException
     *             the krn exception
     */
    public ClientQuery createQuery(String path, KrnObject lang) throws KrnException {
        Cache cash = ClientOrLang.getFrame().getCash();
        return createQuery(path, lang, cash.getTransactionId());
    }

    public ClientQuery createQuery(String path, KrnObject lang, long tid) throws KrnException {
        Kernel krn = ClientOrLang.getKernel();
        return new ClientQuery(path, lang, krn, tid);
    }

    public ClientQuery createQuery(String path) throws KrnException {
    	return createQuery(path, null);
    }

    public ClientQuery createQuery(String path, long tid) throws KrnException {
    	return createQuery(path, null, tid);
    }
    
    @Override
    public Object createJavaObject(String className, Object... args) throws Exception {
    	log.info("ClassLoaderWeb2: " + ClientObjects.class.getClassLoader());
    	Class<?> cls = LangUtils.getType(className, null, ClientObjects.class.getClassLoader(), ClientObjects.class); 
        outer: for (Constructor<?> ctr : cls.getConstructors()) {
            Class<?>[] parTypes = ctr.getParameterTypes();
            if (parTypes.length == args.length) {
            	for (int i=0; i<args.length; i++) {
            		if (args[i] != null && !Funcs.isAssignableFrom(parTypes[i], args[i].getClass()))
            			continue outer;
            	}
            	switch (parTypes.length) {
            		case 0:
            			return ctr.newInstance();
            		case 1:
            			return ctr.newInstance(args[0]);
            		case 2:
            			return ctr.newInstance(args[0], args[1]);
            		case 3:
            			return ctr.newInstance(args[0], args[1], args[2]);
            		case 4:
            			return ctr.newInstance(args[0], args[1], args[2], args[3]);
            	}
               	return ctr.newInstance(args);
            }
        }
        throw new EvalException("Конструктор не найден");
    }
	@Override
	public String putRepositoryData(String paths, String fileName, byte[] data) {
		 try {
	            final Kernel krn = ClientOrLang.getKernel();
	            return krn.putRepositoryData(paths, fileName, data);
	        } catch (KrnException e) {
	            log.error(e, e);
	        }
		return null;
	}
	public byte[] getRepositoryData(String docId) {
		 try {
	            final Kernel krn = ClientOrLang.getKernel();
	            return krn.getRepositoryData(docId);
	        } catch (KrnException e) {
	            log.error(e, e);
	        }
		return null;
	}

	@Override
	public String getRepositoryItemName(String docId) throws Exception {
		 try {
	            final Kernel krn = ClientOrLang.getKernel();
	            return krn.getRepositoryItemName(docId);
	        } catch (KrnException e) {
	            log.error(e, e);
	        }
		return null;
	}

	@Override
	public String getRepositoryItemType(String docId) throws Exception {
		 try {
	            final Kernel krn = ClientOrLang.getKernel();
	            return krn.getRepositoryItemType(docId);
	        } catch (KrnException e) {
	            log.error(e, e);
	        }
		return null;
	}

	@Override
	public boolean dropRepositoryItem(String docId) throws Exception {
		 try {
	            final Kernel krn = ClientOrLang.getKernel();
	            return krn.dropRepositoryItem(docId);
	        } catch (KrnException e) {
	            log.error(e, e);
	        }
		return false;
	}

	@Override
	public List<String> searchByQuery(String searchName) throws Exception {
		 try {
	            final Kernel krn = ClientOrLang.getKernel();
	            return krn.searchByQuery(searchName);
	        } catch (KrnException e) {
	            log.error(e, e);
	        }
		return null;
	}
}
