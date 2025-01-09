package kz.tamur.ods.sql92;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;

import com.cifs.or2.kernel.KrnChangeCls;

import kz.tamur.util.Funcs;

/**
 * Created by IntelliJ IDEA.
 * User: naik
 * Date: 29.04.2011
 * Time: 18:56:00
 * To change this template use File | Settings | File Templates.
 */
public class ChangeClsResultSetHandler implements ResultSetHandler{
	public List<KrnChangeCls> handle(ResultSet rs) throws SQLException{
		List<KrnChangeCls> ret = new ArrayList<KrnChangeCls>();
		while(rs.next()){
			long id = rs.getLong("c_id");
			long type = rs.getLong("c_type");
			int action = rs.getInt("c_action");
			String entityUID = Funcs.sanitizeSQL(rs.getString("c_entity_id"));			
			ret.add(new KrnChangeCls(id, type, action, entityUID));
		}
		return ret;
	}
}
