package kz.tamur.ods.sql92;

import static kz.tamur.or3.util.Tname.*;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import com.cifs.or2.kernel.KrnClass;

import kz.tamur.util.Funcs;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 07.12.2005
 * Time: 12:20:32
 * To change this template use File | Settings | File Templates.
 */
public class ClassResultSetHandler implements ResultSetHandler<List<KrnClass>> {

    public List<KrnClass> handle(ResultSet rs) throws SQLException {
        List<KrnClass> res = new ArrayList<KrnClass>();
        while (rs.next()) {
            long id = rs.getInt("c_id");
            String uid = Funcs.sanitizeSQL(rs.getString("c_cuid"));
            String name = Funcs.sanitizeSQL(rs.getString("c_name"));
            long parentId = rs.getLong("c_parent_id");
            boolean isRepl = rs.getBoolean("c_is_repl");
            int mod = rs.getInt("c_mod");
            String tname = null;
            if (isVersion(TnameVersionBD)) {
            	tname = Funcs.sanitizeSQL(rs.getString("c_tname"));
            }
            byte[] beforeCreateObjExpr = isVersion(OrlangTrigersVersionBD2) ? rs.getBytes("c_before_create_obj") : null;
            byte[] afterCreateObjExpr = isVersion(OrlangTrigersVersionBD2) ? rs.getBytes("c_after_create_obj") : null;
            byte[] beforeDeleteObjExpr = isVersion(OrlangTrigersVersionBD2) ? rs.getBytes("c_before_delete_obj") : null;
            byte[] afterDeleteObjExpr = isVersion(OrlangTrigersVersionBD2) ? rs.getBytes("c_after_delete_obj") : null;
            int beforeCreateObjTr = isVersion(OrlangTrigersVersionBD3) ? rs.getInt("c_before_create_obj_tr") : 0;
            int afterCreateObjTr = isVersion(OrlangTrigersVersionBD3) ? rs.getInt("c_after_create_obj_tr") : 0;
            int beforeDeleteObjTr = isVersion(OrlangTrigersVersionBD3) ? rs.getInt("c_before_delete_obj_tr") : 0;
            int afterDeleteObjTr = isVersion(OrlangTrigersVersionBD3) ? rs.getInt("c_after_delete_obj_tr") : 0;

            res.add(new KrnClass(uid, id, parentId, isRepl, mod, name, tname, beforeCreateObjExpr, afterCreateObjExpr, beforeDeleteObjExpr, afterDeleteObjExpr, beforeCreateObjTr, afterCreateObjTr, beforeDeleteObjTr, afterDeleteObjTr));//TODO tname
        }
        return res;
    }
}
