package kz.tamur.lang;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.jdom.xpath.XPath;

import kz.tamur.comps.Constants;
import kz.tamur.util.Funcs;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.List;

/**
 * The Class XmlOp.
 * 
 * @author Berik
 */
public abstract class XmlOp {

    /**
     * Создать пространство имён.
     * 
     * Пример:
     * 
     * <pre>
     * // создаем пространство имен ecm_or3
     * #set($nsCt = $Xml.createNameSpace("ct", "http://www.tamur.kz/ecm_or3"))
     * 
     * // создаем корневой Тег документа
     * #set($tagDoc = $Xml.createElement("Doc", $nsCt))
     * </pre>
     * 
     * 
     * @param префикс
     *            пространства.
     * @param uri
     *            путь к пространству.
     * @return готовое пространство имён.
     */
    public Namespace createNameSpace(String prefix, String uri) {
        return Namespace.getNamespace(prefix, uri);
    }

    /**
     * Создать xml-элемент с заданным именем <code>name</code>.
     * 
     * Пример:
     * 
     * <pre>
     * // создаем пространство имен ecm_or3
     * #set($nsCt = $Xml.createNameSpace("ct", "http://www.tamur.kz/ecm_or3"))
     * 
     * // создаем корневой Тег документа
     * #set($tagDoc = $Xml.createElement("Doc", $nsCt))
     * </pre>
     * 
     * @param name
     *            имя элемента.
     * @return xml-елемент.
     */
    public Element createElement(String name) {
        return new Element(name);
    }

    /**
     * Создать xml-элемент с заданным именем <code>name</code> в определённом пространстве имён.
     * 
     * Пример:
     * 
     * <pre>
     * // создаем пространство имен ecm_or3
     * #set($nsCt = $Xml.createNameSpace("ct", "http://www.tamur.kz/ecm_or3"))
     * 
     * // создаем корневой Тег документа
     * #set($tagDoc = $Xml.createElement("Doc", $nsCt))
     * </pre>
     * 
     * @param name
     *            имя элемента.
     * @param ns
     *            пространство имён.
     * @return xml-елемент.
     */
    public Element createElement(String name, Namespace ns) {
        return new Element(name, ns);
    }

    /**
     * Создать xml-документ с заданным корневым элементом.
     * 
     * @param root
     *            корневой элемент
     * @return xml-документ.
     */
    public Document createDocument(Element root) {
        return new Document(root);
    }

    /**
     * Записать файл в заданный xml-документ.
     * 
     * @param e
     *            xml-документ, куда производится запись.
     * @param fileName
     *            полное имя файла для записи.
     * @throws Exception
     *             the exception
     */
    public void write(Element e, String fileName) throws Exception {
        FileOutputStream os = new FileOutputStream(Funcs.getCanonicalFile(fileName));
        XMLOutputter out = new XMLOutputter();
        out.getFormat().setEncoding("UTF-8");
        out.output(e, os);
        os.close();
    }

    /**
     * Загрузить файл и получить из него корневой xml-элемент.
     * 
     * @param fileName
     *            полное имя файла для чтения.
     * @return xml-элемент.
     * @throws Exception
     *             the exception
     */
    public Element load(String fileName) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(Funcs.getCanonicalFile(fileName));
        return doc.getRootElement();
    }

    /**
     * Загрузить xml-файл и получить из него корневой xml-элемент.
     * 
     * @param file
     *            файл для чтения.
     * @return xml-элемент.
     * @throws Exception
     *             the exception
     */
    public Element load(File file) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(Funcs.getCanonicalFile(file));
        return doc.getRootElement();
    }

    /**
     * Загрузить массив байт и получить из него корневой xml-элемент.
     * 
     * @param buf
     *            массив байт для чтения.
     * @return xml-элемент.
     * @throws Exception
     *             the exception
     */
    public Element load(byte[] buf) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new ByteArrayInputStream(buf), "UTF-8");
        return doc.getRootElement();
    }

    /**
     * Загрузить поток и получить из него корневой xml-элемент.
     * 
     * @param is
     *            поток для чтения.
     * @return xml-элемент.
     * @throws Exception
     *             the exception
     */
    public Element load(InputStream is) throws Exception {
        if (is != null) {
            byte[] buf = Funcs.readStream(is, Constants.MAX_DOC_SIZE);
            is.close();
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new ByteArrayInputStream(buf), "UTF-8");
            return doc.getRootElement();
        }
        return null;
    }

    /**
     * TODO Описать метод
     * Transform.
     * 
     * @param src
     *            the src
     * @param xsltFileName
     *            the xslt file name
     * @return element
     * @throws Exception
     *             the exception
     */
    public Element transform(Element src, String xsltFileName) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Templates stylesheet = transformerFactory.newTemplates(new StreamSource(new FileReader(xsltFileName)));
        Transformer processor = stylesheet.newTransformer();
        JDOMResult res = new JDOMResult();
        processor.transform(new JDOMSource(src), res);
        return res.getDocument().getRootElement();
    }

    /**
     * Выбрать узлы по заданному имени в <code>path</code>.
     * 
     * @param context
     *            узел в котором будет происходить поиск.
     * @param path
     *            имя узла для поиска
     * 
     * @return список найденных узлов.
     * @throws Exception
     *             the exception
     */
    public List selectNodes(Element context, String path) throws Exception {
        return XPath.selectNodes(context, path);
    }

    /**
     * Выбрать единственный узел по заданному имени в <code>path</code>.
     * 
     * @param context
     *            узел в котором будет происходить поиск.
     * @param path
     *            имя узла для поиска.
     * 
     * @return найденный узел.
     * @throws Exception
     *             the exception
     */
    public Object selectSingleNode(Element context, String path) throws Exception {
        return XPath.selectSingleNode(context, path);
    }

    /**
     * Получить значение указанного заданного узла.
     * 
     * @param context
     *            узел в котором будет происходить поиск.
     * @param path
     *            имя узла для выборки значения.
     * @return полученное значение.
     * @throws Exception
     *             the exception
     */
    public String valueOf(Element context, String path) throws Exception {
        XPath xp = XPath.newInstance(path);
        return xp.valueOf(context);
    }

    /**
     * Конвертация xml-элемента в форматированное текстовое представление.
     * 
     * @param xml
     *            xml-элемент
     * @return полученная строка
     * @throws Exception
     *             the exception
     */
    public String xmlToPrettyString(Element xml) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Format ft = Format.getPrettyFormat();
        ft.setEncoding("UTF-8");
        XMLOutputter out = new XMLOutputter(ft);
        out.output(new Document(xml), os);
        os.close();
        return os.toString("UTF-8");
    }

    /**
     * Конвертация xml-элемента в текстовое представление..
     * 
     * @param xml
     *            xml-элемент
     * @return полученная строка
     * @throws Exception
     *             the exception
     */
    public String xmlToString(Element xml) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLOutputter out = new XMLOutputter();
        out.getFormat().setEncoding("UTF-8");
        out.output(xml, os);
        os.close();
        return os.toString("UTF-8");
    }

    /**
     * Создать элемент класса <code>CDATA</code> из xml-элемента.
     * 
     * @param xml
     *            xml-элемент для чтения.
     * @return элемент класса <code>CDATA</code>
     * @throws Exception
     *             the exception
     */
    public CDATA xmlToCDATA(Element xml) throws Exception {
        CharArrayWriter w = new CharArrayWriter();
        XMLOutputter opr = new XMLOutputter();
        opr.getFormat().setEncoding("UTF-8");
        opr.output(xml, w);
        w.close();
        CDATA res = new CDATA(w.toString());
        return res;
    }

    /**
     * Получить xml-элемент из переданного объекта <code>CDATA</code>.
     * 
     * @param var
     *            объект для чтения.
     * @return xml-элемент.
     * @throws Exception
     *             the exception
     */
    public Element xmlFromCDATA(CDATA var) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(var.getText()));
        Element res = doc.getRootElement();
        return res;
    }
}
