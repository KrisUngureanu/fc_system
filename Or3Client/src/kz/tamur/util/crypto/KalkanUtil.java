package kz.tamur.util.crypto;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CertificateParsingException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509CRL;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.ExtendedRequest;
import javax.naming.ldap.ExtendedResponse;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.security.auth.x500.X500Principal;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import kz.gamma.hardware.asn1.cryptopro.GammaObjectIndentifiers;
import kz.gamma.hardware.crypto.software.ocsp.OCSPUtilities;
import kz.gov.pki.kalkan.asn1.ASN1EncodableVector;
import kz.gov.pki.kalkan.asn1.ASN1InputStream;
import kz.gov.pki.kalkan.asn1.DERObject;
import kz.gov.pki.kalkan.asn1.DERObjectIdentifier;
import kz.gov.pki.kalkan.asn1.DEROctetString;
import kz.gov.pki.kalkan.asn1.DERSet;
import kz.gov.pki.kalkan.asn1.cms.Attribute;
import kz.gov.pki.kalkan.asn1.cms.AttributeTable;
import kz.gov.pki.kalkan.asn1.cryptopro.CryptoProObjectIdentifiers;
import kz.gov.pki.kalkan.asn1.knca.KNCAObjectIdentifiers;
import kz.gov.pki.kalkan.asn1.pkcs.PKCSObjectIdentifiers;
import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import kz.gov.pki.kalkan.jce.provider.X509CRLParser;
import kz.gov.pki.kalkan.jce.provider.cms.CMSException;
import kz.gov.pki.kalkan.jce.provider.cms.CMSProcessable;
import kz.gov.pki.kalkan.jce.provider.cms.CMSProcessableByteArray;
import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedData;
import kz.gov.pki.kalkan.jce.provider.cms.CMSSignedDataGenerator;
import kz.gov.pki.kalkan.jce.provider.cms.SignerInformation;
import kz.gov.pki.kalkan.jce.provider.cms.SignerInformationStore;
import kz.gov.pki.kalkan.ocsp.BasicOCSPResp;
import kz.gov.pki.kalkan.ocsp.CertificateID;
import kz.gov.pki.kalkan.ocsp.OCSPReq;
import kz.gov.pki.kalkan.ocsp.OCSPReqGenerator;
import kz.gov.pki.kalkan.ocsp.OCSPResp;
import kz.gov.pki.kalkan.ocsp.RevokedStatus;
import kz.gov.pki.kalkan.ocsp.SingleResp;
import kz.gov.pki.kalkan.ocsp.UnknownStatus;
import kz.gov.pki.kalkan.tsp.TSPAlgorithms;
import kz.gov.pki.kalkan.tsp.TimeStampRequest;
import kz.gov.pki.kalkan.tsp.TimeStampRequestGenerator;
import kz.gov.pki.kalkan.tsp.TimeStampResponse;
import kz.gov.pki.kalkan.tsp.TimeStampToken;
import kz.gov.pki.kalkan.util.encoders.Base64;
import kz.gov.pki.kalkan.util.encoders.Hex;
import kz.gov.pki.kalkan.xmldsig.KncaXS;
import kz.tamur.SecurityContextHolder;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.message.WSSecBase;
import org.apache.ws.security.message.token.SecurityTokenReference;
import org.apache.xml.security.encryption.XMLCipherParameters;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class KalkanUtil {
	private static final Log log = LogFactory.getLog(KalkanUtil.class);
	
	public static final String GOST_ALGORITHM_ECGOST34310 = "ECGOST34310";
	public static final String GOST_ALGORITHM_ECGOST3410 = "ECGOST3410";
	
	public static final String GOST_ALGORITHM_ECGOST3410_2015_512 = "ECGOST3410-2015-512";
	
	public static final String RSA_ALGORITHM_SHA1 = "SHA1";

	private static final String SIGN_METHOD_GOST = Constants.MoreAlgorithmsSpecNS + "gost34310-gost34311";
	private static final String DIGEST_METHOD_GOST = Constants.MoreAlgorithmsSpecNS + "gost34311";
	private static final String SIGN_METHOD_RSA = Constants.MoreAlgorithmsSpecNS + "rsa-sha1";
	private static final String DIGEST_METHOD_RSA = Constants.MoreAlgorithmsSpecNS + "sha1";
	private static final String SIGN_METHOD_RSA_256 = Constants.MoreAlgorithmsSpecNS + "rsa-sha256";
	private static final String DIGEST_METHOD_RSA_256 = XMLCipherParameters.SHA256;
	private static final String SIGN_METHOD_GOST_2015 = "urn:ietf:params:xml:ns:pkigovkz:xmlsec:algorithms:gostr34102015-gostr34112015-512";
	private static final String DIGEST_METHOD_GOST_2015 = "urn:ietf:params:xml:ns:pkigovkz:xmlsec:algorithms:gostr34112015-512";
	
	private static final String DIGEST_GOST3411_2015_256 = "1.2.398.3.10.1.3.2";
	private static final String DIGEST_GOST3411_2015_512 = "1.2.398.3.10.1.3.3";
	private static final String gost3411_2015_with_gost3410_2015_256 = "1.2.398.3.10.1.1.2.3.1";
	private static final String gost3411_2015_with_gost3410_2015_512 = "1.2.398.3.10.1.1.2.3.2";

	// Kalkan
	public static final int ST_IDCARD = 1;
	public static final int ST_ETOKEN = 4;
	public static final int ST_JACARTA = 3;
	public static final int ST_KAZTOKEN = 15;
	public static final int PKCS12 = 17;
	public static final String STR_IDCARD = "AKKZIDCardStore";
	public static final String STR_ETOKEN = "AKEToken72KStore";
	public static final String STR_JACARTA = "AKJaCartaStore";
	public static final String STR_KAZTOKEN = "AKKaztokenStore";
	public static final String STR_PKCS12 = "PKCS12";
	
	public static final String ISSUER_UCGO_KZ = "МЕМЛЕКЕТТІК ОРГАНДАРДЫҢ КУӘЛАНДЫРУ ОРТАЛЫҒЫ";
	public static final String ISSUER_UCGO_RU = "Удостоверяющий центр Государственных органов";

	public static Map<Integer, String> storeTypes = new HashMap<Integer, String>();

	static {
		storeTypes.put(ST_JACARTA, "AKJaCartaStore");
		storeTypes.put(ST_ETOKEN, "AKEToken72KStore");
		storeTypes.put(ST_KAZTOKEN, "AKKaztokenStore");
		storeTypes.put(ST_IDCARD, "AKKZIDCardStore");
		storeTypes.put(PKCS12, "PKCS12");
	}

	/**
	 * This constant represent extension for getting CRL list from certificate.
	 */
	private static final String CRL_EXTENSION = "2.5.29.31";

	// Ключ предназначен для авторизации
	private static final String PURPOSE_AUTH = "1.3.6.1.5.5.7.3.2";
	// Ключ предназначен для подписи
	private static final String PURPOSE_SIGN = "1.3.6.1.5.5.7.3.4";
	// Ключ обычного физлица (новый НУЦ)
	private static final String OWNER_FL = "1.2.398.3.3.4.1.1";
	// Ключ физлица-сотрудника ЮЛ (новый НУЦ)
	private static final String OWNER_UL = "1.2.398.3.3.4.1.2";
	// Ключ руководителя ЮЛ (новый НУЦ)
	private static final String OWNER_UL_HEAD = "1.2.398.3.3.4.1.2.1";
	// Ключ сотрудника ЮЛ с правом подписи (новый НУЦ)
	private static final String OWNER_UL_DEPUTY = "1.2.398.3.3.4.1.2.2";
	// Ключ сотрудника ЮЛ с правом подписи финансовых документов (новый НУЦ)
	private static final String OWNER_UL_FINANCE = "1.2.398.3.3.4.1.2.3";
	// Ключ сотрудника отдела кадров ЮЛ (новый НУЦ)
	private static final String OWNER_UL_HR = "1.2.398.3.3.4.1.2.4";
	// Ключ обычного сотрудника ЮЛ (новый НУЦ)
	private static final String OWNER_UL_EMPLOYEE = "1.2.398.3.3.4.1.2.5";

	private static final String ENCODING = "UTF-8";
	private static final String ROOT_CERTS_FOLDER = Funcs.getSystemProperty("rootCertsFolder");
	private static String OCSP_URL = System.getProperty("ocspUrl") != null ? System.getProperty("ocspUrl")
			: "http://ocsp.pki.gov.kz/";
	
	private static Map<String, String> OCSP_URL_MAP = new HashMap<>();
	
	private static final String TSP_URL = System.getProperty("tspUrl") != null ? System.getProperty("tspUrl")
			: "http://tsp.pki.gov.kz/";
	private static final String[] ROOT_CERTS = { "/kz/tamur/util/crypto/rootcerts/pki_rsa.cer",
			"/kz/tamur/util/crypto/rootcerts/pki_gost.cer", "/kz/tamur/util/crypto/rootcerts/ucgo_gost.cer",
			"/kz/tamur/util/crypto/rootcerts/oldnuc_rsa.cer", "/kz/tamur/util/crypto/rootcerts/oldnuc_gost.cer",
			"/kz/tamur/util/crypto/rootcerts/nca_gost2022_test.cer", "/kz/tamur/util/crypto/rootcerts/root_gost2022_test.cer",
			"/kz/tamur/util/crypto/rootcerts/root_gost_2020.cer", "/kz/tamur/util/crypto/rootcerts/UCGO_2022.cer",
			"/kz/tamur/util/crypto/rootcerts/root_gost2015_2022.cer", "/kz/tamur/util/crypto/rootcerts/nca_gost2015.cer",
			"/kz/tamur/util/crypto/rootcerts/root-gost.cer"
	};

	private static final int[] DONT_CHECK_CERT_PROPERTIES;

    private static ThreadLocal<Element> xmlToCheck = new ThreadLocal<Element>();

    static {
		/*
		 * Не проверять некоторые ограничения сертификата Например
		 * -DdontCheckCertProperties=1,3 - не будет проверять что сертификат
		 * просрочени и подписан НУЦ
		 */
		String tmp = System.getProperty("dontCheckCertProperties");
		log.info("dontCheckCertProperties = " + tmp);
		if (tmp != null) {
			String[] ps = tmp.split(",");
			DONT_CHECK_CERT_PROPERTIES = new int[ps.length];
			for (int i = 0; i < ps.length; i++)
				DONT_CHECK_CERT_PROPERTIES[i] = Integer.parseInt(ps[i]);
		} else
			DONT_CHECK_CERT_PROPERTIES = null; //new int[] {5};

		// Добавление провайдера в java.security.Security
		boolean exists = false;
		Provider[] providers = Security.getProviders();
		for (Provider p : providers) {
			log.info("known provider = " + p.getName());
			if (p.getName().equals(KalkanProvider.PROVIDER_NAME)) {
				exists = true;
			}
		}
		if (!exists) {
			log.info("initializing provider KALKAN");
			Security.addProvider(new KalkanProvider());
			KncaXS.loadXMLSecurity();
		} else {
			log.info("KncaXS.loadXMLSecurity()");
			KncaXS.loadXMLSecurity();
		}
        ResourceResolver.register(NullURIResolverSpi.class.getName());
        
        initOCSPMap(null);
    }

	public static X509Certificate getCertificate(byte[] cert) {
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(cert);
			CertificateFactory cf = CertificateFactory.getInstance("X.509", KalkanProvider.PROVIDER_NAME);
			X509Certificate c = (X509Certificate) cf.generateCertificate(is);
			is.close();
			return c;
		} catch (Exception e) {
			SecurityContextHolder.getLog().error(e, e);
		}
		return null;
	}

	public static X509Certificate getCertificate(File cert) {
		try {
			String canonicalPath = Funcs.normalizeInput(cert.getCanonicalPath());
			if (canonicalPath.matches(".+")) {
				FileInputStream is = new FileInputStream(cert);
				CertificateFactory cf = CertificateFactory.getInstance("X.509", KalkanProvider.PROVIDER_NAME);
				X509Certificate c = (X509Certificate) cf.generateCertificate(is);
				is.close();
				return c;
			}
		} catch (Exception e) {
			SecurityContextHolder.getLog().error(e, e);
		}
		return null;
	}
	
	public static void saveCertificateToFile(String p12FileName, char[] pd, String fileName) throws Exception {
		X509Certificate c = loadCertificate(p12FileName, pd);
		byte[] b = c.getEncoded();
		
		FileOutputStream fos = new FileOutputStream(fileName);
		fos.write(b);
		fos.close();
	}

	public static X509Certificate loadCertificate(String p12FileName, char[] pd) throws KeyStoreException,
			NoSuchProviderException, IOException, NoSuchAlgorithmException, CertificateException {
		if (Funcs.isValid(p12FileName)) {
			File f = Funcs.getCanonicalFile(p12FileName);
			if (f != null) {
				FileInputStream fis = new FileInputStream(f);
		
				KeyStore ks = KeyStore.getInstance("PKCS12", KalkanProvider.PROVIDER_NAME);
				ks.load(fis, pd);
				fis.close();
		
				@SuppressWarnings("rawtypes")
				Enumeration en = ks.aliases();
				String alias = null;
				while (en.hasMoreElements()) {
					alias = en.nextElement().toString();
				}
				X509Certificate certificate = (X509Certificate) ks.getCertificate(alias);// Chain(alias)[0];
				return certificate;
			}
		}
		return null;
	}

	public static X509Certificate loadCertificate(byte[] p12Bytes, char[] pd) throws KeyStoreException,
			NoSuchProviderException, IOException, NoSuchAlgorithmException, CertificateException {
		ByteArrayInputStream is = new ByteArrayInputStream(p12Bytes);
		KeyStore ks = KeyStore.getInstance("PKCS12", KalkanProvider.PROVIDER_NAME);
		ks.load(is, pd);
		is.close();

		@SuppressWarnings("rawtypes")
		Enumeration en = ks.aliases();
		String alias = null;
		while (en.hasMoreElements()) {
			alias = en.nextElement().toString();
		}
		X509Certificate certificate = (X509Certificate) ks.getCertificate(alias);// Chain(alias)[0];
		return certificate;
	}

	private static PrivateKey loadPrivateKey(String p12FileName, char[] pd) throws IOException, KeyStoreException,
			NoSuchProviderException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
		if (Funcs.isValid(p12FileName)) {
			File f = Funcs.getCanonicalFile(p12FileName);
			if (f != null) {
				FileInputStream fis = new FileInputStream(f);
		
				KeyStore ks = KeyStore.getInstance("PKCS12", KalkanProvider.PROVIDER_NAME);
				ks.load(fis, pd);
				fis.close();
		
				@SuppressWarnings("rawtypes")
				Enumeration en = ks.aliases();
				String alias = null;
				while (en.hasMoreElements()) {
					alias = en.nextElement().toString();
				}
				PrivateKey privateKey = (PrivateKey) ks.getKey(alias, pd);
				return privateKey;
			}
		}
		return null;
	}

	private static PrivateKey loadPrivateKey(byte[] p12Bytes, char[] pd) throws IOException, KeyStoreException,
			NoSuchProviderException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
		ByteArrayInputStream is = new ByteArrayInputStream(p12Bytes);

		KeyStore ks = KeyStore.getInstance("PKCS12", KalkanProvider.PROVIDER_NAME);
		ks.load(is, pd);
		is.close();

		@SuppressWarnings("rawtypes")
		Enumeration en = ks.aliases();
		String alias = null;
		while (en.hasMoreElements()) {
			alias = en.nextElement().toString();
		}
		PrivateKey privateKey = (PrivateKey) ks.getKey(alias, pd);
		return privateKey;
	}

	public static Document createXmlDocumentFromString(String xmlString, String charset)
			throws SAXException, ParserConfigurationException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
		Document doc = documentBuilder.parse(new ByteArrayInputStream(xmlString.getBytes(charset)));
		return doc;
	}

	public static Element getXmlSignatureElement(String xmlString, String p12FileName, String pd)
			throws XMLSecurityException, TransformerConfigurationException, IOException, TransformerException,
			SAXException, ParserConfigurationException, UnrecoverableKeyException, KeyStoreException,
			NoSuchProviderException, NoSuchAlgorithmException, CertificateException {
		PrivateKey privateKey = loadPrivateKey(p12FileName, pd.toCharArray());
		X509Certificate certificate = loadCertificate(p12FileName, pd.toCharArray());

		Document doc = createXmlDocumentFromString(xmlString, "UTF-8");
		return getXmlSignatureWithKeys(doc, privateKey, certificate);
	}

    public static XMLSignature getXmlSignatureWithKeys1(SOAPEnvelope envelope, SOAPElement security, String p12FileName, String password)
            throws XMLSecurityException, TransformerConfigurationException, IOException, TransformerException, SAXException,
            ParserConfigurationException, UnrecoverableKeyException, KeyStoreException, NoSuchProviderException,
            NoSuchAlgorithmException, CertificateException, SOAPException {
        
        PrivateKey privateKey = loadPrivateKey(p12FileName, password.toCharArray());
        X509Certificate certificate = loadCertificate(p12FileName, password.toCharArray());
        
        String signMethod = null;
        String digestMethod = null;
        String signedXml = null;

        String sigAlgOid = certificate.getSigAlgOID();
        if (sigAlgOid.equals(PKCSObjectIdentifiers.sha1WithRSAEncryption.getId())) {
            signMethod = SIGN_METHOD_RSA;
            digestMethod = DIGEST_METHOD_RSA;
        } else if (sigAlgOid.equals(PKCSObjectIdentifiers.sha256WithRSAEncryption.getId())) {
            signMethod = SIGN_METHOD_RSA_256;
            digestMethod = DIGEST_METHOD_RSA_256;
		} else if (sigAlgOid.equals(gost3411_2015_with_gost3410_2015_512)) {
			signMethod = SIGN_METHOD_GOST_2015;
			digestMethod = DIGEST_METHOD_GOST_2015;
        } else {
            signMethod = SIGN_METHOD_GOST;
            digestMethod = DIGEST_METHOD_GOST;
        }

        Document doc = envelope.getOwnerDocument();
        XMLSignature sig = new XMLSignature(doc, "", signMethod, Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);

        if (doc.getFirstChild() != null) {
            Transforms transforms = new Transforms(doc);
            transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);

            sig.addDocument("#bodyId", transforms, digestMethod);
            sig.addKeyInfo((X509Certificate) certificate);
            sig.sign(privateKey);
            
            SOAPElement se = SOAPFactory.newInstance().createElement(sig.getElement());
            security.addChildElement(se);
        }
        
        return sig;
    }

    public static XMLSignature getXmlSignatureWithKeys1(String envelope, SOAPElement security, String p12FileName, String password)
            throws XMLSecurityException, TransformerConfigurationException, IOException, TransformerException, SAXException,
            ParserConfigurationException, UnrecoverableKeyException, KeyStoreException, NoSuchProviderException,
            NoSuchAlgorithmException, CertificateException, SOAPException {
        
        PrivateKey privateKey = loadPrivateKey(p12FileName, password.toCharArray());
        X509Certificate certificate = loadCertificate(p12FileName, password.toCharArray());
        
        String signMethod = null;
        String digestMethod = null;
        String signedXml = null;

        String sigAlgOid = certificate.getSigAlgOID();
        if (sigAlgOid.equals(PKCSObjectIdentifiers.sha1WithRSAEncryption.getId())) {
            signMethod = SIGN_METHOD_RSA;
            digestMethod = DIGEST_METHOD_RSA;
        } else if (sigAlgOid.equals(PKCSObjectIdentifiers.sha256WithRSAEncryption.getId())) {
            signMethod = SIGN_METHOD_RSA_256;
            digestMethod = DIGEST_METHOD_RSA_256;
		} else if (sigAlgOid.equals(gost3411_2015_with_gost3410_2015_512)) {
			signMethod = SIGN_METHOD_GOST_2015;
			digestMethod = DIGEST_METHOD_GOST_2015;
        } else {
            signMethod = SIGN_METHOD_GOST;
            digestMethod = DIGEST_METHOD_GOST;
        }

        Document doc = createXmlDocumentFromString(envelope, "UTF-8");
        XMLSignature sig = new XMLSignature(doc, "", signMethod, Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);

        if (doc.getFirstChild() != null) {
            Transforms transforms = new Transforms(doc);
            transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);

            sig.addDocument("#bodyId", transforms, digestMethod);
            sig.addKeyInfo((X509Certificate) certificate);
            sig.sign(privateKey);
            
            SOAPElement se = SOAPFactory.newInstance().createElement(sig.getElement());
            security.addChildElement(se);
        }
        
        return sig;
    }

    public static Element getXmlSignatureWithKeys(Document doc, PrivateKey privateKey, X509Certificate certificate)
			throws XMLSecurityException, TransformerConfigurationException, IOException, TransformerException,
			SAXException, ParserConfigurationException {
		String signMethod = null;
		String digestMethod = null;

		String sigAlgOid = certificate.getSigAlgOID();
		if (sigAlgOid.equals(PKCSObjectIdentifiers.sha1WithRSAEncryption.getId())) {
			signMethod = SIGN_METHOD_RSA;
			digestMethod = DIGEST_METHOD_RSA;
		} else if (sigAlgOid.equals(PKCSObjectIdentifiers.sha256WithRSAEncryption.getId())) {
			signMethod = SIGN_METHOD_RSA_256;
			digestMethod = DIGEST_METHOD_RSA_256;
		} else if (sigAlgOid.equals(gost3411_2015_with_gost3410_2015_512)) {
			signMethod = SIGN_METHOD_GOST_2015;
			digestMethod = DIGEST_METHOD_GOST_2015;
		} else {
			signMethod = SIGN_METHOD_GOST;
			digestMethod = DIGEST_METHOD_GOST;
		}

		XMLSignature sig = new XMLSignature(doc, "", signMethod);
		Transforms transforms = new Transforms(doc);
		transforms.addTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature");
		transforms.addTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
		sig.addDocument("#bodyId", transforms, digestMethod);
		sig.addKeyInfo((X509Certificate) certificate);
		sig.sign(privateKey);

		return sig.getElement();
	}

	public static XMLSignature getXmlSignature(Document doc, PrivateKey privateKey, X509Certificate certificate)
			throws XMLSecurityException, TransformerConfigurationException, IOException, TransformerException,
			SAXException, ParserConfigurationException {
		String signMethod = null;
		String digestMethod = null;

		String sigAlgOid = certificate.getSigAlgOID();
		if (sigAlgOid.equals(PKCSObjectIdentifiers.sha1WithRSAEncryption.getId())) {
			signMethod = SIGN_METHOD_RSA;
			digestMethod = DIGEST_METHOD_RSA;
		} else if (sigAlgOid.equals(PKCSObjectIdentifiers.sha256WithRSAEncryption.getId())) {
			signMethod = SIGN_METHOD_RSA_256;
			digestMethod = DIGEST_METHOD_RSA_256;
		} else if (sigAlgOid.equals(gost3411_2015_with_gost3410_2015_512)) {
			signMethod = SIGN_METHOD_GOST_2015;
			digestMethod = DIGEST_METHOD_GOST_2015;
		} else {
			signMethod = SIGN_METHOD_GOST;
			digestMethod = DIGEST_METHOD_GOST;
		}

		XMLSignature sig = new XMLSignature(doc, "", signMethod);
		Transforms transforms = new Transforms(doc);
		transforms.addTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature");
		transforms.addTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
		sig.addDocument("#bodyId", transforms, digestMethod);
		sig.addKeyInfo((X509Certificate) certificate);
		sig.sign(privateKey);

		return sig;
	}

	private static SOAPElement getElementByName(SOAPElement tag, String name) {
		SOAPElement res = null;
		Iterator it = tag.getChildElements();
		while (it.hasNext()) {
			Object o = it.next();
			if (o instanceof SOAPElement && (name.equals(((SOAPElement) o).getLocalName()))) {
				res = (SOAPElement) o;
				break;
			}
		}
		return res;
	}

	private static Element getElementByName(Element tag, String name) {
		Element res = null;
		NodeList it = tag.getChildNodes();
		for (int i = 0; i < it.getLength(); i++) {
			Object o = it.item(i);
			if (o instanceof Element && (name.equals(((Element) o).getLocalName()))) {
				res = (Element) o;
				break;
			}
		}
		return res;
	}

/*	private static void checkTransportXml(String path, String pathCert) throws Exception {
		File f = new File(path);
		byte[] bs = new byte[(int)f.length()];
		FileInputStream fis = new FileInputStream(f);
		fis.read(bs);
		fis.close();
		
		String signedXml = new String(bs);
		
		//System.out.println(signedXml);
		
		//X509Certificate certKey = getCertificate(new File(pathCert));
		
		CheckSignResult res = new CheckSignResult();

		DocumentBuilderFactory dbf = XmlUtil.getDocumentBuilderFactory();
		dbf.setNamespaceAware(true);
		DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
		Document doc = documentBuilder.parse(new ByteArrayInputStream(bs));

		//Element s = createXmlDocumentFromString(signedXml, "UTF-8").getDocumentElement();
		Element s = doc.getDocumentElement();
		NodeList sigelement = s.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
		
		if (sigelement.getLength() == 0) {
			// нет подписи
			res.setCertError(CheckSignResult.NO_ECP_FOUND);
		} else {
			XMLSignature signature = new XMLSignature((Element) sigelement.item(0), "");
			KeyInfo ki = signature.getKeyInfo();
			X509Certificate certKey = ki.getX509Certificate();

			boolean result = false;
			if (certKey != null) {
				// проверка подписи
                xmlToCheck.set(s);
                result = signature.checkSignatureValue(certKey);
                xmlToCheck.remove();
				if (result) {
					// подпись верна
					res.setDigiSignOK(true);

					// теперь проверяем сертификат
					//res = checkCertificate(res, certKey, false, rootCertPath, ocspUrl, proxyHost, proxyPort, null, null);
				} else {
					// подпись нарушена
					res.setCertError(CheckSignResult.ECP_DAMAGED);
				}
			} else {
				// подпись не содержит сертификата подписавшего лица?
				res.setCertError(CheckSignResult.NO_CERT_FOUND);
			}
		}
	}
*/	

	private static SOAPMessage loadSoap() throws Exception {
		InputStream is = new FileInputStream("D:\\tmp\\soapTest1.xml");
		SOAPMessage request = MessageFactory.newInstance().createMessage(null, is);
		is.close();
		
		return request;
	}
		
	private static void testRenameNamespace() throws Exception {
		String nsToFound = "http://message.persistence.interactive.nat";
		String nsReplacement = "http://my.own.namespace.kz";
		
		SOAPMessage message = loadSoap();
		
        SOAPBody body = message.getSOAPBody();
        
        for (Iterator it = body.getChildElements(); it.hasNext(); ) {
        	Object obj = it.next();
        	if (obj instanceof SOAPElement) {
        		SOAPElement s = (SOAPElement) obj;
        		List<String> toRemove = new ArrayList<String>();
        		for (Iterator nit = s.getNamespacePrefixes(); nit.hasNext(); ) {
        			String px = (String) nit.next();
        			String uri = s.getNamespaceURI(px);
        	    	System.out.println("Found namespace declaration: " + px + ":" + uri);
        	    	
        	    	if (nsToFound.equals(uri)) {
        	    		s.removeNamespaceDeclaration(px);
        	    		s.addNamespaceDeclaration(px, nsReplacement);
        	    	}
        		}
        	}
        }
        
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
        message.writeTo(bos);
        System.out.println(new String(bos.toByteArray()));

	}
	
	private static void signTransportXml(String xmlPath, String p12Path, String pass, String ref) throws Exception {
		InputStream is = new FileInputStream(xmlPath);
		SOAPMessage request = MessageFactory.newInstance().createMessage(null, is);
		SOAPEnvelope envelope = request.getSOAPPart().getEnvelope();

		SOAPBody body = envelope.getBody();
        SOAPHeader header = envelope.getHeader();
        if (header != null) {
          envelope.removeChild(header);
        }
        header = envelope.addHeader();
        
        SOAPElement element = header.addChildElement("Security", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        element.addNamespaceDeclaration("wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
        element.addAttribute(new QName(envelope.getNamespaceURI(), "mustUnderstand", envelope.getPrefix()), "1");
        
        body.addAttribute(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id", "wsu"), "bodyId");

        XMLSignature sig = KalkanUtil.getXmlSignatureWithKeys1(envelope, element, p12Path, pass);
        
        X509Certificate cert = KalkanUtil.loadCertificate(p12Path, pass.toCharArray());
        String serial = cert.getSerialNumber().toString();
        X500Principal d = cert.getIssuerX500Principal();
        String name = d.getName();
        
        WSSecBase ws = new WSSecBase();
        KeyInfo keyInfo = sig.getKeyInfo();
        String keyInfoUri = ws.getWsConfig().getIdAllocator().createSecureId("KI-", keyInfo);

        SOAPElement keyInfoelement = (SOAPElement)element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo").item(0);
        SOAPElement x509DataElement = (SOAPElement)keyInfoelement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "X509Data").item(0);
        SOAPElement x509CertElement = (SOAPElement)x509DataElement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "X509Certificate").item(0);
        String certTxt = x509CertElement.getTextContent();
        
        keyInfoelement.removeChild(x509DataElement);

        keyInfoelement.setAttribute("Id", keyInfoUri);
        
        SOAPElement tokenReferenceElement = keyInfoelement.addChildElement(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "SecurityTokenReference", "wsse"));
        Source src = request.getSOAPPart().getContent();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMResult result = new DOMResult();
        transformer.transform(src, result);
        Document document = (Document)result.getNode();
      
        SecurityTokenReference secRef = new SecurityTokenReference(document);
        String strUri = ws.getWsConfig().getIdAllocator().createSecureId("STR-", secRef);
      
        tokenReferenceElement.addAttribute(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id", "wsu"), strUri);

        SOAPElement keyIdentifierElement = tokenReferenceElement.addChildElement(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "KeyIdentifier", "wsse"));
        keyIdentifierElement.setAttribute("EncodingType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary");
        keyIdentifierElement.setAttribute("ValueType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3");
        keyIdentifierElement.setTextContent(certTxt);

        XmlObject xo = XmlObject.Factory.parse(envelope);
        String envtxt = xo.xmlText();
        
        FileOutputStream fos = new FileOutputStream(xmlPath + ".signed.xml");
        fos.write(envtxt.getBytes());
        fos.close();
        
	}
/*	private static String signTransportXml(String xmlPath, String p12Path, String pass, String ref) throws Exception {
		
		InputStream is = new FileInputStream(xmlPath);
		SOAPMessage request = MessageFactory.newInstance().createMessage(null, is);
		SOAPEnvelope envelope = request.getSOAPPart().getEnvelope();

		envelope.addNamespaceDeclaration("wsu",
				"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
		envelope.addNamespaceDeclaration("wsse",
				"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");

		SOAPBody body = envelope.getBody();
		SOAPHeader header = envelope.getHeader();
	    
        // Удаляем имеющийся хедер, потому что он почему-то идет с другим префиксом
        if (header != null)
            envelope.removeChild(header);
        header = envelope.addHeader();
		
        SOAPElement element = header.addChildElement("Security", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");

		
        body.addAttribute(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id", "wsu"), ref);

        //XmlObject xo = XmlObject.Factory.parse(envelope);
        //String bodytxt = xo.xmlText();

		X509Certificate cert = loadCertificate(p12Path, pass.toCharArray());
		PrivateKey pk = loadPrivateKey(p12Path, pass.toCharArray());
		
		String signMethod = null;
		String digestMethod = null;
		String signedXml = null;

		String sigAlgOid = cert.getSigAlgOID();
		if (sigAlgOid.equals(PKCSObjectIdentifiers.sha1WithRSAEncryption.getId())) {
			signMethod = SIGN_METHOD_RSA;
			digestMethod = DIGEST_METHOD_RSA;
		} else if (sigAlgOid.equals(PKCSObjectIdentifiers.sha256WithRSAEncryption.getId())) {
			signMethod = SIGN_METHOD_RSA_256;
			digestMethod = DIGEST_METHOD_RSA_256;
		} else {
			signMethod = SIGN_METHOD_GOST;
			digestMethod = DIGEST_METHOD_GOST;
		}

		Document doc = envelope.getOwnerDocument();//createXmlDocumentFromString(bodytxt, "UTF-8");
		XMLSignature sig = new XMLSignature(doc, "", signMethod, Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);

		if (doc.getFirstChild() != null) {
			SOAPElement se = SOAPFactory.newInstance().createElement(sig.getElement());
	        element.addChildElement(se);

	        //doc.getFirstChild().appendChild();

			Transforms transforms = new Transforms(doc);
			//transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
			transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);

			sig.addDocument("#" + ref, transforms, digestMethod);
			sig.addKeyInfo((X509Certificate) cert);
			sig.sign(pk);

	        StringWriter os = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.transform(new DOMSource(doc), new StreamResult(os));
			os.flush();
			signedXml = os.toString();
			os.close();
		}
		
		return signedXml;
		int a = signedXml.lastIndexOf("<ds:Signature ");
		int b = signedXml.indexOf("</ds:Signature>", a);

		Element signNode = KalkanUtil.createXmlDocumentFromString(signedXml.substring(a, b + 15), "UTF-8").getDocumentElement();
        SOAPElement se = SOAPFactory.newInstance().createElement(signNode);
        element.addChildElement(se);
        
        
        XmlObject xo = XmlObject.Factory.parse(envelope);
        String envtxt = xo.xmlText();


		return envtxt;
	}
*/	
	
	private static void getCertificateFromP12toFile(String p12Path, String pd) throws Exception {
		X509Certificate certUp = loadCertificate(p12Path, pd.toCharArray());
		OutputStream ss = new FileOutputStream(p12Path.substring(0, p12Path.length() - 4) + ".cer");
		ss.write(certUp.getEncoded());
		ss.close();
	}
	
	
	public static void checkXml() throws Exception {
		FileInputStream fis = new FileInputStream("C:\\Users\\User\\Downloads\\Invalid.xml");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(fis);
		Element el = doc.getDocumentElement();
		
		//el = (Element)el.getElementsByTagName("data").item(0);
		
		CheckSignResult res1 = KalkanUtil.checkXML(el);
		System.out.println("checkingResult = " + res1);
		System.out.println("SIGNER TYPE: " + res1.getSignerType());
		System.out.println("ERROR: " + res1.getCertError());
		System.out.println("DN: " + res1.getSignerDN());

		System.out.println("isCertNew: " + res1.isCertNew());
		System.out.println("isCertOK: " + res1.isCertOK());
		System.out.println("isDigiSignOK: " + res1.isDigiSignOK());
		
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<root>"
                + "<person id=\"someid\">"
                + "<name>Стеве Жобс</name>"
                + "<iin>123456789012</iin>"
                + "</person>"
                + "</root>";
        
        String signedXmlString = signXml(xmlString, "", "D:\\erik\\keys\\erik\\2020\\RSA256_1f88b96ffb30776e86635a93560c318a98e70a19.p12", "1q2w3E$R");

        System.err.println(signedXmlString);

	}

	public static void checkSignedXml(String fileName, String certFileName) throws Exception {
		// ==========================================================================
		// Проверка подписанного ХМЛ
		// ==========================================================================
		FileInputStream fis1 = new FileInputStream(fileName);
		
		//String ssss = new String(Funcs.read("C:\\Users\\User\\Downloads\\GbdulSignupActualData_FinCentr_response.xml"));
		//ssss = ssss.replace("\r\n", "\n");
		//ByteArrayInputStream fis1 = new ByteArrayInputStream(ssss.getBytes());
		
//		Element eee = XmlUtil.createXmlDocumentFromFile("D:\\tmp\\RN\\USC_INIS_SDBRN_NOTIFPROPERTYPARTCANCEL2_inis.xml").getDocumentElement();
		SOAPMessage message = MessageFactory.newInstance().createMessage(null, fis1);
		fis1.close();
		
//		Node body = eee.getElementsByTagName("Body").item(0);
		
		SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        SOAPHeader header = envelope.getHeader();
        Iterator<?> iterator = header.getChildElements();
        boolean isExistsSignature = false;
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (object instanceof SOAPElement && "Security".equals(((SOAPElement) object).getLocalName())) {
            	isExistsSignature = true;
            	
            	System.out.println("ПРОВЕРКА ТРАНСПОРТНОЙ ПОДПИСИ");
            	CheckSignResult res1 = KalkanUtil.checkXML((SOAPElement) object, certFileName);
                System.out.println("checkingResult = " + res1);
        		System.out.println("SIGNER TYPE: " + res1.getSignerType());
        		System.out.println("ERROR: " + res1.getCertError());
        		System.out.println("DN: " + res1.getSignerDN());
        		System.out.println("isCertNew: " + res1.isCertNew());
        		System.out.println("isCertOK: " + res1.isCertOK());
        		System.out.println("isDigiSignOK: " + res1.isDigiSignOK());
            }
        }
        
        String serviceId = null;
        String senderId = null;
        Element data = null;
        
        SOAPBody body = envelope.getBody();
        Iterator iterator_1 = body.getChildElements();
        while (iterator_1.hasNext()) {
            Object object_1 = iterator_1.next();
            if (object_1 instanceof SOAPElement && ("SendMessage".equals(((SOAPElement) object_1).getLocalName()) || "SendMessageResponse".equals(((SOAPElement) object_1).getLocalName()))) {
                Iterator iterator_2 = ((SOAPElement) object_1).getChildElements();
                while (iterator_2.hasNext()) {
                    Object object_2 = iterator_2.next();
                    if (object_2 instanceof SOAPElement && ("request".equals(((SOAPElement) object_2).getLocalName()) || "response".equals(((SOAPElement) object_2).getLocalName()))) {
                        Iterator iterator_3 = ((SOAPElement) object_2).getChildElements();
                        while (iterator_3.hasNext()) {
                            Object object_3 = iterator_3.next();
                            if (object_3 instanceof SOAPElement && ("requestInfo".equals(((SOAPElement) object_3).getLocalName()))) {
                                Iterator iterator_4 = ((SOAPElement) object_3).getChildElements();
                                while (iterator_4.hasNext()) {
                                    Object object_4 = iterator_4.next();
                                    if (object_4 instanceof SOAPElement) {
                                        if ("serviceId".equals(((SOAPElement) object_4).getLocalName())) {
                                            serviceId = ((SOAPElement) object_4).getTextContent();
                                        } else if ("sender".equals(((SOAPElement) object_4).getLocalName())) {
                                            Iterator iterator_5 = ((SOAPElement) object_4).getChildElements();
                                            while (iterator_5.hasNext()) {
                                                Object object_5 = iterator_5.next();
                                                if (object_5 instanceof SOAPElement && ("senderId".equals(((SOAPElement) object_5).getLocalName()))) {
                                                    senderId = ((SOAPElement) object_5).getTextContent();
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (object_3 instanceof SOAPElement && ("requestData".equals(((SOAPElement) object_3).getLocalName()) || "responseData".equals(((SOAPElement) object_3).getLocalName()))) {
                                Iterator iterator_4 = ((SOAPElement) object_3).getChildElements();
                                while (iterator_4.hasNext()) {
                                    Object object_4 = iterator_4.next();
                                    if (object_4 instanceof SOAPElement && ("data".equals(((SOAPElement) object_4).getLocalName()))) {
                                        data = (SOAPElement) object_4;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    	System.out.println("ПРОВЕРКА БИЗНЕС ПОДПИСИ");

    	data = XmlUtil.createXmlDocumentFromString(data.getTextContent(), "UTF-8").getDocumentElement();
		//data = XmlUtil.createXmlDocumentFromFile("C:\\Users\\User\\Downloads\\Ерику3.xml").getDocumentElement();
		
        CheckSignResult res1 = KalkanUtil.checkXML(data);//.getTextContent());

        System.out.println("checkingResult = " + res1);
		System.out.println("SIGNER TYPE: " + res1.getSignerType());
		System.out.println("ERROR: " + res1.getCertError());
		System.out.println("DN: " + res1.getSignerDN());
		System.out.println("isCertNew: " + res1.isCertNew());
		System.out.println("isCertOK: " + res1.isCertOK());
		System.out.println("isDigiSignOK: " + res1.isDigiSignOK());

	}
	
	
	private static void test2015() throws Exception {
		String pass = "1q2w3E$R";
		//String keyFLPath = "D:\\work\\crypto_space\\Nit SDK\\SDK 2.0 2022-04\\GOST2015-TEST\\cert\\ФЛ\\user_634bf899f0b8ad4c4172b7a37bda09a56c4e1b29.p12";
		//String keyULBossPath = "D:\\work\\crypto_space\\Nit SDK\\SDK 2.0 2022-04\\GOST2015-TEST\\cert\\ЮЛ\\первый руководитель\\legal_chief_13227677b24b88af18451c892fa8d94cb4fd1c9c.p12";
		//String keyULSignPath = "D:\\work\\crypto_space\\Nit SDK\\SDK 2.0 2022-04\\GOST2015-TEST\\cert\\ЮЛ\\сотрудник с правом подписи\\legal_signer_30651e3ed1b1df573aab46f2d1cc6c898cd6a2f1.p12";
		//String keyULEmplPath = "D:\\work\\crypto_space\\Nit SDK\\SDK 2.0 2022-04\\GOST2015-TEST\\cert\\ЮЛ\\сотрудник организации\\legal_staff_a5735116498b0bdc29937af241a7e82e6a80a56f.p12";
		String keyErikPath = "D:\\erik\\keys\\erik\\2022\\AUTH_RSA256_091ef2c721d7830aa64cabb68e7abf30681fd439.p12";
		saveCertificateToFile(keyErikPath, pass.toCharArray(), keyErikPath.substring(0, keyErikPath.length() - 4) + ".cer");
		
		X509Certificate certFL = loadCertificate(keyErikPath, pass.toCharArray());
		printCertificateInfo(certFL);
		
		CheckSignResult res = new CheckSignResult();
		res = checkCertificate(res, certFL, true, ROOT_CERTS_FOLDER, OCSP_URL, null, null, new Date(), null);
		res.setDigiSignOK(true);
		
		System.out.println(res);
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
		
		String textToSign = "text to sign";
		String s1 = createPkcs7(keyErikPath, pass, textToSign, false);
		System.out.println("sign = " + s1);
		
		CheckSignResult resu = verifyPkcs7(textToSign, s1, false);
		
		System.out.println(resu);

        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<root>"
                + "<person id=\"someid\">"
                + "<name>Стеве Жобс</name>"
                + "<iin>123456789012</iin>"
                + "</person>"
                + "</root>";
        
        String signedXmlString = signXml(xmlString, "", keyErikPath, pass);

        System.err.println(signedXmlString);

		Element data = XmlUtil.createXmlDocumentFromString(signedXmlString, "UTF-8").getDocumentElement();
		
        CheckSignResult res1 = KalkanUtil.checkXML(data);//.getTextContent());

        System.out.println("checkingResult = " + res1);

	}
	
	public static void printCertificateInfo(X509Certificate cert) throws Exception {
		System.out.println("Basic Constraints: " + cert.getBasicConstraints());
		System.out.println("Signature Algorithm Name: " + cert.getSigAlgName());
		System.out.println("Signature Algorithm OID: " + cert.getSigAlgOID());
		System.out.println("Certificate Type: " + cert.getType());
		System.out.println("Version: " + cert.getVersion());
		
		System.out.println("Non Critical Extension OIDs");
		Collection<String> col = cert.getNonCriticalExtensionOIDs();
		for (String oid : col)
			System.out.println(" - " + oid);

		System.out.println("Critical Extension OIDs");
		col = cert.getCriticalExtensionOIDs();
		for (String oid : col)
			System.out.println(" - " + oid);
		
		System.out.println("Extended Key Usage");
		col = cert.getExtendedKeyUsage();
		for (String oid : col) {
			byte[] val = cert.getExtensionValue(oid);
			System.out.println(" - " + oid);
			System.out.println(" = " + derDecode(val));
		}

		System.out.println("Key Usage");
		boolean[] usage = cert.getKeyUsage();
		int k = 0;
		for (boolean b : usage) {
			System.out.print(++k + ". " + b + " ");
		}
		System.out.println();

		System.out.println("Issuer Alternative Names");
		Collection<List<?>> col2 = cert.getIssuerAlternativeNames();
		if (col2 != null) {
			k = 0;
			for (List<?> list : col2) {
				int j = 1;
				for (Object o : list ) {
					System.out.println(j + "." + ++k + ". " + o);
				}
				j++;
			}
		}

		System.out.println("Subject Alternative Names");
		col2 = cert.getSubjectAlternativeNames();
		if (col2 != null) {
			k = 0;
			for (List<?> list : col2) {
				int j = 1;
				for (Object o : list ) {
					System.out.println(j + "." + ++k + ". " + o);
				}
				j++;
			}
		}
		
		System.out.println("Serial Number: " + cert.getSerialNumber().toString(16));
		System.out.println("DN : " + cert.getSubjectDN().getName());
		System.out.println("Issuer: " + cert.getIssuerDN());
		System.out.println("from: " + cert.getNotBefore());
		System.out.println("to: " + cert.getNotAfter());
		
		System.out.println("Public key: " + new String (Hex.encode(cert.getPublicKey().getEncoded())));
	}
	
	private static String derDecode(byte[] encoded) throws IOException {
		if (encoded != null) {
			DERObject derObject = toDERObject(encoded);
	        if (derObject instanceof DEROctetString) {
	            DEROctetString derOctetString = (DEROctetString) derObject;
	
	            derObject = toDERObject(derOctetString.getOctets());
	            //if (derObject instanceof ASN1String) {
	            //    ASN1String s = (ASN1String)derObject;
	            //    decoded = s.getString();
	           // }
	
	        }
	        return derObject.toString();
		}
		return null;
	}
	
	private static DERObject toDERObject(byte[] data) throws IOException {
	    ByteArrayInputStream inStream = new ByteArrayInputStream(data);
	    ASN1InputStream asnInputStream = new ASN1InputStream(inStream);

	    return asnInputStream.readObject();
	}
	
	private static void testTransport() throws Exception {
		FileInputStream fis1 = new FileInputStream("D:\\tmp\\ul\\transport1.xml");
		
		SOAPMessage message = MessageFactory.newInstance().createMessage(null, fis1);
		fis1.close();

		SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        SOAPHeader header = envelope.getHeader();
        
        Iterator<?> iterator = header.getChildElements();
        
        boolean isExistsSignature = false;
        while (iterator.hasNext()) {
            Object object = iterator.next();
            if (object instanceof SOAPElement && "Security".equals(((SOAPElement) object).getLocalName())) {
            	isExistsSignature = true;
                System.out.println("isExistsSignature = " + isExistsSignature);
            
            	CheckSignResult res1 = KalkanUtil.checkXML((SOAPElement) object, "D:\\tmp\\ul\\ecps");
                System.out.println("checkingResult = " + res1);
            }
        }

	}
	
	public static void main(String[] args) throws Exception {
		

		/*
		 * getCertificateFromP12toFile("D:\\work\\crypto_space\\Nit SDK\\SDK 2.0 2022-09-26\\Keys and Certs\\Gost2015\\Юридическое лицо\\Сотрудник организации\\Valid\\GOST512_e3374f2c2f57472707135c6ec2ba20b8d32e4d49.p12"
		 * , "Aa123456");
		 * getCertificateFromP12toFile("D:\\work\\crypto_space\\Nit SDK\\SDK 2.0 2022-09-26\\Keys and Certs\\Gost2015\\Юридическое лицо\\Сотрудник организации\\Revoked\\GOST512_7f07c12eeeeb48e10ed3cd25d384172aee1d2ca9.p12"
		 * , "Aa123456");
		 * getCertificateFromP12toFile("D:\\work\\crypto_space\\Nit SDK\\SDK 2.0 2022-09-26\\Keys and Certs\\Gost2015\\Юридическое лицо\\Сотр. с правом подписи\\Valid\\GOST512_c8dcb2b732159598e03b6c0f603684945e56762f.p12"
		 * , "Aa123456");
		 * getCertificateFromP12toFile("D:\\work\\crypto_space\\Nit SDK\\SDK 2.0 2022-09-26\\Keys and Certs\\Gost2015\\Юридическое лицо\\Сотр. с правом подписи\\Revoked\\GOST512_5bd5d7cd87cd345a8dfc3c22523adf2061a7e41e.p12"
		 * , "Aa123456");
		 * getCertificateFromP12toFile("D:\\work\\crypto_space\\Nit SDK\\SDK 2.0 2022-09-26\\Keys and Certs\\Gost2015\\Юридическое лицо\\Первый рук\\Valid\\GOST512_1e1d2ce1161ca403c15f150c27825ad86eb0cb61.p12"
		 * , "Aa123456");
		 * getCertificateFromP12toFile("D:\\work\\crypto_space\\Nit SDK\\SDK 2.0 2022-09-26\\Keys and Certs\\Gost2015\\Юридическое лицо\\Первый рук\\Revoked\\GOST512_3a90343e720594c55204b19cb9a402422ef0e622.p12"
		 * , "Aa123456");
		 * getCertificateFromP12toFile("D:\\work\\crypto_space\\Nit SDK\\SDK 2.0 2022-09-26\\Keys and Certs\\Gost2015\\Юридическое лицо\\Казначейство клиент\\Valid\\GOST512_ab5e76d1a08f226171864fa5bb906e43837bb052.p12"
		 * , "Aa123456");
		 * getCertificateFromP12toFile("D:\\work\\crypto_space\\Nit SDK\\SDK 2.0 2022-09-26\\Keys and Certs\\Gost2015\\Юридическое лицо\\Казначейство клиент\\Revoked\\GOST512_fb7ddeb58e876b211626f0004f0ddedf9eeeea20.p12"
		 * , "Aa123456");
		 * 
		 * System.exit(0);;
		 */
		
		X509Certificate e1cert = getCertificate(new File("D:\\tmp\\ul\\ecps\\bankcert.cer"));
		System.out.println("e1cert = " + e1cert);
		
		FileInputStream e1fis = new FileInputStream("D:\\tmp\\ul\\ecps\\GbdulOnlineRegLeader_halykbank.xml");
		
		SOAPMessage e1message = MessageFactory.newInstance().createMessage(null, e1fis);
		e1fis.close();

		SOAPEnvelope e1envelope = e1message.getSOAPPart().getEnvelope();
        SOAPHeader e1header = e1envelope.getHeader();
        
        Iterator<?> e1iterator = e1header.getChildElements();
        
        boolean e1isExistsSignature = false;
        while (e1iterator.hasNext()) {
            Object object = e1iterator.next();
            if (object instanceof SOAPElement && "Security".equals(((SOAPElement) object).getLocalName())) {
            	e1isExistsSignature = true;
                System.out.println("isExistsSignature = " + e1isExistsSignature);
            
            	CheckSignResult res1 = KalkanUtil.checkXML((SOAPElement) object, "D:\\tmp\\ul\\ecps");
                System.out.println("checkingResult = " + res1);
            }
        }
        
    	SOAPBody e1body = e1envelope.getBody();
    	Iterator e1iterator_1 = e1body.getChildElements();
    	SOAPElement e1data = null;
    	while (e1iterator_1.hasNext()) {
    		Object object_1 = e1iterator_1.next();
    		if (object_1 instanceof SOAPElement && (("SendMessage".equals(((SOAPElement) object_1).getLocalName())) || ("SendMessageResponse".equals(((SOAPElement) object_1).getLocalName()))
    				|| ("sendMessage".equals(((SOAPElement) object_1).getLocalName())))) {
    			Iterator iterator_2 = ((SOAPElement) object_1).getChildElements();
    			while (iterator_2.hasNext()) {
    				Object object_2 = iterator_2.next();
    				if (object_2 instanceof SOAPElement && (("request".equals(((SOAPElement) object_2).getLocalName())) || ("response".equals(((SOAPElement) object_2).getLocalName())))) {
    					Iterator iterator_3 = ((SOAPElement) object_2).getChildElements();
    					while (iterator_3.hasNext()) {
    						Object object_3 = iterator_3.next();
    						if (object_3 instanceof SOAPElement && (("requestData".equals(((SOAPElement) object_3).getLocalName())) || ("responseData".equals(((SOAPElement) object_3).getLocalName()))
    								|| ("messageData".equals(((SOAPElement) object_3).getLocalName())))) {
    							Iterator iterator_4 = ((SOAPElement) object_3).getChildElements();
    							while (iterator_4.hasNext()) {
    								Object object_4 = iterator_4.next();
    								if (object_4 instanceof SOAPElement && ("data".equals(((SOAPElement) object_4).getLocalName()))) {
    									e1data = (SOAPElement) object_4;
    									break;
    								}
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    	
    	CheckSignResult e1res = KalkanUtil.checkXML(e1data.getTextContent());

        System.out.println("checkingResult = " + e1res);
		System.out.println("SIGNER TYPE: " + e1res.getSignerType());
		System.out.println("ERROR: " + e1res.getCertError());
		System.out.println("DN: " + e1res.getSignerDN());
		System.out.println("isCertNew: " + e1res.isCertNew());
		System.out.println("isCertOK: " + e1res.isCertOK());
		System.out.println("isDigiSignOK: " + e1res.isDigiSignOK());
		
		System.exit(0);

		String d1KeyPath = "D:\\tmp\\rddu\\2022-12-06-ezSigner\\RSA256_6074cc44643ab7115027a5ca5f44928eed5bffbd.p12";
		String d2KeyPath = "D:\\tmp\\rddu\\2022-12-06-ezSigner\\RSA256_571a55f44a105340d1f518ecb2793d996d3b1081.p12";
		String d1FilePath = "D:\\tmp\\rddu\\2022-12-06-ezSigner\\cms.to.sign.txt";
		
		byte d1BytesToCMS[] = Funcs.read(d1FilePath);
		String d1KeyPass = "1q2w3E$R";		
		String d2KeyPass = "1q2w3E$R";

		// Первая подпись
		byte[] d1ResCMS = createPkcs7(d1KeyPath, d1KeyPass, d1BytesToCMS, true, true);
		Thread.sleep(1000);
		// Вторая подпись
		byte[] d2ResCMS = createPkcs7(d2KeyPath, d2KeyPass, d1ResCMS, true, true);
		
		Funcs.write(d1ResCMS, new File(d1FilePath + ".cms"));
		Funcs.write(d2ResCMS, new File(d1FilePath + ".2cms"));

		System.exit(0);;
		
		
		String obj111 = "YWI5OWNmMWRiNWViZGM5MWRlZTIyOWQ0OTM0OGY0NTY1NjYwZDRlMA==";
		String sign111 = "MIIKSAYJKoZIhvcNAQcCoIIKOTCCCjUCAQExDzANBglghkgBZQMEAgEFADCCAWMGCSqGSIb3DQEHAaCCAVQEggFQeyJBcHBJZCI6IlBLQjE2MzQ4NTk0Mjc2NDQyMiIsInBheW1lbnQiOlt7ImFtb3VudCI6IjEwLjAiLCJiYW5rTmFtZSI6ItCQ0J4gS2FzcGkgQmFua"
				+ "yIsImJpayI6IkNBU1BLWktBIiwiY2FsbENlbnRlciI6Ijk5OTkiLCJudW1iZXJCYW5rVHJhbnNhY3Rpb24iOiIiLCJudW1iZXJDYXJkIjoiIiwicGF5Q29kZSI6ImMzYzBkZDc2LWJmNmItNDgyZC05ZjVjLTBmMTZmZmIxYTJiMSIsInBheURhdGUiOiIyMDIyLTEyLTA3VDEyOjE0"
				+ "OjE1LjE3MSIsInBheWVySWRuIjoiOTkwMzIwMzAxMTcxIiwicGF5RG9jSGFzaCI6IjU2MjMwMDI3MzllNDQwODViZjQ5ODQ5ZjEzZTY4MzdmNTU1MTk2MDYifV19oIIGeDCCBnQwggRcoAMCAQICFEBdYQgdNlwwNMM87UiORROvnYlsMA0GCSqGSIb3DQEBCwUAMFIxCzAJBgNVBAY"
				+ "TAktaMUMwQQYDVQQDDDrSsNCb0KLQotCr0pog0JrQo9OY0JvQkNCd0JTQq9Cg0KPQqNCrINCe0KDQotCQ0JvQq9KaIChSU0EpMB4XDTIxMTIxMjEwMjQxNFoXDTIyMTIxMjEwMjQxNFowgasxJjAkBgNVBAMMHdOY0JzQhtCg0JUg05jQkdCU0IbSmtCQ0JTQq9CgMRMwEQYDVQQEDA"
				+ "rTmNCc0IbQoNCVMRgwFgYDVQQFEw9JSU45OTAzMjAzMDExNzExCzAJBgNVBAYTAktaMR0wGwYDVQQqDBTQkNCc0JDQndCi0JDQmdKw0JvQqzEmMCQGCSqGSIb3DQEJARYXQU1JUkVFVi5BQk9LQUBZQU5ERVguS1owggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDeTTAEY"
				+ "vCEYnHS+q/cx2uu7Xy1rOr19z31liRMkC8553AWtcGpvi+4MnmKJ+c099yPAMIxOQ8ZN3EM4WwqL4nZiZGu2wut6P5Erlt9kQ1aK2cl/EGWmrtG/ENjf+TmDfqKUPN5adD4b7yIGcrfs+z9shAXE9Jo1O2vhCxzDmIxtSFD6UC6jKH3A0L8jNiAmNqcNL07Fvv9kMnITW5vLstKE3HX"
				+ "6DqdddgzpRsmglF7U0LYpu0cOw9GJ40SyJIyoY1a//bYd7Eur4geODcwQktQTt9IVggV23+ILGkllZIgBwBeBZ5N/zDTRUA2FQJQX6sXJK5G0NTIbvJ7qYMe0WoTAgMBAAGjggHmMIIB4jAOBgNVHQ8BAf8EBAMCBsAwKAYDVR0lBCEwHwYIKwYBBQUHAwQGCCqDDgMDBAEBBgkqgw4"
				+ "DAwQDAgEwDwYDVR0jBAgwBoAEW2p0ETAdBgNVHQ4EFgQUPgCeNRovltMZIpoZIR5kDFYDjJUwXgYDVR0gBFcwVTBTBgcqgw4DAwIDMEgwIQYIKwYBBQUHAgEWFWh0dHA6Ly9wa2kuZ292Lmt6L2NwczAjBggrBgEFBQcCAjAXDBVodHRwOi8vcGtpLmdvdi5rei9jcHMwVgYDVR0fBE"
				+ "8wTTBLoEmgR4YhaHR0cDovL2NybC5wa2kuZ292Lmt6L25jYV9yc2EuY3JshiJodHRwOi8vY3JsMS5wa2kuZ292Lmt6L25jYV9yc2EuY3JsMFoGA1UdLgRTMFEwT6BNoEuGI2h0dHA6Ly9jcmwucGtpLmdvdi5rei9uY2FfZF9yc2EuY3JshiRodHRwOi8vY3JsMS5wa2kuZ292Lmt6L"
				+ "25jYV9kX3JzYS5jcmwwYgYIKwYBBQUHAQEEVjBUMC4GCCsGAQUFBzAChiJodHRwOi8vcGtpLmdvdi5rei9jZXJ0L25jYV9yc2EuY2VyMCIGCCsGAQUFBzABhhZodHRwOi8vb2NzcC5wa2kuZ292Lmt6MA0GCSqGSIb3DQEBCwUAA4ICAQCn6kf2hU5ihFVHbZuVK/8YbLgqRiovifCp"
				+ "aEkhnkfm6tWJoTwuqKVKbwRTHrAez5H4vDga5/U2kLHqEAQGsH52yuqBG5CkCVRLNNX957efHlV6m4wjdVxU4aUvPFnPVP0ygSQa4D45jn/P8sh2BIPmtjYlMf0wCccOG2aER3e44bo5INxnxbRNZBpceAt1icctLWYuqgLRnpsUjWVu/34OECu0b1eSFEIMcnU2jhnupaclbt+K1hO"
				+ "WOeSR31N33cDmPpQPzhO+8ZhfNDYvDNSSe9Xv7QCL6pArhC05Qfvp2d0W7pHFTqG+xK8lUyhd92xcHbxuvlI6aoDLFDseDbxdGYQVscsEEiK/8fD/I5RLMvdex/bNhGgavI4zLTHoAkYS/usl6/9DbvvlL4+INVSmkeyGrz33CvdtTqFq/sZ92WFmJv8c95RoledmzMBmzB5isMin7v"
				+ "xLVFu0spq2G4l5rZM9ulkGQwB/rXhHJtSUfY72mnf3ZvcLCEJIzvq2E+YhyETFDAwz310G9WqCoeTR6oYVYRNMGlXGX7rqVkfEKTUtSqnrgEi7h9Z03hhIlkVS4x34ZOqTcOCtTM1RTb71s7jJgaXwO5Gl0UO3aVSMr7Yk0A5l04nh+jltiiYk+T22Kmu7sum/9Uf1mIRSH3ZNXsOpk"
				+ "8fpg7M7NlEbDb2WODGCAjowggI2AgEBMGowUjELMAkGA1UEBhMCS1oxQzBBBgNVBAMMOtKw0JvQotCi0KvSmiDQmtCj05jQm9CQ0J3QlNCr0KDQo9Co0Ksg0J7QoNCi0JDQm9Cr0pogKFJTQSkCFEBdYQgdNlwwNMM87UiORROvnYlsMA0GCWCGSAFlAwQCAQUAoIGiMBgGCSqGSIb3"
				+ "DQEJAzELBgkqhkiG9w0BBwEwHAYJKoZIhvcNAQkFMQ8XDTIyMTIwNzA2MTUxM1owLwYJKoZIhvcNAQkEMSIEIKOGogHH/UyMD3o5pYQNKRI10woMYwbkXYCkwltxqzYDMDcGCyqGSIb3DQEJEAIvMSgwJjAkMCIEIMtosWMFw8pKQfN190eaBp0qnNT4ia1RXWlRDgpGpalBMA0GCSq"
				+ "GSIb3DQEBCwUABIIBAJ201Gz6lT7s0DMfAXHByeO8Z3/xKed3a/h3CPBB95aLZKvec+qeEywUbf2F3Ow/qC7SYMA709bMmxG8SFmGOHZdPWK3cFNHzY/T/tf1+/t6DVOc7wj8N/VqK8yZ5gD9dT8Xmt1wr2S/xhacU5FtzSfDKi1/F8nLHRItSVHZxJvI+Jh3aCajHDfg+cDCoJWSr3"
				+ "U+WMCE63yaWUHCTlsMwHPX4vFZCfMZt3f1aBSl/E4WO+GD30JxsJKXsreSHMzBs/+r4A7zRlq/E8o/nhajfotfeDDU5ShcWnj8VN7/1xsvwClzvplyvNv0GUFtofi19DGupdHBc7LUSFXFXmjNpss=";
		
		verifyPkcs7(obj111.getBytes(), sign111, false);
		
		System.exit(0);;
		
		getCertificateFromP12toFile("D:\\work\\crypto_space\\Nit SDK\\SDK 2.0 2022-09-26\\Keys and Certs\\Gost2015\\Физическое лицо\\Valid\\GOST512_8ff06211dc82beb00253ffa000f1de84abef454f.p12", "Aa123456");
		
		signTransportXml("D:\\tmp\\rddu\\2022-11-04-vshep\\запрос.xml", "D:\\tmp\\rddu\\2022-11-04-vshep\\GOSTKNCA_0c467944edf80ddddc33bbeeed4712982de5bef7.p12 ", "A123456a", "bodyId");
		
		System.exit(0);;
		
		String pass1 = "Zz123456";
		String keyPath1 = "C:\\Users\\User\\Downloads\\Telegram Desktop\\GOSTKNCA_c3451d095ca04b6887109fb87741f598bef259f1.p12";
		X509Certificate cert1 = loadCertificate(keyPath1, pass1.toCharArray());
		
		OutputStream ss1 = new FileOutputStream("C:\\Users\\User\\Downloads\\Telegram Desktop\\GOSTKNCA_c3451d095ca04b6887109fb87741f598bef259f1.cer");
		ss1.write(cert1.getEncoded());
		ss1.close();
		System.exit(0);;
		
		testTransport();
		System.exit(0);;
		
		
		String pass = "1q2w3E$R";
		String passFL = "123456";
		String keyErikPath = "D:\\erik\\keys\\erik\\2022\\AUTH_RSA256_091ef2c721d7830aa64cabb68e7abf30681fd439.p12";
		//String keyErikPath = "D:\\erik\\keys\\erik\\2022\\RSA256_6074cc44643ab7115027a5ca5f44928eed5bffbd.p12";
		String keyFLPath = "D:\\work\\crypto_space\\Nit SDK\\SDK 2.0 2022-04\\GOST2015-TEST\\cert\\ФЛ\\user_634bf899f0b8ad4c4172b7a37bda09a56c4e1b29.p12";

		X509Certificate certFL = loadCertificate(keyErikPath, pass.toCharArray());
		printCertificateInfo(certFL);
		
		CheckSignResult res = new CheckSignResult();
		res = checkCertificate(res, certFL, true, ROOT_CERTS_FOLDER, OCSP_URL, null, null, new Date(), null);
		res.setDigiSignOK(true);
		System.out.println(res);
		System.exit(0);;
		
		test2015();
		System.exit(0);;
		
		checkSignedXml("C:\\Users\\User\\Downloads\\UL.xml", "C:\\Users\\User\\Downloads\\OpenKey_Жазыкбаев.cer");
		System.exit(0);
		
		
		FileInputStream fis = new FileInputStream("C:\\Users\\User\\Downloads\\mgs\\signed.txt");
		byte[] signedBytes = new byte[fis.available()];
		fis.read(signedBytes);
		
		X509Certificate cert3 = null;
		
		CMSSignedData signedData = new CMSSignedData(signedBytes);

		boolean isAttachedContent = signedData.getSignedContent() != null;

		if (isAttachedContent) {
			byte[] content = (byte[]) ((CMSProcessableByteArray)signedData.getSignedContent()).getContent();
			System.out.println(new String(content));
			System.out.println(new String(Base64.encode(content)));
			
			signedData = new CMSSignedData(signedData.getEncoded());
		} else {
//			CMSProcessableByteArray data = new CMSProcessableByteArray(dataToVerify);
			//signedData = new CMSSignedData(data, signedData.getEncoded());
		}

		SignerInformationStore signers = signedData.getSignerInfos();
		CertStore certs = signedData.getCertificatesAndCRLs("Collection", KalkanProvider.PROVIDER_NAME);
		Iterator<?> it = signers.getSigners().iterator();

		if (it.hasNext()) {
			SignerInformation signer = (SignerInformation) it.next();
			X509CertSelector signerConstraints = signer.getSID();
			Collection<?> certCollection = certs.getCertificates(signerConstraints);
			Iterator<?> certIt = certCollection.iterator();

			while (certIt.hasNext()) {
				cert3 = (X509Certificate) certIt.next();
				
				try {
					System.out.println("cert = " + new String(Base64.encode(cert3.getEncoded())));
					System.out.println("dn = " + new String(cert3.getSubjectDN().toString()));
				} catch (CertificateEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (signer.verify(cert3.getPublicKey(), KalkanProvider.PROVIDER_NAME)) {
					//res.setDigiSignOK(true);
					//res = checkCertificate(res, cert, auth, rootCertPath, ocspUrl, proxyHost, proxyPort, currentDate, doNotCheck);
				}
			}
		}

		
		//checkXml();
		System.exit(0);
		//getCertificateFromP12toFile("D:\\erik\\keys\\erik\\2020\\RSA256_1f88b96ffb30776e86635a93560c318a98e70a19.p12", "1q2w3E$R");
		//getCertificateFromP12toFile("D:\\erik\\keys\\erik\\2020\\AUTH_RSA256_0ab7c374acaf669af453b55c475d047dcdfa9ab9.p12", "1q2w3E$R");
		//System.exit(1);
		
		String toVerif = new String(Base64.encode("ABC".getBytes()));//new String(Base64.decode("RDAxRjI4OTVEMzUzQ0Q4NDAyMDBFMzBBRDRDRDdERTkxQUVGQTZCNA=="));
		System.out.println("data = " + new String(Base64.decode("ABC")));

		String s1 = createPkcs7("D:\\erik\\keys\\erik\\2021\\RSA256_78f510ce1446037e2a944cc0b85195bffbaadfe0.p12", "1q2w3E$R", "Yzg4N2E5NjRkYjI5ZTFlODBlODZkNmUwYWExNGUwOWEzN2U3ZmE2Mw==", false);
		System.out.println("sign = " + s1);
		String s2 = createPkcs7("D:\\erik\\keys\\erik\\2021\\RSA256_78f510ce1446037e2a944cc0b85195bffbaadfe0.p12", "1q2w3E$R", "Yzg4N2E5NjRkYjI5ZTFlODBlODZkNmUwYWExNGUwOWEzN2U3ZmE2Mw==", true);
		System.out.println("sign = " + s2);
		
		String data1 = "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";
		//X509Certificate cerr = getCertificate(data1.getBytes());
		//System.out.println("Certificate = " + cerr);

		CheckSignResult resu = verifyPkcs7(toVerif, data1, false);
		
		System.out.println("Result = " + resu);
		System.out.println("Result = " + resu.isDigiSignOK());
		
        String xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<root>"
                + "<person id=\"someid\">"
                + "<name>Стеве Жобс</name>"
                + "<iin>123456789012</iin>"
                + "</person>"
                + "</root>";
        
        String signedXmlString = signXml(xmlString, "", "D:\\erik\\keys\\erik\\2020\\RSA256_1f88b96ffb30776e86635a93560c318a98e70a19.p12", "1q2w3E$R");

        System.err.println(signedXmlString);
//		getCertificateFromP12toFile("D:\\public\\Ivan\\gost_FC.p12", "A123456i");

		X509Certificate cert = getCertificate(new File("D:\\tmp\\fc\\gost_FC.cer"));
		System.out.println(cert.getSerialNumber());

		cert = getCertificate(new File("D:\\tmp\\fc\\EGOV.KZ (1).cer"));
		System.out.println(cert.getSerialNumber());
		
		cert = getCertificate(new File("D:\\tmp\\fc\\shep.test.net.wss.cer"));
		System.out.println(cert.getSerialNumber());

		cert = getCertificate(new File("D:\\tmp\\fc\\test.nit.shep.crt"));
		System.out.println(cert.getSerialNumber());

		//X509Certificate cert = getCertificate(new File("D:\\tmp\\fc\\shep.test.net.wss.cer"));
		//System.out.println(cert.getSerialNumber());
		//System.out.println(cert);
		
		
		// ==========================================================================
				// Проверка подписанного ХМЛ
				// ==========================================================================
				FileInputStream fis1 = new FileInputStream("C:\\Users\\User\\Downloads\\Ерику.xml");
				
				//String ssss = new String(Funcs.read("C:\\Users\\User\\Downloads\\GbdulSignupActualData_FinCentr_response.xml"));
				//ssss = ssss.replace("\r\n", "\n");
				//ByteArrayInputStream fis1 = new ByteArrayInputStream(ssss.getBytes());
				
//				Element eee = XmlUtil.createXmlDocumentFromFile("D:\\tmp\\RN\\USC_INIS_SDBRN_NOTIFPROPERTYPARTCANCEL2_inis.xml").getDocumentElement();
				SOAPMessage message = MessageFactory.newInstance().createMessage(null, fis1);
				fis1.close();
				
//				Node body = eee.getElementsByTagName("Body").item(0);
				
				SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
		        SOAPHeader header = envelope.getHeader();
		        Iterator<?> iterator = header.getChildElements();
		        boolean isExistsSignature = false;
		        while (iterator.hasNext()) {
		            Object object = iterator.next();
		            if (object instanceof SOAPElement && "Security".equals(((SOAPElement) object).getLocalName())) {
		            	isExistsSignature = true;
		            	CheckSignResult res1 = KalkanUtil.checkXML((SOAPElement) object, "D:\\tmp\\fc\\shep.test.net.wss.cer");
		                System.out.println("checkingResult = " + res1);
		        		System.out.println("SIGNER TYPE: " + res1.getSignerType());
		        		System.out.println("ERROR: " + res1.getCertError());
		        		System.out.println("DN: " + res1.getSignerDN());
		        		System.out.println("isCertNew: " + res1.isCertNew());
		        		System.out.println("isCertOK: " + res1.isCertOK());
		        		System.out.println("isDigiSignOK: " + res1.isDigiSignOK());
		            }
		        }
		        
		        String serviceId = null;
		        String senderId = null;
		        Element data = null;
		        
		        SOAPBody body = envelope.getBody();
		        Iterator iterator_1 = body.getChildElements();
		        while (iterator_1.hasNext()) {
		            Object object_1 = iterator_1.next();
		            if (object_1 instanceof SOAPElement && ("SendMessage".equals(((SOAPElement) object_1).getLocalName()) || "SendMessageResponse".equals(((SOAPElement) object_1).getLocalName()))) {
		                Iterator iterator_2 = ((SOAPElement) object_1).getChildElements();
		                while (iterator_2.hasNext()) {
		                    Object object_2 = iterator_2.next();
		                    if (object_2 instanceof SOAPElement && ("request".equals(((SOAPElement) object_2).getLocalName()) || "response".equals(((SOAPElement) object_2).getLocalName()))) {
		                        Iterator iterator_3 = ((SOAPElement) object_2).getChildElements();
		                        while (iterator_3.hasNext()) {
		                            Object object_3 = iterator_3.next();
		                            if (object_3 instanceof SOAPElement && ("requestInfo".equals(((SOAPElement) object_3).getLocalName()))) {
		                                Iterator iterator_4 = ((SOAPElement) object_3).getChildElements();
		                                while (iterator_4.hasNext()) {
		                                    Object object_4 = iterator_4.next();
		                                    if (object_4 instanceof SOAPElement) {
		                                        if ("serviceId".equals(((SOAPElement) object_4).getLocalName())) {
		                                            serviceId = ((SOAPElement) object_4).getTextContent();
		                                        } else if ("sender".equals(((SOAPElement) object_4).getLocalName())) {
		                                            Iterator iterator_5 = ((SOAPElement) object_4).getChildElements();
		                                            while (iterator_5.hasNext()) {
		                                                Object object_5 = iterator_5.next();
		                                                if (object_5 instanceof SOAPElement && ("senderId".equals(((SOAPElement) object_5).getLocalName()))) {
		                                                    senderId = ((SOAPElement) object_5).getTextContent();
		                                                }
		                                            }
		                                        }
		                                    }
		                                }
		                            } else if (object_3 instanceof SOAPElement && ("requestData".equals(((SOAPElement) object_3).getLocalName()) || "responseData".equals(((SOAPElement) object_3).getLocalName()))) {
		                                Iterator iterator_4 = ((SOAPElement) object_3).getChildElements();
		                                while (iterator_4.hasNext()) {
		                                    Object object_4 = iterator_4.next();
		                                    if (object_4 instanceof SOAPElement && ("data".equals(((SOAPElement) object_4).getLocalName()))) {
		                                        data = (SOAPElement) object_4;
		                                        break;
		                                    }
		                                }
		                            }
		                        }
		                    }
		                }
		            }
		        }

				//data = XmlUtil.createXmlDocumentFromString(data.getTextContent(), "UTF-8").getDocumentElement();
				data = XmlUtil.createXmlDocumentFromFile("C:\\Users\\User\\Downloads\\Ерику3.xml").getDocumentElement();
				
		        CheckSignResult res1 = KalkanUtil.checkXML(data);//.getTextContent());

		        System.out.println("checkingResult = " + res1);
				System.out.println("SIGNER TYPE: " + res1.getSignerType());
				System.out.println("ERROR: " + res1.getCertError());
				System.out.println("DN: " + res1.getSignerDN());
				System.out.println("isCertNew: " + res1.isCertNew());
				System.out.println("isCertOK: " + res1.isCertOK());
				System.out.println("isDigiSignOK: " + res1.isDigiSignOK());
				
				System.exit(0);
		//getCertificateFromP12toFile("D:\\erik\\keys\\erik\\2018-2\\RSA256_c5901aec52004d85e72f4093fdb6d09797e82da9.p12", "1q2w3e4R");
		//getCertificateFromP12toFile("D:\\erik\\keys\\erik\\2018-2\\AUTH_RSA256_20b1ba1d17c3c1c8a8c8d365c1daa0b11b2d9373.p12", "1q2w3e4R");
		
		//getCertificateFromP12toFile("D:\\erik\\keys\\aliya\\2019\\RSA256_88ca8c6394e0b1f5e67abd5c82076fe31bcebc6f.p12", "1q2w3E$R");
		//getCertificateFromP12toFile("D:\\erik\\keys\\aliya\\2019\\AUTH_RSA256_6b77349b837e2a028d217d03b970b9d7966c84f2.p12", "1q2w3E$R");
		
		getCertificateFromP12toFile("D:\\public\\Ivan\\GOSTKNCA_50c41de6162b8e33dad678581e19c73b816491ba.p12", "12qazZAQ");
		 
		//getCertificateFromP12toFile("D:\\erik\\keys\\berik\\2018\\GOSTKNCA_6768816073e77c5e26d10d5215938ab0164649b8.p12", "Votsys78");
		//getCertificateFromP12toFile("D:\\erik\\keys\\berik\\2018\\AUTH_RSA256_15b8483dbebe28643b06201422752df3ef8e3ca5.p12", "Votsys78");
		
		//getCertificateFromP12toFile("D:\\erik\\keys\\tamur_systems\\2019\\AUTH_RSA256_0c0fc95bbba05e220cf5e6d6be77f2f785981600.p12", "1q2w3E$R");
		//getCertificateFromP12toFile("D:\\erik\\keys\\tamur_systems\\2019\\GOSTKNCA_9cb464f58333fd48269c886c70c09b5a4f58e140.p12", "1q2w3E$R");
		
		System.exit(0);
		
		X509Certificate certUp = loadCertificate("F:\\PUBLIC\\RN\\gost_fc.p12",
				"A123456i".toCharArray());
		OutputStream ss = new FileOutputStream("F:\\PUBLIC\\RN\\gost_fc.cer");
		ss.write(certUp.getEncoded());
		ss.close();
		

		testRenameNamespace();
		System.exit(0);

		X509Certificate certificate = getCertificate(Base64.decode("MIIDmTCCA0OgAwIBAgIUT5B+ric2M3WTkXa03NwXurenxGAwDQYJKoMOAwoBAQECBQAwgZ8xCzAJBgNVBAYTAktaMTAwLgYDVQQKDCfQoNC10YHQv9GD0LHQu9C40LrQsCDQmtCw0LfQsNGF0YHRgtCw0L0xXjBcBgNVBAMMVdCj0LTQvtGB0YLQvtCy0LXRgNGP0Y7RidC40Lkg0YbQtdC90YLRgCDQk9C+0YHRg9C00LDRgNGB0YLQstC10L3QvdGL0YUg0L7RgNCz0LDQvdC+0LIwHhcNMTcxMDExMDU1NjM2WhcNMTgxMDExMDYwMTM2WjCB0DELMAkGA1UEBhMCS1oxFTATBgNVBAgMDNCQ0YHRgtCw0L3QsDEYMBYGA1UECxMPQklOMTUxMjQwMDIyNTQ1MSwwKgYDVQQDDCPQlNCQ0J3QldCR0JXQmtCe0JIg0J7QndCT0JDQoNCR0JDQmTEYMBYGA1UEBRMPSUlONjUwMTAzMzAxNTA4MR0wGwYDVQQqDBTQltCY0JTQldCR0JDQldCS0JjQpzEpMCcGCSqGSIb3DQEJARYaby5kYW5lYmVrb3ZAIGt5em1ldC5nb3Yua3owbDAlBgkqgw4DCgEBAQEwGAYKKoMOAwoBAQEBAQYKKoMOAwoBAwEBAANDAARAC+fuwcISlXGFJn4gBcmRSjT+OhjNFL2BZtKrWo07bAdDfrA7lpipS6eauR64ewSyVV3StTDeD3CPAtFY7ec15KOCARIwggEOMAsGA1UdDwQEAwIGwDAdBgNVHQ4EFgQUz5B+ric2M3WTkXa03NwXurenxGAwgd8GA1UdIwSB1zCB1IAUHge2xo/6AcNeE67+sSdCNAindbuhgaWkgaIwgZ8xCzAJBgNVBAYTAktaMTAwLgYDVQQKDCfQoNC10YHQv9GD0LHQu9C40LrQsCDQmtCw0LfQsNGF0YHRgtCw0L0xXjBcBgNVBAMMVdCj0LTQvtGB0YLQvtCy0LXRgNGP0Y7RidC40Lkg0YbQtdC90YLRgCDQk9C+0YHRg9C00LDRgNGB0YLQstC10L3QvdGL0YUg0L7RgNCz0LDQvdC+0LKCFB4HtsaP+gHDXhOu/rEnQjQIp3W7MA0GCSqDDgMKAQEBAgUAA0EALoq/6LDsHOWck94eDf3qU+YDUdMIOr5md4Zl6RMlZUqh37GTl44xPGGbycoM+wEH6AMmlHFgFWP3ZB/ETrWjgQ=="));
		
		String rrr = null;
		if (certificate.getSigAlgOID().equals(PKCSObjectIdentifiers.sha1WithRSAEncryption.getId())) {
			rrr = CMSSignedDataGenerator.DIGEST_SHA1;
		} else if (certificate.getSigAlgOID().equals(PKCSObjectIdentifiers.sha256WithRSAEncryption.getId())) {
			rrr = CMSSignedDataGenerator.DIGEST_SHA256;
		} else if (certificate.getSigAlgOID().equals(KNCAObjectIdentifiers.gost34311_95_with_gost34310_2004.getId())) {
			rrr = CMSSignedDataGenerator.DIGEST_GOST34311_95;
		} else if (certificate.getSigAlgOID()
				.equals(CryptoProObjectIdentifiers.gostR3411_94_with_gostR34310_2004.getId())) {
			rrr = CMSSignedDataGenerator.DIGEST_GOST3411_GT;
		}

		
		CheckSignResult rr =  verifyPlainData("111".getBytes(),
				"zKKD+bRL7qY6Txg5dyr2m4rBsmMlBtcCod3sF/5XXMMEXr0l5P5qmRiKdqAUdx63vgGIpuhB2e2UKTNiTA9eKQ==".getBytes(),
				Base64.decode("MIIDmTCCA0OgAwIBAgIUT5B+ric2M3WTkXa03NwXurenxGAwDQYJKoMOAwoBAQECBQAwgZ8xCzAJBgNVBAYTAktaMTAwLgYDVQQKDCfQoNC10YHQv9GD0LHQu9C40LrQsCDQmtCw0LfQsNGF0YHRgtCw0L0xXjBcBgNVBAMMVdCj0LTQvtGB0YLQvtCy0LXRgNGP0Y7RidC40Lkg0YbQtdC90YLRgCDQk9C+0YHRg9C00LDRgNGB0YLQstC10L3QvdGL0YUg0L7RgNCz0LDQvdC+0LIwHhcNMTcxMDExMDU1NjM2WhcNMTgxMDExMDYwMTM2WjCB0DELMAkGA1UEBhMCS1oxFTATBgNVBAgMDNCQ0YHRgtCw0L3QsDEYMBYGA1UECxMPQklOMTUxMjQwMDIyNTQ1MSwwKgYDVQQDDCPQlNCQ0J3QldCR0JXQmtCe0JIg0J7QndCT0JDQoNCR0JDQmTEYMBYGA1UEBRMPSUlONjUwMTAzMzAxNTA4MR0wGwYDVQQqDBTQltCY0JTQldCR0JDQldCS0JjQpzEpMCcGCSqGSIb3DQEJARYaby5kYW5lYmVrb3ZAIGt5em1ldC5nb3Yua3owbDAlBgkqgw4DCgEBAQEwGAYKKoMOAwoBAQEBAQYKKoMOAwoBAwEBAANDAARAC+fuwcISlXGFJn4gBcmRSjT+OhjNFL2BZtKrWo07bAdDfrA7lpipS6eauR64ewSyVV3StTDeD3CPAtFY7ec15KOCARIwggEOMAsGA1UdDwQEAwIGwDAdBgNVHQ4EFgQUz5B+ric2M3WTkXa03NwXurenxGAwgd8GA1UdIwSB1zCB1IAUHge2xo/6AcNeE67+sSdCNAindbuhgaWkgaIwgZ8xCzAJBgNVBAYTAktaMTAwLgYDVQQKDCfQoNC10YHQv9GD0LHQu9C40LrQsCDQmtCw0LfQsNGF0YHRgtCw0L0xXjBcBgNVBAMMVdCj0LTQvtGB0YLQvtCy0LXRgNGP0Y7RidC40Lkg0YbQtdC90YLRgCDQk9C+0YHRg9C00LDRgNGB0YLQstC10L3QvdGL0YUg0L7RgNCz0LDQvdC+0LKCFB4HtsaP+gHDXhOu/rEnQjQIp3W7MA0GCSqDDgMKAQEBAgUAA0EALoq/6LDsHOWck94eDf3qU+YDUdMIOr5md4Zl6RMlZUqh37GTl44xPGGbycoM+wEH6AMmlHFgFWP3ZB/ETrWjgQ=="));
		
		String dataToVerify = "444444444444444444444";
		System.out.println(new String(Base64.encode(dataToVerify.getBytes())));
		
		//String signToVerify = "MIAGCSqGSIb3DQEHAqCAMIACAQExDjAMBggqgw4DCgEDAQUAMIAGCSqGSIb3DQEHAaCAJIAEFTQ0NDQ0NDQ0NDQ0NDQ0NDQ0NDQ0NAAAAAAAAKCAMIIDmTCCA0OgAwIBAgIUT5B+ric2M3WTkXa03NwXurenxGAwDQYJKoMOAwoBAQECBQAwgZ8xCzAJBgNVBAYTAktaMTAwLgYDVQQKDCfQoNC10YHQv9GD0LHQu9C40LrQsCDQmtCw0LfQsNGF0YHRgtCw0L0xXjBcBgNVBAMMVdCj0LTQvtGB0YLQvtCy0LXRgNGP0Y7RidC40Lkg0YbQtdC90YLRgCDQk9C+0YHRg9C00LDRgNGB0YLQstC10L3QvdGL0YUg0L7RgNCz0LDQvdC+0LIwHhcNMTcxMDExMDU1NjM2WhcNMTgxMDExMDYwMTM2WjCB0DELMAkGA1UEBhMCS1oxFTATBgNVBAgMDNCQ0YHRgtCw0L3QsDEYMBYGA1UECxMPQklOMTUxMjQwMDIyNTQ1MSwwKgYDVQQDDCPQlNCQ0J3QldCR0JXQmtCe0JIg0J7QndCT0JDQoNCR0JDQmTEYMBYGA1UEBRMPSUlONjUwMTAzMzAxNTA4MR0wGwYDVQQqDBTQltCY0JTQldCR0JDQldCS0JjQpzEpMCcGCSqGSIb3DQEJARYaby5kYW5lYmVrb3ZAIGt5em1ldC5nb3Yua3owbDAlBgkqgw4DCgEBAQEwGAYKKoMOAwoBAQEBAQYKKoMOAwoBAwEBAANDAARAC+fuwcISlXGFJn4gBcmRSjT+OhjNFL2BZtKrWo07bAdDfrA7lpipS6eauR64ewSyVV3StTDeD3CPAtFY7ec15KOCARIwggEOMAsGA1UdDwQEAwIGwDAdBgNVHQ4EFgQUz5B+ric2M3WTkXa03NwXurenxGAwgd8GA1UdIwSB1zCB1IAUHge2xo/6AcNeE67+sSdCNAindbuhgaWkgaIwgZ8xCzAJBgNVBAYTAktaMTAwLgYDVQQKDCfQoNC10YHQv9GD0LHQu9C40LrQsCDQmtCw0LfQsNGF0YHRgtCw0L0xXjBcBgNVBAMMVdCj0LTQvtGB0YLQvtCy0LXRgNGP0Y7RidC40Lkg0YbQtdC90YLRgCDQk9C+0YHRg9C00LDRgNGB0YLQstC10L3QvdGL0YUg0L7RgNCz0LDQvdC+0LKCFB4HtsaP+gHDXhOu/rEnQjQIp3W7MA0GCSqDDgMKAQEBAgUAA0EALoq/6LDsHOWck94eDf3qU+YDUdMIOr5md4Zl6RMlZUqh37GTl44xPGGbycoM+wEH6AMmlHFgFWP3ZB/ETrWjgQAAMYIBjDCCAYgCAQEwgbgwgZ8xCzAJBgNVBAYTAktaMTAwLgYDVQQKDCfQoNC10YHQv9GD0LHQu9C40LrQsCDQmtCw0LfQsNGF0YHRgtCw0L0xXjBcBgNVBAMMVdCj0LTQvtGB0YLQvtCy0LXRgNGP0Y7RidC40Lkg0YbQtdC90YLRgCDQk9C+0YHRg9C00LDRgNGB0YLQstC10L3QvdGL0YUg0L7RgNCz0LDQvdC+0LICFE+Qfq4nNjN1k5F2tNzcF7q3p8RgMAwGCCqDDgMKAQMBBQCgaTAYBgkqhkiG9w0BCQMxCwYJKoZIhvcNAQcBMBwGCSqGSIb3DQEJBTEPFw0xODA4MjgwNTU5MTlaMC8GCSqGSIb3DQEJBDEiBCBWB+Y2Rsp4g/PpAqo+4mrydIeQgIN9yuebcevkJkgmBzANBgkqgw4DCgEBAQIFAARA0xFI7k1AxSwkecEuK/r9k/CE78tS6CS1wK8QrK4RmOpeiRZf6aYBPCub9mcKp2fEfDO9SNB89fifTBd2fUx7twAAAAAAAA==";
		String signToVerify = "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";
		//String signToVerify = "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";
		//System.out.println(new String(Base64.decode("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")));

		verifyPkcs7(dataToVerify, signToVerify, false);
		
		System.exit(1);
		
		

		
/*		String xmlStr1 = new String(b1, "UTF-8");

		CheckSignResult res1 = checkXML(xmlStr1);
		System.out.println("SIGNER TYPE: " + res1.getSignerType());
		System.out.println("ERROR: " + res1.getCertError());
		System.out.println("DN: " + res1.getSignerDN());
		System.out.println("isCertNew: " + res1.isCertNew());
		System.out.println("isCertOK: " + res1.isCertOK());
		System.out.println("isDigiSignOK: " + res1.isDigiSignOK());
*/
		System.exit(0);
		// ==========================================================================

/*		X509Certificate certUp = loadCertificate("F:\\erik\\GOSTKNCA_6143defe0281e1793127dc1d9fab8167770f0d46.p12",
				"12qazZAQ".toCharArray());
		OutputStream ss = new FileOutputStream("F:\\erik\\GOSTKNCA_6143defe0281e1793127dc1d9fab8167770f0d46.cer");
		ss.write(certUp.getEncoded());
		ss.close();
*/		
/*		X509Certificate certUp = loadCertificate("F:\\erik\\KEYS\\erik\\erik_sign_2016.p12",
				"123456".toCharArray());
		OutputStream ss = new FileOutputStream("F:\\erik\\KEYS\\erik\\erik_sign_2016.p12.cer");
		ss.write(certUp.getEncoded());
		ss.close();
*/
//		X509Certificate cert = getCertificate(new File("F:\\PUBLIC\\CRYPTO\\websocket2\\1.cer"));
/*		X509Certificate cert = getCertificate(Base64.decode("MIIETTCCA/egAwIBAgIUGI+ZbL9TD+yNmfc2rEyToQAmIIQwDQYJKoMOAwoBAQECBQAwgZ8xCzAJBgNVBAYTAktaMTAwLgYDVQQKDCfQoNC10YHQv9GD0LHQu9C40LrQsCDQmtCw0LfQsNGF0YHRgtCw0L0xXjBcBgNVBAMMVdCj0LTQvtGB0YLQvtCy0LXRgNGP0Y7RidC40Lkg0YbQtdC90YLRgCDQk9C+0YHRg9C00LDRgNGB0YLQstC10L3QvdGL0YUg0L7RgNCz0LDQvdC+0LIwHhcNMTcxMjA0MTAxMzM3WhcNMTgxMjA0MTAxODM3WjCCAYMxCzAJBgNVBAYTAktaMRUwEwYDVQQIDAzQkNGB0YLQsNC90LAxGDAWBgNVBAsTD0JJTjE1MTI0MDAyMjU0NTEmMCQGA1UEAwwd0JDQu9C10LrQtdGI0LXQsiDQqNGL0qPSk9GL0YExITAfBgNVBCoMGNCQ0LzQsNC90LPQtdC70LTRltKx0LvRizEYMBYGA1UEBRMPSUlOODgwNTI0MzAwODMzMYG2MIGzBgNVBAoMgavQkNCz0LXQvdGC0YHRgtCy0L4g0KDQtdGB0L/Rg9Cx0LvQuNC60Lgg0JrQsNC30LDRhdGB0YLQsNC9INC/0L4g0LTQtdC70LDQvCDQs9C+0YHRg9C00LDRgNGB0YLQstC10L3QvdC+0Lkg0YHQu9GD0LbQsdGLINC4INC/0YDQvtGC0LjQstC+0LTQtdC50YHRgtCy0LjRjiDQutC+0YDRgNGD0L/RhtC40LgxJTAjBgkqhkiG9w0BCQEWFmNoLmFsZWtlc2hldkBnbWFpbC5jb20wbDAlBgkqgw4DCgEBAQEwGAYKKoMOAwoBAQEBAQYKKoMOAwoBAwEBAANDAARAhcNU/IPNil1GHQ3ob3+RJeeOTqLT+2XZ27JYs29xnnqKYHDccrZnzTbFDYQrVGAmx+HC7CFCBKAot211oiFva6OCARIwggEOMAsGA1UdDwQEAwIGwDAdBgNVHQ4EFgQUmI+ZbL9TD+yNmfc2rEyToQAmIIQwgd8GA1UdIwSB1zCB1IAUHge2xo/6AcNeE67+sSdCNAindbuhgaWkgaIwgZ8xCzAJBgNVBAYTAktaMTAwLgYDVQQKDCfQoNC10YHQv9GD0LHQu9C40LrQsCDQmtCw0LfQsNGF0YHRgtCw0L0xXjBcBgNVBAMMVdCj0LTQvtGB0YLQvtCy0LXRgNGP0Y7RidC40Lkg0YbQtdC90YLRgCDQk9C+0YHRg9C00LDRgNGB0YLQstC10L3QvdGL0YUg0L7RgNCz0LDQvdC+0LKCFB4HtsaP+gHDXhOu/rEnQjQIp3W7MA0GCSqDDgMKAQEBAgUAA0EAuTQ59wGTnx08JOybivSo/XAxOS7js2M+ihJF3OP7GfPyhRa6mON5FN7i8uB3pd3mVJmVaVmzxyekXdNbVsvLTw==".getBytes()));
		CheckSignResult res = new CheckSignResult();
		checkCertificate(res, cert, false, ROOT_CERTS_FOLDER, OCSP_URL, null, null, new Date(), null);
		
		System.exit(0);
*/
	}
		/*
		try {
			try {
				String envtxt = signTransportXml("G:\\tmp\\ivan\\ответ_рн_wss.xml", "G:\\tmp\\ivan\\GOSTKNCA_806ab2677ac5a3d78f461ee459a9fb1e0f4ff4b1.p12", "12qazZAQ", "bodyId");
				FileOutputStream envOut = new FileOutputStream("G:\\tmp\\ivan\\ответ_рн_wss.signed.xml");
				envOut.write(envtxt.getBytes());
				envOut.close();
				
				System.out.println(envtxt);
				
				//checkXML(envtxt);
				
				checkTransportXml("F:\\tmp\\ekyzmet\\transport.signed.xml", "G:\\tmp\\ivan\\GOSTKNCA_806ab2677ac5a3d78f461ee459a9fb1e0f4ff4b1.cer");
				//checkTransportXml("G:\\tmp\\ivan\\запрос_шэп_wss.txt", "G:\\tmp\\ivan\\test.crt");
				System.exit(0);

				// Чтение сертификата из файла
				CertificateFactory cf = CertificateFactory.getInstance("X.509", KalkanProvider.PROVIDER_NAME);
				
				InputStream is = new FileInputStream("G:\\tmp\\kyzmet\\УЦГО\\cert_kair.cer");
				X509Certificate testCert = (X509Certificate) cf.generateCertificate(is);
				is.close();
				
				File dir = new File("D:\\distr\\crypto\\УЦГО\\ROOT_CERTS");
				if (dir != null && dir.exists()) {
					File[] fs = dir.listFiles();

					for (File f : fs) {
						FileInputStream fis = new FileInputStream(f);
						X509Certificate c = (X509Certificate) cf.generateCertificate(fis);
						System.out.println(c.getSerialNumber().toString(16));
						fis.close();
						try {
							testCert.verify(c.getPublicKey(), KalkanProvider.PROVIDER_NAME);
							System.out.println(f.getAbsolutePath() + ": checking = " + true);
						} catch (Exception e) {
							System.out.println(f.getAbsolutePath() + ": checking = " + false);
							e.printStackTrace();
						}
					}
				}

				System.exit(0);
				// ==========================================================================
				// Вытаскивание сертификата в файл
				// ==========================================================================
				X509Certificate certUp = loadCertificate("F:\\erik\\GOSTKNCA_806ab2677ac5a3d78f461ee459a9fb1e0f4ff4b1.p12",
						"12qazZAQ".toCharArray());
				OutputStream ss = new FileOutputStream("F:\\erik\\GOSTKNCA_806ab2677ac5a3d78f461ee459a9fb1e0f4ff4b1.cer");
				ss.write(certUp.getEncoded());
				ss.close();
				
				System.exit(0);

				certUp = loadCertificate("\\\\IVAN-PC\\public\\erik\\RSA256_37e72c2359b5e3da2a0280550d8d86644deecdba.p12", "123456".toCharArray());
				ss = new FileOutputStream("\\\\IVAN-PC\\public\\erik\\RSA256_37e72c2359b5e3da2a0280550d8d86644deecdba.cer");
				ss.write(certUp.getEncoded());
				ss.close();

				
				 * certUp =
				 * loadCertificate("D:\\distr\\crypto\\TEST\\keys\\fl12.p12",
				 * "123456".toCharArray()); ss = new
				 * FileOutputStream("D:\\distr\\crypto\\TEST\\certs\\fl12.cer");
				 * ss.write(certUp.getEncoded()); ss.close(); certUp =
				 * loadCertificate("D:\\distr\\crypto\\TEST\\keys\\fl21.p12",
				 * "123456".toCharArray()); ss = new
				 * FileOutputStream("D:\\distr\\crypto\\TEST\\certs\\fl21.cer");
				 * ss.write(certUp.getEncoded()); ss.close(); certUp =
				 * loadCertificate("D:\\distr\\crypto\\TEST\\keys\\fl22.p12",
				 * "123456".toCharArray()); ss = new
				 * FileOutputStream("D:\\distr\\crypto\\TEST\\certs\\fl22.cer");
				 * ss.write(certUp.getEncoded()); ss.close(); certUp =
				 * loadCertificate("D:\\distr\\crypto\\TEST\\keys\\fl31.p12",
				 * "123456".toCharArray()); ss = new
				 * FileOutputStream("D:\\distr\\crypto\\TEST\\certs\\fl31.cer");
				 * ss.write(certUp.getEncoded()); ss.close(); certUp =
				 * loadCertificate("D:\\distr\\crypto\\TEST\\keys\\fl32.p12",
				 * "123456".toCharArray()); ss = new
				 * FileOutputStream("D:\\distr\\crypto\\TEST\\certs\\fl32.cer");
				 * ss.write(certUp.getEncoded()); ss.close(); certUp =
				 * loadCertificate("D:\\distr\\crypto\\TEST\\keys\\fl41.p12",
				 * "123456".toCharArray()); ss = new
				 * FileOutputStream("D:\\distr\\crypto\\TEST\\certs\\fl41.cer");
				 * ss.write(certUp.getEncoded()); ss.close(); certUp =
				 * loadCertificate("D:\\distr\\crypto\\TEST\\keys\\fl42.p12",
				 * "123456".toCharArray()); ss = new
				 * FileOutputStream("D:\\distr\\crypto\\TEST\\certs\\fl42.cer");
				 * ss.write(certUp.getEncoded()); ss.close(); certUp =
				 * loadCertificate("D:\\distr\\crypto\\TEST\\keys\\fl51.p12",
				 * "123456".toCharArray()); ss = new
				 * FileOutputStream("D:\\distr\\crypto\\TEST\\certs\\fl51.cer");
				 * ss.write(certUp.getEncoded()); ss.close(); certUp =
				 * loadCertificate("D:\\distr\\crypto\\TEST\\keys\\fl52.p12",
				 * "123456".toCharArray()); ss = new
				 * FileOutputStream("D:\\distr\\crypto\\TEST\\certs\\fl52.cer");
				 * ss.write(certUp.getEncoded()); ss.close();
				 * 
				 * //ss1.write(Base64.encode(certUp.getEncoded()).getBytes());
				 * //ss1.close(); System.exit(0);
				 
				// ==========================================================================

				// ==========================================================================
				// Подписывание ХМЛ
				// ==========================================================================
				FileInputStream fis = new FileInputStream("F:\\tmp\\ekyzmet\\transport.xml");
				byte[] b = new byte[fis.available()];
				fis.read(b);
				fis.close();
				
				String xmlStr = Funcs.normalizeInput(new String(b, "UTF-8"));

				Document doc = createXmlDocumentFromString(xmlStr, "UTF-8");

				SOAPEnvelope envelope = (SOAPEnvelope) SOAPFactory.newInstance()
						.createElement(doc.getDocumentElement());
				envelope.addNamespaceDeclaration("wsu",
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
				envelope.addNamespaceDeclaration("wsse",
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
				SOAPHeader header = envelope.getHeader();
				if (header == null)
					header = envelope.addHeader();
				SOAPElement element = header.addChildElement("Security", "wsse",
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
				SOAPBody body = envelope.getBody();
				body.addAttribute(
						new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd",
								"Id", "wsu"),
						"bodyId");

				XmlObject xo = XmlObject.Factory.parse(envelope);
				String bodytxt = xo.xmlText();
				System.out.println(bodytxt);

				String signature = KalkanUtil.getXmlSignatureWithKeys(bodytxt,
						"G:\\tmp\\ivan\\GOSTKNCA_806ab2677ac5a3d78f461ee459a9fb1e0f4ff4b1.p12", "12qazZAQ");
						//"F:\\distr\\crypto\\TEST\\keys\\erik.p12", "123456");
				Element signNode = KalkanUtil.createXmlDocumentFromString(signature, "UTF-8").getDocumentElement();
				SOAPElement se = SOAPFactory.newInstance().createElement(signNode);
				element.addChildElement(se);
				System.out.println("Added Signature");

				xo = XmlObject.Factory.parse(envelope);
				bodytxt = xo.xmlText();
				//System.out.println(bodytxt);

				FileOutputStream fos = new FileOutputStream("F:\\tmp\\ekyzmet\\transport.signed.xml");
				fos.write(bodytxt.getBytes("UTF-8"));
				fos.close();				 System.exit(0); 
				// ==========================================================================

				// ==========================================================================
				// Проверка подписанного ХМЛ
				// ==========================================================================
				FileInputStream fis1 = new FileInputStream("F:\\tmp\\ekyzmet\\ответ_рн_wss.signed.xml");
				byte[] b1 = new byte[fis1.available()];
				fis1.read(b1);
				fis1.close();
				String xmlStr1 = new String(b1, "UTF-8");

				CheckSignResult res1 = checkXML(xmlStr1);
				System.out.println("SIGNER TYPE: " + res1.getSignerType());
				System.out.println("ERROR: " + res1.getCertError());
				System.out.println("DN: " + res1.getSignerDN());
				System.out.println("isCertNew: " + res1.isCertNew());
				System.out.println("isCertOK: " + res1.isCertOK());
				System.out.println("isDigiSignOK: " + res1.isDigiSignOK());

				System.exit(0);
				// ==========================================================================

				
				 * fis = new FileInputStream("D:\\tmp\\gbdul\\body.xml"); b =
				 * new byte[fis.available()]; fis.read(b); xmlStr = new
				 * String(b, "UTF-8"); String bb = checkXML(xmlStr);
				 * System.out.println("result : " + bb);
				 * 
				 * CertificateFactory cf =
				 * CertificateFactory.getInstance("X.509",
				 * KalkanProvider.PROVIDER_NAME); //X509Certificate cert =
				 * (X509Certificate) cf.generateCertificate(new
				 * FileInputStream("D:\\tmp\\gbdul\\krs_ul.cer"));
				 * 
				 * String cer =
				 * "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";
				 * ByteArrayInputStream bais = new
				 * ByteArrayInputStream(Base64.decode(cer.getBytes())); fos =
				 * new FileOutputStream("kalimova.cer");
				 * 
				 * byte[] buf = new byte[1024]; int n = 0; while
				 * ((n=bais.read(buf, 0, buf.length)) > -1) { fos.write(buf, 0,
				 * n); } fos.close(); X509Certificate cert = (X509Certificate)
				 * cf.generateCertificate(new
				 * ByteArrayInputStream(Base64.decode(cer.getBytes())));
				 * bais.close();
				 * 
				 * 
				 * 
				 * int res = verifyCertificate("KALKAN", cert,
				 * ROOT_CERTS_FOLDER, OCSP_URL, null, null); System.out.println(
				 * "Результат проверки сертификата: " + res);
				 
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.exit(0);
			X509Certificate cert = loadCertificate(
					"D:\\erik\\KEYS\\tamur_systems\\GOSTKZ_24c9a579b7c834f07d465183e7c40ac2bdb02293.p12",
					"123456".toCharArray());
			OutputStream ss = new FileOutputStream(
					"D:\\erik\\KEYS\\tamur_systems\\GOSTKZ_24c9a579b7c834f07d465183e7c40ac2bdb02293.cer");
			ss.write(cert.getEncoded());
			ss.close();
			System.exit(0);

			
			 * String xml = "<html><body>ddd</body></html>"; Document doc =
			 * createXmlDocumentFromString(xml, "UTF-8");
			 * 
			 * String xml2 = XmlUtil.createStringFromXmlDocument(doc);
			 * OutputStream os = new FileOutputStream("d:\\tmp\\xml2.xml");
			 * os.write(xml2.getBytes()); os.close();
			 * 
			 * signXml(doc, "", "D:\\tmp\\gost.p12", "123456");
			 
			Document doc = XmlUtil.createXmlDocumentFromFile("D:\\PUBLIC\\primer2.xml");
			String signedxml = XmlUtil.createStringFromXmlDocument(doc);
			OutputStream os = new FileOutputStream("d:\\tmp\\signed.xml");
			// os.write(signedxml.getBytes());
			// os.close();

			CheckSignResult ok = checkXML(signedxml);
			System.out.println(ok);

			System.exit(0);
			
			 * InputStream is = new FileInputStream("d:\\tmp\\requestSign.xml");
			 * byte[] b = new byte[is.available()]; is.read(b); is.close();
			 
			InputStream is = new FileInputStream("d:\\tmp\\requestWithSignature1.xml");
			SOAPMessage message = MessageFactory.newInstance().createMessage(null, is);
			// byte[] b = new byte[is.available()];
			// is.read(b);
			is.close();
			// String str = new String(b, "UTF-8");

			SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
			SOAPBody body = envelope.getBody();

			Iterator it = body.getChildElements();

			// Document doc = createXmlDocumentFromString(str, "UTF-8");

			Element data = null;// (Element)doc.getFirstChild();
			Element head = null;
			Node nextNode = null;
			while (it.hasNext()) {
				Object o = it.next();
				if (o instanceof SOAPElement) {
					data = (SOAPElement) o;
					System.out.println("Found request = " + data.getNodeName());
					NodeList it2 = data.getChildNodes();
					for (int i = 0; i < it2.getLength(); i++) {
						Object o2 = it2.item(i);
						if (o2 instanceof Element) {
							System.out.println("Found head = " + ((Element) o2).getNodeName());
							head = (Element) o2;
							nextNode = head.getNextSibling();
							/// data.removeChild(head);
							break;
						}
					}
					break;
				}
			}

			if (head != null) {
				Element s = getElementByName(head, "digiSign");
				String cont = s.getNodeValue();
				byte[] b = kz.gov.pki.kalkan.util.encoders.Base64.decode(cont);
				String str = new String(b, "cp1251");

				os = new FileOutputStream("d:\\tmp\\requestSign.xml");
				os.write(b);
				os.close();

				System.out.println(str);
				// ok = "ERROR";

				
				 * //s.setTextContent(""); head.removeChild(s);
				 * 
				 * 
				 * XmlObject xo = XmlObject.Factory.parse(body); String bodytxt
				 * = xo.xmlText();
				 * 
				 * int k = bodytxt.indexOf("</" + body.getNodeName()); bodytxt =
				 * bodytxt.substring(0, k) + str + bodytxt.substring(k);
				 * 
				 * System.out.println(
				 * "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
				 * ); System.out.println(
				 * "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
				 * ); System.out.println(
				 * "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
				 * );
				 * 
				 * System.out.println(bodytxt);
				 * 
				 * ok = KalkanUtil.checkXML(bodytxt);
				 * System.out.println("Signature:"+ok);
				 
				doc = createXmlDocumentFromString(str, "UTF-8");
				// Node bodyNode =
				// doc.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/",
				// "Body").item(0);
				// NodeList list = bodyNode.getChildNodes();
				// for (int i=0; i<list.getLength(); i++) {
				Node n = doc.getFirstChild();// list.item(i);
				if (n instanceof Element) {
					Element sigelement = (Element) n.getLastChild();
					XmlObject xo = XmlObject.Factory.parse(n);
					String txt = xo.xmlText();
					ok = KalkanUtil.checkXML(txt);
					System.out.println("Signature:" + ok);
				}
				// }
			} else {
				System.out.println("NO Head element");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/
	public static String getXmlSignatureWithKeys(String xmlString, String p12FileName, String pd)
			throws XMLSecurityException, TransformerConfigurationException, IOException, TransformerException,
			SAXException, ParserConfigurationException, UnrecoverableKeyException, KeyStoreException,
			NoSuchProviderException, NoSuchAlgorithmException, CertificateException {
		return getXmlSignatureWithKeys(xmlString, "#bodyId", p12FileName, pd);
	}

	public static String getXmlSignatureWithKeys(String xmlString, String ref, String p12FileName, String pd)
			throws XMLSecurityException, TransformerConfigurationException, IOException, TransformerException,
			SAXException, ParserConfigurationException, UnrecoverableKeyException, KeyStoreException,
			NoSuchProviderException, NoSuchAlgorithmException, CertificateException {
		String signedXml = signXml(xmlString, ref, p12FileName, pd);
		int a = signedXml.lastIndexOf("<ds:Signature ");
		int b = signedXml.indexOf("</ds:Signature>", a);
		return signedXml.substring(a, b + 15);
	}

	public static String signXmlWithP12Bytes(String xmlString, String ref, byte[] p12Bytes, String pd)
			throws XMLSecurityException, TransformerConfigurationException, IOException, TransformerException,
			SAXException, ParserConfigurationException, UnrecoverableKeyException, KeyStoreException,
			NoSuchProviderException, NoSuchAlgorithmException, CertificateException {
		PrivateKey privateKey = loadPrivateKey(p12Bytes, pd.toCharArray());
		X509Certificate certificate = loadCertificate(p12Bytes, pd.toCharArray());
		return signXml(xmlString, ref, privateKey, certificate);
	}
	
	
	public static String signPlainData1(Object object, String password, String inputText) throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, InvalidKeyException, SignatureException {
		KeyStore ks = KeyStore.getInstance("PKCS12", KalkanProvider.PROVIDER_NAME);
		if (object instanceof File) {
			InputStream fis = new FileInputStream((File) object);
			ks.load(fis, password.toCharArray());
			fis.close();
		} else {
			InputStream bais = new ByteArrayInputStream((byte[]) object);
			ks.load(bais, password.toCharArray());
			bais.close();
		}
		@SuppressWarnings("rawtypes")
		Enumeration en = ks.aliases();
		String alias = null;
		while (en.hasMoreElements()) {
			alias = en.nextElement().toString();
		}
		PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());
		Signature signature = Signature.getInstance(privateKey.getAlgorithm(), KalkanProvider.PROVIDER_NAME);
		signature.initSign(privateKey);
		signature.update(inputText.getBytes(ENCODING));
		String signedTextBase64 = new String(Base64.encode(signature.sign()));
		
//		CheckSignResult res = verifyPlainData1(object, password, inputText, signedTextBase64);

		return signedTextBase64;
	}
	
	public static CheckSignResult verifyPlainData1(Object object, String password, String inputText, String signedTextBase64) throws NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, KeyStoreException, NoSuchProviderException {
		KeyStore ks = KeyStore.getInstance("pkcs12", "SunJSSE");
		ks.load(object instanceof File ? new FileInputStream((File) object) : new ByteArrayInputStream((byte[]) object), password.toCharArray());
		Certificate[] cc = ks.getCertificateChain("1");
		X509Certificate certificate = (X509Certificate) cc[0];
		return verifyPlainData(KalkanProvider.PROVIDER_NAME, inputText, signedTextBase64, certificate, false);
	}
	
	public static String signXml(String xmlString, String ref, String p12FileName, String pd)
			throws XMLSecurityException, TransformerConfigurationException, IOException, TransformerException,
			SAXException, ParserConfigurationException, UnrecoverableKeyException, KeyStoreException,
			NoSuchProviderException, NoSuchAlgorithmException, CertificateException {
		PrivateKey privateKey = loadPrivateKey(p12FileName, pd.toCharArray());
		X509Certificate certificate = loadCertificate(p12FileName, pd.toCharArray());

		return signXml(xmlString, ref, privateKey, certificate);
	}

	public static String signXml(String xmlString, String ref, PrivateKey privateKey, X509Certificate certificate)
			throws XMLSecurityException, TransformerConfigurationException, IOException, TransformerException,
			SAXException, ParserConfigurationException, UnrecoverableKeyException, KeyStoreException,
			NoSuchProviderException, NoSuchAlgorithmException, CertificateException {

		String signMethod = null;
		String digestMethod = null;
		String signedXml = null;

		String sigAlgOid = certificate.getSigAlgOID();
		if (sigAlgOid.equals(PKCSObjectIdentifiers.sha1WithRSAEncryption.getId())) {
			signMethod = SIGN_METHOD_RSA;
			digestMethod = DIGEST_METHOD_RSA;
		} else if (sigAlgOid.equals(PKCSObjectIdentifiers.sha256WithRSAEncryption.getId())) {
			signMethod = SIGN_METHOD_RSA_256;
			digestMethod = DIGEST_METHOD_RSA_256;
		} else if (sigAlgOid.equals(gost3411_2015_with_gost3410_2015_512)) {
			signMethod = SIGN_METHOD_GOST_2015;
			digestMethod = DIGEST_METHOD_GOST_2015;
		} else {
			signMethod = SIGN_METHOD_GOST;
			digestMethod = DIGEST_METHOD_GOST;
		}

		Document doc = createXmlDocumentFromString(xmlString, "UTF-8");
		XMLSignature sig = new XMLSignature(doc, "", signMethod);

		if (doc.getFirstChild() != null) {
			doc.getFirstChild().appendChild(sig.getElement());

			Transforms transforms = new Transforms(doc);
			transforms.addTransform(Transforms.TRANSFORM_ENVELOPED_SIGNATURE);
			transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);

			sig.addDocument(ref, transforms, digestMethod);
			sig.addKeyInfo((X509Certificate) certificate);
			sig.sign(privateKey);
			StringWriter os = new StringWriter();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer trans = tf.newTransformer();
			trans.transform(new DOMSource(doc), new StreamResult(os));
			os.flush();
			signedXml = os.toString();
			os.close();
		}
		return signedXml;
	}

	public static CheckSignResult checkXML(Element xml) throws SAXException, ParserConfigurationException, IOException,
			TransformerException, XMLSignatureException, XMLSecurityException, CertificateParsingException {
		return checkXML(xml, ROOT_CERTS_FOLDER, OCSP_URL, null, null, false);
	}

	public static CheckSignResult checkXML(Element s, String rootCertPath, String ocspUrl, String proxyHost,
			String proxyPort, boolean auth) throws SAXException, ParserConfigurationException, IOException, TransformerException,
					XMLSignatureException, XMLSecurityException, CertificateParsingException {
		CheckSignResult res = new CheckSignResult();
		
		NodeList sigelement = s.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
		
		if (sigelement.getLength() == 0) {
			// нет подписи
			res.setCertError(CheckSignResult.NO_ECP_FOUND);
		} else {
			XMLSignature signature = new XMLSignature((Element) sigelement.item(0), "");
			KeyInfo ki = signature.getKeyInfo();
			X509Certificate certKey = ki.getX509Certificate();

			boolean result = false;
			if (certKey != null) {
				// проверка подписи
                xmlToCheck.set(s);
                NullURIResolverSpi.setXmlToCheck(s);
                result = signature.checkSignatureValue(certKey);
                NullURIResolverSpi.setXmlToCheck(null);
                xmlToCheck.remove();
				if (result) {
					// подпись верна
					res.setDigiSignOK(true);

					// теперь проверяем сертификат
					res = checkCertificate(res, certKey, auth, rootCertPath, ocspUrl, proxyHost, proxyPort, null, null);
				} else {
					// подпись нарушена
					res.setCertError(CheckSignResult.ECP_DAMAGED);
				}
			} else {
				// подпись не содержит сертификата подписавшего лица?
				res.setCertError(CheckSignResult.NO_CERT_FOUND);
			}
		}
		return res;
	}
	
	public static CheckSignResult checkXML(Element s, String shepCertPath)
			throws SAXException, ParserConfigurationException, IOException, TransformerException, XMLSignatureException,
			XMLSecurityException, CertificateParsingException {
		CheckSignResult res = new CheckSignResult();

		NodeList sigelement = s.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
		if (sigelement.getLength() == 0) {
			// нет подписи
			res.setCertError(CheckSignResult.NO_ECP_FOUND);
		} else {
			if (Funcs.isValid(shepCertPath)) {
				File f = Funcs.getCanonicalFile(shepCertPath);
				if (f != null && f.exists()) {
					if (f.isDirectory()) {
						File[] fs = f.listFiles();
						if (fs.length > 0) {
							for (File file : fs) {
								if (!file.isDirectory()) {
									res = checkXML(s, file);
									if (res.isDigiSignOK())
										return res;
								}
							}
						} else {
							res.setCertError(CheckSignResult.NO_CERT_FOUND);
						}
					} else {
						return checkXML(s, f);
					}
				}
			} else {
				// подпись не содержит сертификата подписавшего лица?
				res.setCertError(CheckSignResult.NO_CERT_FOUND);
			}
		}
		return res;
	}

	public static CheckSignResult checkXML(Element s, File certFile)
			throws SAXException, ParserConfigurationException, IOException, TransformerException, XMLSignatureException,
			XMLSecurityException, CertificateParsingException {
		
        System.out.println("certFile = " + certFile.getAbsolutePath());

		CheckSignResult res = new CheckSignResult();

		NodeList sigelement = s.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
		if (sigelement.getLength() == 0) {
			// нет подписи
			res.setCertError(CheckSignResult.NO_ECP_FOUND);
		} else {
			XMLSignature signature = new XMLSignature((Element) sigelement.item(0), "");
			
			X509Certificate certKey = getCertificate(certFile);

			boolean result = false;
			if (certKey != null) {
				try {
					// проверка подписи
	                NullURIResolverSpi.setXmlToCheck(s);
	                result = signature.checkSignatureValue(certKey);
	                NullURIResolverSpi.setXmlToCheck(null);
					if (result) {
						// подпись верна
						res.setDigiSignOK(true);
	
						// теперь проверяем сертификат
						res = checkCertificate(res, certKey, false, ROOT_CERTS_FOLDER, OCSP_URL, null, null, null, null);
					} else {
						// подпись нарушена
						res.setCertError(CheckSignResult.ECP_DAMAGED);
					}
				} catch (XMLSignatureException e) {
					// подпись нарушена (подписано другим ключом)
					res.setCertError(CheckSignResult.ECP_DAMAGED);
				}
			} else {
				// подпись не содержит сертификата подписавшего лица?
				res.setCertError(CheckSignResult.NO_CERT_FOUND);
			}
		}
		return res;
	}

	public static CheckSignResult checkCertificate(String p12FileName, String pd, boolean auth) {
		return checkCertificate(p12FileName, pd, auth, null);
	}
	
	public static CheckSignResult checkCertificate(byte[] p12Bytes, String password, boolean auth) {
		return checkCertificate(p12Bytes, password, auth, null);
	}
	 
	public static CheckSignResult checkCertificate(byte[] p12Bytes, String password, boolean auth, Date currentDate) {
		CheckSignResult res = new CheckSignResult();
		res.setDigiSignOK(true);
		X509Certificate cert;
		try {
			cert = loadCertificate(p12Bytes, password.toCharArray());
			res = checkCertificate(res, cert, auth, ROOT_CERTS_FOLDER, OCSP_URL, null, null, currentDate, null);
		} catch (IOException e) {
			if ("stream does not represent a PKCS12 key store".equals(e.getMessage()))
				res.setCertError(CheckSignResult.WRONG_FILE_FORMAT);
			else if ("PKCS12 key store mac invalid - wrong password or corrupted file.".equals(e.getMessage()))
				res.setCertError(CheckSignResult.WRONG_PASSWORD);
			else {
				e.printStackTrace();
				res.setCertError(CheckSignResult.NO_CERT_FOUND);
			}
		} catch (Exception e) {
			res.setCertError(CheckSignResult.NO_CERT_FOUND);
			e.printStackTrace();
		}
		return res;
	}

	public static CheckSignResult checkCertificate(String p12FileName, String pd, boolean auth, Date currentDate) {
		CheckSignResult res = new CheckSignResult();
		res.setDigiSignOK(true);
		X509Certificate cert;
		try {
			cert = loadCertificate(p12FileName, pd.toCharArray());
			res = checkCertificate(res, cert, auth, ROOT_CERTS_FOLDER, OCSP_URL, null, null, currentDate, null);
		} catch (IOException e) {
			if ("stream does not represent a PKCS12 key store".equals(e.getMessage()))
				res.setCertError(CheckSignResult.WRONG_FILE_FORMAT);
			else if ("PKCS12 key store mac invalid - wrong password or corrupted file.".equals(e.getMessage()))
				res.setCertError(CheckSignResult.WRONG_PASSWORD);
			else {
				e.printStackTrace();
				res.setCertError(CheckSignResult.NO_CERT_FOUND);
			}
		} catch (Exception e) {
			res.setCertError(CheckSignResult.NO_CERT_FOUND);
			e.printStackTrace();
		}
		return res;
	}

	public static CheckSignResult checkCertificate(byte[] certBytes, boolean auth) {
		return checkCertificate(certBytes, auth, null);
	}

	public static CheckSignResult checkCertificate(byte[] certBytes, boolean auth, Date currentDate) {
		return checkCertificate(certBytes, auth, ROOT_CERTS_FOLDER, OCSP_URL, null, null, currentDate);
	}

	public static CheckSignResult checkCertificate(byte[] certBytes, boolean auth, String rootCertPath, String ocspUrl,
			String proxyHost, String proxyPort, Date currentDate) {

		CheckSignResult res = new CheckSignResult();
		X509Certificate cert = getCertificate(certBytes);
		return checkCertificate(res, cert, auth, rootCertPath, ocspUrl, proxyHost, proxyPort, currentDate, null);
	}

	public static CheckSignResult checkCertificate(byte[] certBytes, boolean auth, String rootCertPath, String ocspUrl,
			String proxyHost, String proxyPort, Date currentDate, String doNotCheck) {

		CheckSignResult res = new CheckSignResult();
		X509Certificate cert = getCertificate(certBytes);
		return checkCertificate(res, cert, auth, rootCertPath, ocspUrl, proxyHost, proxyPort, currentDate, doNotCheck);
	}

	public static CheckSignResult checkCertificate(CheckSignResult res, X509Certificate cert, boolean auth,
			String rootCertPath, String ocspUrl, String proxyHost, String proxyPort, Date currentDate, String doNotCheck) {
		loadCertificateInfo(cert, res);

		
		System.out.println("after: " + cert.getNotBefore());
		System.out.println("before: " + cert.getNotAfter());
		System.out.println("subject: " + cert.getSubjectDN());
		System.out.println("issuer: " + cert.getIssuerDN());

		
		// проверяем тип владельца сертификата (новый НУЦ)
		try {
			List<String> usage = cert.getExtendedKeyUsage();
			if (usage != null && !usage.isEmpty()) {
				for (Iterator<String> i = usage.iterator(); i.hasNext();) {
					String oid = i.next();

					if (OWNER_FL.equals(oid))
						res.setSignerType(CheckSignResult.FL_SIMPLE);
					else if (OWNER_UL_HEAD.equals(oid))
						res.setSignerType(CheckSignResult.UL_HEAD);
					else if (OWNER_UL_DEPUTY.equals(oid))
						res.setSignerType(CheckSignResult.UL_DEPUTY);
					else if (OWNER_UL_FINANCE.equals(oid))
						res.setSignerType(CheckSignResult.UL_FINANCE);
					else if (OWNER_UL_HR.equals(oid))
						res.setSignerType(CheckSignResult.UL_HR);
					else if (OWNER_UL_EMPLOYEE.equals(oid))
						res.setSignerType(CheckSignResult.UL_EMPLOYEE);
				}
			}
		} catch (CertificateParsingException e) {
			res.setCertError(CheckSignResult.CERT_OTHER_ERROR);
		}
		// Если тип владельца есть, то ставим флажок, что это новый НУЦ
		if (res.getSignerType() > -1)
			res.setCertNew(true);

		String issuer = cert.getIssuerDN().toString();
		if (issuer.contains(ISSUER_UCGO_KZ) || issuer.contains(ISSUER_UCGO_RU))
			res.setCertUCGO(true);

		try {
			List<String> usages = cert.getExtendedKeyUsage();
			
			if (GOST_ALGORITHM_ECGOST3410_2015_512.equals(cert.getSigAlgName())) {
				res.setForAuth(true);
				res.setForSign(true);
			} else {
				boolean[] use = cert.getKeyUsage();

				res.setForAuth((usages != null && usages.contains(PURPOSE_AUTH)) || use[2]);
				res.setForSign((usages != null && usages.contains(PURPOSE_SIGN)) || use[1]);
			}

			int certRes = verifyCertificate(KalkanProvider.PROVIDER_NAME, cert, auth, rootCertPath, ocspUrl, proxyHost,
					proxyPort, currentDate, doNotCheck);
			System.out.println("Результат проверки сертификата: " + certRes);

			res.setCertError(certRes);
			if (certRes == CheckSignResult.ECP_AND_CERT_OK)
				res.setCertOK(true);

		} catch (Exception e) {
			e.printStackTrace();
			res.setCertError(CheckSignResult.CERT_OTHER_ERROR);
		}
		return res;
	}

	private static void loadCertificateInfo(X509Certificate certKey, CheckSignResult res) {
		String dn = certKey.getSubjectDN().getName();

		res.setSignerDN(dn);
		int beg = dn.indexOf("SERIALNUMBER=IIN");
		if (beg > -1) {
			int end = dn.indexOf(",", beg + 16);
			if (end == -1)
				end = dn.length();
			String iin = dn.substring(beg + 16, end);
			res.setSignerIIN(iin);
		}
		beg = dn.indexOf("OU=BIN");
		if (beg > -1) {
			int end = dn.indexOf(",", beg + 6);
			if (end == -1)
				end = dn.length();
			String bin = dn.substring(beg + 6, end);
			res.setSignerBIN(bin);
		}
	}

	public static X509Certificate getCertificate(String xml) throws SAXException, ParserConfigurationException, IOException, XMLSignatureException, XMLSecurityException {
		Document doc = createXmlDocumentFromString(xml, "UTF-8");

		NodeList sigelement = doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
		
		X509Certificate certKey = null;
		
		if (sigelement.getLength() > 0) {
			XMLSignature signature = new XMLSignature((Element) sigelement.item(0), "");
			KeyInfo ki = signature.getKeyInfo();
			certKey = ki.getX509Certificate();
		}
		return certKey;
	}
	
	public static CheckSignResult checkXML(String xml) throws SAXException, ParserConfigurationException, IOException,
			TransformerException, XMLSignatureException, XMLSecurityException, CertificateParsingException {
		return checkXML(xml, ROOT_CERTS_FOLDER, OCSP_URL, null, null);
	}

	public static CheckSignResult checkXML(String xml, boolean auth) throws SAXException, ParserConfigurationException, IOException,
			TransformerException, XMLSignatureException, XMLSecurityException, CertificateParsingException {
		Document doc = createXmlDocumentFromString(xml, "UTF-8");
		return checkXML(doc.getDocumentElement(), ROOT_CERTS_FOLDER, OCSP_URL, null, null, auth);
	}

	public static CheckSignResult checkXML(String xml, String rootCertPath, String ocspUrl, String proxyHost,
			String proxyPort) throws SAXException, ParserConfigurationException, IOException, TransformerException,
					XMLSignatureException, XMLSecurityException, CertificateParsingException {
		Document doc = createXmlDocumentFromString(xml, "UTF-8");

		return checkXML(doc.getDocumentElement(), rootCertPath, ocspUrl, proxyHost, proxyPort, false);
	}

	public static void signXml(Document doc, String ref, String p12FileName, String pd)
			throws XMLSecurityException, TransformerConfigurationException, IOException, TransformerException,
			SAXException, ParserConfigurationException, UnrecoverableKeyException, KeyStoreException,
			NoSuchProviderException, NoSuchAlgorithmException, CertificateException {
		PrivateKey privateKey = loadPrivateKey(p12FileName, pd.toCharArray());
		X509Certificate certificate = loadCertificate(p12FileName, pd.toCharArray());

		String signMethod = null;
		String digestMethod = null;

		String sigAlgOid = certificate.getSigAlgOID();
		if (sigAlgOid.equals(PKCSObjectIdentifiers.sha1WithRSAEncryption.getId())) {
			signMethod = SIGN_METHOD_RSA;
			digestMethod = DIGEST_METHOD_RSA;
		} else if (sigAlgOid.equals(PKCSObjectIdentifiers.sha256WithRSAEncryption.getId())) {
			signMethod = SIGN_METHOD_RSA_256;
			digestMethod = DIGEST_METHOD_RSA_256;
		} else if (sigAlgOid.equals(gost3411_2015_with_gost3410_2015_512)) {
			signMethod = SIGN_METHOD_GOST_2015;
			digestMethod = DIGEST_METHOD_GOST_2015;
		} else {
			signMethod = SIGN_METHOD_GOST;
			digestMethod = DIGEST_METHOD_GOST;
		}

		XMLSignature sig = new XMLSignature(doc, "", signMethod);

		if (doc.getFirstChild() != null) {
			doc.getFirstChild().appendChild(sig.getElement());
			Transforms transforms = new Transforms(doc);
			transforms.addTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature");
			transforms.addTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
			sig.addDocument(ref, transforms, digestMethod);
			sig.addKeyInfo((X509Certificate) certificate);
			sig.sign(privateKey);
		}
	}

	/**
	 * Проверка сертификата
	 * 
	 * @param providerName
	 *            - наименование крипто-провайдера
	 * @param cert
	 *            - проверяемый сертификат
	 * @param isForAuth
	 *            - проверяем аутентификацию или эцп? true - аутентификацию,
	 *            false - эцп
	 * @param rootStorePath
	 *            - папка с сертификатами доверенных центров сертификации
	 * @param ocspURL
	 *            - адрес онлайн-сервиса проверки сертификатов OCSP
	 * @param phost
	 *            - адрес прокси-сервера
	 * @param pport
	 *            - порт прокси-сервера
	 * @return
	 * @throws KeyStoreException
	 * @throws NoSuchProviderException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */

	public static int verifyCertificate(String providerName, X509Certificate cert, boolean isForAuth,
			String rootStorePath, String ocspURL, String phost, String pport, Date currentDate)
					throws KeyStoreException, NoSuchProviderException, FileNotFoundException, IOException,
					NoSuchAlgorithmException, CertificateException, InvalidKeyException, SignatureException {
		return verifyCertificate(providerName, cert, isForAuth, rootStorePath, ocspURL, phost, pport, currentDate, null);
	}
	
	public static int verifyCertificate(String providerName, X509Certificate cert, boolean isForAuth,
			String rootStorePath, String ocspURL, String phost, String pport, Date currentDate, String doNotCheck)
					throws KeyStoreException, NoSuchProviderException, FileNotFoundException, IOException,
					NoSuchAlgorithmException, CertificateException, InvalidKeyException, SignatureException {
		// Проверка срока действия сертификата
		try {
			if (currentDate == null)
				cert.checkValidity();
			else
				cert.checkValidity(currentDate);

		} catch (CertificateExpiredException ex) {
			if (needCheckProperty(CheckSignResult.CERT_EXPIRED, doNotCheck))
				return CheckSignResult.CERT_EXPIRED;
		} catch (CertificateNotYetValidException ex) {
			if (needCheckProperty(CheckSignResult.CERT_NOT_YET_VALID, doNotCheck))
				return CheckSignResult.CERT_NOT_YET_VALID;
		}

		// проверка назначения сертификата
		if (needCheckProperty(CheckSignResult.CERT_NOT_FOR_AUTH, doNotCheck)) {
			List<String> usages = cert.getExtendedKeyUsage();
			boolean[] use = cert.getKeyUsage();
			
			boolean isOK = false;
			
			if (GOST_ALGORITHM_ECGOST3410_2015_512.equals(cert.getSigAlgName())) {
				isOK = true;
			} else if ((isForAuth && usages != null && usages.contains(PURPOSE_AUTH)) || (!isForAuth && usages != null && usages.contains(PURPOSE_SIGN))) { 
				isOK = true;
			} else if ((isForAuth && use[2]) || (!isForAuth && use[1])) 
				isOK = true;

			if (!isOK)
				return isForAuth ? CheckSignResult.CERT_NOT_FOR_AUTH : CheckSignResult.CERT_NOT_FOR_SIGN;
		}

		if ("".equals(phost))
			phost = null;
		if ("".equals(pport))
			pport = null;

//		String issuer = cert.getIssuerDN().toString();
//		if (issuer.contains(ISSUER_UCGO_KZ) || issuer.contains(ISSUER_UCGO_RU)) {
//			return CheckSignResult.ECP_AND_CERT_OK;
//		} else {
			// проверка не отозван ли сертификат
			int r = checkNotRevoked(providerName, cert, rootStorePath, ocspURL, phost, pport, doNotCheck);
	
			if (r == CheckSignResult.CERT_OTHER_ERROR && !needCheckProperty(CheckSignResult.CERT_OTHER_ERROR, doNotCheck))
				return CheckSignResult.ECP_AND_CERT_OK;
			else if (r == CheckSignResult.CERT_OCSP_ERROR && !needCheckProperty(CheckSignResult.CERT_OCSP_ERROR, doNotCheck))
				return CheckSignResult.ECP_AND_CERT_OK;
			else if (r == CheckSignResult.CERT_CRL_NO_ACCESS && !needCheckProperty(CheckSignResult.CERT_CRL_NO_ACCESS, doNotCheck))
				return CheckSignResult.ECP_AND_CERT_OK;
	
			return r;
//		}
	}

	public static int checkNotRevoked(String providerName, X509Certificate cert, String rootStorePath, String ocspURL,
			String phost, String pport, String doNotCheck) {
		if (cert == null) {
			return CheckSignResult.NO_CERT_FOUND;
		}
		int res = CheckSignResult.ECP_AND_CERT_OK; // OK

		// проверка через онлайн службу
		res = checkOCSP(providerName, cert, rootStorePath, ocspURL, phost, pport, doNotCheck);

		// если при онлайн-проверке вышла ошибка, то проверяем через список
		// отозванных сертификатов
		if (res == CheckSignResult.CERT_OTHER_ERROR || res == CheckSignResult.CERT_OCSP_ERROR) {
			if (needCheckProperty(CheckSignResult.CERT_REVOKED, doNotCheck)) {
				List<X509CRL> crls = loadCRLs(providerName, cert, phost, pport);
				if (!crls.isEmpty()) {
					for (Iterator<X509CRL> iter = crls.iterator(); iter.hasNext();) {
						X509CRL crl = iter.next();
						if (crl.isRevoked(cert)) {
							return CheckSignResult.CERT_REVOKED; // Сертификат
																	// отозван
						}
					}
					res = CheckSignResult.ECP_AND_CERT_OK;
				} else {
					res = CheckSignResult.CERT_CRL_NO_ACCESS; // Не доступен
																// список
																// отозванных
																// сертификатов
				}
			}
		}
		return res;
	}

	private static List<X509CRL> loadCRLs(String providerName, X509Certificate cert, String phost, String pport) {
		// loading CRL
		if (phost != null && pport != null) {
			Properties props = System.getProperties();
			props.put("http.proxyHost", phost);
			props.put("http.proxyPort", pport);
		}
		X509CRLParser crlParser = new X509CRLParser();
		InputStream crlStream = null;
		List<X509CRL> crls = new LinkedList<X509CRL>();
		// получаем список адресов из самого сертификата
		List<String> urls = getCRLs(cert);
		if (urls.size() > 0) {
			System.out.println("CRL urls size = " + urls.size());

			for (Iterator<String> iter = urls.iterator(); iter.hasNext();) {
				String urlName = iter.next();
				System.out.println("Next url in CRL list: " + urlName);
				try {
					try {
						URLConnection c = new URL(urlName).openConnection();
						// set the connection timeout to 3 seconds and the read
						// timeout to 5 seconds
						c.setConnectTimeout(2000);
						c.setReadTimeout(4000);

						crlStream = c.getInputStream();
					} catch (Exception e) {
						crlStream = new FileInputStream(urlName);
					}
					crlParser.engineInit(crlStream);

					Collection something = crlParser.engineReadAll();
					System.out.println("CRLParser returnes elements: " + something.size());
					for (Iterator iter2 = something.iterator(); iter2.hasNext();) {
						crls.add((X509CRL) iter2.next());
					}
				} catch (Exception e) {
					System.out.println("Couldn't get CRL. Exception message:\n" + e.getLocalizedMessage());
				} finally {
					if (crlStream != null) {
						try {
							crlStream.close();
						} catch (Exception e) {
						}
					}
				}
			}
		}
		return crls;
	}

	private static List<String> getCRLs(java.security.cert.X509Extension cert) {
		byte[] bytes = cert.getExtensionValue(CRL_EXTENSION);
		List<String> httpCRLS = new LinkedList<String>();
		if (bytes == null || bytes.length == 0) {
			// just return empty list
			return httpCRLS;
		} else {
			String s = null;
			try {
				s = new String(bytes, "UTF8");
			} catch (UnsupportedEncodingException uee) {
			}
			int pos = 0;
			String crlEnding = ".crl";
			while (pos >= 0) {
				int y;
				int x = s.indexOf("http", pos);
				if (x >= 0) {
					y = s.indexOf(crlEnding, x);
					httpCRLS.add(s.substring(x, y + crlEnding.length()));
					y = s.indexOf("http", y + 1);
					pos = y;
				} else {
					pos = -1;
				}
			}
		}
		return httpCRLS;
	}

	public static void initOCSPMap(String path) {
		try {
			Element ocspMapEl = null; 
			if (path != null)
				ocspMapEl = XmlUtil.createXmlDocumentFromFile(path).getDocumentElement();
			else if (ROOT_CERTS_FOLDER != null)
				ocspMapEl = XmlUtil.createXmlDocumentFromFile(ROOT_CERTS_FOLDER + "/ocsp.xml").getDocumentElement();
			else {
				InputStream is = KalkanUtil.class.getResourceAsStream("/kz/tamur/util/crypto/rootcerts/ocsp.xml");
				if (is != null)
					ocspMapEl = XmlUtil.getDocument(is).getDocumentElement();
			}
			
			if (ocspMapEl != null) {
				
				NodeList list = ocspMapEl.getElementsByTagName("defaultOcspAddress");
				if (list != null && list.getLength() > 0)
					OCSP_URL = list.item(0).getTextContent();
				
				list = ocspMapEl.getElementsByTagName("ocspParam");
				if (list != null && list.getLength() > 0) {
					for (int i = 0; i < list.getLength(); i++) {
						Element param = (Element) list.item(i);
						NodeList tmp = param.getElementsByTagName("rootSerial");
						if (tmp != null && tmp.getLength() > 0) {
							String serial = tmp.item(0).getTextContent();
							tmp = param.getElementsByTagName("ocspAddress");
							if (tmp != null && tmp.getLength() > 0) {
								String addr = tmp.item(0).getTextContent();
								OCSP_URL_MAP.put(serial, addr);
							}
						}
					}
				}
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public static int checkOCSP(String providerName, X509Certificate cert, String rootStorePath, String ocspURL,
			String phost, String pport, String doNotCheck) {
		try {
			if (ocspURL != null) {
				X509Certificate rootCert = getIssuerCertificate(providerName, cert, rootStorePath);

				if (rootCert != null) {
					// если да, то делаем запрос
					if (needCheckProperty(CheckSignResult.CERT_REVOKED, doNotCheck)) {
						if (rootCert.getSubjectDN().toString().contains(ISSUER_UCGO_KZ) ||
								rootCert.getSubjectDN().toString().contains(ISSUER_UCGO_RU)) {
							return CheckSignResult.ECP_AND_CERT_OK;
						} else {
							String serial = rootCert.getSerialNumber().toString(16);
							
							URL url = new URL(OCSP_URL_MAP.get(serial) != null ? OCSP_URL_MAP.get(serial) : ocspURL);
							HttpURLConnection con = null;
							OutputStream os = null;
							try {
								byte[] ocspReq = getOcspPackage(cert.getSerialNumber(), rootCert,
										CertificateID.HASH_GOST34311GT);
	
								if (phost != null && pport != null) {
									Proxy proxy = new Proxy(Type.HTTP,
											new InetSocketAddress(phost, Integer.parseInt(pport)));
									con = (HttpURLConnection) url.openConnection(proxy);
								} else {
									con = (HttpURLConnection) url.openConnection();
								}
	
								con = (HttpURLConnection) url.openConnection();
								con.setDoOutput(true);
								con.setRequestMethod("POST");
								con.setRequestProperty("Content-Type", "application/ocsp-request");
								os = con.getOutputStream();
								os.write(ocspReq);
								Funcs.write(ocspReq, new File("D:\\tmp\\ul\\2022-12-13-Login-ECP\\" + serial + "\\ocsp.req"));
								os.close();
								
								System.out.println("OCSP URL: " + url.toString());
	
								int res = makeOcspResponse(con);
								// возвращаем результат проверки
								return res;
							} catch (Exception e) {
								e.printStackTrace();
								return CheckSignResult.CERT_OTHER_ERROR;
							} finally {
								if (os != null)
									try {
										os.close();
									} catch (Throwable e) {
									}
								if (con != null)
									try {
										con.disconnect();
									} catch (Throwable e) {
									}
							}
						}
					}
				} else {
					if (needCheckProperty(CheckSignResult.CERT_SIGN_ERROR, doNotCheck))
						return CheckSignResult.CERT_SIGN_ERROR;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CheckSignResult.CERT_OTHER_ERROR;
	}

	private static int makeOcspResponse(HttpURLConnection con) {
		try {
			InputStream in = con.getInputStream();

			byte[] b = Funcs.readStream(in, 10000000);
			Funcs.write(b, new File("D:\\tmp\\ul\\2022-12-13-Login-ECP\\ocsp.resp"));

			OCSPResp response = new OCSPResp(b);
			in.close();

			if (response.getStatus() != 0) {
				System.out.println("OCSP Response is FATAL");
				return CheckSignResult.CERT_OCSP_ERROR;
			}
			BasicOCSPResp brep = (BasicOCSPResp) response.getResponseObject();
			X509Certificate ocspcert = brep.getCerts(KalkanProvider.PROVIDER_NAME)[0];
			System.out.println("OCSP Response sigAlg: " + brep.getSignatureAlgName());
			System.out.println(
					"OCSP Response verify: " + brep.verify(ocspcert.getPublicKey(), KalkanProvider.PROVIDER_NAME));

			SingleResp[] singleResps = brep.getResponses();
			SingleResp singleResp = singleResps[0];
			Object status = singleResp.getCertStatus();

			if (status == null) {
				System.out.println("OCSP Response is GOOD");
				return CheckSignResult.ECP_AND_CERT_OK;
			}
			if (status instanceof RevokedStatus) {
				System.out.println("OCSP Response is REVOKED");
				if (((RevokedStatus) status).hasRevocationReason()) {
					System.out.println("Reason: " + ((RevokedStatus) status).getRevocationReason());
					System.out.println("Date: " + ((RevokedStatus) status).getRevocationTime());
				}
				return CheckSignResult.CERT_REVOKED;
			}
			if (status instanceof UnknownStatus) {
				System.out.println("OCSP Response is UNKNOWN");
				return CheckSignResult.CERT_OCSP_ERROR;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CheckSignResult.CERT_OTHER_ERROR;
	}

	private static byte[] getOcspPackage(BigInteger serialNr, Certificate cacert, String hashAlg) throws Exception {
		OCSPReqGenerator gen = new OCSPReqGenerator();
		CertificateID certId = new CertificateID(hashAlg, (X509Certificate) cacert, serialNr,
				KalkanProvider.PROVIDER_NAME);
		gen.addRequest(certId);
		// gen.setRequestExtensions(generateExtensions());
		OCSPReq req = gen.generate();
		return req.getEncoded();
	}

	public static boolean checkCertificateSign(X509Certificate cert, String providerName, String rootPath)
			throws KeyStoreException, NoSuchProviderException, FileNotFoundException, IOException,
			NoSuchAlgorithmException, CertificateException, InvalidKeyException, SignatureException,
			FileNotFoundException {
		File dir = new File(rootPath);
		if (dir != null && dir.exists()) {
			File[] fs = dir.listFiles();
			CertificateFactory cf = CertificateFactory.getInstance("X.509", providerName);

			for (File f : fs) {
				FileInputStream fis = new FileInputStream(f);
				X509Certificate c = (X509Certificate) cf.generateCertificate(fis);
				fis.close();
				try {
					cert.verify(c.getPublicKey(), providerName);
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public static X509Certificate getIssuerCertificate(String providerName, X509Certificate cert,
			String rootStorePath) {
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509", providerName);
			if (Funcs.isValid(rootStorePath)) {
				File dir = Funcs.getCanonicalFile(rootStorePath);
				if (dir != null && dir.exists() && dir.isDirectory()) {
					File[] fs = dir.listFiles();
					// пробегаем по всем сертификатам доверенных центров
					for (File f : fs) {
						if (!f.getName().endsWith(".xml")) {
							FileInputStream fis = new FileInputStream(f);
							X509Certificate rootCert = (X509Certificate) cf.generateCertificate(fis);
							fis.close();
							boolean verify = true;
							try {
								// проверяем подписан ли сертификат доверенным
								// центром
								cert.verify(rootCert.getPublicKey(), providerName);
							} catch (Exception e) {
								verify = false;
							}
							if (verify) {
								// если да, то возвращаем сертификат центра
								System.out.println("!ISSUER TRUSTED!: " + f.getAbsolutePath());
								return rootCert;
							}
						}
					}
				}
			} else {
				for (String resourceName : ROOT_CERTS) {
					if (!resourceName.endsWith(".xml")) {
						InputStream is = KalkanUtil.class.getResourceAsStream(resourceName);
						X509Certificate rootCert = (X509Certificate) cf.generateCertificate(is);
						is.close();
						boolean verify = true;
						try {
							// проверяем подписан ли сертификат доверенным центром
							cert.verify(rootCert.getPublicKey(), providerName);
						} catch (Exception e) {
							verify = false;
						}
						if (verify) {
							// если да, то возвращаем сертификат центра
							System.out.println("!ISSUER TRUSTED! resource: " + resourceName);
							return rootCert;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static CheckSignResult verifyPlainData(byte[] data, byte[] signBase64, byte[] certBytes) {
		X509Certificate cert = getCertificate(certBytes);
		return verifyPlainData(KalkanProvider.PROVIDER_NAME, data, signBase64, cert);
	}

	public static CheckSignResult verifyPlainData(String providerName, String data, String signBase64,
			X509Certificate cert) {
		return verifyPlainData(providerName, data, signBase64, cert, false);
	}

	public static CheckSignResult verifyPlainData(String providerName, String data, String signBase64,
			X509Certificate cert, boolean auth) {
		try {
			return verifyPlainData(providerName, data.getBytes("UTF-8"), signBase64.getBytes("UTF-8"), cert, auth);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new CheckSignResult();
	}

	public static CheckSignResult verifyPlainData(String providerName, byte[] data, byte[] signBase64,
			X509Certificate cert) {
		return verifyPlainData(providerName, data, signBase64, cert, false);
	}

	public static CheckSignResult verifyPlainData(String providerName, byte[] data, byte[] signBase64,
			X509Certificate cert, boolean auth) {
		return verifyPlainData(providerName, data, signBase64, cert, auth, null);
	}

	public static CheckSignResult verifyPlainData(String providerName, byte[] data, byte[] signBase64,
			X509Certificate cert, boolean auth, Date currentDate) {
		CheckSignResult res = new CheckSignResult();
		boolean b = false;
		try {
			Signature signature = Signature.getInstance(cert.getPublicKey().getAlgorithm(), providerName);
			signature.initVerify(cert.getPublicKey());
			signature.update(data);
			b = signature.verify(kz.gov.pki.kalkan.util.encoders.Base64.decode(signBase64));
		} catch (Exception e) {
			res.setCertError(CheckSignResult.ECP_DAMAGED);
			e.printStackTrace();
		}
		res.setDigiSignOK(b);

		res = checkCertificate(res, cert, auth, ROOT_CERTS_FOLDER, OCSP_URL, null, null, currentDate, null);

		return res;
	}

	public static String signPlainData(String p12FileName, String pd, String inputText)
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException,
			UnrecoverableKeyException, KeyStoreException, CertificateException, IOException {
		byte[] signedData = signPlainData(p12FileName, pd, inputText.getBytes(ENCODING));
		return new String(Base64.encode(signedData));
	}

	/**
	 * подписывает данные в виде массива байтов
	 * 
	 * @param inputData
	 *            массив байтов для подписания
	 * @param privateKey
	 *            закрытый ключ подписываемого
	 * @return подпись в виде массива байтов
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 * @throws IOException
	 * @throws CertificateException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 */
	public static byte[] signPlainData(String p12FileName, String pd, byte[] inputData)
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException,
			UnrecoverableKeyException, KeyStoreException, CertificateException, IOException {
		PrivateKey privateKey = loadPrivateKey(p12FileName, pd.toCharArray());
		Signature signature = Signature.getInstance(privateKey.getAlgorithm(), KalkanProvider.PROVIDER_NAME);
		signature.initSign(privateKey);
		signature.update(inputData);
		return signature.sign();
	}

	public static String createPkcs7(String p12FileName, String pd, String dataToSign, boolean attached)
			throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException,
			IOException, UnrecoverableKeyException, InvalidAlgorithmParameterException, CertStoreException,
			CMSException {

		byte[] signedDataEncoded = createPkcs7(p12FileName, pd, dataToSign.getBytes(ENCODING), attached);
		return new String(kz.gov.pki.kalkan.util.encoders.Base64.encode(signedDataEncoded));
	}
	
	public static byte[] createPkcs7(String p12FileName, String pd, byte[] dataToSign, boolean attached)
			throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException,
			IOException, UnrecoverableKeyException, InvalidAlgorithmParameterException, CertStoreException,
			CMSException {
		return createPkcs7(p12FileName, pd, dataToSign, attached, false);
	}
	public static byte[] createPkcs7(String p12FileName, String pd, byte[] dataToSign, boolean attached,
			boolean addTimeStamp)
			throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException,
			IOException, UnrecoverableKeyException, InvalidAlgorithmParameterException, CertStoreException,
			CMSException {
		
		CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
		
		CMSProcessable content = null;
		try {
			// Проверяем является ли документ уже подписанными CMS
			CMSSignedData signedData = new CMSSignedData(dataToSign);
			content = signedData.getSignedContent();
			generator.addSigners(signedData.getSignerInfos());
			generator.addCertificatesAndCRLs(signedData.getCertificatesAndCRLs("Collection", KalkanProvider.PROVIDER_NAME));
		} catch (CMSException e) {
			// Иначе все по-старому
			System.out.println("Not signed");
			content = new CMSProcessableByteArray(dataToSign);
		}
		
		PrivateKey privateKey = loadPrivateKey(p12FileName, pd.toCharArray());
		X509Certificate certificate = loadCertificate(p12FileName, pd.toCharArray());
		X509Certificate chain[] = new X509Certificate[] { certificate };

		CertStore chainStore = CertStore.getInstance("Collection",
				new CollectionCertStoreParameters(Arrays.asList(chain)), KalkanProvider.PROVIDER_NAME);
		
		if (certificate.getSigAlgOID().equals(PKCSObjectIdentifiers.sha1WithRSAEncryption.getId())) {
			generator.addSigner(privateKey, certificate, CMSSignedDataGenerator.DIGEST_SHA1);
		} else if (certificate.getSigAlgOID().equals(PKCSObjectIdentifiers.sha256WithRSAEncryption.getId())) {
			generator.addSigner(privateKey, certificate, CMSSignedDataGenerator.DIGEST_SHA256);
		} else if (certificate.getSigAlgOID().equals(KNCAObjectIdentifiers.gost34311_95_with_gost34310_2004.getId())) {
			generator.addSigner(privateKey, certificate, CMSSignedDataGenerator.DIGEST_GOST34311_95);
		} else if (certificate.getSigAlgOID()
				.equals(CryptoProObjectIdentifiers.gostR3411_94_with_gostR34310_2004.getId())) {
			generator.addSigner(privateKey, certificate, CMSSignedDataGenerator.DIGEST_GOST3411_GT);
		} else if (certificate.getSigAlgOID()
				.equals(gost3411_2015_with_gost3410_2015_256)) {
			generator.addSigner(privateKey, certificate, DIGEST_GOST3411_2015_256);
		} else if (certificate.getSigAlgOID()
				.equals(gost3411_2015_with_gost3410_2015_512)) {
			generator.addSigner(privateKey, certificate, DIGEST_GOST3411_2015_512);
		} else {
			return null;
		}
		generator.addCertificatesAndCRLs(chainStore);

		CMSSignedData signedData = generator.generate(content, attached, KalkanProvider.PROVIDER_NAME);
		
		if (addTimeStamp)
			signedData = addTimestamp(signedData);
		
		byte[] signedDataEncoded = signedData.getEncoded();

		return signedDataEncoded;
	}
	
	public static CMSSignedData addTimestamp(CMSSignedData signedData) throws IOException {
		DERObjectIdentifier tst_id = new DERObjectIdentifier("1.2.840.113549.1.9.16.2.14");
		
		Collection<?> ss = signedData.getSignerInfos().getSigners();
		Collection<SignerInformation> ss2 = new ArrayList<>();
        Iterator<?> it = ss.iterator();
        
        while (it.hasNext()) {
        	SignerInformation si = (SignerInformation) it.next();
        
	        Attribute tstAttr = si.getUnsignedAttributes() != null ? si.getUnsignedAttributes().get(tst_id) : null;
	        
	        if (tstAttr == null) {
	        	
	        	byte[] signature = si.getSignature();
	        	
		        TimeStampToken tok = getTSPStamp(signature, TSPAlgorithms.GOST34311);
		
		        ASN1InputStream asn1InputStream = new ASN1InputStream(tok.getEncoded());
		        DERObject tstDER = asn1InputStream.readObject();
		        DERSet ds = new DERSet(tstDER);
		
		        Attribute a = new Attribute(new DERObjectIdentifier("1.2.840.113549.1.9.16.2.14"), ds);
		        ASN1EncodableVector dv = new ASN1EncodableVector();
		        dv.add(a);
		        AttributeTable at = new AttributeTable(dv);
		        si = SignerInformation.replaceUnsignedAttributes(si, at);
	        }
	        ss2.add(si);
        }
        SignerInformationStore sis = new SignerInformationStore(ss2);
        signedData = CMSSignedData.replaceSigners(signedData, sis);
        
        return signedData;
    }


	/**
	 * выполняет проверку подписи в формате pkcs7
	 *
	 * @param dataToVerify
	 *            данные для проверки
	 * @param signToVerify
	 *            подпись в формате Base64
	 * @return true - подпись валидна, false - подпись не валидна
	 * @throws CertificateExpiredException
	 * @throws CertificateNotYetValidException
	 * @throws CMSException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws CertStoreException
	 * @throws CertificateParsingException
	 */
	public static CheckSignResult verifyPkcs7(byte[] dataToVerify, String signToVerify, boolean auth)
			throws CertificateExpiredException, CertificateNotYetValidException, CMSException, IOException,
			NoSuchAlgorithmException, NoSuchProviderException, CertStoreException, CertificateParsingException {
		return verifyPkcs7(dataToVerify, signToVerify, auth, null);
	}

	public static CheckSignResult verifyPkcs7(String dataToVerify, String signToVerify, boolean auth)
			throws CertificateExpiredException, CertificateNotYetValidException, CMSException, IOException,
			NoSuchAlgorithmException, NoSuchProviderException, CertStoreException, CertificateParsingException {
		return verifyPkcs7(dataToVerify, signToVerify, auth, null);
	}

	public static CheckSignResult verifyPkcs7(byte[] dataToVerify, String signToVerify, boolean auth, Date currentDate)
			throws CertificateExpiredException, CertificateNotYetValidException, CMSException, IOException,
			NoSuchAlgorithmException, NoSuchProviderException, CertStoreException, CertificateParsingException {

		return verifyPkcs7(dataToVerify, signToVerify, auth, ROOT_CERTS_FOLDER, OCSP_URL, null, null, currentDate, null);
	}

	public static CheckSignResult verifyPkcs7(String dataToVerify, String signToVerify, boolean auth, Date currentDate)
			throws CertificateExpiredException, CertificateNotYetValidException, CMSException, IOException,
			NoSuchAlgorithmException, NoSuchProviderException, CertStoreException, CertificateParsingException {

		return verifyPkcs7(dataToVerify, signToVerify, auth, ROOT_CERTS_FOLDER, OCSP_URL, null, null, currentDate, null);
	}

	public static CheckSignResult verifyPkcs7(byte[] dataToVerify, String signToVerify, boolean auth, Date currentDate, String doNotCheck)
			throws CertificateExpiredException, CertificateNotYetValidException, CMSException, IOException,
			NoSuchAlgorithmException, NoSuchProviderException, CertStoreException, CertificateParsingException {

		return verifyPkcs7(dataToVerify, signToVerify, auth, ROOT_CERTS_FOLDER, OCSP_URL, null, null, currentDate, doNotCheck);
	}

	public static CheckSignResult verifyPkcs7(String dataToVerify, String signToVerify, boolean auth, Date currentDate, String doNotCheck)
			throws CertificateExpiredException, CertificateNotYetValidException, CMSException, IOException,
			NoSuchAlgorithmException, NoSuchProviderException, CertStoreException, CertificateParsingException {

		return verifyPkcs7(dataToVerify, signToVerify, auth, ROOT_CERTS_FOLDER, OCSP_URL, null, null, currentDate, doNotCheck);
	}

	public static CheckSignResult verifyPkcs7(String dataToVerify, String signToVerify, boolean auth,
			String rootCertPath, String ocspUrl, String proxyHost, String proxyPort, Date currentDate, String doNotCheck)
					throws CertificateExpiredException, CertificateNotYetValidException, CMSException, IOException,
					NoSuchAlgorithmException, NoSuchProviderException, CertStoreException, CertificateParsingException {
		return verifyPkcs7(dataToVerify.getBytes(ENCODING), signToVerify, auth, rootCertPath, ocspUrl, proxyHost, proxyPort, currentDate, doNotCheck);
	}		

	public static CheckSignResult verifyPkcs7(byte[] dataToVerify, String signToVerify, boolean auth,
			String rootCertPath, String ocspUrl, String proxyHost, String proxyPort, Date currentDate, String doNotCheck)
					throws CertificateExpiredException, CertificateNotYetValidException, CMSException, IOException,
					NoSuchAlgorithmException, NoSuchProviderException, CertStoreException, CertificateParsingException {

		CheckSignResult res = new CheckSignResult();

		X509Certificate cert = null;

		CMSSignedData signedData = new CMSSignedData(kz.gov.pki.kalkan.util.encoders.Base64.decode(signToVerify));

		boolean isAttachedContent = signedData.getSignedContent() != null;

		if (isAttachedContent) {
			byte[] content = (byte[]) ((CMSProcessableByteArray)signedData.getSignedContent()).getContent();
			System.out.println(new String(content));
			System.out.println(new String(Base64.encode(content)));
			
			signedData = new CMSSignedData(signedData.getEncoded());
		} else {
			CMSProcessableByteArray data = new CMSProcessableByteArray(dataToVerify);
			signedData = new CMSSignedData(data, signedData.getEncoded());
		}

		SignerInformationStore signers = signedData.getSignerInfos();
		CertStore certs = signedData.getCertificatesAndCRLs("Collection", KalkanProvider.PROVIDER_NAME);
		Iterator<?> it = signers.getSigners().iterator();

		if (it.hasNext()) {
			SignerInformation signer = (SignerInformation) it.next();
			X509CertSelector signerConstraints = signer.getSID();
			Collection<?> certCollection = certs.getCertificates(signerConstraints);
			Iterator<?> certIt = certCollection.iterator();

			while (certIt.hasNext()) {
				cert = (X509Certificate) certIt.next();
				
				try {
					System.out.println("cert = " + new String(Base64.encode(cert.getEncoded())));
				} catch (CertificateEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (signer.verify(cert.getPublicKey(), KalkanProvider.PROVIDER_NAME)) {
					res.setDigiSignOK(true);
					res = checkCertificate(res, cert, auth, rootCertPath, ocspUrl, proxyHost, proxyPort, currentDate, doNotCheck);
				}
			}
		}

		return res;
	}
	
	public static TimeStampToken getTSPStamp(byte[] data, String hashAlg) {
		try {
			MessageDigest md = MessageDigest.getInstance(hashAlg, KalkanProvider.PROVIDER_NAME);
			md.update(data);
			byte[] hash = md.digest();
			System.out.println("Hash: " + Hex.encodeStr(hash));
			TimeStampRequestGenerator reqGen = new TimeStampRequestGenerator();
			// требуем сертификат TSA
			reqGen.setCertReq(true);
			// указываем политику, чтобы сервер понял каким ключом подписать 
	        reqGen.setReqPolicy(KNCAObjectIdentifiers.tsa_gost_policy.getId());
	
	        // необязательный nonce, случайное число произвольной длины
	        BigInteger nonce = BigInteger.valueOf(System.currentTimeMillis());
	        
	        TimeStampRequest request = reqGen.generate(hashAlg, hash, nonce);
	        byte[] reqData = request.getEncoded();
	        URL tspUrl = new URL(TSP_URL);
	        HttpURLConnection con = (HttpURLConnection) tspUrl.openConnection();
        	con.setRequestMethod("POST");
        	con.setDoOutput(true);
        	con.setRequestProperty("Content-Type", "application/timestamp-query");
        	OutputStream reqStream = con.getOutputStream();
            reqStream.write(reqData);
            reqStream.close();

	        InputStream respStream = con.getInputStream();
	        
	        TimeStampResponse response = new TimeStampResponse(respStream);
	        System.err.println(response.getStatus());
	        System.err.println(response.getFailInfo());
	        System.err.println(response.getStatusString());
	        response.validate(request);
	        
	        return response.getTimeStampToken();

		} catch (Exception e) {
			
			e.printStackTrace();
			return null;
		}
	}
	

	public static Map<String, Object> getECPParams(JFrame frm, Point location, final String initPath) {
		final JDialog dlg = new JDialog(frm, "Параметры ЭЦП", true);
		final Map<String, Object> res = new HashMap<String, Object>();
		res.put("storeType", -1);

		Insets defInsets = new Insets(2, 5, 2, 5);
		JPanel p = new JPanel(new GridBagLayout());

		JLabel lbl = new JLabel("Тип хранилища");
		p.add(lbl, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, defInsets,
				0, 0));

		final JComboBox cb = new JComboBox();
		cb.addItem("");
		cb.addItem("Файл");
		cb.addItem("Казтокен");
		p.add(cb, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				defInsets, 0, 0));

		lbl = new JLabel("Путь к ключу");
		p.add(lbl, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, defInsets,
				0, 0));

		final JTextField tfPath = new JTextField();
		tfPath.setEditable(false);
		p.add(tfPath, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				defInsets, 0, 0));

		lbl = new JLabel("Пароль");
		p.add(lbl, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, defInsets,
				0, 0));

		final JPasswordField pd = new JPasswordField();
		p.add(pd, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				defInsets, 0, 0));

		cb.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					int index = ((JComboBox) e.getSource()).getSelectedIndex();
					if (index == 1) {
						try {
							String filePath = FileUtil.selectFile(initPath);
							tfPath.setText(filePath);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						/*
						 * } else if (index == 2) { String storeName =
						 * getStoreNames(15); tfPath.setText(storeName);
						 */ } else {
						tfPath.setText("");
					}
				}
			}
		});

		final JButton ok = new JButton("OK");
		p.add(ok, new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, defInsets,
				0, 0));

		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("index = " + cb.getSelectedIndex());
				int storeType = (cb.getSelectedIndex() == 1) ? 17 : (cb.getSelectedIndex() == 2) ? 15 : -1;
				res.put("storeType", storeType);
				res.put("storePath", tfPath.getText());
				res.put("password", new String(pd.getPassword()));
				dlg.dispose();
			}
		});

		dlg.setContentPane(p);
		dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dlg.setPreferredSize(new Dimension(400, 150));
		dlg.pack();
		dlg.setLocation(location);
		dlg.getRootPane().setDefaultButton(ok);
		dlg.setVisible(true);

		return res;
	}

	// Нужно ли проверять свойство сертификта?
	private static boolean needCheckProperty(int prop, String doNotCheck) {
		if (doNotCheck != null && doNotCheck.length() > 0) {
			String[] ps = doNotCheck.split(",");
			for (int i = 0; i < ps.length; i++) {
				if (prop == Integer.parseInt(ps[i]))
					return false;
			}
		} else {
			if (DONT_CHECK_CERT_PROPERTIES != null) {
				for (int p : DONT_CHECK_CERT_PROPERTIES)
					if (p == prop)
						return false;
			}
		}
		return true;
	}

	/*
	 * public static String getStoreNames(int storeType) { StringBuilder sb =
	 * new StringBuilder(); try { TerminalFactory factory =
	 * TerminalFactory.getDefault(); List<CardTerminal> terminals =
	 * factory.terminals().list();
	 * 
	 * for (CardTerminal terminal : terminals) { System.out.println((new
	 * StringBuilder()).append(terminal.getName()).append(" = isPresent = ")
	 * .append(terminal.isCardPresent()).toString()); if
	 * (terminal.isCardPresent()) { switch (storeType) { case 4: // e-token case
	 * 15: // kaztoken case 14: // ID card if (terminal.getName() != null) { if
	 * (sb.length() == 0) sb.append("|"); sb.append(terminal.getName()); }
	 * break; default: System.err.println("Smartcard type is unknown!"); break;
	 * } } } } catch (CardException ex) { ex.printStackTrace(); } return
	 * sb.toString(); }
	 */
    public static Map<String, Object> checkUCGOCertificate(String url, byte[] cert, String keyName) {
        Map<String, Object> res = null;
        
		DataOutputStream dataOutputStream = null;
		DataInputStream dataInputStream = null;
		Log log = SecurityContextHolder.getLog();
    	try {
            //проверка статуса сертификата
        	OCSPUtilities ocspUtilities = new OCSPUtilities();
            byte[] ocspRequest = ocspUtilities.generateOcspRequest(cert, keyName, GammaObjectIndentifiers.gost34311.getId(), null, "gammaca");
			log.info("ocspRequest = " + new String(Base64.encode(ocspRequest)));

			// Устанавливаем соединение с сервером УЦГО
			HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
			urlConn.setDoOutput(true);
			//urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("Content-Type", "application/pkixcmp");
			//urlConn.connect();
	
			// Отправка параметров запроса на сервер
			dataOutputStream = new DataOutputStream(urlConn.getOutputStream());
            dataOutputStream.write(ocspRequest);
            dataOutputStream.flush();
            
			// Читаем параметры ответа
			int responseSize = urlConn.getContentLength();
			log.info("response code = " + urlConn.getResponseCode());
			log.info("length = " + responseSize);
	
            if (responseSize < kz.tamur.comps.Constants.MAX_MESSAGE_SIZE) {
    			// Читаем ответ
            	dataInputStream = new DataInputStream(urlConn.getInputStream());
                ByteArrayOutputStream bos = new ByteArrayOutputStream(responseSize);
            	Funcs.writeStream(dataInputStream, bos, kz.tamur.comps.Constants.MAX_MESSAGE_SIZE);
                bos.close();
                byte response[] = bos.toByteArray();

    			log.info("ocspResponse = " + new String(Base64.encode(response)));
    	        res = ocspUtilities.verifyOcspResponse(response);
            } else
            	throw new IOException("Превышен допустимый размер сообщения: " + kz.tamur.comps.Constants.MAX_MESSAGE_SIZE);

    	} catch (Exception e) {
	        log.error(e, e);
    	} finally {
    		Utils.closeQuietly(dataOutputStream);
    		Utils.closeQuietly(dataInputStream);
    	}
		return res;
    }

    public static Map<String, Object> checkUCGOCertificateLDAP(String url, byte[] cert, String keyName) {
        Map<String, Object> res = null;
        
        //отправка запроса по ldap
		Log log = SecurityContextHolder.getLog();
        try {
            Hashtable<String, String> env = new Hashtable<String, String>();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, url);
            env.put(Context.SECURITY_AUTHENTICATION, "SIMPLE");
            env.put(Context.SECURITY_PRINCIPAL, "");
            env.put(Context.SECURITY_CREDENTIALS, "");

            LdapContext ctx = new InitialLdapContext(env, null);
            //проверка статуса сертификата
            OCSPUtilities ocspUtilities = new OCSPUtilities();
            byte[] ocspRequest = ocspUtilities.generateOcspRequest(cert, keyName, GammaObjectIndentifiers.gost34311.getId(), null, "gammaca");
            //byte[] response = RequestSender.sendRequest(ocspUrl, ocspRequest);

            ExtendedRequest registerRequest = new RegisterRequest(ocspRequest, "1.3.6.1.4.1.6801.11.1.1");
            ExtendedResponse response = ctx.extendedOperation(registerRequest);
            System.out.println(new String(Base64.encode(response.getEncodedValue())));
            res = ocspUtilities.verifyOcspResponse(response.getEncodedValue());
            log.info("ocsp response: " + res.toString());
        } catch (Exception e) {
            log.error("error sending by ldap");
            log.error(e, e);
        }
		return res;
    }

	public static class RegisterRequest implements ExtendedRequest {
		public String ID;
		public byte[] cert;

		RegisterRequest(byte[] certificate, String id) {
			this.ID = id;
			this.cert = certificate;
		}

		public String getID() {
			// return "1.3.6.1.4.1.6801.11.1.4";//Отправка запроса на службу
			// Verify . ощвращает 0 если все OK!, иначе число больше 0.
			// return "1.3.6.1.4.1.6801.11.1.10"; //Отправка запроса на RA.
			// 1.3.6.1.4.1.6801.11.1.1
			return this.ID;
		}

		public byte[] getEncodedValue() {
			return cert;
		}

		public ExtendedResponse createExtendedResponse(String id, byte[] berValue, int offset, int length)
				throws NamingException {
			return new RegisterResponse(id, berValue, offset, length);
		}
	}

	public static class RegisterResponse implements ExtendedResponse {
		public String ID;
		byte[] cert;
	    private static Log ldapLog = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + RegisterResponse.class.getName());

		public RegisterResponse(String id, byte[] berValue, int offset, int length) throws NamingException {
			// Cлужба вернула ответ в byte[] berValue
			// В этом методе можно писать код по обработке, записи в файл и т.д.
			// над berValue
			this.cert = berValue;
			this.ID = id;
			ldapLog.info(new String(berValue));
		}

		public String getID() {
			return ID;
		}

		public byte[] getEncodedValue() {
			return cert;
		}
	}
}
