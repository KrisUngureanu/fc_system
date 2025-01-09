package com.cifs.or2.server;

import static kz.tamur.common.ErrorCodes.ATTRIBUTE_NOT_FOUND;
import static kz.tamur.common.ErrorCodes.PASS_NOT_COMPLETE;
import static kz.tamur.common.ErrorCodes.PASS_OLD_PASS_INVALID;
import static kz.tamur.common.ErrorCodes.PASS_PASS_IDENT;
import static kz.tamur.common.ErrorCodes.PASS_PASS_NOT_EQUALS;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_EASY_SYMBOLS;
import static kz.tamur.common.ErrorCodes.SERVER_BLOCKED;
import static kz.tamur.common.ErrorCodes.SERVER_MAX_CLIENTS_REACHED;
import static kz.tamur.common.ErrorCodes.SERVER_NOT_AVAILABLE;
import static kz.tamur.common.ErrorCodes.TYPE_WARNING;
import static kz.tamur.common.ErrorCodes.USER_ECP_FAILED;
import static kz.tamur.common.ErrorCodes.USER_HAS_CONNECT;
import static kz.tamur.common.ErrorCodes.USER_HAS_CONNECT_SAME_IP;
import static kz.tamur.common.ErrorCodes.USER_IIN_NOT_MATCH;
import static kz.tamur.common.ErrorCodes.USER_IS_BLOCKED;
import static kz.tamur.common.ErrorCodes.USER_IS_ENDED;
import static kz.tamur.common.ErrorCodes.USER_IS_EXPIRED;
import static kz.tamur.common.ErrorCodes.USER_NOT_ACT;
import static kz.tamur.common.ErrorCodes.USER_NOT_ADM;
import static kz.tamur.common.ErrorCodes.USER_NOT_FOUND;
import static kz.tamur.common.ErrorCodes.USER_NOT_LOGIN;
import static kz.tamur.common.ErrorCodes.USER_NO_RIGHTS;
import static kz.tamur.comps.Constants.ATTR_ACTIVATE_ECP_EXPIRY_NOTIF;
import static kz.tamur.comps.Constants.ATTR_ACTIVATE_LIABILITY_SIGN;
import static kz.tamur.comps.Constants.ATTR_ACTIVATE_TEMP_REG_NOTIF;
import static kz.tamur.comps.Constants.ATTR_BAN_FAMILIES;
import static kz.tamur.comps.Constants.ATTR_BAN_KEYBOARD;
import static kz.tamur.comps.Constants.ATTR_BAN_LOGIN_IN_PASSWORD;
import static kz.tamur.comps.Constants.ATTR_BAN_NAMES;
import static kz.tamur.comps.Constants.ATTR_BAN_PHONE;
import static kz.tamur.comps.Constants.ATTR_BAN_REPEAT_ANYWHERE_MORE_2_NOREGISTER_CHAR;
import static kz.tamur.comps.Constants.ATTR_BAN_REPEAT_CHAR;
import static kz.tamur.comps.Constants.ATTR_BAN_USE_OWN_IDENTIFICATION_DATA;
import static kz.tamur.comps.Constants.ATTR_BAN_WORD;
import static kz.tamur.comps.Constants.ATTR_CHANGE_FIRST_PASS;
import static kz.tamur.comps.Constants.ATTR_CHECK_CLIENT_IP;
import static kz.tamur.comps.Constants.ATTR_ECP_EXPIRY_NOTIF_PERIOD;
import static kz.tamur.comps.Constants.ATTR_LIABILITY_SIGN_PERIOD;
import static kz.tamur.comps.Constants.ATTR_MAX_LENGTH_LOGIN;
import static kz.tamur.comps.Constants.ATTR_MAX_LENGTH_PASS;
import static kz.tamur.comps.Constants.ATTR_MAX_PERIOD_FIRST_PASS;
import static kz.tamur.comps.Constants.ATTR_MAX_PERIOD_PASSWORD;
import static kz.tamur.comps.Constants.ATTR_MAX_VALID_PERIOD;
import static kz.tamur.comps.Constants.ATTR_MIN_LOGIN_LENGTH;
import static kz.tamur.comps.Constants.ATTR_MIN_PASSWORD_LENGTH;
import static kz.tamur.comps.Constants.ATTR_MIN_PASSWORD_LENGTH_ADMIN;
import static kz.tamur.comps.Constants.ATTR_MIN_PERIOD_PASSWORD;
import static kz.tamur.comps.Constants.ATTR_NUMBER_FAILED_LOGIN;
import static kz.tamur.comps.Constants.ATTR_NUMBER_PASSWORD_DUBLICATE;
import static kz.tamur.comps.Constants.ATTR_NUMBER_PASSWORD_DUBLICATE_ADMIN;
import static kz.tamur.comps.Constants.ATTR_TEMP_REG_NOTIF_PERIOD;
import static kz.tamur.comps.Constants.ATTR_TIME_LOCK;
import static kz.tamur.comps.Constants.ATTR_USE_ECP;
import static kz.tamur.comps.Constants.ATTR_USE_NOTALLNUMBERS;
import static kz.tamur.comps.Constants.ATTR_USE_NUMBERS;
import static kz.tamur.comps.Constants.ATTR_USE_REGISTER_SYMBOLS;
import static kz.tamur.comps.Constants.ATTR_USE_SPECIAL_SYMBOL;
import static kz.tamur.comps.Constants.ATTR_USE_SYMBOLS;
import static kz.tamur.comps.Constants.DD_MM_YYYY_HH_MM_SS;
import static kz.tamur.ods.ComparisonOperations.CO_EQUALS;
import static kz.tamur.ods.ComparisonOperations.CO_EQUALS_IGNORE_CASE;
import static kz.tamur.or3ee.common.SessionIds.CID_BLOB;
import static kz.tamur.or3ee.common.SessionIds.CID_BOOL;
import static kz.tamur.or3ee.common.SessionIds.CID_DATE;
import static kz.tamur.or3ee.common.SessionIds.CID_FLOAT;
import static kz.tamur.or3ee.common.SessionIds.CID_INTEGER;
import static kz.tamur.or3ee.common.SessionIds.CID_MEMO;
import static kz.tamur.or3ee.common.SessionIds.CID_STRING;
import static kz.tamur.or3ee.common.SessionIds.CID_TIME;
import static kz.tamur.or3ee.common.UserSession.SERVER_ID;
import static kz.tamur.rt.Utils.outErrorCreateAttrUser;
import static kz.tamur.util.CollectionTypes.COLLECTION_SET;
import static kz.tamur.comps.Constants.BY_SRVNAME;
import static kz.tamur.comps.Constants.BY_DB;
import static kz.tamur.comps.Constants.BY_SESSION;
import static kz.tamur.comps.Constants.BY_CLIENTTYPE;
import static kz.tamur.comps.Constants.BY_LOGIN;
import static kz.tamur.comps.Constants.BY_IP;
import static kz.tamur.comps.Constants.BY_COMP;
import static kz.tamur.comps.Constants.BY_TIME;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.queryparser.classic.ParseException;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.OrlangTriggerInfo;
import com.cifs.or2.client.ResponseWaiter;
import com.cifs.or2.client.User;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.AnyPair;
import com.cifs.or2.kernel.AnyValue;
import com.cifs.or2.kernel.BlobValue;
import com.cifs.or2.kernel.Cursor;
import com.cifs.or2.kernel.DataChanges;
import com.cifs.or2.kernel.Date;
import com.cifs.or2.kernel.DateValue;
import com.cifs.or2.kernel.DeleteNotificationsNote;
import com.cifs.or2.kernel.FilterDate;
import com.cifs.or2.kernel.FloatValue;
import com.cifs.or2.kernel.IerValue;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnIndex;
import com.cifs.or2.kernel.KrnIndexKey;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnSearchResult;
import com.cifs.or2.kernel.KrnVcsChange;
import com.cifs.or2.kernel.LongPair;
import com.cifs.or2.kernel.LongValue;
import com.cifs.or2.kernel.MessageNote;
import com.cifs.or2.kernel.ModelChanges;
import com.cifs.or2.kernel.Note;
import com.cifs.or2.kernel.NotificationNote;
import com.cifs.or2.kernel.ObjectValue;
import com.cifs.or2.kernel.OrderReloadNote;
import com.cifs.or2.kernel.QueryResult;
import com.cifs.or2.kernel.ReplChangesProgressNote;
import com.cifs.or2.kernel.ReplFilesProgressNote;
import com.cifs.or2.kernel.ScriptExecResultNote;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.kernel.SuperMap;
import com.cifs.or2.kernel.SystemNote;
import com.cifs.or2.kernel.TaskReloadNote;
import com.cifs.or2.kernel.Time;
import com.cifs.or2.kernel.TimeValue;
import com.cifs.or2.kernel.UpdateNotificationsNote;
import com.cifs.or2.kernel.UserSessionValue;
import com.cifs.or2.server.db.ConnectionManager;
import com.cifs.or2.server.db.Database;
import com.cifs.or2.server.db.DatabaseCacheListener;
import com.cifs.or2.server.db.SrvJcrRepository;
import com.cifs.or2.server.db.Utils;
import com.cifs.or2.server.exchange.transport.MessageCash;
import com.cifs.or2.server.orlang.SrvOrLang;
import com.cifs.or2.server.orlang.SrvReport;
import com.cifs.or2.server.replicator.Replication;
import com.cifs.or2.server.timer.ServerTasks;
import com.cifs.or2.server.workflow.definition.Node;
import com.cifs.or2.server.workflow.definition.Transition;
import com.cifs.or2.server.workflow.organisation.OrganisationComponent;
import com.cifs.or2.util.Funcs;
import com.cifs.or2.util.MultiMap;
import com.eclipsesource.json.JsonArray;

import kz.gamma.asn1.ASN1Sequence;
import kz.gamma.asn1.x509.TBSCertificateStructure;
import kz.gamma.util.encoders.Base64;
import kz.tamur.DriverException;
import kz.tamur.admin.ErrorsNotification;
import kz.tamur.common.ErrorCodes;
import kz.tamur.common.PasswordPolicy;
import kz.tamur.comps.Constants;
import kz.tamur.comps.TriggerInfo;
import kz.tamur.guidesigner.ws.WSHelper;
import kz.tamur.lang.EvalException;
import kz.tamur.lang.OrLang;
import kz.tamur.lang.parser.ASTStart;
import kz.tamur.ods.AttrRequest;
import kz.tamur.ods.AttrRequestCache;
import kz.tamur.ods.Driver;
import kz.tamur.ods.Driver2;
import kz.tamur.ods.Lock;
import kz.tamur.ods.LockMethod;
import kz.tamur.ods.ModelChange;
import kz.tamur.ods.Toolkit;
import kz.tamur.ods.Value;
import kz.tamur.ods.debug.ResourceRegistry;
import kz.tamur.or3.util.ProtocolRule;
import kz.tamur.or3.util.SystemAction;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.or3.util.SystemRight;
import kz.tamur.or3ee.common.MetadataChangeListener;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.admin.ServerMessage;
import kz.tamur.or3ee.server.kit.AttrRequestBuilder;
import kz.tamur.or3ee.server.kit.Cache;
import kz.tamur.or3ee.server.kit.CacheUtils;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.server.indexer.Indexer;
import kz.tamur.server.indexer.SearchUtil;
import kz.tamur.server.plugins.NotificationListener;
import kz.tamur.server.plugins.doc.UniversalGenerator;
import kz.tamur.server.wf.ExecutionComponent;
import kz.tamur.server.wf.ExecutionEngine;
import kz.tamur.server.wf.FlowState;
import kz.tamur.server.wf.WfUtils;
import kz.tamur.util.CacheChangeRecord;
import kz.tamur.util.MapMap;
import kz.tamur.util.Pair;
import kz.tamur.util.PasswordService;
import kz.tamur.util.ThreadLocalDateFormat;
import kz.tamur.util.crypto.CheckSignResult;
import kz.tamur.util.crypto.KalkanUtil;
import kz.tamur.util.crypto.XmlUtil;
import kz.tumar.Signer32;

/**
 * Session Bean implementation class Session
 */ 
public class Session {
	
    private String dsName;
    transient private Database db;
    transient private kz.tamur.ods.Driver2 drv;
    private ServerUserSession user;
    private boolean isUserLog=true;
    private boolean ownUser = true;
    transient private SrvOrLang orLang;
    
    private static Map<String, ExecutionComponent> exeComps =
    		new HashMap<String, ExecutionComponent>();
    private static Map<String, OrganisationComponent> orgComps =
    		new HashMap<String, OrganisationComponent>();
    private static Map<String, ServerTasks> serverTasks =
			new HashMap<String, ServerTasks>();
    private static Map<String, MessageCash> messageCaches =
			new HashMap<String, MessageCash>();
    private static Map<UUID, Map<String, ResponseWaiter>> responseWaiters =
    		new HashMap<UUID, Map<String, ResponseWaiter>>();

    private static GlobalFuncs globalFuncs = new GlobalFuncs();
    
    //private static Map<UUID, ServerUserSession> userSessions =
    //	Collections.synchronizedMap(new HashMap<UUID, ServerUserSession>());

    transient private Log log;
    transient private Log protocol;
    
    private static Map<String, Log> userLogs = new HashMap<String, Log>();
    private static KrnObject langObj;
    // Constants

    static final String outputEncoding = "UTF-8";
    public static final String BASE_DS_NAME = "COMMON";
    
    public static final int SQL_MYSQL = 0;
    public static final int SQL_IFX = 1;

    private static Map<Integer, com.cifs.or2.kernel.Date> filterDates_ = Collections.synchronizedMap(new HashMap<Integer, com.cifs.or2.kernel.Date>());

    public static final KrnObject nullObject = new KrnObject(0, "", 0);
    
    private UniversalGenerator uniGen = null;

    // для извлечения политики (при переносе политики в клиент переделать
    // извлечение параметров)
    private KrnClass policyCls;
    private PasswordPolicy policyWrapper;
    protected KrnObject krnObj;
    private final static String policyName = "Политика учетных записей";

    private static final boolean allowConvertDb = Boolean.parseBoolean(System.getProperty("allowConvertDb", "false"));

    private static Cache<UUID, ServerUserSession> userSessionCache = CacheUtils.getCache("userSessionCache");
    public static Map<UUID, ServerUserSession> allUserSessionCache = new HashMap<>();
    private static Map<UUID, ServerUserSession> myUserSessionCache = new HashMap<>();
    private static Map<UUID, ServerUserSession> localUserSessionCache = Collections.synchronizedMap(new HashMap<>());

    private static final boolean bindingModuleToUser = Boolean.parseBoolean(System.getProperty("bindingModuleToUser", "false"));
    
    public static final java.util.Date serverStartupDatetime = new java.util.Date();
    public static List<ServerUserSession> loggedInUsers = new ArrayList<>();
    public static List<ServerUserSession> loggedOutUsers = new ArrayList<>();

    private static final boolean deleteNotificationsAtLogin = Boolean.parseBoolean(System.getProperty("deleteNotificationsAtLogin", "true"));

	private static final String procAllowed = System.getProperty("exec.procAllowed");
	private static final String procDenied = System.getProperty("exec.procDenied");

    // Класс отслеживающий изменения в кешах
 	private static DatabaseCacheListener cacheListener = null;
 	
    static {
    	cacheListener = new DatabaseCacheListener(BASE_DS_NAME);
    	userSessionCache.addEntryListener(cacheListener);
		for (UUID key : userSessionCache.getKeys()) {
			cacheListener.entryAdded(userSessionCache.getName(), key, userSessionCache.get(key), null);
		}
    }
    
    @PrePassivate
    private void prePassivate() {
    	try {
    		db.releaseDriver(drv);
    	} catch (DriverException e) {
    		log.error("Ошибка при пассивации сессии", e);
    	}
    }
    
    @PostActivate
    public void postActivate() {
    	ServerUserSession us = (ServerUserSession)getUserSession();
    	initLogs(us.getUserName());
    	db = ConnectionManager.instance().getDatabase(dsName);
    	try {
    		initDriver(us);
    	} catch (DriverException e) {
    		log.error("Ошибка при восстановлении сессии", e);
    	}
    }
    
    public void close() {
        if (drv != null) {
            try {
        		db.releaseDriver(drv);
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
        drv = null;
    	ResourceRegistry.instance().resourceReleased(this);
    }
    
    public void release() {
    	destroy();
    	ResourceRegistry.instance().resourceReleased(this);
    }
    
	public void destroy() {
    	log.debug("#### removing session " + this);

    	if (ownUser && user != null) {
    		removeUserSession(user);
    	}
        if (drv != null) {
            try {
            	drv.rollback();
                rollbackLocked();
                drv.commit();
            } catch (Exception e) {
                log.warn(e.getMessage());
            } finally {
            	try {
            		db.releaseDriver(drv);
            	} catch (Throwable e) {
            		log.error(e, e);
            	}
            }
        }
        drv = null;
    }

    private void setUserSession(ServerUserSession userSession) throws KrnException {
    	if (ownUser) {
    		if (user != null) {
    			removeUserSession(user);
    		}
    		putUserSession(userSession);
    	}
        user = userSession;
        //установка языка по умолчанию
        if(langObj==null) {
        	List<KrnObject> langObjs=getSystemLangs();
        	if(langObjs.size()>0)
        		langObj=langObjs.get(0);
        }
        user.setLang(langObj);
        user.setDataLanguage(langObj);
        //и объект пользователя если его нет
        if(user.getUserObj()==null)
        	user.setUserObj(new KrnObject(-1, "-1", -1));
        if (drv != null) drv.setUserSession(userSession);
        userSession.setSession(this);
    }
    
    // Transaction support
    public void beginTransaction() throws KrnException {
    }
    private void saveGroupAttrObjects() throws KrnException{
    	if(orLang!=null){
    		orLang.save();
    	}
    }
    private void clearObjCache() throws KrnException{
    	if(orLang!=null){
    		orLang.cleaObjCache();
    	}
    }
    public void commitTransaction() throws KrnException {
        try {
        	Context ctx=null;
        	if(!isContextEmpty()) {
        		ctx=getContext();
        		ExecutionComponent exeComp;
        		if((exeComp = getExeComp())!=null && ctx.flowId>0 && ctx.isOpenTranpaction) {//Если коммит вызван внутри процесса 
        			exeComp.closeFlowTransaction(ctx.flowId);	 //то отменяется предыдущая установка openTransaction
                    log.info("OPEN_TRANSACTION: commitTransaction;fowId:"+ctx.flowId+";CONNECTION_ID:"+getConnectionId());
       		}
        	}
        	if(ctx!=null && ctx.beforeCommitExpr!=null) {
        		try {
					orLang.evaluate(ctx.beforeCommitExpr, new HashMap<String,Object>(), null, false, new Stack<String>(), null);
				} catch (Exception e) {
		            log.error(e.getMessage(), e);
					e.printStackTrace();
				}
        	}
        	//сохраняем все объекты с групповыми атрибутами
        	saveGroupAttrObjects();
        	drv.commit();
        	if(ctx!=null && ctx.afterCommitExpr!=null) {
        		try {
					orLang.evaluate(ctx.afterCommitExpr, new HashMap<String,Object>(), null, false, new Stack<String>(), null);
				} catch (Exception e) {
		            log.error(e.getMessage(), e);
					e.printStackTrace();
				}
        	}
        } catch (DriverException e) {
            log.error(e.getMessage(), e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public void rollbackTransaction() throws KrnException {
        try {
        	if(!isContextEmpty()) {
        		Context ctx=getContext();
        		if(getExeComp()!=null && ctx.flowId>0 && ctx.isOpenTranpaction) { 
                    log.info("OPEN_TRANSACTION: rollbackTransaction;fowId:"+ctx.flowId+";CONNECTION_ID:"+getConnectionId());
        		}
        	}
        	drv.rollback();
        	clearObjCache();
        } catch (DriverException e) {
            log.error(e.getMessage(), e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public void rollbackTransactionQuietly() {
        try {
        	if(!isContextEmpty()) {
        		Context ctx=getContext();
        		if(getExeComp()!=null && ctx.flowId>0 && ctx.isOpenTranpaction) { 
                    log.info("OPEN_TRANSACTION: rollbackTransaction;fowId:"+ctx.flowId+";CONNECTION_ID:"+getConnectionId());
        		}
        	}
        	drv.rollback();
        	clearObjCache();
        } catch (DriverException e) {
            log.error(e.getMessage(), e);
        } catch (KrnException e) {
			log.error(e, e);
		}
    }

    public void changePassword(String dsName, String nameUs, String typeClient, String ip, String pcName, KrnObject object, char[] oldpd, char[] newpd, char[] confpd) throws KrnException {
        String pdStr = new String(newpd);
        String newPd = PasswordService.getInstance().encrypt(pdStr);
        // первоначальные проверки паролей, не требующие обращения к БД
        if (oldpd.length == 0 || newpd.length == 0 || confpd.length == 0) {
            throw new KrnException(PASS_NOT_COMPLETE, object); // Не заполнены необходимые поля!
        }

        if (!Arrays.equals(newpd, confpd)) {
            throw new KrnException(PASS_PASS_NOT_EQUALS, object); // Пароли не совпадают!
        }

        if (Arrays.equals(newPd.toCharArray(), oldpd)) {
            throw new KrnException(PASS_PASS_IDENT, object); // Пароли идентичны!
        }

        initLogs(nameUs);
        // получение необходимых значений политики безопасности
        getPolicy();
        
        KrnClass clsUser = getClassByName("User");
        AttrRequestBuilder arb = new AttrRequestBuilder(clsUser, this).add("name").add("password").add(ATTR_NUMBER_FAILED_LOGIN)
                .add("admin").add("isLogged").add("isChangedPassBySys").add("previous passwords").add("дата изменения пароля");

        long[] objIds = new long[] { object.id };

        Object[] row = getObjects(objIds, arb.build(), 0).rows.get(0);

        String pdDB = (String) arb.getValue("password", row);
        if (!Arrays.equals(oldpd, pdDB.toCharArray())) {
            throw new KrnException(PASS_OLD_PASS_INVALID, object); // Старый пароль введён неверно!
        }
        
        String name = (String) arb.getValue("name", row);
        boolean admin = arb.getBooleanValue("admin", row);
        boolean isLogged = arb.getValue("isLogged", row)!=null? (Boolean) arb.getValue("isLogged", row):true;
        boolean isChangedPassBySys = (Boolean) arb.getValue("isChangedPassBySys", row);
        String prevPath = (String) arb.getValue("previous passwords", row);
        Time dataTime = null;

        try {
            dataTime = (Time) arb.getValue("дата изменения пароля", row);
        } catch (Exception ex) {
            outErrorCreateAttrUser("дата изменения пароля");
        }

        verifyPassword(nameUs, object, newpd, name, admin, isLogged, prevPath, dataTime);

        long k = admin ? policyWrapper.getNumPassDublAdmin() : policyWrapper.getNumPassDubl();
        String prevPathOld = prevPath;
        String pdArr[] = prevPath != null ? prevPath.split(";") : null;
        // даже если политика отключена, пароли помнить нужно
        k = (k == 0) ? 20 : k;
        // записать новый хеш
        // пароля в атрибут, при превышении размерности атрибута
        // массив урезается

        int l = pdArr == null ? 0 : pdArr.length;

        // при достижении дозволенных границ
        if (l >= k) {
            int k_ = (int) k;
            String[] pdArr_ = new String[k_];
            // в обратном порядке переписать в промежуточный массив
            // все данные с предыдущего массива, с конца, пропустив
            // одну позицию
            for (int i = k_ - 2; i >= 0; --i) {
            	pdArr_[i] = pdArr[--l];
            }

            // последнему элементу массива присвоить
            // значение нового пароля
            pdArr_[--k_] = newPd;
            // сборка нового значения для атрибута
            // "previous passwords"
            StringBuilder b = new StringBuilder();
            for (int i = 0;; i++) {
                b.append(pdArr_[i]);
                if (i == k_)
                    break;
                b.append(";");
            }
            prevPath = b.toString();
        } else {
            if (prevPath == null) {
            	prevPath = newPd;
            } else {
            	prevPath += ";" + newPd;
            }
        }

        // Смена пароля корректна, можно сохранить!

        if (!prevPath.equals(prevPathOld)) {
            // записать новый пул хешей паролей
            setString(object.id, getAttributeByName(clsUser, "previous passwords").id, 0, 0, true, prevPath, 0);
        }

        // Записать дату изменения пароля
        java.util.Date tm = new GregorianCalendar(TimeZone.getTimeZone("Asia/Dhaka")).getTime();
        setTime(object.id, getAttributeByName(clsUser, "дата изменения пароля").id, 0, kz.tamur.util.Funcs.convertTime(tm), 0);

        // если это первая авторизация и пароль был изменён, то снять флаг первой авторизации у пользователя
        if (!isLogged) {
            setLong(object.id, getAttributeByName(clsUser, "isLogged").id, 0, 1, 0);
        }
        
        if (isChangedPassBySys) {
            setLong(object.id, getAttributeByName(clsUser, "isChangedPassBySys").id, 0, 0, 0);
        }

        // Записать пароль
        setString(object.id, getAttributeByName(clsUser, "password").id, 0, 0, false, newPd, 0);
        writeLogRecord(SystemEvent.EVENT_CHANGE_PASSWORD, "",clsUser.id,-1);
    }
    
    public void verifyPassword(String nameUs, KrnObject object, char[] newpd, String name, boolean admin, boolean isLogged, String psw, Time lastChangeTime) throws KrnException {
        initLogs(nameUs);
        // получение необходимых значений политики безопасности
        getPolicy();
        policyWrapper.verifyPassword(object, newpd, name, admin, isLogged, psw, lastChangeTime);
        if (policyWrapper.isBanUseOwnIdentificationData()) {
        	checkForUseOwnIdentificationData(object, newpd);
        }
    }
    
    private void checkForUseOwnIdentificationData(KrnObject object, char[] newpd) throws KrnException {
    	// Проверка включения имени, фамилии и даты рождения для кызмета
        KrnClass userCls = getClassById(object.classId);
        KrnAttribute personalAttr = getAttributeByName(userCls, "персона");
        if (personalAttr != null) {
	        KrnObject personalObj = getObjectsSingular(object.id, personalAttr.id, false);
	        if (personalObj != null) {
	            KrnClass personalCls = getClassByName("Персонал");
		        if (personalCls != null) {
		            KrnAttribute zapTablPersDannyhAttr = getAttributeByName(personalCls, "текущ  состояние -зап табл персон данных-");
			        if (zapTablPersDannyhAttr != null) {
			            KrnObject zapTablPersDannyhObj = getObjectsSingular(personalObj.id, zapTablPersDannyhAttr.id, false);
			            if (zapTablPersDannyhObj != null) {
			                KrnClass zapTablPersDannyhCls = getClassByName("Зап табл персон данных");
				            if (zapTablPersDannyhCls != null) {
				                String pdStr = new String(newpd);
				            	String pdUp = pdStr.toUpperCase(Constants.OK);

				                KrnAttribute nameAttr = getAttributeByName(zapTablPersDannyhCls, "идентиф -имя-");
				            	String personName = getStringsSingular(zapTablPersDannyhObj.id, nameAttr.id, 0, false, false);
				            	if (personName != null && personName.length() > 0) { 
					            	String personNameUp = personName.toUpperCase(Constants.OK);
					            	if (pdUp.contains(personNameUp)) {
					                    throw new KrnException(PASS_VALID_PWD_NOT_EASY_SYMBOLS, object, "", TYPE_WARNING); // Пароль не должен включать в себя легко вычисляемые сочетания символов!
					            	}
				            	}
				            	
				                KrnAttribute surnameAttr = getAttributeByName(zapTablPersDannyhCls, "идентиф -фамилия-");
				            	String personSurname = getStringsSingular(zapTablPersDannyhObj.id, surnameAttr.id, 0, false, false);
				            	if (personSurname != null && personSurname.length() > 0) {
					            	String personSurnameUp = personSurname.toUpperCase(Constants.OK);
					            	if (pdUp.contains(personSurnameUp)) {
					                    throw new KrnException(PASS_VALID_PWD_NOT_EASY_SYMBOLS, object, "", TYPE_WARNING);
					            	}
				            	}
				            	
				                KrnAttribute middlenameAttr = getAttributeByName(zapTablPersDannyhCls, "идентиф -отчество-");
				            	String personMiddlename = getStringsSingular(zapTablPersDannyhObj.id, middlenameAttr.id, 0, false, false);
				            	if (personMiddlename != null && personMiddlename.length() > 0) {
					            	String personMiddlenameUp = personMiddlename.toUpperCase(Constants.OK);
					            	if (pdUp.contains(personMiddlenameUp)) {
					                    throw new KrnException(PASS_VALID_PWD_NOT_EASY_SYMBOLS, object, "", TYPE_WARNING);
					            	}
				            	}
				            	
				            	KrnAttribute telAttr = getAttributeByName(zapTablPersDannyhCls, "семья -домашний телефон");
				            	String personTel = getStringsSingular(zapTablPersDannyhObj.id, telAttr.id, 0, false, false);
				            	if (personTel != null && personTel.length() > 0) {
					            	String personTelUp = personTel.toUpperCase(Constants.OK);
					            	if (pdUp.contains(personTelUp)) {
					                    throw new KrnException(PASS_VALID_PWD_NOT_EASY_SYMBOLS, object, "", TYPE_WARNING);
					            	}
				            	}
				            	
				                KrnAttribute dateAttr = getAttributeByName(zapTablPersDannyhCls, "идентиф -дата рождения-");
				            	DateValue[] dateValues = getDateValues(new long[] {zapTablPersDannyhObj.id}, dateAttr.id, 0);
				            	Date orDate = (dateValues.length == 0) ? null : dateValues[0].value;
				            	if (orDate != null) {
					            	Calendar c = Calendar.getInstance();
					            	c.set(Calendar.YEAR, orDate.year);
					            	c.set(Calendar.MONTH, orDate.month);
					            	c.set(Calendar.DAY_OF_MONTH, orDate.day);
					            	String dateString = String.format("%1$td%1$tm%1$tY", c.getTime());
					            	if (pdUp.contains(dateString)) {
					                    throw new KrnException(PASS_VALID_PWD_NOT_EASY_SYMBOLS, object, "", TYPE_WARNING);
					            	}
				            	}
				            }
			            }
			        }
		        }
	        }
        }
    }

    public UserSession getUserSession() {
    	if (user != null)
    		return user;
    	return new ServerUserSession(dsName, "server", new UserSrv("sys"), Database.address.getHostAddress(), Database.address.getHostName(), SERVER_ID, false);
    }

    public void setSessionUser(ServerUserSession user) {
        this.user = user;
    }

    public KrnObject getUser() {
        return user.getUserObj();
    }
    
    public void updateUser(long id){
        try {
            KrnObject obj =getObjectById(id,-1);
            updateUser(obj,"");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
    public void removeUser(long id){
        try {
            KrnObject obj =getObjectById(id,-1);
            removeUser(obj,"");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
    public void removeUser(KrnObject obj, String name){
        try {
            long cid = getClassByName("User").id;
            if (cid == obj.classId) {
                writeLogRecord(SystemEvent.EVENT_USER_DELETE, name,cid,-1);
                getOrgComp().removeUser(new KrnObject[]{obj});
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }
    
    public void updateUsers(KrnObject[] objs) {
    	getOrgComp().updateUser(objs, true, this);
    }
    
    public void  updateUser(KrnObject obj, String name) {
    	try {
    		long cid = getClassByName("User").id;
            long cidf = getClassByName("UserFolder").id;
    		if (cid == obj.classId || cidf == obj.classId) {
                if(!name.equals("")){
	                writeLogRecord(SystemEvent.EVENT_USER_CHANGE, name,cid == obj.classId?cid:cidf,-1);
                }
                //getOrgComp().updateUser(new KrnObject[]{obj}, true, this);
    	    }
    	} catch (Exception e) {
            log.error(e.getMessage(), e);
    	}
    }

    public long createLongTransaction() throws KrnException {
        try {
            return drv.createLongTransaction();
        } catch (DriverException e) {
            log.error(e.getMessage(), e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public void commitLongTransaction(long tid, long otid) throws KrnException {
    	commitLongTransaction(tid, otid, false);
    }

    public void commitLongTransaction(long tid, long otid, boolean deleteRefs) throws KrnException {
    	if (tid == 0) {
    		return;
    	}
        try {
            drv.commitLongTransaction(tid, deleteRefs,this);
        } catch (DriverException e) {
            log.error(e.getMessage(), e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public void rollbackLongTransaction(long tid) throws KrnException {
    	if (tid == 0) {
            throw new KrnException(0, "Невозможно откатить нулевую транзакцию!");
    	}
        try {
        	if(!isContextEmpty()) {
        		Context ctx=getContext();
        		if(getExeComp()!=null && ctx.flowId>0 && ctx.isOpenTranpaction) { 
                    log.info("OPEN_TRANSACTION: rollbackLongTransaction;fowId:"+ctx.flowId+";CONNECTION_ID:"+getConnectionId());
        		}
        	}
            drv.rollbackLongTransaction(tid);
        } catch (DriverException e) {
            log.error(e.getMessage(), e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public long[] getSelectedBases() throws KrnException {
    	if (user != null)
    		return user.getBaseIds();
    	return new long[0];
    }

    public void selectBases(long[] baseIds) throws KrnException {
    	if (user != null)
    		user.setBaseIds(baseIds);
    }

	public LongPair[] commit2(List<CacheChangeRecord> changes, long tid)
 throws KrnException {
        try {
            Map<Long, Long> ids = new TreeMap<Long, Long>();
            for (CacheChangeRecord change : changes) {

                if (change.changeType == 1) { // Создание объекта
                    KrnObject vo = KrnObject.class.cast(change.value);
                    KrnClass cls = getClassById(vo.classId);
                    KrnObject obj = createObject(cls, tid);
                    ids.put(new Long(vo.id), new Long(obj.id));
                    if(vo.id<0)
                    	log.info("Создание объекта: OBJ_ID="+vo.id+" ->  OBJ_ID="+obj.id);
                    continue;
                }

                if (change.objectId < 0) {
                    Long id = ids.get(change.objectId);
                    if (id == null)
                        continue;
                    change.objectId = id;
                }
                Object v = change.value;
                if (v instanceof KrnObject) {
                    KrnObject vo = KrnObject.class.cast(v);
                    if (vo.id < 0) {
                        Long id = ids.get(vo.id);
                        if (id == null)
                            continue;
                        vo.id = id;
                    }
                }

                if (change.changeType == 2) { // Удаление объекта
                    KrnObject vo = KrnObject.class.cast(v);
                    deleteObject(vo, tid);
                    continue;
                }

                if (v instanceof Date)
                    v = kz.tamur.util.Funcs.convertToSqlDate((Date) v);
                else if (v instanceof Time)
                    v = kz.tamur.util.Funcs.convertToSqlTime((Time) v);

                if (change.changeType == 3) { // Изменение значения атрибута
                    if (v instanceof KrnObject)
                        v = ((KrnObject) v).id;
                    
                    Object oldVal = change.oldValue;
                    if (oldVal instanceof Date)
                    	oldVal = kz.tamur.util.Funcs.convertToSqlDate((Date) oldVal);
                    else if (oldVal instanceof Time)
                    	oldVal = kz.tamur.util.Funcs.convertToSqlTime((Time) oldVal);
                    else if (oldVal instanceof KrnObject)
                    	oldVal = ((KrnObject) oldVal).id;
                    
                    try {
                        setValue(change.objectId, change.attrId, change.index, change.langId, tid, v, oldVal, change.insert);
                    } catch (Exception e) {
                        log.error("Error in 'setValue' for ID:" + change.objectId);
                    	log.error(e, e);
                    	throw new KrnException(0,e.getMessage());
                    }

                } else if (change.changeType == 4) { // Удаление значения атрибута
                    KrnAttribute attr = getAttributeById(change.attrId);
            		if (drv.getThrowOnSaveNullMode()) {
            	    	if (attr.typeClassId > 10 && change.isSetToNull == true) {
            				throw new DriverException("Установка пустого значения! Id объекта: " + change.objectId + ", атрибут: " + attr.name + ".");
            			}
                	}
                    if (attr.collectionType == COLLECTION_SET) {
                        deleteValue(change.objectId, change.attrId, Collections.singletonList(v), tid);
                    } else {
                        deleteValue(change.objectId, change.attrId, new int[] { change.index }, change.langId, tid);
                    }
                }
            }

            // фиксируем закэшированные на сервере заблокированные объекты и освобождаем их
            commitLocked();

            commitTransaction();

            LongPair[] res = new LongPair[ids.size()];
            int i = 0;
            for (Long oldId : ids.keySet()) {
                Long newId = ids.get(oldId);
                res[i++] = new LongPair(oldId, newId);
            }
            return res;
        } catch (KrnException e) {
            log.error(e, e);
            rollbackTransaction();
            log.error(getOwnerString() + " Ошибка при сохранении интерфейса", e);
            throw e;
        } catch (Exception e) {
            log.error(e, e);
            rollbackTransaction();
            log.error(getOwnerString() + " Ошибка при сохранении интерфейса", e);
            String msg = e.getMessage();
            if (msg == null)
                msg = "NullPointerException";
            throw new KrnException(0, msg);
        }
    }

    public KrnObject getObjectById(long objId,long trId) throws KrnException {
        try {
            return drv.getObjectById(objId);
        } catch (DriverException e) {
            log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public KrnObject getObjectByIdQuit(long objId,long trId) {
        try {
            return drv.getObjectById(objId);
        } catch (DriverException e) {
            log.error(e, e);
            return null;
        }
    }

    public KrnObject getDirtyObjectById(long objId) throws KrnException {
        try {
            List<KrnObject> objs = drv.getObjectsByIds(new long[] {objId});
            if (objs.size() > 0) {
                return objs.get(0);
            } else
                throw new KrnException(0, "object with c_id=" + objId + " not found");
        } catch (DriverException e) {
            log.error(e.getMessage(), e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public KrnObject[] getObjectsById(long[] ids,long trId) throws KrnException {
        try {
            List<KrnObject> objs = drv.getObjectsByIds(ids);
            return objs.toArray(new KrnObject[objs.size()]);
        } catch (DriverException e) {
            log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public KrnObject getObjectByUid(String uid, long trId) throws KrnException {
        try {
        	return db.getObjectByUid(uid, drv);
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public KrnObject getDirtyObjectByUid(String uid, long trId) throws KrnException {
       	return getObjectByUid(uid, trId);
    }

    public KrnObject[] getObjectsByUid(String[] uids, long trId) throws KrnException {
        try {
            List<KrnObject> objs = drv.getObjectsByUids(uids, trId, false);
            return objs.toArray(new KrnObject[objs.size()]);
        } catch (DriverException e) {
            log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }
    
    public KrnObject getClassObjectByUid(long clsId, String uid, long trId) throws KrnException {
        try {
        	return drv.getClassObjectByUid(clsId, uid, trId, false);
        } catch (DriverException e) {
            log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    // Classes
    /** Проверка присутствия класса в БД
     *  @param name имя класса
     *  @return <code>true</code> если класс найден
     */
    public boolean checkExistenceClassByName(String name) {
        KrnClass cnode;
        try {
            cnode = getClassByName(name);
            return cnode != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    public KrnClass getClassByName(String name) {
    	return db.getClassByName(name);
    }
    
    public List<KrnClass> getClassesByNameWithOptions(String name, long searchMethod) throws KrnException {
    	return db.getClassesByNameWithOptions(name, searchMethod);
    }

    public KrnClass getClassById(long classId) {
    	return db.getClassById(classId);
    }

	public KrnClass getClassByUid(String classUid) throws KrnException {
		return db.getClassByUid(classUid);
	}

    public List<KrnClass> getClasses(long baseClassId, boolean withSubclasses)
            throws KrnException {
    	List<KrnClass> res = new ArrayList<KrnClass>();
        db.getSubClasses(baseClassId, withSubclasses, res);
        return res;
    }

    public KrnClass[] getClasses(long baseClassId)
            throws KrnException {
    	List<KrnClass> res = new ArrayList<KrnClass>();
        db.getSubClasses(baseClassId, false, res);
        return (KrnClass[])res.toArray(new KrnClass[res.size()]);
    }
    
    //вернуть все классы    
    public KrnClass[] getClasses() throws KrnException{
    	List<KrnClass> clss = getAllClasses();
    	return clss.toArray(new KrnClass[clss.size()]);
    }

    public Set<KrnClass> getSuperClasses(long classId) throws KrnException {
        List<KrnClass> cs = db.getSuperClasses(classId);
        Set<KrnClass> res = new HashSet<KrnClass>(cs);
        return res;
    }

    public void createConfirmationFile(long ADbId)
            throws KrnException {
        throw new KrnException(0, "Not implemented");
    }

    public KrnClass createClass(KrnClass baseClass, String name, boolean isRepl, String tname, int mod)
            throws KrnException {
        try {
        	KrnClass ncls = drv.createClass(name, baseClass.id, isRepl, mod, -1, null, tname);
            writeLogRecord(SystemEvent.EVENT_CLASS_CREATED, "Класс " + name,ncls.id,-1);
            drv.fireClassCreated(ncls);
            return ncls;
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public KrnClass createClass(long baseId, String name, boolean isRepl, long id, String uid, String tname, int mod)
            throws KrnException {
        try {
            KrnClass ncls = drv.createClass(name, baseId, isRepl, mod, id, uid, tname);
            writeLogRecord(SystemEvent.EVENT_CLASS_CREATED, "Класс " + name, ncls.id, -1);
            drv.fireClassCreated(ncls);
            return ncls;
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public KrnClass changeClass(KrnClass cls, KrnClass baseClass, String name, boolean isRepl)
            throws com.cifs.or2.kernel.KrnException {
        try {
            KrnClass ncls = drv.changeClass(cls.id, baseClass.id, name, isRepl);
            SrvUtils.expungeStalePathes(cls);
            writeLogRecord(SystemEvent.EVENT_CLASS_CHANGED, "Старое название: " + cls.name + ", новое: " + name + ", реплицировать: " + isRepl,ncls.id,-1);
            drv.fireClassChanged(cls, ncls);
            return ncls;
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public void deleteClass(KrnClass cls) throws KrnException {
        try {
            drv.deleteClass(cls.id);
            SrvUtils.expungeStalePathes(cls);
            writeLogRecord(SystemEvent.EVENT_CLASS_DELETED, "Класс " + cls.name,cls.id,-1);
            drv.fireClassDeleted(cls);
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }
    
    public String renameClass(KrnClass cls, String newname) throws KrnException {//TODO r tname
    	String result = null;
        try {
        	String name = newname;
            if (drv.renameClassTable(cls.id, name)) {
    			db.getClassById(cls.id).tname = name;
            	cls.tname = name;
            	result = name;
            }
        } catch (Exception e) {
        	log.error(e, e);
            throw new KrnException(0, e.getMessage());
        }
        return result;
    }
    
    public String renameAttr(KrnAttribute attr, String newname) throws KrnException {//TODO r tname
    	String result = null;
        try {
        	String name = newname;
            if (drv.renameAttrTable(attr, name)) {
    			db.getAttributeById(attr.id).tname = name;
            	attr.tname = name;
            	result = name;
            }
        } catch (Exception e) {
        	log.error(e, e);
            throw new KrnException(0, e.getMessage());
        }
        return result;
    }
    
    public boolean classExists(String classUid) throws KrnException{
    	try {
			return drv.classExists(classUid);
		} catch (DriverException e) {
			log.error(e, e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		}
    }

    // Attributes
    public KrnAttribute getAttributeByName(KrnClass cls, String name) {
    	return db.getAttributeByName(cls.id, name);
    }

    public KrnAttribute getAttributeById(long attrId) {
    	return db.getAttributeById(attrId);
    }

    public KrnAttribute getAttributeByUid(String attrUid) throws KrnException {
    	return db.getAttributeByUid(attrUid);
    }

    public List<KrnAttribute> getAttributesByUidPart(String attruid, long searchMethod) throws KrnException {
    	return db.getAttributesByUidPart(attruid, searchMethod);
    }

    public KrnAttribute[] getAttributes(KrnClass cls) {
    	List<KrnAttribute> res = db.getAttributesByClassId(cls.id, true);
    	return res.toArray(new KrnAttribute[res.size()]);
    }

    public List<KrnAttribute> getClassAttributes(KrnClass cls) {
    	return db.getAttributesByClassId(cls.id, true);
    }

    public List<KrnAttribute> getAttributesByName(String name, long searchMethod) {
    	return db.getAttributesByName(name, searchMethod);
    }
    
    public List<KrnAttribute> getDependAttrs(KrnAttribute attr)  throws KrnException {
    	try {
			return drv.getDependAttrs(attr.id);
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
		}
    }
    public KrnAttribute[] getAttributesByTypeId(long typeId, boolean inherited) throws KrnException {
    	List<KrnAttribute> res = db.getAttributesByTypeId(typeId,inherited);
    	return res.toArray(new KrnAttribute[res.size()]);
    }

    public KrnAttribute createAttribute(
            KrnClass cls,
            KrnClass type,
            String name,
            int collType,
            boolean isUnique,
            boolean isIndexed,
            boolean isMultilingual,
            boolean isRepl,
            int size,
            long flags,
            long rAttrId,
            long sAttrId,
            boolean sDesc,
            String tname,
            int accessModifier) throws KrnException {
        return createAttribute(cls, type, name, collType, isUnique, isIndexed, isMultilingual, isRepl, size, flags, rAttrId, sAttrId, sDesc, tname, accessModifier, false);
    }
    
    public KrnAttribute createAttribute(
            KrnClass cls,
            KrnClass type,
            String name,
            int collType,
            boolean isUnique,
            boolean isIndexed,
            boolean isMultilingual,
            boolean isRepl,
            int size,
            long flags,
            long rAttrId,
            long sAttrId,
            boolean sDesc,
            String tname,
            int accessModifier,
            boolean isEncrypt) throws KrnException {
        return createAttribute(-1, null, cls.id, type.id, name, collType,
                isUnique, isIndexed, isMultilingual, isRepl, size, flags,
                rAttrId, sAttrId, sDesc, tname, accessModifier, isEncrypt);
    }
    
    public KrnAttribute createAttribute(
            long id,
            String uid,
            long classId,
            long typeId,
            String name,
            int collectionType,
            boolean isUnique,
            boolean isIndexed,
            boolean isMultilingual,
            boolean isRepl,
            int size,
            long flags,
            long rAttrId,
            long sAttrId,
            boolean sDesc,
            String tname,
            int accessModifier) throws KrnException {
        return createAttribute(id, uid, classId, typeId, name, collectionType, isUnique, isIndexed, isMultilingual, isRepl, size, flags, rAttrId, sAttrId, sDesc, tname, accessModifier, false);
    }

    public KrnAttribute createAttribute(
            long id,
            String uid,
            long classId,
            long typeId,
            String name,
            int collectionType,
            boolean isUnique,
            boolean isIndexed,
            boolean isMultilingual,
            boolean isRepl,
            int size,
            long flags,
            long rAttrId,
            long sAttrId,
            boolean sDesc,
            String tname,
            int accessModifier,
            boolean isEncrypt) throws KrnException {
        try {
            KrnAttribute attr = drv.createAttribute(
                    id, uid, classId, typeId, name, collectionType, isUnique, isIndexed,
                    isMultilingual, isRepl, size, flags, rAttrId, sAttrId, sDesc, tname, accessModifier, isEncrypt);
            if (attr.typeClassId == CID_BLOB && attr.isIndexed) {
            	Indexer.updateIndex(attr, this, null);
            }
            
            writeLogRecord(SystemEvent.EVENT_ATTR_CREATED, "Атрибут " + name,classId,id);
            
            drv.fireAttrCreated(attr);

            return attr;
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
        	log.error(e, e);
            throw new KrnException(0, e.getMessage());
        }
    }
    
    public KrnAttribute changeAttribute(
            KrnAttribute attr,
            KrnClass type,
            String name,
            int collType,
            boolean isUnique,
            boolean isIndexed,
            boolean isMultilingual,
            boolean isRepl,
            int size,
            long flags,
            long rAttrId,
            long sAttrId,
            boolean sDesc,
            String tname,
            int accessModifier) throws KrnException {
        return changeAttribute(attr, type, name, collType, isUnique, isIndexed, isMultilingual, isRepl, size, flags, rAttrId, sAttrId, sDesc, tname, accessModifier, false);
    }

    public KrnAttribute changeAttribute(
            KrnAttribute attr,
            KrnClass type,
            String name,
            int collType,
            boolean isUnique,
            boolean isIndexed,
            boolean isMultilingual,
            boolean isRepl,
            int size,
            long flags,
            long rAttrId,
            long sAttrId,
            boolean sDesc,
            String tname,
            int accessModifier,
            boolean isEncrypted) throws KrnException {
        try {
            KrnAttribute newAttr = drv.changeAttribute(
                    attr.id, type.id, name, collType, isUnique, isIndexed,
                    isMultilingual, isRepl, size, flags, rAttrId, sAttrId, sDesc, tname, accessModifier, isEncrypted);
            if (newAttr.typeClassId == CID_BLOB
            		&& !attr.isIndexed && newAttr.isIndexed) {
            	//Indexer.updateIndex(newAttr, this, null);
            }
            SrvUtils.expungeStalePathes(attr);
            writeLogRecord(SystemEvent.EVENT_ATTR_CHANGED, "Старое название: " + attr.name + ", новое: " + name + ", реплицировать: " + isRepl,attr.classId,attr.id);
            drv.fireAttrChanged(attr, newAttr);
            return newAttr;
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
        	log.error(e, e);
            throw new KrnException(0, e.getMessage());
        }
    }

    public void deleteAttribute(KrnAttribute attr)
            throws KrnException {
        try {
            drv.deleteAttribute(attr.id);
            SrvUtils.expungeStalePathes(attr);
            writeLogRecord(SystemEvent.EVENT_ATTR_DELETED, "Атрибут " + attr.name,attr.classId,attr.id);
            drv.fireAttrDeleted(attr);
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }
    
    public boolean attributeExists(String attrUid) throws KrnException{
    	try {
			return drv.attributeExists(attrUid);
		} catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
		}
    }
    
    //Indexes    
    public KrnIndex createIndex(KrnClass cls,KrnAttribute[] attrs,boolean[] descs) throws KrnException{
    	return createIndex(cls,attrs,descs,null);
    }
    public KrnIndex createIndex(KrnClass cls,KrnAttribute[] attrs,boolean[] descs,String uid) throws KrnException{
    	try{    		
    		KrnIndex ndx = drv.createIndexInfo(uid, cls.id);
    		for(int i=0;i<attrs.length;i++){
    			drv.createIndexKeyInfo(ndx.getId(), attrs[i].id, i + 1,descs[i]);
    		}
    		try{
    			drv.createIndex(ndx.getId(),ndx.getUID(),cls.id,attrs);
    		}catch(DriverException e){
    			drv.rollback();
    			drv.deleteIndexInfo(ndx.getId());
    			drv.commit();
    			throw new DriverException(e);
    		}
    		return ndx;
    	}catch(DriverException e){
    		log.error(e, e);
    		throw new KrnException(e.getErrorCode(), e.getMessage());
    	}
    	
    }
           
    public KrnIndex getIndexByUid(String uid) throws KrnException{
    	try{
    		return drv.getIndexByUid(uid);
    	}catch(DriverException e){
    		log.error(e,e);
    		throw new KrnException(e.getErrorCode(),e.getMessage());
    	}
    }
    
    public KrnIndex[] getIndexesByClassId(KrnClass cls) throws KrnException{
    	try{    		
    		List<KrnIndex> list = drv.getIndexesByClassId(cls.id);    		
    		return (KrnIndex[])list.toArray(new KrnIndex[list.size()]);
    	}catch(DriverException e){
    		log.error(e, e);
    		throw new KrnException(e.getErrorCode(), e.getMessage());
    	}
    }
    
    public KrnIndexKey[] getIndexKeysByIndexId(KrnIndex ndx) throws KrnException{
    	try{
    		List<KrnIndexKey> list = drv.getIndexKeysByIndexId(ndx.getId());
    		return (KrnIndexKey[])list.toArray(new KrnIndexKey[list.size()]);
    	}catch(DriverException e){
    		log.error(e, e);
    		throw new KrnException(e.getErrorCode(), e.getMessage());
    	}
    }
    
    public KrnAttribute[] getAttributesForIndexing(KrnClass cls) throws KrnException{
    	try{
    		List<KrnAttribute> list = drv.getAttributesForIndexing(cls.id);
    		return (KrnAttribute[])list.toArray(new KrnAttribute[list.size()]);
    	}catch(DriverException e){
    		log.error(e, e);
    		throw new KrnException(e.getErrorCode(),e.getMessage());
    	}
    }
    
    public void deleteIndex(KrnIndex ndx) throws KrnException{
    	try{    		
    		drv.deleteIndex(ndx.getId(),ndx.getUID(),ndx.getClassId());    		
    	}catch(DriverException e){
    		log.error(e, e);
    		throw new KrnException(e.getErrorCode(), e.getMessage());
    	}
    }
    
    public boolean indexExists(String indexUid) throws KrnException{
    	try {
			return drv.indexExists(indexUid);
		} catch (DriverException e) {
    		log.error(e, e);
    		throw new KrnException(e.getErrorCode(), e.getMessage());
		}
    }

    // SrvObjects
    public int consolidateObjects(KrnObject consObj, KrnObject[] objs, int tid) throws KrnException {
        throw new KrnException(0, "Not implemented");
    }

    public KrnObject createObject(KrnClass cls, long tid) throws KrnException {
        try {
            // Создание Объекта
        	executeClsTriggerEventExpression(0, cls, null, tid);
            KrnObject obj = drv.createObject(cls.id, tid, 0, null);
            executeClsTriggerEventExpression(1, cls, obj, tid);
            if (tid == 0 && isUserLog && (contexts_.isEmpty()||!getContext().isNotWriteSysLog)) {
            	writeCreateObjectLogRecord(obj.id, cls.name, tid);
                StringBuilder out = new StringBuilder();
                out.append("Создание объекта:id=").append(obj.id).append("; класс=").append(cls.name).append(";");
                if (user != null) {
                    out.append(" пользователь:").append(user.getUserName()).append(" IP:").append(user.getIp());
                }
                log.info(out.toString());
            }
            return obj;
        } catch (DriverException e) {
            rollbackTransaction();
            log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public KrnObject createObject(KrnClass cls, Map<Pair<KrnAttribute, Long>, Object> initValues, long tid) throws KrnException {
        try {
            // Создание Объекта
        	executeClsTriggerEventExpression(0, cls, null, tid);
            KrnObject obj = drv.createObject(cls.id, tid, 0, null, initValues);
        	executeClsTriggerEventExpression(1, cls, obj, tid);
            if(tid == 0 && isUserLog  && (contexts_.isEmpty()||!getContext().isNotWriteSysLog)){
            	writeCreateObjectLogRecord(obj.id, cls.name, tid);
				log.info("Создание объекта:id=" + obj.id + "; класс=" + cls.name + "; пользователь:" + user.getUserName() + " IP:" + user.getIp());
				if(initValues!=null && initValues.size()>0){
					for(Pair<KrnAttribute, Long> p:initValues.keySet()){
						writeSetValueLogRecord(obj.id, p.first.id, p.second, initValues.get(p), tid);
					}
				}
            }
            return obj;
        } catch (DriverException e) {
            rollbackTransaction();
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public void createObjects(KrnClass cls, List<Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>>> objValues, long tid,
    		boolean executeTriggers, boolean logRecords) throws KrnException {
        try {
        	// Выполнение триггеров до создания
        	if (executeTriggers) {
        		for (Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>> objValue : objValues) {
	        		executeClsTriggerEventExpression(0, cls, null, tid);
        		}
        	}
	        	
            // Создание Объектов
	        drv.createObjects(cls.id, tid, objValues, logRecords);
	
        	// Выполнение триггеров после создания
	        // Логирование
	        
        	for (Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>> objValue : objValues) {
        		KrnObject obj = objValue.first;
        		Map<Pair<KrnAttribute, Long>, Object> initValues = objValue.second;
	            if (executeTriggers) {
	            	executeClsTriggerEventExpression(1, cls, obj, tid);
	            }
	            if (logRecords && tid == 0 && isUserLog  && (contexts_.isEmpty()||!getContext().isNotWriteSysLog)){
	            	writeCreateObjectLogRecord(obj.id, cls.name, tid);
					log.info("Создание объекта:id=" + obj.id + "; класс=" + cls.name + "; пользователь:" + user.getUserName() + " IP:" + user.getIp());
					if(initValues!=null && initValues.size()>0){
						for(Pair<KrnAttribute, Long> p:initValues.keySet()){
							writeSetValueLogRecord(obj.id, p.first.id, p.second, initValues.get(p), tid);
						}
					}
	            }
        	}
        } catch (DriverException e) {
            rollbackTransaction();
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public KrnObject updateObject(KrnClass cls, KrnObject obj, Map<Pair<KrnAttribute, Long>, Object> initValues, long tid) throws KrnException {
    	KrnObject res;
        try {
            // Изменение Объекта
            res = drv.updateObject(cls.id, tid, obj.id, obj.uid, initValues, true);

            if(tid == 0 && isUserLog && (contexts_.isEmpty()||!getContext().isNotWriteSysLog)){
                writeLogRecord(SystemEvent.EVENT_OBJECT_UPDATE, "ID:" + obj.id + ", КЛАСС:" + cls.name,cls.id,-1);
                log.info("Изменение объекта:id="+obj.id+"; класс="+
                        cls.name+ "; пользователь:"+user.getUserName()+" IP:"+user.getIp());
				if(initValues!=null && initValues.size()>0){
					for(Pair<KrnAttribute, Long> p:initValues.keySet()){
						writeSetValueLogRecord(obj.id, p.first.id, p.second, initValues.get(p), tid);
					}
				}
            }
        } catch (DriverException e) {
            rollbackTransaction();
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
        return res;
    }

    public void updateObjects(KrnClass cls, List<Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>>> objValues, long tid,
    		boolean executeTriggers, boolean logRecords) throws KrnException {
        try {
            // Изменение Объекта
             drv.updateObjects(cls.id, tid, objValues, logRecords);

            for (Pair<KrnObject, Map<Pair<KrnAttribute, Long>, Object>> objValue : objValues) {
        		KrnObject obj = objValue.first;
        		Map<Pair<KrnAttribute, Long>, Object> initValues = objValue.second;
	            if (logRecords && tid == 0 && isUserLog  && (contexts_.isEmpty()||!getContext().isNotWriteSysLog)){
	                writeLogRecord(SystemEvent.EVENT_OBJECT_UPDATE, "ID:" + obj.id + ", КЛАСС:" + cls.name,cls.id,-1);
	                log.info("Изменение объекта:id="+obj.id+"; класс="+
	                        cls.name+ "; пользователь:"+user.getUserName()+" IP:"+user.getIp());
					if(initValues!=null && initValues.size()>0){
						for(Pair<KrnAttribute, Long> p:initValues.keySet()){
							writeSetValueLogRecord(obj.id, p.first.id, p.second, initValues.get(p), tid);
						}
					}
	            }
        	}
        } catch (DriverException e) {
            rollbackTransaction();
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public void userCreated(String name) {
        writeLogRecord(SystemEvent.EVENT_USER_CREATE, name, getClassByName("User").id, -1);
    }

    public void userDeleted(String name) {
        writeLogRecord(SystemEvent.EVENT_USER_DELETE, name, getClassByName("User").id, -1);
    }

    public void userRightsChanged(String name) {
        writeLogRecord(SystemEvent.EVENT_USER_RIGHTS, name, getClassByName("User").id, -1);
    }

    public void userBlocked(String name) {
        writeLogRecord(SystemEvent.EVENT_USER_BLOCK, name, getClassByName("User").id, -1);
    }

    public void userUnblocked(String name) {
        writeLogRecord(SystemEvent.EVENT_USER_UNBLOCK, name, getClassByName("User").id, -1);
    }

    public KrnObject createObjectWithUid(KrnClass cls, String uid, long tid) throws KrnException {
        try {
            // Создание Объекта
        	executeClsTriggerEventExpression(0, cls, null, tid);
            KrnObject obj = drv.createObject(cls.id, tid, 0, uid);
        	executeClsTriggerEventExpression(0, cls, obj, tid);
			if (tid == 0 && isUserLog && (contexts_.isEmpty()||!getContext().isNotWriteSysLog)) {
            	writeCreateObjectLogRecord(obj.id, cls.name, tid);
				log.info("Создание объекта:id=" + obj.id + "; класс=" + cls.name + "; пользователь:" + user.getUserName() + " IP:" + user.getIp());
			}
            return obj;
        } catch (DriverException e) {
            rollbackTransaction();
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public KrnObject[] getClassObjects(KrnClass cls, long[] filterIds, long tid)
    throws KrnException {
    	return getClassObjects2(cls, filterIds, new int[1], tid);
    }
    
    public KrnObject[] getClassOwnObjects(KrnClass cls, long tid)
    	    throws KrnException {try {
                List<KrnObject> res = Collections.emptyList();
                if(cls!=null) {
                    res = drv.getOwnObjects(cls.id, tid);
                }
                return (KrnObject[])res.toArray(new KrnObject[res.size()]);
            } catch (DriverException e) {
            	log.error(e, e);
                throw new KrnException(e.getErrorCode(), e.getMessage());
            }
    	    }
    
    public KrnObject[] getClassObjects2(KrnClass cls, long[] filterIds, int[] limit, long tid)
            throws KrnException {
        try {
            List<KrnObject> res = Collections.emptyList();
            if(filterIds.length > 0) {
                res = drv.filter(filterIds,
                        user != null && user.getDataLanguage()!=null? user.getDataLanguage().id : 102,
                        user != null ? user.getUserObj().id : -1, getSelectedBases(), getSrvOrLang(),
                        limit,null,null, tid, this);
            } else if(cls!=null) {
                res = drv.getObjects(cls.id, limit, tid);
            }
            return (KrnObject[])res.toArray(new KrnObject[res.size()]);
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

	public QueryResult getClassObjects3(
			KrnClass cls,
			AttrRequest req,
			long[] filterIds,
			int limit,
			long tid,
			String info
	) throws KrnException {
		try {
			int[] lh = { limit };
			List<Object[]> rows = Collections.emptyList();
			if (filterIds.length > 0) {
				List<KrnObject> objs = drv.filter(filterIds, user != null
						&& user.getDataLanguage() != null ? user
						.getDataLanguage().id : 102, user != null ? user
						.getUserObj().id : -1, getSelectedBases(),
						getSrvOrLang(), lh,null,null, tid, this);
				long[] objIds = kz.tamur.util.Funcs.makeObjectIdArray(objs);
				rows = drv.getObjects(cls.id, objIds, req, tid, new int[] {limit}, 0, info,this);
			} else {
				rows = drv.getObjects(cls.id, null, req, tid, new int[] {limit}, 0, info,this);
			}
			return new QueryResult(lh[0], rows);
		} catch (DriverException e) {
			log.error(e, e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		}
	}

	public KrnObject[] getObjects(KrnClass cls, long[] filterIds,
			int[] limit, long tid) throws KrnException {
		try {
			List<KrnObject> res = Collections.emptyList();
			if (filterIds.length > 0) {
				res = drv.filter(filterIds, user != null
						&& user.getDataLanguage() != null ? user
						.getDataLanguage().id : 102, user != null ? user
						.getUserObj().id : -1, getSelectedBases(),
						getSrvOrLang(), limit,null,null, tid, this);
			} else {
				res = drv.getObjects(cls.id, limit, tid);
			}
			return (KrnObject[]) res.toArray(new KrnObject[res.size()]);
		} catch (DriverException e) {
			log.error(e, e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		}
	}

    public KrnObject[] getObjectsOfClass(KrnClass AClass) throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public KrnObject[] getObjectsLiveOfClass(KrnClass AClass) throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public KrnObject[] getObjectsByAttribute(
            long classId, long attrId, long langId, int op, Object value, long tid
            ) throws KrnException {
        try {
            List<KrnObject> res = drv.getObjectsByAttribute(
                    classId, attrId, langId, op, value, tid);
            return (KrnObject[])res.toArray(new KrnObject[res.size()]);
        } catch (DriverException ex) {
        	log.error(ex, ex);
            throw new KrnException(ex.getErrorCode(), ex.getMessage());
        }
    }

    public KrnObject[] getObjectsByAttribute(
            long classId, long attrId, long langId, int op, Object value, long tid,KrnAttribute[] krnAttrs
            ) throws KrnException {
        try {
            List<KrnObject> res = drv.getObjectsByAttribute(
                    classId, attrId, langId, op, value, tid, krnAttrs);
            return (KrnObject[])res.toArray(new KrnObject[res.size()]);
        } catch (DriverException ex) {
                log.error(ex, ex);
            throw new KrnException(ex.getErrorCode(), ex.getMessage());
        }
    }
    
    public long[] getFilteredObjectIds2(String[] filterUids,
                                       FilterDate[] dates,
                                       int[] limit) throws KrnException {
        return getFilteredObjectIds2(filterUids, dates, limit, 0);
    }

    public long[] getFilteredObjectIds2(String[] filterUids,
                                       FilterDate[] dates,
                                       int[] limit,
                                       int trId) throws KrnException {
        try {
            List<KrnObject> fobjs = drv.getObjectsByUids(filterUids, trId, false);
            long[] fids = new long[fobjs.size()];
            for (int i = 0; i < fids.length; ++i)
                fids[i] = fobjs.get(i).id;
            return getFilteredObjectIds(fids, dates, limit,null,null, trId);
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public long[] getFilteredObjectIds(long[] filterIds, FilterDate[] dates, int[] limit)
            throws KrnException {
        return getFilteredObjectIds(filterIds, dates, limit,null,null, 0);
    }

    public long[] getFilteredObjectIds(long[] filterIds, FilterDate[] dates, int[] limit,int[] beginRows,int[] endRows, long trId)
            throws KrnException {
        try {
            filterDates_.clear();
            for (int i = 0; i < dates.length; i++) {
                FilterDate date = dates[i];
                filterDates_.put(new Integer(date.type), date.date);
            }
            getSrvOrLang().getDateOp().setFilterDates(filterDates_);
            List<KrnObject> objs = drv.filter(filterIds,
                    user != null && user.getDataLanguage()!=null? user.getDataLanguage().id : 102,
                    user != null?user.getUserObj().id:-1,getSelectedBases(),getSrvOrLang(),
                    limit,beginRows,endRows, trId, this);

            long[] res = new long[objs.size()];
            for (int i = 0; i < objs.size(); ++i)
                res[i] = objs.get(i).id;
            return res;
        } catch (DriverException e) {
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public Set<Long> getObjIds(long classId, long[] filterIds, long trId)
            throws KrnException {
        try {
            List<KrnObject> objs = drv.filter(filterIds,
                    user != null && user.getDataLanguage()!=null? user.getDataLanguage().id : 102,
                    user != null?user.getUserObj().id:-1,getSelectedBases(),getSrvOrLang(),
                    new int[1],null,null, trId, this);
            Set<Long> res = new HashSet<Long>(objs.size());
            for (Iterator<KrnObject> it = objs.iterator(); it.hasNext();) {
                KrnObject obj = it.next();
                res.add(new Long(obj.id));
            }
            return res;
        } catch (DriverException e) {
            log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public KrnObject[] getFilteredObjects(KrnObject filterObj, int limit,int beginRow,int endRow, long trId)
            throws KrnException {
        List<KrnObject> res = filter(filterObj, limit,beginRow,endRow, trId);
        return res.toArray(new KrnObject[res.size()]);
    }

    public List<KrnObject> filterLocal(String sql, int limit, int beginRow,int endRow,long trId) throws KrnException {
        try {
            return drv.filterLocal(sql,Driver.FILTER_LOCAL,
                    user != null? user.getDataLanguage().id : 102,
                    user.getUserObj().id,getSelectedBases(),getSrvOrLang(),
                    limit,beginRow,endRow, trId, this);
        } catch (DriverException e) {
            throw new KrnException( e.getMessage(),e.getErrorCode(),e);
        }
    }
    public List<KrnObject> filter(KrnObject filterObj, int limit, int beginRow,int endRow,long trId) throws KrnException {
        if (filterObj == null) {
            return Collections.emptyList();
        }
        try {
            return drv.filter(new long[]{filterObj.id},
                    user != null ? user.getDataLanguage().id : 102,
                    user.getUserObj().id,getSelectedBases(),getSrvOrLang(),
                    new int[] {limit},new int[] {beginRow},new int[] {endRow}, trId, this);
        } catch (DriverException e) {
            throw new KrnException( e.getMessage(),e.getErrorCode(),e);
        }
    }

    public long filterCount(KrnObject filterObj, long trId) throws KrnException {
        if (filterObj == null) {
            return 0;
    }
        try {
            return drv.filterCount(new long[]{filterObj.id},
                    user != null? user.getDataLanguage().id : 102,
                    user.getUserObj().id,getSelectedBases(),getSrvOrLang(), trId, this);
        } catch (DriverException e) {
            throw new KrnException( e.getMessage(),e.getErrorCode(),e);
        }
    }
    public List<Object> filterGroup(KrnObject filterObj,long trId) throws KrnException {
        if (filterObj == null) {
            return Collections.emptyList();
        }
        try {
            return drv.filterGroup(new long[]{filterObj.id},
                    user != null? user.getDataLanguage().id : 102,
                    user.getUserObj().id,getSelectedBases(),getSrvOrLang(), trId, this);
        } catch (DriverException e) {
            throw new KrnException( e.getMessage(),e.getErrorCode(),e);
        }
    }
    public long filterToAttr(KrnObject filterObj,long pobjId,long attrId,long trId) throws KrnException {
        if (filterObj == null) {
            return 0;
        }
        try {
            return drv.filterToAttr(filterObj.id,pobjId,attrId,
                    user != null? user.getDataLanguage().id : 102,
                    user.getUserObj().id,getSelectedBases(),getSrvOrLang(), trId, this);
        } catch (DriverException e) {
            throw new KrnException( e.getMessage(),e.getErrorCode(),e);
        }
    }
    public KrnObject cloneObject(KrnObject obj, int langId) throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public Set<KrnClass> getSubClasses(long classId) throws KrnException {
        return getSubClasses(classId, false);
    }

    public Set<KrnClass> getSubClasses(long classId, boolean recursive) throws KrnException {
        Set<KrnClass> res = new HashSet<KrnClass>();
        db.getSubClasses(classId, recursive, res);
        return res;
    }

    public void setValue(KrnObject obj, long attrId, int i, long langId, long tid, Object value, boolean insert) throws KrnException {
    	setValue(obj, attrId, i, langId, tid, value, null, insert);
    }
    
    public void setValue(KrnObject obj, long attrId, int i, long langId, long tid, Object value, Object oldValue, boolean insert) throws KrnException {
        try {
        	KrnAttribute attr = getAttributeById(attrId);
        	// Контроль модификатора доступа
    		if (attr.accessModifierType == 1) { // protected
    			
    		} else if (attr.accessModifierType == 2) {	// private
    			
    		}
        	executeAttrTriggerEventExpression(attrId, value, 0, obj.id, langId, tid, false, null);
            drv.setValue(obj, attrId, i, langId, tid, value, oldValue, insert);
        	executeAttrTriggerEventExpression(attrId, value, 1, obj.id, langId, tid, false, null);
            if (tid == 0 && (contexts_.isEmpty()||!getContext().isNotWriteSysLog))
    			writeSetValueLogRecord(obj.id, attrId, langId, value, 0);
        	if (attr.typeClassId == CID_BLOB && attr.isIndexed  && !db.inJcrRepository(attr.id)) {
        		Indexer.updateIndex(obj, attr, kz.tamur.util.Funcs.toByteArray(value), langId, tid, this);
        	}
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
        	log.error(e, e);
            throw new KrnException(0, e.getMessage());
        }
    }

    void setValue(long objectId, long attrId, int i, long langId, long tid, Object val, Object oldVal, boolean insert) throws KrnException {
        KrnObject obj = getObjectById(objectId, tid);
        setValue(obj, attrId, i, langId, tid, val, oldVal, insert);
    }

    public Map<Pair<KrnAttribute, Long>, Object> setValue(KrnObject obj, KrnAttribute attr, long langId, Object val, Map<Pair<KrnAttribute, Long>, Object> cache)
            throws KrnException {
    	if (cache == null)
    		cache = new HashMap<Pair<KrnAttribute,Long>, Object>();
    	if (attr.typeClassId == CID_DATE && val instanceof java.util.Date)
    		val = new java.sql.Date(((java.util.Date)val).getTime());
    	else if (attr.typeClassId == CID_TIME && val instanceof java.util.Date)
    		val = new java.sql.Timestamp(((java.util.Date)val).getTime());
    	cache.put(new Pair<KrnAttribute, Long>(attr, langId), val);
    	return cache;
    }
    
    public void saveValues(KrnObject obj, long trId, Map<Pair<KrnAttribute, Long>, Object> values) throws KrnException {
        try {
        	drv.setValue(obj, trId, values);
        	for (Pair<KrnAttribute, Long> key : values.keySet()) {
        		KrnAttribute attr = key.first;
        		long langId = key.second;
                if (trId == 0 && (contexts_.isEmpty()||!getContext().isNotWriteSysLog)) {
        			writeSetValueLogRecord(obj.id, attr.id, langId, values.get(key), 0);
                }
                if (attr.typeClassId == CID_BLOB && attr.isIndexed) {
            		Indexer.updateIndex(obj, attr, kz.tamur.util.Funcs.toByteArray(values.get(key)), langId, trId, this);
            	}
        	}
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
        	log.error(e, e);
            throw new KrnException(0, e.getMessage());
        }
    }

    public KrnObject[] getObjects(long objId, long attrId,
                                  long[] filterIds, long tid) throws KrnException {
        KrnAttribute attr = getAttributeById(attrId);
        Set<Long> ids =null;
        if(filterIds.length>0)
           ids = getObjIds(attr.typeClassId, filterIds, tid);
        try {
            long[] objIds = {objId};
            Object[] vs = makeArray(drv.getValues(objIds, ids, attrId, 0, tid));
            KrnObject[] res = new KrnObject[vs.length];
            for (int i = 0; i < vs.length; i++) {
                KrnObject v = (KrnObject)vs[i];
                res[i] = (v != null) ? v : nullObject;
            }
            return res;
        } catch (DriverException e) {
            log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public IerValue[] getIerValues(long[] objIds, int attrId, int tid)
            throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public void lock(long objId, long flowId) throws KrnException {
		ExecutionComponent exeComp = getExeComp();
		if (exeComp != null) {
			exeComp.lock(objId, flowId, this);
		}
	}

    public void cachedLock(long objId, long processId, long flowId){
		if (!user.cachedLock(objId, processId)) {
	    	try {
	    		UserSession myUs = getUserSession();
	    		lockObject(objId, processId, Lock.LOCK_FLOW, flowId, myUs.getId().toString());
	    		commitTransaction();
	    	} catch (KrnException e) {
	    		log.error(e, e);
	    		rollbackTransactionQuietly();
	    	}
		}
    }
    
    public KrnObject[] getCachedConflictLocker(long objId, long flowId, long pdId) {
		if (!user.cachedUnlocked(objId, pdId)) {
			ExecutionComponent exeComp = getExeComp();
			if (exeComp != null) {
				List<Lock> locks = exeComp.getConflictLocker(objId, flowId, pdId, this);
				KrnObject[] objs = new KrnObject[locks.size()];
				for (int i = 0; i < locks.size(); i++) {
					try {
						objs[i] = getObjectById(locks.get(i).lockerId, 0);
					} catch (KrnException e) {
						log.error(e, e);
					}
				}
				return objs;
			}
		}
		return new KrnObject[0];
	}

    public void unlock(long objId, long flowId) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
    	if(exeComp!=null)
    		exeComp.unlock(objId,flowId,this);
    }

    public void cachedUnlock(long objId, long processId){
    	user.cachedUnlock(objId, processId);
    }

    public KrnObject getLocker(long objId, long processId) {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp != null) {
            KrnObject res = exeComp.getLocker(objId, processId, this);
            if (res != null) {
            	return res;
            }
        }
        return nullObject;
    }

    public KrnObject getCachedLocker(long objId, long processId) {
		if (!user.cachedUnlocked(objId, processId)) {
	    	ExecutionComponent exeComp = getExeComp();
	        if(exeComp != null) {
	            KrnObject res = exeComp.getLocker(objId, processId, this);
	            if (res != null) {
	            	return res;
	            }
	        }
		}
        return nullObject;
    }

    public KrnObject[] getConflictLocker(long objId, long flowId, long pdId) {
		ExecutionComponent exeComp = getExeComp();
		if (exeComp != null) {
			List<Lock> locks = exeComp.getConflictLocker(objId, flowId, pdId, this);
			KrnObject[] objs = new KrnObject[locks.size()];
			for (int i = 0; i < locks.size(); i++) {
				try {
					objs[i] = getObjectById(locks.get(i).lockerId, 0);
				} catch (KrnException e) {
					log.error(e, e);
				}
			}
			return objs;
		}
		return new KrnObject[0];
	}

    public boolean isObjectLock(long objId,long processId){
    	try {
    		Lock lock = drv.getLock(objId, processId);
    		return lock != null;
    	} catch (DriverException e) {
            log.error(e.getMessage(), e);
    	}
    	return false;
    }
    
    public boolean isCachedObjectLock(long objId, long processId){
		if (!user.cachedUnlocked(objId, processId)) {
	    	try {
	    		Lock lock = drv.getLock(objId, processId);
	    		return lock != null;
	    	} catch (DriverException e) {
	            log.error(e.getMessage(), e);
	    	}
		}
    	return false;
    }

    public Lock isObjectLock2(long objId, long processId) {
		Lock lock = null;
		try {
			lock = drv.getLock(objId, processId);
		} catch (DriverException e) {
			log.error(e.getMessage(), e);
		}
		return lock;
	}

    public String isObjectLock(long objId) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp!=null)
            return exeComp.isObjectLock(objId, this);
       return "";
    }

    public String isCachedObjectLock(long objId) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if (exeComp!=null) {
            return exeComp.isCachedObjectLock(objId, this);
        }
       return "";
    }

    public List<String> getProcessLocker(long processId){
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp!=null)
             return exeComp.getProcessLocker(processId,this);
        return new ArrayList<String>();
    }
    
    public long[] getProcessDefinitions() throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if (exeComp!=null) {
        	if (User.USE_OLD_USER_RIGHTS)
        		return exeComp.getProcessDefinitions(this);
        	else {
        		UserSession us = getUserSession();
        		return db.getProcessDefinitions(us, (Driver2)drv);
        	}
        } else return null;
    }

    public long getTasksCount() throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp!=null) {
        	UserSession us = getUserSession();
        	String searchText = null;
        	String interfaceLangId = "1";
    		AnyPair[] params=us.getTasksFilter();
    		if (params != null && params.length > 0) {
    			for (AnyPair ap : params) {
    				if ("searchText".equals(ap.name)) {
    					searchText = (String)ap.value;
    				} else if ("interfaceLangId".equals(ap.name)) {
    					interfaceLangId = (String)ap.value;
    				}
    			}
    		}
       		return db.getTasksCount(us, null, null, -1, (Driver2) drv,-1,-1, true, searchText, interfaceLangId);
        } else {
        	return 0;
        }
    }
    
    public Activity[] getTaskList() throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp!=null) {
        	UserSession us = getUserSession();
        	long beginRow=-1,endRow=-1;
        	String orderBy = null;
        	boolean desc = true;
        	String searchText = null;
        	String interfaceLangId = "1";
    		java.util.Date beginDate = null, endDate = null;
    		AnyPair[] params=us.getTasksFilter();
    		if (params != null && params.length > 0) {
    			for (AnyPair ap : params) {
    				if ("beginDate".equals(ap.name)) {
    					beginDate = kz.tamur.util.Funcs.convertDate((com.cifs.or2.kernel.Date)ap.value);
    				} else if ("endDate".equals(ap.name)) {
    					endDate = kz.tamur.util.Funcs.convertDate((com.cifs.or2.kernel.Date)ap.value);
    				} else if ("beginRow".equals(ap.name)) {
    					beginRow = (Long)ap.value;
    				} else if ("endRow".equals(ap.name)) {
    					endRow = (Long)ap.value;
    				} else if ("sortBy".equals(ap.name)) {
    					orderBy = (String)ap.value;
    				} else if ("sortDesc".equals(ap.name)) {
    					desc = (Boolean)ap.value;
    				} else if ("searchText".equals(ap.name)) {
    					searchText = (String)ap.value;
    				} else if ("interfaceLangId".equals(ap.name)) {
    					interfaceLangId = (String)ap.value;
    				}
    			}
    		}
       		List<Activity> acts = db.getTasks(us, beginDate, endDate, -1, (Driver2) drv,beginRow, endRow, true, true, orderBy, desc, searchText, interfaceLangId);
            return acts.toArray(new Activity[acts.size()]);
            //return exeComp.getTaskList(us, us.getTasksFilter());
        }
        else return new Activity[0];
    }
    public static void setMaxActiveCount(int maxThreadCount){
    	ExecutionEngine.setMaxActiveCount(maxThreadCount);
    }
    
    public static void setMaxFlowCount(int maxThreadCount){
    	ExecutionComponent.setMaxFlowCount(maxThreadCount);
    }
    public SuperMap[] getMapList(long[] flowIds) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if (exeComp!=null)
        	return exeComp.getMapList(flowIds, this);
        else return null;
    }
    
    public Map<Long,Long> getActiveFlows(){
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp!=null)
        	return exeComp.getActiveFlows();
        else 
        	return null;
    }

    public void reloadProcessDefinition(long processDefId)throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp!=null){
            exeComp.reloadProcessDefinition(processDefId,this.getUserSession(),false);
        }else return;
    }

    public String[] startProcess(long processDefId,boolean withoutTransaction) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp!=null){
            log.info("Инициализация процесса: id=" + processDefId
                    + "; пользователь: "+ user.getUserName()+" IP:"+user.getComputer());

            return exeComp.startProcessInstance(processDefId,-1,user.getUser(),null,this.getIpAddress(), this.getComputer(),false,withoutTransaction,true, this);
        } else return new String[]{"Системная ошибка!!!"};
    }

    public boolean sendMessage(Element msg,long boxId) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp!=null){
        	long flowId=this.getContext().flowId;
            log.info("Отправка сообщения: boxId=" + boxId + "; flowId: "+ flowId);
            return exeComp.sendMessage(msg,boxId,getContext().flowId);
        } else return false;
    }
    public String[] startProcess(long processDefId, boolean withoutTransaction, boolean start) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp!=null){
            log.info("Инициализация процесса: id=" + processDefId
                    + "; пользователь: "+ user.getUserName()+" IP:"+user.getComputer());

            return exeComp.startProcessInstance(processDefId,-1,user.getUser(),null,this.getIpAddress(), this.getComputer(),false,withoutTransaction,start, this);
        } else return new String[]{"Системная ошибка!!!"};
    }

    public void setMessageStatus(String initId,String prodId,String text){
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp!=null)  exeComp.setMessageStatus(initId,prodId,text,this);

    }
    public boolean reloadSincFlows() throws KrnException {
        boolean res=false;
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp!=null){
            log.info("Перегрузка синхронизованных потоков; пользователь: "+ user.getUserName()+" IP:"+user.getComputer());
            res= exeComp.reloadSincFlows(this);
            if(res)
                log.info("Перегрузка синхронизованных потоков закончена успешно!");
            else
                log.info("Перегрузка синхронизованных потоков не удалась!");
        }
        return res;
    }
    public String[] startProcess(long processDefId, Map<String, Object> vars) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp!=null){
            log.info("Инициализация процесса: id=" + processDefId
                    + "; пользователь: "+ user.getUserName()+" IP:"+user.getComputer());
            UserSrv u = user.getUser();
            return exeComp.startProcessInstance(processDefId,-1,u,vars,this.getIpAddress(), this.getComputer(),false,false,true, this);
        } else return new String[]{"Системная ошибка!!!"};
    }

    public String[] startProcess(long processDefId, long actorId,boolean isTimerTask) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp!=null) {
        	UserSrv u = user.getUser();
             return exeComp.startProcessInstance(processDefId,actorId, u,null,this.getIpAddress(), this.getComputer(), isTimerTask,false,true, this);
        }
        else return null;
    }

    public void setPermitPerform(long flowId,boolean permit){
    	ExecutionComponent exeComp = getExeComp();
    	try{
    		if(exeComp!=null)  exeComp.setPermit(flowId,permit,this);
    	}catch(Exception e){
            log.error(e.getMessage(), e);
        	rollbackTransactionQuietly();
    	}
    }
    
    public void setProcessInitiator(long flowId,long userId){
    	ExecutionComponent exeComp = getExeComp();
    	try{
    		if(exeComp!=null)  exeComp.setProcessInitiator(flowId, userId, this);
    		commitTransaction();
    	}catch(Exception e){
            log.error(e.getMessage(), e);
        	rollbackTransactionQuietly();
    	}
    }
    
    public Object openInterface(long uiId,long flowId,long trId,long pdId){
    	Object res = null;
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp!=null) {
            Context ctx = new Context(new long[0], 0, 0);
            ctx.langId = getUserSession().getIfcLang().id;
    		ctx.flowId=flowId;
    		ctx.trId=trId;
    		ctx.uiId=uiId;
    		ctx.pdId=pdId;
    		this.setContext(ctx);
        	try {
        		res = exeComp.openInterface(flowId,this);
        		commitTransaction();
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            	rollbackTransactionQuietly();
            }finally{
            	restoreContext();
            }
        }
        return res;
    }
    
    public String getNextProcessNode(long flowId) {
		String nodeType = "";
		ExecutionComponent executionComponent = getExeComp();
		if (executionComponent != null) {
			try {
				ExecutionEngine engine = executionComponent.getEngine(flowId, this);
				
				if (engine != null) {
					FlowState flowState = engine.getNextState();
					Node node = flowState.node;
					if (node != null) {
						Collection leavingTransitions = node.getLeavingTransitions();
						if (leavingTransitions.size() == 1) {
							Transition transition = (Transition) leavingTransitions.iterator().next();
							Node to = transition.getTo();
							nodeType = to.getType();
						}
					}	
					else {
						log.info("Поток flowId=" + flowId + " не имеет следующего шага");
					}
				}
			} catch (Exception ex) {
				log.error(ex.getMessage(), ex);
				rollbackTransactionQuietly();
			}
		}
		return nodeType;
	}
    
    public boolean cancelProcess(long flowId,String nodeId, boolean isAll, boolean forceCancel) throws KrnException {
    	return cancelProcess(flowId,nodeId,isAll, forceCancel, false);
    }
    public boolean cancelProcess(long flowId,String nodeId,boolean isAll, boolean forceCancel, boolean isFromEhCache) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp!=null){
        	if(!isContextEmpty() && getContext().flowId==flowId) {//Откат самого себя невозможен
            	log.info("Попытка процесса откатить самого себя: flowId=" + flowId + "; пользователь: "+ user.getUserName() + " IP:" + user.getComputer());
       		}else {
	        	log.info("Остановка процесса: flowId=" + flowId + "; пользователь: "+ user.getUserName() + " IP:" + user.getComputer());
	        	return exeComp.cancelProcessInstance(flowId,nodeId,this.getUserSession(),this.getIpAddress(), this.getComputer(), isAll, forceCancel, isFromEhCache);
       		}
        } 
        return false;
    }
    
    public boolean isRunning(long flowId) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if (exeComp != null) {
            return exeComp.isRunning(flowId,this);
        }
        return false ;
    }

    public boolean reloadFlow(long flowId) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp!=null){
            log.info("Перегрузка процесса: id=" + flowId + "; пользователь: "+ user.getUserName() + " IP:" + user.getComputer());
            return exeComp.reLoadFlow(flowId,this);
        }
        return false ;
    }
    
    public boolean saveFlowParam(long flowId,List<String> args) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp!=null){
            return exeComp.saveFlowParam(flowId,args,this);
        }
        return false ;
    }
    public boolean setSelectedObjects(long flowId,long nodeId,KrnObject[]sel_objs){
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp!=null)  return exeComp.setSelectedObjects(flowId,nodeId,sel_objs,this);
        return false;
    }
    public void setLang(KrnObject lang) throws KrnException{
        this.getUserSession().setLang(lang);
    }
    public void setDataLanguage(KrnObject lang) throws KrnException{
        this.getUserSession().setDataLanguage(lang);
    }
    
    public String performActivity(long flowId, String transition) throws KrnException {
    	return performActivity(getTask(flowId, -1, true, true), transition);
    }
    
    public String performActivity(Activity activity, String transition) throws KrnException {
    	String[] res = performActivitys(new Activity[] {activity}, transition);
        return res != null && res.length > 0 ? res[0] : "";
    }
    
    public String[] performActivitys(Activity[] activitys,String transition) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp!=null) {
                return exeComp.performActivitys(activitys,transition,this,null);
        }
        return null;
    }
    public String[] performActivitys(Activity[] activitys,String transition,String event) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if (exeComp!=null) {
        	return exeComp.performActivitys(activitys,transition,this,event);
        }
        return null;
    }
    public void startTransport(int transportId)throws KrnException{
    	String dsName = getUserSession().getDsName();
        if(transportId<0)
            getMessageCache(dsName).stopTransport(-transportId);
        else
        	getMessageCache(dsName).startTransport(transportId);
    }
    public void restartTransport(int transportId)throws KrnException{
    	String dsName = getUserSession().getDsName();
    	getMessageCache(dsName).resetTransport(transportId);
    }
    public void setTransportParam(byte[] data,int transportId)throws KrnException{
    	String dsName = getUserSession().getDsName();
    	getMessageCache(dsName).setTransportParam(data,transportId);
    }
    public byte[] getTransportParam(int transportId)throws KrnException{
    	String dsName = getUserSession().getDsName();
    	MessageCash mc = getMessageCache(dsName);
        return mc != null ? mc.getTransportParam(transportId) : new byte[0];
    }
    public void reloadBox(KrnObject obj)throws KrnException{
    	String dsName = getUserSession().getDsName();
    	getMessageCache(dsName).reloadBox(obj,this);
    }

    public String resendMessage(Activity act)throws KrnException{
    	ExecutionComponent exeComp = getExeComp();
        return exeComp.resendMessage(act,this);
    }

    public void reloadFilter(long filterId) throws KrnException {
        try {
            db.reloadFilter(filterId, user);
        } catch (Throwable e) {
            log.error(e, e);
        }
    }

    public void removeFilter(long filterId) throws KrnException {
        try {
            db.removeFilter(filterId);
        } catch (Throwable e) {
            log.error(e, e);
        }
    }
    public String compileFilter(long filterId,Element xml) throws KrnException {
    	String res="";
        try {
    		res= drv.compileFilter(filterId, user.getDataLanguage().id, xml, 0, this);
        } catch (Throwable e) {
            log.error(e, e);
        }
        return res;
    }

    public boolean saveFilter(long filterId, long trId) throws KrnException {
    	boolean res=false;
        try {
            KrnClass flrCls = getClassByName("Filter");
            KrnAttribute cfgAttr = getAttributeByName(flrCls, "config");
            KrnObject obj = getObjectById(filterId, 0);
            if(obj==null) return false;
            writeLogRecord(SystemEvent.EVENT_CHANGE_FILTER, obj.uid,flrCls.id,cfgAttr.id);

            byte[] data = getBlob(filterId, cfgAttr.id, 0, 0, trId);
            String sql="";
            if (data != null && data.length > 0) {
            	String dataStr = kz.tamur.util.Funcs.normalizeInput(new String(data, "UTF-8"));
            	dataStr = kz.tamur.util.Funcs.validate(dataStr);
                ByteArrayInputStream is = new ByteArrayInputStream(dataStr.getBytes("UTF-8"));
                SAXBuilder b = XmlUtil.createSaxBuilder();
                Element root = b.build(is).getRootElement();
                is.close();
                if (obj.classId == flrCls.id) {
                	try {
                		setLoadingFile(true);
                		sql = drv.compileFilter(filterId, user.getDataLanguage().id, root, trId, this);
                		/*int indexGroupFunc=sql.indexOf(" GROUPPING ");
                		if(indexGroupFunc>0) {
                			groupping=sql.substring(indexGroupFunc+11);
                    		KrnAttribute grpAttr = getAttributeByName(flrCls, "groupFunc");
                    		setString(filterId, grpAttr.id,0, 0,false, groupping,trId);
                			sql=sql.substring(0,indexGroupFunc);
                		}*/
                		KrnAttribute sqlAttr = getAttributeByName(flrCls, "exprSql");
                		setBlob(filterId, sqlAttr.id, 0, sql.getBytes("UTF-8"), 0, trId);
                	} finally {
                		setLoadingFile(false);
                	}
                }
                db.reloadFilter(filterId, user);
            }
            if(!"".equals(sql))
            	res=true;
            else
    			log.info("ОШИБКА При пересохранении фильтра: id='" + obj.id + ";uid='"+obj.uid+"'!");
        } catch (Throwable e) {
			log.info("ОШИБКА При пересохранении фильтра: id='" + filterId + "'!");
            log.error(e, e);
        }
        return res;
    }
    
    public Element getXmlForFilter(String filterUid,long trId) {
    	Element root=null;
        try {
            KrnClass flrCls = getClassByName("Filter");
            KrnAttribute cfgAttr = getAttributeByName(flrCls, "config");
            KrnObject obj = getObjectByUid(filterUid, 0);
            if(obj!=null) {
	            writeLogRecord(SystemEvent.EVENT_CHANGE_FILTER, obj.uid,flrCls.id,cfgAttr.id);
	
	            byte[] data = getBlob(obj.id, cfgAttr.id, 0, 0, trId);
	            if (data != null && data.length > 0) {
	            	String dataStr = kz.tamur.util.Funcs.normalizeInput(new String(data, "UTF-8"));
	            	dataStr = kz.tamur.util.Funcs.validate(dataStr);
	                ByteArrayInputStream is = new ByteArrayInputStream(dataStr.getBytes("UTF-8"));
	                SAXBuilder b = XmlUtil.createSaxBuilder();
	                root = b.build(is).getRootElement();
	                is.close();
	            }
            }
        } catch (Throwable e) {
			log.info("ОШИБКА При пересохранении фильтра: id='" + filterUid + "'!");
            log.error(e, e);
        }
        return root;
    }

    public void resaveFilters() {
        Set<Long> fSet = new TreeSet<Long>();
        int countFlrs=0;
        int countResave=0;
        int countNotResave=0;
        StringBuilder sb=new StringBuilder();
        try{
            final KrnClass fCls = getClassByName("Filter");
            final KrnClass gfCls = getClassByName("FilterRoot");
            KrnObject[] gfObjs = getClassObjects(gfCls, new long[0], 0);
            final KrnAttribute cnAttr = getAttributeByName(gfCls, "children");
            if(gfObjs.length>0){
                SrvUtils.loadFilters(this, fSet, gfObjs[0].id, cnAttr.id, fCls.id);
            }
            countFlrs=fSet.size();
            for(Iterator<Long> it=fSet.iterator();it.hasNext();){
            	long objId=it.next();
                if(saveFilter(objId, 0))
                	countResave++;
                else{
                	countNotResave++;
                	sb.append(objId+";");
                	if(countNotResave%10==0)
                    	sb.append("\n");
                }
            }
        } catch (Throwable e) {
            log.error(e, e);
        }
        log.info("Пересохранение фильтров завершено: "+ countResave+"/"+countFlrs);
        if(sb.length()>0){
            log.info("Ошибочных фильтров - "+ countNotResave+"/"+countFlrs+":\n"+sb.toString());
        }
    }

    public void reloadFilters(){
        Set<Long> fSet = new TreeSet<Long>();
        try{
            final KrnClass fCls = getClassByName("Filter");
            final KrnClass gfCls = getClassByName("FilterRoot");
            KrnObject[] gfObjs = getClassObjects(gfCls, new long[0], 0);
            final KrnAttribute cnAttr = getAttributeByName(gfCls, "children");
            if(gfObjs.length>0){
                SrvUtils.loadFilters(this, fSet, gfObjs[0].id, cnAttr.id, fCls.id);
            }
            log.info("Перегрузка содержимого фильтров; Пользователь: "+ user.getUserName()+" IP:"+user.getComputer());
            for(Iterator<Long> it=fSet.iterator();it.hasNext();){
                Number fId = it.next();
                reloadFilter(fId.longValue());
            }
        } catch (Throwable e) {
            log.error(e, e);
        }
    }
    public void resaveTriggers() throws KrnException {
        try{
        	drv.updateAllTriggers();
        	commitTransaction();
        } catch (DriverException e) {
            log.error(e, e);
        	rollbackTransactionQuietly();
            throw new KrnException(0, e.getMessage());
        }
    }

    public long[] getSrvObjIds(long[] srv_cls) throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public void setModifySrv() throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public int isModifySrvContent(long[] act_ids, int act_time) throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public void changeTimerTask(long objId, boolean isDelete){
    	ServerTasks serverTasks = getServerTasks(getUserSession().getDsName());
         serverTasks.changeTimerTask(objId,isDelete,this);
    }

    public void executeTask(long objId){
    	ServerTasks serverTasks = getServerTasks(getUserSession().getDsName());
    	serverTasks.executeTask(objId,this);
    }
    public long[] pushObjectAction(int act_id, int obj_id, long[] role_id, int role_push_id, int lid, int forward)
            throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public int setObjectPermit(int act_id, int obj_id,int val_)
            throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public long[] setSrvObject(long[] role_id, int act_id, int obj_id, int tid,
                              int tid_in, boolean cre_tid)
            throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public String[] getDocCondition(int docId) throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public long[] setDynActObject(long[] role_id, int act_id, int obj_id,
                                 int tid, boolean cre_tid)
            throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public void runFlrConvertation(int lid) throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public AnyValue[] getAnyValues(long[] objIds, LongPair[] attrIds,
                                   long[] filterIds, long tid)
            throws KrnException {
        if (objIds.length == 0 || attrIds.length == 0) return new AnyValue[0];
        ArrayList<AnyValue> avals = new ArrayList<AnyValue>();
        try {
            for (byte i = 0; i < attrIds.length; i++) {
            	LongPair p = attrIds[i];
                KrnAttribute attr = getAttributeById(p.first);
                if (attr.typeClassId == CID_STRING
                        || attr.typeClassId == CID_MEMO) {
                    long lid = attr.isMultilingual ? p.second : 0;
                    SortedSet<Value> vs = drv.getValues(objIds, null, attr.id, lid, tid);
                    for(Value v : vs) {
                        avals.add(new AnyValue(v.objectId, i, v.index, v.value));
                    }
                } else if (attr.typeClassId == CID_INTEGER
                        || attr.typeClassId == CID_BOOL) {
                    LongValue[] lvs = getLongValues(objIds, attr.id, tid);
                    for (int j = 0; j < lvs.length; ++j) {
                        LongValue lv = lvs[j];
                        AnyValue av =
                                new AnyValue(lv.objectId, i, lv.index, lv.value);
                        avals.add(av);
                    }
                } else if (attr.typeClassId == CID_DATE) {
                    SortedSet<Value> vs = drv.getValues(objIds, null, attr.id, 0, tid);
                    for(Value v : vs) {
                        Date date = kz.tamur.util.Funcs.convertDate((java.sql.Date)v.value);
                        avals.add(new AnyValue(v.objectId, i, v.index, date));
                    }
                } else if (attr.typeClassId == CID_TIME) {
                    TimeValue[] vs = getTimeValues(objIds, attr.id, tid);
                    for (int j = 0; j < vs.length; ++j) {
                        TimeValue tv = vs[j];
                        AnyValue av =
                                new AnyValue(tv.objectId, i, tv.index, tv.value);
                        avals.add(av);
                    }
                } else if (attr.typeClassId == CID_FLOAT) {
                    FloatValue[] fvs = getFloatValues(objIds, attr.id, tid);
                    for (int j = 0; j < fvs.length; ++j) {
                        FloatValue fv = fvs[j];
                        AnyValue av =
                                new AnyValue(fv.objectId, i, fv.index, fv.value);
                        avals.add(av);
                    }
                } else if (attr.typeClassId == CID_BLOB) {
                    for (int j = 0; j < objIds.length; ++j) {
                        long lid = attr.isMultilingual ? p.second : 0;
                        byte[] value = getBlob(objIds[j], attr.id, 0, lid, tid);
                        AnyValue av =
                                new AnyValue(objIds[j], i, 0, value);
                        avals.add(av);
                    }
                } else {
                    SortedSet<Value> vs = drv.getValues(objIds, null, attr.id, 0, tid);
                    for(Value v : vs) {
                        avals.add(new AnyValue(v.objectId, i, v.index, v.value));
                    }
                }
            }
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
        return avals.toArray(new AnyValue[avals.size()]);
    }

    public ObjectValue[] getObjectValues(long[] objIds, long attrId,
            long[] filterIds, long tid) throws KrnException {
    	return getObjectValues(objIds, attrId, filterIds, new int[1], tid);
	}

	public ObjectValue[] getObjectValues(long[] objIds, long attrId,
                                         long[] filterIds, int[] limit, long tid)
            throws KrnException {
        final KrnAttribute attr = getAttributeById(attrId);
        Set<Long> ids=null;
        if(filterIds.length>0)
            ids = getObjIds(attr.typeClassId, filterIds, tid);
        try {
            SortedSet<Value> vs = drv.getValues(objIds, ids, limit, attrId, 0, tid);
            List<ObjectValue> res = new ArrayList<ObjectValue>(vs.size());
            for (Iterator<Value> it = vs.iterator(); it.hasNext();) {
                Value v = it.next();
                KrnObject obj = (KrnObject)v.value;
                res.add(new ObjectValue(v.objectId, v.index, obj, v.trId));
            }
            return res.toArray(new ObjectValue[res.size()]);
        } catch (DriverException e) {
            log.error(e.getMessage(), e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

	public BlobValue[] getBlobValues(long[] objIds, long attrId, long langId, long tid) throws KrnException {
		try {
			SortedSet<Value> vs = drv.getValues(objIds, null, attrId, langId, tid);
			List<BlobValue> res = new ArrayList<BlobValue>(vs.size());
			for (Iterator<Value> it = vs.iterator(); it.hasNext();) {
				Value v = it.next();
				res.add(new BlobValue(v.objectId, attrId, (byte[])v.value, langId));
			}
			return res.toArray(new BlobValue[res.size()]);
		} catch (DriverException e) {
			log.error(e.getMessage(), e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		}
	}

    public QueryResult getObjects(
			long[] objIds,
			AttrRequest req,
			long tid
	) throws KrnException {
		try {
			List<Object[]> rows = drv.getObjects(0, objIds, req, tid, new int[1], 0, null,this);
			return new QueryResult(rows.size(), rows);
		} catch (DriverException e) {
			log.error(e.getMessage(), e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		}
	}

    public QueryResult getObjects(
			long[] objIds,
			AttrRequest req,
			AttrRequestCache reqCache,
			long tid
	) throws KrnException {
		try {
			List<Object[]> rows = drv.getObjects(0, objIds, req, tid, new int[1], 0, null, reqCache,this);
			return new QueryResult(rows.size(), rows);
		} catch (DriverException e) {
			log.error(e.getMessage(), e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		}
	}

    public QueryResult getObjectValues(long[] objIds, long attrId,
			long[] filterIds, long tid, AttrRequest req)
					throws KrnException {
    	return getObjectValues(objIds, attrId, filterIds, new int[1], tid, req);
    }
    
    public QueryResult getObjectValues(long[] objIds, long attrId,
			long[] filterIds, int[] limit, long tid, AttrRequest req)
					throws KrnException {
		
		final KrnAttribute attr = getAttributeById(attrId);
		Set<Long> ids=null;
		if(filterIds.length>0)
			ids = getObjIds(attr.typeClassId, filterIds, tid);
		try {
			SortedSet<Value> vs = drv.getValues(objIds, ids, limit, attrId, 0, tid);
			List<Long> vobjIds = new ArrayList<Long>(vs.size());
			List<Value> values = new ArrayList<Value>(vs.size());
			for (Iterator<Value> it = vs.iterator(); it.hasNext();) {
				Value v = it.next();
				KrnObject obj = (KrnObject)v.value;
				vobjIds.add(obj.id);
				values.add(v);
			}
			// Оставляем 2 колонки для ID объекта и индекса
			List<Object[]> rows = drv.getObjects(0, Funcs.makeLongArray(vobjIds), req, tid, new int[1], 2, null,this);
			// Добавляем данные в строчки
			for (int i = 0; i < rows.size(); i++) {
				Object[] row = rows.get(i);
				Value value = values.get(i);
				row[row.length - 2] = value.objectId;
				row[row.length - 1] = value.index;
			}
			return new QueryResult(rows.size(), rows);
		} catch (DriverException e) {
			log.error(e.getMessage(), e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		}
	}

    public ObjectValue[] getObjectsByPath(KrnObject obj, KrnAttribute[] path)
            throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public String[] getStrings(long objId, long attrId, long langId, boolean isMemo,
                               long tid) throws KrnException {
        try {
            long[] objIds = {objId};
            Object[] vs = makeArray(drv.getValues(objIds, null, attrId, langId, tid));
            String[] res = new String[vs.length];
            for (int i = 0; i < vs.length; i++) {
                String v = (String)vs[i];
                res[i] = v != null ? v : "";
            }
            return res;
        } catch (DriverException e) {
            log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    private Object[] makeArray(SortedSet<Value> values) {
        if (values.size() > 0) {
            Value last = (Value)values.last();
            Object[] res = new Object[last.index + 1];
            for (Iterator<Value> it = values.iterator(); it.hasNext();) {
                Value v = it.next();
                res[v.index] = v.value;
            }
            return res;
        }
        return new Object[0];
    }

    public long[] getLangs(long objId, long attrId, long tid) throws KrnException {
        try {
            List<Long> objs = drv.getLangs(objId, attrId, tid);
            long[] res = new long[objs.size()];
            for (int i = 0; i < objs.size(); i++) {
                res[i] = ((Number)objs.get(i)).longValue();
            }
            return res;
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public int getMaxIndex(long objId, long attrId, long langId,
                               long tid) throws KrnException {
        try {
            return drv.getMaxIndex(objId, attrId, langId, tid);
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public byte[] getBlob(long objId, long attrId, int index, long langId, long tid)
            throws KrnException {
        byte[][] bs = getBlobs(objId, attrId, langId, tid);
        if (bs.length > 0) {
            return bs[0];
        }
        return new byte[0];
    }

    public byte[][] getBlobs(long objId, long attrId, long langId, long tid)
            throws KrnException {
        try {
            long[] objIds = {objId};
            Object[] vs = makeArray(drv.getValues(objIds, null, attrId, langId, tid));
            byte[][] res = new byte[vs.length][];
            for (int i = 0; i < vs.length; i++) {
                byte[] v = (byte[])vs[i];
                res[i] = v != null ? v : new byte[0];
            }
            return res;
        } catch (DriverException e) {
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public StringValue[] getStringValues(long[] objIds, long attrId, long langId,
                                         boolean isMemo, long tid)
            throws KrnException {
        try {
            SortedSet<Value> vs = drv.getValues(objIds, null, attrId, langId, tid);
            StringValue[] res = new StringValue[vs.size()];
            int i = 0;
            for (Iterator<Value> it = vs.iterator(); it.hasNext();) {
                Value v = it.next();
                res[i++] = new StringValue(v.objectId, v.index, (String)v.value);
            }
            return res;
        } catch (DriverException e) {
            log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public StringValue[] getStringsByPath(KrnObject obj, KrnAttribute[] path)
            throws KrnException {
        throw new KrnException(0, "Операция не поддерживается");
    }

    public long[] getLongs(long objId, long attrId, long tid)
            throws KrnException {
        try {
            long[] objIds = {objId};
            Object[] vs = makeArray(drv.getValues(objIds, null, attrId, 0, tid));
            long[] res = new long[vs.length];
            for (int i = 0; i < vs.length; i++) {
                if (vs[i] instanceof Number) {
                    res[i] = ((Number)vs[i]).longValue();
                } else if (vs[i] instanceof Boolean) {
                    res[i] = ((Boolean)vs[i]).booleanValue() ? 1L : 0L;
                } else {
                    res[i] = 0L;
                }
            }
            return res;
        } catch (DriverException e) {
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public long getLongsSingular(
            KrnObject obj,
            KrnAttribute attr,
            boolean isNotNull
            ) throws KrnException {
        long[] vals = getLongs(obj.id, attr.id, 0);
        if (vals.length == 0) {
            if (isNotNull) {
                String msg = "Attribute [" + attr.id + "] " + attr.name + " not defined of object " + obj.id;
                throw new KrnException(0, msg);
            } else
                return 0;
        }
        return vals[0];
    }

    public String getStringsSingular(
            long objId,
            long attrId,
            long langId,
            boolean isMemo,
            boolean isNotNull
            ) throws KrnException {
        String[] vals = getStrings(objId, attrId, langId, isMemo, 0);
        if (vals.length == 0) {
            if (isNotNull) {
                log.info("У объекта " + objId + " не определено значение свойства c_id=" + attrId);
                throw new KrnException(0, "У объекта " + objId + " не определено значение свойства c_id=" + attrId);
            } else
                return "";
        }
        return vals[0];
    };

    public KrnObject getObjectsSingular(
            long objId,
            long attrId,
            boolean isNotNull
            ) throws KrnException {
        KrnObject[] vals = getObjects(objId, attrId, new long[0], 0);
        if (vals.length == 0) {
            if (isNotNull) {
                String msg = "Attribute " + attrId + " not defined of object " + objId;
                log.info(msg);
                throw new KrnException(0, msg);
            } else
                return null;
        }
        return vals[0];
    };
    
    public SortedSet<Value> getValues(long[] objIds, long attrId, long langId, long tid)
    		throws KrnException {
    	try {
    		return drv.getValues(objIds, null, attrId, langId, tid);
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public LongValue[] getLongValues(long[] objIds, long attrId, long tid)
            throws KrnException {
        try {
            SortedSet<Value> vs = drv.getValues(objIds, null, attrId, 0, tid);
            LongValue[] res = new LongValue[vs.size()];
            int i = 0;
            for (Iterator<Value> it = vs.iterator(); it.hasNext();) {
                Value v = it.next();
                long d = 0L;
                if (v.value instanceof Number) {
                    d = ((Number)v.value).longValue();
                } else if (v.value instanceof Boolean) {
                    d = ((Boolean)v.value).booleanValue() ? 1L : 0L;
                }
                res[i++] = new LongValue(v.objectId, v.index, d);
            }
            return res;
        } catch (DriverException e) {
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public LongValue[] getLongsByPath(KrnObject obj, KrnAttribute[] path)
            throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public double[] getFloats(KrnObject obj, KrnAttribute attr, long tid)
            throws KrnException {
        try {
            long[] objIds = {obj.id};
            Object[] vs = makeArray(drv.getValues(objIds, null, attr.id, 0, tid));
            double[] res = new double[vs.length];
            for (int i = 0; i < vs.length; i++) {
                Double v = (Double)vs[i];
                res[i] = v != null ? v.doubleValue() : 0.0;
            }
            return res;
        } catch (DriverException e) {
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public FloatValue[] getFloatValues(long[] objIds, long attrId, long tid)
            throws KrnException {
        try {
            SortedSet<Value> vs = drv.getValues(objIds, null, attrId, 0, tid);
            FloatValue[] res = new FloatValue[vs.size()];
            int i = 0;
            for (Iterator<Value> it = vs.iterator(); it.hasNext();) {
                Value v = it.next();
                double d = ((Double)v.value).doubleValue();
                res[i++] = new FloatValue(v.objectId, v.index, d);
            }
            return res;
        } catch (DriverException e) {
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public void setValue(long objectId, long attrId, int index, long langId, Object value, long tid, boolean insert) throws KrnException {
        try {
        	executeAttrTriggerEventExpression(attrId, value, 0, objectId, langId, tid, false, null);
            drv.setValue(objectId, attrId, index, langId, tid, value, insert);
        	executeAttrTriggerEventExpression(attrId, value, 1, objectId, langId, tid, false, null);
            if (tid == 0 && (contexts_.isEmpty()||!getContext().isNotWriteSysLog))
    			writeSetValueLogRecord(objectId, attrId, langId, value, 0);
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public void setObject(long objectId, long attrId, int index, long value, long tid, boolean insert) throws KrnException {
        try {
        	executeAttrTriggerEventExpression(attrId, value, 0, objectId, 0, tid, false, null);
            drv.setValue(objectId, attrId, index, 0, tid, new Long(value), insert);
        	executeAttrTriggerEventExpression(attrId, value, 1, objectId, 0, tid, false, null);
            if (tid == 0 && (contexts_.isEmpty()||!getContext().isNotWriteSysLog))
    			writeSetValueLogRecord(objectId, attrId, 0, value, 0);
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public void insertValue(long objectId, long attrId, long lang_id, int index, long value, long tid)
            throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public void setString(long objectId, long attrId, int index, long langId, boolean isMemo, String value, long tid) throws KrnException {
        try {
        	executeAttrTriggerEventExpression(attrId, value, 0, objectId, langId, tid, false, null);
            drv.setValue(objectId, attrId, index, langId, tid, value, false);
        	executeAttrTriggerEventExpression(attrId, value, 1, objectId, langId, tid, false, null);
            if (tid == 0 && (contexts_.isEmpty()||!getContext().isNotWriteSysLog))
    			writeSetValueLogRecord(objectId, attrId, langId, value, 0);
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public void setBlob(long objectId, long attrId, int index, byte[] value, long langId, long tid) throws KrnException {
    	setBlob(objectId, attrId, index, value, null, langId, tid);
    }
    
    public void setBlob(long objectId, long attrId, int index, byte[] value, String charSet, long langId, long tid) throws KrnException {
        try {
            if (value.length > 0) {
            	executeAttrTriggerEventExpression(attrId, value, 0, objectId, langId, tid, false, null);
                drv.setValue(objectId, attrId, index, langId, tid, value, false);
            	executeAttrTriggerEventExpression(attrId, value, 1, objectId, langId, tid, false, null);
                if (tid == 0 && (contexts_.isEmpty()||!getContext().isNotWriteSysLog))
        			writeSetValueLogRecord(objectId, attrId, langId, value, 0);
            } else {
                drv.deleteValue(objectId,attrId,new int[]{index}, langId, tid, false);
                if (tid == 0 && (contexts_.isEmpty()||!getContext().isNotWriteSysLog))
                	writeDeleteValueLogRecord(objectId, attrId, langId, null, tid);
            }
            globalFuncs.updateText(objectId, value, this);
            // Обновляем полнотекстный индекс
            KrnAttribute attr = getAttributeById(attrId);
        	if (attr.typeClassId == CID_BLOB && attr.isIndexed && !db.inJcrRepository(attr.id)) {
        		KrnObject obj = getObjectById(objectId,-1);        		
        		Indexer.updateIndex(obj, attr, value, charSet, langId, tid, this);
        	}
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
        	log.error(e, e);
            throw new KrnException(0, e.getMessage());
        }
    }
    //Jcr repository
    public String putRepositoryData(String paths,String fileName,byte[] data){
    	SrvJcrRepository rep=new SrvJcrRepository();
    	try {
			return rep.putRepositoryData(paths, fileName, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    public byte[] getRepositoryData(String docId){
    	SrvJcrRepository rep=new SrvJcrRepository();
    	try {
			return rep.getRepositoryData(docId);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    public String getRepositoryItemName(String docId){
    	SrvJcrRepository rep=new SrvJcrRepository();
    	try {
			return rep.getRepositoryItemName(docId);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    public String getRepositoryItemType(String docId){
    	SrvJcrRepository rep=new SrvJcrRepository();
    	try {
			return rep.getRepositoryItemType(docId);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    public boolean dropRepositoryItem(String docId){
    	SrvJcrRepository rep=new SrvJcrRepository();
    	try {
			return rep.dropRepositoryItem(docId);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return false;
    }
    public List<String> searchByQuery(String searchName){
    	SrvJcrRepository rep=new SrvJcrRepository();
    	try {
			return rep.searchByQuery(searchName);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
    //

    public void executeAttrTriggerEventExpression(long attrId, Object value, int mode, long objectId, long langId, long tid, boolean isDeleting, KrnObject[] objs) throws KrnException {
        executeAttrTriggerEventExpression(attrId, value, mode, objectId, langId, tid, isDeleting,false, objs);
    }
    public void executeAttrTriggerEventExpression(long attrId, Object value, int mode, long objectId, long langId, long tid, boolean isDeleting, boolean isCommitLongTransaction, KrnObject[] objs) throws KrnException {
    	KrnAttribute attr = getAttributeById(attrId);
    	if (attr.rAttrId > 0) {
    		if (isDeleting) {
				for (KrnObject obj: objs) {
					if (obj != null)
						executeAttrTriggerEventExpression(attr.rAttrId, objectId, mode, obj.id, langId, tid, isDeleting, null);
				}
				return;
    		} else {
	    		KrnObject vobj = value instanceof Number ? getObjectById(((Number) value).longValue(), 0) : (KrnObject) value;
	    		if (vobj != null)
	    			executeAttrTriggerEventExpression(attr.rAttrId, objectId, mode, vobj.id, langId, tid, isDeleting, null);
				return;
    		}
    	} else {
    		if (isDeleting) {
    			value = null;
    		}
    	}
        byte[] eventExpression;
        int tr;
        String event;
	    if (mode == 0) {
	    	eventExpression = attr.beforeEventExpr;
	    	tr = attr.beforeEventTr;
	    	event = "Перед изменением значения атрибута";
	    } else if (mode == 1) {
	    	eventExpression = attr.afterEventExpr;
	    	tr = attr.afterEventTr;
	    	event = "После изменения значения атрибута";
	    } else if (mode == 2) {
	    	eventExpression = attr.beforeDelEventExpr;
	    	tr = attr.beforeDelEventTr;
	    	event = "Перед удалением значения атрибута";
	    } else {
	    	eventExpression = attr.afterDelEventExpr;
	    	tr = attr.afterDelEventTr;
	    	event = "После удаления значения атрибута";
	    }
	    if ((isCommitLongTransaction && tr == 0) || (tid == 0 && tr == 0) || (tid != 0 && tr != 0)) {
	        if (eventExpression != null && eventExpression.length > 0) {
				Object returnValue = null;
				Map<String, Object> vars = new HashMap<String, Object>();
				try {
		    		ASTStart expr = OrLang.createStaticTemplate(new InputStreamReader(new ByteArrayInputStream(eventExpression), "UTF-8"));
		    		if (value instanceof Number && attr.typeClassId > 10)
		    			value = getObjectById(((Number) value).longValue(), 0);
		    		else if (value instanceof Date)
		    			value = kz.tamur.util.Funcs.convertDate((Date)value);
		    		else if (value instanceof Time)
		    			value = kz.tamur.util.Funcs.convertTime((Time)value);
		        	vars.put("VALUE", value);
		        	vars.put("OBJ", getObjectById(objectId, 0));
		        	vars.put("LANG", langId);
		        	vars.put("THIS", attr);
					Context ctx = new Context(new long[0], tid, getUserSession().getDataLanguage().id);
					setContext(ctx);
		            SrvOrLang orlang = getSrvOrLang();
					returnValue = orlang.exec(expr, getClassById(attr.classId), new ArrayList<Object>(), new Stack<String>(), vars);
				} catch (Throwable e) {
					log.error("Ошибка при выполнении события '" + event + "'! (класс '" + getClassById(attr.classId).name + "', атрибут '" + attr.name + "')");
					String msg = (e instanceof EvalException) ? ((EvalException)e).getFullMessage() : e.getMessage();
					throw new KrnException(msg, 0, e.getCause());
				} finally {
					restoreContext();
				}
				if (mode == 0 || mode == 2) {
					if (returnValue instanceof Number && ((Number) returnValue).intValue() == 0) {
						Object msg = vars.get("ERRMSG");
						throw new KrnException(0, msg == null ? "Запрет на" + (mode == 0 ? "изменение" : "удаление") + " значения атрибута триггером " + event + "!" : msg.toString());
					}
		        }
	        }
	    }
    }
    public void executeClsTriggerEventExpression(int mode, KrnClass cls, KrnObject obj, long tid) throws KrnException {
        executeClsTriggerEventExpression(mode,  cls,  obj, false, tid);
    }

    public void executeClsTriggerEventExpression(int mode, KrnClass cls, KrnObject obj, boolean isCommitLongTransaction, long tid) throws KrnException {
        byte[] eventExpression;
        int tr;
        String event;
	    if (mode == 0) {
	    	eventExpression = cls.beforeCreateObjExpr;
	    	tr = cls.beforeCreateObjTr;
	    	event = "Перед созданием объекта";
	    } else if (mode == 1) {
	    	eventExpression = cls.afterCreateObjExpr;
	    	tr = cls.afterCreateObjTr;
	    	event = "После создания объекта";
	    } else if (mode == 2) {
	    	eventExpression = cls.beforeDeleteObjExpr;
	    	tr = cls.beforeDeleteObjTr;
	    	event = "Перед удалением объекта";
	    } else {
	    	eventExpression = cls.afterDeleteObjExpr;
	    	tr = cls.afterDeleteObjTr;
	    	event = "После удаления объекта";
	    }
	    if ((isCommitLongTransaction && tr == 0) || (tid == 0 && tr == 0) || (tid != 0 && tr != 0)) {
	        if (eventExpression != null && eventExpression.length > 0) {
				Object returnValue = null;
				Map<String, Object> vars = new HashMap<String, Object>();
				try {
		    		ASTStart expr = OrLang.createStaticTemplate(new InputStreamReader(new ByteArrayInputStream(eventExpression), "UTF-8"));
		        	vars.put("OBJ", obj);
		        	vars.put("THIS", cls);
					Context ctx = new Context(new long[0], tid, getUserSession().getDataLanguage().id);
					setContext(ctx);
		            SrvOrLang orlang = getSrvOrLang();
					returnValue = orlang.exec(expr, cls, new ArrayList<Object>(), new Stack<String>(), vars);
				} catch (Throwable e) {
					log.error("Ошибка при выполнении события '" + event + "'! (класс '" + cls.name + "')");
					String msg = (e instanceof EvalException) ? ((EvalException)e).getFullMessage() : e.getMessage();
					throw new KrnException(msg, 0, e.getCause());
				} finally {
					restoreContext();
				}
				if (mode == 0 || mode == 2) {
					if (returnValue instanceof Number && ((Number) returnValue).intValue() == 0) {
						Object msg = vars.get("ERRMSG");
						throw new KrnException(0, msg == null ? "Запрет на" + (mode == 0 ? "создание" : "удаление") + " объекта класса триггером '" + event + "'!" : msg.toString());
					}
		        }
	        }
	    }
	    // Вызов триггера на родительском классе	    
	    KrnClass parentCls = getClassById(cls.parentId);
	    if (parentCls != null) {
	    	executeClsTriggerEventExpression(mode, parentCls, obj, isCommitLongTransaction, tid);
	    }
    }
    
    public void setFloat(long objectId, long attrId, int index, double value, long tid) throws com.cifs.or2.kernel.KrnException {
        try {
        	executeAttrTriggerEventExpression(attrId, value, 0, objectId, 0, tid, false, null);
            drv.setValue(objectId, attrId, index, 0, tid, new Double(value), false);
        	executeAttrTriggerEventExpression(attrId, value, 1, objectId, 0, tid, false, null);
            if (tid == 0 && (contexts_.isEmpty()||!getContext().isNotWriteSysLog))
    			writeSetValueLogRecord(objectId, attrId, 0, value, 0);
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public void setLong(long objectId, long attrId, int index, long value, long tid) throws KrnException {
        try {
        	executeAttrTriggerEventExpression(attrId, value, 0, objectId, 0, tid, false, null);
            drv.setValue(objectId, attrId, index, 0, tid, new Long(value), false);
        	executeAttrTriggerEventExpression(attrId, value, 1, objectId, 0, tid, false, null);
            if (tid == 0 && (contexts_.isEmpty()||!getContext().isNotWriteSysLog))
    			writeSetValueLogRecord(objectId, attrId, 0, value, 0);
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public void setLong(List<Long> objectsIds, long attrId, long value, long tid) throws KrnException {
        try {
            drv.setValue(objectsIds, attrId, 0, tid, new Long(value));
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }
    
    public void deleteValue(long objectId, long attrId, int[] indexes, long langId, long tid) throws KrnException {
    	deleteValue(objectId, attrId, indexes, langId, tid, false);
    }

    public void deleteValue(long objectId, long attrId, int[] indexes, long langId, long tid, boolean deleteRefs) throws KrnException {
		try {
			KrnAttribute attr = getAttributeById(attrId);
			KrnObject[] vobjs = null;
			if (attr.rAttrId > 0) {
				vobjs = new KrnObject[indexes.length];
				KrnObject[] objs = getObjects(objectId, attrId, new long[0], tid);
				int i = 0;
				for (int index : indexes) {
					vobjs[i++] = index < objs.length ? objs[index] : null;
				}
			}
        	executeAttrTriggerEventExpression(attrId, indexes, 0, objectId, langId, tid, true, vobjs);
        	drv.deleteValue(objectId, attrId, indexes, langId, tid, deleteRefs);
    		executeAttrTriggerEventExpression(attrId, indexes, 1, objectId, langId, tid, true, vobjs);
		    if (tid == 0 && (contexts_.isEmpty()||!getContext().isNotWriteSysLog))
            	writeDeleteValueLogRecord(objectId, attrId, langId, null, tid);
		} catch (DriverException e) {
			log.error(e, e);
		    throw new KrnException(e.getErrorCode(), e.getMessage());
		}
	}

    public void deleteValueInSet(long objectId, long attrId, Object[] vs, long tid) throws KrnException {
    	List<Object> values = new ArrayList<Object>(vs.length);
    	for(Object any : vs) {
    		values.add(any);
    	}
    	deleteValue(objectId, attrId, values, tid);
    }

    public void deleteValue(long objectId, long attrId, Collection<Object> values, long tid) throws KrnException {
    	deleteValue(objectId, attrId, values, tid, false);
    }
    
    public void deleteValue(long objectId, long attrId, Collection<Object> values, long tid, boolean deleteRefs) throws KrnException {
		try {
			KrnAttribute attr = getAttributeById(attrId);
			KrnObject[] vobjs = null;
			if (attr.rAttrId > 0) {
				vobjs = new KrnObject[values.size()];
				int i = 0;
				for (Object value: values) {
					if (value instanceof KrnObject) {
						vobjs[i] = (KrnObject) value;
					} else {
						vobjs[i] = getObjectById((Long) value, 0);
					}
					i++;
				}
			}	 
    		executeAttrTriggerEventExpression(attrId, values, 0, objectId, 0, tid, true, vobjs);
			drv.deleteValue(objectId, attrId, values, tid, deleteRefs);
    		executeAttrTriggerEventExpression(attrId, values, 1, objectId, 0, tid, true, vobjs);
            if (tid == 0 && (contexts_.isEmpty()||!getContext().isNotWriteSysLog))
            	writeDeleteValueLogRecord(objectId, attrId, 0, null, tid);
		} catch (DriverException e) {
			log.error(e, e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		}
    }

    public void deleteObject(KrnObject obj, long tid) throws KrnException {
    	deleteObject(obj, tid, false);
    }
    
    public void deleteObject(KrnObject obj, long tid, boolean deleteRefs) throws KrnException {
        try {
            KrnClass cls = getClassById(obj.classId);
        	executeClsTriggerEventExpression(2, cls, obj, tid);
            drv.deleteObject(obj, tid, deleteRefs);
            db.removeObjectFromCache(obj.uid);
        	executeClsTriggerEventExpression(3, cls, null, tid);
            // Удаляем объект из полнотекстных индексов
            KrnAttribute[] attrs = getAttributes(cls);
            for (KrnAttribute attr : attrs) {
            	if (attr.typeClassId == CID_BLOB && attr.isIndexed) {
            		Indexer.removeIndex(obj, attr);
            	}
            }
            if(tid == 0 && isUserLog && (contexts_.isEmpty()||!getContext().isNotWriteSysLog)){
            	writeDeleteObjectLogRecord(obj, cls.name, tid);
            }
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
        	log.error(e, e);
            throw new KrnException(0, e.getMessage());
        }
    }

    public void undeleteObject(KrnObject obj, long tid) throws KrnException {
        throw new KrnException(0, "Not implemented");
    }

    public void deleteUnusedObjects(long cid, long aid) throws KrnException {
        throw new KrnException(0, "Not implemented");
    }

    public void updateReferences(long cid, long aid) throws KrnException {
        log.info("Updating references");
        int count = 0;
        KrnClass cls = getClassById(cid);
        long[] revAttrIds = getRevAttributes(aid);
        KrnAttribute rattr = null;
        if (revAttrIds.length > 1) {
            for (int j = 0; j < revAttrIds.length; j++) {
                KrnAttribute a = getAttributeById(revAttrIds[j]);
                if (a.typeClassId == cid) {
                    rattr = a;
                }
            }
        } else if (revAttrIds.length > 0) {
            rattr = getAttributeById(revAttrIds[0]);
        }
        if (rattr != null) {
            KrnObject[] objs = getClassObjects(cls, new long[0], 0);
            long[] objIds = Funcs.makeObjectIdArray(objs);
            ObjectValue[] ovs = getObjectValues(objIds, aid, new long[0], 0);
            Set<Long> vals = new HashSet<Long>();
            for (int i = 0; i < ovs.length; ++i) {
                ObjectValue ov = ovs[i];
                vals.add(ov.value.id);
            }
            long[] valIds = Funcs.makeLongArray(vals);
            ObjectValue[] vovs = getObjectValues(valIds, rattr.id, new long[0], 0);
            MapMap<Long, Integer, Long> mmap = new MapMap<Long, Integer, Long>();
            for (int i = 0; i < vovs.length; i++) {
                ObjectValue vov = vovs[i];
                mmap.put(vov.objectId, vov.index, vov.value.id);
            }
            try {
                for (int i = 0; i < ovs.length; ++i) {
                    ObjectValue ov = ovs[i];
                    SortedMap<Integer, Long> map = (SortedMap<Integer, Long>)mmap.get(ov.value.id);
                    if (map == null || !map.values().contains(ov.objectId)) {
                        int index =
                                (map != null) ? ((Integer) map.lastKey()).intValue() + 1 : 0;
                        try {
                            setLong(ov.value.id, rattr.id, index, ov.objectId, 0);
                            count++;
                        } catch (KrnException e) {//временный try, чтобы не падал при ссылке на удаленный объект,
                            log.error(e.getMessage(), e);//после того как сделаем при удалении объекта и удаление всех ссылок на него,
                                                //то этот try нужно убрать
                        }
                        mmap.put(ov.value.id, new Integer(index), ov.objectId);
                    }
                }
                commitTransaction();
                log.info("Done. " + count + " row(s) inserted.");
            } catch (KrnException e) {
                rollbackTransaction();
                throw e;
            }
        }
    }
    
    public Session() {
    	String sfs_=System.getProperty("sfs");
        if("1".equals(sfs_)) isUserLog=false;

        ResourceRegistry.instance().resourceAllocated(this);
    }
    
    /**
     * Авторизация без проверок политики безопасности.
     *
     * @param us the us
     * @throws KrnException the krn exception
     */
    public void login(UserSession us, boolean withDbConnection) throws KrnException {
    	login(us, withDbConnection, null);
    }
    
    public void login(UserSession us, boolean withDbConnection, Database dataBase) throws KrnException {
    	this.dsName = us.getDsName();
    	this.ownUser = false;
    	initLogs(us.getUserName());
		this.db = dataBase != null ? dataBase : ConnectionManager.instance().getDatabase(dsName);
    	if (withDbConnection) {
	    	try {
	    		if (this.db != null) {
	    			initDriver(us);
	    		}
	        } catch (DriverException e) {
	        	releaseDriver();
	            log.error(e.getMessage(), e);
	            throw new KrnException(0, e.getMessage());
	        }
    	}
		setUserSession((ServerUserSession)us);
    }
    
    public void loginWithoutDB(String dsName, String name, String ip, String pcName) throws KrnException {
    	ServerUserSession us = new ServerUserSession(dsName, null, new UserSrv(name), ip, pcName, SERVER_ID, false);

        this.dsName = dsName;
    	this.ownUser = false;
    	
    	initLogs(name);
		db = ConnectionManager.instance().getDatabase(dsName);
		setUserSession(us);
    }

    /**
     * Авторизация без проверок политики безопасности.
     *
     * @param dsName the ds name
     * @param typeClient the type client
     * @param userObj the user obj
     * @param ip the ip
     * @param pcName the pc name
     * @throws KrnException the krn exception
     */
    public void login(String dsName, String typeClient, KrnObject userObj, String ip, String pcName) throws KrnException {
		this.dsName = dsName;
    	try {
    		db = ConnectionManager.instance().getDatabase(dsName);
    		// Сначала создаем временный драйвер, для поиска юзера при необходимости
	        drv = initDriver(new ServerUserSession(dsName, typeClient, new UserSrv("sys"), ip, pcName, SERVER_ID, false));
	        UserSrv user = getOrgComp().findActorById(userObj.id, this);
	        releaseDriver();
	    	initLogs(user.getUserName());
	        ServerUserSession us = new ServerUserSession(dsName, typeClient, user, ip, pcName, SERVER_ID, false);
	        // теперь уже создаем драйвер под нужным юзером
	        initDriver(us);
			setUserSession(us);
        } catch (Throwable e) {
        	releaseDriver();
        	if (log == null) {
        		initLogs("UNKNOWN");
        		log.error("dsName = " + dsName + ", typeClient = " + typeClient + ", userObj = " + userObj 
        				+ ", ip = " + ip + ", pcName = " + pcName);
        	}
        	log.error("Ошибка авторизации.", e);
            throw new KrnException(0, e.getMessage());
        }
    }
    
    public UserSession login(String name, String typeClient, String pd, String ip, String pcName) throws KrnException {
        this.dsName = BASE_DS_NAME;
        initLogs(name);

        // получить хэш пароля
        if (pd != null) {
            pd = PasswordService.getInstance().encrypt(pd);
        }
        
        if (ConnectionManager.instance().authorizeUser(name, pd)) {
            writeLogRecord(SystemEvent.EVENT_LOGIN, "",-1,-1);
            ServerUserSession us = new ServerUserSession(dsName, typeClient, null, ip, pcName, SERVER_ID, true);
            setUserSession(us);
        	return us;
        }
        
        KrnException ex=new KrnException(USER_NOT_FOUND, "Неверное имя пользователя или пароль", TYPE_WARNING);
        if(ErrorsNotification.isInitialize()){
            String message="Неверное имя пользователя:'"+name+"' или пароль. IP:'"+ip+" PC:"+pcName+"'.";
        	ErrorsNotification.notifyErrors("TO_200","IP_address_"+ip,message,null, this);
        }
        throw ex;
    }
    
    /**
     * Авторизация с проверкой политики безопасности по логину и паролю
     *
     * @param dsName the ds name
     * @param name the name
     * @param typeClient the type client
     * @param pd the pd
     * @param ip the ip
     * @param pcName the pc name
     * @param callbacks the callbacks
     * @return user session value
     * @throws KrnException the krn exception
     */
    public UserSession login(String dsName, String name, String typeClient, String pd, String newPd, String confPd, String ip, String pcName,
            boolean callbacks) throws KrnException {
    	return login(dsName, name, typeClient, pd, newPd, confPd, ip, pcName, callbacks, false, false, false, null);
    }

    private KrnObject[] filterObjects(KrnObject[] objs, long clsId) {
    	List<KrnObject> filteredObjs = new ArrayList<KrnObject>();
    	for (int i = 0; i < objs.length; i++) {
    		if (objs[i].classId == clsId) {
    			filteredObjs.add(objs[i]);
    		}
    	}
    	return filteredObjs.toArray(new KrnObject[filteredObjs.size()]);
    }
    
    public UserSession login(String dsName, String name, String typeClient, String pd, String newPd, String confPd, String ip, String pcName, boolean callbacks, boolean force, boolean sLogin, boolean isUseECP, String signedData) throws KrnException {
        this.dsName = dsName;
        initLogs(name);
        boolean notReached = !callbacks || !isReachedUserSessionLimit();

        if (callbacks && isServerBlocked(ip) == 2) {
            throw new KrnException(SERVER_BLOCKED,
                    "Сервер заблокирован. Попробуйте зайти в программу позже или обратитесь к администратору.", TYPE_WARNING);
        }

        if (notReached) {
            db = ConnectionManager.instance().getDatabase(dsName);

            if (db == null || (callbacks && !db.isReadyForConnection())) {
                throw new KrnException(SERVER_NOT_AVAILABLE,
                        "Сервер с dataSourceNames = '" + dsName + "' не доступен. Возможно это вызванно неправильной конфигурацией. Проверьте соответствие параметра dataSourceNames в web.xml и в файле, указанном в -DinitParamsFile", TYPE_WARNING);
            }
            try {
                initDriver(new ServerUserSession(dsName, typeClient, new UserSrv(name), ip, pcName, SERVER_ID, false));
                OrganisationComponent orgComp = getOrgComp();
	            
                if ("sys".equals(name) && pd == null && !callbacks) {
	                UserSrv user = orgComp.getSuperUser(this);
	                ServerUserSession us = new ServerUserSession(dsName, typeClient, user, ip, pcName, SERVER_ID, callbacks);
	                setUserSession(us);
	                return us;
	            }

	            KrnClass userCls = getClassByName("User");
	            KrnAttribute nameAttr = getAttributeByName(userCls, "name");
	
	            boolean ignCase = "true".equals(System.getProperty("ignoreCase")) || "1".equals(System.getProperty("ignoreCase"));
	
	            KrnObject[] objs = getObjectsByAttribute(userCls.id, nameAttr.id, 0, ignCase ? CO_EQUALS_IGNORE_CASE : CO_EQUALS, ignCase ? name.toUpperCase(Constants.OK) : name, 0);
	            if (objs.length > 1) {
	            	objs = filterObjects(objs, userCls.id);
	            }
	            
 //               if (objs.length > 1)
 //                   throw new KrnException(USER_DUBLICATED, objs[0], "Найдено '" + objs.length + "' пользователей с именем '" + name + "'!", TYPE_WARNING);

	            if (objs.length > 0) {
	            	// Обновление прав доступа пользователя
	            	for (int i = 0; i < objs.length; i++) {
	            		updateUser(objs[i], "");
	            	}
		            // получить хэш пароля
		            if (pd != null) {
		                pd = PasswordService.getInstance().encrypt(pd);
		            }
		
		            // получение необходимых значений политики безопасности
		            getPolicy();
		
		            AttrRequestBuilder arb = new AttrRequestBuilder(userCls, this).add("name").add("password")
		                    .add(ATTR_NUMBER_FAILED_LOGIN).add(ATTR_TIME_LOCK).add("blocked").add("admin").add("multi").add("isLogged").add("isChangedPassBySys")
		                    .add("activated").add("base").add("interface language").add("data language").add("дата изменения пароля").add("iin").add("superadmin");
		            
		            try {
		                arb.add("аватар");
		            } catch (Throwable e) {
		                log.warn("Не найден атрибут 'аватар:blob' в классе 'User'");
		            }
	
		            long[] objIds = new long[] {objs[0].id};
		
		            KrnAttribute faledLogin = getAttributeByName(userCls, ATTR_NUMBER_FAILED_LOGIN);
		            KrnAttribute timeLock = getAttributeByName(userCls, ATTR_TIME_LOCK);
		            KrnAttribute lock = getAttributeByName(userCls, "blocked");
		            //KrnAttribute isLoggedAttr = getAttributeByName(userCls, "isLogged");
		
		            QueryResult qr = getObjects(objIds, arb.build(), 0);
		
		            for (Object[] row : qr.rows) {
		                KrnObject obj = arb.getObject(row);
		                if (obj.classId == userCls.id) {
			                Time timeLockU = (Time) arb.getValue(ATTR_TIME_LOCK, row);
			                boolean lockU = arb.getBooleanValue("blocked", row);
			                long failedCount = arb.getLongValue(ATTR_NUMBER_FAILED_LOGIN, row);
			                String pdBD = (String) arb.getValue("password", row);
			                boolean admin = arb.getBooleanValue("admin", row);
			                boolean superadmin = arb.getBooleanValue("superadmin", row);
			                boolean multi = arb.getBooleanValue("multi", row);
			                KrnObject base = arb.getObjectValue("base", row);
			                KrnObject ifcLang = arb.getObjectValue("interface language", row);
			                KrnObject dataLang = arb.getObjectValue("data language", row);
			                String iin = arb.getStringValue("iin", row);
			                
			                // Проверка сертификата
			                if (isUseECP) {
				                String userDN;
			                	CheckSignResult res;
			                	try {
				                	X509Certificate c = KalkanUtil.getCertificate(signedData);
				                	String fullDN = c.getSubjectDN().getName();
				        			int beg = fullDN.indexOf("IIN");
				        			if (beg > -1) {
				        				int end = fullDN.indexOf(",", beg + 3);
				        				if (end == -1) end = fullDN.length();
				        				userDN = fullDN.substring(beg + 3, end);
				        			} else {
				        				userDN = fullDN;
				        			}
				                	res = KalkanUtil.checkXML(signedData, true);
			                	} catch (Exception e) {
				                	logUnsuccessfullLoginTime(obj);

			                		log.error("Ошибка авторизации.", e);
				                	throw new KrnException(USER_ECP_FAILED, obj,  "Ошибка при проверке ЭЦП!");
			                	}
			                    if (!res.isOK()) {
				                	logUnsuccessfullLoginTime(obj);
	                                throw new KrnException(USER_ECP_FAILED, obj, res.getErrorMessage(true));
			                    }
			                	if (userDN != null && !userDN.equals(iin)) {
				                	logUnsuccessfullLoginTime(obj);
				                	throw new KrnException(USER_IIN_NOT_MATCH, obj, "ИИН пользователя не задан или не совпадает с указанным в сертификате!");
				                }
			                }
			                
			                boolean isLogged = true;
			                java.util.Date activated = null;
			                java.util.Date toActivate = null;
			                boolean isChangedPassBySys = false;
			
			                try {
			                    isLogged = (Boolean) arb.getValue("isLogged", row);
			                } catch (Exception ex) {
			                    outErrorCreateAttrUser("isLogged");
			                }
			                try {
			                	isChangedPassBySys = (Boolean) arb.getValue("isChangedPassBySys", row);
			                } catch (Exception ex) {
			                	 outErrorCreateAttrUser("isChangedPassBySys");
			                }
			
			                try {
			                    Time dataTime = (Time) arb.getValue("activated", row);
			                    activated = (dataTime == null || kz.tamur.rt.Utils.isEmpty(dataTime)) ? null : kz.tamur.util.Funcs
			                            .convertTime(dataTime);
			                    if (activated != null) {
			                        Long t = activated.getTime() + Constants.ONE_DAY * policyWrapper.getMaxPeriodFirstPass();
			                        toActivate = new java.util.Date(t);
			                    }
			                } catch (Exception ex) {
			                    outErrorCreateAttrUser("activated");
			                }
			
			                /** рекомендуемая дата смены пароля(точность до дня)*/
			                java.util.Date expireDate;
			                /** дата последнего редактирования пароля*/
			                java.util.Date editPassDate;
			                /** Дата последнего действия пароля*/
			                java.util.Date lastDatePass;

			                try {
			                    Time dataTime = (Time) arb.getValue("дата изменения пароля", row);
			                    editPassDate = (dataTime == null || kz.tamur.rt.Utils.isEmpty(dataTime)) ? null : kz.tamur.util.Funcs
			                            .convertTime(dataTime);
			                } catch (Exception ex) {
			                    outErrorCreateAttrUser("дата изменения пароля");
			                    editPassDate = null;
			                }
			
			                if (editPassDate == null) {
			                    expireDate = null;
			                    lastDatePass = null;
			                } else {
			                    expireDate = new java.util.Date(editPassDate.getTime() + Constants.ONE_DAY * policyWrapper.getMaxValidPeriod());
			                    lastDatePass = new java.util.Date(editPassDate.getTime() + Constants.ONE_DAY * policyWrapper.getMaxPeriodPassword());
			                }
			
			                boolean isActive = true;
			                // если дата максимального периода действия первого пароля ОПРЕДЕЛЕНА
			                if (policyWrapper.isChangeFirstPass() && policyWrapper.getMaxPeriodFirstPass() > 0) {
			                    // если дата последней возможной активации ЗАДАНА
			                    if (toActivate != null) {
			                        Calendar c1 = new GregorianCalendar(TimeZone.getTimeZone("Asia/Dhaka"));
			                        Calendar c2 = new GregorianCalendar(TimeZone.getTimeZone("Asia/Dhaka"));
			                        // задать дату последней возможной активации
			                        c2.setTime(toActivate);
			                        // сравнить текущую дату с последней возможной активации
			                        isActive = c1.before(c2);
			                    }
			                }
			
			                Calendar cal1 = null;
			                Calendar cal2 = null;
			                // если установлен флаг блокировки
			                if (lockU) {
			                    // если таймаут пустой (то есть пользователя блокировал администратор)
			                    if (timeLockU == null) {
				                	logUnsuccessfullLoginTime(obj);

			                        throw new KrnException(USER_IS_BLOCKED, obj, "Учетная запись заблокирована администратором!", TYPE_WARNING);
			                    } else {
			                        // текущая дата
			                        cal1 = new GregorianCalendar(TimeZone.getTimeZone("Asia/Dhaka"));
			                        // дата из атрибутов пользователя
			                        short day = timeLockU.day;
			                        short month = timeLockU.month;
			                        short year = timeLockU.year;
			                        short hour = timeLockU.hour;
			                        short min = timeLockU.min;
			                        cal2 = new GregorianCalendar(TimeZone.getTimeZone("Asia/Dhaka"));
			                        cal2.set(Calendar.DAY_OF_MONTH, day);
			                        cal2.set(Calendar.MONTH, month);
			                        cal2.set(Calendar.YEAR, year);
			                        cal2.set(Calendar.HOUR_OF_DAY, hour);
			                        cal2.set(Calendar.MINUTE, min);
			                        cal2.set(Calendar.SECOND, 0);
			                        cal2.set(Calendar.MILLISECOND, 0);
			                        // если текущая дата раньше даты разблокировки
			                        if (cal1.before(cal2)) {
					                	logUnsuccessfullLoginTime(obj);

			                            throw new KrnException(USER_IS_BLOCKED, obj, "Учетная запись заблокирована до "
			                                    + ThreadLocalDateFormat.get("dd.MM.yyyy HH:mm").format(cal2.getTime()), TYPE_WARNING);
			                        }
				                    setLong(obj.id, lock.id, 0, 0, 0);
			                    }
			                }
			
			                // если пароль проходит проверку
			                if (pdBD != null && (pd == null || pd.equals(pdBD))) {
			                    // Проверка на возможность доступа пользователя в конструктор
			                    if (Constants.CLIENT_TYPE_DESIGNER.equals(typeClient) && !admin) {
				                	logUnsuccessfullLoginTime(obj);

			                        throw new KrnException(USER_NOT_ADM, obj, "Доступ запрещен!\nВы не являетесь администратором системы!", TYPE_WARNING);
			                    } else {
			                        if (base == null)
			                            throw new KrnException(ErrorCodes.USER_NO_BASE, obj, "messNotBD", TYPE_WARNING);
			                        else if (ifcLang == null)
			                            throw new KrnException(ErrorCodes.USER_NO_IFC_LANG, obj, "messNotLangInf", TYPE_WARNING);
			                        else if (dataLang == null)
			                            throw new KrnException(ErrorCodes.USER_NO_DATA_LANG, obj, "messNotLangData", TYPE_WARNING);
			                    }
			
			                    // Проверка времени активации учётной записи
			                    if (!isLogged && !isActive) {
				                	logUnsuccessfullLoginTime(obj);

			                        if (lock != null) {
			                            userBlocked(name);
			                            setLong(obj.id, lock.id, 0, 1, 0);
			                        }
			                        throw new KrnException(USER_NOT_ACT, obj, "Доступ запрещен!\nИстекло время активации вашей учётной записи!\nОбратитесь к системному администратору.", TYPE_WARNING);
			                    }
			
			                    if (!multi) {
				                    // если не админ и не мульти вход, то проверка на то что пользователь уже подключён к системе
				                    UserSession exUs = findUserSession(obj.id);
				                    // если подключён
				                    if (exUs != null && ip != null && dsName.equals(exUs.getDsName())) {
				                    	if (force) {
				        					log.info("Killing previous logged session of user: " + exUs.getUserName());
				                    		killUserSessions(exUs.getId(), false);
				                        } else {
				                            if (callbacks) {
				                                writeLogRecord(SystemEvent.WARNING_USER_CONNECTED, name, ip, pcName, false, "",-1,-1);
				                            }
				                            throw new KrnException(/*ip.equals(exUs.getIp()) ? */USER_HAS_CONNECT_SAME_IP /*: USER_HAS_CONNECT*/, obj,
				                                    "Пользователь с именем '" + name + "' уже подключен к Системе", TYPE_WARNING);
				                        }
				                    }
			                    }
			
			                    UserSrv user = orgComp.findActorById(obj.id,this);
			                    // Пользователь может быть создан не в конструкторе, тогда необходимо добавить данные о нем в OrganisationComponent
			                    if (user == null) {
			                        orgComp.updateUser(new KrnObject[] { obj }, true, this);
			                        user = orgComp.findActorById(obj.id,this);
			                    }
			
			                    // Проверка IP пользователя
			                    if (policyWrapper.isCheckClientIp() && !superadmin /*&& !user.isAdmin()*/) {
				                	logUnsuccessfullLoginTime(obj);

			                        String allowedIps = user.getIp();
			                        if (allowedIps == null || !allowedIps.contains(ip))
			                            throw new KrnException(USER_NO_RIGHTS, obj, "Отсутствует доступ к Системе с данного рабочего места (" + ip + ")", TYPE_WARNING);
			                    }
			
			                    // Проверка наличия системного права на вход
			                    boolean hasRight = User.USE_OLD_USER_RIGHTS || checkUserHasRight(SystemAction.ACTION_LOGIN, user.getUserId(), null, ip);
			                    if (!hasRight) {
				                	logUnsuccessfullLoginTime(obj);

			                        throw new KrnException(USER_NO_RIGHTS, obj, "Отсутствует право на вход в Систему", TYPE_WARNING);
			                    }
			                    
			                    if (newPd != null && confPd != null && pd != null) {
			                        changePassword(dsName, user.getUserName(), typeClient, ip, pcName, user.getUserObj(), pd.toCharArray(), newPd.toCharArray(), confPd.toCharArray());
			                    } else {
				                    if (!isLogged && policyWrapper.isChangeFirstPass()) {
				                        throw new KrnException(USER_NOT_LOGIN, obj, "Это ваша первая авторизация.\nВам необходимо сменить пароль!", TYPE_WARNING);
				                    } else if (isChangedPassBySys == true){
				                    	throw new KrnException(USER_NOT_LOGIN, obj, "Ваш пароль был сброшен администратором.\nВам необходимо сменить пароль!", TYPE_WARNING);
				                    }
	
				                    Calendar c1 = new GregorianCalendar(TimeZone.getTimeZone("Asia/Dhaka"));
			                        // если дата обязательная смены пароля ОПРЕДЕЛЕНА
			                        if (policyWrapper.getMaxPeriodPassword() > 0 && lastDatePass != null) {
			                            Calendar c2 = new GregorianCalendar(TimeZone.getTimeZone("Asia/Dhaka"));
			                            // задать дату обязательной смены пароля
			                            c2.setTime(lastDatePass);
			                            // сравнить текущую дату с датой обязательной смены пароля
			                            if (c2.before(c1))
			                                throw new KrnException(USER_IS_ENDED, obj, "Доступ запрещен!\nВам необходимо сменить пароль!", TYPE_WARNING);
			                        }
			
			                        // если дата рекомендуемой смены пароля ОПРЕДЕЛЕНА
			                        if (policyWrapper.getMaxValidPeriod() > 0 && expireDate != null) {
			                            Calendar c2 = new GregorianCalendar(TimeZone.getTimeZone("Asia/Dhaka"));
			                            // задать дату рекомендуемой смены пароля
			                            c2.setTime(expireDate);
			                            // сравнить текущую дату с датой рекомендуемой смены пароля, если уже был выброс этого исключения, то не реагировать на него
			                            if (c2.before(c1) && !sLogin) {
			                                sLogin = true;
			                                throw new KrnException(USER_IS_EXPIRED, obj, "Ваш пароль устарел. Вам рекомендуется сменить пароль!", TYPE_WARNING);
			                            }
			                        }
			                    }
			                    // ************************* здесь уже можно считать пользователя прошедшим проверки сервера ************************
			
			                    try {
			                    	if (failedCount > 0) {
			                    		// обнуление количества некорректных авторизаций
			                    		setLong(obj.id, faledLogin.id, 0, 0, 0);
			                    	}
			                    	if (timeLockU != null) {
			                    		// удаление времени блокировки
			                    		deleteValue(obj.id, timeLock.id, new int[0], 0, 0);
			                    	}
			                    } catch (Exception e) {
			                        outErrorCreateAttrUser("isLogged, "+ATTR_NUMBER_FAILED_LOGIN+", "+ATTR_TIME_LOCK);
			                    }
			
			                    // подтверждение транзакции, необходимо чтобы записать в базу изменённые атрибуты
			                    ServerUserSession us = new ServerUserSession(dsName, typeClient, user, ip, pcName, SERVER_ID, callbacks);
			                    us.logLoginTime(this);
			                    setUserSession(us);
			                    
			                    byte[] foto = (byte[]) arb.getValue("аватар", row);
			                    if (foto != null && foto.length > 0) {
			                    	us.setPhoto(foto);
			                    }

			                    if (deleteNotificationsAtLogin)
			                    	deleteNotification(obj);
			                    
			                    commitTransaction();
			                    // перезагрузка фильтров, требуется на конфигурации с несколькими БД, чтобы не пересохранять фильтры
			                    if (!"sys".equals(name) && Kernel.reloadFlt) {
			                        reloadFilters();
			                    }
			                    if (callbacks) {
			                        writeLogRecord(SystemEvent.EVENT_LOGIN, "",-1,-1);
			                    }
			
			                    if (typeClient != null) {
			                        try {
			                            KrnObject[] process = us.getUser().getProcess(this);
			                            if (process != null && process.length > 0) {
			                                for (KrnObject pr : process) {
			                                	if (ExecutionComponent.isProcUidAllowed(pr.uid)) {
			                                		startProcess(pr.id, null);
			                                	}
			                                }
			                            }
			                        } catch (KrnException e) {
			                            log.error(e.getMessage(), e);
			                        }
			                    }
			                    return us;
			                } else {
			                	logUnsuccessfullLoginTime(obj);
			                    // счетчик некорректных авторизаций
			                    long j = failedCount + 1;
			
			                    if (policyWrapper.getNumberFailedLogin() > 0 && j > policyWrapper.getNumberFailedLogin()) {
			                        j = policyWrapper.getNumberFailedLogin();
			                    } else {
				                    try {
				                    	if (j != failedCount)
				                    		setLong(obj.id, faledLogin.id, 0, j, 0);
				                    } catch (Exception e) {
				                        outErrorCreateAttrUser(ATTR_NUMBER_FAILED_LOGIN);
				                    }
			                    }
			                    if (policyWrapper.getNumberFailedLogin() > 0 && j >= policyWrapper.getNumberFailedLogin()) {
			                        // блокировка пользователя
			                        setLong(obj.id, lock.id, 0, 1, 0);
			                        // запись времени после которого пользовать будет разблокирован
			                        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("Asia/Dhaka"));
			                        // установка даты/времени для разблокировки пользователя
			                        cal.add(Calendar.MINUTE, (int) policyWrapper.getTimeLock());
			                        try {
			                            setTime(obj.id, timeLock.id, 0, kz.tamur.util.Funcs.convertTime(cal.getTime()), 0);
			                        } catch (Exception e) {
			                            outErrorCreateAttrUser(ATTR_TIME_LOCK);
			                        }
			                    }
			                    // подтверждение транзакции
			                    commitTransaction();
			                }
		                }
		            }
	            }
	            if (callbacks) {
	                writeLogRecord(SystemEvent.WARNING_PASSWSORD, name, ip, pcName, false, "",-1,-1);
	            }
	            KrnException ex=new KrnException(USER_NOT_FOUND, "Неверное имя пользователя или пароль", TYPE_WARNING);
	            if(ErrorsNotification.isInitialize()){
	                String message="Неверное имя пользователя:'"+name+"' или пароль. IP:'"+ip+" PC:"+pcName+"'.";
	            	ErrorsNotification.notifyErrors("TO_200","IP_address_"+ip,message,null, this);
	            }
	            throw ex;
	            
            } catch (DriverException e) {
            	releaseDriver();
            	log.error("Ошибка авторизации.", e);
                throw new KrnException("Ошибка авторизации.", -1, e);
                
            } catch (KrnException e) {
            	releaseDriver();
                throw e;
            }
            
        } else {
            if (callbacks) {
                writeLogRecord(SystemEvent.WARNING_MAX_CONNECTIONS, name, ip, pcName, false, "",-1,-1);
            }
            throw new KrnException(
                    SERVER_MAX_CLIENTS_REACHED,
                    "Достигнуто максимально возможное количество клиентских подключений. Пожалуйста, подождите пока другие пользователи закончат работу.");
        }
    }
    
    public void logUnsuccessfullLoginTime(KrnObject userObj) throws KrnException {
        KrnClass userCls = getClassByName("User");
        KrnAttribute timeAttr = getAttributeByName(userCls, "дата неудачного входа");
        if (timeAttr != null) {
            setValue(userObj.id, timeAttr.id, 0, 0, new Timestamp(System.currentTimeMillis()), 0, false);
        }
    }


    /**
     * Авторизация с проверкой политики безопасности по ЭЦП.
     *
     * @param dsName the ds name
     * @param pkcs7 the pkcs7
     * @param sct the secret
     * @param typeClient the type client
     * @param ip the ip
     * @param pcName the pc name
     * @param callbacks the callbacks
     * @return user session value
     * @throws KrnException the krn exception
     */
    public UserSession loginWithECP(String dsName, String pkcs7, String sct, String typeClient, String ip, String pcName,
            boolean callbacks) throws KrnException {

        this.dsName = dsName;
        initLogs("ecpLogin");
        boolean notReached = !callbacks || !isReachedUserSessionLimit();

        if (isServerBlocked(ip) == 2) {
            throw new KrnException(SERVER_BLOCKED,
                    "Сервер заблокирован. Попробуйте зайти в программу позже или обратитесь к администратору.");
        }

        if (notReached) {

            CheckSignResult checkRes = null;

            try {
                checkRes = KalkanUtil.verifyPkcs7(sct, pkcs7, true);
                if (!checkRes.isOK()) {
                    String msg = checkRes.getErrorMessage(true);
                	if(ErrorsNotification.isInitialize()){
                		ErrorsNotification.notifyErrors("TO_201", "IP_address_"+ip+"PC+"+pcName, "USER_NOT_FOUND: '" + msg + "'", null, this);
                	}
                    throw new KrnException(USER_NOT_FOUND, msg);
                }

            } catch (KrnException e) {
                throw e;
            } catch (Exception e) {
                log.error(e, e);
            	if(ErrorsNotification.isInitialize()){
            		ErrorsNotification.notifyErrors("TO_201", "IP_address_"+ip+"PC+"+pcName, "USER_NOT_FOUND: 'ЭЦП нарушено'", null, this);
            	}
                throw new KrnException(USER_NOT_FOUND, "ЭЦП нарушено");
            }

            if (checkRes.isDigiSignOK()) {
                String iin = checkRes.getSignerIIN();

                db = ConnectionManager.instance().getDatabase(dsName);
                if (db == null || (callbacks && !db.isReadyForConnection())) {
                    throw new KrnException(SERVER_NOT_AVAILABLE,
                            "Сервер с dataSourceNames = '" + dsName + "' не доступен. Возможно это вызванно неправильной конфигурацией. Проверьте соответствие параметра dataSourceNames в web.xml и в файле, указанном в -DinitParamsFile", TYPE_WARNING);
                }

                try {
                    initDriver(new ServerUserSession(dsName, typeClient, new UserSrv(iin), ip, pcName, SERVER_ID, false));

	                KrnClass userCls = getClassByName("User");
	                KrnAttribute iinAttr = getAttributeByName(userCls, "iin");
	                KrnAttribute faledLogin = getAttributeByName(userCls, ATTR_NUMBER_FAILED_LOGIN);
	                KrnAttribute timeLock = getAttributeByName(userCls, ATTR_TIME_LOCK);
	                KrnAttribute lock = getAttributeByName(userCls, "blocked");
	
	                KrnObject[] objs = null;
	
	                // Найти всех пользователей с данным IIN
	                objs = getObjectsByAttribute(userCls.id, iinAttr.id, 0, CO_EQUALS, iin, 0);
	
	                for (KrnObject obj : objs) {
	                    Time[] timeLockU = null;
	                    long[] lockU = getLongs(obj.id, lock.id, 0);
	                    long[] failedCount = null;
	
	                    try {
	                        timeLockU = getTimes(obj.id, timeLock.id, 0);
	                        failedCount = getLongs(obj.id, faledLogin.id, 0);
	                    } catch (Exception e) {
	                        outErrorCreateAttrUser(ATTR_NUMBER_FAILED_LOGIN+", "+ATTR_TIME_LOCK);
	                    }
	
	                    Calendar cal1 = null;
	                    Calendar cal2 = null;
	                    // если установлен флаг блокировки
	                    if (lockU[0] == 1) {
	                        // если таймаут пустой (то есть пользователя блокировал
	                        // администратор)
	                        if (timeLockU == null || timeLockU.length == 0 || timeLockU[0] == null) {
	                            throw new KrnException(USER_IS_BLOCKED, "Ошибка!\r\nУчетная запись заблокирована администратором.");
	                        } else {
	                            // текущая дата
	                            cal1 = new GregorianCalendar(TimeZone.getTimeZone("Asia/Dhaka"));
	                            // дата из атрибутов пользователя
	                            short day = timeLockU[0].day;
	                            short month = timeLockU[0].month;
	                            short year = timeLockU[0].year;
	                            short hour = timeLockU[0].hour;
	                            short min = timeLockU[0].min;
	                            cal2 = new GregorianCalendar(TimeZone.getTimeZone("Asia/Dhaka"));
	                            cal2.set(Calendar.DAY_OF_MONTH, day);
	                            cal2.set(Calendar.MONTH, month);
	                            cal2.set(Calendar.YEAR, year);
	                            cal2.set(Calendar.HOUR_OF_DAY, hour);
	                            cal2.set(Calendar.MINUTE, min);
	                            cal2.set(Calendar.SECOND, 0);
	                            cal2.set(Calendar.MILLISECOND, 0);
	                            // если текущая дата раньше даты разблокировки
	                            if (cal1.before(cal2)) {
	                                throw new KrnException(USER_IS_BLOCKED, "\t\tОшибка!\r\nУчетная запись заблокирована до " + day
	                                        + "." + (month + 1) + "." + year + " " + hour + ":" + min);
	                            }
	                        }
	                    }
	
	                    // проверка на то что пользователь уже подключён к системе
	                    UserSession exUs = findUserSession(obj.id);
	                    // если подключён
	                    if (exUs != null && !exUs.isAdmin() && !exUs.isMulti() && ip != null && !ip.equals(exUs.getIp())
	                            && dsName.equals(exUs.getDsName())) {
	                        if (callbacks) {
	                            writeLogRecord(SystemEvent.WARNING_USER_CONNECTED, exUs.getUserName(), ip, pcName, false, "",-1,-1);
	                        }
	                        String msg = "Пользователь с именем '" + exUs.getUserName() + "' уже подключен к Системе";
	                        throw new KrnException(USER_HAS_CONNECT, msg);
	                    }
	
	                    OrganisationComponent orgComp = getOrgComp();
	                    UserSrv user = orgComp.findActorById(obj.id,this);
	                    // Пользователь может быть создан не в конструкторе, тогда
	                    // необходимо добавить данные о нем в OrganisationComponent
	                    if (user == null) {
	                        orgComp.updateUser(new KrnObject[] { obj }, true, this);
	                        user = orgComp.findActorById(obj.id,this);
	                    }
	                    initLogs(user.getUserName());
	
	                    try {
	                    	if (failedCount == null || failedCount.length == 0 || failedCount[0] > 0) {
		                        // обнуление количества некорректных авторизаций
		                        setLong(obj.id, faledLogin.id, 0, 0, 0);
	                    	}
	                        // удаление времени блокировки
	                        deleteValue(obj.id, timeLock.id, new int[0], 0, 0);
	                    } catch (Exception e) {
	                        outErrorCreateAttrUser(ATTR_NUMBER_FAILED_LOGIN);
	                    }
	
	                    setLong(obj.id, lock.id, 0, 0, 0);
	
	                    if (callbacks) {
	                        writeLogRecord(SystemEvent.EVENT_LOGIN, "",-1,-1);
	                    }
	                    // подтверждение транзакции, необходимо чтобы записать в
	                    // базу изменеённые атрибуты
	                    ServerUserSession us = new ServerUserSession(dsName, typeClient, user, ip, pcName, SERVER_ID, callbacks);
	                    us.logLoginTime(this);
	                    setUserSession(us);
	                    commitTransaction();
	
	                    if (typeClient != null) {
	                        try {
	                            KrnObject[] process = us.getUser().getProcess(this);
	                            if (process != null && process.length > 0) {
	                                for (KrnObject pr : process) {
	                                	if (ExecutionComponent.isProcUidAllowed(pr.uid)) {
	                                		startProcess(pr.id, null);
	                                	}
	                                }
	                            }
	                        } catch (KrnException e) {
	                            log.error(e.getMessage(), e);
	                        }
	                    }
	                    return us;
	                }
	                if (callbacks) {
	                    writeLogRecord(SystemEvent.WARNING_PASSWSORD, iin, ip, pcName, false, "",-1,-1);
	                }
	
	                throw new KrnException(USER_NOT_FOUND, "Пользователь с ИИН = " + iin + " не найден");

                } catch (DriverException e) {
                	releaseDriver();
                	log.error("Ошибка авторизации.", e);
                    throw new KrnException("Ошибка автроизации.", 0, e);

                } catch (KrnException e) {
                	releaseDriver();
                	throw e;
                }
            }

            if (callbacks) {
                writeLogRecord(SystemEvent.WARNING_PASSWSORD, "", ip, pcName, false, "",-1,-1);
            }

            throw new KrnException(USER_NOT_FOUND, "ЭЦП нарушено");
        } else {
            if (callbacks) {
                writeLogRecord(SystemEvent.WARNING_MAX_CONNECTIONS, "", ip, pcName, false, "",-1,-1);
            }
            throw new KrnException(
                    SERVER_MAX_CLIENTS_REACHED,
                    "Достигнуто максимально возможное количество клиентских подключений. Пожалуйста, подождите пока другие пользователи закончат работу.");
        }
    }

    public UserSession loginWithCert(String dsName, String name, String typeClient, String sctSign, String ip, String pcName, boolean callbacks) throws KrnException {

		this.dsName = dsName;
		initLogs(name);

		if (isServerBlocked(ip) == 2) {
			throw new KrnException(SERVER_BLOCKED,
					"Сервер заблокирован. Попробуйте зайти в программу попозже или обратитесь к администратору.");

		}

		boolean notReached = !callbacks || !isReachedUserSessionLimit();

		if (notReached) {
			db = ConnectionManager.instance().getDatabase(dsName);
			if (db == null || (callbacks && !db.isReadyForConnection()))
                throw new KrnException(SERVER_NOT_AVAILABLE,
                        "Сервер с dataSourceNames = '" + dsName + "' не доступен. Возможно это вызванно неправильной конфигурацией. Проверьте соответствие параметра dataSourceNames в web.xml и в файле, указанном в -DinitParamsFile", TYPE_WARNING);

			try {
				initDriver(new ServerUserSession(dsName, typeClient, new UserSrv(name), ip, pcName, SERVER_ID, false));

				KrnClass userCls = getClassByName("User");
				KrnAttribute nameAttr = getAttributeByName(userCls, "name");
				KrnAttribute certAttr = getAttributeByName(userCls, "certificate");
				KrnClass msdocClass = getClassByName("MSDoc");
				KrnAttribute fileAttr = getAttributeByName(msdocClass, "file");
				KrnAttribute lock = getAttributeByName(userCls, "blocked");
	
				KrnClass langClass = getClassByName("Language");
				KrnAttribute codeAttr = getAttributeByName(langClass, "code");
	
				KrnObject ru = getObjectsByAttribute(langClass.id, codeAttr.id, 0, CO_EQUALS, "RU", 0)[0];
	
				KrnObject[] objs = getObjectsByAttribute(userCls.id, nameAttr.id, 0, CO_EQUALS, name, 0);
				for (KrnObject obj : objs) {
					// проверка блокировки пользователя
					long[] lockU = getLongs(obj.id, lock.id, 0);
					if (lockU[0] == 1) {
						throw new KrnException(USER_IS_BLOCKED,
								"Ошибка!\r\nУчетная запись заблокирована администратором.");
					}
					try {
						KrnObject certObj = getObjectsSingular(obj.id, certAttr.id, false);
						if (certObj != null) {
							byte[] cert = getBlob(certObj.id, fileAttr.id, 0, ru.id, 0);
	
							if (cert != null && cert.length > 0) {
								Signer32 signer = new Signer32();
								signer.init();
	
								CertificateFactory cf = CertificateFactory.getInstance("X.509");
								InputStream certSteam = new ByteArrayInputStream(kz.tamur.util.Funcs.normalizeInput(cert, "UTF-8"));
								X509Certificate c = (X509Certificate) cf.generateCertificate(certSteam);
								certSteam.close();
	
								byte[] cb = new TBSCertificateStructure((ASN1Sequence) ASN1Sequence.fromByteArray(c
										.getTBSCertificate())).getSubjectPublicKeyInfo().getPublicKeyData().getBytes();
	
								log.info("=== cb = " + new String(Base64.encode(cb)));
								log.info("=== ss = " + sctSign);
	
								boolean ok = signer
										.verifyString("top_secret_or3".getBytes(), cb, Base64.decode(sctSign)) == 1;
								log.info("ok = " + ok);
	
								if (ok) {
									UserSession exUs = findUserSession(name);
									if (exUs != null && !exUs.isAdmin() && !exUs.isMulti() && !ip.equals(exUs.getIp())) {
										writeLogRecord(SystemEvent.WARNING_USER_CONNECTED, name, ip, pcName, false, "",-1,-1);
										String msg = "Пользователь с именем '" + name + "' уже подключен к Системе";
										throw new KrnException(USER_HAS_CONNECT, msg);
									}
									OrganisationComponent orgComp = getOrgComp();
									UserSrv user = getOrgComp().findActorById(obj.id,this);
									// Пользователь может быть создан не в
									// конструкторе, тогда необходимо добавить
									// данные о нем в OrganisationComponent
									if (user == null) {
										orgComp.updateUser(new KrnObject[] { obj }, true, this);
										user = orgComp.findActorById(obj.id,this);
									}
	
									ServerUserSession us = new ServerUserSession(dsName, typeClient, user, ip, pcName,
											SERVER_ID, callbacks);
									us.logLoginTime(this);
									setUserSession(us);
									commitTransaction();
	
									writeLogRecord(SystemEvent.EVENT_LOGIN, "",-1,-1);
									if (typeClient != null) {
										try {
											KrnObject[] process = us.getUser().getProcess(this);
											if (process != null && process.length > 0) {
												for (int i = 0; i < process.length; ++i)
													if (ExecutionComponent.isProcUidAllowed(process[i].uid)) {
														startProcess(process[i].id, null);
													}
											}
										} catch (KrnException e) {
											log.error(e.getMessage(), e);
										}
									}
									return us;
								} else {
									writeLogRecord(SystemEvent.WARNING_WRONG_TOKEN, name,
											ip, pcName, false, "",-1,-1);
	
									throw new KrnException(USER_NOT_FOUND,
											"Ошибка авторизации! Используйте другой ключевой контейнер.");
								}
							}
						}
					} catch (KrnException e) {
						if (e.code == USER_NOT_FOUND) {
							writeLogRecord(SystemEvent.WARNING_PASSWSORD, name, ip,
									pcName, false, "",-1,-1);
	
							throw e;
						}
					} catch (Exception e) {
						writeLogRecord(SystemEvent.WARNING_CANT_READ_CERT, name, ip,
								pcName, false, "",-1,-1);
	
						throw new KrnException(USER_NOT_FOUND, "Ошибка при чтении сертификата пользователя");
					}
				}
				writeLogRecord(SystemEvent.WARNING_PASSWSORD, name, ip, pcName, false, "",-1,-1);
	
				throw new KrnException(USER_NOT_FOUND,
						"Неверное имя пользователя или у пользователя отсутствует сертификат");
            
			} catch (DriverException e) {
            	releaseDriver();
            	log.error("Ошибка авторизации.", e);
                throw new KrnException("Ошибка автроизации.", 0, e);

            } catch (KrnException e) {
            	releaseDriver();
            	throw e;
            }
		} else {
			if (callbacks)
				writeLogRecord(SystemEvent.WARNING_MAX_CONNECTIONS, name, ip, pcName,
						false, "",-1,-1);

			throw new KrnException(
					SERVER_MAX_CLIENTS_REACHED,
					"Достигнуто максимально возможное количество клиентских подключений. Пожалуйста, подождите пока другие пользователи закончат работу.");
		}	
    }

    public UserSession loginWithDN(String dsName, String dn, String typeClient, String ip, String pcName, boolean callbacks, boolean force) throws KrnException {
		this.dsName = dsName;
    	initLogs("dnLogin");

    	if (isServerBlocked(ip) == 2) {
			throw new KrnException(SERVER_BLOCKED, "Сервер заблокирован. Попробуйте зайти в программу попозже или обратитесь к администратору.");
    	}

    	boolean notReached = !callbacks || !isReachedUserSessionLimit();

    	if (notReached) {
    		db = ConnectionManager.instance().getDatabase(dsName);
			if (db == null || (callbacks && !db.isReadyForConnection())) {
                throw new KrnException(SERVER_NOT_AVAILABLE, "Сервер с dataSourceNames = '" + dsName + "' не доступен. Возможно это вызванно неправильной конфигурацией. Проверьте соответствие параметра dataSourceNames в web.xml и в файле, указанном в -DinitParamsFile", TYPE_WARNING);
			}
			try {
		        initDriver(new ServerUserSession(dsName, typeClient, new UserSrv(dn), ip, pcName, SERVER_ID, false));
		        
		        KrnClass userCls = getClassByName("User");
		    	KrnAttribute dnAttr = getAttributeByName(userCls, "iin");
                KrnAttribute lock = getAttributeByName(userCls, "blocked");
		    	KrnObject[] objs = getObjectsByAttribute(userCls.id, dnAttr.id, 0, CO_EQUALS, dn, 0);
                for (int i = 0; i < objs.length; i++) {
                	KrnObject obj = objs[i]; 
                	long[] lockU = getLongs(obj.id, lock.id, 0);
                    if (lockU[0] == 1) {
                    	if (objs.length == 1) {
                            throw new KrnException(USER_IS_BLOCKED, "Ошибка!\r\nУчетная запись заблокирована администратором.");
                    	} else if (objs.length > 1 && i == objs.length - 1) {
                            throw new KrnException(USER_IS_BLOCKED, "Ошибка!\r\nУчетные записи (" + objs.length + ") заблокированы администратором.");
                    	} else {
                    		continue;
                    	}
                    }

	    	    	OrganisationComponent orgComp = getOrgComp();
	    	    	UserSrv user = orgComp.findActorById(obj.id,this);
	
	    	    	if (!user.isAdmin() && !user.isMulti()) {
	                    // Если не админ и не мульти вход, то проверяем подключен ли уже пользователь к системе
	                    UserSession exUs = findUserSession(obj.id);
	                    if (exUs != null && ip != null && dsName.equals(exUs.getDsName())) {
	                    	if (force) {
	                    		killUserSessions(exUs.getId(), false);
	                        } else {
	                            if (callbacks) {
	                                writeLogRecord(SystemEvent.WARNING_USER_CONNECTED, user.getUserName(), ip, pcName, false, "",-1,-1);
	                            }
	                            throw new KrnException(/*ip.equals(exUs.getIp()) ? */USER_HAS_CONNECT_SAME_IP /*: USER_HAS_CONNECT*/, obj, 
	                            		"Пользователь с именем '" + user.getUserName() + "' уже подключен к Системе", TYPE_WARNING);
	                        }
	                    }
                    }
	    	    	
	    	    	/*
	    	    	UserSession exUs = (user != null) ? findUserSession(user.getUserName()) : null;
	    	    	if (exUs != null && !exUs.isAdmin() && !exUs.isMulti() && !ip.equals(exUs.getIp())) {
	    	    		writeLogRecord(SystemEvent.WARNING_USER_CONNECTED, user.getUserName(), ip, pcName, false, "",-1,-1);
	                    String msg = "Пользователь с именем '" + user.getUserName() + "' уже подключен к Системе";
		                throw new KrnException(USER_HAS_CONNECT, msg);
	    	    	}
	    	    	*/
	    	    	
	    	    	// Пользователь может быть создан не в конструкторе, тогда необходимо добавить данные о нем в OrganisationComponent
	    	    	if (user == null) {
	    	    		orgComp.updateUser(new KrnObject[] {obj}, true, this);
	    	    		user = orgComp.findActorById(obj.id,this);
	    	    	}
	    	    	initLogs(user.getUserName());
	    	    	
	    			ServerUserSession us = new ServerUserSession(dsName,typeClient, user, ip, pcName, SERVER_ID, callbacks);
	    			us.logLoginTime(this);
	    			setUserSession(us);
	    			commitTransaction();
	
					writeLogRecord(SystemEvent.EVENT_LOGIN, "", -1, -1);
					if (typeClient != null) {
						try {
							KrnObject[] process = us.getUser().getProcess(this);
							if (process != null && process.length > 0) {
								for (int j = 0; j < process.length; ++j) {
									if (ExecutionComponent.isProcUidAllowed(process[j].uid)) {
										startProcess(process[j].id, null);
									}
								}
							}
						} catch (KrnException e) {
							log.error(e.getMessage(), e);
						}
					}
					return us;
		    	}
		    	writeLogRecord(SystemEvent.WARNING_PASSWSORD, dn, ip, pcName, false, "",-1,-1);
		    	throw new KrnException(USER_NOT_FOUND, "Пользователь с ИИН = " + dn + " не найден!");
            } catch (DriverException e) {
            	releaseDriver();
            	log.error("Ошибка авторизации.", e);
                throw new KrnException("Ошибка автроизации.", 0, e);
            } catch (KrnException e) {
            	releaseDriver();
            	throw e;
            }
    	} else {
			if (callbacks) {
		    	writeLogRecord(SystemEvent.WARNING_MAX_CONNECTIONS, dn, ip, pcName, false, "",-1, -1);
			}
			throw new KrnException(SERVER_MAX_CLIENTS_REACHED, "Достигнуто максимально возможное количество клиентских подключений. Пожалуйста, подождите пока другие пользователи закончат работу.");
    	}
    }

    public UserSession loginWithLDAP(String dsName, String dn, String typeClient, String ip, String pcName, boolean callbacks) throws KrnException {
		this.dsName = dsName;
    	initLogs("ldapLogin");

    	if (isServerBlocked(ip) == 2) {

			throw new KrnException(SERVER_BLOCKED,
			"Сервер заблокирован. Попробуйте зайти в программу попозже или обратитесь к администратору.");

    	}

    	boolean notReached = !callbacks || !isReachedUserSessionLimit();

    	if (notReached) {
    		db = ConnectionManager.instance().getDatabase(dsName);
			if (db == null || (callbacks && !db.isReadyForConnection()))
                throw new KrnException(SERVER_NOT_AVAILABLE,
                        "Сервер с dataSourceNames = '" + dsName + "' не доступен. Возможно это вызванно неправильной конфигурацией. Проверьте соответствие параметра dataSourceNames в web.xml и в файле, указанном в -DinitParamsFile", TYPE_WARNING);
	
			try {
		        initDriver(new ServerUserSession(dsName, typeClient, new UserSrv(dn), ip, pcName, SERVER_ID, false));

		        KrnClass userCls = getClassByName("User");
		    	KrnAttribute dnAttr = getAttributeByName(userCls, "ldapName");
		    	KrnObject[] objs = getObjectsByAttribute(userCls.id, dnAttr.id, 0, CO_EQUALS, dn, 0);
		    	if (objs.length > 0) {
		    		KrnObject obj = objs[0];
	    	    	OrganisationComponent orgComp = getOrgComp();
	    	    	UserSrv user = orgComp.findActorById(obj.id,this);
	
	    	    	UserSession exUs = (user != null) ? findUserSession(user.getUserName()) : null;
	    	    	if (exUs != null && !exUs.isAdmin() && !exUs.isMulti() && !ip.equals(exUs.getIp())) {
	    	    		writeLogRecord(
	    	    				SystemEvent.WARNING_USER_CONNECTED,
	    	    				user.getUserName(), ip, pcName, false, "",-1,-1);
	                    String msg = "Пользователь с именем '"
	                        + user.getUserName() + "' уже подключен к Системе";
		                throw new KrnException(USER_HAS_CONNECT, msg);
	    	    	}
	    	    	// Пользователь может быть создан не в конструкторе, тогда необходимо добавить 
	    	    	// данные о нем в OrganisationComponent
	    	    	if (user == null) {
	    	    		orgComp.updateUser(new KrnObject[] {obj}, true, this);
	    	    		user = orgComp.findActorById(obj.id,this);
	    	    	}
	    	    	initLogs(user.getUserName());
	
	    			ServerUserSession us = new ServerUserSession(dsName,typeClient, user, ip, pcName, SERVER_ID, callbacks);
	    			us.logLoginTime(this);
	    			setUserSession(us);
	    			commitTransaction();
	
	    			writeLogRecord(SystemEvent.EVENT_LOGIN, "",-1,-1);
	                if(typeClient!=null){
	                    try{
	                        KrnObject[] process=us.getUser().getProcess(this);
	                        if(process!=null && process.length>0){
	                            for(int i=0;i<process.length;++i)
	                                startProcess(process[i].id, null);
	                        }
	                    }catch(KrnException e){
	                        log.error(e.getMessage(), e);
	                    }
	                }
	    			return us;
		    	}
		    	writeLogRecord(
						SystemEvent.WARNING_PASSWSORD,
						dn, ip, pcName, false, "",-1,-1);
		
		    	throw new KrnException(USER_NOT_FOUND,
		    			"Пользователь с LDAP именем = " + dn + " не найден!");
		    	
            } catch (DriverException e) {
            	releaseDriver();
            	log.error("Ошибка авторизации.", e);
                throw new KrnException("Ошибка автроизации.", 0, e);

            } catch (KrnException e) {
            	releaseDriver();
            	throw e;
            }
    	} else {
			if (callbacks)
		    	writeLogRecord(
						SystemEvent.WARNING_MAX_CONNECTIONS,
						dn, ip, pcName, false, "",-1,-1);

			throw new KrnException(SERVER_MAX_CLIENTS_REACHED,
			"Достигнуто максимально возможное количество клиентских подключений. Пожалуйста, подождите пока другие пользователи закончат работу.");
    	}
    }

    private Stack<Context> contexts_ = new Stack<Context>();


    // @todo Перенести в Driver private static Long databaseVersion;
    
    public void runReplication()
            throws com.cifs.or2.kernel.KrnException {
        Replication r = new Replication(this);
        r.run(this);
    }

    public boolean replIsRunning()
            throws com.cifs.or2.kernel.KrnException {
        throw new KrnException(0, "Not implemented");
    }

    public int getChanges(int action, String info, String scriptOnBeforeAction, String scriptOnAfterAction) throws com.cifs.or2.kernel.KrnException {
        Replication r = new Replication(this);
        return r.getChanges(this, action, info, scriptOnBeforeAction, scriptOnAfterAction);
    }

	public String setChanges() throws com.cifs.or2.kernel.KrnException {
		String res = "";
		List<Long> disabledRules = new ArrayList<Long>();
		db.disableFGACRules(getDriver(), getUserSession(), disabledRules);
		try {
			log.info("Начало репликации. Пользователь:" + getUserSession().getUserName() + " IP:" + user.getComputer());
			Replication r = new Replication(this);
			res = r.setChanges(this);
			log.info("Завершение репликации!");
		} catch (KrnException e) {
			log.error(e, e);
			throw e;
		} finally {
			db.enableFGACRules(getDriver(), getUserSession(), disabledRules);
		}
		return res;
	}

    public int getNextValue(long seqId, long tr_id) throws KrnException {
        throw new KrnException(0, "Not implemented");
    }

    public int getLastValue(long seqId) throws KrnException {
        throw new KrnException(0, "Not implemented");
    }

    public void useValue(long seqId, long value, String strVal, long tr_id) throws KrnException {
        throw new KrnException(0, "Not implemented");
    }

    public void unuseValue(long seqId, String oldStrValue, long newValue,
                           String newStrValue, long tr_id) throws KrnException {
        throw new KrnException(0, "Not implemented");
    }

    public void skipValue(long seqId, long value, String strVal, long tr_id) throws KrnException {
        throw new KrnException(0, "Not implemented");
    }

    public long[] getSkippedValues(long seqId) throws KrnException {
        throw new KrnException(0, "Not implemented");
    }

    public void rollbackSeqValues(long seqId, long tr_id) throws KrnException {
        throw new KrnException(0, "Not implemented");
    }

    public Pair getObjectAttr(long[] objIds, String path, long langId,
                              long trId, Map cash) throws KrnException {
        Pair res = null;

        if (cash.containsKey(path))
            res = (Pair) cash.get(path);
        else {
            MultiMap dict = new MultiMap();
            StringTokenizer t = new StringTokenizer(path, ".");

            String currPath = "";
            KrnClass cls = null;
            if (t.hasMoreTokens()) {
                currPath = t.nextToken();
                cls = getClassByName(currPath);
            }

            while (t.hasMoreTokens()) {
                String token = t.nextToken();
                currPath += "." + token;

                if (cash.containsKey(currPath)) {
                    res = (Pair) cash.get(currPath);
                    if (t.hasMoreTokens()) {
                        MultiMap objValues = (MultiMap) res.second;
                        cls = (KrnClass) res.first;
                        objIds = new long[objValues.keySet().size()];
                        Iterator objIt = objValues.keySet().iterator();
                        int i = 0;
                        while (objIt.hasNext()) {
                            Integer objId = (Integer) objIt.next();
                            ArrayList objList = (ArrayList) objValues.get(objId);
                            ObjectValue ov = (ObjectValue) objList.get(objList.size() - 1);
                            objIds[i++] = ov.value.id;
                            dict.put(new Long(ov.value.id), objId);
                        }
                    }
                } else {
                    KrnAttribute attr = getAttributeByName(cls, token);
                    cls = getClassById(attr.typeClassId);
                    long aid = attr.id;

                    MultiMap objVals = new MultiMap();
                    res = new Pair(cls, objVals);

                    if (cls.name.equals("string") || cls.name.equals("String")
                            || cls.name.equals("memo") || cls.name.equals("Memo")) {
                        long lid = (cls.name.equals("String") || cls.name.equals("Memo"))
                                   ? langId : 0;
                        boolean isMemo = cls.name.equals("memo") || cls.name.equals("Memo");
                        StringValue[] svs = getStringValues(objIds, aid, lid, isMemo, trId);
                        for (int i = 0; i < svs.length; ++i) {
                            Long oid = new Long(svs[i].objectId);
                            ArrayList goids = (ArrayList) dict.get(oid);
                            if (goids == null) {
                                goids = new ArrayList();
                                goids.add(oid);
                            }
                            for (int j = 0; j < goids.size(); ++j) {
                                Integer goid = (Integer) goids.get(j);
                                ArrayList vals = (ArrayList) objVals.get(goid);
                                if (vals == null) {
                                    vals = new ArrayList();
                                    objVals.put(goid, vals);
                                }
                                advSet(vals, svs[i].index, svs[i]);
                            }
                        }
                    } else if (cls.name.equals("integer") || cls.name.equals("boolean")
                            || cls.name.equals("date")) {
                        LongValue[] lvs = getLongValues(objIds, aid, trId);
                        for (int i = 0; i < lvs.length; ++i) {
                            Long oid = new Long(lvs[i].objectId);
                            ArrayList goids = (ArrayList) dict.get(oid);
                            if (goids == null) {
                                goids = new ArrayList();
                                goids.add(oid);
                            }
                            for (int j = 0; j < goids.size(); ++j) {
                                Integer goid = (Integer) goids.get(j);
                                ArrayList vals = (ArrayList) objVals.get(goid);
                                if (vals == null) {
                                    vals = new ArrayList();
                                    objVals.put(goid, vals);
                                }
                                advSet(vals, lvs[i].index, lvs[i]);
                            }
                        }
                    } else if (cls.name.equals("float")) {
                        FloatValue[] fvs = getFloatValues(objIds, aid, trId);
                        for (int i = 0; i < fvs.length; ++i) {
                            Long oid = new Long(fvs[i].objectId);
                            ArrayList goids = (ArrayList) dict.get(oid);
                            if (goids == null) {
                                goids = new ArrayList();
                                goids.add(oid);
                            }
                            for (int j = 0; j < goids.size(); ++j) {
                                Integer goid = (Integer) goids.get(j);
                                ArrayList vals = (ArrayList) objVals.get(goid);
                                if (vals == null) {
                                    vals = new ArrayList();
                                    objVals.put(goid, vals);
                                }
                                advSet(vals, fvs[i].index, fvs[i]);
                            }
                        }
                    } else {
                        ObjectValue[] ovs = getObjectValues(objIds, aid, new long[0], trId);
                        for (int i = 0; i < ovs.length; ++i) {
                            Long oid = new Long(ovs[i].objectId);
                            ArrayList goids = (ArrayList) dict.get(oid);
                            if (goids == null) {
                                goids = new ArrayList();
                                goids.add(oid);
                            }
                            for (int j = 0; j < goids.size(); ++j) {
                                Integer goid = (Integer) goids.get(j);
                                ArrayList vals = (ArrayList) objVals.get(goid);
                                if (vals == null) {
                                    vals = new ArrayList();
                                    objVals.put(goid, vals);
                                }
                                advSet(vals, ovs[i].index, ovs[i]);
                            }
                        }
                        if (t.hasMoreTokens()) {
                            dict.clear();
                            objIds = new long[objVals.keySet().size()];
                            int i = 0;
                            for (Object o : objVals.keySet()) {
                                Integer goid = (Integer) o;
                                ArrayList objList = (ArrayList) objVals.get(goid);
                                ObjectValue ov = (ObjectValue) objList.get(objList.size() - 1);
                                dict.put(new Long(ov.value.id), goid);
                                objIds[i++] = ov.value.id;
                            }
                        }
                    }
                    cash.put(currPath, res);
                }
            }
        }

        return res;
    }

    private void advSet(ArrayList arr, int index, Object value) {
        for (int i = arr.size(); i <= index; ++i)
            arr.add(null);
        arr.set(index, value);
    }

    public void setContext(Context ctx) {
        contexts_.push(ctx);
    }

    public void restoreContext() {
        contexts_.pop();
    }

    public Context getContext() {
        return contexts_.peek();
    }

    public boolean  isContextEmpty() {
        return contexts_.isEmpty();
    }
    public KrnAttribute[] getAttributesForPath(String path)
            throws KrnException {
        try {
            return Toolkit.getAttributesForPath(path, drv);
        } catch (DriverException e) {
            log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public String getUId(int AId) throws KrnException {
        throw new KrnException(0, "Obsolette");
    }

    public KrnObject[] getReplRecords(int log_type, long replicationID)
            throws KrnException {
        throw new KrnException(0, "Not implemented");
    }

    public String getUniquePresentation(KrnObject obj, int c_lang_id)
            throws KrnException {
        throw new KrnException(0, "Not implemented");
    }

    //BEGIN Поддержка параметрических фильтров
    public Map<String, List<Object>> getFilterParams(String fuid) {
    	return user.getFilterParams(fuid);
    }

    public List<Object> getFilterParams(String fuid, String pid) {
    	Map<String, List<Object>> map = getFilterParams(fuid);
    	return (map != null && map.get(pid)!=null) ? map.get(pid) : new ArrayList<Object>();
    }

    public Object[] getFilterParam(String fuid, String pid) throws KrnException {
        List<Object> vs = getFilterParams(fuid, pid);
        return vs.toArray(new Object[vs.size()]);
    }

    public boolean clearFilterParams(String fuid) {
    	return user.clearFilterParams(fuid);
    }

    public boolean setFilterParam(String fuid, String pid, Object param)
            throws KrnException {
        if (param == null || param.toString().equals(""))
            return user.setFilterParam(fuid, pid, (List)null);
        else if (param instanceof List)
        	return user.setFilterParam(fuid, pid, (List)param);
        else {
            List<Object> vals = new ArrayList<Object>();
            vals.add(param);
            return user.setFilterParam(fuid, pid, vals);
        }
    }

    public boolean setFilterParam(String fuid, String pid, Object[] param)
            throws KrnException {
    	
        if (param.length > 0) {
            List<Object> pars = new ArrayList<Object>(param.length);
            for (int i = 0; i < param.length; i++) {
                pars.add(param[i]);
            }
            return user.setFilterParam(fuid, pid, pars);
        } else {
        	return user.setFilterParam(fuid, pid, (List)null);
        }
    }
    
    public long[] filter(String[] fuids, FilterDate[] dates, int[] limit) throws KrnException {
        return filter(fuids, dates, limit,null,null, 0);
    }

    public long[] filter(String[] fuids, FilterDate[] dates, int[] limit,int[] beginRows,int[] endRows, int trId) throws KrnException {
        try {
            List fobjs = drv.getObjectsByUids(fuids, trId, false);
            long[] fids = new long[fobjs.size()];
            for (int i = 0; i < fobjs.size(); i++)
                fids[i] = ((KrnObject)fobjs.get(i)).id;
            return getFilteredObjectIds(fids, dates, limit,beginRows,endRows, trId);
        } catch (DriverException e) {
            log.error(e.getMessage(), e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public KrnObject[] cloneObject2(KrnObject[] sources, long get_tr_id, long set_tr_id) throws KrnException {
        try {
        	KrnObject[] res = new KrnObject[sources.length];
            for (int i = 0; i < sources.length; i++) {
                KrnObject source = sources[i];
                res[i] = drv.cloneObject(source, get_tr_id, set_tr_id);                
                try {
                	indexObject(res[i], getClassById(res[i].classId), false);
                } catch (Exception e) {
                	log.error("Failed to index Object: " + res[i].id);
                }                
            }            
            return res;
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

	public long[] getRevAttributes(long attrId) throws KrnException {
		KrnAttribute[] attrs = db.getRevAttributes(attrId);
		long[] res = new long[attrs.length];
		for (int i = 0; i < attrs.length; i++) {
			res[i] = attrs[i].id;
		}
		return res;
	}

	public List<KrnAttribute> getRevAttributes2(long attrId) throws KrnException {
		List<KrnAttribute> res = new ArrayList<>();
		List<KrnAttribute> attrs = db.getAttributesByTypeId(getAttributeById(attrId).classId, false);
		for (int i = 0; i < attrs.size(); i++) {
			if (attrs.get(i).rAttrId == attrId) {
				res.add(attrs.get(i));
			}
		}
		return res;
	}

    public long[] getLinkAttributes(long attrId) throws KrnException {
        try {
            KrnAttribute[] attrs = drv.getLinkAttributes(attrId);
            long[] res = new long[attrs.length];
            for (int i = 0; i < attrs.length; i++) {
                res[i] = attrs[i].id;
            }
            return res;
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public String getXml(KrnObject obj) throws KrnException {
        try {
            return Utils.toXml(obj, this);
        } catch (KrnException e) {
            log.error(e, e);
            throw e;
        } catch (Exception e) {
            log.error(e, e);
            throw new KrnException(0, e.getMessage());
        }
    }

    public OrLang getOrLang() {
        return getSrvOrLang();
    }

    public SrvOrLang getSrvOrLang() {
        if (orLang == null) {
            orLang = new SrvOrLang(this);
        }
        return orLang;
    }
    
    public Date[] getDates(long objId, long attrId, long tid) throws KrnException {
        try {
            long[] objIds = {objId};
            Object[] vs = makeArray(drv.getValues(objIds, null, attrId, 0, tid));
            Date[] res = new Date[vs.length];
            for (int i = 0; i < vs.length; i++) {
                java.sql.Date v = (java.sql.Date)vs[i];
                res[i] = kz.tamur.util.Funcs.convertDate(v);
            }
            return res;
        } catch (DriverException e) {
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public DateValue[] getDateValues(long[] objIds, long attrId, long tid)
            throws KrnException {
        try {
            SortedSet vs = drv.getValues(objIds, null, attrId, 0, tid);
            DateValue[] res = new DateValue[vs.size()];
            int i = 0;
            for (Iterator it = vs.iterator(); it.hasNext();) {
                Value v = (Value)it.next();
                Date date = kz.tamur.util.Funcs.convertDate((java.sql.Date)v.value);
                res[i++] = new DateValue(v.objectId, v.index, date);
            }
            return res;
        } catch (DriverException e) {
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }
    
    public DateValue[] getDateValues2(long[] objIds, long attrId, long tid) throws KrnException {
        try {
            SortedSet vs = drv.getValues(objIds, null, attrId, 0, tid);
            DateValue[] res = new DateValue[vs.size()];
            int i = 0;
            for (Iterator it = vs.iterator(); it.hasNext();) {
                Value v = (Value) it.next();
                Date date = kz.tamur.util.Funcs.convertDate2((java.sql.Date) v.value);
                res[i++] = new DateValue(v.objectId, v.index, date);
            }
            return res;
        } catch (DriverException e) {
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public void setDate(long objectId, long attrId, int index, Date value, long tid) throws KrnException {
        try {
        	executeAttrTriggerEventExpression(attrId, value, 0, objectId, 0, tid, false, null);
            drv.setValue(objectId, attrId, index, 0, tid, kz.tamur.util.Funcs.convertToSqlDate(value), false);
        	executeAttrTriggerEventExpression(attrId, value, 1, objectId, 0, tid, false, null);
        } catch (DriverException e) {
            log.error(e.getMessage(), e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    private java.sql.Date convertDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, date.day);
        cal.set(Calendar.MONTH, date.month);
        cal.set(Calendar.YEAR, date.year);
        return new java.sql.Date(cal.getTimeInMillis());
    }

    public Time[] getTimes(long objId, long attrId, long tid) throws KrnException {
        try {
            long[] objIds = {objId};
            Object[] vs = makeArray(drv.getValues(objIds, null, attrId, 0, tid));
            Time[] res = new Time[vs.length];
            for (int i = 0; i < vs.length; i++) {
                Timestamp v = (Timestamp)vs[i];
                res[i] = kz.tamur.util.Funcs.convertTime(v);
            }
            return res;
        } catch (DriverException e) {
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public TimeValue[] getTimeValues(long[] objIds, long attrId, long tid)
            throws KrnException {
        try {
            SortedSet vs = drv.getValues(objIds, null, attrId, 0, tid);
            TimeValue[] res = new TimeValue[vs.size()];
            int i = 0;
            for (Iterator it = vs.iterator(); it.hasNext();) {
                Value v = (Value)it.next();
                Time time = kz.tamur.util.Funcs.convertTime((java.sql.Timestamp)v.value);
                res[i++] = new TimeValue(v.objectId, v.index, time);
            }
            return res;
        } catch (DriverException e) {
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public void setTime(long objectId, long attrId, int index, Time value, long tid) throws KrnException {
        try {
        	executeAttrTriggerEventExpression(attrId, value, 0, objectId, 0, tid, false, null);
            drv.setValue(objectId, attrId, index, 0, tid, convertTime(value), false);
        	executeAttrTriggerEventExpression(attrId, value, 1, objectId, 0, tid, false, null);
        } catch (DriverException e) {
            log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }

    public KrnObject[] getChildDbs(boolean recursive, boolean onlyPhisycal) throws KrnException {
        throw new KrnException(0, "Not implemented");
    }

    public Map<Integer, com.cifs.or2.kernel.Date> getFilterDates(){
          return filterDates_;
    }

    private Timestamp convertTime(Time time) {
    	if (time == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, time.msec);
        cal.set(Calendar.SECOND, time.sec);
        cal.set(Calendar.MINUTE, time.min);
        cal.set(Calendar.HOUR_OF_DAY, time.hour);
        cal.set(Calendar.DAY_OF_MONTH, time.day);
        cal.set(Calendar.MONTH, time.month);
        cal.set(Calendar.YEAR, time.year);
        return new Timestamp(cal.getTimeInMillis());
    }
    
    public GlobalFuncs getGlobalFuncs() {
        return globalFuncs;
    }
    
    public String getGlobalFunc(String funcName) {
    	return globalFuncs.getText(funcName, this);
    }

    public KrnObject getCurrentDb() throws KrnException {
        return db.getObject();
    }

	public Cursor createCursor(long classId, long[] filterIds, long trId)
			throws KrnException {
		throw new KrnException(0, "NOT IMPLEMENTED");
	}

    public void rollbackLocked() {
    	if (user != null) {
	    	try {
	    		user.cachedClear();
				drv.rollbackLocks(user.getId().toString());
			} catch (DriverException e) {
	            log.error(e.getMessage(), e);
			}
    	}
    }
    public void pauseProcess(){
    	ExecutionComponent exeComp = getExeComp();
        exeComp.pause();
    }
    public void resumeProcess(){
    	ExecutionComponent exeComp = getExeComp();
        exeComp.resume();
    }

    public void commitLocked() {
    	try {
    		for (Pair<Long, Long> cu : user.getCachedUnlocks()) {
    			drv.unlockObject(cu.first, cu.second);
    		}
    		user.cachedClear();
    		UserSession myUs = getUserSession();
    		if (myUs != null)
    			drv.commitLocks(myUs.getId().toString());
		} catch (DriverException e) {
            log.error(e.getMessage(), e);
		}
    }
    
    public KrnMethod rollbackMethod(String methodId) throws KrnException {
		try {
			KrnMethod m = getMethodById(methodId);
			byte[] expr = drv.getMethodExpression(m.uid);
			Indexer.updateMethodsIndex(m, expr, null);
            writeLogRecord(SystemEvent.EVENT_METHOD_ROLLBACKED, "Название: " + m.name + ", метод класса: " + m.isClassMethod, m.classId,-1);
            drv.fireMethodChanged(m, m);
            return m;
		} catch (DriverException e) {
			log.error(e, e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
        	log.error(e, e);
            throw new KrnException(0, e.getMessage());
        }
	}

    public KrnMethod changeMethod(String methodId, String name, boolean isClassMethod, byte[] expr) throws KrnException {
		try {
			KrnMethod m = getMethodById(methodId);
			String oldName = m.name;
			Indexer.updateMethodsIndex(m, expr, null);
			KrnMethod newm = drv.changeMethod(methodId, name, isClassMethod, expr);
            writeLogRecord(SystemEvent.EVENT_METHOD_CHANGED, "Старое название: " + oldName + ", новое: " + name + ", метод класса: " + isClassMethod,m.classId,-1);
            drv.fireMethodChanged(m, newm);
            return newm;
		} catch (DriverException e) {
			log.error(e, e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
        	log.error(e, e);
            throw new KrnException(0, e.getMessage());
        }
	}

	public KrnMethod createMethod(KrnClass cls, String name, boolean isClassMethod, byte[] expr) throws KrnException {
		try {
			KrnMethod method=drv.createMethod(cls, name, isClassMethod, expr);
			Indexer.updateMethodsIndex(method, expr, null);
            writeLogRecord(SystemEvent.EVENT_METHOD_CREATED, "Метод " + name,cls.id,-1);
            drv.fireMethodCreated(method);
			return method;			
		} catch (DriverException e) {
			log.error(e, e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
        	log.error(e, e);
            throw new KrnException(0, e.getMessage());
        }
	}

	public KrnMethod createMethod(String id, KrnClass cls, String name, boolean isClassMethod, byte[] expr) throws KrnException {
		try {
			KrnMethod method=drv.createMethod(id, cls, name, isClassMethod, expr);
			Indexer.updateMethodsIndex(method, expr, null);
            writeLogRecord(SystemEvent.EVENT_METHOD_CREATED, "Метод " + name,cls.id,-1);
            drv.fireMethodCreated(method);
			return method;			
		} catch (DriverException e) {
			log.error(e, e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
        	log.error(e, e);
            throw new KrnException(0, e.getMessage());
        }
	}

	public void deleteMethod(String uid) throws KrnException {
		try {
			KrnMethod method = getMethodById(uid);
			Indexer.removeMethodsIndex(method);
			drv.deleteMethod(uid);
//			dataModelCache.put(new net.sf.ehcache.Element("" + method, null));	// Загрузка в кэш
            writeLogRecord(SystemEvent.EVENT_METHOD_DELETED, "Метод " + method.name,method.classId,-1);
            drv.fireMethodDeleted(method);
			//removeMethodFromCache(method);
		} catch (DriverException e) {
			log.error(e, e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		} catch (Exception e) {
        	log.error(e, e);
            throw new KrnException(0, e.getMessage());
        }
	}

    public List<KrnClass> getAllClasses() throws KrnException{
    	List<KrnClass> clss = new ArrayList<KrnClass>();
        db.getSubClasses(99, true, clss);
        return clss;
    }
    
	public byte[] getMethodExpression(String methodId) throws KrnException {
		try {
			return drv.getMethodExpression(methodId);
		} catch (DriverException e) {
			log.error(e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		}
	}
	
	public byte[] getVcsChangeExpression(KrnVcsChange change) throws KrnException {
		try {
			return drv.getVcsChangeExpression(change);
		} catch (DriverException e) {
			log.error(e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		}
	}
	public void removeMethodExpression(String methodId) throws KrnException {
		Driver2.removeMethodExpression(dsName, methodId);
	}

	// Метод, возвращающий список всех аттрибутов
	public List<KrnMethod> getAllMethods() throws KrnException{
        return db.getAllMethods();
    }
	
	// Метод, возвращающий HashMap всех аттрибутов
	public Map<String, KrnMethod> getMethodsMap() throws KrnException{
        return db.getMethodsMap();
    }

	public KrnMethod[] getMethods(long classId) throws KrnException {
		List<KrnMethod> res = db.getMethodsByClassId(classId);
    	return res.toArray(new KrnMethod[res.size()]);
	}

    public KrnMethod[] getMethodsByName(String name,long op)throws KrnException {
    	List<KrnMethod> res = db.getMethodsByName(name,(int)op);
    	return res.toArray(new KrnMethod[res.size()]);
    }
    
    public KrnMethod[] getMethodsByUid(String name,long op)throws KrnException {
    	List<KrnMethod> res = db.getMethodsByUid(name,(int)op);
    	return res.toArray(new KrnMethod[res.size()]);
    }
    
	public KrnMethod getMethodByName(long classId, String name) throws KrnException {
		return db.getMethodByName(classId, name);
	}

    public KrnMethod getMethodById(String id) throws KrnException {
    	return db.getMethodByUid(id);
    }

	public ASTStart getMethodExpression(long classId, String name) throws Throwable {
		KrnMethod m = getMethodByName(classId, name);
		if (m == null) {
			KrnClass cls = getClassById(classId);
			throw new KrnException(0,
                    "Метод '" + cls.name + "." + name + "' не найден");
		}
		return getMethodExpression(m);
	}

    public String getIpAddress() {
        return user.getIp();
    }
    
	public String getComputer() {
		return user.getComputer();
	}

	public ASTStart getMethodExpression(KrnMethod m) throws Throwable {
        try {
            return drv.getMethodExpression2(m.uid);
        } catch (DriverException e) {
            log.error(e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
		} catch (Throwable e) {
			KrnClass cls = db.getClassById(m.classId);
            log.error("Ошибка в коде метода '" + cls.name + "." + m.name + "'");
			throw e;
        }
    }
	
	public byte[] getLogoPic() {
		KrnClass clsConfigGlb = getClassByName("ConfigGlobal");
		AttrRequestBuilder arb = new AttrRequestBuilder(clsConfigGlb, this).add("logotypePic");
		byte[] img = null;
		try {
		KrnObject[] objs = getClassOwnObjects(clsConfigGlb, 0);
		if(objs != null && objs.length > 0) {
			KrnObject object = objs[0];
			long[] objIds = new long[] { object.id };
			Object[] row = getObjects(objIds, arb.build(), 0).rows.get(0);
			img = (byte[]) arb.getValue("logotypePic", row);
		}	
		}catch(KrnException e) {
			log.error(e, e);
		}
		return img;
	}
	
	public long getLogoPicWidth() {
		KrnClass clsConfigGlb = getClassByName("ConfigGlobal");
		AttrRequestBuilder arb = new AttrRequestBuilder(clsConfigGlb, this).add("logoPicWidth");
		long width = 0;
		try {
		KrnObject[] objs = getClassOwnObjects(clsConfigGlb, 0);
		if(objs != null && objs.length > 0) {
			KrnObject object = objs[0];
			long[] objIds = new long[] { object.id };
			Object[] row = getObjects(objIds, arb.build(), 0).rows.get(0);
			width = arb.getLongValue("logoPicWidth", row);
		}	
		}catch(KrnException e) {
			log.error(e, e);
		}
		return width;
	}
	
	public long getLogoPicHeight() {
		KrnClass clsConfigGlb = getClassByName("ConfigGlobal");
		AttrRequestBuilder arb = new AttrRequestBuilder(clsConfigGlb, this).add("logoPicHeight");
		long height = 0;
		try {
		KrnObject[] objs = getClassOwnObjects(clsConfigGlb, 0);
		if(objs != null && objs.length > 0) {
			KrnObject object = objs[0];
			long[] objIds = new long[] { object.id };
			Object[] row = getObjects(objIds, arb.build(), 0).rows.get(0);
			height = arb.getLongValue("logoPicHeight", row);
		}	
		}catch(KrnException e) {
			log.error(e, e);
		}
		return height;
	}
	
	public Map<String, String> getStringUidMap(String[] scopeUids){
		return drv.getStringUidMap(scopeUids);
	}
	
	public void addIndex(String search_str, KrnObject obj, KrnObject balans_ed) {
		drv.addIndex(search_str, obj, balans_ed);
	}
	
	public void removeIndex(KrnObject obj) {
		drv.removeIndex(obj);
	}

    public Driver2 getDriver() {
        return drv;
    }

    public void dbExport(String dir, String separator) {
        try {
            drv.dbExport(dir, separator);
        } catch (DriverException e) {
            log.error(e.getMessage(), e);
        }
    }
    
    public boolean isDel(KrnObject obj, long trId){
    	boolean res = true;
    	try {
    		res = drv.isDel(obj,trId);
    	} catch (SQLException e) {
    		log.error(e.getMessage(), e);
    	}
    	return res;
    }
    
    public boolean isDeleted(KrnObject obj) {
        boolean res = true;
        try {
            res = drv.isDeleted(obj);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return res;
    }

	public Map<String, Object> executeInThread(String cmd, Map<String, Object> vars, long threadId) throws KrnException {
		try {
			SrvOrLang lng = getSrvOrLang();
	        Context ctx = new Context(new long[0], 0, getUserSession().getDataLanguage().id); 
	        setContext(ctx);
			lng.evaluate2(cmd, vars, null, true, new Stack<String>(), threadId);
			if (lng.evalsMap.remove(threadId) == null) {
				rollbackTransaction();
			} else {
				commitTransaction();
			}
			return vars;
		} catch (KrnException e) {
			log.error(e, e);
			throw e;
		} catch (Throwable e) {
			log.error(e, e);
			rollbackTransaction();
			throw new KrnException(0, e.getMessage());
		}
	}
	
	public Map<String, Object> execute(String cmd, long trId, Map<String, Object> vars) throws KrnException {
		SrvOrLang lng = getSrvOrLang();
		try {
	        Context ctx = new Context(new long[0], trId, getUserSession().getDataLanguage().id); 
	        setContext(ctx);
			lng.evaluate(cmd, vars, null, true, new Stack<String>());
			return vars;
		} catch (Exception e) {
            log.error(e.getMessage(), e);
			rollbackTransaction();
			throw new KrnException(0, e.getMessage());
		}
	}

    public boolean lockRecord(KrnClass cls, long objId, int timeout) {
        boolean res = false;
        try {
            res = drv.lockRecord(cls, objId, timeout);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return res;
    }
    
    public List runSql(String sql,int limit,boolean isUpdate) {
        List res = null;
        try {
            res = drv.runSql(sql,limit,isUpdate);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return res;
    }
    public List runSql(String sql,boolean isUpdate) {
        List res = null;
        try {
            res = drv.runSql(sql,isUpdate);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return res;
    }

    public boolean updateAttrFromExtDb(String jndiName,long attrId, String objUids) {
    	return drv.updateAttrFromExtDb(jndiName,attrId, objUids);
    }

    public void setTaskListFilter(AnyPair[] params){
        getUserSession().setTasksFilter(params);
    }

	public UserSessionValue[] getUserSessions() throws KrnException {
		if (this.user == null || !this.user.isAdmin()) {
			throw new KrnException(USER_NO_RIGHTS,
                "У пользователя недостаточно прав для выполнения данной операции");
		}
		List<UserSessionValue> res = new ArrayList<UserSessionValue>();
		for (ServerUserSession us : getActiveUsers()) {
			res.add(createValueObject(us));
		}
		return res.toArray(new UserSessionValue[res.size()]);
	}
	
	public UserSessionValue[] getUserSessions(int criteria, String txt, String txt2) throws KrnException {
		if (this.user == null || !this.user.isAdmin()) {
			throw new KrnException(USER_NO_RIGHTS,
                "У пользователя недостаточно прав для выполнения данной операции");
		}
		List<UserSessionValue> res = new ArrayList<UserSessionValue>();
		for (ServerUserSession us : getActiveUsers(criteria, txt, txt2)) {
			res.add(createValueObject(us));
		}
		return res.toArray(new UserSessionValue[res.size()]);
	}
	
	public List<ServerUserSession> getActiveUsers(int criteria, String txt, String txt2){
		List<ServerUserSession> res = new ArrayList<ServerUserSession>();

		if(criteria == BY_TIME) {
			java.util.Date startDate = null;
			java.util.Date endDate = null;
			try {
				startDate = txt != null? kz.tamur.util.Funcs.getDateFormat(DD_MM_YYYY_HH_MM_SS).parse(txt): null;
				endDate = txt2 != null? kz.tamur.util.Funcs.getDateFormat(DD_MM_YYYY_HH_MM_SS).parse(txt2): null;
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
    		synchronized (allUserSessionCache) {
				for (UUID key : allUserSessionCache.keySet()) {
					ServerUserSession us = getUserSession(key);
					java.util.Date usStartTime = us.getStartTime();
	
					if(startDate != null && endDate != null) {
						if(usStartTime.after(startDate) && usStartTime.before(endDate)) {
							res.add(us);
						}
					} else if(startDate != null){
						if(usStartTime.after(startDate)) {
							res.add(us);
						}
					} else if(endDate != null) {
						if(usStartTime.before(endDate)) {
							res.add(us);
						}
					} else {
						res.add(us);
					}
				}
    		}
		} else {
			
			java.util.Date endDate = null;
			try {
				endDate = txt2 != null? kz.tamur.util.Funcs.getDateFormat(DD_MM_YYYY_HH_MM_SS).parse(txt2): null;
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
    		synchronized (allUserSessionCache) {
				for (UUID key : allUserSessionCache.keySet()) {
					ServerUserSession us = getUserSession(key);
					
					if(endDate != null) {
						if(us.getStartTime().after(endDate)) {
							continue;
						}
					}
					
					switch(criteria) {
					case BY_SRVNAME: 
						if(us.getServerId() != null && us.getServerId().toLowerCase().indexOf(txt.toLowerCase()) > -1) {
							res.add(us);			
						}
						break;
					case BY_DB: 
						if(us.getDsName() != null && us.getDsName().toLowerCase().indexOf(txt.toLowerCase()) > -1) {
							res.add(us);
						}
						break;
					case BY_SESSION:
						if(us.getId() != null && us.getId().toString().toLowerCase().indexOf(txt.toLowerCase()) > 0) {
							res.add(us);
						}
						break;
					case BY_CLIENTTYPE:
						if(us.getTypeClient() != null && us.getTypeClient().indexOf(txt.toLowerCase()) > -1) {
							res.add(us);
						}
						break;
					case BY_LOGIN:
						if(us.getUserName() != null && us.getUserName().toLowerCase().indexOf(txt.toLowerCase()) > -1) {
							res.add(us);
						}
						break;
					case BY_IP:
						if(us.getIp() != null && us.getIp().toLowerCase().indexOf(txt.toLowerCase()) > -1) {
							res.add(us);	
						}
						break;
					case BY_COMP:
						if(us.getComputer() != null && us.getComputer().toLowerCase().indexOf(txt.toLowerCase()) > -1) {
							res.add(us);
						}
						break;
					}
				}
    		}
		}
		return res;
	}
	
	public List<ServerUserSession> getActiveUsers() {
		List<ServerUserSession> res = new ArrayList<ServerUserSession>();
		synchronized (allUserSessionCache) {
			for (UUID key : allUserSessionCache.keySet()) {
				ServerUserSession us = getUserSession(key);
				res.add(us);
			}
		}
		return res;
	}
	
    public UserSessionValue blockMethod(String muid) throws KrnException {
        if (this.user == null || !this.user.isAdmin()) {
            throw new KrnException(USER_NO_RIGHTS,
                "У пользователя недостаточно прав для выполнения данной операции");
        }
        try {
        	UserSession myUs = this.user;
        	lockMethod(muid, Lock.LOCK_SESSION, 0, myUs.getId().toString());
        	commitTransaction();
        } catch (KrnException e) {
        	rollbackTransaction();
        	LockMethod lock = getLockMethod(muid);
        	if (lock != null) {
        		if (lock.sessionId != null) {
		        	UserSession us = findUserSession(UUID.fromString(lock.sessionId));
		        	if (us != null) {
		        		return createValueObject(us);
		        	}
        		}
        		return new UserSessionValue(null, null, null, null, null, null, null, null, null, false, null,UserSession.SERVER_ID);
        	}
        }
        return null;
    }

    public UserSessionValue blockObject(long objId) throws KrnException {
        if (this.user == null || !this.user.isAdmin()) {
            throw new KrnException(USER_NO_RIGHTS,
                "У пользователя недостаточно прав для выполнения данной операции");
        }
        try {
        	UserSession myUs = this.user;
        	lockObject(objId, objId, Lock.LOCK_SESSION, 0, myUs.getId().toString());
        	commitTransaction();
        } catch (KrnException e) {
        	rollbackTransaction();
        	Lock lock = getLock(objId, objId);
        	if (lock != null) {
        		if (lock.sessionId != null) {
		        	UserSession us = findUserSession(UUID.fromString(lock.sessionId));
		        	if (us != null) {
		        		return createValueObject(us);
		        	}
        		}
        		return new UserSessionValue(null, null, null, null, null, null, null, null, null, false, null,UserSession.SERVER_ID);
        	}
        }
        return null;
    }

    public UserSessionValue getObjectBlocker(long objId) throws KrnException {
        if (this.user == null || !this.user.isAdmin()) {
            throw new KrnException(USER_NO_RIGHTS,
                "У пользователя недостаточно прав для выполнения данной операции");
        }
    	Lock lock = getLock(objId, objId);
    	UserSession myUs = this.user;
    	if (lock != null && !myUs.getId().toString().equals(lock.sessionId)) {
    		if (lock.sessionId != null) {
	    		UserSession us = findUserSession(UUID.fromString(lock.sessionId));
	            if (us != null) {
	                return createValueObject(us);
	            }
    		}
    		return new UserSessionValue(null, null, null, null, null, null, null, null, null, false, null,UserSession.SERVER_ID);
    	}
        return null;
    }

    public UserSessionValue vcsLockObject(long objId) throws KrnException {
        if (this.user == null || !this.user.isAdmin()) {
            throw new KrnException(USER_NO_RIGHTS,
                "У пользователя недостаточно прав для выполнения данной операции");
        }
        try {
        	UserSession myUs = this.user;
        	long userId=drv.checkVcsDataChanges(objId);
        	KrnClass cls=getClassById(myUs.getUserObj().classId);
        	KrnAttribute attr=getAttributeByName(cls, "name");
        	String title=getStringsSingular(userId, attr.id,0, false,false);
        	if(userId!=myUs.getUserObj().id){
        		return new UserSessionValue(null, null, null, null, title, null, null, null, null, false, null,UserSession.SERVER_ID);
        	}
        } catch (DriverException e) {
        	log.error(e, e);
        }
        return null;
    }

	public UserSessionValue vcsLockModel(String uid, int modelChangeType) throws KrnException {
		if (this.user == null || !this.user.isAdmin()) {
			throw new KrnException(USER_NO_RIGHTS, "У пользователя недостаточно прав для выполнения данной операции");
		}
		try {
			UserSession myUs = this.user;
			long userId = drv.checkVcsModelChanges(uid, modelChangeType);
			KrnClass cls = getClassById(myUs.getUserObj().classId);
			KrnAttribute attr = getAttributeByName(cls, "name");
			String title = getStringsSingular(userId, attr.id, 0, false, false);
			if (userId != myUs.getUserObj().id) {
				return new UserSessionValue(null, null, null, null, title, null, null, null, null, false, null, UserSession.SERVER_ID);
			}
		} catch (DriverException e) {
			log.error(e, e);
		}
		return null;
	}
	
	public void releaseEngagedObject(long objId) {
    	try {
			unlockObject(objId, objId);
			commitTransaction();
		} catch (KrnException e) {
			log.error(e, e);
			rollbackTransactionQuietly();
		}
    }

	public void killUserSessions(UUID usId, boolean blockUser) throws KrnException {
		if (this.user != null && !this.user.isAdmin()) {
			throw new KrnException(USER_NO_RIGHTS,
                "У пользователя недостаточно прав для выполнения данной операции");
		}
		ServerUserSession us = findUserSession(usId);
		if (us != null)
			killUserSessions(us, blockUser);
		else
			log.warn("Session for killing not found: " + usId);
	}
	
	public void killUserSessions(ServerUserSession us, boolean blockUser) throws KrnException {
		log.info("Staring killing session of user:" + us.getUserName());
		if (blockUser) {
			KrnObject user = us.getUserObj();
			if (user != null) {
				KrnClass userCls = getClassByName("User");
				try {
					KrnAttribute blockedAttr = getAttributeByName(userCls, "blocked");
					setLong(user.id, blockedAttr.id, 0, 1, 0);
					commitTransaction();
					
		    		writeLogRecord(
		    			SystemEvent.EVENT_USER_BLOCK,
		    			getUserSession().getUserName(),
		    			getIpAddress(), getComputer(), getUserSession().isAdmin(), us.getUserName(),-1,-1);
		    		
				} catch (KrnException e) {
					rollbackTransactionQuietly();
					if (e.code == ATTRIBUTE_NOT_FOUND) {
                        log.warn("Атрибут 'blocked' не найден в классе 'User'");
					} else {
						throw e;
					}
				}
			}
		}
		
		if (user != null) {
			writeLogRecord(
				SystemEvent.EVENT_USER_CLOSE,
				getUserSession().getUserName(),
				getIpAddress(), getComputer(), getUserSession().isAdmin(), us.getUserName(),-1,-1);
		}
		
    	try {
    		if (us != null && us.isMySession()) {
    			log.info("Killing (same server) session of user:" + us.getUserName());
    			writeLogRecord(SystemEvent.EVENT_LOGOUT, us.getUserName(), us.getIp(), us.getComputer(), us.isAdmin(), "",-1,-1);
	    		us.cachedClear();
				drv.rollbackLocks(us.getId().toString());
				removeUserSession(us);
    			log.info("Killed (same server) session of user:" + us.getUserName());
    		} else if (us != null) {
    			log.info("Session of user " + us.getUserName() + " on remote server. Sent killing message to " + us.getServerId());
    			ServerMessage.sendKillMessage(us.getDsName(), us.getId());
    		}
		} catch (DriverException e) {
            log.error(e.getMessage(), e);
		}
	}
	
	public boolean handlekillUserSession(UUID usId) throws KrnException {
		ServerUserSession us = findUserSession(usId);
    	try {
    		if (us != null && us.isMySession()) {
    			log.info("Killing (remote server) session of user:" + us.getUserName());
    			writeLogRecord(SystemEvent.EVENT_LOGOUT, us.getUserName(), us.getIp(), us.getComputer(), us.isAdmin(), "",-1,-1);
	    		us.cachedClear();
				drv.rollbackLocks(us.getId().toString());
				removeUserSession(us);
    			log.info("Killed (remote server) session of user:" + us.getUserName());
				return true;
    		}
		} catch (DriverException e) {
            log.error(e.getMessage(), e);
		}
    	return false;
	}

	public void refreshMethodsForReplication() {
        List<KrnClass> classes = new ArrayList<KrnClass>();
        try {
            db.getSubClasses(db.getClassByName("Объект").id, true, classes);
            for (KrnClass cls : classes) {
                List<KrnMethod> ms = db.getMethodsByClassId(cls.id);
                for (KrnMethod m : ms) {
                    drv.logModelChanges(2, 0, m.uid);
                }
            }
            drv.commit();
        } catch (DriverException e) {
            log.error(e.getMessage(), e);
        }
    }
	

	public String getAttributeComment(long attrId) throws KrnException {
		try {
			return drv.getAttributeComment(attrId);
		} catch (DriverException e) {
			log.error(e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		}
	}

	public void setAttributeComment(String attrUid, String comment) throws KrnException {
		try {
			drv.setAttributeComment(attrUid, comment);
		} catch (DriverException e) {
			log.error(e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		}
	}

	public String getClassComment(long clsId) throws KrnException {
		try {
			return drv.getClassComment(clsId);
		} catch (DriverException e) {
			log.error(e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		}
	}

	public void setClassComment(String clsUid, String comment) throws KrnException {
		try {
			drv.setClassComment(clsUid, comment);
		} catch (DriverException e) {
			log.error(e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		}
	}

	public String getMethodComment(String methodId) throws KrnException {
		try {
			return drv.getMethodComment(methodId);
		} catch (DriverException e) {
			log.error(e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		}
	}

	public void setMethodComment(String methodId, String comment) throws KrnException {
		try {
			drv.setMethodComment(methodId, comment);
		} catch (DriverException e) {
			log.error(e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		}
	}

    public void writeLogRecord(String type, String event, String description) {
        writeLogRecord(null, type, event, description);
    }
    
    public void writeLogRecord(String loggerName, String type, String event, String description) {
        if (user != null && !"sys".equals(user.getUserName())) {
            String res = " | " + type + " | " + event + " | " + user.getUserName() + " | " + user.getIp() + " | "
                    + user.getComputer() + " | " + (user.isAdmin() ? "1" : "0") + " | " + description;
            Log log = (loggerName == null) ? protocol : getLog(loggerName);
            log.info(res);
        }
    }

    public void writeLogRecord(SystemEvent event, String description,long clsId,long attrId){
 	   if (user != null) {
 		   writeLogRecord(event, user.getUserName(), user.getIp(), user.getComputer(), user.isAdmin(), description, clsId, attrId);
	   }
    }

    public void writeLogRecord(SystemEvent event, String name,
			String ip, String comp, boolean isAdmin, String description,long clsId,long attrId){
 	   boolean needLog = db != null ? checkLogCondition(event, name, ip, isAdmin) : true;
	   if (needLog) {
		   writeLogRecord(event, name, ip, comp, isAdmin, description, clsId, attrId, needLog);
	   }    	
    }
    
    public void writeLogRecord(SystemEvent event, String name,
    						String ip, String comp, boolean isAdmin, String description, 
    						long clsId, long attrId, boolean needLog) {
	   if (needLog) {
		   KrnClass cls=null;
		   KrnAttribute attr =null;
		   try{
			   cls = clsId>0?db.getClassById(clsId):null;
		   }catch(Exception e){
				log.info(e.getMessage());
		   }
		   try{
			   attr = attrId>0?db.getAttributeById(attrId):null;
			   if(attr!=null && cls==null){
				   cls=db.getClassById(attr.classId);
			   }
		   }catch(Exception e){
				log.info(e.getMessage());
		   }
		   description = Funcs.xmlQuote(description);
		   if (description.length() > 2000)
			   description = description.substring(0, 2000);
 	       StringBuilder res = new StringBuilder(" | ").append(SystemEvent.TYPES[event.getTypeCode()]).append(" | ").append(event.getName())
 	    		   .append(" | ").append(name).append(" | ").append(ip).append(" | ").append(comp).append(" | ").append(isAdmin ? 1 : 0)
 	    		   .append(" | ").append(SERVER_ID).append(" | ").append(description).append(" | ")
 	    		   .append(cls!=null? cls.tname!=null ? cls.tname:("ct"+cls.id) : "").append(" | ")
 	    		   .append(attr!=null ? attr.tname!=null ? attr.tname:("cm"+attr.id) : "");
	       protocol.info(res);
	   }
    }

	public void writeLogRecord(SystemEvent event, String proc, String objName, String description,long clsId,long attrId) {
		if (user != null) {
			writeLogRecord(event, user.getUserName(), user.getIp(), user.getComputer(), user.isAdmin(), proc, objName, description,clsId,attrId);
		}
	}
	
	public void writeLogRecord(SystemEvent event, String name, String ip, String comp, boolean isAdmin,
			String proc, String objName, String description,long clsId,long attrId) {
		
		boolean needLog = db != null ? checkLogCondition(event, name, ip, isAdmin) : true;

		if (needLog) {
			   KrnClass cls=null;
			   KrnAttribute attr =null;
			   try{
				   cls = clsId>0?db.getClassById(clsId):null;
			   }catch(Exception e){
					log.info(e.getMessage());
			   }
			   try{
				   attr = attrId>0?db.getAttributeById(attrId):null;
				   if(attr!=null && cls==null){
					   cls=db.getClassById(attr.classId);
				   }
			   }catch(Exception e){
					log.info(e.getMessage());
			   }
			description = Funcs.xmlQuote(description);
			if (description.length() > 2000)
				   description = description.substring(0, 2000);
			StringBuilder res = new StringBuilder(" | ").append(SystemEvent.TYPES[event.getTypeCode()]).append(" | ")
					.append(event.getName()).append(" | ").append(name).append(" | ").append(ip).append(" | ")
					.append(comp).append(" | ").append(isAdmin ? 1 : 0).append(" | ").append(SERVER_ID)
					.append(" | ").append(proc).append(" | ").append(objName).append(" | ").append(description)
					.append(" | ").append(cls!=null? cls.tname!=null?cls.tname:("ct"+cls.id) : "")
					.append(" | ").append(attr!=null ? attr.tname : "");
			protocol.info(res);
		}
	}

    public void writeCreateObjectLogRecord(long objId, String name, long trId) throws KrnException {
  	   	if (user != null) {
  	   		KrnObject obj = getObjectById(objId, trId);
  	   		
  	   		boolean needLog = db != null ? checkLogCondition(SystemEvent.EVENT_OBJECT_CREATE, user.getUserName(), user.getIp(), 
  	   				user.isAdmin(), obj, 1) : true;
  	   		
  	   		if (needLog) {
	  	   		StringBuilder res = new StringBuilder("ID: ").append(objId)
	  	   							.append(", КЛАСС: ").append(name)
	  	   							.append(", trId: ").append(trId);
		   		
	        	writeLogRecord(SystemEvent.EVENT_OBJECT_CREATE, user.getUserName(), user.getIp(), user.getComputer(), user.isAdmin(), res.toString(), obj!=null?obj.classId:-1, -1, true);
  	   		}
  	   	}
    }

    public void writeDeleteObjectLogRecord(KrnObject obj, String name, long trId) throws KrnException {
  	   	if (user != null) {
  	   	//	KrnObject obj = getObjectById(objId, trId);

  	   		boolean needLog = db != null ? checkLogCondition(SystemEvent.EVENT_OBJECT_DELETE, user.getUserName(), user.getIp(), 
  	   				user.isAdmin(), obj, 2) : true;
  	   		
  	   		if (needLog) {
	  	   		StringBuilder res = new StringBuilder("ID: ").append(obj.id)
	  	   							.append(", КЛАСС: ").append(name)
	  	   							.append(", trId: ").append(trId);
		   		
	        	writeLogRecord(SystemEvent.EVENT_OBJECT_DELETE, user.getUserName(), user.getIp(), user.getComputer(), user.isAdmin(), res.toString(), obj!=null?obj.classId:-1, -1, true);
  	   		}
  	   	}
    }

    public void writeSetValueLogRecord(long objId, long attrId, long langId, Object value, long trId){
  	   	if (user != null) {
  	   		KrnObject obj = null;
			try {
				obj = getObjectById(objId, trId);
			} catch (KrnException e) {
				log.error(e, e);
			}

  	   		boolean needLog = db != null ? checkLogCondition(SystemEvent.EVENT_VALUE_SET, user.getUserName(), user.getIp(), 
  	   				user.isAdmin(), obj, attrId) : true;
  	   		
  	   		if (needLog) {
  	  	   		StringBuilder res = new StringBuilder("ID: ").append(objId)
 							.append(", атрибут: ").append(attrId);
  	  	   		if (langId > 0)
  	  	   			res.append(", язык: ").append(langId);
 		
  	  	   		res.append(", значение: ");
 		
  	  	   		if (value instanceof byte[])
  	  	   			res.append("blob[").append(((byte[])value).length).append("]");
  	  	   		else
  	  	   			res.append(value);
 		
  	  	   		res.append(", trId: ").append(trId);
		   		
	        	writeLogRecord(SystemEvent.EVENT_VALUE_SET, user.getUserName(), user.getIp(), user.getComputer(), user.isAdmin(), res.toString(), obj!=null?obj.classId:-1, attrId, true);
  	   		}
  	   	}
    }

    public void writeDeleteValueLogRecord(long objId, long attrId, long langId, Object value, long trId) throws KrnException {
  	   	if (user != null) {
  	   		KrnObject obj=getObjectById(objId, trId);

  	   		boolean needLog = db != null ? checkLogCondition(SystemEvent.EVENT_VALUE_DELETE, user.getUserName(), user.getIp(), 
  	   				user.isAdmin(), obj, attrId) : true;
  	   		
  	   		if (needLog) {
  	  	   		StringBuilder res = new StringBuilder("ID: ").append(objId)
 							.append(", атрибут: ").append(attrId);
  	  	   		if (langId > 0)
  	  	   			res.append(", язык: ").append(langId);
 		
  	  	   		if (value != null) {
  	  	   			res.append(", значение: ");
 		
  	  	   			if (value instanceof byte[])
  	  	   				res.append("blob[").append(((byte[])value).length).append("]");
  	  	   			else
  	  	   				res.append(value);

  	  	   			res.append(", trId: ").append(trId);
  	  	   		}
		   		
	        	writeLogRecord(SystemEvent.EVENT_VALUE_DELETE, user.getUserName(), user.getIp(), user.getComputer(), user.isAdmin(), res.toString(), obj!=null?obj.classId:-1, attrId, true);
  	   		}
  	   	}
    }

    public int blockServer(boolean serverBlocked) {
        if (serverBlocked) {
            SessionManager.unblockServer(user.getIp());
            writeLogRecord(SystemEvent.EVENT_SERVER_UNBLOCK, getIpAddress(),-1,-1);
        } else {
            SessionManager.blockServer(user.getIp());
            writeLogRecord(SystemEvent.EVENT_SERVER_BLOCK, getIpAddress(),-1,-1);
        }
		ServerMessage.sendBlockServer(getDsName(), getIpAddress(), serverBlocked ? ServerMessage.ACTION_UNBLOCK_SERVER : ServerMessage.ACTION_BLOCK_SERVER);
        return isServerBlocked();
    }
    
    public int isServerBlocked() {
        return SessionManager.isServerBlocked(user.getIp());
    }
    
    public int isServerBlocked(String ip) {
        return SessionManager.isServerBlocked(ip);
    }

    public List<String> showDblocks(){
    		return drv.showDbLocks();
    }
    public void lockMethod(String muid, int scope, long flowId,
    		String sessionId) throws KrnException {
    	try {
    		drv.lockMethod(muid, scope, flowId, sessionId);
    	} catch (DriverException e) {
    		throw new KrnException(e.getErrorCode(), e.getMessage());
    	}
    }
    public void unlockMethod(String muid)
    throws KrnException {
    	try {
    		drv.unlockMethod(muid);
    	} catch (DriverException e) {
    		log.error(e, e);
    		throw new KrnException(e.getErrorCode(), e.getMessage());
    	}
    }
    public Lock lockObject(long objId, long lockerId, int scope, long flowId,
    		String sessionId) throws KrnException {
    	try {
    		drv.lockObject(objId, lockerId, scope, flowId, sessionId);
    		return new Lock(objId, lockerId, flowId, sessionId, scope);
    	} catch (DriverException e) {
    		throw new KrnException(e.getErrorCode(), e.getMessage());
    	}
    }
    
    public void unlockObject(long objId, long lockerId)
    throws KrnException {
    	try {
    		drv.unlockObject(objId, lockerId);
    	} catch (DriverException e) {
    		log.error(e, e);
    		throw new KrnException(e.getErrorCode(), e.getMessage());
    	}
    }
    
    public void unlockFlowObjects(long flowId)
    throws KrnException {
    	try {
    		drv.unlockFlowObjects(flowId);
    	} catch (DriverException e) {
    		log.error(e, e);
    		throw new KrnException(e.getErrorCode(), e.getMessage());
    	}
    }

    public void unlockUnexistingFlowObjects()
    		throws KrnException {
    	try {
    		drv.unlockUnexistingFlowObjects(getClassByName("Flow").id);
    	} catch (DriverException e) {
    		log.error(e, e);
    		throw new KrnException(e.getErrorCode(), e.getMessage());
    	}
    }

    public Lock getLock(long objId, long lockerId) throws KrnException {
    	try {
    		return drv.getLock(objId, lockerId);
    	} catch (DriverException e) {
    		log.error(e, e);
    		throw new KrnException(e.getErrorCode(), e.getMessage());
    	}
    }

    public LockMethod getLockMethod(String muid) throws KrnException {
    	try {
    		return drv.getLockMethod(muid);
    	} catch (DriverException e) {
    		log.error(e, e);
    		throw new KrnException(e.getErrorCode(), e.getMessage());
    	}
    }
    public Collection<Lock> getLocksByObjectId(long objId) throws KrnException {
    	try {
    		return drv.getLocksByObjectId(objId);
    	} catch (DriverException e) {
    		log.error(e, e);
    		throw new KrnException(e.getErrorCode(), e.getMessage());
    	}
    }

    public Collection<Lock> getLocksByLockerId(long lockerId) throws KrnException {
    	try {
    		return drv.getLocksByLockerId(lockerId);
    	} catch (DriverException e) {
    		log.error(e, e);
    		throw new KrnException(e.getErrorCode(), e.getMessage());
    	}
    }

    public Collection<Lock> getAllLocks() throws KrnException {
    	try {
    		return drv.getAllLocks();
    	} catch (DriverException e) {
    		log.error(e, e);
    		throw new KrnException(e.getErrorCode(), e.getMessage());
    	}
    }

    public Collection<LockMethod> getMethodAllLocks() throws KrnException {
    	try {
    		return drv.getMethodAllLocks();
    	} catch (DriverException e) {
    		log.error(e, e);
    		throw new KrnException(e.getErrorCode(), e.getMessage());
    	}
    }
    public Element prepareReport(long reportId, KrnObject lang, KrnObject[] srvObjs, FilterDate[] fds) throws KrnException {
    	try {
    		SrvReport srv = new SrvReport(this);
    		return srv.prepareReport(reportId, lang, srvObjs, fds);
    	} catch (Exception e) {
    		log.error(e, e);
    		throw new KrnException(0, e.getMessage());
    	}
    }

    public static void clientTaskReload(Collection<Long> list, Collection<Long> listInf, long flowId) {
		synchronized (allUserSessionCache) {
			for (UUID key : allUserSessionCache.keySet()) {
				ServerUserSession us = getUserSession(key);
	
				if (us == null) continue;
				
				if (us.callbacks() && !Constants.CLIENT_TYPE_DESIGNER.equals(us.getTypeClient()) && !Constants.CLIENT_TYPE_REPORT.equals(us.getTypeClient())) {
					if (list == null && listInf == null) {
						sendNoteClustered(us, new TaskReloadNote(new java.util.Date(), null, flowId, -1));
					} else if (list!=null && listInf!=null && listInf.contains(us.getUserId()) && list.contains(us.getUserId())) {
						sendNoteClustered(us, new TaskReloadNote(new java.util.Date(), null, flowId, 2));
					} else if (listInf!=null && listInf.contains(us.getUserId())) {
						sendNoteClustered(us, new TaskReloadNote(new java.util.Date(), null, flowId, 1));
					} else if (list!=null && list.contains(us.getUserId())) {
						sendNoteClustered(us, new TaskReloadNote(new java.util.Date(), null, flowId, 0));
					} else if (us.isAdmin()) {
						//users_ = orgComp.getBaseUsers(user.getBaseId());
						int flowParam = -1;
						OrganisationComponent orgComp = Session.getExeComp(us.getDsName()).getOrgComp();
						long balId = us.getBalansId();
						if (balId > 0) {
							Collection<Long> users_ = orgComp.getBalansEdUsers(balId);
							
							for (Long user : users_) {
								if (listInf.contains(user) && list.contains(user))
									flowParam = 2;
								else if (listInf.contains(user))
									flowParam = 1;
								else if (list.contains(user))
									flowParam = 0;
								
								if (flowParam > -1) {
									sendNoteClustered(us, new TaskReloadNote(new java.util.Date(), null, flowId, flowParam));
									break;
								}
							}
						} else {						
							if (list != null && listInf != null) {
								sendNoteClustered(us, new TaskReloadNote(new java.util.Date(), null, flowId, 2));
							} else if (listInf != null) {
								sendNoteClustered(us, new TaskReloadNote(new java.util.Date(), null, flowId, 1));
							} else if (list != null) {
								sendNoteClustered(us, new TaskReloadNote(new java.util.Date(), null, flowId, 0));
							}
						}
					}
				}
			}
		}
	}
    
    public static void sendNoteClustered(ServerUserSession us, Note note) {
		if (us != null) {
			if (us.isMySession())
				us.addNote(note);
			else
				ServerMessage.sendMessage(us.getDsName(), us.getId(), note);
		}
    }
       
    public void sendMessage(UUID usId, String message) {
    	if ("Akmaral index".equals(message)) {
	    	try {
	    		log.info("MSDoc indexing started.");
	    		Indexer.indexAllMSDoc(null, 122, 0, this);
	    		log.info("MSDoc indexing stoped.");
	    	} catch (Exception e) {
	    		log.error("MSDoc indexing failed.");
	        	log.error(e, e);
	    	}
    	}

		ServerUserSession us = findUserSession(usId);
		if (us != null) {
    		sendNoteClustered(us, new MessageNote(new java.util.Date(), createValueObject(user), message));
    	} else {
    		int ind = message.indexOf('|');
    		if (ind > 0) {
    			ResponseWaiter rw = getResponseWaiter(usId, message.substring(0, ind));
    			if (rw != null) {
    				rw.responseRecieved(message.substring(ind + 1));
    				return;
    			}
    		}
    		ServerMessage.sendMessage(getDsName(), usId, new MessageNote(new java.util.Date(), createValueObject(user), message));
    	}
    }
    
    public void sendMessage(KrnObject user, String message) {
    	ServerUserSession us = findUserSession(user.id);
		if (us != null) {
    		sendNoteClustered(us, new MessageNote(new java.util.Date(), null, message));
		}
    }
    
    public KrnObject sendNotification(KrnObject user, String message, String uid, String cuid, String proc, String iter, long trId) {
    	try {
	    	KrnClass notificationCls = db.getClassByName("Notification");
	    	KrnAttribute messageAttr = db.getAttributeByName(notificationCls.id, "message");
	    	KrnAttribute uidAttr = db.getAttributeByName(notificationCls.id, "uid");
	    	KrnAttribute cuidAttr = db.getAttributeByName(notificationCls.id, "cuid");
	    	KrnAttribute datetimeAttr = db.getAttributeByName(notificationCls.id, "datetime");
	    	
	    	trId = (trId == -1) ? getContext().trId : trId;
	    	KrnObject notificationObj = createObject(notificationCls, trId);
	    	setString(notificationObj.id, messageAttr.id, 0, 0, false, message, trId);
	    	setString(notificationObj.id, uidAttr.id, 0, 0, false, uid, trId);
	    	setString(notificationObj.id, cuidAttr.id, 0, 0, false, cuid, trId);
	    	
	    	java.util.Date datetime = new java.util.Date();
	    	setTime(notificationObj.id, datetimeAttr.id, 0, kz.tamur.util.Funcs.convertTime(datetime), trId);

	    	KrnClass userCls = db.getClassByName("User");
	    	KrnAttribute notificationsAttr = db.getAttributeByName(userCls.id, "notifications");
	    	setObject(user.id, notificationsAttr.id, 0, notificationObj.id, trId, true);
	    	
	    	ServerUserSession us = findUserSession(user.id);
			if (us != null) {
				NotificationNote note = new NotificationNote(datetime, null, notificationObj.id, message, uid, cuid, proc, iter);
				if (trId == 0)
					sendNoteClustered(us, note);
				else
					NotificationListener.instance(dsName).addNotificationNote(note, user.id, trId);
			}
			
			return notificationObj;
    	} catch (KrnException e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
    public void interfaceChanged(long id) {
		List<MetadataChangeListener> list = getMetadataChangeListeners();
		for (MetadataChangeListener l : list) {
			l.ifcChanged(id);
		}
		if (UserSession.SERVER_ID != null)
			ServerMessage.sendMessage(UserSession.SERVER_ID, user.getDsName(), ServerMessage.ACTION_CHG_IFC, id, 0);
    }

    public void processCanceled(long id, String serverId, String nodeId, boolean isAll, boolean forceCancel) {
		ServerMessage.sendMessage(serverId, user.getDsName(), ServerMessage.ACTION_CANCEL_PROCESS, new Object[] {id, nodeId, isAll, forceCancel}, 0);
    }

    // Используется для отправки сообщения о загруженности сервера
    public void sendMessage(String message) {
		synchronized (allUserSessionCache) {
			for (UUID key : allUserSessionCache.keySet()) {
				ServerUserSession us = getUserSession(key);
		    	if (us != null && us.callbacks() && Constants.CLIENT_TYPE_DESIGNER.equals(us.getTypeClient())) {
		    		sendNoteClustered(us, new MessageNote(new java.util.Date(), null, message));
		    	}
			}
		}
    }
    
    public void sendMessage(int resultCode, Map<String, Object> varsMap, String message) {
		getUserSession().addNote(new ScriptExecResultNote(new java.util.Date(), null, resultCode, varsMap, message));
    }
    
    // Используется для передачи прогресса импорта репликации (для файлов)
    public void sendMessage(int type, int filesCount, int currentFileNumber, String currentFileName, Time importTime) {
		getUserSession().addNote(new ReplFilesProgressNote(new java.util.Date(), null, type, filesCount, currentFileNumber, currentFileName, importTime));
    }
    
    // Используется для передачи прогресса импорта репликации (для записей в файле)
    public void sendMessage(int type, int currentChangeNumber, int changesCount, String changeType, String changeId) {
		getUserSession().addNote(new ReplChangesProgressNote(new java.util.Date(), null, type, currentChangeNumber, changesCount, changeType, changeId));
    }

    public void orderChanged(long userId, String operation, String type, List<String> orderIds) {
    	synchronized (myUserSessionCache) {
    		for (UUID key : myUserSessionCache.keySet()) {
    			ServerUserSession us = myUserSessionCache.get(key);
    	    	if (us != null && us.callbacks() && us.getUserId() == userId
    	    			&& (Constants.CLIENT_TYPE_WEB.equals(us.getTypeClient()) || Constants.CLIENT_TYPE_LOCALWEB.equals(us.getTypeClient()))
    	    			&& dsName.equals(us.getDsName())) {
    	    		
        			us.addNote(new OrderReloadNote(new java.util.Date(), createValueObject(user), operation, type, orderIds));
    	    		if (!us.isMulti() && !us.isAdmin()) return;
    	    	}
    		}
		}
    }

    public static ServerUserSession getUserSession(UUID id) {
    	ServerUserSession lus = myUserSessionCache.get(id);
    	return (lus != null) ? lus : allUserSessionCache.get(id);
    }
    
    public static ServerUserSession findUserSession(UUID id) {
    	ServerUserSession us = getUserSession(id);
    	if (us == null)
    		us = localUserSessionCache.get(id);
    	return us;
    }

    public static ServerUserSession findUserSession(String name) {
		synchronized (allUserSessionCache) {
			for (UUID key : allUserSessionCache.keySet()) {
		    	ServerUserSession us = getUserSession(key);
		    	if (us != null && us.getUserName() == name) {
		    		return us;
		    	}
			}
		}
    	return null;
    }
    
    public static boolean isReachedUserSessionLimit() {
    	String p = System.getProperty("connPort");
    	if (p != null && p.length() > 2) {
    		int maxCount = Integer.parseInt(p.substring(1, p.length() - 1));
    		int count = allUserSessionCache.size();
   			return count >= maxCount;
    	}
    	return false;
    }
    
    public static ServerUserSession findUserSession(long userId) {
		synchronized (myUserSessionCache) {
			for (UUID key : myUserSessionCache.keySet()) {
		    	ServerUserSession us = getUserSession(key);
		    	if (us != null && us.getUserId() == userId) {
		    		return us;
		    	}
			}
		}
		synchronized (allUserSessionCache) {
			for (UUID key : allUserSessionCache.keySet()) {
		    	ServerUserSession us = getUserSession(key);
		    	if (us != null && us.getUserId() == userId) {
		    		return us;
		    	}
			}
		}
    	return null;
    }
    
    public ServerUserSession findLocalUserSession(long userId) {
    	synchronized (myUserSessionCache) {
    		for (UUID key : myUserSessionCache.keySet()) {
    			ServerUserSession us = myUserSessionCache.get(key);
    	    	if (us != null && us.callbacks() && (Constants.CLIENT_TYPE_WEB.equals(us.getTypeClient()) || Constants.CLIENT_TYPE_LOCALWEB.equals(us.getTypeClient()))
    	    			&& dsName.equals(us.getDsName()) && us.getUserId() == userId) {
    	    		
        			return us;
    	    	}
    		}
    		return null;
		}
    }

    public static UserSessionValue createValueObject(UserSession us) {
    	return new UserSessionValue(
				us.getId(),
				us.getUserObj(),
				us.getBaseObj(),
				us.getDsName(),
				us.getUserName(),
				us.getLogUserName(),
				us.getTypeClient(),
				us.getIp(),
				us.getComputer(),
				us.isAdmin(),
				us.getStartTime(),
    			us.getServerId());
    }
    
    private ExecutionComponent getExeComp() {
    	return exeComps.get(user.getDsName());
    }
    
    public static ExecutionComponent getExeComp(String dsName) {
    	synchronized(exeComps) {
    		return exeComps.get(dsName);
    	}
    }

    public static void addExeComp(String dsName, ExecutionComponent exeComp) {
    	synchronized(exeComps) {
    		exeComps.put(dsName, exeComp);
    	}
    }

    public OrganisationComponent getOrgComp() {
    	synchronized(orgComps) {
    		OrganisationComponent orgComp = orgComps.get(dsName);
    		if (orgComp == null) {
    			orgComp = new OrganisationComponent(this);
    			orgComps.put(dsName, orgComp);
    		}
    		return orgComp;
    	}
    }
    
    public ServerTasks getServerTasks(String dsName) {
    	synchronized(serverTasks) {
    		return serverTasks.get(dsName);
    	}
    }
    
    public static void addServerTasks(String dsName, ServerTasks serverTasks) {
    	synchronized(Session.serverTasks) {
    		Session.serverTasks.put(dsName, serverTasks);
    	}
    }

    public static MessageCash getMessageCache(String dsName) {
    	synchronized(messageCaches) {
    		return messageCaches.get(dsName);
    	}
    }
    
    public static void addMessageCache(String dsName, MessageCash messageCache) {
    	synchronized(messageCaches) {
    		messageCaches.put(dsName, messageCache);
    	}
    }

	public Map<String, Object> getInterfaceVars(long flowId)
			throws KrnException {
		if (flowId != 0) {
	    	ExecutionComponent exeComp = getExeComp();
	        if (exeComp != null) {
	        	byte[] data = getBlob(flowId, exeComp.SA_IFC_VARS.id, 0, 0, 0);
	        	if (data.length > 0) {
	        		try {
	        			return WfUtils.loadFromXml(data, this);
	        		} catch (Exception e) {
	        			log.error(e);
	        			throw new KrnException(0, e.getMessage());
	        		}
	        	}
	        }
		}
        return Collections.EMPTY_MAP;
	}
	
	public String getDsName() {
		return dsName;
	}

    private void initLogs(String name) {
        String userName = (name == null) ? "sys" : name.replaceAll("\\s|\\.", "_");
        log = LogFactory.getLog(new StringBuilder().append(dsName).append(".").append(userName).append(".").append(UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "").append(Session.class.getName()).toString());
        protocol = LogFactory.getLog(new StringBuilder().append(dsName).append(".Protocol.").append(userName).append(UserSession.SERVER_ID != null ? ("." + UserSession.SERVER_ID) : "").toString());
    }
	
	public File getReplicationDirectory() {
		return db.getReplicationDirectory();
	}
	
	public String getReplicationDirectoryPath() {
		return db.getReplicationDirectoryPath();
	}
	
    private String getOwnerString() {
    	StringBuilder res = new StringBuilder();
    	if (user != null) {
    		res.append(user.getUserName());
        	res.append('@').append(user.getIp()).append("(");
        	res.append(user.getComputer()).append(')');
    	} else
    		res.append("unknown");
    	return res.toString();
    }
    
    public void refreshObjectCreating(KrnObject obj) {
    	try {
			drv.refreshObjectCreating(obj);
		} catch (SQLException e) {
            log.error(e.getMessage(), e);
		}
    }
    
    private void initUniversalGenerator() {
    	if (uniGen == null) {
    		uniGen = new UniversalGenerator();
    		uniGen.setSession(this);
    	}
    }

    public long getNextGeneratedNumber(String docTypeUid, Number period, Number initNumber) throws Exception {
		initUniversalGenerator();
		return uniGen.getNextNumber(docTypeUid, period, initNumber);
	}
	
    public long setLastGeneratedNumber(String docTypeUid, Number period, Number initNumber) throws Exception {
		initUniversalGenerator();
		return uniGen.setNumber(docTypeUid, period, initNumber);
	}

    public boolean rejectGeneratedNumber(String docTypeUid, Number period, Number number, Time time) throws Exception {
		initUniversalGenerator();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, time.msec);
        cal.set(Calendar.SECOND, time.sec);
        cal.set(Calendar.MINUTE, time.min);
        cal.set(Calendar.HOUR_OF_DAY, time.hour);
        cal.set(Calendar.DAY_OF_MONTH, time.day);
        cal.set(Calendar.MONTH, time.month);
        cal.set(Calendar.YEAR, time.year);
        KrnDate d = new KrnDate(cal.getTimeInMillis());

        return uniGen.rejectNumber(docTypeUid, period, number, d);
	}

	public long getOldGeneratedNumber(String docTypeUid, Number period) throws Exception {
		initUniversalGenerator();
		return uniGen.getOldNumber(docTypeUid, period);
	}

	public KrnObject saveNumber(String className, String attrName, String kadastrNumber) throws Exception {
		initUniversalGenerator();
		return uniGen.saveNumber(className, attrName, kadastrNumber);
	}

	public Note[] getNotes() {
    	if (user != null) {
        	return user.getNotes();
    	}
    	return null;
    }
	
    public Activity getTask(long flowId, long ifsPar, boolean isCheckEvent, boolean onlyMy) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if (exeComp != null) {
        	UserSession us = getUserSession();
			List<Activity> acts = db.getTasks(us, null, null, flowId, (Driver2) drv, -1, -1, isCheckEvent, onlyMy, null, true, null, "1");
       		Activity act = acts.get(0);
       		act.timeActive = exeComp.getTimeActive(flowId);
       		
       		ExecutionEngine activeEngine = ExecutionEngine.getActivityExecutionEngine(flowId);
       		// Если задание еще в обработке, не возваращать ошибочное состояние
       		if (activeEngine != null && (act.param & Constants.ACT_ERR) > 0 && (activeEngine.getFlow().getParam() & Constants.ACT_ERR) == 0)
       			act.param = act.param ^ Constants.ACT_ERR;
       		
       		return act;
        } else {
        	return null;
        }
    }

    public void ping() {
    	if (user != null) {
        	user.ping();
    	}
    }
    
    public void setOwnUser(boolean ownUser) {
    	this.ownUser = ownUser;
    }
    
    public String getReportLogName() {
    	return dsName + ".report.log";
    }
    
    public List<KrnObject> getSystemLangs() {
    	try {
    		return drv.getSystemLangs();
    	} catch (DriverException e) {
    		// Проглатываем эксепшн
    		log.error(e, e);
    		return null;
    	}
    }
    
    public Map<Long, String> getStringAllLangs(
    		long objId,
    		long attrId,
            long tid) throws KrnException {
    	Map<Long, String> res = new HashMap<Long, String>();
    	List<KrnObject> langs = getSystemLangs();
    	for (KrnObject lang : langs) {
    		String[] strs = getStrings(objId, attrId, lang.id, false, tid);
    		if (strs.length > 0)
    			res.put(lang.id, strs[0]);
    	}
    	return res;
    }

	public void setStringAllLangs(
			long objectId,
			long attrId,
			int index,
			Map<Long, String> value,
			long tid
	) throws KrnException {
    	for (Long langId : value.keySet()) {
    		setString(objectId, attrId, index, langId, false, value.get(langId), tid);
    	}
	}

    public int truncateClass(KrnClass cls) throws KrnException {
        try {
            return drv.truncate(cls);
        } catch (DriverException e) {
        	log.error(e, e);
            throw new KrnException(e.getErrorCode(), e.getMessage());
        }
    }
    
    public void indexMethods(KrnClass krnClass) throws KrnException, java.io.IOException {
    	byte [] blob;
    	for (KrnMethod krnMethod : getMethods(krnClass.id)) {
    		blob = this.getMethodExpression(krnMethod.uid);
    		Indexer.updateMethodsIndex(krnMethod, blob, null);
    	}
    }
    
    //Get Attributes of KrnClass that have blob field type
    public KrnAttribute[] getBlobAttributes(KrnClass krnClass) throws KrnException {
    	KrnAttribute[] krnAttributes = getAttributes(krnClass);
    	List<KrnAttribute> attrs = new ArrayList<KrnAttribute>();
    	for (KrnAttribute krnAttribute : krnAttributes) {
    		if (krnAttribute.typeClassId == 10)
    			attrs.add(krnAttribute);
    	}
    	return attrs.toArray(new KrnAttribute[0]);
    }
    
    //Lucene search
    public List<Object> search(String pattern, int results, int[] searchProperties, boolean[] searchArea) {
    	try {
    		return Indexer.search(pattern, results, searchProperties, searchArea, this);
    	} catch (ParseException e) {
    		log.warn(e);
    	} catch (java.io.IOException e) {
    		log.warn(e);
    	}
    	return new ArrayList<Object>();
		
    }
    
    //Lucene index Hierarchy of KrnClass
    public void indexHierarchy(String clName, boolean fullIndex) throws KrnException {
    	/*
    	log.info("Индексация иерархии класса " + clName);    	
    	KrnClass root = getClassByName(clName);
    	try {
    		indexClass(root, fullIndex);
    	} catch (Exception e) {
    		log.error("Ошибка индексации: " + e);
    	}
    	for (KrnClass krnClass : getClasses(root.id))
    		indexHierarchy(krnClass.name, fullIndex);*/
    	KrnClass parent = getClassByName(clName);
    	KrnClass node = getClasses(parent.id)[0];
    	KrnClass childs[];
    	while (node != null) {
    		log.info("Индексация иерархии класса " + node.name);
    		try {
				indexClass(node, fullIndex);
    		} catch (KrnException e) {
				log.error(e, e);
			}
    		if ((childs = getClasses(node.id)).length > 0) {
    			node = childs[0];
    		} else {
    			while (getNextSibling(node) == null && node != parent)
    				node = getClassById(node.parentId);
    			node = getNextSibling(node);
    		}
    	}
    }
        
    private KrnClass getNextSibling(KrnClass node) throws KrnException {
    	KrnClass [] childs = getClasses(node.parentId);
    	for (int i = 0; i < childs.length; i++)
    		if (childs[i].id == node.id && i + 1 < childs.length)
    			return childs[i + 1];
    	return null;
    }
    
    // Индексирование классов
    public void indexClass(KrnClass krnClass, boolean foolIndex) throws KrnException {
    	try {
    		indexMethods(krnClass);
    		KrnAttribute [] krnAttributes = getBlobAttributes(krnClass);
    		if (krnAttributes.length == 0)
    			return;
    		KrnObject [] krnObjects;
    		try {
    			krnObjects = getClassObjects(krnClass, new long[0], 0);
    		} catch (KrnException e) {
    			krnObjects = new KrnObject[0];
    		}
    		for (KrnAttribute krnAttribute : krnAttributes)
		    	for (KrnObject krnObject : krnObjects)
		    		indexObject(krnObject, krnClass, krnAttribute, foolIndex);
    	} catch (java.io.IOException e) {
    		log.error(e, e);
    	}
    }
    
    // Обнуление папки с индексами атрибута или методов
	public void dropIndex(KrnAttribute attr) throws KrnException {
		if (attr == null) {
			Indexer.dropIndex(null, 0);
		} else if (attr.isIndexed) {
			if (!attr.isMultilingual) {
				Indexer.dropIndex(attr, 0);
			}
			else {
				for (KrnObject lang : getSystemLangs()) {
					Indexer.dropIndex(attr, lang.id);
				}
			}
		}
	}
	
	// Обнуление папки с индексами vcs changes
	public void dropVcsChangesIndexFolder() throws KrnException {
		Indexer.dropVcsChangesIndexFolder();
	}

	// Обнуление папки с индексами триггеров
	public void dropTriggersIndexFolder() throws KrnException {
		Indexer.dropTriggersIndexFolder();
	}

    // Индексирование объектов
    public void indexObject(KrnObject krnObject, KrnClass krnClass, KrnAttribute krnAttribute, boolean foolIndex) throws KrnException {   
		if (krnAttribute.isIndexed || foolIndex) {
			if (!krnAttribute.isMultilingual) {
				for (byte [] blob : getBlobs(krnObject.id, krnAttribute.id, 0, 0))    							
					Indexer.updateIndex(krnObject, krnAttribute, blob, null, 0, 0, this);
			}
			else {
				for (KrnObject lang : getSystemLangs()) {
					for (byte [] blob : getBlobs(krnObject.id, krnAttribute.id, lang.id, 0))
						Indexer.updateIndex(krnObject, krnAttribute, blob, null, lang.id, 0, this);
				}
			}
		}
    }
    
    // Индексирование объектов
    public void indexObject(KrnObject krnObject, KrnClass krnClass, boolean foolIndex) throws KrnException, java.io.IOException {
    	if (krnClass.name.equals("MSDoc")) return;
    	for (KrnAttribute krnAttribute : getBlobAttributes(krnClass))
			if (krnAttribute.isIndexed || foolIndex) {
				if (!krnAttribute.isMultilingual) {					
					for (byte [] blob : getBlobs(krnObject.id, krnAttribute.id, 0, 0))    							
						Indexer.updateIndex(krnObject, krnAttribute, blob, null, 0, 0, this);
				}
				else {					
					for (byte [] blob : getBlobs(krnObject.id, krnAttribute.id, 122, 0))
						Indexer.updateIndex(krnObject, krnAttribute, blob, null, 122, 0, this);
					for (byte [] blob : getBlobs(krnObject.id, krnAttribute.id, 123, 0))
						Indexer.updateIndex(krnObject, krnAttribute, blob, null, 123, 0, this);
				}
			}
    }
    
    // Индексирование методов
    public void indexMethod(KrnMethod method) throws KrnException {
    	Indexer.updateMethodsIndex(method, getMethodExpression(method.uid), "UTF-8");
    }
    
    // Индексирование изменений объектов проектирования
    public void indexVcsChange(KrnVcsChange change) throws KrnException {
    	Indexer.updateVcsChangesIndex(change, getVcsChangeExpression(change), "UTF-8");
    }

    // Индексирование триггеров
    public void indexTrigger(OrlangTriggerInfo trigger) {
    	Indexer.updateTriggersIndex(trigger, this);
    }
    
    // Возвращает полный путь к хранилищу индексов 
    public String getIndexDirectory() {
		return Indexer.getIndexDirectory();
	}
    
    // Считывает информацию о последнем процессе индексации
    public String getLastIndexingInfo() {
    	return SearchUtil.getLastIndexingInfo();
    }
    
    // Записывает информацию о последнем процессе индексации
    public void setLastIndexingInfo(String lastIndexingInfo) {
    	SearchUtil.setLastIndexingInfo(lastIndexingInfo);
    }
    
    public String getHighlightedFragments(String objUid, long attrId, long langId, String pattern) throws KrnException {
    	byte[] contents;
    	if (attrId == -1) {
	    	contents = getMethodExpression(objUid);
    	} else {
    		KrnObject obj = getObjectByUid(objUid, 0);
	    	contents = getBlobs(obj.id, attrId, langId, 0)[0];
    	}
    	try {
			return Indexer.getHighlightedFragments(pattern, contents, langId);
		} catch (ParseException e) {
			log.error(e, e);
		} catch (IOException e) {
			log.error(e, e);
		}
		return null;
    }
            
	public List<KrnSearchResult> getConfigsByConditions(String txt, String uid) throws KrnException {
		List<KrnSearchResult> res = new ArrayList<KrnSearchResult>();
		KrnClass cls = null;
	//try {
		cls = getClassByName("UI");	
		KrnAttribute attr = getAttributeByName(cls, "config");
		KrnObject[] objList = getClassObjects(cls, new long[0], 0);
		try {
			for (int i=0; i<objList.length; i++) {
				byte[] config = getBlob(objList[i].id, attr.id, 0, 0, 0);
				String blob = new String(config,"UTF-8");
				if ((uid.length()>0  &&  blob.indexOf(uid) > -1) || 
					(txt.length()>0  &&  blob.indexOf(txt) > -1) )	{
					String[] title = getStrings(objList[i].id, getAttributeByName(cls, "title").id, 0, false, 0);
					KrnSearchResult ksr = new KrnSearchResult(cls.id, objList[i].uid, title[0]);
					res.add(ksr); 	
				}
			}
		} catch (Exception e) {
    		log.error(e, e);
    	}
 	//} catch (DriverException e) {
    //   	log.error(e, e);
    //   	throw new KrnException(e.getErrorCode(), e.getMessage());
	//}
 	   	return res;
	}

	public boolean stringSearch(String content, String pattern, long langId) {
		try {
			return Indexer.stringSearch(content, pattern, langId);
		} catch (IOException e) {
			log.error(e, e);
		} catch (ParseException e) {
			log.error(e, e);
		}
		return false;
	}
	
	public ModelChanges getModelChanges(long changeId) throws KrnException {
		try {
			return drv.getModelChanges(changeId);
		} catch (DriverException e) {
			log.error(e.getMessage(), e);
			throw new KrnException(0, e.getMessage());
		}
	}

	public DataChanges getDataChanges(long classId, long changeId, AttrRequest req) throws KrnException {
		try {
			return drv.getDataChanges(classId, changeId, req,this);
		} catch (DriverException e) {
			log.error(e.getMessage(), e);
			throw new KrnException(0, e.getMessage());
		}
	}

    // когда PolicyNode переренесётся на сервер, данные класссы использовать не нужно будет напрямую можно будет вытаскивать данные из политики
    public PasswordPolicy getPolicy() {
        getPolicyClass();
        load();
        return policyWrapper;
    }

    public void getPolicyClass() {
        if (policyCls == null) {
            try {
                policyCls = getClassByName(policyName);
                if (policyCls != null) {
                    KrnObject[] objs = getClassObjects(policyCls, new long[0], 0);
                    krnObj = (objs != null && objs.length > 0) ? objs[0] : createObject(policyCls, 0);
                }
            } catch (KrnException e) {
            	log.error("Не найден класс \"" + policyName + "\"");
            }
        }
    }

    protected void load() {
        if (policyCls != null) {
            long maxValidPeriod;
            long minPDLength = 6;
            long minLoginLength = 3;
            long minPDLengthAdmin;
            long numPassDubl;
            long numPassDublAdmin;
            boolean useNumbers;
            boolean useNotAllNumbers = false;
            boolean useSymbols;
            boolean useRegisterSymbols;
            boolean useSpecialSymbol;
            boolean banNames;
            boolean banFamilies;
            boolean banPhone;
            boolean banWord;
            long maxPeriodPD = 90;
            long minPeriodPD;
            long numberFailedLogin;
            long timeLock;
            boolean banLoginInPD;
            long maxLengthPass = 30;
            long maxLengthLogin = 50;
            boolean changeFirstPass = true;
            long maxPeriodFirstPass = 5;
            boolean banRepeatChar = true;
            boolean banRepAnyWhereMoreTwoChar = false;
            boolean banKeyboard = false;
            boolean activateLiabilitySign = false;
            long liabilitySignPeriod = 365;
            boolean activateECPExpiryNotif = false;
            long ecpExpiryNotifPeriod = 30;
            boolean activateTempRegNotif = false;
            long tempRegNotifPeriod = 0;
            boolean checkClientIp = false;
            boolean useECP = false;
            boolean banUseOwnIdentificationData = false;
            
            try {
                AttrRequestBuilder arb = new AttrRequestBuilder(policyCls, this).add(ATTR_MAX_PERIOD_PASSWORD)
                        .add(ATTR_MIN_LOGIN_LENGTH).add(ATTR_MIN_PASSWORD_LENGTH).add(ATTR_MAX_VALID_PERIOD)
                        .add(ATTR_MIN_PASSWORD_LENGTH_ADMIN).add(ATTR_NUMBER_PASSWORD_DUBLICATE)
                        .add(ATTR_NUMBER_PASSWORD_DUBLICATE_ADMIN).add(ATTR_USE_NUMBERS).add(ATTR_USE_NOTALLNUMBERS).add(ATTR_USE_SYMBOLS)
                        .add(ATTR_USE_REGISTER_SYMBOLS).add(ATTR_USE_SPECIAL_SYMBOL).add(ATTR_BAN_NAMES).add(ATTR_BAN_FAMILIES)
                        .add(ATTR_BAN_PHONE).add(ATTR_BAN_WORD).add(ATTR_BAN_KEYBOARD).add(ATTR_MIN_PERIOD_PASSWORD).add(ATTR_BAN_LOGIN_IN_PASSWORD)
                        .add(ATTR_MAX_LENGTH_PASS).add(ATTR_MAX_LENGTH_LOGIN)
                        .add(ATTR_CHANGE_FIRST_PASS).add(ATTR_MAX_PERIOD_FIRST_PASS).add(ATTR_BAN_REPEAT_CHAR).add(ATTR_BAN_REPEAT_ANYWHERE_MORE_2_NOREGISTER_CHAR)
                        .add(ATTR_NUMBER_FAILED_LOGIN).add(ATTR_TIME_LOCK)
                        .add(ATTR_ACTIVATE_LIABILITY_SIGN).add(ATTR_LIABILITY_SIGN_PERIOD)
                        .add(ATTR_ACTIVATE_ECP_EXPIRY_NOTIF).add(ATTR_ECP_EXPIRY_NOTIF_PERIOD)
                        .add(ATTR_ACTIVATE_TEMP_REG_NOTIF).add(ATTR_TEMP_REG_NOTIF_PERIOD).add(ATTR_CHECK_CLIENT_IP);
                if (isRNDB() || hasUseECP()) {
                	arb.add(ATTR_USE_ECP);
                }
                arb.add(ATTR_BAN_USE_OWN_IDENTIFICATION_DATA);


                long[] objIds = { krnObj.id };
                Object[] row = getObjects(objIds, arb.build(), 0).rows.get(0);
                maxPeriodPD = arb.getLongValue(ATTR_MAX_PERIOD_PASSWORD, row, 90);
                minLoginLength = arb.getLongValue(ATTR_MIN_LOGIN_LENGTH, row, 3);
                minPDLength = arb.getLongValue(ATTR_MIN_PASSWORD_LENGTH, row, 6);
                maxValidPeriod = arb.getLongValue(ATTR_MAX_VALID_PERIOD, row, 30);
                minPDLengthAdmin = arb.getLongValue(ATTR_MIN_PASSWORD_LENGTH_ADMIN, row, 12);
                numPassDubl = arb.getLongValue(ATTR_NUMBER_PASSWORD_DUBLICATE, row, 3);
                numPassDublAdmin = arb.getLongValue(ATTR_NUMBER_PASSWORD_DUBLICATE_ADMIN, row, 20);
                useNumbers = arb.getBooleanValue(ATTR_USE_NUMBERS, row, true);
                useNotAllNumbers = arb.getBooleanValue(ATTR_USE_NOTALLNUMBERS, row, false);
                useSymbols = arb.getBooleanValue(ATTR_USE_SYMBOLS, row, true);
                useRegisterSymbols = arb.getBooleanValue(ATTR_USE_REGISTER_SYMBOLS, row, false);
                useSpecialSymbol = arb.getBooleanValue(ATTR_USE_SPECIAL_SYMBOL, row, false);
                banNames = arb.getBooleanValue(ATTR_BAN_NAMES, row, true);
                banFamilies = arb.getBooleanValue(ATTR_BAN_FAMILIES, row, true);
                banPhone = arb.getBooleanValue(ATTR_BAN_PHONE, row, true);
                banWord = arb.getBooleanValue(ATTR_BAN_WORD, row, true);
                banKeyboard = arb.getBooleanValue(ATTR_BAN_KEYBOARD, row, false);
                minPeriodPD = arb.getLongValue(ATTR_MIN_PERIOD_PASSWORD, row, 2);
                banLoginInPD = arb.getBooleanValue(ATTR_BAN_LOGIN_IN_PASSWORD, row, true);
                timeLock = arb.getLongValue(Constants.ATTR_TIME_LOCK, row, 0);
                numberFailedLogin = arb.getLongValue(Constants.ATTR_NUMBER_FAILED_LOGIN, row, 0);
                maxLengthPass = arb.getLongValue(ATTR_MAX_LENGTH_PASS, row, 30);
                maxLengthLogin = arb.getLongValue(ATTR_MAX_LENGTH_LOGIN, row, 50);
                changeFirstPass = arb.getBooleanValue(ATTR_CHANGE_FIRST_PASS, row, true);
                maxPeriodFirstPass = arb.getLongValue(ATTR_MAX_PERIOD_FIRST_PASS, row, 5);
                banRepeatChar = arb.getBooleanValue(ATTR_BAN_REPEAT_CHAR, row, true);
                banRepAnyWhereMoreTwoChar = arb.getBooleanValue(ATTR_BAN_REPEAT_ANYWHERE_MORE_2_NOREGISTER_CHAR, row, false);
                activateLiabilitySign = arb.getBooleanValue(ATTR_ACTIVATE_LIABILITY_SIGN, row, false);
                liabilitySignPeriod = arb.getLongValue(ATTR_LIABILITY_SIGN_PERIOD, row, 365);
                activateECPExpiryNotif = arb.getBooleanValue(ATTR_ACTIVATE_ECP_EXPIRY_NOTIF, row, false);
                ecpExpiryNotifPeriod = arb.getLongValue(ATTR_ECP_EXPIRY_NOTIF_PERIOD, row, 30);
                activateTempRegNotif = arb.getBooleanValue(ATTR_ACTIVATE_TEMP_REG_NOTIF, row, false);
                tempRegNotifPeriod = arb.getLongValue(ATTR_TEMP_REG_NOTIF_PERIOD, row, 0);
                checkClientIp = arb.getBooleanValue(ATTR_CHECK_CLIENT_IP, row, false);
                if (isRNDB() || hasUseECP()) {
                	useECP = arb.getBooleanValue(ATTR_USE_ECP, row, false);
                }
                banUseOwnIdentificationData = arb.getBooleanValue(ATTR_BAN_USE_OWN_IDENTIFICATION_DATA, row, false);
            } catch (Exception e) {
                kz.tamur.rt.Utils.outErrorCreateAttrPolicy();
                maxValidPeriod = 30;
                minPDLengthAdmin = 12;
                numPassDubl = 3;
                numPassDublAdmin = 20;
                useNumbers = true;
                useNotAllNumbers = false;
                useSymbols = true;
                useRegisterSymbols = false;
                useSpecialSymbol = false;
                banNames = true;
                banFamilies = true;
                banPhone = true;
                banWord = true;
                banKeyboard = false;
                minPeriodPD = 2;
                banLoginInPD = true;
                maxLengthPass = 30;
                maxLengthLogin = 50;
                changeFirstPass = true;
                maxPeriodFirstPass = 5;
                banRepeatChar = true;
                banRepAnyWhereMoreTwoChar = false;
                timeLock = 0;
                numberFailedLogin = 0;
                minPDLength = 6;
                minLoginLength = 3;
                maxPeriodPD = 90;
                activateLiabilitySign = false;
                liabilitySignPeriod = 365;
                activateECPExpiryNotif = false;
                ecpExpiryNotifPeriod = 30;
                activateTempRegNotif = false;
                tempRegNotifPeriod = 0;
                checkClientIp = false;
            }
            policyWrapper = new PasswordPolicy(maxValidPeriod, minPDLength, minLoginLength, minPDLengthAdmin, 
            		numPassDubl, numPassDublAdmin, useNumbers, useNotAllNumbers, useSymbols, useRegisterSymbols, useSpecialSymbol,
            		banNames, banFamilies, banPhone, banWord, maxPeriodPD, minPeriodPD, numberFailedLogin, timeLock,
            		banLoginInPD, maxLengthPass, maxLengthLogin, changeFirstPass, maxPeriodFirstPass,
            		banRepeatChar, banRepAnyWhereMoreTwoChar, banKeyboard, activateLiabilitySign, liabilitySignPeriod,
            		activateECPExpiryNotif, ecpExpiryNotifPeriod, activateTempRegNotif, tempRegNotifPeriod, checkClientIp, useECP, banUseOwnIdentificationData);
        }
    }
    
    public void sendMessage(UserSession from, Set<KrnObject> users, String text) {
    	UserSessionValue fromValue = from != null ? createValueObject(from) : null;
		synchronized (allUserSessionCache) {
			for (UUID key : allUserSessionCache.keySet()) {
				ServerUserSession us = getUserSession(key);
		    	if (us != null) {
		    		if (users == null || users.contains(us.getUserObj()))
		    			sendNoteClustered(us, new MessageNote(new java.util.Date(), fromValue, text));
		    	}
			}
		}
    }

    public void sendNotification(UserSession from, Set<KrnObject> users, Object data, int type, String title) {
    	UserSessionValue fromValue = from != null ? createValueObject(from) : null;
		synchronized (allUserSessionCache) {
			for (UUID key : allUserSessionCache.keySet()) {
				ServerUserSession us = getUserSession(key);
		    	if (us != null) {
		    		if (users == null || users.contains(us.getUserObj()))
		    			sendNoteClustered(us, new SystemNote(new java.util.Date(), fromValue, data, type, title));
		    	}
			}
		}
    }
    
    public void deleteNotification(KrnObject user) {
    	try {
    		KrnClass userCls = getClassByName("User");
	        KrnAttribute notificationsAttr = getAttributeByName(userCls, "notifications");
	        KrnObject[] notifications = getObjects(user.id, notificationsAttr.id, new long[0], 0);
	        
	        for (KrnObject note : notifications) {
	        	List<Object> list = new ArrayList<>();
	        	list.add(note);
	        	deleteObject(note, 0);
	        }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void deleteNotification(KrnObject user, String uid, String cuid) {
    	try {
    		KrnClass userCls = getClassByName("User");
	        KrnAttribute notificationsAttr = getAttributeByName(userCls, "notifications");
	        KrnObject[] notifications = getObjects(user.id, notificationsAttr.id, new long[0], 0);
	        
	        for (KrnObject note : notifications) {
	        	Object obj = note.getAttr("Notification.uid");
	        	String noteuid = obj != null ? obj.toString() : "";
	        	obj = note.getAttr("Notification.cuid");
	        	String notecuid = obj != null ? obj.toString() : "";
	        	if (noteuid.equals(uid) && (cuid == null || cuid.equals(notecuid))) {
	        		List<Object> list = new ArrayList<>();
	        		list.add(note);
	        		deleteValue(note.id, notificationsAttr.id, list, 0);
	        		deleteObject(note, 0);
	        	}
	        }
	        ServerUserSession us = findUserSession(user.id);
	    	java.util.Date datetime = new java.util.Date();
    		sendNoteClustered(us, new UpdateNotificationsNote(datetime, null));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void deleteNotificationByUID(KrnObject user, String uid) {
    	try {
    		KrnClass userCls = getClassByName("User");
	        KrnAttribute notificationsAttr = getAttributeByName(userCls, "notifications");
	        KrnObject[] notifications = getObjects(user.id, notificationsAttr.id, new long[0], 0);
	        
	    	JsonArray idArr = new JsonArray();
	        for (KrnObject note : notifications) {
	        	if (note.uid.equals(uid)) {
	        		idArr.add(note.id);
	        		List<Object> list = new ArrayList<>();
	        		list.add(note);
	        		deleteValue(note.id, notificationsAttr.id, list, 0);
	        		deleteObject(note, 0);
	        	}
	        }
	        ServerUserSession us = findUserSession(user.id);
	    	java.util.Date datetime = new java.util.Date();
	    	sendNoteClustered(us, new DeleteNotificationsNote(datetime, null, idArr));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

    public boolean sendMailMessage(String host, String port,String user, String pd,
    		String[] froms,String[] tos,String theme,String text,String mime,String charSet,boolean ssl)throws KrnException{
    	boolean res=false;
        res=com.cifs.or2.server.exchange.ExchangeUtils.sendMailMessage(host, port,user,pd,
        		froms,tos,theme,text,mime,charSet, ssl);
    	return res;
    }

    public boolean sendMailMessage(KrnObject obj,String host, String port,
			String user, String pd)throws KrnException{
    	boolean res=false;
        res=com.cifs.or2.server.exchange.ExchangeUtils.sendMailMessage(obj,host, port,user,pd,this);
    	return res;
    }
    
    public boolean isValidEmailAddress(String email){
        boolean res=false;
        res=com.cifs.or2.server.exchange.ExchangeUtils.isValidEmailAddress(email);
        return res;
    }
    
    public List<TriggerInfo> getTriggers(KrnClass cls) {
    	return drv.getTriggers(cls);
    }
    
    public String createTrigger(String triggerContext) {
    	return drv.createTrigger(triggerContext);
    }
    
    public String removeTrigger(String triggerName) {
    	return drv.removeTrigger(triggerName);
    }
    
    private boolean checkLogCondition(SystemEvent event, String name, String ip, boolean isAdmin) {
    	return checkLogCondition(event, name, ip, isAdmin, null, -1);
    }
    
    private boolean checkLogCondition(SystemEvent event, String name, String ip, boolean isAdmin, KrnObject obj, long attrId) {
    	List<ProtocolRule> rules = db.getProtocolRulesByEvent(event.getCode());
    	
    	for (ProtocolRule rule : rules) {
    		if (!rule.isBlocked()) {
	    		ASTStart expr = rule.getFormula();
	    		if (expr != null) {
	                Map<String, Object> vars = new HashMap<String, Object>();
	                vars.put("TIME", new Date());
	                vars.put("IP", ip);
	                vars.put("USER_NAME", name);
	                vars.put("USER_IS_ADMIN", isAdmin);
	                vars.put("OBJ", obj);
	                vars.put("ATTR_ID", attrId);
	                
	                boolean res = false;
	                try {
	                    res = getSrvOrLang().evaluate(expr, vars, null, false, new Stack<String>(), null);
	                } catch (Exception e) {
	                    log.error(e, e);
	                }
	                if (res) {
	                	Object value = vars.get("RETURN");
	                	if (Boolean.TRUE.equals(value) || new Integer(1).equals(value))
	                		return !rule.isDeny();
	                }
	                    
	    		} else {
	    			return !rule.isDeny();
	    		}
    		}
    	}
    	
    	rules = db.getProtocolRulesByEventType(event.getTypeCode());
    	
    	for (ProtocolRule rule : rules) {
    		if (!rule.isBlocked() && rule.getEvent() < 1) {
	    		ASTStart expr = rule.getFormula();
	    		if (expr != null) {
	                Map<String, Object> vars = new HashMap<String, Object>();
	                vars.put("TIME", new Date());
	                vars.put("IP", ip);
	                vars.put("USER_NAME", name);
	                
	                boolean res = false;
	                try {
	                    res = getSrvOrLang().evaluate(expr, vars, null, false, new Stack<String>(), null);
	                } catch (Exception e) {
	                	log.error(e, e);
	                }
	                if (res) {
	                	Object value = vars.get("RETURN");
	                	if (Boolean.TRUE.equals(value) || new Integer(1).equals(value))
	                		return !rule.isDeny();
	                }
	                    
	    		} else {
	    			return !rule.isDeny();
	    		}
    		}
    	}

    	rules = db.getProtocolRulesByEvent(SystemEvent.ALL_EVENTS);
    	
    	for (ProtocolRule rule : rules) {
    		if (!rule.isBlocked() && rule.getEventType() < 1) {
	    		ASTStart expr = rule.getFormula();
	    		if (expr != null) {
	                Map<String, Object> vars = new HashMap<String, Object>();
	                vars.put("TIME", new Date());
	                vars.put("IP", ip);
	                vars.put("USER_NAME", name);
	                
	                boolean res = false;
	                try {
	                    res = getSrvOrLang().evaluate(expr, vars, null, false, new Stack<String>(), null);
	                } catch (Exception e) {
	                	log.error(e, e);
	                }
	                if (res) {
	                	Object value = vars.get("RETURN");
	                	if (Boolean.TRUE.equals(value) || new Integer(1).equals(value))
	                		return !rule.isDeny();
	                }
	                    
	    		} else {
	    			return !rule.isDeny();
	    		}
    		}
    	}

    	return true;
    }
    
    public List<Long> getUserSubjects(SystemAction action, long userId) throws KrnException {
		OrganisationComponent orgComp = getOrgComp();
		UserSrv user = orgComp.findActorById(userId,this);

		if ("sys_admin".equals(user.getName()) || "sys".equals(user.getName())) {
			return null;
		}
    	
    	List<SystemRight> rights = db.getSystemRightsByAction(action.getCode());

    	List<Long> ids = new ArrayList<Long>();
    	ids.add(userId);

		KrnObject[] roles = user.getParents();
		if (roles != null) {
			for (KrnObject role : roles) {
				ids.add(role.id);
			}
		}

		List<Long> result = new ArrayList<Long>();
		List<Long> denyResult = new ArrayList<Long>();
		
    	for (SystemRight right : rights) {
    		// Правило незаблокировано и для всех пользователей, либо для любой из ролей текущего пользователя
    		if (!right.isBlocked() && right.isForUser(ids)) {
    			
    			boolean tmpRes = false;
    			
	    		ASTStart expr = right.getFormula();
	    		if (expr != null) {
	                Map<String, Object> vars = new HashMap<String, Object>();
	                vars.put("IP", getIpAddress());
	                vars.put("TIME", new Date());
	                vars.put("USER", getUser());
	                vars.put("USER_NAME", user.getUserName());
	                
	                boolean res = false;
	                try {
	                    res = getSrvOrLang().evaluate(expr, vars, null, false, new Stack<String>(), null);
	                } catch (Exception e) {
	                    log.error(e, e);
	                }
	                if (res) {
	                	Object value = vars.get("RETURN");
	                	if (Boolean.TRUE.equals(value) || new Integer(1).equals(value))
	                		tmpRes = true;
	                	else 
	                		tmpRes = false;
	                }
	    		} else
	    			tmpRes = true;
	    		
	    		List<KrnObject> subjects = right.getSubjects();
    			processSubject(subjects, tmpRes, right.isDenying() ? denyResult : result);
    		}
    	}
    	result.removeAll(denyResult);
    	
    	return result;
    }
    
    private void processSubject(List<KrnObject> subjects, boolean tmpRes, List<Long> result) throws KrnException {
    	if (subjects.size() > 0) {

			List<Long> subjIds = new ArrayList<Long>();
			KrnAttribute attr = null;

			for (KrnObject subject : subjects) {
				KrnClass cls = getClassById(subject.classId);
				if (tmpRes) {
					if (!result.contains(subject.id) && !cls.name.equals("ProcessDefFolder") && !cls.name.equals("ProcessDefRoot")) 
						result.add(subject.id);
				} else
					result.remove(Long.valueOf(subject.id));

				
				if (cls.name.equals("HiperFolder")) {
					attr = getAttributeByName(cls, "hipers");
					subjIds.add(subject.id);
				}
				else if (cls.name.equals("ProcessDefFolder") || cls.name.equals("ProcessDefRoot") || cls.name.equals("UserFolder")) {
					attr = getAttributeByName(cls, "children");
					subjIds.add(subject.id);
				}
			}	
			
			if (attr != null) {
				long[] ids = new long[subjIds.size()];
				int i = 0;
				for (long id : subjIds)
					ids[i++] = id;
				
				List<KrnObject> children = new ArrayList<KrnObject>();
	        	SortedSet<Value> vals = getValues(ids, attr.id, 0, 0);
	        	for (Value val : vals) {
					if (val.value instanceof KrnObject) {
						children.add((KrnObject)val.value);
					}
	        	}
    			processSubject(children, tmpRes, result);
			}
    	}
    }
    
    public boolean checkUserHasRight(SystemAction action, long userId, KrnObject subject) {
    	return checkUserHasRight(action, userId, subject, getUserSession() != null ? getUserSession().getIp() : null);
    }
    
    public boolean checkUserHasRight(SystemAction action, long userId, KrnObject subject, String ip) {
		OrganisationComponent orgComp = getOrgComp();
		UserSrv user = orgComp.findActorById(userId,this);

		if ("sys_admin".equals(user.getName()) || "sys".equals(user.getName())) {
			return true;
		}
    	
    	List<SystemRight> rights = db.getSystemRightsByAction(action.getCode());

    	List<Long> ids = new ArrayList<Long>();
    	ids.add(userId);
    	

		KrnObject[] roles = user.getParents();
		if (roles != null) {
			for (KrnObject role : roles) {
				ids.add(role.id);
			}
		}

		boolean result = false;
		
    	for (SystemRight right : rights) {
    		// Правило незаблокировано и для всех пользователей, либо для любой из ролей текущего пользователя
    		if (!right.isBlocked() && right.isForUser(ids) 
    				&& (subject == null || right.isForSubject(subject.id))) {
    			
	    		ASTStart expr = right.getFormula();
	    		if (expr != null) {
	                Map<String, Object> vars = new HashMap<String, Object>();
	                vars.put("IP", ip);
	                vars.put("TIME", new Date());
	                //vars.put("USER", getUser());
	                vars.put("USER_NAME", user.getUserName());
	                vars.put("OBJ", subject);
	                
	                boolean res = false;
	                try {
	                    res = getSrvOrLang().evaluate(expr, vars, null, false, new Stack<String>(), null);
	                } catch (Exception e) {
	                    log.error(e, e);
	                }
	                if (res) {
	                	Object value = vars.get("RETURN");
	                	if (Boolean.TRUE.equals(value) || new Integer(1).equals(value))
	                		result = true;
	                	else 
	                		result = false;
	                }
	    		} else
	    			result = true;
	    		
	    		if (right.isDenying())
	    			result = !result;
	    		
	    		if (result) return true;
    		}
    	}
    	return result;
    }
    
    public String[][] getColumnsInfo(String tableName){ //TODO: Tedit
    	return drv.getColumnsInfo(tableName);
	}
	
	public boolean columnMove(int[] cols, String tableName){ //TODO: Tedit
		return drv.columnMove(cols, tableName);
	}
	
    // Записывать изменения значений в репликационный файл?
	public static boolean isDataLog() {
		return Driver2.isDataLog();
	}
	
	public static void setDataLog(boolean logData) {
		Driver2.setDataLog(logData);
	}
	
    public String generateWS(byte[] wsdlFileInBytes, String fileName, String packageName, String methodName) {
        WSHelper wsHelper = new WSHelper(this);
        String statusMessage = wsHelper.generateWebServiceClasses(wsdlFileInBytes, fileName, packageName, methodName);
        return statusMessage;
    }
    
    public byte[] generateXML(String serviceName, int type) {
        WSHelper wsHelper = new WSHelper(this);
        return wsHelper.generateXML(serviceName, type);
    }

    public List<Long> findForeignProcess(long proceeDefId,long cutObjId) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp != null){
        	UserSession us = getUserSession();
        	return db.findForeignProcess(proceeDefId, cutObjId,us,(Driver2)drv);
        }else return null;
    }
    
    public List<Long> findProcessByUiType(String uiType) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp != null){
        	UserSession us = getUserSession();
             return db.findProcessByUiType(uiType,us,(Driver2)drv);
        }else return null;
    }

    public long getSubFlowTask(long superFlowTaskId) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp != null){
             return db.getSubFlowTask(superFlowTaskId,(Driver2)drv);
        }
        return -1;
    }

    public List<String> getFlowsToRemove(long pdId,long roleId) throws KrnException {
            return db.getFlowsToRemove(pdId, roleId,(Driver2)drv);
    }

    public long[] getFlowsByEvent(String event,String boxId) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp != null){
             return db.getFlowsByEvent(event, boxId,(Driver2)drv);
        }else return null;
    }
    public long findFlowByCorelId(String corelId) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp != null){
             return db.findFlowByCorelId(corelId, (Driver2)drv);
        }else return -1;
    }
    public List<String> getFlowsByControlDate(boolean onlyExist,Map<Long,String> pdimap) throws KrnException {
    	ExecutionComponent exeComp = getExeComp();
        if(exeComp != null){
             return db.getFlowsByControlDate((Driver2)drv,onlyExist,pdimap);
        }else return null;
    }

    /*
     *	Методы для автоматического создания datasource в JBoss - думаю будут не нужны при переходе на безсерверную архитектуру
     * 
     */
/*
    public boolean checkIfDatasourceExists(String poolName, String adminHost, int adminPort) throws IOException {
    	  ModelNode request = new ModelNode();
    	  request.get(ClientConstants.OP).set("read-resource");
    	  request.get("recursive").set(false);
    	  request.get(ClientConstants.OP_ADDR).add("subsystem", "datasources");
    	  ModelControllerClient client = ModelControllerClient.Factory.create(
    	          InetAddress.getByName(adminHost), adminPort);
    	  ModelNode responce = client.execute(new OperationBuilder(request).build());
    	  ModelNode datasources = responce.get(ClientConstants.RESULT).get("data-source");
    	  if (datasources.isDefined()) {
    	      for (ModelNode dataSource : datasources.asList()) {
    	          String dataSourceName = dataSource.asProperty().getName();
    	          if (dataSourceName.equals(poolName)) {
    	              return true;
    	          }
    	      }
    	  }
    	  return false;
    }
    
    public void createDatasource(String dsJndiName, String poolName, String driverName, String connectionUrl,
			String transactionIsolation, boolean prefill, boolean useStrictMin, int minPoolSize, int maxPoolSize, String flushStrategy,
			boolean sharePreparedStatements, long cacheSize,
			String userName, String userPd, String adminHost, int adminPort) throws IOException {
	  
		ModelNode request = new ModelNode();
		request.get(ClientConstants.OP).set(ClientConstants.ADD);
		request.get(ClientConstants.OP_ADDR).add("subsystem", "datasources");
		request.get(ClientConstants.OP_ADDR).add("data-source", poolName);
	
		request.get("jndi-name").set(dsJndiName);
	
		request.get("use-java-context").set(true);
		request.get("use-ccm").set(true);
		request.get("jta").set(false);
		//request.get("enabled").set(true);
		request.get("transaction-isolation").set(transactionIsolation);
	
		request.get("pool-prefill").set(prefill);
		request.get("pool-use-strict-min").set(useStrictMin);
		request.get("flush-strategy").set(flushStrategy);
		request.get("min-pool-size").set(minPoolSize);
		request.get("max-pool-size").set(maxPoolSize);
	
		request.get("share-prepared-statements").set(sharePreparedStatements);
		request.get("prepared-statements-cache-size").set(cacheSize);
	
		request.get("connection-url").set(connectionUrl);
		request.get("driver-name").set(driverName);
		request.get("user-name").set(userName);
		request.get("password").set(userPd);
		
		ModelControllerClient client = ModelControllerClient.Factory.create(
				InetAddress.getByName(adminHost), adminPort);
		ModelNode resp = client.execute(new OperationBuilder(request).build());
		log.info(resp.toString());
		client.close();
		
		request = new ModelNode();
		request.get(ClientConstants.OP).set("enable");
		request.get(ClientConstants.OP_ADDR).add("subsystem", "datasources");
		request.get(ClientConstants.OP_ADDR).add("data-source", poolName);
		client = ModelControllerClient.Factory.create(
				InetAddress.getByName(adminHost), adminPort);
		resp = client.execute(new OperationBuilder(request).build());
		log.info(resp.toString());
		client.close();
	}

    public void removeDatasource(String poolName, String adminHost, int adminPort) throws IOException {
	  
		ModelNode request = new ModelNode();
		request.get(ClientConstants.OP).set("disable");
		request.get(ClientConstants.OP_ADDR).add("subsystem", "datasources");
		request.get(ClientConstants.OP_ADDR).add("data-source", poolName);
		ModelControllerClient client = ModelControllerClient.Factory.create(
				InetAddress.getByName(adminHost), adminPort);
		ModelNode resp = client.execute(new OperationBuilder(request).build());
		log.info(resp.toString());
		client.close();
	
		request = new ModelNode();
		request.get(ClientConstants.OP).set("remove");
		request.get(ClientConstants.OP_ADDR).add("subsystem", "datasources");
		request.get(ClientConstants.OP_ADDR).add("data-source", poolName);
		client = ModelControllerClient.Factory.create(
				InetAddress.getByName(adminHost), adminPort);
		resp = client.execute(new OperationBuilder(request).build());
		log.info(resp.toString());
		client.close();
	}
*/
    private void putUserSession(ServerUserSession us) {
    	if (us.callbacks()) {
    		userSessionCache.put(us.getId(), us);
    		synchronized (myUserSessionCache) {
        		myUserSessionCache.put(us.getId(), us);
			}
    		loggedInUsers.add(us);
    	} else
    		localUserSessionCache.put(us.getId(), us);
    }

    private void removeUserSession(UserSession us) {
    	cleanTempFiles();
		us.setAlive(false);
    	if (us.callbacks()) {
    		userSessionCache.remove(us.getId());
    		synchronized (myUserSessionCache) {
    			myUserSessionCache.remove(us.getId());
    		}
    		loggedOutUsers.add((ServerUserSession) us);
    	} else {
    		localUserSessionCache.remove(us.getId());
    	}
    }
    
    private void cleanTempFiles() {
    	if (user != null)
    		user.cleanTempFiles();
    }
    
	public void deleteFileOnExit(File f) {
    	if (user != null)
    		user.deleteFileOnExit(f);
	}
    
	public Log getLog(Class<?> cls) {
		ServerUserSession us = (ServerUserSession)getUserSession();
		StringBuilder sb = new StringBuilder(dsName).append(".").append(us.getLogUserName()).append(".");
		if (UserSession.SERVER_ID != null)
			sb.append(UserSession.SERVER_ID).append(".");
		sb.append(cls.getName());
		
    	return LogFactory.getLog(sb.toString());
	}
	
    private Log getLog(String name) {
        String key = dsName + "." + name;
        Log log = null;
        synchronized (userLogs) {
            log = userLogs.get(key);
            if (log == null) {
                log = LogFactory.getLog(key);
                userLogs.put(key, log);
            }
        }
        return log;
    }

	public List<Object> downloadFile(String source) {
    	List<Object> result = new ArrayList<Object>();
    	File sourceFile = kz.tamur.util.Funcs.getCanonicalFile(source);
    	if (sourceFile.isFile() && sourceFile.exists()) {
    		try {
    			byte[] fileInBytes = FileUtils.readFileToByteArray(sourceFile);
    			result.add(1);
    			result.add(fileInBytes);
			} catch (IOException e) {
				log.error(e, e);
    			result.add(3);
			}
    	} else {
			result.add(2);
    	}
    	return result;
    }
    
    public KrnAttribute setAttrTriggerEventExpression(String expr, long attrId, int mode, boolean isZeroTransaction) throws KrnException {
    	KrnAttribute attr = null;
    	try {
    		int action = drv.setAttrTriggerEventExpression(expr, attrId, mode, isZeroTransaction);
    		attr = getAttributeById(attrId);
    		if (action > -1) {
    			if (action == 2) {
        			Indexer.removeTriggersIndex(attr, mode);
    			} else {
    				Indexer.updateTriggersIndex(expr, attr, mode);
    			}
	    		String triggerName = mode == 0 ? "Перед изменением значения атрибута" : mode == 1 ? "После изменения значения атрибута" : mode == 2 ? "Перед удалением значения атрибута" : "После удаления значения атрибута";
	    		KrnClass cls = getClassById(attr.classId);
	            writeLogRecord(SystemEvent.EVENT_ATTR_TRIGGER_CHANGED, "Триггер: " + triggerName + ", класс: " + cls.name + ", атрибут: " + attr.name,cls.id,attr.id);
    		}
		} catch (SQLException e) {
			log.error(e, e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		} catch (DriverException e) {
			log.error(e, e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
        	log.error(e, e);
            throw new KrnException(0, e.getMessage());
		}
    	return attr;
    }
    
    public KrnClass setClsTriggerEventExpression(String expr, long clsId, int mode, boolean isZeroTransaction) throws KrnException {
    	KrnClass cls = null;
    	try {
    		int action = drv.setClsTriggerEventExpression(expr, clsId, mode, isZeroTransaction);
    		cls = getClassById(clsId);
    		if (action > -1) {
    			if (action == 2) {
        			Indexer.removeTriggersIndex(cls, mode);
    			} else {
    				Indexer.updateTriggersIndex(expr, cls, mode);
    			}
	    		String triggerName = mode == 0 ? "Перед созданием объекта" : mode == 1 ? "После создания объекта" : mode == 2 ? "Перед удалением объекта" : "После удаления объекта";
	            writeLogRecord(SystemEvent.EVENT_CLASS_TRIGGER_CHANGED, "Триггер: " + triggerName + ", класс: " + cls.name,cls.id,-1);
    		}
		} catch (SQLException e) {
			log.error(e, e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
		} catch (DriverException e) {
			log.error(e, e);
			throw new KrnException(e.getErrorCode(), e.getMessage());
        } catch (Exception e) {
        	log.error(e, e);
            throw new KrnException(0, e.getMessage());
		}
    	return cls;
    }
    
    public boolean initServerTasks() {
    	return serverTasks.get(dsName).init(this);
    }
    
    public void setActivateScheduler(boolean activate) {
    	serverTasks.get(dsName).setActivateScheduler(activate, this);
    }

    public String createProcedure(String name,List params,String body) {
    		return  drv.createProcedure(name, params, body);
    }
    
    public List execProcedure(String name) throws DriverException {
		return  drv.execProcedure(name);
    }
    
    public List execProcedure(String name,List<Object> vals) throws DriverException {
		return  drv.execProcedure(name, vals);
    }
    
    public List execProcedure(String name,List<Object> vals,List<String> types_in,List<String> types_out) throws DriverException {
		return  drv.execProcedure(name, vals,types_in,types_out);
    }
    
    public List<String> getListProcedure(String type) {
			return  drv.getListProcedure(type);
    }
    
    public byte[] getProcedureContent(String name,String type) {
			return  drv.getProcedureContent(name,type);
    }
    
    public boolean deleteProcedure(String name,String type) {
			return  drv.deleteProcedure(name,type);
    }
    
    private Driver2 initDriver(UserSession us) throws DriverException {
    	drv = db.getDriver(us);
    	return drv;
    }
    
    private void releaseDriver() {
    	db.releaseDriverQuietly(drv);
    	drv = null;
    }
    
    public List<Long> getVcsGroupObjects(long clsId) throws KrnException {
    	try {
    		return  drv.getVcsGroupObjects(clsId);
    	} catch (DriverException e) {
    		throw new KrnException("Ошибка при получении списка измененных объектов.", 0, e);
    	}
    }
    
	public List<KrnVcsChange> getVcsChanges(int isFixd, int isRepld, long userId, long replId) throws KrnException {
		try {
			return drv.getVcsDataChanges(isFixd, isRepld, userId, replId, null);
		} catch (DriverException e) {
			throw new KrnException("Ошибка при получении списка измененных объектов.", 0, e);
		}
	}
    
	public List<KrnVcsChange> getVcsChangesByUID(int isFixd, int isRepld, long userId, long replId, String uid) throws KrnException {
		try {
			return drv.getVcsDataChanges(isFixd, isRepld, userId, replId, uid);
		} catch (DriverException e) {
			throw new KrnException("Ошибка при получении списка измененных объектов.", 0, e);
		}
	}
    
	public List<KrnVcsChange> getVcsHistoryChanges(boolean isModel, String uid, int typeId,boolean isLastChane) throws KrnException {
		try {
			return drv.getVcsHistoryDataChanges(isModel, uid, typeId,isLastChane);
		} catch (DriverException e) {
			throw new KrnException("Ошибка при получении списка истории измененных объектов.", 0, e);
		}
	}
	
	public List<KrnVcsChange> getVcsDifChanges(boolean isModel,long[] ids) throws KrnException {
		try {
			return drv.getVcsDifDataChanges(isModel,ids);
		} catch (DriverException e) {
			throw new KrnException("Ошибка при получении списка истории измененных объектов.", 0, e);
		}
	}
	
	public String getVcsHistoryDataIncrement(KrnVcsChange change) throws KrnException {
    	try {
    		return  drv.getVcsHistoryDataIncrement(change);
    	} catch (DriverException e) {
    		throw new KrnException("Ошибка при получении приращения.", 0, e);
    	}
    }
    
    public void commitVcsObjects(List<KrnVcsChange> changes, String comment) throws KrnException {
    	try {
    		drv.commitVcsObjects(changes, comment);
    	} catch (DriverException e) {
    		throw new KrnException("Ошибка при фиксации изменений.", 0, e);
    	}
    }
    
    public boolean setVcsUserForObject(KrnVcsChange change, long userId) throws KrnException {
        try {
                return drv.setVcsUserForObject(change, userId);
        } catch (DriverException e) {
                throw new KrnException("Ошибка при фиксации изменений.", 0, e);
        }
    }
    public void setVcsExport(long expObjId) throws KrnException {
    	try {
    		drv.setVcsExport(expObjId);
    	} catch (DriverException e) {
    		throw new KrnException("Ошибка при фиксации репликации.", 0, e);
    	}
    }

    public void commitVcsModelClassAttr(String comment) throws KrnException {
    	try {
    		drv.commitVcsModelClassAttr(comment);
    	} catch (DriverException e) {
    		throw new KrnException("Ошибка при фиксации изменений модели классов и атрибутов.", 0, e);
    	}
    }
    
    public String getEntityNameFromVCS(ModelChange ch) throws KrnException {
    	try {
    		return drv.getEntityNameFromVCS(ch);
    	} catch (DriverException e) {
    		throw new KrnException("Ошибка при получении названия сущности из таблицы t_vcs_model.", 0, e);
    	}
    }
    
    public void commitVcsObjectsAfterReplication(long replId, String comment) throws KrnException {
    	try {
    		drv.commitVcsObjectsAfterReplication(replId, comment);
    	} catch (DriverException e) {
    		throw new KrnException("Ошибка при фиксации изменений после репликации.", 0, e);
    	}
    }

    public void rollbackVcsObjects(List<KrnVcsChange> changes) throws KrnException {
    	try {
    		drv.rollbackVcsObjects(changes, this);
    	} catch (DriverException e) {
    		throw new KrnException("Ошибка при откате изменений.", 0, e);
    	}
    }

    protected static List<MetadataChangeListener> metadataChangeListeners = new ArrayList<MetadataChangeListener>();
    
	public static void addMetadataChangeListener(MetadataChangeListener l) {
		metadataChangeListeners.add(l);
	}

	public static List<MetadataChangeListener> getMetadataChangeListeners() {
    	return metadataChangeListeners;
    }
	
	public static boolean getBindingModuleToUserMode() {
		return bindingModuleToUser;
	}

	public boolean isDbReadOnly() {
		return drv.isDbReadOnly();
	}
	
	public void setDbId(String name,long value) throws KrnException{
		try {
			drv.setId(name, value);
		} catch (DriverException e) {
			throw new KrnException("Ошибка установке значения:"+value+" для "+name, 0, e);
		}
	}
    public boolean convertLinkForSysDb(long newBaseId, long oldBaseId) throws KrnException  {
		try {
			return drv.convertLinkForSysDb(newBaseId,oldBaseId);
		} catch (DriverException e) {
			throw new KrnException("Ошибка установке значения newBaseId:"+newBaseId+" для oldBaseId:"+oldBaseId, 0, e);
		}
    }
	public long getId(String tname) throws KrnException{
		try {
			return drv.getId(tname);
		} catch (DriverException e) {
			throw new KrnException(e.getMessage(), 0, e);
		}
	}
	public long getLastId(String tname) throws KrnException{
		try {
			return drv.getLastId(tname);
		} catch (DriverException e) {
			throw new KrnException(e.getMessage(), 0, e);
		}
	}
	public List<OrlangTriggerInfo> getOrlangTriggersInfo() throws KrnException {
		List<OrlangTriggerInfo> triggers = new ArrayList<OrlangTriggerInfo>();
		try {
			triggers = drv.getOrlangTriggersInfo();
		} catch (SQLException e) {
			throw new KrnException("Ошибка при формировании списка тригеров.", 0, e);
		} catch (DriverException e) {
			throw new KrnException("Ошибка при формировании списка тригеров.", 0, e);
		}
		return triggers;
	}

	public void setLoadingFile(boolean b) {
		drv.setLoadingFile(b);
	}
	
	public static boolean isAllowConvertDb(){
		return allowConvertDb;
	}
	
	public String getUrlConnection(){
		return db.getUrlConnection();
	}

	public static void putResponseWaiter(UUID uuid, ResponseWaiter waiter) {
		Map<String, ResponseWaiter> map = null;
		synchronized (responseWaiters) {
			map = responseWaiters.get(uuid);
			if (map == null) {
				map = new HashMap<>();
				responseWaiters.put(uuid, map);
			}
		}
		map.put(waiter.getReportId(), waiter);
	}

	public static ResponseWaiter getResponseWaiter(UUID uuid, String reportId) {
		Map<String, ResponseWaiter> map = responseWaiters.get(uuid);
		return map.remove(reportId);
	}
	
	public Database getDatabase() {
		return db;
	}
	
	public void setLoggingGetObjSql(boolean logginGetObjSql) {
		Driver2.setLoggingGetObjSql(logginGetObjSql);
	}
	
    public String getCurDirExchange() {
        return getMessageCache(dsName).curDir;
    }
    
    public String getConnectionId() {
        return drv.getSessionId();
    }
    
	public void getout(KrnObject user, String message) throws KrnException {
		ServerUserSession us = findUserSession(user.id);
		if (us != null) {
    		sendNoteClustered(us, new MessageNote(new java.util.Date(), null, message, true));
		}
	}
	
	public void removeObjectsFromCache(final List<String> uids) {
		db.removeObjectsFromCache(uids);
	}
	
	public List<Long> getFiltersContainingAttr(KrnAttribute attr) {
		KrnClass filterCls = db.getClassByName("Filter");
		KrnAttribute configAttr = db.getAttributeByName(filterCls.id, "config");
		KrnAttribute classNameAttr = db.getAttributeByName(filterCls.id, "className");
		KrnAttribute exprSqlAttr = db.getAttributeByName(filterCls.id, "exprSql");
		KrnAttribute titleAttr = db.getAttributeByName(filterCls.id, "title");
		KrnClass cls = db.getClassById(attr.classId);
		return drv.getFiltersContainingAttr(filterCls, configAttr, classNameAttr, exprSqlAttr, titleAttr, cls, attr);
	}
	
	public boolean isRNDB() {
		return Database.isRnDB;
	}
	
	public boolean isULDB() {
		return Database.isUlDB;
	}
	
	public boolean hasUseECP() {
		KrnClass cls = db.getClassByName("Политика учетных записей");
		KrnAttribute attr = db.getAttributeByName(cls.id, Constants.ATTR_USE_ECP);
		return attr != null;
	}
	
    public String updateSysLang() throws KrnException {
    	return drv.updateColumnsSysLang(-1,false);
   	}
    
    public String getProcAllowed() {
    	return procAllowed;
    }
    
    public String getProcDenied() {
    	return procDenied;
    }
 }