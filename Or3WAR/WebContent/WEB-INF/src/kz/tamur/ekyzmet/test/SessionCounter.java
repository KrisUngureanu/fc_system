package kz.tamur.ekyzmet.test;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionCounter implements ServletRequestListener, ServletContextListener, HttpSessionListener {

	private static final String ATTRIBUTE_NAME = "kz.tamur.ekyzmet.test.SessionCounter";
	private Map<String, String> ipByIin = new HashMap<String, String>();
	
	@Override
	public void sessionCreated(HttpSessionEvent arg0) {
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		String iin = (String)event.getSession().getAttribute("iin");
		if (iin != null)
			ipByIin.remove(iin);
	}

    @Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
        event.getServletContext().setAttribute(ATTRIBUTE_NAME, this);
	}

	@Override
	public void requestDestroyed(ServletRequestEvent arg0) {
	}

	@Override
	public void requestInitialized(ServletRequestEvent event) {
	}

    public static SessionCounter getInstance(ServletContext context) {
        return (SessionCounter) context.getAttribute(ATTRIBUTE_NAME);
    }

    public boolean addIin(String iin, String ip) {
    	String par = ipByIin.get(iin);
    	if (par == null || par.equals(ip)) {
    		ipByIin.put(iin, ip);
    		return true;
    	}
    	return false;
    }

    public void removeIin(String iin) {
    	ipByIin.remove(iin);
    }
}
