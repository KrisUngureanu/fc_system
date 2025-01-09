package kz.tamur.ods.sql92;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 08.12.2005
 * Time: 10:57:54
 * To change this template use File | Settings | File Templates.
 */
public class LongListResultSetHandler implements ResultSetHandler {
    public Object handle(ResultSet resultSet) throws SQLException {
        List res = new ArrayList();
        while (resultSet.next()) {
            res.add(new Long(resultSet.getLong(1)));
        }
        return res;
    }
}
