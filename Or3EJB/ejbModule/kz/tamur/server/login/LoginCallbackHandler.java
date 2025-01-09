package kz.tamur.server.login;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

import com.cifs.or2.server.SessionManager;

public class LoginCallbackHandler implements CallbackHandler {
	
	private String name;
	private String pd;
	private String ip;
	private String computer;
	private SessionManager mgr;
	
	public LoginCallbackHandler(String name, String pd, String ip, String computer, SessionManager mgr) {
		super();
		this.name = name;
		this.pd = pd;
		this.ip = ip;
		this.computer = computer;
		this.mgr = mgr;
	}

	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		
		for (Callback cb : callbacks) {
			if (cb instanceof NameCallback) {
				((NameCallback)cb).setName(name);
			} else if (cb instanceof PasswordCallback) {
				((PasswordCallback)cb).setPassword(pd.toCharArray());
			} else if (cb instanceof Or3LoginContextCallback) {
				Or3LoginContextCallback lcb = (Or3LoginContextCallback)cb;
				lcb.setMgr(mgr);
				lcb.setIp(ip);
				lcb.setComputer(computer);
			}
		}

	}

}
