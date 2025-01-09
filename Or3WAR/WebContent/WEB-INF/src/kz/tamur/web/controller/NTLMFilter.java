package kz.tamur.web.controller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jcifs.ntlmssp.Type3Message;
import kz.gov.pki.kalkan.util.encoders.Base64;

public class NTLMFilter implements Filter {
	
	private static final Log log = LogFactory.getLog(NTLMFilter.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		
		// pass the request along the filter chain
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		log.info("doFilter auth = " + request.getParameter("sso"));

		boolean needNtlmAuth = "ntlm".equals(request.getParameter("sso"));

		if (needNtlmAuth) {
			String username = null;
			// first, get the user agent
			String useragent = request.getHeader("user-agent");
			log.info("useragent = " + useragent);

			HttpSession s = request.getSession(true);
	
			try {
				String auth = request.getHeader("Authorization");
				log.info("auth = " + auth);
	
				if (auth == null) {
					response.setHeader("WWW-Authenticate", "NTLM");
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setContentLength(0);
					response.flushBuffer();
					return;
				}
				if (auth.startsWith("NTLM ")) {
					byte[] msg = Base64.decode(auth.substring(5));
					if (msg[8] == 1) {
						byte z = 0;
						byte[] msg1 = { (byte) 'N', (byte) 'T', (byte) 'L', (byte) 'M', (byte) 'S', (byte) 'S', (byte) 'P',
								z, (byte) 2, z, z, z, z, z, z, z, (byte) 40, z, z, z, (byte) 1, (byte) 130, z, z, z,
								(byte) 2, (byte) 2, (byte) 2, z, z, z, z, z, z, z, z, z, z, z, z };
						response.setHeader("WWW-Authenticate", "NTLM " + new String(Base64.encode(msg1)));
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						response.setContentLength(0);
						response.flushBuffer();
						return;
					} else if (msg[8] == 3) {
						// Did Authentication Succeed? All this is always printed.
						Type3Message type3 = new Type3Message(msg);
	
						System.out.println("osUser: " + type3.getUser());
						System.out.println("osRemoteHost: + " + type3.getWorkstation());
						System.out.println("osDomain: " + type3.getDomain());
						
						s.setAttribute("winuser", type3.getUser());
						s.setAttribute("winpc", type3.getWorkstation());
						s.setAttribute("windomain", type3.getDomain());
						
						request.setAttribute("winuser", type3.getUser());
						request.setAttribute("winpc", type3.getWorkstation());
						request.setAttribute("windomain", type3.getDomain());
					}
				}
			} catch (Exception e) {
				System.out.println(e);
			}
//		} else {
//			log.info("allready logged in");
		}
		
		try {
			chain.doFilter(req, res);
		} catch (IOException e) {
			System.out.println(e);
		} catch (ServletException e) {
			System.out.println(e);
		}
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
	}
}
