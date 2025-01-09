package com.cifs.or2.client.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kz.tamur.ods.AttrRequest;
import kz.tamur.ods.Value;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.Date;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.Time;

public class ClientQuery {
	
	private Kernel krn;
	private long tid;
	private AttrRequest areq;
	private String clsName;
	private List<Object[]> rows;
	private Map<String, Integer> indexes = new HashMap<String, Integer>();
	private Map<String, AttrRequest> chreqs = new HashMap<String, AttrRequest>();
	
	public ClientQuery(String path, KrnObject lang, Kernel krn) throws KrnException {
		this(path, lang, krn, 0);
	}
	
	public ClientQuery(String path, KrnObject lang, Kernel krn, long tid) throws KrnException {
		this(path, lang != null ? lang.id : 0, krn, tid);
	}

	public ClientQuery(String path, long langId, Kernel krn) throws KrnException {
		this(path, langId, krn, 0);
	}

	public ClientQuery(String path, long langId, Kernel krn, long tid) throws KrnException {
		this.krn = krn;
		this.tid = tid;
		areq = new AttrRequest(null);
		clsName = path.substring(0, path.indexOf('.'));
		chreqs.put(clsName, areq);
		addPath(path, langId);
	}

	public ClientQuery addPath(String path) throws KrnException {
		return addPath(path, null);
	}

	public ClientQuery addPath(String path, KrnObject lang) throws KrnException {
		return addPath(path, lang != null ? lang.id : 0);
	}
	
	public ClientQuery addPath(String path, long langId) throws KrnException {
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
		KrnAttribute attr = krn.getAttributeById(req.attrId);
		while (dotPos != -1) {
			KrnClass cls = attr != null ? krn.getClass(attr.typeClassId) : krn.getClassByName(path.substring(0, path.indexOf('.')));
			if (cls == null)
				break;
			int beg = dotPos + 1;
			dotPos = path.indexOf('.', dotPos + 1);
			int end = dotPos != -1 ? dotPos : path.length();
			attr = krn.getAttributeByName(cls, path.substring(beg, end));
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
			KrnAttribute attr = krn.getAttributeById(req.attrId);
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
		execute(obj, krn);
	}
	
	public void execute(KrnObject obj, Kernel krn) throws KrnException {
		indexes.clear();
		arrangeIndexes(areq, clsName);
		rows = krn.getObjects(new long[] { obj.id }, areq, tid);
	}

	public Object getAttr(String path) {
		return getAttr(path, 0);
	}
	
	public Object getAttr(String path, KrnObject lang) {
		return getAttr(path, lang != null ? lang.id : 0);
	}
	
	public Object getAttr(String path, long langId) {
		if (rows.size() > 0) {
			String key = langId > 0 ? path + "_" + langId : path;
			Integer index = indexes.get(key);
			return index != null ? convertDate(rows.get(0)[index]) : null;
		}
		return null;
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
	
	public static Object convertDate(Object val) {
		if (val instanceof Date)
			return kz.tamur.util.Funcs.convertDate((Date)val);
		else if (val instanceof Time)
			return kz.tamur.util.Funcs.convertTime((Time)val);
		else
			return val;
	}

}
