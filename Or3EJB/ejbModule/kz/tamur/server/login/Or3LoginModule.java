package kz.tamur.server.login;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.cifs.or2.server.SessionManager;

public class Or3LoginModule implements LoginModule {
	
	private SessionManager mgr;
	private String name;
	private String pd;
	private String ip;
	private String computer;
	
	private CallbackHandler callbackHandler;
	private Subject subject;

	public boolean abort() throws LoginException {
		return false;
	}

	public boolean commit() throws LoginException {
/*		try {
            //boolean checkPasswd = true;
            //if (pd != null && pd.length() > 0)
	        //    pd = PasswordService.getInstance().encrypt(pd);
            //else
            //    checkPasswd = false;
            //System.out.println(name);
			//Session session = mgr.getImplSession(name, pd, ip, computer, client);
			//subject.getPrincipals().add(new UserSession(session, name));
		} catch (KrnException e) {
			throw new Or3LoginException(e.code, e.message);
		}
*/		return true;
	}

	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {
		
		this.callbackHandler = callbackHandler;
		this.subject = subject;
	}

	public boolean login() throws LoginException {

		NameCallback ncb = new NameCallback("Пользователь");
		PasswordCallback pcb = new PasswordCallback("Пароль", true);
		Or3LoginContextCallback lcb = new Or3LoginContextCallback();
		
		try {
			callbackHandler.handle(new Callback[] {ncb, pcb, lcb});
		} catch (Exception e) {
			throw new LoginException(e.getMessage());
		}
		
		name = ncb.getName();
		if (name == null || name.length() == 0) {
			throw new LoginException("Не задано имя пользователя");
		}
		
		pd = new String(pcb.getPassword());
		
		mgr = lcb.getMgr();
		
		ip = lcb.getIp();
		
		computer = lcb.getComputer();

		return true;
	}

	public boolean logout() throws LoginException {
		return false;
	}
}
