package kz.tamur.web.common;

import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;
import kz.tamur.web.component.WebFrame;
import kz.tamur.web.controller.WebController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.AttrChangeNote;
import com.cifs.or2.kernel.DeleteNotificationsNote;
import com.cifs.or2.kernel.UpdateNotificationsNote;
import com.cifs.or2.kernel.MessageNote;
import com.cifs.or2.kernel.Note;
import com.cifs.or2.kernel.NotificationNote;
import com.cifs.or2.kernel.OrderReloadNote;
import com.cifs.or2.kernel.SystemNote;
import com.cifs.or2.kernel.TaskReloadNote;
import com.cifs.or2.kernel.UserSessionValue;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 16.12.2004
 * Time: 11:58:02
 * To change this template use File | Settings | File Templates.
 */
public class WebClientCallback extends Thread {

	private Log log;
	private Kernel krn;
    private WebSession webSession;
    private final static int FEEDBACK_SLEEP_TIMEOUT = 1000;
    private static final long PERIOD_START_NOTIFY = 60000;
    private long periodToNotify = PERIOD_START_NOTIFY;

    public WebClientCallback(Kernel krn) {
    	this.krn = krn;
        this.log = LogFactory.getLog(krn.getUserSession().dsName + "."
				+ krn.getUserSession().logName + "." + (UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + getClass().getName());

    }

    @Override
	public void interrupt() {
    	krn = null;
		super.interrupt();
	}

    public void setWebSession(WebSession webSession) {
        this.webSession = webSession;
    }

	public void run() {
		while(getNotes());
        log.info("WebClientCallback stoped!");
	}
	
	public boolean getNotes() {
		Note[] notes = null;
		try {
    		if (krn != null && krn.isAlive()) {
		    	try {
		    		Thread.sleep(FEEDBACK_SLEEP_TIMEOUT);
		    	} catch (InterruptedException e) {
		    	}
		    	if (krn != null) {
		    		notes = krn.getNotes();
		    		if (notes == null)
		    			throw new Exception();
		    	} else
		    		return false;
	    	} else
	    		return false;
    		
		} catch (Throwable e) {
			log.error(e, e);
            log.info("|USER: " + webSession.getUserName() + "| Session is being unbounded after exception!");
            WebSessionManager.releaseSession(webSession.getId());
            log.info("|USER: " + webSession.getUserName() + "| Session has been unbounded after exception!");
            webSession = null;
            krn = null;
            return false;
		}
		for (Note note : notes) {
			if (note instanceof MessageNote) {
		        if (webSession != null) {
		        	MessageNote mnote = MessageNote.class.cast(note);
		        	UserSessionValue us = note.from;
		        	String msg = null;
		        	if (us != null)
		        		msg = "Сообщение от пользователя '" + us.name + "' с компьютера " + us.pcName + " (" + us.ip + "): \n\n" + mnote.message;
		        	else
		        		msg = mnote.message;
                    if (mnote.isDropUser) {
                    	if (msg == null) {
                    		webSession.sendMultipleCommand("logout", null);
                    	} else {
                        	webSession.sendMultipleCommand("alertAndDrop", msg);
                    	}
                    } else {
                        webSession.setMessage(msg);
                    }
		        }
			} else if (note instanceof NotificationNote) {
				NotificationNote nnote = NotificationNote.class.cast(note);
				JsonObject notification = new JsonObject();
				notification.add("objId", nnote.objId);
				notification.add("message", nnote.message);
				notification.add("uid", nnote.uid);
				notification.add("cuid", nnote.cuid);
				notification.add("datetime", nnote.time);
				notification.add("proc", nnote.proc);
				notification.add("iter", nnote.iter);
        		webSession.sendCommand("notification", notification);
			} else if (note instanceof TaskReloadNote) {
				final TaskReloadNote tnote = TaskReloadNote.class.cast(note);
                if (webSession != null) {
                    webSession.taskReload(tnote.flowId, tnote.flowParam, true);
                }
			} else if (note instanceof SystemNote) {
                if (webSession != null) {
                    webSession.doOnNotification((SystemNote) note);
                }
			} else if (note instanceof OrderReloadNote) {
				final OrderReloadNote onote = OrderReloadNote.class.cast(note);
                if (webSession != null) {
                	JsonArray arr = new JsonArray();
                	for (String uid : onote.orderIds)
                		arr.add(uid);
                	
                	JsonObject obj = new JsonObject().add(onote.type, arr);

                	webSession.sendMultipleCommand(onote.operation, obj);
                }
			} else if (note instanceof AttrChangeNote) {
				final AttrChangeNote a = AttrChangeNote.class.cast(note);
                if (webSession != null) {
                	webSession.sendMultipleCommand("alert", a.attrId);
                }
            } else if (note instanceof UpdateNotificationsNote) {
				JsonObject updateNotifications = new JsonObject();
        		webSession.sendCommand("updateNotifications", updateNotifications);
            } else if (note instanceof DeleteNotificationsNote) {
				JsonObject deleteNotifications = new JsonObject();
				deleteNotifications.add("objids", ((DeleteNotificationsNote) note).getObjIds());
        		webSession.sendCommand("deleteNotifications", deleteNotifications);
            }
		}
		
        if (webSession != null && !"mu_pub".equals(webSession.getUserName())) {
        	long idleTime = System.currentTimeMillis() - webSession.getLastPing();
            if (idleTime > WebController.WEB_SESSION_TIMEOUT) {
                WebFrame frm = webSession.getFrameManager().getCurrentFrame();
                if (frm != null) {
                	frm.commit(false).toString();
            	}
                log.info("|USER: " + webSession.getUserName() + "| Session is being unbounded by timeout!");
                WebSessionManager.releaseSession(webSession.getId());
                log.info("|USER: " + webSession.getUserName() + "| Session has been unbounded by timeout!");
                webSession = null;
                krn = null;
                return false;
            } else if (WebController.WEB_SESSION_TIMEOUT - idleTime < periodToNotify) {
            	periodToNotify -= 10000;
            	long sec = 1 + (WebController.WEB_SESSION_TIMEOUT - idleTime) / 1000;
            	String msg = "Через " + sec + " сек Вы будете отключены от сервера,\nтак как нет активных действий!";
                webSession.setPopupMessage(msg);
            } else if (WebController.WEB_SESSION_TIMEOUT - idleTime > PERIOD_START_NOTIFY) {
            	periodToNotify = PERIOD_START_NOTIFY;
            }
        }
        return (webSession != null);
	}
}