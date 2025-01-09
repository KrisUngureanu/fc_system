package kz.tamur.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ThreadLocalDateFormat extends ThreadLocal<DateFormat> {
	
	private static Map<String, ThreadLocalDateFormat> formats =
			Collections.synchronizedMap(new HashMap<String, ThreadLocalDateFormat>());

	public static ThreadLocalDateFormat dd_MM_yyyy = ThreadLocalDateFormat.get("dd.MM.yyyy");
	public static ThreadLocalDateFormat dd_MM_yyyy_HH_mm_ss = ThreadLocalDateFormat.get("dd.MM.yyyy HH:mm:ss");

	private String pattern;
	private Locale locale;

    public static synchronized ThreadLocalDateFormat get(String pattern) {
    	ThreadLocalDateFormat fmt = formats.get(pattern);
    	if (fmt == null) {
            fmt = new ThreadLocalDateFormat(pattern);
            formats.put(pattern, fmt);
    	}
        return fmt;
    }

	public ThreadLocalDateFormat(String pattern) {
		this.pattern = pattern;
		this.locale = Locale.getDefault();
	}

	public ThreadLocalDateFormat(String pattern, Locale locale) {
		this.pattern = pattern;
		this.locale = locale;
	}

	@Override
	protected DateFormat initialValue() {
		return new SimpleDateFormat(pattern, locale);
	}

	public String format(Object date) {
		if (date instanceof Date)
			return get().format(date);
		return
			null;
	}

	public String format(Object date, boolean lenient) {
		DateFormat df = get();
		boolean oldLenient = df.isLenient();
		if (oldLenient == lenient)
			return df.format(date);
		else {
			df.setLenient(lenient);
			try {
				return df.format(date);
			} finally {
				df.setLenient(oldLenient);
			}
		}
	}

	public Date parse(String str) throws ParseException {
		return get().parse(str);
	}

	public Date parse(String str, boolean lenient) throws ParseException {
		DateFormat df = get();
		boolean oldLenient = df.isLenient();
		if (oldLenient == lenient)
			return df.parse(str);
		else {
			df.setLenient(lenient);
			try {
				return df.parse(str);
			} finally {
				df.setLenient(oldLenient);
			}
		}
	}
}
