package kz.tamur.ods.sql92;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;

import com.cifs.or2.kernel.KrnIndex;

import kz.tamur.util.Funcs;

public class IndexResultSetHandler implements ResultSetHandler{
	public Object handle(ResultSet rs) throws SQLException{
		List<KrnIndex> ret = new ArrayList<KrnIndex>();
		while(rs.next()){
			long id = rs.getLong("c_id");
			String uid = Funcs.sanitizeSQL(rs.getString("c_uid"));
			long classId = rs.getLong("c_class_id");
			ret.add(new KrnIndex(id,classId,uid));
		}
		return ret;
	}
}
