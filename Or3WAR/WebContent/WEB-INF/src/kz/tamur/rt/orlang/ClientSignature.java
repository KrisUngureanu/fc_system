package kz.tamur.rt.orlang;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.kernel.KrnDate;

import java.util.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import kz.gov.pki.kalkan.util.encoders.Base64;
import kz.tamur.comps.Constants;
import kz.tamur.comps.OrFrame;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.orlang.ClientOrLang;
import kz.tamur.util.Funcs;
import kz.tamur.util.crypto.KalkanUtil;
import kz.tamur.web.component.WebFrame;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 28.09.2006
 * Time: 19:31:38
 * To change this template use File | Settings | File Templates.
 */
public class ClientSignature {
    private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ClientSignature.class.getName());
    public static String SECURITY_PROPERTIES_FILE = "security.properties";
    private KeyStore store;
    private boolean initialized = false;

    public ClientSignature() {
        initialized = false;
        //init();
    }

    public void init() {
        Properties ps = new Properties();
        try {
            File cf = new File(SECURITY_PROPERTIES_FILE);
            String filePath = null, pd = null;
            if (cf.exists()) {
                ps.load(new FileInputStream(cf));
                filePath = ps.getProperty("certfile");
                pd = ps.getProperty("password");
                //log.debug("KeyStore successfully initialized!");
            }
            if (filePath == null) filePath = "doc.keystore";
            if (pd == null) pd = "бббббб";

            store = KeyStore.getInstance("jks");
            File f = new File(filePath);
            if (f.exists()) {
                FileInputStream fis = new FileInputStream(filePath);
                store.load(fis, pd.toCharArray());
                fis.close();
                initialized = true;
            }
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    public String sign(String text, String alias, String pass) {
        try {
            if (!initialized) init();
            PrivateKey key = (PrivateKey) store.getKey(alias, pass.toCharArray());
            X509Certificate cert = (X509Certificate) store.getCertificate(alias);

            Signature signature = Signature.getInstance(cert.getSigAlgName());
            signature.initSign(key);

            signature.update(text.getBytes("UTF-8"));

            return new String(Base64.encode(signature.sign()));
        } catch (Exception e) {
            log.error(e, e);
        }
        return null;
    }

    public boolean verify(String text, String sign, String alias) {
        try {
            if (!initialized) init();
            X509Certificate cert = (X509Certificate) store.getCertificate(alias);
            Signature signature = Signature.getInstance(cert.getSigAlgName());
            signature.initVerify(cert.getPublicKey());

            signature.update(text.getBytes("UTF-8"));

            return signature.verify(Base64.decode(sign));
        } catch (Exception e) {
            log.error(e, e);
        }
        return false;
    }

    public File sign(File file, String alias, String pass) {
        try {
            if (!initialized) init();
            PrivateKey key = (PrivateKey) store.getKey(alias, pass.toCharArray());
            X509Certificate cert = (X509Certificate) store.getCertificate(alias);

            Signature signature = Signature.getInstance(cert.getSigAlgName());
            signature.initSign(key);

            byte[] text = getBytes(file);
            signature.update(text);

            byte[] data = Base64.encode(signature.sign());

            File f = null;
            if (data.length > 0) {
                try {
                    f = Funcs.createTempFile("sign", null);
                    f.deleteOnExit();
                    FileOutputStream os = new FileOutputStream(f);
                    os.write(data);
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return f;
        } catch (Exception e) {
            log.error("Ошибка при подписывании файла!");
        }
        return null;
    }

    public boolean verify(File file, File sign, String alias) {
        try {
            if (!initialized) init();
            X509Certificate cert = (X509Certificate) store.getCertificate(alias);
            Signature signature = Signature.getInstance(cert.getSigAlgName());
            signature.initVerify(cert.getPublicKey());

            byte[] text = getBytes(file);
            byte[] signText = getBytes(sign);
            signature.update(text);

            return signature.verify(Base64.decode(signText));
        } catch (Exception e) {
            log.error("Ошибка при верификации подписи!");
        }
        return false;
    }

    public byte[] getBytes(File f) {
        if (!initialized) init();
        byte[] val = null;
        try {
            int len = (int)f.length();
            if (len < Constants.MAX_DOC_SIZE) {
                InputStream inp = new FileInputStream(f);
                ByteArrayOutputStream bos = new ByteArrayOutputStream(len);
            	Funcs.writeStream(inp, bos, Constants.MAX_DOC_SIZE);
                inp.close();
                bos.close();
                val = bos.toByteArray();
            } else
            	throw new IOException("Превышен допустимый размер документа: " + Constants.MAX_DOC_SIZE);

        } catch (IOException e) {
            log.error(e, e);
        }
        return val;
    }
    
    // Подключаемся к Вебсокет для работы с ЭЦП УЦГО
    public boolean connectUcgoWebsocket() {
        OrFrame frame = ClientOrLang.getFrame();
        if (frame instanceof WebFrame) {
            return ((WebFrame) frame).connectUcgoWebsocket();
        }
        return false;
    }

/*    // Получаем список подключенных к компьютеру токенов
    public List<Map<String, Object>> getConnectedTokens() {
        OrFrame frame = ClientOrLang.getFrame();
        if (frame instanceof WebFrame) {
            String tokens = ((WebFrame) frame).getConnectedTokens();
            JsonObject tokensJson = JsonObject.readFrom(tokens);
        }
        return false;
    }
*/

    public String generateUcgoPKCS10(String dn) {
    	return generateUcgoPKCS10(dn, false);
    }
    
    public String generateUcgoPKCS10(String dn, boolean auth) {
        OrFrame frame = ClientOrLang.getFrame();
        if (dn != null && frame instanceof WebFrame) {
            return ((WebFrame) frame).generateUcgoPKCS10(dn, auth);
        }
        return null;
    }

    public String createPKCS7(String content) {
    	return createPKCS7(content, false);
    }
    
    public String createPKCS7(String content, boolean auth) {
        OrFrame frame = ClientOrLang.getFrame();
        if (content != null && frame instanceof WebFrame) {
            try {
				return ((WebFrame) frame).generateUcgoPKCS7(new String(Base64.encode(content.getBytes("UTF-8"))), auth);
			} catch (UnsupportedEncodingException e) {
			}
        }
        return null;
    }

    public String createPKCS7(byte[] content) {
    	return createPKCS7(content, false);
    }

    public String createPKCS7(byte[] content, boolean auth) {
        OrFrame frame = ClientOrLang.getFrame();
        if (content != null && frame instanceof WebFrame) {
            return ((WebFrame) frame).generateUcgoPKCS7(new String(Base64.encode(content)), auth);
        }
        return null;
    }

    public String saveUcgoCertificate(String cert, String reader, String uid) {
        return saveUcgoCertificate(cert, reader, uid, null);
    }

    public String saveUcgoCertificate(String cert, String reader, String uid, String tokPD) {
        OrFrame frame = ClientOrLang.getFrame();
        if (cert != null && frame instanceof WebFrame) {
            return ((WebFrame) frame).saveUcgoCertificate(cert, reader, uid, tokPD);
        }
        return null;
    }

    public String haveUcgoCertificate(String reader, String uid) {
        return haveUcgoCertificate(reader, uid, null);
    }

    public String haveUcgoCertificate(String reader, String uid, String tokPD) {
        OrFrame frame = ClientOrLang.getFrame();
        if (frame instanceof WebFrame) {
            return ((WebFrame) frame).haveUcgoCertificate(reader, uid, tokPD);
        }
        return null;
    }
    
    public String selectUcgoCertificate() {
    	return selectUcgoCertificate(null, null);
    }

    public String selectUcgoCertificate(String iin) {
    	return selectUcgoCertificate(iin, null);
    }
    
    public String selectUcgoCertificate(String iin, String bin) {
        OrFrame frame = ClientOrLang.getFrame();
        if (frame instanceof WebFrame) {
            return ((WebFrame) frame).selectUcgoCertificate(iin, bin);
        }
        return null;
    }
    
    public String deleteUcgoCertificate(String keyName) {
        OrFrame frame = ClientOrLang.getFrame();
        if (frame instanceof WebFrame) {
            return ((WebFrame) frame).deleteUcgoCertificate(keyName);
        }
        return null;
    }

    public String createUcgoRevokeRequest(File cert) {
    	try {
    		cert = cert.getCanonicalFile();
    		if (cert.length() < Constants.MAX_IMAGE_SIZE) {
        		FileInputStream fis = new FileInputStream(cert);
        		byte[] b = Funcs.readStream(fis, Constants.MAX_IMAGE_SIZE);
        		fis.close();
        		return createUcgoRevokeRequest(b);
    		}
    	} catch (Exception e) {
    		log.error(e, e);
    	}
    	return null;
    }
    
    public String createUcgoRevokeRequest(byte[] cert) {
    	try {
    		CertificateFactory cf = CertificateFactory.getInstance("X.509");
    		X509Certificate c = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(Base64.decode(cert)));
    		
    		String dn = c.getSubjectDN().toString();
    		String[] dnArray = dn.split(",");
    		
    		String iin = "", bin = "", email = "";
    		
            for (int i = 0; i < dnArray.length; i++) {
                String[] uzel = dnArray[i].trim().split("=");
                if (uzel[0].equals("SERIALNUMBER")) {
                    iin = uzel[1].substring(uzel[1].length() - 12);
                } else if (uzel[0].equals("OU")) {
                    bin = uzel[1].substring(uzel[1].length() - 12);
                } else if (uzel[0].equals("E")) {
                    email = uzel[1];
                }
            }

			return Funcs.normalizeInput(new String(cert) + "|col|" + dn + "|col|" + iin + "|col|" + bin + "|col|" + email + "|col|" + dn);
    	} catch (Exception e) {
    		log.error(e, e);
    	}
    	return null;
    }

    public Map<String, Object> getCertificateInfo(byte[] cert) {
    	try {
    		CertificateFactory cf = CertificateFactory.getInstance("X.509");
    		X509Certificate c = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(Base64.decode(cert)));
    		
    		String dn = c.getSubjectDN().toString();
    		String[] dnArray = dn.split(",");
    		
    		String iin = "", bin = "", email = "", fio = "", o = "", org = "", obl = "", country = "";
    		
            for (int i = 0; i < dnArray.length; i++) {
                String[] uzel = dnArray[i].trim().split("=");
            	
            	if (uzel[0].equals("SERIALNUMBER")) {
                    iin = uzel[1].substring(uzel[1].length() - 12);
                } else if (uzel[0].equals("OU")) {
                    bin = uzel[1].substring(uzel[1].length() - 12);
                } else if (uzel[0].equals("E") || uzel[0].equals("EMAILADDRESS")) {
                    email = uzel[1];
                } else if (uzel[0].equals("GIVENNAME")) {
                    o = uzel[1];
                } else if (uzel[0].equals("CN")) {
                    fio = uzel[1];
                } else if (uzel[0].equals("O")) {
                    org = uzel[1];
                } else if (uzel[0].equals("ST")) {
                    obl = uzel[1];
                } else if (uzel[0].equals("C")) {
                    country = uzel[1];
                }
            }
            
            if (o.length() > 0)
            	fio += " " + o;
            
            Map<String, Object> res = new HashMap<String, Object>();
            res.put("IIN", iin);
            res.put("BIN", bin);
            res.put("EMAIL", email);
            res.put("FIO", fio);
            res.put("ORG", org);
            res.put("OBL", obl);
            res.put("COUNTRY", country);
            res.put("START", new KrnDate(c.getNotBefore().getTime()));
            res.put("END", new KrnDate(c.getNotAfter().getTime()));

			return res;
    	} catch (Exception e) {
    		log.error(e, e);
    	}
    	return null;
    }

    public Map<String, Object> checkUCGOCertificate(String url, byte[] cert, String keyName) {
        return KalkanUtil.checkUCGOCertificate(url, cert, keyName);
    }

    public Map<String, Object> checkUCGOCertificateLDAP(String url, byte[] cert, String keyName) {
        return KalkanUtil.checkUCGOCertificateLDAP(url, cert, keyName);
    }
}
