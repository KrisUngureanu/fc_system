package kz.tamur.ods.mssql;

import static com.cifs.or2.kernel.ModelChange.ACTION_CREATE;
import static com.cifs.or2.kernel.ModelChange.ACTION_DELETE;
import static com.cifs.or2.kernel.ModelChange.ACTION_MODIFY;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_ATTRIBUTE;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_CLASS;

import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import kz.tamur.ods.Lock;
import kz.tamur.ods.Value;
import kz.tamur.ods.mysql.MySqlDriver3;
import kz.tamur.ods.sql92.AttrResultSetHandler;
import kz.tamur.ods.sql92.AttributeRsh;
import kz.tamur.ods.sql92.ClassResultSetHandler;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.server.wf.ExecutionComponent;
import kz.tamur.util.Funcs;
import kz.tamur.util.KrnUtil;
import kz.tamur.util.Pair;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.logging.Log;
import org.jdom.Element;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.db.Database;
import com.cifs.or2.util.MMap;

import static kz.tamur.or3.util.Tname.*;
import static kz.tamur.or3ee.common.SessionIds.CID_BOOL;
import static kz.tamur.or3ee.common.SessionIds.CID_DATE;
import static kz.tamur.or3ee.common.SessionIds.CID_INTEGER;
import static kz.tamur.or3ee.common.SessionIds.CID_STRING;

public class MsSqlDriver3 extends MySqlDriver3 {
	
	public MsSqlDriver3(Database db, String dsName, UserSession us) throws DriverException {
		super(db, dsName, us);
	}

	@Override
	protected String getSqlTypeName(long typeId, int sz) {
		if (typeId == PC_STRING) {
			return "NVARCHAR(" + (sz > 0 ? sz : 2000) + ")";
		} else if (typeId == PC_INTEGER) {
			return "BIGINT";
		} else if (typeId == PC_DATE) {
			return "DATETIME";
		} else if (typeId == PC_TIME) {
			return "DATETIME";
		} else if (typeId == PC_BOOL) {
			return "INTEGER";
		} else if (typeId == PC_FLOAT) {
			return "FLOAT";
		} else if (typeId == PC_MEMO) {
			return "NTEXT";
		} else if (typeId == PC_BLOB) {
			return "IMAGE";
		}
		return "BIGINT";
	}

    protected void install() throws DriverException {
        try {
            Statement st = conn.createStatement();
            ResultSet set = st.executeQuery("select * from sysobjects where name='t_ids'");
            boolean installed = set.next();
            set.close();
            if (!installed) {
		        isUpgrading = true;

		        st.executeUpdate("CREATE TABLE t_classes (" +
                		"c_id BIGINT PRIMARY KEY IDENTITY," +
                		"c_cuid CHAR(36) NOT NULL," +
                		"c_name NVARCHAR(255) NOT NULL," +
                		"c_parent_id BIGINT NOT NULL," +
                		"c_is_repl INTEGER NOT NULL," +
                		"c_comment NTEXT," +
                		"c_mod INTEGER NOT NULL)");
                st.executeUpdate("CREATE UNIQUE INDEX idx_class_name ON t_classes(c_name)");
                st.executeUpdate("CREATE UNIQUE INDEX idx_cuid ON t_classes(c_cuid)");
                st.executeUpdate("CREATE INDEX idx_class_pr ON t_classes(c_parent_id)");

                st.executeUpdate("CREATE TABLE t_clinks ("
                        + "c_parent_id BIGINT NOT NULL,"
                        + "c_child_id BIGINT NOT NULL,"
                        + "FOREIGN KEY(c_parent_id) REFERENCES t_classes(c_id) ON DELETE NO ACTION,"
                        + "FOREIGN KEY(c_child_id) REFERENCES t_classes(c_id) ON DELETE NO ACTION,"
                        + "PRIMARY KEY(c_parent_id,c_child_id))");
                st.executeUpdate("CREATE TABLE t_attrs ("
                        + "c_id BIGINT PRIMARY KEY IDENTITY,"
                        + "c_auid CHAR(36) NOT NULL,"
                        + "c_class_id BIGINT NOT NULL,"
                        + "c_name NVARCHAR(255) NOT NULL,"
                        + "c_type_id BIGINT NOT NULL,"
                        + "c_col_type INTEGER NOT NULL,"
                        + "c_is_unique INTEGER NOT NULL,"
                        + "c_is_indexed INTEGER NOT NULL,"
                        + "c_is_multilingual INTEGER NOT NULL,"
                        + "c_is_repl INTEGER NOT NULL,"
                        + "c_size INTEGER NOT NULL,"
                        + "c_flags BIGINT NOT NULL,"
                        + "c_rattr_id BIGINT,"
                        + "c_sattr_id BIGINT,"
            			+ "c_sdesc INTEGER,"
            			+ "c_is_encrypt INTEGER,"
                        + "c_comment NTEXT,"
                        + "FOREIGN KEY(c_class_id) REFERENCES t_classes(c_id) ON DELETE  NO ACTION,"
                        + "FOREIGN KEY(c_type_id) REFERENCES t_classes(c_id) ON DELETE  NO ACTION,"
                        + "FOREIGN KEY(c_rattr_id) REFERENCES t_attrs(c_id),"
                        + "FOREIGN KEY(c_sattr_id) REFERENCES t_attrs(c_id))");
                st.executeUpdate("CREATE UNIQUE INDEX idx_attr_cl_nm ON t_attrs(c_class_id,c_name)");
                st.executeUpdate("CREATE UNIQUE INDEX idx_auid ON t_attrs(c_auid)");

                st.executeUpdate("CREATE TABLE t_rattrs ("
                        + "c_attr_id BIGINT NOT NULL,"
                        + "c_rattr_id BIGINT NOT NULL,"
                        + "PRIMARY KEY(c_attr_id,c_rattr_id),"
                        + "FOREIGN KEY(c_attr_id) REFERENCES t_attrs(c_id) ON DELETE  NO ACTION,"
                        + "FOREIGN KEY(c_rattr_id) REFERENCES t_attrs(c_id) ON DELETE  NO ACTION)");
                st.executeUpdate("CREATE TABLE t_methods ("
                        + "c_id BIGINT PRIMARY KEY IDENTITY,"
                        + "c_muid CHAR(36) NOT NULL,"
                        + "c_class_id BIGINT NOT NULL,"
                        + "c_name NVARCHAR(255) NOT NULL,"
                        + "c_is_cmethod INTEGER NOT NULL,"
                        + "c_expr IMAGE,"
                        + "c_comment NTEXT,"
                        + "FOREIGN KEY(c_class_id) REFERENCES t_classes(c_id) ON DELETE CASCADE)");
                st.executeUpdate("CREATE UNIQUE INDEX idx_method_cl_nm ON t_methods(c_class_id,c_name)");

				st.executeUpdate("CREATE TABLE ct99 ("
						+ "c_obj_id BIGINT IDENTITY,"
						+ "c_uid NVARCHAR(20),"
						+ "c_class_id BIGINT NOT NULL,"
						+ "PRIMARY KEY(c_obj_id),"
						+ "FOREIGN KEY(c_class_id) REFERENCES t_classes(c_id) ON DELETE CASCADE)");
				st.executeUpdate("CREATE UNIQUE INDEX uid99idx ON ct99(c_uid)");
				st.executeUpdate("CREATE INDEX cls99idx ON ct99(c_class_id)");

				// Таблица блокировок объектов
				createLocksTable(conn);
				
				//Таблицы для хранение многоатрибутных индексов
				st.executeUpdate("" +
						"CREATE TABLE t_indexes(" +
						" c_id BIGINT NOT NULL IDENTITY," +
						" c_uid CHAR(36) NOT NULL," +
						" c_class_id BIGINT NOT NULL," +
						" c_is_multilingual BIT NOT NULL," +
						" PRIMARY KEY(c_id)," +
						" FOREIGN KEY(c_class_id) REFERENCES t_classes(c_id) ON DELETE NO ACTION" +
						")" +						
						"");
				st.executeUpdate("" +
						"CREATE TABLE t_indexkeys(" +
						" c_index_id BIGINT NOT NULL," +
						" c_attr_id BIGINT NOT NULL," +
						" c_keyno BIGINT NOT NULL," +
						" c_is_desc BIT NOT NULL," +
						" FOREIGN KEY(c_index_id) REFERENCES t_indexes(c_id) ON DELETE NO ACTION," +
						" FOREIGN KEY(c_attr_id) REFERENCES t_attrs(c_id) ON DELETE NO ACTION" +
						")" +
						"");				
				
				st.executeUpdate("CREATE TABLE t_changescls ("
						+ "c_id BIGINT PRIMARY KEY IDENTITY,"
						+ "c_type INTEGER NOT NULL,"
						+ "c_action INTEGER NOT NULL,"
						+ "c_entity_id NVARCHAR(255) NOT NULL)");
				
				st.executeUpdate("CREATE TABLE t_changes ("
						+ "c_id BIGINT PRIMARY KEY IDENTITY,"
						+ "c_class_id BIGINT,"
						+ "c_object_id BIGINT NOT NULL,"
						+ "c_attr_id BIGINT NOT NULL,"
						+ "c_lang_id BIGINT NOT NULL,"
						+ "c_tr_id BIGINT NOT NULL,"
						+ "c_is_repl BIT NOT NULL,"
						+ "FOREIGN KEY(c_attr_id) REFERENCES t_attrs(c_id) ON DELETE CASCADE)");
				st.executeUpdate("CREATE INDEX ch_tr_repl_idx ON t_changes(c_tr_id,c_is_repl)");
				st.executeUpdate("CREATE INDEX idx_ch_obj"
						+ " ON t_changes(c_object_id)");
				st.executeUpdate("CREATE TABLE t_ids ("
						+ "c_name NVARCHAR(255) NOT NULL,"
						+ "c_last_id BIGINT NOT NULL,"
						+ "PRIMARY KEY(c_name))");

                // Создаем запись в таблице классов
                /*st.executeUpdate("SET IDENTITY_INSERT t_classes ON");
                PreparedStatement pst = conn.prepareStatement("INSERT INTO t_classes (c_id,c_name, c_parent_id, c_is_repl, c_mod, c_cuid) VALUES (?,?,?,?,?,?)");
                pst.setLong(1,ROOT_CLASS_ID);
                pst.setString(2, "Объект");
                pst.setLong(3, -1);
                pst.setBoolean(4, false);
                pst.setBoolean(5, false);
                pst.setString(6, "" + ROOT_CLASS_ID);
                pst.executeUpdate();
                st.executeUpdate("SET IDENTITY_INSERT t_classes OFF");*/

                PreparedStatement pst = conn
						.prepareStatement("INSERT INTO t_ids (c_name,c_last_id) VALUES (?,?)");
				String dbImportDir = System.getProperty("dbImportDir");
				if (dbImportDir == null) {
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
					pst.setLong(2, 10);
					pst.executeUpdate();
				} else {
					sysLangCount = Integer.parseInt(System.getProperty("sysLangCount"));
					dbImport(dbImportDir, System.getProperty("separator"));
				}
				//
				pst.setString(1, "installed");
				pst.setLong(2, 1);
				pst.executeUpdate();
				pst.close();
		        isUpgrading = false;
				commit();
			}
            version = 10;
			st.close();
		} catch (SQLException e) {
			throw new DriverException(e);
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
		uid = Funcs.sanitizeSQL(uid);

        // устанавливаем индекс на объектном атрибуте.
        if (typeId >= 99) {
            isIndexed = true;
        }
        try {
        	String sql = null;
        	if (isVersion(kz.tamur.or3.util.Tname.TnameVersionBD) && tname != null && tname.trim().length() != 0) {
    	    	sql = "SELECT TOP 1 c_id, c_name FROM t_attrs WHERE c_tname=?;";
    			PreparedStatement istnane = conn.prepareStatement(sql);
    			istnane.setString(1, tname);
    			ResultSet rst = istnane.executeQuery();
    			if(rst.next()) {
    				throw new DriverException("Параметр TName '" + tname 
    						+ "' уже существует у аттрибута ID=" + rst.getLong(1) 
    						+ " '" + getString(rst, 2) + "'");
    			}
        	}
        	
            // Создаем запись в таблице атрибутов
            if (uid == null) {
                uid = UUID.randomUUID().toString();
            } else {
                if (db.getAttributeByUid(uid) != null) {
                    this.log.warn("Атрибут \""+name+"\" не создан, т.к. уже есть в БД. UUID:"+uid);
                    return db.getAttributeByUid(uid);
                }
            }
            Statement st = conn.createStatement();
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
                st.executeUpdate("SET IDENTITY_INSERT t_attrs ON");
                sql = "INSERT INTO t_attrs (c_class_id,c_name,c_type_id,"
                        + "c_col_type,c_is_unique,c_is_indexed,"
                        + "c_is_multilingual,c_is_repl,c_size,c_flags,"
                        + "c_rattr_id,c_sattr_id,c_sdesc,c_auid,c_id"+ATname+AAccessMod+AIsEncrypt+")"
                        + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?"+qATname+qAAccessMod+qAIsEncrypt+")";
            } else {
                sql = "INSERT INTO t_attrs (c_class_id,c_name,c_type_id,"
                        + "c_col_type,c_is_unique,c_is_indexed,"
                        + "c_is_multilingual,c_is_repl,c_size,c_flags,"
                        + "c_rattr_id,c_sattr_id,c_sdesc,c_auid"+ATname+AAccessMod+AIsEncrypt+")"
                        + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?"+qATname+qAAccessMod+qAIsEncrypt+")";
            }
            PreparedStatement pst = conn.prepareStatement(sql);
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
			try {
				pst.executeUpdate();
	            if (id == -1) {
	                id = getLastAttributeId();
	            } else {
	                st.executeUpdate("SET IDENTITY_INSERT t_attrs OFF");
	            }
			} finally {
				pst.close();
				st.close();
			}
			
			if (id == -1) {
				id = getAttributeIdByUID(uid);
			}
            
            // Создаем объект KrnAttribute
            KrnAttribute attr = KrnUtil.createAttribute(
                    uid, id, name, classId, typeId, collectionType, isUnique,
                    isMultilingual, isIndexed, size, flags, isRepl, rAttrId,
                    sAttrId, sDesc, tname, null, null, null, null, 0, 0, 0, 0, accessModifier, isEncrypt);

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
            this.log.error("Атрибут \"" + name + "\" не создан. UUID:" + uid);
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
				sb.append(" ADD ");
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
				st.executeUpdate(sb.toString());
                if (attr.typeClassId == PC_BOOL) {
                    st.executeUpdate("ALTER TABLE " + getClassTableName(attr.classId)
                            + " ADD CONSTRAINT DF_ct" + attr.classId +"_cm" + attr.id +
                            " DEFAULT 0 FOR "+getColumnName(attr));
                }
				st.close();
			} else {
				// Создаем таблицу для атрибута
				String atName = getAttrTableName(attr);
				StringBuffer sb = new StringBuffer();
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
				sb.append(" (c_obj_id,c_tr_id) ON DELETE CASCADE ON UPDATE CASCADE)");
				Statement st = conn.createStatement();
				st.executeUpdate(sb.toString());
				st.close();
			}

			// Создаем индекс. Для FK в MySQL индекс создается автоматически
			if (attr.isIndexed) {
				updateIndex(attr, true);
			}
		} else if (isArray) {
			// Добавляем колонку в таблицу типа
			//TODO Рассмотреть необходимость в индексе
			String ctName = getClassTableName(attr.typeClassId);
			String cmiName = getRevIndexColumnName(attr.id);
			Statement st = conn.createStatement();
			st.executeUpdate(
				"ALTER TABLE " + ctName
				+ " ADD " + cmiName + " INTEGER NOT NULL");
			st.close();
		}
	}
    
    

    @Override
	protected void updateTriggers(long classId)
	throws SQLException, DriverException {
		
		// Для мультиязычных атрибутов пока проверяем обязательность только
		// на 1-ом системном языке
		long langId = getSystemLangs().get(0).id;
		
		KrnClass cls = db.getClassById(classId);
		List<KrnAttribute> attrs = db.getAttributesByClassId(cls.id, false);
		
		Statement st = conn.createStatement();
		String ctName = Funcs.sanitizeSQL(getClassTableName(cls.id));
		String itgName = getInsertTriggerName(cls.id);
		String utgName = getUpdateTriggerName(cls.id);
		// Удаляем старые триггеры (если существуют)
		PreparedStatement pst = conn.prepareStatement(
				"SELECT COUNT(*) FROM dbo.sysobjects" +
				" WHERE id = object_id(?)" +
				" and OBJECTPROPERTY(id, N'IsTrigger') = 1");
		pst.setString(1, "[dbo].[" + itgName + "]");
		ResultSet rs = pst.executeQuery();
		rs.next();
		if (rs.getInt(1) > 0)
			st.executeUpdate("DROP TRIGGER [dbo].[" + itgName + "]");
		rs.close();
		
		pst.setString(1, "[dbo].[" + utgName + "]");
		rs = pst.executeQuery();
		rs.next();
		if (rs.getInt(1) > 0)
			st.executeUpdate("DROP TRIGGER [dbo].[" + utgName + "]");
		rs.close();
		pst.close();
		
		boolean create = false;
		String declare = "@objId AS INT, @trId AS INT, @isDel AS INT, @classId AS INT, @msg as NVARCHAR(2000)";
		String fetch = "@objId,@trId,@isDel,@classId";
		String select = "c_obj_id,c_tr_id,c_is_del,c_class_id";
		String insertChecks = "";
		String updateChecks = "";
		
		Map<MultiKey, List<Long>> m = triggerExcept.get(dsName);
		
		for (KrnAttribute a : attrs) {
			if (!a.isMandatory())
				continue;
			create = true;
			
			String cmName = Funcs.sanitizeSQL(getColumnName(a, langId));
			
			declare += ", @" + cmName + " AS " + getSqlTypeName(a);
			fetch += ",@" + cmName;
			select += "," + cmName;

			String sqlExcept = null;
			MultiKey key = new MultiKey(classId, a.id);
			if (m != null && m.containsKey(key)) {
				List<Long> child_ids = m.get(key);
				if (child_ids != null && child_ids.size() > 0) {
					sqlExcept = " AND @classId NOT IN (";
					for (Long child_id : child_ids) {
						sqlExcept += child_id + ",";
					}
					sqlExcept = sqlExcept.substring(0, sqlExcept.length() - 1) + ")";
				}
			}
			
			String sql1 = "\t\tIF (@trId=0 AND @isDel=0";
			if (sqlExcept != null) sql1 += sqlExcept;
			
			sql1 += " AND (@" + cmName + " IS NULL";
			if (a.typeClassId == PC_STRING) // string - проверяем на пустую строку
				sql1 += " OR LEN(@" + cmName + ")=0";
			sql1 += ")) BEGIN\n";
			sql1 += "\t\t\tROLLBACK TRAN\n";
			sql1 += "\t\t\tSET @msg='{01,' + CONVERT(VARCHAR,@objId) + '," + a.id + "," + Funcs.sanitizeSQL(cls.name) + "." + Funcs.sanitizeSQL(a.name) + "}'\n";
			sql1 += "\t\t\tRAISERROR(@msg,18,1)\n";
			sql1 += "\t\tEND\n";
			
			String usql1 = "\t\tIF ((UPDATE(c_tr_id) OR UPDATE(" + cmName + ")) AND @trId=0";
			if (sqlExcept != null) usql1 += sqlExcept;
			
			usql1 += " AND (@" + cmName + " IS NULL";
			if (a.typeClassId == PC_STRING) // string - проверяем на пустую строку
				usql1 += " OR LEN(@" + cmName + ")=0";
			
			usql1 += ")) BEGIN\n";
			usql1 += "\t\t\tROLLBACK TRAN\n";
			usql1 += "\t\t\tSET @msg='{01,' + CONVERT(VARCHAR,@objId) + '," + a.id + "," + Funcs.sanitizeSQL(cls.name) + "." + Funcs.sanitizeSQL(a.name) + "}'\n";
			usql1 += "\t\t\tRAISERROR(@msg,18,1)\n";
			usql1 += "\t\tEND\n";

			insertChecks += sql1;
			updateChecks += usql1;
			
			if (sqlExcept != null) {
				log.debug("********************************************************");
				log.debug(sql1);
				log.debug(usql1);
				log.debug("********************************************************");
			}
		}
		
		String sql = "CREATE TRIGGER " + itgName + " ON " + ctName +
				" FOR INSERT AS BEGIN\n" +
				"\tDECLARE " + declare + "\n" +
				"\tDECLARE db_cursor CURSOR FOR SELECT " + select + " FROM Inserted\n" +
				"\tOPEN db_cursor\n" +
				"\tFETCH NEXT FROM db_cursor INTO " + fetch + "\n" +
				"\tWHILE @@FETCH_STATUS = 0 BEGIN\n" +
				insertChecks +
				"\t\tFETCH NEXT FROM db_cursor INTO " + fetch + "\n" +
				"\tEND\n" +
				"\tCLOSE db_cursor\n" +
				"\tDEALLOCATE db_cursor\n" +
				"END\n";
		
		String usql = "CREATE TRIGGER " + utgName + " ON " + ctName +
				" FOR UPDATE AS BEGIN\n" +
				"\tDECLARE " + declare + "\n" +
				"\tDECLARE db_cursor CURSOR FOR SELECT " + select + " FROM Inserted\n" +
				"\tOPEN db_cursor\n" +
				"\tFETCH NEXT FROM db_cursor INTO " + fetch + "\n" +
				"\tWHILE @@FETCH_STATUS = 0 BEGIN\n" +
				updateChecks +
				"\t\tFETCH NEXT FROM db_cursor INTO " + fetch + "\n" +
				"\tEND\n" +
				"\tCLOSE db_cursor\n" +
				"\tDEALLOCATE db_cursor\n" +
				"END\n";

		if (create) {
			log.debug("before insert");
			st.executeUpdate(sql);
			log.debug("before update");
			st.executeUpdate(usql);
		}
		
		st.close();
	}
    
    @Override
    protected void updateTriggersComp(long classId)
    		throws SQLException, DriverException {

    			KrnClass cls = getClassByIdComp(classId);
    			String itgName = getInsertTriggerName(cls.id);
    			String utgName = getUpdateTriggerName(cls.id);
    			
    			Statement st = conn.createStatement();
    			try {
    				// Удаляем старые триггеры (если существуют)
    				String sql1 = "IF EXISTS (SELECT * FROM sys.objects WHERE [name] = N'"+ itgName + "' AND [type] = 'TR')\n" +
    						" BEGIN \n  DROP TRIGGER " + itgName + "; \n END;" ;
    				String sql2 = "IF EXISTS (SELECT * FROM sys.objects WHERE [name] = N'" + utgName +"' AND [type] = 'TR')\n" +
    						" BEGIN \n  DROP TRIGGER " + utgName + "; \n END;" ;
    				st.executeUpdate(sql1);
    				st.executeUpdate(sql2);
    			} catch (SQLException e) {
    				throw e;
    			} finally {
    				DbUtils.closeQuietly(st);
    			}

    			List<KrnAttribute> attrs = getAttributesByClassIdComp(cls, false);
    			createTriggers(cls, attrs);
    		}
	
	protected String getDropForeignKeySql(String tname, String fkname) {
		return "ALTER TABLE " + Funcs.sanitizeSQL(tname) + " DROP CONSTRAINT " + Funcs.sanitizeSQL(fkname);
	}
    
	protected String getDropColumnSql(String tname, String cname) {
		return "ALTER TABLE " + Funcs.sanitizeSQL(tname) + " DROP COLUMN " + Funcs.sanitizeSQL(cname);
	}
    
	protected String getDropIndexSql(String tname, String indexName) {
		return "DROP INDEX " + Funcs.sanitizeSQL(tname) + "." + Funcs.sanitizeSQL(indexName);
	}
    
	@Override
	protected void deleteAttribute(long id, boolean log, long dbVer) throws DriverException {

		KrnAttribute attr = db.getAttributeById(id);

		try {
			// Удаляем запись из таблицы t_rattrs
			QueryRunner qr = new QueryRunner(true);
			qr.update(conn,
					"DELETE FROM t_rattrs WHERE c_attr_id=? OR c_rattr_id=?", id, id);

			// Удаляем запись из таблицы t_attrs
			qr.update(conn, "DELETE FROM t_attrs WHERE c_id=?", id);

			if (attr.rAttrId == 0) {
				if (attr.collectionType == COLLECTION_ARRAY
						|| attr.collectionType == COLLECTION_SET
						|| attr.isMultilingual) {
					// Удаляем дополнительную таблицу (если она существует)
					String tname = getAttrTableName(attr);
					qr.update(conn, "IF EXISTS(SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='" + tname + "')  DROP TABLE " + tname);
				} else {
					// Удаляем колонку в таблице класса
					String tname = getClassTableName(attr.classId);
					if (attr.typeClassId >=99) {
						// String fkname = "FK" + attr.id;
						// qr.update(conn, getDropForeignKeySql(tname, fkname));
						if (attr.isIndexed) {
							qr.update(conn, getDropIndexSql(tname, "idx" + attr.classId
									+ "_" + attr.id));
						}
					} else if (attr.typeClassId == PC_BOOL) {
						String cname = getColumnName(attr);
						qr.update(conn, "ALTER TABLE " + tname
								+ " DROP CONSTRAINT DF_" + tname + "_" + cname);
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
			updateTriggers(attr.classId);

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
    
	@Override
	public KrnClass createClass(String name, long parentId, boolean isRepl,
			int mod, long id, String uid, boolean log, String tname) throws DriverException {
		try {
			//tname = setPrefix_ct(tname);
			String sql = "";
			if (isVersion(kz.tamur.or3.util.Tname.TnameVersionBD) && tname != null && tname.trim().length() != 0) {
    	    	sql = "SELECT TOP 1 c_id, c_name FROM t_attrs WHERE c_tname=?;";
    			PreparedStatement istnane = conn.prepareStatement(sql);
    			istnane.setString(1, tname);
    			ResultSet rst = istnane.executeQuery();
    			if(rst.next()) {
    				throw new DriverException("Параметр TName '" + tname 
    						+ "' уже существует у класса ID=" + rst.getLong(1) 
    						+ " '" + getString(rst, 2) + "'");
    			}
        	}
            if (uid == null) {
                uid = UUID.randomUUID().toString();
            } else {
                if (db.getClassByUid(uid) != null) {
                    this.log.warn("Класс \""+name+"\" не создан, т.к. уже есть в БД. UUID:"+uid);
                    return db.getClassByUid(uid);
                }
            }
			if (id > 0) {
				// Создаем запись в таблице классов
				Statement st = conn.createStatement();
				if (isVersion(kz.tamur.or3.util.Tname.TnameVersionBD)) {
					sql = "INSERT INTO t_classes (c_id, c_name, c_parent_id, c_is_repl,c_mod, c_cuid, c_tname) VALUES (?,?,?,?,?,?,?)";
				} else {
					sql = "INSERT INTO t_classes (c_id, c_name, c_parent_id, c_is_repl,c_mod, c_cuid) VALUES (?,?,?,?,?,?)";
				}
				PreparedStatement pst = conn.prepareStatement(sql);
				try {
					st.executeUpdate("SET IDENTITY_INSERT t_classes ON");

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
				} finally {
					pst.close();
					st.executeUpdate("SET IDENTITY_INSERT t_classes OFF");
					st.close();
				}
			} else {
				// Создаем запись в таблице классов
				if (isVersion(kz.tamur.or3.util.Tname.TnameVersionBD)) {
					sql = "INSERT INTO t_classes (c_name, c_parent_id, c_is_repl,c_mod, c_cuid, c_tname) VALUES (?,?,?,?,?,?)";
				} else {
					sql = "INSERT INTO t_classes (c_name, c_parent_id, c_is_repl,c_mod, c_cuid) VALUES (?,?,?,?,?)";
				}
				PreparedStatement pst = conn.prepareStatement(sql);
				try {
					pst.setString(1, name);
					pst.setLong(2, parentId);
					pst.setBoolean(3, isRepl);
					pst.setInt(4, mod);
					pst.setString(5, uid);
					if (isVersion(kz.tamur.or3.util.Tname.TnameVersionBD)) {
						pst.setString(6, tname);
					}
					pst.executeUpdate();
				} finally {
					pst.close();
				}
				id = getLastClassId();
			}
			
			// Создаем записи в таблице рекурсивных связей с родителей с детьми
			PreparedStatement pst = conn
					.prepareStatement("INSERT INTO t_clinks (c_parent_id,c_child_id)"
							+ " SELECT c_parent_id,"
							+ id
							+ " FROM t_clinks WHERE c_child_id=?");
			pst.setLong(1, parentId);
			pst.executeUpdate();
			pst.close();
			pst = conn
					.prepareStatement("INSERT INTO t_clinks (c_parent_id,c_child_id)"
							+ " VALUES (?,?)");
			pst.setLong(1, id);
			pst.setLong(2, id);
			pst.executeUpdate();
			pst.close();

			// Создаем объект KrnClass...
			KrnClass cls = new KrnClass(uid, id, parentId, isRepl, mod, name, tname, null, null, null, null, 0, 0, 0, 0);

			if (id >= 100 && (mod & 1) == 0) {
				// Создаем таблицу для объектов класса
				String tbName = getClassTableName(cls, true);
				Statement st = conn.createStatement();
				st.executeUpdate("CREATE TABLE "
								+ tbName
								+ " (c_obj_id BIGINT,"
								+ "c_tr_id BIGINT,"
								+ "c_uid VARCHAR(20),"
								+ "c_class_id BIGINT NOT NULL,"
								+ "c_is_del BIGINT DEFAULT 0,"
								+ "PRIMARY KEY (c_obj_id,c_tr_id),"
								+ "FOREIGN KEY(c_class_id) REFERENCES t_classes(c_id) ON DELETE CASCADE)");

				String tableidx = getClassTableName(cls, false).toUpperCase(Constants.OK);
				st.executeUpdate("CREATE INDEX idx_" + tableidx + "_tr ON "
						+ tbName + "(c_tr_id)");
				st.close();
			}

			// Создаем запись в журнале изменения модели
			if (log)
				logVcsModelChanges(ENTITY_TYPE_CLASS, ACTION_CREATE, cls, cls, null, conn);
				logModelChanges(ENTITY_TYPE_CLASS, ACTION_CREATE, cls.uid, conn);

			// ... и ложим его в кэши
			db.addClass(cls, false);

			return cls;
        } catch (SQLException e) {
            this.log.error("Класс \"" + name + "\" не создан. UUID:" + uid);
            String msg = null;
            if (e.getMessage().toLowerCase(Constants.OK).indexOf("\"c_tname_UNIQUE\"".toLowerCase(Constants.OK)) != -1) {
                msg = "C_TNAME";
            } else if (e.getMessage().toLowerCase(Constants.OK).indexOf("\"idx_class_name\"".toLowerCase(Constants.OK)) != -1) {
                msg = "C_NAME";
            }
            if (msg == null) {
                throw convertException(e);
            } else {
                throw new DriverException(msg);
            }
        }
	}

	public void deleteClass(long id) throws DriverException {

		// Удаляется класс вместе со всеми его подклассами
		List<KrnClass> classes = new ArrayList<KrnClass>();
		db.getSubClasses(id, true, classes);

		try {
			QueryRunner qr = new QueryRunner(true);
			Set<Long> attrset = new TreeSet<Long>();
			for (Object aClass : classes) {
				KrnClass cls = (KrnClass) aClass;
				List<KrnAttribute> attrs = db.getAttributesByTypeId(cls.id, false);
				attrs.addAll(db.getAttributesByClassId(cls.id, false));
				for (Object attr1 : attrs) {
					KrnAttribute attr = (KrnAttribute) attr1;
					if (attrset.contains(attr.id))
						continue;
					deleteAttribute(attr.id);
					attrset.add(attr.id);
					if ((attr.classId != cls.id)
							&& (attr.collectionType == COLLECTION_ARRAY
									|| attr.collectionType == COLLECTION_SET || attr.isMultilingual)) {
						KrnClass type = db.getClassById(attr.classId);
						String tname = getClassTableName(attr.classId);
						String cname = getColumnName(attr);
						/*if (!isPrimary(type)) {
							String fkname = "FK" + attr.id;
							qr
									.update(conn, getDropForeignKeySql(tname,
											fkname));
						} else */ if (type.id == PC_BOOL) {
							qr.update(conn, "ALTER TABLE " + tname
									+ " DROP CONSTRAINT DF_" + tname + "_"
									+ cname);
						}
						qr.update(conn, getDropColumnSql(tname, cname));
					}
				}
				List<KrnClass> parents = db.getSuperClasses(cls.id);
				// Удаляем записи из таблицы классов
				PreparedStatement pst = conn
						.prepareStatement("DELETE FROM t_clinks WHERE c_child_id=? OR c_parent_id=?");
				pst.setLong(1, cls.id);
				pst.setLong(2, cls.id);
				pst.executeUpdate();

				List<KrnClass> allClasses = new ArrayList<KrnClass>();
				db.getSubClasses(db.getClassByName("Объект").id, true, allClasses);
				Statement st = conn.createStatement();
				try {
					// выключаем проверку на FK у всех "CT" таблиц
					for (KrnClass cls_ : allClasses) {
						if (cls_.id >= 99) {
							String tbname = getClassTableName(cls_.id);
							st.executeUpdate("ALTER TABLE " + tbname
									+ " NOCHECK CONSTRAINT ALL");
						}
					}
					// удаляем все объекты удаляемого класса из "CT" таблиц
					// родительских классов
					for (KrnClass parent : parents) {
						if (parent.id != cls.id) {
							String tbname = getClassTableName(parent.id);
							st.executeUpdate("DELETE FROM " + tbname
									+ " WHERE c_class_id = " + cls.id);
						}
					}

					// Удаляем запись из таблицы классов
					pst = conn.prepareStatement("DELETE FROM t_classes WHERE c_id=?");
					pst.setLong(1, cls.id);
					pst.executeUpdate();
					pst.close();

				} finally {
					// включаем проверку на FK у всех "CT" таблиц
					for (KrnClass cls_ : allClasses) {
						if (cls_.id >= 99) {
							String tbname = getClassTableName(cls_.id);
							st.executeUpdate("ALTER TABLE " + tbname
									+ " CHECK CONSTRAINT ALL");
						}
					}
					st.close();
				}

				st = conn.createStatement();
				try {
					// Удаляем таблицу для объектов класса
					// @todo и таблицы для всех его множественных связей
					st.executeUpdate("DROP TABLE " + getClassTableName(cls.id));
				} finally {
					st.close();
				}

				// Создаем запись в журнале изменения модели
				logVcsModelChanges(ENTITY_TYPE_CLASS, ACTION_DELETE, cls, cls, null, conn);
				logModelChanges(ENTITY_TYPE_CLASS, ACTION_DELETE, cls.uid, conn);

				// Удаляем класс из кэша
				db.removeClass(cls);
			}

		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	@Override
	protected String getColumnDef(KrnAttribute attr, int langIndex) throws DriverException {
		// @todo Уникальность пока не реализуется так как объект создается
		StringBuffer res = new StringBuffer(getColumnName(attr, langIndex));
		res.append(" ");
		res.append(getSqlTypeName(attr));
		return res.toString();
	}
    
	public void avtoIncrementOnOff(boolean onoff, String tabName) throws DriverException {
		try {
			QueryRunner qr = new QueryRunner(true);
			if (onoff) {
				qr.update(conn, "SET IDENTITY_INSERT " + tabName + " ON");
			} else {
				qr.update(conn, "SET IDENTITY_INSERT " + tabName + " OFF");
			}
		} catch (SQLException e) {
			throw convertException(e);
		}
	}

	public KrnObject createObject(long classId, long trId, long id, String uid,
			boolean rootOnly, Map<Pair<KrnAttribute, Long>, Object> initValues, boolean log) throws DriverException {
		try {
			if (id > 0) {
				QueryRunner qr = new QueryRunner(true);
				qr.update(conn, "SET IDENTITY_INSERT ct99 ON");
			}
			KrnObject obj = super
					.createObject(classId, trId, id, uid, rootOnly, initValues, log);
			return obj;
		} catch (SQLException e) {
			throw convertException(e);
		} finally {
			if (id > 0) {
				QueryRunner qr = new QueryRunner(true);
				try {
					qr.update(conn, "SET IDENTITY_INSERT ct99 OFF");
				} catch (SQLException e) {
					throw convertException(e);
				}
			}
		}
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

		try {
			logVcsDataChanges(obj, attr.id, langId, value, trId);
			trId = openTransaction(obj, trId, false);

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
				
				PreparedStatement pst = conn.prepareStatement(updSql.toString());
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
					StringBuffer sql = new StringBuffer("INSERT INTO " + mtName
							+ " (c_obj_id,c_tr_id," + cname);
					if (isArray) {
						sql.append(",c_index");
						sql.append(",c_id");
					}
					sql.append(") VALUES (?,?,?");
					if (isArray) {
						sql.append("," + index);
						sql.append("," + System.nanoTime());
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
				PreparedStatement pst = conn.prepareStatement("UPDATE "
						+ getClassTableName(attr.classId) + " SET " + cname
						+ "=? WHERE c_obj_id=? AND c_tr_id=?");
				setValue(pst, 1, attr.typeClassId, value);
				pst.setLong(2, obj.id);
				pst.setLong(3, trId);
				pst.executeUpdate();
				pst.close();
			}
			if(replLog)
				logDataChanges(obj, attr.id, langId, trId);
		} catch (SQLException e) {
			String msg = "Ошибка при установке значения атрибута " + obj + "." + attr.name + "=" + value;
			throw convertException(msg, 0, e);
		} catch (Exception e) {
			throw new DriverException(e);
		}
	}

	@Override
	public void setForeignKeysEnabled(boolean enabled) throws DriverException {
	}

	@Override
	public void rollbackLongTransaction(long trId) throws DriverException {
		try {
			saveDataLog();
			
			QueryRunner qr = new QueryRunner(true);

			Set<Long> clsIds = new HashSet<Long>();
			PreparedStatement pst = conn
					.prepareStatement("SELECT DISTINCT c_parent_id FROM t_changes,t_clinks"
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
					"DELETE "+getClassTableName(99)+" FROM "+getClassTableName(99)+","+getDBPrefix()+"t_changes WHERE c_obj_id=c_object_id " +
					"AND c_tr_id=? AND c_attr_id=1", trId);

			rollbackDataLog(trId);
		} catch (SQLException e) {
			throw convertException(e);
		}
	}
	
	protected String getSqlString(Element xml, String colName) {
		String operator_ = operMap.get(getStringParam(xml, "operFlr")), sql_mean_ = "", val_;
		String num_ = getStringParam(xml, "compFlr");
		int dataSelect_ = num_.equals("") ? 0 : Integer.valueOf(num_);
		String rightStr_ = getStringParam(xml.getChild("valFlr"), "exprFlr");
		if (dataSelect_ == 1 && rightStr_ != null && !rightStr_.equals(""))
			rightStr_ = rightStr_.trim();
		if (operator_ == null || operator_.equals(""))
			return "";
		if (operator_.equals("содержит")) {
			if (dataSelect_ == 1)
				val_ = " [" + rightStr_ + "Like%%" + colName + "]";
			else
				val_ = " Like N'%" + rightStr_ + "%'";
		} else if (operator_.equals("начинается с")) {
			if (dataSelect_ == 1)
				val_ = " [" + rightStr_ + "Like%" + colName + "]";
			else
				val_ = " Like N'" + rightStr_ + "%'";
		} else if (operator_.equals("включает")) {
			if (dataSelect_ == 1)
				val_ = " IN ([" + rightStr_ + "])";
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
				val_ = operator_ + "[" + rightStr_ + "]";
			else
				val_ = operator_ + "N'" + rightStr_.trim() + "'";
		}
		if (dataSelect_ == 1
				&& (operator_.equals("содержит") || operator_
						.equals("начинается с"))) {
			assert val_ != null;
			sql_mean_ += val_.trim();
		} else {
			assert val_ != null;
			sql_mean_ += colName + " " + val_.trim();
		}
		return sql_mean_;
	}

	protected String getSqlMemo(Element xml, String colName) {
		String operator_ = operMap.get(getStringParam(xml, "operFlr")), sql_mean_ = "", val_;
		String num_ = getStringParam(xml, "compFlr");
		int dataSelect_ = num_.equals("") ? 0 : Integer.valueOf(num_);
		String rightStr_ = getStringParam(xml.getChild("valFlr"), "exprFlr");
		if (dataSelect_ == 1 && rightStr_ != null && !rightStr_.equals(""))
			rightStr_ = rightStr_.trim();
		if (operator_ == null || operator_.equals(""))
			return "";
		if (operator_.equals("=")) {
			if (dataSelect_ == 1)
				val_ = " Like [" + rightStr_ + "]";
			else
				val_ = "Like N'" + rightStr_ + "'";
		} else if (operator_.equals("содержит")) {
			if (dataSelect_ == 1)
				val_ = " [" + rightStr_ + "Like%%" + colName + "]";
			else
				val_ = "Like N'%" + rightStr_ + "%'";
		} else if (operator_.equals("начинается с")) {
			if (dataSelect_ == 1)
				val_ = " [" + rightStr_ + "Like%" + colName + "]";
			else
				val_ = "Like N'" + rightStr_ + "%'";
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
				val_ = operator_ + "[" + rightStr_ + "]";
			else
				val_ = operator_ + "N'" + rightStr_.trim() + "'";
		}
		if (dataSelect_ == 1
				&& (operator_.equals("содержит") || operator_
						.equals("начинается с"))) {
			assert val_ != null;
			sql_mean_ += val_.trim();
		} else {
			assert val_ != null;
			sql_mean_ += colName + " " + val_.trim();
		}

		return sql_mean_;
	}

	@Override
	protected long getLastClassId() throws SQLException {
		return getLastInsertId();//"t_classes");
	}

	@Override
	protected long getLastAttributeId() throws SQLException {
		return getLastInsertId();//"t_attrs");
	}

	@Override 
	protected long getLastIndexId() throws SQLException{
		return getLastInsertId();//"t_indexes");
	}
	
	@Override
	protected long getLastMethodId() throws SQLException {
		return getLastInsertId();//"t_methods");
	}

	@Override
	protected long getLastObjectId() throws SQLException {
		return getLastInsertId();//"ct99");
	}
	
	//Созданием таблицы для хранения информации о многостолбцовых индексах
	@Override
	protected void createIndexTables(Connection conn) throws SQLException{
		Statement st = conn.createStatement();
		String sql = "";
		
		sql =
			"CREATE TABLE t_indexes(" +
			" c_id BIGINT NOT NULL IDENTITY," +
			" c_uid CHAR(36) NOT NULL," +
			" c_class_id INTEGER NOT NULL," +
			" c_is_multilingual BIT NOT NULL," +
			" PRIMARY KEY(c_id)," +
			" FOREIGN KEY(c_class_id) REFERENCES t_classes(c_id) ON DELETE NO ACTION" +
			")" +
			"";
		st.executeUpdate(sql);
		
		sql = 
			"CREATE TABLE t_indexkeys(" +
			" c_index_id BIGINT NOT NULL," +
			" c_attr_id INTEGER NOT NULL," +
			" c_keyno BIGINT NOT NULL," +
			" c_is_desc BIT NOT NULL," +
			" FOREIGN KEY(c_index_id) REFERENCES t_indexes(c_id) ON DELETE NO ACTION," +
			" FOREIGN KEY(c_attr_id) REFERENCES t_attrs(c_id) ON DELETE NO ACTION" +
			")";
		st.executeUpdate(sql);
				
		st.close();
	}
	
	@Override
	protected long getLastInsertId() throws SQLException {
		long res = -1;
		try (
			Statement st = conn.createStatement();
			ResultSet set = st.executeQuery("SELECT @@IDENTITY AS idn");
			) {
			
			if (set.next()) {
				res = set.getLong(1);
			} else {
				throw new SQLException("Failed to get last ID");
			}
		}
		return res;
	}

	@Override
	protected long upgradeImpl(long ver) throws SQLException, DriverException {
		version = ver;
		if (version <= 3) {
			QueryRunner qr = new QueryRunner(true);
			Statement ust = conn.createStatement();
			
			// Удаление ошибочно созданных записей в таблице ct99
			ust.executeUpdate("DELETE FROM ct99 WHERE c_obj_id IS NULL");
			
			// Удаление неправильно созданных атрибутов
			class DeleteWrongAttributes implements ResultSetHandler<Object> {
			    public Object handle(ResultSet rs) throws SQLException {
			        while (rs.next()) {
			            long id = rs.getLong("c_id");
			            try { 
			            	deleteAttribute(id); 
			            } catch (DriverException e) {
			            	log.error(e, e);
			            }
			        }
			        return null;
			    }
			}
			qr.query(conn, "SELECT c_id FROM t_attrs WHERE c_type_id > 99 AND c_is_multilingual = 1", new DeleteWrongAttributes());
		
			// Удаляем не нужный индекс
			PreparedStatement pst = conn.prepareStatement("SELECT COUNT(*) FROM sysobjects WHERE name=?");
			pst.setString(1, "idx_ct99_uid_tr");
			ResultSet res = pst.executeQuery();
			res.next();
			if (res.getLong(1) > 0) {
				try {
					ust.executeUpdate("DROP INDEX ct99.idx_ct99_uid_tr");
				} catch (SQLException e) {
					
				}
			}
			res.close();
			pst.close();
			
			// Запрос для нахождения имени ограничения по названию AT
			PreparedStatement fkPst = conn.prepareStatement("SELECT so.name"
					+ " FROM sysobjects so, sysobjects t, sysreferences sr,"
					+ " syscolumns sc"
					+ " WHERE so.type='F' AND t.id=so.parent_obj"
					+ " AND sr.constid=so.id AND sr.fkeyid=sc.id"
					+ " AND sr.fkey1=sc.colid"
					+ " AND t.name=? AND sc.name=?");

			// Запрос для нахождения имени PK для таблицы
			PreparedStatement pkPst = conn
					.prepareStatement("SELECT so.name from sysobjects so, sysobjects t"
							+ " WHERE so.xtype='PK' AND t.id=so.parent_obj AND t.name=?");

			// Запрос для нахождения имени default constraint для таблицы
			PreparedStatement dfPst = conn
					.prepareStatement("select sd.name from syscolumns sc, sysobjects sd, sysobjects st"
							+ " where sd.id=sc.cdefault and st.id=sc.id and st.name=? and sc.name='c_is_del'");

			// Удаляем все колонки для которых отсутствует запись в t_attrs
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(
					"SELECT so.name, sc.name FROM sysobjects so, syscolumns sc"
					+ " WHERE so.id=sc.id AND sc.name like 'cm%' AND NOT EXISTS"
					+ " (SELECT c_id FROM t_attrs at WHERE sc.name='cm'+CONVERT(VARCHAR,at.c_id))");
			while (rs.next()) {
				String tbName = getSanitizedString(rs, 1);
				String cmName = getSanitizedString(rs, 2);
				String fkName = getCnrName(fkPst, tbName, cmName);
				if (fkName != null) {
					ust.executeUpdate("ALTER TABLE " + tbName + " DROP CONSTRAINT " + fkName);
				}
				ust.executeUpdate("ALTER TABLE " + tbName + " DROP COLUMN " + cmName);
			}
			st.close();

			// Обрабатываем таблицы атрибутов. Проход 1.
			List<KrnAttribute> attrs = qr.query(conn, "SELECT * FROM t_attrs", new AttrResultSetHandler());

			for (KrnAttribute attr : attrs) {
				log.info("Attribute " + attr.id + " pass 1");
				String ctName = getClassTableNameComp(attr.classId);
				String vtName = getClassTableNameComp(attr.typeClassId);
				String atName = getAttrTableName(attr);
				String cmName = getColumnName(attr);

				// Если атрибут объектного типа
				if (attr.typeClassId > 99) {
					String fkName = getAttrFKName(attr.id);
					String idxName = "idx" + attr.classId + "_" + attr.id;
					if (attr.collectionType == COLLECTION_NONE) {
						// удаляем FK
						ust.executeUpdate("ALTER TABLE " + ctName
								+ " DROP CONSTRAINT " + fkName);
						// удаляем индекс
						if (attr.isIndexed) {
							try {
								ust.executeUpdate("DROP INDEX " + ctName + "."
										+ idxName);
							} catch (SQLException e) {
				            	log.error(e);
							}
						}
						// Меняем ссылку с c_id на c_obj_id
						if (attr.classId == attr.typeClassId) {
							ust.executeUpdate("CREATE TABLE t1 ("
									+ "c_id BIGINT NOT NULL,"
									+ "c_obj_id BIGINT NOT NULL,"
									+ "PRIMARY KEY(c_id))");
							ust.executeUpdate("INSERT INTO t1 (c_id,c_obj_id)"
									+ " SELECT c_id,c_obj_id FROM " + ctName);
							ust.executeUpdate("UPDATE " + ctName + " SET "
									+ cmName + "=(SELECT c_obj_id FROM t1"
									+ " WHERE t1.c_id=" + cmName + ") WHERE "
									+ cmName + " IS NOT NULL");
							ust.executeUpdate("DROP TABLE t1");
						} else {
							ust.executeUpdate("UPDATE " + ctName + " SET "
									+ cmName + "=(SELECT c_obj_id FROM "
									+ vtName + " vt WHERE vt.c_id=" + cmName
									+ ") WHERE " + cmName + " IS NOT NULL");
						}
					} else {
						// удаляем (ошибочный) FK
						ust.executeUpdate("ALTER TABLE " + ctName
								+ " DROP CONSTRAINT " + fkName);
						// удаляем (ошибочную) колонку
						ust.executeUpdate("ALTER TABLE " + ctName
								+ " DROP COLUMN " + cmName);
						// удаляем индекс
						if (attr.isIndexed) {
							ust.executeUpdate("DROP INDEX " + atName + "."
									+ idxName);
						}
						// Меняем ссылку с c_id на c_obj_id
						ust.executeUpdate("UPDATE " + atName + " SET " + cmName
								+ "=(SELECT c_obj_id FROM " + vtName
								+ " vt WHERE vt.c_id=" + cmName + ") WHERE "
								+ cmName + " IS NOT NULL AND EXISTS (SELECT c_obj_id FROM "
								+ vtName + " vt1 WHERE vt1.c_id=" + cmName + ")");
					}
				}

				if (attr.isMultilingual
						|| attr.collectionType != COLLECTION_NONE) {
					// Удаляем FK на с_id
					ust.executeUpdate("ALTER TABLE " + atName
							+ " DROP CONSTRAINT "
							+ getCnrName(fkPst, atName, "c_obj_id"));
					// Удаляем PK
					ust.executeUpdate("ALTER TABLE " + atName
							+ " DROP CONSTRAINT " + getCnrName(pkPst, atName));
					// Изменяем тип c_obj_id
					ust.executeUpdate("ALTER TABLE " + atName
							+ " ALTER COLUMN c_obj_id BIGINT NOT NULL");
					// Добавляем колонку для ID транзакции
					ust.executeUpdate("ALTER TABLE " + atName
							+ " ADD c_tr_id BIGINT NOT NULL DEFAULT 0");
					// Добавляем колонку c_del
					ust.executeUpdate("ALTER TABLE " + atName
							+ " ADD c_del BIGINT NOT NULL DEFAULT 0");
					if (attr.isMultilingual) {
						// Изменяем тип c_lang_id
						ust.executeUpdate("ALTER TABLE " + atName
								+ " ALTER COLUMN c_lang_id BIGINT NOT NULL");
					}
					// Меняем ссылку с (c_id) на (c_obj_id, c_tr_id)
					ust.executeUpdate("UPDATE " + atName
							+ " SET c_tr_id=(SELECT c_tr_id FROM " + ctName
							+ " WHERE c_id=" + atName + ".c_obj_id)");
					ust.executeUpdate("UPDATE " + atName
							+ " SET c_obj_id=(SELECT c_obj_id FROM " + ctName
							+ " WHERE c_id=" + atName + ".c_obj_id)");
				}
			}
			
			// Обрабатываем таблицы классов
			List<KrnClass> classes = qr.query(conn,
					"SELECT * FROM t_classes WHERE c_id>10 ORDER BY c_id",
					new ClassResultSetHandler());
			for (KrnClass cls : classes) {
				log.info("Class " + cls.id);
				String ctName = getClassTableName(cls, true);
				// Удаляем PK
				ust.executeUpdate("ALTER TABLE " + ctName + " DROP CONSTRAINT "
						+ getCnrName(pkPst, ctName));
				// Удаляем индекс c_tr_id
				ust.executeUpdate("DROP INDEX " + ctName + ".idx_" + ctName
						+ "_tr");
				// Удаляем индекс c_is_del
				ust.executeUpdate("DROP INDEX " + ctName + ".idx_" + ctName
						+ "_del_obj_tr");
				// Удаляем Default Constraint c c_is_del
				ust.executeUpdate("ALTER TABLE " + ctName + " DROP CONSTRAINT "
						+ getCnrName(dfPst, ctName));
				if (cls.id == 99) {
					// Удаляем индекс idx_ct99_obj
					ust.executeUpdate("DROP INDEX ct99.idx_ct99_obj");
				}
				// Изменяем тип c_obj_id
				ust.executeUpdate("ALTER TABLE " + ctName
						+ " ALTER COLUMN c_obj_id BIGINT NOT NULL");
				// Изменяем тип c_tr_id
				ust.executeUpdate("ALTER TABLE " + ctName
						+ " ALTER COLUMN c_tr_id BIGINT NOT NULL");
				// Изменяем тип c_is_del
				ust.executeUpdate("ALTER TABLE " + ctName
						+ " ALTER COLUMN c_is_del BIGINT NOT NULL");
				// Добавляем Default Constraint на c_is_del
				ust.executeUpdate("ALTER TABLE " + ctName
						+ " ADD DEFAULT 0 FOR c_is_del");
				// Добавляем NOT NULL для c_obj_id
				ust.executeUpdate("ALTER TABLE " + ctName
						+ " ALTER COLUMN c_obj_id BIGINT NOT NULL");
				// Создаем PK
				ust.executeUpdate("ALTER TABLE " + ctName
						+ " ADD PRIMARY KEY(c_obj_id,c_tr_id)");
				// Создаем индекс для c_tr_id
				ust.executeUpdate("CREATE INDEX idx_" + ctName + "_tr ON "
					+ ctName + "(c_tr_id)");
				// Удаляем колонку c_id
				ust.executeUpdate("ALTER TABLE " + ctName + " DROP COLUMN c_id");
			}

			// Обрабатываем таблицы атрибутов. Проход 2.
			for (KrnAttribute attr : attrs) {
				log.info("Attribute " + attr.id + " pass 2");
				String ctName = getClassTableNameComp(attr.classId);
				String atName = getAttrTableName(attr);
				String cmName = getColumnName(attr);
				// Если атрибут объектного типа
				if (attr.typeClassId > 99) {
					if (attr.isMultilingual || attr.collectionType != COLLECTION_NONE) {
						// Изменяем тип cmXXX
						String sql = "ALTER TABLE " + atName
							+ " ALTER COLUMN " + cmName + " BIGINT";
						if (attr.collectionType == COLLECTION_SET) {
							sql += " NOT NULL";
						}
						ust.executeUpdate(sql);
					} else {
						// Изменяем тип cmXXX
						ust.executeUpdate("ALTER TABLE " + ctName
								+ " ALTER COLUMN " + cmName + " BIGINT");
					}

					try {
						updateIndex(attr, true);
					} catch (SQLException e) {
						log.error(e);
					}
				}
				
				if (attr.isMultilingual
						|| attr.collectionType != COLLECTION_NONE) {
					
					// Создаем PK
					StringBuilder sql = new StringBuilder("ALTER TABLE "
							+ atName + " ADD PRIMARY KEY(c_obj_id,c_tr_id");
					if (attr.collectionType == COLLECTION_ARRAY) {
						sql.append(",c_index");
					}
					if (attr.isMultilingual) {
						sql.append(",c_lang_id");
					}
					if (attr.collectionType == COLLECTION_SET) {
						sql.append(",").append(getColumnName(attr));
					}
					sql.append(",").append("c_del)");
					ust.executeUpdate(sql.toString());

					// Создаем FK на (с_obj_id, c_tr_id)
					ust.executeUpdate("ALTER TABLE "
									+ atName
									+ " ADD CONSTRAINT "
									+ getAttrFKName(attr)
									+ " FOREIGN KEY (c_obj_id,c_tr_id) REFERENCES "
									+ ctName
									+ " (c_obj_id,c_tr_id) ON DELETE CASCADE ON UPDATE CASCADE");
				}
			}

			// Добавляем IDENTITY на c_obj_id в таблице ct99
			// ust.executeUpdate(
			// "ALTER TABLE ct99 MODIFY c_obj_id INTEGER PRIMARY KEY IDENTITY");

			ust.close();
			fkPst.close();
			pkPst.close();
			dfPst.close();
			version = 3;
    	}
    	if (version < 4) {
    		QueryRunner qr = new QueryRunner(true);
    		
			// Удаляем PK
			// Запрос для нахождения имени PK для таблицы
    		qr.update(conn,
    			"ALTER TABLE t_classes ADD c_comment NTEXT");

    		qr.update(conn, "ALTER TABLE t_attrs ADD c_rattr_id INTEGER");
    		qr.update(conn, "ALTER TABLE t_attrs ADD c_sattr_id INTEGER");
    		qr.update(conn, "ALTER TABLE t_attrs ADD c_sdesc INTEGER");
    		qr.update(conn,
				"ALTER TABLE t_attrs ADD FOREIGN KEY (c_rattr_id) REFERENCES t_attrs(c_id)");
    		qr.update(conn,
				"ALTER TABLE t_attrs ADD FOREIGN KEY (c_sattr_id) REFERENCES t_attrs(c_id)");
    		qr.update(conn, "ALTER TABLE t_attrs ADD c_comment NTEXT");

			qr.update(conn, "ALTER TABLE t_methods ADD c_comment NTEXT");
			version = 4;
    	}
    	if (version < 5) {
    		QueryRunner qr = new QueryRunner(true);
    		PreparedStatement fkPst = conn.prepareStatement(
    				"SELECT so.name FROM sysobjects so, sysobjects t, sysreferences sr,"
    				+ " syscolumns sc1, syscolumns sc2"
    				+ " WHERE so.type='F' AND t.id=so.parent_obj"
    				+ " AND sr.constid=so.id AND sr.fkeyid=sc1.id"
    				+ " AND sr.fkey1=sc1.colid AND sr.fkeyid=sc2.id AND sr.fkey2=sc2.colid"
    				+ " AND t.name=? AND sc1.name='c_obj_id' AND sc2.name='c_tr_id'");
    		Statement st = conn.createStatement();
    		ResultSet rs = st.executeQuery("select c_id, c_class_id from t_attrs where c_col_type<>0 or c_is_multilingual=1");
    		while (rs.next()) {
    			long attrId = rs.getLong(1);
    			long clsId = rs.getLong(2);
				KrnAttribute a = db.getAttributeById(attrId);
    			String atName = getAttrTableName(a);
    			fkPst.setString(1, atName);
    			ResultSet rs1 = fkPst.executeQuery();
    			if (rs1.next()) {
    				String fkName = getString(rs1, 1);
    				qr.update(conn, "ALTER TABLE " + atName + " DROP CONSTRAINT " + fkName);
    				qr.update(conn, "ALTER TABLE " + atName + " ADD CONSTRAINT " + fkName + " FOREIGN KEY (c_obj_id,c_tr_id) REFERENCES "+getClassTableNameComp(clsId) + "(c_obj_id,c_tr_id) ON DELETE CASCADE ON UPDATE CASCADE");
    			}
    			rs1.close();
    		}
    		rs.close();
    		st.close();
    		fkPst.close();
    		version = 5;
    	}
    	if (version < 6) {
    		QueryRunner qr = new QueryRunner(true);
    		Statement st = conn.createStatement();
    		ResultSet rs = st.executeQuery(
    				"SELECT c_id,c_class_id,c_is_multilingual" +
    				" FROM t_attrs WHERE c_rattr_id is null and c_col_type=1");
    		while (rs.next()) {
    			long attrId = rs.getLong(1);
				KrnAttribute a = db.getAttributeById(attrId);
    			boolean isMultiLang = rs.getBoolean(3);
    			String atName = getAttrTableName(a);
				qr.update(conn, "ALTER TABLE " + atName + " ADD c_id BIGINT");
				if (isMultiLang) {
					qr.update(conn, "UPDATE " + atName + " SET c_id=c_index*c_lang_id");
				} else {
					qr.update(conn, "UPDATE " + atName + " SET c_id=c_index");
				}
				qr.update(conn, "ALTER TABLE " + atName + " ALTER COLUMN c_id BIGINT NOT NULL");
    		}
    		rs.close();
    		st.close();
    		version = 6;
    	}
    	if (version < 7) {
			log.info("Апгрейд БД до версии 7...");
    		// Зпрос для проверики наличия индекса в таблице.
    		PreparedStatement idxPst = conn.prepareStatement(
    				"select count(*) from sysindexes where name=?");
    			
    		Statement ust = conn.createStatement();
    		Statement qst = conn.createStatement();
    		ResultSet rs = qst.executeQuery("SELECT c_id FROM t_classes WHERE c_id>99");
    		while (rs.next()) {
    			long clsId = rs.getLong(1);
    			log.info("Создание индекса для "+getClassTableNameComp(clsId) + ": ");
    			String tbName = getClassTableNameComp(clsId);
    			String idxName = "uid" + clsId + "idx";
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
			st.executeUpdate("DROP INDEX t_changes.idx_ch_tr");
			// Добавляем колонку c_is_repl в таблицу t_changes
			st.executeUpdate("ALTER TABLE t_changes ADD c_is_repl BIT");
			st.executeUpdate("UPDATE t_changes SET c_is_repl=1");
			st.executeUpdate("ALTER TABLE t_changes ALTER COLUMN c_is_repl BIT NOT NULL");
			// Создаем составной индекс c_tr_id,c_is_repl
			st.executeUpdate("CREATE INDEX ch_tr_repl_idx ON t_changes(c_tr_id,c_is_repl)");
	
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
			st.executeUpdate("ALTER TABLE t_classes ADD c_mod INTEGER");
    		st.executeUpdate("UPDATE t_classes SET c_mod=0");
			st.executeUpdate("ALTER TABLE t_classes ALTER COLUMN c_mod INTEGER NOT NULL");
			// c_cuid
			st.executeUpdate("ALTER TABLE t_classes ADD c_cuid CHAR(36)");
    		st.executeUpdate("UPDATE t_classes SET c_cuid=c_id");
			st.executeUpdate("ALTER TABLE t_classes ALTER COLUMN c_cuid CHAR(36) NOT NULL");
			st.executeUpdate("CREATE UNIQUE INDEX idx_cuid ON t_classes(c_cuid)");

			// Таблица атрибутов
			// c_auid
			st.executeUpdate("ALTER TABLE t_attrs ADD c_auid CHAR(36)");
    		st.executeUpdate("UPDATE t_attrs SET c_auid=c_id");
			st.executeUpdate("ALTER TABLE t_attrs ALTER COLUMN c_auid CHAR(36) NOT NULL");
			st.executeUpdate("CREATE UNIQUE INDEX idx_auid ON t_attrs(c_auid)");

			// Таблица методов
			// c_id
			// Запрос для нахождения имени PK для таблицы
			ResultSet rs = st.executeQuery(
					"SELECT so.name from sysobjects so, sysobjects t" +
					" WHERE so.xtype='PK' AND t.id=so.parent_obj AND t.name='t_methods'");
			if (rs.next()) {
				String pkName = getString(rs, 1);
				st.executeUpdate("ALTER TABLE t_methods DROP CONSTRAINT " + pkName);
			}
			rs.close();
			st.executeUpdate("ALTER TABLE t_methods ADD c_muid CHAR(36)");
    		st.executeUpdate("UPDATE t_methods SET c_muid=c_id");
			st.executeUpdate("ALTER TABLE t_methods ALTER COLUMN c_muid CHAR(36) NOT NULL");
			st.executeUpdate("ALTER TABLE t_methods DROP COLUMN c_id");
			st.executeUpdate("ALTER TABLE t_methods ADD PRIMARY KEY (c_muid)");
			
			// Таблица изменения модели данных
			st.executeUpdate("ALTER TABLE t_changescls ALTER COLUMN c_entity_id CHAR(36) NOT NULL");

			// Таблица блокировок объектов
    		createLocksTable(conn);
			
    		// Переносим существующие блокировки
			KrnClass flowCls = getClassByNameComp("Flow");
			KrnAttribute procAttr = db.getAttributeByName(flowCls.id, "processInstance");
			KrnAttribute lockAttr = db.getAttributeByName(flowCls.id, "lockObjects");
			KrnAttribute pdefAttr = db.getAttributeByName(procAttr.typeClassId, "processDefinition");
			KrnAttribute trnsAttr = db.getAttributeByName(procAttr.typeClassId, "transId");

			String sql = "INSERT INTO t_locks (c_obj_id,c_locker_id,c_flow_id,c_scope)" +
					" SELECT " + getColumnName(lockAttr) + ","+getColumnName(pdefAttr) +
					",MAX(f.c_obj_id)," + Lock.LOCK_FLOW +
					" FROM "+getClassTableNameComp(flowCls.id) + " f," + 
					getAttrTableName(lockAttr) + " la," +
					getClassTableNameComp(procAttr.typeClassId) + " p" +
					" WHERE la.c_obj_id=f.c_obj_id AND p.c_obj_id="+getColumnName(procAttr) +
					" GROUP BY "+getColumnName(lockAttr) + ","+getColumnName(pdefAttr);
					
			st.executeUpdate(sql);

			// Таблица ct99
			// Переносим атрибут 'config' из Object в конкретные классы
			KrnAttribute cfgAttr = db.getAttributeByName(99, "config");
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
			deleteAttribute(db.getAttributeByName(99, "locks").id, false, version);
			// Удаляем из ct99 все записи из зависших транзакций
			// Откат зависших транзакций делаем позже после изменений в структуре
			//  т.к. процедура rollbackLongTransaction работает уже с новой структурой
			List<Long> badTrIds = new ArrayList<Long>();
			rs = st.executeQuery(
					"SELECT DISTINCT c_tr_id FROM ct99 " +
					"WHERE c_tr_id<>0 AND c_tr_id NOT IN (" +
					"SELECT "+getColumnName(trnsAttr) + " FROM "+getClassTableNameComp(trnsAttr.classId) +
					" WHERE "+getColumnName(trnsAttr) +" IS NOT NULL)");
			while (rs.next()) {
				badTrIds.add(rs.getLong(1));
			}
			rs.close();
			PreparedStatement pst = conn.prepareStatement(
					"DELETE FROM ct99 WHERE c_tr_id=?");
			for (Long trId : badTrIds) {
				pst.setLong(1, trId);
				pst.executeUpdate();
			}
			pst.close();
			// Оставляем только по одной записи для каждого объекта
			rs = st.executeQuery(
					"SELECT c_obj_id,c_tr_id FROM ct99 t1 " +
					"WHERE c_tr_id<>0 AND EXISTS (" +
					"SELECT * FROM ct99 t2 " +
					"WHERE t2.c_obj_id=t1.c_obj_id AND t2.c_tr_id=0)");
			List<Pair<Long, Long>> recs = new ArrayList<Pair<Long,Long>>();
			while (rs.next()) {
				recs.add(new Pair<Long, Long>(rs.getLong(1), rs.getLong(2)));
			}
			rs.close();
			pst = conn.prepareStatement(
					"DELETE FROM ct99 WHERE c_obj_id=? AND c_tr_id=?");
			for (Pair<Long, Long> rec : recs) {
				pst.setLong(1, rec.first);
				pst.setLong(2, rec.second);
				pst.executeUpdate();
			}
			pst.close();
			// Удаляем первичный ключ (необходимо для удаления 'c_tr_id')
			// Запрос для нахождения имени PK для таблицы
			rs = st.executeQuery(
					"SELECT so.name from sysobjects so, sysobjects t"
					+ " WHERE so.xtype='PK' AND t.id=so.parent_obj AND t.name='ct99'");
			if (rs.next()) {
				String pkName = getString(rs, 1);
				st.executeUpdate("ALTER TABLE ct99 DROP CONSTRAINT " + pkName);
			}
			rs.close();
			// Удаляем индексы содержащие 'c_tr_id' (необходимо для удаления 'c_tr_id')
			pst = conn.prepareStatement(
					"SELECT si.name FROM syscolumns sc, sysindexkeys sk, " +
					"sysindexes si, sysobjects so " +
					"WHERE so.id=si.id AND sk.id=so.id AND sk.indid=si.indid " +
					"AND sc.colid=sk.colid AND sc.id=so.id " +
					"AND so.name=? AND sc.name=?");
			pst.setString(1, "ct99");
			pst.setString(2, "c_tr_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				String idxName = getString(rs, 1);
				st.executeUpdate("DROP INDEX ct99." + idxName);
			}
			rs.close();
			pst.close();
			// Удаляем default значения для c_is_del (необходимо для удаления колонки)
			rs = st.executeQuery(
					"SELECT sd.name FROM syscolumns sc, sysobjects sd, " +
					"sysobjects st " +
					"WHERE sd.id=sc.cdefault AND st.id=sc.id " +
					"AND st.name='ct99' AND sc.name='c_is_del'");
			if (rs.next()) {
				String cnrName = getString(rs, 1);
				st.executeUpdate("ALTER TABLE ct99 DROP CONSTRAINT " + cnrName);
			}
			rs.close();
			// Удаляем лишние колонки
			st.executeUpdate("ALTER TABLE ct99 DROP COLUMN c_is_del");
			st.executeUpdate("ALTER TABLE ct99 DROP COLUMN c_tr_id");
			// Создаем первичный ключ
			st.executeUpdate("ALTER TABLE ct99 ADD PRIMARY KEY (c_obj_id)");
			// Создаем уникальный индекс для 'c_uid'
			st.executeUpdate("CREATE UNIQUE INDEX uid99idx ON ct99 (c_uid)");
			
			// Удаляем атрибут 'зап табл цикла обмена'
			KrnClass uclsCls = getClassByNameComp("Пользовательский класс");
			KrnAttribute ztexAttr = db.getAttributeByName(uclsCls.id, "зап табл цикла обмена");
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
				deleteAttribute(stbAttr.id, false, version);
			}
			
			// Переносим атрибут в другие классы 'bases'
			KrnClass sysCls = getClassByNameComp("Системный класс");
			KrnAttribute basesAttr = db.getAttributeByName(sysCls.id, "bases");
			cls = getClassByNameComp("ReportPrinter");
			createBasesAttr(basesAttr, cls.id, "34469857-bfb6-49c8-b01f-dfd98805fdb7");

			// Удаляем атрибут 'bases'
			deleteAttribute(basesAttr.id, false, version);

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
			cls = getClassByNameComp("ТЕХНИЧЕСКИЙ КЛАСС");
			makeDummy(cls);
			cls = getClassByNameComp("Технич класс КАДРЫ");
			makeDummy(cls);
			cls = getClassByNameComp("Технический класс БУХУЧЕТ");
			makeDummy(cls);

			// Очищаем кэш классов, т.к. в них вносились изменения нештатно.
			db.reloadCache(this);

			// Теперь, после всех изменений в структуре откатываем повисшие
			//  транзакции
			for (Long trId : badTrIds) {
				rollbackLongTransaction(trId);
			}

			st.close();
    		log.info("Апгрейд БД до версии 9 успешно завершен.");
    		version = 9;
    	}
    	setId("version", 9);
    	commit();
    	// Исключение таблиц для мультиязычных атрибутов
    	if (version < 10) {
    		log.info("Апгрейд БД до версии 10 ...");
			
    		QueryRunner qr = new QueryRunner(true);
    		Statement st = conn.createStatement();
    		// Выбираем все системые языки (сортировка по c_obj_id)
    		List<KrnObject> sysLangs = getSystemLangs();
    		// Выбираем все мультиязычные атрибуты
    		// Количество для отображения прогресса
    		ResultSet rs = st.executeQuery(
    				"SELECT COUNT(*) FROM t_attrs WHERE c_is_multilingual=1");
    		rs.next();
    		int attrCount = rs.getInt(1);
    		rs.close();
    		AttributeRsh rsh = new AttributeRsh();
    		List<KrnAttribute> attrs = qr.query(
    				conn,
    				"SELECT * FROM t_attrs WHERE c_is_multilingual=1",
    				rsh);
    		int p = 0;
    		for (KrnAttribute attr : attrs) {
    			int percent = p++ * 100 / attrCount;
    			if (percent % 10 == 0)
    				log.info(percent + "%");
    			String ctName = getClassTableNameComp(attr.classId);
				String atName = getAttrTableName(attr);
    			String cmName = getColumnName(attr);
    			if (attr.collectionType == COLLECTION_NONE) {
            		// Если не коллекция
					// Создаем колонки в основной таблице
    				createAttributeInDatabase(attr);
    				for (int i = 0; i < sysLangs.size(); i++) {
    					long langId = sysLangs.get(i).id;
    					String ncmName = getColumnName(attr, langId);
    					if (attr.typeClassId != PC_MEMO && attr.typeClassId != PC_BLOB) {
	    					// Копируем данные в колонки
	    					st.executeUpdate("UPDATE " + ctName +
	    							" SET " + ncmName + "=(SELECT " + cmName +
	    							" FROM " + atName + " WHERE " +
	    							atName + ".c_obj_id=" +	ctName + ".c_obj_id AND " +
	    							atName + ".c_tr_id=" + ctName + ".c_tr_id AND " +
	    							atName + ".c_lang_id=" + langId + " AND c_del=0)");
    					} else {
    						// Копируем вручную, т.к. СУБД не может использовать
    						// blob и text в подзапросах
    						PreparedStatement upst = conn.prepareStatement(
    								"UPDATE " + ctName + " SET " + ncmName + "=?" +
    								" WHERE c_obj_id=? AND c_tr_id=?");
    						rs = st.executeQuery(
    								"SELECT c_obj_id,c_tr_id," + cmName +
    								" FROM " + atName +
    								" WHERE c_lang_id=" + langId +
    								" AND " + cmName + " IS NOT NULL AND c_del=0");
    						while (rs.next()) {
    							upst.setLong(2, rs.getLong(1));
    							upst.setLong(3, rs.getLong(2));
    							Object v = getValue(rs, cmName, attr.typeClassId);
    							setValue(upst, 1, attr.typeClassId, v);
    							upst.executeUpdate();
    						}
    						rs.close();
    						upst.close();
    					}
    				}
    				// Удаляем дополнительную таблицу
    				st.executeUpdate("DROP TABLE " + atName);
    			} else {
    				// Если коллекция
    				// Переименовываем дополнительную таблицу
    				CallableStatement cst = conn.prepareCall("{call sp_rename(?,?)}");
    				cst.setString(1, atName);
    				cst.setString(2, atName + "_tmp");
    				cst.executeUpdate();
    				cst.close();
    				// Удаляем FK, иначе СУБД не дает создать новую таблицу
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
    					if (attr.typeClassId != PC_MEMO && attr.typeClassId != PC_BLOB) {
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
    					} else {
    						// Копируем вручную, т.к. СУБД не может использовать
    						// blob и text в подзапросах
    						String sql =
    								"UPDATE " + atName + " SET " + ncmName + "=?" +
    								" WHERE c_obj_id=? AND c_tr_id=? AND c_del=?";
    						if (attr.collectionType == COLLECTION_ARRAY)
    							sql += " AND c_index=?";
    						PreparedStatement upst = conn.prepareStatement(sql);
    						
    						sql = "SELECT c_obj_id,c_tr_id,c_del" + cmName;
    						if (attr.collectionType == COLLECTION_ARRAY)
    							sql += ",c_index";
    						sql += " FROM " + atName + "_tmp WHERE c_lang_id=" + langId +
    								" AND " + cmName + " IS NOT NULL";
    						rs = st.executeQuery(sql);
    						while (rs.next()) {
    							upst.setLong(2, rs.getLong(1));
    							upst.setLong(3, rs.getLong(2));
    							upst.setLong(4, rs.getLong(3));
    							Object v = getValue(rs, cmName, attr.typeClassId);
    							setValue(upst, 1, attr.typeClassId, v);
    							upst.executeUpdate();
    						}
    						rs.close();
    						upst.close();
    					}
    				}
    				// Удаляем старую таблицу
					st.executeUpdate("DROP TABLE " + atName + "_tmp");
    			}
    			// Комитим транзакция для исключения переполнения логов
    			conn.commit();
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
    		st.executeUpdate("ALTER TABLE t_changes ADD c_class_id BIGINT");
    		st.executeUpdate("UPDATE t_changes SET c_class_id=(SELECT c_class_id FROM ct99 WHERE c_obj_id=c_object_id)");
    		st.close();
    		log.info("Апгрейд БД до версии 12 успешно завершен.");
    		version = 12;
    	}
        if (version < 13) {
            log.info("INT to BIGINT");

            // Запрос для нахождения имени ограничения по названию AT
            PreparedStatement fkPst = conn .prepareStatement("SELECT so.name FROM sysobjects so, sysobjects t, sysreferences sr, syscolumns sc WHERE so.type='F' AND t.id=so.parent_obj AND sr.constid=so.id AND sr.fkeyid=sc.id AND sr.fkey1=sc.colid AND t.name=? AND sc.name=?");

            // Запрос для нахождения имени PK для таблицы
            PreparedStatement pkPst = conn .prepareStatement("SELECT so.name from sysobjects so, sysobjects t WHERE so.xtype='PK' AND t.id=so.parent_obj AND t.name=?");

            QueryRunner qr = new QueryRunner(true);
            Statement ust = conn.createStatement();

            // Получить все таблицы классов
            List<KrnClass> classes = (List<KrnClass>) qr.query(conn, "SELECT * FROM t_classes WHERE c_id > 10 ORDER BY c_id",
                    new ClassResultSetHandler());
            List<String> names = new ArrayList<String>();
            List<String> names2 = new ArrayList<String>(); 
            
            log.info("************** Update classe's tables ************** ");
            
            // обновление таблиц классов
            for (KrnClass cls : classes) {
                db.addClass(cls, false);
                String ctName = getClassTableName(cls.id);
                String name = getCnrName(fkPst, ctName, "c_class_id");
                if (name != null) {
                    log.info("Update table "+ctName);
                    names.add(ctName);
                    // Удалить внешний ключ
                    ust.executeUpdate("ALTER TABLE " + ctName + " DROP CONSTRAINT " + name);
                    try {
                        // Удалить индекс
                        ust.executeUpdate("DROP INDEX idx_" + ctName + "_cl ON " + ctName);
                    } catch (SQLException e) {
                        names2.add(ctName);
                    }

                    ust.executeUpdate("ALTER TABLE " + ctName + " ADD c_class_id_1 BIGINT"); // без этого не работает обновление типа столбца
                    ust.executeUpdate("ALTER TABLE " + ctName + " ALTER COLUMN c_class_id BIGINT NOT NULL");
                    ust.executeUpdate("ALTER TABLE " + ctName + " DROP COLUMN c_class_id_1");
                    log.info("Complite!");
                } else {
                    log.info(ctName + " table skipped.");
                }
            }
            log.info("Complite update classe's tables!");

            String name;
            
            log.info("Update table T_INDEXKEYS");
            name = getCnrName(fkPst, "t_indexkeys", "c_attr_id");
            if (name != null) {
                ust.executeUpdate("ALTER TABLE t_indexkeys DROP CONSTRAINT " + name);
                }
            ust.executeUpdate("ALTER TABLE t_indexkeys ALTER COLUMN c_attr_id BIGINT NOT NULL");
            log.info("Complite!");

            log.info("Update table T_CHANGES");
            name = getCnrName(fkPst, "t_changes", "c_attr_id");
            if (name != null) {
                ust.executeUpdate("ALTER TABLE t_changes DROP CONSTRAINT " + name);
            }
            ust.executeUpdate("ALTER TABLE t_changes ALTER COLUMN c_attr_id BIGINT NOT NULL");
            log.info("Complite!");

            log.info("Update table T_CLINKS");
            name = getCnrName(fkPst, "t_clinks", "c_child_id");
            if (name != null) {
                ust.executeUpdate("ALTER TABLE t_clinks DROP CONSTRAINT " + name);
            }
            name = getCnrName(fkPst, "t_clinks", "c_parent_id");
            if (name != null) {
                ust.executeUpdate("ALTER TABLE t_clinks DROP CONSTRAINT " + name);
            }
            ust.executeUpdate("ALTER TABLE t_clinks DROP CONSTRAINT " + getCnrName(pkPst, "t_clinks"));
            ust.executeUpdate("ALTER TABLE t_clinks ALTER COLUMN c_child_id BIGINT NOT NULL");
            ust.executeUpdate("ALTER TABLE t_clinks ALTER COLUMN c_parent_id BIGINT NOT NULL");
            log.info("Complite!");
            
            log.info("Update table T_INDEXES");
            name = getCnrName(fkPst, "t_indexes", "c_class_id");
            if (name != null) {
                ust.executeUpdate("ALTER TABLE t_indexes DROP CONSTRAINT " + name);
            }
            ust.executeUpdate("ALTER TABLE t_indexes ALTER COLUMN c_class_id BIGINT NOT NULL");
            log.info("Complite!");
            
            log.info("Update table T_METHODS");
            name = getCnrName(fkPst, "t_methods", "c_class_id");
            if (name != null) {
                ust.executeUpdate("ALTER TABLE t_methods DROP CONSTRAINT " + name);
            }
            ust.executeUpdate("DROP INDEX idx_method_cl_nm ON t_methods");
            ust.executeUpdate("ALTER TABLE t_methods ALTER COLUMN c_class_id BIGINT NOT NULL");
            log.info("Complite!");
            
            log.info("Update table T_RATTRS");
            name = getCnrName(fkPst, "t_rattrs", "c_attr_id");
            if (name != null) {
                ust.executeUpdate("ALTER TABLE t_rattrs DROP CONSTRAINT " + name);
            }
            name = getCnrName(fkPst, "t_rattrs", "c_rattr_id");
            if (name != null) {
                ust.executeUpdate("ALTER TABLE t_rattrs DROP CONSTRAINT " + name);
            }
            ust.executeUpdate("ALTER TABLE t_rattrs DROP CONSTRAINT " + getCnrName(pkPst, "t_rattrs"));
            ust.executeUpdate("ALTER TABLE t_rattrs ALTER COLUMN c_attr_id BIGINT NOT NULL");
            ust.executeUpdate("ALTER TABLE t_rattrs ALTER COLUMN c_rattr_id BIGINT NOT NULL");
            log.info("Complite!");
            
            log.info("Update table T_ATTRS");
            name = getCnrName(fkPst, "t_attrs", "c_class_id");
            if (name != null) {
                ust.executeUpdate("ALTER TABLE t_attrs DROP CONSTRAINT " + name);
            }
            name = getCnrName(fkPst, "t_attrs", "c_type_id");
            if (name != null) {
                ust.executeUpdate("ALTER TABLE t_attrs DROP CONSTRAINT " + name);
            }
            name = getCnrName(fkPst, "t_attrs", "c_rattr_id");
            if (name != null) {
                ust.executeUpdate("ALTER TABLE t_attrs DROP CONSTRAINT " + name);
            }
            name = getCnrName(fkPst, "t_attrs", "c_sattr_id");
            if (name != null) {
                ust.executeUpdate("ALTER TABLE t_attrs DROP CONSTRAINT " + name);
            }
            ust.executeUpdate("DROP INDEX idx_attr_cl_nm ON t_attrs");
            ust.executeUpdate("ALTER TABLE t_attrs DROP CONSTRAINT " + getCnrName(pkPst, "t_attrs"));
            ust.executeUpdate("ALTER TABLE t_attrs ALTER COLUMN c_id BIGINT NOT NULL");
            ust.executeUpdate("ALTER TABLE t_attrs ALTER COLUMN c_class_id BIGINT NOT NULL");
            ust.executeUpdate("ALTER TABLE t_attrs ALTER COLUMN c_type_id BIGINT NOT NULL");
            ust.executeUpdate("ALTER TABLE t_attrs ALTER COLUMN c_rattr_id BIGINT NULL");
            ust.executeUpdate("ALTER TABLE t_attrs ALTER COLUMN c_sattr_id BIGINT NULL");
            log.info("Complite!");
            
            log.info("Update table T_CLASSES");
            ust.executeUpdate("ALTER TABLE t_classes DROP CONSTRAINT " + getCnrName(pkPst, "t_classes"));
            ust.executeUpdate("ALTER TABLE t_classes ALTER COLUMN c_id BIGINT NOT NULL");
            log.info("Complite!");
            
            log.info("********************* RESTORE KEYs & INDEXes ***********************");
            
            log.info("Update table T_CLASSES");
            ust.executeUpdate("ALTER TABLE t_classes ADD PRIMARY KEY (c_id)");
            log.info("Complite!");
            
            log.info("Update table T_ATTRS");
            ust.executeUpdate("ALTER TABLE t_attrs ADD PRIMARY KEY (c_id)");
            ust.executeUpdate("ALTER TABLE t_attrs ADD FOREIGN KEY (c_class_id) REFERENCES t_classes (c_id)");//CONSTRAINT t_attrs_FK
            ust.executeUpdate("ALTER TABLE t_attrs ADD FOREIGN KEY (c_type_id) REFERENCES t_classes (c_id)");
            ust.executeUpdate("ALTER TABLE t_attrs ADD FOREIGN KEY (c_rattr_id) REFERENCES t_attrs (c_id)");
            ust.executeUpdate("ALTER TABLE t_attrs ADD FOREIGN KEY (c_sattr_id) REFERENCES t_attrs (c_id)");
            ust.executeUpdate("CREATE UNIQUE INDEX idx_attr_cl_nm ON t_attrs(c_class_id,c_name)");
            log.info("Complite!");
            
            log.info("Update table T_RATTRS");
            ust.executeUpdate("ALTER TABLE t_rattrs ADD PRIMARY KEY (c_attr_id, c_rattr_id)");
            ust.executeUpdate("ALTER TABLE t_rattrs ADD FOREIGN KEY (c_attr_id) REFERENCES t_attrs (c_id)");
            ust.executeUpdate("ALTER TABLE t_rattrs ADD FOREIGN KEY (c_rattr_id) REFERENCES t_attrs (c_id)");
            log.info("Complite!");
            
            log.info("Update T_METHODS");
            ust.executeUpdate("ALTER TABLE t_methods ADD FOREIGN KEY (c_class_id) REFERENCES t_classes (c_id) ON DELETE CASCADE");
            ust.executeUpdate("CREATE UNIQUE INDEX idx_method_cl_nm ON t_methods(c_class_id,c_name)");
            log.info("Complite!");
            
            log.info("Update T_INDEXES");
            ust.executeUpdate("ALTER TABLE t_indexes ADD FOREIGN KEY (c_class_id) REFERENCES t_classes (c_id)");
            log.info("Complite!");
            
            log.info("Update table T_CLINKS");
            ust.executeUpdate("ALTER TABLE t_clinks ADD FOREIGN KEY (c_child_id) REFERENCES t_classes (c_id)");
            ust.executeUpdate("ALTER TABLE t_clinks ADD FOREIGN KEY (c_parent_id) REFERENCES t_classes (c_id)");
            ust.executeUpdate("ALTER TABLE t_clinks ADD PRIMARY KEY (c_parent_id, c_child_id)");
            log.info("Complite!");

            log.info("Update table T_CHANGES");
            ust.executeUpdate("ALTER TABLE t_changes ADD FOREIGN KEY (c_attr_id) REFERENCES t_attrs (c_id)");
            log.info("Complite!");

            log.info("Update table T_INDEXKEYS");
            ust.executeUpdate("ALTER TABLE t_indexkeys ADD FOREIGN KEY (c_attr_id) REFERENCES t_attrs (c_id)");
            log.info("Complite!");

            log.info("************** Update classe's tables ************** ");
            
            for (String ctName : names) {
                log.info("Update table "+ctName);
                // добавить FK
                ust.executeUpdate("ALTER TABLE " + ctName + " ADD FOREIGN KEY (c_class_id) REFERENCES t_classes (c_id) ON DELETE CASCADE");
                // добавить индекс
                if (!names2.contains(ctName)) {
                    ust.executeUpdate("CREATE INDEX idx_" + ctName + "_cl ON " + ctName + "(c_class_id)");
                }
                log.info("Complite!");
            }
            log.info("Complite update classe's tables!");
            ust.close();
            log.info("'INT to BIGINT' Complite!");
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
	
	public String getCnrName(PreparedStatement pst, String tName)
			throws SQLException {
		String res = null;
		pst.setString(1, tName);
		ResultSet rs = pst.executeQuery();
		if (rs.next()) {
			res = getString(rs, 1);
		}
		rs.close();
		return res;
	}

	public String getCnrName(PreparedStatement pst, String tName, String cName)
			throws SQLException {
		String res = null;
		pst.setString(1, tName);
		pst.setString(2, cName);
		ResultSet rs = pst.executeQuery();
		if (rs.next()) {
			res = getString(rs, 1);
		}
		rs.close();
		return res;
	}

	protected DriverException convertException(SQLException e) {
		if (e.getErrorCode() == 1205
				|| (e.getNextException() != null && e.getNextException()
						.getErrorCode() == 1205)
				|| e.getMessage().toLowerCase(Constants.OK).contains("deadlock"))
			return new DriverException(e.getMessage(),
					ErrorCodes.ER_LOCK_DEADLOCK);
		else {
			log.error("ErrorCode=" + e.getErrorCode());
			return new DriverException(e);
		}
	}
	
	protected DriverException convertException(String msg, int code, SQLException e) {
		if (e.getErrorCode() == 1205
				|| (e.getNextException() != null && e.getNextException()
						.getErrorCode() == 1205)
				|| e.getMessage().toLowerCase(Constants.OK).contains("deadlock"))
			return new DriverException(msg, ErrorCodes.ER_LOCK_DEADLOCK, e);
		else {
			log.error("ErrorCode=" + e.getErrorCode());
			return new DriverException(msg, code, e);
		}
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
            select.append(tbAlias + "." + rcmName + " AS id,vt.c_tr_id");
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
      	  where.append(rtalias + "." + rcmName + "=?");
            if (tid == 0) {
                where.append(" AND " + rtalias + ".c_tr_id=0");
            } else if (tid != -1) {
            	  // атрибут может быть изменен в текущей транзакции,
            	  // а в 0-транзакции иметь значение = objId
                  //where.append(" AND " + tbAlias + ".c_tr_id IN (0,").append(tid).append(")");
              	where.append(" AND " + rtalias + ".c_tr_id IN (0,?)");
              	rtcnt++;
              	if (rattr.collectionType != COLLECTION_NONE) {
                  	where.append(
                  			" AND NOT EXISTS (SELECT 1 FROM " + rtname + " tt WHERE tt.c_obj_id=" + 
                  			rtalias + ".c_obj_id AND tt.c_tr_id=? AND tt." + rcmName + "=" + rtalias + "." + rcmName + " AND tt.c_del>0");
                  	rtcnt++;
              	} else {
                	where.append(
                			" AND NOT EXISTS (SELECT 1 FROM " + rtname + " tt WHERE tt.c_obj_id=" + 
                			rtalias + ".c_obj_id AND tt.c_tr_id=? AND (tt." + rcmName + " is null or tt." + rcmName + "<>" + rtalias + "." + rcmName + ")");
                  	rtcnt++;
              	}
              	where.append(")");
            }
        } else { 
        	from.append(getClassTableName(cls.id) + " pt");
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
                    if (!sattr.isMultilingual || langId > 0) {
	                    String scmName = getColumnName(sattr, langId);
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
	                    select.append(",CASE WHEN ").append(orderPrefix)
	                    	.append(scmName)
	                    	.append(" IS NULL THEN 1 ELSE 0 END AS scm_is_null,");
	                    select.append(orderPrefix).append(scmName);
	
	                    order.append("scm_is_null,").append(orderPrefix).append(scmName);
	                    if (attr.sDesc) {
	                        order.append(" DESC");
	                    }
	                } else if (attr.collectionType == COLLECTION_SET) {
	              	  order.append("vt.c_obj_id");
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
        String sql = "SELECT " + (valTbl?"DISTINCT ":"") + select + " FROM " + from + " WHERE " + where;
        if (order.length() > 0) {
            sql += " ORDER BY " + order;
        }
        if (limit[0] > 0)
        	sql = addLimit(sql, limit[0], 0);

        // Map для хранения обработанных строк
        Map<Long, Value> revValues = new HashMap<Long, Value>();
        
        ltcnt++;
        PreparedStatement pst = conn.prepareStatement(sql);
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

	/*@Override*/
	protected String addLimit(String sql, int limit, int offset) {
		int pos = sql.toLowerCase(Constants.OK).indexOf("select");
		if (pos != -1) {
			//return sql.substring(0, pos + 6) + " TOP " + limit + sql.substring(pos + 6);
            return sql.substring(0, pos) +" SELECT TOP " + limit + " * FROM ("+sql.substring(pos)+") o";
		}
		return sql;
	}
	
	@Override
	public List<Object[]> getObjects(long classId, long[] objIds,
			AttrRequest req, long tid, int[] limit, int extraColumnCount,
			String info, AttrRequestCache cache,Session session) throws DriverException {

		List<Object[]> res = new ArrayList<Object[]>();

		int lim = limit[0];
		if (objIds == null && lim == 0 && defaultLimit > 0)
			lim = defaultLimit;

		if (cache.sql == null) {
	        // Атрибуты, участвующие в запросе
			List<Pair<KrnAttribute, Integer>> attrs = new ArrayList<Pair<KrnAttribute, Integer>>();
	
			StringBuilder selectSql = new StringBuilder();
			StringBuilder fromSql = new StringBuilder();
			StringBuilder whereSql = new StringBuilder();
			int[] tindex = { 1 };
			int[] cindex = { 4 };
			Map<String, String> aliases = new HashMap<String, String>();
			List<String> trAliases = new ArrayList<String>();
			int vtcnt = processAttrRequest(objIds == null ? classId : 0, req, tid,
					"ct99", -1, attrs, selectSql, fromSql, whereSql, tindex, cindex,
					aliases, trAliases);
	
			StringBuilder sql = new StringBuilder();
			if (selectSql.length() > 0) {
				sql.append("SELECT ").append(selectSql).append(" FROM ").append(fromSql);
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
					res.add(getObjectValue(rs, cache.attrs, null, tid, cal,
							extraColumnCount));
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

			return res;
		} catch (SQLException e) {
			log.error("ERROR INFO:(" + info + ") SQL:" + cache.sql);
			throw convertException(e);
		} finally {
			if (pst != null)
				DbUtils.closeQuietly(pst);
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
		        		fromSql.append(" LEFT JOIN (SELECT c_obj_id,");
		        		
		        		if (a.rAttrId > 0) fromSql.append(cname).append(",");
		        		
		        		fromSql.append(
		        				"MAX(c_tr_id) AS c_tr_id FROM ").append(vtname).append(" GROUP BY c_obj_id) ").append(mtalias)
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

	@Override
	public boolean upgradeTo17(){
		boolean result = false;
		try { //TODO DB tname
        	String sql_istname = "SELECT DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS " +
        			"WHERE TABLE_NAME='t_classes' AND COLUMN_NAME='c_tname';";
			PreparedStatement istnane = conn.prepareStatement(sql_istname);
			ResultSet rst = istnane.executeQuery();
			if(!rst.next()) {
				QueryRunner qr = new QueryRunner(true);
				String sql = "";
    			sql = "ALTER TABLE t_classes ADD c_tname [nvarchar](30) NULL DEFAULT NULL;";
				//log.info("SQL Query...\n " + sql);
    			qr.update(conn, sql);
/*    			sql = "DECLARE CL_Cursor CURSOR FOR SELECT c_id FROM t_classes WHERE c_tname IS NULL; \n"
    			+"OPEN CL_Cursor; \n"
    			+"DECLARE @id VARCHAR(12); \n"
    			+"FETCH NEXT FROM CL_Cursor INTO @id ; \n"
    			+"WHILE @@FETCH_STATUS = 0 \n"
    			+"BEGIN \n"
    			+"UPDATE t_classes SET c_tname='ct'+CAST(@id AS CHAR) WHERE c_id=@id ; \n"
    			+"FETCH NEXT FROM CL_Cursor INTO @id; \n"
    			+"END \n"
    			+"CLOSE CL_Cursor; \n"
    			+"DEALLOCATE CL_Cursor; \n";
				//log.info("SQL Query...\n " + sql);
    			qr.update(conn, sql);
    			sql = "ALTER TABLE t_classes alter column c_tname [nvarchar](32) NOT NULL; \n";
				//log.info("SQL Query...\n " + sql);
    			qr.update(conn, sql);
    			sql = "CREATE UNIQUE NONCLUSTERED INDEX c_tname_UNIQUE ON t_classes(c_tname ASC); \n";
				//log.info("SQL Query...\n " + sql);
    			qr.update(conn, sql);
*/    			
    			sql = "ALTER TABLE t_attrs ADD c_tname [nvarchar](30) NULL DEFAULT NULL;";
    			qr.update(conn, sql);
    			
        		log.info("Апгрейд БД до версии 17 успешно завершен.");
				result = true;
			}
			if(!rst.isClosed()) {
				rst.close();
			}
		} catch (Exception e1) {
			log.error("ERROR to Update 17.\n" + e1.getMessage());
		}
		
		return result;
    }
	
	@Override
	public boolean renameClassTable(long id, String newName) {//TODO r tname
		boolean result = false;
		String sql;
		//newName = setPrefix_ct(newName);
		try {
			sql = "SELECT id=ISNULL(OBJECT_ID(N'[dbo].["+newName+"]'), 0);";
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				if (rs.getLong(1) == 0) {
					QueryRunner qr = new QueryRunner(true);
					String TableName = getClassTableName(id);
					sql = "sp_rename '"+TableName+"','"+newName+"';";
					//log.info("SQL Query...\n " + sql);
					qr.update(conn, sql);
					
					db.getClassById(id).tname = newName;

					boolean isCorrect = false;
					try {
						sql = "SELECT TOP 1 * FROM "+newName+"";
						PreparedStatement st2 = conn.prepareStatement(sql);
						ResultSet rs2 = st2.executeQuery();
						if (rs2.next()) {
							sql = "UPDATE t_classes SET c_tname='" + newName + "' WHERE c_id='" + id + "' ;";
							qr.update(conn, sql);
							isCorrect = true;
						}
					} catch (SQLException e) {
						//
					}
					if (!isCorrect){
						sql = "sp_rename '"+newName+"','"+TableName+"';";
						qr.update(conn, sql);
						return false;
					}
					result = true;
				}
			}
			st.close();
			
		} catch (SQLException e) {
			log.error("ERROR RenameClassTable: " + e.getMessage());
		}
		
		return result;
	}
	
	/*
	 * NOT Optimisation
	 * @see kz.tamur.ods.mysql.MySqlDriver3#renameAttrTable(com.cifs.or2.kernel.KrnAttribute, java.lang.String)
	 */
	@Override
	public boolean renameAttrTable(KrnAttribute attr, String newName) {//TODO r tname
		boolean result = false;
		String sql;
		try {
			if (attr.collectionType != 0){
				sql = "SELECT id=ISNULL(OBJECT_ID(?), 0)";
				PreparedStatement st = conn.prepareStatement(sql);
    			st.setString(1, "[dbo].[" + Funcs.sanitizeSQL(newName) + "]");

				ResultSet rs = st.executeQuery();
				if (rs.next()) {
					if (rs.getLong(1) != 0) {
						QueryRunner qr = new QueryRunner(true);
						String TableName = getAttrTableName(attr);
						String ColumnName = getColumnName(attr);
						if (attr.isMultilingual) {
							List<KrnObject> langs = getSystemLangs();
							for (KrnObject lang : langs) {
								sql = "EXEC sp_rename '"+TableName+"."+ColumnName+"_"+getSystemLangIndex(lang.id)+"', '"+newName+"_"+getSystemLangIndex(lang.id)+"', 'COLUMN';";
								log.info("SQL Query...\n " + sql);
								qr.update(conn, sql);
							}
						} else {
							sql = "EXEC sp_rename '"+TableName+"."+ColumnName+"', '"+newName+"', 'COLUMN';";
							log.info("SQL Query...\n " + sql);
							qr.update(conn, sql);
						}
						
						sql = "EXEC sp_rename '"+TableName+"', '"+newName;
						log.info("SQL Query...\n " + sql);
						qr.update(conn, sql);
						
						sql = "UPDATE t_attrs SET c_tname='" + newName + "' WHERE c_id=" + attr.id;
						log.info("SQL Query...\n " + sql);
						try {
							qr.update(conn, sql);
						} catch (Exception e) {
							log.error(e, e);;
						}

						result = true;
					}
				}
				st.close();
			} else {
				String TableName = getClassTableName(attr.classId);
				String ColumnName = getColumnName(attr);
				
				sql = "SELECT id=ISNULL((SELECT top 1 id FROM syscolumns WHERE id = OBJECT_ID(?) AND name = ?), 0)";
				PreparedStatement st = conn.prepareStatement(sql);
    			st.setString(1, "[dbo].[" + Funcs.sanitizeSQL(TableName) + "]");
    			st.setString(2, Funcs.sanitizeSQL(newName));

    			ResultSet rs = st.executeQuery();
				if (rs.next()) {
					if (rs.getLong(1) == 0) {
						QueryRunner qr = new QueryRunner(true);
						if (attr.isMultilingual) {
							List<KrnObject> langs = getSystemLangs();
							for (KrnObject lang : langs) {
								sql = "EXEC sp_rename '"+TableName+"."+ColumnName+"_"+getSystemLangIndex(lang.id)+"', '"+newName+"_"+getSystemLangIndex(lang.id)+"', 'COLUMN';";
								log.info("SQL Query...\n " + sql);
								qr.update(conn, sql);
							}
						} else {
							sql = "EXEC sp_rename '"+TableName+"."+ColumnName+"', '"+newName+"', 'COLUMN';";
							log.info("SQL Query...\n " + sql);
							qr.update(conn, sql);
						}
						
						result = true;
					}
				}
				st.close();
			}
		} catch (Exception e) {
			log.error("ERROR renameAttrTable: " + e.getMessage());
		}
		
		return result;
	}

	public void upgradeTo22() throws SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 22 ...");
        log.info("В апгрейде у таблицы t_attrs уникальный индекс изменен с c_tname на (`c_class_id`, `c_tname`)");
		log.info("Апгрейд БД до версии 22 успешно завершен.");
		isUpgrading = false;
    }
	
	public void upgradeTo24() throws SQLException, DriverException {
        isUpgrading = true;
        
        log.info("Апгрейд БД до версии 24 ...");
        log.info("В таблице t_changes добавлено поле c_object_uid.");
		
        
        String sql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='t_changes' AND COLUMN_NAME='c_object_uid';";
		PreparedStatement pst = conn.prepareStatement(sql);
		ResultSet set = pst.executeQuery();
		boolean isExist = false;
		if (set.next()) {
			isExist = set.getInt(1) > 0;
		}
		set.close();
		pst.close();
		
		if (!isExist) {
			sql = "ALTER TABLE " + getDBPrefix() + "t_changes ADD c_object_uid NVARCHAR(20) DEFAULT NULL";
			Statement st = conn.createStatement();
			st.executeUpdate(sql);
			st.close();
		}
        
		log.info("Апгрейд БД до версии 24 успешно завершен.");
		
        isUpgrading = false;
    }
	
	@Override
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
	    	String sql = "ALTER TABLE " + tname+" ADD "+cname+" NVARCHAR(MAX) DEFAULT NULL;";
	    	log.info(sql);
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

	    	//Удаляем таблицу относящуюся к множественному атрибуту, а его делаем одиночным и типом мемо
	    	sql = "DROP TABLE " + atname+";";
	    	qr.update(conn, sql);
	    	sql = "UPDATE " + getDBPrefix() + "t_attrs SET c_type_id = 6, c_col_type = 0, c_is_multilingual = 0 WHERE c_id="+attr.id+";";
	    	qr.update(conn, sql);
	    	db.removeAttribute(attr);
	        attr=getAttributeByNameComp(cls, "cutObj");
        }
    	log.info("Апгрейд БД до версии 26 успешно завершен.");
		
        isUpgrading = false;
    }

	@Override
	// Надо реализовать выборку триггеров
	public List<TriggerInfo> getTriggers(KrnClass cls) {
		List<TriggerInfo> list = new ArrayList<TriggerInfo>();
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
			String sql = "DROP TRIGGER " + triggerName + ";";
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
  
		String sql = "ALTER TABLE t_attrs ADD c_before_event_expr IMAGE DEFAULT NULL, c_after_event_expr IMAGE DEFAULT NULL";
		Statement st = conn.createStatement();
		st.executeUpdate(sql);
		st.close();
		
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
	        String cname1 = getColumnName(attr, 1);
	        String cname = getColumnName(attr);
	        String atname = getAttrTableName(attr);
	        Statement st = null;
	        try {
	        	String sql = "ALTER TABLE " + tname+" ADD "+cname+" NVARCHAR(MAX) DEFAULT NULL";
	        	log.info(sql);
	    		st = conn.createStatement();
	    		st.executeUpdate(sql);
	    		st.close();
	        } catch (SQLException e) {
	        	DbUtils.closeQuietly(st);
				log.error("Поле " + cname + " в таблице " + tname + " уже существует!");
		        try {
		        	log.info("Смена типа полня на ТЕКСТ");
		        	String sql = "ALTER TABLE " + tname+" ALTER COLUMN "+cname+" NVARCHAR(MAX) DEFAULT NULL";
		    		st = conn.createStatement();
		    		st.executeUpdate(sql);
		    		st.close();
		        	log.info("Смена типа полня на ТЕКСТ - ОК!");
		        } catch (SQLException ex) {
		        	log.error(ex);
		        	DbUtils.closeQuietly(st);
		        }
		    }
	    	st = conn.createStatement();
	    	ResultSet rs = st.executeQuery("select c_obj_id from " + tname);
	    	PreparedStatement selPst = conn.prepareStatement("select distinct " + (db.isRnDB ? cname1 : cname)  + ",c_index from " + atname + " where c_obj_id=? order by c_index");
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
	    	QueryRunner qr = new QueryRunner(true);
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
	        
			// удаляем FK
			st.executeUpdate(getDropIndexSql(tname, "idx" + ui_attr.classId + "_" + ui_attr.id));

			String sql = "EXEC sp_rename '" + tname + "." + cname_ui + "', '" + cname_ui + "1', 'COLUMN'";
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
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_changescls ADD " +
					"c_time DATETIME," +
					"c_user_id BIGINT DEFAULT NULL," +
					"c_ip CHAR(15) DEFAULT NULL"
					);
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_changescls ADD CONSTRAINT " +
					"DF_CHANGESCLS_C_TIME DEFAULT GETDATE() FOR c_time"
					);
			
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_changes ADD " +
					"c_time DATETIME," +
					"c_user_id BIGINT DEFAULT NULL," +
					"c_ip CHAR(15) DEFAULT NULL"
					);
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_changes ADD CONSTRAINT " +
					"DF_CHANGES_C_TIME DEFAULT GETDATE() FOR c_time"
					);
			
			st.executeUpdate(
					"CREATE TABLE " + getDBPrefix() + "t_vcs_objects ("
					+ "c_id BIGINT PRIMARY KEY IDENTITY,"
					+ "c_obj_id BIGINT NOT NULL,"
					+ "c_obj_uid CHAR(36) NOT NULL,"
					+ "c_obj_class_id BIGINT NOT NULL,"
					+ "c_attr_id BIGINT NOT NULL,"
					+ "c_lang_id BIGINT NOT NULL,"
					+ "c_old_value IMAGE,"
					+ "c_user_id BIGINT NOT NULL,"
					+ "c_ip CHAR(15) NOT NULL,"
					+ "c_mod_start_time DATETIME CONSTRAINT DF_VCS_OBJECTS_C_MOD_START_TIME DEFAULT GETDATE(),"
					+ "c_mod_last_time DATETIME CONSTRAINT DF_VCS_OBJECTS_C_MOD_LAST_TIME DEFAULT GETDATE(),"
					+ "c_fix_start_id BIGINT,"
					+ "c_fix_end_id BIGINT,"
					+ "c_fix_comment NTEXT"
					+ ")");

            st.executeUpdate("CREATE UNIQUE INDEX IDX_VCS_OBJ_ATTR_LANG ON " + getDBPrefix() + "t_vcs_objects(c_obj_id,c_attr_id,c_lang_id,c_fix_end_id)");

			st.executeUpdate(
					"CREATE TABLE " + getDBPrefix() + "t_vcs_model ("
					+ "c_id BIGINT PRIMARY KEY IDENTITY,"
					+ "c_entity_id CHAR(36) NOT NULL,"
					+ "c_type BIGINT NOT NULL,"
					+ "c_action BIGINT NOT NULL,"
					+ "c_old_value IMAGE,"
					+ "c_user_id BIGINT NOT NULL,"
					+ "c_ip CHAR(15) NOT NULL,"
					+ "c_mod_start_time DATETIME CONSTRAINT DF_VCS_MODEL_C_MOD_START_TIME DEFAULT GETDATE(),"
					+ "c_mod_last_time DATETIME CONSTRAINT DF_VCS_MODEL_C_MOD_LAST_TIME DEFAULT GETDATE(),"
					+ "c_fix_start_id BIGINT,"
					+ "c_fix_end_id BIGINT,"
					+ "c_fix_comment NTEXT"
					+ ")");
			
            st.executeUpdate("CREATE UNIQUE INDEX IDX_VCS_MODEL_ENTITY_TYPE_ACTION ON " + getDBPrefix() + "t_vcs_model(c_entity_id,c_type,c_action,c_fix_end_id)");

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
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_dif NTEXT");
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_dif NTEXT");
			
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
					+ "c_time DATETIME CONSTRAINT DF_SYSLOG_C_TIME DEFAULT GETDATE(),"
					+ "c_logger NVARCHAR(50) NOT NULL,"
					+ "c_type NVARCHAR(20) NOT NULL,"
					+ "c_action NVARCHAR(50) NOT NULL,"
					+ "c_user NVARCHAR(50) NOT NULL,"
					+ "c_ip  CHAR(15) NOT NULL,"
					+ "c_host CHAR(30) NOT NULL,"
					+ "c_admin INTEGER NOT NULL,"
					+ "c_message NTEXT"
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
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_name NVARCHAR(255)");
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_name NVARCHAR(255)");
			
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
			st.executeUpdate("DROP INDEX IDX_VCS_OBJ_ATTR_LANG ON " + getDBPrefix() + "t_vcs_objects");
			st.executeUpdate("DROP INDEX IDX_VCS_MODEL_ENTITY_TYPE_ACTION ON " + getDBPrefix() + "t_vcs_model");
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
					"ALTER TABLE " + getDBPrefix() + "t_syslog ADD "
					+ "c_server_id CHAR(50),"
					+ "c_object NVARCHAR(255),"
					+ "c_process NVARCHAR(255)");
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
	        log.info("Изменение типа колонки 'c_dif' таблицы 't_vcs_model' на 'IMAGE'.");
	        log.info("Создание колонки 'c_dif_2'");
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_dif_2 IMAGE");
	        
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
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model DROP COLUMN c_dif");
	        log.info("Переименование колонки 'c_dif_2' в 'c_dif'");
	        
			st.executeUpdate("EXEC sp_rename 't_vcs_model.c_dif_2', 'c_dif', 'COLUMN'");
	        log.info("OK!!!");

	        log.info("Изменение типа колонки 'c_dif' таблицы 't_vcs_objects' на 'IMAGE'.");
	        log.info("Создание колонки 'c_dif_2'");
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_dif_2 IMAGE");
	        
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
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects DROP COLUMN c_dif");
	        log.info("Переименование колонки 'c_dif_2' в 'c_dif'");
			st.executeUpdate("EXEC sp_rename 't_vcs_objects.c_dif_2', 'c_dif', 'COLUMN'");
	        log.info("OK!!!");
	        rs.close();
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
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_methods ADD developer BIGINT");
	        log.info("OK!!!");
		} finally {
			st.close();
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
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_before_del_event_expr IMAGE DEFAULT NULL, c_after_del_event_expr IMAGE DEFAULT NULL");
			st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD c_before_create_obj IMAGE DEFAULT NULL, c_after_create_obj IMAGE DEFAULT NULL, c_before_delete_obj IMAGE DEFAULT NULL, c_after_delete_obj IMAGE DEFAULT NULL");
		} finally {
			st.close();
		}
		log.info("Апгрейд БД до версии 40 успешно завершен.");
        isUpgrading = false;
    }
	
	protected boolean isColumnExists(String t_name, String c_name) {
		boolean isExist = true;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME=? AND COLUMN_NAME=?");
			pst.setString(1, t_name);
			pst.setString(2, c_name);
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
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD c_before_create_obj_tr INT NOT NULL DEFAULT(0)");
				log.info("Колонка c_before_create_obj_tr добавлена.");
			} else {
				log.info("Колонка c_before_create_obj_tr уже создана.");
			}
			if (!isColumnExists("t_classes", "c_after_create_obj_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD c_after_create_obj_tr INT NOT NULL DEFAULT(0)");
				log.info("Колонка c_after_create_obj_tr добавлена.");
			} else {
				log.info("Колонка c_after_create_obj_tr уже создана.");
			}
			if (!isColumnExists("t_classes", "c_before_delete_obj_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD c_before_delete_obj_tr INT NOT NULL DEFAULT(0)");
				log.info("Колонка c_before_delete_obj_tr добавлена.");
			} else {
				log.info("Колонка c_before_delete_obj_tr уже создана.");
			}
			if (!isColumnExists("t_classes", "c_after_delete_obj_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_classes ADD c_after_delete_obj_tr INT NOT NULL DEFAULT(0)");
				log.info("Колонка c_after_delete_obj_tr добавлена.");
			} else {
				log.info("Колонка c_after_delete_obj_tr уже создана.");
			}
			
			if (!isColumnExists("t_attrs", "c_before_event_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_before_event_tr INT NOT NULL DEFAULT(0)");
				log.info("Колонка c_before_event_tr добавлена.");
			} else {
				log.info("Колонка c_before_event_tr уже создана.");
			}
			if (!isColumnExists("t_attrs", "c_after_event_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_after_event_tr INT NOT NULL DEFAULT(0)");
				log.info("Колонка c_after_event_tr добавлена.");
			} else {
				log.info("Колонка c_after_event_tr уже создана.");
			}
			if (!isColumnExists("t_attrs", "c_before_del_event_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_before_del_event_tr INT NOT NULL DEFAULT(0)");
				log.info("Колонка c_before_del_event_tr добавлена.");
			} else {
				log.info("Колонка c_before_del_event_tr уже создана.");
			}
			if (!isColumnExists("t_attrs", "c_after_del_event_tr")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_after_del_event_tr INT NOT NULL DEFAULT(0)");
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
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_syslog ADD c_tab_name NVARCHAR(255)");
			}
			if (!isColumnExists("t_syslog", "c_col_name")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_syslog ADD c_col_name NVARCHAR(255)");
			}
			log.info("Апгрейд БД до версии 48 успешно завершен!");
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
		log.info("Добавление поля c_old_user_id в таблицу t_vcs_objects");
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
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_old_user_id BIGINT");
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
				st.executeUpdate("EXEC sp_rename 't_vcs_objects.c_repl_id', 'c_rimport_id', 'COLUMN'");
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
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_objects ADD c_rexport_id BIGINT");
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
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_old_user_id BIGINT");
				log.info("Колонка c_old_user_id добавлена.");
			} else {
				log.info("Колонка c_old_user_id уже создана.");
			}
			
	        log.info("Удаление индекса IDX_VCS_MODEL_REPL с таблицы t_vcs_model");
			try {
				st.executeUpdate("DROP INDEX IDX_VCS_MODEL_REPL ON " + getDBPrefix() + "t_vcs_model");
		        log.info("Индекс IDX_VCS_REPL с таблицы t_vcs_model удален");
			} catch (Exception e) {
		        log.error("Индекс IDX_VCS_MODEL_REPL с таблицы t_vcs_model не удален!");
			}
			
	        log.info("Переименование колонки 'c_repl_id' в 'c_rimport_id' таблицы 't_vcs_model'");
			try {
				st.executeUpdate("EXEC sp_rename 't_vcs_model.c_repl_id', 'c_rimport_id', 'COLUMN'");
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
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_vcs_model ADD c_rexport_id BIGINT");
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
	        log.info("Апгрейд БД до версии 55 успешно завершен!");
		} catch (DriverException e) {
			log.error("Ошибка при апгрейде БД до версии 55!");
		} 
        isUpgrading = false;
	}

	@Override
	public KrnObject getObjectById(long id) throws DriverException {
        KrnObject obj = null;
        ResultSet rs = null;
        try {
            if (pstObjById == null) {
                pstObjById = conn.prepareStatement("SELECT c_uid,c_class_id FROM "+getClassTableName(99)+" WHITH(NOLOCK) WHERE c_obj_id=?");
        		ExecutionComponent.writeExprToFile("SELECT c_uid,c_class_id FROM "+getClassTableName(99)+" WHITH(NOLOCK) WHERE c_obj_id=?");
            }
            pstObjById.setLong(1, id);
            rs = pstObjById.executeQuery();
            if (rs.next()) {
                obj = new KrnObject(id, Funcs.sanitizeSQL(rs.getString(1)), rs.getLong(2));
            }
        } catch (SQLException e) {
            throw convertException(e);
        } finally {
            DbUtils.closeQuietly(rs);
        }
        return obj;
    }
	
	@Override
	public void setAttributeComment(String attrUid, String comment, boolean log) throws DriverException {
		// Добавление комментария атрибута в запись таблицы t_attrs
		PreparedStatement pstSetAttributeComment = null;
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
		// TODO
	}
	
	@Override
	//Вызов процедуры на исполнение
	public List execProcedure(String nameProcedure, List<Object> vals, List<String> types_in, List<String> types_out) {
		return execProcedure(nameProcedure,vals,types_in, types_out, conn, log);
	}

	public static List execProcedure(String nameProcedure, List<Object> vals, List<String> types_in, List<String> types_out ,Connection connection, Log log) {
		CallableStatement proc = null;
		PreparedStatement pst=null;
        List res = new ArrayList();
		String psql="SELECT name,type FROM sysobjects WHERE type IN ('P','FN') AND name=?";
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
			
		} catch (SQLException e) {
			log.error(e, e);
		} catch (DriverException e) {
			log.error(e, e);
	    } finally {
	    	DbUtils.closeQuietly(proc);
	    	DbUtils.closeQuietly(pst);
	    } 
		return res;
	}

	@Override
	public String getCurrentScheme() throws DriverException {
		return null; //TODO Реализовать.
	}

	@Override
	public String getDropTableSql(String tname) {
		return "DROP TABLE " + getDBPrefix() + "[" + tname + "]";
	}
	
	public void upgradeTo51() throws SQLException {
        isUpgrading = true;
        log.info("Апгрейд БД до версии 51 ...");
		Statement st = conn.createStatement();
		try {
	        log.info("Добавление колонки 'c_thread' в таблицу 't_syslog'...");
			if (!isColumnExists("t_syslog", "c_thread")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_syslog ADD c_thread NVARCHAR(255)");
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
	
	public void upgradeTo59() throws DriverException, SQLException {
		isUpgrading = true;
		log.info("Апгрейд БД до версии 59 ...");
		log.info("Добавление поля c_access_modifier (модификатор доступа атрибута) в таблице t_attrs.");
		Statement st = conn.createStatement();
		try {
			if (!isColumnExists("t_attrs", "c_access_modifier")) {
				st.executeUpdate("ALTER TABLE " + getDBPrefix() + "t_attrs ADD c_access_modifier INT NOT NULL DEFAULT (0)");
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
		String obj_name = Funcs.sanitizeSQL((String) getValue(obj.id, tattr.id, 0, tlangId, trId));
		String sql;
		if(version < VERSION_UL){
			sql = "INSERT INTO " + getDBPrefix() + "t_vcs_objects"
				+ "(c_obj_id,c_obj_uid,c_obj_class_id,c_attr_id,c_lang_id,c_old_value,c_dif,c_user_id,c_ip,c_name,c_repl_id,c_fix_start_id)"
				+ " SELECT ?,?,?,?,?,?,?,?,?,?,?,"
				+ "(SELECT MAX(c_id) FROM " + getDBPrefix() + "t_changes WHERE c_object_id=? AND c_attr_id=? AND c_lang_id=?)";
		}else{
			sql = "INSERT INTO " + getDBPrefix() + "t_vcs_objects"
					+ "(c_obj_id,c_obj_uid,c_obj_class_id,c_attr_id,c_lang_id,c_old_value,c_dif,c_user_id,c_ip,c_name,c_rimport_id,c_fix_start_id)"
					+ " SELECT ?,?,?,?,?,?,?,?,?,?,?,"
					+ "(SELECT MAX(c_id) FROM " + getDBPrefix() + "t_changes WHERE c_object_id=? AND c_attr_id=? AND c_lang_id=?)";
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
				pst.setNull(7, Types.BLOB);
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
	
	@Override
	protected void createVcsModelRecord(int type, int action, Object oldObj, Object newObj, byte[] newExpr, Connection conn)
			throws DriverException, SQLException {
		String uid=getVcsUid(newObj);
		String name=getName(uid, type, newObj);
		PreparedStatement pst;
		if(version < VERSION_UL){
			pst = conn.prepareStatement(
				"INSERT INTO " + getDBPrefix() + "t_vcs_model"
				+ "(c_entity_id,c_type,c_action,c_old_value,c_dif,c_user_id,c_ip,c_name,c_repl_id,c_fix_start_id)"
				+ " SELECT ?,?,?,?,?,?,?,?,?,"
				+ "(SELECT MAX(c_id) FROM " + getDBPrefix() + "t_changescls WHERE c_entity_id=? AND c_type=?)");
		}else{
			pst = conn.prepareStatement(
					"INSERT INTO " + getDBPrefix() + "t_vcs_model"
					+ "(c_entity_id,c_type,c_action,c_old_value,c_dif,c_user_id,c_ip,c_name,c_rimport_id,c_fix_start_id)"
					+ " SELECT ?,?,?,?,?,?,?,?,?,"
					+ "(SELECT MAX(c_id) FROM " + getDBPrefix() + "t_changescls WHERE c_entity_id=? AND c_type=?)");
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

	@Override
	public String getSessionId(Connection conn) {
		Statement st = null;
		ResultSet rs = null;
		String SID="";
		try {
			st = conn.createStatement();
			rs = st.executeQuery("SELECT @@SPID AS 'ID', SYSTEM_USER AS 'Login Name', USER AS 'User Name'");
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
	public List<String> showDbLocks() {
		List<String> res=new ArrayList<>();
		//Необходимо имплементировать метод
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
	        						tsql = "ALTER TABLE DROP "+keyTblName+sqlMap.get(keyTblName);
	        					else
	        						tsql = "ALTER TABLE ADD ("+keyTblName+sqlMap.get(keyTblName);
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
	   	String selSql="SELECT CHARACTER_MAXIMUM_LENGTH "
	  	   		+ "FROM INFORMATION_SCHEMA.COLUMNS "
	  	   		+ "WHERE (DATA_TYPE = 'varchar' OR DATA_TYPE = 'nvarchar') "
	  	   		+ "AND TABLE_NAME=? "
	  	   		+ "AND COLUMN_NAME=? "
	  	   		+ (db.getSchemeName()!=null?"AND TABLE_CATALOG=?":"");
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
	        		+ "c_id BIGINT PRIMARY KEY IDENTITY,"
					+ "c_search_str NVARCHAR(2000) NOT NULL,"
					+ "c_obj_uid CHAR(255) NOT NULL,"
					+ "c_ext_field CHAR(255))");
	        
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

}