package com.cifs.or2.server.orlang;

import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.GlobalFuncs;
import com.cifs.or2.server.Session;

import kz.tamur.SecurityContextHolder;
import kz.tamur.lang.XmlOp;
import kz.tamur.rt.Utils;

import org.jdom.Element;
import org.jdom.CDATA;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.*;
import java.util.*;

import javax.swing.text.Document;
import javax.swing.text.BadLocationException;
import javax.swing.text.rtf.RTFEditorKit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by IntelliJ IDEA.
 * Date: 12.05.2005
 * Time: 12:17:34
 * 
 * @author Vale
 */
public class SrvXml extends XmlOp {

    /** Сесиия пользователя. */
    private Session s;

    /**
     * Конструктор класса SrvXml.
     * 
     * @param s
     *            Сесиия пользователя
     * @param log
     *            the log
     */
    public SrvXml(Session s) {
        this.s = s;
    }

    /**
     * TODO Описать метод
     * Создать xml-элемент.
     * 
     * @param name
     *            имя элемента.
     * @param data
     *            данные элемента.
     * @return xml-элемент.
     * @throws Exception
     *             the exception
     */
    public Element createElement(String name, Object data) throws Exception {
        return createElement(name, name, data);
    }

    /**
     * TODO Описать метод
     * Создать xml-элемент.
     * 
     * @param name
     *            имя элемента.
     * @param tagName
     *            имя тега элемента.
     * @param data
     *            данные элемента.
     * @return xml-элемент.
     * @throws Exception
     *             the exception
     */
    public Element createElement(String name, String tagName, Object data) throws Exception {
        GlobalFuncs gf = new GlobalFuncs();
        String expr = gf.getText(name, s);
        if (expr != null) {
            SrvOrLang orLang = new SrvOrLang(s);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("OBJ", data);
            vc.put("NAME", tagName);
            try {
                orLang.evaluate(expr, vc, null, new Stack<String>());
            } catch (Exception e) {
                SecurityContextHolder.getLog().info("$Xml.createElement(\"" + name + "\",\"" + tagName + "\", data)");
                SecurityContextHolder.getLog().error(e, e);
                throw e;
            }
            return (Element) vc.get("RETURN");
        }
        return null;
    }

    /**
     * TODO Описать метод
     * Парсинг xml-элемента.
     * 
     * @param name
     *            имя элемента.
     * @param e
     *            xml-элемент.
     * @param obj
     *            данные для элемента.
     * @throws Exception
     *             the exception
     */
    public void parseElement(String name, Element e, KrnObject obj) throws Exception {
        GlobalFuncs gf = new GlobalFuncs();
        String expr = gf.getText(name, s);
        if (expr != null) {
            SrvOrLang orLang = new SrvOrLang(s);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("OBJ", obj);
            vc.put("XML", e);
            try {
                orLang.evaluate(expr, vc, null, new Stack<String>());
            } catch (Exception ex) {
                SecurityContextHolder.getLog().info("$Xml.parseElement(\"" + name + "\", element, object)");
                SecurityContextHolder.getLog().error(ex, ex);
                throw ex;
            }
        }
    }

    /**
     * TODO Описать метод
     * Парсинг xml-элемента.
     * 
     * @param name
     *            имя элемента.
     * @param e
     *            xml-элемент.
     * @return krn object
     * @throws Exception
     *             the exception
     */
    public KrnObject parseElement(String name, Element e) throws Exception {
        GlobalFuncs gf = new GlobalFuncs();
        String expr = gf.getText(name, s);
        if (expr != null) {
            SrvOrLang orLang = new SrvOrLang(s);
            Map<String, Object> vc = new HashMap<String, Object>();
            vc.put("XML", e);
            try {
                orLang.evaluate(expr, vc, null, new Stack<String>());
                return (KrnObject) vc.get("RETURN");
            } catch (Exception ex) {
                SecurityContextHolder.getLog().info("$Xml.parseElement(\"" + name + "\", element, object)");
                SecurityContextHolder.getLog().error(ex, ex);
                throw ex;
            }
        }
        return null;
    }

    /**
     * TODO Описать метод
     * Преобразование описания.
     * 
     * @param e
     *            xml-элемент.
     * @throws Exception
     *             the exception
     */
    public void transformDescription(Element e) throws Exception {
        if (e.getContentSize() > 0 && e.getContent(0) instanceof CDATA) {
            return;
        }
        String str = e.getText();
        byte[] bs = (str.length() > 0) ? Utils.decodeImage(str) : null;
        if (bs != null && bs.length > 0) {
            Document doc = Utils.processCreateDocument(bs);
            if (doc != null) {
                try {
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    RTFEditorKit kit = new RTFEditorKit();
                    kit.write(os, doc, 0, doc.getLength());
                    os.close();
                    String value = os.toString();
                    CDATA cdata = new CDATA(value);
                    e.setText("");
                    e.addContent(cdata);
                } catch (BadLocationException e1) {
                    SecurityContextHolder.getLog().error(e1, e1);
                    throw e1;
                } catch (IOException e1) {
                    SecurityContextHolder.getLog().error(e1, e1);
                    throw e1;
                }
            }
        }
    }
    
    public List<File> getXMLFilesByTagAndValue(String sourcePath, String tagName, String tagValue) {
    	List<File> res = new ArrayList<>();
    	File source = new File(sourcePath);
    	if (source.exists()) {
    		File[] files = source.listFiles();
    		for(File file: files) {
    			try {
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					org.w3c.dom.Document document = db.parse(file);
					NodeList nodes = document.getElementsByTagName(tagName);
					for(int i = 0; i < nodes.getLength(); i++) {
						Node node = nodes.item(i);
						if (tagValue.equals(node.getTextContent())) {
							res.add(file);
							break;
						}
					}
				} catch (ParserConfigurationException | SAXException | IOException e) {
					e.printStackTrace();
				}
    		}
    	}
    	return res;
    }
}