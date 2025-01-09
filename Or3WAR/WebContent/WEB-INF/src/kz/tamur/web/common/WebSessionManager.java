package kz.tamur.web.common;

import com.cifs.or2.kernel.KrnException;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import kz.tamur.or3ee.common.UserSession;
import kz.tamur.web.controller.WebController;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WebSessionManager {
    private static AtomicInteger sessionId_ = new AtomicInteger(1);
    private static Map<Integer, WebSession> sessions = Collections.synchronizedMap(new HashMap<Integer, WebSession>());

    public static Map<String, Log> webLogs = new HashMap<>();

    public static Integer createSession(String name, String path, String newPass, String confPass, String ip, String host,
    		int loginType, int configNumber, String dsName, boolean force, boolean sLogin, long downtime, boolean isUseECP, String signedData)
            throws KrnException {

    	Integer sid = sessionId_.getAndIncrement();
        WebSession s = new WebSession(sid, name, path, newPass, confPass, ip, host, "OR3", loginType, configNumber, dsName, force, sLogin, downtime, isUseECP, signedData);
        sessions.put(sid, s);
        
        String logName = name.replaceAll("\\s|\\.", "_");
        Log log = WebSessionManager.getLog(dsName, logName);
        
        log.info(new StringBuilder("|USER: ").append(name).append("| WebSession with id = ").append(sid).append(" created successfully! Count:").append(sessions.size()));
    	log.info(new StringBuilder("Active sessions: ").append(sessions.size()));
        return sid;
    }

    public static synchronized WebSession getSession(Integer sid) {
        return sessions.get(sid);
    }

    public static Map<Integer, WebSession> getSessions() {
    	Map<Integer, WebSession> res = new HashMap<Integer, WebSession>();
        synchronized (sessions) {
        	res.putAll(sessions);
        }
        return res;
    }

    public static boolean releaseSession(Integer sid) {
    	return releaseSession(sid, true);
    }
    
    public static boolean releaseSession(Integer sid, boolean planned) {
        WebSession s = sessions.remove(sid);
        if (s != null) {
        	if (planned)
        		s.getWebUser().setWaitingToUnbound(true);
            Log log = WebSessionManager.getLog(WebController.BASE_NAME[s.getConfigNumber()], s.getUserName().replaceAll("\\s|\\.", "_"));
            s.release();
        	log.info("Active sessions: " + sessions.size());
            s = null;
            return true;
        }
        return false;
    }
    
    public static void takeMemorySnapshot() {
    	if (sessions.size() > 0) {
    		WebSession s = sessions.values().iterator().next();
    		if (s != null) {
    			CommonHelper.takeMemorySnapshot(s);
    		}
    	}
    }
    
    public static Log getLog(String dsName, String userLogName) {
    	if (userLogName == null)
    		userLogName = "";
    	Log log = webLogs.get(userLogName);
    	if (log == null) {
    		if (userLogName.length() == 0) {
    			log = LogFactory.getLog(UserSession.SERVER_ID + ".WebLog");
    		} else {
    			StringBuilder sb = new StringBuilder(dsName).append(".").append(userLogName).append(".").append(UserSession.SERVER_ID).append(".WebLog");
    			String key = sb.toString();
    			log = LogFactory.getLog(key);
    		}
            webLogs.put(userLogName, log);
        }
    	return log;
    }
}