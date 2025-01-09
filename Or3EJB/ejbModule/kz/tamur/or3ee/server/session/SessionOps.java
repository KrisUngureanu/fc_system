package kz.tamur.or3ee.server.session;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.Stack;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import kz.tamur.comps.Constants;
import kz.tamur.comps.TriggerInfo;
import kz.tamur.lang.parser.EvaluatorVisitor;
import kz.tamur.ods.AttrRequest;
import kz.tamur.ods.Driver2;
import kz.tamur.ods.Lock;
import kz.tamur.ods.Value;
import kz.tamur.or3.util.SystemAction;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.admin.ServerMessage;
import kz.tamur.or3ee.server.kit.DocumentConverter;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.server.indexer.Indexer;
import kz.tamur.server.plugins.OrdersPlugin;
import kz.tamur.server.wf.ExecutionComponent;
import kz.tamur.util.CacheChangeRecord;
import kz.tamur.util.DataUtil;

import com.cifs.or2.client.OrlangTriggerInfo;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.AnyPair;
import com.cifs.or2.kernel.BlobValue;
import com.cifs.or2.kernel.DataChanges;
import com.cifs.or2.kernel.Date;
import com.cifs.or2.kernel.DateValue;
import com.cifs.or2.kernel.FilterDate;
import com.cifs.or2.kernel.FloatValue;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnVcsChange;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnIndex;
import com.cifs.or2.kernel.KrnIndexKey;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnSearchResult;
import com.cifs.or2.kernel.LongPair;
import com.cifs.or2.kernel.LongValue;
import com.cifs.or2.kernel.ModelChanges;
import com.cifs.or2.kernel.Note;
import com.cifs.or2.kernel.ObjectValue;
import com.cifs.or2.kernel.ProjectConfiguration;
import com.cifs.or2.kernel.QueryResult;
import com.cifs.or2.kernel.ReportData;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.kernel.SuperMap;
import com.cifs.or2.kernel.Time;
import com.cifs.or2.kernel.TimeValue;
import com.cifs.or2.kernel.UserSessionValue;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.ServerUserSession;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.db.ConnectionManager;
import com.cifs.or2.server.db.Database;
import com.cifs.or2.server.orlang.SrvOrLang;

/**
 * Session Bean implementation class SessionOps
 */
@Stateless(mappedName="SessionOps")
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class SessionOps implements SessionOpsRemote, SessionOpsLocal {
	private String randomStr = null;
    private final static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + SessionOps.class.getName());

	protected UserSession us;
	
	private Map<Long, Session> terminalThreads = new HashMap<>();

    /**
     * Default constructor. 
     */
    public SessionOps() {}
    
    @Override
    public UserSessionValue login(
    		String dsName,
    		String name,
    		String typeClient,
    		String path,
    		String newPass,
    		String confPass,
    		String ip,
    		String pcName,
    		boolean callbacks,
    		boolean force, boolean sLogin
    ) throws KrnException {
    	return login(dsName, name, typeClient, path, newPass, confPass, ip, pcName, callbacks, force, sLogin, false, null);
    }

    @Override
    public UserSessionValue login(
    		String dsName,
    		String name,
    		String typeClient,
    		String path,
    		String newPass,
    		String confPass,
    		String ip,
    		String pcName,
    		boolean callbacks,
    		boolean force, boolean sLogin,
    		boolean isUseECP, String signedData
    ) throws KrnException {
    	Session s = new Session();
    	try {
	    	UserSession us = (dsName != null && dsName.length() > 0) 
	    			? s.login(dsName, name, typeClient, path, newPass, confPass, ip, pcName, callbacks, force, sLogin, isUseECP, signedData)
	    			: s.login(name, typeClient, path, ip, pcName);
	    			
	    	if (Constants.CLIENT_TYPE_LOCALWEB.equals(typeClient))
	    		this.us = us;
	    	return Session.createValueObject(us);
    	} finally {
    		s.close();
    	}
    }
    
    @Override
    public UserSessionValue loginWithCert(String dsName, String name, String typeClient, String signedStr, String ip, String pcName, boolean callbacks) throws KrnException {
    	Session s = new Session();
    	try {
    		UserSession us = s.loginWithCert(dsName, name, typeClient, signedStr, ip, pcName, callbacks);
	    	if (Constants.CLIENT_TYPE_LOCALWEB.equals(typeClient))
	    		this.us = us;
	    	return Session.createValueObject(us);
    	} finally {
    		s.close();
    	}
    }

    @Override
    public UserSessionValue loginWithDN(String dsName, String dn, String typeClient, String ip, String pcName, boolean callbacks, boolean force) throws KrnException {
    	Session s = new Session();
    	try {
    		UserSession us = s.loginWithDN(dsName, dn, typeClient, ip, pcName, callbacks, force);
	    	if (Constants.CLIENT_TYPE_LOCALWEB.equals(typeClient))
	    		this.us = us;
	    	return Session.createValueObject(us);
    	} finally {
    		s.close();
    	}
    }

    @Override
    public UserSessionValue loginWithLDAP(String dsName, String dn, String typeClient, String ip, String pcName, boolean callbacks) throws KrnException {
    	Session s = new Session();
    	try {
    		UserSession us = s.loginWithLDAP(dsName, dn, typeClient, ip, pcName, callbacks);
	    	if (Constants.CLIENT_TYPE_LOCALWEB.equals(typeClient))
	    		this.us = us;
	    	return Session.createValueObject(us);
    	} finally {
    		s.close();
    	}
    }
    
    @Override
    public UserSessionValue loginWithECP(
    		String dsName,
    		String pkcs7,
    		String typeClient,
    		String ip,
    		String pcName,
    		boolean callbacks
    ) throws KrnException {
    	Session s = new Session();
    	try {
    		UserSession us = s.loginWithECP(dsName, pkcs7, randomStr, typeClient, ip, pcName, callbacks);
	    	if (Constants.CLIENT_TYPE_LOCALWEB.equals(typeClient))
	    		this.us = us;
	    	return Session.createValueObject(us);
    	} finally {
    		s.close();
    	}
    }

    @Override
	public void release(UUID usId) {
		try {
	    	Session s = getSession(usId);
	    	if (s != null) {
		    	((ServerUserSession) s.getUserSession()).logLogoutTime(s);
		    	us = null;
		    	s.setOwnUser(true);
		    	UserSession user = s.getUserSession();
		    	if (user != null && user.callbacks()) {
					s.writeLogRecord(SystemEvent.EVENT_LOGOUT, "",-1,-1);
		    	}
		    	s.release();
		    	
		        if (user != null && user.callbacks()) {
		        	if ((!user.isAdmin() && !user.isMulti()) || s.findLocalUserSession(user.getUserId()) == null)
		        		OrdersPlugin.removeUserOrders(user);
		        }

	    	}
		} catch (Throwable e) {
			log.error("Сессия уже закрыта");
		}
	}

	@Override
	public UserSessionValue blockObject(UUID usId, long objId)
			throws KrnException {
    	Session s = getSession(usId);
    	try {
    		UserSessionValue res = s.blockObject(objId);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public UserSessionValue lockMethod(UUID usId, String muid)
			throws KrnException {
    	Session s = getSession(usId);
    	try {
    		UserSessionValue res = s.blockMethod(muid);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void unlockMethod(UUID usId, String muid)
			throws KrnException {
    	Session s = getSession(usId);
    	try {
    		s.unlockMethod(muid);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}
	@Override
	public UserSessionValue vcsLockObject(UUID usId, long objId)
			throws KrnException {
    	Session s = getSession(usId);
    	try {
    		UserSessionValue res = s.vcsLockObject(objId);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public UserSessionValue vcsLockModel(UUID usId, String uid, int modelChangeType) throws KrnException {
    	Session s = getSession(usId);
    	try {
    		UserSessionValue res = s.vcsLockModel(uid, modelChangeType);
    		return res;
    	} finally {
    		s.close();
    	}
	}
	
	@Override
	public int blockServer(UUID usId, boolean serverBlocked){
    	Session s = getSessionQuietly(usId);
    	try {
    		int res = s.blockServer(serverBlocked);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
    public void cachedLock(UUID usId, long objId, long lockerId, long flowId) {
        try {
            Session s = getSession(usId);
            try {
                /*if (s.isObjectLock(objId, lockerId)) {
                    log.info("Объект уже заблокирован!");
                } else {*/
                    s.cachedLock(objId, lockerId, flowId);
                    s.commitTransaction();
             // }
            } finally {
                s.close();
            }
        } catch (KrnException e) {
            log.error(e, e);
        }
    }

	@Override
	public void cachedUnlock(UUID usId, long objId, long processId, long flowId) {
		try {
	    	Session s = getSession(usId);
	    	try {
	    		s.cachedUnlock(objId, processId);
	    		s.commitTransaction();
	    	} finally {
	    		s.close();
	    	}
		} catch (KrnException e) {
			log.error(e, e);
		}
	}

	@Override
	public boolean cancelProcess(UUID usId, long flowId, String nodeId,
			boolean isAll, boolean forceCancel) throws KrnException {
    	boolean res = false;
		Session s = getSession(usId);
    	try {
    		res = s.cancelProcess(flowId, nodeId, isAll, forceCancel);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
    	return res;
	}
	
	public KrnAttribute changeAttribute(UUID usId, KrnAttribute attr, KrnClass type,
			String name, int collectionType, boolean isUnique,
			boolean isIndexed, boolean isMultilingual, boolean isRepl,
			int size, long flags, long rAttrId, long sAttrId, boolean sDesc, String tname, int accessModifier)
			throws KrnException {
		return changeAttribute(usId, attr, type, name, collectionType, isUnique, isIndexed, isMultilingual, isRepl, size, flags, rAttrId, sAttrId, sDesc, tname, accessModifier, false);
	}

	@Override
	public KrnAttribute changeAttribute(UUID usId, KrnAttribute attr,
			KrnClass type, String name, int collectionType, boolean isUnique,
			boolean isIndexed, boolean isMultilingual, boolean isRepl,
			int size, long flags, long rAttrId, long sAttrId, boolean sDesc, String tname, int accessModifier, boolean isEncrypt)
			throws KrnException {

		Session s = getSession(usId);
    	try {
    		KrnAttribute res = s.changeAttribute(
    				attr, type, name, collectionType, isUnique,
    				isIndexed, isMultilingual, isRepl, size, flags,
    				rAttrId, sAttrId, sDesc, tname, accessModifier, isEncrypt);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnClass changeClass(UUID usId, KrnClass cls, KrnClass baseClass,
			String name, boolean isRepl) throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnClass res = s.changeClass(cls, baseClass, name, isRepl);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}
	
	@Override
	public KrnMethod rollbackMethod(UUID usId, String methodUid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnMethod res = s.rollbackMethod(methodUid);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnMethod changeMethod(UUID usId, String methodUid, String name,
			boolean isClassMethod, byte[] expr) throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnMethod res = s.changeMethod(methodUid, name, isClassMethod, expr);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

    @Override
    public void changePassword(String dsName, String nameUs, String typeClient, String ip, String pcName, KrnObject object, char[] oldPath, char[] newPath, char[] confPath) throws KrnException {
        Session s = SrvUtils.getSession(dsName, "sys", null);
        try {
            s.changePassword(dsName, nameUs, typeClient, ip, pcName, object, oldPath, newPath, confPath);
            s.commitTransaction();
        } finally {
            s.release();
        }
    }
    
    public void verifyPassword(String dsName, String nameUs, KrnObject object, char[] newPath,
    		String name, boolean admin, boolean isLogged, String psw, Time lastChangeTime) throws KrnException {
        Session s = SrvUtils.getSession(dsName, "sys", null);
        try {
            s.verifyPassword(nameUs, object, newPath, name, admin, isLogged, psw, lastChangeTime);
            s.commitTransaction();
        } finally {
            s.release();
        }
    }

	@Override
	public void changeTimerTask(UUID usId, long objId, boolean isDelete)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.changeTimerTask(objId, isDelete);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public boolean clearFilterParams(UUID usId, String fuid) throws KrnException {
		boolean res = false;
		Session s = getSessionWithNoDB(usId);
    	try {
    		res = s.clearFilterParams(fuid);
    	} finally {
    		s.close();
    	}
    	return res;
	}

	@Override
	public KrnObject[] cloneObject2(UUID usId, KrnObject[] source,
			long getTrId, long setTrId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnObject[] res = s.cloneObject2(source, getTrId, setTrId);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public LongPair[] commit2(UUID usId, List<CacheChangeRecord> changes,
			long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		LongPair[] res = s.commit2(changes, tid);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void commitLongTransaction(UUID usId, long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.commitLongTransaction(tid, 0);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}
	
	@Override
	public KrnAttribute createAttribute(UUID usId, KrnClass cls, KrnClass type,
			String name, int collectionType, boolean isUnique,
			boolean isIndexed, boolean isMultilingual, boolean isRepl,
			int size, long flags, long rAttrId, long sAttrId, boolean sDesc, String tname, int accessModifier)
			throws KrnException {
		return createAttribute(usId, cls, type, name, collectionType, isUnique, isIndexed, isMultilingual, isRepl, size, flags, rAttrId, sAttrId, sDesc, tname, accessModifier, false);
	}

	@Override
	public KrnAttribute createAttribute(UUID usId, KrnClass cls, KrnClass type,
			String name, int collectionType, boolean isUnique,
			boolean isIndexed, boolean isMultilingual, boolean isRepl,
			int size, long flags, long rAttrId, long sAttrId, boolean sDesc, String tname, int accessModifier, boolean isEncrypt)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnAttribute res = s.createAttribute(
    				cls, type, name, collectionType, isUnique, isIndexed,
    				isMultilingual, isRepl, size, flags, rAttrId, sAttrId,
    				sDesc, tname, accessModifier, isEncrypt);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnClass createClass(UUID usId, KrnClass baseClass, String name,
			boolean isRepl, String tname, int mod) throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnClass res = s.createClass(baseClass, name, isRepl, tname, mod);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}
	
	@Override
	public KrnIndex createIndex(UUID usId,KrnClass cls,KrnAttribute[] attrs,boolean[] descs) throws KrnException{
		Session s = getSession(usId);
		try{
			KrnIndex ret = s.createIndex(cls, attrs,descs);
			s.commitTransaction();
			return ret;
		}finally{
			s.close();
		}
	}
	
	@Override
	public KrnIndex[] getIndexesByClassId(UUID usId, KrnClass cls)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnIndex[] ret = s.getIndexesByClassId(cls);
    		return ret;
    	} finally {
    		s.rollbackTransaction();///
    		s.close();
    	}
	}
	
	@Override 
	public KrnIndexKey[] getIndexKeysByIndexId(UUID usId,KrnIndex ndx)
		throws KrnException{
		Session s = getSession(usId);
		try{
			KrnIndexKey[] ret = s.getIndexKeysByIndexId(ndx);
			return ret;
		}finally{
			s.close();
		}
	}
	
	@Override
	public KrnAttribute[] getAttributesForIndexing(UUID usId,KrnClass cls) 
		throws KrnException{
		Session s = getSession(usId);
		try{
			KrnAttribute[] ret = s.getAttributesForIndexing(cls);
			return ret;
		}finally{
			s.close();
		}
	}
	
	@Override	       
	public void deleteIndex(UUID usId,KrnIndex ndx) throws KrnException{
		Session s = getSession(usId);
		try{
			s.deleteIndex(ndx);
			s.commitTransaction();
		}finally{
			s.close();
		}
	}
	
	@Override
	public void createConfirmationFile(UUID usId, long DbId)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.createConfirmationFile(DbId);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public long createLongTransaction(UUID usId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		long res = s.createLongTransaction();
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnMethod createMethod(UUID usId, KrnClass cls, String name,
			boolean isClassMethod, byte[] expr) throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnMethod res = s.createMethod(cls, name, isClassMethod, expr);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnObject createObject(UUID usId, KrnClass cls, long tid)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnObject res = s.createObject(cls, tid);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnObject createObjectWithUid(UUID usId, KrnClass cls, String uid,
			long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnObject res = s.createObjectWithUid(cls, uid, tid);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void dbExport(UUID usId, String dir, String separator) {
		try {
			Session s = getSession(usId);
	    	try {
	    		s.dbExport(dir, separator);
	    		s.commitTransaction();
	    	} finally {
	    		s.close();
	    	}
		} catch (KrnException e) {
			log.error(e, e);
		}
	}

	@Override
	public void deleteAttribute(UUID usId, KrnAttribute attr)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.deleteAttribute(attr);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void deleteClass(UUID usId, KrnClass cls) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.deleteClass(cls);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}
	
	@Override
	public String renameClassTable(UUID usId, KrnClass cls, String newname) throws KrnException {//TODO r tname
		Session s = getSession(usId);
		String result = null;
    	try {
    		result = s.renameClass(cls, newname);
    		if (result != null){
    			s.commitTransaction();
    		}
    	} finally {
    		s.close();
    	}
    	return result;
	}
	
	@Override
	public String renameAttrTable(UUID usId, KrnAttribute attr, String newname) throws KrnException {//TODO r tname
		Session s = getSession(usId);
		String result = null;
    	try {
    		result = s.renameAttr(attr, newname);
    		if (result != null) {
    			s.commitTransaction();
    		}
    	} finally {
    		s.close();
    	}
    	return result;
	}

	@Override
	public void deleteMethod(UUID usId, String methodUid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.deleteMethod(methodUid);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void deleteObject(UUID usId, KrnObject obj, long tid)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.deleteObject(obj, tid);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void deleteUnusedObjects(UUID usId, long cid, long aid)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.deleteUnusedObjects(cid, aid);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void deleteValue(UUID usId, long objectId, long attrId,
			int[] indexes, long langId, long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.deleteValue(objectId, attrId, indexes, langId, tid);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void deleteValueInSet(UUID usId, long objectId, long attrId,
			Object[] values, long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.deleteValueInSet(objectId, attrId, values, tid);
    		s.commitTransaction();
    	} finally {
    		if (s != null) s.close();
    	}
	}

	@Override
	public long execute(final UUID usId, final String cmd, final Map<String, Object> vars, final boolean closeSession) throws KrnException {
		final Session session = getSession(usId);
		Thread terminalThread = new Thread() {
    		public void run() {
				try {
    	    		Map<String, Object> tmp = session.executeInThread(cmd, vars, this.getId());
	    	    	Map<String, Object> res = new HashMap<String, Object>(); 
    	    		for (String key : tmp.keySet()) {
    	    			Object obj = tmp.get(key);
    	    			if (isSerializable(obj)) {
    	    				res.put(key, obj);
    	    			}
    	    		}
    	    		session.sendMessage(0, res, null);
    	    		terminalThreads.remove(this.getId());
    	    	} catch (KrnException e) {
					e.printStackTrace();
    	    		session.sendMessage(-1, null, e.getMessage());
				} finally {
					session.getSrvOrLang().evalsMap.remove(this.getId());
    	    		if (closeSession && session != null) {
    	    			session.close();
    	    		}
    	    	}
			}
		};
		terminalThreads.put(terminalThread.getId(), session);
		terminalThread.start();
		return terminalThread.getId();
	}
	
	@Override
	public void stopTerminalThread(long threadId) {
		Session session = terminalThreads.get(threadId);
		if (session != null) {
			EvaluatorVisitor ev = session.getSrvOrLang().evalsMap.remove(threadId);
			if (ev != null) {
				ev.setBreaking(3);
			}
		}
	}

	@Override
	public Object executeMethod(UUID usId, KrnObject obj, KrnObject this_, String name, List<Object> args, long trId) throws KrnException {
		Session s = getSession(usId);
    	try {
	        Context ctx = new Context(
	        		new long[0],
	        		trId,
	        		s.getUserSession().getDataLanguage().id);
	        s.setContext(ctx);
	        SrvOrLang orlang = s.getSrvOrLang();
	        Object res = orlang.exec(obj, this_, name, args, new Stack<String>());
    		s.commitTransaction();
    		return res;
    	} catch (Throwable e) {
    		log.error(e, e);
    		throw new KrnException(0, e.getMessage());
    	} finally {
    		s.close();
    	}
	}

	@Override
	public Object executeMethod(UUID usId, KrnClass cls, KrnClass this_, String name, List<Object> args, long trId) throws KrnException {
		Session s = getSession(usId);
    	try {
	        Context ctx = new Context(
	        		new long[0],
	        		trId,
	        		s.getUserSession().getDataLanguage().id);
	        s.setContext(ctx);
	        SrvOrLang orlang = s.getSrvOrLang();
	        Object res = orlang.exec(cls, this_, name, args, new Stack<String>());
    		s.commitTransaction();
    		return res;
    	} catch (Throwable e) {
    		log.error(e, e);
    		throw new KrnException(0, e.getMessage());
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void executeTask(UUID usId, long objId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.executeTask(objId);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public long[] filter(UUID usId, String[] fuids, FilterDate[] dates,
			int[] limit) throws KrnException {
		Session s = getSession(usId);
    	try {
    		long[] res = s.filter(fuids, dates, limit);
    		return res;
    	} finally {
    		s.close();
    	}
	}
	
	@Override
	public long[] filter(UUID usId, String[] fuids, FilterDate[] dates,
			int[] limit,int[] beginRows,int[] endRows) throws KrnException {
		Session s = getSession(usId);
    	try {
    		long[] res = s.filter(fuids, dates, limit,beginRows,endRows,0);
    		return res;
    	} finally {
    		s.close();
    	}
	}
	public List<KrnObject> filter(UUID usId,KrnObject filterObj,int limit, long trId) throws KrnException {
		return filter(usId,filterObj,limit,-1,-1,trId);
	}
	public List<KrnObject> filter(UUID usId,KrnObject filterObj,int limit,int beginRow,int endRow, long trId) throws KrnException
	{
	    Session s = getSession(usId);
	    try {
	        List<KrnObject> objList = s.filter(filterObj, limit,beginRow,endRow, trId);
	        return objList;
	    }
	    finally
	    {
	        s.close();
	    }
	}

	@Override
	public KrnAttribute getAttributeById(UUID usId, long attrId)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnAttribute res = s.getAttributeById(attrId);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnAttribute getAttributeByName(UUID usId, KrnClass cls, String name)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnAttribute res = s.getAttributeByName(cls, name);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public List<KrnAttribute> getDependAttrs(UUID usId, KrnAttribute attr)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		List<KrnAttribute> res = s.getDependAttrs(attr);
    		return res;
    	} finally {
    		s.close();
    	}
	}
	@Override
	public List<KrnAttribute> getAttributesByName(UUID usId, String name, long searchMethod)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		List<KrnAttribute> res = s.getAttributesByName(name, searchMethod);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public String getAttributeComment(UUID usId, long attrId)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		String res = s.getAttributeComment(attrId);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnAttribute[] getAttributes(UUID usId, KrnClass cls)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnAttribute[] res = s.getAttributes(cls);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public List<KrnAttribute> getClassAttributes(UUID usId, KrnClass cls)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		List<KrnAttribute> res = s.getClassAttributes(cls);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override 
	public List<KrnSearchResult> getConfigsByConditions(UUID usId, String objTitle, String objUID)
			throws KrnException {    
		Session s = getSession(usId);
    	try {
			List<KrnSearchResult> res = s.getConfigsByConditions(objTitle, objUID);
    		return res;
    	} finally {
    		s.close();
    	} 
	}

	@Override
	public KrnAttribute[] getAttributesByTypeId(UUID usId, long typeId,
			boolean inherited) throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnAttribute[] res = s.getAttributesByTypeId(typeId, inherited);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public byte[] getBlob(UUID usId, long objId, long attrId, int index,
			long langId, long tid, boolean compress) throws KrnException {
		Session s = getSession(usId);
    	try {
    		byte[] res = s.getBlob(objId, attrId, index, langId, tid);
    		if (compress)
    			res = DataUtil.compress(res, 9);
    		return res;
    	} catch (IOException e) {
    		log.error(e, e);
    		throw new KrnException(0, e.getMessage());
    	} finally {
    		s.close();
    	}
	}

	@Override
	public byte[][] getBlobs(UUID usId, long objId, long attrId, long langId,
			long tid, boolean compress) throws KrnException {
		Session s = getSession(usId);
    	try {
    		byte[][] res = s.getBlobs(objId, attrId, langId, tid);
    		if (compress)
    			for (int i = 0; i < res.length; i++)
    				res[i] = DataUtil.compress(res[i], 9);
    		return res;
    	} catch (IOException e) {
    		log.error(e, e);
    		throw new KrnException(0, e.getMessage());
    	} finally {
    		s.close();
    	}
	}

	@Override
	public int getChanges(UUID usId, int action, String info, String scriptOnBeforeAction, String scriptOnAfterAction) throws KrnException {
		Session s = getSession(usId);
    	try {
    		int res = s.getChanges(action, info, scriptOnBeforeAction, scriptOnAfterAction);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnObject[] getChildDbs(UUID usId, boolean recursive,
			boolean onlyPhisycal) throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnObject[] res = s.getChildDbs(recursive, onlyPhisycal);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnClass getClassById(UUID usId, long classId) throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		KrnClass res = s.getClassById(classId);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnClass getClassByName(UUID usId, String name) throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		KrnClass res = s.getClassByName(name);
    		return res;
    	} finally {
    		s.close();
    	}
	}
	
	@Override
	public List<KrnClass> getClassesByNameWithOptions(UUID usId, String name, long searchMethod) throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		List<KrnClass> res = s.getClassesByNameWithOptions(name, searchMethod);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public String getClassComment(UUID usId, long clsId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		String res = s.getClassComment(clsId);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public List<KrnClass> getClasses(UUID usId, long baseClassId, boolean withSubclasses) throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		List<KrnClass> res = s.getClasses(baseClassId, withSubclasses);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnClass[] getClasses(UUID usId, long baseClassId)
			throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		KrnClass[] res = s.getClasses(baseClassId);
    		return res;
    	} finally {
    		s.close();
    	}
	}
	
	@Override 
	public KrnClass[] getClasses(UUID usId) throws KrnException{
		Session s = getSessionWithNoDB(usId);
		try{
			KrnClass[] res = s.getClasses();
			return res;
		}finally{
			s.close();
		}
	}

	@Override
	public KrnObject[] getClassObjects(UUID usId, KrnClass cls,
			long[] filterIds, long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnObject[] res = s.getClassObjects(cls, filterIds, tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}
	
	@Override
	public KrnObject[] getClassOwnObjects(UUID usId, KrnClass cls, long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnObject[] res = s.getClassOwnObjects(cls, tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnObject[] getClassObjects2(UUID usId, KrnClass cls,
			long[] filterIds, int[] limit, long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnObject[] res = s.getClassObjects2(cls, filterIds, limit, tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public QueryResult getClassObjects3(
			UUID usId,
			KrnClass cls,
			AttrRequest req,
			long[] filterIds,
			int limit,
			long tid
	) throws KrnException {
		return getClassObjects3(usId, cls, req, filterIds, limit, tid, null);
	}
	
	@Override
	public QueryResult getClassObjects3(
			UUID usId,
			KrnClass cls,
			AttrRequest req,
			long[] filterIds,
			int limit,
			long tid,
			String info
	) throws KrnException {
		Session s = getSession(usId);
    	try {
    		QueryResult res = s.getClassObjects3(cls, req, filterIds, limit, tid, info);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnObject[] getCachedConflictLocker(UUID usId, long objId, long processId,
			long processDefId) {
		try {
			Session s = getSession(usId);
	    	try {
	    		KrnObject[] res = s.getCachedConflictLocker(objId, processId, processDefId);
	    		s.commitTransaction();
	    		return res;
	    	} finally {
	    		s.close();
	    	}
		} catch (KrnException e) {
			log.error(e, e);
		}
		return new KrnObject[0];
	}

	@Override
	public KrnObject getCurrentDb(UUID usId) throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		KrnObject res = s.getCurrentDb();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public Date[] getDates(UUID usId, long objId, long attrId, long tid)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		Date[] res = s.getDates(objId, attrId, tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public DateValue[] getDateValues(UUID usId, long[] objIds, long attrId,
			long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		DateValue[] res = s.getDateValues(objIds, attrId, tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}
	
	@Override
	public DateValue[] getDateValues2(UUID usId, long[] objIds, long attrId,
			long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		DateValue[] res = s.getDateValues2(objIds, attrId, tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public long filterCount(UUID usId, KrnObject filterObj, long trId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		long res = s.filterCount(filterObj, trId);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public long[] getFilteredObjectIds(UUID usId, long[] filterIds,
			FilterDate[] dates, int[] limit, long trId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		long[] res = s.getFilteredObjectIds(filterIds, dates, limit,null,null, trId);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public long[] getFilteredObjectIds2(UUID usId, String[] filterUids,
			FilterDate[] dates, int[] limit) throws KrnException {
		Session s = getSession(usId);
    	try {
    		long[] res = s.getFilteredObjectIds2(filterUids, dates, limit);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnObject[] getFilteredObjects(UUID usId, KrnObject filterObj,
			int limit, long trId) throws KrnException {
		return getFilteredObjects(usId, filterObj,limit,-1,-1, trId);
	}

	@Override
	public KrnObject[] getFilteredObjects(UUID usId, KrnObject filterObj,
			int limit, int beginRow,int endRow,long trId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnObject[] res = s.getFilteredObjects(filterObj, limit,beginRow,endRow, trId);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public Object[] getFilterParam(UUID usId, String fuid, String pid)
			throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		Object[] param = s.getFilterParam(fuid, pid);
            for (int i = 0; i < param.length; i++) {
                if (param[i] instanceof java.util.Date)
                	param[i] = kz.tamur.util.Funcs.convertTime((java.util.Date)param[i]);
            }
    		return param;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public double[] getFloats(UUID usId, KrnObject obj, KrnAttribute attr,
			long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		double[] res = s.getFloats(obj, attr, tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public FloatValue[] getFloatValues(UUID usId, long[] objIds, long attrId,
			long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		FloatValue[] res = s.getFloatValues(objIds, attrId, tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public Map<String, Object> getInterfaceVars(UUID usId, long flowId)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		Map<String, Object> res = s.getInterfaceVars(flowId);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public long[] getLangs(UUID usId, long objId, long attrId, long tid)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		long[] res = s.getLangs(objId, attrId, tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public int getLastValue(UUID usId, long seqId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		int res = s.getLastValue(seqId);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnObject getCachedLocker(UUID usId, long objId, long processId) {
		try {
			Session s = getSession(usId);
	    	try {
	    		KrnObject res = s.getCachedLocker(objId, processId);
	    		s.commitTransaction();
	    		return res;
	    	} finally {
	    		s.close();
	    	}
		} catch (KrnException e) {
			log.error(e, e);
		}
		return Session.nullObject;
	}

	@Override
	public long[] getLongs(UUID usId, long objId, long attrId, long tid)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		long[] res = s.getLongs(objId, attrId, tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public LongValue[] getLongValues(UUID usId, long[] objIds, long attrId,
			long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		LongValue[] res = s.getLongValues(objIds, attrId, tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public SuperMap[] getMapList(UUID usId, long[] flowIds) throws KrnException {
		Session s = getSession(usId);
    	try {
    		SuperMap[] res = s.getMapList(flowIds);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public int getMaxIndex(UUID usId, long objectId, long attrId, long langId,
			long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		int res = s.getMaxIndex(objectId, attrId, langId, tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public String getMethodComment(UUID usId, String methodId)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		String res = s.getMethodComment(methodId);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public byte[] getMethodExpression(UUID usId, String methodUid)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		byte[] res = s.getMethodExpression(methodUid);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public List<KrnMethod> getAllMethods(UUID usId) throws KrnException {
		Session s = getSessionWithNoDB(usId);
		try {
    		List<KrnMethod> methods = s.getAllMethods();
    		return methods;
    	} finally {
    		s.close();
    	}
	}
	
	@Override
	public Map<String, KrnMethod> getMethodsMap(UUID usId) throws KrnException {
		Session s = getSessionWithNoDB(usId);
		try {
    		Map<String, KrnMethod> methods = s.getMethodsMap();
    		return methods;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnMethod[] getMethods(UUID usId, long cls) throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		KrnMethod[] res = s.getMethods(cls);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnMethod getMethodById(UUID usId, String id) throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		KrnMethod res = s.getMethodById(id);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnMethod[] getMethodsByName(UUID usId, String name, long op)
			throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		KrnMethod[] res = s.getMethodsByName(name, op);
    		return res;
    	} finally {
    		s.close();
    	}
	}
	
	@Override
	public KrnMethod[] getMethodsByUid(UUID usId, String name, long op)
			throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		KrnMethod[] res = s.getMethodsByUid(name, op);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public long getNextGeneratedNumber(UUID usId, String docTypeUid,
			Number period, Number initNumber) throws Exception {
		Session s = getSession(usId);
    	try {
    		long res = s.getNextGeneratedNumber(docTypeUid, period, initNumber);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public long setLastGeneratedNumber(UUID usId, String docTypeUid,
			Number period, Number initNumber) throws Exception {
		Session s = getSession(usId);
    	try {
    		long res = s.setLastGeneratedNumber(docTypeUid, period, initNumber);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public int getNextValue(UUID usId, long seqId, long trId)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		int res = s.getNextValue(seqId, trId);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public UserSessionValue getObjectBlocker(UUID usId, long objId)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		UserSessionValue res = s.getObjectBlocker(objId);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnObject[] getObjects(UUID usId, long objId, long attrId,
			long[] filterIds, long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnObject[] res = s.getObjects(objId, attrId, filterIds, tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnObject[] getObjectsByAttribute(UUID usId, long classId,
			long attrId, long langId, int op, Object value, long tid)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnObject[] res = s.getObjectsByAttribute(classId, attrId, langId, op, value, tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}
	
	@Override
	public KrnObject[] getObjectsByAttribute(UUID usId, long classId,
	        long attrId, long langId, int op, Object value, long tid, KrnAttribute[] krnAttrs)
	                throws KrnException {
	    Session s = getSession(usId);
	    try {
	        KrnObject[] res = s.getObjectsByAttribute(classId, attrId, langId, op, value, tid, krnAttrs);
	        return res;
	    } finally {
	        s.close();
	    }
	}

	@Override
	public KrnObject[] getObjectsById(UUID usId, long[] ids, long trId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnObject[] res = s.getObjectsById(ids, trId);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnObject[] getObjectsByUid(UUID usId, String[] uids, long trId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnObject[] res = s.getObjectsByUid(uids, trId);
    		return res;
    	} finally {
    		s.close();
    	}
	}
	
	@Override
	public KrnObject getClassObjectByUid(UUID usId, long clsId, String uid, long trId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		return s.getClassObjectByUid(clsId, uid, trId);
    	} finally {
    		s.close();
    	}
    }

	@Override
	public KrnObject[] getObjectsLiveOfClass(UUID usId, KrnClass cls)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnObject[] res = s.getObjectsLiveOfClass(cls);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnObject[] getObjectsOfClass(UUID usId, KrnClass cls)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnObject[] res = s.getObjectsOfClass(cls);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public ObjectValue[] getObjectValues(UUID usId, long[] objIds, long attrId,
			long[] filterIds, long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		ObjectValue[] res = s.getObjectValues(objIds, attrId, filterIds, new int[1], tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}
	
	@Override
	public ObjectValue[] getObjectValues(UUID usId, long[] objIds, long attrId,
			long[] filterIds, int[] limit, long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		ObjectValue[] res = s.getObjectValues(objIds, attrId, filterIds, limit, tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public BlobValue[] getBlobValues(UUID usId, long[] objIds, long attrId, long langId,
			long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		BlobValue[] res = s.getBlobValues(objIds, attrId, langId, tid);
    		for (int i = 0; i < res.length; i++)
    			res[i].value = DataUtil.compress(res[i].value, 9);
    		return res;
    	} catch (IOException e) {
    		log.error(e, e);
    		throw new KrnException(0, e.getMessage());
    	} finally {
    		s.close();
    	}
	}

	@Override
	public QueryResult getObjects(UUID usId, long[] objIds,
			AttrRequest req, long tid)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		QueryResult res = s.getObjects(objIds, req, tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public QueryResult getObjectValues(UUID usId, long[] objIds, long attrId,
			long[] filterIds, long tid, AttrRequest req) throws KrnException {
		Session s = getSession(usId);
    	try {
    		QueryResult res = s.getObjectValues(objIds, attrId, filterIds, new int[1], tid, req);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public QueryResult getObjectValues(UUID usId, long[] objIds, long attrId,
			long[] filterIds, int[] limit, long tid, AttrRequest req) throws KrnException {
		Session s = getSession(usId);
    	try {
    		QueryResult res = s.getObjectValues(objIds, attrId, filterIds, limit, tid, req);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public long getOldGeneratedNumber(UUID usId, String docTypeUid,
			Number period) throws Exception {
		Session s = getSession(usId);
    	try {
    		long res = s.getOldGeneratedNumber(docTypeUid, period);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnObject saveNumber(UUID usId, String className, String attrName, String kadastrNumber) throws Exception {
		Session s = getSession(usId);
    	try {
    		KrnObject res = s.saveNumber(className, attrName, kadastrNumber);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public long[] getProcessDefinitions(UUID usId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		long[] res = s.getProcessDefinitions();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnObject[] getReplRecords(UUID usId, int logType, long replicationID)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		KrnObject[] res = s.getReplRecords(logType, replicationID);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public long[] getRevAttributes(UUID usId, long attrId) throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		long[] res = s.getRevAttributes(attrId);
    		return res;
    	} finally {
    		s.close();
    	}
	}
	
	@Override
	public List<KrnAttribute> getRevAttributes2(UUID usId, long attrId) throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		return s.getRevAttributes2(attrId);
    	} finally {
    		s.close();
    	}
	}

	@Override
	public long[] getLinkAttributes(UUID usId, long attrId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		long[] res = s.getLinkAttributes(attrId);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public long[] getSelectedBases(UUID usId) throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		long[] res = s.getSelectedBases();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public long[] getSkippedValues(UUID usId, long seqId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		long[] res = s.getSkippedValues(seqId);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public String[] getStrings(UUID usId, long objId, long attrId, long langId,
			boolean isMemo, long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		String[] res = s.getStrings(objId, attrId, langId, isMemo, tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public StringValue[] getStringValues(UUID usId, long[] objIds, long attrId,
			long langId, boolean isMemo, long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		StringValue[] res = s.getStringValues(objIds, attrId, langId, isMemo, tid);
    		return res;
    	} catch (Exception e) {
    		log.error(e, e);
    		return new StringValue[0];
    	} finally {
    		s.close();
    	}
	}

	@Override
	public SortedSet<Value> getValues(UUID usId, long[] objIds, long attrId, long langId, long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		return s.getValues(objIds, attrId, langId, tid);
    	} finally {
    		s.close();
    	}
    }


	@Override
	public Activity[] getTaskList(UUID usId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		Activity[] res = s.getTaskList();
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public long getTasksCount(UUID usId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		long res = s.getTasksCount();
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}
	@Override
	public Time[] getTimes(UUID usId, long objId, long attrId, long tid)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		Time[] res = s.getTimes(objId, attrId, tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public TimeValue[] getTimeValues(UUID usId, long[] objIds, long attrId,
			long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		TimeValue[] res = s.getTimeValues(objIds, attrId, tid);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public byte[] getTransportParam(UUID usId, int transportId)
			throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		byte[] res = s.getTransportParam(transportId);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public String getUId(UUID usId, int AId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		String res = s.getUId(AId);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public KrnObject getUser(UUID usId) {
		try {
			Session s = getSessionWithNoDB(usId);
	    	try {
	    		return s.getUser();
	    	} finally {
	    		s.close();
	    	}
		} catch (KrnException e) {
			log.error(e, e);
		}
		return null;
	}

	@Override
	public UserSessionValue[] getUserSessions(UUID usId) throws KrnException {
		Session s = getSessionWithNoDB(usId);
		if (s != null) {
	    	try {
	    		UserSessionValue[] res = s.getUserSessions();
	    		return res;
	    	} finally {
	    		s.close();
	    	}
		} else
			return new UserSessionValue[0];
	}
	
	@Override
	public UserSessionValue[] getUserSessions(UUID usId, int criteria, String txt, String txt2) throws KrnException {
		Session s = getSessionWithNoDB(usId);
		if (s != null) {
	    	try {
	    		UserSessionValue[] res = s.getUserSessions(criteria, txt, txt2);
	    		return res;
	    	} finally {
	    		s.close();
	    	}
		} else
			return new UserSessionValue[0];
	}

	@Override
	public String getXml(UUID usId, KrnObject obj) throws KrnException {
		Session s = getSession(usId);
    	try {
    		String res = s.getXml(obj);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void interfaceChanged(UUID usId, long id) {
		try {
			Session s = getSessionWithNoDB(usId);
	    	try {
	    		s.interfaceChanged(id);
	    	} finally {
	    		s.close();
	    	}
		} catch (KrnException e) {
			log.error(e, e);
		}
	}
	
	@Override	
	public boolean isDel(UUID usId, KrnObject obj, long trId) throws KrnException{
		Session s = getSession(usId);
		try {
    		boolean res = s.isDel(obj, trId);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public boolean isDeleted(UUID usId, KrnObject obj) throws KrnException {
		Session s = getSession(usId);
    	try {
    		boolean res = s.isDeleted(obj);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public boolean isCachedObjectLock(UUID usId, long objId, long processId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		boolean res = s.isCachedObjectLock(objId, processId);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public String isCachedObjectLock(UUID usId, long objId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		String res = s.isCachedObjectLock(objId);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public int isServerBlocked(UUID usId) throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		return s.isServerBlocked();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void killUserSessions(UUID usId, UUID kusId, boolean blockUser)
			throws KrnException {
		Session s = getSession(usId);
    	try {
			log.info("Requested killing session: " + kusId);
    		s.killUserSessions(kusId, blockUser);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public Object openInterface(UUID usId, long uiId,long flowId,long trId,long pdId) throws KrnException {
		Object res = null;
		Session s = getSession(usId);
    	try {
    		res = s.openInterface(uiId,flowId,trId,pdId);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
    	return res;
	}

	@Override
	public String[] performActivitys(UUID usId, Activity[] activitys,
			String transition,String event) throws KrnException {
		Session s = getSession(usId);
    	try {
    		String[] res = s.performActivitys(activitys, transition,event);
    	//	s.commitTranifc.getCurrentInterface().getObj()saction(); // TODO Дублирование фиксации транзакции из ExecutionComponent.performActivitys
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void refreshMethodsForReplication(UUID usId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.refreshMethodsForReplication();
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public boolean rejectGeneratedNumber(UUID usId, String docTypeUid,
			Number period, Number number, Time date) throws Exception {
		Session s = getSession(usId);
    	try {
    		boolean res = s.rejectGeneratedNumber(docTypeUid, period, number, date);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void releaseEngagedObject(UUID usId, long objId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.releaseEngagedObject(objId);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void reloadBox(UUID usId, KrnObject obj) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.reloadBox(obj);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public boolean saveFlowParam(UUID usId, long flowId,List<String> args) throws KrnException {
		Session s = getSession(usId);
    	try {
    		boolean res = s.saveFlowParam(flowId, args);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}
	@Override
	public boolean reloadFlow(UUID usId, long flowId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		boolean res = s.reloadFlow(flowId);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void reloadProcessDefinition(UUID usId, long processDefId)
			throws KrnException {
    	UserSession us = (this.us != null) ? this.us : Session.findUserSession(usId);
    	
    	if (us != null && us.isAlive()) {
	    	if (UserSession.SERVER_ID != null)
				ServerMessage.sendMessage(UserSession.SERVER_ID, us.getDsName(), ServerMessage.ACTION_CHG_PRD, processDefId, null);
	    	else {
	    		Session s = getSession(usId);
	        	try {
	        		s.reloadProcessDefinition(processDefId);
	        		s.commitTransaction();
	        	} finally {
	        		s.close();
	        	}
	    	}
    	}		
	}

	@Override
	public String resendMessage(UUID usId, Activity act) throws KrnException {
		Session s = getSession(usId);
    	try {
    		String res = s.resendMessage(act);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void restartTransport(UUID usId, int transportId)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.restartTransport(transportId);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void rollbackLocked(UUID usId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.rollbackLocked();
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void rollbackLongTransaction(UUID usId, long tid)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.rollbackLongTransaction(tid);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void rollbackSeqValues(UUID usId, long seqId, long trId)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.rollbackSeqValues(seqId, trId);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void runReplication(UUID usId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.runReplication();
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void saveFilter(UUID usId, long filterId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.saveFilter(filterId, 0);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void selectBases(UUID usId, long[] baseIds) throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		s.selectBases(baseIds);
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void sendMessage(UUID usId, String message) throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		s.sendMessage(message);
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void sendMessage(UUID usId, UUID toUsId, String message) throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		s.sendMessage(toUsId, message);
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void setAttributeComment(UUID usId, String attrUid, String comment)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.setAttributeComment(attrUid, comment);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void setBlob(UUID usId, long objectId, long attrId, int index,
			byte[] value, long langId, long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.setBlob(objectId, attrId, index, value, langId, tid);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public String setChanges(UUID usId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		String res = s.setChanges();
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void setClassComment(UUID usId, String clsUid, String comment)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.setClassComment(clsUid, comment);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void setDate(UUID usId, long objectId, long attrId, int index,
			Date value, long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.setDate(objectId, attrId, index, value, tid);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public boolean setFilterParam(UUID usId, String fuid, String pid, Object param)
			throws KrnException {
		boolean res = false;
		Session s = getSessionWithNoDB(usId);
    	try {
            if (param instanceof List) {
            	List l = (List)param;
    	        for (int i = 0; i < l.size(); i++) {
    	            if (l.get(i) instanceof Time) l.set(i, kz.tamur.util.Funcs.convertTime((Time)l.get(i)));
    	        }
            } else if (param instanceof Time) {
                param = kz.tamur.util.Funcs.convertTime((Time)param);
            }
    		res = s.setFilterParam(fuid, pid, param);
    	} finally {
    		s.close();
    	}
    	return res;
	}

	@Override
	public boolean setFilterParam(UUID usId, String fuid, String pid,
			Object[] param) throws KrnException {
		boolean res = false;
		Session s = getSessionWithNoDB(usId);
    	try {
            for (int i = 0; i < param.length; i++) {
                if (param[i] instanceof Time) param[i] = kz.tamur.util.Funcs.convertTime((Time)param[i]);
            }
    		res = s.setFilterParam(fuid, pid, param);
    	} finally {
    		s.close();
    	}
    	return res;
	}

	@Override
	public void setFloat(UUID usId, long objectId, long attrId, int index,
			double value, long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.setFloat(objectId, attrId, index, value, tid);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void setLang(UUID usId, KrnObject lang) throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		s.setLang(lang);
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void setDataLanguage(UUID usId, KrnObject lang) throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		s.setDataLanguage(lang);
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void setLong(UUID usId, long objectId, long attrId, int index,
			long value, long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.setLong(objectId, attrId, index, value, tid);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}
	
	@Override
	public void setLong(UUID usId, List<Long> objectsIds, long attrId, long value, long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.setLong(objectsIds, attrId, value, tid);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void setMethodComment(UUID usId, String methodId, String comment)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.setMethodComment(methodId, comment);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void setObject(UUID usId, long objectId, long attrId, int index,
			long value, long tid, boolean insert) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.setObject(objectId, attrId, index, value, tid, insert);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void setPermitPerform(UUID usId, long flowId, boolean permit)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.setPermitPerform(flowId, permit);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public boolean setSelectedObjects(UUID usId, long flowId, long nodeId,
			KrnObject[] objs) throws KrnException {
		Session s = getSession(usId);
    	try {
    		boolean res = s.setSelectedObjects(flowId, nodeId, objs);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void setString(UUID usId, long objectId, long attrId, int index,
			long langId, boolean isMemo, String value, long tid)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.setString(objectId, attrId, index, langId, isMemo, value, tid);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void setTaskListFilter(UUID usId, AnyPair[] params)
			throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		s.setTaskListFilter(params);
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void setTime(UUID usId, long objectId, long attrId, int index,
			Time value, long tid) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.setTime(objectId, attrId, index, value, tid);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void setTransportParam(UUID usId, byte[] data, int transportId)
			throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		s.setTransportParam(data, transportId);
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void skipValue(UUID usId, long seqId, long value, String strVal,
			long trId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.skipValue(seqId, value, strVal, trId);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public String[] startProcess(UUID usId, long processDefIdin, Map<String, Object> vars)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		String[] res = s.startProcess(processDefIdin, vars);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void startTransport(UUID usId, int transport) throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		s.startTransport(transport);
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void unuseValue(UUID usId, long seqId, String oldStrValue,
			long newValue, String newStrValue, long trId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.unuseValue(seqId, oldStrValue, newValue, newStrValue, trId);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void updateReferences(UUID usId, long cid, long aid)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.updateReferences(cid, aid);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}
	
	@Override
	public void updateUsers(UUID usId, KrnObject[] objs) throws KrnException {
		Session s = getSession(usId);
		try {
			s.updateUsers(objs);
			s.commitTransaction();
		} finally {
			s.close();
		}
	}

	@Override
	public void updateUser(UUID usId, KrnObject obj, String name)
			throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.updateUser(obj, name);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void userBlocked(UUID usId, String name) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.userBlocked(name);
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void userCreated(UUID usId, String name) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.userCreated(name);
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void userDeleted(UUID usId, String name) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.userDeleted(name);
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void userRightsChanged(UUID usId, String name) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.userRightsChanged(name);
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void userUnblocked(UUID usId, String name) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.userUnblocked(name);
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void useValue(UUID usId, long seqId, long value, String strVal,
			long trId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.useValue(seqId, value, strVal, trId);
    		s.commitTransaction();
    	} finally {
    		s.close();
    	}
	}

	@Override
	public void writeLogRecord(UUID usId, SystemEvent event,
			String description) throws KrnException {
		Session s = getSession(usId);
    	try {
    		s.writeLogRecord(event, description,-1,-1);
    	} finally {
    		s.close();
    	}
	}

    @Override
    public void writeLogRecord(UUID usId, String loggerName, String type, String event, String description) throws KrnException {
        Session s = getSessionWithNoDB(usId);
        try {
            s.writeLogRecord(loggerName, type, event, description);
        } finally {
            s.close();
        }
    }

	public Note[] getNotes(UUID usId) {
    	UserSession us = (this.us != null) ? this.us : Session.findUserSession(usId);
    	if (us != null && us.isAlive()) {
    		us.ping();
    		Note[] notes = us.getNotes();
    		return notes;
    	} else {
			log.warn("Session for getNotes not found: " + usId);
    	}
    	return null;
    }
	
	public boolean showUserDB(UUID usId) throws KrnException{
		return Database.showUserDB;
	}

	@Override
    public Activity getTask(UUID usId, long flowId, long ifsPar, boolean isCheckEvent, boolean onlyMy) throws KrnException {
    	/*
    	UserSession us = (this.us != null) ? this.us : Session.findUserSession(usId);
        if(us==null) 
        	return null;
    	ExecutionComponent exeComp = Session.getExeComp(us.getDsName());
        if(exeComp==null) 
        	return null;
        return exeComp.getTask(flowId,us, us.getTasksFilter(),ifsPar);
    	*/
    	Session s = getSession(usId);
    	try {
    		Activity res = s.getTask(flowId, ifsPar, isCheckEvent, onlyMy);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
    }

    public boolean ping(UUID usId) {
    	UserSession us = (this.us != null) ? this.us : Session.findUserSession(usId);
    	if (us != null) {
        	us.ping();
        	return true;
    	} else
			log.warn("Session for ping not found: " + usId);
    	return false;
    }

	@Override
	public Map<String, List<Object>> getFilterParams(UUID usId, String fuid)
			throws KrnException {
		Session s = getSessionWithNoDB(usId);
    	try {
    		Map<String, List<Object>> res = s.getFilterParams(fuid);
    		return res;
    	} finally {
    		s.close();
    	}
	}

	@Override
	public int truncateClass(UUID usId, KrnClass cls) throws KrnException {
		Session s = getSession(usId);
    	try {
    		int res = s.truncateClass(cls);
    		s.commitTransaction();
    		return res;
    	} finally {
    		s.close();
    	}
	}

	private Session getSession(UUID usId) throws KrnException {
    	UserSession us = (this.us != null) ? this.us : Session.findUserSession(usId);
    	if (us != null && us.isAlive()) 
    		return SrvUtils.getSession(us);
    	//else
    	//	log.info("GET SESSION: " + usId + " " + (us != null ? ("us = " + us.isAlive()) : "us = null"));
    	throw new KrnException(0, "Сессия закрыта");
    }
    
	private Session getSessionWithNoDB(UUID usId) throws KrnException {
    	UserSession us = (this.us != null) ? this.us : Session.findUserSession(usId);
    	if (us != null && us.isAlive()) 
    		return SrvUtils.getSession(us, false);
    	//else
    	//	log.info("GET SESSION: " + usId + " " + (us != null ? ("us = " + us.isAlive()) : "us = null"));
    	throw new KrnException(0, "Сессия закрыта");
    }

	private Session getSessionQuietly(UUID usId) {
    	try {
	    	UserSession us = (this.us != null) ? this.us : Session.findUserSession(usId);
	    	if (us != null) 
	    		return SrvUtils.getSession(us);
	    	else
	    		throw new KrnException(0, "Сессия закрыта");
    	} catch (KrnException e) {
    		log.error(e, e);
    	}
    	return null;
    }
    
    //Lucene search
    public List<Object> search(UUID usId, String pattern, int results, int[] searchProperties, boolean[] searchArea) throws KrnException {
    	Session s = getSession(usId);
    	try {
    		return s.search(pattern, results, searchProperties, searchArea);
    	} finally {
    		s.close();
    	}
    }
    
    //Lucene index Hierarchy of KrnClass
    public void indexHierarchy(UUID usId, String clName, boolean fullIndex) throws KrnException {
    	Session s = getSession(usId);
    	try {
    		s.indexHierarchy(clName, fullIndex);
    	} finally {
    		s.close();
    	}
    }
    
    // Индексирование классов
    @Override
    public void indexClass(UUID usId, KrnClass kClass, boolean fullIndex) throws KrnException {
    	Session s = getSession(usId);
    	try {
    		s.indexClass(kClass, fullIndex);
    	} finally {
    		s.close();
    	}
    }
    
    // Индексирование методов
    @Override
	public void indexMethod(UUID usId, KrnMethod method) throws KrnException {
    	Session s = getSession(usId);
    	try {
    		s.indexMethod(method);
    	} finally {
    		s.close();
    	}
	}
    
    // Индексирование изменений объектов проектирования
    @Override
	public void indexVcsChange(UUID usId, KrnVcsChange change) throws KrnException {
    	Session s = getSession(usId);
    	try {
    		s.indexVcsChange(change);
    	} finally {
    		s.close();
    	}
	}
    // Индексирование триггеров
    @Override
	public void indexTrigger(UUID usId, OrlangTriggerInfo trigger) throws KrnException {
    	Session s = getSession(usId);
    	try {
    		s.indexTrigger(trigger);
    	} finally {
    		s.close();
    	}
	}
    
    // Обнуление папки с индексами атрибута или методов
    @Override
	public void dropIndex(UUID usId, KrnAttribute krnAttribute) throws KrnException {
    	Session s = getSession(usId);
    	try {
    		Indexer.stopIndexing();
    		
    		s.dropIndex(krnAttribute);
    		
    		Indexer.startIndexing();
    	} finally {
    		s.close();
    	}
	}
    
    // Обнуление папки с индексами триггеров
    @Override
	public void dropTriggersIndexFolder(UUID usId) throws KrnException {
    	Session s = getSessionWithNoDB(usId);
    	try {
    		Indexer.stopIndexing();
    		
    		s.dropTriggersIndexFolder();
    		
    		Indexer.startIndexing();
    	} finally {
    		s.close();
    	}
	}

    // Обнуление папки с индексами изменений
    @Override
	public void dropVcsChangesIndexFolder(UUID usId) throws KrnException {
    	Session s = getSessionWithNoDB(usId);
    	try {
    		Indexer.stopIndexing();
    		
    		s.dropVcsChangesIndexFolder();
    		
    		Indexer.startIndexing();
    	} finally {
    		s.close();
    	}
	}
    
    // Индексирование объектов
    @Override
	public void indexObject(UUID usId, KrnObject krnObject, KrnClass krnClass, KrnAttribute krnAttribute, boolean foolIndex) throws KrnException {
    	Session s = getSession(usId);
    	try {
    		s.indexObject(krnObject, krnClass, krnAttribute, foolIndex);
    	} finally {
    		s.close();
    	}
	}
    
    // Обнуление всех папок с индексами
    @Override
    public boolean dropIndexFolder(UUID usId) throws KrnException {
    	Session s = getSessionWithNoDB(usId);
    	try {
    		Indexer.stopIndexing();
    		
    		boolean res = Indexer.dropAllIndexes();
    		
    		Indexer.startIndexing();

    		return res;
    	} finally {
    		s.close();
    	}
    }
    
    // Возвращает полный путь к хранилищу индексов 
    @Override
    public String getIndexDirectory(UUID usId) throws KrnException {
    	Session s = getSessionWithNoDB(usId);
    	try {
    		return s.getIndexDirectory();
    	} finally {
    		s.close();
    	}
    }
    
    // Считывает информацию о последнем процессе индексации
    @Override
    public String getLastIndexingInfo(UUID usId) throws KrnException {
    	Session s = getSessionWithNoDB(usId);
    	try {
    		return s.getLastIndexingInfo();
    	} finally {
    		s.close();
    	}
    }
    
    // Записывает информацию о последнем процессе индексации
    @Override
    public void setLastIndexingInfo(UUID usId, String lastIndexingInfo) throws KrnException {
    	Session s = getSessionWithNoDB(usId);
    	try {
    		s.setLastIndexingInfo(lastIndexingInfo);
    	} finally {
    		s.close();
    	}
    }
    
    public String getHighlightedFragments(
    		UUID usId,
    		String objUid,
    		long attrId,
    		long langId,
    		String pattern) throws KrnException {
    	Session s = getSession(usId);
    	try {
    		return s.getHighlightedFragments(objUid, attrId, langId, pattern);
    	} finally {
    		s.close();
    	}
    }

	@Override
	public boolean stringSearch(UUID usId, String content, String pattern, long langId) {
		Session s = null;
		try {
			s = getSessionWithNoDB(usId);
			return s.stringSearch(content, pattern, langId);
		} catch (KrnException e) {
			log.error(e, e);
		} finally {
			if (s != null) s.close();
		}
		return false;
	}	
    
	@Override
	public ModelChanges getModelChanges(UUID usId, long changeId) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.getModelChanges(changeId);
		} finally {
			s.close();
		}
	}	

	@Override
	public DataChanges getDataChanges(UUID usId, long classId, long changeId, AttrRequest req) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.getDataChanges(classId, changeId, req);
		} finally {
			s.close();
		}
	}

	@Override
	public Collection<Lock> getLocksByObjectId(UUID usId, long objId) {
		try {
			Session s = getSession(usId);
			try {
				return s.getLocksByObjectId(objId);
			} finally {
				s.close();
			}
		} catch (KrnException e) {
			log.error(e, e);
		}
		return null;
	}	

	@Override
	public ReportData prepareReport(UUID usId, long reportId, KrnObject lang, KrnObject[] srvObjs, FilterDate[] fds, long trId) throws KrnException {
		Session s = getSession(usId);
        Context ctx = new Context(
        		new long[0],
        		trId,
        		lang.id);
        s.setContext(ctx);
		try {
			return new ReportData(s.prepareReport(reportId, lang, srvObjs, fds));
		} finally {
			s.restoreContext();
			s.close();
		}
	}

	@Override
	public byte[] convertOfficeDocument(UUID usId, byte[] docData,
			String outputFormat) throws KrnException {
		try {
			docData = DataUtil.decompress(docData);
			byte[] res = DocumentConverter.convert(docData, outputFormat);
			res = DataUtil.compress(res, 9);
			return res;
		} catch (Exception e) {
			log.error(e, e);
			throw new KrnException(0, e.getMessage());
		}
	}

	@Override
	public String randomString() {
		return randomStr = UUID.randomUUID().toString();
	}

 	@Override
	public boolean sendMailMessage(UUID usId, String host, String port,	String user, String path,
    		String[] froms,String[] tos,String theme,String text,String mime,String charSet) throws KrnException {
		boolean res=false;
		try {
			Session s = getSession(usId);
			try {
		        Context ctx = new Context(
		        		new long[0],
		        		0,
		        		s.getUserSession().getDataLanguage().id);
		        s.setContext(ctx);
				res= s.sendMailMessage(host, port, user, path,froms,tos,theme,text,mime,charSet,true);
			} finally {
				s.close();
			}
		} catch (KrnException e) {
			log.error(e, e);
		}
		return res;
	}
 	
	@Override
	public boolean isValidEmailAddress(UUID usId, String email) {
		boolean res=false;
		try {
			Session s = getSessionWithNoDB(usId);
			try {
		        Context ctx = new Context(
		        		new long[0],
		        		0,
		        		s.getUserSession().getDataLanguage().id);
		        s.setContext(ctx);
				res = s.isValidEmailAddress(email);
			} finally {
				s.close();
			}
		} catch (KrnException e) {
			log.error(e, e);
		}
		return res;
	}
	
	@Override
	public boolean checkUserHasRight(UUID usId, SystemAction action, long userId, KrnObject subject) throws KrnException {
		boolean res = false;
		Session s = getSession(usId);
		try {
			res = s.checkUserHasRight(action, userId, subject);
		} finally {
			s.close();
		}
		return res;
	}
	
	public List<Long> getUserSubjects(UUID usId, SystemAction action, long userId) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.getUserSubjects(action, userId);
		} finally {
			s.close();
		}
	}
	
	public String[][] getColumnsInfo(UUID usId, String tableName) throws KrnException { //TODO: Tedit
		Session s = getSession(usId);
		try {
			return s.getColumnsInfo(tableName);
		} finally {
			s.close();
		}
	}
	
	public boolean columnMove(UUID usId, int[] cols, String tableName) throws KrnException { //TODO: Tedit
		Session s = getSession(usId);
		try {
			return s.columnMove(cols, tableName);
		} finally {
			s.close();
		}
	}
	
	public boolean isDataLog() {
		return Session.isDataLog();
	}	

	public void setDataLog(boolean dataLog) {
		Session.setDataLog(dataLog);
	}
	
	public void addAttrChangeListener(UUID usId, long classId) throws KrnException {
    	UserSession us = (this.us != null) ? this.us : Session.findUserSession(usId);
    	if (us != null && us.isAlive()) {
			Driver2.addAttrChangeListener(classId, (ServerUserSession)us);
    	}
    	else
    		throw new KrnException(0, "Сессия закрыта");
	}

	public byte[] getUserPhoto(String uid) throws KrnException {
    	ServerUserSession us = Session.findUserSession(UUID.fromString(uid));
    	if (us != null && us.isAlive()) {
			return us.getPhoto();
    	}
    	else
    		throw new KrnException(0, "Сессия закрыта");
	}

	@Override
	public String generateWS(UUID usId, byte[] wsdlFileInBytes, String fileName, String packageName, String methodName) {
		try {
			Session s = getSession(usId);
			try {
				Context ctx = new Context(new long[0], 0, s.getUserSession().getDataLanguage().id);
				s.setContext(ctx);
				return s.generateWS(wsdlFileInBytes, fileName, packageName, methodName);
			} finally {
				s.close();
			}
		} catch (KrnException e) {
			log.error(e, e);
			return "Ошибка при генерировании сервиса!";
		}
	}

	@Override
	public byte[] generateXML(UUID usId, String serviceName, int type) throws KrnException {
		try {
			Session s = getSession(usId);
			try {
				Context ctx = new Context(new long[0], 0, s.getUserSession().getDataLanguage().id);
				s.setContext(ctx);
				return s.generateXML(serviceName, type);
			} finally {
				s.close();
			}
		} catch (KrnException e) {
			log.error(e, e);
			throw new KrnException(0, e.getMessage());
		}
	}

	@Override
	public List<Long> findForeignProcess(UUID usId, long processDefId, long cutObjId) throws KrnException {
    	ExecutionComponent exeComp = Session.getExeComp(us.getDsName());
        if (exeComp!=null) { 
			Session s = getSession(usId);
			try {
				return s.findForeignProcess(processDefId, cutObjId);
			} finally {
				s.close();
			}
        }
		return new ArrayList<Long>();
	}
	
	@Override
    public List<ProjectConfiguration> getChildConfigurations(String dsParent) {
		return ConnectionManager.instance().getChildConfigurations(dsParent);
	}
	
	@Override
	public void addConfiguration(ProjectConfiguration c, String dsParent) {
		ProjectConfiguration parent = ConnectionManager.instance().getConfiguration(dsParent);
		parent.addChild(c);
		ConnectionManager.instance().addConfiguration(c.getDsName(), c);
	}
	
	@Override
	public void removeConfiguration(String dsName, String dsParent) {
		ProjectConfiguration parent = ConnectionManager.instance().getConfiguration(dsParent);
		ProjectConfiguration child = ConnectionManager.instance().getConfiguration(dsName);
		parent.getChildren().remove(child);
		ConnectionManager.instance().removeConfiguration(dsName);
	}
	
	@Override
	public void moveConfiguration(String dsName, String dsParent) {
		ProjectConfiguration child = ConnectionManager.instance().getConfiguration(dsName);
		ProjectConfiguration parent = ConnectionManager.instance().getConfiguration(child.getParent().getDsName());
		parent.getChildren().remove(child);

		parent = ConnectionManager.instance().getConfiguration(dsParent);
		parent.addChild(child);

	}

	@Override
	public void changeConfiguration(String dsName, ProjectConfiguration c) {
		ProjectConfiguration pc = ConnectionManager.instance().getConfiguration(dsName);
		pc.load(c);
		if (!dsName.equals(c.getDsName())) {
			ConnectionManager.instance().removeConfiguration(dsName);
			ConnectionManager.instance().addConfiguration(c.getDsName(), c);
		}
	}
	
	@Override
	public void saveAllConfigurations() {
		ConnectionManager.instance().saveAllConfigurations();
	}

	@Override
	public List<Long> findProcessByUiType(UUID usId,String uiType) throws KrnException{
		Session s = getSession(usId);
		try {
			return s.findProcessByUiType(uiType);
		} finally {
			s.close();
		}
	}

	@Override
	public String getReplicationDirectoryPath(UUID usId) throws KrnException {
		Session s = getSessionWithNoDB(usId);
		try {
			return s.getReplicationDirectoryPath();
		} finally {
			s.close();
		}
	}
	
	@Override
	public List<TriggerInfo> getTriggers(UUID usId, KrnClass cls) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.getTriggers(cls);
		} finally {
			s.close();
		}
	}
	
	@Override
	public String createTrigger(UUID usId, String triggerContext) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.createTrigger(triggerContext);
		} finally {
			s.close();
		}
	}
	
	@Override
	public String removeTrigger(UUID usId, String triggerName) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.removeTrigger(triggerName);
		} finally {
			s.close();
		}
	}

	@Override
	public List<Object> downloadFile(UUID usId, String source) throws KrnException {
		Session s = getSessionWithNoDB(usId);
		try {
			return s.downloadFile(source);
		} finally {
			s.close();
		}
	}
	
	@Override
	public String getNextProcessNode(UUID usId, long flowId) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.getNextProcessNode(flowId);
		} finally {
			s.close();
		}
	}

	@Override
	public KrnAttribute setAttrTriggerEventExpression(UUID usId, String expr, long attrId, int mode, boolean isZeroTransaction) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.setAttrTriggerEventExpression(expr, attrId, mode, isZeroTransaction);
		} finally {
			s.close();
		}
	}
	
	@Override
	public KrnClass setClsTriggerEventExpression(UUID usId, String expr, long clsId, int mode, boolean isZeroTransaction) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.setClsTriggerEventExpression(expr, clsId, mode, isZeroTransaction);
		} finally {
			s.close();
		}
	}

	@Override
	public void setMaxActiveCount(int maxThreadCount){
		Session.setMaxActiveCount(maxThreadCount);
	}

	@Override
	public void setMaxFlowCount(int maxThreadCount) {
		Session.setMaxFlowCount(maxThreadCount);
	}

	@Override
	public boolean initServerTasks(UUID usId) throws KrnException{
		Session s = getSession(usId);
		try {
			return s.initServerTasks();
		} finally {
			s.close();
		}
	}

	@Override
	public List<String> getListProcedure(UUID usId, String type)
			throws KrnException {
		Session s = getSession(usId);
		try {
			return s.getListProcedure(type);
		} finally {
			s.close();
		}
	}

	@Override
	public byte[] getProcedureContent(UUID usId, String name,String type)
			throws KrnException {
		Session s = getSession(usId);
		try {
			return s.getProcedureContent(name,type);
		} finally {
			s.close();
		}
	}

	@Override
	public String createProcedure(UUID usId, String name, List params,
			String body) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.createProcedure(name, params, body);
		} finally {
			s.close();
		}
	}

	@Override
	public boolean deleteProcedure(UUID usId, String name, String type) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.deleteProcedure(name, type);
		} finally {
			s.close();
		}
	}
	
	@Override
	public List<Long> getVcsGroupObjects(UUID usId,long clsId) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.getVcsGroupObjects(clsId);
		} finally {
			s.close();
		}
	}

	@Override
	public List<KrnVcsChange> getVcsChanges(UUID usId,int isFixd,int isRepld,long userId,long replId) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.getVcsChanges(isFixd,isRepld,userId,replId);
		} finally {
			s.close();
		}
	}
	
	@Override
	public List<KrnVcsChange> getVcsChangesByUID(UUID usId, int isFixd, int isRepld, long userId, long replId, String uid) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.getVcsChangesByUID(isFixd,isRepld,userId,replId,uid);
		} finally {
			s.close();
		}
	}

	@Override
	public List<KrnVcsChange> getVcsHistoryChanges(UUID usId, boolean isModel, String uid, int typeId,boolean isLastChange) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.getVcsHistoryChanges(isModel, uid, typeId,isLastChange);
		} finally {
			s.close();
		}
	}
	
	@Override
	public List<KrnVcsChange> getVcsDifChanges(UUID usId, boolean isModel,long[] ids) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.getVcsDifChanges(isModel,ids);
		} finally {
			s.close();
		}
	}
	
	@Override
	public String getVcsHistoryDataIncrement(UUID usId, KrnVcsChange change)
			throws KrnException {
		Session s = getSession(usId);
		try {
			return s.getVcsHistoryDataIncrement(change);
		} finally {
			s.close();
		}
	}
	
	@Override
	public void commitVcsObjects(UUID usId,List<KrnVcsChange> changes, String comment) throws KrnException {
		Session s = getSession(usId);
		try {
			s.commitVcsObjects(changes, comment);
			s.commitTransaction();
		} finally {
			s.close();
		}
	}

	@Override
    public void rollbackVcsObjects(UUID usId, List<KrnVcsChange> changes) throws KrnException {
		Session s = getSession(usId);
		try {
			s.rollbackVcsObjects(changes);
			s.commitTransaction();
		} finally {
			s.close();
		}
	}

	@Override
	public void orderChanged(UUID usId, String operation, String type, List<String> orderIds) throws KrnException {
		Session s = getSessionWithNoDB(usId);
		try {
			s.orderChanged(s.getUserSession().getUserId(), operation, type, orderIds);
		} finally {
			s.close();
		}		
	}

	@Override
	public Map<Long,Long> getActiveFlows(UUID usId) throws KrnException {
		Session s = getSessionWithNoDB(usId);
		try {
			return s.getActiveFlows();
		} finally {
			s.close();
		}		
	}

	@Override
	public boolean getBindingModuleToUserMode(UUID usId) throws KrnException {
		return Session.getBindingModuleToUserMode();
	}

	@Override
	public boolean isDbReadOnly(UUID usId) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.isDbReadOnly();
		} finally {
			s.close();
		}
	}

	@Override
	public List<OrlangTriggerInfo> getOrlangTriggersInfo(UUID usId) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.getOrlangTriggersInfo();
		} finally {
			s.close();
		}
	}

	@Override
	public KrnClass getClassByUid(UUID usId, String uid) throws KrnException {
		Session s = getSessionWithNoDB(usId);
		try {
			return s.getClassByUid(uid);
		} finally {
			s.close();
		}
	}

	@Override
	public KrnAttribute getAttributeByUid(UUID usId, String uid) throws KrnException {
		Session s = getSessionWithNoDB(usId);
		try {
			return s.getAttributeByUid(uid);
		} finally {
			s.close();
		}
	}
	
	@Override
	public List<KrnAttribute> getAttributesByUidPart(UUID usId, String uid, long searchMethod) throws KrnException {
		Session s = getSessionWithNoDB(usId);
		try {
			return s.getAttributesByUidPart(uid, searchMethod);
		} finally {
			s.close();
		}
	}
	
	@Override
	public void setDbId(UUID usId, String name,long value) throws KrnException {
		Session s = getSession(usId);
		try {
			s.setDbId(name, value);
		} finally {
			s.close();
		}
	}
	@Override
	public boolean convertLinkForSysDb(UUID usId,long newBaseId, long oldBaseId) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.convertLinkForSysDb(newBaseId,oldBaseId);
		} finally {
			s.close();
		}
	}
	@Override
	public long getId(UUID usId, String tname) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.getId(tname);
		} finally {
			s.close();
		}
	}
	@Override
	public long getLastId(UUID usId, String tname) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.getLastId(tname);
		} finally {
			s.close();
		}
	}
	@Override
	public boolean isAllowConvertDb(UUID usId) throws KrnException {
		return Session.isAllowConvertDb();
	}

	@Override
	public String getUrlConnection(UUID usId) throws KrnException{
		Session s = getSession(usId);
		try {
			return s.getUrlConnection();
		} finally {
			s.close();
		}
	}

	@Override
	public List runSql(UUID usId, String sql, int limit, boolean isUpdate) throws KrnException {
		Session s = getSession(usId);
		try {
			return s.runSql(sql, limit, isUpdate);
		} finally {
			s.close();
		}
	}

	@Override
	public boolean setVcsUserForObject(UUID usId, KrnVcsChange change, long userId) throws KrnException {
		Session s = getSession(usId);
		boolean res = false;
		try {
			res = s.setVcsUserForObject(change, userId);
			s.commitTransaction();
		} finally {
			s.close();
		}
		return res;
	}

	@Override
	public void setLoggingGetObjSql(UUID usId, boolean logginGetObjSql) throws KrnException {
		Driver2.setLoggingGetObjSql(logginGetObjSql);
	}

	@Override
	public void getout(UUID usId, KrnObject user, String message) throws KrnException {
		Session s = getSessionWithNoDB(usId);
		try {
			s.getout(user, message);
		} finally {
			s.close();
		}
	}

	@Override
	public void sendMessage(UUID usId, KrnObject user, String message) throws KrnException {
		Session s = getSessionWithNoDB(usId);
		try {
			s.sendMessage(user, message);
		} finally {
			s.close();
		}
	}
	
	@Override
	public KrnObject sendNotification(UUID usId, KrnObject user, String message, String uid, String cuid) throws KrnException {
		Session s = getSession(usId);
		try {
			KrnObject not = s.sendNotification(user, message, uid, cuid, null, null, -1);
			s.commitTransaction();
			return not;
		} finally {
			s.close();
		}
	}
	
	@Override
	public KrnObject sendNotification(UUID usId, KrnObject user, String message, String uid, String cuid, long trId) throws KrnException {
		Session s = getSession(usId);
		try {
			KrnObject not = s.sendNotification(user, message, uid, cuid, null, null, trId);
			s.commitTransaction();
			return not;
		} finally {
			s.close();
		}
	}

	@Override
	public java.util.Date getServerStartupDatetime(UUID usId) {
		return Session.serverStartupDatetime;
	}

	@Override
	public long getLoggedInUsersCount(UUID usId, java.util.Date period) {
		if (period == null) {
			return Session.loggedInUsers.size();
		} else {
			long count = 0;
			for (ServerUserSession us: Session.loggedInUsers) {
				if (us.getStartTime().after(period)) {
					count++;
				}
			}
			return count;
		}
	}

	@Override
	public long getLoggedOutUsersCount(UUID usId, java.util.Date period) {
		if (period == null) {
			return Session.loggedOutUsers.size();
		} else {
			long count = 0;
			for (ServerUserSession us: Session.loggedOutUsers) {
				if (us.getStartTime().after(period)) {
					count++;
				}
			}
			return count;
		}
	}

	@Override
	public List<Object> filterGroup(UUID usId, KrnObject filterObj, long trId) throws KrnException {
	    Session s = getSession(usId);
	    try {
	        List<Object> strList = s.filterGroup(filterObj, trId);
	        return strList;
	    }
	    finally
	    {
	        s.close();
	    }
	}

	@Override
	public long filterToAttr(UUID usId, KrnObject filterObj, long pobjId, long attrId, long trId) throws KrnException {
	    Session s = getSession(usId);
	    long res=0;
	    try {
	        res= s.filterToAttr(filterObj, pobjId, attrId, trId);
			s.commitTransaction();
	    }
	    finally
	    {
	        s.close();
	    }
        return res;
	}

	@Override
	public List<String> showDbLocks(UUID usId) throws KrnException {
	    Session s = getSession(usId);
	    try {
	        List<String> strList = s.showDblocks();
	        return strList;
	    } finally {
	        s.close();
	    }
	}

	@Override
	public List<Long> getFiltersContainingAttr(UUID usId, KrnAttribute attr) throws KrnException {
	    Session s = getSession(usId);
	    try {
	    	return s.getFiltersContainingAttr(attr);
	    } finally {
	        s.close();
	    }
	}

	@Override
	public boolean isRNDB(UUID usId) throws KrnException {
		return Database.isRnDB;
	}
	
	@Override
	public boolean isULDB(UUID usId) throws KrnException {
		return Database.isUlDB;
	}
	
	@Override
	public boolean hasUseECP(UUID usId) throws KrnException {
		Session s = null;
		try {
			s = getSessionWithNoDB(usId);
			return s.hasUseECP();
		} finally {
			s.close();
		}
	}

	//Jcr repository
	@Override
	public String putRepositoryData(UUID usId, String paths, String fileName, byte[] data) throws KrnException {
		Session s = null;
		try {
			s = getSessionWithNoDB(usId);
			return s.putRepositoryData(paths, fileName, data);
		} finally {
			s.close();
		}
	}

	@Override
	public byte[] getRepositoryData(UUID usId, String docId) throws KrnException {
		Session s = null;
		try {
			s = getSessionWithNoDB(usId);
			return s.getRepositoryData(docId);
		} finally {
			s.close();
		}
	}

	@Override
	public String getRepositoryItemName(UUID usId, String docId) throws KrnException {
		Session s = null;
		try {
			s = getSessionWithNoDB(usId);
			return s.getRepositoryItemName(docId);
		} finally {
			s.close();
		}
	}

	@Override
	public String getRepositoryItemType(UUID usId, String docId) throws KrnException {
		Session s = null;
		try {
			s = getSessionWithNoDB(usId);
			return s.getRepositoryItemType(docId);
		} finally {
			s.close();
		}
	}

	@Override
	public boolean dropRepositoryItem(UUID usId, String docId) throws KrnException {
		Session s = null;
		try {
			s = getSessionWithNoDB(usId);
			return s.dropRepositoryItem(docId);
		} finally {
			s.close();
		}
	}
	@Override
	public List<String> searchByQuery(UUID usId, String searchName) throws KrnException {
		Session s = null;
		try {
			s = getSessionWithNoDB(usId);
			return s.searchByQuery(searchName);
		} finally {
			s.close();
		}
	}

	@Override
	public String compileFilter(UUID usId, long filterId,Element xml) throws KrnException {
		Session s = getSession(usId);
		String res="";
    	try {
    		res=s.compileFilter(filterId, xml);
    	} finally {
    		s.close();
    	}
    	return res;
	}

	@Override
	public List<KrnObject> filterLocal(UUID usId, String sql, int limit,int beginRow,int endRow, long trId) throws KrnException {
		Session s = null;
		try {
			s = getSession(usId);
			return s.filterLocal(sql, limit, beginRow, endRow, trId);
		} finally {
			s.close();
		}
	}

	@Override
	public Map<String, String> getStringUidMap(UUID usId, String[] scopeUids) throws KrnException {
		Session s = getSession(usId);
    	try {
    		return s.getStringUidMap(scopeUids);
    	} finally {
    		s.close();
    	}
	}
	
	public boolean isSerializable(Object obj) {
		
		if (obj == null)
			return true;
		
		if (obj instanceof Serializable) {
			if (obj instanceof Collection) {
				Collection<?> list = (Collection<?>)obj;
				
				for (Object child : list) {
					boolean childRes = isSerializable(child);
					if (!childRes)
						return false;
				}
			} else if (obj instanceof Map) {
				Map<?, ?> map = (Map<?, ?>)obj;
				for (Object key : map.keySet()) {
					boolean childRes = isSerializable(key);
					if (!childRes)
						return false;
					childRes = isSerializable(map.get(key));
					if (!childRes)
						return false;
				}
				
			}
			return true;
		}
		
		return false;
	}

	@Override
	public boolean isProcessRunning(UUID usId, long flowId) throws KrnException {
		Session s = getSession(usId);
    	try {
    		return s.isRunning(flowId);
    	} finally {
    		s.close();
    	}
	}
	
	@Override
	public String getProcAllowed(UUID usId) throws KrnException {
		Session s = null;
		try {
			s = getSession(usId);
			return s.getProcAllowed();
		} finally {
			s.close();
		}
	}

	@Override
	public String getProcDenied(UUID usId) throws KrnException {
		Session s = null;
		try {
			s = getSession(usId);
			return s.getProcDenied();
		} finally {
			s.close();
		}
	}
	
	@Override
    public boolean getActivateScheduler(UUID usId) {
        boolean res = false;
        try {
            Session s = null;
            try {
                s = getSession(usId);
                res = s.getServerTasks(s.getDsName()).getActivateScheduler();
            } finally {
                s.close();
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public void setActivateScheduler(UUID usId, boolean activateScheduler) {
        try {
            Session s = null;
            try {
                s = getSession(usId);
                s.getServerTasks(s.getDsName()).setActivateScheduler(activateScheduler, s);
            } finally {
                s.close();
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }
}