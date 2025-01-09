package com.cifs.or2.server.exchange.transport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jdom.input.SAXBuilder;

import java.util.*;
import java.io.*;

import com.cifs.or2.server.exchange.Box;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.mq.jms.JMSC;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.jms.*;
import javax.jms.Queue;

import kz.tamur.admin.ExchangeEvents;
import kz.tamur.or3ee.common.TransportIds;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 18.06.2005
 * Time: 12:45:02
 * To change this template use File | Settings | File Templates.
 */
public class MqJmsTransport implements Transport {


    private static Log log = LogFactory.getLog(MqJmsTransport.class);

    private HashMap receivedBox = new HashMap();
    private Map connections=new HashMap();
    private Timer timer = new Timer();
    private TimerTask timerTask=null;
    private String inDir;
    private String outDir;
    private int delay=0;
    private int delay_p=100000;
    private boolean ready=false,connect=false;
    private MessageCash messageCache;

    public MqJmsTransport(MessageCash messageCache) {
        this.messageCache = messageCache;
        inDir=messageCache.curDir+MessageCash.fileSepar+"IN"+MessageCash.fileSepar+"MQ_JMS";
        outDir=messageCache.curDir+MessageCash.fileSepar+"OUT"+MessageCash.fileSepar+"MQ_JMS";
        reset();
    }
    public void reset(){
        Properties ps= new Properties();
        try{
            File cf = new File(messageCache.t_props_file);
            if (cf.exists()) {
                ps.load(new FileInputStream(cf));
                String delay_=ps.getProperty("Jms_delay");
                if(delay_!=null)
                delay_p= Integer.valueOf(delay_).intValue();
                String ready_=ps.getProperty("Jms_ready");
                ready= (ready_!=null && ready_.equals("yes"));
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
                    } catch (Exception e) {
                        log.error(e, e);
                    }
                }
            }, delay, delay);
            log.debug("MqTransport started");
        }

    }
    public int getId() {
        return TransportIds.MQ_JMS;
    }

    public String getName() {
        return "MQ_JMS";
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
                    Queue ioQueue;
                    ioQueue = qsession.createQueue(queue);
                    QueueSender queueSender = qsession.createSender(ioQueue);
                    StreamMessage outMessage = qsession.createStreamMessage();
                    InputStream is = new BufferedInputStream(new FileInputStream(files[j]));
                    if (is != null) {
                        byte[] buf = new byte[8 * 1024];
                        int n = 0;
                        while ((n = is.read(buf)) > 0){
                           outMessage.writeBytes(buf, 0, n);
                        }
                    }
                    queueSender.send(outMessage);
                    is.close();
                    result=true;
                } catch (JMSException ex) {
                    Element xml = messageCache.load(files[j]);
                  log.info("ОШИБКА ПРИ ОТПРАВКЕ:"+"A JMS MQ error occurred : Completion code " +
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
                        ioQueue = qsession.createQueue(queue);
                        QueueReceiver queueReceiver = qsession.createReceiver(ioQueue);
                        Message inMessage ;
                        while((inMessage = queueReceiver.receiveNoWait())!=null){
                            ByteArrayOutputStream os = new ByteArrayOutputStream();
                            if (inMessage instanceof TextMessage) {
                              String replyString = ((TextMessage) inMessage).getText();
                                os.write(replyString.getBytes());
                            } else if (inMessage instanceof StreamMessage) {
                                StreamMessage replyStream = (StreamMessage) inMessage;
                                if (os != null) {
                                    byte[] buf = new byte[8 * 1024];
                                    int n = 0;
                                    while ((n = replyStream.readBytes(buf)) > 0){
                                       os.write(buf, 0, n);
                                       if(n<buf.length) break;
                                    }
                                }
                            }
                            os.close();
                            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
                            SAXBuilder builder = new SAXBuilder();
                            Document doc = builder.build(is);
                            is.close();
                            messageCache.
                                    dispose(MessageCash.IN,box.getKrnObject().id,0,doc.getRootElement(),"");
                        }
                    }
                    catch (JMSException ex)
                    {
                      log.info("ОШИБКА ПРИ ПОЛУЧЕНИИ:"+"A JMS MQ error occurred : Completion code " +
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
                File[] files=dirs[i].listFiles();
                int count = files.length;
                if (count > 10) {
                    count = 10;
                }
                Box box =(Box)receivedBox.get(dirs[i].getName());
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
        //To change body of implemented methods use File | Settings | File Templates.
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
            Hashtable environment = new java.util.Hashtable();
            String p_url= MessageCash.getParam(url,"url");//"file:/C:/JNDI-Directory";
            String icf =MessageCash.getParam(url,"icf");//"com.sun.jndi.fscontext.RefFSContextFactory";
            String cfName= MessageCash.getParam(url,"cf_name");//"ivtQCF";
            environment.put(Context.INITIAL_CONTEXT_FACTORY, icf);
            environment.put(Context.PROVIDER_URL, p_url);
            Context ctx = new InitialDirContext( environment );
            MQQueueConnectionFactory factory = (MQQueueConnectionFactory)ctx.lookup(cfName);
            factory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
            QueueConnection connection = factory.createQueueConnection();
            connection.start();
           connections.put(box,connection);
            return connection;
        } catch (NamingException e) {
                e.printStackTrace();
         }catch (JMSException ex) {
            log.info("ОШИБКА ПРИ УСТАНОВЛЕНИИ СВЯЗИ:"+"A JMS MQ error occurred : Completion code " +
                               ex.getErrorCode() + " Reason code " + ex.getLinkedException());
            messageCache.writeLogRecord(ExchangeEvents.JMST_001,
                         "","","",url);
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
                log.info("ОШИБКА ПРИ ЗАВЕРШЕНИИ СВЯЗИ:" + "A JMS MQ error occurred : Completion code " +
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
        ready=Boolean.getBoolean(param.getText());
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
