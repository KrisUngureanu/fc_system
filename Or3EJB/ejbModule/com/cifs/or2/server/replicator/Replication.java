package com.cifs.or2.server.replicator;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.server.Session;

import kz.tamur.or3ee.common.UserSession;

import java.io.*;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by IntelliJ IDEA.
 * User: daulet
 * Date: 28.03.2006
 * Time: 18:12:37
 * To change this template use File | Settings | File Templates.
 */
public class Replication {

    private Session ses;
    private Log log;

    private long replicationID = 0;
    private static StringBuffer trace_log = new StringBuffer();
    private static int trace_count = 0;
    private static File trace_log_file;
    private static boolean replication_running = false;

    private static SimpleDateFormat formatter_time =
            new SimpleDateFormat("hh:mm:ss");
    
    public Replication(Session session) {
    	ses = session;
        this.log = LogFactory.getLog(ses.getDsName() + "." + ses.getUserSession().getLogUserName() + "." + (UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + getClass().getName());
    }

    public void run(Session session)
            throws com.cifs.or2.kernel.KrnException {
        // реализация RunReplication будет после реализации пакетирования проектов
        // пока RunReplication() запускает только импорт данных
        trace("RunReplication()", log);
        ses = session;
        replication_running = true;
        replicationID = getNewReplicationID();
        initTraceFile();
        try {
            try {
                setChanges(ses);
            } catch (Exception e) {
                log.error(e, e);
                throw new KrnException(0, e.getMessage());
            }
        } finally {
			replication_running = false;
            freeTraceFile();
        }
    }

	public String setChanges(Session session) throws com.cifs.or2.kernel.KrnException {
		if(replication_running) return "REPLICATION - Процесс репликации уже выполнятется!!!";
		System.out.println("REPLICATION - setChanges()");
		String res = "";
		ses = session;
		replication_running = true;
		replicationID = getNewReplicationID();
		initTraceFile();
		try {
			try {
				ReplImport i = new ReplImport(session);
				res = i.run();
			} catch (Throwable e) {
				log.error(e, e);
				res += "\nERROR:" + e.getMessage();
			}
		} finally {
			replication_running = false;
			freeTraceFile();
		}
		registerReplication();
		return res;
	}

    public int getChanges(Session session, int action, String info, String scriptOnBeforeAction, String scriptOnAfterAction) throws com.cifs.or2.kernel.KrnException {
		if(replication_running) return -1;
        System.out.println("REPLICATION - getChanges(" + action + ")");
        ses = session;
        replication_running = true;
        replicationID = getNewReplicationID();

        int exportResult;
        
        initTraceFile();
        try {
            try {
                ReplExport e = new ReplExport(session);
                exportResult = e.run(action, info, scriptOnBeforeAction, scriptOnAfterAction);
            } catch (Throwable e) {
                log.error(e, e);
                throw new KrnException(0, e.getMessage());
            }
        } finally {
            replication_running = false;
            freeTraceFile();
        }
        registerReplication();
        return exportResult;
    }

    private void registerReplication() throws KrnException{
        KrnClass cls = ses.getClassByName("ReplCollection");
        KrnObject obj = ses.createObject(cls, 0);
        ses.setLong(obj.id, ses.getAttributeByName(cls, "replicationID").id,
                0, replicationID, 0);
    }

    static void trace_important(String text, Log log) {
        trace("", log);
        trace("*******************************", log);
        trace(text, log);
        trace("*******************************", log);
        trace("", log);
    }

    static void trace(String text, Log log) {
        trace_count++;
        trace(trace_log, text, log);
        if (trace_count == 10 && trace_log_file != null) {
            trace_count = 0;
            try {
                PrintWriter pw = new PrintWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(trace_log_file.getPath(), true), "UTF-8"
                        )
                );
                try {
                    pw.print(trace_log.toString());
                } finally {
                    pw.close();
                }
                trace_log = null;
                trace_log = new StringBuffer();
                System.gc();
            } catch (Exception e) {
                trace("FileNotFoundException error: e.getMessage() = " + e.getMessage(), log);
            }
        }
    }

    private static void trace(StringBuffer sb, String text, Log log) {
        String time = "[" + formatter_time.format(new java.util.Date()) + "] ";
        log.info(time + text);
        sb.append(time + text + "\r\n");
    }

    private void initTraceFile() {
        if (trace_log != null)
            return;
        trace_log = new StringBuffer();
        trace_log_file = new File("rep_" + replicationID + ".log");
        if (trace_log_file.exists())
            trace_log_file.delete();
        try {
            trace_log_file.createNewFile();
        } catch (IOException e) {
            System.out.println("ERROR IOException: " + e.getMessage());
        }
    }

    private void freeTraceFile() {
        if (trace_log == null)
            return;
        try {
            PrintWriter pw = new PrintWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(
                                    trace_log_file.getPath(), true), "UTF-8"));
            try {
                pw.print(trace_log.toString());
            } finally {
                pw.close();
            }
            trace_log = null;
        } catch (Exception e) {
            System.out.println("FileNotFoundException error: e.getMessage() = " + e.getMessage());
        }
    }

    private long getNewReplicationID() throws KrnException {
        KrnObject[] coll = ses.getClassObjects(
                ses.getClassByName("ReplCollection"),
                new long[0], 0
        );
        if (coll.length > 0) {
            return ses.getLongsSingular(
                    coll[coll.length - 1],
                    ses.getAttributeByName(
                        ses.getClassByName("ReplCollection"), "replicationID"),
                    false) + 1;
        }
        return 1;
    }
}
