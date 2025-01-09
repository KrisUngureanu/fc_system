package kz.tamur.rt.orlang;

import kz.tamur.SecurityContextHolder;

import com.cifs.or2.kernel.KrnDate;

public class ClientUniversalGenerator {
	
	public ClientUniversalGenerator() {
	}
	
	public long getNextNumber(String docTypeUid, Number period, Number initNumber) throws Exception {
		return SecurityContextHolder.getKernel().getNextGeneratedNumber(docTypeUid, period, initNumber);
	}

	public long setNumber(String docTypeUid, Number period, Number number) throws Exception {
		return SecurityContextHolder.getKernel().setLastGeneratedNumber(docTypeUid, period, number);
	}

	public boolean rejectNumber(String docTypeUid, Number period, Number number, KrnDate date) throws Exception {
    	return SecurityContextHolder.getKernel().rejectGeneratedNumber(docTypeUid, period, number, date);
    }

    public long getOldNumber(String docTypeUid, Number period) throws Exception {
    	return SecurityContextHolder.getKernel().getOldGeneratedNumber(docTypeUid, period);
    }
}
