package kz.tamur.ods.sql92;

import static kz.tamur.or3.util.Tname.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kz.tamur.util.Funcs;
import kz.tamur.util.KrnUtil;

import org.apache.commons.dbutils.ResultSetHandler;

import com.cifs.or2.kernel.KrnAttribute;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 07.12.2005
 * Time: 12:19:24
 * To change this template use File | Settings | File Templates.
 */
public class AttributeRsh implements ResultSetHandler<List<KrnAttribute>> {
    public List<KrnAttribute> handle(ResultSet rs) throws SQLException {
        List<KrnAttribute> res = new ArrayList<KrnAttribute>();
        while (rs.next()) {
            long id = rs.getLong("c_id");
            String uid = Funcs.sanitizeSQL(rs.getString("c_auid")).trim();
            long classId = rs.getLong("c_class_id");
            long typeId  = rs.getLong("c_type_id");
            String name = Funcs.sanitizeSQL(rs.getString("c_name"));
            int colType  = rs.getInt("c_col_type");
            boolean isUnique = rs.getBoolean("c_is_unique");
            boolean isIndexed = rs.getBoolean("c_is_indexed");
            boolean isMultilingual = rs.getBoolean("c_is_multilingual");
            boolean isRepl = rs.getBoolean("c_is_repl");
            int size = rs.getInt("c_size");
            long flags = rs.getLong("c_flags");
            long rAttrId = rs.getLong("c_rattr_id");
            long sAttrId = rs.getLong("c_sattr_id");
            boolean sDesc = rs.getBoolean("c_sdesc");
            boolean isEncrypt = isVersion(EncryptColumnsVersionBD) ? rs.getBoolean("c_is_encrypt") : false;
            String tname = null;
            if (kz.tamur.or3.util.Tname.isVersion(kz.tamur.or3.util.Tname.TnameVersionBD)) {
            	tname = Funcs.sanitizeSQL(rs.getString("c_tname"));
            }
            byte[] beforeEventExpression = isVersion(OrlangTrigersVersionBD1) ? rs.getBytes("c_before_event_expr") : null;
            byte[] afterEventExpression = isVersion(OrlangTrigersVersionBD1) ? rs.getBytes("c_after_event_expr") : null;
            byte[] beforeDeleteEventExpression = isVersion(OrlangTrigersVersionBD2) ? rs.getBytes("c_before_del_event_expr") : null;
            byte[] afterDeleteEventExpression = isVersion(OrlangTrigersVersionBD2) ? rs.getBytes("c_after_del_event_expr") : null;
            int beforeEventTr = isVersion(OrlangTrigersVersionBD3) ? rs.getInt("c_before_event_tr") : 0;
            int afterEventTr = isVersion(OrlangTrigersVersionBD3) ? rs.getInt("c_after_event_tr") : 0;
            int beforeDeleteEventTr = isVersion(OrlangTrigersVersionBD3) ? rs.getInt("c_before_del_event_tr") : 0;
            int afterDeleteEventTr = isVersion(OrlangTrigersVersionBD3) ? rs.getInt("c_after_del_event_tr") : 0;
            int accessModifier = isVersion(AttrAccessModVersionBD) ? rs.getInt("c_access_modifier") : 0;

            res.add(KrnUtil.createAttribute(uid, id, name, classId, typeId, colType, isUnique, isMultilingual, isIndexed, size, flags, isRepl, rAttrId,
                    sAttrId, sDesc, tname, beforeEventExpression, afterEventExpression, beforeDeleteEventExpression, afterDeleteEventExpression, beforeEventTr, afterEventTr, beforeDeleteEventTr, afterDeleteEventTr, accessModifier, isEncrypt));
        }
        return res;
    }
}