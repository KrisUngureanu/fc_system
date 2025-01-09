package com.cifs.or2.server.exchange.transport;

import com.cifs.or2.server.exchange.Box;
import kz.tamur.admin.ExchangeEvents;
import kz.tamur.or3ee.common.TransportIds;
import kz.tamur.or3ee.common.UserSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import javax.jms.*;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 25.06.2005
 * Time: 11:08:15
 * To change this template use File | Settings | File Templates.
 */
public class JbossJmsTransport implements Transport {


    private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + JbossJmsTransport.class.getName());

    private HashMap receivedBox = new HashMap();
    private Map connections=new HashMap();
    private Timer timer = new Timer();
    private TimerTask timerTask=null;
    private String inDir;
    private String outDir;
    private String inInv;
    private String url_pkg="org.jboss.naming:org.jnp.interfaces";
    private String icf="org.jboss.naming.remote.client.InitialContextFactory";
    private int delay=0;
    private int delay_p=10000;
    private boolean ready=false,connect=false;
    private boolean isStoping=false;
    private MessageCash messageCache;
    private InitialContext ctx;

    public JbossJmsTransport(MessageCash messageCache) {
        this.messageCache = messageCache;
        inDir=messageCache.curDir+MessageCash.fileSepar+"IN"+MessageCash.fileSepar+"JBOSS_JMS";
        outDir=messageCache.curDir+MessageCash.fileSepar+"OUT"+MessageCash.fileSepar+"JBOSS_JMS";
        inInv=messageCache.curDir+MessageCash.fileSepar+"IN"+MessageCash.fileSepar+"INV"+MessageCash.fileSepar+"JBOSS_JMS";
        reset();
    }
    public void reset(){
        Properties ps= new Properties();
        try{
            File cf = new File(messageCache.t_props_file);
            if (cf.exists()) {
                ps.load(new FileInputStream(cf));
                 String delay_=ps.getProperty("Jboss_delay");
                if(delay_!=null)
                 delay_p= Integer.valueOf(delay_).intValue();
                String ready_=ps.getProperty("Jboss_ready");
                ready= (ready_!=null && ready_.equals("yes"));
                url_pkg=ps.getProperty("Jboss_url_pkg");
                icf=ps.getProperty("Jboss_icf");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        restart(true);
    }
    public void restart(boolean isConnect){
        if(ready ){
            if(isConnect){
                 delay=delay_p;
            }else{
                if(timerTask!=null)
                    timerTask.cancel();
            }
            timer.schedule(timerTask=new TimerTask() {
                public void run() {
                    try {
                        if(!isStoping){
                        	received();
                        	check();
                        	resend();
                        	if(!connect){
                        		delay=10*delay;
                        	}
                        	if(delay!=delay_p){
                        		restart(connect);
                        	}
                        	mqDisconnect();
                        }
                    } catch (Exception e) {
                        log.error(e, e);
                    }
                }
            }, delay, delay);
            log.debug("JbossJmsTransport started");
        }

    }
    public int getId() {
        return TransportIds.JBOSS_JMS;
    }

    public String getName() {
        return "JBOSS_JMS";
    }

    public synchronized void resend(){
        File dir = new File(outDir);
        dir.mkdirs();
        File[] dirs = dir.listFiles();
        boolean connect_=true;
        for(int i=0;i<dirs.length;++i){
            if(dirs[i].isDirectory()){
                File[] files=dirs[i].listFiles();
                int count = files.length;
                if (count > 20) {
                    count = 20;
                }
                for(int j=0;j<files.length;++j){
                    if(files[j].isFile() && files[j].canWrite()){
                        boolean result=false;
                        Box box=(Box)receivedBox.get(dirs[i].getName());
                        if(box==null) continue;
            String queue= MessageCash.getParam(box.getUrlOut(),"queue");
            QueueConnection connection=(QueueConnection) connections.get(box);
            QueueSession qsession=null;
            if(connection==null)
                connection=mqConnect(box,MessageCash.OUT);
            if(connection!=null){
                try {
                    boolean transacted = false;
                    qsession = connection.createQueueSession(transacted,
                                                            QueueSession.AUTO_ACKNOWLEDGE);
                    Queue ioQueue = (Queue) ctx.lookup(queue);
                    QueueSender queueSender = qsession.createSender(ioQueue);
                    
                    InputStream is = new BufferedInputStream(new FileInputStream(files[j]));
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    if (is != null) {
                        int n = 0;
                        while ((n = is.read()) != -1){
                                bos.write(n);
                        }
                    }
                    TextMessage msg = qsession.createTextMessage(bos.toString("UTF-8"));
                    queueSender.send(msg);
                    bos.close();
                    is.close();
                    result=true;
                } catch (NamingException ex) {
                    Element xml = messageCache.load(files[j]);
                  log.info("ОШИБКА ПРИ ОТПРАВКЕ:"+"A Naming error occurred : Completion code " +
                          ex.getExplanation() + " Reason code " + ex.getMessage());
                  messageCache.writeLogRecord(ExchangeEvents.JMST_0030,
                                  messageCache.getMessageId(box,MessageCash.OUT,xml),
                                  messageCache.getMessageType(box,MessageCash.OUT,xml),
                            files[j].getPath(),box.getUrlOut());
                } catch (JMSException ex) {
                    Element xml = messageCache.load(files[j]);
                  log.info("ОШИБКА ПРИ ОТПРАВКЕ:"+"A JMS error occurred : Completion code " +
                          ex.getErrorCode() + " Reason code " + ex.getLinkedException());
                  messageCache.writeLogRecord(ExchangeEvents.JMST_0030,
                                  messageCache.getMessageId(box,MessageCash.OUT,xml),
                                  messageCache.getMessageType(box,MessageCash.OUT,xml),
                            files[j].getPath(),box.getUrlOut());
                }
                catch (java.io.IOException ex)
                {
                    Element xml = messageCache.load(files[j]);
                  log.info("ОШИБКА ПРИ ОТПРАВКЕ:"+"An error occurred whilst writing to the message buffer: " + ex);
                  messageCache.writeLogRecord(ExchangeEvents.JMST_0031,
                                  messageCache.getMessageId(box,MessageCash.OUT,xml),
                            messageCache.getMessageType(box,MessageCash.OUT,xml),
                            files[j].getPath(),box.getUrlOut());
                }finally{
                        messageCache.messageSent(files[j],box,result);
                    if(qsession!=null)
                        try {
                            qsession.close();
                        } catch (JMSException e) {
                            e.printStackTrace();
                        }
                }
            }else if(connect_){
                connect_=false;
            }
        }
                }
            }
        }
        connect=connect_;
    }
    public void received(){
        synchronized(receivedBox) {
            Iterator boxIt = receivedBox.values().iterator();
            boolean connect_=true;
            while(boxIt.hasNext()) {
                Box box = (Box)boxIt.next();
                String queue= MessageCash.getParam(box.getUrlIn(),"queue");
                QueueConnection connection=(QueueConnection) connections.get(box);
                QueueSession qsession=null;
                if(connection==null)
                    connection=mqConnect(box,MessageCash.IN);
                if(connection!=null){
                    try {
                        boolean transacted = false;
                        qsession = connection.createQueueSession(transacted,
                                                                QueueSession.AUTO_ACKNOWLEDGE);
                        Queue ioQueue;

                        //ioQueue = qsession.createQueue(queue);
                        ioQueue = (Queue) ctx.lookup(queue);
                        QueueReceiver queueReceiver = qsession.createReceiver(ioQueue);
                        Message inMessage ;
                        while((inMessage = queueReceiver.receiveNoWait())!=null){
                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            Document doc = null;
                            if (inMessage instanceof TextMessage) {
                                String replyString = ((TextMessage) inMessage).getText();
                                SAXBuilder builder = new SAXBuilder();
                                try{
                                        doc = builder.build(new StringReader(replyString.trim()));
                                }catch(Exception ex){
                                    File dir_inv=new File(inInv+MessageCash.fileSepar+box.getName());
                                    dir_inv.mkdirs();
                                    File fileInv=new File(dir_inv,"ERR_FILE");
                                    int i=0;
                                    while(fileInv.exists()){
                                        fileInv=new File(dir_inv,"ERR_FILE"+"_"+i);
                                        i++;
                                    }
                                    FileOutputStream fos = new FileOutputStream(fileInv);
                                    fos.write(replyString.getBytes("UTF-8"));
                                    fos.close();
                                    log.info("ОШИБКА ПРИ ПОЛУЧЕНИИ:не строится xml");
                                }
    //                            os.write(replyString.getBytes());
                            } else if (inMessage instanceof StreamMessage) {
                                StreamMessage replyStream = (StreamMessage) inMessage;
                                if (os != null) {
                                    byte[] buf = new byte[8 * 1024];
                                    int n = 0;
                                    while ((n = replyStream.readBytes(buf)) > 0){
                                       os.write(buf, 0, n);
                                       if(n<buf.length) break;
                                    }
                                    os.close();
                                    ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                                    SAXBuilder builder = new SAXBuilder();
                                    doc = builder.build(is);
                                    is.close();
                                }
                            }
                            if (doc != null) messageCache.
                                    dispose(MessageCash.IN,box.getKrnObject().id,0,doc.getRootElement(),"");
                        }
                    }
                    catch (NamingException ex)
                    {
                      log.info("ОШИБКА ПРИ ПОЛУЧЕНИИ:"+"A JMS error occurred : Completion code " +
                              ex);
                       ex.printStackTrace();
                       messageCache.writeLogRecord(ExchangeEvents.JMST_1020,"","","",box.getUrlIn());
                    }
                    catch (JMSException ex)
                    {
                      log.info("ОШИБКА ПРИ ПОЛУЧЕНИИ:"+"A JMS error occurred : Completion code " +
                              ex.getErrorCode() + " Reason code " + ex.getLinkedException());
                       ex.printStackTrace();
                       messageCache.writeLogRecord(ExchangeEvents.JMST_1020,"","","",box.getUrlIn());
                    }
                    catch (IOException ex)
                    {
                      log.info("ОШИБКА ПРИ ПОЛУЧЕНИИ:"+"An error occurred whilst writing to the message buffer: " + ex);
                      messageCache.writeLogRecord(ExchangeEvents.JMST_1021,"","","",box.getUrlIn());
                    } catch (JDOMException e) {
                        e.printStackTrace();
                    } catch (TransportException e) {
                        e.printStackTrace();
                    }finally{
                        if(qsession!=null)
                            try {
                                qsession.close();
                            } catch (JMSException e) {
                                e.printStackTrace();
                            }
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
        for(int i=0;i<dirs.length;++i){
            if(dirs[i].isDirectory()){
                Box box =(Box)receivedBox.get(dirs[i].getName());
                if(box==null) continue;
                File[] files=dirs[i].listFiles();
                int count = files.length;
                if (count > 10) {
                    count = 10;
                }
                for(int j=0;j<files.length;++j){
                    if(files[j].isFile() && files[j].canWrite())
                        messageCache.messageReceived(files[j], box);
                    }
                if(count==0){
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
        //To change body of implemented methods use File | Settings | File Templates.
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
    public QueueConnection mqConnect(Box box,int folderId){
        String url="";
        if(folderId==MessageCash.IN){
            url=box.getUrlIn();
        }else if(folderId==MessageCash.OUT){
            url=box.getUrlOut();
        }
        if(url.equals(""))return null;
        try {
        Hashtable properties = new java.util.Hashtable();
        String p_url= MessageCash.getParam(url,"url");//"192.168.13.8";
        String cfName= MessageCash.getParam(url,"cf_name");//"ConnectionFactory";
        String user_= MessageCash.getParam(url,"user");//"user";
        String pd_= MessageCash.getParam(url,"pwd");//"pd";
        properties.put(Context.INITIAL_CONTEXT_FACTORY, icf);
        //log.info(icf);
        properties.put(Context.URL_PKG_PREFIXES, url_pkg);
        //log.info(url_pkg);
        properties.put(Context.PROVIDER_URL, p_url);
        //log.info(p_url);
        if(!"".equals(user_)){
            properties.put(Context.SECURITY_PRINCIPAL, user_);
            properties.put(Context.SECURITY_CREDENTIALS,pd_);
        }
        ctx = new InitialContext(properties);
        //log.info(cfName);
        QueueConnectionFactory factory = (QueueConnectionFactory)ctx.lookup(cfName);
        QueueConnection connection;
        if(!"".equals(user_)){
            connection = factory.createQueueConnection(user_,pd_);
        }else{
            connection = factory.createQueueConnection();
        }

            connection.start();
            connections.put(box,connection);
            return connection;
        } catch (NamingException e) {
            log.info("Нет связи с " + url);
            e.printStackTrace();
         }catch (JMSException ex) {
            log.info("ОШИБКА ПРИ УСТАНОВЛЕНИИ СВЯЗИ:"+"A JMS error occurred : Completion code " +
                               ex.getErrorCode() + " Reason code " + ex.getLinkedException());
            messageCache.writeLogRecord(ExchangeEvents.JMST_001,"","","",url);
        }
        return null;
    }

    public void mqDisconnect(){
        for(Iterator it=connections.values().iterator();it.hasNext();){
            QueueConnection connection = (QueueConnection) it.next();
            try {
                if (connection != null) {
                    connection.stop();
                    connection.close();
                }
            } catch (JMSException ex) {
                log.info("ОШИБКА ПРИ ЗАВЕРШЕНИИ СВЯЗИ:" + "A JMS error occurred : Completion code " +
                        ex.getErrorCode() + " Reason code " + ex.getLinkedException());
            }
            it.remove();
        }
    }
    public void setTransportParam(byte[] data)throws IOException,JDOMException{
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new ByteArrayInputStream(data), "UTF-8");
        Element root = doc.getRootElement();
        Element param=root.getChild("delay");
        delay_p=Integer.valueOf(param.getText()).intValue();
        param=root.getChild("ready");
        ready="true".equals(param.getText());
        if(ready){
        	if(timer==null) 
        		timer = new Timer();
        	restart(ready);
            isStoping=false;
        }else
        	stop();
    }
    public byte[] getTransportParam() throws IOException {
        Element root = new Element("params");
        Element e = new Element("delay");
        e.setText(""+delay);
        root.addContent(e);
        e = new Element("ready");
        e.setText(""+ready);
        root.addContent(e);
        e = new Element("connect");
        e.setText(""+connect);
        root.addContent(e);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLOutputter out = new XMLOutputter();
        out.getFormat().setEncoding("UTF-8");
        out.output(root, os);
        os.close();
        return os.toByteArray();
    }
}
