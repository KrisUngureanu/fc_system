package com.cifs.or2.server.plugins;

import com.cifs.or2.server.orlang.SrvPlugin;

import kz.gov.pki.kalkan.util.encoders.Base64;
import kz.tamur.util.Funcs;

import com.cifs.or2.server.Session;
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
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import javax.xml.namespace.QName;

import java.util.*;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: erik-b
 * Date: 04.04.2009
 * Time: 15:33:04
 * To change this template use File | Settings | File Templates.
 */
public class CBPlugin  implements SrvPlugin {
    private Session s;
    private EndpointReference targetEPR;// =
        //new EndpointReference("http://www-test2.1cb.kz/DataPumpTest/DataPumpService.asmx");
    private String userName;// = "KDBuser01";
    private String pd;// = "KDBuser01";

    public CBPlugin() {
        Properties ps= new Properties();
        try {
            String t_props_file_ = Funcs.normalizeInput(System.getProperty("TPropsFile", "transport.properties"));
            File cf = Funcs.getCanonicalFile(t_props_file_);
            if (cf.exists()) {
                ps.load(new FileInputStream(cf));
                if (ps.getProperty("proxyHost") != null)
                    System.getProperties().put("http.proxyHost", ps.getProperty("proxyHost"));
                if (ps.getProperty("proxyPort") != null)
                    System.getProperties().put("http.proxyPort", ps.getProperty("proxyPort"));
                if (ps.getProperty("proxyUser") != null)
                    System.getProperties().put("http.proxyUser", ps.getProperty("proxyUser"));
                if (ps.getProperty("proxyPassword") != null)
                    System.getProperties().put("http.proxyPassword", ps.getProperty("proxyPassword"));

                if (ps.getProperty("pcbUser") != null)
                    userName = ps.getProperty("pcbUser");
                if (ps.getProperty("pcbPassword") != null)
                    pd = ps.getProperty("pcbPassword");
                if (ps.getProperty("pcbAddress") != null)
                    targetEPR = new EndpointReference(ps.getProperty("pcbAddress"));
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public Session getSession() {
        return s;
    }

    public void setUser(String name, String pass) {
        this.userName = name;
        this.pd = pass;
    }

    public void setAddress(String address) {
        this.targetEPR = new EndpointReference(address);
    }

    public void sleep(Number ms) {
        try {
            Thread.sleep(ms.longValue());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setSession(Session session) {
        this.s = session;
    }

    public Map<String, String> getVersion() {
        Map<String, String> res = new HashMap<String, String>();
        try {
            OMElement method = createGetVersionRequest();

            SOAPEnvelope se = creatSOAPEnvelope();
            SOAPBody sb = se.getBody();
            sb.addChild(method);

            SOAPEnvelope response = send(se, "https://ws.creditinfo.com/GetVersion");

            sb = response.getBody();

            OMElement resp = sb.getFirstElement();
            resp = resp.getFirstElement();

            res.put("GetVersionResult", resp.getText());

        } catch (AxisFault af) {
            af.printStackTrace();
            res.put("error", af.getReason());
        }
        return res;
    }

    public Map<String, Object> uploadZippedData2(Element xml, Number schemaId) {
        Map<String, Object> res = new HashMap<String, Object>();
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            XMLOutputter out = new XMLOutputter();
            out.getFormat().setEncoding("UTF-8");
            out.output(xml, os);
            os.close();
            byte[] data = os.toByteArray();

            os = new ByteArrayOutputStream();
            ZipOutputStream zos = new ZipOutputStream(os);
            zos.putNextEntry(new ZipEntry("some.xml"));
            zos.write(data);
            zos.close();
            os.close();
            data = os.toByteArray();
            String base64data = new String(Base64.encode(data), "UTF-8");

            OMElement method = createUploadZippedData2Request(base64data, schemaId.intValue());

            OMElement header = creatSOAPHeader();

            SOAPEnvelope se = creatSOAPEnvelope();
            SOAPBody sb = se.getBody();
            sb.addChild(method);
            se.getHeader().addChild(header);

            SOAPEnvelope response = send(se, "https://ws.creditinfo.com/UploadZippedData2");

            sb = response.getBody();

            OMElement resp = sb.getFirstElement();                              //UploadZippedData2Response
            resp = resp.getFirstElement();                                      //UploadZippedData2Result
            resp = resp.getFirstElement();                                      //CigResult
            resp = resp.getFirstChildWithName(new QName("", "Result"));
            resp = resp.getFirstElement();                                      //Batch
            if (resp != null) {
                String tmp = resp.getAttributeValue(new QName("", "Id"));
                if (tmp != null) res.put("BatchId", tmp);
                tmp = resp.getAttributeValue(new QName("", "StatusId"));
                if (tmp != null) res.put("BatchStatusId", tmp);
                tmp = resp.getAttributeValue(new QName("", "StatusName"));
                if (tmp != null) res.put("BatchStatusName", tmp);

                Iterator it = resp.getChildrenWithLocalName("Message");                                  //Message
                if (it != null && it.hasNext()) {
                    List<Map> messages = new ArrayList<Map>();
                    while (it.hasNext()) {
                        resp = (OMElement)it.next();
                        Map<String, String> messageMap = new HashMap<String, String>();
                        tmp = resp.getAttributeValue(new QName("", "TypeId"));
                        if (tmp != null) messageMap.put("MessageTypeId", tmp);
                        tmp = resp.getAttributeValue(new QName("", "TypeName"));
                        if (tmp != null) messageMap.put("MessageTypeName", tmp);
                        tmp = resp.getAttributeValue(new QName("", "GroupId"));
                        if (tmp != null) messageMap.put("MessageGroupId", tmp);
                        tmp = resp.getAttributeValue(new QName("", "GroupName"));
                        if (tmp != null) messageMap.put("MessageGroupName", tmp);

                        OMElement e = resp.getFirstChildWithName(new QName("", "Description"));
                        if (e != null) messageMap.put("MessageDescription", e.getText());
                        e = resp.getFirstChildWithName(new QName("", "Values"));
                        if (e != null) messageMap.put("MessageValues", e.getText());

                        messages.add(messageMap);
                    }
                    res.put("messages", messages);
                }
            }

        } catch (AxisFault af) {
            af.printStackTrace();
            res.put("error", af.getReason());
        } catch (Exception e) {
            e.printStackTrace();
            res.put("error", e.getMessage());
        }
        return res;
    }

    public Map<String, Object> getBatchStatus2(String batchId) {
        Map<String, Object> res = new HashMap<String, Object>();
        try {
            OMElement method = createGetBatchStatus2Request(batchId);

            OMElement header = creatSOAPHeader();

            SOAPEnvelope se = creatSOAPEnvelope();
            SOAPBody sb = se.getBody();
            sb.addChild(method);
            se.getHeader().addChild(header);

            SOAPEnvelope response = send(se, "https://ws.creditinfo.com/GetBatchStatus2");

            sb = response.getBody();

            OMElement resp = sb.getFirstElement();                              //UploadZippedData2Response
            resp = resp.getFirstElement();                                      //UploadZippedData2Result
            resp = resp.getFirstElement();                                      //CigResult
            resp = resp.getFirstChildWithName(new QName("", "Result"));
            resp = resp.getFirstElement();                                      //Batch
            if (resp != null) {
                String tmp = resp.getAttributeValue(new QName("", "Id"));
                if (tmp != null) res.put("BatchId", tmp);
                tmp = resp.getAttributeValue(new QName("", "StatusId"));
                if (tmp != null) res.put("BatchStatusId", tmp);
                tmp = resp.getAttributeValue(new QName("", "StatusName"));
                if (tmp != null) res.put("BatchStatusName", tmp);

                Iterator it = resp.getChildrenWithLocalName("Message");                                  //Message
                if (it != null && it.hasNext()) {
                    List<Map> messages = new ArrayList<Map>();
                    while (it.hasNext()) {
                        resp = (OMElement)it.next();
                        Map<String, String> messageMap = new HashMap<String, String>();
                        tmp = resp.getAttributeValue(new QName("", "TypeId"));
                        if (tmp != null) messageMap.put("MessageTypeId", tmp);
                        tmp = resp.getAttributeValue(new QName("", "TypeName"));
                        if (tmp != null) messageMap.put("MessageTypeName", tmp);
                        tmp = resp.getAttributeValue(new QName("", "GroupId"));
                        if (tmp != null) messageMap.put("MessageGroupId", tmp);
                        tmp = resp.getAttributeValue(new QName("", "GroupName"));
                        if (tmp != null) messageMap.put("MessageGroupName", tmp);

                        OMElement e = resp.getFirstChildWithName(new QName("", "Description"));
                        if (e != null) messageMap.put("MessageDescription", e.getText());
                        e = resp.getFirstChildWithName(new QName("", "Values"));
                        if (e != null) messageMap.put("MessageValues", e.getText());

                        messages.add(messageMap);
                    }
                    res.put("messages", messages);
                }
            }

        } catch (AxisFault af) {
            af.printStackTrace();
            res.put("error", af.getReason());
        } catch (Exception e) {
            e.printStackTrace();
            res.put("error", e.getMessage());
        }
        return res;
    }

    private SOAPEnvelope send(SOAPEnvelope request, String action) throws AxisFault {
        ServiceClient sender = new ServiceClient();
        OperationClient oc = sender.createClient(ServiceClient.ANON_OUT_IN_OP);

        MessageContext outMsgCtx = new MessageContext();
        Options opts = outMsgCtx.getOptions();
        //setting properties into option
        opts.setTo(targetEPR);
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

    public OMElement createGetVersionRequest() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        String prefix = "ns2";
        OMNamespace omNs = fac.createOMNamespace("https://ws.creditinfo.com", prefix);

        OMElement method = fac.createOMElement("GetVersion", omNs);

        OMElement tmp = fac.createOMElement("username", omNs);
        tmp.addChild(fac.createOMText(tmp, userName));
        method.addChild(tmp);

        tmp = fac.createOMElement("password", omNs);
        tmp.addChild(fac.createOMText(tmp, pd));
        method.addChild(tmp);

        return method;
    }

    public OMElement createUploadZippedData2Request(String data, int schemaId) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        String prefix = "ns2";
        OMNamespace omNs = fac.createOMNamespace("https://ws.creditinfo.com", prefix);

        OMElement method = fac.createOMElement("UploadZippedData2", omNs);

        OMElement tmp = fac.createOMElement("zippedXML", omNs);
        tmp.addChild(fac.createOMText(tmp, data));
        method.addChild(tmp);

        tmp = fac.createOMElement("schemaId", omNs);
        tmp.addChild(fac.createOMText(tmp, Integer.toString(schemaId)));
        method.addChild(tmp);

        return method;
    }

    public OMElement createGetBatchStatus2Request(String batchId) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        String prefix = "ns2";
        OMNamespace omNs = fac.createOMNamespace("https://ws.creditinfo.com", prefix);

        OMElement method = fac.createOMElement("GetBatchStatus2", omNs);

        OMElement tmp = fac.createOMElement("batchId", omNs);
        tmp.addChild(fac.createOMText(tmp, batchId));
        method.addChild(tmp);

        return method;
    }

    public static SOAPEnvelope creatSOAPEnvelope() {
        SOAPFactory fac = OMAbstractFactory.getSOAP11Factory();
        SOAPEnvelope envelope = fac.getDefaultEnvelope();

        OMNamespace soapEnvNs = fac.createOMNamespace("http://schemas.xmlsoap.org/soap/envelope/", "soapenv");
        OMNamespace xsdNs = fac.createOMNamespace("http://www.w3.org/2001/XMLSchema", "xsd");
        OMNamespace xsiNs = fac.createOMNamespace("http://www.w3.org/2001/XMLSchema-instance", "xsi");

        envelope.declareNamespace(soapEnvNs);
        envelope.declareNamespace(xsdNs);
        envelope.declareNamespace(xsiNs);

        return envelope;
    }

    public OMElement creatSOAPHeader() {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        String prefix = "ns2";
        OMNamespace omNs = fac.createOMNamespace("https://ws.creditinfo.com", prefix);

        OMElement cigHeader = fac.createOMElement("CigWsHeader", omNs);

        OMElement tmp = fac.createOMElement("UserName", omNs);
        tmp.addChild(fac.createOMText(tmp, userName));
        cigHeader.addChild(tmp);

        tmp = fac.createOMElement("Password", omNs);
        tmp.addChild(fac.createOMText(tmp, pd));
        cigHeader.addChild(tmp);

        return cigHeader;
    }

    public static void main(String[] args) {
//        System.out.println(String.format("%018.2f", 1212412.3));
        System.out.println("===============================================");
        System.out.println("===================GetVersion==================");
        System.out.println("===============================================");

        CBPlugin p = new CBPlugin();
        Map res = p.getVersion();
        for (Iterator it = res.keySet().iterator(); it.hasNext(); ) {
            String key = (String)it.next();
            String value = (String)res.get(key);
            System.out.println(key + " --> " + value);
        }

        System.out.println("===============================================");
        System.out.println("===================uploadZippedData2===========");
        System.out.println("===============================================");
        Element xml = null;
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new File("D:/WORK/1CB/gen1.xml"));
            xml = doc.getRootElement();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //res = p.uploadZippedData2(xml, 3);

        for (Iterator it = res.keySet().iterator(); it.hasNext(); ) {
            String key = (String)it.next();
            Object value = res.get(key);
            System.out.println(key + " --> " + value);
        }

        String batchStatusId = (String)res.get("BatchStatusId");
        while (true) {//"519".equals(batchStatusId)) {
            p.sleep(8000);
            String batchId = "9714";//(String)res.get("BatchId");

            System.out.println("===============================================");
            System.out.println("===================getBatchStatus==============");
            System.out.println("===============================================");
            res = p.getBatchStatus2(batchId);

            for (Iterator it = res.keySet().iterator(); it.hasNext(); ) {
                String key = (String)it.next();
                Object value = res.get(key);
                System.out.println(key + " --> " + value);
            }
            batchStatusId = (String)res.get("BatchStatusId");
        }
    }
}
