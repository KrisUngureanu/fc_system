package kz.tamur;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import kz.tamur.or3ee.server.session.SessionOpsOperations;

public class WeblogicAuthenticator implements Or3Authenticator {
	public SessionOpsOperations authenticate(String host,
			int port, String earName, boolean remote) throws NamingException {

		if (remote) {
			Hashtable<String, String> env = new Hashtable<String, String>();
	
			env.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
			env.put(Context.PROVIDER_URL, "t3://" + host + ":" + port);
	
			InitialContext ctx = new InitialContext(env);
			Object obj = ctx.lookup("SessionOps#kz.tamur.or3ee.server.session.SessionOpsRemote");
			ctx.close();
			return (SessionOpsOperations) obj;
		} else {
			InitialContext ctx = new InitialContext();
			Object obj = ctx.lookup("SessionOps#kz.tamur.or3ee.server.session.SessionOpsLocal");
			ctx.close();
			return (SessionOpsOperations) obj;
		}
	}
}
