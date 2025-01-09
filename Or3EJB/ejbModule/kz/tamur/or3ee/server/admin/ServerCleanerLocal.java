package kz.tamur.or3ee.server.admin;

import javax.ejb.Local;

@Local
public interface ServerCleanerLocal {
	public void start();
}
