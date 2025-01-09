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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.tamur.SecurityContextHolder;
import kz.tamur.comps.Constants;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.util.POIUtils;
import kz.tamur.util.PasswordService;
import kz.tamur.util.crypto.CheckSignResult;
import kz.tamur.util.crypto.KalkanUtil;
import kz.tamur.web.common.webgui.WebPanel;
import kz.tamur.web.component.WebFrame;
import kz.tamur.web.controller.WebController;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.UserSessionValue;

public class SystemOp extends kz.tamur.lang.SystemOp {

    private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + SystemOp.class.getName());
	
	@Override
	public void commit() throws Exception {}

	@Override
	public void rollback() throws Exception {}

	@Override
	public void rollbackLongTransaction(long id) throws Exception {}

	@Override
	public void commitLongTransaction(long id) throws Exception {}

	@Override
	public List<UserSessionValue> getUserSessions() throws Exception {
		return null;
	}

	@Override
	public void killUserSession(UUID usId, boolean blockUser) throws Exception {}

	@Override
	public String encrypt(String str) {
        return PasswordService.getInstance().encrypt(str);
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
	public List<Long> findProcessByUiType(String uiType) throws KrnException {
		return null;
	}

	@Override
	public List<Long> findForeignProcess(long proceeDefId, long cutObjId) throws KrnException {
		return null;
	}

	@Override
	public byte[] convertOfficeDocument(byte[] docData, String outputFormat) throws Exception {
		return null;
	}

	@Override
	public void sendNotification(UserSession from, Set<KrnObject> usersTo, Object data, int type) throws Exception {}
	
	@Override
	public void deleteNotification(KrnObject user, String uid, String cuid) throws Exception {}
	
	@Override
	public void deleteNotificationByUID(KrnObject user, String uid) throws Exception {}

	@Override
	public void sendNotification(UserSession from, Set<KrnObject> usersTo, Object data, int type, String title) throws Exception {}

	@Override
	public void sendMessage(UserSession from, Set<KrnObject> usersTo, String text) throws Exception {}

	@Override
	public boolean initServerTasks() throws Exception {
		return false;
	}

	@Override
	public String createProcedure(String name, List<String> params, String body) throws Exception {
		return null;
	}

	@Override
	public List execProcedure(String name) throws Exception {
		return null;
	}

	@Override
	public List execProcedure(String name, List<Object> vals) throws Exception {
		return null;
	}

	@Override
	public List execProcedure(String name, List<Object> vals, List<String> types_in, List<String> types_out) throws Exception {
		return null;
	}

	@Override
	public void setLoggingGetObjSql(boolean logginGetObjSql) throws KrnException {}

	@Override
	public void showMessage(Object value) {
        print(value);
        OrFrame frame = ClientOrLang.getFrame();
        if (frame instanceof WebFrame) {
            ((WebPanel) frame.getPanel()).setAlertMessage(String.valueOf(value), true);
        }
	}

	@Override
	public void showErrorMessage(Object value) {
        print(value);
        OrFrame frame = ClientOrLang.getFrame();
        if (frame instanceof WebFrame) {
            ((WebPanel) frame.getPanel()).setErrorMessage(String.valueOf(value), true);
        }
	}

	@Override
	public void showInfoMessage(Object value) {
        print(value);
        OrFrame frame = ClientOrLang.getFrame();
        if (frame instanceof WebFrame) {
            ((WebPanel) frame.getPanel()).setAlertMessage(String.valueOf(value), true);
        }
	}

	@Override
	public void showWarningMessage(Object value) {
        print(value);
        OrFrame frame = ClientOrLang.getFrame();
        if (frame instanceof WebFrame) {
            ((WebPanel) frame.getPanel()).setWarningMessage(String.valueOf(value), true);
        }
	}

	@Override
	public int showConfirmMessage(Object value) {
        print(value);
        OrFrame frame = ClientOrLang.getFrame();
        if (frame instanceof WebFrame) {
            return ((WebFrame) frame).confirm(String.valueOf(value));
        }
        return ButtonsFactory.BUTTON_NO;
	}

	@Override
    public String sign(String text) {
        return sign(text, false);
    }

    
    @Override
    public String sign(String text, boolean newKey) {
    	return sign(text, newKey, false);
    }
    
    public String sign(String text, boolean newKey, boolean auth) {
        OrFrame frame = ClientOrLang.getFrame();
        if (text != null && frame instanceof WebFrame) {
            return ((WebFrame) frame).signIola(text, newKey, auth);
        }
        return null;
    }
	
	@Override
	public String requestPassword() {
    	return requestPassword("Введите пароль:");
	}
	
	public String requestPassword(String msg) {
        OrFrame frame = ClientOrLang.getFrame();
        if (frame instanceof WebFrame) {
            return ((WebFrame) frame).askPassword(msg);
        }
        return null;
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
        return checkSign(str, sign, cert, WebController.ROOT_CERT_PATH, WebController.OCSP_SERVICE_URL, WebController.PROXY_HOST,
                WebController.PROXY_PORT);
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
        return checkSign(str, sign, cert, WebController.ROOT_CERT_PATH, WebController.OCSP_SERVICE_URL, WebController.PROXY_HOST,
                WebController.PROXY_PORT);
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
            CheckSignResult res = KalkanUtil.verifyPlainData(KalkanProvider.PROVIDER_NAME, str, sign, cert, false);
            if (!res.isOK())
                return res.getErrorMessage(false);

            return "ЭЦП верна. Сертификат действителен.";
        } catch (Exception e) {
        	SecurityContextHolder.getLog().error(e, e);
        }
        return "Ошибка при проверке подписи";
    }

    /**
     * Получить параметры ЭЦП.
     * 
     * @return ЭЦП параметры.
     */
    public Map<String, Object> getECPParams() {
        OrFrame frame = ClientOrLang.getFrame();
        if (frame instanceof WebFrame) {
            ((WebFrame) frame).getECPParams();
        }
        return null;
    }
    
    public List<Object> signTextWithNCA(String text) {
        return signTextWithNCA(text, true);
    }

    public List<Object> signTextWithNCA(String text, boolean newKey) {
        OrFrame frame = ClientOrLang.getFrame();
        if (text != null && frame instanceof WebFrame) {
            return ((WebFrame) frame).signTextWithNCA(text, newKey);
        }
        return null;
    }

    public File getFile() {
        OrFrame frame = ClientOrLang.getFrame();
        if (frame instanceof WebFrame) {
            return ((WebFrame) frame).getFile();
        }
        return null;
    }

    /**
     * Очистить параметры ЭЦП.
     */
    public void clearECPParams() {
        OrFrame frame = ClientOrLang.getFrame();
        if (frame instanceof WebFrame) {
            ((WebFrame) frame).clearECPParams();
        }
    }

    /**
     * Задать параметры ЭЦП.
     * 
     * @param params
     *            параметры ЭЦП.
     */
    public void setECPParams(Map<String, Object> params) {}
    
    public Map<String, Object> readIdCard() {
        OrFrame frame = ClientOrLang.getFrame();
        if (frame instanceof WebFrame) {
            return ((WebFrame) frame).readIdCard();
        }
        return null;
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
        OrFrame frame = ClientOrLang.getFrame();
        try {
            if (frame instanceof WebFrame)
                ((WebFrame) frame).getKernel().writeLogRecord(loggerName, type, event, description);
        } catch (KrnException e) {
            log.error(e, e);
        }
	}
	
	@Override
    public void loadApplet(String name) {
        OrFrame frame = ClientOrLang.getFrame();
        if (frame instanceof WebFrame) {
        	((WebFrame) frame).getSession().sendCommand("loadApplet", name);
        }
    }

	public void openDocument(String fileName, Object content) throws IOException {
        OrFrame frame = ClientOrLang.getFrame();
        if (frame instanceof WebFrame) {
            File tmpDir = WebController.WEB_DOCS_DIRECTORY;
            
            String fs = "";
            int beg = fileName.lastIndexOf('.');
            if (beg > -1) {
            	fs = fileName.substring(beg);
            	fileName = fileName.substring(0, beg);
            }
            
            File tmpFile = null;
            int i = 0;
            
            do {
            	tmpFile = new File(tmpDir, fileName + (i++ > 0 ? ("-" + i) : "") + fs);
            } while (!tmpFile.createNewFile());
            
            ((WebFrame)frame).getSession().deleteOnExit(tmpFile);
            
            if (content instanceof File) {
                Funcs.copy((File) content, tmpFile);
            } else if (content instanceof byte[]) {
                Funcs.write((byte[])content, tmpFile);
            }

        	((WebFrame) frame).getSession().sendCommand("openDocument", kz.tamur.web.common.Base64.encodeBytes(tmpFile.getName().getBytes()));
        }
    }
    
	public File mergeXls(List<Object> files) {
		return POIUtils.mergeXls(files);
	}

	public File convertToPdf(File file) {
		try {
			return POIUtils.convertToPDF(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public File convertToPdf(List<Object> files) {
		try {
			return POIUtils.convertToPDF(files);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public List<String> findText(String text, int condition) {
		List<String> uidResults = new ArrayList<String>();
		try {			
			String inputString = Funcs.normalizeInput(text).trim();
			boolean[] searchArea = { true, false, false, false, false, false,
					false, false };
			int[] searchProperties = { 0, condition };
			List<Object> results = ClientOrLang.getKernel().search(inputString, 1000,
					searchProperties, searchArea);
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
        ResourceBundle res = ResourceBundle.getBundle("kz.tamur.rt.RuntimeResourcesSrv", loc);
        try {
       	 	OrFrame frame = ClientOrLang.getFrame();
        	Utils.getPolicyNode(((WebFrame) frame).getKernel()).getPolicyWrapper().checkPassAndLogin(password, userName, isAdmin, chekLogin);
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
   	 	OrFrame frame = ClientOrLang.getFrame();
		((WebFrame) frame).getKernel().getout(user, message);
	}

	@Override
	public void sendMessage(KrnObject user, String message) throws KrnException {
   	 	OrFrame frame = ClientOrLang.getFrame();
		((WebFrame) frame).getKernel().sendMessage(user, message);
	}
	
	public boolean isProfileSaved() throws KrnException {
   	 	OrFrame frame = ClientOrLang.getFrame();
		String pd = ((WebFrame) frame).getSession().getProfilePassword();
		return pd != null && pd.length() > 0;
	}
	
	public boolean isNCAProfileSaved() throws KrnException {
   	 	OrFrame frame = ClientOrLang.getFrame();
		String pd = ((WebFrame) frame).getSession().getNCAProfilePassword();
		return pd != null && pd.length() > 0;
	}

	
	public KrnObject sendNotification(KrnObject user, String message, String uid, String cuid) throws KrnException {
   	 	OrFrame frame = ClientOrLang.getFrame();
		return ((WebFrame) frame).getKernel().sendNotification(user, message, uid, cuid);
	}
	
	public KrnObject sendNotification(KrnObject user, String message, String uid, String cuid, long trId) throws KrnException {
   	 	OrFrame frame = ClientOrLang.getFrame();
		return ((WebFrame) frame).getKernel().sendNotification(user, message, uid, cuid, trId);
	}
	
	@Override
	public byte[] generateReport(long reportId, Object obj, KrnObject lang, String format) throws KrnException {
		OrFrame frame = ClientOrLang.getFrame();
		Kernel krn = ((WebFrame) frame).getKernel();
		KrnObject mainReport = krn.getObjectById(reportId, 0);
		return generateReport(mainReport, obj, lang, format);
	}

	@Override
	public byte[] generateReport(String reportUID, Object obj, KrnObject lang, String format) throws KrnException {
		OrFrame frame = ClientOrLang.getFrame();
		Kernel krn = ((WebFrame) frame).getKernel();
		KrnObject mainReport = krn.getObjectByUid(reportUID, 0);
		return generateReport(mainReport, obj, lang, format);
	}

	@Override
	public byte[] generateReport(KrnObject mainReport, Object obj, KrnObject lang, String format) throws KrnException {
		OrFrame frame = ClientOrLang.getFrame();
		Kernel krn = ((WebFrame) frame).getKernel();
		
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
    	OrFrame frame = ClientOrLang.getFrame();
    	Kernel krn = ((WebFrame) frame).getKernel();
    	return krn.isProcessRunning(id);
    }
}