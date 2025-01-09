package kz.tamur.soap.handler;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class WSFcSecurityHandler implements SOAPHandler<SOAPMessageContext> {

	private static String fcUserId = System.getProperty("fcUserId");

	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
	       Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

           SOAPMessage message = smc.getMessage();
           if (outboundProperty.booleanValue() && fcUserId!=null && !"".equals(fcUserId)) {
        	    // Getting SOAP headers
				try {
					SOAPMessage soapMsg = smc.getMessage();
					SOAPEnvelope envelope = soapMsg.getSOAPPart().getEnvelope();
					SOAPHeader header = envelope.getHeader();
					if (header == null)
						header = envelope.addHeader();

					SOAPElement	wsun = header.addChildElement(new QName( "http://report.chdb.scb.kz", "userId", "pre"));
					wsun.setValue(fcUserId);
					wsun.setElementQName(new QName("", "userId"));
				} catch (SOAPException e) {
					e.printStackTrace();
				}
		   }
           return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close(MessageContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}
}
