package kz.tamur.ods;

import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import kz.tamur.DriverException;
import kz.tamur.comps.TriggerInfo;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.or3.util.FGACRule;
import kz.tamur.or3.util.FGARule;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.CollectionTypes;
import kz.tamur.util.LongHolder;
import kz.tamur.util.Pair;

import org.jdom.Element;

import com.cifs.or2.client.OrlangTriggerInfo;
import com.cifs.or2.kernel.DataChanges;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnIndex;
import com.cifs.or2.kernel.KrnIndexKey;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnVcsChange;
import com.cifs.or2.kernel.ModelChanges;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.db.Database;
import com.cifs.or2.server.orlang.SrvOrLang;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 04.12.2005
 * Time: 14:48:09
 * To change this template use File | Settings | File Templates.
 */
public interface Driver extends PrimaryClassIds, CollectionTypes {

    int DB_VERSION = 16;

    long ROOT_CLASS_ID = 99;
	String FILTER_LOCAL="FilterLocal";
    public KrnClass EMPTY_CLASS = new KrnClass("", 0, 0, false, 0, "", null, null, null, null, null, 0, 0, 0, 0);//TODO tname
    public boolean upgradeTo17();
    
    public String getCurrentScheme() throws DriverException;
    public long init() throws DriverException;
    public boolean validate();
    
    public String getClassTableName(long clsId);
    public String getClassTableName(String clsUid);
    public String getClassTableName(KrnClass cls, boolean withPrefix);
    public String getAttrTableName(KrnAttribute attr, boolean withPrefix);
    public String getAttrTableName(KrnAttribute attr);
    public String getAttrTableName(long attrId);
    public String getColumnName(long attrId);
    public String getColumnName(KrnAttribute attr);
    public String getColumnName(KrnAttribute attr, long langId) throws DriverException;

    KrnClass createClass(String name, long parentId, boolean isRepl, long id, String uid, String tname)//TODO tname
            throws DriverException;
    KrnClass createClass(String name, long parentId, boolean isRepl, int mod, long id, String uid, String tname)//TODO tname
    		throws DriverException;
    KrnClass createClass(String name, long parentId, boolean isRepl, int mod, long id, String uid, boolean log, String tname)//TODO tname
    		throws DriverException;
    KrnClass changeClass(long id, long parentId, String name, boolean isRepl)
            throws DriverException;
    void deleteClass(long id) throws DriverException;

    List<KrnClass> getAllClasses() throws DriverException;
	int truncate(KrnClass cls) throws DriverException;
	
	public boolean renameClassTable(long id, String newName);//TODO r tname
	
	public boolean renameAttrTable(KrnAttribute attr, String newName);//TODO r tname
	
	public String[][] getColumnsInfo(String tableName); //TODO: Tedit
	
	public boolean columnMove(int[] cols, String tableName); //TODO: Tedit

    public KrnAttribute createAttribute(long id, String uid, long classId, long typeId, String name, int collectionType, boolean isUnique, boolean isIndexed,
                                        boolean isMultilingual, boolean isRepl, int size, long flags, long rAttrId, long sAttrId, boolean sDesc, boolean log, String tname, int accessModifier
                                        ) throws DriverException;
    
    public KrnAttribute createAttribute(long id, String uid, long classId, long typeId, String name, int collectionType, boolean isUnique, boolean isIndexed,
            boolean isMultilingual, boolean isRepl, int size, long flags, long rAttrId, long sAttrId, boolean sDesc, boolean log, String tname, int accessModifier, boolean Encrypt
            ) throws DriverException;

    public KrnAttribute createAttribute(long id,
			String uid,
            long classId,
            long typeId,
            String name,
            int collectionType,
            boolean isUnique,
            boolean isIndexed,
            boolean isMultilingual,
            boolean isRepl,
            int size,
            long flags,
            long rAttrId,
            long sAttrId,
            boolean sDesc,
            String tname,
            int accessModifier
            ) throws DriverException;
    
    public KrnAttribute createAttribute(long id,
			String uid,
            long classId,
            long typeId,
            String name,
            int collectionType,
            boolean isUnique,
            boolean isIndexed,
            boolean isMultilingual,
            boolean isRepl,
            int size,
            long flags,
            long rAttrId,
            long sAttrId,
            boolean sDesc,
            String tname,
            int accessModifier,
            boolean isEncrypt
            ) throws DriverException;

    public KrnAttribute changeAttribute(long id,
            long typeId,
            String name,
            int collectionType,
            boolean isUnique,
            boolean isIndexed,
            boolean isMultilingual,
            boolean isRepl,
            int size,
            long flags,
            long rAttrId,
            long sAttrId,
            boolean sDesc,
            String tname,
            int accessModifier) throws DriverException;

    public KrnAttribute changeAttribute(long id,
                                        long typeId,
                                        String name,
                                        int collectionType,
                                        boolean isUnique,
                                        boolean isIndexed,
                                        boolean isMultilingual,
                                        boolean isRepl,
                                        int size,
                                        long flags,
                                        long rAttrId,
                                        long sAttrId,
                                        boolean sDesc,
                                        String tname,
                                        int accessModifier,
                                        boolean isEncrypt) throws DriverException;

    void deleteAttribute(long id) throws DriverException;

    KrnAttribute[] getRevAttributes(long attrId) throws DriverException;
    KrnAttribute[] getLinkAttributes(long attrId) throws DriverException;

    List<KrnAttribute> getDependAttrs(long id) throws DriverException;
    List<KrnAttribute> getAllAttributes() throws DriverException;
    //Индексы
    KrnIndex createIndexInfo(String uid,long classId) 
    	throws DriverException;
    KrnIndexKey createIndexKeyInfo(long indexId,long attrId,long keyNo,boolean isDesc) 
    	throws DriverException;    
    void createIndex(long id,String uid,long classId,KrnAttribute[] attrs)
    	throws DriverException;
    void deleteIndex(long id,String uid,long classId) throws DriverException;
    void deleteIndexInfo(long id) throws DriverException;
    KrnIndex getIndexByUid(String uid) 
    	throws DriverException;
    List<KrnIndex> getIndexesByClassId(long classId) 
    	throws DriverException;
    List<KrnIndexKey> getIndexKeysByIndexId(long indexId)
    	throws DriverException;    
    List<KrnAttribute> getAttributesForIndexing(long classId)
    	throws DriverException;
    boolean isMultiLangIndex(long indexId)
    	throws DriverException;
    boolean classExists(String classUid) throws DriverException;
    boolean attributeExists(String attrUid) throws DriverException;
    boolean indexExists(String indexUid) throws DriverException;
        
    void commit() throws DriverException;
    void rollback() throws DriverException;
    void release();

    long createLongTransaction() throws DriverException;
    void commitLongTransaction(long trId, boolean deleteRefs,Session session) throws DriverException;
    void rollbackLongTransaction(long trId) throws DriverException;

    KrnObject createObject(long classId, long trId, long id, String uid)
            throws DriverException;
    KrnObject createObject(long classId, long trId, long id, String uid, Map<Pair<KrnAttribute, Long>, Object> initValues)
            throws DriverException;
    void createObjects(long classId, long trId, List<Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>>> objValues, boolean log) throws DriverException;

    KrnObject updateObject(long classId, long trId, long id, String uid, Map<Pair<KrnAttribute, Long>, Object> initValues)
            throws DriverException;
    KrnObject updateObject(long classId, long trId, long id, String uid, Map<Pair<KrnAttribute, Long>, Object> initValues, boolean log)
            throws DriverException;
    void updateObjects(long classId, long trId, List<Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>>> objValues, boolean log) throws DriverException;

    KrnObject cloneObject(KrnObject objId, long getTrId, long trId)
            throws DriverException;

    void deleteObject(KrnObject obj, long trId, boolean deleteRefs) throws DriverException;

    List<KrnObject> getObjects(long classId, long trId) throws DriverException;
    List<KrnObject> getOwnObjects(long classId, long trId) throws DriverException;
    List<KrnObject> getObjects(long classId, int[] limit, long trId) throws DriverException;
	List<Object[]> getObjects (
			long classId,
			long[] objIds,
			AttrRequest req,
            long tid,
            int[] limit,
            int extraColumnCount,
            String info,
            Session session
	) throws DriverException;
	List<Object[]> getObjects (
			long classId,
			long[] objIds,
			AttrRequest req,
            long tid,
            int[] limit,
            int extraColumnCount,
            String info,
            AttrRequestCache cache,
            Session session
	) throws DriverException;

    List<KrnObject> getObjectsByUids(String[] uids, long trId, boolean isDirty) throws DriverException;
    
    KrnObject getClassObjectByUid(long clsId, String uid, long trId, boolean isDirty) throws DriverException;

    KrnObject getObjectById(long id) throws DriverException;
    List<KrnObject> getObjectsByIds(long[] ids) throws DriverException;
    KrnObject getObjectById(long clsId, long id, long trId, boolean dirty) throws DriverException;

    public List<KrnObject> getObjectsByAttribute(long classId,
                                      long attrId,
                                      long langId,
                                      int op,
                                      Object value,
                                      long tid) throws DriverException;

    public List<KrnObject> getObjectsByAttribute(long classId,
            long attrId,
            long langId,
            int op,
            Object value,
            long tid, KrnAttribute[] krnAttrs) throws DriverException;

    Object getValue(long objId, long attrId, int index, long langId,
                        long trId) throws DriverException;

    SortedSet<Value> getValues(long[] objIds, Set<Long> filteredIds, long attrId, long langId,
            long tid) throws DriverException;

    SortedSet<Value> getValues(long[] objIds, Set<Long> filteredIds, int[] limit, long attrId, long langId,
                        long tid) throws DriverException;

	void setValue(KrnObject obj, long attrId, int index, long langId, long trId,
                  Object value, boolean insert) throws DriverException;

    void setValue(long objId, long attrId, int index, long langId, long trId,
                  Object value, boolean insert) throws DriverException;

	public void setValue(KrnObject obj, long trId, Map<Pair<KrnAttribute, Long>, Object> values) throws DriverException;
	
    void deleteValue(long objId, long attrId, int[] indices, long langId, long trId, boolean deleteRefs)
            throws DriverException;

    void deleteValue(long objId, long attrId, Collection<Object> values, long trId, boolean deleteRefs)
    	throws DriverException;

    int getMaxIndex(long objId, long attrId, long langId, long trId)
            throws DriverException;

    List<Long> getLangs(long objId, long attrId, long trId) throws DriverException;

    long getModelChanges(long fromId, ModelChangeProcessor p)
            throws DriverException;

    long getModelChanges(long fromId, long toId, ModelChangeProcessor p)
            throws DriverException;

    Iterator getDataChanges(long fromId, LongHolder lastId)
            throws DriverException;

    Iterator getDataChanges(long fromId, long toId, LongHolder lastId)
            throws DriverException;

    long getId(String name) throws DriverException;

    void setId(String name, long value) throws DriverException;

    boolean convertLinkForSysDb(long newBaseId, long oldBaseId) throws DriverException;
    String compileFilter(long id, long langId, Element xml, long trId, Session s) throws DriverException;

    List<KrnObject> filter(long[] ids, long langId,long userId,long[] baseIds,SrvOrLang orLang, int[] limit,int[] beginRow,int[] endRow, long trId, Session session) throws DriverException;
    List<KrnObject> filterLocal(String sql,String fud, long langId,long userId,long[] baseIds,SrvOrLang orLang, int limit,int beginRow,int endRow, long trId, Session session) throws DriverException;
    List<Object> filterGroup(long[] ids, long langId,long userId,long[] baseIds,SrvOrLang orLang, long trId, Session session) throws DriverException;
    long filterCount(long[] ids, long langId,long userId,long[] baseIds,SrvOrLang orLang, long trId, Session session) throws DriverException;
    long filterToAttr(long fid, long pobjId,long attrId,long langId,long userId,long[] baseIds,SrvOrLang orLang, long trId, Session session) throws DriverException;

    void compileClass(long classId) throws DriverException;

    void dbExport(String dir, String separator) throws DriverException;

    long dbImport(String dir, String separator) throws DriverException;
    
    // Methods
    
	KrnMethod changeMethod(
			String uid,
			String name,
			boolean isClassMethod,
			byte[] expr
			) throws DriverException;

	KrnMethod createMethod(
			KrnClass cls,
			String name,
			boolean isClassMethod,
			byte[] expr
			) throws DriverException;

	KrnMethod createMethod(String uid, KrnClass cls, String name, boolean isClassMethod, byte[] expr) throws DriverException;
	KrnMethod createMethod(String uid, KrnClass cls, String name, boolean isClassMethod, byte[] expr, long developer, boolean log) throws DriverException;

	void deleteMethod(String methodUid) throws DriverException;

	byte[] getMethodExpression(String methodUid) throws DriverException;
	byte[] getVcsChangeExpression(KrnVcsChange change) throws DriverException;
	ASTStart getMethodExpression2(String methodUid) throws Throwable;
	
	List<KrnMethod> getAllMethods() throws DriverException;

    Connection getConnection();

    public PreparedStatement getAttrPst(KrnAttribute attr) throws SQLException;
    public void loadAttributeValue(KrnAttribute attr, Map values) throws SQLException, DriverException;

    public void loadAttributeValue2(PreparedStatement pst, KrnAttribute attr, Map values) throws SQLException, DriverException;
    public String IDColumnName();
    public String getMemo(ResultSet rs, String name) throws SQLException;
    public long getNextId(String name) throws DriverException;
    public void initId(String name, long value) throws SQLException;
    public boolean isDeleted(KrnObject obj) throws SQLException;
    public void avtoIncrementOnOff(boolean onoff,String tabName) throws DriverException;
	void setForeignKeysEnabled(boolean enabled) throws DriverException;
    
    List runSql(String sql,boolean idUpdate) throws SQLException;
    boolean lockRecord(KrnClass cls, long objId, int timeout) throws SQLException;
    
    String getClassComment(long clsId) throws DriverException;
    void setClassComment(String clsUid, String comment) throws DriverException;
    void setClassComment(String clsUid, String comment, boolean log) throws DriverException;

    public void logModelChanges(int type, int action, String entityId) throws DriverException;

    String getAttributeComment(long attrId) throws DriverException;
    void setAttributeComment(String attrUid, String comment) throws DriverException;
    void setAttributeComment(String attrUid, String comment, boolean log) throws DriverException;

    String getMethodComment(String methodUid) throws DriverException;
    void setMethodComment(String methodUid, String comment) throws DriverException;
    void setMethodComment(String methodUid, String comment, boolean log) throws DriverException;

    void lockObject(long objId, long lockerId, int scope, long flowId, String sessionId) throws DriverException;
    void unlockObject(long objId, long lockerId) throws DriverException;
    void lockMethod(String muid, int scope, long flowId, String sessionId) throws DriverException;
    void unlockMethod(String muid) throws DriverException;
    void unlockFlowObjects(long flowId) throws DriverException;
    void unlockUnexistingFlowObjects(long flowClassId) throws DriverException;
    void commitLocks(String sessionId) throws DriverException;
    void rollbackLocks(String sessionId) throws DriverException;
    Lock getLock(long objId, long lockerId) throws DriverException;
    LockMethod getLockMethod(String muid) throws DriverException;
    Collection<Lock> getLocksByObjectId(long objId) throws DriverException;
    Collection<Lock> getLocksByLockerId(long lockerId) throws DriverException;
    Collection<Lock> getAllLocks() throws DriverException;
    Collection<LockMethod> getMethodAllLocks() throws DriverException;
    
    List<KrnObject> getSystemLangs() throws DriverException;
    
    void updateAllTriggers() throws DriverException;

    void refreshObjectCreating(KrnObject obj) throws SQLException;
    
    public Database getDatabase();

    void loadClassTable(KrnClass cls, BufferedReader r, String separator) throws DriverException;
    void loadAttributeTable(KrnAttribute attr, BufferedReader r, String separator) throws DriverException;
    void loadClassTableFromExtDb(KrnClass cls, Connection extConn) throws DriverException;
    void loadAttributeTableFromExtDb(KrnAttribute attr, Connection extConn) throws DriverException;

    // Local Cache support
    public ModelChanges getModelChanges(long changeId) throws DriverException;
	public DataChanges getDataChanges(long classId, long changeId, AttrRequest req,Session session)
			throws DriverException;
	public List<DataChanges> getDataChanges2(List<Object[]> changeRequests,Session session)
			throws DriverException;
	public void dropPolicy(FGACRule rule);
	public void createOrUpdatePolicy(String oldName, String oldTable, FGACRule rule);
	public void dropPolicy(FGARule rule);
	public void createOrUpdatePolicy(String oldName, String oldTable, FGARule rule);
	
	public String getPrefixForQuery();
	void setUserSession(UserSession us);
	
	public List<TriggerInfo> getTriggers(KrnClass cls);
	public String createTrigger(String triggerContext);
	public String removeTrigger(String triggerName);
	
	public int setAttrTriggerEventExpression(String expr, long attrId, int mode, boolean isZeroTransaction) throws SQLException, DriverException;
	public int setAttrTriggerEventExpression(String expr, long attrId, int mode, boolean isZeroTransaction, boolean logChanges) throws SQLException, DriverException;
	public int setClsTriggerEventExpression(String expr, long clsId, int mode, boolean isZeroTransaction) throws SQLException, DriverException;
	public int setClsTriggerEventExpression(String expr, long clsId, int mode, boolean isZeroTransaction, boolean logChanges) throws SQLException, DriverException;

	public void fireAttrCreated(KrnAttribute attr);
	public void fireAttrDeleted(KrnAttribute attr);
	public void fireAttrChanged(KrnAttribute attrOld, KrnAttribute attrNew);
	public void fireClassCreated(KrnClass cls);
	public void fireClassDeleted(KrnClass cls);
	public void fireClassChanged(KrnClass clsOld, KrnClass clsNew);
	public void fireMethodCreated(KrnMethod m);
	public void fireMethodDeleted(KrnMethod m);
	public void fireMethodChanged(KrnMethod oldm, KrnMethod newm);
	public String createProcedure(String nameProcedure, List<String> args, String body);
	public List execProcedure(String nameProcedure) throws DriverException;
	public List execProcedure(String nameProcedure, List<Object> vals) throws DriverException;
	public List execProcedure(String nameProcedure, List<Object> vals, List<String> types_in, List<String> types_out) throws DriverException;
	public List<String> getListProcedure(String type);
	public byte[] getProcedureContent(String name,String type);
	public boolean deleteProcedure(String name,String type);
	public List<Long> getVcsGroupObjects(long clsId) throws DriverException;
	public List<KrnVcsChange> getVcsDataChanges(int isFixd,int isRepld,long userId,long replId,String uid) throws DriverException;
	public List<KrnVcsChange> getVcsHistoryDataChanges(boolean isModel, String uid, int typeId,boolean isLastChange) throws DriverException;
	public List<KrnVcsChange> getVcsDifDataChanges(boolean isModel,long[] ids) throws DriverException;
	public long checkVcsDataChanges(long objId) throws DriverException;
	public long checkVcsModelChanges(String uid, int modelChangeType) throws DriverException;
	public String getVcsHistoryDataIncrement(KrnVcsChange change) throws DriverException;
	public void commitVcsModelClassAttr(String comment) throws DriverException;
	public void commitVcsObjects(List<KrnVcsChange> changes, String comment) throws DriverException;
	public void commitVcsObjectsAfterReplication(long replId, String comment) throws DriverException;
	public void rollbackVcsObjects(List<KrnVcsChange> changes, Session session) throws DriverException;
	public boolean isTableExists(String tableName);
	public boolean isIndexExists(String tName,String idxName);
	public String getIndexType(String tName,String idxName);
    public boolean isDbReadOnly();
    public List<OrlangTriggerInfo> getOrlangTriggersInfo() throws SQLException, DriverException;
	public void setLoadingFile(boolean b);
	public boolean getThrowOnSaveNullMode();
    public boolean setVcsUserForObject(KrnVcsChange change, long userId) throws DriverException;
	public void setVcsExport(long expObjId) throws DriverException;
    public long getLastId(String tname) throws DriverException;
    public List runSql(String sql,int limit,boolean isUpdate) throws SQLException;
    public boolean updateAttrFromExtDb(String jndiName,long attrId, String objUids);
    public String getSessionId();
    public String getSessionId(Connection conn);
    public String getDropTableSql(String tname);
    public List<String> showDbLocks();
    public List<Long> getFiltersContainingAttr(KrnClass filterCls, KrnAttribute configAttr, KrnAttribute classNameAttr, KrnAttribute exprSqlAttr, KrnAttribute titleAttr, KrnClass cls, KrnAttribute attr);
    public String getEntityNameFromVCS(ModelChange ch) throws DriverException;
    public String updateColumnsSysLang(int drop_index,boolean isDrop);
    }