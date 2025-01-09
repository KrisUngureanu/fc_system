package kz.tamur.shep.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import kz.tamur.util.crypto.CheckSignResult;
import kz.tamur.util.crypto.KalkanUtil;

import org.apache.xml.security.c14n.Canonicalizer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class OIPSignatureTest {

    public static void test() throws IOException, SOAPException {
        try {
            File file = new File("C:/soap.xml");
            InputStream inputStream = new FileInputStream(file);
            Reader reader = new InputStreamReader(inputStream, "UTF-8");

            InputSource is = new InputSource(reader);
            is.setEncoding("UTF-8");
            
          DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
          DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
          Document doc = docBuilder.parse(is);
          
          org.apache.xml.security.Init.init();
          Canonicalizer c14n = Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_WITH_COMMENTS);
          byte[] canonicalMessage = c14n.canonicalizeSubtree(doc);
          ByteArrayInputStream in = new ByteArrayInputStream(canonicalMessage);
          MessageFactory factory = MessageFactory.newInstance();
          SOAPMessage message =  factory.createMessage(null, in);
          SOAPBody body = message.getSOAPBody();
          
          Iterator iterator_1 = body.getChildElements();
          while (iterator_1.hasNext()) {
              Object object_1 = iterator_1.next();
              if (object_1 instanceof SOAPElement && ("SendMessageResponse".equals(((SOAPElement) object_1).getLocalName()))) {
                  Iterator iterator_2 = ((SOAPElement) object_1).getChildElements();
                  while (iterator_2.hasNext()) {
                      Object object_2 = iterator_2.next();
                      if (object_2 instanceof SOAPElement && ("response".equals(((SOAPElement) object_2).getLocalName()))) {
                          Iterator iterator_3 = ((SOAPElement) object_2).getChildElements();
                          while (iterator_3.hasNext()) {
                              Object object_3 = iterator_3.next();
                              if (object_3 instanceof SOAPElement && ("responseData".equals(((SOAPElement) object_3).getLocalName()))) {
                                  Iterator iterator_4 = ((SOAPElement) object_3).getChildElements();
                                  while (iterator_4.hasNext()) {
                                      Object object_4 = iterator_4.next();
                                      if (object_4 instanceof SOAPElement && ("data".equals(((SOAPElement) object_4).getLocalName()))) {
                                          Iterator iterator_5 = ((SOAPElement) object_4).getChildElements();
                                          while (iterator_5.hasNext()) {
                                              Object object_5 = iterator_5.next();
                                              if (object_5 instanceof SOAPElement && ("items".equals(((SOAPElement) object_5).getLocalName()))) {
                                                  
                                                  TransformerFactory transformerFactory = TransformerFactory.newInstance();
                                                  Transformer transformer = transformerFactory.newTransformer();
                                                  DOMSource source = new DOMSource((SOAPElement) object_5);
                                                  StreamResult result = new StreamResult(new File("C:/element.xml"));
                                                  transformer.transform(source, result);
                                                  
                                                  CheckSignResult checkingResult = KalkanUtil.checkXML((SOAPElement) object_5);
                                                  if (!checkingResult.isDigiSignOK()) {
                                                      System.out.println("Сертификат валидный!");
                                                  } else {
                                                      System.out.println("Сертификат не валидный!");
                                                  }
                                              }
                                          }
                                      }
                                  }
                              }
                          }
                      }
                  }
              }
          }
          
          
        }
        catch (Exception e) {
          e.printStackTrace();
        }   
    }
}