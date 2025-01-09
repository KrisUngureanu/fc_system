package com.cifs.or2.server.plugins;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;

import kz.tamur.lang.MathOp;
import kz.tamur.ods.ComparisonOperations;
import kz.tamur.or3ee.server.kit.SrvUtils;

import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

/**
 * Created by IntelliJ IDEA. User: Кайржан Date: 02.07.2005 Time: 11:54:28 To
 * change this template use File | Settings | File Templates.
 */
public class Generator implements SrvPlugin {
    Session s;

    // TODO Системные последовательности. (работа в кластере).
    private static KrnObject msgCounter;
    private static long msgIdAttr = -1;
    private static long msgDayAttr = -1;

	private static Map<Long, KrnObject> monthCounters;
	private static KrnClass monthCounterCls;
	private static KrnAttribute monthDateAttr;
	private static KrnAttribute monthCountAttr;

	/**
	 * Сгенерировать БИН
	 * @param date дата
	 * @param type тип
	 * @param detail 
	 * @return БИН
	 * @throws Exception
	 */
	public String makeBIN(KrnDate date, int type, int detail) throws Exception {
		return makeBIN(date, type, detail, -1);
	}
	
	/**
	 * Сгенерировать БИН
	 * @param date дата
	 * @param type тип
	 * @param detail 
	 * @param extra - седьмая цифра (для МФЦА передаем 9, по старому - передаем - -1) 
	 * @return БИН
	 * @throws Exception
	 */
	public String makeBIN(KrnDate date, int type, int detail, int extra) throws Exception {
		synchronized (Generator.class) {
			Session ses = null;
			try {
				ses = createSession();
				initBinCounters(ses);

                long monthKey = (date.getYear()%100) * 100 + date.getMonth();
                
                // Если для МФЦА, то к ключу добавляем 12
                if (extra > -1)
                	monthKey += 12;
                	
				// ген.-ем первый фасет (4 знака)
				int month = date.getMonth();
				int year = date.getYear();
				String dateStr = String.format("%04d", (year % 100) * 100 + month);
				// конец первого фасета
				int control_sum = 10;
				String bin = "";

            	String prefix = ses.getDriver().getPrefixForQuery();
                ses.runSql("UPDATE " + prefix + "t_ids SET c_last_id = c_last_id WHERE c_name = 'version'", true);

                long lastId = getNext(monthKey, ses);

				while (control_sum >= 10) {
					// добавляем нули вперед айди
					String ID = (extra > -1) ? extra + String.format("%04d", lastId)
											 : String.format("%05d", lastId);

					// собираем БИН
					bin = dateStr + type + detail + ID;
					int[] num_bin = getNumeric(bin);
					control_sum = getControlSum(num_bin);
					if (control_sum == 10)
						lastId = getNext(monthKey, ses);
				}
				ses.commitTransaction();

				return bin + control_sum;
			} finally {
				if (ses != null) {
					ses.release();
				}
			}
		}
	}
	/**
	 * Сгенерировать ИН
	 * @param date дата
	 * @param type 
	 * @param lastId
	 * @return ИН
	 * @throws Exception
	 */
    public String makeIN(KrnDate date, Number type, Number lastId) throws Exception {
        synchronized (Generator.class) {
            SimpleDateFormat fmt = new SimpleDateFormat("yyMMdd");
            String dateStr = fmt.format(date);
                int control_sum = 10;
                String iin = "";
                    // добавляем нули вперед айди
                    String ID = String.format("%04d", lastId);

                    // собираем ИН
                    iin = dateStr + type + ID;
                    int[] num_iin = getNumeric(iin);
                    control_sum = getControlSum(num_iin);
                    if (control_sum == 10)
                        return "";
                    else
                        return iin + control_sum;
        }
    }
    /**
     * Добавить нули
     * @param str строка
     * @param max макс. размер
     * @return новая строка, с нулями в конце
     */
	public String addZero(String str, int max) {
		int zero_count = max - str.length();
		String str_ID = "";
		for (int i = 0; i < zero_count; i++) {
			str_ID += "0";
		}
		return str_ID += str;
	}
	/**
	 * Сгенерировать MsgID
	 * @param max
	 * @return
	 * @throws Exception
	 */
	public String makeMsgID(int max) throws Exception {//TODO
		synchronized (Generator.class) {
			Session ses = null;
            long msgLastId = 0;
            long msgLastDay = 0;
			try {
				ses = createSession();
            	String prefix = ses.getDriver().getPrefixForQuery();
                ses.runSql("UPDATE " + prefix + "t_ids SET c_last_id = c_last_id WHERE c_name = 'version'", true);
                init(ses);

                if (msgCounter != null) {
                    long[] ls = ses.getLongs(msgCounter.id, msgIdAttr, 0);
                    msgLastId = ls.length > 0 ? ls[0] : 0;
                    ls = ses.getLongs(msgCounter.id, msgDayAttr, 0);
                    msgLastDay = ls.length > 0 ? ls[0] : 0;
                
                    Calendar c = Calendar.getInstance();
                    c.get(Calendar.YEAR);
                    c.get(Calendar.DAY_OF_YEAR);
                    
                    long day = c.get(Calendar.YEAR) * 10000 + 100 * (c.get(Calendar.MONTH) + 1) + c.get(Calendar.DAY_OF_MONTH);
                    if (day != msgLastDay) {
                        msgLastId = 0;
                        msgLastDay = day;
                        ses.setLong(msgCounter.id, msgDayAttr, 0, msgLastDay, 0);
                    }
                    
                    msgLastId++;
                    ses.setLong(msgCounter.id, msgIdAttr, 0, msgLastId, 0);
                    ses.commitTransaction();
                }
			} finally {
				if (ses != null) {
					ses.release();
				}
			}
			return String.format("%0" + max + "d", msgLastId);
		}
	}

	private int getControlSum(int[] a) {//TODO
		int number = a[0] + 2 * a[1] + 3 * a[2] + 4 * a[3] + 5 * a[4] + 6
				* a[5] + 7 * a[6] + 8 * a[7] + 9 * a[8] + 10 * a[9] + 11
				* a[10];
		number %= 11;
		// конец проверка А
		// если контрольное число равна 10 то идет проверка В
		// проверка В
		if (number == 10) {
			number = 3 * a[0] + 4 * a[1] + 5 * a[2] + 6 * a[3] + 7 * a[4] + 8
					* a[5] + 9 * a[6] + 10 * a[7] + 11 * a[8] + 1 * a[9] + 2
					* a[10];
			number %= 11;
		}
		// конец проверка В
		return number;
	}
	/**
	 * Преобразовать строковое значение в число
	 * @param code код в виде строки
	 * @return код в виде числа
	 */
	private int[] getNumeric(String code) {
		int a[] = new int[code.length()];
		for (int i = 0; i < code.length(); i++) {
			a[i] = Character.digit(code.charAt(i), 10);
		}
		return a;
	}

	public Session getSession() {
		return s;
	}

	public void setSession(Session session) {
		s = session;
	}
	/**
	 * Сгенерировать РНН
	 * @param type тип
	 * @param isFalse
	 * @return РНН
	 */
	public String makeRNN(int type, int isFalse) {
		String[] rnn_head = new String[]{"6202", "6004", "6003", "0314"};
		
		int i = MathOp.random(4).intValue();
		String head = rnn_head[i];
		if (type == 0)
			head += "0";
		else
			head += "1";

		int body = 0;
		while (Integer.toString(body).length() != 6)
			body = MathOp.random(1000000).intValue();
		int control = -1;
		while (control == -1)
			control = checkRNN(head + Integer.toString(body));
		if (isFalse == 1) {
			if (control == 9)
				control--;
			else
				control++;
		}
		String rnn = head + Integer.toString(body) + control;
		System.out.println("generated RNN = " + rnn);
		return rnn;
	}
	/**
	 * Проверяет валидность РНН	
	 * @param rnn РНН
	 * @return true - валидна, false - иначе
	 */
	private int checkRNN(String rnn) {
		int rnn_int = -1;
		boolean rightRNN = true;
		int coefficient = 10;
		int k = 0;
		while (coefficient >= 10) {
			k++;
			int sum = 0;
			if (k == 10) {
				rightRNN = false;
				break;
			}
			for (int i = 0, n = k; i < 11; i++, n++) {
				if (n > 10)
					n = 1;
				sum += Character.digit(rnn.charAt(i), 10) * n;
			}
			int r = sum / 11;
			coefficient = sum - r * 11;
		}
		if (rightRNN)
			rnn_int = coefficient;
		return rnn_int;
	}
	/**
	 * Сгенерировать ОКПО
	 * @param isFalse
	 * @return ОКПО
	 */
	public String makeOKPO(int isFalse) {
		int OKPO_sum = 11;
		int body = 0;
		while (OKPO_sum >= 10) {
			body = 0;
			while (Integer.toString(body).length() != 7)
				body = MathOp.random(10000000).intValue();
			OKPO_sum = OKPOSum(Integer.toString(body));
		}
		return "" + body + "" + OKPO_sum;
	}
	/**
	 * Преобразует строковое значение в число
	 * @param okpo ОКПО в виде строки
	 * @return ОКПО в виде числа
	 */
	private int OKPOSum(String okpo) {
		int sum = 0;
		for (int i = 0; i < 7; i++) {
			sum += Character.digit(okpo.charAt(i), 10) * (i + 1);
		}
		int r = sum / 11;
		int OKPO_sum = sum - r * 11;
		return OKPO_sum;
	}

	private Session createSession() throws KrnException {
		Session ses = SrvUtils.getSession(s.getUserSession());
		return ses;
	}
	
    private void initBinCounters(Session s) {
        if (monthCounters == null) {
            try {
                monthCounterCls = s.getClassByName("MonthBinCounter");
                monthCountAttr = s.getAttributeByName(monthCounterCls, "count");
                monthDateAttr = s.getAttributeByName(monthCounterCls, "date");
                monthCounters = new HashMap<Long, KrnObject>();
            } catch (Exception e) {
                System.out.println("Не найден класс MonthBinCounter");
                System.out.println("Не найден атрибут count");
                System.out.println("Не найден атрибут date");
            }
        }
    }
	
    private void init(Session s) {
        if (msgCounter == null) {
            try {
                KrnClass cls = s.getClassByName("MSGCounter");
                msgIdAttr = s.getAttributeByName(cls, "lastID").id;
                msgDayAttr = s.getAttributeByName(cls, "lastDate").id;
                
                msgCounter = s.getClassObjects(cls, new long[0], 0)[0];
            } catch (Exception e) {
                System.out.println("Не найден класс MSGCounter");
                System.out.println("Не найден атрибут lastID");
                System.out.println("Не найден атрибут lastDate");
                System.out.println("Не найден объект класса MSGCounter");
            }
        }
    }

    private long getNext(long monthKey, Session s) throws KrnException {//TODO
		KrnObject counter = monthCounters.get(monthKey);
		long res = 0;
		if (counter == null) {
			KrnObject[] objs = s.getObjectsByAttribute(monthCounterCls.id, monthDateAttr.id, 0, ComparisonOperations.CO_EQUALS, monthKey, 0);
			if (objs.length > 0) {
				counter = objs[0];
			} else {
				counter = s.createObject(monthCounterCls, 0);
				s.setLong(counter.id, monthDateAttr.id, 0, monthKey, 0);
				s.setLong(counter.id, monthCountAttr.id, 0, 1, 0);
				res = 1;
			}
			monthCounters.put(monthKey, counter);
		}
		if (res == 0) {
			long[] ls = s.getLongs(counter.id, monthCountAttr.id, 0);
			res = ls.length > 0 ? ls[0] + 1 : 1;
			s.setLong(counter.id, monthCountAttr.id, 0, res, 0);
		}
		return res;
	}
}
