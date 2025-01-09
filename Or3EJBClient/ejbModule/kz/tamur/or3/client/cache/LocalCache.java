package kz.tamur.or3.client.cache;

import static com.cifs.or2.kernel.ModelChange.ACTION_CREATE;
import static com.cifs.or2.kernel.ModelChange.ACTION_DELETE;
import static com.cifs.or2.kernel.ModelChange.ACTION_MODIFY;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_ATTRIBUTE;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_CLASS;
import static com.cifs.or2.kernel.ModelChange.ENTITY_TYPE_METHOD;
import static kz.tamur.util.CollectionTypes.COLLECTION_ARRAY;
import static kz.tamur.util.CollectionTypes.COLLECTION_NONE;
import static kz.tamur.util.CollectionTypes.COLLECTION_SET;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import kz.tamur.ods.AttrRequest;
import kz.tamur.ods.Value;
import kz.tamur.util.Funcs;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.DataChanges;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.ModelChange;
import com.cifs.or2.kernel.ModelChanges;

import static kz.tamur.or3.util.Tname.*;

public class LocalCache {
	
	private static final int PARAM_DB_VERSION		= 1;
	private static final int PARAM_MODEL_CHANGE_ID	= 2;
	private static final int PARAM_DATA_CHANGE_ID	= 3;
	
	private static final int CUR_DB_VERSION = 4;

	private Kernel krn;
	private Connection conn;
	private String schemaId;
	
	private long[] langIds;
	private Map<Long, Integer> langIdxs = new HashMap<Long, Integer>();
	
	private Set<String> cachedClassNames;
	private Set<Long> cachedClassIds = new HashSet<Long>();
	
	private static Map<String, String> userPD = new HashMap<>();
	
	static {
		userPD.put("client", "123456");
	}
	
    public LocalCache(Kernel krn, String cacheConfPath) throws Exception {
		this.krn = krn;
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(LocalCache.class.getResourceAsStream(cacheConfPath));
		Element root = doc.getRootElement();
		// Системные языки
		List<Element> langsE = root.getChild("Langs").getChildren();
		langIds = new long[langsE.size()];
		for (int i = 0; i < langsE.size(); i++) {
			Element langE = langsE.get(i);
			langIds[i] = Long.parseLong(langE.getAttributeValue("id"));
			langIdxs.put(langIds[i], i + 1);
		}
		// Имена кэшированных классов
		List<Element> clssE = root.getChild("Classes").getChildren();
		cachedClassNames = new HashSet<String>();
		for (int i = 0; i < clssE.size(); i++) {
			Element clsE = clssE.get(i);
			cachedClassNames.add(clsE.getAttributeValue("name"));
		}
	}

	public void init() throws KrnException {
		String dsName = krn.getServerHost() + '_' + krn.getServerPort() + "_" + krn.getBaseName();
		try {
			System.out.println("Инициализация локального кэша.");
			String path = Funcs.getSystemProperty("user.home").replace('\\', '/') + "/.OR3Cache/" + dsName;
			
			if (Funcs.isValid(path)) {
				String url = "jdbc:derby:" + path;
				String user = "client";
				
				conn = DriverManager.getConnection(url + ";create=true", user, userPD.get(user));
				conn.setAutoCommit(false);
		
				schemaId = getSchemaId(user);
				
				if (schemaId != null) {
					
					// Проверяем версию схемы кэша
					int dbVer = (int)getParam(PARAM_DB_VERSION);
					if (CUR_DB_VERSION > dbVer) {
						// Необходимо пересоздать БД
						System.out.println("Текущая версия кэша " + dbVer + ". Обновление до версии " + CUR_DB_VERSION + "...");
						conn.rollback();
						conn.close();
						try {
							DriverManager.getConnection(url + ";shutdown=true", user, userPD.get(user));
						} catch (SQLException e) {
							// NOP
						}
						deleteDirectory(Funcs.getCanonicalFile(path));
						conn = DriverManager.getConnection(url + ";create=true", user, userPD.get(user));
						conn.setAutoCommit(false);
						schemaId = null;
					}
				}
				
				if (schemaId == null) {	
					Statement st = conn.createStatement();
					st.executeUpdate("CREATE SCHEMA AUTHORIZATION " + user);
					schemaId = getSchemaId(user);
					st.executeUpdate(
						"CREATE TABLE t_params (" +
						"c_id DECIMAL(2)," +
						"c_value BIGINT," +
						"PRIMARY KEY (c_id))"
					);
					st.executeUpdate(
						"CREATE TABLE t_classes (" +
						"c_id BIGINT," +
						"c_name VARCHAR(255) NOT NULL," +
						"c_parent_id BIGINT NOT NULL," +
						"c_is_repl DECIMAL(1) NOT NULL," +
						"c_comment VARCHAR(32672)," +
						"c_mod INTEGER NOT NULL," +
						"c_cuid CHAR(36) NOT NULL," +
						"c_change_id BIGINT," +
						"PRIMARY KEY (c_id)," +
						"UNIQUE (c_name)," +
						"UNIQUE (c_cuid))"
					);
					st.executeUpdate(
						"CREATE TABLE t_clinks (" +
						"c_parent_id BIGINT," +
						"c_child_id BIGINT," +
						"PRIMARY KEY (c_parent_id,c_child_id)," +
						"FOREIGN KEY (c_parent_id) REFERENCES t_classes(c_id) ON DELETE CASCADE," +
						"FOREIGN KEY (c_child_id) REFERENCES t_classes(c_id) ON DELETE CASCADE)"
					);
					st.executeUpdate(
						"CREATE INDEX idx_clinks_child ON t_clinks(c_child_id)");
					st.executeUpdate(
						"CREATE TABLE t_attrs (" +
						"c_id BIGINT," +
						"c_class_id BIGINT NOT NULL," +
						"c_name VARCHAR(255) NOT NULL," +
						"c_type_id BIGINT NOT NULL," +
						"c_col_type DECIMAL(1) NOT NULL," +
						"c_is_unique DECIMAL(1) NOT NULL," +
						"c_is_indexed DECIMAL(1) NOT NULL," +
						"c_is_multilingual DECIMAL(1) NOT NULL," +
						"c_is_repl DECIMAL(1) NOT NULL," +
						"c_size INTEGER NOT NULL," +
						"c_flags BIGINT NOT NULL," +
						"c_rattr_id BIGINT DEFAULT NULL," +
						"c_sattr_id BIGINT DEFAULT NULL," +
						"c_sdesc DECIMAL(1) DEFAULT NULL," +
						"c_comment VARCHAR(32672)," +
						"c_auid CHAR(36) NOT NULL," +
						"PRIMARY KEY (c_id)," +
						"UNIQUE (c_class_id,c_name)," +
						"UNIQUE (c_auid)," +
						"FOREIGN KEY (c_class_id) REFERENCES t_classes (c_id) ON DELETE CASCADE," +
						"FOREIGN KEY (c_type_id) REFERENCES t_classes (c_id) ON DELETE CASCADE," +
						"FOREIGN KEY (c_rattr_id) REFERENCES t_attrs (c_id) ON DELETE CASCADE," +
						"FOREIGN KEY (c_sattr_id) REFERENCES t_attrs (c_id) ON DELETE CASCADE)"
					);
					st.executeUpdate(
						"CREATE INDEX idx_attrs_type ON t_attrs(c_type_id)");
					st.executeUpdate(
						"CREATE TABLE t_methods (" +
						"c_class_id BIGINT," +
						"c_name VARCHAR(255)," +
						"c_is_cmethod DECIMAL(1) NOT NULL," +
						"c_expr CLOB," +
						"c_comment VARCHAR(32672)," +
						"c_muid CHAR(36) NOT NULL," +
						"PRIMARY KEY (c_muid)," +
						"UNIQUE (c_class_id,c_name)," +
						"FOREIGN KEY (c_class_id) REFERENCES t_classes(c_id) ON DELETE CASCADE)"
					);
					st.close();
					setParam(PARAM_DB_VERSION, CUR_DB_VERSION);
					conn.commit();
				}
				conn.commit();
				synchronize();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new KrnException(0, e.getMessage());
		}
	}
	
	public boolean isCachedClass(long classId) {
		return cachedClassIds.contains(classId);
	}
	
	private String getSchemaId(String user) throws SQLException {
		String schemaId = null;
		PreparedStatement pst = conn.prepareStatement(
				"SELECT SCHEMAID FROM SYS.SYSSCHEMAS WHERE SCHEMANAME=?");
		pst.setString(1, user.toUpperCase(Locale.ROOT));
		ResultSet rs = pst.executeQuery();
		if (rs.next())
			schemaId = Funcs.sanitizeSQL(rs.getString(1));
		rs.close();
		pst.close();
		return schemaId;
	}
	
	public void release() throws SQLException {
		if (conn != null) {
			conn.rollback();
			conn.close();
		}
	}
	
	public KrnClass getClassById(long id) throws KrnException {
		try {
			KrnClass cls = null;
			PreparedStatement pst = conn.prepareStatement(
					"SELECT * FROM t_classes WHERE c_id=?");
			pst.setLong(1, id);
			ResultSet rs = pst.executeQuery();
			if (rs.next())
				cls = makeClass(rs);
			rs.close();
			pst.close();
			return cls;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new KrnException(0, e.getMessage());
		}
	}
	
	public KrnClass getClassByName(String name) throws KrnException {
		try {
			KrnClass cls = null;
			PreparedStatement pst = conn.prepareStatement(
					"SELECT * FROM t_classes WHERE c_name=?");
			pst.setString(1, name);
			ResultSet rs = pst.executeQuery();
			if (rs.next())
				cls = makeClass(rs);
			rs.close();
			pst.close();
			return cls;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new KrnException(0, e.getMessage());
		}
	}
	
	public List<KrnClass> getSubClasses(long parentId) throws KrnException {
		try {
			List<KrnClass> clss = new ArrayList<KrnClass>();
			PreparedStatement pst = conn.prepareStatement(
					"SELECT * FROM t_classes WHERE c_parent_id=?");
			pst.setLong(1, parentId);
			ResultSet rs = pst.executeQuery();
			while (rs.next())
				clss.add(makeClass(rs));
			rs.close();
			pst.close();
			return clss;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new KrnException(0, e.getMessage());
		}
	}
	
	public List<KrnClass> getAllClasses() throws KrnException {
		try {
			List<KrnClass> clss = new ArrayList<KrnClass>();
			PreparedStatement pst = conn.prepareStatement(
					"SELECT * FROM t_classes");
			ResultSet rs = pst.executeQuery();
			while (rs.next())
				clss.add(makeClass(rs));
			rs.close();
			pst.close();
			return clss;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new KrnException(0, e.getMessage());
		}
	}
	
	public long getClassChangeId(long classId) throws SQLException {
		long changeId = 0;
		PreparedStatement pst = conn.prepareStatement(
				"SELECT c_change_id FROM t_classes WHERE c_id=?");
		pst.setLong(1, classId);
		ResultSet rs = pst.executeQuery();
		if (rs.next())
			changeId = rs.getLong(1);
		rs.close();
		pst.close();
		return changeId;
	}
	
	public void setClassChangeId(long classId, long changeId) throws SQLException {
		PreparedStatement pst = conn.prepareStatement(
				"UPDATE t_classes SET c_change_id=? WHERE c_id=?");
		pst.setLong(1, changeId);
		pst.setLong(2, classId);
		pst.executeUpdate();
		pst.close();
	}
	
	public KrnAttribute getAttributeById(long id) throws KrnException {
		try {
			KrnAttribute attr = null;
			String sql = "SELECT * FROM t_attrs WHERE c_id=?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setLong(1, id);
			ResultSet rs = pst.executeQuery();
			if (rs.next())
				attr = makeAttribute(rs);
			rs.close();
			pst.close();
			return attr;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new KrnException(0, e.getMessage());
		}
	}
	
	public List<KrnAttribute> getAttributeByClassId(long classId, boolean recursive)
			throws KrnException {
		try {
			List<KrnAttribute> attrs = new ArrayList<KrnAttribute>();
			String sql = recursive ?
					"SELECT a.* FROM t_clinks l,t_attrs a" +
					" WHERE c_child_id=? AND c_class_id=c_parent_id"
					: "SELECT * FROM t_attrs WHERE c_class_id=?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setLong(1, classId);
			ResultSet rs = pst.executeQuery();
			while (rs.next())
				attrs.add(makeAttribute(rs));
			rs.close();
			pst.close();
			return attrs;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new KrnException(0, e.getMessage());
		}
	}
	
	public List<KrnAttribute> getRevAttributes(long attrId)
			throws KrnException {
		return Collections.emptyList();
		/*
		try {
			List<KrnAttribute> attrs = new ArrayList<KrnAttribute>();
			String sql = "SELECT * FROM t_attrs WHERE c_rattr_id=?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setLong(1, attrId);
			ResultSet rs = pst.executeQuery();
			while (rs.next())
				attrs.add(makeAttribute(rs));
			rs.close();
			pst.close();
			return attrs;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new KrnException(0, e.getMessage());
		}
		*/
	}
	
	public List<KrnAttribute> getAttributeByTypeId(long typeId, boolean inherited)
			throws KrnException {
		try {
			List<KrnAttribute> attrs = new ArrayList<KrnAttribute>();
			String sql = inherited ?
					"SELECT * FROM t_attrs WHERE c_type_id IN (" +
					"SELECT c_parent_id FROM t_clinks WHERE c_child_id=?)"
					:
					"SELECT * FROM t_attrs WHERE AND c_type_id=?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setLong(1, typeId);
			ResultSet rs = pst.executeQuery();
			while (rs.next())
				attrs.add(makeAttribute(rs));
			rs.close();
			pst.close();
			return attrs;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new KrnException(0, e.getMessage());
		}
	}
	
	public KrnMethod getMethodByUid(String uid) throws KrnException {
		try {
			KrnMethod m = null;
			String sql = "SELECT * FROM t_methods WHERE AND c_muid=?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, uid);
			ResultSet rs = pst.executeQuery();
			if (rs.next())
				m = makeMethod(rs);
			rs.close();
			pst.close();
			return m;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new KrnException(0, e.getMessage());
		}
	}
	
	public List<KrnMethod> getMethodsByClassId(long classId)
			throws KrnException {
		try {
			List<KrnMethod> methods = new ArrayList<KrnMethod>();
			String sql = "SELECT * FROM t_methods WHERE c_class_id=?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setLong(1, classId);
			ResultSet rs = pst.executeQuery();
			while (rs.next())
				methods.add(makeMethod(rs));
			rs.close();
			pst.close();
			return methods;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new KrnException(0, e.getMessage());
		}
	}
	
	public byte[] getMethodExpression(String methodUid) throws KrnException {
		try {
			String res = null;
			String sql = "SELECT c_expr FROM t_methods WHERE c_muid=?";
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, methodUid);
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				StringBuilder sb = new StringBuilder();
				Reader r = rs.getCharacterStream(1);
				char[] cbuf = new char[32768];
				for (int i; (i = r.read(cbuf)) != -1; sb.append(cbuf, 0, i));
				r.close();
				res = sb.toString();
			}
			rs.close();
			pst.close();
			return res != null ? res.getBytes("UTF-8") : new byte[0];
		} catch (Exception e) {
			e.printStackTrace();
			throw new KrnException(0, e.getMessage());
		}
	}
	
	public Object getAttributeValue(long objId, KrnAttribute attr, long langId)
			throws KrnException {
		try {
			int langIndex = attr.isMultilingual ? langIdxs.get(langId) : 0;
			Object res = null;
			
			KrnClass attrCls = krn.getClass(attr.classId);
			PreparedStatement pst = conn.prepareStatement(
					"SELECT " + getColumnName(attr, langIndex) +
					" FROM " + getClassTableName(attrCls) + " WHERE c_obj_id=?");
			pst.setLong(1, objId);
			ResultSet rs = pst.executeQuery();
			if (rs.next())
				res = getValue(rs, 1, attr.typeClassId);
			rs.close();
			pst.close();
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			throw new KrnException(0, e.getMessage());
		}
	}

	// Пока только для обратных атрибутов!!!
    public List<Object> getAttributeValues(long objId, KrnAttribute attr, long langId, Kernel krn)
    		throws KrnException {
		try {
			List<Object> res = new ArrayList<Object>();
			String sql = null;
			if (attr.rAttrId > 0) {
				KrnAttribute rattr = krn.getAttributeById(attr.rAttrId);
				KrnAttribute sattr = attr.sAttrId != 0 ? krn.getAttributeById(attr.sAttrId) : null;
				KrnClass rattrCls = krn.getClass(rattr.classId);
				KrnClass sattrCls = krn.getClass(sattr.classId);
				sql = "SELECT c_obj_id,c_uid,c_class_id" +
						" FROM " + getClassTableName(rattrCls) + " rt";
				if (sattr != null && sattr.classId != rattr.classId)
					sql += " LEFT JOIN " + getClassTableName(sattrCls) + " st ON st.c_obj_id=rt.c_obj_id";
				sql += " WHERE " + getColumnName(rattr, 0) + "=?";
				if (sattr != null)
					sql += " ORDER BY " + getColumnName(sattr, 0);

			} else if (attr.collectionType != COLLECTION_NONE) {
				//String ptname = attr.classId + "_" + attr.id;
				if (attr.typeClassId > 99) {
					KrnClass attrTypeCls = krn.getClass(attr.typeClassId);
					sql = "SELECT vt.c_obj_id,vt.c_uid,vt.c_class_id FROM " + getClassTableName(attrTypeCls) + " vt WHERE vt.c_obj_id IN (SELECT " +
							getColumnName(attr, 0)+ " FROM " + getAttrTableName(attr) + " pt WHERE pt.c_obj_id=?)"; 
				} else {
					int langIndex = attr.isMultilingual ? langIdxs.get(langId) : 0;
					sql = "SELECT " + getColumnName(attr, langIndex) + " FROM " + getAttrTableName(attr) +
							" WHERE c_obj_id=?";
				}
			
			} else {
				KrnClass attrCls = krn.getClass(attr.classId);
				if (attr.typeClassId > 99) {
					KrnClass attrTypeCls = krn.getClass(attr.typeClassId);
					sql = "SELECT vt.c_obj_id,vt.c_uid,vt.c_class_id FROM " + getClassTableName(attrTypeCls) + " vt WHERE vt.c_obj_id=(SELECT " +
							getColumnName(attr, 0)+ " FROM " + getClassTableName(attrCls) + " pt WHERE pt.c_obj_id=?)"; 
				} else {
					int langIndex = attr.isMultilingual ? langIdxs.get(langId) : 0;
					sql = "SELECT " + getColumnName(attr, langIndex) + " FROM " + getClassTableName(attrCls) +
							" WHERE c_obj_id=?";
				}
			}
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setLong(1, objId);
			ResultSet rs = pst.executeQuery();
			while (rs.next())
				res.add(getValue(rs, 1, attr.typeClassId));
			rs.close();
			pst.close();
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			throw new KrnException(0, e.getMessage());
		}
    }
    
    public List<KrnObject> getClassObjects(KrnClass cls) throws KrnException {
		try {
	    	List<KrnObject> res = new ArrayList<KrnObject>();
			PreparedStatement pst = conn.prepareStatement(
					"SELECT * FROM " + getClassTableName(cls));
			ResultSet rs = pst.executeQuery();
			while (rs.next())
				res.add(makeObject(rs));
			rs.close();
			pst.close();
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new KrnException(0, e.getMessage());
		}
    }
    
    public List<Object[]> getObjects(long classId, long[] objIds, AttrRequest req, Kernel krn)
    		throws KrnException {
    	try {
	    	AttrRequest firstReq = req.getChildren().get(0);
	    	KrnAttribute attr = krn.getAttributeById(firstReq.attrId);
			KrnClass attrCls = krn.getClass(attr.classId);

			Map<Long, String> levelTables = new HashMap<Long, String>();
	    	String parentTable = "pt";
	    	levelTables.put(attr.classId, parentTable);
	    	StringBuilder selSql = new StringBuilder("pt.c_obj_id,pt.c_uid,pt.c_class_id");
	    	StringBuilder fromSql = new StringBuilder(getClassTableName(attrCls) + " pt");
	    	List<KrnAttribute> attrs = new ArrayList<KrnAttribute>();
	    	int[] tableIndex = { 1 };
	    	for (AttrRequest chReq : req.getChildren())
	    		processAttrRequest(chReq, selSql, fromSql, parentTable, levelTables, tableIndex, attrs, krn);
	    	List<Object[]> res = new ArrayList<Object[]>();
	    	if (objIds != null) { 
		    	PreparedStatement pst = conn.prepareStatement(
		    			"SELECT " + selSql + " FROM " + fromSql + " WHERE pt.c_obj_id=?");
		    	for (long objId : objIds) {
		    		pst.setLong(1, objId);
		    		ResultSet rs = pst.executeQuery();
		    		if (rs.next()) {
		    			res.add(makeRow(attrs, rs));
		    		}
		    		rs.close();
		    	}
		    	pst.close();
	    	} else {
		    	PreparedStatement pst = conn.prepareStatement(
		    			"SELECT " + selSql + " FROM " + fromSql);
	    		ResultSet rs = pst.executeQuery();
	    		while (rs.next()) {
	    			res.add(makeRow(attrs, rs));
	    		}
	    		rs.close();
		    	pst.close();
	    	}
	    	return res;
		} catch (Exception e) {
			e.printStackTrace();
			throw new KrnException(0, e.getMessage());
		}
    }
    
    private Object[] makeRow(List<KrnAttribute> attrs, ResultSet rs) throws SQLException {
		Object[] row = new Object[2 + attrs.size()];
		row[0] = new KrnObject(rs.getLong(1), rs.getString(2), rs.getLong(3));
		row[1] = 0;
		int i = 4;
		int j = 2;
		for (KrnAttribute a : attrs) {
			if (a.typeClassId >= 99) {
				long id = rs.getLong(i++);
				if (rs.wasNull()) {
					row[j++] = null;
					i += 2;
				} else {
					row[j++] = new KrnObject(id, rs.getString(i++), rs.getLong(i++));
				}
			} else {
    			row[j++] = getValue(rs, i++, a.typeClassId);
			}
		}
		return row;
    }
    
    private void processAttrRequest(
    		AttrRequest req,
    		StringBuilder selSql,
    		StringBuilder fromSql,
    		String parentTable,
    		Map<Long,String> levelTables,
    		int[] tableIndex,
    		List<KrnAttribute> attrs,
    		Kernel krn)
    				throws KrnException {
    	KrnAttribute attr = krn.getAttributeById(req.attrId);
		KrnClass attrCls = krn.getClass(attr.classId);
    	attrs.add(attr);
    	String lt = levelTables.get(attr.classId);
    	if (lt == null) {
    		lt = "t" + (tableIndex[0]++);
    		levelTables.put(attr.classId, lt);
    		fromSql.append(" LEFT JOIN " + getClassTableName(attrCls) + " " + lt +
    				" ON " + lt + ".c_obj_id=" + parentTable + ".c_obj_id");
    	}
    	List<AttrRequest> children = req.getChildren();
    	if (children.size() > 0) {
    		KrnAttribute chAttr = krn.getAttributeById(children.get(0).attrId);
			KrnClass chattrCls = krn.getClass(chAttr.classId);
    		String vt = "t" + tableIndex[0]++;
    		selSql.append(
    				"," + vt + ".c_obj_id" +
    				"," + vt + ".c_uid" +
    				"," + vt + ".c_class_id");
    		fromSql.append(" LEFT JOIN " + getClassTableName(chattrCls) + " " + vt +
    				" ON " + vt + ".c_obj_id=" + lt + "." + getColumnName(attr, 0));
    		Map<Long, String> chLevelTables = new HashMap<Long, String>();
    		chLevelTables.put(attr.classId, vt);
    		for (AttrRequest chReq : children)
    			processAttrRequest(chReq, selSql, fromSql, vt, chLevelTables, tableIndex, attrs, krn);
    	} else if (attr.typeClassId >= 99) {
    		String vt = "t" + tableIndex[0]++;
    		selSql.append(
    				"," + vt + ".c_obj_id" +
    				"," + vt + ".c_uid" +
    				"," + vt + ".c_class_id");
    		fromSql.append(" LEFT JOIN ct99 " + vt +
    				" ON " + vt + ".c_obj_id=" + lt + "." + getColumnName(attr, 0));
    	} else {
    		int langIndex = attr.isMultilingual ? langIdxs.get(req.langId) : 0;
    		selSql.append("," + lt + "." + getColumnName(attr, langIndex));
    	}
    }
    
	private KrnClass makeClass(ResultSet rs) throws SQLException {//TODO tname
		return new KrnClass(
				Funcs.sanitizeSQL(rs.getString("c_cuid")),
				rs.getLong("c_id"),
				rs.getLong("c_parent_id"),
				rs.getBoolean("c_is_repl"),
				rs.getInt("c_mod"),
				Funcs.sanitizeSQL(rs.getString("c_name")),
				null,
				isVersion(OrlangTrigersVersionBD2) ? rs.getBytes("c_before_create_obj") : null,
				isVersion(OrlangTrigersVersionBD2) ? rs.getBytes("c_after_create_obj") : null,
				isVersion(OrlangTrigersVersionBD2) ? rs.getBytes("c_before_delete_obj") : null,
				isVersion(OrlangTrigersVersionBD2) ? rs.getBytes("c_after_delete_obj") : null,
		        isVersion(OrlangTrigersVersionBD3) ? rs.getInt("c_before_create_obj_tr") : 0,
		        isVersion(OrlangTrigersVersionBD3) ? rs.getInt("c_after_create_obj_tr") : 0,
		        isVersion(OrlangTrigersVersionBD3) ? rs.getInt("c_before_delete_obj_tr") : 0,
		        isVersion(OrlangTrigersVersionBD3) ? rs.getInt("c_after_delete_obj_tr") : 0);
	}
	
	private KrnAttribute makeAttribute(ResultSet rs) throws SQLException {
		return new KrnAttribute(
				Funcs.sanitizeSQL(rs.getString("c_auid")),
				rs.getLong("c_id"),
				Funcs.sanitizeSQL(rs.getString("c_name")),
				rs.getLong("c_class_id"),
				rs.getLong("c_type_id"),
				rs.getInt("c_col_type"),
				rs.getBoolean("c_is_unique"),
				rs.getBoolean("c_is_multilingual"),
				rs.getBoolean("c_is_indexed"),
				rs.getInt("c_size"),
				rs.getLong("c_flags"),
				rs.getBoolean("c_is_repl"),
				rs.getLong("c_rattr_id"),
				rs.getLong("c_sattr_id"),
				rs.getBoolean("c_sdesc"),
				isVersion(TnameVersionBD) ? Funcs.sanitizeSQL(rs.getString("c_tname")) : null,
				isVersion(OrlangTrigersVersionBD1) ? rs.getBytes("c_before_event_expr") : null,
				isVersion(OrlangTrigersVersionBD1) ? rs.getBytes("c_after_event_expr") : null,
				isVersion(OrlangTrigersVersionBD2) ? rs.getBytes("c_before_delete_event_expr") : null,
				isVersion(OrlangTrigersVersionBD2) ? rs.getBytes("c_after_delete_event_expr") : null,
		        isVersion(OrlangTrigersVersionBD3) ? rs.getInt("c_before_event_tr") : 0,
		        isVersion(OrlangTrigersVersionBD3) ? rs.getInt("c_after_event_tr") : 0,
		        isVersion(OrlangTrigersVersionBD3) ? rs.getInt("c_before_delete_event_tr") : 0,
		        isVersion(OrlangTrigersVersionBD3) ? rs.getInt("c_after_delete_event_tr") : 0,
				isVersion(AttrAccessModVersionBD) ? rs.getInt("c_access_modifier") : 0);
	}
	
	private KrnMethod makeMethod(ResultSet rs) throws SQLException {
		String className="";
		try {
			KrnClass classM = getClassById(rs.getLong("c_class_id"));
			className=classM.name;
		} catch (KrnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new KrnMethod(
				rs.getString("c_muid"),
				rs.getString("c_name"),
				rs.getLong("c_class_id"),
				className,
				rs.getBoolean("c_is_cmethod"),
				rs.getLong("developer")
				);
	}
	
	private KrnObject makeObject(ResultSet rs) throws SQLException {
		return new KrnObject(
				rs.getLong("c_obj_id"),
				rs.getString("c_uid"),
				rs.getLong("c_class_id"));
	}
	
	private long getParam(int id) throws SQLException {
		long value = 0;
		PreparedStatement pst = conn.prepareStatement(
				"SELECT c_value FROM t_params WHERE c_id=?");
		pst.setInt(1, id);
		ResultSet rs = pst.executeQuery();
		if (rs.next())
			value = rs.getLong(1);
		rs.close();
		pst.close();
		return value;
	}
	
	private void setParam(int id, long value) throws SQLException {
		PreparedStatement pst = conn.prepareStatement(
				"UPDATE t_params SET c_value=? WHERE c_id=?");
		pst.setLong(1, value);
		pst.setInt(2, id);
		int res = pst.executeUpdate();
		pst.close();
		if (res == 0) {
			pst = conn.prepareStatement(
					"INSERT INTO t_params(c_id,c_value) VALUES (?,?)");
			pst.setInt(1, id);
			pst.setLong(2, value);
			pst.executeUpdate();
		}
	}
	
	private void synchronize() throws KrnException, UnsupportedEncodingException, IOException, SQLException {
		System.out.println("Синхронизация локального кэша...");
		System.out.println("Синхронизация модели...");
		long changeId = getParam(PARAM_MODEL_CHANGE_ID);
		ModelChanges mcs = krn.getModelChanges(changeId);

		Set<Long> changedClasses = new HashSet<Long>();
		if (mcs.changes.size() > 0) {
			PreparedStatement ccPst = prepareCreateClass();
			PreparedStatement caPst = prepareCreateAttribute();
			PreparedStatement cmPst = prepareCreateMethod();
			for (ModelChange mc : mcs.changes) {
				
				if (ACTION_CREATE == mc.changeType) {
					if (ENTITY_TYPE_CLASS == mc.entityType)
						createClass((KrnClass)mc.entity, mc.entityComment, ccPst);
					else if (ENTITY_TYPE_ATTRIBUTE == mc.entityType) {
						KrnAttribute attr = (KrnAttribute)mc.entity;
						createAttribute(attr, mc.entityComment, caPst);
						changedClasses.add(attr.classId);
					} else if (ENTITY_TYPE_METHOD == mc.entityType) {
						KrnMethod m = (KrnMethod)mc.entity;
						createMethod(m, mc.entityComment, mc.entityData, cmPst);
						changedClasses.add(m.classId);
					}

				} else if (ACTION_MODIFY == mc.changeType) {
					if (ENTITY_TYPE_CLASS == mc.entityType)
						updateClass((KrnClass)mc.entity, mc.entityComment);
					else if (ENTITY_TYPE_ATTRIBUTE == mc.entityType)
						updateAttribute((KrnAttribute)mc.entity, mc.entityComment);
					else if (ENTITY_TYPE_METHOD == mc.entityType)
						updateMethod((KrnMethod)mc.entity, mc.entityComment, mc.entityData);
				
				} else if (ACTION_DELETE == mc.changeType) {
					if (ENTITY_TYPE_CLASS == mc.entityType)
						deleteClass((String)mc.entity);
					else if (ENTITY_TYPE_ATTRIBUTE == mc.entityType)
						deleteAttribute((String)mc.entity);
					else if (ENTITY_TYPE_METHOD == mc.entityType)
						deleteMethod((String)mc.entity);
				}
			}
			ccPst.close();
			caPst.close();
			cmPst.close();
			setParam(PARAM_MODEL_CHANGE_ID, mcs.changeId);
		}
		conn.commit();
		System.out.println(mcs.changes.size() + " записей загружено.");
		
		System.out.println("Синхронизация данных...");
		List<KrnClass> clss = getAllClasses();
		for (KrnClass cls : clss) {
			if (cachedClassNames.contains(cls.name)) {
				cachedClassIds.add(cls.id);
				System.out.print(Funcs.sanitizeHtml(cls.name) + "...");
				long lastChangeId = getClassChangeId(cls.id);
				// Запоминаем классы без атрибутов чтобы обнвоить в них только объекты
				Set<KrnClass> emptyClss = new HashSet<KrnClass>();
				DataChanges lastDcs = null;
				KrnClass pcls = cls;
				while (pcls != null) {
					if (!pcls.isVirtual()) {
						if (changedClasses.remove(pcls.id)) {
							removeCachedClass(pcls.id);
							lastChangeId = 0;
						}
						addCachedClass(pcls);
						DataChanges dcs = updateCachedClass(cls, pcls, emptyClss, lastChangeId);
						if (lastDcs == null && dcs != null)
							lastDcs = dcs;
					}
					pcls = pcls.parentId > 0 ? getClassById(pcls.parentId) : null;
				}
				emptyClss.add(getClassById(99));
				if (lastDcs != null) {
					updateEmptyCachedClasses(emptyClss, lastDcs);
					setClassChangeId(cls.id, lastDcs.changeId);
					System.out.println(lastDcs.rows.size() + " записей загружено.");
				} else {
					System.out.println("0 записей загружено.");
				}
				conn.commit();
			}
		}
	}
	
	private Object getValue(ResultSet rs, int cindex, long typeId)
			throws SQLException {
		if (typeId == Kernel.IC_STRING || typeId == Kernel.IC_MEMO) {
			return rs.getString(cindex);
		} else if (typeId == Kernel.IC_BOOL) {
			return rs.getBoolean(cindex);
		} else if (typeId == Kernel.IC_INTEGER) {
			long v = rs.getLong(cindex);
			return rs.wasNull() ? null : v;
		} else if (typeId == Kernel.IC_DATE) {
			Date v = rs.getDate(cindex);
			return kz.tamur.util.Funcs.convertDate(v);
		} else if (typeId == Kernel.IC_TIME) {
			Timestamp v = rs.getTimestamp(cindex);
			return kz.tamur.util.Funcs.convertTime(v);
		} else if (typeId == Kernel.IC_FLOAT) {
			double v = rs.getDouble(cindex);
			return rs.wasNull() ? null : v;
		} else if (typeId == Kernel.IC_BLOB) {
			byte[] v = rs.getBytes(cindex);
			return rs.wasNull() ? null : v;
		} else if (typeId >= 99) {
			KrnObject v = new KrnObject(rs.getLong(cindex), rs.getString(cindex + 1), rs.getLong(cindex + 2));
			return rs.wasNull() ? null : v;
		}
		return null;
	}
	
	private void setValue(PreparedStatement pst, int colIndex, long typeId,
			Object value) throws SQLException {
		if (value == null) {
			pst.setNull(colIndex, getSqlType(typeId));
		} else if (typeId == Kernel.IC_STRING) {
			String res = (String) value;
			pst.setString(colIndex, res != null && res.length() > 255 ? res
					.substring(0, 255) : res);
		} else if (typeId == Kernel.IC_BOOL) {
			if (value instanceof Number) {
				pst.setBoolean(colIndex, ((Number) value).longValue() != 0);
			} else
				pst.setBoolean(colIndex, (Boolean) value);
		} else if (typeId >= 99 || typeId == Kernel.IC_INTEGER) {
			if (value instanceof KrnObject)
				pst.setLong(colIndex, ((KrnObject) value).id);
			else if (value instanceof Number)
				pst.setLong(colIndex, ((Number) value).longValue());
		} else if (typeId == Kernel.IC_DATE) {
			if (value instanceof com.cifs.or2.kernel.Date)
				value = Funcs.convertToSqlDate((com.cifs.or2.kernel.Date)value);
			pst.setDate(colIndex, (Date) value);
		} else if (typeId == Kernel.IC_TIME) {
			if (value instanceof com.cifs.or2.kernel.Time)
				value = Funcs.convertToSqlTime((com.cifs.or2.kernel.Time)value);
			pst.setTimestamp(colIndex, (Timestamp) value);
		} else if (typeId == Kernel.IC_FLOAT) {
			pst.setDouble(colIndex, ((Number) value).doubleValue());
		} else if (typeId == Kernel.IC_MEMO) {
			pst.setString(colIndex, (String) value);
		} else if (typeId == Kernel.IC_BLOB) {
			byte[] buf = (value != null) ? (byte[]) value : new byte[0];
			pst.setBytes(colIndex, buf);
		}
	}
	
	private PreparedStatement prepareCreateClass() throws SQLException {
		return conn.prepareStatement(
				"INSERT INTO t_classes(c_id,c_name,c_parent_id,c_is_repl,c_comment,c_mod,c_cuid) VALUES (?,?,?,?,?,?,?)");
	}
	
	private void createClass(KrnClass cls, String comment, PreparedStatement pst) throws SQLException, KrnException {
		pst.setLong(1, cls.id);
		pst.setString(2, cls.name);
		pst.setLong(3, cls.parentId);
		pst.setBoolean(4, cls.isRepl);
		pst.setString(5, comment);
		pst.setInt(6, cls.modifier);
		pst.setString(7, cls.uid);
		pst.executeUpdate();
		
		if (cls.parentId > 0) {
			pst = conn.prepareStatement(
				"INSERT INTO t_clinks(c_parent_id,c_child_id)" +
				" SELECT c_parent_id,? FROM t_clinks" +
				" WHERE c_child_id=?");
			pst.setLong(1, cls.id);
			pst.setLong(2, cls.parentId);
			pst.executeUpdate();
			pst.close();
		}
		pst = conn.prepareStatement(
			"INSERT INTO t_clinks(c_parent_id,c_child_id) VALUES (?,?)");
		pst.setLong(1, cls.id);
		pst.setLong(2, cls.id);
		pst.executeUpdate();
		pst.close();
	}
	
	private PreparedStatement prepareCreateAttribute() throws SQLException {
		return conn.prepareStatement(
				"INSERT INTO t_attrs(c_id,c_class_id,c_name,c_type_id,c_col_type,c_is_unique,c_is_indexed,c_is_multilingual,c_is_repl,c_size,c_flags,c_rattr_id,c_sattr_id,c_sdesc,c_comment,c_auid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
	}
	
	private void createAttribute(KrnAttribute attr, String comment, PreparedStatement pst) throws SQLException, KrnException {
		pst.setLong(1, attr.id);
		pst.setLong(2, attr.classId);
		pst.setString(3, attr.name);
		pst.setLong(4, attr.typeClassId);
		pst.setInt(5, attr.collectionType);
		pst.setBoolean(6, attr.isUnique);
		pst.setBoolean(7, attr.isIndexed);
		pst.setBoolean(8, attr.isMultilingual);
		pst.setBoolean(9, attr.isRepl);
		pst.setInt(10, attr.size);
		pst.setLong(11, attr.flags);
		if (attr.rAttrId == 0)
			pst.setNull(12, Types.BIGINT);
		else
			pst.setLong(12, attr.rAttrId);
		if (attr.sAttrId == 0)
			pst.setNull(13, Types.BIGINT);
		else
			pst.setLong(13, attr.sAttrId);
		pst.setBoolean(14, attr.sDesc);
		pst.setString(15, comment);
		pst.setString(16, attr.uid);
		pst.executeUpdate();
	}
	
	private PreparedStatement prepareCreateMethod() throws SQLException {
		return conn.prepareStatement(
				"INSERT INTO t_methods(c_class_id,c_name,c_is_cmethod,c_expr,c_comment,c_muid) VALUES (?,?,?,?,?,?)");
	}
	
	private void createMethod(KrnMethod m, String comment, byte[] expr, PreparedStatement pst) throws SQLException, UnsupportedEncodingException, KrnException {
		pst.setLong(1, m.classId);
		pst.setString(2, m.name);
		pst.setBoolean(3, m.isClassMethod);
		pst.setString(4, new String(expr, "UTF-8"));
		pst.setString(5, comment);
		pst.setString(6, m.uid);
		pst.executeUpdate();
	}
	
	private void updateClass(KrnClass cls, String comment) throws SQLException, KrnException {
		PreparedStatement pst = conn.prepareStatement(
			"UPDATE t_classes SET c_name=?,c_parent_id=?,c_is_repl=?,c_comment=?,c_mod=?,c_cuid=? WHERE c_id=?");
		pst.setString(1, cls.name);
		pst.setLong(2, cls.parentId);
		pst.setBoolean(3, cls.isRepl);
		pst.setString(4, comment);
		pst.setInt(5, cls.modifier);
		pst.setString(6, cls.uid);
		pst.setLong(7, cls.id);
		pst.executeUpdate();
		pst.close();
	}
	
	private void updateAttribute(KrnAttribute attr, String comment) throws SQLException, KrnException {
		PreparedStatement pst = conn.prepareStatement(
			"UPDATE t_attrs SET c_class_id=?,c_name=?,c_type_id=?,c_col_type=?,c_is_unique=?,c_is_indexed=?,c_is_multilingual=?,c_is_repl=?,c_size=?,c_flags=?,c_rattr_id=?,c_sattr_id=?,c_sdesc=?,c_comment=?,c_auid=? WHERE c_id=?");
		pst.setLong(1, attr.classId);
		pst.setString(2, attr.name);
		pst.setLong(3, attr.typeClassId);
		pst.setInt(4, attr.collectionType);
		pst.setBoolean(5, attr.isUnique);
		pst.setBoolean(6, attr.isIndexed);
		pst.setBoolean(7, attr.isMultilingual);
		pst.setBoolean(8, attr.isRepl);
		pst.setInt(9, attr.size);
		pst.setLong(10, attr.flags);
		if (attr.rAttrId == 0)
			pst.setNull(11, Types.BIGINT);
		else
			pst.setLong(11, attr.rAttrId);
		if (attr.sAttrId == 0)
			pst.setNull(12, Types.BIGINT);
		else
			pst.setLong(12, attr.sAttrId);
		pst.setBoolean(13, attr.sDesc);
		pst.setString(14, comment);
		pst.setString(15, attr.uid);
		pst.setLong(16, attr.id);
		pst.executeUpdate();
		pst.close();
	}
	
	private void updateMethod(KrnMethod m, String comment, byte[] expr) throws SQLException, UnsupportedEncodingException, KrnException {
		PreparedStatement pst = conn.prepareStatement(
			"UPDATE t_methods SET c_class_id=?,c_name=?,c_is_cmethod=?,c_comment=?,c_expr=? WHERE c_muid=?");
		pst.setLong(1, m.classId);
		pst.setString(2, m.name);
		pst.setBoolean(3, m.isClassMethod);
		pst.setString(4, comment);
		pst.setString(5, new String(expr, "UTF-8"));
		pst.setString(6, m.uid);
		pst.executeUpdate();
		pst.close();
	}
	
	private void deleteClass(String uid) throws SQLException {
		PreparedStatement pst = conn.prepareStatement(
				"DELETE FROM t_classes WHERE c_cuid=?");
		pst.setString(1, uid);
		pst.executeUpdate();
		pst.close();
	}

	private void deleteAttribute(String uid) throws SQLException {
		PreparedStatement pst = conn.prepareStatement(
				"DELETE FROM t_attrs WHERE c_auid=?");
		pst.setString(1, uid);
		pst.executeUpdate();
		pst.close();
	}

	private void deleteMethod(String uid) throws SQLException {
		PreparedStatement pst = conn.prepareStatement(
				"DELETE FROM t_methods WHERE c_muid=?");
		pst.setString(1, uid);
		pst.executeUpdate();
		pst.close();
	}
	
	private void addCachedClass(KrnClass cls) throws SQLException, KrnException {
		String tableName = getClassTableName(cls);
		if (getTableId(tableName) != null)
			return;
		String columnsSql =
				"c_obj_id BIGINT," +
				"c_uid VARCHAR(36)," +
				"c_class_id BIGINT";
		List<KrnAttribute> attrs = getAttributeByClassId(cls.id, false);
		for (KrnAttribute attr : attrs) {
			if (attr.id != 1 && attr.id != 2 && attr.collectionType == 0 && attr.rAttrId == 0) {
				if (attr.isMultilingual) {
					for (int i = 1; i <= langIds.length; i++) {
						columnsSql += "," + getColumnName(attr, i) + " " + getSqlTypeName(attr.typeClassId);
					}
				} else {
					columnsSql += "," + getColumnName(attr, 0) + " " + getSqlTypeName(attr.typeClassId);
				}
			}
		}
		Statement st = conn.createStatement();
		st.executeUpdate(
				"CREATE TABLE " + tableName + "(" +
				columnsSql + "," +
				"PRIMARY KEY (c_obj_id)," +
				"FOREIGN KEY (c_class_id) REFERENCES t_classes(c_id)," +
				"UNIQUE (c_uid))"
		);
		for (KrnAttribute attr : attrs) {
			if (attr.id != 1 && attr.id != 2 && attr.collectionType != COLLECTION_NONE && attr.rAttrId == 0) {
				columnsSql = "c_obj_id BIGINT";
				String pkColumnsSql = "c_obj_id";
				if (attr.collectionType == COLLECTION_ARRAY) {
					columnsSql += ",c_index INTEGER";
					pkColumnsSql += ",c_index";
				}
				if (attr.isMultilingual) {
					for (int i = 1; i <= langIds.length; i++) {
						columnsSql += "," + getColumnName(attr, i) + " " + getSqlTypeName(attr.typeClassId);
					}
					if (attr.collectionType == COLLECTION_SET)
						pkColumnsSql += "," + getColumnName(attr, 1);
				} else {
					String cname = getColumnName(attr, 0);
					columnsSql += "," + cname + " " + getSqlTypeName(attr.typeClassId);
					if (attr.collectionType == COLLECTION_SET)
						pkColumnsSql += "," + cname;
				}
				KrnClass attrCls = krn.getClass(attr.classId);
				st.executeUpdate(
						"CREATE TABLE "+getAttrTableName(attr) + "(" +
						columnsSql + "," +
						"PRIMARY KEY (" + pkColumnsSql + ")," +
						"FOREIGN KEY (c_obj_id) REFERENCES " + getClassTableName(attrCls) + "(c_obj_id))"
				);
			}
		}
	}
	
	private void removeCachedClass(long clsId) throws SQLException, KrnException {
		KrnClass cls = krn.getClass(clsId);

		String tableName = getClassTableName(cls);
		if (getTableId(tableName) == null)
			return;
		List<KrnAttribute> attrs = getAttributeByClassId(clsId, false);
		Statement st = conn.createStatement();
		for (KrnAttribute attr : attrs) {
			if (attr.id != 1 && attr.id != 2 && attr.collectionType != COLLECTION_NONE && attr.rAttrId == 0) {
				st.executeUpdate("DROP TABLE "+getAttrTableName(attr));
			}
		}
		st.executeUpdate("DROP TABLE " + tableName);
		st.close();
	}

	private DataChanges updateCachedClass(KrnClass cls, KrnClass pcls, Set<KrnClass> emptyClss, long classChangeId) throws SQLException, IOException, KrnException {
		String set = "";
		String columns = "c_obj_id,c_uid,c_class_id";
		String values = "?,?,?";
		AttrRequest rootReq = new AttrRequest(null);
		List<KrnAttribute> attrs = getAttributeByClassId(cls.id, true);
		// Сортировка обязательна, т.к. сервер кэширует объекты
		Collections.sort(attrs, new Comparator<KrnAttribute>() {
			public int compare(KrnAttribute a1, KrnAttribute a2) {
				long res = a1.id - a2.id;
				return res > 0 ? 1 : res < 0 ? -1 : 0;
			}
		});
		List<KrnAttribute> reqAttrs = new ArrayList<KrnAttribute>();
		boolean emptyCls = true;
		for (KrnAttribute attr : attrs) {
			if (attr.id != 1 && attr.id != 2 && attr.rAttrId == 0) {
				reqAttrs.add(attr);
				if (attr.isMultilingual) {
					for (int i = 0; i < langIds.length; i++) {
						AttrRequest req = new AttrRequest(rootReq);
						req.attrId = attr.id;
						req.langId = langIds[i];
						if (attr.classId == pcls.id) {
							emptyCls = false;
							if (attr.collectionType == COLLECTION_NONE) {
								set += getColumnName(attr, i + 1) + "=?,";
								columns += "," + getColumnName(attr, i + 1);
								values += ",?";
							}
						}
					}
				} else {
					AttrRequest req = new AttrRequest(rootReq);
					req.attrId = attr.id;
					if (attr.classId == pcls.id) {
						emptyCls = false;
						if (attr.collectionType == COLLECTION_NONE) {
							set += getColumnName(attr, 0) + "=?,";
							columns += "," + getColumnName(attr, 0);
							values += ",?";
						}
					}
				}
			}
		}
		if (emptyCls) {
			emptyClss.add(pcls);
			return null;
		}
		DataChanges dcs = krn.getDataChanges(cls.id, classChangeId, rootReq);
		if (dcs.rows.size() > 0) {
			PreparedStatement upst = conn.prepareStatement(
					"UPDATE " + getClassTableName(pcls) + " SET " + set.substring(0, set.length() - 1) + " WHERE c_obj_id=?");
			PreparedStatement ipst = conn.prepareStatement(
					"INSERT INTO " + getClassTableName(pcls) + " (" + columns + ") VALUES (" + values + ")");
			Map<Long, PreparedStatement> dpsts = new HashMap<Long, PreparedStatement>();
			Map<Long, PreparedStatement> ipsts = new HashMap<Long, PreparedStatement>();
			for (KrnAttribute attr : reqAttrs) {
				if (attr.classId != pcls.id)
					continue;
				if (attr.collectionType != COLLECTION_NONE) {
					String tname = getAttrTableName(attr);
					dpsts.put(attr.id, conn.prepareStatement("DELETE FROM " + tname + " WHERE c_obj_id=?"));
					String atColumns = "c_obj_id";
					String atValues = "?";
					atColumns += "," + getColumnName(attr, 0); // Пока без мультиязычных!!!
					atValues += ",?";
					if (attr.collectionType == COLLECTION_ARRAY) {
						atColumns += ",c_index";
						atValues += ",?";
					}
					ipsts.put(attr.id, conn.prepareStatement(
							"INSERT INTO " + tname + " (" + atColumns + ") VALUES (" + atValues + ")"));
				}
			}
			for(Object[] row : dcs.rows) {
				KrnObject obj = (KrnObject)row[0];
				ipst.setLong(1, obj.id);
				ipst.setString(2, obj.uid);
				ipst.setLong(3, obj.classId);
				int i = 0;
				int k = 0;
				for (KrnAttribute ra : reqAttrs) {
					if (ra.collectionType != COLLECTION_NONE) {
						i++;
						continue;
					}
					if (ra.classId != pcls.id) {
						i += ra.isMultilingual ? langIds.length : 1;
						continue;
					}
					if (ra.isMultilingual) {
						for (int j = 0; j < langIds.length; j++) {
							setValue(upst, k + 1, ra.typeClassId, row[i + 2]);
							setValue(ipst, k + 4, ra.typeClassId, row[i + 2]);
							i++;
							k++;
						}
					} else {
						setValue(upst, k + 1, ra.typeClassId, row[i + 2]);
						setValue(ipst, k + 4, ra.typeClassId, row[i + 2]);
						i++;
						k++;
					}
				}
				upst.setLong(k + 1, obj.id);
				if (upst.executeUpdate() == 0)
					ipst.executeUpdate();
				// Обработка множественных атирбутов
				int offset = 2;
				for (int j = 0; j < reqAttrs.size(); j++) {
					KrnAttribute ra = reqAttrs.get(j);
					if (ra.isMultilingual)
						offset += langIds.length - 1;
					if (ra.classId != pcls.id)
						continue;
					if (ra.collectionType != COLLECTION_NONE) {
						PreparedStatement dpst = dpsts.get(ra.id);
						dpst.setLong(1, obj.id);
						dpst.executeUpdate();
						
						List<Value> cvals = (List<Value>)row[j + offset];
						if (cvals != null) {
							PreparedStatement ipst2 = ipsts.get(ra.id);
							ipst2.setLong(1, obj.id);
							for(Value val : cvals) {
								setValue(ipst2, 2, ra.typeClassId, val.value);
								if (ra.collectionType == COLLECTION_ARRAY) {
									ipst2.setInt(3, val.index);
								}
								ipst2.executeUpdate();
							}
						}
					}
				}
			}
			upst.close();
			ipst.close();
			for (Long key : dpsts.keySet())
				dpsts.get(key).close();
			for (Long key : ipsts.keySet())
				ipsts.get(key).close();
		}
		return dcs;
	}

	private void updateEmptyCachedClasses(Set<KrnClass> emptyClss, DataChanges dcs) throws SQLException {
		for (KrnClass cls : emptyClss) {
			PreparedStatement qpst = conn.prepareStatement(
					"SELECT 1 FROM " + getClassTableName(cls) + " WHERE c_obj_id=?");
			PreparedStatement ipst = conn.prepareStatement(
					"INSERT INTO " + getClassTableName(cls) + " (c_obj_id,c_uid,c_class_id) VALUES (?,?,?)");
			
			for (Object[] row : dcs.rows) {
				KrnObject obj = (KrnObject)row[0];
				qpst.setLong(1, obj.id);
				ResultSet rs = qpst.executeQuery();
				if (!rs.next()) {
					ipst.setLong(1, obj.id);
					ipst.setString(2, obj.uid);
					ipst.setLong(3, obj.classId);
					ipst.executeUpdate();
				}
				rs.close();
			}
			ipst.close();
			qpst.close();
		}
	}

	private String getTableId(String tableName) throws SQLException {
		String tableId = null;
		PreparedStatement pst = conn.prepareStatement(
			"SELECT TABLEID FROM SYS.SYSTABLES WHERE SCHEMAID=? AND TABLENAME=?");
		pst.setString(1, schemaId);
		pst.setString(2, tableName);
		ResultSet rs = pst.executeQuery();
		if (rs.next())
			tableId = rs.getString(1);
		rs.close();
		pst.close();
		return tableId;
	}
	
	private String getColumnName(KrnAttribute attr, int langIndex) {
		return kz.tamur.or3.util.Tname.getColumnName(attr, langIndex);
	}
	
	private String getSqlTypeName(long typeId) {
		if (Kernel.IC_INTEGER == typeId)
			return "BIGINT";
		if (Kernel.IC_STRING == typeId)
			return "VARCHAR(255)";
		if (Kernel.IC_BLOB == typeId)
			return "BLOB";
		if (Kernel.IC_MEMO == typeId)
			return "VARCHAR(32672)";
		if (Kernel.IC_FLOAT == typeId)
			return "DOUBLE";
		if (Kernel.IC_BOOL == typeId)
			return "DECIMAL(1)";
		if (Kernel.IC_DATE == typeId)
			return "DATE";
		if (Kernel.IC_TIME == typeId)
			return "TIMESTAMP";
		return "BIGINT";
	}

	protected int getSqlType(long typeId) {
		if (typeId == Kernel.IC_STRING) {
			return Types.VARCHAR;
		} else if (typeId == Kernel.IC_INTEGER) {
			return Types.BIGINT;
		} else if (typeId == Kernel.IC_DATE) {
			return Types.DATE;
		} else if (typeId == Kernel.IC_TIME) {
			return Types.TIMESTAMP;
		} else if (typeId == Kernel.IC_BOOL) {
			return Types.BIT;
		} else if (typeId == Kernel.IC_FLOAT) {
			return Types.DOUBLE;
		} else if (typeId == Kernel.IC_MEMO) {
			return Types.VARCHAR;
		} else if (typeId == Kernel.IC_BLOB) {
			return Types.BLOB;
		}
		return Types.BIGINT;
	}

	static public boolean deleteDirectory(File path) {
		if (path.exists()) {
			path = Funcs.getCanonicalFile(path);
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	private AttrRequest createAttrRequest(KrnClass cls, KrnClass pcls) throws KrnException {
		AttrRequest rootReq = new AttrRequest(null);
		List<KrnAttribute> attrs = getAttributeByClassId(pcls.id, false);
		List<KrnAttribute> reqAttrs = new ArrayList<KrnAttribute>();
		for (KrnAttribute attr : attrs) {
			if (attr.id != 1 && attr.id != 2 && attr.rAttrId == 0) {
				reqAttrs.add(attr);
				if (attr.isMultilingual) {
					for (int i = 0; i < langIds.length; i++) {
						AttrRequest req = new AttrRequest(rootReq);
						req.attrId = attr.id;
						req.langId = langIds[i];
					}
				} else {
					AttrRequest req = new AttrRequest(rootReq);
					req.attrId = attr.id;
				}
			}
		}
		return rootReq;
	}
	
    public String getClassTableName(KrnClass cls) {
		return "ct" + cls.id;
	}
    
	public String getAttrTableName(KrnAttribute attr) {
		return "at" + attr.classId + "_" + attr.id;
	}
	
	public String getAttrTableName(long clsId, long attrID, Connection conn) {
		return "at" + clsId + "_" + attrID;
	}
}
