package kz.tamur.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.servlet.ServletRequest;

import kz.tamur.comps.Constants;
import kz.tamur.ods.Value;
import kz.tamur.or3.util.PathElement;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.data.AttrRecord;
import kz.tamur.rt.data.ObjectRecord;
import kz.tamur.rt.data.Record;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.owasp.esapi.ESAPI;

import com.cifs.or2.kernel.Date;
import com.cifs.or2.kernel.FloatValue;
import com.cifs.or2.kernel.KrnAttribute;
import com.cifs.or2.kernel.KrnDate;
import com.cifs.or2.kernel.KrnObject;
import com.cifs.or2.kernel.LongValue;
import com.cifs.or2.kernel.ObjectValue;
import com.cifs.or2.kernel.StringValue;
import com.cifs.or2.kernel.Time;

public class Funcs {
	private static final String logLevel = "ALL";
	
    private static Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + Funcs.class.getName());

	private static ThreadLocalDateFormat formatter_time = new ThreadLocalDateFormat("hh:mm:ss");

	private static ThreadLocalDateFormat dateFormat;
	private static ThreadLocalDateFormat shortTimeFormat;
	private static ThreadLocalDateFormat timeFormat;
	private static ThreadLocalDateFormat bigTimeFormat;
	private static ThreadLocalDateFormat timeOnlyFormat;
	private static ThreadLocalDateFormat shortTimeOnlyFormat;
	private static ThreadLocalDateFormat dateOnlyFormat;
	private static final String[] tensNames = {
		"",
		" ten",
		" twenty",
		" thirty",
		" forty",
		" fifty",
		" sixty",
		" seventy",
		" eighty",
		" ninety"
		};

		private static final String[] numNames = {
		"",
		" one",
		" two",
		" three",
		" four",
		" five",
		" six",
		" seven",
		" eight",
		" nine",
		" ten",
		" eleven",
		" twelve",
		" thirteen",
		" fourteen",
		" fifteen",
		" sixteen",
		" seventeen",
		" eighteen",
		" nineteen"
		};

	private static Map<String, String> allConnectionURLs;

	public static final com.cifs.or2.kernel.Time nullTime = new com.cifs.or2.kernel.Time((short) -1, (short) -1,
			(short) -1, (short) -1, (short) -1, (short) -1, (short) -1);
	
	private static Map<String, String> contentTypes = new HashMap<>();

	static {
    	contentTypes.put("rtf", "application/rtf");
    	contentTypes.put("csv", "text/csv");

    	contentTypes.put("doc", "application/msword");
    	contentTypes.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    	contentTypes.put("xls", "application/vnd.ms-excel");
    	contentTypes.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    	contentTypes.put("ppt", "application/vnd.ms-powerpoint");
    	contentTypes.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
    	contentTypes.put("vsd", "application/vnd.visio");
    	
    	contentTypes.put("htm", "text/html; charset=UTF-8");
    	contentTypes.put("html", "text/html; charset=UTF-8");
    	contentTypes.put("xml", "text/xml; charset=UTF-8");
    	contentTypes.put("txt", "text/plain; charset=UTF-8");
    	contentTypes.put("pdf", "application/pdf");

    	contentTypes.put("png", "image/png");
    	contentTypes.put("jpg", "image/jpeg");
    	contentTypes.put("jpeg", "image/jpeg");
    	contentTypes.put("gif", "image/gif");
    	contentTypes.put("bmp", "image/bmp");
    	contentTypes.put("ico", "image/vnd.microsoft.icon");
    	contentTypes.put("svg", "image/svg+xml");
    	contentTypes.put("tif", "image/tiff");
    	contentTypes.put("tiff", "image/tiff");
    	
    	contentTypes.put("avi", "video/x-msvideo");
    	contentTypes.put("webm", "video/webm");
    	contentTypes.put("mpeg", "video/mpeg");
    	
    	contentTypes.put("wav", "audio/wav");
    	contentTypes.put("weba", "audio/webm");
    	contentTypes.put("mp3", "audio/mpeg");
    	contentTypes.put("mid", "audio/midi");
    	contentTypes.put("midi", "audio/x-midi");
    	
    	contentTypes.put("zip", "application/zip");
    	contentTypes.put("rar", "application/vnd.rar");
    }

	public static String getContentTypeByExtension(String ext) {
		return contentTypes.get(ext);
	}

	public static int[] makeIntArray(Collection col) {
		if (col == null)
			return new int[0];
		int[] res = new int[col.size()];
		int j = 0;
		for (Iterator it = col.iterator(); it.hasNext(); ++j) {
			Integer i = (Integer) it.next();
			res[j] = i.intValue();
		}
		return res;
	}

	public static long[] makeLongArray(Collection col) {
		if (col == null)
			return new long[0];
		long[] res = new long[col.size()];
		int j = 0;
		for (Iterator it = col.iterator(); it.hasNext(); ++j) {
			Number i = (Number) it.next();
			res[j] = i.longValue();
		}
		return res;
	}

	public static Set getSet(Map map, Object key) {
		Set res = (Set) map.get(key);
		if (res == null) {
			res = new TreeSet();
			map.put(key, res);
		}
		return res;
	}

	public static List<Object> makeList(Object[] objs) {
		List<Object> res = new ArrayList<Object>();
		if (objs != null) {
			for (Object obj : objs)
				res.add(obj);
		}
		return res;
	}

	public static Set getSet(MapMap mmap, Object key1, Object key2) {
		Set res = (Set) mmap.get(key1, key2);
		if (res == null) {
			res = new TreeSet();
			mmap.put(key1, key2, res);
		}
		return res;
	}

	/**
	 * Замена нестандардных символов на их аналоги в HTML
	 * 
	 * @param str
	 *            исходная строка
	 * @return изменённая строка
	 */
	public static String xmlSpChar(String str) {
		if (str == null)
			return "";
		StringBuilder res = new StringBuilder();
		char[] arr = str.toCharArray();
		for (int i = 0; i < arr.length; ++i) {
			switch ((int) arr[i]) {
			case 61548:
				res.append("&#8226;");
				break;
			default:
				res.append(arr[i]);
			}
		}
		return res.toString();
	}

	public static String xmlQuote(String str) {
		if (str == null)
			return "";
		StringBuilder res = new StringBuilder();
		char[] arr = str.toCharArray();
		for (int i = 0; i < arr.length; ++i) {
			switch (arr[i]) {
			case '<':
				res.append("&#60;");
				break;
			case '>':
				res.append("&#62;");
				break;
			case '&':
				res.append("&#38;");
				break;
			case '\'':
				res.append("&#39;");
				break;
			case '/':
				res.append("&#47;");
				break;
			case '"':
				res.append("&#34;");
				break;
			default:
				res.append(arr[i]);
			}
		}
		return res.toString();
	}

	public static String xmlQuote2(String str) {
		if (str == null)
			return "";
		StringBuilder res = new StringBuilder();
		char[] arr = str.toCharArray();
		for (int i = 0; i < arr.length; ++i) {
			switch (arr[i]) {
			case '<':
				res.append("&#60;");
				break;
			case '>':
				res.append("&#62;");
				break;
			case '&':
				res.append("&#38;");
				break;
			case '\'':
				res.append("&#39;");
				break;
			case '/':
				res.append("&#47;");
				break;
			case '"':
				res.append("&#34;");
				break;
			case ' ':
				res.append("&amp;nbsp;");
				break;
			case '\r':
				res.append("&#60;br&#47;&#62;");
				break;
			case '\n':
				res.append("&#60;br&#47;&#62;");
				break;
			default:
				res.append(arr[i]);
			}
		}
		return res.toString();
	}

	public static String[] append(String[] src, String[] strs) {
		int i = src.length;
		String[] res = new String[i + strs.length];
		System.arraycopy(src, 0, res, 0, src.length);
		System.arraycopy(strs, 0, res, src.length, strs.length);
		return res;
	}

	public static boolean isIntegralType(long classId) {
		return classId < 100;
	}

	public static String removeChars(String str, char ch) {
		if (str == null)
			return "";
		StringBuffer res = new StringBuffer(str.length());
		char[] s = str.toCharArray();
		for (int i = 0; i < s.length; i++) {
			if (s[i] != ch)
				res.append(s[i]);
		}
		return res.toString();
	}

	public static long[] makeObjectIdArray(KrnObject[] objs) {
		long[] res = new long[objs.length];
		for (int i = 0; i < res.length; ++i)
			res[i] = objs[i].id;
		return res;
	}

	public static long[] makeValueIdArray(Collection<Value> vals) {
		long[] res = new long[vals.size()];
		Iterator<Value> it = vals.iterator();
		for (int i = 0; i < res.length; ++i)
			res[i] = ((KrnObject) it.next().value).id;
		return res;
	}

	public static long[] makeObjectIdArray(Collection<KrnObject> objs) {
		long[] res = new long[objs.size()];
		Iterator<KrnObject> it = objs.iterator();
		for (int i = 0; i < res.length; ++i)
			res[i] = it.next().id;
		return res;
	}

	public static long[] makeAttrIdArray(List<KrnAttribute> attrs) {
		long[] res = new long[attrs.size()];
		for (int i = 0; i < res.length; ++i)
			res[i] = attrs.get(i).id;
		return res;
	}

	public static MapMap convertObjectValues(ObjectValue[] ovs, boolean isArray) {
		MapMap res = new MapMap();
		for (int i = 0; i < ovs.length; ++i) {
			ObjectValue ov = ovs[i];
			if (isArray || ov.index == 0) {
				Pair val = new Pair(ov.value, new Long(ov.tid));
				res.put(new Long(ov.objectId), new Integer(ov.index), val);
			}
		}
		return res;
	}

	public static MapMap convertStringValues(StringValue[] svs, boolean isArray) {
		// HACK!!! Надо добавить поле tid в StringValue, LongValue и FloatValue
		final Integer ZERO_TID = new Integer(0);
		// END HACK
		MapMap res = new MapMap();
		for (int i = 0; i < svs.length; ++i) {
			StringValue sv = svs[i];
			if (isArray || sv.index == 0) {
				Pair val = new Pair(sv.value, ZERO_TID);
				res.put(new Long(sv.objectId), new Integer(sv.index), val);
			}
		}
		return res;
	}

	public static MapMap convertLongValues(LongValue[] lvs, boolean isArray) {
		// HACK!!! Надо добавить поле tid в StringValue, LongValue и FloatValue
		final Integer ZERO_TID = new Integer(0);
		// END HACK
		MapMap res = new MapMap();
		for (int i = 0; i < lvs.length; ++i) {
			LongValue lv = lvs[i];
			if (isArray || lv.index == 0) {
				Pair val = new Pair(new Long(lv.value), ZERO_TID);
				res.put(new Long(lv.objectId), new Integer(lv.index), val);
			}
		}
		return res;
	}

	public static Object getLastElement(Collection col) {
		Object res = null;

		for (Iterator it = col.iterator(); it.hasNext();) {
			res = it.next();
			if (!it.hasNext())
				break;
		}

		return res;
	}

	public static void trace(StringBuffer sb, String text) {
		String time = "[" + formatter_time.format(new java.util.Date()) + "] ";
		System.out.println(time + text);
		sb.append(time + text + "\r\n");
	}

	public static String ids2String(int[] ids, int offs, int len) {
		StringBuffer res = new StringBuffer();
		if (ids.length > offs) {
			res.append(ids[offs]);
			for (int i = offs + 1; i < offs + len; ++i)
				res.append("," + ids[i]);
		}
		return res.toString();
	}

	public static String ids2String(Collection ids) {
		StringBuffer res = new StringBuffer();
		if (ids.size() > 0) {
			Iterator it = ids.iterator();
			res.append(it.next());
			while (it.hasNext())
				res.append("," + it.next());
		}
		return res.toString();
	}

	public static String krnObjects2String(Collection objs) {
		StringBuffer res = new StringBuffer();
		if (objs.size() > 0) {
			Iterator it = objs.iterator();
			res.append(((KrnObject) it.next()).id);
			while (it.hasNext())
				res.append("," + ((KrnObject) it.next()).id);
		}
		return res.toString();
	}

	public static Pair splitAttrName(String path) {
		String name = null;
		Object index = null;
		int len = path.length();
		if (path.charAt(len - 1) == ']') {
			int open = path.lastIndexOf('[');
			name = path.substring(0, open);
			index = (len - open > 2) ? Integer.valueOf(path.substring(open + 1, len - 1)) : null;
		} else {
			name = path;
			index = "UNKNOWN";
		}
		return new Pair(name, index);
	}

	private static final boolean USE_ATTR_FILTER = "1".equals(Funcs.getSystemProperty("attrFilter", "1"));
	private static final Pattern attrNamePtn = USE_ATTR_FILTER
			? Pattern.compile("([^\\(<\\[]+)(?:\\(([^\\)]*)\\))?(?:\\[([^\\]]*)\\])?(?:<([^>]+)>)?")
			: Pattern.compile("([^\\(<\\[]+)(?:\\[([^\\]]*)\\])?(?:<([^>]+)>)?");

	public static PathElement parseAttrName(String name) {
		PathElement res = new PathElement();
		Matcher m = attrNamePtn.matcher(name);
		if (m.matches()) {
			res.name = m.group(1);

			String index = null;
			String cast = null;
			if (USE_ATTR_FILTER) {
				res.filterUid = m.group(2);
				index = m.group(3);
				cast = m.group(4);
			} else {
				index = m.group(2);
				cast = m.group(3);
			}

			if (index == null) {
				res.index = "UNKNOWN";
			} else if (index.length() > 0) {
				res.index = Integer.parseInt(m.group(3));
			}
			res.castClassName = cast;
		}
		return res;
	}

	public static boolean contains(Collection objs, KrnObject obj) {
		for (Iterator it = objs.iterator(); it.hasNext();) {
			KrnObject o = (KrnObject) it.next();
			if (o == obj || (o != null && obj != null && (o.id == obj.id))) {
				return true;
			}
		}
		return false;
	}

	public static boolean remove(Collection objs, KrnObject obj) {
		for (Iterator it = objs.iterator(); it.hasNext();) {
			KrnObject o = (KrnObject) it.next();
			if (o == obj || (o != null && obj != null && (o.id == obj.id))) {
				it.remove();
				return true;
			}
		}
		return false;
	}

	public static synchronized ThreadLocalDateFormat getDateFormat() {
		return getDateFormat(Constants.DD_MM_YYYY);
	}

	public static synchronized ThreadLocalDateFormat getDateFormat(int format) {
		switch (format) {
		case Constants.DD_MM_YYYY: {
			if (dateFormat == null) {
				dateFormat = new ThreadLocalDateFormat("dd.MM.yyyy");
			}
			return dateFormat;
		}
		case Constants.DD_MM_YYYY_HH_MM: {
			if (shortTimeFormat == null) {
				shortTimeFormat = new ThreadLocalDateFormat("dd.MM.yyyy HH:mm");
			}
			return shortTimeFormat;
		}
		case Constants.DD_MM_YYYY_HH_MM_SS: {
			if (timeFormat == null) {
				timeFormat = new ThreadLocalDateFormat("dd.MM.yyyy HH:mm:ss");
			}
			return timeFormat;
		}
		case Constants.DD_MM_YYYY_HH_MM_SS_SSS: {
			if (bigTimeFormat == null) {
				bigTimeFormat = new ThreadLocalDateFormat("dd.MM.yyyy HH:mm:ss:SSS");
			}
			return bigTimeFormat;
		}
		case Constants.HH_MM_SS: {
			if (timeOnlyFormat == null) {
				timeOnlyFormat = new ThreadLocalDateFormat("HH:mm:ss");
			}
			return timeOnlyFormat;
		}
		case Constants.HH_MM: {
			if (shortTimeOnlyFormat == null) {
				shortTimeOnlyFormat = new ThreadLocalDateFormat("HH:mm");
			}
			return shortTimeOnlyFormat;
		}
		case Constants.DD_MM: {
			if (dateOnlyFormat == null) {
				dateOnlyFormat = new ThreadLocalDateFormat("dd.MM");
			}
			return dateOnlyFormat;
		}
		}
		return null;
	}

	public static java.util.Date getCurrDate() {
		return getCurrDate(Constants.DD_MM_YYYY);
	}

	public static java.util.Date getCurrDate(int dateFormat) {
		Calendar c = Calendar.getInstance();
		switch (dateFormat) {
		case Constants.DD_MM:
			c.clear(Calendar.YEAR);
			c.clear(Calendar.HOUR);
			c.clear(Calendar.HOUR_OF_DAY);
			c.clear(Calendar.MINUTE);
			c.clear(Calendar.SECOND);
			c.clear(Calendar.MILLISECOND);
			break;
		case Constants.DD_MM_YYYY:
			c.clear(Calendar.HOUR);
			c.clear(Calendar.HOUR_OF_DAY);
			c.clear(Calendar.MINUTE);
			c.clear(Calendar.SECOND);
			c.clear(Calendar.MILLISECOND);
			break;
		case Constants.DD_MM_YYYY_HH_MM:
			c.clear(Calendar.SECOND);
			c.clear(Calendar.MILLISECOND);
			break;
		case Constants.DD_MM_YYYY_HH_MM_SS:
			c.clear(Calendar.MILLISECOND);
			break;
		}
		return c.getTime();
	}

	public static String upgradeExpression(String expr) {
		if (expr != null && expr.length() > 0) {
			String str = expr.trim();
			if (str.charAt(str.length() - 1) != ';') {
				return str + ";";
			}
		}
		return expr;
	}

	public static String underline(String str) {
		StringBuffer res = new StringBuffer(str.length() * 2 + 1);
		char[] chs = str.toCharArray();
		final char un = (char) 0x0332;
		// res.append(un);
		for (char ch : chs) {
			res.append(ch);
			res.append(un);
		}
		return res.toString();
	}

	public static String find(KrnObject o, StringValue[] svs) {
		for (int i = 0; i < svs.length; i++) {
			StringValue sv = svs[i];
			if (sv.objectId == o.id && sv.index == 0) {
				return sv.value;
			}
		}
		return null;
	}

	public static double find(KrnObject o, FloatValue[] fvs) {
		for (int i = 0; i < fvs.length; i++) {
			FloatValue fv = fvs[i];
			if (fv.objectId == o.id && fv.index == 0) {
				return fv.value;
			}
		}
		return 0;
	}

	public static long find(KrnObject o, LongValue[] vs) {
		for (int i = 0; i < vs.length; i++) {
			LongValue v = vs[i];
			if (v.objectId == o.id && v.index == 0) {
				return v.value;
			}
		}
		return 0;
	}

	public static long find(KrnObject o, int index, LongValue[] vs) {
		for (int i = 0; i < vs.length; i++) {
			LongValue v = vs[i];
			if (v.objectId == o.id && v.index == index) {
				return v.value;
			}
		}
		return 0;
	}

	public static KrnObject find(KrnObject o, ObjectValue[] vs) {
		for (int i = 0; i < vs.length; i++) {
			ObjectValue v = vs[i];
			if (v.objectId == o.id && v.index == 0) {
				return v.value;
			}
		}
		return null;
	}

	public static void addProperty(Element e, String name, String value) {
		Element prop = new Element(name);
		prop.setText(value);
		e.addContent(prop);
	}

	public static long[] makeObjectValueIdArray(ObjectValue[] ovs) {
		Set set = new HashSet();
		for (int i = 0; i < ovs.length; i++) {
			ObjectValue ov = ovs[i];
			set.add(new Long(ov.value.id));
		}
		long[] res = new long[set.size()];
		int i = 0;
		for (Iterator it = set.iterator(); it.hasNext();) {
			res[i++] = ((Number) it.next()).longValue();
		}
		return res;
	}

	public static Date convertDate(java.util.Date date) {
		if (date == null) {
			return new Date((short) 0, (short) 0, (short) 0);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return new Date(
				(short) cal.get(Calendar.DAY_OF_MONTH),
				(short) cal.get(Calendar.MONTH), 
				(short) cal.get(Calendar.YEAR));
	}
	
	public static Date convertDate2(java.util.Date date) {
        if (date == null) return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return new Date(
                (short)cal.get(Calendar.DAY_OF_MONTH),
                (short)cal.get(Calendar.MONTH),
                (short)cal.get(Calendar.YEAR));
    }

	public static Date convertDate(java.util.Date date, Calendar cal) {
		if (date == null) {
			return new Date((short) 0, (short) 0, (short) 0);
		}
		if (cal == null)
			cal = Calendar.getInstance();
		cal.setTime(date);
		return new Date((short) cal.get(Calendar.DAY_OF_MONTH), (short) cal.get(Calendar.MONTH),
				(short) cal.get(Calendar.YEAR));
	}

	public static KrnDate convertDate(Date date) {
		if (date == null || (date.day == 0 && date.month == 0 && date.year == 0))
			return null;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, date.day);
		cal.set(Calendar.MONTH, date.month);
		cal.set(Calendar.YEAR, date.year);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new KrnDate(cal.getTimeInMillis());
	}

	public static java.sql.Date convertToSqlDate(Date date) {
		if (date == null || (date.day == 0 && date.month == 0 && date.year == 0))
			return null;
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, date.day);
		cal.set(Calendar.MONTH, date.month);
		cal.set(Calendar.YEAR, date.year);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new java.sql.Date(cal.getTimeInMillis());
	}

	public static java.sql.Date convertToSqlDate(java.util.Date date) {
		if (date != null) {
			return new java.sql.Date(date.getTime());
		}
		return null;
	}

	public static Time convertTime(java.util.Date time) {
		if (time == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		return new Time((short) cal.get(Calendar.MILLISECOND), (short) cal.get(Calendar.SECOND),
				(short) cal.get(Calendar.MINUTE), (short) cal.get(Calendar.HOUR_OF_DAY),
				(short) cal.get(Calendar.DAY_OF_MONTH), (short) cal.get(Calendar.MONTH),
				(short) cal.get(Calendar.YEAR));
	}

	public static Time convertTime(java.util.Date time, Calendar cal) {
		if (time == null)
			return null;

		if (cal == null)
			cal = Calendar.getInstance();
		cal.setTime(time);
		return new Time((short) cal.get(Calendar.MILLISECOND), (short) cal.get(Calendar.SECOND),
				(short) cal.get(Calendar.MINUTE), (short) cal.get(Calendar.HOUR_OF_DAY),
				(short) cal.get(Calendar.DAY_OF_MONTH), (short) cal.get(Calendar.MONTH),
				(short) cal.get(Calendar.YEAR));
	}

	public static KrnDate convertTime(Time time) {
		if (time == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, time.msec);
		cal.set(Calendar.SECOND, time.sec);
		cal.set(Calendar.MINUTE, time.min);
		cal.set(Calendar.HOUR_OF_DAY, time.hour);
		cal.set(Calendar.DAY_OF_MONTH, time.day);
		cal.set(Calendar.MONTH, time.month);
		cal.set(Calendar.YEAR, time.year);
		return new KrnDate(cal.getTimeInMillis());
	}

	public static Timestamp convertToSqlTime(Time time) {
		if (time == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, time.msec);
		cal.set(Calendar.SECOND, time.sec);
		cal.set(Calendar.MINUTE, time.min);
		cal.set(Calendar.HOUR_OF_DAY, time.hour);
		cal.set(Calendar.DAY_OF_MONTH, time.day);
		cal.set(Calendar.MONTH, time.month);
		cal.set(Calendar.YEAR, time.year);
		return new Timestamp(cal.getTimeInMillis());
	}

	public static java.sql.Time convertToTimeSql(Time time) {
		if (time == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, time.msec);
		cal.set(Calendar.SECOND, time.sec);
		cal.set(Calendar.MINUTE, time.min);
		cal.set(Calendar.HOUR_OF_DAY, time.hour);
		cal.set(Calendar.DAY_OF_MONTH, time.day);
		cal.set(Calendar.MONTH, time.month);
		cal.set(Calendar.YEAR, time.year);
		return new java.sql.Time(cal.getTimeInMillis());
	}

	public static java.sql.Time convertToTimeSql(java.util.Date time) {
		if (time != null) {
			return new java.sql.Time(time.getTime());
		}
		return null;
	}

	public static java.util.Date convertToDate(Time time) {
		if (time == null)
			return null;

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, time.msec);
		cal.set(Calendar.SECOND, time.sec);
		cal.set(Calendar.MINUTE, time.min);
		cal.set(Calendar.HOUR_OF_DAY, time.hour);
		cal.set(Calendar.DAY_OF_MONTH, time.day);
		cal.set(Calendar.MONTH, time.month);
		cal.set(Calendar.YEAR, time.year);
		return cal.getTime();
	}

	public static Timestamp convertToSqlTime(java.util.Date time) {
		if (time != null) {
			return new Timestamp(time.getTime());
		}
		return null;
	}

	public static Record find(SortedSet<Record> recs, long objId, long attrId, long langId, int index) {
		Record res = null;
		Iterator<Record> it = recs.iterator();
		while (index-- >= 0) {
			if (it.hasNext()) {
				res = it.next();
			} else {
				res = null;
				break;
			}
		}
		return res;
	}

	public static Record findByValue(SortedSet<Record> recs, long objId, long attrId, long langId, Object value) {
		if (value != null) {
			for (Record rec : recs) {
				if (value instanceof KrnObject) {
					KrnObject val = (KrnObject) rec.getValue();
					if (val.id == ((KrnObject) value).id)
						return rec;
				} else {
					if (value.equals(rec.getValue())) {
						return rec;
					}
				}
			}
		}
		return null;
	}

	public static SortedSet<AttrRecord> find(SortedSet<AttrRecord> recs, long objId) {
		AttrRecord low = new AttrRecord(objId, 0, 0, 0);
		AttrRecord high = new AttrRecord(objId, Long.MAX_VALUE, Long.MAX_VALUE, Integer.MAX_VALUE);
		return recs.subSet(low, high);
	}

	public static SortedSet<AttrRecord> find(SortedSet<AttrRecord> recs, long objId, long attrId) {
		AttrRecord low = new AttrRecord(objId, attrId, 0, 0);
		AttrRecord high = new AttrRecord(objId, attrId, Long.MAX_VALUE, Integer.MAX_VALUE);
		return recs.subSet(low, high);
	}

	public static Record findLast(SortedSet<Record> recs, long objId, long attrId, long langId) {
		Record low = new AttrRecord(objId, attrId, langId, 0);
		Record high = new AttrRecord(objId, attrId, langId, Integer.MAX_VALUE);
		SortedSet<Record> tset = recs.subSet(low, high);
		return tset.size() > 0 ? tset.last() : null;
	}

	public static ObjectRecord find(SortedSet<ObjectRecord> recs, long classId, KrnObject obj) {
		if (recs.size() > 0) {
			ObjectRecord low = new ObjectRecord(classId, obj);
			SortedSet<ObjectRecord> tset = recs.tailSet(low);
			if (tset.size() > 0) {
				ObjectRecord res = tset.first();
				KrnObject o = (KrnObject) res.getValue();
				if (o != null && o.id == obj.id) {
					return res;
				}
			}
		}
		return null;
	}

	public static <T> int indexOf(T value, T[] array) {
		for (int i = 0; i < array.length; i++) {
			if (equals(value, array[i])) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(int value, int[] array) {
		for (int i = 0; i < array.length; i++) {
			if (value == array[i]) {
				return i;
			}
		}
		return -1;
	}

	public static long indexOf(long value, long[] array) {
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				if (value == array[i]) {
					return i;
				}
			}
		}
		return -1;
	}

	public static String translite(String str) {
		if (str == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder(str.length());
		char ch;
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			sb.append(Constants.letters.containsKey(ch) ? Constants.letters.get(ch) : ch);
		}
		return sb.toString();
	}

	public static String generateName(String str) {
		if (str == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder(str.length());
		char ch;
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			sb.append(Constants.letters.containsKey(ch) ? Constants.letters.get(ch)
					: Constants.nonletters.containsKey(ch) ? Constants.nonletters.get(ch) : ch);
		}
		return sb.toString().replaceAll("_+", "_");
	}

	public static boolean equals(Object a, Object b) {
		return (a == b || (a != null && a.equals(b)));
	}

	public static boolean equals(KrnObject a, KrnObject b) {
		return (a == b || (a != null && b != null && a.id == b.id));
	}

	public static void copy(String src, String dst) throws IOException {
		if (src.equals(dst)) {
			return;
		}
		File fsrc = new File(normalizeInput(src));
		File fdst = new File(normalizeInput(dst));
		copy(fsrc, fdst);
	}

	public static void copy(File src, File dst) throws IOException {
		if (src.equals(dst)) {
			return;
		}
		String dstPath = normalizeInput(dst.getCanonicalPath());
		String srcPath = normalizeInput(src.getCanonicalPath());
		if (dstPath.matches(".+") && srcPath.matches(".+")) {
			FileInputStream is = new FileInputStream(srcPath);
			FileOutputStream os = new FileOutputStream(dstPath);
			writeStream(is, os, Constants.MAX_ARCHIVED_SIZE);
			os.close();
			is.close();
		}
	}
	
    public static File getFreeFile(File dir, String fileName) {
    	File tmpFile = new File(dir, fileName);
    	
    	int i = 1;
    	int ind = fileName.lastIndexOf('.');
    	String ext = ind > -1 ? fileName.substring(ind) : "";
    	String name = ind > -1 ? fileName.substring(0, ind) : fileName; 
    	
    	while (tmpFile.exists() && !tmpFile.delete()) {
    		tmpFile = new File(dir, name + "_" + i++ + ext);
    	}
    	
    	return tmpFile;
    }

	public static void write(byte[] src, File dst) throws IOException {
		String canonicalPath = normalizeInput(dst.getCanonicalPath());
		if (canonicalPath.matches(".+")) {
			FileOutputStream os = new FileOutputStream(canonicalPath);
			os.write(src);
			os.close();
		}
	}

	public static byte[] read(File src) throws IOException {
		String canonicalPath = normalizeInput(src.getCanonicalPath());
		if (canonicalPath.matches(".+")) {
			Path f = Paths.get(canonicalPath);
			InputStream is = Files.newInputStream(f);
			int l = is.available();
            if (l < Constants.MAX_DOC_SIZE) {
                byte[] res = Funcs.readStream(is, Constants.MAX_DOC_SIZE);
                is.close();
                return res;
            } else {
            	is.close();
            	throw new IOException("Превышен допустимый размер документа: " + Constants.MAX_DOC_SIZE);
            }
		}
		return null;
	}

	public static byte[] read(String fileName) throws IOException {
		fileName = new File(normalizeInput(fileName)).getCanonicalPath();
		if (fileName.matches(".+")) {
			Path f = Paths.get(fileName);
			InputStream is = Files.newInputStream(f);
			int l = is.available();
            if (l < Constants.MAX_DOC_SIZE) {
                byte[] res = Funcs.readStream(is, Constants.MAX_DOC_SIZE);
                is.close();
                return res;
            } else {
            	is.close();
            	throw new IOException("Превышен допустимый размер документа: " + Constants.MAX_DOC_SIZE);
            }
		}
		return null;
	}
	
	public static byte[] toByteArray(Object value) throws IOException {
		if (value instanceof File) {
			return read((File) value);
		}
		if (value instanceof byte[]) {
			return (byte[])value;
		}
		return null;
	}

	public static <T> boolean contains(T[] arr, T value) {
		for (T arrValue : arr) {
			if (arrValue == value || (arrValue != null && arrValue.equals(value))) {
				return true;
			}
		}
		return false;
	}

	public static boolean contains(long[] arr, long value) {
		for (long arrValue : arr) {
			if (arrValue == value) {
				return true;
			}
		}
		return false;
	}

	public static String intToStringRus(long y, long rod, long padezh) {
		StringBuffer res = new StringBuffer();

		long i12 = y / 1000000000000L % 1000;
		if (i12 > 0)
			res.append(intToStringRus((int) i12, 4, 0, (int) padezh));

		long i9 = (y - i12 * 1000000000000L) / 1000000000L % 1000;
		if (i9 > 0)
			res.append(intToStringRus((int) i9, 3, 0, (int) padezh));

		long i6 = (y - i9 * 1000000000L) / 1000000L % 1000;
		if (i6 > 0)
			res.append(intToStringRus((int) i6, 2, 0, (int) padezh));

		long i3 = (y - i6 * 1000000L) / 1000L % 1000;
		if (i3 > 0)
			res.append(intToStringRus((int) i3, 1, 1, (int) padezh));

		long i0 = (y - i3 * 1000L) % 1000;
		if (i0 > 0)
			res.append(intToStringRus((int) i0, 0, (int) rod, (int) padezh));
		
		if (i12 == 0 && i9 == 0 && i6 == 0 && i3 == 0 && i0 == 0)
			res.append(intToStringRus((int) i0, 0, (int) rod, (int) padezh));

		return res.toString().trim();
	}

	public static String intToStringKaz(long y) {
		StringBuffer res = new StringBuffer();

		long i12 = y / 1000000000000L % 1000;
		if (i12 > 0)
			res.append(intToStringKaz((int) i12, 4, 0));

		long i9 = (y - i12 * 1000000000000L) / 1000000000L % 1000;
		if (i9 > 0)
			res.append(intToStringKaz((int) i9, 3, 0));

		long i6 = (y - i9 * 1000000000L) / 1000000L % 1000;
		if (i6 > 0)
			res.append(intToStringKaz((int) i6, 2, 0));

		long i3 = (y - i6 * 1000000L) / 1000L % 1000;
		if (i3 > 0)
			res.append(intToStringKaz((int) i3, 1, 1));

		long i0 = (y - i3 * 1000L) % 1000;
		if (i0 > 0)
			res.append(intToStringKaz((int) i0, 0, 0));
		
		if (i12 == 0 && i9 == 0 && i6 == 0 && i3 == 0 && i0 == 0)
			res.append(intToStringKaz((int) i0, 0, 0));

		return res.toString().trim();
	}
	
	public static String intToStringEn (long number) {
		if (number == 0) { return "zero"; }
		String snumber = Long.toString(number);
		String mask = "000000000000";
		DecimalFormat df = new DecimalFormat(mask);
		snumber = df.format(number);
		int billions = Integer.parseInt(snumber.substring(0,3));
		int millions  = Integer.parseInt(snumber.substring(3,6));
		int hundredThousands = Integer.parseInt(snumber.substring(6,9));
		int thousands = Integer.parseInt(snumber.substring(9,12));
		String tradBillions;
		switch (billions) {
		case 0:
		  tradBillions = "";
		  break;
		case 1 :
		  tradBillions = convertLessThanOneThousand(billions)
		  + " billion ";
		  break;
		default :
		  tradBillions = convertLessThanOneThousand(billions)
		  + " billion ";
		}
		String result =  tradBillions;
			
		String tradMillions;
		switch (millions) {
		case 0:
		  tradMillions = "";
		  break;
		case 1 :
		  tradMillions = convertLessThanOneThousand(millions)
		     + " million ";
		  break;
		default :
		  tradMillions = convertLessThanOneThousand(millions)
		     + " million ";
		}
		result =  result + tradMillions;
			
		String tradHundredThousands;
		switch (hundredThousands) {
		case 0:
		  tradHundredThousands = "";
		  break;
		case 1 :
		  tradHundredThousands = "one thousand ";
		  break;
		default :
		  tradHundredThousands = convertLessThanOneThousand(hundredThousands)
		     + " thousand ";
		}
		result =  result + tradHundredThousands;
		String tradThousand;
		tradThousand = convertLessThanOneThousand(thousands);
		result =  result + tradThousand;
		
		return result.replaceAll("^\\s+", "").replaceAll("\\b\\s{2,}\\b", " ");
		}
	
	private static String intToStringRus(int y, int power, int rod, int padezh) {
		StringBuffer res = new StringBuffer();

		int i2 = y / 100;

		switch (i2) {
		case 9:
			switch (padezh) {
			case 1:
				res.append(" девятисот");
				break;
			case 2:
				res.append(" девятистам");
				break;
			case 4:
				res.append(" девятьюстами");
				break;
			case 5:
				res.append(" девятистах");
				break;
			default:
				res.append(" девятьсот");
				break;
			}
			break;
		case 8:
			switch (padezh) {
			case 1:
				res.append(" восьмисот");
				break;
			case 2:
				res.append(" восьмистам");
				break;
			case 4:
				res.append(" восемьюстами");
				break;
			case 5:
				res.append(" восьмистах");
				break;
			default:
				res.append(" восемьсот");
				break;
			}
			break;
		case 7:
			switch (padezh) {
			case 1:
				res.append(" семисот");
				break;
			case 2:
				res.append(" семистам");
				break;
			case 4:
				res.append(" семьюстами");
				break;
			case 5:
				res.append(" семистах");
				break;
			default:
				res.append(" семьсот");
				break;
			}
			break;
		case 6:
			switch (padezh) {
			case 1:
				res.append(" шестисот");
				break;
			case 2:
				res.append(" шестистам");
				break;
			case 4:
				res.append(" шестьюстами");
				break;
			case 5:
				res.append(" шестистах");
				break;
			default:
				res.append(" шестьсот");
				break;
			}
			break;
		case 5:
			switch (padezh) {
			case 1:
				res.append(" пятисот");
				break;
			case 2:
				res.append(" пятистам");
				break;
			case 4:
				res.append(" пятьюстами");
				break;
			case 5:
				res.append(" пятистах");
				break;
			default:
				res.append(" пятьсот");
				break;
			}
			break;
		case 4:
			switch (padezh) {
			case 1:
				res.append(" четырехсот");
				break;
			case 2:
				res.append(" четыремстам");
				break;
			case 4:
				res.append(" четырьмястами");
				break;
			case 5:
				res.append(" четырехстах");
				break;
			default:
				res.append(" четыреста");
				break;
			}
			break;
		case 3:
			switch (padezh) {
			case 1:
				res.append(" трехсот");
				break;
			case 2:
				res.append(" тремстам");
				break;
			case 4:
				res.append(" тремястами");
				break;
			case 5:
				res.append(" трехстах");
				break;
			default:
				res.append(" триста");
				break;
			}
			break;
		case 2:
			switch (padezh) {
			case 1:
				res.append(" двухсот");
				break;
			case 2:
				res.append(" двумстам");
				break;
			case 4:
				res.append(" двумястами");
				break;
			case 5:
				res.append(" двухстах");
				break;
			default:
				res.append(" двести");
				break;
			}
			break;
		case 1:
			switch (padezh) {
			case 1:
			case 2:
			case 4:
			case 5:
				res.append(" ста");
				break;
			default:
				res.append(" сто");
				break;
			}
			break;
		}

		int i1 = (y - i2 * 100) / 10;
		int i0 = y - i2 * 100 - i1 * 10;

		switch (i1) {
		case 9:
			switch (padezh) {
			case 1:
			case 2:
			case 4:
			case 5:
				res.append(" девяноста");
				break;
			default:
				res.append(" девяносто");
				break;
			}
			break;
		case 8:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" восьмидесяти");
				break;
			case 4:
				res.append(" восемьюдесятью");
				break;
			default:
				res.append(" восемьдесят");
				break;
			}
			break;
		case 7:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" семидесяти");
				break;
			case 4:
				res.append(" семьюдесятью");
				break;
			default:
				res.append(" семьдесят");
				break;
			}
			break;
		case 6:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" шестидесяти");
				break;
			case 4:
				res.append(" шестьюдесятью");
				break;
			default:
				res.append(" шестьдесят");
				break;
			}
			break;
		case 5:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" пятидесяти");
				break;
			case 4:
				res.append(" пятьюдесятью");
				break;
			default:
				res.append(" пятьдесят");
				break;
			}
			break;
		case 4:
			switch (padezh) {
			case 1:
			case 2:
			case 4:
			case 5:
				res.append(" сорока");
				break;
			default:
				res.append(" сорок");
				break;
			}
			break;
		case 3:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" тридцати");
				break;
			case 4:
				res.append(" тридцатью");
				break;
			default:
				res.append(" тридцать");
				break;
			}
			break;
		case 2:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" двадцати");
				break;
			case 4:
				res.append(" двадцатью");
				break;
			default:
				res.append(" двадцать");
				break;
			}
			break;
		case 1:
			i0 += 10;
			break;
		}

		switch (i0) {
		case 0:
			if(i2 == 0 && i1 == 0) {
				switch (padezh) {
				case 1:
					res.append(" ноля");
					break;
				case 2:
					res.append(" нолю");
					break;
				case 4:
					res.append(" нолём");
					break;
				case 5:
					res.append(" ноле");
					break;
				default:
					res.append(" ноль");
					break;
				}
			}
			break;
		case 1:
			if (rod == 0) {
				switch (padezh) {
				case 1:
					res.append(" одного");
					break;
				case 2:
					res.append(" одному");
					break;
				case 4:
					res.append(" одним");
					break;
				case 5:
					res.append(" одном");
					break;
				default:
					res.append(" один");
					break;
				}
			} else if (rod == 1) {
				switch (padezh) {
				case 1:
				case 2:
				case 4:
				case 5:
					res.append(" одной");
					break;
				case 3:
					res.append(" одну");
					break;
				default:
					res.append(" одна");
					break;
				}
			} else if (rod == 2) {
				switch (padezh) {
				case 1:
					res.append(" одного");
					break;
				case 2:
					res.append(" одному");
					break;
				case 4:
					res.append(" одним");
					break;
				case 5:
					res.append(" одном");
					break;
				default:
					res.append(" одно");
					break;
				}
			} else if (rod == 3) {
				switch (padezh) {
				case 1:
				case 5:
					res.append(" одних");
					break;
				case 2:
					res.append(" одним");
					break;
				case 4:
					res.append(" одними");
					break;
				default:
					res.append(" одни");
					break;
				}
			}
			break;
		case 2:
			switch (padezh) {
			case 1:
			case 5:
				res.append(" двух");
				break;
			case 2:
				res.append(" двум");
				break;
			case 4:
				res.append(" двумя");
				break;
			default:
				if (rod == 0 || rod == 2) {
					res.append(" два");
				} else if (rod == 1) {
					res.append(" две");
				} else if (rod == 3) {
					res.append(" двое");
				}
				break;
			}
			break;
		case 3:
			switch (padezh) {
			case 1:
			case 5:
				res.append(" трех");
				break;
			case 2:
				res.append(" трем");
				break;
			case 4:
				res.append(" тремя");
				break;
			default:
				if (rod == 3) {
					res.append(" трое");
				} else {
					res.append(" три");
				}
				break;
			}
			break;
		case 4:
			switch (padezh) {
			case 1:
			case 5:
				res.append(" четырех");
				break;
			case 2:
				res.append(" четырем");
				break;
			case 4:
				res.append(" четырьмя");
				break;
			default:
				if (rod == 3) {
					res.append(" четверо");
				} else {
					res.append(" четыре");
				}
				break;
			}
			break;
		case 5:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" пяти");
				break;
			case 4:
				res.append(" пятью");
				break;
			default:
				res.append(" пять");
				break;
			}
			break;
		case 6:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" шести");
				break;
			case 4:
				res.append(" шестью");
				break;
			default:
				res.append(" шесть");
				break;
			}
			break;
		case 7:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" семи");
				break;
			case 4:
				res.append(" семью");
				break;
			default:
				res.append(" семь");
				break;
			}
			break;
		case 8:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" восеми");
				break;
			case 4:
				res.append(" восемью");
				break;
			default:
				res.append(" восемь");
				break;
			}
			break;
		case 9:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" девяти");
				break;
			case 4:
				res.append(" девятью");
				break;
			default:
				res.append(" девять");
				break;
			}
			break;
		case 10:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" десяти");
				break;
			case 4:
				res.append(" десятью");
				break;
			default:
				res.append(" десять");
				break;
			}
			break;
		case 11:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" одиннадцати");
				break;
			case 4:
				res.append(" одиннадцатью");
				break;
			default:
				res.append(" одиннадцать");
				break;
			}
			break;
		case 12:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" двенадцати");
				break;
			case 4:
				res.append(" двенадцатью");
				break;
			default:
				res.append(" двенадцать");
				break;
			}
			break;
		case 13:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" тринадцати");
				break;
			case 4:
				res.append(" тринадцатью");
				break;
			default:
				res.append(" тринадцать");
				break;
			}
			break;
		case 14:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" четырнадцати");
				break;
			case 4:
				res.append(" четырнадцатью");
				break;
			default:
				res.append(" четырнадцать");
				break;
			}
			break;
		case 15:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" пятнадцати");
				break;
			case 4:
				res.append(" пятнадцатью");
				break;
			default:
				res.append(" пятнадцать");
				break;
			}
			break;
		case 16:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" шестнадцати");
				break;
			case 4:
				res.append(" шестнадцатью");
				break;
			default:
				res.append(" шестнадцать");
				break;
			}
			break;
		case 17:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" семнадцати");
				break;
			case 4:
				res.append(" семнадцатью");
				break;
			default:
				res.append(" семнадцать");
				break;
			}
			break;
		case 18:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" восемнадцати");
				break;
			case 4:
				res.append(" восемнадцатью");
				break;
			default:
				res.append(" восемнадцать");
				break;
			}
			break;
		case 19:
			switch (padezh) {
			case 1:
			case 2:
			case 5:
				res.append(" девятнадцати");
				break;
			case 4:
				res.append(" девятнадцатью");
				break;
			default:
				res.append(" девятнадцать");
				break;
			}
			break;
		}

		res.append(powerToString(i0, power));

		return res.toString();
	}

	private static String intToStringKaz(int y, int power, int rod) {
		StringBuffer res = new StringBuffer();

		int i2 = y / 100;

		switch (i2) {
		case 9:
			res.append(" то\u0493ыз ж\u04afз");
			break;
		case 8:
			res.append(" сегіз ж\u04afз");
			break;
		case 7:
			res.append(" жеті ж\u04afз");
			break;
		case 6:
			res.append(" алты ж\u04afз");
			break;
		case 5:
			res.append(" бес ж\u04afз");
			break;
		case 4:
			res.append(" т\u04e9рт ж\u04afз");
			break;
		case 3:
			res.append(" \u04afш ж\u04afз");
			break;
		case 2:
			res.append(" екі ж\u04afз");
			break;
		case 1:
			res.append(" бір ж\u04afз");
			break;
		}

		int i1 = (y - i2 * 100) / 10;
		int i0 = y - i2 * 100 - i1 * 10;

		switch (i1) {
		case 9:
			res.append(" то\u049bсан");
			break;
		case 8:
			res.append(" сексен");
			break;
		case 7:
			res.append(" жетпіс");
			break;
		case 6:
			res.append(" алпыс");
			break;
		case 5:
			res.append(" елу");
			break;
		case 4:
			res.append(" \u049bыры\u049b");
			break;
		case 3:
			res.append(" отыз");
			break;
		case 2:
			res.append(" жиырма");
			break;
		case 1:
			res.append(" он");
			break;
		}

		switch (i0) {
		case 9:
			res.append(" то\u0493ыз");
			break;
		case 8:
			res.append(" сегіз");
			break;
		case 7:
			res.append(" жеті");
			break;
		case 6:
			res.append(" алты");
			break;
		case 5:
			res.append(" бес");
			break;
		case 4:
			res.append(" т\u04e9рт");
			break;
		case 3:
			res.append(" \u04afш");
			break;
		case 2:
			res.append(" екі");
			break;
		case 1:
			res.append(" бір");
			break;
		case 0:
			if(i2 == 0 && i1 == 0) {
				res.append(" н\u04e9л");
				break;
			}
			break;
		}

		res.append(powerToStringKaz(i0, power));

		return res.toString();
	}
	
	private static String convertLessThanOneThousand(int number) {
		
		String soFar;
		if (number % 100 < 20){
		soFar = numNames[number % 100];
		number /= 100;
		}
		else {
		soFar = numNames[number % 10];
		number /= 10;
		soFar = tensNames[number % 10] + soFar;
		number /= 10;
		}
		if (number == 0) return soFar;
		return numNames[number] + " hundred" + soFar;
	}

	private static String powerToString(int y, int power) {
		StringBuffer res = new StringBuffer();

		switch (power) {
		case 0:
			break;
		case 1:
			switch (y) {
			case 1:
				res.append(" тысяча");
				break;
			case 2:
			case 3:
			case 4:
				res.append(" тысячи");
				break;
			default:
				res.append(" тысяч");
				break;
			}
			break;
		case 2:
			switch (y) {
			case 1:
				res.append(" миллион");
				break;
			case 2:
			case 3:
			case 4:
				res.append(" миллиона");
				break;
			default:
				res.append(" миллионов");
				break;
			}
			break;
		case 3:
			switch (y) {
			case 1:
				res.append(" миллиард");
				break;
			case 2:
			case 3:
			case 4:
				res.append(" миллиарда");
				break;
			default:
				res.append(" миллиардов");
				break;
			}
			break;
		case 4:
			switch (y) {
			case 1:
				res.append(" триллион");
				break;
			case 2:
			case 3:
			case 4:
				res.append(" триллиона");
				break;
			default:
				res.append(" триллионов");
				break;
			}
			break;
		}
		return res.toString();
	}

	private static String powerToStringKaz(int y, int power) {
		StringBuffer res = new StringBuffer();

		switch (power) {
		case 0:
			break;
		case 1:
			res.append(" мы\u04a3");
			break;
		case 2:
			res.append(" миллион");
			break;
		case 3:
			res.append(" миллиард");
			break;
		case 4:
			res.append(" триллион");
			break;
		}
		return res.toString();
	}

	public static String getIds(long[] arr) {
		String res = "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++)
			sb.append("," + arr[i]);
		if (sb.length() > 0)
			res = sb.toString().substring(1);
		return res;
	}

	private static LdapContext connectCertLDAP(Map<String, String> env) {
		Hashtable<String, String> props = new Hashtable<String, String>();
		props.putAll(env);
		try {
			return new InitialLdapContext(props, null);
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static List<File> getCertificatesFromLDAP(Map<String, String> props, String dnName, String fio) {
		try {
			LdapContext ctx = connectCertLDAP(props);
			SearchControls ctls = new SearchControls();
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> answer = ctx.search(dnName, "cn=" + fio, ctls);
			List<File> fs = getDigiSignCertificate(answer);
			ctx.close();
			return fs;
		} catch (Exception e) {
			System.out.println("Exception: " + e + "\n Possible, there were enter not correct data");
			return Collections.emptyList();
		}
	}

	private static List<File> getDigiSignCertificate(NamingEnumeration<SearchResult> answer) {
		List<File> fs = new ArrayList<File>();
		File dir = Funcs.getCanonicalFile("certs");
		dir.mkdirs();
		try {
			if (answer.hasMoreElements()) {
				while (answer.hasMore()) {
					SearchResult sr = answer.next();
					Attributes pp = sr.getAttributes();
					for (Enumeration e = pp.getAll(); e.hasMoreElements();) {
						Attribute attr = (Attribute) e.nextElement();
						String attrID = attr.getID();
						try {
							if (attrID.equals("userCertificate")) {
								for (int i = 0; i < attr.size(); i++) {
									byte[] csd_b = (byte[]) attr.get(i);
									ByteArrayInputStream bis = new ByteArrayInputStream(csd_b);
									CertificateFactory cf1 = CertificateFactory.getInstance("X.509");
									Collection c1 = cf1.generateCertificates(bis);
									Iterator i1 = c1.iterator();
									while (i1.hasNext()) {
										X509Certificate cert = (X509Certificate) i1.next();
										if (cert.getKeyUsage()[0]) {
											java.security.Principal principal = cert.getSubjectDN();
											File f = File.createTempFile("cert", ".cer", dir);
											FileOutputStream fos = new FileOutputStream(f);
											fos.write(cert.getEncoded());
											fos.close();
											f.deleteOnExit();
											System.out.println("SubjectDN: " + principal.getName() + "\n\n");

											fs.add(f);
										}
									}
								}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fs;
	}

	public static boolean isAssignableFrom(Class<?> to, Class<?> from) {
		boolean res = to.isAssignableFrom(from);
		if (!res && to.isPrimitive()) {
			if (to.equals(java.lang.Boolean.TYPE))
				res = Boolean.class.isAssignableFrom(from);
			else if (to.equals(java.lang.Character.TYPE))
				res = Character.class.isAssignableFrom(from);
			else if (to.equals(java.lang.Byte.TYPE))
				res = Byte.class.isAssignableFrom(from);
			else if (to.equals(java.lang.Short.TYPE))
				res = Short.class.isAssignableFrom(from);
			else if (to.equals(java.lang.Integer.TYPE))
				res = Integer.class.isAssignableFrom(from);
			else if (to.equals(java.lang.Long.TYPE))
				res = Long.class.isAssignableFrom(from);
			else if (to.equals(java.lang.Float.TYPE))
				res = Float.class.isAssignableFrom(from);
			else if (to.equals(java.lang.Double.TYPE))
				res = Double.class.isAssignableFrom(from);
		}
		return res;
	}

	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0;
	}

	public static boolean isDigits(String s) {
		if (s == null || s.length() == 0) {
			return false;
		}
		char[] chs = s.toCharArray();
		for (char ch : chs) {
			if (!Character.isDigit(ch)) {
				return false;
			}
		}
		return true;
	}

	public static String sanitizeUsername(String s) {
		return s;
/*		if (s != null)
			return Funcs.normalizeInput(s).replaceAll("\\/", "").replaceAll("..", "").replaceAll("'", "").replaceAll("\"", "").replaceAll("`", "")
        			.replaceAll("\\:", "").replaceAll("\\*", "").replaceAll("\\?", "").replaceAll("<", "").replaceAll(">", "").replaceAll("\\|", "")
        			.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\{", "").replaceAll("\\}", "")
        			.replaceAll("\\+", "").replaceAll("=", "").replaceAll("\\\r", "").replaceAll("\\\n", "");
		else
			return null;
*/	}

	/* Имя элемента OR3 - название папки, процесса, пользователя, фильтра и т.д. А также название узла в TreeCtrl и подобных. */
	public static String sanitizeElementName(String s) {
		if (s != null) {
			s = normalizeInput(s);
        	
			if (s.length() > Constants.MAX_ELEMENT_NAME)
				s = s.substring(0, Constants.MAX_ELEMENT_NAME);

			s = s.replace("\\", "").replace("/", "").replace("<", "&lt;").replace(">", "&gt;").replace("\r", "").replace("\n", " ");
        	return s;
		} 
		return null;
	}

	public static String sanitizeMessage(String s) {
		if (s != null) {
			s = normalizeInput(s);

			if (s.length() > Constants.MAX_DIALOG_MSG_SIZE)
				s = s.substring(0, Constants.MAX_DIALOG_MSG_SIZE);
			
			s = s.replace("<", "&lt;").replace(">", "&gt;");
        	return s;
		} 
		return null;
	}

	public static String sanitizeEmail(String s) {
		if (s != null)
			return StringEscapeUtils.escapeHtml(Funcs.normalizeInput(s)).replaceFirst("&lt;", "<").replaceFirst("&gt;", ">");
		else
			return null;
	}

	public static String sanitizeSQL(String s) {
		if (s != null)
			return Funcs.normalizeInput(s).replaceAll("'", "").replaceAll("\"", "").replaceAll("`", "").replaceAll("\\]", "")
					.replaceAll("\\[", "").replaceAll("\\\r", "").replaceAll("\\\n", "");
		else
			return null;
	}

	public static String sanitizeSQL2(String s) {
		if (s != null)
			return Funcs.normalizeInput(s).replace("'", "").replace("\"", "").replace("`", "").replace("]", "")
					.replace("[", "").replace("\r", "").replace("\n", "");
		else
			return null;
	}

	public static String sanitizeLDAP(String s) {
		if (s != null)
			return Funcs.normalizeInput(s).replace("'", "").replace("\"", "").replace("`", "").replace("]", "")
					.replace("*", "").replace("?", "").replace("<", "").replace(">", "").replace("|", "")
					.replace("[", "").replace("(", "").replace(")", "").replace("{", "").replace("}", "")
					.replace("\r", "").replace("\n", "");
		else
			return null;
	}

	public static File getCanonicalFile(String name) {
		name = normalizeInput(name);
		if (name.matches(".+")) {
			return new File(name);/*
			String[] paths = name.split("\\\\|\\/");
			int len = paths.length;
			
			StringBuilder res = new StringBuilder();
			if (len > 0 && len < 500) {
				for (int i = 0; i < len; i++) {
					String path = paths[i].replace("..", "").replace("\\", "").replace("/", "");
					if (path.length() > 0 && path.matches(".+")) {
						if (i > 0)
							res.append(File.separator);
						
						res.append(path);
					}
				}
				File f = getCanonicalFile(new File(res.toString()));
				try {
					if (f.getAbsolutePath().matches(".+")
//							&& f.getCanonicalPath().equals(f.getAbsolutePath())
							)
						return f;
				} catch (Exception e) {
					log.error(e, e);
				}
			}*/
		}
		return null;
	}

	public static File getCanonicalFile(File f) {
		if (f != null) {
			try {
				f = f.getCanonicalFile();
				if (f.getPath().matches(".+") && f.getCanonicalPath().equals(f.getAbsolutePath()))
					return f;
			} catch (IOException e) {
				log.error("File '" + f.getAbsolutePath() + "' is not canonical!!!");
				log.error(e, e);
			}
		}
		return null;
	}

	public static File getCanonicalFile(File f, String child) {
		if (f != null) {
			try {
				f = getCanonicalFile(f);
				if (f.getAbsolutePath().matches(".+")) {
					child = sanitizeFileName(child);
					if (child.matches(".+")) {
						f = new File(f, child);
						if (f.getCanonicalPath().equals(f.getAbsolutePath()))
							return f.getCanonicalFile();
					}
				}
			} catch (IOException e) {
				log.error("File '" + f.getAbsolutePath() + "' is not canonical!!!");
				log.error(e, e);
			}
		}
		return null;
	}

	public static String getCanonicalName(File f) {
		if (f != null) {
			try {
				String path = f.getCanonicalPath();
				if (path.matches(".+"))
					return path;
			} catch (IOException e) {
				log.error("File '" + f.getAbsolutePath() + "' is not canonical!!!");
				log.error(e, e);
			}
		}
		return null;
	}

	public static String sanitizeFileName(String s) {
		if (s != null)
			return Funcs.normalizeInput(s).replace("\\", "").replace("/", "").replace("..", "").replace(":", "").replace("*", "").replace("?", "")
        			.replace("\"", "").replace("<", "").replace(">", "").replace("|", "").replace("\r", "").replace("\n", "");
		else
			return null;
	}

	public static String sanitizeHashUrl(String s) {
		if (s != null)
			return StringEscapeUtils.escapeHtml(Funcs.normalizeInput(s)).replace("&amp;", "&");
		else
			return null;
	}

	public static String sanitizeHtml(String s) {
		if (s != null)
			return StringEscapeUtils.escapeHtml(Funcs.normalizeInput(s));
		else
			return null;
	}
	
	public static String encodeHtml(String s) {
		if (s != null)
			return ESAPI.encoder().encodeForHTML(Funcs.normalizeInput(s));
		else
			return null;
	}

	public static boolean checkUID(String s) {
		if (s != null) {
			for (char ch : s.toCharArray()) {
				if (!Character.isDigit(ch) && ch != '.')
					return false;
			}
			return true;
		} else
			return false;
	}

	public static synchronized String sanitizeURL(String url) {
		if (allConnectionURLs == null) {
			allConnectionURLs = new HashMap<String, String>();
			// Oracle Real
			allConnectionURLs.put("jdbc:oracle:thin:@192.168.13.66:1521:kyzmet",
					"jdbc:oracle:thin:@192.168.13.66:1521:kyzmet");
			// Ekyzmet SYS
			allConnectionURLs.put("jdbc:mysql://192.168.13.61:3306/e_kyz_resource",
					"jdbc:mysql://192.168.13.61:3306/e_kyz_resource");
			// НУЦ OCSP
			allConnectionURLs.put("http://ocsp.pki.gov.kz/", "http://ocsp.pki.gov.kz/");
			allConnectionURLs.put("http://ocsp.pki-test.halykbank.kz", "http://ocsp.pki-test.halykbank.kz");
			allConnectionURLs.put("http://ocsp.pki.halykbank.kz", "http://ocsp.pki.halykbank.kz");
			// cache DB url
			allConnectionURLs.put("jdbc:Cache://127.0.0.1:1972/ODS100", "jdbc:Cache://127.0.0.1:1972/ODS100");
		}
		return allConnectionURLs.get(url);
	}

	public static boolean isRegularFile(Path path) {
		try {
			BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
			return attr.isRegularFile();
		} catch (IOException e) {
			log.error(e, e);
		}
			 
		return false;
	}
	
	public static String sanitizeXml(String str) {
		str = normalizeInput(str);
		if (str == null)
			return "";
		StringBuilder res = new StringBuilder();
		char[] arr = str.toCharArray();
		for (int i = 0; i < arr.length; ++i) {
			switch (arr[i]) {
			case '<':
				res.append("&lt;");
				break;
			case '>':
				res.append("&gt;");
				break;
			case '&':
				res.append("&amp;");
				break;
			case '\'':
				res.append("&#39;");
				break;
			case '/':
				res.append("&#47;");
				break;
			case '"':
				res.append("&quot;");
				break;
			default:
				res.append(arr[i]);
			}
		}
		return res.toString();
	}

	public static boolean isInSecureDir(Path file, UserPrincipal user, int symlinkDepth) {
/*		if (!file.isAbsolute()) {
			file = file.toAbsolutePath();
		}
		if (symlinkDepth <= 0) {
			// Too many levels of symbolic links
			return false;
		}

		// Get UserPrincipal for specified user and superuser
		FileSystem fileSystem = Paths.get(file.getRoot().toString()).getFileSystem();
		UserPrincipalLookupService upls = fileSystem.getUserPrincipalLookupService();
		
		UserPrincipal root = null;
		try {
			root = upls.lookupPrincipalByName("root");
			if (user == null) {
				user = upls.lookupPrincipalByName(Funcs.getSystemProperty("user.name"));
			}
			if (root == null || user == null) {
				return false;
			}
		} catch (IOException x) {
			return false;
		}

		// If any parent dirs (from root on down) are not secure,
		// dir is not secure
		for (int i = 1; i <= file.getNameCount(); i++) {
			Path partialPath = Paths.get(file.getRoot().toString(), file.subpath(0, i).toString());

			try {
				if (Files.isSymbolicLink(partialPath)) {
					if (!isInSecureDir(Files.readSymbolicLink(partialPath), user, symlinkDepth - 1)) {
						// Symbolic link, linked-to dir not secure
						return false;
					}
				} else {
					UserPrincipal owner = Files.getOwner(partialPath);
					
					if (!user.equals(owner) && !root.equals(owner)) {
						// dir owned by someone else, not secure
						return false;
					}
					PosixFileAttributes attr = Files.readAttributes(partialPath, PosixFileAttributes.class);
					Set<PosixFilePermission> perms = attr.permissions();
					if (perms.contains(PosixFilePermission.GROUP_WRITE)
							|| perms.contains(PosixFilePermission.OTHERS_WRITE)) {
						// Someone else can write files, not secure
						return false;
					}
				}
			} catch (IOException x) {
				return false;
			}
		}
*/		return true;
	}
	
	public static File createTempFile(String prefix, String suffix) {
		try {
			File f = Files.createTempFile(prefix, suffix).toFile();
			f.setExecutable(false);
			f.setWritable(true, true);
			f.setExecutable(true, true);
			return f;
		} catch (IOException e) {
			System.out.println("Can not create temp file with prefix = " + prefix + ", suffix = " + suffix);
		}
		return null;
	}
	
	public static File createTempFile(String prefix, String suffix, File dir) {
		try {
			File f = Files.createTempFile(dir.toPath(), prefix, suffix).toFile();
			f.setExecutable(false);
			f.setWritable(true, true);
			f.setExecutable(true, true);
			return f;
		} catch (IOException e) {
			System.out.println("Can not create temp file with prefix = " + prefix + ", suffix = " + suffix + ", in dir = " + dir.getAbsolutePath());
		}
		return null;
	}

	public static long normalizeInput(long l) {
		return l > 0 ? l : 0;
	}
	
	public static int checkInt(int i, int max) {
		i = (i > max) ? max : i;
		return i;
	}

	public static float checkFloat(float i, float min, float max) {
		if (i > max)
			return max;
		else if (i < min)
			return min;
		else
			return i;
	}

	public static int checkInt2(int i) {
		return i > Integer.MIN_VALUE ? (i < Integer.MAX_VALUE ? i : Integer.MAX_VALUE) : Integer.MIN_VALUE;
	}

	public static String normalizeInput(String input) {
		return input;
//		return Normalizer.normalize(input, Form.NFKC);
/*		if (input != null) {
			return Normalizer.normalize(input, Form.NFKC);
			//if (input.matches(".+"))
			//		return input;
		}
		return null;
*/
//		return (input != null) ? Normalizer.normalize(input.replace("№", "_|No|="), Form.NFKC).replace("_|No|=", "№") : null;
	}

	public static byte[] normalizeInput(byte[] input, String encoding) {
		return input;
/*		if (input == null) return null;
		if (encoding == null) encoding = "UTF-8";
		try {
			return Normalizer.normalize(new String(input, encoding), Form.NFKC).getBytes(encoding);
		} catch (UnsupportedEncodingException e) {
			log.error(e, e);
		}
		return null;
*/	}

	public static List<Path> fileList(Path dir, String pattern) {
		if (pattern == null) pattern = "*";
        List<Path> fileNames = new ArrayList<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir, pattern)) {
            for (Path path : directoryStream) {
                fileNames.add(path);
            }
        } catch (IOException ex) {}
        return fileNames;
    }
	
	public static Path getChild(Path dir, String name) {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir, name)) {
            for (Path path : directoryStream) {
                return path;
            }
        } catch (IOException ex) {}
        return null;
    }
	
	public static void writeStream(InputStream is, OutputStream os, long max) throws IOException {
        if (is.available() > max)
        	throw new IOException("Превышен допустимый размер сообщения/файла/изображения: " + max);
        
        byte[] buffer = new byte[4096];
		int len = 0;
		long MAX_ITERS = 1 + max / buffer.length;
		
		for (int i = 0; i < MAX_ITERS; i++) {
			len = is.read(buffer);
			if (len > -1)
				os.write(buffer, 0, len);
			else
				break;
		}
	}

	public static byte[] readStream(InputStream is, long max) throws IOException {
		int length = is.available();
        if (length > max)
        	throw new IOException("Превышен допустимый размер сообщения/файла/изображения: " + max);
        
        byte[] buffer = new byte[4096];
		int l = 0;
		long MAX_ITERS = 1 + max / buffer.length;
        ByteArrayOutputStream bos = new ByteArrayOutputStream(buffer.length);
		
		for (int i = 0; i < MAX_ITERS; i++) {
			l = is.read(buffer);
			if (l > -1)
				bos.write(buffer, 0, l);
			else
				break;
		}
		bos.close();
		return bos.toByteArray();
	}
	
	public static final String getSystemProperty(String prop) {
		return getValidatedSystemProperty(prop, null);
	}
	
	public static final String getParameter(ServletRequest req, String name) {
		String val = req.getParameter(Normalizer.normalize(name, Form.NFKC));
		if (val != null) {
			return val;
			//return Normalizer.normalize(val.replace("№", "_|No|="), Form.NFKC).replace("_|No|=", "№");
		}
		return null;
	}

	public static final String getValidatedParameter(ServletRequest req, String name) {
		try {
			name = Normalizer.normalize(name, Form.NFKC);
			if (name.matches(".+")) {
				String res = req.getParameter(name);
				if (res != null) {
					res = Normalizer.normalize(res, Form.NFKC);
					if (res.matches(".+"))
						return res;
				}
			}
		} catch (Throwable e) {
			log.error("Couldn't read parameter '" + name + "'");
			log.debug(e, e);
		}
		
		return null;
	}

	public static final String getSystemProperty(String prop, String defValue) {
		return getValidatedSystemProperty(prop, defValue);
	}

	public static final String getValidatedSystemProperty(String prop) {
		return getValidatedSystemProperty(prop, null);
	}

	public static final String getValidatedSystemProperty(String prop, String defValue) {
		try {
			prop = Normalizer.normalize(prop, Form.NFKC);
			if (prop.matches(".+")) {
				String res = System.getProperty(prop);
				if (res != null) {
					res = Normalizer.normalize(res, Form.NFKC);
					if (res.matches(".+"))
						return res;
				}
			}
		} catch (Throwable e) {
			log.error("Couldn't read property '" + prop + "'");
			log.debug(e, e);
		}
		
		return defValue;
	}

	public static final String validate(String str) {
		return str;
/*		if (str != null && str.matches("[\\S\\s]+")) 
			return str;
		else
			return null;
*/	}

	public static final boolean isValid(String str) {
		return (str != null && str.matches("[\\S\\s]+"));
	}

	public static final int add(int[] x) {
		if (x.length == 2)
			return add(x[0], x[1]);
		else {
			int[] y = new int[x.length - 1];
			System.arraycopy(x, 0, y, 0, x.length - 1);
			return add(add(y), x[x.length - 1]);
		}
	}
	
	public static final int add(int x, int y) {
		if (y > 0 ? x > Integer.MAX_VALUE - y : x < Integer.MIN_VALUE - y)
			return (int) add((long)x, (long)y);
		return x + y;
	}
	
	public static final long add(long x, long y) {
		if (y > 0 ? x > Long.MAX_VALUE - y : x < Long.MIN_VALUE - y)
			throw new ArithmeticException("Long overflow");
		return x + y;
	}

	public static final int sub(int x, int y) {
		if (y > 0 ? x < Integer.MIN_VALUE + y : x > Integer.MAX_VALUE + y)
		    throw new ArithmeticException("Integer overflow");
		  return x - y;
	}
	
	public static final int mul(int x, int y) {
		if (y > 0 ? x > Integer.MAX_VALUE / y || x < Integer.MIN_VALUE / y
				: (y < -1 ? x > Integer.MIN_VALUE / y || x < Integer.MAX_VALUE / y
						: y == -1 && x == Integer.MIN_VALUE)) {
			return (int) mul((long)x, (long)y);
		}
		return x * y;
	}

	public static final long mul(long x, long y) {
		if (y > 0 ? x > Long.MAX_VALUE / y || x < Long.MIN_VALUE / y
				: (y < -1 ? x > Long.MIN_VALUE / y || x < Long.MAX_VALUE / y
						: y == -1 && x == Long.MIN_VALUE)) {
			throw new ArithmeticException("Long overflow");
		}
		return x * y;
	}
	
	public static void logException(Log log, String msg) {
		log.error(Funcs.encodeHtml(msg));
	}

	public static void logException(Log log, Throwable e) {
		logException(log, e, null);
	}
	
	public static void logException(Log log, Throwable e, String msg) {
		if (log != null) {
			if (msg != null && msg.length() > 0)
				log.error(Funcs.encodeHtml(msg));

			if (logLevel.equals("ALL"))
				log.error(e, e);
		}
	}
	
	public static void closeQuietly(InputStream s) {
		if (s != null) {
			try {
				s.close();
			} catch (Throwable e) {}
		}
	}
	
	public static void closeQuietly(OutputStream s) {
		if (s != null) {
			try {
				s.close();
			} catch (Throwable e) {}
		}
	}
	
	public static boolean isEmpty(List<?> l) {
		return l == null || l.size() == 0;
	}

}