package kz.tamur.or3.client.lang;

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
import static kz.tamur.guidesigner.MessagesFactory.ENTER_PASSWORD_MESSAGE;
import static kz.tamur.guidesigner.MessagesFactory.ERROR_MESSAGE;
import static kz.tamur.guidesigner.MessagesFactory.EXCLAMATION_MESSAGE;
import static kz.tamur.guidesigner.MessagesFactory.INFORMATION_MESSAGE;
import static kz.tamur.guidesigner.MessagesFactory.QUESTION_MESSAGE;
import static kz.tamur.guidesigner.MessagesFactory.showMessageDialog;
import static kz.tamur.guidesigner.MessagesFactory.showPasswordDialog;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.cert.X509Certificate;
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

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

import kz.crypto.CryptoApplet;
import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.tamur.SecurityContextHolder;
import kz.tamur.comps.Constants;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.InterfaceManagerFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.Funcs;
import kz.tamur.util.PasswordService;
import kz.tamur.util.crypto.CheckSignResult;
import kz.tamur.util.crypto.KalkanUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.UserSessionValue;

public class SystemOp extends kz.tamur.lang.SystemOp {

	/** krn. */
    private Kernel krn;

    /** key path. */
    private static String keyPath;

    /** key pass. */
    private static String keyPD;

    /** ecp params. */
    private static Map<String, Object> ecpParams = null;

    /** last path. */
    private static String lastPath = null;
    
    private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + SystemOp.class.getName());

	public SystemOp(Kernel krn) {
        this.krn = krn;
    }
	
    @Override
    public void commit() throws Exception {}
    
    @Override
    public void rollback() throws Exception {}
    
    @Override
    public void commitLongTransaction(long id) throws Exception {
        SecurityContextHolder.getKernel().commitLongTransaction(id, 0);
    }
    
    @Override
    public void rollbackLongTransaction(long id) throws Exception {
    	SecurityContextHolder.getKernel().rollbackLongTransaction(id);
    }
    
    @Override
    public List<UserSessionValue> getUserSessions() throws Exception {
        return Arrays.asList(SecurityContextHolder.getKernel().getUserSessions());
    }
    
    @Override
    public void killUserSession(UUID usId, boolean blockUser) throws Exception {
    	SecurityContextHolder.getKernel().killUserSession(usId, blockUser);
    }
    
    @Override
    public String verifyPassword(KrnObject user, String str, String name, boolean admin, boolean isLogged, String psw, Object lastChangeTime) {
    	try {
    		UserSessionValue us = SecurityContextHolder.getKernel().getUserSession();
    		SecurityContextHolder.getKernel().verifyPassword(us.dsName, us.name, user, str.toCharArray(), name, admin, isLogged, psw, (Date)lastChangeTime);
    	} catch (KrnException e) {
            String mess = "";
            Object frame = SecurityContextHolder.getFrame();
            try {
	            Method method = frame.getClass().getMethod("getResourceBundle");
	            ResourceBundle res = (ResourceBundle) method.invoke(frame);
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
            } catch (Exception e1) {
            	e1.printStackTrace();
            	mess = e.getMessage();
            }
    		return mess;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return "UNKNOWN EXCEPTION";
    	}
        return null;
    }
    
    @Override
    public String encrypt(String str) {
        return PasswordService.getInstance().encrypt(str);
    }

    @Override
    public byte[] convertOfficeDocument(byte[] docData, String outputFormat) throws Exception {
        return SecurityContextHolder.getKernel().convertOfficeDocument(docData, outputFormat);
    }

    @Override
    public void sendNotification(UserSession from, Set<KrnObject> usersTo, Object data, int type) throws Exception {
    }
    
    @Override
    public void deleteNotification(KrnObject user, String uid, String cuid) throws Exception {
    }
    
    @Override
    public void deleteNotificationByUID(KrnObject user, String uid) throws Exception {
    }

    @Override
    public void sendNotification(UserSession from, Set<KrnObject> usersTo, Object data, int type, String title) throws Exception {
    }
    
    @Override
    public void sendMessage(UserSession from, Set<KrnObject> usersTo, String text) throws Exception {
    }

    /**
     * Отправка e-mail.
     * 
     * @param host
     *            адрес сервера.
     * @param port
     *            порт.
     * @param user
     *            пользователь.
     * @param passwd
     *            пароль пользователя.
     * @param froms
     *            от кого, массив.
     * @param tos
     *            кому, массив.
     * @param theme
     *            тема сообщения.
     * @param text
     *            текст сообщения.
     * @param mime
     *            тип данных.
     * @param charSet
     *            кодировка сообщения.
     * @return true, в случае успеха
     * @throws KrnException
     *             the krn exception
     */
    public boolean sendMailMessage(String host, String port, String user, String passwd, String[] froms, String[] tos,
            String theme, String text, String mime, String charSet) throws KrnException {
        return SecurityContextHolder.getKernel().sendMailMessage(host, port, user, passwd, froms, tos, theme, text, mime, charSet);
    }

    /**
     * Проверка валидности e-mail адреса.
     * 
     * @param email
     *             e-mail адрес.
     * @return <code>true</code>, если адрес валиден.
     */
    public boolean isValidEmailAddress(String email) {
        return SecurityContextHolder.getKernel().isValidEmailAddress(email);
    }

    @Override
	public List<Long> findProcessByUiType(String uiType) throws KrnException {
		return SecurityContextHolder.getKernel().findProcessByUiType(uiType);
	}
    
	@Override
	public List<Long> findForeignProcess(long proceeDefId,long cutObjId) throws KrnException {
		return SecurityContextHolder.getKernel().findForeignProcess(proceeDefId, cutObjId);
	}

	@Override
	public boolean initServerTasks() throws KrnException{
		return SecurityContextHolder.getKernel().initServerTasks();
	}

	@Override
	public String createProcedure(String name, List params, String body) throws Exception {
		return "";
	}

	@Override
	public List execProcedure(String name) throws Exception {
		return null;
	}
	
	@Override
	public List execProcedure(String name, List vals) throws Exception {
		return null;
	}
	
	@Override
	public List execProcedure(String name, List vals, List types_in, List types_out) throws Exception {
		return null;
	}
	
	public void setLoggingGetObjSql(boolean logginGetObjSql)  throws KrnException {
		SecurityContextHolder.getKernel().setLoggingGetObjSql(logginGetObjSql);
	}

	// Методы перенесенные из класса kz.tamur.or3ee.client.lang.ClientSystemWrp

	@Override
	public void showMessage(Object value) {
        showMessageDialog(new Frame("Сообщение"), INFORMATION_MESSAGE, String.valueOf(value));
	}

	@Override
	public void showErrorMessage(Object value) {
        showMessageDialog(new Frame("Ошибка!"), ERROR_MESSAGE, String.valueOf(value));
	}

	@Override
	public void showInfoMessage(Object value) {
        showMessageDialog(new Frame("Инфо!"), INFORMATION_MESSAGE, String.valueOf(value));
	}

	@Override
	public void showWarningMessage(Object value) {
        showMessageDialog(new Frame("Предупреждение!"), EXCLAMATION_MESSAGE, String.valueOf(value));
	}

	@Override
	public int showConfirmMessage(Object value) {
        return showMessageDialog(new Frame("Подтверждение"), QUESTION_MESSAGE, String.valueOf(value));
	}

	@Override
	public String requestPassword() {
        return showPasswordDialog((Frame) null, ENTER_PASSWORD_MESSAGE, "RU");
	}

	@Override
    public String sign(String text) {
        return sign(text, false);
    }
    
    @Override
    public String sign(String text, boolean newKey) {
        try {
            CryptoApplet a = new CryptoApplet();
            a.init("ru");

            String path = getKeyPath(newKey, a);
            
            if (path != null) {
	            String pd = getKeyPass(newKey);
	
	            String signed = a.getSignAndCert(text, 17, path, pd);
	
	            int b1 = signed.indexOf("sign=");
	            int b2 = signed.indexOf("&cert=");
	
	            String sign = signed.substring(b1 + 5, b2);
	            String cert = signed.substring(b2 + 6);
	            System.out.println(cert);
	            krn.setCert(cert);
	
	            return sign;
            }
        } catch (Exception e) {
        	e.printStackTrace();
            showMessageDialog(new Frame("Ошибка!"), ERROR_MESSAGE,
                    "Ошибка при проставлении подписи! Проверьте путь к ключевому файлу и правильность ввода пароля!");
        }
        return null;
    }
    
    /**
     * Выводит диалог выбора файла ключа.
     * 
     * @param newKey
     *            новый ключ?
     * @param a
     *            крипто апплет
     * @return путь к файлу ключа.
     * @throws Exception
     *             the exception
     */
    private synchronized String getKeyPath(boolean newKey, CryptoApplet a) throws Exception {
        if (keyPath == null || newKey) {
            String path = a.selectFile("p12", "Файлы ключей ЭЦП в формате PKCS12", keyPath);
            if (path != null && !path.startsWith("[ERROR:"))
            	keyPath = path;
            else
            	return null;
        }
        return keyPath;
    }

    /**
     * Ввод пароля для ключа.
     * 
     * @param newKey
     *            новый ключ?
     * @return пароль к файлу ключа.
     * @throws Exception
     *             the exception
     */
    private synchronized String getKeyPass(boolean newKey) throws Exception {
        if (keyPD == null || newKey) {
            final JPasswordField jpf = new JPasswordField();
            JOptionPane jop = new JOptionPane(jpf, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            JDialog dialog = jop.createDialog("Введите пароль");
            // слушатель для передачи фокуса компоненту ввода пароля
            jpf.addHierarchyListener(new HierarchyListener() {
                public void hierarchyChanged(HierarchyEvent e) {
                    final Component c = e.getComponent();
                    if (c.isShowing() && (e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                        Window toplevel = SwingUtilities.getWindowAncestor(c);
                        toplevel.addWindowFocusListener(new WindowAdapter() {
                            public void windowGainedFocus(WindowEvent e) {
                                c.requestFocus();
                            }
                        });
                    }
                }
            });
            // показать диалог
            dialog.setVisible(true);
            dialog.dispose();
            keyPD = ((Integer) jop.getValue() == JOptionPane.OK_OPTION) ? new String(jpf.getPassword()) : "";
        }
        return keyPD;
    }

    /**
     * Получить параметры ЭЦП.
     * 
     * @return ЭЦП параметры.
     */
    public Map<String, Object> getECPParams() {
        if (ecpParams == null || (Integer) ecpParams.get("storeType") == -1) {
            JFrame frm = (MainFrame) InterfaceManagerFactory.instance().getManager();
            ecpParams = KalkanUtil.getECPParams(frm, kz.tamur.comps.Utils.getCenterLocationPoint(400, 150), lastPath);
            if ((Integer) ecpParams.get("storeType") == 17 && ecpParams.get("storePath") != null) {
                lastPath = (String) ecpParams.get("storePath");
            }
        }
        return ecpParams;
    }

    /**
     * Очистить параметры ЭЦП.
     */
    public void clearECPParams() {
        ecpParams = null;
    }

    /**
     * Задать параметры ЭЦП.
     * 
     * @param params
     *            параметры ЭЦП.
     */
    public void setECPParams(Map<String, Object> params) {
        ecpParams = params;
    }
    
    /**
     * Проверить подпись.
     * 
     * @param str
     *            подписанный текст.
     * @param sign
     *            подпись.
     * @param cert
     *            сертификат.
     * @return результат проверки подписи.
     */
    public String checkSign(String str, String sign, File cert) {
    	String rootCertPath = Constants.ROOT_CERT_PATH != null ? Constants.ROOT_CERT_PATH : new File("rootCerts").getAbsolutePath(); 
        return checkSign(str, sign, cert, rootCertPath, Constants.OCSP_SERVICE_URL, Constants.PROXY_HOST,
        		Constants.PROXY_PORT);
    }

    /**
     * Проверить подпись.
     * 
     * @param str
     *            подписанный текст.
     * @param sign
     *            подпись.
     * @param cert
     *            сертификат.
     * @param rootStorePath
     *            путь к папке в которой лежат сертификаты корневого узла НУЦ для проверки что ваш сертификат выдан НУЦ.
     * @param ocspURL
     *            адрес сервиса OCSP центра сертификации для онлайн проверки статуса сертификата.
     * @param phost
     *            адрес прокси сервера если интернет через прокси, если нет то <code>null</code>
     * @param pport
     *            порт прокси сервера если интернет через прокси, если нет то <code>null</code>
     * @return результат проверки подписи.
     */
    public String checkSign(String str, String sign, File cert, String rootStorePath, String ocspURL, String phost, String pport) {
        try {
            X509Certificate c = KalkanUtil.getCertificate(cert);
            return checkSign(str, sign, c, rootStorePath, ocspURL, phost, pport);
        } catch (Exception e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return "Ошибка при проверке подписи";
    }

    /**
     * Проверить подпись.
     * 
     * @param str
     *            подписанный текст.
     * @param sign
     *            подпись.
     * @param cert
     *            сертификат.
     * @return результат проверки подписи.
     */
    public String checkSign(String str, String sign, byte[] cert) {
    	String rootCertPath = Constants.ROOT_CERT_PATH != null ? Constants.ROOT_CERT_PATH : new File("rootCerts").getAbsolutePath(); 
        return checkSign(str, sign, cert, rootCertPath, Constants.OCSP_SERVICE_URL, Constants.PROXY_HOST,
        		Constants.PROXY_PORT);
    }

    /**
     * Проверить подпись.
     * 
     * @param str
     *            подписанный текст.
     * @param sign
     *            подпись.
     * @param cert
     *            сертификат.
     * @param rootStorePath
     *            путь к папке в которой лежат сертификаты корневого узла НУЦ для проверки что ваш сертификат выдан НУЦ.
     * @param ocspURL
     *            адрес сервиса OCSP центра сертификации для онлайн проверки статуса сертификата.
     * @param phost
     *            адрес прокси сервера если интернет через прокси, если нет то <code>null</code>
     * @param pport
     *            порт прокси сервера если интернет через прокси, если нет то <code>null</code>
     * @return результат проверки подписи.
     */
    public String checkSign(String str, String sign, byte[] cert, String rootStorePath, String ocspURL, String phost, String pport) {
        try {
            X509Certificate c = KalkanUtil.getCertificate(cert);
            return checkSign(str, sign, c, rootStorePath, ocspURL, phost, pport);
        } catch (Exception e) {
        	SecurityContextHolder.getLog().error(e, e);
        }
        return "Ошибка при проверке подписи";
    }

    /**
     * Проверить подпись.
     * 
     * @param str
     *            подписанный текст.
     * @param sign
     *            подпись.
     * @param cert
     *            сертификат.
     * @param rootStorePath
     *            путь к папке в которой лежат сертификаты корневого узла НУЦ для проверки что ваш сертификат выдан НУЦ.
     * @param ocspURL
     *            адрес сервиса OCSP центра сертификации для онлайн проверки статуса сертификата.
     * @param phost
     *            адрес прокси сервера если интернет через прокси, если нет то <code>null</code>
     * @param pport
     *            порт прокси сервера если интернет через прокси, если нет то <code>null</code>
     * @return результат проверки подписи.
     */
    private String checkSign(String str, String sign, X509Certificate cert, String rootStorePath, String ocspURL, String phost,
            String pport) {
        try {
            CheckSignResult res = KalkanUtil.verifyPlainData(KalkanProvider.PROVIDER_NAME, str, sign, cert);
            if (!res.isDigiSignOK()) {
                return "ЭЦП неверная!";
            }

            switch (res.getCertError()) {
            case CheckSignResult.CERT_EXPIRED:
                return "Сертификат просрочен!";
            case CheckSignResult.CERT_NOT_YET_VALID:
                return "Сертификат еще не действует!";
            case CheckSignResult.CERT_SIGN_ERROR:
                return "Сертификат не подписан НУЦ!";
            case CheckSignResult.CERT_CRL_NO_ACCESS:
                return "Не доступен список отзыва сертификатов!";
            case CheckSignResult.CERT_REVOKED:
                return "Сертификат отозван!";
            case CheckSignResult.CERT_NOT_FOR_SIGN:
                return "Сертификат не может быть использован для подписи!";
            case CheckSignResult.CERT_OCSP_ERROR:
                return "Ошибка при вызове службы OCSP!";
            case CheckSignResult.CERT_OTHER_ERROR:
                return "Ошибка при проверке подписи!";
            }
            return "ЭЦП верна. Сертификат действителен.";
        } catch (Exception e) {
        	SecurityContextHolder.getLog().error(e, e);
        }
        return "Ошибка при проверке подписи";
    }
	    
	@Override
	public void log(SystemEvent event, String proc, String objName, String message) {
    	UserSessionValue us = SecurityContextHolder.getKernel().getUserSession();
		StringBuilder res = new StringBuilder(" | ").append(SystemEvent.TYPES[event.getTypeCode()]).append(" | ")
				.append(event.getName()).append(" | ").append(us.name).append(" | ").append(us.ip).append(" | ")
				.append(us.pcName).append(" | ").append(us.isAdmin ? 1 : 0).append(" | ").append(us.serverId)
				.append(" | ").append(proc).append(" | ").append(objName).append(" | ").append(message)
				.append(" | | "); // Добавляем пустые поля таблицы и колонки, иначе не будет писать в t_sys_log
		log.info(res.toString());
	}

	@Override
	public void writeLogRecord(String loggerName, String type, String event, String description) {
        try {
            krn.writeLogRecord(loggerName, type, event, description);
        } catch (KrnException e) {
        	SecurityContextHolder.getLog().error(e, e);
        }
	}

	@Override
	public List<String> findText(String text, int condition) {
		List<String> uidResults = new ArrayList<String>();
		try {			
			String inputString = Funcs.normalizeInput(text).trim();
			boolean[] searchArea = { true, false, false, false, false, false, false, false };
			int[] searchProperties = { 0, condition };
			List<Object> results = Kernel.instance().search(inputString, 1000, searchProperties, searchArea);
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
		} catch (KrnException e) {
			e.printStackTrace();
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
        ResourceBundle res = ResourceBundle.getBundle("kz.tamur.rt.RuntimeResources", loc);
        try {
        	kz.tamur.comps.Utils.getPolicyNode().getPolicyWrapper().checkPassAndLogin(password, userName, isAdmin, chekLogin);
        } catch (KrnException e) {
            String mess = "";
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
		krn.getout(user, message);
	}

	@Override
	public void sendMessage(KrnObject user, String message) throws KrnException {
		krn.sendMessage(user, message);
	}
	
	
	public KrnObject sendNotification(KrnObject user, String message, String uid, String cuid) throws KrnException {
		return krn.sendNotification(user, message, uid, cuid, -1);
	}
	
	public KrnObject sendNotification(KrnObject user, String message, String uid, String cuid, long trId) throws KrnException {
		return krn.sendNotification(user, message, uid, cuid, trId);
	}
	
	@Override
	public byte[] generateReport(long reportId, Object obj, KrnObject lang, String format) throws KrnException {
		KrnObject mainReport = krn.getObjectById(reportId, 0);
		return generateReport(mainReport, obj, lang, format);
	}

	@Override
	public byte[] generateReport(String reportUID, Object obj, KrnObject lang, String format) throws KrnException {
		KrnObject mainReport = krn.getObjectByUid(reportUID, 0);
		return generateReport(mainReport, obj, lang, format);
	}

	@Override
	public byte[] generateReport(KrnObject mainReport, Object obj, KrnObject lang, String format) throws KrnException {
		
		KrnClass reportCls = krn.getClassByName("ReportPrinter");
		KrnClass reportFolderCls = krn.getClassByName("ReportFolder");
		KrnAttribute configAttr = krn.getAttributeByName(reportCls, "config");
		KrnAttribute templateAttr = krn.getAttributeByName(reportCls, "template");
		KrnAttribute parentAttr = krn.getAttributeByName(reportCls, "parent");
		KrnAttribute childrenAttr = krn.getAttributeByName(reportFolderCls, "children");
		
        byte[] data = krn.getBlob(mainReport.id, configAttr, 0, 0, 0);

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
                		KrnClass utilCls = krn.getClassByName("ReportUtil");
						xmlData = (Element) utilCls.exec(utilCls, xmlMethod, args, new Stack<String>());
					} catch (Throwable e) {
						throw new KrnException("Ошибка при формировании xml данных для отчета", 0, e);
					}
                	// Шаблон основного отчета
                    byte[] jasperMain = krn.getBlob(mainReport.id, templateAttr, 0, lang.id, 0);
                    
                    if (jasperMain == null || jasperMain.length == 0) {
                    	throw new KrnException(0, "Не задан шаблон отчета " + mainReport.uid);
                    }
                	
                    Map<String, Object> params = new HashMap<>();

                    KrnObject[] parents = krn.getObjects(mainReport, parentAttr, new long[0], 0);
                    if (parents != null && parents.length > 0) {
                    	KrnObject parent = parents[0];
                    	KrnObject[] children = krn.getObjects(parent, childrenAttr, new long[0], 0);
                    	
                    	if (children != null && children.length > 0) {
                    		for (KrnObject childReport : children) {
                    			if (childReport.id != mainReport.id) {
	                    			byte[] config = krn.getBlob(childReport.id, configAttr, 0, 0, 0);
	
	                    	        is = new ByteArrayInputStream(config);
	                    	        is.close();
	                    	        builder = new SAXBuilder();
	
	                	            xml = builder.build(is).getRootElement();
	                	            macros = xml.getChild("macros");
	                	            
	                	            if (macros == null || macros.getText().trim().length() == 0) {
	                                    throw new KrnException(0, "Не задано название переменной для дочернего отчета");
	                                } else {
		                            	// Шаблон дочернего отчета
		                                byte[] jasperChild = krn.getBlob(childReport.id, templateAttr, 0, lang.id, 0);
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

	public boolean isProcessRunning(long id) throws Exception {
		return krn.isProcessRunning(id);
	}
}