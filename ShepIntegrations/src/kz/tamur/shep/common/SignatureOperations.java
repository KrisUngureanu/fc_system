package kz.tamur.shep.common;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import kz.tamur.util.crypto.KalkanUtil;

import org.apache.xml.security.utils.Base64;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;

public class SignatureOperations {

    private static final String p12file = System.getProperty("p12file");
    private static final String password = System.getProperty("p12pass");
    
    public static String getSignatureString(JAXBElement<?> jaxbElement, JAXBContext context) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(jaxbElement, document);
            
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage message = messageFactory.createMessage();
            SOAPBody body = message.getSOAPBody();
            SOAPElement soapElement = body.addDocument(document);
            
            XmlObject xdo = XmlObject.Factory.parse(soapElement);
            String datatxt = xdo.xmlText();
            String signature = KalkanUtil.getXmlSignatureWithKeys(datatxt, "", p12file, password);
            return Base64.encode(signature.getBytes("UTF-8"));
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}