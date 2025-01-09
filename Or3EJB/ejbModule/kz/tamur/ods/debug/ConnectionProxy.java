package kz.tamur.ods.debug;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.server.orlang.SrvOrLang;

import kz.tamur.or3ee.common.UserSession;

public class ConnectionProxy implements InvocationHandler {
	
	private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ConnectionProxy.class.getName());
	
	private static Method CREATE_STATEMENT;
	private static Method PREPARE_STATEMENT;
	private static Method COMMIT;
	private static Method ROLLBACK;
	private static Method CLOSE;
	private static Method EXECUTE_UPDATE;
	private static Method EXECUTE_UPDATE_STRING;
	
	private static Map<Connection, Item> conItems = Collections.synchronizedMap(new HashMap<Connection, Item>());
	
	static {
		try {
			CREATE_STATEMENT = Connection.class.getMethod("createStatement", new Class[0]);
			PREPARE_STATEMENT = Connection.class.getMethod("prepareStatement", new Class[] {String.class});
			COMMIT = Connection.class.getMethod("commit", new Class[0]);
			ROLLBACK = Connection.class.getMethod("rollback", new Class[0]);
			CLOSE = Connection.class.getMethod("close", new Class[0]);
			EXECUTE_UPDATE = PreparedStatement.class.getMethod("executeUpdate", new Class[0]);
			EXECUTE_UPDATE_STRING = PreparedStatement.class.getMethod("executeUpdate", new Class[] {String.class});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	Object delegate;
	
	public ConnectionProxy(Object delegate) {
		this.delegate = delegate;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object res = null;
		try {
			res = method.invoke(delegate, args);
		} catch (InvocationTargetException ite) {
			if (ROLLBACK.equals(method)) {
				Item item = conItems.remove((Connection)proxy);
				if (item != null)
					log.error("Transaction failed in " + (System.currentTimeMillis() - item.startTime) + " ms.");
			}
			Throwable cause = ite.getCause();
			if (cause != null)
				throw ite.getCause();
		}
		if (CREATE_STATEMENT.equals(method))
			res = Proxy.newProxyInstance(
					ConnectionProxy.class.getClassLoader(),
					new Class[] {Statement.class},
					new ConnectionProxy(res));
		else if (PREPARE_STATEMENT.equals(method))
			res = Proxy.newProxyInstance(
					ConnectionProxy.class.getClassLoader(),
					new Class[] {PreparedStatement.class},
					new ConnectionProxy(res));
		else if (EXECUTE_UPDATE.equals(method) || EXECUTE_UPDATE_STRING.equals(method)) {
			Connection conn = ((Statement)proxy).getConnection();
			if (!conItems.containsKey(conn)) {
				Collection<String> stack = SrvOrLang.getExecutionStack();
				if (stack == null || stack.isEmpty()) {
					StackTraceElement[] stes = Thread.currentThread().getStackTrace();
					stack = new ArrayList<String>(stes.length);
					for (StackTraceElement ste : stes)
						stack.add(ste.toString());
				}
				conItems.put(conn, new Item(System.currentTimeMillis(), stack, Thread.currentThread().getName(),getSessionId(conn)));
				log.debug("Transaction started.");
			}
		
		} else if (COMMIT.equals(method) || ROLLBACK.equals(method) || CLOSE.equals(method)) {
			Item item = conItems.remove((Connection)proxy);
			if (item != null)
				log.debug("Transaction ended in " + (System.currentTimeMillis() - item.startTime) + " ms.");
		}
		
		if (CLOSE.equals(method)) {
			ResourceRegistry.instance().resourceReleased(proxy);
		}
		
		return res;
	}
	
	private String getSessionId(Connection conn){
		Statement st = null;
		ResultSet rs = null;
		String SID="";
		try {
			String productName=conn.getMetaData().getDatabaseProductName();
			String sql="";
			if(productName.contains("MySQL"))
				sql="SELECT CONNECTION_ID()";
			else if(productName.contains("Oracle"))
				sql="select USERENV ('SID') from dual";
			else if(productName.contains("Microsoft SQL Server"))
				sql="SELECT @@SPID AS 'ID', SYSTEM_USER AS 'Login Name', USER AS 'User Name'";
			if(!"".equals(sql)) {
				st = conn.createStatement();
				rs = st.executeQuery(sql);
				if (rs.next()) {
					SID=rs.getString(1);
				}
			}
		} catch (SQLException e) {
			log.error(e, e);
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(st);
		}
		return SID;
	}
	public static List<Item> getItems() {
		List<Item> res = new ArrayList<ConnectionProxy.Item>();
		synchronized (conItems) {
			res.addAll(conItems.values());
		}
		return res;
	}

	public static class Item {
		
		public final long startTime;
		public final Collection<String> executionStack;
		public final String threadName;
		public final String sid;
		
		public Item(long startTime, Collection<String> executionStack, String threadName,String sid) {
			super();
			this.startTime = startTime;
			this.executionStack = executionStack;
			this.threadName = threadName;
			this.sid = sid;
		}
	}
}
