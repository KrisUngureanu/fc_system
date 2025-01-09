package kz.tamur.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import kz.tamur.lang.SystemOp;
import kz.tamur.or3ee.server.kit.SrvUtils;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnMethod;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.LongValue;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvOrLang;

public class ErrorsNotification {
	private static long MAX_RECORDS=1000000;//максимальное количество записей при отборе в интерфейс
	private static long MAX_DURATION=600000;//максимальная продолжительность выполнения запроса(10мин)
	private static String NO_CONNECT_DB="TO_400";
	private static KrnClass notifyCls;
	private static KrnMethod notifyMethod;
	private static boolean init=false;
	private static String dsName="";
	private static List<String> tos;
	private static String from="";
	private static String fromHost="";
	private static String fromPort="";
	private static String fromPasswd="";
	
    public static  void notifyErrors(String code,String key,String message, Throwable cause,Session userSession){
    	Session session=null;
    	if(!init) return;
        try {
	        	if(userSession!=null){
		        	dsName=userSession.getDsName();
		        	String suId=userSession.getUserSession().getId().toString();
	        	}
	        	session=SrvUtils.getSession(dsName,"sys",null);
	        	KrnDate time=new KrnDate(System.currentTimeMillis());
	            StringBuffer causes = new StringBuffer();
	            causes.append(message);
	            if(cause!=null){
	                causes.append("\n"+cause.toString());
	                Class<? extends Throwable> causeCls=cause.getClass();
	                for(int i=0;i<10;i++){
	                	cause=cause.getCause();
	                	if(cause==null) break;
	                	if(causeCls!=cause.getClass()){
	                		causes.append("\n"+cause.toString());
	            		causeCls=cause.getClass();
	                	}	
	                }
	            }
	            if(NO_CONNECT_DB.equals(code)){
	            	causes.insert(0,code+"\n"+time.toString("yyyy-MM-dd hh:mm:ss.SSS")+"\n"+code+"\n");
	            	SystemOp.sendMail(fromHost, fromPort, from, tos, from, fromPasswd, "NO_CONNECT_DB", causes.toString(), null);
	            	
	            }else{
		            List<Object> args = new ArrayList<Object>();
		        	args.add(code);
		           	args.add(time.toString("yyyy-MM-dd hh:mm:ss.SSS"));
		        	args.add(key);
		        	args.add(causes.toString());
		            SrvOrLang orlang = session.getSrvOrLang();
					orlang.exec(notifyCls,notifyCls,notifyMethod.name, args, new Stack<String>());
		        	if(session!=null)
		        		session.commitTransaction();
	            }
			} catch (Throwable e) {
				e.printStackTrace();
            } finally {
                if (session != null)
                	session.release();
        }
    }
    public static void  init(Session session){
    	if(init) return;
		try {
			dsName=session.getDsName();
			notifyCls = session.getClassByName("Журнал оповещения администратора");
			if(notifyCls!=null){
				notifyMethod = session.getMethodByName(notifyCls.id, "обработка события");
				KrnClass paramCls=session.getClassByName("Параметры Системы оповещения");
				KrnClass tosCls=session.getClassByName("Зап табл лиц для оповещения");
				KrnAttribute recordsAttr=session.getAttributeByName(paramCls, "лимит кол-ва записей");
				KrnAttribute durationsAttr=session.getAttributeByName(paramCls, "лимит по времени");
				KrnAttribute fromAttr=session.getAttributeByName(paramCls, "e-mail отправителя");
				KrnAttribute fromHostAttr=session.getAttributeByName(paramCls, "host");
				KrnAttribute fromPortAttr=session.getAttributeByName(paramCls, "port");
				KrnAttribute fromPasswdAttr=session.getAttributeByName(paramCls, "пароль e-mail отправителя");
				KrnAttribute tosAttr=session.getAttributeByName(paramCls, "зап табл лиц для оповещения");
				KrnAttribute toAttr=session.getAttributeByName(tosCls, "e-mail");
				KrnObject[] objs= session.getClassObjects(paramCls, new long[0], 0);
				if(objs.length>0){
					KrnObject obj=objs[0];
					LongValue[] recs=session.getLongValues(new long[]{obj.id},recordsAttr.id, 0);
					if(recs.length>0){
						MAX_RECORDS=recs[0].value;
					}
					LongValue[] durations=session.getLongValues(new long[]{obj.id},durationsAttr.id, 0);
					if(durations.length>0){
						MAX_DURATION=durations[0].value;
					}
					String[] froms=session.getStrings(obj.id, fromAttr.id, 0,false, 0);
					if(froms.length>0){
						from=froms[0];
					}
					String[] hosts=session.getStrings(obj.id, fromHostAttr.id, 0,false, 0);
					if(hosts.length>0){
						fromHost=hosts[0];
					}
					String[] ports=session.getStrings(obj.id, fromPortAttr.id, 0,false, 0);
					if(ports.length>0){
						fromPort=ports[0];
					}
					String[] pds=session.getStrings(obj.id, fromPasswdAttr.id, 0,false, 0);
					if(pds.length>0){
						fromPasswd=pds[0];
					}
					KrnObject[] toObjs=session.getObjects(obj.id, tosAttr.id,new long[0], 0);
					if(toObjs.length>0){
						tos=new ArrayList<String>();
						for(KrnObject toObj:toObjs){
							String[] tos_=session.getStrings(toObj.id, toAttr.id, 0,false, 0);
							if(tos_.length>0){
								tos.add(tos_[0]);
							}
						}
					}
				}

				init=true;
			}
			
		} catch (KrnException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
    public static boolean isInitialize(){
    	return init;
    }
    public static long getMaxRecords(){
    	return MAX_RECORDS;
    }
    public static long getMaxDuration(){
    	return MAX_DURATION;
    }
}
