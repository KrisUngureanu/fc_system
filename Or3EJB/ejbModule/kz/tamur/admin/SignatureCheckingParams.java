package kz.tamur.admin;

import kz.tamur.comps.Constants;
import kz.tamur.or3ee.server.kit.SrvUtils;

import java.util.HashMap;
import java.util.Map;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.Session;

public class SignatureCheckingParams {
    private static KrnClass reestrCls;
    private static KrnAttribute codeAttr;
    private static KrnAttribute serviceUsersAttr;
    
    private static KrnClass serviceUsersCls;
    private static KrnAttribute senderIdAttr;
    private static KrnAttribute checkingTypeAttr;
    private static KrnAttribute signingTypeAttr;
    
    private static boolean init = false;
    private static String dsName = "";
    
    private static Map<String, Map<String, Integer>> checkingTypeCache;
    private static Map<String, Integer> signingTypeCache;
    
    public static void init(Session session) {
        try {
        	checkingTypeCache = new HashMap<>();
        	signingTypeCache = new HashMap<>();
        	
            dsName = session.getDsName();
            reestrCls = session.getClassByName("Реестр электронных сервисов");
            codeAttr = session.getAttributeByName(reestrCls, "код");
            serviceUsersAttr = session.getAttributeByName(reestrCls, "зап табл получателей сервиса");
            signingTypeAttr = session.getAttributeByName(reestrCls, "тип подписи бизнес-данных");
            
            if (Constants.IS_FC_PROJECT) {
            	serviceUsersCls = session.getClassByName("уд::осн::Зап_табл_получателей_сервиса");
            } else {
            	serviceUsersCls = session.getClassByName("Зап табл получателей сервиса");
            }
            
            senderIdAttr = session.getAttributeByName(serviceUsersCls, "senderId");
            checkingTypeAttr = session.getAttributeByName(serviceUsersCls, "тип проверки ЭЦП бизнес-данных");
            init = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static int getCheckingType(String serviceId, String senderId) {
        int checkingType = -1;
        if (serviceId == null || serviceId.trim().length() == 0 || senderId == null || senderId.trim().length() == 0 || !init) {
            return checkingType;
        }
        
        synchronized (checkingTypeCache) {
        	Map<String, Integer> map = checkingTypeCache.get(serviceId);
        	if (map != null) {
        		Integer value = map.get(senderId);
        		if (value != null) {
        			return value;
        		}
        	}
		}
        
        Session session = null;
        try {
            session = SrvUtils.getSession(dsName, "sys", null);
            KrnObject[] objects = session.getObjectsByAttribute(reestrCls.id, codeAttr.id, 0, 0, serviceId, 0);
            if (objects.length > 0) {
                KrnObject[] serviceUsers = session.getObjects(objects[0].id, serviceUsersAttr.id, new long[0], 0);
                for (int i = 0; i < serviceUsers.length; i++) {
                    if (senderId.equals(session.getStringsSingular(serviceUsers[i].id, senderIdAttr.id, 0, false, false))) {
                        checkingType = (int) session.getLongsSingular(serviceUsers[i], checkingTypeAttr, false);
                        break;
                    }
                }
            }

            synchronized (checkingTypeCache) {
            	Map<String, Integer> map = checkingTypeCache.get(serviceId);
            	if (map == null) {
            		map = new HashMap<>();
            		checkingTypeCache.put(serviceId, map);
            	}
            	map.put(senderId, checkingType);
    		}

            return checkingType;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.release();
            }
        }
        return checkingType;
    }
    
    public static int getSigningType(String serviceId) {
        int signingType = -1;
        if (serviceId == null || serviceId.trim().length() == 0 || !init) {
            return signingType;
        }
        
        synchronized (signingTypeCache) {
            Integer value = signingTypeCache.get(serviceId);
            if (value != null) {
            	return value;
            }
        }

        Session session = null;
        try {
            session = SrvUtils.getSession(dsName, "sys", null);
            KrnObject[] objects = session.getObjectsByAttribute(reestrCls.id, codeAttr.id, 0, 0, serviceId, 0);
            if (objects.length > 0) {
            	long[] vals = session.getLongs(objects[0].id, signingTypeAttr.id, 0);
            	if (vals.length > 0) {
            		signingType = (int) vals[0];
            	}
            }
            
            synchronized (signingTypeCache) {
            	signingTypeCache.put(serviceId, signingType);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.release();
            }
        }
        return signingType;
    }
}