package com.cifs.or2.server.exchange.transport.gdms3;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.jdom.JDOMException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jdom.input.SAXBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.server.exchange.Box;
import com.cifs.or2.server.exchange.transport.Transport;
import com.cifs.or2.server.exchange.transport.MessageCash;
import kz.tamur.or3ee.common.TransportIds;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;
import kz.tamur.comps.Constants;

public class Gdms3Transport implements Transport {

    static {
        //System.loadLibrary("sgds3");
    }
    private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + "Gdms3Transport");

     private final HashMap<String,Box> receivedBox = new HashMap<String,Box>();
     private final HashMap<String,Box> receivedUrlBox = new HashMap<String,Box>();
     private String inDir;
    private String inInvDir;
     private String outDir;
     private int delay_p=100000;
     private boolean ready=false;
     private MessageCash messageCache;
     private boolean connect=false;
     private boolean isStoping=false;
     private boolean loging=false;
     private String dsHost="localhost";
     private String dsDomain;
     private String dsName= "berikb";
     private String dsPD=",fehcfr";
     private String dsApplication= "Kadry";
     private String dsSession="0";
     private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
     private Gdms3DsInfo di;

    public Gdms3Transport(MessageCash messageCache) {
        this.messageCache = messageCache;
        inDir= messageCache.curDir+MessageCash.fileSepar+"IN"+MessageCash.fileSepar+"SGDS_TRANSPORT";
        inInvDir= messageCache.curDir+MessageCash.fileSepar+"IN"+MessageCash.fileSepar+"INV"+MessageCash.fileSepar+"SGDS_TRANSPORT";
        outDir=messageCache.curDir+MessageCash.fileSepar+"OUT"+MessageCash.fileSepar+"SGDS_TRANSPORT";
		String log = Funcs.getSystemProperty("logSGDS");
        if (log != null && log.equals("1"))
        	loging = true;
    }

    public void addTransportListener(Box box) {
        synchronized(receivedBox) {
            receivedBox.put(box.getName(),box);
            if (box.getUrlIn() != null && box.getUrlIn().length() > 0) {
            	receivedUrlBox.put(box.getUrlIn(),box);
            }
        }

    }

    public void check() {
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
                            log.error(e, e);
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

    public int getId() {
        return TransportIds.SGDS_TRANSPORT;
    }

    public String getName() {
        return "SGDS_TRANSPORT";
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

    public void received() {
        synchronized(receivedBox) {
            boolean connect_=true;
            try{
                initConnection();
                log.info("Checking INBOX");
                long count = getInboxMessages();
                log.info(count + " messages found");
                SAXBuilder builder = new SAXBuilder();
                Document doc;
                for(int i = 0; i < count; i++) {
                    String trId=null;
                    Gdms3Message msg = null;
                    try {
	                    trId = startTransaction();
	                    msg = getBinMessage(trId);
	                    log.info("Successfully received message [" + msg.msgUUID + "]; Sender: " + msg.sender.name +"; Recipient: " + msg.recipient.name);
	                    InputStream is = new ByteArrayInputStream(
	                    		msg.binaryData);
	                    doc = builder.build(is);
	                    String boxName = msg.recipient.name;
	                    Box box = receivedUrlBox.get(boxName);
	                    if (box != null) {
	                    	if (box.getTypeMsg() == Constants.MSG_XML_INT) {
	                    		messageCache.dispose(
	                    				MessageCash.IN, box.getKrnObject().id,
	                    				0, doc.getRootElement(), msg.docId);
	                    		
	                    	} else {
	                    		messageCache.dispose(
	                    				MessageCash.IN, box.getKrnObject().id,
	                    				0, msg.binaryData, msg.docId);
	                    	}
	                    } else if (doc != null) {
	                    	try {
	                    		File inInvDir_ = new File(inInvDir);
	                    		inInvDir_.mkdirs();
	                    		File fileInv = new File(inInvDir, boxName);
	                    		int k = 0;
	                    		while (fileInv.exists()) {
	                    			fileInv = new File(inInvDir, boxName + "_"
	                    					+ k);
	                    			k++;
	                    		}
	                    		OutputStream os = new BufferedOutputStream(
	                    				new FileOutputStream(fileInv));
	                    		XMLOutputter opr = new XMLOutputter();
	                    		Element xml = doc.getRootElement();
	                    		xml.detach();
	                    		opr.output(new Document(xml), os);
	                    		os.close();
	                    	} catch (IOException e) {
	                    		log.info("ОШИБКА!: При записи сообщения в файл");
	                    	}
	                    	log.error("ОШИБКА!: Не найден пункт обмена с наименованием: "
												+ boxName);
	                    }
	                    commitTransaction(trId);
                    } catch (Exception ex) {
						if (msg != null) {
							if (msg.sender == null)
								log.error("SENDER IS NULL!");
							if (msg.recipient == null)
								log.error("RECIPIENT IS NULL!");
						} else {
							log.error("MESSAGE IS NULL!");
						}
						log.error(ex, ex);
						rollbackTransaction(trId);
					}
				}
				connect = connect_;
			} catch (Exception e) {
				log.error(e, e);
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

    public void resend() {
        File dir = new File(outDir);
        dir.mkdirs();
        File[] dirs = dir.listFiles();
        boolean connect_=true;
        try{
            String trId=null;
            initConnection();
            for (File dir1 : dirs) {
                if (dir1.isDirectory()) {
                    File[] files = dir1.listFiles();
                    Box box = receivedBox.get(dir1.getName());
                    if(box==null) continue;
                    String url=box.getUrlIn();
                    String baseUid=box.getBase().uid;
                    for (File file : files) {
                        if (file.isFile() && file.canWrite()) {
                            try {
                                FileOutputStream osf = new FileOutputStream(file, true);
                                if (osf.getChannel().tryLock() != null)
                                    osf.close();
                                else
                                    continue;
                            } catch (IOException e) {
                                log.error(e, e);
                                continue;
                            }
                            boolean result = false;
                            try {
                                InputStream is = new BufferedInputStream(new FileInputStream(file));
                                ByteArrayOutputStream os=new ByteArrayOutputStream();
                                if (is != null) {
                	            	Funcs.writeStream(is, os, Constants.MAX_MESSAGE_SIZE);
                                    is.close();
                                }
                                os.close();
                                byte[] data=os.toByteArray();
                                if (data == null || data.length==0) continue;
                                Gdms3ParticipantInfo sender = new Gdms3ParticipantInfo();
                                sender.name = di.clientName;
                                sender.application = dsApplication;
                                sender.session = dsSession;

                                Gdms3ParticipantInfo recipient = new Gdms3ParticipantInfo();
                                recipient.name = url;
                                recipient.application = dsApplication;
                                recipient.session = baseUid;

                                Gdms3Message msg = new Gdms3Message();
                                msg.textData=formatter.format(new Date());
                                msg.msgPriority = 5;
                                msg.shouldNotify = Gdms3Message.MN_NONE;
                                msg.docVer = "1";
                                msg.docType = "rn-doc";
                                msg.docId = "1";
                                msg.signed = true;
                                msg.crypted = false;
                                msg.compressed = 9;
                                msg.sender = sender;
                                msg.recipient = recipient;
                                msg.binaryData = data;
                                trId = startTransaction();
                                putBinMessage(msg, trId);
                                commitTransaction(trId);
                                String msgUUID=msg.msgUUID;
                                log.info("Successfully send message " + file.getName()+"; msgUUID="+msgUUID);
                                result = true;
                            } catch (IOException e) {
                                log.error(e, e);
                            } catch (Exception ex) {
                                log.error(ex, ex);
                                if(trId!=null)
                                rollbackTransaction(trId);
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

    public void reset() {
        Properties ps= new Properties();
        try{
            File cf = new File(messageCache.t_props_file);
            if (cf.exists()) {
                ps.load(new FileInputStream(cf));
                String delay_=ps.getProperty("Sgds_delay");
                if(delay_!=null)
                	delay_p= Integer.valueOf(delay_.trim());
                String ready_=ps.getProperty("Sgds_ready");
                if(ready_ != null) {
                	ready_= ready_.trim(); 
                	ready= (ready_!=null && ready_.equals("yes"));
                }
                String dsHost_=ps.getProperty("Sgds_host");
                if(dsHost_!=null)
                    dsHost= dsHost_.trim();
                // Имя домена
                dsDomain= ps.getProperty("Sgds_domain");
                if (dsDomain != null) {
                	dsDomain= dsDomain.trim();
                	if (dsDomain.length() == 0) 
                		dsDomain= null;
                }
                // Имя пользователя
                String dsName_=ps.getProperty("Sgds_name");
                if(dsName_!=null)
                    dsName= dsName_.trim();
                // Пароль пользователя
                String dsPD_=ps.getProperty("Sgds_password");
                if(dsPD_!=null)
                    dsPD= dsPD_.trim();
                // Идентификатор приложения
                String dsApplication_=ps.getProperty("Sgds_application");
                if(dsApplication_!=null)
                    dsApplication= dsApplication_.trim();
                // Идентфикатор сессии
                String dsSession_=ps.getProperty("Sgds_session");
                if(dsSession_!=null)
                    dsSession= dsSession_.trim();
            }
        }catch (IOException e){
            log.error(e, e);
        }
        if(ready)
            restart(true);

    }

    public void restart(final boolean isConnect) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                	if (loging)
                		log.info("GDMS3Transport: trying to connect " + dsHost + " " + dsDomain + " " + dsName + " " + dsPD + " " + dsApplication + " " + dsSession);
                	di = connect(dsHost, dsDomain, dsName, dsPD, dsApplication, dsSession);
                    if(di!=null){
                        try {
                            if(!isStoping){
                            	if (loging ) {
                            		log.info("GDMS3Transport: connected.");
                            		log.info("GDMS3Transport: process messages...");
                            	}
                                received();
                                check();
                                resend();
                            	if (loging)
                            		log.info("GDMS3Transport: sleeping " + delay_p + "ms");
                                Thread.sleep(delay_p);
                            }
                        } catch (Exception e) {
                            log.error(e, e);
                        }finally{
                          release();
                        }
                    } else {
                    	try {
                        	if (loging)
                        		log.info("GDMS3Transport: failed to connect. sleeping " + delay_p + "ms");
                    		Thread.sleep(delay_p);
                    	} catch (InterruptedException e) {
                    		log.error(e, e);
                    	}
                    }
                    if(!isConnect) break;
                }
            }
        });
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
        log.info("SgdsTransport started");

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
     public void resume() {
         isStoping=false;

    }

    public void setTransportParam(byte[] data) throws JDOMException,
            IOException {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new ByteArrayInputStream(data), "UTF-8");
        Element root = doc.getRootElement();
        Element param=root.getChild("delay");
        delay_p= Integer.valueOf(param.getText());
        param=root.getChild("ready");
        ready="true".equals(param.getText());
        restart(connect);

    }

    public void stop() {
        isStoping=true;

    }

    private native Gdms3DsInfo connect(String host, String domain, String user, String pd, String appName, String sessionName);
    private native void release();

    private native void initConnection();
    private native void closeConnection();

    private native String startTransaction();
    private native long commitTransaction(String trId);
    private native long rollbackTransaction(String trId);

    private native void putBinMessage(Gdms3Message msg, String trId);

    private native long getInboxMessages();
    private native Gdms3Message getBinMessage(String trId);

    public static void main(String[] args) throws Exception {
        Gdms3Transport t = new Gdms3Transport(null);
        try {
            Gdms3DsInfo di = t.connect("localhost", null, "berikb", ",fehcfr", "Kadry", "0");

            Gdms3ParticipantInfo sender = new Gdms3ParticipantInfo();
            sender.name = di.clientName;
            sender.application = "Kadry";
            sender.session = "0";

            Gdms3Message msg = new Gdms3Message();
            msg.msgPriority = 5;
            msg.shouldNotify = Gdms3Message.MN_NONE;
            msg.docVer = "1";
            msg.docType = "rn-doc";
            msg.docId = "1";
            msg.signed = true;
            msg.crypted = false;
            msg.compressed = 9;
            msg.sender = sender;
            msg.recipient = sender;
            msg.binaryData = "‡¤а ўбвўг©, ЊЁа!".getBytes("UTF-8");

            String trId = t.startTransaction();
            t.putBinMessage(msg, trId);
            t.commitTransaction(trId);
            System.in.read();
            t.initConnection();
//			t.closeConnection();

            long count = t.getInboxMessages();
            for(int i = 0; i < count; i++) {
                trId = t.startTransaction();
                msg = t.getBinMessage(trId);
                String str = new String(msg.binaryData, "UTF-8");
                log.info("Sender: " + msg.sender.name);
                log.info("Recipient: " + msg.recipient.name);
                log.info(str);
                t.commitTransaction(trId);
            }

        } finally {
            t.release();
        }
    }
}
