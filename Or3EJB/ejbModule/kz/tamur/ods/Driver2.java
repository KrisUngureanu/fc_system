package kz.tamur.ods;

import static kz.tamur.comps.Constants.*;
import static kz.tamur.or3.util.Tname.OrlangTrigersVersionBD2;
import static kz.tamur.or3.util.Tname.OrlangTrigersVersionBD3;
import static kz.tamur.or3.util.Tname.TnameVersionBD;
import static kz.tamur.or3.util.Tname.isVersion;
import static kz.tamur.or3.util.Tname.version;
import static kz.tamur.or3ee.common.SessionIds.CID_BFILE;
import static kz.tamur.or3ee.common.SessionIds.CID_BLOB;
import static kz.tamur.or3ee.common.SessionIds.CID_BOOL;
import static kz.tamur.or3ee.common.SessionIds.CID_DATE;
import static kz.tamur.or3ee.common.SessionIds.CID_INTEGER;
import static kz.tamur.or3ee.common.SessionIds.CID_MEMO;
import static kz.tamur.or3ee.common.SessionIds.CID_STRING;
import static kz.tamur.or3ee.common.SessionIds.CID_TIME;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kz.tamur.DriverException;
import kz.tamur.admin.ErrorsNotification;
import kz.tamur.common.ErrorCodes;
import kz.tamur.comps.Constants;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.ods.mssql.MsSqlDriver3;
import kz.tamur.ods.sql92.LongResultSetHandler;
import kz.tamur.or3.util.FGACRule;
import kz.tamur.or3.util.FGARule;
import kz.tamur.or3ee.common.AttrChangeListener;
import kz.tamur.or3ee.common.ModelChangeListener;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.admin.ServerMessage;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.util.ThreadLocalDateFormat;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.cache.Cache;
import org.jboss.cache.CacheFactory;
import org.jboss.cache.DefaultCacheFactory;
import org.jboss.cache.Fqn;
import org.jboss.cache.Node;
import org.jboss.cache.config.Configuration;
import org.jboss.cache.config.EvictionAlgorithmConfig;
import org.jboss.cache.config.EvictionConfig;
import org.jboss.cache.config.EvictionRegionConfig;
import org.jboss.cache.eviction.MRUAlgorithmConfig;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.owasp.esapi.ESAPI;

import com.cifs.or2.kernel.AttrChange;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.Time;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.db.Database;
import com.cifs.or2.server.orlang.SrvOrLang;
import com.cifs.or2.server.sgds.HexStringOutputStream;
/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 13.01.2006
 * Time: 19:07:51
 * To change this template use File | Settings | File Templates.
 */
public abstract class Driver2 implements Driver {
	
	// Версия объединения веток Кызмет и ЮЛ
	protected static final int VERSION_UL = 55;
	
	protected static final ThreadLocalDateFormat FILE_DATE_FMT = new ThreadLocalDateFormat("yyyy-MM-dd");
	protected static final ThreadLocalDateFormat FILE_TIME_FMT = new ThreadLocalDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final String EMPTY_TEXT = "<!EMPTY!>";

	protected static boolean isInstallDb = false;
	protected static boolean dataLog = true;
	protected static boolean isSchemeName = false;
	private String prefixForQuery;
	public static boolean isImportState = false;
	protected boolean isLoadingFile = false;
	public static long importObjId = -1;

    protected String dsName;
	protected Database db;
	protected int sysLangCount;
    private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + Driver2.class.getName());
    protected Connection conn;
    protected static final Map <String,String>operMap = new HashMap<String, String>();
    protected static final Map <String,String>grpMap = new HashMap<String, String>();
    protected List<String> params;
    protected Map <String,Object> dateParams;
    protected String addColName;
    protected String addColFullTextFind;
    protected Map<String,Integer> sortedFindMap=new HashMap<String,Integer>();
    protected static Pattern wRe,wRe1, sRe,sRe1, uRe, groupRe;

    protected static boolean deleteRefs;
    protected static boolean checkRefs;
	protected static Map<String, Map<Long, Integer>> sysLangIndexes =
			new HashMap<String, Map<Long,Integer>>();
	
	public abstract boolean renameClassTable(long id, String newName);//TODO r tname
    //protected abstract String getClassTname(long clsId); 
    public abstract boolean renameAttrTable(KrnAttribute attr, String newName);

    protected static Map<String, Map<String, ASTStart>> methodsExprsById =
    	new HashMap<String, Map<String, ASTStart>>();

    protected static Map<String, Map<MultiKey, List<Long>>> triggerExcept =
    	new HashMap<String, Map<MultiKey, List<Long>>>();

    protected static Cache<Pair<Long, Long>, Object> cache;
	private static Node<Pair<Long, Long>, Object> objRoot;
	private static final Pair<Long, Long> OBJ_KEY = new Pair<Long, Long>(0L, 0L);
	
	protected UserSession us = null;
	protected List<Pair<Long, String>> notCommittedJrbNodes = new ArrayList<>();
	protected List<Pair<Long, String>> rewrittenJrbNodes = new ArrayList<>();

    private static Map<Long, List<AttrChangeListener>> attrChangeListenersByClassId = new HashMap<Long, List<AttrChangeListener>>();
    protected static List<ModelChangeListener> modelChangeListeners = new ArrayList<ModelChangeListener>();
    private static boolean loggingGetObjSql=false;
    public static void addTriggerExceptFile(String dsName, String fileName) {
		if (!triggerExcept.containsKey(dsName)) {
			loadTriggerExceptFiles(dsName, Funcs.getCanonicalFile(fileName));
		}
	}

    static {
		String str = System.getProperty("dataLog");
		dataLog = (str != null) ? !"false".equals(str.toLowerCase()) : true;
		str = System.getProperty("schemeName");
		isSchemeName = (str != null) ? "true".equals(str.toLowerCase()) : false;

		deleteRefs = !"0".equalsIgnoreCase(System.getProperty("deleteRefs"));
    	checkRefs = "1".equalsIgnoreCase(System.getProperty("checkRefs"));

    	operMap.put(""+OPER_EQ,OP_EQ);
        operMap.put(""+OPER_NEQ,OP_NEQ);
        operMap.put(""+OPER_GT,OP_GT);
        operMap.put(""+OPER_LT,OP_LT);
        operMap.put(""+OPER_GEQ,OP_GEQ);
        operMap.put(""+OPER_LEQ,OP_LEQ);
        operMap.put(""+OPER_EXIST,OP_EXIST);
        operMap.put(""+OPER_NOT_EXIST,OP_NOT_EXIST);
        operMap.put(""+OPER_INCLUDE,OP_INCLUDE);
        operMap.put(""+OPER_EXCLUDE,OP_EXCLUDE);
        operMap.put(""+OPER_CONTAIN,OP_CONTAIN);
        operMap.put(""+OPER_NOT_CONTAIN,OP_NOT_CONTAIN);
        operMap.put(""+OPER_START_WITH,OP_START_WITH);
        operMap.put(""+OPER_FINISH_ON,OP_FINISH_ON);
        operMap.put(""+OPER_ANOTHER,OP_ANOTHER);
        operMap.put(""+OPER_DESCEND,OP_DESCEND);
        operMap.put(""+OPER_ASCEND,OP_ASCEND);
        grpMap.put(""+GROUP_COUNT,GR_COUNT);
        grpMap.put(""+GROUP_SUM,GR_SUM);
        grpMap.put(""+GROUP_MAX,GR_MAX);
        grpMap.put(""+GROUP_MIN,GR_MIN);
        grpMap.put(""+GROUP_AVG,GR_AVG);
        
        try {
            wRe = Pattern.compile("\\{(\\d+)[^\\{]*?(\\[.*?\\])[^\\]]*?(\\}\\d+)");
            wRe1 = Pattern.compile("\\[.*?\\]");
            sRe = Pattern.compile("\\{(\\d+)([^\\{]*?)\\}\\d+");
            sRe1 = Pattern.compile("t(\\d+)\\.*?");
            uRe = Pattern.compile("\n UNION \n");
            groupRe = Pattern.compile("G_(" + GR_COUNT + "|" + GR_SUM + "|" + GR_MAX + "|" + GR_MIN + "|" + GR_AVG + "|" + ")_\\d");
        } catch (Exception e) {
        	log.error(e);
        }
        
        if ("1".equals(System.getProperty("useCache"))) {
    		CacheFactory<Pair<Long, Long>, Object> cf = new DefaultCacheFactory<Pair<Long, Long>, Object>();
    		// Eviction
    		EvictionAlgorithmConfig ealg = new MRUAlgorithmConfig(100000);
    		EvictionRegionConfig eregion = new EvictionRegionConfig(Fqn.ROOT, ealg);
    		EvictionConfig ecfg = new EvictionConfig(eregion, 1000);
    		Configuration cfg = new Configuration();
    		cfg.setEvictionConfig(ecfg);
    		cfg.setCacheMode(Configuration.CacheMode.LOCAL);
    		cache = cf.createCache(cfg);
    		cache.start();
    		objRoot = cache.getRoot().addChild(Fqn.fromString("/KrnObject"));
        }
    }
    
    @Override
	public Database getDatabase() {
		return db;
	}

	@Override
	public long init() throws DriverException {
        Statement st = null;
        //Блокировка t_ids для исключения дублирования апгрейда при одновременном старте нескольких серверов
        Connection conn_ = getNewConnection();
		try {
			install();
	        st = conn_.createStatement();
	        st.setQueryTimeout(1);
	        String sql="UPDATE " + prefixForQuery + "t_ids SET c_last_id = c_last_id WHERE c_name = 'dbase_id'";
	        int res = -1;
	        int count=1;
	        while(res<0){
	        	try{
			        res = st.executeUpdate(sql);
	    		} catch (SQLException e) {
	                log.error(e, e);
					log.info("Блокировка t_ids, попытка: "+count);
					count++;
					try {
						java.lang.Thread.sleep(30000);//sleep on 30sec
					} catch (InterruptedException ie) {
						// TODO Auto-generated catch block
			            log.error(ie, ie);
					}
	            }
	        }
	        kz.tamur.or3.util.Tname.version = getId("version");
	        upgrade();
	        log.info("DB version="+version);
	        conn_.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
            log.error(e, e);
        } finally {
            DbUtils.closeQuietly(st);
            DbUtils.closeQuietly(conn_);
        }
		return getId("dbase_id");
	}
    
	@Override
	public boolean validate() {
		return true;
/*		try {
			return conn.isValid(10);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
*/	}

	protected Driver2(Database db, String dsName, UserSession us) throws DriverException {
		kz.tamur.or3.util.Tname.isServer = true;
        this.db = db;
        this.dsName = dsName;
        this.us = us;
        conn = getNewConnection();
//        try {
//			conn.setAutoCommit(false);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
        dateParams = new HashMap<String, Object>();
        params = new ArrayList<String>();
		prefixForQuery = isSchemeName ? Funcs.sanitizeSQL(this.db.getSchemeName()) + "." : "";
    }

    protected Connection getNewConnection() throws DriverException {
        try {
            Connection conn_= db.getConnection();
            if (conn_!= null) {
            	String threadName = Thread.currentThread().getName();
            	threadName = threadName.replaceAll("\\(CONN_ID:\\d{1,}\\)", "");  
           		Thread.currentThread().setName(threadName + "(CONN_ID:" + getSessionId(conn_) + ")");

                try {
        			conn_.setAutoCommit(false);
        		} catch (SQLException e) {
                    log.error(e, e);
        		}

            }
            return conn_;
        } catch (SQLException ex) {
			if(ErrorsNotification.isInitialize()){
				ErrorsNotification.notifyErrors("TO_400", "NO_CONNECT_DB", "Communications link failure", ex,null);
			}
            throw new DriverException(ex);
        }
    }
    
    public boolean isPrimary(KrnClass type) {
        return type.id <= 10;
    }

    protected void putMethodExpression(String uid, ASTStart expr) {
    	synchronized(methodsExprsById) {
    		Map<String, ASTStart> map = methodsExprsById.get(dsName);
    		if (map == null) {
    			map = new HashMap<String, ASTStart>();
    			methodsExprsById.put(dsName, map);
    		}
    		map.put(uid, expr);
    	}
    }

    public static void removeMethodExpression(String dsName, String uid) {
    	synchronized(methodsExprsById) {
    		Map<String, ASTStart> map = methodsExprsById.get(dsName);
    		if (map != null)
    			map.remove(uid);
    	}
    }

    protected ASTStart getCachedMethodExpression(String uid) {
    	synchronized(methodsExprsById) {
    		Map<String, ASTStart> map = methodsExprsById.get(dsName);
    		return (map != null) ? map.get(uid) : null;
    	}
    }

	public ASTStart getMethodExpression2(String uid) throws Throwable {
		ASTStart expr = getCachedMethodExpression(uid);
		if (expr != null)
			return expr;
		
		byte[] bs = getMethodExpression(uid);
		expr = OrLang.createStaticTemplate(new InputStreamReader(
        		new ByteArrayInputStream(bs), "UTF-8"));
		putMethodExpression(uid, expr);
    	return expr;
    }

    public synchronized long getNextId(String name)
            throws DriverException {

        Connection conn = getNewConnection();
        try {
            conn.setAutoCommit(false);
            // Увеличиваем значение на 1
            QueryRunner qr = new QueryRunner(true);
            qr.update(conn,
                    "UPDATE "+prefixForQuery+"t_ids SET c_last_id=c_last_id+1 WHERE c_name=?",
                    name);

            // Считываем значение
            LongResultSetHandler h = new LongResultSetHandler();
            Long id = qr.query(conn,
                    "SELECT c_last_id FROM "+prefixForQuery+"t_ids WHERE c_name=?",
                    h, name);
            conn.commit();

            if (id == null) {
                throw new DriverException("Failed to get next id for '" + name + "'");
            }

            return id.longValue();

        } catch (SQLException e) {
            try {
                DbUtils.rollback(conn);
            } catch (SQLException e1) {
                // NOP
            }
            throw new DriverException(e);
        } finally {
            DbUtils.closeQuietly(conn);
        }
    }

    public long getLastId(String tname) throws DriverException {
            Connection conn = getNewConnection();
            try {
                // Считываем значение
                QueryRunner qr = new QueryRunner(true);
                LongResultSetHandler h = new LongResultSetHandler();
                Long id = qr.query(conn, "SELECT max(c_id) FROM "+prefixForQuery+tname, h);

                if (id == null) {
                    throw new DriverException("Failed to get id for '" + tname + "'", ErrorCodes.DRV_ID_NOT_FOUND);
                }

                return id.longValue();

            } catch (SQLException e) {
                throw new DriverException(e);
            } finally {
                DbUtils.closeQuietly(conn);
            }
    }
    public long getId(String name) throws DriverException {
        synchronized(Driver2.class) {
            Connection conn = getNewConnection();
            try {
                // Считываем значение
                QueryRunner qr = new QueryRunner(true);
                LongResultSetHandler h = new LongResultSetHandler();
                Long id = qr.query(conn, "SELECT c_last_id FROM "+prefixForQuery+"t_ids WHERE c_name=?", h, name);

                if (id == null) {
                    throw new DriverException("Failed to get id for '" + name + "'", ErrorCodes.DRV_ID_NOT_FOUND);
                }

                return id.longValue();

            } catch (SQLException e) {
                throw new DriverException(e);
            } finally {
                DbUtils.closeQuietly(conn);
            }
        }
    }

    public void initId(String name, long value)
            throws SQLException {
        QueryRunner qr = new QueryRunner(true);
        qr.update(conn, "INSERT INTO "+prefixForQuery+"t_ids (c_name, c_last_id) VALUES(?,?)", new Object[]{name, value});
    }

    public void avtoIncrementOnOff(boolean onoff,String tabName) throws DriverException{
    }

    public void setId(String name, long value)
            throws DriverException {
        synchronized(Driver2.class) {
            Connection conn = getNewConnection();
            try {
                // Перезаписываем тек значение, для блокировки записи в таблице
                QueryRunner qr = new QueryRunner(true);
                qr.update(conn,
                        "UPDATE "+prefixForQuery+"t_ids SET c_last_id=c_last_id WHERE c_name=?",
                        name);

                // Считываем значение
                LongResultSetHandler h = new LongResultSetHandler();
                Long id = qr.query(conn,
                        "SELECT c_last_id FROM "+prefixForQuery+"t_ids WHERE c_name=?",
                        h, name);

                if (id == null) {
                    conn.rollback();
                    throw new DriverException("Failed to get next id for '" + name + "'");
                }

                // Если value > id то записываем value
                if (value > id.longValue() || "dbase_id".equals(name)) {
                    qr.update(conn,
                            "UPDATE "+prefixForQuery+"t_ids SET c_last_id=? WHERE c_name=?",
                            new Object[]{new Long(value), name});
                    conn.commit();
                } else {
                    conn.rollback();
                }

            } catch (SQLException e) {
                try {
                    DbUtils.rollback(conn);
                } catch (SQLException e1) {
                    // NOP
                }
                throw new DriverException(e);
            } finally {
                DbUtils.closeQuietly(conn);
            }
        }
    }

    public boolean convertLinkForSysDb(long newBaseId, long oldBaseId) throws DriverException  {
        boolean res=false;
        //clsId=512 - Структура баз
        //attrId=4762 - производственная структура
        //attrId=3669 - текущий прик о штатн распис -приказ о штатн распис-
        try {
			KrnClass cls = getClassByIdComp(512);
			if(!"Структура баз".equals(cls.name))
				return res;
		} catch (Exception e2) {
			return res;
		}
        synchronized(Driver2.class) {
            Connection conn = getNewConnection();
            try {
    	    	System.out.print("newBaseId:"+newBaseId + ";oldBaseId:" + oldBaseId);
	    	    Statement stSel = conn.createStatement();
	    	    Statement st = conn.createStatement();
	    	    ResultSet rs = stSel.executeQuery("select c_id, c_name, c_class_id,(select c_name from t_classes where c_id=c_class_id), c_col_type, c_rattr_id from t_attrs where c_type_id=512");
	    	    while(rs.next()) {
	    	    	long classId = rs.getLong(3);
	    	    	String className = rs.getString(4);
	    	    	long attrId = rs.getLong(1);
	    	    	String attrName = rs.getString(2);
	    	    	System.out.print(ESAPI.encoder().encodeForHTML(className) + "." + ESAPI.encoder().encodeForHTML(attrName) + ":");
	    	    	long colType = rs.getLong(5);
	    	    	long rAttrId = rs.getLong(6);
	    	    	if (rs.wasNull()){
	    	    	int r = 0;
	    	    	if(colType == 0)
	    	    		r = st.executeUpdate("update ct"+classId+" set cm"+attrId+ "="+newBaseId+" where cm"+attrId+"="+oldBaseId);
	    	    	else	
	    	    		r = st.executeUpdate("update at"+classId+"_"+attrId+" set cm"+attrId+ "="+newBaseId+" where cm"+attrId+"="+oldBaseId);
	    	    	System.out.println(" UPDATED = " + r);
	    	    	}
	    	    }
	    	    rs.close();
	    	    stSel.close();
	    	    st.close();
                    //сохраяем штатку и производственную структуру в системную БД копируя их из выбранной БД.
			    long SHRId = 0;
		    	    Statement stSHR = conn.createStatement();
		    	    ResultSet resSHR = stSHR.executeQuery("select cm3669 from ct512 where c_obj_id="+oldBaseId);
		    	    if(resSHR.next()) { 
		    	    	SHRId = resSHR.getLong(1);
			    }
			    resSHR.close();
			    stSHR.executeUpdate("update ct512 set cm3669="+SHRId+" where c_obj_id="+newBaseId);
	
			    long PodrId = 0;
		    	    Statement stPodrId = conn.createStatement();
		    	    ResultSet resPodrId = stPodrId.executeQuery("select cm4762 from ct512 where c_obj_id="+oldBaseId);
		    	    if(resPodrId.next()) { 
		    	    	PodrId = resPodrId.getLong(1);
			    }
			    resPodrId.close();
			    stSHR.executeUpdate("update ct512 set cm4762="+SHRId+" where c_obj_id="+newBaseId);
			    stPodrId.close();
			    stSHR.close();
	
	            conn.commit();
	            res=true;
	        } catch (SQLException e) {
	            try {
	                DbUtils.rollback(conn);
	            } catch (SQLException e1) {
	                // NOP
	            }
	            throw new DriverException(e);
	        } finally {
	            DbUtils.closeQuietly(conn);
	        }
        }
        return res;
    }
    public long getBaseId() {
        return db.getId();
    }

    public void setValue(long objId, long attrId, int index, long langId, long trId, Object value, boolean insert) throws DriverException {
        KrnAttribute attr = db.getAttributeById(attrId);
        KrnObject obj = getObjectById(attr.classId, objId, trId, false);
        setValue(obj, attrId, index, langId, trId, value, insert, true);
    }
    
    public void setValue(List<Long> objectsIds, long attrId, long langId, long trId, Object value) throws DriverException {
        KrnAttribute attr = db.getAttributeById(attrId);
        setValueImpl(objectsIds, attr, langId, trId, value);
    }
    
    public void setValue(KrnObject obj, long attrId, int index, long langId, long trId, Object value, boolean insert) throws DriverException {
    	setValue(obj, attrId, index, langId, trId, value, null, insert);
    }
    
    public void setValue(KrnObject obj, long attrId, int index, long langId, long trId, Object value, Object oldValue, boolean insert) throws DriverException {
        setValue(obj, attrId, index, langId, trId, value, oldValue, insert, true);
    }

    public void setValue(KrnObject obj, long attrId, int index, long langId, long trId, Object value, boolean insert, boolean replLog) throws DriverException {
        setValue(obj, attrId, index, langId, trId, value, null, insert, replLog);
    }
    
    public void setValue(KrnObject obj, long attrId, int index, long langId, long trId, Object value, Object oldValue, boolean insert, boolean replLog) throws DriverException {
        KrnAttribute attr = db.getAttributeById(attrId);
        if (attr.typeClassId > 10) {
            // Для объектных типов делаем обработку обратных атрибутов
            KrnAttribute[] rattrs = db.getRevAttributes(attrId);
            if (rattrs.length > 0) {
                // Сначала удалем ссылку на себя в существующем значении атрибута
                long[] objIds = {obj.id};
                SortedSet<Value> vs = getValues(objIds, null, attrId, langId, trId);
                for (Iterator<Value> it = vs.iterator(); it.hasNext();) {
                    Value v = (Value) it.next();
                    if (attr.collectionType != COLLECTION_SET && v.index == index) {
                        KrnObject ov = (KrnObject)v.value;
                        removeValue(ov, rattrs, obj.id, trId, false);
                        break;
                    }
                }
                // Проставляем ссылку на себя в новом значении атрибута
                KrnObject vobj = null;
                if (value instanceof Number) {
                	vobj = getObjectById(((Number)value).longValue());
                } else if (value instanceof KrnObject) {
                	vobj = (KrnObject)value;
                }
                if (vobj != null)
                addValue(vobj, rattrs, obj, trId);
            }
        }
        setValueImpl(obj, attr, index, langId, trId, value, oldValue, insert, replLog);
        
        if ("6168007f-a2c9-4992-90e6-eaefa529b55e".equals(attr.uid)) {
        	db.reloadBlobDirs(this);
        }
    }

	public void setValue(KrnObject obj, long trId, Map<Pair<KrnAttribute, Long>, Object> values) throws DriverException {
		for (Pair<KrnAttribute, Long> key : values.keySet()) {
			KrnAttribute attr = key.first;
			Object value = values.get(key);
			if (attr.typeClassId > 10) {
				// Для объектных типов делаем обработку обратных атрибутов
				KrnAttribute[] rattrs = db.getRevAttributes(attr.id);
				if (rattrs.length > 0) {
					// Сначала удалем ссылку на себя в существующем значении
					// атрибута
					long[] objIds = { obj.id };
					SortedSet<Value> vs = getValues(objIds, null, attr.id, 0, trId);
					for (Iterator<Value> it = vs.iterator(); it.hasNext();) {
						Value v = (Value) it.next();
						if (attr.collectionType != COLLECTION_SET
								&& v.index == 0) {
							KrnObject ov = (KrnObject) v.value;
							removeValue(ov, rattrs, obj.id, trId, false);
							break;
						}
					}
					// Проставляем ссылку на себя в новом значении атрибута
					KrnObject vobj = null;
					if (value instanceof Number) {
						vobj = getObjectById(((Number) value).longValue());
					} else if (value instanceof KrnObject) {
						vobj = (KrnObject) value;
					}
					if (vobj != null)
						addValue(vobj, rattrs, obj, trId);
				}
			}
			if ("6168007f-a2c9-4992-90e6-eaefa529b55e".equals(attr.uid)) {
				db.reloadBlobDirs(this);
			}
		}
		setValueImpl(obj, trId, values);
	}

    public void deleteValue(long objId, long attrId, int[] indices, long langId, long trId, boolean deleteRefs)
            throws DriverException {
        KrnAttribute attr = db.getAttributeById(attrId);
        if (attr.typeClassId > 10) {
            // Для объектных типов делаем обработку обратных атрибутов
            KrnAttribute[] rattrs = db.getRevAttributes(attrId);
            if (rattrs.length > 0) {
                // Удалем ссылку на себя в существующем значении атрибута
                long[] objIds = {objId};
                SortedSet<Value> vs = getValues(objIds, null, attrId, 0, trId);
                for (Iterator<Value> it = vs.iterator(); it.hasNext();) {
                    Value v = (Value) it.next();
                    if (Arrays.binarySearch(indices, v.index) >= 0) {
                        removeValue(((KrnObject)v.value), rattrs, objId, trId, deleteRefs);
                    }
                }
            }
        }
        KrnObject obj = getObjectById(objId);
        deleteValueImpl(obj, attr, indices, langId, trId, deleteRefs);
    }

    public void deleteValue(long objId, long attrId, Collection<Object> values, long trId, boolean deleteRefs)
            throws DriverException {
        KrnAttribute attr = db.getAttributeById(attrId);
        if (attr.typeClassId > 10) {
            // Для объектных типов делаем обработку обратных атрибутов
            KrnAttribute[] rattrs = db.getRevAttributes(attrId);
            if (rattrs.length > 0) {
                // Удалем ссылку на себя в существующем значении атрибута
                for (Object value : values) {
                    removeValue(((KrnObject)value), rattrs, objId, trId, deleteRefs);
                }
            }
        }
        KrnObject obj = getObjectById(objId);
        deleteValueImpl(obj, attr, values, trId, deleteRefs);
    }

    public abstract void setValueImpl(KrnObject obj,
                                      KrnAttribute attr,
                                      int index,
                                      long langId,
                                      long trId,
                                      Object value,
                                      Object oldValue,
                                      boolean insert, boolean replLog) throws DriverException;

    // Сырая реализация. Метод используется только для массового сохранения свойств роли пользователей 
    public abstract void setValueImpl(List<Long> objectsIds, KrnAttribute attr, long langId, long trId, Object value) throws DriverException;

	public abstract void setValueImpl(KrnObject obj, long trId, Map<Pair<KrnAttribute, Long>, Object> values) throws DriverException;
	
    public abstract void deleteValueImpl(KrnObject obj,
                                         KrnAttribute attr,
                                         int[] indices,
                                         long langId,
                                         long trId, boolean deleteRefs) throws DriverException;

    public abstract void deleteValueImpl(KrnObject obj,
                                         KrnAttribute attr,
                                         Collection<Object> values,
                                         long trId, boolean deleteRefs) throws DriverException;

    protected abstract void install() throws DriverException;

    protected void upgrade() throws DriverException {
        //long v = -1L;
        try {
        	version = getId("version");
        } catch(DriverException e) {
            if (e.getErrorCode() == ErrorCodes.DRV_ID_NOT_FOUND) {
            	version = 0;
                try {
                    initId("version", version);
                    commit();
                } catch (SQLException e1) {
                    rollback();
                    throw new DriverException(e1);
                }
            } else {
                throw e;
            }
        }
        try {
            if (version < 1) {
                KrnClass userClass = db.getClassByName("User");
                KrnAttribute pdAttr = db.getAttributeByName(userClass.id, "password");
                    PreparedStatement pst = conn.prepareStatement(
                        "UPDATE "+getClassTableName(userClass.id) + " SET "+getColumnName(pdAttr) +"=?");
                    pst.setString(1, "QL0AFWMIX8NRZTKeof9cXsvbvu8=");
                    pst.executeUpdate();
                    pst.close();
            }
            long v = version;
            long nv = upgradeImpl(v);
            if (nv != v) {
                setId("version", nv);
                commit();
            }
            //version = getId("version");
        } catch (SQLException e) {
            log.error(e, e);
            rollback();
            throw new DriverException(e);
        }
    }

    protected boolean contains(List<KrnAttribute> attrs, KrnAttribute attr) {
        for (int i = 0; i < attrs.size(); i++) {
            if (((KrnAttribute)attrs.get(i)).id == attr.id) {
                return true;
            }
        }
        return false;
    }

    protected boolean contains(List<KrnClass> classes, KrnClass cls) {
        for (int i = 0; i < classes.size(); i++) {
            if (classes.get(i).id == cls.id) {
                return true;
            }
        }
        return false;
    }

    private void removeValue(KrnObject obj, KrnAttribute[] attrs,
                             long valueId, long trId, boolean deleteRefs) throws DriverException {
        long[] objIds = {obj.id};
        List<KrnAttribute> oattrs = db.getAttributesByClassId(obj.classId, true);
        for (int i = 0; i < attrs.length; i++) {
            KrnAttribute attr = attrs[i];
            if (contains(oattrs, attr)) {
                if (attr.collectionType == COLLECTION_SET) {
                    KrnObject vobj = getObjectById(valueId);
                    deleteValueImpl(obj, attr, Collections.singletonList((Object)vobj), trId, deleteRefs);
                } else {
                    SortedSet<Value> vs = getValues(objIds, null, attr.id, 0, trId);
                    SortedSet<Integer> indicies = new TreeSet<Integer>();
                    for (Iterator<Value> it = vs.iterator(); it.hasNext();) {
                        Value v = (Value) it.next();
                        if (((KrnObject)v.value).id == valueId) {
                            indicies.add(v.index);
                        }
                    }
                    if (indicies.size() > 0) {
                        deleteValueImpl(obj, attr, Funcs.makeIntArray(indicies), 0, trId, deleteRefs);
                    }
                }
            }
        }
    }

    private void addValue(KrnObject obj, KrnAttribute[] attrs,
                          KrnObject value, long trId) throws DriverException {
        long[] objIds = {obj.id};
        List<KrnClass> valueSuperClasses = db.getSuperClasses(value.classId);
        List<KrnAttribute> oattrs = db.getAttributesByClassId(obj.classId, true);
        for (int i = 0; i < attrs.length; i++) {
            KrnAttribute attr = attrs[i];
            KrnClass cls = db.getClassById(attr.typeClassId);
            if (contains(valueSuperClasses, cls) && contains(oattrs, attr)) {
                int index = 0;
                if (attr.collectionType == COLLECTION_ARRAY) {
                    SortedSet<Value> vs = getValues(objIds, null, attr.id, 0, trId);
                    if (vs.size() > 0) {
                        for (Iterator<Value> it = vs.iterator(); it.hasNext();) {
                            Value v = (Value) it.next();
                            if (((KrnObject)v.value).id == value.id) {
                                // Объект уже существует
                                return;
                            } else {
                                index = Math.max(index, v.index + 1);
                            }
                        }
                    }
                }
                setValueImpl(obj, attr, index, 0, trId, new Long(value.id), null,  false, true);
            }
        }
    }

    @Override
	public Object getValue(long objId, long attrId, int index, long langId,
			long trId) throws DriverException {
    	
		SortedSet<Value> vs = getValues(new long[] { objId }, null, attrId, langId, trId);
		for (Object v1 : vs) {
			Value v = (Value) v1;
			if (v.index == index) {
				return v.value;
			}
		}
		return null;
	}
   
	public List<KrnObject> getObjectsByIds(long[] ids) throws DriverException {
    	List<KrnObject> res = new ArrayList<KrnObject>(ids.length);
    	for (long id : ids) {
    		KrnObject obj = getObjectById(id);
    		if (obj != null)
    			res.add(obj);
    	}
    	return res;
    }
    
    public KrnObject getDirtyObjectById(long id) throws DriverException {
        List<KrnObject> objs = getObjectsByIds(new long[] {id});
        if (objs.size() == 0) {
            throw new DriverException("Object with id=" + id + " not found");
        }
        return (KrnObject)objs.get(0);
    }

    public KrnObject getDirtyObjectByIdQuietly(long id) throws DriverException {
        List<KrnObject> objs = getObjectsByIds(new long[] {id});
        return objs.size() > 0 ? objs.get(0) : null;
    }

    public KrnObject getObjectByUid(String uid, long trId) throws DriverException {
        List<KrnObject> objs = getObjectsByUids(new String[] {uid}, trId, false);
        if (objs.size() == 0) {
            throw new DriverException("Object with uid=" + uid + " not found");
        }
        return (KrnObject)objs.get(0);
    }

    public java.sql.Date convertDate(com.cifs.or2.kernel.Date date) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, date.day);
        cal.set(Calendar.MONTH, date.month);
        cal.set(Calendar.YEAR, date.year);
        return new java.sql.Date(cal.getTimeInMillis());
    }

    public Timestamp convertTime(Time time) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, time.msec);
        cal.set(Calendar.SECOND, time.sec);
        cal.set(Calendar.MINUTE, time.min);
        cal.set(Calendar.HOUR_OF_DAY, time.hour);
        cal.set(Calendar.DAY_OF_MONTH, time.day);
        cal.set(Calendar.MONTH, time.month);
        cal.set(Calendar.YEAR, time.year);
        return new Timestamp(cal.getTimeInMillis());
    }
    protected Object getParamAt(String fuid, int index, Session s) {
        if (params.size() > index) {
            String param = params.get(index);
            if("$".equals(param.substring(param.length()-1)))
                param=param.substring(0,param.length()-1);
            if (param.substring(0, 1).equals("$")) {
                return dateParams.get(param);
            }else if (param.length()>9 && param.substring(0, 9).equals("_CURTRANS")) {
                    return Long.valueOf(param.substring(9));
            } else {
                if("#".equals(param.substring(param.length()-1)))
                    param=param.substring(0,param.length()-1);
                int ind = param.indexOf("Like%");
                if (ind < 0) ind = param.indexOf("LikeNot%");
                if (ind < 0) ind = param.length();
                List<Object> pv = s.getFilterParams(fuid, param.substring(0, ind));
                
                boolean ignoreCase = param.contains("lower(");
                if (param.indexOf("Like%") > 0) {
                	int e_par=0;
                	if(param.indexOf(" ESCAPE")>0){
                		e_par= param.indexOf(" ESCAPE2")>0?2:1;
                	}
                    List<String> pv_=new Vector<String>();
                    if (param.indexOf("Like%%%") > 0){
                    	for (Object aPv : pv) {
                    		if(e_par==2){
                    			aPv=((String)aPv).replaceAll("~", "~~"); 
                    			aPv=((String)aPv).replaceAll("_", "~_");
                    			aPv=((String)aPv).replaceAll("%", "~%");
                    		}
                    		pv_.add("%" + (ignoreCase ? aPv.toString().toLowerCase(Constants.OK) : aPv));
                    }
                    return pv_;
                    } else if (param.indexOf("Like%%") > 0) {
                        for (Object aPv : pv) {
                    		if(e_par==2){
                    			aPv=((String)aPv).replaceAll("~", "~~"); 
                    			aPv=((String)aPv).replaceAll("_", "~_");
                    			aPv=((String)aPv).replaceAll("%", "~%");
                    		}
                            pv_.add("%" + (ignoreCase ? aPv.toString().toLowerCase(Constants.OK) : aPv) + "%");
                        }
                        return pv_;
                    } else {
                        for (Object aPv : pv) {
                    		if(e_par==2){
                    			aPv=((String)aPv).replaceAll("~", "~~"); 
                    			aPv=((String)aPv).replaceAll("_", "~_");
                    			aPv=((String)aPv).replaceAll("%", "~%");
                    		}
                            pv_.add((ignoreCase ? aPv.toString().toLowerCase(Constants.OK) : aPv) + "%");
                        }
                        return pv_;
                    }

                } else if (param.indexOf("LikeNot%") > 0) {
                	int e_par=0;
                	if(param.indexOf(" ESCAPE")>0){
                		e_par= param.indexOf(" ESCAPE2")>0?2:1;
                	}
                    List<String> pv_=new Vector<String>();
                    if (param.indexOf("LikeNot%%") > 0) {
                        for (Object aPv : pv) {
                    		if(e_par==2){
                    			aPv=((String)aPv).replaceAll("~", "~~"); 
                    			aPv=((String)aPv).replaceAll("_", "~_");
                    			aPv=((String)aPv).replaceAll("%", "~%");
                    		}
                            pv_.add("%" + (ignoreCase ? aPv.toString().toLowerCase(Constants.OK) : aPv) + "%");
                        }
                        return pv_;
                    } else {
                        for (Object aPv : pv) {
                    		if(e_par==2){
                    			aPv=((String)aPv).replaceAll("~", "~~"); 
                    			aPv=((String)aPv).replaceAll("_", "~_");
                    			aPv=((String)aPv).replaceAll("%", "~%");
                    		}
                            pv_.add((ignoreCase ? aPv.toString().toLowerCase(Constants.OK) : aPv) + "%");
                        }
                        return pv_;
                    }
                } else if (pv.size() > 1)
                    return pv;
                else if (pv.size() > 0) 
                	return pv.get(0);
                else return null;
            }
        } else return null;

    }
    protected String getSqlExpr(String fuid, String filterSql, long langId, long userId, long[] baseIds, SrvOrLang orLang, long trId,Session session, List<Boolean> paramRespRegs) {
        return getSqlExpr(fuid, filterSql, langId, userId, baseIds, orLang, trId, null, session, paramRespRegs);
    }
    protected String getSqlExpr(String fuid, String filterSql, long langId, long userId, long[] baseIds, SrvOrLang orLang, long trId, String numGroupTables,Session session, List<Boolean> paramRespRegs) {
    	String res="";
        Matcher matcher = uRe.matcher(filterSql);
        int pos=0;
        dateParams.clear();
        ArrayList<String> params_ = new ArrayList<String>();
	    while (matcher.find()) {
	     	String filterSql_=filterSql.substring(pos, matcher.start());
	       	String flr_sql=getSqlExprUnit(fuid,filterSql_,langId,userId,baseIds,orLang,trId,numGroupTables,session, paramRespRegs);
	       	flr_sql = setCurTransUnit(flr_sql, trId, paramRespRegs);
	       	if(flr_sql.indexOf(" 1>1 ")<0){
		       	if(!res.equals(""))
		       		res+="\n UNION \n";
	       		res+=flr_sql;
	       		params_.addAll(params);
	       	}
	        pos = matcher.end();
	    }
     	String filterSql_=filterSql.substring(pos);
       	String flr_sql=getSqlExprUnit(fuid,filterSql_,langId,userId,baseIds,orLang,trId,numGroupTables,session, paramRespRegs);
       	flr_sql = setCurTransUnit(flr_sql, trId, paramRespRegs);
       	if(flr_sql.indexOf(" 1>1 ")<0){
	       	if(!res.equals(""))
	       		res+="\n UNION \n";
       		res+=flr_sql;
       		params_.addAll(params);
       	}
        params.clear();
   		params.addAll(params_);
  	   	return res;
    }
    protected String getSqlExprUnit(String fuid, String filterSql, long langId, long userId, long[] baseIds, SrvOrLang orLang, long trId, String numGroupTables, Session session, List<Boolean> paramRespRegs) {
        params.clear();
        sortedFindMap.clear();
        addColName="";
        addColFullTextFind="";
        try {
    		int lang_index = getSystemLangIndex(langId);
            StringBuffer res = new StringBuffer(1024 * 2);
            Matcher matcher;
            filterSql=filterSql.replaceAll("_LANGUAGE","_"+lang_index);
            Set<String> notDelete = new HashSet<String>();
            if (numGroupTables!=null && !"".equals(numGroupTables)) {
            	String[] nums=numGroupTables.split(",");
            	for (String num:nums)
            		if (!notDelete.contains(num))
            			notDelete.add(num);
            }
            res = new StringBuffer(1024 * 2);
            int removeBracs = 0;
            int pos, pos_;
            transformSql(fuid, orLang,baseIds,userId,trId,langId,res,filterSql,notDelete,session, paramRespRegs);
            if(res.length()>0){
                String str = res.toString();
                pos_ = str.lastIndexOf("WHERE");
                //Если есть ссылка на таблицы, то они должны присутствовать в конструкции join
                String str_where = str.substring(pos_);
                matcher = sRe1.matcher(str_where);
                while(matcher.find()) {
                	String t_num = matcher.group(1);
                    if (!notDelete.contains(t_num))
                    	notDelete.add(matcher.group(1));
                }
                //
                matcher = sRe.matcher(str);
                res = new StringBuffer(res.length());
                pos = 0;
                while(matcher.find()) {
                    res.append(str.substring(pos, matcher.start()));
                    pos = matcher.end();
                    if (notDelete.contains(matcher.group(1))) {
                        String par_=matcher.group(2);
                        res.append(par_);
                        if(par_==null || par_.trim().equals(""))res.append(" 1=1");
                    } else {
                        removeBracs += getClosingBraces(matcher.group(2));
//              res.append(getClosingBraces(match.toString(2)));
                        if (pos > pos_) res.append(" 1=1");
                    }
                }
                res.append(str.substring(pos));
                str = res.toString();
                matcher = wRe1.matcher(str);
                res = new StringBuffer(res.length());
                pos = 0;
                boolean mandatory=false;
                while(matcher.find()) {
                    res.append(str.substring(pos, matcher.start()));
                    String param = str.substring(matcher.start()+1,matcher.end()-1);
                    //поверяем обязательный ли атрибут
                    if (param.length() > 0 && param.charAt(param.length()-1) == '+') {
                        param = param.substring(0,param.length()-1);
                        mandatory=true;
                    }
                    if (param.length() > 1 && param.charAt(0) == '%') {
                        // Вставка параметров
                        List<Object> pvals=null;
                        pvals = getFilterParam(fuid, param,true, session);
                        if((pvals==null || pvals.size()==0) && mandatory){
                			throw new DriverException("undefine mandatory param:"+param,
                					ErrorCodes.FLR_ATTR_NOT_FILL);
                        }
                         String param_str=paramsToString(pvals,false) ;
                         res.append(param_str);
                         pos = matcher.end();
                    }else{
                        pos = matcher.start();
                    }
                }
                res.append(str.substring(pos));
                int pos_w = res.lastIndexOf("WHERE");
                int pos_r;
                while ((pos_r = res.indexOf(" 1=1 AND ", pos_w)) > 0
                        || (pos_r = res.indexOf(" AND  1=1", pos_w)) > 0) {
                    res.delete(pos_r, pos_r + 9);
                }
                while ((pos_r = res.indexOf(" 1=1 OR ", pos_w)) > 0
                        || (pos_r = res.indexOf(" OR  1=1", pos_w)) > 0) {
                    res.delete(pos_r, pos_r + 8);
                }
                while ((pos_r = res.indexOf("( 1=1) AND ", pos_w)) > 0
                        || (pos_r = res.indexOf(" AND ( 1=1)", pos_w)) > 0) {
                    res.delete(pos_r, pos_r + 11);
                }
                while ((pos_r = res.indexOf("( 1=1) OR ", pos_w)) > 0
                        || (pos_r = res.indexOf(" OR ( 1=1)", pos_w)) > 0) {
                    res.delete(pos_r, pos_r + 10);
                }
                while ((pos_r = res.indexOf("(( 1=1)) OR ", pos_w)) > 0
                        || (pos_r = res.indexOf(" OR (( 1=1))", pos_w)) > 0) {
                    res.delete(pos_r, pos_r + 12);
                }
//          while((pos_r=res.indexOf("AND (1=1) ",pos_w))>0){
//             res.delete(pos_r,pos_r+10);
//          }
                while ((pos_r = res.indexOf("AND (( 1=1)) ", pos_w)) > 0
                        || (pos_r = res.indexOf(" AND (( 1=1))", pos_w)) > 0) {
                    res.delete(pos_r, pos_r + 13);
                }

                while ((pos_r = res.indexOf("AND ((( 1=1))) ", pos_w)) > 0
                        || (pos_r = res.indexOf(" AND ((( 1=1)))", pos_w)) > 0) {
                    res.delete(pos_r, pos_r + 15);
                }
                while ((pos_r = res.indexOf("AND (((( 1=1)))) ", pos_w)) > 0
                        || (pos_r = res.indexOf(" AND (((( 1=1))))", pos_w)) > 0) {
                    res.delete(pos_r, pos_r + 17);
                }

//          while((pos_r=res.indexOf("OR (1=1) ",pos_w))>0){
//             res.delete(pos_r,pos_r+9);
//          }
                while ((pos_r = res.indexOf("OR (( 1=1)) ", pos_w)) > 0
                        || (pos_r = res.indexOf(" OR (( 1=1))", pos_w)) > 0) {
                    res.delete(pos_r, pos_r + 12);
                }
                int startBrac = res.indexOf("(");
                if (removeBracs > 0)
                    res.delete(startBrac, startBrac + removeBracs);
                return res.toString();
            }
            return filterSql;
        } catch (Exception e) {
            log.error(e, e);
            return "Error:"+e.getMessage();
        }
    }
    
    protected int getSystemLangIndex(long langId) throws DriverException {
    	synchronized(sysLangIndexes) {
    		Map<Long, Integer> map = sysLangIndexes.get(dsName);
    		if (map == null) {
    			map = new HashMap<Long, Integer>();
    			List<KrnObject> sysLangs = getSystemLangs();
    			for(int i = 0; i < sysLangs.size(); i++) {
    				KrnObject lang = sysLangs.get(i);
    				map.put(lang.id, i + 1);
    			}
    			sysLangIndexes.put(dsName, map);
    		}
			if (map.containsKey(langId))
				return map.get(langId);
			else
				return 1;
    	}
	}

    protected void transformSql(String fuid,SrvOrLang orLang,
                                long[] baseIds,long userId,long trId,long langId,
                                StringBuffer res,String bitSql,Set<String> notDelete,Session session, List<Boolean> paramRespRegs) throws DriverException{
        Matcher matcher = wRe.matcher(bitSql);
        int pos=0;
            while (matcher.find()) {
                res.append(bitSql.substring(pos, matcher.start()));
                int beg = matcher.end(1);
                int end = matcher.start(2);
                String prefix = (beg < end) ? bitSql.substring(beg, end) : "";                 
                paramRespRegs.add(prefix.contains("lower("));
                beg = matcher.end(2);
                end = matcher.start(3);
                String suffix = (beg < end) ? bitSql.substring(beg, end) : "";
                String expr = matcher.group(2);
                expr = expr.substring(1, expr.length() - 1);
                String param = expr.trim();
                boolean mandatory=false;
                //поверяем обязательный ли атрибут
                if (param.length() > 0 && param.charAt(param.length()-1) == '+') {
                    param = param.substring(0,param.length()-1);
                    mandatory=true;
                }
               if (param.length()==1 && param.charAt(0) == '%') {
                    res.append(prefix);
                    res.append(suffix.substring(0, getIndSuffix(suffix, notDelete)));
                } else if (param.length() > 1 && param.charAt(0) == '%') {
                    // Вставка параметров
                    List<Object> pvals=null;
                    if(param.charAt(param.length()-1)=='%' && param.indexOf("]")<0){
                        pvals = getFilterParam(fuid, param.substring(0,param.length()-1),false, session);
                    }else if(param.charAt(param.length()-1)=='%' && param.indexOf("[")>1){
                            pvals = getFilterParam(fuid, param.substring(param.indexOf("[")+1,param.length()-1),false, session);
                            if (pvals != null && pvals.size() != 0 )
                            	pvals = getFilterParam(fuid, param,true, session);
                    }else{
                        pvals = getFilterParam(fuid, param,true, session);
                    }
                    if (pvals == null || pvals.size() == 0 || (pvals.size() == 1 && pvals.get(0)==null)) {
                        if(param.indexOf("]")>0 && param.substring(0, param.indexOf("]")).equals(param.substring(param.indexOf("[")+1,param.length()-1)))
                            res.append(" 1>1 ");
                        else{
                                if(mandatory){
                        			throw new DriverException("undefine mandatory param:"+param,
                        					ErrorCodes.FLR_ATTR_NOT_FILL);
                                }
                        	res.append(" 1=1");
                        }
                    }else if (!"".equals(addColFullTextFind) && pvals.get(0).equals(addColFullTextFind)) {
                        notDelete.add(matcher.group(1));
                    	String colName=prefix.substring(0,prefix.indexOf("IN")).trim();
                    	addColFullTextFind = colName+";"+addColFullTextFind;
                    	prefix=colName+" IS NOT NULL";
                    	res.append(prefix);
                        res.append(suffix.substring(0, getIndSuffix(suffix, notDelete)));
                    } else {
                        notDelete.add(matcher.group(1));
                        if (param.length() > 2 && param.charAt(1) == '%') {
                            res.append(getBoolParam(prefix, pvals.get(0)));
                            res.append(suffix.substring(0, getIndSuffix(suffix, notDelete)));
                        } else {

                            int prefix_ind_=0;
                            String prefix_end= prefix.equals("")?"":prefix.substring(prefix.length()-1);
                            if(prefix_end.equals("=") || prefix_end.equals(">") || prefix_end.equals("<")){
                                prefix_ind_=1;
                                prefix_end= prefix.substring(prefix.length()-2);
                                if(prefix_end.equals("=") || prefix_end.equals(">") || prefix_end.equals("<")){
                                    prefix_ind_=2;
                                }
                            }
                            boolean b_par = prefix_end.equals("(");
                            String param_str=paramsToString(pvals,!b_par && prefix_ind_==0).trim() ;
                            if(prefix_ind_>0 && (param_str.substring(0,1).equals("=")
                                    || param_str.substring(0,1).equals(">")
                                    || param_str.substring(0,1).equals("<")))
                                res.append(prefix.substring(0,prefix.length()-prefix_ind_));
                            else
                                res.append(prefix);
                            if(param.charAt(param.length()-1)!='%' || param.indexOf("]")>0)
                                res.append(param_str);
                            
                            // Зависимый параметр с отношением "IN"
                            int beg1 = param.indexOf("]");
                            int end1 = param.indexOf("[", beg1);
                            if (beg1 > 0 && end1 > beg1)
                            	res.append(param.substring(beg1 + 1, end1));
                            
                            res.append(suffix.substring(0, getIndSuffix(suffix, notDelete)));
                        }
                    }
                } else {
                    String f_par = getFuncParam(fuid,orLang, baseIds, param, userId, langId, trId, session);
                    if (!f_par.equals("")) {
                        res.append(prefix);
                        res.append(f_par);
                        res.append(suffix.substring(0, getIndSuffix(suffix, notDelete)));
                    } else{
                       //поверяем обязательный ли атрибут
                       if( mandatory){
                 			throw new DriverException("undefine mandatory param:"+param,
                 					ErrorCodes.FLR_ATTR_NOT_FILL);
                       }
                       res.append(" 1=1");
                        getIndSuffix(suffix, notDelete);
                    }
                }
                pos = matcher.end();
            }
        res.append(bitSql.substring(pos));
    }

    protected int getIndSuffix(String suffix, Set<String> notDelete_) {
        int f_ind = suffix.indexOf("|"), l_ind = suffix.lastIndexOf("|");
        if (f_ind == -1) return suffix.length();
        if (l_ind - f_ind > 1) {
            String heads_ = suffix.substring(f_ind + 1, l_ind);
            StringTokenizer st_ = new StringTokenizer(heads_, ",");
            while (st_.hasMoreElements())
                notDelete_.add((String)st_.nextElement());
        }
        return f_ind;
    }

    protected String getBoolParam(String prefix_, Object pval_) {
        int intVal = (Integer) pval_;
        if (intVal == 0) {
            String[] del_ = {"AND", "<>", "IS NOT NULL"};
            String[] del_m = {"OR", "=", "IS NULL"};
            for (int m = 0; m < del_.length; ++m) {
                while (true) {
                    int inBeg = prefix_.indexOf(del_[m]);
                    if (inBeg < 0) break;
                    prefix_ = prefix_.substring(0, inBeg) + del_m[m] +
                            prefix_.substring(inBeg + del_[m].length(), prefix_.length());
                }
            }
        }
        return prefix_;
    }

    protected String getFuncParam(String fuid,SrvOrLang orLang, long[] baseIds, String param, long userId, long langId, long trId, Session session) {
        String res = "";
        String param_=param;
        if("$".equals(param_.substring(param_.length()-1)))
            param_=param.substring(0,param.length()-1);

        String ost = null;
        int index0 = param_.indexOf("Like%");
        if (index0 > -1) {
        	ost =  param_.substring(index0 + 4);
        	param_ = param_.substring(0, index0);
        }
        
        try {
            if (param.equals("$BASES")) {
                if (baseIds.length > 0) {
//                    res = "'" + getObjectById(baseIds[0]).uid + "'";
                    res = "" +baseIds[0];
                    for (int i = 1; i < baseIds.length; ++i)
//                        res += ",'" + getObjectById(baseIds[i]).uid + "'";
                        res += "," + baseIds[i];
                }
            } else if (param.equals("$BASE")) {
                try {
//                    res = "'" + session.getCurrentDb().uid + "'";
                    res = "" + session.getCurrentDb().id;
                } catch(KrnException e) {
                    log.error(e, e);
                }
            } else if (param.equals("$USER")) {
//                res = "'" + getObjectById(userId).uid + "'";
                res = "" + userId;
            } else if (param.equals("CURTRANS")) {
                res = "" + trId;
            } else {
                Context ctx = new Context(new long[0], 0, 0);
                ctx.trId = trId;
                ctx.langId = langId;
                session.setContext(ctx);
                Map <String,Object> vars = new HashMap<String,Object>();
                Map fparams = session.getFilterParams(fuid);
                if(fparams==null) fparams=new HashMap();
                vars.put("FILTER_PARAMS",fparams);
                vars.put("CURRENT_FILTER",getObjectByUid(fuid, trId));
                try {
                    orLang.evaluate(param_, vars, null, new Stack<String>());
                } catch (Exception e) {
                    log.error(e, e);
                }
                Object ret = vars.get("RETURN");
                if (ret instanceof java.util.Date) {
                    params.add("$" + param);
                    dateParams.put("$" + param_, ret);
                    res = "?";
                } else if (ret instanceof KrnObject) {
//                    res = "'" + ((KrnObject) ret).uid + "'";
                    res = "" + ((KrnObject) ret).id;
//                    params.add(param);
//                    res = "?";
                } else if (ret instanceof List) {
                    StringBuffer r = new StringBuffer();
                    for (Object obj : (List)ret) {
                        if (obj instanceof KrnObject) {
//                            r.append("'" + ((KrnObject) obj).uid + "',");
                            r.append("" + ((KrnObject) obj).id + ",");
//                          r.append("?,");
                        }
                    }
                    if (r.length() > 0)
                        res = r.toString().substring(0, r.length() - 1);
                } else if (ret !=null) {
                    if (ost != null) {
                    	if (ost.endsWith("#"))
                    		ost = ost.substring(0, ost.length() - 1);
                    	
                    	if (ost.startsWith("%%%"))
                    		res = ost.substring(3) + " Like '" + ret.toString() + "%'";
                    	else if (ost.startsWith("%%"))
                    		res = ost.substring(2) + " Like '%" + ret.toString() + "%'";
                    	else if (ost.startsWith("%"))
                    		res = ost.substring(1) + " Like '%" + ret.toString() + "'";
                    }
                    else res=ret.toString();
                }
                
                session.restoreContext();
            }
        } catch (DriverException e) {
            log.error(e, e);
        }
        return res;
    }

    protected int getClosingBraces(String str) {
        char[] chs = str.toCharArray();
        int count = 0;
        for (char ch : chs) {
            if (ch == '(')
                --count;
            else if (ch == ')')
                ++count;
        }
        return count > 0 ? count : 0;
    }

    protected String paramsToString(List pvals,boolean isBrackets) {
        String res = "";
        String val = paramToString(pvals.get(0));
        res += val;
        if (pvals.size() > 1) {
            if (val.indexOf("Like") < 0) {
                for (int i = 1; i < pvals.size(); i++) {
                    res += "," + paramToString(pvals.get(i));
                }
            } else {
                for (int i = 1; i < pvals.size(); i++) {
                    res += " OR " + paramToString(pvals.get(i));
                }
            }
            if(isBrackets)
                res = "(" + res + ")";
        }else if(!pvals.get(0).equals("?") && isBrackets){
            res = "(" + res + ")";
        }
        return res;
    }

    protected String paramToString(Object pval) {
        if (pval instanceof KrnObject) {
//            return "'" + ((KrnObject) pval).uid + "'";
            return "" + ((KrnObject) pval).id;
        } else if (pval != null) {
            return pval.toString();
        }
        return "";
    }

    public List<Object> getFilterParam(String fuid, String paramName,boolean isAddParam, Session s) {
        String paramName_ = paramName;
        if("$".equals(paramName.substring(paramName.length()-1)) 
        		|| "#".equals(paramName.substring(paramName.length()-1))
        		|| "^".equals(paramName.substring(paramName.length()-1)))
        paramName_=paramName.substring(0,paramName.length()-1);
        if("#".equals(paramName_.substring(paramName_.length()-1))){
            String pName=paramName_.substring(0,paramName_.indexOf("|"));
            String pAttr=paramName_.substring(paramName_.lastIndexOf("|")+3,paramName_.length()-1);
            List<Object> pv_ = s.getFilterParams(fuid, pName);
            if(pv_==null || pv_.size()==0 || pv_.get(0) == null) return null;
           	String[] strCols = paramName_.split("\\|");
            addColName=strCols.length>2?strCols[1]:"";
            List<Object> pv=new ArrayList<Object>();
           	String[] strs = pAttr.split("_");
           	if(strs.length>0){
           		addColFullTextFind=strs[0]+";"+(strs.length > 1 ? strs[1]:"")+";"+(String)pv_.get(0);
                   pv.add(addColFullTextFind);
           	}
            if(pv.size()==0)
                pv.add("'-1'");
            return pv;
        }
    	int index= paramName_.length();
        int index0=paramName_.indexOf("]");
        int index_e=paramName_.indexOf(" ESCAPE");
        String e_str=" ESCAPE '~' ";
        if(index_e<0){
        	index_e = index;
        	e_str="";
        }
        if(index0>0) 
            index=index0;
        int index1=paramName_.indexOf("Like%");
        if(index1<0) index1=paramName_.indexOf("LikeNot%");
       if(index1>0)
            index=index1;
        List<Object> pv = s.getFilterParams(fuid, paramName_.substring(0,index));
        if(pv!=null && pv.size()>0){
            Object pv0=pv.get(0);
            if(pv0 instanceof com.cifs.or2.kernel.Date
                    || pv0 instanceof java.util.Date
                    || pv0 instanceof com.cifs.or2.kernel.Time
                    || pv0 instanceof KrnObject
                    || pv0 instanceof String){
                List res=new Vector();
                if(pv.size()==1 && pv0 instanceof String
                        && (((String)pv0).trim().indexOf("=")==0
                        ||((String)pv0).trim().indexOf(">")==0
                        ||paramName.indexOf("%%")==0
                        || ((String)pv0).trim().indexOf("<")==0)) {
                    res.add(pv0);
                }else{
                    if(isAddParam)
                    	if(index0>0)
                            params.add(paramName_.substring(0,index0));
                    	else
                    		params.add(paramName);
                    if(index1<0){
                        for (int i=0; i<pv.size(); i++) res.add("?");
                    }else if(paramName_.indexOf("LikeNot%")>0){
                        int index_c=paramName_.lastIndexOf("%");
                        String colNameOper=paramName_.length()>index_c+1?paramName_.substring(index_c+1,index_e)+" Not Like ":"";
                        for (int i=0; i<pv.size(); i++) res.add(colNameOper+"?"+e_str);
                    }else{
                        int index_c=paramName_.lastIndexOf("%");
                        String colNameOper=paramName_.length()>index_c+1?paramName_.substring(index_c+1,index_e)+" Like ":"";
                        for (int i=0; i<pv.size(); i++) res.add(colNameOper+"?"+e_str);
                    }
                }
                return res;
            }
        }
        return pv;
    }

    public void compileClass(long classId) throws DriverException {
        // ничего не делаем
    }

    public long dbImport(String dir, String separator) throws DriverException {
        String s = "|";
        if (separator != null && !"".equals(separator)) {
            if (separator.length() > 0)
                s = separator;//.charAt(0);
            else
                throw new DriverException("Separator must have one or more char.");
        }
        Setter set = new Setter(this, dir, s);
        set.setChanges();
        log.info("dbImport completed.");
        return set.lastObjectId;
    }

    public long dbImportFromExtDb(String jndiName,int step) throws DriverException {
        Setter set = new Setter(this, jndiName);
        set.setChangesFromExtDb(step);
        log.info("dbImport completed.");
        return set.lastObjectId;
    }
    public void dbExport(String dir, String separator) throws DriverException {
        String s = "|";
        if (separator != null && !"".equals(separator)) {
            if (separator.length() > 0)
                s = separator;//.charAt(0);
            else
                throw new DriverException("Separator must have one or more char.");
        }
        Getter get = new Getter(this, dir);
        get.getChanges(s);
        log.info("dbExport completed.");
    }

    public void loadAttributeValue(KrnAttribute attr, Map values) throws SQLException, DriverException{
        //для загрузки атрибутов, значения которых содержатся в таблицах ct
        String tabName = getClassTableName(attr.classId); 
        String columnName = getColumnName(attr);
        if (values.get(columnName) == null)
            return;
        long objId = Long.parseLong((String) values.get("c_id"));
        PreparedStatement pst = conn.prepareStatement(
            "UPDATE " + tabName + " SET " + columnName + "=? WHERE " + IDColumnName() + "=?"
        );
        long colType = attr.typeClassId;
        if (colType == 4) { //CID_DATE
            long v = Long.parseLong((String) values.get(columnName));
            pst.setDate(1, new java.sql.Date(v));
        } else
        if (colType == 3) { //CID_TIME
            long v = Long.parseLong((String) values.get(columnName));
            pst.setTimestamp(1, new java.sql.Timestamp(v));
        } else
        if (colType == 1 || colType == 6) { //CID_STRING or CID_MEMO
            byte[] b = HexStringOutputStream.fromHexString(
                    (String) values.get(columnName));
            try {
                pst.setString(1, new String(b, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.error(e, e);
            }
        } else
        if(colType == 10) { //CID_BLOB
            byte[] b = HexStringOutputStream.fromHexString(
                    (String) values.get(columnName));
            pst.setBinaryStream(1,
                    new ByteArrayInputStream(b), b.length);
        } else
        if (colType == 8) {
            pst.setDouble(1, Double.parseDouble((String) values.get(columnName)));
        } else
        if (colType == 5) {
            String sVal = (String) values.get(columnName);
            pst.setBoolean(1, sVal.equals("1") ? true : false);
        } else
            pst.setString(1, (String) values.get(columnName));

        pst.setLong(2, objId);

        pst.executeUpdate();
        pst.close();
    }
    
    @Override
    public PreparedStatement getAttrPst(KrnAttribute attr) throws SQLException{
        //для загрузки атрибутов, значения которых содержатся в таблицах at
        String tabName = getAttrTableName(attr);
        String columnNameS = getColumnName(attr) + ",";
        String valueParam = ",?";
        String sql = "";
        if (attr.isMultilingual) {
            if (attr.collectionType == COLLECTION_ARRAY)
                sql = "INSERT INTO " + tabName + "(" +
                    columnNameS + " c_obj_id, c_index, c_lang_id) VALUES(?,?,?" + valueParam + ")";
            else if (attr.collectionType == COLLECTION_SET)
                sql = "INSERT INTO " + tabName + "(" +
                    columnNameS + " c_obj_id, c_lang_id) VALUES(?,?" + valueParam + ")";
            else
                sql = "INSERT INTO " + tabName + "(" +
                    columnNameS + " c_obj_id, c_lang_id) VALUES(?,?" + valueParam + ")";
        } else {
            if (attr.collectionType == COLLECTION_ARRAY) {
                sql = "INSERT INTO " + tabName + "(" +
                    columnNameS + " c_obj_id, c_index) VALUES(?,?" + valueParam +")";
            } else if (attr.collectionType == COLLECTION_SET) {
                sql = "INSERT INTO " + tabName + "(" +
                    columnNameS + " c_obj_id) VALUES(?" + valueParam +")";
            }
        }
        PreparedStatement pst  = conn.prepareStatement(sql);
        return pst;
    }
    public void loadAttributeValue2(PreparedStatement pst,KrnAttribute attr, Map values) throws SQLException, DriverException{
        //для загрузки атрибутов, значения которых содержатся в таблицах at
        String columnName = getColumnName(attr); 
        boolean valueIsNotEmpty = values.get(columnName) != null;
        if (valueIsNotEmpty) {
            long objId = Long.parseLong((String) values.get("c_obj_id"));
            long colType = attr.typeClassId;
            if (colType == 4) { //CID_DATE
                long v = Long.parseLong((String) values.get(columnName));
                pst.setDate(1, new java.sql.Date(v));
            } else
            if (colType == 3) { //CID_TIME
                long v = Long.parseLong((String) values.get(columnName));
                pst.setTimestamp(1, new java.sql.Timestamp(v));
            } else
            if (colType == 1 || colType == 6) { //CID_STRING or CID_MEMO
                byte[] b = HexStringOutputStream.fromHexString(
                        (String) values.get(columnName));
                try {
                    pst.setString(1, new String(b, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    log.error(e, e);
                }
            } else
            if(colType == 10) { //CID_BLOB
                byte[] b = HexStringOutputStream.fromHexString(
                        (String) values.get(columnName));
                pst.setBinaryStream(1,
                        new ByteArrayInputStream(b), b.length);
            } else
            if (colType == 8) {
                pst.setDouble(1, Double.parseDouble((String) values.get(columnName)));
            } else
            if (colType == 5) {
                String sVal = (String) values.get(columnName);
                pst.setBoolean(1, sVal.equals("1") ? true : false);
            } else
                pst.setString(1, (String) values.get(columnName));

            pst.setLong(2, objId);

            if (attr.collectionType == COLLECTION_ARRAY) {
                int index = Integer.parseInt((String) values.get("c_index"));
                pst.setInt(3, index);
            }
            if (attr.isMultilingual) {
                long langId = Long.parseLong((String) values.get("c_lang_id"));
                if (attr.collectionType == COLLECTION_ARRAY) {
                    pst.setLong(4, langId);
                } else {
                    pst.setLong(3, langId);
                }
            }
            pst.executeUpdate();
        }
    }
    
    @Override
    public boolean lockRecord(KrnClass cls, long objId, int timeout)  throws SQLException {
        String sql="UPDATE " + getDBPrefix() + "ct99 SET c_uid = c_uid WHERE c_obj_id = ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        if (timeout == 0) timeout = 1; // 1 second
    	pst.setQueryTimeout(timeout);
    	pst.setLong(1, objId);
        int res = -1;
        try {
        	log.info(">>>>>>>>>>>>>>>>>>" + objId);
            res = pst.executeUpdate();
            log.info("<<<<<<<<<<<<<<<<<<<<<<" + objId);
        } catch (SQLException e) {
            log.error(e, e);
        } finally {
            DbUtils.closeQuietly(pst);
        }
       	return res > 0;
    }
    public boolean updateAttrFromExtDb(String jndiName,long attrId, String objUids) {
    	Connection extConn=db.getExternalConnection(jndiName);
    	if(extConn==null) return false;
    	KrnAttribute attr=db.getAttributeById(attrId);
    	String tName="";
    	String aName=getColumnName(attr);
    	if(attr.collectionType==0)
    		tName=getClassTableName(attr.classId, false);
    	else
    		tName=getAttrTableName(attr, false);
    	
    	String selSqlExt="select c_uid,"+aName+" from "+tName+" where c_uid in ("+objUids+")";
    	String updSql="UPDATE "+tName+" SET "+aName+" = ? where c_uid= ?";
    	try (PreparedStatement pstSelExt=extConn.prepareStatement(selSqlExt);
    			PreparedStatement pstUpd=conn.prepareStatement(updSql);
	    		ResultSet rsExt=pstSelExt.executeQuery()){
    		while(rsExt.next()) {
    			Object value=null;
    			if(attr.typeClassId==PC_BLOB) 
    				value=rsExt.getBytes(2);
    			else if(attr.typeClassId == PC_MEMO)
    				value=getClobToText(rsExt,aName);
	   			else if(attr.typeClassId == PC_DATE)
	   				value= rsExt.getDate(2);
	   			else if(attr.typeClassId == PC_TIME)
	   				value= rsExt.getTimestamp(2);
    			else
    				value=rsExt.getObject(2);
    			if (attr.typeClassId == 0)
    				pstUpd.setString(1, (String)value);
    			else
    				setValue(pstUpd, 1, attr.typeClassId, value);
    			
    			pstUpd.setString(2, rsExt.getString(1));
    			int res=pstUpd.executeUpdate();
    		}
			conn.commit();
			return true;
    	} catch (SQLException e) {
        	log.info("Ошибка при доступе к таблице " + tName);
            log.error(e, e);
		}
    	
    	return false;
    }
    public List runSql(String sql, boolean isUpdate) throws SQLException {
    	return runSql(sql, isUpdate, false);
    }
    
    public List runSql(String sql, boolean isUpdate, boolean isReturnException) throws SQLException {
        Statement st = conn.createStatement();
        ResultSet rs = null;
        List res = new ArrayList();
        try {
            if (isUpdate) {
                st.executeUpdate(sql);
            } else {
                rs = st.executeQuery(sql);
                while(rs.next()) {
                    ResultSetMetaData md = rs.getMetaData();
                    int count = md.getColumnCount();
                    if (count == 1) {
                        res.add(rs.getObject(1));
                    } else {
                        List t = new ArrayList();
                        for (int i = 0; i < count; i++) {
                            t.add(rs.getObject(i + 1));
                        }
                        res.add(t);
                    }
                }
            }
        } catch (SQLException e) {
            log.error(e, e);
			if (isReturnException) {
				throw e;
			}
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(st);
        }
        return res;
    }

    public Connection getConnection(){
        return conn;
    }


    protected void dbPreInit() {
        // Создаем главный класс Объект
    	try {
            createClass("Объект", -1, false, 0, 99, "99", false, null);
	    } catch (DriverException e) {
            log.error(e, e);
	    }
    }
    
    protected long dbInit() {
        long res = -1;
        try {
        	log.info("Запущена инициализация Базы Данных!");
            // Инициализируем таблицы классов и атрибутов
            createClass("Системный класс", 99, false, 1, 100, "100", "");
            createClass("Пользовательский класс", 99, false, 1, 101, "101", "");
            createClass("string", 100, false, 1, 1, "1", "");
            createClass("long", 100, false, 1, 2, "2", "");
            createClass("time", 100, false, 1, 3, "3", "");
            createClass("date", 100, false, 1, 4, "4", "");
            createClass("boolean", 100, false, 1, 5, "5", "");
            createClass("memo", 100, false, 1, 6, "6", "");
            createClass("float", 100, false, 1, 8, "8", "");
            createClass("blob", 100, false, 1, CID_BLOB, String.valueOf(CID_BLOB), "");
            createClass("bfile", 100, false, 1, CID_BFILE, String.valueOf(CID_BFILE), null);
            
            KrnClass baseStructCls = createClass("Структура баз", 100, true, 145, "145", "");
            createAttribute(1, "1", 99, CID_INTEGER, "creating", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(2, "2", 99, CID_INTEGER, "deleting", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);

            // В первую очередь создаем справочник языков
            // так как он необходим для создания мультиязычных атрибутов
            createClass("Language", 100, true, 147, "147", "");
            KrnAttribute lCode = createAttribute(-1, null, 147, CID_STRING, "code", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            KrnAttribute lSys = createAttribute(-1, null, 147, CID_BOOL, "system?", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            KrnAttribute lLang = createAttribute(-1, null, 147, CID_BOOL, "lang?", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            KrnAttribute lName = createAttribute(-1, null, 147, CID_STRING, "name", 0, true, false, false, true, 255, 0, 0, 0, false, null, 0);
            // Русский
            KrnObject obj_ru = createObject(147, 0, -1, null);
            setValue(obj_ru, lCode.id, 0, 0, 0, "RU", false);
            setValue(obj_ru, lLang.id, 0, 0, 0, 1, false);
            setValue(obj_ru, lSys.id, 0, 0, 0, 1, false);
            setValue(obj_ru, lName.id, 0, 0, 0, "Русский", false);
            // Казахский
            KrnObject obj = createObject(147, 0, -1, null);
            setValue(obj, lCode.id, 0, 0, 0, "KZ", false);
            setValue(obj, lLang.id, 0, 0, 0, 1, false);
            setValue(obj, lSys.id, 0, 0, 0, 1, false);
            setValue(obj, lName.id, 0, 0, 0, "Казахский", false);


            
            createClass("EventInitiator", 103, true, 104, "104", "");
            KrnAttribute eCod = createAttribute(-1, null, 104, CID_STRING, "kod", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            KrnAttribute eName = createAttribute(-1, null, 104, CID_STRING, "name", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            
            createClass("EventType", 103, true, 105, "105", "");
            KrnAttribute etCode = createAttribute(-1, null, 105, CID_INTEGER, "code", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            KrnAttribute etType = createAttribute(-1, null, 105, CID_STRING, "type", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            KrnAttribute etTypem = createAttribute(-1, null, 105, CID_STRING, "type_M", 0, false, false, true, true, 255, 0, 0, 0, false, null, 0);
            
            createClass("EventSpr", 103, true, 106, "106", "");
            createAttribute(-1, null, 106, CID_STRING, "description", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 106, CID_STRING, "description_M", 0, false, false, true, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 106, 104, "initiator", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 106, CID_STRING, "kod", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 106, 105, "type", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);

            createClass("Filter", 100, true, 107, "107", "");
            createAttribute(-1, null, 107, CID_STRING, "className", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 107, CID_INTEGER, "dateSelect", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 107, CID_BLOB, "exprSql", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            KrnAttribute fTitle = createAttribute(-1, null, 107, CID_STRING, "title", 0, false, false, true, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 107, CID_BLOB, "config", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);

            createClass("FilterFolder", 107, true, 108, "108", "");
            KrnAttribute flPa = createAttribute(-1, null, 107, 108, "parent", COLLECTION_NONE, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 108, 107, "children", COLLECTION_SET, false, false, false, true, 0, 0, flPa.id, 0, false, null, 0);
            createClass("FilterRoot", 108, true, 109, "109", "");

            createClass("GuiComponent", 100, true, 110, "110", "");
            createAttribute(-1, null, 110, CID_MEMO, "constraints", COLLECTION_ARRAY, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 110, CID_MEMO, "descInfo", 0, false, false, true, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 110, CID_INTEGER, "flags", COLLECTION_ARRAY, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 110, CID_STRING, "ref", COLLECTION_ARRAY, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            KrnAttribute gTitle = createAttribute(-1, null, 110, CID_STRING, "title", 0, false, false, true, true, 255, 0, 0, 0, false, null, 0);

            createClass("UI", 100, true, 120, "120", "");
            createAttribute(-1, null, 120, CID_BLOB, "config", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 120, CID_BLOB, "strings", 0, false, false, true, true, 0, 0, 0, 0, false, null, 0);
            KrnAttribute uiTitle = createAttribute(-1, null, 120, CID_STRING, "title", 0, false, false, true, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 120, CID_BOOL, "webConfigChanged", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 120, CID_BLOB, "webConfig", 0, false, false, true, true, 0, 0, 0, 0, false, null, 0);

            createClass("HiperTree", 110, true, 111, "111", "");
            createAttribute(-1, null, 111, CID_INTEGER, "access", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 111, 120, "hiperObj", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 111, CID_BOOL, "isChangeable", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 111, CID_BOOL, "isDialog", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 111, CID_BOOL, "isHeader", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 111, CID_INTEGER, "runtimeIndex", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);

            createClass("HiperFolder", 111, true, 112, "112", "");
            KrnAttribute htPA = createAttribute(-1, null, 111, 112, "parent", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            KrnAttribute hHs = createAttribute(-1, null, 112, 111, "hipers", COLLECTION_SET, false, true, false, true, 0, 0, htPA.id, 0, false, null, 0);

            createClass("MainTree", 112, true, 113, "113", "");

            createClass("ReportPrinter", 110, true, 114, "114", "");
            createAttribute(-1, null, 114, CID_BLOB, "data", 0, false, false, true, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 114, CID_BLOB, "data2", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 114, CID_BLOB, "template", 0, false, false, true, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 114, CID_BLOB, "template2", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 114, 114, "базовый отчет", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 114, CID_BLOB, "config", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 114, baseStructCls.id, "bases", COLLECTION_SET, false, false, false, true, 0, 0, 0, 0, false, null, 0);

            createClass("ReportFolder", 114, true, 115, "115", "");
            KrnAttribute rpPA = createAttribute(-1, null, 114, 115, "parent", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 115, 114, "children", COLLECTION_SET, false, false, false, true, 0, 0, rpPA.id, 0, false, null, 0);
            
            createClass("ReportRoot", 115, true, 116, "116", "");
            
            createClass("ImpExp", 100, true, 117, "117", "");
            createAttribute(-1, null, 117, CID_INTEGER, "change_id", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 117, CID_INTEGER, "clschange_id", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 117, CID_DATE, "date", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 117, CID_INTEGER, "exp_id", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 117, CID_STRING, "file_name", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 117, CID_INTEGER, "prior_change_id", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 117, CID_INTEGER, "prior_clschange_id", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 117, CID_INTEGER, "rised", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 117, CID_MEMO, "scriptOnAfterAction", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 117, CID_MEMO, "scriptOnBeforeAction", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            
            createClass("Import", 117, true, 118, "118", "");
            createAttribute(-1, null, 118, 4, "exp_date", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 118, CID_TIME, "importFinish", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 118, CID_TIME, "importStart", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            
            createClass("Export", 117, true, 119, "119", "");
            createAttribute(-1, null, 119, CID_INTEGER, "change_count", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 119, CID_INTEGER, "clschange_count", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);

            createClass("UIFolder", 120, true, 121, "121", "");
            KrnAttribute uiPA = createAttribute(-1, null, 120, 121, "parent", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 121, 120, "children", COLLECTION_SET, false, false, false, true, 0, 0, uiPA.id, 0, false, null, 0);

            createClass("Property", 100, false, 163, "163", "");
            KrnAttribute attr = createAttribute(-1, null, 163, CID_STRING, "name", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Название свойства.");
            attr = createAttribute(-1, null, 163, CID_STRING, "value", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Значение свойства.");

            createClass("ConfigObject", 159, false, 162, "162", "");
            attr = createAttribute(-1, null, 162, CID_STRING, "uuid", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Идентификатор объекта.");
            attr = createAttribute(-1, null, 162, 163, "properties", COLLECTION_SET, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Набор свойств для определённого объекта");

            createClass("Config", 100, false, 159, "159", "");
            // глобальная конфигурация интерфейса
            createClass(NAME_CLASS_CONFIG_GLOBAL, 159, false, 160, "160", "");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_COLOR_BACK_TAB_TITLE, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Цвет фона заголовка фоновых вкладок.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_COLOR_FONT_BACK_TAB_TITLE, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Цвет шрифта заголовка фоновых вкладок.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_COLOR_FONT_TAB_TITLE, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Цвет шрифта заголовка выбранной вкладки.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_COLOR_HEADER_TABLE, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Цвет заголовков таблиц и деревьев.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_COLOR_MAIN, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Цвет определяющий основную цветовую гамму интерфейсов.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_COLOR_TAB_TITLE, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Цвет фона заголовка выбранной вкладки.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_GRADIENT_CONTROL_PANEL, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Параметры градиентной заливки для панели управления системы.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_GRADIENT_FIELD_NO_FLC, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Параметры градиентной заливки для полей не прошедших ФЛК.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_GRADIENT_MAIN_FRAME, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Параметры градиентной заливки для главного фрейма системы.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_GRADIENT_MENU_PANEL, 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Параметры градиентной заливки для панели меню системы.");
            attr = createAttribute(-1, null, 160, CID_INTEGER, ATTR_TRANSPARENT_BACK_TAB_TITLE, 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Прозрачность заголовка фоновой вкладки");
            attr = createAttribute(-1, null, 160, CID_INTEGER, ATTR_TRANSPARENT_CELL_TABLE, 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Прозрачность ячеек таблиц и деревьев.");
            attr = createAttribute(-1, null, 160, CID_INTEGER, ATTR_TRANSPARENT_MAIN, 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Настройка прозрачности панелей системы.");
            attr = createAttribute(-1, null, 160, CID_INTEGER, ATTR_TRANSPARENT_DIALOG, 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Настройка прозрачности диалогов системы.");
            attr = createAttribute(-1, null, 160, CID_INTEGER, ATTR_TRANSPARENT_SELECTED_TAB_TITLE, 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Прозрачность фона заголовка выбранной вкладки.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_BLUE_SYS_COLOR, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Связан с системной переменной blueSysColor.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_DARK_SHADOW_SYS_COLOR, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Связан с системной переменной darkShadowSysColor.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_MID_SYS_COLOR, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Связан с системной переменной midSysColor.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_LIGHT_YELLOW_COLOR, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Связан с системной переменной lightYellowColor.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_RED_COLOR, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Связан с системной переменной redColor.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_LIGHT_RED_COLOR, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Связан с системной переменной lightRedColor.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_LIGHT_GREEN_COLOR, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Связан с системной переменной lightGreenColor.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_SHADOW_YELLOW_COLOR, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Связан с системной переменной shadowYellowColor.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_SYS_COLOR, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Связан с системной переменной sysColor.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_LIGHT_SYS_COLOR, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Связан с системной переменной lightSysColor.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_DEFAULT_FONT_COLOR, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Связан с системной переменной defaultFontColor.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_SILVER_COLOR, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Связан с системной переменной silverColor.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_SHADOWS_GREY_COLOR, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Связан с системной переменной shadowsGreyColor.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_KEYWORD_COLOR, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Связан с системной переменной keywordColor.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_VARIABLE_COLOR, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Связан с системной переменной variableColor.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_CLIENT_VARIABLE_COLOR, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Связан с системной переменной clientVariableColor.");
            attr = createAttribute(-1, null, 160, CID_STRING, ATTR_COMMENT_COLOR, 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Связан с системной переменной commentColor.");
            attr = createAttribute(-1, null, 160, CID_INTEGER, ATTR_OBJECT_BROWSER_LIMIT, 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Лимит количества объектов класса для отображения");
            attr = createAttribute(-1, null, 160, 163, ATTR_OBJECT_BROWSER_LIMIT_FOR_CLASSES, COLLECTION_SET, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Набор лимитов количества объектов для отображения. Задаёт индивидуальные, для каждого класса, лимиты.");
            attr = createAttribute(-1, null, 160, CID_BOOL, ATTR_IS_OBJECT_BROWSER_LIMIT, 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Активирует лимитированное отображение классов.");
            attr = createAttribute(-1, null, 160, CID_BOOL, ATTR_IS_OBJECT_BROWSER_LIMIT_FOR_CLASSES, 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Активирует индивидуальное лимитированное отображение классов.");
            
            createClass(Constants.NAME_CLASS_CONFIG_LOCAL, 160, false, 161, "161", "");
            attr = createAttribute(-1, null, 161, CID_INTEGER, "maxObjectCount", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Количество объектов, отображаемых в инспекторе классов.");
            attr = createAttribute(-1, null, 161, CID_INTEGER, "isToolBar", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Флаг отображения панели инструментов в WEB интерфейсе.");
            attr = createAttribute(-1, null, 161, CID_INTEGER, "isMonitor", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Флаг отображения монитора задач.");
            attr = createAttribute(-1, null, 161, 162, "configByUUIDs", COLLECTION_SET, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Конфигурация объектов системы (набор по идентификаторам)");
            attr = createAttribute(-1, null, 161, 120, ATTR_HISTORY_IFC, COLLECTION_SET, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "История просмотра интерфейсов.");
            attr = createAttribute(-1, null, 161, 107, ATTR_HISTORY_FLT, COLLECTION_SET, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "История просмотра фильтров.");
            attr = createAttribute(-1, null, 161, 114, ATTR_HISTORY_RPT, COLLECTION_SET, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "История просмотра отчётов.");
            
            createClass("UIRoot", 121, true, 122, "122", "");
            createClass("User", 100, false, 123, "123", "");
            createAttribute(-1, null, 111, 123, "users", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            KrnAttribute uAdm = createAttribute(-1, null, 123, CID_BOOL, "admin", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 120, 123, "developer", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 107, 123, "developer", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 123, CID_BOOL, "blocked", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 123, CID_BOOL, "developer", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 123, CID_BOOL, "multi", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            KrnAttribute uEdit = createAttribute(-1, null, 123, CID_BOOL, "editor", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 123, CID_STRING, "doljnost", 0, false, false, true, true, 255, 0, 0, 0, false, null, 0);
            KrnAttribute u_name = createAttribute(-1, null, 123, CID_STRING, "name", 0, true, false, false, true, 255, 0, 0, 0, false, null, 0);
            KrnAttribute u_pd = createAttribute(-1, null, 123, CID_STRING, "password", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            KrnAttribute uSign = createAttribute(-1, null, 123, CID_STRING, "sign", 0, false, false, true, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 123, CID_STRING, "email", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 123, CID_STRING, "iin", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 123, CID_BOOL, "onlyECP", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            attr = createAttribute(-1, null, 123, CID_MEMO, "previous passwords", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Хэши предыдущих паролей.");
            attr = createAttribute(-1, null, 123, CID_INTEGER, "кол неуд авторизаций", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Разрешённое количество ошибочных авторизаций.");
            attr = createAttribute(-1, null, 123, CID_TIME, "дата изменения пароля", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Дата изменения пароля.");
            attr = createAttribute(-1, null, 123, CID_TIME, "время блокировки", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Время блокировки пользователя.");
            attr = createAttribute(-1, null, 123, CID_DATE, "дата истечения срока действия пароля", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Дата истечения срока действия пароля.");
            attr = createAttribute(-1, null, 123, CID_BOOL, "isFolder", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Пользовательская роль.");
            KrnAttribute uNL = createAttribute(-1, null, 123, CID_BOOL, "isLogged", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(uNL.uid, "Признак входа пользователя в систему.");
            createAttribute(-1, null, 123, 111, "hyperMenu", COLLECTION_SET, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 123, 120, "interface", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            KrnAttribute uLLT = createAttribute(-1, null, 123, CID_TIME, "lastLoginTime", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(uLLT.uid, "Время последней авторизации пользователя.");
            KrnAttribute udc = createAttribute(-1, null, 123, CID_TIME, "activated", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(udc.uid, "Время активации пользователя (создание и разблокировка).");
            createAttribute(-1, null, 123, CID_BLOB, "quickList", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            attr = createAttribute(-1, null, 123, 161, "config", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Пользовательская конфигурация.");
            createAttribute(-1, null, 123, CID_INTEGER, "favoritesClasses", COLLECTION_ARRAY, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 123, CID_STRING, "lastIndexingConfig", COLLECTION_ARRAY, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 123, CID_STRING, "templates", COLLECTION_ARRAY, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createClass("UserFolder", 123, true, 124, "124", "");
            KrnAttribute uParent = createAttribute(-1, null, 123, 124, "parent", COLLECTION_SET, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            KrnAttribute uChildren = createAttribute(-1, null, 124, 123, "children", COLLECTION_SET, false, false, false, true, 0, 0, uParent.id, 0, false, null, 0);
            createAttribute(-1, null, 124, CID_BLOB, "or3rights", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
           
            createClass("UserRoot", 124, true, 125, "125", "");
           
            createClass("Политика учетных записей", 100, false, 126, "126", "");
            createAttribute(-1, null, 126, CID_INTEGER, "рекомен срок действия пароля", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_INTEGER, "мин длина логина", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_INTEGER, "мин длина пароля", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_INTEGER, "мин длина пароля адм", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_INTEGER, "кол не дублир паролей", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_INTEGER, "кол не дублир паролей адм", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_BOOL, "использовать цифры", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_BOOL, "использовать буквы", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_BOOL, "использовать регистр", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_BOOL, "использовать спец символы", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_BOOL, "запрет имён", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_BOOL, "запрет фамилий", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_BOOL, "запрет телефонов", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_BOOL, "запрет слов", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_INTEGER, "макс срок действия пароля", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_INTEGER, "мин срок действия пароля", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_INTEGER, "кол неуд авторизаций", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_INTEGER, "время блокировки", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_BOOL, "блокировать логин в пароле", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_INTEGER, "макс длина пароля", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_INTEGER, "макс длина логина", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_BOOL, "смена 1го пароля", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_INTEGER, "макс срок 1го пароля", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_BOOL, "запрет повтора 1х 3х букв пароля", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            // Перенос апгрейда 38
            createAttribute(-1, null, 126, CID_BOOL, "не должно явно преобладать цифры", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_BOOL, "запрет повтора в любом месте из более 2-х одинаковых символов пароля", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 126, CID_BOOL, "запрет слов на клавиатуре", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);

            createClass("Timer", 100, false, 127, "127", "");
            createAttribute(-1, null, 127, CID_BLOB, "config", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 127, CID_BLOB, "protocol", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 127, CID_BOOL, "redy", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 127, CID_TIME, "start", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            KrnAttribute t_title = createAttribute(-1, null, 127, 1, "title", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 127, 123, "user", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);

            createClass("TimerFolder", 127, true, 128, "128", "");
            KrnAttribute tmPA = createAttribute(-1, null, 127, 128, "parent", COLLECTION_NONE, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 128, 127, "children", COLLECTION_SET, false, false, false, true, 0, 0, tmPA.id, 0, false, null, 0);
            
            createClass("TimerRoot", 128, true, 129, "129", "");

            createClass("TimerProtocol", 100, false, 170, "170", "");
            createAttribute(-1, null, 170, CID_STRING, "err", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 170, CID_BOOL, "status", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 170, CID_TIME, "timeNextStart", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 170, CID_TIME, "timeStart", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 170, 127, "timer", 0, false, true, false, false, 0, 0, 0, 0, false, null, 0);
            
            createClass("Note", 100, true, 130, "130", "");
            createAttribute(-1, null, 123, 130, "help", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 123, 130, "helps", COLLECTION_ARRAY, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 130, CID_BLOB, "content", 0, false, false, true, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 130, CID_STRING, "title", 0, false, false, true, true, 255, 0, 0, 0, false, null, 0);
            
            createClass("NoteFolder", 130, true, 131, "131", "");
            KrnAttribute ntPA = createAttribute(-1, null, 130, 131, "parent", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 131, 130, "children", COLLECTION_SET, false, false, false, true, 0, 0, ntPA.id, 0, false, null, 0);
            
            createClass("NoteRoot", 131, true, 132, "132", "");
           
            createClass("WorkFlow", 100, false, 1, 133, "133", "");

            createClass("BoxExchange", 133, true, 134, "134", "");
            createAttribute(-1, null, 134, CID_STRING, "charSet", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            KrnAttribute boxName = createAttribute(-1, null, 134, 1, "name", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 134, CID_STRING, "urlIn", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 134, CID_STRING, "urlOut", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 134, CID_STRING, "xpathIn", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 134, CID_STRING, "xpathOut", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 134, CID_STRING, "xpathTypeIn", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 134, CID_STRING, "xpathTypeOut", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 134, CID_STRING, "xpathIdInit", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 134, CID_BOOL, "isRestrict", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 134, CID_INTEGER, "transport", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 134, CID_INTEGER, "typeMsg", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 134, CID_BLOB, "config", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);

            createClass("BoxFolder", 134, true, 135, "135", "");
            KrnAttribute bxPA = createAttribute(-1, null, 134, 135, "parent", COLLECTION_NONE, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 135, 134, "children", COLLECTION_SET, false, true, false, true, 0, 0, bxPA.id, 0, false, null, 0);
            
            createClass("BoxRoot", 135, true, 136, "136", "");
           
            createClass("Flow", 133, false, 137, "137", "");
            createAttribute(-1, null, 137, 123, "actor", 0, false, true, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_BLOB, "article", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, 147, "article_lang", 0, false, true, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, 134, "box", 0, false, true, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, 137, "children", COLLECTION_ARRAY, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_DATE, "control", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_STRING, "corelId", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_TIME, "current", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_MEMO, "cutObj",0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_BLOB, "debug", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_TIME, "end", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_STRING, "event", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_BLOB, "interfaceVars", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_INTEGER, "lockObjects", COLLECTION_ARRAY, false, false, true, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_STRING, "name", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_STRING, "node", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, 137, "parentFlow", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_BOOL, "parentReactivate", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_INTEGER, "permit", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_TIME, "start", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_STRING, "status", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_STRING, "syncNode", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_STRING, "title", 0, false, false, true, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_STRING, "titleObj", 0, false, false, true, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_INTEGER, "transId", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_STRING, "transition", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_STRING, "typeNode", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_STRING, "typeUi", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_STRING, "ui", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, 123, "user", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, CID_BLOB, "variables", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            
            createClass("Process", 133, false, 138, "138", "");
            createAttribute(-1, null, 138, CID_TIME, "end", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 138, 123, "initiator", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 138, CID_BOOL, "isProcess", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 138, 123, "killer", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 138, CID_STRING, "observers", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 137, 138, "processInstance", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 138, 137, "rootFlow", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 138, CID_TIME, "start", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 138, 137, "superFlow", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 138, CID_INTEGER, "transId", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 138, CID_STRING, "typeUiObservers", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 138, CID_STRING, "uiObservers", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 138, CID_BLOB, "variables", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);

            createClass("ProcessDef", 133, true, 139, "139", "");
            createAttribute(-1, null, 123, 139, "process", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 127, 139, "process", COLLECTION_ARRAY, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 138, 139, "processDefinition", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 139, CID_BLOB, "config", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 139, 123, "developer", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 139, CID_BLOB, "diagram", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 138, CID_BOOL, "isFolder", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 139, CID_BLOB, "message", 0, false, false, true, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 139, CID_BLOB, "strings", 0, false, false, true, true, 0, 0, 0, 0, false, null, 0);
            attr = createAttribute(-1, null, 139, CID_INTEGER, "isBtnToolBar", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Для процесса задана кнопка на панели задач.");

            createAttribute(-1, null, 139, CID_BLOB, "icon", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 139, CID_STRING, "hotKey", 0, false, false, true, true, 255, 0, 0, 0, false, null, 0);
            
            attr = createAttribute(-1, null, 161, 139, ATTR_HISTORY_SRV, COLLECTION_SET, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "История просмотра процессов.");

            KrnAttribute p_title = createAttribute(-1, null, 139, 1, "title", 0, false, false, true, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 139, 2, "runtimeIndex", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            
            createClass("ProcessDefFolder", 139, true, 140, "140", "");
            KrnAttribute pdPA = createAttribute(-1, null, 139, 140, "parent", COLLECTION_NONE, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 140, 139, "children", COLLECTION_SET, false, true, false, true, 0, 0, pdPA.id, 0, false, null, 0);
            createAttribute(-1, null, 140, CID_BOOL, "isTab", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 140, CID_STRING, "tabName", 0, false, false, true, true, 255, 0, 0, 0, false, null, 0);
            
            createClass("ProcessDefRoot", 140, true, 141, "141", "");
            
            createClass("Mail@Message", 100, false, 142, "142", "");
            createAttribute(-1, null, 142, CID_STRING, "bcc", COLLECTION_SET, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 142, CID_STRING, "cc", COLLECTION_SET, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 142, CID_STRING, "from", COLLECTION_SET, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 142, CID_STRING, "headers", COLLECTION_SET, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 142, CID_STRING, "id", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 142, CID_DATE, "receivedDate", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 142, CID_DATE, "sentDate", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 142, CID_STRING, "subject", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 142, CID_STRING, "to", COLLECTION_SET, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            
            createClass("Mail@Part", 100, false, 143, "143", "");
            createAttribute(-1, null, 142, 143, "parts", COLLECTION_SET, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 143, CID_STRING, "charSet", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 143, CID_BLOB, "content", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 143, CID_STRING, "description", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 143, CID_STRING, "disposition", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 143, CID_STRING, "fields", COLLECTION_SET, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 143, CID_STRING, "fileName", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 143, CID_STRING, "headers", COLLECTION_SET, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 143, CID_STRING, "mimeType", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 143, CID_STRING, "subType", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            
            createClass("База", 100, true, 144, "144", "");
            createAttribute(-1, null, 144, CID_STRING, "код", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            KrnAttribute bName = createAttribute(-1, null, 144, 1, "наименование", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 118, 145, "from_database", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 118, 145, "база", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 119, 145, "to_database", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 119, 145, "база", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            KrnAttribute uBase = createAttribute(-1, null, 123, 145, "base", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 134, 145, "base", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 145, 118, "imports", COLLECTION_ARRAY, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 145, 119, "exports", COLLECTION_ARRAY, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            KrnAttribute sbFlags = createAttribute(-1, null, 145, CID_INTEGER, "flags", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 145, CID_STRING, "mail", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            KrnAttribute sbP = createAttribute(-1, null, 145, 145, "родитель", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 145, 145, "дети", COLLECTION_SET, false, true, false, true, 0, 0, sbP.id, 0, false, null, 0);
            KrnAttribute sbZnach = createAttribute(-1, null, 145, 144, "значение", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 145, CID_STRING, "код", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            KrnAttribute sbName = createAttribute(-1, null, 145, CID_STRING, "наименование", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            KrnAttribute sbLevel = createAttribute(-1, null, 145, CID_INTEGER, "уровень", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            KrnAttribute sbPhys = createAttribute(-1, null, 145, CID_BOOL, "физически раздельная?", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            
            createClass("Корень структуры баз", 145, true, 146, "146", "");
            KrnAttribute u_dl = createAttribute(-1, null, 123, 147, "data language", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            KrnAttribute u_il = createAttribute(-1, null, 123, 147, "interface language", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            
            createClass("MSDoc", 100, false, 148, "148", "");
            createAttribute(-1, null, 148, CID_BLOB, "file", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 148, CID_STRING, "filename", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            
            createClass("ReplCollection", 100, false, 149, "149", "");
            createAttribute(-1, null, 149, CID_DATE, "date", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 149, CID_INTEGER, "replicationID", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 149, CID_INTEGER, "runMode", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 149, CID_INTEGER, "type", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            
            createClass("Зап табл репликации", 100, false, 150, "150", "");
            createAttribute(-1, null, 117, 150, "зап табл репликации", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 149, 150, "зап табл репликации", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 150, 145, "database", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 150, CID_DATE, "date", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 150, 117, "entity", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 150, CID_TIME, "error message", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 150, CID_TIME, "fileName", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 150, CID_INTEGER, "logType", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 150, 149, "replObject", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 150, CID_INTEGER, "status", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 150, CID_INTEGER, "uniqId", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            
            createClass("MenuItemsDesc", 100, true, 151, "151", "");
            createAttribute(-1, null, 151, CID_BLOB, "itemDesc", 0, false, false, true, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 151, CID_STRING, "name", 0, true, false, false, true, 255, 0, 0, 0, false, null, 0);
            
            createClass("Тип сообщения", 100, true, 152, "152", "");
            createAttribute(-1, null, 152, CID_STRING, "код", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 152, CID_STRING, "наименование", 0, true, false, false, true, 255, 0, 0, 0, false, null, 0);
            
            createClass("OrLang", 100, true, 153, "153", "");
            KrnAttribute olFuncs = createAttribute(-1, null, 153, CID_BLOB, "funcs", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            KrnAttribute olVars = createAttribute(-1, null, 153, CID_BLOB, "vars", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);

            createClass("Func", 100, true, 154, "154", "");
            createAttribute(-1, null, 154, CID_STRING, "name", 0, false, false, true, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 154, CID_BLOB, "strings", 0, false, false, true, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 154, CID_BLOB, "text", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            
            createClass("FuncFolder", 154, true, 155, "155", "");
            createAttribute(-1, null, 155, 154, "children", COLLECTION_SET, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 154, 155, "parent", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            
            createClass("CreateXmlRoot", 155, true, 156, "156", "");
            
            createClass("DefaultXmlRoot", 155, true, 157, "157", "");
            
            createClass("ParseXmlRoot", 155, true, 158, "158", "");

            createClass("ChatClass", 100, false, 164, "164", "");
            createAttribute(-1, null, 164, CID_STRING, "from", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 164, CID_STRING, "to", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 164, CID_STRING, "canDeleteFrom", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 164, CID_STRING, "canDeleteTo", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 164, CID_STRING, "text", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 164, CID_TIME, "datetime", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);

            createClass("Action", 100, false, 165, "165", "");
//            createAttribute(-1, null, 165, CID_STRING, "action", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 165, CID_TIME, "editingDate", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 165, CID_INTEGER, "id", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 165, CID_MEMO, "log", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 165, CID_STRING, "name", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 165, CID_STRING, "type", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 165, CID_STRING, "user", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);

            createClass(Constants.NAME_CLASS_CONTROL_FOLDER, 100, false, 166, "166", "");
            attr = createAttribute(-1, null, 166, CID_STRING, "title", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Наименование папки.");
            KrnAttribute attr1 = createAttribute(-1, null, 166, 166, "parent", 0, false, true, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr1.uid, "Предок.");
            attr = createAttribute(-1, null, 166, 99, "value", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Объект узла, если его нет, то это директория.");
            attr = createAttribute(-1, null, 166, 166, "children", COLLECTION_SET, false, false, false, false, 0, 0, attr1.id, 0, false, null, 0);
            setAttributeComment(attr.uid, "Потомки.");
            attr = createAttribute(-1, null,166, CID_INTEGER, "type", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Тип директории");
            
            createClass(Constants.NAME_CLASS_CONTROL_FOLDER_ROOT, 166, false, 167, "167", "");

            createClass("SystemAction", 100, false, 171, "171", "");
            KrnAttribute sa_code=createAttribute(-1, null, 171, CID_INTEGER, "code", 0, false, true, false, false, 0, 0, 0, 0, false, null, 0);
            KrnAttribute sa_name=createAttribute(-1, null, 171, CID_STRING, "name", 0, false, false, true, false, 255, 0, 0, 0, false, null, 0);
            createClass("SystemRight", 100, false, 172, "172", "");
            createAttribute(-1, null, 172, 171, "action", 0, false, true,false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 172, CID_BOOL, "block", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 172, CID_BOOL, "deny", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 172, CID_MEMO, "description", 0, false, false, true, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 172, CID_BLOB, "expr", 0, false, false, true, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 172, CID_STRING, "name", 0, false, false, true, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 172, 123, "userOrRole", COLLECTION_ARRAY, false, true,false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 172, 111, "НСИ", COLLECTION_ARRAY, false, true,false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 172, 111, "архив", COLLECTION_ARRAY, false, true,false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 172, 120, "пользователь", COLLECTION_ARRAY, false, true,false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 172, 139, "процесс", COLLECTION_ARRAY, false, true,false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 172, 121, "роль", COLLECTION_ARRAY, false, true,false, false, 0, 0, 0, 0, false, null, 0);

            createClass("ProtocolRule", 100, false, 173, "173", "");
            createAttribute(-1, null, 173, CID_BOOL, "block", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 173, CID_BOOL, "deny", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 173, 105, "eventType", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 173, CID_BLOB, "expr", 0, false, false, true, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 173, CID_STRING, "name", 0, false, false, true, false, 255, 0, 0, 0, false, null, 0);

            createClass("SystemEvent", 103, true, 174, "174", "");
            createAttribute(-1, null, 173, 174, "event", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 174, CID_STRING, "code", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 174, CID_STRING, "name", 0, false, false, false, true, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 174, 105, "type", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);

            createClass("FGACRule", 100, false, 175, "175", "");
            createAttribute(-1, null, 175, CID_STRING, "name", 0, false, false, true, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 175, CID_STRING, "атрибуты", 0, false, false, true, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 175, CID_MEMO, "дополнительное условие", 0, false, false, true, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 175, CID_BOOL, "заблокировано?", 0, false, false, true, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 175, CID_STRING, "класс", 0, false, false, true, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 175, CID_STRING, "операции", 0, false, false, true, false, 255, 0, 0, 0, false, null, 0);
           createClass("FGARule", 100, false, 176, "176", "");
           createAttribute(-1, null, 176, CID_STRING, "name", 0, false, false, true, false, 255, 0, 0, 0, false, null, 0);
           createAttribute(-1, null, 176, CID_STRING, "атрибуты", 0, false, false, true, false, 255, 0, 0, 0, false, null, 0);
           createAttribute(-1, null, 176, CID_MEMO, "дополнительное условие", 0, false, false, true, false, 0, 0, 0, 0, false, null, 0);
           createAttribute(-1, null, 176, CID_BOOL, "заблокировано?", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
           createAttribute(-1, null, 176, CID_STRING, "класс", 0, false, false, true, false, 255, 0, 0, 0, false, null, 0);
           createAttribute(-1, null, 176, CID_STRING, "операции", 0, false, false, true, false, 255, 0, 0, 0, false, null, 0);
            createClass("FSDirectory", 100, false, 177, "177", "");
            createAttribute(-1, null, 177, CID_INTEGER, "attrId", 0, false, true, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 177, CID_STRING, "name", 0, false, false, true, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 177, CID_STRING, "url", 0, false, false, true, false, 255, 0, 0, 0, false, null, 0);
            createClass("Recycle", 100, false, 178, "178", "");
            createClass("FilterRecycle", 178, false, 179, "179", "");
            createAttribute(-1, null, 179, CID_STRING, "className", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 179, CID_STRING, "eventDate", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 179, CID_STRING, "eventInitiator", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 179, CID_STRING, "title", 0, false, false, true, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 179, CID_STRING, "uid", 0, false, true, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 179, CID_BLOB, "config", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 179, CID_BLOB, "exprSql", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 179, 107, "parent", 0, false, true, false, false, 0, 0, 0, 0, false, null, 0);
            createClass("ProcessDefRecycle", 178, false, 180, "180", "");
            createAttribute(-1, null, 180, CID_STRING, "eventDate", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 180, CID_STRING, "eventInitiator", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 180, CID_STRING, "title", 0, false, false, true, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 180, CID_STRING, "uid", 0, false, true, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 180, CID_STRING, "hotKey", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 180, CID_BLOB, "diagram", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 180, CID_BLOB, "config", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 180, CID_BLOB, "icon", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 180, CID_BLOB, "test", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 180, CID_BLOB, "strings", 0, false, false, true, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 180, CID_BLOB, "message", 0, false, false, true, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 180, CID_INTEGER, "isBtnToolBar", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 180, CID_INTEGER, "runtimeIndex", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 180, 107, "filters", 0, false, true, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 180, 139, "parent", 0, false, true, false, false, 0, 0, 0, 0, false, null, 0);
            createClass("ReportPrinterRecycle", 178, false, 181, "181", "");
            createAttribute(-1, null, 181, CID_STRING, "eventDate", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 181, CID_STRING, "eventInitiator", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 181, CID_STRING, "title", 0, false, false, true, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 181, CID_STRING, "uid", 0, false, true, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 181, CID_STRING, "ref", 1, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 181, CID_MEMO, "constraints", 1, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 181, CID_MEMO, "descInfo", 0, false, false, true, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 181, CID_BLOB, "config", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 181, CID_BLOB, "data", 0, false, false, true, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 181, CID_BLOB, "data2", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 181, CID_BLOB, "template", 0, false, false, true, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 181, CID_BLOB, "template2", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 181, CID_INTEGER, "flags", 1, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 181, 145, "bases", 1, false, true, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 181, 114, "parent", 0, false, true, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 181, 115, "базовый отчет", 0, false, true, false, false, 0, 0, 0, 0, false, null, 0);
            createClass("UIRecycle", 178, false, 182, "182", "");
            createAttribute(-1, null, 182, CID_STRING, "eventDate", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 182, CID_STRING, "eventInitiator", 0, false, false, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 182, CID_STRING, "title", 0, false, false, true, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 182, CID_STRING, "uid", 0, false, true, false, false, 255, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 182, CID_BLOB, "config", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 182, CID_BLOB, "strings", 0, false, false, true, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 182, CID_BLOB, "webConfig", 0, false, false, true, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 182, CID_BOOL, "webConfigChanged", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 182, 107, "filtersFolder", 0, false, true, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 182, 123, "parent", 0, false, true, false, false, 0, 0, 0, 0, false, null, 0);
            
            // Перенос апгрейда 41
            KrnClass cls = createClass("ProcessDefUsingHistory", 100, false, 183, "183", "");
            KrnAttribute attr2 = createAttribute(-1, null, 183, 123, "user", 0, false, true, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 183, 139, "processDef", 0, false, true, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 183, 3, "time", 0, false, true, false, false, 0, 0, 0, 0, false, null, 0);
            createAttribute(-1, null, 123, cls.id, "historyProcessDef", 2, false, false, false, false, 0, 0, attr2.id, 0, false, null, 0);

            // Создать необходимые объекты

            // Структура БД
            KrnObject obj_b = createObject(144, 0, -1, null);
            setValue(obj_b, bName.id, 0, 0, 0, "Системная база", false);
            KrnObject obj_sb = createObject(146, 0, -1, null);
            setValue(obj_sb, sbZnach.id, 0, 0, 0, obj_b.id, false);
            setValue(obj_sb, sbName.id, 0, 0, 0, "Системная база", false);
            setValue(obj_sb, sbFlags.id, 0, 0, 0, 0, false);
            setValue(obj_sb, sbLevel.id, 0, 0, 0, 0, false);
            setValue(obj_sb, sbPhys.id, 0, 0, 0, true, false);
            //
            // Фильтры
            obj = createObject(109, 0, -1, null);
            setValue(obj, fTitle.id, 0, obj_ru.id, 0, "Фильтры", false);
            //
            // Меню
            KrnObject obj_h = createObject(113, 0, -1, null);
            setValue(obj_h, gTitle.id, 0, obj_ru.id, 0, "Меню", false);
            obj = createObject(112, 0, -1, null);
            setValue(obj, gTitle.id, 0, obj_ru.id, 0, "Архив", false);
            setValue(obj_h, hHs.id, 0, 0, 0, obj.id, false);
            obj = createObject(112, 0, -1, null);
            setValue(obj, gTitle.id, 0, obj_ru.id, 0, "Справочники", false);
            setValue(obj_h, hHs.id, 1, 0, 0, obj.id, false);
            //
            // Интерфейсы
            obj = createObject(122, 0, -1, null);
            setValue(obj, uiTitle.id, 0, obj_ru.id, 0, "Интерфейсы", false);
            //
            // Отчеты
            obj = createObject(116, 0, -1, null);
            setValue(obj, gTitle.id, 0, obj_ru.id, 0, "Отчеты", false);
            //
            // Пользователи
            KrnObject obj_u = createObject(125, 0, -1, null);
            setValue(obj_u, u_name.id, 0, 0, 0, "Пользователи", false);
            obj = createObject(123, 0, -1, null);
            setValue(obj, u_name.id, 0, 0, 0, "sys", false);
            setValue(obj, uAdm.id, 0, 0, 0, 1, false);
            setValue(obj, uEdit.id, 0, 0, 0, 0, false);
            setValue(obj, u_pd.id, 0, 0, 0, "QL0AFWMIX8NRZTKeof9cXsvbvu8=", false);
            setValue(obj, uBase.id, 0, 0, 0, obj_sb.id, false);
            setValue(obj, uSign.id, 0, 0, 0, "Системный администратор", false);
            setValue(obj, u_dl.id, 0, 0, 0, obj_ru.id, false);
            setValue(obj, u_il.id, 0, 0, 0, obj_ru.id, false);
            setValue(obj_u, uChildren.id, 0, 0, 0, obj.id, false);
            setValue(obj, uNL.id, 0, 0, 0, 1, false);
            setValue(obj, uLLT.id, 0, 0, 0, new Timestamp(System.currentTimeMillis()), false);
            setValue(obj, udc.id, 0, 0, 0, new Timestamp(System.currentTimeMillis()), false);

            obj = createObject(123, 0, -1, null);
            setValue(obj, u_name.id, 0, 0, 0, "sys_admin", false);
            setValue(obj, uAdm.id, 0, 0, 0, 1, false);
            setValue(obj, uEdit.id, 0, 0, 0, 0, false);
            setValue(obj, u_pd.id, 0, 0, 0, "QL0AFWMIX8NRZTKeof9cXsvbvu8=", false);
            setValue(obj, uBase.id, 0, 0, 0, obj_sb.id, false);
            setValue(obj, uSign.id, 0, 0, 0, "Администратор", false);
            setValue(obj, u_dl.id, 0, 0, 0, obj_ru.id, false);
            setValue(obj, u_il.id, 0, 0, 0, obj_ru.id, false);
            setValue(obj_u, uChildren.id, 1, 0, 0, obj.id, false);
            setValue(obj, uNL.id, 0, 0, 0, 1, false);
            setValue(obj, uLLT.id, 0, 0, 0, new Timestamp(System.currentTimeMillis()), false);
            setValue(obj, udc.id, 0, 0, 0, new Timestamp(System.currentTimeMillis()), false);

            obj = createObject(123, 0, -1, null);
            setValue(obj, u_name.id, 0, 0, 0, "Отчет", false);
            setValue(obj, uAdm.id, 0, 0, 0, 1, false);
            setValue(obj, uEdit.id, 0, 0, 0, 0, false);
            setValue(obj, u_pd.id, 0, 0, 0, "KI+neIS58u02uzj38E130jf/S34=", false);
            setValue(obj, uBase.id, 0, 0, 0, obj_sb.id, false);
            setValue(obj, uSign.id, 0, 0, 0, "Редактор", false);
            setValue(obj, u_dl.id, 0, 0, 0, obj_ru.id, false);
            setValue(obj, u_il.id, 0, 0, 0, obj_ru.id, false);
            setValue(obj_u, uChildren.id, 1, 0, 0, obj.id, false);
            setValue(obj, uNL.id, 0, 0, 0, 1, false);
            setValue(obj, uLLT.id, 0, 0, 0, new Timestamp(System.currentTimeMillis()), false);
            setValue(obj, udc.id, 0, 0, 0, new Timestamp(System.currentTimeMillis()), false);

            //
            // События
            obj = createObject(104, 0, -1, null);
            setValue(obj, eCod.id, 0, 0, 0, "TMR", false);
            setValue(obj, eName.id, 0, 0, 0, "Timer", false);
            obj = createObject(104, 0, -1, null);
            setValue(obj, eCod.id, 0, 0, 0, "MSC", false);
            setValue(obj, eName.id, 0, 0, 0, "MessageCash", false);
            obj = createObject(104, 0, -1, null);
            setValue(obj, eCod.id, 0, 0, 0, "MQT", false);
            setValue(obj, eName.id, 0, 0, 0, "MqTransport", false);
            obj = createObject(104, 0, -1, null);
            setValue(obj, eCod.id, 0, 0, 0, "JMST", false);
            setValue(obj, eName.id, 0, 0, 0, "JmsTransport", false);
            obj = createObject(104, 0, -1, null);
            setValue(obj, eCod.id, 0, 0, 0, "WSC", false);
            setValue(obj, eName.id, 0, 0, 0, "WebService", false);
            obj = createObject(104, 0, -1, null);
            setValue(obj, eCod.id, 0, 0, 0, "EML", false);
            setValue(obj, eName.id, 0, 0, 0, "MailTransport", false);
            obj = createObject(105, 0, -1, null);
            setValue(obj, etCode.id, 0, 0, 0, 1, false);
            setValue(obj, etType.id, 0, 0, 0, "Уведомление", false);
            setValue(obj, etTypem.id, 0, 0, 0, "Уведомление", false);
            obj = createObject(105, 0, -1, null);
            setValue(obj, etCode.id, 0, 0, 0, 2, false);
            setValue(obj, etType.id, 0, 0, 0, "Предупреждение", false);
            setValue(obj, etTypem.id, 0, 0, 0, "Предупреждение", false);
            obj = createObject(105, 0, -1, null);
            setValue(obj, etCode.id, 0, 0, 0, 3, false);
            setValue(obj, etType.id, 0, 0, 0, "Ошибка", false);
            setValue(obj, etTypem.id, 0, 0, 0, "Ошибка", false);
            obj = createObject(105, 0, -1, null);
            setValue(obj, etCode.id, 0, 0, 0, 4, false);
            setValue(obj, etType.id, 0, 0, 0, "Фатальная ошибка", false);
            setValue(obj, etTypem.id, 0, 0, 0, "Фатальная ошибка", false);
            // Процессы
            obj = createObject(141, 0, -1, null);
            setValue(obj, p_title.id, 0, obj_ru.id, 0, "Процессы", false);
            //
            // Пункты обмена
            obj = createObject(136, 0, -1, null);
            setValue(obj, boxName.id, 0, obj_ru.id, 0, "Пункты обмена", false);
            //
            // Планировщик
            obj = createObject(129, 0, -1, null);
            setValue(obj, t_title.id, 0, obj_ru.id, 0, "Планировщик", false);

            //SystemAction
            obj = createObject(171, 0, -1, null);
            setValue(obj, sa_code.id, 0, 0, 0, 1, false);
            setValue(obj, sa_name.id, 0, obj_ru.id, 0, "Вход в систему", false);

            obj = createObject(171, 0, -1, null);
            setValue(obj, sa_code.id, 0, 0, 0, 2, false);
            setValue(obj, sa_name.id, 0, obj_ru.id, 0, "Запуск процесса", false);
            
            obj = createObject(171, 0, -1, null);
            setValue(obj, sa_code.id, 0, 0, 0, 3, false);
            setValue(obj, sa_name.id, 0, obj_ru.id, 0, "Остановка процесса", false);
            
            obj = createObject(171, 0, -1, null);
            setValue(obj, sa_code.id, 0, 0, 0, 4, false);
            setValue(obj, sa_name.id, 0, obj_ru.id, 0, "Просмотр архива", false);
            
            obj = createObject(171, 0, -1, null);
            setValue(obj, sa_code.id, 0, 0, 0, 5, false);
            setValue(obj, sa_name.id, 0, obj_ru.id, 0, "Редактирование записей НСИ", false);
            
            obj = createObject(171, 0, -1, null);
            setValue(obj, sa_code.id, 0, 0, 0, 6, false);
            setValue(obj, sa_name.id, 0, obj_ru.id, 0, "Редактирование пользователей", false);
            
            obj = createObject(171, 0, -1, null);
            setValue(obj, sa_code.id, 0, 0, 0, 7, false);
            setValue(obj, sa_name.id, 0, obj_ru.id, 0, "Редактирование ролей", false);

            // OrLang
            obj = createObject(153, 0, -1, null);
            try {
                byte[] b_funcs = Utils.getXMLResource("funcs");
                setValue(obj, olFuncs.id, 0, 0, 0, b_funcs, false);
            } catch (Exception e) {
                log.error(e, e);
            }
            try {
                byte[] b_vars = Utils.getXMLResource("vars");
                setValue(obj, olVars.id, 0, 0, 0, b_vars, false);
            } catch (Exception e) {
                log.error(e, e);
            }

            res = obj_sb.id;
            log.info("Инициализация Базы Данных завершена!");
        } catch (DriverException e) {
            log.error(e, e);
        }
        return res;
    }
    
    protected abstract long upgradeImpl(long version) throws SQLException, DriverException;

    private static void loadTriggerExceptFiles(String dsName, File xml) {
        Map<MultiKey, List<Long>> m = new HashMap<MultiKey, List<Long>>();
    	triggerExcept.put(dsName, m);
        
    	if (xml != null && xml.exists()) {
            SAXBuilder builder = new SAXBuilder();
            List<Element> plugs = null;
            try {
                plugs = builder.build(xml).getRootElement().getChildren();
                for (int i = 0; i < plugs.size(); i++) {
                    Element item = plugs.get(i);
                    Long parent_class_id = Long.parseLong(item.getAttribute("parent_class_id").getValue());
                    Long child_class_id = Long.parseLong(item.getAttribute("child_class_id").getValue());
                    Long attr_id = Long.parseLong(item.getAttribute("attr_id").getValue());
                    
                    MultiKey key = new MultiKey(parent_class_id, attr_id);
                    List<Long> child_ids = m.get(key);
                    if (child_ids == null) {
                    	child_ids = new ArrayList<Long>();
                    	m.put(key, child_ids);
                    }
                    child_ids.add(child_class_id);

                }
            } catch (JDOMException e) {
                log.error(e, e);
            } catch (IOException e) {
                log.error(e, e);
            }
        } else {
        	log.info("Не найден файл для исключений триггеров");
        }
    }
    
/*	
 * TODO Удалить (кажется не используется)
 * protected Node<Pair<Long, Long>, Object> putObject(KrnObject obj) {
    	if (objRoot != null) {
			Fqn<String> fqn = Fqn.fromElements("" + obj.id);
			Node<Pair<Long, Long>, Object> n = objRoot.addChild(fqn);
			n.put(OBJ_KEY, obj);
			return n;
    	}
    	return null;
    }

    protected void removeObject(KrnObject obj) {
    	if (objRoot != null)
    		objRoot.removeChild("" + obj.id);
    }
*/    
    @Override
    public String getClassTableName(long clsId) { 
    	KrnClass cls = db.getClassById(clsId);
		return getClassTableName(cls, true);
	}
    
    public String getClassTableName(long clsId, boolean withPrefix) { 
    	KrnClass cls = db.getClassById(clsId);
		return getClassTableName(cls, withPrefix);
	}

    public String getClassTableNameComp(long clsId) throws SQLException { 
    	KrnClass cls = getClassByIdComp(clsId);
		return getClassTableName(cls, true);
	}

    protected KrnClass getClassByIdComp(long id) throws SQLException {
    	KrnClass cls = db.getClassById(id);
    	if (cls == null) {
	        PreparedStatement pst = conn.prepareStatement("SELECT * FROM "+prefixForQuery+"t_classes WHERE c_id=?");
	        pst.setLong(1, id);
	        ResultSet set = pst.executeQuery();
	        if (set.next()) {
	            String name = getSanitizedString(set, "c_name");
	            long parentId = set.getLong("c_parent_id");
	            boolean isRepl = set.getBoolean("c_is_repl");
	            String uid = getSanitizedString(set, "c_cuid");
				int mod = set.getInt("c_mod");
				String tname = null;
	            if (isVersion(TnameVersionBD)) {
	            	tname = getSanitizedString(set, "c_tname");
	            }
	            byte[] beforeCreateObjExpr = isVersion(OrlangTrigersVersionBD2) ? set.getBytes("c_before_create_obj") : null;
	            byte[] afterCreateObjExpr = isVersion(OrlangTrigersVersionBD2) ? set.getBytes("c_after_create_obj") : null;
	            byte[] beforeDeleteObjExpr = isVersion(OrlangTrigersVersionBD2) ? set.getBytes("c_before_delete_obj") : null;
	            byte[] afterDeleteObjExpr = isVersion(OrlangTrigersVersionBD2) ? set.getBytes("c_after_delete_obj") : null;
	            int beforeCreateObjTr = isVersion(OrlangTrigersVersionBD3) ? set.getInt("c_before_create_obj_tr") : 0;
	            int afterCreateObjTr = isVersion(OrlangTrigersVersionBD3) ? set.getInt("c_after_create_obj_tr") : 0;
	            int beforeDeleteObjTr = isVersion(OrlangTrigersVersionBD3) ? set.getInt("c_before_delete_obj_tr") : 0;
	            int afterDeleteObjTr = isVersion(OrlangTrigersVersionBD3) ? set.getInt("c_after_delete_obj_tr") : 0;

                cls = new KrnClass(uid, id, parentId, isRepl, mod, name, tname, beforeCreateObjExpr, afterCreateObjExpr, beforeDeleteObjExpr, afterDeleteObjExpr, beforeCreateObjTr, afterCreateObjTr, beforeDeleteObjTr, afterDeleteObjTr);
				db.addClass(cls, false);
	        }
	        set.close();
	        pst.close();
    	}
        return cls;
    }

    @Override
    public String getClassTableName(String clsUid) { 
    	KrnClass cls = db.getClassByUid(clsUid);
		return getClassTableName(cls, true);
	}

    @Override
	public String getAttrTableName(KrnAttribute attr) {
		return getAttrTableName(attr, true);
	}

    @Override
	public String getAttrTableName(KrnAttribute attr, boolean withPrefix) {
    	if (withPrefix) {
	    	StringBuilder atn = new StringBuilder(prefixForQuery);
	    	kz.tamur.or3.util.Tname.setTableNamePrefixU(atn, kz.tamur.or3.util.Tname.getAttrTableName(attr));
			return atn.toString();
    	}
    	else
    		return kz.tamur.or3.util.Tname.getAttrTableName(attr);
	}

    @Override
    public String getClassTableName(KrnClass cls, boolean withPrefix) {
    	if (withPrefix && !cls.ignoreScheme()) {
        	StringBuilder atn = new StringBuilder(prefixForQuery);
        	kz.tamur.or3.util.Tname.setTableNamePrefixU(atn, kz.tamur.or3.util.Tname.getClassTableName(cls));
    		return atn.toString();
    	}
    	else
    		return kz.tamur.or3.util.Tname.getClassTableName(cls);
	}

    @Override
	public String getAttrTableName(long attrId) {
    	KrnAttribute attr = db.getAttributeById(attrId);
		return getAttrTableName(attr);
	}

    @Override
    public String getColumnName(long attrId) {
    	KrnAttribute attr = db.getAttributeById(attrId);
		return getColumnName(attr);
	}

    @Override
    public String getColumnName(KrnAttribute attr) {
		return kz.tamur.or3.util.Tname.getColumnName(attr);
	}

    @Override
    public String getColumnName(KrnAttribute attr, long langId) throws DriverException {
    	String ret = kz.tamur.or3.util.Tname.getColumnName(attr);
    	
		if (attr.isMultilingual) {
			int i = getSystemLangIndex(langId);
			ret = ret + "_" + i;
		}
		return ret;
	}

	protected String getColumnNames(KrnAttribute attr) throws DriverException {
		String cm = kz.tamur.or3.util.Tname.getColumnName(attr);
		if (attr.isMultilingual) {
			String s = "";
			int count = getSystemLangs().size();
			for (int i=1; i<count; i++) {
				s += cm + "_" + i + ",";
			}
			s += cm + "_" + count;
			return s;
		}
		return cm;
	}

	protected String getColumnName(KrnAttribute attr, int langIndex) throws DriverException {
		String ret = kz.tamur.or3.util.Tname.getColumnName(attr);
		if (attr.isMultilingual) {
			return ret + "_" + langIndex;
		}
		return ret;
	}

	public void loadClassTable(KrnClass cls, BufferedReader br, String separator) throws DriverException {
		log.info("Loading " + cls.id + " " + cls.name);
    	PreparedStatement pst = null;
    	try {
	    	// Первая строка содержит названия полей, которые должны соответствовать
	    	// названиям колонок в таблице
	    	String line = br.readLine();
	    	if (line == null || line.trim().length() <= 1)
	    		return;
	    	
	    	line = line.trim();
	    	
	    	if (line.matches(".+")) {
		    	String[] fields = split(line, separator);
		    	
		    	// Подготавливаем запрос
	        	StringBuilder sql = new StringBuilder(
	        			"INSERT INTO " + Funcs.sanitizeSQL(getClassTableName(cls.id)) + " (" + Funcs.sanitizeSQL(fields[0]));
	        	StringBuilder valuesSql = new StringBuilder("?");
	        	for(int i = 1; i < fields.length; i++) {
	            	if("END".equals(fields[i]))
	            		fields[i]="C_END";
	            	else if("LAST".equals(fields[i]))
	            		fields[i]="C_LAST";
	            	else if("BEGIN".equals(fields[i]))
	            		fields[i]="C_BEGIN";
		        		
	            	sql.append(",").append(Funcs.sanitizeSQL(fields[i]));
		        		valuesSql.append(",?");
	        	}
	        	if (cls.id == 99 && this instanceof MsSqlDriver3) {
	        		pst = conn.prepareStatement(
	        				"SET IDENTITY_INSERT "+Funcs.sanitizeSQL(getClassTableName(99))+" ON " + sql + ") VALUES (" + valuesSql + ") SET IDENTITY_INSERT "+Funcs.sanitizeSQL(getClassTableName(99))+" OFF");
	        	} else {
	        		pst = conn.prepareStatement(
	        				sql + ") VALUES (" + valuesSql + ")");
	    		}
	        		
	            // Узнаем типы колонок
	            long[] types = new long[fields.length];
	            // Предопределенные колонки
	            types[Funcs.indexOf("c_uid", fields)] = 0;
	            types[Funcs.indexOf("c_obj_id", fields)] = PC_INTEGER;
	            types[Funcs.indexOf("c_class_id", fields)] = PC_INTEGER;
	            if (cls.id > 99) {
		            types[Funcs.indexOf("c_tr_id", fields)] = PC_INTEGER;
		            types[Funcs.indexOf("c_is_del", fields)] = PC_BOOL;
		            // Клонки для атрибутов
		            List<KrnAttribute> attrs = db.getAttributesByClassId(cls.id, false);
		            for(KrnAttribute attr : attrs) {
		            	if (attr.collectionType == 0 && attr.rAttrId == 0) {
		            		if (attr.isMultilingual) {
		            			for(int i = 0; i < sysLangCount; i++) {
		                    		String cname = getColumnName(attr, i + 1);
		                    		types[Funcs.indexOf(cname, fields)] = attr.typeClassId;
		            			}
		            		} else {
		                		String cname = getColumnName(attr);
		                		types[Funcs.indexOf(cname, fields)] = attr.typeClassId;
		            		}
		            	}
		            }
	            }
		    	// Считываем строки с данными
	            int j = 0;
		    	while((line = br.readLine()) != null) {
			    	line = Funcs.sanitizeSQL(line);

			    	if (line.matches(".+")) {
			    		String[] values = split(line, separator);
			    		for (int i = 0; i < values.length; i++) {
			    			long type = types[i];
			    			String value = Funcs.validate(values[i]);
			    			if (type == 0)
			    				pst.setString(i + 1, value);
			    			else
			    				setValue(pst, i + 1, type, parseFileValue(value, type));
			    		}
			    		try {
			    			pst.executeUpdate();
			    		}catch(Exception epst) {
			    			log.info("Ошибка при записи в базу:"+epst.getMessage());
			    		}
			    	}
	
	                if (++j%500 == 0)
	                	log.info("Проверено объектов " + cls.name + ": " + j);
		    	}
	        	log.info("Проверено объектов " + cls.name + ": " + j);
	    	}
    	} catch (Exception e) {
            log.error(e, e);
    		throw new DriverException(e.getMessage());
    	} finally {
    		DbUtils.closeQuietly(pst);
    	}
    }
    
	public void loadClassTableFromExtDb(KrnClass cls, Connection extConn) throws DriverException {
    	PreparedStatement pst = null;
    	
    	String tname = getClassTableName(cls.id);
		String tnameExt = getClassTableName(cls, false);

		String selSql = "SELECT * FROM " + tnameExt;
		String selCountSql = "SELECT count(*) FROM " + tname;
		
		String selCountSqlExt = "SELECT count(*) FROM " + tnameExt.toUpperCase();
		
		long countRowsExt=0, countRows=0;
		
		// смотрим сколько записей в таблице класса во внешней и текущей БД
    	try (
    			PreparedStatement pstSelExt = extConn.prepareStatement(selCountSqlExt);
    			PreparedStatement pstSel=conn.prepareStatement(selCountSql);
	    		ResultSet rsExt=pstSelExt.executeQuery();
    			ResultSet rs=pstSel.executeQuery()
    	) {
    		if (rsExt.next())
    			countRowsExt = rsExt.getLong(1);
    		if (rs.next())
    			countRows = rs.getLong(1);
		
    	} catch (SQLException e) {
        	log.error("Ошибка при доступе к таблице " + cls.name);
        	log.error(selCountSql);
        	log.error(selCountSqlExt);
            //log.error(e, e);
		}
    	
    	// если во внешней БД больше
		if (countRowsExt>0 && countRowsExt>countRows) {
	    	try (
	    			PreparedStatement pstSel = extConn.prepareStatement(selSql);
		    		ResultSet rs = pstSel.executeQuery()
		    ){
	    		ResultSetMetaData rsmd = pstSel.getMetaData();
	    		int colCount = rsmd.getColumnCount();
		    	List<String> fields = new ArrayList<>();
		    	List<Long> types = new ArrayList<>();
		    	
		    	//Заполняем массив названий полей
	    		for (int i=0; i<colCount; i++) {
	    			fields.add(rsmd.getColumnName(i+1).toUpperCase());
	    			types.add(-1L);
	    		}
			    	
	    		// Подготавливаем запрос
		        StringBuilder sql = new StringBuilder("INSERT INTO " + getClassTableName(cls.id) + " (");
		        
		        StringBuilder valuesSql = new StringBuilder("");
		        
		        for(int i = 1; i < fields.size(); i++) {
	            	if("END".equals(fields.get(i)))
	            		fields.set(i,"C_END");
	            	else if("LAST".equals(fields.get(i)))
	            		fields.set(i,"C_LAST");
	            	else if("BEGIN".equals(fields.get(i)))
	            		fields.set(i,"C_BEGIN");
	        	}
	            
		        // Узнаем типы колонок
	            // Предопределенные колонки
	        
		        long pc_integer = PC_INTEGER;
		        int index = fields.indexOf("C_UID");
	            if(index>=0)  types.set(index, 0L);
	            index=fields.indexOf("C_OBJ_ID");
	            if(index>=0)  types.set(index, pc_integer);
	            index=fields.indexOf("C_CLASS_ID");
	            if(index>=0)  types.set(index, pc_integer);
		            
	            if (cls.id > 99) {
		            index=fields.indexOf("C_TR_ID");
		            if(index>=0)  types.set(index, pc_integer);
		            index=fields.indexOf("C_IS_DEL");
		            if(index>=0)  types.set(index, pc_integer);
			        
		            // Клонки для атрибутов
		            List<KrnAttribute> attrs = db.getAttributesByClassId(cls.id, false);
		            for(KrnAttribute attr : attrs) {
		            	if (attr.collectionType == 0 && attr.rAttrId == 0) {
		            		if (attr.isMultilingual) {
		            			for(int i = 0; i < sysLangCount; i++) {
		                    		String cname = getColumnName(attr, i + 1);
		        		            index = fields.indexOf(cname.toUpperCase());
		        		            if(index>=0) types.set(index, attr.typeClassId);
		            			}
		            		} else {
		                		String cname = getColumnName(attr);
	        		            index = fields.indexOf(cname.toUpperCase());
	        		            if(index>=0) types.set(index, attr.typeClassId);
		            		}
		            	}
		            }
	            }
		           
	            boolean par=false;
	        	for(int i = 0; i < fields.size(); i++) {
	            	if(types.get(i)>=0) {
	            		if (par) {
			        		sql.append(",").append(fields.get(i));
			        		valuesSql.append(",?");
	            		} else {
			        		sql.append(fields.get(i));
			        		valuesSql.append("?");
			        		par=true;
	            		}
	        		}
	        	}

	        	pst = conn.prepareStatement(sql + ") VALUES (" + valuesSql + ")");
			    	
	        	// Считываем строки с данными
		    	long j=0;
		    		
		    	while(rs.next()) {
		    		// Если записи в таблице уже были, то проверяем на существование
	    			if(countRows>0 && countRowsExt>countRows && isRecordExists(tname,rs.getLong("C_OBJ_ID"),rs.getLong("C_TR_ID"),-1)) continue;
	    			
		    		try {
			    		for (int i = 0, k=1; i < colCount; i++) {
			    			long type = types.get(i);
			    			if(type==-1) continue;
			    			if (type == 0) {
			    				pst.setString(k, rs.getString(fields.get(i)));
			    			}else if(type == PC_MEMO) {
			    				pst.setString(k, getClobToText(rs,fields.get(i)));
			    			}else if(type == PC_BLOB) {
			    				pst.setBytes(k, rs.getBytes(fields.get(i)));
			    			}else if(type == PC_DATE) {
			    				pst.setDate(k, rs.getDate(fields.get(i)));
			    			}else if(type == PC_TIME) {
			    				pst.setTimestamp(k, rs.getTimestamp(fields.get(i)));
			    			}else {
			    				setValue(pst, k, type, rs.getObject(fields.get(i)));
			    			}
			    			k++;
			    		}
		    			pst.addBatch();
	    			} catch(Exception ex) {
	                	log.info("Ошибка при записи объекта в класс " + cls.name + " obj_id: " + rs.getLong("C_OBJ_ID"));
	    	            log.error(ex.getMessage());
	    	            
	    	            pst.executeBatch();
	    	            conn.commit();
	    			}
	    			++j;
	                if (j%500 == 0) {
	                	log.info("Записано объектов " + cls.name + ": " + (j+countRows)+"/"+countRowsExt);
	                }
	                if (j%2500 == 0 && countRowsExt<100000) {
	    	            pst.executeBatch();
	                	conn.commit();
	                } else if (j%5000 == 0) {
	    	            pst.executeBatch();
	                	conn.commit();
	                }
	    		}
		    	pst.executeBatch();
            	conn.commit();
	        	log.info("Записано объектов " + cls.name + ": " + (j+countRows)+"/"+countRowsExt);
	        	
	    	} catch (Exception e) {
	            log.error(e, e);
	    	} finally {
	    		DbUtils.closeQuietly(pst);
	    	}
		} else {
			if (countRowsExt>0)
				log.info("В талбице " + cls.name + " уже записано объектов:"+countRowsExt);
			else
				log.info("В талбице " + cls.name + " записи отсутствуют.");
		}
    }
	private boolean isRecordExists(String tname,long obj_id,long tr_id,long index_id) {
		String selSql="SELECT C_OBJ_ID FROM "+tname+" WHERE C_OBJ_ID=? AND C_TR_ID=?";
		if(index_id>=0) 	selSql+=" AND C_INDEX=?";
    	try (PreparedStatement pstSel=conn.prepareStatement(selSql);){
			pstSel.setLong(1, obj_id);
			pstSel.setLong(2, tr_id);
			if(index_id>=0) pstSel.setLong(3, index_id);
			ResultSet rs=pstSel.executeQuery();
    		if(rs.next()) return true;
    	} catch (SQLException e) {
        	log.info("Ошибка при доступе к таблице " + tname);
            log.error(e, e);
		}
    	return false;
	}
    public void loadAttributeTableFromExtDb(KrnAttribute attr, Connection extConn) throws DriverException {
		log.info("Loading " +
				"classId=" + attr.classId + ", " +
				"id=" + attr.id + ", " +
				"name=" + attr.name + ", " +
				"rAttrId=" + attr.rAttrId + ", " +
				"tname="+((attr.tname!=null)?attr.tname:"null"));
		
    	PreparedStatement pst = null;
    	
    	String tname = getAttrTableName(attr);
		String tnameExt = getAttrTableName(attr, false);

		String selSql="SELECT * FROM " + tnameExt;
		String selCountSql="SELECT count(*) FROM " + tname;
		String selCountSqlExt="SELECT count(*) FROM " + tnameExt.toUpperCase();
		
		long countRowsExt=0,countRows=0;
    	
		try (PreparedStatement pstSelExt=extConn.prepareStatement(selCountSqlExt);
    			PreparedStatement pstSel=conn.prepareStatement(selCountSql);
	    		ResultSet rsExt=pstSelExt.executeQuery();
    			ResultSet rs=pstSel.executeQuery()){
    		if(rsExt.next())
    			countRowsExt=rsExt.getLong(1);
    		if(rs.next())
    			countRows=rs.getLong(1);
		
    	} catch (SQLException e) {
        	log.error("Ошибка при доступе к таблице атрибута " + tnameExt);
        	log.error(e);
        	log.error(e);
            //log.error(e, e);
		}
		
		if(countRowsExt>0 && countRowsExt>countRows) {
			try (
					PreparedStatement pstSel=extConn.prepareStatement(selSql);
		    		ResultSet rs=pstSel.executeQuery()
		    ){
	    		ResultSetMetaData rsmd=pstSel.getMetaData();
	    		int colCount=rsmd.getColumnCount();
		    	List<String> fields = new ArrayList<>();
		    	List<Long> types = new ArrayList<>();
		    	//Заполняем массив названий полей
	    		for(int i=0;i<colCount;i++) {
	    			fields.add(rsmd.getColumnName(i+1).toUpperCase());
	    			types.add(-1L);
	    		}
			    	
	    		// Подготавливаем запрос
	        	StringBuilder sql = new StringBuilder(
	        			"INSERT INTO " + getAttrTableName(attr) + " (");
	        	StringBuilder valuesSql = new StringBuilder("");
		    	
	            // Узнаем типы колонок
	            int c_del_ind=-1,c_ind_ind=-1;
	            // Предопределенные колонки
	            long pc_integer=PC_INTEGER;
	            int index=fields.indexOf("C_OBJ_ID");
	            if(index>=0)  types.set(index, pc_integer);
	            index=fields.indexOf("C_TR_ID");
	            if(index>=0)  types.set(index, pc_integer);
	            index=fields.indexOf("C_DEL");
	            if(index>=0)  types.set(c_del_ind=index, pc_integer);
	            if (attr.collectionType == COLLECTION_ARRAY) {
		            index=fields.indexOf("C_INDEX");
		            if(index>=0)  types.set(c_ind_ind=index, pc_integer);
		            index=fields.indexOf("C_ID");
		            if(index>=0)  types.set(index, pc_integer);
	            }
	            // Клонки для атрибута
	    		if (attr.isMultilingual) {
	    			for(int i = 0; i < sysLangCount; i++) {
	            		String cname = getColumnName(attr, i + 1);
	    	            index=fields.indexOf(cname.toUpperCase());
	    	            if(index>=0)  types.set(index, attr.typeClassId);
	    			}
	    		} else {
	        		String cname = getColumnName(attr);
    	            index=fields.indexOf(cname.toUpperCase());
    	            if(index>=0)  types.set(index, attr.typeClassId);
	    		}
	    		boolean par=false;
	        	for(int i = 0; i < fields.size(); i++) {
	        		if(types.get(i)>=0) {
	            		if(par) {
			        		sql.append(",").append(fields.get(i));
			        		valuesSql.append(",?");
	            		}else {
			        		sql.append(fields.get(i));
			        		valuesSql.append("?");
			        		par=true;
	            			
	            		}
	        		}
	        	}
	            pst = conn.prepareStatement(sql + ") VALUES (" + valuesSql + ")");
		    	// Считываем строки с данными
	    		long j=0;
	    		while(rs.next()) {
	    			if(countRows>0 && countRowsExt>countRows && isRecordExists(tname,rs.getLong("C_OBJ_ID"),rs.getLong("C_TR_ID"),c_ind_ind>0?rs.getLong("C_INDEX"):-1)) continue;
	    			try {
			    		for (int i = 0,k=1; i < colCount; i++) {
			    			long type = types.get(i);
			    			if(type==-1) continue;
			    			Object value;
			    			if(type==PC_BLOB) 
			    				value=rs.getBytes(fields.get(i));
			    			else if(type == PC_MEMO)
			    				value=getClobToText(rs,fields.get(i));
				   			else if(type == PC_DATE)
				   				value= rs.getDate(fields.get(i));
				   			else if(type == PC_TIME)
				   				value= rs.getTimestamp(fields.get(i));
			    			else
			    				value=rs.getObject(fields.get(i));
			    			//if(c_del_ind>0 && c_del_ind==i && !"0".equals(""+value))
			    			//	value = 1;
			    			if (type == 0)
			    				pst.setString(k, (String)value);
			    			else
			    				setValue(pst, k, type, value);
			    			k++;
			    		}
		    			pst.addBatch();
	    			}catch(Exception ex){
	                	log.error("Ошибка при записи множественного атрибута " + tnameExt + " obj_id: " + rs.getLong("C_OBJ_ID"));
	    	            log.error(ex.getMessage());
	    	            pst.executeBatch();
	    	            conn.commit();
	    			}
	    			++j;
	                if (j%500 == 0) {
	                	log.info("Записано объектов " + attr.name + ": " + (j+countRows)+"/"+countRowsExt);
	                }
	                if (j%2500 == 0 && countRowsExt<100000) {
	    	            pst.executeBatch();
	                	conn.commit();
	                } else if (j%5000 == 0) {
	    	            pst.executeBatch();
	                	conn.commit();
	                }
	    		}
	            pst.executeBatch();
            	conn.commit();
	        	log.info("Записано множественных атрибутов " + attr.name + ": " + (j+countRows)+"/"+countRowsExt);

	    	} catch (Exception e) {
	    		log.error(e.getMessage(), e);
	    	} finally {
	    		DbUtils.closeQuietly(pst);
	    	}
		}else {
			if(countRowsExt>0)
				log.info("В талбице атрибутов " + tnameExt + " уже записано множественных атрибутов:"+countRowsExt);
			else
				log.info("В талбице атрибутов " + tnameExt + " записи отсутствуют.");
		}
   }
 private Object parseFileValue(String value, long type) {
    	if (value.length() == 0)
    		return null;
    	if (type == 0)
    		return value;
        if (type == PC_STRING || type == PC_MEMO) {
        	if (EMPTY_TEXT.equals(value)) {
        		return "";
        	} else if (value.length() == 0) {
        		return null;
        	} else {
        		byte[] data = HexStringOutputStream.fromHexString(value);
                try {
                	return new String(data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    log.error(e, e);
                }
            }
        } else if (type == PC_BOOL) {
            return "0".equals(value) ? false : true;
        } else if (type == PC_INTEGER) {
        	return Long.valueOf(value);
        } else if (type == PC_DATE) {
        	try {
        		return new java.sql.Date(FILE_DATE_FMT.parse(value).getTime());
        	} catch (ParseException e) {
                log.error(e, e);
        	}
        } else if (type == PC_TIME) {
        	try {
        		return new java.sql.Timestamp(FILE_TIME_FMT.parse(value).getTime());
        	} catch (ParseException e) {
                log.error(e, e);
        	}
        } else if (type == PC_FLOAT) {
        	return Double.valueOf(value);
        } else if (type == PC_BLOB) {
        	if (EMPTY_TEXT.equals(value)) {
        		return new byte[0];
        	} else {
        		return HexStringOutputStream.fromHexString(value);
            }
        } else {
        	return Long.valueOf(value);
        }
        return null;
    }
    
	private String  getClobToText(ResultSet rs, String name) throws SQLException {
		Clob clobData = rs.getClob(name);
		if (clobData != null) {
			Reader reader = clobData.getCharacterStream();
			StringWriter writer = new StringWriter(1024);
			try {
				char[] buffer = new char[4096];
				for(int i = 0; (i = reader.read(buffer)) != -1; writer.write(buffer, 0, i));
				reader.close();
				writer.close();
			} catch (IOException e) {
				log.error(e, e);
			} finally {
				clobData.free();
			}
			return Funcs.normalizeInput(writer.toString());
		}
		return null;
	}
  public void loadAttributeTable(KrnAttribute attr, BufferedReader br, String separator
    		) throws DriverException {
		log.info("Loading " +
				"classId=" + attr.classId + ", " +
				"id=" + attr.id + ", " +
				"name=" + attr.name + ", " +
				"rAttrId=" + attr.rAttrId + ", " +
				"tname="+((attr.tname!=null)?attr.tname:"null"));
		
    	PreparedStatement pst = null;
    	String line="";
    	try {
	    	// Первая строка содержит названия полей, которые должны соответствовать
	    	// названиям колонок в таблице
	    	line = br.readLine();
	    	if (line == null || line.length() <= 1)
	    		return;

	    	line = Funcs.sanitizeSQL2(line.trim());
	    	
	    	if (line.matches(".+")) {
		    	String[] fields = split(line, separator);
		    	
		    	// Подготавливаем запрос
	        	StringBuilder sql = new StringBuilder(
	        			"INSERT INTO " + getAttrTableName(attr) + " (" + fields[0]);
	        	StringBuilder valuesSql = new StringBuilder("?");
	        	for(int i = 1; i < fields.length; i++) {
	        		sql.append(",").append(fields[i]);
	        		valuesSql.append(",?");
	        	}
	            pst = conn.prepareStatement(sql + ") VALUES (" + valuesSql + ")");
		    	
	            // Узнаем типы колонок
	            long[] types = new long[fields.length];
	            int c_del_ind=-1;
	            // Предопределенные колонки
	            types[Funcs.indexOf("c_obj_id", fields)] = PC_INTEGER;
	            types[Funcs.indexOf("c_tr_id", fields)] = PC_INTEGER;
	            types[c_del_ind=Funcs.indexOf("c_del", fields)] = PC_INTEGER;
	            if (attr.collectionType == COLLECTION_ARRAY) {
	            	types[Funcs.indexOf("c_index", fields)] = PC_INTEGER;
	            	types[Funcs.indexOf("c_id", fields)] = PC_INTEGER;
	            }
	            // Клонки для атрибута
	    		if (attr.isMultilingual) {
	    			for(int i = 0; i < sysLangCount; i++) {
	            		String cname = getColumnName(attr, i + 1);
	            		types[Funcs.indexOf(cname, fields)] = attr.typeClassId;
	    			}
	    		} else {
	        		String cname = getColumnName(attr);
	        		types[Funcs.indexOf(cname, fields)] = attr.typeClassId;
	    		}
	    		int j = 0;
		    	// Считываем строки с данными
		    	while((line = br.readLine()) != null) {
			    	line = Funcs.sanitizeSQL2(line);

			    	if (line.matches(".+")) {
			    		String[] values = split(line, separator);
			    		for (int i = 0; i < values.length; i++) {
			    			long type = types[i];
			    			if(c_del_ind>0 && !"0".equals(values[c_del_ind]))
			    				values[c_del_ind]="1";
			    			if (type == 0)
			    				pst.setString(i + 1, values[i]);
			    			else
			    				setValue(pst, i + 1, type, parseFileValue(values[i], type));
			    		}
			    		try {
			    			pst.executeUpdate();
			    		}catch (Exception e) {
			            	log.info("Запись с ошибкой" + attr.name + ": " + line);
			    		}
			    	}
	    			commit();
	                if (++j%500 == 0)
	                	log.info("Проверено объектов " + attr.name + ": " + j);
		    	}
	        	log.info("Проверено объектов " + attr.name + ": " + j);
	    	}
    	} catch (Exception e) {
    		//throw new DriverException(e.getMessage());
    		log.error(e.getMessage());
    	} finally {
    		DbUtils.closeQuietly(pst);
    	}
    }
   
	private String[] split(String str, String separator) {
		List<String> res = new ArrayList<String>(100);

		int pos = 0;
		for (int i = 0; (i = str.indexOf(separator, pos)) != -1; res.add(str.substring(pos, i)), pos = i + separator.length());
		return res.toArray(new String[res.size()]);
	}
	
	@Override
	public String[][] getColumnsInfo(String TableName) { //TODO: Tedit
		return null;
	}

	@Override
	public boolean columnMove(int[] cols, String TableName) { //TODO: Tedit
		return false;
	}

	protected abstract void setValue(
			PreparedStatement pst,
			int colIndex,
			long typeId,
			Object value
	) throws SQLException;

	@Override
	public void dropPolicy(FGACRule rule) {
	}

	@Override
	public void createOrUpdatePolicy(String oldName, String oldTable, FGACRule rule) {
	}

	@Override
	public void dropPolicy(FGARule rule) {
	}

	@Override
	public void createOrUpdatePolicy(String oldName, String oldTable, FGARule rule) {
	}
	
	public static boolean isDataLog() {
		return dataLog;
	}
	
	public static void setDataLog(boolean logData) {
		dataLog = logData;
	}
	
	public static void addAttrChangeListener(long classId, AttrChangeListener l) {
		List<AttrChangeListener> list = attrChangeListenersByClassId.get(classId);
		if (list == null) {
			list = new ArrayList<AttrChangeListener>();
			attrChangeListenersByClassId.put(classId, list);
		}
		if (!list.contains(l))
			list.add(l);
	}

	public static void addModelChangeListener(ModelChangeListener l) {
		modelChangeListeners.add(l);
	}

	public static void removeAttrChangeListener(long classId, AttrChangeListener l) {
	}

	public static List<ModelChangeListener> getModelListeners() {
    	return modelChangeListeners;
    }

	public static Map<Long, List<AttrChangeListener>> getAllListeners() {
		return attrChangeListenersByClassId;
    }

	public static List<AttrChangeListener> getListenersByClassId(long classId) {
		List<AttrChangeListener> list = attrChangeListenersByClassId.get(classId);
    	return list;
    }

	public static boolean hasListenerFor(long classId) {
		List<AttrChangeListener> list = attrChangeListenersByClassId.get(classId);
		if (list != null) {
			return list.size() > 0;
		}
    	return false;
    }

	protected void fireAttrChanged(KrnObject obj, long attrId, long langId, long trId) {
		if (obj != null) {
			List<AttrChangeListener> list = getListenersByClassId(obj.classId);
			if (list != null && list.size() > 0) {
				for (AttrChangeListener l : list) {
					l.attrChanged(obj, attrId, langId, trId, us.getId());
				}
				if (UserSession.SERVER_ID != null)
	    			ServerMessage.sendMessage(UserSession.SERVER_ID, us.getDsName(), us.getId(), new AttrChange(obj, attrId, langId, trId));
			}
		}
	}
	
	public void fireAttrCreated(KrnAttribute attr) {
		List<ModelChangeListener> list = getModelListeners();
		if (list != null) {
			for (ModelChangeListener l : list) {
				l.attrCreated(attr);
			}
		}
		if (UserSession.SERVER_ID != null)
			ServerMessage.sendMessage(UserSession.SERVER_ID, us.getDsName(), ServerMessage.ACTION_ADD_ATTR, attr, null);
	}
	
	public void fireAttrDeleted(KrnAttribute attr) {
		List<ModelChangeListener> list = getModelListeners();
		for (ModelChangeListener l : list) {
			l.attrDeleted(attr);
		}
		if (UserSession.SERVER_ID != null)
			ServerMessage.sendMessage(UserSession.SERVER_ID, us.getDsName(), ServerMessage.ACTION_DEL_ATTR, attr, null);
	}

	public void fireAttrChanged(KrnAttribute attrOld, KrnAttribute attrNew) {
		List<ModelChangeListener> list = getModelListeners();
		for (ModelChangeListener l : list) {
			l.attrChanged(attrOld, attrNew);
		}
		if (UserSession.SERVER_ID != null)
			ServerMessage.sendMessage(UserSession.SERVER_ID, us.getDsName(), ServerMessage.ACTION_CHG_ATTR, attrNew, attrOld);
	}

	public void fireClassCreated(KrnClass cls) {
		List<ModelChangeListener> list = getModelListeners();
		for (ModelChangeListener l : list) {
			l.classCreated(cls);
		}
		if (UserSession.SERVER_ID != null)
			ServerMessage.sendMessage(UserSession.SERVER_ID, us.getDsName(), ServerMessage.ACTION_ADD_CLASS, cls, null);
	}
	
	public void fireClassDeleted(KrnClass cls) {
		List<ModelChangeListener> list = getModelListeners();
		for (ModelChangeListener l : list) {
			l.classDeleted(cls);
		}
		if (UserSession.SERVER_ID != null)
			ServerMessage.sendMessage(UserSession.SERVER_ID, us.getDsName(), ServerMessage.ACTION_DEL_CLASS, cls, null);
	}

	public void fireClassChanged(KrnClass clsOld, KrnClass clsNew) {
		List<ModelChangeListener> list = getModelListeners();
		for (ModelChangeListener l : list) {
			l.classChanged(clsOld, clsNew);
		}
		if (UserSession.SERVER_ID != null)
			ServerMessage.sendMessage(UserSession.SERVER_ID, us.getDsName(), ServerMessage.ACTION_CHG_CLASS, clsNew, clsOld);
	}

	public void fireMethodCreated(KrnMethod m) {
		List<ModelChangeListener> list = getModelListeners();
		for (ModelChangeListener l : list) {
			l.methodCreated(m);
		}
		if (UserSession.SERVER_ID != null)
			ServerMessage.sendMessage(UserSession.SERVER_ID, us.getDsName(), ServerMessage.ACTION_ADD_METH, m, null);
	}
	
	public void fireMethodDeleted(KrnMethod m) {
		List<ModelChangeListener> list = getModelListeners();
		for (ModelChangeListener l : list) {
			l.methodDeleted(m);
		}
		if (UserSession.SERVER_ID != null)
			ServerMessage.sendMessage(UserSession.SERVER_ID, us.getDsName(), ServerMessage.ACTION_DEL_METH, m, null);
	}

	public void fireMethodChanged(KrnMethod oldm, KrnMethod newm) {
		List<ModelChangeListener> list = getModelListeners();
		for (ModelChangeListener l : list) {
			l.methodChanged(oldm, newm);
		}
		if (UserSession.SERVER_ID != null)
			ServerMessage.sendMessage(UserSession.SERVER_ID, us.getDsName(), ServerMessage.ACTION_CHG_METH, newm, oldm);
	}

	public String getPrefixForQuery() {
		return prefixForQuery;
	}
	
	public void setUserSession(UserSession us) {
		this.us = us;
	}
	
	@Override
    public boolean isDbReadOnly() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = conn.prepareStatement("SELECT c_last_id FROM "+prefixForQuery+"t_ids WHERE c_name=?");
			pst.setString(1, "mode");
			rs = pst.executeQuery();
			if (rs.next()) {
				long mode = rs.getLong(1);
				return mode != 1;
			}
		} catch (SQLException e) {
			log.error(e, e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
		}
		return true;
	}
	
	private String setCurTransUnit(String expr, long trId, List<Boolean> paramRespRegs){
    	if(!expr.contains("_CURTRANS")) return expr;
		String[] exprs=expr.split("\\?");
    	int j=0;
    	for(int i=0;i<exprs.length;i++,j++){
    		String[] expr_=exprs[i].split("_CURTRANS");
        	for(int k=0;k<expr_.length-1;k++,j++){
        		params.add(j,"_CURTRANS"+trId);
        		paramRespRegs.add(j, false);
        	}
    	}
    	return expr.replaceAll("_CURTRANS","?");
	}
	
	@Override
	public void setLoadingFile(boolean b) {
		this.isLoadingFile = b;
	}

	protected static String getString(ResultSet rs, int index) throws SQLException {
		return Funcs.normalizeInput(rs.getString(index));
	}

	protected static String getString(ResultSet rs, String name) throws SQLException {
		return Funcs.normalizeInput(rs.getString(name));
	}

	protected static String getSanitizedString(ResultSet rs, int index) throws SQLException {
		return Funcs.sanitizeSQL(rs.getString(index));
	}

	protected static String getSanitizedString(ResultSet rs, String name) throws SQLException {
		return Funcs.sanitizeSQL(rs.getString(name));
	}

	protected static byte[] getNormBytes(ResultSet rs, String name) throws SQLException {
		return Funcs.normalizeInput(rs.getBytes(name), "UTF-8");
	}
	
	public Map<String, String> getStringUidMap(String[] scopeUids){
		Map<String, String> result = new HashMap<String, String>();
		Statement pst = null;
		try {
			StringBuilder scope_uids = new StringBuilder();
			for(int i=0; i<scopeUids.length; i++) {
				if (i == 0)
					scope_uids.append('\'').append(scopeUids[i]).append('\'');
				else
					scope_uids.append(",'").append(scopeUids[i]).append('\'');
			}
			String sql = "SELECT c_search_str, c_obj_uid FROM " + getDBPrefix() + "t_search_indexes WHERE c_ext_field in ("
					+ scope_uids + ")";
			pst = conn.createStatement();
			ResultSet set = pst.executeQuery(sql);
			while(set.next()){
				result.put(set.getString(1), set.getString(2));
			}
			set.close();
		} catch (SQLException e) {
			log.error(e, e);
		} finally {
			DbUtils.closeQuietly(pst);
		}
		return result;
	}
	
	public void removeIndex(KrnObject obj) {
		PreparedStatement pst = null;
		try {
			String sql = "DELETE FROM " + getDBPrefix() + "t_search_indexes WHERE c_obj_uid = ?";
			pst = conn.prepareStatement(sql);
			pst.setString(1, obj.uid);
			pst.executeUpdate();
		} catch (SQLException e) {
			log.error(e, e);
		} finally {
			DbUtils.closeQuietly(pst);
		}
	}
	
	public void addIndex(String search_str, KrnObject obj, KrnObject balans_ed) {
		PreparedStatement pst = null;
		try {
			String sql = "SELECT COUNT(*) FROM " + getDBPrefix() + "t_search_indexes WHERE c_obj_uid = ? AND c_ext_field = ?";
			pst = conn.prepareStatement(sql);
			pst.setString(1, obj.uid);
			pst.setString(2, balans_ed.uid);
			ResultSet set = pst.executeQuery();
			boolean isExist = false;
			if (set.next()) {
				isExist = set.getInt(1) > 0;
			}
			set.close();
			pst.close();
			if(isExist) {
				sql = "UPDATE " + getDBPrefix() + "t_search_indexes SET c_search_str = ? WHERE c_obj_uid = ? AND c_ext_field = ?";
				pst = conn.prepareStatement(sql);
				pst.setString(1, search_str);
				pst.setString(2, obj.uid);
				pst.setString(3, balans_ed.uid);
				pst.executeUpdate();
			} else {
				sql = "INSERT INTO " + getDBPrefix() + "t_search_indexes (c_search_str, c_obj_uid, c_ext_field)"
						+ "VALUES (?, ?, ?)";
				pst = conn.prepareStatement(sql);
				pst.setString(1, search_str);
				pst.setString(2, obj.uid);
				pst.setString(3, balans_ed.uid);
				pst.executeUpdate();
			}
		} catch (SQLException e) {
			log.error(e, e);
		} finally {
			DbUtils.closeQuietly(pst);
		}
	}

	protected final String getDBPrefix() {
		return prefixForQuery;
	}
	
	// Если коннекция оборвалась, то пытается взять новую
	protected Connection getMutexConnection(String muid) throws DriverException {
		Connection mc = db.getMutexConnection(muid);
		if (mc == null) {
			mc = getNewConnection();
			db.setMutexConnection(muid, mc);
		}
		
		return mc;
	}
	
	// Попытка внести в таблицу t_mutex уникальное значение, тем самым блокировав другие сервера кластера.
	// Если же этот же сервер попытается вставить такое же значение, то вернет значение false
	// Если другой сервер попытается вставить такое же значение, то метод будет ожидать пока не закончит первый сервер.
	// После успешной вставки возвращается true
	public boolean lockMutex(String muid) throws DriverException {
		Connection c = db.getMutexConnection(muid);
		
		if (c != null) {
			log.error("Mutex '" + muid + "' уже используется данным сервером!");
		} else {
			c = getNewConnection();
			db.setMutexConnection(muid, c);

			PreparedStatement pst = null;
			try {
				pst = c.prepareStatement("INSERT INTO " + prefixForQuery + "t_mutex VALUES (?)");
				pst.setString(1, muid);
				int count = pst.executeUpdate();
				return count == 1;
			} catch (SQLException e) {
				log.error(e, e);
				db.destroyMutexConnection(muid);
	            throw new DriverException(e);
			} finally {
				DbUtils.closeQuietly(pst);
			}
		}
		return false;
	}

	// Удаляем уникальное значение из t_mutex, тем самым позволяя другим серверам кластера блокировать это значение
	// Возвращает true при успешной разблокировке
	public boolean unlockMutex(String muid) throws DriverException {
		Connection c = db.getMutexConnection(muid);

		if (c == null) {
			log.error("Mutex '" + muid + "' не был блокирован данным сервером!");
		} else {
			db.destroyMutexConnection(muid);
			return true;
		}
		return false;
	}
	public static boolean isLoggingGetObjSql() {
		return loggingGetObjSql;
	}
	public static void setLoggingGetObjSql(boolean logginGetObjSql) {
		Driver2.loggingGetObjSql = logginGetObjSql;
	}
	public boolean isDel(KrnObject obj, long trId)throws SQLException {
		boolean res = false;
		String tblName= getClassTableName(obj.classId);
		if (tblName != null && tblName.length() > 0) {
			PreparedStatement pst = null;
			try {
				String sql = "SELECT c_is_del FROM " +tblName + " WHERE c_obj_id=? AND c_tr_id=?" ;
				pst = conn.prepareStatement(sql);
				pst.setLong(1, obj.id);
				pst.setLong(2, trId);
				ResultSet set = pst.executeQuery();
				if(set.next()){
					res = set.getBoolean(1);
				}
				set.close();
			} catch (SQLException e) {
				log.error(e, e);
			} finally {
				DbUtils.closeQuietly(pst);
			}
		}
		return res;
	}
	
	public void createTableOrders() {
	}

	protected void addNotCommitedJrbNode(long attrId, Object nodeId) {
		String nodeIdStr = (nodeId instanceof byte[]) ? new String((byte[])nodeId) : (String)nodeId;
        notCommittedJrbNodes.add(new Pair<Long, String>(attrId, nodeIdStr));
    }
    
	protected void clearNotCommitedJrbNodes() {
		notCommittedJrbNodes.clear();
	}
	
	protected void deleteNotCommitedJrbNodes() throws DriverException {
		for (Pair<Long, String> node : notCommittedJrbNodes)
			db.deleteRepositoryData(node.first, node.second);
		
		notCommittedJrbNodes.clear();
	}

	protected void addRewrittenJrbNode(long attrId, Object nodeId) {
		String nodeIdStr = (nodeId instanceof byte[]) ? new String((byte[])nodeId) : (String)nodeId;
        rewrittenJrbNodes.add(new Pair<Long, String>(attrId, nodeIdStr));
    }
    
	protected void clearRewrittenJrbNodes() {
		rewrittenJrbNodes.clear();
	}
	
	protected void deleteRewrittenJrbNodes() throws DriverException {
		for (Pair<Long, String> node : rewrittenJrbNodes)
			db.deleteRepositoryData(node.first, node.second);
		
		rewrittenJrbNodes.clear();
	}
}
