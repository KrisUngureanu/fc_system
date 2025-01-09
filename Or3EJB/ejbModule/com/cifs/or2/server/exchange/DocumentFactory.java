package com.cifs.or2.server.exchange;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.server.Session;
import org.xml.sax.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.WeakHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 12.12.2003
 * Time: 17:56:55
 * To change this template use Options | File Templates.
 */
public class DocumentFactory {
    private static DocumentFactory instance;
    private static Map factories = new HashMap();
    private static Map xmlObjects = new WeakHashMap();

    private Session session;

    public static synchronized DocumentFactory instance(Session session) {
        if (instance == null) {
            instance = new DocumentFactory();
        }
        instance.session = session;
        return instance;
    }

    private DocumentFactory() {
        // Private ctor
        // Регистрация всех XML объектов
        //registerFactory("Box", Box.getFactory());
        registerFactory("Doc", Doc.getFactory());
        registerFactory("XmlOption", XmlOption.getFactory());
    }

    public synchronized void registerFactory(String element,
                                             XmlFactory factory) {
        factories.put(element, factory);
    }

    public synchronized void load(XmlObject xo, int tid)
            throws KrnException, UnsupportedEncodingException {
        KrnClass cls = session.getClassByName("Объект");
        KrnAttribute xcAttr = session.getAttributeByName(cls, "config");
        int objId = Integer.parseInt(xo.getId());

        byte[] data = session.getBlob(objId, xcAttr.id, 0, 0, tid);

        if (data.length > 0) {
            String xml = new String(data, "UTF-8");
            SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            parserFactory.setValidating(false);
            XMLReader xmlReader = null;
            SAXParser saxParser = null;
            try {
                saxParser = parserFactory.newSAXParser();
                xmlReader = saxParser.getXMLReader();
                xmlReader.setContentHandler(new XmlContentHandler(xo, this, tid));
                xmlReader.parse(new InputSource(new StringReader(xml)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public XmlObject createObject(String name, String id, int tid) {
        XmlObject xo = null;
        if (id != null) {
            xo = (XmlObject)xmlObjects.get(id);
            if (xo == null) {
                xo = createNewObject(name, id);
                try {
                    DocumentFactory df = new DocumentFactory();
                    df.session = session;
                    df.load(xo, tid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                xmlObjects.put(id, xo);
            }
        } else {
            xo = createNewObject(name, id);
        }
        return xo;
    }

    private XmlObject createNewObject(String name, String id) {
        XmlObject xo = null;
        XmlFactory factory = (XmlFactory)factories.get(name);
        if (factory != null) {
            xo = factory.createObject(name, id);
        } else {
            xo = new XmlObject(name, id);
        }
        return xo;
    }

    private static class XmlContentHandler implements ContentHandler {
        private DocumentFactory df;
        private Stack contextStack = new Stack();
        private Stack attributeStack = new Stack();
        private String currAttributeName;
        private XmlObject currContext;
        private StringBuffer buffer;
        private boolean isAttribute;
        private int tid;

        public XmlContentHandler(XmlObject xo, DocumentFactory df, int tid) {
            this.df = df;
            currContext = xo;
            currAttributeName = null;
            isAttribute = false;
            this.tid = tid;
        }

        public void setDocumentLocator(Locator locator) {
        }

        public void startDocument() throws SAXException {
        }

        public void endDocument() throws SAXException {
        }

        public void startPrefixMapping(String s, String s1)
                throws SAXException {
        }

        public void endPrefixMapping(String s) throws SAXException {
        }

        public void startElement(String uri, String localName, String qName,
                                 Attributes attrs) throws SAXException {
            if (!isAttribute) {
                XmlObject newObject = currContext;
                if (currAttributeName != null) {
                    newObject = df.createObject(qName, attrs.getValue("id"), tid);
                    currContext.setAttribute(currAttributeName, newObject, -1);
                    contextStack.push(currContext);
                    currContext = newObject;
                }
            } else {
                attributeStack.push(currAttributeName);
                currAttributeName = qName;
            }
            buffer = null;
            isAttribute = !isAttribute;
        }

        public void endElement(String uri, String localName, String qName)
                throws SAXException {
            if (!isAttribute) {
                if (buffer != null
                        && currContext.getAttribute(currAttributeName).isEmpty()) {
                    currContext.setAttribute(currAttributeName, buffer.toString(), 0);
                }
                currAttributeName = (String)attributeStack.pop();
            } else {
                if (currAttributeName != null) {
                    currContext = (XmlObject)contextStack.pop();
                }
            }
            isAttribute = !isAttribute;
        }

        public void characters(char[] chars, int off, int len)
                throws SAXException {
            if (!isAttribute) {
                if (buffer == null) {
                    buffer = new StringBuffer();
                }
                buffer.append(chars, off, len);
            }
        }

        public void ignorableWhitespace(char[] chars, int i, int i1)
                throws SAXException {
        }

        public void processingInstruction(String s, String s1)
                throws SAXException {
        }

        public void skippedEntity(String s) throws SAXException {
        }
        // end ContentHandler
    }
}
