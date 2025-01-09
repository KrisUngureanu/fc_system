package kz.tamur.server.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.NotificationNote;
import com.cifs.or2.server.ServerUserSession;
import com.cifs.or2.server.Session;

import kz.tamur.ods.Driver2;
import kz.tamur.or3ee.common.AttrChangeListener;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.SrvUtils;

public class NotificationListener implements AttrChangeListener {
	private static final Log log = LogFactory.getLog("NotificationListener" + (UserSession.SERVER_ID != null ? ("." + UserSession.SERVER_ID) : ""));
	private String dsName;
	
	private static Map<Long, List<Notification>> notificationsByTr = new HashMap<>();
	private static NotificationListener instance = null;
	
	public static NotificationListener instance(String dsName) {
		if (instance == null) {
			instance = new NotificationListener(dsName);

	        Session s = null;
	        try {
	        	s = SrvUtils.getSession(dsName, "sys", null);
				KrnClass noteCls = s.getClassByName("Notification");
				if (noteCls != null) {
					Driver2.addAttrChangeListener(noteCls.id, instance);
					log.info("Notification Listener initialized!");
				}
	        } catch (Exception e) {
				log.error(e, e);
            } finally {
                if (s != null) {
                    s.release();
                }
            }
		}
		return instance;
	}
	
	private NotificationListener(String dsName) {
		this.dsName = dsName;
	}
	
	public void addNotificationNote(NotificationNote note, long userId, long trId) {
        List<Notification> trNotes = null;
        synchronized (notificationsByTr) {
        	trNotes = notificationsByTr.get(trId);
	        if (trNotes == null) {
	        	trNotes = new ArrayList<>();
	        	notificationsByTr.put(trId, trNotes);
	        }
		}
        
        synchronized (trNotes) {
        	trNotes.add(new Notification(note, userId));
		}
	}

	@Override
	public void commitLongTransaction(UUID uuid, long trId) {
        List<Notification> trNotes = null;
        synchronized (notificationsByTr) {
        	trNotes = notificationsByTr.remove(trId);
		}
        
        if (trNotes != null) {
        	synchronized (trNotes) {
        		for (Notification n : trNotes) {
	    	    	ServerUserSession us = Session.findUserSession(n.getUserId());
	    			if (us != null) {
    					Session.sendNoteClustered(us, n.getNote());
	    			}
        		}
			}
        }
	}

	@Override
	public void rollbackLongTransaction(UUID uuid, long trId) {
		synchronized (notificationsByTr) {
			notificationsByTr.remove(trId);
		}
	}
	
	@Override
	public void attrChanged(KrnObject obj, long attrId, long langId, long trId, UUID uuid) {
	}

	@Override
	public void commit(UUID uuid) {
	}

	@Override
	public void rollback(UUID uuid) {
	}

	private class Notification {
		private NotificationNote note;
		private long userId;
		
		public Notification(NotificationNote note, long userId) {
			this.note = note;
			this.userId = userId;
		}

		public NotificationNote getNote() {
			return note;
		}

		public long getUserId() {
			return userId;
		}
	}
}
