package kz.tamur.server.plugins;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

import kz.gamma.TumarCSP;
import kz.gamma.asn1.ASN1Sequence;
import kz.gamma.asn1.x509.TBSCertificateStructure;
import kz.gamma.util.encoders.Base64;
import kz.tamur.util.Funcs;
import kz.tumar.Signer32;

public class ECPPlugin implements SrvPlugin {
	private Session session;

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	private boolean validate(String data, String sign, X509Certificate cert) {
        try {
	        byte[] cb = (new TBSCertificateStructure((ASN1Sequence)ASN1Sequence.fromByteArray(cert.getTBSCertificate()))).getSubjectPublicKeyInfo().getPublicKeyData().getBytes();

	        byte[] s = Base64.decode(sign.getBytes());

	        Signer32 s32 = new Signer32();
	    	int res = s32.verifyString(data.getBytes("UTF-8"), cb, s);
	    	
	    	return res == 1;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return false;
    }

    public boolean validate(String data, String sign, File cert) {
        try {
            FileInputStream fis = new FileInputStream(cert);
    		CertificateFactory cf = CertificateFactory.getInstance("X.509"); 
	        X509Certificate c = (X509Certificate)cf.generateCertificate(fis);
	        fis.close();

	        return validate(data, sign, c);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return false;
    }

    public boolean validate(String data, String sign, String cert) {
        try {
        	byte[] certB = Base64.decode(cert.getBytes());
    		CertificateFactory cf = CertificateFactory.getInstance("X.509"); 
    		ByteArrayInputStream bis = new ByteArrayInputStream(certB);
	        X509Certificate c = (X509Certificate)cf.generateCertificate(bis);
	        bis.close();

	        return validate(data, sign, c);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return false;
    }

    public String getCN(String cert) {
        try {
        	byte[] certB = Base64.decode(cert.getBytes());
    		CertificateFactory cf = CertificateFactory.getInstance("X.509"); 
    		ByteArrayInputStream bis = new ByteArrayInputStream(certB);
	        X509Certificate c = (X509Certificate)cf.generateCertificate(bis);
	        bis.close();

	        return c.getSubjectDN().toString();
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return "";
    }
    
    public String getCertificate(String url, String cn) {
		Map<String, String> env = new HashMap<String, String>();

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		// env.put(Context.SECURITY_AUTHENTICATION, "DIGEST-MD5");
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_PRINCIPAL, "");
		env.put(Context.SECURITY_CREDENTIALS, "");
		env.put("java.naming.ldap.attributes.binary", "userCertificate");
		
		List<File> fs = Funcs.getCertificatesFromLDAP(env, "", cn);
		
		try {
			if (fs != null && fs.size() > 0) {
				byte[] b = new byte[(int)fs.get(0).length()];
				FileInputStream fis = new FileInputStream(fs.get(0));
				fis.read(b);
				fis.close();
				return new String(Base64.encode(b));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public String encodeBase64(String text) {
    	try {
	    	return new String(Base64.encode(text.getBytes("UTF-8")));
    	} catch (Exception e) {
    		return null;
    	}
    }

    public String decodeBase64(String base64) {
    	return new String(Base64.decode(base64));
    }

//    public String getCertificateFromPKCS7(String sign) {
//		TumarCSP tumar = new TumarCSP();
//		tumar.init();
//
//    	String res = tumar.getCertificateFromPKCS7(sign);
//
//    	tumar.destroy();
//    	return res;
//    }
//    
//    public String sign(String text, String profile, String password) {
//		TumarCSP tumar = new TumarCSP();
//		tumar.init();
//
//    	String res = tumar.signString(text, "", profile, password, "1.3.6.1.4.1.6801.1.5.8", true);
//
//    	tumar.destroy();
//    	return res;
//    }
//
//    public boolean verifySign(String base64, String signBase64) {
//		TumarCSP tumar = new TumarCSP();
//		tumar.init();
//
//    	String res = tumar.verifyString(base64, signBase64);
//
//    	tumar.destroy();
//    	return "true".equals(res);
//    }
    
    public X509Certificate getCertificate(String signedDataBase64) {
		TumarCSP tumarCSP = new TumarCSP();
		byte[] signedData = Base64.decode(signedDataBase64);
		return tumarCSP.getCertificateFromPKCS7(signedData);
	}
    
    public String getIIN(String signedDataBase64) {
		TumarCSP tumarCSP = new TumarCSP();
		byte[] signedData = Base64.decode(signedDataBase64);
		X509Certificate cert = tumarCSP.getCertificateFromPKCS7(signedData);
		String dn = cert.getSubjectDN().toString();
		int index1 = dn.indexOf("SERIALNUMBER=IIN");
		int index2 = dn.indexOf("OU=BIN");
		String iin = dn.substring(index1 + 16, index1 + 28);
		String bin = dn.substring(index2 + 6, index2 + 18);
		return iin;
	}
	
	public boolean checkAttachs(String dataBase64, String signedDataBase64) {
		
		
		
		boolean res = false;
		try {
			TumarCSP tumarCSP = new TumarCSP();
			byte[] data = dataBase64 == null ? null : dataBase64.getBytes();
			byte[] signedData = Base64.decode(signedDataBase64);
			res = tumarCSP.verifyPKCS7(data, signedData);
			
//			Pkcs7Data pkcs7Data = new Pkcs7Data(signedData);
//			byte[] data2 = pkcs7Data.getData();
//			System.out.println(new String(data2));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public boolean checkAttachs1(byte[] data, byte[] signedData) throws Exception {
		TumarCSP tumarCSP = new TumarCSP();
		return tumarCSP.verifyPKCS7(data, signedData);
	}
	
	public void test() throws Exception {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.parse("C:/requestAnswerWP_new.xml");
		Element e1 = (Element) doc.getDocumentElement().getElementsByTagName("signOrder").item(0);
		String dataBase64 = e1.getTextContent();

		Element e = (Element) doc.getDocumentElement().getElementsByTagName("orderSigningECD").item(0);
		String signedDataBase64 = e.getTextContent();
		
		
		
		System.out.println(checkAttachs(dataBase64, new String(Base64.decode(signedDataBase64))));
		System.out.println(getCertificate(signedDataBase64));
		System.out.println(getIIN(signedDataBase64));
	}
}