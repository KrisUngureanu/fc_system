package kz.tamur.server.plugins.afn;

import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;

import kz.tamur.util.Funcs;

import com.cifs.or2.kernel.*;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

public class AfnGenerator implements SrvPlugin {

	private Session session;

	private static KrnAttribute numberAttr;
    private static KrnAttribute numberMagAttr;
    private static KrnAttribute oldStocksAttr;    //
    private static KrnAttribute oldBondsAttr;
    private static KrnAttribute oldSharesAttr;
    private static KrnAttribute oldKDRsAttr;
    private static KrnAttribute oldNTPsAttr; // Пропущенные значени нового типа бумаг
	private static KrnObject cntObj;
    private static KrnAttribute numberMagYearAttr;
    private static KrnAttribute yearMagAttr;
    //private static long numberByYear = -1;
    //private static long year = -1;
    //private static long number = -1;

    public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public long getNewNumber(int index) throws Exception {
		synchronized(AfnGenerator.class) {
			init();
			long[] objIds = {cntObj.id};
			LongValue[] lvs = session.getLongValues(objIds, numberAttr.id, 0);
            long number = Funcs.find(cntObj, index, lvs);
            if (number == 99) {
                number = 0;
            } else if (number == 339999) {
                number = 100000;
            } else if (number >= 100000) {
                if (number%10000 == 9999) {
                    number++;
                }
            } else if (number == 3399) {
                number = 1000;
            } else if (number >= 1000 && number%100 == 99) {
                number++;
            }
            number++;
            session.setLong(cntObj.id, numberAttr.id, index, number, 0);
            session.commitTransaction();
			return number;
		}
	}

    public String getOldNumber(int index) throws Exception {
        synchronized(AfnGenerator.class) {
            init();
            String res = null;
            long[] objIds = {cntObj.id};
            StringValue[] svs = null;
            KrnAttribute attr = null;
            switch (index) {
                case 0:
                    attr = oldStocksAttr;
                    break;
                case 1:
                    attr = oldBondsAttr;
                    break;
                case 2:
                    attr = oldSharesAttr;
                    break;
                case 3:
                    attr = oldKDRsAttr;
                    break;
                case 4:
                    attr = oldNTPsAttr;
                    break;
            }

            svs = session.getStringValues(objIds, attr.id, 0, false, 0);

            if (svs != null && svs.length > 0) {
                for (int i = 0; i < svs.length; i++) {
                    StringValue sv = svs[i];
                    if (sv.objectId == cntObj.id && (res == null || sv.value.compareTo(res) < 0)) {
                        res = sv.value;
                    }
                }
                List<Object> values = new ArrayList<Object>(1);
                values.add(res);
                session.deleteValue(cntObj.id, attr.id, values, 0);
            }
            session.commitTransaction();
            return res;
        }
    }

    public void rejectNumber(int index, String number) throws Exception {
        synchronized(AfnGenerator.class) {
            init();
            KrnAttribute attr = null;
            switch (index) {
                case 0:
                    attr = oldStocksAttr;
                    break;
                case 1:
                    attr = oldBondsAttr;
                    break;
                case 2:
                    attr = oldSharesAttr;
                    break;
                case 3:
                    attr = oldKDRsAttr;
                    break;
                case 4:
                    attr = oldNTPsAttr;
                    break;
            }

            session.setString(cntObj.id, attr.id, 0, 0, false, number, 0);
            session.commitTransaction();
        }
    }

    public long getNewMagNumber(int index) throws Exception {
        synchronized(AfnGenerator.class) {
            init();
            long[] objIds = {cntObj.id};
            LongValue[] lvs = session.getLongValues(objIds, numberMagAttr.id, 0);
            long number = Funcs.find(cntObj, index, lvs);
            number++;
            session.setLong(cntObj.id, numberMagAttr.id, index, number, 0);
            session.commitTransaction();
            return number;
        }
    }

    public long getNewMagNumberByYear(int index) throws Exception {
        synchronized(AfnGenerator.class) {
            init();
            //if (numberByYear == -1) {
            long[] objIds = {cntObj.id};
            LongValue[] lvs = session.getLongValues(objIds, numberMagYearAttr.id, 0);
            long numberByYear = Funcs.find(cntObj, index, lvs);

            lvs = session.getLongValues(objIds, yearMagAttr.id, 0);
            long year = Funcs.find(cntObj, index, lvs);
            //}

    		Calendar c = Calendar.getInstance();
			long curYear = c.get(Calendar.YEAR);
            if (year != curYear) {
                numberByYear = 0;
                year = curYear;
                session.setLong(cntObj.id, yearMagAttr.id, index, year, 0);
            }

            numberByYear++;
            session.setLong(cntObj.id, numberMagYearAttr.id, index, numberByYear, 0);
            session.commitTransaction();
            return numberByYear;
        }
    }

	private void init() throws Exception {
		if (cntObj == null) {
			KrnClass cls = session.getClassByName("Номера выпусков ЭЦБ");
			numberAttr = session.getAttributeByName(cls, "последнее значение выпуска");
            numberMagAttr = session.getAttributeByName(cls, "последние номера журналов");
            oldStocksAttr = session.getAttributeByName(cls, "свободные номера выпуска акций");
            oldBondsAttr = session.getAttributeByName(cls, "свободные номера выпуска облигаций");
            oldSharesAttr = session.getAttributeByName(cls, "свободные номера выпуска паев");
            oldKDRsAttr = session.getAttributeByName(cls, "свободные номера выпуска КДР");

            oldNTPsAttr = session.getAttributeByName(cls, "свободные номера выпуска ИЦБ"); // Свободные номера нового типа бумаг

            numberMagYearAttr = session.getAttributeByName(cls, "последние номера журналов по годам");
            yearMagAttr = session.getAttributeByName(cls, "последний год для номеров журналов");

			KrnObject[] objs = session.getClassObjects(cls, new long[0], 0);
			if (objs.length == 0) {
				cntObj = session.createObject(cls, 0);
                session.setLong(cntObj.id, numberAttr.id, 0, 100000, 0);
                session.setLong(cntObj.id, numberAttr.id, 1, 1000, 0);
				session.commitTransaction();
			} else {
				cntObj = objs[0];
			}
		}
	}
}
