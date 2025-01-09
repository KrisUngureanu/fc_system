package kz.tamur.server.plugins;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertStoreException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

import kz.gov.pki.kalkan.jce.provider.cms.CMSException;
import kz.tamur.util.crypto.CheckSignResult;
import kz.tamur.util.crypto.KalkanUtil;

public class IolaPlugin implements SrvPlugin {
	
	private Session session;
	private String lastError = "";

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String getLastError() {
		return lastError;
	}
	
	public String createPKCS7(int storeType, String storePath, String password, boolean attachText, String text) {
		lastError = "";
		String res = null;
		
		try {
			res = KalkanUtil.createPkcs7(storePath, password, text, attachText);
		} catch (KeyStoreException ex) {
			lastError = "KeyStoreException";
			ex.printStackTrace();
		} catch (CMSException ex) {
			lastError = "CMSException";
			ex.printStackTrace();
		} catch (CertStoreException ex) {
			lastError = "CertStoreException";
			ex.printStackTrace();
		} catch (UnrecoverableKeyException ex) {
			lastError = "UnrecoverableKeyException";
			ex.printStackTrace();
		} catch (InvalidAlgorithmParameterException ex) {
			lastError = "InvalidAlgorithmParameterException";
			ex.printStackTrace();
		} catch (NoSuchProviderException ex) {
			lastError = "NoSuchProviderException";
			ex.printStackTrace();
		} catch (FileNotFoundException ex) {
			lastError = "FileNotFoundException";
			//ex.printStackTrace();
		} catch (IOException ex) {
			lastError = "IOException";
			//ex.printStackTrace();
		} catch (NoSuchAlgorithmException ex) {
			lastError = "NoSuchAlgorithmException";
			ex.printStackTrace();
		} catch (CertificateException ex) {
			lastError = "CertificateException";
			ex.printStackTrace();
		} catch (Exception ex) {
			lastError = "Exception";
			ex.printStackTrace();
		}
		return res;
	}
	
	public boolean verifyPKCS7(String text, String pkcs7) {
		lastError = "";
		try {
            CheckSignResult res = KalkanUtil.verifyPkcs7(text, pkcs7, false);
            return res.isDigiSignOK();
		} catch (CertificateExpiredException e) {
			lastError = "CertificateExpiredException";
			e.printStackTrace();
		} catch (CertificateNotYetValidException e) {
			lastError = "CertificateNotYetValidException";
			e.printStackTrace();
		} catch (CMSException e) {
			lastError = "CMSException";
			e.printStackTrace();
		} catch (IOException e) {
			lastError = "IOException";
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			lastError = "NoSuchAlgorithmException";
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			lastError = "NoSuchProviderException";
			e.printStackTrace();
		} catch (CertStoreException e) {
			lastError = "CertStoreException";
			e.printStackTrace();
		} catch (CertificateParsingException e) {
			lastError = "CertificateParsingException";
			e.printStackTrace();
		}
		return false;
	}
}
