package kz.tamur.server.plugins.reginfo;

import java.util.Calendar;

import kz.tamur.util.Funcs;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.LongValue;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

public class Numbers implements SrvPlugin {
	
	private Session session;
	
	private static KrnAttribute periodAttr;
	private static KrnAttribute numberAttr;
	private static KrnObject cntObj;

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	public String getNumber(int index, int digitCount) throws Exception {
		return String.format("%0" + digitCount + "d", getNumber(index));
	}

	public long getNumber(int index) throws Exception {
		synchronized(Numbers.class) {
			init();
			long[] objIds = {cntObj.id};
			LongValue[] lvs = session.getLongValues(objIds, periodAttr.id, 0);
			long period = Funcs.find(cntObj, index, lvs);
			long number;
			int year = getCurrentYear();
			if (period != year) {
				period = year;
				number = 1;
				session.setLong(cntObj.id, periodAttr.id, index, period, 0);
				session.setLong(cntObj.id, numberAttr.id, index, number, 0);
			} else {
				lvs = session.getLongValues(objIds, numberAttr.id, 0);
				number = Funcs.find(cntObj, index, lvs) + 1;
				session.setLong(cntObj.id, numberAttr.id, index, number, 0);
			}
			session.commitTransaction();
			return number;
		}
	}
	
	private int getCurrentYear() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR);
	}
	
	private void init() throws Exception {
		if (cntObj == null) {
			KrnClass cls = session.getClassByName("Номера ОУ");
			periodAttr = session.getAttributeByName(cls, "период");
			numberAttr = session.getAttributeByName(cls, "значение");
			KrnObject[] objs = session.getClassObjects(cls, new long[0], 0);
			if (objs.length == 0) {
				cntObj = session.createObject(cls, 0);
				session.commitTransaction();
			} else {
				cntObj = objs[0];
			}
		}
	}
}
