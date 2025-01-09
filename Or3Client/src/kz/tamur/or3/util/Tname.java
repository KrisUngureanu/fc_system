package kz.tamur.or3.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;

import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;

public class Tname {
	
	private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + Tname.class.getName());

	public static final long TnameVersionBD = 17;
	public static final long OrlangTrigersVersionBD1 = 28;
	public static final long OrlangTrigersVersionBD2 = 40;
	public static final long OrlangTrigersVersionBD3 = 43;
	public static final long AttrAccessModVersionBD = 59;
	public static final long EncryptColumnsVersionBD = 77;
	
	public static long version = -1;
	public static boolean isServer = false;

	private static final String ATTRS = "t_attrs";
	private static final String CLASSES = "t_classes";

	public static String DBtype = null;
	public static String TableLeftQuotes = "";
	public static String TableRightQuotes = "";
	
	public static void setTableNamePrefixU(StringBuilder sb, String tname){
		sb.append(TableLeftQuotes).append(tname).append(TableRightQuotes);
	}

	public static void setDBtype(String dBtype) {
		DBtype = dBtype;
		if (dBtype.equals("mysql") || dBtype.equals("mysql3")) {
			TableLeftQuotes = "`";
        	TableRightQuotes = "`";
        } else if (dBtype.equals("oracle3")) {
        	TableLeftQuotes = "";				// "" - если использовать кавычки, то чувствителен к регистру
        	TableRightQuotes = "";
        } else if (dBtype.equals("mssql3")) {
        	TableLeftQuotes = "";				// []
        	TableRightQuotes = "";
        }
	}
	
    public static boolean isVersion(long v) {
        return isServer ? (version >= v ? true : false) : true;
    }

	public static String getTname(long clsId, Connection conn, String type) {
		PreparedStatement st = null;
		ResultSet rs = null;
    	try {
			String sql = "SELECT c_tname FROM " + type + " WHERE c_id=?";
			st = conn.prepareStatement(sql);
			st.setLong(1, clsId);
			rs = st.executeQuery();
			if(rs.next()){
				String tname = Funcs.sanitizeSQL(rs.getString(1));
				if (Funcs.isValid(tname)) {
					return tname.toUpperCase(Constants.OK);
				}
			}
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {}
			}
			if (st != null ){
				try {
					st.close();
				} catch (Exception e) {}
			}
		}
    	return null;
    }
	
    public static String getClassTableName(long clsId, Connection conn) {
		if (isVersion(TnameVersionBD)) {
			String tname = getTname(clsId, conn, CLASSES);
			if (tname != null)
				return tname;
		}
		return new StringBuilder("ct").append(clsId).toString();
	}
    
    public static String getClassTableName(KrnClass cls) {
		if (isVersion(TnameVersionBD)) {
			if (cls.tname != null && cls.tname.trim().length() != 0) {
				return Funcs.sanitizeSQL(cls.tname);
			}			
		}
		return new StringBuilder("ct").append(cls.id).toString();
	}
    
	public static String getAttrTableName(KrnAttribute attr) {
		if (isVersion(TnameVersionBD) && attr.tname != null && attr.tname.trim().length() != 0) {
			return Funcs.sanitizeSQL(attr.tname.trim());
		}
		return new StringBuilder("at").append(attr.classId).append("_").append(attr.id).toString();
	}
	
	public static String getAttrTableName(long clsId, long attrID, Connection conn) {
		if (isVersion(TnameVersionBD)) {
			String tname = getTname(attrID, conn, ATTRS);
			if (tname != null) {
				return tname;
			}
		}
		return new StringBuilder("at").append(clsId).append("_").append(attrID).toString();
	}

    public static String getColumnName(KrnAttribute attr) {
		if (isVersion(TnameVersionBD) && attr.tname != null && attr.tname.trim().length() != 0) {
			return Funcs.sanitizeSQL(attr.tname.trim());
		}
		return new StringBuilder("cm").append(attr.id).toString();
	}
    
    public static String getColumnName(long attrID, Connection conn) {
		if (isVersion(TnameVersionBD)) {
			String tname = getTname(attrID, conn, ATTRS);
			if (tname != null) {
				return tname;
			}
		}
		return new StringBuilder("cm").append(attrID).toString();
	}
    
    public static String getColumnName(KrnAttribute attr, int langIndex) {
		if (attr.isMultilingual)
			return getColumnName(attr) + "_" + langIndex;
		else
			return getColumnName(attr);
	}
    
    public static String getAttrFKName(KrnAttribute attr) {
		return getAttrFKName(attr.classId, attr.id);
	}
    
    public static String getAttrFKName(long clsid, long id) {
		return new StringBuilder("at").append(clsid).append("_").append(id).append("_FK").toString();
    }
    
    public static String getExtFKName(KrnAttribute attr) {
		return getAttrFKName(attr.id);
	}
    
    public static String getAttrFKName(long id) {
		return new StringBuilder("FK").append(id).toString();
    }
}
