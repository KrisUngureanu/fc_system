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
import java.io.File;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

import kz.crypto.CryptoApplet;
import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.tamur.SecurityContextHolder;
import kz.tamur.comps.Constants;
import kz.tamur.comps.Utils;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.or3ee.common.lang.SystemWrp;
import kz.tamur.rt.InterfaceManagerFactory;
import kz.tamur.rt.MainFrame;
import kz.tamur.util.Funcs;
import kz.tamur.util.crypto.CheckSignResult;
import kz.tamur.util.crypto.KalkanUtil;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.UserSessionValue;

/**
 * The Class ClientSystemWrp.
 * 
 */
public class ClientSystemWrp extends SystemWrp {

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

    /**
     * Конструктор класса ClientSystemWrp.
     * 
     * @param krn
     *            the krn
     */
    public ClientSystemWrp(Kernel krn) {
        this.krn = krn;
    }
    
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
       	 	Utils.getPolicyNode().getPolicyWrapper().checkPassAndLogin(password, userName, isAdmin, chekLogin);
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
}