package kz.tamur.or3.client.plugins;

import java.io.File;
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
import java.util.Map;

import kz.gov.pki.kalkan.jce.provider.cms.CMSException;
import kz.tamur.rt.orlang.ClientPlugin;
import kz.tamur.util.Funcs;
import kz.tamur.util.crypto.CheckSignResult;
import kz.tamur.util.crypto.KalkanUtil;

public class IolaSigner implements ClientPlugin {
	
	private String lastError = "";
	
	public String getLastError() {
		return lastError;
	}
	/**
	 * Подписывает данные
	 * @param storeType 
	 * @param storePath
	 * @param password
	 * @param text
	 * @return подпись в виде байтов
	 */
	public byte[] sign(int storeType, String storePath, String password, byte[] text) {//TODO
		lastError = "";
		byte[] res = null;
		
		try {
            res = KalkanUtil.signPlainData(storePath, password, text);
		} catch (KeyStoreException ex) {
			lastError = "KeyStoreException";
			ex.printStackTrace();
		} catch (UnrecoverableKeyException ex) {
			lastError = "UnrecoverableKeyException";
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
	/**
	 * Возвращает сертификат
	 * @param params параметры
	 * @return сертификат в виде байтов
	 */
	public byte[] getCertificate(Map<String, Object> params) {
		return getCertificate((Integer)params.get("storeType"), (String)params.get("storePath"), (String)params.get("password"));
	}

	public byte[] getCertificate(int storeType, String storePath, String password) {
		lastError = "";
		byte[] res = null;
		
		try {
            res = KalkanUtil.loadCertificate(storePath, password.toCharArray()).getEncoded();
		} catch (KeyStoreException ex) {
			lastError = "KeyStoreException";
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
	/**
	 * Подписывает данные 
	 * @param params параметры
	 * @param file файл
	 * @return подпись в виде байтов
	 */
	public byte[] sign(Map<String, Object> params, File file) {
		try {
			return sign(params, Funcs.read(file));
		} catch (IOException e) {
			lastError = "IOException";
		}
		return null;
	}

	public byte[] sign(Map<String, Object> params, byte[] text) {//TODO
		return sign((Integer)params.get("storeType"), (String)params.get("storePath"), (String)params.get("password"), text);
	}

	public String createPKCS7(int storeType, String storePath, String password, boolean attachText, String text) {//TODO
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

	public String createPKCS7(Map<String, Object> params, boolean attachText, String text) {//TODO
		return createPKCS7((Integer)params.get("storeType"), (String)params.get("storePath"),
				(String)params.get("password"), attachText, text);
	}

	public CheckSignResult verifyFullPKCS7(String text, String pkcs7) {
        CheckSignResult res = null;
        try {
            res = KalkanUtil.verifyPkcs7(text, pkcs7, false);
        } catch (Exception e) {
            res = new CheckSignResult();
            res.setCertError(CheckSignResult.ECP_DAMAGED);
            e.printStackTrace();
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
	/**
	 * Проверяет подпись
	 * @param data данные
	 * @param sign подпись
	 * @param cert сертификат
	 * @return true - подпись валидна, false - не валидна
	 */
	public boolean verify(Object data, Object sign, Object cert) {
		lastError = "";
		try {
			byte[] dataB = (data instanceof File) ? Funcs.read((File)data) : (byte[]) data;
			byte[] signB = (sign instanceof File) ? Funcs.read((File)sign) : (byte[]) sign;
			byte[] certB = (cert instanceof File) ? Funcs.read((File)cert) : (byte[]) cert;

			return verify(dataB, signB, certB);
		} catch (IOException e) {
			lastError = "IOException";
			e.printStackTrace();
		}
		return false;
	}

    public CheckSignResult verifyFull(byte[] text, byte[] sign, byte[] cert) {
        CheckSignResult res = null;
        try {
            res = KalkanUtil.verifyPlainData(text, sign, cert);
        } catch (Exception e) {
            res = new CheckSignResult();
            res.setCertError(CheckSignResult.ECP_DAMAGED);
            e.printStackTrace();
        }
        return res;
    }

    public boolean verify(byte[] text, byte[] sign, byte[] cert) {
        lastError = "";
        try {
            CheckSignResult res = KalkanUtil.verifyPlainData(text, sign, cert);
            return res.isDigiSignOK();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
