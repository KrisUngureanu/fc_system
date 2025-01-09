package kz.tamur;

import javax.naming.NamingException;

import kz.tamur.or3ee.server.session.SessionOpsOperations;

public interface Or3Authenticator {
	public SessionOpsOperations authenticate(String host, int port, String earName, boolean remote) throws NamingException;
}
