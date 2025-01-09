package kz.tamur;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/*
import org.jboss.ejb.client.ContextSelector;
import org.jboss.ejb.client.EJBClientConfiguration;
import org.jboss.ejb.client.EJBClientContext;
import org.jboss.ejb.client.PropertiesBasedEJBClientConfiguration;
import org.jboss.ejb.client.remoting.ConfigBasedEJBClientContextSelector;
*/

import com.cifs.or2.kernel.KrnException;

import kz.tamur.common.ErrorCodes;
import kz.tamur.or3ee.server.session.SessionOpsOperations;

public class WildflyClusterAuthenticator {
	private String connectedHost = null;
	private String connectedPort = null;
	
	public SessionOpsOperations authenticate(String hosts, String ports, String earName, boolean remote) throws NamingException, KrnException {
        boolean blockedSelections = false;
        
		String[] hostArr = hosts.split(",");
		String[] portArr = ports.split(",");
     
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
		props.put(Context.SECURITY_PRINCIPAL, "user1");
		props.put(Context.SECURITY_CREDENTIALS, "123456");

		//Properties clientProp = new Properties();

		props.put("remote.clusters", "ejb");
		props.put("remote.cluster.ejb.username", "user1");
		props.put("remote.cluster.ejb.password", "123456");
		props.put("remote.cluster.ejb.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");  
		props.put("remote.cluster.ejb.connect.options.org.xnio.Options.SSL_ENABLED", "false"); 
				
		props.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
		props.put("remote.connectionprovider.create.options.org.xnio.Options.KEEP_ALIVE", "true");
		props.put("remote.connections", "default");
		props.put("remote.connection.default.username", "user1");
		props.put("remote.connection.default.password", "123456");
		props.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");

	    // Соединяемся с первым случайным доступным хостом
        List<Integer> selections = new ArrayList<Integer>(hostArr.length);
        for (int i = 0; i < hostArr.length; i++) {
        	selections.add(i);
        }
        Collections.shuffle(selections);
                        
		for (int i = 0; i < selections.size(); i++) {
            final int randomSelection = selections.get(i);

    		props.put("remote.connection.default.port", String.valueOf(portArr[randomSelection]));
    		props.put("remote.connection.default.host", hostArr[randomSelection]);

    		//EJBClientConfiguration cc = new PropertiesBasedEJBClientConfiguration(clientProp);
    		//ContextSelector<EJBClientContext> selector = new ConfigBasedEJBClientContextSelector(cc);
    		//EJBClientContext.setSelector(selector);

    		props.put(Context.PROVIDER_URL, "http-remoting://" + hostArr[randomSelection] + ":" + portArr[randomSelection]);
    		System.out.println("connecting: " + hostArr[randomSelection] + ":" + portArr[randomSelection]);

    		Context ic = new InitialContext(props);

    		String lookupName = earName + "/Or3EJB/SessionOps!kz.tamur.or3ee.server.session.SessionOps" + (remote ? "Remote" : "Local");
    		System.out.println("searching: " + lookupName);

			try {
				Object obj = ic.lookup(lookupName);
				
				connectedHost = hostArr[randomSelection];
				connectedPort = portArr[randomSelection];
				
	    		return (SessionOpsOperations)obj;
			} catch (Exception e1) {
				System.out.println("Сервер не доступен:" + hostArr[randomSelection]);

	    		lookupName = earName + "/Or3EJB-1.0-SNAPSHOT/SessionOps!kz.tamur.or3ee.server.session.SessionOps" + (remote ? "Remote" : "Local");
	    		System.out.println("searching: " + lookupName);

				try {
					Object obj = ic.lookup(lookupName);
					
					connectedHost = hostArr[randomSelection];
					connectedPort = portArr[randomSelection];
					
		    		return (SessionOpsOperations)obj;
				} catch (Exception e2) {
					System.out.println("Сервер не доступен:" + hostArr[randomSelection]);
				} finally {
					ic.close();
				}
			} finally {
				ic.close();
			}
		}
		if (blockedSelections) {
			throw new KrnException(ErrorCodes.SERVER_BLOCKED, "Все доступные сервера заблокированы. Попробуйте зайти в программу позже или обратитесь к администратору.");
		} else {
			throw new KrnException(ErrorCodes.SERVER_NOT_AVAILABLE, "Все сервера не доступны.");
		}
	}

	public String getConnectedHost() {
		return connectedHost;
	}

	public String getConnectedPort() {
		return connectedPort;
	}
}
