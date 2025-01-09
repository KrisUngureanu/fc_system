package com.cifs.or2.server.exchange.transport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.XMLOutputter;
import org.jdom.input.SAXBuilder;

import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

import com.cifs.or2.server.exchange.Box;
import com.jacob.com.Variant;

import kz.gov.pki.kalkan.util.encoders.Base64;
import kz.tamur.or3ee.common.TransportIds;
import kz.tamur.sgds.*;

/**
 * Created by IntelliJ IDEA.
 * User: Valeri
 * Date: 18.12.2006
 * Time: 10:44:26
 * To change this template use File | Settings | File Templates.
 */
public class SGDSTransport implements Transport {
    private static Log log = LogFactory.getLog(SGDSTransport.class);

    private final HashMap<String,Box> receivedBox = new HashMap<String,Box>();
    private final HashMap<String,Box> receivedUrlBox = new HashMap<String,Box>();
    private String inDir;
    private String outDir;
    private int delay_p=100000;
    private boolean ready=false;
    private MessageCash messageCache;
    private boolean connect=false;
    private boolean isStoping=false;
    private String dsName;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
    private DeliverySubSystem2 dss;

    public SGDSTransport(MessageCash messageCache) {
        this.messageCache = messageCache;
        inDir=messageCache.curDir+MessageCash.fileSepar+"IN"+MessageCash.fileSepar+"SGDS_TRANSPORT";
        outDir=messageCache.curDir+MessageCash.fileSepar+"OUT"+MessageCash.fileSepar+"SGDS_TRANSPORT";
        reset();
    }
    public void reset(){
        Properties ps= new Properties();
        try{
            File cf = new File(messageCache.t_props_file);
            if (cf.exists()) {
                ps.load(new FileInputStream(cf));
                String delay_=ps.getProperty("Sgds_delay");
                if(delay_!=null)
                delay_p= Integer.valueOf(delay_);
                String ready_=ps.getProperty("Sgds_ready");
                ready= (ready_!=null && ready_.equals("yes"));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        if(ready)
            restart(true);
    }

    public void restart(final boolean isConnect){
        Thread thread = new Thread(new Runnable() {
            public void run() {
            	sgdsConnect(3);
                if(dss!=null)
                while (true) {
                    try {
                        if(!isStoping){
                            received();
                            check();
                            resend();
                            Thread.sleep(delay_p);
                        }else{
                            sgdsDisconnect();
                        }
                    } catch (Exception e) {
                        log.error(e, e);
                    }
                    if(!isConnect) break;
                }
            }
        });
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
        log.debug("SgdsTransport started");
    }

    public void start(){
        if(!ready) {
            ready=true;
            isStoping=false;
            restart(true);
        }
        if(isStoping)
            isStoping=false;
    }

    public int getId() {
        return TransportIds.SGDS_TRANSPORT;
    }

    public String getName() {
        return "SGDS_TRANSPORT";
    }

    public synchronized void resend(){
        File dir = new File(outDir);
        dir.mkdirs();
        File[] dirs = dir.listFiles();
        boolean connect_=true;
        try{
            dss.initConnection(3);
            for (File dir1 : dirs) {
                if (dir1.isDirectory()) {
                    File[] files = dir1.listFiles();
                    Box box = receivedBox.get(dir1.getName());
                    for (File file : files) {
                        if (file.isFile() && file.canWrite()) {
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
                            try {
                                InputStream is = new BufferedInputStream(new FileInputStream(file));
                                SAXBuilder builder = new SAXBuilder();
                                Document doc = builder.build(is);
                                String msg = wrapSentMessage(doc.getRootElement(), box);
                                if (msg == null || msg.equals("")) continue;
                                String[] lastParam = {""};
                                dss.putBinMsg(msg, lastParam);
                                dss.commitOnPut();
                                result = true;
                                OutputStream fos = new FileOutputStream(lastParam[0] + ".xml");
                                fos.write(msg.getBytes("UTF-8"));
                                fos.close();
                                log.info("Successfully send message " + file.getName() + " Id=" + lastParam[0]);
                            } catch (JDOMException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                dss.rollBackOnPut();
                            } finally {
                            	messageCache.messageSent(file, box, result);
                            }
                        }
                    }
                }
            }
            connect=connect_;
        }catch(Exception e){
            log.error(e, e);
        }
    }
    public void received(){
        synchronized(receivedBox) {
            boolean connect_=true;
            try{
                dss.initConnection(3);
                Variant count = dss.getInBoxMessages();
                for (int i = 0; i < count.changeType(Variant.VariantInt).getInt(); ++i) {
                    String[] msg=new String[]{""};
                    String[] msgId=new String[]{""};
                    try {
                        dss.getMessage(msg, msgId);
                        unwrapReceivedMessage(msg[0],msgId[0]);
                        dss.commitOnGet();
                        log.info("Successfully received message " + msgId.toString());
                    } catch (Exception ex) {
                        log.error(ex,ex);
                        dss.rollBackOnGet();
                    }
                }
                connect=connect_;
            }catch(Exception e){
                log.error(e, e);
            }

        }
    }
    private boolean unwrapReceivedMessage(String msg,String msgId){
        SAXBuilder builder = new SAXBuilder();
        Document doc;
        try {
            InputStream is = new ByteArrayInputStream(msg.getBytes());
            doc = builder.build(is);
            Element root=doc.getRootElement();
            Element to=root.getChild("to");
            String client=to.getAttributeValue("client");
            Element e=root.getChild("document");
            String res=e.getText();
            is = new ByteArrayInputStream(Base64.decode(res));
            doc = builder.build(is);
            Box box=receivedUrlBox.get(client);
            if(box!=null){
            	messageCache.
                            dispose(MessageCash.IN,box.getKrnObject().id,0,doc.getRootElement(),msgId);
                return true;
            }
        } catch (TransportException e) {
            e.printStackTrace();
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    private String wrapSentMessage(Element xml,Box box){
        String url,id ;
        url=box.getUrlIn();
        id = messageCache.getMessageId(box,MessageCash.IN,xml);
        try {
            Element root=new Element("message");
            root.setAttribute("datetime", formatter.format(new Date()));
            root.setAttribute("notify", "none");
            root.setAttribute("log", "n");
            root.setAttribute("cryto", "none");
            root.setAttribute("compress", "n");
            Element e=new Element("to");
            e.setAttribute("client", url/*toClient*/);
            e.setAttribute("application","KADRY" /*application*/);
            e.setAttribute("session", "0"/*session*/);
            root.addContent(e);
            e=new Element("from");
            e.setAttribute("client", dsName);
            e.setAttribute("application", "KADRY");
            e.setAttribute("session","0");
            root.addContent(e);
            e=new Element("document");
            e.setAttribute("type", "gds-note");
            e.setAttribute("version", "1");
            e.setAttribute("id", id);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            XMLOutputter out = new XMLOutputter();
            out.getFormat().setEncoding("UTF-8");
            xml.detach();
            out.output(new Document(xml), bos);
            bos.close();
            e.setText(new String(Base64.encode(bos.toByteArray()), "UTF-8"));
            root.addContent(e);
            bos = new ByteArrayOutputStream();
            out = new XMLOutputter();
            out.getFormat().setEncoding("UTF-8");
            out.output(new Document(root), bos);
            bos.close();
            return bos.toString();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }
    public void check(){
        File dir = new File(inDir);
        dir.mkdirs();
        File[] dirs = dir.listFiles();
        for (File dir1 : dirs) {
            if (dir1.isDirectory()) {
                File[] files = dir1.listFiles();
                int count = files.length;
                if (count > 10) {
                    count = 10;
                }
                Box box = receivedBox.get(dir1.getName());
                if (box == null) continue;
                for (File file : files) {
                    if (file.isFile() && file.canWrite()) {
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
                        messageCache.messageReceived(file, box);
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

    }

    public void resume() {
        isStoping=false;
    }

    public void addTransportListener(Box box) {
        synchronized(receivedBox) {
            receivedBox.put(box.getName(),box);
            if (box.getUrlIn() != null && box.getUrlIn().length() > 0) {
            	receivedUrlBox.put(box.getUrlIn(),box);
            }
        }
    }

    public void removeTransportListener(Box box) {
        synchronized(receivedBox) {
                receivedBox.remove(box.getName());
            if (box.getUrlIn() != null && box.getUrlIn().length() > 0) {
            	receivedUrlBox.remove(box.getUrlIn());
            }
        }
    }

    private IDeliverySubSystem2 sgdsConnect(int mode){
        try{
          dss = new DeliverySubSystem2();
          dss.appRegistration("KADRY", "0");
          String[] lastName= {""};
          dss.getDSName(lastName);
          dsName = lastName[0];
        }catch(Exception e){
            log.error(e,e);
        }
        log.info("Connected to ClientDS mode=" + mode);
        return dss;
    }
    private void sgdsDisconnect(){
        try{
        if(dss!=null)
            dss.safeRelease();
        }catch(Exception e){
            log.error(e,e);
        }
    }

    public void setTransportParam(byte[] data)throws IOException,JDOMException{
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new ByteArrayInputStream(data), "UTF-8");
        Element root = doc.getRootElement();
        Element param=root.getChild("delay");
        delay_p= Integer.valueOf(param.getText());
        param=root.getChild("ready");
        ready="true".equals(param.getText());
        restart(connect);
    }
    public byte[] getTransportParam() throws IOException {
        Element root = new Element("params");
        Element e = new Element("delay");
        e.setText(""+delay_p);
        root.addContent(e);
        e = new Element("ready");
        e.setText("true");
        root.addContent(e);
        e = new Element("connect");
        e.setText("true");
        root.addContent(e);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        XMLOutputter out = new XMLOutputter();
        out.getFormat().setEncoding("UTF-8");
        out.output(root, os);
        os.close();
        return os.toByteArray();
    }
}
