package com.cifs.or2.server.exchange.transport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jdom.input.SAXBuilder;
import java.util.*;
import static java.util.Arrays.*;
import java.io.*;

import com.cifs.or2.server.exchange.Box;
import com.ibm.mq.*;

import kz.tamur.admin.ExchangeEvents;
import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.TransportIds;
import kz.tamur.server.wf.ExecutionEngine;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 13.05.2005
 * Time: 11:43:23
 * To change this template use File | Settings | File Templates.
 */
public class MqTransport implements Transport {


    private static Log log = LogFactory.getLog(MqTransport.class);

    private final HashMap<String,Box> receivedBox = new HashMap<String, Box>();
    private Map<Box,MQQueueManager> connections= Collections.synchronizedMap(new HashMap<Box, MQQueueManager>());
    private String inDir;
    private String outDir;
    private int maxCutMsg=10;
    private int maxDepthQueue=500;
    private int delay_p=100000;
    private int ccsid=1208;
    private boolean ready=false;
    private boolean connect=false;
    private boolean connecting=true;
    private boolean isStoping=false;
    
    private MessageCash messageCache;
    private Thread thread1=null,thread2=null;

    public MqTransport(MessageCash messageCache) {
    	this.messageCache = messageCache;
        inDir=messageCache.curDir+MessageCash.fileSepar+"IN"+MessageCash.fileSepar+"MQ_TRANSPORT";
        outDir=messageCache.curDir+MessageCash.fileSepar+"OUT"+MessageCash.fileSepar+"MQ_TRANSPORT";
        reset();
    }
    public void reset(){
        Properties ps= new Properties();
        try{
            File cf = new File(messageCache.t_props_file);
            if (cf.exists()) {
                ps.load(new FileInputStream(cf));
                String delay_=ps.getProperty("Mq_delay");
                if(delay_!=null)
                delay_p= Integer.valueOf(delay_);
                String ready_=ps.getProperty("Mq_ready");
                ready= (ready_!=null && ready_.equals("yes"));
                String ccsid_=ps.getProperty("ccsid");
                if(ccsid_!=null)
                ccsid= Integer.valueOf(ccsid_);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        if(ready){
        	isStoping=true;
        	if(thread1!=null){
	        	try {
					thread1.join();
				} catch (InterruptedException e) {
					log.error("wait stopping thread1 error");
				}
        	}
        	if(thread2!=null){
	        	try {
					thread2.join();
				} catch (InterruptedException e) {
					log.error("wait stopping thread2 error");
				}
        	}
        	isStoping=false;
        	connecting=true;
            restart(ready);
        }
    }

    public void restart(boolean isConnect){
    		if(!isConnect) return;
	        thread1 = new Thread(new Runnable() {
	            public void run() {
					while (true) {
	                    try {
	                    	received();
                            resend();
                            mqDisconnect();
	                        Thread.sleep(delay_p);
	                    } catch (Exception e) {
	                        log.error(e, e);
	                    }
	                    if(isStoping){
	    					log.info("thread1 is stopping");
	                    	break;
	                    }
	                }
	            }
	        });
	        thread1.setPriority(Thread.MIN_PRIORITY);
	        thread1.start();
	        thread2 = new Thread(new Runnable() {
	            public void run() {
					while (true) {
	                    try {
                            check();
	                        Thread.sleep(delay_p);
	                    } catch (Exception e) {
	                        log.error(e, e);
	                    }
	                    if(isStoping){
	    					log.info("thread2 is stopping");
	                    	break;
	                    }
	                }
	            }
	        });
	        thread2.setPriority(Thread.MIN_PRIORITY);
	        thread2.start();
        log.debug("MqTransport started");
    }

    public void start(){
    	isStoping=true;
    	if(thread1!=null){
        	try {
				thread1.join();
			} catch (InterruptedException e) {
				log.error("wait stopping thread1 error");
			}
    	}
    	if(thread2!=null){
        	try {
				thread2.join();
			} catch (InterruptedException e) {
				log.error("wait stopping thread2 error");
			}
    	}
        ready=true;
        isStoping=false;
        restart(true);
        log.debug("MqTransport started");
    }

    public int getId() {
        return TransportIds.MQ_TRANSPORT;
    }

    public String getName() {
        return "MQ_TRANSPORT";
    }

    public synchronized void resend(){
        File dir = new File(outDir);
        dir.mkdirs();
        File[] dirs = dir.listFiles();
        boolean connect_=true;
        for (File dir1 : dirs) {
            if (dir1.isDirectory()) {
                Box box = receivedBox.get(dir1.getName());
                if (box == null) continue;
                String queue = MessageCash.getParam(box.getUrlOut(), "queue");
                File[] files = dir1.listFiles();
                for (File file : files) {
                    int count=0;
                    if (!messageCache.isErrFile(file.getName()) && file.isFile() && file.canWrite()) {
                        try {
                            FileOutputStream osf = new FileOutputStream(file, true);
                            if (osf.getChannel().tryLock() != null)
                                osf.close();
                            else
                                continue;
                        } catch (IOException e) {
                            e.printStackTrace();
                            continue;
                        }
                        boolean result = false;
                        MQQueueManager qMgr = connections.get(box);
                        if (qMgr == null)
                            qMgr = mqConnect(box, MessageCash.OUT);
                        if (!isStoping && qMgr != null) {
                            String idInit = messageCache.getMessageInit(box, file);
                            try (
                            	InputStream is = new BufferedInputStream(new FileInputStream(file));
                            	)
                            {
//                                int openOptions =  MQC.MQOO_INQUIRE ;
//                                MQQueue mqQueue =  qMgr.accessQueue(queue, openOptions);
//                                count=mqQueue.getCurrentDepth();
//                                mqQueue.close();
//                                if(count<maxDepthQueue){
                                    
                                    int openOptions = MQC.MQOO_OUTPUT;
                                    MQQueue mqQueue = qMgr.accessQueue(queue, openOptions);
                                    MQMessage send_msg = new MQMessage();
                                    send_msg.format = MQC.MQFMT_STRING;
                                    send_msg.characterSet = 1208;
                                    if (is != null) {
                                        BufferedReader r = new BufferedReader(
                                                new InputStreamReader(is, "UTF-8"));
                                        char[] buf = new char[4096];
                                        int n = r.read(buf);

                                        int total = 0;
                                        
                                        int MAX_READ_COUNT = Constants.MAX_DOC_SIZE / 4096;
                                        int k = 0;
                                        
                                        while (k++ < MAX_READ_COUNT) {
                                        	if (n > -1)
                      		        	  		send_msg.writeString(new String(buf, 0, n));
                                        	else
                                        		break;
                                        	
                                            n = r.read(buf);
                                            total += n;

                      		        	  	if (total > Constants.MAX_DOC_SIZE)
                      		        	  		throw new IOException("Превышен допустимый размер документа: " + Constants.MAX_DOC_SIZE);
                                        }
                                        r.close();
                                    }
                                    if (idInit != null && idInit.length() > 0) {
                                        send_msg.correlationId = idInit.getBytes("UTF-8");
                                    }
                                    MQPutMessageOptions pmo = new MQPutMessageOptions();
                                    mqQueue.put(send_msg, pmo);
                                    mqQueue.close();
                                    result = true;
//                                }
                            } catch (IOException e) {
                                Element xml = messageCache.load(file);
                                log.info("ОШИБКА ПРИ ОТПРАВКЕ:" + "An error occurred whilst writing to the message buffer: " + e);
                                messageCache.writeLogRecord(ExchangeEvents.MQT_0031,
                                		messageCache.getMessageId(box, MessageCash.OUT, xml),
                                		messageCache.getMessageType(box, MessageCash.OUT, xml),
                                        file.getPath(), box.getUrlOut());
                            }
                            catch (MQException ex) {
                                Element xml = messageCache.load(file);
                                log.info("ОШИБКА ПРИ ОТПРАВКЕ:" + "A WebSphere MQ error occurred : Completion code " +
                                        ex.completionCode + " Reason code " + ex.reasonCode);
                                messageCache.writeLogRecord(ExchangeEvents.MQT_0030,
                                		messageCache.getMessageId(box, MessageCash.OUT, xml),
                                		messageCache.getMessageType(box, MessageCash.OUT, xml),
                                        file.getPath(), box.getUrlOut());
                            } finally {
                            	messageCache.messageSent(file, box, result);
                            }
                        } else if (connect_) {
                            connect_ = false;
                        }
                    }
                    if(count >=maxDepthQueue) break;
                }
                if(files.length==0){
                	messageCache.getRejectSentMessageBox(box.getKrnObject().id);
                }
            }
        }
        connect=connect_;
    }
    public void received(){
        synchronized(receivedBox) {
            Iterator<Box> boxIt = receivedBox.values().iterator();
            boolean connect_=true;
            while(boxIt.hasNext()) {
                Box box = boxIt.next();
                String queue= MessageCash.getParam(box.getUrlIn(),"queue");
                MQQueueManager qMgr=connections.get(box);
                if(qMgr==null)
                    qMgr=mqConnect(box,MessageCash.IN);
                if(!isStoping && qMgr!=null){
                    int count;
                    try {
                       int openOptions =  MQC.MQOO_INQUIRE ;
                       MQQueue mqQueue =  qMgr.accessQueue(queue, openOptions);
                       count=mqQueue.getCurrentDepth();
                       mqQueue.close();
                       if(count!=0){
                           openOptions =  MQC.MQOO_INPUT_AS_Q_DEF ;
                           mqQueue =  qMgr.accessQueue(queue, openOptions);
                            for (int i = 0; i < count; i++) {
                                String corelId_str="";
                                ByteArrayInputStream is;
                                ByteArrayOutputStream os=null;
                                try {
                                    os = new ByteArrayOutputStream();
                                    MQGetMessageOptions gmo = new MQGetMessageOptions();
                                    MQMessage receivedMessage = new MQMessage();
                                    mqQueue.get(receivedMessage, gmo);
                                    int length=receivedMessage.getMessageLength();
                                    byte[] corelId = receivedMessage.correlationId;
                                    if(corelId.length>0)
                                    corelId_str=new String(corelId);
                                    if (os != null) {
                                        byte[] buf = new byte[8 * 1024];
                                        int n;
                                        while ((n = length>buf.length?buf.length:length) > 0){
                                           receivedMessage.readFully(buf,0,n);
                                           os.write(buf, 0, n);
                                           length -= buf.length;
                                        }
                                    }
                                    os.close();
                                    is = new ByteArrayInputStream(os.toByteArray());
                                    SAXBuilder builder = new SAXBuilder();
                                    Document doc = builder.build(is);
                                    is.close();
                                    messageCache.
                                            dispose(MessageCash.IN,box.getKrnObject().id,0,doc.getRootElement(),corelId_str);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JDOMException e) {
                                    e.printStackTrace();
                                    if(os!=null){
                                        is = new ByteArrayInputStream(os.toByteArray());
                                        messageCache.writeToInvalid(box,is);
                                    }
                                    messageCache.replayErrorMsg(box,1,corelId_str);
                                } catch (TransportException e) {
                                    e.printStackTrace();
                                }
                            }
                            mqQueue.close();
                       }
                    }
                    catch (MQException ex)
                    {
                      log.info("ОШИБКА ПРИ ПОЛУЧЕНИИ:"+"A WebSphere MQ error occurred : Completion code " +
                                         ex.completionCode + " Reason code " + ex.reasonCode);
                      messageCache.writeLogRecord(ExchangeEvents.MQT_1020,"","","",box.getUrlIn());
                    }
                }else if(connect_){
                    connect_=false;
                }
            }
            connect=connect_;

        }
    }

    public void check(){
        File dir = new File(inDir);
        dir.mkdirs();
        File[] dirs = dir.listFiles();
        for (File dir1 : dirs) {
            if (dir1.isDirectory()) {
                File[] files = dir1.listFiles();
                sort(files, MessageCash.fileComparator);
                int count = files.length;
                Box box = receivedBox.get(dir1.getName());
                if (box == null) continue;
                if(ExecutionEngine.isMaxThreadCount()) continue;
                for (int i = 0; i < files.length && i<maxCutMsg; i++) {
                    if (files[i].isFile() && files[i].canWrite()) {
                        try {
                            FileOutputStream osf = new FileOutputStream(files[i], true);
                            if (osf.getChannel().tryLock() != null)
                                osf.close();
                            else
                                continue;
                        } catch (IOException e) {
                            e.printStackTrace();
                            continue;
                        }
                        messageCache.messageReceived(files[i], box);
                    }
                }
                if (count == 0) {
                	messageCache.getRejectMessageBox(box.getKrnObject().id);
                }
            }
        }
    }

    public void stop() {
        isStoping=true;
        log.debug("MqTransport stopped");
    }

    public void resume() {
        isStoping=false;
    }

    public void addTransportListener(Box box) {
        synchronized(receivedBox) {
            receivedBox.put(box.getName(),box);
        }
    }

    public void removeTransportListener(Box box) {
        synchronized(receivedBox) {
                receivedBox.remove(box.getName());
        }
    }

    public MQQueueManager mqConnect(Box box,int folderId){
        String url="";
        if(folderId==MessageCash.IN ){
            url=box.getUrlIn();
        }else if(folderId==MessageCash.OUT){
            url=box.getUrlOut();
        }
        if(isStoping || url.equals("")) return null;
        try {
            MQEnvironment.hostname = MessageCash.getParam(url,"hostname");
            MQEnvironment.channel  = MessageCash.getParam(url,"channel");
//            MQEnvironment.sslCipherSuite = "SSL_RSA_WITH_RC4_128_MD5";
            MQEnvironment.port = Integer.valueOf(MessageCash.getParam(url, "port"));
            MQEnvironment.CCSID = ccsid;
            MQEnvironment.userID=MessageCash.getParam(url,"userId");
            MQEnvironment.password=MessageCash.getParam(url,"pwd");
            MQQueueManager qMgr = new MQQueueManager(MessageCash.getParam(url,"qmgr"));
            connections.put(box,qMgr);
            if(!connecting){
                connecting=true;
                log.info("УСТАНОВЛЕНИЕ СВЯЗИ:"+"A WebSphere MQ" );
            }
            return qMgr;
         }
         catch (MQException ex)
         {
             if(connecting){
                 connecting=false;
                 log.info("ОШИБКА ПРИ УСТАНОВЛЕНИИ СВЯЗИ:"+"A WebSphere MQ error occurred : Completion code " +
                                    ex.completionCode + " Reason code " + ex.reasonCode);
                 messageCache.writeLogRecord(ExchangeEvents.MQT_001,"","","",url);
             }
         }
        return null;
    }

    public void mqDisconnect(){
    	synchronized (connections) {
	        for(Iterator<MQQueueManager> it = connections.values().iterator();it.hasNext();){
	            MQQueueManager qMgr=it.next();
	            try {
	                if(qMgr!=null)
	               qMgr.disconnect();
	               it.remove();
	            }
	            catch (MQException ex)
	            {
	              log.info("ОШИБКА ПРИ ЗАВЕРШЕНИИ СВЯЗИ:"+"A WebSphere MQ error occurred : Completion code " +
	                                 ex.completionCode + " Reason code " + ex.reasonCode);
	            }
	        }
    	}
    }
    public void setTransportParam(byte[] data)throws IOException,JDOMException{
    	boolean ready_=false;
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new ByteArrayInputStream(data), "UTF-8");
        Element root = doc.getRootElement();
        Element param=root.getChild("delay");
        if(param!=null) delay_p= Integer.valueOf(param.getText());
        param=root.getChild("ready");
        if(param!=null) {
        	ready_="true".equals(param.getText()); 
        	if(isStoping && ready) isStoping=true;
        }
        param=root.getChild("maxCutMsg");
        if(param!=null) maxCutMsg=Integer.valueOf(param.getText());
        if((ready && !ready_) || (!ready && ready_)){
        	ready=ready_;
	        if(ready){ 
	        	isStoping=false;
	        	connecting=true;
	        	restart(ready);
	        }else{
	        	isStoping=true;
	        	if(thread1!=null){
		        	try {
						thread1.join();
					} catch (InterruptedException e) {
						log.error("wait stopping thread1 error");
					}
	        	}
	        	if(thread2!=null){
		        	try {
						thread2.join();
					} catch (InterruptedException e) {
						log.error("wait stopping thread2 error");
					}
	        	}
	        }
        }
    }
    public byte[] getTransportParam() throws IOException {
        Element root = new Element("params");
        Element e = new Element("delay");
        e.setText(""+delay_p);
        root.addContent(e);
        e = new Element("ready");
        e.setText(""+ready);
        root.addContent(e);
        e = new Element("connect");
        e.setText(""+connect);
        root.addContent(e);
        e = new Element("maxCutMsg");
        e.setText(""+maxCutMsg);
        root.addContent(e);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLOutputter out = new XMLOutputter();
        out.getFormat().setEncoding("UTF-8");
        out.output(root, os);
        os.close();
        return os.toByteArray();
    }
}
