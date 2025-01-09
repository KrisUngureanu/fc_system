package com.cifs.or2.server.exchange.transport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.jdom.output.XMLOutputter;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.exchange.Box;
import com.cifs.or2.server.exchange.ExchangeUtils;
import com.cifs.or2.server.exchange.transport.gdms3.Gdms3Transport;
import com.cifs.or2.server.orlang.SrvOrLang;
import com.cifs.or2.util.Funcs;
import com.cifs.or2.kernel.*;

import kz.tamur.admin.ExchangeEvents;
import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.server.wf.ExecutionComponent;
import kz.tamur.server.wf.ExecutionEngine;

import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.*;
import java.nio.file.Path;

import static kz.tamur.or3ee.common.TransportIds.DIRECTORY;
import static kz.tamur.or3ee.common.TransportIds.MAIL;
import static kz.tamur.or3ee.common.TransportIds.MQ_TRANSPORT;
import static kz.tamur.or3ee.common.TransportIds.MQ_JMS;
import static kz.tamur.or3ee.common.TransportIds.JBOSS_JMS;
import static kz.tamur.or3ee.common.TransportIds.WEB_SERVICE;
import static kz.tamur.or3ee.common.TransportIds.SGDS_TRANSPORT;
import static kz.tamur.or3ee.common.TransportIds.LOTUS_DIIOP;
//import static kz.tamur.or3ee.common.TransportIds.OPEN_MQ;;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 22.06.2005
 * Time: 16:49:11
 * To change this template use File | Settings | File Templates.
 */
public class MessageCash{

    private  Log log;
    private  Log exchangeLog;
    private  Log balansLog;
    public static String fileSepar = File.separator;
    public String curDir = "." + File.separator + "exchange";
    private String rejDir=curDir+fileSepar+"IN"+fileSepar+"REJ";
    private String rejSentDir=curDir+fileSepar+"OUT"+fileSepar+"INV";
    public String tempDir=curDir+fileSepar+"TEMP";
//    private static String charSet="UTF-8";
    private Map<Long,Transport> transports = new HashMap<Long, Transport>();
    private Map<Long,Box> boxMap = Collections.synchronizedMap(new HashMap<Long, Box>());
    private Map<String,Integer> rejMap = Collections.synchronizedMap(new HashMap<String, Integer>());
    private Map<String,Long> msgTypeMap=new HashMap<String,Long>();
    private Map<Long,String> eventMap=new HashMap<Long, String>();
    private List<String> errSentList=new ArrayList<String>();
    private KrnClass boxClass;
    private DirectoryTransport directoryTransport;
    private MqTransport mqTransport;
    private MqJmsTransport mqJmsTransport;
    private JbossJmsTransport jbossJmsTransport;
    private WSTransport wsTransport;
    private Gdms3Transport sgdsTransport;
    private MailTransport mailTransport;
    private LotusDIIOPTransport diiopTransport;
    //Status message
    public static final int IN=0;
    public static final int OUT=1;
    public static final int ARH=2;
    public static final int INV=3;
    public static final int REJ=4;
    public static final int RECEIVED=2;
    public static final int SENT=3;
    //Transport
    public String t_props_file = "transport.properties";
    // status received
    public static final int NOT_FOUND=0;
    public static final int NORMAL=1;
    public static final int NOT_PARS=-1;
    public static final int NOT_BOX=-2;
    public static final int NOT_PERFORM=-3;

    private static boolean isStopping=false;
    private static long baseId;
    public static Comparator<File> fileComparator;
    private static boolean sgdsReady=false;
    private static String dsName;
    private ExecutionComponent exeComp;
    
    public MessageCash(Session session, String tpropsFileName, ExecutionComponent exeComp) {
    	
    	this.dsName = session.getUserSession().getDsName();
    	this.exeComp = exeComp;
    	
    	initLogs(dsName);
    	
    	t_props_file = tpropsFileName;
     
        Properties ps= new Properties();
        try {
        	if (kz.tamur.util.Funcs.isValid(t_props_file)) {
	            File cf = kz.tamur.util.Funcs.getCanonicalFile(t_props_file);
	            if (cf.exists()) {
	                ps.load(new FileInputStream(cf));
	                if (ps.getProperty("dir")!=null)
	                    curDir=ps.getProperty("dir");
	                rejDir=curDir+fileSepar+"IN"+fileSepar+"REJ";
	                tempDir=curDir+fileSepar+"TEMP";
	                String ready_=ps.getProperty("Sgds_ready");
	                sgdsReady= (ready_!=null && ready_.equals("yes"));
	            }
        	}
        }catch (IOException e){
            log.error(e, e);
        }
        try {
            MessageCash.baseId = session.getCurrentDb().id;
            KrnClass cls_msg_type = session.getClassByName("Тип сообщения");
            KrnAttribute attr_msg_type_kod = session.getAttributeByName(cls_msg_type, "код");
            KrnObject[] objs=session.getClassObjects(cls_msg_type,new long[0],0);
            long[] ids=Funcs.makeObjectIdArray(objs);
            StringValue[] svs=session.getStringValues(ids,attr_msg_type_kod.id,0,false,0);
            for (StringValue sv : svs) {
                if (sv.index == 0) {
                    msgTypeMap.put(sv.value, sv.objectId);
                }
            }
        } catch (KrnException e) {
            log.error(e, e);
        }
        log.debug("MessageCash is ready");
        fileComparator= new Comparator<File>() {
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        };

        registerTransport(directoryTransport=new DirectoryTransport(this));
        registerTransport(mqTransport=new MqTransport(this));
        registerTransport(mqJmsTransport=new MqJmsTransport(this));
        registerTransport(jbossJmsTransport=new JbossJmsTransport(this));
        registerTransport(wsTransport=new WSTransport(this));
        if(sgdsReady)
            registerTransport(sgdsTransport=new Gdms3Transport(this));

        registerTransport(mailTransport=new MailTransport(this));
        registerTransport(diiopTransport=new LotusDIIOPTransport(this));

        try {
            loadBoxes(session);
        } catch (KrnException e) {
            log.error(e, e);
        }
        if(sgdsReady)
            sgdsTransport.reset();
    }

    public Transport getTransport(int id) {
        return transports.get((long) id);
    }

    public void registerTransport(Transport transport) {
        transports.put((long) transport.getId(), transport);
    }

    public boolean dispose(int folderId,long boxId,long flowId,Object msg,String corelId) throws TransportException{
        boolean res=false;
        Box box=boxMap.get(boxId);
        if(box!=null){
            try {
                File tempDir_=new File(tempDir);
                tempDir_.mkdirs();
                String fileName= getNextMessageId(tempDir_);
                File fileInv,fileTemp=new File(tempDir,fileName+"_"+flowId),file;
                OutputStream os = new BufferedOutputStream(new FileOutputStream(fileTemp));
                if(msg instanceof Element){
                    //Format fmt=Format.getCompactFormat();
                    XMLOutputter opr = new XMLOutputter();
                    if(!box.getCharSet().equals("")){
                        opr.setFormat(opr.getFormat().setEncoding(box.getCharSet()));
                    }else{
                        opr.setFormat(opr.getFormat().setEncoding("UTF-8"));
                    }
                    Element xml=(Element)msg;
                    xml.detach();
                    opr.output(new Document(xml), os);
                    os.close();
                    String msgId= getMessageId(box,folderId,xml);
                    String msgType= getMessageType(box,folderId,xml);
                    String urgency= getMessagePrioritet(box,folderId,xml);
                    int prior=9-Integer.valueOf(urgency.equals("")?"0":urgency);
                    String prioritet= prior>0?""+prior:"0";
                    if(msgId.equals("")||msgType.equals("")){
                        File dir_inv=new File(getPathName(box,folderId,INV));
                        dir_inv.mkdirs();
                        fileName= prioritet+msgId/*getNextMessageId(dir_inv)*/;
                        fileInv=new File(dir_inv,fileName+"_"+flowId);
                        int i=0;
                        while(fileInv.exists()){
                            fileInv=new File(dir_inv,fileName+"_"+i+"_"+flowId);
                            i++;
                        }
                        
                        fileTemp.renameTo(fileInv);
                        if(folderId==IN){
                                writeLogRecord(ExchangeEvents.MSC_106,
                                        msgId,msgType,fileInv.getPath(),box.getUrlIn());
                        }else if(folderId==OUT){
                            writeLogRecord(ExchangeEvents.MSC_005,
                                    msgId,msgType,fileInv.getPath(),box.getUrlOut());
                        }
                    }else{
                        File dir=new File(getPathName(box,folderId,0));
                        dir.mkdirs();
                        fileName= prioritet+msgId/*getNextMessageId(dir)*/;
                        file=new File(dir,fileName+"_"+flowId);
                        int i=0;
                        while(file.exists()){
                            file=new File(dir,fileName+"_"+i+"_"+flowId);
                            i++;
                        }
                        fileTemp.renameTo(file);
                        if(folderId==IN){
                                writeLogRecord(ExchangeEvents.MSC_100, msgId,msgType,file.getPath(),box.getUrlIn());
                                replayErrorMsg(box,2,corelId);
                        }else if(folderId==OUT){
                                writeLogRecord(ExchangeEvents.MSC_000,
                                        msgId,msgType,file.getPath(),box.getUrlOut());
                        }
                    }
                }else if(msg instanceof MimeMessage){
                    //Создание почтового сообщения
                    MimeMessage mmsg=(MimeMessage)msg;
                    mmsg.writeTo(os);
                    os.close();
                    File dir=new File(getPathName(box,folderId,0));
                    dir.mkdirs();
                    fileName = corelId;
                    file=new File(dir,fileName+"_"+flowId);
                    int i=0;
                    while(file.exists()){
                        file=new File(dir,fileName+"_"+i+"_"+flowId);
                        i++;
                    }
                    fileTemp.renameTo(file);
                    if(folderId==IN){
                            writeLogRecord(ExchangeEvents.MSC_100,fileName,"mailReceived",file.getPath(),box.getUrlIn());
                            replayErrorMsg(box,2,corelId);
                    }else if(folderId==OUT){
                            writeLogRecord(ExchangeEvents.MSC_000,
                                    fileName,"mailSend",file.getPath(),box.getUrlOut());
                    }
                }else if(msg instanceof File){
                    //Создание почтового сообщения
                    InputStream is = new BufferedInputStream(new FileInputStream((File)msg));
                    kz.tamur.util.Funcs.writeStream(is, os, Constants.MAX_MESSAGE_SIZE);
                    is.close();
                    os.close();
                    File dir=new File(getPathName(box,folderId,0));
                    dir.mkdirs();
                    fileName = corelId;
                    file=new File(dir,fileName+"_"+flowId);
                    int i=0;
                    while(file.exists()){
                        file=new File(dir,fileName+"_"+i+"_"+flowId);
                        i++;
                    }
                    fileTemp.renameTo(file);
                    if(folderId==IN){
                            writeLogRecord(ExchangeEvents.MSC_100,fileName,"fileReceived",file.getPath(),box.getUrlIn());
                            replayErrorMsg(box,2,corelId);
                    }else if(folderId==OUT){
                            writeLogRecord(ExchangeEvents.MSC_000,
                                    fileName,"fileSend",file.getPath(),box.getUrlOut());
                    }
                }else if(msg instanceof byte[]){
                    //Создание почтового сообщения
                    os.write((byte[])msg);
                    os.close();
                    File dir=new File(getPathName(box,folderId,0));
                    dir.mkdirs();
                    fileName = corelId;
                    file=new File(dir,fileName+"_"+flowId);
                    int i=0;
                    while(file.exists()){
                        file=new File(dir,fileName+"_"+i+"_"+flowId);
                        i++;
                    }
                    fileTemp.renameTo(file);
                    if(folderId==IN){
                            writeLogRecord(ExchangeEvents.MSC_100,fileName,"byteReceived",file.getPath(),box.getUrlIn());
                            replayErrorMsg(box,2,corelId);
                    }else if(folderId==OUT){
                            writeLogRecord(ExchangeEvents.MSC_000,
                                    fileName,"byteSend",file.getPath(),box.getUrlOut());
                    }
                }else{
                    log.info("ОШИБКА!: Данный тип сообщения:'"+msg.getClass().getName()+"' не поддерживается транспортной системой!");
                }
            } catch (IOException e) {
                log.info("ОШИБКА!: При записи сообщения в файл");
            } catch (MessagingException e) {
                log.info("ОШИБКА!: При создании электронного сообщения");
            }
        } else {
            log.error("ОШИБКА!: Не найден ящик с кодом " + boxId);
            log.error("folderId: " + folderId);
            log.error("boxId: " + boxId);
            log.error("flowId: " + flowId);
            log.error("corelId: " + corelId);
        }
        return res;
    }

    private void addBox(Box box){
        Transport transport=transports.get(box.getTransportId());
        if(transport!=null){
            boxMap.put(box.getKrnObject().id,box);
            transport.addTransportListener(box);
        }
    }
    public void removeBox(Box box){
        Transport transport=transports.get(box.getTransportId());
        transport.removeTransportListener(box);

    }

    private String getPathName(Box box,int folderId,int arhInvRej){
        String res="";
        if(folderId==IN){
           res+=curDir+fileSepar+"IN";
        }else if(folderId==OUT){
            res+=curDir+fileSepar+"OUT";
        }
        if(arhInvRej==INV){
           res+=fileSepar+"INV";
        }else if(arhInvRej==REJ){
            res+=fileSepar+"REJ";
        }else if(arhInvRej==ARH){
            res+=fileSepar+"ARH";
        }
        res+=fileSepar+(transports.get(box.getTransportId())).getName()+fileSepar+box.getName();
        return res;
    }
    private static synchronized String getNextMessageId(File dir) {
        String[] names = dir.list();
        long last = 0;
        for (String name : names) {
            long current;
            try {
                current = Long.parseLong(name.substring(0, name.indexOf("_")));
            } catch (Exception ex) {
                continue;
            }
            if (current > last) {
                last = current;
            }
        }
        return "" + (last + 1);
    }
    public static String getParam(String url,String sp){
        String res="";
        int i1= url.indexOf(sp);
        if(i1>-1){
            int i2= url.indexOf("=",i1);
            if(i2>-1){
                int i3= url.indexOf(";",i2);
                if(i3>-1)
                    res=url.substring(i2+1,i3).trim();
            }
        }
        return res;
    }
    public static String getParamSgds(String url,String sp){
        String res="";
        int i1= url.indexOf(sp);
        if(i1>-1){
            int i2= url.indexOf("<",i1);
            if(i2>-1){
                int i3= url.indexOf(">",i2);
                if(i3>-1)
                    res=url.substring(i2+1,i3).trim();
            }
        }
        return res;
    }
    public String getMessageId(Box box,int folderId,Element xml){
        String res="";
        String xpath="";
        if(folderId==IN){
            xpath=box.getXpathIn();
        }else if(folderId==OUT){
            xpath=box.getXpathOut();
        }
        if(xpath.equals(""))return "UNTITELD";
        try {
            XPath oPath = XPath.newInstance(xpath);
            Object MessageId = oPath.valueOf(xml);
            if (null != MessageId) {
                res= (String)MessageId;
            }
        }catch (JDOMException e) {
            log.error(e, e);
        }
        return res;
    }

    public String getMessageType(Box box,int folderId,Element xml){
        String res="";
        String xpath="";
        if(folderId==IN){
            xpath=box.getXpathTypeIn();
        }else if(folderId==OUT){
            xpath=box.getXpathTypeOut();
        }
        if(xpath.equals("")) return "UNTITELD";
        try {
            XPath oPath = XPath.newInstance(xpath);
            Object MessageId = oPath.valueOf(xml);
            if (null != MessageId) {
                res= (String)MessageId;
            }
        }catch (JDOMException e) {
            log.error(e, e);
        }
        return res;
    }
    public String getMessagePrioritet(Box box,int folderId,Element xml){
        String res="0";
        String xpath="./Header/ct:urgency/child::text() | ./Head/urgency/child::text()";
        try {
            XPath oPath = XPath.newInstance(xpath);
            Object urgency = oPath.valueOf(xml);
            if (null != urgency) {
                res= (String)urgency;
            }
        }catch (JDOMException e) {
        	log.error(e.getMessage());
            //log.error(e, e);
        }
        return res;
    }

    public String getMessageInit(Box box,File file){
        String res="";
        String xpath;
            xpath=box.getXpathIdInit();
        if(xpath.equals("")) return res;
        try {
            Element xml = load(file);
            XPath oPath = XPath.newInstance(xpath);
            Object MessageId = oPath.valueOf(xml);
            if (null != MessageId) {
                res= (String)MessageId;
            }
        }catch (JDOMException e) {
            log.error(e, e);
        }
        return res;
    }
    private int getFlowId(File file){
        String res=file.getName();
        try {
        	res = res.substring(res.lastIndexOf("_") + 1);
        	return Integer.valueOf(res);
        } catch (Throwable e) {
        	log.error(e, e);
        }
        return 0;
    }
    public static String getMsgId(File file){
        String res = file.getName();
        int pos = res.indexOf("_");
        if (pos != -1) {
	        res = res.substring(0, pos);
        }
        return res;
    }

    public synchronized void getRejectMessageBox(long boxId){
        if(ExecutionEngine.isMaxThreadCount())return;
        Box box=boxMap.get(boxId);
        if (box == null) return;
        File dir=new File(rejDir+fileSepar+(transports.get(box.getTransportId())).getName()+fileSepar+box.getName());
        if(!dir.exists())return;
        dir.mkdirs();
        File[] files=dir.listFiles();
        for (File file : files) {
            String filePath = file.getPath();
            int m = filePath.indexOf(fileSepar + "REJ");
            filePath = filePath.substring(0, m) + filePath.substring(m + 4);
            File dir_ = new File(filePath.substring(0, filePath.lastIndexOf(fileSepar)));
            dir_.mkdirs();
            int index=file.getName().indexOf("_0");
            String fname=index>0?file.getName().substring(0,index):file.getName();
            int li=file.getName().lastIndexOf("_")+1;
            int li_=file.getName().lastIndexOf(".");
            if(li>li_) li_=file.getName().length();
            int flowId=Long.valueOf(file.getName().substring(li,li_)).intValue();
            File f_new = new File(dir_, fname + "_"+(flowId>0?flowId:"0"));
            int i = 0;
            while (f_new.exists()) {
                f_new = new File(dir_, fname + "_" + i + "_0");
                i++;
            }
            file.renameTo(f_new);
        }
    }

    public synchronized void getRejectSentMessageBox(long boxId){
        if(ExecutionEngine.isMaxThreadCount())return;
        Box box=boxMap.get(boxId);
        if (box == null) return;
        File dir=new File(rejSentDir+fileSepar+(transports.get(box.getTransportId())).getName()+fileSepar+box.getName());
        if(!dir.exists())return;
        dir.mkdirs();
        File[] files=dir.listFiles();
        for (File file : files) {
            String filePath = file.getPath();
            int m = filePath.indexOf(fileSepar + "INV");
            filePath = filePath.substring(0, m) + filePath.substring(m + 4);
            File dir_ = new File(filePath.substring(0, filePath.lastIndexOf(fileSepar)));
            dir_.mkdirs();
            int index=file.getName().lastIndexOf("_");
            String fname=index>0?file.getName().substring(0,index):file.getName();
            int flowId=0;
            if(index>0){
                int li=file.getName().lastIndexOf(".");
                if(index+1 >li) li=file.getName().length();
                flowId=Long.valueOf(file.getName().substring(index+1,li)).intValue();
            }
            File f_new = new File(dir_, fname + "_"+(flowId>0?flowId:"0"));
            int i = 0;
            while (f_new.exists()) {
                f_new = new File(dir_, fname + "_" + i + "_0");
                i++;
            }
            file.renameTo(f_new);
        }
    }
    
	public void messageReceived(File file, Box box) {
		if (box == null || isStopping)
			return;
		Object msg = null;
		String msgId = "";
		int flowId = getFlowId(file);
		
		if (box.getTypeMsg() == 0) {
			msg = load(file);
		}
		
		handleMessage(file, box, msg, msgId, flowId);
	}

	public void mailMessageReceived(Path file, Box box) {
		if (box == null || isStopping)
			return;
		Object msg = null;
		int flowId = getFlowId(file.toFile());
		String msgId = getMsgId(file.toFile());
		Session session = null;
		try {
			session = SrvUtils.getSession(dsName, "sys", null);
			if (!msgId.equals("")) {
				try {
					msg = session.getObjectById(Long.valueOf(msgId), -1);
				} catch (NumberFormatException e) {
					log.error(e, e);
				} catch (KrnException e) {
					log.error(e, e);
				}
			} else {
				msg = ExchangeUtils.receivedMailMessage(session, file, 0);
				File f_new = new File(getPathName(box, IN, -1), msgId + "_" + flowId);
				file.toFile().renameTo(f_new);
			}
		} catch (KrnException e) {
			log.error(e, e);
		} finally {
			if (session != null) {
				session.release();
			}
		}
		
		handleMessage(file.toFile(), box, msg, msgId, flowId);
	}

	private void handleMessage(File file, Box box, Object msg, String msgId, int flowId) {
		String msgInitId = "", msgType = "";
		String filePath = file.getPath();

		int res = -1;
		Integer count = 0;
		if (box.getTypeMsg() == 0 && msg != null) {
			res = box.messageReceived(file, msg);
		} else {
			File dir_arh = new File(getPathName(box, IN, ARH));
			dir_arh.mkdirs();
			File f_new = new File(dir_arh, file.getName() + "_" + flowId);
			int i = 0;
			while (f_new.exists()) {
				f_new = new File(dir_arh, msgId + "_" + i + "_" + flowId);
				i++;
			}
			try {
				FileOutputStream osf = new FileOutputStream(file, true);
				int count_ = 0;
				while (count_++ < 5) {
					if (osf.getChannel().tryLock() != null) {
						osf.close();
						break;
					} else {
						log.debug("@SLEEPING WHILE COPY:" + count_);
						Thread.sleep(5000);
					}
				}
			} catch (Exception ex) {
				log.error(ex, ex);
			}
			file.renameTo(f_new);
			res = box.messageReceived(f_new, msg);
			writeLogRecord(ExchangeEvents.MSC_102, msgId, msgType, f_new.getPath(), box.getUrlIn());
			balansLog.info("|IN|" + box.getName() + "|" + msgId + "|" + msgInitId);
			return;
		}
		if (box.getTransportId() == MAIL) {
			msgId = "" + ((KrnObject) msg).id;
			msgType = "mailReceived";
		} else {
			msgId = getMessageId(box, IN, (Element) msg);
			msgType = getMessageType(box, IN, (Element) msg);
			msgInitId = exeComp.getInitId((Element) msg);

		}
		if ("".equals(msgId) || msgId.contains("UNTITELD"))
			count = 10;
		else
			count = rejMap.get(msgId);
		// }
		if (res == NORMAL) {
			if (count != null)
				rejMap.remove(msgId);
			File dir_arh = new File(getPathName(box, IN, ARH));
			dir_arh.mkdirs();
			File f_new = new File(dir_arh, msgId + "_" + flowId);
			int i = 0;
			while (f_new.exists()) {
				f_new = new File(dir_arh, msgId + "_" + i + "_" + flowId);
				i++;
			}
			file.renameTo(f_new);
			writeLogRecord(ExchangeEvents.MSC_102, msgId, msgType, f_new.getPath(), box.getUrlIn());
			balansLog.info("|IN|" + box.getName() + "|" + msgId + "|" + msgInitId);
		} else if (res == NOT_FOUND && (count == null || count < 10)) {
			File f_new = null;
			if (filePath.indexOf(fileSepar + "REJ") == -1) {
				File dir_rej = new File(getPathName(box, IN, REJ));
				dir_rej.mkdirs();
				if (count == null)
					count = 0;
				else
					count++;
				rejMap.put(msgId, count);
				f_new = new File(dir_rej, msgId + "_" + flowId);
				int i = 0;
				while (f_new.exists()) {
					f_new = new File(dir_rej, msgId + "_" + i + "_" + flowId);
					i++;
				}
				file.renameTo(f_new);
			} else {
				f_new = file;
			}
			if (count == 0)
				writeLogRecord(ExchangeEvents.MSC_104, msgId, msgType, f_new.getPath(), box.getUrlIn());
		} else {
			if (count != null)
				rejMap.remove(msgId);
			File dir_inv = new File(getPathName(box, IN, INV));
			dir_inv.mkdirs();
			File f_new = new File(dir_inv, msgId + "_" + flowId);
			int i = 0;
			while (f_new.exists()) {
				f_new = new File(dir_inv, msgId + "_" + i + "_" + flowId);
				i++;
			}
			file.renameTo(f_new);
			writeLogRecord(res == NOT_PARS ? ExchangeEvents.MSC_103 : ExchangeEvents.MSC_104, msgId, msgType,
					f_new.getPath(), box.getUrlIn());
			if (res == NOT_FOUND)
				replayErrorMsg(box, 3, msgId);
			balansLog.info("|ININV|" + box.getName() + "|" + msgId + "|" + msgInitId);
		}
	}

    public boolean messageSent(File file,Box box,boolean result) {
            Element xml;
            String msgId="",msgType;
        	if(box.getTransportId()==MAIL){
                msgId= getMsgId(file);
                msgType= "mailSend";
        	}else{
                xml = load(file);
                if(xml!=null)
                    msgId= getMessageId(box,OUT,xml);
                    msgType= getMessageType(box,OUT,xml);
        	}
            long flowId=getFlowId(file);
        if(result){
            File dir_arh=new File(getPathName(box,OUT,ARH));
            dir_arh.mkdirs();
            File f_new=new File(dir_arh,file.getName());
            int i=0;
            while(f_new.exists()){
                f_new=new File(dir_arh,file.getName()+"_"+i);
                i++;
            }
            try{
                file.renameTo(f_new);
                writeLogRecord(ExchangeEvents.MSC_001,msgId,msgType,f_new.getPath(),box.getUrlOut());
                balansLog.info("|OUT|"+box.getName()+"|"+msgId);
            }catch(Exception ex){
                writeLogRecord(ExchangeEvents.MSC_107,msgId,msgType,f_new.getPath(),box.getUrlOut());
                if(!errSentList.contains(file.getName()))
                    errSentList.add(file.getName());
            }
        }else{
            File dir_inv=new File(getPathName(box,OUT,INV));
            dir_inv.mkdirs();
            File f_new=new File(dir_inv,file.getName());
            int i=0;
            while(f_new.exists()){
                f_new=new File(dir_inv,file.getName()+"_"+i);
                i++;
            }
            try{
                file.renameTo(f_new);
                writeLogRecord(ExchangeEvents.MSC_003,msgId,msgType,f_new.getPath(),box.getUrlOut());
                balansLog.info("|OUTINV|"+box.getName()+"|"+msgId);
            }catch(Exception ex){
                writeLogRecord(ExchangeEvents.MSC_108,msgId,msgType,f_new.getPath(),box.getUrlOut());
                if(!errSentList.contains(file.getName()))
                    errSentList.add(file.getName());
            }
        }
        int res = box.responseSend(flowId,result);
        if(res==NOT_PERFORM){
        	//если в момент отправки сообщения действие не перешло на следующий шаг, то дожидаемся 5сек.
        	//и повторно уведомляем
        	final Box boxf=box;
        	final long flowIdf=flowId;
        	final boolean resultf=result;
            new Thread() {
                public void run() {
	               	try {
	                	log.info("Перед повторным уведомлением,flowId:"+flowIdf+";result:"+NOT_PERFORM);
	                	sleep(5000);
	            		int resf=boxf.responseSend(flowIdf,resultf);
	                	log.info("После повторного уведомления,flowId:"+flowIdf+";result:"+resf);
	                } catch (Exception e) {
	                	log.info("Ошибка при оповещения пункта обмена об отправлении сообщения!");
	                	log.error(e, e);
	                }
                }
            }.start();
        }
        return res==NORMAL || res==NOT_PERFORM;
    }
    
    public boolean isErrFile(String fname){
        return errSentList.contains(fname);
    }
    
    public Element load(File file) {
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(is);
            is.close();
            return doc.getRootElement();
        } catch (Exception e) {
            log.info("ghb");
            return null;
        }
    }

    public void writeToInvalid(Box box,File f) {
        try {
            log.error("Неправильный файл!!!");
            File dir_inv=new File(getPathName(box,IN,INV));
            dir_inv.mkdirs();
            String name= f.getName();
            if(name.indexOf(".")>0){
                name=name.substring(0,name.lastIndexOf("."));
            }
            File f_new=new File(dir_inv,name);
            int i=0;
            while(f_new.exists()){
                f_new=new File(dir_inv,name+"_"+i);
                i++;
            }
            InputStream is = new BufferedInputStream(new FileInputStream(f));
            OutputStream os = new BufferedOutputStream(new FileOutputStream(f_new));
            kz.tamur.util.Funcs.writeStream(is, os, Constants.MAX_MESSAGE_SIZE);
            is.close();
            os.close();
        } catch (Exception e) {
            log.error(e, e);
        }
    }

    public void writeToInvalid(Box box,InputStream is) {
        try {
            log.error("Неправильный файл!!!");
            File dir_inv=new File(getPathName(box,IN,INV));
            dir_inv.mkdirs();
            File f_new=new File(dir_inv,"inv");
            int i=0;
            while(f_new.exists()){
                f_new=new File(dir_inv,"inv"+"_"+i);
                i++;
            }
            OutputStream os = new BufferedOutputStream(new FileOutputStream(f_new));
            if (is != null) {
                kz.tamur.util.Funcs.writeStream(is, os, Constants.MAX_MESSAGE_SIZE);
                is.close();
            }
            os.close();
        } catch (Exception e) {
            log.error(e, e);
        }
    }
    public  void loadOked(){
          File f=new File("c:\\OKED.txt");
          File file=new File("c:\\OKED.xml");
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(f));
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            if (is != null) {
            	kz.tamur.util.Funcs.writeStream(is, os, Constants.MAX_ARCHIVED_SIZE);
                is.close();
            }
            os.close();
            String str= new String(os.toByteArray(),"UTF-16");
            int index_=0,index=str.indexOf("\n");
            Element root=new Element("Items");
            while(index>0){
                String str_teg=str.substring(index_,index);
                Element e=new Element("Item");
                Element ee=new Element("Kod");
                Element eee=new Element("Naim");
                ee.setText(str_teg.substring(0,str_teg.indexOf("\t")));
                eee.setText(str_teg.substring(str_teg.indexOf("\t")).trim());
                e.addContent(ee);
                e.addContent(eee);
                root.addContent(e);
                index=str.indexOf("\n",index_=index+1);
            }
            OutputStream os1 = new BufferedOutputStream(new FileOutputStream(file));
            XMLOutputter opr = new XMLOutputter();
            opr.getFormat().setEncoding("UTF-8");
            opr.output(root, os1);
            os1.close();
/*            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(is);
            is.close();
            return doc.getRootElement();
*/        } catch (Exception e) {
			log.error(e, e);
        }
    }

    public Box getBoxById(long id) {
        return boxMap.get(id);
    }

    public Collection<Box> getBoxes() {
        return boxMap.values();
    }

    private void loadBoxes(Session session) throws KrnException {
        boxClass = session.getClassByName("BoxExchange");
        KrnObject[] boxObjs = session.getClassObjects(boxClass, new long[0], 0);
        for (KrnObject boxObj : boxObjs) {
            if (boxObj.classId == boxClass.id) {
                Box box = loadBox(boxObj, session);
                box.addBoxListener(exeComp);
                addBox(box);
            }
        }
    }
    public void reloadBox(KrnObject boxObj,Session session)throws KrnException {
        if (boxObj.classId == boxClass.id) {
            Box box=boxMap.get(boxObj.id);
            if(box!=null) box.remove();
            box = loadBox(boxObj,session);
            box.addBoxListener(exeComp);
            addBox(box);
        }
    }
    private Box loadBox(KrnObject boxObj,Session session) throws KrnException {
        KrnClass boxClass = session.getClassByName("BoxExchange");
        KrnAttribute nameAttr = session.getAttributeByName(boxClass, "name");
        KrnAttribute urlInAttr = session.getAttributeByName(boxClass, "urlIn");
        KrnAttribute urlOutAttr = session.getAttributeByName(boxClass, "urlOut");
        KrnAttribute xpathInAttr = session.getAttributeByName(boxClass, "xpathIn");
        KrnAttribute xpathOutAttr = session.getAttributeByName(boxClass, "xpathOut");
        KrnAttribute xpathTypeInAttr = session.getAttributeByName(boxClass, "xpathTypeIn");
        KrnAttribute xpathTypeOutAttr = session.getAttributeByName(boxClass, "xpathTypeOut");
        KrnAttribute xpathIdInit = session.getAttributeByName(boxClass, "xpathIdInit");
        KrnAttribute charSet = session.getAttributeByName(boxClass, "charSet");
        KrnAttribute isRestrict = session.getAttributeByName(boxClass, "isRestrict");
        KrnAttribute typeMsgAttr = session.getAttributeByName(boxClass, "typeMsg");
        KrnAttribute transportAttr =
                session.getAttributeByName(boxClass, "transport");
        KrnAttribute baseAttr =
                session.getAttributeByName(boxClass, "base");
        KrnAttribute exprAttr = session.getAttributeByName(boxClass, "config");

        String[] strs = session.getStrings(boxObj.id, nameAttr.id, 0, false, 0);
        String name = (strs.length > 0) ? strs[0] : "Безымянный";

        strs = session.getStrings(boxObj.id, urlInAttr.id, 0, false, 0);
        String urlIn = (strs.length > 0) ? strs[0] : "";

        strs = session.getStrings(boxObj.id, urlOutAttr.id, 0, false, 0);
        String urlOut = (strs.length > 0) ? strs[0] : "";

        strs = session.getStrings(boxObj.id, xpathInAttr.id, 0, false, 0);
        String xpathIn = (strs.length > 0) ? strs[0] : "";

        strs = session.getStrings(boxObj.id, xpathOutAttr.id, 0, false, 0);
        String xpathOut = (strs.length > 0) ? strs[0] : "";

        strs = session.getStrings(boxObj.id, xpathTypeInAttr.id, 0, false, 0);
        String xpathTypeIn = (strs.length > 0) ? strs[0] : "";

        strs = session.getStrings(boxObj.id, xpathTypeOutAttr.id, 0, false, 0);
        String xpathTypeOut = (strs.length > 0) ? strs[0] : "";

        strs = session.getStrings(boxObj.id, xpathIdInit.id, 0, false, 0);
        String xpathInit = (strs.length > 0) ? strs[0] : "";

        strs = session.getStrings(boxObj.id, charSet.id, 0, false, 0);
        String charSets = (strs.length > 0) ? strs[0] : "";

        long[] isRst = session.getLongs(boxObj.id, isRestrict.id, 0);
        long restrict = (isRst.length > 0) ? isRst[0] : 0;

        long[] trs = session.getLongs(boxObj.id, transportAttr.id, 0);
        long transportId = (trs.length > 0) ? trs[0] : 0;
        long[] tms = session.getLongs(boxObj.id, typeMsgAttr.id, 0);
        long typeMsg = (tms.length > 0) ? tms[0] : 0;

        KrnObject[] objs = session.getObjects(boxObj.id, baseAttr.id, new long[0], 0);
        KrnObject baseObj = (objs.length > 0) ? objs[0] : null;
        byte[] config = session.getBlob(boxObj.id, exprAttr.id, 0,0,0);
        return new Box(boxObj, baseObj,name, urlIn,urlOut,xpathIn,xpathOut,
                xpathTypeIn,xpathTypeOut,xpathInit,config,charSets, restrict,transportId,typeMsg,this);
    }

    public void resetTransport(int transportId){
        if(transportId==DIRECTORY){
            directoryTransport.reset();
        }else if(transportId==MAIL){
            mailTransport.reset();
        }else if(transportId==MQ_TRANSPORT){
            mqTransport.reset();
        }else if(transportId==MQ_JMS){
            mqJmsTransport.reset();
        }else if(transportId==JBOSS_JMS){
            jbossJmsTransport.reset();
        }else if(transportId==WEB_SERVICE){
            wsTransport.reset();
        }else if(transportId==SGDS_TRANSPORT){
            sgdsTransport.reset();
        }else if(transportId==LOTUS_DIIOP){
            diiopTransport.reset();
        }
    }
    public void startTransport(int transportId){
        if(transportId==DIRECTORY){
//            directoryTransport.reset();
        }else if(transportId==MAIL){

        }else if(transportId==MQ_TRANSPORT){
            mqTransport.start();
		}else if(transportId==-MQ_TRANSPORT){
			mqTransport.stop();
        }else if(transportId==MQ_JMS){
//            mqJmsTransport.reset();
        }else if(transportId==JBOSS_JMS){
//            jbossJmsTransport.reset();
        }else if(transportId==WEB_SERVICE){
//            wsTransport.reset();
        }
    }
	public void stopTransport(int transportId){
		if(transportId==DIRECTORY){
//            directoryTransport.reset();
		}else if(transportId==MAIL){
            mailTransport.stop();
		}else if(transportId==MQ_TRANSPORT){
			mqTransport.stop();
		}else if(transportId==MQ_JMS){
//            mqJmsTransport.reset();
		}else if(transportId==JBOSS_JMS){
//            jbossJmsTransport.reset();
		}else if(transportId==WEB_SERVICE){
//            wsTransport.reset();
        }else if(transportId==SGDS_TRANSPORT){
//            sgdsTransport.stop();
		}else if(transportId==LOTUS_DIIOP){
            //diiopTransport.stop();
		}
	}
    public void setTransportParam(byte[] data,int transportId){
        try{
            if(transportId==DIRECTORY){
                directoryTransport.setTransportParam(data);
            }else if(transportId==MAIL){
                mailTransport.setTransportParam(data);
            }else if(transportId==MQ_TRANSPORT){
                mqTransport.setTransportParam(data);
            }else if(transportId==MQ_JMS){
                mqJmsTransport.setTransportParam(data);
            }else if(transportId==JBOSS_JMS){
                jbossJmsTransport.setTransportParam(data);
            }else if(transportId==WEB_SERVICE){
                wsTransport.setTransportParam(data);
            }else if(transportId==LOTUS_DIIOP){
                diiopTransport.setTransportParam(data);
            }else if(transportId==SGDS_TRANSPORT){
                //sgdsTransport.setTransportParam(data);
            }
        }catch(NullPointerException e){
        }catch(JDOMException e){
            log.error(e, e);
        }catch(IOException e){
            log.error(e, e);
        }
    }
    public byte[] getTransportParam(int transportId){
        try{
            if(transportId==DIRECTORY){
                return directoryTransport.getTransportParam();
            }else if(transportId==MAIL){
                return mailTransport.getTransportParam();
            }else if(transportId==MQ_TRANSPORT){
                return mqTransport.getTransportParam();
            }else if(transportId==MQ_JMS){
                return mqJmsTransport.getTransportParam();
            }else if(transportId==JBOSS_JMS){
                return jbossJmsTransport.getTransportParam();
            }else if(transportId==WEB_SERVICE){
                return wsTransport.getTransportParam();
            }else if(transportId==LOTUS_DIIOP){
                return diiopTransport.getTransportParam();
            //}else if(transportId==SGDS_TRANSPORT){
            //    return sgdsTransport.getTransportParam();
            }
        }catch(IOException e){
            log.error(e, e);
        }
        return new byte[0];
    }

    public void writeLogRecord(ExchangeEvents event,String msgId,String msgType,String fName,String url){
        String res=" | "+event.getEventType()
                  +" | " +event.getDescription()
                  +" | "+event.getType()
                  +" | "+ msgId 
                  +" | "+ msgType 
                  +" | "+ fName 
                  +" | "+ url;
		exchangeLog.info(res);
    }
    
    public void replayErrorMsg(Box box,int cod,String corelId){
        byte[] config=box.getExpr();
        if (config != null && config.length>0) {
            String expression=new String(config);
            boolean res_v=false;
            Element xml=null;
            Map<String,Object> vc = new HashMap<String,Object>();
            vc.put("COD",cod);
            vc.put("CORELID",corelId);
            Session session = null;
            try{
            	session = SrvUtils.getSession(dsName, "sys", null);
            	SrvOrLang orl = SrvOrLang.class.cast(session.getOrLang());
                res_v = orl.evaluate(expression, vc, null, false, new Stack<String>());
            } catch (Throwable e) {
                log.error(e.getMessage(),e);
            } finally {
            	session.release();
            }
        if(res_v){
            xml=(Element)vc.get("XML");
        }
        if(xml!=null)
            new Thread(new replaySend(box.getKrnObject().id,xml,corelId)).start();
        }

    }
    private class replaySend implements Runnable {
        private long boxId;
        private Element xml;
        private String corelId;

        public replaySend(long boxId,Element xml,String corelId) {
            this.boxId = boxId;
            this.xml = xml;
            this.corelId = corelId;
        }

        public void run() {
            try {
                dispose(OUT,boxId,0,xml,corelId);
            } catch (TransportException e) {
                log.error(e, e);
            }
        }
    }
    public static boolean isLimitThreads(){
       return ExecutionComponent.isLimitThreads();
    }
    public static long getBaseId(){
        return baseId;
    }
    public boolean transportStop(){
        isStopping=true;
        mqTransport.stop();
        directoryTransport.stop();
        mqJmsTransport.stop();
        jbossJmsTransport.stop();
        wsTransport.stop();
        //sgdsTransport.stop();
        //sgdsTransport.stop();
        mailTransport.stop();
        diiopTransport.stop();
        return true;
    }
    public boolean transportResume(){
        isStopping=false;
        mqTransport.resume();
        directoryTransport.resume();
        mqJmsTransport.resume();
        jbossJmsTransport.resume();
        wsTransport.resume();
        //sgdsTransport.resume();
        //sgdsTransport.resume();
        mailTransport.resume();
        diiopTransport.resume();
        return true;
    }
    public String resendMessage(String name,long boxId){
        String res="";
        Box box=boxMap.get(boxId);
        long transport =box.getTransportId();
        String boxName=box.getName();
        String outDir="",outArhDir="";
        if(DIRECTORY==transport){
            outArhDir=curDir+fileSepar+"OUT"+fileSepar+"ARH"+fileSepar
                    +"DIRECTORY"+fileSepar+boxName;
            outDir=curDir+fileSepar+"OUT"+fileSepar+"DIRECTORY"+fileSepar+boxName;

        }else if(MAIL==transport){
            outArhDir=curDir+fileSepar+"OUT"+fileSepar+"ARH"+fileSepar
            +"MAIL"+fileSepar+boxName;
            outDir=curDir+fileSepar+"OUT"+fileSepar+"MAIL"+fileSepar+boxName;

        }else if(MQ_TRANSPORT==transport){
            outArhDir=curDir+fileSepar+"OUT"+fileSepar+"ARH"+fileSepar
                    +"MQ_TRANSPORT"+fileSepar+boxName;
            outDir=curDir+fileSepar+"OUT"+fileSepar+"MQ_TRANSPORT"+fileSepar+boxName;
        }
        if(!outArhDir.equals("")){
            File dir = new File(outArhDir);
            dir.mkdirs();
            File file_=null;
            File[] files=dir.listFiles();
            try{
                if(files!=null && files.length>0){
                    for(File file:files){  // проверяю текущий архив
                        if(file.isFile() && file.getName().contains(name)){
                            File dir_=new File(outDir);
                            dir_.mkdirs();
                            String fname= file.getName();
                            if(fname.contains("."))
                             fname=fname.substring(0,fname.indexOf("."));
                            file_=new File(dir_,fname);
                            int i=0;
                            while(file_.exists()){
                                file_=new File(dir_,fname+"_"+i);
                                i++;
                            }
                            InputStream is = new FileInputStream(file);
                            OutputStream os = new FileOutputStream(file_);
                            if (is != null) {
                                kz.tamur.util.Funcs.writeStream(is, os, Constants.MAX_MESSAGE_SIZE);
                                is.close();
                            }
                            os.close();
                            res=name;
                            break;
                        }
                    }
                }
                if(res.equals("")){ // проверяю запакованный архив
                    String date=name.substring(name.indexOf("-")+1,name.lastIndexOf("-"));
                    String year=date.substring(0,4);
                    String month=date.substring(4,6);
                    String day=date.substring(6);
                    String outYearFile=curDir+fileSepar+"OUT"+fileSepar+"YEAR"+fileSepar
                            +year+fileSepar+month+fileSepar+day;
                    File file=new File(outYearFile);
                    if(file.exists()){
                        try{
                            ZipFile zf = new ZipFile(file);
                            Enumeration e = zf.entries();
                            while (e.hasMoreElements()) {
                                ZipEntry ze = (ZipEntry)e.nextElement();
                                String fname=  ze.getName();
                                if(fname.contains(name)){
                                    File dir_=new File(outDir);
                                    dir_.mkdirs();
                                    if(fname.contains("."))
                                     fname=fname.substring(0,fname.indexOf("."));
                                    file_=new File(dir_,fname.substring(fname.lastIndexOf(fileSepar)+1));
                                    FileOutputStream os = new FileOutputStream(file_);
                                    InputStream is = zf.getInputStream(ze);
                	            	kz.tamur.util.Funcs.writeStream(is, os, Constants.MAX_ARCHIVED_SIZE);
                                    os.close();
                                    is.close();
                                    res=name;
                                    break;
                                }
                            }
                            zf.close();
                        } catch (Exception e) {
                            log.error(e, e);
                        }

                    }
                }
                if(!res.equals("")){
                writeLogRecord(ExchangeEvents.MSC_000,
                        name,"dublicat",file_.getPath(),box.getUrlOut());
                }
            }catch(IOException ex){
                log.error(ex, ex);
            }
        }
        //
        return res;
    }
    public static String getDsName(){
    	return dsName;
    }
	private void initLogs(String dsName) {
		log = LogFactory.getLog(dsName + "." + (UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + getClass().getName());
	    exchangeLog = LogFactory.getLog(dsName + ".ExchangeLog" + (UserSession.SERVER_ID != null ? ("." + UserSession.SERVER_ID) : ""));
	    balansLog = LogFactory.getLog(dsName + ".BalansLog" + (UserSession.SERVER_ID != null ? ("." + UserSession.SERVER_ID) : ""));
	}

}
