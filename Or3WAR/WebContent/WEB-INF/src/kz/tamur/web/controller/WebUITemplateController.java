package kz.tamur.web.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cifs.or2.client.Kernel;

import kz.tamur.or3ee.common.UserSession;
import kz.tamur.util.Funcs;
import kz.tamur.web.common.TemplateHelper;
import kz.tamur.web.common.WebUser;

/**
 * Servlet implementation class WebUITemplateController
 */
public class WebUITemplateController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ImagesController.class);
	
	public static String ORUITEMPLATES_HOME;
	private static boolean cacheIfcInFile = false;

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		log.info("Loading servlet init params...");
		
		ORUITEMPLATES_HOME = System.getProperty("apphome") != null? System.getProperty("apphome").replaceAll("\\\\", "/") + "/orui" 
				:config.getInitParameter("apphome").replaceAll("\\\\", "/") + "/orui";
        try {
            File parent = Funcs.getCanonicalFile(ORUITEMPLATES_HOME);
            ORUITEMPLATES_HOME = parent.getCanonicalPath().replaceAll("\\\\", "/");
	        log.info("ORUITEMPLATES_HOME = " + ORUITEMPLATES_HOME);
	        parent.mkdirs();
		} catch (IOException e) {
			log.error(e, e);
        	throw new ServletException("ORUITEMPLATES_HOME path couldn't be canonicalized!");
		}

		
		String temp = config.getInitParameter("cache");
		cacheIfcInFile = "1".equals(temp) || "true".equals(temp); 
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String objUID = Funcs.sanitizeFileName(request.getParameter("id"));
		String langUID = Funcs.sanitizeFileName(request.getParameter("lang"));
		
		response.setContentType("text/html; charset=UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "must-revalidate");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Cache-Control", "no-store");
		OutputStream out = response.getOutputStream();

		if (Funcs.checkUID(objUID)) {
            Map<String, Object> hs = WebController.getSession(request, Funcs.getValidatedParameter(request, "guid"));
            WebUser user = (WebUser) hs.get("user");
	
			if (user != null) {
				Kernel krn = user.getSession().getKernel();
				long langId = langUID != null ? Long.parseLong(langUID) : krn.getUser().getIfcLang().id;
	
				out.write(TemplateHelper.load(user.getGUID(), objUID, langId, krn, ORUITEMPLATES_HOME, cacheIfcInFile).getBytes("UTF-8"));
			} else {
				out.write("error".getBytes());
			}
		} else {
			out.write("error".getBytes());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
