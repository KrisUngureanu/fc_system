package kz.tamur.ods.sql92;

import org.apache.commons.dbutils.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 07.12.2005
 * Time: 12:53:19
 * To change this template use File | Settings | File Templates.
 */
public class LongResultSetHandler implements ResultSetHandler<Long> {
    public Long handle(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            return new Long(resultSet.getLong(1));
        }
        return null;
    }
}
