package com.cifs.or2.server.exchange.transport;

import com.cifs.or2.server.exchange.Box;
import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.TransportIds;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.server.wf.ExecutionEngine;
import kz.tamur.util.Funcs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import java.io.*;
import static java.util.Arrays.sort;
import java.util.*;

public class DirectoryTransport implements Transport {

    private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + DirectoryTransport.class.getName());

    private final HashMap<String,Box> receivedBox = new HashMap<String,Box>();
    private String inDir;
    private String outDir;
    private int maxCutMsg=10;
    private Timer timer1 = new Timer();
//    private Timer timer2 = new Timer();
    private TimerTask timerTask1=null;
    private TimerTask timerTask2=null;
    private int delay= 0;
    private int delay_p=100000;
    private int fileDelay=0;
    private MessageCash messageCache;
    private boolean ready=false,connect=false,isStopping=false;

    public DirectoryTransport(MessageCash messageCache) {
        this.messageCache = messageCache;
        inDir=messageCache.curDir+MessageCash.fileSepar+"IN"+MessageCash.fileSepar+"DIRECTORY";
        outDir=messageCache.curDir+MessageCash.fileSepar+"OUT"+MessageCash.fileSepar+"DIRECTORY";
        reset();
    }
    public void reset(){
        Properties ps= new Properties();
        try{
            File cf = new File(messageCache.t_props_file);
            if (cf.exists()) {
                ps.load(new FileInputStream(cf));
                String delay_=ps.getProperty("Dir_delay");
                if(delay_!=null)
                delay_p= Integer.valueOf(delay_);
                String file_delay_=ps.getProperty("Dir_file_delay");
                if(file_delay_!=null)
                fileDelay= Integer.valueOf(file_delay_);
                delay=delay_p;
                String ready_=ps.getProperty("Dir_ready");
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
                if(timerTask1!=null)
                    timerTask1.cancel();
                if(timerTask2!=null)
                    timerTask2.cancel();
            }
            timer1.schedule(timerTask1=new TimerTask() {
                public void run() {
                    try {
                        if(!isStopping){
                            received();
                            resend();
                            check();
                        }
                    } catch (Exception e) {
                        log.error(e, e);
                    }
                }
            }, delay, delay);
            /*timer2.schedule(timerTask2=new TimerTask() {
                public void run() {
                    try {
                        if(!isStopping){
                            check();
                        }
                    } catch (Exception e) {
                        log.error(e, e);
                    }
                }
            }, delay, delay);
            */
            log.debug("DirectoryTransport started");
        }

    }
    public int getId() {
        return TransportIds.DIRECTORY;
    }

    public String getName() {
        return "DIRECTORY";
    }

    public void resend() {
        File ddir = new File(outDir);
        ddir.mkdirs();
        File[] dirs = ddir.listFiles();
        boolean connect_=true;
        for (File dir1 : dirs) {
            if (dir1.isDirectory()) {
                File[] files = dir1.listFiles();
                Box box = receivedBox.get(dir1.getName());
                if (box == null) continue;
                for (File file : files){
                    if (!messageCache.isErrFile(file.getName())&& file.isFile() && file.canWrite()) {
                        boolean result = false;
                        boolean insert = false;
                        try {
                            FileOutputStream osf = new FileOutputStream(file, true);
                            if (osf.getChannel().tryLock() != null) {
                                osf.close();
                                insert = true;
                                File dir = new File(box.getUrlOut());
                                dir.mkdirs();
                                String name = "~"+file.getName();
                                File out = new File(dir, name);
                                int l = 0;
                                while (out.exists()) {
                                    out = new File(dir, name + "_" + l);
                                    l++;
                                }
                                OutputStream os = new BufferedOutputStream(new FileOutputStream(out));
                                InputStream is = new BufferedInputStream(new FileInputStream(file));
                                if (is != null) {
                                    Funcs.writeStream(is, os, Constants.MAX_MESSAGE_SIZE);
                                    is.close();
                                }
                                os.close();
                                String name_=out.getName().substring(1);
                                File out_ = new File(dir, name_+".xml");
                                l = 0;
                                while (out_.exists()) {
                                    out_ = new File(dir, name_ + "_" + l + ".xml");
                                    l++;
                                }
                                out.renameTo(out_);
                                result = true;
                            } else
                                osf.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            connect_ = false;
                        } finally {
                            if(insert)
                            	messageCache.messageSent(file, box, result);
                        }
                    }
                }
//                if(files.length==0){
//                    MessageCash.getRejectSentMessageBox(box.getKrnObject().id);
//                }
            }
        }
        connect=connect_;
    }

    public void received() {
        synchronized(receivedBox) {
            Iterator boxIt = receivedBox.values().iterator();
            boolean connect_=true;
            while(boxIt.hasNext()) {
                Box box = (Box)boxIt.next();
//                if(box.getBase()==null || box.getBase().id!=MessageCash.getBaseId())continue;
                if(box.getRestrict()==1 && MessageCash.isLimitThreads())
                    continue;
                File dir = new File(box.getUrlIn());
                dir.mkdirs();
                File[] files = dir.listFiles();
                if(files==null) continue;
                long curTime=System.currentTimeMillis();
                for (int i = 0; i < files.length; i++) {
                    try {
                        File file = files[i];
                        if(file.isDirectory()) continue;
                        if(file.getName().substring(0,1).equals("~")) continue;
                        if(fileDelay>0 && curTime-file.lastModified()<=fileDelay) continue;
                        FileOutputStream os = new FileOutputStream(file,true);
                        if(os.getChannel().tryLock()!=null){
                            os.close();
                            Element xml=null;
                            if(box.getTypeMsg()== Constants.MSG_XML_INT){
                                xml = messageCache.load(file);
                            }
                            if (xml != null) {
                            	messageCache.dispose(MessageCash.IN,box.getKrnObject().id,0,xml,file.getName());
                            } else {
                            	messageCache.dispose(MessageCash.IN,box.getKrnObject().id,0,file,file.getName());
//                                MessageCash.writeToInvalid(box,file);
//                                MessageCash.instance(session).replayErrorMsg(box,1,file.getName());
                            }
                            file.delete();
                        }else
                            os.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
        isStopping=true;
    }

    public void resume() {
        isStopping=false;
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
    public void setTransportParam(byte[] data)throws IOException,JDOMException{
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new ByteArrayInputStream(data), "UTF-8");
        Element root = doc.getRootElement();
        Element param=root.getChild("delay");
        delay_p= Integer.valueOf(param.getText());
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
