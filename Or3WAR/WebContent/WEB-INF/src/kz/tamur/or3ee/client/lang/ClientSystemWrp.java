package kz.tamur.or3ee.client.lang;

import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MAX_LOGIN;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MAX_PASS;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MIN_LOGIN;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MIN_PASS;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MIN_PASS_ADM;
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

import java.io.File;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.tamur.SecurityContextHolder;
import kz.tamur.comps.OrFrame;
import kz.tamur.comps.Utils;
import kz.tamur.guidesigner.ButtonsFactory;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.or3ee.common.lang.SystemWrp;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.util.POIUtils;
import kz.tamur.util.crypto.CheckSignResult;
import kz.tamur.util.crypto.KalkanUtil;
import kz.tamur.web.common.webgui.WebPanel;
import kz.tamur.web.component.WebFrame;
import kz.tamur.web.controller.WebController;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.UserSessionValue;

/**
 * The Class ClientSystemWrp.
 * 
 */
public class ClientSystemWrp extends SystemWrp {

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
}