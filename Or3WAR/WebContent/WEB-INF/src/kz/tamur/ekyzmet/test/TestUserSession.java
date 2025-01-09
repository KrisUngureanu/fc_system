package kz.tamur.ekyzmet.test;

import java.util.UUID;

import kz.tamur.or3ee.common.UserSession;

import com.cifs.or2.server.ServerUserSession;

public class TestUserSession extends ServerUserSession {

	private UUID uid = UUID.randomUUID();
	
    public TestUserSession(String dsName) {
    	super(dsName, "test", null, null, null, UserSession.SERVER_ID, false);
	}
	
	@Override
	public UUID getId() {
		return uid;
	}

}
