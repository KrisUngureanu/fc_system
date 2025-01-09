package com.cifs.or2.server.db;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.jcr.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import kz.tamur.DriverException;
import kz.tamur.comps.Constants;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.ods.Driver;
import kz.tamur.ods.Driver2;
import kz.tamur.ods.Value;
import kz.tamur.ods.debug.ConnectionProxy;
import kz.tamur.ods.debug.ResourceRegistry;
import kz.tamur.ods.mssql.MsSqlDriver3;
import kz.tamur.ods.mysql.MySqlDriver3;
import kz.tamur.ods.oracle.OracleDriver3;
import kz.tamur.ods.postgre.PgSqlDriver;
import kz.tamur.or3.util.FGACRule;
import kz.tamur.or3.util.FGARule;
import kz.tamur.or3.util.ProtocolRule;
import kz.tamur.or3.util.SystemAction;
import kz.tamur.or3.util.SystemRight;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.admin.ServerMessage;
import kz.tamur.or3ee.server.kit.Cache;
import kz.tamur.or3ee.server.kit.CacheListener;
import kz.tamur.or3ee.server.kit.CacheUtils;
import kz.tamur.util.Funcs;
import kz.tamur.util.Pair;
import kz.tamur.util.crypto.XmlUtil;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.ServerUserSession;
import com.cifs.or2.server.UserSrv;

public class Database {
	
	private static final KrnAttribute[] EMPTY_ATTR_ARRAY = new KrnAttribute[0];
	private Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + Database.class.getName());
	
	private DataSource dataSource;
	private Map<String, Connection> mutexConns = new HashMap<>();
	private String type;
	private String name;
	private String schemeName;
	private String urlConnection;
	private String fileStoreType;
	private File replDir;
	private long id;
	private boolean readyForConnection = false;
	private KrnObject obj;
	public boolean hasArticleAttr;
	
	public KrnClass protocolRuleCls = null;
	public KrnClass fgacRuleCls = null;
	public KrnClass fgaRuleCls = null;
	public KrnClass systemRightCls = null; 
	
	// Класс отслеживающий изменения в кешах
	DatabaseCacheListener cacheListener = null;
    // Кэш классов 
    private Cache<Long, KrnClass> classCache = CacheUtils.getCache("classCache");
    protected static Map<Long, KrnClass> classesById = Collections.synchronizedMap(new HashMap<Long, KrnClass>());
    protected static Map<String, KrnClass> classesByUid = Collections.synchronizedMap(new HashMap<String, KrnClass>());
    protected static Map<String, KrnClass> classesByName = Collections.synchronizedMap(new HashMap<String, KrnClass>());
    protected static Map<Long, List<KrnClass>> classesByParentId = new HashMap<Long, List<KrnClass>>();
    
    // Кэш атрибутов
    private Cache<Long, KrnAttribute> attributeCache = CacheUtils.getCache("attributeCache");

    protected static Map<Long, KrnAttribute> attrsById =
    		Collections.synchronizedMap(new HashMap<Long, KrnAttribute>());
    protected static Map<String, KrnAttribute> attrsByUid =
    		Collections.synchronizedMap(new HashMap<String, KrnAttribute>());
    protected static Map<Long, Map<String, KrnAttribute>> attrsByName =
        	new HashMap<Long, Map<String, KrnAttribute>>();
    protected static Map<Long, Set<KrnAttribute>> attrsByTypeId = new HashMap<Long, Set<KrnAttribute>>();
    protected Map<Long, KrnAttribute[]> rattrsByAttrId = new HashMap<Long, KrnAttribute[]>();
    		
    // Кэш методов
    private Cache<String, KrnMethod> methodCache = CacheUtils.getCache("methodCache");
    protected static Map<String, KrnMethod> methodsByUid =	Collections.synchronizedMap(new HashMap<String, KrnMethod>());
    protected static Map<Long, Map<String, KrnMethod>> methodsByName = new HashMap<Long, Map<String, KrnMethod>>();

    // Кэш SQL фильтров
    private static Map<Long, KrnObject> objByFid = new HashMap<Long, KrnObject>();
    private Map<Long, String> sqlByFuid = new HashMap<>();
    
    // Кэш правил протоколирования
    private Cache<Long, ProtocolRule> protocolRulesCache = CacheUtils.getCache("protocolRulesCache");
    protected static Map<Integer, List<ProtocolRule>> rulesByEvent = new HashMap<Integer, List<ProtocolRule>>(); 
    protected static Map<Integer, List<ProtocolRule>> rulesByType = new HashMap<Integer, List<ProtocolRule>>(); 

    // Кэш правил FGAC (детального контроля доступа - только для ORACLE)
    protected static Map<Long, FGACRule> fgacByObjectId = Collections.synchronizedMap(new HashMap<Long, FGACRule>());
    // Кэш правил FGA (детального аудита - только для ORACLE)
    protected static Map<Long, FGARule> fgaByObjectId = Collections.synchronizedMap(new HashMap<Long, FGARule>());

    // Кэш системных прав пользователей
    private Cache<Long, SystemRight> systemRightsCache = CacheUtils.getCache("systemRightsCache");
    protected static Map<Integer, List<SystemRight>> rightByAction = new HashMap<Integer, List<SystemRight>>(); 
    
    // Кэш директорий для хранения внешнийх BLOB атрибутов
    private Map<Long, String> blobDirs = Collections.synchronizedMap(new HashMap<Long, String>());

	private Map<Long, Pair<Long,Map<Long,Pair<Long, Object[]>>>> dataChangesCache =
			new HashMap<Long, Pair<Long,Map<Long,Pair<Long, Object[]>>>>();
	
	private boolean withTransactionWatchDog;
	
	public static InetAddress address;
	
	private Map<String, KrnObject> objCache = new HashMap<>();
	
	// Классы метаданных
	public KrnClass CLS_PROCESS_DEF;
	public KrnClass CLS_UI;
	public KrnClass CLS_FILTER;
	public KrnClass CLS_REPORT;
	
	//Классы и атрибуты при формировании селекта для получения списка потоков
    private KrnClass PROCESS;
    private KrnClass FLOW;
    private KrnClass PROCESSDEF;
    private KrnClass USER;
    private KrnClass USER_ROOT;
    //private KrnAttribute FUSER;
    private KrnAttribute ACTOR;
    private KrnAttribute BASE;
    private KrnAttribute CURRENT;
    private KrnAttribute FLOW_START;
    private KrnAttribute FLOW_END;
    private KrnAttribute CONTROLL;
    private KrnAttribute FLOW_TITLE;
    private KrnAttribute CUTOBJ;
    private KrnAttribute TITLEOBJ;
    private KrnAttribute TRANSITION;
    private KrnAttribute SYNCNODE;
    private KrnAttribute NODE;
    private KrnAttribute TYPENODE;
    private KrnAttribute STATUS;
    private KrnAttribute EVENT;
    private KrnAttribute UI;
    private KrnAttribute TYPEUI;
    private KrnAttribute PERMIT;
    private KrnAttribute CORELID;
    private KrnAttribute PROCESSINSTANCE;
    private KrnAttribute PARENTFLOW;
    private KrnAttribute ROOTFLOW;
    private KrnAttribute SUPERFLOW;
    private KrnAttribute PROCESSDEFINITION;
    private KrnAttribute PROCESS_START;
    private KrnAttribute PROCESS_END;
    private KrnAttribute INITIATOR;
    private KrnAttribute KILLER;
    private KrnAttribute ISPROCESS;
    private KrnAttribute TRANSID;
    private KrnAttribute OBSERVERS;
    private KrnAttribute UIOBSERVERS;
    private KrnAttribute TYPEUIOBSERVERS;
    private KrnAttribute PD_TITLE;
    private KrnAttribute USER_SIGN;
    private KrnAttribute USER_PARENT;
    private KrnAttribute BALANS_ED;
    private KrnAttribute USER_BASE;
    private KrnAttribute USER_BASE_NAME;
    private String TASK_SQL;
    private String TASK_COUNT_SQL;
    private String PROCDEFS_SQL;
    private String CORELID_SQL;
    private String FLOWEVENT_SQL;
    private String FLOWS_FOR_REMOVE_SQL;
    private String FLOWBYUITYPE_SQL;
    private String FLOWBYCUTOBJ_SQL;
    private String FLOWBYCUTOBJ_SQL2;
    private String USERSADM_SQL;
    private String DATACONTROL_EXIST_SQL;
    private String TASKCOLOR_EXIST_SQL;
    private String DATACONTROL_SQL;
    private String PERMIT_UPDATE_SQL;
    private String USERPARENT_SQL;
    private String SUBFLOW_SQL;
    private String FLOW_tname;
    private String PROCESSDEF_tname;
    private String USER_tname;
    private String USER_PARENT_tname;
    private String USER_ROOT_tname;
    private String PROCESS_tname;
    private String ACTOR_tname;
    private String BASE_tname;
    private String CURRENT_tname;
    private String FLOW_START_tname;
    private String FLOW_END_tname;
    private String CONTROLL_tname;
    private String FLOW_TITLE_tname;
    private String TITLEOBJ_tname;
    private String TRANSITION_tname;
    private String SYNCNODE_tname;
    private String NODE_tname;
    private String TYPENODE_tname;
    private String STATUS_tname;
    private String EVENT_tname;
    private String UI_tname;
    private String TYPEUI_tname;
    private String PERMIT_tname;
    private String ROOTFLOW_tname;
    private String SUPERFLOW_tname;
    private String PROCESSDEFINITION_tname;
    private String PROCESSINSTANCE_tname;
    private String PROCESS_START_tname;
    private String PROCESS_END_tname;
    private String INITIATOR_tname;
    private String KILLER_tname;
    private String ISPROCESS_tname;
    private String TRANSID_tname;
    private String OBSERVERS_tname;
    private String UIOBSERVERS_tname;
    private String TYPEUIOBSERVERS_tname;
    private String USER_SIGN_tname;
    private String USERPARENT_tname;
    private String PD_TITLE_tname;
    //private String FUSER_tname;
    private String PARENTFLOW_tname;
    private String CORELID_tname;
    private String CUTOBJ_tname;
    private String BALANS_ED_tname;
    private String USER_BASE_tname;
    private String USER_BASE_cname;
    private String USER_BASE_NAME_cname;
    private String CUTOBJ_sql;
    private Activity nullActivity = new Activity(-1, -1,new long[0],-1, new long[0][0], new long[0], new KrnObject[0],
    		new KrnObject(-1,"",-1), "", 0, 0, null, null, "", "", new String[0], "", new byte[0], new KrnObject(-1,"",-1),
            new String[0], new KrnObject[0], new KrnObject(-1,"",-1), "", false, "", "");
    
    private Set<Long> vcsClassIds = new HashSet<Long>();
    private Set<Long> vcsDiffAttrIds = new HashSet<Long>();
    private Map<Long,KrnAttribute> vcsClsTtlAttr = new HashMap<Long,KrnAttribute>();
    private Map<Long,List<KrnAttribute>> vcsClsAttrs = new HashMap<Long,List<KrnAttribute>>();
    
    private boolean dbReadOnly = true;
    
    //TODO Позже необходимо сделать все классы отдельными.
    private Set<Long> separateClassIds = new HashSet<Long>();
    
	private Map<Long, JcrRepositoryConfig> jcrRepositoryByAttrId = new HashMap<Long, JcrRepositoryConfig>();
	
    static {
		try {
			address = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isObserversAllow=false;
	public static boolean isRnDB = "true".equals(System.getProperty("isRnDB"))||"true".equals(System.getProperty("isEGKNDB"));
	public static boolean isUlDB = "true".equals(System.getProperty("isUlProject"));
	private boolean isVcsNotDiffUi=false;
	
	public static boolean showUserDB = "true".equals(System.getProperty("showUserBaseName"));
	
	private String[] connParams = null;
	
    public Database(String name, String schemeName, String type, DataSource dataSource, String replDir, boolean withTransactionWatchDog,
    		String dbSeparateClassIds, String fileStoreType) throws DriverException {
    	this(name, schemeName, type, dataSource, null, null, null, null, null, null, replDir, withTransactionWatchDog, dbSeparateClassIds, fileStoreType);
    	
    }
    
    public Database(String name, String schemeName, String type, DataSource dataSource, 
    		String url, String user, String pd,
    		String urlExt, String userExt, String pdExt,
    		String replDir, boolean withTransactionWatchDog,
    		String dbSeparateClassIds,String fileStoreType) throws DriverException {
		this.name = Funcs.sanitizeSQL(name);
		this.schemeName = Funcs.sanitizeSQL(schemeName);
		this.type = type;
		this.dataSource = dataSource;
		this.fileStoreType = fileStoreType;
		this.withTransactionWatchDog = withTransactionWatchDog;

    	this.connParams = new String[] {url, user, pd, urlExt, userExt, pdExt};

		isObserversAllow = "true".equals(System.getProperty("isObserversAllow"));
		isVcsNotDiffUi = "true".equals(System.getProperty("isVcsNotDiffUi","false"));
		
		if (dbSeparateClassIds != null) {
			for (String classIdString : dbSeparateClassIds.split(",")) {
				separateClassIds.add(Long.valueOf(classIdString));
			}
		}
		
		if (Funcs.isValid(replDir)) {
			this.replDir = Funcs.getCanonicalFile(replDir);
			this.replDir.mkdirs();
		}

		addCacheListener(name);

		// Иницализация репозитариев до драйвера, т.к. он может их использовать.
		initJcrRepositories();

		Driver drv = getDriver(new ServerUserSession(name, "server", new UserSrv("sys"), address.getHostAddress(), address.getHostName(), UserSession.SERVER_ID, false));
		if (schemeName == null) {
			schemeName = drv.getCurrentScheme();
		}
		this.schemeName = Funcs.sanitizeSQL(schemeName);
		
		id = drv.init();
		dbReadOnly = drv.isDbReadOnly();

		reloadCache(drv);
		reloadBlobDirs(drv);
		reloadProtocoleRuleCache(drv);
		reloadSystemRightsCache(drv);
		reloadFGACRuleCache(drv);
		reloadFGARuleCache(drv);
		
		obj = drv.getObjectById(id);
                
        KrnClass cls = getClassByName("Flow");
    	KrnAttribute attr = getAttributeByName(cls.id, "article");
    	hasArticleAttr = attr != null;

		CLS_PROCESS_DEF = getClassByName("ProcessDef");
		CLS_UI = getClassByName("UI");
		CLS_FILTER = getClassByName("Filter");
		CLS_REPORT = getClassByName("ReportPrinter");

    	//tasklist
        PROCESSDEF = CLS_PROCESS_DEF;
        PROCESS = getClassByName("Process");
        FLOW = getClassByName("Flow");
        USER = getClassByName("User");
        USER_ROOT = getClassByName("UserRoot");
        //FUSER = getAttributeByName(FLOW.id, "user");
        ACTOR = getAttributeByName(FLOW.id, "actor");
        BASE = getAttributeByName(FLOW.id, "base");
        CURRENT = getAttributeByName(FLOW.id, "current");
        FLOW_START = getAttributeByName(FLOW.id, "start");
        FLOW_END = getAttributeByName(FLOW.id, "end");
        CONTROLL = getAttributeByName(FLOW.id, "control");
        FLOW_TITLE = getAttributeByName(FLOW.id, "title");
        CUTOBJ = getAttributeByName(FLOW.id, "cutObj");
        TITLEOBJ = getAttributeByName(FLOW.id, "titleObj");
        TRANSITION = getAttributeByName(FLOW.id, "transition");
        SYNCNODE = getAttributeByName(FLOW.id, "syncNode");
        NODE = getAttributeByName(FLOW.id, "node");
        TYPENODE = getAttributeByName(FLOW.id, "typeNode");
        STATUS = getAttributeByName(FLOW.id, "status");
        EVENT = getAttributeByName(FLOW.id, "event");
        UI = getAttributeByName(FLOW.id, "ui");
        TYPEUI = getAttributeByName(FLOW.id, "typeUi");
        PARENTFLOW = getAttributeByName(FLOW.id, "parentFlow");
        PROCESSINSTANCE = getAttributeByName(FLOW.id, "processInstance");
        PERMIT = getAttributeByName(FLOW.id, "permit");
        CORELID = getAttributeByName(FLOW.id, "corelId");
        ROOTFLOW = getAttributeByName(PROCESS.id, "rootFlow");
        SUPERFLOW = getAttributeByName(PROCESS.id, "superFlow");
        PROCESSDEFINITION = getAttributeByName(PROCESS.id, "processDefinition");
        PROCESS_START = getAttributeByName(PROCESS.id, "start");
        PROCESS_END = getAttributeByName(PROCESS.id, "end");
        INITIATOR = getAttributeByName(PROCESS.id, "initiator");
        KILLER = getAttributeByName(PROCESS.id, "killer");
        ISPROCESS = getAttributeByName(PROCESS.id, "isProcess");
        TRANSID = getAttributeByName(PROCESS.id, "transId");
        OBSERVERS = getAttributeByName(PROCESS.id, "observers");
        UIOBSERVERS = getAttributeByName(PROCESS.id, "uiObservers");
        TYPEUIOBSERVERS = getAttributeByName(PROCESS.id, "typeUiObservers");
        PD_TITLE = getAttributeByName(PROCESSDEF.id, "title");
        USER_SIGN = getAttributeByName(USER.id, "sign");
        USER_PARENT = getAttributeByName(USER.id, "parent");
        BALANS_ED = getAttributeByName(USER.id, "баланс_ед");
        USER_BASE = getAttributeByName(USER.id, "base");
        USER_BASE_NAME = getAttributeByName(USER_BASE.typeClassId, "наименование");
        FLOW_tname = drv.getClassTableName(FLOW.id);
        PROCESSDEF_tname = drv.getClassTableName(PROCESSDEF.id);
        USER_tname = drv.getClassTableName(USER.id);
        USER_ROOT_tname = drv.getClassTableName(USER_ROOT.id);
        USER_PARENT_tname = drv.getAttrTableName(USER_PARENT);
        PROCESS_tname = drv.getClassTableName(PROCESS.id);
        ACTOR_tname = drv.getColumnName(ACTOR.id);
        BASE_tname = drv.getColumnName(BASE.id);
        CURRENT_tname = drv.getColumnName(CURRENT.id);
        FLOW_START_tname = drv.getColumnName(FLOW_START.id);
        FLOW_END_tname = drv.getColumnName(FLOW_END.id);
        CONTROLL_tname = drv.getColumnName(CONTROLL.id);
        FLOW_TITLE_tname = drv.getColumnName(FLOW_TITLE.id);
        TITLEOBJ_tname = drv.getColumnName(TITLEOBJ.id);
        TRANSITION_tname = drv.getColumnName(TRANSITION.id);
        SYNCNODE_tname = drv.getColumnName(SYNCNODE.id);
        NODE_tname = drv.getColumnName(NODE.id);
        TYPENODE_tname = drv.getColumnName(TYPENODE.id);
        STATUS_tname = drv.getColumnName(STATUS.id);
        EVENT_tname = drv.getColumnName(EVENT.id);
        UI_tname = drv.getColumnName(UI.id);
        System.out.println("UI=" + UI);
        System.out.println("UI_tname=" + UI_tname);
        if(UI.isMultilingual) UI_tname +="_1";
        TYPEUI_tname = drv.getColumnName(TYPEUI.id);
        PERMIT_tname = drv.getColumnName(PERMIT.id);
        ROOTFLOW_tname = drv.getColumnName(ROOTFLOW.id);
        SUPERFLOW_tname = drv.getColumnName(SUPERFLOW.id);
        PROCESSDEFINITION_tname = drv.getColumnName(PROCESSDEFINITION.id);
        PROCESSINSTANCE_tname = drv.getColumnName(PROCESSINSTANCE.id);
        PROCESS_START_tname = drv.getColumnName(PROCESS_START.id);
        PROCESS_END_tname = drv.getColumnName(PROCESS_END.id);
        INITIATOR_tname = drv.getColumnName(INITIATOR.id);
        KILLER_tname = drv.getColumnName(KILLER.id);
        ISPROCESS_tname = drv.getColumnName(ISPROCESS.id);
        TRANSID_tname = drv.getColumnName(TRANSID.id);
        OBSERVERS_tname = drv.getColumnName(OBSERVERS.id);
        UIOBSERVERS_tname = drv.getColumnName(UIOBSERVERS.id);
        TYPEUIOBSERVERS_tname = drv.getColumnName(TYPEUIOBSERVERS.id);
        USER_SIGN_tname = drv.getColumnName(USER_SIGN.id);
        USERPARENT_tname = drv.getColumnName(USER_PARENT.id);
        PD_TITLE_tname = drv.getColumnName(PD_TITLE.id);
        //FUSER_tname = drv.getColumnName(FUSER.id);
        PARENTFLOW_tname = drv.getColumnName(PARENTFLOW.id);
        CORELID_tname = drv.getColumnName(CORELID.id);
        CUTOBJ_tname = drv.getColumnName(CUTOBJ.id);
        BALANS_ED_tname = "";
        USER_BASE_cname = drv.getColumnName(USER_BASE.id);
        USER_BASE_tname = drv.getClassTableName(USER_BASE.typeClassId);
        USER_BASE_NAME_cname = drv.getColumnName(USER_BASE_NAME);  
        
        if(BALANS_ED!=null)
            BALANS_ED_tname = drv.getColumnName(BALANS_ED.id);
        //Запрос списка заданий для пользователя на конкретный период
        //CUTOBJ_sql = (drv instanceof OracleDriver3) ? "" : "f."+CUTOBJ_tname+" fcutobj,";
        CUTOBJ_sql = (drv instanceof OracleDriver3)?"TO_CHAR(f."+CUTOBJ_tname+") fcutobj,":"f."+CUTOBJ_tname+" fcutobj,";
        
        String ERR_SQL = (drv instanceof OracleDriver3)
        				? "BITAND(f." + PERMIT_tname + ", 512) - BITAND(f." + PERMIT_tname + ", 1) - BITAND(f." + PERMIT_tname + ", 256)/256 - BITAND(f." + PERMIT_tname + ", 32768)/32768 ferror,"
        				: (drv instanceof MsSqlDriver3 || drv instanceof PgSqlDriver) ?
        				  "(f." + PERMIT_tname + " & 512) - (f." + PERMIT_tname + " & 1) - (f." + PERMIT_tname + " & 256)/256 - (f." + PERMIT_tname + " & 32768)/32768 ferror,"
        				: "cast(f." + PERMIT_tname + " & 512 as signed) - cast(f." + PERMIT_tname + " & 1 as signed) - cast(f." + PERMIT_tname + " & 256 as signed)/256 - cast(f." + PERMIT_tname + " & 32768 as signed)/32768 ferror,";

        String TITLE_SQL = (drv instanceof OracleDriver3)
				? "NVL(f." + FLOW_TITLE_tname + "_1, ' ') ftitle1,"
				: (drv instanceof MsSqlDriver3) ?
				  "ISNULL(f." + FLOW_TITLE_tname + "_1, '') ftitle1,"
				: (drv instanceof PgSqlDriver) ?
				  "COALESCE(f." + FLOW_TITLE_tname + "_1, '') ftitle1,"
				: "IFNULL(f." + FLOW_TITLE_tname + "_1, '') ftitle1,";

        TASK_SQL="SELECT f.c_obj_id fcobjid,"+CUTOBJ_sql
    			+"f."+ACTOR_tname +" factor,f."+BASE_tname +" fbase,f."+CURRENT_tname+" fcurrent,f."+FLOW_START_tname+" fstart,f."
        		+FLOW_END_tname	+" fend,f."+CONTROLL_tname+" fcontrol," + TITLE_SQL + "f."
    			+FLOW_TITLE_tname+"_2 ftitle2,f."+TITLEOBJ_tname +"_1 ftitleobj1,f."+TITLEOBJ_tname+"_2 ftitleobj2,f."
        		+TRANSITION_tname+" ftransition,f."+SYNCNODE_tname+" fsyncnode,f."
    			+NODE_tname+" fnode,f."+TYPENODE_tname+" ftypenode,f."+STATUS_tname+" fstatus,f."+EVENT_tname
    			+" fevent,f."+UI_tname+" fui,f."+TYPEUI_tname+" ftypeui,f."+PERMIT_tname+" fpermit," + ERR_SQL + "p."+ROOTFLOW_tname
    			+" prootflow,p."+SUPERFLOW_tname+" psuperflow,p."+PROCESSDEFINITION_tname+" pprocessdef"
    			+ ",p.c_obj_id pcobjid,p."+PROCESS_START_tname+" pstart,p."+PROCESS_END_tname+" pend,p."
    			+INITIATOR_tname + " pinitiator,p."+KILLER_tname+" pkiller,p."+ISPROCESS_tname+" pisprocess,p."
    			+TRANSID_tname +" ptransid,p."+OBSERVERS_tname + " pobservers,p."+UIOBSERVERS_tname+" puiobservers,p."
    			+TYPEUIOBSERVERS_tname+" ptypeuiobservers,u."+USER_SIGN_tname+"_1 uusersign1,uf."+USER_SIGN_tname
    			+"_1 ufusersign1,pd."+PD_TITLE_tname +"_1 pdtitle1,pd."+PD_TITLE_tname+"_2 pdtitle2,fp."+NODE_tname 
    			+" fpnode,p1."+SUPERFLOW_tname +" p1superflow,p1."+PROCESSDEFINITION_tname+" p1processdef,f1."+NODE_tname+" f1node,pd1."
    			+PD_TITLE_tname+"_1 pd1title1,pd1."+PD_TITLE_tname+"_2 pd1title2 ,p2."+SUPERFLOW_tname+" p2superflow,p2."
    			+PROCESSDEFINITION_tname +" p2processdef,f2."+NODE_tname+" f2node,pd2."+PD_TITLE_tname+"_1 pd2title1,pd2."
    			+PD_TITLE_tname+"_2 pd2title2 " +  (showUserDB ? (", ub." + USER_BASE_NAME_cname + " ubName ") : "")
    			+ "FROM "+FLOW_tname+" f LEFT JOIN "+PROCESS_tname +" p ON f."+PROCESSINSTANCE_tname +" = p.c_obj_id "
    			+ "LEFT JOIN "+PROCESSDEF_tname+" pd ON p."+ PROCESSDEFINITION_tname+" =pd.c_obj_id "
    			+ "LEFT JOIN "+USER_tname+" u ON p."+ INITIATOR_tname+" =u.c_obj_id AND u.c_tr_id=0 "
    			+ "LEFT JOIN "+USER_tname+" uf ON f."+ ACTOR_tname+" =uf.c_obj_id AND uf.c_tr_id=0 "
    			+ "LEFT JOIN "+FLOW_tname+" fp ON f."+PARENTFLOW_tname+"=fp.c_obj_id "
    			+ "LEFT JOIN "+FLOW_tname+" f1 ON p."+SUPERFLOW_tname+"=f1.c_obj_id "
    			+ "LEFT JOIN "+PROCESS_tname +" p1 ON f1."+PROCESSINSTANCE_tname +" = p1.c_obj_id "
    			+ "LEFT JOIN "+PROCESSDEF_tname+" pd1 ON p1."+ PROCESSDEFINITION_tname+" =pd1.c_obj_id AND pd1.c_tr_id=0 "
    			+ "LEFT JOIN "+FLOW_tname+" f2 ON p1."+SUPERFLOW_tname+"=f2.c_obj_id "
    			+ "LEFT JOIN "+PROCESS_tname +" p2 ON f2."+PROCESSINSTANCE_tname +" = p2.c_obj_id "
    			+ "LEFT JOIN "+PROCESSDEF_tname+" pd2 ON p2."+ PROCESSDEFINITION_tname+" =pd2.c_obj_id AND pd2.c_tr_id=0 "
    			+ (showUserDB ? ("LEFT JOIN "+USER_BASE_tname+ " ub ON u." + USER_BASE_cname + "=ub.c_obj_id AND ub.c_tr_id=0 ") : "")
    			+ "WHERE f.c_obj_id is not null ";
        TASK_COUNT_SQL="SELECT f."+TYPENODE_tname+" ftypenode,f."+PERMIT_tname+" fpermit "
    			+ "FROM "+FLOW_tname+" f LEFT JOIN "+PROCESS_tname +" p ON f."+PROCESSINSTANCE_tname +" = p.c_obj_id "
    			+ "LEFT JOIN "+PROCESSDEF_tname+" pd ON p."+ PROCESSDEFINITION_tname+" =pd.c_obj_id "
    			+ "LEFT JOIN "+USER_tname+" u ON p."+ INITIATOR_tname+" =u.c_obj_id AND u.c_tr_id=0 "
    			+ "LEFT JOIN "+USER_tname+" uf ON f."+ ACTOR_tname+" =uf.c_obj_id AND uf.c_tr_id=0 "
    			+ "LEFT JOIN "+FLOW_tname+" fp ON f."+PARENTFLOW_tname+"=fp.c_obj_id "
    			+ "LEFT JOIN "+FLOW_tname+" f1 ON p."+SUPERFLOW_tname+"=f1.c_obj_id "
    			+ "LEFT JOIN "+PROCESS_tname +" p1 ON f1."+PROCESSINSTANCE_tname +" = p1.c_obj_id "
    			+ "LEFT JOIN "+PROCESSDEF_tname+" pd1 ON p1."+ PROCESSDEFINITION_tname+" =pd1.c_obj_id AND pd1.c_tr_id=0 "
    			+ "LEFT JOIN "+FLOW_tname+" f2 ON p1."+SUPERFLOW_tname+"=f2.c_obj_id "
    			+ "LEFT JOIN "+PROCESS_tname +" p2 ON f2."+PROCESSINSTANCE_tname +" = p2.c_obj_id "
    			+ "LEFT JOIN "+PROCESSDEF_tname+" pd2 ON p2."+ PROCESSDEFINITION_tname+" =pd2.c_obj_id AND pd2.c_tr_id=0 "
    			+ "WHERE f.c_obj_id is not null ";
        //Запрос для описаний служб
        PROCDEFS_SQL="SELECT c_obj_id FROM "+PROCESSDEF_tname+" WHERE c_class_id="+PROCESSDEF.id;
        //Запрос на получение задания с конкретным идентификатором для инициатора сообщения
        CORELID_SQL="SELECT c_obj_id FROM "+FLOW_tname+" WHERE "+CORELID_tname+" = ?";
        //Запрос по событию
        FLOWEVENT_SQL="SELECT c_obj_id FROM "+FLOW_tname+" WHERE "+EVENT_tname+" = ?";
        //Запрос по типу интерфейса
        FLOWBYUITYPE_SQL="SELECT c_obj_id FROM "+FLOW_tname+" WHERE "+TYPEUI_tname+" = ?";
        //Запрос по конкретному объекту обработки и конкретному описанию процесса
        FLOWBYCUTOBJ_SQL="SELECT f.c_obj_id FROM "+FLOW_tname+" f "
        		+ "LEFT JOIN "+PROCESS_tname+" p ON f."+PROCESSINSTANCE_tname +" = p.c_obj_id "
        		+ "LEFT JOIN "+FLOW_tname +" sf ON p."+SUPERFLOW_tname +" = sf.c_obj_id "
        		+ "LEFT JOIN "+PROCESS_tname+" sp ON sf."+PROCESSINSTANCE_tname +" = sp.c_obj_id "
        		+ "LEFT JOIN "+FLOW_tname +" ssf ON sp."+SUPERFLOW_tname +" = ssf.c_obj_id "
        		+ "LEFT JOIN "+PROCESS_tname+" ssp ON ssf."+PROCESSINSTANCE_tname +" = ssp.c_obj_id "
        		+ "WHERE (p."+ PROCESSDEFINITION_tname+ "=? OR sp."+ PROCESSDEFINITION_tname + "=? "
        		+ "OR ssp."+ PROCESSDEFINITION_tname + "=?) AND f."+CUTOBJ_tname+" LIKE ?";
        FLOWBYCUTOBJ_SQL2="SELECT f.c_obj_id FROM "+FLOW_tname+" f LEFT JOIN "+PROCESS_tname 
        		+" p ON f."+PROCESSINSTANCE_tname +" = p.c_obj_id WHERE f."+CUTOBJ_tname+" LIKE ?";
        //Запрос пользователей с данной балансовой единицей
        if(BALANS_ED!=null)
        	USERSADM_SQL="SELECT c_obj_id FROM "+USER_tname+" WHERE "+BALANS_ED_tname+" = ? AND c_is_del=0 AND c_tr_id=0";
        //Запрос списка заданий с датой контроля
        DATACONTROL_EXIST_SQL="SELECT c_obj_id,"+ACTOR_tname+" factor FROM "+FLOW_tname+" WHERE "+ACTOR_tname+" IS NOT NULL AND "+CONTROLL_tname+" IS NOT NULL ";
        //Запрос списка заданий с датой контроля
        TASKCOLOR_EXIST_SQL="SELECT f.c_obj_id fobj,f."+ACTOR_tname+" factor,f."+NODE_tname+" fnode,p."+PROCESSDEFINITION_tname+" pdobj"
        		+ " FROM "+FLOW_tname+" f LEFT JOIN "+PROCESS_tname +" p ON f."+PROCESSINSTANCE_tname +" = p.c_obj_id WHERE f."+ACTOR_tname+" IS NOT NULL ";
        //Запрос списка заданий с просроченной датой контроля
        DATACONTROL_SQL="SELECT c_obj_id,"+ACTOR_tname+" factor,"+STATUS_tname+ " fstatus,"+CONTROLL_tname+" fcontrol,"
        +PERMIT_tname+" fpermit FROM "+FLOW_tname+" WHERE "+ACTOR_tname+" IS NOT NULL AND "+CONTROLL_tname+" IS NOT NULL AND "+CONTROLL_tname+" <= ? ";
        PERMIT_UPDATE_SQL="UPDATE "+FLOW_tname+" SET "+PERMIT_tname+"=? WHERE c_obj_id=?"; 
        //Запрос списка ролей для данного пользователя
        USERPARENT_SQL=(drv instanceof OracleDriver3)
        		?"SELECT "+USERPARENT_tname+" FROM "+USER_PARENT_tname 
        		+" START WITH c_obj_id=? AND c_tr_id=0 CONNECT BY NOCYCLE PRIOR c_obj_id = "+USERPARENT_tname
        		:"SELECT "+USERPARENT_tname+" FROM "+USER_PARENT_tname 
        		+" WHERE "+USERPARENT_tname+" NOT IN(SELECT c_obj_id FROM "+USER_ROOT_tname+") AND c_tr_id=0 AND c_obj_id IN ";
        SUBFLOW_SQL="SELECT MAX("+ROOTFLOW_tname+") prootflow FROM "+PROCESS_tname+" WHERE "+SUPERFLOW_tname+"=? GROUP BY "+SUPERFLOW_tname;
        			
        FLOWS_FOR_REMOVE_SQL="SELECT f.c_obj_id fobjid,f."+PROCESSINSTANCE_tname +" pobjid ,p."+TRANSID_tname +" ptid "
    			+ "FROM "+FLOW_tname+" f LEFT JOIN "+PROCESS_tname +" p ON f."+PROCESSINSTANCE_tname +" = p.c_obj_id "
    			+ "WHERE f.c_obj_id is not null AND ("+PERMIT_tname+"=0 OR "+PERMIT_tname+" IS NULL) AND f."+ACTOR_tname +"=? AND p."+ PROCESSDEFINITION_tname+" =?";
        initVcsClassIds();
        
        releaseDriver(drv);
	}
	
	private void addCacheListener(String dsName) {
		this.cacheListener = new DatabaseCacheListener(dsName);
	    classCache.addEntryListener(cacheListener);
	    attributeCache.addEntryListener(cacheListener);
	    methodCache.addEntryListener(cacheListener);
	    protocolRulesCache.addEntryListener(cacheListener);
	    systemRightsCache.addEntryListener(cacheListener);
		
		initializeMapsFromCache(cacheListener);
	}

	private void initializeMapsFromCache(CacheListener<Object, Object> cacheListener) {
		for (Long key : classCache.getKeys()) {
			cacheListener.entryAdded(classCache.getName(), key, classCache.get(key), null);
		}
		for (Long key : attributeCache.getKeys()) {
			cacheListener.entryAdded(attributeCache.getName(), key, attributeCache.get(key), null);
		}
		for (String key : methodCache.getKeys()) {
			cacheListener.entryAdded(methodCache.getName(), key, methodCache.get(key), null);
		}
		for (Long key : protocolRulesCache.getKeys()) {
			cacheListener.entryAdded(protocolRulesCache.getName(), key, protocolRulesCache.get(key), null);
		}
		
		systemRightsCache.printClusterInfo();
		
		log.info("Считываем значения");
		for (Long key : systemRightsCache.getKeys()) {
			log.info("Key: " + key);
			cacheListener.entryAdded(systemRightsCache.getName(), key, systemRightsCache.get(key), null);
		}
		log.info("После считывания значений");
	}

	public Connection getConnection() throws SQLException {
		Connection conn = dataSource != null 
				? dataSource.getConnection()
				: DriverManager.getConnection(connParams[0], connParams[1], connParams[2]);
				
		urlConnection = conn.getMetaData().getURL();
		if (withTransactionWatchDog) {
			conn = (Connection)Proxy.newProxyInstance(
					getClass().getClassLoader(),
					new Class[] {Connection.class},
					new ConnectionProxy(conn));
			ResourceRegistry.instance().resourceAllocated(conn);
		}
		return conn;
	}
	public Connection getExternalConnection(String jndiName) {
		try {
			Context ic = new InitialContext();
			Object obj = ic.lookup(Funcs.sanitizeSQL(jndiName));
			ic.close();
			Connection extConn = ((DataSource)obj).getConnection();
			return extConn;
		} catch (NamingException e) {
    		log.error(e);
    		try {
    			return DriverManager.getConnection(connParams[3], connParams[4], connParams[5]);
    		} catch (SQLException e2) {
        		log.error(e2);
    		}
		} catch (SQLException e) {
    		log.error(e);
		}
		return null;
	}
	public long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public String getSchemeName() {
		return schemeName != null ? schemeName : name;
	}

	public void setSchemeName(String schemeName) {
		this.schemeName = Funcs.sanitizeSQL(schemeName);
	}

	public Driver2 getDriver(UserSession us) throws DriverException {
    	try {
    		Driver2 drv = makeObject(us);
    		return drv;
    	} catch (Exception e) {
    		throw new DriverException(e);
    	}
    }
    
    public void releaseDriver(Driver drv) throws DriverException {
    	try {
    		drv.rollback();
    	} catch (Exception e) {
    		throw new DriverException(e);
    	} finally {
    		drv.release();
    	}
    }
    
    public void releaseDriverQuietly(Driver drv) {
    	if (drv == null) {
    		return;
    	}
    	try {
    		drv.rollback();
    	} catch (Exception e) {
    		log.error(e);
    	} finally {
    		drv.release();
    	}
    }
    
	public Driver2 makeObject(UserSession us) throws Exception {
		kz.tamur.or3.util.Tname.setDBtype(type);
		if (type.equals("mysql") || type.equals("mysql3")) {
        	return new MySqlDriver3(this, name, us);
        } else if (type.equals("oracle3") || type.equals("oracle")) {
        	return new OracleDriver3(this, name, us);
        } else if (type.equals("mssql3")) {
        	return new MsSqlDriver3(this, name, us);
        } else if (type.equals("pgsql")) {
        	return new PgSqlDriver(this, name, us);
        }
	    return null;
	}

	public File getReplicationDirectory() {
		return replDir;
	}
	
	public String getReplicationDirectoryPath() {
		return replDir.getAbsolutePath();
	}

	public boolean isReadyForConnection() {
		return readyForConnection;
	}

	public void setReadyForConnection(boolean readyForConnection) {
		this.readyForConnection = readyForConnection;
	}
	
	public KrnObject getObject() {
		return obj;
	}

	public KrnClass getClassById(long id) {
		return classesById.get(id);
	}
	
	public KrnClass getClassByUid(String uid) {
		return classesByUid.get(uid);
	}
	
	public KrnClass getClassByName(String name) {
		return classesByName.get(name);
	}
	
	public List<KrnClass> getClassesByNameWithOptions(String name, long searchMethod) {
		
		String entryS = new String();
    	String nameS = name.toString().toUpperCase(Constants.OK);
    	List<KrnClass> clses = new ArrayList<KrnClass>();
		if (searchMethod == ComparisonOperations.SEARCH_START_WITH) 
    		for (Map.Entry<String, KrnClass> entry : classesByName.entrySet())
        	{
        		entryS = entry.getKey().toString().toUpperCase(Constants.OK);
        		if(entryS.startsWith(nameS)) {
        			clses.add(classesByName.get(entry.getKey()));
        		}
        	}
    	else if (searchMethod == ComparisonOperations.CO_EQUALS) 
    		for (Map.Entry<String, KrnClass> entry : classesByName.entrySet())
        	{
        		entryS = entry.getKey().toString().toUpperCase(Constants.OK);
        		if(entryS.equals(nameS)) {
        			clses.add(classesByName.get(entry.getKey()));
        		}
        	}
    	else if (searchMethod == ComparisonOperations.CO_CONTAINS) 
    		for (Map.Entry<String, KrnClass> entry : classesByName.entrySet())
        	{
        		entryS = entry.getKey().toString().toUpperCase(Constants.OK);
        		if(entryS.contains(nameS)) {
        			clses.add(classesByName.get(entry.getKey()));
        		}
        	}
    	return clses;
	}
	
	public void getSubClasses(long id, boolean recursive, Collection<KrnClass> classes) {
		List<KrnClass> list = classesByParentId.get(id);
		if (list != null) {
	    	for (int i = 0; i < list.size(); i++) {
				classes.add(list.get(i));
				if (recursive)
					getSubClasses(list.get(i).id, recursive, classes);
			}
		}
	}
	
	public List<KrnClass> getSuperClasses(long id) {
		List<KrnClass> classes = new ArrayList<KrnClass>();
		KrnClass cls = classesById.get(id);
		while (cls != null) {
			classes.add(cls);
			cls = getClassById(cls.parentId);
		}
		return classes;
	}

	public void addClass(KrnClass cls, boolean isLoading) {
		if (isLoading) {
    		if (classCache.get(cls.id) == null) {
    			classCache.put(cls.id, cls);
        	}
    		else if (classesByName.get(cls.name) == null) {
    			log.error("Не должен сюда заходить class");
    			cacheListener.entryAdded(classCache.getName(), cls.id, classCache.get(cls.id), null);
    		}
    	} else {
    		classCache.put(cls.id, cls);
    	}
	}

    public void removeClass(KrnClass cls) {
    	classCache.remove(cls.id);
    }

    public void addAttribute(KrnAttribute attr, boolean isLoading) {
    	if (isLoading) {
    		if (attributeCache.get(attr.id) == null) {
    			attributeCache.put(attr.id, attr);
        	}
    		else if (getAttributeByName(attr.classId, attr.name) == null) {
    			log.error("Не должен сюда заходить attr");
    			cacheListener.entryAdded(attributeCache.getName(), attr.id, attributeCache.get(attr.id), null);
    		}
    	} else {
    		attributeCache.put(attr.id, attr);
    	}
    }
    
    public KrnAttribute getAttributeById(long id) {
    	return attrsById.get(id);
    }

    public KrnAttribute getAttributeByUid(String uid) {
    	return attrsByUid.get(uid);
    }
    
    public List<KrnAttribute> getAttributesByUidPart(String uid, long searchMethod) {
    	List<KrnAttribute> attributes = new ArrayList<KrnAttribute>();
    	String entryS = new String();
    	String uidS = new String();
    	if (searchMethod == ComparisonOperations.SEARCH_START_WITH) 
    		for (Map.Entry<String, KrnAttribute> entry : attrsByUid.entrySet())
        	{
        		entryS = entry.getKey().toString().toUpperCase(Constants.OK);
        		uidS = uid.toString().toUpperCase(Constants.OK);
        		if(entryS.startsWith(uidS)) {
        			attributes.add(entry.getValue());
        		}
        	}
    	else if (searchMethod == ComparisonOperations.CO_EQUALS)
            attributes.add(attrsByUid.get(uid));
    	else if (searchMethod == ComparisonOperations.CO_CONTAINS)
        	for (Map.Entry<String, KrnAttribute> entry : attrsByUid.entrySet())
        	{
        		entryS = entry.getKey().toString().toUpperCase(Constants.OK);
        		uidS = uid.toString().toUpperCase(Constants.OK);
        		if(entryS.contains(uidS)) {
        			attributes.add(entry.getValue());
        		}
        	}
    	else attributes.clear();
    	
    	return attributes;
    }

    public KrnAttribute getAttributeByName(long classId, String name) {
		KrnAttribute attr = null;
		Map<String, KrnAttribute> map = attrsByName.get(classId);
		if (map != null) {
			attr = map.get(name);
		}
		if (attr != null)
			return attr;
    	KrnClass cls = getClassById(classId);
    	if (cls.parentId > 0) {
			return getAttributeByName(cls.parentId, name);
    	}
    	return null;
    }

    public List<KrnAttribute> getAttributesByName(String name, long searchMethod) {
    	List<KrnAttribute> attributes = new ArrayList<KrnAttribute>();
    	
    	for (Long clsId : attrsByName.keySet()) {
    		Map<String, KrnAttribute> attrs = attrsByName.get(clsId);
    		for (String attrName : attrs.keySet()) {
    	    	if (
    	    			(searchMethod == ComparisonOperations.CO_EQUALS && attrName.equals(name)) ||
    	    			(searchMethod == ComparisonOperations.CO_CONTAINS && attrName.contains(name)) ||
    	    			(searchMethod == ComparisonOperations.SEARCH_START_WITH && attrName.startsWith(name))
    	    		) {
    	    		attributes.add(attrs.get(attrName));
    	        }
    		}
    	}
    	return attributes;
    }
    
    public List<KrnAttribute> getAttributesByClassId(long classId, boolean inherited) {
    	List<KrnAttribute> res = new ArrayList<KrnAttribute>();
		Map<String, KrnAttribute> map = attrsByName.get(classId);
		if (map != null) {
			res.addAll(map.values());
		}
		if (inherited) {
			KrnClass cls = getClassById(classId);
			while (cls.parentId > 0) {
	    		map = attrsByName.get(cls.parentId);
	    		if (map != null)
	    			res.addAll(map.values());
	    		cls = getClassById(cls.parentId);
			}
		}
    	return res;
    }

    public List<KrnAttribute> getAttributesByTypeId(long typeId, boolean inherited) {
    	List<KrnAttribute> res = new ArrayList<KrnAttribute>();
		Set<KrnAttribute> attrs = attrsByTypeId.get(typeId);
		if (attrs != null)
			res.addAll(attrs);
		if (inherited) {
			KrnClass cls = getClassById(typeId);
			while (cls.parentId > 0) {
	    		attrs = attrsByTypeId.get(cls.parentId);
	    		if (attrs != null)
	    			res.addAll(attrs);
	    		cls = getClassById(cls.parentId);
			}
		}
    	return res;
    }

    public void removeAttribute(KrnAttribute attr) {
    	attributeCache.remove(attr.id);
    }

    // Метод, возвращающий список всех аттрибутов
    public List<KrnMethod> getAllMethods() {
    	List<KrnMethod> methods = new ArrayList<KrnMethod>();
        for (String key : methodsByUid.keySet()) {
        	methods.add(methodsByUid.get(key));
        }
    	return methods;
    }
    
    // Метод, возвращающий HashMap всех аттрибутов
    public Map<String, KrnMethod> getMethodsMap() {
    	Map<String, KrnMethod> methodsMap = new HashMap<String, KrnMethod>();
        for (String key : methodsByUid.keySet()) {
        	methodsMap.put(key, methodsByUid.get(key));
        }
    	return methodsMap;
    }
    
    public KrnMethod getMethodByUid(String uid) {
    	return methodsByUid.get(uid);
    }
    
    public List<KrnMethod> getMethodsByUid(String uid, long searchMethod) {
    	List<KrnMethod> methods = new ArrayList<KrnMethod>();
    	
    	if (searchMethod == ComparisonOperations.CO_EQUALS) {
    		KrnMethod m = methodsByUid.get(uid);
    		if (m != null)
    			methods.add(m);
    	} else {
	    	for (String key : methodsByUid.keySet()) {
		    	if (
		    			(searchMethod == ComparisonOperations.SEARCH_START_WITH && key.startsWith(uid)) ||
		    			(searchMethod == ComparisonOperations.CO_CONTAINS && key.contains(uid))
		    		) {
	        		KrnMethod m = methodsByUid.get(key);
	        		if (m != null)
	        			methods.add(m);
		    	}
	    	}
    	}    	
    	return methods;
    }

    public KrnMethod getMethodByName(long classId, String name) {
		KrnMethod m = null;
		Map<String, KrnMethod> map = methodsByName.get(classId);
		if (map != null) {
			m = map.get(name);
		}
		if (m != null)
			return m;
    	// Если не нашли, то ищем в родительском классе
    	KrnClass cls = getClassById(classId);
		if (cls.parentId > 0)
			return getMethodByName(cls.parentId, name);
    	return null;
    }
    
    public List<KrnMethod> getMethodsByName(String name, int searchMethod) {
        List<KrnMethod> methods = new ArrayList<KrnMethod>();
        
    	for (Long clsId : methodsByName.keySet()) {
    		Map<String, KrnMethod> ms = methodsByName.get(clsId);
    		for (String mName : ms.keySet()) {
    	    	if (
    	    			(searchMethod == ComparisonOperations.CO_EQUALS && mName.equals(name)) ||
    	    			(searchMethod == ComparisonOperations.CO_CONTAINS && mName.contains(name)) ||
    	    			(searchMethod == ComparisonOperations.SEARCH_START_WITH && mName.startsWith(name))
    	    		) {
    	    		methods.add(ms.get(mName));
    	        }
    		}
    	}
    	return methods;
    }

    public List<KrnMethod> getMethodsByClassId(long clsId) {
        List<KrnMethod> methods = new ArrayList<KrnMethod>();
        Map<String, KrnMethod> ms = methodsByName.get(clsId);

        if (ms != null)
        	methods.addAll(ms.values());

    	return methods;
    }

    public void addMethod(KrnMethod method, boolean isLoading) {
    	if (isLoading) {
    		if (methodCache.get(method.uid) == null) {
        		methodCache.put(method.uid, method);
        	}
    		else if (getMethodByName(method.classId, method.name) == null) {
    			log.error("Не должен сюда заходить method");
    			cacheListener.entryAdded(methodCache.getName(), method.uid, methodCache.get(method.uid), null);
    		}
    	} else {
    		methodCache.put(method.uid, method);
    	}
    }
    
    public void removeMethod(KrnMethod method) {
    	methodCache.remove(method.uid);
    }
    
    public void addProtocolRule(KrnObject obj, long attrId, Driver drv) throws DriverException {
    	KrnClass cls = getProtocolRuleCls();
    	
    	if (cls != null && obj.classId == cls.id) {
            if (attrId == 1) {
            	addProtocolRule(new ProtocolRule(obj.id, "", null, 0, 0, false, false));
            } else if (attrId == 2) {
            	removeProtocolRule(obj.id);
            } else {
                KrnAttribute nameAttr = getAttributeByName(cls.id, "name");
                KrnAttribute exprAttr = getAttributeByName(cls.id, "expr");
                KrnAttribute eventAttr = getAttributeByName(cls.id, "event");
                KrnAttribute typeAttr = getAttributeByName(cls.id, "eventType");
                KrnAttribute denyAttr = getAttributeByName(cls.id, "deny");
                KrnAttribute blockAttr = getAttributeByName(cls.id, "block");
                KrnClass eventCls = getClassById(eventAttr.typeClassId);
                KrnClass typeCls = getClassById(typeAttr.typeClassId);
                KrnAttribute codeAttr = getAttributeByName(eventCls.id, "code");
                KrnAttribute typeCodeAttr = getAttributeByName(typeCls.id, "code");

                ProtocolRule rule = protocolRulesCache.get(obj.id);
            	if (rule != null) {
                	protocolRulesCache.remove(rule.getId());
                    if (nameAttr.id == attrId) {
                    	rule.setName(drv.getValue(obj.id, attrId, 0, 0, 0));
                    } else if (exprAttr.id == attrId) {
                    	rule.setExpression((byte[])drv.getValue(obj.id, attrId, 0, 0, 0));
                    } else if (eventAttr.id == attrId) {
                    	Object val = drv.getValue(obj.id, attrId, 0, 0, 0);
                    	
                    	int event = 0;
                    	
                    	if (val instanceof KrnObject) {
                    		Object val2 = drv.getValue(((KrnObject)val).id, codeAttr.id, 0, 0, 0);
                    		event = (val2 instanceof Number) ? ((Number)val2).intValue() : 0;
                    	}
                    	int oldEvent = rule.getEvent();
                    	
                    	if (oldEvent != event) {
    	                	rule.setEvent(event);
                    	}
                    } else if (typeAttr.id == attrId) {
                    	Object val = drv.getValue(obj.id, attrId, 0, 0, 0);
                    	
                    	int type = 0;
                    	
                    	if (val instanceof KrnObject) {
                    		Object val2 = drv.getValue(((KrnObject)val).id, typeCodeAttr.id, 0, 0, 0);
                    		type = (val2 instanceof Number) ? ((Number)val2).intValue() : 0;
                    	}
                    	int oldType = rule.getEventType();
                    	
                    	if (oldType != type) {
    	                	rule.setEventType(type);
                    	}
                    } else if (blockAttr.id == attrId) {
                    	Object val = drv.getValue(obj.id, attrId, 0, 0, 0);
                    	boolean blocked = (val instanceof Boolean) ? ((Boolean)val).booleanValue() : false;
                    	rule.setBlocked(blocked);
                    } else if (denyAttr.id == attrId) {
                    	Object val = drv.getValue(obj.id, attrId, 0, 0, 0);
                    	boolean deny = (val instanceof Boolean) ? ((Boolean)val).booleanValue() : false;
                    	rule.setDeny(deny);
                    }
                	protocolRulesCache.put(rule.getId(), rule);
            	}
            }
    	}
            
    }

    public void addProtocolRule(ProtocolRule rule) {
    	protocolRulesCache.put(rule.getId(), rule);
    }

    public void removeProtocolRule(long id) {
    	protocolRulesCache.remove(id);
    }
    
    public void removeProtocolRule(ProtocolRule rule) {
    	protocolRulesCache.remove(rule.getId());
    }

    public List<ProtocolRule> getProtocolRulesByEvent(int event) {
    	List<ProtocolRule> res = new ArrayList<ProtocolRule>();
    	synchronized (rulesByEvent) {
    		List<ProtocolRule> map = rulesByEvent.get(event);
    		if (map != null)
    			res.addAll(map);
    	}
    	return res;
    }

    public List<ProtocolRule> getProtocolRulesByEventType(int type) {
    	List<ProtocolRule> res = new ArrayList<ProtocolRule>();
    	synchronized (rulesByType) {
    		List<ProtocolRule> map = rulesByType.get(type);
    		if (map != null)
    			res.addAll(map);
    	}
    	return res;
    }

    public void addFGACRule(KrnObject obj, long attrId, Driver drv) throws DriverException {
    	KrnClass cls = getFGACRuleCls();
    	
    	if (cls != null && obj.classId == cls.id) {
            if (attrId == 1) {
            	addFGACRule(new FGACRule(obj.id, "", "", null, null, null, false));
            } else if (attrId == 2) {
            	FGACRule rule = removeFGACRule(obj.id);
                drv.dropPolicy(rule);
            } else {
                KrnAttribute nameAttr = getAttributeByName(cls.id, "name");
                KrnAttribute exprAttr = getAttributeByName(cls.id, "дополнительное условие");
                KrnAttribute attrsAttr = getAttributeByName(cls.id, "атрибуты");
                KrnAttribute classAttr = getAttributeByName(cls.id, "класс");
                KrnAttribute opsAttr = getAttributeByName(cls.id, "операции");
                KrnAttribute blockAttr = getAttributeByName(cls.id, "заблокировано?");
                
            	FGACRule rule = fgacByObjectId.get(obj.id);
            	String oldName = rule.getName();
            	String oldTable = rule.getClassName();
            	
            	if (nameAttr.id == attrId) {
                	fgacByObjectId.get(obj.id).setName(drv.getValue(obj.id, attrId, 0, 0, 0));
                } else if (exprAttr.id == attrId) {
                	fgacByObjectId.get(obj.id).setExpression(drv.getValue(obj.id, attrId, 0, 0, 0));
                } else if (attrsAttr.id == attrId) {
                	fgacByObjectId.get(obj.id).setAttrNames(drv.getValue(obj.id, attrId, 0, 0, 0));
                } else if (classAttr.id == attrId) {
                	fgacByObjectId.get(obj.id).setClassName(drv.getValue(obj.id, attrId, 0, 0, 0));
                } else if (opsAttr.id == attrId) {
                	fgacByObjectId.get(obj.id).setOperations(drv.getValue(obj.id, attrId, 0, 0, 0));
                } else if (blockAttr.id == attrId) {
                	Object val = drv.getValue(obj.id, attrId, 0, 0, 0);
                	boolean blocked = (val instanceof Boolean) ? ((Boolean)val).booleanValue() : false;
                	rule.setBlocked(blocked);
                }
                drv.createOrUpdatePolicy(oldName, oldTable, rule);
            }
    	}
            
    }

    public void addFGACRule(FGACRule rule) {
		fgacByObjectId.put(rule.getId(), rule);
    }

    public FGACRule removeFGACRule(long id) {
    	return fgacByObjectId.remove(id);
    }
    
    public void removeFGACRule(FGACRule rule) {
    	fgacByObjectId.remove(rule.getId());
    }

    public void addFGARule(KrnObject obj, long attrId, Driver drv) throws DriverException {
    	KrnClass cls = getFGARuleCls();
    	
    	if (cls != null && obj.classId == cls.id) {
            if (attrId == 1) {
            	addFGARule(new FGARule(obj.id, "", "", null, null, null, false));
            } else if (attrId == 2) {
            	FGARule rule = removeFGARule(obj.id);
                drv.dropPolicy(rule);
            } else {
                KrnAttribute nameAttr = getAttributeByName(cls.id, "name");
                KrnAttribute exprAttr = getAttributeByName(cls.id, "дополнительное условие");
                KrnAttribute attrsAttr = getAttributeByName(cls.id, "атрибуты");
                KrnAttribute classAttr = getAttributeByName(cls.id, "класс");
                KrnAttribute opsAttr = getAttributeByName(cls.id, "операции");
                KrnAttribute blockAttr = getAttributeByName(cls.id, "заблокировано?");
                
            	FGARule rule = fgaByObjectId.get(obj.id);
            	String oldName = rule.getName();
            	String oldTable = rule.getClassName();
            	
            	if (nameAttr.id == attrId) {
                	fgaByObjectId.get(obj.id).setName(drv.getValue(obj.id, attrId, 0, 0, 0));
                } else if (exprAttr.id == attrId) {
                	fgaByObjectId.get(obj.id).setExpression(drv.getValue(obj.id, attrId, 0, 0, 0));
                } else if (attrsAttr.id == attrId) {
                	fgaByObjectId.get(obj.id).setAttrNames(drv.getValue(obj.id, attrId, 0, 0, 0));
                } else if (classAttr.id == attrId) {
                	fgaByObjectId.get(obj.id).setClassName(drv.getValue(obj.id, attrId, 0, 0, 0));
                } else if (opsAttr.id == attrId) {
                	fgaByObjectId.get(obj.id).setOperations(drv.getValue(obj.id, attrId, 0, 0, 0));
                } else if (blockAttr.id == attrId) {
                	Object val = drv.getValue(obj.id, attrId, 0, 0, 0);
                	boolean blocked = (val instanceof Boolean) ? ((Boolean)val).booleanValue() : false;
                	rule.setBlocked(blocked);
                }
                drv.createOrUpdatePolicy(oldName, oldTable, rule);
            }
    	}
            
    }

    public void addFGARule(FGARule rule) {
		fgaByObjectId.put(rule.getId(), rule);
    }

    public FGARule removeFGARule(long id) {
    	return fgaByObjectId.remove(id);
    }
    
    public void removeFGARule(FGARule rule) {
    	fgaByObjectId.remove(rule.getId());
    }

    public void addSystemRight(KrnObject obj, long attrId, Driver drv, long trId) throws DriverException {
    	KrnClass cls = getSystemRightCls();
    	
    	if (cls != null && obj.classId == cls.id) {
            if (attrId == 1) {
            	addSystemRight(new SystemRight(obj.id, "", "", null, 0, false, false), false);
            } else if (attrId == 2) {
            	removeSystemRight(obj.id);
            } else {
                KrnAttribute nameAttr = getAttributeByName(cls.id, "name");
                KrnAttribute descAttr = getAttributeByName(cls.id, "description");
                KrnAttribute exprAttr = getAttributeByName(cls.id, "expr");
                KrnAttribute actionAttr = getAttributeByName(cls.id, "action");
                KrnAttribute blockAttr = getAttributeByName(cls.id, "block");
                KrnAttribute denyAttr = getAttributeByName(cls.id, "deny");
                KrnClass actionCls = getClassById(actionAttr.typeClassId);
                KrnAttribute codeAttr = getAttributeByName(actionCls.id, "code");
                KrnAttribute userAttr = getAttributeByName(cls.id, "userOrRole");
                KrnAttribute subjectAttr1 = getAttributeByName(cls.id, "архив");
                KrnAttribute subjectAttr2 = getAttributeByName(cls.id, "процесс");
                KrnAttribute subjectAttr3 = getAttributeByName(cls.id, "НСИ");
                KrnAttribute subjectAttr4 = getAttributeByName(cls.id, "пользователь");
                KrnAttribute subjectAttr5 = getAttributeByName(cls.id, "роль");
                SystemRight right = systemRightsCache.get(obj.id);
            	if (right != null) {
                	systemRightsCache.remove(right.getId());
	            	if (nameAttr.id == attrId) {
	                	right.setName(drv.getValue(obj.id, attrId, 0, 0, trId));
	            	} else if (descAttr.id == attrId) {
	            		right.setDescription(drv.getValue(obj.id, attrId, 0, 0, trId));
	            	} else if (exprAttr.id == attrId) {
	            		right.setExpression((byte[])drv.getValue(obj.id, attrId, 0, 0, trId));
	            	} else if (actionAttr.id == attrId) {
	                	Object val = drv.getValue(obj.id, attrId, 0, 0, trId);
	                	
	                	int action = 0;
	                	
	                	if (val instanceof KrnObject) {
	                		Object val2 = drv.getValue(((KrnObject)val).id, codeAttr.id, 0, 0, trId);
	                		action = (val2 instanceof Number) ? ((Number)val2).intValue() : 0;
	                	}
	                	int oldAction = right.getAction();
	                	
	                	if (oldAction != action) {
		                	right.setAction(action);
	                	}
	                } else if (userAttr.id == attrId) {
	                	SortedSet<Value> vals = drv.getValues(new long[] {obj.id}, null, attrId, 0, trId);
	               	
	                	right.clearUsers();
	                	
	                	for (Value val : vals) {
	                    	long userId = 0;
	                    	KrnObject user = null;
	                    	
	                    	if (val.value instanceof KrnObject) {
	                    		user = (KrnObject)val.value;
	                    		userId = user.id;
	                    	}
	                    	if (userId > 0) {
			                	right.addUser(user);
	                    	}
	                	}
	                }else if (subjectAttr1.id == attrId || subjectAttr2.id == attrId
	            		 || subjectAttr3.id == attrId || subjectAttr4.id == attrId || subjectAttr5.id == attrId) {
		            	SortedSet<Value> vals = drv.getValues(new long[] {obj.id}, null, attrId, 0, trId);
		            	
		            	right.clearSubjects();
		            	for (Value val : vals) {
		            		if (val.value instanceof KrnObject) {
		            			right.addSubject((KrnObject)val.value);
		            		}
		            	}
	                } else if (blockAttr.id == attrId) {
	                	Object val = drv.getValue(obj.id, attrId, 0, 0, trId);
	                	boolean blocked = (val instanceof Boolean) ? ((Boolean)val).booleanValue() : false;
	                	right.setBlocked(blocked);
	                } else if (denyAttr.id == attrId) {
	                	Object val = drv.getValue(obj.id, attrId, 0, 0, trId);
	                	boolean denying = (val instanceof Boolean) ? ((Boolean)val).booleanValue() : false;
	                	right.setDenying(denying);
	                }
                	systemRightsCache.put(right.getId(), right);
            	}
            }
    	}
    }

    public void addSystemRight(SystemRight right, boolean isLoading) {
    	if (isLoading) {
    		if (systemRightsCache.get(right.getId()) == null) {
    			log.info("Добавление 1");
    			systemRightsCache.put(right.getId(), right);
    			log.info("После добавления 1");
        	}
    		else {
    			putRightByActionIfNotLoaded(right);
    		}
    	} else {
    		log.info("Добавление 2");
    		systemRightsCache.put(right.getId(), right);
    		log.info("После добавления 2");
    	}
    }

    public void removeSystemRight(long id) {
    	systemRightsCache.remove(id);
    }
    
    public void removeSystemRight(SystemRight right) {
    	systemRightsCache.remove(right.getId());
    }

    public List<SystemRight> getSystemRightsByAction(int action) {
    	List<SystemRight> res = new ArrayList<SystemRight>();
    	synchronized (rightByAction) {
    		List<SystemRight> map = rightByAction.get(action);
    		if (map != null)
    			res.addAll(map);
    	}
    	return res;
    }
    
    private void putRightByActionIfNotLoaded(SystemRight right) {
    	synchronized (rightByAction) {
    		List<SystemRight> l = rightByAction.get(right.getAction());
    		if (l == null) {
    			l = new ArrayList<SystemRight>();
    			rightByAction.put(right.getAction(), l);
    		}
    		if (!l.contains(right)) {
    			log.error("Не должен сюда заходить");
    			l.add(right);
    		}
    	}
    }

    public void reloadCache(Driver drv) throws DriverException {
        // Загружаем кэш классов
        List<KrnClass> clss = drv.getAllClasses();
        for (KrnClass cls : clss)
        	addClass(cls, true);
        // Загружаем кэш атрибутов и обратных атрибутов
        List<KrnAttribute> attrs = drv.getAllAttributes();
        for (KrnAttribute attr : attrs) {
        	addAttribute(attr, true);
        	KrnAttribute[] rAttrs = drv.getRevAttributes(attr.id);
			rattrsByAttrId.put(attr.id, rAttrs);
        }
        // Загружаем кэш методов
        List<KrnMethod> methods = drv.getAllMethods();
        for (KrnMethod method : methods)
        	addMethod(method, true);
    }
    
    public void reloadProtocoleRuleCache(Driver drv) throws DriverException {
        KrnClass cls = getProtocolRuleCls();
        if (cls != null) {
        	boolean ready = true;
            KrnAttribute nameAttr = getAttributeByName(cls.id, "name");
            if (nameAttr == null) {
            	log.warn("Не найден атрибут \"name\" класса \"ProtocolRule\"");
            	ready = false;
            }
        	KrnAttribute exprAttr = getAttributeByName(cls.id, "expr");
            if (exprAttr == null) {
            	log.warn("Не найден атрибут \"expr\" класса \"ProtocolRule\"");
            	ready = false;
            }
            KrnAttribute eventAttr = getAttributeByName(cls.id, "event");
            if (eventAttr == null) {
            	log.warn("Не найден атрибут \"event\" класса \"ProtocolRule\"");
            	ready = false;
            }
            KrnAttribute typeAttr = getAttributeByName(cls.id, "eventType");
            if (typeAttr == null) {
            	log.warn("Не найден атрибут \"eventType\" класса \"ProtocolRule\"");
            	ready = false;
            }
            KrnAttribute denyAttr = getAttributeByName(cls.id, "deny");
            if (denyAttr == null) {
            	log.warn("Не найден атрибут \"deny\" класса \"ProtocolRule\"");
            	ready = false;
            }
            KrnAttribute blockAttr = getAttributeByName(cls.id, "block");
            if (blockAttr == null) {
            	log.warn("Не найден атрибут \"block\" класса \"ProtocolRule\"");
            	ready = false;
            }
            if (!ready) return;
            
            KrnClass eventCls = getClassById(eventAttr.typeClassId);
            KrnAttribute codeAttr = getAttributeByName(eventCls.id, "code");
            if (codeAttr == null) {
            	log.warn("Не найден атрибут \"code\" класса \"" + eventCls.name + "\"");
            	ready = false;
            }
            KrnClass typeCls = getClassById(typeAttr.typeClassId);
            KrnAttribute typeCodeAttr = getAttributeByName(typeCls.id, "code");
            if (typeCodeAttr == null) {
            	log.warn("Не найден атрибут \"code\" класса \"" + typeCls.name + "\"");
            	ready = false;
            }
            if (!ready) return;

        	AttrRequestBuilder arb = new AttrRequestBuilder(cls, null).add(nameAttr).add(exprAttr)
        			.add(typeAttr, new AttrRequestBuilder(typeCls.getKrnClass(), null).add(typeCodeAttr))
        			.add(eventAttr, new AttrRequestBuilder(eventCls.getKrnClass(), null).add(codeAttr)).add(denyAttr).add(blockAttr);
        	List<Object[]> prows = drv.getObjects(cls.id, null, arb.build(), 0, new int[1], 0, null,null);

            for (Object[] prow : prows) {
                KrnObject o = arb.getObject(prow);

                String name = arb.getStringValue("name", prow);
                byte[] expression = (byte[]) arb.getValue("expr", prow);
                int type = arb.getIntValue("eventType.code", prow);
                int event = arb.getIntValue("event.code", prow);
                boolean deny = arb.getBooleanValue("deny", prow);
                boolean block = arb.getBooleanValue("block", prow);
                
            	addProtocolRule(new ProtocolRule(o.id, name, expression, type, event, deny, block));
            }
        } else {
        	log.warn("Не найден класс ProtocolRule");
        }
    }
    
    public void reloadFGACRuleCache(Driver drv) throws DriverException {
        KrnClass cls = getFGACRuleCls();
        if (cls != null) {
        	boolean ready = true;
            KrnAttribute nameAttr = getAttributeByName(cls.id, "name");
            if (nameAttr == null) {
            	log.warn("Не найден атрибут \"name\" класса \"FGACRule\"");
            	ready = false;
            }
            KrnAttribute exprAttr = getAttributeByName(cls.id, "дополнительное условие");
            if (exprAttr == null) {
            	log.warn("Не найден атрибут \"дополнительное условие\" класса \"FGACRule\"");
            	ready = false;
            }
            KrnAttribute attrsAttr = getAttributeByName(cls.id, "атрибуты");
            if (attrsAttr == null) {
            	log.warn("Не найден атрибут \"атрибуты\" класса \"FGACRule\"");
            	ready = false;
            }
            KrnAttribute classAttr = getAttributeByName(cls.id, "класс");
            if (classAttr == null) {
            	log.warn("Не найден атрибут \"класс\" класса \"FGACRule\"");
            	ready = false;
            }
            KrnAttribute opsAttr = getAttributeByName(cls.id, "операции");
            if (opsAttr == null) {
            	log.warn("Не найден атрибут \"операции\" класса \"FGACRule\"");
            	ready = false;
            }
            KrnAttribute blockAttr = getAttributeByName(cls.id, "заблокировано?");
            if (blockAttr == null) {
            	log.warn("Не найден атрибут \"заблокировано?\" класса \"FGACRule\"");
            	ready = false;
            }
            if (!ready) return;

        	AttrRequestBuilder arb = new AttrRequestBuilder(cls, null).add(nameAttr).add(exprAttr).add(attrsAttr).add(classAttr).add(opsAttr).add(blockAttr);
        	List<Object[]> prows = drv.getObjects(cls.id, null, arb.build(), 0, new int[1], 0, null,null);

            for (Object[] prow : prows) {
                KrnObject o = arb.getObject(prow);

                String name = arb.getStringValue("name", prow);
                String expression = arb.getStringValue("дополнительное условие", prow);
                String className = arb.getStringValue("класс", prow);
                String attrsName = arb.getStringValue("атрибуты", prow);
                String operations = arb.getStringValue("операции", prow);
                boolean block = arb.getBooleanValue("заблокировано?", prow);
                
            	addFGACRule(new FGACRule(o.id, name, className, attrsName, operations, expression, block));
            }
        } else {
        	log.warn("Не найден класс FGACRule");
        }
    }

    public void reloadFGARuleCache(Driver drv) throws DriverException {
        KrnClass cls = getFGARuleCls();
        if (cls != null) {
        	boolean ready = true;
            KrnAttribute nameAttr = getAttributeByName(cls.id, "name");
            if (nameAttr == null) {
            	log.warn("Не найден атрибут \"name\" класса \"FGARule\"");
            	ready = false;
            }
            KrnAttribute exprAttr = getAttributeByName(cls.id, "дополнительное условие");
            if (exprAttr == null) {
            	log.warn("Не найден атрибут \"дополнительное условие\" класса \"FGARule\"");
            	ready = false;
            }
            KrnAttribute attrsAttr = getAttributeByName(cls.id, "атрибуты");
            if (attrsAttr == null) {
            	log.warn("Не найден атрибут \"атрибуты\" класса \"FGARule\"");
            	ready = false;
            }
            KrnAttribute classAttr = getAttributeByName(cls.id, "класс");
            if (classAttr == null) {
            	log.warn("Не найден атрибут \"класс\" класса \"FGARule\"");
            	ready = false;
            }
            KrnAttribute opsAttr = getAttributeByName(cls.id, "операции");
            if (opsAttr == null) {
            	log.warn("Не найден атрибут \"операции\" класса \"FGARule\"");
            	ready = false;
            }
            KrnAttribute blockAttr = getAttributeByName(cls.id, "заблокировано?");
            if (blockAttr == null) {
            	log.warn("Не найден атрибут \"заблокировано?\" класса \"FGARule\"");
            	ready = false;
            }
            if (!ready) return;

        	AttrRequestBuilder arb = new AttrRequestBuilder(cls, null).add(nameAttr).add(exprAttr).add(attrsAttr).add(classAttr).add(opsAttr).add(blockAttr);
        	List<Object[]> prows = drv.getObjects(cls.id, null, arb.build(), 0, new int[1], 0, null,null);

            for (Object[] prow : prows) {
                KrnObject o = arb.getObject(prow);

                String name = arb.getStringValue("name", prow);
                String expression = arb.getStringValue("дополнительное условие", prow);
                String className = arb.getStringValue("класс", prow);
                String attrsName = arb.getStringValue("атрибуты", prow);
                String operations = arb.getStringValue("операции", prow);
                boolean block = arb.getBooleanValue("заблокировано?", prow);
                
            	addFGARule(new FGARule(o.id, name, className, attrsName, operations, expression, block));
            }
        } else {
        	log.warn("Не найден класс FGARule");
        }
    }

    public KrnClass getProtocolRuleCls() {
    	if (protocolRuleCls == null) {
    		protocolRuleCls = getClassByName("ProtocolRule");
    	}
    	return protocolRuleCls;
    }
    
    public KrnClass getFGACRuleCls() {
    	if (fgacRuleCls == null) {
    		fgacRuleCls = getClassByName("FGACRule");
    	}
    	return fgacRuleCls;
    }

    public KrnClass getFGARuleCls() {
    	if (fgaRuleCls == null) {
    		fgaRuleCls = getClassByName("FGARule");
    	}
    	return fgaRuleCls;
    }

    public void reloadSystemRightsCache(Driver drv) throws DriverException {
        KrnClass cls = getSystemRightCls();
        if (cls != null) {
        	boolean ready = true;
            KrnAttribute nameAttr = getAttributeByName(cls.id, "name");
            if (nameAttr == null) {
            	log.warn("Не найден атрибут \"name\" класса \"SystemRight\"");
            	ready = false;
            }
            KrnAttribute descAttr = getAttributeByName(cls.id, "description");
            if (descAttr == null) {
            	log.warn("Не найден атрибут \"description\" класса \"SystemRight\"");
            	ready = false;
            }
            KrnAttribute exprAttr = getAttributeByName(cls.id, "expr");
            if (exprAttr == null) {
            	log.warn("Не найден атрибут \"expr\" класса \"SystemRight\"");
            	ready = false;
            }
            KrnAttribute actionAttr = getAttributeByName(cls.id, "action");
            if (actionAttr == null) {
            	log.warn("Не найден атрибут \"action\" класса \"SystemRight\"");
            	ready = false;
            }
            KrnAttribute denyAttr = getAttributeByName(cls.id, "deny");
            if (denyAttr == null) {
            	log.warn("Не найден атрибут \"deny\" класса \"SystemRight\"");
            	ready = false;
            }
            KrnAttribute blockAttr = getAttributeByName(cls.id, "block");
            if (blockAttr == null) {
            	log.warn("Не найден атрибут \"block\" класса \"SystemRight\"");
            	ready = false;
            }
            KrnAttribute userAttr = getAttributeByName(cls.id, "userOrRole");
            if (userAttr == null) {
            	log.warn("Не найден атрибут \"userOrRole\" класса \"SystemRight\"");
            	ready = false;
            }
            KrnAttribute subjectAttr1 = getAttributeByName(cls.id, "архив");
            if (subjectAttr1 == null) {
            	log.warn("Не найден атрибут \"архив\" класса \"SystemRight\"");
            	ready = false;
            }
            KrnAttribute subjectAttr2 = getAttributeByName(cls.id, "процесс");
            if (subjectAttr2 == null) {
            	log.warn("Не найден атрибут \"процесс\" класса \"SystemRight\"");
            	ready = false;
            }
            KrnAttribute subjectAttr3 = getAttributeByName(cls.id, "НСИ");
            if (subjectAttr3 == null) {
            	log.warn("Не найден атрибут \"НСИ\" класса \"SystemRight\"");
            	ready = false;
            }
            KrnAttribute subjectAttr4 = getAttributeByName(cls.id, "пользователь");
            if (subjectAttr4 == null) {
            	log.warn("Не найден атрибут \"пользователь\" класса \"SystemRight\"");
            	ready = false;
            }
            KrnAttribute subjectAttr5 = getAttributeByName(cls.id, "роль");
            if (subjectAttr5 == null) {
            	log.warn("Не найден атрибут \"роль\" класса \"SystemRight\"");
            	ready = false;
            }
            if (!ready) return;
            
            KrnClass actionCls = getClassById(actionAttr.typeClassId);
            KrnAttribute codeAttr = getAttributeByName(actionCls.id, "code");
            if (codeAttr == null) {
            	log.warn("Не найден атрибут \"code\" класса \"" + actionCls.name + "\"");
            	ready = false;
            }
            if (!ready) return;

        	AttrRequestBuilder arb = new AttrRequestBuilder(cls, null).add(nameAttr).add(descAttr).add(exprAttr)
        			.add(actionAttr, new AttrRequestBuilder(actionCls.getKrnClass(), null)
        			.add(codeAttr)).add(userAttr).add(denyAttr).add(blockAttr);
        	
        	List<Object[]> prows = drv.getObjects(cls.id, null, arb.build(), 0, new int[1], 0, null,null);

            for (Object[] prow : prows) {
                KrnObject o = arb.getObject(prow);

                String name = arb.getStringValue("name", prow);
                String description = arb.getStringValue("description", prow);
                byte[] expression = (byte[]) arb.getValue("expr", prow);
                int action = arb.getIntValue("action.code", prow);
                boolean deny = arb.getBooleanValue("deny", prow);
                boolean block = arb.getBooleanValue("block", prow);

                SystemRight right = new SystemRight(o.id, name, description, expression, action, deny, block);
                
            	SortedSet<Value> vals = drv.getValues(new long[] {o.id}, null, userAttr.id, 0, 0);
            	for (Value val : vals) {
                	long userId = 0;
                	KrnObject user = null;
                	if (val.value instanceof KrnObject) {
                		user = (KrnObject)val.value;
                		userId = user.id;
                	}
                	if (userId > 0) {
	                	right.addUser(user);
                	}
            	}

            	KrnAttribute attr = null;
                if (action == SystemAction.ACTION_VIEW_ARCHIVE.getCode()) {
                	attr = subjectAttr1;
                } else if (action == SystemAction.ACTION_START_PROCESS.getCode()) {
                	attr = subjectAttr2;
                } else if (action == SystemAction.ACTION_EDIT_DICTIONARY.getCode()) {
                	attr = subjectAttr3;
            	} else if (action == SystemAction.ACTION_EDIT_USER.getCode()) {
                	attr = subjectAttr4;
            	} else if (action == SystemAction.ACTION_EDIT_ROLE.getCode()) {
                	attr = subjectAttr5;
            	}
                
                if (attr != null) {
	            	vals = drv.getValues(new long[] {o.id}, null, attr.id, 0, 0);
	            	for (Value val : vals) {
	                	long userId = 0;
	                	KrnObject subject = null;
	                	if (val.value instanceof KrnObject) {
	                		subject = (KrnObject)val.value;
	                		userId = subject.id;
	                	}
	                	if (userId > 0) {
		                	right.addSubject(subject);
	                	}
	            	}
                }
            	addSystemRight(right, true);
            }
        } else {
        	log.warn("Не найден класс SystemRight");
        }
    }
    
    public KrnClass getSystemRightCls() {
    	if (systemRightCls == null) {
    		systemRightCls = getClassByName("SystemRight");
    	}
    	return systemRightCls;
    }

    public Map<Long, Pair<Long,Map<Long,Pair<Long, Object[]>>>> getDataChangesCache() {
    	return dataChangesCache;
    }

	public KrnObject getFilterObject(long fid, Driver drv) throws DriverException {
		synchronized (objByFid) {
			KrnObject obj = objByFid.get(fid);
			if (obj == null) {
				obj = drv.getObjectById(fid);
				objByFid.put(fid, obj);
			}
			return obj;
		}
	}

	public String getFilterSql(KrnObject fobj, Driver drv, long trId) throws DriverException, UnsupportedEncodingException {
		String sql = sqlByFuid.get(fobj.id);
		if (sql == null) {
			KrnClass flrCls = getClassByName("Filter");
			KrnAttribute sqlAttr = getAttributeByName(flrCls.id, "exprSql");
			byte[] data = (byte[]) drv.getValue(fobj.id, sqlAttr.id, 0, 0, trId);
			sql = (data != null && data.length > 0) ? new String(data, 0, data.length, "UTF-8") : "";
			sqlByFuid.put(fobj.id, sql);
		}
		return sql;
	}

	public void reloadFilter(long fid, UserSession us) throws DriverException {
		ServerMessage.sendReloadFilter(us.getDsName(), fid);
	}
	
	public void removeFilter(long fid) throws DriverException {
		sqlByFuid.remove(fid);
	}

	public KrnAttribute[] getRevAttributes(long attrId) {
		KrnAttribute[] attrs = rattrsByAttrId.get(attrId);
    	return attrs != null ? attrs : EMPTY_ATTR_ARRAY;
	}
	
	public void reloadBlobDirs(Driver drv) throws DriverException {
		synchronized (blobDirs) {
			blobDirs.clear();
			KrnClass dirCls = getClassByName("FSDirectory");
			if (dirCls != null) {
				AttrRequestBuilder dirRb = new AttrRequestBuilder(dirCls, null)
				.add(getAttributeByName(dirCls.id, "url"));
	        	List<Object[]> rows = drv.getObjects(dirCls.id, null, dirRb.build(), 0, new int[1], 0, null,null);
				for (Object[] row : rows) {
					blobDirs.put(dirRb.getObject(row).id, dirRb.getStringValue("url", row));
				}
			}
		}
	}
	
	public String getBlobDir(long dirId) {
		return blobDirs.get(dirId);
	}

	public void disableFGACRules(Driver driver, UserSession us, List<Long> disabledRules) {
		if (driver instanceof OracleDriver3) {
			OracleDriver3 drv = (OracleDriver3) driver;
			for (long id : fgacByObjectId.keySet()) {
				FGACRule rule = fgacByObjectId.get(id);
				if (!rule.isBlocked()) {
					disabledRules.add(id);
					drv.enablePolicy(rule, false, us);
				}
			}
		}
	}
	
	public void enableFGACRules(Driver driver, UserSession us, List<Long> disabledRules) {
		if (driver instanceof OracleDriver3) {
			OracleDriver3 drv = (OracleDriver3) driver;
			for (long id : disabledRules) {
				FGACRule rule = fgacByObjectId.get(id);
				drv.enablePolicy(rule, true, us);
			}
		}
	}
		
	public List<String> getFlowsByControlDate(Driver2 drv,boolean onlyExist,Map<Long,String> pdimap){
    	Connection conn=drv.getConnection();
        PreparedStatement pst = null,ust=null;
        ResultSet rs = null;
        List<String> res = new ArrayList<String>();
        Timestamp currDate = Funcs.convertToSqlTime(new Date(System.currentTimeMillis()));
        try {
        	if(onlyExist){
	        	pst=conn.prepareStatement(DATACONTROL_EXIST_SQL);
	            rs = pst.executeQuery();
	            while(rs.next()) {
	            	long objId=rs.getLong("c_obj_id");
	            	long factor=rs.getLong("factor");
	            	res.add(""+objId+";"+factor);
	            }
	            if(pdimap!=null && pdimap.size()>0){
	            	String sql_taskColor=TASKCOLOR_EXIST_SQL+" AND p."+PROCESSDEFINITION_tname+" IN (";
	            	boolean parsql=true;
		        	int i=0;
	            	while(i++<pdimap.size()){
	            		if(parsql){
		            		sql_taskColor +="?";
			            	parsql=false;
	            		}else
	            			sql_taskColor +=",?";
	            	}
	            	sql_taskColor += ")";
	            	rs.close();
	            	pst.close();
		        	pst=conn.prepareStatement(sql_taskColor);
		        	i=1;
	            	for(Long id:pdimap.keySet()){
	            		pst.setLong(i++, id);
	            	}
		            rs = pst.executeQuery();
		            while(rs.next()) {
		            	long pdobjId=rs.getLong("pdobj");
		            	String fnode=rs.getString("fnode");
		            	String node_s=pdimap.get(pdobjId);
		            	if(fnode.contains(";"+node_s.substring(0,node_s.indexOf(";")))){
			            	long objId=rs.getLong("fobj");
			            	long factor=rs.getLong("factor");
			            	res.add(""+objId+";"+factor+node_s.substring(node_s.indexOf(";")));
		            	}
		            }
	            }
        	}else{
	        	pst=conn.prepareStatement(DATACONTROL_SQL);
	       		pst.setTimestamp(1, currDate);
	            rs = pst.executeQuery();
	            while(rs.next()) {
	            	int permit=Constants.ACT_ALERT;
	            	long objId=rs.getLong("c_obj_id");
	            	long factor=rs.getLong("factor");
	            	long fpermit=rs.getLong("fpermit");
	            	Timestamp dAlert=rs.getTimestamp("fcontrol");
	            	Timestamp dAlarm=null;
	            	String fstatus=rs.getString("fstatus");
	            	if(fstatus!=null){
	            		try{
	            			dAlarm= new Timestamp(dAlert.getTime()+Long.parseLong(fstatus));
	                    	if(dAlarm.before(currDate)){
	                        	permit=Constants.ACT_ALARM;
	                    	}
	            		}catch(NumberFormatException nfe){
	                        log.error(nfe, nfe);
	            		}
	            	}else{
	                	permit=Constants.ACT_ALARM;
	            	}
	            	if((fpermit & permit)!=permit){
		            	try{
		                	ust=conn.prepareStatement(PERMIT_UPDATE_SQL);
		            		ust.setLong(1,fpermit | permit);
		            		ust.setLong(2,objId);
		            		int r=ust.executeUpdate();
		                    conn.commit();
		                    log.info("UPDATE PERMIT_ALARM_ALERT:"+r);
		                } catch (SQLException ex) {
		                    log.error(ex, ex);
		        		} finally {
		                    DbUtils.closeQuietly(ust);
		                }
	            	}
	            	res.add(""+objId+";"+factor);
	            }
        	}
        } catch (SQLException e) {
            log.error(e, e);
		} finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(pst);
        }
        return res;
	}
	public List<Long> findProcessByUiType(String uiType,UserSession user,Driver2 drv){
    	Connection conn=drv.getConnection();
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Long> res = new ArrayList<Long>();
        try {
        	pst=conn.prepareStatement(FLOWBYUITYPE_SQL);
    		pst.setString(1,uiType);
            rs = pst.executeQuery();
            while(rs.next()) {
               	res.add(rs.getLong("c_obj_id"));
            }
        } catch (SQLException e) {
            log.error(e, e);
		} finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(pst);
        }
        return res;
	}
	public List<Long> findForeignProcess(long defId,long objId,UserSession user,Driver2 drv){
    	Connection conn=drv.getConnection();
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Long> res = new ArrayList<Long>();
        try {
        	KrnObject obj=drv.getObjectById(objId);
        	if (defId == -1) {
            	pst = conn.prepareStatement(FLOWBYCUTOBJ_SQL2);
        		pst.setString(1, "%" + obj.uid + "%");
        	} else {
        		pst=conn.prepareStatement(FLOWBYCUTOBJ_SQL);
        		pst.setLong(1, defId);//процесс
        		pst.setLong(2, defId);//супер-процесс
        		pst.setLong(3, defId);//супур-супер-процесс
        		pst.setString(4,"%"+obj.uid+"%");
        	}
            rs = pst.executeQuery();
            while(rs.next()) {
            	res.add(rs.getLong(1));
            }
        } catch (Throwable e) {
        	log.error("defId:"+defId+";objId:"+objId+";res:" + res);
			log.error(e, e);
		} finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(pst);
        }
        return res;
	}
	public long[] getProcessDefinitions(UserSession user,Driver2 drv){
		
    	Connection conn=drv.getConnection();
        Statement st = null;
        ResultSet rs = null;
        List<Long> res = new ArrayList<Long>();
        try {
        		st=conn.createStatement();
                rs = st.executeQuery(PROCDEFS_SQL);
                while(rs.next()) {
                	res.add(rs.getLong("c_obj_id"));
               }
        } catch (SQLException e) {
            log.error(e, e);
		} finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(st);
        }
        return Funcs.makeLongArray(res);
	}
	public long findFlowByCorelId(String corelId,Driver2 drv){
		
    	Connection conn=drv.getConnection();
        PreparedStatement pst = null;
        ResultSet rs = null;
        long flowId= -1;
        try {
        		pst=conn.prepareStatement(CORELID_SQL);
        		pst.setString(1, corelId);
                rs = pst.executeQuery();
                if(rs.next()) {
                	flowId=rs.getLong("c_obj_id");
               }
        } catch (SQLException e) {
            log.error(e, e);
		} finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(pst);
        }
        return flowId;
	}

	public long getSubFlowTask(long superFlowTaskId,Driver2 drv){
		
    	Connection conn=drv.getConnection();
        PreparedStatement pst = null;
        ResultSet rs = null;
        long flowId= -1;
        try {
        		pst=conn.prepareStatement(SUBFLOW_SQL);
        		pst.setLong(1, superFlowTaskId);
                rs = pst.executeQuery();
                if(rs.next()) {
                	flowId=rs.getLong("prootflow");
               }
        } catch (SQLException e) {
            log.error(e, e);
		} finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(pst);
        }
        return flowId;
	}
	public List<String> getFlowsToRemove(long pdId,long roleId,Driver2 drv){
		
    	Connection conn=drv.getConnection();
        PreparedStatement pst = null;
        ResultSet rs = null;
        long flowId= -1;
        List<String> ress=new ArrayList<>();
        try {
        		pst=conn.prepareStatement(FLOWS_FOR_REMOVE_SQL);
        		pst.setLong(1, roleId);
        		pst.setLong(2, pdId);
                rs = pst.executeQuery();
                
                while(rs.next()) {
                	String res="";
                	res+=""+rs.getLong("fobjid");
                	res+=","+rs.getLong("ptid");
                	res+=","+rs.getLong("pobjid");
                	ress.add(res);
               }
        } catch (SQLException e) {
            log.error(e, e);
		} finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(pst);
        }
        return ress;
	}
	public long[] getFlowsByEvent(String event,String boxId,Driver2 drv){
		
    	Connection conn=drv.getConnection();
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Long> res = new ArrayList<Long>();
        try {
        		pst=conn.prepareStatement(FLOWEVENT_SQL);
        		pst.setString(1, event);
                rs = pst.executeQuery();
                while(rs.next()) {
                	res.add(rs.getLong("c_obj_id"));
               }
        } catch (SQLException e) {
            log.error(e, e);
		} finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(pst);
        }
        return Funcs.makeLongArray(res);
	}
	
	public long getTasksCount(UserSession user, Date start, Date end, long flowId, Driver2 drv,long beginRow,long endRow, boolean isCheckEvent, String searchText, String interfaceLangId) {
    	Connection conn=drv.getConnection();
        PreparedStatement pst = null;
        ResultSet rs = null;
        long res=0;
        List<Long> actors = new ArrayList<Long>();
        String USERPARENT_SQL_RES="";
        String sql=USERPARENT_SQL;
        //Выборка всех ролей для рядового пользователя
    	if(user!=null  && !user.isAdmin()){
    		if(drv instanceof OracleDriver3){
		    	try {
		    	  	int ipar=1;
		    		pst = conn.prepareStatement(sql);
		        		if(drv instanceof OracleDriver3)
		        		pst.setLong(ipar, user.getUserId());
		        		else{
		        		pst.setLong(ipar++, user.getUserId());
		        		pst.setLong(ipar++, user.getUserId());
		        		}
		        		USERPARENT_SQL_RES+="?";
		                rs = pst.executeQuery();
		                while(rs.next()) {
		                	actors.add(rs.getLong(1));
		            		USERPARENT_SQL_RES+=",?";
		                }
		    		
		        } catch (SQLException e) {
		        	log.error(sql);
		            log.error(e, e);
				} finally {
		            DbUtils.closeQuietly(rs);
		            DbUtils.closeQuietly(pst);
		        }
    		}else{
    			//Рекурсивно выбираю всех парентов
	        	String sql_mysql=sql+"(?)";
    	    	try {
    	    	  	int ipar=1;
    	    		pst = conn.prepareStatement(sql_mysql);
    	        		pst.setLong(ipar, user.getUserId());
    	        		USERPARENT_SQL_RES+="?";
    	                rs = pst.executeQuery();
    	                while(rs.next()) {
    	                	actors.add(rs.getLong(1));
    	            		USERPARENT_SQL_RES+=",?";
    	                }
    	    		
    	        } catch (SQLException e) {
    	        	log.error(sql);
    	            log.error(e, e);
    			} finally {
    	            DbUtils.closeQuietly(rs);
    	            DbUtils.closeQuietly(pst);
    	        }
    	    	List<Long> actorsp=new ArrayList<Long>();
    	    	actorsp.addAll(actors);
    	        while(actorsp.size()>0){
    	        	sql_mysql=sql+"(";
    	        	int i=0;
    	        	for(long actorp:actorsp){
    	        		if(i>0)
    	        			sql_mysql+=",?";
    	        		else
    	        			sql_mysql+="?";
    	        		i++;
    	        	}
        			sql_mysql+=")";
	    	    	try {
	    	    	  	int ipar=1;
	    	    		pst = conn.prepareStatement(sql_mysql);
	    	        	for(long actorp:actorsp)
	    	        		pst.setLong(ipar++, actorp);
	    	                rs = pst.executeQuery();
	    	                actorsp.clear();
	    	                while(rs.next()) {
	    	                	long actorp=rs.getLong(1);
	    	                	if(!actors.contains(actorp)){
	    	                		actors.add(actorp);
	    	                		actorsp.add(actorp);
	        	            		USERPARENT_SQL_RES+=",?";
	    	                	}
	    	                }
	    	    		
	    	        } catch (SQLException e) {
	    	        	log.error(sql);
	    	            log.error(e, e);
	    			} finally {
	    	            DbUtils.closeQuietly(rs);
	    	            DbUtils.closeQuietly(pst);
	    	        }
    	        }
    		}
    			
        }
        sql=TASK_COUNT_SQL;
        if(start!=null && end !=null && !start.before(end)){
        	end=new KrnDate(start.getTime()+24*3600*1000);
        }
        if(user!=null && !user.isAdmin()){
        	sql += " AND (f."+ACTOR_tname+" IN ("+USERPARENT_SQL_RES+") "
        			//для того чтобы наблюдатель мог видить свои задания
        			+ (isObserversAllow ? "OR p."+OBSERVERS_tname+" Like ?)":")");
        }else if(user!=null && user.isAdmin() && user.getBalansId()>0){
        	//Администратор должен видить задания всех пользователей своей балансовой единицы
        	sql += " AND f."+ACTOR_tname+" IN ("+USERSADM_SQL+")";
        }
    	if(start!=null){
    		sql += " AND f."+CURRENT_tname+" >= ?";
    	}
    	if(end!=null){
    		sql += " AND f."+CURRENT_tname+" <= ?";
    	}
    	if ((user == null || !user.isAdmin()) && isCheckEvent){
   			//Чтобы пользователь видил упавшие процессы
    		if(drv instanceof OracleDriver3)
        		sql += " AND f."+TYPENODE_tname+" IS NOT NULL AND f."+TYPENODE_tname+" NOT IN ('join')";
    		else
    			sql += " AND f."+TYPENODE_tname+" IS NOT NULL AND f."+TYPENODE_tname+" NOT IN ('','join')";
   		}
    	
    	if (searchText != null && searchText.length() > 0) {
      		sql += " AND (";
    		if (drv instanceof OracleDriver3) {
    			sql += "NVL(f." + FLOW_TITLE_tname + "_" + interfaceLangId + ", ' ')";
    		} else if (drv instanceof MsSqlDriver3) {
    			sql += "ISNULL(f." + FLOW_TITLE_tname + "_" + interfaceLangId + ", '')";
    		} else if (drv instanceof PgSqlDriver) {
    			sql += "COALESCE(f." + FLOW_TITLE_tname + "_" + interfaceLangId + ", '')";
    		} else {
    			sql += "IFNULL(f." + FLOW_TITLE_tname + "_" + interfaceLangId + ", '')";
    		}
    		
    		if (drv instanceof PgSqlDriver)
    			sql += " ~* ? OR f." + TITLEOBJ_tname + "_" + interfaceLangId + " ~* ? OR pd." + PD_TITLE_tname + "_" + interfaceLangId + " ~* ? OR u." + USER_SIGN_tname + "_" + interfaceLangId + " ~* ? OR ";
    		else
    			sql += " LIKE ? OR f." + TITLEOBJ_tname + "_" + interfaceLangId + " LIKE ? OR pd." + PD_TITLE_tname + "_" + interfaceLangId + " LIKE ? OR u." + USER_SIGN_tname + "_" + interfaceLangId + " LIKE ? OR ";

    		if (drv instanceof OracleDriver3 || drv instanceof PgSqlDriver) {
    			sql += "TO_CHAR(f." + CURRENT_tname + ", 'DD.MM.YYYY HH24:MI:SS')";
    		} else if (drv instanceof MsSqlDriver3) {
    			sql += "FORMAT(f." + CURRENT_tname + ", 'dd.MM.yyyy hh:mm:ss')";
    		} else {
    			sql += "DATE_FORMAT(f." + CURRENT_tname + ", '%d.%m.%Y %T')";
    		}
    		if (drv instanceof PgSqlDriver)
    			sql += " ~* ?)";
    		else
    			sql += " LIKE ?)";
    	}
    	
    	try {
    	  	int ipar=1;
    		pst = conn.prepareStatement(sql);
        	if(user!=null  && !user.isAdmin()){
        		pst.setLong(ipar++, user.getUserId());
        		for(long parentId:actors){
        			pst.setLong(ipar++, parentId);
        		}
        		if(isObserversAllow)
        			pst.setString(ipar++, "%"+user.getUserId()+"%");
            }else if(user!=null && user.isAdmin() && user.getBalansId()>0){
        		pst.setLong(ipar++, user.getBalansId());
            }
        	if(start!=null){
        		pst.setTimestamp(ipar++, Funcs.convertToSqlTime(start));
        	}
        	if(end!=null){
        		pst.setTimestamp(ipar++, Funcs.convertToSqlTime(end));
        	}
        	if(flowId!=-1){
        		pst.setLong(ipar++, flowId);
        	}
        	
        	if (searchText != null && searchText.length() > 0) {
        		for (int i = 0; i < 5; i++) {
        			if (drv instanceof PgSqlDriver)
        				pst.setString(ipar++, searchText);
        			else
        				pst.setString(ipar++, "%" + searchText + "%");
        		}
        	}
        	
            rs = pst.executeQuery();
			while (rs.next()) {
				String typenode = rs.getString("ftypenode");
				long permit = rs.getLong("fpermit");
				if ("process-state".equals(typenode) && (permit & Constants.ACT_ERR) != Constants.ACT_ERR) {
					continue;
				}
				res++;
			}
        } catch (SQLException e) {
        	log.error(sql);
            log.error(e, e);
		} finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(pst);
        }
        return res;
    }

	public List<Activity> getTasks(UserSession user, Date start, Date end, long flowId, Driver2 drv,long beginRow,long endRow, boolean isCheckEvent, boolean onlyMy, String orderBy, boolean desc, String searchText, String interfaceLangId){
    	Connection conn=drv.getConnection();
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Long> testFlows = new ArrayList<Long>();
        List<Activity> res = new ArrayList<Activity>();
        List<Long> actors = new ArrayList<Long>();
        String USERPARENT_SQL_RES="";
        String sql=USERPARENT_SQL;
        //Выборка всех ролей для рядового пользователя
    	if(user!=null  && !user.isAdmin()){
    		if(drv instanceof OracleDriver3){
		    	try {
		    	  	int ipar=1;
		    		pst = conn.prepareStatement(sql);
		        		if(drv instanceof OracleDriver3)
		        		pst.setLong(ipar, user.getUserId());
		        		else{
		        		pst.setLong(ipar++, user.getUserId());
		        		pst.setLong(ipar++, user.getUserId());
		        		}
		        		USERPARENT_SQL_RES+="?";
		                rs = pst.executeQuery();
		                while(rs.next()) {
		                	actors.add(rs.getLong(1));
		            		USERPARENT_SQL_RES+=",?";
		                }
		    		
		        } catch (SQLException e) {
		        	log.error(sql);
		            log.error(e, e);
				} finally {
		            DbUtils.closeQuietly(rs);
		            DbUtils.closeQuietly(pst);
		        }
    		}else{
    			//Рекурсивно выбираю всех парентов
	        	String sql_mysql=sql+"(?)";
    	    	try {
    	    	  	int ipar=1;
    	    		pst = conn.prepareStatement(sql_mysql);
    	        		pst.setLong(ipar, user.getUserId());
    	        		USERPARENT_SQL_RES+="?";
    	                rs = pst.executeQuery();
    	                while(rs.next()) {
    	                	actors.add(rs.getLong(1));
    	            		USERPARENT_SQL_RES+=",?";
    	                }
    	    		
    	        } catch (SQLException e) {
    	        	log.error(sql);
    	            log.error(e, e);
    			} finally {
    	            DbUtils.closeQuietly(rs);
    	            DbUtils.closeQuietly(pst);
    	        }
    	    	List<Long> actorsp=new ArrayList<Long>();
    	    	actorsp.addAll(actors);
    	        while(actorsp.size()>0){
    	        	sql_mysql=sql+"(";
    	        	int i=0;
    	        	for(long actorp:actorsp){
    	        		if(i>0)
    	        			sql_mysql+=",?";
    	        		else
    	        			sql_mysql+="?";
    	        		i++;
    	        	}
        			sql_mysql+=")";
	    	    	try {
	    	    	  	int ipar=1;
	    	    		pst = conn.prepareStatement(sql_mysql);
	    	        	for(long actorp:actorsp)
	    	        		pst.setLong(ipar++, actorp);
	    	                rs = pst.executeQuery();
	    	                actorsp.clear();
	    	                while(rs.next()) {
	    	                	long actorp=rs.getLong(1);
	    	                	if(!actors.contains(actorp)){
	    	                		actors.add(actorp);
	    	                		actorsp.add(actorp);
	        	            		USERPARENT_SQL_RES+=",?";
	    	                	}
	    	                }
	    	    		
	    	        } catch (SQLException e) {
	    	        	log.error(sql);
	    	            log.error(e, e);
	    			} finally {
	    	            DbUtils.closeQuietly(rs);
	    	            DbUtils.closeQuietly(pst);
	    	        }
    	        }
    		}
    			
        }
        sql=TASK_SQL;
        if(start!=null && end !=null && !start.before(end)){
        	end=new KrnDate(start.getTime()+24*3600*1000);
        }
        if (user!=null && !user.isAdmin() && onlyMy) {
        	sql += " AND (f."+ACTOR_tname+" IN ("+USERPARENT_SQL_RES+") "
        			//для того чтобы наблюдатель мог видить свои задания
        			+ (isObserversAllow ? "OR p."+OBSERVERS_tname+" Like ?)":")");
        } else if (user != null && (user.isAdmin() || !onlyMy) && user.getBalansId() > 0){
        	//Администратор должен видить задания всех пользователей своей балансовой единицы
        	sql += " AND f."+ACTOR_tname+" IN ("+USERSADM_SQL+")";
        }
    	if(start!=null){
    		sql += " AND f."+CURRENT_tname+" >= ?";
    	}
    	if(end!=null){
    		sql += " AND f."+CURRENT_tname+" <= ?";
    	}
    	if ((user == null || !user.isAdmin()) && isCheckEvent){
   			//Чтобы пользователь видил упавшие процессы
    		if(drv instanceof OracleDriver3)
        		sql += " AND f."+TYPENODE_tname+" IS NOT NULL AND f."+TYPENODE_tname+" NOT IN ('join')";
    		else
    		sql += " AND f."+TYPENODE_tname+" IS NOT NULL AND f."+TYPENODE_tname+" NOT IN ('','join')";
   		}
    	
    	if (searchText != null && searchText.length() > 0) {
    		sql += " AND (";
    		if (drv instanceof OracleDriver3) {
    			sql += "NVL(f." + FLOW_TITLE_tname + "_" + interfaceLangId + ", ' ')";
    		} else if (drv instanceof MsSqlDriver3) {
    			sql += "ISNULL(f." + FLOW_TITLE_tname + "_" + interfaceLangId + ", '')";
    		} else if (drv instanceof PgSqlDriver) {
    			sql += "COALESCE(f." + FLOW_TITLE_tname + "_" + interfaceLangId + ", '')";
    		} else {
    			sql += "IFNULL(f." + FLOW_TITLE_tname + "_" + interfaceLangId + ", '')";
    		}
    		
    		if (drv instanceof PgSqlDriver)
    			sql += " ~* ? OR f." + TITLEOBJ_tname + "_" + interfaceLangId + " ~* ? OR pd." + PD_TITLE_tname + "_" + interfaceLangId + " ~* ? OR u." + USER_SIGN_tname + "_" + interfaceLangId + " ~* ? OR ";
    		else
    			sql += " LIKE ? OR f." + TITLEOBJ_tname + "_" + interfaceLangId + " LIKE ? OR pd." + PD_TITLE_tname + "_" + interfaceLangId + " LIKE ? OR u." + USER_SIGN_tname + "_" + interfaceLangId + " LIKE ? OR ";
    		
    		if (drv instanceof OracleDriver3 || drv instanceof PgSqlDriver) {
    			sql += "TO_CHAR(f." + CURRENT_tname + ", 'DD.MM.YYYY HH24:MI:SS')";
    		} else if (drv instanceof MsSqlDriver3) {
    			sql += "FORMAT(f." + CURRENT_tname + ", 'dd.MM.yyyy hh:mm:ss')";
    		} else {
    			sql += "DATE_FORMAT(f." + CURRENT_tname + ", '%d.%m.%Y %T')";
    		}
    		if (drv instanceof PgSqlDriver)
    			sql += " ~* ?)";
    		else
    			sql += " LIKE ?)";
    	}
    	
    	if(flowId!=-1){
    		sql += " AND f.c_obj_id = ?";
    	}else if(!(drv instanceof MsSqlDriver3) || beginRow<0 || endRow<=0 || endRow<beginRow){
    		if ("date".equals(orderBy))
    			sql += " ORDER BY fcurrent";
    		else if ("cdate".equals(orderBy))
    			sql += " ORDER BY fcontrol";
    		else if ("taskName".equals(orderBy))
    			sql += " ORDER BY ftitle1";
    		else if ("processName".equals(orderBy))
    			sql += " ORDER BY pdtitle1";
    		else if ("error".equals(orderBy))
    			sql += " ORDER BY ferror";
    		else
    			sql += " ORDER BY fcurrent";
    		
    		if (desc) sql += " DESC";
    	}
    	//Постраничная выборка
		long startTime=System.currentTimeMillis();
    	if(beginRow>=0 && endRow>=0 && endRow>=beginRow){
    		if(drv instanceof OracleDriver3){
    			sql="SELECT * FROM (SELECT m1.*, ROWNUM rn FROM ("+sql+") m1 WHERE ROWNUM <= ?) WHERE rn >= ?";
    		}else if(drv instanceof MsSqlDriver3){
    			sql="SELECT * FROM (SELECT ROW_NUMBER() OVER (ORDER BY f."+CURRENT_tname+" DESC) ROWNUM,"+sql.substring(7)+") m1 WHERE m1.ROWNUM BETWEEN (?) AND (?)";
    		}else if(drv instanceof PgSqlDriver){
        		sql += " LIMIT ? OFFSET ?";
    		}else if(drv instanceof MySqlDriver3){
        		sql += " LIMIT ?,?";
    		}
    	}

    	try {
        	  	int ipar=1;
        		pst = conn.prepareStatement(sql);
            	if(user !=null && !user.isAdmin() && onlyMy){
            		pst.setLong(ipar++, user.getUserId());
            		for(long parentId:actors){
            			pst.setLong(ipar++, parentId);
            		}
            		if(isObserversAllow)
            			pst.setString(ipar++, "%"+user.getUserId()+"%");
                } else if (user != null && (user.isAdmin() || !onlyMy) && user.getBalansId()>0){
            		pst.setLong(ipar++, user.getBalansId());
                }
            	if(start!=null){
            		pst.setTimestamp(ipar++, Funcs.convertToSqlTime(start));
            	}
            	if(end!=null){
            		pst.setTimestamp(ipar++, Funcs.convertToSqlTime(end));
            	}
            	if(flowId!=-1){
            		pst.setLong(ipar++, flowId);
            	}
            	
            	if (searchText != null && searchText.length() > 0) {
            		for (int i = 0; i < 5; i++) {
            			if (drv instanceof PgSqlDriver)
            				pst.setString(ipar++, searchText);
            			else
            				pst.setString(ipar++, "%" + searchText + "%");
            		}
            	}
            	
            	if(beginRow>=0 && endRow>=0 && endRow>=beginRow){
            		if(drv instanceof OracleDriver3){
            		pst.setLong(ipar++, endRow);
            		pst.setLong(ipar++, beginRow);
            		}else if(drv instanceof MsSqlDriver3){
                		pst.setLong(ipar++, beginRow);
                		pst.setLong(ipar++, endRow);
            		}else if(drv instanceof PgSqlDriver){
                		pst.setLong(ipar++, endRow-beginRow+(beginRow>0?1:0));
                		pst.setLong(ipar++, beginRow-(beginRow>0?1:0));
            		}else if(drv instanceof MySqlDriver3){
                		pst.setLong(ipar++, beginRow-(beginRow>0?1:0));
                		pst.setLong(ipar++, endRow-beginRow+(beginRow>0?1:0));
            		}
            	}
                rs = pst.executeQuery();
                while(rs.next()) {
                	String typenode = rs.getString("ftypenode");
                	long permit = rs.getLong("fpermit");
                	String base_=rs.getString("fbase");
                	//исключаем все потоки не принадлежащие той базе которой принадлежит пользователь(кроме администраторов)
                	if(user!=null && !user.isAdmin() && base_!=null && !"".equals(base_)) {
                		try {
	                		long base=Long.valueOf(base_);
	                		if(user.getBaseObj()==null || user.getBaseObj().id!=base)
	                			continue;
                		}catch(Exception e) {
                			log.error(e,e);
                		}
                	}
                	//
                	if ("process-state".equals(typenode) && !user.isAdmin() && (permit & Constants.ACT_ERR) != Constants.ACT_ERR) {
                		continue;
                	}
                    Activity activity = new Activity();
                    activity.flowId = rs.getLong("fcobjid");
                    activity.rootFlowId = rs.getLong("prootflow");
                    long sp0=rs.getLong("psuperflow");
                    long sp1=rs.getLong("p1superflow");
                    long sp2=rs.getLong("p2superflow");
                    if(sp2>0){
                        activity.superFlowIds = new long[]{sp0,sp1,sp2};
                    }else if(sp1>0){
                            activity.superFlowIds = new long[]{sp0,sp1};
                    }else if(sp0>0){
                        activity.superFlowIds = new long[]{sp0};
                    }
                    String nodes_=Funcs.normalizeInput(rs.getString("fnode"));
                	long [] nodesId=null;
                    if(nodes_!=null && !"".equals(nodes_)){
                    	String[] nodes=nodes_.split(";");
                    	nodesId = new long[nodes.length];
                        for(int i=0;i<nodes.length;i++){
                        	try{
                        		nodesId[i]=Long.valueOf("".equals(nodes[i])?"-1":nodes[i]);
	                    	}catch(Exception e){
	                    		nodesId[i]=-1;
	                    		log.error(e,e);
	                    	}
                        }
                    	
                    }
                    String nodes_p=Funcs.normalizeInput(rs.getString("fpnode"));
                	long [] nodespId=null;
                    if(nodes_p!=null && !"".equals(nodes_p)){
                    	String[] nodesp=nodes_p.split(";");
                    	nodespId = new long[nodesp.length];
                        for(int i=0;i<nodesp.length;i++){
                        	try{
                        		nodespId[i]=Long.valueOf("".equals(nodesp[i])?"-1":nodesp[i]);
	                    	}catch(Exception e){
	                    		nodespId[i]=-1;
	                    		log.error(e,e);
	                    	}
                        }
                    	
                    }
                    if(nodesId==null && nodespId==null){
                        activity.nodesId = new long[0][0];
                    }else if(nodespId==null){
                        activity.nodesId = new long[1][];
                        activity.nodesId[0]=nodesId;
                    }else if(nodesId==null){
                        activity.nodesId = new long[2][];
                        activity.nodesId[0]=new long[0];
                        activity.nodesId[1]=nodespId;
                    }else{
                        activity.nodesId = new long[2][];
                        activity.nodesId[0]=nodesId;
                        activity.nodesId[1]=nodespId;
                    }
                    activity.trId=rs.getLong("ptransid");
                    String uids_ = Funcs.normalizeInput(rs.getString("fcutobj"));
            		if("-1".equals(uids_)){
            			activity.objs=new KrnObject[0];
            		}else if(uids_!=null && !"".equals(uids_)){
	                	String[] uids=uids_.split(";");
	                	activity.objs = new KrnObject[uids.length];
	                    for(int i=0;i<uids.length;i++){
	                    	String uid[]=uids[i].split(",");
	                    	if(uid.length>1){
	                    		try{
	                    			activity.objs[i]=new KrnObject(Long.parseLong(uid[0]),uid[1],Long.parseLong(uid[2]));
		                        }catch(NumberFormatException ex){
		                        	log.error("Ошибка при распарсивании объекта обработки:"+activity.flowId+";uid="+uids[i]);
		                        }
	                    	}else{
		                    	try{
		                    			activity.objs[i]=drv.getObjectByUid(uids[i],-1);
		                    	}catch(Exception e){
		                    		activity.objs[i]=new KrnObject(-1, "", -1);
		                    		log.error("FlowId:"+activity.flowId+"- Object with uid = '"+uids[i]+"' not founde");
		                    	}
	                    	}
	                    }
                    }else
                		activity.objs = new KrnObject[]{new KrnObject(-1, "", -1)};
                    	
                    String ui = Funcs.normalizeInput(rs.getString("fui"));
                    String tui="";//тип интерфейса
                    long cui=0;// цвет строки задания записан через ';' после типа интерфейса
                    String tcui=Funcs.normalizeInput(rs.getString("ftypeui"));
                    try{
	                    if(tcui!=null && !"".equals(tcui)){
	                    	String tcui_[]=tcui.split(";");
	                    	tui=tcui_[0];
	                    	if(tcui_.length>1 && !"".equals(tcui_[1]) && !"null".equals(tcui_[1])){
	                    		cui=Long.parseLong(tcui_[1]);
	                    	}
	                    }
                    }catch(NumberFormatException ex){
                    	log.error("Ошибка при распарсивании цвета строки задания:"+activity.flowId+";tcui="+tcui);
                    }
                    String t5="";
                    long factor=rs.getLong("factor");
                    activity.actorId=factor;
                    if(activity.actorId != 0 && showUserDB) {
                    	String ubName = rs.getString("ubName");
                    	activity.userBase_name = ubName;
                    }
                    if(ui!=null && !"".equals(ui)//для наблюдателя интерфейс для корректировки недоступен
                    		&& (user.isAdmin() || factor==user.getUserId() || actors.contains(factor))){
                    	String ui_[]=ui.split(",");
                    	if(ui_.length>1){
                    		try{
                    			activity.setUI(new KrnObject(Long.parseLong(ui_[0]),ui_[1],Long.parseLong(ui_[2])));
	                        }catch(NumberFormatException ex){
	                        	log.error("Ошибка при распарсивании интерфейса:"+activity.flowId+";ui="+ui);
	                        }
                    	}else{
	                    	try{
	                    		activity.setUI(drv.getObjectByUid(ui,-1));
	                    	}catch(Exception e){
	                    		activity.setUI(new KrnObject(-1, "", -1));
	                    		log.error("FlowId:"+activity.flowId+"- Object with uid = '"+ui+"' not founde");
	                    	}
                    	}
                    	if(ui_.length>3) t5=ui_[3];
                        activity.uiType=tui;
                    	
                    }else{
                        activity.setUI(new KrnObject(-1, "", -1));
                    }
                    if(activity.ui!=null) {
                        activity.uiType=tui;
                    }else
                    	activity.uiType="";
                	activity.color=cui;//цвет строки задания
                    String obss=Funcs.normalizeInput(rs.getString("pobservers"));
                    String infUi=Funcs.normalizeInput(rs.getString("puiobservers"));
                    if(infUi!=null && !"".equals(infUi) && obss.contains(""+user.getUserId())){
                		try{
	                    	String uiobjs_[]=infUi.split(";");
	                    	if(uiobjs_.length>0){
	                    		//в первом интерфейс
	                        	String ui_[]=uiobjs_[0].split(",");
		                    	if(ui_.length>1){
	                    			activity.infUi=new KrnObject(Long.parseLong(ui_[0]),ui_[1],Long.parseLong(ui_[2]));
		                    	}else{
			                    	try{
			                    		activity.infUi=drv.getObjectByUid(ui_[0],-1);
			                    	}catch(Exception e){
			                    		activity.infUi=new KrnObject(-1, "", -1);
			                    		log.warn("FlowId:"+activity.flowId+"- Object with uid = '"+infUi+"' not founde");
			                    	}
		                    	}
		                    	if(uiobjs_.length>1){
		                    		//в остальных объекты обработки для этого интерфейса
		                            activity.infObjs = new KrnObject[uiobjs_.length-1];
		                            for(int i=1;i<uiobjs_.length;i++){
		                            	String obj_[]=uiobjs_[i].split(",");
		    	                    	if(obj_.length>2){
	    	                    			activity.infObjs[i-1]=new KrnObject(Long.parseLong(obj_[0]),obj_[1],Long.parseLong(obj_[2]));
		    	                    	}
		                            }
		                    	}else
		                            activity.infObjs = new KrnObject[0];
	                    	}
	                        activity.uiTypeInf=Funcs.normalizeInput(rs.getString("ptypeuiobservers"));
	                    }catch(NumberFormatException ex){
	                    	log.error("Ошибка при распарсивании интерфейса наблюдателя:"+activity.flowId+";infUi="+infUi);
	                    }
                    }else{
                        activity.infUi=new KrnObject(-1, "", -1);
                        activity.uiTypeInf="";
                        activity.infObjs = new KrnObject[0];
                    }

                    activity.date = Funcs.convertTime(Funcs.convertTime(rs.getTimestamp("fcurrent")));
	            	String fstatus=rs.getString("fstatus");
	            	Timestamp finalDate=null;
	            	if(fstatus!=null && rs.getTimestamp("fcontrol")!=null){
	            		try{
	            			finalDate= new Timestamp(rs.getTimestamp("fcontrol").getTime()+Long.parseLong(fstatus));
	            		}catch(NumberFormatException nfe){
	                        log.error(nfe, nfe);
	            		}
	            	}
                    activity.controlDate = Funcs.convertTime(Funcs.convertTime(finalDate!=null?finalDate:rs.getTimestamp("fcontrol")));
                    String t1=Funcs.normalizeInput(rs.getString("ftitle1"));
                    String t2=Funcs.normalizeInput(rs.getString("ftitleobj1"));
                    String t3=Funcs.normalizeInput(rs.getString("ftitle1"));
                    String t4=Funcs.normalizeInput(rs.getString("pdtitle1"));
                    activity.titles = new String[]{t1!=null?t1:"",t2!=null?t2:"",t3!=null?t3:"",t4!=null?t4:"",t5!=null?t5:""};
                    long pd0=rs.getLong("pprocessdef");
                    long pd1=rs.getLong("p1processdef");
                    long pd2=rs.getLong("p2processdef");
                    if(pd2>0){
                        activity.processDefId = new long[]{pd0,pd1,pd2};
                    }else if(pd1>0){
                            activity.processDefId = new long[]{pd0,pd1};
                    }else{
                        activity.processDefId = new long[]{pd0};
                    }
                    String trs=Funcs.normalizeInput(rs.getString("ftransition"));
                    if(trs!=null && !"".equals(trs)){
                    	activity.transitions=trs.split(";");
                    }else
                    	activity.transitions=new String[0];
                    activity.param = rs.getLong("fpermit");
                    activity.msg = (activity.nodesId.length>0 && activity.nodesId[0].length > 0) ? ""+activity.nodesId[0][activity.nodesId[0].length-1] : "";
                	activity.userFrom=Funcs.normalizeInput(rs.getString("fstatus"));
                	String userFrom=Funcs.normalizeInput(rs.getString("ufusersign1"));
                	if(userFrom!=null && !"".equals(userFrom) 
                			&& (activity.userFrom==null ||"".equals(activity.userFrom)))
                		activity.userFrom=userFrom;
                	String userInit=Funcs.normalizeInput(rs.getString("uusersign1"));
                	activity.userInit = userInit!=null?userInit:"";
                    if (user!=null && user.isAdmin()){
                    	if((activity.param & Constants.ACT_CANCEL) != Constants.ACT_CANCEL) {
                                activity.param = activity.param | Constants.ACT_CANCEL;
                    	}
                    }
                    //
                    if(testFlows.contains(activity.flowId)){
                    	log.error("Дубликат flow в запросе:"+activity.flowId);
                    }else {
                    	testFlows.add(activity.flowId);
                        res.add(activity);
                    }
                    //
               }
        } catch (SQLException e) {
        	log.error(sql);
            log.error(e, e);
		} finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(pst);
        }
		long endTime=System.currentTimeMillis();
		log.info("Tasks:"+res.size()+";beginRow:"+beginRow+", endRow:"+endRow+";flowId:"+flowId+";Time:"+(endTime-startTime)+" ms");
        if(res.size()==0)
        	res.add(nullActivity);
        return res;
    }
	
	private void initVcsClassIds() {
		
		final KrnClass procCls = CLS_PROCESS_DEF;
		final KrnClass uiCls = CLS_UI;
		final KrnClass flrCls = CLS_FILTER;
		final KrnClass rptCls = CLS_REPORT;
		final List<KrnAttribute> procLst=new ArrayList<KrnAttribute>();
		final List<KrnAttribute> uiLst=new ArrayList<KrnAttribute>();
		final List<KrnAttribute> flrLst=new ArrayList<KrnAttribute>();
		final List<KrnAttribute> rptLst=new ArrayList<KrnAttribute>();
		
		vcsClassIds.add(procCls.id);
		vcsClassIds.add(uiCls.id);
		vcsClassIds.add(flrCls.id);
		vcsClassIds.add(rptCls.id);
		
		vcsDiffAttrIds.add(getAttributeByName(procCls.id, "config").id);
		vcsDiffAttrIds.add(getAttributeByName(procCls.id, "message").id);
		if(!isVcsNotDiffUi) vcsDiffAttrIds.add(getAttributeByName(uiCls.id, "config").id);
		if(!isVcsNotDiffUi) vcsDiffAttrIds.add(getAttributeByName(uiCls.id, "strings").id);
		vcsDiffAttrIds.add(getAttributeByName(flrCls.id, "config").id);
		vcsClsTtlAttr.put(procCls.id, getAttributeByName(procCls.id, "title"));
		vcsClsTtlAttr.put(uiCls.id, getAttributeByName(uiCls.id, "title"));
		vcsClsTtlAttr.put(flrCls.id, getAttributeByName(flrCls.id, "title"));
		vcsClsTtlAttr.put(rptCls.id, getAttributeByName(rptCls.id, "title"));
		procLst.add(getAttributeByName(procCls.id, "title"));
		procLst.add(getAttributeByName(procCls.id, "diagram"));
		procLst.add(getAttributeByName(procCls.id, "message"));
		procLst.add(getAttributeByName(procCls.id, "config"));
		uiLst.add(getAttributeByName(uiCls.id, "title"));
		uiLst.add(getAttributeByName(uiCls.id, "webConfig"));
		uiLst.add(getAttributeByName(uiCls.id, "config"));
		uiLst.add(getAttributeByName(uiCls.id, "strings"));
		flrLst.add(getAttributeByName(flrCls.id, "title"));
		flrLst.add(getAttributeByName(flrCls.id, "className"));
		flrLst.add(getAttributeByName(flrCls.id, "exprSql"));
		rptLst.add(getAttributeByName(rptCls.id, "title"));
		rptLst.add(getAttributeByName(rptCls.id, "config"));
		rptLst.add(getAttributeByName(rptCls.id, "data2"));
		rptLst.add(getAttributeByName(rptCls.id, "ref"));
		rptLst.add(getAttributeByName(rptCls.id, "template2"));
		vcsClsAttrs.put(procCls.id, procLst);
		vcsClsAttrs.put(uiCls.id, uiLst);
		vcsClsAttrs.put(flrCls.id, flrLst);
		vcsClsAttrs.put(rptCls.id, rptLst);
		
	}
	
	public boolean isVcsClass(long clsId) {
		return vcsClassIds.contains(clsId);
	}

	public boolean isVcsDiffAttr(long attrId) {
		return vcsDiffAttrIds.contains(attrId);
	}
	
	public List<KrnAttribute> getVcsAttributes(long clsId){
		return vcsClsAttrs.get(clsId);
	}
	public KrnAttribute getTitleAttribut(long clsId){
		return vcsClsTtlAttr.get(clsId);
	}

	public boolean isDbReadOnly() {
		return dbReadOnly;
	}
	
	public KrnObject getObjectByUid(final String uid, final Driver drv) throws DriverException {
		KrnObject obj = null; 
		synchronized (objCache) {
			obj = objCache.get(uid);
		}
        if (obj != null) {
        	return obj;
        }
        List<KrnObject> objs = drv.getObjectsByUids(new String[] {uid}, 0, false);
        if (objs.size() > 0) {
        	obj = objs.get(0);
    		synchronized (objCache) {
    			objCache.put(uid, obj);
    		}
        	return obj;
        }
        return null;
	}
	
	public void removeObjectFromCache(final String uid) {
		synchronized (objCache) {
			objCache.remove(uid);
		}
	}
	
	public void removeObjectsFromCache(final List<String> uids) {
		synchronized (objCache) {
			for (String uid : uids)
				objCache.remove(uid);
		}
	}

	public boolean isSeparateClass(long classId) {
		return separateClassIds.contains(classId);
	}
	
	public Set<Long> getSeparateClassIds() {
		return separateClassIds;
	}
	
	public KrnClass getRootClass(long classId) {
		KrnClass res = getClassById(classId);
		long parentClassId = res.parentId;
		while (parentClassId > 99) {
			KrnClass parent = getClassById(parentClassId);
			if (!parent.isVirtual()) {
				res = parent;
			}
			parentClassId = parent.parentId;
		}
		return res;
	}

	private void initJcrRepositories() throws DriverException {
		String fileName = Funcs.sanitizeHtml(System.getProperty("jcr.repositories"));
		if (fileName != null) {
			File jcrFile = new File(fileName);
			if (!jcrFile.exists()) {
				throw new DriverException("Ошибка при инициализации JCR-репозитариев. Файл \"" + fileName + "\" не найден");
			}
            SAXBuilder builder = XmlUtil.createSaxBuilder();
            try {
	        	byte[] bytes = Funcs.read(jcrFile);
            	String dataStr = kz.tamur.util.Funcs.normalizeInput(new String(bytes, "UTF-8"));
            	dataStr = kz.tamur.util.Funcs.validate(dataStr);
                ByteArrayInputStream is = new ByteArrayInputStream(dataStr.getBytes("UTF-8"));

            	Element root = builder.build(is).getRootElement();
            	for (Element repXml : (List<Element>)root.getChildren("repository")) {
            		JcrRepositoryConfig config = new JcrRepositoryConfig(repXml);
            		for (Long attrId : config.getAttrIds()) {
            			jcrRepositoryByAttrId.put(attrId, config);
            		}
            		// Проверка соединения
            		try {
	            		Session session = config.getSession(true);
	            		session.logout();
            		} catch (DriverException e) {
            			log.error("Ошибка при проверке соединения с JCR-репозитарием " + config.getName()
    																			+ ", conn: " + config.getConn(true)
    																			+ ", user: " + config.getUser());
            			throw new DriverException("Ошибка при проверке соединения с JCR-репозитарием " + name, 0, e);
            		}
            	}
            	is.close();
            } catch (JDOMException e) {
    			throw new DriverException("Ошибка при инициализации JCR-репозитариев.", 0, e);
            } catch (IOException e) {
    			throw new DriverException("Ошибка при инициализации JCR-репозитариев.", 0, e);
			}
		}
	}
	
	public boolean inJcrRepository(long attrId) {
		if(fileStoreType!=null && !"toJcrRepository".equals(fileStoreType))
			return false;
		return jcrRepositoryByAttrId.containsKey(attrId);
	}

	public JcrRepositoryConfig getJcrRepository(Number attrId) {
		return jcrRepositoryByAttrId.get(attrId.longValue());
	}

	public JcrRepositoryConfig getJcrRepository(long attrId) {
		return jcrRepositoryByAttrId.get(attrId);
	}

	public byte[] getRepositoryData(long attrId, byte[] docId) throws DriverException {
		if (docId != null && docId.length == 36) {
			JcrRepositoryConfig jcrConfig = jcrRepositoryByAttrId.get(attrId);
			return jcrConfig.getRepositoryData(Funcs.normalizeInput(new String(docId)));
		}
		return docId;
	}

	public String getRepositoryData(long attrId, String docId) throws DriverException {
		if (docId != null && docId.length() == 36) {
			JcrRepositoryConfig jcrConfig = jcrRepositoryByAttrId.get(attrId);
			return new String(jcrConfig.getRepositoryData(Funcs.normalizeInput(docId)));
		}
		return docId;
	}

	public byte[] putRepositoryData(long attrId, long objId, long trId, byte[] data) throws DriverException {
		JcrRepositoryConfig jcrConfig = jcrRepositoryByAttrId.get(attrId);
		if (!jcrConfig.isReadOnly()) {
			String docId = jcrConfig.putRepositoryData(objId + "_" + attrId + "_" + trId, data);
			return Funcs.normalizeInput(docId).getBytes();
		}
		return data;
	}

	public String putRepositoryData(long attrId, long objId, long trId, String data) throws DriverException {
		JcrRepositoryConfig jcrConfig = jcrRepositoryByAttrId.get(attrId);
		if (!jcrConfig.isReadOnly()) {
			String docId = jcrConfig.putRepositoryData(objId + "_" + attrId + "_" + trId, data.getBytes());
			return Funcs.normalizeInput(docId);
		}
		return data;
	}

	public boolean deleteRepositoryData(long attrId, String docId) throws DriverException {
		/*
		log.info("Deleting JCR node: " + docId);
		
		if (docId != null && docId.length() == 36) {
			JcrRepositoryConfig jcrConfig = jcrRepositoryByAttrId.get(attrId);
			String name = jcrConfig.deleteRepositoryData(Funcs.normalizeInput(docId));
			if (name != null) {
				log.info("JCR node successfully deleted: " + name);
				return true;
			} else
				log.error("JCR node not foumd: " + docId);
		}*/
		return false;
	}

	public String getUrlConnection(){
		return urlConnection;
	}

	public Connection getMutexConnection(String muid) {
		return mutexConns.get(muid);
	}

	public void setMutexConnection(String muid, Connection mutexConn) {
		destroyMutexConnection(muid);
		mutexConns.put(muid, mutexConn);
	}
	
	public void destroyMutexConnection(String muid) {
		Connection mutexConn = mutexConns.remove(muid);
		if (mutexConn != null) {
			try {
				mutexConn.rollback();
			} catch (SQLException e) {
				log.error(e, e);
			} finally {
				DbUtils.closeQuietly(mutexConn);
			}
			mutexConn = null;
		}
	}

	public void destroyMutexConnections() {
		for (String uid : mutexConns.keySet()) {
			destroyMutexConnection(uid);
		}
	}
	public String getFileStoreType() {
		return fileStoreType;
	}
}
