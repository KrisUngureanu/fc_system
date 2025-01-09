package kz.tamur.or3ee.common.lang;

import java.io.File;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.List;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.gov.pki.kalkan.util.encoders.Base64;
import kz.tamur.SecurityContextHolder;
import kz.tamur.lang.EvalException;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.util.Funcs;
import kz.tamur.util.POIUtils;
import kz.tamur.util.crypto.CheckSignResult;
import kz.tamur.util.crypto.KalkanUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;

/**
 * Created by IntelliJ IDEA.
 * 
 * Date: 10.03.2006
 * Time: 11:30:17
 * 
 * @author daulet
 */
public abstract class SystemWrp {

	protected static Log log = LogFactory.getLog(SystemWrp.class);

	/**
     * Конструктор класса SystemWrp.
     */
    public SystemWrp() {
    }

    /**
     * Преобразование объекта в строку
     * 
     * @param obj
     *            объект.
     * @return строка, полученная преобразованием объекта.
     */
    public String toString(Object obj) {
        return obj.toString();
    }

    /**
     * Вывод в консоль.
     * 
     * @param value
     *            выводимое значение.
     */
    public void print(Object value) {
        SecurityContextHolder.getLog().info(String.valueOf(value));
    }

    public void debug(Object value) {
        SecurityContextHolder.getLog().debug(String.valueOf(value));
    }

    /**
     * Вывод в консоль.
     * 
     * @param value
     *            выводимое значение.
     */
    public void println(Object value) {
        print(value);
    }

    /**
     * Вывод в консоль переменной и её значения.
     * 
     * @param varName
     *            переменная.
     * @param value
     *            значение переменной.
     */
    public void printVar(Object varName, Object value) {
    	SecurityContextHolder.getLog().info(new StringBuilder().append(varName).append(" = ").append(value).toString());
    }

    /**
     * Показать сообщение.
     * 
     * @param value
     *            текст сообщения
     */
    public abstract void showMessage(Object value);

    /**
     * Показать сообщение об ошибке.
     * 
     * @param value
     *            текст сообщения
     */
    public abstract void showErrorMessage(Object value);
    
    /**
     * Инфо сообщение.
     * 
     * @param value
     *            текст сообщения
     */
    public abstract void showInfoMessage(Object value);
    
    /**
     * Показать сообщение об предупреждение.
     * 
     * @param value
     *            текст сообщения
     */
    public abstract void showWarningMessage(Object value);

    /**
     * Показать сообщение требования подтверждения действий.
     * 
     * @param value
     *            текст сообщения.
     * @return результат выбора пользователя.
     */
    public abstract int showConfirmMessage(Object value);

    /**
     * Верификация пароля на соответствие требования м политики паролей.
     * 
     * @param password
     *            пароль пользователя
     * @param userName
     *            логин пользователя
     * @param isAdmin
     *            пользователь администратор?
     * @param chekLogin
     *            необходима ли проверка логина?
     * @return если <code>null</code> то пароль валиден
     */
    public String checkPassAndLogin(String password, String userName, boolean isAdmin, boolean chekLogin) {
        return "Not realised";
    }

    /**
     * Верификация пароля на соответствие требования м политики паролей.
     * 
     * @param password
     *            пароль пользователя
     * @param userName
     *            логин пользователя
     * @param isAdmin
     *            пользователь администратор?
     * @param chekLogin
     *            необходима ли проверка логина?
     * @param lang
     *            Язык на котором должны выдаваться сообщения
     * @return если <code>null</code> то пароль валиден
     */
    public String checkPassAndLogin(String password, String userName, boolean isAdmin, boolean chekLogin, KrnObject lang) {
        return "Not realised";
    }

    /**
     * Получить подпись текста.
     * 
     * @param text
     *            текст для подписи
     * @return подпись
     */
    public String sign(String text) {
        return "Not realised";
    }

    /**
     * Получить подпись текста.
     * 
     * @param text
     *            текст для подписи
     * @param newKey
     *            новый ключ?
     * @return подпись
     */
    public String sign(String text, boolean newKey) {
        return "Not realised";
    }

    /**
     * Получить полное DN-имя владельца.
     * 
     * @param cert
     *            сертификат.
     * @return DN-имя
     */
    public String getUserDN(String cert) {
        try {
            X509Certificate c = KalkanUtil.getCertificate(Base64.decode(cert));
            return c.getSubjectDN().getName();
        } catch (Exception e) {
        	SecurityContextHolder.getLog().error(e, e);
            return "Ошибка при проверке подписи!";
        }
    }

    /**
     * Проверить подпись.
     * 
     * @param text
     *            подписанный текст.
     * @param sign
     *            подпись.
     * @param cert
     *            сертификат.
     * @param isForAuth
     *            проверить сертификат на возможность авторизации или возможность ЭЦП.
     * @param rootStorePath
     *             путь к папке в которой лежат сертификаты корневого узла НУЦ для проверки что ваш сертификат выдан НУЦ.
     * @param ocspURL
     *             адрес сервиса OCSP центра сертификации для онлайн проверки статуса сертификата.
     * @param phost
     *            адрес прокси сервера если интернет через прокси, если нет то <code>null</code>
     * @param pport
     *            порт прокси сервера если интернет через прокси, если нет то <code>null</code>
     * @return результат проверки сертификата <code>-1</code> при некорректных данных или ошибке
     */
    public int checkSign(String text, String sign, String cert, String rootStorePath, String ocspURL, String phost, String pport) {
    	return checkSign(text, sign, cert, false, rootStorePath, ocspURL, phost, pport);
    }

    /**
     * Проверить подпись.
     * 
     * @param text
     *            подписанный текст.
     * @param sign
     *            подпись.
     * @param cert
     *            сертификат.
     * @param isForAuth
     *            проверить сертификат на возможность авторизации или возможность ЭЦП.
     * @param rootStorePath
     *             путь к папке в которой лежат сертификаты корневого узла НУЦ для проверки что ваш сертификат выдан НУЦ.
     * @param ocspURL
     *             адрес сервиса OCSP центра сертификации для онлайн проверки статуса сертификата.
     * @param phost
     *            адрес прокси сервера если интернет через прокси, если нет то <code>null</code>
     * @param pport
     *            порт прокси сервера если интернет через прокси, если нет то <code>null</code>
     * @return результат проверки сертификата <code>-1</code> при некорректных данных или ошибке
     */
    public int checkSign(String text, String sign, String cert, boolean isForAuth, String rootStorePath, String ocspURL, String phost, String pport) {
        try {
            X509Certificate c = KalkanUtil.getCertificate(Base64.decode(cert));
            CheckSignResult res = KalkanUtil.verifyPlainData(KalkanProvider.PROVIDER_NAME, text, sign, c);
            if (!res.isDigiSignOK()) {
                return -1;
            }
            return res.getCertError();
        } catch (Exception e) {
        	SecurityContextHolder.getLog().error(e, e);
            return -1;
        }
    }

    public CheckSignResult verifySign(String text, String sign, byte[] cert) {
        CheckSignResult res = null;
        try {
            X509Certificate c = KalkanUtil.getCertificate(cert);
        	res = KalkanUtil.verifyPlainData(KalkanProvider.PROVIDER_NAME, text.getBytes("UTF-8"), sign.getBytes("UTF-8"), c);
        } catch (Exception e) {
            res = new CheckSignResult();
            res.setCertError(CheckSignResult.CERT_OTHER_ERROR);
            e.printStackTrace();
        }
        return res;
    }

    public CheckSignResult verifySign(String text, String sign, File cert) {
        CheckSignResult res = null;
        try {
            X509Certificate c = KalkanUtil.getCertificate(cert);
        	res = KalkanUtil.verifyPlainData(KalkanProvider.PROVIDER_NAME, text.getBytes("UTF-8"), sign.getBytes("UTF-8"), c);
        } catch (Exception e) {
            res = new CheckSignResult();
            res.setCertError(CheckSignResult.CERT_OTHER_ERROR);
            e.printStackTrace();
        }
        return res;
    }

    public CheckSignResult checkCertificate(String p12FileName, String password, boolean auth) throws IOException {
        return KalkanUtil.checkCertificate(p12FileName, password, auth);
    }

    public CheckSignResult checkCertificate(File certFile, boolean auth) throws IOException {
        byte[] certBytes = Funcs.read(certFile);
        return KalkanUtil.checkCertificate(certBytes, auth);
    }

    public CheckSignResult checkCertificate(byte[] certBytes, boolean auth) {
        return KalkanUtil.checkCertificate(certBytes, auth);
    }

    public CheckSignResult checkCertificate(byte[] p12Bytes, String password, boolean auth) throws IOException {
        return KalkanUtil.checkCertificate(p12Bytes, password, auth);
    }
    
    public CheckSignResult checkCertificate(File certFile, String password, boolean auth) throws IOException {
        byte[] certBytes = Funcs.read(certFile);
        return KalkanUtil.checkCertificate(certBytes, password, auth);
    }
    
    public CheckSignResult verifyPKCS7(String text, byte[] pkcs7) {
        return verifyPKCS7(text, pkcs7, false);
    }
    
    public CheckSignResult verifyPKCS7(String text, byte[] pkcs7, boolean auth) {
        CheckSignResult res = null;
        try {
            res = KalkanUtil.verifyPkcs7(text, new String(Base64.encode(pkcs7)), auth);
        } catch (Exception e) {
            res = new CheckSignResult();
            res.setCertError(CheckSignResult.ECP_DAMAGED);
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Запросить ввод пароля.
     * 
     * @return введённый пароль.
     */
    public abstract String requestPassword();

    /**
     * Транслитерация русского текста.
     * 
     * @param text
     *            исходный текст
     * @return транслит текста
     */
    public String toLatin(String text) {
        return Funcs.translite(text);
    }

    public abstract void log(SystemEvent event, String proc, String objName, String message);

    public abstract void writeLogRecord(String loggerName, String type, String event, String description);

	public void log(String proc, String objName, String message) {
		log(SystemEvent.EVENT_DEBUG_MESSAGE, proc, objName, message);
	}

	public void log(String message) {
		log(SystemEvent.EVENT_DEBUG_MESSAGE, "", "", message);
	}

	public void log(Throwable e) {
		SecurityContextHolder.getLog().error(e, e);
	}

    public void loadApplet(String name) {
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
	
	 /**
	 * Поиск уидов по тексту в OR3 lang
	 * condition: 0 - любое из этих слов, 1 - целиком, 2 - без учета порядка 3 - регулярное выражение
	 * @param text
	 * @param condition
	 * @return
	 */
	public abstract List<String> findText(String text, int condition);
	
	public abstract void getout(KrnObject user, String message) throws KrnException;
	public abstract void sendMessage(KrnObject user, String message) throws KrnException;
}