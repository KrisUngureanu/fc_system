package kz.tamur.lang;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.io.*;
import java.security.*;
import java.security.cert.X509Certificate;

import kz.gov.pki.kalkan.util.encoders.Base64;
import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;

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
            File cf = Funcs.getCanonicalFile(SECURITY_PROPERTIES_FILE);
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
            File f = Funcs.getCanonicalFile(filePath);
            if (f.exists()) {
                FileInputStream fis = new FileInputStream(f);
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
}
