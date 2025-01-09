package kz.tamur.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.rt.Utils;
import kz.tamur.util.Funcs;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImagesController extends HttpServlet {
    private static final Log log = LogFactory.getLog((UserSession.SERVER_ID != null ? (UserSession.SERVER_ID + ".") : "") + ImagesController.class);
    public static File IMAGES_HOME;

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        log.info("Loading servlet init params...");
        String imgHome = System.getProperty("apphome") != null? System.getProperty("apphome").replaceAll("\\\\", "/") + "/images/foto":
        	servletConfig.getInitParameter("apphome").replaceAll("\\\\", "/") + "/images/foto";
        IMAGES_HOME = Funcs.getCanonicalFile(imgHome);
        IMAGES_HOME.mkdirs();
        log.info("IMAGES_HOME = " + IMAGES_HOME.getAbsolutePath());
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request,  response);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletOutputStream stream = null;
        FileInputStream input = null;
        try {
        	String name = Funcs.normalizeInput(request.getPathInfo());
        	if (name != null && name.matches(".+")) {
        		String imgName = name.substring(name.lastIndexOf('/') + 1).replace("..", "").replace("/", "").replace("\\", "");
        		String folderName = null;
        		
        		File folder = Funcs.getCanonicalFile(IMAGES_HOME);
		        int fStart = imgName.lastIndexOf(':');
		        if (fStart > -1) {
		        	folderName = imgName.substring(fStart +1);
		        	imgName = imgName.substring(0, fStart);
			        folder = Funcs.getCanonicalFile(folder, folderName);
			        folder.mkdir();
			        
			        log.info("Folder name: " + folderName + ", img: " + imgName);
		        }

		        File doc = Funcs.getCanonicalFile(folder, imgName);
	    		imgName = doc.getName();
	    		
	        	if (imgName.length() < Constants.MAX_FILE_NAME) {
	        		stream = response.getOutputStream();
			        
	        		if (doc.exists() && doc.canRead()) {
				        response.setContentLength((int) doc.length());
				        
				        String ext = null;
				        int eStart = imgName.lastIndexOf('.');
				        if (eStart > -1) {
				        	ext = imgName.substring(eStart +1);
					        log.info("Extension: " + ext + ", type: " + Funcs.getContentTypeByExtension(ext));

				        }
			        	
				        if (!imgName.endsWith(".html")) {
				        	imgName = URLEncoder.encode(imgName, "UTF-8").replaceAll("\\+", "%20");

				        	response.addHeader("Content-Disposition", "attachment; filename=" + imgName);
							long expiry = new Date(). getTime() + Constants.ONE_DAY*3;
							response.setDateHeader("Expires", expiry);
							response.setHeader("Cache-Control", "max-age="+ Constants.ONE_DAY*3);
							
							String ct = Funcs.getContentTypeByExtension(ext);
							if (ct != null)
								response.setHeader("Content-Type", ct);
//							else
//								response.setHeader("Content-Type", "image/png");
			        	}
				        input = new FileInputStream(doc);
				        Funcs.writeStream(input, stream, Constants.MAX_DOC_SIZE);
	        		}
	        	}
        	}
        } catch (IOException ioe) {
        	throw new ServletException(ioe.getMessage());
        } finally {
        	Utils.closeQuietly(stream);
        	Utils.closeQuietly(input);
        }
    }
}
