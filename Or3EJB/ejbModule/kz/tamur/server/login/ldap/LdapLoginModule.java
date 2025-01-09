package kz.tamur.server.login.ldap;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import com.cifs.or2.server.SessionManager;

import kz.tamur.server.login.Or3LoginContextCallback;

public class LdapLoginModule implements LoginModule {

	private SessionManager mgr;
	private String name;
	private String pd;
	private String ip;
	private String computer;

	private boolean verification;

	private DirContext ctx;

	private CallbackHandler callbackHandler;
	private Subject subject;

	private Properties options;


	public LdapLoginModule() {
	}

	public boolean abort() throws LoginException {
		return false;
	}

	public boolean commit() throws LoginException {
/*		try {
	        pd = PasswordService.getInstance().encrypt(pd);
			Session session = mgr.getImplSession2(name, pd, ip, computer, client, false);
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
		
		NameCallback ncb = new NameCallback("Пользователь");
		PasswordCallback pcb = new PasswordCallback("Пароль", true);
		Or3LoginContextCallback lcb = new Or3LoginContextCallback();

		if (callbackHandler == null)
			throw new LoginException("callback is null");
		try {
			callbackHandler.handle(new Callback[] {ncb, pcb, lcb});
		} catch (IOException e) {

			throw new LoginException(e.toString());
		} catch (UnsupportedCallbackException e) {

			throw new LoginException(
				e.toString()
						+ "callbackHandler does not support name or password callback");
		}

		name = ncb.getName();
		if (name.equals(null))
			throw new LoginException("name must not be null");
		pd = String.valueOf(pcb.getPassword());
		if (pd.equals(null))
			throw new LoginException("password must not be null");
		try {
			Hashtable props = new Hashtable();
			props.put(Context.INITIAL_CONTEXT_FACTORY, options
				.getProperty(Context.INITIAL_CONTEXT_FACTORY));
			props.put(Context.PROVIDER_URL, options
				.getProperty(Context.PROVIDER_URL));
			props.put(Context.SECURITY_PRINCIPAL, name);
			props.put(Context.SECURITY_CREDENTIALS, pd);
			props.put(Context.SECURITY_AUTHENTICATION, "simple");
			ctx = new InitialDirContext(props);
			verification = true;
			ctx.close();
		} catch (NamingException e) {
			throw new LoginException(e.toString() + "  " + e.getRootCause());
		}

		mgr = lcb.getMgr();
		
		ip = lcb.getIp();
		
		computer = lcb.getComputer();

		return verification;
	}

	public boolean logout() throws LoginException {
		return false;
	}

}
