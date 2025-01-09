package kz.tamur.ods;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static kz.tamur.or3.util.Tname.*;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 27.02.2006
 * Time: 11:30:28
 * To change this template use File | Settings | File Templates.
 */
public class FKIndexer {
    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:/db2_ul");
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

        List attrIds = new ArrayList();

        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT c_id,c_class_id,c_col_type,c_is_multilingual FROM t_attrs WHERE c_type_id>10");
        while(rs.next()) {
            attrIds.add(new long[] {rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getLong(4)});
        }
        rs.close();

        PreparedStatement pst = conn.prepareStatement(
                "UPDATE t_attrs SET c_is_indexed=1 WHERE c_id=?");
        for (int i = 0; i < attrIds.size(); i++) {
            try{
            long[] attr = (long[]) attrIds.get(i);
            String idxName = "idx" + attr[1] + "_" + attr[0];
            String cmName = kz.tamur.or3.util.Tname.getColumnName(attr[0], conn);
            String tname = (attr[2] == 0 && attr[3] == 0)
                           ? getClassTableName(attr[1], conn) : getAttrTableName(attr[1], attr[0], conn);
            st.executeUpdate("CREATE INDEX " + idxName + " ON " + tname + "(" + cmName + ")");
            pst.setLong(1, attr[0]);
            pst.executeUpdate();
            System.out.println("Processed " + i + " of " + attrIds.size());
            } catch(Exception ex){
                ex.printStackTrace();

        }
        }
        st.close();
        pst.close();
        conn.commit();
        conn.close();
    }
}
