package kz.tamur.ods.sql92;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;

import com.cifs.or2.kernel.KrnIndexKey;

public class IndexKeyResultSetHandler implements ResultSetHandler{
	public Object handle(ResultSet rs) throws SQLException{
		List<KrnIndexKey> ret = new ArrayList<KrnIndexKey>();
		while(rs.next()){
			long indexId = rs.getLong("c_index_id");
			long attrId = rs.getLong("c_attr_id");
			long keyNo = rs.getLong("c_keyno");
			boolean isDesc = rs.getBoolean("c_is_desc");
			ret.add(new KrnIndexKey(indexId,attrId,keyNo,isDesc));
		}
		return ret;
	}
}
