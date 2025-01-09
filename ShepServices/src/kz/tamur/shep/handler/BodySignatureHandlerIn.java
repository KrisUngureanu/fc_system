package kz.tamur.shep.handler;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.security.auth.x500.X500Principal;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import kz.tamur.fc.common.SignatureCheckingResult;
import kz.tamur.shep.common.ShepError;
import kz.tamur.shep.synchronous.ErrorInfo;
import kz.tamur.shep.synchronous.ObjectFactory;
import kz.tamur.util.crypto.CheckSignResult;
import kz.tamur.util.crypto.KalkanUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.message.WSSecBase;
import org.apache.ws.security.message.token.SecurityTokenReference;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BodySignatureHandlerIn implements SOAPHandler<SOAPMessageContext> {
	
	private static Log log = LogFactory.getLog(BodySignatureHandlerIn.class);

    private static final String p12file = System.getProperty("p12file");
    private static final String password = System.getProperty("p12pass");
    public static final String shepCertPath = System.getProperty("shepCertPath", "shep.crt");

    @Override
    public boolean handleMessage(SOAPMessageContext smc) {
    	Boolean dumpMessage = (Boolean) smc.get("MESSAGE_DUMP");
        Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        SOAPMessage message = smc.getMessage();
        if (outboundProperty.booleanValue()) {
            try {
                SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
                SOAPBody body = envelope.getBody();
	            
                boolean isExistsSignature = (Boolean) smc.get("BODY_SIGNATURE_EXIST");
                if (isExistsSignature) {
                    SOAPHeader header = envelope.getHeader();
                    if (header != null) {
                      envelope.removeChild(header);
                    }
                    header = envelope.addHeader();
                    
                    SOAPElement element = header.addChildElement("Security", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
                    element.addNamespaceDeclaration("wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
                    element.addAttribute(new QName(envelope.getNamespaceURI(), "mustUnderstand", envelope.getPrefix()), "1");
                    
                    body.addAttribute(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id", "wsu"), "bodyId");

                    XMLSignature sig = KalkanUtil.getXmlSignatureWithKeys1(envelope, element, p12file, password);
                    
                    X509Certificate cert = KalkanUtil.loadCertificate(p12file, password.toCharArray());
                    String serial = cert.getSerialNumber().toString();
                    X500Principal d = cert.getIssuerX500Principal();
                    String name = d.getName();
                    
                    WSSecBase ws = new WSSecBase();
                    KeyInfo keyInfo = sig.getKeyInfo();
                    String keyInfoUri = ws.getWsConfig().getIdAllocator().createSecureId("KI-", keyInfo);
					log.info(keyInfoUri);

                    SOAPElement keyInfoelement = (SOAPElement)element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo").item(0);
                    SOAPElement x509DataElement = (SOAPElement)keyInfoelement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "X509Data").item(0);
                  
                    keyInfoelement.removeChild(x509DataElement);

                    keyInfoelement.setAttribute("Id", keyInfoUri);
                    
                    SOAPElement tokenReferenceElement = keyInfoelement.addChildElement(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "SecurityTokenReference", "wsse"));
                    Source src = message.getSOAPPart().getContent();
                    TransformerFactory tf = TransformerFactory.newInstance();
                    Transformer transformer = tf.newTransformer();
                    DOMResult result = new DOMResult();
                    transformer.transform(src, result);
                    Document document = (Document)result.getNode();
                  
                    SecurityTokenReference secRef = new SecurityTokenReference(document);
                    String strUri = ws.getWsConfig().getIdAllocator().createSecureId("STR-", secRef);
                  
                    tokenReferenceElement.addAttribute(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "Id", "wsu"), strUri);
                    
                    SOAPElement X509DataElem = tokenReferenceElement.addChildElement(new QName("http://www.w3.org/2000/09/xmldsig#", "X509Data", "ds"));
                    SOAPElement X509IssuerSerialElem = X509DataElem.addChildElement(new QName("http://www.w3.org/2000/09/xmldsig#", "X509IssuerSerial", "ds"));
                    X509IssuerSerialElem.addChildElement(new QName("http://www.w3.org/2000/09/xmldsig#", "X509IssuerName", "ds")).setTextContent(name);
                    X509IssuerSerialElem.addChildElement(new QName("http://www.w3.org/2000/09/xmldsig#", "X509SerialNumber", "ds")).setTextContent(serial);
                }
            } catch (Exception e) {
            	log.error(e, e);
            }
            
            if (dumpMessage != null && dumpMessage.booleanValue()) {
            	DumpMessageHandler.dumpMessage(smc);
        	}
        } else {
        	if (dumpMessage != null && dumpMessage.booleanValue()) {
        		DumpMessageHandler.dumpMessage(smc);
        	}
        	
        	log.info("START TRANSPORT ECP CHECKING");
            try {
                CheckSignResult checkingResult = null;
                SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
                SOAPHeader header = envelope.getHeader();
                Iterator<?> iterator = header.getChildElements();
                boolean isExistsSignature = false;
                while (iterator.hasNext()) {
                    Object object = iterator.next();
                    if (object instanceof SOAPElement && "Security".equals(((SOAPElement) object).getLocalName())) {
                    	isExistsSignature = true;
                        checkingResult = KalkanUtil.checkXML((SOAPElement) object, shepCertPath);
                    }
                }
                smc.put("BODY_SIGNATURE_EXIST", isExistsSignature);
                smc.setScope("BODY_SIGNATURE_EXIST", Scope.APPLICATION);
                String uuid = UUID.randomUUID().toString();
                smc.put("OR3_MSG_ID_1", uuid);
                smc.setScope("OR3_MSG_ID_1", Scope.APPLICATION);
                SignatureCheckingResult.getCheckingResultMap().put(uuid, checkingResult);
            } catch (Exception e) {
            	log.error(e, e);
            }
        }
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext smc) {
    	SOAPMessage message = smc.getMessage();
        
        QName ifc = (QName) smc.get(MessageContext.WSDL_INTERFACE);
        QName op = (QName) smc.get(MessageContext.WSDL_OPERATION);
        
        // Изменяем faultcode и faultstring
        try {
	    	SOAPFault fault = message.getSOAPBody().getFault();
	    	if (fault != null) {
	    		XmlObject xo = XmlObject.Factory.parse(fault);
	    		String txt = xo.xmlText();
	    		
	    		int beg = txt.indexOf("errorCode>");
	    		if (beg > -1) {
	    			int end = txt.indexOf("</", beg);
	    			String errorCode = txt.substring(beg + 10, end);
	            	fault.setFaultCode(errorCode);
	            	
	        		beg = txt.indexOf("errorMessage>");
	        		if (beg > -1) {
	        			end = txt.indexOf("</", beg);
	        			String errorMsg = txt.substring(beg + 13, end);
	                	fault.setFaultString(errorMsg);
	        		}
	    		} else {
	    			fault.setFaultCode(ShepError.SCE001.name());
	    			String faultData = fault.getFaultString();
	    			fault.setFaultString(ShepError.SCE001.getMessage());

	    			if ("ISyncChannel".equals(ifc.getLocalPart())) {
		    			ErrorInfo info = ShepError.createErrorInfo(faultData, ShepError.SCE001, null);
		    	    	
		    	    	String packageName = "kz.tamur.shep.synchronous";
		    	    	try {
		    	    		JAXBContext context = JAXBContext.newInstance(packageName);
		    		        Marshaller m = context.createMarshaller();
		    		        
		    		        Element faultNode = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().createElement("tempElement");
		    		        m.marshal(new ObjectFactory().createSendMessageFault1SendMessageFault(info), faultNode);
		    		        fault.addDetail().addChildElement(SOAPFactory.newInstance().createElement((Element)faultNode.getFirstChild()));
		    	    	} catch (Exception e) {
		    	    		log.error(e, e);
		    	    	}
	    			} else if ("IAsyncChannelClient".equals(ifc.getLocalPart()) && "sendMessage".equals(op.getLocalPart())) {
		    			kz.bee.bip.common.v10.types.ErrorInfo info = ShepError.createAsyncErrorInfo(faultData, ShepError.SCE001, null);
		    	    	
		    	    	String packageName = "kz.bee.bip.common.v10.types";
		    	    	try {
		    	    		JAXBContext context = JAXBContext.newInstance(packageName);
		    		        Marshaller m = context.createMarshaller();
		    		        
		    		        Element faultNode = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().createElement("tempElement");
		    		        m.marshal(new kz.bee.bip.asyncchannel.v10.types.client.ObjectFactory().createSendMessageFault1SendMessageFault(info), faultNode);
		    		        fault.addDetail().addChildElement(SOAPFactory.newInstance().createElement((Element)faultNode.getFirstChild()));
		    	    	} catch (Exception e) {
		    	    		log.error(e, e);
		    	    	}
	    			} else if ("IAsyncChannelClient".equals(ifc.getLocalPart()) && "changeMassageStatusNotification".equals(op.getLocalPart())) {
		    			kz.bee.bip.common.v10.types.ErrorInfo info = ShepError.createAsyncErrorInfo(faultData, ShepError.SCE001, null);
		    	    	
		    	    	String packageName = "kz.bee.bip.common.v10.types";
		    	    	try {
		    	    		JAXBContext context = JAXBContext.newInstance(packageName);
		    		        Marshaller m = context.createMarshaller();
		    		        
		    		        Element faultNode = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().createElement("tempElement");
		    		        m.marshal(new kz.bee.bip.asyncchannel.v10.types.client.ObjectFactory().createChangeMassageStatusNotificationFault1ChangeMassageStatusNotificationFault(info), faultNode);
		    		        fault.addDetail().addChildElement(SOAPFactory.newInstance().createElement((Element)faultNode.getFirstChild()));
		    	    	} catch (Exception e) {
		    	    		log.error(e, e);
		    	    	}
	    			}
	    		}
	    		
				if (fault.getDetail() != null) {
					for (Iterator it = fault.getDetail().getChildElements(); it.hasNext();) {
						Object tmp = it.next();
						if (tmp instanceof SOAPElement) {
							SOAPElement s = (SOAPElement) tmp;
							xo = XmlObject.Factory.parse(s);
							String msg = xo.xmlText();
							List<String> toRemove = new ArrayList<String>();
							for (Iterator nit = s.getNamespacePrefixes(); nit.hasNext();) {
								String px = (String) nit.next();
								//log.info("Found namespace declaration: " + px + ":" + s.getNamespaceURI(px));
								if (msg.indexOf(px + ":") == -1) {
									//log.info("Found UNUSED namespace declaration: " + px + ":" + s.getNamespaceURI(px));
									toRemove.add(px);
								}
							}

							for (String px : toRemove) {
								s.removeAttribute("xmlns:" + px);
								//log.info("Removing UNUSED namespace declaration: " + px + ":" + s.getNamespaceURI(px));
							}
						}
					}
				}
	    	}
        } catch (Throwable e) {
        	log.error(e, e);
        }
        
        Boolean dumpMessage = (Boolean) smc.get("MESSAGE_DUMP");
    	if (dumpMessage != null && dumpMessage.booleanValue()) {
    		DumpMessageHandler.dumpMessage(smc);
    	}
    	
        return true;
    }

    @Override
    public void close(MessageContext context) {}

    @Override 
    public Set<QName> getHeaders() { 
        QName securityHeader = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security"); 
        HashSet<QName> headers = new HashSet<QName>(); 
        headers.add(securityHeader);         
        return headers; 
    }
}