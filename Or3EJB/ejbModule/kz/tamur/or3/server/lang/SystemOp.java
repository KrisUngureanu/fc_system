package kz.tamur.or3.server.lang;

import static kz.tamur.common.ErrorCodes.PASS_MESS_PASS_DUPL;
import static kz.tamur.common.ErrorCodes.PASS_MIN_PERIOD_PASS;
import static kz.tamur.common.ErrorCodes.PASS_NOT_COMPLETE;
import static kz.tamur.common.ErrorCodes.PASS_OLD_PASS_INVALID;
import static kz.tamur.common.ErrorCodes.PASS_PASS_IDENT;
import static kz.tamur.common.ErrorCodes.PASS_PASS_NOT_EQUALS;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MAX_LOGIN;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MAX_PASS;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MIN_LOGIN;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MIN_PASS;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MIN_PASS_ADM;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_EASY_SYMBOLS;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_KEYBOARD;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_LOGIN;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_NAME;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_REP;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_REP_ANY_MORE_TWO;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_SURN;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_TEL;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_WORD;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_ALL_NUMB;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_NUMB;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_REG;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_SPEC;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_SYMB;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import kz.tamur.DriverException;
import kz.tamur.SecurityContextHolder;
import kz.tamur.comps.Constants;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.DocumentConverter;
import kz.tamur.server.indexer.Indexer;
import kz.tamur.util.Funcs;
import kz.tamur.util.PasswordService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.UserSessionValue;
import com.cifs.or2.server.Session;

/**
 * The Class SystemOp.
 * 
 * @author Berik
 */
public class SystemOp extends kz.tamur.lang.SystemOp {
    
	private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + SystemOp.class.getName());

	public static void main(String[] args) {
		try {
			SystemOp op = new SystemOp(null);
			
			byte[] imageData = Funcs.read("D:\\tmp\\kyzmet\\2021-02-18\\норм.jpeg");
			int width = 36;
			int height = 48;
			String format = "jpg";
			
			byte[] res = op.getScaledImage(imageData, width, height, format);
			byte[] res2 = op.getScaledImage2(imageData, width, height, format);
			
			Funcs.write(res, new File("D:\\tmp\\kyzmet\\2021-02-18\\1.jpg"));
			Funcs.write(res2, new File("D:\\tmp\\kyzmet\\2021-02-18\\2.jpg"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    /** session. */
    private Session session;

    /**
     * Конструктор класса SystemOp.
     * 
     * @param session
     *            the session
     */
    public SystemOp(Session session) {
        super();
        this.session = session;
    }
    
    @Override
    public void commit() throws Exception {
        session.commitTransaction();
    }

    @Override
    public void rollback() throws Exception {
        session.rollbackTransaction();
    }
    
    @Override
    public void commitLongTransaction(long id) throws Exception {
        session.commitLongTransaction(id, 0);
    }

    @Override
    public void rollbackLongTransaction(long id) throws Exception {
        session.rollbackLongTransaction(id);
    }
    
    @Override
    public List<UserSessionValue> getUserSessions() throws Exception {
        return Arrays.asList(session.getUserSessions());
    }
    
    @Override
    public void killUserSession(UUID usId, boolean blockUser) throws Exception {
        session.killUserSessions(usId, blockUser);
    }
    
    @Override
    public byte[] convertOfficeDocument(byte[] docData, String outputFormat) throws Exception {
        return DocumentConverter.convert(docData, outputFormat);
    }
    
    @Override
    public void sendNotification(UserSession from, Set<KrnObject> usersTo, Object data, int type) throws Exception {
        session.sendNotification(from, usersTo, data, type, null);
    }
    
    @Override
    public void deleteNotification(KrnObject user, String uid, String cuid) throws Exception {
    	session.deleteNotification(user, uid, cuid);
    }
    
    @Override
    public void deleteNotificationByUID(KrnObject user, String uid) throws Exception {
    	session.deleteNotificationByUID(user, uid);
    }

    @Override
    public void sendNotification(UserSession from, Set<KrnObject> usersTo, Object data, int type, String title) throws Exception {
        session.sendNotification(from, usersTo, data, type, title);
    }
    
    @Override
    public void sendMessage(UserSession from, Set<KrnObject> usersTo, String text) throws Exception {
        session.sendMessage(from, usersTo, text);
    }

    @Override
    public String encrypt(String str) {
        return PasswordService.getInstance().encrypt(str);
    }
    
    @Override
    public String verifyPassword(KrnObject user, String str, String name, boolean admin, boolean isLogged, String psw, Object lastChangeTime) {
    	try {
    		session.verifyPassword(session.getUserSession().getUserName(), user, str.toCharArray(), name, admin, isLogged, psw, kz.tamur.util.Funcs.convertTime((Date) lastChangeTime));
		} catch (KrnException e) {
			String mess = "";
	        ResourceBundle res = ResourceBundle.getBundle("kz.tamur.rt.RuntimeResourcesSrv", new Locale("ru"));
			switch (e.code) {
				case PASS_NOT_COMPLETE:
					mess = res.getString("notCompleteMessage");
					break;
				case PASS_PASS_NOT_EQUALS:
					mess = res.getString("passNotEqualsMessage");
					break;
				case PASS_PASS_IDENT:
					mess = res.getString("messPassIdent");
					break;
				case PASS_OLD_PASS_INVALID:
					mess = res.getString("oldPassInvalidMessage");
					break;
				case PASS_MIN_PERIOD_PASS:
					mess = res.getString("messMinPeriodPass");
					mess = mess.replaceFirst("X", e.getMessage());
					break;
				case PASS_VALID_PWD_MIN_LOGIN:
					mess = res.getString("validPwdMinLogin");
					mess = mess.replaceFirst("X", e.getMessage());
					break;
				case PASS_VALID_PWD_MAX_LOGIN:
					mess = res.getString("validPwdMaxLogin");
					mess = mess.replaceFirst("X", e.getMessage());
					break;
				case PASS_VALID_PWD_MIN_PASS:
					mess = res.getString("validPwdmMinPass");
					mess = mess.replaceFirst("X", e.getMessage());
					break;
				case PASS_VALID_PWD_MIN_PASS_ADM:
					mess = res.getString("validPwdMinPassAdm");
					mess = mess.replaceFirst("X", e.getMessage());
					break;
				case PASS_VALID_PWD_MAX_PASS:
					mess = res.getString("validPwdmMaxPass");
					mess = mess.replaceFirst("X", e.getMessage());
					break;
				case PASS_VALID_PWD_NO_NUMB:
					mess = res.getString("validPwdNoNumb");
					break;
				case PASS_VALID_PWD_NO_ALL_NUMB:
					mess = res.getString("validPwdNoAllNumb");
					break;
				case PASS_VALID_PWD_NO_SYMB:
					mess = res.getString("validPwdNoSymb");
					break;
				case PASS_VALID_PWD_NO_REG:
					mess = res.getString("validPwdNoReg");
					break;
				case PASS_VALID_PWD_NO_SPEC:
					mess = res.getString("validPwdNoSpec");
					break;
				case PASS_VALID_PWD_NOT_NAME:
					mess = res.getString("validPwdNotName");
					break;
				case PASS_VALID_PWD_NOT_SURN:
					mess = res.getString("validPwdNotSurn");
					break;
				case PASS_VALID_PWD_NOT_TEL:
					mess = res.getString("validPwdNotTel");
					break;
				case PASS_VALID_PWD_NOT_WORD:
					mess = res.getString("validPwdNotWord");
					break;
				case PASS_VALID_PWD_NOT_KEYBOARD:
					mess = res.getString("validPwdNotKeyboard");
					break;
				case PASS_VALID_PWD_NOT_LOGIN:
					mess = res.getString("validPwdNotLogin");
					break;
				case PASS_VALID_PWD_NOT_REP:
					mess = res.getString("validPwdNotRep");
					break;
				case PASS_VALID_PWD_NOT_REP_ANY_MORE_TWO:
					mess = res.getString("validPwdNotRepAnyMoreTwo");
					break;
				case PASS_MESS_PASS_DUPL:
					mess = res.getString("messPassDupl");
					mess = mess.replaceFirst("X", e.getMessage());
					break;
				case PASS_VALID_PWD_NOT_EASY_SYMBOLS:
					mess = res.getString("validPwdNotIdentificationData");
					break;
				default:
					mess = e.getMessage();
					break;
			}
			return mess;
    	} catch (Exception e) {
			log.error(e, e);
    		return "UNKNOWN EXCEPTION";
    	}
        return null;
    }

    /**
     * Запуск процесса.
     * 
     * @param id
     *            идентификатор процесса.
     * @param withoutTransaction
     *            нулевая транзакция? TODO уточнить
     * @return результат запуска процесса.
     * @throws Exception
     *             the exception
     */
    public String[] startProcess(long id, boolean withoutTransaction) throws Exception {
        return session.startProcess(id, withoutTransaction);
    }

    /**
     * Запуск процесса.
     * 
     * @param id
     *            идентификатор процесса.
     * @param withoutTransaction
     *            нулевая транзакция? TODO уточнить
     * @param start
     *            инициирован процесс?
     * @return  результат запуска процесса.
     * @throws Exception
     *             the exception
     */
    public String[] startProcess(long id, boolean withoutTransaction, boolean start) throws Exception {
        return session.startProcess(id, withoutTransaction, start);
    }

    /**
     * Запуск процесса.
     * 
     * @param pdef
     *            процесс.
     * @param vars
     *            переменные для процесса.
     * @return  результат запуска процесса.
     * @throws Exception
     *             the exception
     */
    public String[] startProcess(KrnObject pdef, Map<String, Object> vars) throws Exception {
        return session.startProcess(pdef.id, vars);
    }

    /**
     * Перегрузка синхронизованных потоков закончена.
     * 
     * @return <code>true</code>, в случае успеха
     * @throws Exception
     *             the exception
     */
    public boolean reloadSincFlows() throws Exception {
        return session.reloadSincFlows();
    }

    /**
     * Остановить процесс.
     * 
     * @param id
     *            id процесса.
     * @throws Exception
     *             the exception
     */
    public void cancelProcess(long id) throws Exception {
        session.cancelProcess(id, "", true, false);
    }

    public void cancelProcess(long id, boolean forceCancel) throws Exception {
        session.cancelProcess(id, "", true, forceCancel);
    }

    public boolean isProcessRunning(long id) throws Exception {
        return session.isRunning(id);
    }

    /**
     * Перегрузить процес.
     * 
     * @param id
     *            id процесса.
     * @throws Exception
     *             the exception
     */
    public void reloadFlow(long id) throws Exception {
        session.reloadFlow(id);
    }

    /**
     *Сохранение параметров потока.
     * 
     * @param id
     *            id процесса.
     * @param args
     *            список параметров.
     * @throws Exception
     *             the exception
     */
    public void saveFlowParam(long flowId,List<String> args) throws Exception {
        session.saveFlowParam(flowId, args);
    }
    /**
     *Установление максисмального кол. одновременно выполняемых потоков.
     * 
     * @param maxThreadCount
     *            максимальное количество потоков.
     */
    public void setMaxActiveCount(int maxThreadCount){
    	Session.setMaxActiveCount(maxThreadCount);
    }
    /**
     *Установление максисмального запущенных потоков.
     * 
     * @param maxThreadCount
     *            максимальное количество потоков.
     */
    public void setMaxFlowCount(int maxThreadCount){
    	Session.setMaxFlowCount(maxThreadCount);
    }
    /**
     * Запуск репликации.
     * 
     * @throws Exception
     *             the exception
     */
    public void runReplication() throws Exception {
        session.runReplication();
    }

    /**
     * Обновление пользователя.
     * 
     * @param id
     *            id пользователя.
     * @throws Exception
     *             the exception
     */
    public void updateUser(long id) throws Exception {
        session.updateUser(id);
    }

    /**
     * Удалить пользователя.
     * 
     * @param id
     *            id пользователя.
     * @throws Exception
     *             the exception
     */
    public void removeUser(long id) throws Exception {
        session.removeUser(id);
    }

    /**
     * Загрузить объекты для конкретного из внешней базы с такой же моделью данных.
     * 
     * @param jndiName путь к врешней базе.
     * @param attrId идентификатор атрибута
     * @param objUids список уидов объектов для зарузки и обновления(через запятую)
     * @return list результат выполнения запроса, не пустой если <code>isUpdate=false</code> и была выборка данных. 
     */
    public boolean updateAttrFromExtDb(String jndiName,long attrId, String objUids) {
        return session.updateAttrFromExtDb(jndiName,attrId, objUids);
    }

    /**
     * Выполнить  sql запрос.
     * 
     * @param sql
     *            sql запрос.
     * @param isUpdate
     *            обновление данных?
     * @return list результат выполнения запроса, не пустой если <code>isUpdate=false</code> и была выборка данных. 
     */
    public List runSql(String sql, boolean isUpdate) {
        return session.runSql(sql, isUpdate);
    }
    /**
     * Пересобрать индекс атрибута.
     * 
     * @param attrId
     *            id атрибута.
     * @throws Exception
     *             the exception
     */
    public void rebuildIndex(long attrId) throws Exception {
        KrnAttribute attr = session.getAttributeById(attrId);
        Indexer.updateIndex(attr, session, null);
    }

    /**
     * Поиск значения в атрибуте.
     * 
     * @param attrId
     *            id атрибута.
     * @param lang
     *            язык.
     * @param pattern
     *            шаблон поиска.
     * @return list список совпавших значений атрибута.
     * @throws Exception
     *             the exception
     */
    public List<String> find(long attrId, KrnObject lang, String pattern) throws Exception {
        return Indexer.find(attrId, lang.id, pattern);
    }

    /**
     * Отправка сообщения.
     * 
     * @param msg
     *            сообщение xml-элемент.
     * @param boxId
     *            id узла.
     * @return <code>true</code>, в случае успеха отправки
     * @throws Exception
     *             the exception
     */
    public boolean sendMessage(Element msg, long boxId) throws Exception {
        return session.sendMessage(msg, boxId);
    }

    /**
     * Отправка e-mail.
     * 
     * @param obj
     *            отправляемый объект
     * @param host
     *            адрес сервера.
     * @param port
     *            порт.
     * @param user
     *            пользователь.
     * @param passwd
     *            пароль пользователя.
     * @return true, в случае успеха
     * @throws KrnException
     *             the krn exception
     */
    public boolean sendMailMessage(KrnObject obj, String host, String port, String user, String passwd) throws KrnException {
        return session.sendMailMessage(obj, host, port, user, passwd);
    }

    public boolean sendMailMessage(String host, String port, String user, String pd,
    		List<String> froms, List<String> tos, String theme, String text, String mime, String charSet, boolean ssl) throws KrnException {
        return session.sendMailMessage(host, port, user, pd, froms.toArray(new String[0]), tos.toArray(new String[0]), theme, text, mime, charSet, ssl);
    }

    /**
     * Проверка валидности e-mail адреса.
     * 
     * @param email
     *            e-mail адрес.
     * @return <code>true</code>, если адрес валиден.
     */
    public boolean isValidEmailAddress(String email) {
        return session.isValidEmailAddress(email);
    }

	@Override
	public List<Long> findProcessByUiType(String uiType) throws KrnException {
		return session.findProcessByUiType(uiType);
	}
	@Override
	public List<Long> findForeignProcess(long processDefId,long cutObjId) throws KrnException {
		return session.findForeignProcess(processDefId, cutObjId);
	}

	@Override
	public boolean initServerTasks() {
		return session.initServerTasks();
	}

	public void setActivateScheduler(boolean activate) {
		session.setActivateScheduler(activate);
	}

	@Override
	public String createProcedure(String name, List params, String body) throws Exception {
		return session.createProcedure(name, params, body);
	}

	@Override
	public List execProcedure(String name) throws Exception {
		return session.execProcedure(name);
	}
	
	@Override
	public List execProcedure(String name, List<Object> vals) throws Exception {
		return session.execProcedure(name, vals);
	}
	
	@Override
	public List execProcedure(String name, List<Object> vals, List<String> types_in, List<String> types_out) throws Exception {
		return session.execProcedure(name, vals,types_in,types_out);
	}
	
	 public void addIndex(String search_str, KrnObject obj, KrnObject balans_ed) {
		 session.addIndex(search_str,obj, balans_ed);
	 }
	 
	 public void deleteIndex(KrnObject obj) {
		 session.removeIndex(obj);
	 }
	
	public void reloadProcessDefinition(KrnObject processDef) throws Exception {
		if (processDef != null) {
			reloadProcessDefinition(processDef.id);
		}
	}
	
	public void reloadProcessDefinition(long processDefId) throws Exception {
		session.reloadProcessDefinition(processDefId);
	}
	
	public boolean lockMutex(String muid) throws DriverException {
		return session.getDriver().lockMutex(muid);
	}

	public boolean unlockMutex(String muid) throws DriverException {
		return session.getDriver().unlockMutex(muid);
	}
	
	public void setLoggingGetObjSql(boolean logginGetObjSql) throws KrnException {
        session.setLoggingGetObjSql(logginGetObjSql);
	}

	// Методы перенесенные из класса kz.tamur.or3ee.server.lang.SrvSystemWrp

	@Override
	public void showMessage(Object value) {}

	@Override
	public void showErrorMessage(Object value) {}

	@Override
	public void showInfoMessage(Object value) {}

	@Override
	public void showWarningMessage(Object value) {}

	@Override
	public int showConfirmMessage(Object value) {
		return 0;
	}

	@Override
	public String requestPassword() {
		return null;
	}

	@Override
	public void log(SystemEvent event, String proc, String objName, String message) {
		UserSession us = session.getUserSession();
		StringBuilder res = new StringBuilder(" | ").append(SystemEvent.TYPES[event.getTypeCode()]).append(" | ")
				.append(event.getName()).append(" | ").append(us.getUserName()).append(" | ").append(us.getIp()).append(" | ")
				.append(us.getComputer()).append(" | ").append(us.isAdmin() ? 1 : 0).append(" | ").append(us.getServerId())
				.append(" | ").append(proc).append(" | ").append(objName).append(" | ").append(message)
				.append(" | | "); // Добавляем пустые поля таблицы и колонки, иначе не будет писать в t_sys_log
		log.info(res.toString());
	}

	@Override
	public void writeLogRecord(String loggerName, String type, String event, String description) {
    	session.writeLogRecord(loggerName, type, event, description);
	}

	@Override
	public List<String> findText(String text, int condition) {
		List<String> uidResults = new ArrayList<String>();
		String inputString = Funcs.normalizeInput(text).trim();
		boolean[] searchArea = { true, false, false, false, false, false, false, false };
		int[] searchProperties = { 0, condition };			
		List<Object> results = session.search(inputString, 1000, searchProperties, searchArea);			
		List<String[]> objects = (List<String[]>) results.get(0);
		if (objects.size() > 0) {
			int count = Funcs.checkInt(objects.size(), 1000);		
			for (int i = 0; i < count; i++) {
				String[] object = objects.get(i);
				String res = (object[4].equals("class")? "object attr: " + object[1] : object[4]) + ", " + object[0];
				uidResults.add(res);			
				System.out.println(res);
			}
		} else {
			System.out.println("Objects not found!");
		}			
		return uidResults;
	}
	
	@Override
    public String checkPassAndLogin(String pd, String userName, boolean isAdmin, boolean chekLogin) {
        return checkPassAndLogin(pd.toCharArray(), userName, isAdmin, chekLogin, new Locale("ru"));
    }
    
    @Override
    public String checkPassAndLogin(String pd, String userName, boolean isAdmin, boolean chekLogin, KrnObject lang) {
        return checkPassAndLogin(pd.toCharArray(), userName, isAdmin, chekLogin, new Locale(lang != null && (lang.id == 2 || lang.id == 123) ? "kk" : "ru"));
    }
     
    public String checkPassAndLogin(char[] password, String userName, boolean isAdmin, boolean chekLogin, Locale loc) {
        try {
        	session.getPolicy().checkPassAndLogin(password, userName, isAdmin, chekLogin);
        } catch (KrnException e) {
            String mess = "";
            ResourceBundle res = ResourceBundle.getBundle("kz.tamur.rt.RuntimeResourcesSrv", loc);
            switch (e.code) {
	            case PASS_VALID_PWD_MIN_LOGIN:
	                mess = res.getString("validPwdMinLogin");
	                mess = mess.replaceFirst("X", e.getMessage());
	                break;
	            case PASS_VALID_PWD_MAX_LOGIN:
	                mess = res.getString("validPwdMaxLogin");
	                mess = mess.replaceFirst("X", e.getMessage());
	                break;
	            case PASS_VALID_PWD_MIN_PASS:
	                mess = res.getString("validPwdmMinPass");
	                mess = mess.replaceFirst("X", e.getMessage());
	                break;
	            case PASS_VALID_PWD_MIN_PASS_ADM:
	                mess = res.getString("validPwdMinPassAdm");
	                mess = mess.replaceFirst("X", e.getMessage());
	                break;
	            case PASS_VALID_PWD_MAX_PASS:
	                mess = res.getString("validPwdmMaxPass");
	                mess = mess.replaceFirst("X", e.getMessage());
	                break;
	            case PASS_VALID_PWD_NO_NUMB:
	                mess = res.getString("validPwdNoNumb");
	                break;
	            case PASS_VALID_PWD_NO_ALL_NUMB:
	                mess = res.getString("validPwdNoAllNumb");
	                break;
	            case PASS_VALID_PWD_NO_SYMB:
	                mess = res.getString("validPwdNoSymb");
	                break;
	            case PASS_VALID_PWD_NO_REG:
	                mess = res.getString("validPwdNoReg");
	                break;
	            case PASS_VALID_PWD_NO_SPEC:
	                mess = res.getString("validPwdNoSpec");
	                break;
	            case PASS_VALID_PWD_NOT_NAME:
	                mess = res.getString("validPwdNotName");
	                break;
	            case PASS_VALID_PWD_NOT_SURN:
	                mess = res.getString("validPwdNotSurn");
	                break;
	            case PASS_VALID_PWD_NOT_TEL:
	                mess = res.getString("validPwdNotTel");
	                break;
	            case PASS_VALID_PWD_NOT_WORD:
	                mess = res.getString("validPwdNotWord");
	                break;
	            case PASS_VALID_PWD_NOT_KEYBOARD:
	                mess = res.getString("validPwdNotKeyboard");
	                break;
	            case PASS_VALID_PWD_NOT_LOGIN:
	                mess = res.getString("validPwdNotLogin");
	                break;
	            case PASS_VALID_PWD_NOT_REP:
	                mess = res.getString("validPwdNotRep");
	                break;
	            case PASS_VALID_PWD_NOT_REP_ANY_MORE_TWO:
	                mess = res.getString("validPwdNotRepAnyMoreTwo");
	                break;
	            default:
	            	mess = e.getMessage();
            }
            return mess;
        }
        return null;
    }

	public void getout(KrnObject user) throws KrnException {
    	getout(user, null);
	}
	
	@Override
	public void getout(KrnObject user, String message) throws KrnException {
		session.getout(user, message);
	}

	@Override
	public void sendMessage(KrnObject user, String message) throws KrnException {
		session.sendMessage(user, message);
	}

	public KrnObject sendNotification(KrnObject user, String message, String uid, String cuid) throws KrnException {
		return sendNotification(user, message, uid, cuid, null, null, -1);
	}
	
	public KrnObject sendNotification(KrnObject user, String message, String uid, String cuid, String proc, String iter, long trId) throws KrnException {
		return session.sendNotification(user, message, uid, cuid, proc, iter, trId);
	}
	
	public KrnObject sendNotification(KrnObject user, String message, String uid, String cuid, long trId) throws KrnException {
		return sendNotification(user, message, uid, cuid, null, null, trId);
	}
	
	public KrnObject sendNotification(KrnObject user, String message, String uid, String cuid, String proc, String iter) throws KrnException {
		return sendNotification(user, message, uid, cuid, proc, iter, -1);
	}
	
	// Методы перенесенные из класса com.cifs.or2.server.plugins.SystemOr3
	
	/**
     * Пересохранить фильтры
     */
    public void resaveFilter(){
    	session.resaveFilters();
    }

    public void resaveFilter(long filterId) throws KrnException {
    	session.saveFilter(filterId, 0);
    }

    public void resaveFilter(KrnObject filter) throws KrnException {
    	session.saveFilter(filter.id, 0);
    }
    
    /**
     * Перезагрузить содержимое фильтров
     */
    public void reloadFilters(){
    	session.reloadFilters();
    }
    
    /**
     * Перезагрузить содержимое данного фильтра
     * @param id фильтр
     */
    public void reloadFilter(long id) {
        try {
        	session.reloadFilter(id);
		} catch (KrnException e) {
        	SecurityContextHolder.getLog().error(e, e);
		}
    }
    
    /**
     * Пересохранить триггеры
     * @throws KrnException
     */
	public void resaveTriggers() throws KrnException {
		session.resaveTriggers();
	}
	
    /**
     * Начать отправку
     * @param transportId
     */
    public void startTransport(int transportId){
        try {
        	session.startTransport(transportId);
        } catch (KrnException e) {
        	SecurityContextHolder.getLog().error(e, e);
        }
    }
    
    public void setMessageStatus(String initId,String prodId,String text){
        session.setMessageStatus(initId,prodId,text);
    }
    
    /**
     * Возвращает входной поток буферизации
     * @param path путь к файлу
     * @return
     */
	public BufferedReader getBufferedReader(String path) {
		BufferedReader res = null;
		try {
			File file = new File(path);
			if (!file.exists())
				return res;
			FileReader fr = new FileReader(file);
			res = new BufferedReader(fr);
		} catch (Exception e) {
			SecurityContextHolder.getLog().error(e, e);
		}
		return res;
	}
	public String updateSysLang(){
        try {
        	return session.updateSysLang();
        } catch (KrnException e) {
        	SecurityContextHolder.getLog().error(e, e);
        }
        return null;
	}

	
	@Override
	public byte[] generateReport(long reportId, Object obj, KrnObject lang, String format) throws KrnException {
		KrnObject mainReport = session.getObjectById(reportId, 0);
		return generateReport(mainReport, obj, lang, format);
	}

	@Override
	public byte[] generateReport(String reportUID, Object obj, KrnObject lang, String format) throws KrnException {
		KrnObject mainReport = session.getObjectByUid(reportUID, 0);
		return generateReport(mainReport, obj, lang, format);
	}

	@Override
	public byte[] generateReport(KrnObject mainReport, Object obj, KrnObject lang, String format) throws KrnException {
		
		KrnClass reportCls = session.getClassByName("ReportPrinter");
		KrnClass reportFolderCls = session.getClassByName("ReportFolder");
		KrnAttribute configAttr = session.getAttributeByName(reportCls, "config");
		KrnAttribute templateAttr = session.getAttributeByName(reportCls, "template");
		KrnAttribute parentAttr = session.getAttributeByName(reportCls, "parent");
		KrnAttribute childrenAttr = session.getAttributeByName(reportFolderCls, "children");
		
        byte[] data = session.getBlob(mainReport.id, configAttr.id, 0, 0, 0);

        InputStream is = new ByteArrayInputStream(data);
        SAXBuilder builder = new SAXBuilder();

        try {
            Element xml = builder.build(is).getRootElement();
            is.close();
            Element editorType = xml.getChild("editorType");
            if (editorType == null || !String.valueOf(Constants.JASPER_EDITOR).equals(editorType.getText())) {
                throw new KrnException(0, "Тип отчета должен быть Jasper Reports");
            } else {
                Element macros = xml.getChild("macros");
                if (macros == null || macros.getText().trim().length() == 0) {
                    throw new KrnException(0, "Не задан метод формирования xml данных");
                } else {
                	String xmlMethod = macros.getText();
                    List<Object> args = new ArrayList<Object>();
                    args.add(obj);
                    args.add(lang);
                	
                	Element xmlData = null;
                	
                	try {
                		KrnClass utilCls = session.getClassByName("ReportUtil");
						xmlData = (Element) utilCls.exec(utilCls, xmlMethod, args, new Stack<String>());
					} catch (Throwable e) {
						throw new KrnException("Ошибка при формировании xml данных для отчета", 0, e);
					}
                	// Шаблон основного отчета
                    byte[] jasperMain = session.getBlob(mainReport.id, templateAttr.id, 0, lang.id, 0);
                	
                    if (jasperMain == null || jasperMain.length == 0) {
                    	throw new KrnException(0, "Не задан шаблон отчета " + mainReport.uid);
                    }

                    Map<String, Object> params = new HashMap<>();

                    KrnObject[] parents = session.getObjects(mainReport.id, parentAttr.id, new long[0], 0);
                    if (parents != null && parents.length > 0) {
                    	KrnObject parent = parents[0];
                    	KrnObject[] children = session.getObjects(parent.id, childrenAttr.id, new long[0], 0);
                    	
                    	if (children != null && children.length > 0) {
                    		for (KrnObject childReport : children) {
                    			if (childReport.id != mainReport.id) {
	                    			byte[] config = session.getBlob(childReport.id, configAttr.id, 0, 0, 0);
	
	                    	        is = new ByteArrayInputStream(config);
	                    	        is.close();
	                    	        builder = new SAXBuilder();
	
	                	            xml = builder.build(is).getRootElement();
	                	            macros = xml.getChild("macros");
	                	            
	                	            if (macros == null || macros.getText().trim().length() == 0) {
	                                    throw new KrnException(0, "Не задано название переменной для дочернего отчета");
	                                } else {
		                            	// Шаблон дочернего отчета
		                                byte[] jasperChild = session.getBlob(childReport.id, templateAttr.id, 0, lang.id, 0);
		                                if (jasperChild == null || jasperChild.length == 0) {
		                                	throw new KrnException(0, "Не задан шаблон отчета " + mainReport.uid);
		                                }
		                                params.put(macros.getText().trim(), jasperChild);
	                                }
                    			}                    			
                    		}
                    	}
                    }
                    
                    try {
						byte[] resPdf = generateReport(xmlData, jasperMain, params, format);
						
						return resPdf;
					} catch (Exception e) {
						throw new KrnException("Ошибка при формировании PDF отчета", 0, e);
					}
                }
            }	
		} catch (JDOMException e) {
			throw new KrnException("Ошибка чтения отчета", 0, e);
		} catch (IOException e) {
			throw new KrnException("Ошибка чтения отчета", 0, e);
		}
	}
}