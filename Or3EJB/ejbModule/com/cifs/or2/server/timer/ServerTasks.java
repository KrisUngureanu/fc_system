package com.cifs.or2.server.timer;

import com.cifs.or2.server.Session;
import com.cifs.or2.kernel.*;
import com.cifs.or2.util.Funcs;

import java.util.*;
import java.util.Date;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import org.jdom.Element;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.admin.ExchangeEvents;
import kz.tamur.comps.Constants;
import kz.tamur.ods.Value;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.server.wf.ExecutionComponent;

/**
 * Created by IntelliJ IDEA.
 * User: Vale
 * Date: 20.04.2005
 * Time: 17:55:37
 * To change this template use File | Settings | File Templates.
 */
public class ServerTasks{
    private static final int[] empty = new int[0]; // если не задан ни один день недели то опираемся на задание дней месяца

    private static final int[] mins = new int[60];
    private static final int[] hours= new int[24];
    private static final int[] daysm = new int[31];
    private static final int[] months = new int[] {0,1,2,3,4,5,6,7,8,9,10,11};
    
    static {
    	for (int i = 0; i<60; i++) mins[i] = i;
    	for (int i = 0; i<24; i++) hours[i] = i;
    	for (int i = 1; i<32; i++) daysm[i-1] = i;
    }
    
    private static KrnAttribute attr_start;
    private HashMap<Long,Task> tasks=new HashMap<Long,Task>();
    private static KrnClass cls;
    private static KrnAttribute procAttr;
    private static KrnClass cls_protocol;
    private static KrnAttribute attr_protocol_status;
    private static KrnAttribute attr_protocol_start;
    private static KrnAttribute attr_protocol_finish;
    private static KrnAttribute attr_protocol_next_start;
    private static KrnAttribute attr_protocol_timer;
    private static KrnAttribute attr_protocol_err;
    private static KrnObject lang_obj=null;
    private Timer timer;
    private String dsName;

	private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ServerTasks.class.getName());

	private boolean activateScheduler;
	
    public ServerTasks(Session s, String dsName, boolean activateScheduler) {
		this.dsName = dsName;
		this.activateScheduler = activateScheduler;

    	cls = s.getClassByName("Timer");
    	procAttr = s.getAttributeByName(cls, "process");
    	
        cls_protocol = s.getClassByName("TimerProtocol");
        attr_protocol_status=s.getAttributeByName(cls_protocol,"status");
        attr_protocol_start=s.getAttributeByName(cls_protocol,"timeStart");
        attr_protocol_finish=s.getAttributeByName(cls_protocol,"timeFinish");
        attr_protocol_next_start=s.getAttributeByName(cls_protocol,"timeNextStart");
        attr_protocol_timer=s.getAttributeByName(cls_protocol,"timer");
        attr_protocol_err=s.getAttributeByName(cls_protocol,"err");
        attr_start=s.getAttributeByName(cls,"start");
        List<KrnObject> langs=s.getSystemLangs();
        if(langs.size()>0) lang_obj=langs.get(0);
        String isServerTasks=System.getProperty("isServerTasks");
        if("1".equals(isServerTasks) || "true".equals(isServerTasks)) init(s);
    }
    
    public boolean init(Session s){
        try {
        	if (activateScheduler) {
        		
	            KrnObject[] objs=s.getClassObjects(cls,new long[0],0);
	            for (KrnObject obj : objs) {
	                if (obj.classId == cls.id) {
	                	Set<Value> pvs = s.getValues(new long[] { obj.id }, procAttr.id, 0, 0);
						for (Value pv : pvs) {
							if (ExecutionComponent.isProcUidAllowed(((KrnObject) pv.value).uid)) {
			                    tasks.put(obj.id, new Task(obj));
								break;
							}
						}
	                }
	            }
	            long[] ids=Funcs.makeLongArray(tasks.keySet());
	            if(ids.length>0) {
		            loadTasks(ids,s);
		            timer=new Timer();
		            startTasks(ids,s);
		    		log.info("Планировщик инициализирован!");
	            }
	            return true;
        	} else {
        		log.info("Планировщик заданий отключен!");
        	}
        	
        }catch(KrnException e){
            e.printStackTrace();
        }
        return false;
    }
    private void startTasks(long[] ids,Session s){
        for (long id : ids) {
            Task task = tasks.get(new Long(id));
            if(task.process==null || task.process.length==0) continue;
            if (task.ready != Constants.TIMER_NOT_ACTIVE) {
            	String err=null;
            	Date dateNext=null;
            	Date dateStart = new Date();
                TimerTaskImpl ttask = new TimerTaskImpl(task, timer, dsName);
    	    	KrnObject tprot=null;
				try {
					tprot = s.createObject(cls_protocol, 0);
				} catch (KrnException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
              if (task.ready == Constants.TIMER_RUN_AT_START && (task.date == null || task.date.before(dateStart))) {
                    task.date = dateStart;
                    writeLogRecord(task, ExchangeEvents.TMR_000);
                    ttask.run();
                } else if (task.ready == Constants.TIMER_RUN_DAY_START && task.date != null && task.date.before(dateStart)) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(dateStart);
                    int min_ = cal.get(Calendar.MINUTE);
                    int h_ = cal.get(Calendar.HOUR_OF_DAY);
                    int[] nextHM = getNextTime(task.hours, task.mins, h_, min_);
                    if (nextHM != null) {
                    	cal.set(Calendar.HOUR_OF_DAY, nextHM[0]);
                    	cal.set(Calendar.MINUTE, nextHM[1]);
                        dateNext = cal.getTime();
                        task.date = dateNext;
                        writeLogRecord(task, ExchangeEvents.TMR_000);
                        timer.schedule(ttask, dateNext);
                    } else {
                    	// устанавливаем следующий день
                    	cal.set(Calendar.HOUR_OF_DAY, task.hours[0]);
                    	cal.set(Calendar.MINUTE, task.mins[0]);
                    	cal.add(Calendar.DAY_OF_MONTH, 1);
                        dateNext = cal.getTime();
                        task.date = dateNext;
                        writeLogRecord(task, ExchangeEvents.TMR_000);
                        timer.schedule(ttask, dateNext);
                    }
                } else {
                    if (task.date != null && task.date.before(dateStart)) {
                        writeLogRecord(task, ExchangeEvents.TMR_002);
                    }
                    dateNext = getNextDate(task.mins, task.hours, task.daysm, task.months, task.daysw);
                    if(dateNext!=null){
                        task.date = dateNext;
                        timer.schedule(ttask, dateNext);
                    }else{
                    	err="Не задано время для следующего выполнения!";
                    }
                }
                writeProtocol(tprot,err, task, dateStart, dateNext,0, s);
            }
        }
    }
    private void loadTasks(long[] ids,Session s){
        try{
            final KrnAttribute attr_config =s.getAttributeByName(cls,"config");
            final KrnAttribute attr_proc=s.getAttributeByName(cls,"process");
            final KrnAttribute attr_user=s.getAttributeByName(cls,"user");
            final KrnAttribute attr_ready=s.getAttributeByName(cls,"redy");
            final KrnAttribute attr_title=s.getAttributeByName(cls,"title");
            SAXBuilder builder = new SAXBuilder();
            LongValue[] lvs=s.getLongValues(ids,attr_ready.id,0);
            for (LongValue lv : lvs) {
                Task task = tasks.get(lv.objectId);
                if (lv.index == 0) {
                    task.ready = (int) lv.value;
                }
            }
            TimeValue[] tvs=s.getTimeValues(ids,attr_start.id,0);
            for (TimeValue tv : tvs) {
                Task task = tasks.get(tv.objectId);
                if (tv.index == 0) {
                    task.date = kz.tamur.util.Funcs.convertTime(tv.value);
                }
            }
            ObjectValue[] ovs=s.getObjectValues(ids,attr_proc.id,new long[0],0);
            for (ObjectValue ov : ovs) {
                Task task = tasks.get(ov.objectId);
                if (task == null || task.ready == Constants.TIMER_NOT_ACTIVE) continue;
                if (task.process == null) task.process = new long[ov.index + 1];
                if (task.process.length <= ov.index) {
                    long[] src_ = new long[ov.index + 1];
                    System.arraycopy(task.process, 0, src_, 0, task.process.length);
                    task.process = src_;
                }
                task.process[ov.index] = ov.value.id;
            }
            ovs=s.getObjectValues(ids,attr_user.id,new long[0],0);
            for (ObjectValue ov1 : ovs) {
                Task task = tasks.get(ov1.objectId);
                if (ov1.index == 0) {
                    task.user = ov1.value;
                    if (ov1.value != null)
                    	s.getOrgComp().findActorById(ov1.value.id, s);
                }
            }
            StringValue[] svs=s.getStringValues(ids,attr_title.id,lang_obj!=null?lang_obj.id:-1,false,0);
            for (StringValue sv : svs) {
                Task task = tasks.get(sv.objectId);
                if (sv.index == 0)
                    task.title = sv.value;
            }
            for (long id : ids) {
                Task task = tasks.get(new Long(id));
                if (task.ready == Constants.TIMER_NOT_ACTIVE) continue;
                try {
                    byte[] data = s.getBlob(task.obj.id, attr_config.id, 0, 0, 0);
                    if (data.length == 0) continue;
                    InputStream is = new ByteArrayInputStream(data);
                    Document doc = builder.build(is);
                    if (doc == null) continue;
                    Element xml = doc.getRootElement();
                    Element e = xml.getChild("minut");
                    if (e != null) {
                        task.mins = getStrToIntArray(e.getText());
                    } else
                        task.mins = mins;
                    e = xml.getChild("hour");
                    if (e != null) {
                        task.hours = getStrToIntArray(e.getText());
                    } else
                        task.hours = hours;
                    e = xml.getChild("daym");
                    if (e != null) {
                        task.daysm = getStrToIntArray(e.getText());
                    } else
                        task.daysm = daysm;
                    e = xml.getChild("dayw");
                    if (e != null) {
                        task.daysw = getStrToIntArray(e.getText());
                    	// Если заданы дни недели, то игнорируем дни месяца
                        if (task.daysw != null && task.daysw.length > 0)
                        	task.daysm = empty;
                        
                    } else
                        task.daysw = empty;
                    e = xml.getChild("month");
                    if (e != null) {
                        task.months = getStrToIntArray(e.getText());
                    } else
                        task.months = months;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }catch (KrnException e){
            e.printStackTrace();
        }

    }
    
    public static void main(String[] args) {
    	int[] mins = getStrToIntArray("1");
    	int[] hours = getStrToIntArray("2");
    	int[] daysm = ServerTasks.daysm;// //ServerTasks.daysm; getStrToIntArray("1,2,3");
    	int[] months = ServerTasks.months;//getStrToIntArray("1,2,3");
    	int[] daysw = ServerTasks.empty;//getStrToIntArray("3,4,5");
		Date d1 = getNextDate(mins, hours, daysm, months, daysw);
		System.out.println(d1);
	}
    
    //Расчет следующуй даты запуска
    public static Date getNextDate(int[] mins, int[] hours, int[] daysm, int[] months, int[] daysw){
    	//Минуты, часы, месяцы должны быть заданы обязательно, а также либо дни либо дни недели
        
    	if (mins == null || mins.length == 0 || hours == null || hours.length == 0 ||
    		months == null || months.length == 0 || daysm == null || daysw == null || (daysm.length == 0 && daysw.length == 0)) return null;
        
    	// Текущая дата-время
    	Date curDate = new Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        
        // текущие значения минут, часов, дня, дня недели, месяца, года
        int m = cal.get(Calendar.MINUTE);
        int h = cal.get(Calendar.HOUR_OF_DAY);
        int dm = cal.get(Calendar.DAY_OF_MONTH);
        int dw = cal.get(Calendar.DAY_OF_WEEK);
        int mt = cal.get(Calendar.MONTH);
        int g = cal.get(Calendar.YEAR);
        
        int[] nextHM = getNextTime(hours, mins, h, m);
        
        // Если есть следующее время запуска внутри дня и сегодняшний день является допустимым
        if (nextHM != null && isCurrentDayAvailable(months, daysm, daysw, mt, dm, dw)) {
        	cal.set(g, mt, dm, nextHM[0], nextHM[1]);
            return cal.getTime();
        } else {
        	// иначе вычисляем следующий день, а время берем первое возможное hours[0], mins[0]

        	// устанавливаем следующий день
        	cal.set(g, mt, dm, hours[0], mins[0]);
        	cal.add(Calendar.DAY_OF_MONTH, 1);
        	
        	dm = cal.get(Calendar.DAY_OF_MONTH);
            dw = cal.get(Calendar.DAY_OF_WEEK);
            mt = cal.get(Calendar.MONTH);
            g = cal.get(Calendar.YEAR);
        	
        	// поочередно пробегаемся по месяцам
            for (int month : months) {
            	// если текущий месяц равен доступному            	
                if (mt == month) {
                	int nextDay = getNextDay(cal, daysm, daysw);
                	if (nextDay > 0)
                		return cal.getTime();
                }
                if (mt < month) {
                	// устанавливаем первое число месяца
                	cal.set(g, month, 1, hours[0], mins[0]);
                	
                	int nextDay = getNextDay(cal, daysm, daysw);
                	if (nextDay > 0)
                		return cal.getTime();
                }
            }
            // если до сих пор не нашли дату запуска, то переходим на следующий год
        	// устанавливаем первое число первого возможного месяца
        	cal.set(g + 1 , months[0], 1, hours[0], mins[0]);
        	
        	int nextDay = getNextDay(cal, daysm, daysw);
        	if (nextDay > 0)
        		return cal.getTime();
        }
        
        return null;
    }
    
    // возвращаем ближайшее доступное время к текущему
    private static int[] getNextTime(int[] hours, int[] mins, int curHour, int curMin) {
    	// пробегаемся по возможным значениям часа
    	for (int hour : hours) {
    		// если текущий час равен доступному
            if (curHour == hour) {
            	for (int min : mins) {
            		// и текущая минута меньше доступной
                    if (curMin < min) {
                    	// то возвращаем ближайшее время
                        return new int[] {hour, min};
                    }
                }
            }
            // если текущий час меньше следующего доступного
            else if (curHour < hour) {
            	// то возвращаем ближайшие час и наименьшее возможное значение минуты
            	return new int[] {hour, mins[0]};
            }
        }
    	
    	return null;
    }
    
    private static int getNextDay(Calendar cal, int[] daysm, int[] daysw) {
    	int dm = cal.get(Calendar.DAY_OF_MONTH);
        int dw = cal.get(Calendar.DAY_OF_WEEK);
        int mt = cal.get(Calendar.MONTH);

    	// Если заданы дни месяца
    	if (daysm.length > 0) {
    		for (int daym : daysm) {
    			if (dm == daym) {
    				// возвращаем текущий день если он допустим
    			    return daym;
                } else if (dm < daym) {
                	// иначе следующий допустимый
                	cal.add(Calendar.DAY_OF_MONTH, daym - dm);
                    return daym;
                }
    		}
    	} else if (daysw.length > 0) {
    		for (int dayw : daysw) {
    			if (dw == dayw) {
    				// возвращаем текущий день если он допустим
    			    return dm;
                } else if (dw < dayw) {
                	// иначе следующий допустимый, если он в текущем месяце
                	cal.add(Calendar.DAY_OF_MONTH, dayw - dw);
                	if (mt == cal.get(Calendar.MONTH))
                		return dm + dayw - dw;
                	else
                		return -1;
                }
    		}
    		
			// Если не нашли день недели, то устанавливаем воскресенье (1) и заново ищем
			cal.add(Calendar.DAY_OF_MONTH, 8 - dw);
        	if (mt == cal.get(Calendar.MONTH)) {
        		dw = cal.get(Calendar.DAY_OF_WEEK);
        		dm = cal.get(Calendar.DAY_OF_MONTH);
        		
        		for (int dayw : daysw) {
        			if (dw == dayw) {
        				// возвращаем текущий день если он допустим
        			    return dm;
                    } else if (dw < dayw) {
                    	// иначе следующий допустимый, если он в текущем месяце
                    	cal.add(Calendar.DAY_OF_MONTH, dayw - dw);
                    	if (mt == cal.get(Calendar.MONTH))
                    		return dm + dayw - dw;
                    	else
                    		return -1;
                    }
        		}
        	}
    	}
    	return -1;
    }
    
    // находится ли текущий день среди возможных дней для запуска
    private static boolean isCurrentDayAvailable(int[] months, int[] days, int[] daysOfWeek, int curMonth, int curDay, int curDayOfWeek) {
    	// пробегаемся по возможным значениям месяцев
    	for (int month : months) {
    		// если текущий месяц равен доступному
            if (curMonth == month) {
            	for (int day : days) {
            		// и текущий день равен доступному
                    if (curDay == day) {
                        return true;
                    }
                }
            	for (int dayOfWeek : daysOfWeek) {
            		// или текущий день недели равен доступному
                    if (curDayOfWeek == dayOfWeek) {
                        return true;
                    }
                }
            }
        }
    	
    	return false;
    }

    private static int[] getStrToIntArray(String str){
        StringTokenizer st= new StringTokenizer(str,",",false);
        int[] res=new int[st.countTokens()];
        int i=0;
        while(st.hasMoreTokens()){
            res[i++]= Integer.valueOf(st.nextToken());
        }
        Arrays.sort(res);
        return res;
    }
    public synchronized void changeTimerTask(long objId,boolean isDelete,Session s){
          Task task= tasks.get(new Long(objId));
          if(isDelete){
              if(task==null)return;
              if(task.ttask!=null)task.ttask.cancel();
              tasks.remove(new Long(objId));
          }else if(task!=null){
              long[]ids=new long[]{objId};
                if(task.ttask!=null){
                    task.ttask.cancel();
                    task.ttask=null;
                }
                loadTasks(ids,s);
                startTasks(ids,s);
          }else{
              task= new Task(new KrnObject(objId, "", cls.id));
              tasks.put(objId,task);
              long[]ids=new long[]{objId};
              loadTasks(ids,s);
              startTasks(ids,s);
          }
    }
    public synchronized void executeTask(long id,Session s){
        Task task=tasks.get(id);
        if(task!=null && task.ttask!=null && task.ready>Constants.TIMER_NOT_ACTIVE){
            Date cdate=new Date();
            task.date=cdate;
            writeLogRecord(task,ExchangeEvents.TMR_003);
            task.ttask.runOnes();
        }
    }
    public static void writeLogRecord(Task task,ExchangeEvents event){
        String res = event.getEvent() + " | " + task.title;
        log.info(res);
    }
    public static void writeProtocol(KrnObject tprot,String err,Task timer,Date start,Date nextStart,long status,Session s){
        
        try{
	        s.setValue(tprot, attr_protocol_timer.id,0, 0,0, timer.obj,false);
	        s.setTime(tprot.id, attr_protocol_start.id,0,kz.tamur.util.Funcs.convertTime(start),0);
	        s.setTime(tprot.id, attr_protocol_next_start.id,0,kz.tamur.util.Funcs.convertTime(nextStart),0);
	        s.setLong(tprot.id, attr_protocol_status.id,0, status,0);
	       	if(err!=null && !"".equals(err))
	       		s.setString(tprot.id, attr_protocol_err.id, 0, 0, false,err, 0);
	        s.commitTransaction();
        }catch(Exception e){
        	s.rollbackTransactionQuietly();
        	e.printStackTrace();
        }
    }
    public static void updateProtocol(long tpobjId,boolean isError,Session s){//проставляется время завершения задания запущенного планировщиком
        try{
	        s.setTime(tpobjId, attr_protocol_finish.id,0,kz.tamur.util.Funcs.convertTime(new Date()),0);
	        if(isError) s.setString(tpobjId, attr_protocol_err.id,0,0,false,"ERROR",0);
	        s.commitTransaction();
        }catch(Exception e){
        	s.rollbackTransactionQuietly();
        	e.printStackTrace();
        }
    }
    public static KrnClass getClsProtocol() {
    	return cls_protocol;
    }
    
    public boolean getActivateScheduler() {
		return activateScheduler;
	}

    public void setActivateScheduler(boolean activateScheduler, Session s) {
    	this.activateScheduler = activateScheduler;
		if (activateScheduler == true) {
			// Активация планировщика
			schedulerActivation(s);
			log.info("Планировщик заданий запущен!");
		} else {
			// Деактивация планировщика
			schedulerDeactivation(s);
			log.info("Планировщик заданий отключен!");
		}
	}

	private void schedulerActivation(Session s) {
		if (timer == null) {
			try {
				KrnObject[] objs = s.getClassObjects(cls, new long[0], 0);
				for (KrnObject obj : objs) {
					if (obj.classId == cls.id) {
						Set<Value> pvs = s.getValues(new long[] { obj.id }, procAttr.id, 0, 0);
						for (Value pv : pvs) {
							if (ExecutionComponent.isProcUidAllowed(((KrnObject) pv.value).uid)) {
			                    tasks.put(obj.id, new Task(obj));
								break;
							}
						}
					}
				}
			} catch (KrnException e) {
				e.printStackTrace();
			}
			long[] ids = Funcs.makeLongArray(tasks.keySet());
			loadTasks(ids, s);
			timer = new Timer();
			startTasks(ids, s);
		} else {
			log.warn("Планировщик заданий уже включен!");
		}
	}

	private void schedulerDeactivation(Session session) {
		long[] ids = Funcs.makeLongArray(tasks.keySet());
		for (int i = 0; i < ids.length; i++) {
			Task task = tasks.get(new Long(ids[i]));
			if (task != null) {
				if (task.ttask != null) {
					task.ttask.cancel();
					task.ttask = null;
				}
			}
		}
		tasks.clear();
		try {
			KrnObject[] objs = session.getClassObjects(cls, new long[0], 0);
			for (KrnObject obj : objs) {
				if (obj.classId == cls.id) {
					Set<Value> pvs = session.getValues(new long[] { obj.id }, procAttr.id, 0, 0);
					for (Value pv : pvs) {
						if (ExecutionComponent.isProcUidAllowed(((KrnObject) pv.value).uid)) {
		                    tasks.put(obj.id, new Task(obj));
							break;
						}
					}
				}
			}
		} catch (KrnException e) {
			e.printStackTrace();
		}
		timer.cancel();
		timer = null;
	}
}
