package kz.tamur.shep.handler;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;

public class DumpMessageHandler implements SOAPHandler<SOAPMessageContext> {

    private static final boolean disableDumpMessageHandler = Boolean.parseBoolean(System.getProperty("disableDumpMessageHandler", "false"));
    
    private static Log log = LogFactory.getLog(DumpMessageHandler.class);

    @Override
    public void close(MessageContext arg0) {
    }

    @Override
    public boolean handleFault(SOAPMessageContext arg0) {
        return true;
    }

    @Override
    public boolean handleMessage(SOAPMessageContext smc) {
        if (!disableDumpMessageHandler) {
            Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
            SOAPMessage message = smc.getMessage();
            try {
                if (outboundProperty.booleanValue()) {
                    log.info("Outbound message:");
                } else {
                    log.info("Inbound message:");
                }
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                message.writeTo(bos);
                log.info(new String(bos.toByteArray()));
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        return true;
    }

    @Override
    public Set<QName> getHeaders() {
        return Collections.emptySet();
    }
    
    public static void dumpMessage(MessageContext smc) {
    	try {
    		SoapMessage msg = (SoapMessage) ((WrappedMessageContext) smc).getWrappedMessage();
    		Exchange exchange = msg.getExchange();
    		SOAPMessage message = msg.getContent(SOAPMessage.class);
    		
    		boolean isRequestor = MessageUtils.isRequestor(msg);
            boolean isFault = MessageUtils.isFault(msg);
            boolean isOutbound = MessageUtils.isOutbound(msg);
            
            String serviceId = (String) smc.get("SERVICE_ID");
            
    		String typeMessage = "";
    		String par = "";
    		
    		if (isOutbound) {
                if (isFault) {
                	typeMessage = "FAULT_OUT";
                } else {
                	typeMessage = isRequestor ? "REQ_OUT" : "RESP_OUT";
                }
            } else {
                if (isFault) {
                	typeMessage = "FAULT_IN";
                } else {
                	typeMessage = isRequestor ? "RESP_IN" : "REQ_IN";
                }
            }
    		
    		String logMessage = typeMessage;

    		par = (String) exchange.get(LoggingMessage.ID_KEY);
    		if (par != null && !par.isEmpty()) {
    			logMessage += "\n    ID: " + par;
    		}
    		if (((Integer) smc.get(MessageContext.HTTP_RESPONSE_CODE)) != null) {
    			par = ((Integer) smc.get(MessageContext.HTTP_RESPONSE_CODE)).toString();
        		if (par != null && !par.isEmpty()) {
        			logMessage += "\n    ResponseCode: " + par;
        		}
    		}
    		if ((isOutbound && isRequestor) || (!isOutbound && !isRequestor)) {
	    		par = (String) smc.get(Message.ENDPOINT_ADDRESS);
	    		if (par == null || par.isEmpty()) {
	    			par = (String) smc.get("org.apache.cxf.request.url");
	    		}
	    		if (par != null && !par.isEmpty()) {
	    			logMessage += "\n    Address: " + par;
	    		}
    		}
    		par = (String) smc.get(Message.ENCODING);
    		if (par != null && !par.isEmpty()) {
    			logMessage += "\n    Encoding: " + par;
    		}
    		if ((isOutbound && isRequestor) || (!isOutbound && !isRequestor)) {
    			par = (String) smc.get("org.apache.cxf.request.method");
        		if (par != null && !par.isEmpty()) {
        			logMessage += "\n    Http-Method: " + par;
        		}
    		}
    		par = (String) smc.get("Content-Type");
    		if (par != null && !par.isEmpty()) {
    			logMessage += "\n    Content-Type: " + par;
    		}
    		if (((QName) smc.get(MessageContext.WSDL_PORT)) != null) {
    			par = ((QName) smc.get(MessageContext.WSDL_PORT)).getLocalPart();
        		if (par != null && !par.isEmpty()) {
        			logMessage += "\n    PortName: " + par;
        		}
    		}
    		if (((QName) smc.get(MessageContext.WSDL_INTERFACE)) != null) {
    			par = ((QName) smc.get(MessageContext.WSDL_INTERFACE)).getLocalPart();
        		if (par != null && !par.isEmpty()) {
        			logMessage += "\n    PortTypeName: " + par;
        			
        			 if (serviceId == null || serviceId.isEmpty()) {
        				 serviceId = par;
        			 }
        		}
    		}
    		if (((QName) smc.get(MessageContext.WSDL_SERVICE)) != null) {
    			par = ((QName) smc.get(MessageContext.WSDL_SERVICE)).getLocalPart();
        		if (par != null && !par.isEmpty()) {
        			logMessage += "\n    ServiceName: " + par;
        		}
    		}
    		
    		par = "{}";
    		if ((isOutbound && isRequestor) || (!isOutbound && !isRequestor)) {
    			if (((TreeMap) smc.get(MessageContext.HTTP_REQUEST_HEADERS)) != null) {
        			par = ((TreeMap) smc.get(MessageContext.HTTP_REQUEST_HEADERS)).toString();
    			} else if (((TreeMap) smc.get(Message.PROTOCOL_HEADERS)) != null) {
    				par = ((TreeMap) smc.get(Message.PROTOCOL_HEADERS)).toString();
    			}
    		} else {
    			if (((TreeMap) smc.get(MessageContext.HTTP_RESPONSE_HEADERS)) != null) {
    				par = ((TreeMap) smc.get(MessageContext.HTTP_RESPONSE_HEADERS)).toString();    				
    			}
    		}
    		if (par != null && !par.isEmpty()) {
    			logMessage += "\n    Headers: " + par;
    		}
    		
    		ByteArrayOutputStream bos = new ByteArrayOutputStream();
        	message.writeTo(bos);
    		logMessage += "\n    Payload: " + new String(bos.toByteArray());
    		
    		if (serviceId != null && !serviceId.isEmpty()) {
    			LogFactory.getLog("org.apache.cxf.services." + serviceId + "." + typeMessage).info(logMessage + "\n");
    		} else {
    			log.info(logMessage + "\n");
    		}
    	} catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
}