package com.cifs.or2.server.orlang;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.gov.pki.kalkan.util.encoders.Base64;
import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;
import kz.tamur.util.crypto.KalkanUtil;

import org.jdom.Element;
import org.jdom.Namespace;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 28.09.2006
 * Time: 19:31:38
 * To change this template use File | Settings | File Templates.
 */
public class SrvSignature {
    private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + SrvSignature.class.getName());
    public static String SECURITY_PROPERTIES_FILE = "security.properties";
    //private KeyStore store;

    public SrvSignature() {
        //init();
    }

    public X509Certificate getCertificate(Element xml)
	    throws GeneralSecurityException
	{
	    try
	    {
            Namespace ns = Namespace.getNamespace("ds", "http://www.w3.org/2000/09/xmldsig#");
            String cert = xml.getChild("Signature", ns).getChild("KeyInfo", ns).getChild("X509Data", ns).getChildText("X509Certificate", ns);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(Base64.decode(cert)));
/*

            Init.init();
            XMLSignature xmlsignature = new XMLSignature(element1, "");
            //removeDsNamespace(element1);
	        KeyInfo keyinfo = xmlsignature.getKeyInfo();
	        return keyinfo.getX509Certificate();

*/
	    }
	    catch(Exception e)
	    {
	        log.error(e, e);
	    }
	    return null;
	}

/*
    public void init() {
        Properties ps = new Properties();
        try {
            File cf = new File(SECURITY_PROPERTIES_FILE);
            String filePath = null, password = null;
            if (cf.exists()) {
                ps.load(new FileInputStream(cf));
                filePath = ps.getProperty("certfile");
                password = ps.getProperty("password");
                //log.debug("KeyStore successfully initialized!");
            }
            if (filePath == null) filePath = "doc.keystore";
            if (password == null) password = "бббббб";

            store = KeyStore.getInstance("jks");
            FileInputStream fis = new FileInputStream(filePath);
            store.load(fis, password.toCharArray());
            fis.close();

        } catch (Exception e) {
            log.error(e, e);
        }
    }

    public void initKeyStore(String filePath, String password) {
        try {
            store = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream(filePath);
            store.load(fis, password.toCharArray());
            fis.close();
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    public Element sign(Element xml, String alias, String password) {
        try {
            // Create a DOM XMLSignatureFactory that will be used to generate the
            // enveloped signature
            String providerName = System.getProperty
                ("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
            Object provider = Class.forName(providerName).newInstance();
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM",
                        (Provider) provider);

            // Create a Reference to the enveloped document (in this case we are
            // signing the whole document, so a URI of "" signifies that) and
            // also specify the SHA1 digest algorithm and the ENVELOPED Transform.
            Reference ref = fac.newReference
                        ("", fac.newDigestMethod(DigestMethod.SHA1, null),
                                Collections.singletonList
                        (fac.newTransform
                        (Transform.ENVELOPED, (TransformParameterSpec) null)),
                                null, null);

            PrivateKey key = (PrivateKey) store.getKey(alias, password.toCharArray());
            Certificate cert = store.getCertificate(alias);
            String signAlg = "";
            if (key.getAlgorithm().equalsIgnoreCase("RSA"))
                signAlg = SignatureMethod.RSA_SHA1;
            else if (key.getAlgorithm().equalsIgnoreCase("DSA"))
                signAlg = SignatureMethod.DSA_SHA1;
            else if (key.getAlgorithm().equalsIgnoreCase("HMAC"))
                signAlg = SignatureMethod.HMAC_SHA1;

            // Create the SignedInfo
            SignedInfo si = fac.newSignedInfo
                    (fac.newCanonicalizationMethod
                    (CanonicalizationMethod.INCLUSIVE_WITH_COMMENTS,
                            (C14NMethodParameterSpec) null),
                            fac.newSignatureMethod(signAlg, null),
                            Collections.singletonList(ref));

            // Create a KeyValue containing the DSA PublicKey that was generated
            KeyInfoFactory kif = fac.getKeyInfoFactory();
            //KeyValue kv = kif.newKeyValue(cert.getPublicKey());
            X509Data cv = kif.newX509Data(Collections.singletonList(cert));

            // Create a KeyInfo and add the KeyValue to it
            KeyInfo ki = kif.newKeyInfo(Collections.singletonList(cv));

            // Instantiate the document to be signed
            // transform JDOM to DOM
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            JDOMSource source = new JDOMSource(xml);
            DOMResult result = new DOMResult();
            trans.transform(source, result);

            org.w3c.dom.Document doc = (org.w3c.dom.Document)result.getNode();

            // Create a DOMSignContext and specify the RSA PrivateKey and
            // location of the resulting XMLSignature's parent element
            DOMSignContext dsc = new DOMSignContext
                    (key, doc.getDocumentElement());

            // Create the XMLSignature (but don't sign it yet)
            XMLSignature signature = fac.newXMLSignature(si, ki);
            // Marshal, generate (and sign) the enveloped signature
            signature.sign(dsc);

            // transform DOM to JDOM
            JDOMResult domRes = new JDOMResult();
            trans.transform(new DOMSource(doc), domRes);
            return domRes.getDocument().getRootElement();
        } catch (Exception e) {
            log.error(e, e);
        }
        return null;
    }

    public Document validate(Element xml, String alias) {
        try {
            // transform JDOM to DOM
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            JDOMSource source = new JDOMSource(xml);
            DOMResult result = new DOMResult();
            trans.transform(source, result);

            org.w3c.dom.Document doc = (org.w3c.dom.Document)result.getNode();

            // Find Signature element
            NodeList nl =
                doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");

            if (nl.getLength() == 0) {
                throw new Exception("Cannot find Signature element");
            }

            // Create a DOM XMLSignatureFactory that will be used to unmarshal the
            // document containing the XMLSignature
            String providerName = System.getProperty
                    ("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
            Object provider = Class.forName(providerName).newInstance();
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM",
                    (Provider) provider);

            Certificate cert = null;
            try {
                cert = store.getCertificate(alias);
            } catch (Exception e) {
                log.error(e, e);
            }
            // Create a DOMValidateContext and specify a KeyValue KeySelector
            // and document context
            DOMValidateContext valContext;
            if (cert != null)
                valContext = new DOMValidateContext(cert.getPublicKey(), nl.item(0));
            else
                valContext = new DOMValidateContext(new KeyValueKeySelector(), nl.item(0));

            // unmarshal the XMLSignature
            XMLSignature signature = fac.unmarshalXMLSignature(valContext);

            // Validate the XMLSignature (generated above)
            boolean coreValidity = signature.validate(valContext);

            // Check core validation status
            if (coreValidity == false) {
                log.error("Signature failed core validation");
                boolean sv = signature.getSignatureValue().validate(valContext);
                log.debug("signature validation status: " + sv);
                // check the validation status of each Reference
                Iterator i = signature.getSignedInfo().getReferences().iterator();
                for (int j = 0; i.hasNext(); j++) {
                    boolean refValid =
                            ((Reference) i.next()).validate(valContext);
                    log.debug("ref[" + j + "] validity status: " + refValid);
                }
            } else {
                log.debug("Signature passed core validation");
            }

            Node signNode = nl.item(0);
            signNode.getParentNode().removeChild(signNode);

            // transform DOM to JDOM
            JDOMResult domRes = new JDOMResult();
            trans.transform(new DOMSource(doc), domRes);
            return domRes.getDocument();
        } catch (Exception e) {
            log.error(e, e);
        }
        return null;
    }

    public int hasSignature(Element xml) {
        try {
            // transform JDOM to DOM
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            JDOMSource source = new JDOMSource(xml);
            DOMResult result = new DOMResult();
            trans.transform(source, result);

            org.w3c.dom.Document doc = (org.w3c.dom.Document)result.getNode();

            // Find Signature element
            NodeList nl =
                doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");

            if (nl.getLength() == 0)
                return 0;
            else
                return 1;
        } catch(Exception e) {
            log.error(e, e);
        }
        return 0;
    }

    public Document validate(Element xml, String certpath, String cn) {
        try {
            // transform JDOM to DOM
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            JDOMSource source = new JDOMSource(xml);
            DOMResult result = new DOMResult();
            trans.transform(source, result);

            org.w3c.dom.Document doc = (org.w3c.dom.Document)result.getNode();

            // Find Signature element
            NodeList nl =
                doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");

            if (nl.getLength() == 0) {
                throw new Exception("Cannot find Signature element");
            }

            // Create a DOM XMLSignatureFactory that will be used to unmarshal the
            // document containing the XMLSignature
            String providerName = System.getProperty
                    ("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
            Object provider = Class.forName(providerName).newInstance();
            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM",
                    (Provider) provider);

            FileInputStream fis = new FileInputStream(certpath);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Collection c = cf.generateCertificates(fis);
            Iterator it = c.iterator();
            X509Certificate cert = null;
            while (it.hasNext()) {
                X509Certificate cert1 = (X509Certificate)it.next();
                String p = cert1.getSubjectDN().getName();
                String[] strs = p.split(", ");
                String subjectName = "";
                for (String str : strs) {
                    if (str.startsWith("CN=")) {
                        subjectName = str.substring(3);
                        break;
                    }
                }
                if (subjectName.equals(cn)) {
                    cert = cert1;
                    break;
                }
            }
            // Create a DOMValidateContext and specify a KeyValue KeySelector
            // and document context
            DOMValidateContext valContext;
            if (cert != null)
                valContext = new DOMValidateContext(cert.getPublicKey(), nl.item(0));
            else
                valContext = new DOMValidateContext(new KeyValueKeySelector(), nl.item(0));

            // unmarshal the XMLSignature
            XMLSignature signature = fac.unmarshalXMLSignature(valContext);

            // Validate the XMLSignature (generated above)
            boolean coreValidity = signature.validate(valContext);

            // Check core validation status
            if (coreValidity == false) {
                return null;
            } else {
                log.debug("Signature passed core validation");
            }

            Node signNode = nl.item(0);
            signNode.getParentNode().removeChild(signNode);

            // transform DOM to JDOM
            JDOMResult domRes = new JDOMResult();
            trans.transform(new DOMSource(doc), domRes);
            return domRes.getDocument();
        } catch (Exception e) {
            log.error(e, e);
        }
        return null;
    }

    public String sign(String text, String alias, String pass) {
        try {
            PrivateKey key = (PrivateKey) store.getKey(alias, pass.toCharArray());
            X509Certificate cert = (X509Certificate) store.getCertificate(alias);

            Signature signature = Signature.getInstance(cert.getSigAlgName(), "BC");
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
            X509Certificate cert = (X509Certificate) store.getCertificate(alias);
            Signature signature = Signature.getInstance(cert.getSigAlgName(), "BC");
            signature.initVerify(cert.getPublicKey());

            signature.update(text.getBytes("UTF-8"));

            return signature.verify(Base64.decode(sign));
        } catch (Exception e) {
            log.error(e, e);
        }
        return false;
    }

    public byte[] sign(byte[] text, String alias, String pass) {
        try {
            PrivateKey key = (PrivateKey) store.getKey(alias, pass.toCharArray());
            X509Certificate cert = (X509Certificate) store.getCertificate(alias);

            Signature signature = Signature.getInstance(cert.getSigAlgName());
            signature.initSign(key);

            signature.update(text);

            return Base64.encode(signature.sign());
        } catch (Exception e) {
            log.error(e, e);
        }
        return null;
    }

    public boolean verify(byte[] text, byte[] sign, String alias) {
        try {
            X509Certificate cert = (X509Certificate) store.getCertificate(alias);
            Signature signature = Signature.getInstance(cert.getSigAlgName());
            signature.initVerify(cert.getPublicKey());

            signature.update(text);

            return signature.verify(Base64.decode(sign));
        } catch (Exception e) {
            log.error(e, e);
        }
        return false;
    }
    */
/**
     * KeySelector which retrieves the public key out of the
     * KeyValue element and returns it.
     * NOTE: If the key algorithm doesn't match signature algorithm,
     * then the public key will be ignored.
     */
/*
    private static class KeyValueKeySelector extends KeySelector {
        public KeySelectorResult select(KeyInfo keyInfo,
                                        KeySelector.Purpose purpose,
                                        AlgorithmMethod method,
                                        XMLCryptoContext context)
                throws KeySelectorException {
            if (keyInfo == null) {
                throw new KeySelectorException("Null KeyInfo object!");
            }
            SignatureMethod sm = (SignatureMethod) method;
            List list = keyInfo.getContent();

            for (int i = 0; i < list.size(); i++) {
                XMLStructure xmlStructure = (XMLStructure) list.get(i);
                if (xmlStructure instanceof KeyValue) {
                    PublicKey pk = null;
                    try {
                        pk = ((KeyValue) xmlStructure).getPublicKey();
                    } catch (KeyException ke) {
                        throw new KeySelectorException(ke);
                    }
                    // make sure algorithm is compatible with method
                    if (algEquals(sm.getAlgorithm(), pk.getAlgorithm())) {
                        return new SimpleKeySelectorResult(pk);
                    }
                }
                else if (xmlStructure instanceof X509Data) {
                    Certificate pk = null;
                    try {
                        pk = (Certificate)((X509Data) xmlStructure).getContent().get(0);
                    } catch (Exception ke) {
                        throw new KeySelectorException(ke);
                    }
                    // make sure algorithm is compatible with method
                    if (algEquals(sm.getAlgorithm(), pk.getPublicKey().getAlgorithm())) {
                        return new SimpleKeySelectorResult(pk.getPublicKey());
                    }
                }
            }
            throw new KeySelectorException("No KeyValue element found!");
        }

        //@@@FIXME: this should also work for key types other than DSA/RSA
        static boolean algEquals(String algURI, String algName) {
            if (algName.equalsIgnoreCase("DSA") &&
                    algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1)) {
                return true;
            } else if (algName.equalsIgnoreCase("RSA") &&
                    algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1)) {
                return true;
            } else {
                return false;
            }
        }
    }

    private static class SimpleKeySelectorResult implements KeySelectorResult {
        private PublicKey pk;

        SimpleKeySelectorResult(PublicKey pk) {
            this.pk = pk;
        }

        public Key getKey() {
            return pk;
        }
    }
*/
    
    public String sendRequestToUCGO(String url, Map<String, String> params) {
		String response = null;
    	try {
			// Записывам параметры запроса в строку param1=value1&param2=value2&...
			StringBuilder json = new StringBuilder();
			for (String name : params.keySet()) {
				if (json.length() > 0) json.append("&");
				json.append(URLEncoder.encode(name, "UTF-8"));
				json.append('=');
				log.info(params.get(name));
				json.append(URLEncoder.encode(params.get(name), "UTF-8"));
			}
			log.info(url + "?" + json.toString());

			// Устанавливаем соединение с сервером УЦГО
			HttpURLConnection urlConn = (HttpURLConnection) new URL(url).openConnection();
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setRequestMethod("POST");
			urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			// Чтобы ответ был в виде JSON
			urlConn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			urlConn.connect();
	
			// Отправка параметров запроса на сервер
			OutputStream output = urlConn.getOutputStream();
			output.write(json.toString().getBytes());
			output.flush();
			output.close();
	
			// Читаем параметры ответа
			log.info("response code = " + urlConn.getResponseCode());
			log.info("length = " + urlConn.getContentLength());
	
			// Читаем ответ
			InputStream is = urlConn.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
        	Funcs.writeStream(is, bos, Constants.MAX_MESSAGE_SIZE);
        	is.close();
            bos.close();
            response = new String(bos.toByteArray(), "UTF-8").replaceAll("\\\\", "");
    	} catch (Exception e) {
	        log.error(e, e);
    	}
		return response;
	}

    public Map<String, Object> checkUCGOCertificate(String url, byte[] cert, String keyName) {
        return KalkanUtil.checkUCGOCertificate(url, cert, keyName);
    }

    public Map<String, Object> checkUCGOCertificateLDAP(String url, byte[] cert, String keyName) {
        return KalkanUtil.checkUCGOCertificateLDAP(url, cert, keyName);
    }
}
