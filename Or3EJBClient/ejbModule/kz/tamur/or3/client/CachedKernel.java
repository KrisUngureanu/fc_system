package kz.tamur.or3.client;

import static kz.tamur.util.CollectionTypes.COLLECTION_NONE;
import static kz.tamur.util.CollectionTypes.COLLECTION_ARRAY;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.ods.AttrRequest;
import kz.tamur.or3.client.cache.LocalCache;
import kz.tamur.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.LongValue;
import com.cifs.or2.kernel.ObjectValue;
import com.cifs.or2.kernel.QueryResult;
import com.cifs.or2.kernel.StringValue;

public class CachedKernel extends Kernel {

    private static final Log log = LogFactory.getLog(CachedKernel.class);
    
	LocalCache cache;
	String cacheConfPath;
	
	public CachedKernel(String cacheConfPath) {
		super();
		this.cacheConfPath = cacheConfPath;
	}

	private LocalCache getCache() {
		if (cache == null) {
			try {
			cache = new LocalCache(this, cacheConfPath);
			cache.init();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return cache;
	}

    @Override
    public synchronized ClassNode getClassNode(long id) throws KrnException {
        ClassNode cnode = cnodesByClassId_.get(new Long(id));
        if (cnode == null) {
            cnode = new ClassNode(getCache().getClassById(id));
            addClass(cnode);
        }
        return cnode;
    }

    @Override
    public synchronized ClassNode getClassNodeByName(String name)
            throws KrnException {
        ClassNode cnode = cnodesByClassName_.get(name);
        if (cnode == null) {
            cnode = new ClassNode(getCache().getClassByName(name));
	        addClass(cnode);
        }
        return cnode;
    }
	
    @Override
    public synchronized KrnClass[] getClasses(long clsId) throws KrnException {
    	List<KrnClass> clss = getCache().getSubClasses(clsId);
    	return clss.toArray(new KrnClass[clss.size()]);
    }
    
    @Override
    public synchronized void getClasses(Collection<ClassNode> cnodes) throws KrnException{
    	if (!allClassesReceived) {
        	List<KrnClass> clss = getCache().getAllClasses();
        	for (KrnClass cls : clss) {
                ClassNode cnode = cnodesByClassId_.get(cls.id);
                if (cnode == null) {
                	cnode = new ClassNode(cls);
                	addClass(cnode);
                }
                cnodes.add(cnode);
        	}
        	allClassesReceived = true;
    	} else {
    		cnodes.addAll(cnodesByClassId_.values());
    	}
    }

    @Override
    public synchronized KrnAttribute getAttributeById(long id)
            throws KrnException {
    	KrnAttribute attr = attrById.get(id);
    	if (attr == null) {
    		attr = getCache().getAttributeById(id);
    		attrById.put(id, attr);
    	}
    	return attr;
    }

    @Override
    public synchronized KrnAttribute[] getAttributesByClassId(KrnClass cls) throws KrnException {
        List<KrnAttribute> res = getCache().getAttributeByClassId(cls.id, true);
        return res.toArray(new KrnAttribute[res.size()]);
    }

    @Override
    public synchronized List<KrnAttribute> getAttributesByTypeId(long classId, boolean inherited) throws KrnException {
        return getCache().getAttributeByTypeId(classId, inherited);
    }
    
    @Override
    public synchronized KrnAttribute[] getRevAttributes(long attrId) throws KrnException {
        Long id = new Long(attrId);
        KrnAttribute[] res = revAttrs.get(id);
        if (res == null) {
        	List<KrnAttribute> rattrs = getCache().getRevAttributes(attrId);
        	res = rattrs.toArray(new KrnAttribute[rattrs.size()]);
            revAttrs.put(id, res);
        }
        return res;
    }
    
    @Override
    public synchronized KrnMethod getMethodById(String id) throws KrnException {
    	return getCache().getMethodByUid(id);
    }

    @Override
    public synchronized KrnMethod[] getMethods(long clsId) throws KrnException {
        List<KrnMethod> res = getCache().getMethodsByClassId(clsId);
        return res.toArray(new KrnMethod[res.size()]);
    }

    @Override
    public synchronized ASTStart getMethodTemplate(KrnMethod m) throws Throwable {
    	ASTStart expr = exprByMethodUid_.get(m.uid);
    	if (expr == null) {
			byte[] bs = getCache().getMethodExpression(m.uid);
            try {
            	expr = OrLang.createStaticTemplate(new InputStreamReader(
                		new ByteArrayInputStream(bs), "UTF-8"));
			} catch (Throwable e) {
				log.error("Ошибка в коде метода '" + getClassNode(m.classId).getName() + "." + m.name + "'", e);
				throw e;
			}
            exprByMethodUid_.put(m.uid, expr);
    	}
		return expr;
    }

    @Override
    public synchronized KrnObject[] getClassObjects(KrnClass cls, long tid)
            throws KrnException {
		LocalCache cache = getCache();
    	if (cache.isCachedClass(cls.id)) {
    		List<KrnObject> objs = cache.getClassObjects(cls);
    		return objs.toArray(new KrnObject[objs.size()]);
    	}
        return s_.getClassObjects(us.id, cls, new long[0], tid);
    }

    @Override
	public synchronized KrnObject[] getClassObjects(KrnClass cls,
			long[] filterIds, int[] limit, long tid) throws KrnException {
		if (filterIds.length == 0 && limit[0] == 0) {
			return getClassObjects(cls, tid);
		}
		return super.getClassObjects(cls, filterIds, limit, tid);
	}

    @Override
    public List<Object[]> getObjects(long[] objIds, AttrRequest req, long tid) throws KrnException {
		LocalCache cache = getCache();
    	KrnAttribute attr = getAttributeById(req.getChildren().get(0).attrId);
    	if (cache.isCachedClass(attr.classId)) {
    		return cache.getObjects(0, objIds, req, this);
    	}
    	return s_.getObjects(us.id, objIds, req, tid).rows;
    }
    
	@Override
	public List<Object[]> getClassObjects(
			KrnClass cls,
			AttrRequest req,
			long[] filterIds,
			int[] limit,
			long tid
	) throws KrnException {
		return getClassObjects(cls, req, filterIds, limit, tid, null);
	}
	
	@Override
	public List<Object[]> getClassObjects(
			KrnClass cls,
			AttrRequest req,
			long[] filterIds,
			int[] limit,
			long tid,
			String info
	) throws KrnException {
		if (filterIds.length == 0) {
			LocalCache cache = getCache();
	    	if (cache.isCachedClass(cls.id)) {
	    		return cache.getObjects(cls.id, null, req, this);
	    	}
		}
		if (info != null && user_ != null)
			info = "USER:" + user_.getName() + " " + info;
		QueryResult res = s_.getClassObjects3(us.id, cls, req, filterIds, limit[0], tid, info);
		limit[0] = res.totalRows;
		return res.rows;
	}

	@Override
    public synchronized KrnObject[] getObjects(
            KrnObject obj,
            KrnAttribute attr,
            long[] filterIds,
            long tid
            ) throws KrnException {
    	if (filterIds.length == 0) {
			LocalCache cache = getCache();
			KrnAttribute rattr = attr.rAttrId > 0 ? getAttributeById(attr.rAttrId) : null;
			if ((rattr != null && rattr.collectionType == COLLECTION_NONE && cache.isCachedClass(rattr.classId))
					|| (cache.isCachedClass(obj.classId)
					&& (attr.typeClassId < 99 || cache.isCachedClass(attr.typeClassId)))) {
				List<Object> vals = cache.getAttributeValues(obj.id, attr, 0, this);
				KrnObject[] res = new KrnObject[vals.size()];
				for (int i = 0; i < vals.size(); i++)
					res[i] = (KrnObject)vals.get(i);
	    		return res;
			}
    	}
        return s_.getObjects(us.id, obj.id, attr.id, filterIds, tid);
    }

    public synchronized ObjectValue[] getObjectValues(
            long[] objIds, KrnAttribute attr, long tid) throws KrnException {
        return getObjectValues(objIds, attr.classId, attr, new long[0], new int[1], tid);
    }

    @Override
    public synchronized ObjectValue[] getObjectValues(
            long[] objIds,
            long classId,
            KrnAttribute attr,
            long[] filterIds,
            int[] limit,
            long tid
            ) throws KrnException {
    	if (filterIds.length == 0) {
			LocalCache cache = getCache();
			KrnAttribute rattr = attr.rAttrId > 0 ? getAttributeById(attr.rAttrId) : null;
			if ((rattr != null && rattr.collectionType == COLLECTION_NONE && cache.isCachedClass(rattr.classId))
					|| (cache.isCachedClass(classId)
					&& (attr.typeClassId < 99 || cache.isCachedClass(attr.typeClassId))
					&& attr.collectionType != COLLECTION_ARRAY)) {
				List<ObjectValue> res = new ArrayList<ObjectValue>();
				for(long objId : objIds) {
					List<Object> vals = cache.getAttributeValues(objId, attr, 0, this);
					for (int i = 0; i < vals.size(); i++)
						res.add(new ObjectValue(objId, i, (KrnObject)vals.get(i), 0));
				}
				return res.toArray(new ObjectValue[res.size()]);
			}
    	}
        return s_.getObjectValues(us.id, objIds, attr.id, filterIds, limit, tid);
    }

    @Override
    public synchronized LongValue[] getLongValues(long[] objIds,
    		KrnAttribute attr, long tid)
    				throws KrnException {
		LocalCache cache = getCache();
		if (cache.isCachedClass(attr.classId)) {
			List<LongValue> res = new ArrayList<LongValue>();
			for(long objId : objIds) {
				List<Object> vals = cache.getAttributeValues(objId, attr, 0, this);
				for (int i = 0; i < vals.size(); i++) {
					long v = 0;
					Object ov = vals.get(i);
					if (ov instanceof Number)
						v = ((Number)ov).longValue();
					else if (ov instanceof Boolean)
						v = ((Boolean)ov).booleanValue() ? 1 : 0;
					res.add(new LongValue(objId, i, v));
				}
			}
			return res.toArray(new LongValue[res.size()]);
		}
    	return s_.getLongValues(us.id, objIds, (int)attr.id, tid);
    }

    @Override
    public synchronized StringValue[] getStringValues(
            long[] objIds,
            KrnAttribute attr,
            long langId,
            boolean isMemo,
            long tid
            ) throws KrnException {
		LocalCache cache = getCache();
		if (cache.isCachedClass(attr.classId)
				&& attr.collectionType != COLLECTION_ARRAY) {
			List<StringValue> res = new ArrayList<StringValue>();
			for(long objId : objIds) {
				List<Object> vals = cache.getAttributeValues(objId, attr, langId, this);
				for (int i = 0; i < vals.size(); i++)
					res.add(new StringValue(objId, i, (String)vals.get(i)));
			}
			return res.toArray(new StringValue[res.size()]);
		}
        return s_.getStringValues(us.id, objIds, (int)attr.id, langId, isMemo, tid);
    }

    @Override
	public synchronized String[] getStrings(KrnObject obj, KrnAttribute attr,
			long langId, long tid) throws KrnException {
		if (attr.isMultilingual && langId == 0) {
			langId = getDataLanguage().id;
		}
		LocalCache cache = getCache();
		if (cache.isCachedClass(obj.classId)
				&& attr.rAttrId == 0 && attr.collectionType == COLLECTION_NONE) {
			String str = (String)cache.getAttributeValue(obj.id, attr, langId);
			return str != null ? new String[] { str } : new String[0];
		}
		String[] strings = s_.getStrings(us.id, obj.id, attr.id, langId, false,
				tid);
		return strings;
	}

    @Override
	public synchronized byte[] getBlob(long objId, KrnAttribute attr,
			int index, long langId, long tid) throws KrnException {
		try {
			LocalCache cache = getCache();
			ClassNode cn = getClassNode(attr.classId);
			if (cache.isCachedClass(cn.getId())) {
				return (byte[])getCache().getAttributeValue(objId, attr, langId);
			} else {
				byte[] data = s_.getBlob(us.id, objId, attr.id, index, langId, tid, true);
				if (data.length > 0) {
					return DataUtil.decompress(data);
				}
				return data;
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new KrnException(0, e.getMessage());
		}
	}
}
