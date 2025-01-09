package kz.tamur.ods.oracle;

import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_CLASS;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_ATTRIBUTE;
import static com.cifs.or2.kernel.ModelChange.ACTION_CREATE;
import static com.cifs.or2.kernel.ModelChange.ACTION_MODIFY;
import static com.cifs.or2.kernel.ModelChange.ACTION_DELETE;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_METHOD;
import static kz.tamur.or3ee.common.SessionIds.CID_BOOL;
import static kz.tamur.or3ee.common.SessionIds.CID_DATE;
import static kz.tamur.or3ee.common.SessionIds.CID_INTEGER;
import static kz.tamur.or3ee.common.SessionIds.CID_STRING;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import kz.tamur.DriverException;
import kz.tamur.common.ErrorCodes;
import kz.tamur.comps.Constants;
import kz.tamur.comps.TriggerInfo;
import kz.tamur.ods.AttrRequest;
import kz.tamur.ods.AttrRequestCache;
import kz.tamur.ods.Driver2;
import kz.tamur.ods.Lock;
import kz.tamur.ods.Value;
import kz.tamur.ods.mysql.MySqlDriver3;
import kz.tamur.ods.sql92.AttrResultSetHandler;
import kz.tamur.ods.sql92.AttributeRsh;
import kz.tamur.ods.sql92.ClassResultSetHandler;
import kz.tamur.or3.util.FGACRule;
import kz.tamur.or3.util.FGARule;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;
import kz.tamur.util.KrnUtil;
import kz.tamur.util.Pair;
import kz.tamur.util.XmlUtil;
import oracle.jdbc.OracleConnection;
import oracle.sql.BLOB;
import oracle.sql.CLOB;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;
import oracle.jdbc.OracleResultSet;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnIndex;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnVcsChange;
import com.cifs.or2.server.ServerUserSession;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.db.Database;
import com.cifs.or2.server.sgds.HexStringOutputStream;
import com.cifs.or2.util.MMap;

import static kz.tamur.or3.util.Tname.TnameVersionBD;
import static kz.tamur.or3.util.Tname.AttrAccessModVersionBD;
import static kz.tamur.or3.util.Tname.EncryptColumnsVersionBD;
import static kz.tamur.or3.util.Tname.getAttrFKName;
import static kz.tamur.or3.util.Tname.isVersion;
import static kz.tamur.or3.util.Tname.version;

/**
 * Created by IntelliJ IDEA. User: berik Date: 16.02.2006 Time: 16:58:59 To
 * change this template use File | Settings | File Templates.
 */
@SuppressWarnings({"unchecked", "deprecation", "unused", "rawtypes"})
public class OracleDriver3 extends MySqlDriver3 {
	
	private OracleConnection oraConn;
	private boolean isFirstTryToConnect = true;
	private static final int MAX_OBJECT_NAME_LENGTH = 30;
	private static final String DOMAIN="DOMAIN";
	private static final String IDX_CONTEXT="INDEXTYPE IS CTXSYS.CONTEXT PARAMETERS ('SYNC (ON COMMIT) memory 50m')";

	public OracleDriver3(Database db, String dsName, UserSession us) throws DriverException {
		super(db, dsName, us);
		try {
			oraConn = conn.unwrap(OracleConnection.class);
		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	@Override
	protected String getColumnDef(KrnAttribute attr, int langIndex) throws DriverException {
		// @todo Уникальность пока не реализуется так как объект создается
		// пустым
		StringBuffer res = new StringBuffer(getColumnName(attr, langIndex));
		res.append(" ");
		res.append(getSqlTypeName(attr));
		return res.toString();
	}

	@Override
	protected String getSqlTypeName(KrnAttribute attr) {
		return getSqlTypeName(attr.typeClassId,attr.size,attr.isFullText());
	}

	@Override
	protected String getSqlTypeName(long typeId,int sz) {
		return getSqlTypeName(typeId,sz,false);
	}
	
	protected String getSqlTypeName(long typeId,int sz,boolean isFullText) {
		if (typeId == PC_STRING) {
			return (isFullText?"":"N")+"VARCHAR2(" + (sz > 0 ? sz : 2000) + ")";
		} else if (typeId == PC_INTEGER) {
			return "INTEGER";
		} else if (typeId == PC_DATE) {
			return "DATE";
		} else if (typeId == PC_TIME) {
			return "TIMESTAMP";
		} else if (typeId == PC_BOOL) {
			return "NUMBER(1) DEFAULT 0";
		} else if (typeId == PC_FLOAT) {
			return "FLOAT";
		} else if (typeId == PC_MEMO) {
			return "CLOB";
		} else if (typeId == PC_BLOB) {
			return "BLOB";
		}
		return "INTEGER";
	}

	protected void install() throws DriverException {
		try {
			Statement st = conn.createStatement();
			// ResultSet set = null;
			String qStr="SELECT TABLE_NAME FROM USER_TABLES WHERE ROWNUM=1";
			if(isSchemeName){ 
				qStr="SELECT TABLE_NAME FROM ALL_TABLES WHERE TABLE_NAME = 'T_IDS' " +
						"AND OWNER = '"+db.getSchemeName().toUpperCase(Constants.OK)+"'";
			}
			ResultSet set = st.executeQuery(qStr);//TODO EDIT
			boolean installed = set.next();
			set.close();

			if (!installed) {
		        isUpgrading = true;

				st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_classes ("
						+ "c_id INTEGER NOT NULL ,"
                        + "c_cuid VARCHAR2(36) NOT NULL,"
						+ "c_name VARCHAR2(255) NOT NULL,"
						+ "c_parent_id INTEGER NOT NULL,"
						+ "c_is_repl INTEGER NOT NULL,"
						+ "c_comment CLOB,"
                        + "c_mod INTEGER NOT NULL,"
                        + "UNIQUE (c_name),"
                        + "UNIQUE (c_cuid),"
						+ "PRIMARY KEY(c_id))");
				st.executeUpdate("CREATE INDEX cls_parent_idx"
						+ " ON "+getDBPrefix()+"t_classes(c_parent_id)");

				st
						.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_clinks ("
								+ "c_parent_id INTEGER NOT NULL,"
								+ "c_child_id INTEGER NOT NULL,"
								+ "FOREIGN KEY(c_parent_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE,"
								+ "FOREIGN KEY(c_child_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE,"
								+ "PRIMARY KEY(c_parent_id,c_child_id))");
				st
						.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_attrs ("
								+ "c_id INTEGER NOT NULL,"
								+ "c_auid VARCHAR2(36) NOT NULL,"
								+ "c_class_id INTEGER NOT NULL,"
								+ "c_name VARCHAR2(255) NOT NULL,"
								+ "c_type_id INTEGER NOT NULL,"
								+ "c_col_type INTEGER NOT NULL,"
								+ "c_is_unique INTEGER NOT NULL,"
								+ "c_is_indexed INTEGER NOT NULL,"
								+ "c_is_multilingual INTEGER NOT NULL,"
								+ "c_is_repl INTEGER NOT NULL,"
								+ "c_size INTEGER NOT NULL,"
								+ "c_flags INTEGER NOT NULL,"
								+ "c_rattr_id INTEGER,"
								+ "c_sattr_id INTEGER,"
								+ "c_sdesc INTEGER,"
								+ "c_is_encrypt NUMBER(1) DEFAULT 0,"
								+ "c_comment CLOB,"
								+ "PRIMARY KEY(c_id),"
								+ "UNIQUE(c_auid),"
								+ "FOREIGN KEY(c_class_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE,"
								+ "FOREIGN KEY(c_type_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE,"
								+ "FOREIGN KEY(c_rattr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id),"
								+ "FOREIGN KEY(c_sattr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id))");
				st.executeUpdate("CREATE UNIQUE INDEX attr_name_idx"
						+ " ON "+getDBPrefix()+"t_attrs(c_class_id,c_name)");

				st
						.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_rattrs ("
								+ "c_attr_id INTEGER NOT NULL,"
								+ "c_rattr_id INTEGER NOT NULL,"
								+ "PRIMARY KEY(c_attr_id,c_rattr_id),"
								+ "FOREIGN KEY(c_attr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id) ON DELETE CASCADE,"
								+ "FOREIGN KEY(c_rattr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id) ON DELETE CASCADE)");
				st
						.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_methods ("
								+ "c_muid VARCHAR2(36) NOT NULL,"
								+ "c_class_id INTEGER NOT NULL,"
								+ "c_name VARCHAR2(255) NOT NULL,"
								+ "c_is_cmethod INTEGER NOT NULL,"
								+ "c_expr BLOB,"
								+ "c_comment CLOB,"
								+ "PRIMARY KEY(c_muid),"
								+ "FOREIGN KEY(c_class_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE)");
				st.executeUpdate("CREATE UNIQUE INDEX method_name_idx"
						+ " ON "+getDBPrefix()+"t_methods(c_class_id,c_name)");

				dbPreInit();

				st
						.executeUpdate("CREATE TABLE "+getDBPrefix()+"ct99 ("
								+ "c_obj_id INTEGER NOT NULL,"
								+ "c_uid VARCHAR2(20),"
								+ "c_class_id INTEGER NOT NULL,"
								+ "PRIMARY KEY(c_obj_id),"
								+ "FOREIGN KEY(c_class_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE)");
				st.executeUpdate("CREATE UNIQUE INDEX uid99idx"
						+ " ON "+getDBPrefix()+"ct99(c_uid)");
				st.executeUpdate("CREATE INDEX cls99idx"
						+ " ON "+getDBPrefix()+"ct99(c_class_id)");

				st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_changescls ("
						+ "c_id INTEGER NOT NULL,"
						+ " c_type INTEGER NOT NULL,"
						+ "c_action INTEGER NOT NULL,"
						+ "c_entity_id VARCHAR2(36) NOT NULL,"
						+ "PRIMARY KEY(c_id))");
				st.executeUpdate("CREATE SEQUENCE "+getDBPrefix()+"seq_changescls ORDER");
				st
						.executeUpdate("CREATE OR REPLACE TRIGGER "+getDBPrefix()+"ti_changescls "
								+ "BEFORE INSERT ON "+getDBPrefix()+"t_changescls "
								+ "FOR EACH ROW "
								+ "BEGIN"
								+ " SELECT "+getDBPrefix()+"seq_changescls.nextval INTO :new.c_id FROM dual; "
								+ "END;");

				st
						.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_changes ("
								+ "c_id INTEGER NOT NULL,"
								+ "c_class_id INTEGER NOT NULL,"
								+ "c_object_id INTEGER NOT NULL,"
								+ "c_attr_id INTEGER NOT NULL,"
								+ "c_lang_id INTEGER NOT NULL,"
								+ "c_tr_id INTEGER NOT NULL,"
								+ "c_is_repl NUMBER(1) NOT NULL,"
								+ "PRIMARY KEY(c_id),"
								+ "FOREIGN KEY(c_attr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id) ON DELETE CASCADE)");
				st.executeUpdate("CREATE INDEX ch_tr_repl_idx"
						+ " ON "+getDBPrefix()+"t_changes(c_tr_id,c_is_repl)");
				st.executeUpdate("CREATE INDEX ch_tr_attr_idx"
						+ " ON "+getDBPrefix()+"t_changes(c_tr_id,c_attr_id)");
				st.executeUpdate("CREATE INDEX ch_tr_obj_idx"
						+ " ON "+getDBPrefix()+"t_changes(c_tr_id,c_object_id)");
				st.executeUpdate("CREATE INDEX ch_class_id_idx"
						+ " ON "+getDBPrefix()+"t_changes(c_class_id,c_id)");

				st.executeUpdate("CREATE SEQUENCE "+getDBPrefix()+"seq_changes ORDER");

				st
						.executeUpdate("CREATE OR REPLACE TRIGGER "+getDBPrefix()+"ti_changes "
								+ "BEFORE INSERT ON "+getDBPrefix()+"t_changes "
								+ "FOR EACH ROW "
								+ "BEGIN"
								+ " SELECT "+getDBPrefix()+"seq_changes.nextval INTO :new.c_id FROM dual; "
								+ "END;");

				st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_ids ("
						+ "c_name VARCHAR2(255) NOT NULL,"
						+ "c_last_id INTEGER NOT NULL,"
						+ "PRIMARY KEY(c_name))");


				// Таблица блокировок объектов
				createLocksTable(conn);
				
				//Таблицы для хранение многоатрибутных индексов
				st.executeUpdate("" +
					"CREATE TABLE "+getDBPrefix()+"t_indexes(" +
					" c_id NUMBER NOT NULL," +
					" c_uid VARCHAR2(36) NOT NULL," +
					" c_class_id INTEGER NOT NULL," +
					" c_is_multilingual NUMBER(1) NOT NULL," +
					" PRIMARY KEY(c_id)," +
					" FOREIGN KEY(c_class_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE" +
					")" +
					"");
				
				st.executeUpdate("CREATE SEQUENCE " + getDBPrefix() + "seq_indexes START WITH 1");	
								
				st.executeUpdate("" +
					"CREATE TABLE "+getDBPrefix()+"t_indexkeys(" +
					" c_index_id NUMBER NOT NULL," +
					" c_attr_id INTEGER NOT NULL," +
					" c_keyno NUMBER NOT NULL," +
					" c_is_del NUMBER(1) NOT NULL," +
					" FOREIGN KEY(c_index_id) REFERENCES "+getDBPrefix()+"t_indexes(c_id) ON DELETE CASCADE," +
					" FOREIGN KEY(c_attr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id) ON DELETE CASCADE" +
					")" +
					"");

				
				PreparedStatement pst = conn
				.prepareStatement("INSERT INTO "+getDBPrefix()+"t_ids (c_name,c_last_id) VALUES (?,?)");
				
				// Создание базы с нуля или импорт данных из существующей базы
				String dbImportDir = System.getProperty("dbImportDir");
				if (dbImportDir == null) {
					st.executeUpdate("CREATE SEQUENCE "+getDBPrefix()+"seq_attrs START WITH 100");
					st.executeUpdate("CREATE SEQUENCE "+getDBPrefix()+"seq_ct99");
					long db_id = dbInit();
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
					pst.setLong(2, 12);
					pst.executeUpdate();

					pst.setString(1, "installed");
					pst.setLong(2, 1);
					pst.executeUpdate();
					pst.close();
				} else {
					sysLangCount = Integer.parseInt(System.getProperty("sysLangCount"));
					upgradeStructure();
/*                    st.executeUpdate("INSERT INTO t_attrs (c_id,c_class_id,c_name,c_type_id," +
                            "c_col_type,c_is_unique,c_is_indexed,c_is_multilingual,c_is_repl," +
                            "c_size,c_flags) VALUES (1,99,'creating',2,0,0,0,0,1,0,0)");
                    st.executeUpdate("INSERT INTO t_attrs (c_id,c_class_id,c_name,c_type_id," +
                            "c_col_type,c_is_unique,c_is_indexed,c_is_multilingual,c_is_repl," +
                            "c_size,c_flags) VALUES (2,99,'deleting',2,0,0,0,0,1,0,0)");
*/					dbImport(dbImportDir, System.getProperty("separator"));
					long max_id = 0;
					set = st.executeQuery("SELECT max(c_id) FROM "+getDBPrefix()+"t_attrs");
					if (set.next()) {
						max_id = set.getLong(1);
					} else
						max_id = 0;
					set.close();
					st
							.executeUpdate("CREATE SEQUENCE "+getDBPrefix()+"seq_attrs"
									+ (max_id > 1 ? " START WITH "
											+ (max_id + 1) : ""));
					set = st.executeQuery("SELECT max(c_obj_id) FROM "+getDBPrefix()+"ct99");
					if (set.next()) {
						max_id = set.getLong(1);
					} else
						max_id = 0;
					set.close();
					st
							.executeUpdate("CREATE SEQUENCE "+getDBPrefix()+"seq_ct99"
									+ (max_id > 1 ? " START WITH "
											+ (max_id + 1) : ""));

				}
				//
				long max_id = 0;
				set = st.executeQuery("SELECT max(c_id) FROM "+getDBPrefix()+"t_classes");
				if (set.next()) {
					max_id = set.getLong(1);
				}
				set.close();
				st.executeUpdate("CREATE SEQUENCE "+getDBPrefix()+"seq_classes"
						+ (max_id > 1 ? " START WITH " + (max_id + 1) : ""));

				set = st
						.executeQuery("SELECT c_last_id FROM "+getDBPrefix()+"t_ids WHERE c_name = 'transaction_id'");
				if (set.next()) {
					max_id = set.getLong(1);
				} else
					max_id = 0;
				st.executeUpdate("CREATE SEQUENCE "+getDBPrefix()+"seq_transaction"
						+ (max_id > 1 ? " START WITH " + (max_id + 1) : ""));
				set.close();
			}else{
                String load_=System.getProperty("load");
                if("1".equals(load_)){
                    QueryRunner qr = new QueryRunner(true);
                    List<KrnClass> classes = qr.query(conn,
                            "SELECT * FROM "+getDBPrefix()+"t_classes WHERE c_id>10",
                            new ClassResultSetHandler());
                    for (KrnClass cls : classes) {
                        log.info("Class " + cls.id +" nolog");
                        String ctName = getClassTableName(cls, true);
                        // Удаляем PK
                        st.executeUpdate("ALTER TABLE " + ctName + " logging");
                        st.executeUpdate("ALTER TABLE " + ctName + " noparallel");
                    }

                    List<KrnAttribute> attrs = (List<KrnAttribute>) qr.query(conn,
                            "SELECT * FROM "+getDBPrefix()+"t_attrs", new AttrResultSetHandler());

                    for (KrnAttribute attr : attrs) {
                        log.info("Attribute " + attr.id + " nolog");
                        String atName = getAttrTableName(attr);
                        // Если атрибут объектного типа
                        if ((attr.isMultilingual
                                || attr.collectionType != COLLECTION_NONE) && attr.rAttrId==0) {
                            // Создаем FK на (с_obj_id, c_tr_id)
                            st.executeUpdate("ALTER TABLE " + atName + " logging");
                            st.executeUpdate("ALTER TABLE " + atName + " noparallel");
                        }
                    }
                }else if("2".equals(load_)){
                    QueryRunner qr = new QueryRunner(true);
                    List<KrnClass> classes = (List<KrnClass>) qr.query(conn,
                            "SELECT * FROM "+getDBPrefix()+"t_classes WHERE c_id>10",
                            new ClassResultSetHandler());
                    for (KrnClass cls : classes) {
                        log.info("Class " + cls.id+" log");
                        String ctName = getClassTableName(cls, true);
                        // Удаляем PK
                        st.executeUpdate("ALTER TABLE " + ctName + " logging");
                        st.executeUpdate("ALTER TABLE " + ctName + "noparallel");
                    }

                    List<KrnAttribute> attrs = (List<KrnAttribute>) qr.query(conn,
                            "SELECT * FROM "+getDBPrefix()+"t_attrs", new AttrResultSetHandler());

                    for (KrnAttribute attr : attrs) {
                        log.info("Attribute " + attr.id +" log");
                        String atName = getAttrTableName(attr);
                        // Если атрибут объектного типа
                        if ((attr.isMultilingual
                                || attr.collectionType != COLLECTION_NONE) && attr.rAttrId==0) {
                            // Создаем FK на (с_obj_id, c_tr_id)
                            st.executeUpdate("ALTER TABLE " + atName + " logging");
                            st.executeUpdate("ALTER TABLE " + atName + "noparallel");
                        }
                    }

                }
            }
	        isUpgrading = false;
			version = 11;
			st.close();
			commit();
		} catch (SQLException e) {
			log.error(e, e);
			throw new DriverException(e);
		}
	}
	
	@Override
	public KrnClass createClass(String name, long parentId, boolean isRepl, int mod, long id, String uid, boolean log, String tname) throws DriverException {
		try {
			String sql = "";
			if (uid == null) {
				uid = UUID.randomUUID().toString();
			} else {
			    if(db.getClassByUid(uid) != null) {
			        this.log.warn("Класс \""+name+"\" не создан, т.к. уже есть в БД. UUID:"+uid);
			        return db.getClassByUid(uid);
			    }
			}
			if (id > 0) {
				// Создаем запись в таблице классов
				if (isVersion(kz.tamur.or3.util.Tname.TnameVersionBD)) {
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
				if (isVersion(kz.tamur.or3.util.Tname.TnameVersionBD)) {
					pst.setString(7, tname);
				}
				pst.executeUpdate();
				pst.close();
			} else {
				// Создаем запись в таблице классов
				id = getNextId("seq_classes");
				if (isVersion(kz.tamur.or3.util.Tname.TnameVersionBD)) {
					sql = "INSERT INTO "+getDBPrefix()+"T_CLASSES (C_ID, C_NAME, C_PARENT_ID, C_IS_REPL, C_MOD, C_CUID, C_TNAME) VALUES (?,?,?,?,?,?,?)";
				} else {
					sql = "INSERT INTO "+getDBPrefix()+"T_CLASSES (C_ID, C_NAME, C_PARENT_ID, C_IS_REPL, C_MOD, C_CUID) VALUES (?,?,?,?,?,?)";
				}
				PreparedStatement pst = conn.prepareStatement(sql);
				pst.setLong(1, id);
				pst.setString(2, name);
				pst.setLong(3, parentId);
				pst.setBoolean(4, isRepl);
				pst.setInt(5, mod);
				pst.setString(6, uid);
				if (isVersion(kz.tamur.or3.util.Tname.TnameVersionBD)) {
					pst.setString(7, tname);
				}
				pst.executeUpdate();
				pst.close();
			}

			// Создаем записи в таблице рекурсивных связей с родителей с детьми
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

			// Создаем таблицу для объектов класса
			if (id >= 100 && (mod & 1) == 0) {
				String tablename = getClassTableName(cls, true).toUpperCase(Constants.OK);
				Statement st = conn.createStatement();
				
				// Устанавливаем 30 секундное ожидание в случае блокировки t_classes
				try {
					st.executeUpdate("ALTER SESSION SET DDL_LOCK_TIMEOUT = 30");
				} catch (SQLException sqle) {
					this.log.warn("No rights to alter session DDL_LOCK_TIMEOUT!");
				}
				
				st.executeUpdate("CREATE TABLE "
								+ tablename
								+ " (C_OBJ_ID INTEGER NOT NULL,"
								+ "C_UID VARCHAR2(20),"
								+ "C_CLASS_ID INTEGER NOT NULL,"
								+ "C_TR_ID INTEGER NOT NULL,"
								+ "C_IS_DEL INTEGER DEFAULT 0,"
								+ "PRIMARY KEY(C_OBJ_ID,C_TR_ID),"
								+ "FOREIGN KEY(C_CLASS_ID) REFERENCES "+getDBPrefix()+"t_classes(C_ID) ON DELETE CASCADE)");
			
				st.executeUpdate("CREATE INDEX CT" + cls.id + "_TR_IDX ON "	+ tablename + "(C_TR_ID)");
				st.close();
			}
			// Создаем запись в журнале изменения модели
			if (log) {
				logVcsModelChanges(ENTITY_TYPE_CLASS, ACTION_CREATE, cls, cls, null, conn);
				logModelChanges(ENTITY_TYPE_CLASS, ACTION_CREATE, cls.uid, conn);
			}

			// ... и ложим его в кэши
			db.addClass(cls, false);
			return cls;
		} catch (SQLException e) {
			this.log.error(e, e);
			String msg = null;
			if (e.getMessage().indexOf("ORA-00001") != -1){
				int dot = e.getMessage().lastIndexOf(".") + 1;
				int end = e.getMessage().lastIndexOf(")");
				msg = e.getMessage().substring(dot, end);
			}
			if (msg != null && msg.length() != 0) {
				//select * from dba_ind_columns where index_name='?';
				PreparedStatement istnane = null;
				try {
					String sql = "select column_name from dba_ind_columns where index_name='" + msg + "'";
					istnane = conn.prepareStatement(sql);
					ResultSet rst = istnane.executeQuery();
					if(rst.next()) {
						String gtname = getSanitizedString(rst, 1);
						if (gtname.equalsIgnoreCase("C_NAME")){
							msg = "C_NAME";
						}
						if (gtname.equalsIgnoreCase("C_TNAME")){
							msg = "C_TNAME";
						}
					}
					rst.close();
				} catch (SQLException e1) {
					this.log.error(e1, e1);
					this.log.error(e1.getMessage());
				} finally {
					DbUtils.closeQuietly(istnane);
				}
				throw new DriverException(msg);
			} else {
				throw convertException(e);
			}
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
			boolean log,
			String tname,
			int accessModifier,
			boolean isEncrypt
	) throws DriverException {
		
		if (tname != null && tname.trim().length() == 0){
			tname = null;
		}

		// устанавливаем индекс на объектном атрибуте.
		if (typeId >= 99) {
			isIndexed = true;
		}

        try {
            // Создаем запись в таблице атрибутов
            if (uid == null) {
                uid = UUID.randomUUID().toString();
            } else {
                if (db.getAttributeByUid(uid) != null) {
                    this.log.warn("Атрибут \""+name+"\" не создан, т.к. уже есть в БД. UUID:"+uid);
                    return db.getAttributeByUid(uid);
                }
            }
			if (id == -1) {
				id = getNextId("seq_attrs");
			}
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
			String sql = "INSERT INTO "+getDBPrefix()+"t_attrs (c_class_id,c_name,c_type_id,"
					+ "c_col_type,c_is_unique,c_is_indexed,"
					+ "c_is_multilingual,c_is_repl,c_size,c_flags,c_rattr_id,"
					+ "c_sattr_id,c_sdesc,c_id,c_auid"+ATname+AAccessMod+AIsEncrypt+")"
					+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"+qATname+qAAccessMod+qAIsEncrypt+")";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setLong(1, classId);
			pst.setString(2, name);
			pst.setLong(3, typeId);
			pst.setInt(4, collectionType);
			pst.setBoolean(5, isUnique);
			pst.setBoolean(6, isIndexed);
			pst.setBoolean(7, isMultilingual);
			pst.setBoolean(8, isRepl);
			pst.setInt(9, size);
			pst.setLong(10, flags);
			if (rAttrId != 0) {
				pst.setLong(11, rAttrId);
			} else {
				pst.setNull(11, Types.BIGINT);
			}
			if (sAttrId != 0) {
				pst.setLong(12, sAttrId);
			} else {
				pst.setNull(12, Types.BIGINT);
			}
			pst.setBoolean(13, sDesc);
			pst.setLong(14, id);
			pst.setString(15, uid);
			if (isVersion(kz.tamur.or3.util.Tname.TnameVersionBD)){
				pst.setString(16, tname);
			}
			if (isVersion(AttrAccessModVersionBD)){
				pst.setInt(17, accessModifier);
			}
			if (isVersion(EncryptColumnsVersionBD)){
	            pst.setBoolean(18, isEncrypt);
			}
			try {
				this.log.debug("Создание атрибута. " + sql + " Paramters. " + pst.getParameterMetaData());
				pst.executeUpdate();
			} catch (SQLException e) {
                if (db.getAttributeByUid(uid) != null) {
                    this.log.warn("Атрибут \""+name+"\" не создан, т.к. уже есть в БД. UUID:"+uid);
                    return db.getAttributeByUid(uid);
                }

			} finally {
				pst.close();
			}
			
			// Создаем объект KrnAttribute
			KrnAttribute attr = KrnUtil.createAttribute(uid, id, name, classId, typeId,
					collectionType, isUnique, isMultilingual, isIndexed, size,
					flags, isRepl, rAttrId, sAttrId, sDesc, tname, null, null, null, null, 0, 0, 0, 0, accessModifier, isEncrypt);

			// Обновляем схему БД
			createAttributeInDatabase(attr);

			if (log) {
				// Создаем запись в журнале изменения модели
				logVcsModelChanges(ENTITY_TYPE_ATTRIBUTE, ACTION_CREATE, attr, attr, null, conn);
				logModelChanges(ENTITY_TYPE_ATTRIBUTE, ACTION_CREATE, attr.uid, conn);
			}

			// Добавляем новый атрибут в кэш
			db.addAttribute(attr, false);

			return attr;

		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	@Override
	protected void createAttributeInDatabase(KrnAttribute attr)
	throws SQLException, DriverException{
		if (attr.id < 3)
			return;
		
		boolean isArray = attr.collectionType == COLLECTION_ARRAY;
		boolean isSet = attr.collectionType == COLLECTION_SET;
		if (attr.rAttrId == 0) {
			if (!isSet && !isArray) {
				// Добавляем колонку в таблицу для объектов класса
				Statement st = conn.createStatement();
				StringBuffer sb = new StringBuffer();
				sb.append("ALTER TABLE ");
				sb.append(getClassTableName(attr.classId));
				sb.append(" ADD (");
				if (attr.isMultilingual) {
					// Создаем колонки для каждого системного языка
					int langCount = sysLangCount > 0 ? sysLangCount : getSystemLangs().size();
					for (int i = 0; i < langCount; i++) {
						if (i > 0){
							sb.append(",");
						}
						sb.append(getColumnName(attr, i + 1)+" "+getSqlTypeName(attr));
					}
				} else {
					sb.append(getColumnName(attr)+" "+getSqlTypeName(attr));
				}
				sb.append(")");
				log.debug("Создание атрибута. SQL:" + sb.toString());
				st.executeUpdate(sb.toString());
				st.close();
			} else {
				// Создаем таблицу для атрибута
				String atName = getAttrTableName(attr);
				StringBuilder sb = new StringBuilder();
				sb.append("CREATE TABLE ");
				sb.append(atName);
				sb.append(" (c_obj_id INTEGER NOT NULL,c_tr_id INTEGER NOT NULL,");
				if (isArray) {
					sb.append("c_index INTEGER NOT NULL,");
					sb.append("c_id INTEGER NOT NULL,");
				}
				sb.append("c_del INTEGER DEFAULT 0,");
				if (attr.isMultilingual) {
					// Создаем колонки для каждого системного языка
					int langCount = sysLangCount > 0 ? sysLangCount : getSystemLangs().size();
					for (int i = 0; i < langCount; i++) {
						if (i > 0){
							sb.append(",");
						}
						sb.append(getColumnName(attr, i + 1));
						sb.append(" ").append(getSqlTypeName(attr));
					}
				} else {
					sb.append(getColumnName(attr));
					sb.append(" ").append(getSqlTypeName(attr));
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
				sb.append(" (c_obj_id,c_tr_id) ON DELETE CASCADE ");
				sb.append("DEFERRABLE INITIALLY DEFERRED");
				sb.append(")");
				Statement st = conn.createStatement();
				log.debug("Создание атрибута. SQL:" + sb.toString());
				st.executeUpdate(sb.toString());
				st.close();
			}

			// Создаем индекс.
			if (attr.isIndexed) {
				updateIndex(attr, true);
			}
		} else if (isArray) {
			// Добавляем колонку в таблицу типа
			//TODO Рассмотреть необходимость в индексе
			String ctName = getClassTableName(attr.typeClassId);
			String cmiName = getRevIndexColumnName(attr.id);
			Statement st = conn.createStatement();
			log.debug("Создание атрибута. SQL: ALTER TABLE " + ctName
					+ " ADD " + cmiName + " INTEGER NOT NULL");
			st.executeUpdate(
				"ALTER TABLE " + ctName	+ " ADD " + cmiName + " INTEGER NOT NULL");
			st.close();
		}
	}

	@Override
	protected void deleteAttribute(long id, boolean log, long dbVer) throws DriverException {

		KrnAttribute attr = db.getAttributeById(id);

		try {
			// Удаляем запись из таблицы t_attrs
			QueryRunner qr = new QueryRunner(true);
			qr.update(conn, "DELETE FROM "+getDBPrefix()+"t_attrs WHERE c_id=?", id);

			if (attr.rAttrId == 0) {
				if (attr.collectionType == COLLECTION_ARRAY
						|| attr.collectionType == COLLECTION_SET) {
					// Удаляем дополнительную таблицу
					String tname = getAttrTableName(attr);
					try {
						qr.update(conn, "DROP TABLE " + tname);
					} catch (SQLException e) {
						this.log.warn("Table " + tname + " not found!!!");
					}
				} else {
					// Удаляем колонку в таблице класса
					String tname = getClassTableName(attr.classId);
					if (attr.typeClassId >= 99) {
						String fkname = getAttrFKName(attr.id);
						try {
							qr.update(conn, getDropForeignKeySql(tname, fkname));
						} catch (SQLException e) {
							this.log.warn("Constraint " + fkname + " not found!!!");
						}
					}
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

			// Удаляем атрибут из кэша
			db.removeAttribute(attr);

			if (log) {
				// Создаем запись в журнале изменения модели
				logVcsModelChanges(ENTITY_TYPE_ATTRIBUTE, ACTION_DELETE, attr, attr, null, conn);
				logModelChanges(ENTITY_TYPE_ATTRIBUTE, ACTION_DELETE, attr.uid, conn);
			}

		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	protected void deleteAttributeComp(KrnAttribute attr, boolean log, long dbVer) throws DriverException {

		try {
			// Удаляем запись из таблицы t_attrs
			QueryRunner qr = new QueryRunner(true);
			qr.update(conn, "DELETE FROM "+getDBPrefix()+"t_attrs WHERE c_id=?", attr.id);

			if (attr.rAttrId == 0) {
				if (attr.collectionType == COLLECTION_ARRAY
						|| attr.collectionType == COLLECTION_SET) {
					// Удаляем дополнительную таблицу
					String tname = getAttrTableName(attr);
					qr.update(conn, "DROP TABLE " + tname);
				} else {
					// Удаляем колонку в таблице класса
					String tname = getClassTableName(attr.classId);
					if (attr.typeClassId >= 99) {
						String fkname = getAttrFKName(attr.id);
						// удаляем FK
						// Запрос для получения имени FK на c_id для AT таблиц.
						PreparedStatement cnrByNamePst = conn
								.prepareStatement("SELECT CONSTRAINT_NAME"
										+ " FROM ALL_CONSTRAINTS"
										+ " WHERE CONSTRAINT_NAME=?");
						String str = getConstraintName(cnrByNamePst, fkname);
						if (str != null) {
							qr.update(conn, getDropForeignKeySql(tname, fkname));
						} else {
							OracleDriver3.this.log.warn("Constraint " + fkname + " not found.");
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

			if (log) {
				// Создаем запись в журнале изменения модели
				logVcsModelChanges(ENTITY_TYPE_ATTRIBUTE, ACTION_DELETE, attr, attr, null, conn);
				logModelChanges(ENTITY_TYPE_ATTRIBUTE, ACTION_DELETE, attr.uid, conn);
			}

		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	public KrnObject createObject(long classId, long trId, long objId,
			String uid, boolean rootOnly, Map<Pair<KrnAttribute, Long>, Object> initValues, boolean replLog) throws DriverException {
		String lastSql = "";
		
		try {
			final boolean sepClass = db.isSeparateClass(classId);
			QueryRunner qr = new QueryRunner(true);
			Long lid;
			if (objId > 0) {
				lid = objId;
			} else {
				lid = getNextId(getDBPrefix() + (sepClass ? "seq_ct" + db.getRootClass(classId).id : "seq_ct99"));
			}
			if (uid == null) {
				uid = getBaseId() + (sepClass ? "_" + classId : "") + "." + lid;
			}
			
			if (!sepClass) {
				lastSql = "INSERT INTO " + getDBPrefix() + "ct99 (c_uid,c_obj_id,c_class_id) VALUES (?,?,?)";
				
				try {
					qr.update(conn, lastSql, new Object[] { uid, lid, classId});
				} catch (SQLException e) {
					if (e.getErrorCode() == 28115) {
						log.error(lastSql);
						throw new DriverException("Вставка запрещена правилами FGAC", ErrorCodes.ERROR_FGAC_NOT_ALLOW);
					}
					throw e;
				}
			}
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
						lastSql = "INSERT INTO " + tname + "(" + cols + ") VALUES (" + vals + ")"; 
						PreparedStatement pst = conn.prepareStatement(lastSql);
						pst.setString(1, uid);
						pst.setLong(2, lid);
						pst.setLong(3, classId);
						pst.setLong(4, trId);
						if (initValues != null) {
							// Проход 2. Установка значений
							int i = 5;
							for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
								if (key.first.classId == cls.id && key.first.rAttrId == 0) {
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
						int c = pst.executeUpdate();
						pst.close();
					}
				}
			}
			KrnObject obj = new KrnObject(lid, uid, classId);
			logVcsDataChanges(obj, 1, 0, null,trId);
			if(replLog)
				logDataChanges(obj, 1, 0, trId);

			if (initValues != null) {
				for (Pair<KrnAttribute, Long> key : initValues.keySet()) {
					if (key.first.rAttrId > 0) {
						setValueImpl(obj, key.first, 0, 0, trId, initValues.get(key), false);
					}
				}
			}
			
			return obj;
		} catch (SQLException e) {
			log.error(lastSql);
			throw convertException(e);
		}
	}

	@Override
	public void createObjects(long classId, long trId, List<Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>>> objValues, boolean log) throws DriverException {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		Map<Long, PreparedStatement> clsPsts = new TreeMap<>();

		try {
			final boolean sepClass = db.isSeparateClass(classId);
			QueryRunner qr = new QueryRunner(true);
			long[] objIds = getNextIds(getDBPrefix() + (sepClass ? "seq_ct" + db.getRootClass(classId).id : "seq_ct99"), objValues.size());

			String lastSQL = "INSERT INTO " + getClassTableName(99) + " (c_uid,c_obj_id,c_class_id) VALUES (?,?,?)";
			pst = conn.prepareStatement(lastSQL);

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
    			String uid = getBaseId() + (sepClass ? "_" + classId : "") + "." + objId;
        		
    			if (!sepClass) {
    				try {
    	    			pst.setString(1, uid);
    	    			pst.setLong(2, objId);
    	    			pst.setLong(3, classId);
    	        		pst.addBatch();
    				} catch (SQLException e) {
    					if (e.getErrorCode() == 28115) {
    						this.log.error(lastSQL);
    						throw new DriverException("Вставка запрещена правилами FGAC", ErrorCodes.ERROR_FGAC_NOT_ALLOW);
    					}
    					throw e;
    				}
    			}
    			
    			for (long clsId : clsPsts.keySet()) {
					PreparedStatement clsPst = clsPsts.get(clsId);

					clsPst.setString(1, uid);
					clsPst.setLong(2, objId);
					clsPst.setLong(3, classId);
					clsPst.setLong(4, trId);
					if (initValues != null) {
						// Проход 2. Установка значений
						int i = 5;
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
			pst.executeBatch();
			for (long clsId : clsPsts.keySet()) {
				PreparedStatement clsPst = clsPsts.get(clsId);
				clsPst.executeBatch();
			}

			for (Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>> objValue : objValues) {
        		KrnObject obj = objValue.first;
        		Map<Pair<KrnAttribute, Long>, Object> initValues = objValue.second;

        		logVcsDataChanges(obj, 1, 0, null, trId);
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
			for (PreparedStatement clsPst : clsPsts.values()) {
				DbUtils.closeQuietly(clsPst);
			}
		}
	}

	@Override
	protected void commitDeletedObject(KrnObject obj, long trId, boolean deleteRefs, boolean isCreated,
			Set<Pair<Long,String>> jrbNodesToDelete) throws SQLException, DriverException {
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
				// Удаляем записи в АТ таблицах если созданы в этой же транзакции
				if(isCreated){
					for (KrnAttribute attr : attrs) {
						if (attr.rAttrId == 0 && attr.collectionType != COLLECTION_NONE) {
							String atName = getAttrTableName(attr);
							qr.update(conn, "DELETE FROM " + atName 
									+ " WHERE c_obj_id=? AND c_tr_id IN (0,?)",
									new Object[] { obj.id, trId });
						}
					}
				}
				qr.update(conn, "DELETE FROM " + tname
						+ " WHERE c_obj_id=? AND c_tr_id IN (0,?)",
						new Object[] { obj.id, trId });
			}
		}
	}
	
	private void createSequence(String tableName, String columnName, String sequenceName) throws SQLException{
		String sql = "";		
		//Вычисляем с какого числа начинать 
		//последовательность нумерования
		Statement st = conn.createStatement();
		sql = "SELECT max(" +  columnName + ") FROM " + tableName;
		ResultSet set = st.executeQuery(sql);			
		long max_id = 0;
		if(set.next()){
			max_id = set.getLong(1);
		}
		set.close();
		//Создание последовательности
		sql = "CREATE SEQUENCE " + sequenceName + " START WITH " + (max_id + 1);
		st.executeUpdate(sql);	
		st.close();
	}
	
	@Override
	public long getNextId(String sequence) throws DriverException {
		long res;
		String nextIdSQL = "SELECT " + sequence + ".nextval FROM dual";
		Statement st = null;
		ResultSet set = null;
		try {
			st = conn.createStatement();
			set = st.executeQuery(nextIdSQL);
			if (set.next()) {
				res = set.getBigDecimal(1).longValue();
			} else {
				throw new DriverException("Failed to get next ID");
			}
			return res;
		} catch (SQLException e) {
			log.error(nextIdSQL);
			log.error(e, e);
			throw new DriverException(e);
		} finally {
			DbUtils.closeQuietly(set);
			DbUtils.closeQuietly(st);
		}
	}
	
	public long[] getNextIds(String sequence, int count) throws DriverException {
		long[] res = new long[count];
		String nextIdSQL = "SELECT " + sequence + ".nextval FROM (select level from dual connect by level <= ?)";
		PreparedStatement pst = null;
		ResultSet set = null;
		try {
			pst = conn.prepareStatement(nextIdSQL);
			pst.setLong(1, count);
			set = pst.executeQuery();
			
			int i = 0;
			while (set.next()) {
				res[i++] = set.getLong(1);
			}
			return res;
		} catch (SQLException e) {
			log.error(nextIdSQL);
			log.error(e, e);
			throw new DriverException(e);
		} finally {
			DbUtils.closeQuietly(set);
			DbUtils.closeQuietly(pst);
		}
	}


	public long createLongTransaction() throws DriverException {
		return getNextId(getDBPrefix() + "seq_transaction");
	}

	@Override
	public void setForeignKeysEnabled(boolean enabled) throws DriverException {
	}

	
	@Override
	protected void commitCreatedObject(KrnObject obj, long trId,
			List<KrnClass> superClss, List<KrnAttribute> attrs, Set<Pair<Long,String>> jrbNodesToDelete)
			throws SQLException, DriverException {

		super.commitCreatedObject(obj, trId, superClss, attrs, jrbNodesToDelete);

		QueryRunner qr = new QueryRunner(true);
		for (KrnAttribute attr : attrs) {
			if (attr.rAttrId == 0) {
				if (attr.collectionType > 0) {
					String atName = getAttrTableName(attr);
					qr.update(conn, "UPDATE " + atName + " SET c_tr_id=?"
							+ " WHERE c_obj_id=? AND c_tr_id=?", new Object[] {
							0, obj.id, trId });
				}
			}
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
			int accessModifier,
			boolean isEncrypt
	) throws DriverException {
		return 	changeAttribute(id,typeId,name,collectionType,isUnique,isIndexed,isMultilingual,isRepl,size,flags,rAttrId,sAttrId,sDesc,true,tname, accessModifier, isEncrypt);
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
			boolean needLog,
			String tname,
			int accessModifier,
			boolean isEncrypt
	) throws DriverException {
		boolean tnameChanged = false;
		KrnAttribute attr = db.getAttributeById(id);
		String sql = "";
		
		if (collectionType != attr.collectionType) {
			throw new DriverException("Смена типа коллекции не реализована");
		}

		if (isMultilingual != attr.isMultilingual) {
			throw new DriverException("Смена мультиязычности не реализована");
		}

		if (isVersion(kz.tamur.or3.util.Tname.TnameVersionBD) && tname!= null && tname.trim().length() != 0){
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
		
		if (name!= null && name.trim().length() != 0){
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
		KrnAttribute attr_ = KrnUtil.createAttribute(attr.uid, attr.id,
				attr.name, attr.classId, typeId, attr.collectionType,
				attr.isUnique, attr.isMultilingual, attr.isIndexed,
				attr.size, attr.flags, attr.isRepl, attr.tname,
				attr.beforeEventExpr, attr.afterEventExpr,
				attr.beforeDelEventExpr, attr.afterDelEventExpr,
				attr.beforeEventTr, attr.afterEventTr,
				attr.beforeDelEventTr, attr.afterDelEventTr,
				attr.accessModifierType);
        if (typeId != attr.typeClassId 
        		|| size !=attr.size 
           		|| isIndexed !=attr.isIndexed 
        		|| (isIndexed && attr.isFullText()!=((flags & KrnAttribute.FULLTEXT)>0)) ) {
            String tName="";
            String cName= getColumnName(attr);
            if (attr.collectionType == COLLECTION_NONE) {
                tName=getClassTableName(attr.classId);
            }else{
                tName=getAttrTableName(attr);
            }
            if(typeId==PC_STRING){
                attr_.size=size;
            	if((flags & KrnAttribute.FULLTEXT)>0 && !isIndexed)
        			flags ^= KrnAttribute.FULLTEXT;
            }
            attr_.flags=flags;
            String cType=getSqlTypeName(attr);
            String cType_=getSqlTypeName(attr_);
            QueryRunner qr = new QueryRunner(true);
			Statement st=null ;
            try{
    			st = conn.createStatement();
                //Удаляем индекс если он есть
    			if (attr.isIndexed && attr.typeClassId != PC_BLOB) {
    				updateIndex(attr, false);
    				attr.isIndexed=false;
    				attr.setFullText(attr_.isFullText());
    			}
                
    			if (typeId != attr.typeClassId) {
                    //Удаляем старую колонку
                    sql = "ALTER TABLE "+tName+" DROP COLUMN "+cName;
                    st.executeUpdate(sql);
                    //qr.update(conn, sql);
                    //Создаем новую колонку с прежним именем и с новым типом
                    sql = "ALTER TABLE "+tName+" ADD "+cName+" "+cType_;
                    st.executeUpdate(sql);
                    //qr.update(conn, sql);
                } else if (!cType.equals(cType_)) {
                	boolean isChangeAttr=true;
               		int size_= getRealSizeColumn(attr);
               		if(size_>=size) {
               			//Если реальный размер колонки больше того, на который пытаются изменить,
               			//то в этом случае отсается реальный размер
                    	log.info("Реальный размер атрибута '"+size_+"' больше того, на который хотят изменить'"+size+"', поэтому остается прежний размер!");
               			size=size_;
               			attr_.size=size;
               			isChangeAttr=false;
              		}
               		if(isChangeAttr) {//Если размер колонки не меняется, то и менять нечего
						List<KrnObject> langs;
	    				if (attr.isMultilingual){
	    					langs = getSystemLangs();
	    				}else{
	    					langs = new ArrayList();
	    					langs.add(new KrnObject(-1,null,-1));
	    				}
	    				for (KrnObject lang : langs) {
	    					if(lang.id>0)
	    						cName = getColumnName(attr, lang.id);
	    					//Создаем временную колонку, предварительно проверив нет ли дубликата
	    					int ic=0;
	    					String c_name_temp=cName+"_"+ic;
	    					while(isColumnExists(tName,c_name_temp)){
	        					c_name_temp=cName+"_"+ ++ic;
	    					}
		                    sql = "ALTER TABLE "+tName+" ADD ("+c_name_temp+" "+cType+")";
		                    st.executeUpdate(sql);
		                    //Переписываем в нее содержимое
		                    sql = "UPDATE "+tName+" SET "+c_name_temp+" = "+cName;
		                    st.executeUpdate(sql);
		                    //Удаляем старую колонку
		                    sql = "ALTER TABLE "+tName+" DROP COLUMN "+cName;
		                    st.executeUpdate(sql);
		                    //Создаем новую колонку с прежним именем и с новым типом
		                    sql = "ALTER TABLE "+tName+" ADD ("+cName+" "+cType_+")";
		                    st.executeUpdate(sql);
		                    //Переписываем в нее данные
		                    sql = "UPDATE "+tName+" SET "+cName+" = "+c_name_temp;
		                    st.executeUpdate(sql);
		                    //Удаляем новую колонку
		                    sql = "ALTER TABLE "+tName+" DROP COLUMN "+c_name_temp;
		                    st.executeUpdate(sql);
	    				}
               		}
                }
               		
            } catch (SQLException e) {
    			throw convertException(e);
            }finally{
            	DbUtils.closeQuietly(st);
            }
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
			
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, name);
			pst.setLong(2, typeId);
			pst.setInt(3, collectionType);
			pst.setBoolean(4, isUnique);
			pst.setBoolean(5, isIndexed);
			pst.setBoolean(6, isMultilingual);
			pst.setBoolean(7, isRepl);
			pst.setInt(8, size);
			pst.setLong(9, flags);
			if (rAttrId != 0) {
				pst.setLong(10, rAttrId);
			} else {
				pst.setNull(10, Types.BIGINT);
			}
			if (sAttrId != 0) {
				pst.setLong(11, sAttrId);
			} else {
				pst.setNull(11, Types.BIGINT);
			}
			pst.setBoolean(12, sDesc);
			if(version>=EncryptColumnsVersionBD) {
				pst.setInt(13, accessModifier);
				pst.setBoolean(14, isEncrypt);
				pst.setLong(15, id);
			} else if(version>=AttrAccessModVersionBD) {
				pst.setInt(13, accessModifier);
				pst.setLong(14, id);
			}else
				pst.setLong(13, id);
			pst.executeUpdate();
			pst.close();

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
			if(needLog) {
				logVcsModelChanges(ENTITY_TYPE_ATTRIBUTE, ACTION_MODIFY, attr_, attr, null, conn);
				logModelChanges(ENTITY_TYPE_ATTRIBUTE, ACTION_MODIFY, attr.uid, conn);
			}
			return attr;

		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	@Override
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
				" FOR EACH ROW\n  DECLARE\n  msg varchar2(255); BEGIN\n"+
				" \tIF :NEW.c_tr_id=0 THEN\n";
		String usql = "CREATE TRIGGER " + utgName + " BEFORE UPDATE ON " + ctName +
				" FOR EACH ROW\n  DECLARE\n  msg varchar2(255); BEGIN\n";

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
					sqlExcept = " AND :NEW.c_class_id NOT IN (";
					for (Long child_id : child_ids) {
						sqlExcept += child_id + ",";
					}
					sqlExcept = sqlExcept.substring(0, sqlExcept.length() - 1) + ")";
				}
			}

			sql += "\t\tIF ((:NEW." + cmName + " IS NULL";
			if (a.typeClassId == PC_STRING) // string - проверяем на пустую строку
				sql += " OR LENGTH(:NEW." + cmName + ")=0";
			sql += ")";
			
			if (sqlExcept != null) sql += sqlExcept;

			sql += ") THEN\n";
			sql += "\t\t\t msg:=CONCAT('{01,',CONCAT(TO_CHAR(:NEW.c_obj_id),'," + a.id + "," + Funcs.translite(Funcs.sanitizeSQL(cls.name) + "." + Funcs.sanitizeSQL(a.name))+ "}'));\n";
			sql += "\t\t\t raise_application_error (-20000,msg);\n";
//			sql += "\t\t\tINSERT INTO "+getDBPrefix()+"t_msg VALUES (msg);\n";
			sql += "\t\tEND IF;\n";
			
			usql += "\tIF ((:NEW.c_tr_id=0 AND :OLD.c_tr_id!=0) OR :OLD." + cmName + " IS NOT NULL) AND ((:NEW." + cmName + " IS NULL";
			if (a.typeClassId == PC_STRING) // string - проверяем на пустую строку
				usql += " OR LENGTH(:NEW." + cmName + ")=0";
			usql += ")";

			if (sqlExcept != null) usql += sqlExcept;

			usql += ") THEN\n";
			usql += "\t\t msg:=CONCAT('{01,',CONCAT(TO_CHAR(:NEW.c_obj_id),'," + a.id + "," + Funcs.translite(Funcs.sanitizeSQL(cls.name) + "." + Funcs.sanitizeSQL(a.name))+ "}'));\n";
			usql += "\t\t raise_application_error (-20000,msg);\n";
//			usql += "\t\tINSERT INTO "+getDBPrefix()+"t_msg VALUES (msg);\n";
			usql += "\tEND IF;\n";

			if (sqlExcept != null) {
				log.debug("********************************************************");
				log.debug(sqlExcept);
				log.debug("********************************************************");
			}
		}
		
		sql += "\tEND IF;\n";
		sql += "END;\n";
		
		usql += "END;\n";

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

	@Override
	protected void updateTriggers(long classId)
	throws SQLException, DriverException {
		KrnClass cls = db.getClassById(classId);
		String itgName = getInsertTriggerName(cls.id);
		String utgName = getUpdateTriggerName(cls.id);
		
		Statement st = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			// Удаляем старые триггеры (если существуют)
			st = conn.createStatement();
			pst = conn.prepareStatement("SELECT COUNT(*) FROM USER_TRIGGERS WHERE LOWER(TRIGGER_NAME)=?");
			pst.setString(1, itgName.toLowerCase(Constants.OK));
			rs = pst.executeQuery();
			rs.next();
			if (rs.getInt(1) > 0) {
				st.executeUpdate("DROP TRIGGER " + itgName);
			}
			rs.close();

			pst.setString(1, utgName.toLowerCase(Constants.OK));
			rs = pst.executeQuery();
			rs.next();
			if (rs.getInt(1) > 0) {
				st.executeUpdate("DROP TRIGGER " + utgName);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(st);
			DbUtils.closeQuietly(pst);
		}

		List<KrnAttribute> attrs = db.getAttributesByClassId(classId, false);
		createTriggers(cls, attrs);
	}
	
	@Override
	protected void updateTriggersComp(long classId)
	throws SQLException, DriverException {

		KrnClass cls = getClassByIdComp(classId);
		String itgName = getInsertTriggerName(cls.id);
		String utgName = getUpdateTriggerName(cls.id);
		
		Statement st = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			// Удаляем старые триггеры (если существуют)
			st = conn.createStatement();
			pst = conn.prepareStatement("SELECT COUNT(*) FROM USER_TRIGGERS WHERE LOWER(TRIGGER_NAME)=?");
			pst.setString(1, itgName.toLowerCase(Constants.OK));
			rs = pst.executeQuery();
			rs.next();
			if (rs.getInt(1) > 0) {
				st.executeUpdate("DROP TRIGGER " + itgName);
			}
			rs.close();

			pst.setString(1, utgName.toLowerCase(Constants.OK));
			rs = pst.executeQuery();
			rs.next();
			if (rs.getInt(1) > 0) {
				st.executeUpdate("DROP TRIGGER " + utgName);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(st);
			DbUtils.closeQuietly(pst);
		}

		List<KrnAttribute> attrs = getAttributesByClassIdComp(cls, false);
		createTriggers(cls, attrs);
	}

	protected void updateIndex(KrnAttribute attr, boolean isIndexed)
	throws SQLException, DriverException {
		if (attr.typeClassId != PC_BLOB) {
			Statement st=null;
			try{
				st=conn.createStatement();
				String tname = (attr.collectionType == COLLECTION_ARRAY || attr.collectionType == COLLECTION_SET) ? getAttrTableName(attr)
						: getClassTableName(attr.classId);
				if (isIndexed) {
					if (attr.isMultilingual) {
						List<KrnObject> langs = getSystemLangs();
						for (KrnObject lang : langs) {
							String idxName = getIdxName(attr, lang.id);
							String cname = getColumnName(attr, lang.id);
							st.executeUpdate("CREATE INDEX " + idxName + " ON " + tname
									+ "(" + cname + ")"
									+ (attr.isFullText() 
											&& (attr.typeClassId == PC_STRING || attr.typeClassId == PC_MEMO || attr.typeClassId == PC_MMEMO)
											?IDX_CONTEXT:""));
						}
					} else {
						String idxName = getIdxName(attr, 0);
						String cname = getColumnName(attr, 0);
						st.executeUpdate("CREATE INDEX " + idxName + " ON " + tname
								+ "(" + cname + ")"
										+ (attr.isFullText() 
												&& (attr.typeClassId == PC_STRING || attr.typeClassId == PC_MEMO || attr.typeClassId == PC_MMEMO)
												?IDX_CONTEXT:""));
					}
				} else {
					if (attr.isMultilingual) {
						List<KrnObject> langs = getSystemLangs();
						for (KrnObject lang : langs) {
							String idxName = getIdxName(attr, lang.id);
							String idxType = getIndexType(tname, idxName);
							if(idxType!=null){
								if(attr.typeClassId == PC_STRING || attr.typeClassId == PC_MEMO || attr.typeClassId == PC_MMEMO || DOMAIN.equals(idxType))
									st.executeUpdate("DROP INDEX "+idxName);
								else
									st.executeUpdate(getDropIndexSql(tname, idxName));
							}
						}
					} else {
						String idxName = getIdxName(attr, 0);
						String idxType = getIndexType(tname, idxName);
						if(idxType!=null){
							if(attr.typeClassId == PC_STRING || attr.typeClassId == PC_MEMO || attr.typeClassId == PC_MMEMO || DOMAIN.equals(idxType))
								st.executeUpdate("DROP INDEX "+idxName);
							else
								st.executeUpdate(getDropIndexSql(tname, idxName));
						}
					}
				}
            } catch (SQLException e) {
    			throw convertException(e);
            }finally{
            	DbUtils.closeQuietly(st);
            }
		}
	}
	
	@Override
	protected void setValue(PreparedStatement pst, int colIndex, long typeId,
			Object value) throws SQLException {
		if (value == null) {
			pst.setNull(colIndex, getSqlType(typeId));
		} else if (typeId == PC_STRING) {
			String res = Funcs.normalizeInput((String) value);
			pst.setString(colIndex, res != null && res.length() > 2000 ? res.substring(0, 2000) : res);
		} else if (typeId == PC_BOOL) {
			if (value instanceof Number) {
				pst.setBoolean(colIndex, ((Number) value).longValue() != 0);
			} else {
				pst.setBoolean(colIndex, (Boolean) value);
			}
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
				pst.setString(colIndex, res);
//				setMemo(pst, colIndex, res);
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
	
	public void loadAttributeValue(KrnAttribute attr, Map values)
			throws SQLException, DriverException {
		String tabName = getClassTableName(attr.classId);
		String columnName = getColumnName(attr);
		if (values.get(columnName) == null)
			return;
		long objId = Long.parseLong((String) values.get("c_id"));
		PreparedStatement pst;
		pst = conn.prepareStatement("UPDATE " + tabName + " SET " + columnName
				+ "=? WHERE " + IDColumnName() + "=?");
		long colType = attr.typeClassId;
		if (colType == 3 || colType == 4) { // CID_TIME or CID_DATE
			long v = Long.parseLong((String) values.get(columnName));
			pst.setTimestamp(1, new java.sql.Timestamp(v));
		} else if (colType == 1) { // CID_STRING
			byte[] b = HexStringOutputStream.fromHexString((String) values
					.get(columnName));
			try {
				pst.setString(1, new String(b, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
	            log.error(e, e);
			}
		} else if (colType == 10) { // CID_BLOB
			byte[] b = HexStringOutputStream.fromHexString((String) values
					.get(columnName));
			Blob blob = oraConn.createBlob();
			OutputStream os = blob.setBinaryStream(1);
			try {
				os.write(b);
				os.close();
			} catch (IOException e) {
				throw new SQLException(e);
			}
			pst.setBlob(1, blob);
		} else if (colType == 8) {
			pst.setDouble(1, Double
					.parseDouble((String) values.get(columnName)));
		} else if (colType == 6) {
			byte[] b = HexStringOutputStream.fromHexString((String) values
					.get(columnName));
			Clob clob = oraConn.createClob();
			Writer w = clob.setCharacterStream(1);
			try {
				String str = new String(b, "UTF-8");
				w.write(str);
				w.close();
			} catch (IOException e) {
				throw new SQLException(e);
			}
			pst.setClob(1, clob);
		} else
			pst.setString(1, (String) values.get(columnName));

		pst.setLong(2, objId);

		pst.executeUpdate();
		pst.close();
	}

	public PreparedStatement getAttrPst(KrnAttribute attr) throws SQLException {
		// для загрузки атрибутов, значения которых содержатся в таблицах at
		String tabName = getAttrTableName(attr);
		String columnNameS = getColumnName(attr) + ",";
		String valueParam = ",?";
		String sql = "";
		if (attr.collectionType == COLLECTION_ARRAY) {
			sql = "INSERT INTO " + tabName + "(" + columnNameS
					+ " c_obj_id, c_index) VALUES(?,?" + valueParam + ")";
		} else if (attr.collectionType == COLLECTION_SET) {
			sql = "INSERT INTO " + tabName + "(" + columnNameS
					+ " c_obj_id) VALUES(?" + valueParam + ")";
		}
		if (attr.isMultilingual) {
			if (attr.collectionType == COLLECTION_ARRAY)
				sql = "INSERT INTO " + tabName + "(" + columnNameS
						+ " c_obj_id, c_index, c_lang_id) VALUES(?,?,?"
						+ valueParam + ")";
			else
				sql = "INSERT INTO " + tabName + "(" + columnNameS
						+ " c_obj_id, c_lang_id) VALUES(?,?" + valueParam + ")";
		}
		PreparedStatement pst = conn.prepareStatement(sql);
		return pst;
	}

	public void loadAttributeValue2(PreparedStatement pst, KrnAttribute attr,
			Map values) throws SQLException, DriverException {
		String tabName = getAttrTableName(attr);
		String columnName = getColumnName(attr);
		boolean valueIsNotEmpty = values.get(columnName) != null;
		if (valueIsNotEmpty) {
			long colType = attr.typeClassId;
			long objId = Long.parseLong((String) values.get("c_obj_id"));
			long index = 0;
			long langId = 0;
			if (colType == 3 || colType == 4) { // CID_TIME or CID_DATE
				long v = Long.parseLong((String) values.get(columnName));
				pst.setTimestamp(1, new java.sql.Timestamp(v));
			} else if (colType == 1) { // CID_STRING
				byte[] b = HexStringOutputStream.fromHexString((String) values
						.get(columnName));
				try {
					pst.setString(1, new String(b, "UTF-8"));
				} catch (UnsupportedEncodingException e) {
		            log.error(e, e);
				}
			} else if (colType == 6) { // CID_MEMO
				byte[] b = HexStringOutputStream.fromHexString((String) values
						.get(columnName));
				Clob clob = oraConn.createClob();
				Writer w = clob.setCharacterStream(1);
				try {
					String str = new String(b, "UTF-8");
					w.write(str);
					w.close();
				} catch (IOException e) {
					throw new SQLException(e);
				}
				pst.setClob(1, clob);
			} else if (colType == 10) { // CID_BLOB
				byte[] b = HexStringOutputStream.fromHexString((String) values
						.get(columnName));
				Blob blob = oraConn.createBlob();
				OutputStream os = blob.setBinaryStream(1);
				try {
					os.write(b);
					os.close();
				} catch (IOException e) {
					throw new SQLException(e);
				}
				pst.setBlob(1, blob);
			} else if (colType == 8) {
				pst.setDouble(1, Double.parseDouble((String) values
						.get(columnName)));
			} else
				pst.setString(1, (String) values.get(columnName));
			pst.setLong(2, objId);

			if (attr.collectionType == COLLECTION_ARRAY) {
				index = Integer.parseInt((String) values.get("c_index"));
				pst.setLong(3, index);
			}
			if (attr.isMultilingual) {
				langId = Long.parseLong((String) values.get("c_lang_id"));
				if (attr.collectionType > 0) {
					pst.setLong(4, langId);
				} else {
					pst.setLong(3, langId);
				}
			}
			pst.executeUpdate();
		}
	}

	public String IDColumnName() {
		return "c_id";
	}

	protected String getDropIndexSql(String tname, String idxName) {
		return "ALTER TABLE " + tname + " DROP CONSTRAINT " + Funcs.sanitizeSQL(idxName);
	}

	protected String getDropForeignKeySql(String tname, String fkname) {
		return "ALTER TABLE " + tname + " DROP CONSTRAINT " + Funcs.sanitizeSQL(fkname);
	}

	protected String getDropColumnSql(String tname, String cname) {
		return "ALTER TABLE " + tname + " DROP COLUMN " + Funcs.sanitizeSQL(cname);
	}

	public String getMemo(ResultSet rs, String name) throws SQLException {
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

	public String getMemo(ResultSet rs, int index) throws SQLException {
		Clob clobData = rs.getClob(index);
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

	protected void setMemo(PreparedStatement pst, int colIndex, String value)
			throws SQLException {
		if (value != null) {
			value = Funcs.normalizeInput(value);
			StringReader sr = new StringReader(value);
			pst.setCharacterStream(colIndex, sr, value.length());
		} else {
			pst.setNull(colIndex, Types.CLOB);
		}
	}

	public void rollbackLongTransaction(long trId) throws DriverException {
		try {
			saveDataLog();
			
			QueryRunner qr = new QueryRunner(true);
			Set<Long> clsIds = new HashSet<Long>();
			PreparedStatement pst = null;
			ResultSet rs = null;
			try {
				pst = conn.prepareStatement(
						"SELECT DISTINCT c_parent_id FROM "+getDBPrefix()+"t_changes,"+getDBPrefix()+"t_clinks" +
						" WHERE c_child_id=c_class_id AND c_tr_id=?");
			pst.setLong(1, trId);
				rs = pst.executeQuery();
			while (rs.next()) {
				long clsId = rs.getLong("c_parent_id");
				KrnClass cls = db.getClassById(clsId);
				if (clsId != ROOT_CLASS_ID && !cls.isVirtual())
					clsIds.add(clsId);
			}
			rs.close();
			pst.close();
			} finally {
				DbUtils.closeQuietly(rs);
				DbUtils.closeQuietly(pst);
			}
			// Удаляем записи в таблицах классов
			// записи в таблицах атрбиутов удаляются каскадно
			for (Long clsId : clsIds) {

				String ctName = getClassTableName(clsId);

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
			// Удаляем из ct99 созданные в транзакции объекты
			int n = qr.update(conn,
					"DELETE FROM "+getDBPrefix()+"ct99 WHERE c_obj_id IN " +
					"(SELECT c_object_id FROM "+getDBPrefix()+"t_changes " +
					"WHERE c_tr_id=? AND c_attr_id=1)", trId);

			rollbackDataLog(trId);
		} catch (SQLException e) {
			throw convertException(e);
		}
	}
	public void rollbackLongTransactionComp(long trId) throws DriverException {
		try {
			saveDataLog();
			
			QueryRunner qr = new QueryRunner(true);
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

			// Удаляем записи в таблицах классов
			// записи в таблицах атрбиутов удаляются каскадно
			for (Long clsId : clsIds) {

				String ctName = getClassTableName(clsId);
				qr.update(conn, "DELETE FROM " + ctName + " WHERE c_tr_id=?",
						trId);
			}
			// Удаляем из ct99 созданные в транзакции объекты
			int n = qr.update(conn,
					"DELETE FROM "+getDBPrefix()+"ct99 WHERE c_obj_id IN " +
					"(SELECT c_object_id FROM "+getDBPrefix()+"t_changes " +
					"WHERE c_tr_id=? AND c_attr_id=1)", trId);

			rollbackDataLog(trId);
		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	
	@Override
	protected long upgradeImpl(long v) throws SQLException, DriverException {
		if (version <= 3) {
			QueryRunner qr = new QueryRunner(true);
			Statement ust = conn.createStatement();

			List<KrnAttribute> attrs = (List<KrnAttribute>) qr.query(conn,
					"SELECT * FROM "+getDBPrefix()+"t_attrs", new AttrResultSetHandler());

			int count = attrs.size();
			int curr = 0;
			// Запрос для получения имени FK на c_id для AT таблиц.
			PreparedStatement cnrPst = conn
					.prepareStatement("SELECT cnr.CONSTRAINT_NAME"
							+ " FROM ALL_CONS_COLUMNS col, ALL_CONSTRAINTS cnr"
							+ " WHERE col.TABLE_NAME=? and col.COLUMN_NAME='C_OBJ_ID'"
							+ " AND cnr.CONSTRAINT_NAME=col.CONSTRAINT_NAME"
							+ " AND cnr.CONSTRAINT_TYPE='R'");
			
			// Запрос для получения имени FK на c_id для AT таблиц.
			PreparedStatement cnrByNamePst = conn
					.prepareStatement("SELECT CONSTRAINT_NAME"
							+ " FROM ALL_CONSTRAINTS"
							+ " WHERE CONSTRAINT_NAME=?");
			// Запрос для получения имени PK для таблицы.
			PreparedStatement pkPst = conn
					.prepareStatement("SELECT CONSTRAINT_NAME"
							+ " FROM ALL_CONSTRAINTS"
							+ " WHERE TABLE_NAME=? AND CONSTRAINT_TYPE='P'");

			for (KrnAttribute attr : attrs) {
				log.info("Attribute " + attr.id + " pass 1");
				String ctName = getClassTableNameComp(attr.classId);
				String vtName = getClassTableNameComp(attr.typeClassId);
				String atName = getAttrTableName(attr);
				String cmName = getColumnName(attr);

				// Если атрибут объектного типа
				if (attr.typeClassId > 99) {
					String fkName = getAttrFKName(attr.id);
					if (attr.collectionType == COLLECTION_NONE) {
						// удаляем FK
						String str = getConstraintName(cnrByNamePst, fkName);
						if (str != null) {
							ust.executeUpdate("ALTER TABLE " + ctName
									+ " DROP CONSTRAINT " + fkName);
						} else {
							log.warn("Constraint " + fkName + " not found.");
						}
						// Меняем ссылку с c_id на c_obj_id
						if (attr.classId == attr.typeClassId) {
							ust.executeUpdate("CREATE TABLE "+getDBPrefix()+"t1 ("
									+ "c_id INTEGER NOT NULL,"
									+ "c_obj_id INTEGER NOT NULL,"
									+ "PRIMARY KEY(c_id))");
							ust.executeUpdate("INSERT INTO "+getDBPrefix()+"t1 (c_id,c_obj_id)"
									+ " SELECT c_id,c_obj_id FROM " + ctName);
							ust.executeUpdate("UPDATE " + ctName + " SET "
									+ cmName + "=(SELECT c_obj_id FROM "+getDBPrefix()+"t1"
									+ " WHERE t1.c_id=" + cmName + ") WHERE "
									+ cmName + " IS NOT NULL");
							ust.executeUpdate("DROP TABLE "+getDBPrefix()+"t1");
						} else {
							ust.executeUpdate("UPDATE " + ctName + " SET "
									+ cmName + "=(SELECT c_obj_id FROM "
									+ vtName + " vt WHERE vt.c_id=" + cmName
									+ ") WHERE " + cmName + " IS NOT NULL");
						}
					} else {
						// удаляем FK
						String str = getConstraintName(cnrByNamePst, fkName);
						if (str != null) {
							ust.executeUpdate("ALTER TABLE " + atName
									+ " DROP CONSTRAINT " + fkName);
						} else {
							log.warn("Constraint " + fkName + " not found.");
						}
						// Меняем ссылку с c_id на c_obj_id
						ust.executeUpdate("UPDATE " + atName + " SET " + cmName
								+ "=(SELECT c_obj_id FROM " + vtName
								+ " vt WHERE vt.c_id=" + cmName + ") WHERE "
								+ cmName + " IS NOT NULL");
					}
				}

				if (attr.isMultilingual
						|| attr.collectionType != COLLECTION_NONE) {
					// Удаляем FK на с_id
					cnrPst.setString(1, atName.toUpperCase(Constants.OK));
					ResultSet rs = cnrPst.executeQuery();
					if (rs.next()) {
						String fkName = getString(rs, 1);
						ust.executeUpdate("ALTER TABLE " + atName
								+ " DROP CONSTRAINT " + fkName);
					} else {
						log.warn("Не найден FK для таблицы " + atName);
					}
					rs.close();
					// Удаляем PK
					String pkName = getConstraintName(pkPst, atName
							.toUpperCase(Constants.OK));
					if (pkName != null) {
						ust.executeUpdate("ALTER TABLE " + atName
								+ " DROP PRIMARY KEY");
					} else {
						log.warn("Не найден PK для таблицы " + atName);
					}
					// Добавляем колонку для ID транзакции
					ust.executeUpdate("ALTER TABLE " + atName
							+ " ADD (c_tr_id INTEGER)");
					ust.executeUpdate("UPDATE " + atName + " SET c_tr_id=0");
					ust.executeUpdate("ALTER TABLE " + atName
							+ " MODIFY c_tr_id INTEGER NOT NULL");

					// Добавляем колонку c_del
					ust.executeUpdate("ALTER TABLE " + atName
							+ " ADD (c_del INTEGER DEFAULT 0)");
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
					sql.append(",").append("c_del");
					sql.append("))");
					ust.executeUpdate(sql.toString());
				}
			}
			cnrPst.close();
			cnrByNamePst.close();
			pkPst.close();

			List<KrnClass> classes = qr.query(conn,
					"SELECT * FROM "+getDBPrefix()+"t_classes WHERE c_id>10",
					new ClassResultSetHandler());
			for (KrnClass cls : classes) {
				log.info("Class " + cls.id);
				String ctName = getClassTableName(cls, true);
				// Удаляем PK
				ust
						.executeUpdate("ALTER TABLE " + ctName
								+ " DROP PRIMARY KEY");
				// Удаляем индекс c_is_del
				String idxName = cls.id == 99 ? "DEL_OBJ_ID_TR_IDX" : ctName
						+ "_DEL_OBJ_TR_IDX";
				ust.executeUpdate("DROP INDEX " + idxName);
				// Создаем PK
				ust.executeUpdate("ALTER TABLE " + ctName
						+ " ADD (PRIMARY KEY(c_obj_id,c_tr_id))");
				// Удаляем колонку c_id
				ust
						.executeUpdate("ALTER TABLE " + ctName
								+ " DROP COLUMN c_id");
			}

			for (KrnAttribute attr : attrs) {
				log.info("Attribute " + attr.id + " pass 2");
				// Если атрибут объектного типа
				if (attr.isMultilingual
						|| attr.collectionType != COLLECTION_NONE) {
					String ctName = getClassTableNameComp(attr.classId);
					String atName = getAttrTableName(attr);
					// Создаем FK на (с_obj_id, c_tr_id)
					ust
							.executeUpdate("ALTER TABLE "
									+ atName
									+ " ADD (CONSTRAINT "
									+ getAttrFKName(attr)
									+ " FOREIGN KEY (c_obj_id,c_tr_id) REFERENCES "
									+ ctName
									+ " (c_obj_id,c_tr_id)"
									+ " ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)");
				}
			}
			// добавляем в t_changes все записи атрибутов для которых значения
			// в нулевой и текущей транзакциях не одинаковые(исключая blob и
			// clob)
			count = attrs.size();
			curr = 0;
			for (KrnAttribute attr : attrs) {
				log.info("Attribute " + attr.id + " pass 3 " + curr + "/"
						+ count);
				curr++;
				String ctName = getClassTableNameComp(attr.classId);
				String atName = getAttrTableName(attr);
				if (attr.id < 100)
					continue;
				if (attr.typeClassId == PC_MEMO || attr.typeClassId == PC_BLOB) {
					ust
							.executeUpdate("INSERT INTO "+getDBPrefix()+"t_changes (c_object_id,c_attr_id,c_lang_id,c_tr_id) "
									+ "SELECT t2.c_obj_id,"
									+ attr.id
									+ ",0,t2.c_tr_id FROM "
									+ ctName
									+ " t1 "
									+ "LEFT JOIN "
									+ ctName
									+ " t2 ON t1.c_obj_id=t2.c_obj_id "
									+ "WHERE t1.c_tr_id=0 AND t2.c_tr_id>0 AND t2.c_is_del=0");
				} else if (attr.collectionType == COLLECTION_SET) {
					if (attr.isMultilingual) {
						ust
								.executeUpdate("INSERT INTO "+getDBPrefix()+"t_changes (c_object_id,c_attr_id,c_lang_id,c_tr_id) "
										+ "SELECT t2.c_obj_id,"
										+ attr.id
										+ ",t3.c_lang_id,t2.c_tr_id FROM "
										+ ctName
										+ " t1 "
										+ "LEFT JOIN "
										+ ctName
										+ " t2 ON t1.c_obj_id=t2.c_obj_id "
										+ "LEFT JOIN "
										+ atName
										+ " t3 ON t1.c_obj_id=t3.c_obj_id "
										+ "LEFT JOIN "
										+ atName
										+ " t4 ON t2.c_obj_id=t4.c_obj_id "
										+ "WHERE t1.c_tr_id=0 AND t2.c_tr_id>0 AND t2.c_is_del=0 "
										+ "AND t3.c_lang_id=t4.c_lang_id AND t3.cm"
										+ attr.id + "<>t4."+getColumnName(attr));
					} else {
						ust
								.executeUpdate("INSERT INTO "+getDBPrefix()+"t_changes (c_object_id,c_attr_id,c_lang_id,c_tr_id) "
										+ "SELECT t2.c_obj_id,"
										+ attr.id
										+ ",0,t2.c_tr_id FROM "
										+ ctName
										+ " t1 "
										+ "LEFT JOIN "
										+ ctName
										+ " t2 ON t1.c_obj_id=t2.c_obj_id "
										+ "LEFT JOIN "
										+ atName
										+ " t3 ON t1.c_obj_id=t3.c_obj_id "
										+ "LEFT JOIN "
										+ atName
										+ " t4 ON t2.c_obj_id=t4.c_obj_id "
										+ "WHERE t1.c_tr_id=0 AND t2.c_tr_id>0 AND t2.c_is_del=0 "
										+ "AND t3.cm"
										+ attr.id
										+ "<>t4.cm"
										+ attr.id);
					}
				} else if (attr.collectionType == COLLECTION_ARRAY) {
					if (attr.isMultilingual) {
						ust
								.executeUpdate("INSERT INTO "+getDBPrefix()+"t_changes (c_object_id,c_attr_id,c_lang_id,c_tr_id) "
										+ "SELECT t2.c_obj_id,"
										+ attr.id
										+ ",t3.c_lang_id,t2.c_tr_id FROM "
										+ ctName
										+ " t1 "
										+ "LEFT JOIN "
										+ ctName
										+ " t2 ON t1.c_obj_id=t2.c_obj_id "
										+ "LEFT JOIN "
										+ atName
										+ " t3 ON t1.c_obj_id=t3.c_obj_id "
										+ "LEFT JOIN "
										+ atName
										+ " t4 ON t2.c_obj_id=t4.c_obj_id "
										+ "WHERE t1.c_tr_id=0 AND t2.c_tr_id>0 AND t2.c_is_del=0 "
										+ "AND t3.c_lang_id=t4.c_lang_id AND t3.c_index=t4.c_index AND t3.cm"
										+ attr.id + "<>t4."+getColumnName(attr));
					} else {
						ust
								.executeUpdate("INSERT INTO "+getDBPrefix()+"t_changes (c_object_id,c_attr_id,c_lang_id,c_tr_id) "
										+ "SELECT t2.c_obj_id,"
										+ attr.id
										+ ",0,t2.c_tr_id FROM "
										+ ctName
										+ " t1 "
										+ "LEFT JOIN "
										+ ctName
										+ " t2 ON t1.c_obj_id=t2.c_obj_id "
										+ "LEFT JOIN "
										+ atName
										+ " t3 ON t1.c_obj_id=t3.c_obj_id "
										+ "LEFT JOIN "
										+ atName
										+ " t4 ON t2.c_obj_id=t4.c_obj_id "
										+ "WHERE t1.c_tr_id=0 AND t2.c_tr_id>0 AND t2.c_is_del=0 "
										+ "AND t3.c_index=t4.c_index AND t3.cm"
										+ attr.id + "<>t4."+getColumnName(attr));
					}

				} else if (attr.isMultilingual) {
					ust
							.executeUpdate("INSERT INTO "+getDBPrefix()+"t_changes (c_object_id,c_attr_id,c_lang_id,c_tr_id) "
									+ "SELECT t2.c_obj_id,"
									+ attr.id
									+ ",t3.c_lang_id,t2.c_tr_id FROM "
									+ ctName
									+ " t1 "
									+ "LEFT JOIN "
									+ ctName
									+ " t2 ON t1.c_obj_id=t2.c_obj_id "
									+ "LEFT JOIN "
									+ atName
									+ " t3 ON t1.c_obj_id=t3.c_obj_id "
									+ "LEFT JOIN "
									+ atName
									+ " t4 ON t2.c_obj_id=t4.c_obj_id "
									+ "WHERE t1.c_tr_id=0 AND t2.c_tr_id>0 AND t2.c_is_del=0 "
									+ "AND t3.c_lang_id=t4.c_lang_id AND t3.cm"
									+ attr.id + "<>t4."+getColumnName(attr));
				} else {
					ust
							.executeUpdate("INSERT INTO "+getDBPrefix()+"t_changes (c_object_id,c_attr_id,c_lang_id,c_tr_id) "
									+ "SELECT t2.c_obj_id,"
									+ attr.id
									+ ",0,t2.c_tr_id FROM "
									+ ctName
									+ " t1 "
									+ "LEFT JOIN "
									+ ctName
									+ " t2 ON t1.c_obj_id=t2.c_obj_id "
									+ "WHERE t1.c_tr_id=0 AND t2.c_tr_id>0 AND t2.c_is_del=0 AND t1.cm"
									+ attr.id + "<>t2."+getColumnName(attr));

				}
			}
			// добавляем в t_changes все записи для созданных и удаленных
			// объектов в ненулевой транзакции
			log.info("Созданные!");
			ust
					.executeUpdate("INSERT INTO "+getDBPrefix()+"t_changes (c_object_id,c_attr_id,c_lang_id,c_tr_id) "
							+ "SELECT t1.c_obj_id,1,0,t1.c_tr_id FROM "+getDBPrefix()+"ct99 t1 WHERE t1.c_tr_id<>0 AND c_is_del=0 "
							+ "AND NOT EXISTS (SELECT * FROM "+getDBPrefix()+"ct99 t2 WHERE t2.c_tr_id=0 AND t2.c_obj_id=t1.c_obj_id)");
			log.info("Удаленные!");
			ust
					.executeUpdate("INSERT INTO "+getDBPrefix()+"t_changes (c_object_id,c_attr_id,c_lang_id,c_tr_id) "
							+ "SELECT t1.c_obj_id,2,0,t1.c_tr_id FROM "+getDBPrefix()+"ct99 t1 WHERE t1.c_tr_id<>0 AND c_is_del=1");
			ust.close();
			version = 3;
		}
    	if (version < 4) {
    		QueryRunner qr = new QueryRunner(true);
    		
			// Удаляем PK
			// Запрос для нахождения имени PK для таблицы
    		qr.update(conn,
    			"ALTER TABLE "+getDBPrefix()+"t_classes ADD c_comment CLOB");

    		qr.update(conn, "ALTER TABLE "+getDBPrefix()+"t_attrs ADD c_rattr_id INTEGER");
    		qr.update(conn, "ALTER TABLE "+getDBPrefix()+"t_attrs ADD c_sattr_id INTEGER");
    		qr.update(conn, "ALTER TABLE "+getDBPrefix()+"t_attrs ADD c_sdesc INTEGER");
    		qr.update(conn,
				"ALTER TABLE "+getDBPrefix()+"t_attrs ADD FOREIGN KEY (c_rattr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id)");
    		qr.update(conn,
				"ALTER TABLE "+getDBPrefix()+"t_attrs ADD FOREIGN KEY (c_sattr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id)");
    		qr.update(conn, "ALTER TABLE "+getDBPrefix()+"t_attrs ADD c_comment CLOB");

			qr.update(conn, "ALTER TABLE "+getDBPrefix()+"t_methods ADD c_comment CLOB");
			version = 4;
    	}
    	if (version < 6) {
    		QueryRunner qr = new QueryRunner(true);
    		Statement st = conn.createStatement();
    		ResultSet rs = st.executeQuery(
    				"SELECT c_id,c_class_id,c_is_multilingual" +
    				" FROM "+getDBPrefix()+"t_attrs WHERE c_rattr_id is null and c_col_type=1");
    		while (rs.next()) {
    			long attrId = rs.getLong(1);
				KrnAttribute a = db.getAttributeById(attrId);
    			boolean isMultiLang = rs.getBoolean(3);
    			String atName = getAttrTableName(a);
				qr.update(conn, "ALTER TABLE " + atName + " ADD c_id INTEGER");
				if (isMultiLang) {
					qr.update(conn, "UPDATE " + atName + " SET c_id=c_index*c_lang_id");
				} else {
					qr.update(conn, "UPDATE " + atName + " SET c_id=c_index");
				}
				qr.update(conn, "ALTER TABLE " + atName + " MODIFY c_id INTEGER NOT NULL");
    		}
    		rs.close();
    		st.close();
    		version = 6;
    	}
    	if (version < 7) {
			log.info("Апгрейд БД до версии 7...");
    		// Зпрос для проверики наличия индекса в таблице.
    		PreparedStatement idxPst = conn.prepareStatement(
    				"select count(*) from user_indexes where index_name=?");
    			
    		Statement ust = conn.createStatement();
    		Statement qst = conn.createStatement();
    		ResultSet rs = qst.executeQuery("SELECT c_id FROM "+getDBPrefix()+"t_classes WHERE c_id>99");
    		while (rs.next()) {
    			long clsId = rs.getLong(1);
    			log.info("Создание индекса для "+getClassTableNameComp(clsId) + ": ");
    			String tbName = getClassTableNameComp(clsId);
    			String idxName = "UID" + clsId + "IDX";
    			idxPst.setString(1, idxName);
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
			st.executeUpdate("DROP INDEX ch_tr_idx");
			// Добавляем колонку c_is_repl в таблицу t_changes
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changes ADD c_is_repl NUMBER(1)");
			st.executeUpdate("UPDATE "+getDBPrefix()+"t_changes SET c_is_repl=1");
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changes MODIFY c_is_repl NUMBER(1) NOT NULL");
			// Создаем составной индекс c_tr_id,c_is_repl
			st.executeUpdate("CREATE INDEX ch_tr_repl_idx ON "+getDBPrefix()+"t_changes(c_tr_id,c_is_repl)");
			st.close();
			
			log.info("Апгрейд БД до версии 8 успешно завершен.");
			version = 8;
    	}
    	
    	// Изменение механизма блокировок
    	// Исключение таблиц для классов без атрибутов и не являющихся типом
    	// какого-либо атрибута
    	// Оптимизация работы с таблицей ct99
    	if (version < 9) {
    		Statement st = conn.createStatement();
    		//Пересоздаем последовательности
    		long max_id=0;
    		ResultSet set = st.executeQuery("SELECT max(c_id) FROM "+getDBPrefix()+"t_attrs");
			if (set.next()) {
				max_id = set.getLong(1);
			} else
				max_id = 0;
			st.executeUpdate("DROP SEQUENCE "+getDBPrefix()+"seq_attrs");
			st.executeUpdate("CREATE SEQUENCE "+getDBPrefix()+"seq_attrs"
							+ (max_id > 1 ? " START WITH "
									+ (max_id + 1) : ""));

    		max_id=0;
    		set = st.executeQuery("SELECT max(c_id) FROM "+getDBPrefix()+"t_classes");
			if (set.next()) {
				max_id = set.getLong(1);
			} else
				max_id = 0;
			st.executeUpdate("DROP SEQUENCE "+getDBPrefix()+"seq_classes");
			st.executeUpdate("CREATE SEQUENCE "+getDBPrefix()+"seq_classes"
							+ (max_id > 1 ? " START WITH "
									+ (max_id + 1) : ""));
    		max_id=0;
    		set = st.executeQuery("SELECT max(c_id) FROM "+getDBPrefix()+"t_methods");
			if (set.next()) {
				max_id = set.getLong(1);
			} else
				max_id = 0;
			st.executeUpdate("DROP SEQUENCE "+getDBPrefix()+"seq_methods");
			st.executeUpdate("CREATE SEQUENCE "+getDBPrefix()+"seq_methods"
							+ (max_id > 1 ? " START WITH "
									+ (max_id + 1) : ""));
			// Таблица классов
			// c_mod
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_classes ADD c_mod INTEGER");
    		st.executeUpdate("UPDATE "+getDBPrefix()+"t_classes SET c_mod=0");
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_classes MODIFY c_mod INTEGER NOT NULL");
			// c_cuid
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_classes ADD c_cuid VARCHAR2(36)");
    		st.executeUpdate("UPDATE "+getDBPrefix()+"t_classes SET c_cuid=c_id");
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_classes MODIFY c_cuid VARCHAR2(36) NOT NULL");
			st.executeUpdate("CREATE UNIQUE INDEX idx_cuid ON "+getDBPrefix()+"t_classes(c_cuid)");

			// Таблица атрибутов
			// c_auid
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_attrs ADD c_auid VARCHAR2(36)");
    		st.executeUpdate("UPDATE "+getDBPrefix()+"t_attrs SET c_auid=c_id");
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_attrs MODIFY c_auid VARCHAR2(36) NOT NULL");
			st.executeUpdate("CREATE UNIQUE INDEX idx_auid ON "+getDBPrefix()+"t_attrs(c_auid)");

			// Таблица методов
			// c_id
			// Запрос для нахождения имени PK для таблицы
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_methods ADD c_muid VARCHAR2(36)");
    		st.executeUpdate("UPDATE "+getDBPrefix()+"t_methods SET c_muid=c_id");
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_methods MODIFY c_muid VARCHAR2(36) NOT NULL");
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_methods DROP COLUMN c_id");
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_methods ADD PRIMARY KEY (c_muid)");
			
			// Таблица изменения модели данных
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changescls RENAME COLUMN c_entity_id TO c_entity_id_tmp");
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changescls ADD c_entity_id VARCHAR2(36)");
			st.executeUpdate("UPDATE "+getDBPrefix()+"t_changescls SET c_entity_id=c_entity_id_tmp");
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changescls MODIFY c_entity_id VARCHAR2(36) NOT NULL");
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changescls DROP COLUMN c_entity_id_tmp");
			
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
			log.info(sql);
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
			deleteAttribute(cfgAttr.id, false, version);
			// Удалем атбрибут 'locks' в классе 'Object'
			deleteAttribute(getAttributeByNameComp(objCls, "locks").id, false, version);
			// Удаляем из ct99 все записи из зависших транзакций
			// Откат зависших транзакций делаем позже после изменений в структуре
			//  т.к. процедура rollbackLongTransaction работает уже с новой структурой
			
			List<Long> badTrIds = new ArrayList<Long>();
			ResultSet rs = st.executeQuery(
					"SELECT DISTINCT c_tr_id FROM "+getDBPrefix()+"ct99 " +
					"WHERE c_tr_id<>0 AND c_tr_id NOT IN (" +
					"SELECT "+getColumnName(trnsAttr) + " FROM "+getClassTableNameComp(trnsAttr.classId) +
					" WHERE "+getColumnName(trnsAttr) +" IS NOT NULL)");
			while (rs.next()) {
				badTrIds.add(rs.getLong(1));
			}
			rs.close();
			PreparedStatement pst = conn.prepareStatement(
					"DELETE FROM "+getDBPrefix()+"ct99 WHERE c_tr_id=?");
			for (Long trId : badTrIds) {
				pst.setLong(1, trId);
				pst.executeUpdate();
			}
			pst.close();
			// Оставляем только по одной записи для каждого объекта
			rs = st.executeQuery(
					"SELECT c_obj_id,c_tr_id FROM "+getDBPrefix()+"ct99 t1 " +
					"WHERE c_tr_id<>0 AND EXISTS (" +
					"SELECT * FROM "+getDBPrefix()+"ct99 t2 " +
					"WHERE t2.c_obj_id=t1.c_obj_id AND t2.c_tr_id=0)");
			List<Pair<Long, Long>> recs = new ArrayList<Pair<Long,Long>>();
			while (rs.next()) {
				recs.add(new Pair<Long, Long>(rs.getLong(1), rs.getLong(2)));
			}
			rs.close();
			pst = conn.prepareStatement(
					"DELETE FROM "+getDBPrefix()+"ct99 WHERE c_obj_id=? AND c_tr_id=?");
			for (Pair<Long, Long> rec : recs) {
				pst.setLong(1, rec.first);
				pst.setLong(2, rec.second);
				pst.executeUpdate();
			}
			pst.close();
			// Удаляем первичный ключ (необходимо для удаления 'c_tr_id')
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"ct99 DROP PRIMARY KEY");
			// Удаляем индексы содержащие 'c_tr_id' (необходимо для удаления 'c_tr_id')
			pst = conn.prepareStatement(
					"SELECT index_name from ALL_IND_COLUMNS WHERE " +
					"table_owner=? AND table_name=? AND column_name=?");
			pst.setString(1, conn.getCatalog());
			pst.setString(2, "CT99");
			pst.setString(3, "C_TR_ID");
			rs = pst.executeQuery();
			while (rs.next()) {
				String idxName = getString(rs, 1);
				st.executeUpdate("ALTER TABLE "+getDBPrefix()+"ct99 DROP INDEX " + idxName);
			}
			rs.close();
			pst.close();
			// Удаляем лишние колонки
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"ct99 DROP COLUMN c_is_del");
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"ct99 DROP COLUMN c_tr_id");
			// Создаем первичный ключ
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"ct99 ADD (PRIMARY KEY (c_obj_id))");
			// Создаем уникальный индекс для 'c_uid'
			st.executeUpdate("CREATE UNIQUE INDEX uid99idx ON "+getDBPrefix()+"ct99 (c_uid)");
			
			// Теперь, после всех изменений в структуре откатываем повисшие
			//  транзакции
			for (Long trId : badTrIds) {
				rollbackLongTransactionComp(trId);
			}

			// Удаляем атрибут 'зап табл цикла обмена'
			KrnClass uclsCls = getClassByNameComp("Пользовательский класс");
			KrnAttribute ztexAttr = getAttributeByNameComp(uclsCls, "зап табл цикла обмена");
			if (ztexAttr != null)
				deleteAttribute(ztexAttr.id, false, version);

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
			cls = getClassByNameComp("ReportPrinter");
			createBasesAttr(basesAttr, cls.id, "34469857-bfb6-49c8-b01f-dfd98805fdb7");

			// Удаляем атрибут 'bases'
			deleteAttributeComp(basesAttr, false, version);

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
			
			st.close();
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
    			log.info("Апгрейд атрибута " + attr.id);
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
    							atName + ".c_lang_id=" + langId + 
    							" AND c_del=0)");
    				}
    				// Удаляем дополнительную таблицу
    				st.executeUpdate("DROP TABLE " + atName);
    			} else {
    				// Если коллекция
    				// Переименовываем дополнительную таблицу
    				st.executeUpdate("RENAME " + atName + " TO " + atName + "_tmp");
    				// Удаляем FK, иначе ORACLE не дает создать новую таблицу
    				// с таким же именем FK
    				st.executeUpdate("ALTER TABLE " + atName + "_tmp DROP CONSTRAINT " + getAttrFKName(attr));
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
    		st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changes ADD c_class_id INTEGER");
    		st.executeUpdate("UPDATE "+getDBPrefix()+"t_changes SET c_class_id=(SELECT c_class_id FROM "+getDBPrefix()+"ct99 WHERE c_obj_id=c_object_id)");
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
    		createOracleDBContext();
            upgradeTo16();
			version = 16;
        }
    	
    	if (version < 17) {
    	    if (upgradeTo17()) {
    	    	version = 17;
                setId("version", version);
                commit();
    	    } else {
    	    	throw new DriverException("ERROR to Update DATA BASE "+kz.tamur.or3.util.Tname.TnameVersionBD+".\n");
    	    }
    	}
    	
    	KrnClass protocolRuleCls = getClassByNameComp("ProtocolRule");
        KrnClass eventTypeCls = getClassByNameComp("EventType");
    	if (getAttributeByNameComp(protocolRuleCls, "eventType") == null) {
    		KrnAttribute a = createAttribute(-1, "eb8f27e6-722b-40a8-af1d-70a75c61d0ba", protocolRuleCls.id, eventTypeCls.id, "eventType", 0, false, true, false, true, 0, 0, 0, 0, false, false, null, 0);
            setAttributeComment(a.uid, "Тип событий, которые должны протоколироваться");
    	}
    	
    	if (version < 18) {
    		String call = "CREATE OR REPLACE PACKAGE " + db.getSchemeName() + ".hr_sec_ctx IS " +
    				"PROCEDURE set_user_id (u NUMBER); " +
    				"PROCEDURE set_user_uid (u VARCHAR2); " +
    				"PROCEDURE set_user_name (u VARCHAR2); " +
    				"PROCEDURE set_role_id (u NUMBER); " +
    				"PROCEDURE set_role_uid (u VARCHAR2); " +
    				"PROCEDURE set_role_name (u VARCHAR2); " +
    				"PROCEDURE set_balans_id (u NUMBER); " +
    				"PROCEDURE set_balans_uid (u VARCHAR2); " +
    				"PROCEDURE set_balans_name (u VARCHAR2); " +
    				"END hr_sec_ctx;";

    		Statement st = conn.createStatement();
    		st.executeUpdate(call);
    		st.close();

    		call = "CREATE OR REPLACE PACKAGE BODY " + db.getSchemeName() + ".hr_sec_ctx IS " +
    				"PROCEDURE set_user_id (u NUMBER) IS " +
    				"BEGIN " +
    				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'id', u); " +
    				"END set_user_id; " +
    				"PROCEDURE set_user_uid (u VARCHAR2) IS " +
    				"BEGIN " +
    				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'uid', u); " +
    				"END set_user_uid; " +
    				"PROCEDURE set_user_name (u VARCHAR2) IS " +
    				"BEGIN " +
    				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'name', u); " +
    				"DBMS_SESSION.SET_IDENTIFIER(u); " +
    				"END set_user_name; " +
    				"PROCEDURE set_role_id (u NUMBER) IS " +
    				"BEGIN " +
    				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'rid', u); " +
    				"END set_role_id; " +
    				"PROCEDURE set_role_uid (u VARCHAR2) IS " +
    				"BEGIN " +
    				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'ruid', u); " +
    				"END set_role_uid; " +
    				"PROCEDURE set_role_name (u VARCHAR2) IS " +
    				"BEGIN " +
    				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'rname', u); " +
    				"END set_role_name; " +
    				"PROCEDURE set_balans_id (u NUMBER) IS " +
    				"BEGIN " +
    				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'bid', u); " +
    				"END set_balans_id; " +
    				"PROCEDURE set_balans_uid (u VARCHAR2) IS " +
    				"BEGIN " +
    				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'buid', u); " +
    				"END set_balans_uid; " +
    				"PROCEDURE set_balans_name (u VARCHAR2) IS " +
    				"BEGIN " +
    				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'bname', u); " +
    				"END set_balans_name; " +
    				"END hr_sec_ctx;";

    		st = conn.createStatement();
    		st.executeUpdate(call);
    		st.close();
    		
    		//commit();

    		Statement st1 = conn.createStatement();
    		try {
    			st1.executeUpdate("DROP CONTEXT " + db.getSchemeName() + "_hr_ctx");
        		st1.close();
    		} catch (Exception e) {
    			log.error(e, e);
    		}

    		st1 = conn.createStatement();
    		try {
    			st1.executeUpdate("CREATE CONTEXT " + db.getSchemeName() + "_hr_ctx USING " + db.getSchemeName() + ".hr_sec_ctx");
        		st1.close();
    		} catch (Exception e) {
    			log.error(e, e);
    		}
    		
    		st1 = conn.createStatement();
    		st1.executeUpdate("GRANT EXECUTE ON " + db.getSchemeName() + ".hr_sec_ctx TO public");
    		st1.close();
    		//commit();

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
            setId("version", version);
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
            setId("version", version);
            commit();
        }
    	if (version < 42) {
            upgradeTo42();
            version = 42;
            setId("version", version);
            commit();
        }
    	if (version < 43) {
            upgradeTo43();
            version = 43;
            setId("version", version);
            commit();
        }
    	if (version < 44) {
            upgradeTo44();
            version = 44;
            setId("version", version);
            commit();
        }
    	if (version < 45) {
            upgradeTo45();
            version = 45;
            setId("version", version);
            commit();
        }
    	if (version < 46) {
            upgradeTo46();
            version = 46;
            setId("version", version);
            commit();
        }
    	if (version < 48) {
            upgradeTo48();
            version = 48;
            setId("version", version);
            commit();
        }
    	if (version < 49) {
            upgradeTo49();
            version = 49;
            setId("version", version);
            commit();
        }
    	if (version < 50) {
            upgradeTo50();
            version = 50;
            setId("version", version);
            commit();
        }
    	if (version < 51) {
            upgradeTo51();
            version = 51;
            setId("version", version);
            commit();
        }
    	if (version < 52) {
            upgradeTo52();
            version = 52;
            setId("version", version);
            commit();
        }
    	if (version < 53) {
            upgradeTo53();
            version = 53;
            setId("version", version);
            commit();
        }
    	if (version < 54) {
            upgradeTo54();
            version = 54;
            setId("version", version);
            commit();
        }
    	if (version < 55) {
            upgradeTo55();
            version = 55;
            setId("version", version);
            commit();
        }
    	if (version < 56) {
            upgradeTo56();
            version = 56;
            setId("version", version);
            commit();
        }
    	if (version < 57) {
            upgradeTo57();
            version = 57;
            setId("version", version);
            commit();
        }
    	if (version < 58) {
            upgradeTo58();
            version = 58;
            setId("version", version);
            commit();
        }
    	if (version < 59) {
            upgradeTo59();
            version = 59;
            setId("version", version);
            commit();
        }
    	if (version < 60) {
    		upgradeTo60();
    		version = 60;
            setId("version", version);
            commit();
    	}
    	if (version < 61) {
    		upgradeTo61();
    		version = 61;
            setId("version", version);
            commit();
    	}
    	if (version < 62){
    		upgradeTo62();
    		version = 62;
            setId("version", version);
            commit();
    	}
    	if (version < 63) {
    		upgradeTo63();
    		version = 63;
            setId("version", version);
            commit();
    	}  
    	if (version < 64) {
    		upgradeTo64();
    		version = 64;
            setId("version", version);
            commit();
    	}
    	if (version < 65) {
    		upgradeTo65();
    		version = 65;
            setId("version", version);
            commit();
    	}
    	if (version < 66) {
    		upgradeTo66();
    		version = 66;
            setId("version", version);
            commit();
    	}
    	if (version < 67) {
    		upgradeTo67();
    		version = 67;
            setId("version", version);
            commit();
    	}
    	if (version < 68) {
    		upgradeTo68();
    		version = 68;
            setId("version", version);
            commit();
    	}
    	if (version < 69) {
    		upgradeTo69();
    		version = 69;
            setId("version", version);
            commit();
    	}
    	if (version < 70) {
    		upgradeTo70();
    		version = 70;
            setId("version", version);
            commit();
    	}
    	if (version < 71) {
    		upgradeTo71();
    		version = 71;
            setId("version", version);
            commit();
    	}
    	if (version < 72) {
    		upgradeTo72();
    		version = 72;
            setId("version", version);
            commit();
    	}
    	if (version < 73) {
    		upgradeTo73();
    		version = 73;
            setId("version", version);
            commit();
    	}
    	if (version < 74) {
    		upgradeTo74();
    		version = 74;
            setId("version", version);
            commit();
    	}
    	if (version < 75) {
    		upgradeTo75();
    		version = 75;
    		setId("version", version);
            commit();
    	}
    	if (version < 76) {
    		upgradeTo76();
    		version = 76;
    		setId("version", version);
            commit();
    	}
    	if (version < 77) {
    		upgradeTo77();
    		version = 77;
    		setId("version", version);
            commit();
    	}
    	return version;
	}
	
	private String getConstraintName(PreparedStatement pst, String name)
			throws SQLException {
		String res = null;
		ResultSet rs = null;
		try {
		pst.setString(1, name);
			rs = pst.executeQuery();
		if (rs.next()) {
			res = getString(rs, 1);
		}
		} finally {
			DbUtils.closeQuietly(rs);
		}
		return res;
	}

	@Override
	protected DriverException convertException(SQLException e) {
		if (e.getErrorCode() == 60
				|| (e.getNextException() != null && e.getNextException()
						.getErrorCode() == 60)
                || e.getMessage().toLowerCase(Constants.OK).contains("deadlock"))
			return new DriverException(e.getMessage(),
					ErrorCodes.ER_LOCK_DEADLOCK);
		else {
			log.info("ErrorCode=" + e.getErrorCode());
			return new DriverException(e);
		}
	}

	@Override
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
	        		fromSql.append(ptname).append(" ").append(ptalias);
	        		whereSql.append(ptalias).append(".c_tr_id=(SELECT /*+ no_unnest */ MAX(c_tr_id) FROM ").append(ptname)
	        				.append(" WHERE c_obj_id=").append(ptalias).append(".c_obj_id AND c_tr_id IN (0,?))");
	        		res++;
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
		        		fromSql.append(ptname).append(" ").append(ptalias);
		        		whereSql.append(ptalias).append(".c_obj_id=? AND ").append(ptalias)
		        				.append(".c_tr_id=(SELECT /*+ no_unnest */ MAX(c_tr_id) FROM ").append(ptname).append(" WHERE c_obj_id=")
		        				.append(ptalias).append(".c_obj_id AND c_tr_id IN (0,?))");
		        		res++;
		        	} else if (tid == -1) {
		        		fromSql.append(
		        				"(SELECT c_obj_id,MAX(c_tr_id) AS c_tr_id FROM ").append(ptname).append(" WHERE c_obj_id=? GROUP BY c_obj_id) ").append(pmalias)
		        				.append(" INNER JOIN ").append(ptname).append(" ").append(ptalias).append(" ON ").append(ptalias).append(".c_obj_id=")
		        				.append(pmalias).append(".c_obj_id AND ").append(ptalias).append(".c_tr_id=").append(pmalias).append(".c_tr_id AND ")
		        				.append(ptalias).append(".c_is_del=0");
		        	} else if (tid == -2) {
		        		fromSql.append(
		        				"(SELECT c_obj_id,MAX(c_tr_id) AS c_tr_id FROM ").append(ptname).append(" WHERE c_obj_id=? AND c_tr_id>0 GROUP BY c_obj_id) ").append(pmalias)
		        				.append(" INNER JOIN ").append(ptname).append(" ").append(ptalias).append(" ON ").append(ptalias).append(".c_obj_id=")
		        				.append(pmalias).append(".c_obj_id AND ").append(ptalias).append(".c_tr_id=").append(pmalias).append(".c_tr_id AND ")
		        				.append(ptalias).append(".c_is_del=0");
		        	} else {
		        		fromSql.append(ptname).append(" ").append(ptalias);
						whereSql.append(ptalias).append(".c_obj_id=? AND ").append(ptalias).append(".c_tr_id=0");
		        	}
					addedClsIds.add(ca.classId);
				}
				// Добавляем таблицу в FROM если она уже не добавлена
				if (!addedClsIds.contains(ca.classId)) {
					addedClsIds.add(ca.classId);
					fromSql.append(" LEFT JOIN ").append(tname).append(" ").append(talias).append(" ON ").append(talias).append(".c_obj_id=")
						.append(ptalias).append(".c_obj_id AND ").append(talias).append(".c_tr_id=").append(ptalias).append(".c_tr_id");
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
				String cnameAlias = (cname.length() > MAX_OBJECT_NAME_LENGTH - ptalias.length() - 1) 
						? cname.substring(0, MAX_OBJECT_NAME_LENGTH - ptalias.length() - 1)
						: cname;
				selectSql.append(",").append(ptalias).append(".").append(cname).append(" AS ").append(ptalias).append("_").append(cnameAlias);
				attrs.add(new Pair<KrnAttribute, Integer>(a, cindex[0]++));
			} else {
				String contains = "";
				for (Pair<KrnAttribute, Integer> pair : attrs) {
					if (pair.first.id == a.id) {
						contains = String.valueOf(tindex[0]++);
						break;
					}
				}
				String vt = new StringBuilder(parentAlias).append(contains).append("_").append(a.id).append("_").append(a.typeClassId).toString();
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
					String vtname = getClassTableName(a.typeClassId);
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
			        				.append(rtalias).append(".").append(cname).append(" AND ").append(rtalias).append(".c_del=0")
			        				.append(" AND ").append(rtalias).append(".c_tr_id IN (0,?)");
        					res += 1000;

		        			
			        		if (whereSql.length() > 0)
			        			whereSql.append(" AND ");
			        		whereSql.append("(").append(rtalias).append(".c_tr_id IS null OR ").append(rtalias)
			        				.append(".c_tr_id=(SELECT MAX(c_tr_id) FROM ").append(rtname).append(" WHERE ").append(cname).append("=").append(rtalias).append(".").append(cname)
			        				.append(" AND c_tr_id IN (0,?)))");
		        			res++;
		        			fromSql.append(
			        				" LEFT JOIN ").append(vtname).append(" ").append(vtalias).append(" ON ").append(vtalias).append(".c_obj_id=")
			        				.append(rtalias).append(".c_obj_id AND ").append(vtalias).append(".c_is_del=0")
	        						.append(" AND ").append(vtalias).append(".c_tr_id IN (0,?)");
	        				res += 1000;

			        				
			        		whereSql.append(" AND (").append(vtalias).append(".c_tr_id IS null OR ").append(vtalias)
			        				.append(".c_tr_id=(SELECT MAX(c_tr_id) FROM ").append(vtname).append(" WHERE c_obj_id=")
			        				.append(vtalias).append(".c_obj_id AND c_tr_id IN (0,?)))");
		        			res++;
		        		} else if (ra != null && a.typeClassId != ra.classId) {
		        			KrnClass c = db.getClassById(a.typeClassId);
		        			
		        			fromSql.append(
			        				" LEFT JOIN ").append(vtname).append(" ").append(vtalias).append(" ON ").append(a.rAttrId > 0 ? ptalias : vtalias).append(".c_obj_id=")
			        				.append(a.rAttrId > 0 ? vtalias : ptalias).append(".").append(cname).append(" AND ").append(vtalias).append(".c_del=0")
	        						.append(" AND ").append(vtalias).append(".c_tr_id IN (0,?)");
	        				res += 1000;

			        		if (whereSql.length() > 0)
			        			whereSql.append(" AND ");
			        		whereSql.append("(").append(vtalias).append(".c_tr_id IS null OR ").append(vtalias)
			        				.append(".c_tr_id=(SELECT MAX(c_tr_id) FROM ").append(vtname).append(" WHERE ").append(cname).append("=").append(vtalias).append(".").append(cname)
			        				.append(" AND c_tr_id IN (0,?)))");
		        			res++;

	    					whereSql.append(" AND ").append(vtalias).append(".c_class_id=").append(a.typeClassId);

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
			        				.append(a.rAttrId > 0 ? vtalias : ptalias).append(".").append(cname).append(" AND ").append(vtalias).append(".c_is_del=0")
			        				.append(" AND ").append(vtalias).append(".c_tr_id IN (0,?)");
			        		res += 1000;
			        		
			        		if (whereSql.length() > 0)
			        			whereSql.append(" AND ");
			        		whereSql.append("(").append(vtalias).append(".c_tr_id IS null OR ").append(vtalias)
			        				.append(".c_tr_id=(SELECT MAX(c_tr_id) FROM ").append(vtname).append(" WHERE c_obj_id=")
			        				.append(vtalias).append(".c_obj_id AND c_tr_id IN (0,?)))");
			        		res++;
		        		}
		        	} else if (tid == -1) {
/*		        		fromSql.append(" LEFT JOIN (SELECT c_obj_id,");
		        		
		        		if (a.rAttrId > 0) fromSql.append(cname).append(",");

		        		fromSql.append("MAX(c_tr_id) AS c_tr_id FROM ").append(vtname).append(" GROUP BY c_obj_id").append(a.rAttrId > 0?","+cname:"").append(") ").append(mtalias)
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
		        				"MAX(c_tr_id) AS c_tr_id FROM ").append(vtname).append(" WHERE c_tr_id>0 GROUP BY c_obj_id").append(a.rAttrId > 0?","+cname:"").append(") ").append(mtalias)
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
	@Override
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
      PreparedStatement pst = null;
      StringBuilder sql = null;
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
                        .append(" vt ON vt.c_obj_id=rat.c_obj_id")
                        .append(attr.typeClassId==99?"":" AND vt.c_tr_id=rat.c_tr_id");
                } else {
                	  // одиночный обратный атрибут
                    if (rattr.classId != attr.typeClassId) {
                        from.append(getClassTableName(rattr.classId))
                            .append(" rt INNER JOIN ")
                            .append(getClassTableName(attr.typeClassId))
                            .append(" vt ON vt.c_obj_id=rt.c_obj_id")
                            .append(attr.typeClassId==99?"":" AND vt.c_tr_id=rt.c_tr_id");
                    } else {
                        from.append(getClassTableName(rattr.classId))
	                        .append(" vt");
                    }
                }
                if (attr.sAttrId != 0) {
                    KrnAttribute sattr = db.getAttributeById(attr.sAttrId);
                    String scmName = sattr.isMultilingual ? (langId > 0 ? getColumnName(sattr, langId) : getColumnName(sattr, 1)) : getColumnName(sattr);
                    String orderPrefix = null;
                    if (sattr.classId == attr.typeClassId) {
                  	  orderPrefix = "vt.";
                    } else if (sattr.classId == rattr.classId) {
                  	  orderPrefix = "rt.";
                    } else {
                        from.append(" LEFT JOIN ")
                            .append(getClassTableName(sattr.classId))
                            .append(" st ON st.c_obj_id=vt.c_obj_id")
                            .append(attr.typeClassId==99?"":" AND st.c_tr_id=vt.c_tr_id");
                        orderPrefix = "st.";
                    }
                    select.append(",").append(orderPrefix).append(scmName);

                    order.append(orderPrefix).append(scmName);
                    if (attr.sDesc) {
                        order.append(" DESC NULLS LAST");
                    }
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
                    from.append(attr.typeClassId == 99 ? "" : " AND vt.c_tr_id=0");
                } else if (tid != -1 && attr.typeClassId != 99) {
                    from.append(" AND vt.c_tr_id IN (0,?)");
                    ltcnt++;
                }
            }
        }
        final SortedSet<Value> res = new TreeSet<Value>();
        List<Value> values = new ArrayList<Value>();
        sql = new StringBuilder("SELECT ").append(valTbl?"DISTINCT ":"").append(select).
        		append(" FROM ").append(from).append(" WHERE ").append(where);
        if (order.length() > 0) {
            sql.append(" ORDER BY ").append(order);
        }
        if (limit[0] > 0)
        	sql = addLimit(sql, limit[0], 0);

        // Map для хранения обработанных строк
        Map<Long, Value> revValues = new HashMap<Long, Value>();
        
        ltcnt++;
        pst = conn.prepareStatement(sql.toString());
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
                if(rs.wasNull()){
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
              	  if (!(objectId != objId && del != 0)) {
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
            	if (attr.sAttrId == 0 && !isPrimary(type)) {
            		Collections.sort(values, new Comparator<Value>() {
						@Override
						public int compare(Value o1, Value o2) {
							long id1 = o1.value != null ? ((KrnObject)o1.value).id : 0;
							long id2 = o1.value != null ? ((KrnObject)o2.value).id : 0;
							return id1 < id2 ? -1 : id1 > id2 ? 1 : 0;
						}
					});
            	}
          	  for (int i = 0; i < values.size(); i++) {
          		  values.get(i).index = i;
          	  }
            }
            res.addAll(values);
        }
        return res;
      } catch (SQLException e) {
    	  try {
    		  log.error("ERROR INFO:(" + pst.getParameterMetaData().toString() + ") SQL:" + sql);
    	  } catch (Exception e1) {log.error(e1, e1);}
    	  throw convertException(e);
      } finally {
    	  DbUtils.closeQuietly(pst);
      }
  }
	
	//Создание информации об индексе в БД
	@Override
	public KrnIndex createIndexInfo(String uid,long classId) throws DriverException{		
		if(uid == null)
			uid = UUID.randomUUID().toString();
		try{
			long id = getNextId("seq_indexes");
			String sql = "INSERT INTO "+getDBPrefix()+"t_indexes(c_id,c_uid,c_class_id,c_is_multilingual) VALUES (?,?,?,0)";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setLong(1, id);
			pst.setString(2, uid);
			pst.setLong(3, classId);
			pst.executeUpdate();
			pst.close();			
			
			
			KrnIndex krnIndex = new KrnIndex(id,classId,uid);
			return krnIndex;
		}catch(SQLException e){
			throw convertException(e);
		}
	}
		
	protected void createLocksTable(Connection conn) throws SQLException {
		Statement st = conn.createStatement();
		// Таблица блокировок объектов
		st.executeUpdate(
				"CREATE TABLE "+getDBPrefix()+"t_locks (" +
				"c_obj_id INTEGER NOT NULL," +
				"c_locker_id INTEGER NOT NULL," +
				"c_flow_id INTEGER," +
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
		if (!isTableExists("t_lock_methods")) {
			Statement st = conn.createStatement();
			// Таблица блокировок объектов
			st.executeUpdate(
					"CREATE TABLE "+getDBPrefix()+"t_lock_methods (" +
					"c_muid CHAR(36) NOT NULL," +
					"c_flow_id INTEGER," +
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
	}
	@Override	
	protected void createIndexTables(Connection conn) throws SQLException{
		Statement st = conn.createStatement();
		String sql = "";
		
		sql =
			"CREATE TABLE "+getDBPrefix()+"t_indexes(" +
			" c_id NUMBER NOT NULL," +
			" c_uid VARCHAR2(36) NOT NULL," +
			" c_class_id NUMBER NOT NULL," +
			" c_is_multilingual NUMBER(1) NOT NULL," +			
			" PRIMARY KEY(c_id)," +
			" FOREIGN KEY(c_class_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE" +
			")" +
			"";
		st.executeUpdate(sql);
					
		st.executeUpdate("CREATE SEQUENCE " + getDBPrefix() + "seq_indexes START WITH 1");	
		
		sql =
			"CREATE TABLE "+getDBPrefix()+"t_indexkeys(" +
			" c_index_id NUMBER NOT NULL," +
			" c_attr_id NUMBER NOT NULL," +
			" c_keyno NUMBER NOT NULL," +
			" c_is_desc NUMBER(1) NOT NULL," +
			" FOREIGN KEY(c_index_id) REFERENCES "+getDBPrefix()+"t_indexes(c_id) ON DELETE CASCADE," +
			" FOREIGN KEY(c_attr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id) ON DELETE CASCADE" +
			")" +			
			"";
		st.executeUpdate(sql);
		
		st.close();
	}
	
	//Удаление индекса
	@Override
	protected String dropIndex(String indexName,String tableName) throws SQLException{
		return "DROP INDEX " + indexName;		
	}
		
	@Override
	protected StringBuilder addLimit(StringBuilder sql, int limit, int offset) {
		return sql.insert(0, "SELECT * FROM (").append(") WHERE ROWNUM<=").append(limit); 
	}

	@Override
	protected StringBuilder byPage(StringBuilder sql){
    	return sql.insert(0,"SELECT * FROM (SELECT m1.*, ROWNUM rn FROM (").append(") m1 WHERE ROWNUM <= ?) WHERE rn >= ?");
	}
	protected void setPageValue(PreparedStatement pst,int i, int beginRow, int endRow) throws SQLException {
		pst.setLong(i, endRow);
		pst.setLong(i+1, beginRow);
	}
	
	@Override
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
			int vtcnt = processAttrRequest(objIds == null ? classId : 0, req, tid, "ct99", -1, attrs, selectSql, fromSql, whereSql, tindex, cindex, aliases, trAliases);
	        if (selectSql.length() > 0 && fromSql.length() == 0) {
	            // Выбираются отдельные объекты.
	            // Основная таблица еще не задана, берем таблицу из класса
	            // первого объекта
	            KrnObject obj = getObjectById(objIds[0]);
	            String ptname = getClassTableName(obj.classId);
	            String ptalias = getShortAlias(ptname + "_" + obj.classId, aliases, tindex);
	            String pmalias = getShortAlias(ptname + "m", aliases, tindex);
	            selectSql.append(ptalias + ".c_obj_id AS " + ptalias + "_id,");
	            selectSql.append(ptalias + ".c_uid AS " + ptalias + "_uid,");
	            selectSql.append(ptalias + ".c_class_id AS " + ptalias + "_cid,");
	            selectSql.append(ptalias + ".c_tr_id AS " + ptalias + "_tid");
	            if (tid > 0) {
	                    fromSql.append(ptname + " " + ptalias);
	                    whereSql.append(
	                                    ptalias + ".c_obj_id=? AND " + ptalias + ".c_tr_id=(SELECT /*+ no_unnest */ MAX(c_tr_id) FROM " + ptname + " WHERE c_obj_id=" + ptalias + ".c_obj_id AND c_tr_id IN (0,?))");
	                    vtcnt++;
	            } else if (tid == -1) {
	                    fromSql.append(
	                                    "(SELECT c_obj_id,MAX(c_tr_id) AS c_tr_id FROM " + ptname + " WHERE c_obj_id=? GROUP BY c_obj_id) " + pmalias +
	                                    " INNER JOIN " + ptname + " " + ptalias + " ON " + ptalias + ".c_obj_id=" + pmalias + ".c_obj_id" + " AND " +  ptalias + ".c_tr_id=" + pmalias + ".c_tr_id AND " + ptalias + ".c_is_del=0");
	            } else if (tid == -2) {
	                    fromSql.append(
	                                    "(SELECT c_obj_id,MAX(c_tr_id) AS c_tr_id FROM " + ptname + " WHERE c_obj_id=? AND c_tr_id>0 GROUP BY c_obj_id) " + pmalias +
	                                    " INNER JOIN " + ptname + " " + ptalias + " ON " + ptalias + ".c_obj_id=" + pmalias + ".c_obj_id" + " AND " +  ptalias + ".c_tr_id=" + pmalias + ".c_tr_id AND " + ptalias + ".c_is_del=0");
	            } else {
	                    fromSql.append(ptname + " " + ptalias);
                        whereSql.append(ptalias + ".c_obj_id=? AND " + ptalias + ".c_tr_id=0");
	            }
	        }
	        
			StringBuilder sql = new StringBuilder();
			if (selectSql.length() > 0) {
				sql.append("SELECT ");
				if (objIds == null && lim > 0)
					sql.append("/*+first_rows*/ ");
	
				sql.append(selectSql).append(" FROM ").append(fromSql);
				if (whereSql.length() > 0)
					sql.append(" WHERE ").append(whereSql);
		        if (lim > 0)
		        	sql = addLimit(sql, lim, 0);
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
			            if (rs.next())
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
			if(isLoggingGetObjSql())
				log_sql.info("GET_OBJ_SQL_INFO:(" + info + ") SQL:" + cache.sql);


	        return res;
        } catch (SQLException e) {
        	log.error("ERROR INFO:(" + info + ") SQL:" + cache.sql);
            throw convertException(e);
        } finally {
    		DbUtils.closeQuietly(pst);
        }
	}

	@Override
	public void createOrUpdatePolicy(String oldName, String oldTable, FGACRule rule) {
		String scheme = db.getSchemeName();
		String name = rule.getName();
		String table = rule.getClassName();
		String stTypes = rule.getOperations();
		
		if (oldName != null && oldName.length() > 0 && oldTable != null && oldTable.length() > 0) {
			dropPolicy(scheme, oldName, oldTable);
		}
		
		List<KrnClass> addedPolicy = new ArrayList<>();
		
		if (name != null && name.length() > 0 && table != null && table.length() > 0) {
			CallableStatement st = null;
			try {
				KrnClass cls = getClassByNameComp(table);
				
				Map<Long, String> attrsByClassId = new HashMap<Long, String>();
				
				if (rule.getAttrNames() != null && rule.getAttrNames().length() > 0) {
					String[] attrNames = rule.getAttrNames().split(",");
					for (String attrName : attrNames) {
						KrnAttribute attr = getAttributeByNameComp(cls, attrName);
						String attrsStr = attrsByClassId.get(attr.classId);

						if (attrsStr == null)
							attrsStr = getColumnNames(attr);
						else
							attrsStr += "," + getColumnNames(attr);
						
						attrsByClassId.put(attr.classId, attrsStr);
					}
				}

				String condition = (rule.getExpression() == null || rule.getExpression().length() == 0) 
						? "" 
						: rule.getExpression().replaceAll("\r|\n", "");
				condition = condition
						.replace("$USER_ID", "CXVAR(''id'')")
						.replace("$USER_UID", "CXVAR(''uid'')")
						.replace("$USER_NAME", "CXVAR(''name'')")
						.replace("$ROLE_ID", "CXVAR(''rid'')")
						.replace("$ROLE_UID", "CXVAR(''ruid'')")
						.replace("$ROLE_NAME", "CXVAR(''rname'')")
						.replace("$BALANS_ID", "CXVAR(''bid'')")
						.replace("$BALANS_UID", "CXVAR(''buid'')")
						.replace("$BALANS_NAME", "CXVAR(''bname'')");
				
				String cond = "c_class_id<>" + cls.id;
				if (condition.trim().length() > 0)
					condition = "(" + condition + ") or " + cond;
				else
					condition = cond;
					
				while (cls != null) {
					if (!cls.isVirtual()) {
						addedPolicy.add(cls);
						
						String tableName = getClassTableName(cls, false);
						String attrsStr = attrsByClassId.get(cls.id);

						PreparedStatement st2 = null;
						try {
							String sql = "CREATE OR REPLACE FUNCTION RULE_" + name + "_" + cls.id + " (D1 VARCHAR2, D2 VARCHAR2) RETURN VARCHAR2 IS" 
									+ " RET_CONDITION VARCHAR2 (2000);"
									+ " BEGIN"
									+ " RET_CONDITION := '" + condition + "';"
									+ " RETURN RET_CONDITION;"    
									+ " END RULE_" + name + "_" + cls.id + ";";
							
							log.info("=================== CREATING FUNCTION =============================");
							log.info(sql);
							st2 = conn.prepareStatement(sql);
							st2.executeUpdate();
							log.info("----------------- END CREATING FUNCTION ---------------------------");
						} catch (Exception e) {
							log.error(e, e);
						} finally {
							DbUtils.closeQuietly(st2);
						}

						try {
							String sql = "{call DBMS_RLS.ADD_POLICY (object_schema=>?, object_name=>?, policy_name=>?, function_schema=>?, policy_function=>?";
							if (stTypes != null && stTypes.length() > 0)
								sql += ", statement_types=>?";
							
							if (attrsStr != null)
								sql += ", sec_relevant_cols=>?";
							
							if (attrsStr != null && (stTypes == null || stTypes.length() == 0 || stTypes.equalsIgnoreCase("select")))
								sql += ", sec_relevant_cols_opt => DBMS_RLS.ALL_ROWS";

							if (stTypes == null || stTypes.length() == 0 || stTypes.toLowerCase(Constants.OK).contains("insert") || stTypes.toLowerCase(Constants.OK).contains("update"))
								sql += ", update_check => true";

							sql += ")}";
							
							log.info("=================== ADDING POLICY =============================");
							log.info(sql);
							log.info("scheme = " + scheme);
							log.info("table = " + tableName);
							log.info("name = " + name);
							log.info("function = " + "RULE_" + name + "_" + cls.id);
							log.info("statement_types = " + stTypes);
							log.info("cols = " + attrsStr);

							st = conn.prepareCall(sql);
							
							st.setString(1, scheme);
							st.setString(2, tableName);
							st.setString(3, name);
							st.setString(4, scheme);
							st.setString(5, "RULE_" + name + "_" + cls.id);
							int k = 6;
							if (stTypes != null && stTypes.length() > 0)
								st.setString(k++, stTypes);
							
							if (attrsStr != null)
								st.setString(k++, attrsStr);
							
							st.executeUpdate();
							log.info("----------------- END ADDING POLICY ---------------------------");
						} catch (Exception e) {
							log.error(e, e);
						} finally {
							DbUtils.closeQuietly(st);
						}
					}
					cls = cls.parentId > -1 ? getClassByIdComp(cls.parentId) : null;
				}
			} catch (Exception e) {
				log.error(e, e);
			} finally {
				DbUtils.closeQuietly(st);
			}
			
			try {
				for (KrnClass cls : addedPolicy) {
					st = null;
					try {
						String tableName = getClassTableName(cls, false);
						String sql = "{call DBMS_RLS.ENABLE_POLICY (?, ?, ?, " + !rule.isBlocked() +")}";

						log.info("=================== ENABLING POLICY =============================");
						log.info(sql);
						log.info("scheme = " + scheme);
						log.info("table = " + tableName);
						log.info("name = " + name);
						
						st = conn.prepareCall(sql);
						st.setString(1, scheme);
						st.setString(2, tableName);
						st.setString(3, name);
						st.executeUpdate();
						log.info("----------------- END ENABLING POLICY ---------------------------");
					} catch (Exception e) {
						log.error(e, e);
					} finally {
						DbUtils.closeQuietly(st);
					}
				}
			} catch (Exception e) {
				log.error(e, e);
			}
		}
	}
	
	public void enablePolicy(FGACRule rule, boolean enable, UserSession us) {
		String scheme = db.getSchemeName();
		String name = rule.getName();
		String table = rule.getClassName();

		try {
			KrnClass cls = getClassByNameComp(table);
			while (cls != null) {
				if (!cls.isVirtual()) {
					CallableStatement st = null;
					String tableName = null;
					try {
						tableName = getClassTableName(cls, false);
						String sql = "{call DBMS_RLS.ENABLE_POLICY (?, ?, ?, " + enable +")}";
	
				        log.info((enable ? "Разблокировка" : "Блокировка") + " правила FGAC. Пользователь:" + us.getUserName() + " IP: " + us.getComputer());
				        log.info((enable ? "Разблокировка" : "Блокировка") + " правила FGAC. Наименование:" + name);
						st = conn.prepareCall(sql);
						st.setString(1, scheme);
						st.setString(2, tableName);
						st.setString(3, name);
						st.executeUpdate();
				        log.info("Окончание " + (enable ? "разблокировки" : "блокировки") + " правила FGAC. Наименование:" + name);
					} catch (Exception e) {
				        log.warn("Правило FGAC не было создано ранее для таблицы " + tableName + ". Наименование:" + name);
					} finally {
						DbUtils.closeQuietly(st);
					}
				}
				cls = cls.parentId > -1 ? getClassByIdComp(cls.parentId) : null;
			}
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	@Override
	public void createOrUpdatePolicy(String oldName, String oldTable, FGARule rule) {
		String scheme = db.getSchemeName();
		String name = rule.getName();
		String table = rule.getClassName();
		String stTypes = rule.getOperations();
		
		if (oldName != null && oldName.length() > 0 && oldTable != null && oldTable.length() > 0) {
			dropFGAPolicy(scheme, oldName, oldTable);
		}
		
		List<KrnClass> addedPolicy = new ArrayList<>();
		
		if (name != null && name.length() > 0 && table != null && table.length() > 0) {
			CallableStatement st = null;
			try {
				KrnClass cls = getClassByNameComp(table);
				
				Map<Long, String> attrsByClassId = new HashMap<Long, String>();
				
				if (rule.getAttrNames() != null && rule.getAttrNames().length() > 0) {
					String[] attrNames = rule.getAttrNames().split(",");
					for (String attrName : attrNames) {
						KrnAttribute attr = getAttributeByNameComp(cls, attrName);
						String attrsStr = attrsByClassId.get(attr.classId);

						if (attrsStr == null)
							attrsStr = getColumnNames(attr);
						else
							attrsStr += "," + getColumnNames(attr);
						
						attrsByClassId.put(attr.classId, attrsStr);
					}
				}

				String condition = (rule.getExpression() == null || rule.getExpression().length() == 0) 
						? "" 
						: rule.getExpression().replaceAll("\r|\n", "");
				condition = condition
						.replace("$USER_ID", "CXVAR(''id'')")
						.replace("$USER_UID", "CXVAR(''uid'')")
						.replace("$USER_NAME", "CXVAR(''name'')")
						.replace("$ROLE_ID", "CXVAR(''rid'')")
						.replace("$ROLE_UID", "CXVAR(''ruid'')")
						.replace("$ROLE_NAME", "CXVAR(''rname'')")
						.replace("$BALANS_ID", "CXVAR(''bid'')")
						.replace("$BALANS_UID", "CXVAR(''buid'')")
						.replace("$BALANS_NAME", "CXVAR(''bname'')");
				
				String cond = "c_class_id=" + cls.id;
				if (condition.trim().length() > 0)
					condition = "(" + condition + ") and " + cond;
				else
					condition = cond;
					
				while (cls != null) {
					if (!cls.isVirtual()) {
						addedPolicy.add(cls);
						
						String tableName = getClassTableName(cls, false);
						String attrsStr = attrsByClassId.get(cls.id);

						try {
							String sql = "{call DBMS_FGA.ADD_POLICY (object_schema=>?, object_name=>?, policy_name=>?, audit_condition=>?";
							if (stTypes != null && stTypes.length() > 0)
								sql += ", statement_types=>?";
							
							if (attrsStr != null)
								sql += ", audit_column=>?, audit_column_opts => DBMS_FGA.ALL_COLUMNS";
							
							sql += ")}";
							
							log.info("=================== ADDING POLICY =============================");
							log.info(sql);
							log.info("scheme = " + scheme);
							log.info("table = " + tableName);
							log.info("name = " + name);
							log.info("condition = " + condition);
							log.info("statement_types = " + stTypes);
							log.info("cols = " + attrsStr);

							st = conn.prepareCall(sql);
							
							st.setString(1, scheme);
							st.setString(2, tableName);
							st.setString(3, name);
							st.setString(4, condition);
							int k = 5;
							if (stTypes != null && stTypes.length() > 0)
								st.setString(k++, stTypes);
							if (attrsStr != null)
								st.setString(k++, attrsStr);
							
							st.executeUpdate();
							log.info("----------------- END ADDING POLICY ---------------------------");
						} catch (Exception e) {
							log.error(e, e);
						} finally {
							DbUtils.closeQuietly(st);
						}
					}
					cls = cls.parentId > -1 ? getClassByIdComp(cls.parentId) : null;
				}
			} catch (Exception e) {
				log.error(e, e);
			} finally {
				DbUtils.closeQuietly(st);
			}
			
			try {
				for (KrnClass cls : addedPolicy) {
					st = null;
					try {
						String tableName = getClassTableName(cls, false);
						String sql = "{call DBMS_FGA.ENABLE_POLICY (object_schema => ?, object_name => ?, policy_name => ?, enable => " + !rule.isBlocked() +")}";

						log.info("=================== ENABLING POLICY =============================");
						log.info(sql);
						log.info("scheme = " + scheme);
						log.info("table = " + tableName);
						log.info("name = " + name);
						
						st = conn.prepareCall(sql);
						st.setString(1, scheme);
						st.setString(2, tableName);
						st.setString(3, name);
						st.executeUpdate();
						log.info("----------------- END ENABLING POLICY ---------------------------");
					} catch (Exception e) {
						log.error(e, e);
					} finally {
						DbUtils.closeQuietly(st);
					}
				}
			} catch (Exception e) {
				log.error(e, e);
			}
		}
	}

	@Override
	public void dropPolicy(FGACRule rule) {
		String scheme = db.getSchemeName();
		String name = rule.getName();
		String table = rule.getClassName();
		
		dropPolicy(scheme, name, table);
	}
	
	public void dropPolicy(String scheme, String name, String table) {
		try {
			if (name != null && name.length() > 0 && table != null && table.length() > 0) {
				KrnClass cls = getClassByNameComp(table);
				while (cls != null) {
					if (!cls.isVirtual()) {
						CallableStatement st = null;
						String tableName = null;
						try {
							tableName = getClassTableName(cls, false);
					        log.info("Удаление правила FGAC. Наименование: " + name + ". Пользователь:" + us.getUserName() + " IP: " + us.getComputer());

							String sql = "{call DBMS_RLS.DROP_POLICY (?, ?, ?)}";
							log.info("=================== DROPING POLICY =============================");
							log.info(sql);
							log.info("scheme = " + scheme);
							log.info("table = " + tableName);
							log.info("name = " + name);
	
							st = conn.prepareCall(sql);
							st.setString(1, scheme);
							st.setString(2, tableName);
							st.setString(3, name);
							st.executeUpdate();
							log.info("----------------- END DROPING POLICY ---------------------------");
						} catch (Exception e) {
					        log.warn("Правило FGAC не было создано ранее для таблицы " + tableName + ". Наименование:" + name);
						} finally {
							DbUtils.closeQuietly(st);
						}
					}
					cls = cls.parentId > -1 ? getClassByIdComp(cls.parentId) : null;
				}
			}
		} catch (Exception e) {
			log.error(e, e);
		}
	}
	
	@Override
	public void dropPolicy(FGARule rule) {
		String scheme = db.getSchemeName();
		String name = rule.getName();
		String table = rule.getClassName();
		
		dropFGAPolicy(scheme, name, table);
	}
	
	public void dropFGAPolicy(String scheme, String name, String table) {
		try {
			if (name != null && name.length() > 0 && table != null && table.length() > 0) {
				KrnClass cls = getClassByNameComp(table);
				while (cls != null) {
					if (!cls.isVirtual()) {
						CallableStatement st = null;
						String tableName = null;
						try {
							tableName = getClassTableName(cls, false);
					        log.info("Удаление правила FGA. Наименование: " + name + ". Пользователь:" + us.getUserName() + " IP: " + us.getComputer());

					        String sql = "{call DBMS_FGA.DROP_POLICY (object_schema => ?, object_name => ?, policy_name => ?)}";
							log.info("=================== DROPING POLICY =============================");
							log.info(sql);
							log.info("scheme = " + scheme);
							log.info("table = " + tableName);
							log.info("name = " + name);
	
							st = conn.prepareCall(sql);
							st.setString(1, scheme);
							st.setString(2, tableName);
							st.setString(3, name);
							st.executeUpdate();
							log.info("----------------- END DROPING POLICY ---------------------------");
						} catch (Exception e) {
					        log.warn("Правило FGA не было создано ранее для таблицы " + tableName + ". Наименование:" + name);
						} finally {
							DbUtils.closeQuietly(st);
						}
					}
					cls = cls.parentId > -1 ? getClassByIdComp(cls.parentId) : null;
				}
			}
		} catch (Exception e) {
			log.error(e, e);
		}
	}

	protected Connection getNewConnection() throws DriverException {
    	Connection conn_ = super.getNewConnection();
    	CallableStatement st = null;
        if(conn_!=null && isVersion(16)){
        	
        	if (us != null) {
	        	CallableStatement proc = null;
	        	String un = us.getUserName() != null ? us.getUserName() : "unknown";
	    		try {
	    			String sql_proc="CALL DBMS_SESSION.SET_IDENTIFIER(?)";
	    			proc = conn_.prepareCall(sql_proc);
	    			proc.setString(1, un);
	    		    proc.execute();
	    		} catch (SQLException e) {
	    			log.error(e, e);
	    	    } finally {
	    	    	DbUtils.closeQuietly(proc);
	    	    } 
        	}
        	
            try {
            	StringBuilder sql = new StringBuilder("BEGIN ");
            	
            	if (us != null && us.getUserObj() != null) {
            		sql.append(db.getSchemeName()).append(".hr_sec_ctx.set_user_id(").append(us.getUserObj().id).append("); ")
            		   .append(db.getSchemeName()).append(".hr_sec_ctx.set_user_uid('").append(us.getUserObj().uid).append("'); ");
            	} else {
            		sql.append(db.getSchemeName()).append(".hr_sec_ctx.set_user_id(0); ")
            		   .append(db.getSchemeName()).append(".hr_sec_ctx.set_user_uid(''); ");
            	}
            	if (us != null && us.getUserName() != null) {
            		sql.append(db.getSchemeName()).append(".hr_sec_ctx.set_user_name('").append(us.getUserName()).append("'); ");
            	} else {
            		sql.append(db.getSchemeName()).append(".hr_sec_ctx.set_user_name(''); ");
            	}
            	if (us != null && us.getBalansObj() != null) {
        			sql.append(db.getSchemeName()).append(".hr_sec_ctx.set_balans_id(").append(us.getBalansObj().id).append("); ")
        			   .append(db.getSchemeName()).append(".hr_sec_ctx.set_balans_uid('").append(us.getBalansObj().uid).append("'); ");
            	}
            	
            	sql.append("END;");
            	
            	st = conn_.prepareCall(sql.toString());
            	st.executeUpdate();
    		} catch (SQLException e) {
    			if (log == null)
    				log = LogFactory.getLog(dsName + ".DatabaseLog." + us.getLogUserName() + (UserSession.SERVER_ID != null ? ("." + UserSession.SERVER_ID) : ""));

				log.error(e, e);
				
				if (isFirstTryToConnect) {
					isFirstTryToConnect = false;
					try {
						createOracleDBContext();
					} catch (SQLException e1) {
						log.error(e1, e1);
					}
				}
				
    		} finally {
    			DbUtils.closeQuietly(st);
    		}
        }
        return conn_;
    }


	@Override
	public void setClassComment(String clsUid, String comment, boolean log) throws DriverException {
		super.setClassComment(clsUid, comment, log);
		PreparedStatement st = null;
		if (isVersion(kz.tamur.or3.util.Tname.TnameVersionBD)) {
			try {
				String sql = "select C_TNAME, C_ID from "+getDBPrefix()+"t_classes WHERE C_CUID='" + clsUid + "'";
				st = conn.prepareStatement(sql);
				//st.setString(1, clsUid);
				//log.info("SQL Query...\n " + sql);
				ResultSet rs = st.executeQuery();
				if(rs.next()){
					String tname = getSanitizedString(rs, 1);
					long id = rs.getLong(2);
					if (id > 101) {
						if (tname == null || tname.length() == 0) {
							tname = getClassTableName(id);
						}
						if (tname != null && tname.length() != 0) {
							String comment_orcl = comment.replaceAll("'", "''");
							sql = "COMMENT ON TABLE " + tname + " IS '" + comment_orcl + "'";
							QueryRunner qr = new QueryRunner(true);
							//log.info("SQL Query...\n " + sql);
							qr.update(conn, sql);
						}
					}
				}
				rs.close();
				st.close();
			} catch (SQLException e) {
				this.log.error("ERROR: Oracle Class COMMENT: " + e.getMessage());
			} finally {
				DbUtils.closeQuietly(st);
			}
		}
	}
	
	@Override
	public void setAttributeComment(String attrUid, String comment, boolean log) throws DriverException {
		PreparedStatement pstSetAttributeComment = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String sql = "";
		QueryRunner qr = new QueryRunner(true);
		String comment_orcl = comment.replaceAll("'", "''");
		try {
			pstSetAttributeComment = conn.prepareStatement("UPDATE "+getDBPrefix()+"t_attrs SET c_comment=? WHERE c_auid=?");
			setMemo(pstSetAttributeComment, 1, comment);
			pstSetAttributeComment.setString(2, attrUid);
			pstSetAttributeComment.executeUpdate();
			if (isVersion(kz.tamur.or3.util.Tname.TnameVersionBD)) {
				sql = "select C_TNAME, C_ID, C_CLASS_ID, C_COL_TYPE, C_IS_MULTILINGUAL, C_RATTR_ID from "+getDBPrefix()+"t_attrs WHERE c_auid=?";
				st = conn.prepareStatement(sql);
				st.setString(1, attrUid);
				//log.info("SQL Query...\n " + sql);
				rs = st.executeQuery();
				if(rs.next()) {
					String tname = getSanitizedString(rs, 1);
					long id = rs.getLong(2);
					KrnAttribute a = db.getAttributeById(id);
					long cls_id = rs.getLong(3);
					long C_RATTR_ID = rs.getLong(6);
					if (cls_id > 99 && C_RATTR_ID == 0) {
						String tableName = null;
						long collectionType = rs.getLong(4);
						long isMultilingual = rs.getLong(5);
						if (collectionType != 0){
							if (tname == null || tname.trim().length() == 0){
								tableName = getAttrTableName(a);
							} else {
								tableName = tname;
							}
							sql = "COMMENT ON TABLE " + tableName + " IS '" + comment_orcl + "'";
							qr = new QueryRunner(true);
						} else {
							tableName = getClassTableName(cls_id);
						}
						
						String columnName = getColumnName(a);
						if (isMultilingual != 0) {
							List<KrnObject> langs = getSystemLangs();
							for (KrnObject lang : langs) {
								sql = "COMMENT ON COLUMN " + tableName + "." + columnName + "_" + getSystemLangIndex(lang.id) + " IS '" + comment_orcl + "'";
								//log.info("SQL Query...\n " + sql);
								qr.update(conn, sql);
							}
						} else {
							sql = "COMMENT ON COLUMN " + tableName + "." + columnName + " IS '" + comment_orcl + "'";
							//log.info("SQL Query...\n " + sql);
							qr.update(conn, sql);
						}
					}
				}
			}
			if (log)
				logModelChanges(ENTITY_TYPE_ATTRIBUTE, ACTION_MODIFY, attrUid, conn);
		} catch (SQLException e) {
			//throw convertException(e);
			this.log.error("ERROR: Oracle Attribute COMMENT: " + e.getMessage());
		} finally {
			DbUtils.closeQuietly(pstSetAttributeComment);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(st);
		}
	}
	
	@Override
	public boolean renameClassTable(long id, String newName) {//TODO r tname
		boolean result = false;
		String sql;
		try {
//			sql = "SELECT * FROM user_tab_columns WHERE TABLE_NAME='" + newName + "' AND rownum <= 1";
//			PreparedStatement st = conn.prepareStatement(sql);
//			log.info("SQL Query...\n " + sql);
//			ResultSet rs = st.executeQuery();
//			if(!rs.next()){
				QueryRunner qr = new QueryRunner(true);
				String TableName = getClassTableName(id);
				sql = "ALTER TABLE " + TableName + " RENAME TO " + newName + "";
				//log.info("SQL Query...\n " + sql);
				qr.update(conn, sql);

				db.getClassById(id).tname = newName;
				
				sql = "UPDATE "+getDBPrefix()+"t_classes SET c_tname='" + newName + "' WHERE c_id=" + id + "";
				//log.info("SQL Query...\n " + sql);
				qr.update(conn, sql);
				result = true;
//			}
//			st.close();
			
		} catch (SQLException e) {
			log.error("ERROR RenameClassTable: " + e.getMessage());
			//log.error(e, e);
		}// finally {
			return result;
		//}
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
				sql = "ALTER TABLE " + TableName + " RENAME TO "+newName;
				qr.update(conn, sql);
				TableName = getDBPrefix() + "\"" + newName + "\"";
			}
			if (attr.isMultilingual) {
				List<KrnObject> langs = getSystemLangs();
				for (KrnObject lang : langs) {
					sql = "ALTER TABLE "+TableName+" rename COLUMN "+ColumnName+"_"+getSystemLangIndex(lang.id)+" to "+newName+"_"+getSystemLangIndex(lang.id);
					//log.info("SQL Query...\n " + sql);
					qr.update(conn, sql);
				}
			} else {
				sql = "ALTER TABLE "+TableName+" rename COLUMN "+ColumnName+" to "+newName;
				qr.update(conn, sql);
			}
//			sql = "select COLUMN_ID from DBA_TAB_COLUMNS where owner=(select user from dual) and table_name='"+newName+"' and rownum <= 1";
//			PreparedStatement istnane = null;
//			istnane = conn.prepareStatement(sql);
//			istnane.setString(1, TableName.toUpperCase(Constants.OK));
//			ResultSet rst = istnane.executeQuery();
//			if (rst.next()) {
				sql = "UPDATE "+getDBPrefix()+"t_attrs SET c_tname='" + newName + "' WHERE c_id=" + attr.id;
				qr.update(conn, sql);
			
				result = true;
//			}
		} catch (Exception e) {
			log.error("ERROR renameAttrTable: " + e.getMessage());
			//new DriverException("TNAME");
			return false;
		}
		
		return result;
	}
	
	@Override
	public boolean upgradeTo17(){
		boolean result = false;
		Statement st = null;
		try {
	        log.info("Апгрейд БД до версии 17 ...");

	        try {
	        	if (!isColumnExists("t_classes", "C_TNAME")) {
		        	String sql = "ALTER TABLE "+getDBPrefix()+"t_classes ADD (C_TNAME VARCHAR2(30) DEFAULT NULL ) ADD (UNIQUE (\"C_TNAME\"))";
		        	PreparedStatement pst = conn.prepareStatement(sql);
					pst.executeUpdate();
					pst.close();
	        	}
			} catch (Exception e) {
				log.error(e, e);
			}
			
			// Проверяем есть ли колонка 'C_TNAME'
			st = conn.createStatement();
			ResultSet set = st.executeQuery("select C_TNAME from "+getDBPrefix()+"t_classes where rownum=1");
			set.close();
			
			try {
	        	if (!isColumnExists("t_attrs", "C_TNAME")) {
					String sql = "ALTER TABLE "+getDBPrefix()+"t_attrs ADD (C_TNAME VARCHAR2(30) DEFAULT NULL )";// ADD (UNIQUE (\"c_class_id\",\"C_TNAME\"))";
		        	PreparedStatement pst = conn.prepareStatement(sql);
					pst.executeUpdate();
					pst.close();
	        	}
			} catch (Exception e) {
				log.error(e, e);
			}

			// Проверяем есть ли колонка 'C_TNAME'
			set = st.executeQuery("select C_TNAME from "+getDBPrefix()+"t_attrs where rownum=1");
			set.close();

			log.info("Апгрейд БД до версии "+kz.tamur.or3.util.Tname.TnameVersionBD+" успешно завершен.");
			result = true;
		} catch (Exception e1) {
			log.error(e1, e1);
			log.error("ERROR to Update DATA BASE "+kz.tamur.or3.util.Tname.TnameVersionBD+".\n" + e1.getMessage());
		} finally {
    		DbUtils.closeQuietly(st);
		}
		return result;
    }
	
	@Override
	public KrnClass changeClass(long id, long parentId, String name,
			boolean isRepl) throws DriverException {
		KrnClass result = null;
		try {
			result = super.changeClass(id, parentId, name, isRepl);
		} catch (DriverException e) {
			String msg = null;
			if (e.getMessage().indexOf("ORA-00001") != -1){
				int dot = e.getMessage().lastIndexOf(".") + 1;
				int end = e.getMessage().lastIndexOf(")");
				msg = e.getMessage().substring(dot, end);
			}
			if (msg != null && msg.length() != 0) {
				//select * from dba_ind_columns where index_name='?';
				PreparedStatement istnane = null;
				try {
					String sql = "select column_name from dba_ind_columns where index_name='" + msg + "'";
					istnane = conn.prepareStatement(sql);
					ResultSet rst = istnane.executeQuery();
					if(rst.next()) {
						String gtname = getSanitizedString(rst, 1);
						if (gtname.equalsIgnoreCase("C_NAME")){
							msg = "C_NAME";
						}
					}
					rst.close();
				} catch (SQLException e1) {
					log.error(e1.getMessage());
				} finally {
					DbUtils.closeQuietly(istnane);
				}
				
				throw new DriverException(msg);
			}
		}
		return result;
	}
	
	@Override
	public String[][] getColumnsInfo(String tableName){	
		PreparedStatement istnane = null;
		List<String[]> colL = new ArrayList<String[]>();
		try {
			String sql = "select COLUMN_NAME, DATA_TYPE from DBA_TAB_COLUMNS " +
					"where owner=(select user from dual) and table_name=? ORDER BY COLUMN_ID";
			istnane = conn.prepareStatement(sql);
			istnane.setString(1, tableName.toUpperCase(Constants.OK));
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
		try {
			String pach = "/kz/tamur/ods/oracle/TableColumnMove.sql";
			InputStream i = this.getClass().getResourceAsStream(pach);
			StringWriter writer = new StringWriter();
			IOUtils.copy(i, writer, "UTF-8");
			String tmp = writer.toString();
			StringBuffer sql = new StringBuffer(tmp);
			int Columns_sqlpos = sql.indexOf("/**in_r_columns**/");
			tmp = "";
			for (int j : cols) {
				tmp +=  ","+j;
			}
			sql.insert(Columns_sqlpos, tmp.substring(1));
			int TName_sqlpos = sql.indexOf("/**in_TABLE_NAME**/");
			sql.insert(TName_sqlpos, getDBPrefix() + "'"+tableName.toUpperCase(Constants.OK)+"'");
			
			QueryRunner qr = new QueryRunner(true);
			qr.update(conn, sql.toString());
			
			conn.commit();
			return true;
		} catch (Exception e) {
			log.error(e, e);
		}
		return false;
	}

	public void upgradeTo22() throws SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 22 ...");
        log.info("В апгрейде у таблицы t_attrs удален уникальный индекс c_tname");

    	Statement st = conn.createStatement();
        String sql = "ALTER TABLE "+getDBPrefix()+"t_classes MODIFY (C_TNAME VARCHAR2(30) DEFAULT NULL )";
		st.executeUpdate(sql);
		
		sql = "ALTER TABLE "+getDBPrefix()+"t_attrs MODIFY (C_TNAME VARCHAR2(30) DEFAULT NULL)";
		st.executeUpdate(sql);

		try {
			sql = "ALTER TABLE "+getDBPrefix()+"t_attrs DROP UNIQUE (C_TNAME)";
			st.executeUpdate(sql);
		} catch (Throwable e) {
		} finally {
		}

		st.close();
		log.info("Апгрейд БД до версии 22 успешно завершен.");
		isUpgrading = false;
    }
	
	public void upgradeTo24() throws SQLException, DriverException {
        isUpgrading = true;
        
        log.info("Апгрейд БД до версии 24 ...");
        log.info("В таблице t_changes добавлено поле c_object_uid.");
		
        String sql = "SELECT COUNT(*) FROM user_tab_columns WHERE table_name='T_CHANGES' AND column_name='C_OBJECT_UID'";
		PreparedStatement pst = conn.prepareStatement(sql);
		ResultSet set = pst.executeQuery();
		boolean isExist = false;
		if (set.next()) {
			isExist = set.getInt(1) > 0;
		}
		set.close();
		pst.close();
		
		if (!isExist) {
			QueryRunner qr = new QueryRunner(true);
			sql = "ALTER TABLE " + getDBPrefix() + "t_changes ADD (c_object_uid VARCHAR2(20) DEFAULT NULL)";
			qr.update(conn, sql);
		}
        
		log.info("Апгрейд БД до версии 24 успешно завершен.");
		
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
	        String cname_ = attr.isMultilingual ? cname + "_1" : cname;
	        String atname=getAttrTableName(attr);
	        QueryRunner qr = new QueryRunner(true);
	    	String sql = "ALTER TABLE " + tname+" ADD ("+cname+" CLOB DEFAULT NULL)";
	    	qr.update(conn, sql);
	    	
	    	//Переписываем в него содержимое множественного атрибута
	    	
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
/*	    	sql="UPDATE "+tname+" t1 "
	    			+"SET "+cname+" = (SELECT LISTAGG(t2."+cname+",';') "
	    			+"WITHIN GROUP (ORDER BY t2."+cname+") \"cutobj\"  "
	    			+"FROM "+atname+" t2 where t1.c_obj_id=t2.c_obj_id "
	    			+"AND t2."+cname+" is not null GROUP BY t2.c_obj_id) "
	    			+"WHERE EXISTS (SELECT 1  FROM "+atname+" t2 WHERE t1.c_obj_id = t2.c_obj_id)";
	    	qr.update(conn, sql);
	*/
	    	//Удаляем таблицу относящуюся к множественному атрибуту, а его делаем одиночным и типом мемо
	    	sql = "DROP TABLE " + atname;
	    	qr.update(conn, sql);
	    	sql = "UPDATE " + getDBPrefix() + "t_attrs SET c_type_id = 6, c_col_type = 0 WHERE c_id="+attr.id;
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
		Statement statement = null;
		try {
			statement = conn.createStatement();
			String sql = "select TRIGGER_NAME, DESCRIPTION, TRIGGER_BODY from USER_TRIGGERS where TABLE_NAME='" + getClassTableName(cls, true).toUpperCase(Constants.OK) + "' order by TRIGGER_NAME";
			ResultSet rst = statement.executeQuery(sql);
			while (rst.next()) {
				String name = getString(rst, 1);
				String description = getString(rst, 2);
				String body = getString(rst, 3);
				list.add(new TriggerInfo(name, description, body));
			}
			rst.close();
		} catch (SQLException e) {
			log.error(e.getMessage());
		} finally {
			DbUtils.closeQuietly(statement);
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
			String sql = "drop trigger " + getDBPrefix() + triggerName;
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			log.error(e.getMessage());
			message = e.getMessage();
		} finally {
			DbUtils.closeQuietly(statement);
		}
		return message;
	}
	
	@Override
	public void upgradeTo28() throws DriverException, SQLException {
        isUpgrading = true;
        
        log.info("Апгрейд БД до версии 28 ...");
        log.info("Добавление полей c_before_event_expr (событие до сохранения) и c_after_event_expr (событие после сохранения) в таблицу t_attrs.");
  
        Statement st = conn.createStatement();
        
        if (isColumnExists("t_attrs", "c_before_event_expr")) {
            log.info("Поле c_before_event_expr (событие до сохранения) в таблице t_attrs уже существует");
        } else {
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_before_event_expr BLOB DEFAULT NULL");
        }
        
        if (isColumnExists("t_attrs", "c_after_event_expr")) {
            log.info("Поле c_after_event_expr (событие после сохранения) в таблице t_attrs уже существует");
        } else {
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_after_event_expr BLOB DEFAULT NULL");
        }
        
		st.close();
			
		log.info("Апгрейд БД до версии 28 успешно завершен.");
		
        isUpgrading = false;
   }

	@Override
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
        KrnAttribute attr=db.getAttributeByName(fcls.id, "node");
        String tname = getClassTableName(fcls, true);
        if(attr.collectionType>0){
	        String cname1 = getColumnName(attr, 1);
	        String cname = getColumnName(attr);
	        String atname = getAttrTableName(attr);
	        QueryRunner qr = new QueryRunner(true);
	        String sql = "ALTER TABLE " + tname+" ADD "+cname+" VARCHAR2(2000) DEFAULT NULL";
	        if (!isColumnExists(tname, cname)) {
		    	qr.update(conn, sql);
	        }
	    	Statement st = conn.createStatement();
	    	ResultSet rs = st.executeQuery("select c_obj_id from " + tname);
	    	PreparedStatement selPst = conn.prepareStatement("select distinct " + (db.isRnDB ? cname1 : cname) + ",c_index from " + atname + " where c_obj_id=? order by c_index");
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
	    	sql = "DROP TABLE " + atname;
//	    	qr.update(conn, sql);
	    	sql = "UPDATE " + getDBPrefix() + "t_attrs SET c_type_id = 1, c_col_type = 0, c_is_multilingual = 0 WHERE c_id="+attr.id;
	    	qr.update(conn, sql);
	    	db.removeAttribute(attr);
	        attr = getAttributeByNameComp(fcls, "node");
        }
    	//заменяем все uids на шаблон для KrnObject для интерфейса
        KrnAttribute ui_attr=getAttributeByNameComp(fcls, "ui");
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
    	
    	//заменяем все uids на шаблон для KrnObject для объектов обработки
        KrnAttribute objs_attr=getAttributeByNameComp(fcls, "cutObj");
        String cname_objs = getColumnName(objs_attr);
        st = conn.createStatement();
    	rs = st.executeQuery("select c_obj_id,"+cname_objs+" from " + tname+" where "+cname_objs+" is not null" );
    	updPst = conn.prepareStatement("update " + tname + " set " + cname_objs + "=? where c_obj_id=?");
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
		//commit();
		log.info("Апгрейд БД до версии 29 успешно завершен.");
       isUpgrading = false;
  }
	@Override
	//Создание процедуры
	public String createProcedure(String nameProcedure, List<String> args, String body){
		Statement st = null;
		StringBuffer sb=new StringBuffer();
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
			st.executeUpdate(sb.toString());
			if(st.getWarnings()!=null){
				res=st.getWarnings().getMessage()+"\n status:"+st.getWarnings().getSQLState()+"\n ErrorCode:"+st.getWarnings().getErrorCode();
			}
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
	//Вызов процедуры на исполнение
	public List execProcedure(String nameProcedure,
			List<Object> vals,
			List<String> types_in,
			List<String> types_out) throws DriverException {
		return execProcedure(nameProcedure,vals,types_in, types_out, conn, log);
	}

	public static List execProcedure(String nameProcedure,
			List<Object> vals,
			List<String> types_in,
			List<String> types_out,
			Connection connection, Log log) throws DriverException	{
		CallableStatement proc = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		ResultSet prs = null, prs_ = null;
		String[] pathNames=nameProcedure.split("\\.");
		boolean ref_out = false;
		if (vals == null)
			vals = new ArrayList<Object>();
		List res = new ArrayList();
		StringBuffer sb = new StringBuffer();
		sb.append("CALL ").append(nameProcedure).append("(");
		try {
			String ps = pathNames.length>1? pathNames[0].toUpperCase(Constants.OK):"";
			String pkgn = pathNames.length==3? pathNames[1].toUpperCase(Constants.OK):"";
			String pn = ("".equals(pkgn)?"":pkgn+".")+pathNames[pathNames.length-1].toUpperCase(Constants.OK);
			String sql_shema = !ps.equals("")?" AND OWNER = ?":"";
			String sql_pkg = !pkgn.equals("")?" AND PROCEDURE_NAME = ?":"";
			String sql_pkg_ = !pkgn.equals("")?" AND PACKAGE_NAME = ?":"";
			//Проверка наличия процедуры в базе,определение ее типа и статуса
			String sql_name_check="SELECT OBJECT_NAME FROM ALL_PROCEDURES WHERE OBJECT_NAME = ?"+sql_shema+sql_pkg;
			String sql_status_check="SELECT STATUS,OBJECT_TYPE FROM ALL_OBJECTS WHERE OBJECT_NAME = ?"+sql_shema;
			pst=connection.prepareStatement(sql_name_check);
			pst.setString(1, pkgn.equals("")?pn:pkgn);
			if(!ps.equals("")){
				pst.setString(2, ps);
				if(!pkgn.equals(""))
					pst.setString(3, pathNames[2].toUpperCase(Constants.OK));
			}
			rs = pst.executeQuery();
			//Проверка наличия процедуры в базе
			if(!rs.next()) {
				throw new DriverException("Процедура-функция: '"+nameProcedure.toUpperCase(Constants.OK)+"' отсутствует в базе данных!"
						,ErrorCodes.PROCEDURE_NOT_EXIST);
			}
			rs.close();
			pst.close();
			pst=connection.prepareStatement(sql_status_check);
			pst.setString(1, pkgn.equals("")?pn:pkgn);
			if(!ps.equals(""))
				pst.setString(2, ps);
			rs = pst.executeQuery();
			//определение ее типа и статуса
			if(rs.next()){
				if("INVALID".equals(getString(rs, "STATUS"))) {
					throw new DriverException("Процедура-функция: '"+nameProcedure.toUpperCase(Constants.OK)+"' имеет ошибки компиляции!"
							,ErrorCodes.PROCEDURE_NOT_VALID);
				}
			}
			rs.close();
			pst.close();
			int i = 1;
			String returnType = null;
			String argsSQL = "SELECT ARGUMENT_NAME, DATA_TYPE, POSITION, IN_OUT FROM ALL_ARGUMENTS WHERE OBJECT_NAME = ?"+sql_shema+sql_pkg_+" ORDER BY POSITION";
			pst = connection.prepareStatement(argsSQL);
			pst.setString(1, pkgn.equals("")?pn:pathNames[2].toUpperCase(Constants.OK));
			if(!ps.equals("")){
				pst.setString(2, ps);
				if(!pkgn.equals(""))
					pst.setString(3,pkgn);
			}
			rs = pst.executeQuery();
			if (types_in == null && types_out == null) {
				types_in = new ArrayList<String>();
				types_out = new ArrayList<String>();
				while (rs.next()) {
					if (rs.getLong("POSITION")==0) {
						returnType = getString(rs, "DATA_TYPE");
					} else {
						if ("IN".equals(getString(rs, "IN_OUT")))
							types_in.add(getString(rs, "DATA_TYPE"));
						else
							types_out.add(getString(rs, "DATA_TYPE"));
						if (i == 1) {
							sb.append("?");
						} else {
							sb.append(",?");
						}
						i++;
					}
				}
			} else {
				while (rs.next()) {
					if (rs.getLong("POSITION")==0) {
						returnType = getString(rs, "DATA_TYPE");
						break;
					}
				}
				if (types_in != null && types_in.size() > 0) {
					for (String type : types_in) {
						if (i == 1) {
							sb.append("?");
						} else {
							sb.append(",?");
						}
						i++;
					}
				}
				if (types_out != null && types_out.size() > 0) {
					for (String type : types_out) {
						if (i == 1) {
							sb.append("?");
						} else {
							sb.append(",?");
						}
						i++;
					}
				}
			}
			rs.close();
			pst.close();
			
			if (returnType!=null) {
				sb.append(") INTO ?");
				if(types_out == null)
					types_out = new ArrayList<String>();
				types_out.add(returnType);
			}else
				sb.append(")");
				
			proc = connection.prepareCall(sb.toString());
			i = vals.size() + 1;
			if (types_out != null) {
				for (String type : types_out) {
					type = type.toUpperCase(Locale.ROOT);
					if ("VARCHAR2".equals(type) || "NVARCHAR2".equals(type))
						proc.registerOutParameter(i++, OracleTypes.NVARCHAR);
					else if ("NUMBER".equals(type) || "BINARY_INTEGER".equals(type))
						proc.registerOutParameter(i++, OracleTypes.NUMBER);
					else if ("INTEGER".equals(type))
						proc.registerOutParameter(i++, OracleTypes.INTEGER);
					else if ("FLOAT".equals(type))
						proc.registerOutParameter(i++, OracleTypes.FLOAT);
					else if ("TIME".equals(type))
						proc.registerOutParameter(i++, OracleTypes.TIME);
					else if ("TIMESTAMP".equals(type))
						proc.registerOutParameter(i++, OracleTypes.TIMESTAMP);
					else if ("DATE".equals(type))
						proc.registerOutParameter(i++, OracleTypes.DATE);
					else if ("REF CURSOR".equals(type))
						proc.registerOutParameter(i++, OracleTypes.CURSOR);
				}
			}
			i = 0;
			if (types_in != null) {
				for (String type : types_in) {
					type = type.toUpperCase(Locale.ROOT);
					if (vals.get(i) != null) {
						if ("VARCHAR2".equals(type) || "NVARCHAR2".equals(type)) {
							proc.setString(i + 1, (String) vals.get(i));
						} else if ("NUMBER".equals(type) || "BINARY_INTEGER".equals(type)) {
							proc.setLong(i + 1, Long.parseLong((String) vals.get(i)));
						} else if ("INTEGER".equals(type)) {
							proc.setLong(i + 1, Long.parseLong((String) vals.get(i)));
						} else if ("FLOAT".equals(type)) {
							proc.setFloat(i + 1, Float.parseFloat((String) vals.get(i)));
						} else if ("DOUBLE".equals(type)) {
							proc.setDouble(i + 1, Double.parseDouble((String) vals.get(i)));
						} else if ("TIME".equals(type)) {
							proc.setTime(i + 1, Funcs.convertToTimeSql((KrnDate) vals.get(i)));
						} else if ("TIMESTAMP".equals(type)) {
							proc.setTimestamp(i + 1, Funcs.convertToSqlTime((KrnDate) vals.get(i)));
						} else if ("DATE".equals(type)) {
							proc.setDate(i + 1, Funcs.convertToSqlDate((KrnDate) vals.get(i)));
						}
					} else {
						if ("VARCHAR2".equals(type) || "NVARCHAR2".equals(type)) {
							proc.setNull(i + 1, OracleTypes.NVARCHAR);
						} else if ("NUMBER".equals(type) || "BINARY_INTEGER".equals(type)) {
							proc.setNull(i + 1, OracleTypes.NUMBER);
						} else if ("INTEGER".equals(type)) {
							proc.setNull(i + 1, OracleTypes.INTEGER);
						} else if ("FLOAT".equals(type)) {
							proc.setNull(i + 1, OracleTypes.FLOAT);
						} else if ("DOUBLE".equals(type)) {
							proc.setNull(i + 1, OracleTypes.DOUBLE);
						} else if ("TIME".equals(type)) {
							proc.setNull(i + 1, OracleTypes.TIME);
						} else if ("TIMESTAMP".equals(type)) {
							proc.setNull(i + 1, OracleTypes.TIMESTAMP);
						} else if ("DATE".equals(type)) {
							proc.setNull(i + 1, OracleTypes.DATE);
						}
					}
					i++;
				}
			}
		    proc.execute();
			if (types_out != null) {
				i = vals.size() + 1;
				for (String type : types_out) {
					type = type.toUpperCase(Locale.ROOT);
					if ("VARCHAR2".equals(type) || "NVARCHAR2".equals(type)) {
						String res_str = Funcs.normalizeInput(proc.getString(i++));
						res.add(res_str);
					} else if ("NUMBER".equals(type) || "BINARY_INTEGER".equals(type)) {
						long res_long = proc.getLong(i++);
						res.add(res_long);
					} else if ("INTEGER".equals(type)) {
						int res_int = proc.getInt(i++);
						res.add(res_int);
					} else if ("FLOAT".equals(type)) {
						float res_float = proc.getFloat(i++);
						res.add(res_float);
					} else if ("TIME".equals(type)) {
						Time res_time = proc.getTime(i++);
						res.add(res_time);
					} else if ("TIMESTAMP".equals(type)) {
						Timestamp res_timestamp = proc.getTimestamp(i++);
						res.add(res_timestamp);
					} else if ("DATE".equals(type)) {
						Date res_date = proc.getDate(i++);
						res.add(res_date);
					} else if ("REF CURSOR".equals(type)) {
						List res_ref = new ArrayList();
						prs = (OracleResultSet) proc.getObject(i++);
						while (prs.next()) {
							ResultSetMetaData md = prs.getMetaData();
							int count = md.getColumnCount();
							if (count == 1) {
								res_ref.add(prs.getObject(1));
							} else {
								List t = new ArrayList();
								for (int j = 0; j < count; j++) {
									t.add(prs.getObject(j + 1));
								}
								res_ref.add(t);
							}
						}
						res.add(res_ref);
					}
				}
			}
			if (prs != null)
				prs.close();
			proc.close();
		} catch(SQLException e){
			log.error(e, e);
			throw new DriverException("Процедура: '"+nameProcedure.toUpperCase(Constants.OK)+"' ошибка при выполнении!"
					,ErrorCodes.PROCEDURE_SQL_ERROR);
	    } finally {
	    	DbUtils.closeQuietly(rs);
	    	DbUtils.closeQuietly(prs);
	    	DbUtils.closeQuietly(pst);
	    	DbUtils.closeQuietly(proc);
	    } 
		return res;
	}
	
	@Override
	//Получение списка процедур
	public List<String> getListProcedure(String type) {
		List<String> res=new ArrayList<String>();
		PreparedStatement pst=null;
		ResultSet rs = null;
		String psql="SELECT DISTINCT OBJECT_NAME,STATUS FROM USER_OBJECTS WHERE OBJECT_TYPE = ?";
		try {
			pst=conn.prepareStatement(psql);
			pst.setString(1, type);
			rs = pst.executeQuery();
			while(rs.next()){
				res.add(getString(rs, 1)+";"+getString(rs, 2));
			}
		} catch (SQLException e) {
			log.error(e, e);
	    } finally {
	    	DbUtils.closeQuietly(rs);
	    	DbUtils.closeQuietly(pst);
	    } 
		return res;
	}
	@Override
	public byte[] getProcedureContent(String name,String type) {
		StringBuilder sb=new StringBuilder();
		PreparedStatement pst=null;
		ResultSet rs = null;
		String psql="SELECT TEXT FROM USER_SOURCE WHERE NAME = ? AND TYPE = ?";
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
		String psql="DROP "+type+" "+name;
		try {
			pst=conn.prepareStatement(psql);
			res = pst.executeUpdate();
		} catch (SQLException e) {
			log.error(e, e);
	    } finally {
	    	DbUtils.closeQuietly(pst);
	    } 
		return res==1;
	}
	/**
	 * Модификация системных таблиц для поддержки версионности объектов проектирования.
	 * @throws DriverException
	 * @throws SQLException
	 */
	public void upgradeTo31() throws DriverException, SQLException {
        isUpgrading = true;
        
        log.info("Апгрейд БД до версии 31 ...");
        if (!db.isRnDB) {
			Statement st = conn.createStatement();
			try {
				if (!isColumnExists("t_changescls", "c_time")) {
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changescls ADD (" +
						"c_time TIMESTAMP," +
						"c_user_id INTEGER DEFAULT NULL," +
						"c_ip VARCHAR2(15) DEFAULT NULL)"
						);
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changescls MODIFY " +
						"c_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
						);
				}
				
				if (!isColumnExists("t_changes", "c_time")) {
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changes ADD (" +
							"c_time TIMESTAMP," +
							"c_user_id INTEGER DEFAULT NULL," +
							"c_ip VARCHAR2(15) DEFAULT NULL)"
							);
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changes MODIFY " +
							"c_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
							);
				}
				
				if (!isTableExists("t_vcs_objects")) {
					st.executeUpdate(
							"CREATE TABLE "+getDBPrefix()+"t_vcs_objects ("
							+ "c_id INTEGER NOT NULL,"
							+ "c_obj_id INTEGER NOT NULL,"
							+ "c_obj_uid VARCHAR2(20) NOT NULL,"
							+ "c_obj_class_id INTEGER NOT NULL,"
							+ "c_attr_id INTEGER NOT NULL,"
							+ "c_lang_id INTEGER NOT NULL,"
							+ "c_old_value BLOB,"
							+ "c_user_id INTEGER NOT NULL,"
							+ "c_ip VARCHAR2(15) NOT NULL,"
							+ "c_mod_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
							+ "c_mod_last_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
							+ "c_fix_start_id INTEGER,"
							+ "c_fix_end_id INTEGER,"
							+ "c_fix_comment CLOB,"
							+ "PRIMARY KEY (c_id)"
							+ ")");
		
					st.executeUpdate("CREATE UNIQUE INDEX vcs_obj_attr_lang_idx"
							+ " ON "+getDBPrefix()+"t_vcs_objects(c_obj_id,c_attr_id,c_lang_id,c_fix_end_id)");
	
					st.executeUpdate("CREATE SEQUENCE "+getDBPrefix()+"seq_vcs_objects ORDER");
	
					st.executeUpdate("CREATE OR REPLACE TRIGGER "+getDBPrefix()+"ti_vcs_objects "
									+ "BEFORE INSERT ON "+getDBPrefix()+"t_vcs_objects "
									+ "FOR EACH ROW "
									+ "BEGIN"
									+ " SELECT "+getDBPrefix()+"seq_vcs_objects.nextval INTO :new.c_id FROM dual; "
									+ "END;");
				}
				
				if (!isTableExists("t_vcs_model")) {
					st.executeUpdate(
							"CREATE TABLE "+getDBPrefix()+"t_vcs_model ("
							+ "c_id INTEGER NOT NULL,"
							+ "c_entity_id VARCHAR2(36) NOT NULL,"
							+ "c_type INTEGER NOT NULL,"
							+ "c_action INTEGER NOT NULL,"
							+ "c_old_value BLOB,"
							+ "c_user_id INTEGER NOT NULL,"
							+ "c_ip VARCHAR2(15) NOT NULL,"
							+ "c_mod_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
							+ "c_mod_last_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
							+ "c_fix_start_id INTEGER,"
							+ "c_fix_end_id INTEGER,"
							+ "c_fix_comment CLOB,"
							+ "PRIMARY KEY (c_id)"
							+ ")");
					st.executeUpdate("CREATE INDEX vcs_mdl_ant_tp_act_idx"
							+ " ON "+getDBPrefix()+"t_vcs_model(c_entity_id,c_type,c_action,c_fix_end_id)");
		
					st.executeUpdate("CREATE SEQUENCE "+getDBPrefix()+"seq_vcs_model ORDER");
		
					st.executeUpdate("CREATE OR REPLACE TRIGGER "+getDBPrefix()+"ti_vcs_model "
									+ "BEFORE INSERT ON "+getDBPrefix()+"t_vcs_model "
									+ "FOR EACH ROW "
									+ "BEGIN"
									+ " SELECT "+getDBPrefix()+"seq_vcs_model.nextval INTO :new.c_id FROM dual; "
									+ "END;");
				}
			} finally {
				st.close();
			}
        }	
		log.info("Апгрейд БД до версии 31 успешно завершен.");
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
			if (db.isRnDB) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects DROP COLUMN c_dif");
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model DROP COLUMN c_dif");
			}
			if (!isColumnExists("t_vcs_objects", "c_dif"))
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_dif CLOB");
			if (!isColumnExists("t_vcs_model", "c_dif"))
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_dif CLOB");
			
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
			if (!isTableExists("t_syslog")) {
				st.executeUpdate(
						"CREATE TABLE " + getDBPrefix() + "t_syslog ("
						 + "c_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
						 + "c_logger VARCHAR2(50) NOT NULL,"
						 + "c_type NVARCHAR2(20) NOT NULL,"
						 + "c_action NVARCHAR2(50) NOT NULL,"
						 + "c_user NVARCHAR2(20) NOT NULL,"
						 + "c_ip  VARCHAR2(15) NOT NULL,"
						 + "c_host VARCHAR2(20) NOT NULL,"
						 + "c_admin NUMBER(1) NOT NULL,"
						 + "c_message NVARCHAR2(2000)"
						 + ")");
			}
			if (!isIndexExists("t_syslog", "sl_time_user_idx")) {
				st.executeUpdate("CREATE INDEX sl_time_user_idx"
						+ " ON "+getDBPrefix()+"t_syslog(c_time,c_user)");
			}
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
        if (!db.isRnDB) {
			Statement st = conn.createStatement();
			try {
				if (!isColumnExists("t_vcs_objects", "c_name"))
					st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_name NVARCHAR2(255)");
				if (!isColumnExists("t_vcs_model", "c_name"))
					st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_name NVARCHAR2(255)");
				
		        log.info("Апгрейд БД до версии 34 успешно завершен!");
			} finally {
				st.close();
			}
        }
        isUpgrading = false;
    }
	public void upgradeTo35() throws DriverException, SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 35 ...");
		Statement st = conn.createStatement();
		try {
			// Создаем таблицу блокировок для методов
			if (!db.isRnDB) {
				createLockMethodsTable(conn);
			}
			if (!isColumnExists("t_vcs_objects", "c_repl_id")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_repl_id INTEGER"); // повтор
			}
			if (!isColumnExists("t_vcs_model", "c_repl_id")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_repl_id INTEGER");	// повтор
			}
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
			if (isIndexExists("t_vcs_objects", "vcs_obj_attr_lang_idx")) {
				st.executeUpdate("DROP INDEX " + getDBPrefix() + "vcs_obj_attr_lang_idx");
				st.executeUpdate("CREATE UNIQUE INDEX vcs_obj_attr_lang_idx ON "+getDBPrefix()+"t_vcs_objects(c_obj_id,c_attr_id,c_lang_id,c_fix_end_id,c_repl_id)");
			}
			if (isIndexExists("t_vcs_model", "vcs_mdl_ant_tp_act_idx")) {
				st.executeUpdate("DROP INDEX " + getDBPrefix() + "vcs_mdl_ant_tp_act_idx");
				st.executeUpdate("CREATE INDEX vcs_mdl_ant_tp_act_idx ON "+getDBPrefix()+"t_vcs_model(c_entity_id,c_type,c_action,c_fix_end_id,c_repl_id)");
			}
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
			if (!isColumnExists("t_syslog", "c_server_id")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_syslog ADD c_server_id VARCHAR2(20)");
			}
			if (!isColumnExists("t_syslog", "c_object")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_syslog ADD c_object NVARCHAR2(255)");
			}
			if (!isColumnExists("t_syslog", "c_process")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_syslog ADD c_process NVARCHAR2(2000)");
			}
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
		PreparedStatement ust = null;
		try {
	        log.info("Изменение типа колонки 'c_dif' таблицы 't_vcs_model' на 'BLOB'.");
	        log.info("Создание колонки 'c_dif_2'");
	        if (!isColumnExists("t_vcs_model", "c_dif_2")) {
	        	st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_dif_2 BLOB");
	        }
	        log.info("Копирование колонки 'c_dif' в 'c_dif_2'");
	        ust = conn.prepareStatement("UPDATE " + getDBPrefix() + "t_vcs_model SET c_dif_2=? WHERE c_id = ?");
			ResultSet rs = st.executeQuery("SELECT c_id,c_dif FROM " + getDBPrefix() + "t_vcs_model WHERE c_dif IS NOT NULL");
			while (rs.next()) {
				long id = rs.getLong("c_id");
				String val = (String)getValue(rs, "c_dif", PC_MEMO);
				
				if (val.length() > 0) {
					try {
						setValue(ust, 1, PC_BLOB, val.toString().getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						log.error(e, e);
					}
				} else {
					ust.setNull(1, Types.BLOB);
				}
				ust.setLong(2, id);
				ust.executeQuery();
			}
			rs.close();
			ust.close();
	        log.info("Удаление колонки 'c_dif'");
	        if (isColumnExists("t_vcs_model", "c_dif")) {
	        	st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model DROP COLUMN c_dif");
	        }
	        log.info("Переименование колонки 'c_dif_2' в 'c_dif'");
	        if (isColumnExists("t_vcs_model", "c_dif_2")) {
	        	st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model RENAME COLUMN c_dif_2 TO c_dif");
	        }
	        log.info("OK!!!");

	        log.info("Изменение типа колонки 'c_dif' таблицы 't_vcs_objects' на 'BLOB'.");
	        log.info("Создание колонки 'c_dif_2'");
	        if (!isColumnExists("t_vcs_objects", "c_dif_2")) {
	        	st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_dif_2 BLOB");
	        }
	        log.info("Копирование колонки 'c_dif' в 'c_dif_2'");
	        ust = conn.prepareStatement("UPDATE " + getDBPrefix() + "t_vcs_objects SET c_dif_2=? WHERE c_id = ?");
			rs = st.executeQuery("SELECT c_id,c_dif FROM " + getDBPrefix() + "t_vcs_objects WHERE c_dif IS NOT NULL");
			while (rs.next()) {
				long id = rs.getLong("c_id");
				String val = (String)getValue(rs, "c_dif", PC_MEMO);
				
				if (val.length() > 0) {
					try {
						setValue(ust, 1, PC_BLOB, val.toString().getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						log.error(e, e);
					}
				} else {
					ust.setNull(1, Types.BLOB);
				}
				ust.setLong(2, id);
				ust.executeQuery();
			}
	        rs.close();
	        ust.close();
	        log.info("Удаление колонки 'c_dif'");
	        if (isColumnExists("t_vcs_objects", "c_dif")) {
	        	st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects DROP COLUMN c_dif");
	        }
	        log.info("Переименование колонки 'c_dif_2' в 'c_dif'");
	        if (isColumnExists("t_vcs_objects", "c_dif_2")) {
	        	st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects RENAME COLUMN c_dif_2 TO c_dif");
	        }
	        log.info("OK!!!");
		} catch (Exception e) {
			if (isColumnExists("t_vcs_objects", "c_dif_2")) {
		        log.info("Удаление колонки 'c_dif_2'");
	        	st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects DROP COLUMN c_dif_2");
	        }
			if (isColumnExists("t_vcs_model", "c_dif_2")) {
		        log.info("Удаление колонки 'c_dif_2'");
	        	st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model DROP COLUMN c_dif_2");
	        }
		} finally {
			DbUtils.closeQuietly(ust);
			DbUtils.closeQuietly(st);
		}
        
		log.info("Апгрейд БД до версии 38 успешно завершен!");
		isUpgrading = false;
    }
	
	public void upgradeTo39() throws SQLException, DriverException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 39 ...");
        if (!db.isRnDB) {
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
		        if (!isColumnExists("t_methods", "developer")) {
					st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_methods ADD (developer INTEGER)");
		        }
				log.info("OK!!!");
			} finally {
				st.close();
			}
        }
		log.info("Апгрейд БД до версии 39 успешно завершен!");
        isUpgrading = false;
	}
	
	@Override
	public void upgradeTo40() throws SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 40 ...");
        log.info("Добавление полей c_before_del_event_expr (событие 'Перед удалением значения атрибута'), c_after_del_event_expr (событие 'После удаления значения атрибута') в таблице t_attrs.");
        log.info("Добавление полей c_before_create_obj (событие 'Перед созданием объекта'), c_after_create_obj (событие 'После создания объекта'), c_before_delete_obj (событие 'Перед удалением объекта'), c_after_delete_obj (событие 'После удаления объекта') в таблице t_classes.");
		Statement st = conn.createStatement();
		try {
			if (!isColumnExists("t_attrs", "c_before_del_event_expr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_before_del_event_expr BLOB DEFAULT NULL");
			}
			if (!isColumnExists("t_attrs", "c_after_del_event_expr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_after_del_event_expr BLOB DEFAULT NULL");
			}
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			st.close();
		}
		st = conn.createStatement();
		try {
			if (!isColumnExists("t_classes", "c_before_create_obj")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD c_before_create_obj BLOB DEFAULT NULL");
			}
			if (!isColumnExists("t_classes", "c_after_create_obj")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD c_after_create_obj BLOB DEFAULT NULL");
			}
			if (!isColumnExists("t_classes", "c_before_delete_obj")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD c_before_delete_obj BLOB DEFAULT NULL");
			}
			if (!isColumnExists("t_classes", "c_after_delete_obj")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD c_after_delete_obj BLOB DEFAULT NULL");
			}
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			st.close();
		}
		log.info("Апгрейд БД до версии 40 успешно завершен.");
        isUpgrading = false;
    }
	
	@Override
	public void upgradeTo42() throws DriverException, SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 42 ...");
		Statement st = conn.createStatement();
		try {
			try {
		        log.info("Удаление индекса vcs_obj_attr_lang_idx с таблицы t_vcs_objects");
				st.executeUpdate("DROP INDEX vcs_obj_attr_lang_idx");
		        log.info("Индекс vcs_obj_attr_lang_idx с таблицы t_vcs_objects удален");
			} catch (Exception e) {
		        log.error("Индекс vcs_obj_attr_lang_idx с таблицы t_vcs_objects не удален!");
			}
			
			try {
		        log.info("Удаление индекса vcs_mdl_ant_tp_act_idx с таблицы t_vcs_model");
				st.executeUpdate("DROP INDEX vcs_mdl_ant_tp_act_idx");
		        log.info("Индекс vcs_mdl_ant_tp_act_idx с таблицы t_vcs_model удален");
			} catch (Exception e) {
		        log.error("Индекс vcs_mdl_ant_tp_act_idx с таблицы t_vcs_model не удален!");
			}

			try {
		        log.info("Создание уникального индекса vcs_obj_attr_lang_idx на таблице t_vcs_objects");
				st.executeUpdate("CREATE UNIQUE INDEX vcs_obj_attr_lang_idx"
						+ " ON "+getDBPrefix()+"t_vcs_objects(c_obj_id,c_attr_id,c_lang_id,c_fix_end_id)");
		        log.info("Индекс vcs_obj_attr_lang_idx на таблице t_vcs_objects создан");
			} catch (Exception e) {
		        log.error("Индекс vcs_obj_attr_lang_idx с таблице t_vcs_objects не создан!");
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
		        log.info("Создание уникального индекса vcs_mdl_ant_tp_idx на таблице t_vcs_model");
				st.executeUpdate("CREATE UNIQUE INDEX vcs_mdl_ant_tp_idx"
						+ " ON "+getDBPrefix()+"t_vcs_model(c_entity_id,c_type,c_fix_end_id)");
		        log.info("Индекс vcs_mdl_ant_tp_idx на таблице t_vcs_model создан");
			} catch (Exception e) {
		        log.error("Индекс vcs_mdl_ant_tp_idx с таблице t_vcs_model не создан!");
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
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD c_before_create_obj_tr INTEGER DEFAULT 0 NOT NULL");
				log.info("Колонка c_before_create_obj_tr добавлена.");
			} else {
				log.info("Колонка c_before_create_obj_tr уже создана.");
			}
			if (!isColumnExists("t_classes", "c_after_create_obj_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD c_after_create_obj_tr INTEGER DEFAULT 0 NOT NULL");
				log.info("Колонка c_after_create_obj_tr добавлена.");
			} else {
				log.info("Колонка c_after_create_obj_tr уже создана.");
			}
			if (!isColumnExists("t_classes", "c_before_delete_obj_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD c_before_delete_obj_tr INTEGER DEFAULT 0 NOT NULL");
				log.info("Колонка c_before_delete_obj_tr добавлена.");
			} else {
				log.info("Колонка c_before_delete_obj_tr уже создана.");
			}
			if (!isColumnExists("t_classes", "c_after_delete_obj_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD c_after_delete_obj_tr INTEGER DEFAULT 0 NOT NULL");
				log.info("Колонка c_after_delete_obj_tr добавлена.");
			} else {
				log.info("Колонка c_after_delete_obj_tr уже создана.");
			}
			
			if (!isColumnExists("t_attrs", "c_before_event_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_before_event_tr INTEGER DEFAULT 0 NOT NULL");
				log.info("Колонка c_before_event_tr добавлена.");
			} else {
				log.info("Колонка c_before_event_tr уже создана.");
			}
			if (!isColumnExists("t_attrs", "c_after_event_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_after_event_tr INTEGER DEFAULT 0 NOT NULL");
				log.info("Колонка c_after_event_tr добавлена.");
			} else {
				log.info("Колонка c_after_event_tr уже создана.");
			}
			if (!isColumnExists("t_attrs", "c_before_del_event_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_before_del_event_tr INTEGER DEFAULT 0 NOT NULL");
				log.info("Колонка c_before_del_event_tr добавлена.");
			} else {
				log.info("Колонка c_before_del_event_tr уже создана.");
			}
			if (!isColumnExists("t_attrs", "c_after_del_event_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_after_del_event_tr INTEGER DEFAULT 0 NOT NULL");
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

	public void upgradeTo48() throws DriverException, SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 48 ...");
		Statement st = conn.createStatement();
		try {
			if (!isColumnExists("t_syslog", "c_tab_name")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_syslog ADD c_tab_name VARCHAR2(255)");
			}
			if (!isColumnExists("t_syslog", "c_col_name")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_syslog ADD c_col_name VARCHAR2(255)");
			}
			log.info("Апгрейд БД до версии 48 успешно завершен!");
		} finally {
			st.close();
		}
        isUpgrading = false;
	}

	@Override
	protected void createVcsDataRecord(KrnObject obj, KrnAttribute attr, long langId, Object value,boolean isObjNew,long trId) throws DriverException, SQLException {
		//Сначала создаем записи для атрибутов удаляемого объекта
		if(attr.id==2 && !isObjNew){
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
		String obj_name = (String) getValue(obj.id, tattr.id, 0, tlangId, 0);
		PreparedStatement pst=null;
		if(version < VERSION_UL){
		pst = conn.prepareStatement(
				"INSERT INTO "+getDBPrefix()+"t_vcs_objects"
				+ "(c_id,c_obj_id,c_obj_uid,c_obj_class_id,c_attr_id,c_lang_id,c_old_value,c_dif,c_user_id,c_ip,c_name,c_repl_id,c_fix_start_id)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,"
				+ "(SELECT MAX(c_id) FROM "+getDBPrefix()+"t_changes WHERE c_object_id=? AND c_attr_id=? AND c_lang_id=?))");
		}else{
			pst = conn.prepareStatement(
					"INSERT INTO "+getDBPrefix()+"t_vcs_objects"
					+ "(c_id,c_obj_id,c_obj_uid,c_obj_class_id,c_attr_id,c_lang_id,c_old_value,c_dif,c_user_id,c_ip,c_name,c_rimport_id,c_fix_start_id)"
					+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,"
					+ "(SELECT MAX(c_id) FROM "+getDBPrefix()+"t_changes WHERE c_object_id=? AND c_attr_id=? AND c_lang_id=?))");
			
		}
		try {
			long lid= getNextId(getDBPrefix()+"seq_vcs_objects");
			pst.setLong(1, lid);
			pst.setLong(2, obj.id);
			pst.setString(3, obj.uid);
			pst.setLong(4, obj.classId);
			pst.setLong(5, attr.id);
			pst.setLong(6, langId);
			StringBuilder diff = new StringBuilder();
			setValue(pst, 7, PC_BLOB, packValue(obj, attr, langId, value, diff,trId));
			if (diff.length() > 0) {
				try {
					setValue(pst, 8, PC_BLOB, diff.toString().getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					log.error(e, e);
				}
			} else {
				pst.setNull(8, Types.BLOB);
			}
			pst.setLong(9, us.getUserId());
			pst.setString(10, us.getIp());
			pst.setString(11, obj_name);
			if (isImportState) {
				pst.setLong(12,importObjId);
			} else {
				pst.setNull(12, Types.INTEGER);
			}
			pst.setLong(13, obj.id);
			pst.setLong(14, attr.id);
			pst.setLong(15, langId);
			pst.executeUpdate();
		} finally {
			DbUtils.closeQuietly(pst);
		}
	}
	
	@Override
	protected void createVcsModelRecord(int type, int action, Object oldObj, Object newObj, byte[] newExpr, Connection conn)
			throws DriverException, SQLException {
		
		String uid=getVcsUid(newObj);
		String name=getName(uid, type, newObj);
		final String endl = Constants.EOL;
		PreparedStatement pst=null;
		if(version < VERSION_UL){
			pst = conn.prepareStatement("INSERT INTO "+getDBPrefix()+"t_vcs_model"
				+ "(c_id,c_entity_id,c_type,c_action,c_old_value,c_dif,c_user_id,c_ip,c_name,c_repl_id,c_fix_start_id)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,"
				+ "(SELECT MAX(c_id) FROM "+getDBPrefix()+"t_changescls WHERE c_entity_id=? AND c_type=?))");
		}else{
			pst = conn.prepareStatement("INSERT INTO "+getDBPrefix()+"t_vcs_model"
					+ "(c_id,c_entity_id,c_type,c_action,c_old_value,c_dif,c_user_id,c_ip,c_name,c_rimport_id,c_fix_start_id)"
					+ " VALUES (?,?,?,?,?,?,?,?,?,?,"
					+ "(SELECT MAX(c_id) FROM "+getDBPrefix()+"t_changescls WHERE c_entity_id=? AND c_type=?))");
		}

		try {
			long lid= getNextId(getDBPrefix()+"seq_vcs_model");
			
			pst.setLong(1, lid);
			pst.setString(2, uid);
			pst.setInt(3, type);
			pst.setInt(4, action);

			setValue(pst, 5, PC_BLOB, packModelValue(oldObj, type, action, newExpr));

			String diff = getDiff(type,oldObj, newObj, newExpr, action);

			if (diff.length() > 0) {
				try {
					setValue(pst, 6, PC_BLOB, diff.toString().getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					log.error(e, e);
				}
			} else {
				pst.setNull(6, Types.BLOB);
			}
			pst.setLong(7, us.getUserId());
			pst.setString(8, us.getIp());
			pst.setString(9, name);
			if(isImportState){
				pst.setLong(10,importObjId);
			}else{
				pst.setNull(10, Types.INTEGER);
			}
			pst.setString(11, uid);
			pst.setInt(12, type);
			pst.executeUpdate();
		} catch (DriverException e) {
			log.error(e, e);
			throw e;
		} catch (SQLException e) {
			log.error(e, e);
			throw e;
		} finally {
			DbUtils.closeQuietly(pst);
		}
	}
	/*
	// Для триггеров
	protected void createVcsModelRecord(String entityUid, int entityType, int action, byte[] oldExpr, byte[] newExpr, Connection conn) throws DriverException, SQLException {
		PreparedStatement pst = conn.prepareStatement("INSERT INTO "+getDBPrefix()+"t_vcs_model"
				+ "(c_id,c_entity_id,c_type,c_action,c_old_value,c_dif,c_user_id,c_ip,c_name,c_repl_id,c_fix_start_id)"
				+ " VALUES (?,?,?,?,?,?,?,?,?,?,"
				+ "(SELECT MAX(c_id) FROM "+getDBPrefix()+"t_changescls WHERE c_entity_id=? AND c_type=?))");
		try {
			long lid= getNextId(getDBPrefix()+"seq_vcs_model");
			
			pst.setLong(1, lid);
			pst.setString(2, entityUid);
			pst.setInt(3, entityType);
			pst.setInt(4, action);
			
			setValue(pst, 5, PC_BLOB, packModelValue(action, oldExpr, newExpr));
			
			String diff = getDiff(oldExpr, newExpr, action);
			
			if (diff.length() > 0) {
				try {
					setValue(pst, 6, PC_BLOB, diff.toString().getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					log.error(e, e);
				}
			} else {
				pst.setNull(6, Types.BLOB);
			}
			
			pst.setLong(7, us.getUserId());
			pst.setString(8, us.getIp());
			pst.setString(9, getName(entityUid, entityType));
			if(isImportState){
				pst.setLong(10,importObjId);
			}else{
				pst.setNull(10, Types.BIGINT);
			}
			pst.setString(11, entityUid);
			pst.setInt(12, entityType);
			pst.executeUpdate();
		} catch (DriverException e) {
			log.error(e, e);
			throw e;
		} catch (SQLException e) {
			log.error(e, e);
			throw e;
		} finally {
			DbUtils.closeQuietly(pst);
		}
	}
*/
	@Override
	public void commitVcsObjectIfEditingBeforeReplication(long objId, long attrId, long langId, String comment) throws DriverException {
		try {
			PreparedStatement selPst = conn.prepareStatement(
					"SELECT c_obj_uid,c_obj_class_id,c_user_id,c_ip,c_id FROM " + getDBPrefix() + "t_vcs_objects"
					+ " WHERE c_obj_id=? AND c_attr_id=? AND c_lang_id=? AND c_fix_end_id IS NULL ORDER BY c_id");

			PreparedStatement chPst = conn.prepareStatement(
					"INSERT INTO "+getDBPrefix()+"t_changes (c_id,c_object_id,c_object_uid,c_class_id,c_attr_id,c_lang_id,c_tr_id,c_is_repl,c_user_id,c_ip)"
					+ " VALUES (?,?,?,?,?,?,0,1,?,?)");
			
			PreparedStatement vcsPst = conn.prepareStatement("UPDATE "+getDBPrefix()+"t_vcs_objects SET c_fix_end_id=?,c_fix_comment=? WHERE c_id=?");

			ResultSet rs = null;
			try {
				selPst.setLong(1, objId);
				selPst.setLong(2, attrId);
				selPst.setLong(3, langId);
				rs = selPst.executeQuery();
				while (rs.next()) {
					long lid= getNextId(getDBPrefix()+"seq_changes");
					chPst.setLong(1, lid);
					chPst.setLong(2, objId);
					chPst.setString(3, getSanitizedString(rs, 1));
					chPst.setLong(4, rs.getLong(2));
					chPst.setLong(5, attrId);
					chPst.setLong(6, langId);
					chPst.setLong(7, rs.getLong(3));
					chPst.setString(8, getSanitizedString(rs, 4));
					chPst.executeUpdate();
					
					vcsPst.setLong(1, lid);
					setValue(vcsPst, 2, PC_MEMO, comment);
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
					"SELECT c_obj_id,c_obj_uid,c_obj_class_id,c_attr_id,c_lang_id,c_user_id,c_ip,c_id FROM "+getDBPrefix()+"t_vcs_objects"
					+ " WHERE c_repl_id=? AND c_fix_end_id IS NULL ORDER BY c_id");
			}else{
				selPst = conn.prepareStatement(
						"SELECT c_obj_id,c_obj_uid,c_obj_class_id,c_attr_id,c_lang_id,c_user_id,c_ip,c_id FROM "+getDBPrefix()+"t_vcs_objects"
						+ " WHERE c_rimport_id=? AND c_fix_end_id IS NULL ORDER BY c_id");
			}
			
			PreparedStatement chPst = conn.prepareStatement(
					"INSERT INTO "+getDBPrefix()+"t_changes (c_id,c_object_id,c_object_uid,c_class_id,c_attr_id,c_lang_id,c_tr_id,c_is_repl,c_user_id,c_ip)"
					+ " VALUES (?,?,?,?,?,?,0,1,?,?)");
			
			PreparedStatement vcsPst = conn.prepareStatement("UPDATE "+getDBPrefix()+"t_vcs_objects SET c_fix_end_id=?,c_fix_comment=? WHERE c_id=?");

			ResultSet rs = null;
			try {
				selPst.setLong(1, replId);
				rs = selPst.executeQuery();
				while (rs.next()) {
					long lid= getNextId(getDBPrefix()+"seq_changes");
					chPst.setLong(1, lid);
					chPst.setLong(2, rs.getLong(1));
					chPst.setString(3, getSanitizedString(rs, 2));
					chPst.setLong(4, rs.getLong(3));
					chPst.setLong(5, rs.getLong(4));
					chPst.setLong(6, rs.getLong(5));
					chPst.setLong(7, rs.getLong(6));
					chPst.setString(8, getSanitizedString(rs, 7));
					chPst.executeUpdate();
					
					vcsPst.setLong(1, lid);
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
			throw convertException(e);
		}
	}

	@Override
	public void commitVcsObjects(List<KrnVcsChange> changes, String comment) throws DriverException {
		commitVcsModel(changes, comment);
		try {
			PreparedStatement selPst = conn.prepareStatement(
					"SELECT c_obj_id,c_obj_uid,c_obj_class_id,c_attr_id,c_lang_id,c_user_id,c_ip,c_id FROM "+getDBPrefix()+"t_vcs_objects"
					+ " WHERE c_obj_id=? AND c_fix_end_id IS NULL ORDER BY c_id");
			
			PreparedStatement chPst = conn.prepareStatement(
					"INSERT INTO "+getDBPrefix()+"t_changes (c_id,c_object_id,c_object_uid,c_class_id,c_attr_id,c_lang_id,c_tr_id,c_is_repl,c_user_id,c_ip)"
					+ " VALUES (?,?,?,?,?,?,0,1,?,?)");
			
			PreparedStatement vcsPst = conn.prepareStatement("UPDATE "+getDBPrefix()+"t_vcs_objects SET c_fix_end_id=?,c_fix_comment=? WHERE c_id=?");

			ResultSet rs = null;
			try {
				for (KrnVcsChange change : changes) {
					if (change.cvsChangeObj != null) {
						selPst.setLong(1, change.cvsChangeObj.id);
						rs = selPst.executeQuery();
						while (rs.next()) {
							long lid= getNextId(getDBPrefix()+"seq_changes");
							chPst.setLong(1, lid);
							chPst.setLong(2, rs.getLong(1));
							chPst.setString(3, getSanitizedString(rs, 2));
							chPst.setLong(4, rs.getLong(3));
							chPst.setLong(5, rs.getLong(4));
							chPst.setLong(6, rs.getLong(5));
							chPst.setLong(7, rs.getLong(6));
							chPst.setString(8, getSanitizedString(rs, 7));
							chPst.executeUpdate();
							
							vcsPst.setLong(1, lid);
							setValue(vcsPst, 2, PC_MEMO, comment);
							vcsPst.setLong(3, rs.getLong(8));
							vcsPst.executeUpdate();
						}
						rs.close();
					}
				}
			} finally {
				DbUtils.closeQuietly(rs);
				DbUtils.closeQuietly(vcsPst);
				DbUtils.closeQuietly(chPst);
				DbUtils.closeQuietly(selPst);
			}
		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	@Override
	public void commitVcsModelIfEditingBeforeReplication(String entityUid, int entityType, String comment) throws DriverException {
		try {
			PreparedStatement selPst = conn.prepareStatement(
					"SELECT c_entity_id,c_type,c_action,c_user_id,c_ip,c_id FROM "+getDBPrefix()+"t_vcs_model"
					+ " WHERE c_entity_id=? AND c_type=? AND c_fix_end_id IS NULL ORDER BY c_id");
			
			PreparedStatement chPst = conn.prepareStatement(
					"INSERT INTO "+getDBPrefix()+"t_changescls (c_id,c_entity_id,c_type,c_action,c_user_id,c_ip)"
					+ " VALUES (?,?,?,?,?,?)");
			
			PreparedStatement vcsPst = conn.prepareStatement("UPDATE "+getDBPrefix()+"t_vcs_model SET c_fix_end_id=?,c_fix_comment=? WHERE c_id=?");

			ResultSet rs = null;
			try {
				selPst.setString(1, entityUid);
				selPst.setInt(2, entityType);
				rs = selPst.executeQuery();
				while (rs.next()) {
					long lid= getNextId(getDBPrefix()+"seq_changescls");
					chPst.setLong(1, lid);
					chPst.setString(2, getSanitizedString(rs, 1));
					chPst.setLong(3, rs.getLong(2));
					chPst.setLong(4, rs.getLong(3));
					chPst.setLong(5, rs.getLong(4));
					chPst.setString(6, getSanitizedString(rs, 5));
					chPst.executeUpdate();
					
					vcsPst.setLong(1, lid);
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
			throw convertException(e);
		}
	}

	@Override
	public void commitVcsModelAfterReplication(long replId, String comment) throws DriverException {
		try {
			PreparedStatement selPst = null;
			if(version < VERSION_UL){
				selPst = conn.prepareStatement(
					"SELECT c_id,c_entity_id,c_type,c_action,c_user_id,c_ip FROM "+getDBPrefix()+"t_vcs_model"
					+ " WHERE c_repl_id=? AND c_fix_end_id IS NULL ORDER BY c_id");
			}else{
				selPst = conn.prepareStatement(
						"SELECT c_id,c_entity_id,c_type,c_action,c_user_id,c_ip FROM "+getDBPrefix()+"t_vcs_model"
						+ " WHERE c_rimport_id=? AND c_fix_end_id IS NULL ORDER BY c_id");
			}
			
			PreparedStatement chPst = conn.prepareStatement(
					"INSERT INTO "+getDBPrefix()+"t_changescls (c_id,c_entity_id,c_type,c_action,c_user_id,c_ip)"
					+ " VALUES (?,?,?,?,?,?)");
			
			PreparedStatement vcsPst = conn.prepareStatement("UPDATE "+getDBPrefix()+"t_vcs_model SET c_fix_end_id=?,c_fix_comment=? WHERE c_id=?");

			ResultSet rs = null;
			try {
				selPst.setLong(1, replId);
				rs = selPst.executeQuery();
				while (rs.next()) {
					long lid= getNextId(getDBPrefix()+"seq_changescls");
					chPst.setLong(1, lid);
					chPst.setString(2, getSanitizedString(rs, 2));
					chPst.setLong(3, rs.getLong(3));
					chPst.setLong(4, rs.getLong(4));
					chPst.setLong(5, rs.getLong(5));
					chPst.setString(6, getSanitizedString(rs, 6));
					chPst.executeUpdate();
					
					vcsPst.setLong(1, lid);
					setValue(vcsPst, 2, PC_MEMO, comment);
					vcsPst.setLong(3, rs.getLong(1));
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
			throw convertException(e);
		}
	}

	@Override
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
						long lid= getNextId(getDBPrefix()+"seq_changescls");
						chPst.setString(1, rs.getString(1));
						chPst.setInt(2, rs.getInt(2));
						chPst.setLong(3, rs.getLong(3));
						chPst.setLong(4, rs.getLong(4));
						chPst.setString(5, getString(rs, 5));
						chPst.executeUpdate();
						vcsPst.setLong(1, lid);
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

	@Override
	public void commitVcsModel(List<KrnVcsChange> changes, String comment) throws DriverException {
		try {
			PreparedStatement selPst = conn.prepareStatement(
					"SELECT c_action,c_user_id,c_ip,c_id FROM "+getDBPrefix()+"t_vcs_model"
					+ " WHERE c_entity_id=? AND c_type=? AND c_fix_end_id IS NULL ORDER BY c_id");
			
			PreparedStatement chPst = conn.prepareStatement(
					"INSERT INTO "+getDBPrefix()+"t_changescls (c_id,c_entity_id,c_type,c_action,c_user_id,c_ip)"
					+ " VALUES (?,?,?,?,?,?)");
			
			PreparedStatement vcsPst = conn.prepareStatement("UPDATE "+getDBPrefix()+"t_vcs_model SET c_fix_end_id=?,c_fix_comment=? WHERE c_id=?");

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
						long lid= getNextId(getDBPrefix()+"seq_changescls");
						chPst.setLong(1, lid);
						if (change.cvsChangeMethod != null) {
							chPst.setString(2, change.cvsChangeMethod.uid);
							chPst.setInt(3, ENTITY_TYPE_METHOD);
						} else if (change.cvsChangeClass != null) {
							chPst.setString(2, change.cvsChangeClass.uid);
							chPst.setInt(3, change.typeId);
						} else if (change.cvsChangeAttr != null) {
							chPst.setString(2, change.cvsChangeAttr.uid);
							chPst.setInt(3, change.typeId);
						}
						chPst.setLong(4, rs.getLong(1));
						chPst.setLong(5, rs.getLong(2));
						chPst.setString(6, getSanitizedString(rs, 3));
						chPst.executeUpdate();
						
						vcsPst.setLong(1, lid);
						setValue(vcsPst, 2, PC_MEMO, comment);
						vcsPst.setLong(3, rs.getLong(4));
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
			throw convertException(e);
		}
	}

	@Override
	public boolean isTableExists(String tableName) {
		boolean isExist = true;
		try {
			PreparedStatement pst = conn.prepareStatement("SELECT COUNT(*) FROM USER_TABLES WHERE TABLE_NAME = ?");
			pst.setString(1, tableName.toUpperCase(Constants.OK));
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
	public boolean isIndexExists(String tName,String idxName) {
		boolean isExist = true;
		try {
			PreparedStatement pst = conn.prepareStatement("SELECT COUNT(*) FROM USER_INDEXES WHERE TABLE_NAME = ? AND INDEX_NAME = ?");
			pst.setString(1, tName.toUpperCase(Constants.OK));
			pst.setString(2, idxName.toUpperCase(Constants.OK));
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
		String idxType = null;
		try {
			PreparedStatement pst = conn.prepareStatement("SELECT INDEX_TYPE FROM USER_INDEXES WHERE TABLE_NAME = ? AND INDEX_NAME = ? GROUP BY INDEX_TYPE");
			pst.setString(1, tName.toUpperCase(Constants.OK));
			pst.setString(2, idxName.toUpperCase(Constants.OK));
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				idxType = getString(rs, 1);
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return Funcs.sanitizeSQL(idxType);
	}
	
	protected boolean isColumnExists(String t_name,String c_name){
		boolean res=false;
		PreparedStatement pst=null;
		ResultSet rs=null;
		try{
			String sql="SELECT COLUMN_NAME FROM USER_TAB_COLUMNS WHERE TABLE_NAME = ? AND COLUMN_NAME = ?";
			pst=conn.prepareStatement(sql);
			pst.setString(1, t_name.toUpperCase(Constants.OK));
			pst.setString(2, c_name.toUpperCase(Constants.OK));
			rs=pst.executeQuery();
			if(rs.next())
				res=true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}finally{
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
		}
		return res;
	}

	@Override
	public String getCurrentScheme() throws DriverException {
		return null; //TODO Реализовать.
	}

	@Override
	public String getSessionId(Connection conn) {
		Statement st = null;
		ResultSet rs = null;
		String SID="";
		try {
			st = conn.createStatement();
			rs = st.executeQuery("select USERENV ('SID') from dual");
			//rs = st.executeQuery("SELECT SID,SERIAL#,AUDSID,USERNAME FROM v$session WHERE AUDSID=(select USERENV ('sessionid') from dual)");
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
	
	@Override
	public long init() throws DriverException {
		long res = super.init();
		// TODO Убрать когда будут все классы отдельными.
		PreparedStatement parentPst = null;
		PreparedStatement childrenPst = null;
		PreparedStatement seqPst = null;
		PreparedStatement colPst = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			parentPst = conn.prepareStatement(
					"SELECT cl.C_PARENT_ID FROM T_CLINKS cl,T_CLASSES tc"
					+ " WHERE cl.C_CHILD_ID=? AND tc.C_ID=cl.C_PARENT_ID AND tc.C_MOD=0 AND tc.C_ID>99");
			childrenPst = conn.prepareStatement(
					"SELECT cl.C_CHILD_ID FROM T_CLINKS cl,T_CLASSES tc"
					+ " WHERE cl.C_PARENT_ID=? AND tc.C_ID=cl.C_CHILD_ID AND tc.C_MOD=0");
			seqPst = conn.prepareStatement("SELECT COUNT(*) FROM user_sequences WHERE sequence_name=?");
			colPst = conn.prepareStatement("SELECT DATA_LENGTH FROM USER_TAB_COLUMNS WHERE TABLE_NAME=? AND COLUMN_NAME=?");
			colPst.setString(2, "C_UID");
			st = conn.createStatement();
			for (Long classId : db.getSeparateClassIds()) {
				// Проверяем что все дочерние классы тоже отдельные.
				childrenPst.setLong(1, classId);
				rs = childrenPst.executeQuery();
				while (rs.next()) {
					long childClassId = rs.getLong(1);
					if (!db.isSeparateClass(childClassId)) {
						throw new DriverException(
								"Ошибка при проверке отдельных классов. Класс "
								+ classId + " имеет не отдельный дочерний класс " + childClassId);
					}
				}
				rs.close();
				// Проверяем что все родительские классы тоже отдельные. И выясняем являлется ли он корневым.
				boolean isRoot = true;
				parentPst.setLong(1, classId);
				rs = childrenPst.executeQuery();
				while (rs.next()) {
					long parentClassId = rs.getLong(1);
					if (!db.isSeparateClass(parentClassId)) {
						throw new DriverException(
								"Ошибка при проверке отдельных классов. Класс "
								+ classId + " имеет не отдельный родительский класс " + parentClassId);
					}
					if (parentClassId != classId) {
						isRoot = false;
					}
				}
				rs.close();
				KrnClass cls = getClassByIdComp(classId);
				if(cls==null)
					log.error("Класс classId="+classId+" отсутствует в база");
				final String tname = getClassTableName(cls, false).toUpperCase(Constants.OK); // Верхний регистр обязателен!
				if (isRoot) {
					// Проверяем существует ли sequence для корневого класса.
					final String seqName = "SEQ_CT" + classId; // верхний регистр обязателен!
					seqPst.setString(1, seqName);
					rs = seqPst.executeQuery();
					rs.next();
					int count = rs.getInt(1);
					rs.close();
					if (count == 0) {
						// Создаем sequence продолжая нумерацию c_obj_id.
						rs = st.executeQuery("SELECT MAX(C_OBJ_ID) FROM " + tname);
						rs.next();
						long lastId = rs.getLong(1) + 1;
						rs.close();
						st.executeUpdate("CREATE SEQUENCE " + seqName + " START WITH " + lastId + " INCREMENT BY 1");
					}
				}
				// Расширяем колонку c_uid для более длинного UID-а.
				colPst.setString(1, tname);
				rs = colPst.executeQuery();
				rs.next();
				int size = rs.getInt(1);
				rs.close();
				if (size < 30) {
					st.executeUpdate("ALTER TABLE " + tname + " MODIFY c_uid VARCHAR2(30)");
				}
			}
			commit();
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(st);
			DbUtils.closeQuietly(parentPst);
			DbUtils.closeQuietly(childrenPst);
			DbUtils.closeQuietly(colPst);
			DbUtils.closeQuietly(seqPst);
			DbUtils.closeQuietly(colPst);
		}
		
		return res;
	}
	
	public void upgradeStructure() throws SQLException {
		
		String call = "CREATE OR REPLACE PACKAGE " + db.getSchemeName() + ".hr_sec_ctx IS " +
				"PROCEDURE set_user_id (u NUMBER); " +
				"PROCEDURE set_user_uid (u VARCHAR2); " +
				"PROCEDURE set_user_name (u VARCHAR2); " +
				"PROCEDURE set_role_id (u NUMBER); " +
				"PROCEDURE set_role_uid (u VARCHAR2); " +
				"PROCEDURE set_role_name (u VARCHAR2); " +
				"PROCEDURE set_balans_id (u NUMBER); " +
				"PROCEDURE set_balans_uid (u VARCHAR2); " +
				"PROCEDURE set_balans_name (u VARCHAR2); " +
				"END hr_sec_ctx;";

		Statement st = conn.createStatement();
		st.executeUpdate(call);
		st.close();

		call = "CREATE OR REPLACE PACKAGE BODY " + db.getSchemeName() + ".hr_sec_ctx IS " +
				"PROCEDURE set_user_id (u NUMBER) IS " +
				"BEGIN " +
				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'id', u); " +
				"END set_user_id; " +
				"PROCEDURE set_user_uid (u VARCHAR2) IS " +
				"BEGIN " +
				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'uid', u); " +
				"END set_user_uid; " +
				"PROCEDURE set_user_name (u VARCHAR2) IS " +
				"BEGIN " +
				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'name', u); " +
				"END set_user_name; " +
				"PROCEDURE set_role_id (u NUMBER) IS " +
				"BEGIN " +
				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'rid', u); " +
				"END set_role_id; " +
				"PROCEDURE set_role_uid (u VARCHAR2) IS " +
				"BEGIN " +
				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'ruid', u); " +
				"END set_role_uid; " +
				"PROCEDURE set_role_name (u VARCHAR2) IS " +
				"BEGIN " +
				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'rname', u); " +
				"END set_role_name; " +
				"PROCEDURE set_balans_id (u NUMBER) IS " +
				"BEGIN " +
				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'bid', u); " +
				"END set_balans_id; " +
				"PROCEDURE set_balans_uid (u VARCHAR2) IS " +
				"BEGIN " +
				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'buid', u); " +
				"END set_balans_uid; " +
				"PROCEDURE set_balans_name (u VARCHAR2) IS " +
				"BEGIN " +
				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'bname', u); " +
				"END set_balans_name; " +
				"END hr_sec_ctx;";

		st = conn.createStatement();
		st.executeUpdate(call);
		st.close();
		
		call = "CREATE OR REPLACE FUNCTION CXVAR (D1 VARCHAR2) RETURN VARCHAR2 IS " +
				"RET_CONDITION VARCHAR2 (2000); " +
				"BEGIN " +
				"SELECT SYS_CONTEXT('" + db.getSchemeName() + "_hr_ctx', D1) INTO RET_CONDITION FROM DUAL; " +
				"RETURN RET_CONDITION; " +
				"END CXVAR;";

		st = conn.createStatement();
		st.executeUpdate(call);
		st.close();
		//commit();

		Statement st1 = conn.createStatement();
		try {
			st1.executeUpdate("DROP CONTEXT " + db.getSchemeName() + "_hr_ctx");
		} catch (Exception e) {
		} finally {
    		DbUtils.closeQuietly(st1);
		}

		st1 = conn.createStatement();
		try {
			st1.executeUpdate("CREATE CONTEXT " + db.getSchemeName() + "_hr_ctx USING " + db.getSchemeName() + ".hr_sec_ctx");
		} catch (Exception e) {
			log.error(e, e);
		} finally {
    		DbUtils.closeQuietly(st1);
		}
		
		st1 = conn.createStatement();
		st1.executeUpdate("GRANT EXECUTE ON " + db.getSchemeName() + ".hr_sec_ctx TO public");
		st1.close();

        log.info("Апгрейд БД до версии 17 ...");

        	String sql = "ALTER TABLE "+getDBPrefix()+"t_classes ADD (C_TNAME VARCHAR2(30) DEFAULT NULL ) ADD (UNIQUE (\"C_TNAME\"))";
        	PreparedStatement pst = conn.prepareStatement(sql);
			pst.executeUpdate();
			pst.close();
		
		// Проверяем есть ли колонка 'C_TNAME'
		st = conn.createStatement();
		ResultSet set = st.executeQuery("select C_TNAME from "+getDBPrefix()+"t_classes where rownum=1");
		set.close();
		
			sql = "ALTER TABLE "+getDBPrefix()+"t_attrs ADD (C_TNAME VARCHAR2(30) DEFAULT NULL )";// ADD (UNIQUE (\"c_class_id\",\"C_TNAME\"))";
        	pst = conn.prepareStatement(sql);
			pst.executeUpdate();
			pst.close();

		// Проверяем есть ли колонка 'C_TNAME'
		set = st.executeQuery("select C_TNAME from "+getDBPrefix()+"t_attrs where rownum=1");
		set.close();

		log.info("Апгрейд БД до версии "+kz.tamur.or3.util.Tname.TnameVersionBD+" успешно завершен.");
        
	
        log.info("Апгрейд БД до версии 24 ...");
        log.info("В таблице t_changes добавлено поле c_object_uid.");
		
         sql = "SELECT COUNT(*) FROM user_tab_columns WHERE table_name='T_CHANGES' AND column_name='C_OBJECT_UID'";
		 pst = conn.prepareStatement(sql);
		 set = pst.executeQuery();
		boolean isExist = false;
		if (set.next()) {
			isExist = set.getInt(1) > 0;
		}
		set.close();
		pst.close();
		
		if (!isExist) {
			QueryRunner qr = new QueryRunner(true);
			sql = "ALTER TABLE " + getDBPrefix() + "t_changes ADD (c_object_uid VARCHAR2(20) DEFAULT NULL)";
			qr.update(conn, sql);
		}
        
		log.info("Апгрейд БД до версии 24 успешно завершен.");
		
        log.info("Апгрейд БД до версии 28 ...");
        log.info("Добавление полей c_before_event_expr (событие до сохранения) и c_after_event_expr (событие после сохранения) в таблицу t_attrs.");
  
    	st = conn.createStatement();
		sql = "ALTER TABLE " + getDBPrefix() + "t_attrs ADD (c_before_event_expr BLOB DEFAULT NULL, c_after_event_expr BLOB DEFAULT NULL)";
		st.executeUpdate(sql);
		st.close();
		
		log.info("Апгрейд БД до версии 28 успешно завершен.");
		
        log.info("Апгрейд БД до версии 31 ...");
        
		st = conn.createStatement();
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changescls ADD (" +
					"c_time TIMESTAMP," +
					"c_user_id INTEGER DEFAULT NULL," +
					"c_ip VARCHAR2(15) DEFAULT NULL)"
					);
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changescls MODIFY " +
					"c_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
					);
			
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changes ADD (" +
					"c_time TIMESTAMP," +
					"c_user_id INTEGER DEFAULT NULL," +
					"c_ip VARCHAR2(15) DEFAULT NULL)"
					);
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changes MODIFY " +
					"c_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
					);
			
			st.executeUpdate(
					"CREATE TABLE "+getDBPrefix()+"t_vcs_objects ("
					+ "c_id INTEGER NOT NULL,"
					+ "c_obj_id INTEGER NOT NULL,"
					+ "c_obj_uid VARCHAR2(20) NOT NULL,"
					+ "c_obj_class_id INTEGER NOT NULL,"
					+ "c_attr_id INTEGER NOT NULL,"
					+ "c_lang_id INTEGER NOT NULL,"
					+ "c_old_value BLOB,"
					+ "c_user_id INTEGER NOT NULL,"
					+ "c_ip VARCHAR2(15) NOT NULL,"
					+ "c_mod_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ "c_mod_last_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ "c_fix_start_id INTEGER,"
					+ "c_fix_end_id INTEGER,"
					+ "c_fix_comment CLOB,"
					+ "PRIMARY KEY (c_id)"
					+ ")");

			st.executeUpdate("CREATE UNIQUE INDEX vcs_obj_attr_lang_idx"
					+ " ON "+getDBPrefix()+"t_vcs_objects(c_obj_id,c_attr_id,c_lang_id,c_fix_end_id)");

			st.executeUpdate("CREATE SEQUENCE "+getDBPrefix()+"seq_vcs_objects ORDER");

			st.executeUpdate("CREATE OR REPLACE TRIGGER "+getDBPrefix()+"ti_vcs_objects "
							+ "BEFORE INSERT ON "+getDBPrefix()+"t_vcs_objects "
							+ "FOR EACH ROW "
							+ "BEGIN"
							+ " SELECT "+getDBPrefix()+"seq_vcs_objects.nextval INTO :new.c_id FROM dual; "
							+ "END;");
			st.executeUpdate(
					"CREATE TABLE "+getDBPrefix()+"t_vcs_model ("
					+ "c_id INTEGER NOT NULL,"
					+ "c_entity_id VARCHAR2(36) NOT NULL,"
					+ "c_type INTEGER NOT NULL,"
					+ "c_action INTEGER NOT NULL,"
					+ "c_old_value BLOB,"
					+ "c_user_id INTEGER NOT NULL,"
					+ "c_ip VARCHAR2(15) NOT NULL,"
					+ "c_mod_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ "c_mod_last_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					+ "c_fix_start_id INTEGER,"
					+ "c_fix_end_id INTEGER,"
					+ "c_fix_comment CLOB,"
					+ "PRIMARY KEY (c_id)"
					+ ")");
			st.executeUpdate("CREATE INDEX vcs_mdl_ant_tp_act_idx"
					+ " ON "+getDBPrefix()+"t_vcs_model(c_entity_id,c_type,c_action,c_fix_end_id)");

			st.executeUpdate("CREATE SEQUENCE "+getDBPrefix()+"seq_vcs_model ORDER");

			st.executeUpdate("CREATE OR REPLACE TRIGGER "+getDBPrefix()+"ti_vcs_model "
							+ "BEFORE INSERT ON "+getDBPrefix()+"t_vcs_model "
							+ "FOR EACH ROW "
							+ "BEGIN"
							+ " SELECT "+getDBPrefix()+"seq_vcs_model.nextval INTO :new.c_id FROM dual; "
							+ "END;");
			st.close();
		
		log.info("Апгрейд БД до версии 31 успешно завершен.");

        log.info("Апгрейд БД до версии 32 ...");
		st = conn.createStatement();
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_dif CLOB");
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_dif CLOB");
			
	        log.info("Апгрейд БД до версии 32 успешно завершен!");
			st.close();

        log.info("Апгрейд БД до версии 33 ...");
  
		st = conn.createStatement();
			st.executeUpdate(
					"CREATE TABLE " + getDBPrefix() + "t_syslog ("
					 + "c_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
					 + "c_logger NVARCHAR2(60) NOT NULL,"
					 + "c_type NVARCHAR2(20) NOT NULL,"
					 + "c_action NVARCHAR2(50) NOT NULL,"
					 + "c_user NVARCHAR2(20) NOT NULL,"
					 + "c_ip  VARCHAR2(15) NOT NULL,"
					 + "c_host VARCHAR2(20) NOT NULL,"
					 + "c_admin NUMBER(1) NOT NULL,"
					 + "c_message NVARCHAR2(2000)"
					 + ")");
			st.executeUpdate("CREATE INDEX sl_time_user_idx"
					+ " ON "+getDBPrefix()+"t_syslog(c_time,c_user)");
	        log.info("Апгрейд БД до версии 33 успешно завершен!");
			st.close();

        log.info("Апгрейд БД до версии 34 ...");
		st = conn.createStatement();
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_name NVARCHAR2(255)");
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_name NVARCHAR2(255)");
			
	        log.info("Апгрейд БД до версии 34 успешно завершен!");
			st.close();

        log.info("Апгрейд БД до версии 35 ...");
		st = conn.createStatement();
			// Создаем таблицу блокировок для методов
			createLockMethodsTable(conn);
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_repl_id INTEGER");
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_repl_id INTEGER");
			log.info("Апгрейд БД до версии 35 успешно завершен!");
			st.close();

        log.info("Апгрейд БД до версии 36 ...");
		st = conn.createStatement();
			st.executeUpdate("DROP INDEX " + getDBPrefix() + "vcs_obj_attr_lang_idx");
			st.executeUpdate("DROP INDEX " + getDBPrefix() + "vcs_mdl_ant_tp_act_idx");
			st.executeUpdate("CREATE UNIQUE INDEX vcs_obj_attr_lang_idx"
					+ " ON "+getDBPrefix()+"t_vcs_objects(c_obj_id,c_attr_id,c_lang_id,c_fix_end_id,c_repl_id)");

			st.executeUpdate("CREATE INDEX vcs_mdl_ant_tp_act_idx"
					+ " ON "+getDBPrefix()+"t_vcs_model(c_entity_id,c_type,c_action,c_fix_end_id,c_repl_id)");
			log.info("Апгрейд БД до версии 36 успешно завершен!");
			st.close();

        log.info("Апгрейд БД до версии 37 ...");
		st = conn.createStatement();
		st.executeUpdate(
				"ALTER TABLE " + getDBPrefix() + "t_syslog ADD ("
				 + "c_server_id VARCHAR2(20),"
				 + "c_object NVARCHAR2(255),"
				 + "c_process NVARCHAR2(2000)"
				 + ")");
		log.info("Апгрейд БД до версии 37 успешно завершен!");
		st.close();

		st = conn.createStatement();
		PreparedStatement ust = null;
	        log.info("Изменение типа колонки 'c_dif' таблицы 't_vcs_model' на 'BLOB'.");
	        log.info("Создание колонки 'c_dif_2'");
	        if (!isColumnExists("t_vcs_model", "c_dif_2")) {
	        	st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_dif_2 BLOB");
	        }
	        log.info("Копирование колонки 'c_dif' в 'c_dif_2'");
	        ust = conn.prepareStatement("UPDATE " + getDBPrefix() + "t_vcs_model SET c_dif_2=? WHERE c_id = ?");
			ResultSet rs = st.executeQuery("SELECT c_id,c_dif FROM " + getDBPrefix() + "t_vcs_model WHERE c_dif IS NOT NULL");
			while (rs.next()) {
				long id = rs.getLong("c_id");
				String val = (String)getValue(rs, "c_dif", PC_MEMO);
				
				if (val.length() > 0) {
					try {
						setValue(ust, 1, PC_BLOB, val.toString().getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						log.error(e, e);
					}
				} else {
					ust.setNull(1, Types.BLOB);
				}
				ust.setLong(2, id);
				ust.executeQuery();
			}
			rs.close();
	        log.info("Удаление колонки 'c_dif'");
	        if (isColumnExists("t_vcs_model", "c_dif")) {
	        	st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model DROP COLUMN c_dif");
	        }
	        log.info("Переименование колонки 'c_dif_2' в 'c_dif'");
	        if (isColumnExists("t_vcs_model", "c_dif_2")) {
	        	st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model RENAME COLUMN c_dif_2 TO c_dif");
	        }
	        log.info("OK!!!");

	        log.info("Изменение типа колонки 'c_dif' таблицы 't_vcs_objects' на 'BLOB'.");
	        log.info("Создание колонки 'c_dif_2'");
	        if (!isColumnExists("t_vcs_objects", "c_dif_2")) {
	        	st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_dif_2 BLOB");
	        }
	        log.info("Копирование колонки 'c_dif' в 'c_dif_2'");
	        ust = conn.prepareStatement("UPDATE " + getDBPrefix() + "t_vcs_objects SET c_dif_2=? WHERE c_id = ?");
			rs = st.executeQuery("SELECT c_id,c_dif FROM " + getDBPrefix() + "t_vcs_objects WHERE c_dif IS NOT NULL");
			while (rs.next()) {
				long id = rs.getLong("c_id");
				String val = (String)getValue(rs, "c_dif", PC_MEMO);
				
				if (val.length() > 0) {
					try {
						setValue(ust, 1, PC_BLOB, val.toString().getBytes("UTF-8"));
					} catch (UnsupportedEncodingException e) {
						log.error(e, e);
					}
				} else {
					ust.setNull(1, Types.BLOB);
				}
				ust.setLong(2, id);
				ust.executeQuery();
			}
	        log.info("Удаление колонки 'c_dif'");
	        if (isColumnExists("t_vcs_objects", "c_dif")) {
	        	st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects DROP COLUMN c_dif");
	        }
	        log.info("Переименование колонки 'c_dif_2' в 'c_dif'");
	        if (isColumnExists("t_vcs_objects", "c_dif_2")) {
	        	st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects RENAME COLUMN c_dif_2 TO c_dif");
	        }
	        log.info("OK!!!");
	        rs.close();
			DbUtils.closeQuietly(ust);
			DbUtils.closeQuietly(st);
        
		log.info("Апгрейд БД до версии 38 успешно завершен!");

        // Добавление колонки в таблицу t_methods
		st = conn.createStatement();
	        log.info("Добавление поля 'developer' в таблицу t_methods");
	        if (!isColumnExists("t_methods", "developer")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_methods ADD (developer INTEGER)");
	        }
			log.info("OK!!!");
			st.close();
		log.info("Апгрейд БД до версии 39 успешно завершен!");

        log.info("Апгрейд БД до версии 40 ...");
        log.info("Добавление полей c_before_del_event_expr (событие 'Перед удалением значения атрибута'), c_after_del_event_expr (событие 'После удаления значения атрибута') в таблице t_attrs.");
        log.info("Добавление полей c_before_create_obj (событие 'Перед созданием объекта'), c_after_create_obj (событие 'После создания объекта'), c_before_delete_obj (событие 'Перед удалением объекта'), c_after_delete_obj (событие 'После удаления объекта') в таблице t_classes.");
		st = conn.createStatement();
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD (c_before_del_event_expr BLOB DEFAULT NULL, c_after_del_event_expr BLOB DEFAULT NULL)");
			st.close();

		st = conn.createStatement();
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD (c_before_create_obj BLOB DEFAULT NULL, c_after_create_obj BLOB DEFAULT NULL, c_before_delete_obj BLOB DEFAULT NULL, c_after_delete_obj BLOB DEFAULT NULL)");
			st.close();

		log.info("Апгрейд БД до версии 40 успешно завершен.");

        log.info("Апгрейд БД до версии 42 ...");
		st = conn.createStatement();
			try {
		        log.info("Удаление индекса vcs_obj_attr_lang_idx с таблицы t_vcs_objects");
				st.executeUpdate("DROP INDEX vcs_obj_attr_lang_idx");
		        log.info("Индекс vcs_obj_attr_lang_idx с таблицы t_vcs_objects удален");
			} catch (Exception e) {
		        log.error("Индекс vcs_obj_attr_lang_idx с таблицы t_vcs_objects не удален!");
			}
			
			try {
		        log.info("Удаление индекса vcs_mdl_ant_tp_act_idx с таблицы t_vcs_model");
				st.executeUpdate("DROP INDEX vcs_mdl_ant_tp_act_idx");
		        log.info("Индекс vcs_mdl_ant_tp_act_idx с таблицы t_vcs_model удален");
			} catch (Exception e) {
		        log.error("Индекс vcs_mdl_ant_tp_act_idx с таблицы t_vcs_model не удален!");
			}

			try {
		        log.info("Создание уникального индекса vcs_obj_attr_lang_idx на таблице t_vcs_objects");
				st.executeUpdate("CREATE UNIQUE INDEX vcs_obj_attr_lang_idx"
						+ " ON "+getDBPrefix()+"t_vcs_objects(c_obj_id,c_attr_id,c_lang_id,c_fix_end_id)");
		        log.info("Индекс vcs_obj_attr_lang_idx на таблице t_vcs_objects создан");
			} catch (Exception e) {
		        log.error("Индекс vcs_obj_attr_lang_idx с таблице t_vcs_objects не создан!");
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
		        log.info("Создание уникального индекса vcs_mdl_ant_tp_idx на таблице t_vcs_model");
				st.executeUpdate("CREATE UNIQUE INDEX vcs_mdl_ant_tp_idx"
						+ " ON "+getDBPrefix()+"t_vcs_model(c_entity_id,c_type,c_fix_end_id)");
		        log.info("Индекс vcs_mdl_ant_tp_idx на таблице t_vcs_model создан");
			} catch (Exception e) {
		        log.error("Индекс vcs_mdl_ant_tp_idx с таблице t_vcs_model не создан!");
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
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD c_before_create_obj_tr INTEGER DEFAULT 0 NOT NULL");
				log.info("Колонка c_before_create_obj_tr добавлена.");
			} else {
				log.info("Колонка c_before_create_obj_tr уже создана.");
			}
			if (!isColumnExists("t_classes", "c_after_create_obj_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD c_after_create_obj_tr INTEGER DEFAULT 0 NOT NULL");
				log.info("Колонка c_after_create_obj_tr добавлена.");
			} else {
				log.info("Колонка c_after_create_obj_tr уже создана.");
			}
			if (!isColumnExists("t_classes", "c_before_delete_obj_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD c_before_delete_obj_tr INTEGER DEFAULT 0 NOT NULL");
				log.info("Колонка c_before_delete_obj_tr добавлена.");
			} else {
				log.info("Колонка c_before_delete_obj_tr уже создана.");
			}
			if (!isColumnExists("t_classes", "c_after_delete_obj_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD c_after_delete_obj_tr INTEGER DEFAULT 0 NOT NULL");
				log.info("Колонка c_after_delete_obj_tr добавлена.");
			} else {
				log.info("Колонка c_after_delete_obj_tr уже создана.");
			}
			
			if (!isColumnExists("t_attrs", "c_before_event_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_before_event_tr INTEGER DEFAULT 0 NOT NULL");
				log.info("Колонка c_before_event_tr добавлена.");
			} else {
				log.info("Колонка c_before_event_tr уже создана.");
			}
			if (!isColumnExists("t_attrs", "c_after_event_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_after_event_tr INTEGER DEFAULT 0 NOT NULL");
				log.info("Колонка c_after_event_tr добавлена.");
			} else {
				log.info("Колонка c_after_event_tr уже создана.");
			}
			if (!isColumnExists("t_attrs", "c_before_del_event_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_before_del_event_tr INTEGER DEFAULT 0 NOT NULL");
				log.info("Колонка c_before_del_event_tr добавлена.");
			} else {
				log.info("Колонка c_before_del_event_tr уже создана.");
			}
			if (!isColumnExists("t_attrs", "c_after_del_event_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_after_del_event_tr INTEGER DEFAULT 0 NOT NULL");
				log.info("Колонка c_after_del_event_tr добавлена.");
			} else {
				log.info("Колонка c_after_del_event_tr уже создана.");
			}
			st.close();
		log.info("Апгрейд БД до версии 43 успешно завершен.");
		
		kz.tamur.or3.util.Tname.version = 43;
	}
	
	@Override
	public String getDropTableSql(String tname) {
		return "DROP TABLE " + getDBPrefix() + "\"" + tname + "\"";
	}
	
    private void createOracleDBContext() throws SQLException {
		String call = "CREATE OR REPLACE PACKAGE " + db.getSchemeName() + ".hr_sec_ctx IS " +
				"PROCEDURE set_user_id (u NUMBER); " +
				"PROCEDURE set_user_uid (u VARCHAR2); " +
				"PROCEDURE set_user_name (u VARCHAR2); " +
				"PROCEDURE set_role_id (u NUMBER); " +
				"PROCEDURE set_role_uid (u VARCHAR2); " +
				"PROCEDURE set_role_name (u VARCHAR2); " +
				"PROCEDURE set_balans_id (u NUMBER); " +
				"PROCEDURE set_balans_uid (u VARCHAR2); " +
				"PROCEDURE set_balans_name (u VARCHAR2); " +
				"END hr_sec_ctx;";

		Statement st = null;
		try {
			st = conn.createStatement();
			st.executeUpdate(call);
		} catch (SQLException e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(st);
		}
		
		call = "CREATE OR REPLACE PACKAGE BODY " + db.getSchemeName() + ".hr_sec_ctx IS " +
				"PROCEDURE set_user_id (u NUMBER) IS " +
				"BEGIN " +
				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'id', u); " +
				"END set_user_id; " +
				"PROCEDURE set_user_uid (u VARCHAR2) IS " +
				"BEGIN " +
				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'uid', u); " +
				"END set_user_uid; " +
				"PROCEDURE set_user_name (u VARCHAR2) IS " +
				"BEGIN " +
				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'name', u); " +
				"END set_user_name; " +
				"PROCEDURE set_role_id (u NUMBER) IS " +
				"BEGIN " +
				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'rid', u); " +
				"END set_role_id; " +
				"PROCEDURE set_role_uid (u VARCHAR2) IS " +
				"BEGIN " +
				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'ruid', u); " +
				"END set_role_uid; " +
				"PROCEDURE set_role_name (u VARCHAR2) IS " +
				"BEGIN " +
				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'rname', u); " +
				"END set_role_name; " +
				"PROCEDURE set_balans_id (u NUMBER) IS " +
				"BEGIN " +
				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'bid', u); " +
				"END set_balans_id; " +
				"PROCEDURE set_balans_uid (u VARCHAR2) IS " +
				"BEGIN " +
				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'buid', u); " +
				"END set_balans_uid; " +
				"PROCEDURE set_balans_name (u VARCHAR2) IS " +
				"BEGIN " +
				"DBMS_SESSION.SET_CONTEXT('" + db.getSchemeName() + "_hr_ctx', 'bname', u); " +
				"END set_balans_name; " +
				"END hr_sec_ctx;";

		try {
			st = conn.createStatement();
			st.executeUpdate(call);
		} catch (SQLException e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(st);
		}
		
		call = "CREATE OR REPLACE FUNCTION CXVAR (D1 VARCHAR2) RETURN VARCHAR2 IS " +
				"RET_CONDITION VARCHAR2 (2000); " +
				"BEGIN " +
				"SELECT SYS_CONTEXT('" + db.getSchemeName() + "_hr_ctx', D1) INTO RET_CONDITION FROM DUAL; " +
				"RETURN RET_CONDITION; " +
				"END CXVAR;";

		try {
			st = conn.createStatement();
			st.executeUpdate(call);
		} catch (SQLException e) {
			throw e;
		} finally {
			DbUtils.closeQuietly(st);
		}
		//commit();

		try {
			st = conn.createStatement();
			st.executeUpdate("DROP CONTEXT " + db.getSchemeName() + "_hr_ctx");
		} catch (Exception e) {
			log.warn("no context " + db.getSchemeName() + "_hr_ctx");
		} finally {
    		DbUtils.closeQuietly(st);
		}

		try {
			st = conn.createStatement();
			st.executeUpdate("CREATE CONTEXT " + db.getSchemeName() + "_hr_ctx USING " + db.getSchemeName() + ".hr_sec_ctx");
		} catch (Exception e) {
			log.error(e, e);
			throw e;
		} finally {
    		DbUtils.closeQuietly(st);
		}
		
		try {
			st = conn.createStatement();
			st.executeUpdate("GRANT EXECUTE ON " + db.getSchemeName() + ".hr_sec_ctx TO public");
		} catch (Exception e) {
			log.error(e, e);
			throw e;
		} finally {
    		DbUtils.closeQuietly(st);
		}
    }

	public void upgradeTo51() throws SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 51 ...");
		Statement st = conn.createStatement();
		try {
	        log.info("Добавление колонки 'c_thread' в таблицу 't_syslog'...");
			if (!isColumnExists("t_syslog", "c_thread")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_syslog ADD c_thread VARCHAR2(1024)");
			}
			log.info("Апгрейд БД до версии 51 успешно завершен!");
		} catch (SQLException e) {
			log.error("Ошибка при апгрейде БД до версии 51!");
			throw e;
		} finally {
			st.close();
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
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_old_user_id INTEGER");
				log.info("Колонка c_old_user_id добавлена.");
			} else {
				log.info("Колонка c_old_user_id уже создана.");
			}
			
	        log.info("Удаление индекса IDX_VCS_REPL с таблицы t_vcs_objects");
			try {
				st.executeUpdate("DROP INDEX IDX_VCS_REPL");
		        log.info("Индекс IDX_VCS_REPL с таблицы t_vcs_objects удален");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_REPL с таблицы t_vcs_objects не удален!");
			}
			
	        if(isColumnExists("t_vcs_objects","c_rimport_id")) {
	        	if(isColumnExists("t_vcs_objects","c_repl_id")) {
	    	        log.info("Удаление колонки C_REPL_ID в таблицы t_vcs_objects");
	    			try {
	    				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects DROP COLUMN C_REPL_ID");
	    		        log.info("Колонка C_REPL_ID в таблицы t_vcs_objects удалена");
	    			} catch (Exception e) {
	    		        log.info("Колонка C_REPL_ID в таблицы t_vcs_objects не удалена");
	    			}
	        	}
	        }else {
		        log.info("Переименование колонки 'c_repl_id' в 'c_rimport_id' таблицы 't_vcs_objects'");
				try {
					renameColumn(isSchemeName ? db.getSchemeName() : null, "t_vcs_objects", "c_repl_id", "c_rimport_id", "INTEGER");
			        log.info("Успешно!");
				} catch (Exception e) {
			        log.error("Произошла ошибка!");
			        log.error(e, e);
				}
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
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_rexport_id INTEGER");
				log.info("Колонка c_rexport_id добавлена.");
			} else {
				log.info("Колонка c_rexport_id уже создана.");
			}

	        log.info("Создание индекса IDX_VCS_REXP на таблице t_vcs_objects");
			try {
				st.executeUpdate("CREATE INDEX IDX_VCS_REXP ON "+getDBPrefix()+"t_vcs_objects(c_rexport_id)");
		        log.info("Индекс IDX_VCS_REXP на таблице t_vcs_objects создан");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_REXP в таблице t_vcs_objects не создан!");
			}
			//t_vcs_model
			log.info("Добавление поля c_old_user_id в таблицу t_vcs_model");
			if (!isColumnExists("t_vcs_model", "c_old_user_id")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_old_user_id INTEGER");
				log.info("Колонка c_old_user_id добавлена.");
			} else {
				log.info("Колонка c_old_user_id уже создана.");
			}
			
	        log.info("Удаление индекса IDX_VCS_MODEL_REPL с таблицы t_vcs_model");
			try {
				st.executeUpdate("DROP INDEX IDX_VCS_MODEL_REPL");
		        log.info("Индекс IDX_VCS_MODEL_REPL в таблице t_vcs_model удален");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_MODEL_REPL в таблице t_vcs_model не удален!");
			}
			
	        if(isColumnExists("t_vcs_model","c_rimport_id")) {
	        	if(isColumnExists("t_vcs_model","c_repl_id")) {
	    	        log.info("Удаление колонки C_REPL_ID в таблицы t_vcs_model");
	    			try {
	    				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model DROP COLUMN C_REPL_ID");
	    		        log.info("Колонка C_REPL_ID в таблице t_vcs_model удалена");
	    			} catch (Exception e) {
	    		        log.info("Колонка C_REPL_ID в таблице t_vcs_model не удалена");
	    			}
	        	}
	        }else {
		        log.info("Переименование колонки 'c_repl_id' в 'c_rimport_id' таблицы 't_vcs_model'");
				try {
					renameColumn(isSchemeName ? db.getSchemeName() : null, "t_vcs_model", "c_repl_id", "c_rimport_id", "INTEGER");
			        log.info("Успешно!");
				} catch (Exception e) {
			        log.error("Произошла ошибка!");
			        log.error(e, e);
				}
	        }
	        log.info("Создание индекса IDX_VCS_MODEL_RIMP на таблице t_vcs_model");
			try {
				st.executeUpdate("CREATE INDEX IDX_VCS_MODEL_RIMP ON "+getDBPrefix()+"t_vcs_model(c_rimport_id)");
		        log.info("Индекс IDX_VCS_MODEL_RIMP на таблице t_vcs_model создан");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_MODEL_RIMP в таблице t_vcs_model не создан!");
			}
			
			log.info("Добавление поля c_rexport_id в таблицу t_vcs_model");
			if (!isColumnExists("t_vcs_model", "c_rexport_id")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_rexport_id INTEGER");
				log.info("Колонка c_rexport_id добавлена.");
			} else {
				log.info("Колонка c_rexport_id уже создана.");
			}

	        log.info("Создание индекса IDX_VCS_MODEL_REXP на таблице t_vcs_model");
			try {
				st.executeUpdate("CREATE INDEX IDX_VCS_MODEL_REXP ON "+getDBPrefix()+"t_vcs_model(c_rexport_id)");
		        log.info("Индекс IDX_VCS_MODEL_REXP на таблице t_vcs_model создан");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_MODEL_REXP в таблице t_vcs_model не создан!");
			}

			log.info("Апгрейд БД до версии 55 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 55!");
		} finally {
			DbUtils.closeQuietly(st);
		} 
        isUpgrading = false;
    }

	public void upgradeTo57() throws SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 57 ...");
        log.info("Добавление в таблицы t_vcs_objects,t_vcs_model колонки c_mod_confirm_time");
		Statement st = conn.createStatement();
		if (!isColumnExists("t_vcs_objects", "c_mod_confirm_time"))
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_mod_confirm_time TIMESTAMP DEFAULT null");
		if (!isColumnExists("t_vcs_model", "c_mod_confirm_time"))
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_mod_confirm_time TIMESTAMP DEFAULT null");
		st.close();
		log.info("Апгрейд БД до версии 57 успешно завершен!");
		isUpgrading = false;
    }
	
	public void upgradeTo59() throws DriverException, SQLException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 59 ...");
		log.info("Добавление поля c_access_modifier (модификатор доступа атрибута) в таблице t_attrs.");
		Statement st = conn.createStatement();
		try {
			if (!isColumnExists("t_attrs", "c_access_modifier")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_access_modifier INTEGER DEFAULT 0 NOT NULL");
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
	
	public void upgradeTo71() throws SQLException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 71 ...");
		log.info("Создание атрибута 'showSearchField', 'srch_txt', 'ifc_uid' типа boolean и string и Объекта в классе 'ConfigGlobal'.");
		log.info("Создание атрибута 'scope[]'типа объект в классе 'User'.");
		Statement st = null;
		try{
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
	        		+ "c_id INTEGER NOT NULL,"
					+ "c_search_str VARCHAR2(2000) NOT NULL,"
					+ "c_obj_uid VARCHAR2(255) NOT NULL,"
					+ "c_ext_field VARCHAR2(255),"
					+ "PRIMARY KEY(c_id))");
	        st.executeUpdate("CREATE SEQUENCE "+getDBPrefix()+"seq_search_indexes ORDER");
	        
			st.executeUpdate("CREATE UNIQUE INDEX t_search_obj_ext_idx"
					+ " ON "+getDBPrefix()+"t_search_indexes(c_obj_uid,c_ext_field)");

			st.executeUpdate("CREATE INDEX t_search_obj_idx"
					+ " ON "+getDBPrefix()+"t_search_indexes(c_obj_uid)");

			st.executeUpdate("CREATE INDEX t_search_ext_idx"
					+ " ON "+getDBPrefix()+"t_search_indexes(c_ext_field)");

			st.executeUpdate("CREATE OR REPLACE TRIGGER "+getDBPrefix()+"ti_search_indexes "
							+ "BEFORE INSERT ON "+getDBPrefix()+"t_search_indexes "
							+ "FOR EACH ROW "
							+ "BEGIN"
							+ " SELECT "+getDBPrefix()+"seq_search_indexes.nextval INTO :new.c_id FROM dual; "
							+ "END;");
	        
	        log.info("Апгрейд БД до версии 71 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 71!");
		} finally {
			DbUtils.closeQuietly(st);
		}
		isUpgrading = false;
	}

	protected boolean renameColumn(String schemeName, String tableName, String oldColName, String newColName, String sqlType) throws SQLException {
		StringBuilder sql = new StringBuilder("ALTER TABLE ");
		
		if (schemeName != null && schemeName.length() > 0)
			sql.append(schemeName).append(".");
			
		sql.append(tableName)
				.append(" RENAME COLUMN ").append(oldColName)
				.append(" TO ").append(newColName);
		
		log.info("SQL Query...\n " + sql);
		
		QueryRunner qr = new QueryRunner(true);
		qr.update(conn, sql.toString());
		
		return true;
	}
	@Override
	public List<String> showDbLocks() {
		List<String> res=new ArrayList<>();
		String sql="select distinct o.object_name,"
				+ " sh.username || '(' || sh.sid || ',' || sh.serial# || ')' Holder,"
				+ " sh.osuser,sw.username || '(' || sw.sid || ',' || sw.serial# || ')' Waiter,"
				+ " decode(lh.lmode, 1,'null', 2,'row share',3,'row exclusive',4,'share', 5,'share row exclusive', 6,'exclusive') Lock_Type	"
				+ " from v$session   sw, v$lock  lw, all_objects o, v$session   sh,v$lock      lh "
				+ " where lh.id1 = o.object_id     and lh.id1 = lw.id1   and sh.sid = lh.sid  and sw.sid = lw.sid  and sh.lockwait is null  and sw.lockwait is not null "
				+ " and lh.type = 'TM' and lw.type = 'TM'";
		try(Statement st=conn.createStatement();
			ResultSet rs=st.executeQuery(sql)){
			while(rs.next()) {
				res.add("object_name:"+rs.getString("object_name")+";Holder:"+rs.getString("Holder")+";Waiter:"+rs.getString("Waiter")+"Lock_Type:"+rs.getString("Lock_Type"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			res.add(e.getMessage());
		}
		return res;
	}
	@Override
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
							asql = colName+"_"+lang_index;
						else
							asql += ", "+colName+"_"+lang_index;
						sqlMap.put(tblName, asql);
					}else if(!isDrop && !isColumnExists(tblName, colName+"_"+lang_index)) {
						asql=sqlMap.get(tblName);
						if(asql==null || "".equals(asql))
							asql = colName+"_"+lang_index+" "+getSqlTypeName(type_id, 0);
						else
							asql += ", "+colName+"_"+lang_index+" "+getSqlTypeName(type_id, 0);
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
        					String tsql=null;
	        				try(Statement st=conn_.createStatement()) {
	        					if(isDrop)
	        						tsql = "ALTER TABLE DROP ("+keyTblName+sqlMap.get(keyTblName)+")";
	        					else
	        						tsql = "ALTER TABLE ADD ("+keyTblName+sqlMap.get(keyTblName)+")";
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
		String res = "Добавлены мультиязычные колонки у "+sqlMap.keySet().size()+" таблиц";
		return res;
		
	}
	public int getRealSizeColumn(KrnAttribute attr) {
		int size=attr.size;
		String t_name=getClassTableName(attr.classId);
		String c_name=getAttrTableName(attr);
	   	String selSql="SELECT DATA_LENGTH "
	  	   		+ "FROM USER_TAB_COLUMNS "
	  	   		+ "WHERE DATA_TYPE = 'VARCHAR2' "
	  	   		+ "AND TABLE_NAME=? "
	  	   		+ "AND COLUMN_NAME=? "
	  	   		+ (db.getSchemeName()!=null?"AND TABLE_SCHEMA=?":"");
	   	try(PreparedStatement pst = conn.prepareStatement(selSql);){
	   		pst.setString(1, t_name);
	   		pst.setString(2, c_name);
	   		if(db.getSchemeName()!=null)
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
}