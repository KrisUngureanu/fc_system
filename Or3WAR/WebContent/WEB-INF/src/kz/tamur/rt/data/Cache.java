package kz.tamur.rt.data;

import com.cifs.or2.client.Filter;
import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.ClassNode;
import com.cifs.or2.client.SwingWorker;
import com.cifs.or2.client.gui.DataCashListener;
import com.cifs.or2.kernel.*;

import static kz.tamur.rt.data.RecordStatus.*;
import static kz.tamur.util.CollectionTypes.*;

import kz.tamur.comps.OrFrame;
import kz.tamur.ods.AttrRequest;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.adapters.OrCalcRef;
import kz.tamur.util.CacheChangeRecord;
import kz.tamur.util.Funcs;
import kz.tamur.web.common.WebSession;
import kz.tamur.web.common.WebSessionManager;
import kz.tamur.web.component.WebFrame;
import kz.tamur.web.controller.WebController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.event.EventListenerList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.Date;

import static kz.tamur.util.CacheChangeRecord.*;

public class Cache {

	private Log log;

	private static final int ET_COMMITTED = 0;
	private static final int ET_ROLLBACKED = 1;
	private static final int ET_CLEARED = 2;
	private static final int ET_BEFOR_COMMITTED = 3;

	private static final int ACTION_ADD = 0;
	private static final int ACTION_DELETE = 1;
	private static final int ACTION_CHANGE = 2;
	private static final int ACTION_CHANGE_STATUS = 3;
	private static final int ACTION_ADD_OBJECT = 4;
	private static final int ACTION_DELETE_OBJECT = 5;
	
	private final Kernel krn;
	private final WebSession s;

	private long tid;

	private long newObjId = -1;

	private CashChangeSupport cashChangeSupport = new CashChangeSupport();
	private EventListenerList listeners = new EventListenerList();
	private SortedSet<AttrRecord> recs = Collections
			.synchronizedSortedSet(new TreeSet<AttrRecord>());
	private SortedSet<ObjectRecord> objRecs = Collections
			.synchronizedSortedSet(new TreeSet<ObjectRecord>());
	private Map<Long, KrnObject> objById = Collections
			.synchronizedMap(new HashMap<Long, KrnObject>());
	private Map<Long, Set<KrnAttribute>> rattrs = new HashMap<Long, Set<KrnAttribute>>();
	private Map<String, Object> vars = new HashMap<String, Object>();

	private List<CacheChangeRecord> changes = new ArrayList<CacheChangeRecord>();
	
	private Map<String, KrnObject> objByUid =
		Collections.synchronizedMap(new HashMap<String, KrnObject>());

	private long logIfcId = -1;
	
	private Map<Long, Stack<ReverseAction>> reverseActions = new HashMap<Long, Stack<ReverseAction>>();

	public Cache(Kernel krn, WebSession s) {
		this.krn = krn;
		this.s = s;
    	UserSessionValue us = krn.getUserSession();
    	log = WebSessionManager.getLog(us.dsName, us.logName);
	}

	public void setTransactionId(long tid) {
		this.tid = tid;
	}

	public long getTransactionId() {
		return tid;
	}

	public void addCashListener(DataCashListener l, OrFrame frm) {
		listeners.add(DataCashListener.class, l);
		if (frm instanceof WebFrame) ((WebFrame)frm).addCashListener(l);
	}

	public void removeCashListener(DataCashListener l) {
		listeners.remove(DataCashListener.class, l);
	}

	public void addCashChangeListener(long attrId, CashChangeListener l, OrFrame frm) {
		cashChangeSupport.addCashChangeListener(attrId, l);
		if (frm instanceof WebFrame) ((WebFrame)frm).addCashChangeListener(attrId, l);
	}

	public void removeCashChangeListener(CashChangeListener l) {
		cashChangeSupport.removeCashChangeListener(l);
	}

	private void fireEvent(int type) {
		Object[] listeners = this.listeners.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == DataCashListener.class) {
				DataCashListener l = (DataCashListener) listeners[i + 1];
				switch (type) {
					case ET_COMMITTED :
						l.cashCommitted();
						break;
					case ET_ROLLBACKED :
						l.cashRollbacked();
						break;
					case ET_CLEARED :
						l.cashCleared();
						break;
					case ET_BEFOR_COMMITTED :
						l.beforeCommitted();
						break;
				}
			}
		}
	}

	/**
	 * 
	 * @param objIds
	 *            идентификаторы объектов, для которых запрашиваются значения
	 *            атрибута.
	 * @param attr
	 *            атрубт.
	 * @param langId
	 *            язык.
	 * @param receiver
	 *            получатель данных. Если данные отуствуют в кэше, то они будут
	 *            получены асинхронно и переданы получателю. Если receiver равен
	 *            null, то данные передаются только в синхронном режиме.
	 * @return возвращает отсортированный набор записей, или null если данные
	 *         отсутсвуют в кэше и будут получены асинхронно
	 * @throws KrnException
	 */
	public SortedSet<Record> getRecords(
			final long[] objIds,
			final KrnAttribute attr,
			final long langId,
			final long[] filterIds,
			final Set<Long> lostIds,
			final RecordsReceiver receiver
			) throws KrnException {
		return getRecords(objIds, attr, langId, filterIds, lostIds, receiver, true, null);
	}
	
	public SortedSet<Record> getRecords(
			final long[] objIds,
			final KrnAttribute attr,
			final long langId,
			final long[] filterIds,
			final Set<Long> lostIds,
			final RecordsReceiver receiver,
			final boolean loadMissed,
			final AttrRequest req
			) throws KrnException {
		return getRecords(objIds, attr, langId, filterIds, lostIds, new int[1], receiver, loadMissed, req);
	}
	
	public SortedSet<Record> getRecords(
			final long[] objIds,
			final KrnAttribute attr,
			final long langId,
			final long[] filterIds,
			final Set<Long> lostIds,
			final int[] limit,
			final RecordsReceiver receiver,
			final boolean loadMissed,
			final AttrRequest req
			) throws KrnException {

		final SortedSet<Record> res = new TreeSet<Record>();
		if(objIds!=null){		
			for (long objId : objIds) {
				AttrRecord head = new AttrRecord(objId, attr.id, langId, 0);
				AttrRecord tail = new AttrRecord(objId, attr.id, langId,
						Integer.MAX_VALUE);
				
				synchronized (recs) {
					SortedSet<AttrRecord> set = recs.subSet(head, tail);
					if (filterIds != null && filterIds.length > 0) {
						List<AttrRecord> list = new ArrayList<AttrRecord>(set.size());
						list.addAll(set);
						recs.removeAll(list);
						set.clear();
					}
					if (set.size() > 0) {
						for (Record r : set) {
							int st = r.getStatus();
							if ((st & NaD) == 0 && r.getValue() != null) {
								res.add(r);
							}
						}
					} else if (objId > 0) {
						lostIds.add(objId);
					}
				}
			}
		}
		if (lostIds.size() > 0 && loadMissed) {
			// Если есть данные отсутствующие в кэше,
			// то пополняем кэш и возвращаем.
			SwingWorker sw = new SwingWorker() {
				public Object construct() {
					try {
						SortedSet<AttrRecord> rs = addToCash(lostIds, attr,
								langId, filterIds, limit, req);
						for (Record r : rs) {
							if (r.getValue() != null) {
								res.add(r);
							}
						}
					} catch (KrnException e) {
						log.error(e, e);
					}
					return res;
				}

				public void finished() {
					super.finished();
					receiver.setRecords(res);
				}
			};
			if (receiver == null) {
				// Если отсутствует получатель, то возвращаем результат
				// синхронно.
				sw.construct();
				return res;
			} else {
				// Иначе асинхронно.
				sw.start();
				return null;
			}
		} else {
			// Иначе возвращаем результат немедленно.
			putRevAttrs(attr);
			return res;
		}
	}

	/**
	 * 
	 * @param objIds
	 *            идентификаторы объектов, для которых запрашиваются значения
	 *            атрибута.
	 * @param attr
	 *            атрубт.
	 * @param langId
	 *            язык.
	 * @param receiver
	 *            получатель данных. Если данные отуствуют в кэше, то они будут
	 *            получены асинхронно и переданы получателю. Если receiver равен
	 *            null, то данные передаются только в синхронном режиме.
	 * @return возвращает отсортированный набор записей, или null если данные
	 *         отсутсвуют в кэше и будут получены асинхронно
	 * @throws KrnException
	 */
	public SortedSet<Record> getRecords(
			final long[] objIds,
			final KrnAttribute attr,
			final long langId,
			final RecordsReceiver receiver
			) throws KrnException {
		Set<Long> lostIds = new HashSet<Long>();
		return getRecords(objIds, attr, langId, new long[0], lostIds, receiver);
	}

	public Record getRecord(final long objId, final KrnAttribute attr,
			final long langId, int index) throws KrnException {
		Set<Long> lostIds = new HashSet<Long>();
		return getRecord(objId, attr, langId, index, lostIds, null);
	}
	
	public Record getRecord(final long objId, final KrnAttribute attr,
			final long langId, int index, Set<Long> lostIds, AttrRequest req) throws KrnException {
		SortedSet<Record> set = getRecords(new long[] {objId}, attr, langId, new long[0], lostIds, null, true, req);
		if (index < 0)
			index = Math.max(set.size() + index, 0);
		Record res = Funcs.find(set, objId, attr.id, langId, index);
		return (res != null) ? (res.getValue() != null) ? res : null : null;
	}

	public Record getRecord(final long objId, final KrnAttribute attr,
			final long langId, final Object value) throws KrnException {
		SortedSet<Record> set = getRecords(new long[]{objId}, attr, langId,
				null);
		Record res = Funcs.findByValue(set, objId, attr.id, langId, value);
		return (res != null) ? (res.getValue() != null) ? res : null : null;
	}

	public Set<ObjectRecord> getObjects(final long classId,
			final long filterId, final RecordsReceiver receiver)
			throws KrnException {
		return getObjects(classId, filterId, new int[1], false, receiver);
	}
	
	public Set<ObjectRecord> getObjects(final long classId,
			final long filterId, int[] limit, boolean isLoaded,	final RecordsReceiver receiver)
			throws KrnException {

		ObjectRecord low = new ObjectRecord(classId);
		ObjectRecord high = new ObjectRecord(classId + 1);
		Set<ObjectRecord> res = objRecs.subSet(low, high);
		if (!isLoaded || res.isEmpty() || filterId != 0) {
			if (!res.isEmpty()) res.clear();
			if (filterId > 0)
				res = new LinkedHashSet<ObjectRecord>();
			
			Filter uf = krn.getUser().getCurrentFilter();
			KrnClass cls = krn.getClassNode(classId).getKrnClass();
			long[] ufids = (uf != null && uf.className.equals(cls.name))
					? new long[]{uf.obj.id}
					: new long[0];
			if (filterId > 0) {
				if (ufids.length > 0) {
					ufids = new long[]{ufids[0], filterId};
				} else {
					ufids = new long[]{filterId};
				}
			}
			KrnObject[] os = krn.getClassObjects(cls, ufids, limit, tid);
			for (int i = 0; i < os.length; ++i) {
				ObjectRecord r = new ObjectRecord(classId, os[i]);
				objRecs.add(r);
				if (filterId > 0) res.add(r);
				putObject(os[i]);
			}
			if (filterId == 0)
				res = objRecs.subSet(low, high);
		}
		return res;
	}

	public SortedSet<ObjectRecord> findRecords(KrnObject[] objs) {
		SortedSet<ObjectRecord> res = new TreeSet<ObjectRecord>();
		if (objs != null && objs.length > 0) {
			long classId = objs[0].classId;
			ObjectRecord low = new ObjectRecord(classId);
			ObjectRecord high = new ObjectRecord(classId + 1);
			SortedSet<ObjectRecord> set = objRecs.subSet(low, high);
			for (KrnObject obj : objs) {
				ObjectRecord r = Funcs.find(set, classId, obj);
				if (r == null) {
					r = new ObjectRecord(classId, obj);
					putObject(obj);
					objRecs.add(r);
				}
				res.add(r);
			}
		}
		return res;
	}

    public List<ObjectRecord> findRecords(KrnObject[] objs, boolean sort) {
        List<ObjectRecord> res = new ArrayList<ObjectRecord>();
        if (objs != null && objs.length > 0) {
            long classId = objs[0].classId;
            ObjectRecord low = new ObjectRecord(classId);
            ObjectRecord high = new ObjectRecord(classId + 1);
            SortedSet<ObjectRecord> set = objRecs.subSet(low, high);
            for (KrnObject obj : objs) {
                ObjectRecord r = Funcs.find(set, classId, obj);
                if (r == null) {
                    r = new ObjectRecord(classId, obj);
                    putObject(obj);
                    objRecs.add(r);
                }
                res.add(r);
            }
        }
        return res;
    }

	public ObjectRecord findRecord(KrnObject obj) {
		if (obj != null) {
			long classId = obj.classId;
			ObjectRecord low = new ObjectRecord(classId);
			ObjectRecord high = new ObjectRecord(classId + 1);
			SortedSet<ObjectRecord> set = objRecs.subSet(low, high);
			ObjectRecord r = Funcs.find(set, classId, obj);
			if (r == null) {
				r = new ObjectRecord(classId, obj);
				putObject(obj);
				objRecs.add(r);
			}
			return r;
		}
		return null;
	}

    public Record createObject(long classId) {
        KrnObject obj = new KrnObject(--newObjId, "" + newObjId, classId);
        putObject(obj);
        ObjectRecord or = new ObjectRecord(classId, obj);
        or.setStatus(CREATED);
        objRecs.add(or);
        CacheChangeRecord ccr = new CacheChangeRecord(CREATE_OBJECT, 0, 0, 0, 0, obj, false);
        changes.add(ccr);
        if (logIfcId > -1) {
            ReverseAction ra = new ReverseAction();
            ra.record = or;
            ra.action = ACTION_ADD_OBJECT;
            ra.change = ccr;
            getCacheChanges(logIfcId).push(ra);
        }
        cashChangeSupport.fireObjectCreated(this, or.getClassId(), or.getObjId());
        return or;
    }

	public Record createObject(long classId, String uid) {
		try {
			KrnClass cls = krn.getClassNode(classId).getKrnClass();
			KrnObject obj = krn.createObject(cls, uid, tid);
			putObject(obj);
			ObjectRecord or = new ObjectRecord(classId, obj);
			objRecs.add(or);
			CacheChangeRecord ccr = new CacheChangeRecord(CREATE_OBJECT, 0, 0, 0, 0, obj, false);
			changes.add(ccr);

			if (logIfcId > -1) {
				ReverseAction ra = new ReverseAction();
				ra.record = or;
				ra.action = ACTION_ADD_OBJECT;
				ra.change = ccr;
				getCacheChanges(logIfcId).push(ra);
			}
			return or;
		} catch (KrnException e) {
			log.error(e, e);
			return null;
		}
	}

	public boolean changeObjectAttribute(Record rec, Object val, Object originator)
			throws KrnException {

		checkValueType(rec.getAttrId(), val);

		Object oldValue = rec.getValue();
		if (val == null) {
			if (oldValue != null) {
				deleteObjectAttribute(rec, originator, true);
			}
		} else {
			int status = rec.getStatus();

			if (val.equals(oldValue) && (status & NaD) == 0) {
				return false;
			}
	
			if ((status & NaD) > 0) {
				rec.setStatus(rec.getStatus() & ~NaD);
			}
			if ((status & (INSERTED | MODIFIED)) == 0)
				rec.setStatus(MODIFIED);
	
			if (val instanceof KrnObject) {
				KrnObject o = (KrnObject) val;
				putObject(o);
			}
	
			rec.setValue(val);
			
			KrnAttribute attr = krn.getAttributeById(rec.getAttrId());
			
			CacheChangeRecord ccr = new CacheChangeRecord(
					CHANGE_ATTR,
					rec.getObjId(),
					rec.getAttrId(),
					rec.getIndex(),
					rec.getLangId(),
					convertValue(rec.getAttrId(), rec.getValue()),
					attr.collectionType == COLLECTION_SET ? convertValue(rec.getAttrId(), oldValue) : null,
					false);
			changes.add(ccr);
	
			cashChangeSupport.fireObjectChanged(originator, rec.getObjId(), rec.getAttrId());
			
			if (logIfcId > -1) {
				ReverseAction ra = new ReverseAction();
				ra.action = ACTION_CHANGE;
				ra.record = rec;
				ra.status = status;
				ra.value = oldValue;
				ra.change = ccr;
				getCacheChanges(logIfcId).push(ra);
			}
			
			KrnObject obj = objById.get(rec.getObjId());
			
			// Обработка линков
			if (originator != this) {
				putRevAttrs(attr);
			}
			Set<KrnAttribute> rattrs = getRevAttrs(rec.getAttrId());
			if (rattrs != null && rattrs.size() > 0) {
				if (oldValue instanceof KrnObject) {
					removeFromRevAttrObject((KrnObject) oldValue, rattrs,
							obj);
				}
				if (originator != this && val != null) {
					// Добавляем объект в обратные атрибуты нового значения
					addToRevAttrObject(obj, rattrs, (KrnObject) val);
				}
			}
		}
		
		return true;
	}
    protected boolean contains(List<ClassNode> classes, ClassNode cls) {
        for (int i = 0; i < classes.size(); i++) {
            ClassNode krnClass = classes.get(i);
            if (krnClass.getKrnClass().id == cls.getKrnClass().id) {
                return true;
            }
        }
        return false;
    }

	private void addToRevAttrObject(KrnObject obj, Set<KrnAttribute> rattrs,
			KrnObject val) throws KrnException {
        ClassNode cnode = krn.getClassNode(obj.classId);
        List<ClassNode> objSuperClasses = new ArrayList<ClassNode>();
        cnode.getSuperClasses(objSuperClasses);
		// Добавляем объект в обратные атрибуты нового значения
		for (KrnAttribute rattr : rattrs) {
            ClassNode cls = krn.getClassNode(rattr.typeClassId);
			if (contains(objSuperClasses, cls)) {//если тип класса обратного атрибута,
                // находится среди старших классов объекта obj, то вставляем объект obj в значение этого атрибута
				if (rattr.collectionType == COLLECTION_SET
						|| rattr.collectionType == COLLECTION_ARRAY) {
					// Делаем выборку для загрузки кэша
					getRecords(new long[] {val.id}, rattr, 0, null);
					
					int oldSize = changes.size();
					Record r = insertObjectAttribute(val, rattr, -1, 0, obj,
							this);
					// Удаляем запись из лога, т.к. происходит изменение обратного
					// атрибута
					if (changes.size() > oldSize)
						changes.remove(changes.size() - 1);
				} else {
					Record r = getRecord(val.id, rattr, 0, 0);
					int oldSize = changes.size();

					if (r == null) {
						r = insertObjectAttribute(val, rattr, 0, 0, obj, this);
					} else {
						changeObjectAttribute(r, obj, this);
					}
					// Удаляем запись из лога, т.к. происходит изменение обратного
					// атрибута
					if (changes.size() > oldSize)
						changes.remove(changes.size() - 1);
				}
			}
		}
	}

	private void removeFromRevAttrObject(KrnObject obj, Set<KrnAttribute> rattrs,
			KrnObject val) throws KrnException {
		// Удаляем объект из обратных атрибутов старого значения
		long[] objIds = {val.id};
		for (KrnAttribute rattr : rattrs) {
			Set<Long> lostIds = new HashSet<Long>();
			SortedSet<Record> recs = getRecords(objIds, rattr, 0, new long[0], lostIds, null, false, null);
			for (Record r : recs) {
				KrnObject vobj = (KrnObject) r.getValue();
				if (vobj != null && vobj.id == obj.id) {
					deleteObjectAttribute(r, this, false);
					// Удаляем запись из лога, т.к. происходит изменение обратного
					// атрибута
					changes.remove(changes.size() - 1);
				}
			}
		}
	}

	public void deleteObject(Record rec, Object originator) throws KrnException {
		objRecs.remove(rec);
		CacheChangeRecord ccr = new CacheChangeRecord(DELETE_OBJECT, 0, 0, 0, 0, rec.getValue(), false);
		changes.add(ccr);
		
		if (logIfcId > -1) {
			ReverseAction ra = new ReverseAction();
			ra.record = rec;
			ra.action = ACTION_DELETE_OBJECT;
			ra.change = ccr;
			getCacheChanges(logIfcId).push(ra);
		}		
		cashChangeSupport.fireObjectDeleted(this, rec.getClassId(), rec.getObjId());
	}
	
	public boolean isDel(KrnObject obj) {
		boolean res = false;
		for(CacheChangeRecord ccr: changes) {
			if(ccr.changeType == DELETE_OBJECT && ccr.value.equals(obj)) {
				return true;
			}
		}
		return res;
	}

	public void deleteObjectAttribute(Record rec, Object originator, boolean isSetToNull)
			throws KrnException {
		if (rec != null) {
			AttrRecord head = new AttrRecord(rec.getObjId(), rec.getAttrId(),
					rec.getLangId(), 0);
			AttrRecord tail = new AttrRecord(rec.getObjId(), rec.getAttrId(),
					rec.getLangId(), Integer.MAX_VALUE);
			SortedSet<AttrRecord> set1 = recs.subSet(head, tail);
			SortedSet<AttrRecord> set = set1.subSet((AttrRecord)rec, tail);
			
			ReverseAction ra = new ReverseAction();
			if (set1.size() == 1) {
				int status = rec.getStatus();
				rec.setStatus(NaD);
				ra.action = ACTION_CHANGE_STATUS;
				ra.record = rec;
				ra.status = status;
			} else {
				set.remove(rec);
				for (AttrRecord r : set)
					r.setIndex(r.getIndex() - 1);
				ra.action = ACTION_DELETE;
				ra.record = rec;
			}
			CacheChangeRecord ccr = new CacheChangeRecord(
					DELETE_ATTR,
					rec.getObjId(),
					rec.getAttrId(),
					rec.getIndex(),
					rec.getLangId(),
					convertValue(rec.getAttrId(), rec.getValue()),
					true,
					isSetToNull);
			changes.add(ccr);

			if (logIfcId > -1) {
				ra.change = ccr;
				getCacheChanges(logIfcId).push(ra);
			}
			
			cashChangeSupport.fireObjectChanged(originator, rec.getObjId(), rec.getAttrId());
			Object val = rec.getValue();
			KrnObject obj = objById.get(rec.getObjId());
			// Обработка линков
			Set<KrnAttribute> rattrs = getRevAttrs(rec.getAttrId());
			if (rattrs != null && rattrs.size() > 0) {
				if (val instanceof KrnObject) {
					removeFromRevAttrObject(obj, rattrs, (KrnObject) val);
				}
			}
		}
	}

	public boolean hasChanges() {
		return changes.size() > 0;
	}
	
	public void commit(long flowId) throws KrnException {
		fireEvent(ET_BEFOR_COMMITTED);

		LongPair[] res = null;
		List<CacheChangeRecord> list = null;
		
		synchronized (changes) {
			if (changes.size() > 0) {
				for (Iterator<CacheChangeRecord> it = changes.iterator(); it.hasNext();) {
					CacheChangeRecord r = it.next();
					if ((r.changeType == CHANGE_ATTR || r.changeType == DELETE_ATTR)
							&& r.value == null) {
						it.remove();
					}
				}
				list = new ArrayList<CacheChangeRecord>(changes.size());
				list.addAll(changes);
				changes.clear();
			}
		}
		if (list != null)
			res = krn.commit2(list, tid);
		
		if (res != null && res.length > 0) {
			Map<Long, Long> ids = new HashMap<Long, Long>();
			for (LongPair p : res) {
				ids.put(p.first, p.second);
				KrnObject obj = objById.remove(p.first);
				objById.put(p.second, obj);
				objByUid.remove("" + p.first);
			}
			apply(ids);
		}

		fireEvent(ET_COMMITTED);
	}

	private void apply(Map<Long, Long> ids) {
		SortedSet<ObjectRecord> newObjRecs = new TreeSet<ObjectRecord>();
		for (Iterator<ObjectRecord> it = objRecs.iterator(); it.hasNext();) {
			ObjectRecord rec = it.next();
			if ((rec.getStatus() & CREATED) > 0) {
				KrnObject obj = (KrnObject) rec.getValue();
				if (obj.id < 0)
					obj.id = ids.get(obj.id);
				rec.setStatus(UPTODATE);
				newObjRecs.add(rec);
				it.remove();
			}
		}
		objRecs.addAll(newObjRecs);

		SortedSet<AttrRecord> newRecs = new TreeSet<AttrRecord>();
		long currObjId = 0;
		long currAttrId = 0;
		long currLangId = 0;
		for (Iterator<AttrRecord> it = recs.iterator(); it.hasNext();) {
			AttrRecord rec = it.next();
			if (currObjId != rec.getObjId()) {
				currLangId = rec.getLangId();
				currAttrId = rec.getAttrId();
				currObjId = rec.getObjId();
			}
			if (currAttrId != rec.getAttrId()) {
				currLangId = rec.getLangId();
				currAttrId = rec.getAttrId();
			}
			if (currLangId != rec.getLangId()) {
				currLangId = rec.getLangId();
			}
			if (rec.getValue() instanceof KrnObject) {
				KrnObject obj = (KrnObject) rec.getValue();
				if (obj.id < 0) {
					obj.id = ids.get(obj.id);
				}
			}
			if ((rec.getStatus() & NaD) == 0) {
				rec.setStatus(UPTODATE);
			}
			if (rec.getObjId() < 0) {
				it.remove();
				Long id = ids.get(rec.getObjId());
				if (id != null) {
					rec.setObjId(id);
					newRecs.add(rec);
				}
			}
		}
		recs.addAll(newRecs);
	}

	private void processChangeRecord(AttrRecord rec, boolean isInserting,
			long langId, List<LongChange> chObjs, List<StringChange> chStrs,
			List<LongChange> chLongs, List<FloatChange> chFloats,
			List<DateChange> chDates, List<TimeChange> chTimes,
			List<BlobValue> chBlobs) throws KrnException {
		if (rec.getValue() != null) {
			KrnAttribute attr = krn.getAttributeById(rec.getAttrId());
			if (attr.typeClassId == Kernel.IC_STRING
					|| attr.typeClassId == Kernel.IC_MEMO) {
				chStrs.add(new StringChange(rec.getObjId(), rec.getAttrId(),
						rec.getLangId(), rec.getIndex(), isInserting,
						(String) rec.getValue()));
			} else if (attr.typeClassId == Kernel.IC_INTEGER) {
				chLongs.add(new LongChange(rec.getObjId(), rec.getAttrId(), rec
						.getIndex(), isInserting, ((Number) rec.getValue())
						.longValue()));
			} else if (attr.typeClassId == Kernel.IC_BOOL) {
				long val = 0;
				Object v = rec.getValue();
				if (v instanceof Boolean) {
					val = ((Boolean) v).booleanValue() ? 1 : 0;
				} else if (v instanceof Number) {
					val = ((Number) v).longValue() > 0 ? 1 : 0;
				}
				chLongs.add(new LongChange(rec.getObjId(), rec.getAttrId(), rec
						.getIndex(), isInserting, val));
			} else if (attr.typeClassId == Kernel.IC_BLOB) {
				try {
					File f = (File) rec.getValue();
					FileInputStream is = new FileInputStream(f);
					byte[] val = new byte[(int) f.length()];
					is.read(val);
					is.close();
					chBlobs.add(new BlobValue(rec.getObjId(), rec.getAttrId(),
							val, rec.getLangId()));
				} catch (Exception ex) {
					log.error(ex, ex);
				}
			} else if (attr.typeClassId == Kernel.IC_FLOAT) {
				chFloats.add(new FloatChange(rec.getObjId(), rec.getAttrId(),
						rec.getIndex(), isInserting, ((Number) rec.getValue())
								.doubleValue()));
			} else if (attr.typeClassId == Kernel.IC_DATE) {
				chDates.add(new DateChange(rec.getObjId(), rec.getAttrId(), rec
						.getIndex(), isInserting, kz.tamur.util.Funcs
						.convertDate((Date) rec.getValue())));
			} else if (attr.typeClassId == Kernel.IC_TIME) {
				chTimes.add(new TimeChange(rec.getObjId(), rec.getAttrId(), rec
						.getIndex(), isInserting, kz.tamur.util.Funcs
						.convertTime((Date) rec.getValue())));
			} else {
				chObjs.add(new LongChange(rec.getObjId(), rec.getAttrId(), rec
						.getIndex(), isInserting,
						((KrnObject) rec.getValue()).id));
			}
		}
	}

	private void processDeletedRecord(KrnAttribute attr, AttrRecord rec, List<DelRecord> delVals) {
		Object v = (attr.collectionType == COLLECTION_SET)
			? rec.getValue() : rec.getIndex();
		delVals.add(new DelRecord(rec.getObjId(), rec.getAttrId(), v));
	}

	public void rollback(long flowId) throws KrnException {
		krn.rollbackLocked();
		clear();
		reset(flowId);
		fireEvent(ET_ROLLBACKED);
	}

	public Record copyRecord(KrnObject obj_, KrnAttribute attr) {
		return null;
	}

	public Record insertObjectAttribute(KrnObject obj, KrnAttribute attr,
			int i, long langId, Object val, Object originator)
			throws KrnException {

		checkValueType(attr.id, val);

		if (val instanceof KrnObject) {
			KrnObject o = (KrnObject) val;
			putObject(o);
		}

		Long objId = obj.id;
		Long attrId = attr.id;
		AttrRecord head = new AttrRecord(obj.id, attr.id, langId, 0);
		AttrRecord tail = new AttrRecord(obj.id, attr.id, langId,
				Integer.MAX_VALUE);
		SortedSet<AttrRecord> set = recs.subSet(head, tail);

		if (attr.collectionType == COLLECTION_NONE && set.size() > 0) {
			// Если атрбиут не массив и найдены записи, то просто модифицируем
			// запись
			Record r = set.first();
			changeObjectAttribute(r, val, originator);
			return r;
		}

		if (i == -1) {
			i = 0;
			if (set.size() > 0) {
				SortedSet<AttrRecord> rset = new TreeSet<AttrRecord>(
						Collections.reverseOrder(set.comparator()));
				rset.addAll(set);
				for (AttrRecord rec : rset) {
					int status = rec.getStatus();
					if (status == NaD) {
						i = 0;
						set.remove(rec);
						break;
					} else if (attr.collectionType != COLLECTION_NONE) {
						i = rec.getIndex() + 1;
						break;
					}
				}
			}
		}
		if (attr.collectionType == COLLECTION_NONE && i > 0) {
			String msg = "attribute \"" + attr.name + "\" is not array";
			log.error(msg);
			throw new KrnException(0, msg);
		}
		head.setIndex(i);
		SortedSet<AttrRecord> subSet = set.tailSet(head);
		AttrRecord rec = (subSet.size() > 0) ? subSet.first() : null;

		ReverseAction ra = new ReverseAction();

		if (rec == null || rec.getIndex() != i
				|| (rec.getStatus() & NaD) == 0) {
			for (AttrRecord r : subSet) {
				r.setIndex(r.getIndex() + 1);
			}
			rec = new AttrRecord(objId, attrId, langId, i, val);
			set.add(rec);
//			if ((rec.getStatus() & DELETED) == 0) rec.setStatus(INSERTED);
			rec.setStatus(INSERTED);

			ra.action = ACTION_ADD;
			ra.record = rec;
		} else {
			ra.action = ACTION_CHANGE;
			ra.record = rec;
			ra.status = rec.getStatus();
			ra.value = rec.getValue();

			rec.setValue(val);
			rec.setStatus(MODIFIED);
		}
		CacheChangeRecord ccr = new CacheChangeRecord(
				CHANGE_ATTR,
				rec.getObjId(),
				rec.getAttrId(),
				rec.getIndex(),
				rec.getLangId(),
				convertValue(rec.getAttrId(), rec.getValue()),
				true);
		changes.add(ccr);

		if (logIfcId > -1) {
			ra.change = ccr;
			getCacheChanges(logIfcId).push(ra);
		}
		
		cashChangeSupport.fireObjectChanged(originator, rec.getObjId(), rec.getAttrId());

		// Обработка линков
		if (originator != this) {
			putRevAttrs(attr);
		}
		Set<KrnAttribute> rattrs = getRevAttrs(attr.id);
		if (rattrs != null && rattrs.size() > 0) {
			if (originator != this && val != null) {
				// Добавляем объект в обратные атрибуты нового значения
				addToRevAttrObject(obj, rattrs, (KrnObject) val);
			}
		}

		return rec;
	}

    public void moveUpObjectAttribute(KrnObject obj, KrnAttribute attr,
            int i, long langId, Object originator)
            throws KrnException {
        if (i < 1)
            return;

        if (attr.collectionType == COLLECTION_NONE) {
            String msg = "attribute \"" + attr.name + "\" is not array";
            log.error(msg);
            throw new KrnException(0, msg);
        }

        AttrRecord head = new AttrRecord(obj.id, attr.id, langId, i-1);
        AttrRecord tail = new AttrRecord(obj.id, attr.id, langId, i+1);
        SortedSet<AttrRecord> set = recs.subSet(head, tail);
        if (set != null && set.size() == 2) {
            AttrRecord attr1 = set.first();
            AttrRecord attr2 = set.last();
            if (attr1 != null && attr2 != null) {
                Object obj1 = attr1.getValue();
                Object obj2 = attr2.getValue();
                attr2.setValue(obj1);
                attr1.setValue(obj2);

                int status = attr1.getStatus();
                if ((status & (INSERTED | MODIFIED)) == 0)
                    attr1.setStatus(MODIFIED);

    			if (logIfcId > -1) {
    				ReverseAction ra = new ReverseAction();
    				ra.action = ACTION_CHANGE;
    				ra.record = attr1;
    				ra.status = status;
    				ra.value = obj1;
    				getCacheChanges(logIfcId).push(ra);
    			}

                status = attr2.getStatus();
                if ((status & (INSERTED | MODIFIED)) == 0)
                    attr2.setStatus(MODIFIED);

                if (logIfcId > -1) {
    				ReverseAction ra = new ReverseAction();
    				ra.action = ACTION_CHANGE;
    				ra.record = attr2;
    				ra.status = status;
    				ra.value = obj2;
    				getCacheChanges(logIfcId).push(ra);
    			}
            }
        }
        cashChangeSupport.fireObjectChanged(originator, obj.id, attr.id);
    }

    public void moveDownObjectAttribute(KrnObject obj, KrnAttribute attr,
            int i, long langId, Object originator)
            throws KrnException {

    	if (attr.collectionType == COLLECTION_NONE) {
    		String msg = "attribute \"" + attr.name + "\" is not array";
    		log.error(msg);
            throw new KrnException(0, msg);
        }

    	AttrRecord head = new AttrRecord(obj.id, attr.id, langId, i);
    	AttrRecord tail = new AttrRecord(obj.id, attr.id, langId, i+2);
    	SortedSet<AttrRecord> set = recs.subSet(head, tail);
    	if (set != null && set.size() == 2) {
    		AttrRecord attr1 = set.first();
    		AttrRecord attr2 = set.last();
    		if (attr1 != null && attr2 != null) {
    			Object obj1 = attr1.getValue();
    			Object obj2 = attr2.getValue();
    			attr2.setValue(obj1);
    			attr1.setValue(obj2);

    			int status = attr1.getStatus();
    			if ((status & (INSERTED | MODIFIED)) == 0)
    				attr1.setStatus(MODIFIED);
    			
				if (logIfcId > -1) {
					ReverseAction ra = new ReverseAction();
					ra.action = ACTION_CHANGE;
					ra.record = attr1;
					ra.status = status;
					ra.value = obj1;
					getCacheChanges(logIfcId).push(ra);
				}

				status = attr2.getStatus();
              	if ((status & (INSERTED | MODIFIED)) == 0)
              		attr2.setStatus(MODIFIED);

              	if (logIfcId > -1) {
					ReverseAction ra = new ReverseAction();
					ra.action = ACTION_CHANGE;
					ra.record = attr2;
					ra.status = status;
					ra.value = obj2;
					getCacheChanges(logIfcId).push(ra);
				}
    		}
    	}
    	cashChangeSupport.fireObjectChanged(originator, obj.id, attr.id);
    }

    public void moveRowsBeforeObjectAttribute(KrnObject obj, KrnAttribute attr,
            int i, int[] rows, int[] mrows, long langId, Object originator)
            throws KrnException {

        if (attr.collectionType == COLLECTION_NONE) {
            String msg = "attribute \"" + attr.name + "\" is not array";
            log.error(msg);
            throw new KrnException(0, msg);
        }

        int min = Math.min(i, rows[0]);
        int max = Math.max(i, rows[rows.length-1]);

        AttrRecord head = new AttrRecord(obj.id, attr.id, langId, min);
        AttrRecord tail = new AttrRecord(obj.id, attr.id, langId, max+1);
        SortedSet<AttrRecord> set = recs.subSet(head, tail);
        int beforeI = 0;
        for (int k = 0; k<rows.length; k++) {
            if (i > rows[k]) {
                beforeI++;
            }
        }
        int imrowsIndex = -1;
        for (int k = 0; k<mrows.length; k++) {
            if (i == mrows[k]) {
                imrowsIndex = k;
                break;
            }
        }

        if (set != null) {
            Map<Integer, AttrRecord> attrs = new HashMap<Integer, AttrRecord>();
            Map<Integer, Object> objects = new HashMap<Integer, Object>();
          for (AttrRecord attrRecord : set) {
              int index = attrRecord.getIndex();
              Object obj1 = attrRecord.getValue();
              int mrowsIndex = -1;
              for (int k = 0; k<mrows.length; k++) {
                  if (index == mrows[k]) {
                      mrowsIndex = k;
                      break;
                  }
              }
              
              if (mrowsIndex > -1) {
                  boolean done = false;
                  int shift = 0;
                  if (index < i) {
                      for (int k = 0; k<beforeI; k++) {
                          if (index > rows[k]) {
                              shift--;
                          } else if (index == rows[k]) {
                              int ind = imrowsIndex - beforeI + k;
                              AttrRecord head1 = new AttrRecord(obj.id, attr.id, langId, mrows[ind]);
                              AttrRecord tail1 = new AttrRecord(obj.id, attr.id, langId, mrows[ind]+1);
                              SortedSet<AttrRecord> set1 = recs.subSet(head1, tail1);
                              AttrRecord attr1 = set1.first();
                              attrs.put(attr1.getIndex(), attr1);
                              objects.put(attr1.getIndex(), obj1);
                              //indexes.put(attr1, obj1);
                              done = true;
                              break;
                          }
                      }
                  } else {
                      shift = rows.length - beforeI;
                      for (int k = beforeI; k<rows.length; k++) {
                          if (index > rows[k]) {
                              shift--;
                          } else if (index == rows[k]) {
                              int ind = imrowsIndex - beforeI + k;
                              AttrRecord head1 = new AttrRecord(obj.id, attr.id, langId, mrows[ind]);
                              AttrRecord tail1 = new AttrRecord(obj.id, attr.id, langId, mrows[ind]+1);
                              SortedSet<AttrRecord> set1 = recs.subSet(head1, tail1);
                              AttrRecord attr1 = set1.first();
                              attrs.put(attr1.getIndex(), attr1);
                              objects.put(attr1.getIndex(), obj1);
//                              indexes.put(attr1, obj1);
                              done = true;
                              break;
                          }
                      }
                  }
                  if (!done) {
                      int ind = mrowsIndex + shift;
                      AttrRecord head1 = new AttrRecord(obj.id, attr.id, langId, mrows[ind]);
                      AttrRecord tail1 = new AttrRecord(obj.id, attr.id, langId, mrows[ind]+1);
                      SortedSet<AttrRecord> set1 = recs.subSet(head1, tail1);
                      AttrRecord attr1 = set1.first();
                      attrs.put(attr1.getIndex(), attr1);
                      objects.put(attr1.getIndex(), obj1);
//                      indexes.put(attr1, obj1);
                  }
              }
          }
            for (Iterator<Integer> it = attrs.keySet().iterator(); it.hasNext();) {
                Integer key = it.next();
                AttrRecord attr1 = attrs.get(key);
                Object obj1 = objects.get(key);
                attr1.setValue(obj1);
                int status = attr1.getStatus();
                if ((status & (INSERTED | MODIFIED)) == 0)
                    attr1.setStatus(MODIFIED);
            }
/*
            for (Iterator<AttrRecord> it = indexes.keySet().iterator(); it.hasNext();) {
                AttrRecord key = it.next();
                Object obj1 = indexes.get(key);
                key.setValue(obj1);
                int status = key.getStatus();
                if ((status & (INSERTED | MODIFIED)) == 0)
                    key.setStatus(MODIFIED);
            }
*/
        }
        cashChangeSupport.fireObjectChanged(originator, obj.id, attr.id);
    }

  public void changePlacesObjectAttribute(KrnObject obj, KrnAttribute attr,
          Record attr1, Record attr2, Object originator)
          throws KrnException {

      if (attr.collectionType == COLLECTION_NONE) {
          String msg = "attribute \"" + attr.name + "\" is not array";
          log.error(msg);
          throw new KrnException(0, msg);
      }

    if (attr1 != null && attr2 != null) {
        Object obj1 = attr1.getValue();
        Object obj2 = attr2.getValue();
        attr2.setValue(obj1);
        attr1.setValue(obj2);

        int status = attr1.getStatus();
        if ((status & (INSERTED | MODIFIED)) == 0)
            attr1.setStatus(MODIFIED);

        status = attr2.getStatus();
        if ((status & (INSERTED | MODIFIED)) == 0)
            attr2.setStatus(MODIFIED);
    }

    cashChangeSupport.fireObjectChanged(originator, obj.id, attr.id);
  }

	private SortedSet<AttrRecord> addToCash(Set<Long> objectIds,
			KrnAttribute attr, long langId, long[] filterIds, int[] limit, AttrRequest req) throws KrnException {
		
		Set<Long> objIds = new HashSet<Long>(objectIds);
		
		putRevAttrs(attr);

		SortedSet<AttrRecord> res = new TreeSet<AttrRecord>();
		long[] ids = Funcs.makeLongArray(objIds);
		if (attr.typeClassId == Kernel.IC_INTEGER
				|| attr.typeClassId == Kernel.IC_BOOL) {
			LongValue[] vs = krn.getLongValues(ids, attr, tid);
			for (LongValue v : vs) {
				res.add(new AttrRecord(v.objectId, attr.id, langId, v.index,
						v.value));
				objIds.remove(v.objectId);
			}
		} else if (attr.typeClassId == Kernel.IC_STRING) {
			StringValue[] vs = krn.getStringValues(ids, attr, langId, false,
					tid);
			for (StringValue v : vs) {
				res.add(new AttrRecord(v.objectId, attr.id, langId, v.index,
						v.value));
				objIds.remove(v.objectId);
			}
		} else if (attr.typeClassId == Kernel.IC_MEMO) {
			StringValue[] vs = krn
					.getStringValues(ids, attr, langId, true, tid);
			for (StringValue v : vs) {
				res.add(new AttrRecord(v.objectId, attr.id, langId, v.index,
						v.value));
				objIds.remove(v.objectId);
			}
		} else if (attr.typeClassId == Kernel.IC_DATE) {
			DateValue[] vs = krn.getDateValues(ids, attr, tid);
			for (DateValue v : vs) {
				res.add(new AttrRecord(v.objectId, attr.id, langId, v.index,
						Funcs.convertDate(v.value)));
				objIds.remove(v.objectId);
			}
		} else if (attr.typeClassId == Kernel.IC_TIME) {
			TimeValue[] vs = krn.getTimeValues(ids, attr, tid);
			for (TimeValue v : vs) {
				res.add(new AttrRecord(v.objectId, attr.id, langId, v.index,
						Funcs.convertTime(v.value)));
				objIds.remove(v.objectId);
			}
		} else if (attr.typeClassId == Kernel.IC_FLOAT) {
			FloatValue[] vs = krn.getFloatValues(ids, attr, tid);
			for (FloatValue v : vs) {
				res.add(new AttrRecord(v.objectId, attr.id, langId, v.index,
						v.value));
				objIds.remove(v.objectId);
			}
		} else if (attr.typeClassId == Kernel.IC_BLOB) {
			for (long id : ids) {
				byte[][] vs = krn.getBlobs(id, attr, langId, tid);
				for (int i = 0; i < vs.length; i++) {
					File f = null;
					byte[] v = vs[i];
					if (v.length > 0) {
						try {
							f = Funcs.createTempFile("blob", null, WebController.WEB_IMAGES_DIRECTORY);
							s.deleteOnExit(f);
							FileOutputStream os = new FileOutputStream(f);
							os.write(v);
							os.close();
						} catch (IOException e) {
							log.error(e, e);
						}
					}
					res.add(new AttrRecord(id, attr.id, langId, i, f));
					objIds.remove(id);
				}
			}
		} else if (attr.typeClassId >= 99) {
			if (req != null && req.getChildren().size() > 0) {
				QueryResult qr = krn.getObjectValues(ids, attr.classId, attr, filterIds, limit, tid, req);
				for (Object[] row : qr.rows) {
					long objId = (Long)row[row.length - 2];
					int index = (Integer)row[row.length - 1];
					KrnObject vobj = (KrnObject)row[0];
					res.add(new AttrRecord(objId, attr.id, langId, index,
							row[0]));
					putObject(vobj);
					objIds.remove(objId);
				}
				createAttrRecords(req, qr.rows);
			} else {
				ObjectValue[] vs = krn.getObjectValues(ids, attr.classId, attr, filterIds, limit, tid);
				for (ObjectValue v : vs) {
					res.add(new AttrRecord(v.objectId, attr.id, langId, v.index,
							v.value));
					putObject(v.value);
					objIds.remove(v.objectId);
				}
			}
		}
		for (long id : objIds) {
			AttrRecord rec = new AttrRecord(id, attr.id, langId, 0);
			rec.setStatus(NaD);
			res.add(rec);
		}
		recs.addAll(res);
		return res;
	}

	public void clear() {
		synchronized (changes) {
			changes.clear();
		}
		recs.clear();
		objRecs.clear();
		objById.clear();
		objByUid.clear();
		reverseActions.clear();
		fireEvent(ET_CLEARED);
	}

	/**
	 * Проверяет соответсвия типов атрибута и значения. Если типы атрибута и
	 * значения не совпадают, то генерируется исключение.
	 * 
	 * @param attrId
	 *            ID атрибута.
	 * @param value
	 *            Значение.
	 * @throws KrnException
	 */
	private void checkValueType(long attrId, Object value) throws KrnException {
		if (value == null) {
			return;
		}
		KrnAttribute attr = krn.getAttributeById(attrId);
		long type = attr.typeClassId;
		if (type >= 99) {
			if (!(value instanceof KrnObject)) {
				throw new KrnException(0,
						"Неверный тип значения. Ожидается object");
			}
		} else if (type == Kernel.IC_STRING || type == Kernel.IC_MEMO) {
			if (!(value instanceof String)) {
				throw new KrnException(0,
						"Неверный тип значения. Ожидается string");
			}
		} else if (type == Kernel.IC_INTEGER || type == Kernel.IC_FLOAT) {
			if (!(value instanceof Number)) {
				throw new KrnException(0,
						"Неверный тип значения. Ожидается number");
			}
		} else if (type == Kernel.IC_BOOL) {
			if (!(value instanceof Number || value instanceof Boolean)) {
				throw new KrnException(0,
						"Неверный тип значения. Ожидается number или boolean");
			}
		} else if (type == Kernel.IC_DATE || type == Kernel.IC_TIME) {
			if (!(value instanceof Date)) {
				throw new KrnException(0,
                        "Неверный тип значения. Ожидается date");
			}
		} else if (type == Kernel.IC_BLOB) {
			if (!(value instanceof File) && !(value instanceof byte[]) && !(value instanceof String)) {
				throw new KrnException(0,
                        "Неверный тип значения. Ожидается blob");
			}
		}
	}
	
	private Set<KrnAttribute> getRevAttrs(long attrId) throws KrnException {
		synchronized (rattrs) {
			return rattrs.get(attrId);
		}
	}

	private void putRevAttrs(KrnAttribute attr) throws KrnException {
		synchronized (rattrs) {
			if (attr.rAttrId != 0) {
				Set<KrnAttribute> l = rattrs.get(attr.rAttrId);
				if (l == null) {
					l = new HashSet<KrnAttribute>();
					rattrs.put(attr.rAttrId, l);
				}
				l.add(attr);
				
				l = rattrs.get(attr.id);
				if (l == null) {
					l = new HashSet<KrnAttribute>();
					rattrs.put(attr.id, l);
				}
				l.add(krn.getAttributeById(attr.rAttrId));
			} else {
				Set<KrnAttribute> l = rattrs.get(attr.id);
				if (l == null) {
					l = new HashSet<KrnAttribute>();
					rattrs.put(attr.id, l);
				}
				KrnAttribute[] ras = krn.getRevAttributes(attr.id);
				for (KrnAttribute ra : ras) {
					Set<KrnAttribute> l2 = rattrs.get(ra.id);
					if (l2 == null) {
						l2 = new HashSet<KrnAttribute>();
						rattrs.put(ra.id, l2);
					}
					l2.add(attr);
					l.add(ra);
				}
			}
		}
	}
	
	private Object convertValue(long attrId, Object value) throws KrnException {
		if (value == null)
			return value;
		KrnAttribute attr = krn.getAttributeById(attrId);
		if (attr.typeClassId == Kernel.IC_INTEGER) {
			return ((Number)value).longValue();
		} else if (attr.typeClassId == Kernel.IC_BOOL) {
			long val = 0;
			if (value instanceof Boolean) {
				return ((Boolean)value).booleanValue() ? 1 : 0;
			} else if (value instanceof Number) {
				return ((Number)value).longValue() > 0 ? 1 : 0;
			}
		} else if (attr.typeClassId == Kernel.IC_BLOB) {
			try {
				if (value instanceof String) {
					return ((String)value).getBytes("UTF-8");
				}
			} catch (Exception ex) {
				log.error(ex, ex);
			}
		} else if (attr.typeClassId == Kernel.IC_FLOAT) {
			return ((Number)value).doubleValue();
		} else if (attr.typeClassId == Kernel.IC_DATE) {
			return kz.tamur.util.Funcs.convertDate((Date)value);
		} else if (attr.typeClassId == Kernel.IC_TIME) {
			return kz.tamur.util.Funcs.convertTime((Date)value);
		}
		return value;
	}
		
	public Object getVar(String name) {
		return vars.get(name);
	}
	
	public void setVar(String name, Object var) {
		vars.put(name, var);
	}
	
	public void reset(long flowId) throws KrnException {
		vars.clear();
		vars.putAll(krn.getInterfaceVars(flowId));
	}

	public Set<ObjectRecord> getObjects(
			final long classId,
			AttrRequest rootReq,
			final long filterId,
			int[] limit,
			boolean isLoaded,
			final RecordsReceiver receiver
	) throws KrnException {
		ObjectRecord low = new ObjectRecord(classId);
		ObjectRecord high = new ObjectRecord(classId + 1);
		Set<ObjectRecord> res = objRecs.subSet(low, high);
		if (!isLoaded || res.isEmpty() || filterId != 0) {
			if (!res.isEmpty()) res.clear();
			if (filterId > 0)
				res = new LinkedHashSet<ObjectRecord>();

			Filter uf = krn.getUser().getCurrentFilter();
			KrnClass cls = krn.getClassNode(classId).getKrnClass();
			long[] ufids = (uf != null && uf.className.equals(cls.name))
					? new long[]{uf.obj.id}
					: new long[0];
			if (filterId > 0) {
				if (ufids.length > 0) {
					ufids = new long[]{ufids[0], filterId};
				} else {
					ufids = new long[]{filterId};
				}
			}
			List<Object[]> rows = krn.getClassObjects(cls, rootReq, ufids, limit, tid);
			for (Object[] row : rows) {
				KrnObject obj = (KrnObject)row[0];
				ObjectRecord r = new ObjectRecord(classId, obj);
				objRecs.add(r);
				if (filterId > 0) res.add(r);
				putObject(obj);
			}
			createAttrRecords(rootReq, rows);
			if (filterId == 0)
				res = objRecs.subSet(low, high);
		}
		return res;
	}
	
	public void getObjects(
			long[] objIds,
			AttrRequest rootReq
	) throws KrnException {
		List<Object[]> rows = krn.getObjects(objIds, rootReq, tid);
		createAttrRecords(rootReq, rows);
	}

	private void createAttrRecords(
			AttrRequest rootReq,
			List<Object[]> rows
	) throws KrnException {
		int i = 2; // 0 - Объект, 1 - транзакция, 2.. - значения атрибутов
		List<AttrRequest> reqs = rootReq.getDescendants();
		Map<Long, Integer> oinds = new HashMap<Long, Integer>();
		oinds.put(0L, 0);
		for (AttrRequest req : reqs) {
			// Номер колонки объекта
			int oi = oinds.get(req.getParent().attrId);
			KrnAttribute attr = krn.getAttributeById(req.attrId);
			long langId = attr.isMultilingual ? req.langId : 0;
			putRevAttrs(attr);
			for (Object[] row : rows) {
				KrnObject obj = (KrnObject)row[oi];
				if (obj != null) {
					Object value = row[i];
					if (value instanceof Boolean)
						value = ((Boolean)value).booleanValue() ? 1L : 0L;
					else if (value instanceof com.cifs.or2.kernel.Date)
						value = Funcs.convertDate((com.cifs.or2.kernel.Date)value);
					else if (value instanceof com.cifs.or2.kernel.Time)
						value = Funcs.convertTime((com.cifs.or2.kernel.Time)value);
					else if (value instanceof byte[]) {
						File f = null;
						if (((byte[])value).length > 0) {
							try {
								f = Funcs.createTempFile("blob", null, WebController.WEB_IMAGES_DIRECTORY);
								s.deleteOnExit(f);
								FileOutputStream os = new FileOutputStream(f);
								os.write((byte[])value);
								os.close();
							} catch (IOException e) {
								log.error(e, e);
							}
						}
						value = f;
					}
					AttrRecord rec = new AttrRecord(obj.id, req.attrId, langId, 0, value);
					if (value instanceof KrnObject)
						putObject((KrnObject)value);
					else if (value == null)
						rec.setStatus(NaD);
					recs.add(rec);
				}
			}
			if (attr.typeClassId >= 99)
				oinds.put(attr.id, i);
			i++;
		}
	}
	
	public KrnObject getObjectByUid(String uid) throws KrnException {
		if (objByUid.containsKey(uid)) {
			return objByUid.get(uid);
		} else {
			KrnObject obj = krn.getCachedObjectByUid(uid);
			if (obj != null) {
				objByUid.put(uid, obj);
				return obj;
			}
		}
		return null;
	}
	
	public void drop(KrnObject obj) {
		drop(obj, false);
	}
	
	public void drop(KrnObject obj, boolean recursive) {
		List<Long> processedIds = new ArrayList<>();
		processedIds.add(obj.id);
		drop(obj, recursive, processedIds);
	}
	
	private void drop(KrnObject obj, boolean recursive, List<Long> processedIds) {
		List<KrnObject> children = new ArrayList<>();
		
		Set<Long> changedAttrIds = new HashSet<Long>();
		
		// Удаление записей по объектам
		ObjectRecord low = new ObjectRecord(obj.classId);
		ObjectRecord high = new ObjectRecord(obj.classId + 1);
		objRecs.subSet(low, high).clear();

		// Удаление записей по атрибутам
		AttrRecord head = new AttrRecord(obj.id, 0, 0, 0);
		AttrRecord tail = new AttrRecord(obj.id, Long.MAX_VALUE, Long.MAX_VALUE,
				Integer.MAX_VALUE);
		SortedSet<AttrRecord> attrRecs = recs.subSet(head, tail);
		for (AttrRecord r : attrRecs) {
			changedAttrIds.add(r.getAttrId());
			
			if (recursive && r.getValue() instanceof KrnObject) {
				KrnObject child = (KrnObject)r.getValue();
				
				if (!processedIds.contains(child.id)) {
					processedIds.add(child.id);
					children.add(child);
				}
			}
		}
		attrRecs.clear();
		
		// Удаление записей по изменениям
		synchronized (changes) {
			Iterator<CacheChangeRecord> it = changes.iterator();
			while (it.hasNext()) {
				CacheChangeRecord cr = it.next();
				if (cr.objectId == obj.id)
					it.remove();
			}
		}
		
		if (recursive && children.size() > 0)
			drop(children, recursive, processedIds);
		
		// Оповоещение об изменении атрибутов объекта
		for(Long attrId : changedAttrIds)
			cashChangeSupport.fireObjectChanged(this, obj.id, attrId);
	}
	
	public void drop(List<KrnObject> objs) {
		drop(objs, false);
	}
	
	public void drop(List<KrnObject> objs, boolean recursive) {
		List<Long> processedIds = new ArrayList<>();
		for (KrnObject obj : objs) {
			if (!processedIds.contains(obj.id)) {
				processedIds.add(obj.id);
				drop(obj, recursive, processedIds);
			}
		}
	}
	
	private void drop(List<KrnObject> objs, boolean recursive, List<Long> processedIds) {
		for (KrnObject obj : objs)
			drop(obj, recursive, processedIds);
	}
	
	private void putObject(KrnObject obj) {
		objById.put(obj.id, obj);
		objByUid.put(obj.uid, obj);
	}
	
    public List<CacheChangeRecord> getChanges() {
        return Collections.unmodifiableList(changes);
    }

	private class ReverseAction {

		public CacheChangeRecord change;
		public int action;
		public Object value;
		public int status;
		public Record record;
		
	}
	
	private Stack<ReverseAction> getCacheChanges(long ifcId) {
		Stack<ReverseAction> res = reverseActions.get(ifcId);
		if (res == null) {
			res = new Stack<ReverseAction>();
			reverseActions.put(ifcId, res);
		}
		return res;
	}
	
	public void undoCacheChange(long ifcId, Object originator) {
		Stack<ReverseAction> res = reverseActions.get(ifcId);
		if (res != null) {
			while (!res.empty()) {
				ReverseAction ra = res.pop();
                boolean calcOwner = OrCalcRef.setCalculations();
                try {
                	undoCacheChange(ra, originator);
                } catch (Exception e) {
                	log.error(e, e);
                } finally {
	                if (calcOwner)
	                    OrCalcRef.makeCalculations();
                }
			}
		}
	}
	
	public void clearCacheChange(long ifcId, Object originator) {
		Stack<ReverseAction> res = reverseActions.get(ifcId);
		if (res != null)
			res.clear();
	}

	private void undoCacheChange(ReverseAction ra, Object originator) {
		if (ra.change != null)
			changes.remove(ra.change);
		
		if (ra.action == ACTION_ADD) {
			AttrRecord head = new AttrRecord(ra.record.getObjId(), ra.record.getAttrId(),
					ra.record.getLangId(), 0);
			AttrRecord tail = new AttrRecord(ra.record.getObjId(), ra.record.getAttrId(),
					ra.record.getLangId(), Integer.MAX_VALUE);
			SortedSet<AttrRecord> set1 = recs.subSet(head, tail);
			SortedSet<AttrRecord> set = set1.subSet((AttrRecord)ra.record, tail);
			set.remove(ra.record);
			for (AttrRecord r : set) {
				r.setIndex(r.getIndex() - 1);
			}
			cashChangeSupport.fireObjectChanged(originator, ra.record.getObjId(), ra.record.getAttrId());
		} else if (ra.action == ACTION_DELETE) {
			AttrRecord head = new AttrRecord(ra.record.getObjId(), ra.record.getAttrId(),
					ra.record.getLangId(), 0);
			AttrRecord tail = new AttrRecord(ra.record.getObjId(), ra.record.getAttrId(),
					ra.record.getLangId(), Integer.MAX_VALUE);
			SortedSet<AttrRecord> set = recs.subSet(head, tail);

			int i = ra.record.getIndex();
			head.setIndex(i);
			SortedSet<AttrRecord> subSet = set.tailSet(head);

			for (AttrRecord r : subSet) {
				r.setIndex(r.getIndex() + 1);
			}
			set.add((AttrRecord)ra.record);
			cashChangeSupport.fireObjectChanged(originator, ra.record.getObjId(), ra.record.getAttrId());
		} else if (ra.action == ACTION_CHANGE) {
			ra.record.setStatus(ra.status);
			ra.record.setValue(ra.value);
			cashChangeSupport.fireObjectChanged(originator, ra.record.getObjId(), ra.record.getAttrId());
		} else if (ra.action == ACTION_CHANGE_STATUS) {
			ra.record.setStatus(ra.status);
			cashChangeSupport.fireObjectChanged(originator, ra.record.getObjId(), ra.record.getAttrId());
		} else if (ra.action == ACTION_ADD_OBJECT) {
			objRecs.remove(ra.record);
			cashChangeSupport.fireObjectDeleted(this, ra.record.getClassId(), ra.record.getObjId());
		} else if (ra.action == ACTION_DELETE_OBJECT) {
			objRecs.add((ObjectRecord)ra.record);
	        cashChangeSupport.fireObjectCreated(this, ra.record.getClassId(), ra.record.getObjId());
		}
	}

	public void setLogIfcId(long logIfcId) {
		this.logIfcId = logIfcId;
	}
}
