package kz.tamur.rt.orlang;

import com.cifs.or2.client.Kernel;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnObject;

public class ClientUniversalGenerator {
	
	private Kernel krn;

	public ClientUniversalGenerator(Kernel krn) {
		this.krn = krn;
	}
	
	public long getNextNumber(String docTypeUid, Number period, Number initNumber) throws Exception {
		return krn.getNextGeneratedNumber(docTypeUid, period, initNumber);
	}

	public long setNumber(String docTypeUid, Number period, Number number) throws Exception {
		return krn.setLastGeneratedNumber(docTypeUid, period, number);
	}

	public boolean rejectNumber(String docTypeUid, Number period, Number number, KrnDate date) throws Exception {
    	return krn.rejectGeneratedNumber(docTypeUid, period, number, date);
    }

    public long getOldNumber(String docTypeUid, Number period) throws Exception {
    	return krn.getOldGeneratedNumber(docTypeUid, period);
    }

	public KrnObject saveNumber(String className, String attrName, String kadastrNumber) throws Exception {
		return krn.saveNumber(className, attrName, kadastrNumber);
	}
}
