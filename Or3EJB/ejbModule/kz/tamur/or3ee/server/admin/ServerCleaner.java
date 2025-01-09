package kz.tamur.or3ee.server.admin;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.util.Funcs;

import com.cifs.or2.server.ServerUserSession;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.db.ConnectionManagerLocal;

@Stateless(name="ServerCleaner", mappedName = "ServerCleaner")
@Local(ServerCleanerLocal.class)
public class ServerCleaner implements ServerCleanerLocal {
	
	// минута
    private static long SESSION_TIMEOUT = Long.parseLong(Funcs.getSystemProperty("serverCleanerTimeout", "60000"));
    // сутки
    private static long SESSION_TIMEOUT_MOBILE = Long.parseLong(Funcs.getSystemProperty("serverCleanerTimeoutMobile", "86400000"));
    
    @Resource
	TimerService timerService;

    @EJB(beanName="ConnectionManager", beanInterface = ConnectionManagerLocal.class)
    private ConnectionManagerLocal connectionManager;

    public ServerCleaner() {
    }
    
    public void start() {
    	Collection<Timer> timers = timerService.getTimers();
    	for (Timer timer : timers)
    		timer.cancel();
    	timerService.createTimer(SESSION_TIMEOUT, SESSION_TIMEOUT, null);
    }
    
    @Timeout
    public void cleanup(Timer timer) {
    	Set<String> dbNames = connectionManager.getDatabaseNames();
    	for (String dbName : dbNames) {
    		Session s = null;
    		try {
    			s = SrvUtils.getSession(dbName, "sys", null);
    			// Удаление повисших сессий
    			List<ServerUserSession> aus = s.getActiveUsers();
    			for (ServerUserSession au : aus) {
    				if (au.callbacks() && au.isMySession() && 
    						(
    								(System.currentTimeMillis() - au.getLastPing() > SESSION_TIMEOUT && !"mobile".equals(au.getTypeClient()))
    								||
    								(System.currentTimeMillis() - au.getLastPing() > SESSION_TIMEOUT_MOBILE && "mobile".equals(au.getTypeClient()))
    						)
    					) {
    					s.getLog(ServerCleaner.class).info("Killing a hang session of user: " + au.getUserName());
    					s.killUserSessions(au.getId(), false);
    				}
    			}

                // Удаление повисших блокировок
				Collection<kz.tamur.ods.Lock> locks = s.getAllLocks();
				for (kz.tamur.ods.Lock lock : locks) {
					if (lock.sessionId != null) {
						UserSession us = Session.findUserSession(UUID.fromString(lock.sessionId));
						if (us == null) {
							s.unlockObject(lock.objId, lock.lockerId);
							s.commitTransaction();
						}
					}
				}
				Collection<kz.tamur.ods.LockMethod> lockMethods = s.getMethodAllLocks();
				for (kz.tamur.ods.LockMethod lock : lockMethods) {
					if (lock.sessionId != null) {
						UserSession us = Session.findUserSession(UUID.fromString(lock.sessionId));
						if (us == null) {
							s.unlockMethod(lock.muid);
							s.commitTransaction();
						}
					}
				}
	            s.unlockUnexistingFlowObjects();
	            s.commitTransaction();
    		
    		} catch (Exception e) {
    			e.printStackTrace();
    		} finally {
    			if (s != null)
    				s.release();
    		}
    	}
    }

}
