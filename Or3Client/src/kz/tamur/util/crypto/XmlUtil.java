package kz.tamur.util.crypto;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jdom.input.SAXBuilder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import kz.tamur.util.Funcs;

/**
 * класс для работы с xml
 */
public class XmlUtil {

    /**
     * получает структуру xml из строки
     * @param xmlString xml в виде строки
     * @param charset кодировка
     * @return xml в виде объекта org.w3c.dom.Document
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public static Document createXmlDocumentFromString(String xmlString, String charset) throws SAXException, ParserConfigurationException, IOException {
        Document doc = getDocument(new InputSource(new ByteArrayInputStream(xmlString.getBytes(charset))));
        return doc;
    }

    /**
     * получает структуру xml из файла
     * @param fileName путь к файлу
     * @return xml в виде объекта org.w3c.dom.Document
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public static Document createXmlDocumentFromFile(String fileName) throws SAXException, ParserConfigurationException, IOException {
        Document doc = getDocument(new InputSource(new FileInputStream(Funcs.getCanonicalFile(fileName))));
        return doc;
    }

     /**
     * преобразует документ xml в строку
     * @param doc документ xml
     * @return xml в виде строки
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public static String createStringFromXmlDocument(Document doc) throws SAXException, ParserConfigurationException, IOException, TransformerConfigurationException, TransformerException {
        String xmlString = null;
        if (doc.getFirstChild() != null) {
            StringWriter os = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.setOutputProperty(OutputKeys.INDENT, "no");
            trans.transform(new DOMSource(doc), new StreamResult(os));
            os.flush();
            xmlString = os.toString();
            os.close();
        }

        return xmlString;
    }

    public static DocumentBuilderFactory getDocumentBuilderFactory() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
/*        try {
        	dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        	dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        	dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        } catch (Throwable e) {
        	System.out.println("Document Builder set attributes failed");
        }
*/        return dbf;
    }
    
    public static Document getDocument(InputStream is) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
/*        try {
        	dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        	dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        	dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        } catch (Throwable e) {
        	System.out.println("Document Builder set attributes failed");
        }
*/        return dbf.newDocumentBuilder().parse(is);
    }

    public static Document getDocument(InputSource is) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
/*        try {
        	dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        	dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        	dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        } catch (Throwable e) {
        	System.out.println("Document Builder set attributes failed");
        }
*/        return dbf.newDocumentBuilder().parse(is);
    }

    public static SAXBuilder createSaxBuilder() {
        SAXBuilder sb = new SAXBuilder();
/*        sb.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        sb.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);
        sb.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        sb.setFeature("http://xml.org/sax/features/external-general-entities", false);
        sb.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
*/        return sb;
    }

    public static org.jdom2.input.SAXBuilder createSaxBuilder2() {
        org.jdom2.input.SAXBuilder sb = new org.jdom2.input.SAXBuilder();
/*        sb.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);
        sb.setFeature("http://xml.org/sax/features/external-general-entities", false);
        sb.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
*/        
        return sb;
    }
}



