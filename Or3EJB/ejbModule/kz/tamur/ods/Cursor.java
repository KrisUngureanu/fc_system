package kz.tamur.ods;

import java.util.List;

import kz.tamur.DriverException;

import com.cifs.or2.kernel.KrnObject;

public interface Cursor {
	
	int getSize();
	
	List<KrnObject> get(int offs, int count) throws DriverException;
	
	void release();
}
