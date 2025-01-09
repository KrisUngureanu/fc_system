package com.cifs.or2.kernel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.joda.time.Period;
import org.joda.time.PeriodType;

import kz.tamur.util.ThreadLocalDateFormat;

public class KrnDate extends Date {
	
    public KrnDate() {
		super();
	}
    
    public KrnDate(long date) {
		super(date);
	}
    /**
     * Возвращает номер года<br><br>
     * Пример:
	 * $nYear = $date1.getYear()
     * @return номер года
     */
	public int getYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(this);
        return cal.get(Calendar.YEAR);
    }
	/**
	 * Возвращает номер месяца в году<br><br>
	 * Пример:
	 * $nMonth = $date1.getMonth()
	 * @return номер месяца в году
	 */
    public int getMonth() {
        Calendar c = Calendar.getInstance();
        c.setTime(this);
        return c.get(Calendar.MONTH) + 1;
    }
    /**
     * Возвращает номер дня в месяце<br><br>
     * Пример:
	 * $nDay = $date1.getDay()
     * @return номер дня
     */
    public int getDay() {
        Calendar c = Calendar.getInstance();
        c.setTime(this);
        return c.get(Calendar.DAY_OF_MONTH);
    }
    /**
     * возвращает номер дня в неделе<br><br>
     * Пример:
	 * $nDayWeek = $date1.getDayOfWeek()
     * @return номер дня
     */
    public int getDayOfWeek() {
        Calendar c = Calendar.getInstance();
        c.setTime(this);
        return c.get(Calendar.DAY_OF_WEEK);
    }
    /**
     * Возвращает номер часа в дне<br><br>
     * Пример:
	 * $nHour = $date1.getHours()
	 * @return номер часа
     */
    public int getHours() {
        Calendar c = Calendar.getInstance();
        c.setTime(this);
        return c.get(Calendar.HOUR_OF_DAY);
    }
    /**
     * Возвращает номер минуты в часе<br><br>
     * Пример:
	 * $nMinute = $date1.getMinutes()
	 * @return номер минуты
     */
    public int getMinutes() {
        Calendar c = Calendar.getInstance();
        c.setTime(this);
        return c.get(Calendar.MINUTE);
    }
    /**
     * Возвращает номер секунды в минуте<br><br>
     * Пример:
	 * $nSecond = $date1.getSeconds()
	 * @return номер секунды
     */
    public int getSeconds() {
        Calendar c = Calendar.getInstance();
        c.setTime(this);
        return c.get(Calendar.SECOND);
    }
    /**
     * Добавляет к дате указанное в count количество дней. Действует 
	 * инкрементно, т.е возвращает измененную дату.<br><br>
	 * Пример:
	 * $date2 = $date1.addDays(3) после выполнения оператора $date1 = $date2
     * @param count
     * @return новая дата
     * @see java.util.Calendar#add(int, int)
     */
    public KrnDate addDays(long count) {
        Calendar c = Calendar.getInstance();
        c.setTime(this);
        c.add(Calendar.DATE, (int)count);
        setTime(c.getTimeInMillis());
        return this;
    }
    /**
     * Добавляет к дате указанное в count количество секунд. 
	 * Действует инкрементно, т.е возвращает измененную дату.<br><br>
	 * Пример:
	 * $date2 = $date1.addSeconds(3) после выполнения оператора $date1 = $date2
     * @param count
     * @return новая дата
     * @see java.util.Calendar#add(int, int)
     */
    public KrnDate addSeconds(long count) {
        Calendar c = Calendar.getInstance();
        c.setTime(this);
        c.add(Calendar.SECOND, (int)count);
        setTime(c.getTimeInMillis());
        return this;
    }
    /**
     * Добавляет к дате указанное в count количество минут. Действует 
	 * инкрементно, т.е возвращает измененную дату. <br><br>
	 * Пример:
	 * $date2 = $date1.addMinutes(3) после выполнения оператора $date1 = $date2
     * @param count
     * @return новая дата
     * @see java.util.Calendar#add(int, int)
     */
    public KrnDate addMinutes(long count) {
        Calendar c = Calendar.getInstance();
        c.setTime(this);
        c.add(Calendar.MINUTE, (int)count);
        setTime(c.getTimeInMillis());
        return this;
    }
    /**
     * Добавляет к дате указанное в count количество часов. Действует 
	 * инкрементно, т.е возвращает измененную дату.<br><br>
	 * Пример:
	 * $date2 = $date1.addHours(3) после выполнения оператора $date1 = $date2
     * @param count
     * @return новая дата
     * @see java.util.Calendar#add(int, int)
     */
    public KrnDate addHours(long count) {
        Calendar c = Calendar.getInstance();
        c.setTime(this);
        c.add(Calendar.HOUR_OF_DAY, (int)count);
        setTime(c.getTimeInMillis());
        return this;
    }
    /**
     * Добавляет к дате указанное в count количество недель. Действует 
	 * инкрементно, т.е возвращает измененную дату.<br><br>
     * @param count
     * @return новая дата
     * @see java.util.Calendar#add(int, int)
     */
    public KrnDate addWeeks(long count) {
        Calendar c = Calendar.getInstance();
        c.setTime(this);
        c.add(Calendar.WEEK_OF_MONTH, (int)count);
        setTime(c.getTimeInMillis());
        return this;
    }
    /**
     * Добавляет к дате указанное в count количество месяцев. Действует 
	 * инкрементно, т.е возвращает измененную дату.<br><br>
     * @param count
     * @return новая дата
     * @see java.util.Calendar#add(int, int)
     */
    public KrnDate addMonths(long count) {
        Calendar c = Calendar.getInstance();
        c.setTime(this);
        c.add(Calendar.MONTH, (int)count);
        setTime(c.getTimeInMillis());
        return this;
    }
    /**
     * Добавляет к дате указанное в count количество лет. Действует 
	 * инкрементно, т.е возвращает измененную дату.<br><br>
	 * Пример:
	 * $date2 = $date1.addYears(3) после выполнения оператора $date1 = $date2
     * @param count
     * @return новая дата
     * @see java.util.Calendar#add(int, int)
     */
    public KrnDate addYears(long count) {
        Calendar c = Calendar.getInstance();
        c.setTime(this);
        c.add(Calendar.YEAR, (int)count);
        setTime(c.getTimeInMillis());
        return this;
    }

    public String toString(String pattern) {
    	ThreadLocalDateFormat fmt = ThreadLocalDateFormat.get(pattern);
        return fmt.format(this);
    }

	@Override
	public Object clone() {
		return new KrnDate(this.getTime());
	}
    /**
     * Возвращает начальную дату<br><br>
     * Пример:
	 * $date2 = $date1.getYearDateStart()
     * @return дата
     */
	public KrnDate getYearDateStart() {
		Calendar c = Calendar.getInstance();
		c.setTime(this);
		c.set(Calendar.DAY_OF_YEAR, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return new KrnDate(c.getTimeInMillis());
	}
	/**
	 * Возвращает конечную дату<br><br>
	 * Пример:
	 * $date2 = $date1.getYearDateEnd()
	 * @return дата
	 */
	public KrnDate getYearDateEnd() {
		Calendar c = Calendar.getInstance();
		c.setTime(this);
		int year_=c.get(Calendar.YEAR);
		c.set(Calendar.YEAR, year_+1);
		c.set(Calendar.DAY_OF_YEAR, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return new KrnDate(c.getTimeInMillis());
	}
	/**
	 * Возвращает количество дней после даты d2 до наступления 
	 * даты d1 (см. пример).<br><br>
	 * Пример:
	 * $var = $date1.getDaysAfter($date2)
	 * @param date
	 * @return кол-во дней
	 */
	public int getDaysAfter(KrnDate date) {
		Calendar c = Calendar.getInstance();
		c.setTime(this);
		long dist = c.getTimeInMillis();
		c.setTime(date);
		dist -= c.getTimeInMillis();
		return (int)(dist / (1000 * 60 * 60 * 24));
	}
	/**
	 * Возвращает количество часов после даты d2 до наступления 
	 * даты d1 (см. пример).<br><br>
	 * Пример:
	 * $var = $date1.getHoursAfter($date2)
	 * @param date
	 * @return кол-во часов
	 */
	public int getHoursAfter(KrnDate date) {
		Calendar c = Calendar.getInstance();
		c.setTime(this);
		long dist = c.getTimeInMillis();
		c.setTime(date);
		dist -= c.getTimeInMillis();
		return (int)(dist / (1000 * 60 * 60));
	}
	/**
	 * Возвращает количество минут после даты d2 до наступления 
	 * даты d1 (см. пример).<br><br>
	 * Пример:
	 * $var = $date1.getMinutesAfter($date2)
	 * @param date
	 * @return кол-во минут
	 */
	public int getMinutesAfter(KrnDate date) {
		Calendar c = Calendar.getInstance();
		c.setTime(this);
		long dist = c.getTimeInMillis();
		c.setTime(date);
		dist -= c.getTimeInMillis();
		return (int)(dist / (1000 * 60));
	}
	/**
	 * Возвращает массив чисел &lt;лет, месяцев, дней
     * &gt;, содержащихся в периоде.<br><br>
     * Пример:
	 * $list = $date1.getPeriodAfter($date2)
	 * @param date
	 * @return
	 */
    public List<Integer> getPeriodAfter(KrnDate date) {
        Period p  = new Period(date.getTime(), getTime(), PeriodType.yearMonthDay());
        List<Integer> list = new ArrayList<Integer>(3);
        list.add(p.getYears());
        list.add(p.getMonths());
        list.add(p.getDays());
        return list;
    }
    
    public XMLGregorianCalendar getXmlDate() throws Exception {
    	GregorianCalendar c = new GregorianCalendar();
    	c.setTime(this);
    	XMLGregorianCalendar resXmlDate= DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
    	if(resXmlDate!=null && resXmlDate.getTimezone()==420)
    	    resXmlDate.setTimezone(360);
    	return resXmlDate;
    }
}
