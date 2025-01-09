package kz.tamur.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import kz.tamur.or3ee.common.UserSession;

public class XmlUtil {
    
	private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + XmlUtil.class.getName());

	private XmlUtil() {
	}

	public static Element read(byte[] data) throws IOException, JDOMException {
		try {
			SAXBuilder builder = kz.tamur.util.crypto.XmlUtil.createSaxBuilder();
			Document doc = builder.build(new ByteArrayInputStream(Funcs.normalizeInput(data, "UTF-8")), "UTF-8");
			return doc.getRootElement();
		} catch (JDOMException e) {
			log.error(new String(data, "UTF-8"));
			throw e;
		} catch (IOException e) {
			log.error(new String(data, "UTF-8"));
			throw e;
		}
	}
	
	public static byte[] write(Element xml) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        write(xml, os);
        os.close();
        return os.toByteArray();
	}

	public static void write(Element xml, OutputStream os) throws IOException {
        XMLOutputter out = new XMLOutputter();
        out.getFormat().setEncoding("UTF-8");
        out.output(xml, os);
	}

	public static byte[] writePretty(Element xml) throws IOException {
		Format format = Format.getPrettyFormat();
		format.setEncoding("UTF-8");
        XMLOutputter out = new XMLOutputter(format);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        out.output(xml, os);
        os.close();
        return os.toByteArray();
	}
}
