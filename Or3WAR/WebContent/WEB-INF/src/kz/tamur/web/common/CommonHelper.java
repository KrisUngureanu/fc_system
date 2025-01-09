package kz.tamur.web.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Locale;

import kz.tamur.comps.Constants;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;
import kz.tamur.util.ThreadLocalDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.client.util.AttrRequestBuilder;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.ObjectValue;
import com.cifs.or2.kernel.Time;

/**
 * Created by IntelliJ IDEA.
 * User: Администратор
 * Date: 05.12.2006
 * Time: 10:15:30
 * To change this template use File | Settings | File Templates.
 */
public class CommonHelper {
	private static final Log log = LogFactory.getLog("WebLog" + (UserSession.SERVER_ID != null ? ("." + UserSession.SERVER_ID) : ""));
    public static final int TASKS_BUTTON = 0;
    public static final int ARCHIVE_BUTTON = 1;
    public static final int DICT_BUTTON = 2;

    public static final double CRITICAL_MEMORY_LOW = 0.6;
    public static final double CRITICAL_MEMORY_HIGH = 0.9;
    public static final int BtoMB = 1024*1024;

    public static final ResourceBundle RESOURCE_RU = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("ru"));
    public static final ResourceBundle RESOURCE_KZ = ResourceBundle.getBundle(Constants.NAME_RESOURCES, new Locale("kk"));
    public static final ThreadLocalDateFormat fullTimeFormat = new ThreadLocalDateFormat("dd.MM.yyyy HH:mm:ss");
    
    private static boolean serverOverloaded = false;
    
    private int selectedButton;
    private WebSession s = null;
    private final Map<Long, String> openedProcesses = new HashMap<Long,String>();
    private final List<KrnObject> favouriteProcesses = new ArrayList<>();

    public CommonHelper(WebSession s) {
        this.selectedButton = TASKS_BUTTON;
        this.s = s;
        initUserNotOpenProcessDef();
        initUserFavouriteProcesses();
    }

    public int getSelectedButton() {
        return selectedButton;
    }

    public void setSelectedButton(int selectedButton) {
        this.selectedButton = selectedButton;
    }
    
    public static String getUsedMemorySnapshot() {
    	long max = Runtime.getRuntime().maxMemory() / BtoMB;
    	long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / BtoMB;
    	return ""+used+"Mb/"+max+"Mb";
    }
    
    public static void takeMemorySnapshot(WebSession s) {
    	long max = Runtime.getRuntime().maxMemory() / BtoMB;
    	long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / BtoMB;
    	
    	log.info(new StringBuilder("Max memory: ").append(max).append(" Mb").toString());
        log.info(new StringBuilder("Used memory: ").append(used).append(" Mb"));
        
        double percent = (1.0 * used)/max;
        int perc = (int) (100 * percent);
        
        if (s != null) {
	        if (!serverOverloaded && percent > CRITICAL_MEMORY_HIGH) {
	        	serverOverloaded = true;
	        	String msg = new StringBuilder("Сервер перегружен! Сервер использует ").append(perc).append("% доступной памяти.").append(UserSession.SERVER_ID != null ? " ID сервера: " + UserSession.SERVER_ID + "." : "").toString();
	        	try {
	        		s.getKernel().sendMessage(msg);
	        	} catch (Exception e) {
	    			log.error(e, e);
	        	}
	        } else if (serverOverloaded && percent < CRITICAL_MEMORY_LOW) {
	        	serverOverloaded = false;
	        	String msg = new StringBuilder("Память, используемая сервером, снизилась до приемлемого уровня. Сервер использует ").append(perc).append("% доступной памяти.").append(UserSession.SERVER_ID != null ? " ID сервера: " + UserSession.SERVER_ID + "." : "").toString();
	        	try {
	        		s.getKernel().sendMessage(msg);
	        	} catch (Exception e) {
	    			log.error(e, e);
	        	}
	        }
        }
    }
    
    public List<KrnObject> getUserFavouriteProcesses() {
		return favouriteProcesses;
    }

    private void initUserFavouriteProcesses() {
    	final Kernel krn = s.getKernel();
    	KrnObject user = krn.getUser().getObject();
    	favouriteProcesses.clear();
    	
    	try {
	    	KrnClass favouriteProcessCls = krn.getClassByName("UserFavouriteProcess");
	    	KrnAttribute userAttr = krn.getAttributeByName(favouriteProcessCls, "user");
	    	KrnAttribute processDefAttr = krn.getAttributeByName(favouriteProcessCls, "processDef");
	    	
	    	KrnObject[] objsByUser = krn.getObjectsByAttribute(favouriteProcessCls.id, userAttr.id, 0, 0, user, 0);
	    	for (int i = 0; i < objsByUser.length; i++) {
	    		KrnObject favouriteProcess = krn.getObjectsSingular(objsByUser[i].id, processDefAttr.id, false);
	    		if (favouriteProcess != null) {
	    			favouriteProcesses.add(favouriteProcess);
	    		}
	    	}
    	} catch (Throwable e) {
			log.error(e, e);
		}
    }
    
    public boolean addToFavorites(KrnObject obj) throws KrnException {
		// Проверка на наличие

    	boolean isExists = favouriteProcesses.contains(obj);
    	
		if (!isExists) {
			final Kernel krn = s.getKernel();
	    	KrnObject user = krn.getUser().getObject();
	    	
	    	KrnClass favouriteProcessCls = krn.getClassByName("UserFavouriteProcess");
	    	KrnObject favouriteProcess = krn.createObject(favouriteProcessCls, 0);
	    	krn.setLong(favouriteProcess.id, favouriteProcessCls.id, "processDef", 0, obj.id, 0);
	    	krn.setLong(favouriteProcess.id, favouriteProcessCls.id, "user", 0, user.id, 0);
	    	
	    	favouriteProcesses.add(obj);
	    	return true;
		}
		return false;
	}
    
    public boolean removeFromFavorites(KrnObject obj) throws KrnException {
		// Проверка на наличие

    	boolean isExists = favouriteProcesses.remove(obj);
    	
		if (isExists) {
			final Kernel krn = s.getKernel();
	    	KrnObject user = krn.getUser().getObject();
	    	
	    	KrnClass favouriteProcessCls = krn.getClassByName("UserFavouriteProcess");

	    	KrnAttribute userAttr = krn.getAttributeByName(favouriteProcessCls, "user");
	    	KrnAttribute processDefAttr = krn.getAttributeByName(favouriteProcessCls, "processDef");

	    	KrnObject[] objsByUser = krn.getObjectsByAttribute(favouriteProcessCls.id, userAttr.id, 0, 0, user, 0);
	    	for (int i = 0; i < objsByUser.length; i++) {
	    		KrnObject favouriteProcess = krn.getObjectsSingular(objsByUser[i].id, processDefAttr.id, false);
	    		if (obj.equals(favouriteProcess)) {
	    			krn.deleteObject(objsByUser[i], 0);
	    			return true;
	    		}
	    	}
		}
		return false;
	}

    public Map<Long, String> getUserNotOpenProcessDef() {
		return openedProcesses;
    }

    private void initUserNotOpenProcessDef() {
    	final Kernel krn = s.getKernel();
    	long userID = krn.getUser().getObject().id;
    	openedProcesses.clear();
    	
    	try {
			KrnClass cls = krn.getClassByName("ProcessDefUsingHistory");
			KrnAttribute userAttr = krn.getAttributeByName(cls, "user");
			KrnAttribute processDefAttr = krn.getAttributeByName(cls, "processDef");
			KrnAttribute timeAttr = krn.getAttributeByName(cls, "time");
			//Берем все объекты класса 'ProcessDefUsingHistory' у которых атрибут user равен значению userID
	        KrnObject[] objs = krn.getObjectsByAttribute(cls.id, userAttr.id, 0, ComparisonOperations.CO_EQUALS, userID, 0);
	        if (objs != null && objs.length > 0) {
	        	long[] objIds = Funcs.makeObjectIdArray(objs);
	        
				// С каждого объекта берем значение атрибута 'processDef' и время с атрибута 'time'
				AttrRequestBuilder arb = new AttrRequestBuilder(cls, krn).add(processDefAttr).add(timeAttr);
	            List<Object[]> recs = krn.getObjects(objIds, arb.build(), 0);
                
                for (Object[] rec : recs) {
                	KrnObject processDef = arb.getObjectValue("processDef", rec);
                	Time time = arb.getTimeValue("time", rec);
                	
                	if (processDef != null) {
                		openedProcesses.put(processDef.id, (time != null) ? fullTimeFormat.format(Funcs.convertTime(time)) : null);
                	}
                }
			}
		} catch (Throwable e) {
			log.error(e, e);
		}
	}
    
    public void setUserNotOpenProcessDef(long id) {
    	final Kernel krn = s.getKernel();
    	boolean timeRecord = false;
    	long userID = krn.getUser().getObject().id;
    	
    	Date time = new Date();
    	synchronized (openedProcesses) {
    		openedProcesses.put(id, fullTimeFormat.format(time));
		}

		try {
			KrnClass cls = krn.getClassByName("ProcessDefUsingHistory");
			KrnAttribute userAttr = krn.getAttributeByName(cls, "user");
			KrnAttribute processDefAttr = krn.getAttributeByName(cls, "processDef");
			KrnAttribute timeAttr = krn.getAttributeByName(cls, "time");

			//Берем все объекты класса 'ProcessDefUsingHistory' у которых атрибут user равен значению userID
	        KrnObject[] objs = krn.getObjectsByAttribute(cls.id, userAttr.id, 0, ComparisonOperations.CO_EQUALS, userID, 0);
	        if (objs != null && objs.length > 0) {
	        	long[] objIds = Funcs.makeObjectIdArray(objs);

	        	// С каждого объекта берем значение атрибута 'processDef'
	        	// и сравниваем с текущим пользователем и запускаемом процессом
				ObjectValue[] processDefs = krn.getObjectValues(objIds, processDefAttr, 0);
				
				for (ObjectValue ov : processDefs) {
					if (ov.value != null && ov.value.id == id) {
						s.getKernel().setTime(ov.objectId, timeAttr.id, 0, time, 0);
						timeRecord = true;
						break;
					}
				}
			}
			if (!timeRecord) {
				KrnObject obj = krn.createObject(cls, 0);
				if (obj != null) {
					krn.setObject(obj.id, userAttr.id, 0, userID, 0, false);
					krn.setObject(obj.id, processDefAttr.id, 0, id, 0, false);
					krn.setTime(obj.id, timeAttr.id, 0, time, 0);
				}
			}
		} catch (KrnException e) {
			log.error(e, e);
		}
	}
}
