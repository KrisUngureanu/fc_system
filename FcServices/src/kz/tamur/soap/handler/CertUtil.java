package kz.tamur.soap.handler;

import java.io.IOException;
import java.security.cert.X509Certificate;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import kz.tamur.util.crypto.KalkanUtil;

public class CertUtil {

   private static final String ENCODING = "UTF-8";

   public static X509Certificate getCertificateFromSignedTxt(String signtxt) throws KeyResolverException, XMLSignatureException, XMLSecurityException, SAXException, ParserConfigurationException, IOException{

	   Element signXml = KalkanUtil.createXmlDocumentFromString(signtxt, ENCODING).getDocumentElement();
	   X509Certificate cert = getCertificateFromSignedXml(signXml);
	   return cert;
	   
    }
   public static X509Certificate getCertificateFromSignedXml(Element signXml) throws KeyResolverException, XMLSignatureException, XMLSecurityException, SAXException, ParserConfigurationException, IOException{

       Element signElement = null;
       for (int i = 0; i < signXml.getChildNodes().getLength(); i++) {
           if (signXml.getChildNodes().item(i).getNodeName().compareTo("ds:Signature") == 0) {
               signElement = (Element) signXml.getChildNodes().item(i);
               break;
           }
       }

       if (signElement == null) return null;

       XMLSignature signature = new XMLSignature(signElement, "");
       KeyInfo ki = signature.getKeyInfo();

       X509Certificate cert = ki.getX509Certificate();

       return cert;
   }

}
