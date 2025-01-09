package kz.tamur.rt;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnObject;

public class ProcessFolderObject extends ProcessObject {

	public boolean isTab;
	private boolean isNull = false;

	public ProcessFolderObject(Object[] rec, long lang, Kernel krn) {
        langId = lang;
        obj = (KrnObject)rec[0];
        parentObj = (KrnObject) rec[2];
        index = (rec[3] instanceof Long) ? (Long)rec[3] : 0;
		isTab = (Boolean)rec[4];
		if (isTab) {
	        String tmp = (String)rec[7];
	        if (tmp == null || tmp.length() == 0)
	        	tmp = (String)rec[5];
	        
	        titleMap.put(krn.getLangIdByCode("RU"), tmp);

	        tmp = (String)rec[8];
	        if (tmp == null || tmp.length() == 0)
	        	tmp = (String)rec[6];
	        
	        titleMap.put(krn.getLangIdByCode("KZ"), tmp);

		} else {
	        titleMap.put(krn.getLangIdByCode("RU"), (String)rec[5]);
	        titleMap.put(krn.getLangIdByCode("KZ"), (String)rec[6]);
		}
	}

}