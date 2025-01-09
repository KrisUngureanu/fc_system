package com.cifs.or2.server.db;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.rmi.repository.URLRemoteRepository;

import kz.tamur.DriverException;
public class SrvJcrRepository{
	private static Log log = LogFactory.getLog(SrvJcrRepository.class);
	private Repository jcrRepository;
	private Node rootNode;
	private Session jcrSession;
	private static final String rmiJcrUrl=System.getProperty("rmiJcrUrl");
	private static final String login=System.getProperty("rmiJcrUsr","admin");
	private static final String pwd=System.getProperty("rmiJcrPwd","admin");
	
	public SrvJcrRepository(){
		if(rmiJcrUrl==null) return;
		SimpleCredentials cred = new SimpleCredentials(login, pwd.toCharArray());
		try {
				jcrRepository = new URLRemoteRepository(rmiJcrUrl/*"http://192.168.13.77:8886/rmi"*/);
				jcrSession = jcrRepository.login(cred);
				rootNode = jcrSession.getRootNode();
			} catch (Exception e) {
				log.error(e.getMessage());
		}
	}

	public byte[] getRepositoryData(String docId) throws DriverException {
		try {
			Node fileNode=null;
			if(docId.contains("/")){
				fileNode = jcrSession.getNode(docId.indexOf("/")>0?"/"+docId:docId);
			}else
				fileNode = jcrSession.getNodeByIdentifier(docId);
			Node contentNode = fileNode.getNode(Property.JCR_CONTENT);
			Binary binary = contentNode.getProperty(Property.JCR_DATA).getBinary();
			byte[] buffer = new byte[(int)binary.getSize()];
			binary.read(buffer, 0);
			binary.dispose();
			return buffer;
		} catch (Exception e) {
			if(jcrSession==null)
				log.error(e.getMessage());
			else				
				throw new DriverException("Ошибка при получении данных из JCR-репозитария.", 0, e);
		} finally {
			if(jcrSession!=null)
				jcrSession.logout();
		}
		return null;
	}

	public String putRepositoryData(String paths,String fileName, byte[] data) throws DriverException {
		try {
			Node parent=rootNode;
			if(paths!=null && !"".equals(paths)){
				String[] folders=paths.split("/");
				Node folder=rootNode;
				for(String path:folders){
					folder = JcrUtils.getOrAddFolder(parent, path);
					parent=folder;
				}
			}
			Node fileNode = JcrUtils.getOrAddNode(parent, fileName, "nt:file");
	        Node resNode = JcrUtils.getOrAddNode(fileNode, Property.JCR_CONTENT, "nt:resource");
	        resNode.setProperty(Property.JCR_MIMETYPE, getMediaType(fileName));
	        resNode.setProperty(Property.JCR_ENCODING, "UTF-8");
	        Binary binary = jcrSession.getValueFactory().createBinary(new ByteArrayInputStream(data));
	        resNode.setProperty(Property.JCR_DATA, jcrSession.getValueFactory().createValue(binary));
	        resNode.setProperty (Property.JCR_LAST_MODIFIED, Calendar.getInstance());
	        jcrSession.save();
	        return fileNode.getIdentifier();
		} catch (Exception e) {
			if(jcrSession==null)
				log.error(e.getMessage());
			else				
				throw new DriverException("Ошибка при записи данных в JCR-репозитарий.", 0, e);
		} finally {
			if(jcrSession!=null)
				jcrSession.logout();
		}
		return null;
	}
	
	public boolean dropRepositoryItem(String docId) throws DriverException{
		try{
			Node fileNode=null;
			if(docId.contains("/")){
				fileNode = jcrSession.getNode(docId.indexOf("/")>0?"/"+docId:docId);
			}else
				fileNode = jcrSession.getNodeByIdentifier(docId);
			if(fileNode!=null){
				/*Node contentNode = fileNode.getNode(Property.JCR_CONTENT);
				if(contentNode!=null)
					contentNode.remove();*/
				fileNode.remove();
		        jcrSession.save();
				return true;
			}
		} catch (Exception e) {
			if(jcrSession==null)
				log.error(e.getMessage());
			else				
				throw new DriverException("Ошибка при получении данных из JCR-репозитария.", 0, e);
		} finally {
			if(jcrSession!=null)
				jcrSession.logout();
		}
		return false;
	}
	
	public String getRepositoryItemType(String docId) throws DriverException{
		try{
			Node fileNode=null;
			if(docId.contains("/")){
				fileNode = jcrSession.getNode(docId.indexOf("/")>0?"/"+docId:docId);
			}else
				fileNode = jcrSession.getNodeByIdentifier(docId);
			if(fileNode!=null){
				Node contentNode = fileNode.getNode(Property.JCR_CONTENT);
				if(contentNode!=null){
					return contentNode.getProperty(Property.JCR_MIMETYPE).getString();
				}
			}
		} catch (Exception e) {
			if(jcrSession==null)
				log.error(e.getMessage());
			else				
				throw new DriverException("Ошибка при получении данных из JCR-репозитария.", 0, e);
		} finally {
			if(jcrSession!=null)
				jcrSession.logout();
		}
		return null;
	}
	public String getRepositoryItemName(String docId) throws DriverException{
		try{
			Node fileNode=null;
			if(docId.contains("/")){
				fileNode = jcrSession.getNode(docId.indexOf("/")>0?"/"+docId:docId);
			}else
				fileNode = jcrSession.getNodeByIdentifier(docId);
			if(fileNode!=null){
				return fileNode.getName();
			}
		} catch (Exception e) {
			if(jcrSession==null)
				log.error(e.getMessage());
			else				
				throw new DriverException("Ошибка при получении данных из JCR-репозитария.", 0, e);
		} finally {
			if(jcrSession!=null)
				jcrSession.logout();
		}
		return null;
	}
	public List<String> searchByQuery(String sqlExpr) throws DriverException{
		List<String> res=new ArrayList<>();
		 try {
		        // Obtain the query manager for the session via the workspace ...
		        QueryManager queryManager = jcrSession.getWorkspace().getQueryManager();

		        // Create a query object ...
		        sqlExpr = "SELECT * FROM [nt:base]" /*AS s WHERE ISDESCENDANTNODE(s, [/Xml/new/test])"*/;
		        sqlExpr = "SELECT r.[jcr:content] AS rc FROM [nt:resource] AS r WHERE  CONTAINS(r.[jcr:content], '*1*')";
//		        sqlExpr = "SELECT * FROM [nt:resource]";
		        Query query = queryManager.createQuery(sqlExpr, Query.JCR_SQL2);
               QueryResult result = query.execute();
               String [] cns=result.getColumnNames();
               for (RowIterator rit = result.getRows(); rit.hasNext();) {
            	   Row row = (Row)rit.next();
            	   Value[] vs=row.getValues();
            	   Value vv=row.getValue("rc");
            	   for(Value v:vs){
            		   System.out.println("Value-"+v.getType()+";"+v);
            	   }
               }
               for (NodeIterator nit = result.getNodes(); nit.hasNext();) {
            	   Node node = nit.nextNode();
            	   System.out.println(node.getPath());
            	   System.out.println(node.getName());
            	   System.out.println(node.getIdentifier());
            	   res.add(node.getIdentifier());
               }
               return res;
			} catch (Exception e) {
				if(jcrSession==null)
					log.error(e.getMessage());
				else				
					throw new DriverException("Ошибка при получении данных из JCR-репозитария.", 0, e);
			} finally {
				if(jcrSession!=null)
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
	
}
