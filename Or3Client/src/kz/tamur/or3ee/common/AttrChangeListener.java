package kz.tamur.or3ee.common;

import java.util.UUID;

import com.cifs.or2.kernel.KrnObject;

public interface AttrChangeListener {
	public void attrChanged(KrnObject obj, long attrId, long langId, long trId, UUID uuid);
	public void commit(UUID uuid);
	public void rollback(UUID uuid);
	
	public void commitLongTransaction(UUID uuid, long trId);
	public void rollbackLongTransaction(UUID uuid, long trId);
}
