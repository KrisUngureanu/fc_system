package com.cifs.or2.server.plugins;

import com.cifs.or2.server.orlang.SrvPlugin;
import com.cifs.or2.server.Session;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.Constants;
import org.apache.axis2.AxisFault;
import org.apache.axis2.transport.http.HTTPConstants;
import javax.xml.namespace.QName;

import java.util.*;

public class APARMPlugin implements SrvPlugin {
    private Session s;

    public APARMPlugin() {
    }

    public Session getSession() {
        return s;
    }

    public void setSession(Session session) {
        this.s = session;
    }

    public void sleep(Number ms) {
        try {
            Thread.sleep(ms.longValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String add(String address, String user, String author, String number, String url, String descRu, String descKz,
    		String date, String execDate, String status, String note) {
        String res = null;
        try {
            OMElement method = createAddRequest(user, author, number, url, descRu, descKz, date, execDate, status, note);

            SOAPEnvelope se = creatSOAPEnvelope(method.getNamespace());
            SOAPBody sb = se.getBody();
            sb.addChild(method);

            System.out.println(se.toString());
            
            SOAPEnvelope response = send(se, "add", address);

            System.out.println("====================== BEGIN RESPONSE =========================");
            System.out.println(response.toString());
            System.out.println("======================= END RESPONSE ==========================");

            sb = response.getBody();
            
            OMElement resp = null;
            Iterator it = sb.getChildrenWithLocalName("addResponse");
            if (it != null && it.hasNext()) {
            	resp = (OMElement)it.next();
                System.out.println("====================== BEGIN addResponse =========================");
                System.out.println(resp.toString());
                System.out.println("======================= END addResponse ==========================");
                it = resp.getChildrenWithLocalName("addReturn");
                if (it != null && it.hasNext()) {
                	resp = (OMElement)it.next();
                    System.out.println("====================== BEGIN addReturn =========================");
                    System.out.println(resp.toString());
                    System.out.println("======================= END addReturn ==========================");
                    it = resp.getChildrenWithLocalName("processed");
                    if (it != null && it.hasNext()) {
                        it = resp.getChildrenWithLocalName("href");
                        if (it != null && it.hasNext()) {
                        	resp = (OMElement)it.next();
                            res = resp.getText();
                            System.out.println("href = " + res);
                        }
                    } else {
                    	String ref = resp.getAttributeValue(new QName("", "href"));
                        System.out.println("ref = " + ref);
                        it = sb.getChildrenWithLocalName("multiRef");
                        if (it != null && it.hasNext()) {
                        	resp = (OMElement)it.next();
                            System.out.println("====================== BEGIN multiRef =========================");
                            System.out.println(resp.toString());
                            System.out.println("======================= END multiRef ==========================");
                            it = resp.getChildrenWithLocalName("processed");
                            if (it != null && it.hasNext()) {
                                it = resp.getChildrenWithLocalName("href");
                                if (it != null && it.hasNext()) {
                                	resp = (OMElement)it.next();
                                    res = resp.getText();
                                    System.out.println("href = " + res);
                                }
                            }
                        }
                    }
                }
            }
            res = resp.getText();
        } catch (AxisFault af) {
            af.printStackTrace();
        }
        return res;
    }

    public String deleteByHREF(String address, String href) {
        String res = null;
        try {
            OMElement method = createDeleteRequest(href);

            SOAPEnvelope se = creatSOAPEnvelope(method.getNamespace());
            SOAPBody sb = se.getBody();
            sb.addChild(method);

            System.out.println(se.toString());
            
            SOAPEnvelope response = send(se, "deleteByHREF", address);

            System.out.println("====================== BEGIN RESPONSE =========================");
            System.out.println(response.toString());
            System.out.println("======================= END RESPONSE ==========================");

            sb = response.getBody();
            
            OMElement resp = null;
            Iterator it = sb.getChildrenWithLocalName("deleteByHREFResponse");
            if (it != null && it.hasNext()) {
            	resp = (OMElement)it.next();
                it = resp.getChildrenWithLocalName("deleteByHREFReturn");
                if (it != null && it.hasNext()) {
                	resp = (OMElement)it.next();
                    it = resp.getChildrenWithLocalName("processed");
                    if (it != null && it.hasNext()) {
                        it = resp.getChildrenWithLocalName("isDeleted");
                        if (it != null && it.hasNext()) {
                        	resp = (OMElement)it.next();
                            res = resp.getText();
                        }
                    } else {
                    	String ref = resp.getAttributeValue(new QName("", "href"));
                        it = sb.getChildrenWithLocalName("multiRef");
                        if (it != null && it.hasNext()) {
                        	resp = (OMElement)it.next();
                            it = resp.getChildrenWithLocalName("processed");
                            if (it != null && it.hasNext()) {
                                it = resp.getChildrenWithLocalName("isDeleted");
                                if (it != null && it.hasNext()) {
                                	resp = (OMElement)it.next();
                                    res = resp.getText();
                                }
                            }
                        }
                    }
                }
            }
            res = resp.getText();
        } catch (AxisFault af) {
            af.printStackTrace();
        }
        return res;
    }

    private SOAPEnvelope send(SOAPEnvelope request, String action, String address) throws AxisFault {
        ServiceClient sender = new ServiceClient();
        OperationClient oc = sender.createClient(ServiceClient.ANON_OUT_IN_OP);

        MessageContext outMsgCtx = new MessageContext();
        Options opts = outMsgCtx.getOptions();
        //setting properties into option
        opts.setTo(new EndpointReference(address));
        opts.setTransportInProtocol(Constants.TRANSPORT_HTTP);
        opts.setAction(action);
        opts.setProperty(HTTPConstants.CHUNKED, "false");
        outMsgCtx.setEnvelope(request);

        oc.addMessageContext(outMsgCtx);
        oc.execute(true);

        //pass message label as method argument
        MessageContext inMsgtCtx = oc.getMessageContext("In");

        return inMsgtCtx.getEnvelope();
    }

    public OMElement createAddRequest(String user, String author, String number, String url, String descRu, String descKz,
    		String date, String execDate, String status, String note) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        String prefix = "urn";
        OMNamespace omNs = fac.createOMNamespace("urn:main.service.esedo", prefix);
        OMNamespace omNs2 = fac.createOMNamespace("urn:request.service.esedo", prefix);
        OMNamespace soapEnvNs = fac.createOMNamespace("http://schemas.xmlsoap.org/soap/envelope/", "soapenv");
        OMNamespace xsiNs = fac.createOMNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");

        OMElement method = fac.createOMElement("add", omNs);
        OMAttribute enc = fac.createOMAttribute("encodingStyle", soapEnvNs, "http://schemas.xmlsoap.org/soap/encoding/");
        method.addAttribute(enc);

        OMElement pars = fac.createOMElement("parametersAdd", null);
        pars.declareNamespace(omNs2);
        pars.addAttribute("type", "urn:ParametersAdd", xsiNs);

        OMElement tmp = fac.createOMElement("user", null);
        tmp.addChild(fac.createOMText(tmp, user));
        tmp.addAttribute("type", "xsd:string", xsiNs);
        pars.addChild(tmp);

        tmp = fac.createOMElement("author", null);
        tmp.addChild(fac.createOMText(tmp, author));
        tmp.addAttribute("type", "xsd:string", xsiNs);
        pars.addChild(tmp);

        tmp = fac.createOMElement("number", null);
        tmp.addChild(fac.createOMText(tmp, number));
        tmp.addAttribute("type", "xsd:string", xsiNs);
        pars.addChild(tmp);

        tmp = fac.createOMElement("url", null);
        tmp.addChild(fac.createOMText(tmp, url));
        tmp.addAttribute("type", "xsd:string", xsiNs);
        pars.addChild(tmp);

        tmp = fac.createOMElement("descriptionru", null);
        tmp.addChild(fac.createOMText(tmp, descRu));
        tmp.addAttribute("type", "xsd:string", xsiNs);
        pars.addChild(tmp);

        tmp = fac.createOMElement("descriptionkz", null);
        tmp.addChild(fac.createOMText(tmp, descKz));
        tmp.addAttribute("type", "xsd:string", xsiNs);
        pars.addChild(tmp);

        tmp = fac.createOMElement("date", null);
        tmp.addChild(fac.createOMText(tmp, date));
        tmp.addAttribute("type", "xsd:string", xsiNs);
        pars.addChild(tmp);

        tmp = fac.createOMElement("executiondate", null);
        tmp.addChild(fac.createOMText(tmp, execDate));
        tmp.addAttribute("type", "xsd:string", xsiNs);
        pars.addChild(tmp);

        tmp = fac.createOMElement("status", null);
        tmp.addChild(fac.createOMText(tmp, status));
        tmp.addAttribute("type", "xsd:string", xsiNs);
        pars.addChild(tmp);

        if (note != null) {
	        tmp = fac.createOMElement("note", null);
	        tmp.addChild(fac.createOMText(tmp, note));
	        tmp.addAttribute("type", "xsd:string", xsiNs);
	        pars.addChild(tmp);
	        
        }

        method.addChild(pars);

        return method;
    }

    public OMElement createDeleteRequest(String href) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        String prefix = "urn";
        OMNamespace omNs = fac.createOMNamespace("urn:main.service.esedo", prefix);
        OMNamespace omNs2 = fac.createOMNamespace("urn:request.service.esedo", prefix);
        OMNamespace soapEnvNs = fac.createOMNamespace("http://schemas.xmlsoap.org/soap/envelope/", "soapenv");
        OMNamespace xsiNs = fac.createOMNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");

        OMElement method = fac.createOMElement("deleteByHREF", omNs);
        OMAttribute enc = fac.createOMAttribute("encodingStyle", soapEnvNs, "http://schemas.xmlsoap.org/soap/encoding/");
        method.addAttribute(enc);

        OMElement pars = fac.createOMElement("parametersDelete", null);
        pars.declareNamespace(omNs2);
        pars.addAttribute("type", "urn:ParametersDelete", xsiNs);

        OMElement tmp = fac.createOMElement("href", null);
        tmp.addChild(fac.createOMText(tmp, href));
        tmp.addAttribute("type", "xsd:string", xsiNs);
        pars.addChild(tmp);

        method.addChild(pars);

        return method;
    }

    public static SOAPEnvelope creatSOAPEnvelope(OMNamespace ns) {
        SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        SOAPEnvelope envelope = fac.getDefaultEnvelope();

        OMNamespace soapEnvNs = fac.createOMNamespace("http://schemas.xmlsoap.org/soap/envelope/", "soapenv");
        OMNamespace xsdNs = fac.createOMNamespace("http://www.w3.org/2001/XMLSchema", "xsd");
        OMNamespace xsiNs = fac.createOMNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");

        envelope.declareNamespace(soapEnvNs);
        envelope.declareNamespace(xsdNs);
        envelope.declareNamespace(xsiNs);
        envelope.declareNamespace(ns);

        return envelope;
    }

    public static void main(String[] args) {
//        System.out.println(String.format("%018.2f", 1212412.3));
        System.out.println("===============================================");
        System.out.println("===================GetVersion==================");
        System.out.println("===============================================");

        APARMPlugin p = new APARMPlugin();
        String res = p.add("http://axis.apache.org/axis2/java/core", "user", "author", "number", "url", "descRu", "descKz", "date", "execDate", "status", "note");

        System.out.println("res --> " + res);

    }
}
