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

public class UcgoJNLPServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
    
    public UcgoJNLPServlet() {
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
    	response.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-java-jnlp-file");
    	
    	String reqURL = request.getRequestURL().toString();
    	int ind = reqURL.indexOf("/jsp/media/ucgo");
    	reqURL = reqURL.substring(0, ind);
        
        PrintWriter out = response.getWriter();
    	out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		out.println("<jnlp spec=\"1.0+\" codebase=\"" + reqURL + "/jsp/media/ucgo\" href=\"ucgo.jnlp\">");
		
		out.println("<information>");
		out.println("<title>УЦГО - вебсокет</title>");
		out.println("<vendor>ТОО \"ТАМУР\"</vendor>");
		out.println("<description>УП OR3</description>");
		out.println("<description kind=\"short\">Приложение для взаимодействия с УЦГО</description>");
		out.println("<icon href=\"images/icon.jpg\"/>");
		out.println("<shortcut online=\"true\">");
		out.println("<menu submenu=\"TAMUR\"/>");
		out.println("<desktop/>");
		out.println("</shortcut>");
		out.println("</information>");
		
		out.println("<security>");
		out.println("<all-permissions/>");
		out.println("</security>");
		
		out.println("<resources>");
		out.println("<j2se version=\"1.7+\" java-vm-args=\"-Xmx512m\"/>");
		out.println("<jar href=\"ucgo-websocket-signed.jar-1.0.jar\" download=\"eager\" main=\"true\"/>");
		out.println("<jar href=\"crypto-hardware-1.0.jar\" download=\"eager\"/>");
		out.println("<jar href=\"crypto_applet_ucgo_hardware-1.0.jar\" download=\"eager\"/>");
		out.println("<jar href=\"javax.websocket-api-1.1.jar\"/>");
		out.println("<jar href=\"gson-2.8.2.jar\"/>");
		out.println("<jar href=\"tyrus-core-1.13.1.jar\"/>");
		out.println("<jar href=\"tyrus-client-1.13.1.jar\"/>");
		out.println("<jar href=\"tyrus-server-1.13.1.jar\"/>");
		out.println("<jar href=\"tyrus-spi-1.13.1.jar\"/>");
		out.println("<jar href=\"tyrus-container-grizzly-client-1.13.1.jar\"/>");
		out.println("<jar href=\"tyrus-container-grizzly-server-1.13.1.jar\"/>");
		out.println("<jar href=\"grizzly-http-2.3.25.jar\"/>");
		out.println("<jar href=\"grizzly-http-server-2.3.25.jar\"/>");
		out.println("<jar href=\"grizzly-framework-2.3.25.jar\"/>");
		out.println("<jar href=\"kalkancrypt-0.1.1.jar\"/>");
		out.println("<jar href=\"kalkancrypt_xmldsig-0.1.jar\"/>");
		out.println("<jar href=\"commons-logging-1.1.1.jar\"/>");
		out.println("<jar href=\"xmlsec-1.4.4.jar\"/>");
		out.println("<jar href=\"jdom.jar\"/>");
		out.println();                
		out.println("<property name=\"splash\" value=\"SplashTSON.jpg\"/>");
		out.println("<property name=\"selSrv\" value=\"0\"/>");
		out.println("<property name=\"reportType\" value=\"jacob\"/>");
		out.println("<property name=\"ref.fetch.depth\" value=\"1\"/>");
		out.println("<property name=\"serverType\" value=\"Wildfly\"/>");
		out.println("<property name=\"dsName\" value=\"kdr_sed_tt_real\"/>");
		out.println("<property name=\"host\" value=\"192.168.13.58\"/>");
		out.println("<property name=\"port\" value=\"8181\"/>");
		out.println("</resources>");
		
		out.println("<application-desc main-class=\"kz.tamur.crypto.websocket.WebsocketStartup\"/>");
		out.println("</jnlp>");
    }
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}