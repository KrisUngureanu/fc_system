package kz.tamur.shep.handler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

//import kz.gamma.TumarCSP;
//import kz.tamur.soap.handler.CertUtil;  // Gbdul.jar
import kz.tamur.util.crypto.CheckSignResult;
import kz.tamur.util.crypto.KalkanUtil;

//import org.apache.commons.io.IOUtils;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.utils.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sun.security.provider.X509Factory;

public class TestSign {

    public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException, XMLSignatureException, TransformerException, XMLSecurityException, CertificateException {
        File file = new File("C:/test.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        Element element = doc.getDocumentElement();
        Element e1 = (Element) element.getFirstChild();
        Element e2 = (Element) e1.getFirstChild();
        Element e3 = (Element) e2.getFirstChild();
        NodeList l1 = e3.getChildNodes();
        
        
        byte[] b1;
        byte[] b2; 

        
        for (int i = 0; i < l1.getLength(); i++) {
            Element e4 = (Element) l1.item(i);
            if ("empSign".equals(e4.getLocalName())) {
                String cert = e4.getTextContent();
                System.out.println(cert);
                
//                InputStream in = new ByteArrayInputStream(Base64.decode(cert));
//                X509Certificate cet = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(in);

                
//                X509Certificate ce = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(IOUtils.toInputStream(cert));
;
//                KalkanUtil.
//                String signtxt = new String(Base64.decode(cert), "UTF-8");
//                X509Certificate ce = CertUtil.getCertificateFromSignedTxt(cert);

//                X509Certificate c = KalkanUtil.getCertificate(cert.getBytes("UTF-8"));
                
//                X509Certificate ce = CertUtil.getCertificateFromSignedTxt(cert);
//                byte [] decoded = Base64.decode(cert.replaceAll(X509Factory.BEGIN_CERT, "").replaceAll(X509Factory.END_CERT, ""));
//                X509Certificate c = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(decoded));

                b1 =  Base64.decode(cert);

                System.out.println();
//                X509Certificate c = KalkanUtil.getCertificate(kz.gov.pki.kalkan.util.encoders.Base64.decode(cert));
//                X509Certificate c = KalkanUtil.getCertificate(cert.getBytes());


//                System.out.println();
            } else if ("empSignXml".equals(e4.getLocalName())) {
                System.out.println(e4.getTextContent());
                
                String cert = e4.getTextContent();
//
//                String signtxt = new String(Base64.decode(cert), "UTF-8");
                
                b2 =  Base64.decode(cert);

                
                System.out.println();


            }
        }
        ////TumarCSP tumarCSP = new TumarCSP();
        
        
//        NodeList list = e2.getElementsByTagNameNS("http://ws.ap.kz/taskBuilding/types", "empSign");
//        Node node = list.item(0);
//        System.out.println(node.getNodeValue());
//        CheckSignResult checkingResult = KalkanUtil.checkXML(doc.getDocumentElement());
//        System.out.println(checkingResult.getErrorMessage(false));
//        Object a = null;
        
//        System.out.println(UUID.randomUUID().toString());
//        System.out.println(UUID.randomUUID().toString());
//        Date date = new Date();
//        System.out.println(date.getMinutes() + " " + date.getSeconds());
    }
}