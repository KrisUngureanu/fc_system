package com.cifs.or2.server.timer;


import com.cifs.or2.kernel.*;
import com.cifs.or2.server.*;
import com.cifs.or2.server.Session;

import java.util.TimerTask;
import java.util.Timer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.net.InetAddress;
import java.net.UnknownHostException;

import kz.tamur.admin.ExchangeEvents;
import kz.tamur.comps.Constants;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.server.wf.ExecutionComponent;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 20.04.2005
 * Time: 14:44:40
 * To change this template use File | Settings | File Templates.
 */
public class TimerTaskImpl extends TimerTask{
    private Task task;
    private Timer timer;
    private InetAddress address;
    private String dsName;

    public TimerTaskImpl(Task task_,Timer timer,String dsName) {
        this.timer=timer;
        task_.ttask=this;
        this.task=task_;
        this.dsName = dsName;
        try {
            this.address=InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        if(task.process==null || task.process.length==0) return;
        //Описание выполняемой команды и ее запуск
        Session s = null;
        ExecutionComponent exeComp = Session.getExeComp(dsName);
        Date dateStart=new Date();
        Date dateNext=null;
        String err="";
        KrnObject tprot=null;
       try{
            s = SrvUtils.getSession(dsName, task.user, address.getHostAddress(), address.getHostName(), false);
       		tprot=s.createObject(ServerTasks.getClsProtocol(), 0);
            Map<String, Object> vars = new HashMap<String,Object>();
			vars.put("TIMERTASK","1");//передача переменных о планировщике и ссылке на объект протокола в процесс
			vars.put("TIMERPROTOCOLOBJID",tprot.id);
            s.commitTransaction();
         if (task.user != null) {
	            for (long proces : task.process) {
                    String[] res_ = exeComp.startProcessInstance(proces, task.user.id,((ServerUserSession)s.getUserSession()).getUser(),vars,address.getHostAddress(),address.getHostName(), true,false, true, null);
                    if (res_.length > 0 && !res_[0].equals("")) {
                        err+=res_[0];
                        ServerTasks.writeLogRecord(task,ExchangeEvents.TMR_001);
                    }
                    s.commitTransaction();
                }
            }
            ServerTasks.writeLogRecord(task,ExchangeEvents.TMR_000);
            //Запуск таймера для следующего раза
            TimerTaskImpl ttask=new TimerTaskImpl(task,timer,dsName);
            dateNext = ServerTasks.getNextDate(task.mins,task.hours,task.daysm,task.months,task.daysw);
            if (task.ready == Constants.TIMER_ACTIVE) {
	            if (dateNext!=null) {
	                task.date=dateNext;
	                timer.schedule(ttask,dateNext);
	            } else
	            	err+="\nНе задано время для следующего выполнения!";
            }
        }catch (Throwable e){
            e.printStackTrace();
            err+="\nОшибка при выполнении";
            ServerTasks.writeLogRecord(task,ExchangeEvents.TMR_001);
        } finally {
        	if (s != null) {
                ServerTasks.writeProtocol(tprot,err, task, dateStart, dateNext,"".equals(err)?1:2, s);
        		s.release();
        	}
        }
    }
    public void runOnes() {
        if(task.process==null || task.process.length==0) return;
        //Описание выполняемой команды и ее запуск
        Session s = null;
        ExecutionComponent exeComp = Session.getExeComp(dsName);
        String err="";
        KrnObject tprot=null;
        try{
            if (task.user != null) {
               s = SrvUtils.getSession(dsName, task.user, address.getHostAddress(), address.getHostName(), false);
          		tprot=s.createObject(ServerTasks.getClsProtocol(), 0);
                Map<String, Object> vars = new HashMap<String,Object>();
    			vars.put("TIMERTASK","1");//передача переменных о планировщике и ссылке на объект протокола в процесс
    			vars.put("TIMERPROTOCOLOBJID",tprot.id);
                s.commitTransaction();
	            for (long proces : task.process) {
                    String[] res_ = exeComp.startProcessInstance(proces, task.user.id,((ServerUserSession)s.getUserSession()).getUser(),vars,address.getHostAddress(),address.getHostName(), true,false, true, null);
                    if (res_.length > 0 && !res_[0].equals("")) {
                        err+=res_[0];
                        ServerTasks.writeLogRecord(task,ExchangeEvents.TMR_003);
                    }
                    s.commitTransaction();
                }
            }
            ServerTasks.writeLogRecord(task,ExchangeEvents.TMR_000);
        }catch (Throwable e){
            e.printStackTrace();
            err+="\nОшибка при выполнении";
            ServerTasks.writeLogRecord(task,ExchangeEvents.TMR_001);
        } finally {
        	if (s != null) {
        		Date dateNext= ServerTasks.getNextDate(task.mins,task.hours,task.daysm,task.months,task.daysw);
               ServerTasks.writeProtocol(tprot,err, task, new Date(), dateNext,"".equals(err)?1:2, s);
        		s.release();
        	}
        }
    }
}
