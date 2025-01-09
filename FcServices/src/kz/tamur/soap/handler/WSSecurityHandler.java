package kz.tamur.soap.handler;

import java.util.List;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.xml.security.utils.Base64;
import org.apache.xmlbeans.XmlObject;
import org.w3c.dom.Element;

import kz.tamur.admin.ErrorsNotification;
import kz.tamur.util.crypto.CheckSignResult;
import kz.tamur.util.crypto.KalkanUtil;

public class WSSecurityHandler implements SOAPHandler<SOAPMessageContext> {

	private static final String p12file = System.getProperty("p12file");
	private static final String password = System.getProperty("p12pass");
	private static final String rootCertPath = System.getProperty("rootCertPath");
	private static final String ocspUrl = System.getProperty("ocspUrl");
	private static final String proxyHost = System.getProperty("proxyHost");
	private static final String proxyPort = System.getProperty("proxyPort");
	private SOAPMessageContext psmc;
	public WSSecurityHandler(){
		this.psmc=null;
	}
	public WSSecurityHandler(SOAPMessageContext smc){
		this.psmc=smc;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
	       Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

           SOAPMessage message = smc.getMessage();
           if (outboundProperty.booleanValue()) {
	            try {
	                if(p12file!=null && password!=null) {
        	                SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
        	                SOAPBody body = envelope.getBody();
        	                Iterator it = body.getChildElements();
							SOAPElement data = null;
							SOAPElement head = null;
							while(it.hasNext()){
								Object o = it.next();
								if (o instanceof SOAPElement) {
				                	data = (SOAPElement)o;
				                	System.out.println("Found response = " + data.getNodeName());
		        	                Iterator it2 = data.getChildElements();
									while (it2.hasNext()) {
										Object o2 = it2.next();
										if (o2 instanceof SOAPElement) {
						                	head = (SOAPElement)o2;
						                	System.out.println("Found head = " + head.getNodeName());
											break;
										}
									}
									break;
								}
							}

							if (head != null) {
			                	XmlObject xdo = XmlObject.Factory.parse(data);
			                	String datatxt = xdo.xmlText();
			                	String signature = KalkanUtil.getXmlSignatureWithKeys(datatxt, "", p12file, password);
		                        Element signNode = KalkanUtil.createXmlDocumentFromString(signature, "UTF-8").getDocumentElement();
		                        SOAPElement se = SOAPFactory.newInstance().createElement(signNode);
		    	                XmlObject xse = XmlObject.Factory.parse(se);
			                	String setxt = xse.xmlText();
				                int k = datatxt.indexOf("</" + data.getNodeName());
				                datatxt = datatxt.substring(0, k) + setxt + datatxt.substring(k);
			                	SOAPElement digisign = head.addChildElement("digiSign");
			                	digisign.setTextContent(Base64.encode(datatxt.getBytes("UTF-8")));
			                	//data.insertBefore(head, nextNode);
        
			                	System.out.println("Added Signature");
							} else {
			                	System.out.println("NO DATA TO SIGN");
							}
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	                System.out.println("Failed to add Signature");
	            }

	        } else {
	            try {
	     	        Map map=(Map) smc.get(MessageContext.HTTP_REQUEST_HEADERS);
	     	        List ct= (List) map.get("Content-type");
                	System.out.println("Content-type = " + ct);
	     	        if(ct==null){
	     	        	ct=Arrays.asList("text/xml");
	     	        	map.put("Content-type",ct);
	     	        }
	                SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
	                SOAPBody body = envelope.getBody();

	                Iterator it = body.getChildElements();
					SOAPElement data = null;
					SOAPElement head = null;
					while(it.hasNext()){
						Object o = it.next();
						if (o instanceof SOAPElement) {
		                	data = (SOAPElement)o;
		                	System.out.println("Found request = " + data.getNodeName());
        	                Iterator it2 = data.getChildElements();
							while (it2.hasNext()) {
								Object o2 = it2.next();
								if (o2 instanceof SOAPElement) {
				                	head = (SOAPElement)o2;
				                	System.out.println("Found head = " + head.getNodeName());
									break;
								}
							}
							break;
						}
					}

					if (head != null) {
						SOAPElement s = getElementByName(head, "digiSign");
						SOAPElement sender = getElementByName(head, "codeBank");
						CheckSignResult ok = new CheckSignResult();
						if (s != null && s.getTextContent() != null) {
							String cont = s.getTextContent();
							byte[] b = Base64.decode(cont);
							String str = new String(b, "UTF-8");
							XmlObject xo = XmlObject.Factory.parse(str);
			                String txt = xo.xmlText();
			                ok = KalkanUtil.checkXML(txt, rootCertPath, ocspUrl, proxyHost, proxyPort);
			                System.out.println("Signature:" + ok.isOK());
						} else {
			            	System.out.println("No digiSign Element");
						}
						if(!ok.isOK() && ErrorsNotification.isInitialize()){
							String senderCont="";
							if (sender != null && sender.getTextContent() != null) {
								senderCont = sender.getTextContent();
							}
							ErrorsNotification.notifyErrors("TO_106",
									"SERVICE_"+data.getNodeName()+"_SENDER_"+senderCont,
									ok.getErrorMessage(false),
									null,
									null);
						}
					} else {
		            	System.out.println("No Head element");
					}
	                return true;
	            } catch (Exception ex) {
	                    ex.printStackTrace();
	            }
	            System.out.println("NO Signature");
	        }

	        return true;
	}

    private static SOAPElement getElementByName(SOAPElement tag, String name){
        Iterator it = tag.getChildElements();
        while (it.hasNext()) {
        	Object o=it.next();
        	if (o instanceof SOAPElement && (name.equals(((SOAPElement)o).getLocalName()))) {
        		return (SOAPElement)o;
            }
        }
        return null;
    }
    
	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return false;
	}

	@Override
	public void close(MessageContext context) {
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}
}
