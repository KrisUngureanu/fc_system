package kz.tamur.server.login;

import java.security.Principal;

import com.cifs.or2.server.Session;

public class UserSession implements Principal {
	
	private Session session;
	private String name;
	
	public UserSession(Session session, String name) {
		super();
		this.session = session;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Session getSession() {
		return session;
	}
}
