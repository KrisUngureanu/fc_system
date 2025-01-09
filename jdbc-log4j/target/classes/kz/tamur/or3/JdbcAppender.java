package kz.tamur.or3;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;


/**
 * TESTING CVS
 * 
 * @author erik
 *
 */

public class JdbcAppender extends AppenderSkeleton {
	
	private static Log log = LogFactory.getLog(JdbcAppender.class);
	
	private String jndiName;
	private String scheme;
	private DataSource ds;
	
	private String sql8;
	private String sql9;
	private String sql11;
	
	static enum Action {
		EVENT_LOGIN("Вход в систему"),
		EVENT_LOGOUT("Выход из системы"),
		EVENT_CHANGE_PASSWORD("Смена пароля"),
		
		EVENT_PROCESS_START("Запуск процесса"),
		EVENT_PROCESS_END("Завершение процесса"),
		EVENT_PROCESS_CANCEL("Остановка процесса"),
		
		EVENT_USER_BLOCK("Блокировка пользователя"),
		EVENT_USER_UNBLOCK("Разблокировка пользователя"),
		EVENT_USER_CLOSE("Закрытие сеанса пользователя");
		
		String logName;
		
		Action(String logName) {
			this.logName = logName;name();
		}
		
		public String getLogName() {
			return logName;
		}
	}
	
	private List<String> logActions = new ArrayList<String>();
	
	public String getDataSource() {
		return jndiName;
	}

	public void setDataSource(String jndiName) throws Exception {
		this.jndiName = jndiName;
		try {
			InitialContext ctx = new InitialContext();
			ds = (DataSource)ctx.lookup(jndiName);
			ctx.close();
		} catch (Exception e) {
			log.error(e, e);
		}
	}
	
	public String getScheme() {
		return scheme;
	}
	
	public void setScheme(String scheme) {
		String[] fields = scheme.split(",");
		
		this.scheme = fields[0].trim();
		
		this.logActions.clear();
		for (int i = 1; i < fields.length; i++) {
			try {
				this.logActions.add(Action.valueOf(fields[i].trim()).getLogName());
			} catch (Throwable e) {
				log.error(e, e);
			}
		}
		
		sql8 = null;
		sql11 = null;
	}
	
	public boolean requiresLayout() {
		return false;
	}
	
	public void close() {
	}

	@Override
	protected void append(LoggingEvent event) {
		String message = event.getMessage().toString();
		String[] fields = message.split("\\|");
		
		if (this.logActions.size() == 0 || this.logActions.contains(fields[2].trim())) {
			Connection conn = null;
			PreparedStatement pst = null;
			try {
				conn = ds.getConnection();
				if (fields.length == 13) {
					if (sql11 == null) {
						String tname = "t_syslog";
						if (scheme != null) {
							tname = scheme + "." + tname;
						}
						sql11 = "INSERT INTO " + tname + " (c_logger,c_type,c_action,c_user,c_ip,c_host,c_admin,c_server_id,c_process,c_object,c_message,c_tab_name,c_col_name,c_thread)"
								+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					}
					pst = conn.prepareStatement(sql11);
					pst.setString(1, event.getLoggerName());
					pst.setString(2, fields[1].trim());
					pst.setString(3, fields[2].trim());
					pst.setString(4, fields[3].trim());
					pst.setString(5, fields[4].trim());
					pst.setString(6, fields[5].trim());
					pst.setInt(7, "1".equals(fields[6].trim()) ? 1 : 0);
					pst.setString(8, fields[7].trim());
					pst.setString(9, fields[8].trim());
					pst.setString(10, fields[9].trim());
					pst.setString(11, fields[10].trim());
					pst.setString(12, fields[11].trim());
					pst.setString(13, fields[12].trim());
					pst.setString(14, event.getThreadName());
				
				} else if (fields.length == 10) {
					if (sql8 == null) {
						String tname = "t_syslog";
						if (scheme != null) {
							tname = scheme + "." + tname;
						}
						sql8 = "INSERT INTO " + tname + " (c_logger,c_type,c_action,c_user,c_ip,c_host,c_admin,c_message,c_tab_name,c_col_name,c_thread)"
								+ " VALUES (?,?,?,?,?,?,?,?,?,?,?)";
					}
					pst = conn.prepareStatement(sql8);
					pst.setString(1, event.getLoggerName());
					pst.setString(2, fields[1].trim());
					pst.setString(3, fields[2].trim());
					pst.setString(4, fields[3].trim());
					pst.setString(5, fields[4].trim());
					pst.setString(6, fields[5].trim());
					pst.setInt(7, "1".equals(fields[6].trim()) ? 1 : 0);
					pst.setString(8, fields[7].trim());
					pst.setString(9, fields[8].trim());
					pst.setString(10, fields[9].trim());
					pst.setString(11, event.getThreadName());
				} else if (fields.length == 11) {
					if (sql9 == null) {
						String tname = "t_syslog";
						if (scheme != null) {
							tname = scheme + "." + tname;
						}
						sql9 = "INSERT INTO " + tname + " (c_logger,c_type,c_action,c_user,c_ip,c_host,c_admin,c_server_id,c_message,c_tab_name,c_col_name,c_thread)"
								+ " VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
					}
					pst = conn.prepareStatement(sql9);
					pst.setString(1, event.getLoggerName());
					pst.setString(2, fields[1].trim());
					pst.setString(3, fields[2].trim());
					pst.setString(4, fields[3].trim());
					pst.setString(5, fields[4].trim());
					pst.setString(6, fields[5].trim());
					pst.setInt(7, "1".equals(fields[6].trim()) ? 1 : 0);
					pst.setString(8, fields[7].trim());
					pst.setString(9, fields[8].trim());
					pst.setString(10, fields[9].trim());
					pst.setString(11, fields[10].trim());
					pst.setString(12, event.getThreadName());
				} else {
					log.error("Message has not wrote to JDBC Log, it redirected to log file! Required count of fields is: 10 or 11 or 13, real count is:"+fields.length);
					log.error(message);
				}
				if (pst != null)
					pst.executeUpdate();
				conn.commit();
			} catch (SQLException e) {
				log.error(e, e);
				try {
					conn.rollback();
				} catch (SQLException ex) {
					// Rolling back quietly.
				}
			} finally {
				try {
					if (pst != null) 
						pst.close();
				} catch (Exception ex) {
					// Closing quietly.
				}
				try {
					if (conn != null)
					conn.close();
				} catch (Exception ex) {
					// Closing quietly.
				}
			}
		}
	}
}
