package kz.tamur.or3ee.server.admin;

import javax.ejb.Local;

@Local
public interface TransactionWatchDogLocal {
	public void start(long checkInterval, long transactionTimeout);
}
