package kz.tamur.plugins;

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

import kz.gamma.TumarCSP;
import kz.gamma.asn1.ASN1Sequence;
import kz.gamma.asn1.x509.TBSCertificateStructure;
import kz.gamma.util.encoders.Base64;
import kz.tamur.rt.orlang.ClientPlugin;
import kz.tamur.util.Funcs;
import kz.tumar.Signer32;

public class ECPPlugin implements ClientPlugin {
	public ECPPlugin() {
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
    
    public String getFIOFromCert(String cert) {
        try {
        	byte[] certB = Base64.decode(cert.getBytes());
    		CertificateFactory cf = CertificateFactory.getInstance("X.509"); 
    		ByteArrayInputStream bis = new ByteArrayInputStream(certB);
	        X509Certificate c = (X509Certificate)cf.generateCertificate(bis);
	        bis.close();

	        String dn = c.getSubjectDN().toString();
	        int b = dn.indexOf("CN=");
	        if (b > -1) {
	        	int e = dn.indexOf(',', b);
	        	if (e == -1) e = dn.length();
	        	
	        	return dn.substring(b+3, e);
	        }
	        
	        return dn;
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
//
//		String res = null;
//    	try {
//    		tumar.init();
//    		res = tumar.getCertificateFromPKCS7(sign);
//    	} finally {
//    		tumar.destroy();
//    	}
//    	return res;
//    }
//    
//    public String sign(String text, String profile, String password) {
//		TumarCSP tumar = new TumarCSP();
//
//		String res = null;
//    	try {
//    		tumar.init();
//        	res = tumar.signString(text, "", profile, password, "1.3.6.1.4.1.6801.1.5.8", true);
//    	} finally {
//    		tumar.destroy();
//    	}
//    	return res;
//    }
//
//    public boolean verifySign(String base64, String signBase64) {
//		TumarCSP tumar = new TumarCSP();
//
//    	boolean res = false;
//    	
//    	try {
//    		tumar.init();
//    		res = tumar.verifyString(base64, signBase64);
//    	} catch (Exception e) {
//    	} finally {
//        	tumar.destroy();
//    	}
//
//    	return res;
//    }
    
    public X509Certificate getCertificate(String signedDataBase64) {
		TumarCSP tumarCSP = new TumarCSP();
		byte[] signedData = Base64.decode(signedDataBase64);
		return tumarCSP.getCertificateFromPKCS7(signedData);
	}
	
	public boolean checkAttachs(String signedDataBase64) {
		boolean res = false;
		try {
			TumarCSP tumarCSP = new TumarCSP();
			byte[] signedData = Base64.decode(signedDataBase64);
			res = tumarCSP.verifyPKCS7(null, signedData);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public boolean checkAttachs1(byte[] signedData) throws Exception {
		TumarCSP tumarCSP = new TumarCSP();
		return tumarCSP.verifyPKCS7(null, signedData);
	}
}