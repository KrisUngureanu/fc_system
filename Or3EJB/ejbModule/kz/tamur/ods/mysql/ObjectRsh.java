package kz.tamur.ods.mysql;

import org.apache.commons.dbutils.ResultSetHandler;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import com.cifs.or2.kernel.KrnObject;

import kz.tamur.util.Funcs;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 20.01.2006
 * Time: 15:48:10
 * To change this template use File | Settings | File Templates.
 */
public class ObjectRsh implements ResultSetHandler {

    private List<KrnObject> res;
    private int[] limit;

    public ObjectRsh() {
    	this.limit = new int[1];
    }
    
    public ObjectRsh(int[] limit) {
    	this.limit = limit;
    }

    public ObjectRsh(List<KrnObject> res) {
        this.res = res;
    }

    public Object handle(ResultSet rs) throws SQLException {
        if (res == null) {
            res = new ArrayList<KrnObject>();
        }
        int count = 0;
        while(rs.next()) {
        	if (limit[0] == 0 || count++ < limit[0]) {
	            res.add(new KrnObject(
	                    rs.getLong("c_obj_id"),
	                    Funcs.sanitizeSQL(rs.getString("c_uid")),
	                    rs.getLong("c_class_id")));
        	} else {
        		// TODO Позже будем возвращать реальное кол-во объектов
        		limit[0]++;
        		break;
        	}
        }
        return res;
    }
}