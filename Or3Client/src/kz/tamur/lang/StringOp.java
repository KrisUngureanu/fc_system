package kz.tamur.lang;

import kz.tamur.SecurityContextHolder;
import kz.tamur.comps.Constants;
import kz.tamur.util.Funcs;
import kz.tamur.util.ThreadLocalDateFormat;

import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * Date: 08.06.2005
 * Time: 10:23:40
 * 
 * @author Berik
 */
public class StringOp {

    /** string resources. */
    private StringResources stringResources;

    /**
     * Преобразовать строку в число типа <code>Integer</code>.
     * 
     * @param str
     *            строка для обработки.
     * @return число типа <code>Integer</code>.
     */
    public Integer toInt(String str) {
        if (str != null && !str.isEmpty()) {
            try {
                return new Integer(str);
            } catch (NumberFormatException e) {
                SecurityContextHolder.getLog().error(e, e);
            }
        }
        return null;
    }

    /**
     * Преобразовать строку в число типа <code>Long</code>.
     * 
     * @param str
     *            строка для обработки.
     * @return число типа <code>Long</code>.
     */
    public Long toLong(String str) {
        if (str != null && !str.isEmpty()) {
            try {
                return new Long(str);
            } catch (NumberFormatException e) {
            	SecurityContextHolder.getLog().error(e, e);
            }
        }
        return null;
    }

    /**
     * Преобразовать строку в число типа <code>shot</code>.
     * 
     * @param str
     *            строка для обработки.
     * @return число типа <code>Long</code>.
     */
    public Short toShort(String str) {
        if (str != null && !str.isEmpty()) {
            try {
                return new Short(str);
            } catch (NumberFormatException e) {
            	SecurityContextHolder.getLog().error(e, e);
            }
        }
        return null;
    }
    /**
     * Преобразовать строку в дробное число типа <code>Double</code>.
     * 
     * @param str
     *            строка для обработки.
     * @return число типа <code>Double</code>.
     */
    public Double toFloat(String str) {
        if (str != null && !str.isEmpty()) {
            try {
                return new Double(str);
            } catch (NumberFormatException e) {
                try {
                    return new Double(str.replace(',', '.'));
                } catch (NumberFormatException e1) {
                	SecurityContextHolder.getLog().error(e1, e1);
                }
            }
        }
        return null;
    }

    /**
     * Преобразовать строку в число типа <code>BigDecimal</code>.
     * 
     * @param str
     *            строка для обработки.
     * @return число типа <code>BigDecimal</code>.
     */
    public java.math.BigDecimal toBigDecimal(String str) {
        if (str != null && !str.isEmpty()) {
            try {
                return new  java.math.BigDecimal(str);
            } catch (NumberFormatException e) {
                try {
                    return new java.math.BigDecimal(str.replace(',', '.'));
                } catch (NumberFormatException e1) {
                	SecurityContextHolder.getLog().error(e1, e1);
                }
            }
        }
        return null;
    }
   /**
     * Преобразовать строку в дату в соотвествии с заданным форматом.
     * 
     * @param str
     *            строка для преобразования.
     * @param fmt
     *            формат даты.
     * @return полученная дата.
     */
    public KrnDate toDate(String str, String fmt) {
        if (str != null && !str.isEmpty()) {
        	ThreadLocalDateFormat df = ThreadLocalDateFormat.get(fmt);
            try {
                return new KrnDate(df.parse(str, false).getTime());
            } catch (ParseException e) {
            	SecurityContextHolder.getLog().error(e, e);
            }
        }
        return null;
    }

    /**
     * Сгенерировать строку из объекта в соответствии с заданным форматом.
     * 
     * @param fmt
     *            формат строки.
     * @param value
     *            объект для преобразования.
     * @return результатирующая строка.
     */
    public String format(String fmt, Object value) {
        return value != null ? String.format(fmt, value) : "";
    }

    /**
     * Сравнение строк.
     * Используется для сравнения наименований, ФИО.
     * в результате сравнения строки нормализуются убирая в них
     * лишние пробелы и некорректные символы.
     * 
     * 
     * @param str1
     *            первая строка для сравнения.
     * @param str2
     *            вторая строка для сравнения.
     * @return <code>true</code>, в случае равенства строк.
     */
    public boolean checkName(String str1, String str2) {
        str1 = (str1.toLowerCase(Constants.OK)).trim();
        str2 = (str2.toLowerCase(Constants.OK)).trim();
        // убираем знаки
        Pattern pattern = Pattern.compile("([^a-zA-zа-яА-Я0-9\\s])");

        Matcher matcher = pattern.matcher(str1);
        str1 = matcher.replaceAll("");

        matcher = pattern.matcher(str2);
        str2 = matcher.replaceAll("");

        // убираем ненужные пробелы
        pattern = Pattern.compile("\\s+");

        matcher = pattern.matcher(str1);
        str1 = matcher.replaceAll(" ");

        matcher = pattern.matcher(str2);
        str2 = matcher.replaceAll(" ");

        return str1.equals(str2);
    }
    
    public boolean isEmail(String str) {
    	Pattern pattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,8}$", Pattern.CASE_INSENSITIVE);
    	Matcher matcher = pattern.matcher(str);
    	if(matcher.matches()) {
    		return true;
    	} else {
    		return false;
    	}
    }

    public boolean matches(String str, String regex) {
    	Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    	Matcher matcher = pattern.matcher(str);
    	if(matcher.matches()) {
    		return true;
    	} else {
    		return false;
    	}
    }

    /**
     * Задать ресурс строк.
     * 
     * @param stringResources
     *            новое значение ресурса.
     */
    public void setStringResources(StringResources stringResources) {
        this.stringResources = stringResources;
    }

    /**
     * Получить строку из ресурса.
     * 
     * @param uid
     *            идентификатор строки.
     * @return найденная строка
     * @throws Exception
     *             the exception
     */
    public String getString(String uid) throws Exception {
        if (stringResources != null) {
            long langId = stringResources.getDefaultLangId();
            return stringResources.getStrings(langId).get(uid);
        }
        return null;
    }

    /**
     * Получить строку из ресурса.
     * 
     * @param uid
     *            идентификатор строки.
     * @param params
     *            параметры
     * @return найденная строка
     * @throws Exception
     *             the exception
     * @deprecated использовать {@link #getString(String uid)}
     */
    public String getString(String uid, Object[] params) throws Exception {
        return getString(uid);
    }

    /**
     * Получить строку из ресурса.
     * 
     * @param uid
     *            идентификатор строки.
     * @param lang
     *            язык,на котором требуется текст.
     * @return найденная строка
     * @throws Exception
     *             the exception
     */
    public String getString(String uid, KrnObject lang) throws Exception {
        if (stringResources != null) {
            return stringResources.getStrings(lang.getKrnObject().id).get(uid);
        }
        return null;
    }

    /**
     * Получить строку из ресурса.
     * 
     * @param uid
     *            идентификатор строки.
     * @param params
     *            параметры
     * @param lang
     *            язык,на котором требуется текст
     * @return найденная строка
     * @throws Exception
     *             the exception
     * @deprecated использовать {@link #getString(String uid, KrnObject lang)}
     */
    public String getString(String uid, Object[] params, KrnObject lang) throws Exception {
        return getString(uid, lang);
    }

    /**
     * Получить из строки с числами заданный элемент.
     * 
     * @param str
     *            строка для обработки.
     * @param separator
     *            разделитель.
     * @param index
     *            номер элемента.
     * @return полученный элемент
     */
    public int getItem(String str, String separator, int index) {
        int res = 0;
        StringTokenizer st = new StringTokenizer(str, separator);
        int count = st.countTokens();
        for (int i = 1; i <= count; ++i) {
            String s = st.nextToken();
            if (i == index) {
                res = Integer.parseInt(s);
                break;
            }
        }
        return res;
    }

    /**
     * Преобразует число в строку длиной не меньше заданного <code>digitCount</code>.
     * Если число цифр в числе меньше чем заданно в <code>digitCount</code>, то недостающие
     * символы заменяются нулями и помещаются в начало строки.
     * 
     * @param number
     *            преобразуемое число.
     * @param digitCount
     *            минимальное количество символов.
     * @return преобразованное в строку число
     */
    public String toString(Number number, Number digitCount) {
        return String.format("%0" + digitCount.intValue() + "d", number.intValue());
    }

    /**
     * Транслитерация русского текста.
     * 
     * @param str
     *            the str
     * @return транслит текста
     * @deprecated использовать {@link kz.tamur.or3ee.common.lang.SystemWrp#toLatin(String)}
     */
    public String toRuglish(String str) {
        return Funcs.translite(str);
    }

    /**
     * Проверяет, состоит ли строка из цифр..
     * 
     * @param str
     *            строка.
     * @return <code>true</code>, если в строке только цифры.
     */
    public boolean isDigit(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        char[] chs = str.toCharArray();
        for (char ch : chs) {
            if (!Character.isDigit(ch)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Преобразовать число в его представление прописью на русском языке.
     * 
     * @param y
     *            число.
     * @return представление числа прописью.
     */
    public String convertLongToText(Number y) {
        if (y != null) {
            long l = y.longValue();
            return Funcs.intToStringRus(l, 0, 0);
        }
        return "";
    }

    /**
     * Преобразовать число в его представление прописью на русском языке с учётом рода и падежа.
     * 
     * @param y
     *            число.
     * @param rod
     *            род.
     * @param padezh
     *            падеж.
     * @return представление числа прописью.
     */
    public String convertLongToText(Number y, Number rod, Number padezh) {
        if (y != null) {
            long l = y.longValue();
            long r = rod.longValue();
            long p = padezh.longValue();
            return Funcs.intToStringRus(l, r, p);
        }
        return "";
    }

    /**
     * Преобразовать число в его представление прописью на казахском языке.
     * 
     * @param y
     *            число.
     * @return представление числа прописью.
     */
    public String convertLongToTextKaz(Number y) {
        if (y != null) {
            long l = y.longValue();
            return Funcs.intToStringKaz(l);
        }
        return "";
    }
    
    public String convertLongToTextEn(Number y) {
        if (y != null) {
            long l = y.longValue();
            return Funcs.intToStringEn(l);
        }
        return "";
    }
    
    public int compareTo(String string1, String string2) {
    	return string1.compareTo(string2);
    }
    
    public int compareToIgnoreCase(String string1, String string2) {
    	return string1.compareToIgnoreCase(string2);
    }
    
    public String numberToFormattedString(Number number) {
    	return numberToFormattedString(number, "#,##0.000");
    }
    
    public String numberToFormattedString(Number number, String pattern) {
    	DecimalFormat decimalFormat = new DecimalFormat(pattern);
    	return decimalFormat.format(number);
    }
}