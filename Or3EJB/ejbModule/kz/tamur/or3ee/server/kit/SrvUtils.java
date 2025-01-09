package kz.tamur.or3ee.server.kit;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

import oracle.jdbc.OraclePreparedStatement;
import kz.tamur.ods.Value;
import kz.tamur.ods.debug.ResourceRegistry;
import kz.tamur.or3.util.PathElement;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;

import com.cifs.or2.kernel.Date;
import com.cifs.or2.kernel.DateValue;
import com.cifs.or2.kernel.FloatValue;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.LongValue;
import com.cifs.or2.kernel.ObjectValue;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.kernel.Time;
import com.cifs.or2.kernel.TimeValue;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.db.ConnectionManager;
import com.cifs.or2.server.orlang.SrvOrLang;
import com.cifs.or2.server.orlang.SrvQuery;
import com.cifs.or2.server.orlang.SrvQueryCache;

import static kz.tamur.or3ee.common.SessionIds.*;

public class SrvUtils {
	private static InetAddress address;
    private final static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + SrvUtils.class.getName());

    public static Map<String, ThreadPoolExecutor> threadPools = java.util.Collections.synchronizedMap(new HashMap<String, ThreadPoolExecutor>()); 
    
	static {
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.error(e, e);
        }
	}
	
	private static Map<String, Pair<KrnAttribute, Integer>[]> pathes = 
			Collections.synchronizedMap(new HashMap<String, Pair<KrnAttribute, Integer>[]>());
	
	public static Session getSession(String dsName, KrnObject userObj,
			String ip, String pcName, boolean callbacks) throws KrnException {
		Session s = getSession();
		try {
			s.login(dsName, null, userObj, ip, pcName);
			return s;
		} catch (KrnException e) {
	        ResourceRegistry.instance().resourceReleased(s);
			throw e;
		} catch (Throwable e) {
	        ResourceRegistry.instance().resourceReleased(s);
			throw e;
		}
	}

	public static Session getSession(UserSession us) throws KrnException {
		if (ConnectionManager.instance().isReady())
			return getSession(us, true);
		else
			return null;
	}

	public static Session getSession(UserSession us, boolean withDBConnection) throws KrnException {
		Session s = getSession();
		try {
			s.login(us, withDBConnection);
			return s;
		} catch (KrnException e) {
	        ResourceRegistry.instance().resourceReleased(s);
			throw e;
		} catch (Throwable e) {
	        ResourceRegistry.instance().resourceReleased(s);
			throw e;
		}
	}

	public static Session getSession(UUID usId) throws KrnException {
    	UserSession us = Session.findUserSession(usId);
    	if (us != null && us.isAlive()) 
    		return SrvUtils.getSession(us);
    	else
    		throw new KrnException(0, "Сессия закрыта");
    }

	public static Session getSession(String dsName, String name, String passwd)
			throws KrnException {
		Session s = getSession();
		try {
			s.login(dsName, name, null, passwd, null, null, address.getHostAddress(), address.getHostName(), false);
			return s;
		} catch (KrnException e) {
	        ResourceRegistry.instance().resourceReleased(s);
			throw e;
		} catch (Throwable e) {
	        ResourceRegistry.instance().resourceReleased(s);
			throw e;
		}
	}

	public static Session getSessionWithoutDb(String dsName, String name)
			throws KrnException {
		Session s = getSession();
		try {
			s.loginWithoutDB(dsName, name, address.getHostAddress(), address.getHostName());
			return s;
		} catch (KrnException e) {
	        ResourceRegistry.instance().resourceReleased(s);
			throw e;
		} catch (Throwable e) {
	        ResourceRegistry.instance().resourceReleased(s);
			throw e;
		}
	}

	public static Session getSession() throws KrnException {
		Session s = new Session();
		return s;
	}

	public static void setString(PreparedStatement ps, int i, String val)
			throws SQLException {
		if (ps instanceof OraclePreparedStatement)
			((OraclePreparedStatement) ps).setFixedCHAR(i, val);
		else
			ps.setString(i, val);
	}

	public static void loadGlobalFilters(Session s, String f_name, Map<Long, Long> gf) {
		// Загрузка глобальных фильтров
		try {
			Set<Long> gfSet = new TreeSet<Long>();
			final KrnClass fCls = s.getClassByName("Filter");
			final KrnClass gfCls = s.getClassByName("FilterRoot");
			final KrnAttribute cnAttr = s.getAttributeByName(gfCls, "children");
			final KrnAttribute cnAttrName = s
					.getAttributeByName(gfCls, "title");
			final KrnAttribute cnAttrClass = s.getAttributeByName(gfCls,
					"className");
			KrnObject[] gfObjs = s.getClassObjects(gfCls, new long[0], 0);
			KrnObject[] gffObjs = s.getObjects(gfObjs[0].id, cnAttr.id,
					new long[0], 0);
			long[] gfObjIds = Funcs.makeObjectIdArray(gffObjs);
			StringValue[] gfNames = s.getStringValues(gfObjIds, cnAttrName.id,
					0, false, 0);
			if (gfObjIds != null && gfObjIds.length > 0) {
				gfObjIds = null;
				for (int i = 0; i < gfNames.length; ++i) {
					if (gfNames[i].value.equals("Системные")) {
						gffObjs = s.getObjects(gfNames[i].objectId, cnAttr.id,
								new long[0], 0);
						gfObjIds = Funcs.makeObjectIdArray(gffObjs);
						gfNames = s.getStringValues(gfObjIds, cnAttrName.id, 0,
								false, 0);
						break;
					}
				}
				if (gfObjIds != null && gfObjIds.length > 0) {
					gfObjIds = null;
					for (int i = 0; i < gfNames.length; ++i) {
						if (gfNames[i].value.equals(f_name)) {
							gffObjs = s.getObjects(gfNames[i].objectId,
									cnAttr.id, new long[0], 0);
							for (int j = 0; j < gffObjs.length; ++j) {
								if (gffObjs[j].classId == fCls.id) {
									gfSet.add(new Long(gffObjs[j].id));
								} else {
									loadFilters(s, gfSet, gffObjs[j].id,
											cnAttr.id, fCls.id);
								}
							}
							break;
						}
					}
				}
				gfObjIds = Funcs.makeLongArray(gfSet);
				gfNames = s.getStringValues(gfObjIds, cnAttrClass.id, 0, false,
						0);
				for (int i = 0; i < gfNames.length; i++) {
					if (gfSet.contains(new Long(gfNames[i].objectId))
							&& gfNames[i].index == 0) {
						final KrnClass cls = s.getClassByName(gfNames[i].value);
						gf.put(new Long(cls.id), new Long(gfNames[i].objectId));
					}
				}
			}
		} catch (KrnException ex) {
			log.error(ex, ex);
		}
	}

	public static void loadFilters(Session s, Set<Long> gfs, long objId, long attrId,
			long clsId) {
		try {
			KrnObject[] gffObjs = s.getObjects(objId, attrId, new long[0], 0);
			for (int j = 0; j < gffObjs.length; ++j) {
				if (gffObjs[j].classId == clsId) {
					gfs.add(gffObjs[j].id);
				} else {
					loadFilters(s, gfs, gffObjs[j].id, attrId, clsId);
				}
			}
		} catch (KrnException ex) {
			log.error(ex, ex);
		}
	}

	public static Pair<KrnAttribute, Integer>[] parsePath(Session s, String path)
			throws KrnException {
		if (path == null)
			return null;
		
		Pair<KrnAttribute, Integer>[] res = pathes.get(path);
		if (res != null)
			return res;
		
		StringTokenizer st = new StringTokenizer(path, ".");
		int count = st.countTokens();
		res = new Pair[(count == 0) ? 0 : count - 1];
		if (count > 0) {
			KrnClass cls = s.getClassByName(st.nextToken());
			for (int i = 0; i < count - 1; ++i) {
				PathElement pe = kz.tamur.util.Funcs.parseAttrName(st
						.nextToken());
				if(cls==null){
					System.out.println("path:"+path);
				}
				KrnAttribute attr = s.getAttributeByName(cls, pe.name);
				if (attr == null)
					return null;
				if (pe.index instanceof String) {
					pe.index = (attr.collectionType == 0) ? new Integer(0)
							: new Integer(-1);
				}
				res[i] = new Pair<KrnAttribute, Integer>(attr, (Integer)pe.index);
				if (pe.castClassName != null) {
					cls = s.getClassByName(pe.castClassName);
				} else {
					cls = s.getClassById(attr.typeClassId);
				}
			}
		}
		pathes.put(path, res);
		return res;
	}
	
	public static void expungeStalePathes(KrnClass cls) {
		synchronized (pathes) {
			Iterator<Entry<String, Pair<KrnAttribute, Integer>[]>> it = pathes.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Pair<KrnAttribute, Integer>[]> e = it.next();
				for (Pair<KrnAttribute, Integer> p : e.getValue()) {
					KrnAttribute a = p.first;
					if (a.classId == cls.id || a.typeClassId == cls.id) {
						it.remove();
						break;
					}
				}
			}
		}
	}

	public static void expungeStalePathes(KrnAttribute attr) {
		synchronized (pathes) {
			Iterator<Entry<String, Pair<KrnAttribute, Integer>[]>> it = pathes.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Pair<KrnAttribute, Integer>[]> e = it.next();
				for (Pair<KrnAttribute, Integer> p : e.getValue()) {
					KrnAttribute a = p.first;
					if (a.id == attr.id) {
						it.remove();
						break;
					}
				}
			}
		}
	}

	public static Pair<KrnObject, SortedMap<Integer, Object>> getFilteredObjectAttr(KrnObject obj, String path,
			long tid, long[] fids, Session s) throws KrnException {
		return getFilteredObjectAttr(obj, null, path, tid, fids, s);
	}
	
	public static Pair<KrnObject, SortedMap<Integer, Object>> getFilteredObjectAttr(KrnObject obj, List<KrnObject> parentObjs, String path,
			long tid, long[] fids, Session s) throws KrnException {
		Pair<KrnAttribute, Integer>[] ps = parsePath(s, path);
		SortedMap<Integer, Object> res = null;
		for (int i = 0; obj != null && i < ps.length - 1; i++) {
			if (parentObjs != null) parentObjs.add(obj);
			KrnAttribute attr = (KrnAttribute) ps[i].first;
			Object index = ps[i].second;
			res = getObjectAttr(obj.id, attr, 0, tid, s);
			obj = null;
			if (res.size() > 0) {
				if (index instanceof Number) {
					int last = ((Number) res.lastKey()).intValue();
					int ind = ((Number) index).intValue();
					if (ind < 0) {
						ind = last + ind + 1;
					}
					if (ind >= 0) {
						obj = (KrnObject) res.get(new Integer(ind));
					}
				}
			}
		}
		if (obj != null) {
			if (parentObjs != null) parentObjs.add(obj);
			KrnAttribute attr = (KrnAttribute) ps[ps.length - 1].first;
			res = getFilteredObjectAttr(obj.id, attr, tid, fids, s);
			return new Pair<KrnObject, SortedMap<Integer, Object>>(obj, res);
		}
		return null;
	}

	public static Pair<KrnObject, SortedMap<Integer, Object>> getObjectAttr(KrnObject obj, String path, long tid,
			long langId, Session s, boolean useQueryCache) throws KrnException {
		return getObjectAttr(obj, null, path, tid, langId, s, useQueryCache);
	}
	
	public static Pair<KrnObject, SortedMap<Integer, Object>> getObjectAttr(KrnObject obj, List<KrnObject> parentObjs, String path, long tid,
			long langId, Session s, boolean useQueryCache) throws KrnException {
		
		Pair<KrnAttribute, Integer>[] ps = parsePath(s, path);
		SortedMap<Integer, Object> res = null;

		int arrPos = path.indexOf('[');
		int lastPos = path.lastIndexOf('.');
		int castPos = path.indexOf('<');
		boolean hasCollectionAttr = false;
		for (int i = 0; i < ps.length - 1; i++) {
			if (ps[i].first.collectionType > 0) {
				hasCollectionAttr = true;
				break;
			}
		}
		
		if (hasCollectionAttr || castPos > -1 || (arrPos > -1 && lastPos > arrPos)) {
			for (int i = 0; obj != null && i < ps.length - 1; i++) {
				if (parentObjs != null) parentObjs.add(obj);
				KrnAttribute attr = ps[i].first;
				Object index = ps[i].second;
				res = getObjectAttr(obj.id, attr, langId, tid, s);
				obj = null;
				if (res.size() > 0) {
					if (index instanceof Number) {
						int last = ((Number) res.lastKey()).intValue();
						int ind = ((Number) index).intValue();
						if (ind < 0) {
							ind = last + ind + 1;
						}
						if (ind >= 0) {
							obj = (KrnObject) res.get(new Integer(ind));
						}
					}
				}
			}
			if (obj != null) {
				if (parentObjs != null) parentObjs.add(obj);
				KrnAttribute attr = (KrnAttribute) ps[ps.length - 1].first;
				res = getObjectAttr(obj.id, attr, langId, tid, s);
				return new Pair<KrnObject, SortedMap<Integer, Object>>(obj, res);
			}
		} else {
			path = path.replaceAll("\\[.*?\\]", "");
			
			SrvQuery sq = null;
			
			if (useQueryCache) {
				SrvQueryCache queryCache = SrvOrLang.getQueryCache();
				sq = queryCache != null ? queryCache.getQuery(path, langId, s, tid) : new SrvQuery(path, langId, s, tid);
			} else
				sq = new SrvQuery(path, langId, s, tid);
			
	        sq.execute(obj, s);
	        
	        StringBuilder tempPath = new StringBuilder(path.substring(0, path.indexOf('.')));
			for (int i = 0; obj != null && i < ps.length - 1; i++) {
				tempPath.append(".").append(ps[i].first.name); 
				if (parentObjs != null) parentObjs.add(obj);
				obj = (KrnObject) sq.getAttr(tempPath.toString());
			}
			if (obj != null) {
				if (parentObjs != null) parentObjs.add(obj);
				res = new TreeMap<Integer, Object>();
				
				Object vals = convertValue(sq.getAttr(path, langId));
				
				if (vals instanceof List) {
					List<Value> vs = (List<Value>)vals;
					for (int i = 0; i < vs.size(); i++) {
						Value v = vs.get(i);
						res.put(v.index, v.value);
					}
				} else {
					res.put(0, vals);
				}
				return new Pair<KrnObject, SortedMap<Integer, Object>>(obj, res);
			}
		}

		return null;
	}
	
	private static Object convertValue(Object vals) {
		if (vals instanceof Boolean) {
			return (Boolean) vals ? 1 : 0;
		} else if (vals instanceof Date) {
			return kz.tamur.util.Funcs.convertDate((Date)vals);
		} else if (vals instanceof Time) {
			return kz.tamur.util.Funcs.convertTime((Time)vals);
		} else {
			return vals;
		}
	}

	public static Map<Long, SortedMap<Integer, Object>> getObjectAttr(List<KrnObject> objs, String path, long tid,
			long langId, Session s) throws KrnException {
		Pair<KrnAttribute, Integer>[] ps = parsePath(s, path);
		Map<Long, Long> idToId = new HashMap<Long, Long>();
		Map<Long, SortedMap<Integer, Object>> res = null;
		
		for (int i = 0; objs != null && objs.size() > 0 && i < ps.length - 1; i++) {
			KrnAttribute attr = ps[i].first;
			Object index = ps[i].second;
			long[] ids = Funcs.makeObjectIdArray(objs);
			res = getObjectAttr(ids, attr, langId, tid, s);
			objs = new ArrayList<KrnObject>();
			if (i == 0) {
				for (long id : ids) {
					SortedMap<Integer, Object> vals = res.get(id);
					if (vals != null && vals.size() > 0) {
						if (index instanceof Number) {
							int last = ((Number) vals.lastKey()).intValue();
							int ind = ((Number) index).intValue();
							if (ind < 0) {
								ind = last + ind + 1;
							}
							if (ind >= 0) {
								KrnObject obj = (KrnObject) vals.get(new Integer(ind));
								idToId.put(id, obj.id);
								objs.add(obj);
							}
						}
					}
				}
			} else {
				for (long id : idToId.keySet()) {
					long newId = idToId.get(id);
					SortedMap<Integer, Object> vals = res.get(newId);
					if (vals != null && vals.size() > 0) {
						if (index instanceof Number) {
							int last = ((Number) vals.lastKey()).intValue();
							int ind = ((Number) index).intValue();
							if (ind < 0) {
								ind = last + ind + 1;
							}
							if (ind >= 0) {
								KrnObject obj = (KrnObject) vals.get(new Integer(ind));
								idToId.put(id, obj.id);
								objs.add(obj);
							}
						}
					}
				}
			}
		}
		if (objs != null && objs.size() > 0) {
			KrnAttribute attr = (KrnAttribute) ps[ps.length - 1].first;
			long[] ids = Funcs.makeObjectIdArray(objs);

			res = getObjectAttr(ids, attr, langId, tid, s);
			if (idToId.size() > 0) {
				Map<Long, SortedMap<Integer, Object>> finalRes 
							= new HashMap<Long, SortedMap<Integer,Object>>();
				for (long id : idToId.keySet()) {
					long newId = idToId.get(id);
					finalRes.put(id, res.get(newId));
				}
				return finalRes;
			}
			return res;
		}
		return null;
	}

	public static Map<Long, SortedMap<Integer, Object>> getObjectAttr(long[] objIds,
			KrnAttribute attr, long langId, long trId, Session s)
			throws KrnException {
		Map<Long, SortedMap<Integer, Object>> res = new HashMap<Long, SortedMap<Integer, Object>>();
		switch ((int) attr.typeClassId) {
		case CID_STRING:
		case CID_MEMO: {
			boolean isMemo = attr.typeClassId == CID_MEMO;
			long lid = attr.isMultilingual ? langId : 0;
			StringValue[] svs = s.getStringValues(objIds, attr.id, lid, isMemo,
					trId);
			for (int i = 0; i < svs.length; i++) {
				StringValue sv = svs[i];
				put(res, sv.objectId, sv.index, sv.value);
			}
			break;
		}
		case CID_INTEGER:
		case CID_BOOL: {
			LongValue[] lvs = s.getLongValues(objIds, attr.id, trId);
			for (int i = 0; i < lvs.length; i++) {
				LongValue lv = lvs[i];
				put(res, lv.objectId, lv.index, lv.value);
			}
			break;
		}
		case CID_FLOAT: {
			FloatValue[] fvs = s.getFloatValues(objIds, attr.id, trId);
			for (int i = 0; i < fvs.length; i++) {
				FloatValue fv = fvs[i];
				put(res, fv.objectId, fv.index, fv.value);
			}
			break;
		}
		case CID_DATE: {
			DateValue[] dvs = s.getDateValues(objIds, attr.id, trId);
			for (int i = 0; i < dvs.length; i++) {
				DateValue dv = dvs[i];
				put(res, dv.objectId, dv.index, kz.tamur.util.Funcs.convertDate(dv.value));
			}
			break;
		}
		case CID_TIME: {
			TimeValue[] tvs = s.getTimeValues(objIds, attr.id, trId);
			for (int i = 0; i < tvs.length; i++) {
				TimeValue tv = tvs[i];
				put(res, tv.objectId, tv.index, kz.tamur.util.Funcs.convertTime(tv.value));
			}
			break;
		}
		case CID_BLOB: {
			long lid = attr.isMultilingual ? langId : 0;
			for (int i = 0; i < objIds.length; i++) {
				byte[] data = s.getBlob(objIds[i], attr.id, 0, lid, trId);
				put(res, objIds[i], 0, data);
			}
			break;
		}
		default: {
			ObjectValue[] ovs = s.getObjectValues(objIds, attr.id, new long[0],
					trId);
			for (int i = 0; i < ovs.length; i++) {
				ObjectValue ov = ovs[i];
				put(res, ov.objectId, ov.index, ov.value);
			}
		}
		}
		return res;
	}
	
	public static SortedMap<Integer, Object> getObjectAttr(long objId,
			KrnAttribute attr, long langId, long trId, Session s)
			throws KrnException {
		SortedMap<Integer, Object> res = new TreeMap<Integer, Object>();
		long[] objIds = { objId };
		switch ((int) attr.typeClassId) {
		case CID_STRING:
		case CID_MEMO: {
			boolean isMemo = attr.typeClassId == CID_MEMO;
			long lid = attr.isMultilingual ? langId : 0;
			StringValue[] svs = s.getStringValues(objIds, attr.id, lid, isMemo,
					trId);
			for (int i = 0; i < svs.length; i++) {
				StringValue sv = svs[i];
				res.put(sv.index, sv.value);
			}
			break;
		}
		case CID_INTEGER:
		case CID_BOOL: {
			LongValue[] lvs = s.getLongValues(objIds, attr.id, trId);
			for (int i = 0; i < lvs.length; i++) {
				LongValue lv = lvs[i];
				res.put(lv.index, new Long(lv.value));
			}
			break;
		}
		case CID_FLOAT: {
			FloatValue[] fvs = s.getFloatValues(objIds, attr.id, trId);
			for (int i = 0; i < fvs.length; i++) {
				FloatValue fv = fvs[i];
				res.put(fv.index, new Double(fv.value));
			}
			break;
		}
		case CID_DATE: {
			DateValue[] dvs = s.getDateValues(objIds, attr.id, trId);
			for (int i = 0; i < dvs.length; i++) {
				DateValue dv = dvs[i];
				res.put(dv.index, kz.tamur.util.Funcs.convertDate(dv.value));
			}
			break;
		}
		case CID_TIME: {
			TimeValue[] tvs = s.getTimeValues(objIds, attr.id, trId);
			for (int i = 0; i < tvs.length; i++) {
				TimeValue tv = tvs[i];
				res.put(new Integer(tv.index), kz.tamur.util.Funcs
						.convertTime(tv.value));
			}
			break;
		}
		case CID_BLOB: {
			long lid = attr.isMultilingual ? langId : 0;
			for (int i = 0; i < objIds.length; i++) {
				byte[] data = s.getBlob(objIds[i], attr.id, 0, lid, trId);
				res.put(i, data);
			}
			break;
		}
		default: {
			ObjectValue[] ovs = s.getObjectValues(objIds, attr.id, new long[0],
					trId);
			for (int i = 0; i < ovs.length; i++) {
				ObjectValue ov = ovs[i];
				res.put(new Integer(ov.index), ov.value);
			}
		}
		}
		return res;
	}

	private static SortedMap<Integer, Object> getFilteredObjectAttr(long objId,
			KrnAttribute attr, long trId, long[] fids, Session s)
			throws KrnException {
		SortedMap<Integer, Object> res = new TreeMap<Integer, Object>();
		long[] objIds = { objId };

		ObjectValue[] ovs = s.getObjectValues(objIds, attr.id, fids, trId);
		for (int i = 0; i < ovs.length; i++) {
			ObjectValue ov = ovs[i];
			res.put(new Integer(ov.index), ov.value);
		}
		return res;
	}

	private static void put(Map<Long, SortedMap<Integer, Object>> res,
			long objectId, int index, Object value) {
		SortedMap<Integer, Object> m = res.get(objectId);
		if (m == null) {
			m = new TreeMap<Integer, Object>();
			res.put(objectId, m);
		}
		m.put(index, value);
	}
}
