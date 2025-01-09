package kz.tamur.server.login;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.cifs.or2.server.SessionManager;

public class WebSSOLoginModule implements LoginModule {

	private SessionManager mgr;
	private String name;
	private String ip;
	private String computer;

	private CallbackHandler callbackHandler;
	private Subject subject;

	private Properties options;


	public WebSSOLoginModule() {
	}

	public boolean abort() throws LoginException {
		return false;
	}

	public boolean commit() throws LoginException {
/*		try {
			Session session = mgr.getImplSession2(name, null, ip, computer, client, false);
			subject.getPrincipals().add(new UserSession(session, name));
		} catch (KrnException e) {
			e.printStackTrace();
			throw new Or3LoginException(e.code, e.message);
		}
*/		return true;
	}

	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {

		this.callbackHandler = callbackHandler;
		this.options = new Properties();
		this.options.putAll(options);
		this.subject = subject;
	}

	public boolean login() throws LoginException {

		NameCallback ncb = new NameCallback("LTPA токен");
		Or3LoginContextCallback lcb = new Or3LoginContextCallback();

		if (callbackHandler == null)
			throw new LoginException("callback is null");
		try {
			callbackHandler.handle(new Callback[] {ncb, lcb});
		} catch (IOException e) {

			throw new LoginException(e.toString());
		} catch (UnsupportedCallbackException e) {

			throw new LoginException(
				e.toString()
						+ "callbackHandler does not support name or password callback");
		}

		name = ncb.getName();

		if (name == null || name.length() == 0)
			throw new LoginException("Пользователь не авторизован.");

		mgr = lcb.getMgr();

		ip = lcb.getIp();

		computer = lcb.getComputer();

		return true;
	}

	public boolean logout() throws LoginException {
		return false;
	}
}