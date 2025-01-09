package com.cifs.or2.server.db;

import org.apache.commons.dbcp.DataSourceConnectionFactory;

import javax.sql.DataSource;
import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 21.12.2005
 * Time: 16:24:05
 * To change this template use File | Settings | File Templates.
 */
public class MyDataSourceConnectionFactory extends DataSourceConnectionFactory {
    public MyDataSourceConnectionFactory(DataSource dataSource, String url, String user, String passwd) {
        super(dataSource, user, passwd);
        Class cls = dataSource.getClass();

        try {
            Method urlField = cls.getMethod("setURL", new Class[] {String.class});
            urlField.invoke(dataSource, new Object[] {url});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
