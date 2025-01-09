import static kz.tamur.or3ee.common.UserSession.SERVER_ID;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.QueryResult;
import com.cifs.or2.server.ServerUserSession;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.UserSrv;
import com.cifs.or2.server.db.Database;

import kz.tamur.ods.Driver;
import kz.tamur.ods.Getter;
import kz.tamur.ods.Value;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.AttrRequestBuilder;
import kz.tamur.server.plugins.OrdersPlugin;
import kz.tamur.server.plugins.OrdersPlugin.Order;

public class Test {

	private static Log log = LogFactory.getLog(Test.class);
	
	public static void main(String[] args) throws Exception {
		String op = (args.length > 0) ? args[0] : null;
		
		if ("testAttrRequest".equals(op)) {
			testAttrRequest(args);
		} else if (args.length > 5) {
			// адрес базы данных с блобами
			String urlConnection = args[0];
			// имя пользователя БД
			String login = args[1];
			// пароль БД
			String password = args[2];

			String urlConnectionExt = args[3];
			// имя пользователя БДs
			String loginExt = args[4];
			// пароль БД
			String passwordExt = args[5];

			importDB(urlConnection, login, password, urlConnectionExt, loginExt, passwordExt);
		} else if (args.length > 3) {
			// адрес базы данных с блобами
			String urlConnection = args[0];
			// имя пользователя БД
			String login = args[1];
			// пароль БД
			String password = args[2];
			
			String separator = args[3];
			
			exportDB(urlConnection, login, password, separator);
		}
	}
	
	public static void exportDB(String urlConnection, String login, String password, String separator) throws Exception {
				
		Database db = new Database("name", "fc_sys", "mysql", null, urlConnection, login, password, null, null, null, "repl", false, null, null);
		
		Driver drv = db.getDriver(new ServerUserSession("fc_sys", "client", new UserSrv("sys"), "192.168.13.107", "ERIK", SERVER_ID, false));
		
		Getter getter = new Getter(drv, "databaseExport");
        	
		getter.getChanges(separator);
        log.info("dbExport completed.");
	}
	
	public static void importDB(String urlConnection, String login, String password,
			String urlConnectionExt, String loginExt, String passwordExt) throws Exception {
		
		Database db = new Database("name", "fin_center", "pgsql", null, urlConnection, login, password, urlConnectionExt, loginExt, passwordExt, "repl", false, null, null);
		
        log.info("222 dbImport completed.");
        
	}
	
	public static void testAttrRequest(String[] args) throws Exception {
		
		String dsName = "name";
		String scheme = "e_kyz_resource";
		String dbType = "mysql";
		String typeClient = "designer";
		String user = "sys";
		
		String urlConnection = "jdbc:mysql://192.168.13.102:3306/e_kyz_resource";
		String loginBD = "root";
		String passwordBD = "mysql";
		
		Database db = new Database(dsName, scheme, dbType, null, urlConnection, loginBD, passwordBD, null, null, null, "repl", false, null, null);
		
		UserSession us = new ServerUserSession(dsName, typeClient, new UserSrv(user), "192.168.13.107", "ERIK-PC", SERVER_ID, true);
		Session s = new Session();
		s.login(us, true, db);
		
		
		OrdersPlugin plug = new OrdersPlugin();
		plug.setSession(s);
		
		plug.initListener(s);
		AttrRequestBuilder arb = plug.getRequestForOrder(s);
		
		long[] orderIds = new long[] {29518264};
		
        QueryResult qr = s.getObjects(orderIds, arb.build(), 0);

        for (Object[] prow : qr.rows) {
        	log.info("row: " + prow[0]);

        	KrnObject statusObj = arb.getObjectValue(OrdersPlugin.attrStatus.name, prow);
        	KrnObject personObj = arb.getObjectValue(OrdersPlugin.attrRespPerson.name, prow);
        	KrnObject userObj = arb.getObjectValue(OrdersPlugin.attrRespUser.name, prow);
        	KrnObject depObj = arb.getObjectValue(OrdersPlugin.attrRespDep.name, prow);
        	List<Value> roleVals = (List<Value>)arb.getValue(OrdersPlugin.attrRespRole.name, prow);
        	List<KrnObject> roleObjs = null; 
        	if (roleVals != null && roleVals.size() > 0) {
        		roleObjs = new ArrayList<KrnObject>();
        		for (Value val : roleVals)
        			if (val != null && val.value instanceof KrnObject)
        				roleObjs.add((KrnObject)val.value);
        	}
        	KrnObject balObj = arb.getObjectValue(OrdersPlugin.attrBalansEd.name, prow);
        	KrnObject authorObj = arb.getObjectValue(OrdersPlugin.attrAuthor.name, prow);
        	KrnObject parentObj = arb.getObjectValue(OrdersPlugin.attrParent.name, prow);
        	KrnObject parentPersonObj = arb.getObjectValue(OrdersPlugin.attrParent.name + "." + OrdersPlugin.attrRespPerson.name, prow);
        	KrnObject typeObj = arb.getObjectValue(OrdersPlugin.attrOrderType.name, prow);
        	KrnObject killerObj = arb.getObjectValue(OrdersPlugin.attrCanKill.name + "." + OrdersPlugin.attrUserPerson.name, prow);
        	boolean show = arb.getBooleanValue(OrdersPlugin.attrShow.name, prow, false);
        	boolean reassigned = arb.getBooleanValue(OrdersPlugin.attrReassigned.name, prow, false);
        	KrnObject docObj = arb.getObjectValue(OrdersPlugin.attrOrderDoc.name, prow);
        	KrnObject zaprosDocObj = arb.getObjectValue(OrdersPlugin.attrOrderDoc.name + "." + OrdersPlugin.attrDocZaprosDoc.name, prow);
        	KrnObject erkkObj = arb.getObjectValue(OrdersPlugin.attrOrderDoc.name + "." + OrdersPlugin.attrDocAderkk.name, prow);
        	KrnObject erkkObj2 = arb.getObjectValue(OrdersPlugin.attrOrderDoc.name + "." + OrdersPlugin.attrDocZaprosDoc.name + "." + OrdersPlugin.attrDocAderkk.name, prow);

        	List<Value> children_ = (List<Value>) arb.getValue(OrdersPlugin.attrChildren.name, prow);
        	log.info("children_: " + children_);
        	log.info("parentObj: " + parentObj);
        	log.info("parentPersonObj: " + parentPersonObj);

        	List<KrnObject> childObjs = null; 
        	if (children_ != null && children_.size() > 0) {
        		childObjs = new ArrayList<KrnObject>();
        		for (Value val : children_)
        			if (val != null && val.value instanceof KrnObject)
        				childObjs.add((KrnObject)val.value);
        		
        		long childIds[] = new long[childObjs.size()];
        		
        		for (int i=0; i<childObjs.size(); i++) {
        			childIds[i] = childObjs.get(i).id;
	        	}
	
	            AttrRequestBuilder arb2 = new AttrRequestBuilder(OrdersPlugin.clsOrder, s)
						.add(OrdersPlugin.attrAuthor.name);

	            QueryResult qr2 = s.getObjects(childIds, arb2.build(), 0);
        		
	            for (Object[] prow2 : qr2.rows) {
	            	log.info("child: " + prow2[0]);

	            	KrnObject authorObj2 = arb2.getObjectValue(OrdersPlugin.attrAuthor.name, prow2);
	            	log.info("authorObj2: " + authorObj2);
	            }
        	}

        }
        s.release();
		
        log.info("testAttrRequest completed.");
        
	}

}
