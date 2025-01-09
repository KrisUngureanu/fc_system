package kz.tamur.lang;

import kz.tamur.util.Funcs;
import org.joda.time.Period;

import com.cifs.or2.kernel.KrnDate;

import java.util.*;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 22.01.2005
 * Time: 15:56:37
 * 
 * @author berik
 */
public class DateOp {

    /**
     * Хранит даты введенные пользователем.
     * текущую дату, дату <i>с</i>, дату <i>по</i>.
     */
    private Map<Integer, com.cifs.or2.kernel.Date> filterDates_;

    /**
     * Слушатели для карты дат.
     * Необходим для того чтобы знать какие формулы надо перечитать когда пользователь введет дату.
     * */
    private List<FilterDatesListener> list;

    /**
     * Конструктор класса DateOp.
     */
    public DateOp() {
        list = new ArrayList<FilterDatesListener>();
    }

    /**
     * Конструктор класса DateOp.
     * 
     * @param filterDates
     *            пользовательские даты.
     */
    public DateOp(Map<Integer, com.cifs.or2.kernel.Date> filterDates) {
        this.filterDates_ = filterDates;
    }

    /**
     * Задать новую карту пользовательских дат.
     * 
     * @param filterDates
     *            новая карта дат.
     */
    public void setFilterDates(Map<Integer, com.cifs.or2.kernel.Date> filterDates) {
        this.filterDates_ = filterDates;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).changed(filterDates_);
            }
        }
    }

    /**
     * Получить текущую дату.
     * 
     * @return текущая дата типа <code>KrnDate</code>.
     */
    public KrnDate getCurrDate() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return new KrnDate(c.getTimeInMillis());
    }

    /**
     * Получить <code>KrnDate</code> из даты типа <code>java.util.Date</code>.
     * 
     * @param java.util.Date.
     * @return дата типа <code>KrnDate</code>.
     */
    public KrnDate getDate(Date date) {
        return new KrnDate(date.getTime());
    }

    /**
     * Получить <code>KrnDate</code> из даты типа <code>XMLGregorianCalendar</code>.
     * 
     * @param xmlDate
     *            дата типа <code>XMLGregorianCalendar</code>.
     * @return дата типа <code>KrnDate</code>.
     */
    public KrnDate getDate(XMLGregorianCalendar xmlDate) {
        return new KrnDate(xmlDate.toGregorianCalendar().getTimeInMillis());
    }

    /**
     * Сгенерировать <code>KrnDate</code> из числовых значений года, месяца и дня.
     * 
     * @param year
     *            год.
     * @param month
     *            месяц.
     * @param day
     *            день.
     * @return дата типа <code>KrnDate</code>.
     */
    public KrnDate getDate(Number year, Number month, Number day) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(Calendar.YEAR, year.intValue());
        c.set(Calendar.MONTH, month.intValue() - 1);
        c.set(Calendar.DAY_OF_MONTH, day.intValue());
        return new KrnDate(c.getTimeInMillis());
    }

    /**
     * Получить дату первого дня текущего года.
     * 
     * @return дата типа <code>KrnDate</code>.
     */
    public KrnDate getDateStartYear() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return new KrnDate(c.getTimeInMillis());
    }

    /**
     * Получить дату последнего дня текущего года.
     * 
     * @return дата типа <code>KrnDate</code>.
     */
    public KrnDate getDateEndYear() {
        Calendar c = Calendar.getInstance();
        int year_ = c.get(Calendar.YEAR);
        c.set(Calendar.YEAR, year_ + 1);
        c.set(Calendar.DAY_OF_YEAR, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return new KrnDate(c.getTimeInMillis() - (24 * 60 * 60 * 1000L));
    }

    /**
     * Получить текущее время.
     * 
     * @return время типа <code>KrnDate</code>.
     */
    public KrnDate getCurrTime() {
        return new KrnDate();
    }

    /**
     * Прочитать текущую дату для фильтра.
     * 
     * @return дата типа <code>KrnDate</code>.
     */
    public KrnDate readCurrDate() {
        return Funcs.convertDate((com.cifs.or2.kernel.Date) filterDates_.get(new Integer(0)));
    }

    /**
     * Прочитать первую дату для фильтра.
     * 
     * @return дата типа <code>KrnDate</code>.
     */
    public KrnDate readFirstDate() {
        if (filterDates_ != null) {
            Object o = filterDates_.get(new Integer(1));
            if (o != null) {
                return Funcs.convertDate((com.cifs.or2.kernel.Date) o);
            }
        }
        return null;
    }

    /**
     * Прочитать последнюю дату фильтра.
     * 
     * @return дата типа <code>KrnDate</code>.
     */
    public KrnDate readLastDate() {
        if (filterDates_ != null) {
            Object o = filterDates_.get(new Integer(2));
            if (o != null) {
                return Funcs.convertDate((com.cifs.or2.kernel.Date) o);
            }
        }
        return null;
    }

    /**
     * Получить год из даты.
     * 
     * @param date
     *            дата для обработки.
     * @return номер года.
     */
    public int getYear(KrnDate date) {
        return date.getYear();
    }

    /**
     * Получить месяц из даты.
     * 
     * @param date
     *            дата для обработки.
     * @return номер месяца.
     */
    public int getMonth(KrnDate date) {
        return date.getMonth();
    }

    /**
     * Получить день из даты.
     * 
     * @param date
     *            дата для обработки.
     * @return номер дня
     */
    public int getDay(KrnDate date) {
        return date.getDay();
    }

    /**
     * Перевести дни в дату.
     * 
     * @param days
     *            the days
     * @return krn date
     */
    public KrnDate daysToDate(Number days) {
        return new KrnDate(days.intValue() * 24 * 60 * 60 * 1000L);
    }

    /**
     * Перевести год в дату.
     * 
     * @param years
     *            год.
     * @return дата типа <code>KrnDate</code>.
     */
    public KrnDate yearsToDate(Number years) {
        return new KrnDate(years.intValue() * 360 * 24 * 60 * 60 * 1000L);
    }

    /**
     * Добавить слушатель для карты дат.
     * 
     * @param l
     *            новый слушатель.
     */
    public void addFilterDatesListener(FilterDatesListener l) {
        list.add(l);
    }

    /**
     * Удалить слушатель для карты дат.
     * 
     * @param l
     *            удаляемый слушатель.
     */
    public void removeFilterParamListener(FilterDatesListener l) {
        list.remove(l);
    }

    /**
     * Очистить список слушателей дат.
     */
    public void clearFilterDatesListeners() {
        list.clear();
    }

    /**
     * Получить период времени между двумя датами.
     * 
     * @param d1
     *            дата начала периода.
     * @param d2
     *            дата окончания периода.
     * @return целочисленный периодов, где элементы расположны: год, месяц, неделя, день, час, минута, секунда, миллисекунда.
     */
    public List<Integer> getPeriod(KrnDate d1, KrnDate d2) {
        Period p = new Period(d1.getTime(), d2.getTime());
        List<Integer> list = new ArrayList<Integer>(3);
        list.add(p.getYears());
        list.add(p.getMonths());
        list.add(p.getWeeks());
        list.add(p.getDays());
        list.add(p.getHours());
        list.add(p.getMinutes());
        list.add(p.getSeconds());
        list.add(p.getMillis());
        return list;
    }

    /**
     * Получить дату типа <code>XMLGregorianCalendar</code> из даты <code>java.util.Date</code>.
     * 
     * @param date
     *            исходная дата
     * @return преобразованная дата.
     * @throws Exception
     *             the exception
     */
    public XMLGregorianCalendar getXmlDate(Date date) throws Exception {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
    }
}
