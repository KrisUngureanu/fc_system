package kz.tamur.shep.handler;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import kz.tamur.admin.SignatureCheckingParams;
import kz.tamur.fc.common.SignatureCheckingResult;
import kz.tamur.util.crypto.CheckSignResult;
import kz.tamur.util.crypto.KalkanUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

public class AsyncDataSignatureHandlerIn implements SOAPHandler<SOAPMessageContext> {

	private static Log log = LogFactory.getLog(AsyncDataSignatureHandlerIn.class);
    
    @Override
    public boolean handleMessage(SOAPMessageContext smc) {
        Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        SOAPMessage message = smc.getMessage();
        if (outboundProperty.booleanValue()) {
        	try {
        		SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
				SOAPBody body = envelope.getBody();
				Iterator iterator_1 = body.getChildElements();
				while (iterator_1.hasNext()) {
					Object object_1 = iterator_1.next();
					if (object_1 instanceof SOAPElement && ("sendMessageResponse".equals(((SOAPElement) object_1).getLocalName()))) {
						String uri = ((SOAPElement) object_1).getNamespaceURI();
						NamedNodeMap attrs = ((SOAPElement) object_1).getAttributes();
						List<Attr> attrsToDelete = new ArrayList<Attr>();
						for (int i = 0; i < attrs.getLength(); i++) {
			                Attr attr = (Attr) attrs.item(i);
			                if (!uri.equals(attr.getValue())) {
		                		attrsToDelete.add(attr);
			                }
			            }
			            for (int i = 0; i < attrsToDelete.size(); i++) {
			                ((SOAPElement) object_1).removeAttributeNode(attrsToDelete.get(i));
			            }
			            break;
					}
				}
        	} catch (Exception e) {
                log.error(e, e);
        	}
        } else {
        	log.info("START BUSSINESS DATA ECP CHECKING");
            String serviceId = null;
            String senderId = null;
            SOAPElement data = null;
            try {
                SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
                SOAPBody body = envelope.getBody();
                Iterator iterator_1 = body.getChildElements();
                while (iterator_1.hasNext()) {
                    Object object_1 = iterator_1.next();
                    if (object_1 instanceof SOAPElement && ("sendMessage".equals(((SOAPElement) object_1).getLocalName()))) {
                        Iterator iterator_2 = ((SOAPElement) object_1).getChildElements();
                        while (iterator_2.hasNext()) {
                            Object object_2 = iterator_2.next();
                            if (object_2 instanceof SOAPElement && ("request".equals(((SOAPElement) object_2).getLocalName()))) {
                                Iterator iterator_3 = ((SOAPElement) object_2).getChildElements();
                                while (iterator_3.hasNext()) {
                                    Object object_3 = iterator_3.next();
                                    if (object_3 instanceof SOAPElement && ("messageInfo".equals(((SOAPElement) object_3).getLocalName()))) {
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
                                    } else if (object_3 instanceof SOAPElement && ("messageData".equals(((SOAPElement) object_3).getLocalName()))) {
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
                
                CheckSignResult checkingResult = null;
                int checkingType = SignatureCheckingParams.getCheckingType(serviceId, senderId);
                if (checkingType == 0) {
                    checkingResult = KalkanUtil.checkXML(data.getTextContent());
                } else if (checkingType == 1) {
                    XmlObject xo = XmlObject.Factory.parse(data);
                    String txt = xo.xmlText();
                    checkingResult = KalkanUtil.checkXML(txt);
                } else if (checkingType == 2) {
                    iterator_1 = ((SOAPElement) data).getChildElements();
                    while (iterator_1.hasNext()) {
                        Object object_1 = iterator_1.next();
                        if (object_1 instanceof SOAPElement) {
                            XmlObject xo = XmlObject.Factory.parse((SOAPElement) object_1);
                            String txt = xo.xmlText();
                            checkingResult = KalkanUtil.checkXML(txt);
                            break;
                        }
                    }
                } else if (checkingType == 3) {
                    checkingResult = KalkanUtil.checkXML(data);
                } else if (checkingType == 4) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setNamespaceAware(true);
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(new ByteArrayInputStream((data.getValue()).getBytes()));
                    checkingResult = KalkanUtil.checkXML(document.getDocumentElement());
                }    
                
                if (checkingResult != null) {
                	log.info(checkingResult.getErrorMessage(false));
                }
                String uuid = UUID.randomUUID().toString();
                smc.put("OR3_MSG_ID_2", uuid);
                smc.setScope("OR3_MSG_ID_2", Scope.APPLICATION);
                SignatureCheckingResult.getCheckingResultMap().put(uuid, checkingResult);
            } catch (Exception e) {
            	log.error(e, e);
            }
        }
        return true;
    }
    
    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {}

    @Override
    public Set<QName> getHeaders() {
        return null;
    }
}