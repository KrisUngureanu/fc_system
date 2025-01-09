package com.cifs.or2.server.exchange.transport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import org.jdom.input.SAXBuilder;
import com.cifs.or2.server.exchange.Box;

import java.util.*;
import java.io.*;

import kz.tamur.admin.ExchangeEvents;
import kz.tamur.or3ee.common.TransportIds;
import lotus.domino.MIMEEntity;
import lotus.domino.MIMEHeader;
import lotus.domino.Stream;

/**
 * Created by IntelliJ IDEA.
 * User: Valeri
 * Date: 04.07.2007
 * Time: 18:42:56
 * To change this template use File | Settings | File Templates.
 */
public class LotusDIIOPTransport implements Transport {

    private static Log log = LogFactory.getLog(LotusDIIOPTransport.class);

    private final HashMap<String, Box> receivedBox = new HashMap<String,Box>();
    //private String inDir;
    private String outDir;
    //private int maxCutMsg=10;
    private Timer timer1 = new Timer();
    private Timer timer2 = new Timer();
    private TimerTask timerTask1=null;
    private TimerTask timerTask2=null;
    private int delay= 0;
    private int delay_p=100000;
    private MessageCash messageCache;
    private boolean ready=false,connect=false,isStopping=false;

    public LotusDIIOPTransport(MessageCash messageCache) {
        this.messageCache = messageCache;
        //inDir=messageCache.curDir+MessageCash.fileSepar+"IN"+MessageCash.fileSepar+"MAIL";
        outDir=messageCache.curDir+MessageCash.fileSepar+"OUT"+MessageCash.fileSepar+"LotusDIIOP";
        reset();
    }
    
    public void reset(){
        Properties ps= new Properties();
        try{
            File cf = new File(messageCache.t_props_file);
            if (cf.exists()) {
                ps.load(new FileInputStream(cf));
                String delay_=ps.getProperty("DIIOP_delay");
                if(delay_!=null)
                delay_p= Integer.valueOf(delay_);
                delay=delay_p;
                String ready_ = ps.getProperty("DIIOP_ready");
                ready = "yes".equals(ready_);
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
                        }
                    } catch (Exception e) {
                        log.error(e, e);
                    }
                }
            }, delay, delay);
            timer2.schedule(timerTask2=new TimerTask() {
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
            log.info("LotusDIIOPTransport started");
        }

    }
    public int getId() {
        return TransportIds.LOTUS_DIIOP;
    }

    public String getName() {
        return "LotusDIIOP";
    }

    public void resend() {
        File ddir = new File(outDir);
        ddir.mkdirs();
        File[] dirs = ddir.listFiles();
        boolean connect_ = true;
        
		lotus.domino.Session dominoSession = null;
		lotus.domino.Database dominoDb = null;

        String url ="";
        try {
	        for (File dir1 : dirs) {
	            if (dir1.isDirectory()) {
	                Box box = receivedBox.get(dir1.getName());
	                if (box == null) continue;
	                url = box.getUrlOut();
	                if (url.equals("")) continue;
	                File[] files = dir1.listFiles();
	                if(files.length > 0) {
	                	if (dominoSession == null) {
	    	                String host=MessageCash.getParam(url,"host");
	    	                String port=MessageCash.getParam(url,"port");
	    	                String user=MessageCash.getParam(url,"user");
	    	                String pd=MessageCash.getParam(url,"passwd");

	    	                String dominoServer = host + (port.length() > 0 ? ":" + port : "63148");

	    	                dominoSession = lotus.domino.NotesFactory.createSession( dominoServer, user, pd );
	    	    			dominoDb = dominoSession.getDbDirectory(dominoServer).openMailDatabase();

	    	    			log.info("Connected to Lotus Domino as: " + dominoSession.getUserName() + " to mailbox: " + dominoDb);
	                	}
	                }
	                for (File file : files) {
	                    if (file.isFile() && file.canWrite()) {
	                        boolean result = false;
	                        try {
	                            FileOutputStream osf = new FileOutputStream(file, true);
	                            if (osf.getChannel().tryLock() != null) {
	                                osf.close();
	                                InputStream is = new BufferedInputStream(new FileInputStream(file));
	                                
	                                SAXBuilder builder = new SAXBuilder();
	                                Document doc = builder.build(is);
	                                is.close();
	                                Element message = doc.getRootElement();

	                                lotus.domino.Document memo = dominoDb.createDocument();
	                    			memo.replaceItemValue("Form", "Memo");
	                    			
	                    			String par = message.getChildText("Importance");
	                    			if (par != null && par.length() > 0)
	                    				memo.replaceItemValue("Importance", par);
	                    			
	                    			par = message.getChildText("Subject");
	                    			if (par != null && par.length() > 0)
	                    				//par = MimeUtility.encodeText(par, "koi8-r", null);
	                    				memo.replaceItemValue("Subject", par);

	                    			par = message.getChildText("Body");
	                    			if (par != null && par.length() > 0) {

	                    				if (memo.hasItem("Body"))
	                    					memo.removeItem("Body");
	                    				
	                    				dominoSession.setConvertMIME(false);

	                    				MIMEEntity body = memo.createMIMEEntity("Body"); //This line errors if you try and rebuild the message between sends!
	                    				MIMEHeader mh = body.createHeader("MIME-Version");
	                    				mh.setHeaderVal("1.0");

	                    				mh = body.createHeader("Content-Type");
	                    				mh.setHeaderValAndParams( "multipart/alternative;boundary=\"=NextPart_=\"");

	                    				//Now send the HTML part. Order is important!
	                    				MIMEEntity mc = body.createChildEntity();
	                    				Stream stream = dominoSession.createStream();

	                    				mc = body.createChildEntity();

	                    				stream.writeText(par, Stream.EOL_CR);
	                    				mc.setContentFromText(stream, "text/html;charset=\"UTF-8\"", MIMEEntity.ENC_NONE);

	                    				memo.closeMIMEEntities(true);

	                    				dominoSession.setConvertMime(true);
	                    			}
	                    				
	                    			par = message.getChildText("SendTo");
	                    			if (par != null && par.length() > 0) {
	                    				memo.replaceItemValue("SendTo", par);
		                    			memo.send(false, par);
	                    			}	                    			
	                    			
	                                result = true;
	                            } else
	                                osf.close();
	                        } catch (IOException e) {
	                            log.info("Ошибка при записи в буфер сообщения отправляемого по Lotus Notes DIIOP" + e);
	                            messageCache.writeLogRecord(ExchangeEvents.EML_0030,
	                            		file.getName(),
	                                    "mailSend",
	                                    "", url);
	                            connect_ = false;
	                        } catch (Exception e) {
	                            log.info("Ошибка при отправке сообщения Lotus Notes DIIOP" + e);
	                            messageCache.writeLogRecord(ExchangeEvents.EML_0031,
	                            		file.getName(),
	                                    "mailSend",
	                                    "", url);
	                        } finally {
                            	messageCache.messageSent(file, box, result);
	                        }
	                    }
	                }
	            }
	        }
		} catch (Exception e1) {
            log.info("Ошибка при установлении связи Lotus Notes DIIOP" + e1);
            messageCache.writeLogRecord(ExchangeEvents.EML_001,
            		"",
                    "mailSend",
                    "", url);
        } finally {
            try {
            	if (dominoDb != null)
            		dominoDb.recycle();
            	if (dominoSession != null)
            		dominoSession.recycle();
            	
            	dominoDb = null;
            	dominoSession = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
		}
        connect=connect_;
    }

    public void received() {
    }

    public void check(){
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
    public void setTransportParam(byte[] data)throws IOException, JDOMException {
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
