package kz.tamur.or3.server.plugins.kfm.tofi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

public class TofiPlugin implements SrvPlugin {
	
	private Session session;

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	private Connection conn;
	
	private static final java.sql.Date DATE_NULL;
	private static final java.sql.Date DATE_INF;
	
	static {
		Calendar c = Calendar.getInstance();
		c.set(1900, Calendar.JANUARY, 1);
		DATE_NULL = sqlDate(c.getTime());
		c.set(3333, Calendar.DECEMBER, 31);
		DATE_INF = sqlDate(c.getTime());
	}
	
	public TofiPlugin() throws SQLException {
	}
	
	public void createObjects(List<KrnObject> objs) throws Exception {
		try {
			conn = DriverManager.getConnection("jdbc:apache:commons:dbcp:/tofi");
			conn.setAutoCommit(false);
			//KrnClass subjCls = session.getClassByName("фм::осн::Субъект");
			//KrnClass svedCls = session.getClassByName("фм::осн::СведСубъекта");
			//KrnAttribute nameAttr = 
			for (KrnObject obj : objs) {
				String name = (String)obj.getAttr("фм::осн::Субъект.сведСубъекта.наименование");
				String rnn = (String)obj.getAttr("фм::осн::Субъект.рнн");
				String okpo = (String)obj.getAttr("фм::осн::Субъект.сведСубъекта.окпо");
				Long ctyId = (Long)obj.getAttr("фм::осн::Субъект.сведСубъекта.адресРегистрации.страна.кодТофи");
				long tofiId = createObject(name, rnn, okpo, ctyId);
				obj.setAttr("фм::осн::Субъект.кодТофи", tofiId);
			}
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public void createSubject() throws SQLException {
		
	}

	public long createObject(String name, String rnn, String okpo, Long countryId) throws SQLException {
		QueryRunner q = new QueryRunner();
		
		long objId = getNextId("Obj");
		q.update(conn,
				"INSERT INTO Obj (ID,CLASS,NAME,FULLNAME,DBEG,DEND) VALUES (?,?,?,?,?,?)",
				new Object[] {objId, 30, name, name, DATE_NULL, DATE_INF});
		
		//long periodId = createPeriod(DATE_NULL, DATE_INF, "Постоянно", 0);
		long periodId = 21;

		// Код РНН
		if (rnn != null) {
			long id = getNextId("dataattribtyp");
			q.update(conn,
					"INSERT INTO dataattribtyp (ID,OWN,ISCLASS,CHARGRATTRIBTYP,GRATTRIBITEM,PERIOD,VALUE) VALUES (?,?,?,?,?,?,?)",
					new Object[] {id, objId, 0, 14, 21, periodId, rnn});
		}

		// Код ОКПО
		if (okpo != null) {
			long id = getNextId("dataattribtyp");
			q.update(conn,
					"INSERT INTO dataattribtyp (ID,OWN,ISCLASS,CHARGRATTRIBTYP,GRATTRIBITEM,PERIOD,VALUE) VALUES (?,?,?,?,?,?,?)",
					new Object[] {id, objId, 0, 14, 4, periodId, okpo});
		}
		
		// Резидентство
		if (countryId != null) {
			long id = getNextId("datafactortyp");
			q.update(conn,
					"INSERT INTO datafactortyp (ID,OWN,ISCLASS,CHARGRFACTORTYP,GRFACTORITEM,DBEG,DEND,VALUE) VALUES (?,?,?,?,?,?,?,?)",
					new Object[] {id, objId, 0, 21, 1, DATE_NULL, DATE_INF, countryId});
		}
		
		return objId;
	}
	
	public long createPeriod(Date beg, Date end, String name, long kind) throws SQLException {
		QueryRunner q = new QueryRunner();
		List<Period> ps = (List<Period>)q.query(
				conn,
				"SELECT * FROM Period WHERE DBEG=? AND DEND=?",
				new Object[]{sqlDate(beg), sqlDate(end)},
				Period.builder);
		if (ps.size() > 0) {
			return ps.get(0).id;
		} else {
			long id = getNextId("Period");
			q.update(conn,
					"INSERT INTO Period (ID,NAME,FULLNAME,DBEG,DEND,KIND) VALUES (?,?,?,?,?,?)",
					new Object[] {id, name, name, sqlDate(beg), sqlDate(end), kind});
			return id;
		}
	}
	
	private long getNextId(String tbName) throws SQLException {
		long res = 0;
		Statement st = conn.createStatement();
		ResultSet rs = st.executeQuery("SELECT g_" + tbName + ".Nextval FROM Dual");
		if (rs.next()) {
			res = rs.getLong(1);
		}
		rs.close();
		st.close();
		return res;
	}

	private static java.sql.Date sqlDate(Date date) {
		return new java.sql.Date(date.getTime());
	}

	private static int kind(Date beg, Date end) {
		//TODO Реализовать.
		return 0;
	}

	public static void main(String[] args)  throws Exception {
		TofiPlugin plugin = null;
		plugin = new TofiPlugin();
		plugin.createObject("ТОО \"Рога и копыта 2\"", "11111111", "11111111", Long.valueOf(2));
	}

}

class Period {
	
	public final long id;
	public final Date beg;
	public final Date end;
	public final long kind;
	
	public static Builder builder = new Builder();
	
	private Period(long id, Date beg, Date end, long kind) {
		this.id = id;
		this.beg = beg;
		this.end = end;
		this.kind = kind;
	}
	
	public static class Builder implements ResultSetHandler {
		
		private Builder() {
		}
		
		public Object handle(ResultSet rs) throws SQLException {
			List<Period> res = new ArrayList<Period>();
			while (rs.next()) {
				long id = rs.getLong("ID");
				Date beg = rs.getDate("DBEG");
				Date end = rs.getDate("DEND");
				long kind = rs.getLong("KIND");
				res.add(new Period(id, beg, end, kind));
			}
			return res;
		}
	}
}