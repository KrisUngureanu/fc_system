package com.cifs.or2.server.orlang;

import java.util.HashMap;
import java.util.Map;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.server.Session;

import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.util.Pair;

public class SrvQueryCache {
	
	private Map<Long, SrvQuery> queries;
	private KrnAttribute attr;
	private Integer index;
	private String path;
	
	public SrvQuery getQuery(String path, long langId, Session session, long tid) throws KrnException {
		if (queries == null) {
			queries = new HashMap<Long, SrvQuery>();
		}
		SrvQuery query = queries.get(langId);
		if (query == null) {
			query = new SrvQuery(path, langId, session, tid);
			queries.put(langId, query);
		} else {
			query.setTransactionId(tid);
		}
		return query;
	}

	public void init(String path, Session session) throws KrnException {
		if (attr == null || !path.equals(this.path)) {
	    	Pair<KrnAttribute, Integer>[] ps = SrvUtils.parsePath(session, path);
	    	Pair<KrnAttribute, Integer> p = ps[ps.length - 1];
	        attr = p.first;
	        index = p.second;
	        if (queries != null) {
	        	queries.clear();
	        }
	        this.path = path;
		}
	}
	
	public KrnAttribute getAttribute() {
		return attr;
	}
	
	public Integer getIndex() {
		return index;
	}
}
