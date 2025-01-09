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

import org.apache.cxf.endpoint.ClientImpl;
import org.apache.cxf.transport.http.asyncclient.AsyncHTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

public class WSFcbSecurityHandler implements SOAPHandler<SOAPMessageContext> {

	private static String fcbUserName = System.getProperty("fcbUserName");
	private static String fcbUserPwd = System.getProperty("fcbUserPwd");

	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
	       Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
	       fcbUserName = System.getProperty("fcbUserName");
	       fcbUserPwd = System.getProperty("fcbUserPwd");//чтобы можно было поменять пароль без перезагрузки сервера приложения
           if (outboundProperty.booleanValue() && fcbUserName!=null && !"".equals(fcbUserName)) {
        	    // Getting SOAP headers
				try {
					SOAPMessage soapMsg = smc.getMessage();
					SOAPEnvelope envelope = soapMsg.getSOAPPart().getEnvelope();
					SOAPHeader header = envelope.getHeader();
					if (header == null)
						header = envelope.addHeader();
					String pre=header.getPrefix();
					String ns=header.getNamespaceURI();
					SOAPElement	cigws=header.addChildElement(new QName( ns, "CigWsHeader",pre));
					SOAPElement	wsun = cigws.addChildElement(new QName( ns, "UserName",pre));
					SOAPElement	wspwd = cigws.addChildElement(new QName( ns, "Password",pre));
					SOAPElement	wslng = cigws.addChildElement(new QName( ns, "Culture",pre));
					SOAPElement	wsver = cigws.addChildElement(new QName( ns, "Version",pre));
					wsun.setValue(fcbUserName);
					wsun.setElementQName(new QName(ns, "UserName",pre));
					wspwd.setValue(fcbUserPwd);
					wspwd.setElementQName(new QName(ns, "Password",pre));
					wslng.setValue("ru-RU");
					wslng.setElementQName(new QName(ns, "Culture",pre));
					wsver.setValue("DataPump version 3.1.0.0");
					wsver.setElementQName(new QName(ns, "Version",pre));
					try {
						// Отключения chunking в Apache CXF
						ClientImpl cxfClient = (ClientImpl) smc.get("org.apache.cxf.transport.MessageObserver");
						AsyncHTTPConduit conduit = (AsyncHTTPConduit)cxfClient.getConduit();
						HTTPClientPolicy policy = conduit.getClient();
						if (policy == null) {
							policy = new HTTPClientPolicy();
							conduit.setClient(policy);
						}
						policy.setAllowChunking(false);
					} catch (Exception e) {
						e.printStackTrace();
					}
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
