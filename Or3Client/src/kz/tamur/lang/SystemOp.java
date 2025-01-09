package kz.tamur.lang;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.imageio.ImageIO;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.tamur.SecurityContextHolder;
import kz.tamur.comps.Constants;
import kz.tamur.or3.util.SystemEvent;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.Utils;
import kz.tamur.util.Base64;
import kz.tamur.util.Funcs;
import kz.tamur.util.POIUtils;
import kz.tamur.util.crypto.CheckSignResult;
import kz.tamur.util.crypto.KalkanUtil;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleDocxExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.json.JSONException;
import org.json.JSONObject;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.UserSessionValue;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.sun.mail.smtp.SMTPTransport;

public abstract class SystemOp {

	/**
     * Фиксация транзакции.
     * 
     * @throws Exception
     *             the exception
     */
    public abstract void commit() throws Exception;

    /**
     * Откат транзакции.
     * 
     * @throws Exception
     *             the exception
     */
    public abstract void rollback() throws Exception;

    /**
     * Откат заданной транзакции.
     * 
     * @param id
     *            номер транзакции для отката.
     * @throws Exception
     *             the exception
     */
    public abstract void rollbackLongTransaction(long id) throws Exception;

    /**
     * Фиксация заданной транзакции.
     * 
     * @param id
     *            номер фиксируемой транзакции.
     * @throws Exception
     *             the exception
     */
    public abstract void commitLongTransaction(long id) throws Exception;

    // Управление пользовательскими сессиями
    /**
     * Получить список пользовательских сессий.
     * 
     * @return список пользовательских сессий
     * @throws Exception
     *             the exception
     */
    public abstract List<UserSessionValue> getUserSessions() throws Exception;

    /**
     * Отключить сессию пользователя.
     * 
     * @param usId
     *            идентификатор сессии
     * @param blockUser
     *            блокировать пользователя?
     * @throws Exception
     *             the exception
     */
    public abstract void killUserSession(UUID usId, boolean blockUser) throws Exception;

    /**
     * Взять SHA-хэш строки.
     * 
     * @param str
     *            исходная строка.
     * @return хэш строки
     */
    public abstract String encrypt(String str);

    /**
     * Проверить, удовлетворяет ли пароль политике безопасности.
     * 
     * @param user пользователь, которому назначается пароль
     * @param str пароль
     * @param name имя пользователя
     * @param admin админ?
     * @param isLogged входил уже?
     * @param psw предыдущие пароли
     * @param lastChangeTime дата изменения пароля
     *
     * @return сообщения об ошибке при проверке пароля, либо null - если все нормально
     */
    public abstract String verifyPassword(KrnObject user, String str, String name, boolean admin, boolean isLogged, String psw, Object lastChangeTime);

    /**
     * Получить список потоков по типу интерфейса.
     * 
     * @param uiType
     *            тип интерфейса:Полноэкранный,Диалог,Выбор,Отчет.
     * @return массив идентификаторов потоков <code>long</code>.
     * @throws Exception
     *             the exception
     */
    public abstract List<Long> findProcessByUiType(String uiType) throws KrnException;

    /**
     * Получить список потоков по типу процесса и обрабатываемому объекту.
     * 
     * @param proceeDefId идентификатор описания процесса.
     * @param cutObjId идентификатор обрабатываемого объекта
     * @return массив идентификаторов потоков <code>long</code>.
     * @throws Exception
     *             the exception
     */
    public abstract List<Long> findForeignProcess(long proceeDefId,long cutObjId) throws KrnException;
   /**
     * Прочитать файл.
     * 
     * @param fileName
     *            полное имя файла.
     * @return массив <code>byte[]</code> из файла.
     * @throws Exception
     *             the exception
     */
    public byte[] readFile(String fileName) throws Exception {
    	return Funcs.read(fileName);
    }

    /**
     * Прочитать файл.
     * 
     * @param url
     *            URL файла.
     * @return массив <code>byte[]</code> из файла.
     * @throws Exception
     *             the exception
     */
    public byte[] readUrlFile(String url) throws Exception {
        URL u = new URL(url);
        InputStream is = u.openStream();
        byte[] res = Funcs.readStream(is, Constants.MAX_DOC_SIZE);
        is.close();
        return res;
    }

    /**
     * Преобразовать массив байт в строку с минимальной длиной 32 символа.
     * Под каждый байт массива выделяется 2 байта строки
     * 
     * @param data
     *            данные - массив байт.
     * @return полученная строка.
     */
    public String getHexString(byte[] data) {
        return String.format("%032x", new BigInteger(1, data));
    }
    
    public byte[] fromHexString(String hexStr) {
        byte[] res = new byte[hexStr.length()/2];
        for (int i = 0; i < hexStr.length(); i+=2) {
        	String hex = hexStr.substring(i, i+2);
        	int bval = Integer.valueOf(hex, 16);
        	if (bval >= 128) bval -= 256;;
        	res[i/2] = (byte)bval;
        }
        return res;
    }

    /**
     * Сравнение хэш сумм.
     * 
     * @param hash
     *            сравниваеваемый хеш.
     * @param value
     *            данные, хэш которых будет сравниваться.
     * @return <code>true</code>, в случае равенства хэшей.
     * @throws Exception
     *             the exception
     */
    public boolean checkHash(String hash, byte[] value) throws Exception {
        return hash.equals(getHash(value));
    }

    /**
     * Получить хэш сумму файла.
     * 
     * @param value
     *            файл.
     * @return хэш сумма файла.
     * @throws Exception
     *             the exception
     */
    public String getHash(File value) throws Exception {
        byte[] b = readFile(value.getAbsolutePath());
        return getHash(b);
    }

    /**
     * Получить хэш сумму массива байт.
     * 
     * @param value
     *            данные.
     * @return хэш сумма.
     * @throws Exception
     *             the exception
     */
    public String getHash(byte[] value) throws Exception {
        String res = "";
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.update(Utils.getSalt()); // уязвимость
            algorithm.reset();
            algorithm.update(value);
            byte messageDigest[] = algorithm.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String hex = Integer.toHexString(0xFF & messageDigest[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            res = hexString.toString();
        } catch (NoSuchAlgorithmException nsae) {

        }
        return res;
    }

    /**
     * Записать данные в файл.
     * 
     * @param data
     *            данные для записи.
     * @param fileName
     *            полное имя файла.
     * @throws Exception
     *             the exception
     */
    public void write(byte[] data, String fileName) throws Exception {
        FileOutputStream os = new FileOutputStream(Funcs.getCanonicalFile(fileName));
        os.write(data);
        os.close();
    }

    /**
     * Отправка файла по указанному адресу.
     * 
     * @param data
     *            файл для отправки.
     * @param urlStr
     *            адрес для отправки.
     * @return <code>true</code>, в случае успеха отправки.
     * @throws Exception
     *             the exception
     */
	public static boolean postData(File data, String urlStr) throws Exception {
		boolean res = false;
		data = Funcs.getCanonicalFile(data);
		if (data != null && data.length() < Constants.MAX_DOC_SIZE) {
			HttpURLConnection urlc = null;
			URL endpoint = new URL(urlStr);
			try {
				urlc = (HttpURLConnection) endpoint.openConnection();
				try {
					urlc.setRequestMethod("POST");
				} catch (ProtocolException e) {
					throw new Exception("Shouldn't happen: HttpURLConnection doesn't support POST??", e);
				}
				urlc.setDoOutput(true);
				urlc.setDoInput(true);
				urlc.setUseCaches(false);
				urlc.setAllowUserInteraction(false);
				urlc.setRequestProperty("Content-type", "text/xml; charset=UTF-8");
				OutputStream out = urlc.getOutputStream();
				try {
					InputStream is = new FileInputStream(data);
					Funcs.writeStream(is, out, Constants.MAX_DOC_SIZE);
					is.close();
					out.close();
					urlc.getResponseMessage();
					res = true;
				} catch (IOException e) {
					throw new Exception("IOException while posting data", e);
				} finally {
					if (out != null) {
						out.close();
					}
				}
			} catch (IOException e) {
				throw new Exception("Connection error (is server running at " + endpoint + " ?): " + e);
			} finally {
				if (urlc != null)
					urlc.disconnect();
			}
		}
		return res;
	}

	/**
	 * Вызов сервиса по указанному адресу.
	 * 
	 * @param data
	 *            данные для отправки.
	 * @param headers
	 *            заголовки запроса.
	 * @param urlStr
	 *            адрес для отправки.
	 * @return массив:код результата обработки,xml в виде массива байт,заголовки
	 *         ответа.
	 * @throws Exception
	 *             the exception
	 */
	public static Object[] postData(byte[] data, Map<String, String> headers, String urlStr) {
		return postData(data, headers, urlStr,null,null); 
		
	}
	/**
	 * Вызов сервиса по указанному адресу.
	 * 
	 * @param data
	 *            данные для отправки.
	 * @param headers
	 *            заголовки запроса.
	 * @param urlStr
	 *            адрес для отправки.
	 * @param username
	 *            имя пользователя.
	 * @param pd
	 *            пароль.
	 * @return массив:код результата обработки,xml в виде массива байт,заголовки
	 *         ответа.
	 * @throws Exception
	 *             the exception
	 */
	public static Object[] postData(byte[] data, Map<String, String> headers, String urlStr, String username, String pd) {
		HttpURLConnection urlc = null;
		Object[] res = new Object[4];
		try {
			URL endpoint = new URL(urlStr);
			urlc = (HttpURLConnection) endpoint.openConnection();
			try {
				urlc.setRequestMethod("POST");
			} catch (ProtocolException e) {
				e.printStackTrace();
				throw new Exception("Shouldn't happen: HttpURLConnection doesn't support POST??", e);
			}
			urlc.setDoOutput(true);
			urlc.setDoInput(true);
			urlc.setUseCaches(false);
			urlc.setAllowUserInteraction(false);
			if (username != null && pd != null){
				String userPD = username + ":" + pd;
				String encoding = Base64.encodeBytes(userPD.getBytes());
				urlc.setRequestProperty("Authorization", "Basic " + encoding);
			}

			for (String key : headers.keySet()) {
				urlc.setRequestProperty(key, headers.get(key));
			}
			OutputStream out = urlc.getOutputStream();
			try {
				out.write(data);
				out.close();
				res[0] = "" + urlc.getResponseCode();
			} catch (IOException e) {
				throw new Exception("IOException while posting data", e);
			} finally {
				if (out != null)
					out.close();
			}
			InputStream ierr = null;
			try {
				ierr = urlc.getErrorStream();
				if (ierr != null) {
					res[1] = Funcs.readStream(ierr, Constants.MAX_DOC_SIZE);
					if(res[1] instanceof byte[]){
						res[1]= new String((byte[])res[1],"UTF-8");
					}
					res[2] = urlc.getHeaderFields();
				}
			} catch (Throwable e) {
				
			} finally {
				if (ierr != null)
					ierr.close();
			}
			if(!"500".equals(res[0])){
				InputStream in = urlc.getInputStream();
				try {
					if (in != null) {
						res[1] = Funcs.readStream(in, Constants.MAX_DOC_SIZE);
					}
					res[2] = urlc.getHeaderFields();
				} catch (IOException e) {
					throw new Exception("IOException while reading response", e);
				} finally {
					if (in != null)
						in.close();
				}
			}
		} catch (IOException e) {
			res[0] = "-1;Connection error (is server running at " + urlStr + " ?): " + e.getMessage();
			res[3] = e;
		} catch (Throwable e) {
			res[0] = "-1;" + e.getMessage();
			res[3] = e;
		} finally {
			if (urlc != null) {
				urlc.disconnect();
			}
		}
		return res;
	}

	/**
	 * Отправка массива байт по указанному адресу.
	 * 
	 * @param data
	 *            данные для отправки.
	 * @param urlStr
	 *            адрес для отправки.
	 * @return <code>true</code>, в случае успеха отправки.
	 * @throws Exception
	 *             the exception
	 */
	public static boolean postData(byte[] data, String urlStr) throws Exception {
		HttpURLConnection urlc = null;
		URL endpoint = new URL(urlStr);
		boolean res = false;
		try {
			urlc = (HttpURLConnection) endpoint.openConnection();
			try {
				urlc.setRequestMethod("POST");
			} catch (ProtocolException e) {
				throw new Exception("Shouldn't happen: HttpURLConnection doesn't support POST??", e);
			}
			urlc.setDoOutput(true);
			urlc.setDoInput(true);
			urlc.setUseCaches(false);
			urlc.setAllowUserInteraction(false);
			urlc.setRequestProperty("Content-type", "text/xml; charset=" + "UTF-8");
			OutputStream out = urlc.getOutputStream();
			try {
				out.write(data);
				out.close();
				urlc.getResponseMessage();
				res = true;
			} catch (IOException e) {
				throw new Exception("IOException while posting data", e);
			} finally {
				if (out != null)
					out.close();
			}
		} catch (IOException e) {
			throw new Exception("Connection error (is server running at " + endpoint + " ?): " + e);
		} finally {
			if (urlc != null)
				urlc.disconnect();
		}
		return res;
	}

	/**
	 * Отправка xml-элемента по указанному адресу.
	 * 
	 * @param data
	 *            xml-элемент для отправки.
	 * @param urlStr
	 *            адрес для отправки.
	 * @return <code>true</code>, в случае успеха отправки.
	 * @throws Exception
	 *             the exception
	 */
	public static Element postData(Element data, String urlStr) throws Exception {
		HttpURLConnection urlc = null;
		URL endpoint = new URL(urlStr);
		Element res = null;
		try {
			urlc = (HttpURLConnection) endpoint.openConnection();
			try {
				urlc.setRequestMethod("POST");
			} catch (ProtocolException e) {
				throw new Exception("Shouldn't happen: HttpURLConnection doesn't support POST??", e);
			}
			urlc.setDoOutput(true);
			urlc.setDoInput(true);
			urlc.setUseCaches(false);
			urlc.setAllowUserInteraction(false);
			urlc.setRequestProperty("Content-type", "text/xml; charset=" + "UTF-8");
			OutputStream out = urlc.getOutputStream();
			try {
				// Format fmt=Format.getCompactFormat();
				XMLOutputter opr = new XMLOutputter();
				data.detach();
				Writer writer = new OutputStreamWriter(out, "UTF-8");
				opr.output(data, writer);
				writer.flush();
				writer.close();
				out.close();
			} catch (IOException e) {
				throw new Exception("IOException while posting data", e);
			} finally {
				if (out != null)
					out.close();
			}
			InputStream in = urlc.getInputStream();
			try {
				SAXBuilder builder = new SAXBuilder();
				Document doc = builder.build(in);
				in.close();
				res = doc.getRootElement();
			} catch (IOException e) {
				throw new Exception("IOException while reading response", e);
			} finally {
				if (in != null)
					in.close();
			}
		} catch (IOException e) {
			throw new Exception("Connection error (is server running at " + endpoint + " ?): " + e);
		} finally {
			if (urlc != null)
				urlc.disconnect();
		}
		return res;
	}

	/**
	 * Создать <i>читателя</i> для массива байт.
	 * 
	 * @param file
	 *            массив данных.
	 * @param enc
	 *            кодировка, например 'UTF-8'.
	 * @return объект <code>BufferedReader</code>.
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public BufferedReader createReader(byte[] file, String enc) throws IOException {
		return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(file), enc));
	}

	/**
	 * Создать <i>читателя</i> для массива байт.
	 * 
	 * @param file
	 *            имя файла.
	 * @param enc
	 *            кодировка, например 'UTF-8'.
	 * @return объект <code>BufferedReader</code>.
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public BufferedReader createReader(String fileName, String enc) throws IOException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(fileName), enc));
	}

	/**
	 * Создать <i>писателя</i> данных.
	 * 
	 * @param fileName
	 *            имя файла для записи.
	 * @param enc
	 *            кодировка, например 'UTF-8'
	 * @return <i>писатель</i> данных.
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public BufferedWriter createWriter(String fileName, String enc) throws IOException {
		return createWriter(fileName, enc, false);
	}

	/**
	 * Создать <i>писателя</i> данных.
	 * 
	 * @param fileName
	 *            имя файла для записи.
	 * @param enc
	 *            кодировка, например 'UTF-8'
	 * @param append
	 *            дозапись?
	 * @return <i>писатель</i> данных.
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public BufferedWriter createWriter(String fileName, String enc, boolean append) throws IOException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, append), enc));
	}

	/**
	 * Копирование файла. Если файл-копия существует и <code>append=true</code>
	 * данные из файла источника допишуться к файлу копии.
	 * 
	 * @param src
	 *            имя файла-источника
	 * @param dst
	 *            имя копии файла
	 * @param append
	 *            дозапись?
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void copyFile(String src, String dst, boolean append) throws IOException {
		Funcs.copy(src, dst);
	}

	/**
	 * Получить изображение заданных размеров.
	 * 
	 * @param imageData
	 *            изображение.
	 * @param width
	 *            ширина.
	 * @param height
	 *            высота.
	 * @param format
	 *            формат изображения.
	 * @return отмаштабированное изображениев в виде массива байт.
	 * @throws Exception
	 *             the exception
	 */
	public byte[] getScaledImage(byte[] imageData, int width, int height, String format) throws Exception {
	    BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageData));
		if (img != null) {
			float s = (width > 0 && height > 0) ? Math.min((float) width / img.getWidth(), (float) height / img.getHeight()) : Math.max((float) width / img.getWidth(), (float) height / img.getHeight());
			int nw = Math.round(s * img.getWidth());
			int nh = Math.round(s * img.getHeight());
			BufferedImage res = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
			res.createGraphics().drawImage(img.getScaledInstance(nw, nh, Image.SCALE_SMOOTH), width > 0 ? (width - nw) / 2 : 0, height > 0 ? (height - nh) / 2 : 0, null);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(res, format, os);
			os.close();
			return os.toByteArray();
		}
		return null;
	}
	
	public byte[] getScaledImage2(byte[] imageData, int width, int height, String format) throws Exception {
		BufferedImage src = ImageIO.read(new ByteArrayInputStream(imageData));
		BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = dest.createGraphics();
		AffineTransform at = AffineTransform.getScaleInstance((double) width / src.getWidth(), (double) height / src.getHeight());
		g.drawRenderedImage(src, at);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(dest, format, baos);
		baos.close();
		return baos.toByteArray();
	}
	
	/**
	 * Преобразовать докумет MS Office.
	 * 
	 * @param docData
	 *            документ(массив байт).
	 * @param outputFormat
	 *            необходимый формат документа.
	 * @return byte[] полученных документ.
	 * @throws Exception
	 *             the exception
	 */
	public abstract byte[] convertOfficeDocument(byte[] docData, String outputFormat) throws Exception;

	/**
	 * Отправить уведомление.
	 * 
	 * @param from
	 *            от кого (сессия пользователя).
	 * @param usersTo
	 *            кому Krn-объекты пользователей.
	 * @param data
	 *            данные уведомления(текст).
	 * @param type
	 *            тип сообщения.
	 * @throws Exception
	 *             the exception
	 */
	public abstract void sendNotification(UserSession from, Set<KrnObject> usersTo, Object data, int type)
			throws Exception;
	
	public abstract void deleteNotification(KrnObject user, String uid, String cuid) throws Exception;
	
	public abstract void deleteNotificationByUID(KrnObject user, String uid) throws Exception;

	/**
	 * Отправить уведомление.
	 * 
	 * @param from
	 *            от кого (сессия пользователя).
	 * @param usersTo
	 *            кому Krn-объекты пользователей.
	 * @param data
	 *            данные уведомления(текст).
	 * @param type
	 *            тип сообщения.
	 * @param title
	 *            заголовок сообщения.
	 * @throws Exception
	 *             the exception
	 */
	public abstract void sendNotification(UserSession from, Set<KrnObject> usersTo, Object data, int type, String title)
			throws Exception;

	/**
	 * Отправить сообщение.
	 * 
	 * @param from
	 *            от кого (сессия пользователя).
	 * @param usersTo
	 *            кому Krn-объекты пользователей.
	 * @param text
	 *            текст сообщения.
	 * @throws Exception
	 *             the exception
	 */
	public abstract void sendMessage(UserSession from, Set<KrnObject> usersTo, String text) throws Exception;

	/**
	 * upload a file to a FTP server
	 * 
	 * @param FTPADDR,
	 *            адрес ФТП сервера
	 * @param user,
	 *            логин к фтп
	 * @param pd,
	 *            пароль к фтп
	 * @param PathOnFtp,
	 *            путь к файлу на фтп - например /upload/touch.dat
	 * @param FilenameOnLocalMachine,
	 *            путь к файлу на локальной машине - например C:/somefile.txt
	 */
	public static void uploadFileOnFtp(String FTPADDR, int port, String user, String pd, String PathOnFtp,
			String FilenameOnLocalMachine) {
		FTPSClient client = new FTPSClient();

		FileInputStream fis = null;

		try {
			if (port > 0)
				client.connect(FTPADDR, port);
			else
				client.connect(FTPADDR);
			client.login(user, pd);

			// client.changeWorkingDirectory("upload");

			// Create an InputStream of the file to be uploaded
			File f = Funcs.getCanonicalFile(FilenameOnLocalMachine);
			fis = new FileInputStream(f);

			// Store file to server
			//
			client.storeFile(PathOnFtp, fis);
			client.logout();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
				client.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Download File from FTP
	 * 
	 * @param FTPADDR,
	 *            адрес ФТП сервера
	 * @param user,
	 *            логин к фтп
	 * @param pd,
	 *            пароль к фтп
	 * @param FullPathToPutFile
	 *            - полный путь к файлу на локальной машине, куда будем его
	 *            сохранять
	 * @param FilenameOnFTP
	 *            - имя файла на фтп (функция ищет файл в папке /upload/)
	 */
	public static void downLoadFileFromFTP(String FTPADDR, String user, String pd, String FullPathToPutFile,
			String FilenameOnFTP) {

		FTPClient client = new FTPClient();
		FileOutputStream fos = null;

		try {
			client.connect(FTPADDR);
			client.login(user, pd);

			// The remote filename to be downloaded.

			File f = Funcs.getCanonicalFile(FullPathToPutFile);
			fos = new FileOutputStream(f);

			// Download file from FTP server

			client.retrieveFile("/upload/" + FilenameOnFTP, fos);
			//

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				client.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Delete File From FTP
	 * 
	 * @param FTPADDR,
	 *            адрес ФТП сервера
	 * @param user,
	 *            логин к фтп
	 * @param pd,
	 *            пароль к фтп
	 * @param Filename,
	 *            путь к файлу (например /upload/touch.dat)
	 */

	public static void deleteFileOnFtp(String FTPADDR, String user, String pd, String Filename) {
		FTPClient client = new FTPClient();
		try {
			client.connect(FTPADDR);
			client.login(user, pd);

			//
			// Delete file on the FTP server. When the FTP delete complete
			// it returns true.
			//
			String filename = Filename;
			boolean deleted = client.deleteFile(filename);
			if (deleted) {
				System.out.println("File deleted...");
			}

			client.logout();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				client.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * upload a file to a SFTP server
	 * 
	 * @param SFTPADDR,
	 *            адрес сервера
	 * @param SFTPLOGIN,
	 *            логин
	 * @param SFTPPD,
	 *            пароль
	 * @param SFTPDESTINATION,
	 *            путь к файлу - например /upload/touch.dat
	 * @param SFTPSOURCE,
	 *            путь к файлу на локальной машине - например C:/somefile.txt
	 */
	public void upLoadFileOnSFtp(String SFTPHOST, int SFTPPORT, String SFTPLOGIN, String SFTPPD,
			String SFTPDESTINATION, String SFTPSOURCE) {
		FileInputStream is = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;

		try {
			is = new FileInputStream(Funcs.getCanonicalFile(SFTPSOURCE));
			JSch jsch = new JSch();
			session = jsch.getSession(SFTPLOGIN, SFTPHOST, SFTPPORT);
			session.setPassword(SFTPPD);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channelSftp = (ChannelSftp) channel;
			channelSftp.connect();
			// channelSftp.put(is, SFTPDESTINATION);
			bos = new BufferedOutputStream(channelSftp.put(SFTPDESTINATION));
			bis = new BufferedInputStream(is);
			Funcs.writeStream(bis, bos, Constants.MAX_ARCHIVED_SIZE);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			Utils.closeQuietly(bos);
			Utils.closeQuietly(bis);
			Utils.closeQuietly(is);
			if (channel != null) {
				channel.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
	}

	/**
	 * dounload a file from a SFTP server
	 * 
	 * @param SFTPADDR,
	 *            адрес сервера
	 * @param SFTPLOGIN,
	 *            логин
	 * @param SFTPPD,
	 *            пароль
	 * @param SFTPSOURCE,
	 *            путь к катологу на сервере - например /upload
	 * @param SFTPDESTINATION,
	 *            путь к файлу на локальной машине - например C:/somefile.txt
	 * @param SFTPFILE,
	 *            имя файла - например somefile.txt
	 */
	public void downLoadFileOnSFtp(String SFTPHOST, int SFTPPORT, String SFTPLOGIN, String SFTPPD,
			String SFTPSOURCE, String SFTPDESTINATION, String SFTPFILE) {
		FileOutputStream os = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;

		try {
			JSch jsch = new JSch();
			/*
			 * jsch.setLogger(new Logger() { public void log(int level, String
			 * message) { System.out.println(level + " - " + message); }
			 * 
			 * public boolean isEnabled(int level) { return true; } });
			 */

			session = jsch.getSession(SFTPLOGIN, SFTPHOST, SFTPPORT);
			session.setPassword(SFTPPD);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			channelSftp.cd(SFTPSOURCE);
			bis = new BufferedInputStream(channelSftp.get(SFTPFILE));
			File newFile = Funcs.getCanonicalFile(SFTPDESTINATION);
			os = new FileOutputStream(newFile);
			bos = new BufferedOutputStream(os);
			Funcs.writeStream(bis, bos, Constants.MAX_ARCHIVED_SIZE);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			Utils.closeQuietly(bis);
			Utils.closeQuietly(bos);
			Utils.closeQuietly(os);
			if (channel != null) {
				channel.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
	}

	/**
	 * remove a file from a SFTP server
	 * 
	 * @param SFTPADDR,
	 *            адрес сервера
	 * @param SFTPLOGIN,
	 *            логин
	 * @param SFTPPD,
	 *            пароль
	 * @param SFTPSOURCE,
	 *            путь к катологу на сервере - например /upload
	 * @param SFTPFILE,
	 *            имя файла - например somefile.txt
	 */
	public void removeFileOnSFtp(String SFTPHOST, int SFTPPORT, String SFTPLOGIN, String SFTPPD,
			String SFTPSOURCE, String SFTPFILE) {
		FileInputStream is = null;
		Session session = null;
		Channel channel = null;
		ChannelSftp channelSftp = null;

		try {
			JSch jsch = new JSch();
			session = jsch.getSession(SFTPLOGIN, SFTPHOST, SFTPPORT);
			session.setPassword(SFTPPD);
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			channel = session.openChannel("sftp");
			channel.connect();
			channelSftp = (ChannelSftp) channel;
			channelSftp.cd(SFTPSOURCE);
			channelSftp.rm(SFTPFILE);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (channel != null) {
				channel.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
	}

	public boolean zipFiles(String zipFile, Map<String, byte[]> dataFiles, boolean isAdd) {
		return zipFiles(zipFile, dataFiles, isAdd, null);
	}

	public boolean zipFiles(String zipFile, Map<String, byte[]> dataFiles, boolean isAdd, String charSet) {
		FileOutputStream fout = null;
		ZipOutputStream zout = null;
		List<String> fc = null;
		if (charSet == null)
			charSet = "UTF-8";
		if (dataFiles != null && dataFiles.size() > 0)
			try {
				ZipFile zFile = null;
				File file = Funcs.getCanonicalFile(zipFile);
				File newFile = null;
				Enumeration<? extends ZipEntry> entries = null;
				if (isAdd && file.exists()) {
					String f_name = zipFile.substring(0, zipFile.lastIndexOf(".")) + ".tmp";
					file.renameTo(newFile = Funcs.getCanonicalFile(f_name));
				}
				fout = new FileOutputStream(Funcs.getCanonicalFile(zipFile));
				zout = new ZipOutputStream(fout);
				zout.setEncoding(charSet);
				if (newFile != null) {
					fc = new Vector<String>();
					zFile = new ZipFile(newFile.getName(), charSet);
					entries = zFile.getEntries();
					while (entries.hasMoreElements()) {
						ZipEntry entry = (ZipEntry) entries.nextElement();
						fc.add(entry.getName());
						InputStream is = zFile.getInputStream(entry);
						zout.putNextEntry(entry);

						Funcs.writeStream(is, zout, Constants.MAX_ARCHIVED_SIZE);
						is.close();
					}
					zFile.close();
					newFile.delete();
				}
				for (String fName : dataFiles.keySet()) {
					byte[] data = dataFiles.get(fName);
					if (data == null || data.length == 0)
						continue;
					String fName_ = fName;
					if (fc != null && fc.contains(fName_)) {
						int i = 0;
						int ind = fName_.lastIndexOf(".");
						String fileName_ = fName_;
						while (fc.contains(fileName_)) {
							fileName_ = fName_.substring(0, ind) + "_" + i + fName_.substring(ind, fName_.length());
							i++;
						}
						fName_ = fileName_;
					}
					ZipEntry ze = new ZipEntry(fName_);// Имя файла - имя файла
														// в архиве
					zout.putNextEntry(ze);
					ByteArrayInputStream bis = new ByteArrayInputStream(dataFiles.get(fName));
					// отправка данных в поток zout
					Funcs.writeStream(bis, zout, Constants.MAX_ARCHIVED_SIZE);
					zout.closeEntry();
					bis.close();
				}
				return true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				Utils.closeQuietly(zout);
				Utils.closeQuietly(fout);
			}
		return false;
	}

	public boolean zipFiles(String zipFile, String fileName, byte[] dataFile, boolean isAdd) {
		return zipFiles(zipFile, fileName, dataFile, isAdd, null);
	}

	public boolean zipFiles(String zipFile, String fileName, byte[] dataFile, boolean isAdd, String charSet) {
		FileOutputStream fout = null;
		ZipOutputStream zout = null;
		List<String> fc = null;
		if (charSet == null)
			charSet = "UTF-8";
		if (dataFile != null && dataFile.length > 0)
			try {
				ZipFile zFile = null;
				int lind = zipFile.lastIndexOf(File.separator);
				if (lind < 0)
					lind = zipFile.lastIndexOf("/");
				if (lind > 0) {
					File dir = Funcs.getCanonicalFile(zipFile.substring(0, lind));
					if (!dir.exists())
						dir.mkdirs();
				}
				File file = Funcs.getCanonicalFile(zipFile);
				File newFile = null;
				Enumeration<? extends ZipEntry> entries = null;
				if (isAdd && file.exists()) {
					String f_name = zipFile.substring(0, zipFile.lastIndexOf(".")) + ".tmp";
					file.renameTo(newFile = Funcs.getCanonicalFile(f_name));
				}
				fout = new FileOutputStream(Funcs.getCanonicalFile(zipFile));
				zout = new ZipOutputStream(fout);
				zout.setEncoding(charSet);
				if (newFile != null) {
					fc = new Vector<String>();
					zFile = new ZipFile(newFile.getAbsolutePath(), charSet);
					entries = zFile.getEntries();
					while (entries.hasMoreElements()) {
						ZipEntry entry = (ZipEntry) entries.nextElement();
						fc.add(entry.getName());
						InputStream is = zFile.getInputStream(entry);
						zout.putNextEntry(entry);

						Funcs.writeStream(is, zout, Constants.MAX_ARCHIVED_SIZE);
						is.close();
					}
					zFile.close();
					newFile.delete();
				}
				if (fc != null && fc.contains(fileName)) {
					int i = 0;
					int ind = fileName.lastIndexOf(".");
					String fileName_ = fileName;
					while (fc.contains(fileName_)) {
						fileName_ = fileName.substring(0, ind) + "_" + i + fileName.substring(ind, fileName.length());
						i++;
					}
					fileName = fileName_;
				}
				ZipEntry ze = new ZipEntry(fileName);// Имя файла - имя файла в
														// архиве
				zout.putNextEntry(ze);
				ByteArrayInputStream bis = new ByteArrayInputStream(dataFile);
				// отправка данных в поток zout
				Funcs.writeStream(bis, zout, Constants.MAX_ARCHIVED_SIZE);
				zout.closeEntry();
				bis.close();
				return true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				Utils.closeQuietly(zout);
				Utils.closeQuietly(fout);
			}
		return false;
	}

	public boolean zipFiles(String zipFile, List<String> pathFiles, boolean excludePath, boolean deleteFile) {
		return zipFiles(zipFile, pathFiles, excludePath, deleteFile, null);
	}

	public boolean zipFiles(String zipFile, List<String> pathFiles, boolean excludePath, boolean deleteFile,
			String charSet) {
		FileOutputStream fout = null;
		ZipOutputStream zout = null;
		if (charSet == null)
			charSet = "UTF-8";
		if (pathFiles.size() > 0)
			try {
				fout = new FileOutputStream(Funcs.getCanonicalFile(zipFile));
				zout = new ZipOutputStream(fout);
				zout.setEncoding(charSet);
				for (String pathFile : pathFiles) {
					File file = Funcs.getCanonicalFile(pathFile);
					if (file.isDirectory()) {
						File[] files = file.listFiles();
						for (File file_ : files) {
							String fileName = excludePath ? file.getName() : (pathFile + "/" + file.getName());// полный
																												// путь
																												// или
																												// только
																												// имя
							ZipEntry ze = new ZipEntry(fileName);// Имя файла -
																	// имя файла
																	// в архиве
							zout.putNextEntry(ze);
							FileInputStream fis = new FileInputStream(file_);

							Funcs.writeStream(fis, zout, Constants.MAX_ARCHIVED_SIZE);
							// отправка данных в поток zout
							zout.closeEntry();
							fis.close();
							if (deleteFile)
								file_.delete();
						}
					} else {
						String fileName = excludePath ? file.getName() : pathFile;// полный
																					// путь
																					// или
																					// только
																					// имя
						ZipEntry ze = new ZipEntry(fileName);// Имя файла - имя
																// файла в
																// архиве
						zout.putNextEntry(ze);
						FileInputStream fis = new FileInputStream(file);
						Funcs.writeStream(fis, zout, Constants.MAX_ARCHIVED_SIZE);
						// отправка данных в поток zout
						zout.closeEntry();
						fis.close();
						if (deleteFile)
							file.delete();
					}
				}
				return true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				Utils.closeQuietly(zout);
				Utils.closeQuietly(fout);
			}
		return false;
	}

	public List<String> getEntryFiles(String pathZipFile) {
		List<String> res = new Vector<String>();
		ZipFile zipFile;
		try {
			zipFile = new ZipFile(pathZipFile);
			Enumeration<? extends ZipEntry> entries = zipFile.getEntries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				res.add(entry.getName());
			}
			zipFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public static void unzipFiles(String zipFilename, List<String> filenames, String outdir) {

		ZipFile zipFile;
		try {
			zipFile = new ZipFile(zipFilename);
			Enumeration<? extends ZipEntry> entries = zipFile.getEntries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();

				for (String filename : filenames) {
					if (entry.getName().equals(filename)) {
						InputStream zipin = zipFile.getInputStream(entry);
						BufferedOutputStream fileout = new BufferedOutputStream(
								new FileOutputStream(Funcs.getCanonicalFile(outdir + File.separator + filename)));

						Funcs.writeStream(zipin, fileout, Constants.MAX_ARCHIVED_SIZE);

						zipin.close();
						fileout.flush();
						fileout.close();
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static boolean sendMail(String host, String port, String from, List<String> tos, String subject, String text,
			List<String> filePaths) {
		return sendMail(host, port, from, tos, null, null, subject, text, filePaths);
	}

	public static boolean sendMail(String host, String port, String from, List<String> tos, String username,
			String pd, String subject, String text, List<String> filePaths) {
		// Get system properties
		Properties properties = new Properties();
		// Setup mail server
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.socketFactory.port", port);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.port", port);
		javax.mail.Session session = javax.mail.Session.getDefaultInstance(properties);
		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			if (tos == null || tos.size() == 0)
				return false;

			for (String to : tos) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			}

			// Set Subject: header field
			message.setSubject(subject);

			// Create the message part
			BodyPart messageBodyPart = new MimeBodyPart();

			// Fill the message
			messageBodyPart.setText(text);

			// Create a multipar message
			Multipart multipart = new MimeMultipart();

			// Set text message part
			multipart.addBodyPart(messageBodyPart);
			if (filePaths != null && filePaths.size() > 0) {
				// Part two is attachment
				for (String filePath : filePaths) {
					File file = Funcs.getCanonicalFile(filePath);
					if (file.exists()) {
						messageBodyPart = new MimeBodyPart();
						DataSource source = new FileDataSource(file);
						messageBodyPart.setDataHandler(new DataHandler(source));
						messageBodyPart.setFileName(file.getName());
						multipart.addBodyPart(messageBodyPart);
					}
				}
			}

			// Send the complete message parts
			message.setContent(multipart);

			SMTPTransport t = (SMTPTransport) session.getTransport("smtps");
			try {
				t.connect(host, username, pd);
				t.sendMessage(message, message.getAllRecipients());
			} finally {
				t.close();
			}
			return true;
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Запуск планировщика.
	 * 
	 */
	public abstract boolean initServerTasks() throws Exception;

	/**
	 * Создание процедуры.
	 * 
	 * @param name
	 *            имя процедуры
	 * @param params
	 *            список параметров
	 * @param body
	 *            тело процедуры
	 */
	public abstract String createProcedure(String name, List<String> params, String body) throws Exception;

	/**
	 * Выполнение процедуры.
	 * 
	 * @param name
	 *            имя процедуры
	 */
	public abstract List execProcedure(String name) throws Exception;

	/**
	 * Выполнение процедуры.
	 * 
	 * @param name
	 *            имя процедуры
	 * @param params
	 *            список значений
	 */
	public abstract List execProcedure(String name, List<Object> vals) throws Exception;

	/**
	 * Выполнение процедуры.
	 * 
	 * @param name
	 *            имя процедуры
	 * @param params
	 *            список значений
	 * @param types_in
	 *            типы входных параметров
	 * @param types_out
	 *            типы выходных параметров
	 */
	public abstract List execProcedure(String name, List<Object> vals, List<String> types_in, List<String> types_out)
			throws Exception;

	public byte[] sendGetRequest(String endpoint) {
		byte[] result = null;
		if (endpoint.startsWith("http://")) {
			// Send a GET request to the servlet
			try {
				URL url = new URL(endpoint);
				URLConnection conn = url.openConnection();

				// Get the response
				InputStream in = conn.getInputStream();
				try {
					if (in != null) {
						result = Funcs.readStream(in, Constants.MAX_DOC_SIZE);
					}
				} catch (IOException e) {
					throw new Exception("IOException while reading response", e);
				} finally {
					if (in != null)
						in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public abstract void setLoggingGetObjSql(boolean logginGetObjSql) throws KrnException;
	
	/**
	 * Запрос возвращающий org.json.JSONObject.
	 * 
	 * @param url строка запроса
	 */
	public static JSONObject readUrlJSONObject(final String url) {
		try (final InputStream is = new URL(url).openStream(); final BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));) {
			final StringBuilder sb = new StringBuilder();
			String str = "";
			while ((str = rd.readLine()) != null) {
				sb.append(str);
			}
			final JSONObject json = new JSONObject(sb.toString());
			return json;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Методы перенесенные из класса kz.tamur.or3ee.common.lang.SystemWrp
	
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
            X509Certificate c = KalkanUtil.getCertificate(kz.gov.pki.kalkan.util.encoders.Base64.decode(cert));
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
            X509Certificate c = KalkanUtil.getCertificate(kz.gov.pki.kalkan.util.encoders.Base64.decode(cert));
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
            res = KalkanUtil.verifyPkcs7(text, new String(kz.gov.pki.kalkan.util.encoders.Base64.encode(pkcs7)), auth);
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

    public void loadApplet(String name) {}

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
	
	public KrnObject sendNotification(KrnObject user, String message, String uid, String cuid, int row) throws KrnException {return null;}
	
	// JasperReports
	public byte[] generateReport(Element dataElement, File jasperFile, Map<String, Object> params, String format) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLOutputter out = new XMLOutputter();
        out.getFormat().setEncoding("UTF-8");
        out.output(dataElement, os);
        os.close();
        
        InputStream is = new FileInputStream(jasperFile);
        try {
        	return generateReport(os.toByteArray(), is, params, format);
        } finally {
        	is.close();
        }
	}
	
	public byte[] generateReport(Element dataElement, byte[] jasperFileBytes, Map<String, Object> params, String format) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLOutputter out = new XMLOutputter();
        out.getFormat().setEncoding("UTF-8");
        out.output(dataElement, os);
        os.close();
        
        InputStream is = new ByteArrayInputStream(jasperFileBytes);
        try {
        	return generateReport(os.toByteArray(), is, params, format);
        } finally {
        	is.close();
        }
	}

	public byte[] generateReport(byte[] data, InputStream jasperStream, Map<String, Object> params, String format) throws Exception {
		List<InputStream> jasperStreams = new ArrayList<>();
		jasperStreams.add(jasperStream);
		return generateReport(data, jasperStreams, params, format);
	}
	
	public byte[] generateReport(byte[] data, List<InputStream> jasperStreams, Map<String, Object> params, String format) throws Exception {
        JRXmlDataSource xmlDs = new JRXmlDataSource(new ByteArrayInputStream(data));
        xmlDs.next();
        org.w3c.dom.Document doc = xmlDs.subDocument();
        xmlDs.close();
        
        for (String key : params.keySet()) {
        	Object param = params.get(key);
        	
        	if (param instanceof File) {
        		params.put(key, JRLoader.loadObject(new FileInputStream((File)param)));
    		} else if (param instanceof byte[]) {
       		 	params.put(key, JRLoader.loadObject(new ByteArrayInputStream((byte[])param)));
        	}
        }
        
        params.put("XML_DATA_DOCUMENT", doc);
        
		JasperPrint jprint1 = JasperFillManager.fillReport(jasperStreams.get(0), params);
		for (int i = 1; i < jasperStreams.size(); i++) {
			JasperPrint jprint = JasperFillManager.fillReport(jasperStreams.get(i), params);
			for (JRPrintPage page : jprint.getPages()) {
				jprint1.addPage(page);
			}
		}
		
        List<File> tempFiles = new ArrayList<>();
        
        try {
			switch (format) {
				case "XML":
					File resXml = Funcs.createTempFile("REP_", ".xml", Constants.DOCS_DIRECTORY);
					tempFiles.add(resXml);
					JasperExportManager.exportReportToXmlFile(jprint1, resXml.getAbsolutePath(), true);
					byte[] byteXml = Funcs.read(resXml);
					return byteXml;
				case "HTML":
					File resHtml = Funcs.createTempFile("REP_", ".html", Constants.DOCS_DIRECTORY);
					tempFiles.add(resHtml);
					JasperExportManager.exportReportToHtmlFile(jprint1, resHtml.getAbsolutePath());
					byte[] byteHtml = Funcs.read(resHtml);
					return byteHtml;
				case "DOCX":
					ByteArrayOutputStream baosDocx = new ByteArrayOutputStream();
					SimpleDocxExporterConfiguration docxConfiguration = new SimpleDocxExporterConfiguration();
					
					JRDocxExporter exporterDocx = new JRDocxExporter();
					exporterDocx.setExporterInput(new SimpleExporterInput(jprint1));
					exporterDocx.setExporterOutput(new SimpleOutputStreamExporterOutput(baosDocx));
					exporterDocx.setConfiguration(docxConfiguration);
					exporterDocx.exportReport();
					baosDocx.close();
					
					byte[] byteDocx = baosDocx.toByteArray();
					return byteDocx;
				case "XLSX":
					SimpleXlsxReportConfiguration xlsxConfiguration = new SimpleXlsxReportConfiguration();
					xlsxConfiguration.setOnePagePerSheet(false);
					xlsxConfiguration.setIgnoreGraphics(false);
					ByteArrayOutputStream baosXlsx = new ByteArrayOutputStream();

					JRXlsxExporter exporterXlsx = new JRXlsxExporter();
					exporterXlsx.setExporterInput(new SimpleExporterInput(jprint1));
					exporterXlsx.setExporterOutput(new SimpleOutputStreamExporterOutput(baosXlsx));
					exporterXlsx.setConfiguration(xlsxConfiguration);
					exporterXlsx.exportReport();
					baosXlsx.close();

					byte[] byteXlsx = baosXlsx.toByteArray();
					return byteXlsx;
				default:
					return JasperExportManager.exportReportToPdf(jprint1);
			}

	    } finally {
	    	for (File f : tempFiles) {
	    		try {
	    			f.delete();
	    		} catch (Exception e) {
	    		}
	    	}
	    }
	}
	
	public byte[] generateReport(String reportUID, KrnObject obj, KrnObject lang) throws KrnException {
		return generateReport(reportUID, obj, lang, "PDF");
	}
	
	public abstract byte[] generateReport(long reportId, Object obj, KrnObject lang, String format) throws KrnException;
	public abstract byte[] generateReport(String reportUID, Object obj, KrnObject lang, String format) throws KrnException;
	public abstract byte[] generateReport(KrnObject report, Object obj, KrnObject lang, String format) throws KrnException;
}