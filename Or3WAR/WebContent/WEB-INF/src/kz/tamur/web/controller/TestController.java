package kz.tamur.web.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestController extends HttpServlet {
	private static final Log log = LogFactory.getLog((System.getProperty("SERVER_ID") != null ? (System.getProperty("SERVER_ID") + ".") : "") + TestController.class);
    public final static int TIME_OUT_WEB_LONG_POLLING = 180000;

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request,  response);
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	log.info("REQ: " + request.getQueryString());
    	
        request.setCharacterEncoding("UTF-8");

        // Получаем http сессию
        HttpSession hs = request.getSession(true);
        // Достаем из нее уникальный объект
        String uid = (String) hs.getAttribute("uniqueUID");
        Session s = (Session) hs.getAttribute("uniqueSession");
        
        if (s == null) {
        	if (uid == null) {
        		uid = UUID.randomUUID().toString();
        		hs.setAttribute("uniqueUID", uid);
        	}
        	hs.setAttribute("uniqueSession", s = new Session(uid));
        }

        if (request.getParameter("polling") != null) {
        	String res = s.longPolling();

        	prepareResponse(response, 3);
    		PrintWriter w = response.getWriter();
    		w.println(res.toString());
    		
    		return;
        } else if (request.getParameter("wake") != null) {
        	s.wakeupLongPolling(true);
        	prepareResponse(response, 3);
    		PrintWriter w = response.getWriter();
    		w.println("{}");
    		return;
        } else if (request.getParameter("sse") != null) {
        	prepareResponse(response, 4);
    		OutputStream w = response.getOutputStream();
    		s.startSSE(w);
    		return;
        } else if (request.getParameter("get") != null) {
    		s.sendSSE();
        }
        
		prepareResponse(response, 2);
		return;
    }
    
    private void prepareResponse(HttpServletResponse response, int type) throws IOException {
    	if (type == 1)
    		response.setContentType("text/xml; charset=UTF-8");
    	else if (type == 2)
    		response.setContentType("text/html; charset=UTF-8");
    	else if (type == 3)
    		response.setContentType("application/json; charset=UTF-8");
    	else if (type == 4)
    		response.setContentType("text/event-stream");


    	response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setDateHeader("Expires", 0);
    }
    
    
    class Session {
    	String uid;
    	boolean wakeUp = false;
    	OutputStream w = null;
    	int i = 1;
    	boolean send = true;
    	
    	public Session(String uid) {
    		this.uid = uid;
    	}

        public String longPolling() {
            // остановить другие потоки пулинга
            wakeupLongPolling(false);
            
            // засыпаем
            try {
                synchronized (this) {
                	this.wait(TIME_OUT_WEB_LONG_POLLING);
    			}
            } catch (InterruptedException e) {
            	log.info("INTERRAPTED ");
            }

            // пишем ответ
            String res = (this.send) ? "{\"result\":\"test\",\"msg\":\"Message " + i++ +"\"}" 
            						 : "{\"result\":\"test\",\"msg\":\"Refreshing long polling connection\"}";
            log.info(res);
            
            return res;
        }

        // просыпаемся
        public synchronized void wakeupLongPolling(boolean send) {
        	this.send = send;
            try {
            	this.notify();
            } catch (Exception e) {}
    	}

        public void startSSE(OutputStream w) {
            try {
            	this.notify();
            } catch (Exception e) {}

            if (this.w != null) {
                try {
            		this.w.close();
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            }
        	
        	this.w = w;
    		try {
				w.write(("data: Message " + System.currentTimeMillis() + "\r\n\r\n").getBytes());
	    		w.flush();
				w.write(("data: Message " + System.currentTimeMillis() + "\r\n\r\n").getBytes());
	    		w.flush();
				w.write(("data: Message " + System.currentTimeMillis() + "\r\n\r\n").getBytes());
	    		w.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

    		try {
                synchronized (this) {
                	this.wait(TIME_OUT_WEB_LONG_POLLING);
    			}
            } catch (InterruptedException e) {
            }
        }

        public synchronized void sendSSE() {
            try {
            	if (w != null) {
	                w.write(("data: Message " + System.currentTimeMillis() + "\r\n\r\n").getBytes());
	                w.flush();
            	}
            } catch (Exception e) {
            	e.printStackTrace();
            }
    	}
    }
}
