package kz.tamur.test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import kz.tamur.comps.Constants;
import kz.tamur.guidesigner.ws.WSHelper;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.util.Funcs;
import kz.tamur.util.crypto.XmlUtil;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.cifs.or2.kernel.Date;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.Time;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvOrLang;

public class WSServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private String datasource;
    private String user;
    private String pd;
    private boolean printerStatus = false;
    private static final Log log = LogFactory.getLog(WSServlet.class);
    
    // Классы
    private static KrnClass webServiceClass;
    private static KrnClass bindingClass;
    private static KrnClass portTypeClass;
    private static KrnClass wsdlOperationClass;
    private static KrnClass messageClass;
    private static KrnClass wsdlMessageClass;
    private static KrnClass wsdlPartClass;
    private static KrnClass qNameClass;
    private static KrnClass wsUserCls;
    private static KrnClass wsMethodCls;
    
    // Атрибуты
    private static KrnAttribute bindingsAttr;
    private static KrnAttribute portTypeAttr;
    private static KrnAttribute operationsAttr;
    private static KrnAttribute inputMessageAttr;
    private static KrnAttribute outputMessageAttr;
    private static KrnAttribute classNameAttr;
    private static KrnAttribute partsAttr;
    private static KrnAttribute elementAttr;
    private static KrnAttribute localNameAttr;
    private static KrnAttribute namespaceAttr;
    private static KrnAttribute loginAttr;
    private static KrnAttribute pdAttr;
    private static KrnAttribute serviceIdAttr;
    private static KrnAttribute servicesAttr;
    private static KrnAttribute methodOr3Attr;
    private static KrnAttribute packageNameAttr;
    
    private static final Map<Long, String> typesMap;
    static
    {
        typesMap = new HashMap<Long, String>();
        typesMap.put((long) 1, "string");
        typesMap.put((long) 2, "int");
        typesMap.put((long) 3, "dateTime");
        typesMap.put((long) 4, "date");
        typesMap.put((long) 5, "boolean");
    }

    private static final int MAX_NODES_COUNT = 1000000;
    
    public WSServlet() {
        super();
    }
    
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        datasource = servletConfig.getInitParameter("dataSourceName");
        user = servletConfig.getInitParameter("user");
        pd = servletConfig.getInitParameter("password");
        printerStatus = "1".equals(servletConfig.getInitParameter("printerStatus"));
        
        Session session = null;
        try {
            session = getSession();
            
            WSHelper.initClasses(session);

            // Инициализация классов
            webServiceClass = session.getClassByName("WebService");
            bindingClass = session.getClassByName("Binding");
            portTypeClass = session.getClassByName("PortType");
            wsdlOperationClass = session.getClassByName("WSDLOperation");
            messageClass = session.getClassByName("WSDLMessage");
            wsdlMessageClass = session.getClassByName("WSDLMessage");
            wsdlPartClass = session.getClassByName("WSDLPart");
            qNameClass = session.getClassByName("QName");
            wsUserCls = session.getClassByName("WSUser");
            wsMethodCls = session.getClassByName("WSMethod");

            // Инициализация атрибутов
            bindingsAttr = session.getAttributeByName(webServiceClass, "bindings");
            serviceIdAttr = session.getAttributeByName(webServiceClass, "serviceId");
            methodOr3Attr = session.getAttributeByName(webServiceClass, "methodOr3");
            packageNameAttr = session.getAttributeByName(webServiceClass, "packageName");
            portTypeAttr = session.getAttributeByName(bindingClass, "portType");
            operationsAttr = session.getAttributeByName(portTypeClass, "operations");
            inputMessageAttr = session.getAttributeByName(wsdlOperationClass, "inputMessage");
            outputMessageAttr = session.getAttributeByName(wsdlOperationClass, "outputMessage");
            classNameAttr = session.getAttributeByName(messageClass, "className");
            partsAttr = session.getAttributeByName(wsdlMessageClass, "parts");
            elementAttr = session.getAttributeByName(wsdlPartClass, "element");
            localNameAttr = session.getAttributeByName(qNameClass, "localName");
            namespaceAttr = session.getAttributeByName(qNameClass, "namespace");
            loginAttr = session.getAttributeByName(wsUserCls, "login");
            pdAttr = session.getAttributeByName(wsUserCls, "password");
            servicesAttr = session.getAttributeByName(wsUserCls, "services");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.release();
            }
        }
    }
    
    private Session getSession() throws Exception {
        return SrvUtils.getSession(datasource, user, pd);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String login = Funcs.getParameter(request, "login");
        if (login == null || login.equals("")) {
            out.print(createErrorResponseMessage("Не указан логин!"));
            return;
        }
        String pd = Funcs.getParameter(request, "password");
        if (pd == null || pd.equals("")) {
            out.print(createErrorResponseMessage("Не указан пароль!"));
            return;
        }
        String serviceId = Funcs.getParameter(request, "serviceId");
        if (serviceId == null || serviceId.equals("")) {
            out.print(createErrorResponseMessage("Не указан идентификатор сервиса!"));
            return;
        }
        Session session = null;
        try {
            session = getSession();
            KrnObject[] userObjs = session.getClassObjects(wsUserCls, new long[] {}, 0);
            if (userObjs == null || userObjs.length == 0) {
                out.print(createErrorResponseMessage("Пользователь не зарегистрирован!"));
                return;
            } else {
                KrnObject userObj = null;
                for (int i = 0; i < userObjs.length; i++) {
                    if (login.equals(session.getStringsSingular(userObjs[i].id, loginAttr.id, 0, false, false)) && pd.equals(session.getStringsSingular(userObjs[i].id, pdAttr.id, 0, false, false))) {
                        userObj = userObjs[i];
                        break;
                    }
                }
                if (userObj == null) {
                    out.print(createErrorResponseMessage("Неверный логин или пароль!"));
                    return;
                }
                KrnObject[] serviceObjs = session.getObjectsByAttribute(webServiceClass.id, serviceIdAttr.id, 0, 0, serviceId, 0);
                if (serviceObjs == null || serviceObjs.length == 0) {
                    out.print(createErrorResponseMessage("По заданному идентификатору (" + StringEscapeUtils.escapeXml(serviceId) + ") сервис не найден!"));
                    return;
                } else {
                    KrnObject webServiceObj = serviceObjs[0];
                    KrnObject[]  userServices = session.getObjects(userObj.id, servicesAttr.id, new long[] {}, 0);
                    if (userServices == null || userServices.length == 0) {
                        out.print(createErrorResponseMessage("Для указанного пользователя услуга не доступна!"));
                        return;
                    } else {
                        if (!Arrays.asList(userServices).contains(webServiceObj)) {
                            out.print(createErrorResponseMessage("Для указанного пользователя услуга не доступна!"));
                            return;
                        } else {
                            // Выдача wsdl
                            String wsdl = request.getParameter("wsdl");
                            if (wsdl != null) {
                                String requestXML = getRequestXML(webServiceObj, session);
                                out.print(requestXML);
                                return;
                            }
                            // Входящий запрос
                            Document document = getContent(request);
                            if (document != null) {
                                //System.out.println(document.getNamespaceURI());
                                List<String> rootElementNameInfo = getRootElement(webServiceObj, 0, session);
                                if (rootElementNameInfo != null) {
                                    Node envelopeNode = document.getFirstChild();
                                    Node bodyNode = envelopeNode.getFirstChild();
                                    Node rootRequestElement = bodyNode.getFirstChild();        // Корневой элемент

                                    KrnClass requestClass = getRootClass(webServiceObj, 0, session);
                                    if (requestClass != null) {
                                        String packageName = Funcs.sanitizeXml(session.getStringsSingular(webServiceObj.id, packageNameAttr.id, 0, false, false));
                                        KrnObject requestObject = convertXMLToKrnObject(rootRequestElement, requestClass, packageName, session);
                                        if (requestObject != null) {
                                            String methodOr3AttrName = session.getStringsSingular(webServiceObj.id, methodOr3Attr.id, 0, false, false);
                                            if (!methodOr3AttrName.equals("")) {
                                                Context ctx = new Context(new long[0], 0, 0);
                                                ctx.langId = 0;
                                                ctx.trId = 0;
                                                session.setContext(ctx);
                                                List<Object> args = new ArrayList<Object>();
                                                args.add(requestObject);
                                                SrvOrLang orlang = session.getSrvOrLang();
                                                KrnObject responseObject = (KrnObject) orlang.exec(wsMethodCls, wsMethodCls, methodOr3AttrName, args, new Stack<String>());
                                                KrnClass responseClass = getRootClass(webServiceObj, 1, session);
                                                String rootResponseElement = convertKrnObjectToXMLString(responseObject, responseClass, packageName, session);
                                                out.print(rootResponseElement);
                                                session.commitTransaction();
                                                return; 
                                            } else {
                                                out.print(createErrorResponseMessage("Не задан метод обработки!"));
                                                return; 
                                            }
                                        }
                                    }
                                } else {
                                    out.print(createErrorResponseMessage("Ошибка при разборе запроса!"));
                                    return; 
                                }
                            } else {
                              out.print(createErrorResponseMessage("Ошибка при разборе запроса!"));
                              return; 
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            out.print(createErrorResponseMessage("Ошибка выполнения сервиса!"));
            e.printStackTrace();
        } catch (Throwable e) {
            out.print(createErrorResponseMessage("Ошибка выполнения сервиса!"));
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.release();
            }
        }
    }
    
    private String getRequestXML(KrnObject webServiceObj, Session session) throws KrnException, SOAPException, IOException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage message = messageFactory.createMessage();
        SOAPEnvelope envelopeElement = message.getSOAPPart().getEnvelope();
        envelopeElement.removeChild(envelopeElement.getHeader());
        
        envelopeElement.addNamespaceDeclaration("SOAP-ENC", "http://schemas.xmlsoap.org/soap/encoding/");

        SOAPBody bodyElement = envelopeElement.getBody();
        
        KrnClass rootClass = getRootClass(webServiceObj, 0, session);
        if (rootClass != null) {
            KrnAttribute[] attributes = session.getAttributes(rootClass);
            Map<Long, Integer> repeats = new HashMap<Long, Integer>();
            for (int i = 0; i < attributes.length; i++) {
                if (!attributes[i].name.equals("deleting") && !attributes[i].name.equals("creating")) {
                    addElement(attributes[i], bodyElement, repeats, session);
                }
            }
        }
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        message.writeTo(out);
        String xmlString = new String(out.toByteArray());
        return xmlString;
    }
    
    private void addElement(KrnAttribute attribute, SOAPElement element, Map<Long, Integer> repeats, Session session) throws SOAPException {
        String attrName = attribute.name;
        SOAPElement child = element.addChildElement(attrName);
        long typeClsId = attribute.typeClassId;
        if (typesMap.containsKey(typeClsId)) {
            String typeClsName = typesMap.get(typeClsId);
            if (typeClsName.equals("string")) {
                child.setValue("String");
            } else if (typeClsName.equals("int")) {
                child.setValue("0");
            } else if (typeClsName.equals("time")) {
                GregorianCalendar calendar = new GregorianCalendar();
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                String value = format.format(calendar.getTime());
                value = value.substring(0, value.length() - 2) + ":" + value.substring(value.length() - 2);
                child.setValue(value);
            } else if (typeClsName.equals("date")) {
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                GregorianCalendar calendar = new GregorianCalendar();
                String value = format.format(calendar.getTime());
                child.setValue(value);
            } else if (typeClsName.equals("boolean")) {
                child.setValue("true");
            }
        } else {
            KrnClass cls = session.getClassById(typeClsId);
            if (cls != null) {
                KrnAttribute[] attributes = session.getAttributes(cls);
                for (int i = 0; i < attributes.length; i++) {
                    if (attribute.id == attributes[i].id) {
                        child.addChildElement(attributes[i].name);
                        continue;
                        /*
                        if (repeats.containsKey(attribute.id)) {
                            int count = repeats.get(attribute.id);
                            if (count < 2) {
                                repeats.put(attribute.id, count + 1);
                            } else {
                                child.addChildElement(attributes[i].name);
                                continue;
                            }
                        } else {
                            repeats.put(attribute.id, 1);
                        }*/
                    }
                    if (!attributes[i].name.equals("deleting") && !attributes[i].name.equals("creating")) {
                        addElement(attributes[i], child, repeats, session);
                    }
                }
            }
        }
    }
    
    private KrnObject convertXMLToKrnObject(Node node, KrnClass krnClass, String packageName, Session session) throws KrnException {
        KrnObject parentObject = session.createObject(krnClass, 0);
        setData("", node, parentObject, packageName, session);
        return parentObject;
    }
    
    private void setData(String prefix, Node node, KrnObject parentObject, String packageName, Session session) throws KrnException {
        String name = Funcs.sanitizeXml(node.getNodeName());
        name = name.substring(name.indexOf(":") + 1);
        String content = Funcs.sanitizeXml(node.getTextContent());
        
        if (node.hasChildNodes()) {
            Node child = node.getFirstChild();
            if (child.getNodeType() == Node.TEXT_NODE) {
                KrnClass parentCls = session.getClassById(parentObject.classId);
                KrnAttribute attr = session.getAttributeByName(parentCls, name);
                if (attr == null) return;
                long typeClsId = attr.typeClassId;
                String typeClsName = typesMap.get(typeClsId);
                try {
                    if (typeClsName.equals("string")) {
                        session.setString(parentObject.id, attr.id, 0, 0, false, content, 0);
                    } else if (typeClsName.equals("int")) {
                        Long value = new Long(content);
                        session.setLong(parentObject.id, attr.id, 0, value, 0);
                    } else if (typeClsName.equals("dateTime") || typeClsName.equals("time")) {
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        java.util.Date date = format.parse(content);
                        GregorianCalendar calendar = new GregorianCalendar();
                        calendar.setTime(date);
                        Time value = new Time((short) calendar.get(GregorianCalendar.MILLISECOND), (short) calendar.get(GregorianCalendar.SECOND), (short) calendar.get(GregorianCalendar.MINUTE), (short) calendar.get(GregorianCalendar.HOUR), (short) calendar.get(GregorianCalendar.DAY_OF_MONTH), (short) calendar.get(GregorianCalendar.MONTH), (short) calendar.get(GregorianCalendar.YEAR));
                        session.setTime(parentObject.id, attr.id, 0, value, 0);
                    } else if (typeClsName.equals("date")) {
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        java.util.Date date = format.parse(content);
                        GregorianCalendar calendar = new GregorianCalendar();
                        calendar.setTime(date);
                        Date value = new Date((short) calendar.get(GregorianCalendar.DAY_OF_MONTH), (short) calendar.get(GregorianCalendar.MONTH), (short) calendar.get(GregorianCalendar.YEAR));
                        session.setDate(parentObject.id, attr.id, 0, value, 0);
                    } else if (typeClsName.equals("boolean")) {
                        Boolean value = new Boolean(content);
                        session.setLong(parentObject.id, attr.id, 0, value ? 1 : 0, 0);
                    }
                } catch (Exception e) {
                    log.error("Ошибка при записи атрибута \"" + name + "\"  типа \"" + typeClsName + "\"");
                    e.printStackTrace();
                }
                //System.out.println(prefix + name + " --> " + content);
            } else {
                String clsName = "";
                KrnClass parentObjectCls = session.getClassById(parentObject.classId);
                KrnAttribute[] attributes = session.getAttributes(parentObjectCls);
                for (int i = 0; i < attributes.length; i++) {
                    if (name.equals(attributes[i].name)) {
                        long clsId = attributes[i].typeClassId;
                        clsName = session.getClassById(clsId).name;
                    }
                }

                KrnClass cls = session.getClassByName(clsName);
                if (cls == null) return;

                KrnObject obj = session.createObject(cls, 0);
                KrnClass parentCls = session.getClassById(parentObject.classId);
                KrnAttribute attr = session.getAttributeByName(parentCls, name);
                session.setObject(parentObject.id, attr.id, 0, obj.id, 0, true);
                //System.out.println(prefix + name);
                NodeList list = node.getChildNodes();
                
                int count = list.getLength();
                
                for (int i = 0; i < count; i++) {
                    setData(prefix + "\t", list.item(i), obj, packageName, session);
                }
            }
        } else {
            String clsName = getClassName(packageName, name, false);
            KrnClass cls = session.getClassByName(clsName);
            KrnObject obj = session.createObject(cls, 0);
            KrnClass parentCls = session.getClassById(parentObject.classId);
            KrnAttribute attr = session.getAttributeByName(parentCls, name);
            session.setObject(parentObject.id, attr.id, 0, obj.id, 0, true);
            //System.out.println(prefix + name);
        } 
    }
    
    private String getClassName(String packageName, String name, boolean isMain) {
        String className = packageName + "_" + Character.toString(name.charAt(0)).toUpperCase(Constants.OK) + name.substring(1) + (isMain ? "_Main" : "");
        return className;
    }
    
    private String createErrorResponseMessage(String errorInfo) {
        String xmlString = null;
        try {
                MessageFactory messageFactory = MessageFactory.newInstance();
                SOAPMessage responseMessage = messageFactory.createMessage();
                SOAPEnvelope envelopeElement = responseMessage.getSOAPPart().getEnvelope();
                envelopeElement.removeChild(envelopeElement.getHeader());
                
                envelopeElement.addNamespaceDeclaration("SOAP-ENC", "http://schemas.xmlsoap.org/soap/encoding/");
        
                SOAPBody bodyElement = envelopeElement.getBody();
                SOAPElement errorElement = bodyElement.addChildElement("Error");
                errorElement.setValue(errorInfo);
                
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                responseMessage.writeTo(out);
                xmlString = new String(out.toByteArray());
        } catch (Exception e) {
                xmlString = "<status>" + errorInfo + "</status>";
                e.printStackTrace();
        }
        return xmlString;
    }
    
    private String convertKrnObjectToXMLString(KrnObject krnObject, KrnClass krnClass, String packageName, Session session) throws KrnException, SOAPException, IOException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage responseMessage = messageFactory.createMessage();
        SOAPEnvelope envelopeElement = responseMessage.getSOAPPart().getEnvelope();
        envelopeElement.removeChild(envelopeElement.getHeader());
        
        envelopeElement.addNamespaceDeclaration("SOAP-ENC", "http://schemas.xmlsoap.org/soap/encoding/");

        SOAPBody bodyElement = envelopeElement.getBody();
        
        KrnAttribute[] attrs = session.getAttributes(krnClass);
        if (attrs.length > 0) {
            String attrName = attrs[0].name;
            KrnObject rootObj = session.getObjectsSingular(krnObject.id, attrs[0].id, false);
            if (rootObj != null) {
                SOAPElement element = bodyElement.addChildElement(attrName);
                getData(rootObj, element, session);
            }
            
        }
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        responseMessage.writeTo(out);
        String xmlString = new String(out.toByteArray());
        return xmlString;
    }
    
    private void getData(KrnObject krnObject, SOAPElement element, Session session) throws KrnException, SOAPException {
        KrnAttribute[] attrs = session.getAttributes(session.getClassById(krnObject.classId));
        for (int i = 0; i < attrs.length; i++) {
            KrnAttribute attr = attrs[i];
            String name = attr.name;
            if ("deleting".equals(name) || "creating".equals(name)) continue;
            long typeClsId = attr.typeClassId;
            SOAPElement child = element.addChildElement(name);
            if (typesMap.containsKey(typeClsId)) {
                String typeClsName = typesMap.get(typeClsId);
                try {
                    if (typeClsName.equals("string")) {
                        String value = session.getStringsSingular(krnObject.id, attr.id, 0, false, false);
                        child.setValue(value);
                    } else if (typeClsName.equals("int")) {
                        long value = session.getLongsSingular(krnObject, attr, false);
                        child.setValue(String.valueOf(value));
                    } else if (typeClsName.equals("dateTime")) {
                        Time[] times = session.getTimes(krnObject.id, attr.id, 0);
                        if (times != null && times.length > 0) {
                            Time time = times[0];
                            int year = time.year;
                            int month = time.month;
                            int day = time.day;
                            int hour = time.hour;
                            int min = time.min;
                            int sec = time.sec;
                            GregorianCalendar calendar = new GregorianCalendar(year, month, day, hour, min, sec);
                            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                            String value = format.format(calendar.getTime());
                            value = value.substring(0, value.length() - 2) + ":" + value.substring(value.length() - 2);
                            child.setValue(value);
                        }
                    } else if (typeClsName.equals("date")) {
                        Date[] dates = session.getDates(krnObject.id, attr.id, 0);
                        if (dates != null && dates.length > 0) {
                            Date date = dates[0];
                            int year = date.year;
                            int month = date.month;
                            int day = date.day;
                            DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            GregorianCalendar calendar = new GregorianCalendar(year, month, day);
                            String value = format.format(calendar.getTime());
                            child.setValue(value);
                        }
                    } else if (typeClsName.equals("boolean")) {
                        Boolean value = session.getLongsSingular(krnObject, attr, false) == 1;
                        child.setValue(String.valueOf(value));
                    }
                } catch (Exception e) {
                    log.error("Ошибка при считывании атрибута \"" + name + "\"  типа \"" + typeClsName + "\"");
                    e.printStackTrace();
                }
            } else {
                KrnObject[] objs = session.getObjects(krnObject.id, attr.id, new long[] {}, 0);
                for (int j = 0; j < objs.length; j++) {
                    getData(objs[j], child, session);
                }
            }
        }
    }
    
    private Document getContent(HttpServletRequest request) {
        try {
            StringBuffer buffer = new StringBuffer();
            String line = null;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
            	if (line.matches(".*"))
            		buffer.append(line);
            }
            Document document = XmlUtil.getDocument(new InputSource(new StringReader(buffer.toString())));
            if (printerStatus) {                
                log.info("------------------------------------------------------------------------------");
                log.info("Входящий запрос: " + buffer.toString());
                log.info("------------------------------------------------------------------------------");
            }
            return document;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Параметр type принимает 2 значения: 0 - класс входящего сообщения, 1 - класс исходящего сообщения
    private KrnClass getRootClass(KrnObject webServiceObj, int type, Session session) throws KrnException {
        KrnObject[] bindingObjs = session.getObjects(webServiceObj.id, bindingsAttr.id, new long[0], 0);
        if (bindingObjs != null && bindingObjs.length > 0) {
            KrnObject[] portTypeObjs = session.getObjects(bindingObjs[0].id, portTypeAttr.id, new long[0], 0);
            if (portTypeObjs != null && portTypeObjs.length > 0) {
                KrnObject[] operationObjs = session.getObjects(portTypeObjs[0].id, operationsAttr.id, new long[0], 0);
                if (operationObjs != null && operationObjs.length > 0) {
                    KrnObject[] messageObjs = session.getObjects(operationObjs[0].id, type == 0 ? inputMessageAttr.id : outputMessageAttr.id, new long[0], 0);
                    if (messageObjs != null && messageObjs.length > 0) {
                        String className = session.getStringsSingular(messageObjs[0].id, classNameAttr.id, 0, false, false);
                        if (!className.equals("")) {
                            KrnClass rootClass = session.getClassByName(className);
                            if (rootClass != null) {
                                return rootClass;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    // Параметр type принимает 2 значения: 0 - корневой элемент входящего сообщения, 1 - корневой элемент исходящего сообщения
    private List<String> getRootElement(KrnObject webServiceObj, int type, Session session) throws KrnException {
        KrnObject[] bindingObjs = session.getObjects(webServiceObj.id, bindingsAttr.id, new long[0], 0);
        if (bindingObjs != null && bindingObjs.length > 0) {
            KrnObject bindingObj = bindingObjs[0];
            KrnObject[] portTypeObjs = session.getObjects(bindingObj.id, portTypeAttr.id, new long[0], 0);
            if (portTypeObjs != null && portTypeObjs.length > 0) {
                KrnObject portTypeObj = portTypeObjs[0];
                KrnObject[] operationObjs = session.getObjects(portTypeObj.id, operationsAttr.id, new long[0], 0);
                if (operationObjs != null && operationObjs.length > 0) {
                    KrnObject operationObj = operationObjs[0];
                    KrnObject[] messageObjs = session.getObjects(operationObj.id, type == 0 ? inputMessageAttr.id : outputMessageAttr.id, new long[0], 0);
                    if (messageObjs != null && messageObjs.length > 0) {
                        KrnObject inputMessageObj = messageObjs[0];
                        KrnObject[] partObjs = session.getObjects(inputMessageObj.id, partsAttr.id, new long[0], 0);
                        if (partObjs != null && partObjs.length > 0) {
                            KrnObject partObj = partObjs[0];
                            KrnObject[] elementObjs = session.getObjects(partObj.id, elementAttr.id, new long[0], 0);
                            if (elementObjs != null && elementObjs.length > 0) {
                                KrnObject elementObj = elementObjs[0];
                                List<String> rootElementNameInfo = extractQName(elementObj, session);
                                return rootElementNameInfo;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private List<String> extractQName(KrnObject object, Session session) {
        try {
            KrnClass objectClass = session.getClassById(object.classId);
            KrnAttribute nameAttr = session.getAttributeByName(objectClass, "name");
            KrnObject[] qNameObjs = session.getObjects(object.id, nameAttr.id, new long[0], 0);
            if (qNameObjs != null && qNameObjs.length > 0) {
                KrnObject qNameObj = qNameObjs[0];
                String localname = session.getStringsSingular(qNameObj.id, localNameAttr.id, 0, false, false);
                String namespace = session.getStringsSingular(qNameObj.id, namespaceAttr.id, 0, false, false);
                return Arrays.asList(localname, namespace);
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}