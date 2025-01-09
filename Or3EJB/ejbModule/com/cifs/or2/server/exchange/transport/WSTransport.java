package com.cifs.or2.server.exchange.transport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.JDOMException;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import java.io.*;
import java.util.*;

import com.cifs.or2.server.exchange.Box;
import kz.tamur.or3ee.common.TransportIds;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 24.04.2006
 * Time: 18:10:16
 * To change this template use File | Settings | File Templates.
 */
public class WSTransport implements Transport {
    private static Log log = LogFactory.getLog(WSTransport.class);
    private HashMap receivedBox = new HashMap();
    private String inDir;
    private String outDir;
    private Timer timer = new Timer();
    private TimerTask timerTask = null;
    private int delay = 20000;
    private int delay_p = 100000;
    private MessageCash messageCache;
    private boolean ready = false, connect = false;

    public WSTransport(MessageCash messageCache) {
        this.messageCache = messageCache;
        inDir=messageCache.curDir+MessageCash.fileSepar+"IN"+MessageCash.fileSepar+"WEB_SERVICE";
        outDir=messageCache.curDir+MessageCash.fileSepar+"OUT"+MessageCash.fileSepar+"WEB_SERVICE";
        reset();
    }

    public int getId() {
        return TransportIds.WEB_SERVICE;
    }

    public String getName() {
        return "WEB_SERVICE";
    }

    public void reset() {
        Properties ps= new Properties();
        try{
            File cf = new File(messageCache.t_props_file);
            if (cf.exists()) {
                ps.load(new FileInputStream(cf));
                String delay_=ps.getProperty("WS_delay");
                if(delay_!=null)
                delay_p= Integer.valueOf(delay_).intValue();
                String ready_=ps.getProperty("WS_ready");
                ready= (ready_!=null && ready_.equals("yes"));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        if(ready)
            restart(true);
    }

    public void restart(boolean isConnect) {
        timer.schedule(timerTask=new TimerTask() {
            public void run() {
                try {
                    resend();
                } catch (Exception e) {
                    log.error(e, e);
                }
            }
        }, delay, delay);
        log.debug("WebService Transport started");
    }

    public void resend() {
        File ddir = new File(outDir);
        ddir.mkdirs();
        File[] dirs = ddir.listFiles();
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
                        String urlOut = box.getUrlOut();
                        StringTokenizer st = new StringTokenizer(urlOut, "&");
                        String wsUri = st.nextToken();
                        String wsName = st.nextToken();

                        String fileName = files[j].getName();
                        int flowId=Integer.valueOf(fileName.substring(fileName.lastIndexOf("_")+1)).intValue();
                        Element root = null;
                        Element resXml = null;
                        try {
                            InputStream is = new BufferedInputStream(new FileInputStream(files[j]));
                            SAXBuilder builder = new SAXBuilder();
                            Document doc = builder.build(is);
                            is.close();
                            root = doc.getRootElement();
                            resXml = null;//wsHelper.callOperation(wsUri, wsName, root, session);
                            result = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                        	messageCache.messageSent(files[j],box,result);
                            if(result)
                            	try{
                            		messageCache.dispose(MessageCash.IN, box.getKrnObject().id, flowId, resXml,"");
                            	}catch(Exception ex){
                                    ex.printStackTrace();
                            		
                            	}
                        }
                    }
                }
            }
        }
        connect=connect_;
        check();
    }

    public void received() {
    }

    public void check() {
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
                Box box=(Box)receivedBox.get(dirs[i].getName());
                if(box==null) continue;
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

    public byte[] getTransportParam() throws IOException {
        return new byte[0];
    }

    public void setTransportParam(byte[] param) throws JDOMException, IOException {
    }

    public void stop() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void resume() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void addTransportListener(Box box) {
        synchronized (receivedBox) {
            receivedBox.put(box.getName(),box);
        }
    }

    public void removeTransportListener(Box box) {
        synchronized (receivedBox) {
            receivedBox.remove(box.getName());
        }
    }
}
