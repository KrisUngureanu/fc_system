import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jcifs.http.NtlmHttpFilter;
import jcifs.smb.NtlmPasswordAuthentication;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;

/**
 * Servlet implementation class GbdulNtlmFilter
 */
public class GbdulNtlmFilter extends NtlmHttpFilter {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + GbdulNtlmFilter.class.getName());
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
    	try {
    		super.doFilter(request, response, chain);
    	} catch (Exception e) {
    		Funcs.logException(log, e, "NTLM Filter exception");
    	}
    	chain.doFilter(request, response);
	}

	@Override
	protected NtlmPasswordAuthentication negotiate(HttpServletRequest arg0,
			HttpServletResponse arg1, boolean arg2) throws IOException,
			ServletException {
		NtlmPasswordAuthentication auth = super.negotiate(arg0, arg1, arg2);
		
		log.info(auth);
		if (auth != null) {
			log.info(auth.getDomain());
			log.info(auth.getName());
			//log.info(auth.getPassword());
			log.info(auth.getUsername());
			log.info(auth.getClass());
		}	
		return auth;
	}
}
