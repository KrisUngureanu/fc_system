package kz.tamur.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kz.tamur.util.Funcs;
import kz.tamur.util.XmlUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;

import com.cifs.or2.server.ServerUserSession;
import com.cifs.or2.server.Session;

public class ReportServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(ReportServlet.class);
    
    public ReportServlet() {
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String uuid = Funcs.getParameter(request, "uuid");
        if (uuid == null || uuid.equals("")) {
            out.print(createErrorResponseMessage("Не указан UUID дизайнера!"));
            return;
        }
        String rid = Funcs.getParameter(request, "rid");
        if (rid == null || rid.equals("")) {
            out.print(createErrorResponseMessage("Не указан ID отчета!"));
            return;
        }
        String cmd = Funcs.getParameter(request, "cmd");
        if (cmd == null || cmd.equals("")) {
            out.print(createErrorResponseMessage("Не указана команда!"));
            return;
        }

        try {
            String param = Funcs.getParameter(request, "param");
            ServerUserSession us = Session.findUserSession(UUID.fromString(uuid));
            if (us != null) {
            	ReportController rc = ReportController.instance(uuid, rid);
            	
            	if (("expr".equals(cmd) || "xml".equals(cmd)) && param != null)
            		param = param.replace("\r\n\r\n", "\r\n");
            	
            	String res = rc.sendToDesigner(us, cmd, param);
            	out.print(createResponseMessage(res));
            } else
                out.print(createErrorResponseMessage("Не найден дизайнер!"));

        } catch (Exception e) {
            out.print(createErrorResponseMessage("Ошибка выполнения сервиса!"));
            log.error(e, e);
        } catch (Throwable e) {
            out.print(createErrorResponseMessage("Ошибка выполнения сервиса!"));
            log.error(e, e);
        }
    }
    
	private String createErrorResponseMessage(String errorInfo) {
        String xmlString = null;
        try {
        	Element e = new Element("error");
            e.setText(errorInfo);
            byte[] b = XmlUtil.write(e);
            xmlString = new String(b, "UTF-8");
        } catch (Exception e) {
            xmlString = "<error>" + errorInfo + "</error>";
            log.error(e, e);
        }
        return xmlString;
    }
    
	private String createResponseMessage(String msg) {
        String xmlString = null;
        try {
        	Element e = new Element("response");
            e.setText(msg);
            byte[] b = XmlUtil.write(e);
            xmlString = new String(b, "UTF-8");
        } catch (Exception e) {
            xmlString = "<response>" + e.getMessage() + "</response>";
            log.error(e, e);
        }
        return xmlString;
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}