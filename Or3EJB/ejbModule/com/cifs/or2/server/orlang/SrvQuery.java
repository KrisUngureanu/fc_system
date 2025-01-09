package com.cifs.or2.server.orlang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kz.tamur.ods.AttrRequest;
import kz.tamur.ods.AttrRequestCache;
import kz.tamur.ods.Value;

import com.cifs.or2.client.util.ClientQuery;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.QueryResult;
import com.cifs.or2.server.Session;

public class SrvQuery {
	
	private Session session;
	private long tid;
	private AttrRequest areq;
	private String clsName;
	private QueryResult qres;
	private Map<String, Integer> indexes = new HashMap<String, Integer>();
	private Map<String, AttrRequest> chreqs = new HashMap<String, AttrRequest>();
	
	private AttrRequestCache reqCache = new AttrRequestCache();
	
	public SrvQuery(String path, KrnObject lang, Session session) throws KrnException {
		this(path, lang, session, 0);
	}
	
	public SrvQuery(String path, KrnObject lang, Session session, long tid) throws KrnException {
		this(path, lang != null ? lang.id : 0, session, tid);
	}

	public SrvQuery(String path, long langId, Session session) throws KrnException {
		this(path, langId, session, 0);
	}

	public SrvQuery(String path, long langId, Session session, long tid) throws KrnException {
		this.session = session;
		this.tid = tid;
		areq = new AttrRequest(null);
		clsName = path.substring(0, path.indexOf('.'));
		chreqs.put(clsName, areq);
		addPath(path, langId);
	}

	public SrvQuery addPath(String path) throws KrnException {
		return addPath(path, null);
	}

	public SrvQuery addPath(String path, KrnObject lang) throws KrnException {
		return addPath(path, lang != null ? lang.id : 0);
	}
	
	public SrvQuery addPath(String path, long langId) throws KrnException {
		
		reqCache.clear();
		
		String key = langId > 0 ? path + "_" + langId : path;
		if (chreqs.containsKey(key))
			return this;
		
		AttrRequest req = null;
		int dotPos = path.lastIndexOf('.');
		while (dotPos != -1) {
			req = chreqs.get(path.substring(0, dotPos));
			if (req != null)
				break;
			dotPos = path.lastIndexOf('.', dotPos - 1);
		}
		KrnAttribute attr = session.getAttributeById(req.attrId);
		while (dotPos != -1) {
			KrnClass cls = attr != null ? session.getClassById(attr.typeClassId) : session.getClassByName(path.substring(0, path.indexOf('.')));
			if (cls == null)
				break;
			int beg = dotPos + 1;
			dotPos = path.indexOf('.', dotPos + 1);
			int end = dotPos != -1 ? dotPos : path.length();
			attr = session.getAttributeByName(cls, path.substring(beg, end));
			if (attr == null)
				break;
			req = new AttrRequest(req);
			req.attrId = attr.id;
			if (dotPos == -1) {
				if (langId > 0)
					req.langId = langId;
				chreqs.put(key, req);
				indexes.put(key, indexes.size() + 2);
			} else {
				String tempKey = path.substring(0, dotPos);
				chreqs.put(tempKey, req);
				indexes.put(tempKey, indexes.size() + 2);
			}
		}
		return this;
	}
	
	private void arrangeIndexes(AttrRequest req, String parentPath) throws KrnException {
		String key = parentPath;
		if (req.attrId > 0) {
			KrnAttribute attr = session.getAttributeById(req.attrId);
			key += "." + attr.name;
			if (req.langId > 0 && attr.isMultilingual)
				key += "_" + req.langId;

			indexes.put(key, indexes.size() + 2);
		}
		for (AttrRequest child : req.getChildren()) {
			arrangeIndexes(child, key);
		}
	}
	
	public void execute(KrnObject obj) throws KrnException {
		indexes.clear();
		arrangeIndexes(areq, clsName);
		qres = session.getObjects(new long[] { obj.id }, areq, reqCache, tid);
	}
	
	public void execute(KrnObject obj, Session s) throws KrnException {
		qres = s.getObjects(new long[] { obj.id }, areq, reqCache, tid);
	}

	public void execute(List<KrnObject> objs) throws KrnException {
    	long[] ids = new long[objs.size()];
    	
    	for (int i=0; i<ids.length; i++) {
    		ids[i] = objs.get(i).id;
    	}

		qres = session.getObjects(ids, areq, reqCache, tid);
	}
	
	public void execute(List<KrnObject> objs, Session s) throws KrnException {
    	long[] ids = new long[objs.size()];
    	
    	for (int i=0; i<ids.length; i++) {
    		ids[i] = objs.get(i).id;
    	}

    	qres = s.getObjects(ids, areq, reqCache, tid);
	}

	public Object getAttr(String path) {
		return getAttr(0, path, 0);
	}
	
	public Object getAttr(int row, String path) {
		return getAttr(row, path, 0);
	}

	public Object getAttr(String path, KrnObject lang) {
		return getAttr(0, path, lang != null ? lang.id : 0);
	}
	
	public Object getAttr(int row, String path, KrnObject lang) {
		return getAttr(row, path, lang != null ? lang.id : 0);
	}

	public Object getAttr(String path, long langId) {
		return getAttr(0, path, langId);
	}
	
	public Object getAttr(int row, String path, long langId) {
		if (qres.rows.size() > row) {
			String key = langId > 0 ? path + "_" + langId : path;
			Integer index = indexes.get(key);
			return index != null ? ClientQuery.convertDate(qres.rows.get(row)[index]) : null;
		}
		return null;
	}

	public KrnObject getObjectAttr(String path) {
		return getObjectAttr(path, null);
	}
	
	public KrnObject getObjectAttr(String path, KrnObject defaultValue) {
		Object value = getAttr(path, null);
		return value instanceof KrnObject ? (KrnObject)value : defaultValue;
	}

	public String getStringAttr(String path) {
		return getStringAttr(path, (String)null);
	}
	
	public String getStringAttr(String path, String defaultValue) {
		Object value = getAttr(path, null);
		return value instanceof String ? (String)value : defaultValue;
	}

	public String getStringAttr(String path, KrnObject lang) {
		Object value = getAttr(path, lang);
		return value instanceof String ? (String)value : null;
	}

	public boolean getBooleanAttr(String path, boolean defaultValue) {
		Object value = getAttr(path, null);
		return value instanceof Boolean ? ((Boolean)value).booleanValue() : 
				value instanceof Number ? ((Number)value).longValue() == 1 : defaultValue;
	}

	public KrnObject[] getObjectArrayAttr(String path) {
		List<Value> values = (List<Value>)getAttr(path, null);
		if (values != null) {
			KrnObject[] res = new KrnObject[values.size()];
			for (int i = 0; i < res.length; i++) {
				res[i] = (KrnObject)values.get(i).value;
			}
			return res;
		}
		return null;
	}
	
	public void setTransactionId(long trId) {
		this.tid = trId;
	}
}
