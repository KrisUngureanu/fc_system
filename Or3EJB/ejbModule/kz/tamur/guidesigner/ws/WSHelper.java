package kz.tamur.guidesigner.ws;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
//import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.wsdl.Binding;
import javax.wsdl.Definition;
import javax.wsdl.Import;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.Part;
import javax.wsdl.Port;
import javax.wsdl.PortType;
import javax.wsdl.Service;
import javax.wsdl.Types;
import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.soap.SOAPAddress;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.stream.StreamResult;

import jlibs.xml.sax.XMLDocument;
import jlibs.xml.xsd.XSInstance;
import jlibs.xml.xsd.XSParser;
import kz.tamur.comps.Constants;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.xs.XSModel;
import org.jdom.Document;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.cifs.or2.kernel.Date;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.Time;
import com.cifs.or2.server.Session;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSDeclaration;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.parser.XSOMParser;

public class WSHelper {
    
    private Session session;
    private static final Log log = LogFactory.getLog(WSHelper.class);
    private static boolean isVerified = false;

    private XSOMParser parser;
    private Map<String, KrnObject> allNamespaces;
    private Map<String, String> namespacesByPrefices;
    private Map<QName, KrnObject> allBindings;
    private Map<QName, KrnObject> allPortTypes;
    private Map<QName, KrnObject> allMessages;
    private Map<QName, KrnObject> allTypes;
    private Map<QName, KrnObject> allElements;
    
    // Классы
    private static KrnClass webServiceClass;
    private static KrnClass bindingClass;
    private static KrnClass portTypeClass;
    private static KrnClass wsdlOperationClass;
    private static KrnClass wsdlMessageClass;
    private static KrnClass wsdlPartClass;
    private static KrnClass wsClass;
    private static KrnClass elementClass;
    private static KrnClass typeClass;
    private static KrnClass namespaceClass;
    
    // Атрибуты
    private static KrnAttribute wsdlAttr;
    private static KrnAttribute wsdlNameAttr;
    private static KrnAttribute packageNameAttr;
    private static KrnAttribute methodOr3Attr;
    private static KrnAttribute bindingsAttr;
    private static KrnAttribute portTypeAttr;
    private static KrnAttribute operationsAttr;
    private static KrnAttribute inputMessageAttr;
    private static KrnAttribute outputMessageAttr;
    private static KrnAttribute partsAttr;
    private static KrnAttribute classNameAttr;
    private static KrnAttribute elementAttr;
    private static KrnAttribute typeNameAttr;
    private static KrnAttribute minOccursAttr;
    private static KrnAttribute maxOccursAttr;
    private static KrnAttribute typeAttr;
    private static KrnAttribute elementsAttr;
    private static KrnAttribute uriAttr;
    private static KrnAttribute prefixAttr;
    private static KrnAttribute typeURIAttr;
    private static KrnAttribute isArrayAttr;
    private static KrnAttribute arrayTypeNameAttr;
    private static KrnAttribute arrayTypeURIAttr;
    private static KrnAttribute arrayTypeAttr;
    private static KrnAttribute namespacesAttr;
    private static KrnAttribute addressAttr;
    private static KrnAttribute serviceIdAttr;
    
    private static final Map<String, Long> typesMap;
    static
    {
        typesMap = new HashMap<String, Long>();
        typesMap.put("string", (long) 1);
        typesMap.put("int", (long) 2);
        typesMap.put("dateTime", (long) 3);
        typesMap.put("date", (long) 4);
        typesMap.put("boolean", (long) 5);
    }
    
    private static final Map<Long, String> id_typeMap;
    static
    {
        id_typeMap = new HashMap<Long, String>();
        id_typeMap.put((long) 1, "string");
        id_typeMap.put((long) 2, "int");
        id_typeMap.put((long) 3, "dateTime");
        id_typeMap.put((long) 4, "date");
        id_typeMap.put((long) 5, "boolean");
    }
    
    public WSHelper(Session session) {
        this.session = session;
        try {
            initClasses(session);
            
            // Инициализация классов
            webServiceClass = session.getClassByName("WebService");
            bindingClass = session.getClassByName("Binding");
            portTypeClass = session.getClassByName("PortType");
            wsdlOperationClass = session.getClassByName("WSDLOperation");
            wsdlMessageClass = session.getClassByName("WSDLMessage");
            wsdlPartClass = session.getClassByName("WSDLPart");
            wsClass = session.getClassByName("WSClass");
            elementClass = session.getClassByName("WSElement");
            typeClass = session.getClassByName("WSType");
            namespaceClass = session.getClassByName("Namespace");

            // Инициализация атрибутов
            wsdlAttr = session.getAttributeByName(webServiceClass, "wsdl");
            wsdlNameAttr = session.getAttributeByName(webServiceClass, "wsdlName");
            packageNameAttr = session.getAttributeByName(webServiceClass, "packageName");
            methodOr3Attr = session.getAttributeByName(webServiceClass, "methodOr3");
            bindingsAttr = session.getAttributeByName(webServiceClass, "bindings");
            portTypeAttr = session.getAttributeByName(bindingClass, "portType");
            operationsAttr = session.getAttributeByName(portTypeClass, "operations");
            inputMessageAttr = session.getAttributeByName(wsdlOperationClass, "inputMessage");
            outputMessageAttr = session.getAttributeByName(wsdlOperationClass, "outputMessage");
            partsAttr = session.getAttributeByName(wsdlMessageClass, "parts");
            classNameAttr = session.getAttributeByName(wsdlMessageClass, "className");
            elementAttr = session.getAttributeByName(wsdlPartClass, "element");
            typeNameAttr = session.getAttributeByName(elementClass, "typeName");
            minOccursAttr = session.getAttributeByName(elementClass, "minOccurs");
            maxOccursAttr = session.getAttributeByName(elementClass, "maxOccurs");
            typeAttr = session.getAttributeByName(elementClass, "type");
            elementsAttr = session.getAttributeByName(typeClass, "elements");
            uriAttr = session.getAttributeByName(namespaceClass, "URI");
            prefixAttr = session.getAttributeByName(namespaceClass, "prefix");
            typeURIAttr = session.getAttributeByName(elementClass, "typeURI");
            isArrayAttr = session.getAttributeByName(typeClass, "isArray");
            arrayTypeNameAttr = session.getAttributeByName(typeClass, "arrayTypeName");
            arrayTypeURIAttr = session.getAttributeByName(typeClass, "arrayTypeURI");
            arrayTypeAttr = session.getAttributeByName(typeClass, "arrayType");
            namespacesAttr = session.getAttributeByName(webServiceClass, "namespaces");
            addressAttr = session.getAttributeByName(bindingClass, "address");
            serviceIdAttr = session.getAttributeByName(webServiceClass, "serviceId");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static synchronized void initClasses(Session session) throws KrnException {
    	if (isVerified) {
    		return;
    	}
        // Генерирование классов
        System.out.println("Запуск проверки иерархии классов!");
        KrnClass wsClass = session.getClassByName("WS");
        if (wsClass == null) {
            KrnClass systemClass = session.getClassByName("Системный класс");
            wsClass = session.createClass(systemClass, "WS", true, null, 0);
            System.out.println("Создание класса: WS");
        }
        KrnClass namespaceClass = session.getClassByName("Namespace");
        if (namespaceClass == null) {
            namespaceClass = session.createClass(wsClass, "Namespace", true, null, 0);
            System.out.println("Создание класса: Namespace");
        }
        KrnClass qnameClass = session.getClassByName("QName");
        if (qnameClass == null) {
            qnameClass = session.createClass(wsClass, "QName", true, null, 0);
            System.out.println("Создание класса: QName");
        }
        KrnClass wsClassClass = session.getClassByName("WSClass");
        if (wsClassClass == null) {
            wsClassClass = session.createClass(wsClass, "WSClass", true, null, 0);
            System.out.println("Создание класса: WSClass");
        }
        KrnClass wsdlClass = session.getClassByName("WSDL");
        if (wsdlClass == null) {
            wsdlClass = session.createClass(wsClass, "WSDL", true, null, 0);
            System.out.println("Создание класса: WSDL");
        }
        KrnClass bindingClass = session.getClassByName("Binding");
        if (bindingClass == null) {
            bindingClass = session.createClass(wsdlClass, "Binding", true, null, 0);
            System.out.println("Создание класса: Binding");
        }
        KrnClass portTypeClass = session.getClassByName("PortType");
        if (portTypeClass == null) {
            portTypeClass = session.createClass(wsdlClass, "PortType", true, null, 0);
            System.out.println("Создание класса: PortType");
        }
        KrnClass wsdlMessageClass = session.getClassByName("WSDLMessage");
        if (wsdlMessageClass == null) {
            wsdlMessageClass = session.createClass(wsdlClass, "WSDLMessage", true, null, 0);
            System.out.println("Создание класса: WSDLMessage");
        }
        KrnClass wsdlOperationClass = session.getClassByName("WSDLOperation");
        if (wsdlOperationClass == null) {
            wsdlOperationClass = session.createClass(wsdlClass, "WSDLOperation", true, null, 0);
            System.out.println("Создание класса: WSDLOperation");
        }
        KrnClass wsdlPartClass = session.getClassByName("WSDLPart");
        if (wsdlPartClass == null) {
            wsdlPartClass = session.createClass(wsdlClass, "WSDLPart", true, null, 0);
            System.out.println("Создание класса: WSDLPart");
        }
        KrnClass webServiceClass = session.getClassByName("WebService");
        if (webServiceClass == null) {
            webServiceClass = session.createClass(wsdlClass, "WebService", true, null, 0);
            System.out.println("Создание класса: WebService");
        }
        KrnClass wsElementClass = session.getClassByName("WSElement");
        if (wsElementClass == null) {
            wsElementClass = session.createClass(wsClass, "WSElement", true, null, 0);
            System.out.println("Создание класса: WSElement");
        }
        KrnClass wsMethodClass = session.getClassByName("WSMethod");
        if (wsMethodClass == null) {
            wsMethodClass = session.createClass(wsClass, "WSMethod", true, null, 0);
            System.out.println("Создание класса: WSMethod");
        }
        KrnClass wsTypeClass = session.getClassByName("WSType");
        if (wsTypeClass == null) {
            wsTypeClass = session.createClass(wsClass, "WSType", true, null, 0);
            System.out.println("Создание класса: WSType");
        }
        KrnClass wsUserClass = session.getClassByName("WSUser");
        if (wsUserClass == null) {
            wsUserClass = session.createClass(wsClass, "WSUser", true, null, 0);
            System.out.println("Создание класса: WSUser");
        }
        
        // Генерирование атрибутов
        KrnClass stringClass = session.getClassByName("string");
        KrnClass blobClass = session.getClassByName("blob");
        KrnClass longClass = session.getClassByName("long");
        KrnClass booleanClass = session.getClassByName("boolean");

        // Класс Namespace
        if (session.getAttributeByName(namespaceClass, "URI") == null) {
            session.createAttribute(namespaceClass, stringClass, "URI", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + namespaceClass.name + ": URI");
        }
        if (session.getAttributeByName(namespaceClass, "prefix") == null) {
            session.createAttribute(namespaceClass, stringClass, "prefix", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + namespaceClass.name + ": prefix");
        }
        
        // Класс Qname
        if (session.getAttributeByName(qnameClass, "localName") == null) {
            session.createAttribute(qnameClass, stringClass, "localName", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + qnameClass.name + ": localName");
        }
        if (session.getAttributeByName(qnameClass, "namespace") == null) {
            session.createAttribute(qnameClass, stringClass, "namespace", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + qnameClass.name + ": namespace");
        }
        
        // Класс Binding
        if (session.getAttributeByName(bindingClass, "name") == null) {
            session.createAttribute(bindingClass, qnameClass, "name", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + bindingClass.name + ": name");
        }
        if (session.getAttributeByName(bindingClass, "address") == null) {
            session.createAttribute(bindingClass, stringClass, "address", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + bindingClass.name + ": address");
        }
        if (session.getAttributeByName(bindingClass, "portType") == null) {
            session.createAttribute(bindingClass, portTypeClass, "portType", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + bindingClass.name + ": portType");
        }
        
        // Класс PortType
        if (session.getAttributeByName(portTypeClass, "name") == null) {
            session.createAttribute(portTypeClass, qnameClass, "name", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + portTypeClass.name + ": name");
        }
        if (session.getAttributeByName(portTypeClass, "operations") == null) {
            session.createAttribute(portTypeClass, wsdlOperationClass, "operations", 1, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + portTypeClass.name + ": operations");
        }
        
        // Класс WSDLMessage
        if (session.getAttributeByName(wsdlMessageClass, "name") == null) {
            session.createAttribute(wsdlMessageClass, qnameClass, "name", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsdlMessageClass.name + ": name");
        }
        if (session.getAttributeByName(wsdlMessageClass, "className") == null) {
            session.createAttribute(wsdlMessageClass, stringClass, "className", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsdlMessageClass.name + ": className");
        }
        if (session.getAttributeByName(wsdlMessageClass, "parts") == null) {
            session.createAttribute(wsdlMessageClass, wsdlPartClass, "parts", 1, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsdlMessageClass.name + ": parts");
        }
        
        // Класс WSDLOperation
        if (session.getAttributeByName(wsdlOperationClass, "name") == null) {
            session.createAttribute(wsdlOperationClass, qnameClass, "name", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsdlOperationClass.name + ": name");
        }
        if (session.getAttributeByName(wsdlOperationClass, "inputMessage") == null) {
            session.createAttribute(wsdlOperationClass, wsdlMessageClass, "inputMessage", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsdlOperationClass.name + ": inputMessage");
        }
        if (session.getAttributeByName(wsdlOperationClass, "outputMessage") == null) {
            session.createAttribute(wsdlOperationClass, wsdlMessageClass, "outputMessage", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsdlOperationClass.name + ": outputMessage");
        }
        
        // Класс WSDLPart
        if (session.getAttributeByName(wsdlPartClass, "name") == null) {
            session.createAttribute(wsdlPartClass, qnameClass, "name", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsdlPartClass.name + ": name");
        }
        if (session.getAttributeByName(wsdlPartClass, "element") == null) {
            session.createAttribute(wsdlPartClass, wsElementClass, "element", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsdlPartClass.name + ": element");
        }
        if (session.getAttributeByName(wsdlPartClass, "type") == null) {
            session.createAttribute(wsdlPartClass, wsTypeClass, "type", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsdlPartClass.name + ": type");
        }
        if (session.getAttributeByName(wsdlPartClass, "typeName") == null) {
            session.createAttribute(wsdlPartClass, stringClass, "typeName", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsdlPartClass.name + ": typeName");
        }
        if (session.getAttributeByName(wsdlPartClass, "typeURI") == null) {
            session.createAttribute(wsdlPartClass, stringClass, "typeURI", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsdlPartClass.name + ": typeURI");
        }
        
        // Класс WebService
        if (session.getAttributeByName(webServiceClass, "name") == null) {
            session.createAttribute(webServiceClass, qnameClass, "name", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + webServiceClass.name + ": name");
        }
        if (session.getAttributeByName(webServiceClass, "bindings") == null) {
            session.createAttribute(webServiceClass, bindingClass, "bindings", 1, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + webServiceClass.name + ": bindings");
        }
        if (session.getAttributeByName(webServiceClass, "methodOr3") == null) {
            session.createAttribute(webServiceClass, stringClass, "methodOr3", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + webServiceClass.name + ": methodOr3");
        }
        if (session.getAttributeByName(webServiceClass, "namespaces") == null) {
            session.createAttribute(webServiceClass, namespaceClass, "namespaces", 1, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + webServiceClass.name + ": namespaces");
        }
        if (session.getAttributeByName(webServiceClass, "packageName") == null) {
            session.createAttribute(webServiceClass, stringClass, "packageName", 0, true, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + webServiceClass.name + ": packageName");
        }
        if (session.getAttributeByName(webServiceClass, "serviceId") == null) {
            session.createAttribute(webServiceClass, stringClass, "serviceId", 0, true, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + webServiceClass.name + ": serviceId");
        }
        if (session.getAttributeByName(webServiceClass, "wsdl") == null) {
            session.createAttribute(webServiceClass, blobClass, "wsdl", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + webServiceClass.name + ": wsdl");
        }
        if (session.getAttributeByName(webServiceClass, "wsdlName") == null) {
            session.createAttribute(webServiceClass, stringClass, "wsdlName", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + webServiceClass.name + ": wsdlName");
        }
        
        // Класс WSElement
        if (session.getAttributeByName(wsElementClass, "maxOccurs") == null) {
            session.createAttribute(wsElementClass, longClass, "maxOccurs", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsElementClass.name + ": maxOccurs");
        }
        if (session.getAttributeByName(wsElementClass, "minOccurs") == null) {
            session.createAttribute(wsElementClass, longClass, "minOccurs", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsElementClass.name + ": minOccurs");
        }
        if (session.getAttributeByName(wsElementClass, "name") == null) {
            session.createAttribute(wsElementClass, qnameClass, "name", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsElementClass.name + ": name");
        }
        if (session.getAttributeByName(wsElementClass, "type") == null) {
            session.createAttribute(wsElementClass, wsTypeClass, "type", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsElementClass.name + ": type");
        }
        if (session.getAttributeByName(wsElementClass, "typeName") == null) {
            session.createAttribute(wsElementClass, stringClass, "typeName", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsElementClass.name + ": typeName");
        }
        if (session.getAttributeByName(wsElementClass, "typeURI") == null) {
            session.createAttribute(wsElementClass, stringClass, "typeURI", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsElementClass.name + ": typeURI");
        }
        
        // Класс WSType
        if (session.getAttributeByName(wsTypeClass, "arrayType") == null) {
            session.createAttribute(wsTypeClass, wsTypeClass, "arrayType", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsTypeClass.name + ": arrayType");
        }
        if (session.getAttributeByName(wsTypeClass, "arrayTypeName") == null) {
            session.createAttribute(wsTypeClass, stringClass, "arrayTypeName", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsTypeClass.name + ": arrayTypeName");
        }
        if (session.getAttributeByName(wsTypeClass, "arrayTypeURI") == null) {
            session.createAttribute(wsTypeClass, stringClass, "arrayTypeURI", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsTypeClass.name + ": arrayTypeURI");
        }
        if (session.getAttributeByName(wsTypeClass, "elements") == null) {
            session.createAttribute(wsTypeClass, wsElementClass, "elements", 1, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsTypeClass.name + ": elements");
        }
        if (session.getAttributeByName(wsTypeClass, "isArray") == null) {
            session.createAttribute(wsTypeClass, booleanClass, "isArray", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsTypeClass.name + ": isArray");
        }
        if (session.getAttributeByName(wsTypeClass, "name") == null) {
            session.createAttribute(wsTypeClass, qnameClass, "name", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsTypeClass.name + ": name");
        }
        
        // Класс WSUser
        if (session.getAttributeByName(wsUserClass, "login") == null) {
            session.createAttribute(wsUserClass, stringClass, "login", 0, true, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsUserClass.name + ": login");
        }
        if (session.getAttributeByName(wsUserClass, "password") == null) {
            session.createAttribute(wsUserClass, stringClass, "password", 0, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsUserClass.name + ": password");
        }
        if (session.getAttributeByName(wsUserClass, "services") == null) {
            session.createAttribute(wsUserClass, webServiceClass, "services", 1, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            System.out.println("Создание атрибута в классе " + wsUserClass.name + ": services");
        }
        
        System.out.println("Проверка завершена!");
        isVerified = true;
        session.commitTransaction();
    }
    
    public String generateWebServiceClasses(byte[] wsdlFileInBytes, String wsdlName, String packageName, String methodName) {
        try {
            KrnClass rootClass = session.getClassByName(packageName);
            if (rootClass != null) {
                Set<KrnClass> subclasses = session.getSubClasses(rootClass.id);
                if (subclasses.size() > 0) {
                    return "Пакет с указанным именем уже сгенерирован и в нем имеются классы!";
                }
            }
            File wsdlDirectory = new File("wsdlDirectory");
            if (!wsdlDirectory.exists()) {
                wsdlDirectory.mkdir();
            }
            File wsdlFile = new File(wsdlDirectory, wsdlName);
            int j = 0;
            while(wsdlFile.exists()) {
                wsdlFile = new File(wsdlDirectory, wsdlName.substring(0, wsdlName.indexOf(".wsdl")) + "_" + j + ".wsdl");
                j++;
            }
            FileUtils.writeByteArrayToFile(wsdlFile, wsdlFileInBytes);

            InputStream is = new BufferedInputStream(new FileInputStream(wsdlFile));
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(is);
            is.close();
            org.jdom.Element rootElement = document.getRootElement();
            org.jdom.Namespace wsdlNS = org.jdom.Namespace.getNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
            org.jdom.Element serviceElement = rootElement.getChild("service", wsdlNS);
            String serviceName = serviceElement.getAttributeValue("name");

            List<String> existingServices = new ArrayList<String>();
            try {
                KrnObject[] wsObjects = session.getClassObjects(webServiceClass, new long[0], 0);
                for (int i = 0; i < wsObjects.length; i++) {
                    KrnObject wsObject = wsObjects[i];
                    List<String> nameInfo = extractQName(wsObject, session);
                    if (nameInfo != null) {
                        existingServices.add(nameInfo.get(0));
                    }
                }
            } catch (KrnException e) {
                e.printStackTrace();
            }
            
            if (existingServices.contains(serviceName)) {
                wsdlFile.delete();
                return "Сервис уже сгенерирован!";
            }
            Map<QName, KrnObject> services = generateWebServiceClasses(wsdlFile.getAbsolutePath(), session);
            KrnObject serviceObj = null;
            if (services != null) {
                for (Entry<QName, KrnObject> entry : services.entrySet()) {
                    QName key = entry.getKey();
                    if (serviceName.equals(key.getLocalPart())) {
                        serviceObj = services.get(key);
                        session.setBlob(serviceObj.id, wsdlAttr.id, 0, wsdlFileInBytes, 0, 0);
                        session.setString(serviceObj.id, wsdlNameAttr.id, 0, 0, false, wsdlFile.getName(), 0);
                        break;
                    }
                }
            }
            // Генерация классов
            if (serviceObj != null) {
                generateClassesHierarchy(serviceObj, packageName);
                session.setString(serviceObj.id, packageNameAttr.id, 0, 0, false, packageName, 0);
                session.setString(serviceObj.id, methodOr3Attr.id, 0, 0, false, methodName, 0);
                session.commitTransaction();
                return "Сервис успешно сгенерирован!";
            } else {
                return "Ошибка при генерировании сервиса!";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при генерировании сервиса!";
        }
    }
    
    private void generateClassesHierarchy(KrnObject serviceObj, String packageName) {
        try {
            KrnClass rootClass = session.createClass(wsClass, packageName, true, null, 0);
            Map<Long, KrnClass> classesMap = new HashMap<Long, KrnClass>();            
            KrnObject[] bindingObjs = session.getObjects(serviceObj.id, bindingsAttr.id, new long[0], 0);
            if (bindingObjs != null && bindingObjs.length > 0) {
                for (int i = 0; i < bindingObjs.length; i++) {
                    KrnObject[] portTypeObjs = session.getObjects(bindingObjs[i].id, portTypeAttr.id, new long[0], 0);
                    if (portTypeObjs != null && portTypeObjs.length > 0) {
                        KrnObject[] operationObjs = session.getObjects(portTypeObjs[0].id, operationsAttr.id, new long[0], 0);
                        if (operationObjs != null && operationObjs.length > 0) {
                            for (int j = 0; j < operationObjs.length; j++) {
                                KrnObject[] inputMessageObjs = session.getObjects(operationObjs[j].id, inputMessageAttr.id, new long[0], 0);
                                KrnObject[] outputMessageObjs = session.getObjects(operationObjs[j].id, outputMessageAttr.id, new long[0], 0);
                                // Генерирование классов запроса
                                if (inputMessageObjs != null && inputMessageObjs.length > 0) {
                                    List<String> inputMessageNameInfo = extractQName(inputMessageObjs[0], session);
                                    String mainClassName = getClassName(packageName, inputMessageNameInfo.get(0), true);
                                    KrnClass mainClass = session.createClass(rootClass, mainClassName, true, null, 0);
                                    session.setString(inputMessageObjs[0].id, classNameAttr.id, 0, 0, false, mainClassName, 0);
                                    KrnObject[] partObjs = session.getObjects(inputMessageObjs[0].id, partsAttr.id, new long[0], 0);
                                    if (partObjs != null && partObjs.length > 0) {
                                        for (int k = 0; k < partObjs.length; k++) {
                                            KrnObject[] elementObjs = session.getObjects(partObjs[k].id, elementAttr.id, new long[0], 0);
                                            if (elementObjs != null && elementObjs.length > 0) {
                                                KrnObject rootRequestElement = elementObjs[0];
                                                recursiveClassCreator(rootRequestElement, rootClass, mainClass, packageName, classesMap);
                                            }
                                        }
                                    }
                                }
                                
                                // Генерирование классов ответа
                                if (outputMessageObjs != null && outputMessageObjs.length > 0) {
                                    List<String> outputMessageNameInfo = extractQName(outputMessageObjs[0], session);
                                    String mainClassName = getClassName(packageName, outputMessageNameInfo.get(0), true);
                                    KrnClass mainClass = session.createClass(rootClass, mainClassName, true, null, 0);
                                    session.setString(outputMessageObjs[0].id, classNameAttr.id, 0, 0, false, mainClassName, 0);
                                    KrnObject[] partObjs = session.getObjects(outputMessageObjs[0].id, partsAttr.id, new long[0], 0);
                                    if (partObjs != null && partObjs.length > 0) {
                                        for (int k = 0; k < partObjs.length; k++) {
                                            KrnObject[] elementObjs = session.getObjects(partObjs[k].id, elementAttr.id, new long[0], 0);
                                            if (elementObjs != null && elementObjs.length > 0) {
                                                KrnObject rootResponseElement = elementObjs[0];
                                                recursiveClassCreator(rootResponseElement, rootClass, mainClass, packageName, classesMap);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
    }
    
    private void recursiveClassCreator(KrnObject elementObj, KrnClass rootClass, KrnClass mainClass, String packageName, Map<Long, KrnClass> classesMap) throws KrnException {
        String typeName = session.getStringsSingular(elementObj.id, typeNameAttr.id, 0, false, false);
        long minOccurs = session.getLongsSingular(elementObj, minOccursAttr, false);
        long maxOccurs = session.getLongsSingular(elementObj, maxOccursAttr, false);
        int collType = maxOccurs < 0 ? 2 : 0;
        if (typeName != null && !typeName.equals("")) {
            if (typesMap.containsKey(typeName)) {
                long classId = typesMap.get(typeName);  // Id типа атрибута
                List<String> nameInfo = extractQName(elementObj, session);  // Название атрибута
                KrnClass typeCls = session.getClassById(classId);       // Тип атрибута  
                session.createAttribute(mainClass, typeCls, nameInfo.get(0), collType, false, false, false, true, 0, 0, 0, 0, false, null, 0);
            }
        } else {
            KrnObject[] typeObjs = session.getObjects(elementObj.id, typeAttr.id, new long[0], 0);
            if (typeObjs != null && typeObjs.length > 0) {
                List<String> nameInfo = extractQName(elementObj, session);      // Название атрибута
                KrnObject typeObj = typeObjs[0];        // Тип атрибута
                if (classesMap.containsKey(typeObj.id)) {
                    session.createAttribute(mainClass, classesMap.get(typeObj.id), nameInfo.get(0), collType, false, false, false, true, 0, 0, 0, 0, false, null, 0);
                } else {
                    // Если такого типа еще нет, то надо его создать!
                    List<String> newTypeNameInfo = extractQName(typeObj, session);      // Название нового класса
                    String newTypeName = getClassName(packageName, newTypeNameInfo.get(0), false);
                    KrnClass newType = session.createClass(rootClass, newTypeName, true, null, 0);       // Новый тип
                    session.commitTransaction();
                    classesMap.put(typeObj.id, newType);
                    
                    // Добавляем атрибуты в новый тип
                    KrnObject[] elementsObjs = session.getObjects(typeObj.id, elementsAttr.id, new long[0], 0);
                    if (elementsObjs != null && elementsObjs.length > 0) {
                        for (int i = 0; i < elementsObjs.length; i++) {
                            recursiveClassCreator(elementsObjs[i], rootClass, newType, packageName, classesMap);
                        }
                    }
                    session.createAttribute(mainClass, classesMap.get(typeObj.id), nameInfo.get(0), collType, false, false, false, true, 0, 0, 0, 0, false, null, 0);
                }
            }
        }
    }
    
    private String getAttributeName(String name) {
        String attributeName = Character.toString(name.charAt(0)).toLowerCase(Constants.OK) + name.substring(1);
        return attributeName;
    }
    
    private String getClassName(String packageName, String name, boolean isMain) {
        String className = packageName + "_" + Character.toString(name.charAt(0)).toUpperCase(Locale.ROOT) + name.substring(1) + (isMain ? "_Main" : "");
        return className;
    }
    
    public byte[] generateXML(String serviceName, int type) {
        try {
            KrnObject[] wsObjects = session.getClassObjects(wsClass, new long[0], 0);
            for (int i = 0; i < wsObjects.length; i++) {
                KrnObject wsObject = wsObjects[i];
                List<String> webServiceNameInfo = extractQName(wsObject, session);

                if (webServiceNameInfo != null && serviceName.equals(webServiceNameInfo.get(0))) {
                    String wsdlName = session.getStringsSingular(wsObject.id, wsdlNameAttr.id, 0, false, false);
                    File wsdlDirectory = new File("wsdlDirectory");
                    File wsdlFile = new File(wsdlDirectory, wsdlName);
                    if (!wsdlFile.exists()) {
                        byte[] wsdlFileInBytes = session.getBlob(wsObject.id, wsdlAttr.id, 0, 0, 0);
                        FileUtils.writeByteArrayToFile(wsdlFile, wsdlFileInBytes);
                    }
                    WSDLFactory wsdlFactory = WSDLFactory.newInstance();
                    WSDLReader wsdlReader = wsdlFactory.newWSDLReader();

                    wsdlReader.setFeature("javax.wsdl.verbose", false);
                    wsdlReader.setFeature("javax.wsdl.importDocuments", true);

                    Definition definition = wsdlReader.readWSDL(wsdlFile.getAbsolutePath());
                    if (definition != null) {
                        Map<?, ?> services = definition.getServices();
                        for (Iterator<?> it = services.values().iterator(); it.hasNext();) {
                            Service service = (Service) it.next();
                            QName name = service.getQName();
                            System.out.println(name.getLocalPart() + "____" + name.getNamespaceURI() + "___" + name.getPrefix());

                            XSInstance instance = new XSInstance();
                            instance.minimumElementsGenerated = 0;
                            instance.maximumElementsGenerated = 0;
                            instance.generateDefaultAttributes = false;
                            instance.generateOptionalAttributes = false;
                            instance.maximumRecursionDepth = 0;
                            instance.generateOptionalElements = true;
                            InputStream is = new BufferedInputStream(new FileInputStream(wsdlFile));
                            SAXBuilder builder = new SAXBuilder();
                            Document document = builder.build(is);
                            is.close();
                            org.jdom.Element root = document.getRootElement();
                            org.jdom.Namespace xsdNS = org.jdom.Namespace.getNamespace("xsd", "http://www.w3.org/2001/XMLSchema");
                            org.jdom.Namespace tnsNS = org.jdom.Namespace.getNamespace("tns", webServiceNameInfo.get(1));
                            org.jdom.Namespace wsdlNS = org.jdom.Namespace.getNamespace("wsdl", "http://schemas.xmlsoap.org/wsdl/");
                            org.jdom.Element typesElement = root.getChild("types", wsdlNS);
                            org.jdom.Element schemaElement = typesElement.getChild("schema", xsdNS);
                            schemaElement.addNamespaceDeclaration(xsdNS);
                            schemaElement.addNamespaceDeclaration(tnsNS);
                            Document docNew = new Document((org.jdom.Element) schemaElement.clone());
                            XMLOutputter xmlOutput = new XMLOutputter();
                            xmlOutput.setFormat(Format.getPrettyFormat());
                            File xsdFile = new File(wsdlFile.getAbsolutePath().substring(0, wsdlFile.getAbsolutePath().indexOf(".")) + ".xsd");
                            xmlOutput.output(docNew, new PrintWriter(xsdFile, "UTF-8"));
                            List<String> rootElementNameInfo = getRootElement(wsObject, type, session);
                            if (rootElementNameInfo != null) {
                                QName rootElement = new QName(rootElementNameInfo.get(1), rootElementNameInfo.get(0));
                                XSModel xsModel = new XSParser().parse(xsdFile.getAbsolutePath());
                                String xmlFileName = xsdFile.getAbsolutePath().substring(0, xsdFile.getAbsolutePath().indexOf(".")) + ".xml";
                                XMLDocument sample = new XMLDocument(new StreamResult(xmlFileName), false, 4, null);
                                try {
                                    instance.generate(xsModel, rootElement, sample);
                                    byte[] xmlFileInBytes = FileUtils.readFileToByteArray(new File(xmlFileName));
                                    return xmlFileInBytes;
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
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
                    KrnObject[] messageObjs = session.getObjects(operationObj.id, (type == 0 ? inputMessageAttr : outputMessageAttr).id, new long[0], 0);
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

    public Map<QName, KrnObject> generateWebServiceClasses(String fileName, Session session) {
        try {
            parser = new XSOMParser();
            parser.parse("soapenc.xsd");
            parser.parse("wsdl.xsd");

            allNamespaces = new HashMap<String, KrnObject>();
            namespacesByPrefices = new HashMap<String, String>();
            allBindings = new HashMap<QName, KrnObject>();
            allPortTypes = new HashMap<QName, KrnObject>();
            allMessages = new HashMap<QName, KrnObject>();
            allTypes = new HashMap<QName, KrnObject>();
            allElements = new HashMap<QName, KrnObject>();

            WSDLFactory wsdlFactory = WSDLFactory.newInstance();
            WSDLReader wsdlReader = wsdlFactory.newWSDLReader();

            wsdlReader.setFeature("javax.wsdl.verbose",false);
            wsdlReader.setFeature("javax.wsdl.importDocuments",true);

            Definition definition = wsdlReader.readWSDL(fileName);
            if (definition == null){
                System.err.println("definition element is null");
                throw new KrnException(0, "definition element is null");
            }

            Map<QName, KrnObject> services = parseDefinition(definition);
            System.out.println("OK");
            return services;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Map<QName, KrnObject> parseDefinition(Definition definition) throws Exception {
        Map<?, ?> nss = definition.getNamespaces();
        for (Iterator<?> nsit = nss.keySet().iterator(); nsit.hasNext();) {
            String nsp = (String) nsit.next();
            String nsu = (String) nss.get(nsp);
            if (allNamespaces.get(nsu) == null) {
                KrnObject nso = session.createObject(namespaceClass, 0);
                session.setString(nso.id, uriAttr.id, 0, 0, false, nsu, 0);
                session.setString(nso.id, prefixAttr.id, 0, 0, false, nsp, 0);
                namespacesByPrefices.put(nsp, nsu);
                allNamespaces.put(nsu, nso);
            }
        }

        Map<?, ?> imports = definition.getImports();
        for (Iterator<?> it = imports.values().iterator(); it.hasNext();) {
            Collection<?> l = (Collection<?>) it.next();
            for (Iterator<?> it2 = l.iterator(); it2.hasNext();) {
                Import imp = (Import)it2.next();
                Definition d = imp.getDefinition();
                parseDefinition(d);
            }
        }

        parseTypes(definition);
        parseMessages(definition);
        parsePortTypes(definition);
        parseBindings(definition);
        return parseServices(definition);
    }
    
    private void parseTypes(Definition d) throws Exception {
        Map<?, ?> nss = d.getNamespaces();
        Types ts = d.getTypes();
        if (ts == null) return;
        List<?> exts = ts.getExtensibilityElements();
        List<String> targetNamespaces = new ArrayList<String>();
        for (int k=0; k<exts.size(); k++) {
            Schema sch = (Schema)exts.get(k);
            Element e = sch.getElement();
            String tns = e.getAttribute("targetNamespace");
            targetNamespaces.add(tns);
            NamedNodeMap map = e.getAttributes();
            for (int i = 0; i < map.getLength(); i++) {
                Node n = map.item(i);
                String name = n.getNodeName();
                if (name.startsWith("xmlns:")) {
                    String value = n.getNodeValue();
                    String prefix = name.substring(6);
                    if (allNamespaces.get(value) == null) {
                        KrnObject nso = session.createObject(namespaceClass, 0);
                        session.setString(nso.id, uriAttr.id, 0, 0, false, value, 0);
                        session.setString(nso.id, prefixAttr.id, 0, 0, false, prefix, 0);
                        namespacesByPrefices.put(prefix, value);
                        allNamespaces.put(value, nso);
                    }

                }
            }
            for (Iterator<?> nsit = nss.keySet().iterator(); nsit.hasNext();) {
                String nsp = (String) nsit.next();
                String nsu = (String) nss.get(nsp);
                e.setAttribute("xmlns:" + nsp, nsu);
            }

            DOMBuilder db = new DOMBuilder();
            org.jdom.Element oje = db.build(e);
            Format ft = Format.getRawFormat();
            ft.setEncoding("UTF-16LE");
            XMLOutputter o = new XMLOutputter();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            o.output(oje, os);
            os.close();
            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
            parser.parse(is);
            is.close();
        }
        XSSchemaSet schemaSet = parser.getResult();
        for (int i = 0; i < targetNamespaces.size(); i++) {
            String ns = (String) targetNamespaces.get(i);
            XSSchema schema = schemaSet.getSchema(ns);
            Map<String, XSComplexType> cts = schema.getComplexTypes();
            for (Iterator<XSComplexType> it = cts.values().iterator(); it.hasNext();) {
                XSComplexType complexType = (XSComplexType) it.next();
                String name = complexType.getName();
                String nsUri = complexType.getTargetNamespace();

                KrnObject ct = createComplexType(name, nsUri);
                allTypes.put(new QName(nsUri, name), ct);
            }
            Map<String, XSElementDecl> eds = schema.getElementDecls();
            for (Iterator<?> it = eds.values().iterator(); it.hasNext();) {
                XSElementDecl ed = (XSElementDecl) it.next();
                String name = ed.getName();
                String nsUri = ed.getTargetNamespace();
                String typeName = ed.getType().getName();
                String typeUri = ed.getType().getTargetNamespace();
                KrnObject el = createElement(name, nsUri, typeName,  typeUri, null);
                allElements.put(new QName(nsUri, name), el);
            }
        }

        for (int i = 0; i < targetNamespaces.size(); i++) {
            String ns = (String) targetNamespaces.get(i);
            XSSchema schema = schemaSet.getSchema(ns);
            Map<String, XSComplexType> cts = schema.getComplexTypes();
            for (Iterator<XSComplexType> it = cts.values().iterator(); it.hasNext();) {
                XSComplexType complexType = (XSComplexType) it.next();
                String name = complexType.getName();
                String nsUri = complexType.getTargetNamespace();

                KrnObject ct = (KrnObject) allTypes.get(new QName(nsUri, name));

                String baseTypeName = complexType.getBaseType().getName();
                if ("Array".equals(baseTypeName)) {
                    XSAttributeUse a = complexType.getAttributeUse("http://schemas.xmlsoap.org/soap/encoding/", "arrayType");
                    String arrayType = a.getForeignAttribute("http://schemas.xmlsoap.org/wsdl/","arrayType");
                    int pos = arrayType.indexOf(":");
                    String prefix = "";
                    QName arrayTypeName;
                    if (pos > -1) {
                        prefix = arrayType.substring(0, pos);
                        String uri = (String) namespacesByPrefices.get(prefix);
                        String tn = arrayType.substring(pos + 1, arrayType.length() - 2);
                        arrayTypeName = new QName(uri, tn);
                    } else {
                        String tn = arrayType.substring(0, arrayType.length() - 2);
                        arrayTypeName = new QName(tn);
                    }
                    setArrayType(ct, arrayTypeName);
                } else {
                    XSParticle[] children = complexType.getContentType().asParticle().getTerm().asModelGroup().getChildren();
                    for (int j = 0; j < children.length; j++) {
                        XSParticle child = children[j];
                        XSElementDecl el = child.getTerm().asElementDecl();
                        String childName = el.getName();
                        XSDeclaration type = el.getType();
                        String childTypeName = type.getName();
                        String childTypeNs = type.getTargetNamespace();
                        long minOccurs = child.getMinOccurs().longValue();
                        long maxOccurs = child.getMaxOccurs().longValue();
                        addElement(ct, j, childName, nsUri, childTypeName, childTypeNs, new long[] {minOccurs, maxOccurs});
                    }
                }
            }
        }
    }
    
    private void addElement(KrnObject ct, int index, String childName, String childNs, String childTypeName, String childTypeNs, long[] occurs) throws KrnException {
        KrnObject element = createElement(childName, childNs, childTypeName, childTypeNs, occurs);
        session.setObject(ct.id, elementsAttr.id, index, element.id, 0, true);
    }
    
    private KrnObject createElement(String name, String ns, String typeName, String typeNs, long[] occurs) throws KrnException {
        KrnObject element = getElement(name, ns, session);
        KrnObject type = (KrnObject)allTypes.get(new QName(typeNs, typeName));
        if (element == null) {
            element = session.createObject(elementClass, 0);
            setQName(element, new QName(ns, name), session);
            if (type == null) {
                session.setString(element.id, typeNameAttr.id, 0, 0, false, typeName, 0);
                session.setString(element.id, typeURIAttr.id, 0, 0, false, typeNs, 0);
            } else {
                KrnAttribute typeAttr = session.getAttributeByName(elementClass, "type");
                session.setObject(element.id, typeAttr.id, 0, type.id, 0, true);
            }
            if (occurs != null) {
                session.setLong(element.id, minOccursAttr.id, 0, occurs[0], 0);
                session.setLong(element.id, maxOccursAttr.id, 0, occurs[1], 0);
            }
        }
        return element;
    }
    
    private void setArrayType(KrnObject ct, QName name) throws KrnException {
        session.setLong(ct.id, isArrayAttr.id, 0, 1, 0);
        KrnObject arrayType = (KrnObject) allTypes.get(name);
        if (arrayType == null) {
            session.setString(ct.id, arrayTypeNameAttr.id, 0, 0, false, name.getLocalPart(), 0);
            session.setString(ct.id, arrayTypeURIAttr.id, 0, 0, false, name.getNamespaceURI(), 0);
        } else {
            session.setObject(ct.id, arrayTypeAttr.id, 0, arrayType.id, 0, true);
        }
    }
    
    private KrnObject createComplexType(String name, String uri) throws KrnException {
        KrnObject obj = getType(name, uri, session);
        if (obj == null) {
            obj = session.createObject(typeClass, 0);
            setQName(obj, new QName(uri, name), session);
        }
        return obj;
    }
    
    private void parseMessages(Definition d) throws KrnException {
        Map<?, ?> messages = d.getMessages();
        if (messages == null) {
            return;
        }
        for (Iterator<?> it = messages.values().iterator(); it.hasNext();) {
            Message m = (Message)it.next();
            QName name = m.getQName();

            KrnObject mo = session.createObject(wsdlMessageClass, 0);
            setQName(mo, name, session);

            Map<?, ?> parts = m.getParts();
            int i = 0;
            for (Iterator<?> it2 = parts.values().iterator(); it2.hasNext();) {
                Part part = (Part) it2.next();
                QName pName = new QName(name.getNamespaceURI(), part.getName());

                KrnObject po = session.createObject(wsdlPartClass, 0);
                setQName(po, pName, session);

                QName pTypeName = part.getTypeName();
                QName pElementName = part.getElementName();
                if (pTypeName != null) {
                    KrnObject type = (KrnObject) allTypes.get(pTypeName);
                    if (type == null) {
                        KrnAttribute typeNameAttr = session.getAttributeByName(wsdlPartClass, "typeName");
                        KrnAttribute typeNamespaceAttr = session.getAttributeByName(wsdlPartClass, "typeURI");
                        session.setString(po.id, typeNameAttr.id, 0, 0, false, pTypeName.getLocalPart(), 0);
                        session.setString(po.id, typeNamespaceAttr.id, 0, 0, false, pTypeName.getNamespaceURI(), 0);
                    } else {
                        KrnAttribute typeAttr = session.getAttributeByName(wsdlPartClass, "type");
                        session.setObject(po.id, typeAttr.id, 0, type.id, 0, true);
                    }
                } else if (pElementName != null) {
                    KrnObject element = (KrnObject) allElements.get(pElementName);
                    KrnAttribute elementAttr = session.getAttributeByName(wsdlPartClass, "element");
                    session.setObject(po.id, elementAttr.id, 0, element.id, 0, true);
                }
                session.setObject(mo.id, partsAttr.id, i++, po.id, 0, true);
            }
            allMessages.put(name, mo);
        }
    }

    private void parsePortTypes(Definition d) throws KrnException {
        Map<?, ?> types = d.getPortTypes();
        for (Iterator<?> it = types.values().iterator(); it.hasNext();) {
            PortType pt = (PortType)it.next();
            QName name = pt.getQName();

            KrnObject pto = session.createObject(portTypeClass, 0);
            setQName(pto, name, session);

            List<?> ops = pt.getOperations();
            for (int i = 0; i < ops.size(); i++) {
                Operation op = (Operation) ops.get(i);
                String opName = op.getName();
                Input input = op.getInput();
                Message m = input.getMessage();
                KrnObject inpMess = (KrnObject)allMessages.get(m.getQName());
                Output output = op.getOutput();
                m = output.getMessage();
                KrnObject outMess = (KrnObject)allMessages.get(m.getQName());
                KrnObject oo = session.createObject(wsdlOperationClass, 0);
                setQName(oo, new QName(name.getNamespaceURI(), opName), session);
                session.setObject(oo.id, inputMessageAttr.id, 0, inpMess.id, 0, true);
                session.setObject(oo.id, outputMessageAttr.id, 0, outMess.id, 0, true);
                session.setObject(pto.id, operationsAttr.id, i, oo.id, 0, true);
            }
            allPortTypes.put(name, pto);
        }
    }

    private void parseBindings(Definition d) throws KrnException {
        Map<?, ?> bs = d.getBindings();
        for (Iterator<?> it = bs.values().iterator(); it.hasNext();) {
            Binding b = (Binding)it.next();
            QName name = b.getQName();

            KrnObject bo = session.createObject(bindingClass, 0);
            setQName(bo, name, session);

            PortType pt = b.getPortType();
            KrnObject type = (KrnObject) allPortTypes.get(pt.getQName());

            session.setObject(bo.id, portTypeAttr.id, 0, type.id, 0, true);
            allBindings.put(name, bo);
        }
    }
    
    private Map<QName, KrnObject> parseServices(Definition d) throws Exception {
        Map<QName, KrnObject> services = new HashMap<QName, KrnObject>();
        Map<?, ?> ss = d.getServices();
        for (Iterator<?> it = ss.values().iterator(); it.hasNext();) {
            Service s = (Service)it.next();
            QName name = s.getQName();
            KrnObject wso = session.createObject(webServiceClass, 0);
            setQName(wso, name, session);
            services.put(name, wso);

            int i=0;
            for (Iterator<KrnObject> it2 = allNamespaces.values().iterator(); it2.hasNext(); ) {
                KrnObject obj = (KrnObject)it2.next();
                session.setObject(wso.id, namespacesAttr.id, i++, obj.id, 0, true);
            }

            Map<?, ?> ports = s.getPorts();
            i = 0;
            for (Iterator<?> it2 = ports.values().iterator(); it2.hasNext();) {
                Port port = (Port) it2.next();
                SOAPAddress addr = (SOAPAddress)port.getExtensibilityElements().get(0);
                String address = addr.getLocationURI();

                Binding b = port.getBinding();
                KrnObject binding =
                    (KrnObject)allBindings.get(b.getQName());

                session.setString(binding.id, addressAttr.id, 0, 0, false, address, 0);
                session.setObject(wso.id, bindingsAttr.id, i++, binding.id, 0, true);
                // Генерация идентификатора сервиса
                String id = UUID.randomUUID().toString();
                session.setString(wso.id, serviceIdAttr.id, 0, 0, false, id, 0);
            }
        }
        return services;
    }
    
    public KrnObject callService(KrnObject requestObject, String serviceId, String address) {
        try {
            URL url = new URL(address);
            SOAPConnectionFactory connectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = connectionFactory.createConnection();
            SOAPMessage request = createRequestMessage(requestObject);
            request.writeTo(System.out);
//            java.io.FileOutputStream o1 = new FileOutputStream("C:\\req.xml");
//            request.writeTo(o1);
//            o1.close();
            SOAPMessage response = connection.call(request, url);
            response.writeTo(System.out);
//            java.io.FileOutputStream o2 = new FileOutputStream("C:\\res.xml");
//            response.writeTo(o2);
//            o2.close();
            KrnObject responseObject = convertSOAPMessageToKrnObject(response, serviceId);
            System.out.println(responseObject.id);
            session.commitTransaction();
            return responseObject;
                
        }
        catch (Exception e) {
            e.printStackTrace(); 
        } 

        return null;
    }
    
    private KrnObject convertSOAPMessageToKrnObject(SOAPMessage response, String serviceId) throws KrnException, SOAPException, IOException, ParserConfigurationException, SAXException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        response.writeTo(out);
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document document = builder.parse(new InputSource(new StringReader(new String(out.toByteArray()))));
        Node envelopeNode = document.getFirstChild();
        Node bodyNode = envelopeNode.getFirstChild();
        Node rootResponseElement = bodyNode.getFirstChild();
        
        KrnObject[] serviceObjs = session.getObjectsByAttribute(webServiceClass.id, serviceIdAttr.id, 0, 0, serviceId, 0);
        KrnObject webServiceObj = serviceObjs[0];
        String packageName = session.getStringsSingular(webServiceObj.id, packageNameAttr.id, 0, false, false);
        KrnClass responseClass = getRootClass(webServiceObj, 1, session);
        KrnObject parentObject = session.createObject(responseClass, 0);
        String rootResponseElementName = rootResponseElement.getNodeName();
        String attrName = rootResponseElementName.substring(rootResponseElementName.indexOf(":") + 1);
        KrnAttribute attr = session.getAttributeByName(responseClass, attrName);
        KrnClass cls = session.getClassById(attr.classId);
        System.out.println(attr);
        System.out.println(cls);
        
        setData("", rootResponseElement, parentObject, packageName);
        return parentObject;
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
    
    private void setData(String prefix, Node node, KrnObject parentObject, String packageName) throws KrnException {
        String name = node.getNodeName();
        name =  name.substring(name.indexOf(":") + 1);
        String content = node.getTextContent();
        
        if (node.hasChildNodes()) {
            Node child = node.getFirstChild();
            if (child.getNodeType() == Node.TEXT_NODE) {
                KrnClass parentCls = session.getClassById(parentObject.classId);
                KrnAttribute attr = session.getAttributeByName(parentCls, name);
                if (attr == null) return;
                long typeClsId = attr.typeClassId;
                String typeClsName = id_typeMap.get(typeClsId);
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
                System.out.println(prefix + name + " --> " + content);
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
//                int index = 0;
//                if (attr.collectionType == 1) {
//                    index = session.getObjects(parentObject.id, attr.id, new long[0], 0).length;
//                }
                session.setObject(parentObject.id, attr.id, 0, obj.id, 0, true);
                session.commitTransaction();
                System.out.println(prefix + name);
                NodeList list = node.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    setData(prefix + "\t", list.item(i), obj, packageName);
                }
            }
        } else {
            String clsName = getClassName(packageName, name, false);
            KrnClass cls = session.getClassByName(clsName);
            KrnObject obj = session.createObject(cls, 0);
            KrnClass parentCls = session.getClassById(parentObject.classId);
            KrnAttribute attr = session.getAttributeByName(parentCls, name);
//            int index = 0;
//            if (attr.collectionType == 1) {
//                index = session.getObjects(parentObject.id, attr.id, new long[0], 0).length;
//            }
            session.setObject(parentObject.id, attr.id, 0, obj.id, 0, true);
            session.commitTransaction();
            System.out.println(prefix + name);
        } 
    }
    
    private SOAPMessage createRequestMessage(KrnObject requestObject) throws SOAPException, KrnException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage requestMessage = messageFactory.createMessage();
        SOAPEnvelope envelopeElement = requestMessage.getSOAPPart().getEnvelope();
        envelopeElement.removeChild(envelopeElement.getHeader());
        envelopeElement.addNamespaceDeclaration("SOAP-ENC", "http://schemas.xmlsoap.org/soap/encoding/");

        SOAPBody bodyElement = envelopeElement.getBody();
        long classId = requestObject.classId;
        KrnClass requestObjectClass = session.getClassById(classId);
        KrnAttribute[] attrs = session.getAttributes(requestObjectClass);
        if (attrs.length > 0) {
            String attrName = attrs[0].name;
            KrnObject rootObj = session.getObjectsSingular(requestObject.id, attrs[0].id, false);
            if (rootObj != null) {
                SOAPElement element = bodyElement.addChildElement(attrName);
                getData(rootObj, element, session);
            }
            
        }
        return requestMessage;
    }
    
    private void getData(KrnObject krnObject, SOAPElement element, Session session) throws KrnException, SOAPException {
        KrnAttribute[] attrs = session.getAttributes(session.getClassById(krnObject.classId));
        for (int i = 0; i < attrs.length; i++) {
            KrnAttribute attr = attrs[i];
            String name = attr.name;
            if ("deleting".equals(name) || "creating".equals(name)) continue;
            long typeClsId = attr.typeClassId;
            SOAPElement child = element.addChildElement(name);
            if (id_typeMap.containsKey(typeClsId)) {
                String typeClsName = id_typeMap.get(typeClsId);
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
    
    // Метод извлекает название и наймспейс объекта
    private List<String> extractQName(KrnObject object, Session session) {
        try {
            KrnClass objectClass = session.getClassById(object.classId);
            KrnAttribute nameAttr = session.getAttributeByName(objectClass, "name");
            KrnObject[] qNameObjs = session.getObjects(object.id, nameAttr.id, new long[0], 0);
            if (qNameObjs != null && qNameObjs.length > 0) {
                KrnObject qNameObj = qNameObjs[0];
                KrnClass qNameClass = session.getClassByName("QName");
                KrnAttribute localNameAttr = session.getAttributeByName(qNameClass, "localName");
                KrnAttribute namespaceAttr = session.getAttributeByName(qNameClass, "namespace");
                String localname = session.getStringsSingular(qNameObj.id, localNameAttr.id, 0, false, false);
                String namespace = session.getStringsSingular(qNameObj.id, namespaceAttr.id, 0, false, false);
                return Arrays.asList(localname, namespace);
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setQName(KrnObject obj, QName name, Session s) throws KrnException {
        KrnClass tc = s.getClassById(obj.classId);
        KrnClass qnc = s.getClassByName("QName");
        KrnAttribute nameAttr = s.getAttributeByName(tc, "name");
        KrnAttribute qlnAttr = s.getAttributeByName(qnc, "localName");
        KrnAttribute qnsAttr = s.getAttributeByName(qnc, "namespace");

        KrnObject qno = s.createObject(qnc, 0);
        s.setString(qno.id, qlnAttr.id, 0, 0, false, name.getLocalPart(), 0);
        s.setString(qno.id, qnsAttr.id, 0, 0, false, name.getNamespaceURI(), 0);

        s.setObject(obj.id, nameAttr.id, 0, qno.id, 0, true);
    }

    private KrnObject getType(String name, String ns, Session s) {
        return getObject("WSType", ns, name, s);
    }

    private KrnObject getElement(String name, String ns, Session s) {
        return getObject("WSElement", ns, name, s);
    }

    private KrnObject getObject(String clsName, String uri, String name, Session s) {
        try {
            KrnClass wsc = s.getClassByName(clsName);
            KrnClass qnc = s.getClassByName("QName");
            KrnAttribute nameAttr = s.getAttributeByName(wsc, "name");
            KrnAttribute qlnAttr = s.getAttributeByName(qnc, "localName");
            KrnAttribute qnsAttr = s.getAttributeByName(qnc, "namespace");

            KrnObject[] objs1 = s.getObjectsByAttribute(qnc.id, qlnAttr.id, 0, 0, name, 0);
            KrnObject[] objs2 = s.getObjectsByAttribute(qnc.id, qnsAttr.id, 0, 0, uri, 0);
            KrnObject res = null;
            for (int i = 0; i < objs1.length; i++) {
                KrnObject obj1 = objs1[i];
                for (int j = 0; j < objs2.length; j++) {
                    KrnObject obj2 = objs2[j];
                    if (obj1.id == obj2.id) {
                        KrnObject[] objs = s.getObjectsByAttribute(wsc.id, nameAttr.id,
                                0, 0, obj1.id, 0);
                        if (objs != null && objs.length > 0)
                            res = objs[0];
                    }
                }
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}