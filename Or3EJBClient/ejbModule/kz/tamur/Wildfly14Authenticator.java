package kz.tamur;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import kz.tamur.or3ee.server.session.SessionOpsOperations;

public class Wildfly14Authenticator implements Or3Authenticator {
	public SessionOpsOperations authenticate(String host, int port, String earName, final boolean remote) throws NamingException {
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
		props.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		props.put(Context.PROVIDER_URL, "remote+http://" + host + ":" + port);
		props.put(Context.SECURITY_PRINCIPAL, "user1");
		props.put(Context.SECURITY_CREDENTIALS, "123456");
		
		props.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");
		props.put("jboss.naming.client.connect.options.org.xnio.Options.SASL_DISALLOWED_MECHANISMS", "JBOSS-LOCAL-USER");
		props.put("jboss.naming.client.connect.options.org.xnio.Options.SSL_ENABLED", "false"); 
		
		props.put("jboss.naming.client.connect.timeout", 
				System.getProperty("client.connect.timeout") != null ? System.getProperty("client.connect.timeout") : "20000"); 

		props.put("remote.clusters", "ejb");
		props.put("remote.cluster.ejb.username", "user1");
		props.put("remote.cluster.ejb.password", "123456");
		props.put("remote.cluster.ejb.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");  
		props.put("remote.cluster.ejb.connect.options.org.xnio.Options.SSL_ENABLED", "false"); 
		props.put("remote.cluster.ejb.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");
		props.put("remote.cluster.ejb.connect.options.org.xnio.Options.SASL_DISALLOWED_MECHANISMS", "JBOSS-LOCAL-USER");
			
		props.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
		props.put("remote.connectionprovider.create.options.org.xnio.Options.KEEP_ALIVE", "true");
		props.put("remote.connections", "default");
		props.put("remote.connection.default.port", String.valueOf(port));
		props.put("remote.connection.default.host", host);
		props.put("remote.connection.default.username", "user1");
		props.put("remote.connection.default.password", "123456");
		props.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");
		props.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOPLAINTEXT", "false");
		props.put("remote.connection.default.connect.options.org.xnio.Options.SASL_DISALLOWED_MECHANISMS", "JBOSS-LOCAL-USER");

		Context ic = new InitialContext(props);

		try {
			String lookupName = earName + "/Or3EJB/SessionOps!kz.tamur.or3ee.server.session.SessionOps" + (remote ? "Remote" : "Local");
			System.out.println("searching: " + lookupName);
			Object obj = ic.lookup(lookupName);
			ic.close();
			return (SessionOpsOperations)obj;
		} catch (Throwable e) {
			e.printStackTrace();
			String lookupName = earName + "/Or3EJB-1.0-SNAPSHOT/SessionOps!kz.tamur.or3ee.server.session.SessionOps" + (remote ? "Remote" : "Local");
			System.out.println("searching: " + lookupName);
			Object obj = ic.lookup(lookupName);
			ic.close();
			return (SessionOpsOperations)obj;
		}
	}
}
