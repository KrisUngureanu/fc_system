package kz.tamur;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import kz.tamur.or3ee.server.session.SessionOpsOperations;

public class JBoss5Authenticator implements Or3Authenticator {
	public SessionOpsOperations authenticate(String host, int port, String earName, boolean remote) throws NamingException {
		Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
        props.put("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces");
        props.put("java.naming.provider.url", host + ":" + port);

		Context ic = new InitialContext(props);
		Object obj = remote 
				? ic.lookup("Or3EAR/SessionOps/remote")
				: ic.lookup("Or3EAR/SessionOps/local");
    	ic.close();
    	
		return (SessionOpsOperations)obj;
	}
}
