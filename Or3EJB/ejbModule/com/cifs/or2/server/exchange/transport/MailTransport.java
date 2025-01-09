package com.cifs.or2.server.exchange.transport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import org.jdom.input.SAXBuilder;
import com.cifs.or2.server.exchange.Box;
import com.sun.mail.smtp.SMTPMessage;
import com.sun.mail.smtp.SMTPTransport;
import com.sun.mail.pop3.POP3Store;
import com.sun.mail.pop3.POP3Folder;

import javax.mail.*;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import kz.tamur.admin.ExchangeEvents;
import kz.tamur.or3ee.common.TransportIds;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;

/**
 * Created by IntelliJ IDEA.
 * User: Valeri
 * Date: 04.07.2007
 * Time: 18:42:56
 * To change this template use File | Settings | File Templates.
 */
public class MailTransport implements Transport, TransportListener {

    private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + MailTransport.class.getName());

    private final HashMap<String, Box> receivedBox = new HashMap<String,Box>();
    private String inDir;
    private String outDir;
    private int maxCutMsg=10;
    private Timer timer1 = new Timer();
    private Timer timer2 = new Timer();
    private TimerTask timerTask1=null;
    private TimerTask timerTask2=null;
    private int delay= 0;
    private int delay_p=100000;
    private MessageCash messageCache;
    private boolean ready=false,connect=false,isStopping=false;

    public MailTransport(MessageCash messageCache) {
        this.messageCache = messageCache;
        inDir=messageCache.curDir+MessageCash.fileSepar+"IN"+MessageCash.fileSepar+"MAIL";
        outDir=messageCache.curDir+MessageCash.fileSepar+"OUT"+MessageCash.fileSepar+"MAIL";
        reset();
    }
    public void reset(){
        Properties ps= new Properties();
        try{
            File cf = new File(messageCache.t_props_file);
            if (cf.exists()) {
                ps.load(new FileInputStream(cf));
                String delay_=ps.getProperty("Mail_delay");
                if(delay_!=null)
                delay_p= Integer.valueOf(delay_);
                delay=delay_p;
                String ready_=ps.getProperty("Mail_ready");
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
            log.info("MailTransport started");
        }

    }
    public int getId() {
        return TransportIds.MAIL;
    }

    public String getName() {
        return "MAIL";
    }

    public void resend() {
        File ddir = new File(outDir);
        ddir.mkdirs();
        File[] dirs = ddir.listFiles();
        boolean connect_=true;
        javax.mail.Session  s=null;
        SMTPTransport tr = null;
        String url ="";
        try {
	        for (File dir1 : dirs) {
	            if (dir1.isDirectory()) {
	                Box box = receivedBox.get(dir1.getName());
	                if (box == null) continue;
	                url = box.getUrlOut();
	                if (url.equals("")) continue;
	                String notify=MessageCash.getParam(url,"notify");
	                File[] files = dir1.listFiles();
	                if(files.length>0){
	                	if(s==null) {
		                    Properties props = new Properties();

		                    String host=MessageCash.getParam(url,"host");
	    	                String port=MessageCash.getParam(url,"port");
	    	                String user=MessageCash.getParam(url,"user");
	    	                String pd=MessageCash.getParam(url,"passwd");
	    	                String authent=MessageCash.getParam(url,"auth");
		                    props.put("mail.smtp.host", host);
		                    props.put("mail.smtp.port", port);
		                    //props.put("mail.debug", "true");
	    	                if("true".equals(authent)){
			                    props.put("mail.smtp.auth", "true");
			                    props.put("mail.smtp.user", user);
			                    props.put("mail.smtp.password", pd);

			                    System.out.println(">>>>>>>connect with auth");
			                    Authenticator auth = new SMTPAuthenticator(user, pd);
			                    s = javax.mail.Session.getInstance(props, auth);

			                    tr = (SMTPTransport) s.getTransport("smtp");
			                    tr.connect();
	    	                }else{
			                    props.put("mail.smtp.auth", "false");
	    	                    s = javax.mail.Session.getInstance(props);
			                    System.out.println(">>>>>>>connect without auth");
			                    
	    	                	if (tr == null) {
	    							tr = (SMTPTransport) s.getTransport("smtp");
	    		                    tr.connect();
	    	                	}
	    	                	
	    	                }
	                	}
	                }
	                for (File file : files){
	                    if (file.isFile() && file.canWrite()) {
	                        boolean result = false;
	                        try {
	                            FileOutputStream osf = new FileOutputStream(file, true);
	                            if (osf.getChannel().tryLock() != null) {
	                                osf.close();
	                                InputStream is = new BufferedInputStream(new FileInputStream(file));
	                                SMTPMessage m = new SMTPMessage(s,is);
	                                is.close();
	                                if("true".equals(notify)){
		                                m.setNotifyOptions(SMTPMessage.NOTIFY_SUCCESS);
		                                tr.addTransportListener(this);
	                                }
	                                tr.sendMessage(m,m.getRecipients(Message.RecipientType.TO));
	                                result = true;
	                            } else
	                                osf.close();
	                        } catch (IOException e) {
	                            log.info("Ошибка при записи в буфер сообщения отправляемого по электронной почте" + e);
	                            messageCache.writeLogRecord(ExchangeEvents.EML_0030,
	                            		file.getName(),
	                                    "mailSend",
	                                    "", url);
	                            connect_ = false;
	                        } catch (MessagingException e) {
	                            log.info("Ошибка при отправке сообщения по электронной почте" + e);
	                            messageCache.writeLogRecord(ExchangeEvents.EML_0031,
	                            		file.getName(),
	                                    "mailSend",
	                                    "", url);
	                        } finally {
	                            if(!"true".equals(notify))
	                            	messageCache.messageSent(file, box, result);
	                        }
	                    }
	                }
	            }
	        }
		} catch (MessagingException e1) {
            log.info("Ошибка при установлении связи" + e1);
            messageCache.writeLogRecord(ExchangeEvents.EML_001,
            		"",
                    "mailSend",
                    "", url);
        } finally {
            try {
            	if(tr != null) {
            		tr.close();
            		tr = null;
            	}
            } catch (MessagingException e) {
                e.printStackTrace();
            }
		}
        connect=connect_;
    }

    public void received() {
        synchronized(receivedBox) {
            Iterator boxIt = receivedBox.values().iterator();
            boolean connect_=true;
            if(!boxIt.hasNext()) return;
            javax.mail.Session  s = javax.mail.Session.getInstance(new Properties());
            POP3Folder inbox=null;
            POP3Store store= null;
            try {
                store = (POP3Store)s.getStore("pop3");
	            while(boxIt.hasNext()) {
	                Box box = (Box)boxIt.next();
	                String url = box.getUrlIn();
	                if(url.equals("")) continue;
	                try{
	                String host=MessageCash.getParam(url,"host");
	                String port=MessageCash.getParam(url,"port");
	                String user=MessageCash.getParam(url,"user");
	                String pd=MessageCash.getParam(url,"passwd");
	                store.connect(host, Integer.valueOf(port), user, pd);
	                inbox = (POP3Folder) store.getFolder("INBOX");
	                inbox.open(Folder.READ_WRITE);
	                Message[] msgs = inbox.getMessages();
	                for (Message msg : msgs) {
	                    MimeMessage mmsg = (MimeMessage) msg;
	                    try {
	                    	messageCache.dispose(MessageCash.IN, box.getKrnObject().id, 0, mmsg, "");
	                        mmsg.setFlag(Flags.Flag.DELETED, true);
	                    } catch (TransportException e) {
	                        log.info("Ошибка при записи в буфер сообщения полученного по электронной почте" + e);
	                        messageCache.writeLogRecord(ExchangeEvents.EML_1021,
	                        		mmsg.getContentID(),
	                                "mailReceived",
	                                "", url);
	                    }
	                }
	                } catch (MessagingException e) {
	                  log.info("Ошибка при получении сообщения по электронной почте" + e);
	                  messageCache.writeLogRecord(ExchangeEvents.EML_1020,"","mailReceived","", url);
	                  }finally{
	                	  try {
	                		  if(inbox!=null)
	                			  inbox.close(true);
	                		  if(store!=null)
	                			  store.close();
	                		  } catch (MessagingException e) {
	                			  e.printStackTrace();
	                			  }
	                		  }
	                  }
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }
            connect=connect_;
        }
    }
    public void check(){
        File dir = new File(inDir);
        dir.mkdirs();
        
        List<Path> boxdirs = Funcs.fileList(Paths.get(inDir), null); 
        for (Path boxdir : boxdirs) {
        	if (Files.isDirectory(boxdir)) {
                List<Path> files = Funcs.fileList(boxdir, null);
                int count = files.size();
                Box box = receivedBox.get(boxdir.getFileName().toString());
                if (box == null) continue;
                for (int i = 0; i < count && i<maxCutMsg; i++) {
                	Path file = files.get(i);
                    if (!Files.isDirectory(file) && Files.isWritable(file)) {
                        try {
                            FileOutputStream osf = new FileOutputStream(file.toFile(), true);
                            if (osf.getChannel().tryLock() != null)
                                osf.close();
                            else
                                continue;
                        } catch (IOException e) {
                            e.printStackTrace();
                            continue;
                        }
                        messageCache.mailMessageReceived(file, box);
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
	public void messageDelivered(TransportEvent arg0) {
//    	MessageCash.instance(session).messageSent(file, box, true);
	}
	public void messageNotDelivered(TransportEvent arg0) {
//    	MessageCash.instance(session).messageSent(file, box, false);
	}
	public void messagePartiallyDelivered(TransportEvent arg0) {
//    	MessageCash.instance(session).messageSent(file, box, false);
	}

	private class SMTPAuthenticator extends javax.mail.Authenticator {
		
		private String username;
		private String pd;
		
		public SMTPAuthenticator(String name, String pd) {
			this.username = name;
			this.pd = pd;
		}
		
	    public PasswordAuthentication getPasswordAuthentication()
	    {
	        return new PasswordAuthentication(username, pd);
	    }
	}
}
