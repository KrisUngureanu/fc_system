package kz.tamur.ods;

import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.db.Database;
import com.cifs.or2.server.sgds.HexStringOutputStream;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.*;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.DriverException;
import kz.tamur.comps.Constants;
import kz.tamur.ods.sql92.LongResultSetHandler;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;
import kz.tamur.util.ThreadLocalDateFormat;

/**
 * Created by IntelliJ IDEA.
 * User: daulet
 * Date: 03.06.2006
 * Time: 10:05:44
 * To change this template use File | Settings | File Templates.
 */
public class Getter {
    
	protected static final ThreadLocalDateFormat FILE_DATE_FMT = new ThreadLocalDateFormat("yyyy-MM-dd");
	protected static final ThreadLocalDateFormat FILE_TIME_FMT = new ThreadLocalDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + Getter.class.getName());
	
	Database db;
	Driver drv;
    Connection conn;
    String dir;
    List<KrnObject> langs;
    
    private final String EMPTY_TEXT = "<!EMPTY!>";
    
    public Getter(Driver drv, String dir) {
        this.drv = drv;
        this.db = drv.getDatabase();
        conn = drv.getConnection();
        this.dir = dir;
        if (dir.length() == 0)
            this.dir = "dbexport";
    }
    
    public void getChanges(String separator) throws DriverException {
    	langs = drv.getSystemLangs();

    	// Зачем это?
/*    	List<KrnObject> exps = drv.getObjects(db.getClassByName("Export").id, 0);
        if (exps.size() > 0) {
            fixReplExportProperties(exps);
        }
*/        
        File dir = Funcs.getCanonicalFile(this.dir);
        dir.mkdir();
        
        /*File commonFile = new File(dir, "check.zip");
        commonFile.delete();
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(new FileOutputStream(commonFile));
		} catch (FileNotFoundException e) {
			log.error(e, e);
		}*/

        getChanges(new SimpleTableUnloader(separator, "t_msg"));
        //addToArchive(zos, new File(dir, "t_msg"));

        getChanges(new ClassUnloader(separator));
        //addToArchive(zos, new File(dir, "t_classes"));
        getChanges(new AttributeUnloader(separator));
        //addToArchive(zos, new File(dir, "t_attrs"));
        getChanges(new MethodUnloader(separator));
        //addToArchive(zos, new File(dir, "t_methods"));
        getChanges(new SimpleTableUnloader(separator, "t_rattrs"));
        //addToArchive(zos, new File(dir, "t_rattrs"));
        getChanges(new SimpleTableUnloader(separator, "t_changescls"));
        //addToArchive(zos, new File(dir, "t_changescls"));
        getChanges(new SimpleTableUnloader(separator, "t_changes"));
        //addToArchive(zos, new File(dir, "t_changes"));
        // getChanges(new SimpleTableUnloader(separator, "t_clinks")); 			// Сама заполняется при создании классов
        
        getChanges(new IdsUnloader(separator));
        //addToArchive(zos, new File(dir, "t_ids"));
        getChanges(new CT99DataUnloader(separator));
        //addToArchive(zos, new File(dir, "ct99"));
        List<KrnClass> classes = new ArrayList<KrnClass>();
        db.getSubClasses(db.getClassByName("Объект").id, true, classes);
        for (int i = 0; i < classes.size(); i++) {
            KrnClass cls = (KrnClass) classes.get(i);
            if (cls.id <= 99 || cls.isVirtual())
                continue;
            Unloader unld = new CTDataUnloader(separator, cls);
            getChanges(unld);
            //addToArchive(zos, new File(dir, unld.getFileName()));

            List<KrnAttribute> attrs = db.getAttributesByClassId(cls.id, false);
            for (int a = 0; a < attrs.size(); a++) {
                KrnAttribute attr = (KrnAttribute) attrs.get(a);
                if (attr.rAttrId == 0 && attr.collectionType > 0) {
                    unld = new ATDataUnloader(separator, attr);
                    getChanges(unld);
                	//addToArchive(zos, new File(dir, unld.getFileName()));
                }
            }
        }
        
        // getChanges(new SimpleTableUnloader(separator, "t_indexes"));			// Пустая таблица, не используется
        // getChanges(new SimpleTableUnloader(separator, "t_indexkeys"));		// Пустая таблица, не используется
        getChanges(new SimpleTableUnloader(separator, "t_locks"));
        //addToArchive(zos, new File(dir, "t_locks"));
        getChanges(new SimpleTableUnloader(separator, "t_lock_methods"));
        //addToArchive(zos, new File(dir, "t_lock_methods"));
        getChanges(new SimpleTableUnloader(separator, "t_vcs_model"));		// По идее для реальной они не важны
        getChanges(new SimpleTableUnloader(separator, "t_vcs_objects"));		// По идее для реальной они не важны
        getChanges(new TSyslogUnloader(separator));
        //addToArchive(zos, new File(dir, "t_syslog"));
        
/*        try {
			zos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/    }
    
/*    private void addToArchive(ZipOutputStream zos, File f) {
    	try {
			ZipFile z = new ZipFile(f);
			InputStream fis = z.getInputStream(z.getEntry(f.getName()));
			zos.putNextEntry(new ZipEntry(f.getName()));
			
			byte[] buf = new byte[100];
	        for (int n = 0; (n = fis.read(buf)) > 0; zos.write(buf, 0, n));
	        fis.close();
	        z.close();
			zos.closeEntry();
    	} catch (Exception e) {
    		log.error(e, e);
    	}
    }
*/    
    private void fixReplExportProperties(List<KrnObject> exports) throws DriverException {
        KrnObject export = exports.get(0);
        Iterator<KrnObject> it = exports.iterator();
        while (it.hasNext()) {
            KrnObject exp = (KrnObject) it.next();
            if (exp.id > export.id)
                export = exp;
        }
        try {
            String idColumn = drv.IDColumnName();
            long lastId = ((Long) drv.getValue(
                export.id,
                db.getAttributeByName(db.getClassByName("Export").id, "clschange_id").id,
                0, 0, 0)).longValue();
            long count = new QueryRunner().query(
                conn,
                "SELECT COUNT(*) FROM t_changescls WHERE " + idColumn + "<=" +lastId,
                new LongResultSetHandler()).longValue();
            drv.setValue(
                export.id,
                db.getAttributeByName(db.getClassByName("Export").id, "clschange_count").id,
                0, 0, 0, count, true);

            lastId = ((Long) drv.getValue(
                export.id,
                db.getAttributeByName(db.getClassByName("Export").id, "change_id").id,
                0, 0, 0)).longValue();
            count = new QueryRunner().query(
                conn,
                "SELECT COUNT(*) FROM t_changes WHERE " + idColumn + "<=" +lastId,
                new LongResultSetHandler()).longValue();
            drv.setValue(
                export.id,
                db.getAttributeByName(db.getClassByName("Export").id, "change_count").id,
                0, 0, 0, count, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        drv.commit();
    }
    
    void getChanges(Unloader unldr) throws DriverException {
    	String exceptTables = System.getProperty("dbExportExceptTables");
		if (exceptTables != null && exceptTables.indexOf(unldr.getFileName()) > -1) {
			System.out.println(unldr.getFileName() + " table skipped.");
			return;
		}

        File file = Funcs.getCanonicalFile(new File(dir, unldr.getFileName()));
        file.delete();
        
        PrintWriter pw = null;
        try {
        	ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));
        	zos.putNextEntry(new ZipEntry(unldr.getFileName()));
            pw = new PrintWriter(
                new OutputStreamWriter(
                    zos, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("Обработка файла: " + unldr.getFileName());
        unldr.unload(pw);
        pw.close();
        log.info("Конец орбаботки файла: " + unldr.getFileName());
    }
   
    abstract class Unloader {
    
    	abstract protected String getFileName();
        protected String separator;
        abstract void unload(PrintWriter pw) throws DriverException;
        
        void makeClassList(KrnClass cls, List<KrnClass> list) throws DriverException {
            list.add(cls);
            List<KrnClass> classes = new ArrayList<KrnClass>();
            db.getSubClasses(cls.id, false, classes);
            for (int i = 0; i < classes.size(); i++) {
                KrnClass c = (KrnClass) classes.get(i);
                makeClassList(c, list);
            }
        }
        
        String getClassTableName(long clsId) {
            return kz.tamur.or3.util.Tname.getClassTableName(clsId, conn);
        }
        
        String getClassTableName(KrnClass cls) {
            return kz.tamur.or3.util.Tname.getClassTableName(cls);
        }

        String getAttrTableName(KrnAttribute attr) {
            return kz.tamur.or3.util.Tname.getAttrTableName(attr);
        }
        
    	protected String getColumnName(KrnAttribute attr, int langIndex) {
    		if (attr.isMultilingual) {
    			return kz.tamur.or3.util.Tname.getColumnName(attr) + "_" + langIndex;
    		}
    		return kz.tamur.or3.util.Tname.getColumnName(attr);
    	}
    }
    
    class ClassUnloader extends Unloader {
        
    	protected String getFileName() { return "t_classes"; }
        
        public ClassUnloader(String separator) {
            this.separator = separator;
        }
        
        void unload(PrintWriter pw) throws DriverException {
            List<KrnClass> classes = new ArrayList<KrnClass>();
            makeClassList(db.getClassByName("Объект"), classes);
            pw.println(
            		"c_id" + separator +
            		"c_name" + separator +
                    "c_parent_id" + separator +
                    "c_is_repl" + separator +
                    "c_mod" + separator +
                    "c_cuid" + separator +
                    "c_comment" + separator +
            		"c_tname" + separator +
                    "c_before_create_obj" + separator +
                    "c_after_create_obj" + separator +
                    "c_before_delete_obj" + separator +
                    "c_after_delete_obj" + separator +
                    "c_before_create_obj_tr" + separator +
                    "c_after_create_obj_tr" + separator +
                    "c_before_delete_obj_tr" + separator +
                    "c_after_delete_obj_tr" + separator
            );
            
            int i = 0, size = classes.size();
            log.info("Всего OR3 классов: " + size);
            
            for (KrnClass cls : classes) {
            	String comment = drv.getClassComment(cls.id);
            	String hexComment = "";
            	if (comment != null)
            		try {
            			hexComment = HexStringOutputStream.toHexString(
            					comment.getBytes("UTF-8"));
            		} catch (UnsupportedEncodingException e) {
            			e.printStackTrace();
            		}
            	
                pw.println(
                    "" + cls.id + separator +
                    cls.name + separator +
                    cls.parentId + separator +
                    (cls.isRepl ? "1" : "0")  + separator +
                    cls.modifier + separator +
                    cls.uid + separator +
                    hexComment + separator +
                    ((cls.tname != null) ? cls.tname : "") + separator +
                    HexStringOutputStream.toHexStringNullable(cls.beforeCreateObjExpr) + separator +
                    HexStringOutputStream.toHexStringNullable(cls.afterCreateObjExpr) + separator +
                    HexStringOutputStream.toHexStringNullable(cls.beforeDeleteObjExpr) + separator +
                    HexStringOutputStream.toHexStringNullable(cls.afterDeleteObjExpr) + separator +
                    cls.beforeCreateObjTr + separator +
                    cls.afterCreateObjTr + separator +
                    cls.beforeDeleteObjTr + separator +
                    cls.afterDeleteObjTr + separator
                );

                if (i++ % 100 == 0)
                	log.info("Проверено классов: " + i + "/" + size);
            }
            log.info("Проверено классов: " + i + "/" + size);
        }
    }
    
    class AttributeUnloader extends Unloader {
    	private int i = 0;
    	private int size = 0;
        
    	protected String getFileName() { return "t_attrs"; }
        
    	void unload(PrintWriter pw) throws DriverException {
            List<KrnClass> classes = new ArrayList<KrnClass>();
            makeClassList(db.getClassByName("Объект"), classes);
            pw.println(
            		"c_id" + separator +
            		"c_class_id" + separator +
            		"c_name" + separator +
            		"c_type_id" + separator +
            		"c_col_type" + separator +
            		"c_is_unique" + separator +
            		"c_is_indexed" + separator +
            		"c_is_multilingual" + separator +
            		"c_is_repl" + separator +
            		"c_size" + separator +
            		"c_flags" + separator +
            		"c_rattr_id" + separator +
            		"c_sattr_id" + separator +
            		"c_sdesc" + separator +
            		"c_auid" + separator +
            		"c_comment" + separator +
            		"c_tname" + separator +
                    "c_before_event_expr" + separator +
                    "c_after_event_expr" + separator +
                    "c_before_del_event_expr" + separator +
                    "c_after_del_event_expr" + separator +
                    "c_before_event_tr" + separator +
                    "c_after_event_tr" + separator +
                    "c_before_del_event_tr" + separator +
                    "c_after_del_event_tr" + separator +
                    "c_access_modifier" + separator
            );

            // Сначала выгружаем прямые атрибуты, а потом обратные
            // для избежания нарушения FK

            KrnAttribute at = db.getAttributeByName(101, "lang?");
            if (at == null) {
            	PreparedStatement istnane = null;
            	ResultSet rs = null;
            	try {
	            	String sql = "select c_class_id from t_attrs where c_name='lang?'";
	            	istnane = conn.prepareStatement(sql);
					rs = istnane.executeQuery();
					if(rs.next()) {
						long cls_id = rs.getLong(1);
						at = db.getAttributeByName(cls_id, "lang?");//TODO tname
					}
            	} catch (SQLException e1) {
					//log.error(e1.getMessage());
				} finally {
					DbUtils.closeQuietly(rs);
					DbUtils.closeQuietly(istnane);
				}
            }
            unloadAttribute(pw, at);
            
            for (KrnClass cls : classes) {
                for (KrnAttribute attr : db.getAttributesByClassId(cls.id, false)) {
                	if (attr.rAttrId == 0 && !attr.name.equals("lang?")) {
                		unloadAttribute(pw, attr);
                	}
                }
            }
            
            Map<Long, KrnAttribute> rattrs = new HashMap<Long, KrnAttribute>();//для rAttr ссылающихся rAttr
            
            for (KrnClass cls : classes) {
                for (KrnAttribute attr : db.getAttributesByClassId(cls.id, false)) {
                	if (attr.rAttrId > 0) {
                		PreparedStatement st = null;
        				ResultSet rs = null;
                    	try {
                			String sql = "SELECT * FROM t_attrs WHERE c_id=? and (c_rattr_id<>0 or c_rattr_id is not null)";
                			st = conn.prepareStatement(sql);
                			st.setLong(1, attr.rAttrId);
                			rs = st.executeQuery();
                			if(!rs.next()) {
                				unloadAttribute(pw, attr);
                			} else {
                				rattrs.put(attr.id, attr);
                			}
                		} catch (Exception e1) {
                			e1.printStackTrace();
                		} finally {
        					DbUtils.closeQuietly(rs);
                			DbUtils.closeQuietly(st);
                		}
                	}
                }
            }
            
            //правильный порядок обратных арибутов
            //TODO: not optimized
            Iterator<Map.Entry<Long, KrnAttribute>> iter = rattrs.entrySet().iterator();
            while (iter.hasNext()) {
            	Map.Entry<Long, KrnAttribute> ent = iter.next();
            	if (rattrs.get(ent.getValue().rAttrId) == null) {
            		unloadAttribute(pw, ent.getValue());
            		rattrs.remove(ent.getKey());
            		iter = rattrs.entrySet().iterator();
            	}
            }
            
            
            //debug
            for(Map.Entry<Long, KrnAttribute> ent : rattrs.entrySet()){
            	log.info("no unload attr: id=" + ent.getValue());
            }
            
        	log.info("Проверено атрибутов: " + i + "/" + size);
        }
    	
    	private void unloadAttribute(PrintWriter pw, KrnAttribute attr)
    	throws DriverException {
        	String comment = drv.getAttributeComment(attr.id);
        	String hexComment = "";
        	if (comment != null)
        		try {
        			hexComment = HexStringOutputStream.toHexString(
        					comment.getBytes("UTF-8"));
        		} catch (UnsupportedEncodingException e) {
        			e.printStackTrace();
        		}
            pw.println(
                attr.id + separator +
                attr.classId + separator +
                attr.name + separator +
                attr.typeClassId + separator +
                attr.collectionType + separator +
                (attr.isUnique ? "1" : "0") + separator +
                (attr.isIndexed ? "1" : "0") + separator +
                (attr.isMultilingual ? "1" : "0") + separator +
                (attr.isRepl ? "1" : "0") + separator +
                attr.size + separator +
                attr.flags + separator +
                attr.rAttrId + separator +
                attr.sAttrId + separator +
                (attr.sDesc ? "1" : "0") + separator +
                attr.uid + separator +
                hexComment + separator +
                ((attr.tname != null) ? attr.tname : "") + separator +
                HexStringOutputStream.toHexStringNullable(attr.beforeEventExpr) + separator +
                HexStringOutputStream.toHexStringNullable(attr.afterEventExpr) + separator +
                HexStringOutputStream.toHexStringNullable(attr.beforeDelEventExpr) + separator +
                HexStringOutputStream.toHexStringNullable(attr.afterDelEventExpr) + separator +
                attr.beforeEventTr + separator +
                attr.afterEventTr + separator +
                attr.beforeDelEventTr + separator +
                attr.afterDelEventTr + separator +
                attr.accessModifierType + separator
            );
            
            if (i++%500 == 0)
            	log.info("Проверено атрибутов: " + i + "/" + size);
    	}
    	
        public AttributeUnloader(String separator) {
            this.separator = separator;
            
    		Statement st = null;
			ResultSet rs = null;
        	try {
    			String sql = "SELECT COUNT(*) FROM t_attrs";
    			st = conn.createStatement();
    			rs = st.executeQuery(sql);
    			if (rs.next()) {
    				this.size = rs.getInt(1);
    	            log.info("Всего OR3 атрибутов: " + this.size);
    			}
    		} catch (Exception e) {
    			log.error(e, e);
    		} finally {
				DbUtils.closeQuietly(rs);
    			DbUtils.closeQuietly(st);
    		}
        }
    }
    
    class MethodUnloader extends Unloader {
    	private int i = 0;
    	private int size = 0;
       
    	protected String getFileName() { return "t_methods"; }
        
    	void unload(PrintWriter pw) throws DriverException {
            List<KrnClass> classes = new ArrayList<KrnClass>();
            makeClassList(db.getClassByName("Объект"), classes);
            pw.println(
            		"c_muid" + separator +
            		"c_class_id" + separator +
            		"c_name" + separator +
            		"c_is_cmethod" + separator +
            		"c_expr" + separator +
            		"c_comment" + separator +
            		"developer" + separator
            );
            for (KrnClass cls : classes) {
                for (KrnMethod method : db.getMethodsByClassId(cls.id)) {
                    byte[] b = drv.getMethodExpression(method.uid);
                    String expr = (b != null) ?
                    		HexStringOutputStream.toHexString(b, 0, b.length)
                    		: "";
                    String comment = drv.getMethodComment(method.uid);
                    String hexComment = "";
                    if (comment != null)
                    	try {
                    		hexComment = HexStringOutputStream.toHexString(
                    				comment.getBytes("UTF-8"));
                    	} catch (UnsupportedEncodingException e) {
                    		e.printStackTrace();
                    	}
                    pw.println(
                        method.uid + separator +
                        method.classId + separator +
                        method.name + separator +
                        (method.isClassMethod ? "1" : "0") + separator +
                        expr + separator +
                        hexComment + separator +
                        (method.ownerId > 0 ? method.ownerId : "") + separator
                    );
                    if (i++%500 == 0)
                    	log.info("Проверено методов: " + i + "/" + size);
                }
            }
        	log.info("Проверено методов: " + i + "/" + size);
        }
    	
        public MethodUnloader(String separator) {
            this.separator = separator;
    		Statement st = null;
			ResultSet rs = null;
        	try {
    			String sql = "SELECT COUNT(*) FROM t_methods";
    			st = conn.createStatement();
    			rs = st.executeQuery(sql);
    			if (rs.next()) {
    				this.size = rs.getInt(1);
    	            log.info("Всего OR3 методов: " + this.size);
    			}
    		} catch (Exception e) {
    			log.error(e, e);
    		} finally {
				DbUtils.closeQuietly(rs);
    			DbUtils.closeQuietly(st);
    		}
        }
    }
    
    class IdsUnloader extends Unloader {
        
    	private String tabName;
        
    	protected String getFileName() { return tabName; }
        
    	void unload(PrintWriter pw) throws DriverException {
            pw.println(
            		"c_name" + separator +
            		"c_last_id" + separator
            );
            pw.println(
            		"dbase_id" + separator +
                    drv.getId("dbase_id") + separator
            );
            pw.println(
            		"transaction_id" + separator +
                    drv.getId("transaction_id") + separator
            );
            pw.println(
            		"version" + separator +
                    drv.getId("version") + separator
            );
            pw.println(
            		"installed" + separator +
                    drv.getId("installed") + separator
            );
            pw.println(
            		"mode" + separator +
                    drv.getId("mode") + separator
            );
        }
        
    	public IdsUnloader(String separator) {
            this.separator = separator;
            this.tabName = "t_ids";
        }
    }
    
    class TSyslogUnloader extends Unloader {
    	private int i = 0;
    	private int size = 0;
       
    	protected String getFileName() { return "t_syslog"; }
        
    	void unload(PrintWriter pw) throws DriverException {
            try {
                new QueryRunner().query(conn,
                    "SELECT c_time,c_logger,c_type,c_action,c_user,c_ip,c_host,c_admin,c_message,c_server_id,c_object,c_process FROM t_syslog", new RSH(pw));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
    	private class RSH implements ResultSetHandler<Object> {
        
    		PrintWriter pw;
            
    		public RSH(PrintWriter pw) {
                this.pw = pw;
    		}

    		public Object handle(ResultSet rs) throws SQLException {
                StringBuilder sb = new StringBuilder();
                ResultSetMetaData md = rs.getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    String cn = md.getColumnName(i).toLowerCase(Constants.OK);
                    sb.append(cn + separator);
                }
                pw.println(sb.toString());
                while (rs.next()) {
                    Timestamp c_time = rs.getTimestamp("c_time");
                    if (!rs.wasNull()) {
                    	pw.print(Driver2.FILE_TIME_FMT.format(new Date(((Timestamp)c_time).getTime())));
                    }
                    pw.print(separator);
                    
                    String v = rs.getString("c_logger");
                    if (v == null) v = EMPTY_TEXT;
                	pw.print(v + separator);
                    
                    v = rs.getString("c_type");
                    if (v == null) v = EMPTY_TEXT;
                	pw.print(v + separator);
                    v = rs.getString("c_action");
                    if (v == null) v = EMPTY_TEXT;
                	pw.print(v + separator);
                    v = rs.getString("c_user");
                    if (v == null) v = EMPTY_TEXT;
                	pw.print(v + separator);
                    v = rs.getString("c_ip");
                    if (v == null) v = EMPTY_TEXT;
                	pw.print(v + separator);
                    v = rs.getString("c_host");
                    if (v == null) v = EMPTY_TEXT;
                	pw.print(v + separator);
                    v = rs.getString("c_admin");
                    if (v == null) v = EMPTY_TEXT;
                	pw.print(v + separator);
                    v = rs.getString("c_message");
                    if (v == null) v = EMPTY_TEXT;
                    else v = HexStringOutputStream.toHexStringNullable(v.getBytes());
                	pw.print(v + separator);
                    v = rs.getString("c_server_id");
                    if (v == null) v = EMPTY_TEXT;
                	pw.print(v + separator);
                    v = rs.getString("c_object");
                    if (v == null) v = EMPTY_TEXT;
                	pw.print(v + separator);
                    v = rs.getString("c_process");
                    if (v == null) v = EMPTY_TEXT;
                	pw.print(v + separator);
                    
                    pw.println();
                    if (i++%500 == 0)
                    	log.info("Проверено объектов t_syslog: " + i + "/" + size);
                }
            	log.info("Проверено объектов t_syslog: " + i + "/" + size);
                return null;
            }
        }
    
    	public TSyslogUnloader(String separator) {
            this.separator = separator;

    		Statement st = null;
			ResultSet rs = null;
        	try {
    			String sql = "SELECT COUNT(*) FROM t_syslog";
    			st = conn.createStatement();
    			rs = st.executeQuery(sql);
    			if (rs.next()) {
    				this.size = rs.getInt(1);
    	            log.info("Всего объектов t_syslog: " + this.size);
    			}
    		} catch (Exception e) {
    			log.error(e, e);
    		} finally {
				DbUtils.closeQuietly(rs);
    			DbUtils.closeQuietly(st);
    		}
    	}
    }

    class SimpleTableUnloader extends Unloader {
       
    	private String tabName;
    	private int i = 0;
    	private int size = 0;
        
    	protected String getFileName() { return tabName; }
        
    	void unload(PrintWriter pw) throws DriverException {
            try {
                new QueryRunner().query(conn,
                    "SELECT * FROM " + tabName, new RSH(pw));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
    	private class RSH implements ResultSetHandler<Object> {
        
    		PrintWriter pw;
            
    		public RSH(PrintWriter pw) {
                this.pw = pw;
    		}

    		public Object handle(ResultSet rs) throws SQLException {
                StringBuilder sb = new StringBuilder();
                ResultSetMetaData md = rs.getMetaData();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    String cn = md.getColumnName(i).toLowerCase(Constants.OK);
                    sb.append(cn + separator);
                }
                pw.println(sb.toString());
                while (rs.next()) {
                    StringBuffer val = new StringBuffer();
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        String cn = md.getColumnName(i).toLowerCase(Constants.OK);
                    	int type = md.getColumnType(i);
                        Object o = rs.getObject(i);
                        String v = "";
                        
                        if (!rs.wasNull()) {
	                        if (type == Types.TIMESTAMP) {
	                        	v = Driver2.FILE_TIME_FMT.format(new Date(rs.getTimestamp(i).getTime()));
	                        } else if (type == Types.DATE) {
	                            v = Driver2.FILE_DATE_FMT.format(rs.getDate(i));
	                        } else if (o instanceof byte[]) {
	                        	v = HexStringOutputStream.toHexString((byte[])o);
	                        } else if ("c_fix_comment".equals(cn)) {
	                        	try {
									v = HexStringOutputStream.toHexString(((String)o).getBytes("UTF-8"));
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}
	                        } else {
	                            v = rs.getString(i);
	                        }
	                    } else if (type == Types.CHAR || type == Types.VARCHAR || type == Types.LONGVARCHAR
	                    		|| type == Types.BINARY || type == Types.VARBINARY || type == Types.LONGVARBINARY
	                    		|| type == Types.NCHAR || type == Types.NVARCHAR || type == Types.LONGNVARCHAR
	                    		|| type == Types.BLOB || type == Types.CLOB || type == Types.NCLOB) {
	                    	v = EMPTY_TEXT;
	                    }
                        val.append(v + separator);
                    }
                    pw.println(val);
                    if (i++%500 == 0)
                    	log.info("Проверено объектов " + tabName + ": " + i + "/" + size);
                }
            	log.info("Проверено объектов " + tabName + ": " + i + "/" + size);
                return null;
            }
        }
    
    	public SimpleTableUnloader(String separator, String tabName) {
            this.separator = separator;
            this.tabName = tabName;

    		Statement st = null;
			ResultSet rs = null;
        	try {
    			String sql = "SELECT COUNT(*) FROM " + tabName;
    			st = conn.createStatement();
    			rs = st.executeQuery(sql);
    			if (rs.next()) {
    				this.size = rs.getInt(1);
    	            log.info("Всего объектов " + tabName + ": " + this.size);
    			}
    		} catch (Exception e) {
    			log.error(e, e);
    		} finally {
				DbUtils.closeQuietly(rs);
    			DbUtils.closeQuietly(st);
    		}
    	}
    }
    
    class DataUnloader extends Unloader {
    	private int i = 0;
    	private int size = 0;

        protected String getFileName() { return fileName_; }

        protected String fileName_;
        
        protected Calendar calendar = Calendar.getInstance();

        void unload(PrintWriter pw) throws DriverException {
        }

        protected void getAttributeValue(
        		ResultSet rs,
        		String cname,
        		long typeId,
        		boolean isMultilingual,
        		PrintWriter pw
        ) throws SQLException {
            if (typeId == 1) { //CID_STRING
                String val = rs.getString(cname);
                if (rs.wasNull())
                	return;
                if (val.length() == 0)
                	pw.print(EMPTY_TEXT);
                try {
                    byte[] bs = val.getBytes("UTF-8");
                    pw.print(HexStringOutputStream.toHexString(
                        bs, 0, bs.length));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (typeId == 5) { //CID_BOOLEAN
            	pw.print(rs.getBoolean(cname) ? "1" : "0");
            } else if (typeId == 2) { //CID_INTEGER
                long val = rs.getLong(cname);
                if (!rs.wasNull())
                	pw.print(val);
            } else if (typeId == 4) { //CID_DATE
                java.sql.Date val = rs.getDate(cname);
                if (!rs.wasNull()) {
                	pw.print(FILE_DATE_FMT.format(val));
                }
            } else if (typeId == 3) { //CID_TIME
                java.sql.Timestamp val = rs.getTimestamp(cname);
                if (!rs.wasNull()) {
                	pw.print(FILE_TIME_FMT.format(val));
                }
            } else if (typeId == 8) { //CID_FLOAT
                double val = rs.getDouble(cname);
                if (!rs.wasNull())
                	pw.print(Double.valueOf(val).toString());
            } else if (typeId == 6) { //CID_MEMO
                String val = drv.getMemo(rs, cname);
                if (val == null)
                	return;
                if (val.length() == 0)
                	pw.print(EMPTY_TEXT);
                try {
                    byte[] bs = val.getBytes("UTF-8");
                    pw.print(HexStringOutputStream.toHexString(
                        bs, 0, bs.length));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else if (typeId == 10) { //CID_BLOB
                byte[] val = null;
                
                try {
                	long length = 0;
                    InputStream is = rs.getBinaryStream(cname);
                    if (is != null) {
                        byte[] buf = new byte[4 * 1024];
                        for(int i = 0; (i = is.read(buf)) > 0; ) {
                    		pw.print(HexStringOutputStream.toHexString(buf, 0, i));
                    		length += i;
                        }
                        is.close();
                        
                    	if (length == 0)
                    		pw.print(EMPTY_TEXT);
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                    throw new SQLException("Faild to read bytes");
                }
            } else {//OBJECT
                String val = rs.getString(cname);
                if (val != null)
                	pw.print(val);
            }
        }
        
        public DataUnloader() {
        }
    }
    
    class CT99DataUnloader extends DataUnloader {
    	private int i = 0;
    	private int size = 0;

    	void unload(PrintWriter pw) throws DriverException{
            try {
                new QueryRunner().query(conn,
                    "SELECT * FROM " + fileName_, new RSH(pw));
            	log.info("Проверено объектов " + fileName_ + ": " + CT99DataUnloader.this.i + "/" + CT99DataUnloader.this.size);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
    	public CT99DataUnloader(String separator) {
            this.separator = separator;
            fileName_ = getClassTableName(99);

    		Statement st = null;
			ResultSet rs = null;
        	try {
    			String sql = "SELECT COUNT(*) FROM " + fileName_;
    			st = conn.createStatement();
    			rs = st.executeQuery(sql);
    			if (rs.next()) {
    				this.size = rs.getInt(1);
    	            log.info("Всего объектов " + fileName_ + ": " + this.size);
    			}
    		} catch (Exception e) {
    			log.error(e, e);
    		} finally {
				DbUtils.closeQuietly(rs);
    			DbUtils.closeQuietly(st);
    		}
    	}
        
    	private class RSH implements ResultSetHandler<Object> {
            
    		PrintWriter pw;

            public RSH(PrintWriter pw) {
                this.pw = pw;
            }
            
            public Object handle(ResultSet rs) throws SQLException {
                pw.println(
                    "c_uid" + separator +
                    "c_obj_id" + separator +
                    "c_class_id" + separator
                );
                while (rs.next()) {
                    pw.println(
                        rs.getString("c_uid") + separator +
                        rs.getLong("c_obj_id") + separator +
                        rs.getLong("c_class_id") + separator
                    );
                    if (i++%500 == 0)
                    	log.info("Проверено объектов " + fileName_ + ": " + CT99DataUnloader.this.i + "/" + CT99DataUnloader.this.size);
                }
                return null;
            }
        }
    }

    class CTDataUnloader extends DataUnloader {
    	private int i = 0;
    	private int size = 0;

    	private KrnClass cls;
        
    	void unload(PrintWriter pw) throws DriverException{
    		String sql = "SELECT * FROM " + fileName_;
            try {
                new QueryRunner().query(conn, sql, new RSH(pw));
            	log.info("Проверено объектов " + fileName_ + ": " + CTDataUnloader.this.i + "/" + CTDataUnloader.this.size);
            } catch (SQLException e) {
    			log.error(sql);
    			log.error(e, e);
            }
        }
        
    	public CTDataUnloader(String separator, KrnClass cls) {
            this.separator = separator;
            this.cls = cls;
            fileName_ = getClassTableName(cls);
    		Statement st = null;
			ResultSet rs = null;
			String sql = "SELECT COUNT(*) FROM " + fileName_;
        	try {
    			st = conn.createStatement();
    			rs = st.executeQuery(sql);
    			if (rs.next()) {
    				this.size = rs.getInt(1);
    	            log.info("Всего объектов " + fileName_ + ": " + this.size);
    			}
    		} catch (Exception e) {
    			log.error(sql);
    			log.error(e, e);
    		} finally {
				DbUtils.closeQuietly(rs);
    			DbUtils.closeQuietly(st);
    		}
        }
        
    	private class RSH implements ResultSetHandler<Object> {
            
    		PrintWriter pw;

            public RSH(PrintWriter pw) {
                this.pw = pw;
            }
            
            public Object handle(ResultSet rs) throws SQLException {
                pw.print(
                    "c_uid" + separator +
                    "c_obj_id" + separator +
                    "c_class_id" + separator +
                    "c_tr_id" + separator +
                    "c_is_del" + separator
                );
                List<KrnAttribute> attrs = null;
                attrs = db.getAttributesByClassId(cls.id, false);
                List<Column> columns = new ArrayList<Column>();
                for (KrnAttribute attr : attrs) {
                    if (attr.id == 1 || attr.id == 2
                        || attr.rAttrId > 0 || attr.collectionType > 0) {
                        continue;
                    } else {
                    	if (attr.isMultilingual) { 
                        	for (int i = 0; i < langs.size(); i++) {
                        		String cname = getColumnName(attr, i + 1);
                        		columns.add(new Column(cname, attr));
                        		pw.print(cname + separator);
                        	}
                    	} else {
                    		String cname = getColumnName(attr, 0);
                    		columns.add(new Column(cname, attr));
                    		pw.print(cname + separator);
                    	}
                    }
                }
                pw.println();
                while (rs.next()) {
                    int isDel = 0;
                    if (rs.getBoolean("c_is_del")) {
                        isDel = 1;
                    }
                    pw.print(
                        rs.getString("c_uid") + separator +
                        rs.getLong("c_obj_id") + separator +
                        rs.getLong("c_class_id") + separator +
                        rs.getLong("c_tr_id") + separator +
                        isDel + separator
                    );
                    for (Column column : columns) {
                            getAttributeValue(
                                    rs,
                                    column.name,
                                    column.attr.typeClassId,
                                    column.attr.isMultilingual,
                                    pw
                            );
                            pw.print(separator);
                    }
                    pw.println();
                    if (i++%500 == 0)
                    	log.info("Проверено объектов " + fileName_ + ": " + CTDataUnloader.this.i + "/" + CTDataUnloader.this.size);
                }
                return null;
            }
        }
    }
    
    class ATDataUnloader extends DataUnloader {
        
    	protected String getFileName() { return fileName_; }
    	private String fileName_;
        private KrnAttribute attr;

        void unload(PrintWriter pw) throws DriverException{
            String sql = "SELECT * FROM " + fileName_;
        	try {
                new QueryRunner().query(conn, sql, new RSH(pw));
            } catch (SQLException e) {
    			log.error(sql);
    			log.error(e, e);
            }
        }
        
        public ATDataUnloader(String separator, KrnAttribute attr) {
            this.separator = separator;
            this.attr = attr;
            fileName_ = getAttrTableName(attr);
        }
        
        private class RSH implements ResultSetHandler<Object> {
            
        	PrintWriter pw;
            
            public RSH(PrintWriter pw) {
                this.pw = pw;
            }
            
            public Object handle(ResultSet rs) throws SQLException {
                pw.print(
                		"c_obj_id" + separator +
                		"c_tr_id" + separator +
                		"c_del" + separator
                );
                if (attr.collectionType == 1) {
                    pw.print("c_index" + separator);
                    pw.print("c_id" + separator);
                }
                List<String> cnames = new ArrayList<String>();
                if (attr.isMultilingual) {
                	for (int i = 0; i < langs.size(); i++) {
                		String cname = getColumnName(attr, i + 1);
                		cnames.add(cname);
                		pw.print(cname + separator);
                	}
                } else {
            		String cname = getColumnName(attr, 0);
            		cnames.add(cname);
                	pw.print(cname + separator);
                }
                pw.println();
                
                while (rs.next()) {
                	pw.print("" + rs.getLong("c_obj_id") + separator);
                	pw.print("" + rs.getLong("c_tr_id") + separator);
                	pw.print("" + rs.getLong("c_del") + separator);
                    if (attr.collectionType == 1) {
                    	pw.print("" + rs.getInt("c_index") + separator);
                    	pw.print("" + rs.getLong("c_id") + separator);
                    }
                    for (String cname : cnames) {
                				getAttributeValue(
                						rs,
                						cname,
                						attr.typeClassId,
                						attr.isMultilingual,
                						pw
                				);
                				pw.print(separator);
                    }
                    pw.println();
                }
                return null;
            }
        }
    }
}

