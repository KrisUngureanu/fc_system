package kz.tamur.ods.postgre;

import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_CLASS;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_ATTRIBUTE;
import static com.cifs.or2.kernel.ModelChange.ACTION_CREATE;
import static com.cifs.or2.kernel.ModelChange.ACTION_MODIFY;
import static com.cifs.or2.kernel.ModelChange.ACTION_DELETE;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_METHOD;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
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
import java.util.Vector;

import kz.tamur.DriverException;
import kz.tamur.common.ErrorCodes;
import kz.tamur.comps.Constants;
import kz.tamur.comps.TriggerInfo;
import kz.tamur.ods.AttrRequest;
import kz.tamur.ods.AttrRequestCache;
import kz.tamur.ods.Driver2;
import kz.tamur.ods.Lock;
import kz.tamur.ods.Value;
import kz.tamur.ods.mssql.MsSqlDriver3;
import kz.tamur.ods.mysql.MySqlDriver3;
import kz.tamur.ods.mysql.ObjectRsh;
import kz.tamur.ods.mysql.MySqlDriver3.KrnObjectComparator;
import kz.tamur.ods.sql92.AttrResultSetHandler;
import kz.tamur.ods.sql92.AttributeRsh;
import kz.tamur.ods.sql92.ClassResultSetHandler;
import kz.tamur.or3.util.FGACRule;
import kz.tamur.or3.util.FGARule;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.or3ee.common.AttrChangeListener;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.admin.ServerMessage;
import kz.tamur.server.indexer.Indexer;
import kz.tamur.util.Funcs;
import kz.tamur.util.KrnUtil;
import kz.tamur.util.MapMap;
import kz.tamur.util.Pair;
import kz.tamur.util.XmlUtil;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import com.cifs.or2.kernel.AttrChange;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnIndex;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnVcsChange;
import com.cifs.or2.server.ServerUserSession;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.db.Database;
import com.cifs.or2.server.orlang.SrvOrLang;
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
public class PgSqlDriver extends MySqlDriver3 {
	
	//private PGConnection pgConn;
	private boolean isFirstTryToConnect = true;
	private static final int MAX_OBJECT_NAME_LENGTH = 30;
	private static final String DOMAIN="DOMAIN";
//	private static final String IDX_CONTEXT="INDEXTYPE IS CTXSYS.CONTEXT PARAMETERS ('SYNC (ON COMMIT) memory 50m')";
	private final String pg_pass;

	public PgSqlDriver(Database db, String dsName, UserSession us) throws DriverException {
		super(db, dsName, us);
		/*try {
			pgConn = conn.unwrap(PGConnection.class);
		} catch (SQLException e) {
			throw convertException(e);
		}*/
		String pg_pass = null;
		try {
			String pg_pass_location;
			if (System.getProperty("pg_pass") != null) {
				pg_pass_location = System.getProperty("pg_pass");
			} else {
				pg_pass_location = "./pg_pass.txt";
			}
			pg_pass = new String(Files.readAllBytes(Paths.get(pg_pass_location)), "UTF-8");
		} catch (Exception e) {
			pg_pass = "longsecretencryptionkey";
		} finally {
			this.pg_pass = pg_pass;
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
			return Types.BOOLEAN;
		} else if (typeId == PC_FLOAT) {
			return Types.DOUBLE;
		} else if (typeId == PC_MEMO) {
			return Types.LONGVARCHAR;
		} else if (typeId == PC_BLOB) {
			return Types.LONGVARBINARY;
		}
		return Types.BIGINT;
	}
	@Override
	protected String getSqlTypeName(long typeId,int sz) {
		return getSqlTypeName(typeId,sz,false);
	}
	
	protected String getSqlTypeName(long typeId,int sz,boolean isFullText) {
		if (typeId == PC_STRING) {
			return "VARCHAR(" + (sz > 0 ? sz : 2000) + ")";
		} else if (typeId == PC_INTEGER) {
			return "BIGINT";
		} else if (typeId == PC_DATE) {
			return "DATE";
		} else if (typeId == PC_TIME) {
			return "TIMESTAMP";
		} else if (typeId == PC_BOOL) {
			return "BOOLEAN DEFAULT FALSE";
		} else if (typeId == PC_FLOAT) {
			return "FLOAT";
		} else if (typeId == PC_MEMO) {
			return "TEXT";
		} else if (typeId == PC_BLOB) {
			return "BYTEA";
		}
		return "BIGINT";
	}

	protected void install() throws DriverException {
		try {
			Statement st = conn.createStatement();
			// ResultSet set = null;
			String qStr="SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES where UPPER(TABLE_NAME) = 'T_IDS' AND UPPER(TABLE_SCHEMA)='"+db.getSchemeName().toUpperCase(Constants.OK)+"'";
			if(isSchemeName){ 
				qStr="SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES where UPPER(TABLE_NAME) = 'T_IDS' "
						+ "AND UPPER(TABLE_SCHEMA)='"+db.getSchemeName().toUpperCase(Constants.OK)+"'";
			}
			ResultSet set = st.executeQuery(qStr);//TODO EDIT
			boolean installed = set.next();
			//installed=true;
			set.close();
			String dbImportFromExtDb = System.getProperty("dbImportFromExtDb");
			int instStep=1;
			if(installed && dbImportFromExtDb!=null) {
				qStr="SELECT C_LAST_ID FROM T_IDS WHERE C_NAME='installed'";
				set = st.executeQuery(qStr);
				if(set.next()) {
					instStep=set.getInt(1);
				}
			}else if(!installed) {
				instStep=0;
			}
			if (!installed || instStep!=1) {
				if(instStep==0) {
					qStr="SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES where  UPPER(table_schema)='"+db.getSchemeName().toUpperCase(Constants.OK)+"'";
					set = st.executeQuery(qStr);
					List<String> names=new ArrayList<>();
					while(set.next()) {
						names.add(set.getString(1));
					}
					for(String name:names) {
						st.executeUpdate("drop table "+name+" cascade");
						conn.commit();
					}
					names.clear();
					conn.commit();
					qStr="SELECT SEQUENCE_NAME FROM INFORMATION_SCHEMA.SEQUENCES where UPPER(sequence_schema)='"+db.getSchemeName().toUpperCase(Constants.OK)+"'";
					set = st.executeQuery(qStr);
					while(set.next()) {
						names.add(set.getString(1));
					}
					for(String name:names) {
						st.executeUpdate("drop sequence "+name+" cascade");
						conn.commit();
					}
					conn.commit();
			        isUpgrading = true;
			        isInstallDb=true;
					version = 59;
					// Таблица классов
					st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_classes ("
							+ "c_id BIGSERIAL,"
	                        + "c_cuid VARCHAR(36) NOT NULL,"
							+ "c_name VARCHAR(255) NOT NULL,"
							+ "c_tname VARCHAR(30) DEFAULT NULL," 
							+ "c_parent_id BIGINT NOT NULL,"
							+ "c_is_repl BOOLEAN,"
							+ "c_before_create_obj BYTEA DEFAULT NULL,"
							+ "c_after_create_obj BYTEA DEFAULT NULL,"
							+ "c_before_delete_obj BYTEA DEFAULT NULL,"
							+ "c_after_delete_obj BYTEA DEFAULT NULL,"
							+ "c_before_create_obj_tr INTEGER DEFAULT 0 NOT NULL,"
							+ "c_after_create_obj_tr INTEGER DEFAULT 0 NOT NULL,"
							+ "c_before_delete_obj_tr INTEGER DEFAULT 0 NOT NULL,"
							+ "c_after_delete_obj_tr INTEGER DEFAULT 0 NOT NULL,"
							+ "c_comment TEXT,"
	                        + "c_mod INTEGER NOT NULL,"
							+ "c_is_load BOOLEAN,"
							+ " PRIMARY KEY(c_id))");
	
					// Таблица иерархии классов
					st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_clinks ("
									+ "c_parent_id BIGINT NOT NULL,"
									+ "c_child_id BIGINT NOT NULL)");
	
					// Таблица атрибутов
					st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_attrs ("
									+ "c_id BIGSERIAL,"
									+ "c_auid VARCHAR(36) NOT NULL,"
									+ "c_class_id BIGINT NOT NULL,"
									+ "c_name VARCHAR(255) NOT NULL,"
									+ "c_tname VARCHAR(30) DEFAULT NULL," 
									+ "c_type_id BIGINT NOT NULL,"
									+ "c_col_type INTEGER NOT NULL,"
									+ "c_is_unique BOOLEAN,"
									+ "c_is_indexed BOOLEAN,"
									+ "c_is_multilingual BOOLEAN,"
									+ "c_is_repl BOOLEAN,"
									+ "c_size INTEGER NOT NULL,"
									+ "c_flags INTEGER NOT NULL,"
									+ "c_rattr_id BIGINT,"
									+ "c_sattr_id BIGINT,"
									+ "c_sdesc BOOLEAN,"
									+ "c_before_event_expr BYTEA DEFAULT NULL,"
									+ "c_after_event_expr BYTEA DEFAULT NULL,"
									+ "c_before_del_event_expr BYTEA DEFAULT NULL,"
									+ "c_after_del_event_expr BYTEA DEFAULT NULL,"
									+ "c_before_event_tr INTEGER DEFAULT 0 NOT NULL,"
									+ "c_after_event_tr INTEGER DEFAULT 0 NOT NULL,"
									+ "c_before_del_event_tr INTEGER DEFAULT 0 NOT NULL,"
									+ "c_after_del_event_tr INTEGER DEFAULT 0 NOT NULL,"
									+ "c_access_modifier INTEGER DEFAULT 0 NOT NULL,"
									+ "c_comment TEXT,"
									+ "c_is_load BOOLEAN,"
									+ "c_is_encrypt BOOLEAN DEFAULT FALSE,"
									+ " PRIMARY KEY(c_id))");

					st.executeUpdate("ALTER SEQUENCE "+getDBPrefix()+"t_attrs_c_id_seq RESTART WITH 101");
	
					// Таблица обратных атрибутов
					st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_rattrs ("
									+ "c_attr_id BIGINT NOT NULL,"
									+ "c_rattr_id BIGINT NOT NULL)");
					st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_methods ("
									+ "c_muid VARCHAR(36) NOT NULL,"
									+ "c_class_id BIGINT NOT NULL,"
									+ "developer BIGINT,"
									+ "c_name VARCHAR(255) NOT NULL,"
									+ "c_is_cmethod BOOLEAN,"
									+ "c_expr BYTEA,"
									+ "c_comment TEXT,"
									+ "PRIMARY KEY(c_muid))");
					st.executeUpdate("CREATE UNIQUE INDEX method_name_idx"
							+ " ON "+getDBPrefix()+"t_methods(c_class_id,c_name)");
	
					dbPreInit();
	
					// Таблица ct99
					st.executeUpdate("CREATE TABLE "+getDBPrefix()+"ct99 ("
									+ "c_obj_id BIGSERIAL,"
									+ "c_uid VARCHAR(20),"
									+ "c_class_id BIGINT NOT NULL)");
	
					st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_changescls ("
							+ "c_id BIGSERIAL,"
							+ "c_type BIGINT NOT NULL,"
							+ "c_action INTEGER NOT NULL,"
							+ "c_entity_id VARCHAR(36) NOT NULL,"
							+ "c_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
							+ "c_user_id BIGINT DEFAULT NULL,"
							+ "c_ip VARCHAR(15) DEFAULT NULL,"
							+ "PRIMARY KEY(c_id))");
	
					// Таблица изменения объектов
					st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_changes ("
									+ "c_id BIGSERIAL,"
									+ "c_class_id BIGINT NOT NULL,"
									+ "c_object_id BIGINT NOT NULL,"
									+ "c_object_uid VARCHAR(20) DEFAULT NULL,"
									+ "c_attr_id BIGINT NOT NULL,"
									+ "c_lang_id INTEGER NOT NULL,"
									+ "c_tr_id BIGINT NOT NULL,"
									+ "c_is_repl BOOLEAN,"
									+ "c_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
									+ "c_user_id BIGINT DEFAULT NULL,"
									+ "c_ip VARCHAR(15) DEFAULT NULL,"
									+ "PRIMARY KEY(c_id))");
					st.executeUpdate("CREATE INDEX ch_tr_repl_idx"
							+ " ON "+getDBPrefix()+"t_changes(c_tr_id,c_is_repl)");
					st.executeUpdate("CREATE INDEX ch_tr_attr_idx"
							+ " ON "+getDBPrefix()+"t_changes(c_tr_id,c_attr_id)");
					st.executeUpdate("CREATE INDEX ch_tr_obj_idx"
							+ " ON "+getDBPrefix()+"t_changes(c_tr_id,c_object_id)");
					st.executeUpdate("CREATE INDEX ch_class_id_idx"
							+ " ON "+getDBPrefix()+"t_changes(c_class_id,c_id)");
					st.executeUpdate("CREATE INDEX ch_attr_id_idx"
							+ " ON "+getDBPrefix()+"t_changes(c_attr_id)");
	
	
					st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_ids ("
							+ "c_name VARCHAR(255) NOT NULL,"
							+ "c_last_id BIGINT NOT NULL,"
							+ "PRIMARY KEY(c_name))");
	
	
					// Таблица версионности объектов
					st.executeUpdate(
							"CREATE TABLE "+getDBPrefix()+"t_vcs_objects ("
							+ "c_id BIGSERIAL,"
							+ "c_name VARCHAR(255),"
							+ "c_obj_id BIGINT NOT NULL,"
							+ "c_obj_uid VARCHAR(20) NOT NULL,"
							+ "c_obj_class_id BIGINT NOT NULL,"
							+ "c_attr_id BIGINT NOT NULL,"
							+ "c_lang_id INTEGER NOT NULL,"
							+ "c_old_value BYTEA,"
							+ "c_user_id BIGINT NOT NULL,"
							+ "c_old_user_id BIGINT,"
							+ "c_rimport_id BIGINT,"
							+ "c_rexport_id BIGINT,"
							+ "c_ip VARCHAR(15) NOT NULL,"
							+ "c_mod_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
							+ "c_mod_last_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
							+ "c_mod_confirm_time TIMESTAMP DEFAULT null,"
							+ "c_fix_start_id BIGINT,"
							+ "c_fix_end_id BIGINT,"
							+ "c_dif BYTEA,"
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
							+ "c_id BIGSERIAL,"
							+ "c_name VARCHAR(255),"
							+ "c_entity_id VARCHAR(36) NOT NULL,"
							+ "c_type INTEGER NOT NULL,"
							+ "c_action INTEGER NOT NULL,"
							+ "c_old_value BYTEA,"
							+ "c_user_id BIGINT NOT NULL,"
							+ "c_old_user_id BIGINT,"
							+ "c_rimport_id BIGINT,"
							+ "c_rexport_id BIGINT,"
							+ "c_ip VARCHAR(15) NOT NULL,"
							+ "c_mod_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
							+ "c_mod_last_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
							+ "c_mod_confirm_time TIMESTAMP DEFAULT null,"
							+ "c_fix_start_id BIGINT,"
							+ "c_fix_end_id BIGINT,"
							+ "c_dif BYTEA,"
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
					 + "c_admin BOOLEAN,"
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
						" c_id  BIGSERIAL," +
						" c_uid VARCHAR(36) NOT NULL," +
						" c_class_id BIGINT NOT NULL," +
						" c_is_multilingual BOOLEAN," +
						" PRIMARY KEY(c_id))");
					
					st.executeUpdate("" +
						"CREATE TABLE "+getDBPrefix()+"t_indexkeys(" +
						" c_index_id BIGINT NOT NULL," +
						" c_attr_id BIGINT NOT NULL," +
						" c_keyno INTEGER NOT NULL," +
						" c_is_del INTEGER DEFAULT 0)");
	
					st.executeUpdate("" +
							"CREATE TABLE "+getDBPrefix()+"t_msg(" +
							" c_msg VARCHAR(255))" +
							"");
					
					conn.commit();
				}
				// Создание базы с нуля или импорт данных из существующей базы
				String dbImportDir = System.getProperty("dbImportDir");
				if (dbImportDir!= null) {
						sysLangCount = Integer.parseInt(System.getProperty("sysLangCount"));
						dbImport(dbImportDir, System.getProperty("separator"));
				} else if (dbImportFromExtDb!= null) {
					sysLangCount = Integer.parseInt(System.getProperty("sysLangCount"));
					dbImportFromExtDb(dbImportFromExtDb, instStep);
				} else {
					long db_id = dbInit();
					PreparedStatement pst = conn
							.prepareStatement("INSERT INTO "+getDBPrefix()+"t_ids (c_name,c_last_id) VALUES (?,?)");
					// Идентификатор БД
					pst.setString(1, "dbase_id");
					pst.setLong(2, db_id);
					pst.executeUpdate();
                    // Текущая транзакция
                    pst.setString(1, "transaction_id");
                    pst.setLong(2, 1);
                    pst.executeUpdate();
                    // Признак доступа редактирования
                    pst.setString(1, "mode");
                    pst.setLong(2, 1);
                    pst.executeUpdate();
					// Версия БД
					pst.setString(1, "version");
					pst.setLong(2, 59);
					pst.executeUpdate();
					pst.close();
				}
				//Создание индексов на таблицы
				//Таблица классов
				if(!isConstraintExists("t_classes", "t_classes_c_name_key"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_classes ADD UNIQUE (c_name)");
				if(!isConstraintExists("t_classes", "t_classes_c_tname_key"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_classes ADD UNIQUE (c_tname)");
				if(!isConstraintExists("t_classes", "t_classes_c_cuid_key"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_classes ADD UNIQUE (c_cuid)");
				if(!isIndexExists("t_classes", "cls_parent_idx"))
					st.executeUpdate("CREATE INDEX cls_parent_idx ON "+getDBPrefix()+"t_classes(c_parent_id)");
				
				if(!isIndexExists("t_clinks", "t_clinks_c_child_idx"))
					st.executeUpdate("CREATE INDEX t_clinks_c_child_idx ON "+getDBPrefix()+"t_clinks(c_child_id)");
				if(!isConstraintExists("t_clinks", "t_clinks_c_parent_id_fkey"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_clinks ADD FOREIGN KEY(c_parent_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE");
				if(!isConstraintExists("t_clinks", "t_clinks_c_child_id_fkey"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_clinks ADD FOREIGN KEY(c_child_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE");
				if(!isConstraintExists("t_clinks", "t_clinks_pkey"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_clinks ADD PRIMARY KEY(c_parent_id,c_child_id)");
				
				if(!isConstraintExists("t_methods", "t_methods_c_class_id_fkey"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_methods ADD FOREIGN KEY(c_class_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE");
				
				if(!isConstraintExists("t_indexes", "t_indexes_c_class_id_fkey"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_indexes ADD FOREIGN KEY(c_class_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE");
				if(!isConstraintExists("t_indexkeys", "t_indexkeys_c_index_id_fkey"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_indexkeys ADD FOREIGN KEY(c_index_id) REFERENCES "+getDBPrefix()+"t_indexes(c_id) ON DELETE CASCADE");
				if(!isConstraintExists("t_indexkeys", "t_indexkeys_c_attr_id_fkey"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_indexkeys ADD FOREIGN KEY(c_attr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id) ON DELETE CASCADE");
				
				//Таблица атрибутов
				if(!isConstraintExists("t_attrs", "t_attrs_c_auid_key"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_attrs ADD UNIQUE(c_auid)");
				if(!isIndexExists("t_attrs", "attr_name_idx"))
					st.executeUpdate("CREATE UNIQUE INDEX attr_name_idx ON "+getDBPrefix()+"t_attrs(c_class_id,c_name)");
				if(!isIndexExists("t_attrs", "t_attrs_c_tname_idx"))
					st.executeUpdate("CREATE INDEX t_attrs_c_tname_idx ON "+getDBPrefix()+"t_attrs(c_tname)");
				if(!isIndexExists("t_attrs", "t_attrs_c_rattr_id_idx"))
					st.executeUpdate("CREATE INDEX t_attrs_c_rattr_id_idx ON "+getDBPrefix()+"t_attrs(c_rattr_id)");
				if(!isIndexExists("t_attrs", "t_attrs_c_sattr_id_idx"))
					st.executeUpdate("CREATE INDEX t_attrs_c_sattr_id_idx ON "+getDBPrefix()+"t_attrs(c_sattr_id)");
				if(!isIndexExists("t_attrs", "t_attrs_c_type_id_idx"))
					st.executeUpdate("CREATE INDEX t_attrs_c_type_id_idx ON "+getDBPrefix()+"t_attrs(c_type_id)");

				if(!isConstraintExists("t_attrs", "t_attrs_c_class_id_fkey"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_attrs ADD FOREIGN KEY(c_class_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE");
				if(!isConstraintExists("t_attrs", "t_attrs_c_type_id_fkey"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_attrs ADD FOREIGN KEY(c_type_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE");
				if(!isConstraintExists("t_attrs", "t_attrs_c_rattr_id_fkey"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_attrs ADD FOREIGN KEY(c_rattr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id)");
				if(!isConstraintExists("t_attrs", "t_attrs_c_sattr_id_fkey"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_attrs ADD FOREIGN KEY(c_sattr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id)");
				
				if(!isConstraintExists("t_rattrs", "t_rattrs_pkey"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_rattrs ADD PRIMARY KEY(c_attr_id,c_rattr_id)");
				if(!isConstraintExists("t_rattrs", "t_rattrs_c_attr_id_fkey"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_rattrs ADD FOREIGN KEY(c_attr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id) ON DELETE CASCADE");
				if(!isConstraintExists("t_rattrs", "t_rattrs_c_rattr_id_fkey"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_rattrs ADD FOREIGN KEY(c_rattr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id) ON DELETE CASCADE");
				if(!isIndexExists("t_attrs", "t_attrs_c_type_id_idx"))
					st.executeUpdate("CREATE INDEX t_rattrs_c_rattr_id_idx ON "+getDBPrefix()+"t_rattrs(c_rattr_id)");
				
				if(!isConstraintExists("t_changes", "t_changes_c_attr_id_fkey"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"t_changes ADD FOREIGN KEY(c_attr_id) REFERENCES "+getDBPrefix()+"t_attrs(c_id) ON DELETE CASCADE");
				
				
				//Таблица ct99
				if(!isConstraintExists("ct99", "ct99_pkey"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"ct99 ADD PRIMARY KEY(c_obj_id)");
				if(!isConstraintExists("ct99", "ct99_c_class_id_fkey"))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+"ct99 ADD FOREIGN KEY(c_class_id) REFERENCES "+getDBPrefix()+"t_classes(c_id) ON DELETE CASCADE");
				if(!isIndexExists("ct99", "uid99idx"))
					st.executeUpdate("CREATE UNIQUE INDEX uid99idx ON "+getDBPrefix()+"ct99(c_uid)");
				if(!isIndexExists("ct99", "cls99idx"))
					st.executeUpdate("CREATE INDEX cls99idx ON "+getDBPrefix()+"ct99(c_class_id)");
				
				// Индексы для таблиц объектов классов
				
				List<KrnClass> clss = new ArrayList<KrnClass>();
		        db.getSubClasses(99, true, clss);
		        
		        conn.commit();
		        
		        for (KrnClass cls : clss) {
		        	if (cls.id > 99 && (cls.modifier & 1) == 0) {
			        	String ctName = getClassTableName(cls, true);
			        	String ctName2 = getClassTableName(cls, false);
			        	
			        	if(!isConstraintExists(ctName2, ctName2 + "_pkey"))
							st.executeUpdate("ALTER TABLE " + ctName + " ADD PRIMARY KEY(C_OBJ_ID,C_TR_ID)");
			        	
						if(!isConstraintExists(ctName2, ctName2 + "_c_class_id_fkey")) {
							st.executeUpdate("ALTER TABLE " + ctName + " ADD FOREIGN KEY(C_CLASS_ID) REFERENCES "+getDBPrefix()+"t_classes(C_ID) ON DELETE CASCADE");
							st.executeUpdate("CREATE INDEX " + ctName2 + "_CLS_IDX ON " + ctName + "(C_CLASS_ID)");
						}
						
						if(!isIndexExists(ctName2, ctName2 + "_TR_IDX"))
							st.executeUpdate("CREATE INDEX " + ctName2 + "_TR_IDX" + " ON " + ctName + "(C_TR_ID)");
						if(!isIndexExists(ctName2, ctName2 + "_UID_IDX"))
							st.executeUpdate("CREATE UNIQUE INDEX " + ctName2 + "_UID_IDX" + " ON " + ctName + "(c_uid,c_tr_id)");

			        	List<KrnAttribute> attrs = db.getAttributesByClassId(cls.id, false);
			        	for (KrnAttribute attr : attrs) {
		        			boolean isArray = attr.collectionType == COLLECTION_ARRAY;
		        			boolean isSet = attr.collectionType == COLLECTION_SET;

		        			if (attr.rAttrId == 0) {
		        				if (!isSet && !isArray) {
		        					if (attr.typeClassId > 10) {
			        					String cname = getColumnName(attr);
			        					//sString refTname = getClassTableNameComp(attr.typeClassId);
			        					if(!isIndexExists(ctName2, "FK" + attr.id)) {
			        						st.executeUpdate("CREATE INDEX FK" + attr.id + " ON " + ctName + "(" + cname + ")");
			        					}
		        					}
		        				} else {
		        					String atName = getAttrTableName(attr);
		        					String atName2 = kz.tamur.or3.util.Tname.getAttrTableName(attr);

		    			        	if(!isConstraintExists(atName2, atName2 + "_pkey")) {
		    			        		StringBuilder sb = new StringBuilder("ALTER TABLE ");
		    			        		sb.append(atName);
		    			        		sb.append(" ADD PRIMARY KEY(c_obj_id,c_tr_id");
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
			        					sb.append(",c_del)");
			        					st.executeUpdate(sb.toString());
		    			        	}
		    			        	if(!isConstraintExists(atName2, atName2 + "_c_obj_id_c_tr_id_fkey")) {
		    			        		StringBuilder sb = new StringBuilder("ALTER TABLE ");
		    			        		sb.append(atName);
			        					sb.append(" ADD FOREIGN KEY (c_obj_id,c_tr_id) REFERENCES ");
			        					sb.append(getClassTableName(attr.classId));
			        					sb.append(" (c_obj_id,c_tr_id) ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED");
		    			        	}
		        					if (attr.typeClassId > 10) {
			        					String cname = getColumnName(attr);
			    			        	String fkName = getAttrFKName(attr);
			        					if(!isIndexExists(atName2, fkName)) {
			        						st.executeUpdate("CREATE INDEX " + fkName + " ON " + atName + "(" + cname + ")");
			        					}
		        					}
		        				}
			        		}
			        	}
		        	}
		        	conn.commit();
		        }
				
				//
				//установка максимальных значений для автоинкремента
                // Текущая транзакция
				set=st.executeQuery("SELECT c_last_id FROM "+getDBPrefix()+"t_ids WHERE c_name='transaction_id'");
				long transaction_id=1;
				if(set.next())
					transaction_id = set.getLong(1)+1;
				st.executeUpdate("CREATE SEQUENCE "+getDBPrefix()+"seq_transaction START WITH "+transaction_id);
                // Идентификатор класса
				set=st.executeQuery("SELECT MAX(c_id) FROM "+getDBPrefix()+"t_classes");
				if(set.next())
					st.executeUpdate("ALTER SEQUENCE "+getDBPrefix()+"t_classes_c_id_seq RESTART WITH "+(set.getLong(1)+1));
                // Идентификатор атрибута
				set=st.executeQuery("SELECT MAX(c_id) FROM "+getDBPrefix()+"t_attrs");
				if(set.next())
					st.executeUpdate("ALTER SEQUENCE "+getDBPrefix()+"t_attrs_c_id_seq RESTART WITH "+(set.getLong(1)+1));

                // Идентификатор объекта
				set=st.executeQuery("SELECT MAX(c_obj_id) FROM "+getDBPrefix()+"ct99");
				if(set.next())
					st.executeUpdate("ALTER SEQUENCE "+getDBPrefix()+"ct99_c_obj_id_seq RESTART WITH "+(set.getLong(1)+1));

                // Идентификатор изменений
				set=st.executeQuery("SELECT MAX(c_id) FROM "+getDBPrefix()+"t_changes");
				if(set.next())
					st.executeUpdate("ALTER SEQUENCE "+getDBPrefix()+"t_changes_c_id_seq RESTART WITH "+(set.getLong(1)+1));

				set=st.executeQuery("SELECT MAX(c_id) FROM "+getDBPrefix()+"t_changescls");
				if(set.next())
					st.executeUpdate("ALTER SEQUENCE "+getDBPrefix()+"t_changescls_c_id_seq RESTART WITH "+(set.getLong(1)+1));
				
				set=st.executeQuery("SELECT MAX(c_id) FROM "+getDBPrefix()+"t_vcs_model");
				if(set.next())
					st.executeUpdate("ALTER SEQUENCE "+getDBPrefix()+"t_vcs_model_c_id_seq RESTART WITH "+(set.getLong(1)+1));
				
				set=st.executeQuery("SELECT MAX(c_id) FROM "+getDBPrefix()+"t_vcs_objects");
				if(set.next())
					st.executeUpdate("ALTER SEQUENCE "+getDBPrefix()+"t_vcs_objects_c_id_seq RESTART WITH "+(set.getLong(1)+1));

				conn.commit();
				
		        isInstallDb=false;
		        isUpgrading = false;
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
                        st.executeUpdate("ALTER TABLE " + ctName + " noparallel");
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
                            st.executeUpdate("ALTER TABLE " + atName + " noparallel");
                        }
                    }

                }
            }
			st.close();
			commit();
		} catch (SQLException e) {
			log.error(e, e);
			throw new DriverException(e);
		}
	}
    public void deleteClassIndex(KrnClass cls) throws DriverException {
		if(cls.id<=99) return;
		//Удаление индексов для облегчения заливки
		String tablename = getClassTableName(cls, true);
		String fkname=tablename+"_C_CLASS_ID_FKEY";
		String pkname=tablename+"_PKEY";
		String idxname="CT" + cls.id + "_TR_IDX";
		try (Statement st = conn.createStatement()){
			if(isConstraintExists(tablename, pkname))
				st.executeUpdate("ALTER TABLE "+getDBPrefix()+tablename+" DROP CONSTRAINT "+pkname);
			if(isIndexExists(tablename, fkname))
				st.executeUpdate("ALTER TABLE "+getDBPrefix()+tablename+" DROP CONSTRAINT "+fkname);
			if(isIndexExists(tablename, idxname))
				st.executeUpdate("ALTER TABLE "+getDBPrefix()+tablename+" DROP CONSTRAINT "+idxname);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//Удаляем индексы для индексируемых атрибутов класса
        List<KrnAttribute> attrs = db.getAttributesByClassId(cls.id, false);
        for(int a = 0; a < attrs.size(); a++) {
            KrnAttribute attr = attrs.get(a);
            if (attr.collectionType== COLLECTION_NONE && attr.rAttrId==0 &&  attr.isIndexed) {
        		boolean isIndexExists=false;
        		try (Statement st = conn.createStatement()){
	        		if (attr.isMultilingual) {
	        			List<KrnObject> langs = getSystemLangs();
	        			for (KrnObject lang : langs) {
	        				idxname = getIdxName(attr, lang.id);
	        				if(isIndexExists(tablename, idxname)) {
	        					st.executeUpdate("ALTER TABLE "+getDBPrefix()+tablename+" DROP CONSTRAINT "+idxname);
	        				}
	        			}
	        		} else {
	        			idxname = getIdxName(attr, 0);
	        			if(isIndexExists(tablename, idxname))
	    					st.executeUpdate("ALTER TABLE "+getDBPrefix()+tablename+" DROP CONSTRAINT "+idxname);
	        		}
        		} catch (SQLException e) {
        			e.printStackTrace();
        		}finally {
        			try {
        				conn.commit();
        			} catch (SQLException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        		}
            }
        }
	}

    public void deleteAttributeIndex(KrnAttribute attr) throws DriverException {
		String tablename = (attr.collectionType == COLLECTION_ARRAY || attr.collectionType == COLLECTION_SET) ? getAttrTableName(attr)
				: getClassTableName(attr.classId);
		try (Statement st = conn.createStatement()){
			if (attr.collectionType!= COLLECTION_NONE) {
				String pkname=tablename+"_pkey";
				String fkname=getAttrFKName(attr);
				if(isConstraintExists(tablename, pkname)) {
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+tablename+" DROP CONSTRAINT "+pkname);
				}
				if(isConstraintExists(tablename, fkname)) {
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+tablename+" DROP CONSTRAINT "+fkname);
				}
			} 
			if (attr.isMultilingual) {
				List<KrnObject> langs = getSystemLangs();
				for (KrnObject lang : langs) {
					String idxname = getIdxName(attr, lang.id);
					if(isIndexExists(tablename, idxname)) {
    					st.executeUpdate("ALTER TABLE "+getDBPrefix()+tablename+" DROP CONSTRAINT "+idxname);
					}
				}
			} else {
				String idxname = getIdxName(attr, 0);
				if(isIndexExists(tablename, idxname))
					st.executeUpdate("ALTER TABLE "+getDBPrefix()+tablename+" DROP CONSTRAINT "+idxname);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				conn.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
    
    @Override
    public void loadAttributeTableFromExtDb(KrnAttribute attr, Connection extConn) throws DriverException {
		super.loadAttributeTableFromExtDb(attr, extConn);
		String tname = (attr.collectionType == COLLECTION_ARRAY || attr.collectionType == COLLECTION_SET) ? getAttrTableName(attr, false)
				: getClassTableName(attr.classId, false);
		if (attr.collectionType!= COLLECTION_NONE) {
			StringBuilder sb = new StringBuilder();
			if(!isConstraintExists(tname, tname+"_pkey")) {
				sb.append("ALTER TABLE "+getDBPrefix()+tname+" ADD PRIMARY KEY(c_obj_id,c_tr_id");
				if (attr.collectionType==COLLECTION_ARRAY) {
					sb.append(",c_index");
				}
				if (attr.collectionType==COLLECTION_SET) {
					sb.append(",");
					if (attr.isMultilingual)
						sb.append(getColumnName(attr, 1));
					else
						sb.append(getColumnName(attr, 0));
				}
				sb.append(",c_del)");
				try(Statement st = conn.createStatement()) {
					st.executeUpdate(sb.toString());
				} catch (SQLException e) {
					e.printStackTrace();
				}finally {
					try {
						conn.commit();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if(!isConstraintExists(tname, getAttrFKName(attr))) {
				sb = new StringBuilder();
				sb.append("ALTER TABLE "+getDBPrefix()+tname+" ADD CONSTRAINT ").append(getAttrFKName(attr));
				sb.append(" FOREIGN KEY (c_obj_id,c_tr_id) REFERENCES ");
				sb.append(getClassTableName(attr.classId));
				sb.append(" (c_obj_id,c_tr_id) ON DELETE CASCADE ");
				sb.append("DEFERRABLE INITIALLY DEFERRED");
				try(Statement st = conn.createStatement()) {
					st.executeUpdate(sb.toString());
				} catch (SQLException e) {
					e.printStackTrace();
				}finally {
					try {
						conn.commit();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} 
		//Создаем индекс после заливки
		boolean isIndexExists=false;
		if (attr.isMultilingual) {
			List<KrnObject> langs = getSystemLangs();
			for (KrnObject lang : langs) {
				String idxName = getIdxName(attr, lang.id);
				if(isIndexExists(tname, idxName)) {
					isIndexExists=true;
					break;
				}
			}
		} else {
			String idxName = getIdxName(attr, 0);
			if(isIndexExists(tname, idxName))
				isIndexExists=true;
		}
		if (attr.isIndexed && !isIndexExists) {
			try {
				updateIndex(attr, true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}


    @Override
    public void loadClassTableFromExtDb(KrnClass cls, Connection extConn) throws DriverException {
		super.loadClassTableFromExtDb(cls, extConn);
		if(cls.id<=99) return;
		//Создаем индекс после заливки
		String tablename = getClassTableName(cls, false);
		try (Statement st = conn.createStatement()){
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+tablename+" ADD PRIMARY KEY(C_OBJ_ID,C_TR_ID)");
			st.executeUpdate("ALTER TABLE "+getDBPrefix()+tablename+" ADD FOREIGN KEY(C_CLASS_ID) REFERENCES "+getDBPrefix()+"t_classes(C_ID) ON DELETE CASCADE");
			st.executeUpdate("CREATE INDEX " + "CT" + cls.id + "_TR_IDX" + " ON "
					+ tablename + "(C_TR_ID)");
		} catch (SQLException e) {
			log.error(e, e);
		} finally {
			try {
				conn.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				log.error(e, e);
			}
		}
		//Создаем индексы для индексируемых атрибутов класса
        List<KrnAttribute> attrs = db.getAttributesByClassId(cls.id, false);
        for(int a = 0; a < attrs.size(); a++) {
            KrnAttribute attr = attrs.get(a);
            if (attr.collectionType== COLLECTION_NONE && attr.rAttrId==0 &&  attr.isIndexed) {
        		boolean isIndexExists=false;
        		if (attr.isMultilingual) {
        			List<KrnObject> langs = getSystemLangs();
        			for (KrnObject lang : langs) {
        				String idxName = getIdxName(attr, lang.id);
        				if(isIndexExists(tablename, idxName)) {
        					isIndexExists=true;
        					break;
        				}
        			}
        		} else {
        			String idxName = getIdxName(attr, 0);
        			if(isIndexExists(tablename, idxName))
        				isIndexExists=true;
        		}
        		if (!isIndexExists) {
	    			try {
	    				updateIndex(attr, true);
	    			} catch (SQLException e) {
	    				e.printStackTrace();
	    			}
        		}
            }
        }
	}
	@Override
	public KrnClass createClass(String name, long parentId, boolean isRepl, int mod, long id, String uid, boolean log, String tname) throws DriverException {
		try {
			if (tname != null && tname.trim().length() == 0){
				tname = null;
			} else if (isNameNotAllowed(tname)) {
				tname = "T_" + tname.toUpperCase();
			}

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
				if (isVersion(kz.tamur.or3.util.Tname.TnameVersionBD)) {
					sql = "INSERT INTO "+getDBPrefix()+"T_CLASSES (C_NAME, C_PARENT_ID, C_IS_REPL, C_MOD, C_CUID, C_TNAME) VALUES (?,?,?,?,?,?)";
				} else {
					sql = "INSERT INTO "+getDBPrefix()+"T_CLASSES (C_NAME, C_PARENT_ID, C_IS_REPL, C_MOD, C_CUID) VALUES (?,?,?,?,?)";
				}
				PreparedStatement pst = conn.prepareStatement(sql);
				pst.setString(1, name);
				pst.setLong(2, parentId);
				pst.setBoolean(3, isRepl);
				pst.setInt(4, mod);
				pst.setString(5, uid);
				if (isVersion(kz.tamur.or3.util.Tname.TnameVersionBD)) {
					pst.setString(6, tname);
				}
				pst.executeUpdate();
				pst.close();
				id = getLastIndexId();
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
				String tablename = getClassTableName(cls, true)/*.toUpperCase(Constants.OK)*/;
				Statement st = conn.createStatement();
				st.executeUpdate("CREATE TABLE "
								+ tablename
								+ " (C_OBJ_ID BIGINT NOT NULL,"
								+ "C_UID VARCHAR(20),"
								+ "C_CLASS_ID BIGINT NOT NULL,"
								+ "C_TR_ID BIGINT NOT NULL,"
								+ "C_IS_DEL INTEGER DEFAULT 0)");
				// Это было только для импорта из mysql или oracle. Для создания пустой базы нужно создавать сразу.
				if(!isInstallDb) {// Индекс не создаем при инсталляции создадим потом
					st.executeUpdate("ALTER TABLE " + tablename + " ADD PRIMARY KEY(C_OBJ_ID,C_TR_ID)");
					st.executeUpdate("ALTER TABLE " + tablename + " ADD FOREIGN KEY(C_CLASS_ID) REFERENCES "+getDBPrefix()+"t_classes(C_ID) ON DELETE CASCADE");
					st.executeUpdate("CREATE INDEX " + "CT" + cls.id + "_TR_IDX" + " ON "
							+ tablename + "(C_TR_ID)");
				}
				st.close();
			}
			// Создаем запись в журнале изменения модели
			if (log && !isInstallDb) {
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
		} else if (isNameNotAllowed(tname)) {
			tname = "T_" + tname.toUpperCase();
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
				if (isVersion(kz.tamur.or3.util.Tname.TnameVersionBD)){
					pst.setString(++Nval, tname);
				}
				if (isVersion(AttrAccessModVersionBD)){
					pst.setInt(++Nval, accessModifier);
				}
				if (isVersion(EncryptColumnsVersionBD)){
		            pst.setBoolean(++Nval, isEncrypt);
				}

//				this.log.info("Создание атрибута. " + sql + " Paramters. " + pst.getParameterMetaData());
				pst.executeUpdate();
				if (id == -1) {
					id = getLastAttributeId();
				}
			} catch (SQLException e) {
			    this.log.error("Атрибут \"" + name + "\" не создан. UUID:" + uid+";"+e.getMessage());
			} finally {
				pst.close();
			}
			
			// Создаем объект KrnAttribute
			KrnAttribute attr = KrnUtil.createAttribute(uid, id, name, classId, typeId,
					collectionType, isUnique, isMultilingual, isIndexed, size,
					flags, isRepl, rAttrId, sAttrId, sDesc, tname, null, null, null, null, 0, 0, 0, 0, accessModifier, isEncrypt);

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
				sb.append(" ADD COLUMN ");
				if (attr.isMultilingual) {
					// Создаем колонки для каждого системного языка
					int langCount = sysLangCount > 0 ? sysLangCount : getSystemLangs().size();
					for (int i = 0; i < langCount; i++) {
						if (i > 0){
							sb.append(", ADD COLUMN ");
						}
						sb.append(getColumnName(attr, i + 1)+" "+getSqlTypeName(attr));
					}
				} else {
					sb.append(getColumnName(attr)+" "+getSqlTypeName(attr));
				}
				sb.append("");
				st.executeUpdate(sb.toString());
				st.close();
			} else {
				// Создаем таблицу для атрибута
				String atName = getAttrTableName(attr);
				StringBuilder sb = new StringBuilder();
				sb.append("CREATE TABLE ");
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
				if (!isInstallDb) {
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
					sb.append(",c_del)");
					sb.append(", CONSTRAINT ").append(getAttrFKName(attr));
					sb.append(" FOREIGN KEY (c_obj_id,c_tr_id) REFERENCES ");
					sb.append(getClassTableName(attr.classId));
					sb.append(" (c_obj_id,c_tr_id) ON DELETE CASCADE ");
					sb.append("DEFERRABLE INITIALLY DEFERRED");
				}
				sb.append(")");
				Statement st = conn.createStatement();
				log.debug("Создание атрибута. SQL:" + sb.toString());
				st.executeUpdate(sb.toString());
				st.close();
			}

			// Создаем индекс.Индекс не создаем при инсталляции создадим потом
			if (attr.isIndexed && !isInstallDb) {
				updateIndex(attr, true);
			}
		} else if (isArray) {
			// Добавляем колонку в таблицу типа
			//TODO Рассмотреть необходимость в индексе
			String ctName = getClassTableName(attr.typeClassId);
			String cmiName = getRevIndexColumnName(attr.id);
			Statement st = conn.createStatement();
			log.debug("Создание атрибута. SQL: ALTER TABLE " + ctName
					+ " ADD " + cmiName + " BIGINT NOT NULL");
			st.executeUpdate(
				"ALTER TABLE " + ctName	+ " ADD " + cmiName + " BIGINT NOT NULL");
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
						if(isConstraintExists(tname, fkname)) {
							try {
								qr.update(conn, getDropForeignKeySql(tname, fkname));
							} catch (SQLException e) {
								this.log.warn("Constraint " + fkname + " not found!!!");
							}
						}else {
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
							PgSqlDriver.this.log.warn("Constraint " + fkname + " not found.");
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
			Long lid= objId;
			try {
				if (!sepClass) {
					if (objId > 0) {
						qr.update(conn, "INSERT INTO " + getDBPrefix() + "ct99 (c_obj_id,c_class_id) VALUES (?,?)", new Object[] {lid, classId});
					}else {
						qr.update(conn, "INSERT INTO " + getDBPrefix() + "ct99 (c_class_id) VALUES (?)", new Object[] {classId});
						lid = getLastObjectId();
					}
					if (uid == null) {
						uid = getBaseId() + "." + lid;
					}
					qr.update(conn,"UPDATE "+getClassTableName(99)+" SET c_uid=? WHERE c_obj_id=?",	new Object[] { uid, lid });
				}
			} catch (SQLException e) {
				if (e.getErrorCode() == 28115) {
					log.error(lastSql);
					throw new DriverException("Вставка запрещена правилами FGAC", ErrorCodes.ERROR_FGAC_NOT_ALLOW);
				}
				throw e;
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
			if(!isInstallDb) {
				logVcsDataChanges(obj, 1, 0, null,trId);
				if(replLog)
					logDataChanges(obj, 1, 0, trId);
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
			log.error(lastSql);
			throw convertException(e);
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
			if (attr.isEncrypt) {
				if (attr.typeClassId == PC_BLOB) {
					select.append(",pgp_sym_decrypt_bytea(mt.").append(cname).append(", pg_pass.key)").append(" AS ").append(cname);
				} else {
					select.append(",pgp_sym_decrypt(mt.").append(cname).append("::bytea, pg_pass.key)").append(" AS ").append(cname);
				}
			} else {
	            select.append(",mt.").append(cname);
			}
        } else {
			if (attr.isEncrypt) {
				if (attr.typeClassId == PC_BLOB) {
					select.append(",pgp_sym_decrypt_bytea(pt.").append(cname).append(", pg_pass.key)").append(" AS ").append(cname);
				} else {
					select.append(",pgp_sym_decrypt(pt.").append(cname).append("::bytea, pg_pass.key)").append(" AS ").append(cname);
				}
			} else {
	            select.append(",pt.").append(cname);
			}
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
        
        StringBuilder sql = new StringBuilder("WITH pg_pass as (SELECT ? AS key) SELECT ").append(valTbl?"DISTINCT ":"").append(select)
        			.append(" FROM pg_pass, ").append(from)
        			.append(" WHERE ").append(where);
        if (order.length() > 0) {
            sql.append(" ORDER BY ").append(order);
        }
        if (limit[0] > 0)
        	sql = addLimit(sql, limit[0], 0);
        
        // Map для хранения обработанных строк
        Map<Long, Value> revValues = new HashMap<Long, Value>();
        
        ltcnt++;
        
        log.debug("getValuesSql: " + sql);
        
        PreparedStatement pst = conn.prepareStatement(sql.toString());
    	pst.setString(1, pg_pass);

        for (int i = 1; i < ltcnt; i++)
        	pst.setLong(i + 1, tid);
        for (int i = 0; i < rtcnt; i++)
        	pst.setLong(ltcnt + 2 + i, tid);
        for (long objId : objIds) {
			if (filteredIds != null && !filteredIds.contains(objId)) continue;
            pst.setLong(ltcnt + 1, objId);
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
				String cname_v = cname;
				if (a.isEncrypt) {
					if (a.typeClassId == PC_BLOB) {
						selectSql.append(",pgp_sym_decrypt_bytea(").append(ptalias).append(".").append(cname).append(", pg_pass.key)").append(" AS ").append(ptalias).append("_").append(cname);
					} else {
						selectSql.append(",pgp_sym_decrypt(").append(ptalias).append(".").append(cname).append("::bytea, pg_pass.key)").append(" AS ").append(ptalias).append("_").append(cname);
					}
				} else {
					selectSql.append(",").append(ptalias).append(".").append(cname_v).append(" AS ").append(ptalias).append("_").append(cname);
				}
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
		f_obj.sql_head = "WITH pg_pass AS (SELECT '" + pg_pass + "' AS key)";
		f_obj.sql_head += " SELECT DISTINCT o.c_obj_id, o.c_class_id, o.c_uid";
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
		f_obj.sql_head += " FROM pg_pass, ";
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
	
	protected String getSql(Element xml, long typeAttr, String colName,KrnAttribute attr, Map<String, String> sqlParMap) {
		if (typeAttr == CID_STRING && attr.isEncrypt)
			return getSqlString(xml, colName, attr);
		else
			return super.getSql(xml, typeAttr, colName, attr, sqlParMap);

	}
	
	protected String getSqlString(Element xml, String colName, KrnAttribute attr) {
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
		String oldColName = colName;
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
					return checkEncrypt(sql_mean_, oldColName, attr);
				} else {
					val_ = " [" + rightStr_ + "Like%%" + colName + (mandatory_?"+":"")+likeEscape+ "]";
				}
			} else {
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
					return checkEncrypt(sql_mean_, oldColName, attr);
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
					return checkEncrypt(sql_mean_, oldColName, attr);
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
			return checkEncrypt(sql_mean_, oldColName, attr);
		} else if (operator_.equals("не существует")) {
			sql_mean_ += colName + " IS NULL";
			if (rightStr_ != null && rightStr_.length() > 1)
				sql_mean_ += "[" + rightStr_ + "]";
			return checkEncrypt(sql_mean_, oldColName, attr);
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
        } else if(!"".equals(linkPar_)){
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
			} else {
				sql_mean_ += colName + " " + val_.trim();
			}
		}
		return checkEncrypt(sql_mean_, oldColName, attr);
	}
	
	protected String checkEncrypt(String sql, String colName, KrnAttribute attr) {
		if (attr.isEncrypt) {
			String[] colNames = colName.split(",");
			colName = "";
			for(String cn : colNames){
				if (attr.typeClassId == PC_BLOB) {
					sql = sql.replace(cn, "pgp_sym_decrypt_bytea(" + cn + ", pg_pass.key)");
				} else {
					sql = sql.replace(cn, "pgp_sym_decrypt(" + cn + "::bytea, pg_pass.key)");
				}
			}
		}
		return sql;
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
			pst = conn.prepareStatement("UPDATE " + getClassTableName(attr.classId) + " SET " + cname + "=? WHERE c_obj_id IN (" + idsString + ") AND c_tr_id=?");
			if (attr.typeClassId == PC_BLOB && db.inJcrRepository(attr.id)) {
				value = db.putRepositoryData(attr.id, objectsIds.get(0), trId, (byte[])value);
            } else if (attr.typeClassId == PC_MEMO && db.inJcrRepository(attr.id)) {
				value = db.putRepositoryData(attr.id, objectsIds.get(0), trId, (String)value);
			}
			setValue(pst, 1, attr.typeClassId, value);
			pst.setLong(2, trId);
			pst.executeUpdate();
			pst.close();
		} catch (SQLException e) {
			log.error(e, e);
			throw convertException(e);
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
		ResultSet rs = null;
		try {
			logVcsDataChanges(obj, attr.id, langId, value,trId);
			trId = openTransaction(obj, trId, false);

			if (attr.typeClassId == PC_BLOB && db.inJcrRepository(attr.id)) {
				value = db.putRepositoryData(attr.id, obj.id, trId, Funcs.toByteArray(value));
            } else if (attr.typeClassId == PC_MEMO && db.inJcrRepository(attr.id)) {
				value = db.putRepositoryData(attr.id, obj.id, trId, (String)value);
			}

			// Обработка внешнего BLOB. TODO Пока сохраняем и в таблице до полного тестирования механизма.
			if(!isUpgrading)
				setExternalBlob(obj, attr, index, langId, trId, value);
			
			String mtName = getAttrTableName(attr);
			String cname = getColumnName(attr, langId);
			boolean isArray = attr.collectionType == COLLECTION_ARRAY;
			boolean isSet = attr.collectionType == COLLECTION_SET;
			if (insert && isArray) {
				shiftIndexes(obj.id, trId, attr, langId, index, true);
			}
			if (isSet || isArray) {
				StringBuffer updSql = new StringBuffer("UPDATE ")
				.append(mtName).append(" SET ").append(cname).append(
						"=?");
				// Для набора восстанавливаем запись если она была помечена
				// как удаленная
				if (isSet)
					updSql.append(",c_del=0");
		
				updSql.append(" WHERE c_obj_id=? AND c_tr_id=?");
		
				if (isArray) {
					updSql.append(" AND c_index=").append(index);
				}
				if (isSet) {
					updSql.append(" AND ").append(cname).append("=?");
				}
				// Для набора пытаемся обновить существующую запись в mt-таблице
				if (isArray)
					updSql.append(" AND c_del=0");
				
				pst = conn.prepareStatement(updSql.toString());
				setValue(pst, 1, attr.typeClassId, value);
				pst.setLong(2, obj.id);
				pst.setLong(3, trId);
				if (isSet) {
					if (oldValue == null) oldValue = value;
					setValue(pst, 4, attr.typeClassId, oldValue);
				}
				int res = pst.executeUpdate();
				pst.close();
				if (res == 0) {
					sql = new StringBuilder("INSERT INTO " + mtName
							+ " (c_obj_id,c_tr_id," + cname);
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
					int c = pst.executeUpdate();
					pst.close();
				}
			} else {
				sql = new StringBuilder("SELECT COUNT(c_obj_id) FROM ").append(getClassTableName(attr.classId))
						.append(" WHERE c_obj_id=? AND c_tr_id=?");
				pst = conn.prepareStatement(sql.toString());
				pst.setLong(1, obj.id);
				pst.setLong(2, trId);
				rs = pst.executeQuery();
				if (rs.next() && rs.getInt(1) > 0) {
					rs.close();
					pst.close();
					sql = new StringBuilder("WITH pg_pass AS (SELECT ? AS key) UPDATE ").append(getClassTableName(attr.classId))
							.append(" SET ").append(cname);
					if (attr.isEncrypt) {
						if (attr.typeClassId  == PC_BLOB) {
							sql.append("=pgp_sym_encrypt_bytea(?, pg_pass.key)");
						} else {
							sql.append("=pgp_sym_encrypt(?, pg_pass.key)::varchar");
						}
					} else {
						sql.append("=?");
					}
					sql.append(" FROM pg_pass WHERE c_obj_id=? AND c_tr_id=?");
					int i = 1;
					pst = conn.prepareStatement(sql.toString());
					pst.setString(1, pg_pass);
					setValue(pst, 2, attr.typeClassId, value);
					pst.setLong(3, obj.id);
					pst.setLong(4, trId);
					int c = pst.executeUpdate();
					pst.close();
				
					//if (c < 1)
					//	throw new DriverException("Изменение запрещено правилами FGAC", ErrorCodes.ERROR_FGAC_NOT_ALLOW);
				}
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
			DbUtils.closeQuietly(rs);
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
				attr.accessModifierType, attr.isEncrypt);
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
					+ "c_rattr_id=?,c_sattr_id=?,c_sdesc=?,c_access_modifier=?,c_is_encrypt=? WHERE c_id=?";
			
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
			pst.setInt(13, accessModifier);
			pst.setBoolean(14, isEncrypt);
			pst.setLong(15, id);
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
			if (tnameChanged) attr.tname = tname;

			// Если изменилась обязательность, то обновляем триггеры
			if (attr.isMandatory() != isMandatory)
					updateTriggers(attr.classId);
			
			// Шифруем / дешифруем колонку
			if (attr.isEncrypt != isEncrypt) {
				String cName= getColumnName(attr);
                String tName=getClassTableName(attr.classId);
                if (isEncrypt) {
                	if (attr.typeClassId == PC_BLOB) {
                    	sql = "UPDATE " + getDBPrefix() + tName + " SET " + cName + "=pgp_sym_encrypt_bytea(" + cName + ", ?)";
                	} else {
                		sql = "UPDATE " + getDBPrefix() + tName + " SET " + cName + "=pgp_sym_encrypt(" + cName + ",?)::varchar";
                	}
                } else {
                	if (attr.typeClassId == PC_BLOB) {
                		sql = "UPDATE " + getDBPrefix() + tName + " SET " + cName + "=pgp_sym_decrypt_bytea(" + cName + ",?)";
                	} else {
                		sql = "UPDATE " + getDBPrefix() + tName + " SET " + cName + "=pgp_sym_decrypt(" + cName + "::bytea,?)";
                	}
                }
                try (PreparedStatement encPst = conn.prepareStatement(sql)) {
                	encPst.setString(1, pg_pass);
	                encPst.executeUpdate();
                } catch (SQLException e) {
        			throw convertException(e);
                }
				attr.isEncrypt = isEncrypt;
			}

			// Добавляем обновленный атрибут в кэш
			db.addAttribute(attr, false);

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
				sql = "UPDATE "+getDBPrefix()+"t_attrs SET c_tname='" + newName + "' WHERE c_id=" + attr.id;
				qr.update(conn, sql);
			
				result = true;
		} catch (Exception e) {
			log.error("ERROR renameAttrTable: " + e.getMessage());
			//new DriverException("TNAME");
			return false;
		}
		
		return result;
	}
	
	@Override
	protected void createTriggers(KrnClass cls, List<KrnAttribute> attrs)
	throws SQLException, DriverException {
		// Для мультиязычных атрибутов пока проверяем обязательность только
		// на 1-ом системном языке
		long langId = getSystemLangs().get(0).id;

		String ctName = getClassTableName(cls.id);
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
			String cmName = getColumnName(a, langId);
			
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
			sql += "\t\t\t msg:=CONCAT('{01,',CONCAT(TO_CHAR(:NEW.c_obj_id),'," + a.id + "," + Funcs.translite(cls.name + "." + a.name)+ "}'));\n";
			sql += "\t\t\t raise_application_error (-20000,msg);\n";
//			sql += "\t\t\tINSERT INTO "+getDBPrefix()+"t_msg VALUES (msg);\n";
			sql += "\t\tEND IF;\n";
			
			usql += "\tIF ((:NEW.c_tr_id=0 AND :OLD.c_tr_id!=0) OR :OLD." + cmName + " IS NOT NULL) AND ((:NEW." + cmName + " IS NULL";
			if (a.typeClassId == PC_STRING) // string - проверяем на пустую строку
				usql += " OR LENGTH(:NEW." + cmName + ")=0";
			usql += ")";

			if (sqlExcept != null) usql += sqlExcept;

			usql += ") THEN\n";
			usql += "\t\t msg:=CONCAT('{01,',CONCAT(TO_CHAR(:NEW.c_obj_id),'," + a.id + "," + Funcs.translite(cls.name + "." + a.name)+ "}'));\n";
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

	protected void updateIndex(KrnAttribute attr, boolean isIndexed) throws SQLException, DriverException {
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
											?""/*IDX_CONTEXT*/:""));
						}
					} else {
						String idxName = getIdxName(attr, 0);
						String cname = getColumnName(attr, 0);
						st.executeUpdate("CREATE INDEX " + idxName + " ON " + tname
								+ "(" + cname + ")"
										+ (attr.isFullText() 
												&& (attr.typeClassId == PC_STRING || attr.typeClassId == PC_MEMO || attr.typeClassId == PC_MMEMO)
												?""/*IDX_CONTEXT*/:""));
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
			else if (value instanceof Boolean)
				pst.setLong(colIndex, ((Boolean) value) ? 1 : 0);
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
			Blob blob = conn.createBlob();
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
			Clob clob = conn.createClob();
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
				Clob clob = conn.createClob();
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
				Blob blob = conn.createBlob();
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
		return "ALTER TABLE " + Funcs.sanitizeSQL(tname) + " DROP CONSTRAINT " + Funcs.sanitizeSQL(idxName);
	}

	protected String getDropForeignKeySql(String tname, String fkname) {
		return "ALTER TABLE " + Funcs.sanitizeSQL(tname) + " DROP CONSTRAINT " + Funcs.sanitizeSQL(fkname);
	}

	protected String getDropColumnSql(String tname, String cname) {
		return "ALTER TABLE " + Funcs.sanitizeSQL(tname) + " DROP COLUMN " + Funcs.sanitizeSQL(cname);
	}

	public String getMemo(ResultSet rs, String name) throws SQLException {
		return rs.getString(name);
		/*Clob clobData = rs.getClob(name);
		if (clobData != null) {
			Reader reader = clobData.getCharacterStream();
			StringWriter writer = new StringWriter(1024);
			try {
				char[] buffer = new char[4096];
				for(int i = 0; (i = reader.read(buffer)) != -1; writer.write(buffer, 0, i));
			} catch (IOException e) {
				log.error(e, e);
			}
			return Funcs.normalizeInput(writer.toString());
		}
		return null;
		*/
	}

	public String getMemo(ResultSet rs, int index) throws SQLException {
		return rs.getString(index);
		/*Clob clobData = rs.getClob(index);
		if (clobData != null) {
			Reader reader = clobData.getCharacterStream();
			StringWriter writer = new StringWriter(1024);
			try {
				char[] buffer = new char[4096];
				for(int i = 0; (i = reader.read(buffer)) != -1; writer.write(buffer, 0, i));
			} catch (IOException e) {
				log.error(e, e);
			}
			return Funcs.normalizeInput(writer.toString());
		}
		return null;*/
	}

	protected void setMemo(PreparedStatement pst, int colIndex, String value)
			throws SQLException {
		if (value != null) {
			value = Funcs.normalizeInput(value);
			pst.setString(colIndex, value);
			/*StringReader sr = new StringReader(value);
			pst.setCharacterStream(colIndex, sr, value.length());
			*/
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
	public void setAttributeComment(String attrUid, String comment, boolean log) throws DriverException {
		PreparedStatement pstSetAttributeComment = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		String sql = "";
		QueryRunner qr = new QueryRunner(true);
		String comment_pg = comment.replaceAll("'", "''");
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
						boolean isMultilingual = rs.getBoolean(5);
						if (collectionType != 0){
							if (tname == null || tname.trim().length() == 0){
								tableName = getAttrTableName(a);
							} else {
								tableName = tname;
							}
							sql = "COMMENT ON TABLE " + tableName + " IS '" + comment_pg + "'";
							qr = new QueryRunner(true);
						} else {
							tableName = getClassTableName(cls_id);
						}
						
						String columnName = getColumnName(a);
						if (isMultilingual) {
							if(!isInstallDb) {
								List<KrnObject> langs = getSystemLangs();
								for (KrnObject lang : langs) {
									sql = "COMMENT ON COLUMN " + tableName + "." + columnName + "_" + getSystemLangIndex(lang.id) + " IS '" + comment_pg+ "'";
									//log.info("SQL Query...\n " + sql);
									qr.update(conn, sql);
								}
							}else {
								for (int i=1;i<=sysLangCount;i++) {
									sql = "COMMENT ON COLUMN " + tableName + "." + columnName + "_" + i + " IS '" + comment_pg + "'";
									//log.info("SQL Query...\n " + sql);
									qr.update(conn, sql);
								}
							}
						} else {
							sql = "COMMENT ON COLUMN " + tableName + "." + columnName + " IS '" + comment_pg + "'";
							//log.info("SQL Query...\n " + sql);
							qr.update(conn, sql);
						}
					}
				}
			}
			if (log)
				logModelChanges(ENTITY_TYPE_ATTRIBUTE, ACTION_MODIFY, attrUid, conn);
		} catch (SQLException e) {
			this.log.error("ERROR:attrUid:"+attrUid+"; Attribute COMMENT : " + e.getMessage());
			throw convertException(e);
		} finally {
			DbUtils.closeQuietly(pstSetAttributeComment);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(st);
		}
	}
	@Override
	protected long upgradeImpl(long v) throws SQLException, DriverException {
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
	public String getSessionId(Connection conn) {
		Statement st = null;
		ResultSet rs = null;
		String SID="";
		try {
			st = conn.createStatement();
			rs = st.executeQuery("SELECT pg_backend_pid()");
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
	public String getDropTableSql(String tname) {
		return "DROP TABLE " + getDBPrefix() + "\"" + tname + "\"";
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
	protected long getLastInsertId() throws SQLException {
		long res;
		Statement st = conn.createStatement();
		ResultSet set = st.executeQuery("SELECT LASTVAL()");
		if (set.next()) {
			res = set.getLong(1);
		} else {
			throw new SQLException("Failed to get last ID");
		}
		set.close();
		st.close();
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
					String sql="";
					langs = (List<KrnObject>) qr.query(
							conn,sql=
							"SELECT * FROM "+getClassTableName(langCls.id) +
							" WHERE c_tr_id=0 AND "+getColumnName(sysAttr) + "=true ORDER BY c_obj_id",
							rsh);
					sysLangIds.put(dsName, langs);
				} catch (SQLException e) {
					throw convertException(e);
				}
			}
			return langs;
		}
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
				sql.append("WITH pg_pass as (SELECT ? AS key) ");
				fromSql.insert(0, "pg_pass, ");
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
		        pst.setString(1, pg_pass);
		        if (tid > 0) {
			        for (int i = 0; i < cache.joinTableCount; i++) {
		        		pst.setLong(i + 2, tid);
		        		objIdPosition++;
			        }

			        for (int i = 0; i < cache.whereTableCount; i++) {
			        	if (objIds == null)
			        		pst.setLong(i + objIdPosition + 1, tid);
			        	else
			        		pst.setLong(i + objIdPosition + 2, tid);
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
			            pst.setLong(objIdPosition + 1, objId);
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
	public KrnObject getClassObjectByUid(long clsId, String uid, long trId, boolean isDirty) throws DriverException {
		KrnObject res = null;
		StringBuilder sql = new StringBuilder("SELECT c_obj_id,c_class_id FROM "+getClassTableName(clsId));
		sql.append(" WHERE c_uid=?");
		if (trId > 0)
			sql.append(" AND c_tr_id IN (0,?)");
		else if (trId == 0)
			sql.append(" AND c_tr_id = ?");
		if (!isDirty && trId == 0)
			sql.append(" AND c_is_del=?");
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement(sql.toString());
			int i = 2;
			if (trId != -1)
				pst.setLong(i++, trId);
			if (!isDirty && trId == 0)
				pst.setInt(i, 0);
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
	
	@Override
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
				pst.setInt(i, 0);
			
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
	protected void createVcsDataRecord(KrnObject obj, KrnAttribute attr, long langId, Object value,long trId) throws DriverException, SQLException {
		//Сначала создаем записи для атрибутов удаляемого объекта
		if(attr.id==2){
			List<KrnAttribute> attrs=db.getVcsAttributes(obj.classId);
			List<KrnObject> langs=getSystemLangs();
			for(KrnAttribute oattr:attrs){
				if(oattr.isMultilingual){
					for(KrnObject lang:langs){
						createVcsDataRecord(obj, oattr, lang.id, null,trId);
					}
				}else
					createVcsDataRecord(obj, oattr, 0, null,trId);
					
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
		PreparedStatement pst = conn.prepareStatement(Funcs.sanitizeSQL(sql));
		try {
			pst.setLong(1, obj.id);
			pst.setString(2, obj.uid);
			pst.setLong(3, obj.classId);
			pst.setLong(4, attr.id);
			pst.setLong(5, langId);
			
			StringBuilder diff = new StringBuilder();
			byte[] pv=packValue(obj, attr, langId, value, diff,trId);
			if(pv==null)
				pst.setNull(6, Types.BINARY);
			else
				setValue(pst, 6, PC_BLOB, pv);
			if (diff.length() > 0) {
				try {
					setValue(pst, 7, PC_BLOB, diff.toString().getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					log.error(e, e);
				}
			} else {
				pst.setNull(7, Types.BINARY);
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
			byte[] pv=packModelValue(oldObj, type, action, newExpr);
			if(pv==null)
				pst.setNull(4,Types.BINARY);
			setValue(pst, 4, PC_BLOB, pv);
			
			String diff = getDiff(type,oldObj, newObj, newExpr, action);
			
			if (diff.length() > 0) {
				try {
					setValue(pst, 5, PC_BLOB, diff.toString().getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					log.error(e, e);
				}
			} else {
				pst.setNull(5, Types.BINARY);
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
				pst.setNull(2, Types.BINARY);
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
				pst.setNull(3, Types.BINARY);
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
	
	@Override
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
					+ " VALUES (?,?,?,?,?,0,true,?,?)");
			
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
					+ " VALUES (?,?,?,?,?,0,true,?,?)");
			
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
					+ " VALUES (?,?,?,?,?,0,true,?,?)");
			
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
			List<KrnAttribute> res_ = qr.query(conn, "SELECT * FROM " + getDBPrefix() + "t_attrs WHERE c_type_id=? AND c_is_repl=?", rh, new Object[] {cls.id, true});
			for (KrnAttribute a : res_) {
				res.add(a);
			}
			KrnClass cls_ = cls;
			while (cls_.parentId > 0) {
	    		cls_ = getClassByIdComp(cls_.parentId);
	    		if (cls_.modifier == 0) {
					res_ = (List<KrnAttribute>) qr.query(conn, "SELECT * FROM " + getDBPrefix() + "t_attrs WHERE c_type_id=? AND c_is_repl=?", rh, new Object[] {cls_.id, true});
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
	public long getNextId(String sequence) throws DriverException {
		long res;
		String nextIdSQL = "SELECT nextval('"+ sequence + "')";
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

	public void loadClassTable(KrnClass cls, BufferedReader br, String separator) throws DriverException {
		log.info("Loading " + cls.id + " " + cls.name);
    	PreparedStatement pst = null;
    	try {
	    	// Первая строка содержит названия полей, которые должны соответствовать
	    	// названиям колонок в таблице
	    	String line = br.readLine();
	    	if (line == null || line.trim().length() <= 1)
	    		return;
	    	
	    	line = Funcs.sanitizeSQL2(line.trim());
	    	
	    	if (line.matches(".+")) {
		    	String[] fields = split(line, separator);
		    	
		    	// Подготавливаем запрос
	        	StringBuilder sql = new StringBuilder(
	        			"INSERT INTO " + getClassTableName(cls.id) + " (" + fields[0]);
	        	StringBuilder valuesSql = new StringBuilder("?");
	        	for(int i = 1; i < fields.length; i++) {
	            	if("END".equals(fields[i]))
	            		fields[i]="C_END";
            	else if("LAST".equals(fields[i]))
            		fields[i]="C_LAST";
            	else if("BEGIN".equals(fields[i]))
            		fields[i]="C_BEGIN";
	        		sql.append(",").append(fields[i]);
	        		valuesSql.append(",?");
	        	}
        		pst = conn.prepareStatement(
        				sql + ") VALUES (" + valuesSql + ")");
	        		
	            // Узнаем типы колонок
	            long[] types = new long[fields.length];
	            // Предопределенные колонки
	            types[Funcs.indexOf("c_uid", fields)] = 0;
	            types[Funcs.indexOf("c_obj_id", fields)] = PC_INTEGER;
	            types[Funcs.indexOf("c_class_id", fields)] = PC_INTEGER;
	            if (cls.id > 99) {
		            types[Funcs.indexOf("c_tr_id", fields)] = PC_INTEGER;
		            types[Funcs.indexOf("c_is_del", fields)] = PC_INTEGER;
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
			    	line = Funcs.sanitizeSQL2(line);

			    	if (line.matches(".+")) {
			    		String[] values = split(line, separator);
			    		for (int i = 0; i < values.length; i++) {
			    			long type = types[i];
			    			if (type == 0)
			    				pst.setString(i + 1, values[i]);
			    			else
			    				setValue(pst, i + 1, type, parseFileValue(values[i], type));
			    		}
		    			pst.executeUpdate();
			    	}
	
	                if (++j%500 == 0) {
	                	log.info("Проверено объектов " + cls.name + ": " + j);
	                }
		    	}
	        	log.info("Проверено объектов " + cls.name + ": " + j);
	    	}
    	} catch (Exception e) {
            log.error(e, e);
//    		throw new DriverException(e.getMessage());
    	} finally {
    		DbUtils.closeQuietly(pst);
    	}
    }
    private Object parseFileValue(String value, long type) {
    	if (value.length() == 0)
    		return null;
    	if (type == 0)
    		return value;
        if (type == PC_STRING || type == PC_MEMO) {
        	if ("<!EMPTY!>".equals(value)) {
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
        	if ("<!EMPTY!>".equals(value)) {
        		return new byte[0];
        	} else if (value.length() == 0) {
        		return null;
        	} else {
        		return HexStringOutputStream.fromHexString(value);
            }
        } else {
        	return Long.valueOf(value);
        }
        return null;
    }
	private String[] split(String str, String separator) {
		List<String> res = new ArrayList<String>(100);

		int pos = 0;
		for (int i = 0; (i = str.indexOf(separator, pos)) != -1; res.add(str.substring(pos, i)), pos = i + separator.length());
		return res.toArray(new String[res.size()]);
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
				val_ = operator_ + "[" + rightStr_ + (mandatory_?"!+":"!") + "]";
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
				val_ = operator_ + "[" + rightStr_ + (mandatory_?"^+":"^") + "]";
			else
				val_ = operator_+ (rightStr_.equals("1") ? "true" : "false");
			sql_mean_ += colName + val_.trim();
		}
        if(!"".equals(linkPar_))
            sql_mean_ += "[" + linkPar_ + "%]";
		return sql_mean_;
	}
	@Override
	//Создание процедуры
	public String createProcedure(String nameProcedure, List<String> args, String body){
		Statement st = null;
		StringBuffer sb=new StringBuffer();
		String res="";
		if(nameProcedure!=null && !"".equals(nameProcedure) && args!=null && args.size()>0){
		sb.append("CREATE OR REPLACE FUNCTION ").append(nameProcedure).append(" (");
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
	//Получение списка процедур
	public List<String> getListProcedure(String type) {
		List<String> res=new ArrayList<String>();
		if(!"FUNCTION".equals(type)) return res;
		PreparedStatement pst=null;
		ResultSet rs = null;
		String psql="SELECT PRONAME FROM PG_PROC WHERE PROOWNER IN (SELECT NSPOWNER FROM PG_NAMESPACE WHERE NSPNAME ="+ db.getSchemeName().toUpperCase(Constants.OK)+")";
		try {
			pst=conn.prepareStatement(psql);
			rs = pst.executeQuery();
			while(rs.next()){
				res.add(getString(rs, 1));
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
	
	@Override
	public boolean isIndexExists(String tName, String idxName) {
		boolean isExist = true;
		try {
			PreparedStatement pst = conn.prepareStatement(
					"SELECT COUNT(*) FROM PG_CLASS T,PG_CLASS I, PG_INDEX IX "
					+ "WHERE T.OID = IX.INDRELID AND I.OID = IX.INDEXRELID AND LOWER(T.RELNAME)=? AND LOWER(I.RELNAME)=? "
					+ "AND I.RELNAMESPACE IN (SELECT OID FROM PG_NAMESPACE WHERE LOWER(NSPNAME) = ?)");
			pst.setString(1, Funcs.normalizeInput(tName).replace("`", "").toLowerCase());
			pst.setString(2, Funcs.normalizeInput(idxName).toLowerCase());
			pst.setString(3, db.getSchemeName().toLowerCase());
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
	
	public boolean isConstraintExists(String tName,String conName) {
		boolean isExist = true;
		try {
			PreparedStatement pst = conn.prepareStatement(
					"SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE LOWER(TABLE_NAME) = ? AND LOWER(CONSTRAINT_NAME) = ? AND LOWER(CONSTRAINT_SCHEMA) = ?");
			pst.setString(1, Funcs.normalizeInput(tName.toLowerCase()));
			pst.setString(2, Funcs.normalizeInput(conName.toLowerCase()));
			pst.setString(3, db.getSchemeName().toLowerCase());
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
                else if("^".equals(param.substring(param.length()-1)))
                    param=param.substring(0,param.length()-1);
                else if("!".equals(param.substring(param.length()-1)))
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
                else return pv.get(0);
            }
        } else return null;

    }
	@Override
    public List<Object> getFilterParam(String fuid, String paramName,boolean isAddParam, Session s) {
        String paramName_ = paramName;
        if("$".equals(paramName.substring(paramName.length()-1)) 
        		|| "#".equals(paramName.substring(paramName.length()-1))
        		|| "^".equals(paramName.substring(paramName.length()-1))
        		|| "!".equals(paramName.substring(paramName.length()-1)))
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
            if("^".equals(paramName.substring(paramName.length()-1))) {
                List res=new Vector();
            	if(pv0 instanceof Integer) {
                    for(int i=0;i<pv.size();i++) {
                    	res.add(((Integer)pv.get(i))==1?"true":"false");
                    }
            	}else if(pv0 instanceof String) {
                    for(int i=0;i<pv.size();i++) {
                      	res.add(((String)pv.get(i)).equals("1")||((String)pv.get(i)).toUpperCase().equals("TRUE")?"true":"false");
                    }
            	}
                return res;
            }else if(pv0 instanceof com.cifs.or2.kernel.Date
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
	@Override
	protected void setValueFilterParam(PreparedStatement pst, Object o, int i, int typeAttr)
			throws SQLException {
		if (o instanceof String) {
			if (typeAttr == 2) {
				setMemo(pst, i, (String) o);
			} else if (typeAttr == 3){
				pst.setBoolean(i, ((String) o).toUpperCase().equals("TRUE") || ((String) o).toUpperCase().equals("1"));
			}else if (typeAttr == 4) {
				pst.setLong(i, Long.parseLong((String) o));
			} else {
				String res = Funcs.normalizeInput((String) o);
				pst.setString(i, res);
			}
		}else if (o instanceof Long) {
			if (typeAttr == 3) {
				pst.setBoolean(i, ((Long)o)==1);
			}else{
				pst.setLong(i, (Long)o);
			}
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
	@Override
	protected String getCIsRepl(int c_is_repl) {
		return "c_is_repl="+(c_is_repl==1);
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
            		+" WHERE "+attr_p_finish_tname+" IS NULL AND "+attr_p_start_tname+" IS NOT NULL AND "+attr_p_status_tname+" = true");
			log.info("Апгрейд БД до версии 66 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 66!");
		} 
        isUpgrading = false;
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
					+ "WHERE c_is_multilingual=true";
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
			if(sqlMap.keySet().size()>0) {
	            new Thread() {
	                public void run() {
		               	try(Connection conn_ = getNewConnection()) {
		               		int currCountTable=0;
		               		int	countTable=sqlMap.keySet().size();
		        			for(String keyTblName:sqlMap.keySet()) {
		        				String tsql = "ALTER TABLE "+keyTblName+sqlMap.get(keyTblName);
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
		                	log.error("Ошибка при добавлении мультиязычных колонок!");
		                	log.error(e, e);
		                }
	                }
	            }.start();
			}
		} catch(SQLException e){
            log.error(e, e);
		} catch (DriverException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(pst);
		}
		log.info("Закончена проверка на соответствие системных языков и наличия колонок в таблицах!");
		String res = "Будут добавлены мультиязычные колонки для '"+sqlMap.keySet().size()+"' таблиц";
		return res;
		
	}
	public int getRealSizeColumn(KrnAttribute attr) {
		int size=attr.size;
		String t_name=getClassTableName(attr.classId);
		String c_name=getAttrTableName(attr);
	   	String selSql="SELECT CHARACTER_MAXIMUM_LENGTH "
	  	   		+ "FROM INFORMATION_SCHEMA.COLUMNS "
	  	   		+ "WHERE UDT_NAME = 'varchar' "
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
        	log.error("Ошибка при определении размера колонки для атрибута:"+c_name+";в таблице:"+t_name+" !");
        	log.error(e, e);
	   	}
	   	return size;
	}
	
	public void upgradeTo71() throws SQLException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 71 ...");
		log.info("Создание атрибута 'showSearchField', 'srch_txt', 'ifc_uid' типа boolean и string и Объекта в классе 'ConfigGlobal'.");
		log.info("Создание атрибута 'scope[]'типа объект в классе 'User'.");
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

			Statement st = conn.createStatement();
	        st.executeUpdate("CREATE TABLE "+getDBPrefix()+"t_search_indexes ("
	        		+ "c_id BIGSERIAL,"
					+ "c_search_str VARCHAR(2000) NOT NULL,"
					+ "c_obj_uid VARCHAR(255) NOT NULL,"
					+ "c_ext_field VARCHAR(255),"
					+ "PRIMARY KEY(c_id))");
	        
			st.executeUpdate("CREATE UNIQUE INDEX t_search_obj_ext_idx"
					+ " ON "+getDBPrefix()+"t_search_indexes(c_obj_uid,c_ext_field)");

			st.executeUpdate("CREATE INDEX t_search_obj_idx"
					+ " ON "+getDBPrefix()+"t_search_indexes(c_obj_uid)");

			st.executeUpdate("CREATE INDEX t_search_ext_idx"
					+ " ON "+getDBPrefix()+"t_search_indexes(c_ext_field)");

	        log.info("Апгрейд БД до версии 71 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 71!");
		}
		isUpgrading = false;
	}

	protected StringBuilder addLimit(StringBuilder sql, int limit, int offset) {
		return sql.append(" LIMIT ").append(limit);
	}
	
	protected StringBuilder byPage(StringBuilder sql){
    	return	sql.append(" LIMIT ? OFFSET ?");
	}

	protected void setPageValue(PreparedStatement pst,int i, int beginRow, int endRow) throws SQLException {
		pst.setLong(i, endRow-beginRow+(beginRow>0?1:0));
		pst.setLong(i + 1, beginRow-(beginRow>0?1:0));
	}
	
	protected boolean isNameNotAllowed(String name) {
		if ("end".equalsIgnoreCase(name)) {
			return true;
		}
		return false;
	}

	@Override
	public void commitLongTransaction(final long trId, boolean deleteRefs, Session session) throws DriverException {
		try {
			saveDataLog();
			
			Set<Long> createIds = new HashSet<Long>();
			Set<Pair<Long, String>> jrbNodesToDelete = new HashSet<>();
			QueryRunner qr = new QueryRunner(true);

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
			
//			PreparedStatement attrPst = null;
//			ResultSet attrRs = null;
			try {
				// Отключаем проверку FK
//				setForeignKeysEnabled(false);

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

//				attrPst = conn.prepareStatement("SELECT c_id,c_class_id FROM "+getDBPrefix()+"t_attrs WHERE c_class_id=? AND c_col_type>0 AND c_rattr_id IS NULL"); 
				// Выполняем действия над каждым классом из classIds...
				for (Long classId : classIds) {
					// Удаляем все строчки в АТ талблицах класса
					// Необходимо ручное удаления из-за отключенных проверок FK
/*					attrPst.setLong(1, classId);
					attrRs = attrPst.executeQuery();
					while (attrRs.next()) {
						long attrId = attrRs.getLong(1);
						KrnAttribute a = db.getAttributeById(attrId);
						String atName = getAttrTableName(a);
						qr.update(conn, "DELETE FROM " + atName + " WHERE c_tr_id=?", trId);
					}
					attrRs.close();
*/
					final String ctName = getClassTableName(classId);
					// Удалаяем все строчки в таблице класса с X транзакцией,
					pst = conn.prepareStatement("DELETE FROM " + ctName + " WHERE c_tr_id=?");
					pst.setLong(1, trId);
					pst.executeUpdate();
					pst.close();
				}
//				attrPst.close();
			} finally {
				// Включаем проверку FK
//				setForeignKeysEnabled(true);
//				DbUtils.closeQuietly(attrRs);
//				DbUtils.closeQuietly(attrPst);
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
}
