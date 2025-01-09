package kz.tamur.ods;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.db.Database;
import com.cifs.or2.server.sgds.HexStringOutputStream;

import java.sql.*;
import java.util.*;
import java.util.zip.ZipInputStream;

import java.io.*;

import kz.tamur.DriverException;
import kz.tamur.ods.mssql.MsSqlDriver3;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static kz.tamur.or3.util.Tname.*;

/**
 * Created by IntelliJ IDEA.
 * User: daulet
 * Date: 03.06.2006
 * Time: 9:50:28
 * To change this template use File | Settings | File Templates.
 */
class Setter {
	Database db;
    Driver drv;
    Connection  conn,extConn;
    String separator;
    String dir;
    String dbType;
    long lastObjectId;

	private final static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + Setter.class.getName());
    private final String EMPTY_TEXT = "<!EMPTY!>";

    public Setter(Driver drv, String dirFullPath, String separator) {
        this.drv = drv;
        this.db = drv.getDatabase();
        conn = drv.getConnection();
        this.separator = separator;
        this.dir = dirFullPath;
        if (dirFullPath.length() == 0)
            this.dir = "dbexport";
        dbType = "";
        String db = System.getProperty("TypeDb");
        if (db != null)
            dbType = db;
    }
    
    public Setter(Driver drv, String jndiName) {
        this.drv = drv;
        this.db = drv.getDatabase();
        conn = drv.getConnection();
        extConn = db.getExternalConnection(jndiName);
        dbType = "";
        String db = System.getProperty("TypeDb");
        if (db != null)
            dbType = db;
    }
    abstract class Loader {
        protected PreparedStatement pst;
        protected Map<String, String> values = new TreeMap<String, String>();
        abstract protected void loadRowValues() throws SQLException, DriverException;
        protected void load(String tabName, List<String> fileFields, String row)
                throws SQLException, DriverException {
            parseRow(tabName, fileFields, row);
            try {
            	loadRowValues();
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
        abstract protected String getFileName();
        protected String getClassTableName(long clsId) {
            return drv.getClassTableName(clsId);
        }
        protected String getAttrTableName(KrnAttribute attr) {
            return drv.getAttrTableName(attr);
        }
        protected String getColumnName(KrnAttribute attr) {
            return drv.getColumnName(attr);
        }
        protected String getStringValueByName(String columnName) {
            return values.get(columnName);
        }
        protected String getEncodedStringValueByName(String columnName) {
        	String v = values.get(columnName);
            if (EMPTY_TEXT.equals(v))
            	v = null;
            else {
            	try {
					v = new String(HexStringOutputStream.fromHexString(v), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
            }
            return v;
        }
        protected Timestamp getTimestampValueByName(String columnName) {
        	String value = values.get(columnName);
        	if (value == null || value.length() == 0) {
        		return null;
        	}
        	try {
        		return new Timestamp(Driver2.FILE_TIME_FMT.parse(value).getTime());
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        	return null;
        }
        protected Long getLongValueByName(String columnName) {
        	String value = values.get(columnName);
        	if (value == null || value.length() == 0) {
        		return null;
        	}
            return Long.parseLong(value);
        }
        protected Integer getIntValueByName(String columnName) {
        	String value = values.get(columnName);
        	if (value == null || value.length() == 0) {
        		return null;
        	}
            return Integer.parseInt(value);
        }
        protected Boolean getBooleanValueByName(String columnName) {
            return values.get(columnName).equals("1");
        }
        protected void parseRow(String tabName, List<String> fileFields, String row) {
            values.clear();
            Iterator<String> it = fileFields.iterator();
            String[] sts = split(row, separator);
            int i = 0;
            while (it.hasNext() && i <  sts.length) {
                String fn = it.next();
                String t = sts[i++];
                if (!" ".equals(t)) {
                    values.put(fn, t);
                }
            }
        }
        public void release() {
            try {
            	if (pst != null)
            		pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    class ClassLoader extends Loader{
        protected String getFileName() { return "t_classes"; }
        protected void loadRowValues() throws SQLException, DriverException{
            long id = getLongValueByName("c_id");
            String name = getStringValueByName("c_name");
            long parentId = getLongValueByName("c_parent_id");
            boolean isRepl = getBooleanValueByName("c_is_repl");
            int mod = getIntValueByName("c_mod");
            String uid = getStringValueByName("c_cuid");
            String hexComment = getStringValueByName("c_comment");
            String tname = null;
            if (isVersion(kz.tamur.or3.util.Tname.TnameVersionBD)){
            	tname = getStringValueByName("c_tname");
            	if("".equals(tname))
            		tname=null;
            }
                       
            try {
                System.out.println("name = " + name);
                KrnClass cls = drv.createClass(name, parentId, isRepl, mod, id, uid, false, tname);
                if (hexComment != null) {
                	drv.setClassComment(
                			cls.uid,
                			new String(HexStringOutputStream.fromHexString(hexComment),	"UTF-8"),
                			false
                	);
                }
                
                byte[] b = HexStringOutputStream.fromHexStringNullable(getStringValueByName("c_before_create_obj"));
                if (b != null) {
                	boolean is0Tr = "0".equals(getStringValueByName("c_before_create_obj_tr"));
                	drv.setClsTriggerEventExpression(new String(b, "UTF-8"), id, 0, is0Tr, false);
                }
                b = HexStringOutputStream.fromHexStringNullable(getStringValueByName("c_after_create_obj"));
                if (b != null) {
                	boolean is0Tr = "0".equals(getStringValueByName("c_after_create_obj_tr"));
                	drv.setClsTriggerEventExpression(new String(b, "UTF-8"), id, 1, is0Tr, false);
                }
                b = HexStringOutputStream.fromHexStringNullable(getStringValueByName("c_before_delete_obj"));
                if (b != null) {
                	boolean is0Tr = "0".equals(getStringValueByName("c_before_delete_obj_tr"));
                	drv.setClsTriggerEventExpression(new String(b, "UTF-8"), id, 2, is0Tr, false);
                }
                b = HexStringOutputStream.fromHexStringNullable(getStringValueByName("c_after_delete_obj"));
                if (b != null) {
                	boolean is0Tr = "0".equals(getStringValueByName("c_after_delete_obj_tr"));
                	drv.setClsTriggerEventExpression(new String(b, "UTF-8"), id, 3, is0Tr, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SQLException(e.getMessage());
            }
        }
    }

    class AttributeLoader extends Loader{
        protected String getFileName() { return "t_attrs"; }
        protected void loadRowValues() throws SQLException, DriverException{
            long id = getLongValueByName("c_id");
            long classId = getLongValueByName("c_class_id");
            String name = getStringValueByName("c_name");
            long typeId = getLongValueByName("c_type_id");
            int colType = getIntValueByName("c_col_type");
            boolean isUnique = getBooleanValueByName("c_is_unique");
            boolean isIndexed = getBooleanValueByName("c_is_indexed");
            boolean isMultilingual = getBooleanValueByName("c_is_multilingual");
            boolean isRepl = getBooleanValueByName("c_is_repl");
            int size = getIntValueByName("c_size");
            Long flags = getLongValueByName("c_flags");
            Long rAttrId = getLongValueByName("c_rattr_id");
            Long sAttrId = getLongValueByName("c_sattr_id");
            boolean sDesc = getBooleanValueByName("c_sdesc");
            String uid = getStringValueByName("c_auid");
            String hexComment = getStringValueByName("c_comment");
            String tname = null;
            if (isVersion(kz.tamur.or3.util.Tname.TnameVersionBD)){
            	tname = getStringValueByName("c_tname");
            	if("".equals(tname))
            		tname=null;
            	else if("END".equals(tname.toUpperCase()))
            			tname="C_END";
            	else if("LAST".equals(tname.toUpperCase()))
        			tname="C_LAST";
            	else if("BEGIN".equals(tname.toUpperCase()))
        			tname="C_BEGIN";
            }
            int accessModifier = 0;
            if (isVersion(kz.tamur.or3.util.Tname.AttrAccessModVersionBD)){
            	Integer accessModifier_ = getIntValueByName("c_access_modifier");
            	accessModifier = (accessModifier_==null?0:accessModifier_.intValue());
            }
            boolean isEncrypt = false;
            if (isVersion(kz.tamur.or3.util.Tname.EncryptColumnsVersionBD)){
            	isEncrypt = getBooleanValueByName("c_is_encrypt");
            }
            try {
                System.out.println("name = " + name + " classId = " + classId + " attrId = " + id);
                KrnAttribute attr = drv.createAttribute(
                		id, uid, classId, typeId, name, colType, isUnique,
                		isIndexed, isMultilingual, isRepl, size, flags, rAttrId,
                		sAttrId, sDesc, false, tname, accessModifier, isEncrypt);
                if (hexComment != null && attr != null) {
                	drv.setAttributeComment(
                		attr.uid,
                		new String(HexStringOutputStream.fromHexString(hexComment),	"UTF-8"),
                		false
                	);
                }

                byte[] b = HexStringOutputStream.fromHexStringNullable(getStringValueByName("c_before_event_expr"));
                if (b != null) {
                	boolean is0Tr = "0".equals(getStringValueByName("c_before_event_tr"));
                	drv.setAttrTriggerEventExpression(new String(b, "UTF-8"), id, 0, is0Tr, false);
                }
                b = HexStringOutputStream.fromHexStringNullable(getStringValueByName("c_after_event_expr"));
                if (b != null) {
                	boolean is0Tr = "0".equals(getStringValueByName("c_after_event_tr"));
                	drv.setAttrTriggerEventExpression(new String(b, "UTF-8"), id, 1, is0Tr, false);
                }
                b = HexStringOutputStream.fromHexStringNullable(getStringValueByName("c_before_del_event_expr"));
                if (b != null) {
                	boolean is0Tr = "0".equals(getStringValueByName("c_before_del_event_tr"));
                	drv.setAttrTriggerEventExpression(new String(b, "UTF-8"), id, 2, is0Tr, false);
                }
                b = HexStringOutputStream.fromHexStringNullable(getStringValueByName("c_after_del_event_expr"));
                if (b != null) {
                	boolean is0Tr = "0".equals(getStringValueByName("c_after_del_event_tr"));
                	drv.setAttrTriggerEventExpression(new String(b, "UTF-8"), id, 3, is0Tr, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                //throw new SQLException(e.getMessage());
            }
        }
    }

    class MethodLoader extends Loader{
        protected String getFileName() { return "t_methods"; }
        protected void loadRowValues() throws SQLException, DriverException{
            String uid = getStringValueByName("c_muid");
            long classId = getLongValueByName("c_class_id");
            String name = getStringValueByName("c_name");
            boolean isCMethod = getBooleanValueByName("c_is_cmethod");
            byte[] expr = HexStringOutputStream.fromHexString(getStringValueByName("c_expr"));
            String hexComment = getStringValueByName("c_comment");
            Long developer = getLongValueByName("developer");
            long dev = (developer == null) ? 0 : developer.longValue(); 
            try {
                System.out.println("name = " + name + " classId = " + classId + " methodId = " + uid);
                KrnMethod m = drv.createMethod(
                		uid, db.getClassById(classId), name, isCMethod, expr, dev, false);
                if (hexComment != null) {
                	drv.setMethodComment(
                			m.uid,
                			new String(HexStringOutputStream.fromHexString(hexComment),	"UTF-8"),
                			false
                	);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SQLException(e.getMessage());
            }
        }
    }

    class ChangesLoader extends Loader{
        protected String getFileName() { return "t_changes"; }
        protected void loadRowValues() throws SQLException, DriverException{
            long id = getLongValueByName("c_id");
            long objId = getLongValueByName("c_object_id");
            String objUid = getStringValueByName("c_object_uid");
            long attrId = getLongValueByName("c_attr_id");
            long langId = getLongValueByName("c_lang_id");
            long trId = getLongValueByName("c_tr_id");
            boolean isRepl = getBooleanValueByName("c_is_repl");
            long classId = getLongValueByName("c_class_id");
            Timestamp time = getTimestampValueByName("c_time");
            Long userId = getLongValueByName("c_user_id");
            String ip = getStringValueByName("c_ip");
            if (EMPTY_TEXT.equals(ip)) ip = null;
            pst.setLong(1, id);
            pst.setLong(2, objId);
            pst.setString(3, objUid);
            pst.setLong(4, attrId);
            pst.setLong(5, langId);
            pst.setLong(6, trId);
            pst.setBoolean(7, isRepl);
            pst.setLong(8, classId);
            pst.setTimestamp(9, time);
            if (userId == null)
            	pst.setNull(10, Types.BIGINT);
            else
            	pst.setLong(10, userId);
            pst.setString(11, ip);
            pst.executeUpdate();
        }

        public ChangesLoader () {
        	try {
        		if (drv instanceof MsSqlDriver3) {
        			pst = conn.prepareStatement("SET IDENTITY_INSERT t_changes ON INSERT INTO t_changes (c_id, c_object_id, c_object_uid, c_attr_id, c_lang_id, c_tr_id, c_is_repl, c_class_id, c_time, c_user_id, c_ip) VALUES(?,?,?,?,?,?,?,?,?,?,?) SET IDENTITY_INSERT t_changes OFF");
        		} else {
        			pst = conn.prepareStatement("INSERT INTO t_changes (c_id, c_object_id, c_object_uid, c_attr_id, c_lang_id, c_tr_id, c_is_repl, c_class_id, c_time, c_user_id, c_ip) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
        		}
            } catch(SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    class ChangesClsLoader extends Loader{
        protected String getFileName() { return "t_changescls"; }
        protected void loadRowValues() throws SQLException, DriverException{
            long id = getLongValueByName("c_id");
            long typeId = getLongValueByName("c_type");
            long action = getLongValueByName("c_action");
            String entityId = getStringValueByName("c_entity_id");
            Timestamp time = getTimestampValueByName("c_time");
            Long userId = getLongValueByName("c_user_id");
            String ip = getStringValueByName("c_ip");
            if (EMPTY_TEXT.equals(ip)) ip = null;
            pst.setLong(1, id);
            pst.setLong(2, typeId);
            pst.setLong(3, action);
            pst.setString(4, entityId);
            pst.setTimestamp(5, time);
            if (userId == null)
            	pst.setNull(6, Types.BIGINT);
            else
            	pst.setLong(6, userId);
            pst.setString(7, ip);
            pst.executeUpdate();
        }

        public ChangesClsLoader () {
            try {
            	if (drv instanceof MsSqlDriver3) {
            		pst = conn.prepareStatement("SET IDENTITY_INSERT t_changescls ON INSERT INTO t_changescls (c_id,c_type,c_action,c_entity_id, c_time, c_user_id, c_ip) VALUES(?,?,?,?,?,?,?) SET IDENTITY_INSERT t_changescls OFF");
            	} else {
            		pst = conn.prepareStatement("INSERT INTO t_changescls (c_id,c_type,c_action,c_entity_id, c_time, c_user_id, c_ip) VALUES(?,?,?,?,?,?,?)");
            	}
            } catch(SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    class VcsModelLoader extends Loader{
        protected String getFileName() { return "t_vcs_model"; }
        protected void loadRowValues() throws SQLException, DriverException {
            long id = getLongValueByName("c_id");
            String entityId = getStringValueByName("c_entity_id");
            long type = getLongValueByName("c_type");
            long action = getLongValueByName("c_action");
            Long userId = getLongValueByName("c_user_id");
            String ip = getStringValueByName("c_ip");
            if (EMPTY_TEXT.equals(ip)) ip = null;
            Timestamp startTime = getTimestampValueByName("c_mod_start_time");
            Timestamp lastTime = getTimestampValueByName("c_mod_last_time");
            Timestamp confirmTime = getTimestampValueByName("c_mod_confirm_time");
            Long fixStartd = getLongValueByName("c_fix_start_id");
            Long fixEndId = getLongValueByName("c_fix_end_id");
            String hexComment = getStringValueByName("c_fix_comment");
            if (EMPTY_TEXT.equals(hexComment))
            	hexComment = null;
            else {
            	try {
					hexComment = new String(HexStringOutputStream.fromHexString(hexComment), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
            }
            	
            String name = getStringValueByName("c_name");
            if (EMPTY_TEXT.equals(name)) name = null;
            Long importId = getLongValueByName("c_rimport_id");
            Long exportId = getLongValueByName("c_rexport_id");
            Long oldUserId = getLongValueByName("c_old_user_id");
            
            String tmp = getStringValueByName("c_old_value");
            byte[] oldValue = (EMPTY_TEXT.equals(tmp)) ? null : HexStringOutputStream.fromHexString(tmp);
            tmp = getStringValueByName("c_dif");
            byte[] diff = (EMPTY_TEXT.equals(tmp)) ? null : HexStringOutputStream.fromHexString(tmp);
            
            setValue(pst, 1, Types.BIGINT, id);
            setValue(pst, 2, Types.CHAR, entityId);
            setValue(pst, 3, Types.INTEGER, type);
            setValue(pst, 4, Types.INTEGER, action);
            setValue(pst, 5, Types.LONGVARBINARY, oldValue);
            setValue(pst, 6, Types.BIGINT, userId);
            setValue(pst, 7, Types.VARCHAR, ip);
            setValue(pst, 8, Types.TIMESTAMP, startTime);
            setValue(pst, 9, Types.TIMESTAMP, lastTime);
            setValue(pst, 10, Types.BIGINT, fixStartd);
            setValue(pst, 11, Types.BIGINT, fixEndId);
            setValue(pst, 12, Types.LONGVARCHAR, hexComment);
            setValue(pst, 13, Types.LONGVARBINARY, diff);
            setValue(pst, 14, Types.VARCHAR, name);
            setValue(pst, 15, Types.BIGINT, importId);
            setValue(pst, 16, Types.BIGINT, oldUserId);
            setValue(pst, 17, Types.BIGINT, exportId);
            setValue(pst, 18, Types.TIMESTAMP, confirmTime);

            pst.executeUpdate();
        }

        public VcsModelLoader() {
        	try {
        		if (drv instanceof MsSqlDriver3) {
        			pst = conn.prepareStatement("SET IDENTITY_INSERT t_vcs_model ON INSERT INTO t_vcs_model (c_id,c_entity_id,c_type,c_action,c_old_value,c_user_id,c_ip,c_mod_start_time,c_mod_last_time,c_fix_start_id,c_fix_end_id,c_fix_comment,c_dif,c_name,c_rimport_id,c_old_user_id,c_rexport_id,c_mod_confirm_time) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) SET IDENTITY_INSERT t_vcs_model OFF");
        		} else {
        			pst = conn.prepareStatement("INSERT INTO t_vcs_model (c_id,c_entity_id,c_type,c_action,c_old_value,c_user_id,c_ip,c_mod_start_time,c_mod_last_time,c_fix_start_id,c_fix_end_id,c_fix_comment,c_dif,c_name,c_rimport_id,c_old_user_id,c_rexport_id,c_mod_confirm_time) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        		}
            } catch(SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    protected void setValue(PreparedStatement pst, int colIndex, int type,
			Object value) throws SQLException {
		if (value == null) {
			pst.setNull(colIndex, type);
		} else if (value instanceof String) {
			pst.setString(colIndex, (String) value);
		} else if (value instanceof Integer) {
			pst.setInt(colIndex, (Integer) value);
		} else if (value instanceof Long) {
			pst.setLong(colIndex, (Long) value);
		} else if (value instanceof Timestamp) {
			pst.setTimestamp(colIndex, (Timestamp) value);
		} else if (value instanceof byte[]) {
			pst.setBytes(colIndex, (byte[]) value);
		}
    }

    class VcsObjectsLoader extends Loader{
        protected String getFileName() { return "t_vcs_objects"; }
        protected void loadRowValues() throws SQLException, DriverException {
            long id = getLongValueByName("c_id");
            long objId = getLongValueByName("c_obj_id");
            String objUid = getStringValueByName("c_obj_uid");
            long classId = getLongValueByName("c_obj_class_id");
            long attrId = getLongValueByName("c_attr_id");
            long langId = getLongValueByName("c_lang_id");
            Long userId = getLongValueByName("c_user_id");
            String ip = getStringValueByName("c_ip");
            if (EMPTY_TEXT.equals(ip)) ip = null;
            Timestamp startTime = getTimestampValueByName("c_mod_start_time");
            Timestamp lastTime = getTimestampValueByName("c_mod_last_time");
            Timestamp confirmTime = getTimestampValueByName("c_mod_confirm_time");
            Long fixStartd = getLongValueByName("c_fix_start_id");
            Long fixEndId = getLongValueByName("c_fix_end_id");
            String hexComment = getStringValueByName("c_fix_comment");
            if (EMPTY_TEXT.equals(hexComment))
            	hexComment = null;
            else {
            	try {
					hexComment = new String(HexStringOutputStream.fromHexString(hexComment), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
            }
            	
            String name = getStringValueByName("c_name");
            if (EMPTY_TEXT.equals(name)) name = null;
            Long importId = getLongValueByName("c_rimport_id");
            Long exportId = getLongValueByName("c_rexport_id");
            Long oldUserId = getLongValueByName("c_old_user_id");
            
            String tmp = getStringValueByName("c_old_value");
            byte[] oldValue = (EMPTY_TEXT.equals(tmp)) ? null : HexStringOutputStream.fromHexString(tmp);
            tmp = getStringValueByName("c_dif");
            byte[] diff = (EMPTY_TEXT.equals(tmp)) ? null : HexStringOutputStream.fromHexString(tmp);
            
            setValue(pst, 1, Types.BIGINT, id);
            setValue(pst, 2, Types.BIGINT, objId);
            setValue(pst, 3, Types.CHAR, objUid);
            setValue(pst, 4, Types.BIGINT, classId);
            setValue(pst, 5, Types.BIGINT, attrId);
            setValue(pst, 6, Types.BIGINT, langId);
            setValue(pst, 7, Types.LONGVARBINARY, oldValue);
            setValue(pst, 8, Types.BIGINT, userId);
            setValue(pst, 9, Types.VARCHAR, ip);
            setValue(pst, 10, Types.TIMESTAMP, startTime);
            setValue(pst, 11, Types.TIMESTAMP, lastTime);
            setValue(pst, 12, Types.BIGINT, fixStartd);
            setValue(pst, 13, Types.BIGINT, fixEndId);
            setValue(pst, 14, Types.LONGVARCHAR, hexComment);
            setValue(pst, 15, Types.LONGVARBINARY, diff);
            setValue(pst, 16, Types.VARCHAR, name);
            setValue(pst, 17, Types.BIGINT, importId);
            setValue(pst, 18, Types.BIGINT, oldUserId);
            setValue(pst, 19, Types.BIGINT, exportId);
            setValue(pst, 20, Types.TIMESTAMP, confirmTime);

            pst.executeUpdate();
        }

        public VcsObjectsLoader() {
        	try {
        		if (drv instanceof MsSqlDriver3) {
        			pst = conn.prepareStatement("SET IDENTITY_INSERT t_vcs_objects ON INSERT INTO t_vcs_objects (c_id,c_obj_id,c_obj_uid,c_obj_class_id,c_attr_id,c_lang_id,c_old_value,c_user_id,c_ip,c_mod_start_time,c_mod_last_time,c_fix_start_id,c_fix_end_id,c_fix_comment,c_dif,c_name,c_rimport_id,c_old_user_id,c_rexport_id,c_mod_confirm_time) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) SET IDENTITY_INSERT t_vcs_objects OFF");
        		} else {
        			pst = conn.prepareStatement("INSERT INTO t_vcs_objects (c_id,c_obj_id,c_obj_uid,c_obj_class_id,c_attr_id,c_lang_id,c_old_value,c_user_id,c_ip,c_mod_start_time,c_mod_last_time,c_fix_start_id,c_fix_end_id,c_fix_comment,c_dif,c_name,c_rimport_id,c_old_user_id,c_rexport_id,c_mod_confirm_time) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        		}
            } catch(SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    class RevAttrsLoader extends Loader{
        protected String getFileName() { return "t_rattrs"; }
        protected void loadRowValues() throws SQLException, DriverException{
            long attrId = getLongValueByName("c_attr_id");
            long rattrId = getLongValueByName("c_rattr_id");
            PreparedStatement pst = conn.prepareStatement(
                "INSERT INTO t_rattrs (c_attr_id, c_rattr_id) VALUES(?,?)"
            );
            pst.setLong(1, attrId);
            pst.setLong(2, rattrId);
            pst.executeUpdate();
            pst.close();
        }
    }
    class IdsLoader extends Loader{
        protected String getFileName() { return "t_ids"; }
        protected void loadRowValues() throws SQLException, DriverException{
            String name  = getStringValueByName("c_name");
            long lastId = getLongValueByName("c_last_id");
            drv.initId(name, lastId);
        }
    }
    class TLocksLoader extends Loader{
        protected String getFileName() { return "t_locks"; }
        protected void loadRowValues() throws SQLException, DriverException{
            long c_obj_id = getLongValueByName("c_obj_id");
            Long c_locker_id = getLongValueByName("c_locker_id");
            Long c_flow_id = getLongValueByName("c_flow_id");
            String c_session_id = getStringValueByName("c_session_id");
            if (EMPTY_TEXT.equals(c_session_id)) c_session_id = null;

            int c_scope = getIntValueByName("c_scope");

            PreparedStatement pst = conn.prepareStatement(
                "INSERT INTO t_locks (c_obj_id,c_locker_id,c_flow_id,c_session_id,c_scope) VALUES(?,?,?,?,?)"
            );
            pst.setLong(1, c_obj_id);
            pst.setLong(2, c_locker_id);
            pst.setLong(3, c_flow_id);
            pst.setString(4, c_session_id);
            pst.setInt(5, c_scope);
            pst.executeUpdate();
            pst.close();
        }
    }
    class TLockMethodsLoader extends Loader{
        protected String getFileName() { return "t_lock_methods"; }
        protected void loadRowValues() throws SQLException, DriverException{
            String c_muid = getStringValueByName("c_muid");
            Long c_flow_id = getLongValueByName("c_flow_id");
            String c_session_id = getStringValueByName("c_session_id");
            if (EMPTY_TEXT.equals(c_session_id)) c_session_id = null;
            int c_scope = getIntValueByName("c_scope");

            PreparedStatement pst = conn.prepareStatement(
                "INSERT INTO t_lock_methods (c_muid,c_flow_id,c_session_id,c_scope) VALUES(?,?,?,?)"
            );
            pst.setString(1, c_muid);
            pst.setLong(2, c_flow_id);
            pst.setString(3, c_session_id);
            pst.setInt(4, c_scope);
            pst.executeUpdate();
            pst.close();
        }
    }
    
    class TMsgLoader extends Loader{
        protected String getFileName() { return "t_msg"; }
        protected void loadRowValues() throws SQLException, DriverException{
            String c_msg = getStringValueByName("c_msg");

            PreparedStatement pst = conn.prepareStatement(
                "INSERT INTO t_msg (c_msg) VALUES(?)"
            );
            pst.setString(1, c_msg);
            pst.executeUpdate();
            pst.close();
        }
    }

    class TSyslogLoader extends Loader {
        public TSyslogLoader() {
			super();
            try {
				pst = conn.prepareStatement(
				        "INSERT INTO t_syslog (c_time,c_logger,c_type,c_action,c_user,c_ip,c_host,c_admin,c_message,c_server_id,c_object,c_process) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)"
				);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
        
		protected String getFileName() { return "t_syslog"; }
        protected void loadRowValues() throws SQLException, DriverException{
            Timestamp c_time = getTimestampValueByName("c_time");
            String c_logger = getStringValueByName("c_logger");
            String c_type = getStringValueByName("c_type");
            String c_action = getStringValueByName("c_action");
            String c_user = getStringValueByName("c_user");
            String c_ip = getStringValueByName("c_ip");
            String c_host = getStringValueByName("c_host");
            boolean c_admin = getBooleanValueByName("c_admin");
            String c_message = getStringValueByName("c_message");
            
            if (EMPTY_TEXT.equals(c_message)) c_message = null;
            else c_message = new String(HexStringOutputStream.fromHexStringNullable(c_message));
            
            String c_server_id = getStringValueByName("c_server_id");
            if (EMPTY_TEXT.equals(c_server_id)) c_server_id = null;
            String c_object = getStringValueByName("c_object");
            if (EMPTY_TEXT.equals(c_object)) c_object = null;
            String c_process = getStringValueByName("c_process");
            if (EMPTY_TEXT.equals(c_process)) c_process = null;
            
            pst.setTimestamp(1, c_time);
            pst.setString(2, c_logger);
            pst.setString(3, c_type);
            pst.setString(4, c_action);
            pst.setString(5, c_user);
            pst.setString(6, c_ip);
            pst.setString(7, c_host);
            pst.setBoolean(8, c_admin);
            pst.setString(9, c_message);
            pst.setString(10, c_server_id);
            pst.setString(11, c_object);
            pst.setString(12, c_process);
            pst.executeUpdate();
        }
    }

    public void parseFile(Loader ldr) throws DriverException {
        String tabName = ldr.getFileName();
        System.out.println("tabName = " + tabName);
        try {
            File file = Funcs.getCanonicalFile(Funcs.getCanonicalFile(dir), tabName);
            try {
                int i = 0;
                try {
                    boolean firstRow = true;
                    List<String> fileFields = null;
                    //InputStream in = new BufferedInputStream(new FileInputStream(file));
                    ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
                    zis.getNextEntry();
                    BufferedReader r = new BufferedReader(new InputStreamReader(zis, "UTF-8"));
                    String row = null;
                    while ((row = r.readLine()) != null) {
                        if (firstRow) {
                            fileFields = parseRow(row);
                            firstRow = false;
                            continue;
                        }

                        ldr.load(tabName, fileFields, row);

                        if (++i%500 == 0) {
                        	log.info("Проверено объектов " + tabName + ": " + i);
                        	drv.commit();
                    	}
                    }
                	drv.commit();
                    r.close();
                    zis.close();
                    System.gc();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                	log.error("Проверено объектов " + tabName + ": " + i+";"+e.getMessage());
                	//throw new DriverException(e.getMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new DriverException(e.getMessage());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DriverException(e.getMessage());
        }
    }
    
    List<String> parseRow(String row) {
        String[] sts = split(row, separator);
        List<String> res = new ArrayList<String>();
        for (int i=0; i < sts.length; i++) {
            String t = sts[i];
            if (!" ".equals(t)) {
                res.add(t);
            }
        }
        return res;
    }
    
    public void setChanges() throws DriverException {
        loadOR3MetaData();
        loadData();
/*        List<KrnObject> exps = drv.getObjects(db.getClassByName("Export").id, 0);
        if (exps.size() > 0) {
            fixReplExportProperties(exps);
        }
*/    
    }
    
    private void loadOR3MetaData () throws DriverException {  
       /* try {
           /* Statement st = conn.createStatement();
            String sql = "select TABLE_NAME from USER_TABLES";
            ResultSet rs = st.executeQuery(sql);
            log.info(sql);
            
            while (rs.next()) {
            	String tableName = Funcs.sanitizeSQL(rs.getString(1)).toLowerCase(Constants.OK);
            	
            	if (!tableName.startsWith("t_") && !"ct99".equals(tableName)) {
            		sql = drv.getDropTableSql(tableName);
            		
            		log.info(Funcs.sanitizeHtml(sql));
            		st.executeUpdate(sql);
            	}
            }
            
            db.clearClasses();

            st.executeUpdate("CREATE TABLE T_MSG(C_MSG VARCHAR2(255) NOT NULL)");
            st.executeUpdate("DELETE FROM T_VCS_OBJECTS");
            st.executeUpdate("DELETE FROM T_VCS_MODEL");
            st.executeUpdate("DELETE FROM T_SYSLOG");
            st.executeUpdate("DELETE FROM T_LOCK_METHODS");
            st.executeUpdate("DELETE FROM T_LOCKS");
            st.executeUpdate("DELETE FROM T_CHANGESCLS");
            st.executeUpdate("DELETE FROM T_CHANGES");
            st.executeUpdate("DELETE FROM T_METHODS");
            st.executeUpdate("DELETE FROM T_RATTRS");
            st.executeUpdate("DELETE FROM T_ATTRS");
            st.executeUpdate("DELETE FROM T_CLINKS");
            st.executeUpdate("DELETE FROM T_CLASSES");
            st.executeUpdate("DELETE FROM T_IDS");
            st.executeUpdate("DELETE FROM CT99");
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        */
        Loader ldr = new IdsLoader();
        parseFile(ldr);
        ldr.release();
        
        ldr = new TMsgLoader();
        parseFile(ldr);
        ldr.release();

        ldr = new ClassLoader();
        parseFile(ldr);
        ldr.release();
        ldr = new AttributeLoader();
        parseFile(ldr);
        ldr.release();
        ldr = new MethodLoader();
        parseFile(ldr);
        ldr.release();
        ldr = new RevAttrsLoader();
        parseFile(ldr);
        ldr.release();

        ldr = new TLocksLoader();
        parseFile(ldr);
        ldr.release();
        ldr = new TLockMethodsLoader();
        parseFile(ldr);
        ldr.release();
        ldr = new ChangesClsLoader();
        parseFile(ldr);
        ldr.release();
        ldr = new ChangesLoader();
        parseFile(ldr);
        ldr.release();

        ldr = new VcsModelLoader();
        parseFile(ldr);
        ldr.release();

        ldr = new VcsObjectsLoader();
        parseFile(ldr);
        ldr.release();

    	ldr = new TSyslogLoader();
        parseFile(ldr);
        ldr.release();
    }

    private void loadData() throws DriverException {
    	// Отключаем проверки FK
    	drv.setForeignKeysEnabled(false);
    	
        KrnClass cls = db.getClassByName("Объект");
        List<KrnClass> classes = new ArrayList<KrnClass>();
        classes.add(cls);
        db.getSubClasses(cls.id, true, classes);
        for (int i = 0; i < classes.size(); i++) {
            cls = (KrnClass) classes.get(i);
            if (cls.id < 99 || cls.isVirtual())
                continue;
            //отключить автовставку
            if(cls.id==99) {
               // drv.avtoIncrementOnOff(true, getClassTableName(cls));
            }
            BufferedReader r = null;
            try {
                File file = Funcs.getCanonicalFile(Funcs.getCanonicalFile(dir), getClassTableName(cls));
                if (!file.exists()) {
                	log.error("ERROR!!! Файл " + file.getAbsolutePath() + " не найден!!!");
                	continue;
                }
                ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
                zis.getNextEntry();
                r = new BufferedReader(new InputStreamReader(zis, "UTF-8"));
                drv.loadClassTable(cls, r, separator);
                r.close();
                drv.commit();
            } catch (Exception e) {
            	log.error(e, e);
            	throw new DriverException(e.getMessage());
            } finally {
            	Utils.closeQuietly(r);
	            //включить автовставку
	            if(cls.id==99) {
	               // drv.avtoIncrementOnOff(false, getClassTableName(cls));
	            }
            }
        }
        for (int i = 0; i < classes.size(); i++) {
            cls = (KrnClass) classes.get(i);
            if (cls.id < 99)
                continue;

            List<KrnAttribute> attrs = db.getAttributesByClassId(cls.id, false);
            for(int a = 0; a < attrs.size(); a++) {
                KrnAttribute attr = attrs.get(a);
                System.out.println("  attr.id = " + attr.id);
                if (attr.collectionType > 0 && attr.rAttrId == 0) {
                    BufferedReader r = null;
                    try {
                        File file = new File(dir, getAttrTableName(attr));
                        if(file.exists()) {
	                        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
	                        zis.getNextEntry();
	                        r = new BufferedReader(new InputStreamReader(zis, "UTF-8"));
	                        drv.loadAttributeTable(attr, r, separator);
	                        drv.commit();
                        }else
                            System.out.println("Не удается найти указанный файл:" + file.getName());
                        	
                    } catch (Exception e) {
                    	log.error(e, e);
                    	throw new DriverException(e.getMessage());
                    } finally {
                    	Utils.closeQuietly(r);
                    }
                }
            }
        }
    	// Включаем проверки FK
    	drv.setForeignKeysEnabled(true);
    }
    
    public void setChangesFromExtDb(int step) throws DriverException{
 	   if(extConn!=null) {
 		   try {
 			   if(step==0) {
 				   loadOR3MetaDataFromExtDb();
 			   }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 		   if(step < 2)  step=2;//в данном месте шаг должен быть не меньше 2
 		   loadDataFromExtDb(step);
 	   }else
		   log.error("Отсутствует связь с внешней базой данных!");
    }
   private void loadOR3MetaDataFromExtDb () throws SQLException{
	   //Загрузка данных в таблицу t_Ids
	   String selSql="SELECT c_name,c_last_id FROM t_ids";
	   String insSql="INSERT INTO t_ids (c_name,c_last_id) VALUES(?,?)";
	   long countRows=0;
	   try(PreparedStatement pstSel=extConn.prepareStatement(selSql);
			PreparedStatement pstIns=conn.prepareStatement(insSql);
		   ResultSet rs=pstSel.executeQuery()){
		   while(rs.next()) {
			   pstIns.setString(1, rs.getString(1));
			   if("installed".equals(rs.getString(1)))
				   pstIns.setLong(2, 0);//устанавливаем в 0 для случая перезаливки после падения
			   else
				   pstIns.setLong(2, rs.getLong(2));
			   pstIns.executeUpdate();
			   countRows++;
		   }
	   }catch(Exception e) {
		   log.error("Ошибка при загрузке таблицы t_ids:"+e.getMessage());
	   }finally {
		   conn.commit();
		   log.info("В таблицу t_ids загружено записей:"+countRows);
	   }
	   //Загрузка данных в таблицу t_msg
  	   selSql="SELECT c_msg FROM t_msg";
  	   insSql="INSERT INTO t_msg (c_msg) VALUES(?)";
  	   countRows=0;
  	   try(PreparedStatement pstSel=extConn.prepareStatement(selSql);
  			PreparedStatement pstIns=conn.prepareStatement(insSql);
  		   ResultSet rs=pstSel.executeQuery()){
  		   while(rs.next()) {
  			   pstIns.setString(1, rs.getString(1));
  			   pstIns.executeUpdate();
  			   countRows++;
  		   }
  	   }catch(Exception e) {
  		   log.error("Ошибка при загрузке таблицы t_msg:"+e.getMessage());
  	   }finally {
  		   conn.commit();
  		   log.info("В таблицу t_msg загружено записей:"+countRows);
  	   }

	   //Загрузка данных в таблицу t_classes
 	   selSql="SELECT c_id,c_name,c_parent_id,c_is_repl,c_mod,c_cuid,c_comment,c_tname,"
 	   		+ "c_before_create_obj,c_before_create_obj_tr,c_after_create_obj,c_after_create_obj_tr,"
 	   		+ "c_before_delete_obj,c_before_delete_obj_tr,c_after_delete_obj,c_after_delete_obj_tr"
 	   		+ " FROM t_classes";
  	   countRows=0;
  	   try (
  			   PreparedStatement pstSel=extConn.prepareStatement(selSql);
  			   ResultSet rs=pstSel.executeQuery()
  			   ) {
  		   while(rs.next()) {
  			   KrnClass cls = drv.createClass(
  					   rs.getString("c_name"),
            		   rs.getLong("c_parent_id"),
            		   rs.getBoolean("c_is_repl"),
            		   rs.getInt("c_mod"),
            		   rs.getLong("c_id") ,
            		   rs.getString("c_cuid") ,
            		   false,
            		   rs.getString("c_tname")
            		   );
  			   if (rs.getString("c_comment") != null) {
  				   drv.setClassComment(cls.uid, rs.getString("c_comment"), false);
               }
  			   
  			   byte[] b = rs.getBytes("c_before_create_obj");
  			   if (b != null) {
  				   boolean is0Tr = 0 == rs.getInt("c_before_create_obj_tr");
  				   drv.setClsTriggerEventExpression(new String(b, "UTF-8"), rs.getLong("c_id"), 0, is0Tr, false);
  			   }
  			   b = rs.getBytes("c_after_create_obj");
  			   if (b != null) {
  				   boolean is0Tr = 0 == rs.getInt("c_after_create_obj_tr");
  				   drv.setClsTriggerEventExpression(new String(b, "UTF-8"), rs.getLong("c_id"), 1, is0Tr, false);
  			   }
  			   b = rs.getBytes("c_before_delete_obj");
  			   if (b != null) {
  				   boolean is0Tr = 0 == rs.getInt("c_before_delete_obj_tr");
  				   drv.setClsTriggerEventExpression(new String(b, "UTF-8"), rs.getLong("c_id"), 2, is0Tr, false);
  			   }
  			   b = rs.getBytes("c_after_delete_obj");
  			   if (b != null) {
  				   boolean is0Tr = 0 == rs.getInt("c_after_delete_obj_tr");
  				   drv.setClsTriggerEventExpression(new String(b, "UTF-8"), rs.getLong("c_id"), 3, is0Tr, false);
  			   }
  			   countRows++;
  		   }
  	   }catch(Exception e) {
  		   log.error("Ошибка при загрузке таблицы t_classes:"+e.getMessage());
  	   }finally {
  		   conn.commit();
  		   log.info("В таблицу t_classes загружено записей:"+countRows);
  	   }
  	   //Выборка размеров полей таблиц для строковых данных, которые отличаются от представленных в t_attrs
  	   Map<Long,Integer> diff_attr_size=new HashMap<>();
  	   selSql="SELECT ta.c_id,ta.c_class_id,utc.DATA_LENGTH "
  	   		+ "FROM USER_TAB_COLUMNS utc, t_attrs ta "
  	   		+ "WHERE utc.DATA_TYPE = 'VARCHAR2' "
  	   		+ "AND utc.TABLE_NAME=CONCAT('CT',ta.c_class_id) "
  	   		+ "AND utc.COLUMN_NAME=CONCAT('CM',ta.c_id) "
  	   		+ "AND ta.c_size > 0 AND ta.c_size <>  utc.DATA_LENGTH";
  	   try(PreparedStatement pstSel=extConn.prepareStatement(selSql);
  			   ResultSet rs=pstSel.executeQuery()){
  		   while(rs.next()) {
  			   diff_attr_size.put(rs.getLong(1), rs.getInt(3));
  		   }
  	   }catch(Exception e) {
  		   log.error("Ошибка при считывании данных из внешней базы", e);
  	   }
  	   //
	   //Загрузка данных в таблицу t_attrs
  	   String selCountSql="SELECT count(c_id) from t_attrs";
  	   selSql="SELECT c_id,c_class_id,c_name,c_type_id,c_col_type,c_is_unique,c_is_indexed,"
  	   		+ "c_is_multilingual,c_is_repl,c_size,c_flags,c_rattr_id,c_sattr_id,c_sdesc,c_auid,c_comment,c_tname,c_access_modifier,"
  	   		+ "c_before_event_expr, c_after_event_expr, c_before_del_event_expr, c_after_del_event_expr, "
  	   		+ "c_before_event_tr, c_after_event_tr, c_before_del_event_tr, c_after_del_event_tr FROM t_attrs";
  	   long countRowsExt=0;
  	   countRows=0;
  	   long attrId=-1;
  	   try(PreparedStatement pstSel=extConn.prepareStatement(selSql);
  			   PreparedStatement pstCountSel=extConn.prepareStatement(selCountSql);
  			   ResultSet rsCount=pstCountSel.executeQuery();
  			   ResultSet rs=pstSel.executeQuery()){
  		   	if(rsCount.next())
  		   		countRowsExt=rsCount.getLong(1);
  		   String s_comment="";
  		   while(rs.next()) {
  			   try {
  				   attrId=rs.getLong("c_id");
  				   long class_id=rs.getLong("c_class_id");
  				   int size=rs.getInt("c_size");
  				   if(diff_attr_size.containsKey(attrId)) {
  					   int old_size=size;
  	  				   size=diff_attr_size.get(attrId);
            		   log.info("Размер атрибута attrId:"+attrId+",classId:"+class_id+" изменен c "+old_size +" на "+size);
  				   }
	               KrnAttribute attr = drv.createAttribute(
	            		   attrId,
	            		   rs.getString("c_auid"),
	            		   class_id,
	            		   rs.getLong("c_type_id"),
	            		   rs.getString("c_name"),
	            		   rs.getInt("c_col_type"),
	            		   rs.getBoolean("c_is_unique"),
	            		   rs.getBoolean("c_is_indexed"),
	            		   rs.getBoolean("c_is_multilingual"),
	            		   rs.getBoolean("c_is_repl"),
	            		   size,
	            		   rs.getInt("c_flags"),
	            		   rs.getLong("c_rattr_id"),
	            		   rs.getLong("c_sattr_id"),
	            		   rs.getBoolean("c_sdesc"),
	            		   false,
	            		   rs.getString("c_tname"),
	            		   rs.getInt("c_access_modifier")
	            		   );
	               if ((s_comment=rs.getString("c_comment")) != null && attr != null) {
	            	   try {
	            		   drv.setAttributeComment(attr.uid,rs.getString("c_comment"),false);
	            	   } catch(Exception e) {
	            		   log.error("Ошибка при записи коментария в таблице t_attrs s_comment:"+s_comment+";"+e.getMessage());
	            	   }
	               }
	                byte[] b = rs.getBytes("c_before_event_expr");
	                if (b != null) {
	                	boolean is0Tr = 0 == rs.getInt("c_before_event_tr");
	                	drv.setAttrTriggerEventExpression(new String(b, "UTF-8"), attrId, 0, is0Tr, false);
	                }
	                b = rs.getBytes("c_after_event_expr");
	                if (b != null) {
	                	boolean is0Tr = 0 == rs.getInt("c_after_event_tr");
	                	drv.setAttrTriggerEventExpression(new String(b, "UTF-8"), attrId, 1, is0Tr, false);
	                }
	                b = rs.getBytes("c_before_del_event_expr");
	                if (b != null) {
	                	boolean is0Tr = 0 == rs.getInt("c_before_del_event_tr");
	                	drv.setAttrTriggerEventExpression(new String(b, "UTF-8"), attrId, 2, is0Tr, false);
	                }
	                b = rs.getBytes("c_after_del_event_expr");
	                if (b != null) {
	                	boolean is0Tr = 0 == rs.getInt("c_after_del_event_tr");
	                	drv.setAttrTriggerEventExpression(new String(b, "UTF-8"), attrId, 3, is0Tr, false);
	                }

  			   }catch(Exception ex) {
        		   log.error("Ошибка при записи в таблицу t_attrs, attrId="+attrId+" ;"+ex.getMessage());
  			   }finally {
	  			   countRows++;
	                if (countRows%1000 == 0) {
	 	  	  		   conn.commit();
	           		   log.info("В таблицу t_attrs загружено записей:"+countRows+"/"+countRowsExt);
	                }
			   }
  		   }
  	   }catch(Exception e) {
  		   log.error("Ошибка при загрузке таблицы t_attrs error attrId:"+attrId+";"+e.getMessage());
  	   }finally {
  		   conn.commit();
  		   log.info("В таблицу t_attrs загружено записей:"+countRows+"/"+countRowsExt);
  	   }
	   //Загрузка данных в таблицу t_methods
  	   selSql = "SELECT c_muid, c_class_id, c_name, c_is_cmethod, c_expr, developer, c_comment FROM t_methods";
  	   countRows = 0;
  	   try(
  			   PreparedStatement pstSel=extConn.prepareStatement(selSql);
  			   ResultSet rs=pstSel.executeQuery();
  			   ) {
  		   while(rs.next()) {
  			   KrnClass cls = db.getClassById(rs.getLong("c_class_id"));
  			   KrnMethod m = drv.createMethod(
  					 rs.getString("c_muid"), cls, rs.getString("c_name"), 
  					 rs.getBoolean("c_is_cmethod"), rs.getBytes("c_expr"), rs.getLong("developer"), false);
               if (rs.getString("c_comment") != null) {
            	   drv.setMethodComment(m.uid, rs.getString("c_comment"), false);
               }
  			   countRows++;
  		   }
  	   } catch(Exception e) {
  		   log.error("Ошибка при загрузке таблицы t_methods:"+e.getMessage());
  	   } finally {
  		   conn.commit();
  		   log.info("В таблицу t_methods загружено записей:"+countRows);
  	   }
    	   
  	   //Загрузка данных в таблицу t_rattrs
  	   selSql = "SELECT c_attr_id, c_rattr_id FROM t_rattrs";
  	   insSql = "INSERT INTO t_rattrs (c_attr_id, c_rattr_id) VALUES(?,?)";
  	   countRows=0;
  	   try(PreparedStatement pstSel=extConn.prepareStatement(selSql);
  			PreparedStatement pstIns=conn.prepareStatement(insSql);
  		   ResultSet rs=pstSel.executeQuery()){
  		   while(rs.next()) {
  			   pstIns.setLong(1, rs.getLong(1));
  			   pstIns.setLong(2, rs.getLong(2));
  			   pstIns.executeUpdate();
  			   countRows++;
  		   }
  	   }catch(Exception e) {
  		   log.error("Ошибка при загрузке таблицы t_rattrs:"+e.getMessage());
  	   }finally {
  		   conn.commit();
  		   log.info("В таблицу t_rattrs загружено записей:"+countRows);
  	   }
  	   
  	   //Загрузка данных в таблицу t_changes
  	   selSql = "SELECT c_id,c_class_id,c_object_id,c_object_uid,c_attr_id,c_lang_id,c_tr_id,c_is_repl,c_time,c_user_id,c_ip FROM t_changes";
  	   insSql = "INSERT INTO t_changes (c_id,c_class_id,c_object_id,c_object_uid,c_attr_id,c_lang_id,c_tr_id,c_is_repl,c_time,c_user_id,c_ip) VALUES"
  	   		+ " (?,?,?,?,?,?,?,?,?,?,?)";
  	   countRows=0;
  	   try(
  			   PreparedStatement pstSel=extConn.prepareStatement(selSql);
  			   PreparedStatement pstIns=conn.prepareStatement(insSql);
  			   ResultSet rs=pstSel.executeQuery()
  		) {
  		   while(rs.next()) {
  			   pstIns.setLong(1, rs.getLong(1));
  			   pstIns.setLong(2, rs.getLong(2));
  			   pstIns.setLong(3, rs.getLong(3));
  			   pstIns.setString(4, rs.getString(4));
  			   pstIns.setLong(5, rs.getLong(5));
  			   pstIns.setLong(6, rs.getLong(6));
  			   pstIns.setLong(7, rs.getLong(7));
  			   pstIns.setBoolean(8, rs.getBoolean(8));
  			   pstIns.setTimestamp(9, rs.getTimestamp(9));
  			   long userId = rs.getLong(10);
  			   if (userId > 0)
  				   pstIns.setLong(10, userId);
  			   else
  				   pstIns.setNull(10, Types.BIGINT);
  			   
  			   pstIns.setString(11, rs.getString(11));
  			   
  			   pstIns.addBatch();
  			   countRows++;
  		   }
  		   pstIns.executeBatch();
  	   } catch(Exception e) {
  		   log.error("Ошибка при загрузке таблицы t_changes:"+e.getMessage());
  	   } finally {
  		   conn.commit();
  		   log.info("В таблицу t_changes загружено записей:"+countRows);
  	   }

  	   //Загрузка данных в таблицу t_changescls
  	   selSql = "SELECT c_id,c_type,c_action,c_entity_id,c_time,c_user_id,c_ip FROM t_changescls";
  	   insSql = "INSERT INTO t_changescls (c_id,c_type,c_action,c_entity_id,c_time,c_user_id,c_ip) VALUES"
  	   		+ " (?,?,?,?,?,?,?)";
  	   countRows=0;
  	   try(
  			   PreparedStatement pstSel=extConn.prepareStatement(selSql);
  			   PreparedStatement pstIns=conn.prepareStatement(insSql);
  			   ResultSet rs=pstSel.executeQuery()
  		) {
  		   while(rs.next()) {
  			   pstIns.setLong(1, rs.getLong(1));
  			   pstIns.setLong(2, rs.getLong(2));
  			   pstIns.setLong(3, rs.getLong(3));
  			   pstIns.setString(4, rs.getString(4));
  			   pstIns.setTimestamp(5, rs.getTimestamp(5));
  			   long userId = rs.getLong(6);
  			   if (userId > 0)
  				   pstIns.setLong(6, userId);
  			   else
  				   pstIns.setNull(6, Types.BIGINT);
  			   
  			   pstIns.setString(7, rs.getString(7));
  			   
  			   pstIns.addBatch();
  			   countRows++;
  		   }
  		   pstIns.executeBatch();
  	   } catch(Exception e) {
  		   log.error("Ошибка при загрузке таблицы t_changescls:"+e.getMessage());
  	   } finally {
  		   conn.commit();
  		   log.info("В таблицу t_changescls загружено записей:"+countRows);
  	   }

  	   //Загрузка данных в таблицу t_locks
  	   selSql = "SELECT c_obj_id,c_locker_id,c_flow_id,c_session_id,c_scope FROM t_locks";
  	   insSql = "INSERT INTO t_locks (c_obj_id,c_locker_id,c_flow_id,c_session_id,c_scope) VALUES"
  	   		+ " (?,?,?,?,?)";
  	   countRows=0;
  	   try(
  			   PreparedStatement pstSel=extConn.prepareStatement(selSql);
  			   PreparedStatement pstIns=conn.prepareStatement(insSql);
  			   ResultSet rs=pstSel.executeQuery()
  		) {
  		   while(rs.next()) {
  			   pstIns.setLong(1, rs.getLong(1));
  			   pstIns.setLong(2, rs.getLong(2));
  			   pstIns.setLong(3, rs.getLong(3));
  			   pstIns.setString(4, rs.getString(4));
  			   pstIns.setLong(5, rs.getLong(5));

  			   pstIns.addBatch();
  			   countRows++;
  		   }
  		   pstIns.executeBatch();
  	   } catch(Exception e) {
  		   log.error("Ошибка при загрузке таблицы t_locks:"+e.getMessage());
  	   } finally {
  		   conn.commit();
  		   log.info("В таблицу t_locks загружено записей:"+countRows);
  	   }

  	   //Загрузка данных в таблицу t_lock_methods
  	   selSql = "SELECT c_muid,c_flow_id,c_session_id,c_scope FROM t_lock_methods";
  	   insSql = "INSERT INTO t_lock_methods (c_muid,c_flow_id,c_session_id,c_scope) VALUES"
  	   		+ " (?,?,?,?)";
  	   countRows=0;
  	   try(
  			   PreparedStatement pstSel=extConn.prepareStatement(selSql);
  			   PreparedStatement pstIns=conn.prepareStatement(insSql);
  			   ResultSet rs=pstSel.executeQuery()
  		) {
  		   while(rs.next()) {
  			   pstIns.setString(1, rs.getString(1));
  			   pstIns.setLong(2, rs.getLong(2));
  			   pstIns.setString(3, rs.getString(3));
  			   pstIns.setLong(4, rs.getLong(4));

  			   pstIns.addBatch();
  			   countRows++;
  		   }
  		   pstIns.executeBatch();
  	   } catch(Exception e) {
  		   log.error("Ошибка при загрузке таблицы t_lock_methods:"+e.getMessage());
  	   } finally {
  		   conn.commit();
  		   log.info("В таблицу t_lock_methods загружено записей:"+countRows);
  	   }
  	   
  	   //Загрузка данных в таблицу T_SEARCH_INDEXES
  	   selSql = "SELECT C_SEARCH_STR,C_OBJ_UID,C_EXT_FIELD FROM T_SEARCH_INDEXES";
  	   insSql = "INSERT INTO T_SEARCH_INDEXES (C_SEARCH_STR,C_OBJ_UID,C_EXT_FIELD) VALUES"
  	   		+ " (?,?,?)";
  	   countRows=0;
  	   try(
  			   PreparedStatement pstSel=extConn.prepareStatement(selSql);
  			   PreparedStatement pstIns=conn.prepareStatement(insSql);
  			   ResultSet rs=pstSel.executeQuery()
  		) {
  		   while(rs.next()) {
  			   pstIns.setString(1, rs.getString(1));
  			   pstIns.setString(2, rs.getString(2));
  			   pstIns.setString(3, rs.getString(3));

  			   pstIns.addBatch();
  			   countRows++;
  		   }
  		   pstIns.executeBatch();
  	   } catch(Exception e) {
  		   log.error("Ошибка при загрузке таблицы t_lock_methods:"+e.getMessage());
  	   } finally {
  		   conn.commit();
  		   log.info("В таблицу t_lock_methods загружено записей:"+countRows);
  	   }


  	   //Загрузка данных в таблицу t_vcs_model
  	   selSql = "SELECT c_id,c_name,c_entity_id,c_type,c_action,c_old_value,c_user_id,c_old_user_id,c_rimport_id,c_rexport_id,c_ip,"
  	   		+ "c_mod_start_time,c_mod_last_time,c_mod_confirm_time,c_fix_start_id,c_fix_end_id,c_dif,c_fix_comment from t_vcs_model";
  	   insSql = "INSERT INTO t_vcs_model (c_id,c_name,c_entity_id,c_type,c_action,c_old_value,c_user_id,c_old_user_id,c_rimport_id,c_rexport_id,c_ip,"
  	   		+ "c_mod_start_time,c_mod_last_time,c_mod_confirm_time,c_fix_start_id,c_fix_end_id,c_dif,c_fix_comment) VALUES"
  	   		+ " (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  	   countRows=0;
  	   try(
  			   PreparedStatement pstSel=extConn.prepareStatement(selSql);
  			   PreparedStatement pstIns=conn.prepareStatement(insSql);
  			   ResultSet rs=pstSel.executeQuery()
  		) {
  		   while(rs.next()) {
  			   pstIns.setLong(1, rs.getLong(1));
  			   pstIns.setString(2, rs.getString(2));
  			   pstIns.setString(3, rs.getString(3));
  			   pstIns.setLong(4, rs.getLong(4));
  			   pstIns.setLong(5, rs.getLong(5));
  			   
  			   InputStream tmpIs = rs.getBinaryStream(6);
  			   if (tmpIs != null)
				   pstIns.setBinaryStream(6, tmpIs);
			   else
				   pstIns.setNull(6, Types.BINARY);
  			   
  			   long tmpL = rs.getLong(7);
			   if (tmpL > 0)
				   pstIns.setLong(7, tmpL);
			   else
				   pstIns.setNull(7, Types.BIGINT);

  			   tmpL = rs.getLong(8);
			   if (tmpL > 0)
				   pstIns.setLong(8, tmpL);
			   else
				   pstIns.setNull(8, Types.BIGINT);

  			   tmpL = rs.getLong(9);
			   if (tmpL > 0)
				   pstIns.setLong(9, tmpL);
			   else
				   pstIns.setNull(9, Types.BIGINT);

  			   tmpL = rs.getLong(10);
			   if (tmpL > 0)
				   pstIns.setLong(10, tmpL);
			   else
				   pstIns.setNull(10, Types.BIGINT);

  			   pstIns.setString(11, rs.getString(11));
  			   pstIns.setTimestamp(12, rs.getTimestamp(12));
  			   pstIns.setTimestamp(13, rs.getTimestamp(13));
  			   pstIns.setTimestamp(14, rs.getTimestamp(14));

  			   tmpL = rs.getLong(15);
			   if (tmpL > 0)
				   pstIns.setLong(15, tmpL);
			   else
				   pstIns.setNull(15, Types.BIGINT);
  			   tmpL = rs.getLong(16);
			   if (tmpL > 0)
				   pstIns.setLong(16, tmpL);
			   else
				   pstIns.setNull(16, Types.BIGINT);
			   
			   tmpIs = rs.getBinaryStream(17);
  			   if (tmpIs != null)
				   pstIns.setBinaryStream(17, tmpIs);
			   else
				   pstIns.setNull(17, Types.BINARY);
  			   
  			   pstIns.setString(18, rs.getString(18));

  			   pstIns.addBatch();
  			   countRows++;
  			   if (countRows%1000 == 0)
  				 pstIns.executeBatch();
  		   }
  		   pstIns.executeBatch();
  	   } catch(Exception e) {
  		   log.error("Ошибка при загрузке таблицы t_vcs_model", e);
  	   } finally {
  		   conn.commit();
  		   log.info("В таблицу t_vcs_model загружено записей:"+countRows);
  	   }

  	   //Загрузка данных в таблицу t_vcs_objects
  	   selSql = "SELECT c_id,c_name,c_obj_id,c_obj_uid,c_obj_class_id,c_attr_id,c_lang_id,c_old_value,c_user_id,c_old_user_id,c_rimport_id,c_rexport_id,c_ip,"
  	   		+ "c_mod_start_time,c_mod_last_time,c_mod_confirm_time,c_fix_start_id,c_fix_end_id,c_dif,c_fix_comment from t_vcs_objects";
  	   insSql = "INSERT INTO t_vcs_objects (c_id,c_name,c_obj_id,c_obj_uid,c_obj_class_id,c_attr_id,c_lang_id,c_old_value,c_user_id,c_old_user_id,c_rimport_id,c_rexport_id,c_ip,"
  	   		+ "c_mod_start_time,c_mod_last_time,c_mod_confirm_time,c_fix_start_id,c_fix_end_id,c_dif,c_fix_comment) VALUES"
  	   		+ " (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  	   countRows=0;
  	   try(
  			   PreparedStatement pstSel=extConn.prepareStatement(selSql);
  			   PreparedStatement pstIns=conn.prepareStatement(insSql);
  			   ResultSet rs=pstSel.executeQuery()
  		) {
  		   while(rs.next()) {
  			   pstIns.setLong(1, rs.getLong(1));
  			   pstIns.setString(2, rs.getString(2));
  			   pstIns.setLong(3, rs.getLong(3));
  			   pstIns.setString(4, rs.getString(4));
  			   pstIns.setLong(5, rs.getLong(5));
  			   pstIns.setLong(6, rs.getLong(6));
  			   pstIns.setLong(7, rs.getLong(7));
  			   
  			   InputStream tmpIs = rs.getBinaryStream(8);
  			   if (tmpIs != null)
				   pstIns.setBinaryStream(8, tmpIs);
			   else
				   pstIns.setNull(8, Types.BINARY);
  			   
  			   long tmpL = rs.getLong(9);
			   if (tmpL > 0)
				   pstIns.setLong(9, tmpL);
			   else
				   pstIns.setNull(9, Types.BIGINT);

  			   tmpL = rs.getLong(10);
			   if (tmpL > 0)
				   pstIns.setLong(10, tmpL);
			   else
				   pstIns.setNull(10, Types.BIGINT);

  			   tmpL = rs.getLong(11);
			   if (tmpL > 0)
				   pstIns.setLong(11, tmpL);
			   else
				   pstIns.setNull(11, Types.BIGINT);

  			   tmpL = rs.getLong(12);
			   if (tmpL > 0)
				   pstIns.setLong(12, tmpL);
			   else
				   pstIns.setNull(12, Types.BIGINT);

  			   pstIns.setString(13, rs.getString(13));
  			   pstIns.setTimestamp(14, rs.getTimestamp(14));
  			   pstIns.setTimestamp(15, rs.getTimestamp(15));
  			   pstIns.setTimestamp(16, rs.getTimestamp(16));

  			   tmpL = rs.getLong(17);
			   if (tmpL > 0)
				   pstIns.setLong(17, tmpL);
			   else
				   pstIns.setNull(17, Types.BIGINT);
  			   tmpL = rs.getLong(18);
			   if (tmpL > 0)
				   pstIns.setLong(18, tmpL);
			   else
				   pstIns.setNull(18, Types.BIGINT);
			   
			   tmpIs = rs.getBinaryStream(19);
  			   if (tmpIs != null)
				   pstIns.setBinaryStream(19, tmpIs);
			   else
				   pstIns.setNull(19, Types.BINARY);
  			   
  			   pstIns.setString(20, rs.getString(20));

  			   pstIns.addBatch();
  			   countRows++;
  			   
  			   if (countRows%1000 == 0)
  				 pstIns.executeBatch();
  		   }
  		   pstIns.executeBatch();
  	   } catch(Exception e) {
  		   log.error("Ошибка при загрузке таблицы t_vcs_objects", e);
  	   } finally {
  		   conn.commit();
  		   log.info("В таблицу t_vcs_objects загружено записей:"+countRows);
  	   }

  	   //Устанавливаем значение шага инсталляции
  	   //siep=2
  	 setStepOfLoad(2);
   }
   
   
    public void loadDataFromExtDb(int step) throws DriverException {
    	// Отключаем проверки FK
    	drv.setForeignKeysEnabled(false);
    	if(step>=2) {
            	db.reloadCache(drv);
    	}
        KrnClass cls = db.getClassByName("Объект");
        List<KrnClass> classes = new ArrayList<KrnClass>();
        classes.add(cls);
        db.getSubClasses(cls.id, true, classes);
        int countCls=classes.size();
    	if(step<=2) {
	        for (int i = 0; i < countCls; i++) {
	            cls = (KrnClass) classes.get(i);
	            if (cls.id < 99 || cls.isVirtual())
	                continue;
	            try {
	        		log.info("Loading " + cls.id + " " + cls.name+" "+i+"/"+countCls);
	                drv.loadClassTableFromExtDb(cls, extConn);
		 	      	   //Устанавливаем значение шага инсталляции для класса в true
		 	      	   //
	                drv.commit();
	            } catch (Exception e) {
	            	log.error(e, e);
	            	throw new DriverException(e.getMessage());
	            } 
	        }
	      	   //Устанавливаем значение шага инсталляции
	      	   //siep=3
	      	 setStepOfLoad(step=3);
    	}
    	if(step<=3) {
	        for (int i = 0; i < countCls; i++) {
	            cls = (KrnClass) classes.get(i);
	            if (cls.id < 99)
	                continue;
	
	            List<KrnAttribute> attrs = db.getAttributesByClassId(cls.id, false);
	            for(int a = 0; a < attrs.size(); a++) {
	                KrnAttribute attr = attrs.get(a);
	                System.out.println("  attr.id = " + attr.id +" "+i+"/"+countCls);
	                if (attr.collectionType > 0 && attr.rAttrId == 0) {
	                    try {
	                        drv.loadAttributeTableFromExtDb(attr, extConn);
	     	 	      	   //Устанавливаем значение шага инсталляции для атрибута в true
	     	 	      	   //
	                        drv.commit();
	                    } catch (Exception e) {
	                    	log.error(e, e);
	                    	throw new DriverException(e.getMessage());
	                    }
	                }
	            }
	        }
	      	   //Устанавливаем значение шага инсталляции
	      	   //siep=1
	      	 setStepOfLoad(1);
    	}
    	// Включаем проверки FK
    	drv.setForeignKeysEnabled(true);
    }
    private boolean setStepOfLoad(int step){
   	   int countRows=0; 
 	   String updSql="UPDATE t_ids SET c_last_id = ? WHERE c_name ='installed'";
 	   try(PreparedStatement pstUpd=conn.prepareStatement(updSql);){
 		   		pstUpd.setLong(1, step);
			   countRows=pstUpd.executeUpdate();
	 		   conn.commit();
 	   }catch(Exception e) {
 		   log.error("Ошибка при установке значения installed=2 в таблицу t_ids:"+e.getMessage());
 	   }finally {
 		   log.info("В таблицу t_ids установлено значения installed="+step);
 	   }
    	return countRows==1;
    }
    private boolean setStepClassOfLoad(long clsId){
    	   int countRows=0; 
  	   String updSql="UPDATEO t_ids SET c_last_id=2 WHERE c_name ='installed'";
  	   try(PreparedStatement pstUpd=conn.prepareStatement(updSql);){
 			   countRows=pstUpd.executeUpdate();
 	 		   conn.commit();
  	   }catch(Exception e) {
  		   log.error("Ошибка при установке значения installed=2 в таблицу t_ids:"+e.getMessage());
  	   }finally {
  		   log.info("В таблицу t_ids установлено значения installed=2");
  	   }
     	return countRows==1;
     }
    private boolean setStepAttrOfLoad(long attrId){
 	   int countRows=0; 
	   String updSql="UPDATEO t_ids SET c_last_id=2 WHERE c_name ='installed'";
	   try(PreparedStatement pstUpd=conn.prepareStatement(updSql);){
			   countRows=pstUpd.executeUpdate();
	 		   conn.commit();
	   }catch(Exception e) {
		   log.error("Ошибка при установке значения installed=2 в таблицу t_ids:"+e.getMessage());
	   }finally {
		   log.info("В таблицу t_ids установлено значения installed=2");
	   }
  	return countRows==1;
  }
    private void fixReplExportProperties(List<KrnObject> exports) throws DriverException {
        //get last export
        KrnObject export = (KrnObject) exports.get(0);
        Iterator<KrnObject> it = exports.iterator();
        while (it.hasNext()) {
            KrnObject exp = it.next();
            if (exp.id > export.id)
                export = exp;
        }

        String idColumn = drv.IDColumnName();
        try {
            //узнаем кол-во изменений данных объектов
            long changeCount = ((Long) drv.getValue(
                export.id,
                db.getAttributeByName(db.getClassByName("Export").id, "change_count").id,
                0, 0, 0)).longValue();
            //сохраняем change_id,
            //зная что в новой БД id будут начинатся с нуля,
            //change_id будет равно кол-ву изменений
            drv.setValue(
                export.id,
                db.getAttributeByName(db.getClassByName("Export").id, "change_id").id,
                0, 0, 0, changeCount, true);

            class RSH implements ResultSetHandler<Long> {
                long changeCount;
                public Long handle(ResultSet rs) throws SQLException {
                    long i = 0;
                    while (rs.next()) {
                        if (changeCount == i++)
                            return new Long(rs.getLong(1));
                    }
                    return i;
                }
            }
            //узнаем кол-во изменений метаданных
            changeCount = ((Long) drv.getValue(
                export.id,
                db.getAttributeByName(db.getClassByName("Export").id, "clschange_count").id,
                0, 0, 0)).longValue();
            RSH h = new RSH();
            h.changeCount = changeCount;
            //вычисляем id изменения по следующем принципу:
            //кол-во изменений метаданных, зафиксированные в экспортном объекте,
            //в старой и новой БД должны быть одинаковыми,
            //но так как в новой все id будут другими, то перебирая таблицу с первой записи
            //узнаем id той записи, на которой приходится порядковый номер changeCount
            long changeId = new QueryRunner().query(
                conn,
                "SELECT " + idColumn + " FROM t_changescls", h
                ).longValue();
            drv.setValue(
                export.id,
                db.getAttributeByName(db.getClassByName("Export").id, "clschange_id").id,
                0, 0, 0, changeId, true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
	private String[] split(String str, String separator) {
		List<String> res = new ArrayList<String>(100);

		int pos = 0;
		for (int i = 0; (i = str.indexOf(separator, pos)) != -1; res.add(str.substring(pos, i)), pos = i + separator.length());
		return res.toArray(new String[res.size()]);
	}
}

