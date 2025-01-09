package kz.tamur.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.eclipsesource.json.JsonObject;

import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;

@SuppressWarnings("serial")
public class XmppController extends HttpServlet {
	
	private static String xmppUrl;
    private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + XmppController.class);

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
    	
        String path = Funcs.normalizeInput(req.getPathInfo());
    	if ("/auth".equals(path)) {
    		HttpSession hs = req.getSession();
    		String jid = (String)hs.getAttribute("xmpp.jid");
    		String sid = (String)hs.getAttribute("xmpp.sid");
    		String rid = (String)hs.getAttribute("xmpp.rid");
    		if (jid != null && sid != null && rid != null) {
    			JsonObject res = new JsonObject();
    			res.add("jid", jid);
    			res.add("sid", sid);
    			res.add("rid", rid);
    			resp.getWriter().write(res.toString());
    			resp.setContentType("application/json; charset=UTF-8");
    		}
    	
    	} else if ("/ipgoAuth".equals(path)) {
    		// Дергаем сервис авторизации ИПГО и получаем логин и пароль к чату
    		// Сохраняем в БД и добавляем в артибуты сессии
    		HttpSession hs = req.getSession();
    		String jid = (String)hs.getAttribute("xmpp.jid");
    		String sid = (String)hs.getAttribute("xmpp.sid");
    		String rid = (String)hs.getAttribute("xmpp.rid");
    		if (jid != null && sid != null && rid != null) {
    			JsonObject res = new JsonObject();
    			res.add("jid", jid);
    			res.add("sid", sid);
    			res.add("rid", rid);
    			resp.getWriter().write(res.toString());
    			resp.setContentType("application/json; charset=UTF-8");
    		} else {
    			JsonObject res = new JsonObject();
    			//res.add("err", Ответ сервиса);
    		}
    	} else {
    		URL url = new URL(xmppUrl);
    		HttpURLConnection connection = null;

    		OutputStream os = null;
    		InputStream is = null;
    		try {
        		connection = (HttpURLConnection) url.openConnection();
        		connection.setDoOutput(true);
        		connection.setDoInput(true);
        		connection.setInstanceFollowRedirects(false);
        		connection.setRequestMethod(req.getMethod());
        		String contentType = Funcs.normalizeInput(req.getContentType()).replace("'", "").replace("\"", "").replace("`", "").replace("]", "")
    					.replace("*", "").replace("?", "").replace("<", "").replace(">", "").replace("|", "")
    					.replace("[", "").replace("(", "").replace(")", "").replace("{", "").replace("}", "")
    					.replace("\r", "").replace("\n", "").replace("\\", "").replace(":", "");

        		String encoding = Funcs.normalizeInput(req.getContentType()).replace("'", "").replace("\"", "").replace("`", "").replace("]", "")
    					.replace("*", "").replace("?", "").replace("<", "").replace(">", "").replace("|", "")
    					.replace("[", "").replace("(", "").replace(")", "").replace("{", "").replace("}", "")
    					.replace("\r", "").replace("\n", "").replace("\\", "").replace(":", "");

        		connection.setRequestProperty("Content-Type", contentType);
        		connection.setRequestProperty("charset", encoding);
        		connection.setRequestProperty("Content-Length", "" + req.getContentLength());
        		connection.setUseCaches(false);

        		os = connection.getOutputStream();
	    		is = req.getInputStream();
	    		Funcs.writeStream(is, os, Constants.MAX_MESSAGE_SIZE);
	    		os.flush();

    			log.info(connection.getResponseMessage());
    			Funcs.writeStream(connection.getInputStream(), resp.getOutputStream(), Constants.MAX_MESSAGE_SIZE);
    		} catch (Exception e) {
    			log.error("Ошибка при соединении с " + xmppUrl);
    			resp.setStatus(404);
    			resp.getOutputStream().write("<error/>".getBytes());
    		} finally {
    			Utils.closeQuietly(is);
    			Utils.closeQuietly(os);
    			Utils.closeQuietly(connection);
    		}
    	}
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		xmppUrl = config.getInitParameter("url");
	}
}
