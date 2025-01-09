package kz.tamur.shep.common;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class JAXBContextBuilder {
	
	private static Map<String, JAXBContext> packageContextMap = new HashMap<String, JAXBContext>();
	
	public static synchronized JAXBContext buildContext(String packageName) throws JAXBException {
		JAXBContext context = packageContextMap.get(packageName);
		if (context == null) {
			context = JAXBContext.newInstance(packageName);
			packageContextMap.put(packageName, context);
		}
		return context;
	}
}