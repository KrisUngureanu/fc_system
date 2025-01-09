package kz.tamur.or3ee.server.admin;

import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import kz.tamur.ods.debug.ConnectionProxy;
import kz.tamur.ods.debug.ConnectionProxy.Item;
import kz.tamur.ods.debug.ResourceRegistry;
import kz.tamur.or3ee.common.UserSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Session Bean implementation class TransactionWatchDog
 */
@Stateless(name="TransactionWatchDog", mappedName = "TransactionWatchDog")
@Local(TransactionWatchDogLocal.class)
public class TransactionWatchDog implements TransactionWatchDogLocal {
	
	private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + TransactionWatchDog.class.getName());
	
	private static long transactionTimeout;

    @Resource
	TimerService timerService;

    @Override
	public void start(long checkInterval, long transactionTimeout) {
    	for (Timer timer : timerService.getTimers())
    		timer.cancel();
    	TransactionWatchDog.transactionTimeout = transactionTimeout;
    	timerService.createTimer(checkInterval, checkInterval, null);
	}
    
    @Timeout
    public void check(Timer timer) {
    	log.debug("BEGIN TRANSACTION DUMP ==========");
    	long time = System.currentTimeMillis();
    	List<Item> items = ConnectionProxy.getItems();
    	for (Item item : items) {
    		if (time - item.startTime > transactionTimeout) {
    	    	log.warn("Long transaction! Duration: " + (time - item.startTime) + " ms.");
    	    	log.warn("Thread: " + item.threadName+" SID: " + item.sid);
    	    	log.warn("=====BEGIN STACK TRACE=====");
    	    	if (item.executionStack != null)
	    	    	for (String str : item.executionStack)
	    	    		log.warn(str);
    	    	log.warn("=====END STACK TRACE=====");
    		}
    	}
    	log.debug("END TRANSACTION DUMP ============");
    	
    	ResourceRegistry.instance().dump(transactionTimeout);
    }

    public TransactionWatchDog() {
    }
}
