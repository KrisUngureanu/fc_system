package kz.tamur.or3.server.plugins.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import kz.tamur.DriverException;
import kz.tamur.ods.mssql.MsSqlDriver3;
import kz.tamur.ods.mysql.MySqlDriver3;
import kz.tamur.ods.oracle.OracleDriver3;
import kz.tamur.or3ee.common.UserSession;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

public class JdbcPlugin implements SrvPlugin {
	
	private Session session;

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	/**
	 * Выполнить запрос
	 * @param jndiName 
	 * @param sql строка запроса
	 * @param params список параметров
	 * @return результат
	 * @throws SQLException
	 * @throws NamingException
	 */
	public List<List<Object>> query(
			String jndiName,
			String sql,
			List<Object> params
			) throws SQLException, NamingException {
		
		List<List<Object>> rows = new ArrayList<List<Object>>();
		// Устанавливаем соединение с БД
		Context ic = new InitialContext();
		Object obj = ic.lookup(jndiName);
		DataSource ds = (DataSource)obj;
		Connection conn = ds.getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			// Подготавливаем запрос
			pst = conn.prepareStatement(sql);
			// Если есть параметры, то устанавливаем их
			if (params != null) {
				for (int i = 0; i < params.size(); i++) {
					pst.setObject(i + 1, params.get(i));
				}
			}
			// Выполняем запрос и записываем результаты
			rs = pst.executeQuery();
			ResultSetMetaData md = rs.getMetaData();
			int ccnt = md.getColumnCount();
			while (rs.next()) {
				List<Object> row = new ArrayList<Object>(ccnt);
				for (int i = 0; i < ccnt; i++) {
					row.add(convertToOr3Type(rs, i + 1));
				}
				rows.add(row);
			}
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
			DbUtils.closeQuietly(conn);
		}
		return rows;
	}
	/**
	 * Обновить
	 * @param jndiName
	 * @param sql строка запроса
	 * @param sqlTypes
	 * @param rows
	 * @return кол-во обновленных строк
	 * @throws SQLException
	 * @throws NamingException
	 */
	public int update(
			String jndiName,
			String sql,
			List<Number> sqlTypes,
			List<List<Object>> rows
			) throws SQLException, NamingException {
		
		int res = 0;
		// Устанавливаем соединение с БД
		Context ic = new InitialContext();
		Object obj = ic.lookup(jndiName);
		DataSource ds = (DataSource)obj;
		Connection conn = ds.getConnection();
		conn.setAutoCommit(false);
		PreparedStatement pst = null;
		try {
			// Подготавливаем запрос
			pst = conn.prepareStatement(sql);
			if (rows != null) {
				for (List<Object> row : rows) {
					for (int i = 0; i < row.size(); i++) {
						Object value = row.get(i);
						int sqlType = sqlTypes.get(i).intValue();
						if (value == null) {
							pst.setNull(i + 1, sqlType);
						} else {
							pst.setObject(i + 1, convertToJdbcType(sqlType, value));
						}
					}
					res += pst.executeUpdate();
				}
			} else {
				res = pst.executeUpdate();
			}
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			DbUtils.closeQuietly(pst);
			DbUtils.closeQuietly(conn);
		}
		return res;
	}

	public int insert(
			String jndiName,
			String sql,
			List<Number> sqlTypes,
			List<List<Object>> rows,
			List<List<Object>> keyRows
			) throws SQLException, NamingException {
		
		int res = 0;
		// Устанавливаем соединение с БД
		Context ic = new InitialContext();
		Object obj = ic.lookup(jndiName);
		DataSource ds = (DataSource)obj;
		Connection conn = ds.getConnection();
		conn.setAutoCommit(false);
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			// Подготавливаем запрос
			pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			if (rows != null) {
				for (List<Object> row : rows) {
					for (int i = 0; i < row.size(); i++) {
						Object value = row.get(i);
						int sqlType = sqlTypes.get(i).intValue();
						if (value == null) {
							pst.setNull(i + 1, sqlType);
						} else {
							pst.setObject(i + 1, convertToJdbcType(sqlType, value));
						}
					}
					res += pst.executeUpdate();
					if (keyRows != null) {
						List<Object> keyRow = new ArrayList<Object>();
						rs = pst.getGeneratedKeys();
						ResultSetMetaData md = rs.getMetaData();
						int ccnt = md.getColumnCount();
						if (rs.next()) {
							for (int i = 0; i < ccnt; i++) {
								keyRow.add(convertToOr3Type(rs, i + 1));
							}
							keyRows.add(keyRow);
						}
					}
				}
			} else {
				res = pst.executeUpdate();
			}
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(pst);
			DbUtils.closeQuietly(conn);
		}
		return res;
	}

	/**
	 * Преобразовать в тип Or3
	 * @param rs 
	 * @param i
	 * @return новое значение
	 * @throws SQLException
	 */
	private Object convertToOr3Type(ResultSet rs, int i) throws SQLException {//TODO
		Object obj = rs.getObject(i);
		if (obj instanceof Date) {
			Timestamp ts = rs.getTimestamp(i);
			if (ts != null)
				return new KrnDate(ts.getTime());
			else
				return new KrnDate(((Date)obj).getTime());
		}
		return obj;
	}
	/**
	 * Преобразовать в тип jdbc
	 * @param type
	 * @param obj
	 * @return новое значение
	 */
	private Object convertToJdbcType(int type, Object obj) {//TODO
		if (obj instanceof KrnDate) {
			KrnDate d = (KrnDate)obj;
			if (type == Types.DATE)
				return new java.sql.Date(d.getTime());
			else if (type == Types.TIMESTAMP)
				return new java.sql.Timestamp(d.getTime());
			else
				return d;
		}
		return obj;
	}
	/**
	 * Выполнить процедуру-функцию
	 * @param jndiName 
	 * @param nameProcedure имя процедуры-функции
	 * @param vals список параметров
	 * @param types_in список типов входных параметров
	 * @param types_out список типов выходных параметров
	 * @return результат
	 * @throws SQLException
	 * @throws NamingException
	 * @throws DriverException 
	 */
	public List execProcedure(
			String jndiName,
			String nameProcedure, 
			List<Object> vals, 
			List<String> types_in, 
			List<String> types_out
			) throws SQLException, NamingException, DriverException {
		
		// Устанавливаем соединение с БД
		Context ic = new InitialContext();
		Object obj = ic.lookup(jndiName);
		DataSource ds = (DataSource)obj;
		Connection conn = ds.getConnection();
		try {
			String dbName=conn.getMetaData().getDatabaseProductName();
			
			Log log = LogFactory.getLog(session.getDsName() + ".DatabaseLog." + session.getUserSession().getLogUserName() +
					(UserSession.SERVER_ID != null ? ("." + UserSession.SERVER_ID) : ""));

			if (dbName.contains("Oracle")) {
				return OracleDriver3.execProcedure(nameProcedure, vals, types_in, types_out, conn, log);
			} else if (dbName.contains("MySQL")) {
				return MySqlDriver3.execProcedure(nameProcedure, vals, types_in, types_out, conn, log);
			} else if (dbName.contains("Microsoft SQL Server")) {
				return MsSqlDriver3.execProcedure(nameProcedure, vals, types_in, types_out, conn, log);
			}
		} finally {
			DbUtils.closeQuietly(conn);
		}
		return null;
	}
}
