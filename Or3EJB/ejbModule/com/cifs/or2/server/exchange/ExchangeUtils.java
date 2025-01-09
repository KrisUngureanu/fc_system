package com.cifs.or2.server.exchange;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;

import com.cifs.or2.kernel.Date;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.Session;
import com.sun.mail.smtp.SMTPMessage;
import com.sun.mail.smtp.SMTPTransport;

public class ExchangeUtils {
    private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ExchangeUtils.class.getName());
	private static KrnClass cls_mail;
	private static KrnClass cls_mail_part;
	private static KrnAttribute fromAttr;
	private static KrnAttribute toAttr;
	private static KrnAttribute bccAttr;
	private static KrnAttribute ccAttr;
	private static KrnAttribute headersAttr;
	private static KrnAttribute partAttr;
	private static KrnAttribute receivedDateAttr;
	private static KrnAttribute sentDateAttr;
	private static KrnAttribute subjectAttr;
	private static KrnAttribute contentAttr;
	private static KrnAttribute descriptionAttr;
	private static KrnAttribute dispositionAttr;
	private static KrnAttribute fileNameAttr;
	private static KrnAttribute mimeTypeAttr;
	private static KrnAttribute mimeSubTypeAttr;
	private static KrnAttribute charSetAttr;
	private static KrnAttribute idAttr;
	private static KrnAttribute partHeadersAttr;
	private static KrnAttribute partFieldsAttr;
	
	private static boolean init;
	private static void init(Session s){
		if (!init) {
			cls_mail=s.getClassByName("mail@Message");
			cls_mail_part=s.getClassByName("mail@Part");
			fromAttr=s.getAttributeByName(cls_mail, "from");
			toAttr=s.getAttributeByName(cls_mail, "to");
			bccAttr=s.getAttributeByName(cls_mail, "bcc");
			ccAttr=s.getAttributeByName(cls_mail, "cc");
			headersAttr=s.getAttributeByName(cls_mail, "headers");
			subjectAttr=s.getAttributeByName(cls_mail, "subject");
			receivedDateAttr=s.getAttributeByName(cls_mail, "receivedDate");
			sentDateAttr=s.getAttributeByName(cls_mail, "sentDate");
			partAttr=s.getAttributeByName(cls_mail, "parts");
			fileNameAttr=s.getAttributeByName(cls_mail_part, "fileName");
			contentAttr=s.getAttributeByName(cls_mail_part, "content");
			descriptionAttr=s.getAttributeByName(cls_mail_part, "description");
			dispositionAttr=s.getAttributeByName(cls_mail_part, "disposition");
			mimeTypeAttr=s.getAttributeByName(cls_mail_part, "mimeType");
			mimeSubTypeAttr=s.getAttributeByName(cls_mail_part, "subType");
			charSetAttr=s.getAttributeByName(cls_mail_part, "charSet");
			idAttr = s.getAttributeByName(cls_mail, "id");
			partHeadersAttr = s.getAttributeByName(cls_mail_part, "headers");
			partFieldsAttr = s.getAttributeByName(cls_mail_part, "fields");
			init=true;
		}
		System.setProperty("mail.mime.encodefilename", "false");
		System.setProperty("mail.mime.decodefilename", "true");
	}


    public static MimeMessage sentMailMessage(Session s,KrnObject obj,long tid){
    	init(s);
        javax.mail.Session s_ = javax.mail.Session.getInstance(new Properties());
        MimeMessage mmsg = new MimeMessage(s_);
        Multipart mp = new MimeMultipart();
        try{
	        String[] froms=s.getStrings(obj.id, fromAttr.id, 0, false, tid);
	        int i=0;
	        if(froms.length>0){
		        Address[] fromAddrs = new InternetAddress[froms.length];
		        for(String from:froms){
		            fromAddrs[i++]=new InternetAddress(from);
		        }
	        	mmsg.addFrom(fromAddrs);
	        }
	        String[] tos=s.getStrings(obj.id, toAttr.id, 0, false, tid);
	        if(tos.length>0){
	            i=0;
		        Address[] toAddrs = new InternetAddress[tos.length];
		        for(String to:tos){
		            toAddrs[i++]=new InternetAddress(to);
		        }
		        mmsg.addRecipients(Message.RecipientType.TO, toAddrs);
	        }
	        String[] ccs=s.getStrings(obj.id, ccAttr.id, 0, false, tid);
	        if(ccs.length>0){
		        Address[] ccAddrs = new InternetAddress[ccs.length];
		        i=0;
		        for(String cc:ccs){
		            ccAddrs[i++]=new InternetAddress(cc);
		        }
		        mmsg.addRecipients(Message.RecipientType.CC, ccAddrs);
	        }
	        String[] bccs=s.getStrings(obj.id, bccAttr.id, 0, false, tid);
	        if(bccs.length>0){
		        Address[] bccAddrs = new InternetAddress[bccs.length];
		        i=0;
		        for(String bcc:bccs){
		            bccAddrs[i++]=new InternetAddress(bcc);
		        }
		        mmsg.addRecipients(Message.RecipientType.BCC, bccAddrs);
	        }
	        String[] headers=s.getStrings(obj.id, headersAttr.id, 0, false, tid);
	        for(String header:headers){
	        	mmsg.addHeaderLine(header);
	        }
	        String[] subjects=s.getStrings(obj.id, subjectAttr.id, 0, false, tid);
	        if(subjects.length>0)
	        	mmsg.setSubject(MimeUtility.encodeText(subjects[0], "koi8-r", null));
	        Date[] sentDates=s.getDates(obj.id, sentDateAttr.id, tid);
	        if(sentDates.length>0){
	        	mmsg.setSentDate(Funcs.convertDate(sentDates[0]));
	        }
	        KrnObject[] parts=s.getObjects(obj.id, partAttr.id, new long[0], tid);
	        for(int j = 0; j < parts.length; j++) {
	        	KrnObject part = parts[j];
	        	MimeBodyPart mbp = new MimeBodyPart();
	            byte[]content=s.getBlob(part.id, contentAttr.id, 0, 0, tid);
	        	if (j == 0) {
	        		// Первый Part - текст сообщения
    				String[] strs = s.getStrings(part.id, mimeSubTypeAttr.id, 0, false, tid);
    				String mimeSubType = strs.length > 0 ? strs[0] : "plain";
    				strs = s.getStrings(part.id, charSetAttr.id, 0, false, tid);
    				String charSet = strs.length > 0 ? strs[0] : "koi8-r";
		            mbp.setText(new String(content, "UTF-8"), charSet, mimeSubType);
	        	} else {
    				String[] strs = s.getStrings(part.id, mimeTypeAttr.id, 0, false, tid);
    				String mimeType = strs.length > 0 ? strs[0] : "application/octet-stream";
        			mbp.setDataHandler(new DataHandler(new ByteArrayDataSource(content, mimeType)));
	                strs = s.getStrings(part.id, fileNameAttr.id, 0, false, tid);
	                if (strs.length > 0) {
						mbp.setFileName(MimeUtility.encodeText(strs[0], "koi8-r", null));
	                }
	        	}
	        	mp.addBodyPart(mbp);
	        }
	        mmsg.setContent(mp);
	        mmsg.saveChanges();
	        s.setString(obj.id, idAttr.id, 0, 0, false, mmsg.getMessageID(), tid);
        }catch(KrnException e){
        	e.printStackTrace();
        } catch (Exception e) {
			e.printStackTrace();
		}
        return mmsg;
    }
    
    public static MimeMessage sentMailMessage(String[] froms,String[] tos,String theme,String text,String mime,String charSet){
        javax.mail.Session s_ = javax.mail.Session.getInstance(new Properties());
        MimeMessage mmsg = new MimeMessage(s_);
        Multipart mp = new MimeMultipart();
        try{
            int i=0;
	        if(froms.length>0){
		        Address[] fromAddrs = new InternetAddress[froms.length];
		        for(String from:froms){
		            fromAddrs[i++]=new InternetAddress(from);
		        }
	        	mmsg.addFrom(fromAddrs);
	        }
	        if(tos.length>0){
	            i=0;
		        Address[] toAddrs = new InternetAddress[tos.length];
		        for(String to:tos){
		            toAddrs[i++]=new InternetAddress(to);
		        }
		        mmsg.addRecipients(Message.RecipientType.TO, toAddrs);
	        }
        	mmsg.setSubject(MimeUtility.encodeText(theme, charSet, null));
        	MimeBodyPart mbp = new MimeBodyPart();
			String mimeSubType = (mime!=null && mime.length() > 0) ? mime: "plain";
	        mbp.setText(text, charSet, mimeSubType);
	        	mp.addBodyPart(mbp);
	        mmsg.setContent(mp);
	        mmsg.saveChanges();
        } catch (Exception e) {
			e.printStackTrace();
		}
        return mmsg;
    }
    
    public static KrnObject receivedMailMessage(Session s, Path path, long tid) {
    	init(s);
    	
    	InputStream is = null;
        try {
        	if (Funcs.isInSecureDir(path, null, 5) && Funcs.isRegularFile(path) && !path.startsWith("/dev")) {
	        	KrnObject obj=s.createObject(cls_mail, tid);
	        	
	        	is = Files.newInputStream(path);
	        	
	            javax.mail.Session s_ = javax.mail.Session.getInstance(new Properties());
	            MimeMessage mmsg=new MimeMessage(s_,is);
	            is.close();
	            s.setString(obj.id, idAttr.id, 0, 0, false, mmsg.getMessageID(), tid);
	            Address[] froms=mmsg.getFrom();
	            int i=0;
	            if(froms!=null)
	            for(Address from:froms){
	            	s.setString(obj.id, fromAttr.id, i++, 0, false, Funcs.sanitizeEmail(from.toString()), tid);
	            }
	            Address[] tos=mmsg.getRecipients(Message.RecipientType.TO);
	            i=0;
	            if(tos!=null)
	            for(Address to:tos){
	            	s.setString(obj.id, toAttr.id, i++, 0, false,  Funcs.sanitizeEmail(to.toString()), tid);
	            }
	            Address[] ccs=mmsg.getRecipients(Message.RecipientType.CC);
	            i=0;
	            if(ccs!=null)
	            for(Address cc:ccs){
	            	s.setString(obj.id, ccAttr.id, i++, 0, false,  Funcs.sanitizeEmail(cc.toString()), tid);
	            }
	            Address[] bccs=mmsg.getRecipients(Message.RecipientType.BCC);
	            i=0;
	            if(bccs!=null)
	            for(Address bcc:bccs){
	            	s.setString(obj.id, bccAttr.id, i++, 0, false,  Funcs.sanitizeEmail(bcc.toString()), tid);
	            }
	        	s.setString(obj.id, subjectAttr.id, 0, 0, false, mmsg.getSubject(), tid);
	        	Enumeration headers=mmsg.getAllHeaderLines();
	        	i=0;
	            if(headers!=null)
	        	while(headers.hasMoreElements()){
	        		String header=(String)headers.nextElement();
	        		s.setString(obj.id, headersAttr.id, i++, 0, false, header, tid);
	        	}
	        	java.util.Date sentDate=mmsg.getSentDate();
	        	if(sentDate!=null)
	        		s.setDate(obj.id, sentDateAttr.id, 0, Funcs.convertDate(sentDate), tid);
	        	java.util.Date receivedDate=mmsg.getReceivedDate();
	        	if(receivedDate==null)
	        		receivedDate=new java.util.Date();
	        	s.setDate(obj.id, receivedDateAttr.id, 0, Funcs.convertDate(receivedDate), tid);
	        	
	        	Object content = mmsg.getContent();
	        	
	        	if (content instanceof Multipart) {
	        		Multipart mp = (Multipart)content;
	        		int count = mp.getCount();
	        		for (int j = 0; j < count; j++) {
	        			MimeBodyPart bp = (MimeBodyPart)mp.getBodyPart(j);
	        			processPart(bp, j, obj, s, tid);
	        		}
	        	} else {
	        		processPart(mmsg, 0, obj, s, tid);
	        	}
	        	
	            s.commitTransaction();
	            return obj;
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        	try {
				s.rollbackTransaction();
			} catch (KrnException e1) {
				e1.printStackTrace();
			}
        } finally {
        	Utils.closeQuietly(is);
        }
		return null;
    }

    private static void processPart(MimePart mpart, int index, KrnObject msgObj, Session s, long tid) throws IOException, MessagingException, KrnException {
    	Object content = mpart.getContent();
    	if (content instanceof String) {
    		String mp = (String)content;
    		KrnObject obj_=s.createObject(cls_mail_part, tid);
            s.setObject(msgObj.id, partAttr.id, index, obj_.id, tid, false);
            s.setString(obj_.id, mimeTypeAttr.id, 0, 0, false, "text/plain", tid);
            s.setString(obj_.id, mimeSubTypeAttr.id, 0, 0, false, "plain", tid);
            s.setBlob(obj_.id, contentAttr.id, 0, mp.getBytes("UTF-8"), 0, tid);
    	} else {
            KrnObject obj_=s.createObject(cls_mail_part, tid);
            s.setObject(msgObj.id, partAttr.id, index, obj_.id, tid, false);
            String fileName=mpart.getFileName();
            if(fileName!=null)
            s.setString(obj_.id, fileNameAttr.id, 0, 0, false, fileName, tid);
            String description=mpart.getDescription();
            if(description!=null)
            s.setString(obj_.id, descriptionAttr.id, 0, 0, false, description, tid);
            String disposition=mpart.getDisposition();
            if(disposition!=null)
            s.setString(obj_.id, dispositionAttr.id, 0, 0, false, disposition, tid);
            String mimeType = mpart.getContentType();
            s.setString(obj_.id, mimeTypeAttr.id, 0, 0, false, mimeType, tid);

        	Enumeration headers=mpart.getAllHeaderLines();
        	int i = 0;
            if(headers!=null)
        	while(headers.hasMoreElements()){
        		String header = (String)headers.nextElement();
        		s.setString(obj_.id, partHeadersAttr.id, i++, 0, false, header, tid);
        	}
            
            if (mimeType.indexOf("message/disposition-notification") != -1) {
            	BufferedReader r = new BufferedReader(new InputStreamReader(mpart.getInputStream()));
            	for (String line = r.readLine(); line != null; line = r.readLine()) {
            		s.setString(obj_.id, partFieldsAttr.id, 0, 0, false, line, tid);
            	}
            	r.close();
            } else {
	            InputStream is_= mpart.getInputStream();
	            ByteArrayOutputStream os_ = new ByteArrayOutputStream();
	            if (is_ != null) {
	            	Funcs.writeStream(is_, os_, Constants.MAX_BLOB_SIZE);
	                is_.close();
	                os_.close();
	                s.setBlob(obj_.id, contentAttr.id, 0, os_.toByteArray(), 0, tid);
	            }
            }
    	}
    }

    public static boolean sendMailMessage(KrnObject obj, String host, String port,
			String user, String pd,Session session) throws KrnException {
    	boolean res=false;
        try {
    	    Context ctx = session.getContext();
	        Properties props = new Properties();
	        props.put("mail.smtp.host", host);
	        props.put("mail.smtp.auth", "true");
	        props.put("mail.smtp.user", user);
	        props.put("mail.smtp.password", pd);
	        props.put("mail.smtp.port", port);
	        Authenticator auth = new SMTPAuthenticator(user, pd);
	        javax.mail.Session sm = javax.mail.Session.getInstance(props, auth);
	        SMTPTransport tr = (SMTPTransport) sm.getTransport("smtp");
	 			tr.connect();
	        MimeMessage msg=ExchangeUtils.sentMailMessage(session,obj,ctx.trId);
	        SMTPMessage m = new SMTPMessage(msg);
	        tr.sendMessage(m,m.getRecipients(Message.RecipientType.TO));
	        res=true;
		} catch (MessagingException e) {
			e.printStackTrace();
		} 
   	return res;
    }
    public static boolean sendMailMessage(String host, String port,	String user, String pd,
    		String[] froms, String[] tos,String theme,String text,String mime, String charSet, boolean ssl) throws KrnException {
    	boolean res=false;
        try {
	        Properties props = new Properties();
	        
	        if (ssl) {
	    		props.put("mail.smtp.ssl.enable", true);
	    		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
	        }
	        
	        Authenticator auth = new SMTPAuthenticator(user, pd);
	        javax.mail.Session sm = javax.mail.Session.getInstance(props, auth);
	        SMTPTransport tr = (SMTPTransport) sm.getTransport("smtp");
	 		
	        tr.connect(host, Integer.parseInt(port), user, pd);
	        
	        MimeMessage msg=ExchangeUtils.sentMailMessage(froms, tos, theme, text, mime, charSet);
	        SMTPMessage m = new SMTPMessage(msg);
	        tr.sendMessage(m,m.getRecipients(Message.RecipientType.TO));
	        res=true;
		} catch (MessagingException e) {
			log.error(e, e);
			throw new KrnException("SMTP error", 1, e);
		} 
        return res;
    }
    
	private static class SMTPAuthenticator extends javax.mail.Authenticator {
		
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
	public static boolean isValidEmailAddress(String email) {
		   boolean result = true;
		   try {
		      InternetAddress emailAddr = new InternetAddress(email);
		      emailAddr.validate();
		   } catch (AddressException ex) {
		      result = false;
		   }
		   return result;
		}
}
