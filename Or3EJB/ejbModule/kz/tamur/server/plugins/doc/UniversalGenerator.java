package kz.tamur.server.plugins.doc;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.LongValue;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

import kz.tamur.ods.ComparisonOperations;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.util.Funcs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UniversalGenerator implements SrvPlugin {

	private Session session;
    private static KrnClass numerator;
    private static KrnAttribute numTypeAttr;
    private static KrnAttribute numPeriodAttr;
    private static KrnAttribute numLastPeriodAttr;
    private static KrnAttribute numLastNumberAttr;
    private static KrnAttribute numOldNumberAttr;

    public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public KrnObject saveNumber(String className, String attrName, String kadastrNumber) throws Exception {
		KrnObject obj = null; 
		synchronized(UniversalGenerator.class) {
            Session s = SrvUtils.getSession(session.getDsName(), "sys", null);
            try {
	            KrnClass kadastrNumerator = s.getClassByName(className);
	            KrnAttribute numberAttr = s.getAttributeByName(kadastrNumerator, attrName);
	            
	            KrnObject[] objs = s.getObjectsByAttribute(kadastrNumerator.id, numberAttr.id, 0, ComparisonOperations.CO_EQUALS, kadastrNumber, 0);
	
	            if (objs != null && objs.length > 0) return null;
	            
	            obj = s.createObject(kadastrNumerator, 0);
	
	            s.setString(obj.id, numberAttr.id, 0, 0, false, kadastrNumber, 0);
	
	            s.commitTransaction();
	        } finally {
	            s.release();
	        }
            return obj;
		}
	}

	public long getNextNumber(String docTypeUid, Number period, Number initNumber) throws Exception {
		synchronized(UniversalGenerator.class) {
			init();
            if (numerator == null) return -1;
            long lastNumInPeriod;

            Session s = SrvUtils.getSession(session.getDsName(), "sys", null);
            try {
            	String prefix = s.getDriver().getPrefixForQuery();
                s.runSql("UPDATE " + prefix + "t_ids SET c_last_id = c_last_id WHERE c_name = 'installed'", true);
            	
                KrnObject[] objs1 = s.getObjectsByAttribute(numerator.id,
                        numTypeAttr.id, 0, ComparisonOperations.CO_EQUALS, docTypeUid, 0);
                KrnObject[] objs2 = s.getObjectsByAttribute(numerator.id,
                        numPeriodAttr.id, 0, ComparisonOperations.CO_EQUALS, period, 0);

                KrnObject obj = null;
                for (KrnObject obj1 : objs1) {
                    for (KrnObject obj2 : objs2) {
                        if (obj1.id == obj2.id) {
                            obj = obj1;
                        }
                    }
                }

                Calendar c = Calendar.getInstance();
                long curPeriod = 0;
                switch (period.intValue()) {
                    case 0:
                        curPeriod = c.get(Calendar.DAY_OF_MONTH) + c.get(Calendar.MONTH) * 100 + c.get(Calendar.YEAR) * 10000;
                        break;
                    case 1:
                        curPeriod = c.get(Calendar.MONTH) + c.get(Calendar.YEAR) * 100;
                        break;
                    case 2:
                        curPeriod = c.get(Calendar.YEAR);
                        break;
                    case 3:
                        curPeriod = 0;
                        break;
                }

                if (obj == null) {
                    lastNumInPeriod = initNumber.longValue();
                    obj = s.createObject(numerator, 0);
                    s.setString(obj.id, numTypeAttr.id, 0, 0, false, docTypeUid, 0);
                    s.setLong(obj.id, numPeriodAttr.id, 0, period.longValue(), 0);
                    s.setLong(obj.id, numLastPeriodAttr.id, 0, curPeriod, 0);
                    s.setLong(obj.id, numLastNumberAttr.id, 0, initNumber.longValue(), 0);
                } else {

                    long[] objIds = {obj.id};

                    LongValue[] lvs = s.getLongValues(objIds, numLastNumberAttr.id, 0);
                    lastNumInPeriod = Funcs.find(obj, 0, lvs);

                    lvs = s.getLongValues(objIds, numLastPeriodAttr.id, 0);
                    long lastPeriod = Funcs.find(obj, 0, lvs);

                    if (lastPeriod != curPeriod) {
                        lastNumInPeriod = 0;
                        lastPeriod = curPeriod;
                        s.setLong(obj.id, numLastPeriodAttr.id, 0, lastPeriod, 0);
                        long[] ls = s.getLongs(obj.id, numOldNumberAttr.id, 0);
                        if (ls != null && ls.length > 0) {
                            List<Object> values = new ArrayList<Object>();
                            for (long l : ls) values.add(l);

                            s.deleteValue(obj.id, numOldNumberAttr.id, values, 0);
                        }
                    }

                    lastNumInPeriod++;
                    s.setLong(obj.id, numLastNumberAttr.id, 0, lastNumInPeriod, 0);
                }
            	
                s.commitTransaction();
            } finally {
                s.release();
            }
			return lastNumInPeriod;
		}
	}

	public long setNumber(String docTypeUid, Number period, Number initNumber) throws Exception {
		synchronized(UniversalGenerator.class) {
			init();
            if (numerator == null) return -1;

            Session s = SrvUtils.getSession(session.getDsName(), "sys", null);
            try {
            	String prefix = s.getDriver().getPrefixForQuery();
                s.runSql("UPDATE " + prefix + "t_ids SET c_last_id = c_last_id WHERE c_name = 'installed'", true);
            	
            	KrnObject[] objs1 = s.getObjectsByAttribute(numerator.id,
	                    numTypeAttr.id, 0, ComparisonOperations.CO_EQUALS, docTypeUid, 0);
	            KrnObject[] objs2 = s.getObjectsByAttribute(numerator.id,
	                    numPeriodAttr.id, 0, ComparisonOperations.CO_EQUALS, period, 0);
	
	            KrnObject obj = null;
	            for (KrnObject obj1 : objs1) {
	                for (KrnObject obj2 : objs2) {
	                    if (obj1.id == obj2.id) {
	                        obj = obj1;
	                    }
	                }
	            }
	
	            Calendar c = Calendar.getInstance();
	            long curPeriod = 0;
	            switch (period.intValue()) {
	                case 0:
	                    curPeriod = c.get(Calendar.DAY_OF_MONTH) + c.get(Calendar.MONTH) * 100 + c.get(Calendar.YEAR) * 10000;
	                    break;
	                case 1:
	                    curPeriod = c.get(Calendar.MONTH) + c.get(Calendar.YEAR) * 100;
	                    break;
	                case 2:
	                    curPeriod = c.get(Calendar.YEAR);
	                    break;
	                case 3:
	                    curPeriod = 0;
	                    break;
	            }
	
	            if (obj == null) {
	                obj = s.createObject(numerator, 0);
	                s.setString(obj.id, numTypeAttr.id, 0, 0, false, docTypeUid, 0);
	                s.setLong(obj.id, numPeriodAttr.id, 0, period.longValue(), 0);
	                s.setLong(obj.id, numLastPeriodAttr.id, 0, curPeriod, 0);
	                s.setLong(obj.id, numLastNumberAttr.id, 0, initNumber.longValue(), 0);
	            } else {
	                s.setLong(obj.id, numLastNumberAttr.id, 0, initNumber.longValue(), 0);
	            }
                s.commitTransaction();
            } finally {
                s.release();
            }
	            
			return initNumber.longValue();
		}
	}

	public boolean rejectNumber(String docTypeUid, Number period, Number number, KrnDate date) throws Exception {
        synchronized(UniversalGenerator.class) {
            init();
            if (numerator == null) return false;
            boolean res = false;

            Session s = SrvUtils.getSession(session.getDsName(), "sys", null);
            try {
            	String prefix = s.getDriver().getPrefixForQuery();
                s.runSql("UPDATE " + prefix + "t_ids SET c_last_id = c_last_id WHERE c_name = 'installed'", true);

            	KrnObject[] objs1 = s.getObjectsByAttribute(numerator.id,
	                    numTypeAttr.id, 0, ComparisonOperations.CO_EQUALS, docTypeUid, 0);
	            KrnObject[] objs2 = s.getObjectsByAttribute(numerator.id,
	                    numPeriodAttr.id, 0, ComparisonOperations.CO_EQUALS, period, 0);
	
	            KrnObject obj = null;
	            for (KrnObject obj1 : objs1) {
	                for (KrnObject obj2 : objs2) {
	                    if (obj1.id == obj2.id) {
	                        obj = obj1;
	                    }
	                }
	            }
	
	            if (obj == null) return false;
	
	            long[] objIds = {obj.id};
	
	            LongValue[] lvs = s.getLongValues(objIds, numLastPeriodAttr.id, 0);
	            long lastPeriod = Funcs.find(obj, 0, lvs);
	
	    		Calendar c = Calendar.getInstance();
				long curPeriod = 0;
	            switch (period.intValue()) {
	                case 0:
	                    curPeriod = c.get(Calendar.DAY_OF_MONTH) + c.get(Calendar.MONTH) * 100 + c.get(Calendar.YEAR) * 10000;
	                    break;
	                case 1:
	                    curPeriod = c.get(Calendar.MONTH) + c.get(Calendar.YEAR) * 100;
	                    break;
	                case 2:
	                    curPeriod = c.get(Calendar.YEAR);
	                    break;
	                case 3:
	                    curPeriod = 0;
	                    break;
	            }
	
	            c.setTime(date);
	            long rejectPeriod = 0;
	            switch (period.intValue()) {
	                case 0:
	                    rejectPeriod = c.get(Calendar.DAY_OF_MONTH) + c.get(Calendar.MONTH) * 100 + c.get(Calendar.YEAR) * 10000;
	                    break;
	                case 1:
	                    rejectPeriod = c.get(Calendar.MONTH) + c.get(Calendar.YEAR) * 100;
	                    break;
	                case 2:
	                    rejectPeriod = c.get(Calendar.YEAR);
	                    break;
	                case 3:
	                    rejectPeriod = 0;
	                    break;
	            }
	
	            if (lastPeriod != curPeriod) {
	                lastPeriod = curPeriod;
	                s.setLong(obj.id, numLastPeriodAttr.id, 0, lastPeriod, 0);
	                s.setLong(obj.id, numLastNumberAttr.id, 0, 0, 0);
	                long[] ls = s.getLongs(obj.id, numOldNumberAttr.id, 0);
	                if (ls != null && ls.length > 0) {
	                    List<Object> values = new ArrayList<Object>();
	                    for (long l : ls) values.add(l);
	
	                    s.deleteValue(obj.id, numOldNumberAttr.id, values, 0);
	                }
	            }
	
	            if (rejectPeriod == curPeriod) {
	                s.setLong(obj.id, numOldNumberAttr.id, 0, number.longValue(), 0);
	                res = true;
	            }
	            s.commitTransaction();
	        } finally {
	            s.release();
	        }

            return res;
        }
    }

    public long getOldNumber(String docTypeUid, Number period) throws Exception {
        synchronized(UniversalGenerator.class) {
            init();
            long res = -1;
            if (numerator == null) return res;

            Session s = SrvUtils.getSession(session.getDsName(), "sys", null);
            try {
            	String prefix = s.getDriver().getPrefixForQuery();
                s.runSql("UPDATE " + prefix + "t_ids SET c_last_id = c_last_id WHERE c_name = 'installed'", true);

	            KrnObject[] objs1 = s.getObjectsByAttribute(numerator.id,
	                    numTypeAttr.id, 0, ComparisonOperations.CO_EQUALS, docTypeUid, 0);
	            KrnObject[] objs2 = s.getObjectsByAttribute(numerator.id,
	                    numPeriodAttr.id, 0, ComparisonOperations.CO_EQUALS, period, 0);
	
	            KrnObject obj = null;
	            for (KrnObject obj1 : objs1) {
	                for (KrnObject obj2 : objs2) {
	                    if (obj1.id == obj2.id) {
	                        obj = obj1;
	                    }
	                }
	            }
	
	            if (obj == null) return res;
	
	            long[] objIds = {obj.id};
	
	            LongValue[] lvs = s.getLongValues(objIds, numLastPeriodAttr.id, 0);
	            long lastPeriod = Funcs.find(obj, 0, lvs);
	
	    		Calendar c = Calendar.getInstance();
				long curPeriod = 0;
	            switch (period.intValue()) {
	                case 0:
	                    curPeriod = c.get(Calendar.DAY_OF_MONTH) + c.get(Calendar.MONTH) * 100 + c.get(Calendar.YEAR) * 10000;
	                    break;
	                case 1:
	                    curPeriod = c.get(Calendar.MONTH) + c.get(Calendar.YEAR) * 100;
	                    break;
	                case 2:
	                    curPeriod = c.get(Calendar.YEAR);
	                    break;
	                case 3:
	                    curPeriod = 0;
	                    break;
	            }
	
	            if (lastPeriod != curPeriod) {
	                lastPeriod = curPeriod;
	                s.setLong(obj.id, numLastPeriodAttr.id, 0, lastPeriod, 0);
	                s.setLong(obj.id, numLastNumberAttr.id, 0, 0, 0);
	                long[] ls = s.getLongs(obj.id, numOldNumberAttr.id, 0);
	                if (ls != null && ls.length > 0) {
	                    List<Object> values = new ArrayList<Object>();
	                    for (long l : ls) values.add(l);
	
	                    s.deleteValue(obj.id, numOldNumberAttr.id, values, 0);
	                }
	            }
	
	            lvs = s.getLongValues(objIds, numOldNumberAttr.id, 0);
	
	            if (lvs != null && lvs.length > 0) {
	                for (LongValue lv : lvs) {
	                    if (lv.objectId == obj.id && (res == -1 || lv.value < res)) {
	                        res = lv.value;
	                    }
	                }
	                List<Object> values = new ArrayList<Object>(1);
	                values.add(res);
	                s.deleteValue(obj.id, numOldNumberAttr.id, values, 0);
	            }
	            s.commitTransaction();
	        } finally {
	            s.release();
	        }
            return res;
        }
    }

    private void init() throws Exception {
		if (numerator == null) {
            try {
                numerator = session.getClassByName("Нумератор");
                numTypeAttr = session.getAttributeByName(numerator, "тип");
                numPeriodAttr = session.getAttributeByName(numerator, "период");
                numLastPeriodAttr = session.getAttributeByName(numerator, "последнее значение периода");
                numLastNumberAttr = session.getAttributeByName(numerator, "последнее значение нумератора");
                numOldNumberAttr = session.getAttributeByName(numerator, "свободные значения нумератора в текущем периоде");
            } catch (Exception e) {
                System.out.println("Не найден класс Нумератор");
                System.out.println("Не найден атрибут тип");
                System.out.println("Не найден атрибут период");
                System.out.println("Не найден атрибут последнее значение периода");
                System.out.println("Не найден атрибут последнее значение нумератора");
            }
        }
	}
}
