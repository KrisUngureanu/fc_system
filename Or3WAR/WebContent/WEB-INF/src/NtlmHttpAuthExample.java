import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;

public class NtlmHttpAuthExample extends HttpServlet {

    private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + NtlmHttpAuthExample.class.getName());

    @Override
	protected void service(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
    	try {
    		super.service(arg0, arg1);
    	} catch (Exception e) {
    		Funcs.logException(log, e);
    	}
	}
    
	@Override
	public void service(ServletRequest arg0, ServletResponse arg1)
			throws ServletException, IOException {
    	try {
    		super.service(arg0, arg1);
    	} catch (Exception e) {
    		Funcs.logException(log, e);
    	}
	}

	public void doGet( HttpServletRequest req,
                HttpServletResponse resp ) throws IOException, ServletException {
		
		HttpSession hs = req.getSession(true);
        log.info("Session id = " + (hs != null ? hs.getId() : 0));
        
        if (hs != null) {
            String userName = Funcs.validate(Funcs.sanitizeUsername(req.getRemoteUser()));
        	if (userName.length() > 0 && userName.length() < Constants.MAX_USER_NAME) {
	        	int beg = userName.lastIndexOf("\\");
	        	if (beg > -1)
	        		userName = userName.substring(beg + 1);
	        	
	        	if (Character.isAlphabetic(userName.charAt(0)) && userName.matches(".+")) {
	        		hs.setAttribute("remoteUserName", userName);
	                log.info(userName + " - successfully logged in");
	        	}
	        } else {
	        	userName = (String) hs.getAttribute("remoteUserName");
	        }
        }
		
		//req.getRequestDispatcher("/main?trg=top").forward(req, resp);		
        PrintWriter out = resp.getWriter();

        resp.setContentType( "text/html" );
        out.println( "<HTML><HEAD><script type=\"text/javascript\">" );
        out.println( "location.replace(\"main?ntlm=1&trg=top\");");
        out.println( "</script></HEAD><BODY>" );

        out.println( "</BODY></HTML>" );
        
        out.flush();
    }
	
    public void doPost( HttpServletRequest req,
                HttpServletResponse resp ) throws IOException, ServletException {
        doGet( req, resp );
    }
}

