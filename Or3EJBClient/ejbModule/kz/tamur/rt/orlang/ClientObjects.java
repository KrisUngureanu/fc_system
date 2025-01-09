package kz.tamur.rt.orlang;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.Utils;
import com.cifs.or2.client.util.ClientQuery;
import com.cifs.or2.kernel.Activity;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.sun.mail.smtp.SMTPTransport;

import kz.tamur.SecurityContextHolder;
import kz.tamur.util.Pair;
import kz.tamur.lang.*;
import kz.tamur.rt.InterfaceManager;
import kz.tamur.rt.InterfaceManagerFactory;
import kz.tamur.rt.TaskTable;
import kz.tamur.rt.data.Cache;
import kz.tamur.rt.data.ObjectRecord;
import kz.tamur.rt.data.Record;
import kz.tamur.ods.ComparisonOperations;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Created by IntelliJ IDEA.
 * Date: 18.01.2005
 * Time: 15:34:54 To
 * 
 * @author berik
 */
public class ClientObjects extends kz.tamur.lang.Objects {

    /** log. */
    private static Log log = LogFactory.getLog(ClientObjects.class);

    
    @Override
    public KrnClass getClass(String name) {
        try {
            Kernel krn = Kernel.instance();
            return krn.getClassByName(name);
        } catch (KrnException e) {
            log.error(e, e);
        }
        return null;
    }

    
    @Override
    public List<KrnClass> getClasses(long baseClassId, boolean withSubclasses) {
        try {
            Kernel krn = Kernel.instance();
            return krn.getClasses(baseClassId, withSubclasses);
        } catch (KrnException e) {
            log.error(e, e);
        }
        return null;
    }

    
    @Override
    public List<KrnAttribute> getClassAttributes(String name) {
        try {
            Kernel krn = Kernel.instance();
            KrnClass cls = krn.getClassByName(name);
            return krn.getAttributes(cls);
        } catch (KrnException e) {
            log.error(e, e);
        }
        return null;
    }

    
    @Override
    public KrnClass getClassById(Number id) {
        try {
            Kernel krn = Kernel.instance();
            return krn.getClassNode(id.longValue()).getKrnClass();
        } catch (KrnException e) {
            log.error(e, e);
        }
        return null;
    }

    
    @Override
    public KrnAttribute getAttribute(KrnClass cls, String name) {
        try {
            Kernel krn = Kernel.instance();
            return krn.getAttributeByName(cls.getKrnClass(), name);
        } catch (KrnException e) {
            log.error(e, e);
        }
        return null;
    }

    
    @Override
    public KrnAttribute getAttributeById(Number id) {
        try {
            Kernel krn = Kernel.instance();
            return krn.getAttributeById(id.longValue());
        } catch (KrnException e) {
            log.error(e, e);
        }
        return null;
    }

    
    @Override
    public KrnObject createObject(String className) {
        try {
            InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();
            Kernel krn = Kernel.instance();
            KrnClass cls = krn.getClassByName(className);
            Cache cash = mgr.getCash();
            Record rec = cash.createObject(cls.id);
            return (KrnObject) rec.getValue();
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    @Override
    public KrnObject cloneObject(KrnObject obj) {
        return null;
    }

    
    @Override
    public KrnObject getObject(String uid) {
        try {
            InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();
            Cache cache = mgr.getCash();
            KrnObject obj = cache.getObjectByUid(uid);
            if (obj != null) {
                cache.findRecord(obj);
                return obj;
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    @Override
    public KrnObject getObject(Number id) {
        try {
            Kernel krn = Kernel.instance();
            KrnObject obj = krn.getCachedObjectById(id.longValue());
            if (obj != null) {
                InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();
                Cache cache = mgr.getCash();
                cache.findRecord(obj);
                return obj;
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    @Override
    public List<KrnObject> getClassObjects(String className) {
        try {
            InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();
            Kernel krn = Kernel.instance();
            KrnClass cls = krn.getClassByName(className);
            Cache cash = mgr.getCash();
            SortedSet<ObjectRecord> recs = cash.getObjects(cls.id, 0, null);
            List<KrnObject> res = new ArrayList<KrnObject>(recs.size());
            for (Record rec : recs) {
                res.add((KrnObject) rec.getValue());
            }
            return res;
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<KrnObject> filter(KrnObject filter, int limit,int beginRow,int endRow) {
        try {
            final Kernel krn = Kernel.instance();
            InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();
            Cache cash = mgr.getCash();
            KrnObject[] objs = krn.getFilteredObjects(filter.getKrnObject(), limit,beginRow,endRow, cash.getTransactionId());
            List<KrnObject> l = new ArrayList<KrnObject>(objs.length);
            for (int i = 0; i < objs.length; i++) {
                l.add(objs[i]);
            }
            return l;
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    @Override
    public long filterCount(KrnObject filter) throws Exception {
        final Kernel krn = Kernel.instance();
        InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();
        Cache cash = mgr.getCash();
        long res = krn.filterCount(filter.getKrnObject(), cash.getTransactionId());
        return res;
    }

    @Override
    public List<Object> filterGroup(KrnObject filter) throws Exception {
        final Kernel krn = Kernel.instance();
        InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();
        Cache cash = mgr.getCash();
        List<Object> res = krn.filterGroup(filter.getKrnObject(), cash.getTransactionId());
        return res;
    }

    @Override
    public List<Object> filterGroup(KrnObject filter, Map<String, Object> params, boolean allTransactions) throws Exception {
        final Kernel krn = Kernel.instance();
        String uid = filter.getUID();
        krn.clearFilterParams(uid);
        if (params != null) {
            for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String name = it.next();
                Object value = params.get(name);
                if (value instanceof List) {
                    krn.setFilterParam(uid, name, (List) value);
                } else {
                    krn.setFilterParam(uid, name, Collections.singletonList(value));
                }
            }
        }
        InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();
        Cache cash = mgr.getCash();
        long trId = allTransactions ? -1 : cash.getTransactionId();
        return krn.filterGroup(filter.getKrnObject(), trId);
    }

    @Override
    public List<KrnObject> filter(KrnObject filter, Map<String, Object> params, boolean allTransactions, int limit, int beginRow, int endRow) throws Exception {
        final Kernel krn = Kernel.instance();
        String uid = filter.getUID();
        krn.clearFilterParams(uid);
        if (params != null) {
            for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String name = it.next();
                Object value = params.get(name);
                if (value instanceof List) {
                    krn.setFilterParam(uid, name, (List) value);
                } else {
                    krn.setFilterParam(uid, name, Collections.singletonList(value));
                }
            }
        }
        try {
            InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();
            Cache cash = mgr.getCash();
            long trId = allTransactions ? -1 : cash.getTransactionId();
            KrnObject[] objs = krn.getFilteredObjects(filter.getKrnObject(), limit, beginRow, endRow, trId);
            List<KrnObject> l = new ArrayList<KrnObject>(objs.length);
            for (int i = 0; i < objs.length; i++) {
                l.add(objs[i]);
            }
            return l;
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    @Override
    public long filterCount(KrnObject filter, Map<String, Object> params, boolean allTransactions) throws Exception {
        final Kernel krn = Kernel.instance();
        String uid = filter.getUID();
        krn.clearFilterParams(uid);
        if (params != null) {
            for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String name = it.next();
                Object value = params.get(name);
                if (value instanceof List) {
                    krn.setFilterParam(uid, name, (List) value);
                } else {
                    krn.setFilterParam(uid, name, Collections.singletonList(value));
                }
            }
        }
        InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();
        Cache cash = mgr.getCash();
        long trId = allTransactions ? -1 : cash.getTransactionId();
        return krn.filterCount(filter.getKrnObject(), trId);
    }

    
    @Override
    public List sort(List objs, String path) {
        return null;
    }

    @Override
    public Sequence getSequence(String uid) {
        return null;
    }
    
    @Override
    public List<KrnObject> find(String path, Object value) {
        try {
            final Kernel krn = Kernel.instance();
            Pair[] ps = Utils.parsePath(path);
            if (ps.length == 1) {
                KrnAttribute attr = (KrnAttribute) ps[0].first;
                KrnClass cls = krn.getClassByName(path.substring(0, path.indexOf('.')));
                KrnObject[] objs = krn.getObjectsByAttribute(cls.id, attr.id, 0, ComparisonOperations.CO_EQUALS, value, 0);
                List<KrnObject> l = new ArrayList<KrnObject>(objs.length);
                for (int i = 0; i < objs.length; i++) {
                    l.add(objs[i]);
                }
                return l;
            } else {
                log.error("$Objects.find: Требуется путь с глубиной 1");
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    @Override
    public List getSqlResult(String sql, List vals, List params) {
        return null;
    }

    @Override
    public void removeObject(int id) {
    }

    
    @Override
    public String getUID(int id) {
        try {
            return Kernel.instance().getUId(id);
        } catch (KrnException e) {
            log.error(e, e);
        }
        return "";
    }

    
    @Override
    public void setFilterParam(KrnObject filter, Map<String, Object> params) throws Exception {
        final Kernel krn = Kernel.instance();
        final String fuid = filter.getUID();
        for (String pid : params.keySet()) {
            Object value = params.get(pid);
            if (value instanceof List) {
                krn.setFilterParam(fuid, pid, (List) value);
            } else if (value != null) {
                krn.setFilterParam(fuid, pid, Collections.singletonList(value));
            } else {
                krn.setFilterParam(fuid, pid, Collections.emptyList());
            }
        }
    }

    
    @Override
    public void clearFilterParam(KrnObject filter) throws KrnException {
        Kernel.instance().clearFilterParams(filter.getUID());
    }

    
    @Override
    public List<Object> getFilterParam(String fuid, String pid) {
        try {
            final Kernel krn = Kernel.instance();
            List<Object> res = krn.getFilterParam(fuid, pid);
            if (res != null) {
            	List<Object> params = new ArrayList<Object>();
                for (int i = 0; i < res.size(); i++)
                    params.add(res.get(i));
                return params;
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Получить параметры фильтра.
     * 
     * @param fuid
     *            идентификатор фильтра.
     * @return карта параметров фильтра.
     */
    public Map getFilterParams(String fuid) {
        try {
            final Kernel krn = Kernel.instance();
            Map res = krn.getFilterParams(fuid);
            if (res != null) {
                return res;
            }
        } catch (KrnException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    @Override
    public List<KrnAttribute> getAttributesByType(KrnClass cls, boolean inherited) throws KrnException {
        return Kernel.instance().getAttributesByTypeId(cls.getId(), inherited);
    }

    @Override
    public boolean stopProcess(Activity activity) throws KrnException {
        return TaskTable.instance(false).stopProcess(activity, false);
    }

    public boolean stopProcess(Activity activity, boolean forceCancel) throws KrnException {
        return TaskTable.instance(false).stopProcess(activity, forceCancel);
    }

    /**
     * Создать запрос.
     * 
     * @param path
     *            путь к классу
     * @param lang
     *            язык
     * @return объект <code>SrvQuery</code>
     * @throws KrnException
     *             the krn exception
     */
    public ClientQuery createQuery(String path, KrnObject lang) throws KrnException {
    	InterfaceManager mgr = InterfaceManagerFactory.instance().getManager();
        Cache cash = mgr.getCash();
        return createQuery(path, lang, cash.getTransactionId());
    }

    public ClientQuery createQuery(String path, KrnObject lang, long tid) throws KrnException {
        Kernel krn = Kernel.instance();
        return new ClientQuery(path, lang, krn, tid);
    }

    public ClientQuery createQuery(String path) throws KrnException {
    	return createQuery(path, null);
    }

    public ClientQuery createQuery(String path, long tid) throws KrnException {
    	return createQuery(path, null, tid);
    }

    /**
     * Сохранить выражение(текст) в файл.
     * 
     * @param expr
     *            сохраняемый текст.
     * @param dir
     *            директория сохранения файла.
     * @param name
     *            имя файла.
     * @param encoding
     *            кодировка текста, по умолчанию используется <code>UTF-8</code>.
     * @param isAppend
     *            дозапись? если <code>true</code> то информация в файл будет дозаписанна, иначе файл будет перезаписан.
     * @return true, в случае успеха.
     */
    public boolean saveExprToFile(String expr, String dir, String name, String encoding, boolean isAppend) {
        boolean res = false;
        if (encoding == null || encoding.equals("")) {
            encoding = "UTF-8";
        }
        File fileDir = new File(dir);
        fileDir.mkdirs();
        File file = new File(fileDir, name);
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file, isAppend));
            os.write(expr.getBytes(encoding));
            os.close();
            res = true;
        } catch (IOException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return res;
    }
    /**
     * Сохранить XML в файл.
     * 
     * @param xml
     *            сохраняемая XML.
     * @param dir
     *            директория сохранения файла.
     * @param name
     *            имя файла.
     * @param encoding
     *            кодировка текста, по умолчанию используется <code>UTF-8</code>.
     * @return true, в случае успеха.
     */
    public boolean saveXmlToFile(Element xml, String dir, String name, String encoding) {
        boolean res = false;
        if (encoding == null || encoding.equals("")) {
            encoding = "UTF-8";
        }
        File fileDir = new File(dir);
        fileDir.mkdirs();
        File file = new File(fileDir, name + ".xml");
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            org.jdom.output.Format ft = org.jdom.output.Format.getPrettyFormat();
            ft.setEncoding(encoding);
            XMLOutputter opr = new XMLOutputter(ft);
            xml.detach();
            opr.output(new Document(xml), os);
            os.close();
            res = true;
        } catch (IOException e) {
            SecurityContextHolder.getLog().error(e, e);
        }
        return res;
    }

    /**
     * Получить XML из файла.
     * 
     * @param dir
     *            директория хранения файла.
     * @param name
     *            имя файла.
     * @param encoding
     *            кодировка текста, по умолчанию используется <code>UTF-8</code>.
     * @return объект XML
     * @throws Exception
     *             the exception
     */
    public Element getXmlFromFile(String dir, String name, String encoding) throws Exception {
        Element e;
        if (encoding == null || encoding.equals("")) {
            encoding = "UTF-8";
        }
        File fileDir = new File(dir);
        fileDir.mkdirs();
        File file = new File(fileDir, name);
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(is, encoding);
        e = doc == null ? null : doc.getRootElement();
        return e;
    }
    public static boolean sendMail(String host,String port,String from, List<String> tos,String username,String password,String subject,String text,  List<String> filePaths){
        // Get system properties
        Properties properties = new Properties();
        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.socketFactory.port", port);
        properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true"); 
        properties.put("mail.smtp.port", port);
        javax.mail.Session session = javax.mail.Session.getDefaultInstance(properties);
    	try{
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            if(tos==null || tos.size()==0) return false;
            
            for(String to:tos){
                message.addRecipient(Message.RecipientType.TO,
                                         new InternetAddress(to));
            }

            // Set Subject: header field
            message.setSubject(subject);

            // Create the message part 
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText(text);
            
            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);
            if(filePaths!=null && filePaths.size()>0){
                // Part two is attachment
            	for(String filePath:filePaths){
            		File file=new File(filePath);
            		if(file.exists()){
		                messageBodyPart = new MimeBodyPart();
		                DataSource source = new FileDataSource(file);
		                messageBodyPart.setDataHandler(new DataHandler(source));
		                messageBodyPart.setFileName(file.getName());
		                multipart.addBodyPart(messageBodyPart);
            		}
            	}
            }

            // Send the complete message parts
            message.setContent(multipart );

            SMTPTransport t = (SMTPTransport)session.getTransport("smtps");
            try {
                t.connect(host,username, password);
                t.sendMessage(message, message.getAllRecipients());
            } finally {
                t.close();
            }
            return true;
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
    	return false;
    }
	@Override
	public String putRepositoryData(String paths, String fileName, byte[] data) {
		 try {
	            Kernel krn = Kernel.instance();
	            return krn.putRepositoryData(paths, fileName, data);
	        } catch (KrnException e) {
	            log.error(e, e);
	        }
	        return null;
	}

	@Override
	public byte[] getRepositoryData(String docId) {
		 try {
	            Kernel krn = Kernel.instance();
	            return krn.getRepositoryData(docId);
	        } catch (KrnException e) {
	            log.error(e, e);
	        }
		return null;
	}

	@Override
	public String getRepositoryItemName(String docId) throws Exception {
		 try {
	            Kernel krn = Kernel.instance();
	            return krn.getRepositoryItemName(docId);
	        } catch (KrnException e) {
	            log.error(e, e);
	        }
		return null;
	}

	@Override
	public String getRepositoryItemType(String docId) throws Exception {
		 try {
	            Kernel krn = Kernel.instance();
	            return krn.getRepositoryItemType(docId);
	        } catch (KrnException e) {
	            log.error(e, e);
	        }
		return null;
	}

	@Override
	public boolean dropRepositoryItem(String docId) throws Exception {
		 try {
	            Kernel krn = Kernel.instance();
	            return krn.dropRepositoryItem(docId);
	        } catch (KrnException e) {
	            log.error(e, e);
	        }
		return false;
	}

	@Override
	public List<String> searchByQuery(String searchName) throws Exception {
		 try {
	            Kernel krn = Kernel.instance();
	            return krn.searchByQuery(searchName);
	        } catch (KrnException e) {
	            log.error(e, e);
	        }
		return null;
	}
}
