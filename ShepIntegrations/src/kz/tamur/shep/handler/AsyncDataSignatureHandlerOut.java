package kz.tamur.shep.handler;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.security.auth.x500.X500Principal;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFactory;
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

import kz.tamur.fc.common.MessageParametrs;
import kz.tamur.util.crypto.CheckSignResult;
import kz.tamur.util.crypto.KalkanUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.security.message.WSSecBase;
import org.apache.ws.security.message.token.SecurityTokenReference;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class AsyncDataSignatureHandlerOut implements SOAPHandler<SOAPMessageContext> {
	
	private static Log log = LogFactory.getLog(AsyncDataSignatureHandlerOut.class);

    private static final String p12file = System.getProperty("p12file");
    private static final String password = System.getProperty("p12pass");
    private static final String shepCertPath = System.getProperty("shepCertPath", "shep.crt");

    @Override
    public boolean handleMessage(SOAPMessageContext smc) {
    	Boolean dumpMessage = (Boolean) smc.get("MESSAGE_DUMP");
        Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        SOAPMessage message = smc.getMessage();
        if (outboundProperty.booleanValue()) {
            String serviceId = null;
            String messageId = null;
            SOAPElement data = null;
            
            try {
                SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
                SOAPBody body = envelope.getBody();
                Iterator iterator_1 = body.getChildElements();
                while (iterator_1.hasNext()) {
                    Object object_1 = iterator_1.next();
                    if (object_1 instanceof SOAPElement && ("sendMessage".equals(((SOAPElement) object_1).getLocalName()))) {
                    	String dataPrefix = null;
                    	String dataNamespace = null;
                    	boolean isAddDataNamespace = false;
                    	
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
                                            if (object_4 instanceof SOAPElement && ("serviceId".equals(((SOAPElement) object_4).getLocalName()))) {
                                            	serviceId = ((SOAPElement) object_4).getTextContent();
                                            	if (serviceId != null && !serviceId.isEmpty()) {
                                            		smc.put("SERVICE_ID", serviceId);
                                            		smc.setScope("SERVICE_ID", Scope.APPLICATION);
                                            	}
                    						} else if (object_4 instanceof SOAPElement && ("messageId".equals(((SOAPElement) object_4).getLocalName()))) {
                    							messageId = ((SOAPElement) object_4).getTextContent();
                    						}
                                        }
                                    } else if (object_3 instanceof SOAPElement && ("messageData".equals(((SOAPElement) object_3).getLocalName()))) {
                        				Iterator iterator_4 = ((SOAPElement) object_3).getChildElements();
                          				while (iterator_4.hasNext()) {
                          					Object object_4 = iterator_4.next();
                          					if (object_4 instanceof SOAPElement && ("data".equals(((SOAPElement) object_4).getLocalName()))) {
                          						data = (SOAPElement) object_4;
                          						NamedNodeMap attrs = ((SOAPElement) object_4).getAttributes();
                          						for (int i = 0; i < attrs.getLength(); i++) {
                          							Attr attr = (Attr) attrs.item(i);
                          							if ("xsi:type".equals(attr.getName())) {
                          								dataPrefix = attr.getValue().split(":")[0];
                          							}
                          						}
                          					}
                          				}
                                    }
                                }
                            }
                        }
                        
                        if (MessageParametrs.DataNamespace.contains(messageId)) {
                        	isAddDataNamespace = true;
                        	MessageParametrs.DataNamespace.remove(messageId);
						}
                        
                        String uri = ((SOAPElement) object_1).getNamespaceURI();
						NamedNodeMap attrs = ((SOAPElement) object_1).getAttributes();
						List<Attr> attrsToDelete = new ArrayList<Attr>();
						for (int i = 0; i < attrs.getLength(); i++) {
							Attr attr = (Attr) attrs.item(i);
							if (!uri.equals(attr.getValue())) {
					  			if ((dataPrefix != null) && (attr.getLocalName().equals(dataPrefix))) {
					  				dataNamespace = attr.getValue();
					  			}
								if (dataPrefix == null || !attr.getLocalName().equals(dataPrefix) || isAddDataNamespace) {
									attrsToDelete.add(attr);
								}
							}
						}
						for (int i = 0; i < attrsToDelete.size(); i++) {
							((SOAPElement) object_1).removeAttributeNode(attrsToDelete.get(i));
						}
					  	if (isAddDataNamespace && dataPrefix != null && dataNamespace != null) {
					        data.addNamespaceDeclaration(dataPrefix, dataNamespace);
					  	}
					  	
					  	// Если необходимо передавать неймспейс корневого элемента веб-сервиса ВИС без префикса
					  	if (MessageParametrs.EmptyPrefixNamespace.containsKey(messageId)) {
					  		String elementName = MessageParametrs.EmptyPrefixNamespace.get(messageId);
					  		if (elementName != null && !elementName.isEmpty()) {
					  			Iterator iterator_data = data.getChildElements();
					  			while (iterator_data.hasNext()) {
					  				Object object_data = iterator_data.next();
					  				if (object_data instanceof SOAPElement && (elementName.equals(((SOAPElement) object_data).getLocalName()))) {
					  					dataPrefix = ((SOAPElement) object_data).getPrefix();
					  					dataNamespace = ((SOAPElement) object_data).getNamespaceURI();
					  					
					  					attrs = ((SOAPElement) object_data).getAttributes();
					  					for (int i = 0; i < attrs.getLength(); i++) {
					  						Attr attr = (Attr) attrs.item(i);
					  						if (attr != null && attr.getName() != null && dataPrefix != null && attr.getLocalName().equals(dataPrefix)) {
					  							((SOAPElement) object_data).removeAttributeNode(attr);
					  							break;
					  						}
					  					}
					  					
					  					((SOAPElement) object_data).removeNamespaceDeclaration(dataPrefix);
					  					((SOAPElement) object_data).setPrefix("");
					  					
					  					break;
					  				}
					  			}
					  		}
					  		MessageParametrs.EmptyPrefixNamespace.remove(messageId);
					  	}
					  	
					  	break;
                    }
                }
                
                // Подписание бизнес данных
                Integer signingType = null;
				String elementName = null;
				
                if (MessageParametrs.DataSignature.containsKey(messageId)) {
                	signingType = MessageParametrs.DataSignature.get(messageId);
                	MessageParametrs.DataSignature.remove(messageId);
                }
                
                // Если необходимо подписать не содержимое <data>, а содержимое корневого элемента веб-сервиса ВИС
				if (MessageParametrs.BusinessDataSignature.containsKey(messageId)) {
					elementName = MessageParametrs.BusinessDataSignature.get(messageId);
					MessageParametrs.BusinessDataSignature.remove(messageId);
				}
				
				SOAPElement object_data = null;
				
            	if ((signingType != null && signingType == 3) || (elementName != null && !elementName.isEmpty())) { 
					log.info("######### Подписание бизнес данных сервиса " + serviceId + " (messageId:" + messageId + ") в хэндлере!");
					
					// Если необходимо подписать не содержимое <data>, а содержимое корневого элемента веб-сервиса ВИС
					if (elementName != null && !elementName.isEmpty()) {
						Iterator iterator_data = data.getChildElements();
						while (iterator_data.hasNext()) {
							Object object_element = iterator_data.next();
							if (object_element instanceof SOAPElement && (elementName.equals(((SOAPElement) object_element).getLocalName()))) {
								object_data = (SOAPElement) object_element;
								break;
							}
						}
					}
					
					if (object_data == null) {
						object_data = data;
					}
					
					String signature = KalkanUtil.getXmlSignatureWithKeys(XmlObject.Factory.parse(object_data).xmlText(), "", p12file, password);
					Element signNode = KalkanUtil.createXmlDocumentFromString(signature, "UTF-8").getDocumentElement();
					SOAPElement se = SOAPFactory.newInstance().createElement(signNode);
					object_data.addChildElement(se);
            	}
            	
            	// Если необходимо передавать бизнес-данные в CDATA
            	if (MessageParametrs.BusinessDataCDATA.remove(messageId)) {
            		log.info("######### Добавленние CDATA для бизнес данных сервиса " + serviceId + " (messageId:" + messageId + ") в хэндлере!");
    				
    				if (object_data == null) {
    					object_data = data;
    				}
					
            		if (object_data != null && object_data instanceof SOAPElement) {
            			String content = object_data.getTextContent();
            			object_data.removeContents();
            			CDATASection section = object_data.getOwnerDocument().createCDATASection(content);
            			object_data.appendChild(section);
					}
				}
            	
            	// Удалим неймспейс подписи бизнес-данных без префикса
    			if (object_data != null && object_data instanceof SOAPElement) {
    				Iterator iterator_signature = ((SOAPElement) object_data).getChildElements();
    				while (iterator_signature.hasNext()) {
    					Object object_signature = iterator_signature.next();
    					if (object_signature instanceof SOAPElement && ("Signature".equals(((SOAPElement) object_signature).getLocalName()))) {
    						NamedNodeMap attrs = ((SOAPElement) object_signature).getAttributes();
    						for (int i = 0; i < attrs.getLength(); i++) {
    							Attr attr = (Attr) attrs.item(i);
    							if (attr != null) {
    								String prefix = attr.getPrefix();
    								if (prefix == null || prefix.isEmpty()) {
    									((SOAPElement) object_signature).removeAttributeNode(attr);
    								}
    							}
    						}
    					}
    				}
    			}

                // Наложение транспортной ЭЦП
                if (MessageParametrs.TransportSignature.contains(messageId)) {
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

                    SOAPElement keyInfoelement = (SOAPElement) element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo").item(0);
                    SOAPElement x509DataElement = (SOAPElement) keyInfoelement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "X509Data").item(0);

                    keyInfoelement.removeChild(x509DataElement);

                    keyInfoelement.setAttribute("Id", keyInfoUri);

                    SOAPElement tokenReferenceElement = keyInfoelement.addChildElement(new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "SecurityTokenReference", "wsse"));
                    Source src = message.getSOAPPart().getContent();
                    TransformerFactory tf = TransformerFactory.newInstance();
                    Transformer transformer = tf.newTransformer();
                    DOMResult result = new DOMResult();
                    transformer.transform(src, result);
                    Document document = (Document) result.getNode();

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
        	
            String correlationId = null;
            try {
                SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
                SOAPBody body = envelope.getBody();
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
                                    if (object_3 instanceof SOAPElement && ("responseInfo".equals(((SOAPElement) object_3).getLocalName()))) {
                                        Iterator iterator_4 = ((SOAPElement) object_3).getChildElements();
                                        while (iterator_4.hasNext()) {
                                            Object object_4 = iterator_4.next();
                                            if (object_4 instanceof SOAPElement && ("correlationId".equals(((SOAPElement) object_4).getLocalName()))) {
                                                correlationId = ((SOAPElement) object_4).getTextContent();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Проверка транспортной подписи
                if (MessageParametrs.TransportSignature.remove(correlationId)) {
                    CheckSignResult checkingResult = null;
                    SOAPHeader header = envelope.getHeader();
                    Iterator<?> iterator = header.getChildElements();
                    while (iterator.hasNext()) {
                        Object object = iterator.next();
                        if (object instanceof SOAPElement && "Security".equals(((SOAPElement) object).getLocalName())) {
                            checkingResult = KalkanUtil.checkXML((SOAPElement) object, shepCertPath);
                            break;
                        }
                    }
                    if (checkingResult == null) {
                    	log.info("Транспортная ЭЦП не найдена! (correlationId: " + correlationId + ")");
                    } else {
                    	log.info(checkingResult.getErrorMessage(false) + " (correlationId: " + correlationId + ")");
                    }
                }
            } catch (Exception e) {
            	log.error(e, e);
            }
        }
        return true;
    }
    
    @Override
    public boolean handleFault(SOAPMessageContext context) {
    	Boolean dumpMessage = (Boolean) context.get("MESSAGE_DUMP");
    	if (dumpMessage != null && dumpMessage.booleanValue()) {
        	DumpMessageHandler.dumpMessage(context);
    	}
        return true;
    }

    @Override
    public void close(MessageContext context) {}

    @Override
    public Set<QName> getHeaders() {
        return null;
    }
}