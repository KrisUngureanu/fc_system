package kz.tamur.or3ee.server.lang;

import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MAX_LOGIN;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MAX_PASS;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MIN_LOGIN;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MIN_PASS;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_MIN_PASS_ADM;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_KEYBOARD;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_LOGIN;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_NAME;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_REP;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_REP_ANY_MORE_TWO;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_SURN;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_TEL;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NOT_WORD;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_ALL_NUMB;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_NUMB;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_REG;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_SPEC;
import static kz.tamur.common.ErrorCodes.PASS_VALID_PWD_NO_SYMB;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

import kz.tamur.or3.util.SystemEvent;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.common.lang.SystemWrp;
import kz.tamur.util.Funcs;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.ServerUserSession;
import com.cifs.or2.server.Session;

/**
 * The Class SrvSystemWrp.
 * 
 */
public class SrvSystemWrp extends SystemWrp implements Serializable {
	
	private Session session;

    @Override
    public String requestPassword() {
        return null;
    }

    
    @Override
    public int showConfirmMessage(Object value) {
        return 0;
    }

    
    @Override
    public void showErrorMessage(Object value) {
    }
    
    @Override
    public void showInfoMessage(Object value) {
    }
    
    @Override
    public void showWarningMessage(Object value) {
    }

    
    @Override
    public void showMessage(Object value) {
    }

    public void setSession(Session session) {
    	this.session = session;
    }
    
	@Override
	public void log(SystemEvent event, String proc, String objName, String message) {
		UserSession us = session.getUserSession();
		StringBuilder res = new StringBuilder(" | ").append(SystemEvent.TYPES[event.getTypeCode()]).append(" | ")
				.append(event.getName()).append(" | ").append(us.getUserName()).append(" | ").append(us.getIp()).append(" | ")
				.append(us.getComputer()).append(" | ").append(us.isAdmin() ? 1 : 0).append(" | ").append(us.getServerId())
				.append(" | ").append(proc).append(" | ").append(objName).append(" | ").append(message)
				.append(" | | "); // Добавляем пустые поля таблицы и колонки, иначе не будет писать в t_sys_log
		log.info(res.toString());
	}
	
    @Override
    public void writeLogRecord(String loggerName, String type, String event, String description) {
    	session.writeLogRecord(loggerName, type, event, description);
    }


	/* (non-Javadoc)
	 * @see kz.tamur.or3ee.common.lang.SystemWrp#findText(java.lang.String, int)
	 */
	@Override
	public List<String> findText(String text, int condition) {		
		List<String> uidResults = new ArrayList<String>();
		String inputString = Funcs.normalizeInput(text).trim();
		boolean[] searchArea = { true, false, false, false, false, false,
				false, false };
		int[] searchProperties = { 0, condition };			
		List<Object> results = session.search(inputString, 1000, searchProperties, searchArea);			
		List<String[]> objects = (List<String[]>) results.get(0);
		if (objects.size() > 0) {
			int count = Funcs.checkInt(objects.size(), 1000);		
			for (int i = 0; i < count; i++) {
				String[] object = objects.get(i);
				String res = (object[4].equals("class")? "object attr: " + object[1] : object[4]) + ", " + object[0];
				uidResults.add(res);								
				System.out.println(res);
			}
		} else {
			System.out.println("Objects not found!");
		}			
		return uidResults;
		
	}

	@Override
    public String checkPassAndLogin(String pd, String userName, boolean isAdmin, boolean chekLogin) {
        return checkPassAndLogin(pd.toCharArray(), userName, isAdmin, chekLogin, new Locale("ru"));
    }
    
    @Override
    public String checkPassAndLogin(String pd, String userName, boolean isAdmin, boolean chekLogin, KrnObject lang) {
        return checkPassAndLogin(pd.toCharArray(), userName, isAdmin, chekLogin, new Locale(lang != null && (lang.id == 2 || lang.id == 123) ? "kk" : "ru"));
    }
     
    public String checkPassAndLogin(char[] password, String userName, boolean isAdmin, boolean chekLogin, Locale loc) {
        final ResourceBundle res = ResourceBundle.getBundle("kz.tamur.rt.RuntimeResourcesSrv", loc);
        try {
        	session.getPolicy().checkPassAndLogin(password, userName, isAdmin, chekLogin);
        } catch (KrnException e) {
            String mess = "";
            switch (e.code) {
	            case PASS_VALID_PWD_MIN_LOGIN:
	                mess = res.getString("validPwdMinLogin");
	                mess = mess.replaceFirst("X", e.getMessage());
	                break;
	            case PASS_VALID_PWD_MAX_LOGIN:
	                mess = res.getString("validPwdMaxLogin");
	                mess = mess.replaceFirst("X", e.getMessage());
	                break;
	            case PASS_VALID_PWD_MIN_PASS:
	                mess = res.getString("validPwdmMinPass");
	                mess = mess.replaceFirst("X", e.getMessage());
	                break;
	            case PASS_VALID_PWD_MIN_PASS_ADM:
	                mess = res.getString("validPwdMinPassAdm");
	                mess = mess.replaceFirst("X", e.getMessage());
	                break;
	            case PASS_VALID_PWD_MAX_PASS:
	                mess = res.getString("validPwdmMaxPass");
	                mess = mess.replaceFirst("X", e.getMessage());
	                break;
	            case PASS_VALID_PWD_NO_NUMB:
	                mess = res.getString("validPwdNoNumb");
	                break;
	            case PASS_VALID_PWD_NO_ALL_NUMB:
	                mess = res.getString("validPwdNoAllNumb");
	                break;
	            case PASS_VALID_PWD_NO_SYMB:
	                mess = res.getString("validPwdNoSymb");
	                break;
	            case PASS_VALID_PWD_NO_REG:
	                mess = res.getString("validPwdNoReg");
	                break;
	            case PASS_VALID_PWD_NO_SPEC:
	                mess = res.getString("validPwdNoSpec");
	                break;
	            case PASS_VALID_PWD_NOT_NAME:
	                mess = res.getString("validPwdNotName");
	                break;
	            case PASS_VALID_PWD_NOT_SURN:
	                mess = res.getString("validPwdNotSurn");
	                break;
	            case PASS_VALID_PWD_NOT_TEL:
	                mess = res.getString("validPwdNotTel");
	                break;
	            case PASS_VALID_PWD_NOT_WORD:
	                mess = res.getString("validPwdNotWord");
	                break;
	            case PASS_VALID_PWD_NOT_KEYBOARD:
	                mess = res.getString("validPwdNotKeyboard");
	                break;
	            case PASS_VALID_PWD_NOT_LOGIN:
	                mess = res.getString("validPwdNotLogin");
	                break;
	            case PASS_VALID_PWD_NOT_REP:
	                mess = res.getString("validPwdNotRep");
	                break;
	            case PASS_VALID_PWD_NOT_REP_ANY_MORE_TWO:
	                mess = res.getString("validPwdNotRepAnyMoreTwo");
	                break;
	            default:
	            	mess = e.getMessage();
            }
            return mess;
        }
        return null;
    }

    public void getout(KrnObject user) throws KrnException {
    	getout(user, null);
	}
    
	@Override
	public void getout(KrnObject user, String message) throws KrnException {
		session.getout(user, message);
	}

	@Override
	public void sendMessage(KrnObject user, String message) throws KrnException {
		session.sendMessage(user, message);
	}
	
	public Object login(String name, String path, String typeClient, String ip, String pcName, boolean force) {
		Session s = new Session();
    	try {
	    	UserSession us = s.login(session.getDsName(), name, typeClient, path, null, null, ip, pcName, true, force, true, false, null);
	    	return Session.createValueObject(us);
    	} catch (KrnException e) {
			log.error(e, e);
			return e.getMessage();
		} finally {
    		s.close();
    	}
	}
	
	public Object loginWithECP(String pkcs7, String sct, String typeClient, String ip, String pcName, boolean force) {
		Session s = new Session();
    	try {
	    	UserSession us = s.loginWithECP(session.getDsName(), pkcs7, sct, typeClient, ip, pcName, true);
	    	return Session.createValueObject(us);
    	} catch (KrnException e) {
			log.error(e, e);
			return e.getMessage();
		} finally {
    		s.close();
    	}
	}

	public ServerUserSession findUserSession(String uuid) {
		return Session.findUserSession(UUID.fromString(uuid));
	}

	public boolean release(String uuid) {
		try {
			session.killUserSessions(UUID.fromString(uuid), false);
		} catch (KrnException e) {
			log.error(e, e);
			return false;
		}
		return true;
	}
}