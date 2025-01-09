package kz.tamur.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class ComUtil {
	
	public static Variant getProperty(Dispatch dsp, String path) {
		Pattern p = Pattern.compile("([^(]+)(?:\\((([^)])+)\\))?"); 
		String[] pathElements = path.split("\\.");
		Variant v = null;
		for (int i = 0; i < pathElements.length; i++) {
			String pathElement = pathElements[i];
			Matcher m = p.matcher(pathElement);
			if (m.matches()) {
				String name = m.group(1);
				String arg0 = m.group(2);
				if (arg0 != null) {
					v = Dispatch.call(dsp, name, new Variant(arg0));
				} else {
					v = Dispatch.call(dsp, name);
				}
				if (i < pathElements.length - 1) {
					dsp = v.toDispatch();
				}
			}
		}
		return v;
	}
}
