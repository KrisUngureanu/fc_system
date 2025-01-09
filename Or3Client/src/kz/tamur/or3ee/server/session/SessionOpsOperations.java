package kz.tamur.or3ee.server.session;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.UUID;

import org.jdom.Element;

import kz.tamur.comps.TriggerInfo;
import kz.tamur.ods.AttrRequest;
import kz.tamur.ods.Lock;
import kz.tamur.ods.Value;
import kz.tamur.or3.util.SystemAction;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.util.CacheChangeRecord;

import com.cifs.or2.client.OrlangTriggerInfo;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.BlobValue;
import com.cifs.or2.kernel.DataChanges;
import com.cifs.or2.kernel.FilterDate;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnVcsChange;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnIndex;
import com.cifs.or2.kernel.KrnIndexKey;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnSearchResult;
import com.cifs.or2.kernel.ModelChanges;
import com.cifs.or2.kernel.Note;
import com.cifs.or2.kernel.ProjectConfiguration;
import com.cifs.or2.kernel.QueryResult;
import com.cifs.or2.kernel.ReportData;
import com.cifs.or2.kernel.Time;
import com.cifs.or2.kernel.UserSessionValue;

public interface SessionOpsOperations {

    public UserSessionValue login(String dsName, String name,String typeClient, String passwd, String newPass, String confPass, String ip, String pcName, boolean callbacks, boolean force, boolean sLogin) throws KrnException;
    public UserSessionValue login(String dsName, String name,String typeClient, String passwd, String newPass, String confPass, String ip, String pcName, boolean callbacks, boolean force, boolean sLogin, boolean isUseECP, String signedData) throws KrnException;
    public UserSessionValue loginWithCert(String dsName, String name, String typeClient, String secretSign, String ip, String pcName, boolean callbacks) throws KrnException;
    public UserSessionValue loginWithDN(String dsName, String dn, String typeClient, String ip, String pcName, boolean callbacks, boolean force) throws KrnException;
    public UserSessionValue loginWithLDAP(String dsName, String dn, String typeClient, String ip, String pcName, boolean callbacks) throws KrnException;
	public UserSessionValue loginWithECP(String dsName, String pkcs7, String typeClient, String ip, String pcName, boolean callbacks) throws KrnException;
	public void release(UUID usId);

	// Transaction support
	long createLongTransaction(UUID usId) throws KrnException;

	void commitLongTransaction(UUID usId, long tid) throws KrnException;

	void rollbackLongTransaction(UUID usId, long tid) throws KrnException;

	void updateReferences(UUID usId, long cid, long aid) throws KrnException;

	void deleteUnusedObjects(UUID usId, long cid, long aid) throws KrnException;

	long[] getSelectedBases(UUID usId) throws KrnException;

	void selectBases(UUID usId, long[] baseIds) throws KrnException;
	
	String renameClassTable(UUID usId, KrnClass cls, String newname) throws KrnException;//TODO r tname
	
	String renameAttrTable(UUID usId, KrnAttribute attr, String newname) throws KrnException;//TODO r tname

	Map<String, Object> getInterfaceVars(UUID usId, long flowId)
			throws KrnException;

	com.cifs.or2.kernel.LongPair[] commit2(UUID usId,
			List<CacheChangeRecord> changes, long tid) throws KrnException;

	void changePassword(String dsName, String nameUs, String typeClient, String ip, String pcName, KrnObject object, char[] oldPasswd, char[] newPasswd, char[] confPasswd) throws KrnException;

	void verifyPassword(String dsName, String nameUs, KrnObject object, char[] newPasswd,
			String name, boolean admin, boolean isLogged, String psw, Time lastChangeTime) throws KrnException;

	KrnObject getUser(UUID usId);

	// Classes
	KrnClass getClassByName(UUID usId, String name) throws KrnException;
	
	List<KrnClass> getClassesByNameWithOptions(UUID usId, String name, long searchMethod) throws KrnException;

	KrnClass getClassById(UUID usId, long classId) throws KrnException;

	List<KrnClass> getClasses(UUID usId, long baseClassId, boolean withSubclasses) throws KrnException;
	KrnClass[] getClasses(UUID usId, long baseClassId) throws KrnException;
	KrnClass[] getClasses(UUID usId) throws KrnException;	

	KrnClass createClass(UUID usId, KrnClass baseClass, String name,
			boolean isRepl, String tname, int mod) throws KrnException;//TODO tname

	KrnClass changeClass(UUID usId, KrnClass cls, KrnClass baseClass,
			String name, boolean isRepl) throws KrnException;

	void deleteClass(UUID usId, KrnClass cls) throws KrnException;

	// Class comments
	void setClassComment(UUID usId, String clsUid, String comment)
			throws KrnException;

	String getClassComment(UUID usId, long clsId) throws KrnException;

	int truncateClass(UUID usId, KrnClass cls) throws KrnException;

	// Attributes
	KrnAttribute getAttributeByName(UUID usId, KrnClass cls, String name)
			throws KrnException;

	KrnAttribute getAttributeById(UUID usId, long attrId) throws KrnException;

	KrnAttribute[] getAttributes(UUID usId, KrnClass cls) throws KrnException;
	List<KrnAttribute> getClassAttributes(UUID usId, KrnClass cls) throws KrnException;
	List<KrnAttribute> getDependAttrs(UUID usId, KrnAttribute attr) throws KrnException;
	List<KrnAttribute> getAttributesByName(UUID usId, String name, long searchMethod) throws KrnException;
	
	List<KrnSearchResult> getConfigsByConditions(UUID usId, String objTitle, String objUID)
			throws KrnException;
	
	KrnAttribute[] getAttributesByTypeId(UUID usId, long classId,
			boolean inherited) throws KrnException;

	KrnAttribute createAttribute(UUID usId, KrnClass cls, KrnClass type,
			String name, int collectionType, boolean isUnique,
			boolean isIndexed, boolean isMultilingual, boolean isRepl,
			int size, long flags, long rAttrId, long sAttrId, boolean sDesc, String tname, int accessModifier)
			throws KrnException;
	
	KrnAttribute createAttribute(UUID usId, KrnClass cls, KrnClass type,
			String name, int collectionType, boolean isUnique,
			boolean isIndexed, boolean isMultilingual, boolean isRepl,
			int size, long flags, long rAttrId, long sAttrId, boolean sDesc, String tname, int accessModifier, boolean isEncrypt)
			throws KrnException;

	KrnAttribute changeAttribute(UUID usId, KrnAttribute attr, KrnClass type,
			String name, int collectionType, boolean isUnique,
			boolean isIndexed, boolean isMultilingual, boolean isRepl,
			int size, long flags, long rAttrId, long sAttrId, boolean sDesc, String tname, int accessModifier)
			throws KrnException;
	
	KrnAttribute changeAttribute(UUID usId, KrnAttribute attr, KrnClass type,
			String name, int collectionType, boolean isUnique,
			boolean isIndexed, boolean isMultilingual, boolean isRepl,
			int size, long flags, long rAttrId, long sAttrId, boolean sDesc, String tname, int accessModifier, boolean isEncrypt)
			throws KrnException;

	void deleteAttribute(UUID usId, KrnAttribute attr) throws KrnException;

	// Attribute comments
	void setAttributeComment(UUID usId, String attrUid, String comment)
			throws KrnException;

	String getAttributeComment(UUID usId, long attrId) throws KrnException;

	//Indexed
	KrnIndex createIndex(UUID usId,KrnClass cls,KrnAttribute[] attrs,boolean[] descs)
		throws KrnException;
	KrnIndex[] getIndexesByClassId(UUID usId,KrnClass cls) throws KrnException;
	KrnIndexKey[] getIndexKeysByIndexId(UUID usId,KrnIndex ndx) throws KrnException;
	KrnAttribute[] getAttributesForIndexing(UUID usId,KrnClass cls) throws KrnException;
	void deleteIndex(UUID usId, KrnIndex ndx) throws KrnException;
	
	// Methods
	KrnMethod createMethod(UUID usId, KrnClass cls, String name,
			boolean isClassMethod, byte[] expr) throws KrnException;

	KrnMethod rollbackMethod(UUID usId, String methodUid) throws KrnException;
	
	KrnMethod changeMethod(UUID usId, String methodUid, String name,
			boolean isClassMethod, byte[] expr) throws KrnException;

	void deleteMethod(UUID usId, String methodUid) throws KrnException;

	KrnMethod[] getMethods(UUID usId, long cls) throws KrnException;
	
	public KrnMethod getMethodById(UUID usId, String id) throws KrnException;

	KrnMethod[] getMethodsByName(UUID usId, String name, long op)
			throws KrnException;
	
	KrnMethod[] getMethodsByUid(UUID usId, String name, long op)
			throws KrnException;

	// Метод, возвращающий список всех аттрибутов
  	public List<KrnMethod> getAllMethods(UUID usId) throws KrnException;
  	
    // Метод, возвращающий HashMap всех аттрибутов
   	public Map<String, KrnMethod> getMethodsMap(UUID usId) throws KrnException;
	
	byte[] getMethodExpression(UUID usId, String methodUid) throws KrnException;

	// Method comments
	void setMethodComment(UUID usId, String methodId, String comment)
			throws KrnException;

	String getMethodComment(UUID usId, String methodId) throws KrnException;

	// Objects
	KrnObject createObject(UUID usId, KrnClass cls, long tid)
			throws KrnException;

	KrnObject createObjectWithUid(UUID usId, KrnClass cls, String uid, long tid)
			throws KrnException;

	KrnObject[] getClassObjects(UUID usId, KrnClass cls, long[] filterIds,
			long tid) throws KrnException;
	
	KrnObject[] getClassOwnObjects(UUID usId, KrnClass cls,
			long tid) throws KrnException;
	
	Map<String,String> getStringUidMap(UUID usId, String[] scopeUids) throws KrnException;

	KrnObject[] getClassObjects2(UUID usId, KrnClass cls, long[] filterIds,
			int[] limit, long tid) throws KrnException;

	QueryResult getClassObjects3(
			UUID usId,
			KrnClass cls,
			AttrRequest req,
			long[] filterIds,
			int limit,
			long tid
	) throws KrnException;

	QueryResult getClassObjects3(
			UUID usId,
			KrnClass cls,
			AttrRequest req,
			long[] filterIds,
			int limit,
			long tid,
			String info
	) throws KrnException;

	KrnObject[] getObjectsByAttribute(UUID usId, long classId, long attrId,
			long langId, int op, Object value, long tid) throws KrnException;
	
	KrnObject[] getObjectsByAttribute(UUID usId, long classId, long attrId,
	        long langId, int op, Object value, long tid, KrnAttribute[] krnAttrs) throws KrnException;

	int getMaxIndex(UUID usId, long objectId, long attrId, long langId, long tid)
			throws KrnException;

	long[] getFilteredObjectIds(UUID usId, long[] filterIds,
			com.cifs.or2.kernel.FilterDate[] dates, int[] limit, long trId)
			throws KrnException;

	long[] getFilteredObjectIds2(UUID usId, String[] filterUids,
			com.cifs.or2.kernel.FilterDate[] dates, int[] limit)
			throws KrnException;

	KrnObject[] getObjectsById(UUID usId, long[] ids, long trId) throws KrnException;

	KrnObject[] getObjectsByUid(UUID usId, String[] uids, long trId) throws KrnException;
	
	KrnObject getClassObjectByUid(UUID usId, long clsId, String uid, long trId) throws KrnException;

	Object[] getFilterParam(UUID usId, String fuid, String pid)
			throws KrnException;

	Map<String, List<Object>> getFilterParams(UUID usId, String fuid)
	throws KrnException;

	boolean setFilterParam(UUID usId, String fuid, String pid, Object[] param)
			throws KrnException;

	boolean setFilterParam(UUID usId, String fuid, String pid, Object param)
			throws KrnException;

	boolean clearFilterParams(UUID usId, String fuid) throws KrnException;

	long[] filter(UUID usId, String[] fuids,
			com.cifs.or2.kernel.FilterDate[] dates, int[] limit)
			throws KrnException;

	long[] filter(UUID usId, String[] fuids,
			com.cifs.or2.kernel.FilterDate[] dates, int[] limit,int[] beginRows,int[] endRows)
			throws KrnException;

	KrnObject[] getFilteredObjects(UUID usId, KrnObject filterObj, int limit, long trId)
			throws KrnException;
	
	KrnObject[] getFilteredObjects(UUID usId, KrnObject filterObj, int limit,int beginRow,int endRow, long trId)
			throws KrnException;
	long filterCount(UUID usId, KrnObject filterObj, long trId)
			throws KrnException;

	KrnObject[] getObjects(UUID usId, long objId, long attrId,
			long[] filterIds, long tid) throws KrnException;

	KrnObject[] getObjectsOfClass(UUID usId, KrnClass cls) throws KrnException;

	KrnObject[] getObjectsLiveOfClass(UUID usId, KrnClass cls)
			throws KrnException;

	com.cifs.or2.kernel.ObjectValue[] getObjectValues(UUID usId, long[] objIds,
			long attrId, long[] filterIds, long tid) throws KrnException;

	com.cifs.or2.kernel.ObjectValue[] getObjectValues(UUID usId, long[] objIds,
			long attrId, long[] filterIds, int[] limit, long tid) throws KrnException;

	BlobValue[] getBlobValues(UUID usId, long[] objIds, long attrId, long langId,
			long tid) throws KrnException;
	
	QueryResult getObjects(UUID usId, long[] objIds, AttrRequest req, long tid) throws KrnException;

	QueryResult getObjectValues(UUID usId, long[] objIds,
			long attrId, long[] filterIds, long tid, AttrRequest req) throws KrnException;

	QueryResult getObjectValues(UUID usId, long[] objIds,
			long attrId, long[] filterIds, int[] limit, long tid, AttrRequest req) throws KrnException;

	String[] getStrings(UUID usId, long objId, long attrId, long langId,
			boolean isMemo, long tid) throws KrnException;

	com.cifs.or2.kernel.StringValue[] getStringValues(UUID usId, long[] objIds,
			long attrId, long langId, boolean isMemo, long tid)
			throws KrnException;

    public SortedSet<Value> getValues(UUID usId, long[] objIds, long attrId, long langId, long tid) throws KrnException;

    void restartTransport(UUID usId, int transportId) throws KrnException;

	String resendMessage(UUID usId, com.cifs.or2.kernel.Activity act)
			throws KrnException;

	byte[] getTransportParam(UUID usId, int transportId) throws KrnException;

	void setTransportParam(UUID usId, byte[] data, int transportId)
			throws KrnException;

	void reloadBox(UUID usId, KrnObject obj) throws KrnException;

	long[] getProcessDefinitions(UUID usId) throws KrnException;

	com.cifs.or2.kernel.Activity[] getTaskList(UUID usId) throws KrnException;

	long getTasksCount(UUID usId) throws KrnException;

	void setTaskListFilter(UUID usId, com.cifs.or2.kernel.AnyPair[] params)
			throws KrnException;

	com.cifs.or2.kernel.SuperMap[] getMapList(UUID usId, long[] flowIds)
			throws KrnException;

	void updateUsers(UUID usId, KrnObject[] objs) throws KrnException;
	
	void updateUser(UUID usId, KrnObject obj, String name) throws KrnException;

	void reloadProcessDefinition(UUID usId, long processDefId)
			throws KrnException;

	String[] startProcess(UUID usId, long processDefIdin, Map<String, Object> vars) throws KrnException;

	boolean cancelProcess(UUID usId, long processId, String nodeId, boolean isAll, boolean forceCancel) throws KrnException;

	boolean reloadFlow(UUID usId, long flowId) throws KrnException;

	boolean saveFlowParam(UUID usId, long flowId,List<String> args) throws KrnException;

	Object openInterface(UUID usId, long uiId,long flowId,long trId,long pdId) throws KrnException;

	void setPermitPerform(UUID usId, long flowId, boolean permit)
			throws KrnException;

	boolean setSelectedObjects(UUID usId, long flowId, long nodeId,
			KrnObject[] objs) throws KrnException;

	void setLang(UUID usId, KrnObject lang) throws KrnException;

	void setDataLanguage(UUID usId, KrnObject lang) throws KrnException;

	String[] performActivitys(UUID usId,
			com.cifs.or2.kernel.Activity[] activitys, String transition,String event)
			throws KrnException;

	void startTransport(UUID usId, int transport) throws KrnException;

	void saveFilter(UUID usId, long filterId) throws KrnException;
	String compileFilter(UUID usId, long filterId, Element xml)throws KrnException;

	void changeTimerTask(UUID usId, long objId, boolean isDelete)
			throws KrnException;

	void executeTask(UUID usId, long objId) throws KrnException;

	long[] getLongs(UUID usId, long objId, long attrId, long tid)
			throws KrnException;

	com.cifs.or2.kernel.LongValue[] getLongValues(UUID usId, long[] objIds,
			long attrId, long tid) throws KrnException;

	byte[] getBlob(UUID usId, long objId, long attrId, int index, long langId,
			long tid, boolean compress) throws KrnException;

	byte[][] getBlobs(UUID usId, long objId, long attrId, long langId, long tid, boolean compress)
			throws KrnException;

	double[] getFloats(UUID usId, KrnObject obj, KrnAttribute attr, long tid)
			throws KrnException;

	com.cifs.or2.kernel.FloatValue[] getFloatValues(UUID usId, long[] objIds,
			long attrId, long tid) throws KrnException;

	com.cifs.or2.kernel.Date[] getDates(UUID usId, long objId, long attrId,
			long tid) throws KrnException;

	com.cifs.or2.kernel.DateValue[] getDateValues(UUID usId, long[] objIds,
			long attrId, long tid) throws KrnException;

	com.cifs.or2.kernel.DateValue[] getDateValues2(UUID usId, long[] objIds,
			long attrId, long tid) throws KrnException;

	com.cifs.or2.kernel.Time[] getTimes(UUID usId, long objId, long attrId,
			long tid) throws KrnException;

	com.cifs.or2.kernel.TimeValue[] getTimeValues(UUID usId, long[] objIds,
			long attrId, long tid) throws KrnException;

	void setObject(UUID usId, long objectId, long attrId, int index,
			long value, long tid, boolean insert) throws KrnException;

	void setString(UUID usId, long objectId, long attrId, int index,
			long langId, boolean isMemo, String value, long tid)
			throws KrnException;

	void setLong(UUID usId, long objectId, long attrId, int index, long value,
			long tid) throws KrnException;

	void setLong(UUID usId, List<Long> objectsIds, long attrId, long value, long tid) throws KrnException;
	
	void setBlob(UUID usId, long objectId, long attrId, int index,
			byte[] value, long langId, long tid) throws KrnException;

	void setFloat(UUID usId, long objectId, long attrId, int index,
			double value, long tid) throws KrnException;

	void setDate(UUID usId, long objectId, long attrId, int index,
			com.cifs.or2.kernel.Date value, long tid) throws KrnException;

	void setTime(UUID usId, long objectId, long attrId, int index,
			com.cifs.or2.kernel.Time value, long tid) throws KrnException;

	void deleteValue(UUID usId, long objectId, long attrId, int[] indexes,
			long langId, long tid) throws KrnException;

	void deleteValueInSet(UUID usId, long objectId, long attrId,
			Object[] values, long tid) throws KrnException;

	void deleteObject(UUID usId, KrnObject obj, long tid) throws KrnException;

	void runReplication(UUID usId) throws KrnException;

	String setChanges(UUID usId) throws KrnException;

	int getChanges(UUID usId, int action, String info, String scriptOnBeforeAction, String scriptOnAfterAction) throws KrnException;
	
	boolean isDel(UUID usId, KrnObject obj, long trId) throws KrnException;
	
	boolean isDeleted(UUID usId, KrnObject obj) throws KrnException;

	KrnObject getCurrentDb(UUID usId) throws KrnException;

	KrnObject[] getChildDbs(UUID usId, boolean recursive, boolean onlyPhisycal)
			throws KrnException;

	String getUId(UUID usId, int AId) throws KrnException;

	KrnObject[] getReplRecords(UUID usId, int log_type, long replicationID)
			throws KrnException;

	void createConfirmationFile(UUID usId, long DbId) throws KrnException;

	void dbExport(UUID usId, String dir, String separator);

	// Sequences
	int getNextValue(UUID usId, long seqId, long tr_id) throws KrnException;

	void useValue(UUID usId, long seqId, long value, String strVal, long tr_id)
			throws KrnException;

	void unuseValue(UUID usId, long seqId, String oldStrValue, long newValue,
			String newStrValue, long tr_id) throws KrnException;

	void skipValue(UUID usId, long seqId, long value, String strVal, long tr_id)
			throws KrnException;

	void rollbackSeqValues(UUID usId, long seqId, long tr_id)
			throws KrnException;

	long[] getSkippedValues(UUID usId, long seqId) throws KrnException;

	int getLastValue(UUID usId, long seqId) throws KrnException;

	KrnObject[] cloneObject2(UUID usId, KrnObject[] source, long get_tr_id,
			long set_tr_id) throws KrnException;

	// 1@0B=K5 0B@81CBK
	long[] getRevAttributes(UUID usId, long attrId) throws KrnException;
	
	List<KrnAttribute> getRevAttributes2(UUID usId, long attrId) throws KrnException;
	
	long[] getLinkAttributes(UUID usId, long attrId) throws KrnException;

	String getXml(UUID usId, KrnObject obj) throws KrnException;

	boolean isCachedObjectLock(UUID usId, long objId, long processId) throws KrnException;

	String isCachedObjectLock(UUID usId, long objId) throws KrnException;

	void cachedLock(UUID usId, long objId, long lockerId, long flowId);

	void cachedUnlock(UUID usId, long objId, long processId, long flowId);

	KrnObject[] getCachedConflictLocker(UUID usId, long objId, long processId,
			long processDefId);

	Collection<Lock> getLocksByObjectId(UUID usId, long objId);
	KrnObject getCachedLocker(UUID usId, long objId, long processId);

	void rollbackLocked(UUID usId) throws KrnException;

	long[] getLangs(UUID usId, long objId, long attrId, long tid)
			throws KrnException;

	long execute(UUID usId, String cmd, Map<String, Object> vars, boolean closeSession) throws KrnException;
	
	void stopTerminalThread(long threadId);
	
	Object executeMethod(UUID usId, KrnObject obj, KrnObject this_, String name, List<Object> args, long trId) throws KrnException;
	Object executeMethod(UUID usId, KrnClass cls, KrnClass this_, String name, List<Object> args, long trId) throws KrnException;

	void userCreated(UUID usId, String name) throws KrnException;

	void userDeleted(UUID usId, String name) throws KrnException;

	void userBlocked(UUID usId, String name) throws KrnException;

	void userUnblocked(UUID usId, String name) throws KrnException;

	void userRightsChanged(UUID usId, String name) throws KrnException;

	// #?@02;5=85 A5AA8O<8
	UserSessionValue[] getUserSessions(UUID usId) throws KrnException;
	
	UserSessionValue[] getUserSessions(UUID usId, int criteria, String txt, String txt2) throws KrnException;

	void killUserSessions(UUID usId, UUID kusId, boolean blockUser)
			throws KrnException;

	void refreshMethodsForReplication(UUID usId) throws KrnException;

	void releaseEngagedObject(UUID usId, long objId) throws KrnException;

	UserSessionValue blockObject(UUID usId, long objId) throws KrnException;

	void unlockMethod(UUID usId, String muid) throws KrnException;

	UserSessionValue lockMethod(UUID usId, String muid) throws KrnException;
	
	UserSessionValue vcsLockObject(UUID usId, long objId) throws KrnException;

	UserSessionValue vcsLockModel(UUID usId, String uid, int modelChangeType) throws KrnException;

	UserSessionValue getObjectBlocker(UUID usId, long objId)
			throws KrnException;

	void writeLogRecord(UUID usId, SystemEvent event, String description) throws KrnException;

    void writeLogRecord(UUID usId, String loggerName, String type, String event, String description) throws KrnException;

	int blockServer(UUID usId, boolean serverBlocked);

	void sendMessage(UUID usId, UUID toUsId, String message) throws KrnException;

	void sendMessage(UUID usId, String message) throws KrnException;

	void interfaceChanged(UUID usId, long id);

	int isServerBlocked(UUID usId) throws KrnException;

	long getNextGeneratedNumber(UUID usId, String docTypeUid, Number period,
			Number initNumber) throws Exception;

	long setLastGeneratedNumber(UUID usId, String docTypeUid, Number period,
			Number initNumber) throws Exception;

	boolean rejectGeneratedNumber(UUID usId, String docTypeUid, Number period,
			Number number, Time date) throws Exception;

	long getOldGeneratedNumber(UUID usId, String docTypeUid, Number period)
			throws Exception;

	KrnObject saveNumber(UUID usId, String className, String attrName, String kadastrNumber)
			throws Exception;

	public Note[] getNotes(UUID usId);

	public Activity getTask(UUID usId, long flowId, long ifsPar, boolean isCheckEvent, boolean onlyMy) throws KrnException;
	
	public boolean showUserDB(UUID usId) throws KrnException;
	
	public boolean ping(UUID usId);
	
	// Контекстный поиск
	public List<Object> search(UUID usId, String pattern, int results, int[] searchProperties, boolean[] searchArea) throws KrnException;
	
	// Индексирование иерархии классов
	public void indexHierarchy(UUID usId, String clName, boolean fullIndex) throws KrnException;
	
	// Индексирование классов
	public void indexClass(UUID usId, KrnClass kClass, boolean fullIndex) throws KrnException;
	
    // Обнуление папки с индексами атрибута или методов
	public void dropIndex(UUID usId, KrnAttribute krnAttribute) throws KrnException;
	
	// Обнуление папки с индексами триггеров
	public void dropTriggersIndexFolder(UUID usId) throws KrnException;

	// Обнуление папки с индексами изменений
	public void dropVcsChangesIndexFolder(UUID usId) throws KrnException;
	
	// Индексирование объектов
	public void indexObject(UUID usId, KrnObject krnObject, KrnClass krnClass, KrnAttribute krnAttribute, boolean foolIndex) throws KrnException;
	
	// Индексирование методов
	public void indexMethod(UUID usId, KrnMethod method) throws KrnException;
	
	// Индексирование изменений объектов проектирования
	public void indexVcsChange(UUID usId, KrnVcsChange change) throws KrnException;

	// Индексирование триггеров
	public void indexTrigger(UUID usId, OrlangTriggerInfo trigger) throws KrnException;

	//Lucene highlight keyword in the fragment
	public String getHighlightedFragments(UUID usId, String objUid, long attrId, long langId, String pattern) throws KrnException;
	
	//Lucene search in plain text
	public boolean stringSearch(UUID id, String content, String pattern, long langId);
	
	ModelChanges getModelChanges(UUID usId, long changeId) throws KrnException;
	DataChanges getDataChanges(UUID usId, long classId, long changeId, AttrRequest req) throws KrnException;

	ReportData prepareReport(UUID usId, long reportId, KrnObject lang, KrnObject[] srvObjs, FilterDate[] fds, long trId)
			throws KrnException;
	
	public String randomString();

    boolean sendMailMessage(UUID usId,String host,String port,String user,String passwd,
    		String[] froms,String[] tos,String theme,String text,String mime,String charSet) throws KrnException;
	boolean isValidEmailAddress(UUID usId,String email);
	
	byte[] convertOfficeDocument(UUID usId, byte[] docData, String outputFormat) throws KrnException;
	
    public boolean dropIndexFolder(UUID usId) throws KrnException;
    
	public String getIndexDirectory(UUID usId) throws KrnException;
	
	public String getLastIndexingInfo(UUID usId) throws KrnException;

	public void setLastIndexingInfo(UUID usId, String lastIndexingInfo) throws KrnException;
    public List<KrnObject> filter(UUID id, KrnObject filterObj, int limit, long trId) throws KrnException;
    public List<KrnObject> filterLocal(UUID id, String sql, int limit,int beginRow,int endRow, long trId) throws KrnException;
    public List<Object> filterGroup(UUID id, KrnObject filterObj, long trId) throws KrnException;
    public long filterToAttr(UUID id, KrnObject filterObj, long pobjId, long attrId, long trId) throws KrnException;

    
    public boolean checkUserHasRight(UUID usId, SystemAction action, long userId, KrnObject subject) throws KrnException;
    public List<Long> getUserSubjects(UUID usId, SystemAction action, long userId) throws KrnException;
    
    public String[][] getColumnsInfo(UUID usId, String tableName) throws KrnException; //TODO: Tedit
    public boolean columnMove(UUID usId, int[] cols, String tableName) throws KrnException; //TODO: Tedit

	public boolean isDataLog();
	public void setDataLog(boolean logData);
	
	public void addAttrChangeListener(UUID usId, long classId) throws KrnException;
	public byte[] getUserPhoto(String uid) throws KrnException;
	
    public String generateWS(UUID usId, byte[] wsdlFileInBytes, String fileName, String packageName, String methodName);
    
    public byte[] generateXML(UUID usId, String serviceName, int type) throws KrnException;

    public List<Long> findForeignProcess(UUID usId, long processDefId, long cutObjId) throws KrnException;
	
    public List<ProjectConfiguration> getChildConfigurations(String dsParent);
	public void addConfiguration(ProjectConfiguration c, String dsParent);
	public void removeConfiguration(String dsName, String dsParent);
	public void moveConfiguration(String dsName, String dsParent);
	void changeConfiguration(String dsName, ProjectConfiguration c);
	void saveAllConfigurations();
    public List<Long> findProcessByUiType(UUID usId,String uiType) throws KrnException;
    public String getReplicationDirectoryPath(UUID usId) throws KrnException;
    public List<TriggerInfo> getTriggers(UUID usId, KrnClass cls) throws KrnException;
    public String createTrigger(UUID usId, String triggerContext) throws KrnException;
    public String removeTrigger(UUID usId, String triggerName) throws KrnException;
    public List<Object> downloadFile(UUID usId, String source) throws KrnException;
	public String getNextProcessNode(UUID usId, long flowId) throws KrnException;
    public KrnAttribute setAttrTriggerEventExpression(UUID usId, String expr, long attrId, int mode, boolean isZeroTransaction) throws KrnException;
    public KrnClass setClsTriggerEventExpression(UUID usId, String expr, long clsId, int mode, boolean isZeroTransaction) throws KrnException;
	public void setMaxActiveCount(int maxThreadCount);
	public void setMaxFlowCount(int maxThreadCount);
	public boolean initServerTasks(UUID usId) throws KrnException;
    public List<String> getListProcedure(UUID usId,String type) throws KrnException;
    public byte[] getProcedureContent(UUID usId,String name,String type) throws KrnException;
    public String createProcedure(UUID usId,String name,List params,String body) throws KrnException; 
    public boolean deleteProcedure(UUID usId,String name,String type) throws KrnException; 
    public List<KrnVcsChange> getVcsChanges(UUID usId,int isFixd,int isRepld,long userId,long replId) throws KrnException;
	public List<KrnVcsChange> getVcsChangesByUID(UUID usId, int isFixd, int isRepld, long userId, long replId, String uid) throws KrnException;
    public List<Long> getVcsGroupObjects(UUID usId,long clsId) throws KrnException;
	public List<KrnVcsChange> getVcsHistoryChanges(UUID usId, boolean isModel, String uid, int typeId, boolean isLastChange) throws KrnException;
	public List<KrnVcsChange> getVcsDifChanges(UUID usId, boolean isModel, long[] ids) throws KrnException;
	public String getVcsHistoryDataIncrement(UUID usId,KrnVcsChange change) throws KrnException;
    public void commitVcsObjects(UUID usId, List<KrnVcsChange> changes, String comment) throws KrnException;
    public boolean setVcsUserForObject(UUID usId, KrnVcsChange change, long userId) throws KrnException;
    public void rollbackVcsObjects(UUID usId, List<KrnVcsChange> changes) throws KrnException;
    public void orderChanged(UUID usId, String operation, String type, List<String> orderIds) throws KrnException;
    public Map<Long,Long> getActiveFlows(UUID usId) throws KrnException;
    public boolean getBindingModuleToUserMode(UUID usId) throws KrnException;
    public boolean isDbReadOnly(UUID usId) throws KrnException;
    public List<OrlangTriggerInfo> getOrlangTriggersInfo(UUID usId) throws KrnException;
    public KrnClass getClassByUid(UUID usId, String uid) throws KrnException;
    public KrnAttribute getAttributeByUid(UUID usId, String uid) throws KrnException;
    public List<KrnAttribute> getAttributesByUidPart(UUID usId, String uid, long searchMethod) throws KrnException;
    public void setDbId(UUID usId, String name,long value) throws KrnException;
    public boolean convertLinkForSysDb(UUID usId,long newBaseId, long oldBaseId) throws KrnException;
    public long getId(UUID usId, String tname) throws KrnException;
    public long getLastId(UUID usId, String tname) throws KrnException;
    public boolean isAllowConvertDb(UUID usId) throws KrnException;
    public String getUrlConnection(UUID usId) throws KrnException;
    public List runSql(UUID usId,String sql,int limit,boolean isUpdate) throws KrnException;
    public void setLoggingGetObjSql(UUID usId,boolean logginGetObjSql) throws KrnException;
    public void getout(UUID usId, KrnObject user, String message) throws KrnException;
    public void sendMessage(UUID usId, KrnObject user, String message) throws KrnException;
    public KrnObject sendNotification(UUID usId, KrnObject user, String message, String uid, String cuid) throws KrnException;
    public KrnObject sendNotification(UUID usId, KrnObject user, String message, String uid, String cuid, long trId) throws KrnException;
    public Date getServerStartupDatetime(UUID usId);
    public long getLoggedInUsersCount(UUID usId, Date period);
    public long getLoggedOutUsersCount(UUID usId, Date period);
    public List<String> showDbLocks(UUID usId) throws KrnException;
    public List<Long> getFiltersContainingAttr(UUID usId, KrnAttribute attr) throws KrnException;
    public boolean isRNDB(UUID usId) throws KrnException;
    public boolean isULDB(UUID usId) throws KrnException;
    public boolean hasUseECP(UUID usId) throws KrnException;
    public String putRepositoryData(UUID usId,String paths,String fileName, byte[] data) throws KrnException;
    public byte[] getRepositoryData(UUID usId,String docId) throws KrnException;
    public String getRepositoryItemName(UUID usId,String docId) throws KrnException;
    public String getRepositoryItemType(UUID usId,String docId) throws KrnException;
    public boolean dropRepositoryItem(UUID usId,String docId) throws KrnException;
	public List<String> searchByQuery(UUID usId, String searchName) throws KrnException;

	public boolean isProcessRunning(UUID usId, long flowId) throws KrnException;
    public String getProcAllowed(UUID usId) throws KrnException;
    public String getProcDenied(UUID usId) throws KrnException;
	public boolean getActivateScheduler(UUID usId);
	public void setActivateScheduler(UUID usId, boolean activateScheduler);
}