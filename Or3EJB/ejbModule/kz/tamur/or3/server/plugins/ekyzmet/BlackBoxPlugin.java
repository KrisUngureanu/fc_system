package kz.tamur.or3.server.plugins.ekyzmet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Document;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.QueryResult;
import com.cifs.or2.kernel.Time;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

import kz.tamur.comps.Constants;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.ods.Driver;
import kz.tamur.ods.oracle.OracleDriver3;
import kz.tamur.or3.server.lang.SystemOp;
import kz.tamur.or3ee.server.kit.AttrRequestBuilder;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.util.XmlUtil;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

public class BlackBoxPlugin implements SrvPlugin {

	private static final Log log = LogFactory.getLog(BlackBoxPlugin.class);
	private Session session;

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public List<List<Object>> get_list_questions(
			final String jndiName,
			final String blackBoxSchemaName,
			final boolean is_kazakh_lang
			) throws SQLException, NamingException {
		
		List<List<Object>> rows = new ArrayList<List<Object>>();
		// Устанавливаем соединение с БД
		Context ic = new InitialContext();
		Object obj = ic.lookup(jndiName);
		DataSource ds = (DataSource)obj;
		Connection conn = ds.getConnection();
		CallableStatement call = null;
		ResultSet rs = null;
		try {
			// Подготавливаем запрос
			call = conn.prepareCall("{call " + blackBoxSchemaName + ".HILL.get_list_questions(?,?)}");
			call.setInt(1, is_kazakh_lang ? 1 : 0);
			call.registerOutParameter (2, OracleTypes.CURSOR);
			// Выполняем запрос и записываем результаты
			call.execute();
			rs = (ResultSet)call.getObject(2);
			while (rs.next()) {
				List<Object> row = new ArrayList<>(7);
				row.add(rs.getLong(1));
				row.add(rs.getLong(2));
				row.add(rs.getString(3));
				row.add(rs.getString(4));
				row.add(rs.getString(5));
				row.add(rs.getString(6));
				row.add(rs.getString(7));
				row.add(rs.getString(8));
				rows.add(row);
			}
			rs.close();
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(call);
			DbUtils.closeQuietly(conn);
		}
		return rows;
	}

	public void add_question(
			final String jndiName,
			final String blackBoxSchemaName,
			final List<List<Object>> questions
			) throws SQLException, NamingException {
		
		// Устанавливаем соединение с БД
		Context ic = new InitialContext();
		Object obj = ic.lookup(jndiName);
		DataSource ds = (DataSource)obj;
		Connection conn = ds.getConnection();
		CallableStatement call = null;
		try {
			// Подготавливаем запрос
			call = conn.prepareCall("{call " + blackBoxSchemaName + ".HILL.add_question(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			for (List<Object> row : questions) {
				call.setInt(1, ((Number)row.get(0)).intValue());
				for (int i = 1; i < 19; i++) {
					call.setString(i + 1, (String)row.get(i));
				}
				call.execute();
			}
		} finally {
			DbUtils.closeQuietly(call);
			DbUtils.closeQuietly(conn);
		}
	}

	public void clean_questions(
			final String jndiName,
			final String blackBoxSchemaName
			) throws SQLException, NamingException {
		
		// Устанавливаем соединение с БД
		Context ic = new InitialContext();
		Object obj = ic.lookup(jndiName);
		DataSource ds = (DataSource)obj;
		Connection conn = ds.getConnection();
		CallableStatement call = null;
		try {
			// Подготавливаем запрос
			call = conn.prepareCall("{call " + blackBoxSchemaName + ".HILL.clean_questions()}");
			call.execute();
		} finally {
			DbUtils.closeQuietly(call);
			DbUtils.closeQuietly(conn);
		}
	}

	public List<List<Object>> get_list_job_positions(
			final String jndiName,
			final String blackBoxSchemaName
			) throws SQLException, NamingException {
		
		List<List<Object>> rows = new ArrayList<List<Object>>();
		// Устанавливаем соединение с БД
		Context ic = new InitialContext();
		Object obj = ic.lookup(jndiName);
		DataSource ds = (DataSource)obj;
		Connection conn = ds.getConnection();
		CallableStatement call = null;
		ResultSet rs = null;
		try {
			// Подготавливаем запрос
			call = conn.prepareCall("{call " + blackBoxSchemaName + ".HILL.get_list_job_positions(?)}");
			call.registerOutParameter (2, OracleTypes.CURSOR);
			// Выполняем запрос и записываем результаты
			call.execute();
			rs = (ResultSet)call.getObject(2);
			while (rs.next()) {
				List<Object> row = new ArrayList<>();
				row.add(rs.getLong(1));
				row.add(rs.getLong(2));
				row.add(rs.getString(3));
				rows.add(row);
			}
			rs.close();
		} finally {
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(call);
			DbUtils.closeQuietly(conn);
		}
		return rows;
	}

	public void add_job_position(
			final String jndiName,
			final String blackBoxSchemaName,
			final List<List<Object>> jobPositions
			) throws SQLException, NamingException {
		
		// Устанавливаем соединение с БД
		Context ic = new InitialContext();
		Object obj = ic.lookup(jndiName);
		DataSource ds = (DataSource)obj;
		Connection conn = ds.getConnection();
		CallableStatement call = null;
		try {
			// Подготавливаем запрос
			call = conn.prepareCall("{call " + blackBoxSchemaName + ".HILL.add_job_position(?,?,?)}");
			for (List<Object> row : jobPositions) {
				call.setInt(1, ((Number)row.get(0)).intValue());
				call.setInt(2, ((Number)row.get(1)).intValue());
				call.setString(3, (String)row.get(2));
				call.execute();
			}
		} finally {
			DbUtils.closeQuietly(call);
			DbUtils.closeQuietly(conn);
		}
	}

	public void clean_job_positions(
			final String jndiName,
			final String blackBoxSchemaName
			) throws SQLException, NamingException {
		
		// Устанавливаем соединение с БД
		Context ic = new InitialContext();
		Object obj = ic.lookup(jndiName);
		DataSource ds = (DataSource)obj;
		Connection conn = ds.getConnection();
		CallableStatement call = null;
		try {
			// Подготавливаем запрос
			call = conn.prepareCall("{call " + blackBoxSchemaName + ".HILL.clean_job_positions()}");
			call.execute();
		} finally {
			DbUtils.closeQuietly(call);
			DbUtils.closeQuietly(conn);
		}
	}

	public Map<Long, Map<Long, Long>> get_answer_stat(final java.util.Date dateFrom, final java.util.Date dateTo) throws SQLException, KrnException {

		final Driver drv = session.getDriver();
		final Connection conn = drv.getConnection();
		
		final KrnClass appCls = session.getClassByName("ек::тест::Заявка");
		final KrnAttribute appIinAttr = session.getAttributeByName(appCls, "ИИН для поиска");

		final KrnClass histCls = session.getClassByName("ек::тест::История заявки");
		final KrnAttribute histTimeAttr = session.getAttributeByName(histCls, "время начала факт");
		final KrnAttribute histErkAttr = session.getAttributeByName(histCls, "по_ЕРК?");

		final KrnClass qCls = session.getClassByName("ек::тест::спр::Вопрос");
		final KrnAttribute qIdAttr = session.getAttributeByName(qCls, "idExcel");
		
		final KrnClass aCls = session.getClassByName("ек::тест::спр::Вариант ответа");
		final KrnAttribute aIdAttr = session.getAttributeByName(aCls, "idExcel");
		
		final KrnClass arCls = session.getClassByName("ек::тест::Зап таб ответа");
		final KrnAttribute arHstAttr = session.getAttributeByName(arCls, "история заявки");
		final KrnAttribute arQsnAttr = session.getAttributeByName(arCls, "вопрос");
		final KrnAttribute arAwrAttr = session.getAttributeByName(arCls, "ответ");
		final KrnAttribute arAppAttr = session.getAttributeByName(arCls, "заявка");
		final KrnAttribute arHistAttr = session.getAttributeByName(arCls, "история заявки");
		
		final KrnClass vrCls = session.getClassByName("ек::тест::Зап таб просмотра вопроса");
		final KrnAttribute vrArAttr = session.getAttributeByName(vrCls, "зап таб ответа");
		final KrnAttribute vrStartAttr = session.getAttributeByName(vrCls, "время начало");
		final KrnAttribute vrEndAttr = session.getAttributeByName(vrCls, "время конец");
		
		final String aggSql = drv instanceof OracleDriver3 ?
				"SUM(EXTRACT(DAY FROM (" + vrEndAttr.tname + "-" + vrStartAttr.tname + "))*24*3600"
					+ "+EXTRACT(HOUR FROM (" + vrEndAttr.tname + "-" + vrStartAttr.tname + "))*3600"
					+ "+EXTRACT(MINUTE FROM (" + vrEndAttr.tname + "-" + vrStartAttr.tname + "))*60"
					+ "+EXTRACT(SECOND FROM (" + vrEndAttr.tname + "-" + vrStartAttr.tname + ")))"
				: "SUM(UNIX_TIMESTAMP(" + vrEndAttr.tname + ")-UNIX_TIMESTAMP(" + vrStartAttr.tname + "))";

		
		final String sql =
				"SELECT hist.c_obj_id,q." + qIdAttr.tname + ",MAX(a." + aIdAttr.tname + ")," + aggSql
				+ " FROM " + drv.getClassTableName(arCls.id) + " ar"
				+ " INNER JOIN " + drv.getClassTableName(appCls.id) + " app ON app.c_obj_id=ar." + arAppAttr.tname + " AND app.c_tr_id=0"
				+ " INNER JOIN " + drv.getClassTableName(histCls.id) + " hist ON hist.c_obj_id=ar." + arHistAttr.tname + " AND hist.c_tr_id=0"
				+ " INNER JOIN " + drv.getClassTableName(vrCls.id) + " vr ON ar.c_obj_id=vr." + vrArAttr.tname + " AND vr.c_tr_id=0"
				+ " INNER JOIN " + drv.getClassTableName(qCls.id) + " q ON q.c_obj_id=ar." + arQsnAttr.tname + " AND q.c_tr_id=0"
				+ " LEFT JOIN " + drv.getClassTableName(aCls.id) + " a ON a.c_obj_id=ar." + arAwrAttr.tname + " AND a.c_tr_id=0" 
				+ " WHERE ar.c_tr_id=0 AND hist." + histTimeAttr.tname + ">? AND hist." + histTimeAttr.tname + "<=? AND hist." + histErkAttr.tname + "=1"
				+ " GROUP BY hist.c_obj_id,q." + qIdAttr.tname;
		
		log.debug("SQL: " + sql);
		log.error("SQL: " + sql);


		final Map<Long, Map<Long, Long>> res = new HashMap<>();
		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			pst.setDate(1, new Date(dateFrom.getTime()));
			pst.setDate(2, new Date(dateTo.getTime()));
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					final long histId = rs.getLong(1);
					Map<Long, Long> row = res.get(histId);
					if (row == null) {
						row = new HashMap<>();
						res.put(histId, row);
					}
					row.put(rs.getLong(2), rs.getLong(3));
				}
			}
		}
		return res;
	}
	
	public Map<Long, Map<Long, Long>> get_time_stat(final java.util.Date dateFrom, final java.util.Date dateTo) throws SQLException, KrnException {

		final Driver drv = session.getDriver();
		final Connection conn = drv.getConnection();
		
		final KrnClass appCls = session.getClassByName("ек::тест::Заявка");
		final KrnAttribute appIinAttr = session.getAttributeByName(appCls, "ИИН для поиска");

		final KrnClass histCls = session.getClassByName("ек::тест::История заявки");
		final KrnAttribute histTimeAttr = session.getAttributeByName(histCls, "время начала факт");
		final KrnAttribute histErkAttr = session.getAttributeByName(histCls, "по_ЕРК?");

		final KrnClass qCls = session.getClassByName("ек::тест::спр::Вопрос");
		final KrnAttribute qIdAttr = session.getAttributeByName(qCls, "idExcel");
		
		final KrnClass aCls = session.getClassByName("ек::тест::спр::Вариант ответа");
		final KrnAttribute aIdAttr = session.getAttributeByName(aCls, "idExcel");
		
		final KrnClass arCls = session.getClassByName("ек::тест::Зап таб ответа");
		final KrnAttribute arHstAttr = session.getAttributeByName(arCls, "история заявки");
		final KrnAttribute arQsnAttr = session.getAttributeByName(arCls, "вопрос");
		final KrnAttribute arAwrAttr = session.getAttributeByName(arCls, "ответ");
		final KrnAttribute arAppAttr = session.getAttributeByName(arCls, "заявка");
		final KrnAttribute arHistAttr = session.getAttributeByName(arCls, "история заявки");
		
		final KrnClass vrCls = session.getClassByName("ек::тест::Зап таб просмотра вопроса");
		final KrnAttribute vrArAttr = session.getAttributeByName(vrCls, "зап таб ответа");
		final KrnAttribute vrStartAttr = session.getAttributeByName(vrCls, "время начало");
		final KrnAttribute vrEndAttr = session.getAttributeByName(vrCls, "время конец");
		
		final String aggSql = drv instanceof OracleDriver3 ?
				"SUM(EXTRACT(DAY FROM (" + vrEndAttr.tname + "-" + vrStartAttr.tname + "))*24*3600"
					+ "+EXTRACT(HOUR FROM (" + vrEndAttr.tname + "-" + vrStartAttr.tname + "))*3600"
					+ "+EXTRACT(MINUTE FROM (" + vrEndAttr.tname + "-" + vrStartAttr.tname + "))*60"
					+ "+EXTRACT(SECOND FROM (" + vrEndAttr.tname + "-" + vrStartAttr.tname + ")))"
				: "SUM(UNIX_TIMESTAMP(" + vrEndAttr.tname + ")-UNIX_TIMESTAMP(" + vrStartAttr.tname + "))";
		
		final String sql =
				"SELECT hist.c_obj_id,q." + qIdAttr.tname + ",MAX(a." + aIdAttr.tname + ")," + aggSql
				+ " FROM " + drv.getClassTableName(arCls.id) + " ar"
				+ " INNER JOIN " + drv.getClassTableName(appCls.id) + " app ON app.c_obj_id=ar." + arAppAttr.tname + " AND app.c_tr_id=0"
				+ " INNER JOIN " + drv.getClassTableName(histCls.id) + " hist ON hist.c_obj_id=ar." + arHistAttr.tname + " AND hist.c_tr_id=0"
				+ " INNER JOIN " + drv.getClassTableName(vrCls.id) + " vr ON ar.c_obj_id=vr." + vrArAttr.tname + " AND vr.c_tr_id=0"
				+ " INNER JOIN " + drv.getClassTableName(qCls.id) + " q ON q.c_obj_id=ar." + arQsnAttr.tname + " AND q.c_tr_id=0"
				+ " LEFT JOIN " + drv.getClassTableName(aCls.id) + " a ON a.c_obj_id=ar." + arAwrAttr.tname + " AND a.c_tr_id=0" 
				+ " WHERE ar.c_tr_id=0 AND hist." + histTimeAttr.tname + ">? AND hist." + histTimeAttr.tname + "<=? AND hist." + histErkAttr.tname + "=1"
				+ " GROUP BY hist.c_obj_id,q." + qIdAttr.tname;

		log.debug("SQL: " + sql);
		log.error("SQL: " + sql);


		final Map<Long, Map<Long, Long>> res = new HashMap<>();
		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			pst.setDate(1, new Date(dateFrom.getTime()));
			pst.setDate(2, new Date(dateTo.getTime()));
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					final long histId = rs.getLong(1);
					Map<Long, Long> row = res.get(histId);
					if (row == null) {
						row = new HashMap<>();
						res.put(histId, row);
					}
					row.put(rs.getLong(2), rs.getLong(4));
				}
			}
		}
		return res;
	}

	public KrnObject get_result(
			final String jndiName,
			final String blackBoxSchemaName,
			final long histId
			) throws SQLException, NamingException, KrnException, ParseException {
		
		// Устанавливаем соединение с БД
		Context ic = new InitialContext();
		Object obj = ic.lookup(jndiName);
		DataSource ds = (DataSource)obj;
		Connection conn = ds.getConnection();
		CallableStatement call = null;
		
		try {
			OracleConnection oraConn = conn.unwrap(OracleConnection.class);

			call = oraConn.prepareCall("{call " + blackBoxSchemaName + ".HILL.get_result(?,?,?,?)}");
			
			final String schema = blackBoxSchemaName.toUpperCase(Locale.ROOT);
			
			ARRAY test_answers = getAnswers(histId, schema, oraConn);
			
			KrnObject[] prg = { null };
			STRUCT[] sessionAndUser = getSessionAndUser(histId, schema, oraConn, prg);
			
			call.setObject(1, test_answers);
			call.setObject(2, sessionAndUser[0]);
			call.setObject(3, sessionAndUser[1]);
			call.registerOutParameter(4, OracleTypes.STRUCT, schema + ".REPORT_RESULT");

			call.execute();
			STRUCT struct = (STRUCT)call.getObject(4);
			
			return createReportResult(struct);
			
		} finally {
			DbUtils.closeQuietly(call);
			DbUtils.closeQuietly(conn);
		}
	}
	
	public KrnObject get_result_2(
			final String jndiName,
			final String blackBoxSchemaName,
			final Long histId
			) throws SQLException, NamingException, KrnException, ParseException {
		
		// Устанавливаем соединение с БД
		Context ic = new InitialContext();
		Object obj = ic.lookup(jndiName);
		DataSource ds = (DataSource)obj;
		Connection conn = ds.getConnection();
		CallableStatement call = null;
		
		try {
			OracleConnection oraConn = conn.unwrap(OracleConnection.class);

			call = oraConn.prepareCall("{call " + blackBoxSchemaName + ".HILL.get_result(?,?,?,?,?)}");
			
			final String schema = blackBoxSchemaName.toUpperCase(Locale.ROOT);
			
			ARRAY test_answers = getAnswers(histId, schema, oraConn);
			
			KrnObject[] prg = { null };
			
			STRUCT[] sessionAndUser = getSessionAndUser(histId, schema, oraConn, prg);
			
			call.setObject(1, 3);
			call.setObject(2, test_answers);
			call.setObject(3, sessionAndUser[0]);
			call.setObject(4, sessionAndUser[1]);
			call.registerOutParameter(5, OracleTypes.STRUCT, schema + ".REPORT_RESULT");

			call.execute();
			STRUCT struct = (STRUCT)call.getObject(5);
			
			KrnObject[] resObjHolder = new KrnObject[1];
			KrnObject repResObj = createReportResult2(struct, resObjHolder);
			KrnClass histCls = session.getClassByName("ек::тест::История заявки");
			session.setObject(histId, session.getAttributeByName(histCls, "результат").id, 0, resObjHolder[0].id, 0, false);
			return repResObj;
		} finally {
			DbUtils.closeQuietly(call);
			DbUtils.closeQuietly(conn);
		}
	}

	private STRUCT[] getSessionAndUser(final long histId, final String schema, final OracleConnection oraConn, KrnObject[] outPrg) throws SQLException, KrnException {
		
		final KrnObject langRu = session.getObjectByUid("102", 0);
		final KrnObject langKz = session.getObjectByUid("103", 0);

		AttrRequestBuilder catRb = new AttrRequestBuilder("Категория госслужащего", session)
				.add("наименование", langRu.id);

		AttrRequestBuilder famRb = new AttrRequestBuilder("ек::тест::спр::Семья должностей", session)
				.add("наименование", langRu.id)
				.add("наименование", langKz.id);

		AttrRequestBuilder psnRb = new AttrRequestBuilder("ек::тест::Персона", session)
				.add("ИИН")
				.add("фамилия")
				.add("имя")
				.add("отчество");

		AttrRequestBuilder appRb = new AttrRequestBuilder("ек::тест::Заявка", session)
				.add("тип тестирования")
				.add("программа")
				.add("должность", langRu.id)
				.add("персона", psnRb)
				.add("категория", catRb)
				.add("семья должностей", famRb);

		AttrRequestBuilder histRb = new AttrRequestBuilder("ек::тест::История заявки", session)
				.add("время начала факт")
				.add("язык тестирования")
				.add("заявка", appRb);

		QueryResult qr = session.getObjects(new long[] { histId }, histRb.build(), 0);
		if (qr.totalRows > 0) {
			Object[] row = qr.rows.get(0);
			
			Time begTime = (Time)histRb.getValue("время начала факт", row);
			KrnObject lang = histRb.getObjectValue("язык тестирования", row);
			
			StructDescriptor sessionDesc = StructDescriptor.createDescriptor(schema + ".TEST_SESSION", oraConn);
			STRUCT testSession = new STRUCT(sessionDesc, oraConn, new Object[] {
					String.format("%02d.%02d.%04d", begTime.day, begTime.month + 1, begTime.year),
					String.format("%02d:%02d:%02d", begTime.hour, begTime.min + 1, begTime.sec),
					"",
					"Уполномоченный по этике в ЦГО (функциональный блок В)",
//					histRb.getStringValue("заявка.должность", langRu.id, row)
//					+ " " + histRb.getStringValue("заявка.категория.наименование", langRu.id, row),
					"1014162.3482112".equals(histRb.getObjectValue("заявка.тип тестирования", row).uid) ? 1 : 0 //"1014162.3482112" - Аттестация
			});
			
			StructDescriptor userDesc = StructDescriptor.createDescriptor(schema + ".TEST_USER", oraConn);
			STRUCT testUser = new STRUCT(userDesc, oraConn, new Object[] {
					histRb.getStringValue("заявка.персона.ИИН", row),
					histRb.getStringValue("заявка.персона.фамилия", row),
					histRb.getStringValue("заявка.персона.отчество", row),
					histRb.getStringValue("заявка.персона.имя", row),
					"102".equals(lang.uid) ? 0 : 1 //"102" - Русский
			});
			
			outPrg[0] = histRb.getObjectValue("заявка.программа", row);
			
			return new STRUCT[] {testSession, testUser};
		}
		return null;
	}
	
	private ARRAY getAnswers(final long histId, final String schema, final OracleConnection oraConn) throws SQLException, KrnException {

		final Driver drv = session.getDriver();
		final Connection conn = drv.getConnection();
		
		final KrnClass qCls = session.getClassByName("ек::тест::спр::Вопрос");
		final KrnAttribute qIdAttr = session.getAttributeByName(qCls, "idExcel");
		
		final KrnClass aCls = session.getClassByName("ек::тест::спр::Вариант ответа");
		final KrnAttribute aIdAttr = session.getAttributeByName(aCls, "idExcel");
		
		final KrnClass arCls = session.getClassByName("ек::тест::Зап таб ответа");
		final KrnAttribute arHstAttr = session.getAttributeByName(arCls, "история заявки");
		final KrnAttribute arQsnAttr = session.getAttributeByName(arCls, "вопрос");
		final KrnAttribute arAwrAttr = session.getAttributeByName(arCls, "ответ");
		
		final KrnClass vrCls = session.getClassByName("ек::тест::Зап таб просмотра вопроса");
		final KrnAttribute vrArAttr = session.getAttributeByName(vrCls, "зап таб ответа");
		final KrnAttribute vrStartAttr = session.getAttributeByName(vrCls, "время начало");
		final KrnAttribute vrEndAttr = session.getAttributeByName(vrCls, "время конец");
		
		final String aggSql = drv instanceof OracleDriver3 ?
				"SUM(EXTRACT(DAY FROM (" + vrEndAttr.tname + "-" + vrStartAttr.tname + "))*24*3600"
				+ "+EXTRACT(HOUR FROM (" + vrEndAttr.tname + "-" + vrStartAttr.tname + "))*3600"
				+ "+EXTRACT(MINUTE FROM (" + vrEndAttr.tname + "-" + vrStartAttr.tname + "))*60"
				+ "+EXTRACT(SECOND FROM (" + vrEndAttr.tname + "-" + vrStartAttr.tname + ")))"
				: "SUM(UNIX_TIMESTAMP(" + vrEndAttr.tname + ")-UNIX_TIMESTAMP(" + vrStartAttr.tname + "))";
		
		final String sql =
				"SELECT q."+ qIdAttr.tname + ",MAX(a."+ aIdAttr.tname + ")," + aggSql
				+ " FROM " + drv.getClassTableName(arCls.id) + " ar"
				+ " INNER JOIN " + drv.getClassTableName(vrCls.id) + " vr ON ar.c_obj_id=vr." + vrArAttr.tname + " AND vr.c_tr_id=0"
				+ " INNER JOIN " + drv.getClassTableName(qCls.id) + " q ON q.c_obj_id=ar." + arQsnAttr.tname + " AND q.c_tr_id=0"
				+ " LEFT JOIN " + drv.getClassTableName(aCls.id) + " a ON a.c_obj_id=ar." + arAwrAttr.tname + " AND a.c_tr_id=0" 
				+ " WHERE ar." + arHstAttr.tname + "=? AND ar.c_tr_id=0"
				+ " GROUP BY q."+ qIdAttr.tname;

		final StructDescriptor answerDesc = StructDescriptor.createDescriptor(schema + ".ANSWER", oraConn);
		final List<STRUCT> answers = new ArrayList<>();
		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			pst.setLong(1, histId);
			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					Long aid = rs.getLong(2);
					if (!rs.wasNull() && aid > 0) {
						answers.add(new STRUCT(answerDesc, oraConn, new Object[] {rs.getLong(1), aid, rs.getLong(3)}));
					}
				}
			}
		}
		final STRUCT[] answerArr = answers.toArray(new STRUCT[answers.size()]);
		return new ARRAY(ArrayDescriptor.createDescriptor(schema + ".TEST_ANSWERS", oraConn), oraConn, answerArr);
	}
	
	private KrnObject createReportResult(STRUCT reportResult) throws KrnException, SQLException, ParseException {
		
		final KrnObject langRu = session.getObjectByUid("102", 0);
		
		final String report_title = (String)reportResult.getAttributes()[0];
		
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyyHH:mm");

		int i = 0;
		STRUCT page = (STRUCT)reportResult.getAttributes()[1];
		KrnClass cls = session.getClassByName("report_session_description");
		Map<Pair<KrnAttribute, Long>, Object> values = new HashMap<Pair<KrnAttribute,Long>, Object>();
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "surname"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "middlename"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "firstname"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "IIN"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "program_title"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "job_position"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "job_family"), langRu.id), page.getAttributes()[i++]);
		final String sessDate = (String)page.getAttributes()[i++];
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "sess_date"), 0L), new Date(dateFormat.parse(sessDate).getTime()));
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "sess_place"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "competency_profile"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "sess_time_start"), 0L), new Timestamp(timeFormat.parse(sessDate + page.getAttributes()[i++]).getTime()));
//		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "sess_time_end"), 0L), new Timestamp(timeFormat.parse(sessDate + page.getAttributes()[i++]).getTime()));
		final KrnObject page_1 = session.createObject(cls, values, 0);
		
		i = 0;
		page = (STRUCT)reportResult.getAttributes()[2];
		cls = session.getClassByName("report_individual_profile");
		values = new HashMap<Pair<KrnAttribute,Long>, Object>();
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "average_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "recomendation"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "responsibility_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "responsibility_goal"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "collaboration_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "collaboration_goal"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "purposefulness_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "purposefulness_goal"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "system_planing_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "system_planing_goal"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "potential_development_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "potential_development_goal"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "team_work_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "team_work_goal"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "leadership_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "leadership_goal"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "customer_focus_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "customer_focus_goal"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "stress_resistance_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "stress_resistance_goal"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "innovation_management_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "innovation_management_goal"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "sociability_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "sociability_goal"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "conviction_mark"), 0L), page.getAttributes()[i++]);
		i++; // Пропускаем 
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "conviction_goal"), 0L), page.getAttributes()[i++]);
		final KrnObject page_2 = session.createObject(cls, values, 0);

		i = 0;
		page = (STRUCT)reportResult.getAttributes()[3];
		cls = session.getClassByName("report_position_satisfaction");
		values = new HashMap<Pair<KrnAttribute,Long>, Object>();
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "level_description"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "responsibility_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "collaboration_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "purposefulness_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "system_planing_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "potential_development_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "team_work_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "leadership_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "customer_focus_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "stress_resistance_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "innovation_management_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "sociability_mark"), 0L), page.getAttributes()[i++]);
/*
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "system_planing_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "system_planing_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "system_planing_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "purposefulness_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "purposefulness_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "purposefulness_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "responsibility_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "responsibility_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "responsibility_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "leadership_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "leadership_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "leadership_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "team_work_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "team_work_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "team_work_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "potential_development_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "potential_dev_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "potential_dev_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "innovation_management_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "innovation_man_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "innovation_man_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "customer_focus_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "customer_focus_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "customer_focus_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "stress_resistance_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "stress_resist_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "stress_resist_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "conviction_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "conviction_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "conviction_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "collaboration_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "collaboration_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "collaboration_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "sociability_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "sociability_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "sociability_interval_end"), 0L), page.getAttributes()[i++]);
*/
		final KrnObject page_3 = session.createObject(cls, values, 0);
/*		
		i = 0;
		page = (STRUCT)reportResult.getAttributes()[4];
		cls = session.getClassByName("report_value_commitment");
		values = new HashMap<Pair<KrnAttribute,Long>, Object>();
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "main_value"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "second_value"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "fairness_size"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "neutrality_size"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "ethics_size"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "patriotism_size"), 0L), page.getAttributes()[i++]);
		final KrnObject page_4 = session.createObject(cls, values, 0);
*/
		i = 0;
		page = (STRUCT)reportResult.getAttributes()[4];
		cls = session.getClassByName("report_competency_description");
		values = new HashMap<Pair<KrnAttribute,Long>, Object>();
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "competency_description"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "dev_competency_description"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "strong_skills"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "developing_skills"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "glossary"), langRu.id), page.getAttributes()[i++]);
		final KrnObject page_56 = session.createObject(cls, values, 0);

		cls = session.getClassByName("report_result");
		values = new HashMap<Pair<KrnAttribute,Long>, Object>();
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "report_title"), langRu.id), report_title);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "report_session_description"), 0L), page_1);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "report_individual_profile"), 0L), page_2);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "report_position_satisfaction"), 0L), page_3);
//		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "report_value_commitment"), 0L), page_4);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "report_competency_description"), 0L), page_56);
		final KrnObject report_result = session.createObject(cls, values, 0);
		
		return report_result;
	}

	private KrnObject createReportResult2(STRUCT reportResult, KrnObject[] resObjHolder) throws KrnException, SQLException, ParseException {
		
		final KrnObject langRu = session.getObjectByUid("102", 0);
		
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyyHH:mm");

		int i = 0;
		STRUCT page = (STRUCT)reportResult.getAttributes()[0];
		KrnClass cls = session.getClassByName("report_session_description");
		Map<Pair<KrnAttribute, Long>, Object> values = new HashMap<Pair<KrnAttribute,Long>, Object>();
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "surname"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "middlename"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "firstname"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "IIN"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "program_title"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "job_position"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "job_level"), langRu.id), page.getAttributes()[i++]);
		final String sessDate = (String)page.getAttributes()[i++];
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "sess_date"), 0L), new Date(dateFormat.parse(sessDate).getTime()));
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "sess_place"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "competency_profile"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "sess_time_start"), 0L), new Timestamp(timeFormat.parse(sessDate + page.getAttributes()[i++]).getTime()));
		//values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "sess_time_end"), 0L), new Timestamp(timeFormat.parse(sessDate + page.getAttributes()[i++]).getTime()));
		final KrnObject page_1 = session.createObject(cls, values, 0);
		
		i = 0;
		page = (STRUCT)reportResult.getAttributes()[1];
		cls = session.getClassByName("ек::тест::report_individual");
		values = new HashMap<Pair<KrnAttribute,Long>, Object>();
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "level_description"), 0L), page.getAttributes()[i++]);
		final KrnObject page_2 = session.createObject(cls, values, 0);
		
		Object[] arr = (Object[])((ARRAY)page.getAttributes()[i++]).getArray();
		cls = session.getClassByName("ек::тест::item_marks_for_comp");
		KrnClass compCls = session.getClassByName("BB_Компетенция");
		for (Object obj : arr) {
			i = 0;
			STRUCT item = (STRUCT)obj;
			values = new HashMap<Pair<KrnAttribute,Long>, Object>();
			KrnObject[] compObjs = session.getObjectsByAttribute(compCls.id, session.getAttributeByName(compCls, "id_bb").id, 0, ComparisonOperations.CO_EQUALS, item.getAttributes()[i++], 0);
			values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "comp_id"), 0L), compObjs[0]);
			values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "mark_goal"), 0L), item.getAttributes()[i++]);
			values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "mark_sum"), 0L), item.getAttributes()[i++]);
			Number markPrc = (Number)item.getAttributes()[i++];
			values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "mark_prc"), 0L), markPrc);
			Number calcType = (Number)item.getAttributes()[i++];
			values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "calc_type"), 0L), calcType);
			values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "text_block"), 0L), item.getAttributes()[i++]);
			
			STRUCT intervals = (STRUCT)item.getAttributes()[i++];
			if (intervals != null) {
				i = 0;
				values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "r1"), 0L), intervals.getAttributes()[i++]);
				values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "r2"), 0L), intervals.getAttributes()[i++]);
				values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "r3"), 0L), intervals.getAttributes()[i++]);
				values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "r4"), 0L), intervals.getAttributes()[i++]);
			}

			values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "report_individual"), 0L), page_2);
			session.createObject(cls, values, 0);
			
			if (calcType.intValue() == 4) {
				if (markPrc.doubleValue() >= 50) {
					resObjHolder[0] = session.getObjectByUid("1014162.3412572", 0);
				} else {
					resObjHolder[0] = session.getObjectByUid("1014162.3412573", 0);
				}
			}
		}
		
		/*
		i = 0;
		page = (STRUCT)reportResult.getAttributes()[3];
		cls = session.getClassByName("report_position_satisfaction");
		values = new HashMap<Pair<KrnAttribute,Long>, Object>();
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "level_description"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "responsibility_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "collaboration_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "purposefulness_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "system_planing_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "potential_development_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "team_work_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "leadership_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "customer_focus_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "stress_resistance_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "innovation_management_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "sociability_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "system_planing_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "system_planing_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "system_planing_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "purposefulness_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "purposefulness_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "purposefulness_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "responsibility_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "responsibility_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "responsibility_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "leadership_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "leadership_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "leadership_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "team_work_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "team_work_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "team_work_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "potential_development_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "potential_dev_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "potential_dev_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "innovation_management_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "innovation_man_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "innovation_man_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "customer_focus_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "customer_focus_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "customer_focus_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "stress_resistance_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "stress_resist_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "stress_resist_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "conviction_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "conviction_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "conviction_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "collaboration_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "collaboration_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "collaboration_interval_end"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "sociability_mark"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "sociability_interval_start"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "sociability_interval_end"), 0L), page.getAttributes()[i++]);
		final KrnObject page_3 = session.createObject(cls, values, 0);
*/
/*		
		i = 0;
		page = (STRUCT)reportResult.getAttributes()[4];
		cls = session.getClassByName("report_value_commitment");
		values = new HashMap<Pair<KrnAttribute,Long>, Object>();
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "main_value"), langRu.id), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "second_value"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "fairness_size"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "neutrality_size"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "ethics_size"), 0L), page.getAttributes()[i++]);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "patriotism_size"), 0L), page.getAttributes()[i++]);
		final KrnObject page_4 = session.createObject(cls, values, 0);
*/
		i = 0;
		page = (STRUCT)reportResult.getAttributes()[3];
		KrnObject page_56 = null;
		if (page != null) {
			cls = session.getClassByName("report_competency_description");
			values = new HashMap<Pair<KrnAttribute,Long>, Object>();
			values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "competency_description"), langRu.id), page.getAttributes()[i++]);
			values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "dev_competency_description"), langRu.id), page.getAttributes()[i++]);
			values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "strong_skills"), langRu.id), page.getAttributes()[i++]);
			values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "developing_skills"), langRu.id), page.getAttributes()[i++]);
			page_56 = session.createObject(cls, values, 0);
		}

		cls = session.getClassByName("report_result");
		values = new HashMap<Pair<KrnAttribute,Long>, Object>();
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "report_session_description"), 0L), page_1);
		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "page_2"), 0L), page_2);
//		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "report_position_satisfaction"), 0L), page_3);
//		values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "report_value_commitment"), 0L), page_4);
		if (page_56 != null) {
			values.put(new Pair<KrnAttribute, Long>(session.getAttributeByName(cls, "report_competency_description"), 0L), page_56);
		}
		final KrnObject report_result = session.createObject(cls, values, 0);
		
		String[] jobIds = (String[])((ARRAY)reportResult.getAttributes()[2]).getArray();
		if (jobIds.length > 0) {
			KrnAttribute page3Attr = session.getAttributeByName(cls, "page_3");
			values = new HashMap<Pair<KrnAttribute,Long>, Object>();
			KrnClass jobCls = session.getClassByName("BB_Должность_официальная");
			KrnAttribute jobIdAttr = session.getAttributeByName(jobCls, "id_BB");
			i = 0;
			for (String jobId : jobIds) {
				KrnObject[] jobObjs = session.getObjectsByAttribute(jobCls.id, jobIdAttr.id, 0, ComparisonOperations.CO_EQUALS, Long.valueOf(jobId), 0);
				if (jobObjs.length == 0) {
					log.warn("Не найден объект \"BB_Должность_официальная\" с id_BB=" + jobId);
				} else if (jobObjs.length > 1) {
					log.warn("Найдено " + jobObjs.length + " объектов \"BB_Должность_официальная\" с id_BB=" + jobId);
				} else {
					session.setObject(report_result.id, page3Attr.id, i++, jobObjs[0].id, 0, false);
				}
			}
		}

		return report_result;
	}

	public KrnObject get_result(
			final String jndiName,
			final String blackBoxSchemaName,
			final String iin,
			final String lastName,
			final String firstName,
			final String middleName,
			final String position,
			final String awrFileName,
			final String timeFileName,
			final KrnObject lang
			) throws SQLException, NamingException, KrnException, ParseException, IOException {
		
		// Устанавливаем соединение с БД
		Context ic = new InitialContext();
		Object obj = ic.lookup(jndiName);
		DataSource ds = (DataSource)obj;
		Connection conn = ds.getConnection();
		CallableStatement call = null;
		
		try {
			OracleConnection oraConn = conn.unwrap(OracleConnection.class);

			call = oraConn.prepareCall("{call " + blackBoxSchemaName + ".HILL.get_result(?,?,?,?)}");
			
			final String schema = blackBoxSchemaName.toUpperCase(Locale.ROOT);
			
			ARRAY test_answers = getAnswers(iin, schema, oraConn, awrFileName, timeFileName);
			
			StructDescriptor sessionDesc = StructDescriptor.createDescriptor(schema + ".TEST_SESSION", oraConn);
			STRUCT testSession = new STRUCT(sessionDesc, oraConn, new Object[] {
					new SimpleDateFormat("dd.MM.yyyy").format(new java.util.Date()),
					new SimpleDateFormat("HH:mm:ss").format(new java.util.Date()),
					"",
					position,
					1
			});
			
			StructDescriptor userDesc = StructDescriptor.createDescriptor(schema + ".TEST_USER", oraConn);
			STRUCT testUser = new STRUCT(userDesc, oraConn, new Object[] {
					iin,
					lastName,
					middleName,
					firstName,
					"102".equals(lang.uid) ? 0 : 1 //"102" - Русский
			});
			
			call.setObject(1, test_answers);
			call.setObject(2, testSession);
			call.setObject(3, testUser);
			call.registerOutParameter(4, OracleTypes.STRUCT, schema + ".REPORT_RESULT");

			call.execute();
			STRUCT struct = (STRUCT)call.getObject(4);
			
			return createReportResult(struct);
			
		} finally {
			DbUtils.closeQuietly(call);
			DbUtils.closeQuietly(conn);
		}
	}

	public KrnObject get_result_2(
			final String jndiName,
			final String blackBoxSchemaName,
			final String iin,
			final String lastName,
			final String firstName,
			final String middleName,
			final Long programId,
			final String position,
			final String awrFileName,
			final String timeFileName,
			final KrnObject lang
			) throws SQLException, NamingException, KrnException, ParseException, IOException {
		
		// Устанавливаем соединение с БД
		Context ic = new InitialContext();
		Object obj = ic.lookup(jndiName);
		DataSource ds = (DataSource)obj;
		Connection conn = ds.getConnection();
		CallableStatement call = null;
		
		try {
			OracleConnection oraConn = conn.unwrap(OracleConnection.class);

			call = oraConn.prepareCall("{call " + blackBoxSchemaName + ".HILL.get_result(?,?,?,?,?)}");
			
			final String schema = blackBoxSchemaName.toUpperCase(Locale.ROOT);
			
			ARRAY test_answers = getAnswers(iin, schema, oraConn, awrFileName, timeFileName);
			
			StructDescriptor sessionDesc = StructDescriptor.createDescriptor(schema + ".TEST_SESSION", oraConn);
			STRUCT testSession = new STRUCT(sessionDesc, oraConn, new Object[] {
					new SimpleDateFormat("dd.MM.yyyy").format(new java.util.Date()),
					new SimpleDateFormat("HH:mm:ss").format(new java.util.Date()),
					"",
					position,
					0
			});
			
			StructDescriptor userDesc = StructDescriptor.createDescriptor(schema + ".TEST_USER", oraConn);
			STRUCT testUser = new STRUCT(userDesc, oraConn, new Object[] {
					iin,
					lastName,
					middleName,
					firstName,
					"102".equals(lang.uid) ? 0 : 1 //"102" - Русский
			});
			
			call.setLong(1, programId);
			call.setObject(2, test_answers);
			call.setObject(3, testSession);
			call.setObject(4, testUser);
			call.registerOutParameter(5, OracleTypes.STRUCT, schema + ".REPORT_RESULT");

			call.execute();
			STRUCT struct = (STRUCT)call.getObject(5);
			
			KrnObject[] resObjHolder = new KrnObject[1];
			return createReportResult2(struct, resObjHolder);
			
		} finally {
			DbUtils.closeQuietly(call);
			DbUtils.closeQuietly(conn);
		}
	}

	private ARRAY getAnswers(
			final String iin,
			final String schema,
			final OracleConnection oraConn,
			final String awrFileName,
			final String timeFileName
			) throws SQLException, KrnException, IOException {
		
		List<Long> questionIds = new ArrayList<>();
		List<Long> answerIds = new ArrayList<>();
		List<Long> answerTimes = new ArrayList<>();
		
		try (BufferedReader r = new BufferedReader(new FileReader(awrFileName))) {
			String line = r.readLine(); // Строка с номерами вопросов
			String[] tokens = line.split(",");
			for (int i = 1; i < tokens.length; i++) {
				questionIds.add(Long.valueOf(tokens[i].trim()));
			}
			// Ищем строку с iin
			while (line != null && !tokens[0].trim().equals(iin)) {
				line = r.readLine();
				tokens = line.split(",");
			}
			if (line != null) {
				for (int i = 1; i < tokens.length; i++) {
					answerIds.add(Long.valueOf(tokens[i].trim()));
				}
			}
		}
		
		try (BufferedReader r = new BufferedReader(new FileReader(timeFileName))) {
			String line = r.readLine(); // Строка с номерами вопросов
			String[] tokens = line.split(",");
			// Ищем строку с iin
			while (line != null && !tokens[0].trim().equals(iin)) {
				line = r.readLine();
				tokens = line.split(",");
			}
			if (line != null) {
				for (int i = 1; i < tokens.length; i++) {
					answerTimes.add(Long.valueOf(tokens[i].trim()));
				}
			}
		}

		final StructDescriptor answerDesc = StructDescriptor.createDescriptor(schema + ".ANSWER", oraConn);
		final List<STRUCT> answers = new ArrayList<>();
		for (int i = 0; i < questionIds.size(); i++) {
			answers.add(new STRUCT(answerDesc, oraConn, new Object[] {questionIds.get(i).longValue(), answerIds.get(i).longValue(), answerTimes.get(i).longValue()}));
		}
		final STRUCT[] answerArr = answers.toArray(new STRUCT[answers.size()]);
		return new ARRAY(ArrayDescriptor.createDescriptor(schema + ".TEST_ANSWERS", oraConn), oraConn, answerArr);
	}
	
	public byte[] generateReport(Element dataElement, String templateDir, List<String> templateFileNames) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLOutputter out = new XMLOutputter();
        out.getFormat().setEncoding("UTF-8");
        out.output(dataElement, os);
        os.close();
        
        return generateReport(os.toByteArray(), templateDir, templateFileNames);
	}
	
	public byte[] generateReport(byte[] data, String templateDir, List<String> templateFileNames) throws Exception {
        JRXmlDataSource xmlDs = new JRXmlDataSource(new ByteArrayInputStream(data));
        xmlDs.next();
        Document doc = xmlDs.subDocument();
        xmlDs.close();
        
        Map<String, Object> params = new HashMap<>();
        params.put("SUBREPORT_DIR", templateDir + "/");
        params.put("XML_DATA_DOCUMENT", doc);
        
		JasperPrint jprint1 = JasperFillManager.fillReport(templateDir + "/" + templateFileNames.get(0), params);
		for (int i = 1; i < templateFileNames.size(); i++) {
			JasperPrint jprint = JasperFillManager.fillReport(templateDir + "/" + templateFileNames.get(i), params);
			for (JRPrintPage page : jprint.getPages()) {
				jprint1.addPage(page);
			}
		}
		return JasperExportManager.exportReportToPdf(jprint1);
	}
	
	public byte[] generateReport(byte[] data, List<File> templateFiles) throws Exception {
		return generateReport(data, templateFiles, "PDF");
	}
	
	public byte[] generateReport(byte[] data, List<File> templateFiles, String format) throws Exception {
        JRXmlDataSource xmlDs = new JRXmlDataSource(new ByteArrayInputStream(data));
        xmlDs.next();
        Document doc = xmlDs.subDocument();
        xmlDs.close();
        
        List<File> tempTemplateFiles = new ArrayList<>();
        
        try {
	        for (File templateFile : templateFiles) {
		        File res = Funcs.createTempFile("REP_", ".jasper", Constants.DOCS_DIRECTORY);
		        Funcs.copy(templateFile, res);
		        tempTemplateFiles.add(res);
	        }
	        
	        Map<String, Object> params = new HashMap<>();
	        params.put("SUBREPORT_DIR", Constants.DOCS_DIRECTORY.getAbsolutePath() + "/");
	        params.put("XML_DATA_DOCUMENT", doc);
	        
			JasperPrint jprint1 = JasperFillManager.fillReport(tempTemplateFiles.get(0).getAbsolutePath(), params);
			for (int i = 1; i < tempTemplateFiles.size(); i++) {
				JasperPrint jprint = JasperFillManager.fillReport(tempTemplateFiles.get(i).getAbsolutePath(), params);
				for (JRPrintPage page : jprint.getPages()) {
					jprint1.addPage(page);
				}
			}
	
			switch (format) {
				case "XML":
					File resXml = Funcs.createTempFile("REP_", ".xml", Constants.DOCS_DIRECTORY);
					JasperExportManager.exportReportToXmlFile(jprint1, resXml.getAbsolutePath(), true);
					tempTemplateFiles.add(resXml);
					byte[] byteXml = Funcs.read(resXml);
					return byteXml;
				case "HTML":
					File resHtml = Funcs.createTempFile("REP_", ".html", Constants.DOCS_DIRECTORY);
					JasperExportManager.exportReportToHtmlFile(jprint1, resHtml.getAbsolutePath());
					tempTemplateFiles.add(resHtml);
					byte[] byteHtml = Funcs.read(resHtml);
					return byteHtml;
				default:
					return JasperExportManager.exportReportToPdf(jprint1);
			}
        } finally {
        	for (File f : templateFiles) {
        		try {
        			f.delete();
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
        	}
        }
	}

	public static void main(String[] args) throws Exception {
/*		String templateDir = "D:\\tmp\\jasper";
		List<String> templateFileNames = new ArrayList<>();
		templateFileNames.add("Coffee_1.jasper");
		
        InputStream is = new FileInputStream(new File("D:\\tmp\\jasper\\persons.xml"));
        SAXBuilder builder = new SAXBuilder();
        Element xml = builder.build(is).getRootElement();

		byte[] b = new BlackBoxPlugin().generateReport(xml, templateDir, templateFileNames);
		FileOutputStream fos = new FileOutputStream(templateDir + "\\report.pdf");
		fos.write(b);
		fos.close();
*/		
		String reportName = "1 АКТ сдачи на хранение межевых знаков";
		
		String templateDir = "C:/Users/User/JaspersoftWorkspace/MyReports";
		List<String> templateFileNames = new ArrayList<>();
		templateFileNames.add(reportName + ".jasper");
		
        InputStream is = new FileInputStream(new File("C:/Users/User/JaspersoftWorkspace/data/1 zadacha.xml"));
        SAXBuilder builder = new SAXBuilder();
        Element xml = builder.build(is).getRootElement();

		//byte[] b = new BlackBoxPlugin().generateReport(xml, templateDir, templateFileNames);
        Map<String, Object> params = new HashMap<>();
        params.put("tableReport1", new File(templateDir + "/1 table.jasper"));
        
		byte[] b = new SystemOp(null).generateReport(xml, new File(templateDir + "/" + reportName + ".jasper"), params, "PDF");

		FileOutputStream fos = new FileOutputStream(templateDir + "\\" + reportName + ".pdf");
		fos.write(b);
		fos.close();
		
	}
}
