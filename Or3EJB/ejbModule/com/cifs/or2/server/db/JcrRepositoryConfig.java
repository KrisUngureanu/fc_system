package com.cifs.or2.server.db;

import java.io.ByteArrayInputStream;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.commons.JcrUtils;
import org.jdom.Element;

import kz.tamur.DriverException;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;

public class JcrRepositoryConfig {
	
	private Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + JcrRepositoryConfig.class.getName());

	private String name;
	private String conn;
	private String readConn;
	private Repository writeJcrRepository;
	private Repository readJcrRepository;
	private String user;
	private char[] pd;
	private Set<Long> attrIds = new HashSet<Long>();
	
	private boolean jcrReadOnly = false;
	
	private static final Pattern uuidPattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
	
	public JcrRepositoryConfig(Element xml) throws DriverException {
		String jndi = xml.getAttributeValue("jndi");
		String url = xml.getAttributeValue("url");

		name = xml.getAttributeValue("name");
		user = xml.getAttributeValue("user");
		pd = xml.getAttributeValue("password").toCharArray();
		
		if (jndi != null && jndi.trim().length() > 0 && jndi.length() < 100) {
			conn = jndi = Funcs.sanitizeUsername(jndi).trim();
			try {
				InitialContext ic = new InitialContext();
				writeJcrRepository = (Repository)ic.lookup(jndi);
			} catch (Exception e) {
				throw new DriverException("Ошибка соединения с JCR-репозитарием. " + name, 0, e);
			}
			for (Element attrXml : (List<Element>)xml.getChildren("attr")) {
				attrIds.add(Long.valueOf(attrXml.getAttributeValue("id")));
			}
		} else if (url != null && url.trim().length() > 0 && url.length() < 100) {
			conn = url = Funcs.sanitizeUsername(url).trim();
			try {
				writeJcrRepository = JcrUtils.getRepository(url);
			} catch (Exception e) {
				throw new DriverException("Ошибка соединения с JCR-репозитарием. " + name, 0, e);
			}
			for (Element attrXml : (List<Element>)xml.getChildren("attr")) {
				attrIds.add(Long.valueOf(attrXml.getAttributeValue("id")));
			}
		}
		
		String readJndi = xml.getAttributeValue("jndi-read");
		String readUrl = xml.getAttributeValue("url-read");

		if (readJndi != null && readJndi.trim().length() > 0 && readJndi.length() < 50) {
			readConn = readJndi = Funcs.sanitizeUsername(readJndi).trim();
			try {
				InitialContext ic = new InitialContext();
				readJcrRepository = (Repository)ic.lookup(readJndi);
			} catch (Exception e) {
				throw new DriverException("Ошибка соединения с JCR-репозитарием. " + name, 0, e);
			}
			for (Element attrXml : (List<Element>)xml.getChildren("attr")) {
				attrIds.add(Long.valueOf(attrXml.getAttributeValue("id")));
			}
		} else if (readUrl != null && readUrl.trim().length() > 0 && readUrl.length() < 50) {
			readConn = readUrl = Funcs.sanitizeUsername(readUrl).trim();
			try {
				readJcrRepository = JcrUtils.getRepository(readUrl);
			} catch (Exception e) {
				throw new DriverException("Ошибка соединения с JCR-репозитарием. " + name, 0, e);
			}
			for (Element attrXml : (List<Element>)xml.getChildren("attr")) {
				attrIds.add(Long.valueOf(attrXml.getAttributeValue("id")));
			}
			
		}
	}
	
	public Set<Long> getAttrIds() {
		return Collections.unmodifiableSet(attrIds);
	}
	
	public Session getSession(boolean readOnly) throws DriverException {
		SimpleCredentials cred = new SimpleCredentials(user, pd);
		try {
			if (readOnly && readJcrRepository != null)
				return readJcrRepository.login(cred);
			else
				return writeJcrRepository.login(cred);
		} catch (Exception e) {
			throw new DriverException("Ошибка при создании JCR-сессии.", 0, e);
		}
	}

	public byte[] getRepositoryData(String docId) throws DriverException {
		if (uuidPattern.matcher(docId).matches()) {
			log.info("getRepositoryData: " + name + ", conn: " + getConn(true) + ", docId: " + docId);
			javax.jcr.Session jcrSession = getSession(true);
			try {
				Node fileNode = jcrSession.getNodeByIdentifier(docId);
				Node contentNode = fileNode.getNode(Property.JCR_CONTENT);
				Binary binary = contentNode.getProperty(Property.JCR_DATA).getBinary();
				byte[] buffer = new byte[(int)binary.getSize()];
				binary.read(buffer, 0);
				binary.dispose();
				return buffer;
			} catch (Exception e) {
				throw new DriverException("Ошибка при получении данных из JCR-репозитария.", 0, e);
			} finally {
				jcrSession.logout();
			}
		}
		return docId.getBytes();
	}

	public String putRepositoryData(String fileName, byte[] data) throws DriverException {
		javax.jcr.Session jcrSession = getSession(false);
		try {
			log.info("putRepositoryData: " + name + ", conn: " + getConn(false) + ", name: " + fileName);

			Calendar c = GregorianCalendar.getInstance();
			String path = String.format("/%d/%02d/%02d/%02d/%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));

			Node folder = JcrUtils.getOrCreateByPath(path, "nt:folder", jcrSession);
			Node fileNode = JcrUtils.getOrAddNode(folder, fileName, "nt:file");
	        Node resNode = JcrUtils.getOrAddNode(fileNode, Property.JCR_CONTENT, "nt:resource");
	        resNode.setProperty(Property.JCR_MIMETYPE, getMediaType(fileName));
	        resNode.setProperty(Property.JCR_ENCODING, "UTF-8");
	        Binary binary = jcrSession.getValueFactory().createBinary(new ByteArrayInputStream(data));
	        resNode.setProperty(Property.JCR_DATA, jcrSession.getValueFactory().createValue(binary));
	        resNode.setProperty (Property.JCR_LAST_MODIFIED, c);
	        jcrSession.save();
	        return fileNode.getIdentifier();
		} catch (Exception e) {
			throw new DriverException("Ошибка при сохранении данных в JCR-репозитарии.", 0, e);
		} finally {
			jcrSession.logout();
		}
	}

	public String deleteRepositoryData(String docId) throws DriverException{
		log.info("deleteRepositoryData: " + name + ", conn: " + getConn(false) + ", docId: " + docId);
		
		javax.jcr.Session jcrSession = getSession(false);
		try {
			Node fileNode=null;
			if (docId.contains("/")) {
				fileNode = jcrSession.getNode(docId.indexOf("/")>0 ? "/" + docId : docId);
			} else
				fileNode = jcrSession.getNodeByIdentifier(docId);
			
			if (fileNode != null) {
				String name = fileNode.getName();
				fileNode.remove();
		        jcrSession.save();
				return name;
			}
		} catch (Exception e) {
			throw new DriverException("Ошибка при удалении данных из JCR-репозитария.", 0, e);
		} finally {
			jcrSession.logout();
		}
		return null;
	}

	private static String getMediaType(String fileName) {
		if (fileName != null) {
			int p = fileName.lastIndexOf('.');
			if (p != -1) {
				String ext = fileName.substring(p + 1).toLowerCase();
				
				if ("doc".equals(ext)) {
					return "application/msword";
				
				} else if ("docx".equals(ext)) {
					return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

				} else if ("xls".equals(ext)) {
					return "application/vnd.ms-excel";

				} else if ("xlsx".equals(ext)) {
					return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

				} else if ("ppt".equals(ext)) {
					return "application/vnd.ms-powerpoint";

				} else if ("pptx".equals(ext)) {
					return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
				
				} else if ("txt".equals(ext)) {
					return "text/plain";
				
				} else if ("pdf".equals(ext)) {
					return "application/pdf";
				
				} else if ("jpg".equals(ext)) {
					return "image/jpeg";
				
				}
			}
		}
		return "application/octet-stream";
	}

	public String getName() {
		return name;
	}

	public String getUser() {
		return user;
	}

	public String getConn(boolean readOnly) {
		if (readOnly && readConn != null)
			return readConn;
		else
			return conn;
	}

	public boolean isReadOnly() {
		return jcrReadOnly;
	}

	public void setReadOnly(boolean jcrReadOnly) {
		this.jcrReadOnly = jcrReadOnly;
	}

}
