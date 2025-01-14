package kz.tamur.ods.postgre;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;

import com.cifs.or2.server.ServerUserSession;
import com.cifs.or2.server.UserSrv;
import com.cifs.or2.server.db.Database;

import kz.tamur.ods.Driver;
import kz.tamur.or3ee.common.UserSession;

@TestMethodOrder(OrderAnnotation.class)
public class PgSqlDriverTest extends Mockito {
	
	private static String profile;
	private static Database db;
	private static UserSession us;
	private static final String DB_NAME = "testdb";
	
	private static Connection mgmtConn;
	
	@BeforeAll
	static void setup() throws Exception {
		profile = System.getProperty("profileId");
		
		DataSource ds = mock(DataSource.class);
		if ("pgsql".equals(profile)) {
			Class.forName("org.postgresql.Driver");
			mgmtConn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
			when(ds.getConnection()).thenAnswer(
					(inv) -> DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb", "postgres", "postgres"));

		} else if ("mysql".equals(profile)) {
			Class.forName("com.mysql.cj.jdbc.Driver");
			mgmtConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sys", "root", "root");
			when(ds.getConnection()).thenAnswer(
					(inv) -> DriverManager.getConnection("jdbc:mysql://localhost:3306/testdb", "root", "root"));
		}
		
		createTestDB(mgmtConn);
		
		db = new Database(DB_NAME, "public", profile, ds, "C:/wildfly20/replication", false, null, null);
		us = new ServerUserSession(DB_NAME, "server", new UserSrv("sys"), null, "WIN-P8T70NV00PI", UserSession.SERVER_ID, false);
	}
	
	static void createTestDB(Connection conn) throws Exception {
		try (Statement st = conn.createStatement()) {
			st.executeUpdate("drop database if exists " + DB_NAME);
			st.executeUpdate("create database " + DB_NAME);
		}
	}
	
	@Test
	@Order(1)
	public void initDBTest() throws Exception {
		Driver driver = db.makeObject(us);
		db.releaseDriver(driver);
	}
	
	@Test
	@Order(2)
	public void kernelClassesTest() throws Exception {
		
		Driver driver = db.makeObject(us);
		
		assertNotNull(db.getClassByName("Объект"));
		assertNotNull(db.getClassByName("Системный класс"));
		assertNotNull(db.getClassByName("Пользовательский класс"));
		assertNotNull(db.getClassByName("string"));
		assertNotNull(db.getClassByName("long"));
		assertNotNull(db.getClassByName("time"));
		assertNotNull(db.getClassByName("date"));
		assertNotNull(db.getClassByName("boolean"));
		assertNotNull(db.getClassByName("memo"));
		assertNotNull(db.getClassByName("float"));
		assertNotNull(db.getClassByName("blob"));
		assertNotNull(db.getClassByName("bfile"));
		assertNotNull(db.getClassByName("Структура баз"));
		assertNotNull(db.getClassByName("Language"));
		assertNotNull(db.getClassByName("EventInitiator"));
		assertNotNull(db.getClassByName("EventType"));
		assertNotNull(db.getClassByName("EventSpr"));
		assertNotNull(db.getClassByName("Filter"));
		assertNotNull(db.getClassByName("FilterFolder"));
		assertNotNull(db.getClassByName("FilterRoot"));
		assertNotNull(db.getClassByName("GuiComponent"));
		assertNotNull(db.getClassByName("UI"));
		assertNotNull(db.getClassByName("HiperTree"));
		assertNotNull(db.getClassByName("HiperFolder"));
		assertNotNull(db.getClassByName("MainTree"));
		assertNotNull(db.getClassByName("ReportPrinter"));
		assertNotNull(db.getClassByName("ReportFolder"));
		assertNotNull(db.getClassByName("ReportRoot"));
		assertNotNull(db.getClassByName("ImpExp"));
		assertNotNull(db.getClassByName("Import"));
		assertNotNull(db.getClassByName("Export"));
		assertNotNull(db.getClassByName("UIFolder"));
		assertNotNull(db.getClassByName("Property"));
		assertNotNull(db.getClassByName("ConfigObject"));
		assertNotNull(db.getClassByName("Config"));
		assertNotNull(db.getClassByName("ConfigGlobal"));
		assertNotNull(db.getClassByName("ConfigLocal"));
		assertNotNull(db.getClassByName("UIRoot"));
		assertNotNull(db.getClassByName("User"));
		assertNotNull(db.getClassByName("UserFolder"));
		assertNotNull(db.getClassByName("UserRoot"));
		assertNotNull(db.getClassByName("Политика учетных записей"));
		assertNotNull(db.getClassByName("Timer"));
		assertNotNull(db.getClassByName("TimerFolder"));
		assertNotNull(db.getClassByName("TimerRoot"));
		assertNotNull(db.getClassByName("TimerProtocol"));
		assertNotNull(db.getClassByName("Note"));
		assertNotNull(db.getClassByName("NoteFolder"));
		assertNotNull(db.getClassByName("NoteRoot"));
		assertNotNull(db.getClassByName("WorkFlow"));
		assertNotNull(db.getClassByName("BoxExchange"));
		assertNotNull(db.getClassByName("BoxFolder"));
		assertNotNull(db.getClassByName("BoxRoot"));
		assertNotNull(db.getClassByName("Flow"));
		assertNotNull(db.getClassByName("Process"));
		assertNotNull(db.getClassByName("ProcessDef"));
		assertNotNull(db.getClassByName("ProcessDefFolder"));
		assertNotNull(db.getClassByName("ProcessDefRoot"));
		assertNotNull(db.getClassByName("Mail@Message"));
		assertNotNull(db.getClassByName("Mail@Part"));
		assertNotNull(db.getClassByName("База"));
		assertNotNull(db.getClassByName("Корень структуры баз"));
		assertNotNull(db.getClassByName("MSDoc"));
		assertNotNull(db.getClassByName("ReplCollection"));
		assertNotNull(db.getClassByName("Зап табл репликации"));
		assertNotNull(db.getClassByName("MenuItemsDesc"));
		assertNotNull(db.getClassByName("Тип сообщения"));
		assertNotNull(db.getClassByName("OrLang"));
		assertNotNull(db.getClassByName("Func"));
		assertNotNull(db.getClassByName("FuncFolder"));
		assertNotNull(db.getClassByName("CreateXmlRoot"));
		assertNotNull(db.getClassByName("DefaultXmlRoot"));
		assertNotNull(db.getClassByName("ParseXmlRoot"));
		assertNotNull(db.getClassByName("ChatClass"));
		assertNotNull(db.getClassByName("Action"));
		assertNotNull(db.getClassByName("ControlFolder"));
		assertNotNull(db.getClassByName("ControlFolderRoot"));
		assertNotNull(db.getClassByName("SystemAction"));
		assertNotNull(db.getClassByName("SystemRight"));
		assertNotNull(db.getClassByName("ProtocolRule"));
		assertNotNull(db.getClassByName("SystemEvent"));
		assertNotNull(db.getClassByName("FGACRule"));
		assertNotNull(db.getClassByName("FGARule"));
		assertNotNull(db.getClassByName("FSDirectory"));
		assertNotNull(db.getClassByName("Recycle"));
		assertNotNull(db.getClassByName("FilterRecycle"));
		assertNotNull(db.getClassByName("ProcessDefRecycle"));
		assertNotNull(db.getClassByName("ReportPrinterRecycle"));
		assertNotNull(db.getClassByName("UIRecycle"));
		assertNotNull(db.getClassByName("ProcessDefUsingHistory"));
		assertNotNull(db.getClassByName("Notification"));
		
		db.releaseDriver(driver);

	}
	
	@Test
	@Order(3)
	public void kernelAttributesTest() throws Exception {
		
		Driver driver = db.makeObject(us);
		
		assertNotNull(db.getAttributeByName(99, "creating"));
		assertNotNull(db.getAttributeByName(99, "deleting"));
		assertNotNull(db.getAttributeByName(147, "code"));
		assertNotNull(db.getAttributeByName(147, "system?"));
		assertNotNull(db.getAttributeByName(147, "lang?"));
		assertNotNull(db.getAttributeByName(147, "name"));
		assertNotNull(db.getAttributeByName(104, "kod"));
		assertNotNull(db.getAttributeByName(104, "name"));
		assertNotNull(db.getAttributeByName(105, "code"));
		assertNotNull(db.getAttributeByName(105, "type"));
		assertNotNull(db.getAttributeByName(105, "type_M"));
		assertNotNull(db.getAttributeByName(106, "description"));
		assertNotNull(db.getAttributeByName(106, "description_M"));
		assertNotNull(db.getAttributeByName(106, "initiator"));
		assertNotNull(db.getAttributeByName(106, "kod"));
		assertNotNull(db.getAttributeByName(106, "type"));
		assertNotNull(db.getAttributeByName(107, "className"));
		assertNotNull(db.getAttributeByName(107, "dateSelect"));
		assertNotNull(db.getAttributeByName(107, "exprSql"));
		assertNotNull(db.getAttributeByName(107, "title"));
		assertNotNull(db.getAttributeByName(107, "config"));
		assertNotNull(db.getAttributeByName(108, "parent"));
		assertNotNull(db.getAttributeByName(108, "children"));
		assertNotNull(db.getAttributeByName(110, "constraints"));
		assertNotNull(db.getAttributeByName(110, "descInfo"));
		assertNotNull(db.getAttributeByName(110, "flags"));
		assertNotNull(db.getAttributeByName(110, "ref"));
		assertNotNull(db.getAttributeByName(110, "title"));
		assertNotNull(db.getAttributeByName(120, "config"));
		assertNotNull(db.getAttributeByName(120, "strings"));
		assertNotNull(db.getAttributeByName(120, "title"));
		assertNotNull(db.getAttributeByName(120, "webConfigChanged"));
		assertNotNull(db.getAttributeByName(120, "webConfig"));
		assertNotNull(db.getAttributeByName(111, "access"));
		assertNotNull(db.getAttributeByName(111, "hiperObj"));
		assertNotNull(db.getAttributeByName(111, "isChangeable"));
		assertNotNull(db.getAttributeByName(111, "isDialog"));
		assertNotNull(db.getAttributeByName(111, "isHeader"));
		assertNotNull(db.getAttributeByName(111, "runtimeIndex"));
		assertNotNull(db.getAttributeByName(111, "parent"));
		assertNotNull(db.getAttributeByName(112, "hipers"));
		assertNotNull(db.getAttributeByName(114, "data"));
		assertNotNull(db.getAttributeByName(114, "data2"));
		assertNotNull(db.getAttributeByName(114, "template"));
		assertNotNull(db.getAttributeByName(114, "template2"));
		assertNotNull(db.getAttributeByName(114, "базовый отчет"));
		assertNotNull(db.getAttributeByName(114, "config"));
		assertNotNull(db.getAttributeByName(114, "bases"));
		assertNotNull(db.getAttributeByName(114, "parent"));
		assertNotNull(db.getAttributeByName(115, "children"));
		assertNotNull(db.getAttributeByName(117, "change_id"));
		assertNotNull(db.getAttributeByName(117, "clschange_id"));
		assertNotNull(db.getAttributeByName(117, "date"));
		assertNotNull(db.getAttributeByName(117, "exp_id"));
		assertNotNull(db.getAttributeByName(117, "file_name"));
		assertNotNull(db.getAttributeByName(117, "prior_change_id"));
		assertNotNull(db.getAttributeByName(117, "prior_clschange_id"));
		assertNotNull(db.getAttributeByName(117, "rised"));
		assertNotNull(db.getAttributeByName(117, "scriptOnAfterAction"));
		assertNotNull(db.getAttributeByName(117, "scriptOnBeforeAction"));
		assertNotNull(db.getAttributeByName(118, "exp_date"));
		assertNotNull(db.getAttributeByName(118, "importFinish"));
		assertNotNull(db.getAttributeByName(118, "importStart"));
		assertNotNull(db.getAttributeByName(119, "change_count"));
		assertNotNull(db.getAttributeByName(119, "clschange_count"));
		assertNotNull(db.getAttributeByName(120, "parent"));
		assertNotNull(db.getAttributeByName(121, "children"));
		assertNotNull(db.getAttributeByName(163, "name"));
		assertNotNull(db.getAttributeByName(163, "value"));
		assertNotNull(db.getAttributeByName(162, "uuid"));
		assertNotNull(db.getAttributeByName(162, "properties"));
		assertNotNull(db.getAttributeByName(160, "colorBackTabTitle"));
		assertNotNull(db.getAttributeByName(160, "colorFontBackTabTitle"));
		assertNotNull(db.getAttributeByName(160, "colorFontTabTitle"));
		assertNotNull(db.getAttributeByName(160, "colorHeaderTable"));
		assertNotNull(db.getAttributeByName(160, "colorMain"));
		assertNotNull(db.getAttributeByName(160, "colorTabTitle"));
		assertNotNull(db.getAttributeByName(160, "gradientControlPanel"));
		assertNotNull(db.getAttributeByName(160, "gradientFieldNOFLC"));
		assertNotNull(db.getAttributeByName(160, "gradientMainFrame"));
		assertNotNull(db.getAttributeByName(160, "gradientMenuPanel"));
		assertNotNull(db.getAttributeByName(160, "transparentBackTabTitle"));
		assertNotNull(db.getAttributeByName(160, "transparentCellTable"));
		assertNotNull(db.getAttributeByName(160, "transparentMain"));
		assertNotNull(db.getAttributeByName(160, "transparentDialog"));
		assertNotNull(db.getAttributeByName(160, "transparentSelectedTabTitle"));
		assertNotNull(db.getAttributeByName(160, "blueSysColor"));
		assertNotNull(db.getAttributeByName(160, "darkShadowSysColor"));
		assertNotNull(db.getAttributeByName(160, "midSysColor"));
		assertNotNull(db.getAttributeByName(160, "lightYellowColor"));
		assertNotNull(db.getAttributeByName(160, "redColor"));
		assertNotNull(db.getAttributeByName(160, "lightRedColor"));
		assertNotNull(db.getAttributeByName(160, "lightGreenColor"));
		assertNotNull(db.getAttributeByName(160, "shadowYellowColor"));
		assertNotNull(db.getAttributeByName(160, "sysColor"));
		assertNotNull(db.getAttributeByName(160, "lightSysColor"));
		assertNotNull(db.getAttributeByName(160, "defaultFontColor"));
		assertNotNull(db.getAttributeByName(160, "silverColor"));
		assertNotNull(db.getAttributeByName(160, "shadowsGreyColor"));
		assertNotNull(db.getAttributeByName(160, "keywordColor"));
		assertNotNull(db.getAttributeByName(160, "variableColor"));
		assertNotNull(db.getAttributeByName(160, "clientVariableColor"));
		assertNotNull(db.getAttributeByName(160, "commentColor"));
		assertNotNull(db.getAttributeByName(160, "objectBrowserLimit"));
		assertNotNull(db.getAttributeByName(160, "objectBrowserLimitForClasses"));
		assertNotNull(db.getAttributeByName(160, "isObjectBrowserLimit"));
		assertNotNull(db.getAttributeByName(160, "isObjectBrowserLimitForClasses"));
		assertNotNull(db.getAttributeByName(161, "maxObjectCount"));
		assertNotNull(db.getAttributeByName(161, "isToolBar"));
		assertNotNull(db.getAttributeByName(161, "isMonitor"));
		assertNotNull(db.getAttributeByName(161, "configByUUIDs"));
		assertNotNull(db.getAttributeByName(161, "historyIfc"));
		assertNotNull(db.getAttributeByName(161, "historyFlt"));
		assertNotNull(db.getAttributeByName(161, "historyRpt"));
		assertNotNull(db.getAttributeByName(111, "users"));
		assertNotNull(db.getAttributeByName(123, "admin"));
		assertNotNull(db.getAttributeByName(120, "developer"));
		assertNotNull(db.getAttributeByName(107, "developer"));
		assertNotNull(db.getAttributeByName(123, "blocked"));
		assertNotNull(db.getAttributeByName(123, "developer"));
		assertNotNull(db.getAttributeByName(123, "multi"));
		assertNotNull(db.getAttributeByName(123, "editor"));
		assertNotNull(db.getAttributeByName(123, "doljnost"));
		assertNotNull(db.getAttributeByName(123, "name"));
		assertNotNull(db.getAttributeByName(123, "password"));
		assertNotNull(db.getAttributeByName(123, "sign"));
		assertNotNull(db.getAttributeByName(123, "email"));
		assertNotNull(db.getAttributeByName(123, "iin"));
		assertNotNull(db.getAttributeByName(123, "onlyECP"));
		assertNotNull(db.getAttributeByName(123, "previous passwords"));
		assertNotNull(db.getAttributeByName(123, "кол неуд авторизаций"));
		assertNotNull(db.getAttributeByName(123, "дата изменения пароля"));
		assertNotNull(db.getAttributeByName(123, "время блокировки"));
		assertNotNull(db.getAttributeByName(123, "дата истечения срока действия пароля"));
		assertNotNull(db.getAttributeByName(123, "isFolder"));
		assertNotNull(db.getAttributeByName(123, "isLogged"));
		assertNotNull(db.getAttributeByName(123, "hyperMenu"));
		assertNotNull(db.getAttributeByName(123, "interface"));
		assertNotNull(db.getAttributeByName(123, "lastLoginTime"));
		assertNotNull(db.getAttributeByName(123, "activated"));
		assertNotNull(db.getAttributeByName(123, "quickList"));
		assertNotNull(db.getAttributeByName(123, "config"));
		assertNotNull(db.getAttributeByName(123, "favoritesClasses"));
		assertNotNull(db.getAttributeByName(123, "lastIndexingConfig"));
		assertNotNull(db.getAttributeByName(123, "templates"));
		assertNotNull(db.getAttributeByName(123, "parent"));
		assertNotNull(db.getAttributeByName(124, "children"));
		assertNotNull(db.getAttributeByName(124, "or3rights"));
		assertNotNull(db.getAttributeByName(126, "рекомен срок действия пароля"));
		assertNotNull(db.getAttributeByName(126, "мин длина логина"));
		assertNotNull(db.getAttributeByName(126, "мин длина пароля"));
		assertNotNull(db.getAttributeByName(126, "мин длина пароля адм"));
		assertNotNull(db.getAttributeByName(126, "кол не дублир паролей"));
		assertNotNull(db.getAttributeByName(126, "кол не дублир паролей адм"));
		assertNotNull(db.getAttributeByName(126, "использовать цифры"));
		assertNotNull(db.getAttributeByName(126, "использовать буквы"));
		assertNotNull(db.getAttributeByName(126, "использовать регистр"));
		assertNotNull(db.getAttributeByName(126, "использовать спец символы"));
		assertNotNull(db.getAttributeByName(126, "запрет имён"));
		assertNotNull(db.getAttributeByName(126, "запрет фамилий"));
		assertNotNull(db.getAttributeByName(126, "запрет телефонов"));
		assertNotNull(db.getAttributeByName(126, "запрет слов"));
		assertNotNull(db.getAttributeByName(126, "макс срок действия пароля"));
		assertNotNull(db.getAttributeByName(126, "мин срок действия пароля"));
		assertNotNull(db.getAttributeByName(126, "кол неуд авторизаций"));
		assertNotNull(db.getAttributeByName(126, "время блокировки"));
		assertNotNull(db.getAttributeByName(126, "блокировать логин в пароле"));
		assertNotNull(db.getAttributeByName(126, "макс длина пароля"));
		assertNotNull(db.getAttributeByName(126, "макс длина логина"));
		assertNotNull(db.getAttributeByName(126, "смена 1го пароля"));
		assertNotNull(db.getAttributeByName(126, "макс срок 1го пароля"));
		assertNotNull(db.getAttributeByName(126, "запрет повтора 1х 3х букв пароля"));
		assertNotNull(db.getAttributeByName(126, "не должно явно преобладать цифры"));
		assertNotNull(db.getAttributeByName(126, "запрет повтора в любом месте из более 2-х одинаковых символов пароля"));
		assertNotNull(db.getAttributeByName(126, "запрет слов на клавиатуре"));
		assertNotNull(db.getAttributeByName(127, "config"));
		assertNotNull(db.getAttributeByName(127, "protocol"));
		assertNotNull(db.getAttributeByName(127, "redy"));
		assertNotNull(db.getAttributeByName(127, "start"));
		assertNotNull(db.getAttributeByName(127, "title"));
		assertNotNull(db.getAttributeByName(127, "user"));
		assertNotNull(db.getAttributeByName(127, "parent"));
		assertNotNull(db.getAttributeByName(128, "children"));
		assertNotNull(db.getAttributeByName(170, "err"));
		assertNotNull(db.getAttributeByName(170, "status"));
		assertNotNull(db.getAttributeByName(170, "timeNextStart"));
		assertNotNull(db.getAttributeByName(170, "timeStart"));
		assertNotNull(db.getAttributeByName(170, "timer"));
		assertNotNull(db.getAttributeByName(123, "help"));
		assertNotNull(db.getAttributeByName(123, "helps"));
		assertNotNull(db.getAttributeByName(130, "content"));
		assertNotNull(db.getAttributeByName(130, "title"));
		assertNotNull(db.getAttributeByName(130, "parent"));
		assertNotNull(db.getAttributeByName(131, "children"));
		assertNotNull(db.getAttributeByName(134, "charSet"));
		assertNotNull(db.getAttributeByName(134, "name"));
		assertNotNull(db.getAttributeByName(134, "urlIn"));
		assertNotNull(db.getAttributeByName(134, "urlOut"));
		assertNotNull(db.getAttributeByName(134, "xpathIn"));
		assertNotNull(db.getAttributeByName(134, "xpathOut"));
		assertNotNull(db.getAttributeByName(134, "xpathTypeIn"));
		assertNotNull(db.getAttributeByName(134, "xpathTypeOut"));
		assertNotNull(db.getAttributeByName(134, "xpathIdInit"));
		assertNotNull(db.getAttributeByName(134, "isRestrict"));
		assertNotNull(db.getAttributeByName(134, "transport"));
		assertNotNull(db.getAttributeByName(134, "typeMsg"));
		assertNotNull(db.getAttributeByName(134, "config"));
		assertNotNull(db.getAttributeByName(134, "parent"));
		assertNotNull(db.getAttributeByName(135, "children"));
		assertNotNull(db.getAttributeByName(137, "actor"));
		assertNotNull(db.getAttributeByName(137, "article"));
		assertNotNull(db.getAttributeByName(137, "article_lang"));
		assertNotNull(db.getAttributeByName(137, "box"));
		assertNotNull(db.getAttributeByName(137, "children"));
		assertNotNull(db.getAttributeByName(137, "control"));
		assertNotNull(db.getAttributeByName(137, "corelId"));
		assertNotNull(db.getAttributeByName(137, "current"));
		assertNotNull(db.getAttributeByName(137, "cutObj"));
		assertNotNull(db.getAttributeByName(137, "debug"));
		assertNotNull(db.getAttributeByName(137, "end"));
		assertNotNull(db.getAttributeByName(137, "event"));
		assertNotNull(db.getAttributeByName(137, "interfaceVars"));
		assertNotNull(db.getAttributeByName(137, "lockObjects"));
		assertNotNull(db.getAttributeByName(137, "name"));
		assertNotNull(db.getAttributeByName(137, "node"));
		assertNotNull(db.getAttributeByName(137, "parentFlow"));
		assertNotNull(db.getAttributeByName(137, "parentReactivate"));
		assertNotNull(db.getAttributeByName(137, "permit"));
		assertNotNull(db.getAttributeByName(137, "start"));
		assertNotNull(db.getAttributeByName(137, "status"));
		assertNotNull(db.getAttributeByName(137, "syncNode"));
		assertNotNull(db.getAttributeByName(137, "title"));
		assertNotNull(db.getAttributeByName(137, "titleObj"));
		assertNotNull(db.getAttributeByName(137, "transId"));
		assertNotNull(db.getAttributeByName(137, "transition"));
		assertNotNull(db.getAttributeByName(137, "typeNode"));
		assertNotNull(db.getAttributeByName(137, "typeUi"));
		assertNotNull(db.getAttributeByName(137, "ui"));
		assertNotNull(db.getAttributeByName(137, "user"));
		assertNotNull(db.getAttributeByName(137, "variables"));
		assertNotNull(db.getAttributeByName(138, "end"));
		assertNotNull(db.getAttributeByName(138, "initiator"));
		assertNotNull(db.getAttributeByName(138, "isProcess"));
		assertNotNull(db.getAttributeByName(138, "killer"));
		assertNotNull(db.getAttributeByName(138, "observers"));
		assertNotNull(db.getAttributeByName(137, "processInstance"));
		assertNotNull(db.getAttributeByName(138, "rootFlow"));
		assertNotNull(db.getAttributeByName(138, "start"));
		assertNotNull(db.getAttributeByName(138, "superFlow"));
		assertNotNull(db.getAttributeByName(138, "transId"));
		assertNotNull(db.getAttributeByName(138, "typeUiObservers"));
		assertNotNull(db.getAttributeByName(138, "uiObservers"));
		assertNotNull(db.getAttributeByName(138, "variables"));
		assertNotNull(db.getAttributeByName(123, "process"));
		assertNotNull(db.getAttributeByName(127, "process"));
		assertNotNull(db.getAttributeByName(138, "processDefinition"));
		assertNotNull(db.getAttributeByName(139, "config"));
		assertNotNull(db.getAttributeByName(139, "developer"));
		assertNotNull(db.getAttributeByName(139, "diagram"));
		assertNotNull(db.getAttributeByName(138, "isFolder"));
		assertNotNull(db.getAttributeByName(139, "message"));
		assertNotNull(db.getAttributeByName(139, "strings"));
		assertNotNull(db.getAttributeByName(139, "isBtnToolBar"));
		assertNotNull(db.getAttributeByName(139, "icon"));
		assertNotNull(db.getAttributeByName(139, "hotKey"));
		assertNotNull(db.getAttributeByName(161, "historySrv"));
		assertNotNull(db.getAttributeByName(139, "title"));
		assertNotNull(db.getAttributeByName(139, "parent"));
		assertNotNull(db.getAttributeByName(140, "children"));
		assertNotNull(db.getAttributeByName(140, "isTab"));
		assertNotNull(db.getAttributeByName(140, "tabName"));
		assertNotNull(db.getAttributeByName(142, "bcc"));
		assertNotNull(db.getAttributeByName(142, "cc"));
		assertNotNull(db.getAttributeByName(142, "from"));
		assertNotNull(db.getAttributeByName(142, "headers"));
		assertNotNull(db.getAttributeByName(142, "id"));
		assertNotNull(db.getAttributeByName(142, "receivedDate"));
		assertNotNull(db.getAttributeByName(142, "sentDate"));
		assertNotNull(db.getAttributeByName(142, "subject"));
		assertNotNull(db.getAttributeByName(142, "to"));
		assertNotNull(db.getAttributeByName(142, "parts"));
		assertNotNull(db.getAttributeByName(143, "charSet"));
		assertNotNull(db.getAttributeByName(143, "content"));
		assertNotNull(db.getAttributeByName(143, "description"));
		assertNotNull(db.getAttributeByName(143, "disposition"));
		assertNotNull(db.getAttributeByName(143, "fields"));
		assertNotNull(db.getAttributeByName(143, "fileName"));
		assertNotNull(db.getAttributeByName(143, "headers"));
		assertNotNull(db.getAttributeByName(143, "mimeType"));
		assertNotNull(db.getAttributeByName(143, "subType"));
		assertNotNull(db.getAttributeByName(144, "код"));
		assertNotNull(db.getAttributeByName(144, "наименование"));
		assertNotNull(db.getAttributeByName(118, "from_database"));
		assertNotNull(db.getAttributeByName(118, "база"));
		assertNotNull(db.getAttributeByName(119, "to_database"));
		assertNotNull(db.getAttributeByName(119, "база"));
		assertNotNull(db.getAttributeByName(123, "base"));
		assertNotNull(db.getAttributeByName(134, "base"));
		assertNotNull(db.getAttributeByName(145, "imports"));
		assertNotNull(db.getAttributeByName(145, "exports"));
		assertNotNull(db.getAttributeByName(145, "flags"));
		assertNotNull(db.getAttributeByName(145, "mail"));
		assertNotNull(db.getAttributeByName(145, "родитель"));
		assertNotNull(db.getAttributeByName(145, "дети"));
		assertNotNull(db.getAttributeByName(145, "значение"));
		assertNotNull(db.getAttributeByName(145, "код"));
		assertNotNull(db.getAttributeByName(145, "наименование"));
		assertNotNull(db.getAttributeByName(145, "уровень"));
		assertNotNull(db.getAttributeByName(145, "физически раздельная?"));
		assertNotNull(db.getAttributeByName(123, "data language"));
		assertNotNull(db.getAttributeByName(123, "interface language"));
		assertNotNull(db.getAttributeByName(148, "file"));
		assertNotNull(db.getAttributeByName(148, "filename"));
		assertNotNull(db.getAttributeByName(149, "date"));
		assertNotNull(db.getAttributeByName(149, "replicationID"));
		assertNotNull(db.getAttributeByName(149, "runMode"));
		assertNotNull(db.getAttributeByName(149, "type"));
		assertNotNull(db.getAttributeByName(117, "зап табл репликации"));
		assertNotNull(db.getAttributeByName(149, "зап табл репликации"));
		assertNotNull(db.getAttributeByName(150, "database"));
		assertNotNull(db.getAttributeByName(150, "date"));
		assertNotNull(db.getAttributeByName(150, "entity"));
		assertNotNull(db.getAttributeByName(150, "error message"));
		assertNotNull(db.getAttributeByName(150, "fileName"));
		assertNotNull(db.getAttributeByName(150, "logType"));
		assertNotNull(db.getAttributeByName(150, "replObject"));
		assertNotNull(db.getAttributeByName(150, "status"));
		assertNotNull(db.getAttributeByName(150, "uniqId"));
		assertNotNull(db.getAttributeByName(151, "itemDesc"));
		assertNotNull(db.getAttributeByName(151, "name"));
		assertNotNull(db.getAttributeByName(152, "код"));
		assertNotNull(db.getAttributeByName(152, "наименование"));
		assertNotNull(db.getAttributeByName(153, "funcs"));
		assertNotNull(db.getAttributeByName(153, "vars"));
		assertNotNull(db.getAttributeByName(154, "name"));
		assertNotNull(db.getAttributeByName(154, "strings"));
		assertNotNull(db.getAttributeByName(154, "text"));
		assertNotNull(db.getAttributeByName(155, "children"));
		assertNotNull(db.getAttributeByName(154, "parent"));
		assertNotNull(db.getAttributeByName(164, "from"));
		assertNotNull(db.getAttributeByName(164, "to"));
		assertNotNull(db.getAttributeByName(164, "canDeleteFrom"));
		assertNotNull(db.getAttributeByName(164, "canDeleteTo"));
		assertNotNull(db.getAttributeByName(164, "text"));
		assertNotNull(db.getAttributeByName(164, "datetime"));
		assertNotNull(db.getAttributeByName(165, "editingDate"));
		assertNotNull(db.getAttributeByName(165, "id"));
		assertNotNull(db.getAttributeByName(165, "log"));
		assertNotNull(db.getAttributeByName(165, "name"));
		assertNotNull(db.getAttributeByName(165, "type"));
		assertNotNull(db.getAttributeByName(165, "user"));
		assertNotNull(db.getAttributeByName(166, "title"));
		assertNotNull(db.getAttributeByName(166, "parent"));
		assertNotNull(db.getAttributeByName(166, "value"));
		assertNotNull(db.getAttributeByName(166, "children"));
		assertNotNull(db.getAttributeByName(166, "type"));
		assertNotNull(db.getAttributeByName(171, "code"));
		assertNotNull(db.getAttributeByName(171, "name"));
		assertNotNull(db.getAttributeByName(172, "action"));
		assertNotNull(db.getAttributeByName(172, "block"));
		assertNotNull(db.getAttributeByName(172, "deny"));
		assertNotNull(db.getAttributeByName(172, "description"));
		assertNotNull(db.getAttributeByName(172, "expr"));
		assertNotNull(db.getAttributeByName(172, "name"));
		assertNotNull(db.getAttributeByName(172, "userOrRole"));
		assertNotNull(db.getAttributeByName(172, "НСИ"));
		assertNotNull(db.getAttributeByName(172, "архив"));
		assertNotNull(db.getAttributeByName(172, "пользователь"));
		assertNotNull(db.getAttributeByName(172, "процесс"));
		assertNotNull(db.getAttributeByName(172, "роль"));
		assertNotNull(db.getAttributeByName(173, "block"));
		assertNotNull(db.getAttributeByName(173, "deny"));
		assertNotNull(db.getAttributeByName(173, "eventType"));
		assertNotNull(db.getAttributeByName(173, "expr"));
		assertNotNull(db.getAttributeByName(173, "name"));
		assertNotNull(db.getAttributeByName(173, "event"));
		assertNotNull(db.getAttributeByName(174, "code"));
		assertNotNull(db.getAttributeByName(174, "name"));
		assertNotNull(db.getAttributeByName(174, "type"));
		assertNotNull(db.getAttributeByName(175, "name"));
		assertNotNull(db.getAttributeByName(175, "атрибуты"));
		assertNotNull(db.getAttributeByName(175, "дополнительное условие"));
		assertNotNull(db.getAttributeByName(175, "заблокировано?"));
		assertNotNull(db.getAttributeByName(175, "класс"));
		assertNotNull(db.getAttributeByName(175, "операции"));
		assertNotNull(db.getAttributeByName(176, "name"));
		assertNotNull(db.getAttributeByName(176, "атрибуты"));
		assertNotNull(db.getAttributeByName(176, "дополнительное условие"));
		assertNotNull(db.getAttributeByName(176, "заблокировано?"));
		assertNotNull(db.getAttributeByName(176, "класс"));
		assertNotNull(db.getAttributeByName(176, "операции"));
		assertNotNull(db.getAttributeByName(177, "attrId"));
		assertNotNull(db.getAttributeByName(177, "name"));
		assertNotNull(db.getAttributeByName(177, "url"));
		assertNotNull(db.getAttributeByName(179, "className"));
		assertNotNull(db.getAttributeByName(179, "eventDate"));
		assertNotNull(db.getAttributeByName(179, "eventInitiator"));
		assertNotNull(db.getAttributeByName(179, "title"));
		assertNotNull(db.getAttributeByName(179, "uid"));
		assertNotNull(db.getAttributeByName(179, "config"));
		assertNotNull(db.getAttributeByName(179, "exprSql"));
		assertNotNull(db.getAttributeByName(179, "parent"));
		assertNotNull(db.getAttributeByName(180, "eventDate"));
		assertNotNull(db.getAttributeByName(180, "eventInitiator"));
		assertNotNull(db.getAttributeByName(180, "title"));
		assertNotNull(db.getAttributeByName(180, "uid"));
		assertNotNull(db.getAttributeByName(180, "hotKey"));
		assertNotNull(db.getAttributeByName(180, "diagram"));
		assertNotNull(db.getAttributeByName(180, "config"));
		assertNotNull(db.getAttributeByName(180, "icon"));
		assertNotNull(db.getAttributeByName(180, "test"));
		assertNotNull(db.getAttributeByName(180, "strings"));
		assertNotNull(db.getAttributeByName(180, "message"));
		assertNotNull(db.getAttributeByName(180, "isBtnToolBar"));
		assertNotNull(db.getAttributeByName(180, "runtimeIndex"));
		assertNotNull(db.getAttributeByName(180, "filters"));
		assertNotNull(db.getAttributeByName(180, "parent"));
		assertNotNull(db.getAttributeByName(181, "eventDate"));
		assertNotNull(db.getAttributeByName(181, "eventInitiator"));
		assertNotNull(db.getAttributeByName(181, "title"));
		assertNotNull(db.getAttributeByName(181, "uid"));
		assertNotNull(db.getAttributeByName(181, "ref"));
		assertNotNull(db.getAttributeByName(181, "constraints"));
		assertNotNull(db.getAttributeByName(181, "descInfo"));
		assertNotNull(db.getAttributeByName(181, "config"));
		assertNotNull(db.getAttributeByName(181, "data"));
		assertNotNull(db.getAttributeByName(181, "data2"));
		assertNotNull(db.getAttributeByName(181, "template"));
		assertNotNull(db.getAttributeByName(181, "template2"));
		assertNotNull(db.getAttributeByName(181, "flags"));
		assertNotNull(db.getAttributeByName(181, "bases"));
		assertNotNull(db.getAttributeByName(181, "parent"));
		assertNotNull(db.getAttributeByName(181, "базовый отчет"));
		assertNotNull(db.getAttributeByName(182, "eventDate"));
		assertNotNull(db.getAttributeByName(182, "eventInitiator"));
		assertNotNull(db.getAttributeByName(182, "title"));
		assertNotNull(db.getAttributeByName(182, "uid"));
		assertNotNull(db.getAttributeByName(182, "config"));
		assertNotNull(db.getAttributeByName(182, "strings"));
		assertNotNull(db.getAttributeByName(182, "webConfig"));
		assertNotNull(db.getAttributeByName(182, "webConfigChanged"));
		assertNotNull(db.getAttributeByName(182, "filtersFolder"));
		assertNotNull(db.getAttributeByName(182, "parent"));
		assertNotNull(db.getAttributeByName(183, "user"));
		assertNotNull(db.getAttributeByName(183, "processDef"));
		assertNotNull(db.getAttributeByName(183, "time"));
		assertNotNull(db.getAttributeByName(123, "historyProcessDef"));
		assertNotNull(db.getAttributeByName(126, "запрет использования собственных идентификационных данных в пароле"));
		assertNotNull(db.getAttributeByName(114, "config"));
		assertNotNull(db.getAttributeByName(120, "config"));
		assertNotNull(db.getAttributeByName(139, "config"));
		assertNotNull(db.getAttributeByName(107, "config"));
		assertNotNull(db.getAttributeByName(127, "config"));
		assertNotNull(db.getAttributeByName(134, "config"));
	
		assertNotNull(db.getAttributeByName(123, "lastLogoutTime"));
		assertNotNull(db.getAttributeByName(123, "isChangedPassBySys"));
		assertNotNull(db.getAttributeByName(161, "srvHistory"));
		assertNotNull(db.getAttributeByName(161, "ifcHistory"));
		assertNotNull(db.getAttributeByName(161, "fltHistory"));
		assertNotNull(db.getAttributeByName(161, "rptHistory"));
		assertNotNull(db.getAttributeByName(123, "showTooltip"));
		assertNotNull(db.getAttributeByName(123, "instantECP"));
		assertNotNull(db.getAttributeByName(137, "base"));
		assertNotNull(db.getAttributeByName(139, "isInBox"));
		assertNotNull(db.getAttributeByName(184, "message"));
		assertNotNull(db.getAttributeByName(184, "uid"));
		assertNotNull(db.getAttributeByName(184, "cuid"));
		assertNotNull(db.getAttributeByName(184, "row"));
		assertNotNull(db.getAttributeByName(184, "datetime"));
		assertNotNull(db.getAttributeByName(123, "notifications"));
		assertNotNull(db.getAttributeByName(160, "showSearchField"));
		assertNotNull(db.getAttributeByName(160, "srch_txt"));
		assertNotNull(db.getAttributeByName(160, "ifc_uid"));
		assertNotNull(db.getAttributeByName(123, "scope"));
		assertNotNull(db.getAttributeByName(160, "logotypePic"));
		assertNotNull(db.getAttributeByName(160, "logoPicWidth"));
		assertNotNull(db.getAttributeByName(160, "logoPicHeight"));
		assertNotNull(db.getAttributeByName(160, "srch_txt"));
		assertNotNull(db.getAttributeByName(160, "chat_srch_txt"));
		assertNotNull(db.getAttributeByName(160, "useNotificationSound"));
		assertNotNull(db.getAttributeByName(160, "notificationSound"));
		assertNotNull(db.getAttributeByName(123, "useNoteSound"));
		assertNotNull(db.getAttributeByName(111, "uiIcon"));
		
		assertNull(db.getAttributeByName(123, "noattribute"));
		
		db.releaseDriver(driver);
	}

	@AfterAll
	static void clear() throws Exception {
		try (Statement st = mgmtConn.createStatement()) {
			st.executeUpdate("drop database " + DB_NAME);
		}
		mgmtConn.close();
	}
}
