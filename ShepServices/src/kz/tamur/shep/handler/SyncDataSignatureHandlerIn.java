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
import javax.xml.soap.SOAPFactory;
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
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class SyncDataSignatureHandlerIn implements SOAPHandler<SOAPMessageContext> {
	
	private static Log log = LogFactory.getLog(SyncDataSignatureHandlerIn.class);

    private static final String p12file = System.getProperty("p12file");
    private static final String password = System.getProperty("p12pass");
	
    @Override
    public boolean handleMessage(SOAPMessageContext smc) {
        Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        SOAPMessage message = smc.getMessage();
        if (outboundProperty.booleanValue()) {
            String messageId = null;
        	SOAPElement data = null;
        	
        	boolean isAddDataNamespace = (Boolean) smc.get("ADD_DATA_NAMESPACE");
        	boolean isAddDataSignature = (Boolean) smc.get("ADD_DATA_SIGNATURE");
        	boolean isBusinessDataCDATA = (Boolean) smc.get("ADD_BUSINESS_DATA_CDATA");
        	
        	String elementNameEmptyPrefix = (String) smc.get("ADD_EMPTY_PREFIX_NAMESPACE");
        	String elementNameSignature = (String) smc.get("ADD_BUSINESS_DATA_SIGNATURE");
        	
        	String serviceId = (String) smc.get("SERVICE_ID");
        	
        	try {
        		SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        		SOAPBody body = envelope.getBody();
        		Iterator iterator_1 = body.getChildElements();
        		while (iterator_1.hasNext()) {
        			Object object_1 = iterator_1.next();
        			if (object_1 instanceof SOAPElement && ("SendMessageResponse".equals(((SOAPElement) object_1).getLocalName()))) {
        				String dataPrefix = null;
        				String dataNamespace = null;
        				
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
        								  if (object_4 instanceof SOAPElement && ("messageId".equals(((SOAPElement) object_4).getLocalName()))) {
        									  messageId = ((SOAPElement) object_4).getTextContent();
        								  }
        							  }
        							} else if (object_3 instanceof SOAPElement && ("responseData".equals(((SOAPElement) object_3).getLocalName()))) {
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
						  if (elementNameEmptyPrefix != null && !elementNameEmptyPrefix.isEmpty()) {
							  Iterator iterator_data = data.getChildElements();
							  while (iterator_data.hasNext()) {
								  Object object_data = iterator_data.next();
								  if (object_data instanceof SOAPElement && (elementNameEmptyPrefix.equals(((SOAPElement) object_data).getLocalName()))) {
									  dataPrefix = ((SOAPElement) object_data).getPrefix();
									  dataNamespace = ((SOAPElement) object_data).getNamespaceURI();
									  
									  attrs = ((SOAPElement) object_data).getAttributes();
									  for (int i = 0; i < attrs.getLength(); i++) {
										  Attr attr = (Attr) attrs.item(i);
										  if (attr.getLocalName().equals(dataPrefix)) {
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
					  	
			            break;
        			}
        		}
        		
        		SOAPElement object_data = null;
				
				// Подписание бизнес данных
            	if (isAddDataSignature || (elementNameSignature != null && !elementNameSignature.isEmpty())) {
            		log.info("######### Подписание бизнес данных сервиса " + serviceId + " (messageId:" + messageId + ") в хэндлере!");
					
					// Если необходимо подписать не содержимое <data>, а содержимое корневого элемента веб-сервиса ВИС
					if (elementNameSignature != null && !elementNameSignature.isEmpty()) {
						Iterator iterator_data = data.getChildElements();
						while (iterator_data.hasNext()) {
							Object object_element = iterator_data.next();
							if (object_element instanceof SOAPElement && (elementNameSignature.equals(((SOAPElement) object_element).getLocalName()))) {
								object_data = (SOAPElement) object_element;
								break;
							}
						}
					}
					
					if (object_data == null) {
						object_data = data;
					}
					
					if (object_data != null) {
						String signature = KalkanUtil.getXmlSignatureWithKeys(XmlObject.Factory.parse(object_data).xmlText(), "", p12file, password);
						Element signNode = KalkanUtil.createXmlDocumentFromString(signature, "UTF-8").getDocumentElement();
						SOAPElement se = SOAPFactory.newInstance().createElement(signNode);
						object_data.addChildElement(se);
					}
            	}
            	
            	// Если необходимо передавать бизнес-данные в CDATA
            	if (isBusinessDataCDATA) {
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
        	} catch (Exception e) {
            	log.error(e, e);
            }
        } else {
            log.info("START BUSSINESS DATA ECP CHECKING");
            String serviceId = null;
            try {
                String senderId = null;
                SOAPElement data = null;
                SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
                SOAPBody body = envelope.getBody();
                Iterator iterator_1 = body.getChildElements();
                while (iterator_1.hasNext()) {
                    Object object_1 = iterator_1.next();
                    if (object_1 instanceof SOAPElement && ("SendMessage".equals(((SOAPElement) object_1).getLocalName()))) {
                        Iterator iterator_2 = ((SOAPElement) object_1).getChildElements();
                        while (iterator_2.hasNext()) {
                            Object object_2 = iterator_2.next();
                            if (object_2 instanceof SOAPElement && ("request".equals(((SOAPElement) object_2).getLocalName()))) {
                                Iterator iterator_3 = ((SOAPElement) object_2).getChildElements();
                                while (iterator_3.hasNext()) {
                                    Object object_3 = iterator_3.next();
                                    if (object_3 instanceof SOAPElement && ("requestInfo".equals(((SOAPElement) object_3).getLocalName()))) {
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
                                    } else if (object_3 instanceof SOAPElement && ("requestData".equals(((SOAPElement) object_3).getLocalName()))) {
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
                } else {
                	log.info("Не найдена подпись для сервиса serviceId = " + serviceId + ", senderId = " + senderId + ", checkingType = " + checkingType);
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