package kz.tamur.web.controller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.or3ee.common.UserSession;

public class ConfigFilter implements Filter {
	private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ConfigFilter.class);
	private String[] excludeUrls = new String[0];

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
/*    	if (request instanceof HttpServletRequest) {
	    	String contextPath = ((HttpServletRequest)request).getContextPath();
	    	
			boolean rewriteUrl = true;
    		String uri = ((HttpServletRequest)request).getRequestURI();
    		uri = uri.substring(contextPath.length());
    		for (String excludeUrl : excludeUrls) {
    			if (uri.startsWith(excludeUrl)) {
    				rewriteUrl = false;
    				break;
    			}
    		}
    		
    		if (rewriteUrl) {
				int end = uri.indexOf("/", 1);
				if (end == -1) end = uri.length();
				
				String dsName = uri.substring(1, end);
				((HttpServletRequest) request).getSession().setAttribute("dsName", dsName);
				log.info("dsName = " + dsName);
				
				String newUri = uri.substring(dsName.length() + 1);
				log.info("newUri = " + newUri);
				request.getRequestDispatcher(newUri).include(request, response);
				return;
    		}
    	}
*/    	
        chain.doFilter(request, response);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        String temp = filterConfig.getInitParameter("excludeUrls");
        if (temp != null) {
        	excludeUrls = temp.split(";");
        }
    }

    public void destroy() {}
}