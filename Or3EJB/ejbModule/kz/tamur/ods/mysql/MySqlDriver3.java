package kz.tamur.ods.mysql;

import static com.cifs.or2.kernel.ModelChange.ACTION_CREATE;
import static com.cifs.or2.kernel.ModelChange.ACTION_DELETE;
import static com.cifs.or2.kernel.ModelChange.ACTION_MODIFY;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_ATTRIBUTE;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_ATTR_TRIGGER_AFTER_CHANGE;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_ATTR_TRIGGER_AFTER_DELETE;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_ATTR_TRIGGER_BEFORE_CHANGE;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_ATTR_TRIGGER_BEFORE_DELETE;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_CLASS;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_CLS_TRIGGER_AFTER_CREATE;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_CLS_TRIGGER_AFTER_DELETE;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_CLS_TRIGGER_BEFORE_CREATE;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_CLS_TRIGGER_BEFORE_DELETE;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_INDEX;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_METHOD;
import static kz.tamur.comps.Constants.ATTR_BLUE_SYS_COLOR;
import static kz.tamur.comps.Constants.ATTR_CLIENT_VARIABLE_COLOR;
import static kz.tamur.comps.Constants.ATTR_COLOR_BACK_TAB_TITLE;
import static kz.tamur.comps.Constants.ATTR_COLOR_FONT_BACK_TAB_TITLE;
import static kz.tamur.comps.Constants.ATTR_COLOR_FONT_TAB_TITLE;
import static kz.tamur.comps.Constants.ATTR_COLOR_HEADER_TABLE;
import static kz.tamur.comps.Constants.ATTR_COLOR_MAIN;
import static kz.tamur.comps.Constants.ATTR_COLOR_TAB_TITLE;
import static kz.tamur.comps.Constants.ATTR_COMMENT_COLOR;
import static kz.tamur.comps.Constants.ATTR_DARK_SHADOW_SYS_COLOR;
import static kz.tamur.comps.Constants.ATTR_DEFAULT_FONT_COLOR;
import static kz.tamur.comps.Constants.ATTR_GRADIENT_CONTROL_PANEL;
import static kz.tamur.comps.Constants.ATTR_GRADIENT_FIELD_NO_FLC;
import static kz.tamur.comps.Constants.ATTR_GRADIENT_MAIN_FRAME;
import static kz.tamur.comps.Constants.ATTR_GRADIENT_MENU_PANEL;
import static kz.tamur.comps.Constants.ATTR_HISTORY_FLT;
import static kz.tamur.comps.Constants.ATTR_HISTORY_IFC;
import static kz.tamur.comps.Constants.ATTR_HISTORY_RPT;
import static kz.tamur.comps.Constants.ATTR_HISTORY_SRV;
import static kz.tamur.comps.Constants.ATTR_IS_OBJECT_BROWSER_LIMIT;
import static kz.tamur.comps.Constants.ATTR_IS_OBJECT_BROWSER_LIMIT_FOR_CLASSES;
import static kz.tamur.comps.Constants.ATTR_KEYWORD_COLOR;
import static kz.tamur.comps.Constants.ATTR_LIGHT_GREEN_COLOR;
import static kz.tamur.comps.Constants.ATTR_LIGHT_RED_COLOR;
import static kz.tamur.comps.Constants.ATTR_LIGHT_SYS_COLOR;
import static kz.tamur.comps.Constants.ATTR_LIGHT_YELLOW_COLOR;
import static kz.tamur.comps.Constants.ATTR_MID_SYS_COLOR;
import static kz.tamur.comps.Constants.ATTR_OBJECT_BROWSER_LIMIT;
import static kz.tamur.comps.Constants.ATTR_OBJECT_BROWSER_LIMIT_FOR_CLASSES;
import static kz.tamur.comps.Constants.ATTR_RED_COLOR;
import static kz.tamur.comps.Constants.ATTR_SHADOWS_GREY_COLOR;
import static kz.tamur.comps.Constants.ATTR_SHADOW_YELLOW_COLOR;
import static kz.tamur.comps.Constants.ATTR_SILVER_COLOR;
import static kz.tamur.comps.Constants.ATTR_SYS_COLOR;
import static kz.tamur.comps.Constants.ATTR_TRANSPARENT_BACK_TAB_TITLE;
import static kz.tamur.comps.Constants.ATTR_TRANSPARENT_CELL_TABLE;
import static kz.tamur.comps.Constants.ATTR_TRANSPARENT_DIALOG;
import static kz.tamur.comps.Constants.ATTR_TRANSPARENT_MAIN;
import static kz.tamur.comps.Constants.ATTR_TRANSPARENT_SELECTED_TAB_TITLE;
import static kz.tamur.comps.Constants.ATTR_VARIABLE_COLOR;
import static kz.tamur.comps.Constants.NAME_CLASS_CONFIG_GLOBAL;
import static kz.tamur.comps.Constants.NAME_CLASS_CONFIG_GLOBAL_FIX;
import static kz.tamur.comps.Constants.NAME_CLASS_CONFIG_LOCAL;
import static kz.tamur.comps.Constants.NAME_CLASS_CONFIG_OBJECT;
import static kz.tamur.comps.Constants.NAME_CLASS_CONTROL_FOLDER;
import static kz.tamur.comps.Constants.NAME_CLASS_CONTROL_FOLDER_ROOT;
import static kz.tamur.comps.Constants.NAME_CLASS_PROPERTY;
import static kz.tamur.ods.ComparisonOperations.CO_CONTAINS;
import static kz.tamur.ods.ComparisonOperations.CO_EQUALS;
import static kz.tamur.ods.ComparisonOperations.CO_EQUALS_IGNORE_CASE;
import static kz.tamur.ods.ComparisonOperations.CO_EQUALS_TRIM;
import static kz.tamur.ods.ComparisonOperations.CO_IS_NULL;
import static kz.tamur.ods.ComparisonOperations.SEARCH_START_WITH;
import static kz.tamur.or3.util.Tname.AttrAccessModVersionBD;
import static kz.tamur.or3.util.Tname.OrlangTrigersVersionBD2;
import static kz.tamur.or3.util.Tname.OrlangTrigersVersionBD3;
import static kz.tamur.or3.util.Tname.EncryptColumnsVersionBD;
import static kz.tamur.or3.util.Tname.TnameVersionBD;
import static kz.tamur.or3.util.Tname.getAttrFKName;
import static kz.tamur.or3.util.Tname.isVersion;
import static kz.tamur.or3.util.Tname.version;
import static kz.tamur.or3ee.common.SessionIds.CID_BLOB;
import static kz.tamur.or3ee.common.SessionIds.CID_BOOL;
import static kz.tamur.or3ee.common.SessionIds.CID_DATE;
import static kz.tamur.or3ee.common.SessionIds.CID_FLOAT;
import static kz.tamur.or3ee.common.SessionIds.CID_INTEGER;
import static kz.tamur.or3ee.common.SessionIds.CID_MEMO;
import static kz.tamur.or3ee.common.SessionIds.CID_STRING;
import static kz.tamur.or3ee.common.SessionIds.CID_TIME;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.cifs.or2.client.OrlangTriggerInfo;
import com.cifs.or2.kernel.AttrChange;
import com.cifs.or2.kernel.DataChanges;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnChangeCls;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnIndex;
import com.cifs.or2.kernel.KrnIndexKey;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnVcsChange;
import com.cifs.or2.kernel.ModelChanges;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.db.Database;
import com.cifs.or2.server.orlang.SrvOrLang;
import com.cifs.or2.server.sgds.HexStringOutputStream;
import com.cifs.or2.util.MMap;

import difflib.Chunk;
import difflib.Delta;
import difflib.Delta.TYPE;
import difflib.DiffUtils;
import difflib.Patch;
import kz.tamur.DriverException;
import kz.tamur.common.ErrorCodes;
import kz.tamur.comps.Constants;
import kz.tamur.comps.TriggerInfo;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.ods.AttrRequest;
import kz.tamur.ods.AttrRequestCache;
import kz.tamur.ods.AttributeChange;
import kz.tamur.ods.ClassChange;
import kz.tamur.ods.DataChange;
import kz.tamur.ods.Driver2;
import kz.tamur.ods.IndexChange;
import kz.tamur.ods.IndexKeyChange;
import kz.tamur.ods.Lock;
import kz.tamur.ods.LockMethod;
import kz.tamur.ods.MethodChange;
import kz.tamur.ods.ModelChange;
import kz.tamur.ods.ModelChangeProcessor;
import kz.tamur.ods.Toolkit;
import kz.tamur.ods.TriggerChange;
import kz.tamur.ods.Value;
import kz.tamur.ods.oracle.OracleDriver3;
import kz.tamur.ods.postgre.PgSqlDriver;
import kz.tamur.ods.sql92.AttrResultSetHandler;
import kz.tamur.ods.sql92.AttributeRsh;
import kz.tamur.ods.sql92.ChangeClsResultSetHandler;
import kz.tamur.ods.sql92.ClassResultSetHandler;
import kz.tamur.ods.sql92.IndexKeyResultSetHandler;
import kz.tamur.ods.sql92.IndexResultSetHandler;
import kz.tamur.or3.util.PathElement2;
import kz.tamur.or3.util.SystemAction;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.or3ee.common.AttrChangeListener;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.admin.ServerMessage;
import kz.tamur.rt.Utils;
import kz.tamur.server.indexer.Indexer;
import kz.tamur.util.Base64;
import kz.tamur.util.Funcs;
import kz.tamur.util.KrnUtil;
import kz.tamur.util.LongHolder;
import kz.tamur.util.MapMap;
import kz.tamur.util.Pair;
import kz.tamur.util.XmlUtil;

/**
 * Created by IntelliJ IDEA. User: berik Date: 13.01.2006 Time: 18:47:17 To
 * change this template use File | Settings | File Templates.
 */

@SuppressWarnings({"unchecked", "unused", "rawtypes"})
public class MySqlDriver3 extends Driver2 {
	
	protected static int MAX_JOINS = 61;
	
	protected static Map<String, List<KrnObject>>sysLangIds =
		new HashMap<String, List<KrnObject>>();

	private static final String ENGINE = "InnoDB";
	private static final String FULLTEXT="FULLTEXT";

	protected Log log , log_sql;

	protected int t_ind = 0;

	protected static final int defaultLimit = Integer.parseInt(System.getProperty("defaultLimit", "0"));
	protected static final String rendl = Constants.REOL;

	protected PreparedStatement pstObjById = null;
	private PreparedStatement pstObjByUid = null;
	private PreparedStatement pstExtBlobParamsById = null;
	private PreparedStatement pstUpdateExtBlobParams = null;
	
	protected PreparedStatement dataLogPst;
	protected List<String> reverseSQLs = new ArrayList<String>();
	
	protected int dataLogBatchCount;

	protected boolean isUpgrading = false;
	KrnClass sysCls;
	
	protected static final boolean throwOnSaveNull = Boolean.parseBoolean(System.getProperty("throwOnSaveNull", "false"));
	
	public MySqlDriver3(Database db, String dsName, UserSession us) throws DriverException {
		super(db, dsName, us);
		log = LogFactory.getLog(dsName + ".DatabaseLog." + us.getLogUserName() + (UserSession.SERVER_ID != null ? ("." + UserSession.SERVER_ID) : ""));
		log_sql = LogFactory.getLog(dsName + ".DatabaseLogSql." + us.getLogUserName() + (UserSession.SERVER_ID != null ? ("." + UserSession.SERVER_ID) : ""));
	}

	public KrnClass createClass(String name, long parentId, boolean isRepl,
			long id, String uid, String tname) throws DriverException {
		return createClass(name, parentId, isRepl, 0, id, uid, tname);
	}
	
	public KrnClass createClass(String name, long parentId, boolean isRepl,
			int mod, long id, String uid, String tname) throws DriverException {
		return createClass(name, parentId, isRepl, mod, id, uid, true, tname);
	}
	
	public KrnClass createClass(String name, long parentId, boolean isRepl,
			int mod, long id, String uid, boolean log, String tname) throws DriverException {
		try {
			if (tname != null && tname.trim().length() == 0){
				tname = null;
			}
			String sql = "";
            if (uid == null) {
                uid = UUID.randomUUID().toString();
            } else {
                if (db.getClassByUid(uid) != null) {
                    this.log.warn("Класс \""+name+"\" не создан, т.к. уже есть в БД. UUID:"+uid);
                    return db.getClassByUid(uid);
                }
            }
			if ( id > 0) {
				// Создаем запись в таблице классов
				if (isVersion(TnameVersionBD)) {
					sql = "INSERT INTO "+getDBPrefix()+"t_classes (c_id, c_name, c_parent_id, c_is_repl,c_mod, c_cuid, c_tname) VALUES (?,?,?,?,?,?,?)";
				} else {
					sql = "INSERT INTO "+getDBPrefix()+"t_classes (c_id, c_name, c_parent_id, c_is_repl,c_mod, c_cuid) VALUES (?,?,?,?,?,?)";
				}
				PreparedStatement pst = conn.prepareStatement(sql);
				pst.setLong(1, id);
				pst.setString(2, name);
				pst.setLong(3, parentId);
				pst.setBoolean(4, isRepl);
				pst.setInt(5, mod);
				pst.setString(6, uid);
				if (isVersion(TnameVersionBD)) {
					pst.setString(7, tname);
				}
				pst.executeUpdate();
				pst.close();
			} else {
				// Создаем запись в таблице классов
				if (isVersion(TnameVersionBD)) {
					sql = "INSERT INTO "+getDBPrefix()+"t_classes (c_name, c_parent_id, c_is_repl,c_mod, c_cuid, c_tname) VALUES (?,?,?,?,?,?)";
				} else {
					sql = "INSERT INTO "+getDBPrefix()+"t_classes (c_name, c_parent_id, c_is_repl,c_mod, c_cuid) VALUES (?,?,?,?,?)";
				}
				PreparedStatement pst = conn.prepareStatement(sql);
				pst.setString(1, name);
				pst.setLong(2, parentId);
				pst.setBoolean(3, isRepl);
				pst.setInt(4, mod);
				pst.setString(5, uid);
				if (isVersion(TnameVersionBD)) {
					pst.setString(6, tname);
				}
				pst.executeUpdate();
				pst.close();
				id = getLastClassId();
			}

			// Создаем записи в таблице рекурсивных связей родителей с детьми
			PreparedStatement pst = conn
					.prepareStatement("INSERT INTO "+getDBPrefix()+"t_clinks (c_parent_id,c_child_id)"
							+ " SELECT c_parent_id,? FROM "+getDBPrefix()+"t_clinks WHERE c_child_id=?");
			pst.setLong(1, id);
			pst.setLong(2, parentId);
			pst.executeUpdate();
			pst.close();
			pst = conn
					.prepareStatement("INSERT INTO "+getDBPrefix()+"t_clinks (c_parent_id,c_child_id)"
							+ " VALUES (?,?)");
			pst.setLong(1, id);
			pst.setLong(2, id);
			pst.executeUpdate();
			pst.close();

			// Создаем объект KrnClass ...
			KrnClass cls = new KrnClass(uid, id, parentId, isRepl, mod, name, tname, null, null, null, null, 0, 0, 0, 0);

			// Создаем запись в журнале изменения модели
			if (log && !isInstallDb) {
				logVcsModelChanges(ENTITY_TYPE_CLASS, ACTION_CREATE, cls, cls, null, conn);
	
				logModelChanges(ENTITY_TYPE_CLASS, ACTION_CREATE, uid, conn);
			}

			// Создаем таблицу для объектов класса
			if (id > 99 && (mod & 1) == 0) {
				Statement st = conn.createStatement();
				st.executeUpdate("CREATE TABLE IF NOT EXISTS "
								+ getClassTableName(cls, true)
								+ " (c_obj_id BIGINT NOT NULL,"
								+ "c_tr_id BIGINT NOT NULL,"
								+ "c_class_id BIGINT NOT NULL,"
								+ "c_uid CHAR(20),"
								+ "c_is_del BIT DEFAULT 0,"
								+ "PRIMARY KEY(c_obj_id,c_tr_id),"
								+ "INDEX(c_tr_id),"
								+ "CONSTRAINT uid" + id + "idx UNIQUE INDEX(c_uid,c_tr_id),"
								+ "FOREIGN KEY(c_class_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE)"
								+ " ENGINE=" + ENGINE);
				st.close();
			}

			// ... и кладем его в кэш
			db.addClass(cls, false);
			return cls;
        } catch (SQLException e) {
            int ia = e.getMessage().lastIndexOf("Duplicate entry");
            int ib = e.getMessage().lastIndexOf("for key");
            if (ia != -1 && ib != -1 && ia < ib) {
                this.log.error("Класс \"" + name + "\" не создан, т.к. уже есть в БД. UUID:" + uid);
                String column = e.getMessage().substring(ib + 9, e.getMessage().length() - 1);
                if (column.equalsIgnoreCase("c_name") || column.equalsIgnoreCase("c_name_UNIQUE")) {
                    throw new DriverException("C_NAME");
                } else if (column.equalsIgnoreCase("c_tname") || column.equalsIgnoreCase("c_tname_UNIQUE")) {
                    throw new DriverException("C_TNAME");
                }
            }
            throw convertException(e);
        }
	}
	
	private void updateTclinks(long id, long parentId) {
		try {
			KrnClass cls = db.getClassById(id);
			if(true) {
				PreparedStatement pst = conn
						.prepareStatement("DELETE FROM t_clinks WHERE c_child_id=? ");
				pst.setLong(1, cls.id);
				pst.executeUpdate();
				pst.close();
			}
			if(true) {
				PreparedStatement pst = conn
						.prepareStatement("INSERT INTO "+getDBPrefix()+"t_clinks (c_parent_id,c_child_id)"
								+ " SELECT c_parent_id,? FROM "+getDBPrefix()+"t_clinks WHERE c_child_id=?");
				pst.setLong(1, id);
				pst.setLong(2, parentId);
				pst.executeUpdate();
				pst.close();
				pst = conn
						.prepareStatement("INSERT INTO "+getDBPrefix()+"t_clinks (c_parent_id,c_child_id)"
								+ " VALUES (?,?)");
				pst.setLong(1, id);
				pst.setLong(2, id);
				pst.executeUpdate();
				pst.close();
			}
			Statement st = conn.createStatement();
			String sql = "select c_id from t_classes where c_parent_id =" + id;
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()) {
				long cId = rs.getLong("c_id");
				KrnClass clss = db.getClassById(cId);
				long cParentId = clss.parentId;
				updateTclinks(cId, cParentId);
			}
			st.close();
		} catch (Exception e) {
            e.printStackTrace();
        } 
	}
	
    public KrnClass changeClass(long id, long parentId, String name, boolean isRepl) throws DriverException {
    		return changeClass(id, parentId, name, isRepl,true);
    }
    private KrnClass changeClass(long id, long parentId, String name, boolean isRepl,boolean isVcsLog) throws DriverException {
        KrnClass cls = db.getClassById(id);
        /*
        if (parentId != cls.parentId) {
            // Реализация смены суперкласса еще прорабатывается.
            // Пока необходимо удалять класс и создавать новый.
            throw new DriverException("Смена суперкласса не реализована");
        }
		*/
        /*boolean tnameChanged=false;
		String sql = "";
		if (isVersion(TnameVersionBD) && tname!= null && tname.trim().length() != 0){
			if (cls.tname == null || !tname.toLowerCase().equals(cls.tname.toLowerCase())) {
				ResultSet rs = null;
				PreparedStatement pst=null;
				try {
					pst = conn.prepareStatement("SELECT * FROM "+getDBPrefix()+"t_classes where c_tname=? and c_id<>?");
					pst.setString(1, tname);
					pst.setLong(2, id);
					rs = pst.executeQuery();
					if (rs.next()) {
						throw new DriverException("TNAME");
					}
				} catch (Exception e) {
					throw new DriverException("TNAME");
				} finally {
					DbUtils.closeQuietly(rs);
					DbUtils.closeQuietly(pst);
				}
			}
			
        	//переименовываем таблицу
			if (!tname.equals(cls.tname)){
				if (!renameClassTable(cls.id, tname)){
					throw new DriverException("TNAME");
				}
				tnameChanged = true;
			}
		}*/
        KrnClass cls_=new KrnClass(cls.uid,cls.id,cls.parentId,cls.isRepl,cls.modifier,cls.name,cls.tname,cls.beforeCreateObjExpr
        							,cls.afterCreateObjExpr,cls.beforeDeleteObjExpr,cls.afterDeleteObjExpr,cls.beforeCreateObjTr
        							,cls.afterCreateObjTr,cls.beforeDeleteObjTr,cls.afterDeleteObjTr);
        try {
            // Вносим изменения в таблицу классов
            PreparedStatement pst = conn .prepareStatement("UPDATE "+getDBPrefix()+"t_classes SET c_name=?, c_parent_id=?, c_is_repl=? WHERE c_id=?");
            pst.setString(1, name);
            pst.setLong(2, parentId);
            pst.setBoolean(3, isRepl);
            pst.setLong(4, id);
            pst.executeUpdate();
            pst.close();
            try {
                // Обновляем кэши
                db.removeClass(cls);
                // Изменяем объект KrnClass
                cls.name = name;
                cls.parentId = parentId;
                cls.isRepl = isRepl;
            } catch (Exception e) {
                throw new DriverException(e.getMessage());
            } finally {
                db.addClass(cls, false);
            }
            // Создаем запись в журнале изменения модели
            if(isVcsLog) {
				logVcsModelChanges(ENTITY_TYPE_CLASS, ACTION_MODIFY,cls_, cls, null, conn);
	
				logModelChanges(ENTITY_TYPE_CLASS, ACTION_MODIFY, cls.uid, conn);
            }
            updateTclinks(id, parentId);
            return cls;
        } catch (SQLException e) {
            int ia = e.getMessage().lastIndexOf("Duplicate entry");
            int ib = e.getMessage().lastIndexOf("for key");
            if (ia != -1 && ib != -1 && ia < ib) {
                String column = e.getMessage().substring(ib + 9, e.getMessage().length() - 1);
                if (column.equalsIgnoreCase("c_name") || column.equalsIgnoreCase("c_name_UNIQUE")) {
                    throw new DriverException("C_NAME");
                } else if (column.equalsIgnoreCase("c_tname") || column.equalsIgnoreCase("c_tname_UNIQUE")) {
                    throw new DriverException("C_TNAME");
                }
            }
            throw convertException(e);
        }
    }

	public void deleteClass(long id) throws DriverException {
		deleteClass(id,true);
	}
	private void deleteClass(long id,boolean isVcsLog) throws DriverException {

		// Удаляется класс вместе со всеми его подклассами
		List<KrnClass> classes = new ArrayList<KrnClass>();
		db.getSubClasses(id, true, classes);
		KrnClass curCls=db.getClassById(id);
		classes.add(curCls);

		PreparedStatement pst = null;
		try {
			for (Object aClass : classes) {
				KrnClass cls = (KrnClass) aClass;
				try {
                    // Создаем запись в журнале изменения модели
					if(isVcsLog) {
						logVcsModelChanges(ENTITY_TYPE_CLASS, ACTION_DELETE, cls, cls, null, conn);
			            
	                    logModelChanges(ENTITY_TYPE_CLASS, ACTION_DELETE, cls.uid, conn);
					}
                    
					List<KrnAttribute> attrs = db.getAttributesByTypeId(cls.id,	false);
					attrs.addAll(db.getAttributesByClassId(cls.id, false));
					for (Object attr1 : attrs) {
						KrnAttribute attr = (KrnAttribute) attr1;
						deleteAttribute(attr.id);
					}
				} catch (DriverException e) {
					log.error(e, e);
					if (e.getMessage().indexOf("not found") == -1)
						throw new DriverException(e.getMessage(), e
								.getErrorCode());
					else
						return;
				}
				
				// Удаляем таблицу для объектов класса
				// TODO и таблицы для всех его множественных связей
				Statement st = null;
				String tname = getClassTableName(cls.id);
				try {
					st = conn.createStatement();
					st.executeUpdate("DROP TABLE " + tname);
				} catch (SQLException e) {
					this.log.warn("Table '" + tname + "' not found!!!");
				} finally {
					DbUtils.closeQuietly(st);
				}

				// Удаляем запись из таблицы классов
				pst = conn.prepareStatement("DELETE FROM "+getDBPrefix()+"t_classes WHERE c_id=?");
				pst.setLong(1, cls.id);
				pst.executeUpdate();
				pst.close();

				// Удаляем класс из кэша
				db.removeClass(cls);
			}

		} catch (SQLException e) {
			//throw convertException(e);
			log.error(e.getMessage());
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(pst);
		}
	}

	public List<KrnClass> getAllClasses() throws DriverException {
		try {
			ResultSetHandler rh = new ResultSetHandler() {
				public Object handle(ResultSet set) throws SQLException {
					List<KrnClass> res = new ArrayList<KrnClass>();
					while (set.next()) {
						long id = set.getLong("c_id");
						String uid = getSanitizedString(set, "c_cuid").trim();
						String name = getString(set, "c_name");
						long parentId = set.getLong("c_parent_id");
						boolean isRepl = set.getBoolean("c_is_repl");
						int mod = set.getInt("c_mod");
						String tname = null;
			            if (isVersion(TnameVersionBD)) {
			            	tname = getSanitizedString(set, "c_tname");
			            }
			            byte[] beforeCreateObjExpr = isVersion(OrlangTrigersVersionBD2) ? getNormBytes(set, "c_before_create_obj") : null;
			            byte[] afterCreateObjExpr = isVersion(OrlangTrigersVersionBD2) ? getNormBytes(set, "c_after_create_obj") : null;
			            byte[] beforeDeleteObjExpr = isVersion(OrlangTrigersVersionBD2) ? getNormBytes(set, "c_before_delete_obj") : null;
			            byte[] afterDeleteObjExpr = isVersion(OrlangTrigersVersionBD2) ? getNormBytes(set, "c_after_delete_obj") : null;
			            int beforeCreateObjTr = isVersion(OrlangTrigersVersionBD3) ? set.getInt("c_before_create_obj_tr") : 0;
			            int afterCreateObjTr = isVersion(OrlangTrigersVersionBD3) ? set.getInt("c_after_create_obj_tr") : 0;
			            int beforeDeleteObjTr = isVersion(OrlangTrigersVersionBD3) ? set.getInt("c_before_delete_obj_tr") : 0;
			            int afterDeleteObjTr = isVersion(OrlangTrigersVersionBD3) ? set.getInt("c_after_delete_obj_tr") : 0;

			        	res.add(new KrnClass(uid, id, parentId, isRepl, mod, name, tname, beforeCreateObjExpr, afterCreateObjExpr, beforeDeleteObjExpr, afterDeleteObjExpr, beforeCreateObjTr, afterCreateObjTr, beforeDeleteObjTr, afterDeleteObjTr));
					}
					return res;
				}
			};
			QueryRunner qr = new QueryRunner(true);
			return (List<KrnClass>) qr.query(conn, "SELECT * FROM "+getDBPrefix()+"t_classes", rh);
		} catch (SQLException e) {
			throw convertException(e);
		}
	}
	
	@Override
	public KrnAttribute createAttribute(
			long id,
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
    ) throws DriverException {
		return createAttribute(id, uid, classId, typeId, name, collectionType, isUnique, isIndexed, isMultilingual, isRepl, size, flags, rAttrId, sAttrId, sDesc, tname, accessModifier, false);
	}

	@Override
	public KrnAttribute createAttribute(
			long id,
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
	) throws DriverException {
		return createAttribute(id, uid, classId, typeId, name, collectionType,
				isUnique, isIndexed, isMultilingual, isRepl, size, flags,
				rAttrId, sAttrId, sDesc, true, tname, accessModifier, isEncrypt);
	}
	
	@Override
	public KrnAttribute createAttribute(
			long id,
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
			boolean log,
			String tname,
			int accessModifier
	) throws DriverException {
		return createAttribute(id, uid, classId, typeId, name, collectionType, isUnique, isIndexed, isMultilingual, isRepl, size, flags, rAttrId, sAttrId, sDesc, log, tname, accessModifier, false);
	}
	
	@Override
	public KrnAttribute createAttribute(
			long id,
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
			boolean log,
			String tname,
			int accessModifier,
			boolean isEncrypt
	) throws DriverException {
		uid = Funcs.sanitizeSQL(uid);
		
		KrnClass cls = db.getClassById(classId);
		KrnClass type = db.getClassById(typeId);
		
		if (tname != null && tname.trim().length() == 0){
			tname = null;
		}
		// устанавливаем индекс на объектном атрибуте.
		if (typeId >= 99) {
			isIndexed = true;
		}

		try {
			// Создаем запись в таблице атрибутов
			if (uid == null)
				uid = UUID.randomUUID().toString();
			String sql;
            String ATname = "";
            String qATname = "";
            if (isVersion(TnameVersionBD)){
            	ATname = ",c_tname";
            	qATname = ",?";
            }
            String AAccessMod = "";
            String qAAccessMod = "";
            if (isVersion(AttrAccessModVersionBD)){
            	AAccessMod = ",c_access_modifier";
            	qAAccessMod = ",?";
            }
            String AIsEncrypt = "";
            String qAIsEncrypt = "";
            if (isVersion(EncryptColumnsVersionBD)){
            	AIsEncrypt = ",c_is_encrypt";
            	qAIsEncrypt = ",?";
            }
            if (id != -1) {
                sql = "INSERT INTO "+getDBPrefix()+"t_attrs (c_class_id,c_name,c_type_id,"
                        + "c_col_type,c_is_unique,c_is_indexed,"
                        + "c_is_multilingual,c_is_repl,c_size,c_flags,"
                        + "c_rattr_id,c_sattr_id,c_sdesc,c_auid,c_id"+ATname+AAccessMod+AIsEncrypt+")"
                        + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"+qATname+qAAccessMod+qAIsEncrypt+")";
            } else {
                sql = "INSERT INTO "+getDBPrefix()+"t_attrs (c_class_id,c_name,c_type_id,"
                        + "c_col_type,c_is_unique,c_is_indexed,"
                        + "c_is_multilingual,c_is_repl,c_size,c_flags,"
                        + "c_rattr_id,c_sattr_id,c_sdesc,c_auid"+ATname+AAccessMod+AIsEncrypt+")"
                        + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?"+qATname+qAAccessMod+qAIsEncrypt+")";
            }
            PreparedStatement pst = conn.prepareStatement(sql);
			try {
	            int Nval = 0;
	            pst.setLong(++Nval, classId);
	            pst.setString(++Nval, name);
	            pst.setLong(++Nval, typeId);
	            pst.setInt(++Nval, collectionType);
	            pst.setBoolean(++Nval, isUnique);
	            pst.setBoolean(++Nval, isIndexed);
	            pst.setBoolean(++Nval, isMultilingual);
	            pst.setBoolean(++Nval, isRepl);
	            pst.setInt(++Nval, size);
	            pst.setLong(++Nval, flags);
				if (rAttrId != 0) {
					pst.setLong(++Nval, rAttrId);
				} else {
					pst.setNull(++Nval, Types.BIGINT);
				}
				if (sAttrId != 0) {
					pst.setLong(++Nval, sAttrId);
				} else {
					pst.setNull(++Nval, Types.BIGINT);
				}
	            pst.setBoolean(++Nval, sDesc);
	            pst.setString(++Nval, uid);
				if (id != -1) {
					pst.setLong(++Nval, id);
				}
				if (isVersion(TnameVersionBD)){
					pst.setString(++Nval, tname);
				}
				if (isVersion(AttrAccessModVersionBD)){
					pst.setInt(++Nval, accessModifier);
				}
				if (isVersion(EncryptColumnsVersionBD)){
		            pst.setBoolean(++Nval, isEncrypt);
				}
				pst.executeUpdate();
				if (id == -1) {
					id = getLastAttributeId();
				}
			} finally {
				pst.close();
			}
			
			if (id == -1) {
				id = getAttributeIdByUID(uid);
			}
			
			addReverseSQL("DELETE FROM "+getDBPrefix()+"t_attrs WHERE c_auid = '" + uid + "'");

			// Создаем объект KrnAttribute
			KrnAttribute attr = KrnUtil.createAttribute(
					uid, id, name, classId, typeId, collectionType, isUnique,
					isMultilingual, isIndexed, size, flags, isRepl,
					rAttrId, sAttrId, sDesc, tname, null, null, null, null, 0, 0, 0, 0, accessModifier, isEncrypt);

			// Обновляем схему БД
			createAttributeInDatabase(attr);

			if (log && !isInstallDb) {
				// Создаем запись в журнале изменения модели
				logVcsModelChanges(ENTITY_TYPE_ATTRIBUTE, ACTION_CREATE, attr, attr, null, conn);

				logModelChanges(ENTITY_TYPE_ATTRIBUTE, ACTION_CREATE, attr.uid, conn);
			}

			// Добавляем новый атрибут в кэш
			db.addAttribute(attr, false);

			return attr;

		} catch (SQLException e) {
		    this.log.error("Атрибут \"" + name + "\" не создан. UUID:" + uid);
			throw convertException(e);
		}
	}
	
	protected void createAttributeInDatabase(KrnAttribute attr)
	throws SQLException, DriverException{
		if (attr.id < 3)
			return;
		
		boolean isArray = attr.collectionType == COLLECTION_ARRAY;
		boolean isSet = attr.collectionType == COLLECTION_SET;
		Statement st = null;
		try {
			if (attr.rAttrId == 0) {
				if (!isSet && !isArray) {
					// Добавляем колонку в таблицу для объектов класса
					st = conn.createStatement();
					StringBuffer sb = new StringBuffer();
					sb.append("ALTER TABLE ");
					sb.append(getClassTableName(attr.classId));
					sb.append(" ADD ");
					if (attr.isMultilingual) {
						// Создаем колонки для каждого системного языка
						int langCount = sysLangCount > 0 ? sysLangCount : getSystemLangs().size();
						for (int i = 0; i < langCount; i++) {
							if (i > 0)
								sb.append(",ADD ");
							sb.append(getColumnDef(attr, i + 1));
						}
					} else {
						sb.append(getColumnDef(attr, 0));
					}
					if (attr.typeClassId > 10) {
						sb.append(", ADD ");
						sb.append(getKeyDef(attr, false));
					}
					st.executeUpdate(sb.toString());
					st.close();
				} else {
					// Создаем таблицу для атрибута
					String atName = getAttrTableName(attr);
					StringBuffer sb = new StringBuffer();
					sb.append("CREATE TABLE IF NOT EXISTS ");
					sb.append(atName);
					sb.append(" (c_obj_id BIGINT NOT NULL,c_tr_id BIGINT NOT NULL,");
					if (isArray) {
						sb.append("c_index INTEGER NOT NULL,");
						sb.append("c_id BIGINT NOT NULL,");
					}
					sb.append("c_del BIGINT DEFAULT 0,");
					if (attr.isMultilingual) {
						// Создаем колонки для каждого системного языка
						int langCount = sysLangCount > 0 ? sysLangCount : getSystemLangs().size();
						for (int i = 0; i < langCount; i++) {
							if (i > 0)
								sb.append(",");
							sb.append(getColumnDef(attr, i + 1));
						}
					} else {
						sb.append(getColumnDef(attr, 0));
					}
					sb.append("," + "PRIMARY KEY(c_obj_id,c_tr_id");
					if (isArray) {
						sb.append(",c_index");
					}
					if (isSet) {
						sb.append(",");
						if (attr.isMultilingual)
							sb.append(getColumnName(attr, 1));
						else
							sb.append(getColumnName(attr, 0));
					}
					sb.append(",c_del), CONSTRAINT ").append(getAttrFKName(attr));
					sb.append(" FOREIGN KEY (c_obj_id,c_tr_id) REFERENCES ");
					sb.append(getClassTableName(attr.classId));
					sb.append(" (c_obj_id,c_tr_id) ON DELETE CASCADE ON UPDATE CASCADE");
					if (attr.typeClassId > 10)
						sb.append(",").append(getKeyDef(attr, isSet));
					sb.append(") ENGINE=" + ENGINE);
					log.info(sb.toString());
					st = conn.createStatement();
					st.executeUpdate(sb.toString());
					st.close();
				}
	
				// Создаем индекс. Для FK в MySQL индекс создается автоматически
				if (attr.isIndexed && attr.typeClassId < 99) {
					updateIndex(attr, true);
				}
			} else if (isArray) {
				// Добавляем колонку в таблицу типа
				//TODO Рассмотреть необходимость в индексе
				String ctName = getClassTableName(attr.typeClassId);
				String cmiName = getRevIndexColumnName(attr.id);
				st = conn.createStatement();
				st.executeUpdate("ALTER TABLE " + ctName + " ADD " + cmiName + " INTEGER NOT NULL");
				st.close();
			}
		} catch (SQLException e) {
			log.error(e, e);
			throw e;
		} catch (DriverException e) {
			log.error(e, e);
			throw e;
		} finally {
			DbUtils.closeQuietly(st);
		}
	}

	protected void updateForeignKeyInDatabase(KrnAttribute attr)
			throws SQLException, DriverException{
		if (attr.id < 3)
			return;
		
		boolean isArray = attr.collectionType == COLLECTION_ARRAY;
		boolean isSet = attr.collectionType == COLLECTION_SET;
		
		if (attr.rAttrId == 0) {
			if (isSet || isArray) {
				// Создаем таблицу для атрибута
				String atName = getAttrTableName(attr);
				StringBuffer sb = new StringBuffer();
				sb.append("ALTER TABLE ");
				sb.append(atName);
				sb.append(" ADD CONSTRAINT ").append(getAttrFKName(attr));
				sb.append(" FOREIGN KEY (c_obj_id,c_tr_id) REFERENCES ");
				sb.append(getClassTableName(attr.classId));
				sb.append(" (c_obj_id,c_tr_id) ON DELETE CASCADE ON UPDATE CASCADE");
				if (attr.typeClassId > 10)
					sb.append(",").append(getKeyDef(attr, isSet));
				sb.append(") ENGINE=" + ENGINE);
				log.info(sb.toString());
				Statement st = conn.createStatement();
				st.executeUpdate(sb.toString());
				st.close();
			}

			// Создаем индекс. Для FK в MySQL индекс создается автоматически
			if (attr.isIndexed && attr.typeClassId < 99) {
				updateIndex(attr, true);
			}
		} else if (isArray) {
			// Добавляем колонку в таблицу типа
			//TODO Рассмотреть необходимость в индексе
			String ctName = getClassTableName(attr.typeClassId);
			String cmiName = getRevIndexColumnName(attr.id);
			Statement st = conn.createStatement();
			st.executeUpdate("ALTER TABLE " + ctName + " ADD " + cmiName + " INTEGER NOT NULL");
			st.close();
		}
	}

	@Override
	public KrnAttribute changeAttribute(
			long id,
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
	) throws DriverException {
		return 	changeAttribute(id,typeId,name,collectionType,isUnique,isIndexed,isMultilingual,isRepl,size,flags,rAttrId,sAttrId,sDesc,true,tname, accessModifier, false);
	}

	public KrnAttribute changeAttribute(
			long id,
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
			boolean needLog,
			String tname,
			int accessModifier
	) throws DriverException {
		return 	changeAttribute(id,typeId,name,collectionType,isUnique,isIndexed,isMultilingual,isRepl,size,flags,rAttrId,sAttrId,sDesc,needLog,tname, accessModifier, false);
	}
	
	@Override
	public KrnAttribute changeAttribute(
			long id,
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
	) throws DriverException {
		return 	changeAttribute(id,typeId,name,collectionType,isUnique,isIndexed,isMultilingual,isRepl,size,flags,rAttrId,sAttrId,sDesc,true,tname, accessModifier, isEncrypt);

	}
	public KrnAttribute changeAttribute(
			long id,
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
			boolean needLog,
			String tname,
			int accessModifier,
			boolean isEncrypt
	) throws DriverException {
		boolean tnameChanged = false;
		KrnAttribute attr = db.getAttributeById(id);

		if (collectionType != attr.collectionType) {
			if (attr.collectionType != COLLECTION_ARRAY
					|| collectionType != COLLECTION_SET) {
				throw new DriverException("Смена типа коллекции не реализована");
			}
		}

		if (isMultilingual != attr.isMultilingual) {
			throw new DriverException("Смена мультиязычности не реализована");
		}
		
		String sql = "";
		if (isVersion(TnameVersionBD) && tname!= null && tname.trim().length() != 0){
			if (attr.tname == null || !tname.toLowerCase(Constants.OK).equals(attr.tname.toLowerCase(Constants.OK))) {
				PreparedStatement st = null;
				ResultSet rs = null;
				try {
					sql = "SELECT * FROM "+getDBPrefix()+"t_attrs where c_tname=? and c_id<>? and (c_class_id=? or (c_class_id<>? and c_col_type<>0))";
					st = conn.prepareStatement(sql);
					st.setString(1, tname);
					st.setLong(2, id);
					st.setLong(3, attr.classId);
					st.setLong(4, attr.classId);
					rs = st.executeQuery();
					if (rs.next()) {
						throw new DriverException("TNAME");
					}
				} catch (Exception e) {
					throw new DriverException("TNAME");
				} finally {
					DbUtils.closeQuietly(rs);
					DbUtils.closeQuietly(st);
				}
			}
			
			if (!tname.equals(attr.tname)){
				if (!renameAttrTable(attr, tname)){
					throw new DriverException("TNAME");
				}
				tnameChanged = true;
			}
		}
		
		if (name != null && name.trim().length() != 0){
			if (attr.name == null || !name.toLowerCase(Constants.OK).equals(attr.name.toLowerCase(Constants.OK))) {
				boolean iscorrect = true;
				PreparedStatement st = null;
				ResultSet rs = null;
				try {
					sql = "SELECT * FROM "+getDBPrefix()+"t_attrs where c_name=? AND c_class_id=?";  
					st = conn.prepareStatement(sql);
					st.setString(1, name);
					st.setLong(2, attr.classId);
					rs = st.executeQuery();
					if (rs.next()) {
						iscorrect = false;
					}
				} catch (Exception e) {
					//
				} finally {
					DbUtils.closeQuietly(rs);
					DbUtils.closeQuietly(st);
				}
				if (!iscorrect){
					throw new DriverException("NAME");
				}
			}
		}

		// Не даем убирать индекс с объектных атрибутов
		if (attr.typeClassId >= 99) {
			isIndexed = true;
		}
		KrnAttribute attr_= KrnUtil.createAttribute(attr.uid, attr.id, attr.name, attr.classId, typeId, attr.collectionType, attr.isUnique, attr.isMultilingual, attr.isIndexed,
				attr.size, attr.flags, attr.isRepl, attr.rAttrId, attr.sAttrId,attr.sDesc, attr.tname, attr.beforeEventExpr, attr.afterEventExpr, attr.beforeDelEventExpr, attr.afterDelEventExpr, attr.beforeEventTr,
				attr.afterEventTr, attr.beforeDelEventTr, attr.afterDelEventTr, attr.accessModifierType, attr.isEncrypt);
        if (typeId != attr.typeClassId || size !=attr.size || isIndexed !=attr.isIndexed || (isIndexed && attr.isFullText()!=((flags & KrnAttribute.FULLTEXT)>0))) {
			if (typeId == PC_STRING) {
				attr_.size = size;
				if ((flags & KrnAttribute.FULLTEXT) > 0 && !isIndexed)
					flags ^= KrnAttribute.FULLTEXT;
			}
            attr_.flags=flags;
            attr_.isEncrypt=isEncrypt;
            String tName="";
            String cName= getColumnName(attr);
            if (attr.collectionType == COLLECTION_NONE) {
        		tName = getClassTableName(attr.classId);
            } else {
                tName = getAttrTableName(attr);
            }
            String cType=getSqlTypeName(attr);
            String cType_=getSqlTypeName(attr_);
            QueryRunner qr = new QueryRunner(true);
            try{
                //Удаляем индекс если он есть
    			if (attr.isIndexed && attr.typeClassId != PC_BLOB) {
    				updateIndex(attr, false);
    				attr.isIndexed=false;
    				attr.setFullText(attr_.isFullText());
    			}
                if (typeId != attr.typeClassId) {
                    //Удаляем старую колонку
                    sql = "ALTER TABLE "+tName+" DROP COLUMN "+cName;
                    qr.update(conn, sql);
                    //Создаем новую колонку с прежним именем и с новым типом
                    sql = "ALTER TABLE "+tName+" ADD "+cName+" "+cType_;
                    qr.update(conn, sql);
                } else if (!cType.equals(cType_)) {
                	boolean isChangeAttr=true;
               		int size_= getRealSizeColumn(attr);
               		if(size_>=size) {
               			//Если реальный размер колонки больше того, на который пытаются изменить,
               			//то в этом случае отсается реальный размер
                    	log.warn("Реальный размер атрибута '"+size_+"' больше того, на который хотят изменить'"+size+"', поэтому остается прежний размер!");
               			size=size_;
               			attr_.size=size;
               			isChangeAttr=false;
              		}
               		if(isChangeAttr) {//Если размер колонки не меняется, то и менять нечего
	    				if (attr.isMultilingual){
	    					List<KrnObject> langs = getSystemLangs();
	    					for (KrnObject lang : langs) {
	    						cName = getColumnName(attr, lang.id);
	    						sql = "ALTER TABLE "+tName+" MODIFY "+cName+" "+cType_;
	    						qr.update(conn, sql);
	    					}
						} else {
							sql = "ALTER TABLE " + tName + " MODIFY " + cName + " " + cType_;
							qr.update(conn, sql);
						}
               		}
                }
            } catch (SQLException e) {
    			throw convertException(e);
            }
        }
        
		if ((isIndexed != attr.isIndexed || (isIndexed && attr.isFullText() != (flags & KrnAttribute.FULLTEXT) > 0))
				&& (typeId == PC_BLOB || typeId == PC_MBLOB) && (attr.typeClassId == PC_BLOB || attr.typeClassId == PC_MBLOB)) {
			needLog = false;
		}

    	if((flags & KrnAttribute.FULLTEXT)>0 && !isIndexed)
			flags ^= KrnAttribute.FULLTEXT;

		try {
			sql = "UPDATE "+getDBPrefix()+"t_attrs SET c_name=?,c_type_id=?,"
					+ "c_col_type=?,c_is_unique=?,c_is_indexed=?,"
					+ "c_is_multilingual=?,c_is_repl=?,c_size=?,c_flags=?,"
					+ "c_rattr_id=?,c_sattr_id=?,c_sdesc=?"
					+ (version>=AttrAccessModVersionBD?",c_access_modifier=?":"")
					+ (version>=EncryptColumnsVersionBD?",c_is_encrypt=?":"")
					+ " WHERE c_id=?";
			Object[] params;
			if(version>=EncryptColumnsVersionBD)
				params = new Object[] {name, typeId, collectionType, isUnique,
								isIndexed, isMultilingual, isRepl, size, flags,
								rAttrId == 0 ? null : rAttrId,
								sAttrId == 0 ? null : sAttrId, sDesc, accessModifier, isEncrypt, id };

			else if(version>=AttrAccessModVersionBD)
					params = new Object[] {name, typeId, collectionType, isUnique,
									isIndexed, isMultilingual, isRepl, size, flags,
									rAttrId == 0 ? null : rAttrId,
									sAttrId == 0 ? null : sAttrId, sDesc, accessModifier, id };

			else
				params = new Object[] {name, typeId, collectionType, isUnique,
								isIndexed, isMultilingual, isRepl, size, flags,
								rAttrId == 0 ? null : rAttrId,
								sAttrId == 0 ? null : sAttrId, sDesc, id };
			QueryRunner qr = new QueryRunner(true);
			qr.update(conn, sql, params);

			// Создаем индекс, если необходимо.
			if (isIndexed != attr.isIndexed) {
				updateIndex(attr, isIndexed);
			}
			
			// Запоминаем обязательность
			boolean isMandatory = attr.isMandatory();

			// Удаляем атрибут из кэша
			db.removeAttribute(attr);
			
			attr.name = name;
			attr.typeClassId = typeId;
			attr.collectionType = collectionType;
			attr.isUnique = isUnique;
			attr.isIndexed = isIndexed;
			attr.isMultilingual = isMultilingual;
			attr.isRepl = isRepl;
			attr.size = size;
			attr.flags = flags;
			attr.rAttrId = rAttrId;
			attr.sAttrId = sAttrId;
			attr.sDesc = sDesc;
			attr.accessModifierType = accessModifier;
			attr.isEncrypt = isEncrypt;
			if (tnameChanged) attr.tname = tname;

			// Добавляем обновленный атрибут в кэш
			db.addAttribute(attr, false);

			// Если изменилась обязательность, то обновляем триггеры
			if (attr.isMandatory() != isMandatory)
					updateTriggers(attr.classId);

			// Создаем запись в журнале изменения модели
			if (needLog) {
				// Создаем запись в журнале изменения модели
				logVcsModelChanges(ENTITY_TYPE_ATTRIBUTE, ACTION_MODIFY,attr_, attr, null, conn);
				logModelChanges(ENTITY_TYPE_ATTRIBUTE, ACTION_MODIFY, attr.uid, conn);
			}

			return attr;

		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	protected String getDropIndexSql(String tname, String idxName) {
		return "ALTER TABLE " + tname + " DROP INDEX " + Funcs.sanitizeSQL(idxName);
	}

	protected String getDropForeignKeySql(String tname, String fkname) {
		return "ALTER TABLE " + tname + " DROP FOREIGN KEY " + Funcs.sanitizeSQL(fkname);
	}

	protected String getDropColumnSql(String tname, String cname) {
		return "ALTER TABLE " + tname + " DROP " + Funcs.sanitizeSQL(cname);
	}

	@Override
	public String getDropTableSql(String tname) {
		return "DROP TABLE " + getDBPrefix() + "`" + tname + "`";
	}
	public List<KrnAttribute> getDependAttrs(long id) throws DriverException {
		AttributeRsh rh = new AttributeRsh();
		QueryRunner qr = new QueryRunner(true);
		try {
			return qr.query(conn, "SELECT * FROM "+getDBPrefix()+"t_attrs WHERE c_sattr_id="+id, rh);
		} catch (SQLException e) {
			throw convertException(e);
		}
	}
	public void deleteAttribute(long id) throws DriverException {
		deleteAttribute(id, true, version);
	}
	
	protected void deleteAttribute(long id, boolean log, long dbVer) throws DriverException {

		KrnAttribute attr = db.getAttributeById(id);

		try {
			if (dbVer > 10) {
				//Удаляем многостолбцовые индексы, в которых участвует атрибут
				List<KrnIndex> ndxs = getIndexesByAttributeId(id);
				for(KrnIndex ndx : ndxs){				
					deleteIndex(ndx.getId(), ndx.getUID(), ndx.getClassId(), false);
				}
			}

			QueryRunner qr = new QueryRunner(true);
			// Удаляем запись из таблицы t_attrs
			qr.update(conn, "DELETE FROM "+getDBPrefix()+"t_attrs WHERE c_id=?", id);
			
			StringBuilder sql = new StringBuilder("INSERT INTO "+getDBPrefix()+"t_attrs (c_id,c_class_id,c_name,c_type_id,c_col_type,c_is_unique,")
							.append("c_is_indexed,c_is_multilingual,c_is_repl,c_size,c_flags,c_rattr_id,c_sattr_id,c_sdesc,c_auid");
		
			if (attr.tname != null && attr.tname.trim().length() > 0)
				sql.append(",c_tname");
			
			sql.append(") VALUES (");
			sql.append(attr.id).append(",");
			sql.append(attr.classId).append(",");
			sql.append("'").append(attr.name).append("',");
			sql.append(attr.typeClassId).append(",");
			sql.append(attr.collectionType).append(",");
			sql.append(attr.isUnique).append(",");
			sql.append(attr.isIndexed).append(",");
			sql.append(attr.isMultilingual).append(",");
			sql.append(attr.isRepl).append(",");
			sql.append(attr.size).append(",");
			sql.append(attr.flags).append(",");
			if (attr.rAttrId > 0) sql.append(attr.rAttrId);
			else sql.append("null");
			sql.append(",");
			if (attr.sAttrId > 0) sql.append(attr.sAttrId);
			else sql.append("null");
			sql.append(",");
			sql.append(attr.sDesc).append(",");
			sql.append("'").append(attr.uid).append("',");
			if (attr.tname != null && attr.tname.trim().length() > 0) sql.append("'").append(attr.tname).append("'");
			sql.append(")");
			addReverseSQL(sql.toString());

			if (attr.rAttrId == 0) {
				if (attr.collectionType == COLLECTION_ARRAY
						|| attr.collectionType == COLLECTION_SET) {
					// Удаляем дополнительную таблицу
					String tname = getAttrTableName(attr);
					qr.update(conn, "DROP TABLE IF EXISTS " + tname);
				} else {
					// Удаляем ключ
					String tname = getClassTableName(attr.classId);
					if (attr.typeClassId > 10) {
						String fkname = getAttrFKName(attr.id);
						try {
							qr.update(conn, getDropForeignKeySql(tname, fkname));
						} catch (SQLException e) {
							this.log.warn("Constraint " + fkname + " not found!!!");
						}
					}
					// Удаляем колонку в таблице класса
					if (attr.isMultilingual) {
						// Удаляем колонки для каждого системного языка
						List<KrnObject> langs = getSystemLangs();
						for (KrnObject lang : langs) {
							String cname = getColumnName(attr, lang.id);
							try {
								qr.update(conn, getDropColumnSql(tname, cname));
							} catch (SQLException e) {
								this.log.warn("Column '" + cname + "' in table '" + tname + "' not found!!!");
							}
						}
					} else {
						String cname = getColumnName(attr);
						try {
							qr.update(conn, getDropColumnSql(tname, cname));
						} catch (SQLException e) {
							this.log.warn("Column '" + cname + "' in table '" + tname + "' not found!!!");
						}
					}
				}
			}

			if (log) {
	            // Создаем запись в журнале изменения модели

				logVcsModelChanges(ENTITY_TYPE_ATTRIBUTE, ACTION_DELETE, attr, attr, null, conn);
	            
				logModelChanges(ENTITY_TYPE_ATTRIBUTE, ACTION_DELETE, attr.uid, conn);
			}

			// Обновляем триггеры для таблицы класса
			updateTriggers(attr.classId);

			// Удаляем атрибут из кэша
			db.removeAttribute(attr);

		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	protected void deleteAttributeComp(KrnAttribute attr, boolean log, long dbVer) throws DriverException {
		try {
			if (dbVer > 10) {
				//Удаляем многостолбцовые индексы, в которых участвует атрибут
				List<KrnIndex> ndxs = getIndexesByAttributeId(attr.id);
				for(KrnIndex ndx : ndxs){				
					deleteIndex(ndx.getId(), ndx.getUID(), ndx.getClassId(), false);
				}
			}
			// Удаляем запись из таблицы t_attrs
			QueryRunner qr = new QueryRunner(true);
			qr.update(conn, "DELETE FROM "+getDBPrefix()+"t_attrs WHERE c_id=?", attr.id);

			if (log) {
	            // Создаем запись в журнале изменения модели

				logVcsModelChanges(ENTITY_TYPE_ATTRIBUTE, ACTION_DELETE, attr, attr, null, conn);
	            
	            logModelChanges(ENTITY_TYPE_ATTRIBUTE, ACTION_DELETE, attr.uid, conn);
			}

			if (attr.rAttrId == 0) {
				if (attr.collectionType == COLLECTION_ARRAY
						|| attr.collectionType == COLLECTION_SET) {
					// Удаляем дополнительную таблицу
					String tname = getAttrTableName(attr);
					qr.update(conn, "DROP TABLE IF EXISTS " + tname);
				} else {
					// Удаляем колонку в таблице класса
					String tname = getClassTableName(attr.classId);
					if (attr.typeClassId > 10) {
						String fkname = getAttrFKName(attr.id);
						try { 
							qr.update(conn, getDropForeignKeySql(tname, fkname));
						} catch (SQLException e) {
							this.log.warn("Constraint " + fkname + " not found.");
						}
					}
					if (attr.isMultilingual) {
						// Удаляем колонки для каждого системного языка
						List<KrnObject> langs = getSystemLangs();
						for (KrnObject lang : langs) {
							String cname = getColumnName(attr, lang.id);
							qr.update(conn, getDropColumnSql(tname, cname));
						}
					} else {
						String cname = getColumnName(attr);
						qr.update(conn, getDropColumnSql(tname, cname));
					}
				}
			}
			
			// Обновляем триггеры для таблицы класса
			updateTriggersComp(attr.classId);

		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	public List<KrnAttribute> getAllAttributes() throws DriverException {
		AttributeRsh rh = new AttributeRsh();
		QueryRunner qr = new QueryRunner(true);
		try {
			return qr.query(conn, "SELECT * FROM "+getDBPrefix()+"t_attrs", rh);
		} catch (SQLException e) {
			throw convertException(e);
		}
	}
	
	public KrnAttribute[] getRevAttributes(long attrId) throws DriverException {
		AttributeRsh rh = new AttributeRsh();
		QueryRunner qr = new QueryRunner(true);
		try {
			List<KrnAttribute> res = qr.query(conn,
					"SELECT "+getDBPrefix()+"t_attrs.* FROM "+getDBPrefix()+"t_rattrs,"+getDBPrefix()+"t_attrs"
							+ " WHERE c_id="+getDBPrefix()+"t_rattrs.c_rattr_id AND c_attr_id=?",
					rh, attrId);
			return res.toArray(new KrnAttribute[res.size()]);
		} catch (SQLException e) {
			throw convertException(e);
		}
	}
	
	public KrnAttribute[] getLinkAttributes(long attrId) throws DriverException {
		AttributeRsh rh = new AttributeRsh();
		QueryRunner qr = new QueryRunner(true);
		try {
			List<KrnAttribute> res = qr.query(conn,
					"SELECT FROM "+getDBPrefix()+"t_attrs WHERE c_rattr_id=?", rh, attrId);
			return res.toArray(new KrnAttribute[res.size()]);
		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	//Создание информации об индексе в БД
	public KrnIndex createIndexInfo(String uid,long classId) throws DriverException{		
		if(uid == null)
			uid = UUID.randomUUID().toString();
		try{
			String sql = "INSERT INTO "+getDBPrefix()+"t_indexes(c_uid,c_class_id,c_is_multilingual) VALUES (?,?,0)";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, uid);
			pst.setLong(2, classId);
			pst.executeUpdate();
			pst.close();			
			
			long id = getLastIndexId();
			KrnIndex krnIndex = new KrnIndex(id,classId,uid);
			return krnIndex;
		}catch(SQLException e){
			throw convertException(e);
		}
	}
	
	//Создание информации о ключе индекса в БД
	public KrnIndexKey createIndexKeyInfo(long indexId,long attrId,long keyNo,boolean isDesc) throws DriverException{		
		try{
			String sql = "INSERT INTO "+getDBPrefix()+"t_indexkeys(c_index_id,c_attr_id,c_keyno,c_is_desc) VALUES (?,?,?,?)";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setLong(1, indexId);
			pst.setLong(2, attrId);
			pst.setLong(3, keyNo);
			pst.setBoolean(4, isDesc);
			pst.executeUpdate();
			pst.close();			
			
			KrnIndexKey krnIndexKey = new KrnIndexKey(indexId,attrId,keyNo,isDesc);
			return krnIndexKey;
		}catch(SQLException e){
			throw convertException(e);
		}
	}
	
	//Создание индекса в БД
	public void createIndex(long id,String uid,long classId,KrnAttribute[] attrs) throws DriverException{
		PreparedStatement pst;
		String sql,keys;
		try{
			List<KrnObject> langs = null;			
			if(isMultiLangIndex(attrs)){
				langs = getSystemLangs();
			}			
			if(langs == null || langs.size() == 0){//не мультиязычный
				keys = "c_tr_id";
				//формируем ключи индекса
				for(int i=0;i<attrs.length;i++){
					KrnAttribute attr = attrs[i];					
					keys += "," + getColumnName(attr);
				}
				sql = "CREATE INDEX " + getIndexName(id) + " ON " + getClassTableName(classId) + "(" + keys + ")";
				pst = conn.prepareStatement(sql);
				pst.executeUpdate();
				pst.close();
			}else{//мультиязычный
				//делаем пометку о том, что индекс мультиязычный
				sql = "UPDATE "+getDBPrefix()+"t_indexes SET c_is_multilingual = 1 WHERE c_id = ?";
				pst = conn.prepareStatement(sql);
				pst.setLong(1, id);
				pst.executeUpdate();
				//перебираем языки
				for(KrnObject lang : langs){
					keys = "c_tr_id";
					for(int i=0;i<attrs.length;i++){
						KrnAttribute attr = attrs[i];												
						keys += "," + getColumnName(attr,lang.id);						
					}
					sql = "CREATE INDEX " + getIndexName(id,lang.id) + " ON " + getClassTableName(classId) + "(" + keys + ")";
					pst = conn.prepareStatement(sql);
					pst.executeUpdate();					
				}
				pst.close();
			}
			//Создаем запись в журнале изменения модели

			logVcsModelChanges(ENTITY_TYPE_INDEX, ACTION_CREATE, uid, uid, null, conn);

			logModelChanges(ENTITY_TYPE_INDEX, ACTION_CREATE, uid, conn);
		}catch(SQLException e){
			throw convertException(e);
		}
	}
	
	//Получить индекс по uid
	public KrnIndex getIndexByUid(String uid) throws DriverException{
		String sql = "SELECT c_id,c_class_id FROM "+getDBPrefix()+"t_indexes WHERE c_uid = ?";
		KrnIndex ret = null;
		try{
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, uid);
			ResultSet rs = st.executeQuery();
			if(rs.next()){
				ret = new KrnIndex(rs.getLong(1), rs.getLong(2), uid);
			}
			st.close();
			rs.close();
			if(ret == null){
				throw new DriverException("Индекс с uid=" + uid + " не найден");
			}
		}catch(SQLException e){
			throw convertException(e);
		}
		return ret;
	}
		
	//Получить индексы класса
	public List<KrnIndex> getIndexesByClassId(long classId) throws DriverException{		
		String sql = "SELECT * FROM "+getDBPrefix()+"t_indexes WHERE c_class_id = ? ORDER BY c_id";
		ResultSetHandler rh = new IndexResultSetHandler();
		QueryRunner qr = new QueryRunner(true);
		try{			
			return (List<KrnIndex>) qr.query(conn,sql,rh,classId);
		}catch (SQLException e){
			throw convertException(e);
		}
	}
	//Получить ключи индекса
	public List<KrnIndexKey> getIndexKeysByIndexId(long indexId) throws DriverException{
		String sql = "SELECT * FROM "+getDBPrefix()+"t_indexkeys WHERE c_index_id = ? ORDER BY c_keyno";
		ResultSetHandler rh = new IndexKeyResultSetHandler();
		QueryRunner qr = new QueryRunner(true);
		try{
			return (List<KrnIndexKey>) qr.query(conn,sql,rh,indexId);
		}catch(SQLException e){
			throw convertException(e);
		}
	}
	
	//Получить список индексов, в составе которых данный атрибут
	public List<KrnIndex> getIndexesByAttributeId(long attrId) throws DriverException{
		String sql = "" +
				"SELECT i.*" +
				" FROM(" +
				"  SELECT DISTINCT c_index_id FROM "+getDBPrefix()+"t_indexkeys WHERE c_attr_id = ?" +
				" )ik" +
				" JOIN "+getDBPrefix()+"t_indexes i ON ik.c_index_id = i.c_id";
		ResultSetHandler rh = new IndexResultSetHandler();
		QueryRunner qr = new QueryRunner(true);
		try{
			return (List<KrnIndex>) qr.query(conn,sql,rh,attrId);			
		}catch(SQLException e){
			throw convertException(e);
		}
	}
	
	//Получить список атрибутов возможных для индексирования
	public List<KrnAttribute> getAttributesForIndexing(long classId) throws DriverException{
		String sql =
			"SELECT *" +
				" FROM "+getDBPrefix()+"t_attrs WHERE c_class_id = ? " +
				" AND c_col_type NOT IN(1,2)" +//не набор и не массив
				" AND c_rattr_id IS NULL" +//не обратный атрибут
				" AND c_type_id NOT IN(6,10)" +//не memo и не blob
				"";
		AttributeRsh rh = new AttributeRsh();
		QueryRunner qr = new QueryRunner(true);
		try{
			return qr.query(conn, sql, rh, classId);
		}catch(SQLException e){
			throw convertException(e);
		}
		
	}
	
	//Получить список атрибутов в составе индекса
	public boolean isMultiLangIndex(long indexId) throws DriverException{
		try{
			String sql = "SELECT c_is_multilingual FROM "+getDBPrefix()+"t_indexes WHERE c_id = ?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setLong(1, indexId);
			ResultSet rs = pst.executeQuery();
			if(!rs.next()){				
				throw new DriverException("Не удалось проверить мультиязычность индекса, т.к. он не был найден");				
			}else{
				return rs.getBoolean(1);
			}			
		}catch(SQLException e){
			throw convertException(e);
		}		
	}
	
	//Проверить индекс на мультиязычность
	protected boolean isMultiLangIndex(List<KrnAttribute> attrs) throws DriverException{
		if(attrs == null)
			throw new DriverException("Не найдена информация об атрибутах индекса");		
		for(KrnAttribute attr : attrs)
    		if(attr.isMultilingual)
    			return true;
    	return false;		
	}
	
	protected boolean isMultiLangIndex(KrnAttribute[] attrs) throws DriverException{
		if(attrs == null)
			throw new DriverException("Не найдена информация об атрибутах индекса");
		for(KrnAttribute attr : attrs)
    		if(attr.isMultilingual)
    			return true;
    	return false;
	}
		
	//Удалить индекс
	public void deleteIndex(long id,String uid,long classId) throws DriverException{
		deleteIndex(id,uid,classId,true);
	}
	protected void deleteIndex(long id,String uid,long classId,boolean log) throws DriverException{
		String sql;
		PreparedStatement pst = null;
		List<KrnObject> langs = null;
		try{
			if(log){
				logModelChanges(ENTITY_TYPE_INDEX,ACTION_DELETE,uid);
			}					
			if(isMultiLangIndex(id)){
				langs = getSystemLangs();
			}
			if(langs == null || langs.size() == 0){//не мультиязычный
				sql = dropIndex(getIndexName(id), getClassTableName(classId));
				pst = conn.prepareStatement(sql);
				pst.executeUpdate();
			}else{//мультиязычный
				//удаляем по очереди индексы для каждого языка
				for(KrnObject lang : langs){				
					sql = dropIndex(getIndexName(id,lang.id),getClassTableName(classId));
					pst = conn.prepareStatement(sql);
					pst.executeUpdate();
				}
			}
			deleteIndexInfo(id);
		} catch(SQLException e){
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(pst);
		}
	}
	
	//Удаление информации об индексе
	public void deleteIndexInfo(long id) throws DriverException{
		try{
			String sql;
			PreparedStatement pst;
			sql = "DELETE FROM "+getDBPrefix()+"t_indexkeys WHERE c_index_id = ?";
			pst = conn.prepareStatement(sql);
			pst.setLong(1, id);
			pst.executeUpdate();
			
			sql = "DELETE FROM "+getDBPrefix()+"t_indexes WHERE c_id = ?";
			pst = conn.prepareStatement(sql);
			pst.setLong(1, id);
			pst.executeUpdate();			
			pst.close();
		}catch(SQLException e){
			throw convertException(e);
		}
	}
	
	//Удаление индекса
	protected String dropIndex(String indexName,String tableName) throws SQLException{
		return "DROP INDEX " + indexName + " ON " + tableName;		
	}
	
	//Проверяем существование сущности - класс
	public boolean classExists(String classUid) throws DriverException{
		try{
			String sql = "SELECT COUNT(0) FROM "+getDBPrefix()+"t_classes WHERE c_cuid = ?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, classUid);
			ResultSet rs = pst.executeQuery();
			if(rs.next()){
				if(rs.getLong(1) > 0){
					return true;
				}
			}
			pst.close();
			rs.close();
			return false;
		}catch(SQLException e){
			throw convertException(e);
		}		
	}
	
	//Проверяем существование сущности - атрибут
	public boolean attributeExists(String attrUid) throws DriverException{
		try{
			String sql = "SELECT COUNT(0) FROM "+getDBPrefix()+"t_attrs WHERE c_auid = ?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, attrUid);
			ResultSet rs = pst.executeQuery();
			if(rs.next()){
				if(rs.getLong(1) > 0){
					return true;
				}
			}
			pst.close();
			rs.close();
			return false;
		}catch(SQLException e){
			throw convertException(e);
		}
	}
	
	//Проверяем существование сущности - индекс
	public boolean indexExists(String indexUid) throws DriverException{
		try{
			String sql = "SELECT COUNT(0) FROM "+getDBPrefix()+"t_indexes WHERE c_uid = ?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, indexUid);
			ResultSet rs = pst.executeQuery();
			if(rs.next()){
				if(rs.getLong(1) > 0){
					return true;
				}
			}
			pst.close();
			rs.close();
			return false;
		}catch(SQLException e){
			throw convertException(e);
		}
	}
			
	public void commit() throws DriverException {
		try {
			saveDataLog();
			if (!conn.getAutoCommit())
				conn.commit();
			
			clearReverseSQL();
			clearNotCommitedJrbNodes();
			deleteRewrittenJrbNodes();
			
			Map<Long, List<AttrChangeListener>> map = getAllListeners();
			if (map != null && map.size() > 0) {
				for (Long classId : map.keySet()) {
					List<AttrChangeListener> list = map.get(classId);
					for (AttrChangeListener l : list)
						l.commit(us.getId());
				}
				if (UserSession.SERVER_ID != null)
	    			ServerMessage.sendMessage(UserSession.SERVER_ID, us.getDsName(), us.getId(), new AttrChange(null, -1, 0, 0));
			}
		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	public void rollback() throws DriverException {
		try {
			if (dataLogBatchCount > 0) {
				dataLogPst.clearBatch();
				dataLogBatchCount = 0;
			}
			if (!conn.getAutoCommit()) {
				conn.rollback();
				executeReverseSQL();
			}
			clearReverseSQL();
			deleteNotCommitedJrbNodes();
			clearRewrittenJrbNodes();

			Map<Long, List<AttrChangeListener>> map = getAllListeners();
			if (map != null && map.size() > 0) {
				for (Long classId : map.keySet()) {
					List<AttrChangeListener> list = map.get(classId);
					for (AttrChangeListener l : list)
						l.rollback(us.getId());
				}
				if (UserSession.SERVER_ID != null)
	    			ServerMessage.sendMessage(UserSession.SERVER_ID, us.getDsName(), us.getId(), new AttrChange(null, -2, 0, 0));
			}
		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	public void release() {
		dataLogBatchCount = 0;
		DbUtils.closeQuietly(dataLogPst);
		DbUtils.closeQuietly(pstObjById);
		DbUtils.closeQuietly(pstObjByUid);
		DbUtils.closeQuietly(pstExtBlobParamsById);
		DbUtils.closeQuietly(pstUpdateExtBlobParams);
		DbUtils.closeQuietly(conn);

		String threadName = Thread.currentThread().getName();
    	threadName = threadName.replaceAll("\\(CONN_ID:\\d{1,}\\)", "");  
   		Thread.currentThread().setName(threadName);
		conn = null;
	}

	public long createLongTransaction() throws DriverException {
		return getNextId("transaction_id");
	}
	
	public void commitLongTransaction(final long trId, boolean deleteRefs, Session session) throws DriverException {
		try {
			saveDataLog();
			
			Set<Long> createIds = new HashSet<Long>();
			Set<Pair<Long, String>> jrbNodesToDelete = new HashSet<>();
			QueryRunner qr = new QueryRunner(true);
			/*
			// Запоминаем все удаленные в транзакции объекты
			// с целью игнорирования модификации его атибуотов до удаления
			// самого объекта.
			Set<Long> deletingIds = new HashSet<Long>();
			PreparedStatement pst = conn.prepareStatement(
					"SELECT c_object_id FROM t_changes" +
					" WHERE c_tr_id=? AND c_attr_id=2");
			pst.setLong(1, trId);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				deletingIds.add(rs.getLong(1));
			}
			rs.close();
			pst.close();
			*/
			// Загружаем все измененные объекты
			PreparedStatement pst = null;
			ResultSet rs = null;
			try {
				pst = conn.prepareStatement("SELECT ch.c_object_id,ch.c_attr_id,ch.c_lang_id,min(ch.c_id) AS id FROM "
						+ getDBPrefix()+"t_changes ch, "
						+ getDBPrefix()+"t_attrs a WHERE ch.c_tr_id=? AND a.c_id=ch.c_attr_id AND a.c_rattr_id IS NULL GROUP BY ch.c_object_id,ch.c_attr_id,ch.c_lang_id ORDER BY id");
				pst.setLong(1, trId);
				rs = pst.executeQuery();
				KrnAttribute attr = null;
				String cname = null;
	
				while (rs.next()) {
					long objId = rs.getLong("c_object_id");
	                long attrId = rs.getLong("c_attr_id");
					long langId = rs.getLong("c_lang_id");
	
					if (attrId == 1) { // созданный в транзакции объект
						KrnObject obj = getDirtyObjectById(objId);
						try {
							session.executeClsTriggerEventExpression(0, db.getClassById(obj.classId), null,true, trId);
							if (obj != null) {
								List<KrnClass> superClss = db.getSuperClasses(obj.classId);
								List<KrnAttribute> attrs = db.getAttributesByClassId(obj.classId, true);
								commitCreatedObject(obj, trId, superClss, attrs, jrbNodesToDelete);
								
								fireAttrChanged(obj, 1, 0, 0);
							}
							createIds.add(objId);
							if(session.isContextEmpty() || !session.getContext().isNotWriteSysLog)
								session.writeCreateObjectLogRecord(obj.id, db.getClassById(obj.classId).name, trId);
							session.executeClsTriggerEventExpression(1, db.getClassById(obj.classId), obj,true, trId);
						} catch (KrnException e) {
							session.writeLogRecord(SystemEvent.ERROR_TRIGGER_CLS, e.getMessage(),obj!=null?obj.classId:-1,attrId);
							throw new DriverException(e.getMessage(),ErrorCodes.ER_EXEC_TRIGGER_CLS);
						}
						continue;
	                }
	
					if (attrId == 2) { // удаленный в транзакции объект
						KrnObject obj = getDirtyObjectByIdQuietly(objId);
						try {
							if (obj != null) {
								session.executeClsTriggerEventExpression(2, db.getClassById(obj.classId), obj, true, trId);
								commitDeletedObject(obj, trId, deleteRefs, createIds.contains(objId), jrbNodesToDelete);
								fireAttrChanged(obj, 2, 0, 0);
								if(session.isContextEmpty() || !session.getContext().isNotWriteSysLog)
									session.writeDeleteObjectLogRecord(obj, db.getClassById(obj.classId).name, trId);
								session.executeClsTriggerEventExpression(3, db.getClassById(obj.classId), null, true, trId);
							}
						} catch (KrnException e) {
							session.writeLogRecord(SystemEvent.ERROR_TRIGGER_CLS, e.getMessage(),obj!=null?obj.classId:-1,attrId);
							throw new DriverException(e.getMessage(),ErrorCodes.ER_EXEC_TRIGGER_CLS);
						}
						continue;
	                }
	
					attr = db.getAttributeById(attrId);
					cname = getColumnName(attr, langId);
					if (createIds.contains(objId)) {
						if(session.isContextEmpty() || !session.getContext().isNotWriteSysLog)
						if (attr.collectionType == COLLECTION_NONE) {
							String ctName = getClassTableName(attr.classId);
							PreparedStatement smpSelPst = conn.prepareStatement("SELECT " + cname + " FROM " + ctName + " WHERE c_obj_id=? AND c_tr_id=?");
							smpSelPst.setLong(1, objId);
							smpSelPst.setLong(2, 0);
							ResultSet rsc = smpSelPst.executeQuery();
							while (rsc.next()) {
								Object val = getValue(rsc, cname, attr.typeClassId);
								session.writeSetValueLogRecord(objId, attrId, langId, val, trId);
							}
							rsc.close();
		        			smpSelPst.close();
						} else if (attr.collectionType == COLLECTION_SET) {
							String mtName = getAttrTableName(attr);
							// Вставляем в 0 все что добавлено в X
							PreparedStatement setSelXPst = conn.prepareStatement("SELECT " + cname + " FROM " + mtName + " WHERE c_obj_id=? AND c_tr_id=? and c_del=0" + " AND " + cname + " NOT IN (SELECT " + cname + " FROM " + mtName + " WHERE c_obj_id=? AND c_tr_id=0)");
							setSelXPst.setLong(1, objId);
							setSelXPst.setLong(2, 0);
							setSelXPst.setLong(3, objId);
							ResultSet rsc = setSelXPst.executeQuery();
							while (rsc.next()) {
								session.writeSetValueLogRecord(objId, attrId, langId, getValue(rsc, cname, attr.typeClassId), trId);
							}
							rsc.close();
		        			setSelXPst.close();
						} else if (attr.collectionType == COLLECTION_ARRAY) {
							String mtName = getAttrTableName(attr);
							PreparedStatement selUxPst = conn.prepareStatement("SELECT  " + cname + " FROM " + mtName + " WHERE c_obj_id=? AND c_tr_id=?"); 
							selUxPst.setLong(1, objId);
							selUxPst.setLong(2, 0);
							ResultSet rsc = selUxPst.executeQuery();
							
							StringBuilder arrVal = new StringBuilder("[");
							while (rsc.next()) {
								if (arrVal.length() > 1) arrVal.append(',');
								Object v = getValue(rsc, cname, attr.typeClassId);
								if (v instanceof byte[])
									arrVal.append("blob[").append(((byte[])v).length).append("]");
								else
									arrVal.append(v);
							}
							rsc.close();
							arrVal.append(']');
							session.writeSetValueLogRecord(objId, attrId, langId, arrVal.toString(), trId);
		        			selUxPst.close();
						}
						continue;
	                }
	
					if (attr.collectionType == COLLECTION_NONE) {
						String ctName = getClassTableName(attr.classId);
						PreparedStatement smpSelPst = conn.prepareStatement("SELECT " + cname + " FROM " + ctName + " WHERE c_obj_id=? AND c_tr_id=?");
						PreparedStatement smpUpdPst = conn.prepareStatement("UPDATE " + ctName + " SET " + cname + "=? WHERE c_obj_id=? AND c_tr_id=0");
	                    commitSimpleAttr(smpSelPst, smpUpdPst, cname, attr.typeClassId, objId, attrId, langId, trId, session, jrbNodesToDelete);
	        			smpSelPst.close();
	        			smpUpdPst.close();
					} else if (attr.collectionType == COLLECTION_SET) {
						String mtName = getAttrTableName(attr);
						// Удаляем во всех транзакциях все что удалено в X
						PreparedStatement setSelPst = conn.prepareStatement("SELECT " + cname + " FROM " + mtName + " WHERE c_obj_id=? AND c_tr_id=? and c_del>0");
						PreparedStatement setDelPst = conn.prepareStatement("DELETE FROM " + mtName + " WHERE c_obj_id=? AND " + cname + "=?");
						// Вставляем в 0 все что добавлено в X
						PreparedStatement setSelXPst = conn.prepareStatement("SELECT " + cname + " FROM " + mtName + " WHERE c_obj_id=? AND c_tr_id=? and c_del=0 AND " + cname + " NOT IN (SELECT " + cname + " FROM " + mtName + " WHERE c_obj_id=? AND c_tr_id=0)");
						PreparedStatement setInsPst = conn.prepareStatement("INSERT INTO " + mtName + " (c_obj_id,c_tr_id," + cname + ") SELECT c_obj_id,0," + cname + " FROM " + mtName + " WHERE c_obj_id=? AND c_tr_id=? AND c_del=0 AND " + cname + " NOT IN (SELECT " + cname + " FROM " + mtName + " WHERE c_obj_id=? AND c_tr_id=0)");
						PreparedStatement setDelXPst = conn.prepareStatement("DELETE FROM " + mtName + " WHERE c_obj_id=? AND c_tr_id=?");
	                    commitSetAttr(setSelPst, setDelPst, setSelXPst, setInsPst, setDelXPst, cname, attr.typeClassId, objId, attrId, langId, trId, session, jrbNodesToDelete);
	        			setSelPst.close();
	        			setDelPst.close();
	        			setSelXPst.close();
	        			setInsPst.close();
	        			setDelXPst.close();
					} else if (attr.collectionType == COLLECTION_ARRAY) {
						String mtName = getAttrTableName(attr);
						// Выбираем из 0 все чего нет в Х
						PreparedStatement arrS0Pst = conn.prepareStatement("SELECT DISTINCT c_index FROM " + mtName + " t1 WHERE c_obj_id=? AND c_tr_id=0 AND c_id NOT IN (SELECT c_id FROM " + mtName + " t2 WHERE t2.c_obj_id=t1.c_obj_id AND t2.c_tr_id=?)");
						// Удаляем в Х все что помечено на удаление
						PreparedStatement selDdelPst = conn.prepareStatement("SELECT  " + cname + " FROM " + mtName + " WHERE c_obj_id=? AND c_tr_id=? AND c_del<>0"); 
						PreparedStatement arrDdelPst = conn.prepareStatement("DELETE FROM " + mtName + " WHERE c_obj_id=? AND c_tr_id=? AND c_del<>0");
						// Вставляем в Х все что было в 0 и не было в Х
						PreparedStatement arrSxPst = conn.prepareStatement("SELECT DISTINCT c_index FROM " + mtName + " WHERE c_obj_id=? AND c_tr_id=?");
						PreparedStatement arrU0Pst = conn.prepareStatement("UPDATE " + mtName + " SET c_tr_id=? WHERE c_obj_id=? AND c_tr_id=0 AND c_index=?");
						PreparedStatement arrShPst = conn.prepareStatement("UPDATE " + mtName + " SET c_index=c_index+? WHERE c_obj_id=? AND c_tr_id=? AND c_index=?");
						// Удаляем все в 0
						PreparedStatement arrD0Pst = conn.prepareStatement("DELETE FROM " + mtName + " WHERE c_obj_id=? AND c_tr_id=0");
						// заменяем Х на 0
						PreparedStatement selUxPst = conn.prepareStatement("SELECT  " + cname + " FROM " + mtName + " WHERE c_obj_id=? AND c_tr_id=?"); 
						PreparedStatement arrUxPst = conn.prepareStatement("UPDATE " + mtName + " SET c_tr_id=0 WHERE c_obj_id=? AND c_tr_id=?");
						commitArrAttr(arrS0Pst, selDdelPst, arrDdelPst, arrSxPst, arrU0Pst, arrShPst, arrD0Pst, selUxPst, arrUxPst, cname, attr.typeClassId, objId, attrId, langId, trId, session, jrbNodesToDelete);
	        			arrS0Pst.close();
	        			selDdelPst.close();
	        			arrDdelPst.close();
	        			arrSxPst.close();
	        			arrU0Pst.close();
	        			arrShPst.close();
	        			arrD0Pst.close();
	        			selUxPst.close();
	        			arrUxPst.close();
					} else {
						throw new DriverException("NOT IMPLEMENTED");
					}
					KrnObject obj = getDirtyObjectByIdQuietly(objId);
					if (obj != null)
						fireAttrChanged(obj, attrId, langId, 0);
				}
			} finally {
				DbUtils.closeQuietly(rs);
				DbUtils.closeQuietly(pst);
			}
			
			PreparedStatement attrPst = null;
			ResultSet attrRs = null;
			try {
				// Отключаем проверку FK
				setForeignKeysEnabled(false);

				// Загружаем в classIds ID всех классов участвовавших в
				// транзакции
				Set<Long> classIds = new HashSet<Long>();
				pst = conn.prepareStatement("SELECT DISTINCT c_parent_id FROM "+getDBPrefix()+"t_changes,"+getDBPrefix()+"t_clinks WHERE c_child_id=c_class_id AND c_tr_id=?");
				pst.setLong(1, trId);
				rs = pst.executeQuery();
				while (rs.next()) {
					long clsId = rs.getLong("c_parent_id");
					KrnClass cls = db.getClassById(clsId);
					if (clsId != ROOT_CLASS_ID && !cls.isVirtual())
						classIds.add(clsId);
				}
				rs.close();
				pst.close();

				attrPst = conn.prepareStatement("SELECT c_id,c_class_id FROM "+getDBPrefix()+"t_attrs WHERE c_class_id=? AND c_col_type>0 AND c_rattr_id IS NULL"); 
				// Выполняем действия над каждым классом из classIds...
				for (Long classId : classIds) {
					// Удаляем все строчки в АТ талблицах класса
					// Необходимо ручное удаления из-за отключенных проверок FK
					attrPst.setLong(1, classId);
					attrRs = attrPst.executeQuery();
					while (attrRs.next()) {
						long attrId = attrRs.getLong(1);
						KrnAttribute a = db.getAttributeById(attrId);
						String atName = getAttrTableName(a);
						qr.update(conn, "DELETE FROM " + atName + " WHERE c_tr_id=?", trId);
					}
					attrRs.close();

					final String ctName = getClassTableName(classId);
					// Удалаяем все строчки в таблице класса с X транзакцией,
					pst = conn.prepareStatement("DELETE FROM " + ctName + " WHERE c_tr_id=?");
					pst.setLong(1, trId);
					pst.executeUpdate();
					pst.close();
				}
				attrPst.close();
			} finally {
				// Включаем проверку FK
				setForeignKeysEnabled(true);
				DbUtils.closeQuietly(attrRs);
				DbUtils.closeQuietly(attrPst);
				DbUtils.closeQuietly(rs);
				DbUtils.closeQuietly(pst);
			}
			commitDataLog(trId);
			
			for (Pair<Long, String> node : jrbNodesToDelete)
				db.deleteRepositoryData(node.first, node.second);
			jrbNodesToDelete.clear();
			
			Map<Long, List<AttrChangeListener>> map = getAllListeners();
			if (map != null && map.size() > 0) {
				for (Long classId : map.keySet()) {
					List<AttrChangeListener> list = map.get(classId);
					for (AttrChangeListener l : list)
						l.commitLongTransaction(us.getId(), trId);
				}
				if (UserSession.SERVER_ID != null)
	    			ServerMessage.sendMessage(UserSession.SERVER_ID, us.getDsName(), us.getId(), new AttrChange(null, -3, 0, trId));
			}

		} catch (SQLException e) {
			session.writeLogRecord(SystemEvent.ERROR_COMMIT_LONG_TRANSACTION, e.getMessage(),-1,-1);
			throw convertException(e);
		}
	}
	
	@Override
	public void setForeignKeysEnabled(boolean enabled) throws DriverException {
		Statement st = null;
		try {
			st = conn.createStatement();
			if (enabled)
				st.executeUpdate("SET FOREIGN_KEY_CHECKS=1");
			else
				st.executeUpdate("SET FOREIGN_KEY_CHECKS=0");
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(st);
		}
	}
	
	protected void commitSimpleAttr(PreparedStatement smpSelPst, PreparedStatement smpUpdPst, String cname, long typeId, long objId, long attrId, long langId, long trId, Session session, Set<Pair<Long,String>> jrbNodesToDelete)
			throws SQLException, DriverException {
		
		if (db.inJcrRepository(attrId)) {
			smpSelPst.setLong(1, objId);
			smpSelPst.setLong(2, 0);
			ResultSet rs = smpSelPst.executeQuery();
			while (rs.next()) {
				Object nodeId = getValue(rs, cname, typeId);
				if (nodeId != null) {
					String nodeIdStr = (nodeId instanceof byte[]) ? new String((byte[])nodeId) : (String)nodeId;
					jrbNodesToDelete.add(new Pair<Long, String>(attrId, nodeIdStr));
				}
			}
			rs.close();
		}
		
		smpSelPst.setLong(1, objId);
		smpSelPst.setLong(2, trId);
		ResultSet rs = smpSelPst.executeQuery();
		while (rs.next()) {
			try {
				Object value = getValue(rs, cname, typeId);
				session.executeAttrTriggerEventExpression(attrId, value, 0, objId, 0, trId, false,true, null);
	            setValue(smpUpdPst, 1, typeId, value);
				smpUpdPst.setLong(2, objId);
				smpUpdPst.executeUpdate();
				session.executeAttrTriggerEventExpression(attrId, value, 1, objId, 0, trId, false,true, null);
				if(session.isContextEmpty() || !session.getContext().isNotWriteSysLog)
					session.writeSetValueLogRecord(objId, attrId, langId, value, trId);
			} catch (KrnException e) {
				session.writeLogRecord(SystemEvent.ERROR_TRIGGER_ATTR, e.getMessage(),-1,attrId);
				throw new DriverException(e.getMessage(),ErrorCodes.ER_EXEC_TRIGGER_ATTR);
			}
		}
		rs.close();
	}

	protected void commitSetAttr(PreparedStatement setSelPst,
			PreparedStatement setDelPst, PreparedStatement setSelXPst, PreparedStatement setInsPst,
			PreparedStatement setDelXPst,
			String cname, long typeId, long objId, long attrId, long langId, long trId, Session session, Set<Pair<Long,String>> jrbNodesToDelete)
			throws SQLException, DriverException {
		// Удаляем во всех все что удалено в X
		Set<Object> values = new HashSet<Object>();
		setSelPst.setLong(1, objId);
		setSelPst.setLong(2, trId);
		ResultSet rs = setSelPst.executeQuery();
		while (rs.next()) {
			Object value = getValue(rs, cname, typeId);
			values.add(value);
			if (db.inJcrRepository(attrId)) {
				if (value != null) {
					String nodeIdStr = (value instanceof byte[]) ? new String((byte[])value) : (String)value;
					jrbNodesToDelete.add(new Pair<Long, String>(attrId, nodeIdStr));
				}
			}
		}
		rs.close();
		for (Object value : values) {
			try {
				session.executeAttrTriggerEventExpression(attrId, value, 0, objId, 0, trId, false,true, null);
				setDelPst.setLong(1, objId);
				setValue(setDelPst, 2, typeId, value);
				setDelPst.executeUpdate();
				session.executeAttrTriggerEventExpression(attrId, value, 0, objId, 0, trId, false,true, null);
				if(session.isContextEmpty() || !session.getContext().isNotWriteSysLog)
					session.writeDeleteValueLogRecord(objId, attrId, langId, value, trId);
			} catch (KrnException e) {
				session.writeLogRecord(SystemEvent.ERROR_TRIGGER_ATTR, e.getMessage(),-1,attrId);
				throw new DriverException(e.getMessage(),ErrorCodes.ER_EXEC_TRIGGER_ATTR);
			}
		}

		values.clear();
		if(session.isContextEmpty() || !session.getContext().isNotWriteSysLog){
			setSelXPst.setLong(1, objId);
			setSelXPst.setLong(2, trId);
			setSelXPst.setLong(3, objId);
			rs = setSelXPst.executeQuery();
			while (rs.next()) {
				session.writeSetValueLogRecord(objId, attrId, langId, getValue(rs, cname, typeId), trId);
			}
			rs.close();
		}

		// Вставляем в 0 все что добавлено в X
		setInsPst.setLong(1, objId);
		setInsPst.setLong(2, trId);
		setInsPst.setLong(3, objId);
		setInsPst.executeUpdate();
		// Удаляем все в Х
		setDelXPst.setLong(1, objId);
		setDelXPst.setLong(2, trId);
		setDelXPst.executeUpdate();
	}

	protected void commitMultilingualAttr(PreparedStatement setDelPst,
			PreparedStatement setInsPst, long objId, long trId, long langId)
			throws SQLException {
		// Удаляем в 0 все что удалено в X
		setDelPst.setLong(1, objId);
		setDelPst.setLong(2, langId);
		setDelPst.executeUpdate();
		// Вставляем в 0 все что добавлено в X
		setInsPst.setLong(1, objId);
		setInsPst.setLong(2, trId);
		setInsPst.setLong(3, langId);
		setInsPst.executeUpdate();
	}

	protected void commitArrAttr(
			PreparedStatement arrS0Pst,
			PreparedStatement selDdelPst, 
			PreparedStatement arrDdelPst,
			PreparedStatement arrSxPst,
			PreparedStatement arrU0Pst,
			PreparedStatement arrShPst,
			PreparedStatement arrD0Pst,
			PreparedStatement selUxPst,
			PreparedStatement arrUxPst,
			String cname, long typeId, long objId, long attrId, long langId, long trId, Session session, Set<Pair<Long, String>> jrbNodesToDelete) throws SQLException {
		
		// Выбираем из 0 все чего нет в Х
		arrS0Pst.setLong(1, objId);
		arrS0Pst.setLong(2, trId);
		SortedSet<Integer> idxs0 = new TreeSet<Integer>();
		ResultSet rs = arrS0Pst.executeQuery();
		while (rs.next()) {
			idxs0.add(rs.getInt(1));
		}
		rs.close();

		if (db.inJcrRepository(attrId)) {
			selDdelPst.setLong(1, objId);
			selDdelPst.setLong(2, trId);
			rs = selDdelPst.executeQuery();
			while (rs.next()) {
				Object nodeId = getValue(rs, cname, typeId);
				if (nodeId != null) {
					String nodeIdStr = (nodeId instanceof byte[]) ? new String((byte[])nodeId) : (String)nodeId;
					jrbNodesToDelete.add(new Pair<Long, String>(attrId, nodeIdStr));
				}
			}
			rs.close();
		}

		// Удаляем в Х все что помечено на удаление
		arrDdelPst.setLong(1, objId);
		arrDdelPst.setLong(2, trId);
		arrDdelPst.executeUpdate();
		
		if (idxs0.size() > 0) {
			// Вставляем в Х все что было в 0 и не было в Х
			SortedSet<Integer> idxs = new TreeSet<Integer>(Collections.reverseOrder());
			arrSxPst.setLong(1, objId);
			arrSxPst.setLong(2, trId);
			rs = arrSxPst.executeQuery();
			while (rs.next()) {
				idxs.add(rs.getInt(1));
			}
			rs.close();

			arrU0Pst.setLong(1, trId);
			arrU0Pst.setLong(2, objId);
			
			arrShPst.setLong(2, objId);
			arrShPst.setLong(3, trId);
			for(int idx0 : idxs0) {
				SortedSet<Integer> sset = idxs.headSet(idx0);
				for (int idx : sset) {
					arrShPst.setInt(1, 1);
					arrShPst.setInt(4, idx);
					arrShPst.executeUpdate();
				}
				arrShPst.setInt(1, 1);
				arrShPst.setInt(4, idx0);
				arrShPst.executeUpdate();
				
				arrU0Pst.setInt(3, idx0);
				arrU0Pst.executeUpdate();

				if (sset.size() > 0) {
					idxs.add(sset.first() + 1);
				}
			}
		}
		// Удаляем все в 0
		arrD0Pst.setLong(1, objId);
		arrD0Pst.executeUpdate();
		
		selUxPst.setLong(1, objId);
		selUxPst.setLong(2, trId);
		rs = selUxPst.executeQuery();
		
		StringBuilder arrVal = new StringBuilder("[");
		while (rs.next()) {
			if (arrVal.length() > 1) arrVal.append(',');
			Object v = getValue(rs, cname, typeId);
			if (v instanceof byte[])
				arrVal.append("blob[").append(((byte[])v).length).append("]");
			else
				arrVal.append(v);
		}
		rs.close();
		arrVal.append(']');
		if(session.isContextEmpty() || !session.getContext().isNotWriteSysLog)
			session.writeSetValueLogRecord(objId, attrId, langId, arrVal.toString(), trId);

		// заменяем Х на 0
		arrUxPst.setLong(1, objId);
		arrUxPst.setLong(2, trId);
		arrUxPst.executeUpdate();
	}

	protected void commitDeletedObject(KrnObject obj, long trId, boolean deleteRefs,boolean isCreated, Set<Pair<Long,String>> jrbNodesToDelete)
			throws SQLException, DriverException {
		List<KrnClass> clss = db.getSuperClasses(obj.classId);
		QueryRunner qr = new QueryRunner(true);
		for (KrnClass cls : clss) {
			String tname = getClassTableName(cls.id);
			if (deleteRefs || Driver2.deleteRefs) {
				// Удаляем ссылки на объект
				List<KrnAttribute> attrs = db.getAttributesByTypeId(cls.id, false);
				for (KrnAttribute attr : attrs) {
					if (attr.rAttrId == 0) {
						String cmName = getColumnName(attr);
						if (attr.collectionType != COLLECTION_NONE) {
							String atName = getAttrTableName(attr);
							qr.update(conn, "DELETE FROM " + atName + " WHERE "
									+ cmName + "=?", obj.id);
						} else {
							String ctName = getClassTableName(attr.classId);
							qr.update(conn, "UPDATE " + ctName + " SET " + cmName
									+ "=NULL WHERE " + cmName + "=?", obj.id);
						}
					}
				}
			}
			// Удаляем запись и таблицы класса
			if (cls.id == ROOT_CLASS_ID) {
				qr.update(conn, "DELETE FROM " + tname + " WHERE c_obj_id=?",
						new Object[] { obj.id });
			} else if (!cls.isVirtual()) {
				List<KrnAttribute> attrs = db.getAttributesByClassId(cls.id, false);
				for (KrnAttribute attr : attrs) {
					if (attr.rAttrId == 0 && db.inJcrRepository(attr.id)) {
						String cnames = getColumnNames(attr);
						if (attr.collectionType != COLLECTION_NONE) {
							String mtName = getAttrTableName(attr);

							PreparedStatement setSelPst = conn.prepareStatement("SELECT " + cnames + " FROM " + mtName + " WHERE c_obj_id=? AND c_tr_id IN (0,?)");
							setSelPst.setLong(1, obj.id);
							setSelPst.setLong(2, trId);
							ResultSet rs = setSelPst.executeQuery();
							while (rs.next()) {
								String[] cnamesArr = cnames.split(",");
								for (String cname : cnamesArr) {
									Object nodeId = getValue(rs, cname, attr.typeClassId);
									if (nodeId != null) {
										String nodeIdStr = (nodeId instanceof byte[]) ? new String((byte[])nodeId) : (String)nodeId;
										jrbNodesToDelete.add(new Pair<Long, String>(attr.id, nodeIdStr));
									}
								}
							}
							rs.close();
							setSelPst.close();
						} else {
							PreparedStatement setSelPst = conn.prepareStatement("SELECT " + cnames + " FROM " + tname + " WHERE c_obj_id=? AND c_tr_id IN (0,?)");
							setSelPst.setLong(1, obj.id);
							setSelPst.setLong(2, trId);
							ResultSet rs = setSelPst.executeQuery();
							while (rs.next()) {
								String[] cnamesArr = cnames.split(",");
								for (String cname : cnamesArr) {
									Object nodeId = getValue(rs, cname, attr.typeClassId);
									if (nodeId != null) {
										String nodeIdStr = (nodeId instanceof byte[]) ? new String((byte[])nodeId) : (String)nodeId;
										jrbNodesToDelete.add(new Pair<Long, String>(attr.id, nodeIdStr));
									}
								}
							}
							rs.close();
							setSelPst.close();
						}
					}
				}

				qr.update(conn, "DELETE FROM " + tname
						+ " WHERE c_obj_id=? AND c_tr_id IN (0,?)",
						new Object[] { obj.id, trId });
			}
			// TODO Удаляем запись из кэша (используется это???)
			// removeObject(obj);
		}
	}

	protected void commitCreatedObject(KrnObject obj, long trId,
			List<KrnClass> superClss, List<KrnAttribute> attrs)
			throws SQLException, DriverException {
		commitCreatedObject(obj, trId, superClss, attrs, new TreeSet<>());
	}
	
	protected void commitCreatedObject(KrnObject obj, long trId,
			List<KrnClass> superClss, List<KrnAttribute> attrs, Set<Pair<Long,String>> jrbNodesToDelete)
			throws SQLException, DriverException {

		QueryRunner qr = new QueryRunner(true);
		// Удаляем удаленные множ и мультияз атрибуты
		for (KrnAttribute attr : attrs) {
			if (attr.rAttrId == 0) {
				if (attr.collectionType != COLLECTION_NONE) {
					String mtName = getAttrTableName(attr);
					String cnames = getColumnNames(attr);
					
					if (db.inJcrRepository(attr.id)) {
						PreparedStatement setSelPst = conn.prepareStatement("SELECT " + cnames + " FROM " + mtName + " WHERE c_obj_id=? AND c_tr_id=? and c_del<>0");
						setSelPst.setLong(1, obj.id);
						setSelPst.setLong(2, trId);
						ResultSet rs = setSelPst.executeQuery();
						while (rs.next()) {
							String[] cnamesArr = cnames.split(",");
							for (String cname : cnamesArr) {
								Object nodeId = getValue(rs, cname, attr.typeClassId);
								if (nodeId != null) {
									String nodeIdStr = (nodeId instanceof byte[]) ? new String((byte[])nodeId) : (String)nodeId;
									jrbNodesToDelete.add(new Pair<Long, String>(attr.id, nodeIdStr));
								}
							}
						}
						rs.close();
						setSelPst.close();
					}

					qr.update(conn, "DELETE FROM " + mtName
							+ " WHERE c_obj_id=? AND c_tr_id=? AND c_del<>0",
							new Object[] { obj.id, trId });
				}
			}
		}
		// Обновляем номер транзакции на 0
		for (KrnClass cls : superClss) {
			if (cls.id != ROOT_CLASS_ID && !cls.isVirtual()) {
				String tname = getClassTableName(cls.id);
				qr.update(conn, "UPDATE " + tname
						+ " SET c_tr_id=0 WHERE c_obj_id=? AND c_tr_id=? AND c_is_del=0",
						new Object[] { obj.id, trId });
			}
		}
	}
	public void rollbackLongTransaction(long trId) throws DriverException {
		try {
			saveDataLog();
			
			Set<Pair<Long, String>> jrbNodesToDelete = new HashSet<>();

			QueryRunner qr = new QueryRunner(true);
			// Выключаем проверку FK
			qr.update(conn, "SET FOREIGN_KEY_CHECKS=0");

			Set<Long> clsIds = new HashSet<Long>();
			PreparedStatement pst = conn
					.prepareStatement("SELECT DISTINCT c_parent_id FROM "+getDBPrefix()+"t_changes,"+getDBPrefix()+"t_clinks"
							+ " WHERE c_child_id=c_class_id AND c_tr_id=?");
			pst.setLong(1, trId);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				long clsId = rs.getLong("c_parent_id");
				KrnClass cls = db.getClassById(clsId);
				if (clsId != ROOT_CLASS_ID && !cls.isVirtual())
					clsIds.add(clsId);
			}
			rs.close();
			pst.close();

			// Удаляем записи в таблицах классов
			// записи в таблицах атрбиутов удаляются каскадно
			for (Long clsId : clsIds) {
				String ctName = getClassTableName(clsId);
				
				List<KrnAttribute> attrs = db.getAttributesByClassId(clsId, false);
				for (KrnAttribute attr : attrs) {
					if (attr.rAttrId == 0 && db.inJcrRepository(attr.id)) {
						String cnames = getColumnNames(attr);
						if (attr.collectionType != COLLECTION_NONE) {
							String mtName = getAttrTableName(attr);

							PreparedStatement setSelPst = conn.prepareStatement("SELECT " + cnames + " FROM " + mtName + " WHERE c_tr_id = ?");
							setSelPst.setLong(1, trId);
							rs = setSelPst.executeQuery();
							while (rs.next()) {
								String[] cnamesArr = cnames.split(",");
								for (String cname : cnamesArr) {
									Object nodeId = getValue(rs, cname, attr.typeClassId);
									if (nodeId != null) {
										String nodeIdStr = (nodeId instanceof byte[]) ? new String((byte[])nodeId) : (String)nodeId;
										jrbNodesToDelete.add(new Pair<Long, String>(attr.id, nodeIdStr));
									}
								}
							}
							rs.close();
							setSelPst.close();
						} else {
							PreparedStatement setSelPst = conn.prepareStatement("SELECT " + cnames + " FROM " + ctName + " WHERE c_tr_id = ?");
							setSelPst.setLong(1, trId);
							rs = setSelPst.executeQuery();
							while (rs.next()) {
								String[] cnamesArr = cnames.split(",");
								for (String cname : cnamesArr) {
									Object nodeId = getValue(rs, cname, attr.typeClassId);
									if (nodeId != null) {
										String nodeIdStr = (nodeId instanceof byte[]) ? new String((byte[])nodeId) : (String)nodeId;
										jrbNodesToDelete.add(new Pair<Long, String>(attr.id, nodeIdStr));
									}
								}
							}
							rs.close();
							setSelPst.close();
						}
					}
					
					// Удаляем все строчки в АТ талблицах класса
					// Необходимо ручное удаления из-за отключенных проверок FK
					if (attr.rAttrId == 0 && attr.collectionType != COLLECTION_NONE) {
						String mtName = getAttrTableName(attr);
						qr.update(conn, "DELETE FROM " + mtName	+ " WHERE c_tr_id=?", trId);
					}
				}
				
				if (hasListenerFor(clsId)) {
					PreparedStatement objPst = conn
							.prepareStatement("SELECT c_obj_id, c_uid FROM " + ctName 
									+ " t1 WHERE c_tr_id=? AND NOT EXISTS (SELECT 1 FROM " + ctName
									+ " t2 WHERE t2.c_obj_id=t1.c_obj_id AND t2.c_tr_id <> ?)");
					
					objPst.setLong(1, trId);
					objPst.setLong(2, trId);
					ResultSet objRs = objPst.executeQuery();
					while (objRs.next()) {
						long objId = objRs.getLong(1);
						String uid = getSanitizedString(objRs, 2);
						fireAttrChanged(new KrnObject(objId, uid, clsId), 2, 0, 0);
					}
					objRs.close();
					objPst.close();

					objPst = conn.prepareStatement("SELECT c_obj_id, c_uid FROM " + ctName 
									+ " t1 WHERE c_tr_id=? AND EXISTS (SELECT 1 FROM " + ctName
									+ " t2 WHERE t2.c_obj_id=t1.c_obj_id AND t2.c_tr_id <> ?)");
					
					objPst.setLong(1, trId);
					objPst.setLong(2, trId);
					objRs = objPst.executeQuery();
					while (objRs.next()) {
						long objId = objRs.getLong(1);
						String uid = getSanitizedString(objRs, 2);
						fireAttrChanged(new KrnObject(objId, uid, clsId), 1, 0, 0);
					}
					objRs.close();
					objPst.close();
				}
				
				qr.update(conn, "DELETE FROM " + ctName + " WHERE c_tr_id=?",
						trId);
			}

			// Включаем проверку FK
			qr.update(conn, "SET FOREIGN_KEY_CHECKS=1");

			// Удаляем из ct99 созданные в транзакции объекты
			int n = qr.update(conn,
					"DELETE "+getClassTableName(99)+" FROM "+getClassTableName(99)+","+getDBPrefix()+"t_changes WHERE c_obj_id=c_object_id " +
					"AND c_tr_id=? AND c_attr_id=1", trId);

			rollbackDataLog(trId);
			
			for (Pair<Long, String> node : jrbNodesToDelete)
				db.deleteRepositoryData(node.first, node.second);
			jrbNodesToDelete.clear();

		} catch (SQLException e) {
			throw convertException(e);
		}
	}
	
	public void rollbackLongTransactionComp(long trId) throws DriverException {
		try {
			saveDataLog();
			
			QueryRunner qr = new QueryRunner(true);
			// Выключаем проверку FK
			qr.update(conn, "SET FOREIGN_KEY_CHECKS=0");

			Set<Long> clsIds = new HashSet<Long>();
			PreparedStatement pst = conn
					.prepareStatement("SELECT DISTINCT c_parent_id FROM "+getDBPrefix()+"t_changes,"+getDBPrefix()+"t_clinks"
							+ " WHERE c_child_id=c_class_id AND c_tr_id=?");
			pst.setLong(1, trId);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				long clsId = rs.getLong("c_parent_id");
				KrnClass cls = getClassByIdComp(clsId);
				if (clsId != ROOT_CLASS_ID && !cls.isVirtual())
					clsIds.add(clsId);
			}
			rs.close();
			pst.close();

			PreparedStatement attrPst = conn
					.prepareStatement("SELECT c_id,c_class_id FROM "+getDBPrefix()+"t_attrs"
							+ " WHERE c_class_id=? AND c_col_type>0 AND c_rattr_id IS NULL");

			// Удаляем записи в таблицах классов
			// записи в таблицах атрбиутов удаляются каскадно
			for (Long clsId : clsIds) {
				// Удаляем все строчки в АТ талблицах класса
				// Необходимо ручное удаления из-за отключенных проверок FK
				attrPst.setLong(1, clsId);
				ResultSet attrRs = attrPst.executeQuery();
				while (attrRs.next()) {
					long attrId = attrRs.getLong(1);
					KrnAttribute a = db.getAttributeById(attrId);
					String atName = getAttrTableName(a);
					qr.update(conn, "DELETE FROM " + atName
							+ " WHERE c_tr_id=?", trId);
				}
				attrRs.close();

				String ctName = getClassTableName(clsId);
				qr.update(conn, "DELETE FROM " + ctName + " WHERE c_tr_id=?",
						trId);
			}
			attrPst.close();

			// Включаем проверку FK
			qr.update(conn, "SET FOREIGN_KEY_CHECKS=1");

			// Удаляем из ct99 созданные в транзакции объекты
			int n = qr.update(conn,
					"DELETE "+getClassTableName(99)+" FROM "+getClassTableName(99)+","+getDBPrefix()+"t_changes WHERE c_obj_id=c_object_id " +
					"AND c_tr_id=? AND c_attr_id=1", trId);

			rollbackDataLog(trId);
		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	@Override
	public KrnObject createObject(long classId, long trId, long id, String uid)
			throws DriverException {
		return createObject(classId, trId, id, uid, false, null);
	}

	@Override
	public KrnObject createObject(long classId, long trId, long id, String uid,
			Map<Pair<KrnAttribute, Long>, Object> initValues) throws DriverException {
		return createObject(classId, trId, id, uid, false, initValues);
	}

	public KrnObject createObject(long classId, long trId, long objId,
			String uid, boolean rootOnly, Map<Pair<KrnAttribute, Long>, Object> initValues) throws DriverException {
		return createObject(classId, trId, objId, uid, rootOnly, initValues, true);
	}
	
	public KrnObject createObject(long classId, long trId, long objId,
			String uid, boolean rootOnly, Map<Pair<KrnAttribute, Long>, Object> initValues, boolean log) throws DriverException {
		
		if (trId == -1)
			throw new DriverException("Нельзя использовать значение транзакции -1 при создании объекта!", ErrorCodes.DRV_ILLEGAL_ARGUMENT_EXCEPTION);

		try {
			QueryRunner qr = new QueryRunner(true);
			Long lid;
			if (objId > 0) {
				lid = objId;
				qr.update(conn,
						"INSERT INTO "+getClassTableName(99)+" (c_obj_id,c_class_id)"
								+ " VALUES (?,?)", new Object[] { lid,
								classId });
			} else {
				qr.update(conn,
						"INSERT INTO "+getClassTableName(99)+" (c_class_id) VALUES (?)",
						new Object[] { classId });
				lid = getLastObjectId();
			}
			if (uid == null) {
				uid = getBaseId() + "." + lid;
			}
			qr.update(conn,
					"UPDATE "+getClassTableName(99)+" SET c_uid=? WHERE c_obj_id=?",
					new Object[] { uid, lid });
			if (!rootOnly) {
				List<KrnClass> clss = db.getSuperClasses(classId);
				for (Object cls1 : clss) {
					KrnClass cls = (KrnClass) cls1;
					if (cls.id != ROOT_CLASS_ID && !cls.isVirtual()) {
						String tname = getClassTableName(cls.id);
						String cols = "c_uid,c_obj_id,c_class_id,c_tr_id";
						String vals = "?,?,?,?";
						if (initValues != null) {
							// Проход 1. Формирование SQL
							for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
								if (key.first.classId == cls.id && key.first.rAttrId == 0) {
									cols += "," + getColumnName(key.first, key.second);
									vals += ",?";
								}
							}
						}
						PreparedStatement pst = null;
						String sql = null;
						StringBuilder toLog = new StringBuilder();
						try {
							toLog.append(sql = "INSERT INTO " + tname + "(" + cols + ") VALUES (" + vals + ")").append("\r\n");
							pst = conn.prepareStatement(sql);
							pst.setString(1, uid);
							toLog.append("PARAMETER 1(uid): " + uid).append("\r\n");
							pst.setLong(2, lid);
							toLog.append("PARAMETER 2(lang): " + lid).append("\r\n");
							pst.setLong(3, classId);
							toLog.append("PARAMETER 3(class): " + classId).append("\r\n");
							pst.setLong(4, trId);
							toLog.append("PARAMETER 4(tr): " + trId).append("\r\n");
							if (initValues != null) {
								// Проход 2. Установка значений
								int i = 5;
								for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
									if (key.first.classId == cls.id && key.first.rAttrId == 0) {
										toLog.append("PARAMETER " + i + ": " + initValues.get(key)).append("\r\n");
										
										Object value = initValues.get(key);
						            	if (key.first.typeClassId == PC_BLOB && db.inJcrRepository(key.first.id)) {
						            		value = db.putRepositoryData(key.first.id, lid, trId, (byte[])value);
						            		addNotCommitedJrbNode(key.first.id, value);
						            	} else if (key.first.typeClassId == PC_MEMO && db.inJcrRepository(key.first.id)) {
						            		value = db.putRepositoryData(key.first.id, lid, trId, (String)value);
						            		addNotCommitedJrbNode(key.first.id, value);
						            	}
										setValue(pst, i++, key.first.typeClassId, value);
									}
								}
							}
							pst.executeUpdate();
						} catch (SQLException e) {
							this.log.error(e.getMessage());
							this.log.error(toLog.toString());
							throw e;
						} finally {
							DbUtils.closeQuietly(pst);
						}
					}
				}
			}
			KrnObject obj = new KrnObject(lid, uid, classId);
			if(!isInstallDb)
				logVcsDataChanges(obj, 1, 0, null,trId);
			if (log){
				logDataChanges(obj, 1, 0, trId);
				if (initValues != null) {
					for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
						logDataChanges(obj, key.first.id, key.second, trId);
					}
				}
			}
			
			if (initValues != null) {
				for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
					if (key.first.rAttrId > 0) {
						setValueImpl(obj, key.first, 0, 0, trId, initValues.get(key), false);
					}
				}
			}
			return obj;
		} catch (SQLException e) {
			throw convertException(e);
		}
	}
	
	//TODO MsSQLDriver3
	@Override
	public void createObjects(long classId, long trId, List<Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>>> objValues, boolean log) throws DriverException {
		PreparedStatement pst = null, uidPst = null;
		ResultSet rs = null;
		
		Map<Long, PreparedStatement> clsPsts = new TreeMap<>();

		try {
			// создаем PreparedStatement для ct99
			
			int batchSize = 50;
			long[] objIds = new long[objValues.size()]; 
			
			int i=0;

			if (objValues.size() > batchSize) {
				StringBuilder sb = new StringBuilder("INSERT INTO ").append(getClassTableName(99)).append(" (c_class_id) VALUES (?)");
				for (int k=1; k<batchSize; k++) {
					sb.append(",(?)");
				}
				pst = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);

				for (i=0; i < objValues.size()/batchSize; i++) {
					for (int k=0; k<batchSize; k++) {
						pst.setLong(k+1, classId);
					}
	        		pst.executeUpdate();
	        		
	        		rs = pst.getGeneratedKeys();
	    			
	    			ResultSetMetaData md = rs.getMetaData();

	    			int k = 0;
	    			while (rs.next()) {
	    				objIds[i * batchSize + k++] = rs.getLong(1);
	    			}
				}
				rs.close();
				pst.close();
			}
			if (i * batchSize < objValues.size()) {
				StringBuilder sb = new StringBuilder("INSERT INTO ").append(getClassTableName(99)).append(" (c_class_id) VALUES (?)");
				for (int k = i * batchSize + 1; k<objValues.size(); k++) {
					sb.append(",(?)");
				}
				pst = conn.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS);

				for (int k = 0; k < objValues.size() - i * batchSize; k++) {
					pst.setLong(k+1, classId);
				}
        		pst.executeUpdate();
        		
        		rs = pst.getGeneratedKeys();
    			
    			ResultSetMetaData md = rs.getMetaData();

    			int k = 0;
    			while (rs.next()) {
    				objIds[i * batchSize + k++] = rs.getLong(1);
    			}
    			
				rs.close();
				pst.close();
			}
			
			// Собираем все возможные колонки
			Set<Pair<KrnAttribute, Long>> allKeys = new HashSet<>();
			for (Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>> objValue : objValues) {
        		KrnObject obj = objValue.first;
        		Map<Pair<KrnAttribute, Long>, Object> initValues = objValue.second;
        		
				for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
					if (key.first.rAttrId == 0) {
						allKeys.add(key);
					}
				}
			}
			
			uidPst = conn.prepareStatement("UPDATE "+getClassTableName(99)+" SET c_uid=? WHERE c_obj_id=?");

			// создаем PreparedStatement для каждого класса
			List<KrnClass> clss = db.getSuperClasses(classId);
			for (KrnClass cls : clss) {
				if (cls.id != ROOT_CLASS_ID && !cls.isVirtual()) {
					String tname = getClassTableName(cls.id);
					String cols = "c_uid,c_obj_id,c_class_id,c_tr_id";
					String vals = "?,?,?,?";
					// Проход 1. Формирование SQL
					for (Pair<KrnAttribute, Long> key : allKeys) {
						if (key.first.classId == cls.id) {
							cols += "," + getColumnName(key.first, key.second);
							vals += ",?";
						}
					}
					PreparedStatement clsPst = conn.prepareStatement("INSERT INTO " + tname + "(" + cols + ") VALUES (" + vals + ")");
					clsPsts.put(cls.id, clsPst);
				}
			}

			int k = 0;
			for (Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>> objValue : objValues) {
        		KrnObject obj = objValue.first;
        		Map<Pair<KrnAttribute, Long>, Object> initValues = objValue.second;

        		long objId = objIds[k++];
    			String uid = getBaseId() + "." + objId;
    			
    			uidPst.setString(1, uid);
    			uidPst.setLong(2, objId);
    			uidPst.addBatch();
    			
    			for (long clsId : clsPsts.keySet()) {
					PreparedStatement clsPst = clsPsts.get(clsId);

					clsPst.setString(1, uid);
					clsPst.setLong(2, objId);
					clsPst.setLong(3, classId);
					clsPst.setLong(4, trId);
					if (initValues != null) {
						// Проход 2. Установка значений
						i = 5;
						for (Pair<KrnAttribute, Long> key : allKeys) {
							if (key.first.classId == clsId && key.first.rAttrId == 0) {
								Object value = initValues.get(key);
				            	if (key.first.typeClassId == PC_BLOB && db.inJcrRepository(key.first.id)) {
				            		value = db.putRepositoryData(key.first.id, objId, trId, (byte[])value);
				            		addNotCommitedJrbNode(key.first.id, value);
				            	} else if (key.first.typeClassId == PC_MEMO && db.inJcrRepository(key.first.id)) {
				            		value = db.putRepositoryData(key.first.id, objId, trId, (String)value);
				            		addNotCommitedJrbNode(key.first.id, value);
				            	}

								setValue(clsPst, i++, key.first.typeClassId, value);
							}
						}
					}
					clsPst.addBatch();
				}
    			
    			obj.id = objId;
    			obj.uid = uid;
			}
			uidPst.executeBatch();
			for (long clsId : clsPsts.keySet()) {
				PreparedStatement clsPst = clsPsts.get(clsId);
				clsPst.executeBatch();
			}
			
			for (Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>> objValue : objValues) {
				KrnObject obj = objValue.first;
        		Map<Pair<KrnAttribute, Long>, Object> initValues = objValue.second;	

        		logVcsDataChanges(obj, 1, 0, null,trId);
    			if (log) {
        			logDataChanges(obj, 1, 0, trId);
    				if (initValues != null) {
    					for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
    						logDataChanges(obj, key.first.id, key.second, trId);
    					}
    				}
    			}
    			
    			if (initValues != null) {
    				for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
    					if (key.first.rAttrId > 0) {
    						setValueImpl(obj, key.first, 0, 0, trId, initValues.get(key), false);
    					}
    				}
    			}
			}
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
			DbUtils.closeQuietly(uidPst);
			for (PreparedStatement clsPst : clsPsts.values()) {
				DbUtils.closeQuietly(clsPst);
			}
		}
	}

	@Override
	public KrnObject updateObject(long classId, long trId, long objId, String uid,
			Map<Pair<KrnAttribute, Long>, Object> initValues) throws DriverException {
		return updateObject(classId, trId, objId, uid, initValues, true);
	}
	
	@Override
	public KrnObject updateObject(long classId, long trId, long objId, String uid,
			Map<Pair<KrnAttribute, Long>, Object> initValues, boolean log) throws DriverException {
		PreparedStatement pst = null;
		ResultSet rs = null;
		KrnObject obj=null;
		try {
			Long lid;
			boolean recordExist=false;
			if (objId > 0 && initValues != null) {
				try {
					obj=getObjectById(objId);
					//Проверяем есть ли запись по объекту в данной транзакции
					pst = conn.prepareStatement("SELECT c_obj_id FROM "+getClassTableName(obj.classId)
							+ " WHERE c_obj_id=? AND c_tr_id = ?");
					pst.setLong(1, objId);
					pst.setLong(2, trId);
					rs = pst.executeQuery();
					while (rs.next()) {
						recordExist=true;
						break;
					}
				} catch (SQLException e) {
					throw e;
				} finally {
					DbUtils.closeQuietly(rs);
					DbUtils.closeQuietly(pst);
				}
				if (recordExist) {//Обновляем существующую запись с данной транзакцией
					List<KrnClass> clss = db.getSuperClasses(classId);
					for (Object cls1 : clss) {
						KrnClass cls = (KrnClass) cls1;
						if (cls.id != ROOT_CLASS_ID && !cls.isVirtual()) {
							String sql = makeUpdateSQL(cls.id, initValues);
							if(sql != null){
								try {
									pst = conn.prepareStatement(sql);
									int i = 1;
									// Проход 2. Установка значений
									for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
										if (key.first.classId == cls.id && key.first.rAttrId == 0) {
											Object value = initValues.get(key);
											
											if (db.inJcrRepository(key.first.id)) {
							            		String cname = getColumnName(key.first, key.second);
							            		StringBuilder sql2 = new StringBuilder("SELECT ").append(cname).append(" FROM ").append(getClassTableName(cls.id))
														.append(" WHERE c_obj_id=? AND c_tr_id=?");
	
												PreparedStatement setSelPst = conn.prepareStatement(sql2.toString());
												setSelPst.setLong(1, objId);
												setSelPst.setLong(2, trId);
												ResultSet rs2 = setSelPst.executeQuery();
												while (rs2.next()) {
													Object nodeId = getValue(rs2, cname, key.first.typeClassId);
													if (nodeId != null) {
														addRewrittenJrbNode(key.first.id, nodeId);
													}
												}
												rs2.close();
												setSelPst.close();

								            	if (key.first.typeClassId == PC_BLOB) {
								            		value = db.putRepositoryData(key.first.id, objId, trId, (byte[])value);
								            		addNotCommitedJrbNode(key.first.id, value);
								            	} else if (key.first.typeClassId == PC_MEMO) {
								            		value = db.putRepositoryData(key.first.id, objId, trId, (String)value);
								            		addNotCommitedJrbNode(key.first.id, value);
								            	}
											}
											setValue(pst, i++, key.first.typeClassId, value);
										}
									}
									pst.setString(i++, uid);
									pst.setLong(i++, objId);
									pst.setLong(i++, classId);
									pst.setLong(i++, trId);
									pst.executeUpdate();
								} catch (SQLException e) {
									throw e;
								} finally {
									DbUtils.closeQuietly(rs);
									DbUtils.closeQuietly(pst);
								}
							}
						}
					}
				} else {//Создаем новую с данной транзакцией
					List<KrnClass> clss = db.getSuperClasses(classId);
					for (Object cls1 : clss) {
						KrnClass cls = (KrnClass) cls1;
						if (cls.id != ROOT_CLASS_ID && !cls.isVirtual()) {
							String sql = makeCreateSQL(cls.id, initValues);
							try {
								pst = conn.prepareStatement(sql);
								pst.setString(1, uid);
								pst.setLong(2, objId);
								pst.setLong(3, classId);
								pst.setLong(4, trId);
								if (initValues != null) {
									// Проход 2. Установка значений
									int i = 5;
									for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
										if (key.first.classId == cls.id && key.first.rAttrId == 0) {
											Object value = initValues.get(key);
							            	if (key.first.typeClassId == PC_BLOB && db.inJcrRepository(key.first.id)) {
							            		value = db.putRepositoryData(key.first.id, objId, trId, (byte[])value);
							            	} else if (key.first.typeClassId == PC_MEMO && db.inJcrRepository(key.first.id)) {
							            		value = db.putRepositoryData(key.first.id, objId, trId, (String)value);
							            	}

											setValue(pst, i++, key.first.typeClassId, value);
										}
									}
								}
								pst.executeUpdate();
							} catch (SQLException e) {
								throw e;
							} finally {
								DbUtils.closeQuietly(rs);
								DbUtils.closeQuietly(pst);
							}
						}
					}
				}
				
    			if (log) {
					for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
						logVcsDataChanges(obj, key.first.id, key.second, initValues.get(key), trId);
						logDataChanges(obj, key.first.id, key.second, trId);
					}
    			}
    			
				for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
					if (key.first.rAttrId > 0) {
						setValueImpl(obj, key.first, 0, 0, trId, initValues.get(key), false);
					}
				}
			}
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
		}
		return obj;
	}
	
	private String makeUpdateSQL(long classId, Map<Pair<KrnAttribute, Long>, Object> initValues) throws DriverException {
		String tname = getClassTableName(classId);
		StringBuilder sb = new StringBuilder("UPDATE ").append(tname);

		boolean hasSet = false;
		// Проход 1. Формирование SQL
		for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
			if (key.first.classId == classId && key.first.rAttrId == 0) {
				sb.append(!hasSet ? " SET " : ", ").append(getColumnName(key.first, key.second)).append("=?");
				hasSet = true;
			}
		}
		return hasSet ? sb.append(" WHERE c_uid=? AND c_obj_id=? AND c_class_id=? AND c_tr_id=?").toString() : null;	
	}

	private String makeCreateSQL(long classId, Map<Pair<KrnAttribute, Long>, Object> initValues) throws DriverException {
		String tname = getClassTableName(classId);
		StringBuilder sb = new StringBuilder("INSERT INTO ").append(tname).append("(c_uid,c_obj_id,c_class_id,c_tr_id");
		
		StringBuilder vals = new StringBuilder("?,?,?,?");
		// Проход 1. Формирование SQL
		for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
			if (key.first.classId == classId && key.first.rAttrId == 0) {
				sb.append(",").append(getColumnName(key.first, key.second));
				vals.append(",?");
			}
		}
		return sb.append(") VALUES (").append(vals).append(")").toString();
	}

	@Override
	public void updateObjects(long classId, long trId, List<Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>>> objValues, boolean log) throws DriverException {
		PreparedStatement selPst = null;
		ResultSet rs = null;
		
		Map<Pair<Long, String>, PreparedStatement> psts = new HashMap<>();
		
		Map<Long, Boolean> recordExistMap = new TreeMap<>();
		
		try {
			String tableName = getClassTableName(classId);
			
			//Проверяем есть ли запись по объекту в данной транзакции
			selPst = conn.prepareStatement("SELECT c_obj_id FROM " + tableName
					+ " WHERE c_obj_id=? AND c_tr_id = ?");

			int k = 0;
			for (Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>> objValue : objValues) {
        		KrnObject obj = objValue.first;
        		Map<Pair<KrnAttribute, Long>, Object> initValues = objValue.second;

        		if (obj != null && obj.id > 0 && initValues != null) {
        			selPst.setLong(1, obj.id);
        			selPst.setLong(2, trId);
					rs = selPst.executeQuery();
					while (rs.next()) {
						recordExistMap.put(obj.id, true);
						break;
					}
					
					boolean recordExist = recordExistMap.get(obj.id);
					
					if (recordExist) {//Обновляем существующую запись с данной транзакцией
						List<KrnClass> clss = db.getSuperClasses(classId);
						for (Object cls1 : clss) {
							KrnClass cls = (KrnClass) cls1;
							if (cls.id != ROOT_CLASS_ID && !cls.isVirtual()) {
								String sql = makeUpdateSQL(cls.id, initValues);
								if (sql != null) {
									Pair<Long, String> pstKey = new Pair<Long, String>(cls.id, sql);
									PreparedStatement pst = psts.get(pstKey);
									if (pst == null) {
										pst = conn.prepareStatement(sql);
										psts.put(pstKey, pst);
									}
									
									int i = 1;
									// Проход 2. Установка значений
									for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
										if (key.first.classId == cls.id && key.first.rAttrId == 0) {
											Object value = initValues.get(key);
											
											if (db.inJcrRepository(key.first.id)) {
							            		String cname = getColumnName(key.first, key.second);
							            		StringBuilder sql2 = new StringBuilder("SELECT ").append(cname).append(" FROM ").append(getClassTableName(cls.id))
														.append(" WHERE c_obj_id=? AND c_tr_id=?");
	
												PreparedStatement setSelPst = conn.prepareStatement(sql2.toString());
												setSelPst.setLong(1, obj.id);
												setSelPst.setLong(2, trId);
												ResultSet rs2 = setSelPst.executeQuery();
												while (rs2.next()) {
													Object nodeId = getValue(rs2, cname, key.first.typeClassId);
													if (nodeId != null) {
														addRewrittenJrbNode(key.first.id, nodeId);
													}
												}
												rs2.close();
												setSelPst.close();

												if (key.first.typeClassId == PC_BLOB) {
								            		value = db.putRepositoryData(key.first.id, obj.id, trId, (byte[])value);
								            		addNotCommitedJrbNode(key.first.id, value);
								            	} else if (key.first.typeClassId == PC_MEMO) {
								            		value = db.putRepositoryData(key.first.id, obj.id, trId, (String)value);
								            		addNotCommitedJrbNode(key.first.id, value);
								            	}
											}

											setValue(pst, i++, key.first.typeClassId, value);
										}
									}
									pst.setString(i++, obj.uid);
									pst.setLong(i++, obj.id);
									pst.setLong(i++, classId);
									pst.setLong(i++, trId);
									pst.addBatch();
								}
							}
						}
					} else {//Создаем новую с данной транзакцией
						List<KrnClass> clss = db.getSuperClasses(classId);
						for (Object cls1 : clss) {
							KrnClass cls = (KrnClass) cls1;
							if (cls.id != ROOT_CLASS_ID && !cls.isVirtual()) {
								String sql = makeCreateSQL(cls.id, initValues);
								Pair<Long, String> pstKey = new Pair<Long, String>(cls.id, sql);
								PreparedStatement pst = psts.get(pstKey);
								if (pst == null) {
									pst = conn.prepareStatement(sql);
									psts.put(pstKey, pst);
								}
								pst.setString(1, obj.uid);
								pst.setLong(2, obj.id);
								pst.setLong(3, classId);
								pst.setLong(4, trId);
								if (initValues != null) {
									// Проход 2. Установка значений
									int i = 5;
									for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
										if (key.first.classId == cls.id && key.first.rAttrId == 0) {
											Object value = initValues.get(key);
							            	if (key.first.typeClassId == PC_BLOB && db.inJcrRepository(key.first.id)) {
							            		value = db.putRepositoryData(key.first.id, obj.id, trId, (byte[])value);
							            	} else if (key.first.typeClassId == PC_MEMO && db.inJcrRepository(key.first.id)) {
							            		value = db.putRepositoryData(key.first.id, obj.id, trId, (String)value);
							            	}

											setValue(pst, i++, key.first.typeClassId, value);
										}
									}
								}
								pst.addBatch();
							}
						}
					}
        		}
			}
			for (Pair<Long, String> pstKey : psts.keySet()) {
				PreparedStatement pst = psts.get(pstKey);
				pst.executeBatch();
			}
			
			for (Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>> objValue : objValues) {
				KrnObject obj = objValue.first;
        		Map<Pair<KrnAttribute, Long>, Object> initValues = objValue.second;	

        		if (obj != null && obj.id > 0 && initValues != null) {
	    			if (log) {
						for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
							logVcsDataChanges(obj, key.first.id, key.second, initValues.get(key), trId);
							logDataChanges(obj, key.first.id, key.second, trId);
						}
	    			}
	    			
					for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
						if (key.first.rAttrId > 0) {
							setValueImpl(obj, key.first, 0, 0, trId, initValues.get(key), false);
						}
					}
        		}
			}
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(selPst);
			for (PreparedStatement pst : psts.values()) {
				DbUtils.closeQuietly(pst);
			}
		}
	}

	public KrnObject cloneObject(KrnObject obj, long getTrId, long trId)
			throws DriverException {
		try {
			KrnObject cobj = createObject(obj.classId, trId, 0, null, true, null);
			long srcTrId = openTransaction(obj, getTrId, true);
			copyValues(obj, srcTrId, cobj, trId, true);
			return cobj;
		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	public void deleteObject(KrnObject obj, long trId, boolean deleteRefs) throws DriverException {
		
		if (trId == -1)
			throw new DriverException("Нельзя использовать значение транзакции -1 при удалении объекта!", ErrorCodes.DRV_ILLEGAL_ARGUMENT_EXCEPTION);
		
		// begin Обрабатываем агрегатные связи
		List<KrnAttribute> attrs = db.getAttributesByClassId(obj.classId, true);
		long[] objIds = { obj.id };
		for (KrnAttribute attr : attrs) {
			if (attr.isAggregate() && attr.rAttrId != 0) {
				SortedSet<Value> values = getValues(objIds, null, attr.id, 0, trId);
				if (values.size() > 0) {
					if (attr.collectionType == COLLECTION_SET) {
						Collection<Object> vobjs = new ArrayList<Object>();
						for (Value value : values) {
							if (value.value instanceof KrnObject) {
								vobjs.add(value.value);
							}
						}
						deleteValue(obj.id, attr.id, vobjs, trId, deleteRefs);
					} else if (attr.collectionType == COLLECTION_NONE) {
						deleteValue(obj.id, attr.id, new int[] {0}, 0, trId, deleteRefs);
					}
				}
			}
		}
		// end обработка агрегатных связей
		// Проверям наличие ссылок на удаляемый объект
		if (!deleteRefs && checkRefs) {
			String msg = checkRefs(obj, trId);
			if (msg != null) {
				KrnClass cls = db.getClassById(obj.classId);
				throw new DriverException("Невозможно удалить объект " +
						cls.name + "[id=" + obj.id +
						"] так как он используется другими объектами:\n" + msg);
			}
		}
		// end Проверям наличие ссылок на удаляемый объект
		List<KrnClass> clss = db.getSuperClasses(obj.classId);
		StringBuilder sql = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			logVcsDataChanges(obj, 2, 0, null,trId);
			if (trId == 0) {
				QueryRunner qr = new QueryRunner(true);
				for (KrnClass cls : clss) {
					if (deleteRefs || Driver2.deleteRefs) {
						// Удаляем ссылки на объект
						attrs = db.getAttributesByTypeId(cls.id, false);
						for (KrnAttribute attr : attrs) {
							if (attr.rAttrId == 0) { 
								String cmName = getColumnName(attr);
								if (attr.collectionType != COLLECTION_NONE) {
									String atName = getAttrTableName(attr);
									qr.update(conn, "DELETE FROM " + atName + " WHERE "
											+ cmName + "=?", obj.id);
								} else {
									String ctName = getClassTableName(attr.classId);
									qr.update(conn, "UPDATE " + ctName + " SET " + cmName
											+ "=NULL WHERE " + cmName + "=?", obj.id);
								}
							}
						}
					}
					String tname = getClassTableName(cls.id);
					// Удаляем запись из таблицы класса
					if (cls.id == ROOT_CLASS_ID) {
						sql = new StringBuilder("SELECT COUNT(c_obj_id) FROM ").append(tname).append(" WHERE c_obj_id=?");
						pst = conn.prepareStatement(sql.toString());
						pst.setLong(1, obj.id);
						rs = pst.executeQuery();
						if (rs.next() && rs.getInt(1) > 0) {
							rs.close();
							pst.close();

							sql = new StringBuilder("DELETE FROM ").append(tname).append(" WHERE c_obj_id=?");
							int c = qr.update(conn, sql.toString(), new Object[] {obj.id});
							//if (c < 1)
							//	throw new DriverException("Удаление запрещено правилами FGAC", ErrorCodes.ERROR_FGAC_NOT_ALLOW);
						}else{//Закрываем открытый резалтсет
							rs.close();
							pst.close();
						}

					} else if (!cls.isVirtual()) {
						sql = new StringBuilder("SELECT COUNT(c_obj_id) FROM ").append(tname).append(" WHERE c_obj_id=? AND c_tr_id=?");
						pst = conn.prepareStatement(sql.toString());
						pst.setLong(1, obj.id);
						pst.setLong(2, trId);
						rs = pst.executeQuery();
						if (rs.next() && rs.getInt(1) > 0) {
							rs.close();
							pst.close();

							sql = new StringBuilder("DELETE FROM ").append(tname).append(" WHERE c_obj_id=? AND c_tr_id=0");
							int c = qr.update(conn, sql.toString(),	new Object[] {obj.id});
							//if (c < 1)
							//	throw new DriverException("Удаление запрещено правилами FGAC", ErrorCodes.ERROR_FGAC_NOT_ALLOW);
						}else{//Закрываем открытый резалтсет
							rs.close();
							pst.close();
						}
					}
					// TODO (Используется???) Удаляем запись из кэша
					// removeObject(obj);
				}
			} else {
				trId = openTransaction(obj, trId, false);
				QueryRunner qr = new QueryRunner(true);
				for (KrnClass cls : clss) {
					if (cls.id != ROOT_CLASS_ID && !cls.isVirtual()) {
						String tname = getClassTableName(cls.id);
						int c = qr.update(conn, "UPDATE " + tname
								+ " SET c_is_del=1 WHERE c_obj_id=? AND c_tr_id=?",
								new Object[] { obj.id, trId });
					}
				}
			}
			logDataChanges(obj, 2, 0, trId);
		} catch (SQLException e) {
			log.error(sql);
			log.error(e, e);
			throw convertException(e);
		} catch (DriverException e) {
			log.error(sql);
			log.error(e, e);
			throw e;
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
		}
	}

	public List<KrnObject> getObjectsByUids(String[] uids, long trId, boolean dirty) throws DriverException {
		List<KrnObject> res = new ArrayList<KrnObject>(uids.length);
		for (String uid : uids) {
			KrnObject obj = getObjectByUid(uid, trId, dirty);
			if (obj != null)
				res.add(obj);
		}
		return res;
	}

	public KrnObject getObjectByUid(String uid, long trId, boolean dirty) throws DriverException {
		KrnObject res = null;
		ResultSet rs = null;
		try {
            if (pstObjByUid == null) {
            	pstObjByUid = conn.prepareStatement("SELECT c_obj_id,c_class_id FROM "+getClassTableName(99)+" WHERE c_uid=?");
            }
			pstObjByUid.setString(1, uid);
			rs = pstObjByUid.executeQuery();
			if (rs.next()) {
				res = new KrnObject(Funcs.normalizeInput(rs.getLong(1)), uid, Funcs.normalizeInput(rs.getLong(2)));
			}
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
		}
		return res;
	}

	@Override
	public KrnObject getClassObjectByUid(long clsId, String uid, long trId, boolean isDirty) throws DriverException {
		KrnObject res = null;
		StringBuilder sql = new StringBuilder("SELECT c_obj_id,c_class_id FROM "+getClassTableName(clsId));
		sql.append(" WHERE c_uid=?");
		if (trId != -1)
			sql.append(" AND c_tr_id IN (0,?)");
		if (!isDirty)
			sql.append(" AND c_is_del=?");
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(sql.toString());
			int i = 2;
			if (trId != -1)
				pst.setLong(i++, trId);
			if (!isDirty)
				pst.setBoolean(i, false);
			pst.setString(1, uid);
			rs = pst.executeQuery();
			if (rs.next()) {
				res = new KrnObject(rs.getLong(1), uid, rs.getLong(2));
			}
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
		}
		return res;	
	}
	
    public KrnObject getObjectById(long id) throws DriverException {
        KrnObject obj = null;
        ResultSet rs = null;
        try {
            if (pstObjById == null) {
                pstObjById = conn.prepareStatement("SELECT c_uid,c_class_id FROM "+getClassTableName(99)+" WHERE c_obj_id=?");
            }
            pstObjById.setLong(1, id);
            rs = pstObjById.executeQuery();
            if (rs.next()) {
                obj = new KrnObject(id, getSanitizedString(rs, 1), rs.getLong(2));
            }else {
        		log.warn("Объект id="+id+" отсутствует в таблице "+getClassTableName(99)+" !");
            }
        } catch (SQLException e) {
            throw convertException(e);
        } finally {
            DbUtils.closeQuietly(rs);
        }
        return obj;
    }

	public KrnObject getObjectById(long clsId, long id, long trId, boolean dirty) throws DriverException {
		
		KrnObject res = null;
		
		StringBuilder sql = new StringBuilder("SELECT c_uid,c_class_id FROM "+getClassTableName(clsId));
		sql.append(" WHERE c_obj_id=?");
		if (trId != -1)
			sql.append(" AND c_tr_id IN (0,?)");
		if (!dirty)
			sql.append(" AND c_is_del=?");
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(sql.toString());
			int i = 2;
			if (trId != -1)
				pst.setLong(i++, trId);
			if (!dirty)
				pst.setBoolean(i, false);
			
			pst.setLong(1, id);
			rs = pst.executeQuery();
			if (rs.next()) {
				res = new KrnObject(id, getSanitizedString(rs, 1), rs.getLong(2));
			}
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
		}
		
		return res;
    }
	
	public List<KrnObject> getObjects(long classId, long trId)
			throws DriverException {
		return getObjects(classId, new int[1], trId);
	}

	public List<KrnObject> getObjects(long classId, int[] limit, long trId) throws DriverException {
		try {
			QueryRunner qr = new QueryRunner(true);
			ResultSetHandler rh = new ObjectRsh(limit);
			StringBuilder sql = new StringBuilder("SELECT DISTINCT c_obj_id,c_uid,c_class_id FROM " + getClassTableName(classId));
			if (classId == 99) {
				return (List<KrnObject>) qr.query(conn, sql.toString(), rh);
			} else {
				sql.append(" WHERE c_is_del=0");
				if (trId > 0) {
					sql.append(" AND c_tr_id IN (0,?)");
					if(limit.length>0 && limit[0]>0)
						addLimit(sql,limit[0],0);
					return (List<KrnObject>) qr.query(conn, sql.toString(), rh, trId);
				} else {
					if (trId == 0) {
						sql.append(" AND c_tr_id=0");
					} else if (trId == -2) {
						sql.append(" AND c_tr_id>0");
					}
					if(limit.length>0 && limit[0]>0)
						addLimit(sql,limit[0],0);
					return (List<KrnObject>) qr.query(conn, sql.toString(), rh);
				}
			}
		} catch (SQLException e) {
			throw convertException(e);
		}
	}
	
	public List<KrnObject> getOwnObjects(long classId, long trId) throws DriverException {
		try {
			int[] limit = new int[1];
			QueryRunner qr = new QueryRunner(true);
			ResultSetHandler rh = new ObjectRsh(limit);
			StringBuilder sql = new StringBuilder("SELECT DISTINCT c_obj_id,c_uid,c_class_id FROM " + getClassTableName(classId));
			sql.append(" WHERE c_class_id = " + classId);
			if (classId == 99) {
				return (List<KrnObject>) qr.query(conn, sql.toString(), rh);
			} else {
				sql.append(" AND c_is_del=0");
				if (trId > 0) {
					sql.append(" AND c_tr_id IN (0,?)");
					return (List<KrnObject>) qr.query(conn, sql.toString(), rh, trId);
				} else {
					if (trId == 0) {
						sql.append(" AND c_tr_id=0");
					} else if (trId == -2) {
						sql.append(" AND c_tr_id>0");
					}
					return (List<KrnObject>) qr.query(conn, sql.toString(), rh);
				}
			}
		} catch (SQLException e) {
			throw convertException(e);
		}
	}

    public List<KrnObject> getObjectsByAttribute(long classId, long attrId, long langId, int operation, Object value, long tid, KrnAttribute[] krnAttrs) throws DriverException {
        KrnAttribute attr = db.getAttributeById(attrId);	// Атрибут, по которому будет выполняться поиск
        KrnAttribute rattr = attr.rAttrId != 0 ? db.getAttributeById(attr.rAttrId) : null;
        boolean isArray = attr.collectionType == COLLECTION_ARRAY;
        boolean isSet = attr.collectionType == COLLECTION_SET;
        boolean medTbl = attr.rAttrId == 0 && (isSet || isArray);
        List<KrnObject> list = new ArrayList<KrnObject>();
        
    	// Значения начального атрибута
		String topCT = getClassTableName(krnAttrs[0].classId);
		String topCM = getColumnName(krnAttrs[0], langId);
		
		// Значения конечного атрибута
		String bottomCT = getClassTableName(krnAttrs[krnAttrs.length - 1].classId);
		String bottomCM = getColumnName(krnAttrs[krnAttrs.length - 1], langId);
        
        // Условие на транзакцию
        StringBuffer sqlTID = new StringBuffer();
        if (tid == 0) {
			sqlTID.append(" AND " + topCT + ".c_tr_id = 0");
		} else if (tid != -1) {
			sqlTID.append(" AND " + topCT + ".c_tr_id IN (0," + tid + ")");
		}
        
        StringBuffer sqlDEL = new StringBuffer(" AND " + topCT + ".c_is_del = 0");
		
		StringBuffer sqlTERM = new StringBuffer();
        StringBuffer sqlInsertedTERM = new StringBuffer();

		for (int i = krnAttrs.length - 1; i >= 0; i--) {
			if (i == krnAttrs.length - 1) {
				if (medTbl) {
		        	sqlTERM.append(getSqlOp(getAttrTableName(attr) + "." + bottomCM, operation));
		        } else if (attr.rAttrId > 0) {
		        	// Крайний атрибут в цепочке - обратный
		        	sqlTERM.append(getSqlOp(getClassTableName(krnAttrs[i].typeClassId) + ".c_obj_id", operation));
		        } else {
		        	sqlTERM.append(getSqlOp(bottomCT + "." + bottomCM, operation));
		        }
			} else {
				sqlInsertedTERM = new StringBuffer();
    			if (krnAttrs[i].rAttrId > 0) {
        			sqlInsertedTERM.append(getClassTableName(krnAttrs[i].typeClassId) + ".c_obj_id" + " IN (SELECT " + getClassTableName(krnAttrs[i + 1].classId) + ".c_obj_id FROM " + getClassTableName(krnAttrs[i + 1].classId) + " WHERE " + sqlTERM + ")");
    			} else if (i < krnAttrs.length - 1 && krnAttrs[i + 1].rAttrId > 0) {
        			sqlInsertedTERM.append(getClassTableName(krnAttrs[i].classId) + "." + getColumnName(krnAttrs[i], langId) + " IN (SELECT " + getClassTableName(krnAttrs[i + 1].classId) + ".c_obj_id FROM " + getClassTableName(krnAttrs[i + 1].classId) + " LEFT JOIN " + getClassTableName(krnAttrs[i + 1].typeClassId) + " ON (" + getClassTableName(krnAttrs[i + 1].typeClassId) + ".cm" + krnAttrs[i + 1].rAttrId + "=" + getClassTableName(krnAttrs[i].classId) + ".c_obj_id) WHERE " + sqlTERM + ")");
    			} else {
        			sqlInsertedTERM.append(getClassTableName(krnAttrs[i].classId) + "." + getColumnName(krnAttrs[i], langId) + " IN (SELECT " + getClassTableName(krnAttrs[i + 1].classId) + ".c_obj_id FROM " + getClassTableName(krnAttrs[i + 1].classId) + " WHERE " + sqlTERM + ")");
    			}
    			sqlTERM = new StringBuffer();
    			sqlTERM.append(sqlInsertedTERM);
			}
		}
        
		StringBuffer sqlMAIN = new StringBuffer("SELECT " + topCT + ".c_obj_id, " + topCT + ".c_class_id, " + topCT + ".c_uid FROM " + topCT + (krnAttrs[0].rAttrId > 0 ? (" LEFT JOIN " + getClassTableName(krnAttrs[0].typeClassId) + " ON (" + getClassTableName(krnAttrs[0].typeClassId) + ".cm" + krnAttrs[0].rAttrId + "=" + getClassTableName(krnAttrs[0].classId) + ".c_obj_id)") : "") + " WHERE ");
		
		sqlMAIN.append(sqlTERM);
		sqlMAIN.append(sqlTID);
		sqlMAIN.append(sqlDEL);
		log.debug(sqlMAIN.toString());
		
		PreparedStatement pst = null;
		ResultSet res = null;
		try {
			pst = conn.prepareStatement(sqlMAIN.toString());
			if (value != null) {
                // Запись значения
                setValue(pst, 1, attr.typeClassId, value);
            }
			res = pst.executeQuery();
        	while (res.next()) {
        		list.add(new KrnObject(res.getLong("c_obj_id"), getSanitizedString(res, "c_uid"), res.getLong("c_class_id")));
            }
		} catch (SQLException e) {
			throw convertException(e);		
		} finally {
	      if (res != null)
	    	  DbUtils.closeQuietly(res);
	      if (pst != null)
	          DbUtils.closeQuietly(pst);
		}
    	return list;
    }
    
    @Override
    public List<KrnObject> getObjectsByAttribute( long classId, long attrId, long langId, int op, Object value, long tid) throws DriverException { 
		KrnAttribute attr = db.getAttributeById(attrId);
		KrnAttribute rattr = attr.rAttrId != 0 ? db.getAttributeById(attr.rAttrId) : null;
		boolean isArray = attr.collectionType == COLLECTION_ARRAY;
		boolean isSet = attr.collectionType == COLLECTION_SET;
		boolean medTbl = attr.rAttrId == 0 && (isSet || isArray);
		
		String cmName = getColumnName(attr, langId);
		StringBuilder fields = new StringBuilder(
				"pt.c_obj_id,pt.c_uid,pt.c_class_id,max(pt.c_is_del) AS c_is_del");
		StringBuilder group = new StringBuilder(
				"pt.c_obj_id,pt.c_uid,pt.c_class_id");
		StringBuilder from = new StringBuilder(getClassTableName(attr.classId)
				+ " pt, "+getDBPrefix()+"t_clinks cl");
		StringBuilder where = new StringBuilder(
				"pt.c_class_id=cl.c_child_id AND cl.c_parent_id=?");
		if (medTbl) {
			from.append(",");
			from.append(getAttrTableName(attr));
			from.append(" mt");
			where.append(" AND mt.c_obj_id=pt.c_obj_id AND mt.c_tr_id=pt.c_tr_id");
		} else if (attr.rAttrId != 0 && rattr.collectionType != COLLECTION_NONE) {
			String rcmName = getColumnName(rattr);
			from.append(",");
			from.append(getAttrTableName(rattr));
			from.append(" rat");
			where.append(" AND rat." + rcmName + "=pt.c_obj_id AND rat.c_tr_id=pt.c_tr_id");
		} else if (attr.rAttrId != 0 && rattr.collectionType == COLLECTION_NONE) {
			String rcmName = getColumnName(rattr);
			from.append(",");
			from.append(getClassTableName(rattr.classId));
			from.append(" rat");
			where.append(" AND rat." + rcmName + "=pt.c_obj_id AND rat.c_tr_id=pt.c_tr_id");
		}
		if (tid == 0) {
			where.append(" AND pt.c_tr_id=0");
		} else if (tid != -1) {
			where.append(" AND pt.c_tr_id IN (0,");
			where.append(tid);
			where.append(")");
		}
		if (medTbl) {
			if (isArray) {
				fields.append(",c_index");
				group.append(",c_index");
			}
			fields.append(",max(mt.c_del) AS c_del");
			where.append(" AND ");
			where.append(getSqlOp("mt." + cmName, op));
		} else if (attr.rAttrId != 0) {
			if (isArray) {
				fields.append(",c_index");
				group.append(",c_index");
			}
			if (rattr.collectionType != COLLECTION_NONE)
				fields.append(",max(rat.c_del) AS c_del");
			else
				fields.append(",0 AS c_del");
			
			where.append(" AND ");
			where.append(getSqlOp("rat.c_obj_id", op));
		} else {
			where.append(" AND ");
			where.append(getSqlOp("pt." + cmName, op));
		}
		StringBuffer sql = new StringBuffer(
				"SELECT DISTINCT " + fields + " FROM "
						+ from + " WHERE " + where + " GROUP BY " + group);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
            pst = conn.prepareStatement(sql.toString());
            pst.setLong(1, classId);
            if (value != null) {
            	setValue(pst, 2, attr.typeClassId, value);
            }
            Set<KrnObject> res = new HashSet<KrnObject>();
            rs = pst.executeQuery();
            while (rs.next()) {
                    boolean isDel = rs.getBoolean("c_is_del");
                    if (isDel)
                            continue;
                    if (isArray || isSet) {
                            isDel = rs.getLong("c_del") > 0;
                            if (isDel)
                                    continue;
                    }
                    long id = Funcs.normalizeInput(rs.getLong("c_obj_id"));
                    long cid = Funcs.normalizeInput(rs.getLong("c_class_id"));
                    String uid = getSanitizedString(rs, "c_uid");
                    res.add(new KrnObject(id, uid, cid));
            }
            return new ArrayList<KrnObject>(res);
        } catch (SQLException e) {
        	log.error(sql.toString());
        	log.error("classId = " + classId);
        	log.error("value = " + value);
        	throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
		}
	}

	public List<Long> getLangs(long objId, long attrId, long trId)
			throws DriverException {
		KrnAttribute attr = db.getAttributeById(attrId);
		if (!attr.isMultilingual) {
			return Collections.EMPTY_LIST;
		}
		try {
			List<Long> res = new ArrayList<Long>();
			List<KrnObject> langs = getSystemLangs();
			for (KrnObject lang : langs) {
				String cmName = getColumnName(attr, lang.id);
				String sql = null;
				if (attr.collectionType == COLLECTION_NONE) {
					String ctName = getClassTableName(attr.classId);
					sql = "SELECT COUNT(*) FROM " + ctName +
							" WHERE c_obj_id=? AND c_tr_id=? AND " + cmName + " IS NOT NULL";
				} else {
					String atName = getAttrTableName(attr);
					sql = "SELECT COUNT(*) FROM " + atName +
							" WHERE c_obj_id=? AND c_tr_id=? AND c_del=0 AND " + cmName + " IS NOT NULL";
				}
				PreparedStatement pst = conn.prepareStatement(sql);
				pst.setLong(1, objId);
				pst.setLong(2, trId);
				ResultSet set = pst.executeQuery();
				set.next();
				if (set.getInt(1) > 0)
					res.add(lang.id);
				set.close();
				pst.close();
			}
			return res;
		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	public int getMaxIndex(long objId, long attrId, long langId, long trId)
			throws DriverException {
		int res = -1;
		KrnAttribute attr = db.getAttributeById(attrId);
		boolean isArray = attr.collectionType == COLLECTION_ARRAY;
		try {
			String tabName = getAttrTableName(attr);
			StringBuilder select = new StringBuilder();
			if (isArray) {
				select.append("MAX(c_index)");
			} else {
				select.append("c_obj_id");
			}
			StringBuilder where = new StringBuilder("c_obj_id=?");
			if (trId == 0) {
				where.append(" AND c_tr_id=0");
			} else if (trId != -1) {
				where.append(" AND c_tr_id IN (0,");
				where.append(trId);
				where.append(")");
			}
			if (attr.isMultilingual) {
				where.append(" AND c_lang_id=");
				where.append(langId);
			}
			PreparedStatement pst = conn.prepareStatement("SELECT " + select
					+ " FROM " + tabName + " WHERE " + where);
			pst.setLong(1, objId);
			ResultSet set = pst.executeQuery();
			if (isArray) {
				set.next();
				set.getInt(1);
				if (!set.wasNull()) {
					res = set.getInt(1);
				}
			} else {
				res = set.next() ? 0 : -1;
			}
			set.close();
		} catch (SQLException e) {
			throw convertException(e);
		}
		return res;
	}

	public SortedSet<Value> getValues(long[] objIds, Set<Long> filteredIds, final long attrId,
            long langId, long tid) throws DriverException {
		return getValues(objIds, filteredIds, new int[1], attrId, langId, tid);
	}
	
	public SortedSet<Value> getValues(long[] objIds, Set<Long> filteredIds, int[] limit, final long attrId,
            long langId, long tid) throws DriverException {
      KrnAttribute attr = db.getAttributeById(attrId);
      KrnAttribute rattr = attr.rAttrId != 0 ? db.getAttributeById(attr.rAttrId) : null;
      KrnClass cls = db.getClassById(attr.classId);
      KrnClass type = db.getClassById(attr.typeClassId);
      boolean isPrimary = isPrimary(type);
      boolean isArray = attr.collectionType == COLLECTION_ARRAY;
      boolean isSet = attr.collectionType == COLLECTION_SET;
      boolean medTbl = attr.rAttrId == 0 && (isSet || isArray);
      final boolean valTbl = !isPrimary;
      try {
    	  int ltcnt = 0;
    	  int rtcnt = 0;
        final String cname = getColumnName(attr, langId);
        StringBuilder select = new StringBuilder();
        if (rattr != null) {
        	String tbAlias = (rattr.collectionType != COLLECTION_NONE) ? "rat" : (attr.typeClassId != rattr.classId) ? "rt" : "vt";
            String rcmName = getColumnName(rattr);
            select.append(tbAlias).append(".").append(rcmName).append(" AS id,vt.c_tr_id");
        } else {
        	select.append("pt.c_obj_id AS id,pt.c_tr_id");
        }
        if (medTbl && isArray) {
            select.append(",mt.c_index");
        } else {
            select.append(",0 as c_index");
        }
        if (medTbl) {
            select.append(",mt.c_del");
        } else if (rattr != null && rattr.collectionType != COLLECTION_NONE) {
            select.append(",rat.c_del");
        } else {
            select.append(",0 as c_del");
        }
        if (valTbl) {
            select.append(",vt.c_obj_id,vt.c_class_id,vt.c_uid,"+(attr.typeClassId==99?"0 as c_is_del":"vt.c_is_del"));
        } else if (medTbl) {
            select.append(",mt.");
            select.append(cname);
        } else {
            select.append(",pt.");
            select.append(cname);
        }
        StringBuilder from = new StringBuilder();
        StringBuilder where = new StringBuilder();
        StringBuilder order = new StringBuilder();

        if (rattr != null) {
        	String rtname = null;
        	String rtalias = null;
        	if (rattr.collectionType != COLLECTION_NONE) {
        		rtname = getAttrTableName(rattr);
        		rtalias = "rat";
        	} else {
        		rtname = getClassTableName(rattr.classId);
        		rtalias = (attr.typeClassId != rattr.classId) ? "rt" : "vt";
        	}
      	  	String rcmName = getColumnName(rattr);
      	  	where.append(rtalias).append(".").append(rcmName).append("=?");
            if (tid == 0) {
                where.append(" AND ").append(rtalias).append(".c_tr_id=0");
            } else if (tid != -1) {
            	  // атрибут может быть изменен в текущей транзакции,
            	  // а в 0-транзакции иметь значение = objId
                  //where.append(" AND " + tbAlias + ".c_tr_id IN (0,").append(tid).append(")");
              	where.append(" AND ").append(rtalias).append(".c_tr_id IN (0,?)");
              	rtcnt++;
              	if (rattr.collectionType != COLLECTION_NONE) {
                  	where.append(
                  			" AND NOT EXISTS (SELECT 1 FROM ").append(rtname).append(" tt WHERE tt.c_obj_id=")
                  			.append(rtalias).append(".c_obj_id AND tt.c_tr_id=? AND tt.").append(rcmName).append("=")
                  			.append(rtalias).append(".").append(rcmName).append(" AND tt.c_del>0");
                  	rtcnt++;
              	} else {
                	where.append(
                			" AND NOT EXISTS (SELECT 1 FROM ").append(rtname).append(" tt WHERE tt.c_obj_id=")
                			.append(rtalias).append(".c_obj_id AND tt.c_tr_id=? AND (tt.").append(rcmName).append(" is null or tt.")
                			.append(rcmName).append("<>").append(rtalias).append(".").append(rcmName).append(")");
                  	rtcnt++;
              	}
              	where.append(")");
            }
        } else { 
        	from.append(getClassTableName(cls.id)).append(" pt");
      	  	where.append("pt.c_is_del=0 AND pt.c_obj_id=?");
            if (tid == 0) {
            	where.append(" AND pt.c_tr_id=0");
            } else if (tid != -1) {
                where.append(" AND pt.c_tr_id IN (0,?)");
              	rtcnt++;
            }
        }
        if (medTbl) {
            from.append(" LEFT JOIN ");
            from.append(getAttrTableName(attr));
            from.append(" mt ON mt.c_obj_id=pt.c_obj_id AND mt.c_tr_id=pt.c_tr_id");
        }
        if (valTbl) {
            if (attr.rAttrId != 0) {
                if (rattr.collectionType != COLLECTION_NONE) {
              	  // множественный обратный атрибут
                    from.append(getAttrTableName(rattr))
                    	.append(" rat INNER JOIN ")
                        .append(getClassTableName(attr.typeClassId))
                        .append(" vt ON vt.c_obj_id=rat.c_obj_id AND vt.c_tr_id=rat.c_tr_id");
                } else {
                	  // одиночный обратный атрибут
                    if (rattr.classId != attr.typeClassId) {
                        from.append(getClassTableName(rattr.classId))
                        	.append(" rt INNER JOIN ")
                            .append(getClassTableName(attr.typeClassId))
                            .append(" vt ON vt.c_obj_id=rt.c_obj_id AND vt.c_tr_id=rt.c_tr_id");
                    } else {
                        from.append(getClassTableName(rattr.classId))
                        .append(" vt");
                    }
                }
                if (attr.sAttrId != 0) {
                    KrnAttribute sattr = db.getAttributeById(attr.sAttrId);
                    if (!attr.isMultilingual || langId > 0) {
	                    String scmName = null;
                    	scmName = getColumnName(sattr, langId);
	                    String orderPrefix = null;
	                    if (sattr.classId == attr.typeClassId) {
	                  	  orderPrefix = "vt.";
	                    } else if (sattr.classId == rattr.classId) {
	                  	  orderPrefix = "rt.";
	                    } else {
	                        from.append(" LEFT JOIN ")
	                            .append(getClassTableName(sattr.classId))
	                            .append(" st ON st.c_obj_id=vt.c_obj_id")
	                            .append(" AND st.c_tr_id=vt.c_tr_id");
	                        orderPrefix = "st.";
	                    }
	                    select.append(",").append(orderPrefix).append(scmName).append(" IS NULL AS scm_is_null,");
	                    select.append(orderPrefix).append(scmName);
	
	                    order.append("scm_is_null,").append(orderPrefix).append(scmName);
	                    if (attr.sDesc) {
	                        order.append(" DESC");
	                    }
                    }
                } else if (attr.collectionType == COLLECTION_SET) {
              	  	order.append("vt.c_obj_id");
                }
            } else {
                from.append(" LEFT JOIN ");
                from.append(getClassTableName(type.id));
                from.append(" vt ON ");
                if (medTbl) {
                    from.append("vt.c_obj_id=mt.");
                    from.append(getColumnName(attr));
                } else {
                    from.append("vt.c_obj_id=pt.");
                    from.append(getColumnName(attr));
                }
                if (tid == 0) {
                    from.append(attr.typeClassId==99?"":" AND vt.c_tr_id=0");
                } else if (tid != -1 && attr.typeClassId!=99) {
                    from.append(" AND vt.c_tr_id IN (0,?)");
                  	ltcnt++;
                }
            }
        }
        final SortedSet<Value> res = new TreeSet<Value>();
        List<Value> values = new ArrayList<Value>();
        StringBuilder sql = new StringBuilder("SELECT ").append(valTbl?"DISTINCT ":"").append(select)
        			.append(" FROM ").append(from)
        			.append(" WHERE ").append(where);
        if (order.length() > 0) {
            sql.append(" ORDER BY ").append(order);
        }
        if (limit[0] > 0)
        	sql = addLimit(sql, limit[0], 0);
        
        // Map для хранения обработанных строк
        Map<Long, Value> revValues = new HashMap<Long, Value>();
        
        ltcnt++;
        PreparedStatement pst = conn.prepareStatement(sql.toString());
        for (int i = 1; i < ltcnt; i++)
        	pst.setLong(i, tid);
        for (int i = 0; i < rtcnt; i++)
        	pst.setLong(ltcnt + 1 + i, tid);
        for (long objId : objIds) {
			if (filteredIds != null && !filteredIds.contains(objId)) continue;
            pst.setLong(ltcnt, objId);
            ResultSet rs = pst.executeQuery();
            values.clear();
            revValues.clear();
            boolean inPtr = false;
            while (rs.next()) {
                long objectId = rs.getLong("id");
                int index = rs.getInt("c_index");
                long trId = rs.getLong("c_tr_id");
                long del = rs.getLong("c_del");
                if (rs.wasNull()) {
                	continue;
                }
                boolean isDel = false;
                
                Object value = null;
                // objectId != objId если значение обратного атрибута
                // было изменено в текущей транзакции
                if (valTbl) {
              	  	isDel = rs.getBoolean("c_is_del");
                    long vobjId = rs.getLong("c_obj_id");
                    if (!rs.wasNull()) {
                        value = new KrnObject(
                                vobjId,
                                getSanitizedString(rs, "c_uid"),
                                rs.getLong("c_class_id"));
                    }
                } else {
                    value = getValue(rs, cname, attr.typeClassId);
                    if (attr.typeClassId == PC_BLOB && db.inJcrRepository(attr.id)) {
                    	value = db.getRepositoryData(attr.id, (byte[])value);
                    } else if (attr.typeClassId == PC_MEMO && db.inJcrRepository(attr.id)) {
                    	value = db.getRepositoryData(attr.id, (String)value);
                    }
                }

                /* Значение считается NULL если:
                 * 1) обр атрибуту в текущей транзакции присвоен др объект
                 * 2) значение множ обр атибута помечено как удаленное
                 * 3) значение-объект удален
                 * 4) значение удалено
                 */
                boolean valueIsNull = objectId != objId || del != 0 || isDel || value == null;

                if (rattr != null) {
              	  // исключаем удаления других объектов в текущей транзакции
              	  if (!(objectId != objId && (del != 0 || isDel))) {
	                	  KrnObject vobj = KrnObject.class.cast(value);
	                	  Value v = revValues.get(vobj.id);
	                	  if (v != null) {
	                		  if (trId > 0 && ((objId == objectId && (v.objectId != objId || v.value == null)) || v.trId == 0)) {
	            				  values.remove(v);
	                			  if (!valueIsNull) {
	                				  v = new Value(objectId, index, trId, value);
	                				  values.add(v);
	                			  } else {
	                				  v = new Value(objectId, index, trId, null);
	                			  }
	            				  revValues.put(vobj.id, v);
	                		  }
	                	  } else {
	            			  if (!valueIsNull) {
	            				  v = new Value(objectId, index, trId, value);
	            				  values.add(v);
	            			  } else {
	            				  v = new Value(objectId, index, trId, null);
	            			  }
	        				  revValues.put(vobj.id, v);
	                	  }
              	  }
                } else {
                    if (!inPtr && trId > 0) {
                        values.clear();
                        inPtr = true;
                    }
                    if (!inPtr || trId > 0) {
                        if (!valueIsNull) {
                            values.add(new Value(objectId, index, trId, value));
                        }
                    }
                }
            }
            rs.close();
            // Если это набор, то расставлем индексы согласно позиции в списке
            if (isSet) {
          	  for (int i = 0; i < values.size(); i++) {
          		  values.get(i).index = i;
          	  }
            }
            res.addAll(values);
        }
        pst.close();
        return res;
      } catch (SQLException e) {
        throw convertException(e);
      }
  }
	
	public void setValueImpl(KrnObject obj, KrnAttribute attr, int index,
			long langId, long trId, Object value, boolean insert)
			throws DriverException {
		setValueImpl(obj, attr, index, langId, trId, value, null, insert, true);
	}
	
	@Override
	public void setValueImpl(List<Long> objectsIds, KrnAttribute attr, long langId, long trId, Object value) throws DriverException {
		PreparedStatement pst = null;
		try {
			String cname = getColumnName(attr, langId);
			String idsString = "";
			for (int i = 0; i < objectsIds.size(); i++) {
				idsString += objectsIds.get(i);
				if (i < objectsIds.size() - 1) {
					idsString += ",";
				}
			}
			
			if (db.inJcrRepository(attr.id)) {
	    		StringBuilder sql2 = new StringBuilder("SELECT ").append(cname).append(" FROM ").append(getClassTableName(attr.classId))
						.append(" WHERE c_obj_id IN (").append(idsString).append(") AND c_tr_id=?");
	
				PreparedStatement setSelPst = conn.prepareStatement(sql2.toString());
				setSelPst.setLong(1, trId);
				ResultSet rs = setSelPst.executeQuery();
				while (rs.next()) {
					Object nodeId = getValue(rs, cname, attr.typeClassId);
					if (nodeId != null) {
						addRewrittenJrbNode(attr.id, nodeId);
					}
				}
				rs.close();
				setSelPst.close();
			}
			
			pst = conn.prepareStatement("UPDATE " + getClassTableName(attr.classId) + " SET " + cname + "=? WHERE c_obj_id IN (" + idsString + ") AND c_tr_id=?");
			if (attr.typeClassId == PC_BLOB && db.inJcrRepository(attr.id)) {
				value = db.putRepositoryData(attr.id, objectsIds.get(0), trId, (byte[])value);
        		addNotCommitedJrbNode(attr.id, value);
            } else if (attr.typeClassId == PC_MEMO && db.inJcrRepository(attr.id)) {
				value = db.putRepositoryData(attr.id, objectsIds.get(0), trId, (String)value);
        		addNotCommitedJrbNode(attr.id, value);
			}
			setValue(pst, 1, attr.typeClassId, value);
			pst.setLong(2, trId);
			pst.executeUpdate();
			pst.close();
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		} catch (DriverException e) {
			log.error(e, e);
			throw e;
		} catch (Exception e) {
			log.error(e, e);
			throw new DriverException(e);
		} finally {
			DbUtils.closeQuietly(pst);
		}
	}

	@Override
    public void setValueImpl(KrnObject obj, KrnAttribute attr, int index,
			long langId, long trId, Object value, Object oldValue, boolean insert, boolean replLog)
			throws DriverException {
    	
		if (trId == -1)
			throw new DriverException("Нельзя использовать значение транзакции -1 при сохранении атрибута!", ErrorCodes.DRV_ILLEGAL_ARGUMENT_EXCEPTION);

		if (throwOnSaveNull) {
	    	if (attr.typeClassId > 10 && value == null) {
				throw new DriverException("Установка пустого значения! Id объекта: " + obj.id + ", атрибут: " + attr.name + ".");
			}
    	}
    	
		if (attr.rAttrId != 0) {
			KrnAttribute rattr = db.getAttributeById(attr.rAttrId);
			KrnObject vobj = value instanceof Number ? getObjectById(((Number)value).longValue()) : (KrnObject)value;
			setValueImpl(vobj, rattr, 0, 0, trId, obj.id, true);
			return;
		}

		PreparedStatement pst = null;
		StringBuilder sql = null;
		try {
			logVcsDataChanges(obj, attr.id, langId, value, trId);

			trId = openTransaction(obj, trId, false);
			
			if (attr.typeClassId == PC_BLOB && db.inJcrRepository(attr.id)) {
				value = db.putRepositoryData(attr.id, obj.id, trId, (byte[])value);
        		addNotCommitedJrbNode(attr.id, value);
            } else if (attr.typeClassId == PC_MEMO && db.inJcrRepository(attr.id)) {
				value = db.putRepositoryData(attr.id, obj.id, trId, (String)value);
        		addNotCommitedJrbNode(attr.id, value);
			}
			
			// Обработка внешнего BLOB. TODO Пока сохраняем и в таблице до полного тестирования механизма.
			if (!isUpgrading)
				setExternalBlob(obj, attr, index, langId, trId, value);
			
			String mtName = getAttrTableName(attr);
			String cname = getColumnName(attr, langId);
			boolean isArray = attr.collectionType == COLLECTION_ARRAY;
			boolean isSet = attr.collectionType == COLLECTION_SET;
			if (insert && isArray) {
				shiftIndexes(obj.id, trId, attr, langId, index, true);
			}
			if (isSet || isArray) {
				
				if (isArray && db.inJcrRepository(attr.id)) {
					sql = new StringBuilder("SELECT ").append(cname).append(" FROM ").append(mtName)
							.append(" WHERE c_obj_id=? AND c_tr_id=? AND c_del=0 AND c_index=").append(index);

					PreparedStatement setSelPst = conn.prepareStatement(sql.toString());
					setSelPst.setLong(1, obj.id);
					setSelPst.setLong(2, trId);
					ResultSet rs = setSelPst.executeQuery();
					while (rs.next()) {
						Object nodeId = getValue(rs, cname, attr.typeClassId);
						if (nodeId != null) {
							addRewrittenJrbNode(attr.id, nodeId);
						}
					}
					rs.close();
					setSelPst.close();
				}

				sql = new StringBuilder("UPDATE ")
						.append(mtName).append(" SET ").append(cname)
						.append("=?");
				// Для набора восстанавливаем запись если она была помечена
				// как удаленная
				if (isSet)
					sql.append(",c_del=0");
				
				sql.append(" WHERE c_obj_id=? AND c_tr_id=?");
				
				if (isArray) {
					sql.append(" AND c_index=").append(index);
				}
				if (isSet) {
					sql.append(" AND ").append(cname).append("=?");
				}
				// Для набора пытаемся обновить существующую запись в mt-таблице
				if (isArray)
					sql.append(" AND c_del=0");
				
				pst = conn.prepareStatement(sql.toString());
				setValue(pst, 1, attr.typeClassId, value);
				pst.setLong(2, obj.id);
				pst.setLong(3, trId);
				if (isSet) {
					if (oldValue == null) oldValue = value;
					setValue(pst, 4, attr.typeClassId, oldValue);
				}
				int count = pst.executeUpdate();
				pst.close();
				if (count == 0) {
					// Если записи не существует, то создаем новую
					sql = new StringBuilder("INSERT INTO ")
							.append(mtName).append(" (c_obj_id,c_tr_id,")
							.append(cname);
					if (isArray) {
						sql.append(",c_index");
						sql.append(",c_id");
					}
					sql.append(") VALUES (?,?,?");
					if (isArray) {
						sql.append(",").append(index);
						sql.append(",").append(System.nanoTime());
					}
					sql.append(")");

					pst = conn.prepareStatement(sql.toString());
					pst.setLong(1, obj.id);
					pst.setLong(2, trId);
					setValue(pst, 3, attr.typeClassId, value);
					pst.executeUpdate();
					pst.close();
				}
			} else {
				
				if (db.inJcrRepository(attr.id)) {
					sql = new StringBuilder("SELECT ").append(cname).append(" FROM ").append(getClassTableName(attr.classId))
							.append(" WHERE c_obj_id=? AND c_tr_id=?");

					PreparedStatement setSelPst = conn.prepareStatement(sql.toString());
					setSelPst.setLong(1, obj.id);
					setSelPst.setLong(2, trId);
					ResultSet rs = setSelPst.executeQuery();
					while (rs.next()) {
						Object nodeId = getValue(rs, cname, attr.typeClassId);
						if (nodeId != null) {
							addRewrittenJrbNode(attr.id, nodeId);
						}
					}
					rs.close();
					setSelPst.close();
				}
				
				sql = new StringBuilder("UPDATE ").append(getClassTableName(attr.classId))
						.append(" SET ").append(cname).append("=? WHERE c_obj_id=? AND c_tr_id=?");

				pst = conn.prepareStatement(sql.toString());
				setValue(pst, 1, attr.typeClassId, value);
				pst.setLong(2, obj.id);
				pst.setLong(3, trId);
				pst.executeUpdate();
				pst.close();
			}
			if(replLog)
				logDataChanges(obj, attr.id, langId, trId);
		} catch (SQLException e) {
			log.error(sql);
			log.error(e, e);
			throw convertException(e);
		} catch (DriverException e) {
			log.error(sql);
			log.error(e, e);
			throw e;
		} catch (Exception e) {
			log.error(sql);
			log.error(e, e);
			throw new DriverException(e);
		} finally {
			DbUtils.closeQuietly(pst);
		}
	}
    
    public boolean setExternalBlob(KrnObject obj, KrnAttribute attr, int index, long langId, long trId, Object value)
    		throws SQLException, IOException, DriverException {
    	
    	if (version >= 23 && "toDatabaseAndDisk".equals(db.getFileStoreType())) {
	    	ResultSet rs = null;
	    	
	    	try {
	    		if (pstExtBlobParamsById == null) {
	    	    	KrnClass msdocCls = db.getClassByName("MSDoc");
	    	    	KrnAttribute dirAttr = db.getAttributeByName(msdocCls.id, "ext_dir");
	    	    	KrnAttribute fileAttr = db.getAttributeByName(msdocCls.id, "ext_filename");
	    			pstExtBlobParamsById = conn.prepareStatement(
	    					"SELECT " + getColumnName(dirAttr) + "," + getColumnName(fileAttr) +
	    					" FROM " + getClassTableName(msdocCls, true) +
	    					" WHERE c_obj_id=? AND c_tr_id =?");
			    	pstUpdateExtBlobParams = conn.prepareStatement(
			    			"UPDATE " + getClassTableName(msdocCls, true) +
			    			" SET " + getColumnName(fileAttr) + "=?" +
			    			" WHERE c_obj_id=? AND c_tr_id=?");
	    		}
	    		pstExtBlobParamsById.setLong(1, obj.id);
	    		pstExtBlobParamsById.setLong(2, trId);
		    	
		    	rs = pstExtBlobParamsById.executeQuery();
		    	if (rs.next()) {
		    		String dirName = Funcs.normalizeInput(db.getBlobDir(rs.getLong(1)));
		    		if (Funcs.isValid(dirName)) {
		    			File dir = Funcs.getCanonicalFile(dirName);
		    			
		    	    	if (index != 0)
		    	    		throw new DriverException("Не реализовано для множественных атрибутов!");
			    		String fileName = getString(rs, 2);
		    			boolean newFile = false;
		    			if (fileName == null || fileName.length() == 0) {
		    				Calendar c = GregorianCalendar.getInstance();
		    				fileName = c.get(Calendar.YEAR) +
		    						"/" + c.get(Calendar.MONTH) +
		    						"/" + c.get(Calendar.DAY_OF_MONTH) +
		    						"/" + obj.id;
		    				pstUpdateExtBlobParams.setString(1, fileName);
		    				pstUpdateExtBlobParams.setLong(2, obj.id);
		    				pstUpdateExtBlobParams.setLong(3, trId);
		    				pstUpdateExtBlobParams.executeUpdate();
		    				// Регистрируем изменения атрибута ext_filename
		    				logDataChanges(obj, db.getAttributeByUid("0af30c6e-fa16-4a1b-bf62-5887a1998108").id, 0, trId);
		    		    	newFile = true;
		    			}
		    			fileName += "_" + index + "_" + langId + "_" + trId;
		    			if (Funcs.isValid(fileName)) {
		    				String[] dirsNames = fileName.split("/");
		    				if (dirsNames.length > 1) {
		    					for (int k=0; k<dirsNames.length - 1; k++) {
					    			dir = Funcs.getCanonicalFile(dir, dirsNames[k]);
		    					}
		    					dir.mkdirs();
		    					fileName = dirsNames[dirsNames.length - 1];
		    				}
		    				
			    			File file = Funcs.getCanonicalFile(dir, fileName);
			    			
							if (value instanceof byte[]) {
								try {
									Funcs.write((byte[])value, file);
								} catch (IOException e) {
									if ("На устройстве кончилось место".equals(e.getMessage())) {
										throw new IOException("На устройстве \"" + dir.getAbsolutePath() + "\" кончилось место", e);
									}
									throw e;
								}
							} else if (!newFile && file.exists()) {
								file.delete();
							}
		    			}
						return true;
		    		}
		    	}
	    	} finally {
	    		DbUtils.closeQuietly(rs);
	    	}
    	}
    	return false;
    }

    public void setValueImpl(KrnObject obj, long trId, Map<Pair<KrnAttribute, Long>, Object> initValues) throws DriverException {
		try {
			List<KrnClass> clss = db.getSuperClasses(obj.classId);
			for (Object cls1 : clss) {
				KrnClass cls = (KrnClass) cls1;
				if (cls.id != ROOT_CLASS_ID && !cls.isVirtual()) {
					String tname = getClassTableName(cls.id);
					String cols = "";
					String colsBlob="";//для того чтобы все блобы сэтить в конце списка
					List<Pair<KrnAttribute, Long>> keysBlob=new ArrayList<>();
					if (initValues != null) {
						// Проход 1. Формирование SQL
						for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
							if (key.first.classId == cls.id) {
			            		String cname = getColumnName(key.first, key.second);
			            		
								if (db.inJcrRepository(key.first.id)) {
				            		StringBuilder sql2 = new StringBuilder("SELECT ").append(cname).append(" FROM ").append(tname)
											.append(" WHERE c_obj_id=? AND c_tr_id=?");
	
									PreparedStatement setSelPst = conn.prepareStatement(sql2.toString());
									setSelPst.setLong(1, obj.id);
									setSelPst.setLong(2, trId);
									ResultSet rs = setSelPst.executeQuery();
									while (rs.next()) {
										Object nodeId = getValue(rs, cname, key.first.typeClassId);
										if (nodeId != null) {
											addRewrittenJrbNode(key.first.id, nodeId);
										}
									}
									rs.close();
									setSelPst.close();
								}
								
								if(key.first.typeClassId!=PC_BLOB && key.first.typeClassId!=PC_MBLOB)
									cols += cname + "=?,";
								else
									colsBlob += cname + "=?,";
							}
						}
						cols+=colsBlob;
					}
					if (cols.length() == 0)
						continue;
					
					String sql = "UPDATE " + tname + " SET " + cols.substring(0, cols.length() - 1) + " WHERE c_obj_id=? AND c_tr_id=?";
					PreparedStatement pst = conn.prepareStatement(sql);
					int i = 1;
					if (initValues != null) {
						// Проход 2. Установка значений
						for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
							if (key.first.classId == cls.id) {
								if(key.first.typeClassId!=PC_BLOB && key.first.typeClassId!=PC_MBLOB) {
									logVcsDataChanges(obj, key.first.id, key.second, initValues.get(key),trId);
									Object value = initValues.get(key);
					            	if (key.first.typeClassId == PC_MEMO && db.inJcrRepository(key.first.id)) {
					            		value = db.putRepositoryData(key.first.id, obj.id, trId, (String)value);
					            		addNotCommitedJrbNode(key.first.id, value);
					            	}
									setValue(pst, i++, key.first.typeClassId, value);
									logDataChanges(obj, key.first.id, key.second, trId);
								}else{
									keysBlob.add(key);//для того чтобы все блобы сэтить в конце списка
								}
							}
						}
						for (Pair<KrnAttribute, Long> key : keysBlob) {
							logVcsDataChanges(obj, key.first.id, key.second, initValues.get(key),trId);
							Object value = initValues.get(key);
							if (db.inJcrRepository(key.first.id)) {
								value = db.putRepositoryData(key.first.id, obj.id, trId, (byte[])value);
			            		addNotCommitedJrbNode(key.first.id, value);
							}
							setValue(pst, i++, key.first.typeClassId, value);
							logDataChanges(obj, key.first.id, key.second, trId);
						}
					}
					pst.setLong(i++, obj.id);
					pst.setLong(i++, trId);
					pst.executeUpdate();
					pst.close();
				}
			}
		} catch (SQLException e) {
			throw convertException(e);
		}
	}
	
	protected void shiftIndexes(long objId, long trId, KrnAttribute attr,
			long langId, int index, boolean increment) throws SQLException {
		String mtName = getAttrTableName(attr);
		QueryRunner qr = new QueryRunner(true);
		ResultSetHandler rh = new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				List<Integer> res = new ArrayList<Integer>();
				while (rs.next()) {
					res.add(rs.getInt(1));
				}
				rs.close();
				return res;
			}
		};
		List<Integer> inds = (List<Integer>) qr
				.query(conn,
						"SELECT c_index FROM "
								+ mtName
								+ " WHERE c_obj_id=? AND c_tr_id=? AND c_index>=? AND c_del=0 ORDER BY c_index"
								+ (increment ? " DESC" : ""), rh, objId, trId, index);
		if (inds.size() > 0) {
			PreparedStatement pst = null;
			try {
				pst = conn.prepareStatement("UPDATE "
							+ mtName
							+ " SET c_index=c_index"
							+ (increment ? "+1" : "-1")
							+ " WHERE c_obj_id=? AND c_tr_id=? AND c_index=? AND c_del=0");
				pst.setLong(1, objId);
				pst.setLong(2, trId);
				for (int i : inds) {
					pst.setInt(3, i);
					pst.executeUpdate();
				}
			} finally {
				DbUtils.closeQuietly(pst);
			}
		}
	}

	@Override
	public void deleteValueImpl(KrnObject obj, KrnAttribute attr,
			int[] indices, long langId, long trId, boolean deleteRefs) throws DriverException {
		
		if (attr.collectionType == COLLECTION_SET) {
			throw new DriverException("Удаление по индексу не доступно для типа коллекций 'набор'.");
		}
		if (trId == -1)
			throw new DriverException("Нельзя использовать значение транзакции -1 при удалении объекта!", ErrorCodes.DRV_ILLEGAL_ARGUMENT_EXCEPTION);

		if (attr.rAttrId != 0) {
			KrnAttribute rattr = db.getAttributeById(attr.rAttrId);
			SortedSet<Value> values = getValues(new long[] {obj.id}, null, attr.id, 0, trId);
			for (Value value : values) {
				for (int index : indices) {
					if (value.index == index) {
						if (attr.isAggregate())
							deleteObject((KrnObject)value.value, trId, deleteRefs);
						else
							deleteValue((KrnObject)value.value, rattr, obj, trId, deleteRefs);
						break;
					}
				}
			}
			return;
		}

		StringBuilder sql = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			logVcsDataChanges(obj, attr.id, langId, null,trId);
			
			trId = openTransaction(obj, trId, false);
			String mtName = getAttrTableName(attr);
			boolean isArray = attr.collectionType == COLLECTION_ARRAY;
			if (isArray) {
				int indPar = 3;
				if (trId == 0) {
					sql = new StringBuilder("DELETE FROM ").append(mtName)
							.append(" WHERE c_obj_id=? AND c_tr_id=? AND c_index=?");
					pst = conn.prepareStatement(sql.toString());
					pst.setLong(1, obj.id);
					pst.setLong(2, trId);
				} else {
					sql = new StringBuilder("UPDATE ").append(mtName)
							.append(" SET c_del=? WHERE c_obj_id=? AND c_tr_id=? AND c_index=? AND c_del=0");
					pst = conn.prepareStatement(sql.toString());

					long t = System.nanoTime();
					log.debug("# " + t);
					pst.setLong(1, t);
					pst.setLong(2, obj.id);
					pst.setLong(3, trId);
					indPar = 4;
				}
				for (int i = indices.length - 1; i >= 0; i--) {
					int indice = indices[i];
					pst.setInt(indPar, indice);
					pst.executeUpdate();
					shiftIndexes(obj.id, trId, attr, langId, indice, false);
				}
				pst.close();
			} else {
				KrnClass cls = db.getClassById(attr.classId);
				
				sql = new StringBuilder("SELECT COUNT(c_obj_id) FROM ").append(getClassTableName(cls.id))
						.append(" WHERE c_obj_id=? AND c_tr_id=?");
				pst = conn.prepareStatement(sql.toString());
				pst.setLong(1, obj.id);
				pst.setLong(2, trId);
				rs = pst.executeQuery();
				if (rs.next() && rs.getInt(1) > 0) {
					rs.close();
					pst.close();
					
					if (attr.isMultilingual) {
						// Если атрибут мультиязычный
						List<Long> langIds = null;
						if (langId > 0) {
							langIds = Collections.singletonList(langId);
						} else {
							List<KrnObject> sysLangs = getSystemLangs();
							langIds = new ArrayList<Long>(sysLangs.size());
						}
						for (Long lid : langIds) {
							String cname = getColumnName(attr, lid);
							sql = new StringBuilder("UPDATE ").append(getClassTableName(cls.id))
									.append(" SET ").append(cname).append("=NULL WHERE c_obj_id=? AND c_tr_id=?");
							
							pst = conn.prepareStatement(sql.toString());
							pst.setLong(1, obj.id);
							pst.setLong(2, trId);
							int c = pst.executeUpdate();
							pst.close();
							
							//if (c < 1)
							//	throw new DriverException("Изменение запрещено правилами FGAC", ErrorCodes.ERROR_FGAC_NOT_ALLOW);
						}
					} else {
						String cname = getColumnName(attr);
						
						sql = new StringBuilder("UPDATE ").append(getClassTableName(cls.id))
								.append(" SET ").append(cname).append("=NULL WHERE c_obj_id=? AND c_tr_id=?");
						
						pst = conn.prepareStatement(sql.toString());
						pst.setLong(1, obj.id);
						pst.setLong(2, trId);
						int c = pst.executeUpdate();
						pst.close();
						
						//if (c < 1)
						//	throw new DriverException("Изменение запрещено правилами FGAC", ErrorCodes.ERROR_FGAC_NOT_ALLOW);
					}
				}
			}
			logDataChanges(obj, attr.id, langId, trId);
		} catch (SQLException e) {
			log.error(sql);
			log.error(e, e);
			throw convertException(e);
		} catch (DriverException e) {
			log.error(sql);
			log.error(e, e);
			throw e;
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
		}
	}

	@Override
	public void deleteValueImpl(KrnObject obj, KrnAttribute attr,
			Collection<Object> values, long trId, boolean deleteRefs) throws DriverException {
		
		if (attr.collectionType != COLLECTION_SET) {
			throw new DriverException("Удаление по значению доступно только "
					+ "для типа коллекций 'набор'.");
		}

		if (trId == -1)
			throw new DriverException("Нельзя использовать значение транзакции -1 при удалении объекта!", ErrorCodes.DRV_ILLEGAL_ARGUMENT_EXCEPTION);

		if (attr.rAttrId != 0) {
			KrnAttribute rattr = db.getAttributeById(attr.rAttrId);
			for (Object value : values) {
				KrnObject vobj = (KrnObject)value;
				if (attr.isAggregate())
					deleteObject(vobj, trId, deleteRefs);
				else
					deleteValue(vobj, rattr, obj, trId, deleteRefs);
			}
			return;
		}

		try {
			logVcsDataChanges(obj, attr.id, 0, null,trId);
			trId = openTransaction(obj, trId, false);
			if (attr.typeClassId >= 99) {
				Collection<Object> oldValues = values;
				values = new ArrayList<Object>(oldValues.size());
				for (Object value : oldValues) {
					values.add(((KrnObject) value).id);
				}
			}
			String mtName = getAttrTableName(attr);
			String cname = getColumnName(attr);
			if (trId == 0) {
				// Удаляем записи из AT таблицы
				PreparedStatement pst = conn.prepareStatement("DELETE FROM "
						+ mtName + " WHERE c_obj_id=?" + " AND c_tr_id=? AND "
						+ cname + "=?");
				pst.setLong(1, obj.id);
				pst.setLong(2, trId);
				for (Object value : values) {
					setValue(pst, 3, attr.typeClassId, value);
					pst.executeUpdate();
				}
				pst.close();
			} else {
				// Помечае записи AT таблицы как удаленные
				PreparedStatement pst = conn.prepareStatement("UPDATE "
						+ mtName + " SET c_del=? WHERE c_obj_id=?"
						+ " AND c_tr_id=? AND " + cname + "=? AND c_del=0");
				pst.setLong(1, System.nanoTime());
				pst.setLong(2, obj.id);
				pst.setLong(3, trId);
				for (Object value : values) {
					setValue(pst, 4, attr.typeClassId, value);
					pst.executeUpdate();
				}
				pst.close();
			}
			logDataChanges(obj, attr.id, 0, trId);
		} catch (SQLException e) {
			throw convertException(e);
		}
	}
	
	protected void deleteValue(
			KrnObject obj,
			KrnAttribute attr,
			KrnObject vobj,
			long trId,
			boolean deleteRefs
			) throws DriverException {
		
			if (attr.collectionType == COLLECTION_NONE) {
				deleteValueImpl(obj, attr, new int[] {0}, 0, trId, deleteRefs);
			} else if (attr.collectionType == COLLECTION_ARRAY) {
				SortedSet<Value> values = getValues(new long[] {obj.id}, null, attr.id, 0, trId);
				for (Value value : values) {
					if (value.value instanceof KrnObject && ((KrnObject) value.value).id == vobj.id) {
						deleteValueImpl(obj, attr, new int[] {value.index}, 0, trId, deleteRefs);
						break;
					}
				}
			} else {
				deleteValueImpl(obj, attr, Collections.singleton((Object)vobj), trId, deleteRefs);
			}
	}

	public long getModelChanges(final long fromId, ModelChangeProcessor p)
			throws DriverException {
		return getModelChanges(fromId, -1, p);
	}

	public long getModelChanges(final long fromId, final long toId, final ModelChangeProcessor p) throws DriverException {
		try {
			final Set<String> deletedEntities = new TreeSet<String>();
			class ChangesCls {
				long id;
				int action;
				String entityId;

				public ChangesCls(long id, int action, String entityId) {
					this.id = id;
					this.action = action;
					this.entityId = entityId;
				}
			}
			ResultSetHandler rh = new ResultSetHandler() {
				public Object handle(ResultSet rs) throws SQLException {
					List<ChangesCls> res = new ArrayList<ChangesCls>();
					while (rs.next()) {
						long id = rs.getLong("id");
						int action = rs.getInt("c_action");
						String entityId = getSanitizedString(rs, "c_entity_id").trim();
						res.add(new ChangesCls(id, action, entityId));
					}
					return res;
				}
			};

            // Вытаскиваем изменения классов (в первом цикле собираем все события по удалению сущности, во втором все остальные)
            long changeId = fromId;
			List<ChangesCls> changes = queryChanges(ENTITY_TYPE_CLASS, fromId, toId, rh, deletedEntities);
            for (ChangesCls ch : changes) {
                long id = ch.id;
                int action = ch.action;
                String entityId = ch.entityId;
                if (action == ACTION_DELETE) {
                    p.process(new ClassChange(id, action, entityId, "", null, false, "", 0));
                }
                if (id > changeId) {
                    changeId = id;
                }
            }
            for (ChangesCls ch : changes) {
                long id = ch.id;
                int action = ch.action;
                String entityId = ch.entityId;
                if (action != ACTION_DELETE) {
                    if (deletedEntities.contains(entityId)) {
                        continue;	// Игнорируются все события удаленных сущностей
                    }
                    KrnClass cls = null;
                    cls = db.getClassByUid(entityId);
                    if (cls == null && toId == -1)	// Для повторного экпорта можно отсутсвие сущности игнорировать
                        throw new SQLException("Класс с UID='" + entityId + "' не найден.");

                    String cname = cls.name;
                    KrnClass parentCls = db.getClassById(cls.parentId);
                    boolean isRepl = cls.isRepl;
                    String comment = getClassComment(cls.id);
                    String tname = (action == ACTION_CREATE) ? cls.tname : "";

                    p.process(new ClassChange(id, action, entityId, cname, parentCls, isRepl, comment, tname, cls.modifier)); 
                }
                if (id > changeId) {
                    changeId = id;
                }
            }
            
            // Вытаскиваем изменения атрибутов (в первом цикле собираем все события по удалению сущности, во втором все остальные)
            changes = queryChanges(ENTITY_TYPE_ATTRIBUTE, fromId, toId, rh, deletedEntities);
            for (ChangesCls ch : changes) {
                long id = ch.id;
                int action = ch.action;
                String entityId = ch.entityId;
                if (action == ACTION_DELETE) {
                    p.process(new AttributeChange(id, action, entityId, "", EMPTY_CLASS, EMPTY_CLASS, 0, false, false, false, false, 0, 0, null, null, false, "", "", 0));
                }
                if (id > changeId) {
                    changeId = id;
                }
            }
            // Формируем мапу всех событий создания новых атрибутов, где ключем будет id атрибута
            TreeMap<String,ChangesCls> eventsOfAttrCreate = new TreeMap<String,ChangesCls>();
            for (ChangesCls ch : changes) {
                int action = ch.action;
                String entityId = ch.entityId;
                if (action == ACTION_CREATE) {
                	eventsOfAttrCreate.put(entityId, ch);
                }
            }
            // Распределим события по последовательности. Все новые атрибуты, на которые есть ссылки, должны быть созданы раньше всех.
            ArrayList<ChangesCls> firstCreateEvents = new ArrayList<ChangesCls>();
            ArrayList<ChangesCls> restCreateEvents = new ArrayList<ChangesCls>();
            ArrayList<ChangesCls> othersEvents = new ArrayList<ChangesCls>();
            ArrayList<ChangesCls> ctrl = new ArrayList<ChangesCls>();
            for (ChangesCls ch : changes) {
                int action = ch.action;
                String entityId = ch.entityId;
                if (action == ACTION_CREATE) {
                	KrnAttribute attr = db.getAttributeByUid(entityId);
                	if (attr != null) {
	                	if (attr.sAttrId > 0) {
	                    	KrnAttribute sAttr = db.getAttributeById(attr.sAttrId);
		                    ChangesCls ch_ = eventsOfAttrCreate.get(sAttr.uid);
		                    if (ch_ != null) {
		                    	if (!ctrl.contains(ch_)) {
		                    		firstCreateEvents.add(ch_);
		                    		ctrl.add(ch_);
		                    	}
		                    }
	                	}
	                	if (attr.rAttrId > 0) {
	                    	KrnAttribute rAttr = db.getAttributeById(attr.rAttrId);
		                    ChangesCls ch_ = eventsOfAttrCreate.get(rAttr.uid);
		                    if (ch_ != null) {
		                    	if (!ctrl.contains(ch_)) {
		                    		firstCreateEvents.add(ch_);
		                    		ctrl.add(ch_);
		                    	}
		                    }
	                	}
	                	if (!ctrl.contains(ch)) {
	               			restCreateEvents.add(ch);
	               			ctrl.add(ch);
	                	}
                	}
                } else {
                	if (!ctrl.contains(ch)) {
	                	othersEvents.add(ch);
	                	ctrl.add(ch);
                	}
                }
            }
            
            // Соединяем все кусочки в единый массив
            changes.clear();
            changes.addAll(firstCreateEvents);
            changes.addAll(restCreateEvents);
            changes.addAll(othersEvents);
            
            for (ChangesCls ch : changes) {
                long id = ch.id;
                int action = ch.action;
                String entityId = ch.entityId;
                if (action != ACTION_DELETE) {
                    if (deletedEntities.contains(entityId)) {
                        continue;	// Игнорируются все события удаленных сущностей
                    }
                    KrnAttribute attr = null;
                    attr = db.getAttributeByUid(entityId);
                    if (attr == null && toId == -1)	// Для повторного экпорта можно отсутсвие атрирбута игнорировать
                        throw new SQLException("Атрибут с UID='" + entityId + "' не найден.");
                    String revIds = "";
                    KrnClass cls = null;
                    KrnClass type = null;
                    int collectionType = 0;
                    boolean isUnique = false;
                    boolean isIndexed = false;
                    boolean isMultilingual = false;
                    boolean isRepl = false;
                    int size = 0;
                    long flags = 0;
                    KrnAttribute rAttr = null;
                    KrnAttribute sAttr = null;
                    boolean sDesc = false;
                    String aname = "";
                    String comment = "";
                    String tname = "";
                    int accessModifier = 0;
                    if (attr != null) {
                        cls = db.getClassById(attr.classId);
                        type = db.getClassById(attr.typeClassId);
                        collectionType = attr.collectionType;
                        isUnique = attr.isUnique;
                        isIndexed = attr.isIndexed;
                        isMultilingual = attr.isMultilingual;
                        isRepl = attr.isRepl;
                        size= attr.size;
                        flags = attr.flags;
                        rAttr = attr.rAttrId != 0 ? db.getAttributeById(attr.rAttrId) : null;
                        sAttr = attr.sAttrId != 0 ? db.getAttributeById(attr.sAttrId) : null;
                        aname = attr.name;
                        sDesc = attr.sDesc;
                        comment = getAttributeComment(attr.id);
                        KrnAttribute[] attrs = db.getRevAttributes(attr.id);
                        long[] longs = new long[attrs.length];
                        for (int i = 0; i < attrs.length; i++)
                            longs[i] = attrs[i].id;
                        revIds = Funcs.getIds(longs);
                        tname = (action == ACTION_CREATE) ? attr.tname : "";
                        accessModifier = attr.accessModifierType;
                    }
                    p.process(new AttributeChange(id, action, entityId, aname, cls, type, collectionType, isUnique, isIndexed, isMultilingual, isRepl, size, flags, rAttr, sAttr, sDesc, revIds, comment, tname, accessModifier));
                }
                if (id > changeId) {
                    changeId = id;
                }
            }
            
            // Вытаскиваем изменения триггера класса 'Перед созданием объекта'
            changes = queryChanges(ENTITY_TYPE_CLS_TRIGGER_BEFORE_CREATE, fromId, toId, rh, deletedEntities);
            for (ChangesCls ch : changes) {
                long id = ch.id;
                int action = ch.action;
                String entityId = ch.entityId;
                KrnClass cls = db.getClassByUid(entityId);
                String name = "Before creating the object";
                byte[] me = cls.beforeCreateObjExpr;
                int tr = cls.beforeCreateObjTr;
                String expr = (me!=null?HexStringOutputStream.toHexString(me, 0, me.length):"");
                p.process(new TriggerChange(id, action, entityId, name, ENTITY_TYPE_CLS_TRIGGER_BEFORE_CREATE, expr, tr)); 
                if (id > changeId) {
                    changeId = id;
                }
            }
            
            // Вытаскиваем изменения триггера класса 'После создания объекта'
            changes = queryChanges(ENTITY_TYPE_CLS_TRIGGER_AFTER_CREATE, fromId, toId, rh, deletedEntities);
            for (ChangesCls ch : changes) {
                long id = ch.id;
                int action = ch.action;
                String entityId = ch.entityId;
                KrnClass cls = db.getClassByUid(entityId);
                String name = "After creating the object";
                byte[] me = cls.afterCreateObjExpr;
                int tr = cls.afterCreateObjTr;
                String expr = (me!=null?HexStringOutputStream.toHexString(me, 0, me.length):"");
                p.process(new TriggerChange(id, action, entityId, name, ENTITY_TYPE_CLS_TRIGGER_AFTER_CREATE, expr, tr)); 
                if (id > changeId) {
                    changeId = id;
                }
            }
            
            // Вытаскиваем изменения триггера класса 'Перед удалением объекта'
            changes = queryChanges(ENTITY_TYPE_CLS_TRIGGER_BEFORE_DELETE, fromId, toId, rh, deletedEntities);
            for (ChangesCls ch : changes) {
                long id = ch.id;
                int action = ch.action;
                String entityId = ch.entityId;
                KrnClass cls = db.getClassByUid(entityId);
                String name = "Before removing the object";
                byte[] me = cls.beforeDeleteObjExpr;
                int tr = cls.beforeDeleteObjTr;
                String expr = (me!=null?HexStringOutputStream.toHexString(me, 0, me.length):"");
                p.process(new TriggerChange(id, action, entityId, name, ENTITY_TYPE_CLS_TRIGGER_BEFORE_DELETE, expr, tr)); 
                if (id > changeId) {
                    changeId = id;
                }
            }
            
            // Вытаскиваем изменения триггера класса 'После удаления объекта'
            changes = queryChanges(ENTITY_TYPE_CLS_TRIGGER_AFTER_DELETE, fromId, toId, rh, deletedEntities);
            for (ChangesCls ch : changes) {
                long id = ch.id;
                int action = ch.action;
                String entityId = ch.entityId;
                KrnClass cls = db.getClassByUid(entityId);
                String name = "After removing the object";
                byte[] me = cls.afterDeleteObjExpr;
                int tr = cls.afterDeleteObjTr;
                String expr = (me!=null?HexStringOutputStream.toHexString(me, 0, me.length):"");
                p.process(new TriggerChange(id, action, entityId, name, ENTITY_TYPE_CLS_TRIGGER_AFTER_DELETE, expr, tr)); 
                if (id > changeId) {
                    changeId = id;
                }
            }
            
            // Вытаскиваем изменения триггера атрибута 'Перед изменением значения атрибута'
            changes = queryChanges(ENTITY_TYPE_ATTR_TRIGGER_BEFORE_CHANGE, fromId, toId, rh, deletedEntities);
            for (ChangesCls ch : changes) {
                long id = ch.id;
                int action = ch.action;
                String entityId = ch.entityId;
                KrnAttribute attr = db.getAttributeByUid(entityId);
                String name = "Before changing the attribute value";
                byte[] me = attr.beforeEventExpr;
                int tr = attr.beforeEventTr;
                String expr = (me!=null?HexStringOutputStream.toHexString(me, 0, me.length):"");
                p.process(new TriggerChange(id, action, entityId, name, ENTITY_TYPE_ATTR_TRIGGER_BEFORE_CHANGE, expr, tr)); 
                if (id > changeId) {
                    changeId = id;
                }
            }
            
            // Вытаскиваем изменения триггера атрибута 'После изменения значения атрибута'
            changes = queryChanges(ENTITY_TYPE_ATTR_TRIGGER_AFTER_CHANGE, fromId, toId, rh, deletedEntities);
            for (ChangesCls ch : changes) {
                long id = ch.id;
                int action = ch.action;
                String entityId = ch.entityId;
                KrnAttribute attr = db.getAttributeByUid(entityId);
                String name = "After changing the attribute value";
                byte[] me = attr.afterEventExpr;
                int tr = attr.afterEventTr;
                String expr = (me!=null?HexStringOutputStream.toHexString(me, 0, me.length):"");
                p.process(new TriggerChange(id, action, entityId, name, ENTITY_TYPE_ATTR_TRIGGER_AFTER_CHANGE, expr, tr)); 
                if (id > changeId) {
                    changeId = id;
                }
            }
            
            // Вытаскиваем изменения триггера атрибута 'Перед удалением значения атрибута'
            changes = queryChanges(ENTITY_TYPE_ATTR_TRIGGER_BEFORE_DELETE, fromId, toId, rh, deletedEntities);
            for (ChangesCls ch : changes) {
                long id = ch.id;
                int action = ch.action;
                String entityId = ch.entityId;
                KrnAttribute attr = db.getAttributeByUid(entityId);
                String name = "Before removing the attribute value";
                byte[] me = attr.beforeDelEventExpr;
                int tr = attr.beforeDelEventTr;
                String expr = (me!=null?HexStringOutputStream.toHexString(me, 0, me.length):"");
                p.process(new TriggerChange(id, action, entityId, name, ENTITY_TYPE_ATTR_TRIGGER_BEFORE_DELETE, expr, tr)); 
                if (id > changeId) {
                    changeId = id;
                }
            }
            
            // Вытаскиваем изменения триггера атрибута 'После удаления значения атрибута'
            changes = queryChanges(ENTITY_TYPE_ATTR_TRIGGER_AFTER_DELETE, fromId, toId, rh, deletedEntities);
            for (ChangesCls ch : changes) {
                long id = ch.id;
                int action = ch.action;
                String entityId = ch.entityId;
                KrnAttribute attr = db.getAttributeByUid(entityId);
                String name = "After removing the attribute values";
                byte[] me = attr.afterDelEventExpr;
                int tr = attr.afterDelEventTr;
                String expr = (me!=null?HexStringOutputStream.toHexString(me, 0, me.length):"");
                p.process(new TriggerChange(id, action, entityId, name, ENTITY_TYPE_ATTR_TRIGGER_AFTER_DELETE, expr, tr)); 
                if (id > changeId) {
                    changeId = id;
                }
            }

			// Вытаскиваем изменения методов (в первом цикле собираем все события по удалению сущности, во втором все остальные)
            changes = queryChanges(ENTITY_TYPE_METHOD, fromId, toId, rh, deletedEntities);
			final Map<String, Long> changeIdByEntityId = new HashMap<String, Long>();
            for (ChangesCls ch : changes) {
                long id = ch.id;                
                int action = ch.action;
                String entityId = ch.entityId;
                if (action == ACTION_DELETE) {
                    p.process(new MethodChange(id, action, entityId, "", null, false, "", ""));
                } else if (action == ACTION_MODIFY) {
                	changeIdByEntityId.put(entityId, id);
                }
                if (id > changeId) {
                    changeId = id;
                }
            }
            for (ChangesCls ch : changes) {
                long id = ch.id;
                int action = ch.action;
                String entityId = ch.entityId;
                if (action != ACTION_DELETE) {
                    if (deletedEntities.contains(entityId) || (action == ACTION_MODIFY && changeIdByEntityId.get(entityId).longValue() != id)) {
                    	// Игнорируются все события удаленных сущностей и все изменения метода, кроме последнего 
                    	continue;
                    }
                    try {
                        KrnMethod m = null;
                        m = db.getMethodByUid(entityId);
                        String mname = "";
                        KrnClass cls = null;
                        boolean isClassMethod = false;
                        String expr = null;
                        String comment = null;
                        if (m != null) {
                            mname = m.name;
                            cls = db.getClassById(m.classId);
                            isClassMethod = m.isClassMethod;
                            byte[] me = getMethodExpression(m.uid);
                            expr = HexStringOutputStream.toHexString(me, 0, me.length);
                            comment = getMethodComment(m.uid);
                        } else {
                        	Pair<KrnMethod, byte[]> pair = getDeletedMethod(entityId);
                        	if (pair == null && toId == -1)	// Для повторного экпорта можно отсутсвие атрирбута игнорировать
                                throw new SQLException("Метод UID='" + entityId + "' не найден.");
                        	
                            mname = pair.first.name;
                            cls = db.getClassById(pair.first.classId);
                            isClassMethod = pair.first.isClassMethod;
                            expr = HexStringOutputStream.toHexString(pair.second, 0, pair.second.length);
                        }
                        p.process(new MethodChange(id, action, entityId, mname, cls, isClassMethod, expr, comment));
                    } catch (DriverException e) {
            			log.error(e, e);
                        throw new SQLException(e.getMessage());
                    }
                }
                if (id > changeId) {
                    changeId = id;
                }
            }

            // Вытаскиваем измения индексов            
            ResultSetHandler rsh = new ChangeClsResultSetHandler();
            List<KrnChangeCls> chs = queryChanges(ENTITY_TYPE_INDEX,fromId,toId,rsh);
            for(KrnChangeCls ch : chs){
            	boolean check = true;
            	KrnIndex ndx = null;
            	if(indexExists(ch.getEntityUID())){
            		ndx = getIndexByUid(ch.getEntityUID());
            	}
				IndexChange indexChange = new IndexChange(ch.getId(),ch.getAction(),ch.getEntityUID());
        		// Передаем информацию о ключах и языках индекса только при создании индекса
				if (ch.getAction() == ACTION_CREATE) {
					if (ndx != null) {				
        				KrnClass krnClass = db.getClassById(ndx.getClassId());
        				indexChange.setKrnClass(krnClass);
	            		//Собираем информацию о ключах индекса в xml-ку
	            		List<KrnIndexKey> keys = getIndexKeysByIndexId(ndx.getId());
	            		for(KrnIndexKey key : keys){
	            			KrnAttribute krnAttr = db.getAttributeById(key.getAttributeId());	            			
	            			indexChange.addKeyChange(new IndexKeyChange(krnAttr,key.getKeyOrderNumber(),key.isDesc()));
	            		}	            		
					} else {
						check = false;
					}
        		}
            	if(check){
            		p.process(indexChange);
            	}
            	if(ch.getId() > changeId){
            		changeId = ch.getId();
            	}
            }
			return changeId;
		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	public Iterator getDataChanges(final long fromId, final LongHolder lastId)
			throws DriverException {
		return getDataChanges(fromId, -1, lastId);
	}

	public Iterator getDataChanges(final long fromId, final long toId,
			final LongHolder lastId) throws DriverException {
		final long attrCreating = db.getAttributeByName(
				db.getClassByName("Объект").id, "creating").id;
		final long attrDeleting = db.getAttributeByName(
				db.getClassByName("Объект").id, "deleting").id;
		class RSH implements ResultSetHandler {
			private Set<Long> createDeletedObjects;

			public Object handle(ResultSet rs) throws SQLException {
				Map<Integer, KrnObject> indList = new TreeMap<Integer, KrnObject>();
				List<DataChange> res = new ArrayList<DataChange>();
				try {
					while (rs.next()) {
						long objId = rs.getLong("c_object_id");
						if (createDeletedObjects != null
								&& createDeletedObjects.contains(objId))
							continue; // созданные и удаленные объекты
						// пропускаются
						long attrId = rs.getLong("c_attr_id");
						String attrUid = db.getAttributeById(attrId).uid;
						long langId = rs.getLong("c_lang_id");
						Object value = null;
						if (attrId == attrCreating || attrId == attrDeleting) {
							if (attrId == attrCreating) {
								KrnClass cls = db.getClassById(getDirtyObjectById(objId).classId);
								value = cls.uid;
								res.add(new DataChange(0, getDirtyObjectById(objId).uid, attrUid, 0, "0", value));
							} else if (attrId == attrDeleting) {
								value = "0";
								String objUid = getSanitizedString(rs, "c_object_uid");
								if (objUid != null && objUid.length() > 0) {
									res.add(new DataChange(0, objUid, attrUid, 0, "0", value));
								}
							}
						} else {
							KrnAttribute attr = db.getAttributeById(attrId);
							String langUid = "0";
							if (langId > 0)
								langUid = getDirtyObjectById(langId).uid;
							log.debug("objId = " + objId
									+ " attrId = " + attrId + " langId = "
									+ langId);
							if (attr.collectionType > 0) {
								SortedSet<Value> values = getValues(
										new long[] { objId }, null, attrId, langId, 0);
								if (attr.typeClassId >= 99) {
									KrnObject ctrl = null;
									indList.clear();
									List<KrnObject> objList = new ArrayList<KrnObject>();
									for (Value val : values) {
										KrnObject o = (KrnObject) val.value;
										if (isDeleted(o)) {// проверка на
											// удаленность
											// если ссылка указывает на
											// удаленный объект,
											// то создаем Exception
											if (attr.collectionType == COLLECTION_ARRAY) {
												indList.put(val.index, o);
											} else if (attr.collectionType == COLLECTION_SET) {
												objList.add((KrnObject) o);
											} else {
												ctrl = o;
											}
										}
									}
									if (indList.size() > 0
											&& attr.collectionType == COLLECTION_ARRAY) {
										log.debug("Найдена ссылка на удаленный объект!");
										log.debug("objectId = "
												+ objId + " ("
												+ getObjectById(objId).uid
												+ "), attrId = " + attrId
												+ " (" + attr.name + ")");
										log.debug("index | reference (uid)");
										for (Integer integer : indList.keySet()) {
											KrnObject ref = indList
													.get(integer);
											log.debug(integer
													+ "    | " + ref.id + " ("
													+ ref.uid + ")");
										}
										throw new DriverException(
												"The reference to the removed object is found!");
									} else if (objList.size() > 0
											&& attr.collectionType == COLLECTION_SET) {
										log.debug("Найдена ссылка на удаленный объект!");
										log.debug("objectId = "
												+ objId + " ("
												+ getObjectById(objId).uid
												+ "), attrId = " + attrId
												+ " (" + attr.name + ")");
										log.debug("reference (uid)");
										for (KrnObject ref : objList) {
											log.debug(ref.id + " ("
													+ ref.uid + ")");
										}
										log.debug("Exception skiped for SET!");// временно!
										// throw new DriverException("The
										// reference to the removed object is
										// found!");
									} else if (ctrl != null) {
										log.debug("Найдена ссылка на удаленный объект!");
										log.debug("objectId = "
												+ objId + " ("
												+ getObjectById(objId).uid
												+ "), attrId = " + attrId
												+ " (" + attr.name + ")");
										log.debug("reference (uid)");
										log.debug(ctrl.id + " ("
												+ ctrl.uid + ")");
										log.debug("Exception skiped for SINGLE reference!");// временно!
										// throw new DriverException("The
										// reference to the removed object is
										// found!");
									}
									if (objList.size() > 0) {
										for (Value val : values) {
											KrnObject o = (KrnObject) val.value;
											if (!isDeleted(o)) // проверка на
												// удаленность
												res
														.add(new DataChange(
																0,
																getDirtyObjectById(objId).uid,
																attrUid,
																val.index,
																langUid,
																val.value));
										}
									} else
										for (Value val : values) {
											res
													.add(new DataChange(
															0,
															getDirtyObjectById(objId).uid,
															attrUid, val.index,
															langUid, val.value));
										}
								} else {
									for (Value val : values) {
										res.add(new DataChange(0,
												getDirtyObjectById(objId).uid,
												attrUid, val.index, langUid,
												val.value));
									}
								}
								// Если все значения множественного атрибута удалены
								if (values.size() == 0)
									res.add(new DataChange(0, getDirtyObjectById(objId).uid,
											attrUid, 0, langUid, null));
							} else {
								value = getValue(objId, attrId, 0, langId, 0);
								if (attr.typeClassId > 99) {
									KrnObject o = (KrnObject) value;
									if (o == null || isDeleted(o))
										res.add(new DataChange(0,
												getDirtyObjectById(objId).uid,
												attrUid, 0, langUid, null));
									else
										res.add(new DataChange(0,
												getDirtyObjectById(objId).uid,
												attrUid, 0, langUid, value));

								} else
									res.add(new DataChange(0,
											getDirtyObjectById(objId).uid,
											attrUid, 0, langUid, value));
							}
						}
					}
				} catch (DriverException e) {
					log.error(e, e);
					throw new SQLException("ERROR: " + e.getMessage());
				}
				return res;
			}
		}
		RSH rh = new RSH();
		QueryRunner qr = new QueryRunner(true);
		List<Object> res;
		try {
			if (toId == -1)
				rh.createDeletedObjects = prepareDataChanges(fromId);

			String idColumn = IDColumnName();
			String stoId = "";
			if (toId > -1) {
				stoId = " AND " + idColumn + "<=" + toId;
			}
			String whereClause = " AND " + idColumn + ">" + fromId + stoId;
			
			String firstPart = "SELECT DISTINCT c_object_id, c_object_uid, c_attr_id, c_lang_id FROM "+getDBPrefix()+"t_changes c"
					+ " WHERE c_tr_id=0 AND c_object_id IN"
					+ " (SELECT c_obj_id FROM "+getClassTableName(99)+" ct, "+getDBPrefix()+"t_classes c"
					+ " WHERE c.c_id=ct.c_class_id AND "+getCIsRepl(1)+" AND c_class_id IN"
					+ " (SELECT c_child_id FROM "+getDBPrefix()+"t_clinks"
					+ " WHERE c_parent_id IN (SELECT c_id FROM "+getDBPrefix()+"t_classes WHERE "+getCIsRepl(1)+"))"
					+ " AND c_class_id NOT IN ("
					+ getNotReplicatedClasses()
					+ "))";

			res = new ArrayList<Object>();
			
			// creating attribute
			String rc = getRecreatedObjects(whereClause);
			String union = "";
			if (rc.length() > 0) {
				union += " UNION SELECT DISTINCT c_object_id, c_attr_id, c_lang_id FROM "+getDBPrefix()+"t_changes c"
				+ " WHERE c_tr_id=0 AND c_attr_id=" + attrCreating
				+ " AND c_id=(SELECT MAX(c_id) FROM "+getDBPrefix()+"t_changes WHERE c_object_id=c.c_object_id AND c_tr_id=0)"
				+ whereClause
				// учитываем rc
				+ " AND " + rc;
			}
			res.addAll((List<Object>) qr.query(conn, firstPart
					+ " AND c_attr_id=" + attrCreating + whereClause + union, rh));
			
			// all others attributes
			String sql = firstPart
					+
					// атрибуты реплицируемых классов
					" AND c_attr_id IN (SELECT c_id FROM "+getDBPrefix()+"t_attrs"
					+ " WHERE c_class_id IN (SELECT c_parent_id FROM "+getDBPrefix()+"t_clinks"
					+ " WHERE c_child_id IN (SELECT c_id FROM "+getDBPrefix()+"t_classes WHERE "+getCIsRepl(1)+"))"
					+ " OR c_class_id IN (SELECT c_child_id FROM "+getDBPrefix()+"t_clinks"
					+ " WHERE c_parent_id IN (SELECT c_id FROM "+getDBPrefix()+"t_classes WHERE "+getCIsRepl(1)+")))"
					+

					// исключить атрибуты, ссылающиеся на НЕреплицируемые классы
					" AND c_attr_id NOT IN (SELECT c_id FROM "+getDBPrefix()+"t_attrs"
					+ " WHERE c_type_id IN (SELECT c_id FROM "+getDBPrefix()+"t_classes WHERE c_id>10 AND "+getCIsRepl(0)+"))"
					+

					// исключить НЕреплицируемые атрибуты
					" AND c_attr_id IN (SELECT c_id FROM "+getDBPrefix()+"t_attrs WHERE "+getCIsRepl(1)+")"
					+

					// условия для выбора только последнего изменения //теперь
					// не надо MAX index, вместо этого работает DISTINCT
					// " AND " + idColumn + " IN (SELECT MAX(" + idColumn + ")
					// FROM t_changes" +
					// " WHERE c_object_id=c.c_object_id AND
					// c_attr_id=c.c_attr_id AND c_lang_id=c.c_lang_id)" +

					" AND c_attr_id NOT IN (" + attrCreating + ","
					+ attrDeleting + ")" + " AND c_attr_id NOT IN ("
					+ getNotReplicatedAttrs() + ")" + whereClause + union;
			
			log.debug("sql = " + sql);
			res.addAll((List<Object>) qr.query(conn, sql, rh));
			
			// deleting attribute
			String deletedObjectsSQL = "SELECT DISTINCT c_object_id, c_object_uid, c_attr_id, c_lang_id FROM " + getDBPrefix() + "t_changes" + " WHERE c_tr_id=0";
			log.debug("RUS sql = " + deletedObjectsSQL + " AND c_attr_id=" + attrDeleting + whereClause);
			res.addAll((List<Object>) qr.query(conn, deletedObjectsSQL + " AND c_attr_id=" + attrDeleting + whereClause, rh));
			
		} catch (SQLException e) {
			throw convertException(e);
		}

		if (res.size() > 0) {
			long lastId_ = 0;
			try {
				lastId_ = (Long) qr.query(conn, "SELECT MAX(" + IDColumnName()
						+ ") FROM "+getDBPrefix()+"t_changes", new ResultSetHandler() {
					public Object handle(ResultSet resultSet)
							throws SQLException {
						Long res = 0L;
						if (resultSet.next())
							res = resultSet.getLong(1);
						return res;
					}
				});
			} catch (SQLException e) {
				log.error(e, e);
				throw convertException(e);
			}
			lastId.setValue(lastId_);
		} else
			lastId.setValue(fromId);
		return res.iterator();
	}

	protected String getCIsRepl(int c_is_repl) {
		return "c_is_repl="+c_is_repl;
	}
	private String getNotReplicatedAttrs() throws DriverException {
		return " SELECT c_id FROM "+getDBPrefix()+"t_attrs WHERE c_class_id="
				+ db.getClassByName("Объект").id + " AND c_name='locks'";
	}

	private String getNotReplicatedClasses() {
		return " SELECT c_child_id FROM "+getDBPrefix()+"t_clinks WHERE c_parent_id IN"
				+ " (SELECT c_id FROM "+getDBPrefix()+"t_classes WHERE (c_name='ImpExp' OR c_name='ReplCollection'))";
	}

	/**
	 * 	Компиляция фильтра по его конфигурации в виде xml
	 */
	public String compileFilter(long id, long langId, Element xml, long trId, Session s)
			throws DriverException {
		//TODO Общую часть вытащить в Driver2 (см. deleteValues)
		try {
			String res = "";
			
			KrnClass flrCls = db.getClassByName("Filter");
			KrnAttribute clsNameAttr = db.getAttributeByName(flrCls.id,	"className");
			KrnAttribute paramAttr = db.getAttributeByName(flrCls.id, "dateSelect");
			
			Element e = xml.getChild("attrFlr");
			if (e != null && e.getText().length() > 0) {
				String className = e.getText();
				int pos = className.indexOf(".");
				if (pos > 0)
					className = className.substring(0, pos);
				KrnObject obj=null ;
				if(id>0) {//условие на случай компиляции локального фильтра(id=-1)
					obj = getObjectById(id);
					
					// Устанавливаем фильтруемый класс
					s.setValue(obj, clsNameAttr.id, 0, 0, trId, className, false);
				}
				
				KrnClass clsFlr = db.getClassByName(className);
				
				if (clsFlr != null) {
					StringBuffer sb_sql = new StringBuffer();
					Map<String, String> sqlParMap = new HashMap<String, String>();
					createSqlUnion(id, clsFlr, sqlParMap, sb_sql, xml,s);
					long date_param = 0L;
					if (sb_sql.indexOf("readCurrDate()") > 0) {
						date_param |= 1;
					}
					if (sb_sql.indexOf("readFirstDate()") > 0) {
						date_param |= 2;
					}
					if (sb_sql.indexOf("readLastDate()") > 0) {
						date_param |= 4;
					}
					// Устанавливаем флаг использования вводимых пользователем дат
					if(id>0) {
						s.setValue(obj, paramAttr.id, 0, 0, trId, new Long(date_param), false);
					}
					res = sb_sql.toString();
					log.info((id==-1?"FilterLocal:":"id=" + id) + " " + res);
				} else {
					log.warn("ОШИБКА:Для фильтра:'" + (xml.getChild("title")!=null && xml.getChild("title").getChild("L" + langId)!=null? xml.getChild("title").getChild("L" + langId).getText():"") 
							+ " id=" + id + "'не найден фильтруемый класс '" + clsNameAttr + "'!");
				}
			} else {
				log.warn("ОШИБКА:Для фильтра:'" + (xml.getChild("title")!=null && xml.getChild("title").getChild("L" + langId)!=null? xml.getChild("title").getChild("L" + langId).getText():"") 
						+ " id=" + id + "'не задан фильтруемый класс!");
			}
			return res;
		} catch (Exception e) {
			throw new DriverException(e);
		}
	}

	public class KrnObjectComparator implements Comparator<KrnObject> {
		public int compare(KrnObject o1, KrnObject o2) {
			if (!(o1 instanceof KrnObject) || !(o2 instanceof KrnObject)) {
				return 0;
			}
			return (((KrnObject) o1).id < ((KrnObject) o2).id ? -1
					: (((KrnObject) o1).id == ((KrnObject) o2).id ? 0 : 1));
		}

	}

	public long filterCount(long[] fids, long langId, long userId,
			long[] baseIds, SrvOrLang orLang, long trId,
			Session session) throws DriverException {
		long res = 0;
		try {
			for (long fid : fids) {
				KrnObject fobj = db.getFilterObject(fid, this);
				String fuid = fobj.uid;
				String sql = db.getFilterSql(fobj, this, trId);
				//убираем из sql информацию об аггрегирегации
        		int indexGroupFunc=sql.indexOf(" GROUPPING ");
        		if(indexGroupFunc>0) {
        			sql=sql.substring(0,indexGroupFunc);
        		}
				if (sql.length() > 0) {
					ResultSet set = null;
					Statement st = null;
					PreparedStatement pst = null;
					long time = System.currentTimeMillis();
					List<Boolean> paramRespRegs = new ArrayList<>();
					try {
						String str = getSqlExpr(fuid, sql, langId, userId,
							baseIds, orLang, trId, session, paramRespRegs);
					//Преобразование в количество записей
					str="/* " + fuid + " */ SELECT COUNT(*) FROM ( "+str+" ) t_cnt";
					log.debug("FILTER_COUNT:" + fuid + " SQL:" + str);
					int is = params.size();
					if (is > 0) {
						pst = conn.prepareStatement(str);
						int k = 0;
						for (int j = 0; j < is; ++j) {
							Object o = getParamAt(fuid, j, session);
							String param = params.get(j);
							int typeAttr = "$".equals(param.substring(param.length() - 1)) ? 1 
									: "#".equals(param.substring(param.length() - 1)) ? 2 
											: "^".equals(param.substring(param.length() - 1)) ? 3 
													: "!".equals(param.substring(param.length() - 1)) ? 4 : 0;
							if (o instanceof List) {
								int size = ((List) o).size();
								log.debug("FILTER_COUNT:" + fuid + " " + params.get(j) + ": LIST(" + size + ")");
								for (int m = 0; m < ((List) o).size(); m++) {
									Object obj = ((List) o).get(m);
									if(obj instanceof String) {
										if(paramRespRegs.get(j)) {
											obj =((String) obj).toLowerCase();
										}
									}
									setValueFilterParam(pst, obj, ++k, typeAttr);
									if (size < 10) {
										log.debug("FILTER_COUNT:" + fuid + " " + params.get(j) + ":"
											+ getString(obj));
									}
								}
							} else {
								if(o instanceof String) {
									if(paramRespRegs.get(j)) {
										o =((String) o).toLowerCase();
									}
								}
								setValueFilterParam(pst, o, ++k, typeAttr);
								log.debug("FILTER_COUNT:" + fuid + " " + params.get(j) + ":"
										+ getString(o));
							}
						}
						set = pst.executeQuery();
					} else {
						st = conn.createStatement();
						set = st.executeQuery(str);
					}
					while (set.next()) {
						long count = set.getInt(1);
						res+=count;
					}
					} finally {
					if (set != null)
						set.close();
					if (st != null)
						st.close();
					if (pst != null)
						pst.close();
				}
					log.info("FILTER_COUNT:" + fuid +" TIME:"
							+ (System.currentTimeMillis() - time)+" COUNT:" + res);
				}
			}
		} catch (Exception e) {
			log.error(e, e);
			throw new DriverException(e.getMessage(),e);
		}
		return res;
	}
	
	@Override
	public List<KrnObject> filter(long[] fids, long langId, long userId,
			long[] baseIds, SrvOrLang orLang, int[] limit,int[] beginRows,int[] endRows, long trId,
			Session session) throws DriverException {
		List<KrnObject> res = new ArrayList<KrnObject>();
		Set<KrnObject> ids = new TreeSet<KrnObject>(new KrnObjectComparator());
		String error="";
		try {
			for (long fid : fids) {
				KrnObject fobj = db.getFilterObject(fid, this);
				String fuid = fobj.uid;
				String sql = db.getFilterSql(fobj, this, trId);
				//убираем из sql информацию об аггрегирегации
        		int indexGroupFunc=sql.indexOf(" GROUPPING ");
        		if(indexGroupFunc>0) {
        			sql=sql.substring(0,indexGroupFunc);
        		}
				if (sql.length() > 0) {
					ResultSet set = null;
					Statement st = null;
					PreparedStatement pst = null;
					long time = System.currentTimeMillis();
					String [] fullTextParams=null;
					HashMap<String,KrnObject> resFtMap = null;
					List<Boolean> paramRespRegs = new ArrayList<>();
					try {
						String strExpr = getSqlExpr(fobj.uid, sql, langId, userId,
							baseIds, orLang, trId, session, paramRespRegs);
						if(strExpr.indexOf("Error:")==0)
							error=";"+strExpr;
						StringBuilder str = new StringBuilder();
						str.append(strExpr);
					if(!"".equals(addColFullTextFind) && addColFullTextFind!=null){
						fullTextParams=addColFullTextFind.split(";");
						str.insert(str.indexOf("FROM"), ","+fullTextParams[0]+" as c_ft_uid ");
						resFtMap = new HashMap<String,KrnObject>();
						if(fullTextParams[0].equals(addColName))
							addColName="";
					}
					// Дополнительное выводимое поле
					if (addColName != null && !addColName.equals("")) {
						str.insert(str.indexOf("FROM"), ","+addColName+" c_add_uid ");
					}
					int beginRow=-1,endRow=-1;
					if (limit[0] > 0)
						str = addLimit(str, limit[0] + 1, 0);
					else if (((beginRows!=null && beginRows.length>0 && (beginRow = beginRows[0])>=0) 
							| (endRows!=null && endRows.length>0 && (endRow=endRows[0])>=0))
							&& (beginRow>=0 && endRow>=0 && endRow>=beginRow))
						str = byPage(str);
					
					str.insert(0, new StringBuilder("/* ").append(fuid).append(" */ ").toString());
					log.debug("FILTER:" + fuid + " SQL:" + str.toString());
					int is = params.size();
					if (is > 0 || (beginRow>=0 && endRow>=0 && endRow>=beginRow)) {
						pst = conn.prepareStatement(str.toString());
						int k = 0;
						for (int j = 0; j < is; ++j) {
							Object o = getParamAt(fobj.uid, j, session);
							String param = params.get(j);
							int typeAttr = "$".equals(param.substring(param.length() - 1)) ? 1 
									: "#".equals(param.substring(param.length() - 1)) ? 2 
										:"^".equals(param.substring(param.length() - 1)) ? 3
											:"!".equals(param.substring(param.length() - 1)) ? 4 : 0;
							if (o instanceof List) {
								for (int m = 0; m < ((List) o).size(); m++) {
									Object obj = ((List) o).get(m);
									if(obj instanceof String && paramRespRegs.get(j)) {
										obj =((String) obj).toLowerCase();
									}
									setValueFilterParam(pst, obj, ++k, typeAttr);
									log.debug("FILTER:" + fobj.uid + " " + params.get(j) + ":"
											+ getString(obj));
								}
							} else {
								if(o instanceof String && paramRespRegs.get(j)) {
									o =((String) o).toLowerCase();
								}
								setValueFilterParam(pst, o, ++k, typeAttr);
								log.debug("FILTER:" + fobj.uid + " " + params.get(j) + ":"
										+ getString(o));
							}
						}
						if(beginRow>=0 && endRow>=0 && endRow>=beginRow){
							setPageValue(pst, ++k, beginRow, endRow);
						}
						set = pst.executeQuery();
					} else {
						st = conn.createStatement();
						set = st.executeQuery(str.toString());
					}
					int count = 0;
					if (addColName != null && !addColName.equals("")) {
						MapMap resMap = new MapMap();
						while (set.next()) {
							long objId = set.getLong("c_obj_id");
							long classId = set.getLong("c_class_id");
							String uid = getSanitizedString(set, "c_uid");
							String addUid = getString(set, "c_add_uid");
							KrnObject obj = new KrnObject(objId, uid, classId);
							Integer key = sortedFindMap.get(addUid);
							if (key == null)
								key = 0;
							resMap.put(key, obj.id, obj);
							if(!"".equals(addColFullTextFind) && addColFullTextFind!=null){
								String addFtUid = getString(set, "c_ft_uid");
								resFtMap.put(addFtUid, obj);
							}
						}
						for (Object key : resMap.keySet()) {
							Map mp = resMap.get(key);
							if (mp != null) {
								for (Object o : mp.values()) {
									KrnObject obj = (KrnObject) o;
									if (obj != null && !ids.contains(obj)) {
										if (limit[0] == 0
												|| count++ < limit[0]) {
											res.add(obj);
											ids.add(obj);
										} else {
											// TODO Позже будем возвращать
											// реальное кол-во объектов
											limit[0]++;
											break;
										}
									}
								}
							}
						}
					} else {
						while (set.next()) {
							long objId = set.getLong("c_obj_id");
							long classId = set.getLong("c_class_id");
							String uid = getSanitizedString(set, "c_uid");
							KrnObject obj = new KrnObject(objId, uid, classId);
							if(!"".equals(addColFullTextFind) && addColFullTextFind!=null){
								String addFtUid = getString(set, "c_ft_uid");
								resFtMap.put(addFtUid, obj);
							}
							if (!ids.contains(obj)) {
								if (limit[0] == 0 || count++ < limit[0]) {
									res.add(obj);
									ids.add(obj);
								} else {
									// TODO Позже будем возвращать реальное кол-во объектов
									limit[0]++;
									break;
								}
							}
						}
					}
					if(!"".equals(addColFullTextFind) && addColFullTextFind!=null){
						if(fullTextParams.length>3){
							long ft_attrId=Long.valueOf(fullTextParams[1]);
							long ft_langId="".equals(fullTextParams[2])?0:getSystemLangs().get(Integer.parseInt(fullTextParams[2]) - 1).id;
							String pattern=fullTextParams[3];
							res=Indexer.find(ft_attrId, ft_langId, pattern,resFtMap);
						}else{
							log.info("FILTER:" + fuid +" Ошибка полнотекстового поиска");
						}
					}
					} finally {
					if (set != null)
						set.close();
					if (st != null)
						st.close();
					if (pst != null)
						pst.close();
				}
					log.info("FILTER:" + fuid +" TIME:"
							+ (System.currentTimeMillis() - time)+" COUNT:" + res.size());
				}
			}
		} catch (Exception e) {
			log.error(e, e);
			throw new DriverException(e.getMessage()+error,e);
		}
		return res;
	}
	
	@Override
	public List<KrnObject> filterLocal(String sql,String fuid, long langId, long userId,
			long[] baseIds, SrvOrLang orLang, int limit,int beginRow,int endRow, long trId,
			Session session) throws DriverException {
		List<KrnObject> res = new ArrayList<KrnObject>();
		Set<KrnObject> ids = new TreeSet<KrnObject>(new KrnObjectComparator());
		String error="";
		try {
				if (sql.length() > 0) {
					ResultSet set = null;
					Statement st = null;
					PreparedStatement pst = null;
					long time = System.currentTimeMillis();
					String [] fullTextParams=null;
					HashMap<String,KrnObject> resFtMap = null;
					List<Boolean> paramRespRegs = new ArrayList<>();
					try {
						String strExpr = getSqlExpr(fuid, sql, langId, userId,
							baseIds, orLang, trId, session, paramRespRegs);
						if(strExpr.indexOf("Error:")==0)
							error=";"+strExpr;
						StringBuilder str = new StringBuilder();
						str.append(strExpr);
					if(!"".equals(addColFullTextFind) && addColFullTextFind!=null){
						fullTextParams=addColFullTextFind.split(";");
						str.insert(str.indexOf("FROM"), ","+fullTextParams[0]+" as c_ft_uid ");
						resFtMap = new HashMap<String,KrnObject>();
						if(fullTextParams[0].equals(addColName))
							addColName="";
					}
					// Дополнительное выводимое поле
					if (addColName != null && !addColName.equals("")) {
						str.insert(str.indexOf("FROM"), ","+addColName+" c_add_uid ");
					}
					if (limit > 0)
						str = addLimit(str, limit + 1, 0);
					else if (beginRow>=0 && endRow>=0 && endRow>=beginRow)
						str = byPage(str);
					
					str.insert(0, new StringBuilder("/* ").append(fuid).append(" */ ").toString());
					log.debug("FILTER:" + fuid + " SQL:" + str.toString());
					int is = params.size();
					if (is > 0 || (beginRow>=0 && endRow>=0 && endRow>=beginRow)) {
						pst = conn.prepareStatement(str.toString());
						int k = 0;
						for (int j = 0; j < is; ++j) {
							Object o = getParamAt(fuid, j, session);
							String param = params.get(j);
							int typeAttr = "$".equals(param.substring(param.length() - 1)) ? 1 
									: "#".equals(param.substring(param.length() - 1)) ? 2 
										:"^".equals(param.substring(param.length() - 1)) ? 3
											:"!".equals(param.substring(param.length() - 1)) ? 4 : 0;
							if (o instanceof List) {
								for (int m = 0; m < ((List) o).size(); m++) {
									Object obj = ((List) o).get(m);
									if(obj instanceof String) {
										if(paramRespRegs.get(j)) {
											obj =((String) obj).toLowerCase();
										}
									}
									setValueFilterParam(pst, obj, ++k, typeAttr);
									log.debug("FILTER:" + fuid + " " + params.get(j) + ":"
											+ getString(obj));
								}
							} else {
								if(o instanceof String) {
									if(paramRespRegs.get(j)) {
										o =((String) o).toLowerCase();
									}
								}
								setValueFilterParam(pst, o, ++k, typeAttr);
								log.debug("FILTER:" + fuid + " " + params.get(j) + ":"
										+ getString(o));
							}
						}
						if(beginRow>=0 && endRow>=0 && endRow>=beginRow){
							setPageValue(pst, ++k, beginRow, endRow);
						}
						set = pst.executeQuery();
					} else {
						st = conn.createStatement();
						set = st.executeQuery(str.toString());
					}
					int count = 0;
					if (addColName != null && !addColName.equals("")) {
						MapMap resMap = new MapMap();
						while (set.next()) {
							long objId = set.getLong("c_obj_id");
							long classId = set.getLong("c_class_id");
							String uid = getSanitizedString(set, "c_uid");
							String addUid = getString(set, "c_add_uid");
							KrnObject obj = new KrnObject(objId, uid, classId);
							Integer key = sortedFindMap.get(addUid);
							if (key == null)
								key = 0;
							resMap.put(key, obj.id, obj);
							if(!"".equals(addColFullTextFind) && addColFullTextFind!=null){
								String addFtUid = getString(set, "c_ft_uid");
								resFtMap.put(addFtUid, obj);
							}
						}
						for (Object key : resMap.keySet()) {
							Map mp = resMap.get(key);
							if (mp != null) {
								for (Object o : mp.values()) {
									KrnObject obj = (KrnObject) o;
									if (obj != null && !ids.contains(obj)) {
										if (limit == 0
												|| count++ < limit) {
											res.add(obj);
											ids.add(obj);
										} else {
											// TODO Позже будем возвращать
											// реальное кол-во объектов
											limit++;
											break;
										}
									}
								}
							}
						}
					} else {
						while (set.next()) {
							long objId = set.getLong("c_obj_id");
							long classId = set.getLong("c_class_id");
							String uid = getSanitizedString(set, "c_uid");
							KrnObject obj = new KrnObject(objId, uid, classId);
							if(!"".equals(addColFullTextFind) && addColFullTextFind!=null){
								String addFtUid = getString(set, "c_ft_uid");
								resFtMap.put(addFtUid, obj);
							}
							if (!ids.contains(obj)) {
								if (limit == 0 || count++ < limit) {
									res.add(obj);
									ids.add(obj);
								} else {
									// TODO Позже будем возвращать реальное кол-во объектов
									limit++;
									break;
								}
							}
						}
					}
					if(!"".equals(addColFullTextFind) && addColFullTextFind!=null){
						if(fullTextParams.length>3){
							long ft_attrId=Long.valueOf(fullTextParams[1]);
							long ft_langId="".equals(fullTextParams[2])?0:getSystemLangs().get(Integer.parseInt(fullTextParams[2]) - 1).id;
							String pattern=fullTextParams[3];
							res=Indexer.find(ft_attrId, ft_langId, pattern,resFtMap);
						}else{
							log.info("FILTER:" + fuid +" Ошибка полнотекстового поиска");
						}
					}
					} finally {
					if (set != null)
						set.close();
					if (st != null)
						st.close();
					if (pst != null)
						pst.close();
				}
					log.info("FILTER:" + fuid +" TIME:"
							+ (System.currentTimeMillis() - time)+" COUNT:" + res.size());
				}
		} catch (Exception e) {
			log.error(e, e);
			throw new DriverException(e.getMessage()+error,e);
		}
		return res;
	}

	@Override
	public List<Object> filterGroup(long[] fids, long langId, long userId,
			long[] baseIds, SrvOrLang orLang, long trId,
			Session session) throws DriverException {
		List<Object> res = new ArrayList<>();
		try {
			for (long fid : fids) {
				KrnObject fobj = db.getFilterObject(fid, this);
				String fuid = fobj.uid;
				String groupping = "";
				String groupNumTables = null;
				String sql = db.getFilterSql(fobj, this, trId);

				int groupColsCount = 0;
				//заменяю в sql информацию об аггрегирегации
        		int indexGroupFunc=sql.indexOf(" GROUPPING ");
        		if(indexGroupFunc>0) {
        			groupping = sql.substring(indexGroupFunc+11);
        			sql = sql.substring(0, indexGroupFunc);
					if (sql.length() > 0) {
						sql = Funcs.validate(sql);
	        			String[] grps=groupping.split("\\|");
	        			String sqlSelect ="SELECT DISTINCT "+grps[1]+" AS OBJ_ID,"+grps[0];
	        			String sqlGroup = " GROUP BY " + grps[1];
	        			String[] clsIds=grps[1].split(",");
	        			String[] clss=grps[2].split(",");
	        			groupNumTables=grps[3];
	        			int indexFrom=sql.indexOf(" FROM");
	        			int indexOrderBy=sql.indexOf(" ORDER BY");
	        			if(indexOrderBy<0)
	        				indexOrderBy=indexGroupFunc;
	        			sql=sqlSelect+sql.substring(indexFrom,indexOrderBy)+sqlGroup+sql.substring(indexOrderBy,indexGroupFunc);
	
	        			//Преобразование в количество записей
	        			StringBuilder sql2 = new StringBuilder("SELECT c.c_obj_id,c.c_class_id,c.c_uid");
	        			Matcher matcher = groupRe.matcher(sql);
	        			while (matcher.find()) {
	        				sql2.append(",g.").append(Funcs.sanitizeSQL(matcher.group()));
	        				groupColsCount++;
	        			}
	        			sql2.append(" FROM ( ").append(sql).append(" ) g LEFT JOIN ").append(Funcs.sanitizeSQL(getClassTableName(Long.parseLong(clss[0])))).append(" c ON g.OBJ_ID = c.c_obj_id");
	        			
	        			sql = sql2.toString();
	        			
						ResultSet set = null;
						Statement st = null;
						PreparedStatement pst = null;
						long time = System.currentTimeMillis();
						String [] fullTextParams=null;
						HashMap<String,KrnObject> resFtMap = null;
						List<Boolean> paramRespRegs = new ArrayList<>();
						try {
							String strExpr = getSqlExpr(fobj.uid, sql, langId, userId,
								baseIds, orLang, trId,groupNumTables, session, paramRespRegs);
							StringBuilder str = new StringBuilder();
							str.append(strExpr);
							str.insert(0, new StringBuilder("/* ").append(fuid).append(" */ ").toString());
							log.debug("FILTER:" + fuid + " SQL:" + str.toString());
							int is = params.size();
							if (is > 0) {
								pst = conn.prepareStatement(str.toString());
								int k = 0;
								for (int j = 0; j < is; ++j) {
									Object o = getParamAt(fobj.uid, j, session);
									String param = params.get(j);
									int typeAttr = "$".equals(param.substring(param.length() - 1)) ? 1 
											: "#".equals(param.substring(param.length() - 1)) ? 2 
													: "^".equals(param.substring(param.length() - 1)) ? 3 
															: "!".equals(param.substring(param.length() - 1)) ? 4 : 0;
									if (o instanceof List) {
										for (int m = 0; m < ((List) o).size(); m++) {
											Object obj = ((List) o).get(m);
											if(obj instanceof String) {
												if(paramRespRegs.get(j)) {
													obj =((String) obj).toLowerCase();
												}
											}
											setValueFilterParam(pst, obj, ++k, typeAttr);
											log.debug("FILTER:" + fobj.uid + " " + params.get(j) + ":"
													+ getString(obj));
										}
									} else {
										if(o instanceof String) {
											if(paramRespRegs.get(j)) {
												o =((String) o).toLowerCase();
											}
										}
										setValueFilterParam(pst, o, ++k, typeAttr);
										log.debug("FILTER:" + fobj.uid + " " + params.get(j) + ":"
												+ getString(o));
									}
								}
								set = pst.executeQuery();
							} else {
								st = conn.createStatement();
								set = st.executeQuery(str.toString());
							}
							while (set.next()) {
								List<Object> rs = new ArrayList<>();
								long objId = set.getLong(1);
								long classId = set.getLong(2);
								String uid = getSanitizedString(set, 3);
								KrnObject obj = new KrnObject(objId, uid, classId);
								rs.add(obj);
								for (int i = 0; i < groupColsCount; i++) {
									rs.add(set.getLong(i + 4));
								}
								res.add(rs);
							}
						} finally {
							if (set != null)
								set.close();
							if (st != null)
								st.close();
							if (pst != null)
								pst.close();
						}
						log.info("FILTER:" + fuid +" TIME:"
								+ (System.currentTimeMillis() - time)+" COUNT:" + res.size());
					}
        		}
			}
		} catch (Exception e) {
			log.error(e, e);
			throw new DriverException(e.getMessage(),e);
		}
		return res;
	}

	@Override
	public long filterToAttr(long fid, long pobjId,long attrId,long langId, long userId,
			long[] baseIds, SrvOrLang orLang, long trId,
			Session session) throws DriverException {
		long res = 0;
		try {
			KrnObject fobj = db.getFilterObject(fid, this);
			String fuid = fobj.uid;
			String sql = db.getFilterSql(fobj, this, trId);
			//убираем из sql информацию об аггрегирегации
    		int indexGroupFunc=sql.indexOf(" GROUPPING ");
    		if(indexGroupFunc>0) {
    			sql=sql.substring(0,indexGroupFunc);
    		}
    		//компановка INSERT VSLUES совместно с SELECT
    		KrnAttribute attr=db.getAttributeById(attrId);
    		KrnAttribute f_cls_attr=db.getAttributeByName(fobj.classId, "className");
    		String clsName=session.getStringsSingular(fid,f_cls_attr.id,0,false,false);
    		KrnClass f_cls=db.getClassByName(clsName);
    		//атрибут должен принимать объекты соответствующего класса иметь тип набор и не являться обратным
    		if(attr==null ||attr.typeClassId!=f_cls.id || attr.collectionType!=COLLECTION_SET || attr.rAttrId!=0) return 0;
    		String ins_tname=getAttrTableName(attrId);
    		String ins_cname= getColumnName(attr);
    		//
			if (sql.length() > 0) {
				PreparedStatement pst = null;
				long time = System.currentTimeMillis();
				String [] fullTextParams=null;
				HashMap<String,KrnObject> resFtMap = null;
				List<Boolean> paramRespRegs = new ArrayList<>();
				try {
					String strExpr = getSqlExpr(fobj.uid, sql, langId, userId,
						baseIds, orLang, trId, session, paramRespRegs);
					StringBuilder str = new StringBuilder();
					str.append(strExpr);
				//исключить те объекты которые содержатся в наборе
				String not_in_sql=" WHERE m.c_obj_id NOT IN (SELECT "+ins_cname+" FROM "+ins_tname+" WHERE c_obj_id = "+pobjId+" AND c_tr_id="+trId+" AND c_del=0)";
				//построение запроса на вставку по выборке
	    		String ins_sql= "/* "+fuid+" */ INSERT INTO "+ins_tname+" SELECT "+pobjId+" as c_obj_id, "+trId+" as c_tr_id, 0 as c_del, m.c_obj_id as "+ins_cname+" FROM ("+str.toString()+") m "+not_in_sql;
				log.debug("FILTER:" + fuid + " SQL:" + ins_sql);
				int is = params.size();
				pst = conn.prepareStatement(ins_sql);
				if (is > 0) {
					int k = 0;
					for (int j = 0; j < is; ++j) {
						Object o = getParamAt(fobj.uid, j, session);
						String param = params.get(j);
						int typeAttr = "$".equals(param.substring(param.length() - 1)) ? 1 
								: "#".equals(param.substring(param.length() - 1)) ? 2 
										: "^".equals(param.substring(param.length() - 1)) ? 3 
												: "!".equals(param.substring(param.length() - 1)) ? 4 : 0;
						if (o instanceof List) {
							for (int m = 0; m < ((List) o).size(); m++) {
								Object obj = ((List) o).get(m);
								if(obj instanceof String) {
									if(paramRespRegs.get(j)) {
										obj =((String) obj).toLowerCase();
									}
								}
								setValueFilterParam(pst, obj, ++k, typeAttr);
								log.debug("FILTER:" + fobj.uid + " " + params.get(j) + ":"
										+ getString(obj));
							}
						} else {
							if(o instanceof String) {
								if(paramRespRegs.get(j)) {
									o =((String) o).toLowerCase();
								}
							}
							setValueFilterParam(pst, o, ++k, typeAttr);
							log.debug("FILTER:" + fobj.uid + " " + params.get(j) + ":"
									+ getString(o));
						}
					}
				}
				res = pst.executeUpdate();
				} finally {
				if (pst != null)
					pst.close();
			}
				log.info("FILTER:" + fuid +" TIME:"
						+ (System.currentTimeMillis() - time)+" COUNT_INSERT:" + res);
			}
		} catch (Exception e) {
			log.error(e, e);
			throw new DriverException(e.getMessage(),e);
		}
		return res;
	}

	protected StringBuilder addLimit(StringBuilder sql, int limit, int offset) {
		return sql.append(" LIMIT ").append(limit);
	}
	
	protected StringBuilder byPage(StringBuilder sql){
    	return	sql.append(" LIMIT ?,?");
	}

	protected void setPageValue(PreparedStatement pst,int i, int beginRow, int endRow) throws SQLException {
		pst.setLong(i, beginRow-(beginRow>0?1:0));
		pst.setLong(i+1, endRow-beginRow+(beginRow>0?1:0));
	}
	
	protected void setValueFilterParam(PreparedStatement pst, Object o, int i, int typeAttr)
			throws SQLException {
		if (o instanceof String) {
			if (typeAttr == 2) {
				setMemo(pst, i, (String) o);
			} else {
				String res = Funcs.normalizeInput((String) o);
				pst.setString(i, res);
			}
		}else if (o instanceof Long) {
			pst.setLong(i, (Long)o);
		}else if (o instanceof KrnObject) {
					pst.setLong(i, ((KrnObject)o).id);
		} else if (o instanceof java.sql.Time) {
			pst.setTimestamp(i, convertTime(kz.tamur.util.Funcs
					.convertTime(((java.sql.Time) o))));
		} else if (o instanceof java.util.Date) {
			if (typeAttr == 1) {
				pst.setTimestamp(i, convertTime(kz.tamur.util.Funcs
						.convertTime(((java.util.Date) o))));
			} else {
				pst.setDate(i, convertDate(kz.tamur.util.Funcs
						.convertDate(((java.util.Date) o))));
			}
		} else if (o instanceof com.cifs.or2.kernel.Date) {
			pst.setDate(i, convertDate((com.cifs.or2.kernel.Date) o));
		} else if (o instanceof com.cifs.or2.kernel.Time) {
			pst.setTimestamp(i, convertTime((com.cifs.or2.kernel.Time) o));
		}
	}

	public Object getValue(long objId, long attrId, int index, long langId,
			long trId) throws DriverException {
		SortedSet vs = getValues(new long[] { objId }, null, attrId, langId, trId);
		for (Object v1 : vs) {
			Value v = (Value) v1;
			if (v.index == index) {
				return v.value;
			}
		}
		return null;
	}

	protected long getLastClassId() throws SQLException {
		return getLastInsertId();
	}

	protected long getLastAttributeId() throws SQLException {
		return getLastInsertId();
	}
	
	protected long getLastIndexId() throws SQLException{
		return getLastInsertId();
	}
	
	protected long getLastMethodId() throws SQLException {
		return getLastInsertId();
	}

	protected long getLastObjectId() throws SQLException {
		return getLastInsertId();
	}

	protected long getLastInsertId() throws SQLException {
		long res;
		Statement st = conn.createStatement();
		ResultSet set = st.executeQuery("SELECT LAST_INSERT_ID()");
		if (set.next()) {
			res = set.getLong(1);
		} else {
			throw new SQLException("Failed to get last ID");
		}
		set.close();
		st.close();
		return res;
	}

	protected String getColumnDef(KrnAttribute attr, long langId) throws DriverException {
		int langIndex = getSystemLangIndex(langId);
		return getColumnDef(attr, langIndex);
	}

	protected String getColumnDef(KrnAttribute attr, int langIndex) throws DriverException {
		// TODO Уникальность пока не реализуется так как объект создается
		// пустым
		StringBuffer res = new StringBuffer(getColumnName(attr, langIndex));
		res.append(" ");
		res.append(getSqlTypeName(attr));
		if (attr.typeClassId == PC_STRING) {
			res.append(" BINARY");
		} else if (attr.typeClassId == PC_BOOL) {
			res.append(" DEFAULT 0");
		}
		return res.toString();
	}

	protected String getKeyDef(KrnAttribute attr, boolean pk) throws SQLException {
		if (attr.typeClassId > 10) {
			StringBuilder res = new StringBuilder("CONSTRAINT FK");
			res.append(attr.id);
			res.append(" FOREIGN KEY (");
			res.append(getColumnName(attr));
			res.append(") REFERENCES ");
			res.append(getClassTableNameComp(attr.typeClassId));
			res.append("(c_obj_id)");
			return res.toString();
		}
		return "";
	}

	protected int getSqlType(long typeId) {
		if (typeId == PC_STRING) {
			return Types.VARCHAR;
		} else if (typeId == PC_INTEGER) {
			return Types.BIGINT;
		} else if (typeId == PC_DATE) {
			return Types.DATE;
		} else if (typeId == PC_TIME) {
			return Types.TIMESTAMP;
		} else if (typeId == PC_BOOL) {
			return Types.BIT;
		} else if (typeId == PC_FLOAT) {
			return Types.DOUBLE;
		} else if (typeId == PC_MEMO) {
			return Types.CLOB;
		} else if (typeId == PC_BLOB) {
			return Types.BLOB;
		}
		return Types.BIGINT;
	}

	protected String getSqlTypeName(KrnAttribute attr) {
		return getSqlTypeName(attr.typeClassId, attr.size);
	}
	
	protected String getSqlTypeName(long typeId, int sz) {
		if (typeId == PC_STRING) {
			return "VARCHAR(" + (sz > 0 ? sz : 255) + ")";
		} else if (typeId == PC_INTEGER) {
			return "BIGINT";
		} else if (typeId == PC_DATE) {
			return "DATE";
		} else if (typeId == PC_TIME) {
			return "DATETIME";
		} else if (typeId == PC_BOOL) {
			return "BIT";
		} else if (typeId == PC_FLOAT) {
			return "DOUBLE";
		} else if (typeId == PC_MEMO) {
			return "TEXT";
		} else if (typeId == PC_BLOB) {
			return "LONGBLOB";
		}
		return "BIGINT";
	}

	protected void install() throws DriverException {
		try {
			boolean is_del_ = "1".equals(System.getProperty("isDel"));
			Statement st = conn.createStatement();
			String qStr="SHOW TABLES LIKE 't_ids'";
			if(isSchemeName){
				qStr="SHOW TABLES FROM "+db.getSchemeName()+" LIKE 't_ids'";
			}
			ResultSet set = st.executeQuery(qStr);
			boolean installed = set.next();
			set.close();
			if (!installed) {
		        isUpgrading = true;
		        isInstallDb=true;
				version = 59;
		        st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_classes ("
						+ "c_id BIGINT NOT NULL AUTO_INCREMENT,"
						+ "c_cuid CHAR(36) NOT NULL,"
						+ "c_name VARCHAR(255) NOT NULL,"
						+ "c_tname VARCHAR(30) DEFAULT NULL," 
						+ "c_parent_id BIGINT NOT NULL,"
						+ "c_is_repl BIT NOT NULL,"
						+ "c_before_create_obj LONGBLOB DEFAULT NULL,"
						+ "c_after_create_obj LONGBLOB DEFAULT NULL,"
						+ "c_before_delete_obj LONGBLOB DEFAULT NULL,"
						+ "c_after_delete_obj LONGBLOB DEFAULT NULL,"
						+ "c_before_create_obj_tr INTEGER DEFAULT 0 NOT NULL,"
						+ "c_after_create_obj_tr INTEGER DEFAULT 0 NOT NULL,"
						+ "c_before_delete_obj_tr INTEGER DEFAULT 0 NOT NULL,"
						+ "c_after_delete_obj_tr INTEGER DEFAULT 0 NOT NULL,"
                        + "c_comment TEXT,"
                        + "c_mod INTEGER NOT NULL,"
                        + "PRIMARY KEY(c_id),"
						+ "UNIQUE INDEX(c_name),"
						+ "UNIQUE INDEX(c_tname),"
						+ "UNIQUE INDEX(c_cuid),"
                        + "INDEX(c_parent_id))"
						+ " ENGINE=" + ENGINE);
				
				st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_clinks ("
								+ "c_parent_id BIGINT NOT NULL,"
								+ "c_child_id BIGINT NOT NULL,"
								+ "FOREIGN KEY(c_parent_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE,"
								+ "FOREIGN KEY(c_child_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE,"
								+ "PRIMARY KEY(c_parent_id,c_child_id))"
								+ " ENGINE=" + ENGINE);
				
				st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_attrs ("
								+ "c_id BIGINT NOT NULL AUTO_INCREMENT,"
								+ "c_auid CHAR(36) NOT NULL,"
								+ "c_class_id BIGINT NOT NULL,"
								+ "c_name VARCHAR(255) NOT NULL,"
								+ "c_tname VARCHAR(30) DEFAULT NULL," 
								+ "c_type_id BIGINT NOT NULL,"
								+ "c_col_type INTEGER NOT NULL,"
								+ "c_is_unique BIT NOT NULL,"
								+ "c_is_indexed BIT NOT NULL,"
								+ "c_is_multilingual BIT NOT NULL,"
								+ "c_is_repl BIT NOT NULL,"
								+ "c_size INTEGER NOT NULL,"
								+ "c_flags BIGINT NOT NULL,"
                                + "c_rattr_id BIGINT,"
                                + "c_sattr_id BIGINT,"
                    			+ "c_sdesc BIT,"
                    			+ "c_is_encrypt BIT,"
								+ "c_before_event_expr LONGBLOB DEFAULT NULL,"
								+ "c_after_event_expr LONGBLOB DEFAULT NULL,"
								+ "c_before_del_event_expr LONGBLOB DEFAULT NULL,"
								+ "c_after_del_event_expr LONGBLOB DEFAULT NULL,"
								+ "c_before_event_tr INTEGER DEFAULT 0 NOT NULL,"
								+ "c_after_event_tr INTEGER DEFAULT 0 NOT NULL,"
								+ "c_before_del_event_tr INTEGER DEFAULT 0 NOT NULL,"
								+ "c_after_del_event_tr INTEGER DEFAULT 0 NOT NULL,"
								+ "c_access_modifier INTEGER DEFAULT 0 NOT NULL,"
                                + "c_comment TEXT,"
								+ "PRIMARY KEY(c_id),"
								+ "FOREIGN KEY(c_class_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE,"
    							+ "FOREIGN KEY(c_type_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE,"
                                + "FOREIGN KEY(c_rattr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id),"
                                + "FOREIGN KEY(c_sattr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id),"
	    						+ "UNIQUE INDEX(c_class_id,c_name))"
								+ " ENGINE=" + ENGINE);
				st.executeUpdate("CREATE UNIQUE INDEX idx_auid ON "+getDBPrefix()+"t_attrs(c_auid)");

				st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_rattrs ("
								+ "c_attr_id BIGINT NOT NULL,"
								+ "c_rattr_id BIGINT NOT NULL,"
								+ "PRIMARY KEY(c_attr_id,c_rattr_id),"
								+ "FOREIGN KEY(c_attr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id) ON DELETE CASCADE,"
								+ "FOREIGN KEY(c_rattr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id) ON DELETE CASCADE)"
								+ " ENGINE=" + ENGINE);
				
				st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_methods ("
								+ "c_muid CHAR(36) NOT NULL,"
								+ "c_class_id BIGINT NOT NULL,"
								+ "developer BIGINT,"
								+ "c_name VARCHAR(255) NOT NULL,"
								+ "c_is_cmethod BIT NOT NULL,"
								+ "c_expr LONGBLOB,"
                                + "c_comment TEXT,"
								+ "PRIMARY KEY(c_muid),"
								+ "FOREIGN KEY(c_class_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE,"
								+ "UNIQUE INDEX(c_class_id,c_name))"
								+ " ENGINE=" + ENGINE);
				
				dbPreInit();
				
				st.executeUpdate("CREATE TABLE ct99 ("
								+ "c_obj_id BIGINT NOT NULL AUTO_INCREMENT,"
								+ "c_class_id BIGINT NOT NULL,"
								+ "c_uid CHAR(20),"
								+ "PRIMARY KEY(c_obj_id),"
								+ "UNIQUE INDEX(c_uid),"
								+ "INDEX(c_class_id),"
								+ "FOREIGN KEY(c_class_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE)"
								+ " ENGINE=" + ENGINE);
				
			
				st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_changescls ("
						+ "c_id BIGINT NOT NULL AUTO_INCREMENT,"
						+ "c_type INTEGER NOT NULL,"
						+ "c_action INTEGER NOT NULL,"
						+ "c_entity_id CHAR(36) NOT NULL,"						
						+ "c_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
						+ "c_user_id BIGINT DEFAULT NULL,"
						+ "c_ip VARCHAR(15) DEFAULT NULL,"
						+ "PRIMARY KEY(c_id))"
						+ " ENGINE=" + ENGINE);
				
				st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_changes ("
								+ "c_id BIGINT NOT NULL AUTO_INCREMENT,"
								+ "c_class_id BIGINT,"
								+ "c_object_id BIGINT NOT NULL,"
								+ "c_object_uid VARCHAR(20) DEFAULT NULL,"
								+ "c_attr_id BIGINT NOT NULL,"
								+ "c_lang_id BIGINT NOT NULL,"
								+ "c_tr_id BIGINT NOT NULL,"
								+ "c_is_repl BIT NOT NULL,"
								+ "c_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
								+ "c_user_id BIGINT DEFAULT NULL,"
								+ "c_ip VARCHAR(15) DEFAULT NULL,"
								+ "PRIMARY KEY(c_id),"
								+ "FOREIGN KEY(c_attr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id) ON DELETE CASCADE)"
								+ " ENGINE=" + ENGINE);
				st.executeUpdate("CREATE INDEX ch_tr_repl_idx"
						+ " ON "+getDBPrefix()+"t_changes(c_tr_id,c_is_repl)");
				st.executeUpdate("CREATE INDEX ch_tr_attr_idx"
						+ " ON "+getDBPrefix()+"t_changes(c_tr_id,c_attr_id)");
				st.executeUpdate("CREATE INDEX ch_tr_obj_idx"
						+ " ON "+getDBPrefix()+"t_changes(c_tr_id,c_object_id)");
				st.executeUpdate("CREATE INDEX ch_cls_id_idx"
						+ " ON "+getDBPrefix()+"t_changes(c_class_id,c_id)");
				
				st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_ids ("
						+ "c_name VARCHAR(255) NOT NULL,"
						+ "c_last_id BIGINT NOT NULL," + "PRIMARY KEY(c_name))"
						+ " ENGINE=" + ENGINE);

				PreparedStatement pst = conn
						.prepareStatement("INSERT INTO "+getDBPrefix()+"t_ids (c_name,c_last_id) VALUES (?,?)");

				// Таблица версионности объектов
				st.executeUpdate(
						"CREATE TABLE "+getDBPrefix()+"t_vcs_objects ("
						+ "c_id BIGINT NOT NULL AUTO_INCREMENT,"
						+ "c_name VARCHAR(255),"
						+ "c_obj_id BIGINT NOT NULL,"
						+ "c_obj_uid VARCHAR(20) NOT NULL,"
						+ "c_obj_class_id BIGINT NOT NULL,"
						+ "c_attr_id BIGINT NOT NULL,"
						+ "c_lang_id INTEGER NOT NULL,"
						+ "c_old_value LONGBLOB,"
						+ "c_user_id BIGINT NOT NULL,"
						+ "c_old_user_id BIGINT,"
						+ "c_rimport_id BIGINT,"
						+ "c_rexport_id BIGINT,"
						+ "c_ip VARCHAR(15) NOT NULL,"
						+ "c_mod_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
						+ "c_mod_last_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
						+ "c_mod_confirm_time DATETIME DEFAULT null,"
						+ "c_fix_start_id BIGINT,"
						+ "c_fix_end_id BIGINT,"
						+ "c_dif LONGBLOB,"
						+ "c_fix_comment TEXT,"
						+ "PRIMARY KEY (c_id)"
						+ ")");
				st.executeUpdate("CREATE UNIQUE INDEX IDX_VCS_OBJ_ATTR_LANG ON "+getDBPrefix()+"t_vcs_objects(c_obj_id,c_attr_id,c_lang_id,c_fix_end_id)");
				st.executeUpdate("CREATE INDEX IDX_VCS_OBJ_UID ON "+getDBPrefix()+"t_vcs_objects(c_obj_uid)");
				st.executeUpdate("CREATE INDEX IDX_VCS_OBJ_REXP ON "+getDBPrefix()+"t_vcs_objects(c_rexport_id)");
				st.executeUpdate("CREATE INDEX IDX_VCS_OBJ_RIMP ON "+getDBPrefix()+"t_vcs_objects(c_rimport_id)");
				st.executeUpdate("CREATE INDEX IDX_VCS_OBJ_USER ON "+getDBPrefix()+"t_vcs_objects(c_user_id)");
				st.executeUpdate("CREATE INDEX IDX_VCS_OBJ_OLD_USER ON "+getDBPrefix()+"t_vcs_objects(c_old_user_id)");
	

				// Таблица версионности модели
				st.executeUpdate(
						"CREATE TABLE "+getDBPrefix()+"t_vcs_model ("
						+ "c_id BIGINT NOT NULL AUTO_INCREMENT,"
						+ "c_name VARCHAR(255),"
						+ "c_entity_id VARCHAR(36) NOT NULL,"
						+ "c_type INTEGER NOT NULL,"
						+ "c_action INTEGER NOT NULL,"
						+ "c_old_value LONGBLOB,"
						+ "c_user_id BIGINT NOT NULL,"
						+ "c_old_user_id BIGINT,"
						+ "c_rimport_id BIGINT,"
						+ "c_rexport_id BIGINT,"
						+ "c_ip VARCHAR(15) NOT NULL,"
						+ "c_mod_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
						+ "c_mod_last_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
						+ "c_mod_confirm_time DATETIME DEFAULT null,"
						+ "c_fix_start_id BIGINT,"
						+ "c_fix_end_id BIGINT,"
						+ "c_dif LONGBLOB,"
						+ "c_fix_comment TEXT,"
						+ "PRIMARY KEY (c_id)"
						+ ")");
				st.executeUpdate("CREATE INDEX IDX_VCS_MODEL_ENTITY_TYPE_ACTION ON "+getDBPrefix()+"t_vcs_model(c_entity_id,c_type,c_action,c_fix_end_id)");
				st.executeUpdate("CREATE INDEX IDX_VCS_MODEL_REXP ON "+getDBPrefix()+"t_vcs_model(c_rexport_id)");
				st.executeUpdate("CREATE INDEX IDX_VCS_MODEL_RIMP ON "+getDBPrefix()+"t_vcs_model(c_rimport_id)");
				st.executeUpdate("CREATE INDEX IDX_VCS_MODEL_USER ON "+getDBPrefix()+"t_vcs_model(c_user_id)");
				st.executeUpdate("CREATE INDEX IDX_VCS_MODEL_OLD_USER  ON "+getDBPrefix()+"t_vcs_model(c_old_user_id)");

				// Таблица логирования
				st.executeUpdate("CREATE TABLE " + getDBPrefix() + "t_syslog ("
				 + "c_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
				 + "c_logger VARCHAR(50) NOT NULL,"
				 + "c_type VARCHAR(20) NOT NULL,"
				 + "c_action VARCHAR(50) NOT NULL,"
				 + "c_user VARCHAR(20) NOT NULL,"
				 + "c_ip  VARCHAR(15) NOT NULL,"
				 + "c_host VARCHAR(20) NOT NULL,"
				 + "c_admin BIT NOT NULL,"
				 + "c_server_id VARCHAR(20),"
				 + "c_object VARCHAR(255),"
				 + "c_tab_name VARCHAR(255),"
				 + "c_col_name VARCHAR(255),"
				 + "c_thread VARCHAR(1024),"
				 + "c_process VARCHAR(2000),"
				 + "c_message VARCHAR(2000)"
				 + ")");

				 // Таблица блокировок объектов
				createLocksTable(conn);
				
				// Создаем таблицу блокировок для методов
				createLockMethodsTable(conn);

				//Таблицы для хранение многоатрибутных индексов
				st.executeUpdate("" +
					"CREATE TABLE "+getDBPrefix()+"t_indexes(" +
					" c_id  BIGINT NOT NULL AUTO_INCREMENT," +
					" c_uid VARCHAR(36) NOT NULL," +
					" c_class_id BIGINT NOT NULL," +
					" c_is_multilingual BIT NOT NULL," +
					" PRIMARY KEY(c_id)," +
					" FOREIGN KEY(c_class_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE" +
					")" +
					"");
				
				st.executeUpdate("" +
					"CREATE TABLE "+getDBPrefix()+"t_indexkeys(" +
					" c_index_id BIGINT NOT NULL," +
					" c_attr_id BIGINT NOT NULL," +
					" c_keyno BIGINT NOT NULL," +
					" c_is_desc BIT NOT NULL," +
					" FOREIGN KEY(c_index_id) REFERENCES "+getDBPrefix()+"t_indexes(c_id) ON DELETE CASCADE," +
					" FOREIGN KEY(c_attr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id) ON DELETE CASCADE" +
					")" +
					"");

				st.executeUpdate("" +
						"CREATE TABLE "+getDBPrefix()+"t_msg(" +
						" c_msg VARCHAR(255))" +
						"");
				// Создание базы с нуля или импорт данных из существующей базы
				String dbImportDir = System.getProperty("dbImportDir");
				if (dbImportDir == null) {
					long db_id = dbInit();
					st.close();
					// Идентификатор БД
					pst.setString(1, "dbase_id");
					pst.setLong(2, db_id);
					pst.executeUpdate();
                    // Текущая транзакция
                    pst.setString(1, "transaction_id");
                    pst.setLong(2, 1);
                    pst.executeUpdate();
					// Версия БД
					pst.setString(1, "version");
					pst.setLong(2, 59);
					pst.executeUpdate();
					// 
					pst.setString(1, "installed");
					pst.setLong(2, 1);
					pst.executeUpdate();
					pst.close();
                } else {
                    String slc = System.getProperty("sysLangCount");
                    sysLangCount = slc == null ? 2 : Integer.parseInt(slc);
                    dbImport(dbImportDir, System.getProperty("separator"));
                }
				//
		        isInstallDb=false;
		        isUpgrading = false;
			} else if (is_del_) {
				Statement stt = conn.createStatement();
				set = st
						.executeQuery("select c_class_id,c_id,c_col_type,c_is_multilingual from "+getDBPrefix()+"t_attrs where c_col_type>0 or c_is_multilingual >0");
				while (set.next()) {
					String t_name = kz.tamur.or3.util.Tname.getAttrTableName(set.getLong("c_class_id"), set.getLong("c_id"), conn);
					stt.executeUpdate("ALTER TABLE " + t_name
							+ " MODIFY c_del BIGINT DEFAULT 0");
				}
				set = st
						.executeQuery("select c_class_id,c_id,c_col_type,c_is_multilingual from "+getDBPrefix()+"t_attrs where c_type_id=6");
				while (set.next()) {
					// Обновление MEMO с BLOB на TEXT
					String cmName = kz.tamur.or3.util.Tname.getColumnName(set.getLong("c_id"), conn);
					if (set.getLong("c_is_multilingual") == 1
							|| set.getLong("c_col_type") > 0) {
						String atName = kz.tamur.or3.util.Tname.getAttrTableName(set.getLong("c_class_id"), set.getLong("c_id"), conn);
						stt.executeUpdate("ALTER TABLE " + atName + " MODIFY "
								+ cmName + " TEXT");
					} else {
						String ctName = getClassTableName(set.getLong("c_class_id"));
						stt.executeUpdate("ALTER TABLE " + ctName + " MODIFY "
								+ cmName + " TEXT");
					}
				}

				set.close();
				stt.close();
				
		        isUpgrading = false;
				version = 12;
			}
			
			st.close();
			commit();
		} catch (SQLException e) {
			throw convertException(e);
		}
	}
  
	protected String getChildClasses(long clsId) throws DriverException{
		List<KrnClass> clss = new ArrayList<KrnClass>();
		clss.add(db.getClassById(clsId));
        db.getSubClasses(clsId,true, clss);
        String res="";
        for(KrnClass cls:clss){
            res += (res.equals("")?(""+cls.id):(","+cls.id));
        }
        res="("+res+")";
        return res;
    }
	
	protected String getIndexName(long ndxId){
		return "ndx" + ndxId;
	}
	
	protected String getIndexName(long ndxId, long langId){
		return getIndexName(ndxId) + ((langId > 0) ? "_" + langId : "");
	}

	protected String getInsertTriggerName(long clsId) {
		return "tg_ins_ct" + clsId;
	}
	
	protected String getUpdateTriggerName(long clsId) {
		return "tg_upd_ct" + clsId;
	}
	
    protected String getRevAttrTableName(KrnAttribute attr) {
    	KrnAttribute rattr = db.getAttributeById(attr.rAttrId);
        return getAttrTableName(rattr);
    }
//    
//	protected String getAttrTableName(long classId, long attrId) {
//		return getAttrTableName(classId, attrId, conn);
//	}

//	protected String getFkName(long classId, long attrId) {
//		return "at" + classId + "_" + attrId + "_FK";
//	}

    protected String getRevColumnName(KrnAttribute attr) {
    	KrnAttribute rattr = db.getAttributeById(attr.rAttrId);
        return getColumnName(rattr);
    }

	protected String getRevIndexColumnName(long attrId) {
		return "cmi" + attrId;
	}

	public void logModelChanges(int type, int action, String entityUid) throws DriverException {
        try {
            logModelChanges(type, action, entityUid, conn);
        } catch (SQLException e) {
			throw convertException(e);
        }
    }
	
	public void logModelChanges(int type, int action, String entityUid,
			Connection conn) throws SQLException {

    	if (version >= 31/* && ENTITY_TYPE_METHOD==type*/) {
    		return;
    	}
		QueryRunner qr = new QueryRunner(true);

		// Создаем строку журнала
		String sql = "INSERT INTO "+getDBPrefix()+"t_changescls (c_type,c_action,c_entity_id) VALUES (?,?,?)";
		Object[] params = { type, action, Funcs.sanitizeSQL(entityUid) };
		qr.update(conn, sql, params);
	}

    protected void logDataChanges(KrnObject obj, long attrId, long langId, long trId) throws SQLException, DriverException {
        if (isUpgrading) {
            return;
        }
        fireAttrChanged(obj, attrId, langId, trId);
        if (trId == 0) {
        	db.addProtocolRule(obj, attrId, this);
        	if (this instanceof OracleDriver3) {
        		db.addFGACRule(obj, attrId, this);
        		db.addFGARule(obj, attrId, this);
        	}
        }
    	db.addSystemRight(obj, attrId, this, trId);
    	
    	if (trId==0 && version >= 31 && db.isVcsClass(obj.classId)) {
    		return;
    	}

        boolean isRepl = db.getClassById(obj.classId).isRepl
                && (attrId == 1 || attrId == 2 || db.getAttributeById(attrId).isRepl);
        if (trId != 0 || (dataLog && isRepl)) {
            // Создаем строку журнала
        	if (dataLogPst == null) {
        		String sql = "INSERT INTO " + getDBPrefix() + "t_changes (c_class_id,c_object_id,c_object_uid,c_attr_id," +
            		"c_lang_id,c_tr_id,c_is_repl) VALUES (?,?,?,?,?,?,?)";
        		dataLogPst = conn.prepareStatement(sql);
        	}
        	dataLogPst.setLong(1, obj.classId);
        	dataLogPst.setLong(2, obj.id);
        	dataLogPst.setString(3, Funcs.sanitizeSQL(obj.uid));
        	dataLogPst.setLong(4, attrId);
        	dataLogPst.setLong(5, langId);
        	dataLogPst.setLong(6, trId);
        	dataLogPst.setBoolean(7, isRepl);
        	dataLogPst.addBatch();
            
        	if (++dataLogBatchCount > 1000)
        		saveDataLog();
        }
    }
    
    protected void saveDataLog() throws SQLException {
    	if (dataLogBatchCount > 0) {
    		dataLogPst.executeBatch();
    		dataLogBatchCount = 0;
    	}
    }

	protected void commitDataLog(long trId) throws SQLException {
		if (dataLog) {
			PreparedStatement pst = conn.prepareStatement(
					"UPDATE "+getDBPrefix()+"t_changes SET c_tr_id=0 WHERE c_tr_id=? AND c_is_repl=?");
			pst.setLong(1, trId);
			pst.setBoolean(2, true);
			pst.executeUpdate();
			pst.close();
			QueryRunner qr = new QueryRunner(true);
			qr.update(conn, "DELETE FROM "+getDBPrefix()+"t_changes WHERE c_tr_id=?", trId);
		} else {
			QueryRunner qr = new QueryRunner(true);
			qr.update(conn, "DELETE FROM "+getDBPrefix()+"t_changes WHERE c_tr_id=?", trId);
		}
	}

	protected void rollbackDataLog(long trId) throws SQLException {
		QueryRunner qr = new QueryRunner(true);
		qr.update(conn, "DELETE FROM "+getDBPrefix()+"t_changes WHERE c_tr_id=?", trId);
		
		Map<Long, List<AttrChangeListener>> map = getAllListeners();
		if (map != null && map.size() > 0) {
			for (Long classId : map.keySet()) {
				List<AttrChangeListener> list = map.get(classId);
				for (AttrChangeListener l : list)
					l.rollbackLongTransaction(us.getId(), trId);
			}
			if (UserSession.SERVER_ID != null)
    			ServerMessage.sendMessage(UserSession.SERVER_ID, us.getDsName(), us.getId(), new AttrChange(null, -4, 0, trId));
		}

	}

    protected void addReverseSQL(String sql) {
        if (isUpgrading) {
            return;
        }
        // Создаем строку журнала
    	reverseSQLs.add(sql);
    }
    
	protected void clearReverseSQL() {
		reverseSQLs.clear();
	}

	protected void executeReverseSQL() throws SQLException {
		QueryRunner qr = new QueryRunner(true);
		for (String sql : reverseSQLs) {
			qr.update(conn, sql);
		}
		conn.commit();
	}

	protected long openTransaction(KrnObject obj, long trId, boolean forReading)
			throws SQLException, DriverException {
		long t = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			if (trId == -1) {
				pst = conn.prepareStatement("SELECT c_obj_id,MAX(c_tr_id) AS c_tr_id FROM "+getClassTableName(obj.classId)
						+ " WHERE c_obj_id=? GROUP BY c_obj_id");
				pst.setLong(1, obj.id);
				rs = pst.executeQuery();
				if (rs.next()) {
					t = rs.getLong("c_tr_id");
				}
				return t;
			} else {
				pst = conn.prepareStatement("SELECT c_obj_id,c_tr_id FROM "+getClassTableName(obj.classId)
						+ " WHERE c_obj_id=? AND c_tr_id IN (0,?)");
				pst.setLong(1, obj.id);
				pst.setLong(2, trId);
				rs = pst.executeQuery();
				while (rs.next()) {
					t = rs.getLong("c_tr_id");
					if (t == trId) {
						break;
					}
				}
			}
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
		}

		// Если транзакция для объекта не сушествует
		// и не установлен флаг "для чтения", то создаем ее
		if (t != trId && !forReading) {
			t = createTransaction(obj, t, trId);
		}
		return t;
	}

	protected long createTransaction(KrnObject obj, long srcTrId, long trId)
			throws SQLException, DriverException {
		// Копируем значения
		copyValues(obj, srcTrId, obj, trId, false);

		return trId;
	}

	protected void copyValues(KrnObject srcObj, long srcTrId, KrnObject dstObj,
			long dstTrId, boolean clone) throws DriverException, SQLException {

		QueryRunner qr = new QueryRunner(true);
		List clss = db.getSuperClasses(srcObj.classId);
		try {
			for (Object cls1 : clss) {
				KrnClass cls = (KrnClass) cls1;
				if (cls.id != ROOT_CLASS_ID && !cls.isVirtual()) {
					List<KrnAttribute> attrs = db.getAttributesByClassId(cls.id, false);
					StringBuilder colList = new StringBuilder("c_class_id");
					for (KrnAttribute attr : attrs) {
						if ((!clone || (attr.flags & 0x02) == 0)
								&& attr.rAttrId == 0
								&& attr.collectionType == COLLECTION_NONE) {
							if (attr.isMultilingual) {
								List<KrnObject> sysLangs = getSystemLangs();
								for (KrnObject lang : sysLangs) {
									colList.append(",");
									colList.append(getColumnName(attr, lang.id));
								}
							} else {
								colList.append(",");
								colList.append(getColumnName(attr));
							}
						}
					}
					String ctName = getClassTableName(cls.id);
					try {
						qr.update(conn, "INSERT INTO " + ctName
								+ "(c_obj_id,c_uid,c_tr_id," + colList
								+ ") SELECT ? AS c_obj_id,? AS c_uid,"
								+ "? AS c_tr_id," + colList + " FROM " + ctName
								+ " WHERE c_obj_id=? AND c_tr_id=?",
								new Object[] { dstObj.id, dstObj.uid, dstTrId,
										srcObj.id, srcTrId });
					} catch (SQLException e) {
						throw e;
					} catch (Throwable e) {
						log.error(e, e);
						throw new SQLException("ERROR: " + e.getMessage());
					}
					for (KrnAttribute attr : attrs) {
						if ((!clone || (attr.flags & 0x02) == 0)
								&& attr.rAttrId == 0
								&& (attr.collectionType == COLLECTION_ARRAY
										|| attr.collectionType == COLLECTION_SET)) {
							StringBuffer colNames = new StringBuffer();
							if (attr.isMultilingual) {
								List<KrnObject> sysLangs = getSystemLangs();
								for (int i = 0; i < sysLangs.size(); i++) {
									if (i > 0)
										colNames.append(",");
									colNames.append(getColumnName(attr, sysLangs.get(i).id));
								}
							} else {
								colNames.append(getColumnName(attr));
							}
							if (attr.collectionType == COLLECTION_ARRAY) {
								colNames.append(",c_index");
								colNames.append(",c_id");
							}
							if (attr.collectionType == COLLECTION_SET) {
								colNames.append(",c_del");
							}
							String tname = getAttrTableName(attr);
							qr.update(conn, "INSERT INTO " + tname
									+ " (c_obj_id,c_tr_id," + colNames
									+ ") SELECT ? AS c_obj_id,? as c_tr_id,"
									+ colNames + " FROM " + tname
									+ " WHERE c_obj_id=? AND c_tr_id=?",
									new Object[] { dstObj.id, dstTrId,
											srcObj.id, srcTrId });
						}
					}
				}
			}
		} catch (SQLException e) {
			throw convertException(e);
		}
	}
	
	protected Object getValue(ResultSet rs, String cname, long typeId)
			throws SQLException {
		cname = Funcs.normalizeInput(cname);
		
		if (typeId == PC_STRING) {
			return getString(rs, cname);
		} else if (typeId == PC_BOOL) {
			return rs.getBoolean(cname);
		} else if (typeId == PC_INTEGER) {
			long v = rs.getLong(cname);
			return rs.wasNull() ? null : v;
		} else if (typeId == PC_DATE) {
			return rs.getDate(cname);
		} else if (typeId == PC_TIME) {
			return rs.getTimestamp(cname);
		} else if (typeId == PC_FLOAT) {
			double v = rs.getDouble(cname);
			return rs.wasNull() ? null : v;
		} else if (typeId == PC_MEMO) {
			return getMemo(rs, cname);
		} else if (typeId == PC_BLOB) {
			try {
				InputStream is = rs.getBinaryStream(cname);
				if (is != null) {
					byte[] res = Funcs.readStream(is, Constants.MAX_BLOB_SIZE);
					is.close();
					return res;
				}
				return null;
			} catch (Throwable e) {
				log.error(e, e);
				throw new SQLException("Faild to read bytes");
			}
		} else if (typeId >= 99) {
			long v = rs.getLong(cname);
			return rs.wasNull() ? null : v;
		}
		return null;
	}

	protected Object getValue(ResultSet rs, int cindex, long typeId, Calendar cal)
			throws SQLException {
		if (typeId == PC_STRING) {
			return getString(rs, cindex);
		} else if (typeId == PC_BOOL) {
			return rs.getBoolean(cindex);
		} else if (typeId == PC_INTEGER) {
			long v = rs.getLong(cindex);
			return rs.wasNull() ? null : v;
		} else if (typeId == PC_DATE) {
			Date v = rs.getDate(cindex);
			return kz.tamur.util.Funcs.convertDate(v, cal);
		} else if (typeId == PC_TIME) {
			Timestamp v = rs.getTimestamp(cindex);
			return kz.tamur.util.Funcs.convertTime(v, cal);
		} else if (typeId == PC_FLOAT) {
			double v = rs.getDouble(cindex);
			return rs.wasNull() ? null : v;
		} else if (typeId == PC_MEMO) {
			return getMemo(rs, cindex);
		} else if (typeId == PC_BLOB) {
			try {
				InputStream is = rs.getBinaryStream(cindex);
				if (is != null) {
					byte[] res = Funcs.readStream(is, Constants.MAX_BLOB_SIZE);
					is.close();
					return res;
				}
				return null;
			} catch (Throwable e) {
				log.error(e, e);
				throw new SQLException("Faild to read bytes");
			}
		} else if (typeId >= 99) {
			long v = rs.getLong(cindex);
			return rs.wasNull() ? null : v;
		}
		return null;
	}

	protected void setValue(PreparedStatement pst, int colIndex, long typeId, Object value) throws SQLException {
		if (value == null) {
			pst.setNull(colIndex, getSqlType(typeId));
		} else if (typeId == PC_STRING) {
			String res = Funcs.normalizeInput((String) value);
			pst.setString(colIndex, res != null && res.length() > 255 ? res
					.substring(0, 255) : res);
		} else if (typeId == PC_BOOL) {
			if (value instanceof Number) {
				pst.setBoolean(colIndex, ((Number) value).longValue() != 0);
			} else
				pst.setBoolean(colIndex, (Boolean) value);
		} else if (typeId >= 99 || typeId == PC_INTEGER) {
			if (value instanceof KrnObject)
				pst.setLong(colIndex, ((KrnObject) value).id);
			else if (value instanceof Number)
				pst.setLong(colIndex, ((Number) value).longValue());
			else
				throw new SQLException("Trying to set non-number value to long or object attribute!");
		} else if (typeId == PC_DATE) {
			pst.setDate(colIndex, (Date) value);
		} else if (typeId == PC_TIME) {
			pst.setTimestamp(colIndex, (Timestamp) value);
		} else if (typeId == PC_FLOAT) {
			pst.setDouble(colIndex, ((Number) value).doubleValue());
		} else if (typeId == PC_MEMO) {
			String res = (String) value;
			if (res == null || res.length() < Constants.MAX_DOC_SIZE) {
				//setMemo(pst, colIndex, res);
				pst.setString(colIndex, res);
			} else
				throw new SQLException("Too long memo value (> " + Constants.MAX_DOC_SIZE + ")!");
		} else if (typeId == PC_BLOB) {
			try {
				byte[] b = Funcs.toByteArray(value);
				if (b.length < Constants.MAX_BLOB_SIZE) {
					pst.setBytes(colIndex, b);
				} else
					throw new SQLException("Too long blob value (> " + Constants.MAX_BLOB_SIZE + ")!");
			} catch (IOException e) {
				throw new SQLException(e);
			}
		}
	}

	public String getMemo(ResultSet rs, String name) throws SQLException {
		Reader reader = rs.getCharacterStream(name);
		if (reader == null)
			return null;
		CharArrayWriter writer = new CharArrayWriter();
		int i = -1;
		try {
			while ((i = reader.read()) != -1)
				writer.write(i);
		} catch (IOException e) {
			log.error(e, e);
		}
		return Funcs.normalizeInput(new String(writer.toCharArray()));
	}

	public String getMemo(ResultSet rs, int cindex) throws SQLException {
		Reader reader = rs.getCharacterStream(cindex);
		if (reader == null)
			return null;
		CharArrayWriter writer = new CharArrayWriter();
		int i = -1;
		try {
			while ((i = reader.read()) != -1)
				writer.write(i);
		} catch (IOException e) {
			log.error(e, e);
		}
		return Funcs.normalizeInput(new String(writer.toCharArray()));
	}

	protected void setMemo(PreparedStatement pst, int colIndex, String value)
			throws SQLException {
		if (value != null) {
			value = Funcs.normalizeInput(value);
			StringReader sr = new StringReader(value);
			pst.setCharacterStream(colIndex, sr, value.length());
		} else {
			pst.setNull(colIndex, getSqlType(PC_MEMO));
		}
	}

    private String getSqlOp(String cname, int op) {
        switch (op) {
        case CO_IS_NULL:
            return cname + " IS NULL";
        case CO_EQUALS:
            return cname + "=?";
        case CO_EQUALS_TRIM:
            return "TRIM(" + cname + ")=?";
        case CO_EQUALS_IGNORE_CASE:
            return "UPPER(" + cname + ")=?";
        case CO_CONTAINS:
            return "UPPER(" + cname + ") LIKE ?";
        case SEARCH_START_WITH:
            return "UPPER(" + cname + ") LIKE ?";
        default:
            return "";
        }
    }

    protected class FilterObj {
		public FilterObj() { }
		public String join = "";
		public String tablNums = "";
		public boolean isFunc;
        public boolean isJoinTable;
		public String tablNumsObj = "";
		public String joinObj = "";
		public String tipeClassJoinObj = "";
		public String sql_mean = "";
		public String sql_head = "";
		public String sql_sort = "";
		public int position = 0;
		public String join_attr = "";
		public Map<String, FilterObj> tMap = null;
		public FilterObj root;
		public String nodeMaxInd = "";
		public String nodeIndepend = "";
		public String union = "";
		public String groupFieldName = "";
		public String groupNumTable="";
		public String groupFunc = "";
		public String groupClassType = "";
		public int groupCount = 0;
		public List<String> tName = null;
	}

//EXISTS
	private void createSqlUnionExists(long filterId, KrnClass clsFlr, Map<String, String> sqlParMap, StringBuffer sb_sql, Element root) {
		List<Element> minus_=new ArrayList<Element>();
		List<Element> al = root.getChild("children") != null ? root
				.getChild("children").getContent() : null;
		String num_1 = getStringParam(root, "compFlr");
		int dataSelect_ = num_1.equals("") ? 0 : Integer.valueOf(num_1);
		if (dataSelect_ == 3) {
			String rightStr_ = getStringParam(root.getChild("valFlr"), "exprFlr");
			sqlParMap.put(rightStr_.trim(), "o.c_uid");
		}
		if(al.size()>1 && !(getStringParam(root, "unionFlr").equals("1"))){
			for(Element e:al){
				if(getStringParam(e, "inversFlr").equals("true")){
					minus_.add(e);		
				}else{
					StringBuffer e_sql=new StringBuffer();
					createSqlHeadExists(filterId, clsFlr, sqlParMap, e_sql, e,true,minus_);
					if(sb_sql.length()>0){
						sb_sql.append("\n UNION \n");
					}
					if(e_sql.length()>0){
						sb_sql.append("(");
						sb_sql.append(e_sql);
						sb_sql.append(")");
					}
				}
			}
		}else{
			createSqlHeadExists(filterId, clsFlr, sqlParMap, sb_sql, root,false,minus_);
		}
		if(getStringParam(root, "inversFlr").equals("true")){
			String num_ = getStringParam(root, "transFlr");
			int trans_ = num_.equals("") ? 0 : Integer.valueOf(num_);
			String sql_mean = " WHERE c_is_del=0" + (trans_ >= 1 ? "" : " AND c_tr_id=0");
			sb_sql.insert(0,"SELECT DISTINCT c_obj_id, c_class_id, c_uid FROM " +getClassTableName(clsFlr.id)+ sql_mean+"\n MINUS \n(");
			sb_sql.append(")");
		}
		if(minus_.size()>0){
			for(Element e:minus_){
				StringBuffer e_sql=new StringBuffer();
				createSqlHeadExists(filterId, clsFlr, sqlParMap, e_sql, e,true,null);
				if(sb_sql.length()>0){
					sb_sql.append("\n MINUS \n");
				}
				if(e_sql.length()>0){
					sb_sql.append("(");
					sb_sql.append(e_sql);
					sb_sql.append(")");
				}
				
			}
		}
	}

	private void createSqlHeadExists(long filterId, KrnClass clsFlr, Map<String, String> sqlParMap, StringBuffer sb_sql, Element root,boolean isUnion,List minus_) {
		boolean exclude_ = getStringParam(root, "excludeFlr").equals("true");
		if (exclude_)return;
		FilterObj f_obj = new FilterObj();
		f_obj.root = f_obj;
		Map<String, FilterObj> tableMap = new LinkedMap();
		tableMap.put(clsFlr.getName(), f_obj);
		f_obj.tMap = tableMap;
		List al_ = root.getChild("children") != null ? root
				.getChild("children").getContent() : null;
		if (clsFlr == null)
			return;
		f_obj.sql_head = "SELECT DISTINCT o.c_obj_id, o.c_class_id, o.c_uid";
		f_obj.join = getClassTableName(clsFlr.id) + " o ";
		f_obj.tablNums = "0";
		String oper = getStringParam(root, "operFlr");
		String rObj = getStringParam(root.getChild("valFlr"), "krnObjFlr");
		String num_ = getStringParam(root, "compFlr");
		int dataSelect_ = num_.equals("") ? 0 : Integer.valueOf(num_);
		if (!isUnion && oper != null && !oper.equals("")
                && (dataSelect_ == 1 || (rObj != null && !rObj.equals("")))) {
			String sql_min_ = getSqlObject(root, "o.c_uid");
			if (dataSelect_ == 1) {
				sql_min_ = "{0 " + sql_min_ + " }0";
			}
			f_obj.sql_mean = "WHERE o.c_is_del=0 AND " + sql_min_;
		} else
			f_obj.sql_mean = "WHERE o.c_is_del=0";
		num_ = getStringParam(root, "transFlr");
		int trans_ = num_.equals("") ? 0 : Integer.valueOf(num_);
		if (trans_ == 2) {
			f_obj.join = " (SELECT c_is_del,c_obj_id, max(c_tr_id) c_tm_id FROM "
					+ getClassTableName(clsFlr.id)
					+ " WHERE c_is_del=0 AND c_tr_id in (0,_CURTRANS) GROUP BY  c_is_del,c_obj_id) trm LEFT JOIN "
					+ getClassTableName(clsFlr.id)
					+ " o ON o.c_is_del=trm.c_is_del AND o.c_obj_id=trm.c_obj_id AND o.c_tr_id=trm.c_tm_id ";
		} else {
			f_obj.join = getClassTableName(clsFlr.id) + " o ";
		}
		f_obj.sql_mean += (trans_ >= 1 ? "" : " AND o.c_tr_id=0");
		f_obj.sql_mean += (al_ == null || isUnion || al_.size() > 0) ? " AND " : "";
		t_ind = 0;
		//
		String union_ = getStringParam(root, "unionFlr").equals("1") ? " AND "
				: " OR ";
		String operator_ = getStringParam(root, "operFlr");
		createExistsTable(filterId, clsFlr, sqlParMap, root, false, f_obj, trans_,minus_);
		// Построение sql для детей
		if (al_ != null && al_.size() > 0) {
			if (al_.size() > 1) {
				// Открывающая скобка для объединения
				f_obj.sql_mean += "(";
			} else if (!operator_.equals("0") && !operator_.equals(""))
				f_obj.sql_mean += " AND ";
			for (int i = 0; i < al_.size(); ++i) {
				int index_union = f_obj.sql_mean.length();
				if (i > 0
						&& !f_obj.sql_mean.substring(
								f_obj.sql_mean.length() - 1).equals("("))
					f_obj.sql_mean += union_;
				Element e_i = (Element) al_.get(i);
				createSqlExists(filterId, clsFlr, sqlParMap, e_i, f_obj, trans_,minus_);
				if (f_obj.sql_mean.length() == index_union + union_.length()) {
					f_obj.sql_mean = f_obj.sql_mean.substring(0, index_union);
				}
			}
			// Закрывающая скобка для объединения
			if (al_.size() > 1) {
				if (f_obj.sql_mean.substring(f_obj.sql_mean.length() - 1)
						.equals("("))
					f_obj.sql_mean = f_obj.sql_mean.substring(0, f_obj.sql_mean
							.length() - 2);
				else
					f_obj.sql_mean += ")";
			}
		}
		//
		// Сортировка
		f_obj.sql_sort = "";
		List sorts = root.getChildren("Sort");
		if (sorts != null && sorts.size() > 0) {
			for (Object sort : sorts) {
				createExistsTable(filterId, clsFlr, sqlParMap, (Element) sort, true, f_obj, trans_,minus_);
			}
		} else {
			List childs = root.getChild("children") != null ? root.getChild(
					"children").getContent() : null;
			if (childs != null) {
				for (Object child : childs) {
					Element e_i = (Element) child;
					String oper_ = getStringParam(e_i, "operFlr");
					if (oper_.equals("" + Constants.OPER_DESCEND)
							|| oper_.equals("" + Constants.OPER_ASCEND)) {
						createExistsTable(filterId, clsFlr, sqlParMap, e_i, true, f_obj, trans_,minus_);
					}
				}
			}
		}
		if (!f_obj.sql_sort.equals("")) {
			f_obj.sql_sort = " ORDER BY " + f_obj.sql_sort;
		}
		// Построение SQL
		int i = 0;
		for (FilterObj f : f_obj.tMap.values()) {
			String join = f.join;
			String join_obj = f.joinObj;
			String join_mean = f.sql_mean;
			if (join.equals(""))
				continue;
			int last_ind = f.tablNums.lastIndexOf(",");
			if (f.isFunc) {
				sb_sql.append("{");
				sb_sql.append(f.tablNums.substring(last_ind + 1));
			}
			if (!f.equals(f_obj) && !f.sql_mean.equals("")) {
				if (f.position > 0) {
					int j = 0;
					String join_ = "";
					for (FilterObj f_ : f.tMap.values()) {
						if (j > i)
							join_ += f_.join + f_.joinObj;
						++j;
					}
					String pos_str=join.substring(f.position,f.position + 7);
					join = join.substring(0, f.position) + " " + join_
							+ f.joinObj + " WHERE " + f.sql_mean + " AND "
							+ join.substring(f.position + (pos_str.equals(" WHERE ")?7:0));
					sb_sql.append(join);
				} else {
//					if (f_obj.sql_mean.lastIndexOf("AND ") == f_obj.sql_mean
//							.length() - 4)
//						f_obj.sql_mean += f.sql_mean;
//					else
//						f_obj.sql_mean += f.union + f.sql_mean;
					sb_sql.append(join);
					sb_sql.append(join_obj);
					int i1=0;
					int i2=0;
					while ((i2=join.indexOf("(",i1))>0){
						i1=i2+1;
						f.sql_mean +=")";
					}
					sb_sql.append(" AND "+f.sql_mean);
				}
			} else {
				sb_sql.append(join);
				sb_sql.append(join_obj);
				int i1=0;
				int i2=0;
				while ((i2=join.indexOf("(",i1))>0){
					i1=i2+1;
					join_mean +=")";
				}
				sb_sql.append(join_mean);
			}
			if (f.isFunc) {
				sb_sql.append("}");
				sb_sql.append(f.tablNums.substring(last_ind + 1));
			}
			++i;
		}
		f_obj.sql_head += " FROM ";
		sb_sql.insert(0, f_obj.sql_head);
		if (f_obj.sql_mean.lastIndexOf("AND ") == f_obj.sql_mean.length() - 4)
			f_obj.sql_mean = f_obj.sql_mean.substring(0, f_obj.sql_mean
					.length() - 4);
//		sb_sql.append(f_obj.sql_mean);
		sb_sql.append(f_obj.sql_sort);

	}
	private void createSqlExists(long filterId, KrnClass clsFlr, Map<String, String> sqlParMap, Element e, FilterObj f_obj, long trans,List minus_) {
		List al_ = e.getChild("children") != null ? e.getChild("children")
				.getContent() : null;
		//
		String union_ = getStringParam(e, "unionFlr").equals("1") ? " AND "
				: " OR ";
		String left_ = getStringParam(e, "attrFlr");
		String operator_ = getStringParam(e, "operFlr");
		boolean exclude_ = getStringParam(e, "excludeFlr").equals("true");
		if (exclude_)
			return;
		else if(minus_!=null && getStringParam(e, "inversFlr").equals("true")){
			minus_.add(e);		
			return;
		}
		FilterObj f_obj_ = createExistsTable(filterId, clsFlr, sqlParMap, e, false, f_obj, trans,minus_);
		String num_ = getStringParam(e, "transFlr");
		int trans_ = num_.equals("") ? 0 : Integer.valueOf(num_);
		if (trans_ > 0)
			trans = trans_;
		// Построение sql для детей
		if (al_ != null && al_.size() > 0) {
			if (al_.size() > 1) {
				// Открывающая скобка для объединения
				if (!left_.equals("") && !operator_.equals("0")
						&& !operator_.equals(""))
					f_obj.sql_mean += " AND (";
                else if(f_obj_.isJoinTable)
                    f_obj_.sql_mean += union_+"(";
				else
					f_obj_.sql_mean += "(";
			} else if (!operator_.equals("0") && !operator_.equals(""))
				f_obj_.sql_mean += " AND ";
			for (int i = 0; i < al_.size(); ++i) {
				int index_union = f_obj_.sql_mean.length();
				if (i > 0
						&& !f_obj_.sql_mean.substring(
								f_obj_.sql_mean.length() - 1).equals("("))
					f_obj_.sql_mean += union_;
				Element e_i = (Element) al_.get(i);
				createSqlExists(filterId, clsFlr, sqlParMap, e_i, f_obj_, trans,minus_);
				if (f_obj_.sql_mean.length() == index_union
						+ union_.length()) {
					f_obj_.sql_mean = f_obj_.sql_mean.substring(0,
							index_union);
				}
			}
			// Закрывающая скобка для объединения
			if (al_.size() > 1) {
				if (f_obj_.sql_mean.substring(
						f_obj_.sql_mean.length() - 1).equals("("))
					f_obj_.sql_mean = f_obj_.sql_mean.substring(0,
							f_obj_.root.sql_mean.length() - 2);
				else
					f_obj_.sql_mean += ")";
			}
		}
		// Возвращаем старый корень
		f_obj_.root = f_obj.root;
		//
	}
	private FilterObj createExistsTable(long filterId, KrnClass clsFlr, Map<String, String> sqlParMap, Element e, boolean isSort,
			FilterObj f_obj, long trans,List minus_) {
		boolean maxIndpar = false;
		boolean independ = false;
		String left_ = getStringParam(e, "attrFlr");
		String union_ = getStringParam(e, "unionFlr").equals("1") ? " AND "
				: " OR ";
		String operator_ = getStringParam(e, "operFlr");
		String operator = operMap.get(operator_);
		String order_ = getStringParam(e, "order");
		String sql_mean = "";
		boolean exclude_ = getStringParam(e, "excludeFlr").equals("true");
		if (exclude_) {
			f_obj.sql_mean += " 1=1";
			return f_obj;
		}else if(minus_!=null && getStringParam(e, "inversFlr").equals("true")){
				minus_.add(e);		
				return f_obj;
		}
		if (operator_.equals("" + Constants.OPER_DESCEND)
				|| operator_.equals("" + Constants.OPER_ASCEND)) {
			if (isSort) {
				order_ = operator_.equals("" + Constants.OPER_DESCEND) ? "DESC"
						: "ASC";
			} else
				return f_obj;
		}
		String num_ = getStringParam(e, "compFlr");
		int dataSelect_ = num_.equals("") ? 0 : Integer.valueOf(num_);
        String linkPar_ = getStringParam(e, "linkPar");
		boolean maxind_ = getStringParam(e, "maxIndFlr").equals("true");
		boolean independ_ = getStringParam(e, "independFlr").equals("true");
		boolean relative_ = getStringParam(e, "relativeFlr").equals("true");
		String kolOp_ = getStringParam(e, "kolOperFlr");
		String kolObj_ = getStringParam(e, "kolObjFlr");
		num_ = getStringParam(e, "transFlr");
		int trans_ = num_.equals("") ? 0 : Integer.valueOf(num_);
//		if (trans_ > 0)
			trans = trans_;
		String language_ = getStringParam(e, "language");
		String language = "";
        boolean joinCls_ = getStringParam(e, "joinCls").equals("true");
        if(joinCls_){
            try{
                String parentAttr=  getStringParam(e, "attrParent");
                String opJoin_ = getStringParam(e, "operJoin");
                String opJoin = operMap.get(opJoin_);
                String childAttr=  getStringParam(e, "attrChild");
                String pAttr="",cAttr="",joinTabl="";
                String jCls=childAttr.substring(0,childAttr.indexOf("."));
                PathElement2[] path_parent = parentAttr.equals("") ? new PathElement2[0]
                        : Toolkit.parsePath2(parentAttr,this);
                if (path_parent != null && path_parent.length > 1) {
                    PathElement2[] attr_parent=new PathElement2[path_parent.length-1];
                    System.arraycopy(path_parent,1,attr_parent,0,attr_parent.length);
                    pAttr=getColumnName(attr_parent[attr_parent.length-1].attr);
                }
                PathElement2[] path_child = childAttr.equals("") ? new PathElement2[0]
                        : Toolkit.parsePath2(childAttr,this);
                if (path_parent != null && path_child.length > 1) {
                    PathElement2[] attr_child=new PathElement2[path_child.length-1];
                    System.arraycopy(path_child,1,attr_child,0,attr_child.length);
                    cAttr=getColumnName(attr_child[attr_child.length-1].attr);
                    joinTabl=getClassTableName(attr_child[attr_child.length-1].attr.classId);

                }
                int last_ind = f_obj.tablNums.lastIndexOf(",");
                String num_table = f_obj.tablNums.substring(last_ind + 1);
                String parent_table="o";
                if (!num_table.equals("0"))
                    parent_table = "t" + num_table;
				else
					parent_table="o";
                t_ind++;
                String str_exists = " EXISTS ( SELECT 1 FROM "
                        + joinTabl
                        + " t"
                        + t_ind
                        + " WHERE "+parent_table+"."
                        + pAttr
                        + opJoin
                        + "t"
                        + t_ind
                        + "."+cAttr+" "
                        + (trans == 0 ? "AND t" + t_ind
                                + ".c_tr_id=0 " : "");
                FilterObj f = new FilterObj();
                f.join = str_exists;
                f.tablNums = "" + t_ind;
                num_table = "" + t_ind;
                f.isFunc = dataSelect_ == 1 ||!"".equals(linkPar_);
                f.isJoinTable=true;
                f.root = f;
                f.root.tMap = new LinkedMap();
                f.root.tMap.put(jCls,f);
                f_obj.root.tMap.put(""+t_ind,f);
                return f;
            } catch (Exception ex) {
                // Игнорировать корявый фильтр
                log.warn("ОШИБКА: фильтр id = " + filterId + "  left=" + left_
                        + " " + ex.getMessage());
    			log.error(ex, ex);
            }
        }
        if (!language_.equals("")) {
			int m = 0;
			while (true) {
				if (language_.indexOf(",", m) > 0) {
					String val = language_.substring(m, language_.indexOf(",",
							m));
					try {
						KrnObject obj = getObjectByUid(val, 0);
						language += "" + obj.id;
						language += (m = language_.indexOf(",", m) + 1) > 0 ? ","
								: "";
					} catch (Exception ex) {
						log.error(ex, ex);
					}
				} else {
					String val = language_.substring(m);
					try {
						KrnObject obj = getObjectByUid(val, 0);
						language += "" + obj.id;
					} catch (Exception ex) {
						log.error(ex, ex);
					}
					break;
				}
			}

		} else {
			language = "LANGUAGE";
		}
		if (!left_.equals("")) {
			if (f_obj.root.tMap == null)
				f_obj.root.tMap = new LinkedMap();
			Map<String, FilterObj> tMap = f_obj.root.tMap;
			try {
				// Путь к фильтруемой переменной
                PathElement2[] path_a = left_.equals("") ? new PathElement2[0]
						: Toolkit.parsePath2(left_,this);
				if (path_a != null && path_a.length > 1) {
                    PathElement2[] attr_a=new PathElement2[path_a.length-1];
                    System.arraycopy(path_a,1,attr_a,0,attr_a.length);
                    if ((maxind_ || !kolObj_.equals(""))
                            && attr_a[attr_a.length - 1].attr.collectionType > 0) {
                        left_ += "#";
                        maxIndpar = true;
                    } else if (!f_obj.nodeMaxInd.equals("")) {
                        left_ = f_obj.nodeMaxInd
                                + left_.substring(f_obj.nodeMaxInd.length() - 1);
                    }
                    //Построение независимых узлов
                    if (independ_ && attr_a[attr_a.length - 1].attr.collectionType > 0) {
                    	int i=0;
                    	while(true){
                    		String left_0 = left_+"_"+i;
                    		if(!tMap.containsKey(left_0)){
                                left_ += "_"+i;
                    			break;
                    		}
                    		i++;
                    	}
                        independ = true;
                    } else if (!f_obj.nodeIndepend.equals("")) {
                        left_ = f_obj.nodeIndepend
                                + left_.substring(f_obj.nodeIndepend.length() - 2);
                    }

					// Построение sql для атрибута
					int ind_dot = left_.indexOf(".");
					String str_attr = left_.substring(0,
							(ind_dot > -1 ? ind_dot : left_.length()));
					int i = 0;
					FilterObj f = null;
					int ind_dot_ = ind_dot;
					while (ind_dot_ > -1) {
						if (!tMap.containsKey(str_attr)) {// Поиск
							// присоединенных
							// таблиц стаким же
							// путем
							i--;
							ind_dot--;
							break;
						}
						i++;
						f = tMap.get(str_attr);
						if (dataSelect_ == 0 && "".equals(linkPar_) && f.isFunc)// Удаление
							// принадлежности к
							// функции
							f.isFunc = false; // если присоединяется таблица
						// без функции
						ind_dot_ = left_.indexOf(".", ind_dot_ + 1);
						if (ind_dot_ < 0)
							i--;
						else
							ind_dot = ind_dot_;
						str_attr = left_.substring(0, (ind_dot_ > -1 ? ind_dot_
								: left_.length()));
						if (ind_dot_ < 0 && tMap.containsKey(str_attr)) {
							f = tMap.get(str_attr);
							i++;
						}

					}
					String join_table = "o";
					String num_table = "";
					FilterObj f_join0 = f;
					if (f_join0 != null) {
						int last_ind = f_join0.tablNums.lastIndexOf(",");
						num_table = f_join0.tablNums.substring(last_ind + 1);
						if (!num_table.equals("0"))
							join_table = "t" + num_table;
						else
							join_table="o";
					}
					if (i == 0 && clsFlr.id != attr_a[0].attr.classId) {
						KrnClass cls_ = db.getClassById(attr_a[0].attr.classId);// Присоединение
						// таблиц
						// базового
						// класса
						f = tMap.get(cls_.name); // если атрибут определен в
						// суперклассе
						if (f == null) {
							t_ind++;
							String str_exists = " EXISTS ( SELECT 1 FROM "
									+ getClassTableName(attr_a[0].attr.classId)
									+ " t" + t_ind + " WHERE " + join_table
									+ ".c_obj_id" + "=" + "t" + t_ind + ".c_obj_id"
                                    + " AND " + join_table + ".c_tr_id" + "=" + "t" + t_ind + ".c_tr_id ";
							f = new FilterObj();
							f.join = str_exists;
							f.tablNums = f_join0.tablNums
									+ (!f_join0.tablNums.equals("") ? "," : "")
									+ t_ind;
							num_table = "" + t_ind;
							f.isFunc = dataSelect_ == 1 ||!"".equals(linkPar_);
							tMap.put(cls_.name, f);
							join_table = "t" + num_table;
							f.root = f_obj.root;
						}
						f_join0 = f;
						if (f_join0 != null) {
							int last_ind = f_join0.tablNums.lastIndexOf(",");
							num_table = f_join0.tablNums
									.substring(last_ind + 1);
							if (!num_table.equals("0"))
								join_table = "t" + num_table;
							else
								join_table="o";
							if (dataSelect_ == 0 && "".equals(linkPar_) && f_join0.isFunc)// Удаление
								// принадлежности
								// к функции
								f_join0.isFunc = false; // если присоединяется
							// таблица без функции
						}
					}
					if (!tMap.containsKey(left_) && i < attr_a.length) {
						// Построение присоединенных таблиц
						for (; i < attr_a.length; ++i) {
							if (i == attr_a.length - 1 // для последнего
									// атрибута
									// присоединяется
									// таблица
									&& !(// в том
											// слеучае
											// если
											// атрибут
											// мультиязычный
											// или
											// множественный
                                            attr_a[i].attr.rAttrId>0
                                            || attr_a[i].attr.collectionType >= 1 || (i > 0
											&& attr_a[i].attr.typeClassId < 99
											&& attr_a[i - 1].attr.classId != attr_a[i].attr.classId && attr_a[i - 1].attr.typeClassId != attr_a[i].attr.classId)))
								break;

							t_ind++;
							FilterObj f_join = new FilterObj();
							f_join.root = f_obj.root;
							String str_exists = "";
							if (attr_a[i].attr.collectionType >= 1) {
								if (maxind_ || !kolObj_.equals("")) {
									if (!maxind_ || relative_) {
										f_join.root = f_join;
									}
									if (i == attr_a.length - 1) {
										if (maxind_) {
                                            if(attr_a[i].attr.rAttrId>0){
                                                KrnAttribute rAttr=db.getAttributeById(attr_a[i].attr.rAttrId);
                                                String cmColName = getColumnName(rAttr);
                                                String cmColMaxName= "c_obj_id";
                                                String colMax= "max";
                                                String ctRName= getClassTableName(rAttr.classId);
                                                String ctRId= ""+attr_a[i].attr.typeClassId;
                                                KrnAttribute sAttr=null;
                                                if(attr_a[i].attr.sAttrId>0){
                                                    sAttr = db.getAttributeById(attr_a[i].attr.sAttrId);
                                                    cmColMaxName = getColumnName(sAttr);
                                                    if(attr_a[i].attr.sDesc){
                                                      colMax= "min";
                                                    }
                                                }
                                                if(sAttr!=null && (sAttr.collectionType>0
                                                        || sAttr.classId!=rAttr.classId) ){
                                                    String ctSName;
                                                    if(sAttr.collectionType>0)
                                                        ctSName= getAttrTableName(sAttr);
                                                    else
                                                        ctSName= getClassTableName(sAttr.classId);
                                                    str_exists += "LEFT JOIN (SELECT r."+ cmColName+", "+colMax+"(s."+cmColMaxName+") c_i "
                                                    +" FROM "+ctRName+" r,"+ctSName+" s ";
        											if (relative_) {
        												f_join.position = str_exists
        														.length();
        												f_join.join_attr = "s.c_obj_id";
        											}
                                                    str_exists += " WHERE r.c_obj_id=s.c_obj_id GROUP BY r."+cmColName+") tm"+ctRId
                                                            +" ON o.c_obj_id = tm"+ctRId+"."+cmColName
                                                            +" LEFT JOIN "+ctRName+ " tr"+t_ind +" ON tr"+t_ind+"."+cmColName+"=tm"+ctRId+"."+cmColName
                                                            +" LEFT JOIN "+ctSName+ " t"+t_ind +" ON t"+t_ind+".c_obj_id = tr"+t_ind +".c_obj_id"+" AND t"+t_ind+"."+cmColMaxName+"=tm"+ctRId+".c_i ";
                                                }else{
                                                    str_exists += "LEFT JOIN (SELECT "+ cmColName+", "+colMax+"("+cmColMaxName+") c_i  "
                                                    +"FROM "+ctRName;
        											if (relative_) {
        												f_join.position = str_exists
        														.length();
        												f_join.join_attr = "c_obj_id";
        											}
                                                    str_exists += "  GROUP BY "+cmColName+") tm"+ctRId
                                                            +" ON o.c_obj_id = tm"+ctRId+"."+cmColName
                                                            +" LEFT JOIN "+ctRName+ " t"+t_ind +" ON t"+t_ind+"."+cmColMaxName+" =tm"+ctRId+".c_i AND t"+t_ind+"."+cmColName+"=tm"+ctRId+"."+cmColName+" ";
                                                }
                                            }else{
                                            	if (this instanceof OracleDriver3) {
                                            		str_exists += "LEFT JOIN (SELECT t"+t_ind+".c_obj_id, max(t"+t_ind+".c_index) c_i, SUM(1) c_i_"
                                            				+ " FROM "
                                            				+ getAttrTableName(attr_a[i].attr)
                                            				+ " t"+t_ind+" ";
                                            	} else {
                                            		str_exists += "LEFT JOIN (SELECT t"+t_ind+".c_obj_id, max(t"+t_ind+".c_index) c_i, count(t"+t_ind+".c_index) c_i_"
                                            				+ " FROM "
                                            				+ getAttrTableName(attr_a[i].attr)
                                            				+ " t"+t_ind+" ";
                                            	}
											if (relative_) {
												f_join.position = str_exists
														.length();
												f_join.join_attr = "t"+t_ind+"."
														+ getColumnName(attr_a[i].attr);
											}
                                                if(trans_==0)
                                                    str_exists += " WHERE t" + t_ind + ".c_tr_id=0 ";
                                                else if(trans_==2)
                                                    str_exists += " WHERE  t" + t_ind + ".c_tr_id in (0,_CURTRANS) ";
											str_exists += " GROUP BY t"+t_ind+".c_obj_id) tm"
													+ attr_a[i].attr.classId
													+ "_"
													+ t_ind
													+ " ON "
													+ join_table
													+ ".c_obj_id = tm"
													+ attr_a[i].attr.classId
													+ "_"
													+ t_ind + ".c_obj_id ";
											str_exists += "LEFT JOIN "
													+ getAttrTableName(attr_a[i].attr)
													+ " tmm"
													+ attr_a[i].attr.classId + "_"
													+ t_ind + " ON tm"
													+ attr_a[i].attr.classId + "_"
													+ t_ind
													+ ".c_obj_id  = tmm"
													+ attr_a[i].attr.classId + "_"
													+ t_ind + ".c_obj_id "
													+ " AND tmm"
													+ attr_a[i].attr.classId + "_"
													+ t_ind + ".c_index = tm"
													+ attr_a[i].attr.classId + "_"
													+ t_ind + ".c_i ";
											str_exists += "LEFT JOIN "
													+ getClassTableName(attr_a[i].attr.typeClassId)
													+ " t" + t_ind + " ON tmm"
													+ attr_a[i].attr.classId + "_"
													+ t_ind + "."
													+ getColumnName(attr_a[i].attr)
													+ " = t" + t_ind
													+ ".c_obj_id ";
                                            }
                                        } else {
                                            if(attr_a[i].attr.rAttrId>0){
                                                KrnAttribute rAttr=db.getAttributeById(attr_a[i].attr.rAttrId);
                                                String cmColName = getColumnName(rAttr);
                                                String ctRName= rAttr.collectionType>0?getAttrTableName(rAttr):getClassTableName(rAttr.classId);
                                                
                                                if (this instanceof OracleDriver3) {
                                                	str_exists += "LEFT JOIN (SELECT o."+ cmColName+", SUM(1) c_i_  "
                                                            +"FROM "+ctRName+" o";
                                                } else {
                                                	str_exists += "LEFT JOIN (SELECT o."+ cmColName+", count(o.c_obj_id) c_i_  "
                                                			+"FROM "+ctRName+" o";
                                                }
                                                str_exists += " LEFT JOIN "
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + " t" + t_ind + " ON o.c_obj_id"
                                                        + " = t" + t_ind
                                                        + ".c_obj_id ";
                                                if (!kolObj_.equals("")) {
                                                    f_join.position = str_exists
                                                            .length();
                                                }
                                                if(trans_==0)
                                                    str_exists += " WHERE o.c_tr_id =0 AND t" + t_ind + ".c_tr_id=0 ";
                                                else if(trans_==2)
                                                    str_exists += " WHERE  o.c_tr_id in (0,_CURTRANS) AND t" + t_ind + ".c_tr_id in (0,_CURTRANS) ";
                                                str_exists += " GROUP BY o."+cmColName+") tm"
                                                        + attr_a[i].attr.classId
                                                        + "_"
                                                        + t_ind
                                                        + " ON "
                                                        + join_table
                                                        + ".c_obj_id = tm"
                                                        + attr_a[i].attr.classId
                                                        + "_"
                                                        + t_ind + "."+ cmColName+" ";
                                            }else{
                                            	if (this instanceof OracleDriver3) {
        											str_exists += "LEFT JOIN (SELECT o.c_obj_id, max(o.c_index) c_i, SUM(1) c_i_"
        													+ " FROM "
        													+ getAttrTableName(attr_a[i].attr)
        													+ " o ";
                                            	} else {
        											str_exists += "LEFT JOIN (SELECT o.c_obj_id, max(o.c_index) c_i, count(o.c_index) c_i_"
        													+ " FROM "
        													+ getAttrTableName(attr_a[i].attr)
        													+ " o ";
                                            	}
											str_exists += "LEFT JOIN "
													+ getClassTableName(attr_a[i].attr.typeClassId)
													+ " t" + t_ind + " ON o."
													+ getColumnName(attr_a[i].attr)
													+ " = t" + t_ind
													+ ".c_obj_id ";
                                            if (!kolObj_.equals("")) {
												f_join.position = str_exists
														.length();
											}
                                            if(trans_==0)
                                                str_exists += " WHERE o.c_tr_id =0 AND t" + t_ind + ".c_tr_id=0 ";
                                            else if(trans_==2)
                                                str_exists += " WHERE  o.c_tr_id in (0,_CURTRANS) AND t" + t_ind + ".c_tr_id in (0,_CURTRANS) ";
											str_exists += " GROUP BY o.c_obj_id) tm"
													+ attr_a[i].attr.classId
													+ "_"
													+ t_ind
													+ " ON "
													+ join_table
													+ ".c_obj_id = tm"
													+ attr_a[i].attr.classId
													+ "_"
													+ t_ind + ".c_obj_id ";
                                            }
										}

									}
									// Количество объектов
								} else if (attr_a[i].attr.typeClassId >= 99) {
									if (i == attr_a.length - 1
											&& !operator_.equals("0")
											&& !operator_.equals("")) {
                                        if(attr_a[i].attr.rAttrId>0){
                                            KrnAttribute attr=db.getAttributeById(attr_a[i].attr.rAttrId);
                                            if(attr.collectionType>0){
                                                str_exists += " EXISTS ( SELECT 1 FROM "
                                                        + getAttrTableName(attr)
                                                        + " t" + t_ind
                                                        + " WHERE "
                                                        + join_table
                                                        + ".c_obj_id= t"
                                                        + t_ind + "."
                                                        + getColumnName(attr)
                                                        + " AND "
                                                        + join_table
                                                        + ".c_tr_id = t"
                                                        + t_ind
                                                        + ".c_tr_id "
                                                        + (trans > 0 ? "AND t" + t_ind
                                                                + ".c_del=0 " : "");

                                            }else{
                                                if(attr.classId!=attr_a[i].attr.typeClassId){
                                                    str_exists += " EXISTS ( SELECT 1 FROM "
                                                            + getClassTableName(attr.classId)
                                                            + " tr" + t_ind
                                                            + " WHERE "
                                                            + join_table
                                                            + ".c_obj_id = tr"
                                                            + t_ind + "."
                                                            + getRevColumnName(attr_a[i].attr)
                                                            + " AND tr" + t_ind + ".c_class_id IN "+getChildClasses(attr_a[i].attr.typeClassId)
                                                            + " AND tr" + t_ind + ".c_is_del=0 "
                                                            ;
                                                    str_exists += " EXISTS ( SELECT 1 FROM "
                                                            + getClassTableName(attr_a[i].attr.typeClassId)
                                                            + " t" + t_ind
                                                            + " WHERE tr"+t_ind + ".c_obj_id = t"
                                                            + t_ind + ".c_obj_id AND tr"
                                                            + t_ind + ".c_tr_id = t"
                                                            + t_ind + ".c_tr_id "
                                                            ;

                                                }else{
                                            str_exists += " EXISTS ( SELECT 1 FROM "
                                                    + getClassTableName(attr_a[i].attr.typeClassId)
                                                    + " t" + t_ind
                                                    + " WHERE "
                                                    + join_table
                                                    + ".c_obj_id = t"
                                                    + t_ind + "."
                                                    + getRevColumnName(attr_a[i].attr)
                                                    + " AND t" + t_ind + ".c_is_del=0 "
                                                    ;
                                                }
                                            }
                                        }else{
										str_exists += " EXISTS ( SELECT 1 FROM "
												+ getAttrTableName(attr_a[i].attr)
												+ " t"
												+ t_ind
												+ " WHERE "
												+ join_table
												+ ((attr_a.length > 1 && attr_a[i].attr.classId==attr_a[i].attr.typeClassId && attr_a[i - 1].attr.collectionType > 0) ? ".cm"
														+ attr_a[i - 1].attr.id
														: ".c_obj_id")
												+ " = t"
												+ t_ind
												+ ".c_obj_id AND "
												+ join_table
												+ ".c_tr_id = t"
												+ t_ind
												+ ".c_tr_id "
												+ (trans > 0 ? "AND t" + t_ind
														+ ".c_del=0 " : "");
                                        }
                                    } else {
                                        if(attr_a[i].attr.rAttrId>0){
                                            KrnAttribute attr=db.getAttributeById(attr_a[i].attr.rAttrId);
                                            if(attr.collectionType>0){
                                                str_exists += " EXISTS ( SELECT 1 FROM "
                                                        + getAttrTableName(attr)
                                                        + " tr" + t_ind
                                                        + " WHERE "
                                                        + join_table
                                                        + ".c_obj_id = tr"
                                                        + t_ind + "."
                                                        + getRevColumnName(attr_a[i].attr)
                                                        + " AND tr" + t_ind + ".c_del=0"
                                                        + " AND "
                                                        + join_table
                                                        + ".c_tr_id = tr"
                                                        + t_ind + ".c_tr_id "
                                                        ;
                                                str_exists += " EXISTS ( SELECT 1 FROM "
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + " t" + t_ind
                                                        + " WHERE tr"+t_ind + ".c_obj_id = t"
                                                        + t_ind + ".c_obj_id AND tr"
                                                        + t_ind + ".c_tr_id = t"
                                                        + t_ind + ".c_tr_id "
                                                        ;

                                            }else{
	                                            if(attr.classId!=attr_a[i].attr.typeClassId){
	                                                str_exists += " EXISTS ( SELECT 1 FROM "
	                                                        + getClassTableName(attr.classId)
	                                                        + " tr" + t_ind
	                                                        + " WHERE "
	                                                        + join_table
	                                                        + ".c_obj_id = tr"
	                                                        + t_ind + "."
	                                                        + getRevColumnName(attr_a[i].attr)
	                                                        + " AND tr" + t_ind + ".c_class_id IN "+getChildClasses(attr_a[i].attr.typeClassId)
	                                                        + " AND tr" + t_ind + ".c_is_del=0"
	                                                        + " AND "
	                                                        + join_table
	                                                        + ".c_tr_id = tr"
	                                                        + t_ind + ".c_tr_id "
	                                                        ;
	                                                str_exists += " EXISTS ( SELECT 1 FROM "
	                                                        + getClassTableName(attr_a[i].attr.typeClassId)
	                                                        + " t" + t_ind
	                                                        + " WHERE tr"+t_ind + ".c_obj_id = t"
	                                                        + t_ind + ".c_obj_id AND tr"
	                                                        + t_ind + ".c_tr_id = t"
	                                                        + t_ind + ".c_tr_id "
	                                                        ;
	
	                                            }else{
	                                            str_exists += " EXISTS ( SELECT 1 FROM "
	                                                    + getClassTableName(attr_a[i].attr.typeClassId)
	                                                    + " t" + t_ind
	                                                    + " WHERE "
	                                                    + join_table
	                                                    + ".c_obj_id = t"
	                                                    + t_ind + "."
	                                                    + getRevColumnName(attr_a[i].attr)
	                                                    + " AND "
	                                                    + join_table
	                                                    + ".c_tr_id = t"
	                                                    + t_ind + ".c_tr_id"
	                                                    + " AND t" + t_ind + ".c_is_del=0 "
	                                                    ;
	                                            }
                                           }
                                        }else{
                                            str_exists += " EXISTS ( SELECT 1 FROM "
                                                    + getAttrTableName(attr_a[i].attr)
                                                    + " tt"
                                                    + attr_a[i].attr.classId
                                                    + "_"
                                                    + t_ind
                                                    + " WHERE "
                                                    + join_table
                                                    + ".c_obj_id = tt"
                                                    + attr_a[i].attr.classId
                                                    + "_"
                                                    + t_ind
                                                    + ".c_obj_id AND "
                                                    + join_table
                                                    + ".c_tr_id = tt"
                                                    + attr_a[i].attr.classId
                                                    + "_"
                                                    + t_ind
                                                    + ".c_tr_id "
                                                    + (trans > 0 ? "AND tt"
                                                            + attr_a[i].attr.classId
                                                            + "_" + t_ind
                                                            + ".c_del=0 " : "");
                                            if (trans == 2) {
                                                str_exists += " EXISTS ( SELECT 1 FROM (SELECT c_is_del,c_obj_id, max(c_tr_id) c_tm_id FROM "
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + " WHERE c_is_del=0 AND  c_tr_id in (0,_CURTRANS) GROUP BY  c_is_del,c_obj_id) trm"
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + t_ind
                                                        + " WHERE tt"
                                                        + attr_a[i].attr.classId
                                                        + "_"
                                                        + t_ind
                                                        + "."
                                                        + getColumnName(attr_a[i].attr)
                                                        + " = trm"
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + t_ind
                                                        + ".c_obj_id "
                                                        + " AND EXISTS ( SELECT 1 FROM  "
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + " t"
                                                        + t_ind
                                                        + " WHERE  t"
                                                        + t_ind
                                                        + ".c_is_del=trm"
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + t_ind
                                                        + ".c_is_del AND t"
                                                        + t_ind
                                                        + ".c_obj_id=trm"
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + t_ind
                                                        + ".c_obj_id AND t"
                                                        + t_ind
                                                        + ".c_tr_id=trm"
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + t_ind + ".c_tm_id ";
                                            } else {
                                                str_exists += " AND EXISTS ( SELECT 1 FROM  "
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + " t" + t_ind + " WHERE tt"
                                                        + attr_a[i].attr.classId + "_"
                                                        + t_ind + "."
                                                        + getColumnName(attr_a[i].attr)
                                                        + " = t" + t_ind
                                                        + ".c_obj_id "
                                                        + " AND t" + t_ind + ".c_is_del=0 "
                                                        + (trans == 0 ? "AND t" +
                                                        + t_ind + ".c_tr_id=0 " : "")
                                                        ;
                                            }
                                        }
                                    }
								} else {
									str_exists += " AND EXISTS ( SELECT 1 FROM  "
											+ getAttrTableName(attr_a[i].attr)
											+ " t"
											+ t_ind
											+ " WHERE "
											+ join_table
											+ ".c_obj_id = t"
											+ t_ind
											+ ".c_obj_id AND "
											+ join_table
											+ ".c_tr_id = t"
											+ t_ind
											+ ".c_tr_id "
											+ (trans > 0 ? "AND t" + t_ind
													+ ".c_del=0 " : "");
								}
							} else if (i > 0
									&& attr_a[i].attr.typeClassId < 99
									&& attr_a[i - 1].attr.classId != attr_a[i].attr.classId
									&& attr_a[i - 1].attr.typeClassId != attr_a[i].attr.classId) {
								if (trans == 2) {
									str_exists += " AND EXISTS ( SELECT 1 FROM  (SELECT c_is_del,c_obj_id, max(c_tr_id) c_tm_id FROM "
											+ getClassTableName(attr_a[i].attr.classId)
											+ " WHERE c_is_del=0 AND c_tr_id in (0,_CURTRANS) GROUP BY  c_is_del,c_obj_id) trm"
											+ getClassTableName(attr_a[i].attr.classId)
											+ t_ind
											+ " WHERE "
											+ join_table
											+ ".c_obj_id = trm"
											+ getClassTableName(attr_a[i].attr.classId)
											+ t_ind
											+ ".c_obj_id "
											+ "  AND EXISTS ( SELECT 1 FROM  "
											+ getClassTableName(attr_a[i].attr.classId)
											+ " t"
											+ t_ind
											+ " WHERE  t"
											+ t_ind
											+ ".c_is_del=trm"
											+ getClassTableName(attr_a[i].attr.classId)
											+ t_ind
											+ ".c_is_del AND t"
											+ t_ind
											+ ".c_obj_id=trm"
											+ getClassTableName(attr_a[i].attr.classId)
											+ t_ind
											+ ".c_obj_id AND t"
											+ t_ind
											+ ".c_tr_id=trm"
											+ getClassTableName(attr_a[i].attr.classId)
											+ t_ind + ".c_tm_id ";
								} else {
									str_exists += " AND EXISTS ( SELECT 1 FROM  "
											+ getClassTableName(attr_a[i].attr.classId)
											+ " t"
											+ t_ind
											+ " WHERE "
											+ (f_join0.join_attr.equals("") ? join_table
													+ ".c_obj_id"
													: f_join0.join_attr)
											+ " = t"
											+ t_ind
											+ ".c_obj_id"
                                            + " AND t" + t_ind + ".c_is_del=0 "
											+ (trans == 0 ? "AND t" + t_ind
													+ ".c_tr_id=0 " : "");
								}
							} else {
								if (i > 0
										&& !getClassTableName(
												attr_a[i - 1].attr.typeClassId)
												.equals(
														getClassTableName(attr_a[i].attr.classId))) {
									if (trans == 2) {
										str_exists += " AND EXISTS ( SELECT 1 FROM (SELECT c_is_del,c_obj_id, max(c_tr_id) c_tm_id FROM "
												+ getClassTableName(attr_a[i].attr.classId)
												+ " WHERE c_is_del=0 AND c_tr_id in (0,_CURTRANS) GROUP BY  c_is_del,c_obj_id) trm"
												+ getClassTableName(attr_a[i].attr.classId)
												+ t_ind
												+ " WHERE "
												+ join_table
												+ ".c_obj_id = trm"
												+ getClassTableName(attr_a[i].attr.classId)
												+ t_ind
												+ ".c_obj_id "
												+ "  AND EXISTS ( SELECT 1 FROM   "
												+ getClassTableName(attr_a[i].attr.classId)
												+ " ot"
												+ t_ind
												+ " WHERE  ot"
												+ t_ind
												+ ".c_is_del=trm"
												+ getClassTableName(attr_a[i].attr.classId)
												+ t_ind
												+ ".c_is_del AND ot"
												+ t_ind
												+ ".c_obj_id=trm"
												+ getClassTableName(attr_a[i].attr.classId)
												+ t_ind
												+ ".c_obj_id AND ot"
												+ t_ind
												+ ".c_tr_id=trm"
												+ getClassTableName(attr_a[i].attr.classId)
												+ t_ind + ".c_tm_id ";
										str_exists += " AND EXISTS ( SELECT 1 FROM (SELECT c_is_del,c_obj_id, max(c_tr_id) c_tm_id FROM "
												+ getClassTableName(attr_a[i].attr.typeClassId)
												+ " WHERE c_is_del=0 AND c_tr_id in (0,_CURTRANS) GROUP BY  c_is_del,c_obj_id) trm1"
												+ getClassTableName(attr_a[i].attr.typeClassId)
												+ t_ind
												+ " WHERE ot"
												+ t_ind
												+ "."
												+ getColumnName(attr_a[i].attr)
												+ "=trm1"
												+ getClassTableName(attr_a[i].attr.typeClassId)
												+ t_ind
												+ ".c_obj_id "
												+ " AND EXISTS ( SELECT 1 FROM "
												+ getClassTableName(attr_a[i].attr.typeClassId)
												+ " t"
												+ t_ind
												+ " WHERE  t"
												+ t_ind
												+ ".c_is_del=trm1"
												+ getClassTableName(attr_a[i].attr.typeClassId)
												+ t_ind
												+ ".c_is_del AND t"
												+ t_ind
												+ ".c_obj_id=trm1"
												+ getClassTableName(attr_a[i].attr.typeClassId)
												+ t_ind
												+ ".c_obj_id AND t"
												+ t_ind
												+ ".c_tr_id=trm1"
												+ getClassTableName(attr_a[i].attr.typeClassId)
												+ t_ind + ".c_tm_id ";
									} else {
                                        if(attr_a[i].attr.rAttrId>0){
                                            KrnAttribute attr=db.getAttributeById(attr_a[i].attr.rAttrId);
                                            if(attr.classId!=attr_a[i].attr.typeClassId){
                                                str_exists += " AND EXISTS ( SELECT 1 FROM  "
                                                        + getClassTableName(attr.classId)
                                                        + " tr" + t_ind
                                                        + " WHERE "
                                                        + join_table
                                                        + ".c_obj_id = tr"
                                                        + t_ind + "."
                                                        + getRevColumnName(attr_a[i].attr)
                                                        + " AND tr" + t_ind + ".c_class_id IN "+getChildClasses(attr_a[i].attr.typeClassId)
                                                        + " AND tr" + t_ind + ".c_is_del=0 "
                                                        ;
                                                str_exists += " AND EXISTS ( SELECT 1 FROM  "
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + " t" + t_ind
                                                        + " WHERE tr"+t_ind + ".c_obj_id = t"
                                                        + t_ind + ".c_obj_id AND tr"
                                                        + t_ind + ".c_tr_id = t"
                                                        + t_ind + ".c_tr_id "
                                                        ;

                                            }else{
                                                str_exists += " AND EXISTS ( SELECT 1 FROM "
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + " t" + t_ind
                                                        + " WHERE "
                                                        + join_table
                                                        + ".c_obj_id = t"
                                                        + t_ind + "."
                                                        + getRevColumnName(attr_a[i].attr)
                                                        + " AND t" + t_ind + ".c_is_del=0 "
                                                        ;
                                            }
                                        }else{
										str_exists += " AND EXISTS ( SELECT 1 FROM "
												+ getClassTableName(attr_a[i].attr.classId)
												+ " ot" + attr_a[i].attr.classId
												+ "_" + t_ind + " WHERE "
												+ join_table + ".c_obj_id ="
												+ "ot" + attr_a[i].attr.classId
												+ "_" + t_ind + ".c_obj_id "
                                                + (trans == 0 ? "AND ot"
                                                + attr_a[i].attr.classId	+ "_"
                                                + t_ind + ".c_tr_id=0 " : "");
										str_exists += " AND EXISTS ( SELECT 1 FROM "
												+ getClassTableName(attr_a[i].attr.typeClassId)
												+ " t"
												+ t_ind
												+ " WHERE ot"
												+ attr_a[i].attr.classId
												+ "_"
												+ t_ind
												+ "."
												+ getColumnName(attr_a[i].attr)
												+ "="
												+ "t"
												+ t_ind
												+ ".c_obj_id "
                                                + " AND t" + t_ind + ".c_is_del=0 "
												+ (trans == 0 ? "AND t" + t_ind
														+ ".c_tr_id=0 " : "");
                                        }
                                    }
								} else {
									if (trans == 2) {
										str_exists += " AND EXISTS ( SELECT 1 FROM (SELECT c_is_del,c_obj_id, max(c_tr_id) c_tm_id FROM "
												+ getClassTableName(attr_a[i].attr.typeClassId)
												+ " WHERE c_is_del=0 AND  c_tr_id in (0,_CURTRANS) GROUP BY  c_is_del,c_obj_id) trm"
												+ getClassTableName(attr_a[i].attr.typeClassId)
												+ t_ind
												+ " WHERE "
												+ join_table
												+ "."
												+ getColumnName(attr_a[i].attr)
												+ "=trm"
												+ getClassTableName(attr_a[i].attr.typeClassId)
												+ t_ind
												+ ".c_obj_id "
												+ " AND EXISTS ( SELECT 1 FROM  "
												+ getClassTableName(attr_a[i].attr.typeClassId)
												+ " t"
												+ t_ind
												+ " WHERE  t"
												+ t_ind
												+ ".c_is_del=trm"
												+ getClassTableName(attr_a[i].attr.typeClassId)
												+ t_ind
												+ ".c_is_del AND t"
												+ t_ind
												+ ".c_obj_id=trm"
												+ getClassTableName(attr_a[i].attr.typeClassId)
												+ t_ind
												+ ".c_obj_id AND t"
												+ t_ind
												+ ".c_tr_id=trm"
												+ getClassTableName(attr_a[i].attr.typeClassId)
												+ t_ind + ".c_tm_id ";
									} else {
                                        if(attr_a[i].attr.rAttrId>0){
                                            KrnAttribute attr=db.getAttributeById(attr_a[i].attr.rAttrId);
                                            if(attr.classId!=attr_a[i].attr.typeClassId){
                                                str_exists += " AND EXISTS ( SELECT 1 FROM "
                                                        + getClassTableName(attr.classId)
                                                        + " tr" + t_ind
                                                        + " WHERE "
                                                        + join_table
                                                        + ".c_obj_id = tr"
                                                        + t_ind + "."
                                                        + getRevColumnName(attr_a[i].attr)
                                                        + " AND tr" + t_ind + ".c_class_id IN "+getChildClasses(attr_a[i].attr.typeClassId)
                                                        + " AND tr" + t_ind + ".c_is_del=0 "
                                                        ;
                                                str_exists += " AND EXISTS ( SELECT 1 FROM "
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + " t" + t_ind
                                                        + " WHERE tr"+t_ind + ".c_obj_id = t"
                                                        + t_ind + ".c_obj_id AND tr"
                                                        + t_ind + ".c_tr_id = t"
                                                        + t_ind + ".c_tr_id "
                                                        ;

                                            }else{
                                                if(attr.collectionType>0){
                                                    str_exists += " AND EXISTS ( SELECT 1 FROM "
                                                            + getRevAttrTableName(attr_a[i].attr)
                                                            + " tr" + t_ind
                                                            + " WHERE "
                                                            + join_table
                                                            + ".c_obj_id= tr"
                                                            + t_ind + "."
                                                            + getRevColumnName(attr_a[i].attr)
                                                            + " AND "
                                                            + join_table
                                                            + ".c_tr_id = tr"
                                                            + t_ind
                                                            + ".c_tr_id "
                                                            ;
                                                    str_exists += " AND EXISTS ( SELECT 1 FROM "
                                                            + getClassTableName(attr_a[i].attr.typeClassId)
                                                            + " t" + t_ind
                                                            + " WHERE tr"+ t_ind
                                                            + ".c_obj_id = t"
                                                            + t_ind + ".c_obj_id"
                                                            + " AND tr"+ t_ind
                                                            + ".c_tr_id = t"
                                                            + t_ind
                                                            + ".c_tr_id "
                                                            ;
                                                }else{

                                            str_exists += " AND EXISTS ( SELECT 1 FROM "
                                                    + getClassTableName(attr_a[i].attr.typeClassId)
                                                    + " t" + t_ind
                                                    + " WHERE "
                                                    + join_table
                                                    + ".c_obj_id = t"
                                                    + t_ind + "."
                                                    + getRevColumnName(attr_a[i].attr)
                                                    + " AND t" + t_ind + ".c_is_del=0 "
    												+ (trans == 0 ? "AND t" + t_ind
    														+ ".c_tr_id=0 " : "");
                                                }
                                            }
                                        }else{
										str_exists += " AND EXISTS ( SELECT 1 FROM "
												+ getClassTableName(attr_a[i].attr.typeClassId)
												+ " t"
												+ t_ind
												+ " WHERE "
												+ join_table
												+ "."
												+ getColumnName(attr_a[i].attr)
												+ "="
												+ "t"
												+ t_ind
												+ ".c_obj_id "
                                                + " AND t" + t_ind + ".c_is_del=0 "
												+ (trans == 0 ? "AND t" + t_ind
														+ ".c_tr_id=0 " : "");
                                        }
                                    }
								}
							}
							f_join.isFunc = dataSelect_ == 1 ||!"".equals(linkPar_);
							f_join.join = str_exists;
							f_join.tablNums = f_join0.tablNums
									+ (!f_join0.tablNums.equals("") ? "," : "")
									+ t_ind;
							f_join0 = f_join;
							if (f_join0 != null) {
								ind_dot = left_.indexOf(".", ind_dot + 1);
								str_attr = left_.substring(0,
										(ind_dot > -1 ? ind_dot : left_
												.length()));
								tMap.put(str_attr, f_join0);
								int last_ind = f_join0.tablNums
										.lastIndexOf(",");
								num_table = f_join0.tablNums
										.substring(last_ind + 1);
								if (!num_table.equals("0"))
									join_table = "t" + num_table;
								else
									join_table="o";
							}
							if (f_join.root == f_join) {
								f_join.tMap = new LinkedMap();
								f_join.tMap.putAll(tMap);
							}

						}
					} else if (attr_a[attr_a.length - 1].attr.typeClassId >= 99 && !f_obj.equals(f_join0)) {
                        if(attr_a.length == 1 && clsFlr.id != attr_a[0].attr.classId){
                            KrnClass cls_ = db.getClassById(attr_a[attr_a.length - 1].attr.classId);// Присоединение
                            // таблиц
                            // базового
                            // класса
                            f_join0 = tMap.get(cls_.name); // если атрибут определен в
                            // суперклассе
                        }else{
                            f_join0 = tMap.get(str_attr.substring(0, str_attr
                                    .lastIndexOf(".")));
                        }
                        int last_ind = f_join0.tablNums.lastIndexOf(",");
						num_table = f_join0.tablNums.substring(last_ind + 1);
						if (!num_table.equals("0"))
							join_table = "t" + num_table;
						else
							join_table="o";
					}
					// Построение условия для значения
					if (isSort) {// Для сортировки помеченных атрибутов
						String str_par = join_table + "." + getColumnName(attr_a[attr_a.length - 1].attr);
						if(attr_a[attr_a.length - 1].attr.isMultilingual){
							if(language.equals("LANGUAGE")){
								str_par=str_par+"_"+language;
							}else{
								str_par = join_table + "." + getColumnName(attr_a[attr_a.length - 1].attr, Long.valueOf(language));
							}
						}					
						f_join0.root.sql_sort += (f_join0.root.sql_sort.equals("") ? "" : ",")
													+str_par	+ " " + order_;
						f_join0.root.sql_head += ", " +str_par;
					} else {
						if (dataSelect_ == 3) {
							// Установка атрибута как параметра
							String name_col = attr_a[attr_a.length - 1].attr.typeClassId >= 99 ? ("o"
									+ join_table
									+ getColumnName(attr_a[attr_a.length - 1].attr) + ".c_uid")
									: (join_table + "." + getColumnName(attr_a[attr_a.length - 1].attr));
                                                                        if(attr_a[attr_a.length - 1].attr.isMultilingual){
                                                                            if(language.equals("LANGUAGE")){
                                                                                    name_col=name_col+"_LANGUAGE";
                                                                            }else{
                                                                                    name_col=name_col+"_"+ getSystemLangIndex(Long.valueOf(language));
                                                                            }
                                                                    }
							String rightStr_ = getStringParam(e
									.getChild("valFlr"), "exprFlr");
							sqlParMap.put(rightStr_.trim(), name_col);
						} else if (!operator_.equals("0")
								&& !operator_.equals("")) {
							String colName = getColumnName(attr_a[attr_a.length - 1].attr);
							if (dataSelect_ == 1 || !"".equals(linkPar_)) {
								sql_mean += "{" + num_table + " ";
							}
							if (dataSelect_ == 2) {
								// Сравнение с атрибутом
							        String lang_name="";
							        if(attr_a[attr_a.length - 1].attr.isMultilingual){
                                        if(language.equals("LANGUAGE")){
                                            lang_name="_LANGUAGE";
                                        }else{
                                            lang_name="_"+ getSystemLangIndex(Long.valueOf(language));
                                        }
                                    }
								sql_mean += getSqlComp(
										e,
										attr_a[attr_a.length - 1].attr.typeClassId >= 99 ? ("o"
												+ join_table + colName + ".c_uid")
												: (join_table + "." + getColumnName(attr_a[attr_a.length - 1].attr)+lang_name), sqlParMap);
							} else {
								if (attr_a[attr_a.length - 1].attr.typeClassId >= 99 
										&& !(attr_a[attr_a.length - 1].attr.rAttrId>0 
												&& (operator.equals("существует") 
														|| operator.equals("не существует")))) {
									if (f_join0.position > 0
											&& !f_join0.root.equals(f_join0)) {
										// TODO для аналогичного атрибута но
										// без максиндекса
									} else {
										if (f_join0.tablNumsObj.equals("")
												|| f_join0.tablNumsObj.indexOf(colName) < 0) {
											f_join0.tablNumsObj = f_join0.tablNumsObj
													+ (!f_join0.tablNumsObj.equals("") ? "," : "")
													+ getColumnName(attr_a[attr_a.length - 1].attr);
											if (attr_a.length > 1
													&& attr_a[attr_a.length - 2].attr.typeClassId != attr_a[attr_a.length - 1].attr.classId) {
												String t_name= " oot"+ attr_a[attr_a.length - 1].attr.classId + "_"	+ t_ind;
												t_name=getTableName(t_name, f_join0.root);
												String joinObj_ = "LEFT JOIN "
														+ getClassTableName(attr_a[attr_a.length - 1].attr.classId)
														+ " "+ t_name
														+ " ON "
														+ (f_join0.join_attr
																.equals("") ? join_table
																+ ".c_obj_id"
																: f_join0.join_attr)
														+ " = "+ t_name	+ ".c_obj_id "
                                                        + (f_join0.join_attr
                                                                .equals("") ?"AND " + t_name + ".c_tr_id= "
                                                                +join_table + ".c_tr_id "
                                                                :"");
												if (f_join0.joinObj.equals("")
														|| f_join0.joinObj
																.indexOf(joinObj_) < 0) {
													f_join0.joinObj += joinObj_;
												}
												f_join0.joinObj += "LEFT JOIN "
														+ getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
														+ " o"
														+ join_table
														+ colName
														+ " ON o"
														+ join_table
														+ colName
														+ ".c_obj_id = " + t_name
														+ "."
														+ getColumnName(attr_a[attr_a.length - 1].attr)
                                                        + (trans==0?" AND o"
                                                        + join_table
                                                        + colName
                                                        + ".c_tr_id = 0 ":" ");

											} else if (attr_a[attr_a.length - 1].attr.collectionType >0) {
                                                if(attr_a[attr_a.length - 1].attr.rAttrId>0){
                                                    f_join0.joinObj += "LEFT JOIN "
                                                            + getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
                                                            + " o"
                                                            + join_table
                                                            + colName
                                                            + " ON o"
                                                            + join_table
                                                            + colName
                                                            + ".c_obj_id = "
                                                            + join_table
                                                            + ".c_obj_id"
                                                            + " AND o"
                                                            + join_table
                                                            + colName
                                                            + ".c_tr_id = "
                                                            + join_table
                                                            + ".c_tr_id ";
                                                }else{
                                                f_join0.joinObj += "LEFT JOIN "
														+ getAttrTableName(attr_a[attr_a.length - 1].attr)
														+ " oot"
														+ attr_a[attr_a.length - 1].attr.classId
														+ "_"
														+ join_table
														+ " ON "
														+ join_table
														+ ".c_obj_id = oot"
														+ attr_a[attr_a.length - 1].attr.classId
														+ "_"
														+ join_table
														+ ".c_obj_id AND "
														+ join_table
														+ ".c_tr_id = oot"
														+ attr_a[attr_a.length - 1].attr.classId
														+ "_"
														+ join_table
														+ ".c_tr_id "
														+ (trans > 0 ? "AND oot"
																+ attr_a[attr_a.length - 1].attr.classId
																+ "_"
																+ join_table
																+ ".c_del=0 "
																: "");
												if (trans == 2) {
													f_join0.joinObj += "LEFT JOIN (SELECT c_is_del,c_obj_id, max(c_tr_id) c_tm_id FROM "
															+ getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
															+ " WHERE c_is_del=0 AND  c_tr_id in (0,_CURTRANS) GROUP BY  c_is_del,c_obj_id) trm"
															+ getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
															+ join_table
															+ " ON oot"
															+ attr_a[attr_a.length - 1].attr.classId
															+ "_"
															+ join_table
															+ "."
															+ getColumnName(attr_a[attr_a.length - 1].attr)
															+ "=trm"
															+ getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
															+ join_table
															+ ".c_obj_id "
															+ " LEFT JOIN "
															+ getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
															+ " o"
															+ join_table
															+ colName
															+ " ON  o"
															+ join_table
															+ colName
															+ ".c_is_del=trm"
															+ getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
															+ join_table
															+ ".c_is_del AND o"
															+ join_table
															+ colName
															+ ".c_obj_id=trm"
															+ getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
															+ join_table
															+ ".c_obj_id AND o"
															+ join_table
															+ colName
															+ ".c_tr_id=trm"
															+ getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
															+ join_table
															+ ".c_tm_id ";
												} else {
													f_join0.joinObj += "LEFT JOIN "
															+ getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
															+ " o"
															+ join_table
															+ colName
															+ " ON o"
															+ join_table
															+ colName
															+ ".c_obj_id = oot"
															+ attr_a[attr_a.length - 1].attr.classId
															+ "_"
															+ join_table
															+ "."
															+ getColumnName(attr_a[attr_a.length - 1].attr)
															+ (trans == 0 ? " AND o"
																	+ join_table
																	+ colName
																	+ ".c_tr_id=0 "
																	: " ");
												}
                                                }
                                            } else if(attr_a[attr_a.length - 1].attr.rAttrId==0){
												if (trans == 2) {
													f_join0.joinObj += "LEFT JOIN (SELECT c_is_del,c_obj_id, max(c_tr_id) c_tm_id FROM "
															+ getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
															+ " WHERE c_is_del=0 AND c_tr_id in (0,_CURTRANS) GROUP BY  c_is_del,c_obj_id) trm"
															+ getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
															+ join_table
															+ " ON "
															+ join_table
															+ "."
															+ getColumnName(attr_a[attr_a.length - 1].attr)
															+ "=trm"
															+ getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
															+ join_table
															+ ".c_obj_id "
															+ " LEFT JOIN "
															+ getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
															+ " o"
															+ join_table
															+ colName
															+ " ON  o"
															+ join_table
															+ colName
															+ ".c_is_del=trm"
															+ getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
															+ join_table
															+ ".c_is_del AND o"
															+ join_table
															+ colName
															+ ".c_obj_id=trm"
															+ getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
															+ join_table
															+ ".c_obj_id AND o"
															+ join_table
															+ colName
															+ ".c_tr_id=trm"
															+ getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
															+ join_table
															+ ".c_tm_id ";
												} else {
													f_join0.joinObj += "LEFT JOIN "
															+ getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
															+ " o"
															+ join_table
															+ colName
															+ " ON o"
															+ join_table
															+ colName
															+ ".c_obj_id = "
															+ join_table
															+ "."
															+ getColumnName(attr_a[attr_a.length - 1].attr)
															+ (trans == 0 ? " AND o"
																	+ join_table
																	+ colName
																	+ ".c_tr_id=0 "
																	: " ");
												}
											}
											f_join0.tipeClassJoinObj += (f_join0.tipeClassJoinObj
													.equals("") ? "" : ",")
													+ attr_a[attr_a.length - 1].attr.typeClassId;
										}
									}
								}
								String str_par;
								if (attr_a[attr_a.length - 1].attr.typeClassId >= 99) {
                                    if( attr_a[attr_a.length - 1].attr.rAttrId>0 &&
                                    		((attr_a.length==1 && attr_a[0].attr.collectionType==0) 
                                    				|| attr_a[attr_a.length - 1].attr.collectionType==0
                                    				|| db.getAttributeById(attr_a[attr_a.length - 1].attr.rAttrId).collectionType==0)
                                    		/*&& (attr_a[attr_a.length - 1].attr.collectionType==0 
                                    				|| (attr_a.length>1 && attr_a[attr_a.length - 2].attr.rAttrId>0))*/)
                                        str_par=join_table+".c_uid";
                                    else
                                        str_par = "o" + join_table + colName
											+ ".c_uid";
								} else if (attr_a[attr_a.length - 1].attr.typeClassId == 10
										&& (dataSelect_ == 1 || !"".equals(linkPar_))
										&& operator.equals("содержит")) {
									str_par = join_table + ".c_uid" + "|"
											+ colName;
								} else {
									boolean par = false, par_ = false;
									if (attr_a.length > 2) {
										par = attr_a[attr_a.length - 2].attr.collectionType > 0;
									}
									if (!f_join0.tipeClassJoinObj.equals("")) {
										StringTokenizer st = new StringTokenizer(
												f_join0.tipeClassJoinObj, ",");
										while (st.hasMoreTokens()) {
											String str_t = st.nextToken();
											par_ = str_t
													.equals(""
															+ attr_a[attr_a.length - 1].attr.classId);
											if (par_)
												break;
										}
									}
									if ((par
											&& attr_a[attr_a.length - 1].attr.typeClassId >= 99 && attr_a[attr_a.length - 2].attr.typeClassId == attr_a[attr_a.length - 1].attr.classId)
											|| (par_ && attr_a.length>1)) {
										str_par = "o"
												+ join_table
												+ getColumnName(attr_a[attr_a.length - 2].attr)
												+ "."
												+ getColumnName(attr_a[attr_a.length - 1].attr);
									} else {
										str_par = join_table
												+ "."
												+ getColumnName(attr_a[attr_a.length - 1].attr);
									}
								}
								if(attr_a[attr_a.length - 1].attr.isMultilingual){
									if(language.equals("LANGUAGE")){
										sql_mean += getSql(e,
												attr_a[attr_a.length - 1].attr.typeClassId,
												str_par+"_"+language,attr_a[attr_a.length - 1].attr, sqlParMap);
									}else if(language.contains(",")){
										int m=0;
										String str_par_="";
										while(true){
											if (language.indexOf(",", m) > 0) {
												String val = language.substring(m, language.indexOf(",",
														m));
												str_par_+= str_par+"_"+getSystemLangIndex(Long.valueOf(val))+",";
												m=language_.indexOf(",", m)+1;
											} else {
												String val = language.substring(m);
												str_par_+= str_par+"_"+getSystemLangIndex(Long.valueOf(val));
												break;
											}
											
										}
										sql_mean += getSql(e,
												attr_a[attr_a.length - 1].attr.typeClassId,
												str_par_,attr_a[attr_a.length - 1].attr, sqlParMap);
									}else{
										str_par=getColumnName(attr_a[attr_a.length - 1].attr, Long.valueOf(language));
										sql_mean += getSql(e,
												attr_a[attr_a.length - 1].attr.typeClassId,
												str_par,attr_a[attr_a.length - 1].attr, sqlParMap);
									}
									
								}else
								sql_mean += getSql(e,
										attr_a[attr_a.length - 1].attr.typeClassId,
										str_par,attr_a[attr_a.length - 1].attr, sqlParMap);
							}
							if (dataSelect_ == 1 || !"".equals(linkPar_)) {
								if(sql_mean.indexOf(" OR ") > 0){
									int m=0;
									String sql_mean_="";
									while(true){
										if (sql_mean.indexOf(" OR ", m) > 0) {
											m=sql_mean.indexOf(" OR ", m);
											sql_mean_ += sql_mean.substring(0,m)+"|" + f_join0.tablNums + "|}"+ num_table +" OR {"+num_table;
											m+=4;
										} else {
											sql_mean_ += sql_mean.substring(m);
											break;
										}
									}
									sql_mean=sql_mean_;
								}
								sql_mean += "|" + f_join0.tablNums + "|}"
										+ num_table;
							} else {

							}
							// if (f_join0.root.position>0) {
							// f_join0.root.sql_mean +=
							// (f_join0.root.sql_mean.equals("")?"":union) +
							// sql_mean;
							// }else{
							f_join0.sql_mean += (!(f_join0.sql_mean.equals("")|| sql_mean.equals(""))?union_:"") + sql_mean;
							// }
						} else if (!kolObj_.equals("")) {
							if (dataSelect_ == 1 || !"".equals(linkPar_)) {
								sql_mean += "{" + num_table + " ";
							}
							sql_mean += "tm"
									+ attr_a[attr_a.length - 1].attr.classId
									+ "_"
									+ t_ind
									+ ".c_i_"
									+ (kolObj_.equals("0") ? " IS NULL"
											: ((!kolOp_.equals("") && !kolOp_
													.equals("0")) ? operMap
													.get(kolOp_) : "")
													+ kolObj_) + " ";

							if (dataSelect_ == 1 || !"".equals(linkPar_)) {
								sql_mean += "|" + f_join0.tablNums + "|}"
										+ num_table;
							}
							f_obj.sql_mean += (!(f_obj.sql_mean.equals("")|| sql_mean.equals(""))?union_:"") + sql_mean;
						}
					}
                    if(path_a!=null && path_a.length>2){//Для атрибутов а угловых скобках(кастинг),
                    	            //чтобы выбор шел среди объектов выбранных 
                    	            //из условий для обычных атрибутов
                        PathElement2 p=path_a[path_a.length-1];
                        if(p.attr!=null && p.attr.typeClassId!=p.type.id){
                            String p_table="o";
                            int last_ind = f_obj.tablNums.lastIndexOf(",");
                            num_table = f_obj.tablNums.substring(last_ind + 1);
                            if (!num_table.equals("0"))
                                p_table = "t" + num_table;
    						else
    							p_table="o";
                            if(!join_table.equals(p_table))
                            	f_obj.root.sql_mean +=  join_table+".c_obj_id = "+p_table+".c_obj_id  AND ";
                        }
                    }
					if (maxIndpar) {
						f_join0.nodeMaxInd = left_;
						f_join0.union = union_;
					}

					if (independ) {
						f_join0.nodeIndepend = left_;
						f_join0.union = union_;
					}
					return f_join0;//
				}
			} catch (Exception ex) {
				// Игнорировать корявый фильтр
				log.warn("ОШИБКА: фильтр id = " + filterId + "  left=" + left_
						+ " " + ex.getMessage());
				log.error(ex, ex);
			}
		}
		return f_obj;
	}
	
	
//EXISTS
	private void createSqlUnion(long filterId, KrnClass clsFlr, Map<String, String> sqlParMap, StringBuffer sb_sql, Element root,Session session) {
		List<Element> minus_ = new ArrayList<Element>();
		// Дочерние узлы
		List<Element> al = root.getChild("children") != null ? root
				.getChild("children").getContent() : null;
		// Тип правой части отношения (0 - значение атрибута, 1 - функция, 2 - атрибут, 3 - параметр
		int dataSelect_ = getIntParam(root, "compFlr");
		if (dataSelect_ == 3) {
			String rightStr_ = getStringParam(root.getChild("valFlr"), "exprFlr");
			sqlParMap.put(rightStr_.trim(), "o.c_uid");
		}
		// Обединение 0 - по ИЛИ, 1 - по И
		String union = getStringParam(root, "unionFlr");
		if (al!=null && al.size() > 1 && !union.equals("1")) {
			// Объединение по "ИЛИ"
			for (Element e:al) {
				// Обратный фильтр (true, false)
				String inverse = getStringParam(e, "inversFlr");
				if (inverse.equals("true")) {
					// Добавляем обратные фильтры в массив
					minus_.add(e);		
				} else {
					StringBuffer e_sql = new StringBuffer();
					// Создаем SQL для каждого узла ИЛИ
					createSqlHead(filterId, clsFlr, sqlParMap, e_sql, e, true, minus_,session);
					
					// объединяем результаты запросов
					if (e_sql.length() > 0) {
						if (sb_sql.length() > 0)
							sb_sql.append("\n UNION \n");

						sb_sql.append("(").append(e_sql).append(")");
					}
				}
			}
		} else {
			// Объединение по "И"
			createSqlHead(filterId, clsFlr, sqlParMap, sb_sql, root, false, minus_,session);
		}
		// Обратный фильтр (true, false)
		String inverse = getStringParam(root, "inversFlr");
		if (inverse.equals("true")){
			// Транзакция 0 - нулевая, 1 - все, 2 - текущая
			int trans_ = getIntParam(root, "transFlr");
			String sql_mean = " WHERE c_is_del=0" + (trans_ >= 1 ? "" : " AND c_tr_id=0");
			// Выбираем все объекты и убираем оттуда результат головного фильтра
			sb_sql.insert(0, "SELECT DISTINCT c_obj_id, c_class_id, c_uid FROM " +getClassTableName(clsFlr.id) + sql_mean+"\n MINUS \n(");
			sb_sql.append(")");
		}
		// Вычитаем результаты обратных фильтров
		if (minus_.size() > 0) {
			for (Element e:minus_) {
				StringBuffer e_sql=new StringBuffer();
				// Создаем SQL для каждого обратного фильтра
				createSqlHead(filterId, clsFlr, sqlParMap, e_sql, e, true, null,session);
				
				// и вычитаем результат
				if (e_sql.length() > 0) {
					if (sb_sql.length() > 0)
						sb_sql.append("\n MINUS \n");

					sb_sql.append("(").append(e_sql).append(")");
				}
			}
		}
	}
	protected void createSqlHead(long filterId, KrnClass clsFlr, Map<String, String> sqlParMap, StringBuffer sb_sql, Element root, boolean isUnion,List minus_,Session session) {
		// Узел отключен? - тогда уходим отсюда
		if (getStringParam(root, "excludeFlr").equals("true")) return;

		FilterObj f_obj = new FilterObj();
		f_obj.root = f_obj;
		Map<String, FilterObj> tableMap = new LinkedMap();
		tableMap.put(clsFlr.getName(), f_obj);
		f_obj.tMap = tableMap;
		List al_ = root.getChild("children") != null ? root
				.getChild("children").getContent() : null;
		f_obj.sql_head = "SELECT DISTINCT o.c_obj_id, o.c_class_id, o.c_uid";
		f_obj.join = getClassTableName(clsFlr.id) + " o ";
		f_obj.tablNums = "0";
		
		// Операция
		String oper = getStringParam(root, "operFlr");
		// Правая часть отношения (объект)
		String rObj = getStringParam(root.getChild("valFlr"), "krnObjFlr");
		// Тип правой части отношения (0 - значение атрибута, 1 - функция, 2 - атрибут, 3 - параметр
		int dataSelect_ = getIntParam(root, "compFlr");
		// Нечувствительсность к регистру
		boolean resp = getStringParam(root, "respReg").equals("true");
		
		if (!isUnion && oper != null && !oper.equals("")
                && (dataSelect_ == 1 || (rObj != null && !rObj.equals("")))) {
			String sql_min_ = getSqlObject(root, "o.c_obj_id");
			if (dataSelect_ == 1) {
				sql_min_ = "{0 " + sql_min_ + " }0";
			}
			f_obj.sql_mean = "WHERE o.c_is_del=0 AND " + sql_min_;
		} else
			f_obj.sql_mean = "WHERE o.c_is_del=0";

		// Транзакция 0 - нулевая, 1 - все, 2 - текущая
		int trans_ = getIntParam(root, "transFlr");
		// Максимальная транзакция?
		boolean maxTrFlr_ = getStringParam(root, "maxTrFlr").equals("true");
		
		if (trans_ == 2) {
			f_obj.sql_mean += " AND o.c_tr_id = (SELECT max(c_tr_id) FROM "
					+ getClassTableName(clsFlr.id)
					+ " WHERE c_is_del=0 AND c_tr_id in (0,_CURTRANS) AND c_obj_id = o.c_obj_id) ";
		} else if (maxTrFlr_ && trans_ == 1) {
			f_obj.sql_mean += " AND o.c_tr_id = (SELECT max(c_tr_id) FROM "
					+ getClassTableName(clsFlr.id)
					+ " WHERE c_is_del=0 AND c_obj_id = o.c_obj_id) ";
		} else if (trans_ == 0) {
			f_obj.sql_mean += " AND o.c_tr_id=0";
		}
		
		f_obj.sql_mean += (al_ == null || isUnion || al_.size() > 0) ? " AND " : "";
		t_ind = 0;
		//
		String union_ = getStringParam(root, "unionFlr").equals("1") ? " AND "
				: " OR ";
		String operator_ = getStringParam(root, "operFlr");
 		createJoinTable(filterId, clsFlr, sqlParMap, root, false, f_obj, trans_,minus_);
		// Построение sql для детей
		if (al_ != null && al_.size() > 0) {
			if (al_.size() > 1) {
				// Открывающая скобка для объединения
				f_obj.sql_mean += "(";
			} else if (!operator_.equals("0") && !operator_.equals(""))
				f_obj.sql_mean += " AND ";
			for (int i = 0; i < al_.size(); ++i) {
				int index_union = f_obj.sql_mean.length();
				if (i > 0
						&& !f_obj.sql_mean.substring(
								f_obj.sql_mean.length() - 1).equals("("))
					f_obj.sql_mean += union_;
				Element e_i = (Element) al_.get(i);
				createSql(filterId, clsFlr, sqlParMap, e_i, f_obj, trans_,minus_,session);
				if (f_obj.sql_mean.length() == index_union + union_.length()) {
					f_obj.sql_mean = f_obj.sql_mean.substring(0, index_union);
				}
			}
			// Закрывающая скобка для объединения
			if (al_.size() > 1) {
				if (f_obj.sql_mean.substring(f_obj.sql_mean.length() - 1)
						.equals("("))
					f_obj.sql_mean = f_obj.sql_mean.substring(0, f_obj.sql_mean
							.length() - 2);
				else
					f_obj.sql_mean += ")";
			}
		}
		//
		// Сортировка
		f_obj.sql_sort = "";
		List sorts = root.getChildren("Sort");
		if (sorts != null && sorts.size() > 0) {
			for (Object sort : sorts) {
				createJoinTable(filterId, clsFlr, sqlParMap, (Element) sort, true, f_obj, trans_,minus_);
			}
		} else {
			List childs = root.getChild("children") != null ? root.getChild(
					"children").getContent() : null;
			if (childs != null) {
				for (Object child : childs) {
					Element e_i = (Element) child;
					String oper_ = getStringParam(e_i, "operFlr");
					if (oper_.equals("" + Constants.OPER_DESCEND)
							|| oper_.equals("" + Constants.OPER_ASCEND)) {
						createJoinTable(filterId, clsFlr, sqlParMap, e_i, true, f_obj, trans_,minus_);
					}
				}
			}
		}
		if (!f_obj.sql_sort.equals("")) {
			f_obj.sql_sort = " ORDER BY " + f_obj.sql_sort;
		}
		// Построение SQL
		int i = 0;
		for (FilterObj f : f_obj.tMap.values()) {
			String join = f.join;
			String join_obj = f.joinObj;
			if (join.equals(""))
				continue;
			int last_ind = f.tablNums.lastIndexOf(",");
			if (f.isFunc) {
				sb_sql.append("{");
				sb_sql.append(f.tablNums.substring(last_ind + 1));
			}
			if (!f.equals(f_obj) && !f.sql_mean.equals("")) {
				if (f.position > 0) {
					int j = 0;
					String join_ = "";
					for (FilterObj f_ : f.tMap.values()) {
						if (j > i)
							join_ += f_.join + f_.joinObj;
						++j;
					}
					String pos_str=join.substring(f.position,f.position + 7);
					join = join.substring(0, f.position) + " " + join_
							+ f.joinObj + " WHERE " + f.sql_mean + " AND "
							+ join.substring(f.position + (pos_str.equals(" WHERE ")?7:0));
					sb_sql.append(join);
				} else {
					if (f_obj.sql_mean.lastIndexOf("AND ") == f_obj.sql_mean
							.length() - 4)
						f_obj.sql_mean += f.sql_mean;
					else
						f_obj.sql_mean += f.union + f.sql_mean;
					sb_sql.append(join);
					sb_sql.append(join_obj);
				}
			} else {
				sb_sql.append(join);
				sb_sql.append(join_obj);
			}
			if (f.isFunc) {
				sb_sql.append("}");
				sb_sql.append(f.tablNums.substring(last_ind + 1));
			}
			++i;
		}
		f_obj.sql_head += " FROM ";
		sb_sql.insert(0, f_obj.sql_head);
		if (f_obj.sql_mean.lastIndexOf("AND ") == f_obj.sql_mean.length() - 4)
			f_obj.sql_mean = f_obj.sql_mean.substring(0, f_obj.sql_mean
					.length() - 4);
		if (f_obj.sql_mean.lastIndexOf("AND") == f_obj.sql_mean.length() - 3)
			f_obj.sql_mean = f_obj.sql_mean.substring(0, f_obj.sql_mean
					.length() - 3);
		sb_sql.append(f_obj.sql_mean);
		sb_sql.append(f_obj.sql_sort);
		int ind_d=sb_sql.indexOf("AND  AND");
		if(ind_d>0)	sb_sql.replace(ind_d, ind_d+8, "AND");
		ind_d=sb_sql.indexOf("AND AND");
		if(ind_d>0)	sb_sql.replace(ind_d, ind_d+7, "AND");
		if(!"".equals(f_obj.groupFieldName)) {
			sb_sql.append(" GROUPPING ");
			sb_sql.append(f_obj.groupFunc+"|");
			sb_sql.append(f_obj.groupFieldName+"|");
			sb_sql.append(f_obj.groupClassType+"|");
			sb_sql.append(f_obj.groupNumTable+"|");
		}

	}

	protected void createSql(long filterId, KrnClass clsFlr, Map<String, String> sqlParMap, Element e, FilterObj f_obj, long trans,List minus_,Session session) {

		List al_ = e.getChild("children") != null ? e.getChild("children")
				.getContent() : null;
		//
		String union_ = getStringParam(e, "unionFlr").equals("1") ? " AND "
				: " OR ";
		String left_ = getStringParam(e, "attrFlr");
		String linkFlr_ = getStringParam(e, "linkFlr");
		if(linkFlr_!=null && !"".equals(linkFlr_) && (al_==null || al_.size()==0))
			al_=getLinkChild(linkFlr_,trans,session);
		String operator_ = getStringParam(e, "operFlr");
		boolean exclude_ = getStringParam(e, "excludeFlr").equals("true");
		boolean resp = getStringParam(e, "respReg").equals("true");
		if (exclude_)
			return;
		else if(minus_!=null && getStringParam(e, "inversFlr").equals("true")){
			minus_.add(e);		
			return;
		}
		FilterObj f_obj_ = createJoinTable(filterId, clsFlr, sqlParMap, e, false, f_obj, trans,minus_);

		// Транзакция 0 - нулевая, 1 - все, 2 - текущая
		int trans_ = getIntParam(e, "transFlr");
		if (trans_ > 0)
			trans = trans_;
		
		// Построение sql для детей
		if (al_ != null && al_.size() > 0) {
			if (al_.size() > 1) {
				// Открывающая скобка для объединения
				if (!left_.equals("") && !operator_.equals("0")
						&& !operator_.equals(""))
					f_obj.root.sql_mean += " AND (";
                else if(f_obj_.isJoinTable)
                    f_obj_.root.sql_mean += union_+"(";
				else
					f_obj_.root.sql_mean += "(";
			} else if (!operator_.equals("0") && !operator_.equals(""))
				f_obj_.root.sql_mean += " AND ";
			for (int i = 0; i < al_.size(); ++i) {
				int index_union = f_obj_.root.sql_mean.length();
				if (i > 0
						&& !f_obj_.root.sql_mean.substring(
								f_obj_.root.sql_mean.length() - 1).equals("("))
					f_obj_.root.sql_mean += union_;
				Element e_i = (Element) al_.get(i);
				createSql(filterId, clsFlr, sqlParMap, e_i, f_obj_, trans,minus_,session);
				if (f_obj_.root.sql_mean.length() == index_union
						+ union_.length()) {
					f_obj_.root.sql_mean = f_obj_.root.sql_mean.substring(0,
							index_union);
				}
			}
			// Закрывающая скобка для объединения
			if (al_.size() > 1) {
				if (f_obj_.root.sql_mean.substring(
						f_obj_.root.sql_mean.length() - 1).equals("("))
					f_obj_.root.sql_mean = f_obj_.root.sql_mean.substring(0,
							f_obj_.root.sql_mean.length() - 2);
				else
					f_obj_.root.sql_mean += ")";
			}
		}
		// Возвращаем старый корень
		f_obj_.root = f_obj.root;
		//
	}

	protected FilterObj createJoinTable(long filterId, KrnClass clsFlr, Map<String, String> sqlParMap, Element e, boolean isSort,
			FilterObj f_obj, long trans,List minus_) {
		boolean maxIndpar = false;
		boolean independ = false;
		String left_ = getStringParam(e, "attrFlr");
		String union_ = getStringParam(e, "unionFlr").equals("1") ? " AND "
				: " OR ";
		String operator_ = getStringParam(e, "operFlr");
		String operator = operMap.get(operator_);
		String order_ = getStringParam(e, "order");
		String sql_mean = "";
		boolean exclude_ = getStringParam(e, "excludeFlr").equals("true");
		if (exclude_) {
			f_obj.sql_mean += " 1=1";
			return f_obj;
		}else if(minus_!=null && getStringParam(e, "inversFlr").equals("true")){
				minus_.add(e);		
				return f_obj;
		}
		if (operator_.equals("" + Constants.OPER_DESCEND)
				|| operator_.equals("" + Constants.OPER_ASCEND)) {
			if (isSort) {
				order_ = operator_.equals("" + Constants.OPER_DESCEND) ? "DESC"
						: "ASC";
			} else
				return f_obj;
		}
		String num_ = getStringParam(e, "compFlr");
		int dataSelect_ = num_.equals("") ? 0 : Integer.valueOf(num_);
        String linkPar_ = getStringParam(e, "linkPar");
		boolean maxind_ = getStringParam(e, "maxIndFlr").equals("true");
		boolean isgroup_ = getStringParam(e, "groupFlr").equals("true");
		boolean independ_ = getStringParam(e, "independFlr").equals("true");
		boolean relative_ = getStringParam(e, "relativeFlr").equals("true");
		boolean resp = getStringParam(e, "respReg").equals("true");
		String grpFunc_ = getStringParam(e, "grpFuncFlr");
		String kolOp_ = getStringParam(e, "kolOperFlr");
		String kolObj_ = getStringParam(e, "kolObjFlr");
		num_ = getStringParam(e, "transFlr");
		int trans_ = num_.equals("") ? 0 : Integer.valueOf(num_);
		boolean maxTrFlr_ = getStringParam(e, "maxTrFlr").equals("true");
		String currTransFlr="AND c_tr_id in (0,_CURTRANS) "; 
		if (maxTrFlr_ && trans_ == 1)
			currTransFlr=""; 
//		if (trans_ > 0)
			trans = trans_;
		String language_ = getStringParam(e, "language");
		String language = "";
        boolean joinCls_ = getStringParam(e, "joinCls").equals("true");
        if(joinCls_){
            try{
                String parentAttr=  getStringParam(e, "attrParent");
                String opJoin_ = getStringParam(e, "operJoin");
                String opJoin = operMap.get(opJoin_);
                String childAttr=  getStringParam(e, "attrChild");
                String pAttr="",cAttr="",joinTabl="";
                String jCls=childAttr.substring(0,childAttr.indexOf("."));
                PathElement2[] path_parent = parentAttr.equals("") ? new PathElement2[0]
                        : Toolkit.parsePath2(parentAttr,this);
                if (path_parent != null && path_parent.length > 1) {
                    PathElement2[] attr_parent=new PathElement2[path_parent.length-1];
                    System.arraycopy(path_parent,1,attr_parent,0,attr_parent.length);
                    pAttr=getColumnName(attr_parent[attr_parent.length-1].attr);
                }
                PathElement2[] path_child = childAttr.equals("") ? new PathElement2[0]
                        : Toolkit.parsePath2(childAttr,this);
                if (path_parent != null && path_child.length > 1) {
                    PathElement2[] attr_child=new PathElement2[path_child.length-1];
                    System.arraycopy(path_child,1,attr_child,0,attr_child.length);
                    cAttr=getColumnName(attr_child[attr_child.length-1].attr);
                    joinTabl=getClassTableName(attr_child[attr_child.length-1].attr.classId);

                }
                int last_ind = f_obj.tablNums.lastIndexOf(",");
                String num_table = f_obj.tablNums.substring(last_ind + 1);
                String parent_table="o";
                if (!num_table.equals("0"))
                    parent_table = "t" + num_table;
                else
                	parent_table = "o";
                t_ind++;
                String str_join = "LEFT JOIN "
                        + joinTabl
                        + " t"
                        + t_ind
                        + " ON "+parent_table+"."
                        + pAttr
                        + opJoin
                        + "t"
                        + t_ind
                        + "."+cAttr+" "
                        + (trans == 0 ? "AND t" + t_ind
                                + ".c_tr_id=0 " : "");
                FilterObj f = new FilterObj();
                f.join = str_join;
                f.tablNums = "" + t_ind;
                num_table = "" + t_ind;
                f.isFunc = dataSelect_ == 1 ||!"".equals(linkPar_);
                f.isJoinTable=true;
                f.root = f;
                f.root.tMap = new LinkedMap();
                f.root.tMap.put(jCls,f);
                f_obj.root.tMap.put(""+t_ind,f);
                return f;
            } catch (Exception ex) {
                // Игнорировать корявый фильтр
                log.warn("ОШИБКА: фильтр id = " + filterId + "  left=" + left_
                        + " " + ex.getMessage());
    			log.error(ex, ex);
            }
        }
        if (!language_.equals("")) {
			int m = 0;
			while (true) {
				if (language_.indexOf(",", m) > 0) {
					String val = language_.substring(m, language_.indexOf(",",
							m));
					try {
						KrnObject obj = getObjectByUid(val, 0);
						language += "" + obj.id;
						language += (m = language_.indexOf(",", m) + 1) > 0 ? ","
								: "";
					} catch (Exception ex) {
						log.error(ex, ex);
					}
				} else {
					String val = language_.substring(m);
					try {
						KrnObject obj = getObjectByUid(val, 0);
						language += "" + obj.id;
					} catch (Exception ex) {
						log.error(ex, ex);
					}
					break;
				}
			}

		} else {
			language = "LANGUAGE";
		}
		if (!left_.equals("")) {
			if (f_obj.root.tMap == null)
				f_obj.root.tMap = new LinkedMap();
			Map<String, FilterObj> tMap = f_obj.root.tMap;
			try {
				// Путь к фильтруемой переменной
                PathElement2[] path_a = left_.equals("") ? new PathElement2[0]
						: Toolkit.parsePath2(left_,this);
				if (path_a != null && path_a.length > 1) {
                    PathElement2[] attr_a=new PathElement2[path_a.length-1];
                    System.arraycopy(path_a,1,attr_a,0,attr_a.length);
                    if ((maxind_ || !kolObj_.equals(""))
                            && attr_a[attr_a.length - 1].attr.collectionType > 0) {
                		left_ += "#";
                    	while(tMap.containsKey(left_))
                    		left_ += "#";
                        maxIndpar = true;
                    } else if (!f_obj.nodeMaxInd.equals("")) {
                        left_ = f_obj.nodeMaxInd
                                + left_.substring(f_obj.nodeMaxInd.indexOf("#"));
                    }
                    //Построение независимых узлов
                    if (independ_ && kolObj_.equals("") && attr_a[attr_a.length - 1].attr.collectionType > 0) {
                    	int i=0;
                    	while(true){
                    		String left_0 = left_+"_"+i;
                    		if(!tMap.containsKey(left_0)){
                                left_ += "_"+i;
                    			break;
                    		}
                    		i++;
                    	}
                        independ = true;
                    } else if (!f_obj.nodeIndepend.equals("")) {
                        left_ = f_obj.nodeIndepend
                                + left_.substring(f_obj.nodeIndepend.length() - 2);
                    }

					// Построение sql для атрибута
					int ind_dot = left_.indexOf(".");
					String str_attr = left_.substring(0,
							(ind_dot > -1 ? ind_dot : left_.length()));
					int i = 0;
					FilterObj f = null;
					int ind_dot_ = ind_dot;
					while (ind_dot_ > -1) {
						if (!tMap.containsKey(str_attr)) {// Поиск
							// присоединенных
							// таблиц стаким же
							// путем
							i--;
							ind_dot--;
							break;
						}
						i++;
						f = tMap.get(str_attr);
						if (dataSelect_ == 0 && "".equals(linkPar_) && f.isFunc && f != f_obj)// Удаление
							// принадлежности к
							// функции
							f.isFunc = false; // если присоединяется таблица
						// без функции
						ind_dot_ = left_.indexOf(".", ind_dot_ + 1);
						if (ind_dot_ < 0)
							i--;
						else
							ind_dot = ind_dot_;
						str_attr = left_.substring(0, (ind_dot_ > -1 ? ind_dot_
								: left_.length()));
						if (ind_dot_ < 0 && tMap.containsKey(str_attr)) {
							f = tMap.get(str_attr);
							i++;
						}

					}
					String join_table = "o";
					String num_table = "";
					FilterObj f_join0 = f;
					if (f_join0 != null) {
						int last_ind = f_join0.tablNums.lastIndexOf(",");
						num_table = f_join0.tablNums.substring(last_ind + 1);
						if (!num_table.equals("0"))
							join_table = "t" + num_table;
						else
							join_table="o";
					}
					if (i == 0 && clsFlr.id != attr_a[0].attr.classId) {
						KrnClass cls_ = db.getClassById(attr_a[0].attr.classId);// Присоединение
						// таблиц
						// базового
						// класса
						f = tMap.get(cls_.name); // если атрибут определен в
						// суперклассе
						if (f == null) {
							t_ind++;
							String str_join = "LEFT JOIN "
									+ getClassTableName(attr_a[0].attr.classId)
									+ " t" + t_ind + " ON " + join_table
									+ ".c_obj_id" + "=" + "t" + t_ind + ".c_obj_id "
			                        + (trans == 0 ? " AND t" + t_ind + ".c_tr_id=0 ":
			                        	maxTrFlr_ ? " AND t" + t_ind + ".c_tr_id="+join_table+".c_tr_id " : "");
							f = new FilterObj();
							f.join = str_join;
							f.tablNums = f_join0.tablNums
									+ (!f_join0.tablNums.equals("") ? "," : "")
									+ t_ind;
							num_table = "" + t_ind;
							f.isFunc = dataSelect_ == 1 ||!"".equals(linkPar_);
							tMap.put(cls_.name, f);
							join_table = "t" + num_table;
							f.root = f_obj.root;
						}
						f_join0 = f;
						if (f_join0 != null) {
							int last_ind = f_join0.tablNums.lastIndexOf(",");
							num_table = f_join0.tablNums
									.substring(last_ind + 1);
							if (!num_table.equals("0"))
								join_table = "t" + num_table;
							else
								join_table="o";
							if (dataSelect_ == 0 && "".equals(linkPar_) && f_join0.isFunc)// Удаление
								// принадлежности
								// к функции
								f_join0.isFunc = false; // если присоединяется
							// таблица без функции
						}
					}
					if (!tMap.containsKey(left_) && i < attr_a.length) {
						// Построение присоединенных таблиц
						for (; i < attr_a.length; ++i) {
							if (i == attr_a.length - 1 // для последнего
									// атрибута
									// присоединяется
									// таблица
									&& !(// в том
											// слеучае
											// если
											// атрибут
											// мультиязычный
											// или
											// множественный
                                            attr_a[i].attr.rAttrId>0
                                            || attr_a[i].attr.collectionType >= 1 || (i > 0
											&& attr_a[i].attr.typeClassId < 99
											&& attr_a[i - 1].attr.classId != attr_a[i].attr.classId && attr_a[i - 1].attr.typeClassId != attr_a[i].attr.classId)))
								break;

							t_ind++;
							FilterObj f_join = new FilterObj();
							f_join.root = f_obj.root;
							String str_join = "";
							if (attr_a[i].attr.collectionType >= 1) {
								FilterObj f_join_max=null;
								if (tMap.containsKey(str_attr+"#") && i == 0) {
									f_join_max=tMap.get(str_attr+"#");
								}
								if (maxind_ || !kolObj_.equals("")) {
									if (!maxind_ || relative_) {
										f_join.root = f_join;
									}
									if (i == attr_a.length - 1) {
										if (maxind_) {
                                            if(attr_a[i].attr.rAttrId>0){
                                                KrnAttribute rAttr=db.getAttributeById(attr_a[i].attr.rAttrId);
                                                String cmColName = getColumnName(rAttr);
                                                String cmColMaxName= "c_obj_id";
                                                String colMax= "max";
                                                String ctRName= getClassTableName(rAttr.classId);
                                                String ctRId= ""+attr_a[i].attr.typeClassId;
                                                KrnAttribute sAttr=null;
                                                if(attr_a[i].attr.sAttrId>0){
                                                    sAttr=db.getAttributeById(attr_a[i].attr.sAttrId);
                                                    cmColMaxName = getColumnName(sAttr);
                                                    if(attr_a[i].attr.sDesc){
                                                      colMax= "min";
                                                    }
                                                }
                                                if(sAttr!=null && (sAttr.collectionType>0
                                                        || sAttr.classId!=rAttr.classId) ){
                                                    String ctSName;
                                                    if(sAttr.collectionType>0)
                                                        ctSName= getAttrTableName(sAttr);
                                                    else
                                                        ctSName= getClassTableName(sAttr.classId);
                                                    str_join += "LEFT JOIN (SELECT r."+ cmColName+", "+colMax+"(s."+cmColMaxName+") c_i "
                                                    +" FROM "+ctRName+" r,"+ctSName+" s ";
        											if (relative_) {
        												f_join.position = str_join
        														.length();
        												f_join.join_attr = "s.c_obj_id";
        											}
                                                    str_join += " WHERE r.c_obj_id=s.c_obj_id GROUP BY r."+cmColName+") tm"+ctRId
                                                            +" ON "+join_table+".c_obj_id = tm"+ctRId+"."+cmColName
                                                            +" LEFT JOIN "+ctRName+ " tr"+t_ind +" ON tr"+t_ind+"."+cmColName+"=tm"+ctRId+"."+cmColName
                                                            +" LEFT JOIN "+ctSName+ " t"+t_ind +" ON t"+t_ind+".c_obj_id = tr"+t_ind +".c_obj_id"+" AND t"+t_ind+"."+cmColMaxName+"=tm"+ctRId+".c_i ";
                                                }else{
                                                    str_join += "LEFT JOIN (SELECT "+ cmColName+", "+colMax+"("+cmColMaxName+") c_i  "
                                                    +"FROM "+ctRName;
        											if (relative_) {
        												f_join.position = str_join
        														.length();
        												f_join.join_attr = "c_obj_id";
        											}
                                                    str_join += "  GROUP BY "+cmColName+") tm"+ctRId
                                                            +" ON "+join_table+".c_obj_id = tm"+ctRId+"."+cmColName
                                                            +" LEFT JOIN "+ctRName+ " t"+t_ind +" ON t"+t_ind+"."+cmColMaxName+" =tm"+ctRId+".c_i AND t"+t_ind+"."+cmColName+"=tm"+ctRId+"."+cmColName+" ";
                                                }
                                            }else{
                                            	if (this instanceof OracleDriver3) {
                                            		str_join += "LEFT JOIN (SELECT t"+t_ind+".c_obj_id, max(t"+t_ind+".c_index) c_i, SUM(1) c_i_"
                                            				+ " FROM "
                                            				+ getAttrTableName(attr_a[i].attr)
                                            				+ " t"+t_ind+" ";
                                            	} else {
                                            		str_join += "LEFT JOIN (SELECT t"+t_ind+".c_obj_id, max(t"+t_ind+".c_index) c_i, count(t"+t_ind+".c_index) c_i_"
                                            				+ " FROM "
                                            				+ getAttrTableName(attr_a[i].attr)
                                            				+ " t"+t_ind+" ";
                                            	}
                                            	
											if (relative_) {
												f_join.position = str_join
														.length();
												f_join.join_attr = "t"+t_ind+"."
														+ getColumnName(attr_a[i].attr);
											}
                                                if(trans_==0)
                                                    str_join += " WHERE t" + t_ind + ".c_tr_id=0 ";
                                                else if(trans_==2)
                                                    str_join += " WHERE t" + t_ind + ".c_tr_id in (0,_CURTRANS) ";
											str_join += " GROUP BY t"+t_ind+".c_obj_id) tm"
													+ attr_a[i].attr.classId
													+ "_"
													+ t_ind
													+ " ON "
													+ join_table
													+ ".c_obj_id = tm"
													+ attr_a[i].attr.classId
													+ "_"
													+ t_ind + ".c_obj_id ";
											str_join += "LEFT JOIN "
													+ getAttrTableName(attr_a[i].attr)
													+ " tmm"
													+ attr_a[i].attr.classId + "_"
													+ t_ind + " ON tm"
													+ attr_a[i].attr.classId + "_"
													+ t_ind
													+ ".c_obj_id  = tmm"
													+ attr_a[i].attr.classId + "_"
													+ t_ind + ".c_obj_id "
													+ " AND tmm"
													+ attr_a[i].attr.classId + "_"
													+ t_ind + ".c_index = tm"
													+ attr_a[i].attr.classId + "_"
													+ t_ind + ".c_i ";
											str_join += "LEFT JOIN "
													+ getClassTableName(attr_a[i].attr.typeClassId)
													+ " t" + t_ind + " ON tmm"
													+ attr_a[i].attr.classId + "_"
													+ t_ind + "."
													+ getColumnName(attr_a[i].attr)
													+ " = t" + t_ind
													+ ".c_obj_id ";
                                            }
                                        } else {
                                            if(attr_a[i].attr.rAttrId>0){
                                                KrnAttribute rAttr=db.getAttributeById(attr_a[i].attr.rAttrId);
                                                String cmColName = getColumnName(rAttr);
                                                String ctRName= rAttr.collectionType>0?getAttrTableName(rAttr):getClassTableName(rAttr.classId);
                                                if (this instanceof OracleDriver3) {
                                                	str_join += "LEFT JOIN (SELECT o."+ cmColName+", SUM(1) c_i_  "
                                                            +"FROM "+ctRName+" o";
                                                } else {
                                                	str_join += "LEFT JOIN (SELECT o."+ cmColName+", count(o.c_obj_id) c_i_  "
                                                        +"FROM "+ctRName+" o";
                                                }
                                                str_join += " LEFT JOIN "
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + " t" + t_ind + " ON o.c_obj_id"
                                                        + " = t" + t_ind
                                                        + ".c_obj_id ";
                                                if (!kolObj_.equals("")) {
                                                    f_join.position = str_join
                                                            .length();
                                                }
                                                if(trans_==0)
                                                    str_join += " WHERE o.c_tr_id =0 AND t" + t_ind + ".c_tr_id=0 ";
                                                else if(trans_==2)
                                                    str_join += " WHERE o.c_tr_id in (0,_CURTRANS) AND t" + t_ind + ".c_tr_id in (0,_CURTRANS) ";
                                                str_join += " GROUP BY o."+cmColName+") tm"
                                                        + attr_a[i].attr.classId
                                                        + "_"
                                                        + t_ind
                                                        + " ON "
                                                        + join_table
                                                        + ".c_obj_id = tm"
                                                        + attr_a[i].attr.classId
                                                        + "_"
                                                        + t_ind + "."+ cmColName+" ";
                                            } else {
                                            	if (attr_a[i].attr.collectionType==1) {
                                            		if (this instanceof OracleDriver3) {
                                            			str_join += "LEFT JOIN (SELECT o.c_obj_id, o.c_del, SUM(1) c_i_"
                                            					+ " FROM "
                                            					+ getAttrTableName(attr_a[i].attr)
                                            					+ " o ";
                                            		} else {
                                            			str_join += "LEFT JOIN (SELECT o.c_obj_id, o.c_del, count(o.c_index) c_i_"
                                            					+ " FROM "
                                            					+ getAttrTableName(attr_a[i].attr)
                                            					+ " o ";
                                            		}
                                            	} else {
                                            		// у Оракла count работает не стабильно?!, заменяем // Ерик 07.04.2020
                                            		if (this instanceof OracleDriver3) {
                                            			str_join += "LEFT JOIN (SELECT o.c_obj_id, o.c_del, SUM(1) c_i_"
                                            					+ " FROM "
                                            					+ getAttrTableName(attr_a[i].attr)
                                            					+ " o ";
                                            		} else {
                                            			str_join += "LEFT JOIN (SELECT o.c_obj_id, o.c_del, count(o."+getColumnName(attr_a[i].attr)+") c_i_"
                                            					+ " FROM "
                                            					+ getAttrTableName(attr_a[i].attr)
                                            					+ " o ";
                                            		}
                                            	}
                                            	str_join += "LEFT JOIN "
                                            			+ getClassTableName(attr_a[i].attr.typeClassId)
                                            			+ " t" + t_ind + " ON o."
                                            			+ getColumnName(attr_a[i].attr)
                                            			+ " = t" + t_ind
                                            			+ ".c_obj_id ";
                                            	
                                            if (!kolObj_.equals("")) {
												f_join.position = str_join
														.length();
											}
                                            if(trans_==0)
                                                str_join += " WHERE o.c_del=0 AND o.c_tr_id =0 AND t" + t_ind + ".c_tr_id=0";
                                            else if(trans_==2)
                                                str_join += " WHERE o.c_del =0 AND o.c_tr_id in (0,_CURTRANS) AND t" + t_ind + ".c_tr_id in (0,_CURTRANS)";
											
                                            str_join += " GROUP BY o.c_obj_id,o.c_del) tm"
													+ attr_a[i].attr.classId
													+ "_"
													+ t_ind
													+ " ON "
													+ join_table
													+ ".c_obj_id = tm"
													+ attr_a[i].attr.classId
													+ "_"
													+ t_ind + ".c_obj_id ";
                                            }
										}

									}
									// Количество объектов
								} else if (attr_a[i].attr.typeClassId >= 99) {
									if (i == attr_a.length - 1
											&& !operator_.equals("0")
											&& !operator_.equals("")) {
                                        if(attr_a[i].attr.rAttrId>0){
                                            KrnAttribute attr=db.getAttributeById(attr_a[i].attr.rAttrId);
                                            if(attr.rAttrId>0){
                                                KrnAttribute attrr=db.getAttributeById(attr.rAttrId);
	                                            if(attrr.collectionType>0){
	                                                str_join += "LEFT JOIN "
	                                                        + getAttrTableName(attrr)
	                                                        + " t" + t_ind
	                                                        + " ON "
	                                                        + join_table
	                                                        + ".c_obj_id= t"
	                                                        + t_ind + "."
	                                                        + getColumnName(attrr)
	                                                        + (trans == 0 ? " AND t" + t_ind + ".c_tr_id =0":"")
	                                                        + " AND t" + t_ind + ".c_del=0 ";
	
	                                            }else{
	                                                if(attrr.classId!=attr.typeClassId){
	                                                    str_join += "LEFT JOIN "
	                                                            + getClassTableName(attrr.classId)
	                                                            + " tr" + t_ind
	                                                            + " ON "
	                                                            + join_table
	                                                            + ".c_obj_id = tr"
	                                                            + t_ind + "."
	                                                            + getRevColumnName(attr)
	                                                            + " AND tr" + t_ind + ".c_class_id IN "+getChildClasses(attr.typeClassId)
	                                                            + " AND tr" + t_ind + ".c_is_del=0 "
	                                                            ;
	                                                    str_join += "LEFT JOIN "
	                                                            + getClassTableName(attr.typeClassId)
	                                                            + " t" + t_ind
	                                                            + " ON tr"+t_ind + ".c_obj_id = t"
	                                                            + t_ind + ".c_obj_id "
	                                                            +(trans==0?"AND t" + t_ind + ".c_tr_id = 0 ":"");
		                                                }else{
	                                            str_join += "LEFT JOIN "
	                                                    + getClassTableName(attr.typeClassId)
	                                                    + " t" + t_ind
	                                                    + " ON "
	                                                    + join_table
	                                                    + ".c_obj_id = t"
	                                                    + t_ind + "."
	                                                    + getRevColumnName(attr)
	                                                    + " AND t" + t_ind + ".c_is_del=0 "
	                                                    ;
	                                                }
	                                            }
                                            }else{
	                                            if(attr.collectionType>0){
	                                                str_join += "LEFT JOIN "
	                                                        + getAttrTableName(attr)
	                                                        + " t" + t_ind
	                                                        + " ON "
	                                                        + join_table
	                                                        + ".c_obj_id= t"
	                                                        + t_ind + "."
	                                                        + getColumnName(attr)
	                                                        + (trans==0?" AND t"+ t_ind + ".c_tr_id =0":"")
	                                                        + " AND t" + t_ind + ".c_del=0 ";
	
	                                            }else{
	                                                if(attr.classId!=attr_a[i].attr.typeClassId){
	                                                    str_join += "LEFT JOIN "
	                                                            + getClassTableName(attr.classId)
	                                                            + " tr" + t_ind
	                                                            + " ON "
	                                                            + join_table
	                                                            + ".c_obj_id = tr"
	                                                            + t_ind + "."
	                                                            + getRevColumnName(attr_a[i].attr)
	                                                            + " AND tr" + t_ind + ".c_class_id IN "+getChildClasses(attr_a[i].attr.typeClassId)
	                                                            + " AND tr" + t_ind + ".c_is_del=0 "
	                                                            ;
	                                                    str_join += "LEFT JOIN "
	                                                            + getClassTableName(attr_a[i].attr.typeClassId)
	                                                            + " t" + t_ind
	                                                            + " ON tr"+t_ind + ".c_obj_id = t"
	                                                            + t_ind + ".c_obj_id "
	                                                            + (trans==0?"AND t" + t_ind + ".c_tr_id = 0 ":"")
	                                                            ;
	
	                                                }else{
	                                            str_join += "LEFT JOIN "
	                                                    + getClassTableName(attr_a[i].attr.typeClassId)
	                                                    + " t" + t_ind
	                                                    + " ON "
	                                                    + join_table
	                                                    + ".c_obj_id = t"
	                                                    + t_ind + "."
	                                                    + getRevColumnName(attr_a[i].attr)
	                                                    + " AND t" + t_ind + ".c_is_del=0 "
	                                                    ;
	                                                }
	                                            }
                                            }
                                        }else{
										str_join += "LEFT JOIN "
												+ getAttrTableName(attr_a[i].attr)
												+ " ot"
												+ t_ind
												+ getColumnName(attr_a[i].attr)
												+ " ON "
												+ join_table
												+ ((attr_a.length > 1 && attr_a[i].attr.classId==attr_a[i].attr.typeClassId && attr_a[i - 1].attr.collectionType > 0) ? ".cm"
														+ attr_a[i - 1].attr.id
														: ".c_obj_id")
												+ " = ot"
												+ t_ind
												+ getColumnName(attr_a[i].attr)
												+ ".c_obj_id "
												+ " AND ot" + t_ind + getColumnName(attr_a[i].attr) + ".c_del=0 "
												+ "AND ot"+ t_ind + getColumnName(attr_a[i].attr)+ ".c_tr_id ="+(trans==0?" 0 "
														: join_table + ".c_tr_id ");
										str_join += "LEFT JOIN "
												+ getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
												+ " t"
												+ t_ind
												+ " ON t"
												+ t_ind
												+ ".c_obj_id = "
												+ " ot"
												+ t_ind
												+ getColumnName(attr_a[i].attr)
												+ "."
												+ getColumnName(attr_a[attr_a.length - 1].attr)
												+ (trans == 0 ? " AND t"
														+ t_ind
														+ ".c_tr_id=0 "
														: " ");
                                        }
                                    } else {
                                        if(attr_a[i].attr.rAttrId>0){
                                            KrnAttribute attr=db.getAttributeById(attr_a[i].attr.rAttrId);
                                            if(attr.collectionType>0){
                                                str_join += "LEFT JOIN "
                                                        + getAttrTableName(attr)
                                                        + " tr" + t_ind
                                                        + " ON "
                                                        + join_table
                                                        + ".c_obj_id = tr"
                                                        + t_ind + "."
                                                        + getRevColumnName(attr_a[i].attr)
                                                        + " AND tr" + t_ind + ".c_del=0 "
                                                        + (trans==0?"AND tr"+ t_ind + ".c_tr_id =0 ":"");
                                                str_join += "LEFT JOIN "
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + " t" + t_ind
                                                        + " ON tr"+t_ind + ".c_obj_id = t"
                                                        + t_ind + ".c_obj_id "
                                                        + (trans==0?"AND  t"+ t_ind + ".c_tr_id =0 ":"")
                                                        ;

                                            }else{
	                                            if(attr.classId!=attr_a[i].attr.typeClassId){
	                                                str_join += "LEFT JOIN "
	                                                        + getClassTableName(attr.classId)
	                                                        + " tr" + t_ind
	                                                        + " ON "
	                                                        + join_table
	                                                        + ".c_obj_id = tr"
	                                                        + t_ind + "."
	                                                        + getRevColumnName(attr_a[i].attr)
	                                                        + " AND tr" + t_ind + ".c_class_id IN "+getChildClasses(attr_a[i].attr.typeClassId)
	                                                        + " AND tr" + t_ind + ".c_is_del=0"
	                                                        + (trans==0?" AND tr"+ t_ind + ".c_tr_id =0 ":"")
	                                                        ;
	                                                str_join += "LEFT JOIN "
	                                                        + getClassTableName(attr_a[i].attr.typeClassId)
	                                                        + " t" + t_ind
	                                                        + " ON tr"+t_ind + ".c_obj_id = t"
	                                                        + t_ind + ".c_obj_id "
	                                                        + (trans==0?"AND t"+ t_ind + ".c_tr_id =0 ":"")
	                                                        ;
	
	                                            }else{
	                                            str_join += "LEFT JOIN "
	                                                    + getClassTableName(attr_a[i].attr.typeClassId)
	                                                    + " t" + t_ind
	                                                    + " ON "
	                                                    + join_table
	                                                    + ".c_obj_id = t"
	                                                    + t_ind + "."
	                                                    + getRevColumnName(attr_a[i].attr)
	                                                    + (trans==0?" AND t"+ t_ind + ".c_tr_id=0":"")
	                                                    + " AND t" + t_ind + ".c_is_del=0 "
	                                                    ;
		                                            if(f_join_max!=null){//связываем объекты по максимальному индексу
		                        						int ind_max = f_join_max.tablNums.lastIndexOf(",");
		                        						String num_table_max = f_join_max.tablNums.substring(ind_max + 1);
		                        						if (!num_table_max.equals("0")){
		    	                                            str_join +=" AND t" + t_ind + ".c_obj_id = t" + num_table_max + ".c_obj_id ";
		                        						}
		                                            }
	                                            }
                                           }
                                        }else{
                                            str_join += "LEFT JOIN "
                                                    + getAttrTableName(attr_a[i].attr)
                                                    + " tt"
                                                    + attr_a[i].attr.classId
                                                    + "_"
                                                    + t_ind
                                                    + " ON "
                                                    + join_table
                                                    + ".c_obj_id = tt"
                                                    + attr_a[i].attr.classId
                                                    + "_"
                                                    + t_ind
                                                    + ".c_obj_id "
                                                    + (trans==0?"AND tt" + attr_a[i].attr.classId + "_" + t_ind +".c_tr_id =0 ":"")
                                                    + "AND tt" + attr_a[i].attr.classId + "_" + t_ind + ".c_del=0 ";
                                            if (trans == 2 ||(maxTrFlr_ && trans == 1)) {
                                                str_join += "LEFT JOIN (SELECT c_is_del,c_obj_id, max(c_tr_id) c_tm_id FROM "
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + " WHERE c_is_del=0 "+currTransFlr+"GROUP BY  c_is_del,c_obj_id) trm"
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + t_ind
                                                        + " ON  tt"
                                                        + attr_a[i].attr.classId
                                                        + "_"
                                                        + t_ind
                                                        + "."
                                                        + getColumnName(attr_a[i].attr)
                                                        + " = trm"
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + t_ind
                                                        + ".c_obj_id "
                                                        + "LEFT JOIN "
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + " t"
                                                        + t_ind
                                                        + " ON  t"
                                                        + t_ind
                                                        + ".c_is_del=trm"
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + t_ind
                                                        + ".c_is_del AND t"
                                                        + t_ind
                                                        + ".c_obj_id=trm"
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + t_ind
                                                        + ".c_obj_id AND t"
                                                        + t_ind
                                                        + ".c_tr_id=trm"
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + t_ind + ".c_tm_id ";
                                            } else {
                                                str_join += "LEFT JOIN "
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + " t" + t_ind + " ON tt"
                                                        + attr_a[i].attr.classId + "_"
                                                        + t_ind + "."
                                                        + getColumnName(attr_a[i].attr)
                                                        + " = t" + t_ind
                                                        + ".c_obj_id "
                                                        + " AND t" + t_ind + ".c_is_del=0 "
                                                        + (trans == 0 ? "AND t" +
                                                        + t_ind + ".c_tr_id=0 " : "")
                                                        ;
                                            }
                                        }
                                    }
								} else {
									str_join += "LEFT JOIN "
											+ getAttrTableName(attr_a[i].attr)
											+ " t"
											+ t_ind
											+ " ON "
											+ join_table
											+ ".c_obj_id = t"
											+ t_ind
											+ ".c_obj_id "
											+ (trans==0?"AND t"+ t_ind+ ".c_tr_id =0 ":"")
											+ "AND t" + t_ind+ ".c_del=0 ";
								}
							} else if (i > 0
									&& attr_a[i].attr.typeClassId < 99
									&& attr_a[i - 1].attr.classId != attr_a[i].attr.classId
									&& attr_a[i - 1].attr.typeClassId != attr_a[i].attr.classId) {
									str_join += "LEFT JOIN "
											+ getClassTableName(attr_a[i].attr.classId)
											+ " t"
											+ t_ind
											+ " ON "
											+ (f_join0.join_attr.equals("") ? join_table
													+ ".c_obj_id"
													: f_join0.join_attr)
											+ " = t"
											+ t_ind
											+ ".c_obj_id"
                                            + " AND t" + t_ind + ".c_is_del=0 "
											+ (trans == 0 ? "AND t" + t_ind
													+ ".c_tr_id=0 " : "");
                            		if (trans == 2 ||(maxTrFlr_ && trans == 1)) {
                            			sql_mean += "t"+t_ind	+ ".c_tr_id = "
                            				+"(SELECT max(c_tr_id) FROM " + getClassTableName(attr_a[i].attr.classId)
											+ " WHERE c_is_del=0 "+currTransFlr+" AND c_obj_id = t" + t_ind + ".c_obj_id) AND ";
                            		}
							} else {
								if (i > 0
										&& !getClassTableName(attr_a[i - 1].attr.typeClassId)
											.equals(getClassTableName(attr_a[i].attr.classId))) {
                                        if(attr_a[i].attr.rAttrId>0){
                                            KrnAttribute attr=db.getAttributeById(attr_a[i].attr.rAttrId);
                                            if(attr.collectionType>0) {
                                                str_join += "LEFT JOIN "
                                                        + getAttrTableName(attr)
                                                        + " tr" + t_ind
                                                        + " ON "
                                                        + join_table
                                                        + ".c_obj_id = tr"
                                                        + t_ind + "."
                                                        + getRevColumnName(attr_a[i].attr)
                                                        + " AND tr" + t_ind + ".c_del=0 "
                                                        + (trans==0?"AND tr"+ t_ind + ".c_tr_id =0 ":"");
                                                str_join += "LEFT JOIN "
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + " t" + t_ind
                                                        + " ON tr"+t_ind + ".c_obj_id = t"
                                                        + t_ind + ".c_obj_id "
                                                        + (trans==0?"AND  t"+ t_ind + ".c_tr_id =0 ":"")
                                                        ;
                                            }else {
	                                            if(attr.classId!=attr_a[i].attr.typeClassId){
	                                                str_join += "LEFT JOIN "
	                                                        + getClassTableName(attr.classId)
	                                                        + " tr" + t_ind
	                                                        + " ON "
	                                                        + join_table
	                                                        + ".c_obj_id = tr"
	                                                        + t_ind + "."
	                                                        + getRevColumnName(attr_a[i].attr)
	                                                        + " AND tr" + t_ind + ".c_class_id IN "+getChildClasses(attr_a[i].attr.typeClassId)
	                                                        + " AND tr" + t_ind + ".c_is_del=0 "
	                                                        ;
	                                                str_join += "LEFT JOIN "
	                                                        + getClassTableName(attr_a[i].attr.typeClassId)
	                                                        + " t" + t_ind
	                                                        + " ON tr"+t_ind + ".c_obj_id = t"
	                                                        + t_ind + ".c_obj_id "
	                                                        + (trans==0?"AND t"+ t_ind + ".c_tr_id =0 ":"")
	                                                        ;
	
	                                            }else{
	                                                str_join += "LEFT JOIN "
	                                                        + getClassTableName(attr_a[i].attr.typeClassId)
	                                                        + " t" + t_ind
	                                                        + " ON "
	                                                        + join_table
	                                                        + ".c_obj_id = t"
	                                                        + t_ind + "."
	                                                        + getRevColumnName(attr_a[i].attr)
	                                                        + " AND t" + t_ind + ".c_is_del=0 "
	                                                        ;
	                                            }
                                            }
                                        }else{
										str_join += "LEFT JOIN "
												+ getClassTableName(attr_a[i].attr.classId)
												+ " ot" + attr_a[i].attr.classId
												+ "_" + t_ind + " ON "
												+ join_table + ".c_obj_id ="
												+ "ot" + attr_a[i].attr.classId
												+ "_" + t_ind + ".c_obj_id "
                                                + (trans == 0 ? "AND ot"
                                                + attr_a[i].attr.classId	+ "_"
                                                + t_ind + ".c_tr_id=0 " : "");
										str_join += "LEFT JOIN "
												+ getClassTableName(attr_a[i].attr.typeClassId)
												+ " t"
												+ t_ind
												+ " ON ot"
												+ attr_a[i].attr.classId
												+ "_"
												+ t_ind
												+ "."
												+ getColumnName(attr_a[i].attr)
												+ "="
												+ "t"
												+ t_ind
												+ ".c_obj_id "
                                                + " AND t" + t_ind + ".c_is_del=0 "
												+ (trans == 0 ? "AND t" + t_ind
														+ ".c_tr_id=0 " : "");
                                        }
                                		if (trans == 2 ||(maxTrFlr_ && trans == 1)) {
                                			sql_mean += "t" + t_ind + ".c_tr_id = "
                                				+"(SELECT max(c_tr_id) FROM " + getClassTableName(attr_a[i].attr.typeClassId)
												+ " WHERE c_is_del=0 "+currTransFlr+" AND c_obj_id = t" + t_ind + ".c_obj_id) AND ";
                                		}
								} else {
									if(attr_a[i].attr.rAttrId>0){
                                            KrnAttribute attr=db.getAttributeById(attr_a[i].attr.rAttrId);
                                            if(attr.classId!=attr_a[i].attr.typeClassId){
                                                str_join += "LEFT JOIN "
                                                        + getClassTableName(attr.classId)
                                                        + " tr" + t_ind
                                                        + " ON "
                                                        + join_table
                                                        + ".c_obj_id = tr"
                                                        + t_ind + "."
                                                        + getRevColumnName(attr_a[i].attr)
                                                        + " AND tr" + t_ind + ".c_class_id IN "+getChildClasses(attr_a[i].attr.typeClassId)
                                                        + " AND tr" + t_ind + ".c_is_del=0 "
                                                        ;
                                                str_join += "LEFT JOIN "
                                                        + getClassTableName(attr_a[i].attr.typeClassId)
                                                        + " t" + t_ind
                                                        + " ON tr"+t_ind + ".c_obj_id = t"
                                                        + t_ind + ".c_obj_id "
                                                        + (trans==0?"AND t"+ t_ind + ".c_tr_id =0 ":"")
                                                        ;

                                            }else{
                                                if(attr.collectionType>0){
                                                    str_join += "LEFT JOIN "
                                                            + getRevAttrTableName(attr_a[i].attr)
                                                            + " tr" + t_ind
                                                            + " ON "
                                                            + join_table
                                                            + ".c_obj_id= tr"
                                                            + t_ind + "."
                                                            + getRevColumnName(attr_a[i].attr)
                                                            + (trans==0?" AND tr"+ t_ind+ ".c_tr_id =0 ":" ")
                                                            ;
                                                    str_join += "LEFT JOIN "
                                                            + getClassTableName(attr_a[i].attr.typeClassId)
                                                            + " t" + t_ind
                                                            + " ON tr"+ t_ind
                                                            + ".c_obj_id = t"
                                                            + t_ind + ".c_obj_id"
                                                            + (trans==0?" AND t"+ t_ind+ ".c_tr_id =0 ":" ")
                                                            ;
                                                }else{

                                            str_join += "LEFT JOIN "
                                                    + getClassTableName(attr_a[i].attr.typeClassId)
                                                    + " t" + t_ind
                                                    + " ON "
                                                    + join_table
                                                    + ".c_obj_id = t"
                                                    + t_ind + "."
                                                    + getRevColumnName(attr_a[i].attr)
                                                    + " AND t" + t_ind + ".c_is_del=0 "
    												+ (trans == 0 ? "AND t" + t_ind
    														+ ".c_tr_id=0 " : "");
                                                }
                                            }
                                        }else{
                                        		str_join += "LEFT JOIN "
													+ getClassTableName(attr_a[i].attr.typeClassId)
													+ " t"
													+ t_ind
													+ " ON "
													+ join_table
													+ "."
													+ getColumnName(attr_a[i].attr)
													+ "="
													+ "t"
													+ t_ind
													+ ".c_obj_id "
	                                                + " AND t" + t_ind + ".c_is_del=0 "
													+ (trans == 0 ? "AND t" + t_ind
															+ ".c_tr_id=0 " : "");
                                        }
                                		if (trans == 2 ||(maxTrFlr_ && trans == 1)) {
                                			sql_mean += "t" + t_ind + ".c_tr_id = "
                                				+"(SELECT max(c_tr_id) FROM " + getClassTableName(attr_a[i].attr.typeClassId)
												+ " WHERE c_is_del=0 "+currTransFlr+" AND c_obj_id = t" + t_ind + ".c_obj_id) AND ";
                                		}
								}
							}
							f_join.isFunc = dataSelect_ == 1 ||!"".equals(linkPar_);
							f_join.join = str_join;
							f_join.tablNums = f_join0.tablNums
									+ (!f_join0.tablNums.equals("") ? "," : "")
									+ t_ind;
							f_join0 = f_join;
							if (f_join0 != null) {
								ind_dot = left_.indexOf(".", ind_dot + 1);
								str_attr = left_.substring(0,
										(ind_dot > -1 ? ind_dot : left_
												.length()));
								tMap.put(str_attr, f_join0);
								int last_ind = f_join0.tablNums
										.lastIndexOf(",");
								num_table = f_join0.tablNums
										.substring(last_ind + 1);
								if (!num_table.equals("0"))
									join_table = "t" + num_table;
								else
									join_table="o";
							}
							if (f_join.root == f_join) {
								f_join.tMap = new LinkedMap();
								f_join.tMap.putAll(tMap);
							}

						}
					} else if (attr_a[attr_a.length - 1].attr.typeClassId >= 99 && !f_obj.equals(f_join0) && attr_a[attr_a.length - 1].attr.rAttrId==0) {
                        if(attr_a.length == 1 && clsFlr.id != attr_a[0].attr.classId){
                            KrnClass cls_ = db.getClassById(attr_a[attr_a.length - 1].attr.classId);// Присоединение
                            // таблиц
                            // базового
                            // класса
                            f_join0 = tMap.get(cls_.name); // если атрибут определен в
                            // суперклассе
                        }else{
                            f_join0 = tMap.get(str_attr.substring(0, str_attr
                                    .lastIndexOf(".")));
                        }
                        int last_ind = f_join0.tablNums.lastIndexOf(",");
						num_table = f_join0.tablNums.substring(last_ind + 1);
						if (!num_table.equals("0"))
							join_table = "t" + num_table;
						else
							join_table="o";

					}
					if(!"".equals(grpFunc_) && !"0".equals(grpFunc_)) {
						f_join0.root.groupFunc+= "".equals(f_join0.root.groupFunc)?"":",";
						f_join0.root.groupFunc += grpMap.get(grpFunc_)+"("+join_table + "." 
						+ getColumnName(attr_a[attr_a.length - 1].attr)+") AS G_" + grpMap.get(grpFunc_) + "_" + f_join0.root.groupCount++;
					}
					if(isgroup_) {
						f_join0.root.groupFieldName+= "".equals(f_join0.root.groupFieldName)?"":",";
						f_join0.root.groupClassType+= "".equals(f_join0.root.groupClassType)?"":",";
						f_join0.root.groupFieldName +=  join_table + "." + getColumnName(attr_a[attr_a.length - 1].attr);
						f_join0.root.groupClassType +=  ""+attr_a[attr_a.length - 1].attr.typeClassId;
					}
					if (isgroup_ || !"".equals(grpFunc_) && !"0".equals(grpFunc_)) {
						f_join0.root.groupNumTable+= "".equals(f_join0.root.groupNumTable)?"":",";
						f_join0.root.groupNumTable +=  f_join0.tablNums;
					}

					// Построение условия для значения
					if (isSort) {// Для сортировки помеченных атрибутов
						String str_par = join_table + "." + getColumnName(attr_a[attr_a.length - 1].attr);
						if(attr_a[attr_a.length - 1].attr.isMultilingual){
							if(language.equals("LANGUAGE")){
								str_par=str_par+"_"+language;
							}else{
								str_par = join_table + "." + getColumnName(attr_a[attr_a.length - 1].attr, Long.valueOf(language));
							}
						}					
						f_join0.root.sql_sort += (f_join0.root.sql_sort.equals("") ? "" : ",")
													+str_par	+ " " + order_;
						f_join0.root.sql_head += ", " +str_par;
						
						f_join0.root.sql_sort = checkEncrypt(f_join0.root.sql_sort, str_par, attr_a[attr_a.length - 1].attr);
						f_join0.root.sql_head = checkEncrypt(f_join0.root.sql_head, str_par, attr_a[attr_a.length - 1].attr);
					} else {
						if (dataSelect_ == 3) {
							// Установка атрибута как параметра
							String name_col = join_table + "." + getColumnName(attr_a[attr_a.length - 1].attr);
									if(attr_a[attr_a.length - 1].attr.isMultilingual){
										if(language.equals("LANGUAGE")){
											name_col=name_col+"_LANGUAGE";
										}else{
											name_col=name_col+"_"+ getSystemLangIndex(Long.valueOf(language));
										}
									}
							String rightStr_ = getStringParam(e
									.getChild("valFlr"), "exprFlr");
							sqlParMap.put(rightStr_.trim(), name_col);
						} else if (dataSelect_ == 2) {
							KrnAttribute attr=attr_a[attr_a.length - 1].attr;
							String colName =  getColumnName(attr);
							if(attr_a.length>1){
							KrnAttribute attr_=attr_a[attr_a.length - 2].attr;
								//исключить дублирование
								if(attr.classId!=attr_.classId 
										&& f_join0.join.indexOf(getClassTableName(attr.classId)+ " "+join_table + colName)<0){
	                        		String str_join = " LEFT JOIN "
										+ getClassTableName(attr.classId)
										+ " "+join_table + colName
										+ " ON "
										+ join_table
										+ ".c_obj_id = "
										+ join_table + colName
										+ ".c_obj_id"
	                                    + " AND " + join_table + colName + ".c_is_del=0 "
										+ (trans == 0 ? "AND " + join_table + colName
										+ ".c_tr_id=0 " : "");
	
									f_join0.join += str_join;
								}
							}
								// Сравнение с атрибутом
							        String lang_name="";
                                    if(attr_a[attr_a.length - 1].attr.isMultilingual){
                                        if(language.equals("LANGUAGE")){
                                            lang_name="_LANGUAGE";
                                        }else{
                                            lang_name="_"+ getSystemLangIndex(Long.valueOf(language));
                                        }
                                    }
								sql_mean += "{" + num_table + " ";
								sql_mean +=   getSqlComp(
										e,
										attr_a[attr_a.length - 1].attr.typeClassId >= 99 ? (
												join_table + colName + "."+colName)
												: (join_table + "." + colName+lang_name), sqlParMap);
								if(sql_mean.indexOf("[%")<0){
									sql_mean += "[%]";
								}
								sql_mean += "|" + f_join0.tablNums + "|}"
								+ num_table;
								f_obj.root.sql_mean += sql_mean;
						} else if (!operator_.equals("0")
								&& !operator_.equals("")) {
							String colName = getColumnName(attr_a[attr_a.length - 1].attr);
							if(attr_a[attr_a.length - 1].attr.rAttrId>0)
								colName= "c_obj_id";
							if (dataSelect_ == 1 ||!"".equals(linkPar_)) {
								sql_mean = "{" + num_table + " "+sql_mean;
							}
								if (attr_a[attr_a.length - 1].attr.typeClassId >= 99 
										&& (!(attr_a[attr_a.length - 1].attr.rAttrId>0 
												|| (operator.equals("существует") 
														|| operator.equals("не существует")))
												||(attr_a.length > 1 && attr_a[attr_a.length - 1].attr.rAttrId == 0 
														&& attr_a[attr_a.length - 2].attr.typeClassId != attr_a[attr_a.length - 1].attr.classId 
														&& attr_a[attr_a.length - 1].attr.collectionType==0
														&& (operator.equals("существует") 
																|| operator.equals("не существует"))														)
												)
										) {
									if (f_join0.position > 0
											&& !f_join0.root.equals(f_join0)) {
										// TODO для аналогичного атрибута но
										// без максиндекса
									} else {
										if (f_join0.tablNumsObj.equals("") || f_join0.tablNumsObj.indexOf(colName) < 0) {
											f_join0.tablNumsObj = f_join0.tablNumsObj + (!f_join0.tablNumsObj.equals("") 
															? ","
															: "")
													+ getColumnName(attr_a[attr_a.length - 1].attr);
											if (attr_a.length > 1
													&& attr_a[attr_a.length - 2].attr.typeClassId != attr_a[attr_a.length - 1].attr.classId) {
												if(attr_a[attr_a.length - 1].attr.collectionType==0){
													String t_name= "o"	+ join_table + colName;
													String joinObj_ = "LEFT JOIN "
															+ getClassTableName(attr_a[attr_a.length - 1].attr.classId)
															+ " "+ t_name
															+ " ON "
															+ t_name	+ ".c_obj_id = "
															+ (f_join0.join_attr
																	.equals("") ? join_table
																	+ ".c_obj_id"
																	: f_join0.join_attr)
	                                                        + (f_join0.join_attr
	                                                                .equals("") ?" AND " + t_name + ".c_tr_id= "
	                                                                +join_table + ".c_tr_id "
	                                                                :" ");
													if (f_join0.joinObj.equals("")
															|| f_join0.joinObj
																	.indexOf(joinObj_) < 0) {
														f_join0.joinObj += joinObj_;
													}
												}
											} else if (attr_a[attr_a.length - 1].attr.collectionType >0) {
                                                if(attr_a[attr_a.length - 1].attr.rAttrId>0){
                                                    f_join0.joinObj += "LEFT JOIN "
                                                            + getClassTableName(attr_a[attr_a.length - 1].attr.typeClassId)
                                                            + " o"
                                                            + join_table
                                                            + colName
                                                            + " ON o"
                                                            + join_table
                                                            + colName
                                                            + ".c_obj_id = "
                                                            + join_table
                                                            + ".c_obj_id "
                                                            + (trans==0?"AND o"+ join_table+ colName+ ".c_tr_id = 0 ":" ");
                                                }
                                            } else if(attr_a[attr_a.length - 1].attr.rAttrId==0){
											}
											f_join0.tipeClassJoinObj += (f_join0.tipeClassJoinObj
													.equals("") ? "" : ",")
													+ attr_a[attr_a.length - 1].attr.typeClassId;
										}
									}
								}
								String str_par;
								if (attr_a[attr_a.length - 1].attr.typeClassId >= 99) {
									KrnAttribute attr=db.getAttributeById(attr_a[attr_a.length - 1].attr.rAttrId);
                                    if( attr_a[attr_a.length - 1].attr.rAttrId>0 &&
                                    		(attr_a[attr_a.length - 1].attr.collectionType==0 || attr.collectionType==0)
                                    		/*&& (attr_a[attr_a.length - 1].attr.collectionType==0 
                                    				|| (attr_a.length>1 && attr_a[attr_a.length - 2].attr.rAttrId>0))*/){
                                    	if(operator.equals("существует") || operator.equals("не существует")){
                                    		colName=getRevColumnName(attr_a[attr_a.length - 1].attr);
                                    		str_par = 
                                    				((attr.classId != attr_a[attr_a.length - 1].attr.typeClassId
                                    				|| attr.collectionType > 0) ? "tr"+ t_ind : join_table ) + "." + colName;
                                    	}else
                                    		str_par=join_table+".c_obj_id";
                                    }else if (attr_a.length > 1 && attr_a[attr_a.length - 1].attr.rAttrId == 0 
											&& attr_a[attr_a.length - 2].attr.typeClassId != attr_a[attr_a.length - 1].attr.classId 
											&& attr_a[attr_a.length - 1].attr.collectionType==0) {
                                        str_par = "o"+join_table +colName+"." +colName;
                                    }else if (attr_a[attr_a.length - 1].attr.collectionType>0 && attr_a[attr_a.length - 1].attr.rAttrId == 0) {
                                        str_par = "t"+t_ind +".c_obj_id" ;
                                    }else{
                                        str_par = join_table +"." +colName;
                                    }
								} else if (attr_a[attr_a.length - 1].attr.typeClassId == 10
										&& (dataSelect_ == 1 || !"".equals(linkPar_))
										&& operator.equals("содержит")) {
									str_par = join_table + ".c_uid" + "|"
											+ colName;
								} else {
									boolean par = false, par_ = false;
									if (attr_a.length > 2) {
										par = attr_a[attr_a.length - 2].attr.collectionType > 0;
									}
									if (!f_join0.tipeClassJoinObj.equals("")) {
										StringTokenizer st = new StringTokenizer(
												f_join0.tipeClassJoinObj, ",");
										while (st.hasMoreTokens()) {
											String str_t = st.nextToken();
											par_ = str_t
													.equals(""
															+ attr_a[attr_a.length - 1].attr.classId);
											if (par_)
												break;
										}
									}
									if ((par
											&& attr_a[attr_a.length - 1].attr.typeClassId >= 99 
											&& attr_a[attr_a.length - 2].attr.typeClassId == attr_a[attr_a.length - 1].attr.classId)
											|| (par_ && attr_a.length>1) && attr_a[attr_a.length - 2].attr.rAttrId==0) {
										str_par = "o"
												+ join_table
												+ getColumnName(attr_a[attr_a.length - 2].attr)
												+ "."
												+ getColumnName(attr_a[attr_a.length - 1].attr);
									} else {
										str_par = join_table
												+ "."
												+ getColumnName(attr_a[attr_a.length - 1].attr);
									}
								}
								if(attr_a[attr_a.length - 1].attr.isMultilingual){
									if(language.equals("LANGUAGE")){
										sql_mean += getSql(e,
												attr_a[attr_a.length - 1].attr.typeClassId,
												str_par+"_"+language,attr_a[attr_a.length - 1].attr, sqlParMap);
									}else if(language.contains(",")){
										int m=0;
										String str_par_="";
										while(true){
											if (language.indexOf(",", m) > 0) {
												String val = language.substring(m, language.indexOf(",",
														m));
												str_par_+= str_par+"_"+getSystemLangIndex(Long.valueOf(val))+",";
												m=language_.indexOf(",", m)+1;
											} else {
												String val = language.substring(m);
												str_par_+= str_par+"_"+getSystemLangIndex(Long.valueOf(val));
												break;
											}
											
										}
										sql_mean += getSql(e,
												attr_a[attr_a.length - 1].attr.typeClassId,
												str_par_,attr_a[attr_a.length - 1].attr, sqlParMap);
									}else{
										str_par += "_" + getSystemLangIndex(Long.valueOf(language));
										sql_mean += getSql(e,
												attr_a[attr_a.length - 1].attr.typeClassId,
												str_par,attr_a[attr_a.length - 1].attr, sqlParMap);
									}
									
								}else
								sql_mean += getSql(e,
										attr_a[attr_a.length - 1].attr.typeClassId,
										str_par,attr_a[attr_a.length - 1].attr, sqlParMap);
							if (dataSelect_ == 1 || !"".equals(linkPar_)) {
								if(sql_mean.indexOf(" OR ") > 0){
									int m=0;
									String sql_mean_="";
									while(true){
										if (sql_mean.indexOf(" OR ", m) > 0) {
											m=sql_mean.indexOf(" OR ", m);
											sql_mean_ += sql_mean.substring(0,m)+"|" + f_join0.tablNums + "|}"+ num_table +" OR {"+num_table;
											m+=4;
										} else {
											sql_mean_ += sql_mean.substring(m);
											break;
										}
									}
									sql_mean=sql_mean_;
								}
							}
							if (dataSelect_ == 1 || !"".equals(linkPar_)) {
									sql_mean += "|" + f_join0.tablNums + "|}"+ num_table;
							}
							f_join0.root.sql_mean += sql_mean;
						} 
						if (!kolObj_.equals("")) {
							sql_mean="";
							if (dataSelect_ == 1 || !"".equals(linkPar_)) {
								sql_mean += "{" + num_table + " ";
							}
							sql_mean += "tm"
									+ attr_a[attr_a.length - 1].attr.classId
									+ "_"
									+ t_ind
									+ ".c_i_"
									+ (kolObj_.equals("0") && (kolOp_.equals("") || kolOp_.equals("0") || kolOp_.equals("1")) ? " IS NULL "
											: ((!kolOp_.equals("") && !kolOp_.equals("0")) ? operMap.get(kolOp_) : "")
													+ kolObj_) + " ";

							if (dataSelect_ == 1 || !"".equals(linkPar_)) {
								sql_mean +="}"+ num_table;
							}
							f_obj.root.sql_mean += sql_mean;
						}
					}
                    if(path_a!=null && path_a.length>2){//Для атрибутов а угловых скобках(кастинг),
                    	            //чтобы выбор шел среди объектов выбранных 
                    	            //из условий для обычных атрибутов
                        PathElement2 p=path_a[path_a.length-1];
                        if(p.attr!=null && p.attr.typeClassId!=p.type.id){
                            String p_table="o";
                            int last_ind = f_obj.tablNums.lastIndexOf(",");
                            num_table = f_obj.tablNums.substring(last_ind + 1);
                            if (!num_table.equals("0"))
                                p_table = "t" + num_table;
    						else
    							p_table="o";
                            if(!join_table.equals(p_table))
                            	f_obj.root.sql_mean +=  join_table+".c_obj_id = "+p_table+".c_obj_id  AND ";
                        }
                    }
					if (maxIndpar) {
						f_join0.nodeMaxInd = left_;
						f_join0.union = union_;
					}

					if (independ) {
						f_join0.nodeIndepend = left_;
						f_join0.union = union_;
					}
					//Если какое то из условий не функция, то у родительского присоединения тоже не должна быть функция
					if(f_obj.isFunc && !f_join0.isFunc && f_join0.tablNums.indexOf(f_obj.tablNums)>-1)
						f_obj.isFunc=false;
					return f_join0;//
				}
			} catch (Exception ex) {
				// Игнорировать корявый фильтр
				log.warn("ОШИБКА: фильтр id = " + filterId + "  left=" + left_
						+ " " + ex.getMessage());
				log.error(ex, ex);
			}
		}
		return f_obj;
	}
	
	protected String checkEncrypt(String sql, String colName, KrnAttribute attr) {
		return sql;
	}
	
    protected String getTableName(String t_name,FilterObj root){
    	String res=t_name;
    	int i=0;
    	if(root.tName==null)
    		root.tName=new Vector();
    	while(true){
    		if(root.tName.contains(res)){
    			res=t_name+"_"+i;
    		}else
    			break;
    		i++;
    	}
    	root.tName.add(res);
    	return res;
    }
    protected List getLinkChild(String linkFlr_,long trId,Session session) {
    	Element root=session.getXmlForFilter(linkFlr_, trId);
		return (root.getChild("children") != null ? root.getChild("children").getContent() : null);
    }
	protected String getStringParam(Element e, String name) {
		Element ee, eee = null;
		if (e != null && (ee = e.getChild(name)) != null) {
			if ((name.equals("krnObjFlr") || name.equals("language"))
					&& (eee = ee.getChild("KrnObject")) != null) {
				return eee.getAttribute("id").getValue();
			} else if (name.equals("linkFlr") && (eee = ee.getChild("KrnObjectItem")) != null) {//Ссылка на фильтр
					return eee.getAttribute("id").getValue();
			} else
				return ee.getText();
		} else
			return "";
	}

	protected int getIntParam(Element e, String name) {
		String res = getStringParam(e, name);
		return (res != null && res.length() > 0) ? Integer.parseInt(res) : 0;
	}

	protected String getSql(Element xml, long typeAttr, String colName,KrnAttribute attr, Map<String, String> sqlParMap) {
		if (typeAttr == CID_STRING)
			return getSqlString(xml, colName);
		else if (typeAttr == CID_MEMO)
			return getSqlMemo(xml, colName);
		else if (typeAttr == CID_INTEGER)
			return getSqlInteger(xml, colName);
		else if (typeAttr == CID_FLOAT)
			return getSqlFloat(xml, colName);
		else if (typeAttr == CID_BOOL)
			return getSqlBoolean(xml, colName);
		else if (typeAttr == CID_BLOB)
			return getSqlBlob(xml, colName,attr);
		else if (typeAttr == CID_DATE)
			return getSqlDate(xml, colName, sqlParMap);
		else if (typeAttr == CID_TIME)
			return getSqlTime(xml, colName, sqlParMap);
		else
			return getSqlObject(xml, colName);

	}

	protected String getSqlString(Element xml, String colName) {
		String operator_ = operMap.get(getStringParam(xml, "operFlr")), sql_mean_ = "", val_;
		String num_ = getStringParam(xml, "compFlr");
        String linkPar_ = getStringParam(xml, "linkPar");
        String likeEscape = getStringParam(xml, "likeEscape");
		int le = likeEscape.equals("") ? 0 : Integer.parseInt(likeEscape);
       	likeEscape= le>0?" ESCAPE"+le:"";
       	String likeEscape_= le>0?" ESCAPE '~'":"";
        if(likeEscape_!=null && !"".equals(likeEscape_))
        	likeEscape_=" ESCAPE '"+likeEscape_+"' ";
		int dataSelect_ = num_.equals("") ? 0 : Integer.valueOf(num_);
		boolean mandatory_ = getStringParam(xml, "mandatoryFlr").equals("true");
		String rightStr_ = getStringParam(xml.getChild("valFlr"), "exprFlr");
		boolean resp = getStringParam(xml, "respReg").equals("true");
		if(resp){
			String[] colNames=colName.split(",");
			colName="";
			for(String cn:colNames){
				colName += ("".equals(colName)?"lower(" + cn + ")":",lower(" + cn + ")");
			}
		}
		if (dataSelect_ == 1 && rightStr_ != null && !rightStr_.equals(""))
			rightStr_ = rightStr_.trim();
		if (operator_ == null || operator_.equals(""))
			return "";
		if (operator_.equals(Constants.OP_CONTAIN)) {
			if (dataSelect_ == 1){
				if(colName.contains(",")){
					String vals="";
						int m=0;
					while(true){
						if (colName.indexOf(",", m) > 0) {
							vals += (vals.equals("")?"(":" OR ")+"[" + rightStr_ + "Like%%" +colName.substring(m, colName.indexOf(",",m))+ (mandatory_?"+":"") +likeEscape+ "]";
							m=colName.indexOf(",", m)+1;
						} else {
							vals += " OR [" + rightStr_ + "Like%%" +colName.substring(m)+ (mandatory_?"+":"")+likeEscape+ "])";
							break;
						}
					}
					sql_mean_ += vals;
					return sql_mean_;
				}else
				val_ = " [" + rightStr_ + "Like%%" + colName + (mandatory_?"+":"")+likeEscape+ "]";
			}else{
				val_ = " Like '%" + rightStr_ + "%'"+likeEscape_;
			}
        } else if (operator_.equals(Constants.OP_NOT_CONTAIN)) {
            if (dataSelect_ == 1)
                val_ = " [" + rightStr_ + "LikeNot%%" + colName + (mandatory_?"+":"") +likeEscape+ "]";
            else
                val_ = "Not Like '%" + rightStr_ + "%'"+likeEscape_;
		} else if (operator_.equals(Constants.OP_START_WITH)) {
			if (dataSelect_ == 1){
				if(colName.contains(",")){
					String vals="";
						int m=0;
					while(true){
						if (colName.indexOf(",", m) > 0) {
							vals += (vals.equals("")?"(":" OR ")+"[" + rightStr_ + "Like%" +colName.substring(m, colName.indexOf(",",m))+ (mandatory_?"+":"")+likeEscape+ "]";
							m=colName.indexOf(",", m)+1;
						} else {
							vals += " OR [" + rightStr_ + "Like%" +colName.substring(m)+ (mandatory_?"+":"")+likeEscape+ "])";
							break;
						}
					}
					sql_mean_ += vals;
					return sql_mean_;
				}else
				val_ = " [" + rightStr_ + "Like%" + colName + (mandatory_?"+":"")+likeEscape+ "]";
			}else{
				val_ = " Like '" + rightStr_ + "%'"+likeEscape_;
			}
		} else if (operator_.equals(Constants.OP_FINISH_ON)) {
			if (dataSelect_ == 1){
				if(colName.contains(",")){
					String vals="";
						int m=0;
					while(true){
						if (colName.indexOf(",", m) > 0) {
							vals += (vals.equals("")?"(":" OR ")+"[" + rightStr_ + "Like%%%" +colName.substring(m, colName.indexOf(",",m))+ (mandatory_?"+":"")+likeEscape+ "]";
							m=colName.indexOf(",", m)+1;
						} else {
							vals += " OR [" + rightStr_ + "Like%%%" +colName.substring(m)+ (mandatory_?"+":"")+likeEscape+ "])";
							break;
						}
					}
					sql_mean_ += vals;
					return sql_mean_;
				}else
				val_ = " [" + rightStr_ + "Like%%%" + colName +likeEscape+ "]";
			}else{
				val_ = " Like '%" + rightStr_ + "'"+likeEscape_;
			}
		} else if (operator_.equals("включает")) {
			if (dataSelect_ == 1)
				val_ = " IN ([" + rightStr_ + (mandatory_?"+":"")+ "])";
			else
				val_ = " IN (" + rightStr_ + ")";
		} else if (operator_.equals("другой")) {
			val_ = rightStr_;
		} else if (operator_.equals("существует")) {
			sql_mean_ += colName + " IS NOT NULL ";
			if (rightStr_ != null && rightStr_.length() > 1)
				sql_mean_ += "[" + rightStr_ + "]";
			return sql_mean_;
		} else if (operator_.equals("не существует")) {
			sql_mean_ += colName + " IS NULL";
			if (rightStr_ != null && rightStr_.length() > 1)
				sql_mean_ += "[" + rightStr_ + "]";
			return sql_mean_;
		} else {
			if (dataSelect_ == 1)
				val_ = operator_ + "[" + rightStr_ + (mandatory_?"+":"")+ "]";
			else
				val_ = operator_ + "'" + rightStr_.trim() + "'";
		}
		if (dataSelect_ == 1
				&& (operator_.equals(Constants.OP_CONTAIN) 
						|| operator_.equals(Constants.OP_NOT_CONTAIN) 
						|| operator_.equals(Constants.OP_START_WITH) 
						|| operator_.equals(Constants.OP_FINISH_ON))) {
			assert val_ != null;
			sql_mean_ += val_.trim();
        }else if(!"".equals(linkPar_)){
            assert val_ != null;
            sql_mean_ += colName + " " + val_.trim()+"[" + linkPar_ + "%]";
        } else {
			assert val_ != null;
			if(colName.contains(",")){
				String vals="";
					int m=0;
				while(true){
					if (colName.indexOf(",", m) > 0) {
						vals += (vals.equals("")?"(":" OR ")+colName.substring(m, colName.indexOf(",",m))+ " " + val_.trim();
						m=colName.indexOf(",", m)+1;
					} else {
						vals += " OR " + colName.substring(m)+ " " + val_.trim()+")";
						break;
					}
				}
				sql_mean_ += vals;
			}else
				sql_mean_ += colName + " " + val_.trim();
		}
		return sql_mean_;
	}

	protected String getSqlMemo(Element xml, String colName) {
		String operator_ = operMap.get(getStringParam(xml, "operFlr")), sql_mean_ = "", val_;
		String num_ = getStringParam(xml, "compFlr");
		int dataSelect_ = num_.equals("") ? 0 : Integer.parseInt(num_);
		boolean mandatory_ = getStringParam(xml, "mandatoryFlr").equals("true");
		String rightStr_ = getStringParam(xml.getChild("valFlr"), "exprFlr");
        String linkPar_ = getStringParam(xml, "linkPar");
		if (dataSelect_ == 1 && rightStr_ != null && !rightStr_.equals(""))
			rightStr_ = rightStr_.trim();
		if (operator_ == null || operator_.equals(""))
			return "";
		if (operator_.equals("=")) {
			if (dataSelect_ == 1)
				val_ = " Like [" + rightStr_ + (mandatory_?"+":"")+ "]";
			else
				val_ = "Like '" + rightStr_ + "'";
		} else if (operator_.equals(Constants.OP_CONTAIN)) {
			if (dataSelect_ == 1)
				val_ = " [" + rightStr_ + "Like%%" + colName + (mandatory_?"#+":"#") + "]";
			else
				val_ = "Like '%" + rightStr_ + "%'";
        } else if (operator_.equals(Constants.OP_NOT_CONTAIN)) {
            if (dataSelect_ == 1)
                val_ = " [" + rightStr_ + "LikeNot%%" + colName + (mandatory_?"#+":"#") + "]";
            else
                val_ = "Not Like '%" + rightStr_ + "%'";
		} else if (operator_.equals(Constants.OP_START_WITH)) {
			if (dataSelect_ == 1)
				val_ = " [" + rightStr_ + "Like%" + colName + (mandatory_?"#+":"#") + "]";
			else
				val_ = "Like '" + rightStr_ + "%'";
		} else if (operator_.equals(Constants.OP_FINISH_ON)) {
			if (dataSelect_ == 1)
				val_ = " [" + rightStr_ + "Like%%%" + colName + (mandatory_?"#+":"#") + "]";
			else
				val_ = "Like '%" + rightStr_ + "'";
		} else if (operator_.equals("другой")) {
			val_ = rightStr_;
		} else if (operator_.equals("существует")) {
			sql_mean_ += colName + " IS NOT NULL ";
			return sql_mean_;
		} else if (operator_.equals("не существует")) {
			sql_mean_ += colName + " IS NULL";
			return sql_mean_;
		} else {
			if (dataSelect_ == 1)
				val_ = operator_ + "[" + rightStr_ + (mandatory_?"#+":"#") + "]";
			else
				val_ = operator_ + "'" + rightStr_.trim() + "'";
		}
		if (dataSelect_ == 1
				&& (operator_.equals(Constants.OP_CONTAIN) || operator_
						.equals(Constants.OP_START_WITH))) {
			assert val_ != null;
			sql_mean_ += val_.trim();
		} else {
			assert val_ != null;
			if(colName.contains(",")){
				String vals="";
					int m=0;
				while(true){
					if (colName.indexOf(",", m) > 0) {
						vals += (vals.equals("")?"(":" OR ")+colName.substring(m, colName.indexOf(",",m))+ " " + val_.trim();
						m = colName.indexOf(",", m)+1;
					} else {
						vals += " OR " +colName.substring(m)+ " " + val_.trim()+")";
						break;
					}
				}
				sql_mean_ += vals;
			}else
				sql_mean_ += colName + " " + val_.trim();
		}
        if(!"".equals(linkPar_))
            sql_mean_ += "[" + linkPar_ + "%]";
		return sql_mean_;
	}

	protected String getSqlInteger(Element xml, String colName) {
		String operator_ = operMap.get(getStringParam(xml, "operFlr")), sql_mean_ = "";
		String num_ = getStringParam(xml, "compFlr");
		int dataSelect_ = num_.equals("") ? 0 : Integer.parseInt(num_);
		boolean mandatory_ = getStringParam(xml, "mandatoryFlr").equals("true");
		String rightStr_ = getStringParam(xml.getChild("valFlr"), "exprFlr");
        String linkPar_ = getStringParam(xml, "linkPar");
		if (dataSelect_ == 1 && rightStr_ != null && !rightStr_.equals(""))
			rightStr_ = rightStr_.trim();
		String val_ = operator_ + rightStr_;
		if (operator_ == null || operator_.equals(""))
			return "";
		if (operator_.equals("существует")) {
			if (dataSelect_ == 1)
				val_ = "[" + rightStr_ + (mandatory_?"+":"")+ "]";
			else
				val_ = " IS NOT NULL";
			sql_mean_ += colName + val_;
		} else if (operator_.equals("не существует")) {
			sql_mean_ += colName + " IS NULL";
		} else {
			if (dataSelect_ == 1)
				val_ = operator_ + "[" + rightStr_ + (mandatory_?"+":"") + "]";
			sql_mean_ += colName + val_.trim();
		}
        if(!"".equals(linkPar_))
           sql_mean_ += "[" + linkPar_ + "%]";
		return sql_mean_;
	}

	private String getSqlFloat(Element xml, String colName) {
		String operator_ = operMap.get(getStringParam(xml, "operFlr")), sql_mean_ = "", val_;
		String num_ = getStringParam(xml, "compFlr");
		int dataSelect_ = num_.equals("") ? 0 : Integer.parseInt(num_);
		boolean mandatory_ = getStringParam(xml, "mandatoryFlr").equals("true");
		String rightStr_ = getStringParam(xml.getChild("valFlr"), "exprFlr");
        String linkPar_ = getStringParam(xml, "linkPar");
		if (dataSelect_ == 1 && rightStr_ != null && !rightStr_.equals(""))
			rightStr_ = rightStr_.trim();
		if (operator_ == null || operator_.equals(""))
			return "";
		if (operator_.equals("существует")) {
			if (dataSelect_ == 1)
				val_ = "[" + rightStr_ + (mandatory_?"+":"") + "]";
			else
				val_ = "";
			sql_mean_ += "(" + colName + " IS NOT NULL " + val_ + ") ";
		} else if (operator_.equals("не существует")) {
			sql_mean_ += colName + " IS NULL";
		} else {
			if (dataSelect_ == 1)
				val_ = operator_ + "[" + rightStr_ + (mandatory_?"+":"") + "]";
			else
				val_ = operator_ + rightStr_;
			sql_mean_ += colName + val_.trim();
		}
        if(!"".equals(linkPar_))
            sql_mean_ += "[" + linkPar_ + "%]";

        return sql_mean_;
	}

	protected String getSqlBoolean(Element xml, String colName) {
		String operator_ = operMap.get(getStringParam(xml, "operFlr")), sql_mean_ = "", val_;
		String num_ = getStringParam(xml, "compFlr");
		int dataSelect_ = num_.equals("") ? 0 : Integer.parseInt(num_);
		boolean mandatory_ = getStringParam(xml, "mandatoryFlr").equals("true");
		String rightStr_ = getStringParam(xml.getChild("valFlr"), "exprFlr");
        String linkPar_ = getStringParam(xml, "linkPar");
		if (dataSelect_ == 1 && rightStr_ != null && !rightStr_.equals(""))
			rightStr_ = rightStr_.trim();
		if (operator_ == null || operator_.equals(""))
			return "";
		else if (operator_.equals("существует")) {
			if (dataSelect_ == 1)
				val_ = "AND " + colName + " = 1 [" + rightStr_ + (mandatory_?"+":"") + "]";
			else
				val_ = "";
			sql_mean_ += colName + " IS NOT NULL " + val_;
		} else if (operator_.equals("не существует")) {
			sql_mean_ += colName + " IS NULL ";
		} else {
			if (dataSelect_ == 1)
				val_ = operator_ + "[" + rightStr_ + (mandatory_?"+":"") + "]";
			else
				val_ = operator_
						+ ((rightStr_.toLowerCase(Constants.OK).trim().equals("true") || rightStr_
								.toLowerCase(Constants.OK).trim().equals("1")) ? "1" : "0");
			sql_mean_ += colName + val_.trim();
		}
        if(!"".equals(linkPar_))
            sql_mean_ += "[" + linkPar_ + "%]";
		return sql_mean_;
	}

	private String getSqlBlob(Element xml, String colName,KrnAttribute attr) {
		String operator_ = operMap.get(getStringParam(xml, "operFlr")), sql_mean_ = "", val_ = "";
		if (operator_ == null || operator_.equals(""))
			return "";
		String num_ = getStringParam(xml, "compFlr");
		int dataSelect_ = num_.equals("") ? 0 : Integer.parseInt(num_);
		boolean mandatory_ = getStringParam(xml, "mandatoryFlr").equals("true");
		String rightStr_ = getStringParam(xml.getChild("valFlr"), "exprFlr");
        String linkPar_ = getStringParam(xml, "linkPar");
		if (operator_.equals("существует"))
			sql_mean_ += colName + " IS NOT NULL";
		else if (operator_.equals("не существует")) {
			sql_mean_ += colName + " IS NULL";
		} else if (operator_.equals("содержит") && dataSelect_ == 1) {
			String lg=colName.substring(colName.lastIndexOf("_"));
			sql_mean_ += colName.substring(0, colName.indexOf("|")) + " IN ["
					+ rightStr_ + "|" + colName + "|cm"+attr.id+lg+"##]";
		}
		return sql_mean_;
	}

	private String getSqlDate(Element xml, String colName, Map<String, String> sqlParMap) {
		String operator_ = operMap.get(getStringParam(xml, "operFlr")), sql_mean_ = "", val_;
		String num_ = getStringParam(xml, "compFlr");
		int dataSelect_ = num_.equals("") ? 0 : Integer.parseInt(num_);
		boolean mandatory_ = getStringParam(xml, "mandatoryFlr").equals("true");
		String rightStr_ = getStringParam(xml.getChild("valFlr"), "exprFlr");
        String linkPar_ = getStringParam(xml, "linkPar");
		if (dataSelect_ == 1 && rightStr_ != null && !rightStr_.equals(""))
			rightStr_ = rightStr_.trim();
		if (operator_ == null || operator_.equals(""))
			return "";
		if (operator_.equals("существует")) {
			if (dataSelect_ == 1)
				val_ = "[" + rightStr_ + (mandatory_?"+":"") + "]";
			else
				val_ = "";
			sql_mean_ += colName + " IS NOT NULL " + val_;
		} else if (operator_.equals("не существует")) {
			sql_mean_ += colName + " IS NULL";
		}
		else if(operator_.equals("включает"))
		{
		   String oper_ = " IN ";
		    val_ = oper_ + "([" + rightStr_ + (mandatory_?"+":"") + "])";
		    sql_mean_ += colName + " " + val_.trim();
		}
		else if(operator_.equals("исключает"))
		{        String oper_ = "NOT IN ";
                val_ = oper_ + "([" + rightStr_ + (mandatory_?"+":"") + "])";
                sql_mean_ += colName + " " + val_.trim();}
		    
		    else {
			if (dataSelect_ == 1){
                String str_attr="";
                if(sqlParMap.size()>0){
                    for (String s : sqlParMap.keySet()) {
                        String val = sqlParMap.get(s);
                        assert rightStr_ != null;
                        rightStr_ = rightStr_.replaceAll(s, val);
                    }
                    if(rightStr_.indexOf("%")>0){
                        str_attr=rightStr_.substring(0,rightStr_.indexOf("%"));
                        rightStr_=rightStr_.substring(rightStr_.indexOf("%"));
                    }
                }
            	String operator_1=operator_;
                if("другой".equals(operator_)){
                	operator_1="";
                }
                val_ = operator_1 + str_attr+"[" + rightStr_ + (mandatory_?"$+":"$") + "]";
//				val_ = operator_ + "[" + rightStr_ + "]";
			}else
				val_ = operator_ + rightStr_.trim();
			sql_mean_ += colName + val_.trim();
		}
        if(!"".equals(linkPar_))
            sql_mean_ += "[" + linkPar_ + "%]";
		return sql_mean_;
	}

	private String getSqlTime(Element xml, String colName, Map<String, String> sqlParMap) {
		String operator_ = operMap.get(getStringParam(xml, "operFlr")), sql_mean_ = "", val_;
		String num_ = getStringParam(xml, "compFlr");
		int dataSelect_ = num_.equals("") ? 0 : Integer.parseInt(num_);
		boolean mandatory_ = getStringParam(xml, "mandatoryFlr").equals("true");
		String rightStr_ = getStringParam(xml.getChild("valFlr"), "exprFlr");
        String linkPar_ = getStringParam(xml, "linkPar");
		if (dataSelect_ == 1 && rightStr_ != null && !rightStr_.equals(""))
			rightStr_ = rightStr_.trim();
		if (operator_ == null || operator_.equals(""))
			return "";
		if (operator_.equals("существует")) {
			if (dataSelect_ == 1)
				val_ = "[" + rightStr_ + (mandatory_?"$+":"$") + "]";
			else
				val_ = "";
			sql_mean_ += colName + " IS NOT NULL " + val_;
		} else if (operator_.equals("не существует")) {
			sql_mean_ += colName + " IS NULL";
		}
		else if(operator_.equals("исключает"))
                {        String oper_ = "NOT IN ";
                         val_ = oper_ + "([" + rightStr_ + (mandatory_?"+":"") + "])";
                         sql_mean_ += colName + " " + val_.trim();}
	
		          else if(operator_.equals("включает"))
		                {
		                   String oper_ = " IN ";
		                    val_ = oper_ + "([" + rightStr_ + (mandatory_?"+":"") + "])";
		                    sql_mean_ += colName + " " + val_.trim();
		                }
		                    
		
		else {
			if (dataSelect_ == 1){
                String str_attr="";
                if(sqlParMap.size()>0){
                    for (String s : sqlParMap.keySet()) {
                        String val = sqlParMap.get(s);
                        assert rightStr_ != null;
                        rightStr_ = rightStr_.replaceAll(s, val);
                    }
                    if(rightStr_.indexOf("%")>0){
                        str_attr=rightStr_.substring(0,rightStr_.indexOf("%"));
                        rightStr_=rightStr_.substring(rightStr_.indexOf("%"));
                    }
                }
            	String operator_1=operator_;
                if("другой".equals(operator_)){
                	operator_1="";
                }
                val_ = operator_1 + str_attr+"[" + rightStr_ + (mandatory_?"$+":"$") + "]";
            }else
				val_ = operator_ + rightStr_.trim();
			sql_mean_ += colName + val_.trim();
		}
        if(!"".equals(linkPar_))
            sql_mean_ += "[" + linkPar_ + "%]";
		return sql_mean_;
	}

	protected String getSqlObject(Element xml, String colName) {
		String operator_ = operMap.get(getStringParam(xml, "operFlr")), sql_mean_ = "", val_;
		String num_ = getStringParam(xml, "compFlr");
		int dataSelect_ = num_.equals("") ? 0 : Integer.parseInt(num_);
		boolean mandatory_ = getStringParam(xml, "mandatoryFlr").equals("true");
		String rightObj_ = getStringParam(xml.getChild("valFlr"), "krnObjFlr");
		String oper_ = operator_;
		// Зависимый параметр
        String linkPar_ = getStringParam(xml, "linkPar");
		if (oper_ == null || oper_.equals(""))
			return "";
		else if (oper_.equals("существует"))
			oper_ = "=";
		else if (oper_.equals("не существует"))
			oper_ = "<>";
		else if (oper_.equals("включает"))
			oper_ = " IN ";
		else if (oper_.equals("исключает"))
			oper_ = " NOT IN ";
		if (dataSelect_ == 1) {
			String rightStr_ = getStringParam(xml.getChild("valFlr"), "exprFlr");
			if (rightStr_ != null && !rightStr_.equals(""))
				rightStr_ = rightStr_.trim();
			if (oper_.equals(" IN ") || oper_.equals(" NOT IN "))
				val_ = oper_ + "([" + rightStr_ + (mandatory_?"+":"") + "])";
			else if (operator_.equals("существует"))
				val_ = " IS NOT NULL [" + rightStr_ + (mandatory_?"+":"") + "]";
			else
				val_ = oper_ + "[" + rightStr_ + (mandatory_?"+":"") + "]";
			sql_mean_ += colName + " " + val_.trim();
		} else if (rightObj_ != null && rightObj_.length() > 0) {
			String rightObj = "";
			for (StringTokenizer st = new StringTokenizer(rightObj_, ","); st.hasMoreTokens(); ) {
				String val = st.nextToken();
				try {
					rightObj += getObjectByUid(val,0).id;
				} catch (Exception e) {
					log.error(e, e);
				}
				if (st.hasMoreTokens())
					rightObj += ",";
			}
			if (oper_.equals(" IN ") || oper_.equals(" NOT IN "))
				val_ = oper_ + "(" + rightObj + ")";
			else
				val_ = oper_
						+ (rightObj.indexOf(",") > 0 ? rightObj.substring(0,
								rightObj.indexOf(",")) : rightObj);
			sql_mean_ += colName + val_;
		} else if (operator_.equals("существует"))
			sql_mean_ += colName + " IS NOT NULL";
		else if (operator_.equals("не существует"))
			sql_mean_ += colName + " IS NULL";
		else if (operator_.equals("=")) {
			sql_mean_ += colName + " = 0 ";
		} else if (operator_.equals("<>")) {
			sql_mean_ += colName + " <> 0 ";
		}
        
		if(linkPar_.length() > 0)
            sql_mean_ += "[" + linkPar_ + "%]";
		return sql_mean_;
	}

	private String getSqlComp(Element xml, String colName, Map<String, String> sqlParMap) {
		String operator_ = operMap.get(getStringParam(xml, "operFlr")), sql_mean_;
        String linkPar_ = getStringParam(xml, "linkPar");
		if ("существует".equals(operator_))
			operator_ = " = ";
		else if ("не существует".equals(operator_))
			operator_ = " <> ";
		boolean mandatory_ = getStringParam(xml, "mandatoryFlr").equals("true");
		String rightStr_ = getStringParam(xml.getChild("valFlr"), "exprFlr");
		if (rightStr_ != null && !rightStr_.equals(""))
			rightStr_ = rightStr_.trim();
		for (String s : sqlParMap.keySet()) {
			String val = sqlParMap.get(s);
			assert rightStr_ != null;
			rightStr_ = rightStr_.replaceAll(s, val);
		}
		if (operator_ == null || operator_.equals(""))
			sql_mean_ = " " + rightStr_;
		else
			sql_mean_ = colName + " " + operator_ + " " + rightStr_;
        if(!"".equals(linkPar_))
            sql_mean_ += "[" + linkPar_ + "%]";
		return sql_mean_;
	}

	protected String getIdxName(KrnAttribute attr, long langId) throws DriverException {
		if (attr.isMultilingual) {
			int i = getSystemLangIndex(langId);
			return "idx" + attr.classId + "_" + attr.id + "_" + i;
		}
		return "idx" + attr.classId + "_" + attr.id;
	}

	protected void updateIndex(KrnAttribute attr, boolean isIndexed)
			throws SQLException, DriverException {
		if (attr.typeClassId != PC_BLOB) {
			QueryRunner qr = new QueryRunner(true);
			String tname = (attr.collectionType == COLLECTION_ARRAY || attr.collectionType == COLLECTION_SET) ? getAttrTableName(attr)
					: getClassTableName(attr.classId);
			if (isIndexed) {
				if (attr.isMultilingual) {
					List<KrnObject> langs = getSystemLangs();
					for (KrnObject lang : langs) {
						String idxName = getIdxName(attr, lang.id);
						String cname = getColumnName(attr, lang.id);
						qr.update(conn, "CREATE "+(attr.isFullText()?"FULLTEXT":"")+" INDEX " + idxName + " ON " + tname
								+ "(" + cname + ")");
					}
				} else {
					String idxName = getIdxName(attr, 0);
					String cname = getColumnName(attr, 0);
					qr.update(conn, "CREATE "+(attr.isFullText()?"FULLTEXT":"")+" INDEX " + idxName + " ON " + tname
							+ "(" + cname + ")");
				}
			} else {
				if (attr.isMultilingual) {
					List<KrnObject> langs = getSystemLangs();
					for (KrnObject lang : langs) {
						String idxName = getIdxName(attr, lang.id);
						if(isIndexExists(tname, idxName))
							qr.update(conn, getDropIndexSql(tname, idxName));
					}
				} else {
					String idxName = getIdxName(attr, 0);
					if(isIndexExists(tname, idxName))
						qr.update(conn, getDropIndexSql(tname, idxName));
				}
			}
		}
	}
	
	public void updateAllTriggers() throws DriverException {
		List<KrnClass> clss = new ArrayList<KrnClass>();
		db.getSubClasses(99, true, clss);
		for (KrnClass cls : clss) {
			if (cls.id != ROOT_CLASS_ID && !cls.isVirtual())
				try {
					updateTriggers(cls.id);
				} catch (SQLException e) {
					throw new DriverException(e);
				}
		}
	}
	
	protected void updateTriggers(long classId)
	throws SQLException, DriverException {
		KrnClass cls = db.getClassById(classId);
		String itgName = getInsertTriggerName(cls.id);
		String utgName = getUpdateTriggerName(cls.id);
		
		Statement st = conn.createStatement();
		try {
			// Удаляем старые триггеры (если существуют)
			st.executeUpdate("DROP TRIGGER IF EXISTS " + itgName);
			st.executeUpdate("DROP TRIGGER IF EXISTS " + utgName);
		} catch (SQLException e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(st);
		}

		List<KrnAttribute> attrs = db.getAttributesByClassId(classId, false);
		createTriggers(cls, attrs);
	}

	protected void createTriggers(KrnClass cls, List<KrnAttribute> attrs)
	throws SQLException, DriverException {
		// Для мультиязычных атрибутов пока проверяем обязательность только
		// на 1-ом системном языке
		long langId = getSystemLangs().get(0).id;

		String ctName = Funcs.sanitizeSQL(getClassTableName(cls.id));
		String itgName = getInsertTriggerName(cls.id);
		String utgName = getUpdateTriggerName(cls.id);

		boolean create = false;
		String sql = "CREATE TRIGGER " + itgName + " BEFORE INSERT ON " + ctName +
				" FOR EACH ROW BEGIN\n" +
				" \tIF NEW.c_tr_id=0 THEN\n";
		String usql = "CREATE TRIGGER " + utgName + " BEFORE UPDATE ON " + ctName +
				" FOR EACH ROW BEGIN\n";

		Map<MultiKey, List<Long>> m = triggerExcept.get(dsName);

		for (KrnAttribute a : attrs) {
			if (!a.isMandatory())
				continue;
			create = true;
			String cmName = Funcs.sanitizeSQL(getColumnName(a, langId));
			
			String sqlExcept = null;
			MultiKey key = new MultiKey(cls.id, a.id);
			if (m != null && m.containsKey(key)) {
				List<Long> child_ids = m.get(key);
				if (child_ids != null && child_ids.size() > 0) {
					sqlExcept = " AND NEW.c_class_id NOT IN (";
					for (Long child_id : child_ids) {
						sqlExcept += child_id + ",";
					}
					sqlExcept = sqlExcept.substring(0, sqlExcept.length() - 1) + ")";
				}
			}

			sql += "\t\tIF ((NEW." + cmName + " IS NULL";
			if (a.typeClassId == PC_STRING) // string - проверяем на пустую строку
				sql += " OR LENGTH(NEW." + cmName + ")=0";
			sql += ")";
			
			if (sqlExcept != null) sql += sqlExcept;

			sql += ") THEN\n";
			sql += "\t\t\tSET @msg=CONCAT('{01,',CONVERT(NEW.c_obj_id,CHAR),'," + a.id + "," + Funcs.translite(Funcs.sanitizeSQL(cls.name) + "." + Funcs.sanitizeSQL(a.name))+ "}');\n";
			sql += "\t\t\tINSERT INTO "+getDBPrefix()+"t_msg VALUES (@msg),(@msg);\n";
			sql += "\t\tEND IF;\n";
			
			usql += "\tIF ((NEW.c_tr_id=0 AND OLD.c_tr_id!=0) OR OLD." + cmName + "!=NEW." + cmName + ") AND ((NEW." + cmName + " IS NULL";
			if (a.typeClassId == PC_STRING) // string - проверяем на пустую строку
				usql += " OR LENGTH(NEW." + cmName + ")=0";
			usql += ")";

			if (sqlExcept != null) usql += sqlExcept;

			usql += ") THEN\n";
			usql += "\t\tSET @msg=CONCAT('{01,',CONVERT(NEW.c_obj_id,CHAR),'," + a.id + "," + Funcs.translite(Funcs.sanitizeSQL(cls.name) + "." + Funcs.sanitizeSQL(a.name))+ "}');\n";
			usql += "\t\tINSERT INTO "+getDBPrefix()+"t_msg VALUES (@msg),(@msg);\n";
			usql += "\tEND IF;\n";

			if (sqlExcept != null) {
				log.debug("********************************************************");
				log.debug(sqlExcept);
				log.debug("********************************************************");
			}
		}
		
		sql += "\tEND IF;\n";
		sql += "END\n";
		
		usql += "END\n";

		if (create) {
			Statement st = conn.createStatement();
			try {
				st.executeUpdate(sql);
				st.executeUpdate(usql);
			} catch (SQLException e) {
				throw e;
			} finally {
				DbUtils.closeQuietly(st);
			}
		}
	}

	protected void updateTriggersComp(long classId)
	throws SQLException, DriverException {

		KrnClass cls = getClassByIdComp(classId);
		String itgName = getInsertTriggerName(cls.id);
		String utgName = getUpdateTriggerName(cls.id);
		
		Statement st = conn.createStatement();
		try {
			// Удаляем старые триггеры (если существуют)
			st.executeUpdate("DROP TRIGGER IF EXISTS " + itgName);
			st.executeUpdate("DROP TRIGGER IF EXISTS " + utgName);
		} catch (SQLException e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(st);
		}

		List<KrnAttribute> attrs = getAttributesByClassIdComp(cls, false);
		createTriggers(cls, attrs);
	}

	// Methods support
	public KrnMethod changeMethod(String uid, String name, boolean isClassMethod, byte[] expr) throws DriverException {
		KrnMethod method = db.getMethodByUid(uid);
		KrnClass classM = db.getClassById(method.classId);
		KrnMethod newMethod = new KrnMethod(method.uid, name, method.classId,classM==null?"":classM.name, isClassMethod, method.ownerId);
		try {
			// Создаем запись в журнале изменения модели
			logVcsModelChanges(ENTITY_TYPE_METHOD, ACTION_MODIFY,method, newMethod, expr, conn);
			PreparedStatement st = conn.prepareStatement("UPDATE "+getDBPrefix()+"t_methods SET c_name=?,c_is_cmethod=?,c_expr=?"+" WHERE c_muid=?");
			st.setString(1, name);
			st.setBoolean(2, isClassMethod);
			setValue(st, 3, PC_BLOB, expr);
			st.setString(4, newMethod.uid);
			st.executeUpdate();
			st.close();

			// Создаем запись в журнале изменения модели
			logModelChanges(ENTITY_TYPE_METHOD, ACTION_MODIFY, uid, conn);

			db.removeMethod(method);
			db.addMethod(newMethod, false);
			try {
				ASTStart ast = OrLang.createStaticTemplate(new InputStreamReader(
		        		new ByteArrayInputStream(expr), "UTF-8"));
				putMethodExpression(uid, ast);
			} catch (Throwable e) {
				log.warn("Ошибка при разборе метода. " + e.getMessage());
				removeMethodExpression(dsName, uid);
			}
			return newMethod;
		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	public KrnMethod createMethod(KrnClass cls, String name, boolean isClassMethod, byte[] expr) throws DriverException {
		return createMethod(null, cls, name, isClassMethod, expr);
	}

	public KrnMethod createMethod(String uid, KrnClass cls, String name, boolean isClassMethod, byte[] expr) throws DriverException {
		return createMethod(uid, cls, name, isClassMethod, expr, -1, true);
	}
	
	public KrnMethod createMethod(String uid, KrnClass cls, String name, boolean isClassMethod, byte[] expr, long developer, boolean log) throws DriverException {
		try {
			PreparedStatement st;
			if (uid == null)
				uid = UUID.randomUUID().toString();
			st = conn.prepareStatement("INSERT INTO "+getDBPrefix()+"t_methods (c_class_id,c_name,c_is_cmethod,c_muid,developer)"+" VALUES (?,?,?,?,?)");
			st.setLong(1, cls.id);
			st.setString(2, name);
			st.setBoolean(3, isClassMethod);
			st.setString(4, uid);
			if (developer == 0)
				st.setNull(5, Types.BIGINT);
			else
				st.setLong(5, developer > 0 ? developer : us.getUserId());
			st.executeUpdate();
			st.close();
			st = conn.prepareStatement("UPDATE "+getDBPrefix()+"t_methods SET c_expr=? WHERE c_muid=?");
			setValue(st, 1, PC_BLOB, expr);
			st.setString(2, uid);
			st.executeUpdate();
			st.close();

			KrnMethod res = new KrnMethod(uid, name, cls.id, cls.name,isClassMethod, us.getUserId());

			if (log) {
				// Создаем запись в журнале изменения модели
				logVcsModelChanges(ENTITY_TYPE_METHOD, ACTION_CREATE, res, res, expr, conn);
	
				// Создаем запись в журнале изменения модели
				logModelChanges(ENTITY_TYPE_METHOD, ACTION_CREATE, uid, conn);
			}
			
			db.addMethod(res, false);
			return res;
		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	public void deleteMethod(String uid) throws DriverException {
		KrnMethod method = db.getMethodByUid(uid);
		byte[] expr = getMethodExpression(uid);
		try {
			PreparedStatement st = conn
					.prepareStatement("DELETE FROM "+getDBPrefix()+"t_methods WHERE c_muid=?");
			st.setString(1, uid);
			st.executeUpdate();
			st.close();

			// Создаем запись в журнале изменения модели
			logVcsModelChanges(ENTITY_TYPE_METHOD, ACTION_DELETE, method, method, expr, conn);
			logModelChanges(ENTITY_TYPE_METHOD, ACTION_DELETE, uid, conn);

			db.removeMethod(method);
			removeMethodExpression(dsName, uid);
		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	public byte[] getMethodExpression(String methodUid) throws DriverException {
		PreparedStatement  st = null;
		ResultSet rs = null;
		
		try {
			st = conn.prepareStatement("SELECT c_expr FROM "+Funcs.sanitizeSQL(getDBPrefix())+"t_methods WHERE c_muid=?");
			
			byte[] expr = new byte[0];
			st.setString(1, Funcs.sanitizeSQL(methodUid));
			rs = st.executeQuery();
			if (rs.next()) {
				InputStream is = rs.getBinaryStream(1);
				if (is != null) {
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					// noinspection StatementWithEmptyBody
					for (int b; (b = is.read()) != -1; os.write(b))
						;
					is.close();
					os.close();
					expr = os.toByteArray();
				}
			}
			return expr;
		} catch (Exception e) {
			throw new DriverException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(st);
		}
	}

	public List<KrnMethod> getAllMethods() throws DriverException {
		Statement st = null;
		ResultSet rs = null;
		try {
			List<KrnMethod> res = new ArrayList<KrnMethod>();
			st = conn.createStatement();
			if (version >= 39) {
				rs = st.executeQuery("SELECT c_muid,c_name,c_class_id,c_is_cmethod,developer FROM "+getDBPrefix()+"t_methods");
				while (rs.next()) {
					KrnClass classM = db.getClassById(rs.getLong(3));
					KrnMethod m = new KrnMethod(getSanitizedString(rs, 1).trim(), getSanitizedString(rs, 2), rs.getLong(3),classM==null?"":classM.name, rs.getBoolean(4), rs.getLong(5));
					res.add(m);
				}
			} else {
				rs = st.executeQuery("SELECT c_muid,c_name,c_class_id,c_is_cmethod FROM "+getDBPrefix()+"t_methods");
				while (rs.next()) {
					KrnClass classM = db.getClassById(rs.getLong(3));
					KrnMethod m = new KrnMethod(getSanitizedString(rs, 1).trim(), getSanitizedString(rs, 2),rs.getLong(3),classM==null?"":classM.name, rs.getBoolean(4), 0);
					res.add(m);
				}
			}
			rs.close();
			st.close();
			return res;
		} catch (Exception e) {
			throw new DriverException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(st);
		}
	}
	
	public byte[] getVcsChangeExpression(KrnVcsChange change) throws DriverException{
		PreparedStatement  st = null;
		ResultSet rs = null;
		
		try {
			if(change.cvsChangeMethod!=null){
				st = conn.prepareStatement("SELECT c_dif FROM "+Funcs.sanitizeSQL(getDBPrefix())+"t_vcs_model WHERE c_id=? AND c_dif IS NOT NULL");
			}else if(change.cvsChangeObj!=null){
				st = conn.prepareStatement("SELECT c_dif FROM "+Funcs.sanitizeSQL(getDBPrefix())+"t_vcs_objects WHERE c_id=? AND c_dif IS NOT NULL");
			}
			
			byte[] expr = new byte[0];
			if(st==null)	return expr;
			st.setLong(1, change.id);
			rs = st.executeQuery();
			if (rs.next()) {
				InputStream is = rs.getBinaryStream(1);
				if (is != null) {
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					// noinspection StatementWithEmptyBody
					for (int b; (b = is.read()) != -1; os.write(b))
						;
					is.close();
					os.close();
					expr = os.toByteArray();
				}
			}
			return expr;
		} catch (Exception e) {
			throw new DriverException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(st);
		}
	}
    private String getCreatedAndDeletedEntities(int entityType,
			String whereClause) throws SQLException {
		ResultSetHandler rh = new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				List<String> res = new ArrayList<String>();
				while (rs.next()) {
					res.add(getString(rs, 1));
				}
				rs.close();
				return res;
			}
		};
		QueryRunner qr = new QueryRunner(true);
		List<String> list = (List<String>) qr
				.query(conn,
					"SELECT c_entity_id FROM "+getDBPrefix()+"t_changescls t1"
					+ " WHERE c_action=" + ACTION_CREATE
					+ " AND c_type=" + entityType
					+ " AND c_entity_id IN (SELECT c_entity_id FROM "+getDBPrefix()+"t_changescls"
					+ " WHERE c_action=" + ACTION_DELETE
					+ " AND c_type=" + entityType + whereClause
					+ " AND c_entity_id=t1.c_entity_id)"
					+ whereClause, rh);
		ResultSetHandler h = new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				return rs.next();
			}
		};
		qr = new QueryRunner(true);
		String res = "";
		for (String entity : list) {
			res += ",'" + entity + "'";
		}
		if (res.length() > 0) {
			res = " AND c_entity_id NOT IN (" + res.substring(1) + ")"; 
		}
		return res;
	}

	private String getRecreatedEntities(int entityType,
			String whereClause) throws SQLException {
		ResultSetHandler rh = new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				List<String> res = new ArrayList<String>();
				while (rs.next()) {
					res.add(getString(rs, 1));
				}
				rs.close();
				return res;
			}
		};
		QueryRunner qr = new QueryRunner(true);
		List<String> list = (List<String>) qr
				.query(conn,
					"SELECT c_entity_id FROM "+getDBPrefix()+"t_changescls t1"
					+ " WHERE c_action=" + ACTION_CREATE
					+ " AND c_type=" + entityType
					+ " AND c_entity_id IN (SELECT c_entity_id FROM "+getDBPrefix()+"t_changescls"
					+ " WHERE c_action=" + ACTION_DELETE
					+ " AND c_type=" + entityType + whereClause
					+ " AND c_entity_id=t1.c_entity_id)"
					+ whereClause, rh);
		ResultSetHandler h = new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				return rs.next();
			}
		};
		qr = new QueryRunner(true);
		String res = "";
		for (String entity : list) {
			//добавляем в список только в случае если последнее событие для текущей сущности является "Создание"
			if ((Boolean) qr.query(conn, "SELECT c_id FROM "+getDBPrefix()+"t_changescls t1 WHERE c_entity_id = '"
					+ entity + "' AND c_action = " + ACTION_CREATE + " AND c_id = (SELECT MAX(c_id) FROM "+getDBPrefix()+"t_changescls"
					+ " WHERE c_entity_id=t1.c_entity_id)", h))
				res += ",'" + entity+"'";
		}
		if (res.length() > 0) {
			res = " c_entity_id IN (" + res.substring(1) + ")"; 
		}
		return res;
	}

	private String getRecreatedObjects(String whereClause) throws SQLException {
		long attrCreating = 0; long attrDeleting = 0;
		attrCreating = db.getAttributeByName(
				db.getClassByName("Объект").id, "creating").id;
		attrDeleting = db.getAttributeByName(
				db.getClassByName("Объект").id, "deleting").id;
		ResultSetHandler rh = new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				List<Long> res = new ArrayList<Long>();
				while (rs.next()) {
					res.add(rs.getLong(1));
				}
				rs.close();
				return res;
			}
		};
		QueryRunner qr = new QueryRunner(true);
		List<Long> list = (List<Long>) qr
				.query(conn,
					"SELECT c_object_id FROM "+getDBPrefix()+"t_changes t1"
					+ " WHERE c_tr_id=0 AND c_attr_id=" + attrCreating
					+ " AND c_object_id IN (SELECT c_object_id FROM "+getDBPrefix()+"t_changes"
					+ " WHERE c_tr_id=0 AND c_attr_id=" + attrDeleting
					+ whereClause
					+ " AND c_object_id=t1.c_object_id)"
					+ whereClause, rh);
		ResultSetHandler h = new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				return rs.next();
			}
		};
		qr = new QueryRunner(true);
		String res = "";
		for (Long object : list) {
			//добавляем в список только в случае если последнее событие для текущего объекта является "Создание"
			if ((Boolean) qr.query(conn, "SELECT c_id FROM "+getDBPrefix()+"t_changes t1 WHERE c_tr_id=0 AND c_object_id = "
					+ object + " AND c_attr_id = " + attrCreating + " AND c_id = (SELECT MAX(c_id) FROM "+getDBPrefix()+"t_changes"
					+ " WHERE c_tr_id=0 AND c_object_id=t1.c_object_id)", h))
				res += "," + object;
		}
		if (res.length() > 0) {
			res = " c_object_id IN (" + res.substring(1) + ")"; 
		}
		return res;
	}

	private void getDeletedEntities(int entityType, String whereClause,
			final Set<String> set) throws SQLException {
		set.clear();
		ResultSetHandler rh = new ResultSetHandler() {
			public Object handle(ResultSet rs) throws SQLException {
				while (rs.next()) {
					set.add(getString(rs, 1));
				}
				rs.close();
				return set;
			}
		};
		QueryRunner qr = new QueryRunner(true);
		qr.query(conn, "SELECT c_entity_id FROM "+getDBPrefix()+"t_changescls"
				+ " WHERE c_action=" + ACTION_DELETE + " AND c_type="
				+ entityType + whereClause, rh);
	}

	private List queryChanges(int entityType, long fromId, long toId, 
			ResultSetHandler rsh) throws SQLException{
		String sql = "";
		String whereFromId = (fromId != -1) ? whereFromId = " AND c_id > " + fromId : "";
		String whereToId = (toId != -1) ? whereToId = " AND c_id <= " + toId : ""; 
		String whereMain = " c_type = " + entityType + whereFromId + whereToId;

		sql = "SELECT *" +
				" FROM "+getDBPrefix()+"t_changescls" +
				" WHERE" + whereMain +
				"  AND c_entity_id NOT IN (" +
				"   SELECT c_entity_id FROM "+getDBPrefix()+"t_changescls WHERE" + whereMain + " AND c_action = 2" +
				"  )" +
				" ORDER BY c_id" +
				" ";
		log.debug("sql = " + sql);
		return (List) new QueryRunner(true).query(conn, sql, rsh);
	}
	
	private List queryChanges(int entityType, long fromId, long toId,
			ResultSetHandler rh, Set<String> deletedEntities) throws SQLException {
		String noCheck = Funcs.normalizeInput(System.getProperty("metadata_nocheck"));
		if (noCheck == null || (noCheck.equals("false") || noCheck.equals("0")))
			// проверяем базу на корректность данных по изменениям
			checkEntityChanges(entityType);

		String idColumn = IDColumnName();
		String idColumnExists_ = Funcs.getSystemProperty("idColumnExists");
		if (idColumnExists_ != null
				&& (idColumnExists_.equals("true") || idColumnExists_
						.equals("1"))) {
			idColumn = "c_id";
		}
		String stoId = "";
		if (toId > -1) {
			stoId = " AND " + idColumn + "<=" + toId;
		}
		String whereClause = " AND " + idColumn + ">" + fromId + stoId;

		// формируем список удаленных сущностей, 
		// чтобы при экспорте игнорировать все изменения
		getDeletedEntities(entityType, whereClause, deletedEntities);

		// формируем список созданных сущностей, 
		// которые сразу же были и удалены,
		// для того чтобы их исключить из экспорта вообще
		String cde = getCreatedAndDeletedEntities(entityType, whereClause);

		// формируем список созданных сущностей, которые были удалены,
		// а позже были созданы новые сущности под этим же номером
		String rc = getRecreatedEntities(entityType, whereClause);
		
		String order = "";
		if (rc.length() == 0)
			order = " ORDER BY " + idColumn;
			
		String sql = "SELECT " + idColumn + " AS id, t1.* FROM "+getDBPrefix()+"t_changescls t1"
				+ " WHERE c_type=" + entityType + whereClause
				// учитываем cde
				+ cde + order;
		
		if (rc.length() > 0) {
			sql += " UNION SELECT " + idColumn + " AS id, t1.* FROM "+getDBPrefix()+"t_changescls t1"
			+ " WHERE c_type=" + entityType + whereClause + " AND c_action=" + ACTION_CREATE
			+ " AND c_id=(SELECT MAX(c_id) FROM "+getDBPrefix()+"t_changescls WHERE c_entity_id=t1.c_entity_id)"
			// учитываем rc
                        //+ " AND " + rc + " ORDER BY " + idColumn;
                        + " AND " + rc + " ORDER BY id";
		}
		
		// условия для выбора только последнего изменения УБРАЛИ!!!
		// теперь решено выводить все изменения, так как иначе чревато
		// ошибками!!!
		log.debug("sql = " + sql);
		// noinspection UnnecessaryLocalVariable
		List res = (List) new QueryRunner(true).query(conn, sql, rh);
		return res;
	}

    private void checkEntityChanges(int entityType) throws SQLException {
        ResultSetHandler rh = new ResultSetHandler() {
            public Object handle(ResultSet rs) throws SQLException {
                List<String> res = new ArrayList<String>();
                while (rs.next()) {
                    res.add(getString(rs, 1));
                }
                rs.close();
                return res;
            }
        };
        QueryRunner qr = new QueryRunner(true);
        List<String> list = (List<String>) qr.query(conn, "SELECT c_entity_id FROM "+getDBPrefix()+"t_changescls t1 WHERE c_action<>"
                + ACTION_CREATE + " AND c_type=" + entityType
                + " AND c_entity_id NOT IN (SELECT c_entity_id FROM "+getDBPrefix()+"t_changescls WHERE c_action=" + ACTION_CREATE
                + " and c_type=" + entityType + " and c_entity_id=t1.c_entity_id)", rh);
        StringBuilder res = new StringBuilder();
        for (String entity : list) {
            res.append(", ").append(entity);
        }
        if (list.size() > 0) {
            res.delete(0, 1);
            throw new SQLException("ERROR: There are wrong rows in the t_changescls!\n" + "Entities:\n" + res.toString()
                    + "\nhave not creating event.");
        }
    }

	private Set<Long> prepareDataChanges(long fromId) throws SQLException,
			DriverException {
		// данная функци выполняет следующее:
		// - удаляет изменения предыдущего экспорта
		// - готовит и возвращает список объектов, которые были созданы и сразу
		// же удалены
		String idColumn = IDColumnName();
		boolean idColumnIsInc = true;
		String idColumnExists_ = Funcs.normalizeInput(System.getProperty("idColumnIsInc"));
		if (idColumnExists_ != null
				&& (idColumnExists_.equals("false") || idColumnExists_
						.equals("0"))) {
			idColumnIsInc = false;
		}
		// удаляем все строки меньше fromId
		QueryRunner qr = new QueryRunner(true);
		qr.update(conn, "DELETE FROM "+getDBPrefix()+"t_changes WHERE " + idColumn + "<="
				+ fromId + " AND c_tr_id=0");

		final long attrCreating = db.getAttributeByName(
				db.getClassByName("Объект").id, "creating").id;
		final long attrDeleting = db.getAttributeByName(
				db.getClassByName("Объект").id, "deleting").id;
		class LongSetResultSetHandler implements ResultSetHandler {
			public Object handle(ResultSet resultSet) throws SQLException {
				Set<Long> res = new TreeSet<Long>();
				while (resultSet.next()) {
					res.add(resultSet.getLong(1));
				}
				return res;
			}
		}
		// String sss = "SELECT c_object_id FROM t_changes ch" +
		// " WHERE c_object_id IN (SELECT c_obj_id FROM ct99)" +
		// " AND EXISTS (SELECT c_object_id FROM t_changes" +
		// " WHERE " + idColumn + ">" + fromId + " AND c_tr_id=0" +
		// " AND c_attr_id=" + attrCreating + " AND c_object_id=ch.c_object_id)"
		// +
		// " AND EXISTS (SELECT c_object_id FROM t_changes" +
		// " WHERE " + idColumn + ">" + fromId + " AND c_tr_id=0" +
		// " AND c_attr_id=" + attrDeleting + " AND c_object_id=ch.c_object_id)"
		// +
		// " AND " + idColumn + ">" + fromId;
		// System.out.println("sss = " + sss);
		String sss = "SELECT DISTINCT c1.c_object_id FROM "+getDBPrefix()+"t_changes c1, "+getDBPrefix()+"t_changes c2, "+getClassTableName(99)+" c3"
				+ " WHERE c3.c_obj_id=c1.c_object_id"
				+ " AND c1."
				+ idColumn
				+ ">"
				+ fromId
				+ " AND c1.c_tr_id=0"
				+ " AND c1.c_attr_id="
				+ attrCreating
				+ " AND c2."
				+ idColumn
				+ ">"
				+ fromId
				+ " AND c2.c_tr_id=0"
				+ " AND c2.c_attr_id="
				+ attrDeleting
				+ " AND c1.c_object_id=c2.c_object_id";
		log.debug("sql of createDeletedObjects = " + sss);
		// noinspection UnnecessaryLocalVariable
		Set<Long> createDeletedObjects = (Set<Long>) qr.query(conn, sss,
				new LongSetResultSetHandler());
		return createDeletedObjects;
	}

	public String IDColumnName() {
		return "c_id";
	}

	public String getString(Object o) {
		String res;
		if (o instanceof java.sql.Time) {
			java.sql.Time t = (java.sql.Time) o;
			res = t.toString();
		} else if (o instanceof java.sql.Date) {
			java.sql.Date d = (java.sql.Date) o;
			res = d.toString();
		} else if (o instanceof com.cifs.or2.kernel.Date) {
			com.cifs.or2.kernel.Date d = (com.cifs.or2.kernel.Date) o;
			res = d.year + "-" + d.month + "-" + d.day;
		} else if (o instanceof com.cifs.or2.kernel.Time) {
			com.cifs.or2.kernel.Time t = (com.cifs.or2.kernel.Time) o;
			res = t.year + "-" + t.month + "-" + t.day + " " + t.hour + ":"
					+ t.min + ":" + t.sec;
		} else {
			res = o.toString();
		}
		return res;
	}

	protected static class UpdateIdHandler implements ResultSetHandler {

		private final QueryRunner qr = new QueryRunner(true);
		private final Connection conn;
		private String tbName;
		private String cmName;

		public UpdateIdHandler(Connection conn) {
			this.conn = conn;
		}

		public void setParams(String tbName, String cmName) {
			this.tbName = tbName;
			this.cmName = cmName;
		}

		public Object handle(ResultSet set) throws SQLException {
			while (set.next()) {
				long oldId = set.getLong("oldId");
				long newId = set.getLong("newId");
				qr.update(conn, "UPDATE " + tbName + " SET " + cmName + "=?"
						+ " WHERE " + cmName + "=?", new Object[] { newId,
						oldId });
			}
			return null;
		}
	}
	
	public boolean isDeleted(KrnObject obj) throws SQLException {
		boolean res = true;
		String ctName = getClassTableName(obj.classId);
		PreparedStatement pst = conn.prepareStatement(
				"SELECT c_is_del FROM " + ctName + " WHERE c_obj_id=?");
		pst.setLong(1, obj.id);
		ResultSet set = pst.executeQuery();
		if (set.next())
			res = set.getBoolean(1);
		set.close();
		pst.close();
		return res;
	}
	
	public void repairReferences() throws SQLException, DriverException {
	}

	public String getAttributeComment(long attrId) throws DriverException {
		PreparedStatement pstGetAttributeComment = null;
		try {
			pstGetAttributeComment = conn.prepareStatement(
					"SELECT c_comment FROM "+getDBPrefix()+"t_attrs WHERE c_id=?");
			pstGetAttributeComment.setLong(1, attrId);
			ResultSet rs = pstGetAttributeComment.executeQuery();
			String res = "";
			if (rs.next()) {
				res = getMemo(rs, "c_comment");
				if (res == null) {
					res = "";
				}
			}
			rs.close();
			return res;
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(pstGetAttributeComment);
		}
	}

	public String getClassComment(long clsId) throws DriverException {
		PreparedStatement pstGetClassComment = null;
		try {
			pstGetClassComment = conn.prepareStatement("SELECT c_comment FROM "+getDBPrefix()+"t_classes WHERE c_id=?");
			pstGetClassComment.setLong(1, clsId);
			ResultSet rs = pstGetClassComment.executeQuery();
			String res = "";
			if (rs.next()) {
				res = getMemo(rs, "c_comment");
				if (res == null) {
					res = "";
				}
			}
			rs.close();
			return res;
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(pstGetClassComment);
		}
	}

	public String getMethodComment(String methodUid) throws DriverException {
		PreparedStatement pstGetMethodComment = null;
		try {
			pstGetMethodComment = conn.prepareStatement(Funcs.sanitizeSQL("SELECT c_comment FROM "+getDBPrefix()+"t_methods WHERE c_muid=?"));
			pstGetMethodComment.setString(1, Funcs.sanitizeSQL(methodUid));
			ResultSet rs = pstGetMethodComment.executeQuery();
			String res = "";
			if (rs.next()) {
				res = getMemo(rs, "c_comment");
				if (res == null) {
					res = "";
				}
			}
			rs.close();
			return Funcs.sanitizeSQL(res);
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(pstGetMethodComment);
		}
	}

	public void setAttributeComment(String attrUid, String comment) throws DriverException {
		setAttributeComment(attrUid, comment, true);
	}
	
	public void setAttributeComment(String attrUid, String comment, boolean log) throws DriverException {
		// Добавление комментария атрибута в запись таблицы t_attrs
		PreparedStatement pstSetAttributeComment = null;
		attrUid = Funcs.sanitizeSQL(attrUid);
		try {
			pstSetAttributeComment = conn.prepareStatement("UPDATE "+getDBPrefix()+"t_attrs SET c_comment=? WHERE c_auid=?");
			setMemo(pstSetAttributeComment, 1, comment);
			pstSetAttributeComment.setString(2, attrUid);
			pstSetAttributeComment.executeUpdate();
			if (log)
				logModelChanges(ENTITY_TYPE_ATTRIBUTE, ACTION_MODIFY, attrUid, conn);
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(pstSetAttributeComment);
		}
		// Добавление комментария атрибута в БД
		try {
			KrnAttribute attr = db.getAttributeByUid(attrUid);
			long id = attr.id;
			long cls_id = attr.classId;
			long C_RATTR_ID = attr.rAttrId;
			String tname = attr.tname;
			if (cls_id > 99 && C_RATTR_ID == 0) {
				String tableName = null;
				int collectionType = attr.collectionType;
				boolean isMultilingual = attr.isMultilingual;
				if (collectionType != 0) {
					if (tname == null || tname.trim().length() == 0) {
						tableName = getAttrTableName(attr);
					} else {
						tableName = tname;
					}
					new QueryRunner(true).update(conn, "ALTER TABLE " + tableName + " COMMENT='" + comment + "'");
				} else {
					KrnClass cls_t_n = db.getClassById(cls_id);
					tableName = getClassTableName(cls_t_n, false);
					String columnName = getColumnName(attr);
					
					String description = "";
					String curComment = "";
					Statement st = null;
					PreparedStatement pst = null;
					ResultSet result = null;
					try {
						st = conn.createStatement();
						result = st.executeQuery("SHOW CREATE TABLE " + tableName);
						if (result.next()) {
							description = getString(result, 2);
						}
						result.close();
						
						pst = conn.prepareStatement("SELECT COLUMN_COMMENT FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME=? AND COLUMN_NAME=? AND TABLE_SCHEMA=?");
						pst.setString(1, tableName);
						pst.setString(2, columnName);
						pst.setString(3, db.getSchemeName());
						result = pst.executeQuery();
						if (result.next()) {
							curComment = getString(result, 1);
						}
					} finally {
						DbUtils.closeQuietly(result);
						DbUtils.closeQuietly(pst);
						DbUtils.closeQuietly(st);
					}
					
					if (description != null && description.length() > 0 && !cls_t_n.isVirtual()) {
						if (curComment.length() > 0) {
							curComment = curComment.replace("\n", "\\n");
							description = description.replace(" COMMENT '" + curComment + "'", "");
						}
						if (isMultilingual) {
							List<KrnObject> langs = getSystemLangs();
							for (KrnObject lang : langs) {
								String creatingParams = description.substring(description.indexOf(columnName) + columnName.length() + String.valueOf(getSystemLangIndex(lang.id)).length() + 3);
								creatingParams = creatingParams.substring(0, creatingParams.indexOf("`") - 4);
								new QueryRunner(true).update(conn, "ALTER TABLE " + tableName + " CHANGE `" + columnName + "_" + getSystemLangIndex(lang.id) + "` `" + columnName + "_" + getSystemLangIndex(lang.id) + "` " + creatingParams + " COMMENT '" + comment + "'");
							}
						} else {
							String creatingParams = description.substring(description.indexOf(columnName) + columnName.length() + 2);
							creatingParams = creatingParams.substring(0, creatingParams.indexOf(","));
							new QueryRunner(true).update(conn, "ALTER TABLE " + tableName + " CHANGE `" + columnName + "` `" + columnName + "` " + creatingParams + " COMMENT '" + comment + "'");
						}
					}
				}
			}
		} catch (SQLException e) {
			this.log.error("ERROR: MySQL Attribute COMMENT: " + e.getMessage());
		}
	}

	public void setClassComment(String clsUid, String comment) throws DriverException {
		setClassComment(clsUid, comment, true);
	}
	
	public void setClassComment(String clsUid, String comment, boolean log) throws DriverException {
		// Добавление комментария класса в запись таблицы t_classes
		PreparedStatement pstSetClassComment = null;
		try {
			pstSetClassComment = conn.prepareStatement("UPDATE "+getDBPrefix()+"t_classes SET c_comment=? WHERE c_cuid=?");
			setMemo(pstSetClassComment, 1, comment);
			pstSetClassComment.setString(2, clsUid);
			pstSetClassComment.executeUpdate();
			if (log)
				logModelChanges(ENTITY_TYPE_CLASS, ACTION_MODIFY, clsUid, conn);
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(pstSetClassComment);
		}
		// Добавление комментария класса в БД
		String dbType = kz.tamur.or3.util.Tname.DBtype;
		KrnClass cls_t_n=db.getClassByUid(clsUid);
		if ((dbType.equals("mysql") || dbType.equals("mysql3")) && !cls_t_n.isVirtual()) {
			String tname = getClassTableName(clsUid);
			Statement st = null;
			try {
				st = conn.createStatement();
				st.executeUpdate("ALTER TABLE " + tname +  " COMMENT='" + comment + "'");
			} catch (SQLException e) {
				throw convertException(e);
			} finally {
				DbUtils.closeQuietly(st);
	}
		}
	}

	public void setMethodComment(String methodUid, String comment) throws DriverException {
		setMethodComment(methodUid, comment, true);
	}
	
	public void setMethodComment(String methodUid, String comment, boolean log) throws DriverException {
		PreparedStatement pstSetMethodComment = null;
		try {
			pstSetMethodComment = conn.prepareStatement(
					"UPDATE "+getDBPrefix()+"t_methods SET c_comment=? WHERE c_muid=?");
			setMemo(pstSetMethodComment, 1, comment);
			pstSetMethodComment.setString(2, methodUid);
			pstSetMethodComment.executeUpdate();
			if (log)
				logModelChanges(ENTITY_TYPE_METHOD, ACTION_MODIFY, methodUid, conn);
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(pstSetMethodComment);
		}
	}

	public void commitLocks(String sessionId) throws DriverException {
		PreparedStatement pst = null;
		try{
			pst = conn.prepareStatement(
					"UPDATE "+getDBPrefix()+"t_locks SET c_session_id=NULL " +
					"WHERE c_session_id=? AND c_scope<>?");
			pst.setString(1, sessionId);
			pst.setInt(2, Lock.LOCK_SESSION);
			pst.executeUpdate();
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(pst);
		}
	}

	public void rollbackLocks(String sessionId) throws DriverException {
		PreparedStatement pst = null;
		PreparedStatement spst = null;
		ResultSet rs = null;
		boolean del = false;
		try{
            if (spst == null) {
            	spst = conn.prepareStatement("SELECT COUNT(*) FROM "+getDBPrefix()+"t_locks WHERE c_session_id=?");
            }
            spst.setString(1, sessionId);
            rs = spst.executeQuery();
            if (rs.next())
            	del = rs.getInt(1) > 0;
            
            if (del) {
	            pst = conn.prepareStatement(
						"DELETE FROM "+getDBPrefix()+"t_locks WHERE c_session_id=?");
				pst.setString(1, sessionId);
				pst.executeUpdate();
            }
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(spst);
			DbUtils.closeQuietly(pst);
		}
	}

	public Lock getLock(long objId, long lockerId) throws DriverException {
		Lock lock = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = conn.prepareStatement(
					"SELECT c_flow_id,c_session_id,c_scope " +
					"FROM "+getDBPrefix()+"t_locks WHERE c_obj_id=? AND c_locker_id=?");
			pst.setLong(1, objId);
			pst.setLong(2, lockerId);
			rs = pst.executeQuery();
			if (rs.next()) {
				long flowId = rs.getLong(1);
				String sessionId = getSanitizedString(rs, 2);
				int scope = rs.getInt(3);
				lock = new Lock(objId, lockerId, flowId, sessionId, scope);
			}
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
		}
		return lock;
	}

	public LockMethod getLockMethod(String muid) throws DriverException {
		LockMethod lock = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = conn.prepareStatement(
					"SELECT c_flow_id,c_session_id,c_scope " +
					"FROM "+getDBPrefix()+"t_lock_methods WHERE c_muid=?");
			pst.setString(1, muid);
			rs = pst.executeQuery();
			if (rs.next()) {
				long flowId = rs.getLong(1);
				String sessionId = getSanitizedString(rs, 2);
				int scope = rs.getInt(3);
				lock = new LockMethod(muid, flowId, sessionId, scope);
			}
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
		}
		return lock;
	}
	public Collection<Lock> getLocksByObjectId(long objId) throws DriverException {
		Collection<Lock> locks = new ArrayList<Lock>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = conn.prepareStatement(
					"SELECT c_locker_id,c_flow_id,c_session_id,c_scope " +
					"FROM "+getDBPrefix()+"t_locks WHERE c_obj_id=?");
			pst.setLong(1, objId);
			rs = pst.executeQuery();
			while (rs.next()) {
				long lockerId = rs.getLong(1);
				long flowId = rs.getLong(2);
				String sessionId = getSanitizedString(rs, 3);
				int scope = rs.getInt(4);
				locks.add(new Lock(objId, lockerId, flowId, sessionId, scope));
			}
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
		}
		return locks;
	}

	public Collection<Lock> getLocksByLockerId(long lockerId) throws DriverException {
		Collection<Lock> locks = new ArrayList<Lock>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = conn.prepareStatement(
					"SELECT c_obj_id,c_flow_id,c_session_id,c_scope " +
					"FROM "+getDBPrefix()+"t_locks WHERE c_locker_id=?");
			pst.setLong(1, lockerId);
			rs = pst.executeQuery();
			while (rs.next()) {
				long objId = rs.getLong(1);
				long flowId = rs.getLong(2);
				String sessionId = getSanitizedString(rs, 3);
				int scope = rs.getInt(4);
				locks.add(new Lock(objId, lockerId, flowId, sessionId, scope));
			}
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
		}
		return locks;
	}

	public Collection<Lock> getAllLocks() throws DriverException {
		Collection<Lock> locks = new ArrayList<Lock>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = conn.prepareStatement("SELECT c_obj_id,c_locker_id,c_flow_id,c_session_id,c_scope FROM "+getDBPrefix()+"t_locks WHERE c_session_id IS NOT NULL");
			rs = pst.executeQuery();
			while (rs.next()) {
				long objId = rs.getLong(1);
				long lockerId = rs.getLong(2);
				long flowId = rs.getLong(3);
				String sessionId = getSanitizedString(rs, 4);
				int scope = rs.getInt(5);
				locks.add(new Lock(objId, lockerId, flowId, sessionId, scope));
			}
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
		}
		return locks;
	}

	public Collection<LockMethod> getMethodAllLocks() throws DriverException {
		Collection<LockMethod> locks = new ArrayList<LockMethod>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			pst = conn.prepareStatement("SELECT c_muid,c_flow_id,c_session_id,c_scope FROM "+getDBPrefix()+"t_lock_methods WHERE c_session_id IS NOT NULL");
			rs = pst.executeQuery();
			while (rs.next()) {
				String muid = getSanitizedString(rs, 1);
				long flowId = rs.getLong(2);
				String sessionId = getSanitizedString(rs, 3);
				int scope = rs.getInt(4);
				locks.add(new LockMethod(muid, flowId, sessionId, scope));
			}
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
		}
		return locks;
	}
    public void lockObject(long objId, long lockerId, int scope, long flowId, String sessionId) throws DriverException {
		Connection conn = null;
        PreparedStatement pst = null;
        try {
            if (isBlocked(objId, lockerId)) {
            	String msg = "Объект " + objId + " уже заблокирован!";
            	log.error(msg);
                throw new DriverException(msg, ErrorCodes.ER_OR3_LOCKED);
            }

			conn = getNewConnection();
            pst = conn.prepareStatement("INSERT INTO "+getDBPrefix()+"t_locks (c_obj_id,c_locker_id,c_flow_id,c_session_id,c_scope) VALUES (?,?,?,?,?)");
            pst.setLong(1, objId);
            pst.setLong(2, lockerId);
            pst.setLong(3, flowId);
            if (sessionId != null)
                pst.setString(4, sessionId);
            else
                pst.setNull(4, Types.CHAR);
            pst.setInt(5, scope);
            pst.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            throw convertException(e);
        } finally {
            DbUtils.closeQuietly(pst);
            DbUtils.closeQuietly(conn);
        }
    }

    public boolean isBlocked(long objId, long lockerId) throws DriverException {
        boolean res = false;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
	        pst = conn.prepareStatement("SELECT c_locker_id FROM " + getDBPrefix() + "t_locks WHERE c_obj_id=?");
	        pst.setLong(1, objId);
	        rs = pst.executeQuery();
	        while (rs.next()) {
	        	if (rs.getLong("c_locker_id") == lockerId) {
		            res = true;
	        	}
	        }
        } catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
        }
        return res;
    }

    public void lockMethod(String muid, int scope, long flowId, String sessionId) throws DriverException {
		Connection conn = null;
        PreparedStatement pst = null;
        try {
            if (isMethodBlocked(muid)) {
            	String msg = "Объект " + muid + " уже заблокирован!";
            	log.error(msg);
                throw new DriverException(msg, ErrorCodes.ER_OR3_LOCKED);
            }

			conn = getNewConnection();
            pst = conn.prepareStatement("INSERT INTO "+getDBPrefix()+"t_lock_methods (c_muid,c_flow_id,c_session_id,c_scope) VALUES (?,?,?,?)");
            pst.setString(1, muid);
            pst.setLong(2, flowId);
            if (sessionId != null)
                pst.setString(3, sessionId);
            else
                pst.setNull(3, Types.CHAR);
            pst.setInt(4, scope);
            pst.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            throw convertException(e);
        } finally {
            DbUtils.closeQuietly(pst);
            DbUtils.closeQuietly(conn);
        }
    }

    public boolean isMethodBlocked(String muid) throws DriverException {
        boolean res = false;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
	        pst = conn.prepareStatement("SELECT c_session_id FROM " + getDBPrefix() + "t_lock_methods WHERE c_muid=?");
	        pst.setString(1, muid);
	        rs = pst.executeQuery();
	        while (rs.next()) {
		            res = true;
	        }
        } catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
        }
        return res;
    }
	public void unlockMethod(String muid) throws DriverException {
		Connection conn = null;
		PreparedStatement pst = null;
		try{
			conn = getNewConnection();
			pst = conn.prepareStatement("DELETE FROM "+getDBPrefix()+"t_lock_methods WHERE c_muid=?");
			pst.setString(1, muid);
			pst.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(pst);
			DbUtils.closeQuietly(conn);
		}
	}
	

	public void unlockObject(long objId, long lockerId) throws DriverException {
		Connection conn = null;
		PreparedStatement pst = null;
		try{
			conn = getNewConnection();
			pst = conn.prepareStatement("DELETE FROM "+getDBPrefix()+"t_locks WHERE c_obj_id=? AND c_locker_id=?");
			pst.setLong(1, objId);
			pst.setLong(2, lockerId);
			pst.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(pst);
			DbUtils.closeQuietly(conn);
		}
	}

	public void unlockFlowObjects(long flowId) throws DriverException {
		PreparedStatement pst = null;
		try{
			pst = conn.prepareStatement("DELETE FROM "+getDBPrefix()+"t_locks WHERE c_flow_id=? AND c_scope=?");
			pst.setLong(1, flowId);
			pst.setLong(2, Lock.LOCK_FLOW);
			pst.executeUpdate();
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(pst);
		}
	}

	public void unlockUnexistingFlowObjects(long flowClassId) throws DriverException {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			StringBuilder sb = new StringBuilder("SELECT DISTINCT(l.c_flow_id) FROM ").append(getDBPrefix()).append("t_locks l ")
					.append("LEFT JOIN ").append(getClassTableName(flowClassId)).append(" f ON l.c_flow_id = f.c_obj_id ")
					.append("WHERE f.c_obj_id IS NULL and l.c_scope = ?");
			
	        pst = conn.prepareStatement(sb.toString());
	        pst.setLong(1, Lock.LOCK_FLOW);
	        rs = pst.executeQuery();
	        
	        List<Long> flowIds = new ArrayList<Long>();
	        while (rs.next()) {
	            flowIds.add(rs.getLong(1));
	        }
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
			
	        if (flowIds.size() > 0) {
				sb = new StringBuilder("DELETE FROM ").append(getDBPrefix()).append("t_locks WHERE c_flow_id = ?");
				
				pst = conn.prepareStatement(sb.toString());
				for (Long flowId : flowIds) {
					pst.setLong(1, flowId);
					pst.executeUpdate();
				}
	        }
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
		}
	}

	protected long upgradeImpl(long v) throws SQLException, DriverException {
		version = v;
		if (version < 3) {
			QueryRunner qr = new QueryRunner(true);
			Statement ust = conn.createStatement();
			
			// Удаляем все колонки для которых отсутствует запись в t_attrs
			Statement st = conn.createStatement();
			
			PreparedStatement idxPst = conn.prepareStatement(
					"select count(*) from information_schema.STATISTICS " +
					"where table_schema=? and table_name=? and index_name=?");
				
			idxPst.setString(1, conn.getCatalog());
			
			PreparedStatement pst = conn.prepareStatement(
					"select table_name,column_name from information_schema.columns " +
					"where table_schema=? and column_name like 'cm%' " +
					"and not exists (select c_id from "+getDBPrefix()+"t_attrs " +
					"where column_name=CONCAT('cm', c_id))");
			
			pst.setString(1, conn.getCatalog());
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				String tbName = getSanitizedString(rs, 1);
				String cmName = getSanitizedString(rs, 2);
				if ("at".equalsIgnoreCase(tbName.substring(0, 2)))
					ust.executeUpdate("DROP TABLE " + tbName);
				else { 
					String fkName = "FK" + cmName.substring(2);
					if (indexExists(idxPst, tbName, fkName)) {
						ust.executeUpdate("ALTER TABLE " + tbName
								+ " DROP FOREIGN KEY " + fkName);
					}
					ust.executeUpdate("ALTER TABLE " + tbName + " DROP COLUMN " + cmName);
				}
			}
			st.close();
			
			// Удаляем таблицу t1, если существует
			ust.executeUpdate("DROP TABLE IF EXISTS "+getDBPrefix()+"t1");
			// Удаляем AUTOINCREMENT с c_id в таблице ct99
			ust.executeUpdate("ALTER TABLE "+getClassTableNameComp(99)+" MODIFY c_id BIGINT NOT NULL");

			List<KrnAttribute> attrs = (List<KrnAttribute>) qr.query(conn,
					"SELECT * FROM "+getDBPrefix()+"t_attrs", new AttrResultSetHandler());

			for (KrnAttribute attr : attrs) {
				//if (attr.id <= 113) continue;
				log.info("Attribute " + attr.id + " pass 1");
				String ctName = getClassTableNameComp(attr.classId);
				String vtName = getClassTableNameComp(attr.typeClassId);
				String atName = getAttrTableName(attr);
				String cmName = getColumnName(attr);

				// Если атрибут объектного типа
				if (attr.typeClassId >= 99) {
					String fkName = getAttrFKName(attr.id);
					if (attr.collectionType == COLLECTION_NONE && !attr.isMultilingual) {
						// удаляем FK
						ust.executeUpdate("ALTER TABLE " + ctName
								+ " DROP FOREIGN KEY " + fkName);
						// удаляем индекс
						ust.executeUpdate("ALTER TABLE " + ctName
								+ " DROP KEY " + fkName);
						// Меняем ссылку с c_id на c_obj_id
						if (attr.classId == attr.typeClassId) {
							ust.executeUpdate("CREATE TABLE "+getDBPrefix()+"t1 ("
									+ "c_id BIGINT NOT NULL,"
									+ "c_obj_id BIGINT NOT NULL,"
									+ "PRIMARY KEY(c_id))");
							ust.executeUpdate("INSERT INTO "+getDBPrefix()+"t1 (c_id,c_obj_id)"
									+ " SELECT c_id,c_obj_id FROM " + ctName);
							ust.executeUpdate("UPDATE " + ctName + " SET "
									+ cmName + "=(SELECT c_obj_id FROM "+getDBPrefix()+"t1"
									+ " WHERE t1.c_id='" + cmName + "') WHERE "
									+ cmName + " IS NOT NULL");
							ust.executeUpdate("DROP TABLE "+getDBPrefix()+"t1");
						} else {
							ust.executeUpdate("UPDATE " + ctName + " SET "
									+ cmName + "=(SELECT c_obj_id FROM "
									+ vtName + " vt WHERE vt.c_id='" + cmName
									+ "') WHERE " + cmName + " IS NOT NULL");
						}
					} else {
						// удаляем FK
						ust.executeUpdate("ALTER TABLE " + atName
								+ " DROP FOREIGN KEY " + fkName);
						// удаляем индекс
						ust.executeUpdate("ALTER TABLE " + atName
								+ " DROP KEY " + fkName);
						// если это набор, то удаляем все записи, ссылающиеся
						// на не существующий объект
						ust.executeUpdate("DELETE FROM " + atName
								+ " WHERE NOT EXISTS (SELECT c_obj_id FROM " + vtName
								+ " vt WHERE vt.c_id='" + cmName + "')");
						// Меняем ссылку с c_id на c_obj_id
						ust.executeUpdate("UPDATE " + atName + " SET " + cmName
								+ "=(SELECT c_obj_id FROM " + vtName
								+ " vt WHERE vt.c_id='" + cmName + "') WHERE "
								+ cmName + " IS NOT NULL");
					}
				}

				if (attr.isMultilingual
						|| attr.collectionType != COLLECTION_NONE) {
					// Удаляем FK на с_id
					ust.executeUpdate("ALTER TABLE " + atName
							+ " DROP FOREIGN KEY " + atName + "_ibfk_1");
					// Удаляем PK
					ust.executeUpdate("ALTER TABLE " + atName
							+ " DROP PRIMARY KEY");
					// Добавляем колонку для ID транзакции
					ust.executeUpdate("ALTER TABLE " + atName
							+ " ADD (c_tr_id BIGINT NOT NULL)");
					// Добавляем колонку c_del
					ust.executeUpdate("ALTER TABLE " + atName
							+ " ADD (c_del BIGINT NOT NULL DEFAULT 0)");
					// Удаляем записи для несуществующих объектов
					ust.executeUpdate("DELETE FROM " + atName
							+ " WHERE NOT EXISTS (SELECT * FROM " + ctName
							+ " WHERE c_id=" + atName + ".c_obj_id)");
					// Меняем ссылку с (c_id) на (c_obj_id, c_tr_id)
					ust.executeUpdate("UPDATE " + atName + " at "
							+ "SET c_tr_id=(SELECT c_tr_id FROM " + ctName
							+ " WHERE c_id=at.c_obj_id)");
					ust.executeUpdate("UPDATE " + atName + " at "
							+ "SET c_obj_id=(SELECT c_obj_id FROM " + ctName
							+ " WHERE c_id=at.c_obj_id)");
					// Создаем PK
					StringBuilder sql = new StringBuilder("ALTER TABLE "
							+ atName + " ADD (PRIMARY KEY(c_obj_id,c_tr_id");
					if (attr.collectionType == COLLECTION_ARRAY) {
						sql.append(",c_index");
					}
					if (attr.isMultilingual) {
						sql.append(",c_lang_id");
					}
					if (attr.collectionType == COLLECTION_SET) {
						sql.append(",").append(getColumnName(attr));
					}
					sql.append(",").append("c_del))");
					ust.executeUpdate(sql.toString());

					// Обновление MEMO с BLOB на TEXT
					if (attr.typeClassId == PC_MEMO) {
						ust.executeUpdate("ALTER TABLE " + atName + " MODIFY "
								+ cmName + " TEXT");
					}
				} else {
					// Обновление MEMO с BLOB на TEXT
					if (attr.typeClassId == PC_MEMO) {
						ust.executeUpdate("ALTER TABLE " + ctName + " MODIFY "
								+ cmName + " TEXT");
					}
				}
				// }
			}

			List<KrnClass> classes = qr.query(conn,
					"SELECT * FROM "+getDBPrefix()+"t_classes WHERE c_id>10",
					new ClassResultSetHandler());
			for (KrnClass cls : classes) {
				log.info("Class " + cls.id);
				String ctName = getClassTableNameComp(cls.id);
				// Удаляем PK
				ust.executeUpdate("ALTER TABLE " + ctName
								+ " DROP PRIMARY KEY");
				// Удаляем индекс c_is_del
				if (indexExists(idxPst, ctName, "c_is_del")) {
					ust.executeUpdate("ALTER TABLE " + ctName
							+ " DROP INDEX c_is_del");
				}
				// Создаем PK
				ust.executeUpdate("ALTER TABLE " + ctName
						+ " ADD (PRIMARY KEY(c_obj_id,c_tr_id))");
				// Удаляем колонку c_id
				ust.executeUpdate("ALTER TABLE " + ctName
								+ " DROP COLUMN c_id");
			}
			
			idxPst.close();

			for (KrnAttribute attr : attrs) {
				log.info("Attribute " + attr.id + " pass 2");
				String ctName = getClassTableNameComp(attr.classId);
				String atName = getAttrTableName(attr);
				// Если атрибут объектного типа
				if (attr.typeClassId >= 99) {
					if (attr.collectionType == COLLECTION_NONE && !attr.isMultilingual) {
						// Создаем FK
						ust.executeUpdate("ALTER TABLE " + ctName + " ADD ("
								+ getKeyDef(attr, false) + ")");
					} else {
						// Создаем FK
						ust.executeUpdate("ALTER TABLE "
								+ atName
								+ " ADD ("
								+ getKeyDef(attr,
										attr.collectionType == COLLECTION_SET)
								+ ")");
					}
				}
				if (attr.isMultilingual
						|| attr.collectionType != COLLECTION_NONE) {
					// Создаем FK на (с_obj_id, c_tr_id)
					ust
							.executeUpdate("ALTER TABLE "
									+ atName
									+ " ADD (CONSTRAINT "
									+ getAttrFKName(attr)
									+ " FOREIGN KEY (c_obj_id,c_tr_id) REFERENCES "
									+ ctName
									+ " (c_obj_id,c_tr_id) ON DELETE CASCADE ON UPDATE CASCADE)");
				}
			}

			// Добавляем AUTO_INCREMENT на c_obj_id в таблице ct99
			ust
					.executeUpdate("ALTER TABLE "+getClassTableNameComp(99)+" MODIFY c_obj_id BIGINT NOT NULL AUTO_INCREMENT");

			ust.close();
			version = 3;
		}
		
    	if (version < 4) {
    		QueryRunner qr = new QueryRunner(true);
    		qr.update(conn,
    			"ALTER TABLE "+getDBPrefix()+"t_classes ADD (c_comment TEXT)");
    		qr.update(conn,
    			"ALTER TABLE "+getDBPrefix()+"t_attrs ADD ("
    			+ "c_rattr_id BIGINT,"
    			+ "c_sattr_id BIGINT,"
    			+ "c_sdesc BIT,"
    			+ "c_comment TEXT,"
    			+ "FOREIGN KEY (c_rattr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id),"
    			+ "FOREIGN KEY (c_sattr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id))");
    		qr.update(conn,
    			"ALTER TABLE "+getDBPrefix()+"t_methods ADD (c_comment TEXT)");
    		version = 4;
    	}
    	
    	if (version < 6) {
    		// Зпрос для проверики наличия столбца в таблице.
    		PreparedStatement colPst = conn.prepareStatement(
    				"SELECT COUNT(*) from information_schema.columns" +
    				" WHERE table_schema=? AND table_name=? AND column_name=?");
    		colPst.setString(1, conn.getCatalog());
    		
    		QueryRunner qr = new QueryRunner(true);
    		Statement st = conn.createStatement();
    		ResultSet rs = st.executeQuery(
    				"SELECT c_id,c_class_id,c_is_multilingual" +
    				" FROM "+getDBPrefix()+"t_attrs WHERE c_rattr_id is null and c_col_type=1");
    		while (rs.next()) {
    			long attrId = rs.getLong(1);
    			long clsId = rs.getLong(2);
    			boolean isMultiLang = rs.getBoolean(3);
    			String atName = getAttrTableName(attrId);
    			
    			// Проверяем наличие столбца c_id в таблице
    			colPst.setString(2, atName);
    			colPst.setString(3, "c_id");
    			ResultSet colRs = colPst.executeQuery();
    			// Запрос возвращает 1 строку с количеством столбцов в таблице.
    			colRs.next();
    			int colCnt = colRs.getInt(1);
    			colRs.close();
    			// Если столбец отсутствует, то добавляем его.
    			if (colCnt == 0) {
					qr.update(conn, "ALTER TABLE " + atName + " ADD (c_id BIGINT)");
    			}
				if (isMultiLang) {
					qr.update(conn, "UPDATE " + atName + " SET c_id=c_index*c_lang_id");
				} else {
					qr.update(conn, "UPDATE " + atName + " SET c_id=c_index");
				}
				qr.update(conn, "ALTER TABLE " + atName + " MODIFY c_id BIGINT NOT NULL");
    		}
    		rs.close();
    		st.close();
    		colPst.close();
			version = 6;
    	}
    	
    	if (version < 7) {
			log.info("Апгрейд БД до версии 7...");
    		// Зпрос для проверики наличия индекса в таблице.
    		PreparedStatement idxPst = conn.prepareStatement(
    				"select count(*) from information_schema.STATISTICS " +
    				"where table_schema=? and table_name=? and index_name=?");
    			
    		idxPst.setString(1, conn.getCatalog());
    			
    		Statement ust = conn.createStatement();
    		Statement qst = conn.createStatement();
    		ResultSet rs = qst.executeQuery("SELECT c_id FROM "+getDBPrefix()+"t_classes WHERE c_id>99");
    		while (rs.next()) {
    			long clsId = rs.getLong(1);
				// Отключаем проверку FK
				ust.executeUpdate("SET FOREIGN_KEY_CHECKS=0");
				try {
	    			// Удаление кривых объектов
	    			ust.executeUpdate(
	    					"DELETE FROM "+getClassTableNameComp(clsId) + " WHERE c_obj_id IN " +
	    					"(SELECT c_obj_id FROM "+getClassTableNameComp(99)+" WHERE c_uid LIKE '%\\_')");
				} finally {
					ust.executeUpdate("SET FOREIGN_KEY_CHECKS=1");
				}
    			log.info("Создание индекса для " + getClassTableNameComp(clsId) + ": ");
    			String tbName = getClassTableNameComp(clsId);
    			String idxName = "uid" + clsId + "idx";
    			idxPst.setString(2, tbName);
    			idxPst.setString(3, idxName);
    			ResultSet irs = idxPst.executeQuery();
    			irs.next();
    			if (irs.getInt(1) == 0) {
    				ust.executeUpdate("CREATE UNIQUE INDEX " + idxName + " ON " + tbName + "(c_uid,c_tr_id)");
    				log.info("OK");
    			} else {
    				log.info("индекс уже существует");
    			}
    			irs.close();
    		}
    		rs.close();
			// Удаление кривых объектов
			ust.executeUpdate("DELETE FROM "+getClassTableNameComp(99)+" WHERE c_uid LIKE '%\\_'");
    		ust.close();
    		qst.close();
    		idxPst.close();
			log.info("Апгрейд БД до версии 7 успешно завершен.");
			version = 7;
    	}
    	// Логирование только реплицируемых классов и атрибутов
    	if (version < 8) {
			log.info("Апгрейд БД до версии 8...");
			
			Statement st = conn.createStatement();
			// Удаляем индекс c_tr_id из таблицы t_changes
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changes DROP INDEX c_tr_id");
			// Добавляем колонку c_is_repl в таблицу t_changes
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changes ADD (c_is_repl BIT)");
			st.executeUpdate("UPDATE "+getDBPrefix()+"t_changes SET c_is_repl=true");
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changes MODIFY c_is_repl BIT NOT NULL");
			// Создаем составной индекс c_tr_id,c_is_repl
			st.executeUpdate("CREATE INDEX ch_tr_repl_idx ON "+getDBPrefix()+"t_changes(c_tr_id,c_is_repl)");
	
			log.info("Апгрейд БД до версии 8 успешно завершен.");
			version = 8;
    	}
    	// Изменение механизма блокировок
    	// Исключение таблиц для классов без атрибутов и не являющихся типом
    	// какого-либо атрибута
    	// Оптимизация работы с таблицей ct99
    	if (version < 9) {
			log.info("Апгрейд БД до версии 9 ...");
    		
			Statement st = conn.createStatement();

    		// Таблица классов
			// c_mod
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_classes ADD c_mod INTEGER");
    		st.executeUpdate("UPDATE "+getDBPrefix()+"t_classes SET c_mod=0");
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_classes MODIFY c_mod INTEGER NOT NULL");
			// c_cuid
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_classes ADD c_cuid CHAR(36)");
    		st.executeUpdate("UPDATE "+getDBPrefix()+"t_classes SET c_cuid=c_id");
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_classes MODIFY c_cuid CHAR(36) NOT NULL");
			st.executeUpdate("CREATE UNIQUE INDEX idx_cuid ON "+getDBPrefix()+"t_classes(c_cuid)");

			// Таблица атрибутов
			// c_auid
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_attrs ADD c_auid CHAR(36)");
    		st.executeUpdate("UPDATE "+getDBPrefix()+"t_attrs SET c_auid=c_id");
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_attrs MODIFY c_auid CHAR(36) NOT NULL");
			st.executeUpdate("CREATE UNIQUE INDEX idx_auid ON "+getDBPrefix()+"t_attrs(c_auid)");

			// Таблица методов
			// c_id
			// Запрос для нахождения имени PK для таблицы
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_methods ADD c_muid CHAR(36)");
    		st.executeUpdate("UPDATE "+getDBPrefix()+"t_methods SET c_muid=c_id");
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_methods MODIFY c_muid CHAR(36) NOT NULL");
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_methods DROP COLUMN c_id");
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_methods ADD PRIMARY KEY (c_muid)");
			
			// Таблица изменения модели данных
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changescls MODIFY c_entity_id CHAR(36) NOT NULL");

			// Создаем таблицу блокировок
    		createLocksTable(conn);
			
    		// Переносим существующие блокировки
			KrnClass flowCls = getClassByNameComp("Flow");
			KrnAttribute procAttr = getAttributeByNameComp(flowCls, "processInstance");
			KrnAttribute lockAttr = getAttributeByNameComp(flowCls, "lockObjects");
			KrnClass procCls = getClassByIdComp(procAttr.typeClassId);
			KrnAttribute pdefAttr = getAttributeByNameComp(procCls, "processDefinition");
			KrnAttribute trnsAttr = getAttributeByNameComp(procCls, "transId");

			String sql = "INSERT INTO "+getDBPrefix()+"t_locks (c_obj_id,c_locker_id,c_flow_id,c_scope)" +
					" SELECT "+getColumnName(lockAttr) + ","+getColumnName(pdefAttr) +
					",max(f.c_obj_id) ," + Lock.LOCK_FLOW +
					" FROM "+getClassTableNameComp(flowCls.id) + " f," + 
					getAttrTableName(lockAttr) + " la," +
					getClassTableNameComp(procAttr.typeClassId) + " p" +
					" WHERE la.c_obj_id=f.c_obj_id AND p.c_obj_id="+getColumnName(procAttr) +
					" GROUP BY "+getColumnName(lockAttr) + ","+getColumnName(pdefAttr);
			st.executeUpdate(sql);

			// Таблица ct99
			KrnClass objCls = getClassByIdComp(99);
			// Переносим атрибут 'config' из Object в конкретные классы
			KrnAttribute cfgAttr = getAttributeByNameComp(objCls, "config");
			// Отчеты
			KrnClass cls = getClassByNameComp("ReportPrinter");
			createConfigAttr(99, cfgAttr, cls.id, "75310be7-e91a-44a8-8f85-1b791ac437e9");
			// Интерфейсы
			cls  = getClassByNameComp("UI");
			createConfigAttr(99, cfgAttr, cls.id, "0b6ec983-abb5-4826-940a-d82aca81fb23");
			// Процессы
			cls = getClassByNameComp("ProcessDef");
			createConfigAttr(99, cfgAttr, cls.id, "7d1f2b62-2a19-46bd-b6d9-eedc90c7c197");
			// Фильтры
			cls = getClassByNameComp("Filter");
			createConfigAttr(99, cfgAttr, cls.id, "b76f5b01-c2de-49ee-af01-3722748d8787");
			// Таймеры
			cls = getClassByNameComp("Timer");
			createConfigAttr(99, cfgAttr, cls.id, "56e830c5-3290-4ec8-a530-c5724674cadc");
			// Пункты обмена
			cls = getClassByNameComp("BoxExchange");
			createConfigAttr(99, cfgAttr, cls.id, "f003252d-dea0-4bf5-817b-dabe7f584a7b");
			// Удалем атбрибут 'config' в классе 'Object'
			deleteAttributeComp(cfgAttr, false, version);
			// Удалем атбрибут 'locks' в классе 'Object'
			deleteAttributeComp(getAttributeByNameComp(objCls, "locks"), false, version);
			// Удаляем из ct99 все записи из зависших транзакций
			// Откат зависших транзакций делаем позже после изменений в структуре
			//  т.к. процедура rollbackLongTransaction работает уже с новой структурой
			List<Long> badTrIds = new ArrayList<Long>();
			ResultSet rs = st.executeQuery(
					"SELECT DISTINCT c_tr_id FROM "+getClassTableNameComp(99)+" " +
					"WHERE c_tr_id<>0 AND c_tr_id NOT IN (" +
					"SELECT "+getColumnName(trnsAttr) + " FROM "+getClassTableNameComp(trnsAttr.classId) +
					" WHERE "+getColumnName(trnsAttr) +" IS NOT NULL)");
			while (rs.next()) {
				badTrIds.add(rs.getLong(1));
			}
			rs.close();
			PreparedStatement pst = conn.prepareStatement(
					"DELETE FROM "+getClassTableNameComp(99)+" WHERE c_tr_id=?");
			for (Long trId : badTrIds) {
				pst.setLong(1, trId);
				pst.executeUpdate();
			}
			pst.close();
			// Оставляем только по одной записи для каждого объекта
			rs = st.executeQuery(
					"SELECT c_obj_id,c_tr_id FROM "+getClassTableNameComp(99)+" t1 " +
					"WHERE c_tr_id<>0 AND EXISTS (" +
					"SELECT * FROM "+getClassTableNameComp(99)+" t2 " +
					"WHERE t2.c_obj_id=t1.c_obj_id AND t2.c_tr_id=0)");
			List<Pair<Long, Long>> recs = new ArrayList<Pair<Long,Long>>();
			while (rs.next()) {
				recs.add(new Pair<Long, Long>(rs.getLong(1), rs.getLong(2)));
			}
			rs.close();
			pst = conn.prepareStatement(
					"DELETE FROM "+getClassTableNameComp(99)+" WHERE c_obj_id=? AND c_tr_id=?");
			for (Pair<Long, Long> rec : recs) {
				pst.setLong(1, rec.first);
				pst.setLong(2, rec.second);
				pst.executeUpdate();
			}
			pst.close();
			// Удаляем первичный ключ (необходимо для удаления 'c_tr_id')
			st.executeUpdate("ALTER TABLE "+getClassTableNameComp(99)+" MODIFY c_obj_id BIGINT NOT NULL");
			st.executeUpdate("ALTER TABLE "+getClassTableNameComp(99)+" DROP PRIMARY KEY");
			// Удаляем индексы содержащие 'c_tr_id' (необходимо для удаления 'c_tr_id')
			pst = conn.prepareStatement(
					"SELECT index_name FROM information_schema.statistics " +
					"WHERE table_schema=? AND table_name=? AND column_name=?");
			pst.setString(1, conn.getCatalog());
			pst.setString(2, "ct99");
			pst.setString(3, "c_tr_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				String idxName = getString(rs, 1);
				st.executeUpdate("ALTER TABLE "+getClassTableNameComp(99)+" DROP INDEX " + idxName);
			}
			rs.close();
			pst.close();
			// Удаляем лишние колонки
			st.executeUpdate("ALTER TABLE "+getClassTableNameComp(99)+" DROP COLUMN c_is_del");
			st.executeUpdate("ALTER TABLE "+getClassTableNameComp(99)+" DROP COLUMN c_tr_id");
			// Создаем первичный ключ
			st.executeUpdate("ALTER TABLE "+getClassTableNameComp(99)+" ADD (PRIMARY KEY (c_obj_id))");
			st.executeUpdate("ALTER TABLE "+getClassTableNameComp(99)+" MODIFY c_obj_id BIGINT NOT NULL AUTO_INCREMENT");
			// Создаем уникальный индекс для 'c_uid'
			st.executeUpdate("CREATE UNIQUE INDEX uid99idx ON "+getClassTableNameComp(99)+" (c_uid)");
			
			// Теперь, после всех изменений в структуре откатываем повисшие
			//  транзакции
			for (Long trId : badTrIds) {
				rollbackLongTransactionComp(trId);
			}

			// Удаляем атрибут 'зап табл цикла обмена'
			KrnClass uclsCls = getClassByNameComp("Пользовательский класс");
			KrnAttribute ztexAttr = getAttributeByNameComp(uclsCls, "зап табл цикла обмена");
			if (ztexAttr != null)
				deleteAttributeComp(ztexAttr, false, version);

			// Переносим атрибут в другие классы 'структура баз'
			KrnAttribute stbAttr = getAttributeByNameComp(uclsCls, "структура баз");
			if (stbAttr != null) {
				cls = getClassByNameComp("ДОКУМЕНТ ПРЕДПРИЯТИЯ");
				createStrucBaseAttr(stbAttr, cls, "7fb90f9e-1cf0-4cd2-84a6-7512873e1ba9");
				cls = getClassByNameComp("Производственная структура");
				createStrucBaseAttr(stbAttr, cls, "15c83631-33fd-44f9-b6d2-fa00853c88aa");
				cls = getClassByNameComp("Зап табл штатн распис");
				createStrucBaseAttr(stbAttr, cls, "30d65e00-6b4f-4789-bbc3-64fd9ff838e8");
				cls = getClassByNameComp("Зап табл участков ответств");
				createStrucBaseAttr(stbAttr, cls, "c1d930fe-fe9c-4219-bc4d-3da7ba354b6b");
				cls = getClassByNameComp("Зап табл подписаний");
				createStrucBaseAttr(stbAttr, cls, "a8f3f9d2-829c-44a9-b3dd-643416273af6");
				cls = getClassByNameComp("Зап табл плана");
				createStrucBaseAttr(stbAttr, cls, "a5cbf906-b330-4d42-8342-d85b516734b3");
				cls = getClassByNameComp("Персонал");
				createStrucBaseAttr(stbAttr, cls, "3d589a4c-a9d6-4ba2-a7fd-723d0ff700c7");
				cls = getClassByNameComp("Конкурсная процедура");
				createStrucBaseAttr(stbAttr, cls, "16d98306-f99b-4982-8ee2-0c746540f728");
				cls = getClassByNameComp("Процедура раб со статданными");
				createStrucBaseAttr(stbAttr, cls, "ade39679-f71b-4a4f-ac4b-c62d14823348");
				cls = getClassByNameComp("Аттестационная процедура");
				createStrucBaseAttr(stbAttr, cls, "9901a1b0-71e0-4899-823f-6514fc661b24");
				cls = getClassByNameComp("Зап табл аттестанта");
				createStrucBaseAttr(stbAttr, cls, "aedb0fe9-32e3-456f-8b2f-6cefa8b2a4a5");
				cls = getClassByNameComp("кдр::нк::olap::Зап табл куба 1");
				createStrucBaseAttr(stbAttr, cls, "51df8562-b7be-445b-94cb-7421db4bb97d");
				cls = getClassByNameComp("кдр::нк::olap::Зап табл куба 2");
				createStrucBaseAttr(stbAttr, cls, "6a076d77-60ec-42c7-b9ec-f0dcedf5a0e8");
				cls = getClassByNameComp("кдр::нк::olap::Зап табл куба 3");
				createStrucBaseAttr(stbAttr, cls, "7d462b6c-6006-434e-b997-5b699c22d87f");
				cls = getClassByNameComp("кдр::нк::olap::Зап табл куба 4");
				createStrucBaseAttr(stbAttr, cls, "28b5d916-f581-47e8-8086-32b4a8c4eddd");
				cls = getClassByNameComp("кдр::нк::olap::Зап табл куба 5");
				createStrucBaseAttr(stbAttr, cls, "694c64ec-36f3-4e79-88e7-80bd8cf23e93");
				cls = getClassByNameComp("кдр::нк::olap::Зап табл куба 6");
				createStrucBaseAttr(stbAttr, cls, "02f46f62-7d43-4cd3-ad45-8de194f92cd5");

				// Удаляем атрибут 'структура баз'
				deleteAttributeComp(stbAttr, false, version);
			}
			
			// Переносим атрибут в другие классы 'bases'
			KrnClass sysCls = getClassByNameComp("Системный класс");
			KrnAttribute basesAttr = getAttributeByNameComp(sysCls, "bases");
			if (basesAttr != null) {
				cls = getClassByNameComp("ReportPrinter");
				createBasesAttr(basesAttr, cls.id, "34469857-bfb6-49c8-b01f-dfd98805fdb7");
	
				// Удаляем атрибут 'bases'
				deleteAttributeComp(basesAttr, false, version);
			}

			// Удаление таблиц для пустых классов
			cls = getClassByNameComp("Пользовательский класс");
			makeDummy(cls);
			cls = getClassByNameComp("Системный класс");
			makeDummy(cls);
			cls = getClassByNameComp("Event");
			makeDummy(cls);
			cls = getClassByNameComp("WorkFlow");
			makeDummy(cls);
			cls = getClassByNameComp("АДМ КЛАСС");
			makeDummy(cls);
			cls = getClassByNameComp("ВСПОМОГАТЕЛЬНЫЙ КЛАСС");
			makeDummy(cls);
			cls = getClassByNameComp("Вспомогат класс КАДРЫ");
			makeDummy(cls);
			cls = getClassByNameComp("ИЕРАРХИЯ");
			makeDummy(cls);
			cls = getClassByNameComp("Иерархия БУХУЧЕТ");
			makeDummy(cls);
			cls = getClassByNameComp("Иерархия ВНЕШНИЕ СИСТЕМЫ");
			makeDummy(cls);
			cls = getClassByNameComp("Иерархия ДОК-ОБОРОТ");
			makeDummy(cls);
			cls = getClassByNameComp("Иерархия КАДРЫ");
			makeDummy(cls);
			cls = getClassByNameComp("Иерархия ОБЩ");
			makeDummy(cls);
			cls = getClassByNameComp("РЕАЛЬНЫЙ КЛАСС");
			makeDummy(cls);
			cls = getClassByNameComp("Реальный класс БУХУЧЕТ");
			makeDummy(cls);
			cls = getClassByNameComp("Реальный класс КАДРЫ");
			makeDummy(cls);
			cls = getClassByNameComp("Реальный класс ОБЩ");
			makeDummy(cls);
			cls = getClassByNameComp("Справочник БУХУЧЕТ");
			makeDummy(cls);
			cls = getClassByNameComp("Справочник ДОК-ОБОРОТ");
			makeDummy(cls);
			cls = getClassByNameComp("Справочник КАДРЫ");
			makeDummy(cls);
			cls = getClassByNameComp("Справочник ОБЩ");
			makeDummy(cls);
			cls = getClassByNameComp("ТАБЛИЦА");
			makeDummy(cls);
			cls = getClassByNameComp("Таблица БУХУЧЕТ");
			makeDummy(cls);
			cls = getClassByNameComp("Таблица ДОК-ОБОРОТ");
			makeDummy(cls);
			cls = getClassByNameComp("Таблица КАДРЫ");
			makeDummy(cls);
			cls = getClassByNameComp("Таблица ОБЩ");
			makeDummy(cls);
			cls = getClassByNameComp("Тезаурус БУХУЧЕТ");
			makeDummy(cls);
//			cls = getClassByNameComp("ТЕХНИЧЕСКИЙ КЛАСС");
//			makeDummy(cls);
			cls = getClassByNameComp("Технич класс КАДРЫ");
			makeDummy(cls);
			cls = getClassByNameComp("Технический класс БУХУЧЕТ");
			makeDummy(cls);
			
			
			log.info("Апгрейд БД до версии 9 успешно завершен.");
			version = 9;
    	}
    	// Исключение таблиц для мультиязычных атрибутов
    	if (version < 10) {
			log.info("Апгрейд БД до версии 10 ...");
			
    		QueryRunner qr = new QueryRunner(true);
    		Statement st = conn.createStatement();
    		// Выбираем все системые языки (сортировка по c_obj_id)
    		List<KrnObject> sysLangs = getSystemLangs();
    		// Выбираем все мультиязычные атрибуты
    		AttributeRsh rsh = new AttributeRsh();
    		List<KrnAttribute> attrs = qr.query(
    				conn,
    				"SELECT * FROM "+getDBPrefix()+"t_attrs WHERE c_is_multilingual=1",
    				rsh);
    		for (KrnAttribute attr : attrs) {
    			String ctName = getClassTableNameComp(attr.classId);
				String atName = getAttrTableName(attr);
    			String cmName = getColumnName(attr);
    			if (attr.collectionType == COLLECTION_NONE) {
            		// Если не коллекция
					// Создаем колонки в основной таблице
    				createAttributeInDatabase(attr);
    				for (int i = 0; i < sysLangs.size(); i++) {
    					long langId = sysLangs.get(i).id;
    					String ncmName = cmName + "_" + (i + 1);
    					// Копируем данные в колонки
    					st.executeUpdate("UPDATE " + ctName +
    							" SET " + ncmName + "=(SELECT " + cmName +
    							" FROM " + atName + " WHERE " +
    							atName + ".c_obj_id=" +	ctName + ".c_obj_id AND " +
    							atName + ".c_tr_id=" + ctName + ".c_tr_id AND " +
    							atName + ".c_lang_id=" + langId + " AND c_del=0)");
    				}
    				// Удаляем дополнительную таблицу
    				st.executeUpdate("DROP TABLE " + atName);
    			} else {
    				// Если коллекция
    				// Переименовываем дополнительную таблицу
    				st.executeUpdate("RENAME TABLE " + atName + " TO " + atName + "_tmp");
    				// Удаляем FK, иначе MySQL не дает создать новую таблицу
    				// с таким же именем FK
    				st.executeUpdate("ALTER TABLE " + atName + "_tmp DROP FOREIGN KEY " + getAttrFKName(attr));
    				// Создаем новую таблицу
    				createAttributeInDatabase(attr);
    				// Создаем записи в новой таблице
    				String fields = "c_obj_id,c_tr_id,c_del";
    				if (attr.collectionType == COLLECTION_ARRAY)
    					fields += ",c_index,c_id";
    				st.executeUpdate("INSERT INTO " + atName + "(" + fields +
    						") SELECT DISTINCT " + fields + " FROM " + atName + "_tmp");
    				for (int i = 0; i < sysLangs.size(); i++) {
    					Long langId = sysLangs.get(i).id;
    					String ncmName = cmName + "_" + (i + 1);
    					// Копируем данные в колонки
    					String sql = "UPDATE " + atName +
    							" SET " + ncmName + "=(SELECT " + cmName +
    							" FROM " + atName + "_tmp t WHERE " +
    							atName + ".c_obj_id=t.c_obj_id AND " +
    							atName + ".c_tr_id=t.c_tr_id AND " +
    							atName + ".c_del=t.c_del AND " +
    							"t.c_lang_id=" + langId;
    					if (attr.collectionType == COLLECTION_ARRAY) {
    						sql += " AND t.c_index=" + atName + ".c_index";
    					}
    					sql += ")";
    					st.executeUpdate(sql);
    				}
    				// Удаляем старую таблицу
					st.executeUpdate("DROP TABLE " + atName + "_tmp");
    			}
    		}
    		// Закрываем все ресурсы
    		st.close();
			log.info("Апгрейд БД до версии 10 успешно завершен.");
			version = 10;
    	}
    	
    	//Создание инфраструктуры для индексов
    	if(version < 11){
    		log.info("Апгрейд БД до версии 11 ...");
    		createIndexTables(conn);    		
    		log.info("Апгрейд БД до версии 11 успешно завершен.");
			version = 11;
    	}
    	
    	if(version < 12) {
    		log.info("Апгрейд БД до версии 12 ...");
    		Statement st = conn.createStatement();
    		try {
        		log.info("Добавление колонки 'c_class_id' в таблицу 't_changes'.");
    			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changes ADD c_class_id BIGINT");
    		} catch (Exception e) {
        		log.info("Колонка 'c_class_id' в таблице 't_changes' уже существует.");
    		}
    		log.info("Заполнение колонки 'c_class_id' в таблице 't_changes'.");
    		st.executeUpdate("UPDATE "+getDBPrefix()+"t_changes SET c_class_id=(SELECT c_class_id FROM "+getClassTableNameComp(99)+" WHERE c_obj_id=c_object_id)");
    		st.close();
    		log.info("Апгрейд БД до версии 12 успешно завершен.");
    		version = 12;
    	}
    	
    	if (version < 13) {
            upgradeTo13();
			version = 13;
        }
    	if (version < 14) {
            upgradeTo14();
			version = 14;
        }
    	if (version < 15) {
    		upgradeTo15();
            version = 15;
        }
    	if (version < 16) {
            upgradeTo16();
			version = 16;
        }

    	if (version < 17) {
    		if (upgradeTo17()){
            	version = 17;
            }
        }

    	if (version < 18) {
            upgradeTo18();
			version = 18;
        }
    	if (version < 19) {
    	    upgradeTo19();
    	    version = 19;
    	}
    	
    	if (version < 20) {
            upgradeTo20();
            version = 20;
        }
    	if (version < 21) {
    		log.info("Апгрейд БД до версии 21 ...");
    		Statement st = conn.createStatement();
    		st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_attrs modify c_tname varchar(255)");
    		st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_classes modify c_tname varchar(255)");
    		st.close();
    		log.info("Апгрейд БД до версии 21 успешно завершен.");
            version = 21;
        }
    	if (version < 22) {
            upgradeTo22();
            version = 22;
        }
    	if (version < 23) {
            upgradeTo23();
            version = 23;
        }
    	if (version < 24) {
            upgradeTo24();
            version = 24;
        }
    	if (version < 25) {
            upgradeTo25();
            version = 25;
        }
    	if (version < 26) {
            upgradeTo26();
            version = 26;
        }
    	if (version < 27) {
            upgradeTo27();
            version = 27;
        }
    	if (version < 28) {
            upgradeTo28();
            version = 28;
            commit();
        }
    	if (version < 29) {
            upgradeTo29();
            version = 29;
        }
    	if (version < 30) {
            upgradeTo30();
            version = 30;
        }
    	if (version < 31) {
            upgradeTo31();
            version = 31;
        }
    	if (version < 32) {
            upgradeTo32();
            version = 32;
        }
    	if (version < 33) {
            upgradeTo33();
            version = 33;
        }
    	if (version < 34) {
            upgradeTo34();
            version = 34;
        }
    	if (version < 35) {
            upgradeTo35();
            version = 35;
        }
    	if (version < 36) {
            upgradeTo36();
            version = 36;
        }
    	if (version < 37) {
            upgradeTo37();
            version = 37;
        }
    	if (version < 38) {
            upgradeTo38();
            version = 38;
        }
    	if (version < 39) {
            upgradeTo39();
            version = 39;
        }
    	if (version < 40) {
            upgradeTo40();
            version = 40;
        }
    	if (version < 41) {
            upgradeTo41();
            version = 41;
        }
    	if (version < 42) {
            upgradeTo42();
            version = 42;
        }
    	if (version < 43) {
            upgradeTo43();
            version = 43;
        }
    	if (version < 44) {
            upgradeTo44();
            version = 44;
        }
    	if (version < 45) {
            upgradeTo45();
            version = 45;
        }
    	if (version < 46) {
            upgradeTo46();
            version = 46;
        }
    	if (version < 47) {
            upgradeTo47();
            version = 47;
        }
    	if (version < 48) {
            upgradeTo48();
            version = 48;
        }
    	if (version < 49) {
            upgradeTo49();
            version = 49;
        }
    	if (version < 50) {
            upgradeTo50();
            version = 50;
        }
    	if (version < 51) {
            upgradeTo51();
            version = 51;
        }
    	if (version < 52) {
            upgradeTo52();
            version = 52;
        }
    	if (version < 53) {
            upgradeTo53();
            version = 53;
        }
    	if (version < 54) {
            upgradeTo54();
            version = 54;
        }
    	if (version < 55) {
            upgradeTo55();
            version = 55;
        }
    	if (version < 56) {
            upgradeTo56();
            version = 56;
        }
    	if (version < 57) {
            upgradeTo57();
            version = 57;
        }
    	if (version < 58) {
            upgradeTo58();
            version = 58;
        }
    	if (version < 59) {
    		upgradeTo59();
    		version = 59;
    	}
    	if (version < 60) {
    		upgradeTo60();
    		version = 60;
    	}
    	if (version < 61) {
    		upgradeTo61();
    		version = 61;
    	}
    	if (version < 62){
    		upgradeTo62();
    		version = 62;
    	}
    	if (version < 63) {
    		upgradeTo63();
    		version = 63;
    	} 
    	if (version < 64) {
    		upgradeTo64();
    		version = 64;
    	}
    	if (version < 65) {
    		upgradeTo65();
    		version = 65;
    	}
    	if (version < 66) {
    		upgradeTo66();
    		version = 66;
    	}
    	if (version < 67) {
    		upgradeTo67();
    		version = 67;
    	}
    	if (version < 68) {
    		upgradeTo68();
    		version = 68;
    	}
    	if (version < 69) {
    		upgradeTo69();
    		version = 69;
    	}
    	if (version < 70) {
    		upgradeTo70();
    		version = 70;
    	}
    	if (version < 71) {
    		upgradeTo71();
    		version = 71;
    	}
    	if (version < 72) {
    		upgradeTo72();
    		version = 72;
    	}
    	if (version < 73) {
    		upgradeTo73();
    		version = 73;
    	}
    	if (version < 74) {
    		upgradeTo74();
    		version = 74;
    	}
    	if (version < 75) {
    		upgradeTo75();
    		version = 75;
    	}
    	if (version < 76) {
    		upgradeTo76();
    		version = 76;
    	}
    	if (version < 77) {
    		upgradeTo77();
    		version = 77;
    	}
    	return version;
	}
	
	private boolean indexExists(PreparedStatement idxPst, String tbName, String idxName) throws SQLException {
		boolean res = false;
		idxPst.setString(2, tbName);
		idxPst.setString(3, idxName);
		ResultSet rs = idxPst.executeQuery();
		if (rs.next()) {
			res = rs.getInt(1) > 0;
		}
		rs.close();
		return res;
	}
	
	protected DriverException convertException(SQLException e) {
		if (e.getErrorCode() == 1213
				|| (e.getNextException() != null && e.getNextException()
						.getErrorCode() == 1213)
				|| e.getMessage().toLowerCase(Constants.OK).contains("deadlock"))
			return new DriverException(e.getMessage(), ErrorCodes.ER_LOCK_DEADLOCK);
		else if (e.getErrorCode() == 1205)
				return new DriverException(e.getMessage(), ErrorCodes.LOCK_WAIT_TIMEOUT);
		else {
			log.error("ErrorCode=" + e.getErrorCode());
			return new DriverException(e);
		}
	}

	protected void createConfigAttr(long srcClsId, KrnAttribute srcAttr, long dstClsId, String uid)
	throws SQLException, DriverException {
		// Создаем атрибут
		KrnAttribute attr = createAttribute(
				-1, uid, dstClsId, CID_BLOB, "config",
				COLLECTION_NONE, false, false, false, true, 0, 0, 0, 0,
				false, false, null, 0);
		// Копируем зачение
		Statement st = conn.createStatement();
		String srcCm = getColumnName(srcAttr);
		String dstCm = getColumnName(attr);
		PreparedStatement pst = conn.prepareStatement(
				"UPDATE "+getClassTableName(dstClsId) + " SET " + dstCm + "=? " +
				"WHERE c_obj_id=?");
		ResultSet rs = st.executeQuery(
				"SELECT c_obj_id," + srcCm +
				" FROM "+getClassTableName(99)+" WHERE c_class_id=" + dstClsId + " AND " + srcCm + " IS NOT NULL");
		while (rs.next()) {
			Object value = getValue(rs, srcCm, PC_BLOB);
			setValue(pst, 1, PC_BLOB, value);
			pst.setLong(2, rs.getLong(1));
			pst.executeUpdate();
		}
		rs.close();
		st.close();
		pst.close();
	}
	
	protected void createStrucBaseAttr(KrnAttribute srcAttr, KrnClass dstCls, String uid)
	throws SQLException, DriverException {
		if (dstCls == null)
			return;
		// Создаем атрибут
		KrnAttribute attr = createAttribute(
				-1, uid, dstCls.id, srcAttr.typeClassId, srcAttr.name,
				COLLECTION_NONE, false, false, false, true, 0, 0, 0, 0,
				false, false, null, 0);
		// Копируем зачение
		Statement st = null;
		try {
			st = conn.createStatement();
			st.executeUpdate(
					"UPDATE "+getClassTableName(dstCls.id) +
					" SET "+getColumnName(attr) + "=(SELECT "+getColumnName(srcAttr) +
					" FROM "+getClassTableName(srcAttr.classId) + " WHERE "+getClassTableName(dstCls.id) +
					".c_obj_id="+getClassTableName(srcAttr.classId) + ".c_obj_id AND "+getClassTableName(dstCls.id)
					+ ".c_tr_id="+getClassTableName(srcAttr.classId) + ".c_tr_id)");
		} catch (SQLException e) {
            throw convertException(e);
		} finally {
			DbUtils.closeQuietly(st);
		}
	}
	
	protected void createBasesAttr(KrnAttribute srcAttr, long dstClsId, String uid)
	throws SQLException, DriverException {
		// Создаем атрибут
		KrnAttribute attr = createAttribute(
				-1, uid, dstClsId, srcAttr.typeClassId, srcAttr.name,
				srcAttr.collectionType, false, false, false, true, 0, 0, 0, 0,
				false, false, null, 0);
		// Копируем зачение
		String srcTbName = getAttrTableName(srcAttr);
		String dstTbName = getAttrTableName(attr);
		Statement st = conn.createStatement();
		st.executeUpdate(
				"INSERT INTO " + dstTbName + "(c_obj_id,c_tr_id,c_del,"+getColumnName(attr)
				+ ") SELECT DISTINCT t1.c_obj_id,t1.c_tr_id,t1.c_del,"+getColumnName(srcAttr) +
				" FROM " + srcTbName + " t1,"+getClassTableName(99)+" WHERE "+getClassTableName(99)+".c_obj_id=t1.c_obj_id AND c_class_id=" + dstClsId);
		st.close();
	}

	protected void createLocksTable(Connection conn) throws SQLException {
		Statement st = conn.createStatement();
		// Таблица блокировок объектов
		st.executeUpdate(
				"CREATE TABLE "+getDBPrefix()+"t_locks (" +
				"c_obj_id BIGINT NOT NULL," +
				"c_locker_id BIGINT NOT NULL," +
				"c_flow_id BIGINT," +
				"c_session_id CHAR(36)," +
				"c_scope INTEGER NOT NULL," +
				"PRIMARY KEY (c_obj_id,c_locker_id))"
		);
		st.executeUpdate(
				"CREATE INDEX lck_flow_id_idx ON "+getDBPrefix()+"t_locks(c_flow_id)");
		st.executeUpdate(
				"CREATE INDEX lck_session_id_idx ON "+getDBPrefix()+"t_locks(c_session_id)");
		st.close();
	}
	
	protected void createLockMethodsTable(Connection conn) throws SQLException {
		Statement st = conn.createStatement();
		// Таблица блокировок объектов
		st.executeUpdate(
				"CREATE TABLE "+getDBPrefix()+"t_lock_methods (" +
				"c_muid CHAR(36) NOT NULL," +
				"c_flow_id BIGINT," +
				"c_session_id CHAR(36)," +
				"c_scope INTEGER NOT NULL," +
				"PRIMARY KEY (c_muid))"
		);
		st.executeUpdate(
				"CREATE INDEX lckm_flow_id_idx ON "+getDBPrefix()+"t_lock_methods(c_flow_id)");
		st.executeUpdate(
				"CREATE INDEX lckm_session_id_idx ON "+getDBPrefix()+"t_lock_methods(c_session_id)");
		st.close();
	}
	
	//Созданием таблицы для хранения информации о многостолбцовых индексах
	protected void createIndexTables(Connection conn) throws SQLException{
		Statement st = conn.createStatement();
		String sql = "";
		
		sql =
			"CREATE TABLE IF NOT EXISTS "+getDBPrefix()+"t_indexes(" +
			" c_id BIGINT NOT NULL AUTO_INCREMENT," +
			" c_uid CHAR(36) NOT NULL," +
			" c_class_id BIGINT NOT NULL," +
			" c_is_multilingual BIT NOT NULL," +
			" PRIMARY KEY(c_id)," +
			" FOREIGN KEY(c_class_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE" +
			" ) ENGINE = " + ENGINE +
			"";
		st.executeUpdate(sql);
		
		sql = 
			"CREATE TABLE IF NOT EXISTS "+getDBPrefix()+"t_indexkeys(" +
			" c_index_id BIGINT NOT NULL," +
			" c_attr_id BIGINT NOT NULL," +
			" c_keyno BIGINT NOT NULL," +
			" c_is_desc BIT NOT NULL," +
			" FOREIGN KEY(c_index_id) REFERENCES "+getDBPrefix()+"t_indexes(c_id) ON DELETE CASCADE," +
			" FOREIGN KEY(c_attr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id) ON DELETE CASCADE" +
			" ) ENGINE = " + ENGINE +
			"";
		st.executeUpdate(sql);
		st.close();
	}
	
	protected void makeDummy(KrnClass cls) throws SQLException, DriverException {
		if (cls != null) {
			List<KrnAttribute> attrs = db.getAttributesByClassId(cls.id, false);
			if (attrs.size() == 0) {
				Statement st = conn.createStatement();
				st.executeUpdate("UPDATE "+getDBPrefix()+"t_classes SET c_mod=1 WHERE c_id=" + cls.id);
				st.executeUpdate("DROP TABLE "+getClassTableName(cls.id));
				st.close();
			}
		}
	}

    public KrnClass getClassByNameComp(String name) throws SQLException {
		KrnClass cls = null;
		PreparedStatement pst = conn.prepareStatement("SELECT * FROM "+getDBPrefix()+"t_classes WHERE c_name=?");
		pst.setString(1, Funcs.normalizeInput(name));
		ResultSet set = pst.executeQuery();
		if (set.next()) {
			long id = set.getLong("c_id");
			long parentId = set.getLong("c_parent_id");
			boolean isRepl = set.getBoolean("c_is_repl");
			String uid = getSanitizedString(set, "c_cuid");
			int mod = set.getInt("c_mod");
			String tname = null;
            if (isVersion(TnameVersionBD)) {
            	tname = getSanitizedString(set, "c_tname");
            }
            byte[] beforeCreateObjExpr = isVersion(OrlangTrigersVersionBD2) ? getNormBytes(set, "c_before_create_obj") : null;
            byte[] afterCreateObjExpr = isVersion(OrlangTrigersVersionBD2) ? getNormBytes(set, "c_after_create_obj") : null;
            byte[] beforeDeleteObjExpr = isVersion(OrlangTrigersVersionBD2) ? getNormBytes(set, "c_before_delete_obj") : null;
            byte[] afterDeleteObjExpr = isVersion(OrlangTrigersVersionBD2) ? getNormBytes(set, "c_after_delete_obj") : null;
            int beforeCreateObjTr = isVersion(OrlangTrigersVersionBD3) ? set.getInt("c_before_create_obj_tr") : 0;
            int afterCreateObjTr = isVersion(OrlangTrigersVersionBD3) ? set.getInt("c_after_create_obj_tr") : 0;
            int beforeDeleteObjTr = isVersion(OrlangTrigersVersionBD3) ? set.getInt("c_before_delete_obj_tr") : 0;
            int afterDeleteObjTr = isVersion(OrlangTrigersVersionBD3) ? set.getInt("c_after_delete_obj_tr") : 0;

            cls = new KrnClass(uid, id, parentId, isRepl, mod, name, tname, beforeCreateObjExpr, afterCreateObjExpr, beforeDeleteObjExpr, afterDeleteObjExpr, beforeCreateObjTr, afterDeleteObjTr, beforeDeleteObjTr, afterDeleteObjTr);
            db.addClass(cls, false);
		}
		set.close();
		pst.close();
		return cls;
	}

    protected long getAttributeIdByUID(String uid) throws SQLException {
    	long id = -1;
		uid = Funcs.sanitizeSQL(uid);

        PreparedStatement pst = conn.prepareStatement("SELECT c_id FROM "+getDBPrefix()+"t_attrs WHERE c_auid=?");
        pst.setString(1, uid);
        ResultSet set = pst.executeQuery();
        if (set.next()) {
            id = set.getLong("c_id");
        }
        set.close();
        pst.close();
        return id;
    }
    
    public String getEntityNameFromVCS(ModelChange ch) throws DriverException {
    	String name = null;
    	PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("SELECT c_name FROM "+getDBPrefix()+"t_vcs_model WHERE c_entity_id=? AND c_type=? AND c_action=?");
	        pst.setString(1, ch.entityId);
	        pst.setInt(2, ch instanceof TriggerChange ? ((TriggerChange) ch).type : ch.entityType);
	        pst.setInt(3, ch.action);
	        rs = pst.executeQuery();
	        if (rs.next()) {
	            name = rs.getString("c_name");
	        }
		} catch (SQLException e) {
			log.error(e.getMessage());
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
		}
        return name;
    }

    public KrnAttribute getAttributeByNameComp(KrnClass cls, String name)
			throws DriverException {
		try {
			name = Funcs.sanitizeSQL(name);

			KrnAttribute attr = null;
			QueryRunner qr = new QueryRunner(true);
			AttributeRsh rh = new AttributeRsh();
			List<KrnAttribute> res = qr.query(conn,
					"SELECT a.* FROM "+getDBPrefix()+"t_attrs a,"+getDBPrefix()+"t_clinks l"
							+ " WHERE l.c_child_id=?"
							+ " AND a.c_class_id=l.c_parent_id"
							+ " AND a.c_name=?",
					rh, new Object[] { cls.id, name });
			if (res.size() == 1) {
				attr = (KrnAttribute) res.get(0);
			} else if (res.size() > 1) {
				long cid = cls.id;
				do {
					for (KrnAttribute a : res) {
						if (a.classId == cid) {
							attr = a;
							break;
						}
					}
					if (attr == null) {
						cid = getClassByIdComp(cid).parentId;
					}
				} while (attr == null);
			}
			if (attr != null)
				db.addAttribute(attr, false);
			return attr;
		} catch (SQLException e) {
			throw convertException(e);
		}
	}
    protected List<KrnAttribute> getAttributesByClassIdComp(KrnClass cls, boolean inherited) throws DriverException {
    	List<KrnAttribute> res = new ArrayList<KrnAttribute>();
		try {
			KrnAttribute attr = null;
			QueryRunner qr = new QueryRunner(true);
			AttributeRsh rh = new AttributeRsh();
			List<KrnAttribute> res_ = qr.query(conn,
					"SELECT a.* FROM "+getDBPrefix()+"t_attrs a,"+getDBPrefix()+"t_clinks l"
							+ " WHERE l.c_child_id=?"
							+ " AND a.c_class_id=l.c_parent_id",
					rh, new Object[] { cls.id});
					for (KrnAttribute a : res_) {
						if (a.classId == cls.id) {
							res.add(a);
						}
					}
		    		if (inherited) {
		    			KrnClass cls_=cls;
			    			while (cls_.parentId > 0) {
			    	    		cls_ = getClassByIdComp(cls_.parentId);
			    				res_ = (List<KrnAttribute>) qr.query(conn,
								"SELECT a.* FROM "+getDBPrefix()+"t_attrs a,"+getDBPrefix()+"t_clinks l"
								+ " WHERE l.c_child_id=?"
								+ " AND a.c_class_id=l.c_parent_id",
           							rh, new Object[] { cls_.id});
							for (KrnAttribute a : res_) {
								if (a.classId == cls_.id) {
									res.add(a);
								}
							}
		    			}
		    		}
		} catch (SQLException e) {
			throw convertException(e);
		}
    	return res;
    }
	
	@Override
	public List<KrnObject> getSystemLangs() throws DriverException {
		synchronized(sysLangIds) {
			List<KrnObject> langs = sysLangIds.get(dsName);
			if (langs == null) {
				try {
					// Выбираем все системые языки (сортировка по c_obj_id)
					KrnClass langCls = getClassByNameComp("Language");
					KrnAttribute sysAttr = getAttributeByNameComp(langCls, "system?");
					if (sysAttr == null)
						sysAttr = getAttributeByNameComp(langCls, "lang?");
					ObjectRsh rsh = new ObjectRsh();
					QueryRunner qr = new QueryRunner(true);
					langs = (List<KrnObject>) qr.query(
							conn,
							"SELECT * FROM "+getClassTableNameComp(langCls.id) +
							" WHERE c_tr_id=0 AND "+getColumnName(sysAttr) + "=1 ORDER BY c_obj_id",
							rsh);
					sysLangIds.put(dsName, langs);
				} catch (SQLException e) {
					throw convertException(e);
				}
			}
			return langs;
		}
	}
	
	protected String checkRefs(KrnObject obj, long trId) throws DriverException {
		StringBuilder msg = new StringBuilder();
		List<KrnAttribute> attrs = db.getAttributesByTypeId(obj.classId, true);
		for (KrnAttribute attr : attrs) {
			if (attr.rAttrId != 0)
				continue;
			List<KrnObject> robjs = getObjectsByAttribute(
					attr.classId, attr.id, 0, CO_EQUALS, obj, trId);
			for (KrnObject robj : robjs) {
				SortedSet<Value> vs = getValues(new long[] {robj.id}, null, attr.id, 0, trId);
				
				if (vs != null && vs.size() > 0) {
					for (Value v : vs) {
						if (v.value instanceof KrnObject) {
							if (((KrnObject)v.value).id == obj.id) {
								KrnClass cls = db.getClassById(robj.classId);
								msg.append(cls.name);
								msg.append("[id=").append(robj.id);
								msg.append("].").append(attr.name).append('\n');
							}
						}
					}
				}
				
			}
		}
		if (msg.length() > 0)
			return msg.toString();
		return null;
	}
	
	public void refreshObjectCreating(KrnObject obj) throws SQLException {
		// Создаем строку журнала
		PreparedStatement pstLogDatachanges = conn.prepareStatement(
				"INSERT INTO "+getDBPrefix()+"t_changes (c_object_id,c_class_id,c_attr_id,c_lang_id,c_tr_id,c_is_repl) "
						+ " VALUES (?,?,?,?,?,?)");
		pstLogDatachanges.setLong(1, obj.id);
		pstLogDatachanges.setLong(2, obj.classId);
		pstLogDatachanges.setLong(3, 1);
		pstLogDatachanges.setLong(4, 0);
		pstLogDatachanges.setLong(5, 0);
		pstLogDatachanges.setBoolean(6, true);
		pstLogDatachanges.executeUpdate();
		pstLogDatachanges.close();
	}

	public List<Object[]> getObjects (
			long classId,
			long[] objIds,
			AttrRequest req,
            long tid,
            int[] limit,
            int extraColumnCount,
            String info,
            Session session
	) throws DriverException {
		return getObjects(classId, objIds, req, tid, limit, extraColumnCount, info, new AttrRequestCache(),session);
	}

	public List<Object[]> getObjects (
			long classId,
			long[] objIds,
			AttrRequest req,
            long tid,
            int[] limit,
            int extraColumnCount,
            String info,
            AttrRequestCache cache,
            Session session
	) throws DriverException {
		boolean cached = cache.parts.size() > 0;
		List<AttrRequest> reqs = cached ? null : split(req, MAX_JOINS - 10);
		AttrRequest partReq = cached ? null : reqs.get(0);
		AttrRequestCache partCache = cached ? cache.parts.get(0) : cache.createPart();
		int partCount = cached ? cache.parts.size() : reqs.size();
		List<Object[]> res = getObjects2(classId, objIds, partReq, tid, limit, extraColumnCount, info, partCache,session);
		int resSz = res.size();
		if (partCount > 1 && resSz > 0) {
			int rowSz = res.get(0).length;
			for (int i = 1; i < partCount; i++) {
				partReq = cached ? null : reqs.get(i);
				partCache = cached ? cache.parts.get(i) : cache.createPart();
				List<Object[]> res2 = getObjects2(classId, objIds, partReq, tid, limit, extraColumnCount, info, partCache,session);
				int rowSz2 = res2.get(0).length - 2;
				int newRowSz = rowSz + rowSz2 - extraColumnCount;
				for (int j = 0; j < resSz; j++) {
					Object[] row = res.get(j);
					Object[] row2 = res2.get(j);
					Object[] newRow = new Object[newRowSz];
					System.arraycopy(row, 0, newRow, 0, rowSz - extraColumnCount); 			// последние extraColumnCount убираем из предыдущей
					System.arraycopy(row2, 2, newRow, rowSz - extraColumnCount, rowSz2); 	// и первые 2 из текущей
					res.set(j, newRow);
				}
				rowSz = newRowSz;
			}
		}
		return res;
	}

	public List<Object[]> getObjects2 (
			long classId,
			long[] objIds,
			AttrRequest req,
            long tid,
            int[] limit,
            int extraColumnCount,
            String info,
            AttrRequestCache cache,
            Session session
	) throws DriverException {
        List<Object[]> res = new ArrayList<Object[]>();
		int lim = limit[0];
		if (objIds == null && lim == 0 && defaultLimit > 0)
			lim = defaultLimit;

		if (cache.sql == null) {
			// Атрибуты, участвующие в запросе
			List<Pair<KrnAttribute, Integer>> attrs =
					new ArrayList<Pair<KrnAttribute, Integer>>();
			
			StringBuilder selectSql = new StringBuilder();
			StringBuilder fromSql = new StringBuilder();
			StringBuilder whereSql = new StringBuilder();
			int[] tindex = {1};
			int[] cindex = {4};
			Map<String, String> aliases = new HashMap<String, String>();
			List<String> trAliases = new ArrayList<String>();
			int vtcnt = processAttrRequest(objIds == null ? classId : 0, req, tid, getClassTableName(99), -1, attrs, selectSql, fromSql, whereSql, tindex, cindex, aliases, trAliases);
			
			StringBuilder sql = new StringBuilder();
			if (selectSql.length() > 0) {
				if (trAliases.size() > 0 && objIds == null) {
					sql.append("select *");
					sql.append(" FROM (");
					sql.append("SELECT ").append(selectSql);
					for (String tra : trAliases) {
						sql.append(",").append(tra).append(".c_tr_id AS ").append(tra).append("_tid");
					}
					
					sql.append(" FROM ").append(fromSql);
					if (whereSql.length() > 0)
						sql.append(" WHERE ").append(whereSql);
	
					sql.append(" ORDER BY t1.c_obj_id");
					for (String tra : trAliases) {
						sql.append(",").append(tra).append("_tid DESC");
					}
	
					if (lim > 0)
			        	sql = addLimit(sql, lim, 0);
	
					sql.append(") t GROUP BY t.t1_id");
				} else {
					sql.append("SELECT ").append(selectSql).append(" FROM ").append(fromSql);
					if (whereSql.length() > 0)
						sql.append(" WHERE ").append(whereSql);
			        if (lim > 0)
			        	sql = addLimit(sql, lim, 0);
				}
			}
			cache.sql = sql.toString();
			cache.joinTableCount = vtcnt / 1000;
			cache.whereTableCount = vtcnt % 1000;
			cache.attrs = attrs;
		}
		
		PreparedStatement pst = null;
        try {
        	int objIdPosition = 1;
        	if (cache.sql.length() > 0) {
		        pst = conn.prepareStatement(cache.sql);
		        if (tid > 0) {
			        for (int i = 0; i < cache.joinTableCount; i++) {
		        		pst.setLong(i + 1, tid);
		        		objIdPosition++;
			        }

			        for (int i = 0; i < cache.whereTableCount; i++) {
			        	if (objIds == null)
			        		pst.setLong(i + objIdPosition, tid);
			        	else
			        		pst.setLong(i + objIdPosition + 1, tid);
			        }
		        }
        	}
        	
	        Calendar cal = Calendar.getInstance();
	        if (objIds == null) {
	        	ResultSet rs = pst.executeQuery();
	        	while (rs.next())
	        		res.add(getObjectValue(rs, cache.attrs, null, tid, cal, extraColumnCount));
	        	rs.close();
	        } else {
	        	if (cache.sql.length() > 0) {
			        for (long objId : objIds) {
			            pst.setLong(objIdPosition, objId);
			            ResultSet rs = pst.executeQuery();
			            if (rs.last())
			            	res.add(getObjectValue(rs, cache.attrs, null, tid, cal, extraColumnCount));
			            else {
				        	Object[] val = new Object[cache.attrs.size() + 2];
				        	val[0] = new KrnObject(objId, null, 0);
				        	val[1] = 0;
			            	res.add(val);
			            }
			            rs.close();
			        }
	        	} else {
			        for (long objId : objIds) {
			        	Object[] val = new Object[cache.attrs.size() + 2];
			        	val[0] = new KrnObject(objId, null, 0);
			        	val[1] = tid;
		            	res.add(val);
			        }
	        	}
	        	
	        	// Обработка множественных атрибутов
				int sz = cache.attrs.size();
		    	for (int i = 0; i < sz; i++) {
		    		Pair<KrnAttribute, Integer> p = cache.attrs.get(i);
	        		KrnAttribute attr = p.first;
	        		if (attr.collectionType != COLLECTION_NONE) {
	        			int parentIndex = p.second;
	        			long[] parIds = null;
	        			if (parentIndex > -1) {
		        			List<Long> parentIds = new ArrayList<Long>();
		        			for (Object[] val : res) {
		        				KrnObject parentObj = (KrnObject)val[parentIndex + 2];
		        				if (parentObj != null)
		        					parentIds.add(parentObj.id);
		        			}
		        			
		        			parIds = Funcs.makeLongArray(parentIds);
	        			} else
	        				parIds = objIds;
	        			
	    				MMap<Long, Value, List<Value>> chvals = 
	    						new MMap<Long, Value, List<Value>>(((Class<List<Value>>) ((List<Value>)new ArrayList<Value>()).getClass()));

	    				SortedSet<Value> vals = getValues(parIds, null, attr.id, 0, tid);
			        	for (Value val : vals)
			        		chvals.put(val.objectId, val);

			        	for (Object[] val : res) {
		        			KrnObject parentObj = (parentIndex > -1) ? (KrnObject)val[parentIndex + 2] : (KrnObject)val[0];
	        				if (parentObj != null) {
	        					List<Value> arr = chvals.get(parentObj.id);
	        					val[i+2] = arr != null ? arr : new ArrayList<Value>();
	        				}
	        			}
	        		}
	        	}
	        }
	        
			if (objIds == null && limit[0] == 0 && res.size() >= lim)
				log.warn("GET_ALL_OBJECTS INFO:(" + info + ") SQL:" + cache.sql);
			if(isLoggingGetObjSql() && session!=null && !session.isContextEmpty()){
				Context ctx=session.getContext();
				ctx.objIds=Arrays.copyOf(objIds, objIds.length);
				log_sql.info("GET_OBJ_SQL_INFO:(" + info + ") SQL:" + cache.sql);
				log_sql.info("SessionContext:" + (session!=null && !session.isContextEmpty()?session.getContext():""));
				
			}
	        return res;
        } catch (SQLException e) {
        	log.error("ERROR INFO:(" + info + ") SQL:" + cache.sql);
            throw convertException(e);
        } finally {
    		DbUtils.closeQuietly(pst);
        }
	}
	
	protected List<AttrRequest> split(AttrRequest req, int maxJoins) {
		List<AttrRequest> res = new ArrayList<AttrRequest>();
		int count = 0;
		int beg = 0;
		List<AttrRequest> chReqs = req.getChildren();
		
		int chSize = chReqs.size();
		
		if (chSize < Constants.MAX_ELEMENTS_COUNT_2) {
			
			for(int i = chSize - 1; i >= 0 && i < Constants.MAX_ELEMENTS_COUNT_2; i--) {
				AttrRequest chReq = chReqs.get(i);
				int count2 = countJoins(chReq);
				if (count2 > maxJoins) {
					List<AttrRequest> chReqs2 = split(chReq, maxJoins);
					req.remove(chReq);
				}
			}
			chReqs = req.getChildren();
			chSize = chReqs.size();
			
			if (chSize < Constants.MAX_ELEMENTS_COUNT_2) {
				for(int i = 0; i < chSize && i < Constants.MAX_ELEMENTS_COUNT_2; i++) {
					AttrRequest chReq = chReqs.get(i);
					int count2 = countJoins(chReq);
					count += count2;
					if (count > maxJoins-10) {
						AttrRequest newReq = new AttrRequest(req.getParent());
						newReq.attrId = req.attrId;
						newReq.langId = req.langId;
		
						for (int j = beg; j < i && j < Constants.MAX_ELEMENTS_COUNT_2; j++)
							newReq.add(chReqs.get(j));
						res.add(newReq);
						beg = i;
						count = count2;
					}
				}
			}
		}
		
		if (beg == 0) {
			res.add(req);
		} else {
			AttrRequest newReq = new AttrRequest(req.getParent());
			newReq.attrId = req.attrId;
			newReq.langId = req.langId;
			
			for (int j = beg; j < chSize && j < Constants.MAX_ELEMENTS_COUNT_2; j++)
				newReq.add(chReqs.get(j));

			res.add(newReq);
		}
		return res;
	}
	
	protected int countJoins(AttrRequest req) {
		int res = req.attrId > 0 && db.getAttributeById(req.attrId).typeClassId >= 99 ? 1 : 0;
		for(AttrRequest chReq : req.getChildren())
			res += countJoins(chReq);
		return res;
	}
	
	protected String getAlias(AttrRequest req) {
		StringBuilder res = new StringBuilder("vt_");
		AttrRequest r = req;
		while (r != null && r.attrId != 0) {
			res.append(r.attrId);
			r = r.getParent();
		}
		return res.toString();
	}
	
	protected int processAttrRequest(
			long classId,
			AttrRequest req,
			long tid,
			String parentAlias,
			int parentIndex,
			List<Pair<KrnAttribute, Integer>> attrs,
			StringBuilder selectSql,
			StringBuilder fromSql,
			StringBuilder whereSql,
			int[] tindex,
			int[] cindex,
			Map<String, String> aliases,
			List<String> trAliases
	) throws DriverException {
		int res = 0;
		// Индекс родительского атрибута в массиве (нужен для множественных атрибутов) 
		if (req.attrId == 0) { // Корневой запрос
			// Имя основной таблицы
			String ptname = null;
			String ptalias = null;
			String pmalias = null;
			Set<Long> addedClsIds = new HashSet<Long>();
			if (classId != 0) {
				// Выбираются все объекты класса
				ptname = getClassTableName(classId);
				ptalias = getShortAlias(new StringBuilder(ptname).append("_").append(classId).toString(), aliases, tindex);
				pmalias = getShortAlias(ptname + "m", aliases, tindex);
				selectSql.append(ptalias).append(".c_obj_id AS ").append(ptalias).append("_id,");
				selectSql.append(ptalias).append(".c_uid AS ").append(ptalias).append("_uid,");
				selectSql.append(ptalias).append(".c_class_id AS ").append(ptalias).append("_cid,");
				selectSql.append(ptalias).append(".c_tr_id AS ").append(ptalias).append("_tid");
	        	if (tid > 0) {
	        		fromSql.append(
	        				"(SELECT c_obj_id,MAX(c_tr_id) AS c_tr_id FROM ").append(ptname).append(" WHERE c_tr_id IN (0,?) GROUP BY c_obj_id) ").append(pmalias)
	        				.append(" INNER JOIN ").append(ptname).append(" ").append(ptalias).append(" ON ").append(ptalias).append(".c_obj_id=").append(pmalias)
	        				.append(".c_obj_id AND ").append(ptalias).append(".c_tr_id=").append(pmalias).append(".c_tr_id AND ").append(ptalias).append(".c_is_del=0");
	        		res+=1000;
	        	} else if (tid == -1) {
	        		fromSql.append(
	        				"(SELECT c_obj_id,MAX(c_tr_id) AS c_tr_id FROM ").append(ptname).append(" GROUP BY c_obj_id) ").append(pmalias)
	        				.append(" INNER JOIN ").append(ptname).append(" ").append(ptalias).append(" ON ").append(ptalias).append(".c_obj_id=").append(pmalias)
	        				.append(".c_obj_id AND ").append(ptalias).append(".c_tr_id=").append(pmalias).append(".c_tr_id AND ").append(ptalias).append(".c_is_del=0");
	        	} else if (tid == -2) {
	        		fromSql.append(
	        				"(SELECT c_obj_id,MAX(c_tr_id) AS c_tr_id FROM ").append(ptname).append(" WHERE c_tr_id>0 GROUP BY c_obj_id) ").append(pmalias)
	        				.append(" INNER JOIN ").append(ptname).append(" ").append(ptalias).append(" ON ").append(ptalias).append(".c_obj_id=").append(pmalias)
	        				.append(".c_obj_id AND ").append(ptalias).append(".c_tr_id=").append(pmalias).append(".c_tr_id AND ").append(ptalias).append(".c_is_del=0");
	        	} else {
	        		fromSql.append(ptname).append(" ").append(ptalias);
					whereSql.append(ptalias).append(".c_tr_id=0");
	        	}
				addedClsIds.add(classId);
			}
			int pidx = attrs.size() - 1;
			for(AttrRequest child : req.getChildren()) {
				KrnAttribute ca = db.getAttributeById(child.attrId);
				if (ca.collectionType != COLLECTION_NONE) {
					attrs.add(new Pair<KrnAttribute, Integer>(ca, parentIndex));
					continue;
				}
				String tname = getClassTableName(ca.classId);
				String talias = getShortAlias(new StringBuilder(tname).append("_").append(ca.classId).toString(), aliases, tindex);
				if (ptname == null) {
					// Выбираются отдельные объекты.
					// Основная таблица еще не задана, берем таблицу класса
					// первого атрибута
					ptname = tname;
					ptalias = talias;
					pmalias = getShortAlias(ptname + "m", aliases, tindex);
					selectSql.append(ptalias).append(".c_obj_id AS ").append(ptalias).append("_id,");
					selectSql.append(ptalias).append(".c_uid AS ").append(ptalias).append("_uid,");
					selectSql.append(ptalias).append(".c_class_id AS ").append(ptalias).append("_cid,");
					selectSql.append(ptalias).append(".c_tr_id AS ").append(ptalias).append("_tid");
		        	if (tid > 0) {
		        		fromSql.append(
		        				"(SELECT c_obj_id,MAX(c_tr_id) AS c_tr_id FROM ").append(ptname).append(" WHERE c_obj_id=? AND c_tr_id IN (0,?) GROUP BY c_obj_id) ").append(pmalias)
		        				.append(" INNER JOIN ").append(ptname).append(" ").append(ptalias).append(" ON ").append(ptalias).append(".c_obj_id=").append(pmalias)
		        				.append(".c_obj_id AND ").append(ptalias).append(".c_tr_id=").append(pmalias).append(".c_tr_id AND ").append(ptalias).append(".c_is_del=0");
		        		res++;
		        	} else if (tid == -1) {
		        		fromSql.append(
		        				"(SELECT c_obj_id,MAX(c_tr_id) AS c_tr_id FROM ").append(ptname).append(" WHERE c_obj_id=? GROUP BY c_obj_id) ").append(pmalias)
		        				.append(" INNER JOIN ").append(ptname).append(" ").append(ptalias).append(" ON ").append(ptalias).append(".c_obj_id=").append(pmalias)
		        				.append(".c_obj_id AND ").append(ptalias).append(".c_tr_id=").append(pmalias).append(".c_tr_id AND ").append(ptalias).append(".c_is_del=0");
		        	} else if (tid == -2) {
		        		fromSql.append(
		        				"(SELECT c_obj_id,MAX(c_tr_id) AS c_tr_id FROM ").append(ptname).append(" WHERE c_obj_id=? AND c_tr_id>0 GROUP BY c_obj_id) ").append(pmalias)
		        				.append(" INNER JOIN ").append(ptname).append(" ").append(ptalias).append(" ON ").append(ptalias).append(".c_obj_id=").append(pmalias)
		        				.append(".c_obj_id AND ").append(ptalias).append(".c_tr_id=").append(pmalias).append(".c_tr_id AND ").append(ptalias).append(".c_is_del=0");
		        	} else {
		        		fromSql.append(ptname).append(" ").append(ptalias);
						whereSql.append(ptalias).append(".c_obj_id=? AND ").append(ptalias).append(".c_tr_id=0");
		        	}
					addedClsIds.add(ca.classId);
				}
				// Добавляем таблицу в FROM если она уже не добавлена
				if (!addedClsIds.contains(ca.classId)) {
					addedClsIds.add(ca.classId);
					fromSql.append(" LEFT JOIN ").append(tname).append(" ").append(talias).append(" ON ").append(talias).append(".c_obj_id=").append(ptalias)
							.append(".c_obj_id AND ").append(talias).append(".c_tr_id=").append(ptalias).append(".c_tr_id");
				}
				res += processAttrRequest(classId, child, tid, tname, pidx, attrs, selectSql, fromSql, whereSql, tindex, cindex, aliases, trAliases);
			}
		} else {
			KrnAttribute a = db.getAttributeById(req.attrId);
			if (a.collectionType != COLLECTION_NONE) {
				attrs.add(new Pair<KrnAttribute, Integer>(a, parentIndex));
				return res;
			}

			KrnAttribute ra = null;
			String cname = getColumnName(a, req.langId);
			if (a.rAttrId > 0) {
				ra = db.getAttributeById(a.rAttrId);
				cname = getColumnName(ra);
			}
			
			String ptalias = getShortAlias(new StringBuilder(parentAlias).append("_").append(a.classId).toString(), aliases, tindex);
			if (a.typeClassId < 99) {
				selectSql.append(",").append(ptalias).append(".").append(cname).append(" AS ").append(ptalias).append("_").append(cname);
				attrs.add(new Pair<KrnAttribute, Integer>(a, cindex[0]++));
			} else {
				String contains = "";
				for (Pair<KrnAttribute, Integer> pair : attrs) {
					if (pair.first.id == a.id) {
						contains = String.valueOf(tindex[0]++);
						break;
					}
				}
				String vt = new StringBuilder(parentAlias).append(contains).append("_").append(a.id).append("_").append(a.rAttrId > 0 ? ra.classId : a.typeClassId).toString();
				String vtalias = getShortAlias(vt, aliases, tindex);
				if (req.getChildren().size() == 0 && a.rAttrId == 0) {
					// Если атрбиут объектного типа, то добавляем таблицу для значения
					String vtname = getClassTableName(ROOT_CLASS_ID);
					selectSql.append(",").append(vtalias).append(".c_obj_id AS ").append(vtalias).append("_id");
					selectSql.append(",").append(vtalias).append(".c_uid AS ").append(vtalias).append("_uid");
					selectSql.append(",").append(vtalias).append(".c_class_id AS ").append(vtalias).append("_cid");
					attrs.add(new Pair<KrnAttribute, Integer>(a, cindex[0]));
					cindex[0] += 3;
	        		fromSql.append(
		        				" LEFT JOIN ").append(vtname).append(" ").append(vtalias).append(" ON ").append(vtalias).append(".c_obj_id=").append(ptalias).append(".").append(cname);
				} else {
					// Если атрбиут объектного типа, то добавляем таблицу для значения
					String mtalias = getShortAlias(vtalias + "m", aliases, tindex);
					String vtname = getClassTableName(a.rAttrId > 0 ? ra.classId : a.typeClassId);
					selectSql.append(",").append(vtalias).append(".c_obj_id AS ").append(vtalias).append("_id");
					selectSql.append(",").append(vtalias).append(".c_uid AS ").append(vtalias).append("_uid");
					selectSql.append(",").append(vtalias).append(".c_class_id AS ").append(vtalias).append("_cid");
					attrs.add(new Pair<KrnAttribute, Integer>(a, cindex[0]));
					cindex[0] += 3;
					
		        	// Добавляем таблицы для атибутов родительских классов
					Set<Long> addedClsIds = new HashSet<Long>();

		        	if (tid > 0) {
		        		if (ra != null && ra.collectionType != COLLECTION_NONE) {
		    				String rt = new StringBuilder(parentAlias).append(contains).append("_").append(a.id).append("_").append(a.typeClassId).append("_").append(a.rAttrId).toString();
		    				String rtalias = getShortAlias(rt, aliases, tindex);
							String rtname = getAttrTableName(ra);

		        			fromSql.append(
			        				" LEFT JOIN ").append(rtname).append(" ").append(rtalias).append(" ON ").append(ptalias).append(".c_obj_id=")
			        				.append(rtalias).append(".").append(cname).append(" AND ").append(rtalias).append(".c_del=0 AND ").append(rtalias)
			        				.append(".c_tr_id=").append(" (SELECT MAX(c_tr_id) FROM ").append(rtname).append(" WHERE c_tr_id IN (0,?) AND ").append(cname).append("=").append(rtalias).append(".").append(cname).append(")");
		        			res++;
		        			fromSql.append(
			        				" LEFT JOIN ").append(vtname).append(" ").append(vtalias).append(" ON ").append(vtalias).append(".c_obj_id=")
			        				.append(rtalias).append(".c_obj_id AND ").append(vtalias).append(".c_is_del=0 AND ").append(vtalias)
			        				.append(".c_tr_id=").append(" (SELECT MAX(c_tr_id) FROM ").append(vtname).append(" WHERE c_tr_id IN (0,?) AND c_obj_id=").append(vtalias).append(".c_obj_id)");
		        			res++;
		        		} else if (ra != null && a.typeClassId != ra.classId) {
		        			KrnClass c = db.getClassById(a.typeClassId);
		        			
	    					fromSql.append(
			        				" LEFT JOIN ").append(vtname).append(" ").append(vtalias).append(" ON ").append(a.rAttrId > 0 ? ptalias : vtalias).append(".c_obj_id=")
			        				.append(a.rAttrId > 0 ? vtalias : ptalias).append(".").append(cname).append(" AND ").append(vtalias).append(".c_is_del=0 AND ").append(vtalias)
			        				.append(".c_tr_id=").append(" (SELECT MAX(c_tr_id) FROM ").append(vtname).append(" WHERE c_tr_id IN (0,?) AND c_obj_id=").append(vtalias).append(".c_obj_id)");
		        			res++;

	    					if (whereSql.length() > 0) whereSql.append(" AND ");
	    					whereSql.append(vtalias).append(".c_class_id=").append(a.typeClassId);

	    					while (c.id != ra.classId) {
	    						addedClsIds.add(c.id);

	    						String rptname = getClassTableName(c.id);
	    						String rptalias = getShortAlias(new StringBuilder(parentAlias).append(contains).append("_").append(a.id).append("_").append(c.id).toString(), aliases, tindex);
	    						
		    					fromSql.append(" LEFT JOIN ").append(rptname).append(" ").append(rptalias).append(" ON ").append(rptalias).append(".c_obj_id=").append(vtalias)
		    							.append(".c_obj_id AND ").append(rptalias).append(".c_tr_id=").append(vtalias).append(".c_tr_id");
		    					
		    					c = db.getClassById(c.parentId);
		        			}
		        		} else {
			        		fromSql.append(
			        				" LEFT JOIN ").append(vtname).append(" ").append(vtalias).append(" ON ").append(a.rAttrId > 0 ? ptalias : vtalias).append(".c_obj_id=")
			        				.append(a.rAttrId > 0 ? vtalias : ptalias).append(".").append(cname).append(" AND ").append(vtalias).append(".c_is_del=0 AND ").append(vtalias)
			        				.append(".c_tr_id=").append(" (SELECT MAX(c_tr_id) FROM ").append(vtname).append(" WHERE c_tr_id IN (0,?) AND c_obj_id=").append(vtalias).append(".c_obj_id)");
		        			res++;
		        		}
		        	} else if (tid == -1) {
/*		        		fromSql.append(" LEFT JOIN (SELECT c_obj_id,");
		        		
		        		if (a.rAttrId > 0) fromSql.append(cname).append(",");
		        		
		        		fromSql.append(
		        				"MAX(c_tr_id) AS c_tr_id FROM ").append(vtname).append(" GROUP BY c_obj_id) ").append(mtalias)
		        				.append(" ON ").append(a.rAttrId > 0 ? ptalias : mtalias).append(".c_obj_id=").append(a.rAttrId > 0 ? mtalias : ptalias).append(".").append(cname)
		        				.append(" LEFT JOIN ").append(vtname).append(" ").append(vtalias).append(" ON ").append(vtalias).append(".c_obj_id=").append(mtalias)
		        				.append(".c_obj_id AND ").append(vtalias).append(".c_tr_id=").append(mtalias).append(".c_tr_id AND ").append(vtalias).append(".c_is_del=0");
*/		        		
		        		// Попробуем брать транзакцию как и в первой таблице
		        		fromSql.append(" LEFT JOIN ").append(vtname).append(" ").append(vtalias).append(" ON ")
		        				.append(a.rAttrId > 0 ? ptalias : vtalias).append(".c_obj_id=").append(a.rAttrId > 0 ? vtalias : ptalias).append(".").append(cname)
		        				.append(" AND (").append(vtalias).append(".c_tr_id=").append(ptalias).append(".c_tr_id OR ")
		        				.append(vtalias).append(".c_tr_id=0) AND ").append(vtalias).append(".c_is_del=0");
		        		
		        		trAliases.add(vtalias);

		        		if (ra != null && a.typeClassId != ra.classId) {
		        			KrnClass c = db.getClassById(a.typeClassId);
		        			
	    					if (whereSql.length() > 0) whereSql.append(" AND ");
	    					whereSql.append(vtalias).append(".c_class_id=").append(a.typeClassId);
	
	    					while (c.id != ra.classId) {
	    						addedClsIds.add(c.id);
	
	    						String rptname = getClassTableName(c.id);
	    						String rptalias = getShortAlias(new StringBuilder(parentAlias).append(contains).append("_").append(a.id).append("_").append(c.id).toString(), aliases, tindex);
	    						
		    					fromSql.append(" LEFT JOIN ").append(rptname).append(" ").append(rptalias).append(" ON ").append(rptalias).append(".c_obj_id=").append(vtalias)
    									.append(".c_obj_id AND ").append(rptalias).append(".c_tr_id=").append(vtalias).append(".c_tr_id");

		    					c = db.getClassById(c.parentId);
		        			}
		        		}

		        	} else if (tid == -2) {
		        		fromSql.append(" LEFT JOIN (SELECT c_obj_id,");
		        		
		        		if (a.rAttrId > 0) fromSql.append(cname).append(",");

		        		fromSql.append(
		        				"MAX(c_tr_id) AS c_tr_id FROM ").append(vtname).append(" WHERE c_tr_id>0 GROUP BY c_obj_id) ").append(mtalias)
		        				.append(" ON ").append(a.rAttrId > 0 ? ptalias : mtalias).append(".c_obj_id=").append(a.rAttrId > 0 ? mtalias : ptalias).append(".").append(cname)
		        				.append(" LEFT JOIN ").append(vtname).append(" ").append(vtalias).append(" ON ").append(vtalias).append(".c_obj_id=").append(mtalias)
		        				.append(".c_obj_id AND ").append(vtalias).append(".c_tr_id=").append(mtalias).append(".c_tr_id AND ").append(vtalias).append(".c_is_del=0");
		        		
		        		if (ra != null && a.typeClassId != ra.classId) {
		        			KrnClass c = db.getClassById(a.typeClassId);
		        			
	    					if (whereSql.length() > 0) whereSql.append(" AND ");
	    					whereSql.append(vtalias).append(".c_class_id=").append(a.typeClassId);
	
	    					while (c.id != ra.classId) {
	    						addedClsIds.add(c.id);
	
	    						String rptname = getClassTableName(c.id);
	    						String rptalias = getShortAlias(new StringBuilder(parentAlias).append(contains).append("_").append(a.id).append("_").append(c.id).toString(), aliases, tindex);
	    						
		    					fromSql.append(" LEFT JOIN ").append(rptname).append(" ").append(rptalias).append(" ON ").append(rptalias).append(".c_obj_id=").append(vtalias)
    									.append(".c_obj_id AND ").append(rptalias).append(".c_tr_id=").append(vtalias).append(".c_tr_id");

		    					c = db.getClassById(c.parentId);
		        			}
		        		}

		        	} else {
		        		if (ra != null && ra.collectionType != COLLECTION_NONE) {
		    				String rt = new StringBuilder(parentAlias).append(contains).append("_").append(a.id).append("_").append(a.typeClassId).append("_").append(a.rAttrId).toString();
		    				String rtalias = getShortAlias(rt, aliases, tindex);
							String rtname = getAttrTableName(ra);

		        			fromSql.append(
			        				" LEFT JOIN ").append(rtname).append(" ").append(rtalias).append(" ON ").append(ptalias).append(".c_obj_id=")
			        				.append(rtalias).append(".").append(cname).append(" AND ").append(rtalias).append(".c_del=0 AND ").append(rtalias)
			        				.append(".c_tr_id=0");
		        			res++;
		        			fromSql.append(
			        				" LEFT JOIN ").append(vtname).append(" ").append(vtalias).append(" ON ").append(vtalias).append(".c_obj_id=")
			        				.append(rtalias).append(".c_obj_id AND ").append(vtalias).append(".c_is_del=0 AND ").append(vtalias)
			        				.append(".c_tr_id=0");
		        		} else if (ra != null && a.typeClassId != ra.classId) {
		        			KrnClass c = db.getClassById(a.typeClassId);
		        			
			        		fromSql.append(
			        				" LEFT JOIN ").append(vtname).append(" ").append(vtalias).append(" ON ").append(a.rAttrId > 0 ? ptalias : vtalias).append(".c_obj_id=").append(a.rAttrId > 0 ? vtalias : ptalias)
			        				.append(".").append(cname).append(" AND ").append(vtalias).append(".c_tr_id=0 AND ").append(vtalias).append(".c_is_del=0");

	    					if (whereSql.length() > 0) whereSql.append(" AND ");
	    					whereSql.append(vtalias).append(".c_class_id=").append(a.typeClassId);

	    					while (c.id != ra.classId) {
	    						addedClsIds.add(c.id);

	    						String rptname = getClassTableName(c.id);
	    						String rptalias = getShortAlias(new StringBuilder(parentAlias).append(contains).append("_").append(a.id).append("_").append(c.id).toString(), aliases, tindex);
	    						
		    					fromSql.append(" LEFT JOIN ").append(rptname).append(" ").append(rptalias).append(" ON ").append(rptalias).append(".c_obj_id=").append(vtalias)
		    							.append(".c_obj_id AND ").append(rptalias).append(".c_tr_id=0");
		    					
		    					c = db.getClassById(c.parentId);
		        			}
		        		} else {
			        		fromSql.append(
			        				" LEFT JOIN ").append(vtname).append(" ").append(vtalias).append(" ON ").append(a.rAttrId > 0 ? ptalias : vtalias).append(".c_obj_id=").append(a.rAttrId > 0 ? vtalias : ptalias)
			        				.append(".").append(cname).append(" AND ").append(vtalias).append(".c_tr_id=0 AND ").append(vtalias).append(".c_is_del=0");
		        		}
		        	}
					addedClsIds.add(a.typeClassId);
					
					int pidx = attrs.size() - 1;
					String parent = new StringBuilder(parentAlias).append(contains).append("_").append(a.id).toString();
					for (AttrRequest child : req.getChildren()) {
						KrnAttribute ca = db.getAttributeById(child.attrId);
						// Добавляем таблицу в FROM если она уже не добавлена
						if (!addedClsIds.contains(ca.classId)) {
							addedClsIds.add(ca.classId);
							String tname = getClassTableName(ca.classId);
							String vt1 = new StringBuilder(parentAlias).append(contains).append("_").append(a.id).append("_").append(ca.classId).toString();
							String alias = getShortAlias(vt1, aliases, tindex);
							fromSql.append(" LEFT JOIN ").append(tname).append(" ").append(alias).append(" ON ").append(alias).append(".c_obj_id=")
									.append(vtalias).append(".c_obj_id AND ").append(alias).append(".c_tr_id=").append(vtalias).append(".c_tr_id");
						}
						res += processAttrRequest(classId, child, tid, parent, pidx, attrs, selectSql, fromSql, whereSql, tindex, cindex, aliases, trAliases);
					}
				}
			}
		}
		return res;
	}
	
	protected String getShortAlias(String alias, Map<String, String> aliases, int[] tindex) {
		String res = aliases.get(alias);
		if (res == null) {
			res = "t" + tindex[0]++;
			aliases.put(alias, res);
		}
		return res;
	}
	
	protected Object[] getObjectValue(
			ResultSet rs,
			List<Pair<KrnAttribute, Integer>> attrs, // атрибут, индекс колонки
			MMap<String, Value, List<Value>> cvals,  // множественные атрибуты
			long trId,
			Calendar cal,
			int extraColumnCount
	) throws SQLException, DriverException {
        long objectId = rs.getLong(1);
        String objectUid = getSanitizedString(rs, 2);
        long objectClsId = rs.getLong(3);
        long objectTrId = rs.getLong(4);
        Object[] res = new Object[attrs.size() + 2 + extraColumnCount];
    	res[0] = new KrnObject(objectId, objectUid, objectClsId);
		res[1] = objectTrId;
		int sz = attrs.size();
    	for (int i = 0; i < sz; i++) {
    		Pair<KrnAttribute, Integer> attr = attrs.get(i);
    		KrnAttribute a = attr.first;
    		int cindex = attr.second; 
    		if (a.collectionType == COLLECTION_NONE) {
	    		Object v = null;
	    		if (a.typeClassId >= 99) {
	    			long vobjId = rs.getLong(cindex + 1);
	    			if (!rs.wasNull())
	    				v = new KrnObject(
	    						vobjId,
	    						getSanitizedString(rs, cindex + 2),
	    						rs.getLong(cindex + 3));
	    		} else {
	    			v = getValue(rs, cindex + 1, a.typeClassId, cal);
                    if (a.typeClassId == PC_BLOB && db.inJcrRepository(a.id)) {
                    	v = db.getRepositoryData(a.id, (byte[])v);
                    } else if (a.typeClassId == PC_MEMO && db.inJcrRepository(a.id)) {
                    	v = db.getRepositoryData(a.id, (String)v);
                    }
	    		}
	    		if (v != null)
	        		res[i + 2] = v;
    		} else if (cvals != null) {
    			res[i + 2] = cvals.get(a.id + "_" + objectId);
    		}
    	}
    	return res;
	}
	
	public int truncate(KrnClass clazz) throws DriverException {
		try {
			// Удаляем ссылки на объекты удаляемого класса
			List<KrnAttribute> attrs = db.getAttributesByTypeId(clazz.id, true);
			for (KrnAttribute attr : attrs) {
				// Пропускаем обратные атрибуты
				if (attr.rAttrId > 0)
					continue;
				String cmName = getColumnName(attr);
				if (attr.collectionType == COLLECTION_NONE) {
					String ctName = getClassTableName(attr.classId);
					PreparedStatement pst = conn.prepareStatement(
							"UPDATE " + ctName + " SET " + cmName + "=NULL" +
							" WHERE " + cmName + " IN (SELECT c_obj_id FROM "+getClassTableName(99)+"" +
							" WHERE c_class_id=?)");
					pst.setLong(1, clazz.id);
					pst.executeUpdate();
				} else {
					String atName = getAttrTableName(attr);
					PreparedStatement pst = conn.prepareStatement(
							"DELETE FROM " + atName +
							" WHERE " + cmName + " IN (SELECT c_obj_id FROM "+getClassTableName(99)+"" +
							" WHERE c_class_id=?)");
					pst.setLong(1, clazz.id);
					pst.executeUpdate();
				}
			}
			// Удаляем записи объектов класса во всех таблицах
			int res = 0;
			List<KrnClass> clss = db.getSuperClasses(clazz.id);
			for (KrnClass cls : clss) {
				if (!cls.isVirtual()) {
					String ctName = getClassTableName(cls.id);
					PreparedStatement pst = conn.prepareStatement(
							"DELETE FROM " + ctName + " WHERE c_class_id=?");
					pst.setLong(1, clazz.id);
					res = pst.executeUpdate();
				}
			}
			return res;
		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	@Override
	public ModelChanges getModelChanges(long changeId) throws DriverException {
		try {
			if (changeId == 0) {
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(
						"SELECT MAX(c_id) FROM "+getDBPrefix()+"t_changescls");
				rs.next();
				long lastChangeId = rs.getLong(1);
				rs.close();
				st.close();
				// Возвращаем всю модель
				ModelChanges mcs = new ModelChanges(lastChangeId);
				// Классы
				List<KrnClass> clss = new ArrayList<KrnClass>();
				db.getSubClasses(99, true, clss);
				for(KrnClass cls : clss) {
					mcs.changes.add(new com.cifs.or2.kernel.ModelChange(
							ACTION_CREATE,
							ENTITY_TYPE_CLASS,
							cls,
							null,//getClassComment(cls.id),
							null));
				}
				// Прямые атрибуты и методы
				List<com.cifs.or2.kernel.ModelChange> rAttrChanges =
						new ArrayList<com.cifs.or2.kernel.ModelChange>();
				for(KrnClass cls : clss) {
					List<KrnAttribute> attrs = db.getAttributesByClassId(cls.id, false);
					for (KrnAttribute attr : attrs) {
						com.cifs.or2.kernel.ModelChange mc =
								new com.cifs.or2.kernel.ModelChange(
									ACTION_CREATE,
									ENTITY_TYPE_ATTRIBUTE,
									attr,
									null,//getAttributeComment(attr.id),
									null);
						if (attr.rAttrId == 0)
							mcs.changes.add(mc);
						else
							rAttrChanges.add(mc);
					}
					List<KrnMethod> methods = db.getMethodsByClassId(cls.id);
					for (KrnMethod m : methods)
						mcs.changes.add(new com.cifs.or2.kernel.ModelChange(
								ACTION_CREATE,
								ENTITY_TYPE_METHOD,
								m,
								null,//getMethodComment(m.uid),
								getMethodExpression(m.uid)));
				}
				// Обратные атрибуты
				mcs.changes.addAll(rAttrChanges);
				return mcs;
			} else {
				ModelChanges mcs = new ModelChanges(changeId);
				PreparedStatement pst = conn.prepareStatement(
						"SELECT * FROM "+getDBPrefix()+"t_changescls WHERE c_id>? ORDER BY c_id");
				pst.setLong(1, changeId);
				ResultSet rs = pst.executeQuery();
				while (rs.next()) {
					mcs.changeId = rs.getLong("c_id");
					int type = rs.getInt("c_type");
					int action = rs.getInt("c_action");
					String entityUid = getSanitizedString(rs, "c_entity_id");
					Object entity = null;
					String entityComment = null;
					byte[] entityData = null;
					if (ACTION_DELETE == action)
						entity = entityUid;
					else {
						if (ENTITY_TYPE_CLASS == type) {
							KrnClass cls = db.getClassByUid(entityUid);
							entity = cls;
							//entityComment = cls != null ? getClassComment(cls.id) : null;
						} else if (ENTITY_TYPE_ATTRIBUTE == type) {
							KrnAttribute attr = db.getAttributeByUid(entityUid);
							entity = attr;
							//entityComment = attr != null ? getAttributeComment(attr.id) : null;
						} else if (ENTITY_TYPE_METHOD == type) {
							KrnMethod m = db.getMethodByUid(entityUid);
							entity = m;
							//entityComment = m != null ? getMethodComment(m.uid) : null;
							entityData = m != null ? getMethodExpression(m.uid) : null;
						}
					}
					if (entity != null)
						mcs.changes.add(new com.cifs.or2.kernel.ModelChange(
								action, type, entity, entityComment, entityData));
					
				}
				rs.close();
				pst.close();
				return mcs;
			}
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			throw new DriverException(e.getMessage());
		}
	}
	
	private DataChanges updateCache(long classId, long changeId, AttrRequest req,Session session) throws SQLException, DriverException {

		DataChanges res = new DataChanges();
		
		// Получаем последний ID изменений
		PreparedStatement pst = conn.prepareStatement(
				"SELECT MAX(c_id) FROM "+getDBPrefix()+"t_changes WHERE c_class_id=?");
		pst.setLong(1, classId);
		ResultSet rs = pst.executeQuery();
		rs.next();
		res.changeId = rs.getLong(1);
		rs.close();
		pst.close();

		Map<Long, Pair<Long,Map<Long,Pair<Long, Object[]>>>> cache = db.getDataChangesCache();
		synchronized(cache) {
			Pair<Long,Map<Long,Pair<Long, Object[]>>> p = cache.get(classId);
			Map<Long,Pair<Long, Object[]>> map = p != null ? p.second : null;
			if (p == null || p.first < res.changeId) {
				Map<Long,Long> objIds = new HashMap<Long,Long>();
				if (p == null) {
					map = new HashMap<Long, Pair<Long,Object[]>>();
					p = new Pair<Long, Map<Long,Pair<Long,Object[]>>>(res.changeId, map);
					cache.put(classId, p);
					// Загружаем кэш для класса с нуля
					// Загружаем все объекты класса, т.к. не по всем объектам записи в t_changes
					pst = conn.prepareStatement(
							"SELECT c_obj_id FROM "+getClassTableName(classId) +
							" WHERE c_class_id=? AND c_tr_id=0");
					pst.setLong(1, classId);
					rs = pst.executeQuery();
					while (rs.next()) {
						objIds.put(rs.getLong(1), 1L);
					}
					rs.close();
					pst.close();
					// Загружаем объекты класса, по которым есть записи в t_changes
					pst = conn.prepareStatement(
							"SELECT c_object_id,c_id FROM "+getDBPrefix()+"t_changes" +
							" WHERE c_class_id=?");
					pst.setLong(1, classId);
					rs = pst.executeQuery();
					while (rs.next()) {
						Long objId = rs.getLong(1);
						long id = rs.getLong(2);
						Long oldId = objIds.get(objId);
						if (oldId == null || oldId < id)
							objIds.put(objId, id);
					}
					rs.close();
					pst.close();
				} else {
					rs.close();
					pst.close();
					pst = conn.prepareStatement(
							"SELECT c_object_id,c_id FROM "+getDBPrefix()+"t_changes" +
							" WHERE c_id>? AND c_id<=? AND c_class_id=?");
					pst.setLong(1, p.first);
					pst.setLong(2, res.changeId);
					pst.setLong(3, classId);
					rs = pst.executeQuery();
					while (rs.next()) {
						Long objId = rs.getLong(1);
						long id = rs.getLong(2);
						Pair<Long, Object[]> oldP = map.get(objId);
						if (oldP == null || oldP.first < id)
							objIds.put(objId, id);
					}
					rs.close();
					pst.close();
					
					if (p.first < res.changeId) {
						p = new Pair<Long, Map<Long,Pair<Long,Object[]>>>(res.changeId, map);
						cache.put(classId, p);
					}
				}
	
				if (objIds.size() > 0) {
					// Сортируем атрибуты TODO позже формировать AttrRequest самому
					AttrRequest nreq = new AttrRequest(null);
					List<AttrRequest> nchildren = new ArrayList<AttrRequest>(req.getChildren());
					Collections.sort(nchildren, new Comparator<AttrRequest>() {
						public int compare(AttrRequest a1, AttrRequest a2) {
							long res = a1.attrId - a2.attrId;
							return res > 0 ? 1 : res < 0 ? -1 : 0;
						}
					});
					for (AttrRequest nchild : nchildren)
						nreq.add(nchild);

					long[] ids = new long[objIds.size()];
					int i = 0;
					for (Long objId : objIds.keySet())
						ids[i++] = objId;
					List<Object[]> rows = getObjects(classId, ids, nreq, 0, new int[]{0}, 0, null,session);
					for (Object[] row : rows) {
						Long objId = ((KrnObject)row[0]).id;
						map.put(objId, new Pair<Long, Object[]>(objIds.get(objId), row));
					}
				}
			}
			res.rows = new ArrayList<Object[]>();
			for (Long objId : map.keySet()) {
				Pair<Long, Object[]> objRec = map.get(objId);
				if (objRec.first > changeId)
					res.rows.add(objRec.second);
			}
			if (res.changeId == 0)
				res.changeId = 1;
			return res;
		}
	}
	
	public DataChanges getDataChanges(long classId, long changeId, AttrRequest req,Session session)
			throws DriverException {
		try {
			return updateCache(classId, changeId, req,session);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DriverException(e.getMessage());
		}
	}

	public List<DataChanges> getDataChanges2(List<Object[]> changeRequests,Session session)
			throws DriverException {
		try {
			// Получаем последний ID изменений
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(
					"SELECT MAX(c_id) FROM "+getDBPrefix()+"t_changes");
			rs.next();
			long lastChangeId = rs.getLong(1);
			rs.close();
			st.close();
			
			List<DataChanges> changes = new ArrayList<DataChanges>(changeRequests.size());
			
			for (Object[] changeRequest : changeRequests) {
				long classId = ((Number)changeRequest[0]).longValue();
				long changeId = ((Number)changeRequest[1]).longValue();
				AttrRequest req = (AttrRequest)changeRequest[2];
				List<Long> objIds = new ArrayList<Long>();
				if (changeId == 0) {
					// Возвращаем все объекты этого класса
					PreparedStatement pst = conn.prepareStatement(
							"SELECT c_obj_id FROM "+getClassTableName(classId) +
							" WHERE c_class_id=? AND c_tr_id=0");
					pst.setLong(1, classId);
					rs = pst.executeQuery();
					while (rs.next()) {
						objIds.add(rs.getLong(1));
					}
					rs.close();
					pst.close();
					
				} else {
					PreparedStatement pst = conn.prepareStatement(
							"SELECT DISTINCT c_object_id FROM "+getDBPrefix()+"t_changes" +
							" WHERE c_id>? AND c_id<=? AND c_object_id IN (" +
							" SELECT c_obj_id FROM "+getClassTableName(99)+" WHERE c_class_id=?)");
					pst.setLong(1, changeId);
					pst.setLong(2, lastChangeId);
					pst.setLong(3, classId);
					rs = pst.executeQuery();
					while (rs.next()) {
						objIds.add(rs.getLong(1));
					}
					rs.close();
					pst.close();
				}
	
				long[] ids = new long[objIds.size()];
				for (int i = 0; i < ids.length; i++)
					ids[i] = objIds.get(i);

				DataChanges res = new DataChanges();
				res.changeId = lastChangeId;
				res.rows = getObjects(classId, ids, req, 0, new int[]{0}, 0, null,session);
				changes.add(res);
			}
			
			return changes;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new DriverException(e.getMessage());
		}
	}
	public boolean checkExistenceClassByName(String name) {
        KrnClass cnode;
        try {
            cnode = getClassByNameComp(name);
            return cnode != null;
        } catch (Exception e) {
            return false;
        }
    }
	
	/**
	 * Создание класса <code>ConfigGlobal</code>
	 * 
	 * @throws SQLException
	 * @throws DriverException
	 */
	public void createClassConfigGlobal() throws SQLException, DriverException {
	    // проверка наличия родительского класса
	    if (!checkExistenceClassByName("Config")) {
	        db.addClass(createClass("Config", sysCls.id, false, 159, "159", null), false);
	    }
	    if (!checkExistenceClassByName(NAME_CLASS_CONFIG_GLOBAL)) {
	        db.addClass(createClass(NAME_CLASS_CONFIG_GLOBAL, getClassByNameComp("Config").id, false, -1, "c213dbc7-9a84-4bbd-9630-6215d81a1869", null), false);
	    }
	}
	
	/**
	 * Создание класса <code>ConfigLocal</code>
	 * 
	 * @throws DriverException
	 * @throws SQLException 
	 */
	public void createClassConfigLocal() throws DriverException, SQLException {
	    if (!checkExistenceClassByName(NAME_CLASS_CONFIG_LOCAL)) {
	        db.addClass(createClass(NAME_CLASS_CONFIG_LOCAL, getClassByNameComp(NAME_CLASS_CONFIG_GLOBAL).id, false, -1, "58ffb4c4-ddc2-4a83-8b79-0ad6171a7e2c", null), false);
	    }
	
	}
	
	/**
	 * Создание класса <code>ConfigObject</code>
	 * 
	 * @throws DriverException
	 * @throws SQLException 
	 */
	public void createClassConfigObject() throws DriverException, SQLException {
	    if (!checkExistenceClassByName(NAME_CLASS_CONFIG_OBJECT)) {
	        db.addClass(createClass(NAME_CLASS_CONFIG_OBJECT, getClassByNameComp("Config").id, false, -1, "8f6bcec4-d1e3-4403-abb5-8f9a4cb8cf06", null), false);
	    }
	}
	
	/**
	 * Создание класса <code>Property</code>
	 * 
	 * @throws DriverException
	 */
	public void createClassProperty() throws DriverException {
	    if (!checkExistenceClassByName(NAME_CLASS_PROPERTY)) {
	        db.addClass(createClass(NAME_CLASS_PROPERTY, sysCls.id, false, -1, "6f651038-6b56-4883-be3e-1cfe1faef56a", null), false);
	    }
	}
	
	/**
	 * Создание класса <code>ControlFolder</code>
	 * 
	 * @throws DriverException
	 */
	public void createClassControlFolder() throws DriverException {
	    if (!checkExistenceClassByName(NAME_CLASS_CONTROL_FOLDER)) {
	        db.addClass(createClass(NAME_CLASS_CONTROL_FOLDER, sysCls.id, false, -1, "5762cecf-c74f-41cf-8484-d885d2c0e9f5", null), false);
	    }
	}
	
	/**
	 * Создание класса <code>ControlFolderRoot</code>
	 * 
	 * @throws DriverException
	 * @throws SQLException 
	 */
	public void createClassControlFolderRoot() throws DriverException, SQLException {
	    if (!checkExistenceClassByName(NAME_CLASS_CONTROL_FOLDER_ROOT)) {
	        db.addClass(createClass(NAME_CLASS_CONTROL_FOLDER_ROOT,  getClassByNameComp(NAME_CLASS_CONTROL_FOLDER).id, false, -1, "7d4f4e64-59ad-47a3-a9c3-cc7c24b301f1", null), false);
	    }
	}
	
	public void createClassChatClass() throws DriverException, SQLException {
	    if (!checkExistenceClassByName("ChatClass")) {
	        db.addClass(createClass("ChatClass",  sysCls.id, false, -1, "78706682-4814-42ad-b342-6ce8335479cd", null), false);
	    }
	}
	
	public void createClassAction() throws DriverException, SQLException {
	    if (!checkExistenceClassByName("Action")) {
	        db.addClass(createClass("Action",  sysCls.id, false, -1, "0493858a-272f-4bdc-a050-e98176ceda78", null), false);
	    }
	}
	
	/**
	 * Исправление структуры класса <code>ConfigGlobal</code>.
	 * 
	 * @throws KrnException
	 *             the krn exception
	 * @throws DriverException
	 * @throws SQLException
	 */
	public void patchStructureClassConfigGlobal() throws DriverException, SQLException {
	    KrnClass clsCnf = getClassByNameComp(NAME_CLASS_CONFIG_GLOBAL);
	    KrnAttribute attr = null;
	    
	    if (getAttributeByNameComp(clsCnf, ATTR_COLOR_BACK_TAB_TITLE) == null) {
	        attr = createAttribute(-1, "6307ba1c-69ad-4236-9cc9-c95eb55afde1", clsCnf.id, CID_STRING, ATTR_COLOR_BACK_TAB_TITLE, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Цвет фона заголовка фоновых вкладок.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_COLOR_FONT_BACK_TAB_TITLE) == null) {
	        attr = createAttribute(-1, "738a3391-0e5a-4ec0-9fee-b9bdeb0cbfb1", clsCnf.id, CID_STRING, ATTR_COLOR_FONT_BACK_TAB_TITLE, 0, false, false, false, false, 0,
	                0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Цвет шрифта заголовка фоновых вкладок.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_COLOR_FONT_TAB_TITLE) == null) {
	        attr = createAttribute(-1, "c6871349-b700-4bdb-b109-a66f26b31039", clsCnf.id, CID_STRING, ATTR_COLOR_FONT_TAB_TITLE, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Цвет шрифта заголовка выбранной вкладки.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_COLOR_HEADER_TABLE) == null) {
	        attr = createAttribute(-1, "4166591a-6426-4b24-8bc5-7f766a5b39cc", clsCnf.id, CID_STRING, ATTR_COLOR_HEADER_TABLE, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Цвет заголовков таблиц и деревьев.");
	
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_COLOR_MAIN) == null) {
	        attr = createAttribute(-1, "e58b2908-889a-4870-9688-12b6e65ba04e", clsCnf.id, CID_STRING, ATTR_COLOR_MAIN, 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Цвет, определяющий основную цветовую гамму интерфейсов.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_COLOR_TAB_TITLE) == null) {
	        attr = createAttribute(-1, "990637da-6ceb-4085-b4e6-e1f52a7332f3", clsCnf.id, CID_STRING, ATTR_COLOR_TAB_TITLE, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Цвет фона заголовка выбранной вкладки.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_GRADIENT_CONTROL_PANEL) == null) {
	        attr = createAttribute(-1, "ca50710c-2555-45fd-894b-7342fae73ef9", clsCnf.id, CID_STRING, ATTR_GRADIENT_CONTROL_PANEL, 0, false, false, false, false, 0, 0,
	                0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Параметры градиентной заливки для панели управления системы.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_GRADIENT_FIELD_NO_FLC) == null) {
	        attr = createAttribute(-1, "59fd8bc6-5ea5-432f-a91a-d1adeaa75672", clsCnf.id, CID_STRING, ATTR_GRADIENT_FIELD_NO_FLC, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Параметры градиентной заливки для полей не прошедших ФЛК.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_GRADIENT_MAIN_FRAME) == null) {
	        attr = createAttribute(-1, "afa9684b-fa05-4589-b04c-b5e828636a33", clsCnf.id, CID_STRING, ATTR_GRADIENT_MAIN_FRAME, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Параметры градиентной заливки для главного фрейма системы.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_GRADIENT_MENU_PANEL) == null) {
	        attr = createAttribute(-1, "c7ec2a74-5032-408d-8073-f8ec3ae30e04", clsCnf.id, CID_STRING, ATTR_GRADIENT_MENU_PANEL, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Параметры градиентной заливки для панели меню системы.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_TRANSPARENT_BACK_TAB_TITLE) == null) {
	        attr = createAttribute(-1, "291d9a77-138c-4259-9914-dc1c416ac109", clsCnf.id, CID_INTEGER, ATTR_TRANSPARENT_BACK_TAB_TITLE, 0, false, false, false, false, 0,
	                0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Прозрачность заголовка фоновой вкладки");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_TRANSPARENT_CELL_TABLE) == null) {
	        attr = createAttribute(-1, "00470fac-ab80-46f7-af82-8af329412ff4", clsCnf.id, CID_INTEGER, ATTR_TRANSPARENT_CELL_TABLE, 0, false, false, false, false, 0, 0,
	                0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Прозрачность ячеек таблиц и деревьев");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_TRANSPARENT_MAIN) == null) {
	        attr = createAttribute(-1, "ea92e5e5-0294-4bac-8dba-9cf5479d8633", clsCnf.id, CID_INTEGER, ATTR_TRANSPARENT_MAIN, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Прозрачность панелей системы, позволяет включить/выключить возможность работы с ней.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_TRANSPARENT_DIALOG) == null) {
	        attr = createAttribute(-1, "6d76dfe3-07e5-4834-a94e-4313a2e5e90d", clsCnf.id, CID_INTEGER, ATTR_TRANSPARENT_DIALOG, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Прозрачность диалогов системы, позволяет включить/выключить возможность работы с ней.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_TRANSPARENT_SELECTED_TAB_TITLE) == null) {
	        attr = createAttribute(-1, "e73cc440-24c0-467b-bef4-7d114effa7df", clsCnf.id, CID_INTEGER, ATTR_TRANSPARENT_SELECTED_TAB_TITLE, 0, false, false, false,
	                false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Прозрачность фона заголовка выбранной вкладки");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_BLUE_SYS_COLOR) == null) {
	        attr = createAttribute(-1, "01dc0985-f289-44ea-9fdb-415022763211", clsCnf.id, CID_STRING, ATTR_BLUE_SYS_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной blueSysColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_DARK_SHADOW_SYS_COLOR) == null) {
	        attr = createAttribute(-1, "3392bc59-ba1f-4c39-88e7-ef43e9cab383", clsCnf.id, CID_STRING, ATTR_DARK_SHADOW_SYS_COLOR, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной darkShadowSysColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_MID_SYS_COLOR) == null) {
	        attr = createAttribute(-1, "2c14b8e4-8517-4e06-9d93-fb25cf59dfbb", clsCnf.id, CID_STRING, ATTR_MID_SYS_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной midSysColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_LIGHT_YELLOW_COLOR) == null) {
	        attr = createAttribute(-1, "0c180cd5-4770-4199-bd90-39101cb089bb", clsCnf.id, CID_STRING, ATTR_LIGHT_YELLOW_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной lightYellowColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_RED_COLOR) == null) {
	        attr = createAttribute(-1, "f2ffd9e6-fba2-444f-ae08-54843763bdbd", clsCnf.id, CID_STRING, ATTR_RED_COLOR, 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной redColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_LIGHT_RED_COLOR) == null) {
	        attr = createAttribute(-1, "cb60b8cd-8928-455d-bd28-25976742a5e6", clsCnf.id, CID_STRING, ATTR_LIGHT_RED_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной lightRedColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_LIGHT_GREEN_COLOR) == null) {
	        attr = createAttribute(-1, "480d126d-e24b-451c-8b3a-5dc47dcb204d", clsCnf.id, CID_STRING, ATTR_LIGHT_GREEN_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной lightGreenColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_SHADOW_YELLOW_COLOR) == null) {
	        attr = createAttribute(-1, "3b069791-678d-46f6-a236-8ee9778be1db", clsCnf.id, CID_STRING, ATTR_SHADOW_YELLOW_COLOR, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной shadowYellowColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_SYS_COLOR) == null) {
	        attr = createAttribute(-1, "f315c350-f093-460b-9109-e8d97828ec74", clsCnf.id, CID_STRING, ATTR_SYS_COLOR, 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной sysColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_LIGHT_SYS_COLOR) == null) {
	        attr = createAttribute(-1, "839b1e80-31da-4d80-92a1-52a6e62c7aa4", clsCnf.id, CID_STRING, ATTR_LIGHT_SYS_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной lightSysColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_DEFAULT_FONT_COLOR) == null) {
	        attr = createAttribute(-1, "e72a6cdd-9f8b-4117-a9f2-d161cd06f691", clsCnf.id, CID_STRING, ATTR_DEFAULT_FONT_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной defaultFontColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_SILVER_COLOR) == null) {
	        attr = createAttribute(-1, "148280fb-602b-4f03-8722-be4c7bbdc620", clsCnf.id, CID_STRING, ATTR_SILVER_COLOR, 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной silverColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_SHADOWS_GREY_COLOR) == null) {
	        attr = createAttribute(-1, "e4e8aab4-d606-42e0-982e-d1e2db170a57", clsCnf.id, CID_STRING, ATTR_SHADOWS_GREY_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной shadowsGreyColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_KEYWORD_COLOR) == null) {
	        attr = createAttribute(-1, "5f151036-aae5-4f7f-a2f5-9b372a8b48d5", clsCnf.id, CID_STRING, ATTR_KEYWORD_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной keywordColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_VARIABLE_COLOR) == null) {
	        attr = createAttribute(-1, "f4c3954f-d06b-4fc7-a461-bab3ce0aa0f8", clsCnf.id, CID_STRING, ATTR_VARIABLE_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной variableColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_CLIENT_VARIABLE_COLOR) == null) {
	        attr = createAttribute(-1, "ff8ac8a8-a7ca-47d4-a06b-4e14e57e568f", clsCnf.id, CID_STRING, ATTR_CLIENT_VARIABLE_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной clientVariableColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_COMMENT_COLOR) == null) {
	        attr = createAttribute(-1, "975b10a2-6920-43d5-9698-06c9fac7a763", clsCnf.id, CID_STRING, ATTR_COMMENT_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной commentColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_OBJECT_BROWSER_LIMIT) == null) {
	        attr = createAttribute(-1, "0b3c77f0-716a-4f89-8f52-674a4c32563f", clsCnf.id, CID_INTEGER, ATTR_OBJECT_BROWSER_LIMIT, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Лимит количества объектов класса для отображения");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_OBJECT_BROWSER_LIMIT_FOR_CLASSES) == null) {
	        attr = createAttribute(-1, "9f1c3b08-414d-4595-930e-cbd5099fc772", clsCnf.id, getClassByNameComp(NAME_CLASS_PROPERTY).id, ATTR_OBJECT_BROWSER_LIMIT_FOR_CLASSES, COLLECTION_SET, false, false,
	                false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid,
	                "Набор лимитов количества объектов для отображения. Задаёт индивидуальные, для каждого класса, лимиты.");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_IS_OBJECT_BROWSER_LIMIT) == null) {
	        attr = createAttribute(-1, "20ff2e23-c65a-453e-a438-daa140e959b3", clsCnf.id, CID_BOOL, ATTR_IS_OBJECT_BROWSER_LIMIT, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Активирует лимитированное отображение классов.");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_IS_OBJECT_BROWSER_LIMIT_FOR_CLASSES) == null) {
	        attr = createAttribute(-1, "7c0d600b-8ef5-496b-b6a0-079665b55ac2", clsCnf.id, CID_BOOL, ATTR_IS_OBJECT_BROWSER_LIMIT_FOR_CLASSES, 0, false, false, false,
	                false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Активирует индивидуальное лимитированное отображение классов.");
	    }
	}
	
	/**
	 * Исправление структуры класса <code>ConfigLocal</code>
	 * 
	 * @throws KrnException
	 *             the krn exception
	 * @throws DriverException
	 * @throws SQLException
	 */
	public void patchStructureClassConfigLocal() throws DriverException, SQLException {
	    KrnClass clsCnf = getClassByNameComp(NAME_CLASS_CONFIG_LOCAL);
	    KrnAttribute attr = null;
	    if (getAttributeByNameComp(clsCnf, "maxObjectCount") == null) {
	        attr = createAttribute(-1, "b2b451af-578c-4870-8736-cb36802d367b", clsCnf.id, CID_INTEGER, "maxObjectCount", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Количество объектов, отображаемых в инспекторе классов.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, "isToolBar") == null) {
	        attr = createAttribute(-1, "051e27fe-b6ac-4570-b11b-0671fb3ef709", clsCnf.id, CID_INTEGER, "isToolBar", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Флаг отображения панели инструментов в WEB интерфейсе.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, "isMonitor") == null) {
	        attr = createAttribute(-1, "b28d35cb-f48f-4b98-ae34-9ad3262fd9c1", clsCnf.id, CID_INTEGER, "isMonitor", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Флаг отображения монитора задач.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, "configByUUIDs") == null) {
	        attr = createAttribute(-1, "170fd002-70f0-4781-876a-f5ee20fcce53", clsCnf.id, getClassByNameComp(NAME_CLASS_CONFIG_OBJECT).id, "configByUUIDs", COLLECTION_SET, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Конфигурацию объектов системы (набор по идентификаторам)");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_HISTORY_SRV) == null) {
	        attr = createAttribute(-1, "25740e90-1b2d-4736-a46a-7062860fdfba", clsCnf.id, getClassByNameComp("ProcessDef").id, ATTR_HISTORY_SRV, COLLECTION_SET, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "История просмотра процессов.");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_HISTORY_IFC) == null) {
	        attr = createAttribute(-1, "1ebee8e3-5727-4d4c-a1a6-98fa963e14d8", clsCnf.id, getClassByNameComp("UI").id, ATTR_HISTORY_IFC, COLLECTION_SET, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "История просмотра интерфейсов.");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_HISTORY_FLT) == null) {
	        attr = createAttribute(-1, "988671d2-04b0-4cb2-a597-3d95121f1935", clsCnf.id, getClassByNameComp("Filter").id, ATTR_HISTORY_FLT, COLLECTION_SET, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "История просмотра фильтров.");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_HISTORY_RPT) == null) {
	        attr = createAttribute(-1, "470d1b8b-bbcd-46ee-8b10-5f6b2fe6f177", clsCnf.id, getClassByNameComp("ReportPrinter").id, ATTR_HISTORY_RPT, COLLECTION_SET, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "История просмотра отчётов.");
	    }
	}
	
	/**
	 * Исправление структуры класса <code>ConfigObject</code>
	 * 
	 * @throws KrnException
	 *             the krn exception
	 * @throws DriverException
	 * @throws SQLException
	 */
	public void patchStructureClassConfigObject() throws DriverException, SQLException {// "Config"
	    KrnClass clsCnf = getClassByNameComp(NAME_CLASS_CONFIG_OBJECT);
	    KrnAttribute attr = null;
	    if (getAttributeByNameComp(clsCnf, "uuid") == null) {
	        attr = createAttribute(-1, "c31cfc88-5ce2-41c9-ab6b-b3a48185b6e4", clsCnf.id, CID_STRING, "uuid", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Идентификатор объекта.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, "properties") == null) {
	        attr = createAttribute(-1, "414609ba-0a93-46fb-a3d6-3e0196d1957c", clsCnf.id, getClassByNameComp(NAME_CLASS_PROPERTY).id, "properties", COLLECTION_SET, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Набор свойств для определённого объекта");
	    }
	}
	
	/**
	 * Исправление структуры класса <code>Property</code>
	 * 
	 * @throws KrnException
	 *             the krn exception
	 * @throws DriverException
	 * @throws SQLException
	 */
	public void patchStructureClassProperty() throws DriverException, SQLException {
	    KrnClass clsCnf = getClassByNameComp(NAME_CLASS_PROPERTY);
	    KrnAttribute attr = null;
	    if (getAttributeByNameComp(clsCnf, "name") == null) {
	        attr = createAttribute(-1, "0b0bf839-320a-4691-b31a-a8bf605c918f", clsCnf.id, CID_STRING, "name", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Название свойства.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, "value") == null) {
	        attr = createAttribute(-1, "3e5c2b96-2dd0-4e2d-b977-da9ef9b790dc", clsCnf.id, CID_STRING, "value", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Значение свойства.");
	    }
	}
	
	/**
	 * Исправление структуры класса <code>User</code>
	 * 
	 * @throws KrnException
	 *             the krn exception
	 * @throws DriverException
	 * @throws SQLException
	 */
	public void patchStructureClassUser() throws DriverException, SQLException {
	    KrnClass clsCnf = getClassByNameComp("User");
	    KrnAttribute attr = null;
	    if (getAttributeByNameComp(clsCnf, "config") == null) {
	        attr = createAttribute(-1, "152eeb81-81e3-4268-b180-1189f0cdf348", clsCnf.id, getClassByNameComp(NAME_CLASS_CONFIG_LOCAL).id, "config", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Пользовательская конфигурация.");
	    }
	    KrnAttribute uLLT = getAttributeByNameComp(clsCnf, "lastLoginTime");
	    if (uLLT == null) {
	        uLLT = createAttribute(-1, "3279f7c4-591d-4590-93e9-9aee2d36b92c", clsCnf.id, CID_TIME, "lastLoginTime", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(uLLT.uid, "Время последней авторизации пользователя.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, "isLogged") == null) {
	        attr = createAttribute(-1, "ffdc3e8b-4f71-4c0d-a83c-f325aea6e477", clsCnf.id, CID_BOOL, "isLogged", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Признак входа пользователя в систему.");
	        // инициализировать значения атрибута у всех пользователей
	        List<KrnObject> objA = getObjects(clsCnf.id, new int[1], 0);
	        for (KrnObject obj : objA) {
	            setValue(obj, attr.id, 0, 0, 0, new Long(1), false);
	            setValue(obj, uLLT.id, 0, 0, 0, new Timestamp(System.currentTimeMillis()), false);
	        }
	    }
	
	    if (getAttributeByNameComp(clsCnf, "previous passwords") == null) {
	        attr = createAttribute(-1, "ce249743-5529-4128-ac9c-06102fa296c1", clsCnf.id, CID_MEMO, "previous passwords", 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Хэши предыдущих паролей.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, "время блокировки") == null) {
	        attr = createAttribute(-1, "06786e1d-5fd6-4be2-82c6-9d5ede6e2fcc", clsCnf.id, CID_TIME, "время блокировки", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Время блокировки пользователя.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, "дата изменения пароля") == null) {
	        attr = createAttribute(-1, "4d611c48-367a-4a49-b299-49708281b038", clsCnf.id, CID_TIME, "дата изменения пароля", 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Дата изменения пароля.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, "дата истечения срока действия пароля") == null) {
	        attr = createAttribute(-1, "6573ec9a-3f33-418b-b628-a88ee3827017", clsCnf.id, CID_DATE, "дата истечения срока действия пароля", 0, false, false, false,
	                false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Дата истечения срока действия пароля.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, "кол неуд авторизаций") == null) {
	        attr = createAttribute(-1, "c21a78aa-316e-4efb-94db-33704e21e44e", clsCnf.id, CID_INTEGER, "кол неуд авторизаций", 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Разрешённое количество ошибочных авторизаций.");
	    }
	}
	
    /**
     * Исправление структуры класса <code>ControlFolder</code>
     * 
     * @throws KrnException
     *             the krn exception
     * @throws DriverException
     * @throws SQLException
     */
    public void patchStructureClassControlFolder() throws DriverException, SQLException {
        KrnClass clsCnf = getClassByNameComp(NAME_CLASS_CONTROL_FOLDER);
	    KrnAttribute attr = null;
        if (getAttributeByNameComp(clsCnf, "title") == null) {
            attr = createAttribute(-1, "5f7d4108-3b3b-4b30-8cd0-dfcafbdb40fe", clsCnf.id, CID_STRING, "title", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Наименование папки.");
        }
        KrnAttribute attr1 = getAttributeByNameComp(clsCnf, "parent");
        if (attr1 == null) {
            attr1 = createAttribute(-1, "40b3ae0b-3d03-4324-958a-3e3505925718", clsCnf.id, getClassByNameComp(NAME_CLASS_CONTROL_FOLDER).id, "parent", 0, false, true, false, true, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr1.uid, "Предок.");
        }

        if (getAttributeByNameComp(clsCnf, "value") == null) {
            attr = createAttribute(-1, "1547c99e-7347-4568-87c1-1cb6fa9e86a3", clsCnf.id, 99, "value", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Объект узла, если его нет, то это директория.");
        }
        if (getAttributeByNameComp(clsCnf, "children") == null) {
            attr = createAttribute(-1, "103041a0-a1b4-4b8f-bac4-f5630953434b", clsCnf.id, getClassByNameComp(NAME_CLASS_CONTROL_FOLDER).id, "children", COLLECTION_SET, false, false, false, true, 0, 0, attr1.id, 0,
                    false, null, 0);
            setAttributeComment(attr.uid, "Потомки.");
        }
        if (getAttributeByNameComp(clsCnf, "type") == null) {
            attr = createAttribute(-1, "1b23c99e-73e3-4t68-84c1-1cb5ft5e96r0", clsCnf.id, CID_INTEGER, "type", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            setAttributeComment(attr.uid, "Тип директории");
        }
    }
	
	/**
	 * Исправление структуры класса <code>ProcessDef</code>
	 * 
	 * @throws KrnException
	 *             the krn exception
	 * @throws DriverException
	 * @throws SQLException
	 */
	public void patchStructureClassProcessDef() throws DriverException, SQLException {
	    KrnClass clsCnf = getClassByNameComp("ProcessDef");
	    if (getAttributeByNameComp(clsCnf, "isBtnToolBar") == null) {
	    	KrnAttribute attr = createAttribute(-1, "cb25902a-83a2-405d-a370-bcf4a4de3c67", clsCnf.id, CID_INTEGER, "isBtnToolBar", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Для процесса задана кнопка на панели задач.");
	    }
	}
	
	
	public void patchStructureClassChatClass() throws DriverException, SQLException {
	    KrnClass clsCnf = getClassByNameComp("ChatClass");
	    if (getAttributeByNameComp(clsCnf, "from") == null) {
	        createAttribute(-1, "572b98e4-1f67-4572-8d91-3f86f43c297d", clsCnf.id, CID_STRING, "from", 0, false, false, false,
	                false, 0, 0, 0, 0, false, null, 0);
	    }
	    if (getAttributeByNameComp(clsCnf, "to") == null) {
	        createAttribute(-1, "ead0cee4-fa20-41f1-a6d6-75544d4db5a0", clsCnf.id, CID_STRING, "to", 0, false, false, false,
	                false, 0, 0, 0, 0, false, null, 0);
	    }
	    if (getAttributeByNameComp(clsCnf, "canDeleteFrom") == null) {
	        createAttribute(-1, "27f1293a-9464-4c1c-9aee-9cc0ecde95b1", clsCnf.id, CID_STRING, "canDeleteFrom", 0, false, false,
	                false, false, 0, 0, 0, 0, false, null, 0);
	    }
	    if (getAttributeByNameComp(clsCnf, "canDeleteTo") == null) {
	        createAttribute(-1, "23875a76-616d-43e0-a162-2e4d00c59ce3", clsCnf.id, CID_STRING, "canDeleteTo", 0, false, false,
	                false, false, 0, 0, 0, 0, false, null, 0);
	    }
	    if (getAttributeByNameComp(clsCnf, "text") == null) {
	        createAttribute(-1, "9ed35af2-e688-4a86-98e3-bf04245a7932", clsCnf.id, CID_STRING, "text", 0, false, false, false,
	                false, 0, 0, 0, 0, false, null, 0);
	    }
	    if (getAttributeByNameComp(clsCnf, "datetime") == null) {
	        createAttribute(-1, "4306c1f4-f63a-40be-b88b-e39e88b38eb2", clsCnf.id, CID_TIME, "datetime", 0, false, false, false,
	                false, 0, 0, 0, 0, false, null, 0);
	    }
	
	}
	
	public void patchStructureClassAction() throws DriverException, SQLException {
	    KrnClass clsCnf = getClassByNameComp("Action");
	    if (getAttributeByNameComp(clsCnf, "action") == null) {
	        createAttribute(-1, "d21bf203-ee89-47c0-b68d-22d50bdbe399", clsCnf.id, CID_STRING, "action", 0, false, false, false,
	                false, 0, 0, 0, 0, false, null, 0);
	    }
	    if (getAttributeByNameComp(clsCnf, "editingDate") == null) {
	        createAttribute(-1, "67500c8a-47e1-4ca7-ae74-e4d280584b2e", clsCnf.id, CID_TIME, "editingDate", 0, false, false,
	                false, false, 0, 0, 0, 0, false, null, 0);
	    }
	    if (getAttributeByNameComp(clsCnf, "id") == null) {
	        createAttribute(-1, "4b1702b3-75f2-43c2-a6d3-60e69f6bdf23", clsCnf.id, CID_INTEGER, "id", 0, false, false, false,
	                false, 0, 0, 0, 0, false, null, 0);
	    }
	    if (getAttributeByNameComp(clsCnf, "log") == null) {
	        createAttribute(-1, "cae290d5-90b3-40c5-a17a-b48e68bc830b", clsCnf.id, CID_MEMO, "log", 0, false, false, false,
	                false, 0, 0, 0, 0, false, null, 0);
	    }
	    if (getAttributeByNameComp(clsCnf, "name") == null) {
	        createAttribute(-1, "d50d2b5e-6ea2-46ea-b544-dce503776591", clsCnf.id, CID_STRING, "name", 0, false, false, false,
	                false, 0, 0, 0, 0, false, null, 0);
	    }
	    if (getAttributeByNameComp(clsCnf, "type") == null) {
	        createAttribute(-1, "fff97ce5-756a-4042-a31f-bea4564e2a38", clsCnf.id, CID_STRING, "type", 0, false, false, false,
	                false, 0, 0, 0, 0, false, null, 0);
	    }
	    if (getAttributeByNameComp(clsCnf, "user") == null) {
	        createAttribute(-1, "0ac1df93-873b-492f-83b2-31fd63ad2633", clsCnf.id, CID_STRING, "user", 0, false, false, false,
	                false, 0, 0, 0, 0, false, null, 0);
	    }
	
	}
	
	public void patchStructureClassUI() throws DriverException, SQLException {
	    KrnClass clsCnf = getClassByNameComp("UI");
	    KrnAttribute attr = null;
	    if (getAttributeByNameComp(clsCnf, "webConfigChanged") == null) {
	        createAttribute(-1, "3260cb5b-3a9e-44ae-b6f0-41dea77279e2", clsCnf.id, CID_BOOL, "webConfigChanged", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
	    }
	    if (getAttributeByNameComp(clsCnf, "webConfig") == null) {
	        attr = createAttribute(-1, "2ea06fc2-66d6-4530-bd7a-98cc54124ce7", clsCnf.id, CID_BLOB, "webConfig", 0, false, false, true, true, 0, 0, 0, 0,  false, false, null, 0);
	    }
	}
	
	public void upgradeTo13() throws DriverException, SQLException {
	    isUpgrading = true;
	    sysCls = getClassByNameComp("Системный класс");
	    log.info("Апгрейд БД до версии 13 ...");
	    // Создать классы
	    createClassConfigGlobal();
	    createClassConfigLocal();
	    createClassConfigObject();
	    createClassProperty();
	    createClassControlFolder();
	    createClassControlFolderRoot();
	    createClassChatClass();
	    createClassAction();
	    db.addClass(getClassByNameComp("ProcessDef"), false);
        db.addClass(getClassByNameComp("UI"), false);
        db.addClass(getClassByNameComp("Filter"), false);
        db.addClass(getClassByNameComp("ReportPrinter"), false);
	    db.addClass(getClassByNameComp(NAME_CLASS_CONFIG_GLOBAL), false);
	    db.addClass(getClassByNameComp(NAME_CLASS_CONFIG_LOCAL), false);
	    db.addClass(getClassByNameComp(NAME_CLASS_PROPERTY), false);
	    db.addClass(getClassByNameComp(NAME_CLASS_CONTROL_FOLDER), false);
	    db.addClass(getClassByNameComp(NAME_CLASS_CONTROL_FOLDER_ROOT), false);
	    db.addClass(getClassByNameComp("ChatClass"), false);
	    db.addClass(getClassByNameComp("Action"), false);
	    db.addClass(getClassByNameComp("User"), false);
	    db.addClass(getClassByNameComp("UserFolder"), false);
	    db.addClass(getClassByNameComp("UserRoot"), false);
	    db.addClass(getClassByNameComp("Language"), false);
	    db.addClass(getClassByIdComp(99), false);
	    
	    //try {
	        patchStructureClassConfigGlobal();
	        patchStructureClassConfigLocal();
	        patchStructureClassConfigObject();
	        patchStructureClassProperty();
	        patchStructureClassUser();
	        patchStructureClassControlFolder();
	        patchStructureClassProcessDef();
	        patchStructureClassChatClass();
	        patchStructureClassAction();
	        patchStructureClassUI();
	        log.info("Апгрейд БД до версии 13 успешно завершен.");
	    //} catch (Exception e) {
	    //    log.error(e, e);
	    //    isUpgrading = false;
        //}
        isUpgrading = false;
    }
    
	@Override
	public boolean renameClassTable(long id, String newName) {//TODO r tname
		boolean result = false;
		if (id > 99) {
			String sql;
			PreparedStatement istnane = null;
			QueryRunner qr = new QueryRunner(true);
			try {
				sql = "SELECT c_id FROM "+getDBPrefix()+"t_attrs where c_class_id="+id+" and c_col_type <> 0 AND c_rattr_id is null";
				istnane = conn.prepareStatement(sql);
				ResultSet rst = istnane.executeQuery();
				while (rst.next()) {
					long c_id = rst.getLong(1);
					String c_tname = getAttrTableName(c_id);
	
					try {
						sql = "ALTER TABLE "+c_tname+" DROP FOREIGN KEY `"+getAttrFKName(id, c_id)+"`";
						
						log.info("SQL1: " + sql);
						qr.update(conn, sql);
					} catch (Exception e) {
						log.error(e, e);
						//
					}
				}
				rst.close();
				istnane.close();
	
				sql = "SELECT c_id FROM "+getDBPrefix()+"t_attrs where c_type_id="+id+" and c_col_type <> 0 AND c_rattr_id is null";
				istnane = conn.prepareStatement(sql);
				ResultSet rstt = istnane.executeQuery();
				while (rstt.next()) {
					long c_id = rstt.getLong(1);
					String c_tname = getAttrTableName(c_id);
					
					try {
						sql = "ALTER TABLE "+c_tname+" DROP FOREIGN KEY `"+getAttrFKName(c_id)+"`";
						log.info("SQL2: " + sql);
						qr.update(conn, sql);
					} catch (Exception e) {
						log.error(e, e);
						//
					}
				}
				rstt.close();
				istnane.close();
	
				String TableName = getClassTableName(id);
				try {
					sql = "ALTER TABLE " + TableName + " RENAME TO " + getDBPrefix() + "`" + newName + "`";
					log.info("SQL Query...\n " + sql);
					qr.update(conn, sql);
				} catch (SQLException e) {
					//sql = "ALTER TABLE " + newName + " RENAME TO " + newName + ";";
					//log.info("SQL Query...\n " + sql);
					//qr.update(conn, sql);
				}

				db.getClassById(id).tname = newName;
	
				sql = "UPDATE "+getDBPrefix()+"t_classes SET c_tname='" + newName + "' WHERE c_id=" + id;
	//			log.info("SQL Query...\n " + sql); //TODO DEL
				qr.update(conn, sql);
	
				
				sql = "SELECT c_id FROM "+getDBPrefix()+"t_attrs where c_class_id="+id+" and c_col_type <> 0 AND c_rattr_id is null";
				istnane = conn.prepareStatement(sql);
				ResultSet rs = istnane.executeQuery();
				while (rs.next()) {
					long c_id = rs.getLong(1);
					String c_tname = getAttrTableName(c_id);
					sql = "DELETE FROM "+c_tname+" WHERE c_obj_id NOT IN (SELECT c_obj_id from " + getDBPrefix() +"`" + newName + "`)";
					try {
						log.info("SQL3: " + sql);
						qr.update(conn, sql);
					} catch (Exception e) {
						log.error(e, e);
						//
					}

					sql = "ALTER TABLE "+c_tname+" ADD CONSTRAINT `"+getAttrFKName(id, c_id)+"`"
							+" FOREIGN KEY (`c_obj_id`, `c_tr_id`)"
							+" REFERENCES " + getDBPrefix() + "`"+newName+"` (`c_obj_id`, `c_tr_id`)"
							+" ON DELETE CASCADE ON UPDATE CASCADE";
					try {
						log.info("SQL4: " + sql);
						qr.update(conn, sql);
					} catch (Exception e) {
						log.error(e, e);
						//
					}
				}
				rs.close();
				istnane.close();
	
				sql = "SELECT c_id, c_is_multilingual FROM "+getDBPrefix()+"t_attrs where c_type_id="+id+" and c_col_type <> 0 AND c_rattr_id is null";
				istnane = conn.prepareStatement(sql);
				ResultSet rstc = istnane.executeQuery();
				while (rstc.next()) {
					long c_id = rstc.getLong(1);
					long c_is_multilingual = rstc.getLong(2);
					String c_tname = getAttrTableName(c_id);
					String colname = getColumnName(c_id);
					if (c_is_multilingual != 0) {
						List<KrnObject> langs = null;
						try {
							langs = getSystemLangs();
							StringBuffer colnameB = new StringBuffer();
							long l = 0L;
							for (KrnObject lang : langs) {
								if (l > 0){
									colnameB.append(",");
								}
								colnameB.append("`"+colname+"_"+getSystemLangIndex(lang.id)+"`");
								l++;
							}
							colname = colnameB.toString();
						} catch (DriverException e) {
							log.error(e, e);
						}
					} else {
						colname = "`" + colname + "`";
					}
					
					sql = "ALTER TABLE "+c_tname
							+" ADD CONSTRAINT `"+getAttrFKName(c_id)+"`"
							+" FOREIGN KEY ("+colname+")"
							+" REFERENCES " + getDBPrefix() + "`"+newName+"` (`c_obj_id` )"
							+" ON DELETE NO ACTION"
							+" ON UPDATE NO ACTION;";
						try {
							log.info("SQL5: " + sql);
							qr.update(conn, sql);
						} catch (Exception e) {
							log.error(e, e);
							//
						}
				}
				rstc.close();
				istnane.close();
	
				result = true;
			} catch (SQLException e) {
				log.error("ERROR RenameClassTable: " + e.getMessage());
			} finally {
				DbUtils.closeQuietly(istnane);
			}
		}
		return result;
	}
	
	@Override
	public boolean renameAttrTable(KrnAttribute attr, String newName) {
		boolean result = false;
		String sql = null;
		QueryRunner qr = new QueryRunner(true);
		String TableName = getClassTableName(attr.classId);
		String ColumnName = getColumnName(attr);
		try {
			if (attr.collectionType != 0){
				TableName = getAttrTableName(attr);
				try {
					sql = "ALTER TABLE " + TableName + " RENAME TO " + getDBPrefix() + "`" + newName + "`";
					log.info("SQL Query...\n " + sql);
					qr.update(conn, sql);
				} catch (SQLException e) {
					//sql = "ALTER TABLE " + newName + " RENAME TO " + newName + " ;";
					//log.info("SQL Query...\n " + sql);
					//qr.update(conn, sql);
				}
				TableName = getDBPrefix() + "`" + newName + "`";
			}

			if (attr.isMultilingual) {
				List<KrnObject> langs = getSystemLangs();
				long errorCount = 0;
				for (KrnObject lang : langs) {
					try {
						sql = "ALTER TABLE "+TableName+" CHANGE COLUMN "+ColumnName+"_"+getSystemLangIndex(lang.id)+" "+newName+"_"+getSystemLangIndex(lang.id)+" " + getSqlTypeName(attr);
						log.info("SQL Query...\n " + sql);
						qr.update(conn, sql);
					} catch (SQLException e) {
						errorCount++;
						log.error("ERROR RENAMING: " + sql);
					}
				}
				if (errorCount == langs.size()) return false;
			} else {
				if (attr.typeClassId > 99) {
					sql = "ALTER TABLE " +TableName+ " DROP FOREIGN KEY `FK" + attr.id + "`";
					try {
						qr.update(conn, sql);
					} catch (Exception e) {
						log.error(e, e);
					}
				}
				sql = "ALTER TABLE "+TableName+" CHANGE COLUMN "+ColumnName+" "+newName+" " + getSqlTypeName(attr);
				if (attr.typeClassId > 99){
					sql += ", ADD CONSTRAINT `FK" + attr.id + "` FOREIGN KEY (`"+newName+"` ) REFERENCES "+getClassTableName(attr.typeClassId)+" (`c_obj_id` )";
				}
				log.info("SQL Query...\n " + sql);
				qr.update(conn, sql);
			}
			sql = "UPDATE "+getDBPrefix()+"t_attrs SET c_tname='" + newName + "' WHERE c_id=" + attr.id;
			log.info("SQL Query...\n " + sql);
			try {
				qr.update(conn, sql);
			} catch (Exception e) {
				log.error(e, e);
			}
			result = true;
		} catch (Exception e) {
			log.error("ERROR renameAttrTable: " + e.getMessage());
		}
		
		return result;
	}
	
//	protected String getClassTname(long clsId){
//    	return Driver2.getClassTname(clsId, conn);
//    }
    
	@Override
	public boolean upgradeTo17(){
		boolean result = false;
		try { //TODO DB tname
	        log.info("Апгрейд БД до версии 17 ...");
			QueryRunner qr = new QueryRunner(true);
        	String sql = "ALTER TABLE " + getDBPrefix() + "t_classes ADD COLUMN c_tname VARCHAR(30) NULL DEFAULT NULL " +
			" AFTER c_cuid, ADD UNIQUE INDEX `c_tname_UNIQUE` (`c_tname` ASC) ;";
        	qr.update(conn, sql);
			
			sql = "ALTER TABLE " + getDBPrefix() + "t_attrs ADD COLUMN c_tname VARCHAR(30) NULL " +
					"AFTER c_auid, ADD UNIQUE INDEX `c_tname_UNIQUE` (`c_class_id`, `c_tname`);";
			qr.update(conn, sql);
			
			log.info("Апгрейд БД до версии "+TnameVersionBD+" успешно завершен.");
			result = true;
		} catch (Exception e1) {
			log.error("ERROR to Update "+TnameVersionBD+"\n" + e1.getMessage());
		}
		return result;
    }
	
	@Override
	public String[][] getColumnsInfo(String tableName){
		PreparedStatement istnane = null;
		List<String[]> colL = new ArrayList<String[]>();
		try {
			String sql = "SELECT COLUMN_NAME,COLUMN_TYPE"
					+ " FROM INFORMATION_SCHEMA.COLUMNS"
					+ " WHERE TABLE_NAME=? AND TABLE_SCHEMA=?"
					+ " ORDER BY ORDINAL_POSITION";
			istnane = conn.prepareStatement(sql);
			istnane.setString(1, tableName);
			istnane.setString(2, db.getSchemeName());
			ResultSet rst = istnane.executeQuery();

			while (rst.next()) {
				colL.add(new String[]{Funcs.sanitizeSQL(rst.getString(1)), Funcs.sanitizeSQL(rst.getString(2))});
			}
			rst.close();
		} catch (SQLException e1) {
			log.error(e1.getMessage());
		} finally {
			DbUtils.closeQuietly(istnane);
		}
		return colL.toArray(new String[colL.size()][1]);
	}
	
	@Override
	public boolean columnMove(int[] cols, String tableName) {
		tableName = Funcs.sanitizeSQL(tableName);
		String[][] colL = getColumnsInfo(tableName);
		if (colL.length == 0){
			return false;
		}
		try {
			String sql;
			for (int i = cols.length-1; i >= 0; i--) {
				int j = cols[i]-1;
				sql = "alter table " + getDBPrefix() +"`"+tableName+"` change " + Funcs.sanitizeSQL(colL[j][0]) +" "+Funcs.sanitizeSQL(colL[j][0])+" "+Funcs.sanitizeSQL(colL[j][1])+" FIRST";

				QueryRunner qr = new QueryRunner(true);
				qr.update(conn, sql);
				conn.commit();
			}
		} catch (SQLException e1) {
			log.error(e1.getMessage());
			return false;
		}
	
		return true;
	}

    public void upgradeTo14() throws DriverException, SQLException {
        isUpgrading = true;

        // Загружаем классы, так как придется создавать объекты и устанавливать значения атрибутов
		db.reloadCache(this);

        log.info("Апгрейд БД до версии 14 ...");

        sysCls = getClassByNameComp("Event");
        KrnClass typeCls = getClassByNameComp("EventType");
        KrnClass cls = getClassByNameComp("SystemEvent");
        if (cls == null) {
            cls = createClass("SystemEvent", sysCls.id, true, 0, -1, "bf54be4d-fd9e-40b5-a221-b4786f22efa5", false, null);
            db.addClass(cls, false);
            setClassComment(cls.uid, "Справочник системных событий");
        }
        
        long eventClsId = cls.id;

	    KrnAttribute attr = null;
        if ((attr = getAttributeByNameComp(cls, "code")) == null) {
            attr = createAttribute(-1, "a90e4ad0-5584-4338-ab5c-dcfef8999d2c", cls.id, CID_INTEGER, "code", 0, false, true, false, true, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Код события");
        }
        long codeAttrId = attr.id;
        
        if ((attr = getAttributeByNameComp(cls, "name")) == null) {
            attr = createAttribute(-1, "94d92450-2c58-4e9c-aadf-bf8c1a733b27", cls.id, CID_STRING, "name", 0, false, false, true, true, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Наименование события");
        }
        long nameAttrId = attr.id;

        if ((attr = getAttributeByNameComp(cls, "type")) == null) {
            attr = createAttribute(-1, "481bd281-3934-4b3a-a2de-e64ce2dc09a1", cls.id, typeCls.id, "type", 0, false, true, false, true, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Тип события");
        }
        long typeAttrId = attr.id;

        if ((attr = getAttributeByNameComp(typeCls, "code")) == null) {
            attr = createAttribute(-1, "e89e1b2f-ebf0-4247-b6bd-a37691e85003", typeCls.id, CID_INTEGER, "code", 0, false, true, false, true, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Код типа события");
        }
        long typeCodeAttrId = attr.id;

        if ((attr = getAttributeByNameComp(typeCls, "events")) == null) {
            attr = createAttribute(-1, "d0ebba32-52c4-4386-8d66-9b6cb25faa83", typeCls.id, sysCls.id, "events", 2, false, false, false, true, 0, 0, typeAttrId,
            		codeAttrId, false, false, null, 0);
            setAttributeComment(attr.uid, "События типа");
        }
        attr = getAttributeByNameComp(typeCls, "type");
        long typeNameAttrId = attr.id;

        KrnClass langCls = getClassByNameComp("Language");
        KrnAttribute codeAttr = getAttributeByNameComp(langCls, "code");
        KrnObject obj_ru = getObjectsByAttribute(langCls.id, codeAttr.id, 0, CO_EQUALS, "RU", 0).get(0);
        KrnObject obj_kz = getObjectsByAttribute(langCls.id, codeAttr.id, 0, CO_EQUALS, "KZ", 0).get(0);

        List<KrnObject> objs = getObjects(typeCls.id, 0);
        
        // Если нет объектов в классе, то необходимо их создать
        if (objs.size()==0) {
            KrnAttribute etType = getAttributeByNameComp(typeCls, "type");
            KrnAttribute etTypem = getAttributeByNameComp(typeCls, "type_M");
            
            KrnObject obj = createObject(typeCls.id, 0, -1, null);
            objs.add(obj);
            setValue(obj, etType.id, 0, 0, 0, "Уведомление", false);
            setValue(obj, etTypem.id, 0, 0, 0, "Уведомление", false);
            
            obj = createObject(typeCls.id, 0, -1, null);
            objs.add(obj);
            setValue(obj, etType.id, 0, 0, 0, "Предупреждение", false);
            setValue(obj, etTypem.id, 0, 0, 0, "Предупреждение", false);
            
            obj = createObject(typeCls.id, 0, -1, null);
            objs.add(obj);
            setValue(obj, etType.id, 0, 0, 0, "Ошибка", false);
            setValue(obj, etTypem.id, 0, 0, 0, "Ошибка", false);
            
            obj = createObject(typeCls.id, 0, -1, null);
            objs.add(obj);
            setValue(obj, etType.id, 0, 0, 0, "Фатальная ошибка", false);
            setValue(obj, etTypem.id, 0, 0, 0, "Фатальная ошибка", false);
        }
        
        // Привести коды событий к единому шаблону.
        Map<Integer, KrnObject> typesByCode = new HashMap<Integer, KrnObject>();
        for (KrnObject type : objs) {
        	String val = (String)getValue(type.id, typeNameAttrId, 0, 0, 0);
        	int code = "Фатальная ошибка".equals(val) ? 4 : "Ошибка".equals(val) ? 3 : "Предупреждение".equals(val) ? 2 : 1;
        	//setValue(type, typeCodeAttrId, 0, 0, 0, code, false);
        	typesByCode.put(code, type);
        }
        
        // Системные события
        for (SystemEvent event : SystemEvent.SYSTEM_EVENTS) {
            List<KrnObject> os = getObjectsByAttribute(eventClsId, codeAttrId, 0, CO_EQUALS, event.getCode(), 0);
            if (os == null || os.size() == 0) {
	        	KrnObject obj = createObject(eventClsId, 0, -1, null);
	        	setValue(obj, codeAttrId, 0, 0, 0, event.getCode(), false);
	        	setValue(obj, nameAttrId, 0, obj_ru.id, 0, event.getName(), false);
	        	setValue(obj, typeAttrId, 0, 0, 0, typesByCode.get(event.getTypeCode()).id, false);
            }
        }
        
        sysCls = getClassByNameComp("Системный класс");

        cls = getClassByNameComp("ProtocolRule");
        if (cls == null) {
            cls = createClass("ProtocolRule", sysCls.id, false, 0, -1, "3e5ca576-c56c-47a8-9874-e8d610191fb7", false, null);
            setClassComment(cls.uid, "Правила протоколирования");
        }

        if (getAttributeByNameComp(cls, "block") == null) {
            attr = createAttribute(-1, "130b9e70-332e-4868-8bf0-ef45d7f4bec6", cls.id, CID_BOOL, "block", 0, false, false, false, false, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Временное блокирование правила протоколирования");
        }
        if (getAttributeByNameComp(cls, "event") == null) {
            attr = createAttribute(-1, "fbc8245c-e606-4e73-8904-8193f590bacd", cls.id, eventClsId, "event", 0, false, false, false, false, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Событие, которое должно протоколироваться");
        }
    	if (getAttributeByNameComp(cls, "eventType") == null) {
    		attr = createAttribute(-1, "eb8f27e6-722b-40a8-af1d-70a75c61d0ba", cls.id, typeCls.id, "eventType", 0, false, true, false, true, 0, 0, 0, 0, false, false, null, 0);
            setAttributeComment(attr.uid, "Тип событий, которые должны протоколироваться");
    	}
        if (getAttributeByNameComp(cls, "expr") == null) {
            attr = createAttribute(-1, "2ae177fa-e623-4628-b7ac-4c53c94c612e", cls.id, CID_BLOB, "expr", 0, false, false, false, false, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Условие срабатывания правила протоколирования");
        }
        if (getAttributeByNameComp(cls, "name") == null) {
            attr = createAttribute(-1, "403f4007-58e7-49cf-9d06-b32464a8a437", cls.id, CID_STRING, "name", 0, false, false, true, false, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Наименование правила протоколирования");
        }
        if (getAttributeByNameComp(cls, "deny") == null) {
            attr = createAttribute(-1, "05bc9e0f-330e-431f-9268-7bec9ab4140d", cls.id, CID_BOOL, "deny", 0, false, false, false, false, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Запрет или разрешение");
        }

        // Справочник доступных операций в системе 
        cls = getClassByNameComp("SystemAction");
        if (cls == null) {
            cls = createClass("SystemAction", sysCls.id, true, 0, -1, "16bb93c2-ba2a-42a8-b64c-c995f3303f24", false, null);
            db.addClass(cls, false);
            setClassComment(cls.uid, "Справочник доступных операций в системе");
        }
        long actionClsId = cls.id;

        if ((attr = getAttributeByNameComp(cls, "code")) == null) {
            attr = createAttribute(-1, "a6e1e743-237f-4545-b873-b552fb50f17e", cls.id, CID_INTEGER, "code", 0, false, true, false, true, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Код операции");
        }
        codeAttrId = attr.id;
        
        if ((attr = getAttributeByNameComp(cls, "name")) == null) {
            attr = createAttribute(-1, "ed722f06-06db-43ed-b56d-22d1c2f477f5", cls.id, CID_STRING, "name", 0, false, false, true, true, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Наименование операции");
        }
        nameAttrId = attr.id;
        
        // Системные операции
        for (SystemAction event : SystemAction.SYSTEM_ACTIONS) {
            List<KrnObject> os = getObjectsByAttribute(actionClsId, codeAttrId, 0, CO_EQUALS, event.getCode(), 0);
            if (os == null || os.size() == 0) {
		    	KrnObject obj = createObject(actionClsId, 0, -1, null);
		    	setValue(obj, codeAttrId, 0, 0, 0, event.getCode(), false);
		    	setValue(obj, nameAttrId, 0, obj_ru.id, 0, event.getName(), false);
            }
        }

        cls = getClassByNameComp("SystemRight");
        if (cls == null) {
            cls = createClass("SystemRight", sysCls.id, false, 0, -1, "905355d9-f2c8-4e27-9010-8c7b2626207d", false, null);
            setClassComment(cls.uid, "Права доступа");
        }
        long rightClsId = cls.id;

        if (getAttributeByNameComp(cls, "block") == null) {
            attr = createAttribute(-1, "247d4c65-92b8-4a1f-a905-9fb06125d629", cls.id, CID_BOOL, "block", 0, false, false, false, false, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Временное блокирование права доступа");
        }
        if (getAttributeByNameComp(cls, "deny") == null) {
            attr = createAttribute(-1, "a6b585c8-9f0c-4f1b-a6f8-ba5f22daa2da", cls.id, CID_BOOL, "deny", 0, false, false, false, false, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Запрет или разрешение");
        }
        if (getAttributeByNameComp(cls, "action") == null) {
            attr = createAttribute(-1, "78ed2ab6-e419-4da4-9645-89d74992d28f", cls.id, actionClsId, "action", 0, false, false, false, false, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Операция в системе");
        }
        if (getAttributeByNameComp(cls, "expr") == null) {
            attr = createAttribute(-1, "4d77d974-6723-422e-824f-122bcbcf818e", cls.id, CID_BLOB, "expr", 0, false, false, false, false, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Условие срабатывания операции");
        }
        if (getAttributeByNameComp(cls, "name") == null) {
            attr = createAttribute(-1, "0627e0c1-453b-467f-b645-fed0de766aca", cls.id, CID_STRING, "name", 0, false, false, true, false, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Наименование права доступа");
        }
        if (getAttributeByNameComp(cls, "description") == null) {
            attr = createAttribute(-1, "c22d7ca7-a916-447a-822f-854a031dd9d7", cls.id, CID_MEMO, "description", 0, false, false, true, false, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Описание права доступа");
        }
        KrnClass userCls = getClassByNameComp("User");
        if (getAttributeByNameComp(cls, "userOrRole") == null) {
            attr = createAttribute(-1, "9ca4cdef-ec54-4492-8f8b-f55371602146", cls.id, userCls.id, "userOrRole", 1, false, false, false, false, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Пользователь или роль");
        }
        if (getAttributeByNameComp(cls, "архив") == null) {
            attr = createAttribute(-1, "4030dbe9-ef35-4e52-ba2d-7ec5fa9f2901", cls.id, getClassByNameComp("HiperTree").id, "архив", 1, false, false, false, false, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Объект операции");
        }
        if (getAttributeByNameComp(cls, "процесс") == null) {
            attr = createAttribute(-1, "ab52e6a9-3e84-4c40-8f4d-38a009aeafd1", cls.id, getClassByNameComp("ProcessDef").id, "процесс", 1, false, false, false, false, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Объект операции");
        }
        if (getAttributeByNameComp(cls, "НСИ") == null) {
            attr = createAttribute(-1, "d34671d2-84b1-4f1d-96be-6a818c361f88", cls.id, getClassByNameComp("HiperTree").id, "НСИ", 1, false, false, false, false, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Объект операции");
        }
        if (getAttributeByNameComp(cls, "пользователь") == null) {
            attr = createAttribute(-1, "e7e43980-c8b9-48c2-ba5a-91df82954da7", cls.id, getClassByNameComp("UI").id, "пользователь", 1, false, false, false, false, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Объект операции");
        }
        if (getAttributeByNameComp(cls, "роль") == null) {
            attr = createAttribute(-1, "804303c2-8310-4ce3-bedc-bdc756c668e3", cls.id, getClassByNameComp("UIFolder").id, "роль", 1, false, false, false, false, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Объект операции");
        }
        long userAttrId = attr.id;
        
        cls = getClassByNameComp("User");
        if (getAttributeByNameComp(userCls, "systemRights") == null) {
            attr = createAttribute(-1, "3bd26832-c107-4f74-bc39-172b0949ee04", userCls.id, rightClsId, "systemRights", 2, false, false, false, false, 0, 0, 0,
                    0, false, false, null, 0);
            setAttributeComment(attr.uid, "Права пользователя или роли");
        }

        KrnClass uiCls = getClassByNameComp("UI");
        KrnClass rootCls = getClassByNameComp("UIRoot");
        KrnObject uiRoot = getObjects(rootCls.id, 0).get(0);
        KrnObject obj = null;
        try {
        	obj = getObjectByUid("1014162.3554407", 0);
        } catch (Exception e) {}
        if (obj == null) {
	    	obj = createObject(uiCls.id, 0, -1, "1014162.3554407");
	    	KrnAttribute a = getAttributeByNameComp(uiCls, "title");
	    	setValue(obj, a.id, 0, obj_ru.id, 0, "Предоставление прав доступа", false);
	    	a = getAttributeByNameComp(uiCls, "parent");
	    	setValue(obj, a.id, 0, 0, 0, uiRoot.id, false);
	    	a = getAttributeByNameComp(uiCls, "config");
	    	try {
	    		byte[] b = Utils.getXMLResource("rights.config");
	    		setValue(obj, a.id, 0, 0, 0, b, false);
	    	} catch (Exception e) {
	    		log.error(e, e);
	    	}
	    	a = getAttributeByNameComp(uiCls, "strings");
	    	try {
	    		setValue(obj, a.id, 0, obj_ru.id, 0, Utils.getXMLResource("rights.ru"), false);
	    		setValue(obj, a.id, 0, obj_kz.id, 0, Utils.getXMLResource("rights.ru"), false);
	    	} catch (Exception e) {
	    		log.error(e, e);
	    	}
        }        
        
        log.info("Апгрейд БД до версии 14 успешно завершен.");
        isUpgrading = false;
    }
    
    protected void upgradeTo15() throws DriverException, SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 15 ...");
        
        KrnClass sysCls = getClassByNameComp("Системный класс");
        
        KrnClass dirCls = getClassByNameComp("FSDirectory"); 
        if (dirCls == null)
        	dirCls = createClass("FSDirectory", sysCls.id, true, 0, -1, "5a589ac6-d573-4147-9b51-11d843d44071", false, null);
        else
        	db.addClass(dirCls, false);

        if ((getAttributeByNameComp(dirCls, "attrId")) == null)
        	createAttribute(-1, "e2ff792c-fdaa-4afa-9419-0479f1ce88b4", dirCls.id, CID_INTEGER, "attrId", 0, false, true, false, true, 0, 0, 0, 0, false, false, null, 0);
        if ((getAttributeByNameComp(dirCls, "name")) == null)
        	createAttribute(-1, "b12089ea-626b-4853-b2b9-ecb25be27a8e", dirCls.id, CID_STRING, "name", 0, false, true, false, true, 0, 0, 0, 0, false, false, null, 0);
        if ((getAttributeByNameComp(dirCls, "url")) == null)
        	createAttribute(-1, "6168007f-a2c9-4992-90e6-eaefa529b55e", dirCls.id, CID_STRING, "url", 0, false, true, false, true, 0, 0, 0, 0, false, false, null, 0);

        log.info("Апгрейд БД до версии 15 успешно завершен.");
        isUpgrading = false;
    }
    
    public void upgradeTo16() throws DriverException, SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 16 ...");

        KrnClass sysCls = getClassByNameComp("Системный класс");
        KrnClass cls = getClassByNameComp("FGACRule"); 
        if (cls == null)
        	cls = createClass("FGACRule", sysCls.id, true, 0, -1, "076c0b22-d69f-4ede-aaa1-c4d65536ae4f", false, null);
        else {
			db.addClass(cls, false);
        }

        if ((getAttributeByNameComp(cls, "name")) == null)
        	createAttribute(-1, "ea9d2f34-34a7-4196-98b3-d415bf1d9d06", cls.id, CID_STRING, "name", 0, false, true, false, true, 0, 0, 0, 0, false, false, null, 0);
        if ((getAttributeByNameComp(cls, "атрибуты")) == null)
        	createAttribute(-1, "b84f8ccd-fa73-4655-bc1a-e82ef4245a6f", cls.id, CID_STRING, "атрибуты", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        if ((getAttributeByNameComp(cls, "дополнительное условие")) == null)
        	createAttribute(-1, "1287f1a4-b168-4e21-a507-88d1fa84aa4a", cls.id, CID_MEMO, "дополнительное условие", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        if ((getAttributeByNameComp(cls, "класс")) == null)
        	createAttribute(-1, "eada00e6-72f6-4f6e-a4e7-3ecfabf8581f", cls.id, CID_STRING, "класс", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        if ((getAttributeByNameComp(cls, "операции")) == null)
        	createAttribute(-1, "44c03232-d662-4d08-bc34-245c0d452f97", cls.id, CID_STRING, "операции", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        if ((getAttributeByNameComp(cls, "заблокировано?")) == null)
        	createAttribute(-1, "6f6bb53e-0616-4667-87a6-e93286d022c9", cls.id, CID_BOOL, "заблокировано?", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);

        log.info("Апгрейд БД до версии 16 успешно завершен.");
        isUpgrading = false;
    }

    public void upgradeTo18() throws DriverException, SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 18 ...");

        KrnClass sysCls = getClassByNameComp("Системный класс");
        KrnClass cls = getClassByNameComp("FGARule"); 
        if (cls == null)
        	cls = createClass("FGARule", sysCls.id, true, 0, -1, "8ed61d45-441f-4081-83ab-14f2c865e965", false, null);
        else {
        	db.addClass(cls, false);
        }
        
        if ((getAttributeByNameComp(cls, "name")) == null)
        	createAttribute(-1, "c8bffe2f-d40b-4434-afde-f507427d1bdb", cls.id, CID_STRING, "name", 0, false, true, false, true, 0, 0, 0, 0, false, false, null, 0);
        if ((getAttributeByNameComp(cls, "атрибуты")) == null)
        	createAttribute(-1, "ed97a55a-6aa5-40ca-90f7-7b00d75fa834", cls.id, CID_STRING, "атрибуты", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        if ((getAttributeByNameComp(cls, "дополнительное условие")) == null)
        	createAttribute(-1, "f10db354-af9e-4711-bf95-d0a34354d644", cls.id, CID_MEMO, "дополнительное условие", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        if ((getAttributeByNameComp(cls, "класс")) == null)
        	createAttribute(-1, "611832ad-4965-4af1-ab9e-9baa250859de", cls.id, CID_STRING, "класс", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        if ((getAttributeByNameComp(cls, "операции")) == null)
        	createAttribute(-1, "403f0054-c9c1-4ffa-b4b5-3114fd9569a7", cls.id, CID_STRING, "операции", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        if ((getAttributeByNameComp(cls, "заблокировано?")) == null)
        	createAttribute(-1, "183a4f07-dc67-4501-a1ef-837da4d46ee4", cls.id, CID_BOOL, "заблокировано?", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);

        log.info("Апгрейд БД до версии 18 успешно завершен.");
        isUpgrading = false;
    }

    public void upgradeTo19() throws DriverException, SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 19 ...");
        KrnClass sysCls = getClassByNameComp("Системный класс");
        KrnClass processDef = getClassByNameComp("ProcessDef");        
        KrnClass reportPrinter = getClassByNameComp("ReportPrinter");        
        KrnClass policy = getClassByNameComp("Политика учетных записей");        
        KrnClass user = getClassByNameComp("User");        
        KrnClass bases = getClassByNameComp("Структура баз");   
        
        if(policy==null) {
            policy = createClass("Политика учетных записей", sysCls.id, false, 0, -1, "d555bb36-7653-4ba0-b958-48ac4660944a", false, null);
        }
        
        db.addClass(sysCls, false);
        db.addClass(processDef, false);
        db.addClass(reportPrinter, false);
        db.addClass(policy, false);
        db.addClass(user, false);
        db.addClass(bases, false);

        // ProcessDef
        if ((getAttributeByNameComp(processDef, "isBtnToolBar")) == null) {
            createAttribute(-1, "905bee6d-45b5-4842-8267-8142f0940ba2", processDef.id, CID_INTEGER, "isBtnToolBar", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(processDef, "icon")) == null) {
            createAttribute(-1, "afdde962-1ee5-489f-b4aa-73c4afae3f6c", processDef.id, CID_BLOB, "icon", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(processDef, "hotKey")) == null) {
            createAttribute(-1, "85151715-11ea-4a46-b89f-8780c3133496", processDef.id, CID_STRING, "hotKey", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }

        // ReportPrinter
        if ((getAttributeByNameComp(reportPrinter, "bases")) == null) {
            createAttribute(-1, "553c8b82-3df3-49fc-974a-01481ea1ee2f", reportPrinter.id, bases.id, "bases", COLLECTION_SET, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }

        // Политика учетных записей
        
       if ((getAttributeByNameComp(policy, "рекомен срок действия пароля")) == null) {
            createAttribute(-1, "57f25397-6c91-4b5d-9c7b-0b389a0f2d44", policy.id, CID_INTEGER, "рекомен срок действия пароля", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "мин длина логина")) == null) {
            createAttribute(-1, "fbea9d86-dee6-4ca8-82a1-2251045ddff6", policy.id, CID_INTEGER, "мин длина логина", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "мин длина пароля")) == null) {
            createAttribute(-1, "f7e7a30e-dc04-4b24-bebc-5a947a1fc88f", policy.id, CID_INTEGER, "мин длина пароля", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "мин длина пароля адм")) == null) {
            createAttribute(-1, "0e591829-ed06-4e07-98c2-b02ad521e4d0", policy.id, CID_INTEGER, "мин длина пароля адм", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "кол не дублир паролей")) == null) {
            createAttribute(-1, "710fae8b-eb3a-430c-8441-70f8c3fff8e3", policy.id, CID_INTEGER, "кол не дублир паролей", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "кол не дублир паролей адм")) == null) {
            createAttribute(-1, "0b793209-c700-4739-985b-55622d2e978d", policy.id, CID_INTEGER, "кол не дублир паролей адм", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "использовать цифры")) == null) {
            createAttribute(-1, "68098d52-8eee-4c3b-9b62-736b4d7e5cfb", policy.id, CID_BOOL, "использовать цифры", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "использовать буквы")) == null) {
            createAttribute(-1, "dca22371-ea73-4412-93e0-cdde7bffb658", policy.id, CID_BOOL, "использовать буквы", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "использовать регистр")) == null) {
            createAttribute(-1, "588393d4-cfb2-4be6-bdbd-31e44f93ea2d", policy.id, CID_BOOL, "использовать регистр", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "использовать спец символы")) == null) {
            createAttribute(-1, "2f4806fe-af23-470f-8b5f-dd05f8fc2989", policy.id, CID_BOOL, "использовать спец символы", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "запрет имён")) == null) {
            createAttribute(-1, "e26a2f7a-5596-4132-91b8-d94b82fb9500", policy.id, CID_BOOL, "запрет имён", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "запрет фамилий")) == null) {
            createAttribute(-1, "3462a5d7-7fe8-4559-bba8-639d6b5c4139", policy.id, CID_BOOL, "запрет фамилий", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "запрет телефонов")) == null) {
            createAttribute(-1, "0d01ff94-41cb-43f4-8ebb-bf2e1a09d9fa", policy.id, CID_BOOL, "запрет телефонов", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "запрет слов")) == null) {
            createAttribute(-1, "57de5f0a-5b6d-4cdc-9c47-53debe1c75de", policy.id, CID_BOOL, "запрет слов", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "макс срок действия пароля")) == null) {
            createAttribute(-1, "7af05001-3c19-4168-a954-a364cbda62d3", policy.id, CID_INTEGER, "макс срок действия пароля", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "мин срок действия пароля")) == null) {
            createAttribute(-1, "6a8224e4-d28f-4188-8ad4-13448963a33c", policy.id, CID_INTEGER, "мин срок действия пароля", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "кол неуд авторизаций")) == null) {
            createAttribute(-1, "8483645b-ef5f-4b97-ba9b-d0e79048ce48", policy.id, CID_INTEGER, "кол неуд авторизаций", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "время блокировки")) == null) {
            createAttribute(-1, "4b4abd3b-221e-4b63-af85-26bf14eae4a8", policy.id, CID_INTEGER, "время блокировки", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "блокировать логин в пароле")) == null) {
            createAttribute(-1, "a3494900-90ec-40a0-a93b-7d9f138281f8", policy.id, CID_BOOL, "блокировать логин в пароле", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "макс длина пароля")) == null) {
            createAttribute(-1, "d25f0553-ad11-45a1-9b75-d79009b3c64e", policy.id, CID_INTEGER, "макс длина пароля", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "макс длина логина")) == null) {
            createAttribute(-1, "54d6b46b-5277-481d-a8e8-98cd71cacc19", policy.id, CID_INTEGER, "макс длина логина", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "смена 1го пароля")) == null) {
            createAttribute(-1, "a0189ad6-aacd-47f1-8eaa-9474cf57b383", policy.id, CID_BOOL, "смена 1го пароля", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "макс срок 1го пароля")) == null) {
            createAttribute(-1, "3e1057bc-3f8a-4760-9887-6705e12f0ce0", policy.id, CID_INTEGER, "макс срок 1го пароля", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(policy, "запрет повтора 1х 3х букв пароля")) == null) {
            createAttribute(-1, "231b4a0e-9085-4c2f-a3c9-98e166b181b4", policy.id, CID_BOOL, "запрет повтора 1х 3х букв пароля", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        
        // User
        if ((getAttributeByNameComp(user, "favoritesClasses")) == null) {
            createAttribute(-1, "06bffdd3-c7d2-4cdf-b4d5-eb34b0aaea51", user.id, CID_INTEGER, "favoritesClasses", COLLECTION_ARRAY, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(user, "lastIndexingConfig")) == null) {
            createAttribute(-1, "6491d3d9-ea7b-45a2-90f6-a71c9d1afb04", user.id, CID_STRING, "lastIndexingConfig", COLLECTION_ARRAY, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }
        if ((getAttributeByNameComp(user, "templates")) == null) {
            createAttribute(-1, "cdc53225-1e52-4550-9c60-09d829dfe39e", user.id, CID_STRING, "templates", COLLECTION_ARRAY, false, false, false, true, 0, 0, 0, 0, false, false, null, 0); 
        }
        if ((getAttributeByNameComp(user, "activated")) == null) {
            createAttribute(-1, "f0bd8e7d-2ce9-4b83-ad4c-4048bf4f221b", user.id, CID_TIME, "activated", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        }

        log.info("Апгрейд БД до версии 19 успешно завершен.");
        isUpgrading = false;
    }
    
    public void upgradeTo20() throws DriverException, SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 20 ...");
        log.info("В апгрейде атрибут notLogin переименован на isLogged и инвертированны его значения");
        db.addClass(getClassByIdComp(99), false);
        KrnClass user = getClassByNameComp("User");
        db.addClass(user, false);
        db.addClass(getClassByIdComp(5), false);
        db.addClass(getClassByNameComp("UserFolder"), false);
        db.addClass(getClassByNameComp("UserRoot"), false);
        db.addClass(getClassByNameComp("Language"), false);
        // переименовать атрибут
        KrnAttribute attr = getAttributeByNameComp(user, "notLogin");
        if (attr != null) {
         // инвертировать значения
            List<KrnObject> objA = getObjects(user.id, new int[1], 0);
            long[] objIds = Funcs.makeObjectIdArray(objA);
            SortedSet<Value> values = getValues(objIds, null, attr.id, 0, 0);
            KrnObject obj;
            for (Value val : values) {
                obj = getObjectById(val.objectId);
                if (obj != null && obj.classId == user.id) {
                    setValue(val.objectId, attr.id, 0, 0, 0, (Boolean) val.value ? new Long(0) : new Long(1), false);
                }
            }

            if (getAttributeByNameComp(user, "isLogged") != null) {
                log.warn("Атрибут 'isLogged' присутсвует в БД! Удаляем атрибут!");
                deleteAttribute(getAttributeByNameComp(user, "isLogged").id);
            }

            changeAttribute(attr.id, attr.typeClassId, "isLogged", attr.collectionType, attr.isUnique, attr.isIndexed,
                    attr.isMultilingual, attr.isRepl, attr.size, attr.flags, attr.rAttrId, attr.sAttrId, attr.sDesc, attr.tname, attr.accessModifierType, attr.isEncrypt);
        }
        log.info("Апгрейд БД до версии 20 успешно завершен.");
        isUpgrading = false;
    }
    
	public void upgradeTo22() throws SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 22 ...");
        log.info("В апгрейде у таблицы t_attrs уникальный индекс изменен с c_tname на (`c_class_id`, `c_tname`)");
		QueryRunner qr = new QueryRunner(true);
		
		String sql = "ALTER TABLE " + getDBPrefix() + "`t_attrs` DROP INDEX `c_tname_UNIQUE`, ADD UNIQUE INDEX `c_tname_UNIQUE` (`c_class_id`, `c_tname`);";
		qr.update(conn, sql);
		
		log.info("Апгрейд БД до версии 22 успешно завершен.");
        isUpgrading = false;
    }

	public void upgradeTo23() throws SQLException, DriverException {
        isUpgrading = true;
        
        log.info("Апгрейд БД до версии 23 ...");
        log.info("Модификация механизма работы с внешними BLOB.");
		
        KrnClass msdocCls = getClassByNameComp("MSDoc");
        KrnClass fsdirCls = getClassByNameComp("FSDirectory");
		db.addClass(msdocCls, false);
		db.addClass(fsdirCls, false);

        KrnAttribute attr = getAttributeByNameComp(msdocCls, "ext_dir");
        if (attr == null)
        	createAttribute(-1, "04030688-339f-4438-8c14-f72de8742559", msdocCls.id, fsdirCls.id, "ext_dir",
        			COLLECTION_NONE, false, false, false, true, 0, 0, 0, 0, false, false, "EXT_DIR", 0);
        
        attr = getAttributeByNameComp(msdocCls, "ext_filename");
        if (attr == null)
        	createAttribute(-1, "0af30c6e-fa16-4a1b-bf62-5887a1998108", msdocCls.id, CID_STRING, "ext_filename",
        			COLLECTION_NONE, false, false, false, true, 0, 0, 0, 0, false, false, "EXT_FILENAME", 0);
        
		log.info("Апгрейд БД до версии 23 успешно завершен.");
		
        isUpgrading = false;
    }
	
	public void upgradeTo24() throws SQLException, DriverException {
        isUpgrading = true;
        
        log.info("Апгрейд БД до версии 24 ...");
        log.info("В таблице t_changes добавлено поле c_object_uid.");
		
        String sql = "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='" + db.getSchemeName() + "' AND table_name='t_changes' AND column_name='c_object_uid'";
		PreparedStatement pst = conn.prepareStatement(sql);
		ResultSet set = pst.executeQuery();
		boolean isExist = false;
		if (set.next()) {
			isExist = set.getInt(1) > 0;
		}
		set.close();
		pst.close();
		
		if (!isExist) {
			QueryRunner qr = new QueryRunner();
			sql = "ALTER TABLE " + getDBPrefix() + "t_changes ADD COLUMN c_object_uid VARCHAR(20) DEFAULT NULL AFTER c_object_id;";
			qr.update(conn, sql);
		}

		log.info("Апгрейд БД до версии 24 успешно завершен.");
		
        isUpgrading = false;
    }
	
	public void upgradeTo25() throws DriverException, SQLException {
        isUpgrading = true;
        
        log.info("Апгрейд БД до версии 25 ...");
        log.info("Создание классов для корзины.");
        List<KrnClass> clss=getAllClasses();
        for(KrnClass cls:clss){
        	db.addClass(cls, true);
        }
        KrnClass systemCls = db.getClassByName("Системный класс");
		KrnClass recycleCls = getClassByNameComp("Recycle");
		if (recycleCls == null) {
			recycleCls = createClass("Recycle", systemCls.id, true, 0, -1, null, false, null);
        	log.info("Создание класса 'Recycle'");
		}
        db.addClass(recycleCls, true);
		
		KrnClass processDefCLs = getClassByNameComp("ProcessDef");
		KrnClass processDefRecycleCLs = getClassByNameComp("ProcessDefRecycle");
		if (processDefRecycleCLs == null) {
			processDefRecycleCLs = createClass("ProcessDefRecycle", recycleCls.id, true, 0, -1, null, false, null);
        	log.info("Создание класса 'ProcessDefRecycle'");
		}
        db.addClass(processDefRecycleCLs, true);
		List<KrnAttribute> attrs = getAttributesByClassIdComp(processDefCLs, true);
		for (int i = 0; i < attrs.size(); i++) {
			KrnAttribute attr = attrs.get(i);
			if (attr.id != 1 && attr.id != 2) {
				if (getAttributeByNameComp(processDefRecycleCLs, attr.name) == null) {
					createAttribute(-1, null, processDefRecycleCLs.id, attr.typeClassId, attr.name, attr.collectionType, attr.isUnique, attr.isIndexed, attr.isMultilingual, attr.isRepl, attr.size, attr.flags, attr.rAttrId, attr.sAttrId, attr.sDesc, false, null, 0);
		        	log.info("Создание атрибута '" + attr.name + "'");
				}
			}
		}
		
		KrnClass uiCLs = getClassByNameComp("UI");
		KrnClass uiRecycleCLs = getClassByNameComp("UIRecycle");
		if (uiRecycleCLs == null) {
			uiRecycleCLs = createClass("UIRecycle", recycleCls.id, true, 0, -1, null, false, null);
        	log.info("Создание класса 'UIRecycle'");
		}
        db.addClass(uiRecycleCLs, true);
		attrs = getAttributesByClassIdComp(uiCLs, true);
		for (int i = 0; i < attrs.size(); i++) {
			KrnAttribute attr = attrs.get(i);
			if (attr.id != 1 && attr.id != 2) {
				if (getAttributeByNameComp(uiRecycleCLs, attr.name) == null) {
					createAttribute(-1, null, uiRecycleCLs.id, attr.typeClassId, attr.name, attr.collectionType, attr.isUnique, attr.isIndexed, attr.isMultilingual, attr.isRepl, attr.size, attr.flags, attr.rAttrId, attr.sAttrId, attr.sDesc, false, null, 0);
		        	log.info("Создание атрибута '" + attr.name + "'");
				}
			}
		}
		
		KrnClass filterCLs = getClassByNameComp("Filter");
		KrnClass filterRecycleCLs = getClassByNameComp("FilterRecycle");
		if (filterRecycleCLs == null) {
			filterRecycleCLs = createClass("FilterRecycle", recycleCls.id, true, 0, -1, null, false, null);
        	log.info("Создание класса 'FilterRecycle'");
		}
        db.addClass(filterRecycleCLs, true);
		attrs = getAttributesByClassIdComp(filterCLs, true);
		for (int i = 0; i < attrs.size(); i++) {
			KrnAttribute attr = attrs.get(i);
			if (attr.id != 1 && attr.id != 2) {
				if (getAttributeByNameComp(filterRecycleCLs, attr.name) == null) {
					createAttribute(-1, null, filterRecycleCLs.id, attr.typeClassId, attr.name, attr.collectionType, attr.isUnique, attr.isIndexed, attr.isMultilingual, attr.isRepl, attr.size, attr.flags, attr.rAttrId, attr.sAttrId, attr.sDesc, false, null, 0);
		        	log.info("Создание атрибута '" + attr.name + "'");
				}
			}
		}
		
		KrnClass reportPrinterCLs = getClassByNameComp("ReportPrinter");
		KrnClass reportPrinterRecycleCLs = getClassByNameComp("ReportPrinterRecycle");
		if (reportPrinterRecycleCLs == null) {
			reportPrinterRecycleCLs = createClass("ReportPrinterRecycle", recycleCls.id, true, 0, -1, null, false, null);
        	log.info("Создание класса 'ReportPrinterRecycle'");
		}
        db.addClass(reportPrinterRecycleCLs, true);
		attrs = getAttributesByClassIdComp(reportPrinterCLs, true);
		for (int i = 0; i < attrs.size(); i++) {
			KrnAttribute attr = attrs.get(i);
			if (attr.id != 1 && attr.id != 2) {
				if (getAttributeByNameComp(reportPrinterRecycleCLs, attr.name) == null) {
					createAttribute(-1, null, reportPrinterRecycleCLs.id, attr.typeClassId, attr.name, attr.collectionType, attr.isUnique, attr.isIndexed, attr.isMultilingual, attr.isRepl, attr.size, attr.flags, attr.rAttrId, attr.sAttrId, attr.sDesc, false, null, 0);
		        	log.info("Создание атрибута '" + attr.name + "'");
				}
			}
		}
        
        List<KrnClass> classes = Arrays.asList(processDefRecycleCLs, uiRecycleCLs, filterRecycleCLs, reportPrinterRecycleCLs);
        List<String> attrsNames = Arrays.asList("uid", "eventDate", "eventInitiator");
		for (int i = 0; i < classes.size(); i++) {
			for (int j = 0; j < attrsNames.size(); j++) {
				if (getAttributeByNameComp(classes.get(i), attrsNames.get(j)) == null) {
					createAttribute(-1, null, classes.get(i).id, CID_STRING, attrsNames.get(j), 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
		        	log.info("Создание атрибута '" + attrsNames.get(j) + "'");
				}
			}
		}
		
		log.info("Апгрейд БД до версии 25 успешно завершен.");
		
        isUpgrading = false;
    }
	
	public void upgradeTo26() throws SQLException, DriverException {
        isUpgrading = true;
        
        log.info("Апгрейд БД до версии 26 ...");
        log.info("В таблице Flow тип поля cutObj заменен на memo");
		//Создаем поле типа мемо
        KrnClass cls=getClassByNameComp("Flow");
        KrnAttribute attr=getAttributeByNameComp(cls, "cutObj");
        if(attr.collectionType>0){
	        String tname = getClassTableName(cls,true);
	        String cname=getColumnName(attr);
	        String cname_=attr.isMultilingual?cname+"_1":cname;
	        String atname=getAttrTableName(attr);
	        atname=atname.replace("'", "");
	        QueryRunner qr = new QueryRunner(true);
	    	String sql = "ALTER TABLE " + tname+" ADD COLUMN "+cname+" MEDIUMTEXT DEFAULT NULL;";
	    	qr.update(conn, sql);
	    	
	    	Statement st = conn.createStatement();
	    	ResultSet rs = st.executeQuery("select c_obj_id from " + tname);
	    	PreparedStatement selPst = conn.prepareStatement("select " + cname_ + " from " + atname + " where c_obj_id=?");
	    	PreparedStatement updPst = conn.prepareStatement("update " + tname + " set " + cname + "=? where c_obj_id=?");
	    	while (rs.next()) {
	    		long objId = rs.getLong(1);
	    		selPst.setLong(1, objId);
	    		StringBuilder str = new StringBuilder();
	    		ResultSet selRs = selPst.executeQuery();
	    		while (selRs.next()) {
	    			String vId = getString(selRs, 1);
	    			if (!selRs.wasNull()) {
	    				if (str.length() > 0)
	    					str.append(';');
	    				str.append(vId);
	    			}
	    		}
	    		selRs.close();
	    		updPst.setString(1, str.toString());
	    		updPst.setLong(2, objId);
	    		updPst.executeUpdate();
	    	}
	    	rs.close();
	    	st.close();
	    	selPst.close();
	    	updPst.close();
	    	/*
	    	//Переписываем в него содержимое множественного атрибута
	    	sql="UPDATE "+tname+" t1 "
	    			+"SET "+cname+" = (SELECT GROUP_CONCAT(t2."+cname_+" SEPARATOR ';') "
	    			+"FROM "+atname+" t2 where t1.c_obj_id=t2.c_obj_id "
	    			+"AND t2."+cname_+" is not null GROUP BY t2.c_obj_id) "
	    			+"WHERE EXISTS (SELECT 1  FROM "+atname+" t2 WHERE t1.c_obj_id = t2.c_obj_id)";
	    	qr.update(conn, sql);
	
*/	    	//Удаляем таблицу относящуюся к множественному атрибуту, а его делаем одиночным и типом мемо
	    	sql = "DROP TABLE " + atname+";";
	//    	qr.update(conn, sql);
	    	sql = "UPDATE " + getDBPrefix() + "t_attrs SET c_type_id = 6, c_col_type = 0, c_is_multilingual = 0 WHERE c_id="+attr.id+";";
	    	qr.update(conn, sql);
	    	db.removeAttribute(attr);
	        attr=getAttributeByNameComp(cls, "cutObj");
        }
    	log.info("Апгрейд БД до версии 26 успешно завершен.");
		
        isUpgrading = false;
    }
	
	@Override
	public List<TriggerInfo> getTriggers(KrnClass cls) {
		List<TriggerInfo> list = new ArrayList<TriggerInfo>();
		PreparedStatement pst = null;
		ResultSet rst = null;
		try {
			pst = conn.prepareStatement(
					"SELECT TRIGGER_NAME, ACTION_TIMING, EVENT_MANIPULATION, ACTION_STATEMENT"
					+ " FROM INFORMATION_SCHEMA.TRIGGERS"
					+ " WHERE EVENT_OBJECT_TABLE=? AND EVENT_OBJECT_SCHEMA=?"
					+ " ORDER BY TRIGGER_NAME;");
			pst.setString(1, getClassTableName(cls.id));
			pst.setString(2, db.getSchemeName());
			rst = pst.executeQuery();
			while (rst.next()) {
				String name = getString(rst, 1);
				String description = getString(rst, 2) + " " + getString(rst, 3);;
				String body = getString(rst, 4);
				list.add(new TriggerInfo(name, description, body));
			}
		} catch (SQLException e) {
			log.error(e.getMessage());
		} finally {
			DbUtils.closeQuietly(rst);
			DbUtils.closeQuietly(pst);
		}
		return list;
	}
	
	@Override
	public String createTrigger(String triggerContext) {
		String message = "Success";
		Statement statement = null;
		try {
			statement = conn.createStatement();
			statement.executeUpdate(triggerContext);
		} catch (SQLException e) {
			log.error(e.getMessage());
			message = e.getMessage();
		} finally {
			DbUtils.closeQuietly(statement);
		}
		return message;
	}
	
	@Override
	public String removeTrigger(String triggerName) {
		String message = "Success";
		Statement statement = null;
		try {
			statement = conn.createStatement();
			String sql = "DROP TRIGGER IF EXISTS " + triggerName + ";";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			log.error(e.getMessage());
			message = e.getMessage();
		} finally {
			DbUtils.closeQuietly(statement);
		}
		return message;
	}
	
	public void upgradeTo27() throws DriverException, SQLException {
        isUpgrading = true;
        
        log.info("Апгрейд БД до версии 27 ...");
        log.info("Изменение UID-ов классов и атрибутов корзины.");
		
        KrnClass recycleCls = getClassByNameComp("Recycle");
        changeClassUID(recycleCls, "99b3d599-1487-41d8-b001-b7233ac12221");

        
        KrnClass processDefRecycleCLs = getClassByNameComp("ProcessDefRecycle");
    	changeClassUID(processDefRecycleCLs, "afd8673c-c8aa-466b-8510-3aed569ff77b");
		
		KrnAttribute attr = getAttributeByNameComp(processDefRecycleCLs, "config");
		changeAttributeUID(attr, "564f104a-c293-4c09-a86e-2759230f9b08");
		
		attr = getAttributeByNameComp(processDefRecycleCLs, "diagram");
		changeAttributeUID(attr, "e7f3b34c-9550-423d-b0e2-f55c4fd81115");
		
		attr = getAttributeByNameComp(processDefRecycleCLs, "eventDate");
		changeAttributeUID(attr, "8ae3d09b-195b-4082-a366-23d66656495b");

		attr = getAttributeByNameComp(processDefRecycleCLs, "eventInitiator");
		changeAttributeUID(attr, "1cc13bd1-da81-44e1-9fcd-c0ddd7b4efe8");

		attr = getAttributeByNameComp(processDefRecycleCLs, "filters");
		changeAttributeUID(attr, "06b11a4f-009f-4b6a-bc5e-675a4e7d6563");

		attr = getAttributeByNameComp(processDefRecycleCLs, "hotKey");
		changeAttributeUID(attr, "b47c726b-4c36-4293-b68b-35d9662855aa");

		attr = getAttributeByNameComp(processDefRecycleCLs, "icon");
		changeAttributeUID(attr, "88471297-cd2a-4553-aff0-05b4f6c382c8");

		attr = getAttributeByNameComp(processDefRecycleCLs, "isBtnToolBar");
		changeAttributeUID(attr, "dd21a61c-1799-4091-9916-88dd03681aa9");
		
		attr = getAttributeByNameComp(processDefRecycleCLs, "message");
		changeAttributeUID(attr, "e82d2477-93d8-496d-9154-0ba651ce6f6c");

		attr = getAttributeByNameComp(processDefRecycleCLs, "parent");
		changeAttributeUID(attr, "b5a566bc-6d3b-4492-a32f-2d211ded0221");

		attr = getAttributeByNameComp(processDefRecycleCLs, "runtimeIndex");
		changeAttributeUID(attr, "e0891f9e-2143-4a8c-9f03-40ec21fe1dd9");

		attr = getAttributeByNameComp(processDefRecycleCLs, "strings");
		changeAttributeUID(attr, "6f70eef4-8bc7-43f7-aaa6-3769ac768e4e");

		attr = getAttributeByNameComp(processDefRecycleCLs, "test");
		changeAttributeUID(attr, "027f8d2c-17fc-4360-a604-81b33ac5f495");

		attr = getAttributeByNameComp(processDefRecycleCLs, "title");
		changeAttributeUID(attr, "81495d52-cfcd-483b-a841-84a87ea17e25");

		attr = getAttributeByNameComp(processDefRecycleCLs, "uid");
		changeAttributeUID(attr, "a1dbb412-4ad8-466c-be4d-71e6864cbc64");

		attr = getAttributeByNameComp(processDefRecycleCLs, "баланс_ед");
		changeAttributeUID(attr, "314225ca-53e7-403d-9c38-6f0f0e3523b8");

		attr = getAttributeByNameComp(processDefRecycleCLs, "подсистема");
		changeAttributeUID(attr, "200ac6f3-e639-432d-94d2-b293f3be0f7c");
	    
		
		KrnClass uiRecycleCLs = getClassByNameComp("UIRecycle");
	    changeClassUID(uiRecycleCLs, "86311b67-3aec-4f66-a187-769cb0bbe739");

		attr = getAttributeByNameComp(uiRecycleCLs, "config");
		changeAttributeUID(attr, "0a71bd59-66ec-410d-ba8e-e151f7cb2a34");

		attr = getAttributeByNameComp(uiRecycleCLs, "eventDate");
		changeAttributeUID(attr, "e8b0637f-bb77-4f31-9885-923633b12e01");

		attr = getAttributeByNameComp(uiRecycleCLs, "eventInitiator");
		changeAttributeUID(attr, "49f35114-948f-4a4a-9472-e8d54120434f");

		attr = getAttributeByNameComp(uiRecycleCLs, "filtersFolder");
		changeAttributeUID(attr, "b8b4d65d-837f-47ba-ab5f-14804b511ad7");

		attr = getAttributeByNameComp(uiRecycleCLs, "parent");
		changeAttributeUID(attr, "c1a5b255-723a-447e-9a75-000d3348a062");

		attr = getAttributeByNameComp(uiRecycleCLs, "strings");
		changeAttributeUID(attr, "90e7fa1b-3e8d-471d-8c63-03bf482e6d64");

		attr = getAttributeByNameComp(uiRecycleCLs, "title");
		changeAttributeUID(attr, "f053debe-6f36-4700-b2c5-ea68d25801b9");

		attr = getAttributeByNameComp(uiRecycleCLs, "uid");
		changeAttributeUID(attr, "9d1ef772-9879-47f0-959f-58878179c111");

		attr = getAttributeByNameComp(uiRecycleCLs, "webConfig");
		changeAttributeUID(attr, "e8920c01-6985-4ceb-a0dd-0b3604c16fc5");

		attr = getAttributeByNameComp(uiRecycleCLs, "webConfigChanged");
		changeAttributeUID(attr, "674b9a76-3d0b-42ff-9c93-b1c52038eb53");

		attr = getAttributeByNameComp(uiRecycleCLs, "подсистема");
		changeAttributeUID(attr, "22910bc1-967b-4631-8c6e-767b061f490e");
		
		
		KrnClass filterRecycleCLs = getClassByNameComp("FilterRecycle");
	    changeClassUID(filterRecycleCLs, "1470bc49-1bd8-4a4f-9990-418b5128d764");

		attr = getAttributeByNameComp(filterRecycleCLs, "className");
		changeAttributeUID(attr, "29efe30f-07e1-40ef-9b34-d7f84b6ef32b");

		attr = getAttributeByNameComp(filterRecycleCLs, "config");
		changeAttributeUID(attr, "a82255cd-25d5-4a76-a7b2-9e484811bfb7");

		attr = getAttributeByNameComp(filterRecycleCLs, "dateSelect");
		changeAttributeUID(attr, "7ccb9d7d-3122-44cf-a695-549ba5f83fd0");

		attr = getAttributeByNameComp(filterRecycleCLs, "eventDate");
		changeAttributeUID(attr, "f82a5d96-06a8-4bbd-bbcb-e11754e21c99");

		attr = getAttributeByNameComp(filterRecycleCLs, "eventInitiator");
		changeAttributeUID(attr, "9c974244-1daa-4f43-b1ef-ab4790a4f616");

		attr = getAttributeByNameComp(filterRecycleCLs, "exprSql");
		changeAttributeUID(attr, "637b47b4-d8e9-4e16-af86-ab9c4eeadc66");

		attr = getAttributeByNameComp(filterRecycleCLs, "parent");
		changeAttributeUID(attr, "d57cb406-0b3d-4659-bc27-301f12a5761a");
		
		attr = getAttributeByNameComp(filterRecycleCLs, "title");
		changeAttributeUID(attr, "e9a28773-6d99-4587-aa99-231e6a47455a");
		
		attr = getAttributeByNameComp(filterRecycleCLs, "uid");
		changeAttributeUID(attr, "76dec9ec-1dd2-4c2f-ab2e-8aa32e5d48a6");
		
		attr = getAttributeByNameComp(filterRecycleCLs, "подсистема");
		changeAttributeUID(attr, "4f9a8e6c-c2d1-4f88-8038-ad26f7e7cf46");
		
		
		KrnClass reportPrinterRecycleCLs = getClassByNameComp("ReportPrinterRecycle");
	    changeClassUID(reportPrinterRecycleCLs, "37597a9b-493b-4731-b6ad-b17049944905");

	    attr = getAttributeByNameComp(reportPrinterRecycleCLs, "bases");
		changeAttributeUID(attr, "875e5442-5b94-45db-a68a-799b57d1d967");
		
		attr = getAttributeByNameComp(reportPrinterRecycleCLs, "config");
		changeAttributeUID(attr, "9aa1b339-bf03-4a94-8a10-48036acc6cfc");
		
		attr = getAttributeByNameComp(reportPrinterRecycleCLs, "constraints");
		changeAttributeUID(attr, "171414aa-93a6-403a-a960-7630ea2c224c");
		
		attr = getAttributeByNameComp(reportPrinterRecycleCLs, "data");
		changeAttributeUID(attr, "1616036d-01fc-4f7d-8d14-567e363f61f9");
		
		attr = getAttributeByNameComp(reportPrinterRecycleCLs, "data2");
		changeAttributeUID(attr, "adeec872-ed51-4730-9b92-5f7185e50ef9");
		
		attr = getAttributeByNameComp(reportPrinterRecycleCLs, "descInfo");
		changeAttributeUID(attr, "8e556483-1268-4304-8471-3b98c7dc5d65");
		
		attr = getAttributeByNameComp(reportPrinterRecycleCLs, "eventDate");
		changeAttributeUID(attr, "263c5e3d-579b-42d6-a50b-24e31d0460c4");
		
		attr = getAttributeByNameComp(reportPrinterRecycleCLs, "eventInitiator");
		changeAttributeUID(attr, "cd006319-3e4a-4801-848b-574e22f6abf0");
		
		attr = getAttributeByNameComp(reportPrinterRecycleCLs, "flags");
		changeAttributeUID(attr, "a644aebe-64db-42cb-b98a-45c317d51188");
		
		attr = getAttributeByNameComp(reportPrinterRecycleCLs, "parent");
		changeAttributeUID(attr, "717dc9bc-bdd2-43e7-a483-c0ab2c3bf13f");
		
		attr = getAttributeByNameComp(reportPrinterRecycleCLs, "ref");
		changeAttributeUID(attr, "f10e9647-d665-4cf5-8855-eb4e135c8a5f");
		
		attr = getAttributeByNameComp(reportPrinterRecycleCLs, "template");
		changeAttributeUID(attr, "7a887d5e-ae05-43f8-bcee-8281d7737d81");
		
		attr = getAttributeByNameComp(reportPrinterRecycleCLs, "template2");
		changeAttributeUID(attr, "787f98bb-ff6f-49a5-8f0d-c0fe0cd60ea9");
		
		attr = getAttributeByNameComp(reportPrinterRecycleCLs, "title");
		changeAttributeUID(attr, "ed21cc5f-743c-4480-a749-40ee9348d508");
		
		attr = getAttributeByNameComp(reportPrinterRecycleCLs, "uid");
		changeAttributeUID(attr, "c26f1029-fff7-488f-88d3-2c396fc3f308");
		
		attr = getAttributeByNameComp(reportPrinterRecycleCLs, "базовый отчет");
		changeAttributeUID(attr, "b3a78377-7eb4-429a-9e52-5ba39cb97d3c");

		
		log.info("Апгрейд БД до версии 27 успешно завершен.");
		
        isUpgrading = false;
    }
	
	private void changeClassUID(KrnClass cls, String clsUID) {
		if (cls != null) {
			try {
				PreparedStatement updPst = conn.prepareStatement("update t_classes set c_cuid=? where c_id=?");
				updPst.setString(1, clsUID);
				updPst.setLong(2, cls.id);
				updPst.executeUpdate();
				updPst.close();
				log.info("Замена UID-а класса '" + cls.name + "'.");
			} catch (SQLException e) {
				log.error(e, e);
			}
		}
	}
	
	private void changeAttributeUID(KrnAttribute attr, String attrUID) {
		if (attr != null) {
			try {
				PreparedStatement updPst = conn.prepareStatement("update t_attrs set c_auid=? where c_id=?");
				updPst.setString(1, attrUID);
				updPst.setLong(2, attr.id);
				updPst.executeUpdate();
				updPst.close();
				log.info("Замена UID-а атрибута '" + attr.name + "'.");
			} catch (SQLException e) {
				log.error(e, e);
			}
		}
	}
	
	public void upgradeTo28() throws DriverException, SQLException {
        isUpgrading = true;
        
        log.info("Апгрейд БД до версии 28 ...");
        log.info("Добавление полей c_before_event_expr (событие до сохранения) и c_after_event_expr (событие после сохранения) в таблицу t_attrs.");
  
		QueryRunner qr = new QueryRunner(true);
		try {
			String sql = "ALTER TABLE " + getDBPrefix() + "t_attrs ADD COLUMN (c_before_event_expr LONGBLOB DEFAULT NULL, c_after_event_expr LONGBLOB DEFAULT NULL);";
			qr.update(conn, sql);
		} catch (SQLException e) {
			log.error("Поля c_before_event_expr (событие до сохранения) и c_after_event_expr (событие после сохранения) уже существуют!");
		}
		
		log.info("Апгрейд БД до версии 28 успешно завершен.");
		
        isUpgrading = false;
    }

	public void upgradeTo29() throws DriverException, SQLException {
        isUpgrading = true;
        
        log.info("Апгрейд БД до версии 29 ...");
        log.info("Добавление атрибутов в классы Flow и Process.");
        List<KrnClass> clss=getAllClasses();
        for(KrnClass cls:clss){
        	db.addClass(cls, true);
        }
        KrnClass fcls=db.getClassByName("Flow");
        KrnClass pcls=db.getClassByName("Process");
        db.addAttribute(getAttributeByNameComp(fcls, "node"),true);
        KrnAttribute newAttr = getAttributeByNameComp(fcls, "corelId");
        if(newAttr==null){
    		createAttribute(-1, null, fcls.id, CID_STRING, "corelId", 0, false, false, false, true, 0, 0, 0, 0, false, false, "CORELID", 0);
        	log.info("Создание атрибута 'corelId' для класса Flow");
        }

        newAttr = getAttributeByNameComp(fcls, "typeUi");
        if(newAttr==null){
        	createAttribute(-1, null, fcls.id, CID_STRING, "typeUi", 0, false, false, false, true, 0, 0, 0, 0, false, false, "TYPEUI", 0);
        	log.info("Создание атрибута 'typeUi' для класса Flow");
        }
        newAttr = getAttributeByNameComp(pcls, "observers");
        if(newAttr==null){
        	createAttribute(-1, null, pcls.id, CID_STRING, "observers", 0, false, false, false, true, 0, 0, 0, 0, false, false, "OBSERVERS", 0);
        	log.info("Создание атрибута 'observers' для класса Process");
        }
        newAttr = getAttributeByNameComp(pcls, "uiObservers");
        if(newAttr==null){
        	createAttribute(-1, null, pcls.id, CID_STRING, "uiObservers", 0, false, false, false, true, 0, 0, 0, 0, false, false, "UIOBSERVERS", 0);
        	log.info("Создание атрибута 'uiObservers' для класса Process");
        }
        newAttr = getAttributeByNameComp(pcls, "typeUiObservers");
        if(newAttr==null){
        	createAttribute(-1, null, pcls.id, CID_STRING, "typeUiObservers", 0, false, false, false, true, 0, 0, 0, 0, false, false, "TYPEUIOBSERVERS", 0);
        	log.info("Создание атрибута 'typeUiObservers' для класса Process");
        }

    	log.info("замена поля node на тип поля одиночный, ноды перечисляются через точку с запятой");
        KrnAttribute attr=getAttributeByNameComp(fcls, "node");
        String tname = getClassTableName(fcls, true);
        if(attr.collectionType>0){
	        String cname = getColumnName(attr);
	        String atname = getAttrTableName(attr);
	        QueryRunner qr = new QueryRunner(true);
	        try {
	        	String sql = "ALTER TABLE " + tname+" ADD COLUMN "+cname+" TEXT DEFAULT NULL;";
	        	qr.update(conn, sql);
	        } catch (SQLException e) {
				log.error("Поле " + cname + " в таблице " + tname + " уже существует!");
		        try {
		        	log.info("Смена типа полня на ТЕКСТ");
		        	String sql = "ALTER TABLE " + tname+" MODIFY "+cname+" TEXT DEFAULT NULL;";
		        	qr.update(conn, sql);
		        	log.info("Смена типа полня на ТЕКСТ - ОК!");
		        } catch (SQLException ex) {
		        	log.error(ex);
		        }
		    }
	    	Statement st = conn.createStatement();
	    	ResultSet rs = st.executeQuery("select c_obj_id from " + tname);
	    	PreparedStatement selPst = conn.prepareStatement("select distinct " + cname + ",c_index from " + atname + " where c_obj_id=? order by c_index");
	    	PreparedStatement updPst = conn.prepareStatement("update " + tname + " set " + cname + "=? where c_obj_id=?");
	    	while (rs.next()) {
	    		long objId = rs.getLong(1);
	    		selPst.setLong(1, objId);
	    		StringBuilder str = new StringBuilder();
	    		ResultSet selRs = selPst.executeQuery();
	    		while (selRs.next()) {
	    			String vId = getString(selRs, 1);
	    			if(vId.length()==0) continue;
	    			if (!selRs.wasNull()) {
	    				if (str.length() > 0)
	    					str.append(';');
	    				str.append(vId);
	    			}
	    		}
	    		selRs.close();
	    		updPst.setString(1, str.toString());
	    		updPst.setLong(2, objId);
	    		updPst.executeUpdate();
	    	}
	    	rs.close();
	    	st.close();
	    	selPst.close();
	    	updPst.close();
	    	//Удаляем таблицу относящуюся к множественному атрибуту, а его делаем одиночным
	    	String sql = "DROP TABLE " + atname;
//	    	qr.update(conn, sql);
	    	sql = "UPDATE " + getDBPrefix() + "t_attrs SET c_type_id = 6, c_col_type = 0, c_is_multilingual = 0 WHERE c_id="+attr.id;
	    	qr.update(conn, sql);
	    	db.removeAttribute(attr);
	        attr = getAttributeByNameComp(fcls, "node");
        }
    	//заменяем все uids на шаблон для KrnObject для интерфейса
        KrnAttribute ui_attr=getAttributeByNameComp(fcls, "ui");
        if (ui_attr.typeClassId == CID_STRING) {
	        String cname_ui = getColumnName(ui_attr);
	        String cname_ui_=ui_attr.isMultilingual?cname_ui+"_1":cname_ui;
	        Statement st = conn.createStatement();
	    	ResultSet rs = st.executeQuery("select c_obj_id,"+cname_ui_+" from " + tname+" where "+cname_ui_+" is not null" );
	    	PreparedStatement updPst = conn.prepareStatement("update " + tname + " set " + cname_ui_ + "=? where c_obj_id=?");
	    	while (rs.next()) {
	    		long objId = rs.getLong(1);
	    		String ui= getString(rs, 2);
	    		if(!"".equals(ui)){
	    			try{
		        		KrnObject uiObj=getObjectByUid(ui, 0);
		    			String ui_res = ""+uiObj.id+","+uiObj.uid+","+uiObj.classId;
		        		updPst.setString(1, ui_res);
		        		updPst.setLong(2, objId);
		        		updPst.executeUpdate();
	    			}catch(DriverException de){
	    				log.info(de.getMessage());
	    			}
	    		}
	    	}
	    	rs.close();
	    	st.close();
	    	updPst.close();
        } else {
	        String cname_ui = getColumnName(ui_attr);
	        
	        Statement st = conn.createStatement();

			String fkName = getAttrFKName(ui_attr.id);
			// удаляем FK
			st.executeUpdate("ALTER TABLE " + tname + " DROP FOREIGN KEY " + fkName);

			String sql = "ALTER TABLE "+tname+" CHANGE COLUMN "+cname_ui+" "+cname_ui+"1 " + getSqlTypeName(ui_attr);
			log.info("SQL Query...\n " + sql);
			st.executeUpdate(sql);

            //Создаем новую колонку с прежним именем и с новым типом
            sql = "ALTER TABLE "+tname+" ADD "+cname_ui+" "+getSqlTypeName(PC_STRING, 0);
            st.executeUpdate(sql);
	        
	    	ResultSet rs = st.executeQuery("select c_obj_id,"+cname_ui+" from " + tname+" where "+cname_ui+"1 is not null" );
	    	PreparedStatement updPst = conn.prepareStatement("update " + tname + " set " + cname_ui + "=? where c_obj_id=?");
	    	while (rs.next()) {
	    		long objId = rs.getLong(1);
	    		long id= rs.getLong(2);
	    		if(id > 0){
	    			try{
		        		KrnObject uiObj = getObjectById(id);
		    			String ui_res = ""+uiObj.id+","+uiObj.uid+","+uiObj.classId;
		        		updPst.setString(1, ui_res);
		        		updPst.setLong(2, objId);
		        		updPst.executeUpdate();
	    			}catch(DriverException de){
	    				log.info(de.getMessage());
	    			}
	    		}
	    	}
	    	rs.close();
	    	updPst.close();

	    	//Удаляем старую колонку
            sql = "ALTER TABLE "+tname+" DROP COLUMN " + cname_ui + "1";
            st.executeUpdate(sql);

	    	sql = "UPDATE " + getDBPrefix() + "t_attrs SET c_type_id = " + PC_STRING + ", c_is_indexed = 0 WHERE c_id="+ui_attr.id+";";
            st.executeUpdate(sql);
	        st.close();
	    	
            db.removeAttribute(ui_attr);
            ui_attr = getAttributeByNameComp(fcls, "ui");
        }
    	
    	//заменяем все uids на шаблон для KrnObject для объектов обработки
        KrnAttribute objs_attr=getAttributeByNameComp(fcls, "cutObj");
        String cname_objs = getColumnName(objs_attr);
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("select c_obj_id,"+cname_objs+" from " + tname+" where "+cname_objs+" is not null" );
        PreparedStatement updPst = conn.prepareStatement("update " + tname + " set " + cname_objs + "=? where c_obj_id=?");
    	while (rs.next()) {
    		long objId = rs.getLong(1);
    		String objs= getString(rs, 2);
    		String objs_res="";
    		if(!"".equals(objs)){
				String objs_[]= objs.split(";");
				for(String obj:objs_){
	    			try{
	    				KrnObject cutObj=getObjectByUid(obj, 0);
	    				if(!"".equals(objs_res))
	    					objs_res += ";";
	    				objs_res += ""+cutObj.id+","+cutObj.uid+","+cutObj.classId;
	    			}catch(DriverException de){
	    				log.info(de.getMessage());
	    			}
				}
        		updPst.setString(1, objs_res);
        		updPst.setLong(2, objId);
        		updPst.executeUpdate();
    		}
    	}
    	rs.close();
    	st.close();
    	updPst.close();
		log.info("Апгрейд БД до версии 29 успешно завершен.");
       isUpgrading = false;
    }
	
	public void upgradeTo30() throws DriverException, SQLException {
        isUpgrading = true;
        
        log.info("Апгрейд БД до версии 30 ...");
        log.info("Создание атрибута theme в классе User");
  
        KrnClass UserCls = getClassByNameComp("User");
        db.addClass(UserCls, true);
        KrnAttribute attr = getAttributeByNameComp(UserCls, "theme");
	    if (attr == null) {
	        createAttribute(-1, "9def75ac-ca10-4130-9161-ed3df7d9417c", UserCls.id, CID_INTEGER, "theme", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        log.info("Успешное создание атрибута theme в классе User");
	    }
		
		log.info("Апгрейд БД до версии 30 успешно завершен.");
		
        isUpgrading = false;
    }

	/**
	 * Модификация системных таблиц для поддержки версионности объектов проектирования.
	 * @throws DriverException
	 * @throws SQLException
	 */
	public void upgradeTo31() throws DriverException, SQLException {
        isUpgrading = true;
        
        log.info("Апгрейд БД до версии 31 ...");
  
		Statement st = conn.createStatement();
		try {
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_changescls ADD (" +
					"c_time TIMESTAMP," +
					"c_user_id BIGINT DEFAULT NULL," +
					"c_ip VARCHAR(15) DEFAULT NULL)"
					);
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_changescls MODIFY " +
					"c_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
					);
			
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_changes ADD (" +
					"c_time TIMESTAMP," +
					"c_user_id BIGINT DEFAULT NULL," +
					"c_ip VARCHAR(15) DEFAULT NULL)"
					);
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_changes MODIFY " +
					"c_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
					);
			
			st.executeUpdate(
					"CREATE TABLE " + getDBPrefix() + "t_vcs_objects ("
					+ "c_id BIGINT NOT NULL AUTO_INCREMENT,"
					+ "c_obj_id BIGINT NOT NULL,"
					+ "c_obj_uid CHAR(20) NOT NULL,"
					+ "c_obj_class_id BIGINT NOT NULL,"
					+ "c_attr_id BIGINT NOT NULL,"
					+ "c_lang_id BIGINT NOT NULL,"
					+ "c_old_value LONGBLOB,"
					+ "c_user_id BIGINT NOT NULL,"
					+ "c_ip VARCHAR(15) NOT NULL,"
					+ "c_mod_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ "c_mod_last_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ "c_fix_start_id BIGINT,"
					+ "c_fix_end_id BIGINT,"
					+ "c_fix_comment TEXT,"
					+ "PRIMARY KEY PK_VCS (c_id),"
					+ "UNIQUE INDEX IDX_VCS_OBJ_ATTR_LANG (c_obj_id,c_attr_id,c_lang_id,c_fix_end_id)"
					+ ")");

			st.executeUpdate(
					"CREATE TABLE " + getDBPrefix() + "t_vcs_model ("
					+ "c_id BIGINT NOT NULL AUTO_INCREMENT,"
					+ "c_entity_id CHAR(36) NOT NULL,"
					+ "c_type INTEGER NOT NULL,"
					+ "c_action INTEGER NOT NULL,"
					+ "c_old_value LONGBLOB,"
					+ "c_user_id BIGINT NOT NULL,"
					+ "c_ip VARCHAR(15) NOT NULL,"
					+ "c_mod_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ "c_mod_last_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ "c_fix_start_id BIGINT,"
					+ "c_fix_end_id BIGINT,"
					+ "c_fix_comment TEXT,"
					+ "PRIMARY KEY PK_VCS_MODEL (c_id),"
					+ "INDEX IDX_VCS_MODEL_ENTITY_TYPE_ACTION (c_entity_id,c_type,c_action,c_fix_end_id)"
					+ ")");

	        log.info("Апгрейд БД до версии 31 успешно завершен!");
		} finally {
			st.close();
		}
		
        isUpgrading = false;
    }
	
	/**
	 * Модификация системных таблиц для поддержки версионности объектов проектирования, добалена колонка c_dif.
	 * @throws DriverException
	 * @throws SQLException
	 */
	public void upgradeTo32() throws DriverException, SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 32 ...");
		Statement st = conn.createStatement();
		try {
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_dif TEXT");
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_dif TEXT");
			
	        log.info("Апгрейд БД до версии 32 успешно завершен!");
		} finally {
			st.close();
		}
		
        isUpgrading = false;
    }
	
	public void upgradeTo33() throws DriverException, SQLException {
        isUpgrading = true;
        
        log.info("Апгрейд БД до версии 33 ...");
  
		Statement st = conn.createStatement();
		try {
		
			st.executeUpdate(
					"CREATE TABLE " + getDBPrefix() + "t_syslog ("
					+ "c_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ "c_logger VARCHAR(50) NOT NULL,"
					+ "c_type NVARCHAR(20) NOT NULL,"
					+ "c_action NVARCHAR(50) NOT NULL,"
					+ "c_user NVARCHAR(20) NOT NULL,"
					+ "c_ip  VARCHAR(15) NOT NULL,"
					+ "c_host VARCHAR(20) NOT NULL,"
					+ "c_admin BIT NOT NULL,"
					+ "c_message TEXT"
					+ ")");
			st.executeUpdate("CREATE INDEX sl_time_user_idx"
					+ " ON "+getDBPrefix()+"t_syslog(c_time,c_user)");
	        log.info("Апгрейд БД до версии 33 успешно завершен!");
		} finally {
			st.close();
		}
		
        isUpgrading = false;
    }
	/**
	 * Модификация системных таблиц для поддержки версионности объектов проектирования, добалена колонка c_name.
	 * @throws DriverException
	 * @throws SQLException
	 */
	public void upgradeTo34() throws DriverException, SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 34 ...");
		Statement st = conn.createStatement();
		try {
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_name VARCHAR(255)");
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_name VARCHAR(255)");
			
	        log.info("Апгрейд БД до версии 34 успешно завершен!");
		} finally {
			st.close();
		}
		
        isUpgrading = false;
    }
	public void upgradeTo35() throws DriverException, SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 35 ...");
		Statement st = conn.createStatement();
		try {
			// Создаем таблицу блокировок для методов
			createLockMethodsTable(conn);
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_repl_id BIGINT");
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects MODIFY c_dif MEDIUMTEXT");
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_repl_id BIGINT");
			log.info("Апгрейд БД до версии 35 успешно завершен!");
		} finally {
			st.close();
		}
        isUpgrading = false;
	}

	public void upgradeTo36() throws DriverException, SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 36 ...");
		Statement st = conn.createStatement();
		try {
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects DROP INDEX IDX_VCS_OBJ_ATTR_LANG");
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model DROP INDEX IDX_VCS_MODEL_ENTITY_TYPE_ACTION");
			st.executeUpdate("CREATE UNIQUE INDEX IDX_VCS_OBJ_ATTR_LANG_REPL"
					+ " ON "+getDBPrefix()+"t_vcs_objects(c_obj_id,c_attr_id,c_lang_id,c_fix_end_id,c_repl_id)");

			st.executeUpdate("CREATE INDEX IDX_VCS_MODEL_ENTITY_TYPE_ACTION_REPL"
					+ " ON "+getDBPrefix()+"t_vcs_model(c_entity_id,c_type,c_action,c_fix_end_id,c_repl_id)");
			log.info("Апгрейд БД до версии 36 успешно завершен!");
		} finally {
			st.close();
		}
        isUpgrading = false;
	}

	public void upgradeTo37() throws DriverException, SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 37 ...");
		Statement st = conn.createStatement();
		try {
			st.executeUpdate(
					"ALTER TABLE " + getDBPrefix() + "t_syslog add ("
					+ "c_server_id VARCHAR(50),"
					+ "c_object NVARCHAR(255),"
					+ "c_process NVARCHAR(255)"
					+ ")");
			log.info("Апгрейд БД до версии 37 успешно завершен!");
		} finally {
			st.close();
		}
        isUpgrading = false;
	}
	
	public void upgradeTo38() throws DriverException, SQLException {
        isUpgrading = true;
        
        log.info("Апгрейд БД до версии 38 ...");
        log.info("Добавление атрибутов в классы Политика учетных записей.");
        
        KrnClass policy = getClassByNameComp("Политика учетных записей");
        db.addClass(policy, true);
        KrnAttribute newAttr = getAttributeByNameComp(policy, "не должно явно преобладать цифры");
        if(newAttr==null){
        	log.info("Создание атрибута 'не должно явно преобладать цифры' для класса Политика учетных записей");
    		createAttribute(-1, "793aa7fb-ada3-443b-8fca-5a15a696ebbc", policy.id, CID_BOOL, "не должно явно преобладать цифры", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        log.info("OK!!!");
        }
        
        newAttr = getAttributeByNameComp(policy, "запрет повтора в любом месте из более 2-х одинаковых символов пароля");
        if(newAttr==null){
        	log.info("Создание атрибута 'запрет повтора в любом месте из более 2-х одинаковых символов пароля' для класса Политика учетных записей");
    		createAttribute(-1, "57b63257-cc8c-4585-9d03-c21b44870db5", policy.id, CID_BOOL, "запрет повтора в любом месте из более 2-х одинаковых символов пароля", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        log.info("OK!!!");
        }
        
        newAttr = getAttributeByNameComp(policy, "запрет слов на клавиатуре");
        if(newAttr==null){
        	log.info("Создание атрибута 'запрет слов на клавиатуре' для класса Политика учетных записей");
    		createAttribute(-1, "d7ccab94-cca0-4079-af27-e12c5606d3a4", policy.id, CID_BOOL, "запрет слов на клавиатуре", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        log.info("OK!!!");
        }
        
		Statement st = conn.createStatement();
		try {
	        log.info("Изменение типа колонки 'c_dif' таблицы 't_vcs_model' на 'LONGBLOB'.");
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model MODIFY c_dif LONGBLOB");
	        log.info("OK!!!");
	        log.info("Изменение типа колонки 'c_dif' таблицы 't_vcs_objects' на 'LONGBLOB'.");
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects MODIFY c_dif LONGBLOB");
	        log.info("OK!!!");
		} finally {
			st.close();
		}
        
		log.info("Апгрейд БД до версии 38 успешно завершен!");
		isUpgrading = false;
    }
	
	public void upgradeTo39() throws SQLException, DriverException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 39 ...");
        log.info("Добавление атрибута 'developer' типа User в классы UI, Filter и ProcessDef, а также добавление колонки в таблицу t_methods.");

        // Добавление аттрибута 'developer' в классы UI, ProcessDef и Filter
        KrnClass userCls = getClassByNameComp("User");
        db.addClass(userCls, true);

        KrnClass uiCls = getClassByNameComp("UI");
        db.addClass(uiCls, true);
        KrnAttribute developerAttr = getAttributeByNameComp(uiCls, "developer");
        if (developerAttr == null) {
        	log.info("Создание атрибута 'developer' для класса UI");
    		createAttribute(-1, "4997ef24-a84e-42b7-ad2f-7c698b92fd5d", uiCls.id, userCls.id, "developer", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
	        log.info("OK!!!");
        }
        
        KrnClass processDefCls = getClassByNameComp("ProcessDef");
        db.addClass(processDefCls, true);
        developerAttr = getAttributeByNameComp(processDefCls, "developer");
        if (developerAttr == null) {
        	log.info("Создание атрибута 'developer' для класса ProcessDef");
    		createAttribute(-1, "ce30317b-7398-44aa-a5fc-b245b8a2649b", processDefCls.id, userCls.id, "developer", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
	        log.info("OK!!!");
        }
        
        KrnClass filterCls = getClassByNameComp("Filter");
        db.addClass(filterCls, true);
        developerAttr = getAttributeByNameComp(filterCls, "developer");
        if (developerAttr == null) {
        	log.info("Создание атрибута 'developer' для класса Filter");
    		createAttribute(-1, "927d96d6-5a06-498c-b93d-37b0d6a8d63c", filterCls.id, userCls.id, "developer", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
	        log.info("OK!!!");
        }
        
        // Добавление колонки в таблицу t_methods
		Statement st = conn.createStatement();
		try {
	        log.info("Добавление поля 'developer' в таблицу t_methods");
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_methods ADD (developer BIGINT)");
	        log.info("OK!!!");
		} finally {
			st.close();
		}
		log.info("Апгрейд БД до версии 39 успешно завершен!");
        isUpgrading = false;
	}
	
	public void upgradeTo40() throws SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 40 ...");
        log.info("Добавление полей c_before_del_event_expr (событие 'Перед удалением значения атрибута'), c_after_del_event_expr (событие 'После удаления значения атрибута') в таблице t_attrs.");
        log.info("Добавление полей c_before_create_obj (событие 'Перед созданием объекта'), c_after_create_obj (событие 'После создания объекта'), c_before_delete_obj (событие 'Перед удалением объекта'), c_after_delete_obj (событие 'После удаления объекта') в таблице t_classes.");
		Statement st = conn.createStatement();
		try {
			if (!isColumnExists("t_attrs", "c_before_del_event_expr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD COLUMN c_before_del_event_expr LONGBLOB DEFAULT NULL;");
			}
			if (!isColumnExists("t_attrs", "c_after_del_event_expr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD COLUMN c_after_del_event_expr LONGBLOB DEFAULT NULL;");
			}
			if (!isColumnExists("t_classes", "c_before_create_obj")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD COLUMN c_before_create_obj LONGBLOB DEFAULT NULL;");
			}
			if (!isColumnExists("t_classes", "c_after_create_obj")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD COLUMN c_after_create_obj LONGBLOB DEFAULT NULL;");
			}
			if (!isColumnExists("t_classes", "c_before_delete_obj")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD COLUMN c_before_delete_obj LONGBLOB DEFAULT NULL;");
			}
			if (!isColumnExists("t_classes", "c_after_delete_obj")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD COLUMN c_after_delete_obj LONGBLOB DEFAULT NULL;");
			}
		} finally {
			st.close();
		}
		log.info("Апгрейд БД до версии 40 успешно завершен.");
        isUpgrading = false;
    }
	
    public void upgradeTo41() throws DriverException, SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 41 ...");
        
        KrnClass userCls = getClassByNameComp("User");
        KrnClass processDefCls = getClassByNameComp("ProcessDef");

        KrnClass sysCls = getClassByNameComp("Системный класс");
        KrnClass cls = getClassByNameComp("ProcessDefUsingHistory");
        if (cls == null) {
        	cls = createClass("ProcessDefUsingHistory", sysCls.id, false, 0, -1, "b347dfc7-e31d-4364-980f-5949984e7012", false, null);
        	log.info("Создание класса 'ProcessDefUsingHistory'");
        }
        
        db.addClass(cls, true);
        db.addClass(userCls, true);
        db.addClass(processDefCls, true);
        KrnAttribute userAttr = getAttributeByNameComp(cls, "user");
        if (userAttr == null) {
        	log.info("Создание атрибута 'user' для класса ProcessDefUsingHistory");
        	userAttr = createAttribute(-1, "1549a459-0c86-40c3-b7e6-cf6b1cc84be8", cls.id, userCls.id, "user", 0, false, true, false, false, 0, 0, 0, 0, false, false, null, 0);
	        log.info("OK!!!");
        }
        KrnAttribute processAttr = getAttributeByNameComp(cls, "processDef");
        if (processAttr == null) {
        	log.info("Создание атрибута 'processDef' для класса ProcessDefUsingHistory");
        	createAttribute(-1, "94f165c3-2668-48d1-9f3c-d9b4505cddf3", cls.id, processDefCls.id, "processDef", 0, false, true, false, false, 0, 0, 0, 0, false, false, null, 0);
	        log.info("OK!!!");
        }
        KrnAttribute timeAttr = getAttributeByNameComp(cls, "time");
        if (timeAttr == null) {
        	log.info("Создание атрибута 'time' для класса ProcessDefUsingHistory");
        	createAttribute(-1, "2ec2d539-1d7d-442c-a117-83997fd72322", cls.id, CID_TIME, "time", 0, false, true, false, false, 0, 0, 0, 0, false, false, null, 0);
	        log.info("OK!!!");
        }
        
        KrnAttribute attr = getAttributeByNameComp(userCls, "historyProcessDef");
        if (attr == null) {
        	if (userAttr != null) {
        		log.info("Создание обратного атрибута 'historyProcessDef' для класса User");
        		createAttribute(-1, "0af8ddff-463f-4799-918f-3490a3bf9087", userCls.id, cls.id, "historyProcessDef", 2, false, false, false, false, 0, 0, userAttr.id, 0, false, false, null, 0);
        		log.info("OK!!!");
	        }
        }

        log.info("Апгрейд БД до версии 41 успешно завершен.");
        isUpgrading = false;
    }
    
	public void upgradeTo42() throws DriverException, SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 42 ...");
		Statement st = conn.createStatement();
		try {
			try {
		        log.info("Удаление индекса IDX_VCS_OBJ_ATTR_LANG_REPL с таблицы t_vcs_objects");
				st.executeUpdate("DROP INDEX IDX_VCS_OBJ_ATTR_LANG_REPL ON " + getDBPrefix() + "t_vcs_objects");
		        log.info("Индекс IDX_VCS_OBJ_ATTR_LANG_REPL с таблицы t_vcs_objects удален");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_OBJ_ATTR_LANG_REPL с таблицы t_vcs_objects не удален!");
			}
			
			try {
		        log.info("Удаление индекса IDX_VCS_MODEL_ENTITY_TYPE_ACTION_REPL с таблицы t_vcs_model");
				st.executeUpdate("DROP INDEX IDX_VCS_MODEL_ENTITY_TYPE_ACTION_REPL ON " + getDBPrefix() + "t_vcs_model");
		        log.info("Индекс IDX_VCS_MODEL_ENTITY_TYPE_ACTION_REPL с таблицы t_vcs_model удален");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_MODEL_ENTITY_TYPE_ACTION_REPL с таблицы t_vcs_model не удален!");
			}

			try {
		        log.info("Создание уникального индекса IDX_VCS_OBJ_ATTR_LANG на таблице t_vcs_objects");
				st.executeUpdate("CREATE UNIQUE INDEX IDX_VCS_OBJ_ATTR_LANG"
						+ " ON "+getDBPrefix()+"t_vcs_objects(c_obj_id,c_attr_id,c_lang_id,c_fix_end_id)");
		        log.info("Индекс IDX_VCS_OBJ_ATTR_LANG на таблице t_vcs_objects создан");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_OBJ_ATTR_LANG с таблице t_vcs_objects не создан!");
			}
			
			try {
		        log.info("Создание индекса IDX_VCS_USER на таблице t_vcs_objects");
				st.executeUpdate("CREATE INDEX IDX_VCS_USER ON "+getDBPrefix()+"t_vcs_objects(c_user_id)");
		        log.info("Индекс IDX_VCS_USER на таблице t_vcs_objects создан");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_USER с таблице t_vcs_objects не создан!");
			}

			try {
		        log.info("Создание индекса IDX_VCS_REPL на таблице t_vcs_objects");
				st.executeUpdate("CREATE INDEX IDX_VCS_REPL ON "+getDBPrefix()+"t_vcs_objects(c_repl_id)");
		        log.info("Индекс IDX_VCS_REPL на таблице t_vcs_objects создан");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_REPL с таблице t_vcs_objects не создан!");
			}

			try {
		        log.info("Создание уникального индекса IDX_VCS_MODEL_ENTITY_TYPE на таблице t_vcs_model");
				st.executeUpdate("CREATE UNIQUE INDEX IDX_VCS_MODEL_ENTITY_TYPE"
						+ " ON "+getDBPrefix()+"t_vcs_model(c_entity_id,c_type,c_fix_end_id)");
		        log.info("Индекс IDX_VCS_MODEL_ENTITY_TYPE на таблице t_vcs_model создан");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_MODEL_ENTITY_TYPE с таблице t_vcs_model не создан!");
			}
			
			try {
		        log.info("Создание индекса IDX_VCS_MODEL_USER на таблице t_vcs_model");
				st.executeUpdate("CREATE INDEX IDX_VCS_MODEL_USER ON "+getDBPrefix()+"t_vcs_model(c_user_id)");
		        log.info("Индекс IDX_VCS_MODEL_USER на таблице t_vcs_model создан");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_MODEL_USER с таблице t_vcs_model не создан!");
			}

			try {
		        log.info("Создание индекса IDX_VCS_MODEL_REPL на таблице t_vcs_model");
				st.executeUpdate("CREATE INDEX IDX_VCS_MODEL_REPL ON "+getDBPrefix()+"t_vcs_model(c_repl_id)");
		        log.info("Индекс IDX_VCS_MODEL_REPL на таблице t_vcs_model создан");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_MODEL_REPL с таблице t_vcs_model не создан!");
			}

			log.info("Апгрейд БД до версии 42 успешно завершен!");
		} finally {
			st.close();
		}
        isUpgrading = false;
	}
	
	public void upgradeTo43() throws DriverException, SQLException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 43 ...");
		log.info("Добавление полей c_before_create_obj_tr (транзакция события 'Перед созданием объекта'), "
				+ "c_after_create_obj_tr (транзакция события 'После создания объекта'), "
				+ "c_before_delete_obj_tr (транзакция события 'Перед удалением объекта'), "
				+ "c_after_delete_obj_tr (транзакция события 'После удаления объекта') в таблице t_classes.");
		log.info("Добавление полей c_before_event_tr (транзакция события 'Перед изменением значения атрибута'), "
				+ "c_after_event_tr (транзакция события 'После изменения значения атрибута'), "
				+ "c_before_del_event_tr (транзакция события 'Перед удалением значения атрибута'), "
				+ "c_after_del_event_tr (транзакция события 'После удаления значения атрибута') в таблице t_attrs.");
		
		Statement st = conn.createStatement();
		try {
			if (!isColumnExists("t_classes", "c_before_create_obj_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD COLUMN c_before_create_obj_tr INTEGER NOT NULL DEFAULT 0;");
				log.info("Колонка c_before_create_obj_tr добавлена.");
			} else {
				log.info("Колонка c_before_create_obj_tr уже создана.");
			}
			if (!isColumnExists("t_classes", "c_after_create_obj_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD COLUMN c_after_create_obj_tr INTEGER NOT NULL DEFAULT 0;");
				log.info("Колонка c_after_create_obj_tr добавлена.");
			} else {
				log.info("Колонка c_after_create_obj_tr уже создана.");
			}
			if (!isColumnExists("t_classes", "c_before_delete_obj_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD COLUMN c_before_delete_obj_tr INTEGER NOT NULL DEFAULT 0;");
				log.info("Колонка c_before_delete_obj_tr добавлена.");
			} else {
				log.info("Колонка c_before_delete_obj_tr уже создана.");
			}
			if (!isColumnExists("t_classes", "c_after_delete_obj_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD COLUMN c_after_delete_obj_tr INTEGER NOT NULL DEFAULT 0;");
				log.info("Колонка c_after_delete_obj_tr добавлена.");
			} else {
				log.info("Колонка c_after_delete_obj_tr уже создана.");
			}
			
			if (!isColumnExists("t_attrs", "c_before_event_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD COLUMN c_before_event_tr INTEGER NOT NULL DEFAULT 0;");
				log.info("Колонка c_before_event_tr добавлена.");
			} else {
				log.info("Колонка c_before_event_tr уже создана.");
			}
			if (!isColumnExists("t_attrs", "c_after_event_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD COLUMN c_after_event_tr INTEGER NOT NULL DEFAULT 0;");
				log.info("Колонка c_after_event_tr добавлена.");
			} else {
				log.info("Колонка c_after_event_tr уже создана.");
			}
			if (!isColumnExists("t_attrs", "c_before_del_event_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD COLUMN c_before_del_event_tr INTEGER NOT NULL DEFAULT 0;");
				log.info("Колонка c_before_del_event_tr добавлена.");
			} else {
				log.info("Колонка c_before_del_event_tr уже создана.");
			}
			if (!isColumnExists("t_attrs", "c_after_del_event_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD COLUMN c_after_del_event_tr INTEGER NOT NULL DEFAULT 0;");
				log.info("Колонка c_after_del_event_tr добавлена.");
			} else {
				log.info("Колонка c_after_del_event_tr уже создана.");
			}
		} finally {
			st.close();
		}
		log.info("Апгрейд БД до версии 43 успешно завершен.");
        isUpgrading = false;
	}

	public void upgradeTo44() throws DriverException, SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 44 ...");
        log.info("Добавление атрибутов в класс Flow.");
        List<KrnClass> clss=getAllClasses();
        for(KrnClass cls:clss){
        	db.addClass(cls, true);
        }
        KrnClass fcls=db.getClassByName("Flow");
        KrnClass pcls=db.getClassByName("Process");
		Statement st = conn.createStatement();
		try {
	        KrnAttribute newAttr = getAttributeByNameComp(fcls, "transId");
	        if(newAttr==null){
	        	newAttr = createAttribute(-1, null, fcls.id, CID_INTEGER, "transId", 0, false, false, false, true, 0, 0, 0, 0, false, false, "TRANSID", 0);
	        	log.info("Создание атрибута 'transId' для класса Flow");
	        }
       
			try {
		        log.info("Запись значений транзакции из Process во Flow!");
				st.executeUpdate("UPDATE " + getDBPrefix() + "FLOW SET TRANSID = (SELECT TRANSID FROM " 
		        + getDBPrefix() + "PROCESS WHERE c_obj_id=" + getDBPrefix() + "FLOW.PROCESSINSTANCE)"
		        		+ "WHERE FLOW.PROCESSINSTANCE IS NOT NULL");
		        log.info("Запись значений транзакции из Process во Flow завершена!");
			} catch (Exception e) {
		        log.error("Запись значений транзакции из Process во Flow не произведена!");
			}
			
			log.info("Апгрейд БД до версии 44 успешно завершен!");
		} finally {
			st.close();
		}
        isUpgrading = false;
	}
	
	public void upgradeTo45() throws DriverException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 45 ...");
        log.info("Добавление атрибутов в класс Import.");
		List<KrnClass> clss = getAllClasses();
		for (KrnClass cls : clss) {
			db.addClass(cls, true);
		}
		KrnClass importCls = db.getClassByName("Import");
        KrnAttribute importStartAttr = getAttributeByNameComp(importCls, "importStart");
		if (importStartAttr == null) {
			importStartAttr = createAttribute(-1, "a6081a1d-6343-46ed-a8a1-fc47d1414896", importCls.id, CID_TIME, "importStart", 0, false, true, false, false, 0, 0, 0, 0, false, false, null, 0);
        	log.info("Создание атрибута 'importStart' для класса Import");
		}
        KrnAttribute importFinishAttr = getAttributeByNameComp(importCls, "importFinish");
		if (importFinishAttr == null) {
			importFinishAttr = createAttribute(-1, "d19b6f36-dd42-4c7c-8a7e-fa23635ec496", importCls.id, CID_TIME, "importFinish", 0, false, true, false, false, 0, 0, 0, 0, false, false, null, 0);
        	log.info("Создание атрибута 'importFinish' для класса Import");
		}
		log.info("Апгрейд БД до версии 45 успешно завершен!");
        isUpgrading = false;
	}
	
	public void upgradeTo46() throws DriverException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 46 ...");
        log.info("Добавление атрибутов в класс HiperTree.");

		List<KrnClass> clss = getAllClasses();
		for (KrnClass cls : clss) {
			db.addClass(cls, true);
		}
		KrnClass hiperTreeCls = db.getClassByName("HiperTree");
		KrnClass hiperFolderCls = db.getClassByName("HiperFolder");
        KrnAttribute attrSystem = getAttributeByNameComp(hiperTreeCls, "isSystem");
		if (attrSystem == null) {
			attrSystem = createAttribute(-1, "af541c1e-70c3-11e6-8b77-86f30ca893d3", hiperTreeCls.id, CID_BOOL, "isSystem", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
        	log.info("Создание атрибута 'isSystem' для класса HiperTree");
		}
        KrnAttribute attrTitle = getAttributeByNameComp(hiperTreeCls, "title");
        KrnAttribute attrParent = getAttributeByNameComp(hiperTreeCls, "parent");
        KrnObject objMain = getObjectsByAttribute(hiperTreeCls.id, attrTitle.id, 0, CO_EQUALS, "Меню", 0).get(0);
        KrnObject objArchiv =null;
        try{
        	objArchiv = getObjectsByAttribute(hiperTreeCls.id, attrTitle.id, 0, CO_EQUALS, "Электронное хранилище", 0).get(0);
        }catch(Throwable tl){
            try{
            	objArchiv = getObjectsByAttribute(hiperTreeCls.id, attrTitle.id, 0, CO_EQUALS, "Архив Кадры", 0).get(0);
            }catch(Throwable tl1){
            	try {
            		objArchiv = getObjectsByAttribute(hiperTreeCls.id, attrTitle.id, 0, CO_EQUALS, "Архив РДИ", 0).get(0);
            	}catch(Throwable tl2){
            		objArchiv = getObjectsByAttribute(hiperTreeCls.id, attrTitle.id, 0, CO_EQUALS, "Архив", 0).get(0);
            	}
            }
        }
        KrnObject objDicts = getObjectsByAttribute(hiperTreeCls.id, attrTitle.id, 0, CO_EQUALS, "Справочники", 0).get(0);
        KrnObject obj = createObject(hiperFolderCls.id, 0, -1, null, false, null, false);
        if (obj != null) {
        	if (attrSystem != null) setValue(obj, attrSystem.id, 0, 0, 0, new Long(1), false, false);
        	if (attrTitle != null) setValue(obj, attrTitle.id, 0, 0, 0, "Администрирование", false, false);
        	if (attrParent != null && objMain != null) setValue(obj, attrParent.id, 0, 0, 0, objMain, false, false);
        }
        if (objArchiv != null && attrSystem != null) setValue(objArchiv, attrSystem.id, 0, 0, 0, new Long(1), false, false);
        if (objDicts != null && attrSystem != null) setValue(objDicts, attrSystem.id, 0, 0, 0, new Long(1), false, false);
        
		log.info("Апгрейд БД до версии 46 успешно завершен!");
        isUpgrading = false;
	}
	public void upgradeTo47() throws SQLException, DriverException {
        isUpgrading = true;
        
        log.info("Апгрейд БД до версии 47 ...");
        log.info("В таблице ImpExp тип поля 'информация о содержимом файла' заменен c TEXT на MEDIUMTEXT");
		//Создаем поле типа мемо
        KrnClass cls=getClassByNameComp("ImpExp");
        KrnAttribute attr=getAttributeByNameComp(cls,"информация о содержимом файла");
        if (attr == null) {
        	attr = createAttribute(-1, "d8a1634f-3c71-4753-86e5-83e8196d6f5c", cls.id, CID_STRING, "информация о содержимом файла", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
        }
        String tname = getClassTableName(cls,true);
        String cname=getColumnName(attr);
        if (!isColumnExists(tname, cname)) {
        	QueryRunner qr = new QueryRunner(true);
	    	String sql = "ALTER TABLE " + tname+" MODIFY COLUMN "+cname+" MEDIUMTEXT DEFAULT NULL";
	    	qr.update(conn, sql);
        }
    	log.info("Апгрейд БД до версии 47 успешно завершен.");
		
        isUpgrading = false;
    }
	
	public void upgradeTo48() throws DriverException, SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 48 ...");
		Statement st = conn.createStatement();
		try {
			st.executeUpdate(
					"ALTER TABLE " + getDBPrefix() + "t_syslog add ("
					+ "c_tab_name VARCHAR(255),"
					+ "c_col_name VARCHAR(255)"
					+ ")");
			log.info("Апгрейд БД до версии 48 успешно завершен!");
		} finally {
			st.close();
		}
        isUpgrading = false;
	}
	
	public void upgradeTo49() throws DriverException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 49 ...");
        log.info("Переименование значение объекта 'Администрирование' в классе HiperTree.");

		List<KrnClass> clss = getAllClasses();
		for (KrnClass cls : clss) {
			db.addClass(cls, true);
		}
		KrnClass hiperTreeCls = db.getClassByName("HiperTree");
        KrnAttribute attrTitle = getAttributeByNameComp(hiperTreeCls, "title");
        KrnAttribute attrSystem = getAttributeByNameComp(hiperTreeCls, "isSystem");
        List<KrnObject> objs = getObjectsByAttribute(hiperTreeCls.id, attrSystem.id, 0, CO_EQUALS, new Long(1), 0);
        for (KrnObject obj : objs) {
        	String val = (String)getValue(obj.id, attrTitle.id, 0, 0, 0);
        	if(val.equals("Администрирование") && attrTitle != null) {
        		setValue(obj, attrTitle.id, 0, 0, 0, "Технологический блок", false, false);
        		break;
        	}	
        }
        
		log.info("Апгрейд БД до версии 49 успешно завершен!");
        isUpgrading = false;
	}

	@Override
	public int setAttrTriggerEventExpression(String expr, long attrId, int mode, boolean isZeroTransaction) throws SQLException, DriverException {
		return setAttrTriggerEventExpression(expr, attrId, mode, isZeroTransaction, true);
	}
	
	@Override
	public int setAttrTriggerEventExpression(String expr, long attrId, int mode, boolean isZeroTransaction, boolean logChanges) throws SQLException, DriverException {
		KrnAttribute attr = db.getAttributeById(attrId);
		KrnAttribute newObj=new KrnAttribute();
		newObj.id=attr.id;
		newObj.uid=attr.uid;
		newObj.classId=attr.classId;
		newObj.name=attr.name;
		String column1;
		String column2;
		byte[] oldExpr;
		int entityType;
	    if (mode == 0) {
	    	column1 = "c_before_event_expr";
	    	column2 = "c_before_event_tr";
	    	oldExpr = attr.beforeEventExpr;
	    	entityType = ENTITY_TYPE_ATTR_TRIGGER_BEFORE_CHANGE;
	    	newObj.beforeEventTr=isZeroTransaction ? 0 : 1;
	    } else if (mode == 1) {
	    	column1 = "c_after_event_expr";
	    	column2 = "c_after_event_tr";
	    	oldExpr = attr.afterEventExpr;
	    	entityType = ENTITY_TYPE_ATTR_TRIGGER_AFTER_CHANGE;
	    	newObj.afterEventTr=isZeroTransaction ? 0 : 1;
	    } else if (mode == 2) {
	    	column1 = "c_before_del_event_expr";
	    	column2 = "c_before_del_event_tr";
	    	oldExpr = attr.beforeDelEventExpr;
	    	entityType = ENTITY_TYPE_ATTR_TRIGGER_BEFORE_DELETE;
	    	newObj.beforeDelEventTr=isZeroTransaction ? 0 : 1;
	    } else {
	    	column1 = "c_after_del_event_expr";
	    	column2 = "c_after_del_event_tr";
	    	oldExpr = attr.afterDelEventExpr;
	    	entityType = ENTITY_TYPE_ATTR_TRIGGER_AFTER_DELETE;
	    	newObj.afterDelEventTr=isZeroTransaction ? 0 : 1;
	    }
	    byte[] newExpr = new byte[0];
    	try {
    		newExpr = expr.getBytes("UTF-8");
    	} catch (UnsupportedEncodingException e) {
    		log.error(e, e);
    	}
    	if (newExpr.length == 0) {
    		newExpr = null;
    	}
    	
    	int action;
		if (oldExpr == null && newExpr != null) {
			action = 0;
		} else if (oldExpr != null && newExpr == null) {
			action = 2;
		} else if (oldExpr == null && newExpr == null) {
			return -1;
		} else {
			action = 1;
		}

		if (logChanges)
			logVcsModelChanges(entityType, action, attr, newObj, newExpr,conn);
//			logVcsModelChanges(attr.uid, entityType, action, oldExpr, newExpr, conn);

		PreparedStatement st = conn.prepareStatement("UPDATE " + getDBPrefix() + "t_attrs SET " + column1 + "=?, " + column2 + "=? WHERE c_id=?");
    	try {
			setValue(st, 1, PC_BLOB, newExpr);
			st.setInt(2, isZeroTransaction ? 0 : 1);
			st.setLong(3, attrId);
			st.executeUpdate();
    	} finally {
    		st.close();
    	}
		attr = db.getAttributeById(attrId);
		db.removeAttribute(attr);
	    if (mode == 0) {
	    	attr.beforeEventExpr = newExpr;
	    	attr.beforeEventTr = isZeroTransaction ? 0 : 1;
	    } else if (mode == 1) {
	    	attr.afterEventExpr = newExpr;
	    	attr.afterEventTr = isZeroTransaction ? 0 : 1;
	    } else if (mode == 2) {
	    	attr.beforeDelEventExpr = newExpr;
	    	attr.beforeDelEventTr = isZeroTransaction ? 0 : 1;
	    } else {
	    	attr.afterDelEventExpr = newExpr;
	    	attr.afterDelEventTr = isZeroTransaction ? 0 : 1;
	    }
		db.addAttribute(attr, false);
		commit();
		return action;
	}
	
	@Override
	public int setClsTriggerEventExpression(String expr, long clsId, int mode, boolean isZeroTransaction) throws SQLException, DriverException {
		return setClsTriggerEventExpression(expr, clsId, mode, isZeroTransaction, true);
	}
	
	@Override
	public int setClsTriggerEventExpression(String expr, long clsId, int mode, boolean isZeroTransaction, boolean logChanges) throws SQLException, DriverException {
		KrnClass cls = db.getClassById(clsId);
		KrnClass newObj=new KrnClass();
		newObj.uid=cls.uid;
		newObj.id=cls.id;
		newObj.name=cls.name;
		String column1;
		String column2;
		byte[] oldExpr;
		int entityType;
	    if (mode == 0) {
	    	column1 = "c_before_create_obj";
	    	column2 = "c_before_create_obj_tr";
	    	oldExpr = cls.beforeCreateObjExpr;
	    	entityType = ENTITY_TYPE_CLS_TRIGGER_BEFORE_CREATE;
	    	newObj.beforeCreateObjTr= isZeroTransaction ? 0 : 1;
	    } else if (mode == 1) {
	    	column1 = "c_after_create_obj";
	    	column2 = "c_after_create_obj_tr";
	    	oldExpr = cls.afterCreateObjExpr;
	    	entityType = ENTITY_TYPE_CLS_TRIGGER_AFTER_CREATE;
	    	newObj.afterCreateObjTr= isZeroTransaction ? 0 : 1;
	    } else if (mode == 2) {
	    	column1 = "c_before_delete_obj";
	    	column2 = "c_before_delete_obj_tr";
	    	oldExpr = cls.beforeDeleteObjExpr;
	    	entityType = ENTITY_TYPE_CLS_TRIGGER_BEFORE_DELETE;
	    	newObj.beforeDeleteObjTr= isZeroTransaction ? 0 : 1;
	    } else {
	    	column1 = "c_after_delete_obj";
	    	column2 = "c_after_delete_obj_tr";
	    	oldExpr = cls.afterDeleteObjExpr;
	    	entityType = ENTITY_TYPE_CLS_TRIGGER_AFTER_DELETE;
	    	newObj.afterDeleteObjTr= isZeroTransaction ? 0 : 1;
	    }
	    byte[] newExpr = new byte[0];
    	try {
    		newExpr = expr.getBytes("UTF-8");
    	} catch (UnsupportedEncodingException e) {
    		log.error(e, e);
    	}
    	if (newExpr.length == 0) {
    		newExpr = null;
    	}
    	
    	int action;
		if (oldExpr == null && newExpr != null) {
			action = 0;
		} else if (oldExpr != null && newExpr == null) {
			action = 2;
		} else if (oldExpr == null && newExpr == null) {
			return -1;
		} else {
			action = 1;
		}
    	
		if (logChanges)
			logVcsModelChanges(entityType, action, cls, newObj, newExpr,conn);
//			logVcsModelChanges(cls.uid, entityType, action, oldExpr, newExpr, conn);
		
		PreparedStatement st = conn.prepareStatement("UPDATE " + getDBPrefix() + "t_classes SET " + column1 + "=?, " + column2 + "=? WHERE c_id=?");
    	try {
    		setValue(st, 1, PC_BLOB, newExpr);
			st.setInt(2, isZeroTransaction ? 0 : 1);
			st.setLong(3, clsId);
			st.executeUpdate();
    	} finally {
			st.close();
		}
		cls = db.getClassById(clsId);
		db.removeClass(cls);
	    if (mode == 0) {
	    	cls.beforeCreateObjExpr = newExpr;
	    	cls.beforeCreateObjTr = isZeroTransaction ? 0 : 1;
	    } else if (mode == 1) {
	    	cls.afterCreateObjExpr = newExpr;
	    	cls.afterCreateObjTr = isZeroTransaction ? 0 : 1;
	    } else if (mode == 2) {
	    	cls.beforeDeleteObjExpr = newExpr;
	    	cls.beforeDeleteObjTr = isZeroTransaction ? 0 : 1;
	    } else {
	    	cls.afterDeleteObjExpr = newExpr;
	    	cls.afterDeleteObjTr = isZeroTransaction ? 0 : 1;
	    }
		db.addClass(cls, false);
		commit();
        return action;
	}

	//Методы для работы с процедурами
	@Override
	//Создание процедуры
	public String createProcedure(String nameProcedure, List<String> args, String body){
		Statement st = null;
		StringBuffer sb=new StringBuffer();
		String dropSql="DROP PROCEDURE IF EXISTS "+nameProcedure;
		String res="";
		if(nameProcedure!=null && !"".equals(nameProcedure) && args!=null && args.size()>0){
		sb.append("CREATE OR REPLACE PROCEDURE ").append(nameProcedure).append(" (");
		int i=1;
		if(args!=null && args.size()>0){
			String[] ps=args.get(0).split(",");
			if(ps.length==2){
				sb.append(ps[0]).append(" "+getSqlTypeName(Long.parseLong(ps[1]),0));
			}
			while(i++ < args.size()){
				ps=args.get(i).split(",");
				if(ps.length==2){
					sb.append(",").
					append(ps[0]).
					append(" "+getSqlTypeName(Long.parseLong(ps[1]),0));
				}
			}
		}
		sb.append(") ");
		sb.append(body.substring(body.indexOf("IS")));
		}else{
			sb.append(body);
		}
		try {
			st = conn.createStatement();
			st.executeUpdate(dropSql);
			st.executeUpdate(sb.toString());
			st.close();
		} catch (SQLException e) {
			res=e.getMessage();
			log.error(res);
	    } finally {
	    	DbUtils.closeQuietly(st);
	    } 
		return res;
	}
	@Override
	public List execProcedure(String nameProcedure)  throws DriverException {
		return execProcedure(nameProcedure,new ArrayList<Object>());
	}
	@Override
	public List execProcedure(String nameProcedure, List<Object> vals)  throws DriverException {
		return execProcedure(nameProcedure,vals,null,null);
	}
	
	@Override
	//Вызов процедуры на исполнение
	public List execProcedure(String nameProcedure,
			List<Object> vals, List<String> types_in,
			List<String> types_out) throws DriverException {
		return execProcedure(nameProcedure,vals,types_in, types_out, conn, log);
	}

	public static List execProcedure(String nameProcedure,
			List<Object> vals, List<String> types_in,
			List<String> types_out, Connection connection,
			Log log)  throws DriverException {
		CallableStatement proc = null;
		PreparedStatement pst=null;
        List res = new ArrayList();
		String psql="SELECT param_list,name FROM mysql.proc WHERE name=?";
		StringBuffer sb=new StringBuffer();
		sb.append("CALL ").append(nameProcedure).append("(");
		int i=1;
		if(vals!=null && vals.size()>0){
			sb.append("?");
			while(i++ < vals.size()){
				sb.append(",?");
			}
		}
		sb.append(")");
		try {
			pst=connection.prepareStatement(psql);
			pst.setString(1, nameProcedure);
			proc = connection.prepareCall(sb.toString());
			i=0;
			ResultSet rs = pst.executeQuery();
			if(rs.next()){
				String params = getString(rs, 1);
				String[]ps=params.split(",");
				for(String p:ps){
					String[] pars=p.trim().split(" ");
					if(pars[2].contains("CHAR"))
						proc.setString(i+1, (String)vals.get(i++));
					else if(pars[2].contains("INT"))
						proc.setLong(i+1, Long.parseLong((String)vals.get(i++)));
					else if(pars[2].contains("DOUBLE"))
						proc.setDouble(i+1, Double.parseDouble((String)vals.get(i++)));
					else if(pars[2].contains("TIME"))
						proc.setTimestamp(i+1, Funcs.convertToSqlTime((KrnDate)vals.get(i++)));
					else if(pars[2].contains("DATE"))
						proc.setDate(i+1, Funcs.convertToSqlDate((KrnDate)vals.get(i++)));
				}
				proc.execute();
			}else{
				throw new DriverException("Процедура: '"+nameProcedure.toUpperCase(Constants.OK)+"' отсутствует в базе данных!"
						,ErrorCodes.PROCEDURE_NOT_EXIST);
			}
		} catch(SQLException e){
			log.error(e, e);
			throw new DriverException("Процедура: '"+nameProcedure.toUpperCase(Constants.OK)+"' ошибка при выполнении!"
					,ErrorCodes.PROCEDURE_SQL_ERROR);
	    } finally {
	    	DbUtils.closeQuietly(proc);
	    	DbUtils.closeQuietly(pst);
	    } 
		return res;
	}

	@Override
	//Получение списка процедур
	public List<String> getListProcedure(String type) {
		List<String> res=new ArrayList<String>();
		PreparedStatement pst=null;
		String psql="SELECT name FROM mysql.proc WHERE TYPE = ?";
		try {
			pst=conn.prepareStatement(psql);
			pst.setString(1, type);
			ResultSet rs = pst.executeQuery();
			while(rs.next()){
				res.add(getString(rs, 1));
			}
		} catch (SQLException e) {
			log.error(e, e);
	    } finally {
	    	DbUtils.closeQuietly(pst);
	    } 
		return res;
	}

	@Override
	public byte[] getProcedureContent(String name,String type) {
		StringBuilder sb=new StringBuilder();
		PreparedStatement pst=null;
		ResultSet rs = null;
		String psql="SELECT body FROM mysql.proc WHERE name = ? AND type = ?";
		try {
			pst=conn.prepareStatement(psql);
			pst.setString(1, name);
			pst.setString(2, type);
			rs = pst.executeQuery();
			while(rs.next()){
				sb.append(getString(rs, 1));
			}
			return sb.toString().getBytes("UTF-8");
		} catch (SQLException e) {
			log.error(e, e);
	    } catch (UnsupportedEncodingException e) {
	    	log.error(e, e);
	    } finally {
	    	DbUtils.closeQuietly(rs);
	    	DbUtils.closeQuietly(pst);
	    } 
		return new byte[0];
	}

	@Override
	public boolean deleteProcedure(String name, String type) {
		int res=0;
		PreparedStatement pst = null;
		String psql="DELETE FROM mysql.proc WHERE db = ? AND name = ? AND type = ?";
		try {
			pst=conn.prepareStatement(psql);
			pst.setString(1, dsName);
			pst.setString(1, name);
			pst.setString(1, type);
			res = pst.executeUpdate();
		} catch (SQLException e) {
			log.error(e, e);
	    } finally {
	    	DbUtils.closeQuietly(pst);
	    } 
		return res==1;
	}
	
	@Override
	public List<Long> getVcsGroupObjects(long clsId) throws DriverException {
		try {
			PreparedStatement selPst = null; 
			ResultSet rs = null;
			List<Long> res=new ArrayList<Long>();
			KrnClass clsUser = db.getClassByName("User");
            KrnClass clsImport = db.getClassByName("Import");
            KrnClass clsExport = db.getClassByName("Export");
			if(clsId == clsUser.id){
				selPst = conn.prepareStatement(
					"SELECT c_user_id FROM " + getDBPrefix() + "t_vcs_objects  WHERE c_user_id IS NOT NULL GROUP BY c_user_id"
					+" UNION "
					+"SELECT c_user_id FROM " + getDBPrefix() + "t_vcs_model  WHERE c_user_id IS NOT NULL GROUP BY c_user_id"
				);
			} else if (version < VERSION_UL && clsId == clsImport.id){
				selPst = conn.prepareStatement(
						"SELECT c_repl_id FROM " + getDBPrefix() + "t_vcs_objects  WHERE c_repl_id IS NOT NULL GROUP BY c_repl_id"
						+" UNION "
						+"SELECT c_repl_id FROM " + getDBPrefix() + "t_vcs_model  WHERE c_repl_id IS NOT NULL GROUP BY c_repl_id"
				);
			} else if (version < VERSION_UL && clsId == clsExport.id){
				return res;
            } else if (clsId==clsImport.id){
                selPst = conn.prepareStatement(
                        "SELECT c_rimport_id FROM " + getDBPrefix() + "t_vcs_objects  WHERE c_rimport_id IS NOT NULL GROUP BY c_rimport_id"
                        +" UNION "
                        +"SELECT c_rimport_id FROM " + getDBPrefix() + "t_vcs_model  WHERE c_rimport_id IS NOT NULL GROUP BY c_rimport_id"
                );
            } else if (clsId==clsExport.id){
                selPst = conn.prepareStatement(
                		"SELECT c_rexport_id FROM " + getDBPrefix() + "t_vcs_objects  WHERE c_rexport_id IS NOT NULL GROUP BY c_rexport_id"
                        +" UNION "
                        +"SELECT c_rexport_id FROM " + getDBPrefix() + "t_vcs_model  WHERE c_rexport_id IS NOT NULL GROUP BY c_rexport_id"
                );
			}
			if(selPst!=null){
				try {
					rs = selPst.executeQuery();
					while (rs.next()) {
						res.add(rs.getLong(1));
					}
				} finally {
					DbUtils.closeQuietly(rs);
					DbUtils.closeQuietly(selPst);
				}
			}
			return res;
		} catch (SQLException ex) {
			throw convertException(ex);
		}
	}

	@Override
	public List<KrnVcsChange> getVcsDataChanges(int isFixd, int isCount, long userId, long replId, String uid) throws DriverException {
		String sql = null;
		try {
			List<KrnVcsChange> res = getVcsModelChanges(isFixd, isCount, userId, replId, uid);
			String userTableName = getClassTableName(db.getClassByName("User"), true);
			
			if (version < VERSION_UL) {
				sql =	"SELECT v1.c_user_id,u.c_uid,u.c_class_id,0,v1.c_obj_id,v1.c_obj_uid,v1.c_obj_class_id,v1.c_mod_last_time,v1.c_repl_id,0,v1.c_fix_comment,v1.c_name"
						+ " FROM " + getDBPrefix() + "t_vcs_objects v1"
						+ " LEFT JOIN " + userTableName + " u ON u.c_obj_id=v1.c_user_id AND u.c_tr_id=0"
						+ " RIGHT JOIN (SELECT max(v.c_id) as c_id"
						+ " FROM " + getDBPrefix() + "t_vcs_objects v";
						boolean flag = false;
						if (Constants.VCS_ALL != isFixd) {
							flag = true;
							sql += " WHERE v.c_fix_end_id IS " + (Constants.VCS_FIXD==isFixd?"NOT NULL AND v.c_repl_id IS NULL":"NULL");
						}
						if (Constants.VCS_ALL != isFixd) {
							if (flag) {
								sql += " AND ";
							} else {
								sql += " WHERE ";
							}
							sql += "v.c_repl_id IS " + (Constants.VCS_IMPORT == isFixd ? "NOT NULL" : "NULL");
						}
	
				if (userId > 0) sql += " AND v.c_user_id = ?";
				if (replId > 0) sql += " AND v.c_repl_id = ?";
				
				sql += " GROUP BY v.c_obj_id) v2 ON v2.c_id = v1.c_id";
			} else {
                sql =   "SELECT v1.c_user_id,u.c_uid,u.c_class_id,v1.c_old_user_id,v1.c_obj_id,v1.c_obj_uid,v1.c_obj_class_id,v1.c_mod_last_time,v1.c_mod_confirm_time,v1.c_rimport_id,v1.c_rexport_id,v1.c_fix_comment,v1.c_name"
                        + " FROM " + getDBPrefix() + "t_vcs_objects v1"
                        + " LEFT JOIN " + userTableName + " u ON u.c_obj_id=v1.c_user_id AND u.c_tr_id=0"
                        + " RIGHT JOIN (SELECT max(v.c_id) as c_id"
                        + " FROM " + getDBPrefix() + "t_vcs_objects v";
                if(isFixd>=0 && Constants.VCS_ALL!=isFixd) {
                	sql += " WHERE "+(Constants.VCS_FIXD==isFixd?"v.c_fix_end_id IS NOT NULL AND v.c_rexport_id IS NULL"
                                        :(Constants.VCS_IMPORT==isFixd?"v.c_rimport_id IS NOT NULL"
                                        :(Constants.VCS_EXPORT==isFixd?"v.c_rexport_id IS NOT NULL"
                                        :(Constants.VCS_NOT_FIXD==isFixd?"v.c_fix_end_id IS NULL"
                                        :"v.c_fix_start_id IS NOT NULL AND v.c_rimport_id IS NULL"))));

                	if (userId > 0) sql += " AND v.c_user_id = ?";
                	if (replId > 0) {
                		if(Constants.VCS_IMPORT==isFixd)
		                        sql += " AND v.c_rimport_id = ?";
		                else if(Constants.VCS_EXPORT==isFixd)
		                        sql += " AND v.c_rexport_id = ?";
                	}
                }else {
                	if (userId > 0) sql += " WHERE v.c_user_id = ?";
                }
		        sql += " GROUP BY v.c_obj_id"+(Constants.VCS_EXPORT==isFixd?",v.c_rexport_id"
		                        :(Constants.VCS_IMPORT==isFixd?",v.c_rimport_id":""))+") v2 ON v2.c_id = v1.c_id";

			}
			if (uid != null) sql += " WHERE v1.c_obj_uid = ?";
			if(isCount==Constants.VCS_COUNT)
				sql = "SELECT COUNT(*) FROM ("+sql+") m";
			PreparedStatement selPst = conn.prepareStatement(sql);
			ResultSet rs = null;
			try {
				int index = 0;
				if (userId > 0) selPst.setLong(++index, userId);
				if (replId > 0) selPst.setLong(++index, replId);
				if (uid != null) selPst.setString(++index, uid);

				rs = selPst.executeQuery();
				if(isCount==Constants.VCS_COUNT) {
					if(rs.next()) {
						KrnVcsChange change;
						if(res.size()>0)
							change=res.get(0);
						else {
							change=new KrnVcsChange();
							res.add(change);
						}
						change.count += rs.getLong(1);
					}
				}else {
					while (rs.next()) {
						KrnObject userObj = new KrnObject(rs.getLong(1), getSanitizedString(rs, 2), rs.getLong(3));
						String objUid = getSanitizedString(rs, 6);
						objUid = objUid != null ? objUid.trim() : null;
						KrnObject obj = new KrnObject(rs.getLong(5), objUid, rs.getLong(7));
						java.sql.Timestamp t8=rs.getTimestamp(8);
						KrnVcsChange change=new KrnVcsChange(obj, userObj, t8==null?null:new KrnDate(t8.getTime()), getMemo(rs, 12));
						change.title=getString(rs, 13);
	                    if(rs.getLong(4)>0)
	                        change.oldUserId=rs.getLong(4);
						if (Constants.VCS_IMPORT == isFixd && rs.getLong(10) > 0) 
							change.importId=rs.getLong(10);
						if (Constants.VCS_EXPORT == isFixd && rs.getLong(11) > 0) 
							change.exportId=rs.getLong(11);
						res.add(change);
						if(rs.getTimestamp(9)!=null)
							change.dateConfirm = new KrnDate(rs.getTimestamp(9).getTime());
					}
				}
			} finally {
				DbUtils.closeQuietly(rs);
				DbUtils.closeQuietly(selPst);
			}
			return res;
		} catch (SQLException ex) {
			log.error(sql);
			log.error(ex, ex);
			throw convertException(ex);
		}
	}
	
	public List<KrnVcsChange> getVcsModelChanges(int isFixd, int isCount, long userId, long replId, String uid) throws DriverException {
		String sql = null;
		try {
			List<KrnVcsChange> res = new ArrayList<KrnVcsChange>();
			String userTableName = getClassTableName(db.getClassByName("User"), true);
			if (version < VERSION_UL) {
				sql = 	"SELECT v1.c_user_id,u.c_uid,u.c_class_id,v1.c_entity_id,v1.c_type,v1.c_mod_last_time,v1.c_repl_id,0,v1.c_fix_comment,v1.c_name"
						+ " FROM " + getDBPrefix() + "t_vcs_model v1"
						+ " LEFT JOIN " + userTableName + " u ON u.c_obj_id=v1.c_user_id AND u.c_tr_id=0"
						+ " RIGHT JOIN (SELECT max(v.c_id) as c_id"
						+ " FROM " + getDBPrefix() + "t_vcs_model v";
						boolean flag = false;
						if (Constants.VCS_ALL != isFixd) {
							flag = true;
							sql += " WHERE v.c_fix_end_id IS " + (Constants.VCS_FIXD == isFixd ? "NOT NULL AND v.c_repl_id IS NULL":"NULL");
						}
						if (Constants.VCS_ALL != isFixd) {
							if (flag) {
								sql += " AND ";
							} else {
								sql += " WHERE ";
							}
							sql += "v.c_repl_id IS " + (Constants.VCS_IMPORT == isFixd ? "NOT NULL" : "NULL");
						}
						
				if (userId > 0) sql += " AND v.c_user_id = ?";
				if (replId > 0) sql += " AND v.c_repl_id = ?";
			
				sql += " GROUP BY v.c_entity_id,v.c_type) v2 ON v2.c_id = v1.c_id";
			} else {
                sql =   "SELECT v1.c_user_id,u.c_uid,u.c_class_id,v1.c_entity_id,v1.c_type,v1.c_mod_last_time,v1.c_mod_confirm_time,v1.c_rimport_id,v1.c_rexport_id,v1.c_fix_comment,v1.c_name"
                        + " FROM " + getDBPrefix() + "t_vcs_model v1"
                        + " LEFT JOIN " + userTableName + " u ON u.c_obj_id=v1.c_user_id AND u.c_tr_id=0"
                        + " RIGHT JOIN (SELECT max(v.c_id) as c_id"
                        + " FROM " + getDBPrefix() + "t_vcs_model v"
                        + " WHERE "+(Constants.VCS_FIXD==isFixd?"v.c_fix_end_id IS NOT NULL AND v.c_rexport_id IS NULL"
                                        :(Constants.VCS_IMPORT==isFixd?"v.c_rimport_id IS NOT NULL"
                                        :(Constants.VCS_EXPORT==isFixd?"v.c_rexport_id IS NOT NULL"
                                        :(Constants.VCS_NOT_FIXD==isFixd?"v.c_fix_end_id IS NULL"
                                        :"v.c_fix_start_id IS NOT NULL AND v.c_rimport_id IS NULL"))));
        
		        if (userId > 0) sql += " AND v.c_user_id = ?";
		        if (replId > 0) {
		                if(Constants.VCS_IMPORT==isFixd)
		                        sql += " AND v.c_rimport_id = ?";
		                else if(Constants.VCS_EXPORT==isFixd)
		                        sql += " AND v.c_rexport_id = ?";
		        }
		                        
		        sql += " GROUP BY v.c_entity_id,v.c_type"+(Constants.VCS_EXPORT==isFixd?",v.c_rexport_id"
		                        :(Constants.VCS_IMPORT==isFixd?",v.c_rimport_id":""))+") v2 ON v2.c_id = v1.c_id";
			}
			if (uid != null) sql += " WHERE v1.c_entity_id = ?";
			
			if(isCount==Constants.VCS_COUNT)
				sql = "SELECT COUNT(*) FROM ("+sql+") m";
			PreparedStatement selPst = conn.prepareStatement(sql);
			ResultSet rs = null;
			try {
				int index = 0;
				if (userId > 0) selPst.setLong(++index, userId);
				if (replId > 0) selPst.setLong(++index, replId);
				if (uid != null) selPst.setString(++index, uid);

				rs = selPst.executeQuery();
				if(isCount==Constants.VCS_COUNT) {
					if(rs.next()) {
						KrnVcsChange change=new KrnVcsChange();
						change.count = rs.getLong(1);
						res.add(change);
					}
				}else {
					while (rs.next()) {
						String entityUid = getSanitizedString(rs, 4);
						entityUid = entityUid != null ? entityUid.trim() : null;
						
						KrnObject userObj = new KrnObject(rs.getLong(1), getSanitizedString(rs, 2), rs.getLong(3));
						long typeId = rs.getLong(5);
						KrnDate dateChange = rs.getTimestamp(6) != null ? new KrnDate(rs.getTimestamp(6).getTime()) : null;
						
						if (ENTITY_TYPE_METHOD == typeId) {
							KrnMethod obj = db.getMethodByUid(entityUid);
							if (obj == null)
								obj = new KrnMethod(entityUid, getString(rs, 10), -1,"", false, 0);
							KrnVcsChange change = new KrnVcsChange(obj,(int) typeId, userObj, dateChange, getMemo(rs, 10));
							change.title = getString(rs, 11);
	                        if (Constants.VCS_IMPORT == isFixd && rs.getLong(8) > 0) 
	                            change.importId = rs.getLong(8);
	                        if (Constants.VCS_EXPORT == isFixd && rs.getLong(9) > 0) 
	                        	change.exportId = rs.getLong(9);
	    					if(rs.getTimestamp(7)!=null)
	    						change.dateConfirm = new KrnDate(rs.getTimestamp(7).getTime());
							res.add(change);
						} else if (ENTITY_TYPE_CLASS == typeId) {
							KrnClass cls = db.getClassByUid(entityUid);
							if (cls == null)
								cls=new KrnClass(entityUid,-1,-1,false,-1, getString(rs, 10),"",null,null,null,null,-1,-1,-1,-1);
							KrnVcsChange change = new KrnVcsChange(cls, (int) typeId, userObj, dateChange, getMemo(rs, 10));
							change.title = getString(rs, 11);
	                        if (Constants.VCS_IMPORT == isFixd && rs.getLong(8) > 0) 
	                            change.importId = rs.getLong(8);
	                        if (Constants.VCS_EXPORT == isFixd && rs.getLong(9) > 0) 
	                        	change.exportId = rs.getLong(9);
	    					if(rs.getTimestamp(7)!=null)
	    						change.dateConfirm = new KrnDate(rs.getTimestamp(7).getTime());
							res.add(change);
						} else if (ENTITY_TYPE_ATTRIBUTE == typeId) {
							KrnAttribute attr = db.getAttributeByUid(entityUid);
							if (attr == null)
								attr = new KrnAttribute(entityUid,-1, getString(rs, 10), -1,-1,0, false,false,false, 0,0,false,-1,-1,false,"",null,null,null,null,-1,-1,-1,-1,-1);
							KrnVcsChange change = new KrnVcsChange(attr, (int) typeId, userObj, dateChange, getMemo(rs, 10));
							change.title = getString(rs, 11);
	                        if (Constants.VCS_IMPORT == isFixd && rs.getLong(8) > 0) 
	                            change.importId = rs.getLong(8);
	                        if (Constants.VCS_EXPORT == isFixd && rs.getLong(9) > 0) 
	                        	change.exportId = rs.getLong(9);
	    					if(rs.getTimestamp(7)!=null)
	    						change.dateConfirm = new KrnDate(rs.getTimestamp(7).getTime());
							res.add(change);
						} else if (ENTITY_TYPE_CLS_TRIGGER_BEFORE_CREATE == typeId || ENTITY_TYPE_CLS_TRIGGER_AFTER_CREATE == typeId || 
								ENTITY_TYPE_CLS_TRIGGER_BEFORE_DELETE == typeId || ENTITY_TYPE_CLS_TRIGGER_AFTER_DELETE == typeId) {
							KrnClass cls = db.getClassByUid(entityUid);
							if (cls != null) {
								KrnVcsChange change = new KrnVcsChange(cls, (int) typeId, userObj, dateChange, getMemo(rs, 10));
								change.title = getString(rs, 11);
								change.isTrigger = true;
		                        if (Constants.VCS_IMPORT == isFixd && rs.getLong(8) > 0) 
		                            change.importId = rs.getLong(8);
		                        if (Constants.VCS_EXPORT == isFixd && rs.getLong(9) > 0) 
		                        	change.exportId = rs.getLong(9);
		    					if(rs.getTimestamp(7)!=null)
		    						change.dateConfirm = new KrnDate(rs.getTimestamp(7).getTime());
								res.add(change);
							}
						} else if (ENTITY_TYPE_ATTR_TRIGGER_BEFORE_CHANGE == typeId || ENTITY_TYPE_ATTR_TRIGGER_AFTER_CHANGE == typeId || 
								ENTITY_TYPE_ATTR_TRIGGER_BEFORE_DELETE == typeId || ENTITY_TYPE_ATTR_TRIGGER_AFTER_DELETE == typeId) {
							KrnAttribute attr = db.getAttributeByUid(entityUid);
							if (attr != null) {
								KrnVcsChange change = new KrnVcsChange(attr, (int) typeId, userObj, dateChange, getMemo(rs, 10));
								change.title = getString(rs, 11);
								change.isTrigger = true;
		                        if (Constants.VCS_IMPORT == isFixd && rs.getLong(8) > 0) 
		                            change.importId = rs.getLong(8);
		                        if (Constants.VCS_EXPORT == isFixd && rs.getLong(9) > 0) 
		                        	change.exportId = rs.getLong(9);
		    					if(rs.getTimestamp(7)!=null)
		    						change.dateConfirm = new KrnDate(rs.getTimestamp(7).getTime());
								res.add(change);
							}
						}
					}
				}
			} finally {
				DbUtils.closeQuietly(rs);
				DbUtils.closeQuietly(selPst);
			}
			return res;
		} catch (SQLException ex) {
			log.error(sql);
			log.error(ex, ex);
			throw convertException(ex);
		}
	}

	@Override
	public List<KrnVcsChange> getVcsHistoryDataChanges(boolean isModel, String uid, int typeId,boolean isLastChange) throws DriverException {
		try { 
			List<KrnVcsChange> res = new ArrayList<KrnVcsChange>();
			String userTableName = getClassTableName(db.getClassByName("User"), true);
			if(!isModel){
				PreparedStatement selPst = conn.prepareStatement(
						"SELECT v.c_id,v.c_user_id,u.c_uid,u.c_class_id,v.c_obj_id,v.c_obj_uid,v.c_obj_class_id,v.c_mod_last_time,v.c_mod_confirm_time,v.c_fix_comment,v.c_attr_id,v.c_lang_id"
						+ " FROM " + getDBPrefix() + "t_vcs_objects v"
						+ " LEFT JOIN " + userTableName + " u ON u.c_obj_id=v.c_user_id AND u.c_tr_id=0"
						+ " WHERE v.c_obj_id=?");
				ResultSet rs = null;
				try {
					selPst.setLong(1, Long.parseLong(uid));
					rs = selPst.executeQuery();
					while (rs.next()) {
						KrnObject userObj = new KrnObject(rs.getLong(2), getSanitizedString(rs, 3), rs.getLong(4));
						
						String objUid = getSanitizedString(rs, 6);
						objUid = objUid != null ? objUid.trim() : null;
						
						KrnObject obj = new KrnObject(rs.getLong(5), objUid, rs.getLong(7));
						KrnDate dateChange = rs.getTimestamp(8) != null ? new KrnDate(rs.getTimestamp(8).getTime()) : null;
						KrnVcsChange change=new KrnVcsChange(rs.getLong(1),obj, userObj, dateChange, getMemo(rs, 10),rs.getLong(11),rs.getLong(12));
    					if(rs.getTimestamp(9)!=null)
    						change.dateConfirm = new KrnDate(rs.getTimestamp(9).getTime());
						res.add(change);
					}
				} finally {
					DbUtils.closeQuietly(rs);
					DbUtils.closeQuietly(selPst);
				}
			}else{
				String sql="SELECT v.c_id,v.c_user_id,u.c_uid,u.c_class_id,v.c_id,v.c_entity_id,v.c_type,v.c_action,v.c_mod_last_time,v.c_mod_confirm_time,v.c_fix_comment,v.c_name"
				+ " FROM " + getDBPrefix() + "t_vcs_model v"
				+ " LEFT JOIN " + userTableName + " u ON u.c_obj_id=v.c_user_id AND u.c_tr_id=0"
				+ " WHERE v.c_entity_id=? AND v.c_type = ?";
				if(isLastChange)
					sql+=" AND v.c_fix_end_id IS NULL";
				PreparedStatement selPst = conn.prepareStatement(sql);
				ResultSet rs = null;
				try {
					selPst.setString(1, uid);
					selPst.setInt(2, typeId);
					rs = selPst.executeQuery();
					while (rs.next()) {
						KrnObject userObj = new KrnObject(rs.getLong(2), getSanitizedString(rs, 3), rs.getLong(4));
						KrnVcsChange change=null;
						KrnDate dateChange = rs.getTimestamp(9) != null ? new KrnDate(rs.getTimestamp(9).getTime()) : null;
						String entityUid = getSanitizedString(rs, 6);
						entityUid = entityUid != null ? entityUid.trim() : null;

						if (ENTITY_TYPE_CLASS == typeId) {
							KrnClass cls = db.getClassByUid(entityUid);
							if (cls != null) {
								res.add(change = new KrnVcsChange(rs.getLong(1), cls, typeId, userObj, dateChange, getMemo(rs, 11)));
							}
						}else if (ENTITY_TYPE_ATTRIBUTE == typeId) {
							KrnAttribute attr = db.getAttributeByUid(entityUid);
							if (attr != null) {
								res.add(change = new KrnVcsChange(rs.getLong(1), attr, typeId, userObj, dateChange, getMemo(rs, 11)));
							}
						}else if (ENTITY_TYPE_METHOD == typeId) {
							KrnMethod obj = db.getMethodByUid(entityUid.trim());
							if (obj == null)
								obj = new KrnMethod(entityUid, getString(rs, 12), -1,"", false, 0);
							res.add(change = new KrnVcsChange(rs.getLong(1), obj, userObj, dateChange, getMemo(rs, 11)));
						} else if (ENTITY_TYPE_CLS_TRIGGER_BEFORE_CREATE == typeId || ENTITY_TYPE_CLS_TRIGGER_AFTER_CREATE == typeId || 
								ENTITY_TYPE_CLS_TRIGGER_BEFORE_DELETE == typeId || ENTITY_TYPE_CLS_TRIGGER_AFTER_DELETE == typeId) {
							KrnClass cls = db.getClassByUid(entityUid);
							if (cls != null) {
								res.add(change = new KrnVcsChange(rs.getLong(1), cls, typeId, userObj, dateChange, getMemo(rs, 11)));
							}
						} else if (ENTITY_TYPE_ATTR_TRIGGER_BEFORE_CHANGE == typeId || ENTITY_TYPE_ATTR_TRIGGER_AFTER_CHANGE == typeId || 
								ENTITY_TYPE_ATTR_TRIGGER_BEFORE_DELETE == typeId || ENTITY_TYPE_ATTR_TRIGGER_AFTER_DELETE == typeId) {
							KrnAttribute attr = db.getAttributeByUid(entityUid);
							if (attr != null) {
								res.add(change = new KrnVcsChange(rs.getLong(1), attr, typeId, userObj, dateChange, getMemo(rs, 11)));
							}

						}
    					if(change!=null && rs.getTimestamp(10)!=null)
    						change.dateConfirm = new KrnDate(rs.getTimestamp(10).getTime());
					}
				} finally {
					DbUtils.closeQuietly(rs);
					DbUtils.closeQuietly(selPst);
				}
			}
			return res;
		} catch (SQLException ex) {
			log.error(ex, ex);
			throw convertException(ex);
		}
	}

	@Override
	public List<KrnVcsChange> getVcsDifDataChanges(boolean isModel, long[] ids) throws DriverException {
		try { 
			List<KrnVcsChange> res = new ArrayList<KrnVcsChange>();
			String userTableName = getClassTableName(db.getClassByName("User"), true);
			String ids_="";
			if(ids!=null && ids.length>0){
				ids_+="?";
				for(int i=1;i<ids.length;i++){
					ids_+=",?";
				}
			}
			if(!isModel){
				String sql=	"SELECT v.c_id,v.c_user_id,u.c_uid,u.c_class_id,v.c_obj_id,v.c_obj_uid,v.c_obj_class_id,v.c_mod_last_time,v.c_name,v.c_attr_id,v.c_lang_id"
						+ " FROM " + getDBPrefix() + "t_vcs_objects v"
						+ " LEFT JOIN " + userTableName + " u ON u.c_obj_id=v.c_user_id AND u.c_tr_id=0"
						+ " WHERE v.c_dif IS NOT NULL";
				if(!"".equals(ids_))
					sql+=" AND v.c_id IN ("+ids_+")";
				PreparedStatement selPst = conn.prepareStatement(sql);
					
				ResultSet rs = null;
				try {
					if(!"".equals(ids_))
						for(int i=0;i<ids.length;i++)
							selPst.setLong(i+1, ids[i]);
					rs = selPst.executeQuery();
					while (rs.next()) {
						KrnObject userObj = new KrnObject(rs.getLong(2), getSanitizedString(rs, 3), rs.getLong(4));
						String objUid = getSanitizedString(rs, 6);
						objUid = objUid != null ? objUid.trim() : null;
						KrnObject obj = new KrnObject(rs.getLong(5), objUid, rs.getLong(7));
						KrnDate dateChange = rs.getTimestamp(8) != null ? new KrnDate(rs.getTimestamp(8).getTime()) : null;
						KrnVcsChange change=new KrnVcsChange(rs.getLong(1),obj, userObj, dateChange, "",rs.getLong(10),rs.getLong(11));
						change.title=rs.getString(9);
						res.add(change);
					}
				} finally {
					DbUtils.closeQuietly(rs);
					DbUtils.closeQuietly(selPst);
				}
			}else{
				String sql="SELECT v.c_id,v.c_user_id,u.c_uid,u.c_class_id,v.c_id,v.c_entity_id,v.c_type,v.c_action,v.c_mod_last_time,v.c_fix_comment,v.c_name"
				+ " FROM " + getDBPrefix() + "t_vcs_model v"
				+ " LEFT JOIN " + userTableName + " u ON u.c_obj_id=v.c_user_id AND u.c_tr_id=0"
				+ " WHERE v.c_dif IS NOT NULL";
				if(!"".equals(ids_))
					sql+=" AND v.c_id IN ("+ids_+")";
				PreparedStatement selPst = conn.prepareStatement(sql);
				ResultSet rs = null;
				try {
					if(!"".equals(ids_))
						for(int i=0;i<ids.length;i++)
							selPst.setLong(i+1, ids[i]);
					rs = selPst.executeQuery();
					while (rs.next()) {
						int typeId=rs.getInt(7);
						KrnObject userObj = new KrnObject(rs.getLong(2), getSanitizedString(rs, 3), rs.getLong(4));
						KrnVcsChange change=null;
						KrnDate dateChange = rs.getTimestamp(9) != null ? new KrnDate(rs.getTimestamp(9).getTime()) : null;
						String entityUid = getSanitizedString(rs, 6);
						entityUid = entityUid != null ? entityUid.trim() : null;
						
						if (ENTITY_TYPE_METHOD == typeId) {
							KrnMethod obj = db.getMethodByUid(entityUid);
							if (obj == null)
								obj = new KrnMethod(entityUid, getString(rs, 11), -1,"", false, 0);
							change=new KrnVcsChange(rs.getLong(1), obj, userObj, dateChange, getMemo(rs, 10));
							res.add(change);
						} else if (ENTITY_TYPE_CLS_TRIGGER_BEFORE_CREATE == typeId || ENTITY_TYPE_CLS_TRIGGER_AFTER_CREATE == typeId || 
								ENTITY_TYPE_CLS_TRIGGER_BEFORE_DELETE == typeId || ENTITY_TYPE_CLS_TRIGGER_AFTER_DELETE == typeId) {
							KrnClass cls = db.getClassByUid(entityUid);
							if (cls != null) {
								change=new KrnVcsChange(rs.getLong(1), cls, typeId, userObj, dateChange, getMemo(rs, 10));
								res.add(change);
							}
						} else if (ENTITY_TYPE_ATTR_TRIGGER_BEFORE_CHANGE == typeId || ENTITY_TYPE_ATTR_TRIGGER_AFTER_CHANGE == typeId || 
								ENTITY_TYPE_ATTR_TRIGGER_BEFORE_DELETE == typeId || ENTITY_TYPE_ATTR_TRIGGER_AFTER_DELETE == typeId) {
							KrnAttribute attr = db.getAttributeByUid(entityUid);
							if (attr != null) {
								change=new KrnVcsChange(rs.getLong(1), attr, typeId, userObj, dateChange, getMemo(rs, 10));
								res.add(change);
							}

						}
						if(change!=null) change.title=rs.getString(11);
					}
				} finally {
					DbUtils.closeQuietly(rs);
					DbUtils.closeQuietly(selPst);
				}
			}
			return res;
		} catch (SQLException ex) {
			log.error(ex, ex);
			throw convertException(ex);
		}
	}

	@Override
	public String getVcsHistoryDataIncrement(KrnVcsChange change) throws DriverException {
		String res="";
		PreparedStatement selPst=null;
		ResultSet rs = null;
		try {
			if (change.cvsChangeMethod!=null || change.cvsChangeClass!=null || change.cvsChangeAttr!=null) {
				selPst = conn.prepareStatement("SELECT c_dif FROM " + getDBPrefix() + "t_vcs_model WHERE c_id=?");
				selPst.setLong(1, change.id);
			} else if(change.cvsChangeObj!=null) {
				selPst = conn.prepareStatement("SELECT c_dif FROM " + getDBPrefix() + "t_vcs_objects WHERE c_id=?");
				selPst.setLong(1, change.id);
			}
			rs = selPst.executeQuery();
			if(rs.next()) {
				byte[] val = (byte[])getValue(rs, "c_dif", PC_BLOB);
				res = val != null ? new String(val, "UTF-8") : null;
			}
		} catch (UnsupportedEncodingException ex) {
			log.error(ex, ex);
		} catch (SQLException ex) {
			try {
				throw convertException(ex);
			} catch (DriverException e) {
				log.error(e, e);
			}
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(selPst);
		}
		return res;
	}
	
	protected void logVcsDataChanges(KrnObject obj, long attrId, long langId, Object value,long trId) throws DriverException, SQLException {
		if(obj==null) {
			log.info("Внимание! Передан нулевой объект на обработку.");
			return;
		}
    	if (!isUpgrading && version >= 31 && db.isVcsClass(obj.classId)) {
    		if (!isImportState && !isLoadingFile && db.isDbReadOnly()) throw new DriverException("База не предназначена для изменений!");
    		
    		KrnAttribute attr = db.getAttributeById(attrId);
    		boolean isRepl = attrId == 1 || attrId == 2 || attr.isRepl;
    		if (isRepl) {
				boolean needCreate = false;
				PreparedStatement selPst = null;
				ResultSet rs = null;
				boolean isObjNew = false;
				try {
		    		if(isImportState){
		    			commitVcsObjectIfEditingBeforeReplication(obj.id, attrId, langId, "Autocommited before replication " + importObjId);
						needCreate = true;
		    		}else{
						String sql = "SELECT c_user_id FROM " + getDBPrefix() + "t_vcs_objects WHERE c_obj_id=? AND c_attr_id=? AND c_lang_id=? AND c_fix_end_id IS NULL";
						
		    			selPst = conn.prepareStatement(Funcs.sanitizeSQL(sql));
						selPst.setLong(1, obj.id);
						selPst.setLong(2, attrId);
						selPst.setLong(3, langId);
						rs = selPst.executeQuery();
						if (rs.next()) {
							long userId = rs.getLong(1);
							if (userId != us.getUserId()) {
								//TODO Получить имя пользователя и вернуть в тексте ошибки.
								throw new DriverException("Объект редактируется пользователем ID:" + userId);
							}
						} else {
							needCreate = true;
							if(attrId == 2) {
								DbUtils.closeQuietly(rs);
								DbUtils.closeQuietly(selPst);
								sql = "SELECT c_user_id FROM " + getDBPrefix() + "t_vcs_objects WHERE c_obj_id=? AND c_attr_id=? AND c_lang_id=? AND c_fix_end_id IS NULL";
								
				    			selPst = conn.prepareStatement(Funcs.sanitizeSQL(sql));
								selPst.setLong(1, obj.id);
								selPst.setLong(2, 1);
								selPst.setLong(3, 0);
								rs = selPst.executeQuery();
								if (rs.next()) {
									isObjNew = true;
								}
							}
						}
		    		}
				} catch (SQLException ex) {
					log.error(ex, ex);
					throw convertException(ex);
				} finally {
					DbUtils.closeQuietly(rs);
					DbUtils.closeQuietly(selPst);
				}
				if (needCreate) {
					createVcsDataRecord(obj, attr, langId, value,isObjNew,trId);
				} else {
					updateVcsDataRecord(obj, attr, langId, value,trId);
				}
    		}
    	}
	}
	
	public long checkVcsDataChanges(long objId) throws DriverException{
			PreparedStatement selPst = null;
			ResultSet rs = null;
			long res=-1;
			try {
				selPst = conn.prepareStatement(
						"SELECT DISTINCT c_user_id FROM " + getDBPrefix() + "t_vcs_objects"
						+ " WHERE c_obj_id=? AND c_fix_end_id IS NULL");
				selPst.setLong(1, objId);
				rs = selPst.executeQuery();
				if (rs.next()) {
					long userId = rs.getLong(1);
					res = userId;
				}else
					res=us.getUserId();
			} catch (SQLException ex) {
				log.error(ex, ex);
				throw convertException(ex);
			} finally {
				DbUtils.closeQuietly(rs);
				DbUtils.closeQuietly(selPst);
			}
			return res;
	}
	
	public long checkVcsModelChanges(String uid, int modelChangeType) throws DriverException{
		PreparedStatement selPst = null;
		ResultSet rs = null;
		long res=-1;
		try {
			selPst = conn.prepareStatement("SELECT DISTINCT c_user_id FROM " + getDBPrefix() + "t_vcs_model WHERE c_entity_id=? AND c_type=? AND c_fix_end_id IS NULL");
			selPst.setString(1, uid);
			selPst.setInt(2, modelChangeType);
			rs = selPst.executeQuery();
			if (rs.next()) {
				long userId = rs.getLong(1);
				res = userId;
			}else
				res=us.getUserId();
		} catch (SQLException ex) {
			log.error(ex, ex);
			throw convertException(ex);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(selPst);
		}
		return res;
	}
	
	protected void createVcsDataRecord(KrnObject obj, KrnAttribute attr, long langId, Object value, boolean isNewObj,long trId) throws DriverException, SQLException {
		//Сначала создаем записи для атрибутов удаляемого объекта
		if(attr.id==2 && !isNewObj){
			List<KrnAttribute> attrs=db.getVcsAttributes(obj.classId);
			List<KrnObject> langs=getSystemLangs();
			for(KrnAttribute oattr:attrs){
				if(oattr.isMultilingual){
					for(KrnObject lang:langs){
						createVcsDataRecord(obj, oattr, lang.id, null,false,trId);
					}
				}else
					createVcsDataRecord(obj, oattr, 0, null,false,trId);
					
			}
		}
		KrnAttribute tattr=db.getTitleAttribut(obj.classId);
		long tlangId=0;
		if(tattr.isMultilingual){
			tlangId=getSystemLangs().get(0).id;
		}
		String obj_name = Funcs.sanitizeSQL((String) getValue(obj.id, tattr.id, 0, tlangId, trId));
		String sql = "";
		if(version < VERSION_UL){
			sql = "INSERT INTO " + getDBPrefix() + "t_vcs_objects"
				+ "(c_obj_id,c_obj_uid,c_obj_class_id,c_attr_id,c_lang_id,c_old_value,c_dif,c_user_id,c_ip,c_name,c_repl_id,c_fix_start_id)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,"
				+ "(SELECT MAX(c_id) FROM " + getDBPrefix() + "t_changes WHERE c_object_id=? AND c_attr_id=? AND c_lang_id=?))";
		}else{
			sql = "INSERT INTO " + getDBPrefix() + "t_vcs_objects"
					+ "(c_obj_id,c_obj_uid,c_obj_class_id,c_attr_id,c_lang_id,c_old_value,c_dif,c_user_id,c_ip,c_name,c_rimport_id,c_fix_start_id)"
					+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,"
					+ "(SELECT MAX(c_id) FROM " + getDBPrefix() + "t_changes WHERE c_object_id=? AND c_attr_id=? AND c_lang_id=?))";
		}
		PreparedStatement pst = conn.prepareStatement(sql);
		try {
			pst.setLong(1, obj.id);
			pst.setString(2, obj.uid);
			pst.setLong(3, obj.classId);
			pst.setLong(4, attr.id);
			pst.setLong(5, langId);
			
			StringBuilder diff = new StringBuilder();
			setValue(pst, 6, PC_BLOB, packValue(obj, attr, langId, value, diff,trId));
			if (diff.length() > 0) {
				try {
					setValue(pst, 7, PC_BLOB, diff.toString().getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					log.error(e, e);
				}
			} else {
				setValue(pst, 7, PC_BLOB, null);
			}
			
			pst.setLong(8, us.getUserId());
			pst.setString(9, us.getIp());
			pst.setString(10, obj_name);
			if (isImportState) {
				pst.setLong(11,importObjId);
			} else {
				pst.setNull(11, Types.BIGINT);
			}
			pst.setLong(12, obj.id);
			pst.setLong(13, attr.id);
			pst.setLong(14, langId);
			pst.executeUpdate();
		} catch (SQLException ex) {
			log.error(sql);
			log.error(ex, ex);
			throw convertException(ex);
		} finally {
			DbUtils.closeQuietly(pst);
		}
	}
	
	protected void updateVcsDataRecord(KrnObject obj, KrnAttribute attr, long langId, Object value,long trId) throws DriverException, SQLException {
		String diff = "";
		if (db.isVcsDiffAttr(attr.id)) {
			diff = getVcsDataDiff(obj, attr, langId, (byte[])value);
		}
		PreparedStatement pst = conn.prepareStatement(
				"UPDATE " + Funcs.sanitizeSQL(getDBPrefix()) + "t_vcs_objects SET c_mod_last_time=?,c_dif=?"
						+ " WHERE c_obj_id=? AND c_attr_id=? AND c_lang_id=? AND c_fix_end_id IS NULL");
		try {
			pst.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
			if (diff.length() > 0) {
				try {
					setValue(pst, 2, PC_BLOB, diff.toString().getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					log.error(e, e);
				}
			} else {
				pst.setNull(2, Types.BLOB);
			}
			pst.setLong(3, obj.id);
			pst.setLong(4, attr.id);
			pst.setLong(5, langId);
			pst.executeUpdate();
		} catch (SQLException ex) {
			log.error(ex, ex);
			throw convertException(ex);
		} finally {
			DbUtils.closeQuietly(pst);
		}
	}

	protected byte[] packValue(KrnObject obj, KrnAttribute attr, long langId, Object newValue, StringBuilder diff,long trId) throws DriverException {
		if (attr.id == 1 || attr.id == 2) {
			return null;
		}
		SortedSet<Value> vs = getValues(new long[] { obj.id }, null, attr.id, langId, trId);
		if (vs.size() > 0) {
			try {
				Element values = new Element("values");
				for (Value v : vs) {
					Element value = new Element("value");
					value.setAttribute("i", "" + v.index);
					String content = toString(attr.typeClassId, v.value);
					if (content == null) {
						value.setAttribute("null", "true");
					} else {
						value.setText(content);
					}
					values.addContent(value);
					
					if (db.isVcsDiffAttr(attr.id) && newValue != null) {
						getXmlDiff((byte[])newValue, (byte[])v.value, diff);
					}
				}
				return XmlUtil.write(values);
			} catch (Exception ex) {
				throw new DriverException("Failed to serialize old value to XML.", ex);
			}
		}
		return null;
	}

	public void commitVcsObjectIfEditingBeforeReplication(long objId, long attrId, long langId, String comment) throws DriverException {
		try {
			String pfx1 = Funcs.sanitizeSQL(getDBPrefix());
			String pfx2 = Funcs.sanitizeFileName(getDBPrefix());
			String pfx3 = Funcs.sanitizeLDAP(getDBPrefix());
			
			PreparedStatement selPst = conn.prepareStatement(
					"SELECT c_obj_uid,c_obj_class_id,c_user_id,c_ip,c_id FROM " + pfx1 + "t_vcs_objects"
					+ " WHERE c_obj_id=? AND c_attr_id=? AND c_lang_id=? AND c_fix_end_id IS NULL ORDER BY c_id");
			
			PreparedStatement chPst = conn.prepareStatement(
					"INSERT INTO " + pfx2 + "t_changes (c_object_id,c_object_uid,c_class_id,c_attr_id,c_lang_id,c_tr_id,c_is_repl,c_user_id,c_ip)"
					+ " VALUES (?,?,?,?,?,0,1,?,?)");
			
			PreparedStatement vcsPst = conn.prepareStatement("UPDATE " + pfx3 + "t_vcs_objects SET c_fix_end_id=?,c_fix_comment=? WHERE c_id=?");

			ResultSet rs = null;
			try {
				selPst.setLong(1, objId);
				selPst.setLong(2, attrId);
				selPst.setLong(3, langId);
				rs = selPst.executeQuery();
				while (rs.next()) {
					chPst.setLong(1, objId);
					chPst.setString(2, getSanitizedString(rs, 1));
					chPst.setLong(3, rs.getLong(2));
					chPst.setLong(4, attrId);
					chPst.setLong(5, langId);
					chPst.setLong(6, rs.getLong(3));
					chPst.setString(7, getSanitizedString(rs, 4));
					chPst.executeUpdate();
					
					vcsPst.setLong(1, getLastInsertId());
					setValue(vcsPst, 2, PC_MEMO, Funcs.sanitizeSQL(comment));
					vcsPst.setLong(3, rs.getLong(5));
					vcsPst.executeUpdate();
				}
				rs.close();
			} finally {
				DbUtils.closeQuietly(rs);
				DbUtils.closeQuietly(vcsPst);
				DbUtils.closeQuietly(chPst);
				DbUtils.closeQuietly(selPst);
			}
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		}
	}

	@Override
	public void commitVcsObjectsAfterReplication(long replId, String comment) throws DriverException {
		commitVcsModelAfterReplication(replId, comment);
		try {
			PreparedStatement selPst = null;
			if(version < VERSION_UL){
				selPst = conn.prepareStatement(
					"SELECT c_obj_id,c_obj_uid,c_obj_class_id,c_attr_id,c_lang_id,c_user_id,c_ip,c_id FROM " + getDBPrefix() + "t_vcs_objects"
					+ " WHERE c_repl_id=? AND c_fix_end_id IS NULL ORDER BY c_id");
			}else{
				selPst = conn.prepareStatement(
						"SELECT c_obj_id,c_obj_uid,c_obj_class_id,c_attr_id,c_lang_id,c_user_id,c_ip,c_id FROM " + getDBPrefix() + "t_vcs_objects"
						+ " WHERE c_rimport_id=? AND c_fix_end_id IS NULL ORDER BY c_id");
			}
			
			PreparedStatement chPst = conn.prepareStatement(
					"INSERT INTO " + getDBPrefix() + "t_changes (c_object_id,c_object_uid,c_class_id,c_attr_id,c_lang_id,c_tr_id,c_is_repl,c_user_id,c_ip)"
					+ " VALUES (?,?,?,?,?,0,1,?,?)");
			
			PreparedStatement vcsPst = conn.prepareStatement("UPDATE " + getDBPrefix() + "t_vcs_objects SET c_fix_end_id=?,c_fix_comment=? WHERE c_id=?");

			ResultSet rs = null;
			try {
				selPst.setLong(1, replId);
				rs = selPst.executeQuery();
				while (rs.next()) {
					chPst.setLong(1, rs.getLong(1));
					chPst.setString(2, getSanitizedString(rs, 2));
					chPst.setLong(3, rs.getLong(3));
					chPst.setLong(4, rs.getLong(4));
					chPst.setLong(5, rs.getLong(5));
					chPst.setLong(6, rs.getLong(6));
					chPst.setString(7, getSanitizedString(rs, 7));
					chPst.executeUpdate();
					
					vcsPst.setLong(1, getLastInsertId());
					setValue(vcsPst, 2, PC_MEMO, comment);
					vcsPst.setLong(3, rs.getLong(8));
					vcsPst.executeUpdate();
				}
				rs.close();
			} finally {
				DbUtils.closeQuietly(rs);
				DbUtils.closeQuietly(vcsPst);
				DbUtils.closeQuietly(chPst);
				DbUtils.closeQuietly(selPst);
			}
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		}
	}

	@Override
	public void commitVcsObjects(List<KrnVcsChange> changes, String comment) throws DriverException {
		commitVcsModel(changes, comment);
		try {
			PreparedStatement selPst = conn.prepareStatement(
					"SELECT c_obj_id,c_obj_uid,c_obj_class_id,c_attr_id,c_lang_id,c_user_id,c_ip,c_id FROM " + getDBPrefix() + "t_vcs_objects"
					+ " WHERE c_obj_id=? AND c_fix_end_id IS NULL ORDER BY c_id");
			
			PreparedStatement chPst = conn.prepareStatement(
					"INSERT INTO " + getDBPrefix() + "t_changes (c_object_id,c_object_uid,c_class_id,c_attr_id,c_lang_id,c_tr_id,c_is_repl,c_user_id,c_ip)"
					+ " VALUES (?,?,?,?,?,0,1,?,?)");
			
			PreparedStatement vcsPst = conn.prepareStatement("UPDATE " + getDBPrefix() + "t_vcs_objects SET c_fix_end_id=?,c_mod_confirm_time=?,c_fix_comment=? WHERE c_id=?");

			PreparedStatement logDataPst = conn.prepareStatement("INSERT INTO " + getDBPrefix() + "t_changes (c_class_id,c_object_id,c_object_uid,c_attr_id,c_lang_id,c_tr_id,c_is_repl) VALUES (?,?,?,?,?,?,?)");
			
			ResultSet rs = null;
			try {
				for (KrnVcsChange change : changes) {
					KrnObject obj = change.cvsChangeObj;
					if (obj != null) {
						boolean isCreating = false;
						selPst.setLong(1, obj.id);
						rs = selPst.executeQuery();
						while (rs.next()) {
							chPst.setLong(1, rs.getLong(1));
							chPst.setString(2, getSanitizedString(rs, 2));
							chPst.setLong(3, rs.getLong(3));
							chPst.setLong(4, rs.getLong(4));
							if (rs.getLong(4) == 1) {
								isCreating = true;
							}
							chPst.setLong(5, rs.getLong(5));
							chPst.setLong(6, rs.getLong(6));
							chPst.setString(7, getSanitizedString(rs, 7));
							chPst.executeUpdate();
							
							vcsPst.setLong(1, getLastInsertId());
							vcsPst.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
							setValue(vcsPst, 3, PC_MEMO, comment);
							vcsPst.setLong(4, rs.getLong(8));
							vcsPst.executeUpdate();
						}
						rs.close();
						
						if (isCreating) {
							KrnClass cls = getClassByIdComp(obj.classId);
							List<KrnAttribute> attrs = getAttributesOfType(cls);
							for (KrnAttribute attr: attrs) {
								List<KrnObject> objsWithRef = getObjectsByAttribute(attr.classId, attr.id, 0, 0, obj, 0);
								for (KrnObject objWithRef: objsWithRef) {
									logDataPst.setLong(1, objWithRef.classId);
									logDataPst.setLong(2, objWithRef.id);
									logDataPst.setString(3, Funcs.sanitizeSQL(objWithRef.uid));
									logDataPst.setLong(4, attr.id);
									logDataPst.setLong(5, 0);
									logDataPst.setLong(6, 0);
									logDataPst.setBoolean(7, true);
									logDataPst.executeUpdate();
								}
							}
						}
					}
				}
			} finally {
				DbUtils.closeQuietly(rs);
				DbUtils.closeQuietly(vcsPst);
				DbUtils.closeQuietly(chPst);
				DbUtils.closeQuietly(selPst);
			}
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		}
	}
	
	private List<KrnAttribute> getAttributesOfType(KrnClass cls) throws DriverException {
    	List<KrnAttribute> res = new ArrayList<KrnAttribute>();
		try {
			QueryRunner qr = new QueryRunner(true);
			AttributeRsh rh = new AttributeRsh();
			List<KrnAttribute> res_ = qr.query(conn, "SELECT * FROM " + getDBPrefix() + "t_attrs WHERE c_type_id=? AND c_is_repl=?", rh, new Object[] {cls.id, 1});
			for (KrnAttribute a : res_) {
				res.add(a);
			}
			KrnClass cls_ = cls;
			while (cls_.parentId > 0) {
	    		cls_ = getClassByIdComp(cls_.parentId);
	    		if (cls_.modifier == 0) {
					res_ = (List<KrnAttribute>) qr.query(conn, "SELECT * FROM " + getDBPrefix() + "t_attrs WHERE c_type_id=? AND c_is_repl=?", rh, new Object[] {cls_.id, 1});
					for (KrnAttribute a : res_) {
						res.add(a);
					}
	    		}
			}
		} catch (SQLException e) {
			throw convertException(e);
		}
    	return res;
    }

    @Override
    public boolean setVcsUserForObject(KrnVcsChange change,long userId) throws DriverException{
            String sql = null;
            PreparedStatement setPst = null;
            boolean res=false;
            try {
                    try{
                            if(change.cvsChangeObj!=null){
                                    sql =   "UPDATE " + getDBPrefix() + "t_vcs_objects SET c_old_user_id=?,c_user_id=? "
                                            + " WHERE c_fix_end_id IS NULL AND c_user_id=? AND c_obj_uid=?";
                                    setPst = conn.prepareStatement(sql);
                                    setPst.setLong(1, change.user.id);
                                    setPst.setLong(2, userId);
                                    setPst.setLong(3, change.user.id);
                                    setPst.setString(4, change.cvsChangeObj.uid);
                                    setPst.executeUpdate();
                                    res=true;
                            }else if(change.cvsChangeMethod!=null){
                                    sql =   "UPDATE " + getDBPrefix() + "t_vcs_model SET c_old_user_id=?,c_user_id=? "
                                                    + " WHERE c_fix_end_id IS NULL AND c_user_id=? AND c_entity_id=?";
                                    setPst = conn.prepareStatement(sql);
                                    setPst.setLong(1, change.user.id);
                                    setPst.setLong(2, userId);
                                    setPst.setLong(3, change.user.id);
                                    setPst.setString(4, change.cvsChangeMethod.uid);
                                    setPst.executeUpdate();
                                    res=true;
                            }
                    } finally {
                            DbUtils.closeQuietly(setPst);
                    }
            } catch (SQLException ex) {
                    log.error(sql);
                    log.error(ex, ex);
                    throw convertException(ex);
            }
            return res;
    }
	@Override
	public void rollbackVcsObjects(List<KrnVcsChange> changes, Session session) throws DriverException {
		UserSession uss = session.getUserSession();
		rollbackVcsModel(changes,uss);
		try {
			PreparedStatement selPst = conn.prepareStatement(
					"SELECT c_obj_id,c_obj_uid,c_obj_class_id,c_attr_id,c_lang_id,c_old_value,c_id FROM " + getDBPrefix() + "t_vcs_objects"
					+ " WHERE c_obj_id=? AND c_fix_end_id IS NULL ORDER BY c_id DESC");
			
			PreparedStatement delPst = conn.prepareStatement("DELETE FROM " + getDBPrefix() + "t_vcs_objects WHERE c_id=?");

			// Отключаем логирование изменений
			isUpgrading = true;
			ResultSet rs = null;
			try {
				KrnClass uiCls = db.getClassByName("UI");
				for (KrnVcsChange change : changes) {
					if (change.cvsChangeObj != null) {
						boolean created = false;
						selPst.setLong(1, change.cvsChangeObj.id);
						rs = selPst.executeQuery();
						while (rs.next()) {
							KrnObject obj = new KrnObject(rs.getLong(1), getSanitizedString(rs, 2), rs.getLong(3));
							KrnAttribute attr = db.getAttributeById(rs.getLong(4));
							if (attr.id == 1) {
								deleteObject(obj, 0, isUpgrading);
								created = true;
							}else if(attr.id == 2){
								createObject(obj.classId, 0, obj.id, obj.uid);
							}
							if (!created) {
								long langId = rs.getLong(5);
								byte[] oldValue = (byte[])getValue(rs, "c_old_value", PC_BLOB);
								
								if (attr.collectionType == COLLECTION_ARRAY) {
									SortedSet<Value> vs = getValues(new long[] {obj.id}, null, attr.id, langId, 0);
									if (vs.size() > 0) {
										int[] indices = new int[vs.size()];
										int i = 0;
										for (Value v : vs) {
											indices[i] = v.index;
										}
										deleteValueImpl(obj, attr, indices, langId, 0, false);
									}
								} else if (attr.collectionType == COLLECTION_SET) {
									SortedSet<Value> vs = getValues(new long[] {obj.id}, null, attr.id, langId, 0);
									if (vs.size() > 0) {
										Collection<Object> values = new ArrayList<Object>();
										for (Value v : vs) {
											values.add(v.value);
										}
										deleteValueImpl(obj, attr, values, 0, false);
									}
								}
								if (oldValue != null) {
									try {
										List<Element> values = XmlUtil.read(oldValue).getChildren();
										for (Element value : values) {
											int i = Integer.parseInt(value.getAttributeValue("i"));
											if ("true".equals(value.getAttributeValue("null"))) {
												if (attr.collectionType == COLLECTION_NONE) {
													setValueImpl(obj, attr, i, langId, 0, null, false);
												}
											} else {
												setValueImpl(obj, attr, i, langId, 0, fromString(attr.typeClassId, value.getText()), false);
											}
										}
									} catch (Exception ex) {
										throw new DriverException("Failed to parse value from XML.", ex);
									}
								}
							}							
							delPst.setLong(1, rs.getLong(7));
							delPst.executeUpdate();
						}
						this.log.info("Откат изменений по объекту uid:"+change.cvsChangeObj.uid+";userName:"+uss.getUserName()+";userIp:"+uss.getIp());
						rs.close();
						if (uiCls != null && uiCls.id == change.cvsChangeObj.classId) {
							session.interfaceChanged(change.cvsChangeObj.id);
						}
					}
				}
			} finally {
				DbUtils.closeQuietly(rs);
				DbUtils.closeQuietly(delPst);
				DbUtils.closeQuietly(selPst);
				// Включаем логирование изменений
				isUpgrading = false;
			}
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		}
	}
	
	protected void logVcsModelChanges(int type, int action, Object oldObj, Object newObj, byte[] newExpr, Connection conn) throws DriverException, SQLException {
    	if (!isUpgrading && version >= 31 ) {
    		if (!isImportState && !isLoadingFile && db.isDbReadOnly()) throw new DriverException("База не предназначена для изменений!");

    		boolean needCreate = false;
    		// Действие с которого началась последняя незакоммиченная история
    		int vcsAction = action;
			PreparedStatement selPst = null;
			ResultSet rs = null;
			String uid=getVcsUid(newObj);
			try {
	    		if(isImportState){
	    			commitVcsModelIfEditingBeforeReplication(uid, type, "Autocommited before replication " + importObjId);
	    			if(version < VERSION_UL){
	    				selPst = conn.prepareStatement("SELECT c_repl_id FROM " + getDBPrefix() + "t_vcs_model" + " WHERE c_type=? AND c_entity_id=? AND c_repl_id=? AND c_fix_end_id IS NULL");
	    			}else{
		    			selPst = conn.prepareStatement("SELECT c_rimport_id FROM " + getDBPrefix() + "t_vcs_model" + " WHERE c_type=? AND c_entity_id=? AND c_rimport_id=? AND c_fix_end_id IS NULL");
	    			}
					selPst.setLong(1, type);
					selPst.setString(2, uid);
					selPst.setLong(3, importObjId);
					rs = selPst.executeQuery();
					if (!rs.next()) {
						needCreate = true;
					}
	    		}else{
	    			selPst = conn.prepareStatement("SELECT c_user_id, c_action FROM " + getDBPrefix() + "t_vcs_model" + " WHERE c_type=? AND c_entity_id=? AND c_fix_end_id IS NULL");
					selPst.setLong(1, type);
					selPst.setString(2, uid);
					rs = selPst.executeQuery();
					if (rs.next()) {
						long userId = rs.getLong(1);
						vcsAction = rs.getInt(2);
						if (userId != us.getUserId()) {
							//TODO Получить имя пользователя и вернуть в тексте ошибки.
							throw new DriverException("Объект редактируется пользователем ID:" + userId);
						}
					} else {
						needCreate = true;
					}
	    		}
			} catch (SQLException e) {
				log.error(e, e);
				throw convertException(e);
			} finally {
				DbUtils.closeQuietly(rs);
				DbUtils.closeQuietly(selPst);
			}
			if (needCreate) {
				createVcsModelRecord(type, action, oldObj, newObj, newExpr, conn);
			} else if (vcsAction == ACTION_CREATE && action == ACTION_DELETE) {
				// Если было не закоммичено создание и идет удаление, то ничего не сохраняем в истории
				deleteVcsModelRecord(type, uid, conn);
			} else {
				updateVcsModelRecord(type, vcsAction, action, oldObj, newObj, newExpr, conn);
			}
			try{
				//Индексирование именений
				List<KrnVcsChange> changes=getVcsHistoryDataChanges(true,uid, type, true);
				if(changes.size()>0 && changes.get(0)!=null){
					byte[] dif=getVcsChangeExpression(changes.get(0));
					if(dif!=null && dif.length>0)
						Indexer.updateVcsChangesIndex(changes.get(0),dif, "UTF-8");
				}
			}catch(Throwable t){
				log.error(t, t);
			}
    	}
	}
	protected String getVcsUid(Object newObj){
		String uid="-1";
		if(newObj instanceof KrnMethod)
			uid=((KrnMethod)newObj).uid;
		else if(newObj instanceof KrnClass)
			uid=((KrnClass)newObj).uid;
		else if(newObj instanceof KrnAttribute)
			uid=((KrnAttribute)newObj).uid;
		else if(newObj instanceof String)
			uid=(String)newObj;
		return uid;
	}
	protected String getName(String entityUid, int entityType,Object newObj) {
		if(entityType==ENTITY_TYPE_METHOD)
			return ((KrnMethod)newObj).name;
		else if(entityType==ENTITY_TYPE_CLASS)
			return ((KrnClass)newObj).name;
		else if(entityType==ENTITY_TYPE_ATTRIBUTE)
			return ((KrnAttribute)newObj).name;
		else if(entityType==ENTITY_TYPE_INDEX)
			return "INDEX_"+entityUid;
		else{
			StringBuilder sb = new StringBuilder();
			sb.append("Триггер '" + Utils.getTriggerNameByModelChangeType(entityType) + "'");
			sb.append(Utils.getOwnerTypeByModelChangeType(entityType) == 0 ? " класса '" + db.getClassByUid(entityUid).name + "'": " атрибута '" + db.getAttributeByUid(entityUid).name + "'") ;
			return sb.toString();
		}
	}
	
/*	// Для триггеров
	protected void logVcsModelChanges(String entityUid, int entityType, int action, byte[] oldExpr, byte[] newExpr, Connection conn) throws DriverException, SQLException {
		if (!isUpgrading && version >= 31 ) {
    		if (!isImportState && !isLoadingFile && db.isDbReadOnly()) throw new DriverException("База не предназначена для изменений!");

    		boolean needCreate = false;
    		// Действие с которого началась последняя незакоммиченная история
    		int vcsAction = action;
			PreparedStatement selPst = null;
			ResultSet rs = null;
			try {
	    		if(isImportState){
	    			// !Надо проверить
	    			commitVcsModelIfEditingBeforeReplication(entityUid, entityType, "Autocommited before replication " + importObjId);
	    			selPst = conn.prepareStatement("SELECT c_repl_id FROM " + getDBPrefix() + "t_vcs_model" + " WHERE c_type=? AND c_entity_id=? AND c_repl_id=? AND c_fix_end_id IS NULL");
					selPst.setLong(1, entityType);
					selPst.setString(2, entityUid);
					selPst.setLong(3, importObjId);
					rs = selPst.executeQuery();
					if (!rs.next()) {
						needCreate = true;
					}
	    		}else{
	    			selPst = conn.prepareStatement("SELECT c_user_id, c_action FROM " + getDBPrefix() + "t_vcs_model" + " WHERE c_type=? AND c_entity_id=? AND c_fix_end_id IS NULL");
					selPst.setLong(1, entityType);
					selPst.setString(2, entityUid);
					rs = selPst.executeQuery();
					if (rs.next()) {
						long userId = rs.getLong(1);
						vcsAction = rs.getInt(2);
						if (userId != us.getUserId()) {
							// Получить имя пользователя и вернуть в тексте ошибки.
							throw new DriverException("Объект редактируется пользователем ID:" + userId);
						}
					} else {
						needCreate = true;
					}
	    		}
			} catch (SQLException e) {
				log.error(e, e);
				throw convertException(e);
			} finally {
				DbUtils.closeQuietly(rs);
				DbUtils.closeQuietly(selPst);
			}
			if (needCreate) {
				createVcsModelRecord(entityUid, entityType, action, oldExpr, newExpr, conn);
			} else if (vcsAction == ACTION_CREATE && action == ACTION_DELETE) {
				// Если было не закоммичено создание и идет удаление, то ничего не сохраняем в истории
				deleteVcsModelRecord(entityUid, entityType, conn);
			} else {
				updateVcsModelRecord(entityUid, entityType, vcsAction, action, oldExpr, newExpr, conn);
			}
    	}
	}
	
	// Для триггеров
	protected void createVcsModelRecord(String entityUid, int entityType, int action, byte[] oldExpr, byte[] newExpr, Connection conn) throws DriverException, SQLException {
		PreparedStatement pst = conn.prepareStatement(
				"INSERT INTO " + getDBPrefix() + "t_vcs_model"
				+ "(c_entity_id,c_type,c_action,c_old_value,c_dif,c_user_id,c_ip,c_name,c_repl_id,c_fix_start_id)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,"
				+ "(SELECT MAX(c_id) FROM " + getDBPrefix() + "t_changescls WHERE c_entity_id=? AND c_type=?))");
		try {
			pst.setString(1, entityUid);
			pst.setInt(2, entityType);
			pst.setInt(3, action);
			
			setValue(pst, 4, PC_BLOB, packModelValue(action, oldExpr, newExpr));
			
			String diff = getDiff(oldExpr, newExpr, action);
			
			if (diff.length() > 0) {
				try {
					setValue(pst, 5, PC_BLOB, diff.toString().getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					log.error(e, e);
				}
			} else {
				pst.setNull(5, Types.BLOB);
			}
			
			pst.setLong(6, us.getUserId());
			pst.setString(7, us.getIp());
			
			pst.setString(8, getName(entityUid, entityType));
			if(isImportState){
				pst.setLong(9,importObjId);
			}else{
				pst.setNull(9, Types.BIGINT);
			}
			pst.setString(10, entityUid);
			pst.setInt(11, entityType);
			pst.executeUpdate();
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(pst);
		}
	}
	
	// Для триггеров
	protected byte[] packModelValue(int action, byte[] oldExpr, byte[] newExpr) throws DriverException {
		Element values = new Element("values");
		Element value = new Element("value");
		value.setAttribute("i", "content");
		String content = toString(PC_BLOB, action==2 ? newExpr : oldExpr);
		if (content == null) {
			value.setAttribute("null", "true");
		} else {
			value.setText(content);
		}
		values.addContent(value);
		try {
			return XmlUtil.write(values);
		} catch (IOException ex) {
			throw new DriverException("Failed to serialize old value to XML.", ex);
		}
	}
	
	// Для триггеров
	protected void deleteVcsModelRecord(String entityUid, int entityType, Connection conn) throws DriverException, SQLException {
		PreparedStatement pst = null;
		pst = conn.prepareStatement("DELETE FROM " + getDBPrefix() + "t_vcs_model WHERE c_entity_id=? AND c_type=? AND c_fix_end_id IS NULL");
		try {
			pst.setString(1, entityUid);
			pst.setLong(2, entityType);
			pst.executeUpdate();
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(pst);
		}
	}
	
	// Для триггеров
	protected void updateVcsModelRecord(String entityUid, int entityType, int vcsAction, int action, byte[] oldExpr, byte[] newExpr, Connection conn) throws DriverException, SQLException {
		PreparedStatement pst = null;
		pst = conn.prepareStatement("UPDATE " + getDBPrefix() + "t_vcs_model SET c_name=?,c_mod_last_time=?,c_dif=?,c_action=? WHERE c_entity_id=? AND c_type=? AND c_fix_end_id IS NULL");
		try {
			pst.setString(1, getName(entityUid, entityType));
			pst.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
			
			String diff = getVcsModelDiff(entityUid, entityType, action, oldExpr, newExpr);
			if (diff.length() > 0) {
				try {
					setValue(pst, 3, PC_BLOB, diff.toString().getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					log.error(e, e);
				}
			} else {
				pst.setNull(3, Types.BLOB);
			}

			// Если идет удаление триггера, то даже если было незакоммичено изменение, регистрируем удаление
			pst.setInt(4, action == ACTION_DELETE ? action : vcsAction);

			pst.setString(5, entityUid);
			pst.setLong(6, entityType);
			pst.executeUpdate();
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(pst);
		}
	}
	
	// Для триггеров
	protected String getVcsModelDiff(String entityUid, int entityType, int action, byte[] oldExpr, byte[] newExpr) throws DriverException {
		PreparedStatement oldValuePst = null;
		ResultSet rs = null;
		StringBuilder diff = new StringBuilder();
		try {
			oldValuePst = conn.prepareStatement("SELECT c_old_value FROM t_vcs_model WHERE c_entity_id=? AND c_type=? AND c_fix_end_id IS NULL");
			oldValuePst.setString(1, entityUid);
			oldValuePst.setInt(2, entityType);
			rs = oldValuePst.executeQuery();
			if (rs.next()) {
				byte[] oldValue = (byte[]) getValue(rs, "c_old_value", PC_BLOB);
				if (oldValue != null) {
					List<Element> values = XmlUtil.read(oldValue).getChildren();
					for (Element value : values) {
						if ("content".equals(value.getAttributeValue("i"))) {
							getRawDiff(newExpr, (byte[]) fromString(PC_BLOB, value.getText()), diff);
						}
					}
				} else {
					return getDiff(oldExpr, newExpr, action);
				}
			}
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new DriverException("", ex);
		} finally {
			DbUtils.closeQuietly(oldValuePst);
			DbUtils.closeQuietly(rs);
		}
		
		return diff.toString();
	}
	
	// Для триггеров
	protected String getDiff(byte[] oldExpr, byte[] newExpr, int action) throws DriverException {
		StringBuilder diff = new StringBuilder();
		if (newExpr != null && newExpr.length > 0) {
			byte[] oldData = (action == ACTION_DELETE) ? newExpr : (action == ACTION_CREATE) ? new byte[0] : oldExpr;
			byte[] newData = (action == ACTION_DELETE) ? new byte[0] : newExpr;
			getRawDiff(newData, oldData, diff);
		}
		return diff.toString();
	}
	*/		
	protected void createVcsModelRecord(int type, int action, Object oldObj, Object newObj, byte[] newExpr, Connection conn)
			throws DriverException, SQLException {
		String uid=getVcsUid(newObj);
		String name=getName(uid, type, newObj);
		PreparedStatement pst ;
		if(version<VERSION_UL){
			pst = conn.prepareStatement(
				"INSERT INTO " + getDBPrefix() + "t_vcs_model"
				+ "(c_entity_id,c_type,c_action,c_old_value,c_dif,c_user_id,c_ip,c_name,c_repl_id,c_fix_start_id)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,"
				+ "(SELECT MAX(c_id) FROM " + getDBPrefix() + "t_changescls WHERE c_entity_id=? AND c_type=?))");
		}else{
				pst = conn.prepareStatement(
					"INSERT INTO " + getDBPrefix() + "t_vcs_model"
					+ "(c_entity_id,c_type,c_action,c_old_value,c_dif,c_user_id,c_ip,c_name,c_rimport_id,c_fix_start_id)"
					+ " VALUES (?,?,?,?,?,?,?,?,?,"
					+ "(SELECT MAX(c_id) FROM " + getDBPrefix() + "t_changescls WHERE c_entity_id=? AND c_type=?))");
		}
		try {
			pst.setString(1, uid);
			pst.setInt(2, type);
			pst.setInt(3, action);
			
			setValue(pst, 4, PC_BLOB, packModelValue(oldObj, type, action, newExpr));
			
			String diff = getDiff(type,oldObj, newObj, newExpr, action);
			
			if (diff.length() > 0) {
				try {
					setValue(pst, 5, PC_BLOB, diff.toString().getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					log.error(e, e);
				}
			} else {
				pst.setNull(5, Types.BLOB);
			}
			
			pst.setLong(6, us.getUserId());
			pst.setString(7, us.getIp());
			pst.setString(8, name);
			if(isImportState){
				pst.setLong(9,importObjId);
			}else{
				pst.setNull(9, Types.BIGINT);
			}
			pst.setString(10, uid);
			pst.setInt(11, type);
			pst.executeUpdate();
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(pst);
		}
	}
	
	protected String getDiff(int type,Object oldObj, Object newObj, byte[] newExpr, int action) throws DriverException {
		StringBuilder diff = new StringBuilder();
		byte[] oldExpr=new byte[0];
		if(type==ENTITY_TYPE_METHOD){
			KrnMethod oldm=(KrnMethod)oldObj;
			KrnMethod m=(KrnMethod)newObj;
			if (!oldm.name.equals(m.name)) {
	    		diff.append("***** Изменение наименования: прежнее имя'" + oldm.name + "' новое имя '"+m.name+"'").append(rendl);
			}
			if (oldm.isClassMethod!=m.isClassMethod) {
	    		diff.append("***** Изменение типа метода: прежний тип'Метод класса=" + oldm.isClassMethod + "' новый тип 'Метод класса="+m.isClassMethod+"'").append(rendl);
			}
			
			if (newExpr != null && newExpr.length > 0) {
				oldExpr=getMethodExpression(m.uid);
			}
		}else if(type==ENTITY_TYPE_CLASS){
			KrnClass cls=(KrnClass)newObj;
			KrnClass cls_old=(KrnClass)oldObj;
			if(!cls.name.equals(cls_old.name))
				diff.append("*****наименование:"+cls_old.name+"->"+cls.name).append(rendl);
			if(!(""+cls.tname).equals(""+cls_old.tname))
				diff.append("*****наименование таблиы:"+cls_old.tname+"->"+cls.tname).append(rendl);
			if(cls.isRepl!=cls_old.isRepl)
				diff.append("*****репликация:"+cls_old.isRepl+"->"+cls.isRepl).append(rendl);
			if(cls.parentId!=cls_old.parentId)
				diff.append("*****родилель:"+cls_old.parentId+"->"+cls.parentId).append(rendl);
			if(diff.length()>0)
				diff.insert(0, "***** Изменение класса:"+rendl);
		}else if(type==ENTITY_TYPE_ATTRIBUTE){
			KrnAttribute attr=(KrnAttribute)newObj;
			KrnAttribute attr_old=(KrnAttribute)oldObj;
			if(!attr.name.equals(attr_old.name))
				diff.append("*****наименование:"+attr_old.name+"->"+attr.name).append(rendl);
			if(!(""+attr.tname).equals(""+attr_old.tname))
				diff.append("*****наименование колонки:"+attr_old.tname+"->"+attr.tname).append(rendl);
			if(attr.isRepl!=attr_old.isRepl)
				diff.append("*****репликация:"+attr_old.isRepl+"->"+attr.isRepl).append(rendl);
			if(attr.isIndexed!=attr_old.isIndexed)
				diff.append("*****индексирование:"+attr_old.isIndexed+"->"+attr.isIndexed).append(rendl);
			if(attr.isMultilingual!=attr_old.isMultilingual)
				diff.append("*****мультиязычность:"+attr_old.isMultilingual+"->"+attr.isMultilingual).append(rendl);
			if(attr.isUnique!=attr_old.isUnique)
				diff.append("*****уникальность:"+attr_old.isUnique+"->"+attr.isUnique).append(rendl);
			if(attr.sDesc!=attr_old.sDesc)
				diff.append("*****направление сортировки:"+attr_old.sDesc+"->"+attr.sDesc).append(rendl);
			if(attr.isAggregate()!=attr_old.isAggregate())
				diff.append("*****агрегация:"+attr_old.isAggregate()+"->"+attr.isAggregate());
			if(attr.isFullText()!=attr_old.isFullText())
				diff.append("*****полнотекстовый:"+attr_old.isFullText()+"->"+attr.isFullText()).append(rendl);
			if(attr.isGroup()!=attr_old.isGroup())
				diff.append("*****группировка:"+attr_old.isGroup()+"->"+attr.isGroup()).append(rendl);
			if(diff.length()>0)
				diff.insert(0,"***** Изменение атрибута:"+rendl);
		}else if(type>=ENTITY_TYPE_CLS_TRIGGER_BEFORE_CREATE && type<=ENTITY_TYPE_CLS_TRIGGER_AFTER_DELETE){
			KrnClass cls_t=(KrnClass)newObj;
			KrnClass cls_t_old=(KrnClass)oldObj;
			int mode=0;
			if(type==ENTITY_TYPE_CLS_TRIGGER_BEFORE_CREATE){
				oldExpr=cls_t_old.beforeCreateObjExpr;
				mode=cls_t_old.beforeCreateObjTr-cls_t.beforeCreateObjTr;
			}else if(type==ENTITY_TYPE_CLS_TRIGGER_AFTER_CREATE){
				oldExpr=cls_t_old.afterCreateObjExpr;
				mode=cls_t_old.afterCreateObjTr-cls_t.afterCreateObjTr;
			}else if(type==ENTITY_TYPE_CLS_TRIGGER_BEFORE_DELETE){
				oldExpr=cls_t_old.beforeDeleteObjExpr;
				mode=cls_t_old.beforeDeleteObjTr-cls_t.beforeDeleteObjTr;
			}else if(type==ENTITY_TYPE_CLS_TRIGGER_AFTER_DELETE){
				oldExpr=cls_t_old.afterDeleteObjExpr;
				mode=cls_t_old.afterDeleteObjTr-cls_t.afterDeleteObjTr;
			}
    		if(mode!=0)
    			diff.append("***** Изменение транзакции: с'" + (mode<0?"нулевой":"текущей") + "' на '"+ (mode<0?"текущую":"нулевую")+"'").append(rendl);
		}else if(type>=ENTITY_TYPE_ATTR_TRIGGER_BEFORE_CHANGE && type<=ENTITY_TYPE_ATTR_TRIGGER_AFTER_DELETE){
			KrnAttribute attr_t=(KrnAttribute)newObj;
			KrnAttribute attr_t_old=(KrnAttribute)oldObj;
			int mode=0;
			if(type==ENTITY_TYPE_ATTR_TRIGGER_BEFORE_CHANGE){
				oldExpr=attr_t_old.beforeEventExpr;
				mode=attr_t_old.beforeEventTr-attr_t.beforeEventTr;
			}else if(type==ENTITY_TYPE_CLS_TRIGGER_AFTER_CREATE){
				oldExpr=attr_t_old.afterEventExpr;
				mode=attr_t_old.afterEventTr-attr_t.afterEventTr;
			}else if(type==ENTITY_TYPE_CLS_TRIGGER_BEFORE_DELETE){
				oldExpr=attr_t_old.beforeDelEventExpr;
				mode=attr_t_old.beforeDelEventTr-attr_t.beforeDelEventTr;
			}else if(type==ENTITY_TYPE_CLS_TRIGGER_AFTER_DELETE){
				oldExpr=attr_t_old.afterDelEventExpr;
				mode=attr_t_old.afterDelEventTr-attr_t.afterDelEventTr;
			}
    		if(mode!=0)
    			diff.append("***** Изменение транзакции: с'" + (mode<0?"нулевой":"текущей") + "' на '"+ (mode<0?"текущую":"нулевую")+"'").append(rendl);
		}
		if (newExpr != null && newExpr.length > 0) {
			byte[] oldData = (action == ACTION_DELETE) ? newExpr :(action == ACTION_CREATE) ? new byte[0] : oldExpr;
			byte[] newData = (action == ACTION_DELETE) ? new byte[0] : newExpr;
			getRawDiff(newData, oldData, diff);
		}
		return diff.toString();
	}
	
	protected void updateVcsModelRecord(int type, int vcsAction, int action, Object oldObj, Object newObj, byte[] newExpr, Connection conn) throws DriverException, SQLException {
		PreparedStatement pst = null;
		
		String uid=getVcsUid(newObj);
		String name=getName(uid, type, newObj);
		pst = conn.prepareStatement(
				"UPDATE " + getDBPrefix() + "t_vcs_model SET c_name=?,c_mod_last_time=?,c_dif=?,c_action=? WHERE c_entity_id=? AND c_type=? AND c_fix_end_id IS NULL");
		try {
			pst.setString(1, name);
			pst.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
			
			String diff = getVcsModelDiff(type,oldObj, newObj, newExpr, action,conn);
			if (diff.length() > 0) {
				try {
					setValue(pst, 3, PC_BLOB, diff.toString().getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					log.error(e, e);
				}
			} else {
				pst.setNull(3, Types.BLOB);
			}

			// Если идет удаление метода, то даже если было незакоммичено изменение, регистрируем удаление
			pst.setInt(4, action == ACTION_DELETE ? action : vcsAction);

			pst.setString(5, uid);
			pst.setLong(6, type);
			pst.executeUpdate();
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(pst);
		}
	}
	
	protected void deleteVcsModelRecord(int type, String uid, Connection conn) throws DriverException, SQLException {
		PreparedStatement pst = null;
		
		pst = conn.prepareStatement(
				"DELETE FROM " + getDBPrefix() + "t_vcs_model WHERE c_entity_id=? AND c_type=? AND c_fix_end_id IS NULL");
		try {
			pst.setString(1, uid);
			pst.setLong(2, type);
			pst.executeUpdate();
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(pst);
		}
	}

	protected byte[] packModelValue(Object oldObject, int type, int action, byte[] expr) throws DriverException {
		if(oldObject==null)return null;
		if (type==ENTITY_TYPE_METHOD) {
			KrnMethod method = (KrnMethod)oldObject;
			Element values = new Element("values");
			Element value = new Element("value");
			value.setAttribute("i", "name");
			String content = method.name;
			if (content == null) {
				value.setAttribute("null", "true");
			} else {
				value.setText(content);
			}
			values.addContent(value);
			value = new Element("value");
			value.setAttribute("i", "classId");
			content = ""+method.classId;
			if (content == null) {
				value.setAttribute("null", "true");
			} else {
				value.setText(content);
			}
			values.addContent(value);
			value = new Element("value");
			value.setAttribute("i", "isClassMethod");
			content = method.isClassMethod?"true":"false";
			if (content == null) {
				value.setAttribute("null", "true");
			} else {
				value.setText(content);
			}
			values.addContent(value);
			value = new Element("value");
			value.setAttribute("i", "content");
			content = toString(PC_BLOB, (action == ACTION_DELETE || action == ACTION_CREATE) ? expr : getMethodExpression(method.uid));
			if (content == null) {
				value.setAttribute("null", "true");
			} else {
				value.setText(content);
			}
			values.addContent(value);
			try {
				return XmlUtil.write(values);
			} catch (IOException ex) {
				throw new DriverException("Failed to serialize old value to XML.", ex);
			}
		}else if (type==ENTITY_TYPE_CLASS) {
			KrnClass oldCls = (KrnClass)oldObject;
			Element value = new Element("value");
			value.setAttribute("uid",oldCls.uid);
			value.setAttribute("id",""+oldCls.id);
			value.setAttribute("name",oldCls.name);
			if(oldCls.tname!=null)
				value.setAttribute("tname",oldCls.tname);
			value.setAttribute("isRepl",""+oldCls.isRepl);
			value.setAttribute("parentId",""+oldCls.parentId);
			try {
				return XmlUtil.write(value);
			} catch (IOException ex) {
				throw new DriverException("Failed to serialize old value to XML.", ex);
			}
		}else if (type==ENTITY_TYPE_ATTRIBUTE) {
			KrnAttribute attr = (KrnAttribute)oldObject;
			Element value = new Element("value");
			value.setAttribute("uid",attr.uid);
			value.setAttribute("id",""+attr.id);
			value.setAttribute("name",attr.name);
			if(attr.tname!=null)
				value.setAttribute("tname",attr.tname);
			value.setAttribute("classId",""+attr.classId);
			value.setAttribute("typeClassId",""+attr.typeClassId);
			value.setAttribute("isRepl",""+attr.isRepl);
			value.setAttribute("isIndexed",""+attr.isIndexed);
			value.setAttribute("isMultilingual",""+attr.isMultilingual);
			value.setAttribute("isUnique",""+attr.isUnique);
			value.setAttribute("collectionType",""+attr.collectionType);
			value.setAttribute("sDesc",""+attr.sDesc);
			value.setAttribute("flags",""+attr.flags);
			value.setAttribute("size",""+attr.size);
			value.setAttribute("accessModifierType",""+attr.accessModifierType);
			value.setAttribute("rAttrId",""+attr.rAttrId);
			value.setAttribute("sAttrId",""+attr.sAttrId);
			try {
				return XmlUtil.write(value);
			} catch (IOException ex) {
				throw new DriverException("Failed to serialize old value to XML.", ex);
			}
		}else if(type>=ENTITY_TYPE_CLS_TRIGGER_BEFORE_CREATE && type<=ENTITY_TYPE_CLS_TRIGGER_AFTER_DELETE){
			KrnClass cls_old = (KrnClass)oldObject;
			Element values = new Element("values");
			byte[] oldExpr=new byte[0];
			int objTr=0;
			if(type==ENTITY_TYPE_CLS_TRIGGER_BEFORE_CREATE){
				oldExpr=cls_old.beforeCreateObjExpr;
				objTr=cls_old.beforeCreateObjTr;
			}else if(type==ENTITY_TYPE_CLS_TRIGGER_AFTER_CREATE){
				objTr=cls_old.afterCreateObjTr;
				oldExpr=cls_old.afterCreateObjExpr;
			}else if(type==ENTITY_TYPE_CLS_TRIGGER_BEFORE_DELETE){
				objTr=cls_old.beforeDeleteObjTr;
				oldExpr=cls_old.beforeDeleteObjExpr;
			}else if(type==ENTITY_TYPE_CLS_TRIGGER_AFTER_DELETE){
				objTr=cls_old.afterDeleteObjTr;
				oldExpr=cls_old.afterDeleteObjExpr;
			}
			Element value = new Element("value");
			value.setAttribute("i", "objTr");
			value.setText(""+objTr);
			values.addContent(value);
			value = new Element("value");
			value.setAttribute("i", "content");
			String content = toString(PC_BLOB, action==2 ? expr : oldExpr);
			if (content == null) {
				value.setAttribute("null", "true");
			} else {
				value.setText(content);
			}
			values.addContent(value);
			try {
				return XmlUtil.write(values);
			} catch (IOException ex) {
				throw new DriverException("Failed to serialize old value to XML.", ex);
			}
		}else if(type>=ENTITY_TYPE_ATTR_TRIGGER_BEFORE_CHANGE && type<=ENTITY_TYPE_ATTR_TRIGGER_AFTER_DELETE){
			KrnAttribute attr_old = (KrnAttribute)oldObject;
			Element values = new Element("values");
			byte[] oldExpr=new byte[0];
			int objTr=0;
			if(type==ENTITY_TYPE_ATTR_TRIGGER_BEFORE_CHANGE){
				oldExpr=attr_old.beforeEventExpr;
				objTr=attr_old.beforeEventTr;
			}else if(type==ENTITY_TYPE_ATTR_TRIGGER_AFTER_CHANGE){
				objTr=attr_old.afterEventTr;
				oldExpr=attr_old.afterEventExpr;
			}else if(type==ENTITY_TYPE_ATTR_TRIGGER_BEFORE_DELETE){
				objTr=attr_old.beforeDelEventTr;
				oldExpr=attr_old.beforeDelEventExpr;
			}else if(type==ENTITY_TYPE_ATTR_TRIGGER_AFTER_DELETE){
				objTr=attr_old.afterDelEventTr;
				oldExpr=attr_old.afterDelEventExpr;
			}
			Element value = new Element("value");
			value.setAttribute("i", "objTr");
			value.setText(""+objTr);
			values.addContent(value);
			value = new Element("value");
			value.setAttribute("i", "content");
			String content = toString(PC_BLOB, action==2 ? expr : oldExpr);
			if (content == null) {
				value.setAttribute("null", "true");
			} else {
				value.setText(content);
			}
			values.addContent(value);
			try {
				return XmlUtil.write(values);
			} catch (IOException ex) {
				throw new DriverException("Failed to serialize old value to XML.", ex);
			}
		}
		return null;
	}

	public void commitVcsModelIfEditingBeforeReplication(String entityUid, int entityType, String comment) throws DriverException {
		try {
			PreparedStatement selPst = conn.prepareStatement(
					"SELECT c_entity_id,c_type,c_action,c_user_id,c_ip,c_id FROM " + getDBPrefix() + "t_vcs_model"
					+ " WHERE c_entity_id=? AND c_type=? AND c_fix_end_id IS NULL ORDER BY c_id");
			
			PreparedStatement chPst = conn.prepareStatement(
					"INSERT INTO " + getDBPrefix() + "t_changescls (c_entity_id,c_type,c_action,c_user_id,c_ip)"
					+ " VALUES (?,?,?,?,?)");
			
			PreparedStatement vcsPst = conn.prepareStatement("UPDATE " + getDBPrefix() + "t_vcs_model SET c_fix_end_id=?,c_fix_comment=? WHERE c_id=?");

			ResultSet rs = null;
			try {
				selPst.setString(1, entityUid);
				selPst.setInt(2, entityType);
				rs = selPst.executeQuery();
				while (rs.next()) {
					chPst.setString(1, getSanitizedString(rs, 1));
					chPst.setLong(2, rs.getLong(2));
					chPst.setLong(3, rs.getLong(3));
					chPst.setLong(4, rs.getLong(4));
					chPst.setString(5, getSanitizedString(rs, 5));
					chPst.executeUpdate();
					
					vcsPst.setLong(1, getLastInsertId());
					setValue(vcsPst, 2, PC_MEMO, comment);
					vcsPst.setLong(3, rs.getLong(6));
					vcsPst.executeUpdate();
				}
				rs.close();
			} finally {
				DbUtils.closeQuietly(rs);
				DbUtils.closeQuietly(vcsPst);
				DbUtils.closeQuietly(chPst);
				DbUtils.closeQuietly(selPst);
			}
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		}
	}

	public void commitVcsModelAfterReplication(long replId, String comment) throws DriverException {
		try {
			PreparedStatement selPst;
			if(version<VERSION_UL){
				selPst = conn.prepareStatement(
					"SELECT c_entity_id,c_type,c_action,c_user_id,c_ip,c_id FROM " + getDBPrefix() + "t_vcs_model"
					+ " WHERE c_repl_id=? AND c_fix_end_id IS NULL ORDER BY c_id");
			}else{
				selPst = conn.prepareStatement(
						"SELECT c_entity_id,c_type,c_action,c_user_id,c_ip,c_id FROM " + getDBPrefix() + "t_vcs_model"
						+ " WHERE c_rimport_id=? AND c_fix_end_id IS NULL ORDER BY c_id");
			}
			PreparedStatement chPst = conn.prepareStatement(
					"INSERT INTO " + getDBPrefix() + "t_changescls (c_entity_id,c_type,c_action,c_user_id,c_ip)"
					+ " VALUES (?,?,?,?,?)");
			
			PreparedStatement vcsPst = conn.prepareStatement("UPDATE " + getDBPrefix() + "t_vcs_model SET c_fix_end_id=?,c_fix_comment=? WHERE c_id=?");

			ResultSet rs = null;
			try {
				selPst.setLong(1, replId);
				rs = selPst.executeQuery();
				while (rs.next()) {
					chPst.setString(1, getSanitizedString(rs, 1));
					chPst.setLong(2, rs.getLong(2));
					chPst.setLong(3, rs.getLong(3));
					chPst.setLong(4, rs.getLong(4));
					chPst.setString(5, getSanitizedString(rs, 5));
					chPst.executeUpdate();
					
					vcsPst.setLong(1, getLastInsertId());
					setValue(vcsPst, 2, PC_MEMO, comment);
					vcsPst.setLong(3, rs.getLong(6));
					vcsPst.executeUpdate();
				}
				rs.close();
			} finally {
				DbUtils.closeQuietly(rs);
				DbUtils.closeQuietly(vcsPst);
				DbUtils.closeQuietly(chPst);
				DbUtils.closeQuietly(selPst);
			}
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		}
	}

	public void commitVcsModelClassAttr(String comment) throws DriverException {
		try {
			PreparedStatement selPst = conn.prepareStatement(
					"SELECT c_entity_id,c_type,c_action,c_user_id,c_ip,c_id FROM " + getDBPrefix() + "t_vcs_model"
					+ " WHERE c_type IN ("+ENTITY_TYPE_ATTRIBUTE+","+ENTITY_TYPE_CLASS+") AND c_fix_end_id IS NULL ORDER BY c_id");
			
			PreparedStatement chPst = conn.prepareStatement(
					"INSERT INTO " + getDBPrefix() + "t_changescls (c_entity_id,c_type,c_action,c_user_id,c_ip)"
					+ " VALUES (?,?,?,?,?)");
			
			PreparedStatement vcsPst = conn.prepareStatement("UPDATE " + getDBPrefix() + "t_vcs_model SET c_fix_end_id=?,c_mod_confirm_time=?,c_fix_comment=? WHERE c_id=?");

			ResultSet rs = null;
			try {
					rs = selPst.executeQuery();
					while (rs.next()) {
						chPst.setString(1, rs.getString(1));
						chPst.setInt(2, rs.getInt(2));
						chPst.setLong(3, rs.getLong(3));
						chPst.setLong(4, rs.getLong(4));
						chPst.setString(5, getString(rs, 5));
						chPst.executeUpdate();
						vcsPst.setLong(1, getLastInsertId());
						vcsPst.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
						setValue(vcsPst, 3, PC_MEMO, comment);
						vcsPst.setLong(4, rs.getLong(6));
						vcsPst.executeUpdate();
					}
			} finally {
				DbUtils.closeQuietly(rs);
				DbUtils.closeQuietly(vcsPst);
				DbUtils.closeQuietly(chPst);
				DbUtils.closeQuietly(selPst);
			}
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		}
	}

	public void commitVcsModel(List<KrnVcsChange> changes, String comment) throws DriverException {
		try {
			PreparedStatement selPst = conn.prepareStatement(
					"SELECT c_action,c_user_id,c_ip,c_id FROM " + getDBPrefix() + "t_vcs_model"
					+ " WHERE c_entity_id=? AND c_type=? AND c_fix_end_id IS NULL ORDER BY c_id");
			
			PreparedStatement chPst = conn.prepareStatement(
					"INSERT INTO " + getDBPrefix() + "t_changescls (c_entity_id,c_type,c_action,c_user_id,c_ip)"
					+ " VALUES (?,?,?,?,?)");
			
			PreparedStatement vcsPst = conn.prepareStatement("UPDATE " + getDBPrefix() + "t_vcs_model SET c_fix_end_id=?,c_mod_confirm_time=?,c_fix_comment=? WHERE c_id=?");

			ResultSet rs = null;
			try {
				for (KrnVcsChange change : changes) {
					if (change.cvsChangeMethod != null) {
						selPst.setString(1, change.cvsChangeMethod.uid);
						selPst.setInt(2, ENTITY_TYPE_METHOD);
					} else if (change.cvsChangeClass != null) {
						selPst.setString(1, change.cvsChangeClass.uid);
						selPst.setInt(2, change.typeId);
					} else if (change.cvsChangeAttr != null) {
						selPst.setString(1, change.cvsChangeAttr.uid);
						selPst.setInt(2, change.typeId);
					} else {
						continue;
					}
					rs = selPst.executeQuery();
					while (rs.next()) {
						if (change.cvsChangeMethod != null) {
							chPst.setString(1, change.cvsChangeMethod.uid);
							chPst.setInt(2, ENTITY_TYPE_METHOD);
						} else if (change.cvsChangeClass != null) {
							chPst.setString(1, change.cvsChangeClass.uid);
							chPst.setInt(2, change.typeId);
						} else if (change.cvsChangeAttr != null) {
							chPst.setString(1, change.cvsChangeAttr.uid);
							chPst.setInt(2, change.typeId);
						}
						chPst.setLong(3, rs.getLong(1));
						chPst.setLong(4, rs.getLong(2));
						chPst.setString(5, getString(rs, 3));
						chPst.executeUpdate();
						
						vcsPst.setLong(1, getLastInsertId());
						vcsPst.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
						setValue(vcsPst, 3, PC_MEMO, comment);
						vcsPst.setLong(4, rs.getLong(4));
						vcsPst.executeUpdate();
					}
					rs.close();
				}
			} finally {
				DbUtils.closeQuietly(rs);
				DbUtils.closeQuietly(vcsPst);
				DbUtils.closeQuietly(chPst);
				DbUtils.closeQuietly(selPst);
			}
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		}
	}
	
	public void rollbackVcsModel(List<KrnVcsChange> changes,UserSession uss) throws DriverException {
		try {
			PreparedStatement selPst = conn.prepareStatement(
					"SELECT c_entity_id,c_type,c_action,c_user_id,c_ip,c_old_value,c_id FROM " + getDBPrefix() + "t_vcs_model"
					+ " WHERE c_entity_id=? AND c_type=? AND c_fix_end_id IS NULL ORDER BY c_id DESC");
			
			PreparedStatement delPst = conn.prepareStatement("DELETE FROM " + getDBPrefix() + "t_vcs_model WHERE c_id=?");

			// Отключаем логирование изменений
			isUpgrading = true;
			ResultSet rs = null;
			try {
				for (KrnVcsChange change : changes) {
					String uid ;
					if (change.cvsChangeMethod != null) {
						uid = Funcs.sanitizeSQL(change.cvsChangeMethod.uid);
					} else if (change.cvsChangeClass != null) {
						uid = Funcs.sanitizeSQL(change.cvsChangeClass.uid);
					} else if (change.cvsChangeAttr != null) {
						uid = Funcs.sanitizeSQL(change.cvsChangeAttr.uid);
					} else {
						continue;
					}
					selPst.setString(1,uid);
					selPst.setInt(2, change.typeId);
					rs = selPst.executeQuery();
					while (rs.next()) {
						if (change.cvsChangeMethod != null) {
							KrnMethod method = db.getMethodByUid(getSanitizedString(rs, 1).trim());
							long action=rs.getLong(3);
							
							// Если было создание метода, то делаем удаление
							if (action == 0 && method!=null) {
								deleteMethod(getSanitizedString(rs, 1));
							} else {
								byte[] oldValue = (byte[])getValue(rs, "c_old_value", PC_BLOB);
								if (oldValue != null) {
									try {
										List<Element> values = XmlUtil.read(oldValue).getChildren();
										String name=change.cvsChangeMethod.name;
										long classId=-1;
										boolean isClassMethod=false;
										byte[] expr=null;
										for (Element value : values) {
											if("name".equals(value.getAttributeValue("i")))
												name = value.getText();
											else if("classId".equals(value.getAttributeValue("i"))){
												classId = Long.parseLong(value.getText());
											}else if("isClassMethod".equals(value.getAttributeValue("i"))){
												isClassMethod = "true".equals(value.getText());
											}else if("content".equals(value.getAttributeValue("i"))){
												expr=(byte[]) fromString( PC_BLOB, value.getText());
											}
										}
										if (action==2) {
											createMethod(uid, getClassByIdComp(classId), Funcs.sanitizeSQL(name), isClassMethod, expr);
										} else
											changeMethod(uid, Funcs.sanitizeSQL(name), isClassMethod, expr);
									} catch (Exception ex) {
										throw new DriverException("Failed to parse value from XML.", ex);
									}
								}
							}							
							this.log.info("Откат изменений по методу uid:"+change.cvsChangeMethod.uid+";userName:"+uss.getUserName()+";userIp:"+uss.getIp());
						} else if (!change.isTrigger && change.cvsChangeClass != null) {
							KrnClass cls = db.getClassByUid(getSanitizedString(rs, 1).trim());
							long action=rs.getLong(3);
							
							// Если было создание класса, то делаем удаление
							if (action == 0 && cls!=null) {
								deleteClass(cls.id,false);
							} else {
								byte[] oldValue = (byte[])getValue(rs, "c_old_value", PC_BLOB);
								if (oldValue != null) {
									try {
										Element value = XmlUtil.read(oldValue);
										String name = Funcs.sanitizeSQL(value.getAttributeValue("name"));
										String tname = Funcs.sanitizeSQL(value.getAttributeValue("tname"));
										long parentId = Long.parseLong(value.getAttributeValue("parentId"));
										boolean isRepl = "true".equals(value.getAttributeValue("isRepl"));
										if(action==2) {
											//создаем удаленный класс
											String cuid = Funcs.sanitizeSQL(value.getAttributeValue("uid"));
											long cid=Long.parseLong(value.getAttributeValue("id"));
											createClass(name, parentId, isRepl, 0, cid, cuid, false, tname);
											log.info("Откат действия удаления класса(восстановление).Класс:"+name+"|"+cid+";Пользователь:"+us.getLogUserName()+";IP:"+us.getIp());
										}else if(!cls.name.equals(name) ||(tname==null && cls.tname!=null)||(tname!=null && !tname.equals(cls.tname))
												|| cls.isRepl!=isRepl || cls.parentId!=parentId) {
												//возвращаем изменения в первоначальное состояние
												changeClass(cls.id,parentId,name,isRepl,false);
												log.info("Откат действия изменения класса(восстановление).Класс:"+name+"|"+cls.id+";Пользователь:"+us.getLogUserName()+";IP:"+us.getIp());
										}
												
									} catch (Exception ex) {
										throw new DriverException("Failed to parse value from XML.", ex);
									}
								}
							}
						} else if (!change.isTrigger && change.cvsChangeAttr != null) {
							KrnAttribute attr = db.getAttributeByUid(getSanitizedString(rs, 1).trim());
							long action=rs.getLong(3);
							
							// Если было создание атрибута, то делаем удаление
							if (action == 0 && attr!=null) {
								deleteAttribute(attr.id,false,version);
							} else {
								byte[] oldValue = (byte[])getValue(rs, "c_old_value", PC_BLOB);
								if (oldValue != null) {
									try {
										Element value = XmlUtil.read(oldValue);
										String name = Funcs.sanitizeSQL(value.getAttributeValue("name"));
										String tname = Funcs.sanitizeSQL(value.getAttributeValue("tname"));
										long classId=Long.parseLong(value.getAttributeValue("classId"));
										long typeClassId=Long.parseLong(value.getAttributeValue("typeClassId"));
										long flags=Long.parseLong(value.getAttributeValue("flags"));
										int size=Integer.parseInt(value.getAttributeValue("size"));
										int accessModifierType=Integer.parseInt(value.getAttributeValue("accessModifierType"));
										int collectionType=Integer.parseInt(value.getAttributeValue("collectionType"));
										long rAttrId=Long.parseLong(value.getAttributeValue("rAttrId"));
										long sAttrId=Long.parseLong(value.getAttributeValue("sAttrId"));
										boolean isRepl="true".equals(value.getAttributeValue("isRepl"));
										boolean sDesc="true".equals(value.getAttributeValue("sDesc"));
										boolean isIndexed="true".equals(value.getAttributeValue("isIndexed"));
										boolean isMultilingual="true".equals(value.getAttributeValue("isMultilingual"));
										boolean isUnique="true".equals(value.getAttributeValue("isUnique"));
										boolean isEncrypt = "true".equals(value.getAttributeValue("isEncrypt"));
										if(action==2) {
											//создаем удаленный атрибут
											String auid = Funcs.sanitizeSQL(value.getAttributeValue("uid"));
											long aid=Long.parseLong(value.getAttributeValue("id"));
											createAttribute(aid, auid, classId, typeClassId, name, collectionType,
													isUnique, isIndexed, isMultilingual, isRepl, size, flags,
													rAttrId, sAttrId, sDesc, false, tname, accessModifierType);
											log.info("Откат действия удаления атрибута(восстановление).Атрибут:"+name+"|"+aid+";Пользователь:"+us.getLogUserName()+";IP:"+us.getIp());
										}else if(!attr.name.equals(name) ||(tname==null && attr.tname!=null)||(tname!=null && !tname.equals(attr.tname))   
												|| attr.isRepl!=isRepl || attr.sDesc!=sDesc || attr.isIndexed!=isIndexed 
												|| attr.isMultilingual!=isMultilingual || attr.isUnique!=isUnique
												|| attr.classId!=classId || attr.typeClassId!=typeClassId || attr.flags!=flags || attr.size!=size 
												|| attr.accessModifierType!=accessModifierType || attr.collectionType!=collectionType 
												|| attr.rAttrId!=rAttrId || attr.sAttrId!=sAttrId) {
												//возвращаем изменения в первоначальное состояние
											changeAttribute(attr.id, typeClassId, name, collectionType,
													isUnique, isIndexed, isMultilingual, isRepl, size, flags,
													rAttrId, sAttrId, sDesc, false, tname, accessModifierType, isEncrypt);
											log.info("Откат действия изменения атрибута(восстановление).Атрибут:"+name+"|"+attr.id+";Пользователь:"+us.getLogUserName()+";IP:"+us.getIp());
										}
												
									} catch (Exception ex) {
										throw new DriverException("Failed to parse value from XML.", ex);
									}
								}
							}
						} else if (change.isTrigger && (change.cvsChangeClass != null || change.cvsChangeAttr != null)) {
							long action=rs.getLong(3);
								byte[] oldValue = (byte[])getValue(rs, "c_old_value", PC_BLOB);
								String expr=null;
								String objTr=null;
								if (oldValue != null) {
									try {
										List<Element> values = XmlUtil.read(oldValue).getChildren();
										for (Element value : values) {
											if("objTr".equals(value.getAttributeValue("i"))){
												objTr = value.getText();
											}else if("content".equals(value.getAttributeValue("i"))){
												expr=new String((byte[]) fromString( PC_BLOB, value.getText()),"UTF-8");
											}
										}
									} catch (Exception ex) {
										throw new DriverException("Failed to parse value from XML.", ex);
									}
								}
								boolean isZeroTransaction=false;
								int mode=-1;
								if (change.cvsChangeClass != null) {
									KrnClass cls_new = db.getClassByUid(getSanitizedString(rs, 1));
									if(change.typeId==ENTITY_TYPE_CLS_TRIGGER_BEFORE_CREATE){
										isZeroTransaction= (objTr!=null?"0".equals(objTr):cls_new.beforeCreateObjTr==0);
										mode=0;
									}else if(change.typeId==ENTITY_TYPE_CLS_TRIGGER_AFTER_CREATE){
										mode=1;
										isZeroTransaction= (objTr!=null?"0".equals(objTr):cls_new.afterCreateObjTr==0);
									}else if(change.typeId==ENTITY_TYPE_CLS_TRIGGER_BEFORE_DELETE){
										mode=2;
										isZeroTransaction= (objTr!=null?"0".equals(objTr):cls_new.beforeDeleteObjTr==0);
									}else if(change.typeId==ENTITY_TYPE_CLS_TRIGGER_AFTER_DELETE){
										mode=3;
										isZeroTransaction= (objTr!=null?"0".equals(objTr):cls_new.afterDeleteObjTr==0);
									}
									setClsTriggerEventExpression(expr, cls_new.id, mode, isZeroTransaction,false);
								}else if (change.cvsChangeAttr != null) {
									KrnAttribute attr_new = db.getAttributeByUid(getSanitizedString(rs, 1));
									if(change.typeId==ENTITY_TYPE_ATTR_TRIGGER_BEFORE_CHANGE){
										isZeroTransaction= (objTr!=null?"0".equals(objTr):attr_new.beforeEventTr==0);
										mode=0;
									}else if(change.typeId==ENTITY_TYPE_ATTR_TRIGGER_AFTER_CHANGE){
										isZeroTransaction= (objTr!=null?"0".equals(objTr):attr_new.afterEventTr==0);
										mode=1;
									}else if(change.typeId==ENTITY_TYPE_ATTR_TRIGGER_BEFORE_DELETE){
										isZeroTransaction= (objTr!=null?"0".equals(objTr):attr_new.beforeDelEventTr==0);
										mode=2;
									}else if(change.typeId==ENTITY_TYPE_ATTR_TRIGGER_AFTER_DELETE){
										isZeroTransaction= (objTr!=null?"0".equals(objTr):attr_new.afterDelEventTr==0);
										mode=3;
									}
									setAttrTriggerEventExpression(expr, attr_new.id, mode, isZeroTransaction,false);
								}
						}
						delPst.setLong(1, rs.getLong(7));
						delPst.executeUpdate();
					}
					rs.close();
				}
			} finally {
				DbUtils.closeQuietly(rs);
				DbUtils.closeQuietly(delPst);
				DbUtils.closeQuietly(selPst);
				// Включаем логирование изменений
				isUpgrading = false;
			}
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		}
	}
	protected String toString(long typeId, Object value) {
		if (value == null) {
			return null;
		}
		if (PC_STRING == typeId || PC_MSTRING == typeId || PC_MEMO == typeId || PC_MMEMO == typeId) {
			return (String)value;
		} else if (PC_INTEGER == typeId || PC_FLOAT == typeId) {
			return value.toString();
		} else if (PC_BOOL == typeId) {
			return value.toString();
		} else if (PC_DATE == typeId) {
			return "" + ((java.sql.Date)value).getTime();
		} else if (PC_TIME == typeId) {
			return "" + ((java.sql.Date)value).getTime();
		} else if (PC_BLOB == typeId || PC_MBLOB == typeId) {
			return Base64.encodeBytes((byte[])value);
		} else if (typeId >= 99) {
			KrnObject obj = (KrnObject)value;
			return obj.id + ";" + obj.uid + ";" + obj.classId;
		}
		return null;
	}

	protected Object fromString(long typeId, String strValue) {
		if (strValue == null) {
			return null;
		}
		if (PC_STRING == typeId || PC_MSTRING == typeId || PC_MEMO == typeId || PC_MMEMO == typeId) {
			return strValue;
		} else if (PC_INTEGER == typeId) {
			return Long.valueOf(strValue);
		} else if (PC_FLOAT == typeId) {
			return Double.valueOf(strValue);
		} else if (PC_BOOL == typeId) {
			return Boolean.valueOf(strValue);
		} else if (PC_DATE == typeId) {
			return new Date(Long.parseLong(strValue));
		} else if (PC_TIME == typeId) {
			return new Timestamp(Long.parseLong(strValue));
		} else if (PC_BLOB == typeId || PC_MBLOB == typeId) {
			return Base64.decode(strValue);
		} else if (typeId >= 99) {
			String[] strs = strValue.split(";");
			return new KrnObject(Long.parseLong(strs[0]), Funcs.sanitizeSQL(strs[1]), Long.parseLong(strs[2]));
		}
		return null;
	}
	
	protected String getVcsDataDiff(KrnObject obj, KrnAttribute attr, long langId, byte[] newValue) throws DriverException {
		PreparedStatement oldValuePst = null;
		ResultSet rs = null;
		StringBuilder diff = new StringBuilder();
		try {
			oldValuePst = conn.prepareStatement("SELECT c_old_value FROM t_vcs_objects WHERE c_obj_id=? AND c_attr_id=? AND c_lang_id=? AND c_fix_end_id IS NULL");
			oldValuePst.setLong(1, obj.id);
			oldValuePst.setLong(2, attr.id);
			oldValuePst.setLong(3, langId);
			rs = oldValuePst.executeQuery();
			if (rs.next()) {
				byte[] oldValue = (byte[])getValue(rs, "c_old_value", PC_BLOB);
				if(oldValue!=null && oldValue.length>0){
					List<Element> values = XmlUtil.read(oldValue).getChildren();
					for (Element value : values) {
						if (!"true".equals(value.getAttributeValue("null"))) {
							getXmlDiff(newValue, (byte[])fromString(attr.typeClassId, Funcs.sanitizeSQL(value.getText())), diff);
						}
					}
				}
			}
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new DriverException("", ex);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(oldValuePst);
		}
		
		return diff.toString();
	}

	protected void getXmlDiff(byte[] newData, byte[] oldData, StringBuilder diff) throws IOException, JDOMException {
		List<String> original = xmlDataToLines(oldData);
		List<String> revised = xmlDataToLines(newData);
		getDiff(original, revised, diff);
	}
	
	protected void getRawDiff(byte[] newData, byte[] oldData, StringBuilder diff) {
		List<String> original = rawDataToLines(oldData);
		List<String> revised = rawDataToLines(newData);
		getDiff(original, revised, diff);
	}

	private void getDiff(List<String> original, List<String> revised, StringBuilder diff) {
        Patch patch = DiffUtils.diff(original, revised);
        for (Delta delta: patch.getDeltas()) {
        	TYPE type = delta.getType();
        	if (type == TYPE.INSERT) {
        		Chunk chunk = delta.getRevised();
        		diff.append("***** Вставка, позиция " + chunk.getPosition() + " *****").append(rendl);
        		for (Object line : chunk.getLines()) {
            		diff.append(line).append(rendl);
        		}
        		diff.append(rendl);
        	
        	} else if (type == TYPE.DELETE) {
        		Chunk chunk = delta.getOriginal();
        		diff.append("***** Удаление, позиция " + chunk.getPosition() + " *****").append(rendl);
        		for (Object line : chunk.getLines()) {
        			diff.append(line).append(rendl);
        		}
        		diff.append(rendl);

        	} else if (type == TYPE.CHANGE) {
        		Chunk chunk = delta.getOriginal();
        		diff.append("***** Изменение, позиция " + chunk.getPosition() + " *****").append(rendl);
        		for (Object line : chunk.getLines()) {
        			diff.append(line).append(rendl);
        		}
        		chunk = delta.getRevised();
        		diff.append("*****").append(rendl);
        		for (Object line : chunk.getLines()) {
        			diff.append(line).append(rendl);
        		}
        		diff.append(rendl);
        	}
        }
	}
	
	protected String getVcsModelDiff(int type,Object oldObj, Object newObj, byte[] newValue, int action, Connection conn) throws DriverException {
		PreparedStatement oldValuePst = null;
		ResultSet rs = null;
		StringBuilder diff = new StringBuilder();
		try {
			oldValuePst = conn.prepareStatement("SELECT c_old_value FROM t_vcs_model WHERE c_entity_id=? AND c_type=? AND c_fix_end_id IS NULL");
			oldValuePst.setString(1, getVcsUid(newObj));
			oldValuePst.setInt(2, type);
			rs = oldValuePst.executeQuery();
			if (rs.next()) {
				byte[] oldValue = (byte[])getValue(rs, "c_old_value", PC_BLOB);
				
				if (oldValue != null) {
					if(type==ENTITY_TYPE_METHOD){
						KrnMethod m=(KrnMethod)newObj;
						List<Element> values = XmlUtil.read(oldValue).getChildren();
						for (Element value : values) {
							if ("name".equals(value.getAttributeValue("i")) && !m.name.equals(value.getText())) {
								diff.append("***** Изменение наименования: прежнее имя'" + value.getText() + "' новое имя '"+m.name+"'").append(rendl);
							}else if ("isClassMethod".equals(value.getAttributeValue("i")) && m.isClassMethod!="true".equals(value.getText())) {
				        		diff.append("***** Изменение типа метода: прежний тип'Метод класса=" + value.getText() + "' новый тип 'Метод класса="+m.isClassMethod+"'").append(rendl);
							}else if ("content".equals(value.getAttributeValue("i"))) {
								getRawDiff(newValue, (byte[])fromString(PC_BLOB, value.getText()), diff);
							}
						}
					}else if(type==ENTITY_TYPE_CLASS){
						KrnClass cls=(KrnClass)newObj;
						Element value = XmlUtil.read(oldValue);
						String tName = (cls.tname==null?"":cls.tname);
						if (!cls.name.equals(value.getAttributeValue("name")))
							diff.append("***** Изменение наименования: прежнее имя'" + value.getAttributeValue("name") + "' новое имя '"+cls.name+"'").append(rendl);
						if (!tName.equals(value.getAttributeValue("tname")))
							diff.append("***** Изменение наименования таблицы: прежнее имя'" + value.getAttributeValue("name") + "' новое имя '"+cls.name+"'").append(rendl);
						if(!(""+cls.isRepl).equals(value.getAttributeValue("isRepl")))
							diff.append("*****репликация:"+value.getAttributeValue("isRepl")+"->"+cls.isRepl).append(rendl);
						if(!(""+cls.parentId).equals(value.getAttributeValue("parentId")))
							diff.append("*****родитель:"+value.getAttributeValue("parentId")+"->"+cls.parentId).append(rendl);
						if(diff.length()>0)
							diff.insert(0,"***** Изменение класса:"+rendl);
					}else if(type==ENTITY_TYPE_ATTRIBUTE){
						KrnAttribute attr=(KrnAttribute)newObj;
						Element value = XmlUtil.read(oldValue);
						if(!attr.name.equals(value.getAttributeValue("name")))
							diff.append("*****наименование:"+value.getAttributeValue("name")+"->"+attr.name).append(rendl);
						if (attr.tname != null) {
							if (!attr.tname.equals(value.getAttributeValue("tname"))) {
								diff.append("*****наименование колонки:"+value.getAttributeValue("tname")+"->"+attr.tname).append(rendl);
							}
						} else if (value.getAttributeValue("tname") != null) {
							diff.append("*****наименование колонки:"+value.getAttributeValue("tname")+"->"+attr.tname).append(rendl);
						}
						if(!(""+attr.isRepl).equals(value.getAttributeValue("isRepl")))
							diff.append("*****репликация:"+value.getAttributeValue("isRepl")+"->"+attr.isRepl).append(rendl);
						if(!(""+attr.isIndexed).equals(value.getAttributeValue("isIndexed")))
							diff.append("*****индексирование:"+value.getAttributeValue("isIndexed")+"->"+attr.isIndexed).append(rendl);
						if(!(""+attr.isMultilingual).equals(value.getAttributeValue("isMultilingual")))
							diff.append("*****мультиязычность:"+value.getAttributeValue("isMultilingual")+"->"+attr.isMultilingual).append(rendl);
						if(!(""+attr.isUnique).equals(value.getAttributeValue("isUnique")))
							diff.append("*****уникальность:"+value.getAttributeValue("isUnique")+"->"+attr.isUnique).append(rendl);
						if(!(""+attr.sDesc).equals(value.getAttributeValue("sDesc")))
							diff.append("*****направление сортировки:"+value.getAttributeValue("sDesc")+"->"+attr.sDesc).append(rendl);
						if(!(""+attr.flags).equals(value.getAttributeValue("flags")))
							diff.append("*****flags:"+value.getAttributeValue("flags")+"->"+attr.flags).append(rendl);
						if(diff.length()>0)
							diff.insert(0,"***** Изменение атрибута:"+rendl);
					}else if(type>=ENTITY_TYPE_CLS_TRIGGER_BEFORE_CREATE && type<=ENTITY_TYPE_CLS_TRIGGER_AFTER_DELETE){
						KrnClass cls_t=(KrnClass)newObj;
						KrnClass cls_t_old=(KrnClass)oldObj;
						int mode=0;
						if(type==ENTITY_TYPE_CLS_TRIGGER_BEFORE_CREATE){
							oldValue=cls_t_old.beforeCreateObjExpr;
							mode=cls_t_old.beforeCreateObjTr-cls_t.beforeCreateObjTr;
						}else if(type==ENTITY_TYPE_CLS_TRIGGER_AFTER_CREATE){
							oldValue=cls_t_old.afterCreateObjExpr;
							mode=cls_t_old.afterCreateObjTr-cls_t.afterCreateObjTr;
						}else if(type==ENTITY_TYPE_CLS_TRIGGER_BEFORE_DELETE){
							oldValue=cls_t_old.beforeDeleteObjExpr;
							mode=cls_t_old.beforeDeleteObjTr-cls_t.beforeDeleteObjTr;
						}else if(type==ENTITY_TYPE_CLS_TRIGGER_AFTER_DELETE){
							oldValue=cls_t_old.afterDeleteObjExpr;
							mode=cls_t_old.afterDeleteObjTr-cls_t.afterDeleteObjTr;
						}
			    		if(mode!=0)
			    			diff.append("***** Изменение транзакции: с'" + (mode<0?"нулевой":"текущей") + "' на '"+ (mode<0?"текущую":"нулевую")+"'").append(rendl);
						if (newValue != null && newValue.length > 0) {
							byte[] oldData = (action == ACTION_DELETE) ? newValue :(action == ACTION_CREATE) ? new byte[0] : oldValue;
							byte[] newData = (action == ACTION_DELETE) ? new byte[0] : newValue;
							getRawDiff(newData, oldData, diff);
						}
					}else if(type>=ENTITY_TYPE_ATTR_TRIGGER_BEFORE_CHANGE && type<=ENTITY_TYPE_ATTR_TRIGGER_AFTER_DELETE){
						KrnAttribute attr_t=(KrnAttribute)newObj;
						KrnAttribute attr_t_old=(KrnAttribute)oldObj;
						int mode=0;
						if(type==ENTITY_TYPE_ATTR_TRIGGER_BEFORE_CHANGE){
							oldValue=attr_t_old.beforeEventExpr;
							mode=attr_t_old.beforeEventTr-attr_t.beforeEventTr;
						}else if(type==ENTITY_TYPE_CLS_TRIGGER_AFTER_CREATE){
							oldValue=attr_t_old.afterEventExpr;
							mode=attr_t_old.afterEventTr-attr_t.afterEventTr;
						}else if(type==ENTITY_TYPE_CLS_TRIGGER_BEFORE_DELETE){
							oldValue=attr_t_old.beforeDelEventExpr;
							mode=attr_t_old.beforeDelEventTr-attr_t.beforeDelEventTr;
						}else if(type==ENTITY_TYPE_CLS_TRIGGER_AFTER_DELETE){
							oldValue=attr_t_old.afterDelEventExpr;
							mode=attr_t_old.afterDelEventTr-attr_t.afterDelEventTr;
						}
			    		if(mode!=0)
			    			diff.append("***** Изменение транзакции: с'" + (mode<0?"нулевой":"текущей") + "' на '"+ (mode<0?"текущую":"нулевую")+"'").append(rendl);
						if (newValue != null && newValue.length > 0) {
							byte[] oldData = (action == ACTION_DELETE) ? newValue :(action == ACTION_CREATE) ? new byte[0] : oldValue;
							byte[] newData = (action == ACTION_DELETE) ? new byte[0] : newValue;
							getRawDiff(newData, oldData, diff);
						}
					}
				} else {
					return getDiff(type,oldObj, newObj, newValue, action);
				}
			}
		} catch (Exception ex) {
			log.error(ex, ex);
			throw new DriverException("", ex);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(oldValuePst);
		}
		
		return diff.toString();
	}

	private List<String> xmlDataToLines(byte[] data) throws IOException, JDOMException {
		return rawDataToLines(XmlUtil.writePretty(XmlUtil.read(data)));
	}
	
	private List<String> rawDataToLines(byte[] data) {
		//data = Funcs.normalizeInput(data, "UTF-8");
		List<String> lines = new ArrayList<String>();
		if (data != null) {
			String line = "";
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(Funcs.normalizeInput(data, "UTF-8")), "UTF-8"));
				while ((line = in.readLine()) != null) {
					line = Funcs.normalizeInput(line);
					if (Funcs.isValid(line))
						lines.add(line);
				}
				in.close();
			} catch (IOException e) {
				log.error(e, e);
			}
		}
		return lines;
	}

	@Override
	public boolean isTableExists(String tableName) {
		boolean isExist = true;
		try {
			PreparedStatement pst = conn.prepareStatement(
					"SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES"
					+ " WHERE TABLE_TYPE='BASE TABLE' AND TABLE_NAME=? AND TABLE_SCHEMA=?");
			pst.setString(1, tableName);
			pst.setString(2, db.getSchemeName());
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				isExist = rs.getInt(1) > 0;
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return isExist;
	}
	
	protected boolean isColumnExists(String t_name, String c_name) {
		boolean isExist = true;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(
					"SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS"
					+ " WHERE TABLE_NAME=? AND COLUMN_NAME=? AND TABLE_SCHEMA=?");
			pst.setString(1, t_name);
			pst.setString(2, c_name);
			pst.setString(3, db.getSchemeName());
			rs = pst.executeQuery();
			if (rs.next()) {
				isExist = rs.getInt(1) > 0;
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
		}
		return isExist;
	}

	@Override
	public boolean isIndexExists(String tName, String idxName) {
		boolean isExist = true;
		try {
			PreparedStatement pst = conn.prepareStatement(
					"SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS"
					+ " WHERE TABLE_NAME=? AND INDEX_NAME=? AND TABLE_SCHEMA=?");
			pst.setString(1, Funcs.normalizeInput(tName).replace("`", ""));
			pst.setString(2, Funcs.normalizeInput(idxName));
			pst.setString(3, db.getSchemeName());
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				isExist = rs.getInt(1) > 0;
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return isExist;
	}
	@Override
	public String getIndexType(String tName,String idxName) {
		String idxType= null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("SELECT INDEX_TYPE FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_NAME=? AND INDEX_NAME=? AND TABLE_SCHEMA=? GROUP BY INDEX_TYPE");
			pst.setString(1, Funcs.normalizeInput(tName));
			pst.setString(2, Funcs.normalizeInput(idxName));
			pst.setString(3, db.getSchemeName());
			rs = pst.executeQuery();
			if (rs.next()) {
				idxType = getString(rs, 1);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
		}
		return Funcs.sanitizeSQL(idxType);
	}

	@Override
	public List<OrlangTriggerInfo> getOrlangTriggersInfo() throws DriverException, SQLException {
		List<OrlangTriggerInfo> triggers = new ArrayList<OrlangTriggerInfo>();
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			// Тригеры классов
			rs = st.executeQuery("SELECT c_id, c_name, c_before_create_obj,c_before_create_obj_tr, c_after_create_obj,c_after_create_obj_tr, c_before_delete_obj, c_before_delete_obj_tr, c_after_delete_obj, c_after_delete_obj_tr FROM t_classes WHERE c_before_create_obj IS NOT NULL OR c_after_create_obj IS NOT NULL OR c_before_delete_obj IS NOT NULL OR c_after_delete_obj IS NOT NULL");
			while (rs.next()) {
				long clsId = rs.getLong("c_id");
				String clsName = getString(rs, "c_name");
				byte[] c_before_create_obj = rs.getBytes("c_before_create_obj");
				int tr = rs.getInt("c_before_create_obj_tr");
				if (c_before_create_obj != null) {
					triggers.add(new OrlangTriggerInfo(0, 0, clsId, clsName, "Перед созданием объекта", c_before_create_obj, tr));
				}
				byte[] c_after_create_obj = rs.getBytes("c_after_create_obj");
				tr = rs.getInt("c_after_create_obj_tr");
				if (c_after_create_obj != null) {
					triggers.add(new OrlangTriggerInfo(1, 0, clsId, clsName, "После создания объекта", c_after_create_obj, tr));
				}
				byte[] c_before_delete_obj = rs.getBytes("c_before_delete_obj");
				tr = rs.getInt("c_before_delete_obj_tr");
				if (c_before_delete_obj != null) {
					triggers.add(new OrlangTriggerInfo(2, 0, clsId, clsName, "Перед удалением объекта", c_before_delete_obj, tr));
				}
				byte[] c_after_delete_obj = rs.getBytes("c_after_delete_obj");
				tr = rs.getInt("c_after_delete_obj_tr");
				if (c_after_delete_obj != null) {
					triggers.add(new OrlangTriggerInfo(3, 0, clsId, clsName, "После удаления объекта", c_after_delete_obj, tr));
				}
			}
			// Тригеры атрибутов
			rs = st.executeQuery("SELECT c_id, c_name, c_before_event_expr,c_before_event_tr, c_after_event_expr,c_after_event_tr, c_before_del_event_expr,c_before_del_event_tr, c_after_del_event_expr, c_after_del_event_tr FROM t_attrs WHERE c_before_event_expr IS NOT NULL OR c_after_event_expr IS NOT NULL OR c_before_del_event_expr IS NOT NULL OR c_after_del_event_expr IS NOT NULL");
			while (rs.next()) {
				long attrId = rs.getLong("c_id");
				String attrName = getString(rs, "c_name");
				byte[] c_before_event_expr = rs.getBytes("c_before_event_expr");
				int tr = rs.getInt("c_before_event_tr");
				if (c_before_event_expr != null) {
					triggers.add(new OrlangTriggerInfo(0, 1, attrId, attrName, "Перед изменением значения атрибута", c_before_event_expr, tr));
				}
				byte[] c_after_event_expr = rs.getBytes("c_after_event_expr");
				tr = rs.getInt("c_after_event_tr");
				if (c_after_event_expr != null) {
					triggers.add(new OrlangTriggerInfo(1, 1, attrId, attrName, "После изменения значения атрибута", c_after_event_expr, tr));
				}
				byte[] c_before_del_event_expr = rs.getBytes("c_before_del_event_expr");
				tr = rs.getInt("c_before_del_event_tr");
				if (c_before_del_event_expr != null) {
					triggers.add(new OrlangTriggerInfo(2, 1, attrId, attrName, "Перед удалением значения атрибута", c_before_del_event_expr, tr));
				}
				byte[] c_after_del_event_expr = rs.getBytes("c_after_del_event_expr");
				tr = rs.getInt("c_after_del_event_tr");
				if (c_after_del_event_expr != null) {
					triggers.add(new OrlangTriggerInfo(3, 1, attrId, attrName, "После удаления значения атрибута", c_after_del_event_expr, tr));
				}
			}
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(st);
		}
		return triggers;
	}
	
	protected Pair<KrnMethod, byte[]> getDeletedMethod(String uid) throws DriverException, SQLException {
		PreparedStatement selPst = null;
		ResultSet rs = null;
		uid = Funcs.sanitizeSQL(uid);
		try {
			selPst = conn.prepareStatement(
				"SELECT c_old_value, c_name FROM " + Funcs.sanitizeSQL(getDBPrefix()) + "t_vcs_model"
				+ " WHERE c_entity_id=? AND c_type=? AND c_fix_end_id IS NULL");
			selPst.setString(1, uid);
			selPst.setInt(2, ENTITY_TYPE_METHOD);
			rs = selPst.executeQuery();
			
			if (rs.next()) {
				byte[] oldValue = (byte[])getValue(rs, "c_old_value", PC_BLOB);
				if (oldValue != null) {
					try {
						List<Element> values = XmlUtil.read(oldValue).getChildren();
						String name = Funcs.sanitizeSQL(rs.getString("c_name"));
						long classId=-1;
						boolean isClassMethod=false;
						byte[] expr=null;
						for (Element value : values) {
							if("name".equals(value.getAttributeValue("i")))
								name = Funcs.sanitizeSQL(value.getText());
							else if("classId".equals(value.getAttributeValue("i"))){
								classId = Long.parseLong(value.getText());
							}else if("isClassMethod".equals(value.getAttributeValue("i"))){
								isClassMethod = "true".equals(value.getText());
							}else if("content".equals(value.getAttributeValue("i"))){
								expr=(byte[]) fromString( PC_BLOB, Funcs.sanitizeSQL(value.getText()));
							}
						}
						KrnClass classM = db.getClassById(classId);
						return new Pair<KrnMethod, byte[]>(new KrnMethod(uid, name, classId, classM==null?"":classM.name,isClassMethod, -1), expr);
					} catch (Exception ex) {
						throw new DriverException("Failed to parse value from XML.", ex);
					}
				}
			}
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(selPst);
		}
		return null;
	}

	@Override
	public boolean getThrowOnSaveNullMode() {
		return throwOnSaveNull;
	}

	@Override
	public String getCurrentScheme() throws DriverException {
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery("SELECT database()");
			if (rs.next()) {
				return getSanitizedString(rs, 1);
			} else {
				throw new DriverException("Ошибка при получении текущей схемы.");
			}
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(st);
		}
	}

	@Override
	public void setVcsExport(long expObjId)  throws DriverException {
		KrnObject expObj = getObjectById(expObjId);
		KrnAttribute changeIdAttr = db.getAttributeByName(expObj.classId, "change_id");
		KrnAttribute priorChangeIdAttr = db.getAttributeByName(expObj.classId, "prior_change_id");
		KrnAttribute clsChangeIdAttr = db.getAttributeByName(expObj.classId, "clschange_id");
		KrnAttribute priorClsChangeIdAttr = db.getAttributeByName(expObj.classId, "prior_clschange_id");
		long changeId = (Long) getValue(expObjId, changeIdAttr.id, 0, 0, 0);
		long priorChangeId = (Long) getValue(expObjId, priorChangeIdAttr.id, 0, 0, 0);
		long clsChangeId = (Long) getValue(expObjId, clsChangeIdAttr.id, 0, 0, 0);
		long priorClsChangeId = (Long) getValue(expObjId, priorClsChangeIdAttr.id, 0, 0, 0);
		try {
			PreparedStatement chPst,chPst_;
			if(version<VERSION_UL){
				chPst = conn.prepareStatement("UPDATE " + getDBPrefix() + "t_vcs_model SET c_repl_id=? WHERE c_repl_id IS NULL AND c_fix_end_id>? AND c_fix_end_id<=?");
				chPst_ = conn.prepareStatement("UPDATE " + getDBPrefix() + "t_vcs_objects SET c_repl_id=? WHERE c_repl_id IS NULL AND c_fix_end_id>? AND c_fix_end_id<=?");
			}else{
				chPst = conn.prepareStatement("UPDATE " + getDBPrefix() + "t_vcs_model SET c_rexport_id=? WHERE c_rexport_id IS NULL AND c_fix_end_id>? AND c_fix_end_id<=?");
				chPst_ = conn.prepareStatement("UPDATE " + getDBPrefix() + "t_vcs_objects SET c_rexport_id=? WHERE c_rexport_id IS NULL AND c_fix_end_id>? AND c_fix_end_id<=?");
			}
			try {
				chPst.setLong(1, expObjId);
				chPst.setLong(2, priorClsChangeId);
				chPst.setLong(3, clsChangeId);
				chPst.executeUpdate();
				chPst_.setLong(1, expObjId);
				chPst_.setLong(2, priorChangeId);
				chPst_.setLong(3, changeId);
				chPst_.executeUpdate();
			} finally {
				DbUtils.closeQuietly(chPst);
				DbUtils.closeQuietly(chPst_);
			}
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		}
	}
	
	@Override
	public String getSessionId(){
		return getSessionId(conn);
	}
	
	@Override
	public String getSessionId(Connection conn) {
		Statement st = null;
		ResultSet rs = null;
		String SID="";
		try {
			st = conn.createStatement();
			rs = st.executeQuery("SELECT CONNECTION_ID()");
			if (rs.next()) {
				SID=rs.getString(1);
			}
		} catch (SQLException e) {
			log.error(e, e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(st);
		}
		return SID;
	}

	public void upgradeStructure() throws SQLException {
        log.info("Создание колонок c_tname в таблицах t_classes и t_attrs...");
		
        QueryRunner qr = new QueryRunner(true);
    	String sql = "ALTER TABLE " + getDBPrefix() + "t_classes ADD COLUMN c_tname VARCHAR(30) NULL DEFAULT NULL " +
		" AFTER c_cuid, ADD UNIQUE INDEX `c_tname_UNIQUE` (`c_tname` ASC) ;";
    	qr.update(conn, sql);
		
		sql = "ALTER TABLE " + getDBPrefix() + "t_attrs ADD COLUMN c_tname VARCHAR(30) NULL " +
				"AFTER c_auid, ADD UNIQUE INDEX `c_tname_UNIQUE` (`c_class_id`, `c_tname`);";
		qr.update(conn, sql);
		
		log.info("Апгрейд БД успешно завершен.");

		log.info("В апгрейде у таблицы t_attrs уникальный индекс изменен с c_tname на (`c_class_id`, `c_tname`)");
		
		sql = "ALTER TABLE " + getDBPrefix() + "`t_attrs` DROP INDEX `c_tname_UNIQUE`, ADD UNIQUE INDEX `c_tname_UNIQUE` (`c_class_id`, `c_tname`);";
		qr.update(conn, sql);
		
		log.info("Апгрейд БД до версии 22 успешно завершен.");

        log.info("В таблице t_changes добавлено поле c_object_uid.");
		
        sql = "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='" + db.getSchemeName() + "' AND table_name='t_changes' AND column_name='c_object_uid'";
		PreparedStatement pst = conn.prepareStatement(sql);
		ResultSet set = pst.executeQuery();
		boolean isExist = false;
		if (set.next()) {
			isExist = set.getInt(1) > 0;
		}
		set.close();
		pst.close();
		
		if (!isExist) {
			sql = "ALTER TABLE " + getDBPrefix() + "t_changes ADD COLUMN c_object_uid VARCHAR(20) DEFAULT NULL AFTER c_object_id;";
			qr.update(conn, sql);
		}

		log.info("Апгрейд БД до версии 24 успешно завершен.");

		log.info("Добавление полей c_before_event_expr (событие до сохранения) и c_after_event_expr (событие после сохранения) в таблицу t_attrs.");
        
		qr = new QueryRunner(true);
		try {
			sql = "ALTER TABLE " + getDBPrefix() + "t_attrs ADD COLUMN (c_before_event_expr LONGBLOB DEFAULT NULL, c_after_event_expr LONGBLOB DEFAULT NULL);";
			qr.update(conn, sql);
		} catch (SQLException e) {
			log.error("Поля c_before_event_expr (событие до сохранения) и c_after_event_expr (событие после сохранения) уже существуют!");
		}
		
		log.info("Апгрейд БД до версии 28 успешно завершен.");

        log.info("Апгрейд БД до версии 31 ...");
        
		Statement st = conn.createStatement();
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_changescls ADD (" +
					"c_time TIMESTAMP," +
					"c_user_id BIGINT DEFAULT NULL," +
					"c_ip VARCHAR(15) DEFAULT NULL)"
					);
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_changescls MODIFY " +
					"c_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
					);
			
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_changes ADD (" +
					"c_time TIMESTAMP," +
					"c_user_id BIGINT DEFAULT NULL," +
					"c_ip VARCHAR(15) DEFAULT NULL)"
					);
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_changes MODIFY " +
					"c_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
					);
			
			st.executeUpdate(
					"CREATE TABLE " + getDBPrefix() + "t_vcs_objects ("
					+ "c_id BIGINT NOT NULL AUTO_INCREMENT,"
					+ "c_obj_id BIGINT NOT NULL,"
					+ "c_obj_uid CHAR(20) NOT NULL,"
					+ "c_obj_class_id BIGINT NOT NULL,"
					+ "c_attr_id BIGINT NOT NULL,"
					+ "c_lang_id BIGINT NOT NULL,"
					+ "c_old_value LONGBLOB,"
					+ "c_user_id BIGINT NOT NULL,"
					+ "c_ip VARCHAR(15) NOT NULL,"
					+ "c_mod_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ "c_mod_last_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ "c_fix_start_id BIGINT,"
					+ "c_fix_end_id BIGINT,"
					+ "c_fix_comment TEXT,"
					+ "PRIMARY KEY PK_VCS (c_id),"
					+ "UNIQUE INDEX IDX_VCS_OBJ_ATTR_LANG (c_obj_id,c_attr_id,c_lang_id,c_fix_end_id)"
					+ ")");

			st.executeUpdate(
					"CREATE TABLE " + getDBPrefix() + "t_vcs_model ("
					+ "c_id BIGINT NOT NULL AUTO_INCREMENT,"
					+ "c_entity_id CHAR(36) NOT NULL,"
					+ "c_type INTEGER NOT NULL,"
					+ "c_action INTEGER NOT NULL,"
					+ "c_old_value LONGBLOB,"
					+ "c_user_id BIGINT NOT NULL,"
					+ "c_ip VARCHAR(15) NOT NULL,"
					+ "c_mod_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ "c_mod_last_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ "c_fix_start_id BIGINT,"
					+ "c_fix_end_id BIGINT,"
					+ "c_fix_comment TEXT,"
					+ "PRIMARY KEY PK_VCS_MODEL (c_id),"
					+ "INDEX IDX_VCS_MODEL_ENTITY_TYPE_ACTION (c_entity_id,c_type,c_action,c_fix_end_id)"
					+ ")");

	        log.info("Апгрейд БД до версии 31 успешно завершен!");
			st.close();
		
        log.info("Апгрейд БД до версии 32 ...");
		st = conn.createStatement();
		st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_dif TEXT");
		st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_dif TEXT");
		
        log.info("Апгрейд БД до версии 32 успешно завершен!");
		st.close();

        log.info("Апгрейд БД до версии 33 ...");
        
		st = conn.createStatement();
		
			st.executeUpdate(
					"CREATE TABLE " + getDBPrefix() + "t_syslog ("
					+ "c_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ "c_logger VARCHAR(50) NOT NULL,"
					+ "c_type NVARCHAR(20) NOT NULL,"
					+ "c_action NVARCHAR(50) NOT NULL,"
					+ "c_user NVARCHAR(20) NOT NULL,"
					+ "c_ip  VARCHAR(15) NOT NULL,"
					+ "c_host VARCHAR(20) NOT NULL,"
					+ "c_admin BIT NOT NULL,"
					+ "c_message TEXT"
					+ ")");
			st.executeUpdate("CREATE INDEX sl_time_user_idx"
					+ " ON "+getDBPrefix()+"t_syslog(c_time,c_user)");
	        log.info("Апгрейд БД до версии 33 успешно завершен!");
			st.close();

	        log.info("Апгрейд БД до версии 34 ...");
			st = conn.createStatement();
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_name VARCHAR(255)");
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_name VARCHAR(255)");
				
		        log.info("Апгрейд БД до версии 34 успешно завершен!");
				st.close();

			log.info("Апгрейд БД до версии 35 ...");
			st = conn.createStatement();
				// Создаем таблицу блокировок для методов
				createLockMethodsTable(conn);
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_repl_id BIGINT");
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects MODIFY c_dif MEDIUMTEXT");
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_repl_id BIGINT");
				log.info("Апгрейд БД до версии 35 успешно завершен!");
				st.close();

			log.info("Апгрейд БД до версии 36 ...");
			st = conn.createStatement();
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects DROP INDEX IDX_VCS_OBJ_ATTR_LANG");
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model DROP INDEX IDX_VCS_MODEL_ENTITY_TYPE_ACTION");
				st.executeUpdate("CREATE UNIQUE INDEX IDX_VCS_OBJ_ATTR_LANG_REPL"
						+ " ON "+getDBPrefix()+"t_vcs_objects(c_obj_id,c_attr_id,c_lang_id,c_fix_end_id,c_repl_id)");

				st.executeUpdate("CREATE INDEX IDX_VCS_MODEL_ENTITY_TYPE_ACTION_REPL"
						+ " ON "+getDBPrefix()+"t_vcs_model(c_entity_id,c_type,c_action,c_fix_end_id,c_repl_id)");
				log.info("Апгрейд БД до версии 36 успешно завершен!");
				st.close();

			log.info("Апгрейд БД до версии 37 ...");
			st = conn.createStatement();
				st.executeUpdate(
						"ALTER TABLE " + getDBPrefix() + "t_syslog add ("
						+ "c_server_id VARCHAR(50),"
						+ "c_object NVARCHAR(255),"
						+ "c_process NVARCHAR(255)"
						+ ")");
				log.info("Апгрейд БД до версии 37 успешно завершен!");
				st.close();

			st = conn.createStatement();
		        log.info("Изменение типа колонки 'c_dif' таблицы 't_vcs_model' на 'LONGBLOB'.");
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model MODIFY c_dif LONGBLOB");
		        log.info("OK!!!");
		        log.info("Изменение типа колонки 'c_dif' таблицы 't_vcs_objects' на 'LONGBLOB'.");
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects MODIFY c_dif LONGBLOB");
		        log.info("OK!!!");
				st.close();
	        // Добавление колонки в таблицу t_methods
			st = conn.createStatement();
		        log.info("Добавление поля 'developer' в таблицу t_methods");
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_methods ADD (developer BIGINT)");
		        log.info("OK!!!");
				st.close();
			log.info("Апгрейд БД до версии 39 успешно завершен!");

	        log.info("Апгрейд БД до версии 40 ...");
	        log.info("Добавление полей c_before_del_event_expr (событие 'Перед удалением значения атрибута'), c_after_del_event_expr (событие 'После удаления значения атрибута') в таблице t_attrs.");
	        log.info("Добавление полей c_before_create_obj (событие 'Перед созданием объекта'), c_after_create_obj (событие 'После создания объекта'), c_before_delete_obj (событие 'Перед удалением объекта'), c_after_delete_obj (событие 'После удаления объекта') в таблице t_classes.");
			st = conn.createStatement();
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD COLUMN (c_before_del_event_expr LONGBLOB DEFAULT NULL, c_after_del_event_expr LONGBLOB DEFAULT NULL);");
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD COLUMN (c_before_create_obj LONGBLOB DEFAULT NULL, c_after_create_obj LONGBLOB DEFAULT NULL, c_before_delete_obj LONGBLOB DEFAULT NULL, c_after_delete_obj LONGBLOB DEFAULT NULL);");
				st.close();
			log.info("Апгрейд БД до версии 40 успешно завершен.");

	        log.info("Апгрейд БД до версии 42 ...");
			st = conn.createStatement();
				try {
			        log.info("Удаление индекса IDX_VCS_OBJ_ATTR_LANG_REPL с таблицы t_vcs_objects");
					st.executeUpdate("DROP INDEX IDX_VCS_OBJ_ATTR_LANG_REPL ON " + getDBPrefix() + "t_vcs_objects");
			        log.info("Индекс IDX_VCS_OBJ_ATTR_LANG_REPL с таблицы t_vcs_objects удален");
				} catch (Exception e) {
			        log.error("Индекс IDX_VCS_OBJ_ATTR_LANG_REPL с таблицы t_vcs_objects не удален!");
				}
				
				try {
			        log.info("Удаление индекса IDX_VCS_MODEL_ENTITY_TYPE_ACTION_REPL с таблицы t_vcs_model");
					st.executeUpdate("DROP INDEX IDX_VCS_MODEL_ENTITY_TYPE_ACTION_REPL ON " + getDBPrefix() + "t_vcs_model");
			        log.info("Индекс IDX_VCS_MODEL_ENTITY_TYPE_ACTION_REPL с таблицы t_vcs_model удален");
				} catch (Exception e) {
			        log.error("Индекс IDX_VCS_MODEL_ENTITY_TYPE_ACTION_REPL с таблицы t_vcs_model не удален!");
				}

				try {
			        log.info("Создание уникального индекса IDX_VCS_OBJ_ATTR_LANG на таблице t_vcs_objects");
					st.executeUpdate("CREATE UNIQUE INDEX IDX_VCS_OBJ_ATTR_LANG"
							+ " ON "+getDBPrefix()+"t_vcs_objects(c_obj_id,c_attr_id,c_lang_id,c_fix_end_id)");
			        log.info("Индекс IDX_VCS_OBJ_ATTR_LANG на таблице t_vcs_objects создан");
				} catch (Exception e) {
			        log.error("Индекс IDX_VCS_OBJ_ATTR_LANG с таблице t_vcs_objects не создан!");
				}
				
				try {
			        log.info("Создание индекса IDX_VCS_USER на таблице t_vcs_objects");
					st.executeUpdate("CREATE INDEX IDX_VCS_USER ON "+getDBPrefix()+"t_vcs_objects(c_user_id)");
			        log.info("Индекс IDX_VCS_USER на таблице t_vcs_objects создан");
				} catch (Exception e) {
			        log.error("Индекс IDX_VCS_USER с таблице t_vcs_objects не создан!");
				}

				try {
			        log.info("Создание индекса IDX_VCS_REPL на таблице t_vcs_objects");
					st.executeUpdate("CREATE INDEX IDX_VCS_REPL ON "+getDBPrefix()+"t_vcs_objects(c_repl_id)");
			        log.info("Индекс IDX_VCS_REPL на таблице t_vcs_objects создан");
				} catch (Exception e) {
			        log.error("Индекс IDX_VCS_REPL с таблице t_vcs_objects не создан!");
				}

				try {
			        log.info("Создание уникального индекса IDX_VCS_MODEL_ENTITY_TYPE на таблице t_vcs_model");
					st.executeUpdate("CREATE UNIQUE INDEX IDX_VCS_MODEL_ENTITY_TYPE"
							+ " ON "+getDBPrefix()+"t_vcs_model(c_entity_id,c_type,c_fix_end_id)");
			        log.info("Индекс IDX_VCS_MODEL_ENTITY_TYPE на таблице t_vcs_model создан");
				} catch (Exception e) {
			        log.error("Индекс IDX_VCS_MODEL_ENTITY_TYPE с таблице t_vcs_model не создан!");
				}
				
				try {
			        log.info("Создание индекса IDX_VCS_MODEL_USER на таблице t_vcs_model");
					st.executeUpdate("CREATE INDEX IDX_VCS_MODEL_USER ON "+getDBPrefix()+"t_vcs_model(c_user_id)");
			        log.info("Индекс IDX_VCS_MODEL_USER на таблице t_vcs_model создан");
				} catch (Exception e) {
			        log.error("Индекс IDX_VCS_MODEL_USER с таблице t_vcs_model не создан!");
				}

				try {
			        log.info("Создание индекса IDX_VCS_MODEL_REPL на таблице t_vcs_model");
					st.executeUpdate("CREATE INDEX IDX_VCS_MODEL_REPL ON "+getDBPrefix()+"t_vcs_model(c_repl_id)");
			        log.info("Индекс IDX_VCS_MODEL_REPL на таблице t_vcs_model создан");
				} catch (Exception e) {
			        log.error("Индекс IDX_VCS_MODEL_REPL с таблице t_vcs_model не создан!");
				}

				log.info("Апгрейд БД до версии 42 успешно завершен!");
				st.close();

			log.info("Апгрейд БД до версии 43 ...");
			log.info("Добавление полей c_before_create_obj_tr (транзакция события 'Перед созданием объекта'), "
					+ "c_after_create_obj_tr (транзакция события 'После создания объекта'), "
					+ "c_before_delete_obj_tr (транзакция события 'Перед удалением объекта'), "
					+ "c_after_delete_obj_tr (транзакция события 'После удаления объекта') в таблице t_classes.");
			log.info("Добавление полей c_before_event_tr (транзакция события 'Перед изменением значения атрибута'), "
					+ "c_after_event_tr (транзакция события 'После изменения значения атрибута'), "
					+ "c_before_del_event_tr (транзакция события 'Перед удалением значения атрибута'), "
					+ "c_after_del_event_tr (транзакция события 'После удаления значения атрибута') в таблице t_attrs.");
			
			st = conn.createStatement();
				if (!isColumnExists("t_classes", "c_before_create_obj_tr")) {
					st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD COLUMN c_before_create_obj_tr INTEGER NOT NULL DEFAULT 0;");
					log.info("Колонка c_before_create_obj_tr добавлена.");
				} else {
					log.info("Колонка c_before_create_obj_tr уже создана.");
				}
				if (!isColumnExists("t_classes", "c_after_create_obj_tr")) {
					st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD COLUMN c_after_create_obj_tr INTEGER NOT NULL DEFAULT 0;");
					log.info("Колонка c_after_create_obj_tr добавлена.");
				} else {
					log.info("Колонка c_after_create_obj_tr уже создана.");
				}
				if (!isColumnExists("t_classes", "c_before_delete_obj_tr")) {
					st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD COLUMN c_before_delete_obj_tr INTEGER NOT NULL DEFAULT 0;");
					log.info("Колонка c_before_delete_obj_tr добавлена.");
				} else {
					log.info("Колонка c_before_delete_obj_tr уже создана.");
				}
				if (!isColumnExists("t_classes", "c_after_delete_obj_tr")) {
					st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD COLUMN c_after_delete_obj_tr INTEGER NOT NULL DEFAULT 0;");
					log.info("Колонка c_after_delete_obj_tr добавлена.");
				} else {
					log.info("Колонка c_after_delete_obj_tr уже создана.");
				}
				
				if (!isColumnExists("t_attrs", "c_before_event_tr")) {
					st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD COLUMN c_before_event_tr INTEGER NOT NULL DEFAULT 0;");
					log.info("Колонка c_before_event_tr добавлена.");
				} else {
					log.info("Колонка c_before_event_tr уже создана.");
				}
				if (!isColumnExists("t_attrs", "c_after_event_tr")) {
					st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD COLUMN c_after_event_tr INTEGER NOT NULL DEFAULT 0;");
					log.info("Колонка c_after_event_tr добавлена.");
				} else {
					log.info("Колонка c_after_event_tr уже создана.");
				}
				if (!isColumnExists("t_attrs", "c_before_del_event_tr")) {
					st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD COLUMN c_before_del_event_tr INTEGER NOT NULL DEFAULT 0;");
					log.info("Колонка c_before_del_event_tr добавлена.");
				} else {
					log.info("Колонка c_before_del_event_tr уже создана.");
				}
				if (!isColumnExists("t_attrs", "c_after_del_event_tr")) {
					st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD COLUMN c_after_del_event_tr INTEGER NOT NULL DEFAULT 0;");
					log.info("Колонка c_after_del_event_tr добавлена.");
				} else {
					log.info("Колонка c_after_del_event_tr уже создана.");
				}
				st.close();
			log.info("Апгрейд БД до версии 43 успешно завершен.");
			
			kz.tamur.or3.util.Tname.version = 43;
	}
	
	public void upgradeTo50() throws DriverException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 50 ...");
        log.info("Создание класса 'ConfigGlobalFix'");

        try {
		    sysCls = getClassByNameComp("Системный класс");
		    // Создать классы
		    createClassConfigGlobalFix();
		    db.addClass(getClassByNameComp(NAME_CLASS_CONFIG_GLOBAL_FIX), false);
		    db.addClass(getClassByNameComp(NAME_CLASS_PROPERTY), false);

		    createAttributesForClassConfigGlobalFix();

			log.info("Апгрейд БД до версии 50 успешно завершен!");
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
		} finally {
			isUpgrading = false;
		}
	}

	private void createClassConfigGlobalFix() throws SQLException, DriverException {
	    // проверка наличия родительского класса
	    if (!checkExistenceClassByName("Config")) {
	        db.addClass(createClass("Config", sysCls.id, false, 159, "159", null), false);
	    }
	    if (!checkExistenceClassByName(NAME_CLASS_CONFIG_GLOBAL_FIX)) {
	        db.addClass(createClass(NAME_CLASS_CONFIG_GLOBAL_FIX, getClassByNameComp("Config").id, false, -1, "ffffffff-9a84-4bbd-9630-6215d81a1869", "CONFIGGLOBALFIX"), false);
	    }
	}

	public void createAttributesForClassConfigGlobalFix() throws DriverException, SQLException {
	    KrnClass clsCnf = getClassByNameComp(NAME_CLASS_CONFIG_GLOBAL_FIX);
	    KrnAttribute attr = null;
	    
	    if (getAttributeByNameComp(clsCnf, ATTR_COLOR_BACK_TAB_TITLE) == null) {
	        attr = createAttribute(-1, "ffffffff-69ad-4236-9cc9-c95eb55afde1", clsCnf.id, CID_STRING, ATTR_COLOR_BACK_TAB_TITLE, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Цвет фона заголовка фоновых вкладок.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_COLOR_FONT_BACK_TAB_TITLE) == null) {
	        attr = createAttribute(-1, "ffffffff-0e5a-4ec0-9fee-b9bdeb0cbfb1", clsCnf.id, CID_STRING, ATTR_COLOR_FONT_BACK_TAB_TITLE, 0, false, false, false, false, 0,
	                0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Цвет шрифта заголовка фоновых вкладок.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_COLOR_FONT_TAB_TITLE) == null) {
	        attr = createAttribute(-1, "ffffffff-b700-4bdb-b109-a66f26b31039", clsCnf.id, CID_STRING, ATTR_COLOR_FONT_TAB_TITLE, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Цвет шрифта заголовка выбранной вкладки.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_COLOR_HEADER_TABLE) == null) {
	        attr = createAttribute(-1, "ffffffff-6426-4b24-8bc5-7f766a5b39cc", clsCnf.id, CID_STRING, ATTR_COLOR_HEADER_TABLE, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Цвет заголовков таблиц и деревьев.");
	
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_COLOR_MAIN) == null) {
	        attr = createAttribute(-1, "ffffffff-889a-4870-9688-12b6e65ba04e", clsCnf.id, CID_STRING, ATTR_COLOR_MAIN, 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Цвет, определяющий основную цветовую гамму интерфейсов.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_COLOR_TAB_TITLE) == null) {
	        attr = createAttribute(-1, "ffffffff-6ceb-4085-b4e6-e1f52a7332f3", clsCnf.id, CID_STRING, ATTR_COLOR_TAB_TITLE, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Цвет фона заголовка выбранной вкладки.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_GRADIENT_CONTROL_PANEL) == null) {
	        attr = createAttribute(-1, "ffffffff-2555-45fd-894b-7342fae73ef9", clsCnf.id, CID_STRING, ATTR_GRADIENT_CONTROL_PANEL, 0, false, false, false, false, 0, 0,
	                0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Параметры градиентной заливки для панели управления системы.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_GRADIENT_FIELD_NO_FLC) == null) {
	        attr = createAttribute(-1, "ffffffff-5ea5-432f-a91a-d1adeaa75672", clsCnf.id, CID_STRING, ATTR_GRADIENT_FIELD_NO_FLC, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Параметры градиентной заливки для полей не прошедших ФЛК.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_GRADIENT_MAIN_FRAME) == null) {
	        attr = createAttribute(-1, "ffffffff-fa05-4589-b04c-b5e828636a33", clsCnf.id, CID_STRING, ATTR_GRADIENT_MAIN_FRAME, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Параметры градиентной заливки для главного фрейма системы.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_GRADIENT_MENU_PANEL) == null) {
	        attr = createAttribute(-1, "ffffffff-5032-408d-8073-f8ec3ae30e04", clsCnf.id, CID_STRING, ATTR_GRADIENT_MENU_PANEL, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Параметры градиентной заливки для панели меню системы.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_TRANSPARENT_BACK_TAB_TITLE) == null) {
	        attr = createAttribute(-1, "ffffffff-138c-4259-9914-dc1c416ac109", clsCnf.id, CID_INTEGER, ATTR_TRANSPARENT_BACK_TAB_TITLE, 0, false, false, false, false, 0,
	                0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Прозрачность заголовка фоновой вкладки");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_TRANSPARENT_CELL_TABLE) == null) {
	        attr = createAttribute(-1, "ffffffff-ab80-46f7-af82-8af329412ff4", clsCnf.id, CID_INTEGER, ATTR_TRANSPARENT_CELL_TABLE, 0, false, false, false, false, 0, 0,
	                0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Прозрачность ячеек таблиц и деревьев");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_TRANSPARENT_MAIN) == null) {
	        attr = createAttribute(-1, "ffffffff-0294-4bac-8dba-9cf5479d8633", clsCnf.id, CID_INTEGER, ATTR_TRANSPARENT_MAIN, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Прозрачность панелей системы, позволяет включить/выключить возможность работы с ней.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_TRANSPARENT_DIALOG) == null) {
	        attr = createAttribute(-1, "ffffffff-07e5-4834-a94e-4313a2e5e90d", clsCnf.id, CID_INTEGER, ATTR_TRANSPARENT_DIALOG, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Прозрачность диалогов системы, позволяет включить/выключить возможность работы с ней.");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_TRANSPARENT_SELECTED_TAB_TITLE) == null) {
	        attr = createAttribute(-1, "ffffffff-24c0-467b-bef4-7d114effa7df", clsCnf.id, CID_INTEGER, ATTR_TRANSPARENT_SELECTED_TAB_TITLE, 0, false, false, false,
	                false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Прозрачность фона заголовка выбранной вкладки");
	    }
	
	    if (getAttributeByNameComp(clsCnf, ATTR_BLUE_SYS_COLOR) == null) {
	        attr = createAttribute(-1, "ffffffff-f289-44ea-9fdb-415022763211", clsCnf.id, CID_STRING, ATTR_BLUE_SYS_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной blueSysColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_DARK_SHADOW_SYS_COLOR) == null) {
	        attr = createAttribute(-1, "ffffffff-ba1f-4c39-88e7-ef43e9cab383", clsCnf.id, CID_STRING, ATTR_DARK_SHADOW_SYS_COLOR, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной darkShadowSysColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_MID_SYS_COLOR) == null) {
	        attr = createAttribute(-1, "ffffffff-8517-4e06-9d93-fb25cf59dfbb", clsCnf.id, CID_STRING, ATTR_MID_SYS_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной midSysColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_LIGHT_YELLOW_COLOR) == null) {
	        attr = createAttribute(-1, "ffffffff-4770-4199-bd90-39101cb089bb", clsCnf.id, CID_STRING, ATTR_LIGHT_YELLOW_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной lightYellowColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_RED_COLOR) == null) {
	        attr = createAttribute(-1, "ffffffff-fba2-444f-ae08-54843763bdbd", clsCnf.id, CID_STRING, ATTR_RED_COLOR, 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной redColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_LIGHT_RED_COLOR) == null) {
	        attr = createAttribute(-1, "ffffffff-8928-455d-bd28-25976742a5e6", clsCnf.id, CID_STRING, ATTR_LIGHT_RED_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной lightRedColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_LIGHT_GREEN_COLOR) == null) {
	        attr = createAttribute(-1, "ffffffff-e24b-451c-8b3a-5dc47dcb204d", clsCnf.id, CID_STRING, ATTR_LIGHT_GREEN_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной lightGreenColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_SHADOW_YELLOW_COLOR) == null) {
	        attr = createAttribute(-1, "ffffffff-678d-46f6-a236-8ee9778be1db", clsCnf.id, CID_STRING, ATTR_SHADOW_YELLOW_COLOR, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной shadowYellowColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_SYS_COLOR) == null) {
	        attr = createAttribute(-1, "ffffffff-f093-460b-9109-e8d97828ec74", clsCnf.id, CID_STRING, ATTR_SYS_COLOR, 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной sysColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_LIGHT_SYS_COLOR) == null) {
	        attr = createAttribute(-1, "ffffffff-31da-4d80-92a1-52a6e62c7aa4", clsCnf.id, CID_STRING, ATTR_LIGHT_SYS_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной lightSysColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_DEFAULT_FONT_COLOR) == null) {
	        attr = createAttribute(-1, "ffffffff-9f8b-4117-a9f2-d161cd06f691", clsCnf.id, CID_STRING, ATTR_DEFAULT_FONT_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной defaultFontColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_SILVER_COLOR) == null) {
	        attr = createAttribute(-1, "ffffffff-602b-4f03-8722-be4c7bbdc620", clsCnf.id, CID_STRING, ATTR_SILVER_COLOR, 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной silverColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_SHADOWS_GREY_COLOR) == null) {
	        attr = createAttribute(-1, "ffffffff-d606-42e0-982e-d1e2db170a57", clsCnf.id, CID_STRING, ATTR_SHADOWS_GREY_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной shadowsGreyColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_KEYWORD_COLOR) == null) {
	        attr = createAttribute(-1, "ffffffff-aae5-4f7f-a2f5-9b372a8b48d5", clsCnf.id, CID_STRING, ATTR_KEYWORD_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной keywordColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_VARIABLE_COLOR) == null) {
	        attr = createAttribute(-1, "ffffffff-d06b-4fc7-a461-bab3ce0aa0f8", clsCnf.id, CID_STRING, ATTR_VARIABLE_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной variableColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_CLIENT_VARIABLE_COLOR) == null) {
	        attr = createAttribute(-1, "ffffffff-a7ca-47d4-a06b-4e14e57e568f", clsCnf.id, CID_STRING, ATTR_CLIENT_VARIABLE_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной clientVariableColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_COMMENT_COLOR) == null) {
	        attr = createAttribute(-1, "ffffffff-6920-43d5-9698-06c9fac7a763", clsCnf.id, CID_STRING, ATTR_COMMENT_COLOR, 0, false, false, false, false, 0, 0, 0, 0,
	                false, null, 0);
	        setAttributeComment(attr.uid, "Связан с системной переменной commentColor");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_OBJECT_BROWSER_LIMIT) == null) {
	        attr = createAttribute(-1, "ffffffff-716a-4f89-8f52-674a4c32563f", clsCnf.id, CID_INTEGER, ATTR_OBJECT_BROWSER_LIMIT, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Лимит количества объектов класса для отображения");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_OBJECT_BROWSER_LIMIT_FOR_CLASSES) == null) {
	        attr = createAttribute(-1, "ffffffff-414d-4595-930e-cbd5099fc772", clsCnf.id, getClassByNameComp(NAME_CLASS_PROPERTY).id, ATTR_OBJECT_BROWSER_LIMIT_FOR_CLASSES, COLLECTION_SET, false, false,
	                false, false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid,
	                "Набор лимитов количества объектов для отображения. Задаёт индивидуальные, для каждого класса, лимиты.");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_IS_OBJECT_BROWSER_LIMIT) == null) {
	        attr = createAttribute(-1, "ffffffff-c65a-453e-a438-daa140e959b3", clsCnf.id, CID_BOOL, ATTR_IS_OBJECT_BROWSER_LIMIT, 0, false, false, false, false, 0, 0, 0,
	                0, false, null, 0);
	        setAttributeComment(attr.uid, "Активирует лимитированное отображение классов.");
	    }
	    if (getAttributeByNameComp(clsCnf, ATTR_IS_OBJECT_BROWSER_LIMIT_FOR_CLASSES) == null) {
	        attr = createAttribute(-1, "ffffffff-8ef5-496b-b6a0-079665b55ac2", clsCnf.id, CID_BOOL, ATTR_IS_OBJECT_BROWSER_LIMIT_FOR_CLASSES, 0, false, false, false,
	                false, 0, 0, 0, 0, false, null, 0);
	        setAttributeComment(attr.uid, "Активирует индивидуальное лимитированное отображение классов.");
	    }
	}
	
	public void upgradeTo51() throws SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 51 ...");
		Statement st = conn.createStatement();
		try {
	        log.info("Добавление колонки 'c_thread' в таблицу 't_syslog'...");
			st.executeUpdate(
					"ALTER TABLE " + getDBPrefix() + "t_syslog add ("
					+ "c_thread VARCHAR(1024)"
					+ ")");
			log.info("Апгрейд БД до версии 51 успешно завершен!");
		} catch (SQLException e) {
			log.error("Ошибка при апгрейде БД до версии 51!");
			throw e;
		} finally {
			st.close();
		}
        isUpgrading = false;
	}

	public void upgradeTo52() throws SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 52 ...");
		Statement st = conn.createStatement();
		try {
	        log.info("Создание таблицы 't_mutex' для синхронизации серверов кластера...");
			
	        st.executeUpdate(
					"CREATE TABLE " + getDBPrefix() + "t_mutex ("
					+ "c_muid CHAR(50) NOT NULL,"
					+ "PRIMARY KEY(c_muid)"
					+ ")");
			
			log.info("Апгрейд БД до версии 52 успешно завершен!");
		} catch (SQLException e) {
			log.error("Ошибка при апгрейде БД до версии 52!");
			throw e;
		} finally {
			st.close();
		}
        isUpgrading = false;
	}
	
	public void upgradeTo53() throws SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 53 ...");
        log.info("Создание класса TimerProtocol...");
		try {
			KrnClass cls_timer = getClassByNameComp("Timer");
			KrnClass cls_timer_protocol = getClassByNameComp("TimerProtocol");
			if(cls_timer_protocol==null){
				cls_timer_protocol=createClass("TimerProtocol", cls_timer.parentId, false, 0,-1, null, false,"TIMERPROTOCOL");
				KrnAttribute attr_protocol_status=createAttribute(-1, null, cls_timer_protocol.id, CID_BOOL,"status", 0, false, false,
													false,false, 0, 0, 0, 0, false, false, "STATUS", 0);
				KrnAttribute attr_protocol_start=createAttribute(-1, null, cls_timer_protocol.id, CID_TIME,"timeStart", 0, false, false,
						false,false, 0, 0, 0, 0, false, false, "TIMESTART", 0);
				KrnAttribute attr_protocol_startNext=createAttribute(-1, null, cls_timer_protocol.id, CID_TIME,"timeNextStart", 0, false, false,
						false,false, 0, 0, 0, 0, false, false, "TIMENEXTSTART", 0);
				KrnAttribute attr_protocol_err=createAttribute(-1, null, cls_timer_protocol.id, CID_STRING,"err", 0, false, false,
						false,false, 0, 0, 0, 0, false, false, "ERR", 0);
				KrnAttribute attr_protocol_timer=createAttribute(-1, null, cls_timer_protocol.id, cls_timer.id,"timer", 0, false, false,
						false,false, 0, 0, 0, 0, false, false, "TIMER", 0);
				try{
					KrnAttribute attr_p_timer = getAttributeByNameComp(cls_timer, "protocol");
					deleteAttribute(attr_p_timer.id);
					
				}catch(Exception ex){
					log.error("Ошибка при удалении атрибута!");
				}
			}
			log.info("Апгрейд БД до версии 53 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 53!");
		} 
        isUpgrading = false;
	}
	
	public void upgradeTo54() throws SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 54 ...");
        log.info("Создание атрибута 'активировать подписание обязательства о неразглашении' типа boolean в классе 'Политика учетных записей'.");
        log.info("Создание атрибута 'срок действия подписи обязательства о неразглашении' типа long в классе 'Политика учетных записей'.");
        log.info("Создание атрибута 'дата подписания обязательства о неразглашении' типа time в классе 'User'.");
		try {
	        KrnClass policyCls = getClassByNameComp("Политика учетных записей");        
	        KrnClass userCls = getClassByNameComp("User");        
	        db.addClass(policyCls, true);
	        db.addClass(userCls, true);
	        if ((getAttributeByNameComp(policyCls, "активировать подписание обязательства о неразглашении")) == null) {
	            createAttribute(-1, "79af2ef8-aa8c-4a93-8dc3-51255a50b285", policyCls.id, CID_BOOL, "активировать подписание обязательства о неразглашении", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
	        if ((getAttributeByNameComp(policyCls, "срок действия подписи обязательства о неразглашении")) == null) {
	            createAttribute(-1, "3055ced3-974c-472e-81f1-02f92a0eafe1", policyCls.id, CID_INTEGER, "срок действия подписи обязательства о неразглашении", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
	        if ((getAttributeByNameComp(userCls, "дата подписания обязательства о неразглашении")) == null) {
	            createAttribute(-1, "80fe25d9-bee3-40f0-b957-b27b139f16b8", userCls.id, CID_TIME, "дата подписания обязательства о неразглашении", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
	        log.info("Апгрейд БД до версии 54 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 54!");
		} 

        isUpgrading = false;
    }
	
	public void upgradeTo55() throws SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 55 ...");
        log.info("Создание атрибута 'активировать оповещение об истечении срока действия ЭЦП' типа boolean в классе 'Политика учетных записей'.");
        log.info("Создание атрибута 'период оповещения срока действия ЭЦП' типа long в классе 'Политика учетных записей'.");
        log.info("Создание атрибута 'активировать оповещение об истечении срока временной регистрации' типа boolean в классе 'Политика учетных записей'.");
        log.info("Создание атрибута 'период оповещения об истечении срока временной регистрации' типа long в классе 'Политика учетных записей'.");
        log.info("Создание атрибута 'дата истечения срока временной регистрации' типа date в классе 'User'.");

        log.info("Объединение основной ветки и ветки ГБД ЮЛ ...");
		Statement st = conn.createStatement();
		try {
	        KrnClass policyCls = getClassByNameComp("Политика учетных записей");        
	        KrnClass userCls = getClassByNameComp("User");        
	        db.addClass(policyCls, true);
	        db.addClass(userCls, true);
	        if ((getAttributeByNameComp(policyCls, "активировать оповещение об истечении срока действия ЭЦП")) == null) {
	            createAttribute(-1, "80627f2e-1867-404e-85b8-242423215df4", policyCls.id, CID_BOOL, "активировать оповещение об истечении срока действия ЭЦП", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
	        if ((getAttributeByNameComp(policyCls, "период оповещения срока действия ЭЦП")) == null) {
	            createAttribute(-1, "3b5eaadb-209d-4fe0-9ff9-b7d98e5d13f0", policyCls.id, CID_INTEGER, "период оповещения срока действия ЭЦП", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
	        if ((getAttributeByNameComp(policyCls, "активировать оповещение об истечении срока временной регистрации")) == null) {
	            createAttribute(-1, "587c974e-4209-48d6-bdea-0905969eb22b", policyCls.id, CID_BOOL, "активировать оповещение об истечении срока временной регистрации", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
	        if ((getAttributeByNameComp(policyCls, "период оповещения об истечении срока временной регистрации")) == null) {
	            createAttribute(-1, "fbd74566-c185-4bf0-9668-d0ae868061ce", policyCls.id, CID_INTEGER, "период оповещения об истечении срока временной регистрации", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
	        if ((getAttributeByNameComp(userCls, "дата истечения срока временной регистрации")) == null) {
	            createAttribute(-1, "88c366fc-0a75-42a5-80de-8ff699ec558f", userCls.id, CID_DATE, "дата истечения срока временной регистрации", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }

			//t_vcs_objects
			log.info("Добавление поля c_old_user_id в таблицу t_vcs_objects");
			if (!isColumnExists("t_vcs_objects", "c_old_user_id")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD COLUMN c_old_user_id BIGINT");
				log.info("Колонка c_old_user_id добавлена.");
			} else {
				log.info("Колонка c_old_user_id уже создана.");
			}
			
	        log.info("Удаление индекса IDX_VCS_REPL с таблицы t_vcs_objects");
			try {
				st.executeUpdate("DROP INDEX IDX_VCS_REPL ON " + getDBPrefix() + "t_vcs_objects");
		        log.info("Индекс IDX_VCS_REPL с таблицы t_vcs_objects удален");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_REPL с таблицы t_vcs_objects не удален!");
			}
			
	        log.info("Переименование колонки 'c_repl_id' в 'c_rimport_id' таблицы 't_vcs_objects'");
			try {
				renameColumn(isSchemeName ? db.getSchemeName() : null, "t_vcs_objects", "c_repl_id", "c_rimport_id", "BIGINT");
		        log.info("Успешно!");
			} catch (Exception e) {
		        log.error("Произошла ошибка!");
		        log.error(e, e);
			}
				
	        log.info("Создание индекса IDX_VCS_RIMP на таблице t_vcs_objects");
			try {
				st.executeUpdate("CREATE INDEX IDX_VCS_RIMP ON "+getDBPrefix()+"t_vcs_objects(c_rimport_id)");
		        log.info("Индекс IDX_VCS_RIMP на таблице t_vcs_objects создан");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_RIMP с таблице t_vcs_objects не создан!");
			}
			
			log.info("Добавление поля c_rexport_id в таблицу t_vcs_objects");
			if (!isColumnExists("t_vcs_objects", "c_rexport_id")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD COLUMN c_rexport_id BIGINT");
				log.info("Колонка c_rexport_id добавлена.");
			} else {
				log.info("Колонка c_rexport_id уже создана.");
			}

	        log.info("Создание индекса IDX_VCS_REXP на таблице t_vcs_objects");
			try {
				st.executeUpdate("CREATE INDEX IDX_VCS_REXP ON "+getDBPrefix()+"t_vcs_objects(c_rexport_id)");
		        log.info("Индекс IDX_VCS_REXP на таблице t_vcs_objects создан");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_REXP с таблице t_vcs_objects не создан!");
			}
			//t_vcs_model
			log.info("Добавление поля c_old_user_id в таблицу t_vcs_model");
			if (!isColumnExists("t_vcs_model", "c_old_user_id")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD COLUMN c_old_user_id BIGINT");
				log.info("Колонка c_old_user_id добавлена.");
			} else {
				log.info("Колонка c_old_user_id уже создана.");
			}
			
	        log.info("Удаление индекса IDX_VCS_MODEL_REPL с таблицы t_vcs_model");
			try {
				st.executeUpdate("DROP INDEX IDX_VCS_MODEL_REPL ON " + getDBPrefix() + "t_vcs_model");
		        log.info("Индекс IDX_VCS_MODEL_REPL с таблицы t_vcs_model удален");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_MODEL_REPL с таблицы t_vcs_model не удален!");
			}
			
	        log.info("Переименование колонки 'c_repl_id' в 'c_rimport_id' таблицы 't_vcs_model'");
			try {
				renameColumn(isSchemeName ? db.getSchemeName() : null, "t_vcs_model", "c_repl_id", "c_rimport_id", "BIGINT");
		        log.info("Успешно!");
			} catch (Exception e) {
		        log.error("Произошла ошибка!");
		        log.error(e, e);
			}
				
	        log.info("Создание индекса IDX_VCS_MODEL_RIMP на таблице t_vcs_model");
			try {
				st.executeUpdate("CREATE INDEX IDX_VCS_MODEL_RIMP ON "+getDBPrefix()+"t_vcs_model(c_rimport_id)");
		        log.info("Индекс IDX_VCS_MODEL_RIMP на таблице t_vcs_model создан");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_MODEL_RIMP с таблице t_vcs_model не создан!");
			}
			
			log.info("Добавление поля c_rexport_id в таблицу t_vcs_model");
			if (!isColumnExists("t_vcs_model", "c_rexport_id")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD COLUMN c_rexport_id BIGINT");
				log.info("Колонка c_rexport_id добавлена.");
			} else {
				log.info("Колонка c_rexport_id уже создана.");
			}

	        log.info("Создание индекса IDX_VCS_MODEL_REXP на таблице t_vcs_model");
			try {
				st.executeUpdate("CREATE INDEX IDX_VCS_MODEL_REXP ON "+getDBPrefix()+"t_vcs_model(c_rexport_id)");
		        log.info("Индекс IDX_VCS_MODEL_REXP на таблице t_vcs_model создан");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_MODEL_REXP с таблице t_vcs_model не создан!");
			}
			/*
			 * Для системной базы нужно будет вручную переписать данные как для t_vcs_objects так и t_vcs_model
			 *  из c_rimport_id в c_rexport_id после чего c_rimport_id обнулить
			 */
			log.info("Апгрейд БД до версии 55 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 55!");
		} 
        isUpgrading = false;
    }

	public void upgradeTo56() throws SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 56 ...");
        log.info("Создание класса 'ContactInfo'.");
        log.info("Создание атрибута 'email' типа string в классе 'ContactInfo'.");
        log.info("Создание атрибута 'freeTextMessage' типа memo в классе 'ContactInfo'.");
        log.info("Создание атрибута 'person' типа string в классе 'ContactInfo'.");
        log.info("Создание атрибута 'telephone' типа string в классе 'ContactInfo'.");
        log.info("Создание атрибута 'regOrgan' типа long в классе 'ContactInfo'.");
        log.info("Создание атрибута 'аватар' типа blob в классе 'User'.");

		try {
	        KrnClass contactInfoCls = getClassByNameComp("ContactInfo");
	        if (contactInfoCls == null) {
	        	contactInfoCls = createClass("ContactInfo", getClassByNameComp("Системный класс").id, false, 0, -1, "196c341f-6506-4f8e-b16e-5a66cc73d73c", false, null);
	        } else {
	        	db.addClass(contactInfoCls, true);
	        }
	        if ((getAttributeByNameComp(contactInfoCls, "email")) == null) {
	            createAttribute(-1, "9c6a12f8-90a8-48eb-829e-9aed4960ae3f", contactInfoCls.id, CID_STRING, "email", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
	        if ((getAttributeByNameComp(contactInfoCls, "freeTextMessage")) == null) {
	            createAttribute(-1, "1a607ad4-0d11-42a0-a7f0-9b0756e8a749", contactInfoCls.id, CID_MEMO, "freeTextMessage", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
	        if ((getAttributeByNameComp(contactInfoCls, "person")) == null) {
	            createAttribute(-1, "9e60614f-3e71-4073-8fe5-c83706dbb297", contactInfoCls.id, CID_STRING, "person", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
	        if ((getAttributeByNameComp(contactInfoCls, "telephone")) == null) {
	            createAttribute(-1, "94d3d3a3-d950-4b09-bf64-5968d9e18516", contactInfoCls.id, CID_STRING, "telephone", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
	        if ((getAttributeByNameComp(contactInfoCls, "regOrgan")) == null) {
	        	KrnClass regOrganCls = getClassByNameComp("Регистрирующий орган");
	        	if (regOrganCls != null) {
	        		db.addClass(regOrganCls, true);
	        	}
	            createAttribute(-1, "741cf1c2-28c5-47a6-a72a-b2e840a23433", contactInfoCls.id, regOrganCls != null ? regOrganCls.id : CID_INTEGER, "regOrgan", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
	        
	        KrnClass userCls = getClassByNameComp("User");        
	        db.addClass(userCls, true);
	        if ((getAttributeByNameComp(userCls, "аватар")) == null) {
	            createAttribute(-1, "5a56c0ca-14e9-47a9-956d-b08b7c71c118", userCls.id, CID_BLOB, "аватар", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
			log.info("Апгрейд БД до версии 56 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 56!");
		} 
        isUpgrading = false;
    }
	
	public void upgradeTo57() throws SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 57 ...");
        log.info("Добавление в таблицы t_vcs_objects,t_vcs_model колонки c_mod_confirm_time");
		Statement st = conn.createStatement();
		if (!isColumnExists("t_vcs_objects", "c_mod_confirm_time"))
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_mod_confirm_time DATETIME DEFAULT null");
		if (!isColumnExists("t_vcs_model", "c_mod_confirm_time"))
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_mod_confirm_time DATETIME DEFAULT null");
		log.info("Апгрейд БД до версии 57 успешно завершен!");
		isUpgrading = false;
    }
	
	public void upgradeTo58() throws SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 58 ...");
        log.info("Создание атрибута 'проверять ip-адрес клиента' типа boolean в классе 'Политика учетных записей'.");
        log.info("Создание атрибута 'ip_address' типа string в классе 'User'.");
		try {
	        KrnClass policyCls = getClassByNameComp("Политика учетных записей");
        	db.addClass(policyCls, true);
	        if ((getAttributeByNameComp(policyCls, "проверять ip-адрес клиента")) == null) {
	            createAttribute(-1, "d1064660-9711-4bc4-a3c2-0b7fb4f0ce5b", policyCls.id, CID_BOOL, "проверять ip-адрес клиента", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
	        KrnClass userCls = getClassByNameComp("User");        
	        db.addClass(userCls, true);
	        if ((getAttributeByNameComp(userCls, "ip_address")) == null) {
	            createAttribute(-1, "5a638c48-c1dc-45b1-898b-e026022c5092", userCls.id, CID_STRING, "ip_address", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
	        if ((getAttributeByNameComp(userCls, "isFolder")) == null) {
	            createAttribute(-1, "f7521ade-b2d3-4c26-8789-1a762c4a2cda", userCls.id, CID_BOOL, "isFolder", 0, false, false, false, true, 0, 0, 0, 0, false, false, null, 0);
	        }
			log.info("Апгрейд БД до версии 58 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 58!");
		} 
        isUpgrading = false;
    }
	
	public void upgradeTo59() throws DriverException, SQLException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 59 ...");
		log.info("Добавление поля c_access_modifier (модификатор доступа атрибута) в таблице t_attrs.");
		Statement st = conn.createStatement();
		try {
			if (!isColumnExists("t_attrs", "c_access_modifier")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD COLUMN c_access_modifier INTEGER NOT NULL DEFAULT 0;");
				log.info("Колонка c_access_modifier добавлена.");
			} else {
				log.info("Колонка c_access_modifier уже создана.");
			}
		} finally {
			st.close();
		}
		log.info("Апгрейд БД до версии 59 успешно завершен.");
        isUpgrading = false;
	}
	
	public void upgradeTo60() throws SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 60 ...");
        log.info("Создание атрибута 'запрет использования собственных идентификационных данных в пароле' типа boolean в классе 'Политика учетных записей'.");
		try {
	        KrnClass policyCls = getClassByNameComp("Политика учетных записей");
        	db.addClass(policyCls, true);
	        if ((getAttributeByNameComp(policyCls, "запрет использования собственных идентификационных данных в пароле")) == null) {
	            createAttribute(-1, "1a6439b5-f085-4fc7-adb6-63c92624c07f", policyCls.id, CID_BOOL, "запрет использования собственных идентификационных данных в пароле", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
			log.info("Апгрейд БД до версии 60 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 60!");
		} 
        isUpgrading = false;
    }
	
	public void upgradeTo61() throws SQLException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 61 ...");
		log.info("Создание атрибута 'lastLogoutTime' типа time в классе 'User'.");
		try{
			KrnClass userCls = getClassByNameComp("User");        
	        db.addClass(userCls, true);
	        if ((getAttributeByNameComp(userCls,"lastLogoutTime")) == null){
	        	createAttribute(-1, "b51eaa0a-37e0-4248-807c-26bf9dfcaded", userCls.id, CID_TIME, "lastLogoutTime", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
	        }
	        log.info("Апгрейд БД до версии 61 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 61!");
		}
		isUpgrading = false;
	}
	
	public void upgradeTo62() throws SQLException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 62...");
		log.info("Создание атрибута 'isChangedPassBySys' типа boolean в классе 'User'.");
		try{
			KrnClass userCls = getClassByNameComp("User");
			db.addClass(userCls,true);
			if ((getAttributeByNameComp(userCls,"isChangedPassBySys")) == null){
				createAttribute(-1, "f69a8eee-8495-466c-9fb6-5f20a4b06d58", userCls.id, CID_BOOL, "isChangedPassBySys", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
			}
			log.info("Апгрейд до версии 62 успешно завершен!");
		} catch (DriverException e){
			log.error("Ошибка при апгрейде БД до версии 62!");
		}
		isUpgrading = false;
	}
	
	public void upgradeTo63() throws SQLException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 63 ...");
		log.info("Создание атрибута 'srvHistory', 'ifcHistory','fltHistory', 'rptHistory' типа time в классе 'ConfigLocal'.");
		try{
			KrnClass confCls = getClassByNameComp("ConfigLocal");        
	        db.addClass(confCls, true);
	        if ((getAttributeByNameComp(confCls,"srvHistory")) == null){
	        	createAttribute(-1, "ac954073-5a5a-4634-b7e0-c807c2d28649", confCls.id, CID_BLOB, "srvHistory", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
	        }
	        if ((getAttributeByNameComp(confCls,"ifcHistory")) == null){
	        	createAttribute(-1, "888a7d67-42fd-4e6b-a85b-5a1009d5bba6", confCls.id, CID_BLOB, "ifcHistory", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
	        }
	        if ((getAttributeByNameComp(confCls,"fltHistory")) == null){
	        	createAttribute(-1, "fcd54576-30b2-442c-8b20-01f004917566", confCls.id, CID_BLOB, "fltHistory", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
	        }
	        if ((getAttributeByNameComp(confCls,"rptHistory")) == null){
	        	createAttribute(-1, "f0f73508-97ee-4ce8-b37a-752b22bb30df", confCls.id, CID_BLOB, "rptHistory", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
	        }
	        
	        log.info("Апгрейд БД до версии 63 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 63!");
		}
		isUpgrading = false;
	}  
	
	public void upgradeTo64() throws SQLException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 64 ...");
		log.info("Создание атрибута 'showTooltip' типа boolean в классе 'User'.");
		try{
			KrnClass userCls = getClassByNameComp("User");        
	        db.addClass(userCls, true);
	        if ((getAttributeByNameComp(userCls,"showTooltip")) == null){
	        	createAttribute(-1, "716cfc23-667a-4f18-9327-8f2f5c9156b0", userCls.id, CID_BOOL, "showTooltip", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
	        }
	        
	        log.info("Апгрейд БД до версии 64 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 64!");
		}
		isUpgrading = false;
	}

	public void upgradeTo65() throws SQLException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 65 ...");
		log.info("Создание атрибута 'instantECP' типа boolean в классе 'User'.");
		try{
			KrnClass userCls = getClassByNameComp("User");        
	        db.addClass(userCls, true);
	        if ((getAttributeByNameComp(userCls, "instantECP")) == null){
	        	createAttribute(-1, "c8a905a1-842f-423c-8dc0-6790c00dca07", userCls.id, CID_BOOL, "instantECP", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        }
	        log.info("Апгрейд БД до версии 65 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 65!");
		}
		isUpgrading = false;
	}
	
	public void upgradeTo66() throws SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 66 ...");
        log.info("Создание атрибута timeFinish в классе TimerProtocol...");
		Statement st = conn.createStatement();
		try {
			KrnClass cls_timer_protocol = getClassByNameComp("TimerProtocol");
			db.addClass(cls_timer_protocol, true);
			KrnAttribute attr_protocol_finish=getAttributeByNameComp(cls_timer_protocol, "timeFinish");
			if(attr_protocol_finish==null)
				attr_protocol_finish=createAttribute(-1, null, cls_timer_protocol.id, CID_TIME,"timeFinish", 0, false, false,
						false,false, 0, 0, 0, 0, false, false, "TIMEFINISH", 0);
			KrnAttribute attr_protocol_start=getAttributeByNameComp(cls_timer_protocol,"timeStart");
			KrnAttribute attr_protocol_timer=getAttributeByNameComp(cls_timer_protocol,"timer");
			KrnAttribute attr_protocol_status=getAttributeByNameComp(cls_timer_protocol,"status");
            String cls_p_tname = cls_timer_protocol.tname != null && cls_timer_protocol.tname.length() > 0 ? cls_timer_protocol.tname : "ct" + cls_timer_protocol.id;
            String attr_p_start_tname = attr_protocol_start.tname != null && attr_protocol_start.tname.length() > 0 ? attr_protocol_start.tname : "cm" + attr_protocol_start.id;
            String attr_p_finish_tname = attr_protocol_finish.tname != null && attr_protocol_finish.tname.length() > 0 ? attr_protocol_finish.tname : "cm" + attr_protocol_finish.id;
            String attr_p_timer_tname = attr_protocol_timer.tname != null && attr_protocol_timer.tname.length() > 0 ? attr_protocol_timer.tname : "cm" + attr_protocol_timer.id;
            String attr_p_status_tname = attr_protocol_status.tname != null && attr_protocol_status.tname.length() > 0 ? attr_protocol_status.tname : "cm" + attr_protocol_status.id;
	        log.info("Заполнение  атрибута timeFinish равным timeStart...");
            st.executeUpdate("UPDATE "+cls_p_tname+" SET "+attr_p_finish_tname+" = "+attr_p_start_tname
            		+" WHERE "+attr_p_finish_tname+" IS NULL AND "+attr_p_start_tname+" IS NOT NULL AND "+attr_p_status_tname+" =1");
			log.info("Апгрейд БД до версии 66 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 66!");
		} finally {
			DbUtils.closeQuietly(st);
		}
        isUpgrading = false;
	}
	
	public void upgradeTo67() throws SQLException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 67 ...");
		log.info("Создание атрибута 'base' типа 'Структура баз' в классе 'Flow'.");
		try{
			KrnClass cls_base = getClassByNameComp("Структура баз");
			KrnClass flowCls = getClassByNameComp("Flow");        
	        db.addClass(cls_base, true);
	        db.addClass(flowCls, true);
	        if ((getAttributeByNameComp(flowCls, "base")) == null){
	        	createAttribute(-1, null, flowCls.id, cls_base.id, "base", 0, false, false, false, false, 0, 0, 0, 0, false, "BASE", 0);
	        }
	        
	        log.info("Апгрейд БД до версии 67 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 67!");
		}
		isUpgrading = false;
	}
	
	public void upgradeTo68() throws SQLException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 68 ...");
		log.info("Создание атрибута 'isInBox' типа boolean в классе 'ProcessDef'.");
		try{
			KrnClass pd = getClassByNameComp("ProcessDef");        
			KrnClass blob = getClassByNameComp("blob");        
	        db.addClass(pd, true);
	        db.addClass(blob, true);
	        KrnAttribute conf_attr=getAttributeByNameComp(pd, "config");
	        KrnAttribute inbox_attr=getAttributeByNameComp(pd, "isInBox");
	        if (inbox_attr== null){
	        	inbox_attr=createAttribute(-1, null, pd.id, CID_BOOL, "isInBox", 0, false, false, false, false, 0, 0, 0, 0, false, "ISINBOX", 0);
	        }
	        List<KrnObject> objs=getObjects(pd.id, 0);
	        for(KrnObject obj:objs) {
	        	if(obj.classId!=pd.id) continue;
		        byte[] buf=(byte[])getValue(obj.id, conf_attr.id, 0, 0, 0);
		        if(buf==null) continue;
		        try {
			        SAXBuilder builder = new SAXBuilder();
			        Element root= builder.build(new ByteArrayInputStream(buf), "UTF-8").getRootElement();
			        Element inbox = root.getChild("inbox");
			        if(inbox!=null) {
			        	String inbox_text = inbox.getText();
			        	if("true".equals(inbox_text))
			        		setValueImpl(obj,inbox_attr,0,0,0,1,true);
			        }
		        }catch(Exception ex) {
					log.warn("Ошибка при апгрейде БД до версии 68(построение xml документа)!ProcessDef:"+obj.uid);
		        }
	        }
	        log.info("Апгрейд БД до версии 68 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 68!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		isUpgrading = false;
	}
	
	public void upgradeTo69() throws SQLException, DriverException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 69 ...");
		log.info("Создание класса 'Notification'.");
		log.info("Создание атрибута 'message' типа 'string' в классе 'Notification'.");
		log.info("Создание атрибута 'uid' типа 'string' в классе 'Notification'.");
		log.info("Создание атрибута 'cuid' типа 'string' в классе 'Notification'.");
		log.info("Создание атрибута 'row' типа 'long' в классе 'Notification'.");
		log.info("Создание атрибута 'datetime' типа 'time' в классе 'Notification'.");
		log.info("Создание атрибута 'notifications' типа 'Notification' в классе 'User'.");
		try {
			KrnClass notificationCls = getClassByNameComp("Notification");
			if (notificationCls == null) {
				notificationCls = createClass("Notification", getClassByNameComp("Системный класс").id, false, 0, -1, "0610ac24-dc42-4866-84ea-3111d94c97b4", false, null);
			} else {
				db.addClass(notificationCls, true);
			}		
	        if ((getAttributeByNameComp(notificationCls, "message")) == null) {
	            createAttribute(-1, "50f9015f-65cc-4128-afea-2702f2f10561", notificationCls.id, CID_STRING, "message", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
	        if ((getAttributeByNameComp(notificationCls, "uid")) == null) {
	            createAttribute(-1, "7340b888-7b32-4e79-bae6-02b9e8a47829", notificationCls.id, CID_STRING, "uid", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
	        if ((getAttributeByNameComp(notificationCls, "cuid")) == null) {
	            createAttribute(-1, "89e53209-c8d2-469c-af5a-4c6f44568534", notificationCls.id, CID_STRING, "cuid", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
	        if ((getAttributeByNameComp(notificationCls, "row")) == null) {
	            createAttribute(-1, "7caf9647-e508-4135-afad-3db02c97e0ce", notificationCls.id, CID_INTEGER, "row", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
	        if ((getAttributeByNameComp(notificationCls, "datetime")) == null) {
	            createAttribute(-1, "4e048dba-852f-4a2e-9e33-ad738c91f1b4", notificationCls.id, CID_TIME, "datetime", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
	        }
	        KrnClass userCls = getClassByNameComp("User");        
	        db.addClass(userCls, true);
	        if ((getAttributeByNameComp(userCls, "notifications")) == null){
	        	createAttribute(-1, "85e57179-d519-45d2-972a-b2e1badcee93", userCls.id, notificationCls.id, "notifications", COLLECTION_SET, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        }
	        log.info("Апгрейд БД до версии 69 успешно завершен!");
		} catch (SQLException | DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 69!");
			throw e;
		}
		isUpgrading = false;
	}
	
	public void upgradeTo70() throws SQLException, DriverException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 70 ...");
		log.info("Создание атрибута 'link' типа 'string' в классе 'ContactInfo'.");
		try {
			KrnClass contactInfoCls = getClassByNameComp("ContactInfo");
			if (contactInfoCls != null) {
				db.addClass(contactInfoCls, true);
				if ((getAttributeByNameComp(contactInfoCls, "link")) == null) {
		            createAttribute(-1, "c3a2442b-e2fb-4e97-8b3d-1729c1429899", contactInfoCls.id, CID_STRING, "link", 0, false, false, false, false, 0, 0, 0, 0, false, false, null, 0);
		        }
			}		
	        log.info("Апгрейд БД до версии 70 успешно завершен!");
		} catch (SQLException | DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 70!");
			throw e;
		}
		isUpgrading = false;
	}
	
	public void upgradeTo71() throws SQLException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 71 ...");
		log.info("Создание атрибута 'showSearchField', 'srch_txt', 'ifc_uid' типа boolean и string и Объекта в классе 'ConfigGlobal'.");
		log.info("Создание атрибута 'scope[]'типа объект в классе 'User'.");

		Statement st = null;
		try {
			KrnClass mainCls = getClassByNameComp("Объект");
			KrnClass confCls = getClassByNameComp("ConfigGlobal"); 
			KrnClass userCls = getClassByNameComp("User");
			db.addClass(mainCls, true);
	        db.addClass(confCls, true);
	        db.addClass(userCls, true);
	        if ((getAttributeByNameComp(confCls,"showSearchField")) == null){
	        	createAttribute(-1, "b451f0e6-e90c-4b44-a041-5768bbc7a4dc", confCls.id, CID_BOOL, "showSearchField", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        }
	        if ((getAttributeByNameComp(confCls,"srch_txt")) == null){
	        	createAttribute(-1, "a71e60a5-fd35-46d1-8abc-c8a4a1a02c1d", confCls.id, CID_STRING, "srch_txt", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        }
	        if ((getAttributeByNameComp(confCls,"ifc_uid")) == null){
	        	createAttribute(-1, "46e6723f-93e6-4855-b474-d1fe8e47bf2b", confCls.id, CID_STRING, "ifc_uid", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        }
	        
	        if ((getAttributeByNameComp(userCls,"scope")) == null) {
	        	createAttribute(-1, "0d51c191-8082-4740-bf3e-3ce59eb8dcec", userCls.id, mainCls.id, "scope", COLLECTION_SET, false, true, false, false, 0, 0, 0, 0, false, null, 0);
	        }
	        
	        KrnObject obj = createObject(confCls.id, 0, -1, null);
	        
			log.info("Создание таблицы 't_search_indexes'");

			st = conn.createStatement();
	        st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_search_indexes ("
	        		+ "c_id BIGINT NOT NULL AUTO_INCREMENT,"
					+ "c_search_str VARCHAR(2000) NOT NULL,"
					+ "c_obj_uid VARCHAR(255) NOT NULL,"
					+ "c_ext_field VARCHAR(255),"
					+ "PRIMARY KEY(c_id))"
					+ " ENGINE=" + ENGINE);
	        
			st.executeUpdate("CREATE UNIQUE INDEX t_search_obj_ext_idx"
					+ " ON "+getDBPrefix()+"t_search_indexes(c_obj_uid,c_ext_field)");

			st.executeUpdate("CREATE INDEX t_search_obj_idx"
					+ " ON "+getDBPrefix()+"t_search_indexes(c_obj_uid)");

			st.executeUpdate("CREATE INDEX t_search_ext_idx"
					+ " ON "+getDBPrefix()+"t_search_indexes(c_ext_field)");

	        log.info("Апгрейд БД до версии 71 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 71!");
		} finally {
			DbUtils.closeQuietly(st);
		}
		isUpgrading = false;
	}
	
	public void upgradeTo72() throws SQLException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 72 ...");
		log.info("Создание атрибута 'logotypePic', 'logoPicWidth', 'logoPicHeight' типа blob и long в классе 'ConfigGlobal'.");
		try{
			KrnClass mainCls = getClassByNameComp("Объект");
			KrnClass confCls = getClassByNameComp("ConfigGlobal"); 
	        db.addClass(mainCls, true);
	        db.addClass(confCls, true);
	        if ((getAttributeByNameComp(confCls,"logotypePic")) == null){
	        	createAttribute(-1, "b1ed7118-edd4-4a31-9e7e-ebc1616f347e", confCls.id, CID_BLOB, "logotypePic", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        }
	        if ((getAttributeByNameComp(confCls,"logoPicWidth")) == null){
	        	createAttribute(-1, "cdd8c2d5-299a-4887-901b-63bc15b9dbb6", confCls.id, CID_INTEGER, "logoPicWidth", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        }
	        if ((getAttributeByNameComp(confCls,"logoPicHeight")) == null){
	        	createAttribute(-1, "e89b3391-8e11-4a7f-a3ad-1f4da1f955f6", confCls.id, CID_INTEGER, "logoPicHeight", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        }
	        
	        log.info("Апгрейд БД до версии 72 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 72!");
		}
		isUpgrading = false;
	}
	
	public void upgradeTo73() throws SQLException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 73 ...");
		log.info("Пересоздавать атрибута 'srch_txt' типа String как мультиязычным в классе 'ConfigGlobal'.");
		try{
			KrnClass mainCls = getClassByNameComp("Объект");
			KrnClass confCls = getClassByNameComp("ConfigGlobal"); 
			if(confCls != null) {
				db.addClass(confCls, true);
				KrnAttribute attr = getAttributeByNameComp(confCls, "srch_txt");
				if (attr != null) {
					deleteAttributeComp(attr, false, version);
					db.removeAttribute(attr);
				}
	        	createAttribute(-1, "a71e60a5-fd35-46d1-8abc-c8a4a1a02c1d", confCls.id, CID_STRING, "srch_txt", 0, false, false, true, false, 0, 0, 0, 0, 
		        			false, false, null, 0);
			}
	        log.info("Апгрейд БД до версии 73 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 73!");
		}
		isUpgrading = false;
	}
	
	public void upgradeTo74() throws SQLException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 74 ...");
		log.info("Создание атрибута 'chat_srch_txt' типа string в классе 'ConfigGlobal'.");
		try{
			KrnClass mainCls = getClassByNameComp("Объект");
			KrnClass confCls = getClassByNameComp("ConfigGlobal"); 
			db.addClass(mainCls, true);
	        db.addClass(confCls, true);
	        if ((getAttributeByNameComp(confCls,"chat_srch_txt")) == null){
	        	createAttribute(-1, "f2c0810b-7115-45ec-8bde-b9efbda9f41c", confCls.id, CID_STRING, "chat_srch_txt", 0, false, false, true, false, 0, 0, 0, 0, false, null, 0);
	        }
	        
	        log.info("Апгрейд БД до версии 74 успешно завершен!");
		} catch (DriverException e) {
			e.printStackTrace();
			log.error("Ошибка при апгрейде БД до версии 74!");
		}
		isUpgrading = false;
	}
	
	public void upgradeTo75() throws SQLException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 75 ...");
		log.info("Создание атрибута 'useNotificationSound' типа boolean, 'notificationSound' типа blob в классе 'ConfigGlobal' и 'useNoteSound' типа boolean в классе 'User'.");
		try{
			KrnClass confCls = getClassByNameComp("ConfigGlobal"); 
			KrnClass userCls = getClassByNameComp("User");
	        db.addClass(confCls, true);
	        db.addClass(userCls, true);
	        if ((getAttributeByNameComp(confCls,"useNotificationSound")) == null){
	        	createAttribute(-1, "b2f0fff3-925c-496d-84cd-b0e1e76c49d5", confCls.id, CID_BOOL, "useNotificationSound", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        }
	        if ((getAttributeByNameComp(confCls,"notificationSound")) == null){
	        	createAttribute(-1, "7758961d-aa70-42d1-89b2-6d587c6d12cc", confCls.id, CID_BLOB, "notificationSound", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        }
	        
	        if ((getAttributeByNameComp(userCls,"useNoteSound")) == null) {
	        	createAttribute(-1, "365a96f9-cd66-4219-9f26-72e199e41fd8", userCls.id, CID_BOOL, "useNoteSound", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        }
	        
	        log.info("Апгрейд БД до версии 75 успешно завершен!");
		} catch (DriverException e) {
			e.printStackTrace();
			log.error("Ошибка при апгрейде БД до версии 75!");
		}
		isUpgrading = false;
	}
	
	public void upgradeTo76() throws SQLException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 76 ...");
		log.info("Создание нового объекта в классе 'HiperFolder'.");
		try{	    
			KrnClass mainCls = getClassByNameComp("Объект");
			KrnClass guiCompCls = getClassByNameComp("GuiComponent");
			KrnClass hpTreeCls = getClassByNameComp("HiperTree");
			KrnClass hpFolderCls = getClassByNameComp("HiperFolder");
			KrnClass mainTreeCls = getClassByNameComp("MainTree");
			KrnClass langCls = getClassByNameComp("Language");
			db.addClass(mainCls, true);
			db.addClass(guiCompCls, true);
			db.addClass(hpTreeCls, true);
			db.addClass(hpFolderCls, true);
			db.addClass(mainTreeCls, true);
			db.addClass(langCls, true);
			if ((getAttributeByNameComp(hpTreeCls,"uiIcon")) == null){
	        	createAttribute(-1, "8571cfce-6dc2-4790-849b-b05d542c3179", hpTreeCls.id, CID_BLOB, "uiIcon", 0, false, false, false, false, 0, 0, 0, 0, false, null, 0);
	        }
			KrnObject obj = null;
			try {
				obj = getObjectByUid("9.30198536", 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (obj == null) {				
				KrnAttribute codeAttr = getAttributeByNameComp(langCls, "code");
				KrnObject obj_ru = getObjectsByAttribute(langCls.id, codeAttr.id, 0, CO_EQUALS, "RU", 0).get(0);
//				KrnObject obj_kz = getObjectsByAttribute(langCls.id, codeAttr.id, 0, CO_EQUALS, "KZ", 0).get(0);				
				KrnObject mainTreeObj = getObjects(mainTreeCls.id, 0).get(0);				
				obj = createObject(hpFolderCls.id, 0, -1, "9.30198536");
				KrnAttribute a = getAttributeByNameComp(hpFolderCls, "title");
				setValue(obj, a.id, 0, obj_ru.id, 0, "Произвольные пункты", false);
				a = getAttributeByNameComp(hpFolderCls, "parent");
				setValue(obj, a.id, 0, 0, 0, mainTreeObj.id, false);
			}
			log.info("Апгрейд БД до версии 76 успешно завершен!");
		} catch (DriverException e) {
			e.printStackTrace();
			log.error("Ошибка при апгрейде БД до версии 76!");
		}
		isUpgrading = false;
	}
	
	public void upgradeTo77() throws SQLException {
		isUpgrading = true;
        log.info("Апгрейд БД до версии 77 ...");
        log.info("Добавление колонки в таблицу t_attrs.");
        
    	if (!isColumnExists("t_attrs", "c_is_encrypt")) {
    		QueryRunner qr = new QueryRunner(true);
    		qr.update(conn, "ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_is_encrypt " + getSqlTypeName(PC_BOOL, -1));
    	} else {
        	log.info("Колонка 'c_is_encrypt' уже существует в таблице 't_attrs'!");
        }
        
        log.info("Апгрейд БД до версии 77 успешно завершен.");
        isUpgrading = false;
	}
	
	protected boolean renameColumn(String schemeName, String tableName, String oldColName, String newColName, String sqlType) throws SQLException {
		StringBuilder sql = new StringBuilder("ALTER TABLE ");
		
		if (schemeName != null && schemeName.length() > 0)
			sql.append(schemeName).append(".");
			
		sql.append(tableName)
				.append(" CHANGE COLUMN ").append(oldColName)
				.append(" ").append(newColName)
				.append(" ").append(sqlType);
		
		log.info("SQL Query...\n " + sql);
		
		QueryRunner qr = new QueryRunner(true);
		qr.update(conn, sql.toString());
		
		return true;
	}
    public List runSql(String sql,int limit,boolean isUpdate) throws SQLException {
        StringBuffer sql_ = new StringBuffer(1024 * 2);
    	Matcher matcher = wRe1.matcher(sql);
    	int pos=0;
        while(matcher.find()) {
        	sql_.append(sql.substring(pos, matcher.start()));
            String param = sql.substring(matcher.start()+1,matcher.end()-1);
            if(this instanceof PgSqlDriver)
            	 param= ("1".equals(param)?"true":"false");
            sql_.append(param);
            pos = matcher.end();
        }
        if(pos>0) {
        	sql_.append(sql.substring(pos));
        	sql=sql_.toString();
        }
    	if(limit>0){
    		StringBuilder sb_sql=addLimit(new StringBuilder(sql), limit, 0);
    		sql=sb_sql.toString();
    	}
        Statement st = conn.createStatement();
        ResultSet rs = null;
        List res = new ArrayList();
        try {
            if(isUpdate){
                st.executeUpdate(sql);
            }else{
                rs = st.executeQuery(sql);
                while(rs.next()) {
                    ResultSetMetaData md = rs.getMetaData();
                    int count = md.getColumnCount();
                    if (count == 1) {
                        res.add(rs.getObject(1));
                    } else {
                        List t = new ArrayList();
                        for (int i = 0; i < count; i++) {
                        	Object ri=rs.getObject(i + 1);
                        	if(ri instanceof oracle.sql.TIMESTAMP){
                        		ri= ((oracle.sql.TIMESTAMP)ri).timestampValue();
                        	}
                            t.add(ri);
                        }
                        res.add(t);
                    }
                }
            }
        } catch (SQLException e) {
            log.error(e, e);
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(st);
        }
        return res;
    }

	@Override
	public List<String> showDbLocks() {
		List<String> res=new ArrayList<>();
		String sql="SHOW OPEN TABLES where In_use > 0";
		try(Statement st=conn.createStatement();
			ResultSet rs=st.executeQuery(sql)){
			while(rs.next()) {
				res.add("Database:"+rs.getString("Database")+";Table:"+rs.getString("Table")+";In_use:"+rs.getString("In_use")+"Name_locked:"+rs.getString("Name_locked"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			res.add(e.getMessage());
		}
		return res;
	}

	@Override
	public List<Long> getFiltersContainingAttr(KrnClass filterCls, KrnAttribute configAttr, KrnAttribute classNameAttr, KrnAttribute exprSqlAttr, KrnAttribute titleAttr, KrnClass cls, KrnAttribute attr) {
		List<Long> filters = new ArrayList<>();
		PreparedStatement pst = null;
		try {
			String text = cls.name + "." + attr.name;
			String sql = "SELECT c_obj_id FROM " + getClassTableName(filterCls.id) + " WHERE " + getColumnName(configAttr, 1) + " LIKE \"%" + text + "%\"";

//			String sql = "SELECT c_obj_id FROM " + getClassTableName(filterCls.id) + " WHERE " + getColumnName(configAttr, 1) + " LIKE \"%" + attr.name + "%\" OR " + getColumnName(classNameAttr, 1) + " LIKE \"%" + attr.name + "%\" OR " + getColumnName(exprSqlAttr, 1) + " LIKE \"%" + attr.name + "%\" OR " + getColumnName(titleAttr, 1) + " LIKE \"%" + attr.name + "%\"";
//			if (attr.tname != null) {
//				sql += " OR " + getColumnName(configAttr, 1) + " LIKE \"%" + attr.tname + "%\" OR " + getColumnName(classNameAttr, 1) + " LIKE \"%" + attr.tname + "%\" OR " + getColumnName(exprSqlAttr, 1) + " LIKE \"%" + attr.tname + "%\" OR " + getColumnName(titleAttr, 1) + " LIKE \"%" + attr.tname + "%\"";
//			}
			pst = conn.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				filters.add(rs.getLong(1));
			}
		} catch(SQLException e){
            log.error(e, e);
		} catch (DriverException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(pst);
		}
		return filters;
	}
	
	public String updateColumnsSysLang(int drop_index,final boolean isDrop) {
		PreparedStatement pst = null;
		final Map<String,String> sqlMap= new TreeMap<>();
		log.info("Начата проверка на соответствие системных языков и наличия колонок в таблицах!");
		try {
			KrnClass langCls = getClassByNameComp("Language");        
	        db.addClass(langCls, true);
			KrnAttribute sysAttr = getAttributeByNameComp(langCls, "system?");
			if (sysAttr == null)
				sysAttr = getAttributeByNameComp(langCls, "lang?");
			String sql_lang = "SELECT count(*) FROM "+getClassTableName(langCls.id)+" WHERE c_tr_id=0 AND "+getColumnName(sysAttr) + "=true";
			pst = conn.prepareStatement(sql_lang);
			ResultSet rs = pst.executeQuery();
			int countLang=0;
			if(rs.next()) {
				countLang=rs.getInt(1);
			}
			pst.close();
			String sql = "SELECT a.c_id ,a.c_class_id,a.c_type_id,a.c_col_type,a.c_tname,c.c_tname "
					+ "FROM t_attrs a LEFT JOIN t_classes c ON c.c_id=a.c_class_id  "
					+ "WHERE c_is_multilingual=1";
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			String asql="";
			while (rs.next()) {
				long id=rs.getLong(1);
				long class_id=rs.getLong(2);
				long type_id=rs.getLong(3);
				int col_type=rs.getInt(4);
				String atname=rs.getString(5);
				String ctname=rs.getString(6);
				String colName = (atname!=null && atname.trim().length() != 0?atname.trim():"cm"+id);
				String tblName;
				if(col_type==0)
					tblName = (ctname!=null && ctname.trim().length() != 0?ctname.trim():"ct"+class_id);
				else
					tblName = (atname!=null && atname.trim().length() != 0?atname.trim():"at"+class_id+"_"+id);
				for(int lang_index=1;lang_index<=countLang;lang_index++) {
					if(isDrop && drop_index==lang_index && isColumnExists(tblName, colName+"_"+lang_index)) {
						asql=sqlMap.get(tblName);
						if(asql==null || "".equals(asql))
							asql = " DROP COLUMN "+colName+"_"+lang_index;
						else
							asql += ", DROP COLUMN "+colName+"_"+lang_index;
						sqlMap.put(tblName, asql);
					}else if(!isDrop && !isColumnExists(tblName, colName+"_"+lang_index)) {
						asql=sqlMap.get(tblName);
						if(asql==null || "".equals(asql))
							asql = " ADD COLUMN "+colName+"_"+lang_index+" "+getSqlTypeName(type_id, 0);
						else
							asql += ", ADD COLUMN "+colName+"_"+lang_index+" "+getSqlTypeName(type_id, 0);
						sqlMap.put(tblName, asql);
					}
				}
			}
			pst.close();
            new Thread() {
                public void run() {
	               	try(Connection conn_ = getNewConnection()) {
	               		int currCountTable=0;
	               		int	countTable=sqlMap.keySet().size();
	        			for(String keyTblName:sqlMap.keySet()) {
        					String tsql = "ALTER TABLE "+keyTblName+sqlMap.get(keyTblName)+", ALGORITHM=INPLACE, LOCK=NONE";
	        				try(Statement st=conn_.createStatement()) {
	        					st.executeUpdate(tsql);
	        					currCountTable++;
	        					if(isDrop)
	        						log.info(""+currCountTable+"/"+countTable+" Удаление:"+tsql);
	        					else {
	        						log.info(""+currCountTable+"/"+countTable+" Добавление:"+tsql);
	        					}
	        				} catch(SQLException e){
	        					log.info("Ошибка:"+tsql);
	        			        log.error(e.getMessage());
	        				} finally {
	        					conn_.commit();
	        				}
	        			}
	        		} catch(SQLException e){
	        	        log.error(e.getMessage());
	        		} catch (DriverException e1) {
	        			// TODO Auto-generated catch block
	        			e1.printStackTrace();
	                } catch (Exception e) {
	                	log.info("Ошибка при добавлении мультиязычных колонок!");
	                	log.error(e, e);
	                }
                }
            }.start();
		} catch(SQLException e){
            log.error(e, e);
		} catch (DriverException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(pst);
		}
		log.info("Закончена проверка на соответствие системных языков и наличия колонок в таблицах!");
		String res = "Будут добавлены мультиязычные колонки для "+sqlMap.keySet().size()+" таблиц";
		return res;
		
	}
	public int getRealSizeColumn(KrnAttribute attr) {
		int size=attr.size;
		String t_name=getClassTableName(attr.classId);
		String c_name=getAttrTableName(attr);
	   	String selSql="SELECT CHARACTER_MAXIMUM_LENGTH FROM INFORMATION_SCHEMA.COLUMNS"
				+ " WHERE TABLE_NAME=? AND COLUMN_NAME=? AND TABLE_SCHEMA=?";
	   	try(PreparedStatement pst = conn.prepareStatement(selSql);){
	   		pst.setString(1, t_name);
	   		pst.setString(2, c_name);
	   		pst.setString(3, db.getSchemeName());
	   		ResultSet rs=pst.executeQuery();
	   		if(rs.next()) {
	   			size=rs.getInt(1);
	   		}
	   	}catch(SQLException e) {
        	log.info("Ошибка при определении размера колонки для атрибута:"+c_name+";в таблице:"+t_name+" !");
        	log.error(e, e);
	   	}
	   	return size;
	}

	@Override
	public void createTableOrders() {
		Statement st = null;
		
		try {
			st = conn.createStatement();
			st.executeUpdate(
					"CREATE TABLE " + getDBPrefix() + "t_orders ("
					+ "c_id BIGINT NOT NULL AUTO_INCREMENT,"
					+ "c_obj_id BIGINT NOT NULL,"
					+ "c_obj_uid CHAR(20) NOT NULL,"
					+ "c_obj_class_id BIGINT NOT NULL,"
					+ "c_attr_id BIGINT NOT NULL,"
					+ "c_lang_id BIGINT NOT NULL,"
					+ "c_old_value LONGBLOB,"
					+ "c_user_id BIGINT NOT NULL,"
					+ "c_ip VARCHAR(15) NOT NULL,"
					+ "c_mod_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ "c_mod_last_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ "c_fix_start_id BIGINT,"
					+ "c_fix_end_id BIGINT,"
					+ "c_fix_comment TEXT,"
					+ "PRIMARY KEY PK_VCS (c_id),"
					+ "UNIQUE INDEX IDX_VCS_OBJ_ATTR_LANG (c_obj_id,c_attr_id,c_lang_id,c_fix_end_id)"
					+ ")");
		} catch (SQLException e) {
			log.error(e, e);
		} finally {
			DbUtils.closeQuietly(st);
		}

	}
}