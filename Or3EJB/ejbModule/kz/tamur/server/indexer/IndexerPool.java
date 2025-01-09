package kz.tamur.server.indexer;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.or3ee.common.UserSession;

public class IndexerPool {
    private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + IndexerPool.class.getName());
	private static ThreadPoolExecutor executor;
	
	public IndexerPool(int countThreads) {
        ThreadFactory thf = new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                return thread;
            }
        };
        executor = new ThreadPoolExecutor(countThreads, countThreads, 0, TimeUnit.NANOSECONDS,
                new LinkedBlockingQueue<Runnable>(),
                thf);
        
        log.info("Indexing pool started.");
    }
	
	public Future<?> execute(Runnable proc) {
		return executor.submit(proc);
	}

	public void shutdown() {
		executor.shutdown();
        log.info("Indexing pool stoped.");
	}
}
