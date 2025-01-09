package kz.tamur.shep.common;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;

import kz.tamur.util.crypto.KalkanUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class AsyncSignatureOperations {

    private static final String p12file = System.getProperty("p12file");
    private static final String password = System.getProperty("p12pass");
    private static final Log log = LogFactory.getLog(AsyncSignatureOperations.class);
    
    public static Object setSignature(JAXBElement<?> anyJAXBElement, JAXBContext context) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(anyJAXBElement, document);
            
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage message = messageFactory.createMessage();
            SOAPBody body = message.getSOAPBody();
            SOAPElement anySOAPElement = body.addDocument(document);
            
            XmlObject xmlObject = XmlObject.Factory.parse(anySOAPElement);
            String datatxt = xmlObject.xmlText();
            String signature = KalkanUtil.getXmlSignatureWithKeys(datatxt, "", p12file, password);
            
            Element signatureElement = KalkanUtil.createXmlDocumentFromString(signature, "UTF-8").getDocumentElement();
            SOAPElement signatureSOAPElement = SOAPFactory.newInstance().createElement(signatureElement);
            anySOAPElement.addChildElement(signatureSOAPElement);
            
            Unmarshaller unmarshaller = context.createUnmarshaller();
            
            Object signedJAXBElement = unmarshaller.unmarshal(anySOAPElement);
            log.info("ЭЦП успешно добавлена!");
            return ((JAXBElement<?>) signedJAXBElement).getValue();
        } catch(Exception e) {
            log.error("Не удалось добавить ЭЦП!");
            e.printStackTrace();
        }
        return anyJAXBElement.getValue();
    }
}