package kz.tamur;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import kz.tamur.or3ee.server.session.SessionOpsOperations;
/*
import org.jboss.ejb.client.ContextSelector;
import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;
*/
public class JBossEAPAuthenticator implements Or3Authenticator {
	public SessionOpsOperations authenticate(String host, int port, String earName, boolean remote) throws NamingException {
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
		props.put(Context.PROVIDER_URL, "remote://" + host + ":" + port);
		props.put(Context.SECURITY_PRINCIPAL, "user");
		props.put(Context.SECURITY_CREDENTIALS, "vhhwkwm8yd@");

		//Properties clientProp = new Properties();
		props.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
		props.put("remote.connectionprovider.create.options.org.xnio.Options.KEEP_ALIVE", "true");
		props.put("remote.connections", "default");
		props.put("remote.connection.default.port", String.valueOf(port));
		props.put("remote.connection.default.host", host);
		props.put("remote.connection.default.username", "user");
		props.put("remote.connection.default.password", "vhhwkwm8yd@");
		props.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");
		/* 
		EJBClientConfiguration cc = new PropertiesBasedEJBClientConfiguration(clientProp);
		ContextSelector<EJBClientContext> selector = new ConfigBasedEJBClientContextSelector(cc);
		EJBClientContext.setSelector(selector);
		 */
		Context ic = new InitialContext(props);
		Object obj = ic.lookup(earName + "/Or3EJB/SessionOps!kz.tamur.or3ee.server.session.SessionOps" + (remote ? "Remote" : "Local"));
    	ic.close();
		return (SessionOpsOperations)obj;
	}
}