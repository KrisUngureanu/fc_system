package kz.tamur.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvOrLang;
import com.eclipsesource.json.JsonObject;

import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.util.Funcs;

public class UniversalJSONServlet extends HttpServlet {

    private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + UniversalJSONServlet.class);
    
    // Логировать JSON? -DlogJson
    // Возможные значения: in, out, both, none, no
    private static final boolean logJsonInput;
    private static final boolean logJsonOutput;
    
    // Ощищать JSON от комментариев? -DparseJsonComments
    private static final boolean parseJsonComments;
    
    static {
    	String tmp = Funcs.getSystemProperty("parseJsonComments");
    	parseJsonComments = ("1".equals(tmp) || "true".equals(tmp) || "yes".equals(tmp));
    	
    	String logJson = Funcs.getSystemProperty("logJson", "no");
    	logJsonInput = "in".equalsIgnoreCase(logJson) || "both".equalsIgnoreCase(logJson);
        logJsonOutput = "out".equalsIgnoreCase(logJson) || "both".equalsIgnoreCase(logJson);
    }

    private String dsName = null;
    private String user = null;
    private String pd = null;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        if (logJsonInput) {
        	log.info("Headers:");
	        for (Enumeration<String> names = req.getHeaderNames(); names.hasMoreElements(); ) {
	        	String name = names.nextElement();
	        	log.info(name + ": " + req.getHeader(name));
	        }
        }
        
        // устанавливаем параметры ответа
        resp.setContentType("application/json; charset=UTF-8");
        resp.setHeader("Cache-Control", "no-cache");
        PrintWriter w = resp.getWriter();

		Session s = null;
		try {
	        byte[] reqBytes = Funcs.readStream(req.getInputStream(), Constants.MAX_DOC_SIZE);
	        String jsonWithComments = new String(reqBytes);
	        
        	log.info("got json request successfully");
	        if (logJsonInput)
	        	log.info("json input: " + jsonWithComments);
	        
	        JsonObject res = null;
	        if (jsonWithComments.length() > 0) {
	        
	        	String json = null;
	        	if (parseJsonComments) {
	        		json = jsonWithComments.replaceAll("(/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/)", "");
	        		if (logJsonInput && json.length() != jsonWithComments.length())
	        			log.info("json input without comments: " + json);
	        	} else {
	        		json = jsonWithComments;
	        	}
	
		        JsonObject obj = JsonObject.readFrom(json);
		        		
		        s = getSession();
		        
		        if (obj.get("funcName") != null) {
			        KrnClass wsCls = s.getClassByName("JsonUtil");
					Context ctx = new Context(new long[0], 0, 0);
					ctx.langId = 0;
					ctx.trId = 0;
					s.setContext(ctx);
					List<Object> args = new ArrayList<Object>();
					args.add(obj);
					args.add(req);
					
					String url = req.getScheme() + "://" + req.getServerName()  
    				+ ((("http".equals(req.getScheme()) && req.getLocalPort() == 80)
    						|| ("https".equals(req.getScheme()) && req.getLocalPort() == 443))
    						? "" : (":" + req.getLocalPort()));
					
					args.add(url);

					SrvOrLang orlang = s.getSrvOrLang();
					res = (JsonObject) orlang.exec(wsCls, wsCls, obj.get("funcName").asString(), args, new Stack<String>());
		        } else {
			        KrnClass wsCls = s.getClassByName("WsUtilNew");
					Context ctx = new Context(new long[0], 0, 0);
					ctx.langId = 0;
					ctx.trId = 0;
					s.setContext(ctx);
					List<Object> args = new ArrayList<Object>();
					args.add(obj);
					SrvOrLang orlang = s.getSrvOrLang();
					res = (JsonObject) orlang.exec(wsCls, wsCls, "_rest_json_handler", args, new Stack<String>());
		        }
				s.commitTransaction();
	        } else {
	            res = new JsonObject();
	            res.add("result", "error");
	            res.add("message", "empty request");
	        }
	        
        	log.info("sending json response");
	        if (logJsonOutput)
	        	log.info("json output: " + res);

            w.println(res.toString());
            
        	log.info("json response sent");
		} catch (Throwable e) {
        	log.error(e, e);
            JsonObject obj = new JsonObject();
            if (e.getMessage() != null) {
                obj.add("result", "error");
                obj.add("message", e.getMessage());
            }
            w.println(obj.toString());
	        log.error("json output error: " + obj);
		} finally {
			if (s != null)
				s.release();
		}
	}

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        log.info("Loading servlet init params...");
		dsName = servletConfig.getInitParameter("dataSourceName");
		user = servletConfig.getInitParameter("user");
		pd = servletConfig.getInitParameter("password");
    }

	private Session getSession() throws Exception {
		return SrvUtils.getSession(dsName, user, pd);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
        doGet(req,  resp);
	}

}
